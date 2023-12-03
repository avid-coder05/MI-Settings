package com.android.settings.search;

import android.app.AppGlobals;
import android.app.Application;
import android.content.ContentProviderOperation;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class SearchUpdater {
    public static final int ABOUT_DEVICE = 1;
    public static final int ALL = -1;
    public static final int BACKUP = 128;
    public static final int DISPLAY = 4;
    public static final int GOOGLE = 65536;
    public static final int KEY = 32;
    public static final int LOCK_SCREEN = 16;
    public static final int OTHER = Integer.MIN_VALUE;
    public static final int PHONE = 64;
    public static final int SIM = 1073741824;
    public static final int SOUND = 8;
    private static final String TAG = "SearchUpdater";
    public static final int WIRELESS = 2;
    private static volatile SearchUpdater sInstance;
    private volatile UpdateHandler mHandler;
    private final TaskManager mTaskManager = new TaskManager();
    private ArrayList<ContentProviderOperation> mOps = new ArrayList<>();

    /* loaded from: classes2.dex */
    private final class TaskManager {
        private int mFlag;

        private TaskManager() {
            this.mFlag = 0;
        }

        public void add(int i) {
            synchronized (this) {
                this.mFlag = i | this.mFlag;
            }
        }

        public int get() {
            synchronized (this) {
                for (int i = 1; i != 0; i <<= 1) {
                    if ((this.mFlag & i) != 0) {
                        return i;
                    }
                }
                return 0;
            }
        }

        public boolean isEmpty() {
            boolean z;
            synchronized (this) {
                z = this.mFlag == 0;
            }
            return z;
        }

        public void remove(int i) {
            synchronized (this) {
                int i2 = this.mFlag;
                if ((i & i2) == i) {
                    this.mFlag = i ^ i2;
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    private final class UpdateHandler extends Handler {
        UpdateHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            try {
                if (!SearchUpdater.this.mOps.isEmpty()) {
                    SearchUpdater.this.mOps.clear();
                }
            } catch (Exception e) {
                Log.e(SearchUpdater.TAG, "error occures when applyBatch", e);
            }
            if (SearchUpdater.this.mTaskManager.isEmpty()) {
                return;
            }
            int i = SearchUpdater.this.mTaskManager.get();
            if (i != 0) {
                SearchUpdater.this.mTaskManager.remove(i);
                SearchUpdater.this.update(i);
            }
            sendEmptyMessage(message.what);
        }
    }

    private SearchUpdater() {
    }

    public static SearchUpdater getInstance() {
        if (sInstance == null) {
            synchronized (SearchUpdater.class) {
                if (sInstance == null) {
                    sInstance = new SearchUpdater();
                }
            }
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update(int i) {
        Application initialApplication = AppGlobals.getInitialApplication();
        try {
            if ((i & 1) != 0) {
                AboutDeviceUpdateHelper.update(initialApplication, this.mOps);
            } else if ((i & 2) != 0) {
                WirelessUpdateHelper.update(initialApplication, this.mOps);
            } else if ((i & 4) != 0) {
                DisplayUpdateHelper.update(initialApplication, this.mOps);
            } else if ((i & 8) != 0) {
                SoundUpdateHelper.update(initialApplication, this.mOps);
            } else if ((i & 16) != 0) {
                SecurityUpdateHelper.update(initialApplication, this.mOps);
            } else if ((i & 32) != 0) {
                KeySettingsUpdateHelper.update(initialApplication, this.mOps);
            } else if ((i & 64) != 0) {
                PhoneSettingsUpdateHelper.update(initialApplication, this.mOps);
            } else if ((i & 128) != 0) {
                BackupSettingsUpdateHelper.update(initialApplication, this.mOps);
            } else if ((65536 & i) != 0) {
                GoogleSettingsUpdateHelper.update(initialApplication, this.mOps);
            } else if ((1073741824 & i) != 0) {
                SimSettingsUpdateHelper.update(initialApplication, this.mOps);
            } else if ((Integer.MIN_VALUE & i) != 0) {
                OtherSettingsUpdateHelper.update(initialApplication, this.mOps);
            }
        } catch (Exception unused) {
            Log.e(TAG, "error occurs when updating, current: " + i);
        }
    }

    public void handleUpdate(int i) {
        if (this.mHandler == null) {
            HandlerThread handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            this.mHandler = new UpdateHandler(handlerThread.getLooper());
        }
        this.mTaskManager.add(i);
        this.mHandler.obtainMessage(0).sendToTarget();
    }
}
