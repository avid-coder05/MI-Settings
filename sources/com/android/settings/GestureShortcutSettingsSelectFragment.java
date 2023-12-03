package com.android.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.stat.commonpreference.KeySettingsStatHelper;
import com.android.settings.widget.MediaCheckboxPreference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.content.res.ThemeResources;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class GestureShortcutSettingsSelectFragment extends KeyAndGestureShortcutStatHelperFragment implements Preference.OnPreferenceChangeListener {
    private AlertDialog mActionChangeDialog;
    private ContentObserver mContentObserver;
    private ContentResolver mContentResolver;
    private Context mContext;
    private boolean mLongPressPowerKeyLaunchXiaoai;
    private boolean mLongPresspowerKeyLaunchSmartHome;
    private List<MediaCheckboxPreference> mMediaCheckboxPreferences = new ArrayList();
    private String mTitle;
    private String mTitleKey;

    private void bringDialog(final CheckBoxPreference checkBoxPreference, String str) {
        if (this.mActionChangeDialog != null) {
            return;
        }
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.GestureShortcutSettingsSelectFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    MiuiSettings.System.putStringForUser(GestureShortcutSettingsSelectFragment.this.mContentResolver, checkBoxPreference.getKey(), GestureShortcutSettingsSelectFragment.this.mTitleKey, -2);
                } else {
                    checkBoxPreference.setChecked(false);
                }
                if (GestureShortcutSettingsSelectFragment.this.mActionChangeDialog != null) {
                    GestureShortcutSettingsSelectFragment.this.mActionChangeDialog = null;
                }
            }
        };
        AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle((CharSequence) null).setMessage(this.mContext.getResources().getString(R.string.gesture_function_dialog_message, MiuiShortcut$Key.getResourceForKey(checkBoxPreference.getKey(), this.mContext), MiuiShortcut$Key.getResourceForKey(str, this.mContext), this.mTitle)).setPositiveButton(R.string.key_gesture_function_dialog_positive, onClickListener).setNegativeButton(R.string.key_gesture_function_dialog_negative, onClickListener).setCancelable(false).create();
        this.mActionChangeDialog = create;
        create.show();
    }

    private void bringUpGuideAnimation() {
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
        ((TextView) inflate.findViewById(R.id.start_enjoy)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.GestureShortcutSettingsSelectFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                windowManager.removeView(inflate);
            }
        });
        inflate.setOnKeyListener(new View.OnKeyListener() { // from class: com.android.settings.GestureShortcutSettingsSelectFragment.5
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

    private boolean isLongPressPowerLaunchXiaoAi() {
        return Settings.System.getIntForUser(this.mContentResolver, "long_press_power_launch_xiaoai", 0, UserHandle.myUserId()) == 1;
    }

    private boolean needShield() {
        String str = Build.DEVICE;
        return "zeus".equals(str) || "cupid".equals(str);
    }

    private void registerContentObserver(final PreferenceCategory preferenceCategory) {
        this.mContentObserver = new ContentObserver(this.mContext.getMainThreadHandler()) { // from class: com.android.settings.GestureShortcutSettingsSelectFragment.3
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                PreferenceCategory preferenceCategory2;
                MediaCheckboxPreference mediaCheckboxPreference;
                PreferenceCategory preferenceCategory3;
                MediaCheckboxPreference mediaCheckboxPreference2;
                String str = GestureShortcutSettingsSelectFragment.this.mTitleKey;
                str.hashCode();
                if (str.equals("launch_smarthome")) {
                    GestureShortcutSettingsSelectFragment gestureShortcutSettingsSelectFragment = GestureShortcutSettingsSelectFragment.this;
                    gestureShortcutSettingsSelectFragment.mLongPresspowerKeyLaunchSmartHome = Settings.System.getIntForUser(gestureShortcutSettingsSelectFragment.mContentResolver, "long_press_power_launch_smarthome", UserHandle.myUserId(), 0) == 1;
                    if (GestureShortcutSettingsSelectFragment.this.mLongPresspowerKeyLaunchSmartHome && (preferenceCategory2 = preferenceCategory) != null && (mediaCheckboxPreference = (MediaCheckboxPreference) preferenceCategory2.findPreference("long_press_power_key")) != null) {
                        mediaCheckboxPreference.setChecked(true);
                    }
                } else if (str.equals("launch_voice_assistant")) {
                    GestureShortcutSettingsSelectFragment gestureShortcutSettingsSelectFragment2 = GestureShortcutSettingsSelectFragment.this;
                    gestureShortcutSettingsSelectFragment2.mLongPressPowerKeyLaunchXiaoai = Settings.System.getIntForUser(gestureShortcutSettingsSelectFragment2.mContentResolver, "long_press_power_launch_xiaoai", 0, UserHandle.myUserId()) == 1;
                    if (GestureShortcutSettingsSelectFragment.this.mLongPressPowerKeyLaunchXiaoai && (preferenceCategory3 = preferenceCategory) != null && (mediaCheckboxPreference2 = (MediaCheckboxPreference) preferenceCategory3.findPreference("long_press_power_key")) != null) {
                        mediaCheckboxPreference2.setChecked(true);
                    }
                }
                super.onChange(z);
            }
        };
        this.mContentResolver.registerContentObserver(Settings.System.getUriFor("long_press_power_launch_xiaoai"), false, this.mContentObserver, -1);
        this.mContentResolver.registerContentObserver(Settings.System.getUriFor("long_press_power_launch_smarthome"), false, this.mContentObserver, -1);
        this.mContentObserver.onChange(false);
    }

    private void setTitleKey(String str, Resources resources) {
        if (resources.getString(R.string.voice_assist).equals(str)) {
            this.mTitleKey = "launch_voice_assistant";
        } else if (resources.getString(R.string.launch_google_search).equals(str)) {
            this.mTitleKey = "launch_google_search";
        } else if (resources.getString(R.string.screen_shot).equals(str)) {
            this.mTitleKey = "screen_shot";
        } else if (resources.getString(R.string.regional_screen_shot).equals(str)) {
            this.mTitleKey = "partial_screen_shot";
        } else if (resources.getString(R.string.mi_pay_summary).equals(str) || resources.getString(R.string.mi_pay_summary_without_nfc).equals(str)) {
            this.mTitleKey = "mi_pay";
        } else if (resources.getString(R.string.launch_camera).equals(str)) {
            this.mTitleKey = "launch_camera";
        } else if (resources.getString(R.string.turn_on_torch).equals(str)) {
            this.mTitleKey = "turn_on_torch";
        } else if (resources.getString(R.string.au_pay).equals(str)) {
            this.mTitleKey = "au_pay";
        } else if (resources.getString(R.string.google_pay).equals(str)) {
            this.mTitleKey = "google_pay";
        } else if (MiuiShortcut$Key.getResourceForKey("change_brightness", this.mContext).equals(str)) {
            this.mTitleKey = "change_brightness";
        } else if (MiuiShortcut$Key.getResourceForKey("launch_smarthome", this.mContext).equals(str)) {
            this.mTitleKey = "launch_smarthome";
        } else {
            Log.e("GestureShortcutSettingsSelectFragment", "Illegal title!");
            finish();
        }
    }

    @Override // com.android.settings.KeyAndGestureShortcutStatHelperFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.gesture_shortcut_settings_select);
        if (getArguments() == null || getArguments().getString(":settings:show_fragment_title") == null) {
            finish();
            return;
        }
        this.mContext = getContext();
        String string = getArguments().getString(":settings:show_fragment_title");
        this.mTitle = string;
        setTitleKey(string, this.mContext.getResources());
        MiuiShortcut$Key.setGestureMap(this.mContext);
        List<String> list = MiuiShortcut$Key.sGestureMap.get(this.mTitleKey);
        List<String> gestureShortcutAction = MiuiShortcut$Key.getGestureShortcutAction(this.mContext, this.mTitleKey);
        if (list == null) {
            finish();
            return;
        }
        this.mContentResolver = getContentResolver();
        this.mPageTitle = KeySettingsStatHelper.GESTURE_PAGE_KEY;
        PreferenceCategory preferenceCategory = (PreferenceCategory) getPreferenceScreen().findPreference("action_category");
        if (preferenceCategory != null) {
            for (String str : list) {
                MediaCheckboxPreference mediaCheckboxPreference = new MediaCheckboxPreference(getPrefContext(), miui.os.Build.IS_TABLET ? 2 : 1);
                mediaCheckboxPreference.setTitle(MiuiShortcut$Key.getResourceForKey(str, this.mContext));
                mediaCheckboxPreference.setSingleLineTitle(false);
                mediaCheckboxPreference.setKey(str);
                mediaCheckboxPreference.setResName(str);
                if (mediaCheckboxPreference.getType() == 2) {
                    mediaCheckboxPreference.setShowDelimiter(false);
                }
                if (MiuiShortcut$Key.FEATURE_KNOCK.contains(str)) {
                    mediaCheckboxPreference.setSummary(MiuiShortcut$Key.getResourceForKey("knock_edge_area_invalid", this.mContext));
                }
                if ("key_combination_power_volume_down".equals(str) && needShield()) {
                    mediaCheckboxPreference.setEnabled(false);
                    mediaCheckboxPreference.setDefaultValue(Boolean.TRUE);
                }
                mediaCheckboxPreference.setOnPreferenceChangeListener(this);
                if ("double_click_volume_down_when_lock".equals(str)) {
                    mediaCheckboxPreference.setChecked(Settings.System.getIntForUser(this.mContentResolver, "volumekey_launch_camera", 0, -2) == 1);
                } else if (gestureShortcutAction.contains(str)) {
                    mediaCheckboxPreference.setChecked(true);
                } else if ("long_press_power_key".equals(str) && "launch_google_search".equals(this.mTitleKey)) {
                    mediaCheckboxPreference.setChecked(isLongPressPowerLaunchXiaoAi());
                }
                this.mMediaCheckboxPreferences.add(mediaCheckboxPreference);
                preferenceCategory.addPreference(mediaCheckboxPreference);
            }
            MediaCheckboxPreference mediaCheckboxPreference2 = (MediaCheckboxPreference) preferenceCategory.findPreference("key_combination_power_volume_down");
            if (mediaCheckboxPreference2 != null) {
                String str2 = Build.DEVICE;
                if (str2.equals("ingres") || str2.equals("ares")) {
                    mediaCheckboxPreference2.setResName("key_combination_left_power_volume_down");
                }
            }
        }
        Preference findPreference = findPreference("ai_button_global");
        if (!"launch_google_search".equals(this.mTitleKey) || !miui.os.Build.IS_INTERNATIONAL_BUILD || !MiuiShortcut$System.shouldShowAiButton()) {
            getPreferenceScreen().removePreference(findPreference);
        }
        Preference findPreference2 = findPreference("ai_button");
        if (findPreference2 != null) {
            if ("launch_voice_assistant".equals(this.mTitleKey) && !miui.os.Build.IS_INTERNATIONAL_BUILD && MiuiShortcut$System.shouldShowAiButton()) {
                findPreference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.GestureShortcutSettingsSelectFragment.1
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent("android.intent.action.MAIN");
                        intent.setClassName("com.miui.voiceassist", "com.xiaomi.voiceassistant.AiSettings.AiSettingsActivity");
                        intent.addFlags(268435456);
                        GestureShortcutSettingsSelectFragment.this.startActivity(intent);
                        return false;
                    }
                });
            } else {
                getPreferenceScreen().removePreference(findPreference2);
            }
        }
        registerContentObserver(preferenceCategory);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        ContentObserver contentObserver;
        ContentResolver contentResolver = this.mContentResolver;
        if (contentResolver != null && (contentObserver = this.mContentObserver) != null) {
            contentResolver.unregisterContentObserver(contentObserver);
        }
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        String keyAndGestureShortcutSetFunction = MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(this.mContext, preference.getKey());
        boolean equals = "double_click_volume_down_when_lock".equals(preference.getKey());
        String str = MiCloudStatusInfo.QuotaInfo.WARN_NONE;
        if (equals) {
            Settings.System.putInt(this.mContentResolver, "volumekey_launch_camera", booleanValue ? 1 : 0);
            Map<String, String> map = this.mShortcutMap;
            if (booleanValue) {
                str = "volumekey_launch_camera";
            }
            map.put("double_click_volume_down_when_lock", str);
        } else if (!booleanValue) {
            if ("long_press_power_key".equals(preference.getKey())) {
                String str2 = this.mTitleKey;
                str2.hashCode();
                char c = 65535;
                switch (str2.hashCode()) {
                    case -1272325988:
                        if (str2.equals("launch_smarthome")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -856866078:
                        if (str2.equals("launch_google_search")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 596487045:
                        if (str2.equals("launch_voice_assistant")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        if (this.mLongPresspowerKeyLaunchSmartHome) {
                            Settings.System.putIntForUser(getContentResolver(), "long_press_power_launch_smarthome", 0, -2);
                            break;
                        }
                        break;
                    case 1:
                        Settings.System.putIntForUser(getContentResolver(), "long_press_power_launch_xiaoai", 0, -2);
                        break;
                    case 2:
                        if (this.mLongPressPowerKeyLaunchXiaoai) {
                            Settings.System.putIntForUser(getContentResolver(), "long_press_power_launch_xiaoai", 0, -2);
                            break;
                        }
                        break;
                }
            }
            MiuiSettings.System.putStringForUser(this.mContentResolver, preference.getKey(), MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
            this.mShortcutMap.put(preference.getKey(), MiCloudStatusInfo.QuotaInfo.WARN_NONE);
        } else if (!TextUtils.isEmpty(keyAndGestureShortcutSetFunction) && !MiCloudStatusInfo.QuotaInfo.WARN_NONE.equals(keyAndGestureShortcutSetFunction) && !this.mTitleKey.equals(keyAndGestureShortcutSetFunction)) {
            bringDialog((CheckBoxPreference) preference, keyAndGestureShortcutSetFunction);
        } else if ("long_press_power_key".equals(preference.getKey()) && "launch_google_search".equals(this.mTitleKey)) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("power_key_guide", 0);
            if (!sharedPreferences.getBoolean("power_key_guide_already_shown", false)) {
                bringUpGuideAnimation();
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean("power_key_guide_already_shown", true);
                edit.apply();
            }
            Settings.System.putIntForUser(getContentResolver(), "long_press_power_launch_xiaoai", 1, -2);
            this.mShortcutMap.put("long_press_power_key", "launch_google_search");
        } else {
            MiuiSettings.System.putStringForUser(this.mContentResolver, preference.getKey(), this.mTitleKey, -2);
            this.mShortcutMap.put(preference.getKey(), this.mTitleKey);
        }
        return true;
    }
}
