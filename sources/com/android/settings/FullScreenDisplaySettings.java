package com.android.settings;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import android.view.IWindowManager;
import android.view.IWindowManagerCompat;
import android.view.View;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;
import com.android.internal.content.PackageMonitor;
import com.android.settings.SimpleDialogFragment;
import com.android.settings.search.tree.FullScreenDisplaySettingsTree;
import com.android.settings.utils.AnalyticsUtils;
import com.android.settings.view.NavigationBarGuideView;
import java.util.ArrayList;
import java.util.List;
import miui.content.res.ThemeResources;
import miui.yellowpage.YellowPageStatistic;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class FullScreenDisplaySettings extends MiuiSettingsPreferenceFragment {
    private AlertDialog mAlertDialog;
    private Preference mAppGuide;
    private Preference mAppQuickSwitchGuide;
    private CheckBoxPreference mAppSwitchFeature;
    private Preference mAppSwitchGuide;
    private Preference mAutoDisableScreenButtons;
    private Preference mBackGuide;
    private boolean mClickOnDialog;
    private Context mContext;
    private boolean mDemoExistes;
    private PreferenceCategory mGuideCategory;
    private boolean mHasCheckedDemo;
    private CheckBoxPreference mHideGestureLine;
    private Preference mHomeGuide;
    private boolean mIsRecentsWithinLauncher;
    private Preference mKeyShortcutSettings;
    private CheckBoxPreference mMistakeTouch;
    private boolean mNeedShowDialog;
    private LauncherPackageMonitor mPackageMonitor;
    private Preference mRecentGuide;
    private CheckBoxPreference mScreenButtonHide;
    private PreferenceCategory mSettingCategory;
    private SharedPreferences mSharedPreferences;
    private CheckBoxPreference mSwitchScreenButtonOrder;
    private boolean mUseMiuiHomeAsDefaultHome;
    private final BroadcastReceiver mUserPreferenceChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.FullScreenDisplaySettings.12
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean useMiuiHomeAsDefaultHome = com.android.settings.utils.Utils.useMiuiHomeAsDefaultHome(FullScreenDisplaySettings.this.mContext);
            if (FullScreenDisplaySettings.this.mUseMiuiHomeAsDefaultHome != useMiuiHomeAsDefaultHome) {
                FullScreenDisplaySettings.this.mUseMiuiHomeAsDefaultHome = useMiuiHomeAsDefaultHome;
                FullScreenDisplaySettings.this.updateHideGesturePreference();
                FullScreenDisplaySettings.this.updateGestureLineOfNavBarGuideView();
            }
        }
    };
    private ContentObserver mScreenButtonHideListener = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.FullScreenDisplaySettings.14
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            FullScreenDisplaySettings.this.updatePrefence();
        }
    };

    /* loaded from: classes.dex */
    private class LauncherPackageMonitor extends PackageMonitor {
        private LauncherPackageMonitor() {
        }

        public void onPackageAdded(String str, int i) {
            onPackageModified(str);
        }

        public boolean onPackageChanged(String str, int i, String[] strArr) {
            onPackageModified(str);
            return true;
        }

        public void onPackageModified(String str) {
            boolean isRecentsWithinLauncher;
            if (str == null || !FullScreenDisplaySettings.this.isMatchDefaultHome(str) || FullScreenDisplaySettings.this.mIsRecentsWithinLauncher == (isRecentsWithinLauncher = com.android.settings.utils.Utils.isRecentsWithinLauncher(FullScreenDisplaySettings.this.mContext))) {
                return;
            }
            FullScreenDisplaySettings.this.mIsRecentsWithinLauncher = isRecentsWithinLauncher;
            FullScreenDisplaySettings.this.updateHideGesturePreference();
            FullScreenDisplaySettings.this.updateGestureLineOfNavBarGuideView();
        }

        public void onPackageRemoved(String str, int i) {
            onPackageModified(str);
        }
    }

    private void addHideGesturePreference() {
        this.mSettingCategory.addPreference(this.mHideGestureLine);
        this.mSettingCategory.removePreference(this.mAppSwitchFeature);
        this.mGuideCategory.addPreference(this.mAppQuickSwitchGuide);
        updateAppQuickSwitchGuide();
        this.mGuideCategory.removePreference(this.mAppSwitchGuide);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkDemoExist() {
        if (!this.mHasCheckedDemo) {
            this.mHasCheckedDemo = true;
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.HomeDemoAct"));
            intent.putExtra("DEMO_TYPE", "DEMO_TO_HOME");
            if (getContext().getPackageManager().resolveActivity(intent, 0) != null) {
                this.mDemoExistes = true;
            }
        }
        return this.mDemoExistes;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createDialog() {
        this.mClickOnDialog = false;
        AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle(R.string.navigation_guide_gesture_line_dialog_title).setMessage(R.string.navigation_guide_gesture_line_dialog_summary).setCheckBox(false, this.mContext.getResources().getString(R.string.navigation_guide_dialog_dont_show_again)).setPositiveButton(R.string.navigation_guide_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.FullScreenDisplaySettings.16
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                FullScreenDisplaySettings.this.mClickOnDialog = true;
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.AppQuickSwitchActivity"));
                    FullScreenDisplaySettings.this.mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("FullScreenDisplaySettings", "not fullscreen phone but configed fsgmode : " + e);
                }
            }
        }).setNeutralButton(R.string.navigation_guide_dialog_skip, new DialogInterface.OnClickListener() { // from class: com.android.settings.FullScreenDisplaySettings.15
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                FullScreenDisplaySettings.this.mClickOnDialog = true;
            }
        }).create();
        this.mAlertDialog = create;
        create.setCanceledOnTouchOutside(true);
        this.mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.FullScreenDisplaySettings.17
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (FullScreenDisplaySettings.this.mClickOnDialog && FullScreenDisplaySettings.this.mAlertDialog != null) {
                    FullScreenDisplaySettings.this.mNeedShowDialog = !r3.mAlertDialog.isChecked();
                    FullScreenDisplaySettings.this.mSharedPreferences.edit().putBoolean("need_show_gesture_line_guide", FullScreenDisplaySettings.this.mNeedShowDialog).apply();
                }
                FullScreenDisplaySettings fullScreenDisplaySettings = FullScreenDisplaySettings.this;
                fullScreenDisplaySettings.setHideGestureLine(fullScreenDisplaySettings.mHideGestureLine.isChecked());
                FullScreenDisplaySettings.this.mAlertDialog = null;
            }
        });
    }

    private void initAppGuide() {
        Preference findPreference = getPreferenceScreen().findPreference("navigation_guide_app");
        this.mAppGuide = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.FullScreenDisplaySettings.4
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    AnalyticsUtils.trackClickSingleTurorialEvent(FullScreenDisplaySettings.this.mContext, "in_app_function");
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.DrawerDemoAct"));
                    FullScreenDisplaySettings.this.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initAppQuickSwitchGuide() {
        Preference findPreference = getPreferenceScreen().findPreference("navigation_guide_app_quick_switch");
        this.mAppQuickSwitchGuide = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.FullScreenDisplaySettings.6
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    AnalyticsUtils.trackClickSingleTurorialEvent(FullScreenDisplaySettings.this.mContext, "quick_switch");
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.AppQuickSwitchActivity"));
                    FullScreenDisplaySettings.this.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initAppSwitchFeature() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("navigation_appswitch_anim");
        this.mAppSwitchFeature = checkBoxPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.FullScreenDisplaySettings.10
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    FullScreenDisplaySettings.this.setAppSwitchFeatureEnable(((Boolean) obj).booleanValue());
                    return true;
                }
            });
        }
    }

    private void initAppSwitchGuide() {
        Preference findPreference = getPreferenceScreen().findPreference("navigation_guide_appswitch");
        this.mAppSwitchGuide = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.FullScreenDisplaySettings.5
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    if (FullScreenDisplaySettings.this.isAppSwitchFeatureEnable()) {
                        return true;
                    }
                    Toast.makeText(FullScreenDisplaySettings.this.mContext, FullScreenDisplaySettings.this.mContext.getResources().getString(R.string.navigation_guide_appswitch_click), 0).show();
                    return true;
                }
            });
        }
    }

    private void initAutoDisableScreenButtons() {
        this.mAutoDisableScreenButtons = getPreferenceScreen().findPreference("audo_disable_screen_buttons_settings");
        if (isSupportGestureSettings()) {
            return;
        }
        this.mSettingCategory.removePreference(this.mAutoDisableScreenButtons);
    }

    private void initBackGuide() {
        Preference findPreference = getPreferenceScreen().findPreference("navigation_guide_back");
        this.mBackGuide = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.FullScreenDisplaySettings.3
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    AnalyticsUtils.trackClickSingleTurorialEvent(FullScreenDisplaySettings.this.mContext, "back");
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.FsGestureBackDemoActivity"));
                    intent.putExtra("DEMO_TYPE", "FSG_BACK_GESTURE");
                    intent.putExtra("DEMO_STEP", 1);
                    FullScreenDisplaySettings.this.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initHideGestureLine() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("navigation_hide_gesture_line");
        this.mHideGestureLine = checkBoxPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.FullScreenDisplaySettings.11
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    boolean booleanValue = ((Boolean) obj).booleanValue();
                    if (!booleanValue || !FullScreenDisplaySettings.this.mNeedShowDialog || !FullScreenDisplaySettings.this.checkDemoExist()) {
                        FullScreenDisplaySettings.this.setHideGestureLine(booleanValue);
                        return true;
                    }
                    FullScreenDisplaySettings.this.createDialog();
                    FullScreenDisplaySettings.this.mAlertDialog.show();
                    AnalyticsUtils.trackLearnGesturesWindowEvent(FullScreenDisplaySettings.this.mContext);
                    return true;
                }
            });
        }
    }

    private void initHomeGuide() {
        Preference findPreference = getPreferenceScreen().findPreference("navigation_guide_home");
        this.mHomeGuide = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.FullScreenDisplaySettings.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    AnalyticsUtils.trackClickSingleTurorialEvent(FullScreenDisplaySettings.this.mContext, YellowPageStatistic.Display.HOME);
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.HomeDemoAct"));
                    intent.putExtra("DEMO_TYPE", "DEMO_TO_HOME");
                    FullScreenDisplaySettings.this.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initKeyShortcutSettings() {
        this.mKeyShortcutSettings = getPreferenceScreen().findPreference("key_shortcut_settings");
        if (isSupportGestureSettings()) {
            return;
        }
        this.mSettingCategory.removePreference(this.mKeyShortcutSettings);
    }

    private void initMistakeTouch() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("fsg_mistake_touch");
        this.mMistakeTouch = checkBoxPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.FullScreenDisplaySettings.9
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    FullScreenDisplaySettings.this.setMistakeTouchEnable(((Boolean) obj).booleanValue());
                    return true;
                }
            });
        }
    }

    private void initRecentGuide() {
        Preference findPreference = getPreferenceScreen().findPreference("navigation_guide_recent");
        this.mRecentGuide = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.FullScreenDisplaySettings.2
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    AnalyticsUtils.trackClickSingleTurorialEvent(FullScreenDisplaySettings.this.mContext, "recents");
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.HomeDemoAct"));
                    intent.putExtra("DEMO_TYPE", "DEMO_TO_RECENTTASK");
                    FullScreenDisplaySettings.this.startActivity(intent);
                    return true;
                }
            });
        }
    }

    private void initScreenButtonHide() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("screen_button_hide");
        this.mScreenButtonHide = checkBoxPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.FullScreenDisplaySettings.7
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    if (((Boolean) obj).booleanValue()) {
                        FullScreenDisplaySettings.this.showForceImmersiveHintDialog();
                        return true;
                    }
                    FullScreenDisplaySettings.this.setScreenButtonHidden(false);
                    return true;
                }
            });
        }
    }

    private void initSwitchScreenButtonOrder() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference(FullScreenDisplaySettingsTree.SWITCH_SCREEN_BUTTON_ORDER);
        this.mSwitchScreenButtonOrder = checkBoxPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.FullScreenDisplaySettings.8
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    FullScreenDisplaySettings.this.setRightHand(((Boolean) obj).booleanValue());
                    return true;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAppSwitchFeatureEnable() {
        return Settings.Global.getInt(getContentResolver(), "show_gesture_appswitch_feature", 0) != 0;
    }

    private boolean isHideGestureLine() {
        return Settings.Global.getInt(getContentResolver(), "hide_gesture_line", 0) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isMatchDefaultHome(String str) {
        return "com.miui.home".equals(str) || "com.mi.android.globallauncher".equals(str);
    }

    private boolean isMistakeTouchEnable() {
        return Settings.Global.getInt(getContentResolver(), "show_mistake_touch_toast", 1) != 0;
    }

    private boolean isScreenButtonHidden() {
        return MiuiSettings.Global.getBoolean(getContentResolver(), "force_fsg_nav_bar");
    }

    private boolean isSupportGestureSettings() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "show_key_shortcuts_entry_in_full_screen_settings", 0) == 1;
    }

    private boolean isUseFsVersionThree() {
        return Build.VERSION.SDK_INT >= 29 && this.mIsRecentsWithinLauncher && this.mUseMiuiHomeAsDefaultHome;
    }

    private void removeHideGesturePreference() {
        this.mSettingCategory.removePreference(this.mHideGestureLine);
        this.mSettingCategory.addPreference(this.mAppSwitchFeature);
        this.mGuideCategory.removePreference(this.mAppQuickSwitchGuide);
        this.mGuideCategory.addPreference(this.mAppSwitchGuide);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAppSwitchFeatureEnable(boolean z) {
        Settings.Global.putInt(getContentResolver(), "show_gesture_appswitch_feature", z ? 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHideGestureLine(boolean z) {
        Settings.Global.putInt(getContentResolver(), "hide_gesture_line", z ? 1 : 0);
        updateAppQuickSwitchGuide();
        updateGestureLineOfNavBarGuideView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMistakeTouchEnable(boolean z) {
        Settings.Global.putInt(getContentResolver(), "show_mistake_touch_toast", z ? 1 : 0);
    }

    private void setupForceImmersiveHintDialog(SimpleDialogFragment simpleDialogFragment) {
        simpleDialogFragment.setNegativeButton(R.string.force_immersive_compatibility_dont_hide, null);
        simpleDialogFragment.setPositiveButton(R.string.force_immersive_compatibility_hide, new DialogInterface.OnClickListener() { // from class: com.android.settings.FullScreenDisplaySettings.13
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                FullScreenDisplaySettings.this.mScreenButtonHide.setChecked(true);
                FullScreenDisplaySettings.this.getActivity().getWindow().getDecorView().postDelayed(new Runnable() { // from class: com.android.settings.FullScreenDisplaySettings.13.1
                    @Override // java.lang.Runnable
                    public void run() {
                        FullScreenDisplaySettings.this.setScreenButtonHidden(true);
                    }
                }, 300L);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showForceImmersiveHintDialog() {
        this.mScreenButtonHide.setChecked(false);
        SimpleDialogFragment create = new SimpleDialogFragment.AlertDialogFragmentBuilder(1).setTitle(getString(R.string.force_immersive_compatibility_hint_title)).setMessage(getString(R.string.force_immersive_compatibility_hint_message)).create();
        setupForceImmersiveHintDialog(create);
        create.show(getFragmentManager(), "fragment_force_immersive_dialog");
    }

    private void updateAppQuickSwitchGuide() {
        if (isHideGestureLine()) {
            this.mAppQuickSwitchGuide.setSummary(R.string.navigation_guide_app_quick_switch_hide_line_summary);
            this.mAppQuickSwitchGuide.setIcon(R.drawable.navigation_bar_guide_new_appswitch_hide_gesture_line);
            return;
        }
        this.mAppQuickSwitchGuide.setSummary(R.string.navigation_guide_app_quick_switch_summary);
        this.mAppQuickSwitchGuide.setIcon(R.drawable.navigation_bar_guide_new_appswitch);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGestureLineOfNavBarGuideView() {
        NavigationBarGuideView navigationBarGuideView;
        View view = getView();
        if (view == null || (navigationBarGuideView = (NavigationBarGuideView) view.findViewById(R.id.navigation_guide)) == null) {
            return;
        }
        navigationBarGuideView.setIsShowGestureLine(isUseFsVersionThree() && !isHideGestureLine());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHideGesturePreference() {
        if (isUseFsVersionThree()) {
            addHideGesturePreference();
        } else {
            removeHideGesturePreference();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return FullScreenDisplaySettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getPageIndex() {
        return 1001;
    }

    boolean isRightHand() {
        ArrayList screenKeyOrder = MiuiSettings.System.getScreenKeyOrder(getActivity());
        return screenKeyOrder != null && screenKeyOrder.size() > 0 && ((Integer) screenKeyOrder.get(0)).intValue() == 2;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getContext();
        try {
            if (!IWindowManagerCompat.hasNavigationBar(IWindowManager.Stub.asInterface(ServiceManager.getService("window")), ContextCompat.getDisplayId(getContext()))) {
                finish();
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (getFragmentManager().findFragmentByTag("fragment_force_immersive_dialog") != null) {
            setupForceImmersiveHintDialog((SimpleDialogFragment) getFragmentManager().findFragmentByTag("fragment_force_immersive_dialog"));
        }
        addPreferencesFromResource(R.xml.fullscreen_display_settings);
        initScreenButtonHide();
        initSwitchScreenButtonOrder();
        initMistakeTouch();
        initAppSwitchFeature();
        initHideGestureLine();
        initHomeGuide();
        initRecentGuide();
        initBackGuide();
        initAppGuide();
        initAppSwitchGuide();
        initAppQuickSwitchGuide();
        this.mGuideCategory = (PreferenceCategory) getPreferenceScreen().findPreference("navigation_guide_category");
        this.mSettingCategory = (PreferenceCategory) getPreferenceScreen().findPreference("navigation_setting_category");
        initKeyShortcutSettings();
        initAutoDisableScreenButtons();
        checkDemoExist();
        if (!this.mDemoExistes) {
            getPreferenceScreen().removePreference(this.mGuideCategory);
        }
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext.getApplicationContext());
        this.mSharedPreferences = defaultSharedPreferences;
        this.mNeedShowDialog = defaultSharedPreferences.getBoolean("need_show_gesture_line_guide", true);
        this.mIsRecentsWithinLauncher = com.android.settings.utils.Utils.isRecentsWithinLauncher(this.mContext);
        this.mUseMiuiHomeAsDefaultHome = com.android.settings.utils.Utils.useMiuiHomeAsDefaultHome(this.mContext);
        if (Build.VERSION.SDK_INT >= 29) {
            LauncherPackageMonitor launcherPackageMonitor = new LauncherPackageMonitor();
            this.mPackageMonitor = launcherPackageMonitor;
            Context context = this.mContext;
            launcherPackageMonitor.register(context, context.getMainLooper(), UserHandle.ALL, true);
            this.mContext.registerReceiver(this.mUserPreferenceChangeReceiver, new IntentFilter("android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED"));
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        try {
            LauncherPackageMonitor launcherPackageMonitor = this.mPackageMonitor;
            if (launcherPackageMonitor != null) {
                launcherPackageMonitor.unregister();
            }
        } catch (Exception unused) {
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        NavigationBarGuideView navigationBarGuideView;
        super.onPause();
        View view = getView();
        if (view != null && (navigationBarGuideView = (NavigationBarGuideView) view.findViewById(R.id.navigation_guide)) != null) {
            navigationBarGuideView.onPause();
        }
        getContentResolver().unregisterContentObserver(this.mScreenButtonHideListener);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        NavigationBarGuideView navigationBarGuideView;
        super.onResume();
        if (getActivity() == null) {
            return;
        }
        CheckBoxPreference checkBoxPreference = this.mScreenButtonHide;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(isScreenButtonHidden());
        }
        CheckBoxPreference checkBoxPreference2 = this.mSwitchScreenButtonOrder;
        if (checkBoxPreference2 != null) {
            checkBoxPreference2.setChecked(isRightHand());
        }
        CheckBoxPreference checkBoxPreference3 = this.mMistakeTouch;
        if (checkBoxPreference3 != null) {
            checkBoxPreference3.setChecked(isMistakeTouchEnable());
        }
        CheckBoxPreference checkBoxPreference4 = this.mAppSwitchFeature;
        if (checkBoxPreference4 != null) {
            checkBoxPreference4.setChecked(isAppSwitchFeatureEnable());
        }
        CheckBoxPreference checkBoxPreference5 = this.mHideGestureLine;
        if (checkBoxPreference5 != null) {
            checkBoxPreference5.setChecked(isHideGestureLine());
        }
        View view = getView();
        if (view != null && (navigationBarGuideView = (NavigationBarGuideView) view.findViewById(R.id.navigation_guide)) != null) {
            navigationBarGuideView.onResume();
        }
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("force_fsg_nav_bar"), false, this.mScreenButtonHideListener, -1);
        updatePrefence();
    }

    void setRightHand(boolean z) {
        ArrayList arrayList = new ArrayList();
        if (z) {
            arrayList.add(2);
            arrayList.add(1);
            arrayList.add(3);
        } else {
            arrayList.add(3);
            arrayList.add(1);
            arrayList.add(2);
        }
        setScreenKeyOrder(arrayList);
    }

    void setScreenButtonHidden(boolean z) {
        MiuiSettings.Global.putBoolean(getContentResolver(), "force_fsg_nav_bar", z);
        Settings.Global.putString(getContentResolver(), "policy_control", "immersive.preconfirms=*");
    }

    public void setScreenKeyOrder(List<Integer> list) {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toString());
            sb.append(" ");
        }
        Settings.System.putString(getContentResolver(), "screen_key_order", sb.toString());
    }

    void updatePrefence() {
        if (isScreenButtonHidden()) {
            if (this.mDemoExistes) {
                getPreferenceScreen().addPreference(this.mGuideCategory);
            }
            this.mSettingCategory.addPreference(this.mMistakeTouch);
            updateHideGesturePreference();
            this.mSettingCategory.removePreference(this.mSwitchScreenButtonOrder);
            this.mSettingCategory.removePreference(this.mKeyShortcutSettings);
            this.mSettingCategory.removePreference(this.mAutoDisableScreenButtons);
            return;
        }
        if (isSupportGestureSettings()) {
            this.mSettingCategory.addPreference(this.mKeyShortcutSettings);
            this.mSettingCategory.addPreference(this.mAutoDisableScreenButtons);
        }
        getPreferenceScreen().removePreference(this.mGuideCategory);
        this.mSettingCategory.addPreference(this.mSwitchScreenButtonOrder);
        this.mSettingCategory.removePreference(this.mAppSwitchFeature);
        this.mSettingCategory.removePreference(this.mHideGestureLine);
        this.mSettingCategory.removePreference(this.mMistakeTouch);
    }
}
