package com.android.settings.inputmethod;

import android.content.Intent;
import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.language.MiuiLanguageAndInputSettings;
import miui.os.Build;

/* loaded from: classes.dex */
public class AvailableVirtualKeyboardActivity extends SettingsActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        if (!Build.IS_INTERNATIONAL_BUILD) {
            Intent intent = getIntent();
            intent.putExtra(":settings:show_fragment", MiuiLanguageAndInputSettings.class.getName());
            intent.putExtra(":settings:show_fragment_title", getString(R.string.language_settings));
            Bundle bundleExtra = intent.getBundleExtra(":settings:show_fragment_args");
            if (bundleExtra == null) {
                bundleExtra = new Bundle();
            }
            bundleExtra.putBoolean("extra_key_use_custom_fragment", true);
            intent.putExtra(":settings:show_fragment_args", bundleExtra);
        }
        super.onCreate(bundle);
    }
}
