package com.android.settings.controlcenter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.view.CornerVideoView;
import com.android.settings.view.VisualCheckBoxPreference;
import miuix.os.Build;

/* loaded from: classes.dex */
public class ControlCenterStylePreference extends VisualCheckBoxPreference {
    private static final int RES_LEGACY_BG = R.drawable.bg_control_center_legacy;
    private static final int RES_MODERN_BG = R.drawable.bg_control_center_modern;
    private static final int RES_LEGACY_BG_INTER = R.drawable.bg_control_center_legacy_international;
    private static final int RES_MODERN_BG_INTER = R.drawable.bg_control_center_modern_international;
    private static final int RES_MODERN = R.raw.control_center_guide_modern;
    private static final int RES_MODERN_INTER = R.raw.control_center_guide_modern_international;

    public ControlCenterStylePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settings.view.VisualCheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setImportantForAccessibility(2);
    }

    @Override // com.android.settings.view.VisualCheckBoxPreference
    protected void onCreateVisualContent(View view, View view2) {
        if (view != null) {
            CornerVideoView cornerVideoView = (CornerVideoView) view.findViewById(R.id.video_view);
            cornerVideoView.setAudioFocusRequest(0);
            boolean z = Build.IS_INTERNATIONAL_BUILD;
            cornerVideoView.play(z ? RES_MODERN_INTER : RES_MODERN, z ? RES_MODERN_BG_INTER : RES_LEGACY_BG);
        }
        if (view2 != null) {
            ((AppCompatImageView) view2.findViewById(R.id.image_view)).setImageDrawable(getContext().getDrawable(Build.IS_INTERNATIONAL_BUILD ? RES_LEGACY_BG_INTER : RES_LEGACY_BG));
        }
    }
}
