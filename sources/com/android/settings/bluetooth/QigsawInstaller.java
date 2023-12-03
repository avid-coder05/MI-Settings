package com.android.settings.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.android.settings.SettingsApplication;
import com.android.settings.recommend.PageIndexManager;
import com.google.android.play.core.splitinstall.SplitInstallException;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.provider.ExtraNetwork;

/* loaded from: classes.dex */
public class QigsawInstaller extends Activity {
    private static int HEADSETPLUGIN_INITED = 1;
    private SplitInstallManager mInstallManager;
    private ArrayList<String> mModuleNames;
    private int mSessionId;
    private int mStatus;
    private boolean startInstallFlag;
    private boolean mFirstStartup = true;
    private SplitInstallStateUpdatedListener myListener = new SplitInstallStateUpdatedListener() { // from class: com.android.settings.bluetooth.QigsawInstaller.1
        @Override // com.google.android.play.core.listener.StateUpdatedListener
        public void onStateUpdate(SplitInstallSessionState splitInstallSessionState) {
            if (QigsawInstaller.this.mModuleNames != null && splitInstallSessionState.moduleNames().containsAll(QigsawInstaller.this.mModuleNames) && QigsawInstaller.this.mModuleNames.containsAll(splitInstallSessionState.moduleNames())) {
                Log.d("QigsawInstaller", "install ok " + splitInstallSessionState.status());
                QigsawInstaller.this.mStatus = splitInstallSessionState.status();
                switch (splitInstallSessionState.status()) {
                    case 1:
                        QigsawInstaller.this.onPending(splitInstallSessionState);
                        return;
                    case 2:
                        QigsawInstaller.this.onDownloading(splitInstallSessionState);
                        return;
                    case 3:
                        QigsawInstaller.this.onDownloaded();
                        return;
                    case 4:
                        QigsawInstaller.this.onInstalling();
                        return;
                    case 5:
                        Iterator it = QigsawInstaller.this.mModuleNames.iterator();
                        while (it.hasNext()) {
                            BluetoothPluginOneTrackHelper.trackInstalled(QigsawInstaller.this, (String) it.next());
                        }
                        QigsawInstaller.this.onInstalled();
                        return;
                    case 6:
                        int errorCode = splitInstallSessionState.errorCode();
                        Iterator it2 = QigsawInstaller.this.mModuleNames.iterator();
                        while (it2.hasNext()) {
                            BluetoothPluginOneTrackHelper.trackFailed(QigsawInstaller.this, (String) it2.next(), errorCode);
                        }
                        QigsawInstaller.this.onFailed();
                        return;
                    case 7:
                    default:
                        return;
                    case 8:
                        QigsawInstaller.this.onRequiresUserConfirmation(splitInstallSessionState);
                        return;
                }
            }
        }
    };

    /* renamed from: com.android.settings.bluetooth.QigsawInstaller$4  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass4 implements OnCompleteListener<List<SplitInstallSessionState>> {
        final /* synthetic */ QigsawInstaller this$0;

