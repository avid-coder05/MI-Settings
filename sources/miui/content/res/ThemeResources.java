package miui.content.res;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.MiuiResources;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.miui.ResourcesManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.miui.internal.content.res.ThemeDefinition;
import com.miui.internal.content.res.ThemeFallback;
import com.miui.internal.content.res.ThemeToolUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import miui.graphics.BitmapFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* loaded from: classes3.dex */
public class ThemeResources {
    private static final String ATTR_FILTER_PATH = "path";
    static boolean DBG = false;
    public static final String DISABLE_PROVISION_THEME;
    private static final String FILTER_DESCRIPTION_FILE = "filters.xml";
    public static final String FRAMEWORK_NAME = "framework-res";
    public static final String FRAMEWORK_PACKAGE = "android";
    public static final String ICONS_NAME = "icons";
    public static final String LANGUAGE_THEME_PATH = "/data/system/language/";
    public static final String LOCKSCREEN_NAME = "lockscreen";
    public static final String LOCKSCREEN_WALLPAPER_NAME = "lock_wallpaper";
    public static final String MIUI_NAME = "framework-miui-res";
    public static final String MIUI_PACKAGE = "miui";
    public static final String PROVISION_THEME_PATH = "/system/media/theme/provision/";
    public static final String SUPER_WALLPAPER_LOCKSCREEN_NAME = "splockscreen";
    public static final MetaData SUPER_WALLPAPER_PATH;
    public static final String SYSTEMUI_NAME = "com.android.systemui";
    public static final String SYSTEM_LANGUAGE_THEME_PATH = "/system/language/";
    public static final String SYSTEM_THEME_PATH = "/system/media/theme/default/";
    private static final String TAG_FILTER = "filter";
    private static final String TAG_PACKAGE = "package";
    public static final String THEME_DATA_CONFIG_DIR_PATH = "/data/system/theme_config/";
    public static final String THEME_MAGIC_PATH;
    public static final String THEME_PATH = "/data/system/theme/";
    public static final MetaData[] THEME_PATHS;
    public static final String THEME_RIGHTS_PATH = "/data/system/theme/rights/";
    public static final String THEME_VERSION_COMPATIBILITY_PATH = "/data/system/theme/compatibility-v12/";
    public static final String WALLPAPER_NAME = "wallpaper";
    public static final String sAppliedLockstyleConfigPath = "/data/system/theme/config.config";
    protected static int sCookieFramework;
    protected static int sCookieMiuiExtFramework;
    protected static int sCookieMiuiFramework;
    protected static int sCookieMiuiSdk;
    private static boolean sHasUpdatedAfterZygote;
    protected static boolean sIsZygote;
    private static Drawable sLockWallpaperCache;
    private static long sLockWallpaperModifiedTime;
    private static ThemeResourcesSystem sSystem;
    private boolean mHasInitedDefaultValue;
    protected boolean mIsTop;
    protected boolean mIsUserThemePath;
    protected MetaData mMetaData;
    protected boolean mNightMode;
    protected String mPackageName;
    protected ThemeZipFile mPackageZipFile;
    protected MiuiResources mResources;
    protected boolean mShouldFallbackDeeper;
    protected boolean mSupportWrapper;
    protected long mUpdatedTime;
    protected ThemeResources mWrapped;
    private ArrayList<FilterInfo> mFilterInfos = new ArrayList<>();
    private LoadThemeConfigHelper mLoadThemeValuesCallback = new LoadThemeConfigHelper();

    /* loaded from: classes3.dex */
    public enum ConfigType {
        THEME_VALUES,
        THEME_FALLBACK
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes3.dex */
    public static class FilterInfo {
        public ThemeFallback mFallback;
        public boolean mNightMode;
        public HashSet<String> mPackages;
        public String mPath;
        public ThemeValues mValues;

        public FilterInfo(String str, HashSet<String> hashSet) {
            this(str, hashSet, false);
        }

        public FilterInfo(String str, HashSet<String> hashSet, boolean z) {
            this.mValues = new ThemeValues();
            this.mFallback = new ThemeFallback();
            this.mPath = str;
            this.mPackages = hashSet;
            this.mNightMode = z;
        }

