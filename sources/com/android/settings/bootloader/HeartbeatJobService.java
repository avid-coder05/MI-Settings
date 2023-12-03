package com.android.settings.bootloader;

import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

/* loaded from: classes.dex */
public class HeartbeatJobService extends JobService {
    /* JADX INFO: Access modifiers changed from: private */
    public boolean canSendHeartbeat(Context context) {
        if ("locked".equals(SystemProperties.get("ro.secureboot.lockstate", (String) null))) {
            String accountName = Utils.getAccountName(context);
            return !TextUtils.isEmpty(accountName) && accountName.equals(getSharedPreferences(getDefaultSharedPreferencesName(context), 4).getString("bootloader_account", ""));
        }
        return false;
    }

    public static void cancelHeartbeatJob(Context context) {
        ((JobScheduler) context.getSystemService("jobscheduler")).cancel(44012);
    }

    private String getDefaultSharedPreferencesName(Context context) {
        return context.getPackageName() + "_preferences";
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters jobParameters) {
        new Thread() { // from class: com.android.settings.bootloader.HeartbeatJobService.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                HeartbeatJobService heartbeatJobService = HeartbeatJobService.this;
                boolean canSendHeartbeat = heartbeatJobService.canSendHeartbeat(heartbeatJobService);
                boolean z = !canSendHeartbeat;
                if (canSendHeartbeat && CloudDeviceStatus.sendHeartbeat(HeartbeatJobService.this) >= 30) {
                    z = true;
                }
                if (z) {
                    Log.d("bootloader_heartbeat", "cancel job");
                    HeartbeatJobService.cancelHeartbeatJob(HeartbeatJobService.this);
                }
                HeartbeatJobService.this.jobFinished(jobParameters, false);
            }
        }.start();
        return true;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
