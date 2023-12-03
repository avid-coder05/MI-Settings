package com.android.settings.applications;

import android.content.Intent;
import android.preference.PreferenceActivity;
import com.android.settings.R;

/* loaded from: classes.dex */
public class RunningServiceDetailsActivity extends PreferenceActivity {
    @Override // android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":android:show_fragment", RunningServiceDetails.class.getName());
        intent.putExtra(":android:show_fragment_title", R.string.runningservicedetails_settings_title);
        intent.putExtra(":android:show_fragment_args", intent.getExtras());
        intent.putExtra(":android:no_headers", true);
        return intent;
    }

    @Override // android.preference.PreferenceActivity
    protected boolean isValidFragment(String str) {
        return RunningApplicationsFragment.class.getName().equals(str);
    }
}
