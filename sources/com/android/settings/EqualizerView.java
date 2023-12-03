package com.android.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.android.settings.search.SearchUpdater;

/* loaded from: classes.dex */
public class EqualizerView extends View {
    public static int MAX_FREQ = 20000;
    public static int MIN_FREQ = 20;
    public static int SAMPLING_RATE = 44100;
    public static int SCALE = 1;
    private final int mCurveColor;
    private final Paint mCurvePaint;
    private final int mCurveShadowColor;
    private final float mCurveShadowRadius;
    private int mHeight;
    private final float[] mLevels;
    private int mMaxRank;
    private int mMinRank;
    private int mWidth;

    /* loaded from: classes.dex */
    static class Biquad {
        private Complex a0;
        private Complex a1;
        private Complex a2;
        private Complex b0;
        private Complex b1;
        private Complex b2;

        Biquad() {
        }

        protected Complex evaluateTransfer(Complex complex) {
            Complex mul = complex.mul(complex);
            return this.b0.add(this.b1.div(complex)).add(this.b2.div(mul)).div(this.a0.add(this.a1.div(complex)).add(this.a2.div(mul)));
        }

        protected void setHighShelf(float f, float f2, float f3, float f4) {
            double d = (f * 6.283185307179586d) / f2;
            double pow = Math.pow(10.0d, f3 / 40.0f);
            double sin = (Math.sin(d) / 2.0d) * Math.sqrt((((1.0d / pow) + pow) * ((1.0f / f4) - 1.0f)) + 2.0d);
            double d2 = pow + 1.0d;
            double d3 = pow - 1.0d;
            this.b0 = new Complex((float) (((Math.cos(d) * d3) + d2 + (Math.sqrt(pow) * 2.0d * sin)) * pow), 0.0f);
            this.b1 = new Complex((float) ((-2.0d) * pow * (d3 + (Math.cos(d) * d2))), 0.0f);
            this.b2 = new Complex((float) (pow * ((d2 + (Math.cos(d) * d3)) - ((Math.sqrt(pow) * 2.0d) * sin))), 0.0f);
            this.a0 = new Complex((float) ((d2 - (Math.cos(d) * d3)) + (Math.sqrt(pow) * 2.0d * sin)), 0.0f);
            this.a1 = new Complex((float) ((d3 - (Math.cos(d) * d2)) * 2.0d), 0.0f);
            this.a2 = new Complex((float) ((d2 - (d3 * Math.cos(d))) - ((Math.sqrt(pow) * 2.0d) * sin)), 0.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Complex {
        final float im;
        final float re;

        protected Complex(float f, float f2) {
            this.re = f;
            this.im = f2;
        }

        protected Complex add(Complex complex) {
            return new Complex(this.re + complex.re, this.im + complex.im);
        }

        protected Complex con() {
            return new Complex(this.re, -this.im);
        }

        protected Complex div(float f) {
            return new Complex(this.re / f, this.im / f);
        }

        protected Complex div(Complex complex) {
            float f = complex.re;
            float f2 = complex.im;
            return mul(complex.con()).div((f * f) + (f2 * f2));
        }

        protected Complex mul(float f) {
            return new Complex(this.re * f, this.im * f);
        }

        protected Complex mul(Complex complex) {
            float f = this.re;
            float f2 = complex.re;
            float f3 = this.im;
            float f4 = complex.im;
            return new Complex((f * f2) - (f3 * f4), (f * f4) + (f3 * f2));
        }

        protected float rho() {
            float f = this.re;
            float f2 = this.im;
            return (float) Math.sqrt((f * f) + (f2 * f2));
        }
    }

    public EqualizerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLevels = new float[7];
        this.mMinRank = 0;
        this.mMaxRank = 0;
        setWillNotDraw(false);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.EqualizerView);
            this.mCurveColor = obtainStyledAttributes.getColor(R$styleable.EqualizerView_curve_color, 16756224);
            this.mCurveShadowColor = obtainStyledAttributes.getColor(R$styleable.EqualizerView_curve_shadow_color, 0);
            this.mCurveShadowRadius = obtainStyledAttributes.getFloat(R$styleable.EqualizerView_curve_shadow_radius, 0.0f);
            obtainStyledAttributes.recycle();
        } else {
            this.mCurveColor = 16756224;
            this.mCurveShadowColor = 0;
            this.mCurveShadowRadius = 0.0f;
        }
        Paint paint = new Paint();
        this.mCurvePaint = paint;
        paint.setColor(this.mCurveColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1.0f);
        paint.setAntiAlias(true);
    }

    private float lin2dB(float f) {
        if (f != 0.0f) {
            return (float) ((Math.log(f) / Math.log(10.0d)) * 20.0d);
        }
        return -99.0f;
    }

    private float projectX(float f) {
        double log = Math.log(f);
        double log2 = Math.log(MIN_FREQ);
        return (float) ((log - log2) / (Math.log(MAX_FREQ) - log2));
    }

    private float projectY(float f) {
        int i = this.mMaxRank;
        int i2 = this.mMinRank;
        if (i - i2 <= 0) {
            Log.e("EqualizerView", "rank is unint");
            return 0.0f;
        }
        return 1.0f - ((f - i2) / (i - i2));
    }

