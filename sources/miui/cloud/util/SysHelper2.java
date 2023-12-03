package miui.cloud.util;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import miui.content.ExtraIntent;

/* loaded from: classes3.dex */
public class SysHelper2 extends SysHelper {
    public static Intent getCustomSyncSettings(Context context, Account account, String str) {
        Intent intent = new Intent(str + ExtraIntent.SYNC_SETTINGS_ACTION_APPENDER);
        intent.putExtra("account", account);
        intent.putExtra("authority", str);
        if (context.getPackageManager().queryIntentActivities(intent, 32).isEmpty()) {
            return null;
        }
        return intent;
    }
}
