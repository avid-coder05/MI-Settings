package com.iqiyi.android.qigsaw.core.common;

import android.util.Log;

/* loaded from: classes2.dex */
public class SplitLog {
    private static final String TAG = "Split.SplitLog";
    private static Logger defaultLogger;
    private static Logger splitLogImp;

    /* loaded from: classes2.dex */
    public interface Logger {
        void d(String str, String str2, Throwable th);

        void d(String str, String str2, Object... objArr);

        void e(String str, String str2, Throwable th);

        void e(String str, String str2, Object... objArr);

        void i(String str, String str2, Throwable th);

        void i(String str, String str2, Object... objArr);

        void printErrStackTrace(String str, Throwable th, String str2, Object... objArr);

        void v(String str, String str2, Throwable th);

        void v(String str, String str2, Object... objArr);

        void w(String str, String str2, Throwable th);

        void w(String str, String str2, Object... objArr);
    }

    static {
        Logger logger = new Logger() { // from class: com.iqiyi.android.qigsaw.core.common.SplitLog.1
            @Override // com.iqiyi.android.qigsaw.core.common.SplitLog.Logger
            public void d(String str, String str2, Throwable th) {
                Log.d(str, str2, th);
            }

            @Override // com.iqiyi.android.qigsaw.core.common.SplitLog.Logger
            public void d(String str, String str2, Object... objArr) {
                if (objArr != null && objArr.length != 0) {
                    str2 = String.format(str2, objArr);
                }
                Log.d(str, str2);
            }

            @Override // com.iqiyi.android.qigsaw.core.common.SplitLog.Logger
            public void e(String str, String str2, Throwable th) {
                Log.e(str, str2, th);
            }

            @Override // com.iqiyi.android.qigsaw.core.common.SplitLog.Logger
            public void e(String str, String str2, Object... objArr) {
                if (objArr != null && objArr.length != 0) {
                    str2 = String.format(str2, objArr);
                }
                Log.e(str, str2);
            }

            @Override // com.iqiyi.android.qigsaw.core.common.SplitLog.Logger
            public void i(String str, String str2, Throwable th) {
                Log.i(str, str2, th);
            }

            @Override // com.iqiyi.android.qigsaw.core.common.SplitLog.Logger
            public void i(String str, String str2, Object... objArr) {
                if (objArr != null && objArr.length != 0) {
                    str2 = String.format(str2, objArr);
                }
                Log.i(str, str2);
            }

            @Override // com.iqiyi.android.qigsaw.core.common.SplitLog.Logger
            public void printErrStackTrace(String str, Throwable th, String str2, Object... objArr) {
                if (objArr != null && objArr.length != 0) {
                    str2 = String.format(str2, objArr);
                }
                if (str2 == null) {
                    str2 = "";
                }
                Log.e(str, str2 + "  " + Log.getStackTraceString(th));
            }

            @Override // com.iqiyi.android.qigsaw.core.common.SplitLog.Logger
            public void v(String str, String str2, Throwable th) {
                Log.v(str, str2, th);
            }

            @Override // com.iqiyi.android.qigsaw.core.common.SplitLog.Logger
            public void v(String str, String str2, Object... objArr) {
                if (objArr != null && objArr.length != 0) {
                    str2 = String.format(str2, objArr);
                }
                Log.v(str, str2);
            }

            @Override // com.iqiyi.android.qigsaw.core.common.SplitLog.Logger
            public void w(String str, String str2, Throwable th) {
                Log.w(str, str2, th);
            }

            @Override // com.iqiyi.android.qigsaw.core.common.SplitLog.Logger
            public void w(String str, String str2, Object... objArr) {
                if (objArr != null && objArr.length != 0) {
                    str2 = String.format(str2, objArr);
                }
                Log.w(str, str2);
            }
        };
        defaultLogger = logger;
        splitLogImp = logger;
    }

    private SplitLog() {
    }

    public static void d(String str, String str2, Throwable th) {
        Logger logger = splitLogImp;
        if (logger != null) {
            logger.d(str, str2, th);
        }
    }

    public static void d(String str, String str2, Object... objArr) {
        Logger logger = splitLogImp;
        if (logger != null) {
            logger.d(str, str2, objArr);
        }
    }

    public static void e(String str, String str2, Throwable th) {
        Logger logger = splitLogImp;
        if (logger != null) {
            logger.e(str, str2, th);
        }
    }

    public static void e(String str, String str2, Object... objArr) {
        Logger logger = splitLogImp;
        if (logger != null) {
            logger.e(str, str2, objArr);
        }
    }

    public static Logger getImpl() {
        return splitLogImp;
    }

    public static void i(String str, String str2, Throwable th) {
        Logger logger = splitLogImp;
        if (logger != null) {
            logger.i(str, str2, th);
        }
    }

    public static void i(String str, String str2, Object... objArr) {
        Logger logger = splitLogImp;
        if (logger != null) {
            logger.i(str, str2, objArr);
        }
    }

    public static void printErrStackTrace(String str, Throwable th, String str2, Object... objArr) {
        Logger logger = splitLogImp;
        if (logger != null) {
            logger.printErrStackTrace(str, th, str2, objArr);
        }
    }

    public static void setSplitLogImp(Logger logger) {
        splitLogImp = logger;
    }

    public static void v(String str, String str2, Throwable th) {
        Logger logger = splitLogImp;
        if (logger != null) {
            logger.v(str, str2, th);
        }
    }

    public static void v(String str, String str2, Object... objArr) {
        Logger logger = splitLogImp;
        if (logger != null) {
            logger.v(str, str2, objArr);
        }
    }

    public static void w(String str, String str2, Throwable th) {
        Logger logger = splitLogImp;
        if (logger != null) {
            logger.w(str, str2, th);
        }
    }

    public static void w(String str, String str2, Object... objArr) {
        Logger logger = splitLogImp;
        if (logger != null) {
            logger.w(str, str2, objArr);
        }
    }
}
