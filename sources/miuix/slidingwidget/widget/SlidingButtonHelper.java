package miuix.slidingwidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CompoundButton;
import androidx.appcompat.widget.ViewUtils;
import miuix.animation.physics.DynamicAnimation;
import miuix.animation.physics.SpringAnimation;
import miuix.animation.property.FloatProperty;
import miuix.slidingwidget.R$color;
import miuix.slidingwidget.R$dimen;
import miuix.slidingwidget.R$drawable;
import miuix.slidingwidget.R$styleable;
import miuix.smooth.SmoothContainerDrawable;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes5.dex */
public class SlidingButtonHelper {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private boolean mAnimChecked;
    private int mCornerRadius;
    private int mHeight;
    private int mLastX;
    private int mMarginHorizontal;
    private int mMarginVertical;
    private SpringAnimation mMarkedAlphaHideAnim;
    private SpringAnimation mMarkedAlphaShowAnim;
    private Drawable mMaskCheckedSlideBar;
    private float mMaskCheckedSlideBarAlpha;
    private Drawable mMaskUnCheckedPressedSlideBar;
    private Drawable mMaskUnCheckedSlideBar;
    private int mMaxTranslateOffset;
    private CompoundButton.OnCheckedChangeListener mOnPerformCheckedChangeListener;
    private int mOriginalTouchPointX;
    private StateListDrawable mSlideBar;
    private int mSliderHeight;
    private SpringAnimation mSliderMoveAnim;
    private boolean mSliderMoved;
    private Drawable mSliderOff;
    private int mSliderOffset;
    private Drawable mSliderOn;
    private int mSliderOnAlpha;
    private int mSliderPositionEnd;
    private int mSliderPositionStart;
    private SpringAnimation mSliderPressedAnim;
    private Drawable mSliderShadow;
    private SpringAnimation mSliderShadowHideAnim;
    private SpringAnimation mSliderShadowShowAnim;
    private Drawable mSliderStroke;
    private SpringAnimation mSliderUnPressedAnim;
    private int mSliderWidth;
    private int mSlidingBarColor;
    private SpringAnimation mStokeAlphaHideAnim;
    private SpringAnimation mStokeAlphaShowAnim;
    private int mTapThreshold;
    private boolean mTracking;
    private SpringAnimation mUnMarkedPressedAlphaHideAnim;
    private SpringAnimation mUnMarkedPressedAlphaShowAnim;
    private CompoundButton mView;
    private int mWidth;
    private Rect mTmpRect = new Rect();
    private boolean mIsSliderEdgeReached = false;
    private FloatProperty<CompoundButton> mSliderOffsetProperty = new FloatProperty<CompoundButton>("SliderOffset") { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.1
        @Override // miuix.animation.property.FloatProperty
        public float getValue(CompoundButton compoundButton) {
            return SlidingButtonHelper.this.getSliderOffset();
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(CompoundButton compoundButton, float f) {
            SlidingButtonHelper.this.setSliderOffset((int) f);
        }
    };
    private float mSliderScale = 1.0f;
    private float mSliderShadowAlpha = 0.0f;
    private float mStrokeAlpha = 0.1f;
    private float mMaskUnCheckedPressedSlideBarAlpha = 0.0f;
    private boolean mParamCached = false;
    private int mSliderOffsetTemp = -1;
    private int mSliderOnAlphaTemp = -1;
    private boolean mAnimCheckedTemp = false;
    private float mMaskCheckedSlideBarAlphaTemp = -1.0f;
    private FloatProperty<CompoundButton> mSliderScaleFloatProperty = new FloatProperty<CompoundButton>("SliderScale") { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.2
        @Override // miuix.animation.property.FloatProperty
        public float getValue(CompoundButton compoundButton) {
            return SlidingButtonHelper.this.mSliderScale;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(CompoundButton compoundButton, float f) {
            SlidingButtonHelper.this.mSliderScale = f;
        }
    };
    private DynamicAnimation.OnAnimationUpdateListener mInvalidateUpdateListener = new DynamicAnimation.OnAnimationUpdateListener() { // from class: miuix.slidingwidget.widget.SlidingButtonHelper$$ExternalSyntheticLambda0
        @Override // miuix.animation.physics.DynamicAnimation.OnAnimationUpdateListener
        public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
            SlidingButtonHelper.this.lambda$new$0(dynamicAnimation, f, f2);
        }
    };
    private FloatProperty<CompoundButton> mSliderShadowAlphaProperty = new FloatProperty<CompoundButton>("SliderShadowAlpha") { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.3
        @Override // miuix.animation.property.FloatProperty
        public float getValue(CompoundButton compoundButton) {
            return SlidingButtonHelper.this.mSliderShadowAlpha;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(CompoundButton compoundButton, float f) {
            SlidingButtonHelper.this.mSliderShadowAlpha = f;
        }
    };
    private FloatProperty<CompoundButton> mStrokeAlphaProperty = new FloatProperty<CompoundButton>("StrokeAlpha") { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.4
        @Override // miuix.animation.property.FloatProperty
        public float getValue(CompoundButton compoundButton) {
            return SlidingButtonHelper.this.mStrokeAlpha;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(CompoundButton compoundButton, float f) {
            SlidingButtonHelper.this.mStrokeAlpha = f;
        }
    };
    private FloatProperty<CompoundButton> mMaskCheckedSlideBarAlphaProperty = new FloatProperty<CompoundButton>("MaskCheckedSlideBarAlpha") { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.5
        @Override // miuix.animation.property.FloatProperty
        public float getValue(CompoundButton compoundButton) {
            return SlidingButtonHelper.this.mMaskCheckedSlideBarAlpha;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(CompoundButton compoundButton, float f) {
            SlidingButtonHelper.this.mMaskCheckedSlideBarAlpha = f;
        }
    };
    private FloatProperty<CompoundButton> mMaskUnCheckedPressedSlideBarAlphaProperty = new FloatProperty<CompoundButton>("MaskUnCheckedSlideBarAlpha") { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.6
        @Override // miuix.animation.property.FloatProperty
        public float getValue(CompoundButton compoundButton) {
            return SlidingButtonHelper.this.mMaskUnCheckedPressedSlideBarAlpha;
        }

        @Override // miuix.animation.property.FloatProperty
        public void setValue(CompoundButton compoundButton, float f) {
            SlidingButtonHelper.this.mMaskUnCheckedPressedSlideBarAlpha = f;
        }
    };
    private float mExtraAlpha = 1.0f;
    private float[] mTranslateDist = {0.0f, 0.0f};

    public SlidingButtonHelper(CompoundButton compoundButton) {
        this.mMaskCheckedSlideBarAlpha = 1.0f;
        this.mView = compoundButton;
        this.mAnimChecked = compoundButton.isChecked();
        if (this.mView.isChecked()) {
            return;
        }
        this.mMaskCheckedSlideBarAlpha = 0.0f;
    }

    private float[] actualTranslateDist(View view, MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        view.getLocationOnScreen(new int[2]);
        float width = r2[0] + (view.getWidth() * 0.5f);
        float height = r2[1] + (view.getHeight() * 0.5f);
        float width2 = view.getWidth() == 0 ? 0.0f : (rawX - width) / view.getWidth();
        float height2 = view.getHeight() != 0 ? (rawY - height) / view.getHeight() : 0.0f;
        float max = Math.max(-1.0f, Math.min(1.0f, width2));
        float max2 = Math.max(-1.0f, Math.min(1.0f, height2));
        int i = this.mMaxTranslateOffset;
        return new float[]{max * i, max2 * i};
    }

    private void animateToState(boolean z) {
        if (z != this.mView.isChecked()) {
            this.mView.setChecked(z);
            startCheckedChangeAnimInternal(z);
            notifyCheckedChangeListener();
        }
        animateToState(z, z ? this.mSliderPositionEnd : this.mSliderPositionStart, new Runnable() { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.8
            @Override // java.lang.Runnable
            public void run() {
                SlidingButtonHelper slidingButtonHelper = SlidingButtonHelper.this;
                slidingButtonHelper.mAnimChecked = slidingButtonHelper.mSliderOffset >= SlidingButtonHelper.this.mSliderPositionEnd;
            }
        });
    }

    private void animateToState(boolean z, int i, final Runnable runnable) {
        SpringAnimation springAnimation = this.mSliderMoveAnim;
        if (springAnimation != null && springAnimation.isRunning()) {
            this.mSliderMoveAnim.cancel();
        }
        if (z != this.mView.isChecked()) {
            return;
        }
        SpringAnimation springAnimation2 = new SpringAnimation(this.mView, this.mSliderOffsetProperty, i);
        this.mSliderMoveAnim = springAnimation2;
        springAnimation2.getSpring().setStiffness(986.96f);
        this.mSliderMoveAnim.getSpring().setDampingRatio(0.7f);
        this.mSliderMoveAnim.addUpdateListener(this.mInvalidateUpdateListener);
        this.mSliderMoveAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: miuix.slidingwidget.widget.SlidingButtonHelper.7
            @Override // miuix.animation.physics.DynamicAnimation.OnAnimationEndListener
            public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                runnable.run();
            }
        });
        this.mSliderMoveAnim.start();
        if (z) {
            if (!this.mMarkedAlphaShowAnim.isRunning()) {
                this.mMarkedAlphaShowAnim.start();
            }
            if (this.mMarkedAlphaHideAnim.isRunning()) {
                this.mMarkedAlphaHideAnim.cancel();
                return;
            }
            return;
        }
        if (!this.mMarkedAlphaHideAnim.isRunning()) {
            this.mMarkedAlphaHideAnim.start();
        }
        if (this.mMarkedAlphaShowAnim.isRunning()) {
            this.mMarkedAlphaShowAnim.cancel();
        }
    }

    private void animateToggle() {
        animateToState(!this.mView.isChecked());
        HapticCompat.performHapticFeedback(this.mView, HapticFeedbackConstants.MIUI_SWITCH);
    }

    private Drawable createMaskDrawable(Drawable drawable) {
        SmoothContainerDrawable smoothContainerDrawable = new SmoothContainerDrawable();
        smoothContainerDrawable.setLayerType(this.mView.getLayerType());
        smoothContainerDrawable.setCornerRadius(this.mCornerRadius);
        smoothContainerDrawable.setChildDrawable(drawable);
        int i = this.mMarginHorizontal;
        int i2 = this.mMarginVertical;
        smoothContainerDrawable.setBounds(new Rect(i, i2, this.mWidth - i, this.mHeight - i2));
        return smoothContainerDrawable;
    }

    private StateListDrawable createMaskedSlideBar() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.setBounds(0, 0, this.mWidth, this.mHeight);
        stateListDrawable.setCallback(this.mView);
        return stateListDrawable;
    }

