package com.android.settings.knock;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.R;
import miui.provider.Weather;

/* loaded from: classes.dex */
public class KnockGestureLottieView extends LinearLayout {
    private LottieAnimationView mKnockGestureVLottieView;

    public KnockGestureLottieView(Context context) {
        this(context, null);
    }

    public KnockGestureLottieView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KnockGestureLottieView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private void setLottieInfo(int i) {
        LottieAnimationView lottieAnimationView;
        if (i == 0 || (lottieAnimationView = this.mKnockGestureVLottieView) == null) {
            return;
        }
        lottieAnimationView.setAnimation(i);
        this.mKnockGestureVLottieView.setClipToOutline(true);
        this.mKnockGestureVLottieView.playAnimation();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mKnockGestureVLottieView = (LottieAnimationView) findViewById(R.id.knock_gesture_v);
        setLottieInfo(((LinearLayout) this).mContext.getResources().getIdentifier("knock_gesture_v", Weather.RawInfo.PARAM, ((LinearLayout) this).mContext.getPackageName()));
    }
}
