package com.xiaomi.onetrack;

import android.content.Context;
import com.xiaomi.onetrack.api.g;
import com.xiaomi.onetrack.b.h;
import com.xiaomi.onetrack.e.a;
import com.xiaomi.onetrack.util.i;
import com.xiaomi.onetrack.util.p;
import java.util.Map;

/* loaded from: classes2.dex */
public class OneTrack {
    private static boolean a;
    private static boolean b;
    private g c;

    /* loaded from: classes2.dex */
    public interface ICommonPropertyProvider {
        Map<String, Object> getDynamicProperty(String str);
    }

    /* loaded from: classes2.dex */
    public interface IEventHook {
        boolean isCustomDauEvent(String str);

        boolean isRecommendEvent(String str);
    }

    /* loaded from: classes2.dex */
    public enum Mode {
        APP("app"),
        PLUGIN("plugin"),
        SDK("sdk");

        private String a;

        Mode(String str) {
            this.a = str;
        }

        public String getType() {
            return this.a;
        }
    }

    /* loaded from: classes2.dex */
    public enum NetType {
        NOT_CONNECTED("NONE"),
        MOBILE_2G("2G"),
        MOBILE_3G("3G"),
        MOBILE_4G("4G"),
        MOBILE_5G("5G"),
        WIFI("WIFI"),
        ETHERNET("ETHERNET"),
        UNKNOWN("UNKNOWN");

        private String a;

        NetType(String str) {
            this.a = str;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.a;
        }
    }

    private OneTrack(Context context, Configuration configuration) {
        a.a(context.getApplicationContext());
        this.c = new g(context, configuration);
        setEventHook(new DefaultEventHook());
    }

    private static void a(Context context) {
        if (context == null) {
            throw new IllegalStateException("context is null!");
        }
        a.a(context.getApplicationContext());
    }

    public static OneTrack createInstance(Context context, Configuration configuration) {
        return new OneTrack(context, configuration);
    }

    public static boolean isDisable() {
        return a;
    }

    public static boolean isUseSystemNetTrafficOnly() {
        return b;
    }

    public static void setAccessNetworkEnable(Context context, final boolean z) {
        a(context);
        i.a(new Runnable() { // from class: com.xiaomi.onetrack.OneTrack.1
            @Override // java.lang.Runnable
            public void run() {
                h.a(z);
                h.b(z);
            }
        });
    }

    public static void setDebugMode(boolean z) {
        p.a(z);
    }

    public static void setUseSystemNetTrafficOnly() {
        b = true;
    }

    public void setEventHook(IEventHook iEventHook) {
        this.c.a(iEventHook);
    }

    public void track(String str, Map<String, Object> map) {
        this.c.a(str, map);
    }
}
