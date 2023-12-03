package com.xiaomi.onetrack.b;

/* loaded from: classes2.dex */
class n implements Runnable {
    final /* synthetic */ m a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public n(m mVar) {
        this.a = mVar;
    }

    @Override // java.lang.Runnable
    public void run() {
        this.a.a.a();
        try {
            boolean a = com.xiaomi.onetrack.f.c.a();
            com.xiaomi.onetrack.util.p.a("UploadTimer", "UploadTimer netReceiver, 网络是否可用=" + a);
            if (a) {
                int[] iArr = {0, 1, 2};
                for (int i = 0; i < 3; i++) {
                    int i2 = iArr[i];
                    int a2 = com.xiaomi.onetrack.a.m.a(i2);
                    if (!this.a.a.hasMessages(i2)) {
                        this.a.a.sendEmptyMessageDelayed(i2, a2);
                    }
                }
            }
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.a("UploadTimer", "netReceiver: " + e);
        }
    }
}
