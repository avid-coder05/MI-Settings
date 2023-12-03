package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.miui.AppOpsUtils;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.applications.DefaultAppsHelper;
import com.android.settings.applications.PreferredSettings;
import com.android.settings.applications.defaultapps.MiuiDefaultAppSettings;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class DefaultTypePreferencceController extends AbstractPreferenceController implements MiuiDefaultAppSettings.ResetToDefault {
    private final int defaultAppType;
    private String mDefaultPackageName;
    private Intent mIntent;
    private PackageManager mPackageManager;
    private final String preferenceKey;
    private final String preferenceName;

    public DefaultTypePreferencceController(Context context, String str, int i, String str2) {
        super(context);
        this.preferenceKey = str;
        this.defaultAppType = i;
        this.preferenceName = str2;
        this.mPackageManager = this.mContext.getPackageManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.preferenceKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(getPreferenceKey())) {
            this.mContext.startActivity(this.mIntent);
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settings.applications.defaultapps.MiuiDefaultAppSettings.ResetToDefault
    public void resetDefaults() {
        IntentFilter intentFilter = DefaultAppsHelper.getIntentFilter(this.defaultAppType);
        if (TextUtils.isEmpty(this.mDefaultPackageName)) {
            return;
        }
        if ("android.intent.action.SENDTO".equals(intentFilter.getAction(0)) && intentFilter.hasDataScheme("smsto")) {
            return;
        }
        this.mPackageManager.clearPackagePreferredActivities(this.mDefaultPackageName);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        IntentFilter intentFilter = DefaultAppsHelper.getIntentFilter(this.defaultAppType);
        if (AppOpsUtils.isXOptMode() && this.defaultAppType == 5) {
            intentFilter = new IntentFilter("android.intent.action.MAIN");
            intentFilter.addCategory("android.intent.category.APP_GALLERY");
        }
        Intent intent = new Intent(this.mContext, PreferredSettings.class);
        this.mIntent = intent;
        intent.putExtra("preferred_app_intent_filter", intentFilter);
        Intent intent2 = DefaultAppsHelper.getIntent(intentFilter);
        this.mIntent.putExtra("preferred_app_intent", intent2);
        this.mIntent.putExtra("preferred_label", this.preferenceName);
        if (preference instanceof ValuePreference) {
            ((ValuePreference) preference).setShowRightArrow(true);
            preference.setTitle(this.preferenceName);
            preference.setSummary(R.string.preferred_app_settings_default);
            preference.setIntent(this.mIntent);
        }
        ResolveInfo resolveActivity = this.mPackageManager.resolveActivity(intent2, 0);
        if (resolveActivity == null) {
            return;
        }
        String str = resolveActivity.activityInfo.packageName;
        this.mDefaultPackageName = str;
        preference.setSummary(DefaultAppsHelper.getApplicationLabel(this.mContext, str, this.mPackageManager));
        this.mIntent.putExtra("preferred_app_package_name", this.mDefaultPackageName);
        preference.setIntent(this.mIntent);
    }
}
