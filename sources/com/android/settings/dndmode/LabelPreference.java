package com.android.settings.dndmode;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class LabelPreference extends ValuePreference {
    private String mLabel;

    public LabelPreference(Context context) {
        super(context);
    }

    public LabelPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settingslib.miuisettings.preference.ValuePreference
    public void onBindView(View view) {
        super.onBindView(view);
    }

    public void setLabel(String str) {
        if (TextUtils.equals(this.mLabel, str)) {
            return;
        }
        this.mLabel = str;
        setValue(str);
    }
}
