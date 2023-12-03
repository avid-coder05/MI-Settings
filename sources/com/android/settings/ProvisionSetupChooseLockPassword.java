package com.android.settings;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.ChooseLockPassword;
import miuix.provision.ProvisionBaseActivity;

/* loaded from: classes.dex */
public class ProvisionSetupChooseLockPassword extends ProvisionBaseActivity {
    private PreferenceFragmentCompat mSetupChooseLockPasswordFragment;

    /* loaded from: classes.dex */
    public static class ProvisionSetupChooseLockPasswordFragment extends ChooseLockPassword.ChooseLockPasswordFragment {
        private TextView mHeaderTitle;

        @Override // com.android.settings.ChooseLockPassword.ChooseLockPasswordFragment, com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return ProvisionSetupChooseLockPasswordFragment.class.getName();
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
            TextView textView = (TextView) getActivity().findViewById(R.id.provision_title);
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
            this.mPasswordEntry = (EditText) view.findViewById(R.id.setup_password_entry);
            TextView textView = (TextView) getActivity().findViewById(R.id.provision_sub_title);
            this.mHeaderText = textView;
            textView.setVisibility(0);
            this.mNextImage = (ImageView) getActivity().findViewById(R.id.provision_global_next_btn);
            this.mBackImage = (ImageView) getActivity().findViewById(R.id.provision_global_back_btn);
            this.mCancelButton = (TextView) getActivity().findViewById(R.id.provision_back_btn);
            this.mNextButton = (TextView) getActivity().findViewById(R.id.provision_next_btn);
            this.mBackImage.setOnClickListener(this);
            this.mNextImage.setOnClickListener(this);
        }
    }

    @Override // miuix.provision.ProvisionBaseActivity
    public boolean hasPreview() {
        return false;
    }

    @Override // miuix.provision.ProvisionBaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mSetupChooseLockPasswordFragment == null) {
            this.mSetupChooseLockPasswordFragment = new ProvisionSetupChooseLockPasswordFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.provision_container, this.mSetupChooseLockPasswordFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }
}
