package com.xiaomi.accountsdk.futureservice;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.xiaomi.accountsdk.account.utils.ReferenceHolder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: classes2.dex */
public abstract class ClientFuture<ServerDataType, ClientDataType> extends FutureTask<ClientDataType> {
    private final ReferenceHolder<ClientCallback<ClientDataType>> mClientCallbackRef;

    /* loaded from: classes2.dex */
    public interface ClientCallback<ClientSideDataType> {
        void call(ClientFuture<?, ClientSideDataType> clientFuture);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ClientFuture(ClientCallback<ClientDataType> clientCallback) {
        super(new Callable<ClientDataType>() { // from class: com.xiaomi.accountsdk.futureservice.ClientFuture.1
            @Override // java.util.concurrent.Callable
            public ClientDataType call() throws Exception {
                throw new IllegalStateException("this should never be called");
            }
        });
        this.mClientCallbackRef = new ReferenceHolder<>(clientCallback);
    }

    private void ensureNotOnMainThread() {
        Looper myLooper = Looper.myLooper();
        if (myLooper == null || myLooper != Looper.getMainLooper()) {
            return;
        }
        Log.e("ClientFuture", "calling this from your main thread can lead to deadlock and/or ANRs", new IllegalStateException("ClientFuture#calling this from your main thread can lead to deadlock"));
    }

    private void onDone() {
        if (isCancelled()) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.xiaomi.accountsdk.futureservice.ClientFuture.2
            @Override // java.lang.Runnable
            public void run() {
                ClientCallback clientCallback = (ClientCallback) ClientFuture.this.mClientCallbackRef.get();
                if (clientCallback != null) {
                    clientCallback.call(ClientFuture.this);
                }
            }
        });
    }

    @Override // java.util.concurrent.FutureTask, java.util.concurrent.Future
    public boolean cancel(boolean z) {
        this.mClientCallbackRef.set(null);
        return super.cancel(z);
    }

    protected abstract ClientDataType convertServerDataToClientData(ServerDataType serverdatatype) throws Throwable;

    @Override // java.util.concurrent.FutureTask
    protected void done() {
        super.done();
        onDone();
    }

    @Override // java.util.concurrent.FutureTask, java.util.concurrent.Future
    public ClientDataType get() throws InterruptedException, ExecutionException {
        if (!isDone()) {
            ensureNotOnMainThread();
        }
        return (ClientDataType) super.get();
    }

    @Override // java.util.concurrent.FutureTask, java.util.concurrent.Future
    public ClientDataType get(long j, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!isDone()) {
            ensureNotOnMainThread();
        }
        return (ClientDataType) super.get(j, timeUnit);
    }

    public final void setServerData(ServerDataType serverdatatype) {
        try {
            set(convertServerDataToClientData(serverdatatype));
        } catch (Throwable th) {
            setException(th);
        }
    }

    public final void setServerSideThrowable(Throwable th) {
        setException(th);
    }
}
