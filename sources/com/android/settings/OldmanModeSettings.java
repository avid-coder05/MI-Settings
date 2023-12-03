package com.android.settings;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.settingslib.util.ToastUtil;
import java.util.Iterator;
import java.util.List;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class OldmanModeSettings extends AppCompatActivity implements View.OnClickListener {
    private Button mActionBtnView;
    private String mDefaultHomePkg;
    private TextView mDesView;
    private String mInstalledJeejenPkgName;
    private JeejenStatus mJeejenStatus;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.OldmanModeSettings$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$OldmanModeSettings$JeejenStatus;

        static {
            int[] iArr = new int[JeejenStatus.values().length];
            $SwitchMap$com$android$settings$OldmanModeSettings$JeejenStatus = iArr;
            try {
                iArr[JeejenStatus.INSTALLED_CURRENT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$OldmanModeSettings$JeejenStatus[JeejenStatus.INSTALLED_NOT_CURRENT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$OldmanModeSettings$JeejenStatus[JeejenStatus.UNINSTALLED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum JeejenStatus {
        UNINSTALLED,
        INSTALLED_NOT_CURRENT,
        INSTALLED_CURRENT
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onClick$0(DialogInterface dialogInterface, int i) {
        jumpToLauncherSetting();
    }

    private void obtainDefaultHomePackageName() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        ResolveInfo resolveActivity = getPackageManager().resolveActivity(intent, 0);
        if (resolveActivity == null) {
            this.mDefaultHomePkg = "";
        } else {
            this.mDefaultHomePkg = resolveActivity.activityInfo.packageName;
        }
    }

    private void obtainJeejenStatus() {
        boolean z;
        this.mJeejenStatus = JeejenStatus.UNINSTALLED;
        String[] strArr = {"com.jeejen.family.miui", "com.jeejen.family"};
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        List<ResolveInfo> queryIntentActivities = getPackageManager().queryIntentActivities(intent, 131072);
        if (queryIntentActivities == null || queryIntentActivities.size() == 0) {
            return;
        }
        for (int i = 0; i < 2; i++) {
            String str = strArr[i];
            Iterator<ResolveInfo> it = queryIntentActivities.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                } else if (TextUtils.equals(str, it.next().activityInfo.packageName)) {
                    z = true;
                    break;
                }
            }
            if (z) {
                this.mInstalledJeejenPkgName = str;
                obtainDefaultHomePackageName();
                if (TextUtils.equals(this.mDefaultHomePkg, str)) {
                    this.mJeejenStatus = JeejenStatus.INSTALLED_CURRENT;
                    return;
                }
                this.mJeejenStatus = JeejenStatus.INSTALLED_NOT_CURRENT;
            }
        }
    }

    private void setDefaultHomeLauncher(String str) {
        obtainDefaultHomePackageName();
        PackageManager packageManager = getPackageManager();
        if (!TextUtils.isEmpty(this.mDefaultHomePkg)) {
            packageManager.clearPackagePreferredActivities(this.mDefaultHomePkg);
        }
        ResolveInfo resolveInfo = null;
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 131072);
        int size = queryIntentActivities.size();
        ComponentName[] componentNameArr = new ComponentName[size];
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ResolveInfo resolveInfo2 = queryIntentActivities.get(i2);
            ActivityInfo activityInfo = resolveInfo2.activityInfo;
            componentNameArr[i2] = new ComponentName(activityInfo.packageName, activityInfo.name);
            int i3 = resolveInfo2.match;
            if (i3 > i) {
                i = i3;
            }
            if (TextUtils.equals(str, resolveInfo2.activityInfo.packageName)) {
                resolveInfo = resolveInfo2;
            }
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MAIN");
        intentFilter.addCategory("android.intent.category.HOME");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        intentFilter.addCategory("android.intent.category.BROWSABLE");
        ActivityInfo activityInfo2 = resolveInfo.activityInfo;
        packageManager.addPreferredActivity(intentFilter, i, componentNameArr, new ComponentName(activityInfo2.packageName, activityInfo2.name));
    }

    private void updateState() {
        obtainJeejenStatus();
        int i = AnonymousClass1.$SwitchMap$com$android$settings$OldmanModeSettings$JeejenStatus[this.mJeejenStatus.ordinal()];
        if (i == 1) {
            this.mDesView.setText(R.string.oldman_installed_current_des);
            this.mActionBtnView.setText(R.string.oldman_installed_current_btn);
        } else if (i != 2) {
            this.mDesView.setText(R.string.oldman_uninstalled_des);
            this.mActionBtnView.setText(R.string.oldman_uninstalled_btn);
        } else {
            this.mDesView.setText(R.string.oldman_installed_not_current_des);
            this.mActionBtnView.setText(R.string.oldman_installed_not_current_btn);
        }
    }

    public void jumpToLauncherSetting() {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.applications.DefaultHomeSettings");
        JeejenStatus jeejenStatus = this.mJeejenStatus;
        JeejenStatus jeejenStatus2 = JeejenStatus.INSTALLED_CURRENT;
        String str = jeejenStatus == jeejenStatus2 ? "com.miui.home" : this.mInstalledJeejenPkgName;
        if (!TextUtils.isEmpty(str)) {
            setDefaultHomeLauncher(str);
        }
        startActivity(intent);
        ToastUtil.show(this, this.mJeejenStatus.equals(jeejenStatus2) ? R.string.default_launcher_set_system_message : R.string.default_launcher_set_jeejen_message, 0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mActionBtnView) {
            int i = AnonymousClass1.$SwitchMap$com$android$settings$OldmanModeSettings$JeejenStatus[this.mJeejenStatus.ordinal()];
            if (i == 1) {
                jumpToLauncherSetting();
            } else if (i == 2) {
                new AlertDialog.Builder(this).setMessage(getResources().getString(R.string.set_jeejen_prompt_message)).setTitle(R.string.set_jeejen_prompt_title).setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: com.android.settings.OldmanModeSettings$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        OldmanModeSettings.this.lambda$onClick$0(dialogInterface, i2);
                    }
                }).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).show();
            } else {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("market://details?id=com.jeejen.family&ref=com.miui.home_setting&back=true"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                    return;
                }
                Log.i("OldmanModeSettings", "start html to download");
                Intent intent2 = new Intent("android.intent.action.VIEW", Uri.parse("https://app.xiaomi.com/details?id=com.jeejen.family&back=true&ref=com.miui.home_setting"));
                if (intent2.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent2);
                }
            }
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.oldman_mode_settings);
        this.mDesView = (TextView) findViewById(R.id.des);
        Button button = (Button) findViewById(R.id.action_btn);
        this.mActionBtnView = button;
        button.setOnClickListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        updateState();
    }
}
