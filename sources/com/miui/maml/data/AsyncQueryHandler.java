package com.miui.maml.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.lang.ref.WeakReference;

/* loaded from: classes2.dex */
public abstract class AsyncQueryHandler extends Handler {
    private static Looper sLooper;
    final WeakReference<ContentResolver> mResolver;
    private Handler mWorkerThreadHandler;

    /* loaded from: classes2.dex */
    protected static final class WorkerArgs {
        public Object cookie;
        public Handler handler;
        public String orderBy;
        public String[] projection;
        public Object result;
        public String selection;
        public String[] selectionArgs;
        public Uri uri;
        public ContentValues values;

        protected WorkerArgs() {
        }
    }

    /* loaded from: classes2.dex */
    protected class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Cursor cursor;
            ContentResolver contentResolver = AsyncQueryHandler.this.mResolver.get();
            if (contentResolver == null) {
                return;
            }
            WorkerArgs workerArgs = (WorkerArgs) message.obj;
            int i = message.what;
            int i2 = message.arg1;
            if (i2 == 1) {
                try {
                    cursor = contentResolver.query(workerArgs.uri, workerArgs.projection, workerArgs.selection, workerArgs.selectionArgs, workerArgs.orderBy);
                    if (cursor != null) {
                        cursor.getCount();
                    }
                } catch (Exception e) {
                    Log.w("AsyncQuery", "Exception thrown during handling EVENT_ARG_QUERY", e);
                    cursor = null;
                }
                workerArgs.result = cursor;
            } else if (i2 == 2) {
                workerArgs.result = contentResolver.insert(workerArgs.uri, workerArgs.values);
            } else if (i2 == 3) {
                workerArgs.result = Integer.valueOf(contentResolver.update(workerArgs.uri, workerArgs.values, workerArgs.selection, workerArgs.selectionArgs));
            } else if (i2 == 4) {
                workerArgs.result = Integer.valueOf(contentResolver.delete(workerArgs.uri, workerArgs.selection, workerArgs.selectionArgs));
            }
            Message obtainMessage = workerArgs.handler.obtainMessage(i);
            obtainMessage.obj = workerArgs;
            obtainMessage.arg1 = message.arg1;
            obtainMessage.sendToTarget();
        }
    }

    public AsyncQueryHandler(Looper looper, ContentResolver contentResolver) {
        super(looper);
        this.mResolver = new WeakReference<>(contentResolver);
        synchronized (android.content.AsyncQueryHandler.class) {
            if (sLooper == null) {
                HandlerThread handlerThread = new HandlerThread("AsyncQueryWorker");
                handlerThread.start();
                sLooper = handlerThread.getLooper();
            }
        }
        this.mWorkerThreadHandler = createHandler(sLooper);
    }

    public final void cancelOperation(int i) {
        this.mWorkerThreadHandler.removeMessages(i);
    }

    protected abstract Handler createHandler(Looper looper);

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        WorkerArgs workerArgs = (WorkerArgs) message.obj;
        int i = message.what;
        int i2 = message.arg1;
        if (i2 == 1) {
            onQueryComplete(i, workerArgs.cookie, (Cursor) workerArgs.result);
        } else if (i2 == 2) {
            onInsertComplete(i, workerArgs.cookie, (Uri) workerArgs.result);
        } else if (i2 == 3) {
            onUpdateComplete(i, workerArgs.cookie, ((Integer) workerArgs.result).intValue());
        } else if (i2 != 4) {
        } else {
            onDeleteComplete(i, workerArgs.cookie, ((Integer) workerArgs.result).intValue());
        }
    }

    protected void onDeleteComplete(int i, Object obj, int i2) {
    }

    protected void onInsertComplete(int i, Object obj, Uri uri) {
    }

    protected abstract void onQueryComplete(int i, Object obj, Cursor cursor);

    protected void onUpdateComplete(int i, Object obj, int i2) {
    }

    public void startQuery(int i, Object obj, Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Message obtainMessage = this.mWorkerThreadHandler.obtainMessage(i);
        obtainMessage.arg1 = 1;
        WorkerArgs workerArgs = new WorkerArgs();
        workerArgs.handler = this;
        workerArgs.uri = uri;
        workerArgs.projection = strArr;
        workerArgs.selection = str;
        workerArgs.selectionArgs = strArr2;
        workerArgs.orderBy = str2;
        workerArgs.cookie = obj;
        obtainMessage.obj = workerArgs;
        this.mWorkerThreadHandler.sendMessage(obtainMessage);
    }
}
