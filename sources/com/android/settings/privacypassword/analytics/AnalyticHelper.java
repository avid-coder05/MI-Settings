package com.android.settings.privacypassword.analytics;

import com.android.settingslib.util.MiStatInterfaceUtils;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes2.dex */
public class AnalyticHelper {
    private static void recordCountEvent(String str, String str2) {
        MiStatInterfaceUtils.recordCountEvent(str, str2);
    }

    private static void recordCountEvent(String str, String str2, Map<String, String> map) {
        MiStatInterfaceUtils.recordCountEvent(str, str2, map);
    }

    public static void statsApp1UnlockBindingPopup(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("action", str);
        recordCountEvent("privacypassword", "app1_unlock_binding_popup", hashMap);
    }

    public static void statsClickPrivateForget(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("click_time", str);
        recordCountEvent("privacypassword", "private_forget", hashMap);
    }

    public static void statsForgetPageBindingResult(String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put(str, str2);
        recordCountEvent("privacypassword", "set1_forget_page_binding_result", hashMap);
    }

    public static void statsPrivateForgetFinish() {
        recordCountEvent("privacypassword", "private_forget_finish");
    }

    public static void statsPrivateMistakeReachMax(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("account_status", str);
        recordCountEvent("privacypassword", "private_mistake_reach_max", hashMap);
    }

    public static void statsSet1ForgetPageAccount(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_status", str);
        recordCountEvent("privacypassword", "set1_forget_page_account_status", hashMap);
    }

    public static void statsSet1PageAccount(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("account_status", str);
        recordCountEvent("privacypassword", "set1_page_account_status", hashMap);
    }
}
