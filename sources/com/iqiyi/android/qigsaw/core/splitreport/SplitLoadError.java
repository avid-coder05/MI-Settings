package com.iqiyi.android.qigsaw.core.splitreport;

/* loaded from: classes2.dex */
public class SplitLoadError extends SplitBriefInfo {
    public static final int ACTIVATE_APPLICATION_FAILED = -25;
    public static final int CREATE_APPLICATION_FAILED = -24;
    public static final int CREATE_CLASSLOADER_FAILED = -27;
    public static final int CREATE_PROVIDERS_FAILED = -26;
    public static final int INTERNAL_ERROR = -100;
    public static final int INTERRUPTED_ERROR = -99;
    public static final int LOAD_DEX_FAILED = -23;
    public static final int LOAD_LIB_FAILED = -22;
    public static final int LOAD_RES_FAILED = -21;
    public final Throwable cause;
    public final int errorCode;

    public SplitLoadError(SplitBriefInfo splitBriefInfo, int i, Throwable th) {
        super(splitBriefInfo.splitName, splitBriefInfo.version, splitBriefInfo.builtIn);
        this.errorCode = i;
        this.cause = th;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitreport.SplitBriefInfo
    public String toString() {
        return "{\"splitName\":\"" + this.splitName + "\",\"version\":\"" + this.version + "\",\"builtIn\":" + this.builtIn + "\",errorCode\":" + this.errorCode + "\",errorMsg\":\"" + this.cause.getMessage() + "\"}";
    }
}
