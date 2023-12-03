package com.android.settings.accessibility;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.R;
import miuix.preference.FolmeAnimationController;

/* loaded from: classes.dex */
public class LottieAnimationPreference extends Preference implements FolmeAnimationController {
    private String mAssetName;
    private int mBottomMargin;
    private int mLeftMargin;
    private LottieAnimationView mLottieAnimationView;
    private int mMaxHeight;
    private int mRepeatCount;
    private int mRightMargin;
    private int mTopMargin;
    private Uri mUri;

    public LottieAnimationPreference(Context context) {
        super(context);
        this.mMaxHeight = -1;
        this.mRepeatCount = -1;
        setLayoutResource(R.layout.preference_lottie_animation_view);
    }

    public LottieAnimationPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMaxHeight = -1;
        this.mRepeatCount = -1;
        setLayoutResource(R.layout.preference_lottie_animation_view);
    }

    public LottieAnimationPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMaxHeight = -1;
        this.mRepeatCount = -1;
        setLayoutResource(R.layout.preference_lottie_animation_view);
    }

    public void cancelAnimation() {
        LottieAnimationView lottieAnimationView = this.mLottieAnimationView;
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
            this.mLottieAnimationView.removeAllAnimatorListeners();
            this.mLottieAnimationView.clearAnimation();
            this.mLottieAnimationView = null;
        }
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        LottieAnimationView lottieAnimationView = (LottieAnimationView) preferenceViewHolder.itemView.findViewById(R.id.animated_img);
        this.mLottieAnimationView = lottieAnimationView;
        if (lottieAnimationView == null) {
            return;
        }
        int i = this.mMaxHeight;
        if (i > -1) {
            lottieAnimationView.setMaxHeight(i);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mLottieAnimationView.getLayoutParams();
        layoutParams.setMargins(this.mLeftMargin, this.mTopMargin, this.mRightMargin, this.mBottomMargin);
        this.mLottieAnimationView.setLayoutParams(layoutParams);
        if (!TextUtils.isEmpty(this.mAssetName)) {
            startAnimation();
            return;
        }
        Uri uri = this.mUri;
        if (uri != null) {
            this.mLottieAnimationView.setImageURI(uri);
        }
    }

    public void setAssetName(String str) {
        this.mAssetName = str;
    }

    public void setImageURI(Uri uri) {
        this.mUri = uri;
    }

    public void setMargin(int i, int i2, int i3, int i4) {
        this.mLeftMargin = i;
        this.mTopMargin = i2;
        this.mRightMargin = i3;
        this.mBottomMargin = i4;
    }

    public void setMaxHeight(int i) {
        if (i != this.mMaxHeight) {
            this.mMaxHeight = i;
            notifyChanged();
        }
    }

    public void startAnimation() {
        this.mLottieAnimationView.setAnimation(this.mAssetName);
        this.mLottieAnimationView.setRepeatCount(this.mRepeatCount);
        this.mLottieAnimationView.playAnimation();
    }
}
