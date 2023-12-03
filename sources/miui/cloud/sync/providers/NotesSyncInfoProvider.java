package miui.cloud.sync.providers;

import android.content.Context;
import miui.cloud.sync.SyncInfoProviderBase;

/* loaded from: classes3.dex */
public final class NotesSyncInfoProvider extends SyncInfoProviderBase {
    public static final String AUTHORITY = "notes";

    public NotesSyncInfoProvider() {
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
