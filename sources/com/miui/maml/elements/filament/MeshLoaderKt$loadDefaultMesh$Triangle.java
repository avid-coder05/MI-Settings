package com.miui.maml.elements.filament;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MeshLoader.kt */
/* loaded from: classes2.dex */
public final class MeshLoaderKt$loadDefaultMesh$Triangle {
    private final short x;
    private final short y;
    private final short z;

    public MeshLoaderKt$loadDefaultMesh$Triangle(short s, short s2, short s3) {
        this.x = s;
        this.y = s2;
        this.z = s3;
    }

    public boolean equals(@Nullable Object obj) {
        if (this != obj) {
            if (obj instanceof MeshLoaderKt$loadDefaultMesh$Triangle) {
                MeshLoaderKt$loadDefaultMesh$Triangle meshLoaderKt$loadDefaultMesh$Triangle = (MeshLoaderKt$loadDefaultMesh$Triangle) obj;
                return this.x == meshLoaderKt$loadDefaultMesh$Triangle.x && this.y == meshLoaderKt$loadDefaultMesh$Triangle.y && this.z == meshLoaderKt$loadDefaultMesh$Triangle.z;
            }
            return false;
        }
        return true;
    }

    public final short getX() {
        return this.x;
    }

    public final short getY() {
        return this.y;
    }

    public final short getZ() {
        return this.z;
    }

    public int hashCode() {
        return (((this.x * 31) + this.y) * 31) + this.z;
    }

    @NotNull
    public String toString() {
        return "Triangle(x=" + ((int) this.x) + ", y=" + ((int) this.y) + ", z=" + ((int) this.z) + ")";
    }
}
