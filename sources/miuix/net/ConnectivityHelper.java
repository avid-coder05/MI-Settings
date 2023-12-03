package miuix.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import miuix.core.util.SoftReferenceSingleton;

/* loaded from: classes5.dex */
public class ConnectivityHelper {
    private static final SoftReferenceSingleton<ConnectivityHelper> INSTANCE = new SoftReferenceSingleton<ConnectivityHelper>() { // from class: miuix.net.ConnectivityHelper.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // miuix.core.util.SoftReferenceSingleton
        public ConnectivityHelper createInstance(Object obj) {
            return new ConnectivityHelper((Context) obj);
        }
    };
    private ConnectivityManager mConnectivityManager;

    private ConnectivityHelper(Context context) {
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
    }

    public static ConnectivityHelper getInstance(Context context) {
        return INSTANCE.get(context);
    }

    public boolean isNetworkConnected() {
        NetworkInfo activeNetworkInfo = this.mConnectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
