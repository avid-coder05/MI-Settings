package com.xiaomi.accountsdk.request.log;

/* loaded from: classes2.dex */
public class NetworkRequestLogger {
    private volatile LogPrinter mLogPrinter;

    /* loaded from: classes2.dex */
    private static class InstanceSingleton {
        public static final NetworkRequestLogger sInstance = new NetworkRequestLogger();
    }

    /* loaded from: classes2.dex */
    public interface LogPrinter {
        void print(String str, Object... objArr);
    }

    private NetworkRequestLogger() {
    }

    public static NetworkRequestLogger getInstance() {
        return InstanceSingleton.sInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void print(String str, Object... objArr) {
        LogPrinter logPrinter = this.mLogPrinter;
        if (logPrinter != null) {
            logPrinter.print(str, objArr);
        }
    }
}
