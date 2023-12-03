package com.miui.maml.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageInstallObserver2;
import android.content.res.Configuration;
import android.content.res.MiuiConfiguration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.MemoryFile;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.provider.SystemSettings$System;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.WindowManager;
import java.io.FileDescriptor;
import java.util.Optional;
import miui.os.SystemProperties;

/* loaded from: classes2.dex */
public class HideSdkDependencyUtils {
    public static int Configuration_getThemeChanged(Configuration configuration) {
        try {
            MiuiConfiguration miuiConfiguration = (MiuiConfiguration) ReflectionHelper.getFieldValue(Configuration.class, configuration, "extraConfig");
            if (miuiConfiguration == null) {
                return 0;
            }
            return miuiConfiguration.themeChanged;
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | Configuration_getThemeChanged() occur EXCEPTION: ", e);
            return 0;
        }
    }

    public static int Context_getUserId(Context context) {
        try {
            return ((Integer) ReflectionHelper.invokeObject(Context.class, context, "getUserId", new Class[0], new Object[0])).intValue();
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | Context_getUserId() occur EXCEPTION: ", e);
            return 0;
        }
    }

    public static void Context_startActivityAsUser(Context context, Intent intent, Bundle bundle, UserHandle userHandle) {
        try {
            ReflectionHelper.invokeObject(Context.class, context, "startActivityAsUser", new Class[]{Intent.class, Bundle.class, UserHandle.class}, intent, bundle, userHandle);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | Context_startActivityAsUser() occur EXCEPTION: ", e);
        }
    }

