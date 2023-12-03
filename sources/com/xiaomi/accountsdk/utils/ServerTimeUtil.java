package com.xiaomi.accountsdk.utils;

import java.util.concurrent.CopyOnWriteArraySet;

/* loaded from: classes2.dex */
public class ServerTimeUtil {
    private static final CopyOnWriteArraySet<ServerTimeAlignedListener> listeners = new CopyOnWriteArraySet<>();

    /* loaded from: classes2.dex */
    public interface ServerTimeAlignedListener {
    }

    public static void addServerTimeChangedListener(ServerTimeAlignedListener serverTimeAlignedListener) {
        if (serverTimeAlignedListener == null) {
            throw new IllegalArgumentException("listener == null");
        }
        listeners.add(serverTimeAlignedListener);
    }

    public static void removeServerTimeChangedListener(ServerTimeAlignedListener serverTimeAlignedListener) {
        listeners.remove(serverTimeAlignedListener);
    }
}
