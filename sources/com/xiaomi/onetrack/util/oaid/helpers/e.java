package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* loaded from: classes2.dex */
public class e {
    public final LinkedBlockingQueue<IBinder> a = new LinkedBlockingQueue<>(1);
    ServiceConnection b = new ServiceConnection() { // from class: com.xiaomi.onetrack.util.oaid.helpers.LenovoDeviceIDHelper$1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                e.this.a.offer(iBinder, 1L, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    public String a(Context context) {
        final IBinder poll;
        context.getPackageName();
        Intent intent = new Intent();
        intent.setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService");
        String str = "";
        if (context.bindService(intent, this.b, 1)) {
            try {
                try {
                    poll = this.a.poll(1L, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (poll == null) {
                    try {
                        context.unbindService(this.b);
                    } catch (Exception unused) {
                    }
                    return "";
                }
                str = new com.xiaomi.onetrack.util.oaid.a.c(poll) { // from class: com.xiaomi.onetrack.util.oaid.a.c$a$a
                    private IBinder a;

                    {
                        this.a = poll;
                    }

                    @Override // com.xiaomi.onetrack.util.oaid.a.c
                    public String a() {
                        Parcel obtain = Parcel.obtain();
                        Parcel obtain2 = Parcel.obtain();
                        try {
                            try {
                                obtain.writeInterfaceToken("com.zui.deviceidservice.IDeviceidInterface");
                                this.a.transact(1, obtain, obtain2, 0);
                                obtain2.readException();
                                return obtain2.readString();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                obtain2.recycle();
                                obtain.recycle();
                                return null;
                            }
                        } finally {
                            obtain2.recycle();
                            obtain.recycle();
                        }
                    }

                    @Override // android.os.IInterface
                    public IBinder asBinder() {
                        return null;
                    }
                }.a();
                try {
                    context.unbindService(this.b);
                } catch (Exception unused2) {
                }
            } catch (Throwable th) {
                try {
                    context.unbindService(this.b);
                } catch (Exception unused3) {
                }
                throw th;
            }
        }
        return str;
    }
}
