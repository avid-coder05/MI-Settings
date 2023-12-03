package com.android.settings.wifi;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;

/* loaded from: classes2.dex */
public class MiuiNearbyApPreference extends Preference implements View.OnClickListener {
    private FrameLayout mAnimationBg;
    private Drawable mDrawable;
    private ImageView mImageView;
    private View.OnClickListener mOnClickListener;

    public MiuiNearbyApPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.preference_nearby_wifi);
        setTitle(context.getString(R.string.nearby_wifi));
    }

    private void setAlphaFolme(View view) {
        Folme.useAt(view).touch().setAlpha(0.6f, ITouchStyle.TouchType.DOWN).handleTouchOf(view, new AnimConfig[0]);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mImageView = (ImageView) view.findViewById(R.id.refresh_anim);
        this.mAnimationBg = (FrameLayout) view.findViewById(R.id.refresh_anim_bg);
        this.mDrawable = this.mImageView.getDrawable();
        this.mImageView.setOnClickListener(this);
        ImageView imageView = this.mImageView;
        imageView.setContentDescription(imageView.getResources().getString(R.string.menu_stats_refresh));
        this.mAnimationBg.setOnClickListener(this);
        setAlphaFolme(this.mAnimationBg);
        view.setBackgroundColor(0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        this.mOnClickListener.onClick(view);
    }

    public void setOnSettingsClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void startScanAnimation() {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            ((AnimatedVectorDrawable) drawable).start();
        }
    }

    public void stopScanAnimation() {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            ((AnimatedVectorDrawable) drawable).stop();
        }
    }
}
