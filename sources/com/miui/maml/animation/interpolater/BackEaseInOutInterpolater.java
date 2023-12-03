package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;
import com.miui.maml.data.Expression;

/* loaded from: classes2.dex */
public class BackEaseInOutInterpolater implements Interpolator {
    private float mFactor;
    private Expression mFactorExp;

    public BackEaseInOutInterpolater(float f) {
        this.mFactor = 1.70158f;
        this.mFactor = f;
    }

    public BackEaseInOutInterpolater(Expression[] expressionArr) {
        this.mFactor = 1.70158f;
        if (expressionArr == null || expressionArr.length <= 0) {
            return;
        }
        this.mFactorExp = expressionArr[0];
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        Expression expression = this.mFactorExp;
        if (expression != null) {
            this.mFactor = (float) expression.evaluate();
        }
        float f2 = this.mFactor;
        float f3 = f * 2.0f;
        if (f3 < 1.0f) {
            float f4 = (float) (f2 * 1.525d);
            return f3 * f3 * (((1.0f + f4) * f3) - f4) * 0.5f;
        }
        float f5 = f3 - 2.0f;
        float f6 = (float) (f2 * 1.525d);
        return ((f5 * f5 * (((1.0f + f6) * f5) + f6)) + 2.0f) * 0.5f;
    }
}
