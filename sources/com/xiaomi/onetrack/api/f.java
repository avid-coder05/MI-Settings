package com.xiaomi.onetrack.api;

/* loaded from: classes2.dex */
class f implements Runnable {
    final /* synthetic */ Thread a;
    final /* synthetic */ Throwable b;
    final /* synthetic */ e c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public f(e eVar, Thread thread, Throwable th) {
        this.c = eVar;
        this.a = thread;
        this.b = th;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.c.a(this.a, this.b);
    }
}
