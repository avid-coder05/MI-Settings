package com.xiaomi.onetrack.f;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.e.a;
import com.xiaomi.onetrack.util.p;

/* loaded from: classes2.dex */
public class c {
    public static OneTrack.NetType a(Context context) {
        NetworkInfo activeNetworkInfo;
        try {
            activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        } catch (Exception e) {
            p.b("NetworkUtil", "getNetworkState error", e);
        }
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == 1) {
                return OneTrack.NetType.WIFI;
            }
            if (activeNetworkInfo.getType() != 0) {
                if (activeNetworkInfo.getType() == 9) {
                    return OneTrack.NetType.ETHERNET;
                }
                return OneTrack.NetType.UNKNOWN;
            }
            switch (activeNetworkInfo.getSubtype()) {
                case 1:
                case 2:
                case 4:
                case 7:
                case 11:
                case 16:
                    return OneTrack.NetType.MOBILE_2G;
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 12:
                case 14:
                case 15:
                case 17:
                    return OneTrack.NetType.MOBILE_3G;
                case 13:
                case 18:
                case 19:
                    return OneTrack.NetType.MOBILE_4G;
                case 20:
                    return OneTrack.NetType.MOBILE_5G;
                default:
                    return OneTrack.NetType.UNKNOWN;
            }
        }
        return OneTrack.NetType.NOT_CONNECTED;
    }

    @SuppressLint({"MissingPermission"})
    public static boolean a() {
        Context b = a.b();
        if (b != null) {
            try {
                NetworkInfo activeNetworkInfo = ((ConnectivityManager) b.getSystemService("connectivity")).getActiveNetworkInfo();
                if (activeNetworkInfo != null) {
                    return activeNetworkInfo.isConnectedOrConnecting();
                }
                return false;
            } catch (Exception unused) {
                p.a("NetworkUtil", "isNetworkConnected exception");
                return false;
            }
        }
        return false;
    }
}
