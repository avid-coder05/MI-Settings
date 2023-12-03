package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;
import com.miui.maml.data.Expression;

/* loaded from: classes2.dex */
public class BackEaseInInterpolater implements Interpolator {
    private float mFactor;
    private Expression mFactorExp;

    public BackEaseInInterpolater(float f) {
        this.mFactor = 1.70158f;
        this.mFactor = f;
    }

    public BackEaseInInterpolater(Expression[] expressionArr) {
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
        return f * f * (((1.0f + f2) * f) - f2);
    }
}
