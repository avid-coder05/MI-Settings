package miui.telephony;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.content.res.ThemeResources;
import miui.os.SystemProperties;
import miui.reflect.Method;
import miui.util.AppConstants;

/* loaded from: classes4.dex */
public class PhoneDebug {
    public static final String PHONE_DEBUG_FLAG = "phone_debug_flag";
    public static boolean VDBG;
    private static List<Listener> sListeners;

    /* loaded from: classes4.dex */
    public interface Listener {
        void onDebugChanged();
    }

    static {
        try {
            Application currentApplication = AppConstants.getCurrentApplication();
            if (!ThemeResources.FRAMEWORK_PACKAGE.equals(getOpPackageName(currentApplication))) {
                register();
                return;
            }
            registerDelay(60000);
            VDBG = Settings.System.getInt(currentApplication.getContentResolver(), PHONE_DEBUG_FLAG, 0) == 1 || SystemProperties.getBoolean("debug.miui.phone", false);
        } catch (Exception e) {
            Rlog.w("PhoneDebug", "init" + e);
        }
    }

    private PhoneDebug() {
    }

    public static Listener addListener(Listener listener) {
        if (sListeners == null) {
            sListeners = new ArrayList(1);
        }
        if (listener != null && !sListeners.contains(listener)) {
            sListeners.add(listener);
            listener.onDebugChanged();
        }
        return listener;
    }

    private static String getOpPackageName(Context context) {
        return (String) Method.of(Context.class, "getOpPackageName", String.class, new Class[0]).invokeObject((Class) null, context, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void register() {
        try {
            final ContentResolver contentResolver = AppConstants.getCurrentApplication().getContentResolver();
            boolean z = true;
            if (Settings.System.getInt(contentResolver, PHONE_DEBUG_FLAG, 0) != 1 && !SystemProperties.getBoolean("debug.miui.phone", false)) {
                z = false;
            }
            VDBG = z;
            contentResolver.registerContentObserver(Settings.System.getUriFor(PHONE_DEBUG_FLAG), false, new ContentObserver(null) { // from class: miui.telephony.PhoneDebug.2
                @Override // android.database.ContentObserver
                public void onChange(boolean z2) {
                    boolean z3 = Settings.System.getInt(contentResolver, PhoneDebug.PHONE_DEBUG_FLAG, 0) == 1 || SystemProperties.getBoolean("debug.miui.phone", false);
                    PhoneDebug.VDBG = z3;
                    if (z3) {
                        Rlog.w("PhoneDebug", "onChange VDBG=" + PhoneDebug.VDBG);
                    }
                    if (PhoneDebug.sListeners != null) {
                        Iterator it = PhoneDebug.sListeners.iterator();
                        while (it.hasNext()) {
                            ((Listener) it.next()).onDebugChanged();
                        }
                    }
                }
            });
        } catch (Exception e) {
            Rlog.w("PhoneDebug", "register" + e);
        }
    }

    private static void registerDelay(final int i) {
        if (VDBG) {
            Rlog.w("PhoneDebug", "registerDelay");
        }
        new Thread(new Runnable() { // from class: miui.telephony.PhoneDebug.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Thread.sleep(i);
                    PhoneDebug.register();
                } catch (Exception e) {
                    Rlog.w("PhoneDebug", "registerDelay" + e);
                }
            }
        }).start();
    }

    public static void removeListener(Listener listener) {
        List<Listener> list = sListeners;
        if (list == null || listener == null) {
            return;
        }
        list.remove(listener);
        if (sListeners.isEmpty()) {
            sListeners = null;
        }
    }
}
