package kotlin.text;

import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: classes2.dex */
public final class StringsKt extends StringsKt___StringsKt {
    public static /* bridge */ /* synthetic */ <T> void appendElement(@NotNull Appendable appendable, T t, @Nullable Function1<? super T, ? extends CharSequence> function1) {
        StringsKt__AppendableKt.appendElement(appendable, t, function1);
    }

    public static /* bridge */ /* synthetic */ String replace$default(String str, String str2, String str3, boolean z, int i, Object obj) {
        return StringsKt__StringsJVMKt.replace$default(str, str2, str3, z, i, obj);
    }
}
