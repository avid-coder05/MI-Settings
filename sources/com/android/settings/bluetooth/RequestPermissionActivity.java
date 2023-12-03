package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.R;
import com.android.settingslib.bluetooth.BluetoothDiscoverableTimeoutReceiver;
import miui.vip.VipService;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class RequestPermissionActivity extends AppCompatActivity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private CharSequence mAppLabel;
    private BluetoothAdapter mBluetoothAdapter;
    private AlertDialog mDialog;
    private String mPackageName;
    private BroadcastReceiver mReceiver;
    private int mRequest;
    private int mTimeout = 120;
    private Runnable mDelayRunnable = new Runnable() { // from class: com.android.settings.bluetooth.RequestPermissionActivity.1
        @Override // java.lang.Runnable
        public void run() {
            if (RequestPermissionActivity.this.isFinishing() || RequestPermissionActivity.this.isDestroyed()) {
                return;
            }
            RequestPermissionActivity.this.cancelAndFinish();
        }
    };

    /* loaded from: classes.dex */
    private final class StateChangeReceiver extends BroadcastReceiver {
        public StateChangeReceiver() {
            RequestPermissionActivity.this.getWindow().getDecorView().postDelayed(RequestPermissionActivity.this.mDelayRunnable, 10000L);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
            int i = RequestPermissionActivity.this.mRequest;
            if (i == 1 || i == 2) {
                if (intExtra == 12) {
                    RequestPermissionActivity.this.proceedAndFinish();
                }
            } else if (i == 3 && intExtra == 10) {
                RequestPermissionActivity.this.proceedAndFinish();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelAndFinish() {
        setResult(0);
        finish();
    }

    private void createDialog() {
        if (getResources().getBoolean(R.bool.auto_confirm_bluetooth_activation_dialog)) {
            onClick(null, -1);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (this.mReceiver != null) {
            int i = this.mRequest;
            if (i == 1 || i == 2) {
                builder.setView(getLayoutInflater().inflate(R.layout.bt_enabling_progress, (ViewGroup) null));
            }
            builder.setCancelable(false);
        } else {
            int i2 = this.mTimeout;
            if (i2 == 0) {
                CharSequence charSequence = this.mAppLabel;
                builder.setMessage(charSequence != null ? getString(R.string.bluetooth_ask_lasting_discovery, new Object[]{charSequence}) : getString(R.string.bluetooth_ask_lasting_discovery_no_name));
            } else {
                CharSequence charSequence2 = this.mAppLabel;
                builder.setMessage(charSequence2 != null ? getString(R.string.bluetooth_ask_discovery, new Object[]{charSequence2, Integer.valueOf(i2)}) : getString(R.string.bluetooth_ask_discovery_no_name, new Object[]{Integer.valueOf(i2)}));
            }
            builder.setPositiveButton(getString(R.string.allow), this);
            builder.setNegativeButton(getString(R.string.deny), this);
        }
        builder.setOnDismissListener(this);
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    private boolean parseIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return true;
        }
        if (intent.getAction().equals("android.bluetooth.adapter.action.REQUEST_ENABLE")) {
            this.mRequest = 1;
        } else if (intent.getAction().equals("android.bluetooth.adapter.action.REQUEST_DISABLE")) {
            this.mRequest = 3;
        } else if (!intent.getAction().equals("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE")) {
            Log.e("BtRequestPermission", "Error: this activity may be started only with intent android.bluetooth.adapter.action.REQUEST_ENABLE, android.bluetooth.adapter.action.REQUEST_DISABLE or android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
            setResult(0);
            return true;
        } else {
            this.mRequest = 2;
            this.mTimeout = intent.getIntExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 120);
            Log.d("BtRequestPermission", "Setting Bluetooth Discoverable Timeout = " + this.mTimeout);
            int i = this.mTimeout;
            if (i < 1 || i > 3600) {
                this.mTimeout = 120;
            }
        }
        String launchedFromPackage = getLaunchedFromPackage();
        int launchedFromUid = getLaunchedFromUid();
        try {
            this.mPackageName = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
            Log.d("BtRequestPermission", "app package name is " + this.mPackageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (UserHandle.isSameApp(launchedFromUid, VipService.VIP_SERVICE_FAILURE) && getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME") != null) {
            launchedFromPackage = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
        }
        if (!UserHandle.isSameApp(launchedFromUid, VipService.VIP_SERVICE_FAILURE) && getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME") != null) {
            Log.w("BtRequestPermission", "Non-system Uid: " + launchedFromUid + " tried to override packageName \n");
        }
        if (!TextUtils.isEmpty(launchedFromPackage)) {
            try {
                this.mAppLabel = getPackageManager().getApplicationInfo(launchedFromPackage, 0).loadSafeLabel(getPackageManager(), 1000.0f, 5);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("BtRequestPermission", "Couldn't find app with package name " + launchedFromPackage);
                setResult(0);
                return true;
            }
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Log.e("BtRequestPermission", "Error: there's a problem starting Bluetooth");
            setResult(0);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void proceedAndFinish() {
        int i = this.mRequest;
        int i2 = 1;
        if (i == 1 || i == 3) {
            i2 = -1;
        } else if (this.mBluetoothAdapter.setScanMode(23, this.mTimeout)) {
            long currentTimeMillis = System.currentTimeMillis() + (this.mTimeout * 1000);
            LocalBluetoothPreferences.persistDiscoverableEndTimestamp(this, currentTimeMillis);
            if (this.mTimeout > 0) {
                BluetoothDiscoverableTimeoutReceiver.setDiscoverableAlarm(this, currentTimeMillis);
            }
            int i3 = this.mTimeout;
            if (i3 >= 1) {
                i2 = i3;
            }
        } else {
            i2 = 0;
        }
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        setResult(i2);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 != -1) {
            cancelAndFinish();
            return;
        }
        int i3 = this.mRequest;
        if (i3 == 1 || i3 == 2) {
            if (this.mBluetoothAdapter.getState() == 12) {
                proceedAndFinish();
                return;
            }
            StateChangeReceiver stateChangeReceiver = new StateChangeReceiver();
            this.mReceiver = stateChangeReceiver;
            registerReceiver(stateChangeReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
            createDialog();
            try {
                Log.d("BtRequestPermission", "enable Bluetooth start record");
                if (this.mRequest != 1 || TextUtils.isEmpty(this.mPackageName)) {
                    return;
                }
                MiuiEnableBluetoothRecord.getInstance().createNotification(getApplicationContext(), this.mPackageName, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (i3 != 3) {
            cancelAndFinish();
        } else if (this.mBluetoothAdapter.getState() == 10) {
            proceedAndFinish();
        } else {
            StateChangeReceiver stateChangeReceiver2 = new StateChangeReceiver();
            this.mReceiver = stateChangeReceiver2;
            registerReceiver(stateChangeReceiver2, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
            createDialog();
            try {
                Log.d("BtRequestPermission", "disable Bluetooth start record");
                if (TextUtils.isEmpty(this.mPackageName)) {
                    return;
                }
                MiuiEnableBluetoothRecord.getInstance().createNotification(getApplicationContext(), this.mPackageName, false);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        setResult(0);
        super.onBackPressed();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            cancelAndFinish();
        } else if (i != -1) {
        } else {
            proceedAndFinish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        ApplicationInfo applicationInfo;
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        setResult(0);
        if (parseIntent()) {
            finish();
            return;
        }
        int state = this.mBluetoothAdapter.getState();
        int i = this.mRequest;
        if (i == 3) {
            switch (state) {
                case 10:
                case 13:
                    proceedAndFinish();
                    return;
                case 11:
                case 12:
                    Intent intent = new Intent(this, RequestPermissionHelperActivity.class);
                    intent.putExtra("com.android.settings.bluetooth.extra.APP_LABEL", this.mAppLabel);
                    intent.setAction("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_OFF");
                    startActivityForResult(intent, 0);
                    return;
                default:
                    Log.e("BtRequestPermission", "Unknown adapter state: " + state);
                    cancelAndFinish();
                    return;
            }
        }
        switch (state) {
            case 10:
            case 11:
            case 13:
                String str = null;
                if (getCallingActivity() != null) {
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    if (packageManager == null) {
                        finish();
                        return;
                    }
                    try {
                        applicationInfo = packageManager.getApplicationInfo(getCallingActivity().getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException unused) {
                        applicationInfo = null;
                    }
                    if (applicationInfo != null) {
                        str = (String) packageManager.getApplicationLabel(applicationInfo);
                    }
                }
                Intent intent2 = new Intent(this, RequestPermissionHelperActivity.class);
                intent2.putExtra("ApplicationName", str);
                intent2.setAction("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON");
                intent2.putExtra("com.android.settings.bluetooth.extra.APP_LABEL", this.mAppLabel);
                if (this.mRequest == 2) {
                    intent2.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", this.mTimeout);
                }
                startActivityForResult(intent2, 0);
                return;
            case 12:
                if (i == 1) {
                    proceedAndFinish();
                    return;
                } else {
                    createDialog();
                    return;
                }
            default:
                Log.e("BtRequestPermission", "Unknown adapter state: " + state);
                cancelAndFinish();
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        View decorView = getWindow().getDecorView();
        if (decorView != null) {
            decorView.removeCallbacks(this.mDelayRunnable);
        }
        BroadcastReceiver broadcastReceiver = this.mReceiver;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            this.mReceiver = null;
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        cancelAndFinish();
    }
}
