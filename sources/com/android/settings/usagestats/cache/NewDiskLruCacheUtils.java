package com.android.settings.usagestats.cache;

import android.content.Context;
import android.util.Log;
import com.android.settings.usagestats.cache.DiskLruCache;
import com.android.settings.usagestats.utils.FileUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/* loaded from: classes2.dex */
public class NewDiskLruCacheUtils {
    public static NewDiskLruCacheUtils instance;
    private static DiskLruCache lruCache;

    private NewDiskLruCacheUtils(Context context) {
        try {
            lruCache = DiskLruCache.open(new File(FileUtils.getNewCacheDirPath(context)), 1, 1, 10485760L);
        } catch (IOException e) {
            Log.e("DiskLruCacheUtils", "DiskLruCacheUtils: openLruCacheError", e);
        }
    }

    public static synchronized NewDiskLruCacheUtils getInstance(Context context) {
        NewDiskLruCacheUtils newDiskLruCacheUtils;
        synchronized (NewDiskLruCacheUtils.class) {
            if (instance == null) {
                instance = new NewDiskLruCacheUtils(context.getApplicationContext());
            }
            newDiskLruCacheUtils = instance;
        }
        return newDiskLruCacheUtils;
    }

    public void close() {
        try {
            instance = null;
            DiskLruCache diskLruCache = lruCache;
            if (diskLruCache == null || diskLruCache.isClosed()) {
                return;
            }
            lruCache.close();
            lruCache = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DiskLruCache.Editor editor(String str) {
        try {
            DiskLruCache diskLruCache = lruCache;
            if (diskLruCache != null && !diskLruCache.isClosed()) {
                DiskLruCache.Editor edit = lruCache.edit(str);
                if (edit == null) {
                    Log.w("DiskLruCacheUtils", "the entry spcified key:" + str + " is editing by other . ");
                }
                return edit;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void flush() {
        try {
            DiskLruCache diskLruCache = lruCache;
            if (diskLruCache == null || diskLruCache.isClosed()) {
                return;
            }
            lruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream get(String str) {
        try {
            DiskLruCache diskLruCache = lruCache;
            if (diskLruCache != null && !diskLruCache.isClosed()) {
                DiskLruCache.Snapshot snapshot = lruCache.get(str);
                if (snapshot == null) {
                    Log.e("DiskLruCacheUtils", "not find entry , or entry.readable = false");
                    return null;
                }
                return snapshot.getInputStream(0);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getString(String str) {
        InputStream inputStream = get(str);
        String str2 = null;
        try {
            if (inputStream == null) {
                return null;
            }
            try {
                str2 = Util.readFully(new InputStreamReader(inputStream, Util.UTF_8));
                Log.d("DiskLruCacheUtils", "getString: readFroDiskSuccess");
            } catch (IOException e) {
                Log.e("DiskLruCacheUtils", "getString: readFroDiskFail", e);
            }
            return str2;
        } finally {
            FileUtils.closeIO(inputStream);
        }
    }

    public void putString(String str, String str2) {
        BufferedWriter bufferedWriter;
        DiskLruCache.Editor editor;
        BufferedWriter bufferedWriter2 = null;
        DiskLruCache.Editor editor2 = null;
        try {
            try {
                editor = editor(str);
            } catch (IOException e) {
                e = e;
                bufferedWriter = null;
            }
            if (editor == null) {
                FileUtils.closeIO(null);
                return;
            }
            try {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(editor.newOutputStream(0)));
            } catch (IOException e2) {
                e = e2;
                bufferedWriter = null;
            }
            try {
                try {
                    bufferedWriter.write(str2);
                    editor.commit();
                } catch (IOException e3) {
                    e = e3;
                    editor2 = editor;
                    Log.d("DiskLruCacheUtils", "putString: putStringError", e);
                    try {
                        editor2.abort();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                    FileUtils.closeIO(bufferedWriter);
                }
                FileUtils.closeIO(bufferedWriter);
            } catch (Throwable th) {
                th = th;
                bufferedWriter2 = bufferedWriter;
                FileUtils.closeIO(bufferedWriter2);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            FileUtils.closeIO(bufferedWriter2);
            throw th;
        }
    }
}
