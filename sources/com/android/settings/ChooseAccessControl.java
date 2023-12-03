package com.android.settings;

import android.content.Intent;
import android.os.Bundle;
import com.android.settings.ChooseLockPattern;

/* loaded from: classes.dex */
public class ChooseAccessControl extends ChooseLockPattern {

    /* loaded from: classes.dex */
    public static class ChooseAccessControlFragment extends ChooseLockPattern.ChooseLockPatternFragment {
        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment, androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i != 2300 || i2 == -1) {
                return;
            }
            setResult(0);
            finish();
        }

        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            if (this.mChooseLockSettingsHelper.isACLockEnabled()) {
                startActivityForResult(new Intent(getActivity(), ConfirmAccessControl.class), 2300);
            }
        }

        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment
        protected void onCreateNoSavedState() {
            updateStage(ChooseLockPattern.ChooseLockPatternFragment.Stage.Introduction);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment
        public void preSetupViews() {
            ChooseLockPattern.ChooseLockPatternFragment.Stage.Introduction.headerMessage = R.string.access_control_recording_intro_header;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment
        public void saveChosenPatternAndFinish() {
            this.mChooseLockSettingsHelper.utils().saveMiuiLockPattern(this.mChosenPattern);
            this.mChooseLockSettingsHelper.setACLockEnabled(true);
            getActivity().setResult(-1);
            getActivity().finish();
        }
    }

    @Override // com.android.settings.ChooseLockPattern, com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ChooseAccessControlFragment.class.getName());
        return intent;
    }
}