    private void initMaskedSlideBar(Drawable drawable, Drawable drawable2, Drawable drawable3) {
        this.mMaskCheckedSlideBar = drawable;
        this.mMaskUnCheckedSlideBar = drawable2;
        this.mMaskUnCheckedPressedSlideBar = drawable3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.mView.invalidate();
    }

    private void moveSlider(int i) {
        if (ViewUtils.isLayoutRtl(this.mView)) {
            i = -i;
        }
        int i2 = this.mSliderOffset + i;
        this.mSliderOffset = i2;
        int i3 = this.mSliderPositionStart;
        if (i2 < i3) {
            this.mSliderOffset = i3;
        } else {
            int i4 = this.mSliderPositionEnd;
            if (i2 > i4) {
                this.mSliderOffset = i4;
            }
        }
        int i5 = this.mSliderOffset;
        boolean z = i5 == i3 || i5 == this.mSliderPositionEnd;
        if (z && !this.mIsSliderEdgeReached) {
            HapticCompat.performHapticFeedback(this.mView, HapticFeedbackConstants.MIUI_SWITCH);
        }
        this.mIsSliderEdgeReached = z;
        setSliderOffset(this.mSliderOffset);
    }

    private void onDrawSlideBar(Canvas canvas, float f) {
        int i = (int) ((1.0f - this.mMaskCheckedSlideBarAlpha) * 255.0f * f);
        if (i > 0) {
            this.mMaskUnCheckedSlideBar.setAlpha(i);
            this.mMaskUnCheckedSlideBar.draw(canvas);
        }
        int i2 = (int) (this.mMaskUnCheckedPressedSlideBarAlpha * 255.0f * f);
        if (i2 > 0) {
            this.mMaskUnCheckedPressedSlideBar.setAlpha(i2);
            this.mMaskUnCheckedPressedSlideBar.draw(canvas);
        }
        int i3 = (int) (this.mMaskCheckedSlideBarAlpha * 255.0f * f);
        if (i3 > 0) {
            this.mMaskCheckedSlideBar.setAlpha(i3);
            this.mMaskCheckedSlideBar.draw(canvas);
        }
    }

