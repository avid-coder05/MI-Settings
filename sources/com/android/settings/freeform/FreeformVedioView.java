package com.android.settings.freeform;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.R;
import com.android.settings.utils.SettingsFeatures;
import miui.os.Build;

/* loaded from: classes.dex */
public class FreeformVedioView extends LinearLayout {
    private static float PAD_RADIUS = 36.0f;
    private static float PAD_RADIUS_PIN = 14.89f;
    private static float RADIUS = 52.0f;
    private LottieAnimationView mFreeformGuideLottieView;
    private TextView mTitleView;

    public FreeformVedioView(Context context) {
        this(context, null);
    }

    public FreeformVedioView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FreeformVedioView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public static int getScreenType(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getDisplayNoVerify().getDisplayRealMetricsSize(displayMetrics);
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        int min = Math.min(i2, i);
        int max = Math.max(i2, i);
        float f = min;
        float f2 = max;
        double sqrt = Math.sqrt(Math.pow(f / displayMetrics.xdpi, 2.0d) + Math.pow(f2 / displayMetrics.ydpi, 2.0d));
        if (f / f2 <= 0.33333334f) {
            return 0;
        }
        if (sqrt >= 10.0d || sqrt < 8.0d) {
            return sqrt >= 10.0d ? 3 : 1;
        }
        return 2;
    }

    public static boolean isLargeScreen(Context context) {
        return getScreenType(context) == 2;
    }

    private void setLottieInfo(int i) {
        LottieAnimationView lottieAnimationView;
        if (i == 0 || (lottieAnimationView = this.mFreeformGuideLottieView) == null) {
            return;
        }
        lottieAnimationView.setAnimation(i);
        this.mFreeformGuideLottieView.playAnimation();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mFreeformGuideLottieView = (LottieAnimationView) findViewById(R.id.video_view_freeform);
        this.mTitleView = (TextView) findViewById(R.id.freeform_vedio_text_description);
        int dimension = (int) getResources().getDimension(R.dimen.freeform_text_margin_left);
        int dimension2 = (int) getResources().getDimension(R.dimen.freeform_text_margin_right);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mTitleView.getLayoutParams();
        if (SettingsFeatures.isFoldDevice()) {
            marginLayoutParams.setMarginStart(dimension);
            marginLayoutParams.setMarginEnd(dimension2);
            if (SettingsFeatures.isSupportPin()) {
                this.mTitleView.setText(R.string.freeform_vedio_description_open_freeform_and_splitscreen);
                if (isLargeScreen(getContext())) {
                    setLottieInfo(R.raw.freeform_guide_pin_fold_large);
                } else {
                    setLottieInfo(R.raw.freeform_guide_pin_fold_small);
                }
            } else {
                this.mTitleView.setText(R.string.freeform_vedio_description);
            }
            this.mFreeformGuideLottieView.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.settings.freeform.FreeformVedioView.1
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), FreeformVedioView.RADIUS);
                }
            });
        } else if (Build.IS_TABLET) {
            marginLayoutParams.setMarginStart(dimension);
            marginLayoutParams.setMarginEnd(dimension2);
            this.mTitleView.setGravity(17);
            if (FreeformGuideSettings.getCvw()) {
                setLottieInfo(R.raw.freeform_guide_cvw);
            }
            if (SettingsFeatures.isSupportPin()) {
                setLottieInfo(R.raw.freeform_guide_pin_pad);
                this.mTitleView.setText(R.string.freeform_vedio_description_open_freeform_and_splitscreen_pad);
                this.mFreeformGuideLottieView.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.settings.freeform.FreeformVedioView.2
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), FreeformVedioView.PAD_RADIUS_PIN);
                    }
                });
            } else {
                this.mTitleView.setText(R.string.freeform_vedio_description_pad);
                this.mFreeformGuideLottieView.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.settings.freeform.FreeformVedioView.3
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), FreeformVedioView.PAD_RADIUS);
                    }
                });
            }
        } else {
            this.mTitleView.setText(R.string.freeform_vedio_description);
            marginLayoutParams.setMarginStart(dimension);
            marginLayoutParams.setMarginEnd(dimension2);
            setLottieInfo(R.raw.freeform_guide);
            this.mFreeformGuideLottieView.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.settings.freeform.FreeformVedioView.4
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), FreeformVedioView.RADIUS);
                }
            });
        }
        this.mFreeformGuideLottieView.setClipToOutline(true);
    }
}
