package com.google.android.play.core.splitcompat.util;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import java.util.Locale;

/* loaded from: classes2.dex */
public class PlayCore {
    private String mTag;

    public PlayCore(String str) {
        String str2 = "UID: [" + Process.myUid() + "]  PID: [" + Process.myPid() + "] ";
        String valueOf = String.valueOf(str);
        this.mTag = valueOf.length() != 0 ? str2.concat(valueOf) : str2;
    }

    private int log(int i, String str, Object[] objArr) {
        if (Log.isLoggable("PlayCore", i)) {
            return Log.i("PlayCore", logInternal(this.mTag, str, objArr));
        }
        return 0;
    }

    private static String logInternal(String str, String str2, Object... objArr) {
        String str3 = str + " : " + str2;
        if (objArr == null || objArr.length <= 0) {
            return str3;
        }
        try {
            return String.format(Locale.US, str3, objArr);
        } catch (Throwable th) {
            Log.e("PlayCore", str3.length() != 0 ? "Unable to format ".concat(str3) : "Unable to format ", th);
            return str3 + " [" + TextUtils.join(", ", objArr) + "]";
        }
    }

    public int debug(String str, Object... objArr) {
        return log(3, str, objArr);
    }

    public int error(String str, Object... objArr) {
        return log(6, str, objArr);
    }

    public int error(Throwable th, String str, Object... objArr) {
        if (Log.isLoggable("PlayCore", 6)) {
            return Log.e("PlayCore", logInternal(this.mTag, str, objArr), th);
        }
        return 0;
    }

    public int info(String str, Object... objArr) {
        return log(4, str, objArr);
    }

    public int warn(String str, Object... objArr) {
        return log(5, str, objArr);
    }
}
