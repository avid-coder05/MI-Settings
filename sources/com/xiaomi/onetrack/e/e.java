package com.xiaomi.onetrack.e;

/* loaded from: classes2.dex */
final class e implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ String b;
    final /* synthetic */ String c;
    final /* synthetic */ String d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public e(String str, String str2, String str3, String str4) {
        this.a = str;
        this.b = str2;
        this.c = str3;
        this.d = str4;
    }

    @Override // java.lang.Runnable
    public void run() {
        com.xiaomi.onetrack.b.b.a().a(com.xiaomi.onetrack.d.b.a(this.a, this.b, this.c, this.d));
    }
}
