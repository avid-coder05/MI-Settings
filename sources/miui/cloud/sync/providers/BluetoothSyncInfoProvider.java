package miui.cloud.sync.providers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import miui.cloud.sync.SyncInfoProviderBase;

/* loaded from: classes3.dex */
public class BluetoothSyncInfoProvider extends SyncInfoProviderBase {
    public static final String AUTHORITY = "com.android.bluetooth.ble.app.headsetdata.provider";
    private static final String TAG = "BluetoothSyncInfoProvider";
    private static final Uri URI_UNSYNCED = Uri.parse("content://com.android.bluetooth.ble.app.headsetdata.provider/unsynceddata");
    private static final Uri URI_SYNCED = Uri.parse("content://com.android.bluetooth.ble.app.headsetdata.provider/synceddata");

    private int getBluetoothCount(Context context, Uri uri, String str, String[] strArr) {
        Log.d(TAG, "getBluetoothCount, uri: " + uri);
        Cursor query = context.getContentResolver().query(uri, new String[]{"id"}, str, strArr, null);
        if (query == null) {
            Log.d(TAG, "queryDirtyCount: cursor is null");
            return 0;
        }
        try {
            int count = query.getCount();
            query.close();
            Log.d(TAG, "queryDirtyCount = " + count);
            return count;
        } catch (Throwable th) {
            query.close();
            throw th;
        }
    }

    @Override // miui.cloud.sync.SyncInfoProviderBase, miui.cloud.sync.SyncInfoProvider
    public int getSyncedCount(Context context) {
        return getBluetoothCount(context, URI_SYNCED, null, null);
    }

    @Override // miui.cloud.sync.SyncInfoProvider
    public int getUnsyncedCount(Context context) {
        return getBluetoothCount(context, URI_UNSYNCED, null, null);
    }

    @Override // miui.cloud.sync.SyncInfoProvider
    public int getWifiOnlyUnsyncedCount(Context context) {
        return 0;
    }
}
