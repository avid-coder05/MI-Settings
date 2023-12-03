package kotlin.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: _Arrays.kt */
/* loaded from: classes2.dex */
public class ArraysKt___ArraysKt extends ArraysKt___ArraysJvmKt {
    public static final <T> boolean contains(@NotNull T[] contains, T t) {
        Intrinsics.checkNotNullParameter(contains, "$this$contains");
        return indexOf(contains, t) >= 0;
    }

    public static final <T> int indexOf(@NotNull T[] indexOf, T t) {
        Intrinsics.checkNotNullParameter(indexOf, "$this$indexOf");
        int i = 0;
        if (t == null) {
            int length = indexOf.length;
            while (i < length) {
                if (indexOf[i] == null) {
                    return i;
                }
                i++;
            }
            return -1;
        }
        int length2 = indexOf.length;
        while (i < length2) {
            if (Intrinsics.areEqual(t, indexOf[i])) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static char single(@NotNull char[] single) {
        Intrinsics.checkNotNullParameter(single, "$this$single");
        int length = single.length;
        if (length != 0) {
            if (length == 1) {
                return single[0];
            }
            throw new IllegalArgumentException("Array has more than one element.");
        }
        throw new NoSuchElementException("Array is empty.");
    }

    @Nullable
    public static <T> T singleOrNull(@NotNull T[] singleOrNull) {
        Intrinsics.checkNotNullParameter(singleOrNull, "$this$singleOrNull");
        if (singleOrNull.length == 1) {
            return singleOrNull[0];
        }
        return null;
    }

    @NotNull
    public static List<Integer> take(@NotNull int[] take, int i) {
        Intrinsics.checkNotNullParameter(take, "$this$take");
        if (!(i >= 0)) {
            throw new IllegalArgumentException(("Requested element count " + i + " is less than zero.").toString());
        } else if (i == 0) {
            return CollectionsKt__CollectionsKt.emptyList();
        } else {
            if (i >= take.length) {
                return toList(take);
            }
            if (i == 1) {
                return CollectionsKt__CollectionsJVMKt.listOf(Integer.valueOf(take[0]));
            }
            ArrayList arrayList = new ArrayList(i);
            int i2 = 0;
            for (int i3 : take) {
                arrayList.add(Integer.valueOf(i3));
                i2++;
                if (i2 == i) {
                    break;
                }
            }
            return arrayList;
        }
    }

    @NotNull
    public static final List<Integer> toList(@NotNull int[] toList) {
        Intrinsics.checkNotNullParameter(toList, "$this$toList");
        int length = toList.length;
        return length != 0 ? length != 1 ? toMutableList(toList) : CollectionsKt__CollectionsJVMKt.listOf(Integer.valueOf(toList[0])) : CollectionsKt__CollectionsKt.emptyList();
    }

    @NotNull
    public static final List<Integer> toMutableList(@NotNull int[] toMutableList) {
        Intrinsics.checkNotNullParameter(toMutableList, "$this$toMutableList");
        ArrayList arrayList = new ArrayList(toMutableList.length);
        for (int i : toMutableList) {
            arrayList.add(Integer.valueOf(i));
        }
        return arrayList;
    }

    @NotNull
    public static <T> List<T> toMutableList(@NotNull T[] toMutableList) {
        Intrinsics.checkNotNullParameter(toMutableList, "$this$toMutableList");
        return new ArrayList(CollectionsKt__CollectionsKt.asCollection(toMutableList));
    }
}
