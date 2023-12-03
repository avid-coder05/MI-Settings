package com.miui.maml.animation.interpolater;

import android.text.TextUtils;
import android.view.animation.Interpolator;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class InterpolatorHelper {
    private Expression mEaseFunExp;
    private Interpolator mInterpolator;
    private IndexedVariable mRatioVar;

    public InterpolatorHelper(Variables variables, String str, String str2, String str3) {
        this.mInterpolator = InterpolatorFactory.create(str, Expression.buildMultiple(variables, str3));
        Expression build = Expression.build(variables, str2);
        this.mEaseFunExp = build;
        if (build != null) {
            this.mRatioVar = new IndexedVariable("__ratio", variables, true);
        }
    }

    public static InterpolatorHelper create(Variables variables, Element element) {
        String attribute = element.getAttribute("easeType");
        String attribute2 = element.getAttribute("easeExp");
        String attribute3 = element.getAttribute("easeParamsExp");
        if (TextUtils.isEmpty(attribute) && TextUtils.isEmpty(attribute2)) {
            return null;
        }
        return new InterpolatorHelper(variables, attribute, attribute2, attribute3);
    }

    public float get(float f) {
        if (this.mEaseFunExp != null) {
            this.mRatioVar.set(f);
            return (float) this.mEaseFunExp.evaluate();
        }
        Interpolator interpolator = this.mInterpolator;
        return interpolator != null ? interpolator.getInterpolation(f) : f;
    }
}
