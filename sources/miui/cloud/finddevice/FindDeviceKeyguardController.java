package miui.cloud.finddevice;

import android.content.Context;
import android.os.Bundle;

@Deprecated
/* loaded from: classes3.dex */
public class FindDeviceKeyguardController {
    public static final String KEY_EXTRA_BACKOFF = "key_backoff";
    public static final String KEY_EXTRA_DISPLAY_ID = "key_display_id";
    public static final String KEY_EXTRA_EMAIL = "key_email";
    public static final String KEY_EXTRA_NOTIFY_CODE = "key_notify_code";
    public static final String KEY_EXTRA_NOTIFY_EXTRA = "key_notify_extra";
    public static final String KEY_EXTRA_PHONE = "key_phone";
    public static final String KEY_EXTRA_USERID = "key_user_id";
    public static final String LOCK_ACTION = "miui.cloud.finddevice.keyguard.LOCK";
    public static final String NOTIFY_ACTION = "miui.cloud.finddevice.keygurad.NOTIFY";
    public static final int NOTIFY_CODE_ACCOUNT_LOGIN_FINISHED = 1;
    public static final int NOTIFY_CODE_INVALID = 0;
    public static final int NOTIFY_CODE_UPDATE_MESSAGE = 2;
    public static final String NOTIFY_EXTRA_KEY_ACCOUNT_LOGIN_RESULT = "notify_extra_key_account_login_result";
    public static final String NOTIFY_EXTRA_KEY_UPDATE_MESSAGE_CONTENT = "notify_extra_key_update_message_content";
    public static final String SET_BACKOFF_ACTION = "miui.cloud.finddevice.keyguard.SET_BACKOFF";
    public static final String UNLOCK_ACTION = "miui.cloud.finddevice.keyguard.UNLOCK";

    public FindDeviceKeyguardController() {
        throw new RuntimeException("Stub!");
    }

    public static void lock(Context context, String str, String str2, String str3, String str4) {
        throw new RuntimeException("Stub!");
    }

    public static void notify(Context context, int i, Bundle bundle) {
        throw new RuntimeException("Stub!");
    }

    public static void setBackoff(Context context, boolean z) {
        throw new RuntimeException("Stub!");
    }

    public static void unlock(Context context) {
        throw new RuntimeException("Stub!");
    }
}
