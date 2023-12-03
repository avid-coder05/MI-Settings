package com.iqiyi.android.qigsaw.core.splitreport;

/* loaded from: classes2.dex */
public final class SplitInstallError extends SplitBriefInfo {
    public static final int APK_FILE_ILLEGAL = -11;
    public static final int CLASSLOADER_CREATE_FAILED = -17;
    public static final int DEX_EXTRACT_FAILED = -14;
    public static final int DEX_OAT_FAILED = -18;
    public static final int INTERNAL_ERROR = -100;
    public static final int LIB_EXTRACT_FAILED = -15;
    public static final int MARK_CREATE_FAILED = -16;
    public static final int MD5_ERROR = -13;
    public static final int SIGNATURE_MISMATCH = -12;
    public final Throwable cause;
    public final int errorCode;

    public SplitInstallError(SplitBriefInfo splitBriefInfo, int i, Throwable th) {
        super(splitBriefInfo.splitName, splitBriefInfo.version, splitBriefInfo.builtIn);
        this.errorCode = i;
        this.cause = th;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitBriefInfo
    public String toString() {
        return "{\"splitName\":\"" + this.splitName + "\",\"version\":\"" + this.version + "\",\"builtIn\":" + this.builtIn + "\",errorCode\":" + this.errorCode + "\",errorMsg\":\"" + this.cause.getMessage() + "\"}";
    }
}
