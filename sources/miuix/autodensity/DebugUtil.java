package miuix.autodensity;

import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

/* loaded from: classes5.dex */
public class DebugUtil {
    private static String sAutoDensityDebug;
    private static volatile float sDebugAutoDensityScale;

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                Log.w("AutoDensity", "close " + closeable + " failed", e);
            }
        }
    }

    public static float getAutoDensityScaleInDebugMode() {
        return sDebugAutoDensityScale;
    }

    public static void initAutoDensityDebugEnable() {
        String str;
        try {
            str = readProp("log.tag.autodensity.debug.enable");
            sAutoDensityDebug = str;
            if (str == null) {
                str = "0";
            }
        } catch (Exception e) {
            Log.i("AutoDensity", "can not access property log.tag.autodensity.enable, undebugable", e);
            str = "";
        }
        Log.d("AutoDensity", "autodensity debugEnable = " + str);
        try {
            sDebugAutoDensityScale = Float.parseFloat(str);
        } catch (NumberFormatException unused) {
            sDebugAutoDensityScale = 0.0f;
        }
    }

    public static void printDensityLog(String str) {
        if (sDebugAutoDensityScale < 0.0f || TextUtils.isEmpty(sAutoDensityDebug)) {
            return;
        }
        Log.d("AutoDensity", str);
    }

    private static String readProp(String str) {
        InputStreamReader inputStreamReader;
        Throwable th;
        BufferedReader bufferedReader;
        IOException e;
        try {
            inputStreamReader = new InputStreamReader(Runtime.getRuntime().exec("getprop " + str).getInputStream());
            try {
                bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    try {
                        String readLine = bufferedReader.readLine();
                        closeQuietly(bufferedReader);
                        closeQuietly(inputStreamReader);
                        return readLine;
                    } catch (IOException e2) {
                        e = e2;
                        Log.i("AutoDensity", "readProp failed", e);
                        closeQuietly(bufferedReader);
                        closeQuietly(inputStreamReader);
                        return "";
                    }
                } catch (Throwable th2) {
                    th = th2;
                    closeQuietly(bufferedReader);
                    closeQuietly(inputStreamReader);
                    throw th;
                }
            } catch (IOException e3) {
                e = e3;
                bufferedReader = null;
            } catch (Throwable th3) {
                th = th3;
                bufferedReader = null;
                closeQuietly(bufferedReader);
                closeQuietly(inputStreamReader);
                throw th;
            }
        } catch (IOException e4) {
            inputStreamReader = null;
            e = e4;
            bufferedReader = null;
        } catch (Throwable th4) {
            inputStreamReader = null;
            th = th4;
            bufferedReader = null;
        }
    }
}
