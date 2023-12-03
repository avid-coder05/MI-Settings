package miui.cloud.external;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Log;
import java.util.Iterator;
import java.util.Map;
import miui.accounts.ExtraAccountManager;
import miui.cloud.AuthoritiesModel;
import miui.cloud.Constants;
import miui.cloud.sync.providers.CalllogSyncInfoProvider;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;
import miui.cloud.util.SyncAutoSettingUtil;

/* loaded from: classes3.dex */
public class CloudSysHelper {
    private static final Map<String, String> MAIN_SYNCS_WITH_PKG;
    private static final String TAG = "CloudSysHelper";

    static {
        ArrayMap arrayMap = new ArrayMap();
        MAIN_SYNCS_WITH_PKG = arrayMap;
        arrayMap.put("sms", "com.android.mms");
        arrayMap.put(ContactsSyncInfoProvider.AUTHORITY, ContactsSyncInfoProvider.AUTHORITY);
        arrayMap.put(CalllogSyncInfoProvider.AUTHORITY, ContactsSyncInfoProvider.AUTHORITY);
    }

    public static boolean isAllMiCloudSyncOff(Context context) {
        return isMiCloudMainSyncItemsOff(context);
    }

    public static boolean isMiCloudMainSyncItemsOff(Context context) {
        Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(context);
        if (xiaomiAccount == null) {
            Log.d(TAG, "Account is null in isMainSyncsOff()");
            return true;
        } else if (!SyncAutoSettingUtil.getXiaomiGlobalSyncAutomatically()) {
            Log.d(TAG, "Master sync is off in isMainSyncsOff()");
            return true;
        } else {
            Iterator<String> it = new AuthoritiesModel(context, xiaomiAccount).getAllAuthorities().filterBy(AuthoritiesModel.UNAVAILABLE_AUTHORITIES_FILTER).toList().iterator();
            while (it.hasNext()) {
                if (ContentResolver.getSyncAutomatically(xiaomiAccount, it.next())) {
                    return false;
                }
            }
            Log.d(TAG, "all available authorities sync off");
            return true;
        }
    }

    public static boolean isXiaomiAccountPresent(Context context) {
        return ExtraAccountManager.getXiaomiAccount(context) != null;
    }

    public static void promptEnableAllMiCloudSync(Context context) {
        startMiCloudInfoSettingsAcitivity(context);
    }

    public static void promptEnableFindDevice(Context context) {
        startMiCloudInfoSettingsAcitivity(context);
    }

    public static void startMiCloudInfoSettingsAcitivity(Context context) {
        Intent intent = new Intent(Constants.Intents.ACTION_MICLOUD_INFO_SETTINGS);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }
}
