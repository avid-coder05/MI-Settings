package com.android.settings.device;

import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ParseMiShopDataUtils {
    private static int BASICINFO_DISPLAY_VALUE = 1;
    private static String CPU_INFO;

    public static JSONObject getAllParamData(String str) {
        return JSONUtils.getJSONObject(JSONUtils.getJSONObject(str), "data");
    }

    public static JSONArray getBasicItemsArray(String str) {
        return JSONUtils.getJSONArray(JSONUtils.getJSONObject(str), "BasicItems");
    }

    public static String getCpuInfo() {
        return CPU_INFO;
    }

    public static boolean getDataSuccess(String str) {
        return JSONUtils.getBoolean(JSONUtils.getJSONObject(str), "status", false);
    }

    public static String getFrontCameraPixel(String str) {
        return JSONUtils.getString(JSONUtils.getJSONObject(JSONUtils.getJSONObject(JSONUtils.getJSONObject(str), "data"), "camera"), "front_camera");
    }

    public static boolean getItemBooleanSummary(JSONObject jSONObject) {
        return JSONUtils.getBoolean(jSONObject, "Summary", false);
    }

    public static int getItemIndex(JSONObject jSONObject) {
        return JSONUtils.getInt(jSONObject, "Index", -1);
    }

    public static String getItemSummary(JSONObject jSONObject) {
        return JSONUtils.getString(jSONObject, "Summary");
    }

    public static String getItemTitle(JSONObject jSONObject) {
        return JSONUtils.getString(jSONObject, "Title");
    }

    public static String getRearCameraPixel(String str) {
        return JSONUtils.getString(JSONUtils.getJSONObject(JSONUtils.getJSONObject(JSONUtils.getJSONObject(str), "data"), "camera"), "rear_camera");
    }

    public static boolean isCameraPixelEmpty(String str) {
        return TextUtils.isEmpty(str) || TextUtils.isEmpty(str.trim()) || "EMPTY".equalsIgnoreCase(str.trim());
    }

    public static void setCpuInfo(String str) {
        CPU_INFO = str;
    }

    public static boolean showBasicItems(String str) {
        return JSONUtils.getInt(JSONUtils.getJSONObject(str), "BasicInfoToggle", -1) == BASICINFO_DISPLAY_VALUE;
    }
}
