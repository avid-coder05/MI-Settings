package com.xiaomi.onetrack.a;

import com.xiaomi.onetrack.util.p;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class h implements Runnable {
    final /* synthetic */ ArrayList a;
    final /* synthetic */ g b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public h(g gVar, ArrayList arrayList) {
        this.b = gVar;
        this.a = arrayList;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (p.a) {
            p.a("ConfigDbManager", "update: " + this.a);
        }
        this.b.b(this.a);
    }
}