    private void onDrawSliderShadow(Canvas canvas, int i, int i2) {
        int intrinsicWidth;
        int intrinsicHeight;
        int i3 = (int) (this.mSliderShadowAlpha * 255.0f);
        if (i3 == 0) {
            return;
        }
        Drawable drawable = this.mSliderShadow;
        if (drawable instanceof BitmapDrawable) {
            intrinsicWidth = ((BitmapDrawable) drawable).getBitmap().getWidth();
            intrinsicHeight = ((BitmapDrawable) this.mSliderShadow).getBitmap().getHeight();
        } else {
            intrinsicWidth = drawable.getIntrinsicWidth();
            intrinsicHeight = this.mSliderShadow.getIntrinsicHeight();
        }
        int i4 = intrinsicWidth / 2;
        int i5 = intrinsicHeight / 2;
        this.mSliderShadow.setBounds(i - i4, i2 - i5, i + i4, i2 + i5);
        this.mSliderShadow.setAlpha(i3);
        this.mSliderShadow.draw(canvas);
    }

    private void onDrawSliderStroke(Canvas canvas, int i, int i2, int i3, int i4, float f) {
        this.mSliderStroke.setAlpha((int) (this.mStrokeAlpha * 255.0f * f));
        this.mSliderStroke.setBounds(i, i2, i3, i4);
        this.mSliderStroke.draw(canvas);
    }

