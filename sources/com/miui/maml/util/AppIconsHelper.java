package com.miui.maml.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import com.miui.maml.AnimatingDrawable;
import com.miui.maml.FancyDrawable;
import com.miui.maml.LifecycleResourceManager;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.util.RendererCoreCache;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import miui.content.res.IconCustomizer;

/* loaded from: classes2.dex */
public class AppIconsHelper {
    private static HashMap<String, WeakReference<ResourceManager>> mAnimatingIconsResourceManagers = new HashMap<>();
    private static final RendererCoreCache.OnCreateRootCallback mOnCreateRootCallback = new RendererCoreCache.OnCreateRootCallback() { // from class: com.miui.maml.util.AppIconsHelper.1
        @Override // com.miui.maml.util.RendererCoreCache.OnCreateRootCallback
        public void onCreateRoot(ScreenElementRoot screenElementRoot) {
            if (screenElementRoot != null) {
                screenElementRoot.setScaleByDensity(true);
            }
        }
    };
    private static RendererCoreCache mRendererCoreCache;
    private static int mThemeChanged;

    private static void checkVersion(Context context) {
        int Configuration_getThemeChanged = HideSdkDependencyUtils.Configuration_getThemeChanged(context.getResources().getConfiguration());
        if (Configuration_getThemeChanged > mThemeChanged) {
            clearCache();
            mThemeChanged = Configuration_getThemeChanged;
        }
    }

    public static void clearCache() {
        RendererCoreCache rendererCoreCache = mRendererCoreCache;
        if (rendererCoreCache != null) {
            rendererCoreCache.clear();
        }
        HashMap<String, WeakReference<ResourceManager>> hashMap = mAnimatingIconsResourceManagers;
        if (hashMap != null) {
            hashMap.clear();
        }
    }

    public static Drawable getIconDrawable(Context context, PackageItemInfo packageItemInfo, PackageManager packageManager) {
        return getIconDrawable(context, packageItemInfo, packageManager, 0L);
    }

    public static Drawable getIconDrawable(Context context, PackageItemInfo packageItemInfo, PackageManager packageManager, long j) {
        return getIconDrawable(context, packageItemInfo, packageManager, j, HideSdkDependencyUtils.UserHandle_getInstance_with_int(HideSdkDependencyUtils.Context_getUserId(context)));
    }

    public static Drawable getIconDrawable(Context context, PackageItemInfo packageItemInfo, PackageManager packageManager, long j, UserHandle userHandle) {
        Drawable iconDrawable = getIconDrawable(context, packageItemInfo, packageItemInfo.packageName, (Build.VERSION.SDK_INT <= 24 || !(packageItemInfo instanceof ApplicationInfo)) ? packageItemInfo.name : null, j, userHandle);
        return iconDrawable != null ? iconDrawable : packageItemInfo.loadIcon(packageManager);
    }

    public static Drawable getIconDrawable(Context context, PackageItemInfo packageItemInfo, String str, String str2, long j, UserHandle userHandle) {
        return getIconDrawable(context, packageItemInfo, str, str2, j, userHandle, false);
    }

    public static Drawable getIconDrawable(Context context, PackageItemInfo packageItemInfo, String str, String str2, long j, UserHandle userHandle, boolean z) {
        Drawable fancyDrawable;
        String fancyIconRelativePath;
        if (mRendererCoreCache == null) {
            mRendererCoreCache = new RendererCoreCache(new Handler(Looper.getMainLooper()));
        }
        try {
            checkVersion(context);
            String str3 = str + str2 + HideSdkDependencyUtils.UserHandle_getIdentifier(userHandle);
            String animatingIconRelativePath = IconCustomizer.getAnimatingIconRelativePath(packageItemInfo, str, str2);
            if (animatingIconRelativePath == null || z) {
                RendererCoreCache.RendererCoreInfo rendererCoreInfo = mRendererCoreCache.get(str3, j);
                if (rendererCoreInfo == null) {
                    if (animatingIconRelativePath != null) {
                        fancyIconRelativePath = animatingIconRelativePath + "fancy/";
                    } else {
                        fancyIconRelativePath = IconCustomizer.getFancyIconRelativePath(packageItemInfo, str, str2);
                    }
                    rendererCoreInfo = mRendererCoreCache.get(str3, context, j, new FancyIconResourceLoader(fancyIconRelativePath), mOnCreateRootCallback);
                }
                fancyDrawable = (rendererCoreInfo == null || rendererCoreInfo.r == null) ? null : new FancyDrawable(rendererCoreInfo.r);
            } else {
                WeakReference<ResourceManager> weakReference = mAnimatingIconsResourceManagers.get(str3);
                ResourceManager resourceManager = weakReference == null ? null : weakReference.get();
                if (resourceManager == null) {
                    resourceManager = new LifecycleResourceManager(new FancyIconResourceLoader(animatingIconRelativePath + "quiet/"), 3600000L, 360000L);
                    mAnimatingIconsResourceManagers.put(str3, new WeakReference<>(resourceManager));
                }
                fancyDrawable = new AnimatingDrawable(context, str, str2, resourceManager, userHandle);
            }
            if (fancyDrawable != null) {
                PortableUtils.getUserBadgedIcon(context, fancyDrawable, userHandle);
            }
            return fancyDrawable;
        } catch (Exception e) {
            Log.e("MAML AppIconsHelper", e.toString());
            return null;
        }
    }

    public static Drawable getIconDrawable(Context context, ResolveInfo resolveInfo, PackageManager packageManager, long j) {
        PackageItemInfo packageItemInfo = resolveInfo.activityInfo;
        if (packageItemInfo == null) {
            packageItemInfo = resolveInfo.serviceInfo;
        }
        return getIconDrawable(context, packageItemInfo, packageManager, j);
    }
}
