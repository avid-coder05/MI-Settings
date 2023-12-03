package com.android.settings;

import android.content.Context;
import android.graphics.Outline;
import android.provider.MiuiSettings;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import com.airbnb.lottie.LottieAnimationView;

/* loaded from: classes.dex */
public class HandyModeGuideView extends FrameLayout {
    private LottieAnimationView mHandymodeGuideView;

    public HandyModeGuideView(Context context) {
        this(context, null);
    }

    public HandyModeGuideView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HandyModeGuideView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        LottieAnimationView lottieAnimationView = (LottieAnimationView) findViewById(R.id.handymode_guide_lottie_view);
        this.mHandymodeGuideView = lottieAnimationView;
        lottieAnimationView.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.settings.HandyModeGuideView.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), HandyModeGuideView.this.getContext().getResources().getDisplayMetrics().density * 17.4f);
            }
        });
        this.mHandymodeGuideView.setClipToOutline(true);
        if (MiuiSettings.Global.getBoolean(((FrameLayout) this).mContext.getContentResolver(), "force_fsg_nav_bar")) {
            this.mHandymodeGuideView.setAnimation(R.raw.handymode_fullscreen_guide);
        } else {
            this.mHandymodeGuideView.setAnimation(R.raw.handymode_navigationbar_guide);
        }
        this.mHandymodeGuideView.loop(true);
        this.mHandymodeGuideView.playAnimation();
    }
}
