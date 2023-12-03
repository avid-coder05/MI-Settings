package com.android.settings.privacypassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MiuiSettings;
import com.android.settings.KeyguardSettingsPreferenceFragment;
import com.android.settings.SettingsActivity;

/* loaded from: classes2.dex */
public class TransparentHelper extends SettingsActivity {

    /* loaded from: classes2.dex */
    public static class TransparentHelperFragment extends KeyguardSettingsPreferenceFragment {
        private boolean mIsEnterFromSetting;
        private PrivacyPasswordManager mPrivacyPasswordManager;

        private void processResult(int i, int i2) {
            if (i == 29000) {
                if (i2 == -1) {
                    Intent intent = new Intent(getActivity(), AddAccountActivity.class);
                    intent.putExtra("is_start_modify", false);
                    intent.putExtra("enter_forgetpage_way", 2);
                    startActivity(intent);
                    finish();
                }
                finish();
            } else if (i != 290224) {
            } else {
                if (i2 == -1) {
                    Intent intent2 = new Intent(getActivity(), ModifyAndInstructionPrivacyPassword.class);
                    intent2.putExtra("enter_from_settings", this.mIsEnterFromSetting);
                    startActivity(intent2);
                    finish();
                }
                finish();
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return TransparentHelperFragment.class.getName();
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            processResult(i, i2);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            if (bundle == null || !bundle.getBoolean("is_onCreated", false)) {
                this.mIsEnterFromSetting = getActivity().getIntent().getBooleanExtra("enter_from_settings", true);
                String stringExtra = getActivity().getIntent().getStringExtra("bind_account_extra");
                if (stringExtra != null && stringExtra.equals("bind_account")) {
                    Intent intent = new Intent(getActivity(), PrivacyPasswordConfirmAccessControl.class);
                    intent.putExtra("enter_from_settings", true);
                    startActivityForResult(intent, 29000);
                    return;
                }
                PrivacyPasswordManager privacyPasswordManager = PrivacyPasswordManager.getInstance(getActivity());
                this.mPrivacyPasswordManager = privacyPasswordManager;
                if (privacyPasswordManager.havePattern()) {
                    Intent intent2 = new Intent(getActivity(), PrivacyPasswordConfirmAccessControl.class);
                    intent2.putExtra("enter_from_settings", true);
                    startActivityForResult(intent2, 290224);
                    return;
                }
                getActivity().startActivity(new Intent(getActivity(), PrivacyPasswordSetting.class));
                finish();
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public void onFragmentResult(int i, Bundle bundle) {
            processResult(i, bundle != null && bundle.getInt("miui_security_fragment_result", -1) == 0 ? -1 : 0);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            bundle.putBoolean("is_onCreated", true);
        }
    }

    public static boolean isScreenLockOpen(Context context) {
        return MiuiSettings.Secure.hasCommonPassword(context);
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", TransparentHelperFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return TransparentHelperFragment.class.getName().equals(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle("");
    }
}
