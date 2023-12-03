package com.xiaomi.accountsdk.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public class ObjectUtils {
    public static Map<String, String> listToMap(Map<String, List<String>> map) {
        HashMap hashMap = new HashMap();
        if (map != null) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                if (key != null && value != null && value.size() > 0) {
                    hashMap.put(key, value.get(0));
                }
            }
        }
        return hashMap;
    }
}
