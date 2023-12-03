package com.xiaomi.onetrack.b;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.vip.VipService;

/* loaded from: classes2.dex */
public class l extends Handler {
    private final int e;
    private final int f;
    private final int g;
    private int h;
    private AtomicBoolean i;
    private BroadcastReceiver j;

    public l(Looper looper) {
        super(looper);
        this.e = VipService.VIP_SERVICE_FAILURE;
        this.f = 10000;
        this.g = 1200000;
        this.h = 10000;
        this.i = new AtomicBoolean(false);
        this.j = new m(this);
        a(com.xiaomi.onetrack.e.a.b());
    }

    private void a(int i, long j) {
        removeMessages(i);
        com.xiaomi.onetrack.util.p.a("UploadTimer", "will post msg, prio=" + i + ", delay=" + j);
        sendEmptyMessageDelayed(i, j);
    }

    private void a(Context context) {
        if (context == null) {
            return;
        }
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            context.registerReceiver(this.j, intentFilter, null, this);
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.a("UploadTimer", "registerNetReceiver: " + e);
        }
    }

    private void b() {
        if (p.a().a(2)) {
            this.h = 10000;
            com.xiaomi.onetrack.util.p.a("UploadTimer", "retry success");
            return;
        }
        removeMessages(VipService.VIP_SERVICE_FAILURE);
        int i = this.h * 2;
        this.h = i;
        if (i > 1200000) {
            this.h = 1200000;
        }
        com.xiaomi.onetrack.util.p.a("UploadTimer", "will restart retry msg after " + this.h);
        sendEmptyMessageDelayed(VipService.VIP_SERVICE_FAILURE, (long) this.h);
    }

    public void a() {
        com.xiaomi.onetrack.util.i.a(new o(this));
    }

    public void a(int i, boolean z) {
        if (hasMessages(VipService.VIP_SERVICE_FAILURE)) {
            com.xiaomi.onetrack.util.p.a("UploadTimer", "in retry mode, return, prio=" + i);
            return;
        }
        if (z) {
            removeMessages(i);
        }
        if (hasMessages(i)) {
            return;
        }
        long a = z ? 0L : com.xiaomi.onetrack.a.m.a(i);
        com.xiaomi.onetrack.util.p.a("UploadTimer", "will check prio=" + i + ", delay=" + a);
        a(i, a);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        super.handleMessage(message);
        if (!com.xiaomi.onetrack.a.m.a() || !com.xiaomi.onetrack.a.m.c() || com.xiaomi.onetrack.a.m.b()) {
            com.xiaomi.onetrack.util.p.a("UploadTimer", "不用处理消息, available=" + com.xiaomi.onetrack.a.m.a() + ", 是否有网=" + com.xiaomi.onetrack.a.m.c() + ", 数据库是否为空=" + com.xiaomi.onetrack.a.m.b());
            return;
        }
        int i = message.what;
        if (i == 1000) {
            b();
            return;
        }
        boolean a = p.a().a(i);
        com.xiaomi.onetrack.util.p.a("UploadTimer", "handleCheckUpload ret=" + a + ", prio=" + i);
        if (a) {
            return;
        }
        com.xiaomi.onetrack.util.p.a("UploadTimer", "handleCheckUpload failed, will check if need to send retry msg");
        if (hasMessages(VipService.VIP_SERVICE_FAILURE)) {
            return;
        }
        sendEmptyMessageDelayed(VipService.VIP_SERVICE_FAILURE, this.h);
        com.xiaomi.onetrack.util.p.a("UploadTimer", "fire retry timer after " + this.h);
    }
}
