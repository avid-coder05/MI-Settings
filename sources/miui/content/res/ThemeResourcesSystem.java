package miui.content.res;

import android.content.res.MiuiResources;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.miui.internal.content.res.ThemeDensityFallbackUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import miui.content.res.ThemeResources;

/* loaded from: classes3.dex */
public final class ThemeResourcesSystem extends ThemeResources {
    private static final String ADVANCE_LOCKSCREEN_NAME = "advance/";
    private static final String TAG = "ThemeResourcesSystem";
    private static boolean sIconEnable;
    private static boolean sIconInited;
    private static ThemeResources sIcons;
    private static ThemeResources sLockscreen;
    private static ThemeResources sMiui;
    private static ThemeResources sSuperWallpaperLockscreen;
    private static long sUpdatedTimeIcon;
    private static long sUpdatedTimeLockscreen;
    private String mThemePath;

    protected ThemeResourcesSystem(ThemeResourcesSystem themeResourcesSystem, MiuiResources miuiResources, String str, ThemeResources.MetaData metaData) {
        super(themeResourcesSystem, miuiResources, str, metaData);
        this.mThemePath = metaData.mThemePath;
    }

    private void checkIconUpdate() {
        if (sIconEnable) {
            long checkUpdate = sIcons.checkUpdate();
            if (sUpdatedTimeIcon < checkUpdate) {
                sUpdatedTimeIcon = checkUpdate;
                IconCustomizer.clearCache();
            }
        }
    }

    private void checkLockScreenUpdate() {
        long checkUpdate = sLockscreen.checkUpdate();
        if (sUpdatedTimeLockscreen < checkUpdate) {
            sUpdatedTimeLockscreen = checkUpdate;
        }
    }

    private void checkSuperLockScreenUpdate() {
        long checkUpdate = sSuperWallpaperLockscreen.checkUpdate();
        if (sUpdatedTimeLockscreen < checkUpdate) {
            sUpdatedTimeLockscreen = checkUpdate;
        }
    }

    private boolean getIcon(MiuiResources.ThemeFileInfoOption themeFileInfoOption, String str) {
        if (ThemeCompatibility.isThemeEnabled()) {
            int i = Resources.getSystem().getConfiguration().densityDpi;
            int i2 = Resources.getSystem().getConfiguration().smallestScreenWidthDp;
            themeFileInfoOption.inResourcePath = "res/drawable" + ThemeDensityFallbackUtils.getScreenWidthSuffix(Resources.getSystem().getConfiguration()) + ThemeDensityFallbackUtils.getDensitySuffix(i) + "/" + str;
            themeFileInfoOption.inDensity = i;
            initIcon();
            if (sIcons.getThemeFile(themeFileInfoOption)) {
                return true;
            }
            themeFileInfoOption.inResourcePath = "res/drawable" + ThemeDensityFallbackUtils.getDensitySuffix(i) + "/" + str;
            if (sIcons.getThemeFile(themeFileInfoOption)) {
                return true;
            }
            themeFileInfoOption.inResourcePath = str;
            themeFileInfoOption.inDensity = 240;
            return sIcons.getThemeFile(themeFileInfoOption);
        }
        return false;
    }

