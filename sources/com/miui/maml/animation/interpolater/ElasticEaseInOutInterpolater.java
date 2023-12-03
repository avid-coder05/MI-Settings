package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;
import com.miui.maml.data.Expression;

/* loaded from: classes2.dex */
public class ElasticEaseInOutInterpolater implements Interpolator {
    private float mAmplitude;
    private Expression mAmplitudeExp;
    private float mPriod;
    private Expression mPriodExp;

    public ElasticEaseInOutInterpolater(float f, float f2) {
        this.mAmplitude = 0.0f;
        this.mPriod = 0.45000002f;
        this.mPriod = f;
        this.mAmplitude = f2;
    }

    public ElasticEaseInOutInterpolater(Expression[] expressionArr) {
        this.mAmplitude = 0.0f;
        this.mPriod = 0.45000002f;
        if (expressionArr != null) {
            if (expressionArr.length > 0) {
                this.mAmplitudeExp = expressionArr[0];
            }
            if (expressionArr.length > 1) {
                this.mPriodExp = expressionArr[1];
            }
        }
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        float asin;
        Expression expression = this.mAmplitudeExp;
        if (expression != null) {
            this.mAmplitude = (float) expression.evaluate();
        }
        Expression expression2 = this.mPriodExp;
        if (expression2 != null) {
            this.mPriod = (float) expression2.evaluate();
        }
        float f2 = this.mAmplitude;
        if (f == 0.0f) {
            return 0.0f;
        }
        float f3 = f / 0.5f;
        if (f3 == 2.0f) {
            return 1.0f;
        }
        if (f2 < 1.0f) {
            asin = this.mPriod / 4.0f;
            f2 = 1.0f;
        } else {
            asin = (float) ((this.mPriod / 6.283185307179586d) * Math.asin(1.0f / f2));
        }
        if (f3 < 1.0f) {
            float f4 = f3 - 1.0f;
            return ((float) (f2 * Math.pow(2.0d, 10.0f * f4) * Math.sin(((f4 - asin) * 6.283185307179586d) / this.mPriod))) * (-0.5f);
        }
        float f5 = f3 - 1.0f;
        return (float) ((f2 * Math.pow(2.0d, (-10.0f) * f5) * Math.sin(((f5 - asin) * 6.283185307179586d) / this.mPriod) * 0.5d) + 1.0d);
    }
}
