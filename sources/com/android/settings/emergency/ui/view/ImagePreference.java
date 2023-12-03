package com.android.settings.emergency.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class ImagePreference extends Preference {
    private ImageView mImage;

    public ImagePreference(Context context) {
        super(context);
    }

    public ImagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ImagePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.sos_icon);
        this.mImage = imageView;
        imageView.setImageResource(R.drawable.sos_direction_back);
        super.onBindView(view);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        setLayoutResource(R.layout.preference_image);
        return super.onCreateView(viewGroup);
    }
}
