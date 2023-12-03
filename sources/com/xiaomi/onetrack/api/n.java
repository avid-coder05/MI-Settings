package com.xiaomi.onetrack.api;

import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.v;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class n implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ long b;
    final /* synthetic */ g c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public n(g gVar, String str, long j) {
        this.c = gVar;
        this.a = str;
        this.b = j;
    }

    @Override // java.lang.Runnable
    public void run() {
        Configuration configuration;
        JSONObject d;
        Configuration configuration2;
        OneTrack.IEventHook iEventHook;
        v vVar;
        try {
            configuration = this.c.f;
            if (!configuration.isAutoTrackActivityAction()) {
                p.a("OneTrackImp", "config.autoTrackActivityAction is false, ignore onetrack_pa pause event");
                return;
            }
            d = this.c.d("onetrack_pa");
            String str = this.a;
            long j = this.b;
            configuration2 = this.c.f;
            iEventHook = this.c.h;
            vVar = this.c.i;
            com.xiaomi.onetrack.util.aa.i(c.a(str, "onetrack_pa", j, configuration2, iEventHook, d, vVar));
        } catch (Exception e) {
            p.b("OneTrackImp", "savePageEndData error:" + e.toString());
        }
    }
}
