package miui.cloud.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.FileObserver;
import android.os.SystemClock;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import miui.cloud.common.XLogger;
import miui.cloud.os.MultiuserUtils;

/* loaded from: classes3.dex */
public class SwitchFileLogSender implements XLogger.LogSender {
    private static final String LOG_SUFFIX = ".log";
    private static final String PACKAGE_LOG_PATH = "MIUI/debug_log/%s";
    private final Context mAppContext;
    private long mFailOpenTime;
    private boolean mInit;
    private int mMaxFileCount;
    private int mMaxFileSizeInByte;
    private FileObserver mMonitor;
    private FileOutputStream mOutputStream;
    private XLogger.LogSender mParentLogSender;
    private PathProvider mPathProvider;
    private boolean mShutdown;
    private long mSizeUsed;

    /* loaded from: classes3.dex */
    public interface PathProvider {
        File getPath(Context context);
    }

    public SwitchFileLogSender(Context context, final String str, int i, int i2, XLogger.LogSender logSender) {
        this(context, new PathProvider() { // from class: miui.cloud.common.SwitchFileLogSender.1
            @Override // miui.cloud.common.SwitchFileLogSender.PathProvider
            public File getPath(Context context2) {
                return new File(Environment.getExternalStorageDirectory(), String.format(SwitchFileLogSender.PACKAGE_LOG_PATH, str));
            }
        }, i, i2, logSender);
    }

    public SwitchFileLogSender(Context context, PathProvider pathProvider, int i, int i2, XLogger.LogSender logSender) {
        if (context.getApplicationContext() != context) {
            throw new IllegalArgumentException("appContext is not the application context. ");
        }
        if (pathProvider == null) {
            throw new IllegalArgumentException("pathProvider should not be null.");
        }
        if (i <= 0) {
            throw new IllegalArgumentException("maxFileSizeInByte should >0. ");
        }
        if (i2 <= 1) {
            throw new IllegalArgumentException("maxFileCount should >1. ");
        }
        this.mParentLogSender = logSender;
        this.mMaxFileSizeInByte = i;
        this.mMaxFileCount = i2;
        this.mPathProvider = pathProvider;
        this.mAppContext = context;
        this.mInit = false;
    }

    private void closeLogFileLocked() {
        this.mMonitor.stopWatching();
        this.mMonitor = null;
        IOUtil.closeQuietly(this.mOutputStream);
        this.mOutputStream = null;
        this.mSizeUsed = 0L;
        this.mFailOpenTime = 0L;
    }

