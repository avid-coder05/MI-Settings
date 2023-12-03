package com.miui.maml.folme;

import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;

/* loaded from: classes2.dex */
public class PropertyWrapper {
    private double mDefaultValue;
    private Expression mExpression;
    private boolean mInFolmeMode;
    private IndexedVariable mValue;
    private IndexedVariable mVelocity;

    public PropertyWrapper(String str, Variables variables, Expression expression, boolean z, double d) {
        this.mInFolmeMode = z;
        if (z) {
            this.mValue = new IndexedVariable(str, variables, true);
            this.mVelocity = new IndexedVariable(str + "_v", variables, true);
        }
        this.mExpression = expression;
        this.mDefaultValue = d;
    }

    public double getValue() {
        if (this.mInFolmeMode) {
            return this.mValue.getDouble();
        }
        Expression expression = this.mExpression;
        return expression != null ? expression.evaluate() : this.mDefaultValue;
    }

    public void init() {
        if (this.mInFolmeMode) {
            IndexedVariable indexedVariable = this.mValue;
            Expression expression = this.mExpression;
            indexedVariable.set(expression != null ? expression.evaluate() : this.mDefaultValue);
        }
    }

    public void setValue(double d) {
        if (this.mInFolmeMode) {
            this.mValue.set(d);
            return;
        }
        Expression expression = this.mExpression;
        if (expression == null || !(expression instanceof Expression.NumberExpression)) {
            this.mExpression = new Expression.NumberExpression(d);
        } else {
            ((Expression.NumberExpression) expression).setValue(d);
        }
    }

    public void setVelocity(double d) {
        if (this.mInFolmeMode) {
            this.mVelocity.set(d);
        }
    }
}
