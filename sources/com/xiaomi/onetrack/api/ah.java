package com.xiaomi.onetrack.api;

import android.content.Context;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.util.i;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.v;

/* loaded from: classes2.dex */
public class ah implements d {
    private Configuration d;
    private v e;

    public ah(Context context, Configuration configuration, v vVar) {
        com.xiaomi.onetrack.e.f.a(context);
        this.d = configuration;
        this.e = vVar;
    }

    private boolean b(String str) {
        long b = com.xiaomi.onetrack.a.g.a().b(this.d.getAppId(), str);
        long abs = Math.abs(com.xiaomi.onetrack.util.oaid.a.a().a(com.xiaomi.onetrack.e.a.b()).hashCode()) % 100;
        boolean z = b > abs;
        p.a("OneTrackLocalImp", "shouldUploadBySampling " + str + ",  shouldUpload=" + z + ", sample=" + b + ", val=" + abs);
        return !z;
    }

    @Override // com.xiaomi.onetrack.api.d
    public void a(int i) {
        i.a(new ai(this, i));
    }

    @Override // com.xiaomi.onetrack.api.d
    public void a(String str, String str2) {
        v vVar = this.e;
        if (vVar != null && !vVar.a(str)) {
            p.a("OneTrackLocalImp", "The privacy policy is not permitted, and the event is not basic or recommend event or custom dau event, skip it.");
        } else if (b(str, str2)) {
            if (!com.xiaomi.onetrack.b.h.b()) {
                com.xiaomi.onetrack.b.h.a(str, str2);
                return;
            }
            com.xiaomi.onetrack.b.h.a(this);
            if (p.a && !str.equalsIgnoreCase("onetrack_bug_report")) {
                p.a("OneTrackLocalImp", "track data:" + str2);
            }
            com.xiaomi.onetrack.a.a.a(this.d.getAppId());
            if (a(str)) {
                return;
            }
            com.xiaomi.onetrack.e.d.a(this.d.getAppId(), com.xiaomi.onetrack.e.a.e(), str, str2);
        }
    }

    public boolean a(String str) {
        boolean z;
        boolean z2;
        boolean z3;
        try {
            z2 = com.xiaomi.onetrack.a.g.a().a(this.d.getAppId(), "disable_log");
            try {
                z = com.xiaomi.onetrack.a.g.a().a(this.d.getAppId(), str, "disable_log", false);
            } catch (Exception e) {
                e = e;
                z = false;
            }
        } catch (Exception e2) {
            e = e2;
            z = false;
            z2 = false;
        }
        try {
            z3 = b(str);
        } catch (Exception e3) {
            e = e3;
            p.a("OneTrackLocalImp", "isDisableTrack: " + e.toString());
            z3 = false;
            if (z2) {
            }
        }
        return !z2 || z || z3;
    }

    public boolean b(String str, String str2) {
        if (OneTrack.isDisable() || OneTrack.isUseSystemNetTrafficOnly()) {
            return false;
        }
        if ((str != null && str.equals("onetrack_bug_report")) || str2 == null || str2.length() * 2 <= 102400) {
            return true;
        }
        p.a("OneTrackLocalImp", "Event size exceed limitation!");
        return false;
    }
}
