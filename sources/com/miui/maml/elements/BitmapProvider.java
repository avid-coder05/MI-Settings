package com.miui.maml.elements;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ObjectFactory;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.util.Utils;
import com.miui.maml.util.net.IOUtils;
import java.io.File;
import java.io.InputStream;
import java.net.URI;

/* loaded from: classes2.dex */
public abstract class BitmapProvider {
    protected ScreenElementRoot mRoot;
    protected VersionedBitmap mVersionedBitmap = new VersionedBitmap(null);

    /* loaded from: classes2.dex */
    private static class AppIconProvider extends BitmapProvider {
        private String mCls;
        private boolean mNoIcon;
        private String mPkg;
        private String mSrc;

        public AppIconProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        private void parseSrc(String str) {
            this.mNoIcon = false;
            this.mVersionedBitmap.mBitmap = null;
            if (TextUtils.isEmpty(str)) {
                Log.e("BitmapProvider", "invalid src of ApplicationIcon type: " + str);
                this.mNoIcon = true;
                return;
            }
            String[] split = str.split(",");
            if (split.length == 2) {
                this.mPkg = split[0];
                this.mCls = split[1];
            } else if (split.length == 1) {
                this.mPkg = split[0];
            } else {
                Log.e("BitmapProvider", "invalid src of ApplicationIcon type: " + str);
                this.mNoIcon = true;
            }
        }

        private void tryToSetBitmap() {
            try {
                Drawable activityIcon = this.mCls != null ? this.mRoot.getContext().mContext.getPackageManager().getActivityIcon(new ComponentName(this.mPkg, this.mCls)) : this.mRoot.getContext().mContext.getPackageManager().getApplicationIcon(this.mPkg);
                if (activityIcon instanceof BitmapDrawable) {
                    this.mVersionedBitmap.setBitmap(((BitmapDrawable) activityIcon).getBitmap());
                    return;
                }
                int intrinsicWidth = activityIcon.getIntrinsicWidth();
                int intrinsicHeight = activityIcon.getIntrinsicHeight();
                Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, activityIcon.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(createBitmap);
                activityIcon.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
                activityIcon.draw(canvas);
                this.mVersionedBitmap.setBitmap(createBitmap);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("BitmapProvider", "fail to get icon for src of ApplicationIcon type: " + this.mSrc);
                this.mNoIcon = true;
            }
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            if (!TextUtils.equals(str, this.mSrc)) {
                this.mSrc = str;
                parseSrc(str);
            }
            if (this.mVersionedBitmap.getBitmap() == null && !this.mNoIcon) {
                tryToSetBitmap();
            }
            return this.mVersionedBitmap;
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public void init(String str) {
            super.init(str);
            this.mSrc = str;
            parseSrc(str);
        }
    }

    /* loaded from: classes2.dex */
    public static class BitmapHolderProvider extends BitmapProvider {
        private IBitmapHolder mBitmapHolder;
        private String mId;

