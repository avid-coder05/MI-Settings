package com.android.settings.keys;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class AutoDisableScreenButtonsCloudConfigService extends JobService {
    private String mCloudConfig;
    private JobParameters mParams;

    /* JADX INFO: Access modifiers changed from: private */
    public static String getCloudConfig(Context context) {
        List cloudDataList = MiuiSettings.SettingsCloudData.getCloudDataList(context.getContentResolver(), "AutoDisableNavigationButton1");
        if (cloudDataList == null || cloudDataList.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        try {
            Iterator it = cloudDataList.iterator();
            while (it.hasNext()) {
                String cloudData = ((MiuiSettings.SettingsCloudData.CloudData) it.next()).toString();
                if (!TextUtils.isEmpty(cloudData)) {
                    JSONObject jSONObject = new JSONObject(cloudData);
                    String optString = jSONObject.optString("pkg");
                    String optString2 = jSONObject.optString("flag");
                    sb.append("\"");
                    sb.append(optString);
                    sb.append("\"");
                    sb.append(": ");
                    sb.append(optString2);
                    sb.append(", ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int length = sb.length();
        sb.delete(length - 2, length);
        sb.append(" }");
        String sb2 = sb.toString();
        Log.i("AutoDisableCloudConfig", "getCloudConfig: " + sb2);
        return sb2;
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        if (jobParameters.getJobId() != 44010) {
            return false;
        }
        Log.v("AutoDisableCloudConfig", "service started");
        this.mParams = jobParameters;
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.keys.AutoDisableScreenButtonsCloudConfigService.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                AutoDisableScreenButtonsCloudConfigService autoDisableScreenButtonsCloudConfigService = AutoDisableScreenButtonsCloudConfigService.this;
                autoDisableScreenButtonsCloudConfigService.mCloudConfig = AutoDisableScreenButtonsCloudConfigService.getCloudConfig(autoDisableScreenButtonsCloudConfigService.getApplicationContext());
                Settings.System.putString(AutoDisableScreenButtonsCloudConfigService.this.getContentResolver(), "auto_disable_screen_button_cloud_setting", AutoDisableScreenButtonsCloudConfigService.this.mCloudConfig);
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r2) {
                AutoDisableScreenButtonsCloudConfigService autoDisableScreenButtonsCloudConfigService = AutoDisableScreenButtonsCloudConfigService.this;
                autoDisableScreenButtonsCloudConfigService.jobFinished(autoDisableScreenButtonsCloudConfigService.mParams, AutoDisableScreenButtonsCloudConfigService.this.mCloudConfig == null);
            }
        }.execute(new Void[0]);
        return true;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
