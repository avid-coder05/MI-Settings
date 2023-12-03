package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.display.RestrictedSizeAdjustView;
import miuix.animation.Folme;

/* loaded from: classes2.dex */
public class MagicSeekBarPreference extends SeekBarPreference {
    public MagicSeekBarPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.magic_seekbar_preference_layout);
    }

    public MagicSeekBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.magic_seekbar_preference_layout);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setBackgroundColor(0);
        view.setPadding(0, 0, 0, 0);
        RestrictedSizeAdjustView restrictedSizeAdjustView = (RestrictedSizeAdjustView) view.findViewById(R.id.seekbar);
        int paddingStart = ((TextView) view.findViewById(16908310)).getPaddingStart() - restrictedSizeAdjustView.getPaddingStart();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(restrictedSizeAdjustView.getLayoutParams());
        layoutParams.setMarginsRelative(paddingStart, 0, paddingStart, 0);
        restrictedSizeAdjustView.setLayoutParams(layoutParams);
    }
}
