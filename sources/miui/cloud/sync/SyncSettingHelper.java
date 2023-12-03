package miui.cloud.sync;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import miui.accounts.ExtraAccountManager;
import miui.cloud.Constants;

/* loaded from: classes3.dex */
public class SyncSettingHelper {
    public static void openFindDeviceSettingUI(Activity activity) {
        if (ExtraAccountManager.getXiaomiAccount(activity) == null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ExtraAccountManager.EXTRA_SHOW_SYNC_SETTINGS, true);
            AccountManager.get(activity).addAccount("com.xiaomi", null, null, bundle, activity, null, null);
            return;
        }
        Intent intent = new Intent(Constants.Intents.ACTION_FIND_DEVICE_GUIDE);
        intent.putExtra("extra_micloud_find_device_guide_source", activity.getPackageName());
        intent.setPackage(Constants.CLOUDSERVICE_PACKAGE_NAME);
        activity.startActivity(intent);
    }
}
