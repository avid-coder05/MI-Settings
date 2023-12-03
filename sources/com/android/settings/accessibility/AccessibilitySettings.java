package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityShortcutInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
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
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.content.PackageMonitor;
import com.android.settings.R;
import com.android.settings.cloud.AccessibilityDisableList;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.FunctionColumns;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/* loaded from: classes.dex */
public class AccessibilitySettings extends DashboardFragment {
    private static final String[] CATEGORIES = {"screen_reader_category", "captions_category", "audio_category", "display_category", "interaction_control_category", "user_installed_services_category"};
    public static List<String> COMMON_SERVICES_LIST;
    public static List<String> HIDE_SERVICES_LIST;
    public static List<String> HIDE_SERVICES_PACKAGE_LIST;
    public static Set<String> HIDE_SUMMARY_SERVICES_SET;
    public static List<Integer> PRE_CONFIGED_SERVICES_LIST;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER;
    final SettingsContentObserver mSettingsContentObserver;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateRunnable = new Runnable() { // from class: com.android.settings.accessibility.AccessibilitySettings.1
        @Override // java.lang.Runnable
        public void run() {
            if (AccessibilitySettings.this.getActivity() != null) {
                AccessibilitySettings.this.onContentChanged();
            }
        }
    };
    private final PackageMonitor mSettingsPackageMonitor = new PackageMonitor() { // from class: com.android.settings.accessibility.AccessibilitySettings.2
        private void sendUpdate() {
            AccessibilitySettings.this.mHandler.postDelayed(AccessibilitySettings.this.mUpdateRunnable, 1000L);
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
    private boolean mNeedPreferencesUpdate = false;
    private boolean mIsForeground = true;

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
            Log.d("AccessibilitySettings", "createRestrictedPreference: title=" + ((Object) charSequence) + ",fragment=" + str2);
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
                if (!AccessibilitySettings.isHideServices(this.mContext, str, activityInfo.name) && (z ? !(AccessibilitySettings.COMMON_SERVICES_LIST.contains(str) || this.mConfigedServiceList.contains(componentName)) : AccessibilitySettings.COMMON_SERVICES_LIST.contains(str))) {
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
                if (!AccessibilitySettings.isHideServices(this.mContext, str2, serviceInfo.name) && (z ? !(AccessibilitySettings.COMMON_SERVICES_LIST.contains(str2) || this.mConfigedServiceList.contains(componentName)) : AccessibilitySettings.COMMON_SERVICES_LIST.contains(str2) || this.mConfigedServiceList.contains(componentName))) {
                    String flattenToString = componentName.flattenToString();
                    CharSequence loadLabel = resolveInfo.loadLabel(this.mPm);
                    boolean contains = enabledServicesFromSettings.contains(componentName);
                    CharSequence serviceSummary = AccessibilitySettings.getServiceSummary(this.mContext, accessibilityServiceInfo, contains);
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
                    CharSequence serviceDescription = AccessibilitySettings.getServiceDescription(this.mContext, accessibilityServiceInfo, z2);
                    String loadHtmlDescription = accessibilityServiceInfo.loadHtmlDescription(this.mPm);
                    String settingsActivityName = accessibilityServiceInfo.getSettingsActivityName();
                    CharSequence charSequence3 = charSequence;
                    list2 = permittedAccessibilityServices;
                    putBasicExtras(createCustomRestrictedPreference, key, charSequence3, serviceDescription, animatedImageRes, loadHtmlDescription, componentName);
                    putServiceExtras(createCustomRestrictedPreference, resolveInfo, Boolean.valueOf(z2));
                    putSettingsExtras(createCustomRestrictedPreference, str, settingsActivityName);
                    arrayList.add(createCustomRestrictedPreference);
                } else {
                    set = enabledServicesFromSettings;
                    list2 = permittedAccessibilityServices;
                    i = size;
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
        HIDE_SERVICES_PACKAGE_LIST.add("com.miui.powerkeeper");
        ArrayList arrayList2 = new ArrayList();
        HIDE_SERVICES_LIST = arrayList2;
        arrayList2.add("com.google.android.accessibility.accessibilitymenu.AccessibilityMenuService");
        ArrayList arrayList3 = new ArrayList();
        COMMON_SERVICES_LIST = arrayList3;
        arrayList3.add("com.bjbyhd.voiceback");
        COMMON_SERVICES_LIST.add("com.android.tback");
        COMMON_SERVICES_LIST.add("com.nirenr.talkman");
        COMMON_SERVICES_LIST.add("com.dianming.phoneapp");
        ArrayList arrayList4 = new ArrayList();
        PRE_CONFIGED_SERVICES_LIST = arrayList4;
        arrayList4.add(Integer.valueOf(R.array.config_preinstalled_screen_reader_services));
        PRE_CONFIGED_SERVICES_LIST.add(Integer.valueOf(R.array.config_preinstalled_display_services));
        PRE_CONFIGED_SERVICES_LIST.add(Integer.valueOf(R.array.config_preinstalled_interaction_control_services));
        PRE_CONFIGED_SERVICES_LIST.add(Integer.valueOf(R.array.config_downloaded_services));
        HashSet hashSet = new HashSet();
        HIDE_SUMMARY_SERVICES_SET = hashSet;
        hashSet.add("com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService");
        SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.accessibility_settings) { // from class: com.android.settings.accessibility.AccessibilitySettings.4
            @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
            public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
                return FeatureFactory.getFactory(context).getAccessibilitySearchFeatureProvider().getSearchIndexableRawData(context);
            }
        };
    }

    public AccessibilitySettings() {
        Collection values = AccessibilityShortcutController.getFrameworkShortcutFeaturesMap().values();
        ArrayList arrayList = new ArrayList(values.size());
        Iterator it = values.iterator();
        while (it.hasNext()) {
            arrayList.add(((AccessibilityShortcutController.ToggleableFrameworkFeatureInfo) it.next()).getSettingKey());
        }
        arrayList.add("accessibility_button_targets");
        arrayList.add("accessibility_shortcut_target_service");
        this.mSettingsContentObserver = new SettingsContentObserver(this.mHandler, arrayList) { // from class: com.android.settings.accessibility.AccessibilitySettings.3
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                AccessibilitySettings.this.onContentChanged();
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
        arrayList.removeIf(new Predicate() { // from class: com.android.settings.accessibility.AccessibilitySettings$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean containsTargetNameInList;
                containsTargetNameInList = AccessibilitySettings.containsTargetNameInList(context, installedAccessibilityShortcutListAsUser, (AccessibilityServiceInfo) obj);
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CharSequence getServiceSummary(Context context, AccessibilityServiceInfo accessibilityServiceInfo, boolean z) {
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

    private void initializeAllPreferences() {
        int i = 0;
        while (true) {
            String[] strArr = CATEGORIES;
            if (i >= strArr.length) {
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isRampingRingerEnabled(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "apply_ramping_ringer", 0) == 1;
    }

    private void registerContentMonitors() {
        FragmentActivity activity = getActivity();
        this.mSettingsPackageMonitor.register(activity, activity.getMainLooper(), false);
        this.mSettingsContentObserver.register(getContentResolver());
    }

    private void unregisterContentMonitors() {
        this.mSettingsPackageMonitor.unregister();
        this.mSettingsContentObserver.unregister(getContentResolver());
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

    private void updatePreferenceCategoryVisibility(String str) {
        PreferenceCategory preferenceCategory = this.mCategoryToPrefCategoryMap.get(str);
        preferenceCategory.setVisible(preferenceCategory.getPreferenceCount() != 0);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_uri_accessibility;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AccessibilitySettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 2;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return AccessibilitySettings.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.accessibility_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AccessibilityHearingAidPreferenceController) use(AccessibilityHearingAidPreferenceController.class)).setFragmentManager(getFragmentManager());
    }

    void onContentChanged() {
        if (this.mIsForeground) {
            updateAllPreferences();
        } else {
            this.mNeedPreferencesUpdate = true;
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initializeAllPreferences();
        updateAllPreferences();
        registerContentMonitors();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        unregisterContentMonitors();
        super.onDestroy();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        if (this.mNeedPreferencesUpdate) {
            updateAllPreferences();
            this.mNeedPreferencesUpdate = false;
        }
        this.mIsForeground = true;
        super.onStart();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        this.mIsForeground = false;
        super.onStop();
    }

    void updateAllPreferences() {
        updateSystemPreferences();
        updateServicePreferences();
    }

    protected void updateServicePreferences() {
        ArrayList arrayList = new ArrayList(this.mServicePreferenceToPreferenceCategoryMap.keySet());
        for (int i = 0; i < arrayList.size(); i++) {
            Preference preference = (Preference) arrayList.get(i);
            this.mServicePreferenceToPreferenceCategoryMap.get(preference).removePreference(preference);
        }
        initializePreBundledServicesMapFromArray("screen_reader_category", R.array.config_preinstalled_screen_reader_services);
        initializePreBundledServicesMapFromArray("captions_category", R.array.config_preinstalled_captions_services);
        initializePreBundledServicesMapFromArray("audio_category", R.array.config_preinstalled_audio_services);
        initializePreBundledServicesMapFromArray("display_category", R.array.config_preinstalled_display_services);
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
        updateCategoryOrderFromArray("screen_reader_category", R.array.config_order_screen_reader_services);
        updateCategoryOrderFromArray("captions_category", R.array.config_order_captions_services);
        updateCategoryOrderFromArray("audio_category", R.array.config_order_audio_services);
        updateCategoryOrderFromArray("interaction_control_category", R.array.config_order_interaction_control_services);
        updateCategoryOrderFromArray("display_category", R.array.config_order_display_services);
        if (preferenceCategory.getPreferenceCount() == 0) {
            getPreferenceScreen().removePreference(preferenceCategory);
        } else {
            getPreferenceScreen().addPreference(preferenceCategory);
        }
        updatePreferenceCategoryVisibility("screen_reader_category");
    }

    protected void updateSystemPreferences() {
    }
}
