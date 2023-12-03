package com.android.settings;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.settings.MiuiSecurityChooseUnlock;

/* loaded from: classes.dex */
public class SetUpMiuiSecurityChooseUnlock extends MiuiSecurityChooseUnlock {

    /* loaded from: classes.dex */
    public static class InternalActivity extends SetUpMiuiSecurityChooseUnlock {
    }

    /* loaded from: classes.dex */
    public static class SetUpMiuiSecurityChooseUnlockFragment extends MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment {
        private FrameLayout mBackLayout;
        private SetupFooterLayout mFooterLayout;
        private TextView mHeadMsg;
        private TextView mHeadTitle;
        private FrameLayout mNextLayout;
        private TextView mSkipButton;

        @Override // com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment, com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return SetUpMiuiSecurityChooseUnlockFragment.class.getName();
        }

        @Override // com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment, com.android.settings.KeyguardSettingsPreferenceFragment
        protected View inflateCustomizeView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null && actionBar.isShowing()) {
                actionBar.hide();
            }
            View inflate = layoutInflater.inflate(R.layout.setup_choose_unlock, viewGroup, false);
            SetupFooterLayout setupFooterLayout = (SetupFooterLayout) inflate.findViewById(R.id.setup_footer_layout);
            this.mFooterLayout = setupFooterLayout;
            this.mNextLayout = setupFooterLayout.getNextLayout();
            this.mBackLayout = this.mFooterLayout.getBackLayout();
            this.mSkipButton = this.mFooterLayout.getSkipButton();
            this.mFooterLayout.setBackLayoutClickable();
            this.mFooterLayout.setNextLayoutClickable();
            this.mHeadTitle = (TextView) inflate.findViewById(R.id.setup_choose_unlock_title);
            this.mHeadMsg = (TextView) inflate.findViewById(R.id.setup_choose_unlock_msg);
            this.mBackLayout.setVisibility(4);
            this.mNextLayout.setVisibility(4);
            this.mSkipButton.setVisibility(0);
            if (viewGroup != null) {
                PreferenceFrameLayout.LayoutParams layoutParams = ((ViewGroup) viewGroup.getParent()).getLayoutParams();
                if (layoutParams instanceof PreferenceFrameLayout.LayoutParams) {
                    layoutParams.removeBorders = true;
                }
            }
            this.mBackLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SetUpMiuiSecurityChooseUnlock.SetUpMiuiSecurityChooseUnlockFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SetUpMiuiSecurityChooseUnlockFragment.this.getActivity().setResult(0);
                    SetUpMiuiSecurityChooseUnlockFragment.this.finish();
                }
            });
            this.mSkipButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SetUpMiuiSecurityChooseUnlock.SetUpMiuiSecurityChooseUnlockFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SetUpMiuiSecurityChooseUnlockFragment.this.getActivity().setResult(11);
                    SetUpMiuiSecurityChooseUnlockFragment.this.finish();
                }
            });
            return inflate;
        }

        @Override // com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment
        protected boolean isInternalActivity() {
            return getActivity() instanceof InternalActivity;
        }

        @Override // com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment
        protected boolean isSetUp() {
            return true;
        }

        @Override // com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
        }

        @Override // com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            if (this.mAddKeyguardpasswordThenAddFingerprint) {
                this.mHeadMsg.setText(R.string.choose_unlock_fingerprint_msg);
            }
            this.mSkipButton.setText(R.string.setup_password_skip);
        }
    }

    @Override // com.android.settings.MiuiSecurityChooseUnlock, com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", SetUpMiuiSecurityChooseUnlockFragment.class.getName());
        return intent;
    }
}
