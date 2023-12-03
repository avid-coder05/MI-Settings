package com.android.settings.applications.autofill;

import android.content.Intent;
import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.applications.defaultapps.DefaultAutofillPicker;
import miui.provider.ExtraNetwork;

/* loaded from: classes.dex */
public class AutofillPickerActivity extends SettingsActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return super.isValidFragment(str) || DefaultAutofillPicker.class.getName().equals(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        Intent intent = getIntent();
        String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
        intent.putExtra(":settings:show_fragment", DefaultAutofillPicker.class.getName());
        intent.putExtra(":settings:show_fragment_title_resid", R.string.autofill_app);
        intent.putExtra(ExtraNetwork.FIREWALL_PACKAGE_NAME, schemeSpecificPart);
        super.onCreate(bundle);
    }
}