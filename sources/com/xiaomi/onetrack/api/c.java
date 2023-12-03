package com.xiaomi.onetrack.api;

import android.content.Context;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.e.b;
import com.xiaomi.onetrack.util.DeviceUtil;
import com.xiaomi.onetrack.util.q;
import com.xiaomi.onetrack.util.r;
import com.xiaomi.onetrack.util.v;
import miui.provider.ExtraTelephony;
import miui.yellowpage.Tag;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class c {
    public static String a(long j, String str, long j2, long j3, Configuration configuration, OneTrack.IEventHook iEventHook, v vVar) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("H", b.a("onetrack_upgrade", configuration, iEventHook, vVar));
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("last_ver_code", j);
        jSONObject2.put("last_ver_name", str);
        jSONObject2.put("cur_ver_code", j2);
        jSONObject2.put("last_upgrade_time", j3);
        jSONObject.put("B", jSONObject2);
        return jSONObject.toString();
    }

    public static String a(Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, v vVar) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("H", b.a("onetrack_dau", configuration, iEventHook, vVar));
        JSONObject jSONObject3 = new JSONObject();
        Context b = com.xiaomi.onetrack.e.a.b();
        boolean s = com.xiaomi.onetrack.util.aa.s();
        if (s) {
            com.xiaomi.onetrack.util.aa.c(false);
        }
        jSONObject3.put("first_open", s);
        if (!(q.a() ? q.x() : configuration.isInternational())) {
            if (configuration.isIMEIEnable()) {
                jSONObject3.put("imeis", DeviceUtil.q(b));
            }
            if (configuration.isIMSIEnable()) {
                jSONObject3.put("imsis", DeviceUtil.v(b));
            }
        }
        jSONObject3.put("config_status", a.a(configuration));
        jSONObject2.put("B", r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(String str, String str2, long j, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, v vVar) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("H", b.a(str2, configuration, iEventHook, vVar));
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(ExtraTelephony.UnderstandInfo.CLASS, str);
        jSONObject3.put("type", 2);
        jSONObject3.put("duration", j);
        jSONObject2.put("B", r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(String str, String str2, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, boolean z, v vVar) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("H", b.a(str2, configuration, iEventHook, vVar));
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(ExtraTelephony.UnderstandInfo.CLASS, str);
        jSONObject3.put("type", 1);
        jSONObject3.put("app_start", z);
        jSONObject2.put("B", r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(String str, String str2, String str3, String str4, String str5, long j, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject, v vVar) throws JSONException {
        JSONObject jSONObject2 = new JSONObject();
        JSONObject a = b.a("onetrack_bug_report", configuration, iEventHook, vVar);
        if (str5 != null) {
            a.put(b.C0032b.o, str5);
        }
        jSONObject2.put("H", a);
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put("exception", str);
        jSONObject3.put("type", str3);
        jSONObject3.put("message", str2);
        jSONObject3.put(Tag.TagSearch.GeoInfo.FEATURE, str4);
        jSONObject3.put("crashtime", j);
        jSONObject2.put("B", r.a(jSONObject3, jSONObject));
        return jSONObject2.toString();
    }

    public static String a(String str, JSONObject jSONObject, Configuration configuration, OneTrack.IEventHook iEventHook, JSONObject jSONObject2, v vVar) throws JSONException {
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put("H", b.a(str, configuration, iEventHook, vVar));
        jSONObject3.put("B", r.a(jSONObject, jSONObject2));
        return jSONObject3.toString();
    }
}
