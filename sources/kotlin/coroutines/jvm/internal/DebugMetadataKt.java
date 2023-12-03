package kotlin.coroutines.jvm.internal;

import java.lang.reflect.Field;
import kotlin.jvm.internal.Intrinsics;
import miui.provider.ExtraContacts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: DebugMetadata.kt */
/* loaded from: classes2.dex */
public final class DebugMetadataKt {
    private static final void checkDebugMetadataVersion(int i, int i2) {
        if (i2 <= i) {
            return;
        }
        throw new IllegalStateException(("Debug metadata version mismatch. Expected: " + i + ", got " + i2 + ". Please update the Kotlin standard library.").toString());
    }

    private static final DebugMetadata getDebugMetadataAnnotation(BaseContinuationImpl baseContinuationImpl) {
        return (DebugMetadata) baseContinuationImpl.getClass().getAnnotation(DebugMetadata.class);
    }

    private static final int getLabel(BaseContinuationImpl baseContinuationImpl) {
        try {
            Field field = baseContinuationImpl.getClass().getDeclaredField(ExtraContacts.ConferenceCalls.MembersColumns.LABEL);
            Intrinsics.checkNotNullExpressionValue(field, "field");
            field.setAccessible(true);
            Object obj = field.get(baseContinuationImpl);
            if (!(obj instanceof Integer)) {
                obj = null;
            }
            Integer num = (Integer) obj;
            return (num != null ? num.intValue() : 0) - 1;
        } catch (Exception unused) {
            return -1;
        }
    }

    @Nullable
    public static final StackTraceElement getStackTraceElement(@NotNull BaseContinuationImpl getStackTraceElementImpl) {
        String str;
        Intrinsics.checkNotNullParameter(getStackTraceElementImpl, "$this$getStackTraceElementImpl");
        DebugMetadata debugMetadataAnnotation = getDebugMetadataAnnotation(getStackTraceElementImpl);
        if (debugMetadataAnnotation != null) {
            checkDebugMetadataVersion(1, debugMetadataAnnotation.v());
            int label = getLabel(getStackTraceElementImpl);
            int i = label < 0 ? -1 : debugMetadataAnnotation.l()[label];
            String moduleName = ModuleNameRetriever.INSTANCE.getModuleName(getStackTraceElementImpl);
            if (moduleName == null) {
                str = debugMetadataAnnotation.c();
            } else {
                str = moduleName + '/' + debugMetadataAnnotation.c();
            }
            return new StackTraceElement(str, debugMetadataAnnotation.m(), debugMetadataAnnotation.f(), i);
        }
        return null;
    }
}
