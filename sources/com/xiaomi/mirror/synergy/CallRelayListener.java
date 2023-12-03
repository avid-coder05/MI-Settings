package com.xiaomi.mirror.synergy;

/* loaded from: classes2.dex */
public interface CallRelayListener {
    void onAddressUpdate(String str);

    void onLost();

    void onMessage(String str);
}
