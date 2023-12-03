package miui.cloud.sync;

import android.content.Context;
import android.net.Uri;

/* loaded from: classes3.dex */
public abstract class SyncInfoProviderBase implements SyncInfoProvider {
    protected static final boolean DEBUG = false;
    public static final int INVALID_COUNT = -1;

    public SyncInfoProviderBase() {
        throw new RuntimeException("Stub!");
    }

    public static boolean hasTelephonyFeature(Context context) {
        throw new RuntimeException("Stub!");
    }

    @Override // miui.cloud.sync.SyncInfoProvider
    public int getSyncedCount(Context context) {
        throw new RuntimeException("Stub!");
    }

    @Override // miui.cloud.sync.SyncInfoProvider
    public int getUnSyncedSecretCount(Context context) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isDebug() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int queryCount(Context context, Uri uri, String str, String[] strArr) {
        throw new RuntimeException("Stub!");
    }

    protected int queryCountByProjection(Context context, Uri uri, String str, String str2, String[] strArr) {
        throw new RuntimeException("Stub!");
    }
}
