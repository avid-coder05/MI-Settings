package miuix.autodensity;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/* loaded from: classes5.dex */
public class ResourcesImplHelper {
    static boolean canChangeWithSingleActivity() {
        return Build.VERSION.SDK_INT >= 24;
    }

    private static Object createResourcesImpl(Resources resources) throws Exception {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.setTo(resources.getDisplayMetrics());
        DensityConfigManager.getInstance().getDefaultConfig().copyValueToDM(displayMetrics);
        Configuration configuration = new Configuration();
        configuration.setTo(resources.getConfiguration());
        configuration.densityDpi = displayMetrics.densityDpi;
        configuration.fontScale = displayMetrics.scaledDensity / displayMetrics.density;
        Class<?> cls = Class.forName("android.content.res.Resources");
        Object newInstance = cls.getConstructor(AssetManager.class, DisplayMetrics.class, Configuration.class).newInstance(resources.getAssets(), displayMetrics, configuration);
        Field declaredField = cls.getDeclaredField("mResourcesImpl");
        declaredField.setAccessible(true);
        return declaredField.get(newInstance);
    }

    private static Method findMethod(Object obj, String str, Class<?>... clsArr) throws NoSuchMethodException {
        for (Class<?> cls = obj.getClass(); cls != null; cls = cls.getSuperclass()) {
            try {
                Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
                if (!declaredMethod.isAccessible()) {
                    declaredMethod.setAccessible(true);
                }
                return declaredMethod;
            } catch (NoSuchMethodException unused) {
            }
        }
        throw new NoSuchMethodException("Method " + str + " with parameters " + Arrays.asList(clsArr) + " not found in " + obj.getClass());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setImpl(Resources resources) {
        if (canChangeWithSingleActivity()) {
            try {
                findMethod(resources, "setImpl", Class.forName("android.content.res.ResourcesImpl")).invoke(resources, createResourcesImpl(resources));
            } catch (Exception e) {
                Log.w("AutoDensity", "try catch setToDefaultResourcesImpl error", e);
            }
        }
    }
}
