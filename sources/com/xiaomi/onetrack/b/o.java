package com.xiaomi.onetrack.b;

import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class o implements Runnable {
    final /* synthetic */ l a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public o(l lVar) {
        this.a = lVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        AtomicBoolean atomicBoolean;
        AtomicBoolean atomicBoolean2;
        atomicBoolean = this.a.i;
        if (atomicBoolean.get()) {
            com.xiaomi.onetrack.a.d.b();
        }
        atomicBoolean2 = this.a.i;
        atomicBoolean2.set(true);
    }
}
