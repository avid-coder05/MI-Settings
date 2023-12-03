package com.xiaomi.onetrack.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.xiaomi.onetrack.util.oaid.a;
import java.util.HashMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class g implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ String b;
    final /* synthetic */ String c;
    final /* synthetic */ d d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public g(d dVar, String str, String str2, String str3) {
        this.d = dVar;
        this.a = str;
        this.b = str2;
        this.c = str3;
    }

    @Override // java.lang.Runnable
    public void run() {
        String str;
        String str2;
        Context context;
        Context context2;
        Handler handler;
        try {
            if (TextUtils.isEmpty(this.a)) {
                return;
            }
            if (this.a.contains("http://")) {
                str2 = this.a + "/api/open/device/writeBack";
            } else {
                str2 = "http://" + this.a + "/api/open/device/writeBack";
            }
            HashMap hashMap = new HashMap();
            hashMap.put("instanceId", o.a().b());
            context = this.d.h;
            hashMap.put("imei", DeviceUtil.b(context));
            a a = a.a();
            context2 = this.d.h;
            hashMap.put("oaid", a.a(context2));
            hashMap.put("projectId", this.b);
            hashMap.put("user", this.c);
            String b = com.xiaomi.onetrack.f.b.b(str2, hashMap, false);
            if (!TextUtils.isEmpty(b) && !"".equals(b)) {
                this.d.a(b);
                return;
            }
            Message obtain = Message.obtain();
            obtain.what = 100;
            Bundle bundle = new Bundle();
            bundle.putString("hint", "注册信息失败，请检查是网络环境是否在内网");
            obtain.setData(bundle);
            handler = this.d.i;
            handler.sendMessage(obtain);
        } catch (Exception e) {
            str = d.a;
            p.b(str, e.getMessage());
        }
    }
}
