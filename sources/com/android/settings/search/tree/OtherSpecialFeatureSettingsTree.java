package com.android.settings.search.tree;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import com.android.settings.MiuiUtils;
import com.android.settings.freeform.FreeformGuideSettings;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SettingsTree;
import miui.os.Build;
import miui.os.DeviceFeature;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class OtherSpecialFeatureSettingsTree extends SettingsTree {
    private static final String BEAUTY_FC_ASSISTANT = "beauty_fc_assistant";
    private static final String COLOR_GAME_LED_SETTINGS = "color_game_led_title";
    private static final String COLOR_LAMP_BATTERY = "color_lamp_battery_title";
    private static final String COLOR_LAMP_INCALL_PULSE = "color_lamp_incall_pulse_title";
    private static final String COLOR_LAMP_NOTIFICATION_PULSE = "color_lamp_notification_pulse_title";
    private static final String COLOR_LAMP_SETTINGS = "color_lamp_title";
    private static final String COLOR_LAMP_TURN_ON = "color_lamp_turn_on";
    private static final String COLOR_LED_SETTINGS = "color_led_title";
    private static final String DO_NOT_DISTURB_MODE = "do_not_disturb_mode";
    private static final String EASY_MODE = "oldman_mode_settings";
    private static final String EDGE_HANDGRIP = "pref_edge_handgrip";
    private static final String FREEFORM_GUIDE_CLASS = "com.miui.freeform.FreeformDemoActivity";
    private static final String FREEFORM_GUIDE_CLASS_PAD = "com.miui.freeform.FreeformTutorialSettingActivity";
    private static final String FREEFORM_GUIDE_PACKGAE = "com.miui.freeform";
    private static final String FREEFORM_GUIDE_SETTINGS = "freeform_guide_settings";
    private static final String FREEFORM_GUIDE_SETTINGS_DROP_DOWN = "freeform_guide_drop_down_to_fullscreen_title";
    private static final String FREEFORM_GUIDE_SETTINGS_MOVE = "freeform_guide_move_title";
    private static final String FREEFORM_GUIDE_SETTINGS_NOTIFICATION = "freeform_guide_notification_drop_down_title";
    private static final String FREEFORM_GUIDE_SETTINGS_SIDEHIDE = "freeform_guide_to_sidehide_title";
    private static final String FREEFORM_GUIDE_SETTINGS_SLIDE_TO_SMALL = "freeform_guide_slide_to_small_freeform_title";
    private static final String FREEFORM_GUIDE_SETTINGS_SLIDE_UP = "freeform_guide_slide_up_to_close_title";
    private static final String FREEFORM_GUIDE_SETTINGS_TO_SMALL = "freeform_guide_move_to_small_freeform_window_title";
    private static final String FREEFORM_GUIDE_TYPE = "DEMO_TYPE";
    private static final String GAME_TURBO = "game_booster_title";
    private static final String HEALTH_GLOBAL = "title_special_features_health_global";
    private static final String KID_SPACE_RESOURCE = "kid_space_settings";
    private static final String MULTI_WINDOW_CVW = "multi_window_cvw_title";
    private static final String OLDMAN_MODE_SETTINGS = "oldman_mode_entry_name";
    private static final String POPUP_SETTINGS = "popup_title";
    private static final String PRIVACY_LAB_SETTINGS = "privacy_lab_settings";
    private static final String QUICK_REPLY = "quick_reply_title";
    private static final String RESOUCE_SLIDE_NAME = "slider_title";
    private static final String SPECIAL_FEATURE = "miui_special_feature";
    private static final String SUBSCREEN_SETTINGS = "subscreen_settings";
    private static final String SUPER_ASSISTANT = "gd_setting_title";
    private static final String VIDEO_TOOL_BOX = "video_tool_box_title";
    private static final String VOIP_ASSISTANT_SETTINGS = "voip_assistant_settings";

    public OtherSpecialFeatureSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public Intent getIntent() {
        boolean z;
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        char c = 65535;
        switch (columnValue.hashCode()) {
            case -1991581852:
                if (columnValue.equals(FREEFORM_GUIDE_SETTINGS_SIDEHIDE)) {
                    c = 0;
                    break;
                }
                break;
            case -1984110404:
                if (columnValue.equals(FREEFORM_GUIDE_SETTINGS_MOVE)) {
                    c = 1;
                    break;
                }
                break;
            case -1185702511:
                if (columnValue.equals(FREEFORM_GUIDE_SETTINGS_SLIDE_UP)) {
                    c = 2;
                    break;
                }
                break;
            case -1136743448:
                if (columnValue.equals(OLDMAN_MODE_SETTINGS)) {
                    c = 3;
                    break;
                }
                break;
            case -609756699:
                if (columnValue.equals(FREEFORM_GUIDE_SETTINGS_SLIDE_TO_SMALL)) {
                    c = 4;
                    break;
                }
                break;
            case -288010984:
                if (columnValue.equals(FREEFORM_GUIDE_SETTINGS_TO_SMALL)) {
                    c = 5;
                    break;
                }
                break;
            case -179377079:
                if (columnValue.equals(FREEFORM_GUIDE_SETTINGS_NOTIFICATION)) {
                    c = 6;
                    break;
                }
                break;
            case -14665543:
                if (columnValue.equals(FREEFORM_GUIDE_SETTINGS_DROP_DOWN)) {
                    c = 7;
                    break;
                }
                break;
            case 453565812:
                if (columnValue.equals(MULTI_WINDOW_CVW)) {
                    c = '\b';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (FreeformGuideSettings.getPin() && ((z = Build.IS_TABLET) || SettingsFeatures.isFoldDevice())) {
                    Intent intent = new Intent();
                    if (SettingsFeatures.isSplitTablet(((SettingsTree) this).mContext)) {
                        intent.setFlags(268435456);
                    }
                    if (z) {
                        intent.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS_PAD));
                    } else {
                        intent.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS));
                    }
                    intent.putExtra(FREEFORM_GUIDE_TYPE, "DEMO_TO_SIDEHIDE");
                    return intent;
                }
                break;
            case 1:
                Intent intent2 = new Intent();
                if (SettingsFeatures.isSplitTablet(((SettingsTree) this).mContext)) {
                    intent2.setFlags(268435456);
                }
                if (Build.IS_TABLET) {
                    intent2.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS_PAD));
                } else {
                    intent2.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS));
                }
                intent2.putExtra(FREEFORM_GUIDE_TYPE, "DEMO_MOVE");
                return intent2;
            case 2:
                Intent intent3 = new Intent();
                if (SettingsFeatures.isSplitTablet(((SettingsTree) this).mContext)) {
                    intent3.setFlags(268435456);
                }
                if (SettingsFeatures.isSplitTablet(((SettingsTree) this).mContext)) {
                    intent3.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS_PAD));
                } else {
                    intent3.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS));
                }
                intent3.putExtra(FREEFORM_GUIDE_TYPE, "DEMO_SLIDE_UP_TO_CLOSE");
                return intent3;
            case 3:
                Intent intent4 = new Intent("android.intent.action.VIEW");
                intent4.setData(Uri.parse("market://details?id=com.jeejen.family&ref=com.android.settings_search&back=true"));
                return intent4;
            case 4:
                Intent intent5 = new Intent();
                if (SettingsFeatures.isSplitTablet(((SettingsTree) this).mContext)) {
                    intent5.setFlags(268435456);
                }
                if (Build.IS_TABLET) {
                    intent5.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS_PAD));
                    if (Build.IS_INTERNATIONAL_BUILD) {
                        intent5.putExtra(FREEFORM_GUIDE_TYPE, "DEMO_FREEFORM_SLIDE_TO_SMALL_FREEFORM_GLOBAL");
                    } else {
                        intent5.putExtra(FREEFORM_GUIDE_TYPE, "DEMO_FREEFORM_SLIDE_TO_SMALL_FREEFORM");
                    }
                } else {
                    intent5.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS));
                    intent5.putExtra(FREEFORM_GUIDE_TYPE, "DEMO_FULLSCREEN_SLIDE_TO_SMALL_FREEFORM");
                }
                return intent5;
            case 5:
                Intent intent6 = new Intent();
                if (SettingsFeatures.isSplitTablet(((SettingsTree) this).mContext)) {
                    intent6.setFlags(268435456);
                }
                if (Build.IS_TABLET) {
                    intent6.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS_PAD));
                } else {
                    intent6.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS));
                }
                intent6.putExtra(FREEFORM_GUIDE_TYPE, "DEMO_HANG_TO_SMALL_FREEFORM");
                return intent6;
            case 6:
                Intent intent7 = new Intent();
                if (SettingsFeatures.isSplitTablet(((SettingsTree) this).mContext)) {
                    intent7.setFlags(268435456);
                }
                if (Build.IS_TABLET) {
                    intent7.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS_PAD));
                } else {
                    intent7.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS));
                }
                intent7.putExtra(FREEFORM_GUIDE_TYPE, "DEMO_NOTIFICATION_DROP_DOWN");
                return intent7;
            case 7:
                Intent intent8 = new Intent();
                if (SettingsFeatures.isSplitTablet(((SettingsTree) this).mContext)) {
                    intent8.setFlags(268435456);
                }
                if (Build.IS_TABLET) {
                    intent8.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS_PAD));
                } else {
                    intent8.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS));
                }
                intent8.putExtra(FREEFORM_GUIDE_TYPE, "DEMO_DROP_DOWN_TO_FULLSCREEN");
                return intent8;
            case '\b':
                if (Build.IS_TABLET) {
                    Intent intent9 = new Intent();
                    if (SettingsFeatures.isSplitTablet(((SettingsTree) this).mContext)) {
                        intent9.setFlags(268435456);
                    }
                    intent9.setComponent(new ComponentName(FREEFORM_GUIDE_PACKGAE, FREEFORM_GUIDE_CLASS_PAD));
                    if (Build.IS_INTERNATIONAL_BUILD) {
                        intent9.putExtra(FREEFORM_GUIDE_TYPE, "DEMO_MULTI_WINDOW_CVW_GLOBAL");
                    } else {
                        intent9.putExtra(FREEFORM_GUIDE_TYPE, "DEMO_MULTI_WINDOW_CVW");
                    }
                    return intent9;
                }
                break;
        }
        return super.getIntent();
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        char c = 65535;
        switch (columnValue.hashCode()) {
            case -1991581852:
                if (columnValue.equals(FREEFORM_GUIDE_SETTINGS_SIDEHIDE)) {
                    c = 0;
                    break;
                }
                break;
            case -1984110404:
                if (columnValue.equals(FREEFORM_GUIDE_SETTINGS_MOVE)) {
                    c = 1;
                    break;
                }
                break;
            case -1906150209:
                if (columnValue.equals(BEAUTY_FC_ASSISTANT)) {
                    c = 2;
                    break;
                }
                break;
            case -1728184951:
                if (columnValue.equals(COLOR_LAMP_NOTIFICATION_PULSE)) {
                    c = 3;
                    break;
                }
                break;
            case -1263863487:
                if (columnValue.equals(VIDEO_TOOL_BOX)) {
                    c = 4;
                    break;
                }
                break;
            case -1136743448:
                if (columnValue.equals(OLDMAN_MODE_SETTINGS)) {
                    c = 5;
                    break;
                }
                break;
            case -1096585771:
                if (columnValue.equals(KID_SPACE_RESOURCE)) {
                    c = 6;
                    break;
                }
                break;
            case -1073676820:
                if (columnValue.equals(PRIVACY_LAB_SETTINGS)) {
                    c = 7;
                    break;
                }
                break;
            case -979526181:
                if (columnValue.equals(COLOR_LAMP_BATTERY)) {
                    c = '\b';
                    break;
                }
                break;
            case -652537971:
                if (columnValue.equals(COLOR_LAMP_SETTINGS)) {
                    c = '\t';
                    break;
                }
                break;
            case -609756699:
                if (columnValue.equals(FREEFORM_GUIDE_SETTINGS_SLIDE_TO_SMALL)) {
                    c = '\n';
                    break;
                }
                break;
            case -367322655:
                if (columnValue.equals(COLOR_LAMP_INCALL_PULSE)) {
                    c = 11;
                    break;
                }
                break;
            case -102455439:
                if (columnValue.equals(HEALTH_GLOBAL)) {
                    c = '\f';
                    break;
                }
                break;
            case 54489555:
                if (columnValue.equals(EASY_MODE)) {
                    c = '\r';
                    break;
                }
                break;
            case 453565812:
                if (columnValue.equals(MULTI_WINDOW_CVW)) {
                    c = 14;
                    break;
                }
                break;
            case 979361651:
                if (columnValue.equals(COLOR_GAME_LED_SETTINGS)) {
                    c = 15;
                    break;
                }
                break;
            case 1232059228:
                if (columnValue.equals(GAME_TURBO)) {
                    c = 16;
                    break;
                }
                break;
            case 1739796423:
                if (columnValue.equals(SUPER_ASSISTANT)) {
                    c = 17;
                    break;
                }
                break;
            case 1958722888:
                if (columnValue.equals(COLOR_LED_SETTINGS)) {
                    c = 18;
                    break;
                }
                break;
            case 2069645681:
                if (columnValue.equals(QUICK_REPLY)) {
                    c = 19;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (!FreeformGuideSettings.getPin() || (!Build.IS_TABLET && !SettingsFeatures.isFoldDevice())) {
                    return 0;
                }
                break;
            case 1:
                if (FreeformGuideSettings.getPin() && SettingsFeatures.isFoldDevice()) {
                    return 0;
                }
                break;
            case 2:
                if (!SettingsFeatures.isFrontAssistantSupport(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 3:
            case '\b':
            case 11:
                if (SettingsFeatures.isNeedShowColorLamp()) {
                    if (!(Settings.Secure.getIntForUser(((SettingsTree) this).mContext.getContentResolver(), COLOR_LAMP_TURN_ON, 1, -2) == 1)) {
                        return 0;
                    }
                }
                break;
            case 4:
                if (!SettingsFeatures.isShowVideoToolBoxSetting(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 5:
                if (SettingsFeatures.isNeedRemoveOldmanMode(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 6:
                if (SettingsFeatures.IS_NEED_REMOVE_KID_SPACE) {
                    return 0;
                }
                break;
            case 7:
                if (!SettingsFeatures.isShowPrivacyLab()) {
                    return 0;
                }
                break;
            case '\t':
                if (!SettingsFeatures.isNeedShowColorLamp()) {
                    return 0;
                }
                break;
            case '\n':
                if (FreeformGuideSettings.getPin() && Build.IS_TABLET) {
                    return 0;
                }
                break;
            case '\f':
                if (SettingsFeatures.isHealthGlobalItemNeedHide(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case '\r':
                if (SettingsFeatures.isNeedRemoveEasyMode(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 14:
                if (!Build.IS_TABLET) {
                    return 0;
                }
                break;
            case 15:
                if (!SettingsFeatures.isNeedShowColorGameLed()) {
                    return 0;
                }
                break;
            case 16:
                if (!SettingsFeatures.isShowGameTurbo(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 17:
                if (!SettingsFeatures.isSupportDock(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 18:
                if (!SettingsFeatures.isNeedShowColorLed()) {
                    return 0;
                }
                break;
            case 19:
                if (!SettingsFeatures.isShowQuickReplySetting()) {
                    return 0;
                }
                break;
        }
        return super.getStatus();
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        char c = 65535;
        switch (columnValue.hashCode()) {
            case -1906150209:
                if (columnValue.equals(BEAUTY_FC_ASSISTANT)) {
                    c = 0;
                    break;
                }
                break;
            case -1791946374:
                if (columnValue.equals(RESOUCE_SLIDE_NAME)) {
                    c = 1;
                    break;
                }
                break;
            case -1263863487:
                if (columnValue.equals(VIDEO_TOOL_BOX)) {
                    c = 2;
                    break;
                }
                break;
            case -229318809:
                if (columnValue.equals(EDGE_HANDGRIP)) {
                    c = 3;
                    break;
                }
                break;
            case -216885501:
                if (columnValue.equals(DO_NOT_DISTURB_MODE)) {
                    c = 4;
                    break;
                }
                break;
            case 891995397:
                if (columnValue.equals(POPUP_SETTINGS)) {
                    c = 5;
                    break;
                }
                break;
            case 1040558325:
                if (columnValue.equals(FREEFORM_GUIDE_SETTINGS)) {
                    c = 6;
                    break;
                }
                break;
            case 1593029729:
                if (columnValue.equals(SPECIAL_FEATURE)) {
                    c = 7;
                    break;
                }
                break;
            case 1739796423:
                if (columnValue.equals(SUPER_ASSISTANT)) {
                    c = '\b';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (!SettingsFeatures.isFrontAssistantSupport(((SettingsTree) this).mContext)) {
                    return true;
                }
                break;
            case 1:
                if (!DeviceFeature.hasMirihiSupport()) {
                    return true;
                }
                break;
            case 2:
                if (!SettingsFeatures.isShowVideoToolBoxSetting(((SettingsTree) this).mContext)) {
                    return true;
                }
                break;
            case 3:
                if (SettingsFeatures.IS_NEED_REMOVE_EDGE_MODE) {
                    return true;
                }
                break;
            case 4:
                if (SettingsFeatures.IS_NEED_REMOVE_DISTURD) {
                    return true;
                }
                break;
            case 5:
                if (!DeviceFeature.hasPopupCameraSupport()) {
                    return true;
                }
                break;
            case 6:
                if (!SettingsFeatures.isShowFreeformGuideSetting()) {
                    return true;
                }
                break;
            case 7:
                if (MiuiUtils.isLowMemoryMachine() && Build.IS_INTERNATIONAL_BUILD) {
                    return true;
                }
                break;
            case '\b':
                if (!SettingsFeatures.isSupportDock(((SettingsTree) this).mContext)) {
                    return true;
                }
                break;
        }
        return super.initialize();
    }
}
