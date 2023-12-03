package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import com.android.settings.MiuiShortcut$System;
import com.android.settings.personal.ScreenRecorderController;
import com.android.settings.search.FunctionColumns;
import com.android.settings.special.ExternalRamController;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SettingsTree;
import com.android.settingslib.search.TinyIntent;
import miui.os.Build;
import miui.payment.PaymentManager;
import miui.util.IOtgSwitch;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class OtherPersonalSettingsTree extends SettingsTree {
    public static final String AUTO_CLEAN_TITLE = "auto_clean_title";
    public static final String BEAUTY_CAMERA_SETTINGS = "beauty_camera";
    public static final String EDGE_SETTINGS = "edge_mode_state_title";
    public static final String EDGE_SETTINGS_INFO = "edge_mode_state_summary";
    public static final String EDGE_SETTINGS_SEEKBAR_INFO = "seek_bar_info";
    private static final String ENTERPRISE_MODE = "enterprise_mode";
    private static final String EXTERNAL_RAM_TITLE = "external_ram_title";
    public static final String GESTURE_SHORTCUT_SETTINGS = "gesture_settings_title";
    private static final String HANDY_MODE = "handy_mode";
    private static final boolean IS_EXCLUDE_ENTERPRISE_MODE = isExcludeEnterpriseMode();
    private static final String LED_SETTINGS = "led_settings";
    private static final String OTG_SETTINGS = "otg_settings";
    private static final String SCREEN_RECORDER = "screen_recorder_title";
    public static final String SPEAKER_CLEAN = "speaker_clean";
    private static final String TOUCH_ASSISTANT = "touch_assistant";

    protected OtherPersonalSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    private void addSon(String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("resource", str);
            jSONObject.put(FunctionColumns.IS_CHECKBOX, true);
            jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent(new Intent("android.settings.SPEAKER_SETTINGS")).toJSONObject());
            addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
        } catch (JSONException unused) {
        }
    }

    public static boolean isExcludeEnterpriseMode() {
        return Build.IS_INTERNATIONAL_BUILD;
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        char c = 65535;
        switch (columnValue.hashCode()) {
            case -1784188982:
                if (columnValue.equals(SCREEN_RECORDER)) {
                    c = 0;
                    break;
                }
                break;
            case -1549246655:
                if (columnValue.equals(EDGE_SETTINGS_SEEKBAR_INFO)) {
                    c = 1;
                    break;
                }
                break;
            case -497765439:
                if (columnValue.equals(ENTERPRISE_MODE)) {
                    c = 2;
                    break;
                }
                break;
            case 2810398:
                if (columnValue.equals(EDGE_SETTINGS_INFO)) {
                    c = 3;
                    break;
                }
                break;
            case 192383875:
                if (columnValue.equals(EXTERNAL_RAM_TITLE)) {
                    c = 4;
                    break;
                }
                break;
            case 336138482:
                if (columnValue.equals("gesture_settings_title")) {
                    c = 5;
                    break;
                }
                break;
            case 462326238:
                if (columnValue.equals(TOUCH_ASSISTANT)) {
                    c = 6;
                    break;
                }
                break;
            case 1914447008:
                if (columnValue.equals(OTG_SETTINGS)) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (new ScreenRecorderController(((SettingsTree) this).mContext).getVisibility()) {
                    return super.getStatus();
                }
                return 0;
            case 1:
            case 3:
                return 0;
            case 2:
                if (IS_EXCLUDE_ENTERPRISE_MODE) {
                    return 0;
                }
                break;
            case 4:
                if (!ExternalRamController.isShow()) {
                    return 0;
                }
                break;
            case 5:
                if (!MiuiShortcut$System.isSupportNewVersionKeySettings(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 6:
                if (SettingsFeatures.isNeedRemoveTouchAssistant(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 7:
                if (!IOtgSwitch.getInstance().isOtgSupported()) {
                    return 0;
                }
                break;
        }
        return super.getStatus();
    }

    protected String getTitle(boolean z) {
        return SCREEN_RECORDER.equals(getColumnValue("resource")) ? ScreenRecorderController.getTitle(((SettingsTree) this).mContext) : super.getTitle(z);
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        char c = 65535;
        switch (columnValue.hashCode()) {
            case -1940076840:
                if (columnValue.equals(HANDY_MODE)) {
                    c = 0;
                    break;
                }
                break;
            case -1784188982:
                if (columnValue.equals(SCREEN_RECORDER)) {
                    c = 1;
                    break;
                }
                break;
            case -993761321:
                if (columnValue.equals(LED_SETTINGS)) {
                    c = 2;
                    break;
                }
                break;
            case -77840887:
                if (columnValue.equals(SPEAKER_CLEAN)) {
                    c = 3;
                    break;
                }
                break;
            case -3052472:
                if (columnValue.equals(BEAUTY_CAMERA_SETTINGS)) {
                    c = 4;
                    break;
                }
                break;
            case 1001691856:
                if (columnValue.equals(EDGE_SETTINGS)) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (SettingsFeatures.IS_NEED_REMOVE_HANDY_MODE) {
                    return true;
                }
                break;
            case 1:
                setColumnValue("keywords", "search_screen_recorder_title");
                break;
            case 2:
                if (SettingsFeatures.isNeedRemoveLedSettings()) {
                    return true;
                }
                break;
            case 3:
                if (SettingsFeatures.isSupportSpeakerAutoClean(((SettingsTree) this).mContext)) {
                    addSon(AUTO_CLEAN_TITLE);
                    break;
                } else {
                    return true;
                }
            case 4:
                if (!SettingsFeatures.IS_SUPPORT_BEAUTY_CAMERA) {
                    return true;
                }
                break;
            case 5:
                if (!SettingsFeatures.isSupportEdgeSuppression()) {
                    return true;
                }
                break;
        }
        return super.initialize();
    }
}
