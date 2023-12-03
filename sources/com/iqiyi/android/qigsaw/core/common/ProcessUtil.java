package com.iqiyi.android.qigsaw.core.common;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/* loaded from: classes2.dex */
public class ProcessUtil {
    private static final String TAG = "Split:ProcessUtil";

    public static String getProcessName(Context context) {
        String str;
        try {
            str = getProcessNameClassical(context);
        } catch (Exception unused) {
            str = null;
        }
        if (TextUtils.isEmpty(str)) {
            String processNameSecure = getProcessNameSecure();
            SplitLog.i(TAG, "Get process name: %s in secure mode.", processNameSecure);
            return processNameSecure;
        }
        return str;
    }

    private static String getProcessNameClassical(Context context) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
        int myPid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        String str = "";
        if (activityManager == null || (runningAppProcesses = activityManager.getRunningAppProcesses()) == null) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.pid == myPid) {
                str = runningAppProcessInfo.processName;
            }
        }
        return str;
    }

    private static String getProcessNameSecure() {
        String str = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("/proc/" + Process.myPid() + "/cmdline")));
            str = bufferedReader.readLine().trim();
            bufferedReader.close();
            return str;
        } catch (Exception unused) {
            return str;
        }
    }

    public static void killAllOtherProcess(Context context) {
        Log.w(TAG, "no!!!!", new Exception());
    }

    public static void killEspecialProcess(Context context, String[] strArr) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
        if (strArr == null || strArr.length < 1) {
            Log.i(TAG, "killEspecialProcess illegal processNames!");
            return;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        if (activityManager == null || (runningAppProcesses = activityManager.getRunningAppProcesses()) == null) {
            return;
        }
        boolean z = false;
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.uid == Process.myUid()) {
                int length = strArr.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        String str = strArr[i];
                        if (!str.equals(runningAppProcessInfo.processName)) {
                            i++;
                        } else if (runningAppProcessInfo.pid == Process.myPid()) {
                            z = true;
                        } else {
                            Log.i(TAG, "killEspecialProcess kill " + str + " pid: " + runningAppProcessInfo.pid);
                            Process.killProcess(runningAppProcessInfo.pid);
                        }
                    }
                }
            }
        }
        if (z) {
            Log.i(TAG, "killEspecialProcess kill self:" + Process.myPid());
            Process.killProcess(Process.myPid());
        }
    }
}