    private boolean getThemeFileStreamSystem(MiuiResources.ThemeFileInfoOption themeFileInfoOption, String str) {
        if (ThemeCompatibility.isThemeEnabled()) {
            if (themeFileInfoOption.inResourcePath.endsWith("sym_def_app_icon.png")) {
                return getIcon(themeFileInfoOption, "sym_def_app_icon.png");
            }
            if (themeFileInfoOption.inResourcePath.endsWith("default_wallpaper.jpg")) {
                return false;
            }
            return super.getThemeFile(themeFileInfoOption, str);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ThemeResourcesSystem getTopLevelThemeResources(MiuiResources miuiResources) {
        ThemeResources.sIsZygote = true;
        ThemeResourcesSystem themeResourcesSystem = null;
        if (ThemeCompatibility.isThemeEnabled()) {
            sIcons = ThemeResources.getTopLevelThemeResources(miuiResources, "icons");
            sLockscreen = ThemeResources.getTopLevelThemeResources(miuiResources, "lockscreen");
            sSuperWallpaperLockscreen = ThemeResources.getTopLevelThemeResources(miuiResources, ThemeResources.SUPER_WALLPAPER_LOCKSCREEN_NAME, ThemeResources.SUPER_WALLPAPER_PATH);
            sMiui = ThemeResources.getTopLevelThemeResources(miuiResources, ThemeResources.MIUI_NAME);
            boolean needProvisionTheme = ThemeResources.needProvisionTheme();
            int i = 0;
            while (true) {
                ThemeResources.MetaData[] metaDataArr = ThemeResources.THEME_PATHS;
                if (i >= metaDataArr.length) {
                    break;
                }
                if (needProvisionTheme || !ThemeResources.PROVISION_THEME_PATH.equals(metaDataArr[i].mThemePath)) {
                    themeResourcesSystem = new ThemeResourcesSystem(themeResourcesSystem, miuiResources, ThemeResources.FRAMEWORK_NAME, metaDataArr[i]);
                }
                i++;
            }
        } else {
            ThemeResourcesEmpty themeResourcesEmpty = ThemeResourcesEmpty.sInstance;
            sIcons = themeResourcesEmpty;
            sLockscreen = themeResourcesEmpty;
            sSuperWallpaperLockscreen = themeResourcesEmpty;
            sMiui = themeResourcesEmpty;
            themeResourcesSystem = new ThemeResourcesSystem(null, miuiResources, "FakeForEmpty", ThemeResources.THEME_PATHS[0]);
        }
        ThemeResources.sIsZygote = false;
        if ((miuiResources.getConfiguration().uiMode & 32) != 0) {
            themeResourcesSystem.setNightModeEnable(true);
        }
        return themeResourcesSystem;
    }

    private void initIcon() {
        if (sIconInited) {
            return;
        }
        sIconEnable = true;
        checkIconUpdate();
        sIconInited = true;
    }

    @Override // miui.content.res.ThemeResources
    public long checkUpdate() {
        if (!ThemeResources.sIsZygote) {
            super.checkUpdate();
            if (this.mIsTop) {
                checkIconUpdate();
                checkLockScreenUpdate();
                checkSuperLockScreenUpdate();
                long max = Math.max(this.mUpdatedTime, sUpdatedTimeLockscreen);
                this.mUpdatedTime = max;
                this.mUpdatedTime = Math.max(max, sMiui.checkUpdate());
            }
        }
        return this.mUpdatedTime;
    }

    public boolean containsAwesomeLockscreenEntry(String str) {
        return sLockscreen.hasThemeFile(ADVANCE_LOCKSCREEN_NAME + str);
    }

    public boolean containsSuperWallpaperLockscreenEntry(String str) {
        return sSuperWallpaperLockscreen.hasThemeFile(ADVANCE_LOCKSCREEN_NAME + str);
    }

    public InputStream getAwesomeLockscreenFileStream(String str, long[] jArr) {
        return sLockscreen.getThemeStream(ADVANCE_LOCKSCREEN_NAME + str, jArr);
    }

    public Bitmap getIconBitmap(String str) {
        MiuiResources.ThemeFileInfoOption themeFileInfoOption = new MiuiResources.ThemeFileInfoOption(true);
        boolean icon = getIcon(themeFileInfoOption, str);
        Bitmap bitmap = null;
        if (icon) {
            try {
                bitmap = BitmapFactory.decodeStream(themeFileInfoOption.outInputStream);
                if (bitmap != null) {
                    bitmap.setDensity(themeFileInfoOption.outDensity);
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            try {
                themeFileInfoOption.outInputStream.close();
            } catch (IOException unused) {
            }
            return bitmap;
        }
        return null;
    }

    public Bitmap[] getIconBitmaps(String str) {
        ArrayList arrayList = new ArrayList();
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf > 0) {
            String substring = str.substring(0, lastIndexOf);
            for (int i = 0; i < 5; i++) {
                Bitmap iconBitmap = getIconBitmap(substring + "/" + i + ".png");
                if (iconBitmap == null) {
                    break;
                }
                arrayList.add(iconBitmap);
            }
        }
        if (arrayList.size() > 0) {
            return (Bitmap[]) arrayList.toArray(new Bitmap[arrayList.size()]);
        }
        return null;
    }

    public InputStream getIconStream(String str, long[] jArr) {
        initIcon();
        return sIcons.getThemeStream(str, jArr);
    }

    public File getLockscreenWallpaper() {
        ThemeResources themeResources;
        File file = new File(this.mThemePath + ThemeResources.LOCKSCREEN_WALLPAPER_NAME);
        return (file.exists() || (themeResources = this.mWrapped) == null) ? file : ((ThemeResourcesSystem) themeResources).getLockscreenWallpaper();
    }

    public InputStream getSuperWallpaperLockscreenFileStream(String str, long[] jArr) {
        return sSuperWallpaperLockscreen.getThemeStream(ADVANCE_LOCKSCREEN_NAME + str, jArr);
    }

    @Override // miui.content.res.ThemeResources
    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption themeFileInfoOption, String str) {
        return ThemeResources.isMiuiResourceCookie(themeFileInfoOption.inCookie) ? sMiui.getThemeFile(themeFileInfoOption, str) : getThemeFileStreamSystem(themeFileInfoOption, str);
    }

    public boolean hasAwesomeLockscreen() {
        return sLockscreen.hasThemeFile("advance/manifest.xml");
    }

    public boolean hasIcon(String str) {
        initIcon();
        return sIcons.hasThemeFile(str);
    }

    public boolean hasSuperWallpaperLockscreen() {
        return sSuperWallpaperLockscreen.hasThemeFile("advance/manifest.xml");
    }

    @Override // miui.content.res.ThemeResources
    public void mergeThemeValues(String str, ThemeValues themeValues) {
        super.mergeThemeValues(str, themeValues);
        if (this.mIsTop) {
            sMiui.mergeThemeValues(str, themeValues);
            sLockscreen.mergeThemeValues(str, themeValues);
            sSuperWallpaperLockscreen.mergeThemeValues(str, themeValues);
        }
    }

    public void resetIcons() {
        initIcon();
        sIcons.checkUpdate();
    }

    public void resetLockscreen() {
        sLockscreen.checkUpdate();
        sSuperWallpaperLockscreen.checkUpdate();
    }

    @Override // miui.content.res.ThemeResources
    public void setNightModeEnable(boolean z) {
        super.setNightModeEnable(z);
        sMiui.setNightModeEnable(z);
    }
}