        public BitmapHolderProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            IBitmapHolder iBitmapHolder = this.mBitmapHolder;
            if (iBitmapHolder != null) {
                return iBitmapHolder.getBitmap(this.mId);
            }
            return null;
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public void init(String str) {
            super.init(str);
            if (TextUtils.isEmpty(str)) {
                return;
            }
            int indexOf = str.indexOf(46);
            if (indexOf != -1) {
                String substring = str.substring(0, indexOf);
                this.mId = str.substring(indexOf + 1);
                str = substring;
            }
            ScreenElement findElement = this.mRoot.findElement(str);
            if (findElement instanceof IBitmapHolder) {
                this.mBitmapHolder = (IBitmapHolder) findElement;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class BitmapVariableProvider extends BitmapProvider {
        private String mCurSrc;
        private Expression mIndexExpression;
        private IndexedVariable mVar;

        public BitmapVariableProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            int i3;
            Bitmap bitmap = null;
            if (!Utils.equals(this.mCurSrc, str)) {
                this.mVar = null;
                this.mIndexExpression = null;
                if (!TextUtils.isEmpty(str)) {
                    int indexOf = str.indexOf(91);
                    int length = str.length();
                    if (indexOf != -1 && indexOf < length - 1 && str.charAt(i3) == ']') {
                        this.mIndexExpression = Expression.build(this.mRoot.getVariables(), str.substring(indexOf + 1, i3));
                    }
                    this.mVar = new IndexedVariable(this.mIndexExpression == null ? str : str.substring(0, indexOf), this.mRoot.getVariables(), false);
                }
                this.mCurSrc = str;
            }
            try {
                IndexedVariable indexedVariable = this.mVar;
                if (indexedVariable != null) {
                    Expression expression = this.mIndexExpression;
                    bitmap = expression != null ? (Bitmap) indexedVariable.getArr((int) expression.evaluate()) : (Bitmap) indexedVariable.get();
                }
            } catch (ClassCastException unused) {
                Log.w("BitmapProvider", "fail to cast as Bitmap from object: " + str);
            }
            this.mVersionedBitmap.setBitmap(bitmap);
            return this.mVersionedBitmap;
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public void init(String str) {
            super.init(str);
            if (TextUtils.isEmpty(str)) {
                return;
            }
            this.mVar = new IndexedVariable(str, this.mRoot.getVariables(), false);
            this.mCurSrc = str;
        }
    }

    /* loaded from: classes2.dex */
    private static class FileSystemProvider extends UriProvider {
        public FileSystemProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        @Override // com.miui.maml.elements.BitmapProvider.UriProvider, com.miui.maml.elements.BitmapProvider
        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            if (TextUtils.isEmpty(str)) {
                this.mVersionedBitmap.setBitmap(null);
                return this.mVersionedBitmap;
            }
            URI uri = new File(str).toURI();
            if (uri == null) {
                this.mVersionedBitmap.setBitmap(null);
                return this.mVersionedBitmap;
            }
            return super.getBitmap(uri.toString(), z, i, i2);
        }
    }

    /* loaded from: classes2.dex */
    public interface IBitmapHolder {
        VersionedBitmap getBitmap(String str);
    }

    /* loaded from: classes2.dex */
    private static class ResourceImageProvider extends BitmapProvider {
        private ResourceManager.AsyncLoadListener mAsyncLoadListener;
        private String mCachedBitmapName;
        String mLoadingBitmapName;
        Object mSrcNameLock;

        public ResourceImageProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
            this.mSrcNameLock = new Object();
            this.mAsyncLoadListener = new ResourceManager.AsyncLoadListener() { // from class: com.miui.maml.elements.BitmapProvider.ResourceImageProvider.1
                @Override // com.miui.maml.ResourceManager.AsyncLoadListener
                public void onLoadComplete(String str, ResourceManager.BitmapInfo bitmapInfo) {
                    synchronized (ResourceImageProvider.this.mSrcNameLock) {
                        if (TextUtils.equals(str, ResourceImageProvider.this.mLoadingBitmapName)) {
                            Log.i("BitmapProvider", "load image async complete: " + str + " last cached " + ResourceImageProvider.this.mCachedBitmapName);
                            ResourceImageProvider.this.mVersionedBitmap.setBitmap(bitmapInfo == null ? null : bitmapInfo.mBitmap);
                            ResourceImageProvider.this.mCachedBitmapName = str;
                            ResourceImageProvider.this.mLoadingBitmapName = null;
                        } else {
                            Log.i("BitmapProvider", "load image async complete: " + str + " not equals " + ResourceImageProvider.this.mLoadingBitmapName);
                        }
                    }
                    ResourceImageProvider.this.mRoot.requestUpdate();
                }
            };
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public void finish() {
            super.finish();
            synchronized (this.mSrcNameLock) {
                this.mLoadingBitmapName = null;
                this.mCachedBitmapName = null;
                this.mVersionedBitmap.reset();
            }
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            Bitmap bitmap = this.mVersionedBitmap.getBitmap();
            if ((bitmap != null && bitmap.isRecycled()) || !TextUtils.equals(this.mCachedBitmapName, str)) {
                if (z) {
                    ResourceManager.BitmapInfo bitmapInfo = this.mRoot.getContext().mResourceManager.getBitmapInfo(str);
                    this.mVersionedBitmap.setBitmap(bitmapInfo != null ? bitmapInfo.mBitmap : null);
                    this.mCachedBitmapName = str;
                } else {
                    ResourceManager.BitmapInfo bitmapInfoAsync = this.mRoot.getContext().mResourceManager.getBitmapInfoAsync(str, this.mAsyncLoadListener);
                    synchronized (this.mSrcNameLock) {
                        if (bitmapInfoAsync != null) {
                            if (bitmapInfoAsync.mLoading) {
                                this.mLoadingBitmapName = str;
                            }
                        }
                        this.mVersionedBitmap.setBitmap(bitmapInfoAsync == null ? null : bitmapInfoAsync.mBitmap);
                        this.mCachedBitmapName = str;
                        this.mLoadingBitmapName = null;
                    }
                }
            }
            return this.mVersionedBitmap;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class UriProvider extends BitmapProvider {
        private String mCachedBitmapUri;
        private String mCurLoadingBitmapUri;
        private Object mLock;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public class LoaderAsyncTask extends AsyncTask<Object, Object, Bitmap> {
            private int mHeight;
            private String mUri;
            private int mWidth;

            public LoaderAsyncTask(String str, int i, int i2) {
                this.mUri = null;
                this.mWidth = -1;
                this.mHeight = -1;
                this.mUri = str;
                this.mWidth = i;
                this.mHeight = i2;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.AsyncTask
            public Bitmap doInBackground(Object... objArr) {
                Bitmap bitmapFromUri = UriProvider.this.getBitmapFromUri(Uri.parse(this.mUri), this.mWidth, this.mHeight);
                if (bitmapFromUri == null) {
                    Log.w("BitmapProvider", "fail to decode bitmap: " + this.mUri);
                }
                synchronized (UriProvider.this.mLock) {
                    if (TextUtils.equals(this.mUri, UriProvider.this.mCurLoadingBitmapUri)) {
                        UriProvider.this.mVersionedBitmap.setBitmap(bitmapFromUri);
                        UriProvider uriProvider = UriProvider.this;
                        uriProvider.mCachedBitmapUri = uriProvider.mCurLoadingBitmapUri;
                        UriProvider.this.mRoot.requestUpdate();
                        UriProvider.this.mCurLoadingBitmapUri = null;
                    }
                }
                return bitmapFromUri;
            }
        }

        public UriProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
            this.mLock = new Object();
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public void finish() {
            super.finish();
            synchronized (this.mLock) {
                this.mCachedBitmapUri = null;
                this.mCurLoadingBitmapUri = null;
                this.mVersionedBitmap.reset();
            }
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            if (TextUtils.isEmpty(str)) {
                this.mVersionedBitmap.setBitmap(null);
                return this.mVersionedBitmap;
            }
            Bitmap bitmap = this.mVersionedBitmap.getBitmap();
            if ((bitmap != null && bitmap.isRecycled()) || !TextUtils.equals(this.mCachedBitmapUri, str)) {
                synchronized (this.mLock) {
                    if (!TextUtils.equals(this.mCurLoadingBitmapUri, str) && !TextUtils.equals(this.mCachedBitmapUri, str)) {
                        this.mCurLoadingBitmapUri = str;
                        new LoaderAsyncTask(str, i, i2).execute(new Object[0]);
                    }
                }
            }
            this.mVersionedBitmap.setBitmap(bitmap);
            return this.mVersionedBitmap;
        }
    }

    /* loaded from: classes2.dex */
    public static class VersionedBitmap {
        private Bitmap mBitmap;
        private int mVersion;

        public VersionedBitmap(Bitmap bitmap) {
            this.mBitmap = bitmap;
        }

        public static boolean equals(VersionedBitmap versionedBitmap, VersionedBitmap versionedBitmap2) {
            return versionedBitmap != null && versionedBitmap2 != null && versionedBitmap.mBitmap == versionedBitmap2.mBitmap && versionedBitmap.mVersion == versionedBitmap2.mVersion;
        }

        public Bitmap getBitmap() {
            return this.mBitmap;
        }

        public void reset() {
            this.mBitmap = null;
            this.mVersion = 0;
        }

        public void set(VersionedBitmap versionedBitmap) {
            if (versionedBitmap == null) {
                reset();
                return;
            }
            this.mBitmap = versionedBitmap.mBitmap;
            this.mVersion = versionedBitmap.mVersion;
        }

        public boolean setBitmap(Bitmap bitmap) {
            if (bitmap != this.mBitmap) {
                this.mBitmap = bitmap;
                this.mVersion++;
            }
            return bitmap != this.mBitmap;
        }

        public int updateVersion() {
            int i = this.mVersion;
            this.mVersion = i + 1;
            return i;
        }
    }

    /* loaded from: classes2.dex */
    private static class VirtualScreenProvider extends BitmapProvider {
        private VirtualScreen mVirtualScreen;

        public VirtualScreenProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            VersionedBitmap versionedBitmap = this.mVersionedBitmap;
            VirtualScreen virtualScreen = this.mVirtualScreen;
            versionedBitmap.setBitmap(virtualScreen != null ? virtualScreen.getBitmap() : null);
            return this.mVersionedBitmap;
        }

        @Override // com.miui.maml.elements.BitmapProvider
        public void init(String str) {
            super.init(str);
            ScreenElement findElement = this.mRoot.findElement(str);
            if (findElement instanceof VirtualScreen) {
                this.mVirtualScreen = (VirtualScreen) findElement;
            }
        }
    }

    public BitmapProvider(ScreenElementRoot screenElementRoot) {
        this.mRoot = screenElementRoot;
    }

    private static int computeSampleSize(BitmapFactory.Options options, int i) {
        int i2 = 1;
        while (true) {
            int i3 = i2 * 2;
            if (i3 > Math.sqrt((options.outHeight * options.outWidth) / i)) {
                return i2;
            }
            i2 = i3;
        }
    }

    public static BitmapProvider create(ScreenElementRoot screenElementRoot, String str) {
        BitmapProvider create;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1931592872:
                if (str.equals("BitmapVar")) {
                    c = 0;
                    break;
                }
                break;
            case -499376165:
                if (str.equals("BitmapHolder")) {
                    c = 1;
                    break;
                }
                break;
            case -495181077:
                if (str.equals("FileSystem")) {
                    c = 2;
                    break;
                }
                break;
            case 85324:
                if (str.equals("Uri")) {
                    c = 3;
                    break;
                }
                break;
            case 1258571575:
                if (str.equals("VirtualScreen")) {
                    c = 4;
                    break;
                }
                break;
            case 1758035405:
                if (str.equals("ResourceImage")) {
                    c = 5;
                    break;
                }
                break;
            case 2050377161:
                if (str.equals("ApplicationIcon")) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return new BitmapVariableProvider(screenElementRoot);
            case 1:
                return new BitmapHolderProvider(screenElementRoot);
            case 2:
                return new FileSystemProvider(screenElementRoot);
            case 3:
                return new UriProvider(screenElementRoot);
            case 4:
                return new VirtualScreenProvider(screenElementRoot);
            case 5:
                return new ResourceImageProvider(screenElementRoot);
            case 6:
                return new AppIconProvider(screenElementRoot);
            default:
                ObjectFactory.BitmapProviderFactory bitmapProviderFactory = (ObjectFactory.BitmapProviderFactory) screenElementRoot.getContext().getObjectFactory("BitmapProvider");
                return (bitmapProviderFactory == null || (create = bitmapProviderFactory.create(screenElementRoot, str)) == null) ? new ResourceImageProvider(screenElementRoot) : create;
        }
    }

    public void finish() {
        this.mVersionedBitmap.reset();
    }

    public abstract VersionedBitmap getBitmap(String str, boolean z, int i, int i2);

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.io.InputStream] */
    protected Bitmap getBitmapFromUri(Uri uri, int i, int i2) {
        ?? r4;
        InputStream inputStream;
        InputStream inputStream2;
        InputStream inputStream3 = null;
        try {
            try {
                inputStream2 = this.mRoot.getContext().mContext.getContentResolver().openInputStream(uri);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
            inputStream = null;
            inputStream2 = null;
        } catch (Throwable th2) {
            th = th2;
            r4 = 0;
            IOUtils.closeQuietly(inputStream3);
            IOUtils.closeQuietly(r4);
            throw th;
        }
        try {
            if (i <= 0 || i2 <= 0) {
                Bitmap decodeStream = BitmapFactory.decodeStream(inputStream2, null, null);
                IOUtils.closeQuietly(inputStream2);
                IOUtils.closeQuietly(null);
                return decodeStream;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream2, null, options);
            options.inSampleSize = computeSampleSize(options, i * i2);
            options.inJustDecodeBounds = false;
            options.outHeight = i2;
            options.outWidth = i;
            inputStream = this.mRoot.getContext().mContext.getContentResolver().openInputStream(uri);
            try {
                Bitmap decodeStream2 = BitmapFactory.decodeStream(inputStream, null, options);
                IOUtils.closeQuietly(inputStream2);
                IOUtils.closeQuietly(inputStream);
                return decodeStream2;
            } catch (Exception e2) {
                e = e2;
                Log.d("BitmapProvider", "getBitmapFromUri Exception", e);
                IOUtils.closeQuietly(inputStream2);
                IOUtils.closeQuietly(inputStream);
                return null;
            }
        } catch (Exception e3) {
            e = e3;
            inputStream = null;
        } catch (Throwable th3) {
            th = th3;
            this = null;
            inputStream3 = inputStream2;
            r4 = this;
            IOUtils.closeQuietly(inputStream3);
            IOUtils.closeQuietly(r4);
            throw th;
        }
    }

    public void init(String str) {
        reset();
    }

    public void reset() {
    }
}
