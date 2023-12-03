package com.android.settings.dangerousoptions;

import android.app.job.JobParameters;
import android.app.job.JobService;

/* loaded from: classes.dex */
public class DangerousOptionsJobService extends JobService {
    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        DangerousOptionsUtil.checkDangerousOptions(this, false);
        return false;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
