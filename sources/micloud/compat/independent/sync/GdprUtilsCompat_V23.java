package micloud.compat.independent.sync;

import android.content.Context;
import android.provider.Settings;

/* loaded from: classes2.dex */
class GdprUtilsCompat_V23 extends GdprUtilsCompat_Base {
    @Override // micloud.compat.independent.sync.GdprUtilsCompat_Base, micloud.compat.independent.sync.IGdprUtilsCompat
    public boolean isGdprPermissionGranted(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "micloud_gdpr_permission_granted", 1) != 0;
    }
}
