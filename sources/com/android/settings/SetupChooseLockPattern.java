package com.android.settings;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.ChooseLockPattern;

/* loaded from: classes.dex */
public class SetupChooseLockPattern extends ChooseLockPattern {

    /* loaded from: classes.dex */
    public static class SetupChooseLockPatternFragment extends ChooseLockPattern.ChooseLockPatternFragment {
        private ImageView mBackImage;
        private TextView mFooterBackButton;
        private SetupFooterLayout mFooterLayout;

        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment, com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return SetupChooseLockPatternFragment.class.getName();
        }

        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment
        protected boolean isSetUp() {
            return true;
        }

        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment, android.view.View.OnClickListener
        public void onClick(View view) {
            super.onClick(view);
            if (view == this.mFooterBackButton || view == this.mBackImage) {
                getActivity().finish();
            }
        }

        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment, com.android.settings.KeyguardSettingsPreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null && actionBar.isShowing()) {
                actionBar.hide();
            }
            preSetupViews();
            View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
            SetupFooterLayout setupFooterLayout = (SetupFooterLayout) onCreateView.findViewById(R.id.setup_footer_layout);
            this.mFooterLayout = setupFooterLayout;
            this.mFooterBackButton = setupFooterLayout.getBackButton();
            ImageView backImg = this.mFooterLayout.getBackImg();
            this.mBackImage = backImg;
            backImg.setOnClickListener(this);
            this.mFooterBackButton.setOnClickListener(this);
            return onCreateView;
        }

        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment
        protected void onPasswordSaved(byte[] bArr, boolean z) {
            returnToKeyguardPasswordSettings(bArr);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment
        public void saveChosenPatternAndFinish() {
            this.mFooterBackButton.setEnabled(false);
            super.saveChosenPatternAndFinish();
        }

        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment
        protected void setNextEnable(boolean z) {
            this.mNextButton.setEnabled(z);
            this.mNextImage.setEnabled(z);
        }

        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment
        protected void setupViews(View view) {
            this.mSubHeaderText = (TextView) view.findViewById(R.id.setup_subHeaderText);
            this.mLockPatternView = (LockPatternView) view.findViewById(R.id.setup_lockPattern);
            SetupFooterLayout setupFooterLayout = (SetupFooterLayout) view.findViewById(R.id.setup_footer_layout);
            this.mFooterLayout = setupFooterLayout;
            this.mResetButton = setupFooterLayout.getSkipButton();
            this.mNextButton = this.mFooterLayout.getNextButton();
            ImageView nextImg = this.mFooterLayout.getNextImg();
            this.mNextImage = nextImg;
            nextImg.setOnClickListener(this);
            this.mResetButton.setVisibility(0);
            this.mResetButton.setEnabled(false);
            view.findViewById(R.id.setup_topLayout).setDefaultTouchRecepient(this.mLockPatternView);
        }
    }

    @Override // com.android.settings.ChooseLockPattern, com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", SetupChooseLockPatternFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.ChooseLockPattern, com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return true;
    }
}
