package com.android.settings;

import android.content.ComponentName;
import android.content.ContextCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.view.IWindowManager;
import android.view.IWindowManagerCompat;
import android.view.View;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.SimpleDialogFragment;
import com.android.settings.search.tree.FullScreenDisplaySettingsTree;
import com.android.settings.utils.AnalyticsUtils;
import com.android.settings.view.NavigationBarGuideView;
import java.util.ArrayList;
import java.util.List;
import miui.content.res.ThemeResources;
import miui.util.CustomizeUtil;
import miui.yellowpage.YellowPageStatistic;

/* loaded from: classes.dex */
public class InfinityDisplaySettings extends MiuiSettingsPreferenceFragment {
    private Preference mAppGuide;
    private CheckBoxPreference mAppSwitchFeature;
    private Preference mAppSwitchGuide;
    private Preference mAspectRatio;
    private Preference mBackGuide;
    private Preference mCutoutMode;
    private CheckBoxPreference mCutoutType;
    private boolean mDemoExistes;
    private PreferenceCategory mGuideCategory;
    private boolean mHasCheckedDemo;
    private Preference mHomeGuide;
    private CheckBoxPreference mMistakeTouch;
    private CheckBoxPreference mNotchForceBlack;
    private Preference mRecentGuide;
    private CheckBoxPreference mScreenButtonHide;
    private ContentObserver mScreenButtonHideListener = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.InfinityDisplaySettings.2
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            InfinityDisplaySettings.this.updatePrefence();
        }
    };
    private PreferenceCategory mSettingCategory;
    private CheckBoxPreference mSwitchScreenButtonOrder;

    private boolean checkDemoExist() {
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

    private boolean isAppSwitchFeatureEnable() {
        return Settings.Global.getInt(getContentResolver(), "show_gesture_appswitch_feature", 0) != 0;
    }

    private boolean isDripType() {
        return Settings.Global.getInt(getContentResolver(), "overlay_drip", 1) == 1;
    }

    private boolean isForceBlack() {
        return supportForceBlackV2() ? MiuiSettings.Global.getBoolean(getContentResolver(), "force_black_v2") : MiuiSettings.Global.getBoolean(getContentResolver(), "force_black");
    }

    private boolean isMistakeTouchEnable() {
        return Settings.Global.getInt(getContentResolver(), "show_mistake_touch_toast", 1) != 0;
    }

    private boolean isScreenButtonHidden() {
        return MiuiSettings.Global.getBoolean(getContentResolver(), "force_fsg_nav_bar");
    }

    private void setAppSwitchFeatureEnable(boolean z) {
        Settings.Global.putInt(getContentResolver(), "show_gesture_appswitch_feature", z ? 1 : 0);
    }

    private void setDripType(boolean z) {
        Settings.Global.putInt(getContentResolver(), "overlay_drip", z ? 1 : 0);
    }

    private void setForceBlack(boolean z) {
        if (supportForceBlackV2()) {
            MiuiSettings.Global.putBoolean(getContentResolver(), "force_black_v2", z);
        } else {
            MiuiSettings.Global.putBoolean(getContentResolver(), "force_black", z);
        }
    }

    private void setMistakeTouchEnable(boolean z) {
        Settings.Global.putInt(getContentResolver(), "show_mistake_touch_toast", z ? 1 : 0);
    }

    private void setupForceImmersiveHintDialog(SimpleDialogFragment simpleDialogFragment) {
        simpleDialogFragment.setNegativeButton(R.string.force_immersive_compatibility_dont_hide, null);
        simpleDialogFragment.setPositiveButton(R.string.force_immersive_compatibility_hide, new DialogInterface.OnClickListener() { // from class: com.android.settings.InfinityDisplaySettings.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                InfinityDisplaySettings.this.mScreenButtonHide.setChecked(true);
                InfinityDisplaySettings.this.getActivity().getWindow().getDecorView().postDelayed(new Runnable() { // from class: com.android.settings.InfinityDisplaySettings.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        InfinityDisplaySettings.this.setScreenButtonHidden(true);
                    }
                }, 300L);
            }
        });
    }

    private void showForceImmersiveHintDialog() {
        this.mScreenButtonHide.setChecked(false);
        SimpleDialogFragment create = new SimpleDialogFragment.AlertDialogFragmentBuilder(1).setTitle(getString(R.string.force_immersive_compatibility_hint_title)).setMessage(getString(R.string.force_immersive_compatibility_hint_message)).create();
        setupForceImmersiveHintDialog(create);
        create.show(getFragmentManager(), "fragment_force_immersive_dialog");
    }

    private boolean supportForceBlackV2() {
        return Build.VERSION.SDK_INT >= 28;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return InfinityDisplaySettings.class.getName();
    }

    boolean isRightHand() {
        ArrayList screenKeyOrder = MiuiSettings.System.getScreenKeyOrder(getActivity());
        return screenKeyOrder != null && screenKeyOrder.size() > 0 && ((Integer) screenKeyOrder.get(0)).intValue() == 2;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            if (!IWindowManagerCompat.hasNavigationBar(IWindowManager.Stub.asInterface(ServiceManager.getService("window")), ContextCompat.getDisplayId(getContext()))) {
                finish();
                return;
            }
        } catch (RemoteException unused) {
        }
        if (getFragmentManager().findFragmentByTag("fragment_force_immersive_dialog") != null) {
            setupForceImmersiveHintDialog((SimpleDialogFragment) getFragmentManager().findFragmentByTag("fragment_force_immersive_dialog"));
        }
        addPreferencesFromResource(R.xml.infinity_display_settings);
        this.mScreenButtonHide = (CheckBoxPreference) getPreferenceScreen().findPreference("screen_button_hide");
        this.mSwitchScreenButtonOrder = (CheckBoxPreference) getPreferenceScreen().findPreference(FullScreenDisplaySettingsTree.SWITCH_SCREEN_BUTTON_ORDER);
        this.mNotchForceBlack = (CheckBoxPreference) getPreferenceScreen().findPreference("notch_force_black");
        this.mMistakeTouch = (CheckBoxPreference) getPreferenceScreen().findPreference("fsg_mistake_touch");
        this.mAppSwitchFeature = (CheckBoxPreference) getPreferenceScreen().findPreference("navigation_appswitch_anim");
        this.mCutoutType = (CheckBoxPreference) getPreferenceScreen().findPreference("cutout_type");
        this.mCutoutMode = getPreferenceScreen().findPreference("cutout_mode");
        this.mHomeGuide = getPreferenceScreen().findPreference("navigation_guide_home");
        this.mRecentGuide = getPreferenceScreen().findPreference("navigation_guide_recent");
        this.mBackGuide = getPreferenceScreen().findPreference("navigation_guide_back");
        this.mAppGuide = getPreferenceScreen().findPreference("navigation_guide_app");
        this.mAspectRatio = getPreferenceScreen().findPreference("screen_max_aspect_ratio");
        this.mGuideCategory = (PreferenceCategory) getPreferenceScreen().findPreference("navigation_guide_category");
        this.mSettingCategory = (PreferenceCategory) getPreferenceScreen().findPreference("navigation_setting_category");
        this.mAppSwitchGuide = getPreferenceScreen().findPreference("navigation_guide_appswitch");
        checkDemoExist();
        if (this.mDemoExistes) {
            return;
        }
        getPreferenceScreen().removePreference(this.mGuideCategory);
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

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        CheckBoxPreference checkBoxPreference = this.mScreenButtonHide;
        if (preference != checkBoxPreference) {
            CheckBoxPreference checkBoxPreference2 = this.mSwitchScreenButtonOrder;
            if (preference == checkBoxPreference2) {
                setRightHand(checkBoxPreference2.isChecked());
            } else {
                CheckBoxPreference checkBoxPreference3 = this.mNotchForceBlack;
                if (preference == checkBoxPreference3) {
                    setForceBlack(checkBoxPreference3.isChecked());
                    Preference preference2 = this.mCutoutMode;
                    if (preference2 != null) {
                        preference2.setEnabled(!isForceBlack());
                    }
                    CheckBoxPreference checkBoxPreference4 = this.mCutoutType;
                    if (checkBoxPreference4 != null) {
                        checkBoxPreference4.setEnabled(!isForceBlack());
                    }
                } else {
                    CheckBoxPreference checkBoxPreference5 = this.mCutoutType;
                    if (preference == checkBoxPreference5) {
                        setDripType(checkBoxPreference5.isChecked());
                    } else if (preference == this.mHomeGuide) {
                        AnalyticsUtils.trackClickSingleTurorialEvent(getContext(), YellowPageStatistic.Display.HOME);
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.HomeDemoAct"));
                        intent.putExtra("DEMO_TYPE", "DEMO_TO_HOME");
                        startActivity(intent);
                    } else if (preference == this.mRecentGuide) {
                        AnalyticsUtils.trackClickSingleTurorialEvent(getContext(), "recents");
                        Intent intent2 = new Intent();
                        intent2.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.HomeDemoAct"));
                        intent2.putExtra("DEMO_TYPE", "DEMO_TO_RECENTTASK");
                        startActivity(intent2);
                    } else if (preference == this.mBackGuide) {
                        AnalyticsUtils.trackClickSingleTurorialEvent(getContext(), "back");
                        Intent intent3 = new Intent();
                        intent3.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.FsGestureBackDemoActivity"));
                        intent3.putExtra("DEMO_TYPE", "FSG_BACK_GESTURE");
                        intent3.putExtra("DEMO_STEP", 1);
                        startActivity(intent3);
                    } else if (preference == this.mAppGuide) {
                        AnalyticsUtils.trackClickSingleTurorialEvent(getContext(), "in_app_function");
                        Intent intent4 = new Intent();
                        intent4.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.DrawerDemoAct"));
                        startActivity(intent4);
                    } else {
                        CheckBoxPreference checkBoxPreference6 = this.mMistakeTouch;
                        if (preference == checkBoxPreference6) {
                            setMistakeTouchEnable(checkBoxPreference6.isChecked());
                        } else {
                            CheckBoxPreference checkBoxPreference7 = this.mAppSwitchFeature;
                            if (preference == checkBoxPreference7) {
                                setAppSwitchFeatureEnable(checkBoxPreference7.isChecked());
                            } else if (preference == this.mAppSwitchGuide && !isAppSwitchFeatureEnable()) {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.navigation_guide_appswitch_click), 0).show();
                            }
                        }
                    }
                }
            }
        } else if (checkBoxPreference.isChecked()) {
            showForceImmersiveHintDialog();
        } else {
            setScreenButtonHidden(false);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
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
        CheckBoxPreference checkBoxPreference3 = this.mNotchForceBlack;
        if (checkBoxPreference3 != null) {
            checkBoxPreference3.setChecked(isForceBlack());
            Preference preference = this.mCutoutMode;
            if (preference != null) {
                preference.setEnabled(!isForceBlack());
            }
            CheckBoxPreference checkBoxPreference4 = this.mCutoutType;
            if (checkBoxPreference4 != null) {
                checkBoxPreference4.setEnabled(!isForceBlack());
            }
        }
        CheckBoxPreference checkBoxPreference5 = this.mCutoutType;
        if (checkBoxPreference5 != null) {
            checkBoxPreference5.setChecked(isDripType());
        }
        CheckBoxPreference checkBoxPreference6 = this.mMistakeTouch;
        if (checkBoxPreference6 != null) {
            checkBoxPreference6.setChecked(isMistakeTouchEnable());
        }
        CheckBoxPreference checkBoxPreference7 = this.mAppSwitchFeature;
        if (checkBoxPreference7 != null) {
            checkBoxPreference7.setChecked(isAppSwitchFeatureEnable());
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
        boolean isScreenButtonHidden = isScreenButtonHidden();
        if (miui.os.Build.IS_TABLET) {
            this.mSettingCategory.removePreference(this.mAspectRatio);
        }
        if (isScreenButtonHidden) {
            if (this.mDemoExistes) {
                getPreferenceScreen().addPreference(this.mGuideCategory);
            }
            this.mSettingCategory.addPreference(this.mMistakeTouch);
            this.mSettingCategory.addPreference(this.mAppSwitchFeature);
            this.mSettingCategory.removePreference(this.mSwitchScreenButtonOrder);
        } else {
            getPreferenceScreen().removePreference(this.mGuideCategory);
            this.mSettingCategory.addPreference(this.mSwitchScreenButtonOrder);
            this.mSettingCategory.removePreference(this.mAppSwitchFeature);
            this.mSettingCategory.removePreference(this.mMistakeTouch);
        }
        if (!CustomizeUtil.HAS_NOTCH) {
            getPreferenceScreen().removePreference(this.mNotchForceBlack);
        }
        if (!com.android.settings.utils.Utils.supportCutoutMode() && this.mCutoutMode != null) {
            getPreferenceScreen().removePreference(this.mCutoutMode);
        }
        if (com.android.settings.utils.Utils.supportOverlayRoundedCorner()) {
            return;
        }
        getPreferenceScreen().removePreference(this.mCutoutType);
    }
}
