package com.android.settings.wifi;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes2.dex */
public class ButtonPreference extends Preference {
    private Drawable mButtonIcon;
    private View.OnClickListener mClickListener;
    private ImageButton mImageButton;

    public ButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setWidgetLayoutResource(R.layout.wifi_button_preference_widget);
        this.mImageButton = null;
        this.mButtonIcon = null;
        this.mClickListener = null;
    }

    private void setButtonVisibility() {
        ImageButton imageButton = this.mImageButton;
        if (imageButton != null) {
            imageButton.setVisibility(this.mButtonIcon == null ? 8 : 0);
        }
    }

    protected void initButton(PreferenceViewHolder preferenceViewHolder) {
        if (this.mImageButton == null) {
            this.mImageButton = (ImageButton) preferenceViewHolder.findViewById(R.id.button_icon);
        }
        ImageButton imageButton = this.mImageButton;
        if (imageButton != null) {
            imageButton.setImageDrawable(this.mButtonIcon);
            this.mImageButton.setOnClickListener(this.mClickListener);
        }
        setButtonVisibility();
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        initButton(preferenceViewHolder);
    }

    @Override // androidx.preference.Preference
    public void setOrder(int i) {
        super.setOrder(i);
        setButtonVisibility();
    }
}
