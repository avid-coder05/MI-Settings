package com.android.settings;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import com.airbnb.lottie.LottieAnimationView;

/* loaded from: classes.dex */
public class BackTapGuideView extends FrameLayout {
    private static float GUIDE_VIEW_RADIUS = 52.0f;
    private LottieAnimationView mBackTapGuideView;

    public BackTapGuideView(Context context) {
        this(context, null);
    }

    public BackTapGuideView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BackTapGuideView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        LottieAnimationView lottieAnimationView = (LottieAnimationView) findViewById(R.id.backtap_guide_lottie_view);
        this.mBackTapGuideView = lottieAnimationView;
        lottieAnimationView.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.settings.BackTapGuideView.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), BackTapGuideView.GUIDE_VIEW_RADIUS);
            }
        });
        this.mBackTapGuideView.setClipToOutline(true);
    }
}
