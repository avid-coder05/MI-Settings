package miuix.popupwidget.internal.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import miuix.popupwidget.R$attr;
import miuix.popupwidget.R$id;
import miuix.popupwidget.R$style;
import miuix.popupwidget.R$styleable;
import miuix.popupwidget.widget.GuidePopupWindow;

/* loaded from: classes5.dex */
public class GuidePopupView extends FrameLayout implements View.OnTouchListener {
    private View mAnchor;
    private int mAnchorHeight;
    private int mAnchorLocationX;
    private int mAnchorLocationY;
    private int mAnchorWidth;
    private ObjectAnimator mAnimator;
    private int mArrowMode;
    private int mColorBackground;
    private Context mContext;
    private int mDefaultOffset;
    private GuidePopupWindow mGuidePopupWindow;
    private Animator.AnimatorListener mHideAnimatorListener;
    private boolean mIsDismissing;
    private boolean mIsMirrored;
    private float mLineLength;
    private int mMinBorder;
    private LinearLayout mMirroredTextGroup;
    private int mOffsetX;
    private int mOffsetY;
    private final Paint mPaint;
    private Animator.AnimatorListener mShowAnimatorListener;
    private float mStartPointRadius;
    private float mTextCircleRadius;
    private ColorStateList mTextColor;
    private LinearLayout mTextGroup;
    private int mTextSize;
    private View.OnTouchListener mTouchInterceptor;
    private boolean mUseDefaultOffset;

    public GuidePopupView(Context context) {
        this(context, null);
    }

