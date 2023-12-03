package com.miui.maml.animation.interpolater;

import android.view.animation.Interpolator;
import com.miui.maml.data.Expression;

/* loaded from: classes2.dex */
public class PhysicBasedInterpolator implements Interpolator {
    private float c;
    private float c1;
    private float c2;
    private float k;
    private float m;
    private float mDamping;
    private Expression mDampingExp;
    private float mInitial;
    private boolean mNeedEvaluate;
    private float mResponse;
    private Expression mResponseExp;
    private float r;
    private float w;

    public PhysicBasedInterpolator(float f, float f2) {
        this.mDamping = 0.9f;
        this.mResponse = 0.3f;
        this.mInitial = -1.0f;
        this.m = 1.0f;
        this.c1 = -1.0f;
        this.mNeedEvaluate = true;
        this.mDamping = f;
        this.mResponse = f2;
    }

    public PhysicBasedInterpolator(Expression[] expressionArr) {
        this.mDamping = 0.9f;
        this.mResponse = 0.3f;
        this.mInitial = -1.0f;
        this.m = 1.0f;
        this.c1 = -1.0f;
        this.mNeedEvaluate = true;
        if (expressionArr != null) {
            if (expressionArr.length > 0) {
                this.mDampingExp = expressionArr[0];
            }
            if (expressionArr.length > 1) {
                this.mResponseExp = expressionArr[1];
            }
        }
    }

    private void evaluate() {
        if (this.mNeedEvaluate) {
            double pow = Math.pow(6.283185307179586d / this.mResponse, 2.0d);
            float f = this.m;
            this.k = (float) (pow * f);
            this.c = (float) (((this.mDamping * 12.566370614359172d) * f) / this.mResponse);
            float sqrt = (float) Math.sqrt(((f * 4.0f) * r0) - (r1 * r1));
            float f2 = this.m;
            float f3 = sqrt / (f2 * 2.0f);
            this.w = f3;
            float f4 = -((this.c / 2.0f) * f2);
            this.r = f4;
            this.c2 = (0.0f - (f4 * this.mInitial)) / f3;
            this.mNeedEvaluate = false;
        }
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        Expression expression = this.mDampingExp;
        if (expression != null) {
            float evaluate = (float) expression.evaluate();
            if (this.mDamping != evaluate) {
                this.mDamping = evaluate;
                this.mNeedEvaluate = true;
            }
        }
        Expression expression2 = this.mResponseExp;
        if (expression2 != null) {
            float evaluate2 = (float) expression2.evaluate();
            if (this.mResponse != evaluate2) {
                this.mResponse = evaluate2;
                this.mNeedEvaluate = true;
            }
        }
        evaluate();
        return (float) ((Math.pow(2.718281828459045d, this.r * f) * ((this.c1 * Math.cos(this.w * f)) + (this.c2 * Math.sin(this.w * f)))) + 1.0d);
    }
}
