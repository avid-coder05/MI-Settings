package com.iqiyi.android.qigsaw.core.splitload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitload.compat.SplitResourcesLoader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/* loaded from: classes2.dex */
public class SplitCompatResourcesLoader {
    private static final String TAG = "SplitCompatResourcesLoader";
    private static final Object sLock = new Object();
    private static final SplitResourcesLoader resourcesLoader = getSplitResourcesLoader();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class DefaultSplitResourcesLoader implements SplitResourcesLoader {
        private DefaultSplitResourcesLoader() {
        }

        @Override // com.iqiyi.android.qigsaw.core.splitload.compat.SplitResourcesLoader
        public void loadResources(Context context, Resources resources) throws Throwable {
            SplitCompatResourcesLoader.checkOrUpdateResources(context, resources);
        }

        @Override // com.iqiyi.android.qigsaw.core.splitload.compat.SplitResourcesLoader
        public void loadResources(Context context, Resources resources, String str) throws Throwable {
            if (SplitCompatResourcesLoader.getLoadedResourcesDirs(resources.getAssets()).contains(str)) {
                return;
            }
            SplitCompatResourcesLoader.installSplitResDirs(context, resources, Collections.singletonList(str));
            SplitLog.d(SplitCompatResourcesLoader.TAG, "Install split %s resources for application.", str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class V14 extends VersionCompat {
        private V14() {
            super();
        }

        private static void checkOrUpdateResourcesForContext(Context context, Resources resources, Resources resources2) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
            if ((context instanceof ContextThemeWrapper) && Build.VERSION.SDK_INT >= 17 && ((Resources) VersionCompat.mResourcesInContextThemeWrapper().get(context)) == resources) {
                SplitLog.i(SplitCompatResourcesLoader.TAG, "context %s type is @ContextThemeWrapper, and it has its own resources instance!", context.getClass().getSimpleName());
                VersionCompat.mResourcesInContextThemeWrapper().set(context, resources2);
                VersionCompat.mThemeInContextThemeWrapper().set(context, null);
            }
            Context baseContext = getBaseContext(context);
            if (baseContext.getClass().getName().equals("android.app.ContextImpl")) {
                if (((Resources) VersionCompat.mResourcesInContextImpl().get(baseContext)) == resources) {
                    VersionCompat.mResourcesInContextImpl().set(baseContext, resources2);
                    VersionCompat.mThemeInContentImpl().set(baseContext, null);
                    return;
                }
                return;
            }
            try {
                if (((Resources) HiddenApiReflection.findField(baseContext, "mResources").get(baseContext)) == resources) {
                    HiddenApiReflection.findField(baseContext, "mResources").set(baseContext, resources2);
                    HiddenApiReflection.findField(baseContext, "mTheme").set(baseContext, null);
                }
            } catch (NoSuchFieldException e) {
                SplitLog.w(SplitCompatResourcesLoader.TAG, "Can not find mResources in " + baseContext.getClass().getName(), e);
            }
            if (((Resources) VersionCompat.mResourcesInContextImpl().get(baseContext)) == resources) {
                VersionCompat.mResourcesInContextImpl().set(baseContext, resources2);
                VersionCompat.mThemeInContentImpl().set(baseContext, null);
            }
        }

        private static AssetManager createAssetManager() throws IllegalAccessException, InstantiationException {
            return (AssetManager) AssetManager.class.newInstance();
        }

        private static Resources createResources(Context context, Resources resources, List<String> list) throws NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            List<String> appResDirs = getAppResDirs(context.getPackageResourcePath(), resources.getAssets());
            appResDirs.addAll(0, list);
            AssetManager createAssetManager = createAssetManager();
            for (String str : appResDirs) {
                if (((Integer) VersionCompat.getAddAssetPathMethod().invoke(createAssetManager, str)).intValue() == 0) {
                    SplitLog.e(SplitCompatResourcesLoader.TAG, "Split Apk res path : " + str, new Object[0]);
                    throw new RuntimeException("invoke addAssetPath failure! apk format maybe incorrect");
                }
            }
            return newResources(resources, createAssetManager);
        }

        private static List<String> getAppResDirs(String str, AssetManager assetManager) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            boolean z;
            AssetManager assets = Resources.getSystem().getAssets();
            Object[] objArr = (Object[]) VersionCompat.mStringBlocksInAssetManager().get(assets);
            int length = ((Object[]) VersionCompat.mStringBlocksInAssetManager().get(assetManager)).length;
            int length2 = objArr.length;
            ArrayList arrayList = new ArrayList(length - length2);
            int i = length2 + 1;
            while (true) {
                z = true;
                if (i > length) {
                    break;
                }
                arrayList.add((String) VersionCompat.getGetCookieNameMethod().invoke(assetManager, Integer.valueOf(i)));
                i++;
            }
            if (!arrayList.contains(str)) {
                int i2 = 1;
                while (true) {
                    if (i2 > length2) {
                        z = false;
                        break;
                    } else if (str.equals((String) VersionCompat.getGetCookieNameMethod().invoke(assets, Integer.valueOf(i2)))) {
                        break;
                    } else {
                        i2++;
                    }
                }
                if (!z) {
                    arrayList.add(0, str);
                }
            }
            return arrayList;
        }

        private static Context getBaseContext(Context context) {
            while (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            }
            return context;
        }

        /* JADX INFO: Access modifiers changed from: private */
        @SuppressLint({"PrivateApi"})
        public static void installSplitResDirs(Context context, Resources resources, List<String> list) throws Throwable {
            Map map;
            Resources createResources = createResources(context, resources, list);
            checkOrUpdateResourcesForContext(context, resources, createResources);
            Object activityThread = VersionCompat.getActivityThread();
            Iterator it = ((Map) VersionCompat.mActivitiesInActivityThread().get(activityThread)).entrySet().iterator();
            while (it.hasNext()) {
                Object value = ((Map.Entry) it.next()).getValue();
                Activity activity = (Activity) HiddenApiReflection.findField(value, "activity").get(value);
                if (context != activity) {
                    SplitLog.i(SplitCompatResourcesLoader.TAG, "pre-resources found in @mActivities", new Object[0]);
                    checkOrUpdateResourcesForContext(activity, resources, createResources);
                }
            }
            if (Build.VERSION.SDK_INT < 19) {
                map = (Map) VersionCompat.mActiveResourcesInActivityThread().get(activityThread);
            } else {
                map = (Map) VersionCompat.mActiveResourcesInResourcesManager().get(VersionCompat.getResourcesManager());
            }
            Iterator it2 = map.entrySet().iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                Map.Entry entry = (Map.Entry) it2.next();
                Resources resources2 = (Resources) ((WeakReference) entry.getValue()).get();
                if (resources2 != null && resources2 == resources) {
                    map.put(entry.getKey(), new WeakReference(createResources));
                    SplitLog.i(SplitCompatResourcesLoader.TAG, "pre-resources found in @mActiveResources", new Object[0]);
                    break;
                }
            }
            Iterator it3 = ((Map) VersionCompat.mPackagesInActivityThread().get(activityThread)).entrySet().iterator();
            while (it3.hasNext()) {
                Object obj = ((WeakReference) ((Map.Entry) it3.next()).getValue()).get();
                if (obj != null && ((Resources) VersionCompat.mResourcesInLoadedApk().get(obj)) == resources) {
                    SplitLog.i(SplitCompatResourcesLoader.TAG, "pre-resources found in @mPackages", new Object[0]);
                    VersionCompat.mResourcesInLoadedApk().set(obj, createResources);
                }
            }
            Iterator it4 = ((Map) VersionCompat.mResourcePackagesInActivityThread().get(activityThread)).entrySet().iterator();
            while (it4.hasNext()) {
                Object obj2 = ((WeakReference) ((Map.Entry) it4.next()).getValue()).get();
                if (obj2 != null && ((Resources) VersionCompat.mResourcesInLoadedApk().get(obj2)) == resources) {
                    SplitLog.i(SplitCompatResourcesLoader.TAG, "pre-resources found in @mResourcePackages", new Object[0]);
                    VersionCompat.mResourcesInLoadedApk().set(obj2, createResources);
                }
            }
        }

