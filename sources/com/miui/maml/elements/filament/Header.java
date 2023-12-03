package com.miui.maml.elements.filament;

import com.google.android.filament.Box;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: MeshLoader.kt */
/* loaded from: classes2.dex */
final class Header {
    @NotNull
    private Box aabb = new Box();
    private long colorOffset;
    private long colorStride;
    private long flags;
    private long indices16Bit;
    private long indicesSizeInBytes;
    private long parts;
    private long posOffset;
    private long positionStride;
    private long tangentOffset;
    private long tangentStride;
    private long totalIndices;
    private long totalVertices;
    private long uv0Offset;
    private long uv0Stride;
    private long uv1Offset;
    private long uv1Stride;
    private boolean valid;
    private long versionNumber;
    private long verticesSizeInBytes;

    @NotNull
    public final Box getAabb() {
        return this.aabb;
    }

    public final long getColorOffset() {
        return this.colorOffset;
    }

    public final long getColorStride() {
        return this.colorStride;
    }

    public final long getFlags() {
        return this.flags;
    }

    public final long getIndices16Bit() {
        return this.indices16Bit;
    }

    public final long getIndicesSizeInBytes() {
        return this.indicesSizeInBytes;
    }

    public final long getParts() {
        return this.parts;
    }

    public final long getPosOffset() {
        return this.posOffset;
    }

    public final long getPositionStride() {
        return this.positionStride;
    }

    public final long getTangentOffset() {
        return this.tangentOffset;
    }

    public final long getTangentStride() {
        return this.tangentStride;
    }

    public final long getTotalIndices() {
        return this.totalIndices;
    }

    public final long getTotalVertices() {
        return this.totalVertices;
    }

    public final long getUv0Offset() {
        return this.uv0Offset;
    }

    public final long getUv0Stride() {
        return this.uv0Stride;
    }

    public final long getUv1Offset() {
        return this.uv1Offset;
    }

    public final long getUv1Stride() {
        return this.uv1Stride;
    }

    public final long getVerticesSizeInBytes() {
        return this.verticesSizeInBytes;
    }

    public final void setAabb(@NotNull Box box) {
        Intrinsics.checkNotNullParameter(box, "<set-?>");
        this.aabb = box;
    }

    public final void setColorOffset(long j) {
        this.colorOffset = j;
    }

    public final void setColorStride(long j) {
        this.colorStride = j;
    }

    public final void setFlags(long j) {
        this.flags = j;
    }

    public final void setIndices16Bit(long j) {
        this.indices16Bit = j;
    }

    public final void setIndicesSizeInBytes(long j) {
        this.indicesSizeInBytes = j;
    }

    public final void setParts(long j) {
        this.parts = j;
    }

    public final void setPosOffset(long j) {
        this.posOffset = j;
    }

    public final void setPositionStride(long j) {
        this.positionStride = j;
    }

    public final void setTangentOffset(long j) {
        this.tangentOffset = j;
    }

    public final void setTangentStride(long j) {
        this.tangentStride = j;
    }

    public final void setTotalIndices(long j) {
        this.totalIndices = j;
    }

    public final void setTotalVertices(long j) {
        this.totalVertices = j;
    }

    public final void setUv0Offset(long j) {
        this.uv0Offset = j;
    }

    public final void setUv0Stride(long j) {
        this.uv0Stride = j;
    }

    public final void setUv1Offset(long j) {
        this.uv1Offset = j;
    }

    public final void setUv1Stride(long j) {
        this.uv1Stride = j;
    }

    public final void setValid(boolean z) {
        this.valid = z;
    }

    public final void setVersionNumber(long j) {
        this.versionNumber = j;
    }

    public final void setVerticesSizeInBytes(long j) {
        this.verticesSizeInBytes = j;
    }
}
