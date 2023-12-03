package com.android.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ImageView;
import com.miui.maml.util.AppIconsHelper;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class ApkIconLoader implements Handler.Callback {
    private static final ConcurrentHashMap<String, ImageHolder> mImageCache = new ConcurrentHashMap<>();
    private final Context mContext;
    private LoaderThread mLoaderThread;
    private boolean mLoadingRequested;
    private boolean mPaused;
    private final ConcurrentHashMap<ImageView, FileId> mPendingRequests = new ConcurrentHashMap<>();
    private final Handler mMainThreadHandler = new Handler(this);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DrawableHolder extends ImageHolder {
        SoftReference<Drawable> drawableRef;

        private DrawableHolder() {
            super();
        }

        @Override // com.android.settings.ApkIconLoader.ImageHolder
        public boolean isNull() {
            return this.drawableRef == null;
        }

        @Override // com.android.settings.ApkIconLoader.ImageHolder
        public void setImage(Object obj) {
            this.drawableRef = obj == null ? null : new SoftReference<>((Drawable) obj);
        }

        @Override // com.android.settings.ApkIconLoader.ImageHolder
        public boolean setImageView(ImageView imageView) {
            if (this.drawableRef.get() == null) {
                return false;
            }
            imageView.setImageDrawable(this.drawableRef.get());
            return true;
        }
    }

    /* loaded from: classes.dex */
    public static class FileId {
        public String mPkgName;

        public FileId(String str) {
            this.mPkgName = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static abstract class ImageHolder {
        int state;

        private ImageHolder() {
        }

        public static ImageHolder create() {
            return new DrawableHolder();
        }

        public abstract boolean isNull();

        public abstract void setImage(Object obj);

        public abstract boolean setImageView(ImageView imageView);
    }

    /* loaded from: classes.dex */
    private class LoaderThread extends HandlerThread implements Handler.Callback {
        private Handler mLoaderThreadHandler;

        public LoaderThread() {
            super("FileIconLoader");
        }

        private Drawable getApkIcon(Context context, String str) {
            PackageManager packageManager = context.getPackageManager();
            try {
                return AppIconsHelper.getIconDrawable(context, packageManager.getApplicationInfo(str, 0), packageManager, 60000L);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            for (FileId fileId : ApkIconLoader.this.mPendingRequests.values()) {
                ImageHolder imageHolder = (ImageHolder) ApkIconLoader.mImageCache.get(fileId.mPkgName);
                if (imageHolder != null && imageHolder.state == 0) {
                    imageHolder.state = 1;
                    imageHolder.setImage(getApkIcon(ApkIconLoader.this.mContext, fileId.mPkgName));
                    imageHolder.state = 2;
                    ApkIconLoader.mImageCache.put(fileId.mPkgName, imageHolder);
                }
            }
            ApkIconLoader.this.mMainThreadHandler.sendEmptyMessage(2);
            return true;
        }

        public void requestLoading() {
            if (this.mLoaderThreadHandler == null) {
                this.mLoaderThreadHandler = new Handler(getLooper(), this);
            }
            this.mLoaderThreadHandler.sendEmptyMessage(0);
        }
    }

    public ApkIconLoader(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private boolean loadCachedIcon(ImageView imageView, String str) {
        ConcurrentHashMap<String, ImageHolder> concurrentHashMap = mImageCache;
        ImageHolder imageHolder = concurrentHashMap.get(str);
        if (imageHolder == null) {
            imageHolder = ImageHolder.create();
            concurrentHashMap.put(str, imageHolder);
        } else if (imageHolder.state == 2 && (imageHolder.isNull() || imageHolder.setImageView(imageView))) {
            return true;
        }
        imageHolder.state = 0;
        return false;
    }

    private void processLoadedIcons() {
        Iterator<ImageView> it = this.mPendingRequests.keySet().iterator();
        while (it.hasNext()) {
            ImageView next = it.next();
            if (loadCachedIcon(next, this.mPendingRequests.get(next).mPkgName)) {
                it.remove();
            }
        }
        if (this.mPendingRequests.isEmpty()) {
            return;
        }
        requestLoading();
    }

    private void requestLoading() {
        if (this.mLoadingRequested) {
            return;
        }
        this.mLoadingRequested = true;
        this.mMainThreadHandler.sendEmptyMessage(1);
    }

    public void clear() {
        this.mPendingRequests.clear();
        mImageCache.clear();
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        int i = message.what;
        if (i != 1) {
            if (i != 2) {
                return false;
            }
            if (!this.mPaused) {
                processLoadedIcons();
            }
            return true;
        }
        this.mLoadingRequested = false;
        if (!this.mPaused) {
            if (this.mLoaderThread == null) {
                LoaderThread loaderThread = new LoaderThread();
                this.mLoaderThread = loaderThread;
                loaderThread.start();
            }
            this.mLoaderThread.requestLoading();
        }
        return true;
    }

    public boolean loadIcon(ImageView imageView, String str) {
        boolean loadCachedIcon = loadCachedIcon(imageView, str);
        if (loadCachedIcon) {
            this.mPendingRequests.remove(imageView);
        } else {
            this.mPendingRequests.put(imageView, new FileId(str));
            if (!this.mPaused) {
                requestLoading();
            }
        }
        return loadCachedIcon;
    }

    public void pause() {
        this.mPaused = true;
    }

    public void stop() {
        pause();
        LoaderThread loaderThread = this.mLoaderThread;
        if (loaderThread != null) {
            loaderThread.quit();
            this.mLoaderThread = null;
        }
        clear();
    }
}
