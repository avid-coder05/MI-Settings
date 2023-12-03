package androidx.core.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewConfiguration;
import java.lang.reflect.Method;
import miui.content.res.ThemeResources;

/* loaded from: classes.dex */
public final class ViewConfigurationCompat {
    private static Method sGetScaledScrollFactorMethod;

    static {
        if (Build.VERSION.SDK_INT == 25) {
            try {
                sGetScaledScrollFactorMethod = ViewConfiguration.class.getDeclaredMethod("getScaledScrollFactor", new Class[0]);
            } catch (Exception unused) {
                Log.i("ViewConfigCompat", "Could not find method getScaledScrollFactor() on ViewConfiguration");
            }
        }
    }

    private static float getLegacyScrollFactor(ViewConfiguration config, Context context) {
        Method method;
        if (Build.VERSION.SDK_INT >= 25 && (method = sGetScaledScrollFactorMethod) != null) {
            try {
                return ((Integer) method.invoke(config, new Object[0])).intValue();
            } catch (Exception unused) {
                Log.i("ViewConfigCompat", "Could not find method getScaledScrollFactor() on ViewConfiguration");
            }
        }
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(16842829, typedValue, true)) {
            return typedValue.getDimension(context.getResources().getDisplayMetrics());
        }
        return 0.0f;
    }

    public static float getScaledHorizontalScrollFactor(ViewConfiguration config, Context context) {
        return Build.VERSION.SDK_INT >= 26 ? config.getScaledHorizontalScrollFactor() : getLegacyScrollFactor(config, context);
    }

    public static int getScaledHoverSlop(ViewConfiguration config) {
        return Build.VERSION.SDK_INT >= 28 ? config.getScaledHoverSlop() : config.getScaledTouchSlop() / 2;
    }

    public static float getScaledVerticalScrollFactor(ViewConfiguration config, Context context) {
        return Build.VERSION.SDK_INT >= 26 ? config.getScaledVerticalScrollFactor() : getLegacyScrollFactor(config, context);
    }

    public static boolean shouldShowMenuShortcutsWhenKeyboardPresent(ViewConfiguration config, Context context) {
        if (Build.VERSION.SDK_INT >= 28) {
            return config.shouldShowMenuShortcutsWhenKeyboardPresent();
        }
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("config_showMenuShortcutsWhenKeyboardPresent", "bool", ThemeResources.FRAMEWORK_PACKAGE);
        return identifier != 0 && resources.getBoolean(identifier);
    }
}
