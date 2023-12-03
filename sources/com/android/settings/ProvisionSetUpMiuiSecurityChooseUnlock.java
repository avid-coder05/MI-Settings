package com.android.settings;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.MiuiSecurityChooseUnlock;
import miuix.provision.ProvisionBaseActivity;

/* loaded from: classes.dex */
public class ProvisionSetUpMiuiSecurityChooseUnlock extends ProvisionBaseActivity {
    private PreferenceFragmentCompat mSetupMiuiChooseLockFragment;

    /* loaded from: classes.dex */
    public static class InternalActivity extends ProvisionSetUpMiuiSecurityChooseUnlock {
    }

    /* loaded from: classes.dex */
    public static class ProvisionSetUpMiuiSecurityChooseUnlockFragment extends MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment {
        private TextView mBackLayout;
        private View.OnClickListener mBtnClickListener = new View.OnClickListener() { // from class: com.android.settings.ProvisionSetUpMiuiSecurityChooseUnlock.ProvisionSetUpMiuiSecurityChooseUnlockFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (view.getId() == R.id.provision_back_btn || view.getId() == R.id.provision_global_back_btn) {
                    ProvisionSetUpMiuiSecurityChooseUnlockFragment.this.getActivity().setResult(0);
                    ProvisionSetUpMiuiSecurityChooseUnlockFragment.this.finish();
                }
            }
        };
        private ImageButton mGlobalBack;

        @Override // com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment, com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return "ProvisionSetUpMiuiSecurityChooseUnlockFragment";
        }

        @Override // com.android.settings.MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment, com.android.settings.KeyguardSettingsPreferenceFragment
        protected View inflateCustomizeView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null && actionBar.isShowing()) {
                actionBar.hide();
            }
            View inflate = layoutInflater.inflate(R.layout.provision_setup_choose_unlock, viewGroup, false);
            if (viewGroup != null) {
                PreferenceFrameLayout.LayoutParams layoutParams = ((ViewGroup) viewGroup.getParent()).getLayoutParams();
                if (layoutParams instanceof PreferenceFrameLayout.LayoutParams) {
                    layoutParams.removeBorders = true;
                }
            }
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

        @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            if (getActivity() == null) {
                return;
            }
            TextView textView = (TextView) getActivity().findViewById(R.id.provision_back_btn);
            this.mBackLayout = textView;
            textView.setOnClickListener(this.mBtnClickListener);
            ImageButton imageButton = (ImageButton) getActivity().findViewById(R.id.provision_global_back_btn);
            this.mGlobalBack = imageButton;
            imageButton.setOnClickListener(this.mBtnClickListener);
            SetupFooterLayout.updateViewVisibility(this.mBackLayout, this.mGlobalBack);
            getActivity().findViewById(R.id.provision_lyt_btn_next).setVisibility(4);
        }
    }

    @Override // miuix.provision.ProvisionBaseActivity
    public boolean hasPreview() {
        return false;
    }

    @Override // miuix.provision.ProvisionBaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.password_entrance_title);
        this.mSubTitle.setVisibility(0);
        if (MiuiKeyguardSettingsUtils.getBooolExtra(bundle, getIntent(), "add_keyguard_password_then_add_fingerprint")) {
            setSubTitle(R.string.choose_unlock_fingerprint_msg);
        } else if (MiuiKeyguardSettingsUtils.getBooolExtra(bundle, getIntent(), "add_keyguard_password_then_add_face_recoginition")) {
            setSubTitle(R.string.choose_unlock_face_msg);
        } else {
            setSubTitle(R.string.turn_on_keyguard_password_alert);
        }
        if (this.mSetupMiuiChooseLockFragment == null) {
            this.mSetupMiuiChooseLockFragment = new ProvisionSetUpMiuiSecurityChooseUnlockFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.provision_container, this.mSetupMiuiChooseLockFragment).commit();
        getFragmentManager().executePendingTransactions();
    }
}
