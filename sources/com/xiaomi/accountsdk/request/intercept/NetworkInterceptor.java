package com.xiaomi.accountsdk.request.intercept;

/* loaded from: classes2.dex */
public class NetworkInterceptor {

    /* loaded from: classes2.dex */
    private static class Holder {
        private static volatile NetworkInterceptCallback sInstance = new EmptyNetworkInterceptCallback();
    }

    public static NetworkInterceptCallback get() {
        return Holder.sInstance;
    }
}
