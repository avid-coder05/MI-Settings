package miui.cloud.finddevice;

import android.content.Context;

@Deprecated
/* loaded from: classes3.dex */
public class FindDeviceNotification {
    public static final String ACTION_NOTIFY_USER_CLOSE_FAILUER = "miui.cloud.finddevice.notification.CLOSE_FAILURE";
    public static final String ACTION_NOTIFY_USER_CLOSE_SUCCESS = "miui.cloud.finddevice.notification.CLOSE_SUCCESS";
    public static final String ACTION_NOTIFY_USER_CRASH = "miui.cloud.finddevice.notification.CRASH";
    public static final String ACTION_NOTIFY_USER_OPEN_FAILURE = "miui.cloud.finddevice.notification.OPEN_FAILURE";
    public static final String ACTION_NOTIFY_USER_OPEN_SUCCESS = "miui.cloud.finddevice.notification.OPEN_SUCCESS";
    public static final String ACTION_NOTIFY_USER_SERVER_CUSTOM_ERROR = "miui.cloud.finddevice.notification.SERVER_CUSTOM_ERROR";
    public static final String ACTION_NOTIFY_USER_STORAGE_CORRUPTED = "miui.cloud.finddevice.notification.STORAGE_CORRUPTED";
    public static final String ACTION_NOTIFY_USER_TELEPHONY_FAULT = "miui.cloud.finddevice.notification.TELEPHONY_FAULT";
    public static final String ACTION_NOTIFY_USER_TIME_CORRECTION_FAILURE = "miui.cloud.finddevice.notification.TIME_CORRECTION_FAILURE";
    public static final String ACTION_NOTIFY_USER_TIME_CORRECTION_SUCCESS = "miui.cloud.finddevice.notification.TIME_CORRECTION_SUCCESS";
    public static final String KEY_CAUSE = "cause";
    public static final String KEY_DETAIL = "detail";

    public FindDeviceNotification() {
        throw new RuntimeException("Stub!");
    }

    public static void notifyStorageCorrupted(Context context) {
        throw new RuntimeException("Stub!");
    }

    public static void notifyUserCloseFailure(Context context, String str) {
        throw new RuntimeException("Stub!");
    }

    public static void notifyUserCloseSuccess(Context context) {
        throw new RuntimeException("Stub!");
    }

    public static void notifyUserCrash(Context context) {
        throw new RuntimeException("Stub!");
    }

    public static void notifyUserOpenFailure(Context context, String str) {
        throw new RuntimeException("Stub!");
    }

    public static void notifyUserOpenSuccess(Context context) {
        throw new RuntimeException("Stub!");
    }

    public static void notifyUserServerCustomError(Context context, String str) {
        throw new RuntimeException("Stub!");
    }

    public static void notifyUserTelephonyFault(Context context, String str) {
        throw new RuntimeException("Stub!");
    }

    public static void notifyUserTimeCorrectionFailure(Context context, String str) {
        throw new RuntimeException("Stub!");
    }

    public static void notifyUserTimeCorrectionSuccess(Context context) {
        throw new RuntimeException("Stub!");
    }
}
