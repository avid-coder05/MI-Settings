package miui.cloud.util;

import android.accounts.Account;
import android.content.Context;
import android.net.Uri;
import java.util.HashMap;
import miui.cloud.sync.data.SyncSettingState;

/* loaded from: classes3.dex */
public class SyncStateChangedHelper {
    public static final Uri INTERNAL_STAT_URI = null;
    public static final Uri OPEN_SWITCH_STATE_STAT_URI = null;
    public static final Uri OPEN_SYNC_PHONE_STATE = null;
    public static final Uri OPEN_SYNC_RESULT_URI = null;
    public static final Uri OPEN_SYNC_TIME_CONSUME = null;
    public static final String PATH_INTERNAL = "internal";
    public static final String PATH_OPEN_SWITCH_STATE = "open_switch_state";
    public static final String PATH_OPEN_SYNC_PHONE_STATE = "open_sync_phone_state";
    public static final String PATH_OPEN_SYNC_RESULT = "open_sync_result";
    public static final String PATH_OPEN_SYNC_TIME_CONSUME = "open_sync_time_consume";
    public static final String SYNC_AUTHORITY = "authority";
    public static final String SYNC_CHANGE_SOURCE = "change_source";
    public static final String SYNC_PROVIDER_AUTHORITY = "com.miui.cloudservice.SyncSettingStatusProvider";
    public static final String SYNC_SETTING_STATUS_PROVIDER = "com.miui.cloudservice.SyncSettingStatusProvider";
    public static final String SYNC_STATUS = "status";

    public SyncStateChangedHelper() {
        throw new RuntimeException("Stub!");
    }

    public static void clearAllSyncChangedLog(Context context) {
        throw new RuntimeException("Stub!");
    }

    public static HashMap<String, SyncSettingState> getCurrentSyncSettingState(Context context) {
        throw new RuntimeException("Stub!");
    }

    public static void setMiCloudSync(Context context, Account account, String str, String str2, boolean z) {
        throw new RuntimeException("Stub!");
    }

    public static void setSyncChanged(Context context, String str, String str2, boolean z) {
        throw new RuntimeException("Stub!");
    }
}
