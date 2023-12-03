package miui.content.res;

import android.app.MiuiThemeHelper;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.MiuiDisplayMetrics;
import android.util.TypedValue;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.miui.system.internal.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.xml.parsers.DocumentBuilderFactory;
import miui.cloud.sync.providers.CalendarSyncInfoProvider;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;
import miui.graphics.BitmapFactory;
import miui.graphics.BitmapUtil;
import miui.imagefilters.IImageFilter;
import miui.imagefilters.ImageData;
import miui.imagefilters.ImageFilterBuilder;
import miui.io.FileStat;
import miui.os.Build;
import miui.theme.IconCustomizerUtils;
import miui.theme.ThemeFileUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* loaded from: classes3.dex */
public class IconCustomizer {
    private static final String ANIMATING_ICONS_INNER_PATH = "animating_icons/";
    public static final String CUSTOMIZED_ICON_PATH;
    private static final String FANCY_ICONS_INNER_PATH = "fancy_icons/";
    private static final String FINAL_MOD_ICONS;
    private static final String FINAL_MOD_ICONS_MIUI_VERSION;
    private static final String ICON_NAME_SUFFIX = ".png";
    private static final String ICON_TRANSFORM_CONFIG = "transform_config.xml";
    private static final String LOG_TAG = "IconCustomizer";
    private static final String MIUI_MOD_BUILT_IN_ICONS = "/system/media/theme/miui_mod_icons/";
    private static final String MULTI_ANIM_ICONS_INNER_PATH = "layer_animating_icons/";
    private static Map<String, SoftReference<Drawable>> adaptiveIconCache = null;
    private static final int sAlphaShift = 24;
    private static final int sAlphaThreshold = 50;
    private static final Canvas sCanvas;
    private static final int sColorByteSize = 4;
    private static final int sColorShift = 8;
    private static int sCustomizedIconContentHeight = 0;
    private static int sCustomizedIconContentWidth = 0;
    private static int sCustomizedIconHeight = 0;
    private static int sCustomizedIconWidth = 0;
    private static int sCustomizedIrregularContentHeight = 0;
    private static int sCustomizedIrregularContentWidth = 0;
    private static volatile Paint sCutPaint = null;
    private static volatile Holder sHolder = null;
    private static Map<String, WeakReference<Bitmap>> sIconCache = null;
    private static volatile IconConfig sIconConfig = null;
    private static Map<String, String> sIconMapping = null;
    private static Matrix sIconTransformMatrix = null;
    private static boolean sIconTransformNeeded = false;
    private static final float sMaxContentRatio = 2.0f;
    private static Set<String> sModIconPkgWhiteList = null;
    private static Paint sPaintForTransformBitmap = null;
    private static final int sRGBMask = 16777215;
    private static Map<String, SoftReference<Bitmap>> sRawIconCache;
    private static volatile ThemeRuntimeManager sThemeRuntimeManager;

    /* loaded from: classes3.dex */
    public interface CustomizedIconsListener {
        void beforePrepareIcon(int i);

        void finishAllIcons();

