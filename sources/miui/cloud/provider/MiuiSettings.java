package miui.cloud.provider;

import android.content.ContentResolver;
import android.content.Context;

/* loaded from: classes3.dex */
public class MiuiSettings {

    /* loaded from: classes3.dex */
    public static class Secure {
        Secure() {
            throw new RuntimeException("Stub!");
        }

        public static String get_SYNC_ON_WIFI_ONLY() {
            throw new RuntimeException("Stub!");
        }

        public static boolean isSecureSpace(ContentResolver contentResolver) {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static class System {
        System() {
            throw new RuntimeException("Stub!");
        }

        public static String get_MMS_PRIVATE_ADDRESS_MARKER() {
            throw new RuntimeException("Stub!");
        }

        public static String get_MMS_SYNC_WILD_MSG_STATE() {
            throw new RuntimeException("Stub!");
        }

        public static int get_MMS_SYNC_WILD_MSG_STATE_DOWNLOAD_PENDING() {
            throw new RuntimeException("Stub!");
        }

        public static int get_MMS_SYNC_WILD_MSG_STATE_INIT() {
            throw new RuntimeException("Stub!");
        }

        public static int get_MMS_SYNC_WILD_MSG_STATE_UPGRADE() {
            throw new RuntimeException("Stub!");
        }

        public static int get_MMS_SYNC_WILD_MSG_STATE_UPGRADE_SIM() {
            throw new RuntimeException("Stub!");
        }

        public static String get_MMS_THREAD_MARKER() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static class Telephony {
        Telephony() {
            throw new RuntimeException("Stub!");
        }

        public static boolean getEnabledVoiceService(ContentResolver contentResolver) {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static final class VirtualSim {
        public VirtualSim() {
            throw new RuntimeException("Stub!");
        }

        public static int getVirtualSimSlotId(Context context) {
            throw new RuntimeException("Stub!");
        }

        public static int getVirtualSimType(Context context) {
            throw new RuntimeException("Stub!");
        }

        public static int get_DC_ONLY_VIRTUAL_SIM() {
            throw new RuntimeException("Stub!");
        }

        public static boolean isVirtualSimEnabled(Context context) {
            throw new RuntimeException("Stub!");
        }
    }

    MiuiSettings() {
        throw new RuntimeException("Stub!");
    }
}
