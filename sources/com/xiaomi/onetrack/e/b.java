package com.xiaomi.onetrack.e;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.f.c;
import com.xiaomi.onetrack.util.DeviceUtil;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.ac;
import com.xiaomi.onetrack.util.o;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.v;
import miui.provider.ExtraContacts;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class b {
    private String f;
    private String g;
    private String h;
    private int i;
    private JSONObject j;
    private long k;

    /* renamed from: com.xiaomi.onetrack.e.b$b  reason: collision with other inner class name */
    /* loaded from: classes2.dex */
    public static class C0032b {
        public static String A = "sdk_mode";
        public static String B = "ot_first_day";
        public static String C = "ot_test_env";
        public static String D = "ot_privacy_policy";
        public static String E = "market_name";
        public static String a = "event";
        public static String b = "imei";
        public static String c = "oaid";
        public static String e = "gaid";
        public static String g = "instance_id";
        public static String h = "mfrs";
        public static String i = "model";
        public static String j = "platform";
        public static String k = "miui";
        public static String l = "build";
        public static String m = "os_ver";
        public static String n = "app_id";
        public static String o = "app_ver";
        public static String p = "pkg";
        public static String q = "channel";
        public static String r = "e_ts";
        public static String s = "tz";
        public static String t = "net";
        public static String u = "region";
        public static String v = "plugin_id";
        public static String w = "sdk_ver";
        public static String x = "uid";
        public static String y = "uid_type";
        public static String z = "sid";
    }

    public static JSONObject a(String str, Configuration configuration, OneTrack.IEventHook iEventHook, v vVar) throws JSONException {
        return a(str, configuration, iEventHook, "", vVar);
    }

    public static JSONObject a(String str, Configuration configuration, OneTrack.IEventHook iEventHook, String str2, v vVar) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        Context b = a.b();
        jSONObject.put(C0032b.a, str);
        if (!(q.a() ? q.x() : configuration.isInternational())) {
            jSONObject.put(C0032b.b, DeviceUtil.b(b));
            jSONObject.put(C0032b.c, com.xiaomi.onetrack.util.oaid.a.a().a(b));
        } else if (iEventHook != null && iEventHook.isRecommendEvent(str)) {
            String p = DeviceUtil.p(b);
            if (!TextUtils.isEmpty(p)) {
                jSONObject.put(C0032b.e, p);
            }
        }
        jSONObject.put(C0032b.g, o.a().b());
        jSONObject.put(C0032b.h, DeviceUtil.e());
        jSONObject.put(C0032b.i, DeviceUtil.c());
        jSONObject.put(C0032b.j, "Android");
        jSONObject.put(C0032b.k, q.h());
        jSONObject.put(C0032b.l, q.d());
        jSONObject.put(C0032b.m, q.i());
        jSONObject.put(C0032b.o, a.c());
        jSONObject.put(C0032b.r, System.currentTimeMillis());
        jSONObject.put(C0032b.s, q.b());
        jSONObject.put(C0032b.t, c.a(b).toString());
        jSONObject.put(C0032b.u, q.y());
        jSONObject.put(C0032b.w, "1.2.9");
        jSONObject.put(C0032b.n, configuration.getAppId());
        jSONObject.put(C0032b.p, a.e());
        jSONObject.put(C0032b.q, !TextUtils.isEmpty(configuration.getChannel()) ? configuration.getChannel() : ExtraContacts.DefaultAccount.NAME);
        a(jSONObject, configuration, str2);
        a(jSONObject, b);
        jSONObject.put(C0032b.z, q.r());
        jSONObject.put(C0032b.A, (configuration.getMode() != null ? configuration.getMode() : OneTrack.Mode.APP).getType());
        jSONObject.put(C0032b.B, ac.d(aa.B()));
        if (p.c) {
            jSONObject.put(C0032b.C, true);
        }
        jSONObject.put(C0032b.D, vVar.a());
        jSONObject.put(C0032b.E, DeviceUtil.d());
        return jSONObject;
    }

    private static void a(JSONObject jSONObject, Context context) throws JSONException {
        String u = aa.u();
        String w = aa.w();
        if (TextUtils.isEmpty(u) || TextUtils.isEmpty(w)) {
            return;
        }
        jSONObject.put(C0032b.x, u);
        jSONObject.put(C0032b.y, w);
    }

    private static void a(JSONObject jSONObject, Configuration configuration, String str) throws JSONException {
        if (TextUtils.isEmpty(str)) {
            jSONObject.put(C0032b.v, configuration.getPluginId());
        } else {
            jSONObject.put(C0032b.v, str);
        }
    }

    public void a(int i) {
        this.i = i;
    }

    public void a(String str) {
        this.f = str;
    }

    public void a(JSONObject jSONObject) {
        this.j = jSONObject;
    }

    public String b() {
        return this.f;
    }

    public void b(long j) {
        this.k = j;
    }

    public void b(String str) {
        this.g = str;
    }

    public String c() {
        return this.g;
    }

    public void c(String str) {
        this.h = str;
    }

    public String d() {
        return this.h;
    }

    public int e() {
        return this.i;
    }

    public JSONObject f() {
        return this.j;
    }

    public boolean h() {
        try {
            JSONObject jSONObject = this.j;
            if (jSONObject == null || !jSONObject.has("H") || !this.j.has("B") || TextUtils.isEmpty(this.f)) {
                return false;
            }
            return !TextUtils.isEmpty(this.g);
        } catch (Exception e) {
            p.b("Event", "check event isValid error, ", e);
            return false;
        }
    }
}
