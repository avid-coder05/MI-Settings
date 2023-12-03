package com.iqiyi.android.qigsaw.core.splitload;

/* loaded from: classes2.dex */
final class SplitLoadException extends Exception {
    private final int errorCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public SplitLoadException(int r3, java.lang.Throwable r4) {
        /*
            r2 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r1 = 32
            r0.<init>(r1)
            java.lang.String r1 = "Split Load Error: "
            r0.append(r1)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            r2.<init>(r0, r4)
            r2.errorCode = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iqiyi.android.qigsaw.core.splitload.SplitLoadException.<init>(int, java.lang.Throwable):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getErrorCode() {
        return this.errorCode;
    }
}
