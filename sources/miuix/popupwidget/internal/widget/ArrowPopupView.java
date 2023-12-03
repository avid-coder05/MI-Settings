package miuix.popupwidget.internal.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import miuix.internal.util.DeviceHelper;
import miuix.internal.util.ViewUtils;
import miuix.popupwidget.R$attr;
import miuix.popupwidget.R$dimen;
import miuix.popupwidget.R$id;
import miuix.popupwidget.R$style;
import miuix.popupwidget.R$styleable;
import miuix.popupwidget.widget.ArrowPopupWindow;
import miuix.view.CompatViewMethod;

/* loaded from: classes5.dex */
public class ArrowPopupView extends FrameLayout implements View.OnTouchListener {
    private View mAnchor;
    private AnimationSet mAnimationSet;
    private AnimatorSet mAnimator;
    private AppCompatImageView mArrow;
    private Drawable mArrowBottom;
    private Drawable mArrowBottomLeft;
    private Drawable mArrowBottomRight;
    private Drawable mArrowLeft;
    private int mArrowMode;
    private ArrowPopupWindow mArrowPopupWindow;
    private Drawable mArrowRight;
    private int mArrowSpaceLeft;
    private int mArrowSpaceTop;
    private Drawable mArrowTop;
    private Drawable mArrowTopLeft;
    private Drawable mArrowTopRight;
    private Drawable mArrowTopWithTitle;
    private boolean mAutoDismiss;
    private Drawable mBackground;
    private Drawable mBackgroundLeft;
    private Drawable mBackgroundRight;
    private FrameLayout mContentFrame;
    private LinearLayout mContentFrameWrapper;
    private int mElevation;
    private Animation.AnimationListener mHideAnimatorListener;
    private boolean mIsDismissing;
    private int mMinBorder;
    private AppCompatButton mNegativeButton;
    private WrapperOnClickListener mNegativeClickListener;
    private int mOffsetX;
    private int mOffsetY;
    private AppCompatButton mPositiveButton;
    private WrapperOnClickListener mPositiveClickListener;
    private Animation.AnimationListener mShowAnimationListener;
    private boolean mShowingAnimation;
    private int mSpaceLeft;
    private int mSpaceTop;
    private Drawable mTitleBackground;
    private LinearLayout mTitleLayout;
    private AppCompatTextView mTitleText;
    private Rect mTmpRect;
    private RectF mTmpRectF;
    private View.OnTouchListener mTouchInterceptor;
    private int mTranslationValue;

    /* loaded from: classes5.dex */
    class WrapperOnClickListener implements View.OnClickListener {
        public View.OnClickListener mOnClickListener;

        WrapperOnClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            View.OnClickListener onClickListener = this.mOnClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(view);
            }
            ArrowPopupView.this.mArrowPopupWindow.dismiss(true);
        }
    }

    public ArrowPopupView(Context context) {
        this(context, null);
    }

    public ArrowPopupView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.arrowPopupViewStyle);
    }

    public ArrowPopupView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTmpRect = new Rect();
        this.mTmpRectF = new RectF();
        this.mAutoDismiss = true;
        this.mShowingAnimation = false;
        this.mShowAnimationListener = new Animation.AnimationListener() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                ArrowPopupView.this.mAnimationSet = null;
                if (ArrowPopupView.this.mShowingAnimation) {
                    ArrowPopupView.this.animateShowing();
                }
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }
        };
        this.mHideAnimatorListener = new Animation.AnimationListener() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.2
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                ArrowPopupView.this.mIsDismissing = false;
                ArrowPopupView.this.mAnimationSet = null;
                ArrowPopupView.this.mArrowPopupWindow.dismiss();
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }
        };
        this.mArrowMode = 0;
        CompatViewMethod.setForceDarkAllowed(this, false);
        this.mAutoDismiss = true;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ArrowPopupView, i, R$style.Widget_ArrowPopupView_DayNight);
        this.mBackground = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_contentBackground);
        this.mBackgroundLeft = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_backgroundLeft);
        this.mBackgroundRight = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_backgroundRight);
        this.mTitleBackground = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_titleBackground);
        this.mArrowTop = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_topArrow);
        this.mArrowTopWithTitle = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_topArrowWithTitle);
        this.mArrowBottom = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_bottomArrow);
        this.mArrowRight = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_rightArrow);
        this.mArrowLeft = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_leftArrow);
        this.mArrowTopLeft = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_topLeftArrow);
        this.mArrowTopRight = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_topRightArrow);
        this.mArrowBottomRight = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_bottomRightArrow);
        this.mArrowBottomLeft = obtainStyledAttributes.getDrawable(R$styleable.ArrowPopupView_bottomLeftArrow);
        this.mElevation = obtainStyledAttributes.getDimensionPixelOffset(R$styleable.ArrowPopupView_android_elevation, getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_arrow_popup_window_elevation));
        obtainStyledAttributes.recycle();
        this.mMinBorder = context.getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_arrow_popup_window_min_border);
    }

    private void addShadow(View view, ViewOutlineProvider viewOutlineProvider) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            view.setOutlineProvider(viewOutlineProvider);
        }
        if (i >= 21) {
            view.setElevation(this.mElevation);
        }
    }

    private void adjustArrowMode() {
        int[] iArr = new int[2];
        this.mAnchor.getLocationInWindow(iArr);
        int width = getWidth();
        int height = getHeight();
        int measuredWidth = this.mContentFrameWrapper.getMeasuredWidth();
        int measuredHeight = this.mContentFrameWrapper.getMeasuredHeight();
        int height2 = this.mAnchor.getHeight();
        int width2 = this.mAnchor.getWidth();
        SparseIntArray sparseIntArray = new SparseIntArray(4);
        int i = 16;
        sparseIntArray.put(16, iArr[1] - measuredHeight);
        sparseIntArray.put(8, ((height - iArr[1]) - height2) - measuredHeight);
        int i2 = 0;
        sparseIntArray.put(64, iArr[0] - measuredWidth);
        sparseIntArray.put(32, ((width - iArr[0]) - width2) - measuredWidth);
        int i3 = Integer.MIN_VALUE;
        while (true) {
            if (i2 >= sparseIntArray.size()) {
                break;
            }
            int keyAt = sparseIntArray.keyAt(i2);
            if (sparseIntArray.get(keyAt) >= this.mMinBorder) {
                i = keyAt;
                break;
            }
            if (sparseIntArray.get(keyAt) > i3) {
                i3 = sparseIntArray.get(keyAt);
                i = keyAt;
            }
            i2++;
        }
        setArrowMode(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateShowing() {
        if (DeviceHelper.isFeatureWholeAnim()) {
            AnimationSet animationSet = this.mAnimationSet;
            if (animationSet != null) {
                animationSet.cancel();
            }
            AnimatorSet animatorSet = this.mAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.mAnimator = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ArrowPopupView.this.mArrowPopupWindow.dismiss();
                }
            });
            float f = getContext().getResources().getDisplayMetrics().density * 4.0f;
            Property property = View.TRANSLATION_Y;
            int i = this.mArrowMode;
            if (i == 16) {
                f = -f;
            } else if (i == 32) {
                if (ViewUtils.isLayoutRtl(this)) {
                    f = -f;
                }
                property = View.TRANSLATION_X;
            } else if (i == 64) {
                if (!ViewUtils.isLayoutRtl(this)) {
                    f = -f;
                }
                property = View.TRANSLATION_X;
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mContentFrameWrapper, property, 0.0f, f, 0.0f);
            ofFloat.setInterpolator(new AccelerateDecelerateInterpolator());
            ofFloat.setDuration(1200L);
            if (this.mAutoDismiss) {
                ofFloat.setRepeatCount(8);
            } else {
                ofFloat.setRepeatCount(-1);
            }
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.7
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ArrowPopupView.this.mTranslationValue = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    int abs = Math.abs(ArrowPopupView.this.mTranslationValue);
                    ArrowPopupView arrowPopupView = ArrowPopupView.this;
                    arrowPopupView.invalidate(arrowPopupView.mContentFrameWrapper.getLeft() - abs, ArrowPopupView.this.mContentFrameWrapper.getTop() - abs, ArrowPopupView.this.mContentFrameWrapper.getRight() + abs, ArrowPopupView.this.mContentFrameWrapper.getBottom() + abs);
                }
            });
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.mArrow, property, 0.0f, f, 0.0f);
            ofFloat2.setInterpolator(new AccelerateDecelerateInterpolator());
            ofFloat2.setDuration(1200L);
            if (this.mAutoDismiss) {
                ofFloat2.setRepeatCount(8);
            } else {
                ofFloat2.setRepeatCount(-1);
            }
            this.mAnimator.playTogether(ofFloat, ofFloat2);
            this.mAnimator.start();
        }
    }

    private void arrowHorizontalLayout() {
        int i;
        int paddingLeft;
        int paddingLeft2;
        int[] iArr = new int[2];
        this.mAnchor.getLocationOnScreen(iArr);
        int i2 = iArr[0];
        int i3 = iArr[1];
        getLocationOnScreen(iArr);
        int width = this.mAnchor.getWidth();
        int height = this.mAnchor.getHeight();
        int width2 = getWidth();
        int height2 = getHeight();
        int measuredWidth = this.mContentFrameWrapper.getMeasuredWidth() > this.mContentFrameWrapper.getMinimumWidth() ? this.mContentFrameWrapper.getMeasuredWidth() : this.mContentFrameWrapper.getMinimumWidth();
        int measuredHeight = this.mContentFrameWrapper.getMeasuredHeight() > this.mContentFrameWrapper.getMinimumHeight() ? this.mContentFrameWrapper.getMeasuredHeight() : this.mContentFrameWrapper.getMinimumHeight();
        int arrowWidth = getArrowWidth();
        int arrowHeight = getArrowHeight();
        int i4 = ((height / 2) + i3) - iArr[1];
        this.mSpaceTop = i4;
        int i5 = height2 - i4;
        this.mArrowSpaceTop = ((i3 + ((height - arrowHeight) / 2)) - iArr[1]) + ((this.mContentFrameWrapper.getPaddingTop() - this.mContentFrameWrapper.getPaddingBottom()) / 2);
        int i6 = measuredHeight / 2;
        int i7 = measuredHeight - i6;
        this.mSpaceLeft = getLeft() + this.mOffsetX;
        if (isRightMode()) {
            if (ViewUtils.isLayoutRtl(this)) {
                paddingLeft = this.mSpaceLeft + ((((i2 + width) - this.mContentFrameWrapper.getPaddingLeft()) + arrowWidth) - iArr[0]);
                this.mSpaceLeft = paddingLeft;
                paddingLeft2 = this.mContentFrameWrapper.getPaddingLeft();
                i = paddingLeft + (paddingLeft2 - arrowWidth) + 1;
            } else {
                this.mSpaceLeft += (((i2 - measuredWidth) + this.mContentFrameWrapper.getPaddingRight()) - arrowWidth) - iArr[0];
                i = (((i2 - arrowWidth) - iArr[0]) + this.mOffsetX) - 1;
            }
        } else if (!isLeftMode()) {
            i = 0;
        } else if (ViewUtils.isLayoutRtl(this)) {
            this.mSpaceLeft += ((((i2 - measuredWidth) + this.mContentFrameWrapper.getPaddingRight()) - arrowWidth) - iArr[0]) + 1;
            i = (((i2 - arrowWidth) - iArr[0]) + this.mOffsetX) - 1;
        } else {
            paddingLeft = this.mSpaceLeft + ((((i2 + width) - this.mContentFrameWrapper.getPaddingLeft()) + arrowWidth) - iArr[0]);
            this.mSpaceLeft = paddingLeft;
            paddingLeft2 = this.mContentFrameWrapper.getPaddingLeft();
            i = paddingLeft + (paddingLeft2 - arrowWidth) + 1;
        }
        int i8 = this.mSpaceTop;
        if (i8 >= i6 && i5 >= i7) {
            this.mSpaceTop = (i8 - i6) + this.mOffsetY;
        } else if (i5 < i7) {
            this.mSpaceTop = (height2 - measuredHeight) + this.mOffsetY;
        } else if (i8 < i6) {
            this.mSpaceTop = this.mOffsetY;
        }
        int i9 = this.mArrowSpaceTop + this.mOffsetY;
        this.mArrowSpaceTop = i9;
        if (i9 < 0) {
            this.mArrowSpaceTop = 0;
        } else if (i9 + arrowHeight > height2) {
            this.mArrowSpaceTop = i9 - ((i9 + arrowHeight) - height2);
        }
        this.mContentFrameWrapper.layout(Math.max(this.mSpaceLeft, 0), Math.max(this.mSpaceTop, 0), Math.min(this.mSpaceLeft + measuredWidth, width2), Math.min(this.mSpaceTop + measuredHeight, height2));
        AppCompatImageView appCompatImageView = this.mArrow;
        int i10 = this.mArrowSpaceTop;
        appCompatImageView.layout(i, i10, arrowWidth + i, arrowHeight + i10);
    }

    private void arrowLayout() {
        this.mOffsetX = ViewUtils.isLayoutRtl(this) ? -this.mOffsetX : this.mOffsetX;
        if (isVerticalMode()) {
            arrowVerticalLayout();
        } else {
            arrowHorizontalLayout();
        }
        View contentView = getContentView();
        if (contentView != null) {
            ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
            if (contentView.getMeasuredHeight() > this.mContentFrameWrapper.getMeasuredHeight() - this.mTitleLayout.getMeasuredHeight()) {
                layoutParams.height = this.mContentFrameWrapper.getMeasuredHeight() - this.mTitleLayout.getMeasuredHeight();
                contentView.setLayoutParams(layoutParams);
            } else if (contentView.getMeasuredWidth() > this.mContentFrameWrapper.getMeasuredWidth()) {
                layoutParams.width = this.mContentFrameWrapper.getMeasuredWidth();
                contentView.setLayoutParams(layoutParams);
            }
            if (layoutParams.height <= 0 || layoutParams.width <= 0) {
                Log.w("ArrowPopupView", "Invalid LayoutPrams of content view, please check the anchor view");
            }
        }
    }

    private void arrowVerticalLayout() {
        int i;
        int width = this.mAnchor.getWidth();
        int height = this.mAnchor.getHeight();
        int width2 = getWidth();
        int height2 = getHeight();
        int measuredWidth = this.mContentFrameWrapper.getMeasuredWidth() > this.mContentFrameWrapper.getMinimumWidth() ? this.mContentFrameWrapper.getMeasuredWidth() : this.mContentFrameWrapper.getMinimumWidth();
        int measuredHeight = this.mContentFrameWrapper.getMeasuredHeight() > this.mContentFrameWrapper.getMinimumHeight() ? this.mContentFrameWrapper.getMeasuredHeight() : this.mContentFrameWrapper.getMinimumHeight();
        int arrowWidth = getArrowWidth();
        int arrowHeight = getArrowHeight();
        int[] iArr = new int[2];
        this.mAnchor.getLocationOnScreen(iArr);
        int i2 = iArr[0];
        int i3 = iArr[1];
        getLocationOnScreen(iArr);
        int i4 = ((width / 2) + i2) - iArr[0];
        this.mSpaceLeft = i4;
        int i5 = width2 - i4;
        this.mArrowSpaceLeft = (i2 + ((width - arrowWidth) / 2)) - iArr[0];
        this.mSpaceTop = getTop() + this.mOffsetY;
        if (isBottomMode()) {
            this.mSpaceTop += ((i3 - iArr[1]) - measuredHeight) + (this.mContentFrameWrapper.getPaddingBottom() - arrowHeight);
            i = (((i3 - iArr[1]) - arrowHeight) + this.mOffsetY) - 1;
        } else if (isTopMode()) {
            int paddingTop = this.mSpaceTop + (((i3 + height) - iArr[1]) - this.mContentFrameWrapper.getPaddingTop()) + arrowHeight;
            this.mSpaceTop = paddingTop;
            i = paddingTop + (this.mContentFrameWrapper.getPaddingTop() - arrowHeight) + 1;
        } else {
            i = 0;
        }
        int i6 = measuredWidth / 2;
        int i7 = measuredWidth - i6;
        int i8 = this.mSpaceLeft;
        if (i8 >= i6 && i5 >= i7) {
            this.mSpaceLeft = i8 - i6;
        } else if (i5 < i7) {
            this.mSpaceLeft = width2 - measuredWidth;
        } else if (i8 < i6) {
            this.mSpaceLeft = 0;
        }
        int i9 = this.mSpaceLeft;
        int i10 = this.mOffsetX;
        int i11 = i9 + i10;
        this.mSpaceLeft = i11;
        int i12 = this.mArrowSpaceLeft + i10;
        this.mArrowSpaceLeft = i12;
        if (i12 < 0) {
            this.mArrowSpaceLeft = 0;
        } else if (i12 + arrowWidth > width2) {
            this.mArrowSpaceLeft = i12 - ((i12 + arrowWidth) - width2);
        }
        this.mContentFrameWrapper.layout(Math.max(i11, 0), Math.max(this.mSpaceTop, 0), Math.min(this.mSpaceLeft + measuredWidth, width2), Math.min(this.mSpaceTop + measuredHeight, height2));
        executeLayoutArrow(arrowWidth, arrowHeight, i);
    }

    private void executeLayoutArrow(int i, int i2, int i3) {
        int right;
        int i4;
        int right2;
        int bottom;
        int measuredHeight;
        int i5;
        int left;
        int bottom2;
        int measuredHeight2;
        int i6 = this.mArrowMode;
        if (i6 == 9) {
            right = ViewUtils.isLayoutRtl(this) ? (this.mContentFrameWrapper.getRight() - this.mContentFrameWrapper.getPaddingStart()) - i : (this.mContentFrameWrapper.getLeft() + this.mContentFrameWrapper.getPaddingStart()) - 1;
            i3 = (i3 + this.mContentFrameWrapper.getPaddingTop()) - i2;
            AppCompatImageView appCompatImageView = this.mArrow;
            appCompatImageView.layout(right, i3, right + i, appCompatImageView.getMeasuredHeight() + i3);
        } else if (i6 != 10) {
            if (i6 == 17) {
                if (ViewUtils.isLayoutRtl(this)) {
                    right2 = this.mContentFrameWrapper.getLeft() + this.mContentFrameWrapper.getPaddingStart();
                    bottom = this.mContentFrameWrapper.getBottom() - this.mContentFrameWrapper.getPaddingBottom();
                    measuredHeight = this.mArrow.getMeasuredHeight();
                } else {
                    right2 = (this.mContentFrameWrapper.getRight() - this.mContentFrameWrapper.getPaddingEnd()) - i;
                    bottom = this.mContentFrameWrapper.getBottom() - this.mContentFrameWrapper.getPaddingBottom();
                    measuredHeight = this.mArrow.getMeasuredHeight();
                }
                i5 = bottom - (measuredHeight - i2);
                i4 = right2;
            } else if (i6 != 18) {
                i4 = this.mArrowSpaceLeft;
                AppCompatImageView appCompatImageView2 = this.mArrow;
                appCompatImageView2.layout(i4, i3, i + i4, appCompatImageView2.getDrawable().getIntrinsicHeight() + i3);
            } else {
                if (ViewUtils.isLayoutRtl(this)) {
                    left = (this.mContentFrameWrapper.getRight() - this.mContentFrameWrapper.getPaddingEnd()) - i;
                    bottom2 = this.mContentFrameWrapper.getBottom() - this.mContentFrameWrapper.getPaddingBottom();
                    measuredHeight2 = this.mArrow.getMeasuredHeight();
                } else {
                    left = this.mContentFrameWrapper.getLeft() + this.mContentFrameWrapper.getPaddingStart();
                    bottom2 = this.mContentFrameWrapper.getBottom() - this.mContentFrameWrapper.getPaddingBottom();
                    measuredHeight2 = this.mArrow.getMeasuredHeight();
                }
                i5 = bottom2 - (measuredHeight2 - i2);
                i4 = left;
                AppCompatImageView appCompatImageView3 = this.mArrow;
                appCompatImageView3.layout(i4, i5, i4 + i, appCompatImageView3.getMeasuredHeight() + i5);
            }
            i3 = i5 - 5;
            AppCompatImageView appCompatImageView22 = this.mArrow;
            appCompatImageView22.layout(i4, i3, i + i4, appCompatImageView22.getDrawable().getIntrinsicHeight() + i3);
        } else {
            right = ViewUtils.isLayoutRtl(this) ? (this.mContentFrameWrapper.getLeft() + this.mContentFrameWrapper.getPaddingStart()) - 1 : ((this.mContentFrameWrapper.getRight() - this.mContentFrameWrapper.getPaddingEnd()) - i) + 1;
            i3 = (i3 + this.mContentFrameWrapper.getPaddingTop()) - i2;
            AppCompatImageView appCompatImageView4 = this.mArrow;
            appCompatImageView4.layout(right, i3, right + i, appCompatImageView4.getMeasuredHeight() + i3);
        }
        i4 = right;
        AppCompatImageView appCompatImageView222 = this.mArrow;
        appCompatImageView222.layout(i4, i3, i + i4, appCompatImageView222.getDrawable().getIntrinsicHeight() + i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:10:0x0034, code lost:
    
        if (miuix.internal.util.ViewUtils.isLayoutRtl(r6) != false) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0037, code lost:
    
        r6 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x003d, code lost:
    
        if (miuix.internal.util.ViewUtils.isLayoutRtl(r6) != false) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x004a, code lost:
    
        if (miuix.internal.util.ViewUtils.isLayoutRtl(r6) != false) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x004c, code lost:
    
        r6 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0052, code lost:
    
        if (miuix.internal.util.ViewUtils.isLayoutRtl(r6) != false) goto L26;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void getAnimationPivot(float[] r7) {
        /*
            r6 = this;
            androidx.appcompat.widget.AppCompatImageView r0 = r6.mArrow
            int r0 = r0.getTop()
            androidx.appcompat.widget.AppCompatImageView r1 = r6.mArrow
            int r1 = r1.getBottom()
            androidx.appcompat.widget.AppCompatImageView r2 = r6.mArrow
            int r2 = r2.getLeft()
            androidx.appcompat.widget.AppCompatImageView r3 = r6.mArrow
            int r3 = r3.getRight()
            int r4 = r6.mArrowMode
            r5 = 32
            if (r4 == r5) goto L68
            r5 = 64
            if (r4 == r5) goto L5b
            switch(r4) {
                case 8: goto L55;
                case 9: goto L4e;
                case 10: goto L46;
                default: goto L25;
            }
        L25:
            switch(r4) {
                case 16: goto L40;
                case 17: goto L39;
                case 18: goto L30;
                default: goto L28;
            }
        L28:
            int r3 = r3 + r2
            int r3 = r3 / 2
            float r6 = (float) r3
            int r1 = r1 + r0
            int r1 = r1 / 2
            goto L44
        L30:
            boolean r6 = miuix.internal.util.ViewUtils.isLayoutRtl(r6)
            if (r6 == 0) goto L37
            goto L43
        L37:
            float r6 = (float) r2
            goto L44
        L39:
            boolean r6 = miuix.internal.util.ViewUtils.isLayoutRtl(r6)
            if (r6 == 0) goto L43
            goto L37
        L40:
            int r3 = r3 + r2
            int r3 = r3 / 2
        L43:
            float r6 = (float) r3
        L44:
            float r0 = (float) r1
            goto L75
        L46:
            boolean r6 = miuix.internal.util.ViewUtils.isLayoutRtl(r6)
            if (r6 == 0) goto L58
        L4c:
            float r6 = (float) r2
            goto L59
        L4e:
            boolean r6 = miuix.internal.util.ViewUtils.isLayoutRtl(r6)
            if (r6 == 0) goto L4c
            goto L58
        L55:
            int r3 = r3 + r2
            int r3 = r3 / 2
        L58:
            float r6 = (float) r3
        L59:
            float r0 = (float) r0
            goto L75
        L5b:
            boolean r6 = miuix.internal.util.ViewUtils.isLayoutRtl(r6)
            if (r6 == 0) goto L63
            float r6 = (float) r2
            goto L64
        L63:
            float r6 = (float) r3
        L64:
            int r1 = r1 + r0
            int r1 = r1 / 2
            goto L44
        L68:
            boolean r6 = miuix.internal.util.ViewUtils.isLayoutRtl(r6)
            if (r6 == 0) goto L70
            float r6 = (float) r3
            goto L71
        L70:
            float r6 = (float) r2
        L71:
            int r1 = r1 + r0
            int r1 = r1 / 2
            goto L44
        L75:
            r1 = 0
            r7[r1] = r6
            r6 = 1
            r7[r6] = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.popupwidget.internal.widget.ArrowPopupView.getAnimationPivot(float[]):void");
    }

    private int getArrowHeight() {
        int i = this.mArrowMode;
        if (i == 9 || i == 10) {
            return this.mArrowTop.getIntrinsicHeight();
        }
        if (i == 17 || i == 18) {
            return this.mArrowBottom.getIntrinsicHeight();
        }
        int measuredHeight = this.mArrow.getMeasuredHeight();
        return measuredHeight == 0 ? this.mArrow.getDrawable().getIntrinsicHeight() : measuredHeight;
    }

    private int getArrowWidth() {
        int measuredWidth = this.mArrow.getMeasuredWidth();
        return measuredWidth == 0 ? this.mArrow.getDrawable().getIntrinsicWidth() : measuredWidth;
    }

    private boolean isBottomMode() {
        return isCertainMode(16);
    }

    private boolean isCertainMode(int i) {
        return (this.mArrowMode & i) == i;
    }

    private boolean isLeftMode() {
        return isCertainMode(32);
    }

    private boolean isRightMode() {
        return isCertainMode(64);
    }

    private boolean isTopMode() {
        return isCertainMode(8);
    }

    private boolean isVerticalMode() {
        return isTopMode() || isBottomMode();
    }

    private void updateArrowDrawable(int i) {
        if (i == 32) {
            this.mArrow.setImageDrawable(ViewUtils.isLayoutRtl(this) ? this.mArrowRight : this.mArrowLeft);
        } else if (i == 64) {
            this.mArrow.setImageDrawable(ViewUtils.isLayoutRtl(this) ? this.mArrowLeft : this.mArrowRight);
        } else {
            switch (i) {
                case 8:
                    this.mArrow.setImageDrawable(this.mTitleLayout.getVisibility() == 0 ? this.mArrowTopWithTitle : this.mArrowTop);
                    return;
                case 9:
                    this.mArrow.setImageDrawable(ViewUtils.isLayoutRtl(this) ? this.mArrowTopRight : this.mArrowTopLeft);
                    return;
                case 10:
                    this.mArrow.setImageDrawable(ViewUtils.isLayoutRtl(this) ? this.mArrowTopLeft : this.mArrowTopRight);
                    return;
                default:
                    switch (i) {
                        case 16:
                            this.mArrow.setImageDrawable(this.mArrowBottom);
                            return;
                        case 17:
                            this.mArrow.setImageDrawable(ViewUtils.isLayoutRtl(this) ? this.mArrowBottomLeft : this.mArrowBottomRight);
                            return;
                        case 18:
                            this.mArrow.setImageDrawable(ViewUtils.isLayoutRtl(this) ? this.mArrowBottomRight : this.mArrowBottomLeft);
                            return;
                        default:
                            return;
                    }
            }
        }
    }

    public void addShadow() {
        if (Build.VERSION.SDK_INT >= 21) {
            addShadow(this.mArrow, new ViewOutlineProvider() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.3
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    if (view.getWidth() == 0 || view.getHeight() == 0) {
                        return;
                    }
                    int width = view.getWidth();
                    int height = view.getHeight();
                    Rect rect = new Rect(0, 0, width, height);
                    if (width > height) {
                        int i = (width - height) / 2;
                        rect.left += i;
                        rect.right -= i;
                    } else {
                        int i2 = (height - width) / 2;
                        rect.top += i2;
                        rect.bottom -= i2;
                    }
                    Path path = new Path();
                    int i3 = ArrowPopupView.this.mArrowMode;
                    if (i3 == 32) {
                        float f = (rect.bottom + rect.top) / 2.0f;
                        if (ViewUtils.isLayoutRtl(ArrowPopupView.this)) {
                            path.moveTo(rect.left, rect.top);
                            path.quadTo(rect.right + rect.width(), f, rect.left, rect.bottom);
                        } else {
                            path.moveTo(rect.right, rect.top);
                            path.quadTo(-rect.width(), f, rect.right, rect.bottom);
                        }
                        path.close();
                    } else if (i3 != 64) {
                        switch (i3) {
                            case 8:
                                int i4 = rect.right;
                                path.moveTo(rect.left, rect.bottom);
                                path.quadTo((i4 + r0) / 2.0f, -rect.height(), rect.right, rect.bottom);
                                path.close();
                                break;
                            case 9:
                                path.moveTo(0.0f, ArrowPopupView.this.mArrowTop.getIntrinsicHeight());
                                if (ViewUtils.isLayoutRtl(ArrowPopupView.this)) {
                                    path.quadTo(rect.right, (-ArrowPopupView.this.mArrowTop.getIntrinsicHeight()) * 0.7f, rect.right, ArrowPopupView.this.mArrowTop.getIntrinsicHeight());
                                } else {
                                    path.quadTo(0.0f, (-ArrowPopupView.this.mArrowTop.getIntrinsicHeight()) * 0.7f, rect.right, ArrowPopupView.this.mArrowTop.getIntrinsicHeight());
                                }
                                path.close();
                                break;
                            case 10:
                                path.moveTo(0.0f, ArrowPopupView.this.mArrowTop.getIntrinsicHeight());
                                if (ViewUtils.isLayoutRtl(ArrowPopupView.this)) {
                                    path.quadTo(0.0f, (-ArrowPopupView.this.mArrowTop.getIntrinsicHeight()) * 0.7f, rect.right, ArrowPopupView.this.mArrowTop.getIntrinsicHeight());
                                } else {
                                    path.quadTo(rect.right, (-ArrowPopupView.this.mArrowTop.getIntrinsicHeight()) * 0.7f, rect.right, ArrowPopupView.this.mArrowTop.getIntrinsicHeight());
                                }
                                path.close();
                                break;
                        }
                    } else {
                        float f2 = (rect.bottom + rect.top) / 2.0f;
                        if (ViewUtils.isLayoutRtl(ArrowPopupView.this)) {
                            path.moveTo(rect.right, rect.top);
                            path.quadTo(-rect.width(), f2, rect.right, rect.bottom);
                        } else {
                            path.moveTo(rect.left, rect.top);
                            path.quadTo(rect.right + rect.width(), f2, rect.left, rect.bottom);
                        }
                        path.close();
                    }
                    if (path.isConvex()) {
                        outline.setConvexPath(path);
                        return;
                    }
                    Log.d("ArrowPopupView", "outline path is not convex");
                    outline.setOval(rect);
                }
            });
            addShadow(this.mContentFrameWrapper, new ViewOutlineProvider() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.4
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    if (view.getWidth() == 0 || view.getHeight() == 0) {
                        return;
                    }
                    Rect rect = new Rect(0, 0, view.getWidth(), view.getHeight());
                    rect.bottom -= view.getPaddingBottom();
                    rect.top += view.getPaddingTop();
                    rect.right -= view.getPaddingRight();
                    rect.left += view.getPaddingLeft();
                    outline.setRoundRect(rect, ArrowPopupView.this.getContext().getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_arrow_popup_view_round_corners));
                }
            });
        }
    }

    public void animateToDismiss() {
        if (this.mIsDismissing) {
            return;
        }
        AnimatorSet animatorSet = this.mAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimationSet animationSet = this.mAnimationSet;
        if (animationSet != null) {
            animationSet.cancel();
        }
        this.mAnimationSet = new AnimationSet(true);
        float[] fArr = new float[2];
        getAnimationPivot(fArr);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.6f, 1.0f, 0.6f, 0, fArr[0], 0, fArr[1]);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        if (DeviceHelper.isFeatureWholeAnim()) {
            scaleAnimation.setDuration(150L);
            alphaAnimation.setDuration(150L);
        } else {
            this.mAnimationSet.setDuration(0L);
        }
        this.mAnimationSet.addAnimation(scaleAnimation);
        this.mAnimationSet.addAnimation(alphaAnimation);
        this.mAnimationSet.setAnimationListener(this.mHideAnimatorListener);
        this.mAnimationSet.setInterpolator(new AccelerateInterpolator(2.0f));
        startAnimation(this.mAnimationSet);
    }

    public void animateToShow() {
        invalidate();
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: miuix.popupwidget.internal.widget.ArrowPopupView.5
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                ArrowPopupView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                if (ArrowPopupView.this.mAnimator != null) {
                    ArrowPopupView.this.mAnimator.cancel();
                }
                if (ArrowPopupView.this.mAnimationSet != null) {
                    ArrowPopupView.this.mAnimationSet.cancel();
                }
                ArrowPopupView.this.mAnimationSet = new AnimationSet(true);
                float[] fArr = new float[2];
                ArrowPopupView.this.getAnimationPivot(fArr);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.6f, 1.0f, 0.6f, 1.0f, 0, fArr[0], 0, fArr[1]);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                if (DeviceHelper.isFeatureWholeAnim()) {
                    alphaAnimation.setDuration(100L);
                    scaleAnimation.setDuration(280L);
                } else {
                    ArrowPopupView.this.mAnimationSet.setDuration(0L);
                }
                ArrowPopupView.this.mAnimationSet.addAnimation(scaleAnimation);
                ArrowPopupView.this.mAnimationSet.addAnimation(alphaAnimation);
                ArrowPopupView.this.mAnimationSet.setAnimationListener(ArrowPopupView.this.mShowAnimationListener);
                ArrowPopupView.this.mAnimationSet.setInterpolator(new DecelerateInterpolator(1.5f));
                ArrowPopupView arrowPopupView = ArrowPopupView.this;
                arrowPopupView.startAnimation(arrowPopupView.mAnimationSet);
                return true;
            }
        });
    }

    public void enableShowingAnimation(boolean z) {
        this.mShowingAnimation = z;
    }

    public int getArrowMode() {
        return this.mArrowMode;
    }

    public View getContentView() {
        if (this.mContentFrame.getChildCount() > 0) {
            return this.mContentFrame.getChildAt(0);
        }
        return null;
    }

    public AppCompatButton getNegativeButton() {
        return this.mNegativeButton;
    }

    public AppCompatButton getPositiveButton() {
        return this.mPositiveButton;
    }

    @Deprecated
    public float getRollingPercent() {
        return 1.0f;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        int right;
        float f;
        int measuredWidth;
        int i2;
        if (this.mBackground != null) {
            return;
        }
        int width = this.mSpaceLeft + (this.mContentFrameWrapper.getWidth() / 2);
        int height = this.mSpaceTop + (this.mContentFrameWrapper.getHeight() / 2);
        int i3 = this.mArrowMode;
        if (i3 != 8) {
            if (i3 == 16) {
                f = 180.0f;
                measuredWidth = this.mArrowSpaceLeft + (this.mArrow.getMeasuredWidth() / 2);
                i = this.mContentFrameWrapper.getRight() - measuredWidth;
                i2 = this.mSpaceLeft;
            } else if (i3 == 32) {
                f = -90.0f;
                measuredWidth = this.mArrowSpaceTop + (this.mArrow.getMeasuredHeight() / 2);
                i = this.mContentFrameWrapper.getBottom() - measuredWidth;
                i2 = this.mSpaceTop;
            } else if (i3 != 64) {
                f = 0.0f;
                i = 0;
                right = 0;
            } else {
                f = 90.0f;
                int measuredHeight = this.mArrowSpaceTop + (this.mArrow.getMeasuredHeight() / 2);
                i = measuredHeight - this.mSpaceTop;
                right = this.mContentFrameWrapper.getBottom() - measuredHeight;
            }
            right = measuredWidth - i2;
        } else {
            int measuredWidth2 = this.mArrowSpaceLeft + (this.mArrow.getMeasuredWidth() / 2);
            i = measuredWidth2 - this.mSpaceLeft;
            right = this.mContentFrameWrapper.getRight() - measuredWidth2;
            f = 0.0f;
        }
        int save = canvas.save();
        canvas.rotate(f, width, height);
        int i4 = this.mArrowMode;
        if (i4 == 8 || i4 == 16) {
            canvas.translate(this.mSpaceLeft, this.mSpaceTop);
            this.mBackgroundLeft.setBounds(0, 0, i, this.mContentFrameWrapper.getHeight());
            canvas.translate(0.0f, isTopMode() ? this.mTranslationValue : -this.mTranslationValue);
            this.mBackgroundLeft.draw(canvas);
            canvas.translate(i, 0.0f);
            this.mBackgroundRight.setBounds(0, 0, right, this.mContentFrameWrapper.getHeight());
            this.mBackgroundRight.draw(canvas);
        } else if (i4 == 32 || i4 == 64) {
            canvas.translate(width - (this.mContentFrameWrapper.getHeight() / 2), height - (this.mContentFrameWrapper.getWidth() / 2));
            this.mBackgroundLeft.setBounds(0, 0, i, this.mContentFrameWrapper.getWidth());
            canvas.translate(0.0f, isLeftMode() ? this.mTranslationValue : -this.mTranslationValue);
            this.mBackgroundLeft.draw(canvas);
            canvas.translate(i, 0.0f);
            this.mBackgroundRight.setBounds(0, 0, right, this.mContentFrameWrapper.getWidth());
            this.mBackgroundRight.draw(canvas);
        }
        canvas.restoreToCount(save);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mArrow = (AppCompatImageView) findViewById(R$id.popup_arrow);
        this.mContentFrame = (FrameLayout) findViewById(16908290);
        LinearLayout linearLayout = (LinearLayout) findViewById(R$id.content_wrapper);
        this.mContentFrameWrapper = linearLayout;
        linearLayout.setBackground(this.mBackground);
        this.mContentFrameWrapper.setMinimumHeight(getContext().getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_arrow_popup_view_min_height));
        if (this.mBackgroundLeft != null && this.mBackgroundRight != null) {
            Rect rect = new Rect();
            this.mBackgroundLeft.getPadding(rect);
            LinearLayout linearLayout2 = this.mContentFrameWrapper;
            int i = rect.top;
            linearLayout2.setPadding(i, i, i, i);
        }
        LinearLayout linearLayout3 = (LinearLayout) findViewById(R$id.title_layout);
        this.mTitleLayout = linearLayout3;
        linearLayout3.setBackground(this.mTitleBackground);
        this.mTitleText = (AppCompatTextView) findViewById(16908310);
        this.mPositiveButton = (AppCompatButton) findViewById(16908314);
        this.mNegativeButton = (AppCompatButton) findViewById(16908313);
        this.mPositiveClickListener = new WrapperOnClickListener();
        this.mNegativeClickListener = new WrapperOnClickListener();
        this.mPositiveButton.setOnClickListener(this.mPositiveClickListener);
        this.mNegativeButton.setOnClickListener(this.mNegativeClickListener);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (!this.mAnchor.isAttachedToWindow()) {
            if (this.mArrowPopupWindow.isShowing()) {
                this.mArrowPopupWindow.dismiss();
                return;
            }
            return;
        }
        if (this.mArrowMode == 0) {
            adjustArrowMode();
        }
        updateArrowDrawable(this.mArrowMode);
        arrowLayout();
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        Rect rect = this.mTmpRect;
        this.mContentFrameWrapper.getHitRect(rect);
        if (motionEvent.getAction() != 0 || rect.contains(x, y)) {
            View.OnTouchListener onTouchListener = this.mTouchInterceptor;
            return onTouchListener != null && onTouchListener.onTouch(view, motionEvent);
        }
        this.mArrowPopupWindow.dismiss(true);
        return true;
    }

    public void setAnchor(View view) {
        this.mAnchor = view;
    }

    public void setArrowMode(int i) {
        this.mArrowMode = i;
        updateArrowDrawable(i);
    }

    public void setArrowPopupWindow(ArrowPopupWindow arrowPopupWindow) {
        this.mArrowPopupWindow = arrowPopupWindow;
    }

    public void setAutoDismiss(boolean z) {
        this.mAutoDismiss = z;
    }

    public void setContentView(int i) {
        setContentView(LayoutInflater.from(getContext()).inflate(i, (ViewGroup) null));
    }

    public void setContentView(View view) {
        setContentView(view, new ViewGroup.LayoutParams(-2, -2));
    }

    public void setContentView(View view, ViewGroup.LayoutParams layoutParams) {
        this.mContentFrame.removeAllViews();
        if (view != null) {
            this.mContentFrame.addView(view, layoutParams);
        }
    }

    public void setOffset(int i, int i2) {
        this.mOffsetX = i;
        this.mOffsetY = i2;
    }

    @Deprecated
    public void setRollingPercent(float f) {
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitleLayout.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        this.mTitleText.setText(charSequence);
    }

    public void setTouchInterceptor(View.OnTouchListener onTouchListener) {
        this.mTouchInterceptor = onTouchListener;
    }
}
