package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.iqiyi.android.qigsaw.core.common.SplitBaseInfoProvider;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitreport.SplitUpdateReporter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class SplitUpdateService extends IntentService {
    private static final String TAG = "SplitUpdateService";

    public SplitUpdateService() {
        super("qigsaw_split_update");
    }

    private void onUpdateError(String str, String str2, int i) {
        SplitUpdateReporter updateReporter = SplitUpdateReporterManager.getUpdateReporter();
        if (updateReporter != null) {
            updateReporter.onUpdateFailed(str, str2, i);
        }
    }

    private void onUpdateOK(String str, String str2, List<String> list) {
        SplitUpdateReporter updateReporter = SplitUpdateReporterManager.getUpdateReporter();
        if (updateReporter != null) {
            updateReporter.onUpdateOK(str, str2, list);
        }
    }

    @Override // android.app.IntentService, android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        String str = "com.android.settings:Unknown";
        try {
            if (intent == null) {
                SplitLog.w(TAG, "SplitUpdateService receiver null intent!", new Object[0]);
                return;
            }
            SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
            if (splitInfoManagerService == null) {
                SplitLog.w(TAG, "SplitInfoManager has not been created!", new Object[0]);
            } else if (splitInfoManagerService.getAllSplitInfo(this) == null) {
                SplitLog.w(TAG, "Failed to get splits info of current split-info version!", new Object[0]);
            } else {
                String stringExtra = intent.getStringExtra(SplitConstants.NEW_SPLIT_INFO_VERSION);
                String stringExtra2 = intent.getStringExtra(SplitConstants.NEW_SPLIT_INFO_PATH);
                String currentSplitInfoVersion = splitInfoManagerService.getCurrentSplitInfoVersion();
                if (TextUtils.isEmpty(stringExtra)) {
                    SplitLog.w(TAG, "New split-info version null", new Object[0]);
                    onUpdateError(currentSplitInfoVersion, stringExtra, -31);
                } else if (TextUtils.isEmpty(stringExtra2)) {
                    SplitLog.w(TAG, "New split-info path null", new Object[0]);
                    onUpdateError(currentSplitInfoVersion, stringExtra, -32);
                } else {
                    File file = new File(stringExtra2);
                    if (file.exists() && file.canWrite()) {
                        if (stringExtra.equals(splitInfoManagerService.getCurrentSplitInfoVersion())) {
                            SplitLog.w(TAG, "New split-info version %s is equals to current version!", stringExtra);
                            onUpdateError(currentSplitInfoVersion, stringExtra, -34);
                            Log.i(TAG, "onHandleIntent end, send test broadcast sent_version = com.android.settings:Same version");
                            Intent intent2 = new Intent("com.xiaomi.bluetooth.action.BLUETOOTH_PLUGIN_UPDATED");
                            intent2.setPackage("com.xiaomi.bluetooth");
                            intent2.putExtra("UPDATED_INFO", "com.android.settings:Same version");
                            sendBroadcast(intent2);
                            return;
                        }
                        SplitDetails createSplitDetailsForJsonFile = splitInfoManagerService.createSplitDetailsForJsonFile(stringExtra2);
                        if (createSplitDetailsForJsonFile == null) {
                            SplitLog.w(TAG, "Failed to parse SplitDetails for new split info file!", new Object[0]);
                            onUpdateError(currentSplitInfoVersion, stringExtra, -35);
                            return;
                        }
                        String qigsawId = createSplitDetailsForJsonFile.getQigsawId();
                        if (!TextUtils.isEmpty(qigsawId) && qigsawId.equals(SplitBaseInfoProvider.getQigsawId())) {
                            ArrayList arrayList = (ArrayList) createSplitDetailsForJsonFile.getUpdateSplits();
                            if (arrayList != null && !arrayList.isEmpty()) {
                                SplitLog.w(TAG, "Success to check update request, updatedSplitInfoPath: %s, updatedSplitInfoVersion: %s", stringExtra2, stringExtra);
                                if (splitInfoManagerService.updateSplitInfoVersion(getApplicationContext(), stringExtra, file)) {
                                    onUpdateOK(currentSplitInfoVersion, stringExtra, arrayList);
                                    str = "com.android.settings:" + stringExtra;
                                } else {
                                    onUpdateError(currentSplitInfoVersion, stringExtra, -38);
                                }
                                return;
                            }
                            SplitLog.w(TAG, "There are no splits need to be updated!", new Object[0]);
                            onUpdateError(currentSplitInfoVersion, stringExtra, -36);
                            return;
                        }
                        SplitLog.w(TAG, "New qigsaw-id is not equal to current app, so we could't update splits!", new Object[0]);
                        onUpdateError(currentSplitInfoVersion, stringExtra, -37);
                        return;
                    }
                    SplitLog.w(TAG, "New split-info file %s is invalid", stringExtra2);
                    onUpdateError(currentSplitInfoVersion, stringExtra, -33);
                }
            }
        } finally {
            Log.i(TAG, "onHandleIntent end, send test broadcast sent_version = com.android.settings:Unknown");
            Intent intent3 = new Intent("com.xiaomi.bluetooth.action.BLUETOOTH_PLUGIN_UPDATED");
            intent3.setPackage("com.xiaomi.bluetooth");
            intent3.putExtra("UPDATED_INFO", "com.android.settings:Unknown");
            sendBroadcast(intent3);
        }
    }
}
