package com.android.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class ImagePreference extends Preference {
    public ImagePreference(Context context) {
        this(context, null);
    }

    public ImagePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ImagePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(R.layout.image_preference);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        view.setPadding(0, 0, 0, 0);
    }
}
