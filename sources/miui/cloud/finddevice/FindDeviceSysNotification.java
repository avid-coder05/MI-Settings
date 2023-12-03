package miui.cloud.finddevice;

import android.content.Context;
import org.json.JSONException;

/* loaded from: classes3.dex */
public class FindDeviceSysNotification {
    public static final String ACTION_DISMISS = "action_dismiss";
    public static final String ACTION_SHOW = "action_show";
    public static final String KEY_NOTIFICATION = "key_notification";
    public static final String KEY_TYPE = "key_type";
    public static final int TYPE_ASYNC_OPEN_FAILED = 48;
    public static final int TYPE_GUIDE_COMMON = 16;
    public static final int TYPE_GUIDE_FINANCE = 32;
    public static final int TYPE_VERIFY = 64;

    /* loaded from: classes3.dex */
    public static class Notification {
        public final boolean cancelable;
        public final String content;
        public final String title;
        public final int type;

        public Notification(int i, String str, String str2, boolean z) {
            throw new RuntimeException("Stub!");
        }

        public static Notification fromJSON(String str) throws JSONException {
            throw new RuntimeException("Stub!");
        }

        public String toJSON() {
            throw new RuntimeException("Stub!");
        }
    }

    FindDeviceSysNotification() {
        throw new RuntimeException("Stub!");
    }

    public static void dismiss(Context context, int i) {
        throw new RuntimeException("Stub!");
    }

    public static void show(Context context, Notification notification) {
        throw new RuntimeException("Stub!");
    }
}
