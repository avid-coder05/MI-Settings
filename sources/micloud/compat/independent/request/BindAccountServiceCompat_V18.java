package micloud.compat.independent.request;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import miui.accounts.ExtraAccountManager;
import miui.content.ExtraIntent;

/* loaded from: classes2.dex */
class BindAccountServiceCompat_V18 extends BindAccountServiceCompat_Base {
    private static boolean bindAccountService(Context context, String str, ServiceConnection serviceConnection) {
        Intent intent = new Intent(str);
        intent.setPackage(ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
        return context.bindService(intent, serviceConnection, 1);
    }

    @Override // micloud.compat.independent.request.BindAccountServiceCompat_Base, micloud.compat.independent.request.IBindAccountServiceCompat
    public boolean bindAccountService(Context context, ServiceConnection serviceConnection) {
        return bindAccountService(context, "com.xiaomi.account.action.BIND_XIAOMI_ACCOUNT_SERVICE", serviceConnection) || bindAccountService(context, ExtraIntent.ACTION_BIND_XIAOMI_ACCOUNT_SERVICE, serviceConnection);
    }
}
