package miui.cloud.sync.providers;

import android.content.Context;
import android.net.Uri;
import miui.cloud.sync.SyncInfoProviderBase;

/* loaded from: classes3.dex */
public final class ContactsSyncInfoProvider extends SyncInfoProviderBase {
    public static final String AUTHORITY = "com.android.contacts";

    public ContactsSyncInfoProvider() {
        throw new RuntimeException("Stub!");
    }

    public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
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
