package micloud.compat.independent.request;

import android.accounts.Account;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.xiaomi.micloudsdk.data.ExtendedAuthToken;
import com.xiaomi.micloudsdk.remote.RemoteMethodInvoker;
import com.xiaomi.micloudsdk.request.utils.RequestContext;
import com.xiaomi.micloudsdk.utils.MiCloudSDKDependencyUtil;
import micloud.compat.independent.request.IRequestEnvBuilderCompat;

/* loaded from: classes2.dex */
public class RequestEnvBuilderCompat {
    private static final IRequestEnvBuilderCompat sRequestEnvBuilderCompatImpl;

    static {
        if (MiCloudSDKDependencyUtil.SDKEnvironment >= 18) {
            sRequestEnvBuilderCompatImpl = new RequestEnvBuilderCompat_V18();
        } else {
            sRequestEnvBuilderCompatImpl = new RequestEnvBuilderCompat_Base();
        }
    }

    public static RequestContext.RequestEnv build() {
        final IRequestEnvBuilderCompat.RequestEnv build = sRequestEnvBuilderCompatImpl.build();
        if (build == null) {
            return null;
        }
        return new RequestContext.RequestEnv() { // from class: micloud.compat.independent.request.RequestEnvBuilderCompat.1
            @Override // com.xiaomi.micloudsdk.request.utils.RequestContext.RequestEnv
            public String getAccountName() {
                Account systemAccount = IRequestEnvBuilderCompat.RequestEnv.this.getSystemAccount(RequestContext.getContext());
                if (systemAccount == null) {
                    return null;
                }
                return systemAccount.name;
            }

            @Override // com.xiaomi.micloudsdk.request.utils.RequestContext.RequestEnv
            public String getUserAgent() {
                return IRequestEnvBuilderCompat.RequestEnv.this.getUserAgent();
            }

            @Override // com.xiaomi.micloudsdk.request.utils.RequestContext.RequestEnv
            public void invalidateAuthToken() {
                IRequestEnvBuilderCompat.RequestEnv.this.invalidateAuthToken(RequestContext.getContext());
            }

            @Override // com.xiaomi.micloudsdk.request.utils.RequestContext.RequestEnv
            public ExtendedAuthToken queryAuthToken() {
                String queryAuthToken = IRequestEnvBuilderCompat.RequestEnv.this.queryAuthToken(RequestContext.getContext());
                if (queryAuthToken == null) {
                    return null;
                }
                return ExtendedAuthToken.parse(queryAuthToken);
            }

            @Override // com.xiaomi.micloudsdk.request.utils.RequestContext.RequestEnv
            public String queryEncryptedAccountName() {
                final Account systemAccount = IRequestEnvBuilderCompat.RequestEnv.this.getSystemAccount(RequestContext.getContext());
                if (systemAccount == null) {
                    return null;
                }
                return new RemoteMethodInvoker<String>(RequestContext.getContext()) { // from class: micloud.compat.independent.request.RequestEnvBuilderCompat.1.1
                    @Override // com.xiaomi.micloudsdk.remote.RemoteMethodInvoker
                    protected boolean bindService(Context context, ServiceConnection serviceConnection) {
                        return BindAccountServiceCompat.bindAccountService(context, serviceConnection);
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // com.xiaomi.micloudsdk.remote.RemoteMethodInvoker
                    public String invokeRemoteMethod(IBinder iBinder) throws RemoteException {
                        return IRequestEnvBuilderCompat.RequestEnv.this.getEncryptedUserId(RequestContext.getContext(), iBinder, systemAccount);
                    }
                }.invoke();
            }
        };
    }
}