    private void onPressedInner() {
        if (this.mSliderUnPressedAnim.isRunning()) {
            this.mSliderUnPressedAnim.cancel();
        }
        if (!this.mSliderPressedAnim.isRunning()) {
            this.mSliderPressedAnim.start();
        }
        if (!this.mSliderShadowShowAnim.isRunning()) {
            this.mSliderShadowShowAnim.start();
        }
        if (this.mView.isChecked()) {
            return;
        }
        if (this.mUnMarkedPressedAlphaHideAnim.isRunning()) {
            this.mUnMarkedPressedAlphaHideAnim.cancel();
        }
        if (!this.mUnMarkedPressedAlphaShowAnim.isRunning()) {
            this.mUnMarkedPressedAlphaShowAnim.start();
        }
        if (this.mStokeAlphaShowAnim.isRunning()) {
            return;
        }
        this.mStokeAlphaShowAnim.start();
    }

    private void onUnPressedInner() {
        if (this.mSliderPressedAnim.isRunning()) {
            this.mSliderPressedAnim.cancel();
        }
        if (!this.mSliderUnPressedAnim.isRunning()) {
            this.mSliderUnPressedAnim.start();
        }
        if (this.mSliderShadowShowAnim.isRunning()) {
            this.mSliderShadowShowAnim.cancel();
        }
        if (!this.mSliderShadowHideAnim.isRunning()) {
            this.mSliderShadowHideAnim.start();
        }
        if (this.mStokeAlphaShowAnim.isRunning()) {
            this.mStokeAlphaShowAnim.cancel();
        }
        if (this.mView.isChecked()) {
            return;
        }
        if (this.mUnMarkedPressedAlphaShowAnim.isRunning()) {
            this.mUnMarkedPressedAlphaShowAnim.cancel();
        }
        if (!this.mUnMarkedPressedAlphaHideAnim.isRunning()) {
            this.mUnMarkedPressedAlphaHideAnim.start();
        }
        if (this.mStokeAlphaHideAnim.isRunning()) {
            return;
        }
        this.mStokeAlphaHideAnim.start();
    }

    private void popSavedParams() {
        if (this.mParamCached) {
            this.mSliderOffset = this.mSliderOffsetTemp;
            this.mSliderOnAlpha = this.mSliderOnAlphaTemp;
            this.mMaskCheckedSlideBarAlpha = this.mMaskCheckedSlideBarAlphaTemp;
            this.mAnimChecked = this.mAnimCheckedTemp;
            this.mParamCached = false;
            this.mSliderOffsetTemp = -1;
            this.mSliderOnAlphaTemp = -1;
            this.mMaskCheckedSlideBarAlphaTemp = -1.0f;
        }
    }

    private void saveCurrentParams() {
        this.mSliderOffsetTemp = this.mSliderOffset;
        this.mSliderOnAlphaTemp = this.mSliderOnAlpha;
        this.mMaskCheckedSlideBarAlphaTemp = this.mMaskCheckedSlideBarAlpha;
        this.mAnimCheckedTemp = this.mAnimChecked;
        this.mParamCached = true;
    }

