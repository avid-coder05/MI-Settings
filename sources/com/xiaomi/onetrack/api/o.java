package com.xiaomi.onetrack.api;

import android.text.TextUtils;
import com.xiaomi.onetrack.util.p;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class o implements Runnable {
    final /* synthetic */ boolean a;
    final /* synthetic */ g b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public o(g gVar, boolean z) {
        this.b = gVar;
        this.a = z;
    }

    @Override // java.lang.Runnable
    public void run() {
        d dVar;
        try {
            String z = com.xiaomi.onetrack.util.aa.z();
            if (TextUtils.isEmpty(z)) {
                return;
            }
            JSONObject jSONObject = new JSONObject(z);
            JSONObject put = jSONObject.optJSONObject("B").put("app_end", this.a);
            dVar = this.b.b;
            dVar.a("onetrack_pa", jSONObject.put("B", put).toString());
            if (p.a) {
                p.a("OneTrackImp", "trackPageEndAuto");
            }
            com.xiaomi.onetrack.util.aa.i("");
        } catch (Exception e) {
            p.b("OneTrackImp", "trackPageEndAuto error:" + e.toString());
        }
    }
}
