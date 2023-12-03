package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.security.MiuiLockPatternUtils;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.FingerprintManageSetting;
import com.android.settings.NewFingerprintInternalActivity;
import com.android.settings.compat.RestrictedLockUtilsCompat;
import com.android.settings.utils.FingerprintUtils;
import com.android.settings.utils.MiuiGxzwUtils;
import com.android.settings.utils.TabletUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.miuisettings.preference.Preference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import miui.os.Build;
import miuix.preference.DropDownPreference;

/* loaded from: classes.dex */
public class FingerprintManageSetting extends SettingsCompatActivity {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AlwaysClickablePreference extends Preference {
        private Preference.OnPreferenceClickListener mOnClickListener;

        public AlwaysClickablePreference(Context context) {
            super(context);
        }

        @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            preferenceViewHolder.itemView.setEnabled(true);
        }

        @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
        public void performClick() {
            Preference.OnPreferenceClickListener onPreferenceClickListener = this.mOnClickListener;
            if (onPreferenceClickListener != null) {
                onPreferenceClickListener.onPreferenceClick(this);
            }
        }

        @Override // androidx.preference.Preference
        public void setOnPreferenceClickListener(Preference.OnPreferenceClickListener onPreferenceClickListener) {
            this.mOnClickListener = onPreferenceClickListener;
        }
    }

    /* loaded from: classes.dex */
    public static class FingerprintManageFragment extends KeyguardSettingsPreferenceFragment implements OnBackPressedListener {
        private static final int DEFAULT_FINGERPRINT_UNLOCK_TYPE;
        private static final String RO_BOOT_HWC;
        private PreferenceCategory mApplyFingerprintCategory;
        private List<String> mFingerlistIds = new ArrayList();
        private PreferenceCategory mFingerprintList;
        private TextView mFingerprintTipsForCts;
        private Toast mToast;

        static {
            String str = SystemProperties.get("ro.boot.hwc", "");
            RO_BOOT_HWC = str;
            DEFAULT_FINGERPRINT_UNLOCK_TYPE = ("INDIA".equalsIgnoreCase(str) || "IN".equalsIgnoreCase(str)) ? 1 : 0;
        }

        private void addApplyFingerprintItem(String str, String str2, boolean z) {
            final CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPreferenceManager().getContext());
            checkBoxPreference.setKey(str);
            checkBoxPreference.setTitle(str2);
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.FingerprintManageSetting.FingerprintManageFragment.7
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(androidx.preference.Preference preference, Object obj) {
                    boolean booleanValue = ((Boolean) obj).booleanValue();
                    Settings.Secure.putInt(FingerprintManageFragment.this.getContentResolver(), checkBoxPreference.getKey(), booleanValue ? 2 : 1);
                    if (booleanValue && FingerprintManageFragment.this.mFingerlistIds != null && FingerprintManageFragment.this.mFingerlistIds.size() == 0) {
                        FingerprintManageFragment.this.addFingerprint();
                    }
                    return true;
                }
            });
            this.mApplyFingerprintCategory.addPreference(checkBoxPreference);
            int i = Settings.Secure.getInt(getContentResolver(), str, "miui_keyguard".equals(str) ? 2 : 1);
            checkBoxPreference.setEnabled(true);
            checkBoxPreference.setChecked(i == 2);
            if (z) {
                return;
            }
            checkBoxPreference.setEnabled(false);
            checkBoxPreference.setChecked(true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addFingerprint() {
            List<String> list = this.mFingerlistIds;
            if (list != null && list.size() >= 5) {
                showInformationToast(R.string.max_fingerprint_reached_toast);
            } else if (TabletUtils.IS_TABLET) {
                startFragment((Fragment) null, MiuiGxzwUtils.isGxzwSensor() ? GxzwNewFingerprintFragment.class.getName() : NewFingerprintInternalActivity.NewFingerprintFragment.class.getName(), 107, (Bundle) null, R.string.add_fingerprint_text);
            } else {
                Intent intent = new Intent(getActivity(), NewFingerprintInternalActivity.class);
                intent.putExtra(":android:show_fragment_title", R.string.empty_title);
                startActivityForResult(intent, 107);
                finish();
            }
        }

        private void checkedFingerprintAllItemState(boolean z) {
            if (this.mApplyFingerprintCategory == null) {
                return;
            }
            for (int i = 0; i < this.mApplyFingerprintCategory.getPreferenceCount(); i++) {
                androidx.preference.Preference preference = this.mApplyFingerprintCategory.getPreference(i);
                if (preference != null && (preference instanceof CheckBoxPreference)) {
                    preference.setEnabled(z);
                }
            }
        }

        private int getFingerprintUnlockType() {
            return Settings.Secure.getIntForUser(getContentResolver(), "fingerprint_unlock_type", DEFAULT_FINGERPRINT_UNLOCK_TYPE, 0) == 1 ? 1 : 0;
        }

        private boolean isFodAodLowlightShowEnable() {
            return Settings.Secure.getIntForUser(getContentResolver(), "gxzw_icon_aod_lowlight_show_enable", 0, 0) == 1;
        }

        private boolean isFodAodShowEnable() {
            return Settings.Secure.getIntForUser(getContentResolver(), "gxzw_icon_aod_show_enable", 1, 0) == 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$setupUnlockTypeCategory$0(DropDownPreference dropDownPreference, androidx.preference.Preference preference, Object obj) {
            setFingerprintUnlockType(Integer.parseInt((String) obj));
            updateFingerprintUnlockTypePreference(dropDownPreference);
            return true;
        }

        private void setFingerprintUnlockType(int i) {
            Settings.Secure.putIntForUser(getContentResolver(), "fingerprint_unlock_type", i != 1 ? 0 : 1, 0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setFodAodLowlightShowEnable(boolean z) {
            Settings.Secure.putIntForUser(getContentResolver(), "gxzw_icon_aod_lowlight_show_enable", z ? 1 : 0, 0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setFodAodShowEnable(boolean z) {
            Settings.Secure.putIntForUser(getContentResolver(), "gxzw_icon_aod_show_enable", z ? 1 : 0, 0);
        }

        private void setupUnlockTypeCategory(PreferenceScreen preferenceScreen) {
            PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("unlock_type");
            if (!FingerprintUtils.isBroadSideFingerprint()) {
                preferenceScreen.removePreference(preferenceCategory);
                return;
            }
            final DropDownPreference dropDownPreference = (DropDownPreference) preferenceScreen.findPreference("fingerprint_unlock_type");
            dropDownPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.FingerprintManageSetting$FingerprintManageFragment$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public final boolean onPreferenceChange(androidx.preference.Preference preference, Object obj) {
                    boolean lambda$setupUnlockTypeCategory$0;
                    lambda$setupUnlockTypeCategory$0 = FingerprintManageSetting.FingerprintManageFragment.this.lambda$setupUnlockTypeCategory$0(dropDownPreference, preference, obj);
                    return lambda$setupUnlockTypeCategory$0;
                }
            });
            updateFingerprintUnlockTypePreference(dropDownPreference);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showFingerprintDetails(String str, String str2) {
            Bundle bundle = new Bundle();
            bundle.putString("extra_fingerprint_key", str);
            bundle.putString("extra_fingerprint_title", str2);
            startFragment(this, MiuiFingerprintDetailFragment.class.getName(), 1097, bundle, R.string.fingerprint_list_title);
        }

        private void showFingerprintTipsForCts() {
            SpannableString spannableString = new SpannableString(getText(R.string.security_settings_fingerprint_enroll_disclaimer_lockscreen_disabled));
            Annotation[] annotationArr = (Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class);
            final FragmentActivity activity = getActivity();
            for (Annotation annotation : annotationArr) {
                final String value = annotation.getValue();
                spannableString.setSpan(new ClickableSpan() { // from class: com.android.settings.FingerprintManageSetting.FingerprintManageFragment.8
                    @Override // android.text.style.ClickableSpan
                    public void onClick(View view) {
                        if (!"admin_details".equals(value)) {
                            "url".equals(value);
                            return;
                        }
                        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(activity, RestrictedLockUtilsCompat.checkIfKeyguardFeaturesDisabled(activity, 32, UserHandle.myUserId()));
                    }

                    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                    public void updateDrawState(TextPaint textPaint) {
                        super.updateDrawState(textPaint);
                        if ("admin_details".equals(value)) {
                            textPaint.setColor(-16776961);
                        } else if ("url".equals(value)) {
                            textPaint.setColor(0);
                        }
                        textPaint.setUnderlineText(false);
                    }
                }, spannableString.getSpanStart(annotation), spannableString.getSpanEnd(annotation), 33);
            }
            this.mFingerprintTipsForCts.setHighlightColor(0);
            this.mFingerprintTipsForCts.append(spannableString);
            this.mFingerprintTipsForCts.setMovementMethod(LinkMovementMethod.getInstance());
        }

        private void showInformationToast(int i) {
            Toast toast = this.mToast;
            if (toast != null) {
                toast.cancel();
            }
            Toast makeText = Toast.makeText(getActivity(), i, 0);
            this.mToast = makeText;
            makeText.show();
        }

        private void sortFingerprintList() {
            final HashMap hashMap = new HashMap();
            for (String str : this.mFingerlistIds) {
                hashMap.put(str, Long.valueOf(FingerprintUtils.getFingerprintCreateDate(getActivity(), str)));
            }
            Collections.sort(this.mFingerlistIds, new Comparator<String>() { // from class: com.android.settings.FingerprintManageSetting.FingerprintManageFragment.6
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

        private void updateApplyCategories() {
            String[] stringArray = getResources().getStringArray(R.array.common_password_business_keys);
            String[] stringArray2 = getResources().getStringArray(R.array.common_password_business_names);
            String[] stringArray3 = getResources().getStringArray(R.array.common_password_business_clickable_default);
            for (int i = 0; i < stringArray.length; i++) {
                if ("fingerprint_apply_to_privacy_password".equals(stringArray[i]) || "miui_keyguard".equals(stringArray[i]) || (!Build.IS_TABLET && "com_miui_applicatinlock_use_fingerprint_state".equals(stringArray[i]))) {
                    if (i >= stringArray2.length || i >= stringArray3.length) {
                        return;
                    }
                    addApplyFingerprintItem(stringArray[i], stringArray2[i], Boolean.parseBoolean(stringArray3[i]));
                }
            }
        }

        private void updateFingerprintList() {
            this.mFingerlistIds = new FingerprintHelper(getActivity()).getFingerprintIds();
            sortFingerprintList();
            Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.FingerprintManageSetting.FingerprintManageFragment.5
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(androidx.preference.Preference preference) {
                    String key = preference.getKey();
                    String str = (String) preference.getTitle();
                    if ("add_fingerprint".equals(key)) {
                        FingerprintManageFragment.this.addFingerprint();
                        return true;
                    }
                    FingerprintManageFragment.this.showFingerprintDetails(key, str);
                    return true;
                }
            };
            this.mFingerprintList.removeAll();
            for (int i = 0; i < this.mFingerlistIds.size(); i++) {
                String str = this.mFingerlistIds.get(i);
                androidx.preference.Preference preference = new androidx.preference.Preference(getPreferenceManager().getContext());
                preference.setKey(str);
                String fingerprintName = FingerprintUtils.getFingerprintName(getActivity(), str);
                if (TextUtils.isEmpty(fingerprintName)) {
                    fingerprintName = FingerprintUtils.generateFingerprintName(getActivity(), this.mFingerlistIds);
                    FingerprintUtils.setFingerprintName(getActivity(), str, fingerprintName);
                }
                preference.setTitle(fingerprintName);
                preference.setOnPreferenceClickListener(onPreferenceClickListener);
                this.mFingerprintList.addPreference(preference);
            }
            AlwaysClickablePreference alwaysClickablePreference = new AlwaysClickablePreference(getPreferenceManager().getContext());
            alwaysClickablePreference.setKey("add_fingerprint");
            alwaysClickablePreference.setTitle(R.string.enrol_fingerprint_data);
            alwaysClickablePreference.setOnPreferenceClickListener(onPreferenceClickListener);
            this.mFingerprintList.addPreference(alwaysClickablePreference);
            List<String> list = this.mFingerlistIds;
            if (list == null || list.size() < 5) {
                alwaysClickablePreference.setEnabled(true);
            } else {
                alwaysClickablePreference.setEnabled(false);
            }
        }

        private void updateFingerprintUnlockTypePreference(DropDownPreference dropDownPreference) {
            dropDownPreference.setValueIndex(getFingerprintUnlockType());
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public String getName() {
            return FingerprintManageFragment.class.getName();
        }

        @Override // com.android.settings.KeyguardSettingsPreferenceFragment
        public View inflateCustomizeView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            View inflate = layoutInflater.inflate(R.layout.fingerprint_manage_setting_layout, viewGroup, false);
            this.mFingerprintTipsForCts = (TextView) inflate.findViewById(R.id.fingerprint_hint);
            if (RestrictedLockUtilsCompat.checkIfKeyguardFeaturesDisabled(getActivity(), 32, UserHandle.myUserId()) != null) {
                showFingerprintTipsForCts();
            } else {
                this.mFingerprintTipsForCts.setVisibility(8);
            }
            return inflate;
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            List<String> list;
            super.onActivityResult(i, i2, intent);
            if (i == 1097 && i2 == -1 && (list = this.mFingerlistIds) != null && list.size() == 0) {
                finish();
            }
        }

        @Override // com.android.settings.OnBackPressedListener
        public boolean onBackPressed() {
            if (TabletUtils.IS_TABLET) {
                FragmentManager fragmentManager = getFragmentManager();
                while (fragmentManager.getBackStackEntryCount() > 1) {
                    fragmentManager.popBackStackImmediate();
                }
                return true;
            }
            return false;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.fingerprint_manage);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            this.mFingerprintList = (PreferenceCategory) preferenceScreen.findPreference("fingerprint_list");
            this.mApplyFingerprintCategory = (PreferenceCategory) preferenceScreen.findPreference("apply_fingerprint_to");
            updateApplyCategories();
            if (MiuiGxzwUtils.isSupportQuickOpen()) {
                androidx.preference.Preference preference = new androidx.preference.Preference(getPreferenceManager().getContext());
                preference.setTitle(R.string.gxzw_quick_open_title);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.FingerprintManageSetting.FingerprintManageFragment.1
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(androidx.preference.Preference preference2) {
                        FingerprintManageFragment fingerprintManageFragment = FingerprintManageFragment.this;
                        fingerprintManageFragment.startFragment(fingerprintManageFragment, "com.android.settings.MiuiGxzwQuickOpenFragment", 0, (Bundle) null, R.string.gxzw_quick_open_title);
                        return true;
                    }
                });
                this.mApplyFingerprintCategory.addPreference(preference);
            }
            if (!Build.IS_TABLET) {
                PackageManager packageManager = getActivity().getPackageManager();
                Intent intent = new Intent("com.miui.action.MANAGE_FINGERPRINT_PAYMENT");
                List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
                if (queryIntentActivities != null && queryIntentActivities.size() > 0) {
                    androidx.preference.Preference preference2 = new androidx.preference.Preference(getPreferenceManager().getContext());
                    preference2.setTitle(R.string.fingerprint_payment);
                    preference2.setIntent(intent);
                    this.mApplyFingerprintCategory.addPreference(preference2);
                }
            }
            PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("fingerprint_others");
            if (MiuiGxzwUtils.isGxzwSensor()) {
                androidx.preference.Preference findPreference = preferenceCategory.findPreference("gxzw_anim");
                if (MiuiGxzwUtils.isLargeFod() || Build.IS_MIUI_LITE_VERSION) {
                    preferenceCategory.removePreference(findPreference);
                } else {
                    findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.FingerprintManageSetting.FingerprintManageFragment.2
                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public boolean onPreferenceClick(androidx.preference.Preference preference3) {
                            FingerprintManageFragment.this.startActivity(new Intent(FingerprintManageFragment.this.getActivity(), MiuiGxzwAnimSettingsInternalActivity.class));
                            return true;
                        }
                    });
                }
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preferenceScreen.findPreference("gxzw_lowlight_open");
                if (MiuiGxzwUtils.isSupportLowlight()) {
                    checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.FingerprintManageSetting.FingerprintManageFragment.3
                        @Override // androidx.preference.Preference.OnPreferenceChangeListener
                        public boolean onPreferenceChange(androidx.preference.Preference preference3, Object obj) {
                            FingerprintManageFragment.this.setFodAodLowlightShowEnable(((Boolean) obj).booleanValue());
                            return true;
                        }
                    });
                    checkBoxPreference.setChecked(isFodAodLowlightShowEnable());
                } else {
                    preferenceCategory.removePreference(checkBoxPreference);
                }
                CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) preferenceScreen.findPreference("gxzw_aod_show");
                checkBoxPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.FingerprintManageSetting.FingerprintManageFragment.4
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public boolean onPreferenceChange(androidx.preference.Preference preference3, Object obj) {
                        FingerprintManageFragment.this.setFodAodShowEnable(((Boolean) obj).booleanValue());
                        return true;
                    }
                });
                checkBoxPreference2.setChecked(isFodAodShowEnable());
            } else {
                preferenceScreen.removePreference(preferenceCategory);
            }
            setupUnlockTypeCategory(preferenceScreen);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            Toast toast = this.mToast;
            if (toast != null) {
                toast.cancel();
                this.mToast = null;
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            List<String> list;
            List<String> list2;
            super.onResume();
            updateFingerprintList();
            int activePasswordQuality = new MiuiLockPatternUtils(getActivity()).getActivePasswordQuality(UserHandle.myUserId());
            if (activePasswordQuality == 0 || ((list = this.mFingerlistIds) != null && list.size() == 0)) {
                checkedFingerprintAllItemState(false);
            } else if (activePasswordQuality == 0 || (list2 = this.mFingerlistIds) == null || list2.size() == 0) {
            } else {
                checkedFingerprintAllItemState(true);
            }
        }
    }

    public static String getExtraFragmentName() {
        return FingerprintManageFragment.class.getName();
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", FingerprintManageFragment.class.getName());
        intent.putExtra(":settings:show_fragment_title_resid", R.string.privacy_password_use_finger_dialog_title);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return true;
    }

    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.privacy_password_use_finger_dialog_title);
    }
}
