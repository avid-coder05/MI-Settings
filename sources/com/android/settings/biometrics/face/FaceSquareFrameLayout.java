package com.android.settings.biometrics.face;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.settings.search.SearchUpdater;

/* loaded from: classes.dex */
public class FaceSquareFrameLayout extends FrameLayout {
    public FaceSquareFrameLayout(Context context) {
        super(context);
    }

    public FaceSquareFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FaceSquareFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public FaceSquareFrameLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        setMeasuredDimension(size, size);
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, SearchUpdater.SIM);
            getChildAt(i3).measure(makeMeasureSpec, makeMeasureSpec);
        }
    }
}
