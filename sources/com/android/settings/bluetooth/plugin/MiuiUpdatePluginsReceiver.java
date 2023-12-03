package com.android.settings.bluetooth.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.iqiyi.android.qigsaw.core.Qigsaw;
import com.iqiyi.android.qigsaw.core.common.ProcessUtil;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.milink.api.v1.type.DeviceType;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MiuiUpdatePluginsReceiver extends BroadcastReceiver {
    private static final String TAG;
    private static Handler sAsyncHandler;

    static {
        String simpleName = MiuiUpdatePluginsReceiver.class.getSimpleName();
        TAG = simpleName;
        HandlerThread handlerThread = new HandlerThread(simpleName);
        handlerThread.start();
        sAsyncHandler = new Handler(handlerThread.getLooper());
    }

    private boolean checkResourceDir(String str) {
        File file = new File(str);
        if (file.exists() && file.isDirectory()) {
            return true;
        }
        if (file.exists()) {
            return false;
        }
        try {
            return file.mkdirs();
        } catch (Exception e) {
            Log.e(TAG, "error " + e);
            return false;
        }
    }

    private void cleanFolder(File file, boolean z) {
        File[] listFiles;
        if (file == null || !file.exists()) {
            return;
        }
        try {
            if (file.isDirectory() && (listFiles = file.listFiles()) != null) {
                for (File file2 : listFiles) {
                    cleanFolder(file2, true);
                }
            }
            if (z) {
                file.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, "delete folder failed " + e);
        }
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception unused) {
            }
        }
    }

    private boolean copyFromRemote(Context context, String str, String str2) {
        FileOutputStream fileOutputStream;
        Closeable closeable = null;
        try {
            File file = new File(str2);
            if (file.exists()) {
                file.delete();
            }
            InputStream openInputStream = context.getContentResolver().openInputStream(Uri.parse("content://com.android.bluetooth.ble.app.headset.provider" + File.separator + str));
            try {
                fileOutputStream = new FileOutputStream(file);
                try {
                    byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
                    while (true) {
                        int read = openInputStream.read(bArr);
                        if (read == -1) {
                            fileOutputStream.flush();
                            closeQuietly(openInputStream);
                            closeQuietly(fileOutputStream);
                            return true;
                        }
                        fileOutputStream.write(bArr, 0, read);
                    }
                } catch (Exception e) {
                    closeable = openInputStream;
                    e = e;
                    try {
                        Log.e(TAG, "copyFromRemote catch: ", e);
                        closeQuietly(closeable);
                        closeQuietly(fileOutputStream);
                        return false;
                    } catch (Throwable th) {
                        th = th;
                        closeQuietly(closeable);
                        closeQuietly(fileOutputStream);
                        throw th;
                    }
                } catch (Throwable th2) {
                    closeable = openInputStream;
                    th = th2;
                    closeQuietly(closeable);
                    closeQuietly(fileOutputStream);
                    throw th;
                }
            } catch (Exception e2) {
                closeable = openInputStream;
                e = e2;
                fileOutputStream = null;
            } catch (Throwable th3) {
                closeable = openInputStream;
                th = th3;
                fileOutputStream = null;
            }
        } catch (Exception e3) {
            e = e3;
            fileOutputStream = null;
        } catch (Throwable th4) {
            th = th4;
            fileOutputStream = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUpdatePluginsBroadcast(Context context, Intent intent) {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append(context.getFilesDir().getAbsolutePath());
        String str2 = File.separator;
        sb.append(str2);
        sb.append(DeviceType.BLUETOOTH);
        String str3 = sb.toString() + str2 + "plugins";
        File file = new File(str3);
        cleanFolder(file, false);
        Iterator<String> it = intent.getStringArrayListExtra("SETTINGS_URLS").iterator();
        String str4 = "1.0_1.0";
        boolean z = false;
        String str5 = null;
        while (it.hasNext()) {
            String next = it.next();
            String str6 = File.separator;
            int lastIndexOf = next.lastIndexOf(str6);
            if (lastIndexOf < 0) {
                Log.e(TAG, "receiver get an illegal url:" + next);
            } else {
                if (checkResourceDir(str3)) {
                    String str7 = str3 + str6 + next.substring(lastIndexOf + 1);
                    str = str7;
                    z = copyFromRemote(context, next, str7);
                } else {
                    Log.e(TAG, "check resource dir failed, des_dir: " + str3);
                    str = null;
                }
                if (z && next.endsWith(SplitConstants.DOT_JSON)) {
                    String substring = next.substring(lastIndexOf + 1);
                    if (substring.startsWith(SplitConstants.QIGSAW_PREFIX)) {
                        String substring2 = substring.substring(7, substring.indexOf(SplitConstants.DOT_JSON));
                        if (str4.compareTo(substring2) < 0) {
                            str4 = substring2;
                            str5 = str;
                        }
                    } else {
                        Log.w(TAG, "Unexpected json url:" + next);
                    }
                }
            }
        }
        if (str5 != null && z) {
            Qigsaw.updateSplits(context, str4, str5);
            return;
        }
        Log.e(TAG, "check resource failed, do not update and clean folder!");
        cleanFolder(file, false);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        String action = intent.getAction();
        String str = TAG;
        Log.i(str, "onReceive action = " + action);
        if ("com.android.settings.action.UPDATE_NEW_BLUETOOTH_PLUGINS".equals(action)) {
            Log.d(str, "receive message, UPDATE_NEW_BLUETOOTH_PLUGINS");
            final Intent intent2 = new Intent(intent);
            sAsyncHandler.post(new Runnable() { // from class: com.android.settings.bluetooth.plugin.MiuiUpdatePluginsReceiver.1
                @Override // java.lang.Runnable
                public void run() {
                    MiuiUpdatePluginsReceiver.this.handleUpdatePluginsBroadcast(context, intent2);
                }
            });
        } else if ("com.android.settings.action.STOP_TO_UPDATE_BLUETOOTH_PLUGINS".equals(action)) {
            Log.i(str, "recv msg:ACTION_STOP_TO_UPDATE_BLUETOOTH_PLUGINS: ");
            ProcessUtil.killEspecialProcess(context, new String[]{"com.android.settings", "com.android.settings:remote"});
            System.exit(0);
        }
    }
}
