package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes2.dex */
public class ScreenEnhanceEngineNotePreference extends Preference {
    private String noteInfoString;
    private TextView noteInfoTextView;

    public ScreenEnhanceEngineNotePreference(Context context) {
        super(context);
        this.noteInfoTextView = null;
        this.noteInfoString = null;
    }

    public ScreenEnhanceEngineNotePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.noteInfoTextView = null;
        this.noteInfoString = null;
    }

    public ScreenEnhanceEngineNotePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.noteInfoTextView = null;
        this.noteInfoString = null;
    }

    public ScreenEnhanceEngineNotePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.noteInfoTextView = null;
        this.noteInfoString = null;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        TextView textView = (TextView) view;
        this.noteInfoTextView = textView;
        String str = this.noteInfoString;
        if (str != null) {
            textView.setText(str);
        }
    }

    public void setNoteInfo(String str) {
        this.noteInfoString = str;
        TextView textView = this.noteInfoTextView;
        if (textView != null) {
            textView.setText(str);
        }
    }
}
