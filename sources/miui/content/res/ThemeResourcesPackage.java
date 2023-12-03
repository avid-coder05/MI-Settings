package miui.content.res;

import android.content.res.MiuiResources;
import android.os.Process;
import android.os.StrictMode;
import com.miui.internal.content.res.ThemeDefinition;
import com.miui.internal.content.res.ThemeToolUtils;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.content.res.ThemeResources;

/* loaded from: classes3.dex */
public final class ThemeResourcesPackage extends ThemeResources {
    private static final Map<String, WeakReference<ThemeResourcesPackage>> sPackageResources = new HashMap();

    protected ThemeResourcesPackage(ThemeResourcesPackage themeResourcesPackage, MiuiResources miuiResources, String str, ThemeResources.MetaData metaData) {
        super(themeResourcesPackage, miuiResources, str, metaData);
    }

    private static StrictMode.ThreadPolicy allowDiskReads() {
        if (Process.myUid() != 0) {
            StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
            StrictMode.allowThreadDiskWrites();
            return allowThreadDiskReads;
        }
        return null;
    }

    public static ThemeResourcesPackage getThemeResources(MiuiResources miuiResources, String str) {
        WeakReference<ThemeResourcesPackage> weakReference;
        WeakReference<ThemeResourcesPackage> weakReference2;
        StrictMode.ThreadPolicy allowDiskReads = allowDiskReads();
        Map<String, WeakReference<ThemeResourcesPackage>> map = sPackageResources;
        ThemeResourcesPackage themeResourcesPackage = null;
        ThemeResourcesPackage themeResourcesPackage2 = (!map.containsKey(str) || (weakReference2 = map.get(str)) == null) ? null : weakReference2.get();
        if (themeResourcesPackage2 == null) {
            ThemeResourcesPackage topLevelThemeResources = getTopLevelThemeResources(miuiResources, str);
            synchronized (map) {
                if (map.containsKey(str) && (weakReference = map.get(str)) != null) {
                    themeResourcesPackage = weakReference.get();
                }
                if (themeResourcesPackage == null) {
                    map.put(str, new WeakReference<>(topLevelThemeResources));
                    themeResourcesPackage2 = topLevelThemeResources;
                } else {
                    themeResourcesPackage2 = themeResourcesPackage;
                }
            }
        }
        resetOldPolicy(allowDiskReads);
        return themeResourcesPackage2;
    }

    public static ThemeResourcesPackage getTopLevelThemeResources(MiuiResources miuiResources, String str) {
        boolean needProvisionTheme = ThemeResources.needProvisionTheme();
        ThemeResourcesPackage themeResourcesPackage = null;
        int i = 0;
        while (true) {
            ThemeResources.MetaData[] metaDataArr = ThemeResources.THEME_PATHS;
            if (i >= metaDataArr.length) {
                break;
            }
            if (needProvisionTheme || !ThemeResources.PROVISION_THEME_PATH.equals(metaDataArr[i].mThemePath)) {
                themeResourcesPackage = new ThemeResourcesPackage(themeResourcesPackage, miuiResources, str, metaDataArr[i]);
            }
            i++;
        }
        if ((miuiResources.getConfiguration().uiMode & 32) != 0) {
            themeResourcesPackage.setNightModeEnable(true);
        }
        return themeResourcesPackage;
    }

    private boolean loadAppThemeFileFromMiuiFramework(MiuiResources.ThemeFileInfoOption themeFileInfoOption, ThemeDefinition.FallbackInfo fallbackInfo) {
        if (fallbackInfo != null && fallbackInfo.mResType == ThemeDefinition.ResourceType.DRAWABLE && ThemeResources.MIUI_PACKAGE.equals(fallbackInfo.mResFallbackPkgName) && themeFileInfoOption.inResourcePath.endsWith(fallbackInfo.mResOriginalName)) {
            int i = themeFileInfoOption.inCookie;
            String str = themeFileInfoOption.inResourcePath;
            themeFileInfoOption.inCookie = ThemeResources.sCookieMiuiFramework;
            themeFileInfoOption.inResourcePath = str.replace(fallbackInfo.mResOriginalName, fallbackInfo.mResFallbackName);
            boolean loadFrameworkThemeFile = loadFrameworkThemeFile(themeFileInfoOption);
            themeFileInfoOption.inResourcePath = str;
            themeFileInfoOption.inCookie = i;
            return loadFrameworkThemeFile;
        }
        return false;
    }