    public GuidePopupView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.guidePopupViewStyle);
    }

    public GuidePopupView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mUseDefaultOffset = true;
        this.mTextColor = null;
        Paint paint = new Paint();
        this.mPaint = paint;
        this.mShowAnimatorListener = new AnimatorListenerAdapter() { // from class: miuix.popupwidget.internal.widget.GuidePopupView.1
            private boolean mCancel;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.mCancel = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (this.mCancel) {
                    return;
                }
                GuidePopupView.this.mAnimator = null;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                this.mCancel = false;
            }
        };
        this.mHideAnimatorListener = new AnimatorListenerAdapter() { // from class: miuix.popupwidget.internal.widget.GuidePopupView.2
            private boolean mCancel;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.mCancel = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (this.mCancel) {
                    return;
                }
                GuidePopupView.this.mIsDismissing = false;
                GuidePopupView.this.mAnimator = null;
                GuidePopupView.this.mGuidePopupWindow.dismiss();
                GuidePopupView.this.setArrowMode(0);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                this.mCancel = false;
                GuidePopupView.this.mIsDismissing = true;
            }
        };
        this.mArrowMode = -1;
        this.mContext = context;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.GuidePopupView, i, R$style.Widget_GuidePopupView_DayNight);
        this.mStartPointRadius = obtainStyledAttributes.getDimension(R$styleable.GuidePopupView_startPointRadius, 0.0f);
        this.mLineLength = obtainStyledAttributes.getDimension(R$styleable.GuidePopupView_lineLength, 0.0f);
        this.mTextCircleRadius = obtainStyledAttributes.getDimension(R$styleable.GuidePopupView_textCircleRadius, 0.0f);
        this.mColorBackground = obtainStyledAttributes.getColor(R$styleable.GuidePopupView_android_colorBackground, 0);
        paint.setColor(obtainStyledAttributes.getColor(R$styleable.GuidePopupView_paintColor, -1));
        this.mTextSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.GuidePopupView_android_textSize, 15);
        this.mTextColor = obtainStyledAttributes.getColorStateList(R$styleable.GuidePopupView_android_textColor);
        obtainStyledAttributes.recycle();
        this.mMinBorder = (int) (this.mLineLength + (this.mTextCircleRadius * 2.5f));
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x005a, code lost:
    
        if ((r1 - r4) < r3) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x006a, code lost:
    
        if ((r1 - r4) < r2) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0079, code lost:
    
        if ((r0 - r6) < r3) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0089, code lost:
    
        if ((r0 - r6) < r2) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x008b, code lost:
    
        r2 = 5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void adjustArrowMode() {
        /*
            r14 = this;
            int r0 = r14.getWidth()
            int r1 = r14.getHeight()
            r2 = 4
            int[] r3 = new int[r2]
            int r4 = r14.mAnchorLocationY
            r5 = 0
            r3[r5] = r4
            int r6 = r1 - r4
            int r7 = r14.mAnchorHeight
            int r6 = r6 - r7
            r8 = 1
            r3[r8] = r6
            int r6 = r14.mAnchorLocationX
            r9 = 2
            r3[r9] = r6
            int r10 = r0 - r6
            int r11 = r14.mAnchorWidth
            int r10 = r10 - r11
            r12 = 3
            r3[r12] = r10
            int r11 = r11 / r9
            int r6 = r6 + r11
            int r7 = r7 / r9
            int r4 = r4 + r7
            r7 = -2147483648(0xffffffff80000000, float:-0.0)
            r10 = r7
            r7 = r5
        L2d:
            if (r5 >= r2) goto L41
            r11 = r3[r5]
            int r13 = r14.mMinBorder
            if (r11 < r13) goto L36
            goto L42
        L36:
            r11 = r3[r5]
            if (r11 <= r10) goto L3e
            r7 = r3[r5]
            r10 = r7
            r7 = r5
        L3e:
            int r5 = r5 + 1
            goto L2d
        L41:
            r5 = r7
        L42:
            r3 = 5
            r7 = 6
            r10 = 7
            if (r5 == 0) goto L7c
            if (r5 == r8) goto L6d
            if (r5 == r9) goto L5d
            if (r5 == r12) goto L4e
            goto L8d
        L4e:
            float r0 = (float) r4
            float r3 = r14.mTextCircleRadius
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 >= 0) goto L56
            goto L8e
        L56:
            int r1 = r1 - r4
            float r0 = (float) r1
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 >= 0) goto L8d
            goto L83
        L5d:
            float r0 = (float) r4
            float r2 = r14.mTextCircleRadius
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L66
        L64:
            r2 = r7
            goto L8e
        L66:
            int r1 = r1 - r4
            float r0 = (float) r1
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L8d
            goto L8b
        L6d:
            float r1 = (float) r6
            float r3 = r14.mTextCircleRadius
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 >= 0) goto L75
            goto L8e
        L75:
            int r0 = r0 - r6
            float r0 = (float) r0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 >= 0) goto L8d
            goto L64
        L7c:
            float r1 = (float) r6
            float r2 = r14.mTextCircleRadius
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 >= 0) goto L85
        L83:
            r2 = r10
            goto L8e
        L85:
            int r0 = r0 - r6
            float r0 = (float) r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L8d
        L8b:
            r2 = r3
            goto L8e
        L8d:
            r2 = r5
        L8e:
            r14.setArrowMode(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.popupwidget.internal.widget.GuidePopupView.adjustArrowMode():void");
    }

    private void arrowLayout() {
        caculateDefaultOffset();
        drawText(this.mArrowMode, this.mTextGroup, this.mOffsetX, this.mOffsetY);
        if (this.mIsMirrored) {
            drawText(getMirroredMode(), this.mMirroredTextGroup, -this.mOffsetX, -this.mOffsetY);
        }
    }

    private void caculateDefaultOffset() {
        if (!this.mUseDefaultOffset) {
            this.mDefaultOffset = 0;
            return;
        }
        int i = this.mAnchorWidth / 2;
        int i2 = this.mAnchorHeight / 2;
        int sqrt = (int) Math.sqrt(Math.pow(i, 2.0d) + Math.pow(i2, 2.0d));
        int i3 = this.mArrowMode;
        if (i3 == 0 || i3 == 1) {
            this.mDefaultOffset = i2;
        } else if (i3 == 2 || i3 == 3) {
            this.mDefaultOffset = i;
        } else {
            this.mDefaultOffset = sqrt;
        }
    }

    private void drawPopup(Canvas canvas, int i, int i2, int i3) {
        float f;
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.FILL);
        float f2 = this.mAnchorLocationX + (this.mAnchorWidth / 2) + i2;
        float f3 = this.mAnchorLocationY + (this.mAnchorHeight / 2) + i3;
        switch (i) {
            case 0:
                f = 180.0f;
                break;
            case 1:
            default:
                f = 0.0f;
                break;
            case 2:
                f = 90.0f;
                break;
            case 3:
                f = -90.0f;
                break;
            case 4:
                f = -45.0f;
                break;
            case 5:
                f = 135.0f;
                break;
            case 6:
                f = 45.0f;
                break;
            case 7:
                f = -135.0f;
                break;
        }
        canvas.save();
        canvas.rotate(f, f2, f3);
        canvas.translate(0.0f, this.mDefaultOffset);
        int save = canvas.save();
        canvas.clipRect(f2 - 2.0f, f3, f2 + 2.0f, f3 + this.mStartPointRadius, Region.Op.DIFFERENCE);
        canvas.drawCircle(f2, f3, this.mStartPointRadius, this.mPaint);
        canvas.restoreToCount(save);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(4.0f);
        canvas.drawLine(f2, f3, f2, f3 + this.mLineLength, this.mPaint);
        float f4 = f3 + this.mLineLength + this.mTextCircleRadius;
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(4.0f);
        canvas.drawCircle(f2, f4, this.mTextCircleRadius, this.mPaint);
        canvas.restore();
    }

    private void drawText(int i, LinearLayout linearLayout, int i2, int i3) {
        float f;
        int measuredHeight;
        int i4;
        int measuredHeight2;
        float f2 = this.mDefaultOffset + this.mLineLength + this.mTextCircleRadius;
        int i5 = this.mAnchorLocationX + (this.mAnchorWidth / 2);
        int i6 = this.mAnchorLocationY + (this.mAnchorHeight / 2);
        int i7 = 0;
        switch (i) {
            case 0:
            case 5:
            case 7:
                i7 = i5 - (linearLayout.getMeasuredWidth() / 2);
                f = i6 - f2;
                measuredHeight = linearLayout.getMeasuredHeight() / 2;
                i4 = (int) (f - measuredHeight);
                break;
            case 1:
            case 4:
            case 6:
                i7 = i5 - (linearLayout.getMeasuredWidth() / 2);
                f = i6 + f2;
                measuredHeight = linearLayout.getMeasuredHeight() / 2;
                i4 = (int) (f - measuredHeight);
                break;
            case 2:
                i7 = (int) ((i5 - f2) - (linearLayout.getMeasuredWidth() / 2));
                measuredHeight2 = linearLayout.getMeasuredHeight() / 2;
                i4 = i6 - measuredHeight2;
                break;
            case 3:
                i7 = (int) ((i5 + f2) - (linearLayout.getMeasuredWidth() / 2));
                measuredHeight2 = linearLayout.getMeasuredHeight() / 2;
                i4 = i6 - measuredHeight2;
                break;
            default:
                i4 = 0;
                break;
        }
        int sin = (int) (f2 * Math.sin(0.7853981633974483d));
        int i8 = (int) (f2 - sin);
        if (i != 4) {
            if (i == 5) {
                i7 -= sin;
            } else if (i != 6) {
                if (i == 7) {
                    i7 += sin;
                }
                int i9 = i7 + i2;
                int i10 = i4 + i3;
                linearLayout.layout(i9, i10, linearLayout.getMeasuredWidth() + i9, linearLayout.getMeasuredHeight() + i10);
            } else {
                i7 -= sin;
            }
            i4 += i8;
            int i92 = i7 + i2;
            int i102 = i4 + i3;
            linearLayout.layout(i92, i102, linearLayout.getMeasuredWidth() + i92, linearLayout.getMeasuredHeight() + i102);
        }
        i7 += sin;
        i4 -= i8;
        int i922 = i7 + i2;
        int i1022 = i4 + i3;
        linearLayout.layout(i922, i1022, linearLayout.getMeasuredWidth() + i922, linearLayout.getMeasuredHeight() + i1022);
    }

    private int getMirroredMode() {
        int i = this.mArrowMode;
        if (i == -1) {
            return -1;
        }
        return i % 2 == 0 ? i + 1 : i - 1;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.translate(this.mAnchorLocationX, this.mAnchorLocationY);
        this.mAnchor.setDrawingCacheEnabled(true);
        this.mAnchor.buildDrawingCache();
        canvas.drawBitmap(this.mAnchor.getDrawingCache(), 0.0f, 0.0f, (Paint) null);
        this.mAnchor.setDrawingCacheEnabled(false);
        canvas.restore();
        drawPopup(canvas, this.mArrowMode, this.mOffsetX, this.mOffsetY);
        if (this.mIsMirrored) {
            drawPopup(canvas, getMirroredMode(), -this.mOffsetX, -this.mOffsetY);
        }
    }

    public int getArrowMode() {
        return this.mArrowMode;
    }

    public int getColorBackground() {
        return this.mColorBackground;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTextGroup = (LinearLayout) findViewById(R$id.text_group);
        this.mMirroredTextGroup = (LinearLayout) findViewById(R$id.mirrored_text_group);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mAnchorWidth == 0 || this.mAnchorHeight == 0) {
            setAnchor(this.mAnchor);
        }
        this.mTextCircleRadius = (float) Math.max(Math.sqrt(Math.pow(this.mTextGroup.getMeasuredWidth(), 2.0d) + Math.pow(this.mTextGroup.getMeasuredHeight(), 2.0d)) / 2.0d, this.mTextCircleRadius);
        if (this.mIsMirrored) {
            this.mTextCircleRadius = (float) Math.max(Math.sqrt(Math.pow(this.mMirroredTextGroup.getMeasuredWidth(), 2.0d) + Math.pow(this.mMirroredTextGroup.getMeasuredHeight(), 2.0d)) / 2.0d, this.mTextCircleRadius);
        }
        if (this.mArrowMode == -1) {
            adjustArrowMode();
        } else {
            arrowLayout();
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int i = this.mAnchorLocationX;
        Rect rect = new Rect(i, this.mAnchorLocationY, this.mAnchor.getWidth() + i, this.mAnchorLocationY + this.mAnchor.getHeight());
        if (motionEvent.getAction() == 0 && rect.contains(x, y)) {
            this.mAnchor.callOnClick();
            return true;
        }
        this.mGuidePopupWindow.dismiss(true);
        return true;
    }

    public void setAnchor(View view) {
        this.mAnchor = view;
        this.mAnchorWidth = view.getWidth();
        this.mAnchorHeight = this.mAnchor.getHeight();
        int[] iArr = new int[2];
        this.mAnchor.getLocationInWindow(iArr);
        this.mAnchorLocationX = iArr[0];
        this.mAnchorLocationY = iArr[1];
    }

    public void setArrowMode(int i) {
        this.mArrowMode = i;
    }

    public void setGuidePopupWindow(GuidePopupWindow guidePopupWindow) {
        this.mGuidePopupWindow = guidePopupWindow;
    }

    public void setTouchInterceptor(View.OnTouchListener onTouchListener) {
        this.mTouchInterceptor = onTouchListener;
    }
}
