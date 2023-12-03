package com.miui.maml.elements.filament;

import com.google.android.filament.Box;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: MeshLoader.kt */
/* loaded from: classes2.dex */
final class Part {
    @NotNull
    private Box aabb = new Box();
    private long indexCount;
    private long materialID;
    private long maxIndex;
    private long minIndex;
    private long offset;

    public final long getIndexCount() {
        return this.indexCount;
    }

    public final long getMaterialID() {
        return this.materialID;
    }

    public final long getMaxIndex() {
        return this.maxIndex;
    }

    public final long getMinIndex() {
        return this.minIndex;
    }

    public final long getOffset() {
        return this.offset;
    }

    public final void setAabb(@NotNull Box box) {
        Intrinsics.checkNotNullParameter(box, "<set-?>");
        this.aabb = box;
    }

    public final void setIndexCount(long j) {
        this.indexCount = j;
    }

    public final void setMaterialID(long j) {
        this.materialID = j;
    }

    public final void setMaxIndex(long j) {
        this.maxIndex = j;
    }

    public final void setMinIndex(long j) {
        this.minIndex = j;
    }

    public final void setOffset(long j) {
        this.offset = j;
    }
}
