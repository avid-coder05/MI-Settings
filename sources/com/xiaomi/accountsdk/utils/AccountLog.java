package com.xiaomi.accountsdk.utils;

import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;

/* loaded from: classes2.dex */
public abstract class AccountLog {
    private static final AccountLog sAndroidLog;
    private static AccountLog sInstance;

    static {
        AccountLog accountLog = new AccountLog() { // from class: com.xiaomi.accountsdk.utils.AccountLog.1
            @Override // com.xiaomi.accountsdk.utils.AccountLog
            public int logD(String str, String str2) {
                return Log.d(str, str2);
            }

            @Override // com.xiaomi.accountsdk.utils.AccountLog
            public int logE(String str, String str2) {
                return Log.e(str, str2);
            }

            @Override // com.xiaomi.accountsdk.utils.AccountLog
            public int logI(String str, String str2) {
                return Log.i(str, str2);
            }

            @Override // com.xiaomi.accountsdk.utils.AccountLog
            public int logW(String str, String str2) {
                return Log.w(str, str2);
            }
        };
        sAndroidLog = accountLog;
        sInstance = accountLog;
    }

    public static int d(String str, String str2, Throwable th) {
        return getInstance().logD(str, str2 + "\n" + enThrowableMsgIfHasIPAddress(th));
    }

    public static int e(String str, String str2, Throwable th) {
        return getInstance().logE(str, str2 + "\n" + enThrowableMsgIfHasIPAddress(th));
    }

    private static String enThrowableMsgIfHasIPAddress(Throwable th) {
        return th == null ? "" : IpFilterHelper.envIPAddressIfHas(getStackTraceAsString(th));
    }

    public static AccountLog getInstance() {
        return sInstance;
    }

    private static String getStackTraceAsString(Throwable th) {
        StringWriter stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    public static int i(String str, String str2) {
        return getInstance().logI(str, str2);
    }

    public static int w(String str, String str2) {
        return getInstance().logW(str, str2);
    }

    public static int w(String str, String str2, Throwable th) {
        return getInstance().logW(str, str2 + "\n" + enThrowableMsgIfHasIPAddress(th));
    }

    public static int w(String str, Throwable th) {
        return getInstance().logW(str, enThrowableMsgIfHasIPAddress(th));
    }

    protected abstract int logD(String str, String str2);

    protected abstract int logE(String str, String str2);

    protected abstract int logI(String str, String str2);

    protected abstract int logW(String str, String str2);
}
