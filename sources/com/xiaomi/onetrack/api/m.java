package com.xiaomi.onetrack.api;

import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.v;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class m implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ boolean b;
    final /* synthetic */ g c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public m(g gVar, String str, boolean z) {
        this.c = gVar;
        this.a = str;
        this.b = z;
    }

    @Override // java.lang.Runnable
    public void run() {
        Configuration configuration;
        JSONObject d;
        Configuration configuration2;
        OneTrack.IEventHook iEventHook;
        v vVar;
        d dVar;
        try {
            configuration = this.c.f;
            if (!configuration.isAutoTrackActivityAction()) {
                p.a("OneTrackImp", "config.autoTrackActivityAction is false, ignore onetrack_pa resume event");
                return;
            }
            d = this.c.d("onetrack_pa");
            String str = this.a;
            configuration2 = this.c.f;
            iEventHook = this.c.h;
            boolean z = this.b;
            vVar = this.c.i;
            String a = c.a(str, "onetrack_pa", configuration2, iEventHook, d, z, vVar);
            dVar = this.c.b;
            dVar.a("onetrack_pa", a);
            if (p.a) {
                p.a("OneTrackImp", "trackPageStartAuto");
            }
        } catch (Exception e) {
            p.b("OneTrackImp", "auto trackPageStartAuto error: " + e.toString());
        }
    }
}
