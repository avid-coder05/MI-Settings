package com.android.settings.speaker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes2.dex */
public class SpeakerDescriptionPreference extends Preference {
    public SpeakerDescriptionPreference(Context context) {
        this(context, null);
    }

    public SpeakerDescriptionPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SpeakerDescriptionPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(R.layout.speaker_layout);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        view.setPadding(0, 0, 0, 0);
        view.setBackgroundColor(0);
    }
}
