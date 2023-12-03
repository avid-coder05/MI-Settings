package micloud.compat.independent.sync;

import android.content.Context;
import android.content.Intent;
import miui.cloud.Constants;

/* loaded from: classes2.dex */
class GdprUtilsCompat_V24 extends GdprUtilsCompat_V23 {
    @Override // micloud.compat.independent.sync.GdprUtilsCompat_Base, micloud.compat.independent.sync.IGdprUtilsCompat
    public void notifyPrivacyDenied(Context context) {
        Intent intent = new Intent("com.xiaomi.action.PRIVACY_DENIED");
        intent.setPackage(Constants.CLOUDSERVICE_PACKAGE_NAME);
        if (context.getPackageManager().resolveService(intent, 0) != null) {
            context.startService(intent);
        } else {
            context.sendBroadcast(intent);
        }
    }
}
