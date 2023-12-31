package com.android.settings.applications;

import android.content.Intent;
import android.miui.AppOpsUtils;
import com.android.settings.SettingsActivity;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;

/* loaded from: classes.dex */
public class InstalledAppDetailsTop extends SettingsActivity {
    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        if (AppOpsUtils.isXOptMode()) {
            intent.putExtra(":settings:show_fragment", AppInfoDashboardFragment.class.getName());
        } else {
            intent.putExtra(":settings:show_fragment", InstalledAppDetailsFragment.class.getName());
        }
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return AppOpsUtils.isXOptMode() ? AppInfoDashboardFragment.class.getName().equals(str) : InstalledAppDetailsFragment.class.getName().equals(str);
    }
}
