package com.android.settings.keys;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.MiuiUtils;
import com.android.settingslib.miuisettings.preference.Preference;
import miuix.animation.Folme;

/* loaded from: classes.dex */
public class RestrictedEdgeDescriptionPreference extends Preference {
    public RestrictedEdgeDescriptionPreference(Context context) {
        super(context);
    }

    public RestrictedEdgeDescriptionPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RestrictedEdgeDescriptionPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public RestrictedEdgeDescriptionPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setBackgroundResource(0);
    }
}
