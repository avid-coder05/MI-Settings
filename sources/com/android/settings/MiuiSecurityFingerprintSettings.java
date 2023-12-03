package com.android.settings;

import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.security.FingerprintIdUtils;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.utils.FingerprintUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiSecurityFingerprintSettings extends Settings {

    /* loaded from: classes.dex */
    public static class MiuiSecurityFingerprintSettingsFragment extends KeyguardSettingsPreferenceFragment {
        private List<String> mFingerlistIds = new ArrayList();
        private boolean mFingerprintHardwareDetected = false;
        private String mFingerprintId;
        private PreferenceCategory mFingerprintList;
        private PreferenceCategory mMainFingerprintCategory;
        private List<String> mSecondFingerlistIds;

        /* JADX INFO: Access modifiers changed from: private */
        public void addFingerprint() {
            List<String> list = this.mFingerlistIds;
            if (list != null && list.size() >= 5) {
                showInformationDialog(R.string.max_fingerprint_number_reached);
                return;
            }
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycore", "com.miui.securityspace.ui.activity.SetFingerPrintActivity"));
            if (getIntent().getBooleanExtra("isFromSettings", false)) {
                intent.putExtra("isFromSettings", true);
            }
            startActivity(intent);
        }

        private void createNewPassword() {
            startFragment(this, "com.android.settings.MiuiSecurityChooseUnlock$MiuiSecurityChooseUnlockFragment", 106, new Bundle());
        }

        private String generateFingerprintName() {
            try {
                boolean[] zArr = new boolean[5];
                Iterator<String> it = this.mFingerlistIds.iterator();
                while (it.hasNext()) {
                    int parseFingerprintNameIndex = parseFingerprintNameIndex(it.next());
                    if (parseFingerprintNameIndex > 0 && parseFingerprintNameIndex <= 5) {
                        zArr[parseFingerprintNameIndex - 1] = true;
                    }
                }
                for (int i = 0; i < 5; i++) {
                    if (!zArr[i]) {
                        return getString(R.string.fingerprint_list_title) + (i + 1);
                    }
                }
                return null;
            } catch (Exception e) {
                Log.e(NewFingerprintActivity.class.getSimpleName(), e.getMessage(), e);
                return null;
            }
        }

        private void handleSecondFringprintIds() {
            HashMap userFingerprintIds = FingerprintIdUtils.getUserFingerprintIds(getActivity(), UserHandle.myUserId());
            if (userFingerprintIds != null && userFingerprintIds.size() > 0) {
                this.mSecondFingerlistIds.addAll(userFingerprintIds.keySet());
            }
            String str = this.mFingerprintId;
            if (str == null || str.length() <= 0) {
                return;
            }
            if (this.mSecondFingerlistIds.contains(this.mFingerprintId)) {
                this.mSecondFingerlistIds.remove(this.mFingerprintId);
            }
            this.mSecondFingerlistIds.add(0, this.mFingerprintId);
        }

        private int parseFingerprintNameIndex(String str) {
            if (TextUtils.isEmpty(FingerprintUtils.getFingerprintName(getActivity(), str))) {
                return -1;
            }
            return r0.charAt(r0.length() - 1) - '0';
        }

        private void processResult(int i, int i2) {
            if (i == 107 && i2 == -1 && MiuiSettings.Secure.hasCommonPassword(getActivity())) {
                createNewPassword();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showFingerprintDetails(String str, String str2) {
            Bundle bundle = new Bundle();
            bundle.putString("extra_fingerprint_key", str);
            bundle.putString("extra_fingerprint_title", str2);
            startFragment(this, MiuiFingerprintDetailFragment.class.getName(), 0, bundle, R.string.fingerprint_list_title);
        }

        private void showInformationDialog(int i) {
            showInformationDialog(getString(i));
        }

        private void showInformationDialog(String str) {
            new AlertDialog.Builder(getActivity()).setCancelable(false).setIconAttribute(16843605).setMessage(str).setPositiveButton(R.string.information_dialog_button_text, (DialogInterface.OnClickListener) null).create().show();
        }

        private void sortFingerprintList() {
            final HashMap hashMap = new HashMap();
            for (String str : this.mFingerlistIds) {
                hashMap.put(str, Long.valueOf(FingerprintUtils.getFingerprintCreateDate(getActivity(), str)));
            }
            Collections.sort(this.mFingerlistIds, new Comparator<String>() { // from class: com.android.settings.MiuiSecurityFingerprintSettings.MiuiSecurityFingerprintSettingsFragment.1
                @Override // java.util.Comparator
                public int compare(String str2, String str3) {
                    try {
                        return (int) ((((Long) hashMap.get(str2)).longValue() - ((Long) hashMap.get(str3)).longValue()) / 1000);
                    } catch (Exception unused) {
                        return 0;
                    }
                }
            });
        }

        private void updateFingerprintList() {
            this.mFingerlistIds = new FingerprintHelper(getActivity()).getFingerprintIds();
            this.mSecondFingerlistIds = new ArrayList();
            handleSecondFringprintIds();
            sortFingerprintList();
            Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.MiuiSecurityFingerprintSettings.MiuiSecurityFingerprintSettingsFragment.2
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    String key = preference.getKey();
                    String str = (String) preference.getTitle();
                    if ("add_fingerprint".equals(key)) {
                        MiuiSecurityFingerprintSettingsFragment.this.addFingerprint();
                        return true;
                    }
                    MiuiSecurityFingerprintSettingsFragment.this.showFingerprintDetails(key, str);
                    return true;
                }
            };
            this.mFingerprintList.removeAll();
            this.mMainFingerprintCategory.removeAll();
            for (int i = 0; i < this.mFingerlistIds.size(); i++) {
                String str = this.mFingerlistIds.get(i);
                Preference preference = new Preference(getPreferenceManager().getContext());
                preference.setKey(str);
                String fingerprintName = FingerprintUtils.getFingerprintName(getActivity(), str);
                if (TextUtils.isEmpty(fingerprintName)) {
                    fingerprintName = generateFingerprintName();
                    FingerprintUtils.setFingerprintName(getActivity(), str, fingerprintName);
                }
                preference.setTitle(fingerprintName);
                preference.setOnPreferenceClickListener(onPreferenceClickListener);
                if (this.mSecondFingerlistIds.contains(str)) {
                    this.mFingerprintList.addPreference(preference);
                } else {
                    this.mMainFingerprintCategory.addPreference(preference);
                }
            }
            Preference preference2 = new Preference(getPreferenceManager().getContext());
            preference2.setKey("add_fingerprint");
            preference2.setTitle(R.string.add_fingerprint_text);
            preference2.setOnPreferenceClickListener(onPreferenceClickListener);
            this.mFingerprintList.addPreference(preference2);
        }

        private void updatePreferencesByPasswordAndFingerprintState() {
            if (this.mFingerprintHardwareDetected) {
                updateFingerprintList();
                return;
            }
            getPreferenceScreen().removePreference(this.mFingerprintList);
            getPreferenceScreen().removePreference(this.mMainFingerprintCategory);
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return MiuiSecurityFingerprintSettingsFragment.class.getName();
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            processResult(i, i2);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) {
                this.mFingerprintId = extras.getString("fingerprint_id");
            }
            this.mFingerprintHardwareDetected = new FingerprintHelper(getActivity()).isHardwareDetected();
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public void onFragmentResult(int i, Bundle bundle) {
            processResult(i, bundle != null && bundle.getInt("miui_security_fragment_result", -1) == 0 ? -1 : 0);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            preference.getKey();
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService("keyguard");
            if (keyguardManager == null || !keyguardManager.isKeyguardLocked()) {
                PreferenceScreen preferenceScreen = getPreferenceScreen();
                if (preferenceScreen != null) {
                    preferenceScreen.removeAll();
                }
                addPreferencesFromResource(R.xml.security_settings_unlock_fingerprint);
                this.mFingerprintList = (PreferenceCategory) findPreference("fingerprint_list");
                this.mMainFingerprintCategory = (PreferenceCategory) findPreference("fingerprint_list_main");
                updatePreferencesByPasswordAndFingerprintState();
            }
        }
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", MiuiSecurityFingerprintSettingsFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return str.equals(MiuiSecurityFingerprintSettingsFragment.class.getName());
    }
}
