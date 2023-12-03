package miui.cloud.sync;

import android.content.Context;

/* loaded from: classes3.dex */
public interface SyncInfoProvider {
    int getSyncedCount(Context context);

    int getUnSyncedSecretCount(Context context);

    int getUnsyncedCount(Context context);

    int getWifiOnlyUnsyncedCount(Context context);
}
