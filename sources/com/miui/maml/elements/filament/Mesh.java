package com.miui.maml.elements.filament;

import com.google.android.filament.Box;
import com.google.android.filament.Entity;
import com.google.android.filament.IndexBuffer;
import com.google.android.filament.VertexBuffer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MeshLoader.kt */
/* loaded from: classes2.dex */
public final class Mesh {
    @NotNull
    private final Box aabb;
    @NotNull
    private final IndexBuffer indexBuffer;
    private final int renderable;
    @NotNull
    private final VertexBuffer vertexBuffer;

    public Mesh(@Entity int i, @NotNull IndexBuffer indexBuffer, @NotNull VertexBuffer vertexBuffer, @NotNull Box aabb) {
        Intrinsics.checkNotNullParameter(indexBuffer, "indexBuffer");
        Intrinsics.checkNotNullParameter(vertexBuffer, "vertexBuffer");
        Intrinsics.checkNotNullParameter(aabb, "aabb");
        this.renderable = i;
        this.indexBuffer = indexBuffer;
        this.vertexBuffer = vertexBuffer;
        this.aabb = aabb;
    }

    public boolean equals(@Nullable Object obj) {
        if (this != obj) {
            if (obj instanceof Mesh) {
                Mesh mesh = (Mesh) obj;
                return this.renderable == mesh.renderable && Intrinsics.areEqual(this.indexBuffer, mesh.indexBuffer) && Intrinsics.areEqual(this.vertexBuffer, mesh.vertexBuffer) && Intrinsics.areEqual(this.aabb, mesh.aabb);
            }
            return false;
        }
        return true;
    }

    @NotNull
    public final IndexBuffer getIndexBuffer() {
        return this.indexBuffer;
    }

    public final int getRenderable() {
        return this.renderable;
    }

    @NotNull
    public final VertexBuffer getVertexBuffer() {
        return this.vertexBuffer;
    }

    public int hashCode() {
        int i = this.renderable * 31;
        IndexBuffer indexBuffer = this.indexBuffer;
        int hashCode = (i + (indexBuffer != null ? indexBuffer.hashCode() : 0)) * 31;
        VertexBuffer vertexBuffer = this.vertexBuffer;
        int hashCode2 = (hashCode + (vertexBuffer != null ? vertexBuffer.hashCode() : 0)) * 31;
        Box box = this.aabb;
        return hashCode2 + (box != null ? box.hashCode() : 0);
    }

    @NotNull
    public String toString() {
        return "Mesh(renderable=" + this.renderable + ", indexBuffer=" + this.indexBuffer + ", vertexBuffer=" + this.vertexBuffer + ", aabb=" + this.aabb + ")";
    }
}