        @Override // com.google.android.play.core.tasks.OnCompleteListener
        public void onComplete(Task<List<SplitInstallSessionState>> task) {
            if (task.isSuccessful()) {
                for (SplitInstallSessionState splitInstallSessionState : task.getResult()) {
                    if (splitInstallSessionState.status() == 2) {
                        this.this$0.mInstallManager.cancelInstall(splitInstallSessionState.sessionId()).addOnCompleteListener(new OnCompleteListener<Void>() { // from class: com.android.settings.bluetooth.QigsawInstaller.4.1
                            @Override // com.google.android.play.core.tasks.OnCompleteListener
                            public void onComplete(Task<Void> task2) {
                                AnonymousClass4.this.this$0.startInstall();
                            }
                        });
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class BluetoothPluginOneTrackHelper {
        private static Intent buildIntent() {
            try {
                SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
                if (splitInfoManagerService == null) {
                    Log.e("BluetoothPluginOneTrackHelper", "buildIntent SplitInfoManager is null!");
                    return null;
                }
                Intent intent = new Intent("onetrack.action.TRACK_EVENT");
                intent.setPackage("com.miui.analytics");
                intent.putExtra("APP_ID", "31000000416");
                intent.putExtra("PACKAGE", "com.xiaomi.bluetooth");
                intent.setFlags(1);
                intent.putExtra(ExtraNetwork.FIREWALL_PACKAGE_NAME, "com.android.settings");
                intent.putExtra("version", splitInfoManagerService.getCurrentSplitInfoVersion());
                return intent;
            } catch (Exception unused) {
                return null;
            }
        }

        public static void trackFailed(Context context, String str, int i) {
            Log.i("BluetoothPluginOneTrackHelper", "trackFailed errorCode=" + i + " moduleName = " + str);
            try {
                Intent buildIntent = buildIntent();
                if (buildIntent == null) {
                    Log.e("BluetoothPluginOneTrackHelper", "trackFailed intent is null!");
                    return;
                }
                buildIntent.putExtra("EVENT_NAME", "bt_plugin_install_fail");
                buildIntent.putExtra("plugin_name", str);
                buildIntent.putExtra("error_code", i);
                context.startServiceAsUser(buildIntent, UserHandle.CURRENT);
            } catch (Exception e) {
                Log.e("BluetoothPluginOneTrackHelper", "trackFailed:" + e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void trackInstalled(Context context, String str) {
            try {
                Intent buildIntent = buildIntent();
                if (buildIntent == null) {
                    Log.e("BluetoothPluginOneTrackHelper", "trackInstalled intent is null!");
                    return;
                }
                buildIntent.putExtra("EVENT_NAME", "bt_plugin_install_success");
                buildIntent.putExtra("plugin_name", str);
                context.startServiceAsUser(buildIntent, UserHandle.CURRENT);
            } catch (Exception e) {
                Log.e("BluetoothPluginOneTrackHelper", "trackInstalled:" + e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDownloaded() {
        Log.d("QigsawInstaller", "on downloaded");
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"StringFormatInvalid"})
    public void onDownloading(SplitInstallSessionState splitInstallSessionState) {
        Log.d("QigsawInstaller", "on downloaded message");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFailed() {
        setResult(0);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInstalled() {
        onInstallOK();
        Intent intent = new Intent();
        intent.putExtra("moduleNames", this.mModuleNames);
        setResult(-1, intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInstalling() {
        Log.d("QigsawInstaller", "on installing");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPending(SplitInstallSessionState splitInstallSessionState) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRequiresUserConfirmation(SplitInstallSessionState splitInstallSessionState) {
        try {
            startIntentSenderForResult(splitInstallSessionState.resolutionIntent().getIntentSender(), 11, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startInstall() {
        if (this.mInstallManager.getInstalledModules().containsAll(this.mModuleNames)) {
            onInstalled();
            return;
        }
        SplitInstallRequest.Builder newBuilder = SplitInstallRequest.newBuilder();
        Iterator<String> it = this.mModuleNames.iterator();
        while (it.hasNext()) {
            newBuilder.addModule(it.next());
        }
        this.mInstallManager.startInstall(newBuilder.build()).addOnSuccessListener(new OnSuccessListener<Integer>() { // from class: com.android.settings.bluetooth.QigsawInstaller.3
            @Override // com.google.android.play.core.tasks.OnSuccessListener
            public void onSuccess(Integer num) {
                QigsawInstaller.this.mSessionId = num.intValue();
                QigsawInstaller.this.startInstallFlag = true;
            }
        }).addOnFailureListener(new OnFailureListener() { // from class: com.android.settings.bluetooth.QigsawInstaller.2
            @Override // com.google.android.play.core.tasks.OnFailureListener
            public void onFailure(Exception exc) {
                QigsawInstaller.this.startInstallFlag = true;
                if (exc instanceof SplitInstallException) {
                    SplitInstallException splitInstallException = (SplitInstallException) exc;
                    int errorCode = splitInstallException.getErrorCode();
                    String[] moduleNames = splitInstallException.getModuleNames();
                    if (moduleNames != null) {
                        for (String str : moduleNames) {
                            Log.d("QigsawInstaller", "onFailure track : moduleName" + str);
                            BluetoothPluginOneTrackHelper.trackFailed(QigsawInstaller.this, str, errorCode);
                        }
                    }
                    QigsawInstaller.this.onFailed();
                }
            }
        });
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        int i = this.mStatus;
        if (i == 0) {
            Log.d("QigsawInstaller", "Split download is not started!");
            super.onBackPressed();
        } else if (i == 9 || i == 3 || i == 4 || !this.startInstallFlag) {
        } else {
            int i2 = this.mSessionId;
            if (i2 != 0) {
                this.mInstallManager.cancelInstall(i2).addOnSuccessListener(new OnSuccessListener<Void>() { // from class: com.android.settings.bluetooth.QigsawInstaller.6
                    @Override // com.google.android.play.core.tasks.OnSuccessListener
                    public void onSuccess(Void r2) {
                        Log.d("QigsawInstaller", "Cancel task successfully, session id :" + QigsawInstaller.this.mSessionId);
                        if (QigsawInstaller.this.isFinishing()) {
                            return;
                        }
                        QigsawInstaller.this.finish();
                    }
                }).addOnFailureListener(new OnFailureListener() { // from class: com.android.settings.bluetooth.QigsawInstaller.5
                    @Override // com.google.android.play.core.tasks.OnFailureListener
                    public void onFailure(Exception exc) {
                        Log.d("QigsawInstaller", "Cancel task failed, session id :" + QigsawInstaller.this.mSessionId);
                        if (QigsawInstaller.this.isFinishing()) {
                            return;
                        }
                        QigsawInstaller.this.finish();
                    }
                });
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        window.setGravity(51);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = 0;
        attributes.y = 0;
        attributes.width = 1;
        attributes.height = 1;
        attributes.type = PageIndexManager.PAGE_KEY_FUNCTION_SETTINGS;
        attributes.flags = 32;
        window.setAttributes(attributes);
        if (((SettingsApplication) getApplication()).mQigsawStarted != HEADSETPLUGIN_INITED) {
            Log.e("QigsawInstaller", "the qigsaw does not start up");
            finish();
            return;
        }
        this.mInstallManager = SplitInstallManagerFactory.create(this);
        ArrayList<String> stringArrayListExtra = getIntent().getStringArrayListExtra("moduleNames");
        if (stringArrayListExtra == null || stringArrayListExtra.isEmpty()) {
            finish();
        } else {
            this.mModuleNames = stringArrayListExtra;
        }
        this.mInstallManager.registerListener(this.myListener);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        try {
            SplitInstallManager splitInstallManager = this.mInstallManager;
            if (splitInstallManager != null) {
                splitInstallManager.unregisterListener(this.myListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onInstallOK() {
        Log.d("QigsawInstaller", "on install ok");
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.mFirstStartup) {
            startInstall();
        }
        this.mFirstStartup = false;
    }
}
