package com.xiaomi.onetrack.api;

import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.r;
import com.xiaomi.onetrack.util.v;
import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class aa implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ Map b;
    final /* synthetic */ g c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public aa(g gVar, String str, Map map) {
        this.c = gVar;
        this.a = str;
        this.b = map;
    }

    @Override // java.lang.Runnable
    public void run() {
        boolean c;
        JSONObject d;
        Configuration configuration;
        OneTrack.IEventHook iEventHook;
        v vVar;
        d dVar;
        try {
            c = this.c.c(this.a);
            if (c) {
                return;
            }
            JSONObject a = r.a((Map<String, Object>) this.b, true);
            d = this.c.d(this.a);
            String str = this.a;
            configuration = this.c.f;
            iEventHook = this.c.h;
            vVar = this.c.i;
            String a2 = c.a(str, a, configuration, iEventHook, d, vVar);
            dVar = this.c.b;
            dVar.a(this.a, a2);
        } catch (Exception e) {
            p.b("OneTrackImp", "track map error: " + e.toString());
        }
    }
}
