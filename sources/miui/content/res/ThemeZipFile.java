package miui.content.res;

import android.content.res.MiuiResources;
import android.util.Log;
import android.util.MiuiDisplayMetrics;
import com.miui.internal.content.res.ThemeDensityFallbackUtils;
import com.miui.internal.content.res.ThemeToolUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import miui.content.res.ThemeResources;

/* loaded from: classes3.dex */
public final class ThemeZipFile {
    static boolean DBG = ThemeResources.DBG;
    private static final String FUZZY_SEARCH_ICON_SUFFIX = "#*.png";
    static String TAG = "ThemeZipFile";
    public static final String THEME_FALLBACK_FILE = "theme_fallback.xml";
    public static final String THEME_VALUE_FILE = "theme_values.xml";
    public static final String THEME_VALUE_FILE_NAME = "theme_values";
    public static final String THEME_VALUE_FILE_SUFFIX = ".xml";
    private static final int sDensity;
    private static final int[] sFallbackDensities;
    protected static final Map<String, WeakReference<ThemeZipFile>> sThemeZipFiles;
    private volatile long mLastModifiedTime = -1;
    private ThemeResources.MetaData mMetaData;
    private String mPath;
    private long mUpatedTime;
    private MyZipFile mZipFile;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class MyZipFile extends ZipFile {
        private static final String MY_ZIP_FILE_INIT_THREAD = "MyZipFileInitThread";
        private volatile HashMap<String, ZipEntry> mEntryCache;
        private final Object mMutex;

        public MyZipFile(File file) throws ZipException, IOException {
            super(file);
            this.mMutex = new Object();
            new Thread(new Runnable() { // from class: miui.content.res.ThemeZipFile.MyZipFile.1
                @Override // java.lang.Runnable
                public void run() {
                    MyZipFile.this.initCache();
                }
            }, MY_ZIP_FILE_INIT_THREAD).start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initCache() {
            if (this.mEntryCache == null) {
                synchronized (this.mMutex) {
                    if (this.mEntryCache == null) {
                        HashMap<String, ZipEntry> hashMap = new HashMap<>(size());
                        Enumeration<? extends ZipEntry> entries = entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry nextElement = entries.nextElement();
                            if (!nextElement.isDirectory()) {
                                hashMap.put(nextElement.getName(), nextElement);
                            }
                        }
                        this.mEntryCache = hashMap;
                    }
                }
            }
        }

        @Override // java.util.zip.ZipFile
        public ZipEntry getEntry(String str) {
            return this.mEntryCache != null ? this.mEntryCache.get(str) : super.getEntry(str);
        }
    }

    static {
        int i = MiuiDisplayMetrics.DENSITY_DEVICE;
        sDensity = i;
        sFallbackDensities = ThemeDensityFallbackUtils.getFallbackOrder(i);
        sThemeZipFiles = new HashMap();
    }

    ThemeZipFile(String str, ThemeResources.MetaData metaData) {
        if (DBG) {
            Log.d(TAG, "create ThemeZipFile for " + str);
        }
        this.mPath = str;
        this.mMetaData = metaData;
    }

    private void clean() {
        if (DBG) {
            Log.d(TAG, "clean for " + this.mPath);
        }
        MyZipFile myZipFile = this.mZipFile;
        if (myZipFile != null) {
            try {
                myZipFile.close();
            } catch (Exception unused) {
            }
            this.mZipFile = null;
        }
    }