    public static ComponentName Context_startServiceAsUser(Context context, Intent intent, UserHandle userHandle) {
        try {
            return (ComponentName) ReflectionHelper.invokeObject(Context.class, context, "startServiceAsUser", new Class[]{Intent.class, UserHandle.class}, intent, userHandle);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | Context_startServiceAsUser() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static FileDescriptor MemoryFile_getFileDescriptor(MemoryFile memoryFile) {
        try {
            return (FileDescriptor) ReflectionHelper.invokeObject(FileDescriptor.class, memoryFile, "getFileDescriptor", new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | MemoryFile_getFileDescriptor() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static boolean MotionEvent_isTouchEvent(MotionEvent motionEvent) {
        try {
            return ((Boolean) ReflectionHelper.invokeObject(MotionEvent.class, motionEvent, "isTouchEvent", new Class[0], new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | MotionEvent_isTouchEvent() occur EXCEPTION: ", e);
            return false;
        }
    }

    public static boolean PreloadedAppPolicy_installPreloadedDataApp(final Context context, String str, final Intent intent, final Bundle bundle) {
        try {
            Class<?> cls = ReflectionHelper.getClass("miui.content.pm.PreloadedAppPolicy");
            return Build.VERSION.SDK_INT >= 28 ? ((Boolean) ReflectionHelper.invokeObject(cls, null, "installPreloadedDataApp", new Class[]{Context.class, String.class, IPackageInstallObserver2.class, Integer.TYPE}, context, str, new IPackageInstallObserver2.Stub() { // from class: com.miui.maml.util.HideSdkDependencyUtils.1
                public void onPackageInstalled(String str2, int i, String str3, Bundle bundle2) throws RemoteException {
                    Utils.startActivity(context, intent, bundle);
                }

                public void onUserActionRequired(Intent intent2) {
                }
            }, 1)).booleanValue() : ((Boolean) ReflectionHelper.invokeObject(cls, null, "installPreloadedDataApp", new Class[]{Context.class, String.class, IPackageInstallObserver.class, Integer.TYPE}, context, str, new IPackageInstallObserver.Stub() { // from class: com.miui.maml.util.HideSdkDependencyUtils.2
            }, 1)).booleanValue();
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | PreloadedAppPolicy_installPreloadedDataApp() occur EXCEPTION: ", e);
            return false;
        }
    }

    public static String SettingsSecure_UI_NIGHT_MODE() {
        try {
            return (String) ReflectionHelper.getFieldValue(Settings.Secure.class, null, "UI_NIGHT_MODE");
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | SettingsSecure_UI_NIGHT_MODE() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static void StorageManager_disableUsbMassStorage(StorageManager storageManager) {
        try {
            ReflectionHelper.invoke(StorageManager.class, storageManager, "disableUsbMassStorage", new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | StorageManager_disableUsbMassStorage() occur EXCEPTION: ", e);
        }
    }

    public static void StorageManager_enableUsbMassStorage(StorageManager storageManager) {
        try {
            ReflectionHelper.invoke(StorageManager.class, storageManager, "enableUsbMassStorage", new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | StorageManager_enableUsbMassStorage() occur EXCEPTION: ", e);
        }
    }

    public static boolean StorageManager_isUsbMassStorageEnabled(StorageManager storageManager) {
        try {
            return ((Boolean) ReflectionHelper.invokeObject(StorageManager.class, storageManager, "isUsbMassStorageEnabled", new Class[0], new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | StorageManager_isUsbMassStorageEnabled() occur EXCEPTION: ", e);
            return false;
        }
    }

    public static void SurfaceControl_closeTransaction() {
        try {
            ReflectionHelper.invokeObject(ReflectionHelper.getClass("android.view.SurfaceControl"), null, "closeTransaction", new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | SurfaceControl_closeTransaction() occur EXCEPTION: ", e);
        }
    }

    public static SurfaceControl SurfaceControl_getInstance_with_engine(WallpaperService.Engine engine) {
        if (Build.VERSION.SDK_INT == 29) {
            try {
                SurfaceControl surfaceControl = (SurfaceControl) ReflectionHelper.getConstructorInstance(SurfaceControl.class, new Class[0], new Object[0]);
                initSurfaceControl(engine, surfaceControl);
                return surfaceControl;
            } catch (Exception e) {
                Log.e("MAML_Reflect", "Invoke | SurfaceControl_getInstance() occur EXCEPTION: ", e);
                return null;
            }
        }
        return null;
    }

    public static SurfaceControl SurfaceControl_getInstance_with_params(Surface surface, SurfaceControl surfaceControl, String str, int i, int i2, int i3) {
        try {
            Class<?> cls = ReflectionHelper.getClass("android.view.SurfaceSession");
            Class<?> cls2 = ReflectionHelper.getClass("android.view.SurfaceControl");
            int intValue = ((Integer) ReflectionHelper.getFieldValue(cls2, null, "HIDDEN")).intValue();
            int i4 = Build.VERSION.SDK_INT;
            if (i4 == 29) {
                Object constructorInstance = ReflectionHelper.getConstructorInstance(cls, new Class[0], new Object[0]);
                Class cls3 = Integer.TYPE;
                return (SurfaceControl) ReflectionHelper.getConstructorInstance(cls2, new Class[]{cls, String.class, cls3, cls3, cls3, cls3, cls2, SparseIntArray.class}, constructorInstance, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(intValue), surfaceControl, null);
            } else if (i4 == 28) {
                Object constructorInstance2 = ReflectionHelper.getConstructorInstance(cls, new Class[]{Surface.class}, surface);
                Class cls4 = Integer.TYPE;
                return (SurfaceControl) ReflectionHelper.getConstructorInstance(cls2, new Class[]{cls, String.class, cls4, cls4, cls4, cls4, cls2, cls4, cls4}, constructorInstance2, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(intValue), surfaceControl, -1, -1);
            } else if (i4 == 26 || i4 == 27) {
                Object constructorInstance3 = ReflectionHelper.getConstructorInstance(cls, new Class[]{Surface.class}, surface);
                Class cls5 = Integer.TYPE;
                return (SurfaceControl) ReflectionHelper.getConstructorInstance(cls2, new Class[]{cls, String.class, cls5, cls5, cls5, cls5}, constructorInstance3, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(intValue));
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | SurfaceControl_getInstance_with_params() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static void SurfaceControl_hide(SurfaceControl surfaceControl) {
        try {
            ReflectionHelper.invokeObject(ReflectionHelper.getClass("android.view.SurfaceControl"), surfaceControl, "hide", new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | SurfaceControl_hide() occur EXCEPTION: ", e);
        }
    }

    public static void SurfaceControl_openTransaction() {
        try {
            ReflectionHelper.invokeObject(ReflectionHelper.getClass("android.view.SurfaceControl"), null, "openTransaction", new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | SurfaceControl_openTransaction() occur EXCEPTION: ", e);
        }
    }

    public static void SurfaceControl_setBufferSize(SurfaceControl surfaceControl, int i, int i2) {
        try {
            int i3 = Build.VERSION.SDK_INT;
            if (i3 == 29) {
                Class cls = Integer.TYPE;
                ReflectionHelper.invokeObject(SurfaceControl.class, surfaceControl, "setBufferSize", new Class[]{cls, cls}, Integer.valueOf(i), Integer.valueOf(i2));
            } else if (i3 >= 26 && i3 <= 28) {
                Class<?> cls2 = ReflectionHelper.getClass("android.view.SurfaceControl");
                Class cls3 = Integer.TYPE;
                ReflectionHelper.invokeObject(cls2, surfaceControl, "setSize", new Class[]{cls3, cls3}, Integer.valueOf(i), Integer.valueOf(i2));
            }
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | SurfaceControl_setBufferSize() occur EXCEPTION: ", e);
        }
    }

    public static void SurfaceControl_setLayer(SurfaceControl surfaceControl, int i) {
        try {
            ReflectionHelper.invokeObject(ReflectionHelper.getClass("android.view.SurfaceControl"), surfaceControl, "setLayer", new Class[]{Integer.TYPE}, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | SurfaceControl_setLayer() occur EXCEPTION: ", e);
        }
    }

    public static void SurfaceControl_setPosition(SurfaceControl surfaceControl, float f, float f2) {
        try {
            Class<?> cls = ReflectionHelper.getClass("android.view.SurfaceControl");
            Class cls2 = Float.TYPE;
            ReflectionHelper.invokeObject(cls, surfaceControl, "setPosition", new Class[]{cls2, cls2}, Float.valueOf(f), Float.valueOf(f2));
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | SurfaceControl_setPosition() occur EXCEPTION: ", e);
        }
    }

    public static void SurfaceControl_show(SurfaceControl surfaceControl) {
        try {
            ReflectionHelper.invokeObject(ReflectionHelper.getClass("android.view.SurfaceControl"), surfaceControl, "show", new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | SurfaceControl_show() occur EXCEPTION: ", e);
        }
    }

    public static void Surface_copyFrom(Surface surface, SurfaceControl surfaceControl) {
        int i = Build.VERSION.SDK_INT;
        if (i < 26 || i > 29) {
            return;
        }
        try {
            ReflectionHelper.invokeObject(Surface.class, surface, "copyFrom", new Class[]{ReflectionHelper.getClass("android.view.SurfaceControl")}, surfaceControl);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | Surface_copyFrom() occur EXCEPTION: ", e);
        }
    }

    public static Surface Surface_getInstance() {
        try {
            return (Surface) ReflectionHelper.getConstructorInstance(Surface.class, new Class[0], new Object[0]);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | Surface_getInstance() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static String SystemSettingsSystem_DARKEN_WALLPAPER_UNDER_DARK_MODE() {
        try {
            return (String) ReflectionHelper.getFieldValue(SystemSettings$System.class, null, "DARKEN_WALLPAPER_UNDER_DARK_MODE");
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | SystemSettingsSystem_DARKEN_WALLPAPER_UNDER_DARK_MODE() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static Typeface TypefaceUtils_replaceTypeface(Context context, Typeface typeface) {
        try {
            return (Typeface) ReflectionHelper.invokeObject(ReflectionHelper.getClass("miui.util.TypefaceUtils"), null, "replaceTypeface", new Class[]{Context.class, Typeface.class}, context, typeface);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | TypefaceUtils_replaceTypeface() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static UserHandle UserHandle_CURRENT() {
        try {
            return (UserHandle) ReflectionHelper.getFieldValue(UserHandle.class, null, "CURRENT");
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | UserHandle_CURRENT() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static int UserHandle_getIdentifier(UserHandle userHandle) {
        try {
            return ((Integer) ReflectionHelper.invokeObject(UserHandle.class, userHandle, "getIdentifier", new Class[0], new Object[0])).intValue();
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | UserHandle_getIdentifier() occur EXCEPTION: ", e);
            return 0;
        }
    }

    public static UserHandle UserHandle_getInstance_with_int(int i) {
        try {
            return (UserHandle) ReflectionHelper.getConstructorInstance(UserHandle.class, new Class[]{Integer.TYPE}, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | UserHandle_getInstance_with_int() occur EXCEPTION: ", e);
            return null;
        }
    }

    public static void WindowManager_LayoutParams_setLayoutParamsBlurRatio(WindowManager.LayoutParams layoutParams, float f) {
        try {
            ReflectionHelper.setFieldValue(WindowManager.LayoutParams.class, layoutParams, "blurRatio", Float.valueOf(f));
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | WindowManager_LayoutParams_setLayoutParamsBlurRatio() occur EXCEPTION: ", e);
        }
    }

    private static void initSurfaceControl(WallpaperService.Engine engine, SurfaceControl surfaceControl) {
        try {
            Class<?> cls = ReflectionHelper.getClass("android.view.IWindowSession");
            Class<?> cls2 = ReflectionHelper.getClass("android.view.IWindow");
            Class<?> cls3 = ReflectionHelper.getClass("com.android.internal.view.BaseIWindow");
            Class<?> cls4 = ReflectionHelper.getClass("android.view.DisplayCutout$ParcelableWrapper");
            Class<?> cls5 = ReflectionHelper.getClass("android.view.InsetsState");
            Class<?> cls6 = ReflectionHelper.getClass("android.util.MergedConfiguration");
            Object fieldValue = ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mSession");
            Object fieldValue2 = ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mWindow");
            Object fieldValue3 = ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mDisplayCutout");
            Object fieldValue4 = ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mInsetsState");
            Object fieldValue5 = ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mMergedConfiguration");
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mLayout");
            int intValue = ((Integer) ReflectionHelper.getFieldValue(cls3, fieldValue2, "mSeq")).intValue();
            int intValue2 = ((Integer) ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mWidth")).intValue();
            int intValue3 = ((Integer) ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mHeight")).intValue();
            Rect rect = (Rect) ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mVisibleInsets");
            Rect rect2 = (Rect) ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mWinFrame");
            Rect rect3 = (Rect) ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mOverscanInsets");
            Rect rect4 = (Rect) ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mContentInsets");
            Rect rect5 = (Rect) ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mStableInsets");
            Rect rect6 = (Rect) ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mOutsets");
            Rect rect7 = (Rect) ReflectionHelper.getFieldValue(WallpaperService.Engine.class, engine, "mBackdropFrame");
            if (fieldValue != null) {
                Class cls7 = Integer.TYPE;
                ReflectionHelper.invokeObject(cls, fieldValue, "relayout", new Class[]{cls2, cls7, WindowManager.LayoutParams.class, cls7, cls7, cls7, cls7, Long.TYPE, Rect.class, Rect.class, Rect.class, Rect.class, Rect.class, Rect.class, Rect.class, cls4, cls6, SurfaceControl.class, cls5}, fieldValue2, Integer.valueOf(intValue), layoutParams, Integer.valueOf(intValue2), Integer.valueOf(intValue3), 0, 0, -1, rect2, rect3, rect4, rect, rect5, rect6, rect7, fieldValue3, fieldValue5, surfaceControl, fieldValue4);
            }
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | initSurfaceControl() occur EXCEPTION: ", e);
        }
    }

    public static boolean isShowDebugLayout() {
        try {
            return Build.VERSION.SDK_INT >= 29 ? ((Boolean) ((Optional) ReflectionHelper.invokeObject(ReflectionHelper.getClass("android.sysprop.DisplayProperties"), null, "debug_layout", new Class[0], new Object[0])).orElse(Boolean.FALSE)).booleanValue() : SystemProperties.getBoolean("debug.layout", false);
        } catch (Exception e) {
            Log.e("MAML_Reflect", "Invoke | isShowDebugLayout() occur EXCEPTION: ", e);
            return false;
        }
    }
}
