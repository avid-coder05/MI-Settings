package com.android.settings;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.bootloader.HeartbeatJobService;
import com.android.settings.credentials.CredentialsUpdateService;
import com.android.settings.dangerousoptions.DangerousOptionsJobService;
import com.android.settings.display.PaperModeLocateService;
import com.android.settings.keys.AutoDisableScreenButtonsCloudConfigService;
import com.android.settings.stat.StatRecordJobService;
import com.android.settings.statistic.SettingsCollectorService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import miui.mipub.MipubStat;

/* loaded from: classes.dex */
public class JobDispatcher {
    private static List<Integer> QUEUE_JOBS_TO_SCHEDULE = new ArrayList();
    private static final HashMap<Integer, Integer> SERVICE_VERSION = new HashMap<Integer, Integer>() { // from class: com.android.settings.JobDispatcher.1
        {
            put(44004, 1);
            put(44005, 1);
            put(44009, 1);
            put(44011, 1);
            put(44010, 1);
            put(44013, 1);
        }
    };
    private static final Set<Integer> JOB_IDS = new HashSet<Integer>() { // from class: com.android.settings.JobDispatcher.2
        {
            add(44004);
            add(44005);
            add(44009);
            add(44011);
            add(44010);
            add(44012);
            add(44013);
        }
    };

    public static void addJobToSchedule(int i) {
        synchronized (QUEUE_JOBS_TO_SCHEDULE) {
            QUEUE_JOBS_TO_SCHEDULE.add(Integer.valueOf(i));
        }
    }

    private static void cancelOldJobs(Context context, JobScheduler jobScheduler, List<JobInfo> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        for (JobInfo jobInfo : list) {
            if (TextUtils.equals(jobInfo.getService().getPackageName(), context.getPackageName()) && !JOB_IDS.contains(Integer.valueOf(jobInfo.getId()))) {
                Log.d("JobDispatcher", "cancelOldJobs(): jobId=" + jobInfo.getId());
                jobScheduler.cancel(jobInfo.getId());
            }
        }
    }

    public static boolean commit(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
        cancelOldJobs(context, jobScheduler, allPendingJobs);
        synchronized (QUEUE_JOBS_TO_SCHEDULE) {
            for (Integer num : QUEUE_JOBS_TO_SCHEDULE) {
                if (!isJobExist(allPendingJobs, num.intValue())) {
                    JobInfo createJobInfo = createJobInfo(context, num.intValue());
                    if (createJobInfo == null) {
                        Log.d("JobDispatcher", "scheduleJob(): fail to create job info for jobId=" + num);
                    } else {
                        createJobInfo.getExtras().putInt("JOB_VERSION", getJobVersion(num.intValue()));
                        scheduleJob(context, jobScheduler, createJobInfo);
                    }
                }
            }
            QUEUE_JOBS_TO_SCHEDULE.clear();
        }
        return true;
    }

    public static JobInfo createJobInfo(Context context, int i) {
        switch (i) {
            case 44004:
                return new JobInfo.Builder(44004, new ComponentName(context, CredentialsUpdateService.class)).setPeriodic(2592000000L).setPersisted(true).build();
            case 44005:
                return new JobInfo.Builder(44005, new ComponentName(context, SettingsCollectorService.class)).setPeriodic(86400000L).setPersisted(true).build();
            case 44006:
            case 44007:
            case 44008:
            default:
                Log.d("JobDispatcher", "createJobInfo(): unknown jobId=" + i);
                return null;
            case 44009:
                return new JobInfo.Builder(44009, new ComponentName(context, PaperModeLocateService.class)).setRequiredNetworkType(1).setPeriodic(86400000L).build();
            case 44010:
                return new JobInfo.Builder(44010, new ComponentName(context, AutoDisableScreenButtonsCloudConfigService.class)).setRequiredNetworkType(2).setPeriodic(MipubStat.STAT_EXPIRY_DATA).build();
            case 44011:
                return new JobInfo.Builder(44011, new ComponentName(context, DangerousOptionsJobService.class)).setPeriodic(43200000L).build();
            case 44012:
                return new JobInfo.Builder(44012, new ComponentName(context, HeartbeatJobService.class)).setRequiredNetworkType(1).setPeriodic(86400000L).setPersisted(true).build();
            case 44013:
                return new JobInfo.Builder(44013, new ComponentName(context, StatRecordJobService.class)).setPeriodic(86400000L).setPersisted(true).build();
        }
    }

    private static int getJobVersion(int i) {
        Integer num = SERVICE_VERSION.get(Integer.valueOf(i));
        if (num != null) {
            return num.intValue();
        }
        return 0;
    }

    public static boolean isJobExist(JobScheduler jobScheduler, int i) {
        return isJobExist(jobScheduler.getAllPendingJobs(), i);
    }

    private static boolean isJobExist(List<JobInfo> list, int i) {
        if (list != null) {
            for (JobInfo jobInfo : list) {
                if (jobInfo != null) {
                    int i2 = jobInfo.getExtras().getInt("JOB_VERSION");
                    if (jobInfo.getId() == i && i2 == getJobVersion(i)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void scheduleJob(Context context, int i) {
        scheduleJob(context, i, true);
    }

    public static void scheduleJob(Context context, int i, boolean z) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        if (z && isJobExist(jobScheduler, i)) {
            return;
        }
        JobInfo createJobInfo = createJobInfo(context, i);
        if (createJobInfo != null) {
            createJobInfo.getExtras().putInt("JOB_VERSION", getJobVersion(i));
            scheduleJob(context, createJobInfo);
            return;
        }
        Log.d("JobDispatcher", "scheduleJob(): fail to create job info for jobId=" + i);
    }

    public static void scheduleJob(Context context, JobInfo jobInfo) {
        scheduleJob(context, (JobScheduler) context.getSystemService("jobscheduler"), jobInfo);
    }

    private static void scheduleJob(Context context, JobScheduler jobScheduler, JobInfo jobInfo) {
        Log.d("JobDispatcher", " scheduleJob jobInfo = " + jobInfo.getId() + ", version=" + getJobVersion(jobInfo.getId()) + ", result=" + jobScheduler.scheduleAsPackage(jobInfo, context.getPackageName(), UserHandle.myUserId(), null));
    }
}
