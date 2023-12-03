package com.android.settings.search.tree;

import android.content.Context;
import com.android.settings.lab.MiuiAiAsstCallScreenController;
import com.android.settings.lab.MiuiAiPreloadController;
import com.android.settings.lab.MiuiDriveModeController;
import com.android.settings.lab.MiuiFlashbackController;
import com.android.settings.lab.MiuiLabGestureController;
import com.android.settings.lab.MiuiVoipAssistantController;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class MiuiLabSettingsTree extends SettingsTree {
    private static final String AIASST_CALL_SCREEN_TITLE = "miui_lab_aiasst_call_screen_title";
    private static final String DRIVE_MODE_TITLE = "miui_lab_drive_mode_title";
    private static final String MIUI_FLASHBACK_TITLE = "flashback_title";
    private static final String MIUI_LAB_AI_PRELOAD_TITLE = "miui_lab_ai_preload_title";
    private static final String MIUI_LAB_GESTURE_TITLE = "miui_lab_gesture_title";
    private static final String MIUI_LAB_SETTINGS = "miui_lab_settings";
    private static final String MIUI_VOIP_ASSISTANT_TITLE = "voip_assistant_settings";

    protected MiuiLabSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        char c = 65535;
        switch (columnValue.hashCode()) {
            case -1650135740:
                if (columnValue.equals(MIUI_LAB_SETTINGS)) {
                    c = 0;
                    break;
                }
                break;
            case -1549698382:
                if (columnValue.equals(DRIVE_MODE_TITLE)) {
                    c = 1;
                    break;
                }
                break;
            case -1132714653:
                if (columnValue.equals(MIUI_VOIP_ASSISTANT_TITLE)) {
                    c = 2;
                    break;
                }
                break;
            case -318275348:
                if (columnValue.equals(MIUI_LAB_AI_PRELOAD_TITLE)) {
                    c = 3;
                    break;
                }
                break;
            case 191087856:
                if (columnValue.equals(MIUI_FLASHBACK_TITLE)) {
                    c = 4;
                    break;
                }
                break;
            case 1942512481:
                if (columnValue.equals(MIUI_LAB_GESTURE_TITLE)) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (SettingsFeatures.isMiuiLabNeedHide(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 1:
                if (MiuiDriveModeController.isNeedHideDriveMode(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 2:
                if (MiuiVoipAssistantController.isNotSupported()) {
                    return 0;
                }
                break;
            case 3:
                if (MiuiAiPreloadController.isNotSupported()) {
                    return 0;
                }
                break;
            case 4:
                if (MiuiFlashbackController.isNotSupported()) {
                    return 0;
                }
                break;
            case 5:
                if (MiuiLabGestureController.isNotSupported()) {
                    return 0;
                }
                break;
        }
        if (AIASST_CALL_SCREEN_TITLE.equals(columnValue) && MiuiAiAsstCallScreenController.isNeedHideCallScreen(((SettingsTree) this).mContext)) {
            return 0;
        }
        return super.getStatus();
    }
}
