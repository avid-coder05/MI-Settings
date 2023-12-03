package com.android.settings.security;

import android.content.Context;
import android.util.Log;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public class VirtualSimUtils {
    private static Class invokeVirtualSimUtils() {
        try {
            return Class.forName("miui.telephony.VirtualSimUtils");
        } catch (ClassNotFoundException e) {
            Log.w("VirtualSimUtils", "Are we running on a pad?", e);
            return null;
        }
    }

    public static boolean isDcOnlyVirtualSim(Context context) {
        Class invokeVirtualSimUtils = invokeVirtualSimUtils();
        if (invokeVirtualSimUtils != null) {
            try {
                Method declaredMethod = invokeVirtualSimUtils.getDeclaredMethod("isDcOnlyVirtualSim", Context.class);
                if (declaredMethod == null) {
                    return false;
                }
                return ((Boolean) declaredMethod.invoke(null, context)).booleanValue();
            } catch (Exception e) {
                Log.e("VirtualSimUtils", "Method Reflect Error: ", e);
            }
        }
        return false;
    }

    public static boolean isMiSimEnabled(Context context, int i) {
        Class invokeVirtualSimUtils = invokeVirtualSimUtils();
        if (invokeVirtualSimUtils != null) {
            try {
                Method declaredMethod = invokeVirtualSimUtils.getDeclaredMethod("isMiSimEnabled", Context.class, Integer.TYPE);
                if (declaredMethod == null) {
                    return false;
                }
                return ((Boolean) declaredMethod.invoke(null, context, Integer.valueOf(i))).booleanValue();
            } catch (Exception e) {
                Log.e("VirtualSimUtils", "Method Reflect Error: ", e);
            }
        }
        return false;
    }

    public static boolean isValidApnForMiSim(Context context, int i, String str) {
        Class invokeVirtualSimUtils = invokeVirtualSimUtils();
        if (invokeVirtualSimUtils != null) {
            try {
                Method declaredMethod = invokeVirtualSimUtils.getDeclaredMethod("isValidApnForMiSim", Context.class, Integer.TYPE, String.class);
                if (declaredMethod == null) {
                    return false;
                }
                return ((Boolean) declaredMethod.invoke(null, context, Integer.valueOf(i), str)).booleanValue();
            } catch (Exception e) {
                Log.e("VirtualSimUtils", "Method Reflect Error: ", e);
            }
        }
        return false;
    }

    public static boolean isVirtualSim(Context context, int i) {
        Class invokeVirtualSimUtils = invokeVirtualSimUtils();
        if (invokeVirtualSimUtils != null) {
            try {
                Method declaredMethod = invokeVirtualSimUtils.getDeclaredMethod("isVirtualSim", Context.class, Integer.TYPE);
                if (declaredMethod == null) {
                    return false;
                }
                return ((Boolean) declaredMethod.invoke(null, context, Integer.valueOf(i))).booleanValue();
            } catch (Exception e) {
                Log.e("VirtualSimUtils", "Method Reflect Error: ", e);
            }
        }
        return false;
    }
}
