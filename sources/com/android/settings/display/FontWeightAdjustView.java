package com.android.settings.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;
import com.android.settings.R;
import com.android.settingslib.util.HapticUtil;
import com.android.settingslib.util.ToastUtil;
import java.util.Locale;
import miuix.androidbasewidget.widget.SeekBar;

/* loaded from: classes.dex */
public class FontWeightAdjustView extends SeekBar {
    final boolean isPrimaryUser;
    private int mBigPointCenterColor;
    private int mBigPointColor;
    private float mBigPointsRadius;
    private HapticUtil mHapticUtil;
    private FontWeightChangeListener mListener;
    private Paint mPointPaint;
    private float mPointsRadius;
    private float mPointsX;
    private float mPointsY;
    private final SeekBar.OnSeekBarChangeListener mSeekListener;
    private int mSmallPointColor;
    final int myUserId;

    /* loaded from: classes.dex */
    public interface FontWeightChangeListener {
        void onWeightChange(int i);
    }

    public FontWeightAdjustView(Context context) {
        super(context);
        int myUserId = UserHandle.myUserId();
        this.myUserId = myUserId;
        this.isPrimaryUser = myUserId == 0;
        this.mSeekListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.android.settings.display.FontWeightAdjustView.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(android.widget.SeekBar seekBar, int i, boolean z) {
                LargeFontUtils.setFontWeight(FontWeightAdjustView.this.getContext(), i);
                FontWeightAdjustView fontWeightAdjustView = FontWeightAdjustView.this;
                fontWeightAdjustView.mPointsX = fontWeightAdjustView.getPointX();
                FontWeightAdjustView.this.invalidate();
                if (FontWeightAdjustView.this.mListener != null) {
                    FontWeightAdjustView.this.mListener.onWeightChange(i);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            }
        };
        init(null, 0);
    }

