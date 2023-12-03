package com.miui.maml.util;

import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaDataSource;
import android.os.AsyncTask;
import android.os.Build;
import android.os.MemoryFile;
import android.text.TextUtils;
import com.miui.maml.ResourceManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

/* loaded from: classes2.dex */
public class MamlMediaDataSource extends MediaDataSource {
    private Context mContext;
    private long mCurrentPosition;
    private MemoryFile mFile;
    private ResourceManager mManager;
    private String mPath;
    private long mSize;
    private InputStream mStream;
    private boolean mSupportMark;
    private final Object mLock = new Object();
    private ComponentCallbacks2 mComponentCallback = new ComponentCallbacks2() { // from class: com.miui.maml.util.MamlMediaDataSource.1
        @Override // android.content.ComponentCallbacks
        public void onConfigurationChanged(Configuration configuration) {
        }

        @Override // android.content.ComponentCallbacks
        public void onLowMemory() {
            if (Build.VERSION.SDK_INT < 23) {
                return;
            }
            onLowMemory();
        }

        @Override // android.content.ComponentCallbacks2
        public void onTrimMemory(int i) {
            if (Build.VERSION.SDK_INT < 23) {
                return;
            }
            onTrimMemory(i);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class MemoryFileAsyncTask extends AsyncTask<Void, Void, Void> {
        private String mFilePath;
        private ResourceManager mManager;
        private MamlMediaDataSource mSource;

        public MemoryFileAsyncTask(String str, ResourceManager resourceManager, MamlMediaDataSource mamlMediaDataSource) {
            this.mFilePath = str;
            this.mManager = resourceManager;
            this.mSource = mamlMediaDataSource;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            MamlMediaDataSource mamlMediaDataSource;
            ResourceManager resourceManager = this.mManager;
            if (resourceManager == null || (mamlMediaDataSource = this.mSource) == null) {
                return null;
            }
            mamlMediaDataSource.setMemoryFile(resourceManager.getFile(this.mFilePath));
            return null;
        }
    }

    public MamlMediaDataSource(Context context, ResourceManager resourceManager, String str) {
        this.mContext = context;
        this.mManager = resourceManager;
        this.mPath = str;
        init();
        try {
            this.mContext.unregisterComponentCallbacks(this.mComponentCallback);
        } catch (Exception unused) {
        }
    }

    private void closeStream() {
        synchronized (this.mLock) {
            InputStream inputStream = this.mStream;
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.mStream = null;
            }
        }
    }

    private void generateMemoryFile() {
        if (this.mFile != null) {
            return;
        }
        new MemoryFileAsyncTask(this.mPath, this.mManager, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void init() {
        if (TextUtils.isEmpty(this.mPath)) {
            return;
        }
        synchronized (this.mLock) {
            long[] jArr = new long[1];
            InputStream inputStream = this.mManager.getInputStream(this.mPath, jArr);
            this.mStream = inputStream;
            this.mSize = jArr[0];
            if (inputStream != null) {
                boolean markSupported = inputStream.markSupported();
                this.mSupportMark = markSupported;
                if (markSupported) {
                    this.mStream.mark(Integer.MAX_VALUE);
                }
                tryToGenerateMemoryFile();
            }
        }
    }

    private boolean isFileSizeValid() {
        return this.mSize < 52428800;
    }

    private boolean isMemoryEnough() {
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService("activity");
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return !memoryInfo.lowMemory && memoryInfo.availMem - memoryInfo.threshold > 104857600;
    }

    private boolean resetStream() {
        InputStream inputStream = this.mStream;
        if (inputStream == null) {
            return false;
        }
        if (this.mSupportMark) {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                inputStream.close();
                this.mStream = this.mManager.getInputStream(this.mPath, new long[1]);
            } catch (IOException e2) {
                e2.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMemoryFile(MemoryFile memoryFile) {
        synchronized (this.mLock) {
            this.mFile = memoryFile;
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        closeStream();
        releaseMemoryFile();
        try {
            this.mContext.unregisterComponentCallbacks(this.mComponentCallback);
        } catch (Exception unused) {
        }
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public String getPath() {
        return this.mPath;
    }

    @Override // android.media.MediaDataSource
    public long getSize() {
        return this.mSize;
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x007d  */
    @Override // android.media.MediaDataSource
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int readAt(long r8, byte[] r10, int r11, int r12) {
        /*
            r7 = this;
            android.os.MemoryFile r0 = r7.mFile
            if (r0 != 0) goto Lb
            java.io.InputStream r0 = r7.mStream
            if (r0 != 0) goto Lb
            r7.init()
        Lb:
            java.lang.Object r0 = r7.mLock
            monitor-enter(r0)
            android.os.MemoryFile r1 = r7.mFile     // Catch: java.lang.Throwable -> L82
            r2 = 0
            if (r1 == 0) goto L4e
            long r3 = (long) r12
            long r3 = r3 + r8
            long r5 = r7.mSize     // Catch: java.lang.Exception -> L3e java.nio.BufferUnderflowException -> L4c java.lang.Throwable -> L82
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L36
            int r12 = (r8 > r5 ? 1 : (r8 == r5 ? 0 : -1))
            if (r12 >= 0) goto L2c
            java.lang.String r12 = "MamlMediaDataSource"
            java.lang.String r1 = "readAt: position+size is larger than file size, read left data"
            android.util.Log.w(r12, r1)     // Catch: java.lang.Exception -> L3e java.nio.BufferUnderflowException -> L4c java.lang.Throwable -> L82
            long r3 = r7.mSize     // Catch: java.lang.Exception -> L3e java.nio.BufferUnderflowException -> L4c java.lang.Throwable -> L82
            long r3 = r3 - r8
            int r12 = (int) r3     // Catch: java.lang.Exception -> L3e java.nio.BufferUnderflowException -> L4c java.lang.Throwable -> L82
            goto L36
        L2c:
            java.lang.String r8 = "MamlMediaDataSource"
            java.lang.String r9 = "readAt: position is larger than file size, return 0"
            android.util.Log.w(r8, r9)     // Catch: java.lang.Exception -> L3e java.nio.BufferUnderflowException -> L4c java.lang.Throwable -> L82
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L82
            return r2
        L36:
            android.os.MemoryFile r1 = r7.mFile     // Catch: java.lang.Exception -> L3e java.nio.BufferUnderflowException -> L4c java.lang.Throwable -> L82
            int r3 = (int) r8     // Catch: java.lang.Exception -> L3e java.nio.BufferUnderflowException -> L4c java.lang.Throwable -> L82
            int r2 = r1.readBytes(r10, r3, r11, r12)     // Catch: java.lang.Exception -> L3e java.nio.BufferUnderflowException -> L4c java.lang.Throwable -> L82
            goto L7a
        L3e:
            r8 = move-exception
            r8.printStackTrace()     // Catch: java.lang.Throwable -> L82
            android.os.MemoryFile r8 = r7.mFile     // Catch: java.lang.Throwable -> L82
            r8.close()     // Catch: java.lang.Throwable -> L82
            r8 = 0
            r7.mFile = r8     // Catch: java.lang.Throwable -> L82
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L82
            return r2
        L4c:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L82
            return r2
        L4e:
            java.io.InputStream r1 = r7.mStream     // Catch: java.lang.Throwable -> L82
            if (r1 == 0) goto L7a
            long r3 = r7.mCurrentPosition     // Catch: java.lang.Throwable -> L82
            int r1 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r1 <= 0) goto L62
            boolean r1 = r7.resetStream()     // Catch: java.lang.Throwable -> L82
            if (r1 == 0) goto L60
            r3 = r8
            goto L64
        L60:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L82
            return r2
        L62:
            long r3 = r8 - r3
        L64:
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 == 0) goto L6f
            java.io.InputStream r1 = r7.mStream     // Catch: java.lang.Exception -> L76 java.lang.Throwable -> L82
            r1.skip(r3)     // Catch: java.lang.Exception -> L76 java.lang.Throwable -> L82
        L6f:
            java.io.InputStream r1 = r7.mStream     // Catch: java.lang.Exception -> L76 java.lang.Throwable -> L82
            int r2 = r1.read(r10, r11, r12)     // Catch: java.lang.Exception -> L76 java.lang.Throwable -> L82
            goto L7a
        L76:
            r10 = move-exception
            r10.printStackTrace()     // Catch: java.lang.Throwable -> L82
        L7a:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L82
            if (r2 <= 0) goto L81
            long r10 = (long) r2
            long r8 = r8 + r10
            r7.mCurrentPosition = r8
        L81:
            return r2
        L82:
            r7 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L82
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.MamlMediaDataSource.readAt(long, byte[], int, int):int");
    }

    public void releaseMemoryFile() {
        synchronized (this.mLock) {
            MemoryFile memoryFile = this.mFile;
            if (memoryFile != null) {
                memoryFile.close();
                this.mFile = null;
            }
        }
    }

    public void tryToGenerateMemoryFile() {
        if (this.mFile == null && (this.mStream instanceof InflaterInputStream) && isMemoryEnough() && isFileSizeValid()) {
            generateMemoryFile();
        }
    }
}
