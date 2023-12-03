package com.android.settingslib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieListener;
import java.io.FileNotFoundException;
import java.io.InputStream;
import miuix.animation.Folme;
import miuix.preference.FolmeAnimationController;

/* loaded from: classes2.dex */
public class IllustrationPreference extends Preference implements FolmeAnimationController {
    private final Animatable2.AnimationCallback mAnimationCallback;
    private final Animatable2Compat.AnimationCallback mAnimationCallbackCompat;
    private Drawable mImageDrawable;
    private int mImageResId;
    private Uri mImageUri;
    private boolean mIsAutoScale;
    private View mMiddleGroundView;

    public IllustrationPreference(Context context) {
        super(context);
        this.mAnimationCallback = new Animatable2.AnimationCallback() { // from class: com.android.settingslib.widget.IllustrationPreference.1
            @Override // android.graphics.drawable.Animatable2.AnimationCallback
            public void onAnimationEnd(Drawable drawable) {
                ((Animatable) drawable).start();
            }
        };
        this.mAnimationCallbackCompat = new Animatable2Compat.AnimationCallback() { // from class: com.android.settingslib.widget.IllustrationPreference.2
            @Override // androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
            public void onAnimationEnd(Drawable drawable) {
                ((Animatable) drawable).start();
            }
        };
        init(context, null);
    }

