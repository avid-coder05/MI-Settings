package androidx.core.util;

import android.os.Build;
import java.util.Arrays;
import java.util.Objects;

/* loaded from: classes.dex */
public class ObjectsCompat {
    public static boolean equals(Object a, Object b) {
        return Build.VERSION.SDK_INT >= 19 ? Objects.equals(a, b) : a == b || (a != null && a.equals(b));
    }

    public static int hash(Object... values) {
        return Build.VERSION.SDK_INT >= 19 ? Objects.hash(values) : Arrays.hashCode(values);
    }

    public static String toString(Object o, String nullDefault) {
        return o != null ? o.toString() : nullDefault;
    }
}
