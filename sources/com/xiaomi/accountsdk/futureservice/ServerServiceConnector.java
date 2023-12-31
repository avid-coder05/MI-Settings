package com.xiaomi.accountsdk.futureservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import com.xiaomi.accountsdk.utils.AccountLog;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes2.dex */
public abstract class ServerServiceConnector<IServiceType, ServerDataType, ClientDataType> implements ServiceConnection {
    private static final ExecutorService mThreadPool = Executors.newCachedThreadPool();
    private final String mActionName;
    private Context mContext;
    private volatile ClientFuture<ServerDataType, ClientDataType> mFuture;
    private IServiceType mIService;
    private final String mServicePackageName;
    private final AtomicBoolean bindFlag = new AtomicBoolean(false);
    private final AtomicBoolean unbindFlag = new AtomicBoolean(false);

    /* JADX INFO: Access modifiers changed from: protected */
    public ServerServiceConnector(Context context, String str, String str2, ClientFuture<ServerDataType, ClientDataType> clientFuture) {
        this.mContext = context.getApplicationContext();
        this.mActionName = str;
        this.mServicePackageName = str2;
        this.mFuture = clientFuture;
    }

    static boolean checkFirstTimeCall(AtomicBoolean atomicBoolean) {
        return atomicBoolean.compareAndSet(false, true);
    }

    private void clearFields() {
        this.mIService = null;
        this.mContext = null;
        this.mFuture = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doWork() {
        try {
            ServerDataType callServiceWork = callServiceWork();
            if (this.mFuture != null) {
                this.mFuture.setServerData(callServiceWork);
            }
        } catch (Throwable th) {
            if (this.mFuture != null) {
                this.mFuture.setServerSideThrowable(th);
            }
        }
    }

    public final boolean bind() {
        if (checkFirstTimeCall(this.bindFlag)) {
            Intent intent = new Intent();
            intent.setAction(this.mActionName);
            intent.setPackage(this.mServicePackageName);
            boolean bindService = this.mContext.bindService(intent, this, 1);
            if (!bindService) {
                this.mFuture.setServerSideThrowable(Build.VERSION.SDK_INT < 15 ? new RemoteException() : new RemoteException("failed to bind to service"));
                unbind();
            }
            return bindService;
        }
        throw new IllegalStateException("should only bind for one time");
    }

    protected abstract IServiceType binderToServiceType(IBinder iBinder);

    protected abstract ServerDataType callServiceWork() throws RemoteException;

    /* JADX INFO: Access modifiers changed from: protected */
    public final IServiceType getIService() {
        return this.mIService;
    }

    @Override // android.content.ServiceConnection
    public void onBindingDied(ComponentName componentName) {
        AccountLog.i("ServerServiceConnector", "onBindingDied>>>name:" + componentName);
    }

    @Override // android.content.ServiceConnection
    public void onNullBinding(ComponentName componentName) {
        AccountLog.i("ServerServiceConnector", "onNullBinding>>>name:" + componentName);
    }

    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.mIService = binderToServiceType(iBinder);
        mThreadPool.execute(new Runnable() { // from class: com.xiaomi.accountsdk.futureservice.ServerServiceConnector.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ServerServiceConnector.this.doWork();
                } finally {
                    ServerServiceConnector.this.unbind();
                }
            }
        });
    }

    @Override // android.content.ServiceConnection
    public final void onServiceDisconnected(ComponentName componentName) {
        clearFields();
    }

    final void unbind() {
        if (checkFirstTimeCall(this.unbindFlag)) {
            Context context = this.mContext;
            if (context != null) {
                context.unbindService(this);
            }
            clearFields();
        }
    }
}
