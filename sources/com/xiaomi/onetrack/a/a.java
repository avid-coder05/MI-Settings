package com.xiaomi.onetrack.a;

import android.text.TextUtils;
import com.xiaomi.onetrack.util.DeviceUtil;
import com.xiaomi.onetrack.util.i;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.x;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.yellowpage.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class a {
    private static AtomicBoolean p = new AtomicBoolean(false);
    private static ConcurrentHashMap<String, Long> u = new ConcurrentHashMap<>();

    private static JSONArray a(JSONArray jSONArray, JSONArray jSONArray2) {
        int i = 0;
        while (jSONArray2 != null) {
            try {
                if (i >= jSONArray2.length()) {
                    break;
                }
                JSONObject optJSONObject = jSONArray2.optJSONObject(i);
                String optString = optJSONObject.optString(Tag.TagWebService.CommonResult.RESULT_TYPE_EVENT);
                int i2 = 0;
                while (true) {
                    if (jSONArray == null || i2 >= jSONArray.length()) {
                        break;
                    } else if (TextUtils.equals(optString, jSONArray.optJSONObject(i2).optString(Tag.TagWebService.CommonResult.RESULT_TYPE_EVENT))) {
                        jSONArray.remove(i2);
                        break;
                    } else {
                        i2++;
                    }
                }
                if (!optJSONObject.has("status") || (optJSONObject.has("status") && !TextUtils.equals(optJSONObject.optString("status"), "deleted"))) {
                    if (jSONArray == null) {
                        jSONArray = new JSONArray();
                    }
                    jSONArray.put(optJSONObject);
                }
                i++;
            } catch (Exception e) {
                p.b("AppConfigUpdater", "mergeEventsElement error:" + e.toString());
            }
        }
        return jSONArray;
    }

    private static JSONObject a(String str, JSONObject jSONObject) {
        try {
            k e = g.a().e(str);
            jSONObject.put("events", a(e != null ? e.e.optJSONArray("events") : null, jSONObject.optJSONArray("events")));
            return jSONObject;
        } catch (Exception e2) {
            p.b("AppConfigUpdater", "mergeConfig: " + e2.toString());
            return null;
        }
    }

    public static void a(String str) {
        i.a(new b(str));
    }

    public static void a(String str, List<String> list) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.optInt(Tag.TagWebService.CommonResult.RESULT_CODE) == 0) {
                d(list);
                a(jSONObject.optJSONObject("data").optJSONArray("apps"), list);
            }
        } catch (Exception e) {
            p.a("AppConfigUpdater", "saveAppCloudData: " + e.toString());
        }
    }

    private static void a(String str, JSONObject jSONObject, long j) throws JSONException {
        int optInt = jSONObject == null ? 0 : jSONObject.optInt("version");
        int d = g.a().d(str);
        p.a("AppConfigUpdater", "local version: " + d + ", server version: " + optInt);
        if (d > 0 && optInt <= d) {
            a(jSONObject, j);
            return;
        }
        int optInt2 = jSONObject != null ? jSONObject.optInt("type") : -1;
        p.a("AppConfigUpdater", "type: " + optInt2);
        if (optInt2 == 0 || optInt2 == 1) {
            a(jSONObject, j, optInt);
        } else if (optInt2 == 2) {
            b(jSONObject, j);
        } else {
            p.a("AppConfigUpdater", "handleData do nothing!");
        }
    }

    private static void a(List<String> list, long j) {
        try {
            ArrayList<k> arrayList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                k kVar = new k();
                kVar.a = list.get(i);
                kVar.b = 100L;
                kVar.c = j;
                arrayList.add(kVar);
            }
            g.a().a(arrayList);
        } catch (Exception e) {
            p.b("AppConfigUpdater", "handleError" + e.toString());
        }
    }

    private static void a(List<String> list, long j, List<String> list2) {
        try {
            if (list.size() != list2.size()) {
                list.removeAll(list2);
                a(list, j);
            }
        } catch (Exception e) {
            p.b("AppConfigUpdater", "handleInvalidAppIds error:" + e.toString());
        }
    }

    private static void a(JSONArray jSONArray, List<String> list) throws JSONException {
        p.a("AppConfigUpdater", "updateDataToDb start");
        long currentTimeMillis = System.currentTimeMillis() + 86400000 + new Random().nextInt(86400000);
        if (jSONArray == null || jSONArray.length() <= 0) {
            a(list, currentTimeMillis);
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i);
            String optString = optJSONObject == null ? "" : optJSONObject.optString("appId");
            p.a("AppConfigUpdater", "appId: " + optString);
            if (!TextUtils.isEmpty(optString)) {
                arrayList.add(optString);
                a(optString, optJSONObject, currentTimeMillis);
            }
        }
        a(list, currentTimeMillis, arrayList);
    }

    public static void a(JSONObject jSONObject) {
        i.a(new c(jSONObject));
    }

    private static void a(JSONObject jSONObject, long j) {
        ArrayList<k> arrayList = new ArrayList<>();
        if (jSONObject != null) {
            k kVar = new k();
            kVar.a = jSONObject.optString("appId");
            kVar.c = j;
            arrayList.add(kVar);
        }
        if (arrayList.isEmpty()) {
            p.a("AppConfigUpdater", "updateMinVersionData no timestamp can be updated!");
        } else {
            g.a().a(arrayList);
        }
    }

    private static void a(JSONObject jSONObject, long j, int i) throws JSONException {
        ArrayList<k> arrayList = new ArrayList<>();
        if (jSONObject != null) {
            k kVar = new k();
            kVar.d = jSONObject.optString("hash");
            kVar.a = jSONObject.optString("appId");
            kVar.b = b(jSONObject);
            kVar.c = j;
            if (jSONObject.has("events")) {
                kVar.e = jSONObject;
            } else {
                k e = g.a().e(kVar.a);
                JSONObject jSONObject2 = e == null ? null : e.e;
                if (jSONObject2 != null) {
                    jSONObject2.put("version", i);
                    kVar.e = jSONObject2;
                }
            }
            arrayList.add(kVar);
        }
        if (arrayList.isEmpty()) {
            p.a("AppConfigUpdater", "handleFullOrNoNewData no configuration can be updated!");
        } else {
            g.a().a(arrayList);
        }
    }

    private static int b(JSONObject jSONObject) {
        try {
            int optInt = jSONObject.optInt("sample", 100);
            if (optInt < 0 || optInt > 100) {
                return 100;
            }
            return optInt;
        } catch (Exception e) {
            p.a("AppConfigUpdater", "getCommonSample Exception:" + e.getMessage());
            return 100;
        }
    }

    public static void b(String str) {
        if (!d(str)) {
            p.a("AppConfigUpdater", "AppConfigUpdater Does not meet prerequisites for request");
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        b(arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void b(List<String> list) {
        p.a("AppConfigUpdater", "pullCloudData start ");
        if (q.a("AppConfigUpdater")) {
            return;
        }
        if (p.compareAndSet(false, true)) {
            HashMap hashMap = new HashMap();
            try {
                try {
                    hashMap.put("oa", com.xiaomi.onetrack.util.oaid.a.a().a(com.xiaomi.onetrack.e.a.b()));
                    hashMap.put("ov", q.h());
                    hashMap.put("ob", q.d());
                    hashMap.put("ii", q.x() ? "1" : "0");
                    hashMap.put("sv", "1.2.9");
                    hashMap.put("appVer", com.xiaomi.onetrack.e.a.c());
                    hashMap.put("av", q.i());
                    hashMap.put("ml", DeviceUtil.c());
                    hashMap.put("re", q.y());
                    hashMap.put("ail", c(list));
                    hashMap.put("sender", com.xiaomi.onetrack.e.a.e());
                    hashMap.put("platform", "Android");
                    String c = x.a().c();
                    p.a("AppConfigUpdater", "pullData:" + c);
                    String b = com.xiaomi.onetrack.f.b.b(c, hashMap, true);
                    p.a("AppConfigUpdater", "response:" + b);
                    a(b, list);
                } catch (Exception e) {
                    p.b("AppConfigUpdater", "pullCloudData error: " + e.getMessage());
                }
            } finally {
                p.set(false);
            }
        }
    }

    private static void b(JSONObject jSONObject, long j) {
        ArrayList<k> arrayList = new ArrayList<>();
        if (jSONObject == null || !jSONObject.has("events")) {
            p.a("AppConfigUpdater", "handleIncrementalUpdate config is not change!");
        } else {
            k kVar = new k();
            kVar.d = jSONObject.optString("hash");
            String optString = jSONObject.optString("appId");
            kVar.a = optString;
            kVar.b = b(jSONObject);
            kVar.c = j;
            kVar.e = a(optString, jSONObject);
            arrayList.add(kVar);
        }
        if (arrayList.isEmpty()) {
            p.a("AppConfigUpdater", "handleIncrementalUpdate no configuration can be updated!");
        } else {
            g.a().a(arrayList);
        }
    }

    private static String c(List<String> list) {
        JSONArray jSONArray = new JSONArray();
        try {
            JSONObject jSONObject = new JSONObject();
            for (int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                jSONObject.put("appId", str);
                jSONObject.put("hash", g.a().c(str));
                jSONArray.put(jSONObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONArray.toString();
    }

    private static void d(List<String> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            u.put(list.get(i), Long.valueOf(System.currentTimeMillis() + 1800000 + ((long) (Math.random() * 1800000.0d))));
        }
    }

    private static boolean d(String str) {
        if (!com.xiaomi.onetrack.f.c.a()) {
            p.a("AppConfigUpdater", "net is not connected!");
            return false;
        }
        k e = g.a().e(str);
        if (e == null) {
            return true;
        }
        long j = e.c;
        return j < System.currentTimeMillis() || j - System.currentTimeMillis() > 172800000;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean e(String str) {
        Long l = u.get(str);
        return l == null || l.longValue() - System.currentTimeMillis() < 0 || l.longValue() - System.currentTimeMillis() > 3600000;
    }
}
