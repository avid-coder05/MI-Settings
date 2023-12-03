package com.miui.maml.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.miui.maml.RendererCore;
import com.miui.maml.ResourceLoader;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.ScreenElementRootFactory;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class RendererCoreCache implements RendererCore.OnReleaseListener {
    private HashMap<Object, RendererCoreInfo> mCaches = new HashMap<>();
    private Handler mHandler;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public class CheckCacheRunnable implements Runnable {
        private Object mKey;

        public CheckCacheRunnable(Object obj) {
            this.mKey = obj;
        }

        @Override // java.lang.Runnable
        public void run() {
            RendererCoreCache.this.checkCache(this.mKey);
        }
    }

    /* loaded from: classes2.dex */
    public interface OnCreateRootCallback {
        void onCreateRoot(ScreenElementRoot screenElementRoot);
    }

    /* loaded from: classes2.dex */
    public static class RendererCoreInfo {
        public long accessTime = Long.MAX_VALUE;
        public long cacheTime;
        public CheckCacheRunnable checkCache;
        public RendererCore r;

        public RendererCoreInfo(RendererCore rendererCore) {
            this.r = rendererCore;
        }
    }

    public RendererCoreCache(Handler handler) {
        this.mHandler = handler;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void checkCache(Object obj) {
        Log.d("RendererCoreCache", "checkCache: " + obj);
        RendererCoreInfo rendererCoreInfo = this.mCaches.get(obj);
        if (rendererCoreInfo == null) {
            Log.d("RendererCoreCache", "checkCache: the key does not exist, " + obj);
        } else if (rendererCoreInfo.accessTime == Long.MAX_VALUE) {
        } else {
            long currentTimeMillis = System.currentTimeMillis() - rendererCoreInfo.accessTime;
            if (currentTimeMillis >= rendererCoreInfo.cacheTime) {
                this.mCaches.remove(obj);
                Log.d("RendererCoreCache", "checkCache removed: " + obj);
            } else {
                if (currentTimeMillis < 0) {
                    rendererCoreInfo.accessTime = System.currentTimeMillis();
                    currentTimeMillis = 0;
                }
                this.mHandler.postDelayed(rendererCoreInfo.checkCache, rendererCoreInfo.cacheTime - currentTimeMillis);
                Log.d("RendererCoreCache", "checkCache resheduled: " + obj + " after " + (rendererCoreInfo.cacheTime - currentTimeMillis));
            }
        }
    }

    private RendererCoreInfo get(Object obj, Context context, long j, ResourceLoader resourceLoader, String str, OnCreateRootCallback onCreateRootCallback) {
        RendererCoreInfo rendererCoreInfo = get(obj, j);
        if (rendererCoreInfo != null) {
            return rendererCoreInfo;
        }
        ScreenElementRoot create = resourceLoader != null ? ScreenElementRootFactory.create(new ScreenElementRootFactory.Parameter(context, resourceLoader)) : ScreenElementRootFactory.create(new ScreenElementRootFactory.Parameter(context, str));
        if (create == null) {
            Log.e("RendererCoreCache", "fail to get RendererCoreInfo" + obj);
            return null;
        }
        if (onCreateRootCallback != null) {
            onCreateRootCallback.onCreateRoot(create);
        }
        create.setDefaultFramerate(0.0f);
        RendererCore rendererCore = create.load() ? new RendererCore(create) : null;
        RendererCoreInfo rendererCoreInfo2 = new RendererCoreInfo(rendererCore);
        rendererCoreInfo2.accessTime = Long.MAX_VALUE;
        rendererCoreInfo2.cacheTime = j;
        if (rendererCore != null) {
            rendererCore.setOnReleaseListener(this);
            rendererCoreInfo2.checkCache = new CheckCacheRunnable(obj);
        }
        this.mCaches.put(obj, rendererCoreInfo2);
        return rendererCoreInfo2;
    }

    @Override // com.miui.maml.RendererCore.OnReleaseListener
    public synchronized boolean OnRendererCoreReleased(RendererCore rendererCore) {
        Object next;
        RendererCoreInfo rendererCoreInfo;
        Log.d("RendererCoreCache", "OnRendererCoreReleased: " + rendererCore);
        Iterator<Object> it = this.mCaches.keySet().iterator();
        do {
            if (!it.hasNext()) {
                return false;
            }
            next = it.next();
            rendererCoreInfo = this.mCaches.get(next);
        } while (rendererCoreInfo.r != rendererCore);
        release(next);
        return rendererCoreInfo.cacheTime == 0;
    }

    public synchronized void clear() {
        this.mCaches.clear();
    }

    public synchronized RendererCoreInfo get(Object obj, long j) {
        RendererCoreInfo rendererCoreInfo = this.mCaches.get(obj);
        if (rendererCoreInfo != null) {
            rendererCoreInfo.accessTime = Long.MAX_VALUE;
            rendererCoreInfo.cacheTime = j;
            this.mHandler.removeCallbacks(rendererCoreInfo.checkCache);
            return rendererCoreInfo;
        }
        return null;
    }

    public synchronized RendererCoreInfo get(Object obj, Context context, long j, ResourceLoader resourceLoader, OnCreateRootCallback onCreateRootCallback) {
        return get(obj, context, j, resourceLoader, null, onCreateRootCallback);
    }

    public synchronized void release(Object obj) {
        Log.d("RendererCoreCache", "release: " + obj);
        RendererCoreInfo rendererCoreInfo = this.mCaches.get(obj);
        if (rendererCoreInfo != null) {
            rendererCoreInfo.accessTime = System.currentTimeMillis();
            if (rendererCoreInfo.cacheTime == 0) {
                this.mCaches.remove(obj);
                Log.d("RendererCoreCache", "removed: " + obj);
            } else {
                Log.d("RendererCoreCache", "scheduled release: " + obj + " after " + rendererCoreInfo.cacheTime);
                this.mHandler.removeCallbacks(rendererCoreInfo.checkCache);
                this.mHandler.postDelayed(rendererCoreInfo.checkCache, rendererCoreInfo.cacheTime);
            }
        }
    }
}
