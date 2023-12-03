package com.xiaomi.micloudsdk.stat;

/* loaded from: classes2.dex */
public class MiCloudNetEventStatInjector {
    private IMiCloudNetEventStatCallback mIMiCloudStatCallback;
    private boolean mIsInitialized;

    /* loaded from: classes2.dex */
    private static class MiCloudNetEventStatInjectorHolder {
        private static final MiCloudNetEventStatInjector instance = new MiCloudNetEventStatInjector();
    }

    private MiCloudNetEventStatInjector() {
        this.mIsInitialized = false;
    }

    public static MiCloudNetEventStatInjector getInstance() {
        return MiCloudNetEventStatInjectorHolder.instance;
    }

    public void addNetFailedEvent(NetFailedStatParam netFailedStatParam) {
        IMiCloudNetEventStatCallback iMiCloudNetEventStatCallback = this.mIMiCloudStatCallback;
        if (iMiCloudNetEventStatCallback != null) {
            iMiCloudNetEventStatCallback.onAddNetFailedEvent(netFailedStatParam);
        }
    }

    public void addNetSuccessEvent(NetSuccessStatParam netSuccessStatParam) {
        IMiCloudNetEventStatCallback iMiCloudNetEventStatCallback = this.mIMiCloudStatCallback;
        if (iMiCloudNetEventStatCallback != null) {
            iMiCloudNetEventStatCallback.onAddNetSuccessEvent(netSuccessStatParam);
        }
    }
}
