package com.android.settings;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;

/* loaded from: classes.dex */
public class AodStylePreference extends KeyguardRestrictedPreference {
    private ImageView mAodStyleImage;
    private Uri mUri;

    public AodStylePreference(Context context) {
        super(context);
        init();
    }

    public AodStylePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public AodStylePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public AodStylePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private void init() {
        setWidgetLayoutResource(R.layout.aod_style_preference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onBindViewHolder$0(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.arrow_right);
        if (imageView != null) {
            imageView.setVisibility(8);
        }
    }

    @Override // com.android.settings.KeyguardRestrictedPreference, com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ViewGroup.LayoutParams layoutParams = preferenceViewHolder.findViewById(16908312).getLayoutParams();
        layoutParams.height = -2;
        layoutParams.width = -2;
        final View view = preferenceViewHolder.itemView;
        view.post(new Runnable() { // from class: com.android.settings.AodStylePreference$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AodStylePreference.lambda$onBindViewHolder$0(view);
            }
        });
        ImageView imageView = (ImageView) view.findViewById(R.id.aod_style_icon);
        this.mAodStyleImage = imageView;
        if (imageView != null) {
            imageView.setImageURI(this.mUri);
        }
    }

    public void setAodStyleImage(Uri uri) {
        this.mUri = uri;
        ImageView imageView = this.mAodStyleImage;
        if (imageView != null) {
            imageView.setImageURI(null);
            this.mAodStyleImage.setImageURI(uri);
        }
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference
    protected boolean shouldHideSecondTarget() {
        return false;
    }
}
