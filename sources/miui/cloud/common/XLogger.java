package miui.cloud.common;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Locale;

/* loaded from: classes3.dex */
public class XLogger {
    private static final String DEFAULT_LOGGER_CLASS = "miui.cloud.common.XLogger";
    private static final String LOG_PREFIX = "##XLogger##";
    private static volatile LogSender sLogSender = new LogSender() { // from class: miui.cloud.common.XLogger.1
        private final boolean sLogOnAdb = new File(Environment.getExternalStorageDirectory(), "log_on_adb").exists();

        @Override // miui.cloud.common.XLogger.LogSender
        public void sendLog(int i, String str, String str2) {
            if (this.sLogOnAdb) {
                Log.println(i, str, str2);
            }
        }
    };
    private static volatile int sDefLogLevel = 4;
    private static volatile int sOutputLogLevel = 2;
    private static volatile boolean sEnableCallStacktrace = true;

    /* loaded from: classes3.dex */
    public interface LogSender {
        void sendLog(int i, String str, String str2);
    }

    public static void debug(String str, String str2, Object... objArr) {
        logAtLevelImp(3, str, DEFAULT_LOGGER_CLASS, formatStr(str2, objArr));
    }

    public static void enableStackTrace(boolean z) {
        sEnableCallStacktrace = z;
    }

    public static void error(String str, String str2, Object... objArr) {
        logAtLevelImp(6, str, DEFAULT_LOGGER_CLASS, formatStr(str2, objArr));
    }

    private static String formatStr(String str, Object... objArr) {
        try {
            String format = String.format(Locale.ENGLISH, str, objArr);
            if (objArr == null || objArr.length == 0) {
                return format;
            }
            Object obj = objArr[objArr.length - 1];
            if (obj instanceof Throwable) {
                return format + getThrowableString((Throwable) obj);
            }
            return format;
        } catch (IllegalFormatException e) {
            return str + " " + Arrays.toString(objArr) + " : " + getThrowableString(e);
        }
    }

    private static String getCallerInfo(String str) {
        boolean z = false;
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            if (stackTraceElement.getClassName().equals(str)) {
                z = true;
            } else if (z) {
                return String.format("%s::%s@%s:%s, thread:%s", stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getFileName(), Integer.valueOf(stackTraceElement.getLineNumber()), Long.valueOf(Thread.currentThread().getId()));
            }
        }
        return "";
    }

    public static LogSender getLogSender() {
        return sLogSender;
    }

    private static String getObjectString(Object obj) {
        return obj == null ? "NULL" : obj instanceof Throwable ? getThrowableString((Throwable) obj) : obj.toString();
    }

    private static String getThrowableString(Throwable th) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10 && th != null; i++) {
            if (sb.length() != 0) {
                sb.append(" *Caused by* ");
            }
            sb.append(th.toString());
            th = th.getCause();
        }
        if (th != null) {
            sb.append(" *and more...*");
        }
        return sb.toString();
    }

    public static String group(String str, Object... objArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[" + str + ": ");
        int length = objArr.length;
        boolean z = true;
        int i = 0;
        while (i < length) {
            Object obj = objArr[i];
            if (!z) {
                sb.append(", ");
            }
            sb.append(getObjectString(obj));
            i++;
            z = false;
        }
        sb.append("]");
        return sb.toString();
    }

    public static void info(String str, String str2, Object... objArr) {
        logAtLevelImp(4, str, DEFAULT_LOGGER_CLASS, formatStr(str2, objArr));
    }

    @Deprecated
    public static void log(Object... objArr) {
        logAtLevel(sDefLogLevel, objArr);
    }

    public static void logAtLevel(int i, String str, Object... objArr) {
        logAtLevelImp(i, null, str, objArr);
    }

    public static void logAtLevel(int i, Object... objArr) {
        logAtLevelImp(i, null, DEFAULT_LOGGER_CLASS, objArr);
    }

    private static void logAtLevelImp(int i, String str, String str2, Object... objArr) {
        LogSender logSender;
        Object obj;
        if (i >= sOutputLogLevel && (logSender = sLogSender) != null) {
            String str3 = sEnableCallStacktrace ? getCallerInfo(str2) + "--" : "";
            if (objArr.length == 1) {
                obj = getObjectString(objArr[0]);
            } else {
                StringBuilder sb = new StringBuilder();
                for (Object obj2 : objArr) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(getObjectString(obj2));
                }
                obj = sb;
            }
            if (str == null) {
                str = LOG_PREFIX;
            }
            logSender.sendLog(i, str, str3 + obj);
        }
    }

    public static void logd(Object... objArr) {
        logAtLevel(3, objArr);
    }

    public static void loge(Object... objArr) {
        logAtLevel(6, objArr);
    }

    public static void logi(Object... objArr) {
        logAtLevel(4, objArr);
    }

    public static void logv(Object... objArr) {
        logAtLevel(2, objArr);
    }

    public static void logw(Object... objArr) {
        logAtLevel(5, objArr);
    }

    public static void setDefLogLevel(int i) {
        sDefLogLevel = i;
    }

    public static void setLogSender(LogSender logSender) {
        sLogSender = logSender;
    }

    public static void setOutputLogLevel(int i) {
        sOutputLogLevel = i;
    }

    public static void verbose(String str, String str2, Object... objArr) {
        logAtLevelImp(2, str, DEFAULT_LOGGER_CLASS, formatStr(str2, objArr));
    }

    public static void warn(String str, String str2, Object... objArr) {
        logAtLevelImp(5, str, DEFAULT_LOGGER_CLASS, formatStr(str2, objArr));
    }
}
