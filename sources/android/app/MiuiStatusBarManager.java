package android.app;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.util.Log;
import java.lang.reflect.Method;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes.dex */
public class MiuiStatusBarManager {
    private static final Method sSetStatusMethod;

    static {
        Method method;
        try {
            method = Class.forName("android.app.StatusBarManager").getMethod("setStatus", Integer.TYPE, String.class, Bundle.class);
        } catch (Exception e) {
            Log.e("MiuiStatusBarManager", e.toString());
            method = null;
        }
        sSetStatusMethod = method;
    }

    public static boolean applyState(Context context, MiuiStatusBarState miuiStatusBarState) {
        Method method = sSetStatusMethod;
        if (method == null) {
            return false;
        }
        try {
            method.invoke(context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_STATUSBAR), 1, "action_set_status_bar_state." + miuiStatusBarState.getTag(context), miuiStatusBarState.toBundle(context));
            return true;
        } catch (Exception e) {
            Log.e("MiuiStatusBarManager", "applyState exception:" + e.toString());
            return false;
        }
    }

    public static boolean clearState(Context context, String str) {
        Method method = sSetStatusMethod;
        if (method == null) {
            return false;
        }
        try {
            Object systemService = context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_STATUSBAR);
            Bundle bundle = new Bundle();
            bundle.putString("key_status_bar_tag", str);
            bundle.putString("key_status_bar_package_name", context.getPackageName());
            method.invoke(systemService, 0, "action_clear_status_bar_state", bundle);
            return true;
        } catch (Exception e) {
            Log.e("MiuiStatusBarManager", "clearState exception:" + e.toString());
            return false;
        }
    }

    public static boolean isCollapseAfterClicked(Context context) {
        return isCollapseAfterClickedForUser(context, UserHandle.myUserId());
    }

    public static boolean isCollapseAfterClickedForUser(Context context, int i) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "status_bar_collapse_after_clicked", false, i);
    }

    public static boolean isExpandableUnderKeyguard(Context context) {
        return isExpandableUnderKeyguardForUser(context, UserHandle.myUserId());
    }

    public static boolean isExpandableUnderKeyguardForUser(Context context, int i) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "status_bar_expandable_under_keyguard", true, i);
    }

    public static boolean isShowNetworkSpeed(Context context) {
        return isShowNetworkSpeedForUser(context, UserHandle.myUserId());
    }

    public static boolean isShowNetworkSpeedForUser(Context context, int i) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "status_bar_show_network_speed", false, i);
    }

    public static boolean isShowNotificationIcon(Context context) {
        return isShowNotificationIconForUser(context, UserHandle.myUserId());
    }

    public static boolean isShowNotificationIconForUser(Context context, int i) {
        return MiuiSettings.System.getBooleanForUser(context.getContentResolver(), "status_bar_show_notification_icon", true, i);
    }

    public static void setCollapseAfterClicked(Context context, boolean z) {
        setCollapseAfterClickedForUser(context, z, UserHandle.myUserId());
    }

    public static void setCollapseAfterClickedForUser(Context context, boolean z, int i) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_collapse_after_clicked", z, i);
    }

    public static void setExpandableUnderKeyguard(Context context, boolean z) {
        setExpandableUnderKeyguardForUser(context, z, UserHandle.myUserId());
    }

    public static void setExpandableUnderKeyguardForUser(Context context, boolean z, int i) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_expandable_under_keyguard", z, i);
    }

    public static void setShowCarrier(Context context, boolean z) {
        setShowCarrierForUser(context, z, UserHandle.myUserId());
    }

    public static void setShowCarrierForUser(Context context, boolean z, int i) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_show_custom_carrier", z, i);
    }

    public static void setShowNetworkSpeed(Context context, boolean z) {
        setShowNetworkSpeedForUser(context, z, UserHandle.myUserId());
    }

    public static void setShowNetworkSpeedForUser(Context context, boolean z, int i) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_show_network_speed", z, i);
    }

    public static void setShowNotificationIcon(Context context, boolean z) {
        setShowNotificationIconForUser(context, z, UserHandle.myUserId());
    }

    public static void setShowNotificationIconForUser(Context context, boolean z, int i) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "status_bar_show_notification_icon", z, i);
    }
}
