package com.miui.maml.elements.filament;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MeshLoader.kt */
/* loaded from: classes2.dex */
public final class MeshLoaderKt$loadDefaultMesh$Vertex {
    private final float u;
    private final float v;
    private final float x;
    private final float y;

    public MeshLoaderKt$loadDefaultMesh$Vertex(float f, float f2, float f3, float f4) {
        this.x = f;
        this.y = f2;
        this.u = f3;
        this.v = f4;
    }

    public boolean equals(@Nullable Object obj) {
        if (this != obj) {
            if (obj instanceof MeshLoaderKt$loadDefaultMesh$Vertex) {
                MeshLoaderKt$loadDefaultMesh$Vertex meshLoaderKt$loadDefaultMesh$Vertex = (MeshLoaderKt$loadDefaultMesh$Vertex) obj;
                return Float.compare(this.x, meshLoaderKt$loadDefaultMesh$Vertex.x) == 0 && Float.compare(this.y, meshLoaderKt$loadDefaultMesh$Vertex.y) == 0 && Float.compare(this.u, meshLoaderKt$loadDefaultMesh$Vertex.u) == 0 && Float.compare(this.v, meshLoaderKt$loadDefaultMesh$Vertex.v) == 0;
            }
            return false;
        }
        return true;
    }

    public final float getU() {
        return this.u;
    }

    public final float getV() {
        return this.v;
    }

    public final float getX() {
        return this.x;
    }

    public final float getY() {
        return this.y;
    }

    public int hashCode() {
        return (((((Float.floatToIntBits(this.x) * 31) + Float.floatToIntBits(this.y)) * 31) + Float.floatToIntBits(this.u)) * 31) + Float.floatToIntBits(this.v);
    }

    @NotNull
    public String toString() {
        return "Vertex(x=" + this.x + ", y=" + this.y + ", u=" + this.u + ", v=" + this.v + ")";
    }
}
