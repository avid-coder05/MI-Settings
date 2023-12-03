package miuix.animation.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes5.dex */
public class LogUtils {
    private static volatile boolean sIsLogEnabled;
    private static final Handler sLogHandler;
    private static final Map<Integer, String> sTag;
    private static final HandlerThread sThread;

    static {
        HandlerThread handlerThread = new HandlerThread("LogThread");
        sThread = handlerThread;
        sTag = new ConcurrentHashMap();
        handlerThread.start();
        sLogHandler = new Handler(handlerThread.getLooper()) { // from class: miuix.animation.utils.LogUtils.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 0) {
                    Log.d((String) LogUtils.sTag.get(Integer.valueOf(message.arg1)), "thread log, " + ((String) message.obj));
                }
                message.obj = null;
            }
        };
    }

    public static void debug(String str, Object... objArr) {
        if (sIsLogEnabled) {
            if (objArr.length <= 0) {
                Log.i("miuix_anim", str);
                return;
            }
            StringBuilder sb = new StringBuilder(", ");
            int length = sb.length();
            for (Object obj : objArr) {
                if (sb.length() > length) {
                    sb.append(", ");
                }
                sb.append(obj);
            }
            Log.i("miuix_anim", str + sb.toString());
        }
    }

    public static void getLogEnableInfo() {
        String str = "";
        try {
            String readProp = CommonUtils.readProp("log.tag.folme.level");
            if (readProp != null) {
                str = readProp;
            }
        } catch (Exception e) {
            Log.i("miuix_anim", "can not access property log.tag.folme.level, no log", e);
        }
        Log.d("miuix_anim", "logLevel = " + str);
        sIsLogEnabled = str.equals("D");
    }

    public static boolean isLogEnabled() {
        return sIsLogEnabled;
    }

    public static void logThread(String str, String str2) {
        Message obtainMessage = sLogHandler.obtainMessage(0);
        obtainMessage.obj = str2;
        int hashCode = str.hashCode();
        obtainMessage.arg1 = hashCode;
        sTag.put(Integer.valueOf(hashCode), str);
        obtainMessage.sendToTarget();
    }
}