    private boolean getThemeFileInner(MiuiResources.ThemeFileInfoOption themeFileInfoOption, String str) {
        boolean z = true;
        if (getZipInputStream(themeFileInfoOption, str)) {
            int i = themeFileInfoOption.inDensity;
            themeFileInfoOption.outDensity = i != 0 ? i : 160;
            return true;
        }
        String str2 = "/drawable";
        int indexOf = str.indexOf("/drawable");
        if (indexOf < 0) {
            str2 = "/raw";
            indexOf = str.indexOf("/raw");
        }
        if (indexOf <= 0) {
            return false;
        }
        int length = indexOf + str2.length();
        String regularDpiFallbackPath = regularDpiFallbackPath(str, length);
        if (str != regularDpiFallbackPath) {
            if (getZipInputStream(themeFileInfoOption, regularDpiFallbackPath)) {
                int i2 = themeFileInfoOption.inDensity;
                themeFileInfoOption.outDensity = i2 != 0 ? i2 : 160;
                return true;
            }
            str = regularDpiFallbackPath;
        }
        int indexOf2 = str.indexOf(47, length);
        if (indexOf2 < 0) {
            return false;
        }
        FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
        buffer.assign(str, length);
        int[] iArr = sFallbackDensities;
        int length2 = iArr.length;
        int i3 = 0;
        while (true) {
            if (i3 >= length2) {
                z = false;
                break;
            }
            int i4 = iArr[i3];
            if (i4 != themeFileInfoOption.inDensity) {
                buffer.setLength(length);
                buffer.append(ThemeDensityFallbackUtils.getDensitySuffix(i4));
                buffer.append(str, indexOf2, str.length());
                if (getZipInputStream(themeFileInfoOption, buffer.toString())) {
                    themeFileInfoOption.outDensity = i4 != 0 ? i4 : 160;
                }
            }
            i3++;
        }
        FixedSizeStringBuffer.freeBuffer(buffer);
        return z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ThemeZipFile getThemeZipFile(ThemeResources.MetaData metaData, String str) {
        String str2 = metaData.mThemePath + str;
        Map<String, WeakReference<ThemeZipFile>> map = sThemeZipFiles;
        WeakReference<ThemeZipFile> weakReference = map.get(str2);
        ThemeZipFile themeZipFile = weakReference != null ? weakReference.get() : null;
        if (themeZipFile == null) {
            synchronized (map) {
                WeakReference<ThemeZipFile> weakReference2 = map.get(str2);
                ThemeZipFile themeZipFile2 = weakReference2 != null ? weakReference2.get() : null;
                if (themeZipFile2 == null) {
                    themeZipFile = new ThemeZipFile(str2, metaData);
                    map.put(str2, new WeakReference<>(themeZipFile));
                } else {
                    themeZipFile = themeZipFile2;
                }
            }
        }
        return themeZipFile;
    }

    private boolean getZipInputStream(MiuiResources.ThemeFileInfoOption themeFileInfoOption, String str) {
        ZipEntry zipEntry = null;
        try {
            if (str.endsWith(FUZZY_SEARCH_ICON_SUFFIX)) {
                String substring = str.substring(0, str.length() - 6);
                Enumeration<? extends ZipEntry> entries = this.mZipFile.entries();
                while (true) {
                    if (!entries.hasMoreElements()) {
                        break;
                    }
                    ZipEntry nextElement = entries.nextElement();
                    if (!nextElement.isDirectory() && nextElement.getName().startsWith(substring)) {
                        zipEntry = nextElement;
                        break;
                    }
                }
            } else {
                zipEntry = this.mZipFile.getEntry(str);
            }
            if (zipEntry != null) {
                themeFileInfoOption.outSize = zipEntry.getSize();
                if (themeFileInfoOption.inRequestStream) {
                    InputStream inputStream = this.mZipFile.getInputStream(zipEntry);
                    themeFileInfoOption.outInputStream = inputStream;
                    if (inputStream == null) {
                        return false;
                    }
                }
                return themeFileInfoOption.outSize > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadThemeConfigInner(ThemeResources.LoadThemeConfigCallback loadThemeConfigCallback, String str, ThemeResources.ConfigType configType) {
        InputStream zipInputStream = getZipInputStream(str);
        if (zipInputStream != null) {
            loadThemeConfigCallback.load(zipInputStream, configType);
        }
    }

    private String regularDpiFallbackPath(String str, int i) {
        String str2;
        int indexOf = str.indexOf(47, i);
        if (indexOf < 0 || indexOf == i + 1) {
            return str;
        }
        int indexOf2 = str.indexOf("dpi", i);
        if (indexOf2 > 0) {
            int i2 = indexOf2 + 3;
            while (str.charAt(indexOf2) != '-' && indexOf2 > i) {
                indexOf2--;
            }
            if (indexOf2 == i && i2 == indexOf) {
                return str;
            }
            str2 = str.substring(indexOf2, i2);
        } else {
            str2 = "";
        }
        FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
        buffer.assign(str, i);
        buffer.append(str2);
        buffer.append(str, indexOf, str.length());
        String fixedSizeStringBuffer = buffer.toString();
        FixedSizeStringBuffer.freeBuffer(buffer);
        return fixedSizeStringBuffer;
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0035, code lost:
    
        r3 = miui.content.res.FixedSizeStringBuffer.getBuffer();
        r3.assign(r5, r4);
        r3.append(r5, r0, r5.length());
        r5 = r3.toString();
        miui.content.res.FixedSizeStringBuffer.freeBuffer(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x004a, code lost:
    
        return r5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String trimVersionPart(int r4, java.lang.String r5) {
        /*
            r3 = this;
            int r3 = r5.length()
            int r3 = r3 + (-2)
            if (r4 >= r3) goto L4a
            char r3 = r5.charAt(r4)
            r0 = 45
            if (r3 != r0) goto L4a
            int r3 = r4 + 1
            char r3 = r5.charAt(r3)
            r0 = 118(0x76, float:1.65E-43)
            if (r3 != r0) goto L4a
            int r3 = r4 + 2
            r0 = r3
        L1d:
            int r1 = r5.length()
            if (r0 >= r1) goto L33
            char r1 = r5.charAt(r0)
            r2 = 48
            if (r1 < r2) goto L33
            r2 = 57
            if (r1 <= r2) goto L30
            goto L33
        L30:
            int r0 = r0 + 1
            goto L1d
        L33:
            if (r0 <= r3) goto L4a
            miui.content.res.FixedSizeStringBuffer r3 = miui.content.res.FixedSizeStringBuffer.getBuffer()
            r3.assign(r5, r4)
            int r4 = r5.length()
            r3.append(r5, r0, r4)
            java.lang.String r5 = r3.toString()
            miui.content.res.FixedSizeStringBuffer.freeBuffer(r3)
        L4a:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.content.res.ThemeZipFile.trimVersionPart(int, java.lang.String):java.lang.String");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long checkUpdate() {
        if (DBG) {
            Log.d(TAG, "checkUpdate for " + this.mPath);
        }
        File file = new File(this.mPath);
        long lastModified = file.lastModified();
        if (this.mLastModifiedTime != lastModified && ThemeCompatibility.isCompatibleResource(this.mPath)) {
            synchronized (this) {
                if (this.mLastModifiedTime != lastModified) {
                    this.mUpatedTime = System.currentTimeMillis();
                    clean();
                    if (lastModified != 0) {
                        if (DBG) {
                            Log.d(TAG, "openZipFile for " + this.mPath);
                        }
                        try {
                            this.mZipFile = new MyZipFile(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    this.mLastModifiedTime = lastModified;
                }
            }
        }
        return this.mUpatedTime;
    }

    protected void finalize() throws Throwable {
        clean();
        super.finalize();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption themeFileInfoOption) {
        if (this.mMetaData.mSupportFile && isValid()) {
            FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
            if (!ThemeToolUtils.isEmpty(themeFileInfoOption.outFilterPath)) {
                buffer.append(themeFileInfoOption.outFilterPath);
            }
            buffer.append(themeFileInfoOption.inResourcePath);
            String fixedSizeStringBuffer = buffer.toString();
            boolean themeFileInner = getThemeFileInner(themeFileInfoOption, fixedSizeStringBuffer);
            if (!themeFileInner && fixedSizeStringBuffer.endsWith(".9.png")) {
                buffer.move(-5);
                buffer.append("png");
                themeFileInner = getThemeFileInner(themeFileInfoOption, buffer.toString());
            }
            if (!themeFileInner && fixedSizeStringBuffer.endsWith(".webp")) {
                buffer.move(-4);
                buffer.append("png");
                themeFileInner = getThemeFileInner(themeFileInfoOption, buffer.toString());
            }
            FixedSizeStringBuffer.freeBuffer(buffer);
            return themeFileInner;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InputStream getZipInputStream(String str) {
        ZipEntry entry;
        if (isValid() && (entry = this.mZipFile.getEntry(str)) != null) {
            try {
                return this.mZipFile.getInputStream(entry);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isValid() {
        return this.mZipFile != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadThemeConfig(ThemeResources.LoadThemeConfigCallback loadThemeConfigCallback, String str) {
        if (isValid()) {
            FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
            buffer.assign(str);
            buffer.append(THEME_VALUE_FILE_NAME);
            buffer.append(THEME_VALUE_FILE_SUFFIX);
            String fixedSizeStringBuffer = buffer.toString();
            ThemeResources.ConfigType configType = ThemeResources.ConfigType.THEME_VALUES;
            loadThemeConfigInner(loadThemeConfigCallback, fixedSizeStringBuffer, configType);
            buffer.move(-4);
            buffer.append(ThemeDensityFallbackUtils.getDensitySuffix(sDensity));
            buffer.append(THEME_VALUE_FILE_SUFFIX);
            loadThemeConfigInner(loadThemeConfigCallback, buffer.toString(), configType);
            buffer.assign(str);
            buffer.append(THEME_FALLBACK_FILE);
            loadThemeConfigInner(loadThemeConfigCallback, buffer.toString(), ThemeResources.ConfigType.THEME_FALLBACK);
            FixedSizeStringBuffer.freeBuffer(buffer);
        }
    }
}
