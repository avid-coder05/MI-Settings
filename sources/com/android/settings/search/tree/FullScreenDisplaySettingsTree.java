package com.android.settings.search.tree;

import android.content.Context;
import android.provider.Settings;
import com.android.settings.controller.FullScreenDisplayController;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class FullScreenDisplaySettingsTree extends SettingsTree {
    private static final String AUTO_DISABLE_SCREEN_BUTTONS = "auto_disable_screenbuttons_title";
    public static final String FSG_MISTAKE_TOUCH_TITLE = "fsg_mistake_touch_title";
    public static final String INFINITY_DISPLAY_TITLE = "infinity_display_title";
    public static final String NAVIGATION_GUIDE_APPSWITCH_ANIM_TITLE = "navigation_guide_appswitch_anim_title";
    public static final String NAVIGATION_GUIDE_APPSWITCH_TITLE = "navigation_guide_appswitch_title";
    public static final String NAVIGATION_GUIDE_APP_QUICK_SWITCH_TITLE = "navigation_guide_app_quick_switch_title";
    public static final String NAVIGATION_GUIDE_HIDE_GESTURE_LINE_TITLE = "navigation_guide_hide_gesture_line_title";
    public static final String NAVIGATION_GUIDE_SETTINGS = "navigation_guide_settings";
    public static final String NAVIGATION_TYPE_RADIO_TEXT_FULL_SCREEN = "navigation_type_radio_text_full_screen";
    public static final String NAVIGATION_TYPE_RADIO_TEXT_VIRTUAL_KEY = "navigation_type_radio_text_virtual_key";
    private static final String SHOW_KEY_SHORTCUTS_ENTRY = "show_key_shortcuts_entry_in_full_screen_settings";
    public static final String SWITCH_SCREEN_BUTTON_ORDER = "switch_screen_button_order";

    protected FullScreenDisplaySettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    private void addSon(String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("resource", str);
            jSONObject.put(FunctionColumns.IS_CHECKBOX, true);
            addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
        } catch (JSONException unused) {
        }
    }

    private boolean isSupportGestureSettings() {
        return Settings.Secure.getInt(((SettingsTree) this).mContext.getContentResolver(), SHOW_KEY_SHORTCUTS_ENTRY, 0) == 1;
    }

    protected int getStatus() {
        if (FullScreenDisplayController.isRemoveEntryFromSettings(((SettingsTree) this).mContext)) {
            return 0;
        }
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        char c = 65535;
        switch (columnValue.hashCode()) {
            case -1984361614:
                if (columnValue.equals(FSG_MISTAKE_TOUCH_TITLE)) {
                    c = 0;
                    break;
                }
                break;
            case -1823944958:
                if (columnValue.equals(NAVIGATION_GUIDE_APPSWITCH_ANIM_TITLE)) {
                    c = 1;
                    break;
                }
                break;
            case -1651363072:
                if (columnValue.equals(NAVIGATION_GUIDE_APPSWITCH_TITLE)) {
                    c = 2;
                    break;
                }
                break;
            case -604982165:
                if (columnValue.equals(NAVIGATION_GUIDE_APP_QUICK_SWITCH_TITLE)) {
                    c = 3;
                    break;
                }
                break;
            case -41173143:
                if (columnValue.equals(SWITCH_SCREEN_BUTTON_ORDER)) {
                    c = 4;
                    break;
                }
                break;
            case -36394809:
                if (columnValue.equals("auto_disable_screenbuttons_title")) {
                    c = 5;
                    break;
                }
                break;
            case 462153714:
                if (columnValue.equals(NAVIGATION_GUIDE_HIDE_GESTURE_LINE_TITLE)) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (!FullScreenDisplayController.isScreenButtonHidden(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 1:
            case 2:
                if (FullScreenDisplayController.isUseFsVersionThree(((SettingsTree) this).mContext) || !FullScreenDisplayController.isScreenButtonHidden(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 3:
            case 6:
                if (!FullScreenDisplayController.isUseFsVersionThree(((SettingsTree) this).mContext) || !FullScreenDisplayController.isScreenButtonHidden(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 4:
                if (FullScreenDisplayController.isScreenButtonHidden(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 5:
                if (!isSupportGestureSettings()) {
                    return 0;
                }
                break;
        }
        if (!NAVIGATION_GUIDE_SETTINGS.equals(getColumnValue("category_origin")) || FullScreenDisplayController.isScreenButtonHidden(((SettingsTree) this).mContext)) {
            return super.getStatus();
        }
        return 0;
    }

    public boolean initialize() {
        if (INFINITY_DISPLAY_TITLE.equals(getColumnValue("resource"))) {
            addSon(NAVIGATION_TYPE_RADIO_TEXT_VIRTUAL_KEY);
            addSon(NAVIGATION_TYPE_RADIO_TEXT_FULL_SCREEN);
        }
        return super.initialize();
    }
}