    private boolean loadFrameworkThemeFile(MiuiResources.ThemeFileInfoOption themeFileInfoOption) {
        List<ThemeDefinition.FallbackInfo> mayFilterFallbackList;
        if (this.mPackageZipFile.isValid()) {
            if (ThemeResources.isMiuiResourceCookie(themeFileInfoOption.inCookie) && (mayFilterFallbackList = ThemeCompatibility.getMayFilterFallbackList(ThemeResources.MIUI_PACKAGE, ThemeDefinition.ResourceType.DRAWABLE, themeFileInfoOption.inResourcePath)) != null) {
                String str = themeFileInfoOption.inResourcePath;
                for (ThemeDefinition.FallbackInfo fallbackInfo : mayFilterFallbackList) {
                    if (this.mPackageName.equals(fallbackInfo.mResFallbackPkgName) && str.endsWith(fallbackInfo.mResOriginalName)) {
                        themeFileInfoOption.inResourcePath = str.replace(fallbackInfo.mResOriginalName, fallbackInfo.mResFallbackName);
                        boolean themeFileNonFallback = super.getThemeFileNonFallback(themeFileInfoOption);
                        themeFileInfoOption.inResourcePath = str;
                        if (themeFileNonFallback) {
                            return true;
                        }
                    }
                }
            }
            boolean z = false;
            String str2 = themeFileInfoOption.inResourcePath;
            int i = ThemeResources.sCookieFramework;
            int i2 = themeFileInfoOption.inCookie;
            if (i == i2) {
                themeFileInfoOption.inResourcePath = "framework-res/" + str2;
                z = super.getThemeFile(themeFileInfoOption, this.mPackageName, ThemeResources.FRAMEWORK_PACKAGE);
            } else if (ThemeResources.isMiuiResourceCookie(i2)) {
                themeFileInfoOption.inResourcePath = "framework-miui-res/" + str2;
                z = super.getThemeFile(themeFileInfoOption, this.mPackageName, ThemeResources.MIUI_PACKAGE);
            }
            themeFileInfoOption.inResourcePath = str2;
            if (z) {
                themeFileInfoOption.outFilterPath = "package/only";
                return true;
            }
        }
        return ThemeResources.getSystem().getThemeFile(themeFileInfoOption, this.mPackageName);
    }

    private static void resetOldPolicy(StrictMode.ThreadPolicy threadPolicy) {
        if (Process.myUid() != 0) {
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }

    @Override // miui.content.res.ThemeResources
    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption themeFileInfoOption) {
        if (ThemeResources.isAppResourceCookie(themeFileInfoOption.inCookie)) {
            if (super.getThemeFile(themeFileInfoOption)) {
                return true;
            }
            if (this.mPackageZipFile.isValid()) {
                List<ThemeDefinition.FallbackInfo> mayFilterFallbackList = ThemeCompatibility.getMayFilterFallbackList(this.mPackageName, ThemeDefinition.ResourceType.DRAWABLE, themeFileInfoOption.inResourcePath);
                if (mayFilterFallbackList != null) {
                    Iterator<ThemeDefinition.FallbackInfo> it = mayFilterFallbackList.iterator();
                    while (it.hasNext()) {
                        if (loadAppThemeFileFromMiuiFramework(themeFileInfoOption, it.next())) {
                            return true;
                        }
                    }
                }
                Iterator<ThemeResources.FilterInfo> it2 = getFilterInfos().iterator();
                while (it2.hasNext()) {
                    ThemeResources.FilterInfo next = it2.next();
                    if (next.match(this.mPackageName, this.mNightMode)) {
                        if (loadAppThemeFileFromMiuiFramework(themeFileInfoOption, (ThemeDefinition.FallbackInfo) next.mFallback.mFallbackInfoMap.get(ThemeToolUtils.getNameFromPath(themeFileInfoOption.inResourcePath)))) {
                            return true;
                        }
                    }
                }
                return false;
            }
            return false;
        }
        return loadFrameworkThemeFile(themeFileInfoOption);
    }

    @Override // miui.content.res.ThemeResources
    public void mergeThemeValues(String str, ThemeValues themeValues) {
        StrictMode.ThreadPolicy allowDiskReads = allowDiskReads();
        if (this.mIsTop) {
            ThemeResources.getSystem().mergeThemeValues(str, themeValues);
        }
        super.mergeThemeValues(str, themeValues);
        resetOldPolicy(allowDiskReads);
    }
}
