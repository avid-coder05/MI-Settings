package com.android.settings.emergency.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class SosCustomPreference extends Preference {
    private ImageView imageView;

    public SosCustomPreference(Context context) {
        super(context);
    }

    public SosCustomPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SosCustomPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (view != null) {
            view.setBackgroundColor(0);
            this.imageView = (ImageView) view.findViewById(R.id.sos_play_icon);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        setLayoutResource(R.layout.sos_player_layout);
        return super.onCreateView(viewGroup);
    }

    public void setPlayIcon(boolean z) {
        ImageView imageView = this.imageView;
        if (imageView == null) {
            return;
        }
        if (z) {
            imageView.setBackgroundResource(R.drawable.sos_player_play);
        } else {
            imageView.setBackgroundResource(R.drawable.sos_player_pause);
        }
    }
}
