package com.android.settings.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceViewHolder;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.R;
import miui.provider.Weather;
import miuix.preference.FolmeAnimationController;

/* loaded from: classes2.dex */
public class MediaCheckboxPreference extends CheckBoxPreference implements FolmeAnimationController {
    private final Context mContext;
    private View mDelimiterView;
    private int mImageHeight;
    private RelativeLayout mImageRelativeLayout;
    private int mImageWidth;
    private RelativeLayout mLottieRelativeLayout;
    private LottieAnimationView mLottieView;
    private int mResId;
    private boolean mShowDelimiter;
    private int mType;

    public MediaCheckboxPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public MediaCheckboxPreference(Context context, int i) {
        this(context);
        this.mType = i;
    }

    public MediaCheckboxPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MediaCheckboxPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public MediaCheckboxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mContext = context;
        if (2 == this.mType) {
            this.mImageWidth = context.getResources().getDimensionPixelSize(R.dimen.gesture_img_checkbox_width);
            this.mImageHeight = context.getResources().getDimensionPixelSize(R.dimen.gesture_img_checkbox_height);
        }
        setLayoutResource(R.layout.media_checkbox_preference);
    }

    private View findMediaView(int i, RelativeLayout relativeLayout) {
        if (relativeLayout.getChildCount() != 0) {
            for (int i2 = 0; i2 < relativeLayout.getChildCount(); i2++) {
                if (relativeLayout.getChildAt(i2).getId() == i) {
                    return relativeLayout.getChildAt(i2);
                }
            }
            return null;
        }
        return null;
    }

    private int getResId(String str) {
        return this.mContext.getResources().getIdentifier(str, this.mType == 2 ? "drawable" : Weather.RawInfo.PARAM, this.mContext.getPackageName());
    }

    private void setImageInfo(PreferenceViewHolder preferenceViewHolder) {
        int i;
        ViewStub viewStub = (ViewStub) preferenceViewHolder.findViewById(R.id.image_viewstub_checkbox);
        if (this.mImageRelativeLayout == null) {
            RelativeLayout relativeLayout = (RelativeLayout) viewStub.inflate();
            this.mImageRelativeLayout = relativeLayout;
            ImageView imageView = (ImageView) findMediaView(R.id.gesture_img, relativeLayout);
            if (this.mImageHeight != 0 && this.mImageWidth != 0) {
                ViewGroup.LayoutParams layoutParams = viewStub.getLayoutParams();
                layoutParams.height = this.mImageHeight;
                layoutParams.width = this.mImageWidth;
                viewStub.setLayoutParams(layoutParams);
            }
            if (imageView == null || (i = this.mResId) == 0) {
                return;
            }
            imageView.setImageResource(i);
        }
    }

    private void setLottieInfo(PreferenceViewHolder preferenceViewHolder) {
        ViewStub viewStub = (ViewStub) preferenceViewHolder.findViewById(R.id.lottie_viewstub_checkbox);
        if (this.mLottieRelativeLayout != null) {
            LottieAnimationView lottieAnimationView = this.mLottieView;
            if (lottieAnimationView != null) {
                lottieAnimationView.playAnimation();
                return;
            }
            return;
        }
        RelativeLayout relativeLayout = (RelativeLayout) viewStub.inflate();
        this.mLottieRelativeLayout = relativeLayout;
        LottieAnimationView lottieAnimationView2 = (LottieAnimationView) findMediaView(R.id.lottie_video, relativeLayout);
        this.mLottieView = lottieAnimationView2;
        if (lottieAnimationView2 == null || this.mResId == 0) {
            return;
        }
        if (getResId("key_combination_left_power_volume_down") == this.mResId) {
            String str = Build.DEVICE;
            if (str.equals("ingres") || str.equals("ares")) {
                this.mLottieView.setLayoutParams(new RelativeLayout.LayoutParams((int) (this.mContext.getResources().getDimension(R.dimen.video_checkbox_width) * 0.79d), (int) (this.mContext.getResources().getDimension(R.dimen.video_checkbox_height) * 0.79d)));
            }
        }
        this.mLottieView.setAnimation(this.mResId);
        this.mLottieView.setClipToOutline(true);
    }

    public int getType() {
        return this.mType;
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mType == 2) {
            setImageInfo(preferenceViewHolder);
        } else {
            setLottieInfo(preferenceViewHolder);
        }
        if (this.mDelimiterView == null) {
            this.mDelimiterView = preferenceViewHolder.findViewById(R.id.delimiter);
        }
        this.mDelimiterView.setVisibility(this.mShowDelimiter ? 0 : 8);
    }

    public void setResName(String str) {
        this.mResId = getResId(str);
        notifyChanged();
    }

    public void setShowDelimiter(boolean z) {
        this.mShowDelimiter = z;
    }
}
