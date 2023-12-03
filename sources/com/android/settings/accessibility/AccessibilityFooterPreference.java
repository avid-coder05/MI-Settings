package com.android.settings.accessibility;

import android.content.Context;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.widget.FooterPreference;

/* loaded from: classes.dex */
public final class AccessibilityFooterPreference extends FooterPreference {
    private boolean mLinkEnabled;

    public AccessibilityFooterPreference(Context context) {
        super(context);
    }

    public AccessibilityFooterPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settingslib.widget.FooterPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(16908310);
        textView.setTextColor(Color.parseColor("#808080"));
        if (this.mLinkEnabled) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            textView.setMovementMethod(null);
        }
    }

    public void setLinkEnabled(boolean z) {
        if (this.mLinkEnabled != z) {
            this.mLinkEnabled = z;
            notifyChanged();
        }
    }
}