        private static Resources newResources(Resources resources, AssetManager assetManager) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            return (Resources) HiddenApiReflection.findConstructor(resources, AssetManager.class, DisplayMetrics.class, Configuration.class).newInstance(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class V21 extends VersionCompat {
        private V21() {
            super();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void installSplitResDirs(Resources resources, List<String> list) throws Throwable {
            Method addAssetPathMethod = VersionCompat.getAddAssetPathMethod();
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                addAssetPathMethod.invoke(resources.getAssets(), it.next());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static abstract class VersionCompat {
        private static Object activityThread;
        private static Class<?> activityThreadClass;
        private static Method addAssetPathMethod;
        private static Class<?> contextImplClass;
        private static Method getApkAssetsMethod;
        private static Method getAssetPathMethod;
        private static Method getCookieNameMethod;
        private static Class<?> loadedApkClass;
        private static Field mActiveResourcesInActivityThread;
        private static Field mActiveResourcesInResourcesManager;
        private static Field mActivitiesInActivityThread;
        private static Field mPackagesInActivityThread;
        private static Field mResourcePackagesInActivityThread;
        private static Field mResourcesInContextImpl;
        private static Field mResourcesInContextThemeWrapper;
        private static Field mResourcesInLoadedApk;
        private static Field mStringBlocksField;
        private static Field mThemeInContentImpl;
        private static Field mThemeInContextThemeWrapper;
        private static Object resourcesManager;
        private static Class<?> resourcesManagerClass;

        private VersionCompat() {
        }

        @SuppressLint({"PrivateApi"})
        static Object getActivityThread() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            if (activityThread == null) {
                activityThread = HiddenApiReflection.findMethod(getActivityThreadClass(), "currentActivityThread", (Class<?>[]) new Class[0]).invoke(null, new Object[0]);
            }
            return activityThread;
        }

        @SuppressLint({"PrivateApi"})
        static Class<?> getActivityThreadClass() throws ClassNotFoundException {
            if (activityThreadClass == null) {
                activityThreadClass = Class.forName("android.app.ActivityThread");
            }
            return activityThreadClass;
        }

        static Method getAddAssetPathMethod() throws NoSuchMethodException {
            if (addAssetPathMethod == null) {
                addAssetPathMethod = HiddenApiReflection.findMethod((Class<?>) AssetManager.class, "addAssetPath", (Class<?>[]) new Class[]{String.class});
            }
            return addAssetPathMethod;
        }

        @SuppressLint({"PrivateApi"})
        static Class<?> getContextImplClass() throws ClassNotFoundException {
            if (contextImplClass == null) {
                contextImplClass = Class.forName("android.app.ContextImpl");
            }
            return contextImplClass;
        }

        static Method getGetApkAssetsMethod() throws NoSuchMethodException {
            if (getApkAssetsMethod == null) {
                getApkAssetsMethod = HiddenApiReflection.findMethod((Class<?>) AssetManager.class, "getApkAssets", (Class<?>[]) new Class[0]);
            }
            return getApkAssetsMethod;
        }

        @SuppressLint({"PrivateApi"})
        static Method getGetAssetPathMethod() throws ClassNotFoundException, NoSuchMethodException {
            if (getAssetPathMethod == null) {
                getAssetPathMethod = HiddenApiReflection.findMethod(Class.forName("android.content.res.ApkAssets"), "getAssetPath", (Class<?>[]) new Class[0]);
            }
            return getAssetPathMethod;
        }

        static Method getGetCookieNameMethod() throws NoSuchMethodException {
            if (getCookieNameMethod == null) {
                getCookieNameMethod = HiddenApiReflection.findMethod((Class<?>) AssetManager.class, "getCookieName", (Class<?>[]) new Class[]{Integer.TYPE});
            }
            return getCookieNameMethod;
        }

        @SuppressLint({"PrivateApi"})
        static Class<?> getLoadedApkClass() throws ClassNotFoundException {
            if (loadedApkClass == null) {
                loadedApkClass = Class.forName("android.app.LoadedApk");
            }
            return loadedApkClass;
        }

        @SuppressLint({"PrivateApi"})
        static Object getResourcesManager() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            if (resourcesManager == null) {
                resourcesManager = HiddenApiReflection.findMethod(getResourcesManagerClass(), "getInstance", (Class<?>[]) new Class[0]).invoke(null, new Object[0]);
            }
            return resourcesManager;
        }

        @SuppressLint({"PrivateApi"})
        static Class<?> getResourcesManagerClass() throws ClassNotFoundException {
            if (resourcesManagerClass == null) {
                resourcesManagerClass = Class.forName("android.app.ResourcesManager");
            }
            return resourcesManagerClass;
        }

        static Field mActiveResourcesInActivityThread() throws ClassNotFoundException, NoSuchFieldException {
            if (mActiveResourcesInActivityThread == null) {
                mActiveResourcesInActivityThread = HiddenApiReflection.findField(getActivityThreadClass(), "mActiveResources");
            }
            return mActiveResourcesInActivityThread;
        }

        static Field mActiveResourcesInResourcesManager() throws ClassNotFoundException, NoSuchFieldException {
            if (mActiveResourcesInResourcesManager == null) {
                mActiveResourcesInResourcesManager = HiddenApiReflection.findField(getResourcesManagerClass(), "mActiveResources");
            }
            return mActiveResourcesInResourcesManager;
        }

        static Field mActivitiesInActivityThread() throws NoSuchFieldException, ClassNotFoundException {
            if (mActivitiesInActivityThread == null) {
                mActivitiesInActivityThread = HiddenApiReflection.findField(getActivityThreadClass(), "mActivities");
            }
            return mActivitiesInActivityThread;
        }

        static Field mPackagesInActivityThread() throws ClassNotFoundException, NoSuchFieldException {
            if (mPackagesInActivityThread == null) {
                mPackagesInActivityThread = HiddenApiReflection.findField(getActivityThreadClass(), "mPackages");
            }
            return mPackagesInActivityThread;
        }

        static Field mResourcePackagesInActivityThread() throws ClassNotFoundException, NoSuchFieldException {
            if (mResourcePackagesInActivityThread == null) {
                mResourcePackagesInActivityThread = HiddenApiReflection.findField(getActivityThreadClass(), "mResourcePackages");
            }
            return mResourcePackagesInActivityThread;
        }

        static Field mResourcesInContextImpl() throws ClassNotFoundException, NoSuchFieldException {
            if (mResourcesInContextImpl == null) {
                mResourcesInContextImpl = HiddenApiReflection.findField(getContextImplClass(), "mResources");
            }
            return mResourcesInContextImpl;
        }

        static Field mResourcesInContextThemeWrapper() throws NoSuchFieldException {
            if (mResourcesInContextThemeWrapper == null) {
                mResourcesInContextThemeWrapper = HiddenApiReflection.findField((Class<?>) ContextThemeWrapper.class, "mResources");
            }
            return mResourcesInContextThemeWrapper;
        }

        static Field mResourcesInLoadedApk() throws ClassNotFoundException, NoSuchFieldException {
            if (mResourcesInLoadedApk == null) {
                mResourcesInLoadedApk = HiddenApiReflection.findField(getLoadedApkClass(), "mResources");
            }
            return mResourcesInLoadedApk;
        }

        static Field mStringBlocksInAssetManager() throws NoSuchFieldException {
            if (mStringBlocksField == null) {
                mStringBlocksField = HiddenApiReflection.findField((Class<?>) AssetManager.class, "mStringBlocks");
            }
            return mStringBlocksField;
        }

        static Field mThemeInContentImpl() throws ClassNotFoundException, NoSuchFieldException {
            if (mThemeInContentImpl == null) {
                mThemeInContentImpl = HiddenApiReflection.findField(getContextImplClass(), "mTheme");
            }
            return mThemeInContentImpl;
        }

        static Field mThemeInContextThemeWrapper() throws NoSuchFieldException {
            if (mThemeInContextThemeWrapper == null) {
                mThemeInContextThemeWrapper = HiddenApiReflection.findField((Class<?>) ContextThemeWrapper.class, "mTheme");
            }
            return mThemeInContextThemeWrapper;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkOrUpdateResources(Context context, Resources resources) throws SplitCompatResourcesException {
        try {
            List<String> loadedResourcesDirs = getLoadedResourcesDirs(resources.getAssets());
            Collection<String> loadedSplitPaths = getLoadedSplitPaths();
            if (loadedSplitPaths == null || loadedSplitPaths.isEmpty() || loadedResourcesDirs.containsAll(loadedSplitPaths)) {
                return;
            }
            ArrayList arrayList = new ArrayList();
            for (String str : loadedSplitPaths) {
                if (!loadedResourcesDirs.contains(str)) {
                    arrayList.add(str);
                }
            }
            try {
                installSplitResDirs(context, resources, arrayList);
            } catch (Throwable th) {
                throw new SplitCompatResourcesException("Failed to install resources " + arrayList.toString() + " for " + context.getClass().getName(), th);
            }
        } catch (Throwable th2) {
            throw new SplitCompatResourcesException("Failed to get all loaded split resources for " + context.getClass().getName(), th2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<String> getLoadedResourcesDirs(AssetManager assetManager) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        ArrayList arrayList = new ArrayList();
        if (Build.VERSION.SDK_INT >= 28) {
            Object[] objArr = (Object[]) VersionCompat.getGetApkAssetsMethod().invoke(assetManager, new Object[0]);
            if (objArr != null) {
                for (Object obj : objArr) {
                    arrayList.add((String) VersionCompat.getGetAssetPathMethod().invoke(obj, new Object[0]));
                }
            }
        } else {
            Object[] objArr2 = (Object[]) VersionCompat.mStringBlocksInAssetManager().get(assetManager);
            if (objArr2 != null && objArr2.length > 0) {
                int length = objArr2.length;
                SplitLog.i(TAG, "Total resources count: " + length, new Object[0]);
                for (int i = 1; i <= length; i++) {
                    try {
                        arrayList.add((String) VersionCompat.getGetCookieNameMethod().invoke(assetManager, Integer.valueOf(i)));
                    } catch (Throwable th) {
                        SplitLog.w(TAG, "Unable to get cookie name for resources index " + i, th);
                    }
                }
            }
        }
        return arrayList;
    }

    private static Collection<String> getLoadedSplitPaths() {
        SplitLoadManager splitLoadManagerService = SplitLoadManagerService.getInstance();
        if (splitLoadManagerService != null) {
            return splitLoadManagerService.getLoadedSplitApkPaths();
        }
        return null;
    }

    private static SplitResourcesLoader getSplitResourcesLoader() {
        Iterator it = ServiceLoader.load(SplitResourcesLoader.class).iterator();
        return it.hasNext() ? (SplitResourcesLoader) it.next() : new DefaultSplitResourcesLoader();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void installSplitResDirs(final Context context, final Resources resources, final List<String> list) throws Throwable {
        if (Build.VERSION.SDK_INT >= 21) {
            V21.installSplitResDirs(resources, list);
        } else if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            SplitLog.i(TAG, "Install res on main thread", new Object[0]);
            V14.installSplitResDirs(context, resources, list);
        } else {
            Object obj = sLock;
            synchronized (obj) {
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.iqiyi.android.qigsaw.core.splitload.SplitCompatResourcesLoader.1
                    @Override // java.lang.Runnable
                    public void run() {
                        synchronized (SplitCompatResourcesLoader.sLock) {
                            try {
                                V14.installSplitResDirs(context, resources, list);
                                SplitCompatResourcesLoader.sLock.notify();
                            } catch (Throwable th) {
                                throw new RuntimeException(th);
                            }
                        }
                    }
                });
                obj.wait();
            }
        }
    }

    public static void loadResources(Context context, Resources resources) throws Throwable {
        resourcesLoader.loadResources(context, resources);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void loadResources(Context context, Resources resources, String str) throws Throwable {
        resourcesLoader.loadResources(context, resources, str);
    }
}
