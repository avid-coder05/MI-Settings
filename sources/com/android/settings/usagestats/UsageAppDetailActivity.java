package com.android.settings.usagestats;

import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.SubSettings;

/* loaded from: classes2.dex */
public class UsageAppDetailActivity extends SubSettings {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.usage_state_app_usage_detail_title);
    }
}