        void finishPrepareIcon(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class Holder {
        boolean sModIconEnabled;

        private Holder() {
            this.sModIconEnabled = MiuiThemeHelper.isModIconEnabled();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class IconConfig {
        float mCameraX;
        float mCameraY;
        String mConfigIconMask;
        float mIconClipCornerRadius;
        float mIconClipHeight;
        float mIconClipWidth;
        IImageFilter.ImageFilterGroup mIconFilters;
        int mOverridedIrregularContentHeight;
        int mOverridedIrregularContentWidth;
        float[] mPointsMappingFrom;
        float[] mPointsMappingTo;
        float mRotateX;
        float mRotateY;
        float mRotateZ;
        float mScaleX;
        float mScaleY;
        float mSkewX;
        float mSkewY;
        boolean mSupportLayerIcon;
        float mTransX;
        float mTransY;
        boolean mUseModIcon;

        private IconConfig() {
            this.mScaleX = 1.0f;
            this.mScaleY = 1.0f;
            this.mUseModIcon = true;
            this.mSupportLayerIcon = false;
            this.mIconClipCornerRadius = -1.0f;
            this.mIconClipWidth = -1.0f;
            this.mIconClipHeight = -1.0f;
        }
    }

    static {
        String str = ThemeResources.THEME_MAGIC_PATH + "customized_icons/";
        FINAL_MOD_ICONS = str;
        FINAL_MOD_ICONS_MIUI_VERSION = str + "miui_version";
        CUSTOMIZED_ICON_PATH = str;
        Canvas canvas = new Canvas();
        sCanvas = canvas;
        sIconConfig = null;
        sIconTransformMatrix = null;
        sIconTransformNeeded = false;
        sHolder = null;
        canvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        sRawIconCache = new HashMap();
        sIconCache = new HashMap();
        sCustomizedIrregularContentWidth = -1;
        sCustomizedIrregularContentHeight = -1;
        sCustomizedIconContentWidth = -1;
        sCustomizedIconContentHeight = -1;
        sCustomizedIconWidth = -1;
        sCustomizedIconHeight = -1;
        HashSet hashSet = new HashSet();
        sModIconPkgWhiteList = hashSet;
        hashSet.add(CalendarSyncInfoProvider.AUTHORITY);
        sModIconPkgWhiteList.add("com.android.settings");
        sModIconPkgWhiteList.add("com.xiaomi.market");
        sModIconPkgWhiteList.add("com.duokan.reader");
        sModIconPkgWhiteList.add(ContactsSyncInfoProvider.AUTHORITY);
        sModIconPkgWhiteList.add("com.miui.notes");
        sModIconPkgWhiteList.add(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME);
        sModIconPkgWhiteList.add("com.miui.gallery");
        sModIconPkgWhiteList.add("com.xiaomi.scanner");
        sModIconPkgWhiteList.add("com.duokan.phone.remotecontroller");
        sModIconPkgWhiteList.add("com.android.phone");
        sModIconPkgWhiteList.add("com.android.camera");
        sModIconPkgWhiteList.add("com.miui.calculator");
        sModIconPkgWhiteList.add("com.miui.virtualsim");
        sModIconPkgWhiteList.add("com.android.soundrecorder");
        sModIconPkgWhiteList.add("com.android.browser");
        sModIconPkgWhiteList.add("com.android.thememanager");
        sModIconPkgWhiteList.add("com.miui.screenrecorder");
        sModIconPkgWhiteList.add("com.android.updater");
        sModIconPkgWhiteList.add("com.android.deskclock");
        sModIconPkgWhiteList.add("com.mi.health");
        sModIconPkgWhiteList.add("com.xiaomi.gamecenter");
        sModIconPkgWhiteList.add("com.miui.compass");
        sModIconPkgWhiteList.add("com.android.providers.downloads.ui");
        sModIconPkgWhiteList.add("com.miui.weather2");
        sModIconPkgWhiteList.add("com.miui.player");
        sModIconPkgWhiteList.add("com.miui.huanji");
        sModIconPkgWhiteList.add("com.miui.miservice");
        sModIconPkgWhiteList.add("com.android.fileexplorer");
        sModIconPkgWhiteList.add("com.mi.android.globalFileexplorer");
        sModIconPkgWhiteList.add("com.xiaomi.smarthome");
        sModIconPkgWhiteList.add("com.android.mms");
        sModIconPkgWhiteList.add("com.miui.voiceassist");
        sModIconPkgWhiteList.add("com.miui.video");
        sModIconPkgWhiteList.add("com.android.email");
        HashMap hashMap = new HashMap();
        sIconMapping = hashMap;
        hashMap.put("com.android.contacts.activities.TwelveKeyDialer.png", "com.android.contacts.TwelveKeyDialer.png");
        sIconMapping.put("com.miui.weather2.png", "com.miui.weather.png");
        sIconMapping.put("com.miui.gallery.png", "com.android.gallery.png");
        sIconMapping.put("com.android.gallery3d.png", "com.cooliris.media.png");
        sIconMapping.put("com.xiaomi.market.png", "com.miui.supermarket.png");
        sIconMapping.put("com.wali.miui.networkassistant.png", "com.android.monitor.png");
        sIconMapping.put("com.xiaomi.scanner.png", "com.miui.barcodescanner.png");
        sIconMapping.put("com.miui.calculator.png", "com.android.calculator2.png");
        sIconMapping.put("com.android.camera.CameraEntry.png", "com.android.camera.png");
        sIconMapping.put("com.htc.album.png", "com.miui.gallery.png");
        sIconMapping.put("com.htc.fm.activity.FMRadioMain.png", "com.miui.fmradio.png");
        sIconMapping.put("com.htc.fm.FMRadio.png", "com.miui.fmradio.png");
        sIconMapping.put("com.htc.fm.png", "com.miui.fmradio.png");
        sIconMapping.put("com.sec.android.app.camera.Camera.png", "com.android.camera.png");
        sIconMapping.put("com.sec.android.app.fm.png", "com.miui.fmradio.png");
        sIconMapping.put("com.android.gallery3d#com.android.camera.CameraLauncher.png", "com.android.camera.png");
        sIconMapping.put("com.android.hwcamera.png", "com.android.camera.png");
        sIconMapping.put("com.huawei.android.FMRadio.png", "com.miui.fmradio.png");
        sIconMapping.put("com.sonyericsson.android.camera.png", "com.android.camera.png");
        sIconMapping.put("com.sonyericsson.fmradio.png", "com.miui.fmradio.png");
        sIconMapping.put("com.motorola.Camera.Camera.png", "com.android.camera.png");
        sIconMapping.put("com.lge.camera.png", "com.android.camera.png");
        sIconMapping.put("com.oppo.camera.OppoCamera.png", "com.android.camera.png");
        sIconMapping.put("com.lenovo.scg#com.android.camera.CameraLauncher.png", "com.android.camera.png");
        sIconMapping.put("com.lenovo.fm.png", "com.miui.fmradio.png");
        sIconMapping.put("com.android.camera2#com.android.camera.CameraLauncher.png", "com.android.camera.png");
        adaptiveIconCache = new HashMap();
        sPaintForTransformBitmap = new Paint(3);
        sCutPaint = null;
    }

    private static int RGBToColor(int[] iArr) {
        return (((iArr[0] << 8) + iArr[1]) << 8) + iArr[2];
    }

    private static float[] calcBackgroundColor(int i, int i2, int i3, int[] iArr, Bitmap bitmap) {
        int saturation;
        int[] iArr2 = {0, 0, 0, 0};
        int[] iArr3 = {0, 0, 0};
        byte[] buffer = BitmapUtil.getBuffer(bitmap);
        int i4 = 0;
        int i5 = 0;
        while (i4 < i) {
            for (int i6 = 0; i6 < i2; i6++) {
                int i7 = i4 + i6;
                int i8 = iArr[i7];
                if ((sRGBMask & i8) > 0) {
                    iArr2[0] = iArr2[0] + ((i8 & 16711680) >> 16);
                    iArr2[1] = iArr2[1] + ((i8 & 65280) >> 8);
                    iArr2[2] = iArr2[2] + (i8 & 255);
                    i5++;
                }
                if (iArr2[3] == 0 && buffer != null) {
                    iArr2[3] = iArr2[3] + ((i8 >> 24) - buffer[(i7 << 2) + 3]);
                }
            }
            i4 += i3;
        }
        if (i5 > 0) {
            iArr2[0] = iArr2[0] / i5;
            iArr2[1] = iArr2[1] / i5;
            iArr2[2] = iArr2[2] / i5;
        }
        int RGBToColor = RGBToColor(iArr2);
        if (getSaturation(RGBToColor, iArr3) < 0.02d) {
            saturation = 0;
        } else {
            int[][] iArr4 = {new int[]{100, 110}, new int[]{190, 275}};
            int i9 = 0;
            for (int i10 = 0; i10 < 2; i10++) {
                i9 += iArr4[i10][1] - iArr4[i10][0];
            }
            float hue = (i9 * getHue(RGBToColor, iArr3)) / 360.0f;
            int i11 = 0;
            while (true) {
                if (i11 >= 2) {
                    break;
                }
                float f = iArr4[i11][1] - iArr4[i11][0];
                if (hue <= f) {
                    hue += iArr4[i11][0];
                    break;
                }
                hue -= f;
                i11++;
            }
            saturation = setSaturation(setValue(setHue(RGBToColor, hue, iArr3), 0.6f, iArr3), 0.4f, iArr3);
        }
        colorToRGB(saturation, iArr2);
        return new float[]{iArr2[0] / 255.0f, iArr2[1] / 255.0f, iArr2[2] / 255.0f, iArr2[3] / 255.0f};
    }

    public static void checkModIconsTimestamp() {
        String str;
        String str2 = FINAL_MOD_ICONS;
        File file = new File(str2);
        if (file.exists()) {
            try {
                if (new File(getMiuiModDownloadIconDir()).lastModified() <= FileStat.getCreatedTime(str2)) {
                    File file2 = new File(FINAL_MOD_ICONS_MIUI_VERSION);
                    if (file2.exists()) {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file2));
                        str = getCustomizedIconVersionContent().equals(bufferedReader.readLine()) ? null : "miui version update";
                        bufferedReader.close();
                    } else {
                        str = "miui version flag miss";
                    }
                } else {
                    str = "mod download icon update";
                }
            } catch (Exception e) {
                e.printStackTrace();
                str = "miui version read exception";
            }
            Log.d(LOG_TAG, "check time stamp: " + str);
            if (str != null) {
                ThemeNativeUtils.deleteContents(file.getPath());
                clearCache();
            }
        }
        if (file.exists()) {
            return;
        }
        ThemeFileUtils.mkdirs(file.getPath());
    }

    public static void clearCache() {
        clearCache(null);
    }

    public static void clearCache(String str) {
        if (str != null) {
            synchronized (sIconCache) {
                Iterator<Map.Entry<String, WeakReference<Bitmap>>> it = sIconCache.entrySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getKey().startsWith(str)) {
                        it.remove();
                    }
                }
            }
            synchronized (adaptiveIconCache) {
                Iterator<Map.Entry<String, SoftReference<Drawable>>> it2 = adaptiveIconCache.entrySet().iterator();
                while (it2.hasNext()) {
                    if (it2.next().getKey().startsWith(str)) {
                        it2.remove();
                    }
                }
            }
            return;
        }
        synchronized (sRawIconCache) {
            sRawIconCache.clear();
        }
        synchronized (sIconCache) {
            sIconCache.clear();
        }
        synchronized (adaptiveIconCache) {
            adaptiveIconCache.clear();
        }
        sIconConfig = null;
        sHolder = null;
        sIconTransformNeeded = false;
        sCustomizedIrregularContentWidth = -1;
        sCustomizedIrregularContentHeight = -1;
        sCustomizedIconContentWidth = -1;
        sCustomizedIconContentHeight = -1;
        sCustomizedIconWidth = -1;
        sCustomizedIconHeight = -1;
    }

    public static void clearCustomizedIcons(String str) {
        if (Build.IS_MIUI) {
            if (TextUtils.isEmpty(str)) {
                ThemeNativeUtils.deleteContents(FINAL_MOD_ICONS);
                clearCache();
                return;
            }
            String[] list = new File(FINAL_MOD_ICONS).list();
            if (list != null) {
                for (String str2 : list) {
                    if (str2.startsWith(str)) {
                        ThemeNativeUtils.remove(FINAL_MOD_ICONS + str2);
                    }
                }
            }
            clearCache(str);
        }
    }

