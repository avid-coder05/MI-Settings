package kotlin;

import kotlin.internal.PlatformImplementationsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: Exceptions.kt */
/* loaded from: classes2.dex */
public class ExceptionsKt__ExceptionsKt {
    public static void addSuppressed(@NotNull Throwable addSuppressed, @NotNull Throwable exception) {
        Intrinsics.checkNotNullParameter(addSuppressed, "$this$addSuppressed");
        Intrinsics.checkNotNullParameter(exception, "exception");
        if (addSuppressed != exception) {
            PlatformImplementationsKt.IMPLEMENTATIONS.addSuppressed(addSuppressed, exception);
        }
    }
}
