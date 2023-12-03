package com.xiaomi.onetrack.c;

import android.content.Context;
import android.text.TextUtils;
import com.android.settings.search.provider.SettingsProvider;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.x;
import java.util.HashMap;
import miui.yellowpage.Tag;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class f {
    public static final JSONObject a = new JSONObject();
    private Context f;
    private JSONObject g;
    private String[] h;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class a {
        private static final f a = new f();
    }

    private f() {
        this.g = null;
        this.h = new String[2];
        this.f = com.xiaomi.onetrack.e.a.a();
    }

    public static f a() {
        return a.a;
    }

    private void d() {
        if (p.a) {
            if (TextUtils.isEmpty(this.h[0]) || TextUtils.isEmpty(this.h[1])) {
                p.a("SecretKeyManager", "key or sid is invalid!");
            } else {
                p.a("SecretKeyManager", "key  and sid is valid! ");
            }
        }
    }

    private JSONObject e() {
        JSONObject jSONObject = this.g;
        if (jSONObject == null && (jSONObject = f()) != null) {
            this.g = jSONObject;
        }
        return jSONObject == null ? c() : jSONObject;
    }

    private JSONObject f() {
        try {
            String g = aa.g();
            if (TextUtils.isEmpty(g)) {
                return null;
            }
            return new JSONObject(b.b(this.f, g));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized String[] b() {
        JSONObject e = e();
        this.h[0] = e != null ? e.optString(SettingsProvider.ARGS_KEY) : "";
        this.h[1] = e != null ? e.optString(Tag.TagYellowPage.YID) : "";
        d();
        return this.h;
    }

    public JSONObject c() {
        try {
        } catch (Exception e) {
            p.b("SecretKeyManager", "requestSecretData: " + e.toString());
        }
        if (q.a("SecretKeyManager")) {
            return a;
        }
        byte[] a2 = com.xiaomi.onetrack.c.a.a();
        String a3 = c.a(e.a(a2));
        HashMap hashMap = new HashMap();
        hashMap.put("secretKey", a3);
        String b = com.xiaomi.onetrack.f.b.b(x.a().e(), hashMap, true);
        if (!TextUtils.isEmpty(b)) {
            JSONObject jSONObject = new JSONObject(b);
            int optInt = jSONObject.optInt(Tag.TagWebService.CommonResult.RESULT_CODE);
            JSONObject optJSONObject = jSONObject.optJSONObject("data");
            if (optInt == 0 && optJSONObject != null) {
                String optString = optJSONObject.optString(SettingsProvider.ARGS_KEY);
                String optString2 = optJSONObject.optString(Tag.TagYellowPage.YID);
                if (!TextUtils.isEmpty(optString) && !TextUtils.isEmpty(optString2)) {
                    String a4 = c.a(com.xiaomi.onetrack.c.a.b(c.a(optString), a2));
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put(SettingsProvider.ARGS_KEY, a4);
                    jSONObject2.put(Tag.TagYellowPage.YID, optString2);
                    this.g = jSONObject2;
                    aa.a(b.a(this.f, jSONObject2.toString()));
                    aa.i(System.currentTimeMillis());
                }
            }
        }
        return this.g;
    }
}
