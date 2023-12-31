package com.xiaomi.accountsdk.utils;

import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public class DiagnosisLog {
    private static DiagnosisLogInterface sLogger = new DiagnosisLogInterface() { // from class: com.xiaomi.accountsdk.utils.DiagnosisLog.1
        @Override // com.xiaomi.accountsdk.utils.DiagnosisLogInterface
        public String logPostRequest(String str, Map<String, String> map, String str2, Map<String, String> map2, Map<String, String> map3, Map<String, String> map4) {
            return null;
        }

        @Override // com.xiaomi.accountsdk.utils.DiagnosisLogInterface
        public void logRequestException(Exception exc) {
        }

        @Override // com.xiaomi.accountsdk.utils.DiagnosisLogInterface
        public void logResponse(String str, String str2, Map<String, List<String>> map, Map<String, String> map2) {
        }

        @Override // com.xiaomi.accountsdk.utils.DiagnosisLogInterface
        public void logResponseCode(String str, int i) {
        }
    };

    public static DiagnosisLogInterface get() {
        return sLogger;
    }
}