        public boolean match(String str, boolean z) {
            HashSet<String> hashSet = this.mPackages;
            return (hashSet == null || hashSet.contains(str)) && this.mNightMode == z;
        }
    }

    /* loaded from: classes3.dex */
    public interface LoadThemeConfigCallback {
        void load(InputStream inputStream, ConfigType configType);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class LoadThemeConfigHelper implements LoadThemeConfigCallback {
        private FilterInfo mFilter;

        private LoadThemeConfigHelper() {
        }

        @Override // miui.content.res.ThemeResources.LoadThemeConfigCallback
        public void load(InputStream inputStream, ConfigType configType) {
            if (inputStream != null) {
                if (configType == ConfigType.THEME_VALUES) {
                    FilterInfo filterInfo = this.mFilter;
                    ThemeResources themeResources = ThemeResources.this;
                    filterInfo.mValues = ThemeValues.parseThemeValues(themeResources.mResources, inputStream, themeResources.mPackageName);
                } else if (configType == ConfigType.THEME_FALLBACK) {
                    FilterInfo filterInfo2 = this.mFilter;
                    ThemeResources themeResources2 = ThemeResources.this;
                    filterInfo2.mFallback = ThemeFallback.parseThemeFallback(themeResources2.mResources, inputStream, themeResources2.mPackageName);
                }
            }
        }

        public void newTarget(FilterInfo filterInfo) {
            this.mFilter = filterInfo;
            ThemeResources.this.mPackageZipFile.loadThemeConfig(this, filterInfo.mPath);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes3.dex */
    public static final class MetaData {
        public boolean mSupportFile;
        public boolean mSupportValue;
        public String mThemePath;

        public MetaData(String str, boolean z, boolean z2) {
            this.mThemePath = str;
            this.mSupportValue = z;
            this.mSupportFile = z2;
        }
    }

    static {
        String str = Build.VERSION.SDK_INT > 22 ? "/data/system/theme_magic/" : "/data/system/";
        THEME_MAGIC_PATH = str;
        SUPER_WALLPAPER_PATH = new MetaData(str, true, true);
        THEME_PATHS = new MetaData[]{new MetaData(SYSTEM_THEME_PATH, true, true), new MetaData(PROVISION_THEME_PATH, true, true), new MetaData("/data/system/theme/", true, true)};
        DISABLE_PROVISION_THEME = str + "disable_provision_theme";
        sCookieFramework = -1;
        sCookieMiuiExtFramework = -1;
        sCookieMiuiFramework = -1;
        sCookieMiuiSdk = -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ThemeResources(ThemeResources themeResources, MiuiResources miuiResources, String str, MetaData metaData) {
        this.mIsTop = true;
        this.mIsUserThemePath = false;
        initSystemCookies(miuiResources);
        if (themeResources != null) {
            this.mWrapped = themeResources;
            themeResources.mIsTop = false;
        }
        this.mResources = miuiResources;
        this.mPackageName = getPackageName(str);
        this.mMetaData = metaData;
        this.mIsUserThemePath = "/data/system/theme/".equals(metaData.mThemePath);
        this.mPackageZipFile = ThemeZipFile.getThemeZipFile(metaData, str);
        this.mSupportWrapper = ("icons".equals(str) || "lockscreen".equals(str) || SUPER_WALLPAPER_LOCKSCREEN_NAME.equals(str)) ? false : true;
        checkUpdate();
    }

    public static final void clearLockWallpaperCache() {
        sLockWallpaperModifiedTime = 0L;
        sLockWallpaperCache = null;
    }

    private static String getFallbackDrawablePath(String str, String str2, String str3) {
        int lastIndexOf = str.lastIndexOf(47) + 1;
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = lastIndexOf + i;
            if (i3 >= str.length() || i2 >= str2.length()) {
                break;
            }
            char charAt = str.charAt(i3);
            if (charAt != str2.charAt(i2)) {
                return null;
            }
            if (charAt == '.') {
                break;
            }
            i++;
            i2++;
        }
        FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
        buffer.assign(str, lastIndexOf);
        buffer.append(str3);
        String fixedSizeStringBuffer = buffer.toString();
        FixedSizeStringBuffer.freeBuffer(buffer);
        return fixedSizeStringBuffer;
    }

    private List<FilterInfo> getFilterInfos(boolean z) {
        ArrayList arrayList = new ArrayList();
        String str = z ? "nightmode/" : "";
        arrayList.add(new FilterInfo(str, null, z));
        InputStream zipInputStream = this.mPackageZipFile.getZipInputStream(str + FILTER_DESCRIPTION_FILE);
        if (zipInputStream == null) {
            return arrayList;
        }
        try {
            NodeList elementsByTagName = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(zipInputStream).getElementsByTagName(TAG_FILTER);
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                Element element = (Element) elementsByTagName.item(i);
                String attribute = element.getAttribute("path");
                if (!TextUtils.isEmpty(attribute) && attribute.indexOf("/") == -1 && !"res".equals(attribute)) {
                    NodeList elementsByTagName2 = element.getElementsByTagName("package");
                    HashSet hashSet = new HashSet();
                    for (int i2 = 0; i2 < elementsByTagName2.getLength(); i2++) {
                        hashSet.add(elementsByTagName2.item(i2).getFirstChild().getNodeValue());
                    }
                    arrayList.add(new FilterInfo(str + attribute + '/', hashSet, z));
                }
            }
        } catch (Exception unused) {
        } catch (Throwable th) {
            try {
                zipInputStream.close();
            } catch (IOException unused2) {
            }
            throw th;
        }
        try {
            zipInputStream.close();
        } catch (IOException unused3) {
            return arrayList;
        }
    }

    public static final Drawable getLockWallpaperCache(Context context) {
        File lockscreenWallpaper = sSystem.getLockscreenWallpaper();
        if (lockscreenWallpaper == null || !lockscreenWallpaper.exists()) {
            return null;
        }
        if (sLockWallpaperModifiedTime == lockscreenWallpaper.lastModified()) {
            return sLockWallpaperCache;
        }
        sLockWallpaperCache = null;
        try {
            Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
            Point point = new Point();
            getRealSize(defaultDisplay, point);
            int i = point.x;
            int i2 = point.y;
            if (i > i2) {
                Log.e("LockWallpaper", "Wrong display metrics for width = " + i + " and height = " + i2);
                i2 = i;
                i = i2;
            }
            Bitmap decodeBitmap = BitmapFactory.decodeBitmap(lockscreenWallpaper.getAbsolutePath(), i, i2, false);
            if (decodeBitmap != null) {
                sLockWallpaperCache = new BitmapDrawable(context.getResources(), decodeBitmap);
                sLockWallpaperModifiedTime = lockscreenWallpaper.lastModified();
            }
        } catch (Exception e) {
            Log.e("ThemeResources", e.getMessage(), e);
        } catch (OutOfMemoryError e2) {
            Log.e("ThemeResources", e2.getMessage(), e2);
        }
        return sLockWallpaperCache;
    }

    private static final String getPackageName(String str) {
        return (FRAMEWORK_NAME.equals(str) || "icons".equals(str)) ? FRAMEWORK_PACKAGE : (MIUI_NAME.equals(str) || "lockscreen".equals(str) || SUPER_WALLPAPER_LOCKSCREEN_NAME.equals(str)) ? MIUI_PACKAGE : str;
    }

    private static void getRealSize(Display display, Point point) {
        try {
            Display.class.getDeclaredMethod("getRealSize", Point.class, Boolean.TYPE).invoke(display, point, Boolean.TRUE);
        } catch (Exception unused) {
            Log.e("LockWallpaper", "no getRealSize hack method");
            display.getRealSize(point);
        }
    }

    public static ThemeResources getSystem(MiuiResources miuiResources) {
        if (sSystem == null) {
            sSystem = ThemeResourcesSystem.getTopLevelThemeResources(miuiResources);
        }
        return sSystem;
    }

    public static ThemeResourcesSystem getSystem() {
        ThemeResourcesSystem themeResourcesSystem;
        if (!sIsZygote && !sHasUpdatedAfterZygote && (themeResourcesSystem = sSystem) != null) {
            themeResourcesSystem.checkUpdate();
            sHasUpdatedAfterZygote = true;
        }
        return sSystem;
    }

    private boolean getThemeFileWithFallback(MiuiResources.ThemeFileInfoOption themeFileInfoOption, String str, String str2) {
        List<ThemeDefinition.FallbackInfo> mayFilterFallbackList;
        String str3;
        String str4;
        if (this.mIsUserThemePath && this.mPackageZipFile.isValid() && (mayFilterFallbackList = ThemeCompatibility.getMayFilterFallbackList(str2, ThemeDefinition.ResourceType.DRAWABLE, themeFileInfoOption.inResourcePath)) != null) {
            String str5 = themeFileInfoOption.inResourcePath;
            for (ThemeDefinition.FallbackInfo fallbackInfo : mayFilterFallbackList) {
                if (fallbackInfo.mResType == ThemeDefinition.ResourceType.DRAWABLE && fallbackInfo.mResFallbackPkgName == null) {
                    String fallbackDrawablePath = getFallbackDrawablePath(str5, fallbackInfo.mResOriginalName, fallbackInfo.mResFallbackName);
                    themeFileInfoOption.inResourcePath = fallbackDrawablePath;
                    if (fallbackDrawablePath != null) {
                        String[] strArr = fallbackInfo.mResPreferredConfigs;
                        if (strArr != null) {
                            int length = strArr.length;
                            int i = 0;
                            while (true) {
                                str3 = null;
                                if (i >= length) {
                                    str4 = null;
                                    break;
                                }
                                String str6 = strArr[i];
                                int indexOf = themeFileInfoOption.inResourcePath.indexOf(str6);
                                if (indexOf > 0) {
                                    String substring = themeFileInfoOption.inResourcePath.substring(0, indexOf);
                                    String substring2 = themeFileInfoOption.inResourcePath.substring(indexOf + str6.length());
                                    str3 = substring;
                                    str4 = substring2;
                                    break;
                                }
                                i++;
                            }
                            if (str3 != null) {
                                for (String str7 : fallbackInfo.mResPreferredConfigs) {
                                    themeFileInfoOption.inResourcePath = str3 + str7 + str4;
                                    if (getThemeFileWithPath(themeFileInfoOption, str)) {
                                        themeFileInfoOption.inResourcePath = str5;
                                        return true;
                                    }
                                }
                            } else {
                                continue;
                            }
                        } else if (getThemeFileWithPath(themeFileInfoOption, str)) {
                            themeFileInfoOption.inResourcePath = str5;
                            return true;
                        }
                    } else {
                        continue;
                    }
                }
            }
            themeFileInfoOption.inResourcePath = str5;
        }
        return false;
    }

    private boolean getThemeFileWithPath(MiuiResources.ThemeFileInfoOption themeFileInfoOption, String str) {
        if (this.mPackageZipFile.isValid()) {
            ArrayList<FilterInfo> filterInfos = getFilterInfos();
            for (int size = filterInfos.size() - 1; size >= 0; size--) {
                FilterInfo filterInfo = filterInfos.get(size);
                if (filterInfo.match(str, this.mNightMode)) {
                    themeFileInfoOption.outFilterPath = filterInfo.mPath;
                    if (this.mPackageZipFile.getThemeFile(themeFileInfoOption)) {
                        return true;
                    }
                    String nameFromPath = ThemeToolUtils.getNameFromPath(themeFileInfoOption.inResourcePath);
                    ThemeDefinition.FallbackInfo fallbackInfo = (ThemeDefinition.FallbackInfo) filterInfo.mFallback.mFallbackInfoMap.get(nameFromPath);
                    if (fallbackInfo != null && fallbackInfo.mResType == ThemeDefinition.ResourceType.DRAWABLE && this.mPackageName.equals(fallbackInfo.mResFallbackPkgName)) {
                        String str2 = themeFileInfoOption.inResourcePath;
                        themeFileInfoOption.inResourcePath = str2.replace(nameFromPath, fallbackInfo.mResFallbackName);
                        boolean themeFile = this.mPackageZipFile.getThemeFile(themeFileInfoOption);
                        themeFileInfoOption.inResourcePath = str2;
                        if (themeFile) {
                            return true;
                        }
                    }
                }
            }
            themeFileInfoOption.outFilterPath = null;
        }
        if (this.mShouldFallbackDeeper) {
            return this.mWrapped.getThemeFile(themeFileInfoOption, str);
        }
        return false;
    }

    public static ThemeResources getTopLevelThemeResources(MiuiResources miuiResources, String str) {
        boolean needProvisionTheme = needProvisionTheme();
        ThemeResources themeResources = null;
        int i = 0;
        while (true) {
            MetaData[] metaDataArr = THEME_PATHS;
            if (i >= metaDataArr.length) {
                return themeResources;
            }
            if (needProvisionTheme || !PROVISION_THEME_PATH.equals(metaDataArr[i].mThemePath)) {
                themeResources = new ThemeResources(themeResources, miuiResources, str, metaDataArr[i]);
            }
            i++;
        }
    }

    public static ThemeResources getTopLevelThemeResources(MiuiResources miuiResources, String str, MetaData metaData) {
        return new ThemeResources(null, miuiResources, str, metaData);
    }

    private void initBasePaths() {
        ArrayList<FilterInfo> arrayList = new ArrayList<>();
        arrayList.addAll(getFilterInfos(false));
        arrayList.addAll(getFilterInfos(true));
        this.mFilterInfos = arrayList;
    }

    private static void initSystemCookies(Resources resources) {
        if (resources == null) {
            return;
        }
        if (Build.VERSION.SDK_INT <= 27) {
            for (int i = 0; i < 100; i++) {
                if (sCookieFramework >= 0 && sCookieMiuiExtFramework >= 0 && sCookieMiuiFramework >= 0 && sCookieMiuiSdk >= 0) {
                    return;
                }
                try {
                    String cookieName = AssetManagerUtil.getCookieName(resources.getAssets(), i);
                    if ("/system/framework/framework-res.apk".equals(cookieName)) {
                        sCookieFramework = i;
                    } else if (ResourcesManager.isMiuiExtFrameworkPath(cookieName)) {
                        sCookieMiuiExtFramework = i;
                    } else if (ResourcesManager.isMiuiSystemSdkPath(cookieName)) {
                        sCookieMiuiFramework = i;
                    } else if (ResourcesManager.isMiuiSdkPath(cookieName)) {
                        sCookieMiuiSdk = i;
                    }
                } catch (Exception unused) {
                }
            }
            return;
        }
        AssetManager assets = resources.getAssets();
        int findCookieForPath = AssetManagerUtil.findCookieForPath(assets, "/system/framework/framework-res.apk");
        if (findCookieForPath > 0) {
            sCookieFramework = findCookieForPath;
        }
        int findCookieForPath2 = AssetManagerUtil.findCookieForPath(assets, ResourcesManager.FRAMEWORK_EXT_RES_PATH);
        if (findCookieForPath2 > 0) {
            sCookieMiuiExtFramework = findCookieForPath2;
        }
        int findCookieForPath3 = AssetManagerUtil.findCookieForPath(assets, ResourcesManager.MIUI_FRAMEWORK_RES_PATH);
        if (findCookieForPath3 > 0) {
            sCookieMiuiFramework = findCookieForPath3;
        } else if (AssetManagerUtil.findCookieForPath(assets, "/data/app/com.miui.system-1.apk") > 0) {
            sCookieMiuiFramework = AssetManagerUtil.findCookieForPath(assets, "/data/app/com.miui.system-1.apk");
        } else if (AssetManagerUtil.findCookieForPath(assets, "/data/app/com.miui.system-2.apk") > 0) {
            sCookieMiuiFramework = AssetManagerUtil.findCookieForPath(assets, "/data/app/com.miui.system-2.apk");
        }
        int findCookieForPath4 = AssetManagerUtil.findCookieForPath(assets, ResourcesManager.MIUI_SDK_RES_PATH);
        if (findCookieForPath4 > 0) {
            sCookieMiuiSdk = findCookieForPath4;
        } else if (AssetManagerUtil.findCookieForPath(assets, "/data/app/com.miui.core-1.apk") > 0) {
            sCookieMiuiSdk = AssetManagerUtil.findCookieForPath(assets, "/data/app/com.miui.core-1.apk");
        } else if (AssetManagerUtil.findCookieForPath(assets, "/data/app/com.miui.core-2.apk") > 0) {
            sCookieMiuiSdk = AssetManagerUtil.findCookieForPath(assets, "/data/app/com.miui.core-2.apk");
        }
    }

    public static boolean isAppResourceCookie(int i) {
        return (sCookieFramework == i || isMiuiResourceCookie(i)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isMiuiResourceCookie(int i) {
        return i == sCookieMiuiSdk || i == sCookieMiuiFramework || i == sCookieMiuiExtFramework;
    }

    private void loadThemeValues() {
        ArrayList<FilterInfo> filterInfos = getFilterInfos();
        for (int i = 0; i < filterInfos.size(); i++) {
            this.mLoadThemeValuesCallback.newTarget(filterInfos.get(i));
        }
    }

    public static boolean needProvisionTheme() {
        return "scorpio".equals(Build.DEVICE) && !new File(DISABLE_PROVISION_THEME).exists();
    }

    public long checkUpdate() {
        if (!sIsZygote) {
            long checkUpdate = this.mPackageZipFile.checkUpdate();
            boolean z = false;
            if (this.mUpdatedTime != checkUpdate) {
                this.mUpdatedTime = checkUpdate;
                initBasePaths();
                loadThemeValues();
                this.mHasInitedDefaultValue = false;
            }
            if (this.mWrapped != null && (this.mSupportWrapper || !this.mPackageZipFile.isValid())) {
                z = true;
            }
            this.mShouldFallbackDeeper = z;
            if (z) {
                if (PROVISION_THEME_PATH.equals(this.mWrapped.mMetaData.mThemePath) && !needProvisionTheme()) {
                    this.mWrapped = this.mWrapped.mWrapped;
                    this.mUpdatedTime = Math.max(this.mUpdatedTime, System.currentTimeMillis());
                }
                this.mUpdatedTime = Math.max(this.mUpdatedTime, this.mWrapped.checkUpdate());
            }
        }
        return this.mUpdatedTime;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ArrayList<FilterInfo> getFilterInfos() {
        return this.mFilterInfos;
    }

    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption themeFileInfoOption) {
        return getThemeFile(themeFileInfoOption, this.mPackageName);
    }

    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption themeFileInfoOption, String str) {
        return getThemeFile(themeFileInfoOption, str, this.mPackageName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption themeFileInfoOption, String str, String str2) {
        if (getThemeFileWithPath(themeFileInfoOption, str)) {
            return true;
        }
        return getThemeFileWithFallback(themeFileInfoOption, str, str2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean getThemeFileNonFallback(MiuiResources.ThemeFileInfoOption themeFileInfoOption) {
        return getThemeFileWithPath(themeFileInfoOption, this.mPackageName);
    }

    public InputStream getThemeStream(String str, long[] jArr) {
        MiuiResources.ThemeFileInfoOption themeFileInfoOption = new MiuiResources.ThemeFileInfoOption(-1, str, true);
        if (getThemeFile(themeFileInfoOption) && jArr != null) {
            jArr[0] = themeFileInfoOption.outSize;
        }
        return themeFileInfoOption.outInputStream;
    }

    public long getUpdateTime() {
        return this.mUpdatedTime;
    }

    public boolean hasThemeFile(String str) {
        return getThemeFile(new MiuiResources.ThemeFileInfoOption(-1, str, false), this.mPackageName);
    }

    public void mergeThemeValues(String str, ThemeValues themeValues) {
        if (this.mShouldFallbackDeeper) {
            this.mWrapped.mergeThemeValues(str, themeValues);
        }
        if (this.mMetaData.mSupportValue) {
            ArrayList<FilterInfo> filterInfos = getFilterInfos();
            boolean z = false;
            for (int i = 0; i < filterInfos.size(); i++) {
                FilterInfo filterInfo = filterInfos.get(i);
                if (filterInfo.match(str, this.mNightMode) && !filterInfo.mValues.isEmpty()) {
                    themeValues.putAll(filterInfo.mValues);
                    z = true;
                }
            }
            if ((this.mHasInitedDefaultValue || !this.mPackageZipFile.isValid()) && !z) {
                return;
            }
            this.mHasInitedDefaultValue = true;
            themeValues.mergeNewDefaultValueIfNeed(this.mResources, this.mPackageName);
        }
    }

    public void setNightModeEnable(boolean z) {
        this.mNightMode = z;
        if (this.mShouldFallbackDeeper) {
            this.mWrapped.setNightModeEnable(z);
        }
    }
}
