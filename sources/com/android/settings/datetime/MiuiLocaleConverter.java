package com.android.settings.datetime;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiLocaleConverter {
    public static final Map<String, String> sCommonChineseConvertMap;
    public static final Map<String, String> sCommonConvertMap;
    public static final Map<String, String> sSimplifiedChineseConvertMap;
    public static final Map<String, String> sTraditionalChineseConvertMap;

    static {
        HashMap hashMap = new HashMap();
        sCommonConvertMap = hashMap;
        HashMap hashMap2 = new HashMap();
        sSimplifiedChineseConvertMap = hashMap2;
        HashMap hashMap3 = new HashMap();
        sTraditionalChineseConvertMap = hashMap3;
        HashMap hashMap4 = new HashMap();
        sCommonChineseConvertMap = hashMap4;
        hashMap.put("中国", "中国大陆");
        hashMap.put("阿鲁巴", "");
        hashMap.put("安提瓜和巴布达", "");
        hashMap.put("China", "China mainland");
        hashMap.put("Aruba", "");
        hashMap.put("Antigua & Barbuda", "");
        hashMap.put("中國", "中國大陆");
        hashMap4.put("Macau", "Macau, China");
        hashMap4.put("Taiwan", "Taiwan, China");
        hashMap4.put("Hong Kong", "Hong Kong, China");
        hashMap2.put("澳门", "中国澳门");
        hashMap2.put("台湾", "中国台湾");
        hashMap2.put("香港", "中国香港");
        hashMap3.put("澳門", "中國澳門");
        hashMap3.put("台灣", "中國台灣");
        hashMap3.put("香港", "中國香港");
    }

    public static String convert(String str) {
        Map<String, String> map = sCommonConvertMap;
        if (map.containsKey(str)) {
            return map.get(str);
        }
        if (Build.IS_INTERNATIONAL_BUILD) {
            return str;
        }
        Map<String, String> map2 = sCommonChineseConvertMap;
        if (map2.containsKey(str)) {
            return map2.get(str);
        }
        if (Locale.getDefault().equals(Locale.TRADITIONAL_CHINESE)) {
            Map<String, String> map3 = sTraditionalChineseConvertMap;
            return map3.containsKey(str) ? map3.get(str) : str;
        }
        Map<String, String> map4 = sSimplifiedChineseConvertMap;
        return map4.containsKey(str) ? map4.get(str) : str;
    }
}