    private void scaleCanvasEnd(Canvas canvas) {
        canvas.restore();
    }

    private void scaleCanvasStart(Canvas canvas, int i, int i2) {
        canvas.save();
        float f = this.mSliderScale;
        canvas.scale(f, f, i, i2);
    }

    private void setCheckedInner(boolean z) {
        if (this.mAnimChecked) {
            if (this.mMarkedAlphaHideAnim.isRunning()) {
                this.mMarkedAlphaHideAnim.cancel();
            }
            if (!this.mMarkedAlphaShowAnim.isRunning() && !z) {
                this.mMaskCheckedSlideBarAlpha = 1.0f;
            }
        }
        if (this.mAnimChecked) {
            return;
        }
        if (this.mMarkedAlphaShowAnim.isRunning()) {
            this.mMarkedAlphaShowAnim.cancel();
        }
        if (this.mMarkedAlphaHideAnim.isRunning() || !z) {
            return;
        }
        this.mMaskCheckedSlideBarAlpha = 0.0f;
    }

    private void startCheckedChangeAnimInternal(boolean z) {
        SpringAnimation springAnimation = this.mSliderMoveAnim;
        if (springAnimation == null || !springAnimation.isRunning()) {
            boolean z2 = this.mAnimChecked;
            this.mSliderOffset = z2 ? this.mSliderPositionEnd : this.mSliderPositionStart;
            this.mSliderOnAlpha = z2 ? 255 : 0;
        }
        popSavedParams();
        setCheckedInner(z);
    }

    public float getAlpha() {
        return this.mExtraAlpha;
    }

    public int getMeasuredHeight() {
        return this.mHeight;
    }

    public int getMeasuredWidth() {
        return this.mWidth;
    }

    public StateListDrawable getSlideBar() {
        return this.mSlideBar;
    }

    public int getSliderOffset() {
        return this.mSliderOffset;
    }

    public Drawable getSliderOn() {
        return this.mSliderOn;
    }

