package com.android.settings.privacypassword;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes2.dex */
public class PrivacyPasswordPreferenceTitle extends Preference {
    private TextView mPreferenceTitle;

    public PrivacyPasswordPreferenceTitle(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PrivacyPasswordPreferenceTitle(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private int getPreferenceTitleId() {
        return R.string.privacy_password_settings_summary;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        TextView textView = (TextView) view.findViewById(R.id.preference_title);
        this.mPreferenceTitle = textView;
        textView.setText(getPreferenceTitleId());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        setLayoutResource(R.layout.privacy_password_preference_title);
        return null;
    }
}
