package com.xiaomi.accountsdk.utils;

import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public interface DiagnosisLogInterface {
    String logPostRequest(String str, Map<String, String> map, String str2, Map<String, String> map2, Map<String, String> map3, Map<String, String> map4);

    void logRequestException(Exception exc);

    void logResponse(String str, String str2, Map<String, List<String>> map, Map<String, String> map2);

    void logResponseCode(String str, int i);
}
