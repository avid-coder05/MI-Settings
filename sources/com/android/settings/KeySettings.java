package com.android.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.ContextThemeWrapper;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.report.InternationalCompat;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.util.ArrayList;
import java.util.Iterator;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.content.res.ThemeResources;
import miui.os.Build;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class KeySettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private ValuePreference launchRecents;
    private Preference mAiButtonPreference;
    private Preference mBackTapPreference;
    private PreferenceCategory mCustomGesture;
    private CheckBoxPreference mFpNavCenterToHome;
    private PreferenceCategory mFunctionShortCut;
    private DropDownPreference mMenuPress;
    private DropDownPreference mScreenKeyPosition;
    private CheckBoxPreference mSingleKeyUse;
    private CheckBoxPreference mWakeUpVoiceAssistant;
    private Resources resources;
    private ValuePreference showMenu;
    private ArrayMap<String, ValuePreference> mFunctionPreferences = new ArrayMap<>();
    private ArrayMap<String, Preference> mCustomGesturePreferences = new ArrayMap<>();

    private void addPowerGuide() {
        final View inflate = View.inflate(new ContextThemeWrapper(getActivity(), 16973931), R.layout.power_guide, null);
        inflate.setFocusableInTouchMode(true);
        inflate.requestFocus();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2, 218103808, -3);
        layoutParams.layoutInDisplayCutoutMode = 1;
        final WindowManager windowManager = (WindowManager) getActivity().getSystemService("window");
        windowManager.addView(inflate, layoutParams);
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", ThemeResources.FRAMEWORK_PACKAGE);
        int dimensionPixelSize = identifier > 0 ? getResources().getDimensionPixelSize(identifier) : 0;
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.power_guide);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams2.topMargin = (int) (dimensionPixelSize + getResources().getDimension(R.dimen.power_guide_out_margin));
        linearLayout.setLayoutParams(layoutParams2);
        ((TextView) inflate.findViewById(R.id.start_enjoy)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.KeySettings.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                windowManager.removeView(inflate);
            }
        });
        inflate.setOnKeyListener(new View.OnKeyListener() { // from class: com.android.settings.KeySettings.2
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 4 && keyEvent.getAction() == 1) {
                    windowManager.removeView(inflate);
                    return true;
                }
                return false;
            }
        });
    }

    private boolean hasNavigationBar() {
        try {
            return IWindowManager.Stub.asInterface(ServiceManager.getService("window")).hasNavigationBar(0);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateState(boolean z) {
        boolean z2 = MiuiSettings.System.getBoolean(getContentResolver(), "screen_key_press_app_switch", true);
        DropDownPreference dropDownPreference = this.mMenuPress;
        dropDownPreference.setValue((String) (z2 ? dropDownPreference.getEntryValues()[0] : dropDownPreference.getEntryValues()[1]));
        DropDownPreference dropDownPreference2 = this.mMenuPress;
        dropDownPreference2.setSummary(dropDownPreference2.getEntry());
        for (ValuePreference valuePreference : this.mFunctionPreferences.values()) {
            valuePreference.setShowRightArrow(true);
            valuePreference.setValue(R.string.key_none);
        }
        String[] stringArray = this.resources.getStringArray(R.array.key_and_gesture_shortcut_action);
        ArrayList arrayList = new ArrayList();
        if (!hasNavigationBar()) {
            arrayList.add("long_press_menu_key_when_lock");
        }
        arrayList.add("long_press_power_key");
        arrayList.add("double_click_power_key");
        arrayList.add("three_gesture_down");
        arrayList.add("three_gesture_long_press");
        for (String str : stringArray) {
            arrayList.add(str);
        }
        boolean z3 = MiuiSettings.Global.getBoolean(getActivity().getContentResolver(), "force_fsg_nav_bar");
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            String keyAndGestureShortcutFunction = MiuiSettings.Key.getKeyAndGestureShortcutFunction(getContext(), str2);
            if (!(z2 && "launch_recents".equals(keyAndGestureShortcutFunction)) && (z2 || !"show_menu".equals(keyAndGestureShortcutFunction))) {
                ValuePreference valuePreference2 = this.mFunctionPreferences.get(keyAndGestureShortcutFunction);
                if (valuePreference2 != null && (!z3 || "double_click_power_key".equals(str2) || "long_press_power_key".equals(str2) || "three_gesture_down".equals(str2) || "three_gesture_long_press".equals(str2) || "key_none".equals(str2))) {
                    if (str2.equals("three_gesture_long_press")) {
                        valuePreference2.setValue(String.format(this.resources.getString(R.string.three_gesture_long_press), 3));
                    } else {
                        valuePreference2.setValue(this.resources.getIdentifier(str2, "string", getActivity().getPackageName()));
                    }
                }
            } else {
                Settings.System.putStringForUser(getContentResolver(), str2, MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
            }
        }
        if (this.mFpNavCenterToHome != null) {
            this.mFpNavCenterToHome.setChecked(Settings.System.getInt(getContentResolver(), "fingerprint_nav_center_action", 0) == 1);
        }
        if (this.mSingleKeyUse != null) {
            this.mSingleKeyUse.setChecked(Settings.System.getInt(getContentResolver(), "single_key_use_enable", 0) == 1);
        }
        if (this.mWakeUpVoiceAssistant != null) {
            this.mWakeUpVoiceAssistant.setChecked(Settings.System.getInt(getContentResolver(), "long_press_power_launch_xiaoai", 0) == 1);
        }
        if (this.mScreenKeyPosition != null) {
            this.mScreenKeyPosition.setValue(SystemProperties.get("persist.sys.handswap", "0"));
            DropDownPreference dropDownPreference3 = this.mScreenKeyPosition;
            dropDownPreference3.setSummary(dropDownPreference3.getEntry());
        }
        if (this.mAiButtonPreference == null || MiuiUtils.shouldShowAiButton()) {
            return;
        }
        getPreferenceScreen().removePreference(this.mAiButtonPreference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return KeySettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.resources = getActivity().getResources();
        addPreferencesFromResource(R.xml.key_settings);
        boolean z = MiuiSettings.System.getBoolean(getContentResolver(), "screen_key_press_app_switch", true);
        boolean isTSMClientInstalled = MiuiSettings.Key.isTSMClientInstalled(getActivity());
        boolean hasSystemFeature = getActivity().getPackageManager().hasSystemFeature("android.hardware.nfc");
        boolean hasSplitScreen = SettingsFeatures.hasSplitScreen();
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("function_shortcut");
        this.mFunctionShortCut = preferenceCategory;
        if (preferenceCategory != null) {
            ValuePreference valuePreference = (ValuePreference) preferenceCategory.findPreference("launch_camera");
            if (valuePreference != null) {
                this.mFunctionPreferences.put("launch_camera", valuePreference);
            }
            ValuePreference valuePreference2 = (ValuePreference) this.mFunctionShortCut.findPreference("screen_shot");
            if (valuePreference2 != null) {
                this.mFunctionPreferences.put("screen_shot", valuePreference2);
            }
            ValuePreference valuePreference3 = (ValuePreference) this.mFunctionShortCut.findPreference("partial_screen_shot");
            if (valuePreference3 != null) {
                if (MiuiShortcut$System.supportPartialScreenShot()) {
                    this.mFunctionPreferences.put("partial_screen_shot", valuePreference3);
                } else {
                    this.mFunctionShortCut.removePreference(valuePreference3);
                }
            }
            PreferenceCategory preferenceCategory2 = this.mFunctionShortCut;
            boolean z2 = Build.IS_GLOBAL_BUILD;
            ValuePreference valuePreference4 = (ValuePreference) preferenceCategory2.findPreference(z2 ? "launch_google_search" : "launch_voice_assistant");
            ValuePreference valuePreference5 = (ValuePreference) this.mFunctionShortCut.findPreference(z2 ? "launch_voice_assistant" : "launch_google_search");
            if (valuePreference4 != null) {
                this.mFunctionPreferences.put(z2 ? "launch_google_search" : "launch_voice_assistant", valuePreference4);
            }
            if (valuePreference5 != null) {
                this.mFunctionShortCut.removePreference(valuePreference5);
            }
            if (!z2 && !MiuiShortcut$System.hasVoiceAssist(getContext())) {
                this.mFunctionShortCut.removePreference(valuePreference4);
            }
            ValuePreference valuePreference6 = (ValuePreference) this.mFunctionShortCut.findPreference("launch_smarthome");
            if (valuePreference6 == null || !MiuiShortcut$System.hasSmartHome(getContext())) {
                this.mFunctionShortCut.removePreference(valuePreference6);
            } else {
                this.mFunctionPreferences.put("launch_smarthome", valuePreference6);
            }
            ValuePreference valuePreference7 = (ValuePreference) this.mFunctionShortCut.findPreference("go_to_sleep");
            if (valuePreference7 != null) {
                this.mFunctionPreferences.put("go_to_sleep", valuePreference7);
            }
            ValuePreference valuePreference8 = (ValuePreference) this.mFunctionShortCut.findPreference("turn_on_torch");
            if (valuePreference8 == null || !Build.hasCameraFlash(getActivity())) {
                this.mFunctionShortCut.removePreference(valuePreference8);
            } else {
                this.mFunctionPreferences.put("turn_on_torch", valuePreference8);
            }
            ValuePreference valuePreference9 = (ValuePreference) this.mFunctionShortCut.findPreference("close_app");
            if (valuePreference9 != null) {
                this.mFunctionPreferences.put("close_app", valuePreference9);
            }
            ValuePreference valuePreference10 = (ValuePreference) this.mFunctionShortCut.findPreference("split_screen");
            if (valuePreference10 != null) {
                if (hasSplitScreen) {
                    this.mFunctionPreferences.put("split_screen", valuePreference10);
                } else {
                    this.mFunctionShortCut.removePreference(valuePreference10);
                }
            }
            ValuePreference valuePreference11 = (ValuePreference) this.mFunctionShortCut.findPreference("mi_pay");
            if (valuePreference11 != null) {
                valuePreference11.setSummary(hasSystemFeature ? R.string.mi_pay_summary : R.string.mi_pay_summary_without_nfc);
                if (isTSMClientInstalled) {
                    this.mFunctionPreferences.put("mi_pay", valuePreference11);
                } else {
                    this.mFunctionShortCut.removePreference(valuePreference11);
                }
            }
            ValuePreference valuePreference12 = (ValuePreference) this.mFunctionShortCut.findPreference("dump_log");
            if (valuePreference12 != null) {
                if (Build.IS_STABLE_VERSION) {
                    this.mFunctionShortCut.removePreference(valuePreference12);
                } else {
                    this.mFunctionPreferences.put("dump_log", valuePreference12);
                }
            }
            ValuePreference valuePreference13 = (ValuePreference) this.mFunctionShortCut.findPreference("au_pay");
            if (valuePreference13 != null) {
                if ("XIG02".equals(android.os.Build.DEVICE)) {
                    this.mFunctionPreferences.put("au_pay", valuePreference13);
                } else {
                    this.mFunctionShortCut.removePreference(valuePreference13);
                }
            }
            ValuePreference valuePreference14 = (ValuePreference) this.mFunctionShortCut.findPreference("google_pay");
            if (valuePreference14 != null) {
                this.mFunctionShortCut.removePreference(valuePreference14);
            }
            ValuePreference valuePreference15 = (ValuePreference) this.mFunctionShortCut.findPreference("show_menu");
            this.showMenu = valuePreference15;
            if (valuePreference15 != null) {
                this.mFunctionPreferences.put("show_menu", valuePreference15);
            }
            ValuePreference valuePreference16 = (ValuePreference) this.mFunctionShortCut.findPreference("launch_recents");
            this.launchRecents = valuePreference16;
            if (valuePreference16 != null) {
                this.mFunctionPreferences.put("launch_recents", valuePreference16);
            }
            if (z) {
                this.mFunctionShortCut.removePreference(this.launchRecents);
            } else {
                this.mFunctionShortCut.removePreference(this.showMenu);
            }
        }
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("menu_press");
        this.mMenuPress = dropDownPreference;
        dropDownPreference.setOnPreferenceChangeListener(this);
        this.mCustomGesture = (PreferenceCategory) findPreference("custom_gesture");
        this.mBackTapPreference = findPreference("back_tap");
        if (SettingsFeatures.hasBackTapSensorFeature(getContext())) {
            this.mCustomGesturePreferences.put("back_tap", this.mBackTapPreference);
        } else {
            this.mCustomGesture.removePreference(this.mBackTapPreference);
        }
        if (this.mCustomGesturePreferences.size() == 0) {
            getPreferenceScreen().removePreference(this.mCustomGesture);
        }
        PreferenceCategory preferenceCategory3 = (PreferenceCategory) findPreference("key_position_cat");
        if (FeatureParser.getBoolean("support_screen_key_swap", false)) {
            DropDownPreference dropDownPreference2 = (DropDownPreference) preferenceCategory3.findPreference("screen_key_position");
            this.mScreenKeyPosition = dropDownPreference2;
            dropDownPreference2.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(preferenceCategory3);
        }
        PreferenceCategory preferenceCategory4 = (PreferenceCategory) findPreference("convenience_key");
        this.mFpNavCenterToHome = (CheckBoxPreference) preferenceCategory4.findPreference("pref_fingerprint_nav_center_to_home");
        this.mSingleKeyUse = (CheckBoxPreference) preferenceCategory4.findPreference("pref_single_key_use");
        this.mFpNavCenterToHome.setOnPreferenceChangeListener(this);
        this.mSingleKeyUse.setOnPreferenceChangeListener(this);
        if (!FeatureParser.getBoolean("support_tap_fingerprint_sensor_to_home", false)) {
            preferenceCategory4.removePreference(this.mFpNavCenterToHome);
            this.mFpNavCenterToHome = null;
            preferenceCategory4.removePreference(this.mSingleKeyUse);
            this.mSingleKeyUse = null;
        }
        this.mAiButtonPreference = findPreference("ai_settings");
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preferenceCategory4.findPreference("the_way_of_wakeup_voice_assistant");
        this.mWakeUpVoiceAssistant = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        if (!Build.IS_GLOBAL_BUILD || !hasNavigationBar()) {
            preferenceCategory4.removePreference(this.mWakeUpVoiceAssistant);
            this.mWakeUpVoiceAssistant = null;
        }
        InternationalCompat.trackReportEvent("setting_Additional_settings_btnshortcut");
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ValuePreference valuePreference;
        if (preference == this.mScreenKeyPosition) {
            String str = (String) obj;
            SystemProperties.set("persist.sys.handswap", str);
            this.mScreenKeyPosition.setValue(str);
            DropDownPreference dropDownPreference = this.mScreenKeyPosition;
            dropDownPreference.setSummary(dropDownPreference.getEntry());
        } else {
            DropDownPreference dropDownPreference2 = this.mMenuPress;
            if (preference == dropDownPreference2) {
                String str2 = (String) obj;
                dropDownPreference2.setValue(str2);
                DropDownPreference dropDownPreference3 = this.mMenuPress;
                dropDownPreference3.setSummary(dropDownPreference3.getEntry());
                boolean equals = TextUtils.equals(str2, this.mMenuPress.getEntryValues()[0]);
                MiuiSettings.System.putBoolean(getContentResolver(), "screen_key_press_app_switch", equals);
                if (equals) {
                    this.mFunctionShortCut.addPreference(this.showMenu);
                    this.mFunctionShortCut.removePreference(this.launchRecents);
                    valuePreference = this.mFunctionPreferences.get("launch_recents");
                } else {
                    this.mFunctionShortCut.addPreference(this.launchRecents);
                    this.mFunctionShortCut.removePreference(this.showMenu);
                    valuePreference = this.mFunctionPreferences.get("show_menu");
                }
                String resourceEntryName = this.resources.getResourceEntryName(valuePreference.getValueRes());
                if (!MiCloudStatusInfo.QuotaInfo.WARN_NONE.equals(resourceEntryName)) {
                    valuePreference.setValue(R.string.key_none);
                    Settings.System.putStringForUser(getContentResolver(), resourceEntryName, MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
                }
            } else if (preference == this.mFpNavCenterToHome) {
                Settings.System.putInt(getContentResolver(), "fingerprint_nav_center_action", ((Boolean) obj).booleanValue() ? 1 : 0);
            } else if (preference == this.mSingleKeyUse) {
                Settings.System.putInt(getContentResolver(), "single_key_use_enable", ((Boolean) obj).booleanValue() ? 1 : 0);
            } else if (preference == this.mWakeUpVoiceAssistant) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                Settings.System.putInt(getContentResolver(), "long_press_power_launch_xiaoai", booleanValue ? 1 : 0);
                if (booleanValue) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("power_key_guide", 0);
                    if (!sharedPreferences.getBoolean("power_key_guide_already_shown", false)) {
                        addPowerGuide();
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putBoolean("power_key_guide_already_shown", true);
                        edit.apply();
                    }
                }
            }
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateState(true);
    }
}
