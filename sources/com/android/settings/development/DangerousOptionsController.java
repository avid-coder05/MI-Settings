package com.android.settings.development;

import android.app.job.JobScheduler;
import android.content.Context;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.JobDispatcher;
import com.android.settings.dangerousoptions.DangerousOptionsUtil;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class DangerousOptionsController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener {
    public DangerousOptionsController(Context context) {
        super(context);
    }

    private void writeDangerousOption(Object obj) {
        if (obj instanceof Boolean) {
            Boolean bool = (Boolean) obj;
            MiuiSettings.System.putBoolean(this.mContext.getContentResolver(), "dangerous_option_hint", bool.booleanValue());
            if (bool.booleanValue()) {
                DangerousOptionsUtil.checkDangerousOptions(this.mContext, true);
                JobDispatcher.scheduleJob(this.mContext, 44011);
                return;
            }
            ((JobScheduler) this.mContext.getSystemService("jobscheduler")).cancel(44011);
            DangerousOptionsUtil.stopDangerousOptionsHint(this.mContext);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "dangerous_option_hint";
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (TextUtils.equals(preference.getKey(), "dangerous_option_hint")) {
            writeDangerousOption(obj);
            return true;
        }
        return true;
    }

    public void setChecked(boolean z) {
        ((CheckBoxPreference) this.mPreference).setChecked(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        setChecked(DangerousOptionsUtil.isDangerousOptionsHintEnabled(this.mContext));
    }
}