    private static void colorToRGB(int i, int[] iArr) {
        iArr[0] = (16711680 & i) >> 16;
        iArr[1] = (65280 & i) >> 8;
        iArr[2] = i & 255;
    }

    private static Bitmap composeIcon(Bitmap bitmap, Bitmap bitmap2, Bitmap bitmap3, Bitmap bitmap4, Bitmap bitmap5) {
        Canvas canvas;
        int i;
        int[] iArr;
        float[] fArr;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int byteCount = bitmap.getByteCount() / 4;
        int rowBytes = bitmap.getRowBytes() / 4;
        int[] iArr2 = new int[byteCount];
        bitmap.getPixels(iArr2, 0, rowBytes, 0, 0, width, height);
        bitmap.recycle();
        Bitmap createBitmap = Bitmap.createBitmap(getCustomizedIconWidth(), getCustomizedIconHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(createBitmap);
        canvas2.drawBitmap(iArr2, 0, rowBytes, 0, 0, width, height, true, (Paint) null);
        if (bitmap2 != null) {
            if (sCutPaint == null) {
                synchronized (IconCustomizer.class) {
                    if (sCutPaint == null) {
                        Paint paint = new Paint();
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                        sCutPaint = paint;
                    }
                }
            }
            canvas2.drawBitmap(bitmap2, 0.0f, 0.0f, sCutPaint);
            canvas = canvas2;
            createBitmap.getPixels(iArr2, 0, rowBytes, 0, 0, width, height);
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        } else {
            canvas = canvas2;
        }
        if (bitmap3 == null || bitmap3.getByteCount() / 4 != byteCount) {
            i = rowBytes;
            iArr = iArr2;
            fArr = null;
        } else {
            i = rowBytes;
            iArr = iArr2;
            fArr = calcBackgroundColor(byteCount, width, i, iArr, bitmap3);
        }
        if (fArr != null && fArr[3] != 0.0f) {
            Paint paint2 = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.set(new float[]{fArr[0], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, fArr[1], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, fArr[2], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f});
            paint2.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(bitmap3, 0.0f, 0.0f, paint2);
        }
        if (((fArr != null && fArr[3] != 0.0f) || fArr == null) && bitmap4 != null) {
            canvas.drawBitmap(bitmap4, 0.0f, 0.0f, (Paint) null);
        }
        canvas.drawBitmap(iArr, 0, i, 0, 0, width, height, true, (Paint) null);
        if (bitmap5 != null) {
            canvas.drawBitmap(bitmap5, 0.0f, 0.0f, (Paint) null);
        }
        return createBitmap;
    }

    private static Bitmap composeIconWithTransform(Bitmap bitmap, String str, String str2, String str3, String str4) {
        ensureIconConfigLoaded();
        if (sIconConfig.mIconFilters != null) {
            bitmap = ImageData.imageDataToBitmap(sIconConfig.mIconFilters.processAll(bitmap));
        }
        if (sIconTransformNeeded) {
            bitmap = transformBitmap(bitmap, sIconTransformMatrix);
        }
        return composeIcon(bitmap, getRawIcon(str), getRawIcon(str2), getRawIcon(str3), getRawIcon(str4));
    }

    private static Bitmap drawableToBitmap(Drawable drawable, float f) {
        return drawableToBitmap(drawable, f, false);
    }

    private static Bitmap drawableToBitmap(Drawable drawable, float f, boolean z) {
        Bitmap createBitmap;
        Canvas canvas = sCanvas;
        synchronized (canvas) {
            int customizedIconWidth = getCustomizedIconWidth();
            int customizedIconHeight = getCustomizedIconHeight();
            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            if (drawable instanceof PaintDrawable) {
                PaintDrawable paintDrawable = (PaintDrawable) drawable;
                paintDrawable.setIntrinsicWidth(customizedIconWidth);
                paintDrawable.setIntrinsicHeight(customizedIconHeight);
            } else if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                int density = bitmapDrawable.getBitmap().getDensity();
                if (Build.VERSION.SDK_INT >= 28) {
                    if (density != displayMetrics.densityDpi) {
                        bitmapDrawable.setTargetDensity(density);
                        f = getScaleRatio(drawable, false);
                        Log.d(LOG_TAG, "BitmapDensity = " + density + "  setTargetDensity = " + density);
                    }
                } else if (density == 0) {
                    bitmapDrawable.setTargetDensity(displayMetrics);
                }
            }
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (z) {
                f = 1.0f;
                intrinsicWidth = customizedIconWidth;
                intrinsicHeight = customizedIconHeight;
            }
            drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
            createBitmap = Bitmap.createBitmap(displayMetrics, customizedIconWidth, customizedIconHeight, Bitmap.Config.ARGB_8888);
            canvas.setBitmap(createBitmap);
            canvas.save();
            canvas.translate((customizedIconWidth - (intrinsicWidth * f)) / sMaxContentRatio, (customizedIconHeight - (intrinsicHeight * f)) / sMaxContentRatio);
            canvas.scale(f, f);
            drawable.draw(canvas);
            canvas.restore();
            canvas.setBitmap(null);
        }
        return createBitmap;
    }

    private static void ensureIconConfigLoaded() {
        if (sIconConfig == null) {
            synchronized (IconCustomizer.class) {
                if (sIconConfig == null) {
                    IconConfig loadIconConfig = loadIconConfig();
                    sIconTransformMatrix = makeIconMatrix(loadIconConfig);
                    sIconConfig = loadIconConfig;
                }
            }
        }
    }

