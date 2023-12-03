package com.xiaomi.onetrack.api;

import android.text.TextUtils;
import com.xiaomi.onetrack.Configuration;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class h implements Runnable {
    final /* synthetic */ g a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public h(g gVar) {
        this.a = gVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        Configuration configuration;
        Configuration configuration2;
        if (com.xiaomi.onetrack.util.aa.B() == 0) {
            com.xiaomi.onetrack.util.aa.n(System.currentTimeMillis());
        }
        configuration = this.a.f;
        if (!TextUtils.isEmpty(configuration.getInstanceId())) {
            com.xiaomi.onetrack.util.o a = com.xiaomi.onetrack.util.o.a();
            configuration2 = this.a.f;
            a.a(configuration2.getInstanceId());
        }
        this.a.j();
        com.xiaomi.onetrack.util.d.a();
        com.xiaomi.onetrack.b.h.c(false);
    }
}
