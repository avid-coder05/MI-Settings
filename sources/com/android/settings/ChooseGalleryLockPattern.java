package com.android.settings;

import android.content.Intent;
import android.os.Bundle;
import android.security.ChooseLockSettingsHelper;
import android.security.MiuiLockPatternUtils;
import com.android.settings.ChooseLockPattern;
import miui.os.UserHandle;

/* loaded from: classes.dex */
public class ChooseGalleryLockPattern extends ChooseLockPattern {

    /* loaded from: classes.dex */
    public static class ChooseGalleryFragment extends ChooseLockPattern.ChooseLockPatternFragment {
        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity(), 2);
            String callingPackage = getActivity().getCallingPackage();
            if ("com.miui.gallery".equals(callingPackage) || "com.android.settings".equals(callingPackage)) {
                return;
            }
            getActivity().setResult(0);
            getActivity().finish();
        }

        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment
        protected void onCreateNoSavedState() {
            updateStage(ChooseLockPattern.ChooseLockPatternFragment.Stage.Introduction);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment
        public void preSetupViews() {
            ChooseLockPattern.ChooseLockPatternFragment.Stage.Introduction.headerMessage = R.string.private_gallery_recording_intro_header;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment
        public void saveChosenPatternAndFinish() {
            MiuiLockPatternUtils utils = this.mChooseLockSettingsHelper.utils();
            int myUserId = UserHandle.myUserId();
            utils.saveMiuiLockPatternAsUser(this.mChosenPattern, myUserId);
            this.mChooseLockSettingsHelper.setPrivateGalleryEnabledAsUser(true, myUserId);
            getActivity().setResult(-1);
            getActivity().finish();
        }
    }

    @Override // com.android.settings.ChooseLockPattern, com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ChooseGalleryFragment.class.getName());
        return intent;
    }
}
