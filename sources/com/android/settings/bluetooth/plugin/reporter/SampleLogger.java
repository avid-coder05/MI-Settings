package com.android.settings.bluetooth.plugin.reporter;

import android.util.Log;
import com.iqiyi.android.qigsaw.core.common.SplitLog;

/* loaded from: classes.dex */
public class SampleLogger implements SplitLog.Logger {
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
}
