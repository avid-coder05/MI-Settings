package com.xiaomi.onetrack.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.xiaomi.onetrack.e.a;
import miui.yellowpage.YellowPageContract;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class f extends BroadcastReceiver {
    final /* synthetic */ d a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public f(d dVar) {
        this.a = dVar;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String str;
        try {
            String stringExtra = intent.getStringExtra(YellowPageContract.Permission.HOST);
            String stringExtra2 = intent.getStringExtra("packagename");
            String stringExtra3 = intent.getStringExtra("projectId");
            String stringExtra4 = intent.getStringExtra("user");
            boolean booleanExtra = intent.getBooleanExtra("logon", false);
            boolean booleanExtra2 = intent.getBooleanExtra("quickuploadon", false);
            String e = a.e();
            if (!TextUtils.isEmpty(stringExtra2) && !"".equals(stringExtra2) && e.equals(stringExtra2)) {
                p.a = booleanExtra;
                p.b = booleanExtra2;
                if (booleanExtra2) {
                    this.a.a(stringExtra, stringExtra3, stringExtra4);
                }
            }
        } catch (Exception e2) {
            str = d.a;
            p.b(str, e2.getMessage());
        }
    }
}
