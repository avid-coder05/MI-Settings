package com.android.settings.freeform;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.report.InternationalCompat;
import com.android.settings.utils.SettingsFeatures;
import com.milink.api.v1.type.MilinkConfig;
import miui.os.Build;
import miui.os.UserHandle;
import miuix.core.util.SystemProperties;

/* loaded from: classes.dex */
public class FreeformGuideSettings extends MiuiSettingsPreferenceFragment {
    private static boolean mCvw;
    private static boolean mPin;
    public final boolean IS_MIUI_13;
    public final int MIUI_VERSION_CODE;
    private Context mContext;
    private PreferenceCategory mDockAssistant;
    private Preference mDropDown;
    private Preference mFreeformGuideToSidehide;
    private Preference mFullToFreeform;
    private Preference mFullToFreeformGlobal;
    private Preference mHang;
    private Preference mMove;
    private Preference mMultiWindow;
    private Preference mMultiWindowGlobal;
    private Preference mNotification;
    private final Uri WORLD_CIRCULATE_URI = Uri.parse("content://com.milink.service.circulate");
    public final String WORLD_CIRCULATE_PACKAGE_NAME = MilinkConfig.PACKAGE_NAME;
    private final ComponentName PUSH_WORLD_CIRCULATE_NAME = new ComponentName(MilinkConfig.PACKAGE_NAME, "com.miui.circulate.world.AppCirculateActivity");
    public boolean sIsSupportPushAppEnterWorldCirculate = false;
    public final int MIUI_V13_VERSION_CODE = 13;

    public FreeformGuideSettings() {
        int miuiVersionCode = getMiuiVersionCode();
        this.MIUI_VERSION_CODE = miuiVersionCode;
        this.IS_MIUI_13 = miuiVersionCode == 13;
    }

    private boolean canTaskPushEnterWorldCirculate() {
        try {
            return this.mContext.getContentResolver().call(this.WORLD_CIRCULATE_URI, "check_permission", "recentlist_app", (Bundle) null).getBoolean("result");
        } catch (Exception e) {
            Log.d("FreeformGuideSettings", "canTaskPushEnterWorldCirculate", e);
            Bundle worldCirculateMeta = this.getWorldCirculateMeta(this.PUSH_WORLD_CIRCULATE_NAME);
            if (worldCirculateMeta != null) {
                return worldCirculateMeta.getBoolean("appcirculate_support_recentlist");
            }
            return false;
        }
    }

    public static boolean getCvw() {
        return mCvw;
    }

    private static int getGestureLineHeight(Context context) {
        if (isShowNavigationHandle(context)) {
            return context.getResources().getDimensionPixelSize(17105371);
        }
        return 0;
    }

    public static boolean getPin() {
        return mPin;
    }

    private Bundle getWorldCirculateMeta(ComponentName componentName) {
        ActivityInfo activityInfo;
        try {
            activityInfo = this.mContext.getPackageManager().getActivityInfo(componentName, 786560);
        } catch (Exception e) {
            Log.d("FreeformGuideSettingsgetWorldCirculateMeta", "ComponentName = " + componentName, e);
            activityInfo = null;
        }
        if (activityInfo != null) {
            return activityInfo.metaData;
        }
        return null;
    }

    private void initDockAssistant() {
        this.mDockAssistant = (PreferenceCategory) findPreference("freeform_dock_assistant");
    }

