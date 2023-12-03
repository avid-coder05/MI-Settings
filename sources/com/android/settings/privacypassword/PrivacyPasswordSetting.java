package com.android.settings.privacypassword;

import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.MiuiLockPatternUtils;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.ConfirmLockPassword;
import com.android.settings.ConfirmLockPattern;
import com.android.settings.ConfirmSpacePasswordActivity;
import com.android.settings.ConfirmSpacePatternActivity;
import com.android.settings.KeyguardSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.SettingsCompatActivity;
import miui.securityspace.CrossUserUtils;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes2.dex */
public class PrivacyPasswordSetting extends SettingsCompatActivity {

    /* loaded from: classes2.dex */
    public static class PrivacyPasswordSettingFragment extends KeyguardSettingsPreferenceFragment {
        private final Preference.OnPreferenceChangeListener mPasswordSettingsClickListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.privacypassword.PrivacyPasswordSetting.PrivacyPasswordSettingFragment.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                if ("privacy_password_settings".equals(preference.getKey())) {
                    if (TransparentHelper.isScreenLockOpen(PrivacyPasswordSettingFragment.this.getActivity())) {
                        PrivacyPasswordSettingFragment.this.startConfirmActivity();
                    } else if (PrivacyPasswordSettingFragment.this.mPrivacyPasswordManager.havePattern()) {
                        Intent intent = new Intent(PrivacyPasswordSettingFragment.this.getActivity(), PrivacyPasswordConfirmAccessControl.class);
                        intent.putExtra("enter_from_settings", true);
                        PrivacyPasswordSettingFragment.this.startActivityForResult(intent, 290225);
                    } else {
                        Intent intent2 = new Intent(PrivacyPasswordSettingFragment.this.getActivity(), PrivacyPasswordChooseAccessControl.class);
                        intent2.putExtra(YellowPageContract.MipubPhoneEvent.URI_PARAM_EXTRA_DATA, "choose_suspend");
                        PrivacyPasswordSettingFragment.this.startActivity(intent2);
                        PrivacyPasswordSettingFragment.this.finish();
                    }
                }
                return true;
            }
        };
        private PrivacyPasswordManager mPrivacyPasswordManager;

        private boolean isManagedProfile() {
            UserInfo userInfo = ((UserManager) getActivity().getSystemService("user")).getUserInfo(UserHandle.myUserId());
            try {
                return ((Boolean) userInfo.getClass().getMethod("isManagedProfile", new Class[0]).invoke(userInfo, new Object[0])).booleanValue();
            } catch (Exception e) {
                Log.e("PrivacyPasswordSetting", "isManageedProfile error", e);
                return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startConfirmActivity() {
            Class cls;
            FragmentActivity activity = getActivity();
            int myUserId = isManagedProfile() ? 0 : UserHandle.myUserId();
            int activePasswordQuality = new MiuiLockPatternUtils(activity.getApplicationContext()).getActivePasswordQuality(myUserId);
            if (activePasswordQuality != 0) {
                if (activePasswordQuality == 65536) {
                    cls = ConfirmLockPattern.InternalActivity.class;
                    if (CrossUserUtils.isAirSpace(activity.getApplicationContext(), myUserId)) {
                        cls = ConfirmSpacePatternActivity.class;
                    }
                } else {
                    cls = ConfirmLockPassword.InternalActivity.class;
                    if (CrossUserUtils.isAirSpace(activity.getApplicationContext(), myUserId)) {
                        cls = ConfirmSpacePasswordActivity.class;
                    }
                }
                Intent intent = new Intent(activity, cls);
                if (myUserId != 0) {
                    PrivacyPasswordUtils.putIntentExtra(getActivity(), intent);
                }
                startActivityForResult(intent, 290223);
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return PrivacyPasswordSettingFragment.class.getName();
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i != 290223) {
                if (i != 290225) {
                    return;
                }
                if (i2 == -1) {
                    startActivity(new Intent(getActivity(), ModifyAndInstructionPrivacyPassword.class));
                }
                finish();
                return;
            }
            if (i2 == -1) {
                Intent intent2 = new Intent(getActivity(), PrivacyPasswordChooseAccessControl.class);
                intent2.putExtra(YellowPageContract.MipubPhoneEvent.URI_PARAM_EXTRA_DATA, "choose_suspend");
                startActivity(intent2);
            }
            finish();
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.privacy_password_setting);
            this.mPrivacyPasswordManager = PrivacyPasswordManager.getInstance(getActivity().getApplicationContext());
            ((CheckBoxPreference) findPreference("privacy_password_settings")).setOnPreferenceChangeListener(this.mPasswordSettingsClickListener);
            getActivity().setResult(-1);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
        public void onDetach() {
            if (getTargetFragment() != null && (getTargetFragment() instanceof KeyguardSettingsPreferenceFragment)) {
                new Handler().post(new Runnable() { // from class: com.android.settings.privacypassword.PrivacyPasswordSetting.PrivacyPasswordSettingFragment.2
                    @Override // java.lang.Runnable
                    public void run() {
                        Bundle bundle = new Bundle();
                        bundle.putInt("miui_security_fragment_result", 0);
                        ((KeyguardSettingsPreferenceFragment) PrivacyPasswordSettingFragment.this.getTargetFragment()).onFragmentResult(PrivacyPasswordSettingFragment.this.getTargetRequestCode(), bundle);
                    }
                });
            }
            super.onDetach();
        }
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", PrivacyPasswordSettingFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return PrivacyPasswordSettingFragment.class.getName().equals(str);
    }
}
