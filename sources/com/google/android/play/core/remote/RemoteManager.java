package com.google.android.play.core.remote;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IInterface;
import com.google.android.play.core.splitcompat.util.PlayCore;
import com.google.android.play.core.tasks.TaskWrapper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public final class RemoteManager<T extends IInterface> {
    private static final Map<String, Handler> sHandlerMap = Collections.synchronizedMap(new HashMap());
    boolean mBindingService;
    final Context mContext;
    T mIInterface;
    private final String mKey;
    private final WeakReference<OnBinderDiedListener> mOnBinderDiedListenerWkRef;
    final PlayCore mPlayCore;
    final IRemote<T> mRemote;
    ServiceConnection mServiceConnection;
    private final Intent mSplitInstallServiceIntent;
    final List<RemoteTask> mPendingTasks = new ArrayList();
    private final IBinder.DeathRecipient mDeathRecipient = new DeathRecipientImpl(this);

    public RemoteManager(Context context, PlayCore playCore, String str, Intent intent, IRemote<T> iRemote, OnBinderDiedListener onBinderDiedListener) {
        this.mContext = context;
        this.mPlayCore = playCore;
        this.mKey = str;
        this.mSplitInstallServiceIntent = intent;
        this.mRemote = iRemote;
        this.mOnBinderDiedListenerWkRef = new WeakReference<>(onBinderDiedListener);
    }

    private Handler getHandler() {
        Handler handler;
        Map<String, Handler> map = sHandlerMap;
        synchronized (map) {
            if (!map.containsKey(this.mKey)) {
                HandlerThread handlerThread = new HandlerThread(this.mKey, 10);
                handlerThread.start();
                map.put(this.mKey, new Handler(handlerThread.getLooper()));
            }
            handler = map.get(this.mKey);
        }
        return handler;
    }

    public void bindService(RemoteTask remoteTask) {
        post(new BindServiceTask(this, remoteTask));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void bindServiceInternal(RemoteTask remoteTask) {
        if (this.mIInterface != null || this.mBindingService) {
            if (!this.mBindingService) {
                remoteTask.run();
                return;
            }
            this.mPlayCore.info("Waiting to bind to the service.", new Object[0]);
            this.mPendingTasks.add(remoteTask);
            return;
        }
        this.mPlayCore.info("Initiate binding to the service.", new Object[0]);
        this.mPendingTasks.add(remoteTask);
        ServiceConnectionImpl serviceConnectionImpl = new ServiceConnectionImpl(this);
        this.mServiceConnection = serviceConnectionImpl;
        this.mBindingService = true;
        if (this.mContext.bindService(this.mSplitInstallServiceIntent, serviceConnectionImpl, 1)) {
            return;
        }
        this.mPlayCore.info("Failed to bind to the service.", new Object[0]);
        this.mBindingService = false;
        Iterator<RemoteTask> it = this.mPendingTasks.iterator();
        while (it.hasNext()) {
            TaskWrapper task = it.next().getTask();
            if (task != null) {
                task.setException(new RemoteServiceException());
            }
        }
        this.mPendingTasks.clear();
    }

    public T getIInterface() {
        return this.mIInterface;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void linkToDeath() {
        this.mPlayCore.info("linkToDeath", new Object[0]);
        try {
            this.mIInterface.asBinder().linkToDeath(this.mDeathRecipient, 0);
        } catch (Throwable unused) {
            this.mPlayCore.info("linkToDeath failed", new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void post(RemoteTask remoteTask) {
        getHandler().post(remoteTask);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reportBinderDeath() {
        this.mPlayCore.info("reportBinderDeath", new Object[0]);
        OnBinderDiedListener onBinderDiedListener = this.mOnBinderDiedListenerWkRef.get();
        if (onBinderDiedListener != null) {
            this.mPlayCore.info("calling onBinderDied", new Object[0]);
            onBinderDiedListener.onBinderDied();
        }
    }

    public void unbindService() {
        post(new UnbindServiceTask(this));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unlinkToDeath() {
        this.mPlayCore.info("unlinkToDeath", new Object[0]);
        this.mIInterface.asBinder().unlinkToDeath(this.mDeathRecipient, 0);
    }
}
