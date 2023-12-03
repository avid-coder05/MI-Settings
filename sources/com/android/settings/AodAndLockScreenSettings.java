package com.android.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.utils.AodUtils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.util.Arrays;
import java.util.Iterator;
import miui.content.ExtraIntent;
import miui.content.res.ThemeResources;
import miui.os.Build;
import miui.util.FeatureParser;
import miuix.preference.DropDownPreference;

/* loaded from: classes.dex */
public class AodAndLockScreenSettings extends KeyguardSettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private KeyguardRestrictedPreference mAodModePref;
    private AodModePreferenceController mAodModePrefController;
    private KeyguardRestrictedPreference mAodNotificationModePref;
    private AodNotificationPrefController mAodNotificationPrefController;
    private AodSettingPreferenceController mAodSettingPrefController;
    private PreferenceCategory mAodSettingsCategory;
    private Preference mAodSettingsSwitchPref;
    private KeyguardRestrictedPreference mAodShowModePref;
    private AodShowModePreferenceController mAodShowModePrefController;
    private AodStylePreference mAodStylePref;
    private AodStylePreferenceController mAodStylePrefController;
    private DropDownPreference mChargeAnimationStyle;
    private CheckBoxPreference mEyeGazePref;
    private CheckBoxPreference mFoldLockScreenCbp;
    private CheckBoxPreference mGestureWakeupPref;
    private LockPatternUtils mLockPatternUtils;
    private PreferenceCategory mLockScreenDisplayCategory;
    private ValuePreference mLockScreenMagazine;
    private PreferenceCategory mOtherCategory;
    private CheckBoxPreference mPickupWakeupPref;
    private CheckBoxPreference mPowerMenuUnderKeyguard;
    private CheckBoxPreference mScreenOnProximitySensor;
    private KeyguardTimeoutListPreference mScreenTimeout;
    private CheckBoxPreference mShowChargingInNonLockscreen;
    private CheckBoxPreference mSmartCoverSensitiveCbp;
    private CheckBoxPreference mVolumeKeyLaunchCamera;
    private PreferenceCategory mWakeupAndSleepCategory;
    private CheckBoxPreference mWakeupForKeyguardNotificationPref;
    private boolean mNotificationStyleSelectAvaliable = false;
    private boolean mAodShowModeStyleSelectAvaliable = false;
    private boolean mAodStyleListSupportSetMode = false;
    ContentObserver mAodStateObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.AodAndLockScreenSettings.2
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            AodAndLockScreenSettings.this.updateAodState();
        }
    };

    public static boolean checkoutActivityExist(Context context, String str, String str2) {
        Intent intent = new Intent();
        intent.setClassName(str, str2);
        return context.getPackageManager().resolveActivity(intent, 0) != null;
    }

    private void enableScreenOnProximitySensor(boolean z) {
        MiuiSettings.Global.putBoolean(getContentResolver(), "enable_screen_on_proximity_sensor", z);
    }

    public static Intent getAodIntent(Context context) {
        Intent intent = new Intent();
        if (AodUtils.supportSettingSplit(context.getApplicationContext())) {
            intent.addFlags(268435456);
        }
        intent.setClassName("com.miui.aod", "com.miui.aod.settings.AodStyleCategoriesActivity");
        intent.setAction("android.intent.action.MAIN");
        return intent;
    }

    public static Intent getKeyguardClockIntent(Context context) {
        Intent intent = new Intent();
        intent.setClassName(ThemeResources.SYSTEMUI_NAME, "com.android.keyguard.settings.ChooseKeyguardClockActivity");
        intent.putExtra(ExtraIntent.EXTRA_USER_ID, UserHandle.myUserId());
        return intent;
    }

    private static ComponentName getSettingsComponent(Context context, String str) {
        try {
            String string = context.getContentResolver().call(Uri.parse("content://" + str), "getSettingsComponent", (String) null, (Bundle) null).getString("result_string");
            if (TextUtils.isEmpty(string)) {
                return null;
            }
            return ComponentName.unflattenFromString(string);
        } catch (Exception unused) {
            return null;
        }
    }

    public static Intent getWallpaperIntent(Context context) {
        Intent intent;
        Iterator<ResolveInfo> it = context.getPackageManager().queryIntentContentProviders(new Intent("miui.intent.action.LOCKWALLPAPER_PROVIDER"), 0).iterator();
        while (true) {
            if (!it.hasNext()) {
                return null;
            }
            String str = it.next().providerInfo.authority;
            try {
            } catch (Exception e) {
                Log.e(MiuiSecuritySettings.class.getName(), "call lockscreen magazine provider  throw an exception" + e);
            }
            if (isProviderEnabled(context, str)) {
                if ("com.xiaomi.tv.gallerylockscreen.lockscreen_magazine_provider".equals(str)) {
                    intent = new Intent("android.intent.action.VIEW", Uri.parse("mifg://fashiongallery/jump_setting"));
                } else {
                    if ("IN".equalsIgnoreCase(Build.getRegion())) {
                        intent = new Intent("com.miui.android.fashiongallery.setting.SETTING");
                    }
                    intent = null;
                }
                if ((intent != null ? context.getPackageManager().resolveActivity(intent, 64) : null) != null) {
                    break;
                }
                intent = new Intent();
                intent.setComponent(getSettingsComponent(context, str));
                if (context.getPackageManager().resolveActivity(intent, 64) != null) {
                    break;
                }
            }
        }
        return intent;
    }

    private void initKeyguardNotificationPref() {
        if (!MiuiKeyguardSettingsUtils.isSupportAodAnimateDevice(getActivity()) || !this.mNotificationStyleSelectAvaliable) {
            this.mWakeupForKeyguardNotificationPref.setChecked(MiuiKeyguardSettingsUtils.isWakeupForNotification(getActivity(), getContentResolver()));
            return;
        }
        CheckBoxPreference checkBoxPreference = this.mWakeupForKeyguardNotificationPref;
        if (checkBoxPreference != null) {
            this.mWakeupAndSleepCategory.removePreference(checkBoxPreference);
        }
    }

    private void initLockScreenMagazine() {
        if (Build.IS_TABLET) {
            this.mLockScreenDisplayCategory.removePreference(this.mLockScreenMagazine);
        } else {
            new AsyncTask<Void, Void, Intent>() { // from class: com.android.settings.AodAndLockScreenSettings.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Intent doInBackground(Void... voidArr) {
                    if (AodAndLockScreenSettings.this.getActivity() == null) {
                        return null;
                    }
                    return AodAndLockScreenSettings.getWallpaperIntent(AodAndLockScreenSettings.this.getActivity());
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(Intent intent) {
                    if (AodAndLockScreenSettings.this.getActivity() != null) {
                        if (intent != null) {
                            AodAndLockScreenSettings.this.mLockScreenMagazine.setIntent(intent);
                        } else {
                            AodAndLockScreenSettings.this.mLockScreenDisplayCategory.removePreference(AodAndLockScreenSettings.this.mLockScreenMagazine);
                        }
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public static boolean isAdaptiveSleepSupported(Context context) {
        return context != null && context.getResources().getBoolean(17891341) && isAttentionServiceAvailable(context);
    }

    private static boolean isAttentionServiceAvailable(Context context) {
        ResolveInfo resolveService;
        PackageManager packageManager = context.getPackageManager();
        String attentionServicePackageName = packageManager.getAttentionServicePackageName();
        return (TextUtils.isEmpty(attentionServicePackageName) || (resolveService = packageManager.resolveService(new Intent("android.service.attention.AttentionService").setPackage(attentionServicePackageName), 1048576)) == null || resolveService.serviceInfo == null) ? false : true;
    }

    public static boolean isEllipticProximity(Context context) {
        return SystemProperties.getBoolean("ro.vendor.audio.us.proximity", false);
    }

    public static boolean isLockScreenMagazineAvailable(Context context) {
        return (Build.IS_TABLET || getWallpaperIntent(context) == null) ? false : true;
    }

    private static boolean isProviderEnabled(Context context, String str) {
        return "com.xiaomi.tv.gallerylockscreen.lockscreen_magazine_provider".equals(str) || "com.miui.android.fashiongallery.lockscreen_magazine_provider".equals(str);
    }

    private boolean isShowChangingInNonLockscreenEnable() {
        return Settings.System.getInt(getContentResolver(), "show_charging_in_non_lockscreen", 1) == 1;
    }

    public static boolean isSupportAntiMisTouch(Context context) {
        if (context == null) {
            return false;
        }
        SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
        return (sensorManager != null ? sensorManager.getDefaultSensor(33171095) : null) != null || (context.getPackageManager().hasSystemFeature("android.hardware.sensor.proximity") && !isEllipticProximity(context));
    }

    public static boolean isSupportPickupWakeup(Context context) {
        if (context != null) {
            SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
            Sensor defaultSensor = sensorManager.getDefaultSensor(33171036, true);
            if (defaultSensor == null || !("oem7 Pick Up Gesture".equalsIgnoreCase(defaultSensor.getName()) || "pickup  Wakeup".equalsIgnoreCase(defaultSensor.getName()))) {
                return Arrays.asList(context.getResources().getStringArray(R.array.device_support_pickup_by_MTK)).contains(android.os.Build.DEVICE) && sensorManager.getDefaultSensor(22, true) != null;
            }
            return true;
        }
        return false;
    }

    private void setupChargeAnimationStyle() {
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("charge_animation_style");
        this.mChargeAnimationStyle = dropDownPreference;
        dropDownPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.AodAndLockScreenSettings.3
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                try {
                    Settings.System.putInt(AodAndLockScreenSettings.this.getContentResolver(), "charge_animation_type", Integer.parseInt((String) obj));
                    return true;
                } catch (NumberFormatException unused) {
                    return false;
                }
            }
        });
    }

    private void setupPowerMenuUnderKeyguard() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("power_menu_under_keyguard");
        this.mPowerMenuUnderKeyguard = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.AodAndLockScreenSettings.4
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Settings.System.putInt(AodAndLockScreenSettings.this.getContentResolver(), "power_menu_under_keyguard", ((Boolean) obj).booleanValue() ? 1 : 0);
                return true;
            }
        });
    }

    private void setupTimeoutPreference() {
        this.mScreenTimeout.setValue(String.valueOf(Settings.System.getLong(getActivity().getContentResolver(), "screen_off_timeout", 30000L)));
        this.mScreenTimeout.disableUnusableTimeouts();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAodState() {
        if (this.mAodStyleListSupportSetMode) {
            this.mAodModePrefController.updateState(this.mAodModePref);
        } else {
            this.mAodSettingPrefController.updateState(this.mAodSettingsSwitchPref);
            this.mAodStylePrefController.updateState(this.mAodStylePref);
            if (this.mAodShowModeStyleSelectAvaliable && AodStylePreferenceController.isDualClock(getActivity())) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.miui.aod", "com.miui.aod.settings.AODStyleActivity"));
                intent.setAction("android.intent.action.MAIN");
                this.mAodStylePref.setIntent(intent);
            }
            this.mAodShowModePrefController.updateState(this.mAodShowModePref);
        }
        this.mAodNotificationPrefController.updateState(this.mAodNotificationModePref);
    }

    private void updateChargeAnimationStyle() {
        DropDownPreference dropDownPreference = this.mChargeAnimationStyle;
        int i = Settings.System.getInt(getContentResolver(), "charge_animation_type", 0);
        if (i < -1 || i > 2) {
            i = 0;
        }
        dropDownPreference.setValue(String.valueOf(i));
    }

    private void updatePowerMenuUnderKeyguard() {
        this.mPowerMenuUnderKeyguard.setChecked(MiuiSettings.System.getBoolean(getContentResolver(), "power_menu_under_keyguard", true));
    }

    private void updateProximitySensorStatus() {
        boolean z;
        if (isSupportAntiMisTouch(getActivity())) {
            int i = Settings.Global.getInt(getContentResolver(), "enable_screen_on_proximity_sensor", -1);
            if (i == -1) {
                z = MiuiSettings.System.getBoolean(getContentResolver(), "enable_screen_on_proximity_sensor", getResources().getBoolean(285540416));
                MiuiSettings.Global.putBoolean(getContentResolver(), "enable_screen_on_proximity_sensor", z);
            } else {
                z = i != 0;
            }
            this.mScreenOnProximitySensor.setChecked(z);
            return;
        }
        CheckBoxPreference checkBoxPreference = this.mScreenOnProximitySensor;
        if (checkBoxPreference != null) {
            this.mOtherCategory.removePreference(checkBoxPreference);
            this.mScreenOnProximitySensor = null;
            enableScreenOnProximitySensor(false);
        }
    }

    private void updateTimeoutPreferenceState() {
        if (this.mScreenTimeout != null) {
            setupTimeoutPreference();
            this.mScreenTimeout.updateTimeoutPreferenceSummary();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 87;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return AodAndLockScreenSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getPageIndex() {
        return 2;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        boolean z;
        super.onCreate(bundle);
        this.mLockPatternUtils = new LockPatternUtils(getActivity());
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        addPreferencesFromResource(R.xml.aod_and_lockscreen_settings);
        setupPowerMenuUnderKeyguard();
        setupChargeAnimationStyle();
        this.mNotificationStyleSelectAvaliable = checkoutActivityExist(getActivity(), "com.miui.aod", "com.miui.aod.settings.NotificationAnimationSelectActivity");
        this.mAodShowModeStyleSelectAvaliable = checkoutActivityExist(getActivity(), "com.miui.aod", "com.miui.aod.settings.AodShowModeSettingActivity");
        this.mAodSettingsCategory = (PreferenceCategory) findPreference("aod_settings_category");
        this.mAodSettingsSwitchPref = findPreference(AodSettingPreferenceController.KEY_AOD_SETTINGS);
        AodSettingPreferenceController aodSettingPreferenceController = new AodSettingPreferenceController(getActivity());
        this.mAodSettingPrefController = aodSettingPreferenceController;
        aodSettingPreferenceController.setAodShowModeStyleSelectAvaliable(this.mAodShowModeStyleSelectAvaliable);
        this.mAodSettingPrefController.displayPreference(getPreferenceScreen());
        this.mAodStylePref = (AodStylePreference) findPreference(AodStylePreferenceController.KEY_AOD_STYLE);
        AodStylePreferenceController aodStylePreferenceController = new AodStylePreferenceController(getActivity());
        this.mAodStylePrefController = aodStylePreferenceController;
        aodStylePreferenceController.setAodShowModeStyleSelectAvaliable(this.mAodShowModeStyleSelectAvaliable);
        this.mAodStylePrefController.displayPreference(getPreferenceScreen());
        this.mAodSettingPrefController.addController(this.mAodStylePrefController);
        this.mAodShowModePref = (KeyguardRestrictedPreference) findPreference(AodShowModePreferenceController.KEY_AOD_SHOW_MODE);
        AodShowModePreferenceController aodShowModePreferenceController = new AodShowModePreferenceController(getActivity());
        this.mAodShowModePrefController = aodShowModePreferenceController;
        aodShowModePreferenceController.setAodShowModeStyleSelectAvaliable(this.mAodShowModeStyleSelectAvaliable);
        this.mAodShowModePrefController.displayPreference(getPreferenceScreen());
        this.mAodSettingPrefController.addController(this.mAodShowModePrefController);
        this.mAodModePref = (KeyguardRestrictedPreference) findPreference(AodModePreferenceController.KEY_AOD_MODE);
        AodModePreferenceController aodModePreferenceController = new AodModePreferenceController(getActivity());
        this.mAodModePrefController = aodModePreferenceController;
        aodModePreferenceController.displayPreference(getPreferenceScreen());
        this.mAodSettingPrefController.addController(this.mAodModePrefController);
        this.mAodNotificationModePref = (KeyguardRestrictedPreference) findPreference(AodNotificationPrefController.AOD_KEYGUARD_NOTIFICATION_STATUS);
        AodNotificationPrefController aodNotificationPrefController = new AodNotificationPrefController(getActivity());
        this.mAodNotificationPrefController = aodNotificationPrefController;
        aodNotificationPrefController.setAodShowModeStyleSelectAvaliable(this.mAodShowModeStyleSelectAvaliable);
        this.mAodNotificationPrefController.setNotificationStyleSelectAvaliable(this.mNotificationStyleSelectAvaliable);
        this.mAodNotificationPrefController.displayPreference(getPreferenceScreen());
        if (AodUtils.isAodAvailable(getActivity())) {
            if (checkoutActivityExist(getActivity(), "com.miui.aod", "com.miui.aod.settings.AODSettingActivity")) {
                this.mAodStyleListSupportSetMode = AodUtils.actionAvailable(getActivity());
            } else {
                Intent intent = new Intent(getActivity(), AODSettingActivity.class);
                intent.setAction("android.intent.action.MAIN");
                this.mAodStylePref.setIntent(intent);
                this.mAodShowModePref.setIntent(intent);
                this.mAodNotificationModePref.setIntent(intent);
            }
            if (this.mAodStyleListSupportSetMode) {
                this.mAodSettingsCategory.removePreference(this.mAodSettingsSwitchPref);
                this.mAodSettingsCategory.removePreference(this.mAodShowModePref);
                this.mAodSettingsCategory.removePreference(this.mAodStylePref);
            } else {
                this.mAodSettingsCategory.removePreference(this.mAodModePref);
            }
            updateAodState();
        } else {
            getPreferenceScreen().removePreference(this.mAodSettingsCategory);
        }
        this.mWakeupAndSleepCategory = (PreferenceCategory) findPreference("wakeup_and_sleep_settings_category");
        this.mScreenTimeout = (KeyguardTimeoutListPreference) findPreference("screen_timeout");
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("eye_gaze");
        this.mEyeGazePref = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        if (isAdaptiveSleepSupported(getActivity())) {
            this.mEyeGazePref.setChecked((getActivity() == null || Settings.Secure.getIntForUser(getActivity().getContentResolver(), "adaptive_sleep", 0, -2) == 0) ? false : true);
        } else {
            this.mWakeupAndSleepCategory.removePreference(this.mEyeGazePref);
        }
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("gesture_wakeup");
        this.mGestureWakeupPref = checkBoxPreference2;
        checkBoxPreference2.setOnPreferenceChangeListener(this);
        if (!FeatureParser.getBoolean("support_gesture_wakeup", false)) {
            this.mWakeupAndSleepCategory.removePreference(this.mGestureWakeupPref);
        } else if (MiuiSettings.System.getBooleanForUser(getContentResolver(), "gesture_wakeup", false, UserHandle.myUserId())) {
            this.mGestureWakeupPref.setChecked(true);
        } else {
            this.mGestureWakeupPref.setChecked(false);
        }
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("pick_up_gesture_wakeup");
        this.mPickupWakeupPref = checkBoxPreference3;
        checkBoxPreference3.setOnPreferenceChangeListener(this);
        if (isSupportPickupWakeup(getActivity())) {
            this.mPickupWakeupPref.setChecked(MiuiSettings.System.getBooleanForUser(getContentResolver(), "pick_up_gesture_wakeup_mode", false, UserHandle.myUserId()));
        } else {
            this.mWakeupAndSleepCategory.removePreference(this.mPickupWakeupPref);
        }
        CheckBoxPreference checkBoxPreference4 = (CheckBoxPreference) findPreference("wakeup_for_keyguard_notification");
        this.mWakeupForKeyguardNotificationPref = checkBoxPreference4;
        checkBoxPreference4.setOnPreferenceChangeListener(this);
        initKeyguardNotificationPref();
        CheckBoxPreference checkBoxPreference5 = (CheckBoxPreference) findPreference("smartcover_lock_or_unlock_screen");
        this.mSmartCoverSensitiveCbp = checkBoxPreference5;
        checkBoxPreference5.setOnPreferenceChangeListener(this);
        getResources();
        int identifier = Resources.getSystem().getIdentifier("config_smartCoverEnabled", "bool", "android.miui");
        if (identifier > 0) {
            getResources();
            z = Resources.getSystem().getBoolean(identifier);
            Log.d("AodAndLockScreenSetting", "isSupportSmartCover: " + z);
        } else {
            Log.d("AodAndLockScreenSetting", "The device is old smart cover.");
            z = false;
        }
        if (z) {
            this.mSmartCoverSensitiveCbp.setChecked(Settings.Secure.getInt(getContentResolver(), "miui_smart_cover_mode", 1) == 1);
        } else {
            this.mWakeupAndSleepCategory.removePreference(this.mSmartCoverSensitiveCbp);
        }
        this.mFoldLockScreenCbp = (CheckBoxPreference) findPreference("lock_screen_after_fold_screen");
        if ("cetus".equalsIgnoreCase(android.os.Build.DEVICE)) {
            this.mFoldLockScreenCbp.setChecked(MiuiSettings.System.getBooleanForUser(getContentResolver(), "lock_screen_after_fold_screen", true, UserHandle.myUserId()));
            this.mFoldLockScreenCbp.setOnPreferenceChangeListener(this);
        } else {
            this.mWakeupAndSleepCategory.removePreference(this.mFoldLockScreenCbp);
        }
        this.mLockScreenDisplayCategory = (PreferenceCategory) findPreference("lock_screen_display_category");
        this.mLockScreenMagazine = (ValuePreference) findPreference("lockscreen_magazine");
        if ("IN".equalsIgnoreCase(Build.getRegion()) && Build.IS_INTERNATIONAL_BUILD) {
            this.mLockScreenMagazine.setTitle(getResources().getString(R.string.lockscreen_magazine_india));
        }
        this.mLockScreenMagazine.setShowRightArrow(true);
        initLockScreenMagazine();
        ((ValuePreference) findPreference("choose_keyguard_clock")).setShowRightArrow(true);
        this.mOtherCategory = (PreferenceCategory) findPreference("others_category");
        CheckBoxPreference checkBoxPreference6 = (CheckBoxPreference) findPreference("volume_launch_camera");
        this.mVolumeKeyLaunchCamera = checkBoxPreference6;
        checkBoxPreference6.setOnPreferenceChangeListener(this);
        if (FeatureParser.getBoolean("support_edge_touch_volume", false)) {
            CheckBoxPreference checkBoxPreference7 = this.mVolumeKeyLaunchCamera;
            if (checkBoxPreference7 != null) {
                this.mOtherCategory.removePreference(checkBoxPreference7);
            }
        } else {
            this.mVolumeKeyLaunchCamera.setChecked(Settings.System.getInt(getContentResolver(), "volumekey_launch_camera", 0) == 1);
        }
        CheckBoxPreference checkBoxPreference8 = (CheckBoxPreference) findPreference("screen_on_proximity_sensor");
        this.mScreenOnProximitySensor = checkBoxPreference8;
        checkBoxPreference8.setOnPreferenceChangeListener(this);
        CheckBoxPreference checkBoxPreference9 = (CheckBoxPreference) findPreference("show_charging_in_non_lockscreen");
        this.mShowChargingInNonLockscreen = checkBoxPreference9;
        checkBoxPreference9.setOnPreferenceChangeListener(this);
        this.mShowChargingInNonLockscreen.setChecked(isShowChangingInNonLockscreenEnable());
        updateProximitySensorStatus();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (AodUtils.isAodAvailable(getActivity())) {
            AodUtils.unregisterAodStateObserver(getActivity(), this.mAodStateObserver);
            this.mAodShowModePrefController.cancelTask();
            this.mAodStylePrefController.cancelTask();
            this.mAodModePrefController.cancelTask();
        }
        KeyguardTimeoutListPreference keyguardTimeoutListPreference = this.mScreenTimeout;
        if (keyguardTimeoutListPreference != null) {
            keyguardTimeoutListPreference.hideListView();
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference == this.mGestureWakeupPref) {
            MiuiSettings.System.putBooleanForUser(getContentResolver(), "gesture_wakeup", booleanValue, UserHandle.myUserId());
            return true;
        } else if (preference == this.mPickupWakeupPref) {
            MiuiSettings.System.putBooleanForUser(getContentResolver(), "pick_up_gesture_wakeup_mode", booleanValue, UserHandle.myUserId());
            return true;
        } else if (preference == this.mWakeupForKeyguardNotificationPref) {
            Settings.System.putInt(getContentResolver(), "wakeup_for_keyguard_notification", booleanValue ? 1 : 0);
            return true;
        } else if ("smartcover_lock_or_unlock_screen".equals(key)) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            Settings.Secure.putInt(getContentResolver(), "miui_smart_cover_mode", booleanValue ? 1 : 0);
            return true;
        } else if (preference == this.mVolumeKeyLaunchCamera) {
            Settings.System.putInt(getContentResolver(), "volumekey_launch_camera", booleanValue ? 1 : 0);
            return true;
        } else if (preference == this.mScreenOnProximitySensor) {
            enableScreenOnProximitySensor(booleanValue);
            return true;
        } else if (preference == this.mShowChargingInNonLockscreen) {
            Settings.System.putInt(getContentResolver(), "show_charging_in_non_lockscreen", booleanValue ? 1 : 0);
            return true;
        } else if (preference == this.mEyeGazePref) {
            Settings.Secure.putInt(getContentResolver(), "adaptive_sleep", booleanValue ? 1 : 0);
            return true;
        } else if ("lock_screen_after_fold_screen".equals(key)) {
            MiuiSettings.System.putBooleanForUser(getContentResolver(), "lock_screen_after_fold_screen", booleanValue, UserHandle.myUserId());
            return true;
        } else {
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if ("choose_keyguard_clock".equals(preference.getKey())) {
            Intent intent = new Intent();
            intent.setClassName(ThemeResources.SYSTEMUI_NAME, "com.android.keyguard.settings.ChooseKeyguardClockActivity");
            intent.putExtra(ExtraIntent.EXTRA_USER_ID, UserHandle.myUserId());
            startActivity(intent);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (AodUtils.isAodAvailable(getActivity())) {
            AodUtils.registerAodStateObserver(getActivity(), this.mAodStateObserver);
        }
        updateAodState();
        updateTimeoutPreferenceState();
        updateChargeAnimationStyle();
        updatePowerMenuUnderKeyguard();
    }
}
