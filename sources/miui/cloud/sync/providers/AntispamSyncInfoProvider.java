package miui.cloud.sync.providers;

import android.content.Context;
import miui.cloud.sync.SyncInfoProviderBase;

/* loaded from: classes3.dex */
public class AntispamSyncInfoProvider extends SyncInfoProviderBase {
    public static final String AUTHORITY = "antispam";

    public AntispamSyncInfoProvider() {
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
