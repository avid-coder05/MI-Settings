package com.xiaomi.onetrack.util;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.SystemSettings$System;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.MiuiWindowManager$LayoutParams;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class DeviceUtil {
    private static Method j;
    private static Method k;
    private static Method l;
    private static Object m;
    private static Method n;
    private static Method o;
    private static String p;
    private static String q;
    private static String v;
    private static String w;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GAIDClient {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static final class AdvertisingConnection implements ServiceConnection {
            private boolean b;
            private IBinder c;

            private AdvertisingConnection() {
                this.b = false;
            }

            /* synthetic */ AdvertisingConnection(h hVar) {
                this();
            }

            public IBinder a() throws InterruptedException {
                IBinder iBinder = this.c;
                if (iBinder != null) {
                    return iBinder;
                }
                if (iBinder == null && !this.b) {
                    synchronized (this) {
                        wait(30000L);
                        if (this.c == null) {
                            throw new InterruptedException("Not connect or connect timeout to google play service");
                        }
                    }
                }
                return this.c;
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                synchronized (this) {
                    this.c = iBinder;
                    notifyAll();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                this.b = true;
                this.c = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static final class a implements IInterface {
            private IBinder a;

            public a(IBinder iBinder) {
                this.a = iBinder;
            }

            public String a() throws RemoteException {
                if (this.a == null) {
                    return "";
                }
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                    this.a.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean a(boolean z) throws RemoteException {
                if (this.a == null) {
                    return false;
                }
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                    obtain.writeInt(z ? 1 : 0);
                    this.a.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.a;
            }
        }

        static String a(Context context) {
            if (!c(context)) {
                p.a("GAIDClient", "Google play service is not available");
                return "";
            }
            AdvertisingConnection advertisingConnection = new AdvertisingConnection(null);
            try {
                try {
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    if (context.bindService(intent, advertisingConnection, 1)) {
                        return new a(advertisingConnection.a()).a();
                    }
                } catch (Exception e) {
                    p.a("GAIDClient", "Query Google ADID failed ", e);
                }
                return "";
            } finally {
                context.unbindService(advertisingConnection);
            }
        }

        static boolean b(Context context) {
            if (!c(context)) {
                p.a("GAIDClient", "Google play service is not available");
                return false;
            }
            AdvertisingConnection advertisingConnection = new AdvertisingConnection(null);
            try {
                try {
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    if (context.bindService(intent, advertisingConnection, 1)) {
                        return new a(advertisingConnection.a()).a(true);
                    }
                } catch (Exception e) {
                    p.a("GAIDClient", "Query Google isLimitAdTrackingEnabled failed ", e);
                }
                return false;
            } finally {
                context.unbindService(advertisingConnection);
            }
        }

        private static boolean c(Context context) {
            try {
                context.getPackageManager().getPackageInfo("com.android.vending", MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION);
                return true;
            } catch (PackageManager.NameNotFoundException unused) {
                return false;
            }
        }
    }

    static {
        try {
            j = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
        } catch (Throwable th) {
            p.b("DeviceUtil", "sGetProp init failed ex: " + th.getMessage());
        }
        try {
            Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
            m = cls.getMethod("getDefault", new Class[0]).invoke(null, new Object[0]);
            k = cls.getMethod("getImeiList", new Class[0]);
            l = cls.getMethod("getMeidList", new Class[0]);
            o = cls.getMethod("getSubscriberIdForSlot", Integer.TYPE);
        } catch (Throwable th2) {
            p.b("DeviceUtil", "TelephonyManagerEx init failed ex: " + th2.getMessage());
        }
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                n = Class.forName("android.telephony.TelephonyManager").getMethod("getImei", Integer.TYPE);
            }
        } catch (Throwable th3) {
            p.b("DeviceUtil", "sGetImeiForSlot init failed ex: " + th3.getMessage());
        }
    }

    private static List<String> B(Context context) {
        if (n != null) {
            try {
                ArrayList arrayList = new ArrayList();
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                String str = (String) n.invoke(telephonyManager, 0);
                if (c(str)) {
                    arrayList.add(str);
                }
                if (n()) {
                    String str2 = (String) n.invoke(telephonyManager, 1);
                    if (c(str2)) {
                        arrayList.add(str2);
                    }
                }
                return arrayList;
            } catch (Exception e) {
                p.a("DeviceUtil", "getImeiListAboveLollipop failed ex: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    @SuppressLint({"MissingPermission"})
    private static List<String> C(Context context) {
        try {
            ArrayList arrayList = new ArrayList();
            Class<?> cls = Class.forName("android.telephony.TelephonyManager");
            if (!n()) {
                String deviceId = ((TelephonyManager) cls.getMethod("getDefault", new Class[0]).invoke(null, new Object[0])).getDeviceId();
                if (c(deviceId)) {
                    arrayList.add(deviceId);
                }
                return arrayList;
            }
            Class<?> cls2 = Integer.TYPE;
            String deviceId2 = ((TelephonyManager) cls.getMethod("getDefault", cls2).invoke(null, 0)).getDeviceId();
            String deviceId3 = ((TelephonyManager) cls.getMethod("getDefault", cls2).invoke(null, 1)).getDeviceId();
            if (c(deviceId2)) {
                arrayList.add(deviceId2);
            }
            if (c(deviceId3)) {
                arrayList.add(deviceId3);
            }
            return arrayList;
        } catch (Throwable th) {
            p.a("DeviceUtil", "getImeiListBelowLollipop failed ex: " + th.getMessage());
            return null;
        }
    }

    public static String a(Context context) {
        if (TextUtils.isEmpty(p)) {
            r(context);
            return !TextUtils.isEmpty(p) ? p : "";
        }
        return p;
    }

    public static void a() {
        v = null;
    }

    private static boolean a(List<String> list) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (!c(it.next())) {
                return true;
            }
        }
        return false;
    }

    private static String[] a(Class<?> cls, Class<?> cls2, TelephonyManager telephonyManager, SubscriptionManager subscriptionManager) {
        Method method;
        Object obj;
        String[] strArr = new String[2];
        try {
            int i = Build.VERSION.SDK_INT;
            if (i == 21) {
                strArr[0] = (String) cls.getMethod("getSubscriberId", Long.TYPE).invoke(telephonyManager, Long.valueOf(((long[]) cls2.getMethod("getSubId", Integer.TYPE).invoke(subscriptionManager, 0))[0]));
            } else {
                Class<?> cls3 = Integer.TYPE;
                strArr[0] = (String) cls.getMethod("getSubscriberId", cls3).invoke(telephonyManager, Integer.valueOf(((int[]) cls2.getMethod("getSubId", cls3).invoke(subscriptionManager, 0))[0]));
            }
            if (!e(strArr[0]) && (method = o) != null && (obj = m) != null) {
                strArr[0] = (String) method.invoke(obj, 0);
                strArr[1] = (String) o.invoke(m, 1);
            } else if (i == 21) {
                strArr[1] = (String) cls.getMethod("getSubscriberId", Long.TYPE).invoke(telephonyManager, Long.valueOf(((long[]) cls2.getMethod("getSubId", Integer.TYPE).invoke(subscriptionManager, 1))[0]));
            } else {
                Class<?> cls4 = Integer.TYPE;
                strArr[1] = (String) cls.getMethod("getSubscriberId", cls4).invoke(telephonyManager, Integer.valueOf(((int[]) cls2.getMethod("getSubId", cls4).invoke(subscriptionManager, 1))[0]));
            }
        } catch (Exception e) {
            p.a("DeviceUtil", "getImsiFromLToP: " + e);
        }
        return strArr;
    }

    public static String b(Context context) {
        if (TextUtils.isEmpty(w)) {
            String a = a(context);
            if (TextUtils.isEmpty(a)) {
                return "";
            }
            String c = com.xiaomi.onetrack.c.d.c(a);
            w = c;
            return c;
        }
        return w;
    }

    private static String b(String str) {
        try {
            Method method = j;
            if (method != null) {
                return String.valueOf(method.invoke(null, str));
            }
        } catch (Exception e) {
            p.a("DeviceUtil", "getProp failed ex: " + e.getMessage());
        }
        return null;
    }

    private static String[] b(Class<?> cls, Class<?> cls2, TelephonyManager telephonyManager, SubscriptionManager subscriptionManager) {
        String[] strArr = new String[2];
        try {
            Class<?> cls3 = Integer.TYPE;
            int[] iArr = (int[]) cls2.getMethod("getSubscriptionIds", cls3).invoke(subscriptionManager, 0);
            if (iArr != null) {
                strArr[0] = (String) cls.getMethod("getSubscriberId", cls3).invoke(telephonyManager, Integer.valueOf(iArr[0]));
            }
        } catch (Exception e) {
            p.b("DeviceUtil", "get imsi1 above Android Q exception:" + e);
        }
        try {
            Class<?> cls4 = Integer.TYPE;
            int[] iArr2 = (int[]) cls2.getMethod("getSubscriptionIds", cls4).invoke(subscriptionManager, 1);
            if (iArr2 != null) {
                strArr[1] = (String) cls.getMethod("getSubscriberId", cls4).invoke(telephonyManager, Integer.valueOf(iArr2[0]));
            }
        } catch (Exception e2) {
            p.b("DeviceUtil", "get imsi2 above Android Q exception:" + e2);
        }
        return strArr;
    }

    public static String c() {
        return Build.MODEL;
    }

    private static boolean c(String str) {
        return (str == null || str.length() != 15 || str.matches("^0*$")) ? false : true;
    }

    public static String d() {
        return b(SystemSettings$System.RO_MARKET_NAME);
    }

    public static String e() {
        return Build.MANUFACTURER;
    }

    private static boolean e(String str) {
        return str != null && str.length() >= 6 && str.length() <= 15 && !str.matches("^0*$");
    }

    private static List<String> m() {
        if (k == null || o()) {
            return null;
        }
        try {
            List<String> list = (List) k.invoke(m, new Object[0]);
            if (list == null || list.size() <= 0) {
                return null;
            }
            if (a(list)) {
                return null;
            }
            return list;
        } catch (Exception e) {
            p.a("DeviceUtil", "getImeiListFromMiui failed ex: " + e.getMessage());
            return null;
        }
    }

    private static boolean n() {
        if ("dsds".equals(b("persist.radio.multisim.config"))) {
            return true;
        }
        String str = Build.DEVICE;
        return "lcsh92_wet_jb9".equals(str) || "lcsh92_wet_tdd".equals(str) || "HM2013022".equals(str) || "HM2013023".equals(str) || "armani".equals(str) || "HM2014011".equals(str) || "HM2014012".equals(str);
    }

    private static boolean o() {
        if (Build.VERSION.SDK_INT >= 21) {
            return false;
        }
        String str = Build.DEVICE;
        String b = b("persist.radio.modem");
        if ("HM2014812".equals(str) || "HM2014821".equals(str)) {
            return true;
        }
        return ("gucci".equals(str) && "ct".equals(b("persist.sys.modem"))) || "CDMA".equals(b) || "HM1AC".equals(b) || "LTE-X5-ALL".equals(b) || "LTE-CT".equals(b) || "MI 3C".equals(Build.MODEL);
    }

    public static String p(Context context) {
        if (TextUtils.isEmpty(v)) {
            if (GAIDClient.b(context)) {
                return "";
            }
            String a = GAIDClient.a(context);
            if (TextUtils.isEmpty(a)) {
                return "";
            }
            v = a;
            return a;
        }
        return v;
    }

    public static List<String> q(Context context) {
        List<String> r = r(context);
        ArrayList arrayList = new ArrayList();
        if (r != null && !r.isEmpty()) {
            for (int i = 0; i < r.size(); i++) {
                if (!TextUtils.isEmpty(r.get(i))) {
                    arrayList.add(i, com.xiaomi.onetrack.c.d.c(r.get(i)));
                }
            }
        }
        return arrayList;
    }

    @SuppressLint({"MissingPermission"})
    public static List<String> r(Context context) {
        List<String> list;
        if (u.a(context)) {
            list = m();
            if (list == null || list.isEmpty()) {
                list = Build.VERSION.SDK_INT >= 21 ? B(context) : C(context);
            }
        } else {
            list = null;
        }
        if (list != null && !list.isEmpty()) {
            Collections.sort(list);
            p = list.get(0);
            if (list.size() >= 2) {
                q = list.get(1);
            }
        }
        return list;
    }

    @SuppressLint({"MissingPermission"})
    public static List<String> t(Context context) {
        String subscriberId;
        String subscriberId2;
        if (u.b(context)) {
            ArrayList arrayList = new ArrayList();
            try {
                if (!n()) {
                    String subscriberId3 = ((TelephonyManager) context.getSystemService("phone")).getSubscriberId();
                    if (e(subscriberId3)) {
                        arrayList.add(subscriberId3);
                    }
                    return arrayList;
                }
                Class<?> cls = Class.forName("android.telephony.TelephonyManager");
                int i = Build.VERSION.SDK_INT;
                if (i >= 22) {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                    SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service");
                    Class<?> cls2 = Class.forName("android.telephony.SubscriptionManager");
                    if (i < 29) {
                        subscriberId = a(cls, cls2, telephonyManager, subscriptionManager)[0];
                        subscriberId2 = a(cls, cls2, telephonyManager, subscriptionManager)[1];
                    } else {
                        subscriberId = b(cls, cls2, telephonyManager, subscriptionManager)[0];
                        subscriberId2 = b(cls, cls2, telephonyManager, subscriptionManager)[1];
                    }
                } else {
                    Class<?> cls3 = Integer.TYPE;
                    subscriberId = ((TelephonyManager) cls.getMethod("getDefault", cls3).invoke(null, 0)).getSubscriberId();
                    subscriberId2 = ((TelephonyManager) cls.getMethod("getDefault", cls3).invoke(null, 1)).getSubscriberId();
                }
                if (!e(subscriberId)) {
                    subscriberId = "";
                }
                arrayList.add(subscriberId);
                if (!e(subscriberId2)) {
                    subscriberId2 = "";
                }
                arrayList.add(subscriberId2);
                return arrayList;
            } catch (SecurityException unused) {
                p.a("DeviceUtil", "getImsiList failed with on permission");
            } catch (Throwable th) {
                p.b("DeviceUtil", "getImsiList failed: " + th.getMessage());
            }
        }
        return null;
    }

    public static String v(Context context) {
        try {
            List<String> t = t(context);
            if (t != null) {
                for (int i = 0; i < t.size(); i++) {
                    t.set(i, com.xiaomi.onetrack.c.d.h(t.get(i)));
                }
                return t.toString();
            }
            return "";
        } catch (Throwable th) {
            p.b(p.a("DeviceUtil"), "getImeiListMd5 failed!", th);
            return "";
        }
    }
}
