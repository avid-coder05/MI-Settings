package com.xiaomi.onetrack.e;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import com.xiaomi.onetrack.util.i;

/* loaded from: classes2.dex */
public class f {
    private static f b;
    private static BroadcastReceiver c = new h();

    private f(Context context) {
        i.a(new g(this, context.getApplicationContext()));
    }

    public static void a(Context context) {
        if (b == null) {
            b = new f(context);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void c(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        context.registerReceiver(c, intentFilter);
    }
}
