package com.xiaomi.micloudsdk.utils;

import android.util.ArrayMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class JsonUtils {
    private static Object convertObj(Object obj) {
        if (obj instanceof JSONObject) {
            return jsonToMap((JSONObject) obj);
        }
        if (!(obj instanceof JSONArray)) {
            if (obj == JSONObject.NULL) {
                return null;
            }
            return obj;
        }
        JSONArray jSONArray = (JSONArray) obj;
        int length = jSONArray.length();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < length; i++) {
            arrayList.add(convertObj(jSONArray.opt(i)));
        }
        return arrayList;
    }

    public static Map<String, Object> jsonToMap(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        ArrayMap arrayMap = new ArrayMap();
        Iterator<String> keys = jSONObject.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            arrayMap.put(next, convertObj(jSONObject.opt(next)));
        }
        return arrayMap;
    }
}