    private void initDropDown() {
        Preference findPreference = findPreference("key_freeform_guide_drop_down_to_fullscreen");
        this.mDropDown = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.freeform.FreeformGuideSettings.6
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    if (SettingsFeatures.isFoldDevice() || Build.IS_TABLET) {
                        intent.addMiuiFlags(8);
                    }
                    if (Build.IS_TABLET) {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformTutorialSettingActivity"));
                    } else {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformDemoActivity"));
                    }
                    intent.putExtra("DEMO_TYPE", "DEMO_DROP_DOWN_TO_FULLSCREEN");
                    FreeformGuideSettings.this.mContext.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initFreeformSideHide() {
        Preference findPreference = findPreference("key_freeform_guide_to_sidehide");
        this.mFreeformGuideToSidehide = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.freeform.FreeformGuideSettings.5
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    if (SettingsFeatures.isFoldDevice() || Build.IS_TABLET) {
                        intent.addMiuiFlags(8);
                    }
                    if (Build.IS_TABLET) {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformTutorialSettingActivity"));
                        intent.putExtra("DEMO_TYPE", "DEMO_TO_SIDEHIDE");
                    } else {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformDemoActivity"));
                        intent.putExtra("DEMO_TYPE", "DEMO_MOVE");
                    }
                    FreeformGuideSettings.this.mContext.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initFullToFreeform() {
        this.mFullToFreeform = getPreferenceScreen().findPreference("key_freeform_guide_slide_to_small_freeform");
        if (!getSupportPushAppEnterWorldCirculate()) {
            this.mFullToFreeform.setSummary(R.string.freeform_guide_slide_to_small_freeform_summary_squarehot);
        }
        Preference preference = this.mFullToFreeform;
        if (preference != null) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.freeform.FreeformGuideSettings.8
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference2) {
                    Intent intent = new Intent();
                    if (SettingsFeatures.isFoldDevice() || Build.IS_TABLET) {
                        intent.addMiuiFlags(8);
                    }
                    if (Build.IS_TABLET) {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformTutorialSettingActivity"));
                        intent.putExtra("DEMO_TYPE", "DEMO_FREEFORM_SLIDE_TO_SMALL_FREEFORM");
                    } else {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformDemoActivity"));
                        intent.putExtra("DEMO_TYPE", "DEMO_FULLSCREEN_SLIDE_TO_SMALL_FREEFORM");
                    }
                    FreeformGuideSettings.this.mContext.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initFullToFreeformGlobal() {
        Preference findPreference = getPreferenceScreen().findPreference("key_freeform_guide_slide_to_small_freeform_global");
        this.mFullToFreeformGlobal = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.freeform.FreeformGuideSettings.9
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    if (SettingsFeatures.isFoldDevice() || Build.IS_TABLET) {
                        intent.addMiuiFlags(8);
                    }
                    if (Build.IS_TABLET) {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformTutorialSettingActivity"));
                    } else {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformDemoActivity"));
                    }
                    intent.putExtra("DEMO_TYPE", "DEMO_FREEFORM_SLIDE_TO_SMALL_FREEFORM_GLOBAL");
                    FreeformGuideSettings.this.mContext.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initHang() {
        Preference findPreference = getPreferenceScreen().findPreference("key_freeform_guide_move_to_small_freeform_window");
        this.mHang = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.freeform.FreeformGuideSettings.7
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    if (SettingsFeatures.isFoldDevice() || Build.IS_TABLET) {
                        intent.addMiuiFlags(8);
                    }
                    if (Build.IS_TABLET) {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformTutorialSettingActivity"));
                    } else {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformDemoActivity"));
                    }
                    intent.putExtra("DEMO_TYPE", "DEMO_HANG_TO_SMALL_FREEFORM");
                    FreeformGuideSettings.this.mContext.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initMove() {
        Preference findPreference = findPreference("key_freeform_guide_move");
        this.mMove = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.freeform.FreeformGuideSettings.4
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    if (SettingsFeatures.isFoldDevice() || Build.IS_TABLET) {
                        intent.addMiuiFlags(8);
                    }
                    if (Build.IS_TABLET) {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformTutorialSettingActivity"));
                    } else {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformDemoActivity"));
                    }
                    intent.putExtra("DEMO_TYPE", "DEMO_MOVE");
                    FreeformGuideSettings.this.mContext.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initMultiWindow() {
        Preference findPreference = findPreference("key_multi_window_cvw");
        this.mMultiWindow = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.freeform.FreeformGuideSettings.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    if (SettingsFeatures.isFoldDevice() || Build.IS_TABLET) {
                        intent.addMiuiFlags(8);
                    }
                    if (Build.IS_TABLET) {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformTutorialSettingActivity"));
                    } else {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformDemoActivity"));
                    }
                    intent.putExtra("DEMO_TYPE", "DEMO_MULTI_WINDOW_CVW");
                    FreeformGuideSettings.this.mContext.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initMultiWindowGlobal() {
        Preference findPreference = findPreference("key_multi_window_cvw_global");
        this.mMultiWindowGlobal = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.freeform.FreeformGuideSettings.2
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    if (SettingsFeatures.isFoldDevice() || Build.IS_TABLET) {
                        intent.addMiuiFlags(8);
                    }
                    if (Build.IS_TABLET) {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformTutorialSettingActivity"));
                    } else {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformDemoActivity"));
                    }
                    intent.putExtra("DEMO_TYPE", "DEMO_MULTI_WINDOW_CVW_GLOBAL");
                    FreeformGuideSettings.this.mContext.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initNotification() {
        Preference findPreference = findPreference("key_freeform_guide_notification_drop_down");
        this.mNotification = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.freeform.FreeformGuideSettings.3
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    if (SettingsFeatures.isFoldDevice() || Build.IS_TABLET) {
                        intent.addMiuiFlags(8);
                    }
                    if (Build.IS_TABLET) {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformTutorialSettingActivity"));
                    } else {
                        intent.setComponent(new ComponentName("com.miui.freeform", "com.miui.freeform.FreeformDemoActivity"));
                    }
                    intent.putExtra("DEMO_TYPE", "DEMO_NOTIFICATION_DROP_DOWN");
                    FreeformGuideSettings.this.mContext.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private static boolean isHideGestureLine(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "hide_gesture_line", 0) != 0;
    }

    public static boolean isShowNavigationHandle(Context context) {
        return MiuiSettings.Global.getBoolean(context.getContentResolver(), "force_fsg_nav_bar") && !isHideGestureLine(context);
    }

    private void removeDockAssistant() {
        getPreferenceScreen().removePreference(this.mDockAssistant);
    }

    private static void setCvw(boolean z) {
        mCvw = z;
    }

    private static void setPin(boolean z) {
        mPin = z;
    }

    public void checkAndUpdateWorldCirculateView(String str) {
        if (UserHandle.myUserId() != getUserSystemId()) {
            Log.d("FreeformGuideSettingscheckAndUpdateWorldCirculateView", "no in main space");
        } else if (this.IS_MIUI_13 && TextUtils.equals(str, MilinkConfig.PACKAGE_NAME)) {
            this.sIsSupportPushAppEnterWorldCirculate = canTaskPushEnterWorldCirculate();
            Log.d("FreeformGuideSettings", "sIsSupportPushAppEnterWorldCirculate = " + this.sIsSupportPushAppEnterWorldCirculate);
        }
    }

    public int getMiuiVersionCode() {
        String stringFromSystemProperites = getStringFromSystemProperites("ro.miui.ui.version.code", "");
        if (!TextUtils.isEmpty(stringFromSystemProperites)) {
            try {
                return Integer.parseInt(stringFromSystemProperites);
            } catch (Exception unused) {
            }
        }
        return 0;
    }

    public String getStringFromSystemProperites(String str, String str2) {
        return SystemProperties.get(str, str2);
    }

    public boolean getSupportPushAppEnterWorldCirculate() {
        Log.d("FreeformGuideSettings", "sIsSupportPushAppEnterWorldCirculate = " + this.sIsSupportPushAppEnterWorldCirculate);
        return this.sIsSupportPushAppEnterWorldCirculate;
    }

    public int getUserSystemId() {
        return 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mContext = getActivity();
        addPreferencesFromResource(R.xml.freeform_settings);
        getListView().setPadding(0, 0, 0, ((int) getResources().getDimension(R.dimen.freeform_settings_preference_bottom_padding_inner)) + getGestureLineHeight(getContext()));
        InternationalCompat.trackReportEvent("setting_Special_features_freeform");
        checkAndUpdateWorldCirculateView(MilinkConfig.PACKAGE_NAME);
        setCvw(true);
        setPin(SettingsFeatures.isSupportPin());
        initDockAssistant();
        initMultiWindow();
        initMultiWindowGlobal();
        initNotification();
        initMove();
        initFreeformSideHide();
        initDropDown();
        initHang();
        initFullToFreeform();
        initFullToFreeformGlobal();
        if (SettingsFeatures.isFoldDevice()) {
            this.mMultiWindow.setVisible(false);
            this.mMultiWindowGlobal.setVisible(false);
            this.mNotification.setVisible(true);
            this.mMove.setVisible(!mPin);
            this.mFreeformGuideToSidehide.setVisible(mPin);
            this.mDropDown.setVisible(true);
            this.mHang.setVisible(true);
            this.mFullToFreeform.setVisible(true);
            this.mFullToFreeformGlobal.setVisible(false);
        } else {
            boolean z = Build.IS_TABLET;
            if (z && !Build.IS_INTERNATIONAL_BUILD) {
                this.mMultiWindow.setVisible(mCvw);
                this.mMultiWindowGlobal.setVisible(false);
                this.mNotification.setVisible(true);
                this.mMove.setVisible(true);
                this.mFreeformGuideToSidehide.setVisible(mPin);
                this.mFreeformGuideToSidehide.setOrder(100);
                this.mDropDown.setVisible(true);
                this.mHang.setVisible(false);
                this.mFullToFreeform.setVisible(true ^ mPin);
                this.mFullToFreeformGlobal.setVisible(false);
                this.mFullToFreeform.setSummary(R.string.freeform_guide_slide_to_small_freeform_summary_pad);
            } else if (z && Build.IS_INTERNATIONAL_BUILD) {
                this.mMultiWindow.setVisible(false);
                this.mMultiWindowGlobal.setVisible(mCvw);
                this.mNotification.setVisible(true);
                this.mMove.setVisible(true);
                this.mFreeformGuideToSidehide.setVisible(mPin);
                this.mFreeformGuideToSidehide.setOrder(100);
                this.mDropDown.setVisible(true);
                this.mHang.setVisible(false);
                this.mFullToFreeform.setVisible(false);
                this.mFullToFreeformGlobal.setVisible(true ^ mPin);
                this.mFullToFreeformGlobal.setSummary(R.string.freeform_guide_slide_to_small_freeform_summary_pad_global);
            } else {
                this.mMultiWindow.setVisible(false);
                this.mMultiWindowGlobal.setVisible(false);
                this.mNotification.setVisible(true);
                this.mMove.setVisible(true);
                this.mFreeformGuideToSidehide.setVisible(mPin);
                this.mDropDown.setVisible(true);
                this.mHang.setVisible(true);
                this.mFullToFreeform.setVisible(true);
                this.mFullToFreeformGlobal.setVisible(false);
            }
        }
        if (SettingsFeatures.isSupportDock(this.mContext)) {
            return;
        }
        removeDockAssistant();
    }
}
