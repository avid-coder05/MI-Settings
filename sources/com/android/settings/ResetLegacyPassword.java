package com.android.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.ConfirmLockPassword;
import com.android.settings.MiuiSecurityChooseUnlock;

/* loaded from: classes.dex */
public class ResetLegacyPassword extends Settings {

    /* loaded from: classes.dex */
    public static class ResetLegacyPasswordInstructionFragment extends KeyguardSettingsPreferenceFragment {
        @Override // com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return ResetLegacyPasswordInstructionFragment.class.getName();
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 100 && i2 == -1) {
                startFragment(this, MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.class.getName(), 101, null);
            } else if (101 == i && -1 == i2) {
                getActivity().setResult(-1);
                finish();
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            setThemeRes(R.style.Theme_DayNight_Settings_NoTitle);
        }

        @Override // com.android.settings.KeyguardSettingsPreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            View inflate = layoutInflater.inflate(R.layout.reset_legacy_password_instruction_layout, (ViewGroup) null);
            inflate.findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.ResetLegacyPassword.ResetLegacyPasswordInstructionFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ResetLegacyPasswordInstructionFragment resetLegacyPasswordInstructionFragment = ResetLegacyPasswordInstructionFragment.this;
                    resetLegacyPasswordInstructionFragment.startFragment(resetLegacyPasswordInstructionFragment, ConfirmLockPassword.ConfirmLockPasswordFragment.class.getName(), 100, null);
                }
            });
            return inflate;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return true;
    }
}
