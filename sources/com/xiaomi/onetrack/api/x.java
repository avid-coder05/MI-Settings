package com.xiaomi.onetrack.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class x extends BroadcastReceiver {
    final /* synthetic */ g a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public x(g gVar) {
        this.a = gVar;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        try {
            com.xiaomi.onetrack.b.a.a(new y(this, intent));
        } catch (Throwable unused) {
        }
    }
}
