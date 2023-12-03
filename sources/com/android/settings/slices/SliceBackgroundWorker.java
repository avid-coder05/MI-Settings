package com.android.settings.slices;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.vip.VipService;

/* loaded from: classes2.dex */
public abstract class SliceBackgroundWorker<E> implements Closeable {
    private static final Map<Uri, SliceBackgroundWorker> LIVE_WORKERS = new ArrayMap();
    private List<E> mCachedResults;
    private final Context mContext;
    private final Uri mUri;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class NotifySliceChangeHandler extends Handler {
        private static NotifySliceChangeHandler sHandler;
        private final Map<Uri, Long> mLastUpdateTimeLookup;

        private NotifySliceChangeHandler(Looper looper) {
            super(looper);
            this.mLastUpdateTimeLookup = Collections.synchronizedMap(new ArrayMap());
        }

        static /* synthetic */ NotifySliceChangeHandler access$000() {
            return getInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void cancelSliceUpdate(SliceBackgroundWorker sliceBackgroundWorker) {
            removeMessages(VipService.VIP_SERVICE_FAILURE, sliceBackgroundWorker);
            this.mLastUpdateTimeLookup.remove(sliceBackgroundWorker.getUri());
        }

        private static NotifySliceChangeHandler getInstance() {
            if (sHandler == null) {
                HandlerThread handlerThread = new HandlerThread("NotifySliceChangeHandler", 10);
                handlerThread.start();
                sHandler = new NotifySliceChangeHandler(handlerThread.getLooper());
            }
            return sHandler;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateSlice(SliceBackgroundWorker sliceBackgroundWorker) {
            if (hasMessages(VipService.VIP_SERVICE_FAILURE, sliceBackgroundWorker)) {
                return;
            }
            Message obtainMessage = obtainMessage(VipService.VIP_SERVICE_FAILURE, sliceBackgroundWorker);
            long longValue = this.mLastUpdateTimeLookup.getOrDefault(sliceBackgroundWorker.getUri(), 0L).longValue();
            if (longValue == 0) {
                sendMessageDelayed(obtainMessage, 300L);
            } else if (SystemClock.uptimeMillis() - longValue > 300) {
                sendMessage(obtainMessage);
            } else {
                sendMessageAtTime(obtainMessage, longValue + 300);
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1000) {
                return;
            }
            SliceBackgroundWorker sliceBackgroundWorker = (SliceBackgroundWorker) message.obj;
            Uri uri = sliceBackgroundWorker.getUri();
            Context context = sliceBackgroundWorker.getContext();
            this.mLastUpdateTimeLookup.put(uri, Long.valueOf(SystemClock.uptimeMillis()));
            context.getContentResolver().notifyChange(uri, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SliceBackgroundWorker(Context context, Uri uri) {
        this.mContext = context;
        this.mUri = uri;
    }

    private static SliceBackgroundWorker createInstance(Context context, Uri uri, Class<? extends SliceBackgroundWorker> cls) {
        Log.d("SliceBackgroundWorker", "create instance: " + cls);
        try {
            return cls.getConstructor(Context.class, Uri.class).newInstance(context, uri);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Invalid slice background worker: " + cls, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SliceBackgroundWorker getInstance(Context context, Sliceable sliceable, Uri uri) {
        SliceBackgroundWorker sliceBackgroundWorker = getInstance(uri);
        if (sliceBackgroundWorker == null) {
            SliceBackgroundWorker createInstance = createInstance(context.getApplicationContext(), uri, sliceable.getBackgroundWorkerClass());
            LIVE_WORKERS.put(uri, createInstance);
            return createInstance;
        }
        return sliceBackgroundWorker;
    }

    public static <T extends SliceBackgroundWorker> T getInstance(Uri uri) {
        return (T) LIVE_WORKERS.get(uri);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void shutdown() {
        Iterator<SliceBackgroundWorker> it = LIVE_WORKERS.values().iterator();
        while (it.hasNext()) {
            try {
                it.next().close();
            } catch (IOException e) {
                Log.w("SliceBackgroundWorker", "Shutting down worker failed", e);
            }
        }
        LIVE_WORKERS.clear();
    }

    protected boolean areListsTheSame(List<E> list, List<E> list2) {
        return list.equals(list2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Context getContext() {
        return this.mContext;
    }

    public final List<E> getResults() {
        if (this.mCachedResults == null) {
            return null;
        }
        return new ArrayList(this.mCachedResults);
    }

    protected Uri getUri() {
        return this.mUri;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void notifySliceChange() {
        NotifySliceChangeHandler.access$000().updateSlice(this);
    }

    protected abstract void onSlicePinned();

    protected abstract void onSliceUnpinned();

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pin() {
        onSlicePinned();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unpin() {
        onSliceUnpinned();
        NotifySliceChangeHandler.access$000().cancelSliceUpdate(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void updateResults(List<E> list) {
        boolean z = true;
        if (list != null) {
            z = true ^ areListsTheSame(list, this.mCachedResults);
        } else if (this.mCachedResults == null) {
            z = false;
        }
        if (z) {
            this.mCachedResults = list;
            notifySliceChange();
        }
    }
}
