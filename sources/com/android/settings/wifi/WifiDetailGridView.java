package com.android.settings.wifi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

/* loaded from: classes2.dex */
public class WifiDetailGridView extends GridView {
    private double mFontScaler;

    public WifiDetailGridView(Context context) {
        super(context);
    }

    public WifiDetailGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFontScaler = context.getResources().getConfiguration().fontScale;
    }

    public WifiDetailGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 2) {
            return true;
        }
        getParent().requestDisallowInterceptTouchEvent(false);
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.widget.GridView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(536870911, Integer.MIN_VALUE));
        double d = this.mFontScaler;
        if (getCount() > 6) {
            if (this.mFontScaler <= 1.0d) {
                d = 1.1d;
            }
            setMeasuredDimension(View.MeasureSpec.getSize(i), (int) (View.MeasureSpec.getSize(i2) * d));
        }
    }
}
