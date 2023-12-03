package com.android.settings.display;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class SpannablePreference extends Preference {
    public SpannablePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        ((TextView) view.findViewById(16908304)).setMovementMethod(LinkMovementMethod.getInstance());
    }
}
