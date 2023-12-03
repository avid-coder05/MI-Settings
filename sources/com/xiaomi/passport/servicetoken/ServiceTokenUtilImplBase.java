package com.xiaomi.passport.servicetoken;

import android.content.Context;
import android.os.AsyncTask;
import java.util.concurrent.Executor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class ServiceTokenUtilImplBase implements IServiceTokenUtil {
    private static Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;

    /* loaded from: classes2.dex */
    private static abstract class MyWorker {
        private MyWorker() {
        }

        protected abstract ServiceTokenResult realWork();

        ServiceTokenFuture work() {
            final ServiceTokenFuture serviceTokenFuture = new ServiceTokenFuture(null);
            ServiceTokenUtilImplBase.executor.execute(new Runnable() { // from class: com.xiaomi.passport.servicetoken.ServiceTokenUtilImplBase.MyWorker.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        serviceTokenFuture.setServerData(MyWorker.this.realWork());
                    } catch (Throwable th) {
                        serviceTokenFuture.setServerSideThrowable(th);
                    }
                }
            });
            return serviceTokenFuture;
        }
    }

    @Override // com.xiaomi.passport.servicetoken.IServiceTokenUtil
    public final ServiceTokenFuture getServiceToken(final Context context, final String str) {
        return new MyWorker() { // from class: com.xiaomi.passport.servicetoken.ServiceTokenUtilImplBase.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            @Override // com.xiaomi.passport.servicetoken.ServiceTokenUtilImplBase.MyWorker
            protected ServiceTokenResult realWork() {
                return ServiceTokenUtilImplBase.this.getServiceTokenImpl(context, str);
            }
        }.work();
    }

    protected abstract ServiceTokenResult getServiceTokenImpl(Context context, String str);

    @Override // com.xiaomi.passport.servicetoken.IServiceTokenUtil
    public final ServiceTokenFuture invalidateServiceToken(final Context context, final ServiceTokenResult serviceTokenResult) {
        return new MyWorker() { // from class: com.xiaomi.passport.servicetoken.ServiceTokenUtilImplBase.2
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            @Override // com.xiaomi.passport.servicetoken.ServiceTokenUtilImplBase.MyWorker
            protected ServiceTokenResult realWork() {
                return ServiceTokenUtilImplBase.this.invalidateServiceTokenImpl(context, serviceTokenResult);
            }
        }.work();
    }

    protected abstract ServiceTokenResult invalidateServiceTokenImpl(Context context, ServiceTokenResult serviceTokenResult);
}
