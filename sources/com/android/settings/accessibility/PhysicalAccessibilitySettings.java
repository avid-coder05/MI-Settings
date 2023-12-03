package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityShortcutInfo;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.content.PackageMonitor;
import com.android.settings.MiuiValuePreference;
import com.android.settings.R;
import com.android.settings.cloud.AccessibilityDisableList;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.recommend.PageIndexManager;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.FunctionColumns;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.accessibility.AccessibilityUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/* loaded from: classes.dex */
public class PhysicalAccessibilitySettings extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    private static final String[] CATEGORIES = {"interaction_control_category", "user_installed_services_category"};
    public static List<String> HIDE_SERVICES_LIST;
    public static List<String> HIDE_SERVICES_PACKAGE_LIST;
    public static List<Integer> PRE_CONFIGED_SERVICES_LIST;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER;
    private MiuiValuePreference mAccessibilityVoiceAccess;
    private final SettingsContentObserver mSettingsContentObserver;
    private SwitchPreference mToggleLargePointerIconPreference;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateRunnable = new Runnable() { // from class: com.android.settings.accessibility.PhysicalAccessibilitySettings.1
        @Override // java.lang.Runnable
        public void run() {
            if (PhysicalAccessibilitySettings.this.getActivity() != null) {
                PhysicalAccessibilitySettings.this.updateServicePreferences();
            }
        }
    };
    private final BroadcastReceiver mUpdateVoiceAccess = new BroadcastReceiver() { // from class: com.android.settings.accessibility.PhysicalAccessibilitySettings.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null || !action.equals("ACTION_UPDATE_BTN") || PhysicalAccessibilitySettings.this.mAccessibilityVoiceAccess == null) {
                return;
            }
            PhysicalAccessibilitySettings.this.mAccessibilityVoiceAccess.setSummary(PhysicalAccessibilitySettings.this.getPrefContext().getResources().getString(R.string.accessibility_feature_state_off));
        }
    };
    private final PackageMonitor mSettingsPackageMonitor = new PackageMonitor() { // from class: com.android.settings.accessibility.PhysicalAccessibilitySettings.3
        private void sendUpdate() {
            PhysicalAccessibilitySettings.this.mHandler.postDelayed(PhysicalAccessibilitySettings.this.mUpdateRunnable, 1000L);
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
    private final Map<String, PreferenceCategory> mCategoryToPrefCategoryMap = new ArrayMap();
    private final Map<Preference, PreferenceCategory> mServicePreferenceToPreferenceCategoryMap = new ArrayMap();
    private final Map<ComponentName, PreferenceCategory> mPreBundledServiceComponentToCategoryMap = new ArrayMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class RestrictedPreferenceHelper {
        private final List<ComponentName> mConfigedServiceList;
        private final Context mContext;
        private final DevicePolicyManager mDpm;
        private final PackageManager mPm;

        RestrictedPreferenceHelper(Context context) {
            this.mContext = context;
            this.mDpm = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
            this.mPm = context.getPackageManager();
            this.mConfigedServiceList = getConfigedServices(context);
        }

        private RestrictedPreference createCustomRestrictedPreference(String str, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, String str2) {
            CustomRestrictedPreference customRestrictedPreference = new CustomRestrictedPreference(this.mContext);
            customRestrictedPreference.setKey(str);
            customRestrictedPreference.setTitle(charSequence);
            customRestrictedPreference.setSummary(this.mContext.getString(R.string.accessibility_summary_source, charSequence2));
            customRestrictedPreference.setFragment(str2);
            customRestrictedPreference.setValue(charSequence3);
            customRestrictedPreference.setPersistent(false);
            return customRestrictedPreference;
        }

        private RestrictedPreference createRestrictedPreference(String str, CharSequence charSequence, CharSequence charSequence2, String str2) {
            Log.d("PhysicalAccessibilitySettings", "createRestrictedPreference: title=" + ((Object) charSequence) + ",fragment=" + str2);
            RestrictedPreference restrictedPreference = new RestrictedPreference(this.mContext);
            restrictedPreference.setKey(str);
            restrictedPreference.setTitle(charSequence);
            restrictedPreference.setFragment(str2);
            restrictedPreference.setSummary(charSequence2);
            restrictedPreference.setPersistent(false);
            restrictedPreference.setOrder(-1);
            return restrictedPreference;
        }

        private String getAccessibilityServiceFragmentTypeName(AccessibilityServiceInfo accessibilityServiceInfo) {
            return AccessibilityServiceUtils.getAccessibilityServiceFragmentTypeName(accessibilityServiceInfo);
        }

        public static List<ComponentName> getConfigedServices(Context context) {
            ArrayList arrayList = new ArrayList();
            Iterator<Integer> it = AccessibilitySettings.PRE_CONFIGED_SERVICES_LIST.iterator();
            while (it.hasNext()) {
                for (String str : context.getResources().getStringArray(it.next().intValue())) {
                    arrayList.add(ComponentName.unflattenFromString(str));
                }
            }
            return arrayList;
        }

        private void putBasicExtras(RestrictedPreference restrictedPreference, String str, CharSequence charSequence, CharSequence charSequence2, int i, String str2, ComponentName componentName) {
            Bundle extras = restrictedPreference.getExtras();
            extras.putString("preference_key", str);
            extras.putCharSequence("title", charSequence);
            extras.putCharSequence(FunctionColumns.SUMMARY, charSequence2);
            extras.putParcelable("component_name", componentName);
            extras.putInt("animated_image_res", i);
            extras.putString("html_description", str2);
        }

        private void putServiceExtras(RestrictedPreference restrictedPreference, ResolveInfo resolveInfo, Boolean bool) {
            Bundle extras = restrictedPreference.getExtras();
            extras.putParcelable("resolve_info", resolveInfo);
            extras.putBoolean("checked", bool.booleanValue());
        }

        private void putSettingsExtras(RestrictedPreference restrictedPreference, String str, String str2) {
            Bundle extras = restrictedPreference.getExtras();
            if (TextUtils.isEmpty(str2)) {
                return;
            }
            extras.putString("settings_title", this.mContext.getText(R.string.accessibility_menu_item_settings).toString());
            extras.putString("settings_component_name", new ComponentName(str, str2).flattenToString());
        }

        private void setRestrictedPreferenceEnabled(RestrictedPreference restrictedPreference, String str, boolean z, boolean z2) {
            if (z || z2) {
                restrictedPreference.setEnabled(true);
                return;
            }
            RestrictedLockUtils.EnforcedAdmin checkIfAccessibilityServiceDisallowed = RestrictedLockUtilsInternal.checkIfAccessibilityServiceDisallowed(this.mContext, str, UserHandle.myUserId());
            if (checkIfAccessibilityServiceDisallowed != null) {
                restrictedPreference.setDisabledByAdmin(checkIfAccessibilityServiceDisallowed);
            } else {
                restrictedPreference.setEnabled(false);
            }
        }

        List<RestrictedPreference> createAccessibilityActivityPreferenceList(List<AccessibilityShortcutInfo> list, boolean z) {
            Set<ComponentName> set;
            List list2;
            Set<ComponentName> enabledServicesFromSettings = AccessibilityUtils.getEnabledServicesFromSettings(this.mContext);
            List permittedAccessibilityServices = this.mDpm.getPermittedAccessibilityServices(UserHandle.myUserId());
            int size = list.size();
            ArrayList arrayList = new ArrayList(size);
            int i = 0;
            while (i < size) {
                AccessibilityShortcutInfo accessibilityShortcutInfo = list.get(i);
                ActivityInfo activityInfo = accessibilityShortcutInfo.getActivityInfo();
                ComponentName componentName = accessibilityShortcutInfo.getComponentName();
                String str = activityInfo.packageName;
                if (!PhysicalAccessibilitySettings.isHideServices(this.mContext, str, activityInfo.name) && (z ? !(VisualAccessibilitySettings.COMMON_SERVICES_LIST.contains(str) || this.mConfigedServiceList.contains(componentName)) : VisualAccessibilitySettings.COMMON_SERVICES_LIST.contains(str))) {
                    String flattenToString = componentName.flattenToString();
                    CharSequence loadLabel = activityInfo.loadLabel(this.mPm);
                    RestrictedPreference createRestrictedPreference = createRestrictedPreference(flattenToString, loadLabel, accessibilityShortcutInfo.loadSummary(this.mPm), LaunchAccessibilityActivityPreferenceFragment.class.getName());
                    setRestrictedPreferenceEnabled(createRestrictedPreference, str, permittedAccessibilityServices == null || permittedAccessibilityServices.contains(str), enabledServicesFromSettings.contains(componentName));
                    String key = createRestrictedPreference.getKey();
                    String loadDescription = accessibilityShortcutInfo.loadDescription(this.mPm);
                    int animatedImageRes = accessibilityShortcutInfo.getAnimatedImageRes();
                    String loadHtmlDescription = accessibilityShortcutInfo.loadHtmlDescription(this.mPm);
                    String settingsActivityName = accessibilityShortcutInfo.getSettingsActivityName();
                    set = enabledServicesFromSettings;
                    list2 = permittedAccessibilityServices;
                    putBasicExtras(createRestrictedPreference, key, loadLabel, loadDescription, animatedImageRes, loadHtmlDescription, componentName);
                    putSettingsExtras(createRestrictedPreference, str, settingsActivityName);
                    arrayList.add(createRestrictedPreference);
                } else {
                    set = enabledServicesFromSettings;
                    list2 = permittedAccessibilityServices;
                }
                i++;
                permittedAccessibilityServices = list2;
                enabledServicesFromSettings = set;
            }
            return arrayList;
        }

        List<RestrictedPreference> createAccessibilityServicePreferenceList(List<AccessibilityServiceInfo> list, boolean z) {
            Set<ComponentName> set;
            boolean z2;
            CharSequence charSequence;
            int i;
            String str;
            RestrictedPreference createCustomRestrictedPreference;
            List list2;
            Set<ComponentName> enabledServicesFromSettings = AccessibilityUtils.getEnabledServicesFromSettings(this.mContext);
            List permittedAccessibilityServices = this.mDpm.getPermittedAccessibilityServices(UserHandle.myUserId());
            int size = list.size();
            ArrayList arrayList = new ArrayList(size);
            int i2 = 0;
            while (i2 < size) {
                AccessibilityServiceInfo accessibilityServiceInfo = list.get(i2);
                ResolveInfo resolveInfo = accessibilityServiceInfo.getResolveInfo();
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                String str2 = serviceInfo.packageName;
                ComponentName componentName = new ComponentName(str2, resolveInfo.serviceInfo.name);
                if (PhysicalAccessibilitySettings.isHideServices(this.mContext, str2, serviceInfo.name) || VisualAccessibilitySettings.COMMON_SERVICES_LIST.contains(str2) || (z ? this.mConfigedServiceList.contains(componentName) : !this.mConfigedServiceList.contains(componentName))) {
                    set = enabledServicesFromSettings;
                    list2 = permittedAccessibilityServices;
                    i = size;
                } else {
                    String flattenToString = componentName.flattenToString();
                    CharSequence loadLabel = resolveInfo.loadLabel(this.mPm);
                    boolean contains = enabledServicesFromSettings.contains(componentName);
                    CharSequence serviceSummary = PhysicalAccessibilitySettings.getServiceSummary(this.mContext, accessibilityServiceInfo, contains);
                    String accessibilityServiceFragmentTypeName = getAccessibilityServiceFragmentTypeName(accessibilityServiceInfo);
                    String charSequence2 = this.mPm.getApplicationLabel(resolveInfo.serviceInfo.applicationInfo).toString();
                    if (z) {
                        set = enabledServicesFromSettings;
                        z2 = contains;
                        charSequence = loadLabel;
                        i = size;
                        str = str2;
                        createCustomRestrictedPreference = createCustomRestrictedPreference(flattenToString, loadLabel, charSequence2, serviceSummary, accessibilityServiceFragmentTypeName);
                    } else {
                        createCustomRestrictedPreference = createRestrictedPreference(flattenToString, loadLabel, serviceSummary, accessibilityServiceFragmentTypeName);
                        set = enabledServicesFromSettings;
                        i = size;
                        z2 = contains;
                        charSequence = loadLabel;
                        str = str2;
                    }
                    setRestrictedPreferenceEnabled(createCustomRestrictedPreference, str, permittedAccessibilityServices == null || permittedAccessibilityServices.contains(str), z2);
                    String key = createCustomRestrictedPreference.getKey();
                    int animatedImageRes = accessibilityServiceInfo.getAnimatedImageRes();
                    CharSequence serviceDescription = PhysicalAccessibilitySettings.getServiceDescription(this.mContext, accessibilityServiceInfo, z2);
                    String loadHtmlDescription = accessibilityServiceInfo.loadHtmlDescription(this.mPm);
                    String settingsActivityName = accessibilityServiceInfo.getSettingsActivityName();
                    CharSequence charSequence3 = charSequence;
                    list2 = permittedAccessibilityServices;
                    putBasicExtras(createCustomRestrictedPreference, key, charSequence3, serviceDescription, animatedImageRes, loadHtmlDescription, componentName);
                    putServiceExtras(createCustomRestrictedPreference, resolveInfo, Boolean.valueOf(z2));
                    putSettingsExtras(createCustomRestrictedPreference, str, settingsActivityName);
                    arrayList.add(createCustomRestrictedPreference);
                }
                i2++;
                permittedAccessibilityServices = list2;
                size = i;
                enabledServicesFromSettings = set;
            }
            return arrayList;
        }
    }

    static {
        ArrayList arrayList = new ArrayList();
        HIDE_SERVICES_PACKAGE_LIST = arrayList;
        arrayList.add(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME);
        HIDE_SERVICES_PACKAGE_LIST.add("com.miui.personalassistant");
        HIDE_SERVICES_PACKAGE_LIST.add("com.xiaomi.misettings");
        ArrayList arrayList2 = new ArrayList();
        HIDE_SERVICES_LIST = arrayList2;
        arrayList2.add("com.google.android.accessibility.accessibilitymenu.AccessibilityMenuService");
        HIDE_SERVICES_LIST.add("com.google.android.marvin.talkback.TalkBackService");
        HIDE_SERVICES_LIST.add("com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService");
        HIDE_SERVICES_LIST.add("com.google.android.accessibility.selecttospeak.SelectToSpeakService");
        ArrayList arrayList3 = new ArrayList();
        PRE_CONFIGED_SERVICES_LIST = arrayList3;
        arrayList3.add(Integer.valueOf(R.array.config_preinstalled_screen_reader_services));
        PRE_CONFIGED_SERVICES_LIST.add(Integer.valueOf(R.array.config_preinstalled_display_services));
        PRE_CONFIGED_SERVICES_LIST.add(Integer.valueOf(R.array.config_preinstalled_interaction_control_services));
        PRE_CONFIGED_SERVICES_LIST.add(Integer.valueOf(R.array.config_downloaded_services));
        SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.accessibility_settings_physical);
    }

    public PhysicalAccessibilitySettings() {
        Collection values = AccessibilityShortcutController.getFrameworkShortcutFeaturesMap().values();
        ArrayList arrayList = new ArrayList(values.size());
        Iterator it = values.iterator();
        while (it.hasNext()) {
            arrayList.add(((AccessibilityShortcutController.ToggleableFrameworkFeatureInfo) it.next()).getSettingKey());
        }
        arrayList.add("accessibility_button_targets");
        arrayList.add("accessibility_shortcut_target_service");
        this.mSettingsContentObserver = new SettingsContentObserver(this.mHandler, arrayList) { // from class: com.android.settings.accessibility.PhysicalAccessibilitySettings.4
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                PhysicalAccessibilitySettings.this.updateAllPreferences();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean containsTargetNameInList(Context context, List<AccessibilityShortcutInfo> list, AccessibilityServiceInfo accessibilityServiceInfo) {
        ServiceInfo serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo;
        String str = serviceInfo.packageName;
        CharSequence loadLabel = serviceInfo.loadLabel(context.getPackageManager());
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ActivityInfo activityInfo = list.get(i).getActivityInfo();
            String str2 = activityInfo.packageName;
            CharSequence loadLabel2 = activityInfo.loadLabel(context.getPackageManager());
            if (str.equals(str2) && loadLabel.equals(loadLabel2)) {
                return true;
            }
        }
        return false;
    }

    public static List<RestrictedPreference> getInstalledAccessibilityList(final Context context, boolean z) {
        AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(context);
        RestrictedPreferenceHelper restrictedPreferenceHelper = new RestrictedPreferenceHelper(context);
        final List<AccessibilityShortcutInfo> installedAccessibilityShortcutListAsUser = accessibilityManager.getInstalledAccessibilityShortcutListAsUser(context, UserHandle.myUserId());
        ArrayList arrayList = new ArrayList(accessibilityManager.getInstalledAccessibilityServiceList());
        arrayList.removeIf(new Predicate() { // from class: com.android.settings.accessibility.PhysicalAccessibilitySettings$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean containsTargetNameInList;
                containsTargetNameInList = PhysicalAccessibilitySettings.containsTargetNameInList(context, installedAccessibilityShortcutListAsUser, (AccessibilityServiceInfo) obj);
                return containsTargetNameInList;
            }
        });
        List<RestrictedPreference> createAccessibilityActivityPreferenceList = restrictedPreferenceHelper.createAccessibilityActivityPreferenceList(installedAccessibilityShortcutListAsUser, z);
        List<RestrictedPreference> createAccessibilityServicePreferenceList = restrictedPreferenceHelper.createAccessibilityServicePreferenceList(arrayList, z);
        ArrayList arrayList2 = new ArrayList();
        arrayList2.addAll(createAccessibilityActivityPreferenceList);
        arrayList2.addAll(createAccessibilityServicePreferenceList);
        return arrayList2;
    }

    static CharSequence getServiceDescription(Context context, AccessibilityServiceInfo accessibilityServiceInfo, boolean z) {
        return AccessibilityServiceUtils.getServiceDescription(context, accessibilityServiceInfo, z);
    }

    static CharSequence getServiceSummary(Context context, AccessibilityServiceInfo accessibilityServiceInfo, boolean z) {
        CharSequence text;
        if (z && accessibilityServiceInfo.crashed) {
            return context.getText(R.string.accessibility_summary_state_stopped);
        }
        if (AccessibilityUtil.getAccessibilityServiceFragmentType(accessibilityServiceInfo) == 1) {
            text = AccessibilityUtil.getUserShortcutTypesFromSettings(context, new ComponentName(accessibilityServiceInfo.getResolveInfo().serviceInfo.packageName, accessibilityServiceInfo.getResolveInfo().serviceInfo.name)) != 0 ? context.getText(R.string.accessibility_summary_shortcut_enabled) : context.getText(R.string.accessibility_summary_shortcut_disabled);
        } else {
            text = z ? context.getText(R.string.accessibility_summary_state_enabled) : context.getText(R.string.accessibility_summary_state_disabled);
        }
        CharSequence loadSummary = accessibilityServiceInfo.loadSummary(context.getPackageManager());
        return TextUtils.isEmpty(loadSummary) ? text : context.getString(R.string.preference_summary_default_combination, text, loadSummary);
    }

    private void handleToggleLargePointerIconPreferenceClick(boolean z) {
        Settings.Secure.putInt(getContentResolver(), "accessibility_large_pointer_icon", z ? 1 : 0);
    }

    private void initPreferenceChangeListener() {
        SwitchPreference switchPreference = this.mToggleLargePointerIconPreference;
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener(this);
        }
    }

    private void initializeAllPreferences() {
        int i = 0;
        while (true) {
            String[] strArr = CATEGORIES;
            if (i >= strArr.length) {
                this.mToggleLargePointerIconPreference = (SwitchPreference) findPreference("toggle_large_pointer_icon");
                this.mAccessibilityVoiceAccess = (MiuiValuePreference) findPreference("accessibility_voice_access");
                initPreferenceChangeListener();
                return;
            }
            this.mCategoryToPrefCategoryMap.put(strArr[i], (PreferenceCategory) findPreference(strArr[i]));
            i++;
        }
    }

    private void initializePreBundledServicesMapFromArray(String str, int i) {
        String[] stringArray = getResources().getStringArray(i);
        PreferenceCategory preferenceCategory = this.mCategoryToPrefCategoryMap.get(str);
        for (String str2 : stringArray) {
            this.mPreBundledServiceComponentToCategoryMap.put(ComponentName.unflattenFromString(str2), preferenceCategory);
        }
    }

    public static boolean isHideServices(Context context, String str, String str2) {
        Set<String> cacheDisableSet = AccessibilityDisableList.getCacheDisableSet(context);
        cacheDisableSet.add("com.miui.voiceassist");
        return HIDE_SERVICES_PACKAGE_LIST.contains(str) || cacheDisableSet.contains(str) || HIDE_SERVICES_LIST.contains(str2);
    }

    private void releasePreferenceChangeListener() {
        SwitchPreference switchPreference = this.mToggleLargePointerIconPreference;
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAllPreferences() {
        updateServicePreferences();
    }

    private void updateCategoryOrderFromArray(String str, int i) {
        String[] stringArray = getResources().getStringArray(i);
        PreferenceCategory preferenceCategory = this.mCategoryToPrefCategoryMap.get(str);
        int preferenceCount = preferenceCategory.getPreferenceCount();
        int length = stringArray.length;
        for (int i2 = 0; i2 < preferenceCount; i2++) {
            int i3 = 0;
            while (true) {
                if (i3 >= length) {
                    break;
                } else if (preferenceCategory.getPreference(i2).getKey().equals(stringArray[i3])) {
                    preferenceCategory.getPreference(i2).setOrder(i3);
                    break;
                } else {
                    i3++;
                }
            }
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_uri_accessibility;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PhysicalAccessibilitySettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 2;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return PhysicalAccessibilitySettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getPageIndex() {
        return PageIndexManager.PAGE_ACCESSIBILITY_PHYSICAL;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.accessibility_settings_physical;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initializeAllPreferences();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mHandler.removeMessages(0);
        super.onDestroy();
        releasePreferenceChangeListener();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mToggleLargePointerIconPreference == preference && (obj instanceof Boolean)) {
            handleToggleLargePointerIconPreferenceClick(((Boolean) obj).booleanValue());
            return true;
        }
        return false;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        updateAllPreferences();
        this.mSettingsPackageMonitor.register(getActivity(), getActivity().getMainLooper(), false);
        this.mSettingsContentObserver.register(getContentResolver());
        getPrefContext().registerReceiver(this.mUpdateVoiceAccess, new IntentFilter("ACTION_UPDATE_BTN"));
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        this.mSettingsPackageMonitor.unregister();
        this.mSettingsContentObserver.unregister(getContentResolver());
        getPrefContext().unregisterReceiver(this.mUpdateVoiceAccess);
        super.onStop();
    }

    protected void updateServicePreferences() {
        ArrayList arrayList = new ArrayList(this.mServicePreferenceToPreferenceCategoryMap.keySet());
        for (int i = 0; i < arrayList.size(); i++) {
            Preference preference = (Preference) arrayList.get(i);
            this.mServicePreferenceToPreferenceCategoryMap.get(preference).removePreference(preference);
        }
        initializePreBundledServicesMapFromArray("user_installed_services_category", R.array.config_downloaded_services);
        initializePreBundledServicesMapFromArray("interaction_control_category", R.array.config_preinstalled_interaction_control_services);
        List<RestrictedPreference> installedAccessibilityList = getInstalledAccessibilityList(getPrefContext(), false);
        PreferenceCategory preferenceCategory = this.mCategoryToPrefCategoryMap.get("user_installed_services_category");
        int size = installedAccessibilityList.size();
        for (int i2 = 0; i2 < size; i2++) {
            RestrictedPreference restrictedPreference = installedAccessibilityList.get(i2);
            ComponentName componentName = (ComponentName) restrictedPreference.getExtras().getParcelable("component_name");
            PreferenceCategory preferenceCategory2 = this.mPreBundledServiceComponentToCategoryMap.containsKey(componentName) ? this.mPreBundledServiceComponentToCategoryMap.get(componentName) : preferenceCategory;
            preferenceCategory2.addPreference(restrictedPreference);
            this.mServicePreferenceToPreferenceCategoryMap.put(restrictedPreference, preferenceCategory2);
        }
        updateCategoryOrderFromArray("interaction_control_category", R.array.config_order_interaction_control_services);
        if (preferenceCategory.getPreferenceCount() == 0) {
            getPreferenceScreen().removePreference(preferenceCategory);
        } else {
            getPreferenceScreen().addPreference(preferenceCategory);
        }
    }
}
