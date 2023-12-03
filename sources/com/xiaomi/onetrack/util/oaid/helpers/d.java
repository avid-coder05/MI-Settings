package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.xiaomi.onetrack.util.p;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* loaded from: classes2.dex */
public class d {
    public final LinkedBlockingQueue<IBinder> a = new LinkedBlockingQueue<>(1);
    ServiceConnection b = new ServiceConnection() { // from class: com.xiaomi.onetrack.util.oaid.helpers.HWDeviceIDHelper$1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                d.this.a.offer(iBinder, 1L, TimeUnit.SECONDS);
            } catch (Exception e) {
                p.a("HWDeviceIDHelper", e.getMessage());
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    /* JADX WARN: Type inference failed for: r3v2, types: [com.xiaomi.onetrack.util.oaid.a.b$a] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:15:0x0046 -> B:32:0x006d). Please submit an issue!!! */
    public String a(Context context) {
        final IBinder poll;
        Intent intent = new Intent("com.uodis.opendevice.OPENIDS_SERVICE");
        intent.setPackage("com.huawei.hwid");
        String str = "";
        if (context.bindService(intent, this.b, 1)) {
            try {
                try {
                    try {
                        poll = this.a.poll(1L, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        p.a("HWDeviceIDHelper", e.getMessage());
                        context.unbindService(this.b);
                    }
                } catch (Exception e2) {
                    p.a("HWDeviceIDHelper", e2.getMessage());
                }
                if (poll == null) {
                    try {
                        context.unbindService(this.b);
                    } catch (Exception e3) {
                        p.a("HWDeviceIDHelper", e3.getMessage());
                    }
                    return "";
                }
                str = new IInterface(poll) { // from class: com.xiaomi.onetrack.util.oaid.a.b$a
                    private IBinder a;

                    {
                        this.a = poll;
                    }

                    public String a() {
                        String str2;
                        Parcel obtain = Parcel.obtain();
                        Parcel obtain2 = Parcel.obtain();
                        try {
                            obtain.writeInterfaceToken("com.uodis.opendevice.aidl.OpenDeviceIdentifierService");
                            this.a.transact(1, obtain, obtain2, 0);
                            obtain2.readException();
                            str2 = obtain2.readString();
                        } catch (Throwable th) {
                            obtain.recycle();
                            obtain2.recycle();
                            th.printStackTrace();
                            str2 = null;
                        }
                        obtain.recycle();
                        obtain2.recycle();
                        return str2;
                    }

                    @Override // android.os.IInterface
                    public IBinder asBinder() {
                        return this.a;
                    }
                }.a();
                context.unbindService(this.b);
            } catch (Throwable th) {
                try {
                    context.unbindService(this.b);
                } catch (Exception e4) {
                    p.a("HWDeviceIDHelper", e4.getMessage());
                }
                throw th;
            }
        }
        return str;
    }
}
