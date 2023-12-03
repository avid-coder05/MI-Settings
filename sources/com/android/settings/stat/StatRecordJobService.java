package com.android.settings.stat;

import android.app.job.JobParameters;
import android.app.job.JobService;
import com.android.settings.stat.commonpreference.KeySettingsStatHelper;
import com.android.settings.stat.commonpreference.PreferenceStatHelper;
import com.android.settings.stat.commonswitch.SwitchStatHelper;
import com.android.settings.stat.darkmode.DarkmodeStatHelper;
import com.android.settings.stat.print.SettingsPrintStatHelper;
import com.android.settingslib.utils.ThreadUtils;

/* loaded from: classes2.dex */
public class StatRecordJobService extends JobService {
    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartJob$0() {
        PreferenceStatHelper.tracePreferenceEvent(getApplicationContext());
        SwitchStatHelper.traceSwitchEvent(getApplicationContext());
        SettingsPrintStatHelper.getInstance(getApplicationContext()).traceMiPrintEvent(true);
        DarkmodeStatHelper.traceDarkModeEvent(getApplicationContext());
        KeySettingsStatHelper.getInstance(getApplicationContext()).traceVisitPageEvent(new KeySettingsStatHelper.Info(KeySettingsStatHelper.GESTURE_PAGE_KEY, KeySettingsStatHelper.PAGE_INIT));
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.stat.StatRecordJobService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                StatRecordJobService.this.lambda$onStartJob$0();
            }
        });
        return false;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
