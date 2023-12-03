package com.xiaomi.onetrack.api;

/* loaded from: classes2.dex */
class ak implements Runnable {
    final /* synthetic */ aj a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ak(aj ajVar) {
        this.a = ajVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (com.xiaomi.onetrack.b.h.b()) {
            this.a.b();
        }
    }
}
