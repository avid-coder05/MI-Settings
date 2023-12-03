package com.miui.maml.elements.filament;

import java.nio.ByteBuffer;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: MeshLoader.kt */
/* loaded from: classes2.dex */
final class MeshLoaderKt$loadDefaultMesh$1 extends Lambda {
    public static final MeshLoaderKt$loadDefaultMesh$1 INSTANCE = new MeshLoaderKt$loadDefaultMesh$1();

    MeshLoaderKt$loadDefaultMesh$1() {
        super(2);
    }

    @NotNull
    public final ByteBuffer invoke(@NotNull ByteBuffer put, @NotNull MeshLoaderKt$loadDefaultMesh$Vertex v) {
        Intrinsics.checkNotNullParameter(put, "$this$put");
        Intrinsics.checkNotNullParameter(v, "v");
        put.putFloat(v.getX());
        put.putFloat(v.getY());
        put.putFloat(v.getU());
        put.putFloat(v.getV());
        return put;
    }
}
