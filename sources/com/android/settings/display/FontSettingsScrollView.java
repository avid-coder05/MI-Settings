package com.android.settings.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.android.settings.R;

/* loaded from: classes.dex */
public class FontSettingsScrollView extends ScrollView {
    private boolean mCanScroll;

    public FontSettingsScrollView(Context context) {
        super(context);
        this.mCanScroll = true;
    }

    public FontSettingsScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCanScroll = true;
    }

    public FontSettingsScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCanScroll = true;
    }

    public FontSettingsScrollView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mCanScroll = true;
    }

    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int measuredWidth = getMeasuredWidth();
        LinearLayout linearLayout = (LinearLayout) getChildAt(0);
        measureChild(linearLayout, i, i2);
        int childCount = linearLayout.getChildCount();
        int i3 = 0;
        for (int i4 = 0; i4 < childCount; i4++) {
            View childAt = linearLayout.getChildAt(i4);
            childAt.measure(i, i2);
            int measuredHeight = childAt.getMeasuredHeight();
            if (childAt.getId() == R.id.recommend_layout) {
                measuredHeight = (int) (((int) ((measuredHeight * 4.0f) / 5.0f)) * 0.5f);
            }
            i3 += measuredHeight;
        }
        setMeasuredDimension(measuredWidth, i3);
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mCanScroll) {
            return super.onTouchEvent(motionEvent);
        }
        return true;
    }

    public void setCanScroll(boolean z) {
        this.mCanScroll = z;
    }
}
