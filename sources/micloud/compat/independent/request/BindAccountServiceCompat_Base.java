package micloud.compat.independent.request;

import android.content.Context;
import android.content.ServiceConnection;

/* loaded from: classes2.dex */
class BindAccountServiceCompat_Base implements IBindAccountServiceCompat {
    @Override // micloud.compat.independent.request.IBindAccountServiceCompat
    public boolean bindAccountService(Context context, ServiceConnection serviceConnection) {
        return false;
    }
}
