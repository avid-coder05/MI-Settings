package com.google.android.material.progressindicator;

import android.content.Context;
import android.util.AttributeSet;
import com.google.android.material.R$attr;
import com.google.android.material.R$style;

/* loaded from: classes2.dex */
public final class CircularProgressIndicator extends BaseProgressIndicator<CircularProgressIndicatorSpec> {
    public static final int DEF_STYLE_RES = R$style.Widget_MaterialComponents_CircularProgressIndicator;

    public CircularProgressIndicator(Context context) {
        this(context, null);
    }

    public CircularProgressIndicator(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.circularProgressIndicatorStyle);
    }

    public CircularProgressIndicator(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i, DEF_STYLE_RES);
        initializeDrawables();
    }

    private void initializeDrawables() {
        setIndeterminateDrawable(IndeterminateDrawable.createCircularDrawable(getContext(), (CircularProgressIndicatorSpec) this.spec));
        setProgressDrawable(DeterminateDrawable.createCircularDrawable(getContext(), (CircularProgressIndicatorSpec) this.spec));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.android.material.progressindicator.BaseProgressIndicator
    public CircularProgressIndicatorSpec createSpec(Context context, AttributeSet attributeSet) {
        return new CircularProgressIndicatorSpec(context, attributeSet);
    }

    public int getIndicatorDirection() {
        return ((CircularProgressIndicatorSpec) this.spec).indicatorDirection;
    }

    public int getIndicatorInset() {
        return ((CircularProgressIndicatorSpec) this.spec).indicatorInset;
    }

    public int getIndicatorSize() {
        return ((CircularProgressIndicatorSpec) this.spec).indicatorSize;
    }

    public void setIndicatorDirection(int i) {
        ((CircularProgressIndicatorSpec) this.spec).indicatorDirection = i;
        invalidate();
    }

    public void setIndicatorInset(int i) {
        S s = this.spec;
        if (((CircularProgressIndicatorSpec) s).indicatorInset != i) {
            ((CircularProgressIndicatorSpec) s).indicatorInset = i;
            invalidate();
        }
    }

    public void setIndicatorSize(int i) {
        int max = Math.max(i, getTrackThickness() * 2);
        S s = this.spec;
        if (((CircularProgressIndicatorSpec) s).indicatorSize != max) {
            ((CircularProgressIndicatorSpec) s).indicatorSize = max;
            ((CircularProgressIndicatorSpec) s).validateSpec();
            invalidate();
        }
    }

    @Override // com.google.android.material.progressindicator.BaseProgressIndicator
    public void setTrackThickness(int i) {
        super.setTrackThickness(i);
        ((CircularProgressIndicatorSpec) this.spec).validateSpec();
    }
}
