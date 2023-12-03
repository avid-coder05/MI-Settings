package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.provider.MiuiSettings;
import android.provider.Settings;
import com.android.settings.MiuiShortcut$System;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SettingsTree;
import java.util.LinkedList;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class KeySettingsTree extends SettingsTree {
    public static final String AUTO_DISABLE_SCREEN_BUTTONS = "auto_disable_screenbuttons_title";
    public static final String KEY_SETTINGS_FRAGMENT_IN_FULLSCREEN = "com.android.settings.KeyShortcutSettingsFragment";
    public static final String KEY_SETTINGS_FRAGMENT_IN_OTHERSETTINGS = "com.android.settings.KeySettings";
    public static final String KEY_SHORTCUT_SETTINGS_TITLE = "key_shortcut_settings_title";
    private static final String SHOW_KEY_SHORTCUTS_ENTRY = "show_key_shortcuts_entry_in_full_screen_settings";
    private Context mContext;

    protected KeySettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mContext = context;
    }

    private boolean isSupportNewVersionKeySettings() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), SHOW_KEY_SHORTCUTS_ENTRY, 0) == 1;
    }

    public Intent getIntent() {
        int i;
        Intent intent = super.getIntent();
        String columnValue = getColumnValue("resource");
        if ("launch_camera".equals(columnValue)) {
            i = R.string.launch_camera;
        } else if ("screen_shot".equals(columnValue)) {
            i = R.string.screen_shot;
        } else if ("partial_screen_shot".equals(columnValue)) {
            i = R.string.regional_screen_shot;
        } else if ("launch_voice_assistant".equals(columnValue)) {
            i = R.string.launch_voice_assistant;
        } else if ("launch_google_search".equals(columnValue)) {
            i = R.string.launch_google_search;
        } else if ("go_to_sleep".equals(columnValue)) {
            i = R.string.go_to_sleep;
        } else if ("turn_on_torch".equals(columnValue)) {
            i = R.string.turn_on_torch;
        } else if ("close_app".equals(columnValue)) {
            i = R.string.close_app;
        } else if ("split_screen".equals(columnValue)) {
            i = R.string.split_screen;
        } else if ("mi_pay".equals(columnValue)) {
            i = R.string.mi_pay;
        } else if ("dump_log".equals(columnValue)) {
            i = R.string.dump_log;
        } else if ("show_menu".equals(columnValue)) {
            i = R.string.show_menu;
        } else if ("launch_recents".equals(columnValue)) {
            i = R.string.launch_recents;
        } else if (!"au_pay".equals(columnValue)) {
            if ("google_pay".equals(columnValue)) {
                i = R.string.google_pay;
            }
            return intent;
        } else {
            i = R.string.au_pay;
        }
        intent.putExtra(":settings:show_fragment_title", this.mContext.getResources().getString(i));
        return intent;
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        if ("show_menu".equals(columnValue)) {
            if (!MiuiSettings.System.getBoolean(this.mContext.getContentResolver(), "screen_key_press_app_switch", true)) {
                return 0;
            }
        } else if ("ai_button_title_global".equals(columnValue) && !MiuiUtils.shouldShowAiButton()) {
            return 0;
        } else {
            if ("launch_recents".equals(columnValue)) {
                if (MiuiSettings.System.getBoolean(this.mContext.getContentResolver(), "screen_key_press_app_switch", true)) {
                    return 0;
                }
            } else if ("mi_pay".equals(columnValue)) {
                if (!MiuiSettings.Key.isTSMClientInstalled(this.mContext)) {
                    return 0;
                }
            } else if ("dump_log".equals(columnValue)) {
                if (Build.IS_STABLE_VERSION) {
                    return 0;
                }
            } else if (KEY_SHORTCUT_SETTINGS_TITLE.equals(columnValue) && MiuiShortcut$System.isSupportNewVersionKeySettings(this.mContext)) {
                return 0;
            }
        }
        return super.getStatus();
    }

    public boolean initialize() {
        LinkedList sons;
        String columnValue = getColumnValue("resource");
        if ("launch_voice_assistant".equals(columnValue)) {
            if (Build.IS_GLOBAL_BUILD || !MiuiShortcut$System.hasVoiceAssist(this.mContext)) {
                return true;
            }
        } else if ("launch_google_search".equals(columnValue)) {
            if (!Build.IS_GLOBAL_BUILD) {
                return true;
            }
        } else if ("long_press_power_key_half_of_second".equals(columnValue)) {
            if (SettingsFeatures.IS_NEED_REMOVE_WAKE_UP_VOICE_ASSISTANT) {
                return true;
            }
        } else if ("regional_screen_shot".equals(columnValue)) {
            if (!MiuiShortcut$System.supportPartialScreenShot()) {
                return true;
            }
        } else if ("back_tap".equals(columnValue)) {
            if (!SettingsFeatures.hasBackTapSensorFeature(this.mContext)) {
                return true;
            }
        } else if ("split_screen".equals(columnValue)) {
            if (!SettingsFeatures.hasSplitScreen()) {
                return true;
            }
        } else if ("au_pay".equals(columnValue)) {
            if (!"XIG02".equals(android.os.Build.DEVICE)) {
                return true;
            }
        } else if ("google_pay".equals(columnValue)) {
            return true;
        } else {
            if ("launch_smarthome".equals(columnValue) && !MiuiShortcut$System.hasSmartHome(this.mContext)) {
                return true;
            }
        }
        if (!KEY_SHORTCUT_SETTINGS_TITLE.equals(columnValue) && (sons = super.getSons()) != null) {
            for (int size = sons.size() - 1; size >= 0; size--) {
                ((SettingsTree) sons.get(size)).removeSelf();
            }
        }
        return super.initialize();
    }
}