    private void init() {
        if (this.mInit) {
            return;
        }
        this.mInit = true;
        registerShutdownListener(this.mAppContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void onBaseDirChanged() {
        FileDescriptor fileDescriptor;
        int i;
        FileOutputStream fileOutputStream = this.mOutputStream;
        if (fileOutputStream == null) {
            return;
        }
        StructStat structStat = null;
        try {
            fileDescriptor = fileOutputStream.getFD();
        } catch (IOException unused) {
            fileDescriptor = null;
        }
        if (fileDescriptor == null) {
            return;
        }
        boolean z = false;
        try {
            structStat = Os.fstat(fileDescriptor);
            i = 0;
        } catch (ErrnoException e) {
            i = e.errno;
        }
        boolean z2 = true;
        if (structStat != null && structStat.st_nlink <= 0) {
            z = true;
        }
        if (i != OsConstants.ENOENT) {
            z2 = z;
        }
        if (z2) {
            XLogger.LogSender logSender = this.mParentLogSender;
            if (logSender != null) {
                logSender.sendLog(5, getClass().getName(), "File unlinked. ");
            }
            closeLogFileLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void onShutDown() {
        sendLogLocked(6, getClass().getName(), "========================== shut down ========================== ");
        this.mShutdown = true;
        if (this.mOutputStream != null) {
            closeLogFileLocked();
        }
    }

    private void prepareLogFileLocked() {
        File file;
        boolean z;
        if (this.mOutputStream != null) {
            return;
        }
        if (this.mFailOpenTime > 0 && Math.abs(SystemClock.elapsedRealtime() - this.mFailOpenTime) < 180000) {
            XLogger.LogSender logSender = this.mParentLogSender;
            if (logSender != null) {
                logSender.sendLog(6, getClass().getName(), "Failed to open log file recently. Abort. ");
                return;
            }
            return;
        }
        try {
            File path = this.mPathProvider.getPath(this.mAppContext);
            if (path == null) {
                XLogger.LogSender logSender2 = this.mParentLogSender;
                if (logSender2 != null) {
                    logSender2.sendLog(6, getClass().getName(), "Failed to get base log path. Abort. ");
                }
                if (this.mOutputStream == null) {
                    this.mFailOpenTime = SystemClock.elapsedRealtime();
                    return;
                } else {
                    this.mFailOpenTime = 0L;
                    return;
                }
            }
            File file2 = new File(path, "" + MultiuserUtils.myUserId());
            if (!file2.isDirectory()) {
                file2.mkdirs();
            }
            if (!file2.isDirectory()) {
                XLogger.LogSender logSender3 = this.mParentLogSender;
                if (logSender3 != null) {
                    logSender3.sendLog(6, getClass().getName(), String.format("Failed to create folder %s. ", file2.getAbsolutePath()));
                }
                if (this.mOutputStream == null) {
                    this.mFailOpenTime = SystemClock.elapsedRealtime();
                    return;
                } else {
                    this.mFailOpenTime = 0L;
                    return;
                }
            }
            String externalStorageState = Environment.getExternalStorageState(file2);
            if (!"unknown".equals(externalStorageState) && !"mounted".equals(externalStorageState)) {
                XLogger.LogSender logSender4 = this.mParentLogSender;
                if (logSender4 != null) {
                    logSender4.sendLog(6, getClass().getName(), "Storage not mounted. ");
                }
                if (this.mOutputStream == null) {
                    this.mFailOpenTime = SystemClock.elapsedRealtime();
                    return;
                } else {
                    this.mFailOpenTime = 0L;
                    return;
                }
            }
            int i = 0;
            while (true) {
                file = null;
                if (i >= this.mMaxFileCount) {
                    break;
                }
                file = new File(file2, i + LOG_SUFFIX);
                if (file.exists() && file.length() >= this.mMaxFileSizeInByte) {
                    i++;
                }
            }
            if (file == null) {
                long lastModified = new File(file2, "0.log").lastModified();
                long j = 0;
                for (int i2 = 1; i2 < this.mMaxFileCount; i2++) {
                    long lastModified2 = new File(file2, i2 + LOG_SUFFIX).lastModified();
                    if (lastModified2 < lastModified) {
                        j = i2;
                        lastModified = lastModified2;
                    }
                }
                file = new File(file2, j + LOG_SUFFIX);
                z = false;
            } else {
                z = true;
            }
            try {
                this.mOutputStream = new FileOutputStream(file, z);
                this.mSizeUsed = z ? file.length() : 0L;
                FileObserver fileObserver = new FileObserver(file2.getPath(), 1536) { // from class: miui.cloud.common.SwitchFileLogSender.2
                    @Override // android.os.FileObserver
                    public void onEvent(int i3, String str) {
                        SwitchFileLogSender.this.onBaseDirChanged();
                    }
                };
                this.mMonitor = fileObserver;
                fileObserver.startWatching();
                if (this.mOutputStream == null) {
                    this.mFailOpenTime = SystemClock.elapsedRealtime();
                } else {
                    this.mFailOpenTime = 0L;
                }
            } catch (FileNotFoundException e) {
                XLogger.LogSender logSender5 = this.mParentLogSender;
                if (logSender5 != null) {
                    logSender5.sendLog(6, getClass().getName(), String.format("Failed to switch to file %s, error: %s. ", file.getAbsolutePath(), e));
                }
                if (this.mOutputStream == null) {
                    this.mFailOpenTime = SystemClock.elapsedRealtime();
                } else {
                    this.mFailOpenTime = 0L;
                }
            }
        } catch (Throwable th) {
            if (this.mOutputStream == null) {
                this.mFailOpenTime = SystemClock.elapsedRealtime();
            } else {
                this.mFailOpenTime = 0L;
            }
            throw th;
        }
    }

    private void registerShutdownListener(Context context) {
        context.registerReceiver(new BroadcastReceiver() { // from class: miui.cloud.common.SwitchFileLogSender.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                SwitchFileLogSender.this.onShutDown();
            }
        }, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"), null, null);
    }

    private void sendLogLocked(int i, String str, String str2) {
        init();
        XLogger.LogSender logSender = this.mParentLogSender;
        if (logSender != null) {
            logSender.sendLog(i, str, str2);
        }
        if (onFilterLogByLevelLocked(i)) {
            if (this.mShutdown) {
                XLogger.LogSender logSender2 = this.mParentLogSender;
                if (logSender2 != null) {
                    logSender2.sendLog(6, getClass().getName(), "Shutdown state. Skip outputing. ");
                    return;
                }
                return;
            }
            prepareLogFileLocked();
            if (this.mOutputStream == null) {
                XLogger.LogSender logSender3 = this.mParentLogSender;
                if (logSender3 != null) {
                    logSender3.sendLog(6, getClass().getName(), "Null output stream. Skip outputing. ");
                    return;
                }
                return;
            }
            byte[] bytes = String.format("LV:%s, TM: %s, TAG: %s, MSG: %s\n", onGetLevelTagLocked(i), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), str, str2).getBytes();
            this.mSizeUsed += bytes.length;
            try {
                this.mOutputStream.write(bytes);
                this.mOutputStream.flush();
            } catch (IOException e) {
                XLogger.LogSender logSender4 = this.mParentLogSender;
                if (logSender4 != null) {
                    logSender4.sendLog(6, getClass().getName(), String.format("Failed to output log, IOException: %s", e));
                }
            }
            if (this.mSizeUsed >= this.mMaxFileSizeInByte) {
                closeLogFileLocked();
            }
        }
    }

    protected boolean onFilterLogByLevelLocked(int i) {
        return true;
    }

    protected String onGetLevelTagLocked(int i) {
        switch (i) {
            case 2:
                return "V";
            case 3:
                return "D";
            case 4:
                return "I";
            case 5:
                return "W";
            case 6:
                return "E";
            case 7:
                return "A";
            default:
                return String.valueOf(i);
        }
    }

    @Override // miui.cloud.common.XLogger.LogSender
    public synchronized void sendLog(int i, String str, String str2) {
        sendLogLocked(i, str, str2);
    }
}
