package com.xiaomi.passport.servicetoken;

import android.os.RemoteException;
import com.xiaomi.accountsdk.futureservice.ClientFuture;
import com.xiaomi.passport.servicetoken.ServiceTokenResult;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: classes2.dex */
public class ServiceTokenFuture extends ClientFuture<ServiceTokenResult, ServiceTokenResult> {
    public ServiceTokenFuture(ClientFuture.ClientCallback<ServiceTokenResult> clientCallback) {
        super(clientCallback);
    }

    private ServiceTokenResult getInternal(Long l, TimeUnit timeUnit) {
        try {
            return (l == null || timeUnit == null) ? (ServiceTokenResult) super.get() : (ServiceTokenResult) super.get(l.longValue(), timeUnit);
        } catch (InterruptedException e) {
            return new ServiceTokenResult.Builder(null).errorCode(ServiceTokenResult.ErrorCode.ERROR_CANCELLED).errorMessage(e.getMessage()).build();
        } catch (ExecutionException e2) {
            if (e2.getCause() instanceof RemoteException) {
                return new ServiceTokenResult.Builder(null).errorCode(ServiceTokenResult.ErrorCode.ERROR_REMOTE_EXCEPTION).errorMessage(e2.getMessage()).build();
            }
            return new ServiceTokenResult.Builder(null).errorCode(ServiceTokenResult.ErrorCode.ERROR_UNKNOWN).errorMessage(e2.getCause() != null ? e2.getCause().getMessage() : e2.getMessage()).build();
        } catch (TimeoutException unused) {
            return new ServiceTokenResult.Builder(null).errorCode(ServiceTokenResult.ErrorCode.ERROR_TIME_OUT).errorMessage("time out after " + l + " " + timeUnit).build();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.accountsdk.futureservice.ClientFuture
    public ServiceTokenResult convertServerDataToClientData(ServiceTokenResult serviceTokenResult) throws Throwable {
        return serviceTokenResult;
    }

    @Override // com.xiaomi.accountsdk.futureservice.ClientFuture, java.util.concurrent.FutureTask, java.util.concurrent.Future
    public ServiceTokenResult get() {
        return getInternal(null, null);
    }

    @Override // com.xiaomi.accountsdk.futureservice.ClientFuture, java.util.concurrent.FutureTask, java.util.concurrent.Future
    public ServiceTokenResult get(long j, TimeUnit timeUnit) {
        return getInternal(Long.valueOf(j), timeUnit);
    }
}
