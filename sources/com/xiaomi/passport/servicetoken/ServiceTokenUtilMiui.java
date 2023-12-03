package com.xiaomi.passport.servicetoken;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.xiaomi.accountsdk.futureservice.ClientFuture;
import com.xiaomi.accountsdk.futureservice.ServerServiceConnector;
import com.xiaomi.accountsdk.futureservice.SimpleClientFuture;
import com.xiaomi.accountsdk.utils.AccountLog;
import com.xiaomi.accountsdk.utils.MiuiOsBuildReflection;
import com.xiaomi.accountsdk.utils.MiuiVersionDev;
import com.xiaomi.accountsdk.utils.MiuiVersionStable;
import com.xiaomi.accountsdk.utils.SystemXiaomiAccountPackageName;
import com.xiaomi.passport.IPassportServiceTokenService;
import com.xiaomi.passport.servicetoken.ServiceTokenResult;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class ServiceTokenUtilMiui extends ServiceTokenUtilImplBase {
    private static volatile AtomicBoolean miuiServiceTokenServiceAvailability = new AtomicBoolean(true);
    private static volatile Boolean xiaomiAccountAppSlhPhAvailability = null;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class MiuiCompatUtil {
        private static volatile Boolean cachedParcelIssueCheckResult;
        private static volatile Boolean cachedWebLoginIssueCheckResult;

        static boolean hasServiceTokenResultParcelCompatIssue() {
            if (cachedParcelIssueCheckResult != null) {
                return cachedParcelIssueCheckResult.booleanValue();
            }
            boolean z = false;
            if ((MiuiOsBuildReflection.isStable(false) && MiuiVersionStable.earlyThan(new MiuiVersionStable(8, 0), false)) || (MiuiOsBuildReflection.isDevButNotAlpha(false) && MiuiVersionDev.earlyThan(new MiuiVersionDev(6, 7, 1), false))) {
                z = true;
            }
            if (cachedParcelIssueCheckResult == null) {
                cachedParcelIssueCheckResult = new Boolean(z);
            }
            return cachedParcelIssueCheckResult.booleanValue();
        }

        static boolean hasWebLoginCompatIssue() {
            if (cachedWebLoginIssueCheckResult != null) {
                return cachedWebLoginIssueCheckResult.booleanValue();
            }
            boolean z = false;
            if ((MiuiOsBuildReflection.isStable(false) && MiuiVersionStable.earlyThan(new MiuiVersionStable(8, 2), false)) || (MiuiOsBuildReflection.isDevButNotAlpha(false) && MiuiVersionDev.earlyThan(new MiuiVersionDev(6, 11, 25), false))) {
                z = true;
            }
            if (cachedWebLoginIssueCheckResult == null) {
                cachedWebLoginIssueCheckResult = new Boolean(z);
            }
            return cachedWebLoginIssueCheckResult.booleanValue();
        }
    }

    /* loaded from: classes2.dex */
    private static abstract class ServiceTokenServiceConnector extends ServiceTokenServiceConnectorBase<ServiceTokenResult> {
        protected ServiceTokenServiceConnector(Context context, ServiceTokenFuture serviceTokenFuture) {
            super(context, serviceTokenFuture);
        }
    }

    /* loaded from: classes2.dex */
    private static abstract class ServiceTokenServiceConnectorBase<T> extends ServerServiceConnector<IPassportServiceTokenService, T, T> {
        protected ServiceTokenServiceConnectorBase(Context context, ClientFuture<T, T> clientFuture) {
            super(context, "com.xiaomi.account.action.SERVICE_TOKEN_OP", SystemXiaomiAccountPackageName.getValid(context), clientFuture);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.xiaomi.accountsdk.futureservice.ServerServiceConnector
        public final IPassportServiceTokenService binderToServiceType(IBinder iBinder) {
            return IPassportServiceTokenService.Stub.asInterface(iBinder);
        }
    }

    private boolean checkBindServiceSuccess(ServiceTokenFuture serviceTokenFuture) {
        return (serviceTokenFuture.isDone() && serviceTokenFuture.get().errorCode == ServiceTokenResult.ErrorCode.ERROR_REMOTE_EXCEPTION) ? false : true;
    }

    private ServiceTokenUtilAM getAMServiceTokenUtil() {
        return new ServiceTokenUtilAM(new AMUtilImpl(new AMKeys()));
    }

    public boolean doesXiaomiAccountAppSupportServiceTokenUIResponse(Context context) {
        if (miuiServiceTokenServiceAvailability.get()) {
            SimpleClientFuture simpleClientFuture = new SimpleClientFuture();
            new ServiceTokenServiceConnectorBase<Boolean>(context, simpleClientFuture) { // from class: com.xiaomi.passport.servicetoken.ServiceTokenUtilMiui.3
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.xiaomi.accountsdk.futureservice.ServerServiceConnector
                public Boolean callServiceWork() throws RemoteException {
                    return Boolean.valueOf(getIService().supportServiceTokenUIResponse());
                }
            }.bind();
            try {
                return ((Boolean) simpleClientFuture.get()).booleanValue();
            } catch (InterruptedException e) {
                AccountLog.w("ServiceTokenUtilMiui", "", e);
                return false;
            } catch (ExecutionException e2) {
                AccountLog.w("ServiceTokenUtilMiui", "", e2);
                return false;
            }
        }
        return false;
    }

    @Override // com.xiaomi.passport.servicetoken.ServiceTokenUtilImplBase
    public ServiceTokenResult getServiceTokenImpl(final Context context, final String str) {
        if (str != null && str.startsWith("weblogin:") && MiuiCompatUtil.hasWebLoginCompatIssue()) {
            return getAMServiceTokenUtil().getServiceTokenImpl(context, str);
        }
        if (miuiServiceTokenServiceAvailability.get()) {
            ServiceTokenFuture serviceTokenFuture = new ServiceTokenFuture(null);
            new ServiceTokenServiceConnector(context, serviceTokenFuture) { // from class: com.xiaomi.passport.servicetoken.ServiceTokenUtilMiui.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.xiaomi.accountsdk.futureservice.ServerServiceConnector
                public ServiceTokenResult callServiceWork() throws RemoteException {
                    return ServiceTokenUIErrorHandler.blockingHandleIntentError(context, getIService().getServiceToken(str));
                }
            }.bind();
            if (checkBindServiceSuccess(serviceTokenFuture)) {
                return serviceTokenFuture.get();
            }
            miuiServiceTokenServiceAvailability.set(false);
        }
        return getAMServiceTokenUtil().getServiceTokenImpl(context, str);
    }

    @Override // com.xiaomi.passport.servicetoken.ServiceTokenUtilImplBase
    public ServiceTokenResult invalidateServiceTokenImpl(Context context, final ServiceTokenResult serviceTokenResult) {
        if (miuiServiceTokenServiceAvailability.get()) {
            ServiceTokenFuture serviceTokenFuture = new ServiceTokenFuture(null);
            new ServiceTokenServiceConnector(context, serviceTokenFuture) { // from class: com.xiaomi.passport.servicetoken.ServiceTokenUtilMiui.2
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.xiaomi.accountsdk.futureservice.ServerServiceConnector
                public ServiceTokenResult callServiceWork() throws RemoteException {
                    return getIService().invalidateServiceToken((serviceTokenResult == null || !MiuiCompatUtil.hasServiceTokenResultParcelCompatIssue()) ? serviceTokenResult : ServiceTokenResult.Builder.copyFrom(serviceTokenResult).useV1Parcel(true).build());
                }
            }.bind();
            if (checkBindServiceSuccess(serviceTokenFuture)) {
                return serviceTokenFuture.get();
            }
            miuiServiceTokenServiceAvailability.set(false);
        }
        return getAMServiceTokenUtil().invalidateServiceTokenImpl(context, serviceTokenResult);
    }
}
