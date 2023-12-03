package com.android.settings.usagestats.widget.render;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.usagestats.utils.AppInfoUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import miui.provider.ExtraTelephony;

/* loaded from: classes2.dex */
public abstract class BaseViewRender implements Runnable {
    private boolean canDoClick;
    private float coordTextBaseLine;
    protected SimpleDateFormat dateFormat;
    private boolean doCancelTip;
    private float downX;
    private float downY;
    private float firstDownX;
    private float firstDownY;
    private boolean isRtl;
    private boolean isShowTipRect;
    protected float mBarMaxHeight;
    protected Paint mBarPaint;
    protected RectF mBarRect;
    protected List<RectF> mBarRects;
    protected int mBarType;
    protected float mBarWidth;
    protected Context mContext;
    protected Paint mCoordTextPaint;
    protected float mCoordTextSize;
    protected int mDataSize;
    protected Paint mExplainPaint;
    protected float mHavingLineWidth;
    protected int mHeight;
    private Paint mLinePaint;
    protected float mMaxYValue;
    protected int mPaddingBottom;
    protected int mPaddingEnd;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected int mPaddingStart;
    protected int mPaddingTop;
    private ValueAnimator mTipCancelAnimator;
    private PointF mTipLineStart;
    protected RectF mTipRect;
    protected float mTipRectHeight;
    protected Paint mTipRectPaint;
    protected float mTipRectWidth;
    private ValueAnimator mTipShowAnimator;
    protected float mTipTextMargin;
    protected Paint mTipTextPaint;
    private float mTipTextStart;
    protected String mTipTile;
    protected float mTipTileTextSize;
    protected String mTipValue;
    protected float mTipValueTextSize;
    protected View mView;
    protected int mWidth;
    protected float mXInterval;
    public static final String TAG = BaseViewRender.class.getSimpleName();
    public static final SparseIntArray WEEKS = new SparseIntArray() { // from class: com.android.settings.usagestats.widget.render.BaseViewRender.1
        {
            put(2, R.string.usage_state_monday);
            put(3, R.string.usage_state_tuesday);
            put(4, R.string.usage_state_wednesday);
            put(5, R.string.usage_state_thursday);
            put(6, R.string.usage_state_friday);
            put(7, R.string.usage_state_saturday);
            put(1, R.string.usage_state_sunday);
        }
    };
    public static final SparseArray<String> MOUTHS = new SparseArray<String>() { // from class: com.android.settings.usagestats.widget.render.BaseViewRender.2
        {
            put(0, "1");
            put(1, "2");
            put(2, ExtraTelephony.Phonelist.TYPE_VIP);
            put(3, ExtraTelephony.Phonelist.TYPE_CLOUDS_BLACK);
            put(4, ExtraTelephony.Phonelist.TYPE_CLOUDS_WHITE);
            put(5, ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_BLACK);
            put(6, ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_WHITE);
            put(7, "8");
            put(8, "9");
            put(9, "10");
            put(10, "11");
            put(11, "12");
        }
    };
    private Handler mHandler = new Handler();
    protected boolean isWeekData = false;
    protected float mSpace = 8.0f;

