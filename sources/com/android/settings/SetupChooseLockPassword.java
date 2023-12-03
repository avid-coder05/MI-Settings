package com.android.settings;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.android.settings.ChooseLockPassword;

/* loaded from: classes.dex */
public class SetupChooseLockPassword extends ChooseLockPassword {

    /* loaded from: classes.dex */
    public static class SetupChooseLockPasswordFragment extends ChooseLockPassword.ChooseLockPasswordFragment {
        private SetupFooterLayout mFooterLayout;
        private TextView mHeaderTitle;

        @Override // com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment, com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return SetupChooseLockPasswordFragment.class.getName();
        }

        @Override // com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment
        protected boolean isSetUp() {
            return true;
        }

        @Override // com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
        }

        @Override // com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment, com.android.settings.KeyguardSettingsPreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null && actionBar.isShowing()) {
                actionBar.hide();
            }
            View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
            TextView textView = (TextView) onCreateView.findViewById(R.id.setup_choose_unlock_password_title);
            this.mHeaderTitle = textView;
            textView.setText(this.mIsAlphaMode ? R.string.setup_choose_unlock_mixpassword_title : R.string.setup_choose_unlock_password_title);
            return onCreateView;
        }

        @Override // com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment
        protected void onPasswordSaved(byte[] bArr, boolean z) {
            returnToKeyguardPasswordSettings(bArr);
        }

        @Override // com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment
        protected void setCancelEnable(boolean z) {
            this.mCancelButton.setEnabled(z);
            this.mBackImage.setEnabled(z);
        }

        @Override // com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment
        protected void setNextEnable(boolean z) {
            this.mNextButton.setEnabled(z);
            this.mNextImage.setEnabled(z);
        }

        @Override // com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment
        protected void setupViews(View view) {
            this.mHeaderText = (TextView) view.findViewById(R.id.setup_choose_unlock_password_msg);
            this.mPasswordEntry = (EditText) view.findViewById(R.id.setup_password_entry);
            SetupFooterLayout setupFooterLayout = (SetupFooterLayout) view.findViewById(R.id.setup_footer_layout);
            this.mFooterLayout = setupFooterLayout;
            this.mNextImage = setupFooterLayout.getNextImg();
            this.mBackImage = this.mFooterLayout.getBackImg();
            this.mCancelButton = this.mFooterLayout.getBackButton();
            this.mNextButton = this.mFooterLayout.getNextButton();
            this.mBackImage.setOnClickListener(this);
            this.mNextImage.setOnClickListener(this);
        }
    }

    @Override // com.android.settings.ChooseLockPassword, com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", SetupChooseLockPasswordFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.ChooseLockPassword, com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return SetupChooseLockPasswordFragment.class.getName().equals(str);
    }
}
