package miuix.androidbasewidget.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import miui.provider.Recordings;
import miui.vip.VipService;
import miuix.androidbasewidget.R$attr;
import miuix.androidbasewidget.R$color;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.ViewUtils;

/* loaded from: classes5.dex */
public class CircleProgressBar extends ProgressBar {
    private Path mArcPath;
    private RectF mArcRect;
    private Bitmap mBitmapForSoftLayer;
    private Canvas mCanvasForSoftLayer;
    private Animator mChangeProgressAnimator;
    private int mCurrentLevel;
    private Drawable[] mLevelsBackDrawable;
    private Drawable[] mLevelsForeDrawable;
    private Drawable[] mLevelsMiddleDrawable;
    private Paint mPaint;
    private int mPrevAlpha;
    private int mPrevLevel;
    private OnProgressChangedListener mProgressChangedListener;
    private int[] mProgressLevels;
    private int mRotateVelocity;
    private Drawable mThumb;

    /* loaded from: classes5.dex */
    public interface OnProgressChangedListener {
        void onProgressChanged();
    }

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mArcPath = new Path();
        this.mRotateVelocity = 300;
        setIndeterminate(false);
        int resolveColor = AttributeResolver.resolveColor(context, R$attr.circleProgressBarColor, context.getResources().getColor(ViewUtils.isNightMode(context) ? R$color.miuix_appcompat_progressbar_circle_color_dark : R$color.miuix_appcompat_progressbar_circle_color_light));
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(resolveColor);
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(this, new AnimConfig[0]);
    }

    private int calcDuration(int i) {
        return (i * VipService.VIP_SERVICE_FAILURE) / this.mRotateVelocity;
    }

    private void drawLayer(Canvas canvas, Drawable drawable, Drawable drawable2, Drawable drawable3, float f, int i) {
        if (drawable != null) {
            drawable.setAlpha(i);
            drawable.draw(canvas);
        }
        if (canvas.isHardwareAccelerated()) {
            canvas.saveLayer(drawable3.getBounds().left, drawable3.getBounds().top, drawable3.getBounds().right, drawable3.getBounds().bottom, null, 31);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth(drawable3.getBounds().width());
            this.mArcPath.reset();
            this.mArcPath.addArc(this.mArcRect, -90.0f, f * 360.0f);
            canvas.drawPath(this.mArcPath, this.mPaint);
            this.mPaint.setStyle(Paint.Style.FILL);
            this.mPaint.setStrokeWidth(0.0f);
            drawable3.setAlpha(i);
            drawable3.draw(canvas);
            canvas.restore();
        } else {
            if (this.mBitmapForSoftLayer == null) {
                this.mBitmapForSoftLayer = Bitmap.createBitmap(drawable3.getBounds().width(), drawable3.getBounds().height(), Bitmap.Config.ARGB_8888);
                this.mCanvasForSoftLayer = new Canvas(this.mBitmapForSoftLayer);
            }
            this.mBitmapForSoftLayer.eraseColor(0);
            this.mCanvasForSoftLayer.save();
            this.mCanvasForSoftLayer.translate(-drawable3.getBounds().left, -drawable3.getBounds().top);
            this.mCanvasForSoftLayer.drawArc(this.mArcRect, -90.0f, f * 360.0f, true, this.mPaint);
            drawable3.setAlpha(i);
            drawable3.draw(this.mCanvasForSoftLayer);
            this.mCanvasForSoftLayer.restore();
            canvas.drawBitmap(this.mBitmapForSoftLayer, drawable3.getBounds().left, drawable3.getBounds().top, (Paint) null);
        }
        Drawable drawable4 = this.mThumb;
        if (drawable4 != null) {
            canvas.save();
            int width = ((getWidth() - getPaddingLeft()) - getPaddingRight()) / 2;
            int height = ((getHeight() - getPaddingTop()) - getPaddingBottom()) / 2;
            int intrinsicWidth = drawable4.getIntrinsicWidth();
            int intrinsicHeight = drawable4.getIntrinsicHeight();
            canvas.rotate((getProgress() * 360.0f) / getMax(), width, height);
            int i2 = intrinsicWidth / 2;
            int i3 = intrinsicHeight / 2;
            drawable4.setBounds(width - i2, height - i3, width + i2, height + i3);
            drawable4.draw(canvas);
            canvas.restore();
        }
        if (drawable2 != null) {
            drawable2.setAlpha(i);
            drawable2.draw(canvas);
        }
    }

    private Drawable getBackDrawable(int i) {
        Drawable[] drawableArr = this.mLevelsBackDrawable;
        if (drawableArr == null) {
            return null;
        }
        return drawableArr[i];
    }

    private Drawable getForeDrawable(int i) {
        Drawable[] drawableArr = this.mLevelsForeDrawable;
        if (drawableArr == null) {
            return null;
        }
        return drawableArr[i];
    }

    private int getIntrinsicHeight() {
        int intrinsicHeight = getMiddleDrawable(0).getIntrinsicHeight();
        Drawable[] drawableArr = this.mLevelsForeDrawable;
        if (drawableArr != null) {
            intrinsicHeight = Math.max(intrinsicHeight, drawableArr[0].getIntrinsicHeight());
        }
        Drawable[] drawableArr2 = this.mLevelsBackDrawable;
        return drawableArr2 != null ? Math.max(intrinsicHeight, drawableArr2[0].getIntrinsicHeight()) : intrinsicHeight;
    }

    private int getIntrinsicWidth() {
        int intrinsicWidth = getMiddleDrawable(0).getIntrinsicWidth();
        Drawable[] drawableArr = this.mLevelsForeDrawable;
        if (drawableArr != null) {
            intrinsicWidth = Math.max(intrinsicWidth, drawableArr[0].getIntrinsicWidth());
        }
        Drawable[] drawableArr2 = this.mLevelsBackDrawable;
        return drawableArr2 != null ? Math.max(intrinsicWidth, drawableArr2[0].getIntrinsicWidth()) : intrinsicWidth;
    }

    private Drawable getMiddleDrawable(int i) {
        Drawable[] drawableArr = this.mLevelsMiddleDrawable;
        if (drawableArr == null) {
            return null;
        }
        return drawableArr[i];
    }

    private float getRate() {
        return getProgress() / getMax();
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int progressLevelCount = getProgressLevelCount();
        for (int i = 0; i < progressLevelCount; i++) {
            Drawable[] drawableArr = this.mLevelsBackDrawable;
            if (drawableArr != null) {
                drawableArr[i].setState(getDrawableState());
            }
            Drawable[] drawableArr2 = this.mLevelsMiddleDrawable;
            if (drawableArr2 != null) {
                drawableArr2[i].setState(getDrawableState());
            }
            Drawable[] drawableArr3 = this.mLevelsForeDrawable;
            if (drawableArr3 != null) {
                drawableArr3[i].setState(getDrawableState());
            }
        }
        invalidate();
    }

    public int getPrevAlpha() {
        return this.mPrevAlpha;
    }

    public int getProgressLevelCount() {
        int[] iArr = this.mProgressLevels;
        if (iArr == null) {
            return 1;
        }
        return 1 + iArr.length;
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected synchronized void onDraw(Canvas canvas) {
        drawLayer(canvas, getBackDrawable(this.mCurrentLevel), getForeDrawable(this.mCurrentLevel), getMiddleDrawable(this.mCurrentLevel), getRate(), 255 - this.mPrevAlpha);
        if (this.mPrevAlpha >= 10) {
            drawLayer(canvas, getBackDrawable(this.mPrevLevel), getForeDrawable(this.mPrevLevel), getMiddleDrawable(this.mPrevLevel), getRate(), this.mPrevAlpha);
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected synchronized void onMeasure(int i, int i2) {
        setMeasuredDimension(getIntrinsicWidth(), getIntrinsicHeight());
    }

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        this.mProgressChangedListener = onProgressChangedListener;
    }

    public void setPrevAlpha(int i) {
        this.mPrevAlpha = i;
        invalidate();
    }

    @Override // android.widget.ProgressBar
    public synchronized void setProgress(int i) {
        int length;
        super.setProgress(i);
        int[] iArr = this.mProgressLevels;
        if (iArr == null) {
            length = 0;
        } else {
            length = iArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    i2 = -1;
                    break;
                } else if (i < this.mProgressLevels[i2]) {
                    break;
                } else {
                    i2++;
                }
            }
            if (i2 != -1) {
                length = i2;
            }
        }
        int i3 = this.mCurrentLevel;
        if (length != i3) {
            this.mPrevLevel = i3;
            this.mCurrentLevel = length;
            setPrevAlpha(255);
            ObjectAnimator ofInt = ObjectAnimator.ofInt(this, "prevAlpha", 0);
            ofInt.setDuration(300L);
            ofInt.setInterpolator(new LinearInterpolator());
            ofInt.start();
        }
        OnProgressChangedListener onProgressChangedListener = this.mProgressChangedListener;
        if (onProgressChangedListener != null) {
            onProgressChangedListener.onProgressChanged();
        }
    }

    public void setProgressByAnimator(int i) {
        setProgressByAnimator(i, null);
    }

    public void setProgressByAnimator(int i, Animator.AnimatorListener animatorListener) {
        stopProgressAnimator();
        int abs = Math.abs((int) (((i - getProgress()) / getMax()) * 360.0f));
        ObjectAnimator ofInt = ObjectAnimator.ofInt(this, Recordings.Downloads.Columns.PROGRESS, i);
        this.mChangeProgressAnimator = ofInt;
        ofInt.setDuration(calcDuration(abs));
        this.mChangeProgressAnimator.setInterpolator(getInterpolator());
        if (animatorListener != null) {
            this.mChangeProgressAnimator.addListener(animatorListener);
        }
        this.mChangeProgressAnimator.start();
    }

    public void setProgressLevels(int[] iArr) {
        this.mProgressLevels = iArr;
    }

    public void setRotateVelocity(int i) {
        this.mRotateVelocity = i;
    }

    public void setThumb(int i) {
        setThumb(getResources().getDrawable(i));
    }

    public void setThumb(Drawable drawable) {
        this.mThumb = drawable;
    }

    public void stopProgressAnimator() {
        Animator animator = this.mChangeProgressAnimator;
        if (animator == null || !animator.isRunning()) {
            return;
        }
        this.mChangeProgressAnimator.cancel();
    }
}
