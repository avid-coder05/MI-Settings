package com.android.settings;

import android.os.Bundle;
import android.text.TextUtils;
import com.android.settings.password.ConfirmDeviceCredentialBaseActivity;
import miuix.appcompat.app.ActionBar;

/* loaded from: classes.dex */
public abstract class BaseConfirmLockActivity extends ConfirmDeviceCredentialBaseActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.password.ConfirmDeviceCredentialBaseActivity, com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActionBar appCompatActionBar = getAppCompatActionBar();
        CharSequence header = MiuiKeyguardSettingsUtils.getHeader(getIntent());
        CharSequence text = getText(R.string.empty_title);
        if (appCompatActionBar != null) {
            appCompatActionBar.setExpandState(0);
            appCompatActionBar.setResizable(false);
            if (TextUtils.isEmpty(header)) {
                appCompatActionBar.setTitle(text);
            } else {
                appCompatActionBar.setTitle(header);
            }
        }
    }
}
