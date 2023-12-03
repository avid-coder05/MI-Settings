package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.Signature;
import android.os.IBinder;
import com.xiaomi.onetrack.util.oaid.a.e;
import com.xiaomi.onetrack.util.p;
import java.security.MessageDigest;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import miui.util.HashUtils;

/* loaded from: classes2.dex */
public class j {
    com.xiaomi.onetrack.util.oaid.a.e a;
    public final LinkedBlockingQueue<IBinder> b = new LinkedBlockingQueue<>(1);
    ServiceConnection c = new ServiceConnection() { // from class: com.xiaomi.onetrack.util.oaid.helpers.OppoDeviceIDHelper$1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                j.this.b.offer(iBinder, 1L, TimeUnit.SECONDS);
            } catch (Exception e) {
                p.a("OppoDeviceIDHelper", e.getMessage());
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            j.this.a = null;
        }
    };
    private String e;

    private String a(String str, Context context) {
        Signature[] signatureArr;
        String packageName = context.getPackageName();
        if (this.e == null) {
            String str2 = null;
            try {
                signatureArr = context.getPackageManager().getPackageInfo(packageName, 64).signatures;
            } catch (Exception e) {
                e.printStackTrace();
                signatureArr = null;
            }
            if (signatureArr != null && signatureArr.length > 0) {
                byte[] byteArray = signatureArr[0].toByteArray();
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.SHA1);
                    if (messageDigest != null) {
                        byte[] digest = messageDigest.digest(byteArray);
                        StringBuilder sb = new StringBuilder();
                        for (byte b : digest) {
                            sb.append(Integer.toHexString((b & 255) | 256).substring(1, 3));
                        }
                        str2 = sb.toString();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            this.e = str2;
        }
        return ((e.a.C0034a) this.a).a(packageName, this.e, str);
    }

    public String a(Context context) {
        String str;
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.heytap.openid", "com.heytap.openid.IdentifyService"));
        intent.setAction("action.com.heytap.openid.OPEN_ID_SERVICE");
        str = "";
        if (context.bindService(intent, this.c, 1)) {
            try {
                try {
                    try {
                        IBinder poll = this.b.poll(1L, TimeUnit.SECONDS);
                        if (poll == null) {
                            try {
                                context.unbindService(this.c);
                            } catch (Exception e) {
                                p.a("OppoDeviceIDHelper", e.getMessage());
                            }
                            return "";
                        }
                        com.xiaomi.onetrack.util.oaid.a.e a = e.a.a(poll);
                        this.a = a;
                        str = a != null ? a("OUID", context) : "";
                        context.unbindService(this.c);
                    } catch (Exception e2) {
                        p.a("OppoDeviceIDHelper", e2.getMessage());
                        context.unbindService(this.c);
                    }
                } catch (Exception e3) {
                    p.a("OppoDeviceIDHelper", e3.getMessage());
                }
            } catch (Throwable th) {
                try {
                    context.unbindService(this.c);
                } catch (Exception e4) {
                    p.a("OppoDeviceIDHelper", e4.getMessage());
                }
                throw th;
            }
        }
        return str;
    }
}
