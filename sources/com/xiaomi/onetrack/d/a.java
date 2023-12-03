package com.xiaomi.onetrack.d;

import com.xiaomi.onetrack.a.g;
import com.xiaomi.onetrack.util.p;
import miui.provider.Weather;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class a extends com.xiaomi.onetrack.e.b {
    public a(String str, String str2, String str3, String str4) {
        try {
            a(str);
            c(str3);
            b(str2);
            b(System.currentTimeMillis());
            a(new JSONObject(str4));
            a(g.a().a(str, str3, Weather.AlertInfo.LEVEL, 1));
        } catch (Exception e) {
            p.b("CustomEvent", "CustomEvent error:" + e.toString());
        }
    }
}
