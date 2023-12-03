package com.android.settings;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.ChooseLockPattern;
import miuix.provision.ProvisionBaseActivity;

/* loaded from: classes.dex */
public class ProvisionSetupChooseLockPattern extends ProvisionBaseActivity {
    private PreferenceFragmentCompat mSetupChooseLockPatternFragment;

    /* loaded from: classes.dex */
    public static class ProvisionSetupChooseLockPatternFragment extends ChooseLockPattern.ChooseLockPatternFragment {
        private ImageView mBackImage;
        private TextView mFooterBackButton;

        @Override // com.android.settings.ChooseLockPattern.ChooseLockPatternFragment, com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return ProvisionSetupChooseLockPatternFragment.class.getName();
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
            this.mFooterBackButton = (TextView) getActivity().findViewById(R.id.provision_back_btn);
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.provision_global_back_btn);
            this.mBackImage = imageView;
            imageView.setOnClickListener(this);
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
            TextView textView = (TextView) getActivity().findViewById(R.id.provision_sub_title);
            this.mSubHeaderText = textView;
            textView.setVisibility(0);
            this.mLockPatternView = (LockPatternView) view.findViewById(R.id.setup_lockPattern);
            this.mResetButton = (TextView) getActivity().findViewById(R.id.provision_skip_btn);
            this.mNextButton = (TextView) getActivity().findViewById(R.id.provision_next_btn);
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.provision_global_next_btn);
            this.mNextImage = imageView;
            imageView.setOnClickListener(this);
            this.mResetButton.setVisibility(0);
            this.mResetButton.setEnabled(false);
            view.findViewById(R.id.setup_topLayout).setDefaultTouchRecepient(this.mLockPatternView);
        }
    }

    @Override // miuix.provision.ProvisionBaseActivity
    public boolean hasPreview() {
        return false;
    }

    @Override // miuix.provision.ProvisionBaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.setup_choose_unlock_pattern_title);
        if (this.mSetupChooseLockPatternFragment == null) {
            this.mSetupChooseLockPatternFragment = new ProvisionSetupChooseLockPatternFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.provision_container, this.mSetupChooseLockPatternFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }
}
