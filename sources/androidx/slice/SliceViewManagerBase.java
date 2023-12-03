package androidx.slice;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.Pair;
import androidx.slice.SliceViewManager;
import androidx.slice.widget.SliceLiveData;
import java.util.concurrent.Executor;

/* loaded from: classes.dex */
public abstract class SliceViewManagerBase extends SliceViewManager {
    protected final Context mContext;
    private final ArrayMap<Pair<Uri, SliceViewManager.SliceCallback>, SliceListenerImpl> mListenerLookup = new ArrayMap<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SliceListenerImpl {
        final SliceViewManager.SliceCallback mCallback;
        final Executor mExecutor;
        private boolean mPinned;
        Uri mUri;
        final Runnable mUpdateSlice = new Runnable() { // from class: androidx.slice.SliceViewManagerBase.SliceListenerImpl.1
            @Override // java.lang.Runnable
            public void run() {
                SliceListenerImpl.this.tryPin();
                SliceListenerImpl sliceListenerImpl = SliceListenerImpl.this;
                final Slice bindSlice = Slice.bindSlice(SliceViewManagerBase.this.mContext, sliceListenerImpl.mUri, SliceLiveData.SUPPORTED_SPECS);
                SliceListenerImpl.this.mExecutor.execute(new Runnable() { // from class: androidx.slice.SliceViewManagerBase.SliceListenerImpl.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        SliceListenerImpl.this.mCallback.onSliceUpdated(bindSlice);
                    }
                });
            }
        };
        private final ContentObserver mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: androidx.slice.SliceViewManagerBase.SliceListenerImpl.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                AsyncTask.execute(SliceListenerImpl.this.mUpdateSlice);
            }
        };

        SliceListenerImpl(Uri uri, Executor executor, SliceViewManager.SliceCallback callback) {
            this.mUri = uri;
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        void startListening() {
            ContentProviderClient acquireContentProviderClient = SliceViewManagerBase.this.mContext.getContentResolver().acquireContentProviderClient(this.mUri);
            if (acquireContentProviderClient != null) {
                acquireContentProviderClient.release();
                SliceViewManagerBase.this.mContext.getContentResolver().registerContentObserver(this.mUri, true, this.mObserver);
                tryPin();
            }
        }

        void stopListening() {
            SliceViewManagerBase.this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
            if (this.mPinned) {
                SliceViewManagerBase.this.unpinSlice(this.mUri);
                this.mPinned = false;
            }
        }

        void tryPin() {
            if (this.mPinned) {
                return;
            }
            try {
                SliceViewManagerBase.this.pinSlice(this.mUri);
                this.mPinned = true;
            } catch (SecurityException unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SliceViewManagerBase(Context context) {
        this.mContext = context;
    }

    private SliceListenerImpl getListener(Uri uri, SliceViewManager.SliceCallback callback, SliceListenerImpl listener) {
        Pair<Uri, SliceViewManager.SliceCallback> pair = new Pair<>(uri, callback);
        synchronized (this.mListenerLookup) {
            SliceListenerImpl put = this.mListenerLookup.put(pair, listener);
            if (put != null) {
                put.stopListening();
            }
        }
        return listener;
    }

    @Override // androidx.slice.SliceViewManager
    public void registerSliceCallback(Uri uri, SliceViewManager.SliceCallback callback) {
        final Handler handler = new Handler(Looper.getMainLooper());
        registerSliceCallback(uri, new Executor() { // from class: androidx.slice.SliceViewManagerBase.1
            @Override // java.util.concurrent.Executor
            public void execute(Runnable command) {
                handler.post(command);
            }
        }, callback);
    }

    public void registerSliceCallback(Uri uri, Executor executor, SliceViewManager.SliceCallback callback) {
        getListener(uri, callback, new SliceListenerImpl(uri, executor, callback)).startListening();
    }

    @Override // androidx.slice.SliceViewManager
    public void unregisterSliceCallback(Uri uri, SliceViewManager.SliceCallback callback) {
        synchronized (this.mListenerLookup) {
            SliceListenerImpl remove = this.mListenerLookup.remove(new Pair(uri, callback));
            if (remove != null) {
                remove.stopListening();
            }
        }
    }
}
