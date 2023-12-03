package com.xiaomi.onetrack.util;

import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.e.a;

/* loaded from: classes2.dex */
public class v {
    private OneTrack.IEventHook f;
    private Configuration g;
    private boolean h;
    private boolean i;
    private long j = 0;

    public v(Configuration configuration) {
        this.g = configuration;
        this.h = aa.k(r.a(configuration));
    }

    private boolean b() {
        if (Math.abs(System.currentTimeMillis() - this.j) > 900000) {
            this.j = System.currentTimeMillis();
            this.i = q.b(a.b());
        }
        return this.i;
    }

    private boolean b(String str) {
        return "onetrack_dau".equals(str) || "onetrack_pa".equals(str);
    }

    private boolean c(String str) {
        OneTrack.IEventHook iEventHook = this.f;
        return iEventHook != null && iEventHook.isRecommendEvent(str);
    }

    private boolean d(String str) {
        OneTrack.IEventHook iEventHook = this.f;
        return iEventHook != null && iEventHook.isCustomDauEvent(str);
    }

    public String a() {
        return this.g.isUseCustomPrivacyPolicy() ? this.h ? "custom_open" : "custom_close" : b() ? "exprience_open" : "exprience_close";
    }

    public void a(OneTrack.IEventHook iEventHook) {
        this.f = iEventHook;
    }

    public boolean a(String str) {
        boolean b;
        if (this.g.isUseCustomPrivacyPolicy()) {
            StringBuilder sb = new StringBuilder();
            sb.append("use custom privacy policy, the policy is ");
            sb.append(this.h ? "open" : "close");
            p.a("PrivacyManager", sb.toString());
            b = this.h;
        } else {
            b = b();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("use system experience plan, the policy is ");
            sb2.append(b ? "open" : "close");
            p.a("PrivacyManager", sb2.toString());
        }
        if (b) {
            return b;
        }
        boolean b2 = b(str);
        boolean c = c(str);
        boolean d = d(str);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("This event ");
        sb3.append(str);
        sb3.append(b2 ? " is " : " is not ");
        sb3.append("basic event and ");
        sb3.append(c ? "is" : "is not");
        sb3.append(" recommend event and ");
        sb3.append(d ? "is" : "is not");
        sb3.append(" custom dau event");
        p.a("PrivacyManager", sb3.toString());
        return b2 || c || d;
    }
}