    public void initAnim() {
        SpringAnimation springAnimation = new SpringAnimation(this.mView, this.mSliderScaleFloatProperty, 1.61f);
        this.mSliderPressedAnim = springAnimation;
        springAnimation.getSpring().setStiffness(986.96f);
        this.mSliderPressedAnim.getSpring().setDampingRatio(0.6f);
        this.mSliderPressedAnim.setMinimumVisibleChange(0.002f);
        this.mSliderPressedAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation2 = new SpringAnimation(this.mView, this.mSliderScaleFloatProperty, 1.0f);
        this.mSliderUnPressedAnim = springAnimation2;
        springAnimation2.getSpring().setStiffness(986.96f);
        this.mSliderUnPressedAnim.getSpring().setDampingRatio(0.6f);
        this.mSliderUnPressedAnim.setMinimumVisibleChange(0.002f);
        this.mSliderUnPressedAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation3 = new SpringAnimation(this.mView, this.mSliderShadowAlphaProperty, 1.0f);
        this.mSliderShadowShowAnim = springAnimation3;
        springAnimation3.getSpring().setStiffness(986.96f);
        this.mSliderShadowShowAnim.getSpring().setDampingRatio(0.99f);
        this.mSliderShadowShowAnim.setMinimumVisibleChange(0.00390625f);
        this.mSliderShadowShowAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation4 = new SpringAnimation(this.mView, this.mSliderShadowAlphaProperty, 0.0f);
        this.mSliderShadowHideAnim = springAnimation4;
        springAnimation4.getSpring().setStiffness(986.96f);
        this.mSliderShadowHideAnim.getSpring().setDampingRatio(0.99f);
        this.mSliderShadowHideAnim.setMinimumVisibleChange(0.00390625f);
        this.mSliderShadowHideAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation5 = new SpringAnimation(this.mView, this.mStrokeAlphaProperty, 0.15f);
        this.mStokeAlphaShowAnim = springAnimation5;
        springAnimation5.getSpring().setStiffness(986.96f);
        this.mStokeAlphaShowAnim.getSpring().setDampingRatio(0.99f);
        this.mStokeAlphaShowAnim.setMinimumVisibleChange(0.00390625f);
        this.mStokeAlphaShowAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation6 = new SpringAnimation(this.mView, this.mStrokeAlphaProperty, 0.1f);
        this.mStokeAlphaHideAnim = springAnimation6;
        springAnimation6.getSpring().setStiffness(986.96f);
        this.mStokeAlphaHideAnim.getSpring().setDampingRatio(0.99f);
        this.mStokeAlphaHideAnim.setMinimumVisibleChange(0.00390625f);
        this.mStokeAlphaHideAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation7 = new SpringAnimation(this.mView, this.mMaskCheckedSlideBarAlphaProperty, 1.0f);
        this.mMarkedAlphaShowAnim = springAnimation7;
        springAnimation7.getSpring().setStiffness(438.64f);
        this.mMarkedAlphaShowAnim.getSpring().setDampingRatio(0.99f);
        this.mMarkedAlphaShowAnim.setMinimumVisibleChange(0.00390625f);
        this.mMarkedAlphaShowAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation8 = new SpringAnimation(this.mView, this.mMaskCheckedSlideBarAlphaProperty, 0.0f);
        this.mMarkedAlphaHideAnim = springAnimation8;
        springAnimation8.getSpring().setStiffness(986.96f);
        this.mMarkedAlphaHideAnim.getSpring().setDampingRatio(0.99f);
        this.mMarkedAlphaHideAnim.setMinimumVisibleChange(0.00390625f);
        this.mMarkedAlphaHideAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation9 = new SpringAnimation(this.mView, this.mMaskUnCheckedPressedSlideBarAlphaProperty, 0.05f);
        this.mUnMarkedPressedAlphaShowAnim = springAnimation9;
        springAnimation9.getSpring().setStiffness(986.96f);
        this.mUnMarkedPressedAlphaShowAnim.getSpring().setDampingRatio(0.99f);
        this.mUnMarkedPressedAlphaShowAnim.setMinimumVisibleChange(0.00390625f);
        this.mUnMarkedPressedAlphaShowAnim.addUpdateListener(this.mInvalidateUpdateListener);
        SpringAnimation springAnimation10 = new SpringAnimation(this.mView, this.mMaskUnCheckedPressedSlideBarAlphaProperty, 0.0f);
        this.mUnMarkedPressedAlphaHideAnim = springAnimation10;
        springAnimation10.getSpring().setStiffness(986.96f);
        this.mUnMarkedPressedAlphaHideAnim.getSpring().setDampingRatio(0.99f);
        this.mUnMarkedPressedAlphaHideAnim.setMinimumVisibleChange(0.00390625f);
        this.mUnMarkedPressedAlphaHideAnim.addUpdateListener(this.mInvalidateUpdateListener);
    }

    public void initDrawable() {
        this.mSliderShadow = this.mView.getResources().getDrawable(R$drawable.miuix_appcompat_sliding_btn_slider_shadow);
        this.mSliderStroke = this.mView.getResources().getDrawable(R$drawable.miuix_appcompat_sliding_btn_slider_stroke_light);
    }

