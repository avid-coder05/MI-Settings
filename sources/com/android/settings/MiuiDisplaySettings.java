package com.android.settings;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.internal.view.RotationPolicy;
import com.android.settings.BaseSettingsController;
import com.android.settings.display.DarkModeTimeModeUtil;
import com.android.settings.display.FontStatusController;
import com.android.settings.display.MonochromeModeFragment;
import com.android.settings.display.PageLayoutStatusController;
import com.android.settings.report.InternationalCompat;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.os.Build;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiDisplaySettings extends DisplaySettings implements Preference.OnPreferenceChangeListener {
    private static final String TAG = MiuiDisplaySettings.class.getSimpleName();
    private static SparseArray<Integer> sUiModeOrder;
    private ContentObserver mAODObserver;
    private ValuePreference mAdvancedPaperModePref;
    private CheckBoxPreference mAllowAllRotations;
    private Context mContext;
    private ValuePreference mDarkModeRadioTimePref;
    private PreferenceCategory mDarkModeTiming;
    private ValuePreference mFontSettingsPref;
    private BaseSettingsController mFontStatusController;
    private boolean mIsFontSettingEnable;
    private CheckBoxPreference mLineBreaking;
    private MiuiUtils mMiuiUtils;
    private ContentObserver mMonochromeModeEnabledObserver;
    private ValuePreference mMoreDarkModeSettings;
    private BaseSettingsController mPageLayoutStatusController;
    private ValuePreference mPageLayoutStatusPref;
    private ContentObserver mPaperModeEnabledObserver;
    private ValuePreference mPaperModePref;
    private CheckBoxPreference mRotatePreference;
    private Preference mScreenMonochromeModePref;
    private ValuePreference mScreenResolutionPref;
    private CheckBoxPreference mTouchSensitive;
    private final Configuration mCurConfig = new Configuration();
    private Handler mHandler = new Handler() { // from class: com.android.settings.MiuiDisplaySettings.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (MiuiDisplaySettings.this.mIsFontSettingEnable) {
                MiuiDisplaySettings.this.mFontSettingsPref.setVisible(true);
                return;
            }
            PreferenceGroup preferenceGroup = (PreferenceGroup) MiuiDisplaySettings.this.findPreference("font_settings_cat");
            if (preferenceGroup == null || MiuiDisplaySettings.this.mFontSettingsPref == null) {
                return;
            }
            preferenceGroup.removePreference(MiuiDisplaySettings.this.mFontSettingsPref);
            MiuiDisplaySettings.this.mFontStatusController.setUpdateCallback(null);
        }
    };
    private final RotationPolicy.RotationPolicyListener mRotationPolicyListener = new RotationPolicy.RotationPolicyListener() { // from class: com.android.settings.MiuiDisplaySettings.2
        public void onChange() {
            MiuiDisplaySettings.this.updateAccelerometerRotationCheckbox();
        }
    };

    /* renamed from: com.android.settings.MiuiDisplaySettings$7  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass7 implements Runnable {
        AnonymousClass7() {
        }

        @Override // java.lang.Runnable
        public void run() {
            MiuiDisplaySettings miuiDisplaySettings = MiuiDisplaySettings.this;
            miuiDisplaySettings.mIsFontSettingEnable = SettingsFeatures.checkGlobalFontSettingEnable(miuiDisplaySettings.mContext);
            MiuiDisplaySettings.this.mHandler.sendEmptyMessage(0);
        }
    }

    static {
        SparseArray<Integer> sparseArray = new SparseArray<>();
        sUiModeOrder = sparseArray;
        sparseArray.put(10, 0);
        sUiModeOrder.put(12, 1);
        sUiModeOrder.put(1, 2);
        sUiModeOrder.put(13, 2);
        sUiModeOrder.put(14, 3);
        sUiModeOrder.put(15, 4);
        sUiModeOrder.put(11, 5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableScreenOnProximitySensor(boolean z) {
        MiuiSettings.Global.putBoolean(getContentResolver(), "enable_screen_on_proximity_sensor", z);
    }

    private boolean getAnimateStatus() {
        return Settings.System.getInt(getContentResolver(), "animate_settings_status", FeatureParser.getBoolean("default_close_unlock_animator", false) ? 1 : 0) == 1;
    }

    private String isQhdMode() {
        String str = SystemProperties.get("persist.sys.miui_resolution", (String) null);
        return (str == null || "".equals(str) || Integer.parseInt(str.split(",")[0]) != 1080) ? "WQHD+" : "FHD+";
    }

    private void setupAllowAllRotations() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("allow_all_rotations");
        this.mAllowAllRotations = checkBoxPreference;
        if (checkBoxPreference != null) {
            if (getActivity().getResources().getBoolean(17891343)) {
                checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiDisplaySettings.9
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public boolean onPreferenceChange(Preference preference, Object obj) {
                        MiuiSettings.Global.putBoolean(MiuiDisplaySettings.this.getContentResolver(), "allow_all_rotations", ((Boolean) obj).booleanValue());
                        return true;
                    }
                });
                return;
            }
            getPreferenceScreen().removePreference(checkBoxPreference);
            this.mAllowAllRotations = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAccelerometerRotationCheckbox() {
        CheckBoxPreference checkBoxPreference;
        if (getActivity() == null || (checkBoxPreference = this.mRotatePreference) == null) {
            return;
        }
        checkBoxPreference.setChecked(!RotationPolicy.isRotationLocked(getActivity()));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void updateAllowAllRotations() {
        CheckBoxPreference checkBoxPreference = this.mAllowAllRotations;
        if (checkBoxPreference != null) {
            int i = Settings.Global.getInt(getContentResolver(), "allow_all_rotations", 1);
            boolean z = i;
            if (i != 0) {
                z = true;
            }
            checkBoxPreference.setChecked(z);
        }
    }

    private void updateAnimateStatus(boolean z) {
        Settings.System.putInt(getContentResolver(), "animate_settings_status", z ? 1 : 0);
    }

    private void updateDarkMode(ValuePreference valuePreference) {
        String str = TAG;
        Log.d(str, "updateDarkMode");
        FragmentActivity activity = getActivity();
        if (valuePreference == null || activity == null) {
            return;
        }
        valuePreference.setShowRightArrow(true);
        if (!DarkModeTimeModeUtil.isDarkModeTimeEnable(activity)) {
            this.mDarkModeRadioTimePref.setValue(R.string.screen_paper_mode_turn_off);
            return;
        }
        Log.d(str, "updateDarkMode DarkModeTimeEnable");
        if (DarkModeTimeModeUtil.getDarkModeTimeType(activity) == 2) {
            this.mDarkModeRadioTimePref.setValue(R.string.dark_mode_day_night_mode_title);
        } else {
            this.mDarkModeRadioTimePref.setValue(R.string.dark_mode_auto_time_title);
        }
    }

    private void updateDarkModeMoreSettings() {
        if (DarkModeTimeModeUtil.isDarkModeEnable(this.mContext) || DarkModeTimeModeUtil.isDarkModeTimeEnable(this.mContext)) {
            this.mMoreDarkModeSettings.setVisible(true);
        } else {
            this.mMoreDarkModeSettings.setVisible(false);
        }
    }

    private void updateFontSettings() {
        this.mFontStatusController.updateStatus();
        this.mPageLayoutStatusController.updateStatus();
    }

    private void updateLineBreakingPreference(boolean z) {
        SystemProperties.set("persist.sys.line_breaking", z ? "true" : "false");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMonochromeMode() {
        ValuePreference valuePreference = (ValuePreference) findPreference("screen_monochrome_mode");
        FragmentActivity activity = getActivity();
        if (valuePreference == null || activity == null) {
            return;
        }
        valuePreference.setShowRightArrow(true);
        valuePreference.setValue(MonochromeModeFragment.isMonochromeModeEnable(activity) ? R.string.screen_paper_mode_turn_on : R.string.screen_paper_mode_turn_off);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePaperMode(ValuePreference valuePreference) {
        FragmentActivity activity = getActivity();
        if (valuePreference == null || activity == null) {
            return;
        }
        valuePreference.setShowRightArrow(true);
        valuePreference.setValue(MiuiUtils.isPaperModeEnable(activity) ? R.string.screen_paper_mode_turn_on : R.string.screen_paper_mode_turn_off);
    }

    private void updateRotatePreference(boolean z) {
        RotationPolicy.setRotationLockForAccessibility(getActivity(), !z);
    }

    private void updateTouchSensitivePreference(boolean z) {
        this.mMiuiUtils.enableTouchSensitive(getActivity(), z);
        if (z) {
            enableScreenOnProximitySensor(true);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiDisplaySettings.class.getName();
    }

    @Override // com.android.settings.DisplaySettings, com.android.settings.SettingsPreferenceFragment
    public int getPageIndex() {
        return 1;
    }

    @Override // com.android.settings.DisplaySettings, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        Preference findPreference;
        super.onCreate(bundle);
        setupAllowAllRotations();
        getPreferenceScreen().findPreference("screen_effect");
        this.mMiuiUtils = MiuiUtils.getInstance();
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("touch_category");
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preferenceCategory.findPreference("touch_sensitive");
        this.mTouchSensitive = checkBoxPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(this);
        }
        if (FeatureParser.getBoolean("support_touch_sensitive", false)) {
            this.mTouchSensitive.setChecked(this.mMiuiUtils.isTouchSensitive(getActivity()));
        } else {
            preferenceCategory.removePreference(this.mTouchSensitive);
            this.mTouchSensitive = null;
        }
        if (preferenceCategory.getPreferenceCount() == 0) {
            getPreferenceScreen().removePreference(preferenceCategory);
        }
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("line_breaking");
        this.mLineBreaking = checkBoxPreference2;
        if (checkBoxPreference2 != null) {
            checkBoxPreference2.setOnPreferenceChangeListener(this);
        }
        Preference findPreference2 = findPreference("screen_monochrome_mode");
        this.mScreenMonochromeModePref = findPreference2;
        if (findPreference2 == null || (MiuiSettings.ScreenEffect.SCREEN_EFFECT_SUPPORTED & 8) != 0) {
            this.mMonochromeModeEnabledObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.MiuiDisplaySettings.3
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    MiuiDisplaySettings.this.updateMonochromeMode();
                }
            };
            getActivity().getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_monochrome_mode_enabled"), false, this.mMonochromeModeEnabledObserver);
        } else {
            getPreferenceScreen().removePreference(this.mScreenMonochromeModePref);
            this.mScreenMonochromeModePref = null;
        }
        this.mScreenResolutionPref = (ValuePreference) findPreference("screen_resolution");
        int[] intArray = FeatureParser.getIntArray("screen_resolution_supported");
        if (this.mScreenResolutionPref != null) {
            if (intArray == null || UserHandle.getCallingUserId() != 0 || getActivity().isInMultiWindowMode()) {
                getPreferenceScreen().removePreference(this.mScreenResolutionPref);
                this.mScreenResolutionPref = null;
            } else {
                this.mScreenResolutionPref.setValue(isQhdMode());
                this.mScreenResolutionPref.setShowRightArrow(true);
            }
        }
        if ((MiuiSettings.ScreenEffect.SCREEN_EFFECT_SUPPORTED & 7) == 0 && (findPreference = findPreference("screen_effect")) != null) {
            getPreferenceScreen().removePreference(findPreference);
        }
        this.mPaperModePref = (ValuePreference) findPreference("screen_paper_mode");
        ValuePreference valuePreference = (ValuePreference) findPreference("advanced_screen_paper_mode");
        this.mAdvancedPaperModePref = valuePreference;
        if (MiuiSettings.ScreenEffect.isScreenPaperModeSupported) {
            getPreferenceScreen().removePreference(MiuiUtils.supportPaperEyeCare() ? this.mPaperModePref : this.mAdvancedPaperModePref);
            this.mPaperModeEnabledObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.MiuiDisplaySettings.4
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    MiuiDisplaySettings miuiDisplaySettings = MiuiDisplaySettings.this;
                    boolean supportPaperEyeCare = MiuiUtils.supportPaperEyeCare();
                    MiuiDisplaySettings miuiDisplaySettings2 = MiuiDisplaySettings.this;
                    miuiDisplaySettings.updatePaperMode(supportPaperEyeCare ? miuiDisplaySettings2.mAdvancedPaperModePref : miuiDisplaySettings2.mPaperModePref);
                }
            };
            getActivity().getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_paper_mode_enabled"), false, this.mPaperModeEnabledObserver);
        } else if (this.mPaperModePref != null && valuePreference != null) {
            getPreferenceScreen().removePreference(this.mPaperModePref);
            getPreferenceScreen().removePreference(this.mAdvancedPaperModePref);
        }
        this.mFontStatusController = new FontStatusController(getActivity(), null);
        ValuePreference valuePreference2 = (ValuePreference) findPreference("font_settings");
        this.mFontSettingsPref = valuePreference2;
        if (valuePreference2 != null) {
            valuePreference2.setShowRightArrow(true);
            this.mFontStatusController.setUpdateCallback(new BaseSettingsController.UpdateCallback() { // from class: com.android.settings.MiuiDisplaySettings.5
                @Override // com.android.settings.BaseSettingsController.UpdateCallback
                public void updateText(String str) {
                    if (Build.IS_GLOBAL_BUILD) {
                        return;
                    }
                    MiuiDisplaySettings.this.mFontSettingsPref.setValue(str);
                }
            });
        }
        this.mPageLayoutStatusController = new PageLayoutStatusController(getActivity(), null);
        ValuePreference valuePreference3 = (ValuePreference) findPreference("page_layout_settings");
        this.mPageLayoutStatusPref = valuePreference3;
        if (valuePreference3 != null) {
            valuePreference3.setShowRightArrow(true);
            this.mPageLayoutStatusController.setUpdateCallback(new BaseSettingsController.UpdateCallback() { // from class: com.android.settings.MiuiDisplaySettings.6
                @Override // com.android.settings.BaseSettingsController.UpdateCallback
                public void updateText(String str) {
                    MiuiDisplaySettings.this.mPageLayoutStatusPref.setValue(str);
                }
            });
        }
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        if (this.mPageLayoutStatusPref != null) {
            if (MiuiUtils.isEasyMode(activity)) {
                this.mPageLayoutStatusPref.setEnabled(false);
            }
            ValuePreference valuePreference4 = this.mFontSettingsPref;
            if (valuePreference4 != null) {
                valuePreference4.setVisible(false);
            }
            this.mPageLayoutStatusPref.setTitle(R.string.title_font_settings);
            this.mPageLayoutStatusPref.setSummary(R.string.font_settings_summary_cn);
        }
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("font_settings_cat");
        if (preferenceCategory2 != null && preferenceCategory2.getPreferenceCount() == 0) {
            getPreferenceScreen().removePreference(preferenceCategory2);
        }
        boolean z = UserHandle.myUserId() == 0;
        if (preferenceCategory2 != null && !z) {
            getPreferenceScreen().removePreference(preferenceCategory2);
        }
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("auto_rotate");
        this.mRotatePreference = checkBoxPreference3;
        checkBoxPreference3.setOnPreferenceChangeListener(this);
        this.mRotatePreference.setPersistent(false);
        this.mMoreDarkModeSettings = (ValuePreference) findPreference("dark_mode_apps_setting");
        this.mDarkModeTiming = (PreferenceCategory) findPreference("dark_mode_timing");
        if (MiuiSettings.Secure.isSecureSpace(this.mContext.getContentResolver())) {
            getPreferenceScreen().removePreference(this.mDarkModeTiming);
        }
        if (MiuiUtils.supportAnimateCheck()) {
            CheckBoxPreference checkBoxPreference4 = (CheckBoxPreference) findPreference("animate_settings_key");
            checkBoxPreference4.setChecked(getAnimateStatus());
            checkBoxPreference4.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference("animate_settings_cat"));
        }
        this.mDarkModeRadioTimePref = (ValuePreference) findPreference("dark_mode_time_settings");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        return i == 100 ? new AlertDialog.Builder(getActivity()).setTitle(R.string.touch_sensitive_turn_off_title).setMessage(R.string.touch_sensitive_turn_off_summary).setPositiveButton(R.string.touch_sensitive_turn_off_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiDisplaySettings.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                MiuiDisplaySettings.this.enableScreenOnProximitySensor(false);
            }
        }).setNegativeButton(R.string.touch_sensitive_turn_off_cancel, (DialogInterface.OnClickListener) null).create() : super.onCreateDialog(i);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        if (this.mPaperModeEnabledObserver != null) {
            getActivity().getContentResolver().unregisterContentObserver(this.mPaperModeEnabledObserver);
            this.mPaperModeEnabledObserver = null;
        }
        if (this.mMonochromeModeEnabledObserver != null) {
            getActivity().getContentResolver().unregisterContentObserver(this.mMonochromeModeEnabledObserver);
            this.mMonochromeModeEnabledObserver = null;
        }
        if (this.mAODObserver != null) {
            getActivity().getContentResolver().unregisterContentObserver(this.mAODObserver);
            this.mAODObserver = null;
        }
        CheckBoxPreference checkBoxPreference = this.mTouchSensitive;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(null);
        }
        CheckBoxPreference checkBoxPreference2 = this.mLineBreaking;
        if (checkBoxPreference2 != null) {
            checkBoxPreference2.setOnPreferenceChangeListener(null);
        }
        CheckBoxPreference checkBoxPreference3 = this.mRotatePreference;
        if (checkBoxPreference3 != null) {
            checkBoxPreference3.setOnPreferenceChangeListener(null);
        }
        super.onDestroy();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        RotationPolicy.unregisterRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
        super.onPause();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("touch_sensitive".equals(key)) {
            updateTouchSensitivePreference(((Boolean) obj).booleanValue());
            return true;
        } else if ("line_breaking".equals(key)) {
            updateLineBreakingPreference(((Boolean) obj).booleanValue());
            return true;
        } else if ("auto_rotate".equals(key)) {
            updateRotatePreference(((Boolean) obj).booleanValue());
            return true;
        } else if ("animate_settings_key".equals(key)) {
            updateAnimateStatus(((Boolean) obj).booleanValue());
            return true;
        } else {
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == this.mFontSettingsPref) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setData(Uri.parse("theme://zhuti.xiaomi.com/list?S.REQUEST_RESOURCE_CODE=fonts&miback=true&miref=" + this.mContext.getPackageName()));
            intent.putExtra(":miui:starting_window_label", "");
            if (SettingsFeatures.isFoldDevice()) {
                intent.setComponent(new ComponentName("com.android.thememanager", "com.android.thememanager.activity.ThemeTabActivity"));
            }
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException unused) {
                Toast.makeText(this.mContext, R.string.thememanager_not_found, 0);
            }
            InternationalCompat.trackReportEvent("setting_Display");
        }
        if ((RegionUtils.IS_JP_KDDI || RegionUtils.IS_JP_SB) && "page_layout_settings".equals(preference.getKey())) {
            Intent intent2 = new Intent();
            intent2.setAction("android.settings.ACCESSIBILITY_SETTINGS_FOR_SUW");
            intent2.addCategory("android.intent.category.DEFAULT");
            intent2.putExtra("isSetupFlow", true);
            intent2.setComponent(new ComponentName("com.android.settings", "com.android.settings.FontSizeSettingsForSetupWizardActivity"));
            startActivity(intent2);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updatePaperMode(MiuiUtils.supportPaperEyeCare() ? this.mAdvancedPaperModePref : this.mPaperModePref);
        updateFontSettings();
        updateMonochromeMode();
        RotationPolicy.registerRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
        updateAccelerometerRotationCheckbox();
        updateDarkMode(this.mDarkModeRadioTimePref);
        updateDarkModeMoreSettings();
        updateAllowAllRotations();
    }
}
