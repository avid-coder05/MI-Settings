package com.google.zxing.pdf417;

/* loaded from: classes2.dex */
public final class PDF417ResultMetadata {
    private String fileId;
    private boolean lastSegment;
    private int[] optionalData;
    private int segmentIndex;

    public void setFileId(String str) {
        this.fileId = str;
    }

    public void setLastSegment(boolean z) {
        this.lastSegment = z;
    }

    public void setOptionalData(int[] iArr) {
        this.optionalData = iArr;
    }

    public void setSegmentIndex(int i) {
        this.segmentIndex = i;
    }
}