    public FontWeightAdjustView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        int myUserId = UserHandle.myUserId();
        this.myUserId = myUserId;
        this.isPrimaryUser = myUserId == 0;
        this.mSeekListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.android.settings.display.FontWeightAdjustView.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(android.widget.SeekBar seekBar, int i, boolean z) {
                LargeFontUtils.setFontWeight(FontWeightAdjustView.this.getContext(), i);
                FontWeightAdjustView fontWeightAdjustView = FontWeightAdjustView.this;
                fontWeightAdjustView.mPointsX = fontWeightAdjustView.getPointX();
                FontWeightAdjustView.this.invalidate();
                if (FontWeightAdjustView.this.mListener != null) {
                    FontWeightAdjustView.this.mListener.onWeightChange(i);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            }
        };
        init(attributeSet, 0);
    }

    public FontWeightAdjustView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int myUserId = UserHandle.myUserId();
        this.myUserId = myUserId;
        this.isPrimaryUser = myUserId == 0;
        this.mSeekListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.android.settings.display.FontWeightAdjustView.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(android.widget.SeekBar seekBar, int i2, boolean z) {
                LargeFontUtils.setFontWeight(FontWeightAdjustView.this.getContext(), i2);
                FontWeightAdjustView fontWeightAdjustView = FontWeightAdjustView.this;
                fontWeightAdjustView.mPointsX = fontWeightAdjustView.getPointX();
                FontWeightAdjustView.this.invalidate();
                if (FontWeightAdjustView.this.mListener != null) {
                    FontWeightAdjustView.this.mListener.onWeightChange(i2);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            }
        };
        init(attributeSet, i);
    }

    private void ensurePerformHapticFeedback(int i) {
        if (i == 0 || i == 1) {
            this.mHapticUtil.performHapticFeedback();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:16:0x003c  */
    /* JADX WARN: Removed duplicated region for block: B:18:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public float getPointX() {
        /*
            r4 = this;
            android.content.Context r0 = r4.getContext()
            int r0 = com.android.settings.display.LargeFontUtils.getFontWeight(r0)
            r4.setProgress(r0)
            float r1 = r4.mBigPointsRadius
            int r2 = r4.getWidth()
            float r2 = (float) r2
            float r3 = r4.mBigPointsRadius
            float r2 = r2 - r3
            r3 = 100
            if (r0 != r3) goto L1b
        L19:
            r1 = r2
            goto L36
        L1b:
            if (r0 != 0) goto L1e
            goto L36
        L1e:
            float r0 = (float) r0
            r3 = 1065353216(0x3f800000, float:1.0)
            float r0 = r0 * r3
            int r3 = r4.getWidth()
            float r3 = (float) r3
            float r0 = r0 * r3
            r3 = 1120403456(0x42c80000, float:100.0)
            float r0 = r0 / r3
            int r3 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r3 <= 0) goto L30
            goto L19
        L30:
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 >= 0) goto L35
            goto L36
        L35:
            r1 = r0
        L36:
            boolean r0 = r4.isRtl()
            if (r0 == 0) goto L43
            int r4 = r4.getWidth()
            float r4 = (float) r4
            float r1 = r4 - r1
        L43:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.display.FontWeightAdjustView.getPointX():float");
    }

    private void init(AttributeSet attributeSet, int i) {
        this.mBigPointColor = getResources().getColor(R.color.font_size_seekbar_big_pointer_blue, null);
        this.mSmallPointColor = getResources().getColor(R.color.font_weight_view_small_color, null);
        this.mBigPointCenterColor = getResources().getColor(R.color.font_size_view_big_center_color, null);
        this.mPointsRadius = getResources().getDimension(R.dimen.font_size_view_small_radius);
        this.mBigPointsRadius = getResources().getDimension(R.dimen.font_size_view_big_radius);
        Paint paint = new Paint();
        this.mPointPaint = paint;
        paint.setAntiAlias(true);
        this.mPointPaint.setStyle(Paint.Style.FILL);
        this.mPointPaint.setStrokeWidth(0.0f);
        this.mHapticUtil = HapticUtil.getInstance(getContext().getApplicationContext());
        setOnSeekBarChangeListener(this.mSeekListener);
        if (this.isPrimaryUser) {
            return;
        }
        setAlpha(0.3f);
        setEnabled(false);
    }

    private boolean isRtl() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
    }

    private int setPointX(float f) {
        int i;
        int width = getWidth();
        float f2 = this.mBigPointsRadius;
        if (f < f2) {
            i = 0;
            this.mPointsX = f2;
        } else {
            float f3 = width;
            if (f > f3 - f2) {
                this.mPointsX = f3 - f2;
                i = 100;
            } else {
                float f4 = width / 2;
                if (Math.abs(f - f4) < 29.0f) {
                    i = 50;
                    this.mPointsX = f4;
                } else {
                    this.mPointsX = f;
                    i = (int) ((100.0f * f) / f3);
                }
            }
        }
        return isRtl() ? 100 - i : i;
    }

    public FontWeightChangeListener getFontWeightChangeListener() {
        return this.mListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.AppCompatSeekBar, android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!(LargeFontUtils.getFontWeight(getContext()) == 50)) {
            this.mPointPaint.setColor(this.mSmallPointColor);
            canvas.drawCircle(getWidth() / 2, this.mPointsY, this.mPointsRadius, this.mPointPaint);
        }
        this.mPointPaint.setColor(this.mBigPointColor);
        canvas.drawCircle(this.mPointsX, this.mPointsY, this.mBigPointsRadius, this.mPointPaint);
        this.mPointPaint.setColor(this.mBigPointCenterColor);
        canvas.drawCircle(this.mPointsX, this.mPointsY, this.mPointsRadius, this.mPointPaint);
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mPointsX = getPointX();
        this.mPointsY = getHeight() / 2;
    }

    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        getParent().requestDisallowInterceptTouchEvent(true);
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action == 1) {
                if (!isEnabled() && this.isPrimaryUser) {
                    ToastUtil.show(getContext(), R.string.font_setting_weight_toast, 0);
                }
                return true;
            } else if (action != 2) {
                return false;
            }
        }
        if (isEnabled()) {
            ensurePerformHapticFeedback(motionEvent.getAction());
            setProgress(setPointX(motionEvent.getX()));
            return true;
        }
        return true;
    }

    public void setFontWeightChangeListener(FontWeightChangeListener fontWeightChangeListener) {
        this.mListener = fontWeightChangeListener;
    }
}
