package com.android.settings.usagestats.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.android.settings.search.SearchUpdater;
import com.android.settings.usagestats.utils.CommonUtils;
import miuix.viewpager.widget.ViewPager;

/* loaded from: classes2.dex */
public class NoScrollViewPager extends ViewPager {
    private boolean isRtl;
    private boolean needResize;
    private int touchDownX;
    private int touchDownY;

    public NoScrollViewPager(Context context) {
        super(context);
        this.needResize = true;
        init();
    }

    public NoScrollViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.needResize = true;
        init();
    }

    private void ensureResizeNeeded(int i) {
        this.needResize = false;
        if (getChildCount() <= 1) {
            return;
        }
        int viewHeight = getViewHeight(getChildAt(0), i);
        for (int i2 = 1; i2 < getChildCount(); i2++) {
            if (viewHeight != getViewHeight(getChildAt(i2), i)) {
                this.needResize = true;
                return;
            }
        }
    }

    private int getViewHeight(View view, int i) {
        view.measure(i, View.MeasureSpec.makeMeasureSpec(0, 0));
        return view.getMeasuredHeight();
    }

    private void init() {
        this.isRtl = CommonUtils.isRtl();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.touchDownX = (int) motionEvent.getX();
            this.touchDownY = (int) motionEvent.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (action == 2) {
            if (Math.abs(((int) motionEvent.getX()) - this.touchDownX) > Math.abs(((int) motionEvent.getY()) - this.touchDownY)) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // androidx.viewpager.widget.OriginalViewPager
    public int getCurrentItem() {
        int childCount = getChildCount();
        if (childCount > 1 && this.isRtl) {
            return (childCount - 1) - super.getCurrentItem();
        }
        return super.getCurrentItem();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.viewpager.widget.OriginalViewPager, android.view.View
    public void onMeasure(int i, int i2) {
        if (getChildCount() == 0) {
            super.onMeasure(i, i2);
            return;
        }
        ensureResizeNeeded(i);
        View childAt = getChildAt(getCurrentItem());
        if (childAt != null) {
            childAt.measure(i, View.MeasureSpec.makeMeasureSpec(0, 0));
            i2 = View.MeasureSpec.makeMeasureSpec(childAt.getMeasuredHeight(), SearchUpdater.SIM);
        }
        super.onMeasure(i, i2);
    }

    public void resize() {
        if (this.needResize) {
            requestLayout();
        }
    }

    @Override // androidx.viewpager.widget.OriginalViewPager
    public void setCurrentItem(int i) {
        super.setCurrentItem(i);
        resize();
    }
}
