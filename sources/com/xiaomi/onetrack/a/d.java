package com.xiaomi.onetrack.a;

import android.text.TextUtils;
import com.xiaomi.onetrack.util.DeviceUtil;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.x;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import miui.provider.MiCloudSmsCmd;
import miui.yellowpage.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class d {
    private static ConcurrentHashMap<Integer, Integer> f = new ConcurrentHashMap<>();

    private static void a(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.optInt(Tag.TagWebService.CommonResult.RESULT_CODE) == 0) {
                String optString = jSONObject.optString("hash");
                JSONObject optJSONObject = jSONObject.optJSONObject("data");
                if (optJSONObject != null) {
                    JSONObject optJSONObject2 = optJSONObject.optJSONObject("regionUrl");
                    if (optJSONObject2 != null) {
                        x.a().a(optJSONObject2);
                    }
                    aa.d(optJSONObject.toString());
                    aa.c(optString);
                }
                aa.j(System.currentTimeMillis() + 86400000 + new Random().nextInt(86400000));
            }
        } catch (JSONException e) {
            p.a("CommonConfigUpdater", "saveCommonCloudData: " + e.toString());
        }
    }

    public static void b() {
        if (e()) {
            f();
        } else {
            p.a("CommonConfigUpdater", "CommonConfigUpdater Does not meet prerequisites for request");
        }
    }

    public static Map<Integer, Integer> c() {
        try {
        } catch (Exception e) {
            p.a("CommonConfigUpdater", "getLevelIntervalConfig: " + e.toString());
        }
        if (f.isEmpty()) {
            String l = aa.l();
            if (!TextUtils.isEmpty(l)) {
                JSONArray optJSONArray = new JSONObject(l).optJSONArray("levels");
                for (int i = 0; i < optJSONArray.length(); i++) {
                    JSONObject jSONObject = optJSONArray.getJSONObject(i);
                    int optInt = jSONObject.optInt(MiCloudSmsCmd.TYPE_LOCATION);
                    int optInt2 = jSONObject.optInt("t");
                    if (optInt > 0 && optInt2 > 0) {
                        f.put(Integer.valueOf(optInt), Integer.valueOf(optInt2));
                    }
                }
            }
            return f.isEmpty() ? g() : f;
        }
        return f;
    }

    private static boolean e() {
        if (!com.xiaomi.onetrack.f.c.a()) {
            p.b("CommonConfigUpdater", "net is not connected!");
            return false;
        } else if (TextUtils.isEmpty(aa.l())) {
            return true;
        } else {
            long j = aa.j();
            return j < System.currentTimeMillis() || j - System.currentTimeMillis() > 172800000;
        }
    }

    private static void f() {
        if (q.a("CommonConfigUpdater")) {
            return;
        }
        HashMap hashMap = new HashMap();
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
            hashMap.put("platform", "Android");
            String d = x.a().d();
            String b = com.xiaomi.onetrack.f.b.b(d, hashMap, true);
            p.a("CommonConfigUpdater", "url:" + d + " response:" + b);
            a(b);
        } catch (IOException e) {
            p.a("CommonConfigUpdater", "requestCloudData: " + e.toString());
        }
    }

    private static HashMap<Integer, Integer> g() {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        hashMap.put(1, 5000);
        hashMap.put(2, 15000);
        hashMap.put(3, 900000);
        return hashMap;
    }
}
