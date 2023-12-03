package com.xiaomi.onetrack.util;

import android.text.TextUtils;
import com.xiaomi.onetrack.Configuration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class r {

    /* loaded from: classes2.dex */
    public interface a {
        boolean a(Object obj);
    }

    public static String a(Configuration configuration) {
        StringBuilder sb = new StringBuilder();
        String appId = configuration.getAppId();
        String pluginId = configuration.getPluginId();
        if (!TextUtils.isEmpty(appId)) {
            sb.append(appId);
        }
        if (!TextUtils.isEmpty(pluginId)) {
            sb.append(pluginId);
        }
        return com.xiaomi.onetrack.c.d.h(sb.toString());
    }

    private static JSONArray a(List list) throws JSONException {
        if (list == null) {
            return null;
        }
        JSONArray jSONArray = new JSONArray();
        for (Object obj : list) {
            if (b(obj)) {
                jSONArray.put(obj);
            } else if (obj instanceof Map) {
                JSONObject jSONObject = new JSONObject();
                boolean z = false;
                for (Map.Entry entry : ((Map) obj).entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if ((key instanceof String) && b(value)) {
                        jSONObject.put((String) key, value);
                        z = true;
                    }
                }
                if (z) {
                    jSONArray.put(jSONObject);
                }
            }
        }
        return jSONArray;
    }

    private static JSONObject a(Map<String, Object> map, a aVar) {
        JSONObject jSONObject = new JSONObject();
        if (map == null) {
            return jSONObject;
        }
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (aVar.a(value)) {
                    if (b(value)) {
                        jSONObject.put(key, value);
                    } else if (value instanceof List) {
                        jSONObject.put(key, a((List) value));
                    }
                } else if (p.a) {
                    a("ParamUtil", entry.getKey());
                }
            }
        } catch (Exception e) {
            p.b("ParamUtil", "checkParam error:" + e.toString());
        }
        return jSONObject;
    }

    public static JSONObject a(Map<String, Object> map, boolean z) {
        return a(map, new s(z));
    }

    public static JSONObject a(JSONObject jSONObject, JSONObject jSONObject2) {
        if (jSONObject == null && jSONObject2 == null) {
            return new JSONObject();
        }
        if (jSONObject != null || jSONObject2 == null) {
            if (jSONObject == null || jSONObject2 != null) {
                try {
                    Iterator<String> keys = jSONObject.keys();
                    while (keys.hasNext()) {
                        String next = keys.next();
                        jSONObject2.put(next, jSONObject.opt(next));
                    }
                    return jSONObject2;
                } catch (Exception e) {
                    p.b("ParamUtil", "merge error：" + e.toString());
                    return jSONObject;
                }
            }
            return jSONObject;
        }
        return jSONObject2;
    }

    public static void a(String str, String str2) {
        p.b(str, "key is " + str2 + ", the param value is invalid，please change the parameter type to string ,numeric, boolean！");
    }

    public static boolean a(Object obj) {
        return b(obj) || (obj instanceof List);
    }

    public static boolean a(String str) {
        if (c(str)) {
            String lowerCase = str.toLowerCase();
            return (lowerCase.startsWith("onetrack_") || lowerCase.startsWith("ot_")) ? false : true;
        }
        return false;
    }

    public static boolean b(Object obj) {
        return (obj instanceof Boolean) || (obj instanceof Number) || (obj instanceof String);
    }

    private static boolean c(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (i == 0 && Character.isDigit(c)) {
                return false;
            }
            if (c != '_' && !Character.isDigit(c) && ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))) {
                return false;
            }
        }
        return true;
    }
}