    public IllustrationPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAnimationCallback = new Animatable2.AnimationCallback() { // from class: com.android.settingslib.widget.IllustrationPreference.1
            @Override // android.graphics.drawable.Animatable2.AnimationCallback
            public void onAnimationEnd(Drawable drawable) {
                ((Animatable) drawable).start();
            }
        };
        this.mAnimationCallbackCompat = new Animatable2Compat.AnimationCallback() { // from class: com.android.settingslib.widget.IllustrationPreference.2
            @Override // androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
            public void onAnimationEnd(Drawable drawable) {
                ((Animatable) drawable).start();
            }
        };
        init(context, attributeSet);
    }

    public IllustrationPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAnimationCallback = new Animatable2.AnimationCallback() { // from class: com.android.settingslib.widget.IllustrationPreference.1
            @Override // android.graphics.drawable.Animatable2.AnimationCallback
            public void onAnimationEnd(Drawable drawable) {
                ((Animatable) drawable).start();
            }
        };
        this.mAnimationCallbackCompat = new Animatable2Compat.AnimationCallback() { // from class: com.android.settingslib.widget.IllustrationPreference.2
            @Override // androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
            public void onAnimationEnd(Drawable drawable) {
                ((Animatable) drawable).start();
            }
        };
        init(context, attributeSet);
    }

    public IllustrationPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mAnimationCallback = new Animatable2.AnimationCallback() { // from class: com.android.settingslib.widget.IllustrationPreference.1
            @Override // android.graphics.drawable.Animatable2.AnimationCallback
            public void onAnimationEnd(Drawable drawable) {
                ((Animatable) drawable).start();
            }
        };
        this.mAnimationCallbackCompat = new Animatable2Compat.AnimationCallback() { // from class: com.android.settingslib.widget.IllustrationPreference.2
            @Override // androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
            public void onAnimationEnd(Drawable drawable) {
                ((Animatable) drawable).start();
            }
        };
        init(context, attributeSet);
    }

    private static InputStream getInputStreamFromUri(Context context, Uri uri) {
        try {
            return context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.w("IllustrationPreference", "Cannot find content uri: " + uri, e);
            return null;
        }
    }

    private void handleImageWithAnimation(LottieAnimationView lottieAnimationView) {
        if (this.mImageDrawable != null) {
            resetAnimations(lottieAnimationView);
            lottieAnimationView.setImageDrawable(this.mImageDrawable);
            Drawable drawable = lottieAnimationView.getDrawable();
            if (drawable != null) {
                startAnimation(drawable);
            }
        }
        if (this.mImageUri != null) {
            resetAnimations(lottieAnimationView);
            lottieAnimationView.setImageURI(this.mImageUri);
            Drawable drawable2 = lottieAnimationView.getDrawable();
            if (drawable2 != null) {
                startAnimation(drawable2);
            } else {
                startLottieAnimationWith(lottieAnimationView, this.mImageUri);
            }
        }
        if (this.mImageResId > 0) {
            resetAnimations(lottieAnimationView);
            lottieAnimationView.setImageResource(this.mImageResId);
            Drawable drawable3 = lottieAnimationView.getDrawable();
            if (drawable3 != null) {
                startAnimation(drawable3);
            } else {
                startLottieAnimationWith(lottieAnimationView, this.mImageResId);
            }
        }
    }

    private void handleMiddleGroundView(ViewGroup viewGroup) {
        viewGroup.removeAllViews();
        View view = this.mMiddleGroundView;
        if (view == null) {
            viewGroup.setVisibility(8);
            return;
        }
        viewGroup.addView(view);
        viewGroup.setVisibility(0);
    }

    private void init(Context context, AttributeSet attributeSet) {
        setLayoutResource(R$layout.illustration_preference);
        this.mIsAutoScale = false;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.LottieAnimationView, 0, 0);
            this.mImageResId = obtainStyledAttributes.getResourceId(R$styleable.LottieAnimationView_lottie_rawRes, 0);
            obtainStyledAttributes.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$startLottieAnimationWith$0(Uri uri, Throwable th) {
        Log.w("IllustrationPreference", "Invalid illustration image uri: " + uri, th);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$startLottieAnimationWith$1(int i, Throwable th) {
        Log.w("IllustrationPreference", "Invalid illustration resource id: " + i, th);
    }

    private static void resetAnimation(Drawable drawable) {
        if (drawable instanceof Animatable) {
            if (drawable instanceof Animatable2) {
                ((Animatable2) drawable).clearAnimationCallbacks();
            } else if (drawable instanceof Animatable2Compat) {
                ((Animatable2Compat) drawable).clearAnimationCallbacks();
            }
            ((Animatable) drawable).stop();
        }
    }

    private static void resetAnimations(LottieAnimationView lottieAnimationView) {
        resetAnimation(lottieAnimationView.getDrawable());
        lottieAnimationView.cancelAnimation();
    }

    private void resetImageResourceCache() {
        this.mImageDrawable = null;
        this.mImageUri = null;
        this.mImageResId = 0;
    }

    private void startAnimation(Drawable drawable) {
        if (drawable instanceof Animatable) {
            if (drawable instanceof Animatable2) {
                ((Animatable2) drawable).registerAnimationCallback(this.mAnimationCallback);
            } else if (drawable instanceof Animatable2Compat) {
                ((Animatable2Compat) drawable).registerAnimationCallback(this.mAnimationCallbackCompat);
            } else if (drawable instanceof AnimationDrawable) {
                ((AnimationDrawable) drawable).setOneShot(false);
            }
            ((Animatable) drawable).start();
        }
    }

    private static void startLottieAnimationWith(LottieAnimationView lottieAnimationView, final int i) {
        lottieAnimationView.setFailureListener(new LottieListener() { // from class: com.android.settingslib.widget.IllustrationPreference$$ExternalSyntheticLambda0
            @Override // com.airbnb.lottie.LottieListener
            public final void onResult(Object obj) {
                IllustrationPreference.lambda$startLottieAnimationWith$1(i, (Throwable) obj);
            }
        });
        lottieAnimationView.setAnimation(i);
        lottieAnimationView.setRepeatCount(-1);
        lottieAnimationView.playAnimation();
    }

    private static void startLottieAnimationWith(LottieAnimationView lottieAnimationView, final Uri uri) {
        InputStream inputStreamFromUri = getInputStreamFromUri(lottieAnimationView.getContext(), uri);
        lottieAnimationView.setFailureListener(new LottieListener() { // from class: com.android.settingslib.widget.IllustrationPreference$$ExternalSyntheticLambda1
            @Override // com.airbnb.lottie.LottieListener
            public final void onResult(Object obj) {
                IllustrationPreference.lambda$startLottieAnimationWith$0(uri, (Throwable) obj);
            }
        });
        lottieAnimationView.setAnimation(inputStreamFromUri, null);
        lottieAnimationView.setRepeatCount(-1);
        lottieAnimationView.playAnimation();
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        Folme.clean(view);
        view.setBackgroundResource(0);
        FrameLayout frameLayout = (FrameLayout) preferenceViewHolder.findViewById(R$id.middleground_layout);
        LottieAnimationView lottieAnimationView = (LottieAnimationView) preferenceViewHolder.findViewById(R$id.lottie_view);
        int i = getContext().getResources().getDisplayMetrics().widthPixels;
        int i2 = getContext().getResources().getDisplayMetrics().heightPixels;
        FrameLayout frameLayout2 = (FrameLayout) preferenceViewHolder.findViewById(R$id.illustration_frame);
        ViewGroup.LayoutParams layoutParams = frameLayout2.getLayoutParams();
        if (i >= i2) {
            i = i2;
        }
        layoutParams.width = i;
        frameLayout2.setLayoutParams(layoutParams);
        handleImageWithAnimation(lottieAnimationView);
        boolean z = this.mIsAutoScale;
        if (z) {
            lottieAnimationView.setScaleType(z ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.CENTER_INSIDE);
        }
        handleMiddleGroundView(frameLayout);
    }

    public void setImageUri(Uri uri) {
        if (uri != this.mImageUri) {
            resetImageResourceCache();
            this.mImageUri = uri;
            notifyChanged();
        }
    }

    public void setLottieAnimationResId(int i) {
        if (i != this.mImageResId) {
            resetImageResourceCache();
            this.mImageResId = i;
            notifyChanged();
        }
    }
}
