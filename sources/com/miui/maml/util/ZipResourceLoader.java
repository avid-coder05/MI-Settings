package com.miui.maml.util;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* loaded from: classes2.dex */
public class ZipResourceLoader extends ResourceLoader {
    private String mInnerPath;
    private Object mLock;
    private String mResourcePath;
    private ZipFile mZipFile;

    public ZipResourceLoader(String str) {
        this(str, null, null);
    }

    public ZipResourceLoader(String str, String str2) {
        this(str, str2, null);
    }

    public ZipResourceLoader(String str, String str2, String str3) {
        this.mLock = new Object();
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("empty zip path");
        }
        this.mResourcePath = str;
        this.mInnerPath = str2 == null ? "" : str2;
        if (str3 != null) {
            this.mManifestName = str3;
        }
        init();
    }

    private void close() {
        synchronized (this.mLock) {
            ZipFile zipFile = this.mZipFile;
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException unused) {
                }
                this.mZipFile = null;
            }
        }
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override // com.miui.maml.ResourceLoader
    public void finish() {
        close();
        super.finish();
    }

    @Override // com.miui.maml.ResourceLoader
    public String getID() {
        return "ZipResourceLoader" + this.mResourcePath + this.mInnerPath;
    }

    @Override // com.miui.maml.ResourceLoader
    public InputStream getInputStream(String str, long[] jArr) {
        if (this.mZipFile == null || str == null) {
            return null;
        }
        synchronized (this.mLock) {
            ZipFile zipFile = this.mZipFile;
            if (zipFile != null) {
                ZipEntry entry = zipFile.getEntry(this.mInnerPath + str);
                if (entry == null) {
                    return null;
                }
                if (jArr != null) {
                    try {
                        jArr[0] = entry.getSize();
                    } catch (IOException e) {
                        Log.d("ZipResourceLoader", e.toString());
                    }
                }
                return this.mZipFile.getInputStream(entry);
            }
            return null;
        }
    }

    @Override // com.miui.maml.ResourceLoader
    public void init() {
        super.init();
        synchronized (this.mLock) {
            if (this.mZipFile == null) {
                try {
                    this.mZipFile = new ZipFile(this.mResourcePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ZipResourceLoader", "fail to init zip file: " + this.mResourcePath);
                }
            }
        }
    }

    @Override // com.miui.maml.ResourceLoader
    public boolean resourceExists(String str) {
        boolean z = false;
        if (this.mZipFile == null || str == null) {
            return false;
        }
        synchronized (this.mLock) {
            ZipFile zipFile = this.mZipFile;
            if (zipFile != null) {
                if (zipFile.getEntry(this.mInnerPath + str) != null) {
                    z = true;
                }
            }
        }
        return z;
    }
}