    private void setPanitAlpha(float f) {
        if (f < 0.01f) {
            f = 0.01f;
        } else if (f < 0.05f) {
            f = 0.05f;
        }
        this.mCurvePaint.setAlpha((int) (255.0f * f));
        int i = this.mCurveShadowColor;
        if (i != 0) {
            this.mCurvePaint.setShadowLayer(this.mCurveShadowRadius * f, 0.0f, 0.0f, i);
        }
    }

    public int getMaxLevel() {
        return this.mMaxRank * SCALE;
    }

    public int getMinLevel() {
        return this.mMinRank * SCALE;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        char c = 0;
        char c2 = 1;
        char c3 = 2;
        Biquad[] biquadArr = {new Biquad(), new Biquad(), new Biquad(), new Biquad(), new Biquad(), new Biquad()};
        float pow = (float) Math.pow(10.0d, this.mLevels[0] / 20.0f);
        Biquad biquad = biquadArr[0];
        float f = SAMPLING_RATE;
        float[] fArr = this.mLevels;
        biquad.setHighShelf(75.0f, f, fArr[1] - fArr[0], 1.0f);
        Biquad biquad2 = biquadArr[1];
        float f2 = SAMPLING_RATE;
        float[] fArr2 = this.mLevels;
        biquad2.setHighShelf(175.0f, f2, fArr2[2] - fArr2[1], 1.0f);
        Biquad biquad3 = biquadArr[2];
        float f3 = SAMPLING_RATE;
        float[] fArr3 = this.mLevels;
        biquad3.setHighShelf(350.0f, f3, fArr3[3] - fArr3[2], 1.0f);
        Biquad biquad4 = biquadArr[3];
        float f4 = SAMPLING_RATE;
        float[] fArr4 = this.mLevels;
        biquad4.setHighShelf(900.0f, f4, fArr4[4] - fArr4[3], 1.0f);
        Biquad biquad5 = biquadArr[4];
        float f5 = SAMPLING_RATE;
        float[] fArr5 = this.mLevels;
        biquad5.setHighShelf(1750.0f, f5, fArr5[5] - fArr5[4], 1.0f);
        Biquad biquad6 = biquadArr[5];
        float f6 = SAMPLING_RATE;
        float[] fArr6 = this.mLevels;
        biquad6.setHighShelf(3500.0f, f6, fArr6[6] - fArr6[5], 1.0f);
        float f7 = 1.15f;
        float f8 = MIN_FREQ / 1.15f;
        float f9 = -1.0f;
        float f10 = 0.0f;
        while (f8 < MAX_FREQ * f7) {
            double d = (f8 / SAMPLING_RATE) * 3.1415927f * 2.0f;
            Complex complex = new Complex((float) Math.cos(d), (float) Math.sin(d));
            float projectY = projectY(lin2dB(complex.mul(pow).rho() * biquadArr[c].evaluateTransfer(complex).rho() * biquadArr[c2].evaluateTransfer(complex).rho() * biquadArr[c3].evaluateTransfer(complex).rho() * biquadArr[3].evaluateTransfer(complex).rho() * biquadArr[4].evaluateTransfer(complex).rho() * biquadArr[5].evaluateTransfer(complex).rho())) * this.mHeight;
            float projectX = projectX(f8);
            int i = this.mWidth;
            float f11 = projectX * i;
            if (f9 != -1.0f) {
                float f12 = i / 5;
                if (f9 < f12) {
                    setPanitAlpha(f9 / f12);
                } else {
                    float f13 = i - f9;
                    if (f12 > f13) {
                        setPanitAlpha(f13 / f12);
                    } else {
                        this.mCurvePaint.setAlpha(255);
                        int i2 = this.mCurveShadowColor;
                        if (i2 != 0) {
                            this.mCurvePaint.setShadowLayer(this.mCurveShadowRadius, 0.0f, 0.0f, i2);
                            int i3 = ((View) this).mPaddingLeft;
                            int i4 = ((View) this).mPaddingTop;
                            canvas.drawLine(i3 + f9, i4 + f10, i3 + f11, i4 + projectY, this.mCurvePaint);
                        }
                    }
                }
                int i32 = ((View) this).mPaddingLeft;
                int i42 = ((View) this).mPaddingTop;
                canvas.drawLine(i32 + f9, i42 + f10, i32 + f11, i42 + projectY, this.mCurvePaint);
            }
            f8 *= 1.15f;
            f10 = projectY;
            f9 = f11;
            f7 = 1.15f;
            c = 0;
            c2 = 1;
            c3 = 2;
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mWidth = (i3 - i) - (((View) this).mPaddingLeft + ((View) this).mPaddingRight);
        this.mHeight = (i4 - i2) - (((View) this).mPaddingTop + ((View) this).mPaddingBottom);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        Drawable background = getBackground();
        if (background != null && background.getIntrinsicHeight() > 0) {
            i2 = View.MeasureSpec.makeMeasureSpec(background.getIntrinsicHeight(), SearchUpdater.SIM);
        }
        super.onMeasure(i, i2);
    }

    public void setBands(float[] fArr) {
        setBands(fArr, 0);
    }

    public void setBands(float[] fArr, int i) {
        int i2 = 0;
        while (true) {
            float[] fArr2 = this.mLevels;
            if (i2 >= fArr2.length) {
                postInvalidate();
                return;
            } else {
                fArr2[i2] = fArr[i + i2] / SCALE;
                i2++;
            }
        }
    }
}
