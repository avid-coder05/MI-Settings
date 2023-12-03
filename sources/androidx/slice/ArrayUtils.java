package androidx.slice;

import androidx.core.util.ObjectsCompat;
import java.lang.reflect.Array;

/* loaded from: classes.dex */
class ArrayUtils {
    public static <T> T[] appendElement(Class<T> kind, T[] array, T element) {
        T[] tArr;
        int i = 0;
        if (array != null) {
            int length = array.length;
            tArr = (T[]) ((Object[]) Array.newInstance((Class<?>) kind, length + 1));
            System.arraycopy(array, 0, tArr, 0, length);
            i = length;
        } else {
            tArr = (T[]) ((Object[]) Array.newInstance((Class<?>) kind, 1));
        }
        tArr[i] = element;
        return tArr;
    }

    public static <T> boolean contains(T[] array, T item) {
        for (T t : array) {
            if (ObjectsCompat.equals(t, item)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T[] removeElement(Class<T> kind, T[] array, T element) {
        if (array == null || !contains(array, element)) {
            return array;
        }
        int length = array.length;
        for (int i = 0; i < length; i++) {
            if (ObjectsCompat.equals(array[i], element)) {
                if (length == 1) {
                    return null;
                }
                T[] tArr = (T[]) ((Object[]) Array.newInstance((Class<?>) kind, length - 1));
                System.arraycopy(array, 0, tArr, 0, i);
                System.arraycopy(array, i + 1, tArr, i, (length - i) - 1);
                return tArr;
            }
        }
        return array;
    }
}
