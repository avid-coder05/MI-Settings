package com.xiaomi.micloudsdk.remote;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.xiaomi.micloudsdk.utils.ThreadUtil;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/* loaded from: classes2.dex */
public abstract class RemoteMethodInvoker<R> implements ServiceConnection {
    private static final String TAG = "RemoteMethodInvoker";
    private final AsyncFuture<IBinder> future = new AsyncFuture<>();
    private final Context mContext;

    /* loaded from: classes2.dex */
    private static class AsyncFuture<V> extends FutureTask<V> {
        public AsyncFuture() {
            super(new Callable<V>() { // from class: com.xiaomi.micloudsdk.remote.RemoteMethodInvoker.AsyncFuture.1
                @Override // java.util.concurrent.Callable
                public V call() throws Exception {
                    throw new IllegalStateException("this should never be called");
                }
            });
        }

        public void setResult(V v) {
            set(v);
        }
    }

    public RemoteMethodInvoker(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context can't be null");
        }
        this.mContext = context.getApplicationContext();
    }

    protected abstract boolean bindService(Context context, ServiceConnection serviceConnection);

    public R invoke() {
        ThreadUtil.ensureWorkerThread();
        try {
            if (!bindService(this.mContext, this)) {
                Log.e(TAG, "Cannot bind remote service.");
                return null;
            }
            try {
                try {
                    try {
                        R invokeRemoteMethod = invokeRemoteMethod(this.future.get());
                        try {
                            this.mContext.unbindService(this);
                        } catch (NoSuchElementException unused) {
                        }
                        return invokeRemoteMethod;
                    } catch (RemoteException e) {
                        Log.e(TAG, "error while invoking service methods", e);
                        try {
                            this.mContext.unbindService(this);
                        } catch (NoSuchElementException unused2) {
                        }
                        return null;
                    }
                } catch (NoSuchElementException unused3) {
                    return null;
                }
            } catch (InterruptedException unused4) {
                Thread.currentThread().interrupt();
                try {
                    this.mContext.unbindService(this);
                } catch (NoSuchElementException unused5) {
                }
                return null;
            } catch (ExecutionException unused6) {
                this.mContext.unbindService(this);
                return null;
            }
        } catch (Throwable th) {
            try {
                this.mContext.unbindService(this);
            } catch (NoSuchElementException unused7) {
            }
            throw th;
        }
    }

    protected abstract R invokeRemoteMethod(IBinder iBinder) throws RemoteException;

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.i(TAG, "RemoteMethodInvoker connects remote service " + componentName.getShortClassName());
        this.future.setResult(iBinder);
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
    }
}
