package com.xiaomi.onetrack.api;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class w implements Runnable {
    final /* synthetic */ g a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public w(g gVar) {
        this.a = gVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        d dVar;
        dVar = this.a.b;
        com.xiaomi.onetrack.b.h.a(dVar);
    }
}
