package com.android.settings.fuelgauge;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageView;
import com.android.settings.R;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.Utils;
import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class BatteryChartView extends AppCompatImageView implements View.OnClickListener, AccessibilityManager.AccessibilityStateChangeListener {
    private static final List<String> ACCESSIBILITY_SERVICE_NAMES = Arrays.asList("SwitchAccessService", "TalkBackService", "JustSpeakService");
    private static final int DIVIDER_COLOR = Color.parseColor("#CDCCC5");
    private int mDividerHeight;
    private Paint mDividerPaint;
    private int mDividerWidth;
    Handler mHandler;
    private final Rect mIndent;
    private boolean mIsSlotsClickabled;
    private int[] mLevels;
    private OnSelectListener mOnSelectListener;
    private final Rect[] mPercentageBounds;
    private String[] mPercentages;
    int mSelectedIndex;
    private int mTextPadding;
    private Paint mTextPaint;
    String[] mTimestamps;
    private final Rect[] mTimestampsBounds;
    private MotionEvent mTouchUpEvent;
    private int mTrapezoidColor;
    private int mTrapezoidCount;
    Paint mTrapezoidCurvePaint;
    private float mTrapezoidHOffset;
    private Paint mTrapezoidPaint;
    private TrapezoidSlot[] mTrapezoidSlots;
    private int mTrapezoidSolidColor;
    private float mTrapezoidVOffset;
    final Runnable mUpdateClickableStateRun;

    /* loaded from: classes.dex */
    public interface OnSelectListener {
        void onSelect(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class TrapezoidSlot {
        public float mLeft;
        public float mRight;

        private TrapezoidSlot() {
        }

        public String toString() {
            return String.format(Locale.US, "TrapezoidSlot[%f,%f]", Float.valueOf(this.mLeft), Float.valueOf(this.mRight));
        }
    }

    public BatteryChartView(Context context) {
        super(context, null);
        this.mPercentages = getPercentages();
        this.mIndent = new Rect();
        this.mPercentageBounds = new Rect[]{new Rect(), new Rect(), new Rect()};
        this.mTimestampsBounds = new Rect[]{new Rect(), new Rect(), new Rect(), new Rect(), new Rect()};
        this.mHandler = new Handler();
        this.mUpdateClickableStateRun = new Runnable() { // from class: com.android.settings.fuelgauge.BatteryChartView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BatteryChartView.this.lambda$new$0();
            }
        };
        this.mTrapezoidCurvePaint = null;
    }

    public BatteryChartView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPercentages = getPercentages();
        this.mIndent = new Rect();
        this.mPercentageBounds = new Rect[]{new Rect(), new Rect(), new Rect()};
        this.mTimestampsBounds = new Rect[]{new Rect(), new Rect(), new Rect(), new Rect(), new Rect()};
        this.mHandler = new Handler();
        this.mUpdateClickableStateRun = new Runnable() { // from class: com.android.settings.fuelgauge.BatteryChartView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BatteryChartView.this.lambda$new$0();
            }
        };
        this.mTrapezoidCurvePaint = null;
        initializeColors(context);
        setOnClickListener(this);
        setSelectedIndex(-1);
        setTrapezoidCount(12);
        setClickable(false);
        setLatestTimestamp(0L);
    }

    private void drawHorizontalDividers(Canvas canvas) {
        int width = getWidth() - this.mIndent.right;
        int height = getHeight();
        Rect rect = this.mIndent;
        int i = rect.top;
        int i2 = (height - i) - rect.bottom;
        float f = i + (this.mDividerWidth * 0.5f);
        float f2 = width;
        canvas.drawLine(0.0f, f, f2, f, this.mDividerPaint);
        drawPercentage(canvas, 0, f);
        int i3 = this.mDividerWidth;
        float f3 = this.mIndent.top + i3 + ((((i2 - (i3 * 2)) - this.mTrapezoidVOffset) - this.mDividerHeight) * 0.5f);
        canvas.drawLine(0.0f, f3, f2, f3, this.mDividerPaint);
        drawPercentage(canvas, 1, f3);
        float f4 = this.mIndent.top + ((i2 - this.mDividerHeight) - (this.mDividerWidth * 0.5f));
        canvas.drawLine(0.0f, f4, f2, f4, this.mDividerPaint);
        drawPercentage(canvas, 2, f4);
    }

    private void drawPercentage(Canvas canvas, int i, float f) {
        if (this.mTextPaint != null) {
            String str = this.mPercentages[i];
            int width = getWidth() - this.mPercentageBounds[i].width();
            Rect[] rectArr = this.mPercentageBounds;
            canvas.drawText(str, width - rectArr[i].left, f + (rectArr[i].height() * 0.5f), this.mTextPaint);
        }
    }

    private void drawTimestamp(Canvas canvas, float[] fArr) {
        canvas.drawText(this.mTimestamps[0], fArr[0] - this.mTimestampsBounds[0].left, getTimestampY(0), this.mTextPaint);
        canvas.drawText(this.mTimestamps[4], (fArr[4] - this.mTimestampsBounds[4].width()) - this.mTimestampsBounds[4].left, getTimestampY(4), this.mTextPaint);
        for (int i = 1; i <= 3; i++) {
            canvas.drawText(this.mTimestamps[i], fArr[i] - ((this.mTimestampsBounds[i].width() - this.mTimestampsBounds[i].left) * 0.5f), getTimestampY(i), this.mTextPaint);
        }
    }

    private void drawTrapezoids(Canvas canvas) {
        int i;
        if (this.mLevels == null) {
            return;
        }
        int height = (getHeight() - this.mIndent.bottom) - this.mDividerHeight;
        int i2 = this.mDividerWidth;
        float f = (height - i2) - this.mTrapezoidVOffset;
        float f2 = ((f - (i2 * 0.5f)) - r1.top) / 100.0f;
        Path path = new Path();
        int i3 = 0;
        Path path2 = null;
        while (i3 < this.mTrapezoidCount) {
            if (isValidToDraw(i3)) {
                if (this.mIsSlotsClickabled) {
                    int i4 = this.mSelectedIndex;
                    i = (i4 == i3 || i4 == -1) ? this.mTrapezoidSolidColor : this.mTrapezoidColor;
                } else {
                    i = this.mTrapezoidColor;
                }
                this.mTrapezoidPaint.setColor(i);
                float round = Math.round(f - (this.mLevels[i3] * f2));
                float round2 = Math.round(f - (this.mLevels[i3 + 1] * f2));
                path.reset();
                path.moveTo(this.mTrapezoidSlots[i3].mLeft, f);
                path.lineTo(this.mTrapezoidSlots[i3].mLeft, round);
                path.lineTo(this.mTrapezoidSlots[i3].mRight, round2);
                path.lineTo(this.mTrapezoidSlots[i3].mRight, f);
                path.lineTo(this.mTrapezoidSlots[i3].mLeft, f);
                path.lineTo(this.mTrapezoidSlots[i3].mLeft, round);
                canvas.drawPath(path, this.mTrapezoidPaint);
                if (this.mTrapezoidCurvePaint != null) {
                    if (path2 == null) {
                        path2 = new Path();
                        path2.moveTo(this.mTrapezoidSlots[i3].mLeft, round);
                    } else {
                        path2.lineTo(this.mTrapezoidSlots[i3].mLeft, round);
                    }
                    path2.lineTo(this.mTrapezoidSlots[i3].mRight, round2);
                }
            } else {
                Paint paint = this.mTrapezoidCurvePaint;
                if (paint != null && path2 != null) {
                    canvas.drawPath(path2, paint);
                    path2 = null;
                }
            }
            i3++;
        }
        Paint paint2 = this.mTrapezoidCurvePaint;
        if (paint2 == null || path2 == null) {
            return;
        }
        canvas.drawPath(path2, paint2);
    }

    private void drawVerticalDividers(Canvas canvas) {
        int width = getWidth() - this.mIndent.right;
        int i = this.mTrapezoidCount;
        int i2 = i + 1;
        float f = (width - (this.mDividerWidth * i2)) / i;
        float height = getHeight() - this.mIndent.bottom;
        float f2 = height - this.mDividerHeight;
        float f3 = this.mTrapezoidHOffset;
        int i3 = this.mDividerWidth;
        float f4 = f3 + (i3 * 0.5f);
        float f5 = i3 * 0.5f;
        int i4 = 0;
        while (i4 < i2) {
            canvas.drawLine(f5, f2, f5, height, this.mDividerPaint);
            float f6 = this.mDividerWidth + f5 + f;
            TrapezoidSlot[] trapezoidSlotArr = this.mTrapezoidSlots;
            if (i4 < trapezoidSlotArr.length) {
                trapezoidSlotArr[i4].mLeft = Math.round(f5 + f4);
                this.mTrapezoidSlots[i4].mRight = Math.round(f6 - f4);
            }
            i4++;
            f5 = f6;
        }
        if (this.mTimestamps != null) {
            float[] fArr = new float[5];
            int i5 = this.mDividerWidth;
            float f7 = i5 * 0.5f;
            float f8 = i5 + f;
            for (int i6 = 0; i6 < 5; i6++) {
                fArr[i6] = (i6 * f8 * 3.0f) + f7;
            }
            drawTimestamp(canvas, fArr);
        }
    }

    private static String[] getPercentages() {
        return new String[]{Utils.formatPercentage(100.0d, true), Utils.formatPercentage(50.0d, true), Utils.formatPercentage(0.0d, true)};
    }

    private int getTimestampY(int i) {
        return (getHeight() - this.mTimestampsBounds[i].height()) + this.mTimestampsBounds[i].height() + this.mTimestampsBounds[i].top + Math.round(this.mTextPadding * 1.5f);
    }

    private int getTrapezoidIndex(float f) {
        int i = 0;
        while (true) {
            TrapezoidSlot[] trapezoidSlotArr = this.mTrapezoidSlots;
            if (i >= trapezoidSlotArr.length) {
                return -2;
            }
            TrapezoidSlot trapezoidSlot = trapezoidSlotArr[i];
            float f2 = trapezoidSlot.mLeft;
            float f3 = this.mTrapezoidHOffset;
            if (f >= f2 - f3 && f <= trapezoidSlot.mRight + f3) {
                return i;
            }
            i++;
        }
    }

    private void initializeColors(Context context) {
        setBackgroundColor(0);
        int colorAccentDefaultColor = Utils.getColorAccentDefaultColor(context);
        this.mTrapezoidSolidColor = colorAccentDefaultColor;
        this.mTrapezoidColor = Utils.getDisabled(context, colorAccentDefaultColor);
        Resources resources = getContext().getResources();
        this.mDividerWidth = resources.getDimensionPixelSize(R.dimen.chartview_divider_width);
        this.mDividerHeight = resources.getDimensionPixelSize(R.dimen.chartview_divider_height);
        Paint paint = new Paint();
        this.mDividerPaint = paint;
        paint.setAntiAlias(true);
        this.mDividerPaint.setColor(DIVIDER_COLOR);
        this.mDividerPaint.setStyle(Paint.Style.STROKE);
        this.mDividerPaint.setStrokeWidth(this.mDividerWidth);
        Log.i("BatteryChartView", "mDividerWidth:" + this.mDividerWidth);
        Log.i("BatteryChartView", "mDividerHeight:" + this.mDividerHeight);
        this.mTrapezoidHOffset = resources.getDimension(R.dimen.chartview_trapezoid_margin_start);
        this.mTrapezoidVOffset = resources.getDimension(R.dimen.chartview_trapezoid_margin_bottom);
        Paint paint2 = new Paint();
        this.mTrapezoidPaint = paint2;
        paint2.setAntiAlias(true);
        this.mTrapezoidPaint.setColor(this.mTrapezoidSolidColor);
        this.mTrapezoidPaint.setStyle(Paint.Style.FILL);
        this.mTrapezoidPaint.setPathEffect(new CornerPathEffect(resources.getDimensionPixelSize(R.dimen.chartview_trapezoid_radius)));
        this.mTextPadding = resources.getDimensionPixelSize(R.dimen.chartview_text_padding);
    }

    static boolean isAccessibilityEnabled(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        if (accessibilityManager.isEnabled()) {
            for (AccessibilityServiceInfo accessibilityServiceInfo : accessibilityManager.getEnabledAccessibilityServiceList(17)) {
                for (String str : ACCESSIBILITY_SERVICE_NAMES) {
                    String id = accessibilityServiceInfo.getId();
                    if (id != null && id.contains(str)) {
                        Log.d("BatteryChartView", "acccessibilityEnabled:" + id);
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private boolean isValidToDraw(int i) {
        int[] iArr = this.mLevels;
        return (iArr == null || i < 0 || i >= iArr.length - 1 || iArr[i] == 0 || iArr[i + 1] == 0) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateClickableState  reason: merged with bridge method [inline-methods] */
    public void lambda$new$0() {
        Context context = ((ImageView) this).mContext;
        this.mIsSlotsClickabled = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context).isChartGraphSlotsEnabled(context) && !isAccessibilityEnabled(context);
        Log.d("BatteryChartView", "isChartGraphSlotsEnabled:" + this.mIsSlotsClickabled);
        setClickable(isClickable());
        boolean z = this.mIsSlotsClickabled;
        if (!z && this.mTrapezoidCurvePaint == null) {
            Paint paint = new Paint();
            this.mTrapezoidCurvePaint = paint;
            paint.setAntiAlias(true);
            this.mTrapezoidCurvePaint.setColor(this.mTrapezoidSolidColor);
            this.mTrapezoidCurvePaint.setStyle(Paint.Style.STROKE);
            this.mTrapezoidCurvePaint.setStrokeWidth(this.mDividerWidth * 2);
        } else if (z) {
            this.mTrapezoidCurvePaint = null;
            setLevels(this.mLevels);
        }
        invalidate();
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawHorizontalDividers(canvas);
        drawVerticalDividers(canvas);
        drawTrapezoids(canvas);
    }

    @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
    public void onAccessibilityStateChanged(boolean z) {
        Log.d("BatteryChartView", "onAccessibilityStateChanged:" + z);
        this.mHandler.removeCallbacks(this.mUpdateClickableStateRun);
        this.mHandler.postDelayed(this.mUpdateClickableStateRun, 500L);
    }

    @Override // android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        lambda$new$0();
        ((AccessibilityManager) ((ImageView) this).mContext.getSystemService(AccessibilityManager.class)).addAccessibilityStateChangeListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        MotionEvent motionEvent = this.mTouchUpEvent;
        if (motionEvent == null) {
            Log.w("BatteryChartView", "invalid motion event for onClick() callback");
            return;
        }
        int trapezoidIndex = getTrapezoidIndex(motionEvent.getX());
        if (trapezoidIndex == -2 || !isValidToDraw(trapezoidIndex)) {
            return;
        }
        if (trapezoidIndex == this.mSelectedIndex) {
            setSelectedIndex(-1);
        } else {
            setSelectedIndex(trapezoidIndex);
        }
        view.performHapticFeedback(6);
    }

    @Override // android.widget.ImageView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((AccessibilityManager) ((ImageView) this).mContext.getSystemService(AccessibilityManager.class)).removeAccessibilityStateChangeListener(this);
        this.mHandler.removeCallbacks(this.mUpdateClickableStateRun);
    }

    @Override // android.widget.ImageView, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mTextPaint == null) {
            this.mIndent.set(0, 0, 0, 0);
            return;
        }
        int i3 = 0;
        while (true) {
            String[] strArr = this.mPercentages;
            if (i3 >= strArr.length) {
                break;
            }
            this.mTextPaint.getTextBounds(strArr[i3], 0, strArr[i3].length(), this.mPercentageBounds[i3]);
            i3++;
        }
        this.mIndent.top = this.mPercentageBounds[0].height();
        this.mIndent.right = this.mPercentageBounds[0].width() + this.mTextPadding;
        if (this.mTimestamps != null) {
            int i4 = 0;
            for (int i5 = 0; i5 < 5; i5++) {
                Paint paint = this.mTextPaint;
                String[] strArr2 = this.mTimestamps;
                paint.getTextBounds(strArr2[i5], 0, strArr2[i5].length(), this.mTimestampsBounds[i5]);
                i4 = Math.max(i4, this.mTimestampsBounds[i5].height());
            }
            this.mIndent.bottom = i4 + Math.round(this.mTextPadding * 1.5f);
        }
        Log.d("BatteryChartView", "setIndent:" + this.mPercentageBounds[0]);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 1) {
            this.mTouchUpEvent = MotionEvent.obtain(motionEvent);
        } else if (action == 3) {
            this.mTouchUpEvent = null;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public void setClickable(boolean z) {
        super.setClickable(this.mIsSlotsClickabled && z);
    }

    void setClickableForce(boolean z) {
        super.setClickable(z);
    }

    public void setCompanionTextView(TextView textView) {
        if (textView != null) {
            textView.draw(new Canvas());
            this.mTextPaint = textView.getPaint();
        } else {
            this.mTextPaint = null;
        }
        setVisibility(0);
        requestLayout();
    }

    public void setLatestTimestamp(long j) {
        if (j == 0) {
            j = Clock.systemUTC().millis();
        }
        if (this.mTimestamps == null) {
            this.mTimestamps = new String[5];
        }
        boolean is24HourFormat = DateFormat.is24HourFormat(getContext());
        for (int i = 0; i < 5; i++) {
            this.mTimestamps[i] = ConvertUtils.utcToLocalTimeHour(getContext(), j - ((4 - i) * 21600000), is24HourFormat);
        }
        requestLayout();
    }

    public void setLevels(int[] iArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("setLevels() ");
        sb.append(iArr == null ? "null" : Integer.valueOf(iArr.length));
        Log.d("BatteryChartView", sb.toString());
        if (iArr == null) {
            this.mLevels = null;
            return;
        }
        if (iArr.length != this.mTrapezoidCount + 1) {
            iArr = null;
        }
        this.mLevels = iArr;
        int i = 0;
        setClickable(false);
        invalidate();
        if (this.mLevels == null) {
            return;
        }
        while (true) {
            int[] iArr2 = this.mLevels;
            if (i >= iArr2.length - 1) {
                return;
            }
            if (iArr2[i] != 0 && iArr2[i + 1] != 0) {
                setClickable(true);
                return;
            }
            i++;
        }
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.mOnSelectListener = onSelectListener;
    }

    public void setSelectedIndex(int i) {
        if (this.mSelectedIndex != i) {
            this.mSelectedIndex = i;
            invalidate();
            OnSelectListener onSelectListener = this.mOnSelectListener;
            if (onSelectListener != null) {
                onSelectListener.onSelect(this.mSelectedIndex);
            }
        }
    }

    public void setTrapezoidCount(int i) {
        Log.i("BatteryChartView", "trapezoidCount:" + i);
        this.mTrapezoidCount = i;
        this.mTrapezoidSlots = new TrapezoidSlot[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.mTrapezoidSlots[i2] = new TrapezoidSlot();
        }
        invalidate();
    }
}
