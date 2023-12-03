package com.miui.maml.elements.filament;

import java.nio.ByteBuffer;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: MeshLoader.kt */
/* loaded from: classes2.dex */
final class MeshLoaderKt$loadDefaultMesh$2 extends Lambda {
    public static final MeshLoaderKt$loadDefaultMesh$2 INSTANCE = new MeshLoaderKt$loadDefaultMesh$2();

    MeshLoaderKt$loadDefaultMesh$2() {
        super(2);
    }

    @NotNull
    public final ByteBuffer invoke(@NotNull ByteBuffer put, @NotNull MeshLoaderKt$loadDefaultMesh$Triangle t) {
        Intrinsics.checkNotNullParameter(put, "$this$put");
        Intrinsics.checkNotNullParameter(t, "t");
        put.putShort(t.getX());
        put.putShort(t.getY());
        put.putShort(t.getZ());
        return put;
    }
}