    public static void ensureMiuiVersionFlagExist(Context context) {
        String str = FINAL_MOD_ICONS_MIUI_VERSION;
        if (new File(str).exists()) {
            return;
        }
        if (context != null && !ThemeResources.FRAMEWORK_PACKAGE.equals(context.getPackageName())) {
            str = context.getFileStreamPath("customized_icons_version").getAbsolutePath();
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(str));
            bufferedWriter.write(getCustomizedIconVersionContent());
            bufferedWriter.close();
        } catch (Exception unused) {
        }
        String str2 = FINAL_MOD_ICONS_MIUI_VERSION;
        if (str2.equals(str)) {
            return;
        }
        ThemeNativeUtils.copy(str, str2);
        ThemeNativeUtils.remove(str);
        ThemeNativeUtils.updateFilePermissionWithThemeContext(str2);
    }

    public static BitmapDrawable generateIconStyleDrawable(Drawable drawable) {
        return generateIconStyleDrawable(drawable, false);
    }

    public static BitmapDrawable generateIconStyleDrawable(Drawable drawable, Bitmap bitmap, Bitmap bitmap2, Bitmap bitmap3, Bitmap bitmap4, boolean z) {
        return getDrawble(composeIcon(drawableToBitmap(drawable, getScaleRatio(drawable, z)), bitmap, bitmap2, bitmap3, bitmap4));
    }

    public static BitmapDrawable generateIconStyleDrawable(Drawable drawable, boolean z) {
        Bitmap drawableToBitmap = drawableToBitmap(drawable, getScaleRatio(drawable, z));
        if (miui.os.Build.IS_MIUI) {
            drawableToBitmap = composeIconWithTransform(drawableToBitmap, "icon_mask.png", "icon_background.png", "icon_pattern.png", "icon_border.png");
        }
        return getDrawble(drawableToBitmap);
    }

    public static BitmapDrawable generateShortcutIconDrawable(Drawable drawable) {
        Bitmap drawableToBitmap = drawableToBitmap(drawable, getScaleRatio(drawable, false));
        if (miui.os.Build.IS_MIUI) {
            drawableToBitmap = composeIconWithTransform(drawableToBitmap, "icon_mask.png", null, "icon_shortcut.png", "icon_shortcut_arrow.png");
        }
        return getDrawble(drawableToBitmap);
    }

    private static Drawable getAdaptiveIcon(Context context, String str, String str2, int i, ApplicationInfo applicationInfo, boolean z) {
        ensureIconConfigLoaded();
        List<String> iconNames = getIconNames(str, str2);
        String str3 = iconNames.get(0);
        Drawable adaptiveIconFromCache = getAdaptiveIconFromCache(str3);
        if (adaptiveIconFromCache == null) {
            adaptiveIconFromCache = IconCustomizerUtils.transformToAdaptiveIcon(getIconBitmapsFromTheme(iconNames));
        }
        if (adaptiveIconFromCache == null && i != 0 && applicationInfo != null && (i != applicationInfo.icon || z)) {
            adaptiveIconFromCache = IconCustomizerUtils.getAdaptiveIconFromPackage(context, str, i, applicationInfo);
        }
        if (adaptiveIconFromCache == null && sIconConfig.mUseModIcon && isModIconEnabledForPackageName(str)) {
            adaptiveIconFromCache = IconCustomizerUtils.transformToAdaptiveIcon(getMiuiModIconBitamps(iconNames));
        }
        if (adaptiveIconFromCache != null) {
            synchronized (adaptiveIconCache) {
                adaptiveIconCache.put(str3, new SoftReference<>(adaptiveIconFromCache));
            }
        }
        return adaptiveIconFromCache;
    }

    private static Drawable getAdaptiveIconFromCache(String str) {
        SoftReference<Drawable> softReference;
        Drawable drawable;
        Drawable.ConstantState constantState;
        synchronized (adaptiveIconCache) {
            softReference = adaptiveIconCache.get(str);
        }
        if (softReference == null || (drawable = softReference.get()) == null || (constantState = drawable.getConstantState()) == null) {
            return null;
        }
        return constantState.newDrawable(Resources.getSystem());
    }

    public static String getAnimatingIconRelativePath(PackageItemInfo packageItemInfo, String str, String str2) {
        return getIconRelativePath(packageItemInfo, str, str2, ANIMATING_ICONS_INNER_PATH, "fancy/manifest.xml");
    }

    public static String getConfigIconMaskValue() {
        ensureIconConfigLoaded();
        return sIconConfig.mConfigIconMask;
    }

    private static float getContentRatio(Drawable drawable) {
        ensureIconConfigLoaded();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap.getConfig() != Bitmap.Config.ARGB_8888) {
                return -1.0f;
            }
            int edgePosition = getEdgePosition(bitmap, true, false);
            int edgePosition2 = getEdgePosition(bitmap, true, true);
            int edgePosition3 = getEdgePosition(bitmap, false, false);
            int edgePosition4 = getEdgePosition(bitmap, false, true);
            int customizedIconContentWidth = getCustomizedIconContentWidth();
            int customizedIconContentHeight = getCustomizedIconContentHeight();
            float min = Math.min(drawable.getIntrinsicWidth(), (edgePosition4 - edgePosition3) + 1);
            float min2 = Math.min(drawable.getIntrinsicHeight(), (edgePosition2 - edgePosition) + 1);
            if (min >= min2 * 0.8f && min2 >= 0.8f * min && isRegularShape(bitmap, edgePosition3, edgePosition, edgePosition4, edgePosition2)) {
                return Math.min(customizedIconContentWidth / min, customizedIconContentHeight / min2);
            }
            return Math.min(getCustomizedIrregularContentWidth() / min, getCustomizedIrregularContentHeight() / min2);
        }
        return -1.0f;
    }

    public static BitmapDrawable getCustomizedIcon(Context context, String str) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        return getCustomizedIconInner(context, arrayList, null, true);
    }

    public static BitmapDrawable getCustomizedIcon(Context context, String str, String str2, int i, ApplicationInfo applicationInfo, boolean z) {
        BitmapDrawable customizedNormalIcon = getCustomizedNormalIcon(context, str, str2, i, applicationInfo, z);
        return (customizedNormalIcon != null || i == 0 || applicationInfo == null || str2 == null) ? customizedNormalIcon : getCustomizedIcon(context, str, null, i, applicationInfo, true);
    }

    public static BitmapDrawable getCustomizedIcon(Context context, String str, String str2, Drawable drawable) {
        ensureIconConfigLoaded();
        return getCustomizedIconInner(context, getIconNames(str, str2), drawable, sIconConfig.mUseModIcon);
    }

    private static int getCustomizedIconContentHeight() {
        int i = sCustomizedIconContentHeight;
        if (i == -1) {
            int dimensionPixelSize = Resources.getSystem().getDimensionPixelSize(R.dimen.customizer_icon_content_size);
            sCustomizedIconContentHeight = dimensionPixelSize;
            return dimensionPixelSize;
        }
        return i;
    }

    private static int getCustomizedIconContentWidth() {
        int i = sCustomizedIconContentWidth;
        if (i == -1) {
            int dimensionPixelSize = Resources.getSystem().getDimensionPixelSize(R.dimen.customizer_icon_content_size);
            sCustomizedIconContentWidth = dimensionPixelSize;
            return dimensionPixelSize;
        }
        return i;
    }

    public static BitmapDrawable getCustomizedIconFromCache(String str, String str2) {
        return getDrawableFromMemoryCache(getFileName(str, str2));
    }

    public static int getCustomizedIconHeight() {
        int i = sCustomizedIconHeight;
        if (i == -1) {
            int dimensionPixelSize = Resources.getSystem().getDimensionPixelSize(R.dimen.customizer_icon_size);
            sCustomizedIconHeight = dimensionPixelSize;
            return dimensionPixelSize;
        }
        return i;
    }

    private static BitmapDrawable getCustomizedIconInner(Context context, List<String> list, Drawable drawable, boolean z) {
        String str = list.get(0);
        BitmapDrawable drawableFromMemoryCache = getDrawableFromMemoryCache(str);
        if (drawableFromMemoryCache != null) {
            return drawableFromMemoryCache;
        }
        BitmapDrawable drawableFromStaticCache = getDrawableFromStaticCache(str);
        for (int i = 0; drawableFromStaticCache == null && i < list.size(); i++) {
            drawableFromStaticCache = getDrawble(getIconFromTheme(list.get(i)));
        }
        if (drawableFromStaticCache == null) {
            Bitmap bitmap = null;
            if (z && context != null && isModIconEnabledForPackageName(context.getPackageName())) {
                for (int i2 = 0; bitmap == null && i2 < list.size(); i2++) {
                    bitmap = getMiuiModIcon(list.get(i2));
                }
            }
            drawableFromStaticCache = transToMiuiModIcon(context, bitmap, drawable, str);
        }
        if (drawableFromStaticCache != null) {
            synchronized (sIconCache) {
                sIconCache.put(str, new WeakReference<>(drawableFromStaticCache.getBitmap()));
            }
        }
        return drawableFromStaticCache;
    }

    private static String getCustomizedIconVersionContent() {
        return Build.VERSION.INCREMENTAL + "_" + MiuiDisplayMetrics.DENSITY_DEVICE;
    }

    public static int getCustomizedIconWidth() {
        int i = sCustomizedIconWidth;
        if (i == -1) {
            int dimensionPixelSize = Resources.getSystem().getDimensionPixelSize(R.dimen.customizer_icon_size);
            sCustomizedIconWidth = dimensionPixelSize;
            return dimensionPixelSize;
        }
        return i;
    }

    private static int getCustomizedIrregularContentHeight() {
        int i = sCustomizedIrregularContentHeight;
        if (i == -1) {
            i = sIconConfig.mOverridedIrregularContentHeight > 0 ? sIconConfig.mOverridedIrregularContentHeight : Resources.getSystem().getDimensionPixelSize(R.dimen.customizer_irregular_content_size);
            sCustomizedIrregularContentHeight = i;
        }
        return i;
    }

    private static int getCustomizedIrregularContentWidth() {
        int i = sCustomizedIrregularContentWidth;
        if (i == -1) {
            i = sIconConfig.mOverridedIrregularContentWidth > 0 ? sIconConfig.mOverridedIrregularContentWidth : Resources.getSystem().getDimensionPixelSize(R.dimen.customizer_irregular_content_size);
            sCustomizedIrregularContentWidth = i;
        }
        return i;
    }

    private static BitmapDrawable getCustomizedNormalIcon(Context context, String str, String str2, int i, ApplicationInfo applicationInfo, boolean z) {
        ensureIconConfigLoaded();
        List<String> iconNames = getIconNames(str, str2);
        PackageManager packageManager = context.getPackageManager();
        String str3 = iconNames.get(0);
        BitmapDrawable customizedIconInner = getCustomizedIconInner(context, iconNames, null, sIconConfig.mUseModIcon);
        if (customizedIconInner != null) {
            return customizedIconInner;
        }
        BitmapDrawable transToMiuiModIcon = transToMiuiModIcon(context, null, (i == 0 || applicationInfo == null || (i == applicationInfo.icon && !z)) ? null : packageManager.getDrawable(str, i, applicationInfo), str3);
        if (transToMiuiModIcon != null) {
            synchronized (sIconCache) {
                sIconCache.put(str3, new WeakReference<>(transToMiuiModIcon.getBitmap()));
            }
        }
        return transToMiuiModIcon;
    }

    private static int getDimension(String str) {
        return TypedValue.complexToDimensionPixelSize(MiuiThemeHelper.parseDimension(str).intValue(), Resources.getSystem().getDisplayMetrics());
    }

    private static BitmapDrawable getDrawableFromMemoryCache(String str) {
        WeakReference<Bitmap> weakReference;
        synchronized (sIconCache) {
            weakReference = sIconCache.get(str);
        }
        if (weakReference != null) {
            return getDrawble(weakReference.get());
        }
        return null;
    }

    private static BitmapDrawable getDrawableFromStaticCache(String str) {
        Bitmap bitmap;
        String str2 = FINAL_MOD_ICONS + str;
        File file = new File(str2);
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(str2);
            if (bitmap == null) {
                file.delete();
            }
        } else {
            bitmap = null;
        }
        return getDrawble(bitmap);
    }

    private static BitmapDrawable getDrawble(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }

    private static int getEdgePosition(Bitmap bitmap, boolean z, boolean z2) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int rowBytes = bitmap.getRowBytes();
        byte[] buffer = BitmapUtil.getBuffer(bitmap);
        int i = !z2 ? -1 : width;
        int i2 = !z2 ? -1 : height;
        int i3 = z2 ? -1 : 1;
        int i4 = z2 ? -1 : 1;
        if (buffer != null) {
            int i5 = 0;
            while (i5 == 0) {
                if (!z) {
                    i += i3;
                    if (i < 0 || i >= width) {
                        break;
                    }
                    i2 = 0;
                    while (i2 < height) {
                        if ((buffer[(i2 * rowBytes) + (i << 2) + 3] & 255) > 50) {
                            i5++;
                        }
                        i2++;
                    }
                } else {
                    i2 += i4;
                    if (i2 < 0 || i2 >= height) {
                        break;
                    }
                    i = 0;
                    while (i < width) {
                        if ((buffer[(i2 * rowBytes) + (i << 2) + 3] & 255) > 50) {
                            i5++;
                        }
                        i++;
                    }
                }
            }
        }
        return z ? i2 : i;
    }

    public static String getFancyIconRelativePath(PackageItemInfo packageItemInfo, String str, String str2) {
        return getIconRelativePath(packageItemInfo, str, str2, FANCY_ICONS_INNER_PATH, "manifest.xml");
    }

    private static String getFileName(String str, String str2) {
        if (str2 == null) {
            return str + ICON_NAME_SUFFIX;
        } else if (str2.startsWith(str)) {
            return str2 + ICON_NAME_SUFFIX;
        } else {
            return str + '#' + str2 + ICON_NAME_SUFFIX;
        }
    }

    private static float getHue(int i, int[] iArr) {
        colorToRGB(i, iArr);
        int i2 = 0;
        int min = Math.min(iArr[0], Math.min(iArr[1], iArr[2]));
        int max = Math.max(iArr[0], Math.max(iArr[1], iArr[2])) - min;
        if (max == 0) {
            return 0.0f;
        }
        while (i2 < 2 && min != iArr[i2]) {
            i2++;
        }
        int i3 = (i2 + 1) % 3;
        float f = max;
        return (i3 * 120) + (((iArr[(i2 + 2) % 3] - min) * 60.0f) / f) + (((r1 - iArr[i3]) * 60.0f) / f);
    }

    public static Drawable getIcon(Context context, String str, String str2, int i, ApplicationInfo applicationInfo, boolean z) {
        ensureIconConfigLoaded();
        Drawable adaptiveIcon = (Build.VERSION.SDK_INT <= 28 || !sIconConfig.mSupportLayerIcon) ? null : getAdaptiveIcon(context, str, str2, i, applicationInfo, z);
        if (adaptiveIcon == null) {
            adaptiveIcon = getCustomizedNormalIcon(context, str, str2, i, applicationInfo, z);
        }
        return (adaptiveIcon != null || i == 0 || applicationInfo == null || str2 == null) ? adaptiveIcon : getIcon(context, str, null, i, applicationInfo, true);
    }

    private static Bitmap[] getIconBitmapsFromTheme(List<String> list) {
        Bitmap[] bitmapArr = null;
        if (ThemeResources.getSystem() != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                bitmapArr = ThemeResources.getSystem().getIconBitmaps(list.get(i));
                if (bitmapArr != null) {
                    break;
                }
            }
        }
        return bitmapArr;
    }

    public static float getIconClipCornerRadius() {
        ensureIconConfigLoaded();
        return sIconConfig.mIconClipCornerRadius;
    }

    public static float getIconClipHeight() {
        ensureIconConfigLoaded();
        return sIconConfig.mIconClipHeight;
    }

    public static float getIconClipWidth() {
        ensureIconConfigLoaded();
        return sIconConfig.mIconClipWidth;
    }

    private static Bitmap getIconFromMemoryCache(String str) {
        SoftReference<Bitmap> softReference;
        synchronized (sRawIconCache) {
            softReference = sRawIconCache.get(str);
        }
        if (softReference != null) {
            return softReference.get();
        }
        return null;
    }

    private static Bitmap getIconFromTheme(String str) {
        if (ThemeResources.getSystem() == null) {
            return null;
        }
        return scaleBitmap(ThemeResources.getSystem().getIconBitmap(str));
    }

    private static List<String> getIconNames(String str, String str2) {
        ArrayList arrayList = new ArrayList();
        String fileName = getFileName(str, str2);
        String str3 = sIconMapping.get(fileName);
        if (str3 != null) {
            arrayList.add(str3);
        }
        arrayList.add(fileName);
        if (str2 != null && !str2.startsWith(str)) {
            arrayList.add(String.format("%s.png", str2));
        }
        return arrayList;
    }

    private static String getIconRelativePath(PackageItemInfo packageItemInfo, String str, String str2, String str3, String str4) {
        List<String> iconNames = getIconNames(str, str2);
        if (str2 != null && packageItemInfo != null && packageItemInfo.icon == 0) {
            iconNames.add(str);
        }
        for (int i = 0; i < iconNames.size(); i++) {
            String str5 = iconNames.get(i);
            if (str5.endsWith(ICON_NAME_SUFFIX)) {
                str5 = str5.substring(0, str5.length() - 4);
            }
            String str6 = str3 + str5 + "/";
            if (ThemeResources.getSystem() != null) {
                if (ThemeResources.getSystem().hasIcon(str6 + str4)) {
                    return str6;
                }
            }
        }
        return null;
    }

    public static String getMamlAdaptiveIconRelativePath(PackageItemInfo packageItemInfo, String str, String str2) {
        return getIconRelativePath(packageItemInfo, str, str2, MULTI_ANIM_ICONS_INNER_PATH, "config.xml");
    }

    private static Bitmap[] getMiuiModBitmaps(String str) {
        Bitmap decodeFile;
        ArrayList arrayList = new ArrayList();
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf > 0) {
            File file = new File(MIUI_MOD_BUILT_IN_ICONS + str.substring(0, lastIndexOf));
            if (file.exists() && file.isDirectory()) {
                String absolutePath = file.getAbsolutePath();
                for (int i = 0; i < 5 && (decodeFile = BitmapFactory.decodeFile(String.format("%s/%d.png", absolutePath, Integer.valueOf(i)))) != null; i++) {
                    arrayList.add(decodeFile);
                }
            }
        }
        if (arrayList.size() > 0) {
            return (Bitmap[]) arrayList.toArray(new Bitmap[arrayList.size()]);
        }
        return null;
    }

    private static String getMiuiModDownloadIconDir() {
        return "/data/user/" + UserHandle.myUserId() + "/com.xiaomi.market/files/miui_mod_icons/";
    }

    private static Bitmap getMiuiModIcon(String str) {
        if (miui.os.Build.IS_CU_CUSTOMIZATION_TEST) {
            if ("com.android.stk.png".equals(str)) {
                str = "com.android.stk.cu.png";
            } else if ("com.android.stk.StkLauncherActivity2.png".equals(str)) {
                str = "com.android.stk.cu.2.png";
            }
        }
        File file = new File(MIUI_MOD_BUILT_IN_ICONS + str);
        if (file.exists()) {
            return scaleBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }
        return null;
    }

    private static Bitmap[] getMiuiModIconBitamps(List<String> list) {
        int size = list.size();
        Bitmap[] bitmapArr = null;
        for (int i = 0; i < size; i++) {
            bitmapArr = getMiuiModBitmaps(list.get(i));
            if (bitmapArr != null) {
                break;
            }
        }
        return bitmapArr;
    }

    public static Bitmap getRawIcon(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Bitmap iconFromMemoryCache = getIconFromMemoryCache(str);
        if (iconFromMemoryCache == null) {
            iconFromMemoryCache = getIconFromTheme(str);
            if (iconFromMemoryCache == null) {
                iconFromMemoryCache = getMiuiModIcon(str);
            }
            if (iconFromMemoryCache != null) {
                synchronized (sRawIconCache) {
                    sRawIconCache.put(str, new SoftReference<>(iconFromMemoryCache));
                }
            }
        }
        return iconFromMemoryCache;
    }

    public static BitmapDrawable getRawIconDrawable(String str) {
        BitmapDrawable drawableFromMemoryCache = getDrawableFromMemoryCache(str);
        if (drawableFromMemoryCache == null && (drawableFromMemoryCache = getDrawble(getRawIcon(str))) != null) {
            synchronized (sIconCache) {
                sIconCache.put(str, new WeakReference<>(drawableFromMemoryCache.getBitmap()));
            }
        }
        return drawableFromMemoryCache;
    }

    private static float getSaturation(int i, int[] iArr) {
        colorToRGB(i, iArr);
        int min = Math.min(iArr[0], Math.min(iArr[1], iArr[2]));
        int max = Math.max(iArr[0], Math.max(iArr[1], iArr[2]));
        return (max == 0 || max == min) ? i : ((max - min) * 1.0f) / max;
    }

    private static float getScaleRatio(Drawable drawable, boolean z) {
        if (drawable instanceof PaintDrawable) {
            return 1.0f;
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            return 1.0f;
        }
        float f = intrinsicWidth;
        float customizedIconContentWidth = getCustomizedIconContentWidth() / f;
        float f2 = intrinsicHeight;
        float customizedIconContentHeight = getCustomizedIconContentHeight() / f2;
        if (z) {
            return Math.max(getCustomizedIconWidth() / f, getCustomizedIconHeight() / f2);
        }
        float contentRatio = getContentRatio(drawable);
        Log.d(LOG_TAG, "Content Ratio = " + contentRatio);
        return contentRatio > 0.0f ? contentRatio : Math.min(1.0f, Math.min(customizedIconContentWidth, customizedIconContentHeight));
    }

    private static ThemeRuntimeManager getServiceManager(Context context) {
        if (sThemeRuntimeManager == null) {
            synchronized (IconCustomizer.class) {
                if (sThemeRuntimeManager == null) {
                    Context applicationContext = context.getApplicationContext();
                    if (applicationContext != null) {
                        context = applicationContext;
                    }
                    sThemeRuntimeManager = new ThemeRuntimeManager(context);
                }
            }
        }
        return sThemeRuntimeManager;
    }

    private static float getValue(int i, int[] iArr) {
        colorToRGB(i, iArr);
        return (Math.max(iArr[0], Math.max(iArr[1], iArr[2])) * 1.0f) / 255.0f;
    }

    public static double hdpiIconSizeToCurrent(double d) {
        return (d * getCustomizedIconWidth()) / 90.0d;
    }

    public static float hdpiIconSizeToCurrent(float f) {
        return (f * getCustomizedIconWidth()) / 90.0f;
    }

    public static int hdpiIconSizeToCurrent(int i) {
        return (int) (((i * getCustomizedIconWidth()) / 90.0f) + 0.5f);
    }

    private static boolean isAdaptiveIconDrawale(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 26 && drawable != null) {
            try {
                return Class.forName("android.graphics.drawable.AdaptiveIconDrawable").isInstance(drawable);
            } catch (Exception unused) {
                Log.e(LOG_TAG, "check adaptive icon fail");
            }
        }
        return false;
    }

    private static boolean isModIconEnabled() {
        if (sHolder == null) {
            synchronized (IconCustomizer.class) {
                if (sHolder == null) {
                    sHolder = new Holder();
                }
            }
        }
        return sHolder.sModIconEnabled;
    }

    public static boolean isModIconEnabledForPackageName(String str) {
        return sModIconPkgWhiteList.contains(str) || isModIconEnabled();
    }

    private static boolean isRegularShape(Bitmap bitmap, int i, int i2, int i3, int i4) {
        int rowBytes = bitmap.getRowBytes();
        byte[] buffer = BitmapUtil.getBuffer(bitmap);
        if (buffer == null) {
            return true;
        }
        int i5 = i3 - i;
        for (int i6 = (i5 / 4) + i; i6 < ((i5 * 3) / 4) + i; i6++) {
            int i7 = i6 << 2;
            if ((buffer[(i2 * rowBytes) + i7 + 3] & 255) < 50 || (buffer[(i4 * rowBytes) + i7 + 3] & 255) < 50) {
                return false;
            }
        }
        int i8 = i4 - i2;
        for (int i9 = (i8 / 4) + i2; i9 < ((i8 * 3) / 4) + i2; i9++) {
            int i10 = i9 * rowBytes;
            if ((buffer[(i << 2) + i10 + 3] & 255) < 50 || (buffer[i10 + (i3 << 2) + 3] & 255) < 50) {
                return false;
            }
        }
        return true;
    }

    private static IconConfig loadIconConfig() {
        IconConfig iconConfig = new IconConfig();
        if (!miui.os.Build.IS_MIUI || ThemeResources.getSystem() == null) {
            Log.w(LOG_TAG, "can't load ThemeResources");
            return iconConfig;
        }
        InputStream iconStream = ThemeResources.getSystem().getIconStream(ICON_TRANSFORM_CONFIG, null);
        if (iconStream == null) {
            Log.w(LOG_TAG, "can't load transform_config.xml");
            return iconConfig;
        }
        try {
            Element documentElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(iconStream).getDocumentElement();
            try {
                iconStream.close();
                if (documentElement != null) {
                    NodeList childNodes = documentElement.getChildNodes();
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        try {
                            if (childNodes.item(i).getNodeType() == 1) {
                                Element element = (Element) childNodes.item(i);
                                String tagName = element.getTagName();
                                if ("IconFilters".equals(tagName)) {
                                    iconConfig.mIconFilters = loadIconFilters(element.getChildNodes());
                                } else if ("PointsMapping".equals(tagName)) {
                                    NodeList childNodes2 = element.getChildNodes();
                                    ArrayList arrayList = new ArrayList();
                                    ArrayList arrayList2 = new ArrayList();
                                    for (int i2 = 0; i2 < childNodes2.getLength(); i2++) {
                                        if (childNodes2.item(i2).getNodeType() == 1) {
                                            Element element2 = (Element) childNodes2.item(i2);
                                            if ("Point".equals(element2.getNodeName())) {
                                                arrayList.add(Float.valueOf(hdpiIconSizeToCurrent(Float.parseFloat(element2.getAttribute("fromX")))));
                                                arrayList.add(Float.valueOf(hdpiIconSizeToCurrent(Float.parseFloat(element2.getAttribute("fromY")))));
                                                arrayList2.add(Float.valueOf(hdpiIconSizeToCurrent(Float.parseFloat(element2.getAttribute("toX")))));
                                                arrayList2.add(Float.valueOf(hdpiIconSizeToCurrent(Float.parseFloat(element2.getAttribute("toY")))));
                                            }
                                        }
                                    }
                                    int size = arrayList.size();
                                    if (size > 0 && size <= 8) {
                                        iconConfig.mPointsMappingFrom = new float[size];
                                        iconConfig.mPointsMappingTo = new float[size];
                                        for (int i3 = 0; i3 < size; i3++) {
                                            iconConfig.mPointsMappingFrom[i3] = ((Float) arrayList.get(i3)).floatValue();
                                            iconConfig.mPointsMappingTo[i3] = ((Float) arrayList2.get(i3)).floatValue();
                                        }
                                    }
                                } else if ("Config".equals(tagName)) {
                                    String attribute = element.getAttribute("name");
                                    String attribute2 = element.getAttribute("value");
                                    if ("UseModIcon".equalsIgnoreCase(attribute)) {
                                        iconConfig.mUseModIcon = Boolean.parseBoolean(attribute2);
                                    } else if ("SupportLayerIcon".equalsIgnoreCase(attribute)) {
                                        iconConfig.mSupportLayerIcon = Boolean.parseBoolean(attribute2);
                                    } else if ("ConfigIconMask".equalsIgnoreCase(attribute)) {
                                        iconConfig.mConfigIconMask = attribute2;
                                    } else if ("IconClipCornerRadius".equalsIgnoreCase(attribute)) {
                                        iconConfig.mIconClipCornerRadius = Float.parseFloat(attribute2);
                                    } else if ("IconClipWidth".equalsIgnoreCase(attribute)) {
                                        iconConfig.mIconClipWidth = Float.parseFloat(attribute2);
                                    } else if ("IconClipHeight".equalsIgnoreCase(attribute)) {
                                        iconConfig.mIconClipHeight = Float.parseFloat(attribute2);
                                    }
                                } else if ("ScaleX".equals(tagName)) {
                                    iconConfig.mScaleX = Float.parseFloat(element.getAttribute("value"));
                                } else if ("ScaleY".equals(tagName)) {
                                    iconConfig.mScaleY = Float.parseFloat(element.getAttribute("value"));
                                } else if ("SkewX".equals(tagName)) {
                                    iconConfig.mSkewX = Float.parseFloat(element.getAttribute("value"));
                                } else if ("SkewY".equals(tagName)) {
                                    iconConfig.mSkewY = Float.parseFloat(element.getAttribute("value"));
                                } else if ("TransX".equals(tagName)) {
                                    iconConfig.mTransX = hdpiIconSizeToCurrent(Float.parseFloat(element.getAttribute("value")));
                                } else if ("TransY".equals(tagName)) {
                                    iconConfig.mTransY = hdpiIconSizeToCurrent(Float.parseFloat(element.getAttribute("value")));
                                } else if ("RotateX".equals(tagName)) {
                                    iconConfig.mRotateX = Float.parseFloat(element.getAttribute("value"));
                                } else if ("RotateY".equals(tagName)) {
                                    iconConfig.mRotateY = Float.parseFloat(element.getAttribute("value"));
                                } else if ("RotateZ".equals(tagName)) {
                                    iconConfig.mRotateZ = Float.parseFloat(element.getAttribute("value"));
                                } else if ("CameraX".equals(tagName)) {
                                    iconConfig.mCameraX = hdpiIconSizeToCurrent(Float.parseFloat(element.getAttribute("value")));
                                } else if ("CameraY".equals(tagName)) {
                                    iconConfig.mCameraY = hdpiIconSizeToCurrent(Float.parseFloat(element.getAttribute("value")));
                                } else if ("OverridedIrregularContentWidth".equals(tagName)) {
                                    iconConfig.mOverridedIrregularContentWidth = getDimension(element.getAttribute("value"));
                                } else if ("OverridedIrregularContentHeight".equals(tagName)) {
                                    iconConfig.mOverridedIrregularContentHeight = getDimension(element.getAttribute("value"));
                                }
                            }
                        } catch (Exception e) {
                            Log.w(LOG_TAG, "transform_config.xml parse failed.", e);
                        }
                    }
                    sIconTransformNeeded = true;
                }
                return iconConfig;
            } catch (IOException e2) {
                e2.printStackTrace();
                return iconConfig;
            }
        } catch (Exception e3) {
            Log.w(LOG_TAG, "load icon config failed.", e3);
            return iconConfig;
        }
    }

    private static IImageFilter.ImageFilterGroup loadIconFilters(NodeList nodeList) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            short s = 1;
            if (nodeList.item(i).getNodeType() == 1) {
                ImageFilterBuilder imageFilterBuilder = new ImageFilterBuilder();
                Element element = (Element) nodeList.item(i);
                if ("Filter".equals(element.getTagName())) {
                    NodeList childNodes = element.getChildNodes();
                    int i2 = 0;
                    while (i2 < childNodes.getLength()) {
                        if (childNodes.item(i2).getNodeType() == s) {
                            Element element2 = (Element) childNodes.item(i2);
                            if ("Param".equals(element2.getNodeName())) {
                                ArrayList arrayList2 = new ArrayList();
                                String attribute = element2.getAttribute("ignoreWhenNotSupported");
                                if (element2.hasChildNodes()) {
                                    NodeList childNodes2 = element2.getChildNodes();
                                    int i3 = 0;
                                    while (i3 < childNodes2.getLength()) {
                                        if (childNodes2.item(i3).getNodeType() == s) {
                                            Element element3 = (Element) childNodes2.item(i3);
                                            if ("IconFilters".equals(element3.getTagName())) {
                                                arrayList2.add(loadIconFilters(element3.getChildNodes()));
                                            }
                                        }
                                        i3++;
                                        s = 1;
                                    }
                                }
                                if (arrayList2.size() == 0) {
                                    for (String str : element2.getAttribute("value").split("\\|")) {
                                        if (!TextUtils.isEmpty(str)) {
                                            arrayList2.add(str);
                                        }
                                    }
                                }
                                imageFilterBuilder.addParam(element2.getAttribute("name"), arrayList2, TextUtils.isEmpty(attribute) ? false : Boolean.TRUE.toString().equalsIgnoreCase(attribute));
                            }
                        }
                        i2++;
                        s = 1;
                    }
                    imageFilterBuilder.setFilterName(element.getAttribute("name"));
                    String attribute2 = element.getAttribute("ignoreWhenNotSupported");
                    imageFilterBuilder.setIgnoreWhenNotSupported(TextUtils.isEmpty(attribute2) ? false : Boolean.TRUE.toString().equalsIgnoreCase(attribute2));
                    try {
                        IImageFilter createImageFilter = imageFilterBuilder.createImageFilter();
                        if (createImageFilter != null) {
                            arrayList.add(createImageFilter);
                        }
                    } catch (ImageFilterBuilder.NoSupportException e) {
                        e.printStackTrace();
                        arrayList.clear();
                    }
                } else {
                    continue;
                }
            }
        }
        return new IImageFilter.ImageFilterGroup((IImageFilter[]) arrayList.toArray(new IImageFilter[0]));
    }

    private static Matrix makeIconMatrix(IconConfig iconConfig) {
        Matrix matrix = new Matrix();
        float[] fArr = iconConfig.mPointsMappingFrom;
        if (fArr != null) {
            matrix.setPolyToPoly(fArr, 0, iconConfig.mPointsMappingTo, 0, fArr.length / 2);
        } else {
            Camera camera = new Camera();
            camera.rotateX(iconConfig.mRotateX);
            camera.rotateY(iconConfig.mRotateY);
            camera.rotateZ(iconConfig.mRotateZ);
            camera.getMatrix(matrix);
            matrix.preTranslate(((-getCustomizedIconWidth()) / sMaxContentRatio) - iconConfig.mCameraX, ((-getCustomizedIconHeight()) / sMaxContentRatio) - iconConfig.mCameraY);
            matrix.postTranslate((getCustomizedIconWidth() / sMaxContentRatio) + iconConfig.mCameraX, (getCustomizedIconHeight() / sMaxContentRatio) + iconConfig.mCameraY);
            matrix.postScale(iconConfig.mScaleX, iconConfig.mScaleY);
            matrix.postSkew(iconConfig.mSkewX, iconConfig.mSkewY);
        }
        return matrix;
    }

    public static void prepareCustomizedIcons(Context context, CustomizedIconsListener customizedIconsListener) {
        getRawIcon("icon_mask.png");
        getRawIcon("icon_background.png");
        getRawIcon("icon_pattern.png");
        getRawIcon("icon_border.png");
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        int i = 1;
        if (customizedIconsListener != null) {
            customizedIconsListener.beforePrepareIcon(queryIntentActivities.size() + 1);
        }
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long currentTimeMillis = System.currentTimeMillis();
        Log.d(LOG_TAG, "prepareCustomizedIcons start");
        ArrayList arrayList = new ArrayList();
        for (final ResolveInfo resolveInfo : queryIntentActivities) {
            arrayList.add(newFixedThreadPool.submit(new Runnable() { // from class: miui.content.res.IconCustomizer.1
                @Override // java.lang.Runnable
                public void run() {
                    resolveInfo.activityInfo.loadIcon(packageManager);
                }
            }));
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            try {
                ((Future) it.next()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e2) {
                e2.printStackTrace();
            }
            if (customizedIconsListener != null) {
                customizedIconsListener.finishPrepareIcon(i);
                i++;
            }
        }
        newFixedThreadPool.shutdown();
        Log.d(LOG_TAG, "prepareCustomizedIcons end " + (System.currentTimeMillis() - currentTimeMillis));
        if (customizedIconsListener != null) {
            customizedIconsListener.finishAllIcons();
        }
    }

    private static void saveCustomizedIconBitmap(String str, Bitmap bitmap, Context context) {
        getServiceManager(context).saveIcon(str, bitmap);
    }

    public static void saveCustomizedIconBitmap(String str, String str2, Drawable drawable, Context context) {
        if (drawable instanceof BitmapDrawable) {
            saveCustomizedIconBitmap(getFileName(str, str2), ((BitmapDrawable) drawable).getBitmap(), context);
        }
    }

    private static Bitmap scaleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int i = Resources.getSystem().getConfiguration().densityDpi;
        bitmap.setDensity(i);
        if (bitmap.getWidth() == getCustomizedIconWidth() && bitmap.getHeight() == getCustomizedIconHeight()) {
            return bitmap;
        }
        float min = Math.min(bitmap.getWidth() / getCustomizedIconWidth(), bitmap.getHeight() / getCustomizedIconWidth()) + 0.1f;
        if (min >= sMaxContentRatio && bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
            bitmap = BitmapFactory.fastBlur(bitmap, (int) (min - 1.0f));
        }
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, getCustomizedIconWidth(), getCustomizedIconHeight(), true);
        createScaledBitmap.setDensity(i);
        return createScaledBitmap;
    }

    private static int setHue(int i, float f, int[] iArr) {
        colorToRGB(i, iArr);
        int min = Math.min(iArr[0], Math.min(iArr[1], iArr[2]));
        int max = Math.max(iArr[0], Math.max(iArr[1], iArr[2]));
        int i2 = max - min;
        if (i2 == 0) {
            return i;
        }
        while (f < 0.0f) {
            f += 360.0f;
        }
        while (f > 360.0f) {
            f -= 360.0f;
        }
        int floor = (int) Math.floor(f / 120.0f);
        float f2 = f - (floor * 120);
        int i3 = (floor + 2) % 3;
        iArr[i3] = min;
        float f3 = i2;
        iArr[(i3 + 2) % 3] = (int) (min + ((Math.min(f2, 60.0f) * f3) / 60.0f));
        iArr[(i3 + 1) % 3] = (int) (max - ((f3 * Math.max(0.0f, f2 - 60.0f)) / 60.0f));
        return RGBToColor(iArr);
    }

    private static int setSaturation(int i, float f, int[] iArr) {
        colorToRGB(i, iArr);
        int min = Math.min(iArr[0], Math.min(iArr[1], iArr[2]));
        int max = Math.max(iArr[0], Math.max(iArr[1], iArr[2]));
        if (max == 0 || max == min) {
            return i;
        }
        float f2 = max;
        float f3 = ((max - min) * 1.0f) / f2;
        iArr[0] = (int) (f2 - (((max - iArr[0]) * f) / f3));
        iArr[1] = (int) (f2 - (((max - iArr[1]) * f) / f3));
        iArr[2] = (int) (f2 - (((max - iArr[2]) * f) / f3));
        return RGBToColor(iArr);
    }

    private static int setValue(int i, float f, int[] iArr) {
        colorToRGB(i, iArr);
        int max = Math.max(iArr[0], Math.max(iArr[1], iArr[2]));
        if (max == 0) {
            return i;
        }
        float f2 = (max * 1.0f) / 255.0f;
        iArr[0] = (int) ((iArr[0] * f) / f2);
        iArr[1] = (int) ((iArr[1] * f) / f2);
        iArr[2] = (int) ((iArr[2] * f) / f2);
        return RGBToColor(iArr);
    }

    private static BitmapDrawable transToMiuiModIcon(Context context, Bitmap bitmap, Drawable drawable, String str) {
        boolean isAdaptiveIconDrawale = isAdaptiveIconDrawale(drawable);
        if (bitmap == null && drawable != null) {
            bitmap = drawableToBitmap(drawable, getScaleRatio(drawable, false), isAdaptiveIconDrawale);
        }
        if (bitmap != null) {
            Log.d(LOG_TAG, String.format("Generate customized icon for %s", str));
            bitmap = composeIconWithTransform(bitmap, "icon_mask.png", "icon_background.png", "icon_pattern.png", "icon_border.png");
            if (!isAdaptiveIconDrawale) {
                saveCustomizedIconBitmap(str, bitmap, context);
            }
        }
        return getDrawble(bitmap);
    }

    private static Bitmap transformBitmap(Bitmap bitmap, Matrix matrix) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        createBitmap.setDensity(bitmap.getDensity());
        new Canvas(createBitmap).drawBitmap(bitmap, matrix, sPaintForTransformBitmap);
        return createBitmap;
    }
}
