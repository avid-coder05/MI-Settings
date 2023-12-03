package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.content.PackageMonitor;
import com.android.settings.R;
import com.android.settings.accessibility.haptic.HapticRadioButtonPreference;
import com.android.settings.accessibility.utils.MiuiAccessibilityUtils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.FunctionColumns;
import com.android.settings.widget.CustomCheckBoxPreference;
import com.android.settingslib.accessibility.AccessibilityUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class HapticSettingsFragment extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    public static List<String> COMMON_SERVICES_LIST;
    private CheckBoxPreference mAccessibilityScreenReader;
    private String mChangePreferenceKey;
    private Preference mHapticExperienceDetailsPreference;
    private Preference mHapticExperiencePreference;
    private PackageManager mPm;
    private CustomCheckBoxPreference mScreenReaderHapticPreference;
    private final SettingsContentObserver mSettingsContentObserver;
    private SharedPreferences mSharedPrefs;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateRunnable = new Runnable() { // from class: com.android.settings.accessibility.HapticSettingsFragment.2
        @Override // java.lang.Runnable
        public void run() {
            if (HapticSettingsFragment.this.getActivity() != null) {
                HapticSettingsFragment.this.updateServicePreferences();
            }
        }
    };
    private final PackageMonitor mSettingsPackageMonitor = new PackageMonitor() { // from class: com.android.settings.accessibility.HapticSettingsFragment.3
        private void sendUpdate() {
            HapticSettingsFragment.this.mHandler.postDelayed(HapticSettingsFragment.this.mUpdateRunnable, 1000L);
        }

        public void onPackageAdded(String str, int i) {
            sendUpdate();
        }

        public void onPackageAppeared(String str, int i) {
            sendUpdate();
        }

        public void onPackageDisappeared(String str, int i) {
            sendUpdate();
        }

        public void onPackageRemoved(String str, int i) {
            sendUpdate();
        }
    };
    View.AccessibilityDelegate mDelegate = new View.AccessibilityDelegate() { // from class: com.android.settings.accessibility.HapticSettingsFragment.6
        @Override // android.view.View.AccessibilityDelegate
        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            if (TextUtils.isEmpty(HapticSettingsFragment.this.mChangePreferenceKey) || i != 64) {
                return super.performAccessibilityAction(view, i, bundle);
            }
            View view2 = ((CustomCheckBoxPreference) HapticSettingsFragment.this.findPreference("accessibility_screen_reader_haptic")).getView();
            if (view2 != null) {
                view2.sendAccessibilityEvent(8);
            }
            HapticSettingsFragment.this.mChangePreferenceKey = "";
            return true;
        }
    };

    static {
        ArrayList arrayList = new ArrayList();
        COMMON_SERVICES_LIST = arrayList;
        arrayList.add("com.bjbyhd.voiceback/com.bjbyhd.voiceback.BoyhoodVoiceBackService");
        COMMON_SERVICES_LIST.add("com.android.tback/net.tatans.soundback.SoundBackService");
        COMMON_SERVICES_LIST.add("com.nirenr.talkman/com.nirenr.talkman.TalkManAccessibilityService");
        COMMON_SERVICES_LIST.add("com.dianming.phoneapp/com.dianming.phoneapp.MyAccessibilityService");
        COMMON_SERVICES_LIST.add("com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService");
    }

    public HapticSettingsFragment() {
        Collection values = AccessibilityShortcutController.getFrameworkShortcutFeaturesMap().values();
        ArrayList arrayList = new ArrayList(values.size());
        Iterator it = values.iterator();
        while (it.hasNext()) {
            arrayList.add(((AccessibilityShortcutController.ToggleableFrameworkFeatureInfo) it.next()).getSettingKey());
        }
        arrayList.add("accessibility_button_targets");
        arrayList.add("accessibility_shortcut_target_service");
        this.mSettingsContentObserver = new SettingsContentObserver(this.mHandler, arrayList) { // from class: com.android.settings.accessibility.HapticSettingsFragment.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                HapticSettingsFragment.this.updateAllPreferences();
            }
        };
    }

    static CharSequence getServiceSummary(Context context, AccessibilityServiceInfo accessibilityServiceInfo, boolean z) {
        if (z && accessibilityServiceInfo.crashed) {
            return context.getText(R.string.accessibility_summary_state_stopped);
        }
        CharSequence text = z ? context.getText(R.string.accessibility_summary_state_enabled) : context.getText(R.string.accessibility_summary_state_disabled);
        CharSequence loadSummary = accessibilityServiceInfo.loadSummary(context.getPackageManager());
        return TextUtils.isEmpty(loadSummary) ? text : context.getString(R.string.preference_summary_default_combination, text, loadSummary);
    }

    private void initScreenReader() {
        Iterator<String> it = COMMON_SERVICES_LIST.iterator();
        while (it.hasNext()) {
            Preference findPreference = findPreference(it.next());
            if (findPreference != null) {
                findPreference.setVisible(false);
            }
        }
        updateServicePreferences();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSystemPreferences$0() {
        this.mScreenReaderHapticPreference.setEnabled(MiuiAccessibilityUtils.isTallBackActive(getContext()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSystemPreferences$1() {
        this.mHapticExperienceDetailsPreference.setEnabled(MiuiAccessibilityUtils.isTallBackActive(getContext()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSystemPreferences$2() {
        this.mHapticExperiencePreference.setEnabled(MiuiAccessibilityUtils.isTallBackActive(getContext()));
    }

    private void putBasicExtras(Preference preference, String str, CharSequence charSequence, CharSequence charSequence2, int i, String str2, ComponentName componentName) {
        Bundle extras = preference.getExtras();
        extras.putString("preference_key", str);
        extras.putCharSequence("title", charSequence);
        extras.putCharSequence(FunctionColumns.SUMMARY, charSequence2);
        extras.putParcelable("component_name", componentName);
        extras.putInt("animated_image_res", i);
        extras.putString("html_description", str2);
    }

    private void putServiceExtras(Preference preference, ResolveInfo resolveInfo, Boolean bool) {
        Bundle extras = preference.getExtras();
        extras.putParcelable("resolve_info", resolveInfo);
        extras.putBoolean("checked", bool.booleanValue());
    }

    private void putSettingsExtras(Preference preference, String str, String str2) {
        Bundle extras = preference.getExtras();
        if (TextUtils.isEmpty(str2)) {
            return;
        }
        extras.putString("settings_title", getContext().getText(R.string.accessibility_menu_item_settings).toString());
        extras.putString("settings_component_name", new ComponentName(str, str2).flattenToString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAllPreferences() {
        updateSystemPreferences();
        updateServicePreferences();
    }

    private void updateSystemPreferences() {
        if (this.mAccessibilityScreenReader != null) {
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.accessibility.HapticSettingsFragment.4
                @Override // java.lang.Runnable
                public void run() {
                    if (MiuiAccessibilityUtils.isTallBackActive(HapticSettingsFragment.this.getContext())) {
                        HapticSettingsFragment.this.mSharedPrefs.edit().putInt(ScreenReaderController.IS_ACCESSIBILITY_SCREEN_READER_OPEN, 1).apply();
                        HapticSettingsFragment.this.mAccessibilityScreenReader.setChecked(true);
                    }
                }
            }, 500L);
        }
        if (this.mScreenReaderHapticPreference != null) {
            if (!isScreenReaderCheckboxOpen()) {
                this.mScreenReaderHapticPreference.setEnabled(false);
            }
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.accessibility.HapticSettingsFragment$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HapticSettingsFragment.this.lambda$updateSystemPreferences$0();
                }
            }, 500L);
        }
        if (this.mHapticExperienceDetailsPreference != null) {
            if (!isScreenReaderCheckboxOpen()) {
                this.mHapticExperienceDetailsPreference.setEnabled(false);
            }
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.accessibility.HapticSettingsFragment$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    HapticSettingsFragment.this.lambda$updateSystemPreferences$1();
                }
            }, 500L);
        }
        if (this.mHapticExperiencePreference != null) {
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.accessibility.HapticSettingsFragment$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    HapticSettingsFragment.this.lambda$updateSystemPreferences$2();
                }
            }, 500L);
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.accessibility.HapticSettingsFragment.5
            @Override // java.lang.Runnable
            public void run() {
                boolean isTallBackActive = MiuiAccessibilityUtils.isTallBackActive(HapticSettingsFragment.this.getContext());
                boolean isRemoveScreenReaderVibrator = MiuiAccessibilityUtils.isRemoveScreenReaderVibrator(HapticSettingsFragment.this.getContext());
                boolean isHapticOn = MiuiAccessibilityUtils.isHapticOn(HapticSettingsFragment.this.getContext());
                if (isTallBackActive && isRemoveScreenReaderVibrator && !isHapticOn) {
                    AccessibilityUtils.setAccessibilityServiceState(HapticSettingsFragment.this.getContext(), ComponentName.unflattenFromString("com.miui.accessibility/com.miui.accessibility.haptic.HapticAccessibilityService"), true);
                }
            }
        }, 300L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "HapticSettingsFragment";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.haptic_settings;
    }

    public boolean isScreenReaderCheckboxOpen() {
        return this.mSharedPrefs.getInt(ScreenReaderController.IS_ACCESSIBILITY_SCREEN_READER_OPEN, 0) == 1;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSharedPrefs = getContext().getSharedPreferences(ScreenReaderController.ACCESSIBILITY_SCREEN_READER_SP, 0);
        this.mPm = getPackageManager();
        this.mAccessibilityScreenReader = (CheckBoxPreference) findPreference("accessibility_screen_reader");
        CustomCheckBoxPreference customCheckBoxPreference = (CustomCheckBoxPreference) findPreference("accessibility_screen_reader_haptic");
        this.mScreenReaderHapticPreference = customCheckBoxPreference;
        customCheckBoxPreference.setOnPreferenceChangeListener(this);
        this.mHapticExperienceDetailsPreference = findPreference("haptic_experience_details");
        this.mHapticExperiencePreference = findPreference("haptic_experience");
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Boolean bool = (Boolean) obj;
        boolean booleanValue = bool.booleanValue();
        if (preference.getKey().equals("accessibility_screen_reader_haptic")) {
            AccessibilityUtils.setAccessibilityServiceState(getContext(), ComponentName.unflattenFromString("com.miui.accessibility/com.miui.accessibility.haptic.HapticAccessibilityService"), bool.booleanValue());
            if (booleanValue) {
                this.mChangePreferenceKey = preference.getKey();
            }
            this.mScreenReaderHapticPreference.requestFocusDelay();
            return true;
        }
        return false;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        for (String str : COMMON_SERVICES_LIST) {
            ((HapticRadioButtonPreference) findPreference(str)).setPreferenceState(MiuiAccessibilityUtils.isAccessibilityServiceOn(getContext(), ComponentName.unflattenFromString(str)));
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        initScreenReader();
        updateAllPreferences();
        this.mSettingsPackageMonitor.register(getActivity(), getActivity().getMainLooper(), false);
        this.mSettingsContentObserver.register(getContentResolver());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        this.mSettingsPackageMonitor.unregister();
        this.mSettingsContentObserver.unregister(getContentResolver());
        this.mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    protected void updateServicePreferences() {
        Set<ComponentName> set;
        Iterator<AccessibilityServiceInfo> it;
        AccessibilityServiceInfo accessibilityServiceInfo;
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = ((AccessibilityManager) getContext().getSystemService("accessibility")).getInstalledAccessibilityServiceList();
        if (installedAccessibilityServiceList == null) {
            return;
        }
        Set<ComponentName> enabledServicesFromSettings = AccessibilityUtils.getEnabledServicesFromSettings(getContext());
        Iterator<AccessibilityServiceInfo> it2 = installedAccessibilityServiceList.iterator();
        while (it2.hasNext()) {
            AccessibilityServiceInfo next = it2.next();
            ResolveInfo resolveInfo = next.getResolveInfo();
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
            String flattenToString = componentName.flattenToString();
            for (CharSequence charSequence : COMMON_SERVICES_LIST) {
                if (flattenToString.contains(charSequence)) {
                    HapticRadioButtonPreference hapticRadioButtonPreference = (HapticRadioButtonPreference) findPreference(charSequence);
                    CharSequence loadLabel = resolveInfo.loadLabel(this.mPm);
                    boolean contains = enabledServicesFromSettings.contains(componentName);
                    String str = resolveInfo.serviceInfo.packageName;
                    hapticRadioButtonPreference.setVisible(true);
                    hapticRadioButtonPreference.setTitle(loadLabel);
                    hapticRadioButtonPreference.setSummary(getServiceSummary(getContext(), next, contains));
                    hapticRadioButtonPreference.setPreferenceState(contains);
                    hapticRadioButtonPreference.setPersistent(false);
                    String accessibilityServiceFragmentTypeName = AccessibilityServiceUtils.getAccessibilityServiceFragmentTypeName(next);
                    int animatedImageRes = next.getAnimatedImageRes();
                    CharSequence serviceDescription = AccessibilityServiceUtils.getServiceDescription(getContext(), next, contains);
                    String loadHtmlDescription = next.loadHtmlDescription(this.mPm);
                    String settingsActivityName = next.getSettingsActivityName();
                    hapticRadioButtonPreference.setFragment(accessibilityServiceFragmentTypeName);
                    set = enabledServicesFromSettings;
                    it = it2;
                    accessibilityServiceInfo = next;
                    putBasicExtras(hapticRadioButtonPreference, flattenToString, loadLabel, serviceDescription, animatedImageRes, loadHtmlDescription, componentName);
                    putServiceExtras(hapticRadioButtonPreference, resolveInfo, Boolean.valueOf(contains));
                    putSettingsExtras(hapticRadioButtonPreference, str, settingsActivityName);
                } else {
                    set = enabledServicesFromSettings;
                    it = it2;
                    accessibilityServiceInfo = next;
                }
                next = accessibilityServiceInfo;
                enabledServicesFromSettings = set;
                it2 = it;
            }
        }
    }
}
