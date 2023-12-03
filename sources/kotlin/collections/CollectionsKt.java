package kotlin.collections;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/* loaded from: classes2.dex */
public final class CollectionsKt extends CollectionsKt___CollectionsKt {
    public static /* bridge */ /* synthetic */ int binarySearch$default(List list, Comparable comparable, int i, int i2, int i3, Object obj) {
        return CollectionsKt__CollectionsKt.binarySearch$default(list, comparable, i, i2, i3, obj);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ <T> List<T> mutableListOf(@NotNull T... tArr) {
        return CollectionsKt__CollectionsKt.mutableListOf(tArr);
    }

    public static /* bridge */ /* synthetic */ <T extends Comparable<? super T>> void sort(@NotNull List<T> list) {
        CollectionsKt__MutableCollectionsJVMKt.sort(list);
    }
}
