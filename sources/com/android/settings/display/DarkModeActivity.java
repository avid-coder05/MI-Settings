package com.android.settings.display;

import android.os.Bundle;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SubSettings;
import com.android.settingslib.util.MiStatInterfaceUtils;

/* loaded from: classes.dex */
public class DarkModeActivity extends SubSettings {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        MiuiUtils.notifyNightModeShowStateChange(this);
        MiStatInterfaceUtils.trackPageStart("DarkModeActivity");
        setTitle(R.string.dark_mode_time_settings);
    }
}
