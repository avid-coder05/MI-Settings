package com.android.settings.device;

import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class JSONUtils {
    public static boolean getBoolean(JSONObject jSONObject, String str, boolean z) {
        if (!TextUtils.isEmpty(str) && jSONObject != null) {
            try {
                return jSONObject.getBoolean(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return z;
    }

    public static int getInt(JSONObject jSONObject, String str, int i) {
        if (!TextUtils.isEmpty(str) && jSONObject != null) {
            try {
                return jSONObject.getInt(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public static JSONArray getJSONArray(JSONObject jSONObject, String str) {
        if (!TextUtils.isEmpty(str) && jSONObject != null) {
            try {
                return jSONObject.getJSONArray(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JSONObject getJSONObject(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJSONObject(JSONArray jSONArray, int i) {
        if (jSONArray == null) {
            return null;
        }
        try {
            return jSONArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJSONObject(JSONObject jSONObject, String str) {
        if (!TextUtils.isEmpty(str) && jSONObject != null) {
            try {
                return jSONObject.getJSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getString(JSONObject jSONObject, String str) {
        if (!TextUtils.isEmpty(str) && jSONObject != null) {
            try {
                return jSONObject.getString(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