    public BaseViewRender(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private void cancelAnimator(ValueAnimator valueAnimator) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    private void cancelTipRectAnimator() {
        if (this.mTipCancelAnimator == null) {
            ValueAnimator ofInt = ValueAnimator.ofInt(254, 0);
            this.mTipCancelAnimator = ofInt;
            ofInt.setInterpolator(new LinearInterpolator());
            this.mTipCancelAnimator.setDuration(400L);
            this.mTipCancelAnimator.addListener(new SimpleAnimatorListener() { // from class: com.android.settings.usagestats.widget.render.BaseViewRender.5
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    BaseViewRender.this.cancelTipRectDirect();
                }
            });
            this.mTipCancelAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.usagestats.widget.render.BaseViewRender.6
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BaseViewRender.this.doDrawTipRect(((Integer) valueAnimator.getAnimatedValue()).intValue());
                }
            });
        }
        this.mTipCancelAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelTipRectDirect() {
        this.isShowTipRect = false;
        this.mTipRectWidth = getTipRectWidth();
        invalidate();
    }

    private void doClickAction() {
        if (this.mDataSize != this.mBarRects.size()) {
            calculateOthers();
            invalidate();
        }
        float f = this.downX;
        float f2 = f - (this.mBarWidth / 2.0f);
        float f3 = this.mXInterval;
        int i = (int) (f2 / f3);
        if (Math.abs(f - (i * f3)) >= this.mXInterval / 2.0f) {
            i++;
        }
        int i2 = this.mDataSize;
        if (i >= i2 - 1) {
            i = i2 - 1;
        }
        if (i <= 0) {
            i = 0;
        }
        try {
            RectF rectF = this.mBarRects.get(i);
            if (rectF == null || rectF.height() <= 0.0f) {
                return;
            }
            this.isShowTipRect = true;
            getTipTitle(i);
            getTipValue(i);
            getTipRect(rectF);
            showTipRect();
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "doClickAction: ", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doDrawTipRect(int i) {
        this.mTipRectPaint.setAlpha(i);
        this.mTipTextPaint.setAlpha(i);
        invalidate();
    }

    private void drawBar(Canvas canvas) {
        this.mBarRects.clear();
        float f = 0.0f;
        for (int i = 0; i < this.mDataSize; i++) {
            this.mCoordTextPaint.setTextAlign(getCoordTextAlign(i));
            this.mBarPaint.setColor(getBarColor(i));
            this.mBarRect = new RectF(f, getBarTop(i), this.mBarWidth + f, this.mHeight + 100);
            canvas.save();
            canvas.clipRect(new RectF(f, getBarTop(i), this.mBarWidth + f, this.mHeight));
            RectF rectF = this.mBarRect;
            float f2 = this.mBarWidth;
            canvas.drawRoundRect(rectF, f2, f2, this.mBarPaint);
            canvas.restore();
            this.mCoordTextPaint.setColor(getCoordTextColor(i));
            canvas.drawText(getBarCoordText(i), getCoordTextXPoint(i, f), this.coordTextBaseLine, this.mCoordTextPaint);
            drawOthers(canvas, i, isRtl() ? (this.mBarRect.width() / 2.0f) + f + (this.mXInterval / 2.0f) : ((this.mBarRect.width() / 2.0f) + f) - (this.mXInterval / 2.0f));
            this.mBarRects.add(this.mBarRect);
            f += this.mXInterval;
        }
    }

    private void drawLine(Canvas canvas) {
        float f = this.mHeight;
        canvas.drawLine(0.0f, f, this.mWidth, f, this.mLinePaint);
        float f2 = this.mHeight - (this.mBarMaxHeight / 2.0f);
        canvas.drawLine(0.0f, f2, this.mWidth, f2, this.mLinePaint);
        float f3 = this.mHeight - this.mBarMaxHeight;
        canvas.drawLine(0.0f, f3, this.mWidth, f3, this.mLinePaint);
    }

    private void drawRectTip(Canvas canvas) {
        RectF rectF = this.mTipRect;
        if (rectF != null) {
            canvas.drawRoundRect(rectF, 10.0f, 10.0f, this.mTipRectPaint);
            PointF pointF = this.mTipLineStart;
            float f = pointF.x;
            canvas.drawLine(f, pointF.y, f, this.mTipRect.bottom, this.mTipRectPaint);
            this.mTipTextPaint.setColor(getColor(R.color.usage_stats_show_tips_title_text_color));
            this.mTipTextPaint.setTextSize(this.mTipTileTextSize);
            Paint paint = this.mTipTextPaint;
            float textBaseLine = AppInfoUtils.getTextBaseLine(paint, (AppInfoUtils.getTextHeight(paint) / 2.0f) + getDimenValue(R.dimen.usage_state_tip_title_margin_top));
            canvas.drawText(this.mTipTile, this.mTipTextStart, textBaseLine, this.mTipTextPaint);
            this.mTipTextPaint.setColor(getColor(R.color.usage_stats_show_tips_value_text_color));
            this.mTipTextPaint.setTextSize(this.mTipValueTextSize);
            canvas.drawText(this.mTipValue, this.mTipTextStart, AppInfoUtils.getTextBaseLine(this.mTipTextPaint, textBaseLine + MiuiUtils.dp2px(this.mContext.getApplicationContext(), 1.09f) + (AppInfoUtils.getTextHeight(this.mTipTextPaint) / 2.0f)), this.mTipTextPaint);
        }
    }

    private void getTipRect(RectF rectF) {
        if (this.mTipLineStart == null) {
            this.mTipLineStart = new PointF();
        }
        PointF pointF = this.mTipLineStart;
        float width = rectF.right - (rectF.width() / 2.0f);
        pointF.x = width;
        this.mTipLineStart.y = rectF.top;
        resetTipRectWidth();
        float f = this.mTipRectWidth;
        float f2 = (f / 2.0f) + width;
        float f3 = width - (f / 2.0f);
        int i = this.mWidth;
        if (f2 > i) {
            f2 = i;
            f3 = f2 - f;
        }
        if (f3 < 0.0f) {
            f2 = f + 0.0f;
            f3 = 0.0f;
        }
        if (this.mTipRect == null) {
            this.mTipRect = new RectF(0.0f, 0.0f, 0.0f, this.mTipRectHeight);
        }
        RectF rectF2 = this.mTipRect;
        rectF2.left = f3;
        rectF2.right = f2;
        if (isRtl()) {
            this.mTipTextStart = f2 - getDimenValue(R.dimen.usage_state_tip_left_margin);
            this.mTipTextPaint.setTextAlign(Paint.Align.RIGHT);
            return;
        }
        this.mTipTextStart = f3 + getDimenValue(R.dimen.usage_state_tip_left_margin);
        this.mTipTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    private void invalidate() {
        View view = this.mView;
        if (view != null) {
            view.invalidate();
        }
    }

    private void releaseAnimator(ValueAnimator valueAnimator) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.removeAllListeners();
        }
    }

    private void resetTipRectWidth() {
        this.mTipTextPaint.setTextSize(this.mTipTileTextSize);
        float textWidth = getTextWidth(this.mTipTile, this.mTipTextPaint);
        this.mTipTextPaint.setTextSize(this.mTipValueTextSize);
        float max = Math.max(getTextWidth(this.mTipValue, this.mTipTextPaint), textWidth);
        float f = this.mTipRectWidth;
        int i = R.dimen.usage_state_tip_left_margin;
        if (f - getDimenValue(i) < max) {
            this.mTipRectWidth = max + (getDimenValue(i) * 2.0f);
        }
    }

    private void showTipRect() {
        if (this.mTipShowAnimator == null) {
            ValueAnimator ofInt = ValueAnimator.ofInt(0, 255);
            this.mTipShowAnimator = ofInt;
            ofInt.setDuration(400L);
            this.mTipShowAnimator.setInterpolator(new LinearInterpolator());
            this.mTipShowAnimator.addListener(new SimpleAnimatorListener() { // from class: com.android.settings.usagestats.widget.render.BaseViewRender.3
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    BaseViewRender.this.doCancelTip = true;
                    BaseViewRender.this.mHandler.postDelayed(BaseViewRender.this, 1500L);
                }
            });
            this.mTipShowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.usagestats.widget.render.BaseViewRender.4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BaseViewRender.this.doDrawTipRect(((Integer) valueAnimator.getAnimatedValue()).intValue());
                }
            });
        }
        this.mTipShowAnimator.start();
    }

    public void calculateOthers() {
        this.mBarWidth = getDimenValue(this.isWeekData ? R.dimen.usage_state_week_bar_width : R.dimen.usage_state_bar_width);
        this.mDataSize = getDataSize();
        this.mMaxYValue = (float) getMaxYValue();
        this.mBarMaxHeight = getMyBarMaxHeight();
        this.mXInterval = ((this.mWidth - this.mBarWidth) - this.mSpace) / ((float) (this.mDataSize - 1));
    }

    public void draw(Canvas canvas) {
        drawExplain(canvas);
        drawLine(canvas);
        drawBar(canvas);
        if (this.isShowTipRect) {
            drawRectTip(canvas);
        }
    }

    protected abstract void drawExplain(Canvas canvas);

    protected void drawOthers(Canvas canvas, int i, float f) {
    }

    protected abstract int getBarColor(int i);

    protected abstract String getBarCoordText(int i);

    protected abstract float getBarTop(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public int getColor(int i) {
        return this.mContext.getResources().getColor(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Paint.Align getCoordTextAlign(int i) {
        return i == 0 ? Paint.Align.LEFT : Paint.Align.CENTER;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getCoordTextColor(int i) {
        return getColor(R.color.usage_stats_black35);
    }

    protected float getCoordTextXPoint(int i, float f) {
        if (i == this.mDataSize - 1) {
            return this.mWidth;
        }
        if (i == 0) {
            return 0.0f;
        }
        return (this.mBarWidth / 2.0f) + f;
    }

    protected abstract int getDataSize();

    /* JADX INFO: Access modifiers changed from: protected */
    public float getDimenValue(int i) {
        return this.mContext.getResources().getDimension(i);
    }

    protected abstract long getMaxYValue();

    protected abstract float getMyBarMaxHeight();

    /* JADX INFO: Access modifiers changed from: protected */
    public String getString(int i) {
        return this.mContext.getResources().getString(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getString(int i, Object... objArr) {
        return this.mContext.getString(i, objArr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getTextWidth(String str, Paint paint) {
        return paint.measureText(str);
    }

    protected abstract float getTipRectWidth();

    protected abstract void getTipTitle(int i);

    protected abstract void getTipValue(int i);

    public void init() {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        this.dateFormat = simpleDateFormat;
        simpleDateFormat.applyPattern(getString(R.string.usage_state_date));
        this.isRtl = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
        this.mBarRects = new ArrayList();
        this.mHavingLineWidth = getDimenValue(R.dimen.usage_state_having_line);
        this.mCoordTextSize = getDimenValue(R.dimen.usage_state_coord_text_size);
        this.mTipTileTextSize = getDimenValue(R.dimen.usage_state_show_tip_title_text_size);
        this.mTipValueTextSize = getDimenValue(R.dimen.usage_state_show_tip_value_text_size);
        this.mTipRectWidth = getTipRectWidth();
        this.mTipRectHeight = getDimenValue(R.dimen.usage_state_show_tip_height);
        this.mTipTextMargin = getDimenValue(R.dimen.usage_state_tip_text_margin);
        this.mExplainPaint = new Paint();
        Paint paint = new Paint();
        this.mBarPaint = paint;
        paint.setAntiAlias(true);
        Paint paint2 = new Paint(1);
        this.mCoordTextPaint = paint2;
        paint2.setTextSize(this.mCoordTextSize);
        Paint paint3 = new Paint(1);
        this.mLinePaint = paint3;
        paint3.setColor(getColor(R.color.usage_stats_bar_divide_line));
        this.mLinePaint.setStrokeWidth(this.mHavingLineWidth);
        Paint paint4 = new Paint(1);
        this.mTipRectPaint = paint4;
        paint4.setStyle(Paint.Style.FILL);
        this.mTipRectPaint.setColor(getColor(R.color.usage_stats_show_tips_bg));
        Paint paint5 = new Paint(1);
        this.mTipTextPaint = paint5;
        paint5.setTextAlign(Paint.Align.LEFT);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isRtl() {
        return this.isRtl;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.canDoClick = true;
            this.doCancelTip = false;
            this.mHandler.removeCallbacks(this);
            cancelTipRectDirect();
            cancelAnimator(this.mTipShowAnimator);
            cancelAnimator(this.mTipCancelAnimator);
            float x = motionEvent.getX();
            this.downX = x;
            this.firstDownX = x;
            float y = motionEvent.getY();
            this.downY = y;
            this.firstDownY = y;
        } else if (action == 1) {
            float x2 = motionEvent.getX();
            float y2 = motionEvent.getY();
            if ((Math.abs(x2 - this.downX) < 25.0f || Math.abs(y2 - this.downY) < 25.0f) && this.canDoClick) {
                doClickAction();
            }
        } else if (action == 2) {
            float x3 = motionEvent.getX();
            float y3 = motionEvent.getY();
            if (Math.abs(x3 - this.firstDownX) > 25.0f || Math.abs(y3 - this.firstDownY) > 25.0f) {
                Log.d(TAG, "onTouchEvent: move cancel");
                this.canDoClick = false;
            }
        }
        return true;
    }

    public void release() {
        releaseAnimator(this.mTipCancelAnimator);
        releaseAnimator(this.mTipShowAnimator);
        this.mView = null;
        this.mHandler.removeCallbacksAndMessages(null);
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.doCancelTip) {
            cancelTipRectAnimator();
        }
    }

    public void setMyView(View view) {
        this.mView = view;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setTalkBackDescription(CharSequence charSequence) {
        View view = this.mView;
        if (view != null) {
            view.setContentDescription(charSequence);
        }
    }

    public void setWeekData(boolean z) {
        if (this.isWeekData != z) {
            this.isShowTipRect = false;
        }
        this.isWeekData = z;
        calculateOthers();
    }

    public void setmBarType(int i) {
        this.mBarType = i;
    }

    public void setmHeight(int i) {
        this.mHeight = i;
        float dimenValue = getDimenValue(R.dimen.usage_state_coord_text_size);
        this.coordTextBaseLine = AppInfoUtils.getTextBaseLine(this.mCoordTextPaint, i - (AppInfoUtils.getTextHeight(dimenValue) / 2.0f));
        this.mHeight = (int) (this.mHeight - (AppInfoUtils.getTextHeight(dimenValue) + MiuiUtils.dp2px(this.mContext, 5.45f)));
    }

    public void setmPaddingBottom(int i) {
        this.mPaddingBottom = i;
    }

    public void setmPaddingEnd(int i) {
        this.mPaddingEnd = i;
    }

    public void setmPaddingLeft(int i) {
        this.mPaddingLeft = i;
    }

    public void setmPaddingRight(int i) {
        this.mPaddingRight = i;
    }

    public void setmPaddingStart(int i) {
        this.mPaddingStart = i;
    }

    public void setmPaddingTop(int i) {
        this.mPaddingTop = i;
    }

    public void setmWidth(int i) {
        this.mWidth = i;
    }
}
