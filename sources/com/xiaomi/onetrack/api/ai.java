package com.xiaomi.onetrack.api;

import com.xiaomi.onetrack.b.p;

/* loaded from: classes2.dex */
class ai implements Runnable {
    final /* synthetic */ int a;
    final /* synthetic */ ah b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ai(ah ahVar, int i) {
        this.b = ahVar;
        this.a = i;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (com.xiaomi.onetrack.b.h.b() && this.a == 2) {
            p.a().a(0, true);
            p.a().a(1, true);
        }
    }
}
