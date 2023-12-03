package miui.cloud.sync;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import miui.cloud.sync.providers.AntispamSyncInfoProvider;
import miui.cloud.sync.providers.BluetoothSyncInfoProvider;
import miui.cloud.sync.providers.BrowserSyncInfoProvider;
import miui.cloud.sync.providers.CalendarSyncInfoProvider;
import miui.cloud.sync.providers.CalllogSyncInfoProvider;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;
import miui.cloud.sync.providers.GallerySyncInfoProvider;
import miui.cloud.sync.providers.GlobalBrowserSyncInfoProvider;
import miui.cloud.sync.providers.NotesSyncInfoProvider;
import miui.cloud.sync.providers.PersonalAssistantSyncInfoProvider;
import miui.cloud.sync.providers.PhraseSyncInfoProvider;
import miui.cloud.sync.providers.QuickSearchBoxProvider;
import miui.cloud.sync.providers.SmsSyncInfoProvider;
import miui.cloud.sync.providers.SoundRecorderSyncInfoProvider;
import miui.cloud.sync.providers.WifiSyncInfoProvider;

/* loaded from: classes3.dex */
public final class SyncInfoHelper {
    public static final int INVALID_COUNT = -1;
    public static final String TAG = "SyncInfoHelper";
    private static final Map<String, SyncInfoProvider> authorityMap;

    static {
        HashMap hashMap = new HashMap();
        authorityMap = hashMap;
        hashMap.put(ContactsSyncInfoProvider.AUTHORITY, new ContactsSyncInfoProvider());
        hashMap.put("sms", new SmsSyncInfoProvider());
        hashMap.put(GallerySyncInfoProvider.AUTHORITY, new GallerySyncInfoProvider());
        hashMap.put(CalllogSyncInfoProvider.AUTHORITY, new CalllogSyncInfoProvider());
        hashMap.put("notes", new NotesSyncInfoProvider());
        hashMap.put("wifi", new WifiSyncInfoProvider());
        hashMap.put("records", new SoundRecorderSyncInfoProvider());
        hashMap.put(BrowserSyncInfoProvider.AUTHORITY, new BrowserSyncInfoProvider());
        hashMap.put(GlobalBrowserSyncInfoProvider.AUTHORITY, new GlobalBrowserSyncInfoProvider());
        hashMap.put("antispam", new AntispamSyncInfoProvider());
        hashMap.put(CalendarSyncInfoProvider.AUTHORITY, new CalendarSyncInfoProvider());
        hashMap.put(PersonalAssistantSyncInfoProvider.AUTHORITY, new PersonalAssistantSyncInfoProvider());
        hashMap.put(QuickSearchBoxProvider.AUTHORITY, new QuickSearchBoxProvider());
        hashMap.put(PhraseSyncInfoProvider.AUTHORITY, new PhraseSyncInfoProvider());
        hashMap.put(BluetoothSyncInfoProvider.AUTHORITY, new BluetoothSyncInfoProvider());
    }

    public static int getSyncedDataCount(Context context, String str) throws SyncInfoUnavailableException {
        SyncInfoProvider syncInfoProvider = authorityMap.get(str);
        if (syncInfoProvider != null) {
            return syncInfoProvider.getSyncedCount(context);
        }
        throw new SyncInfoUnavailableException("getSyncedDataCount not implemented on authority: " + str);
    }

    public static int getUnSyncedSecretDataCount(Context context, String str) throws SyncInfoUnavailableException {
        SyncInfoProvider syncInfoProvider = authorityMap.get(str);
        if (syncInfoProvider != null) {
            return syncInfoProvider.getUnSyncedSecretCount(context);
        }
        throw new SyncInfoUnavailableException("getUnsyncedSecretDataCount not implemented on authority: " + str);
    }

    public static int getUnsyncedDataCount(Context context, String str) throws SyncInfoUnavailableException {
        SyncInfoProvider syncInfoProvider = authorityMap.get(str);
        if (syncInfoProvider != null) {
            return syncInfoProvider.getUnsyncedCount(context);
        }
        throw new SyncInfoUnavailableException("getUnsyncedDataCount not implemented on authority: " + str);
    }

    public static int getWifiOnlyUnsyncedDataCount(Context context, String str) throws SyncInfoUnavailableException {
        SyncInfoProvider syncInfoProvider = authorityMap.get(str);
        if (syncInfoProvider != null) {
            return syncInfoProvider.getWifiOnlyUnsyncedCount(context);
        }
        throw new SyncInfoUnavailableException("getWifiOnlyUnsyncedDataCount not implemented on authority: " + str);
    }
}
