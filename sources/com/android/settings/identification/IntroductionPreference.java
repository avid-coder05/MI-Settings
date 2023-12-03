package com.android.settings.identification;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class IntroductionPreference extends Preference {
    public IntroductionPreference(Context context) {
        super(context);
    }

    public IntroductionPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public IntroductionPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        setLayoutResource(R.layout.introduction_preference);
        return super.onCreateView(viewGroup);
    }
}
