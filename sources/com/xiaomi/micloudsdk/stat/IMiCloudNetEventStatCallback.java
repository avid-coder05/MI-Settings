package com.xiaomi.micloudsdk.stat;

/* loaded from: classes2.dex */
public interface IMiCloudNetEventStatCallback {
    void onAddNetFailedEvent(NetFailedStatParam netFailedStatParam);

    void onAddNetSuccessEvent(NetSuccessStatParam netSuccessStatParam);
}
