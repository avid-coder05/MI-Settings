package com.android.settings.core.instrumentation;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.settings.R;
import com.android.settings.intelligence.LogProto$SettingsLog;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.LogWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class SettingsIntelligenceLogWriter implements LogWriter {
    private SendLogHandler mLogHandler;
    private final Runnable mSendLogsRunnable = new Runnable() { // from class: com.android.settings.core.instrumentation.SettingsIntelligenceLogWriter$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            SettingsIntelligenceLogWriter.this.lambda$new$0();
        }
    };
    private List<LogProto$SettingsLog> mSettingsLogList = new LinkedList();

    /* loaded from: classes.dex */
    private class SendLogHandler extends Handler {
        SendLogHandler(Looper looper) {
            super(looper);
        }
    }

    public SettingsIntelligenceLogWriter() {
        HandlerThread handlerThread = new HandlerThread("SettingsIntelligenceLogWriter", 10);
        handlerThread.start();
        this.mLogHandler = new SendLogHandler(handlerThread.getLooper());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        Context appContext = FeatureFactory.getAppContext();
        if (appContext == null) {
            Log.e("IntelligenceLogWriter", "context is null");
            return;
        }
        String string = appContext.getString(R.string.config_settingsintelligence_log_action);
        if (TextUtils.isEmpty(string) || this.mSettingsLogList.isEmpty()) {
            return;
        }
        Intent intent = new Intent();
        intent.setPackage(appContext.getString(R.string.config_settingsintelligence_package_name));
        intent.setAction(string);
        intent.putExtra("logs", serialize(this.mSettingsLogList));
        appContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        this.mSettingsLogList.clear();
    }

    static byte[] serialize(List<LogProto$SettingsLog> list) {
        int size = list.size();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            try {
                dataOutputStream.writeInt(size);
                Iterator<LogProto$SettingsLog> it = list.iterator();
                while (it.hasNext()) {
                    byte[] byteArray = it.next().toByteArray();
                    dataOutputStream.writeInt(byteArray.length);
                    dataOutputStream.write(byteArray);
                }
                byte[] byteArray2 = byteArrayOutputStream.toByteArray();
                try {
                    dataOutputStream.close();
                } catch (Exception e) {
                    Log.e("IntelligenceLogWriter", "close error", e);
                }
                return byteArray2;
            } catch (Exception e2) {
                Log.e("IntelligenceLogWriter", "serialize error", e2);
                try {
                    dataOutputStream.close();
                } catch (Exception e3) {
                    Log.e("IntelligenceLogWriter", "close error", e3);
                }
                return null;
            }
        } catch (Throwable th) {
            try {
                dataOutputStream.close();
            } catch (Exception e4) {
                Log.e("IntelligenceLogWriter", "close error", e4);
            }
            throw th;
        }
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void action(int i, int i2, int i3, String str, int i4) {
        ZonedDateTime.now(ZoneId.systemDefault());
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void action(Context context, int i, int i2) {
        action(0, i, 0, "", i2);
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void action(Context context, int i, String str) {
        action(0, i, 0, str, 1);
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void action(Context context, int i, boolean z) {
        action(0, i, 0, "", z ? 1 : 0);
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void action(Context context, int i, Pair<Integer, Object>... pairArr) {
        action(0, i, 0, "", 0);
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void hidden(Context context, int i, int i2) {
        action(0, 2, i, "", i2);
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void visible(Context context, int i, int i2, int i3) {
        action(i, 1, i2, "", i3);
    }
}
