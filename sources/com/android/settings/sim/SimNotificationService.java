package com.android.settings.sim;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Log;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class SimNotificationService extends JobService {
    public static void scheduleSimNotification(Context context, int i) {
        JobScheduler jobScheduler = (JobScheduler) context.getApplicationContext().getSystemService(JobScheduler.class);
        ComponentName componentName = new ComponentName(context.getApplicationContext(), SimNotificationService.class);
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putInt("notification_type", i);
        jobScheduler.schedule(new JobInfo.Builder(R.integer.sim_notification_send, componentName).setExtras(persistableBundle).build());
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        PersistableBundle extras = jobParameters.getExtras();
        if (extras == null) {
            Log.e("SimNotificationService", "Failed to get notification type.");
            return false;
        }
        int i = extras.getInt("notification_type");
        if (i == 1) {
            Log.i("SimNotificationService", "Sending SIM config notification.");
            SimActivationNotifier.setShowSimSettingsNotification(this, false);
            new SimActivationNotifier(this).sendNetworkConfigNotification();
        } else if (i == 2) {
            new SimActivationNotifier(this).sendSwitchedToRemovableSlotNotification();
        } else if (i != 3) {
            Log.e("SimNotificationService", "Invalid notification type: " + i);
        } else {
            new SimActivationNotifier(this).sendEnableDsdsNotification();
        }
        return false;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
