package micloud.compat.independent.request;

import android.accounts.Account;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

/* loaded from: classes2.dex */
interface IRequestEnvBuilderCompat {

    /* loaded from: classes2.dex */
    public interface RequestEnv {
        String getEncryptedUserId(Context context, IBinder iBinder, Account account) throws RemoteException;

        Account getSystemAccount(Context context);

        String getUserAgent();

        void invalidateAuthToken(Context context);

        String queryAuthToken(Context context);
    }

    RequestEnv build();
}
