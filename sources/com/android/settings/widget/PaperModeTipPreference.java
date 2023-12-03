package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes2.dex */
public class PaperModeTipPreference extends Preference {
    public PaperModeTipPreference(Context context) {
        super(context);
        init();
    }

    public PaperModeTipPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public PaperModeTipPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public PaperModeTipPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.paper_mode_tip_lyt);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (view != null) {
            view.setBackgroundColor(0);
        }
    }
}