    public void initResource(Context context, TypedArray typedArray) {
        this.mCornerRadius = this.mView.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_sliding_button_frame_corner_radius);
        this.mMarginVertical = this.mView.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_sliding_button_mask_vertical_padding);
        this.mMarginHorizontal = this.mView.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_sliding_button_mask_horizontal_padding);
        this.mView.setDrawingCacheEnabled(false);
        this.mTapThreshold = ViewConfiguration.get(context).getScaledTouchSlop() / 2;
        this.mSliderOn = typedArray.getDrawable(R$styleable.SlidingButton_sliderOn);
        this.mSliderOff = typedArray.getDrawable(R$styleable.SlidingButton_sliderOff);
        this.mView.setBackground(typedArray.getDrawable(R$styleable.SlidingButton_android_background));
        int parseColor = Color.parseColor("#FF0D84FF");
        int i = Build.VERSION.SDK_INT;
        if (i >= 23) {
            parseColor = context.getColor(R$color.miuix_appcompat_sliding_button_bar_on_light);
        }
        this.mSlidingBarColor = typedArray.getColor(R$styleable.SlidingButton_slidingBarColor, parseColor);
        int dimensionPixelSize = this.mView.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_sliding_button_frame_vertical_padding);
        int dimensionPixelSize2 = this.mView.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_sliding_button_frame_horizontal_padding);
        int dimensionPixelSize3 = this.mView.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_sliding_button_height);
        int dimensionPixelSize4 = (dimensionPixelSize2 * 2) + this.mView.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_sliding_button_width);
        this.mWidth = dimensionPixelSize4;
        this.mHeight = (dimensionPixelSize * 2) + dimensionPixelSize3;
        this.mSliderWidth = Math.min(dimensionPixelSize4, this.mSliderOn.getIntrinsicWidth());
        this.mSliderHeight = Math.min(this.mHeight, this.mSliderOn.getIntrinsicHeight());
        this.mSliderPositionStart = 0;
        this.mSliderPositionEnd = this.mWidth - this.mSliderWidth;
        this.mSliderOffset = 0;
        TypedValue typedValue = new TypedValue();
        int i2 = R$styleable.SlidingButton_barOff;
        typedArray.getValue(i2, typedValue);
        TypedValue typedValue2 = new TypedValue();
        int i3 = R$styleable.SlidingButton_barOn;
        typedArray.getValue(i3, typedValue2);
        Drawable drawable = typedArray.getDrawable(i2);
        Drawable drawable2 = typedArray.getDrawable(i3);
        if (typedValue.type == typedValue2.type && typedValue.data == typedValue2.data && typedValue.resourceId == typedValue2.resourceId) {
            drawable2 = drawable;
        }
        if (drawable2 != null && drawable != null) {
            if (i >= 21) {
                drawable2.setTint(this.mSlidingBarColor);
            }
            initMaskedSlideBar(createMaskDrawable(drawable2), createMaskDrawable(drawable), createMaskDrawable(drawable2));
            this.mSlideBar = createMaskedSlideBar();
        }
        setSliderDrawState();
        if (this.mView.isChecked()) {
            setSliderOffset(this.mSliderPositionEnd);
        }
        this.mMaxTranslateOffset = this.mView.getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_sliding_button_slider_max_offset);
    }

    public void jumpDrawablesToCurrentState() {
        StateListDrawable stateListDrawable = this.mSlideBar;
        if (stateListDrawable != null) {
            stateListDrawable.jumpToCurrentState();
        }
    }

    public void notifyCheckedChangeListener() {
        if (this.mOnPerformCheckedChangeListener != null) {
            this.mOnPerformCheckedChangeListener.onCheckedChanged(this.mView, this.mView.isChecked());
        }
    }

    public void onDraw(Canvas canvas) {
        int i = (int) ((this.mView.isEnabled() ? 255 : 127) * this.mExtraAlpha);
        float f = i / 255.0f;
        onDrawSlideBar(canvas, f);
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this.mView);
        int i2 = isLayoutRtl ? (this.mWidth - this.mSliderOffset) - this.mSliderWidth : this.mSliderOffset;
        float[] fArr = this.mTranslateDist;
        int i3 = ((int) fArr[0]) + i2;
        int i4 = (isLayoutRtl ? this.mWidth - this.mSliderOffset : this.mSliderWidth + this.mSliderOffset) + ((int) fArr[0]);
        int i5 = this.mHeight;
        int i6 = this.mSliderHeight;
        int i7 = ((i5 - i6) / 2) + ((int) fArr[1]);
        int i8 = i7 + i6;
        int i9 = (i4 + i3) / 2;
        int i10 = (i8 + i7) / 2;
        onDrawSliderShadow(canvas, i9, i10);
        scaleCanvasStart(canvas, i9, i10);
        if (this.mAnimChecked) {
            this.mSliderOn.setAlpha(i);
            this.mSliderOn.setBounds(i3, i7, i4, i8);
            this.mSliderOn.draw(canvas);
        } else {
            this.mSliderOff.setAlpha(i);
            this.mSliderOff.setBounds(i3, i7, i4, i8);
            this.mSliderOff.draw(canvas);
        }
        onDrawSliderStroke(canvas, i3, i7, i4, i8, f);
        scaleCanvasEnd(canvas);
    }

    public void onHoverEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 7) {
            this.mTranslateDist = actualTranslateDist(this.mView, motionEvent);
            this.mView.invalidate();
        } else if (actionMasked == 9) {
            if (this.mSliderUnPressedAnim.isRunning()) {
                this.mSliderUnPressedAnim.cancel();
            }
            this.mSliderPressedAnim.start();
        } else if (actionMasked != 10) {
        } else {
            float[] fArr = this.mTranslateDist;
            fArr[0] = 0.0f;
            fArr[1] = 0.0f;
            if (this.mSliderPressedAnim.isRunning()) {
                this.mSliderPressedAnim.cancel();
            }
            this.mSliderUnPressedAnim.start();
        }
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        Rect rect = this.mTmpRect;
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this.mView);
        rect.set(isLayoutRtl ? (this.mWidth - this.mSliderOffset) - this.mSliderWidth : this.mSliderOffset, 0, isLayoutRtl ? this.mWidth - this.mSliderOffset : this.mSliderOffset + this.mSliderWidth, this.mHeight);
        if (action == 0) {
            if (rect.contains(x, y)) {
                this.mTracking = true;
                this.mView.setPressed(true);
                onPressedInner();
                int i = this.mSliderOffset;
                if (i > this.mSliderPositionStart && i < this.mSliderPositionEnd) {
                    r3 = false;
                }
                this.mIsSliderEdgeReached = r3;
            } else {
                this.mTracking = false;
            }
            this.mLastX = x;
            this.mOriginalTouchPointX = x;
            this.mSliderMoved = false;
        } else if (action == 1) {
            this.mView.playSoundEffect(0);
            onUnPressedInner();
            if (!this.mTracking) {
                animateToggle();
            } else if (this.mSliderMoved) {
                r3 = this.mSliderOffset >= this.mSliderPositionEnd / 2;
                this.mAnimChecked = r3;
                animateToState(r3);
                if (rect.contains(x, y)) {
                    HapticCompat.performHapticFeedback(this.mView, HapticFeedbackConstants.MIUI_SWITCH);
                }
            } else {
                animateToggle();
            }
            this.mTracking = false;
            this.mSliderMoved = false;
            this.mView.setPressed(false);
        } else if (action == 2) {
            if (this.mTracking) {
                moveSlider(x - this.mLastX);
                this.mLastX = x;
                if (Math.abs(x - this.mOriginalTouchPointX) >= this.mTapThreshold) {
                    this.mSliderMoved = true;
                    this.mView.getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
        } else if (action != 3) {
        } else {
            onUnPressedInner();
            if (this.mTracking) {
                r3 = this.mSliderOffset >= this.mSliderPositionEnd / 2;
                this.mAnimChecked = r3;
                animateToState(r3);
            }
            this.mTracking = false;
            this.mSliderMoved = false;
            this.mView.setPressed(false);
        }
    }

    public void setAlpha(float f) {
        this.mExtraAlpha = f;
    }

    public void setChecked(boolean z) {
        saveCurrentParams();
        this.mAnimChecked = z;
        this.mSliderOffset = z ? this.mSliderPositionEnd : this.mSliderPositionStart;
        this.mSliderOnAlpha = z ? 255 : 0;
        this.mMaskCheckedSlideBarAlpha = z ? 1.0f : 0.0f;
        SpringAnimation springAnimation = this.mSliderMoveAnim;
        if (springAnimation != null && springAnimation.isRunning()) {
            this.mSliderMoveAnim.cancel();
        }
        if (this.mMarkedAlphaHideAnim.isRunning()) {
            this.mMarkedAlphaHideAnim.cancel();
        }
        if (this.mMarkedAlphaShowAnim.isRunning()) {
            this.mMarkedAlphaShowAnim.cancel();
        }
        this.mView.invalidate();
    }

    public void setLayerType(int i) {
        Drawable drawable = this.mMaskCheckedSlideBar;
        if (drawable instanceof SmoothContainerDrawable) {
            ((SmoothContainerDrawable) drawable).setLayerType(i);
        }
        Drawable drawable2 = this.mMaskUnCheckedSlideBar;
        if (drawable2 instanceof SmoothContainerDrawable) {
            ((SmoothContainerDrawable) drawable2).setLayerType(i);
        }
        Drawable drawable3 = this.mMaskUnCheckedPressedSlideBar;
        if (drawable3 instanceof SmoothContainerDrawable) {
            ((SmoothContainerDrawable) drawable3).setLayerType(i);
        }
    }

    public void setOnPerformCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnPerformCheckedChangeListener = onCheckedChangeListener;
    }

    public void setParentClipChildren() {
        ViewParent parent = this.mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).setClipChildren(false);
        }
    }

    public void setSliderDrawState() {
        if (getSliderOn() != null) {
            getSliderOn().setState(this.mView.getDrawableState());
            getSlideBar().setState(this.mView.getDrawableState());
        }
    }

    public void setSliderOffset(int i) {
        this.mSliderOffset = i;
        this.mView.invalidate();
    }

    public boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mSlideBar;
    }
}
