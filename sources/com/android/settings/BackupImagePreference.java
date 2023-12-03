package com.android.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.settingslib.miuisettings.preference.Preference;
import miuix.animation.Folme;

/* loaded from: classes.dex */
public class BackupImagePreference extends Preference {
    public BackupImagePreference(Context context) {
        this(context, null);
    }

    public BackupImagePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BackupImagePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setBackgroundColor(0);
        view.setEnabled(false);
    }
}
