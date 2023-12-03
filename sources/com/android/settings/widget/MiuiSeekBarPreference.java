package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import miuix.animation.Folme;

/* loaded from: classes2.dex */
public class MiuiSeekBarPreference extends SeekBarPreference {
    private boolean mShowTitleIcon;

    public MiuiSeekBarPreference(Context context) {
        super(context);
        this.mShowTitleIcon = true;
        init();
    }

    public MiuiSeekBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShowTitleIcon = true;
        init();
    }

    private void init() {
        setLayoutResource(MiuiUtils.supportPaperEyeCare() ? R.layout.miui_seekbar_preference_layout : R.layout.old_miui_seekbar_preference_layout);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setBackgroundColor(0);
        ImageView imageView = (ImageView) view.findViewById(R.id.title_icon);
        if (imageView == null || this.mShowTitleIcon) {
            return;
        }
        imageView.setVisibility(8);
    }

    public void setShowTitleIcon(boolean z) {
        this.mShowTitleIcon = z;
        notifyChanged();
    }
}
