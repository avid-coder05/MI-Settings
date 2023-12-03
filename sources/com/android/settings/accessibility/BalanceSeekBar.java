package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.SeekBar;
import com.android.settings.R;

/* loaded from: classes.dex */
public class BalanceSeekBar extends SeekBar {
    static final float SNAP_TO_PERCENTAGE = 0.03f;
    private int mCenter;
    private final Paint mCenterMarkerPaint;
    private final Rect mCenterMarkerRect;
    private final Context mContext;
    private int mLastProgress;
    private final Object mListenerLock;
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    private final SeekBar.OnSeekBarChangeListener mProxySeekBarListener;
    private float mSnapThreshold;

    public BalanceSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.seekBarStyle);
    }

    public BalanceSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mListenerLock = new Object();
        this.mLastProgress = -1;
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.android.settings.accessibility.BalanceSeekBar.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i2, boolean z) {
                if (z) {
                    if (i2 != BalanceSeekBar.this.mCenter) {
                        float f = i2;
                        if (f > BalanceSeekBar.this.mCenter - BalanceSeekBar.this.mSnapThreshold && f < BalanceSeekBar.this.mCenter + BalanceSeekBar.this.mSnapThreshold) {
                            i2 = BalanceSeekBar.this.mCenter;
                            seekBar.setProgress(i2);
                        }
                    }
                    if (i2 != BalanceSeekBar.this.mLastProgress) {
                        if (i2 == BalanceSeekBar.this.mCenter || i2 == BalanceSeekBar.this.getMin() || i2 == BalanceSeekBar.this.getMax()) {
                            seekBar.performHapticFeedback(4);
                        }
                        BalanceSeekBar.this.mLastProgress = i2;
                    }
                    Settings.System.putFloatForUser(BalanceSeekBar.this.mContext.getContentResolver(), "master_balance", (i2 - BalanceSeekBar.this.mCenter) * 0.01f, -2);
                }
                synchronized (BalanceSeekBar.this.mListenerLock) {
                    if (BalanceSeekBar.this.mOnSeekBarChangeListener != null) {
                        BalanceSeekBar.this.mOnSeekBarChangeListener.onProgressChanged(seekBar, i2, z);
                    }
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                synchronized (BalanceSeekBar.this.mListenerLock) {
                    if (BalanceSeekBar.this.mOnSeekBarChangeListener != null) {
                        BalanceSeekBar.this.mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                    }
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                synchronized (BalanceSeekBar.this.mListenerLock) {
                    if (BalanceSeekBar.this.mOnSeekBarChangeListener != null) {
                        BalanceSeekBar.this.mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                    }
                }
            }
        };
        this.mProxySeekBarListener = onSeekBarChangeListener;
        this.mContext = context;
        Resources resources = getResources();
        this.mCenterMarkerRect = new Rect(0, 0, resources.getDimensionPixelSize(R.dimen.balance_seekbar_center_marker_width), resources.getDimensionPixelSize(R.dimen.balance_seekbar_center_marker_height));
        Paint paint = new Paint();
        this.mCenterMarkerPaint = paint;
        paint.setColor(context.getResources().getColor(R.color.balance_seekbar_center_marker_color));
        paint.setStyle(Paint.Style.FILL);
        super.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    public BalanceSeekBar(Context context, AttributeSet attributeSet, int i, int i2) {
        this(context, attributeSet, i);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    protected synchronized void onDraw(Canvas canvas) {
        int height = (canvas.getHeight() - getPaddingBottom()) / 2;
        canvas.save();
        int width = canvas.getWidth();
        Rect rect = this.mCenterMarkerRect;
        canvas.translate((width - rect.right) / 2, height - (rect.bottom / 2));
        canvas.drawRect(this.mCenterMarkerRect, this.mCenterMarkerPaint);
        canvas.restore();
        super.onDraw(canvas);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar
    public synchronized void setMax(int i) {
        super.setMax(i);
        this.mCenter = i / 2;
        this.mSnapThreshold = i * SNAP_TO_PERCENTAGE;
    }

    @Override // android.widget.SeekBar
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        Object obj = this.mListenerLock;
        if (obj != null) {
            synchronized (obj) {
                this.mOnSeekBarChangeListener = onSeekBarChangeListener;
            }
        }
    }
}
