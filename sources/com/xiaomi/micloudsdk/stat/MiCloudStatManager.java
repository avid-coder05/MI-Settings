package com.xiaomi.micloudsdk.stat;

@Deprecated
/* loaded from: classes2.dex */
public class MiCloudStatManager {
    private IMiCloudStatCallback mCldStatCallback;
    private boolean mEnable;
    private boolean mIsInitialized;

    /* loaded from: classes2.dex */
    private static class Holder {
        private static final MiCloudStatManager _instance = new MiCloudStatManager();
    }

    private MiCloudStatManager() {
        this.mEnable = false;
        this.mIsInitialized = false;
    }

    public static MiCloudStatManager getInstance() {
        return Holder._instance;
    }

    public void addHttpEvent(String str, long j, long j2, int i, String str2) {
        IMiCloudStatCallback iMiCloudStatCallback;
        if (this.mEnable && this.mIsInitialized && (iMiCloudStatCallback = this.mCldStatCallback) != null) {
            iMiCloudStatCallback.onAddHttpEvent(str, j, j2, i, str2);
        }
    }
}
