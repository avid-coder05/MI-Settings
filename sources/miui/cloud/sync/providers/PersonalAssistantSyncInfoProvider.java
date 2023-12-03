package miui.cloud.sync.providers;

import android.content.Context;
import android.net.Uri;
import miui.cloud.sync.SyncInfoProviderBase;

/* loaded from: classes3.dex */
public class PersonalAssistantSyncInfoProvider extends SyncInfoProviderBase {
    public static final String AUTHORITY = "personal_assistant";
    public static final String AUTHORITY_FAV = "favorite";
    public static final String COLUMN_CDIRTY = "cdirty";
    public static final Uri CONTENT_URI_ASSISTANT = null;
    public static final Uri CONTENT_URI_FAV = null;
    public static final String RECORD_SYNCED = "0";
    public static final String RECORD_UNSYNCED = "1";
    public static final String TAG = "PersonalAssistantSyncInfoProvider";

    public PersonalAssistantSyncInfoProvider() {
        throw new RuntimeException("Stub!");
    }

    @Override // miui.cloud.sync.SyncInfoProviderBase, miui.cloud.sync.SyncInfoProvider
    public int getSyncedCount(Context context) {
        throw new RuntimeException("Stub!");
    }

    @Override // miui.cloud.sync.SyncInfoProvider
    public int getUnsyncedCount(Context context) {
        throw new RuntimeException("Stub!");
    }

    @Override // miui.cloud.sync.SyncInfoProvider
    public int getWifiOnlyUnsyncedCount(Context context) {
        throw new RuntimeException("Stub!");
    }
}
