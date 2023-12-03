package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.widget.PagerAdapter;
import com.android.internal.widget.ViewPager;
import com.android.settings.R;
import com.android.settings.search.SearchUpdater;

/* loaded from: classes2.dex */
public final class SlidingTabLayout extends FrameLayout implements View.OnClickListener {
    private final View mIndicatorView;
    private final LayoutInflater mLayoutInflater;
    private int mSelectedPosition;
    private float mSelectionOffset;
    private final LinearLayout mTitleView;
    private RtlCompatibleViewPager mViewPager;

    /* loaded from: classes2.dex */
    private final class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        private InternalViewPagerListener() {
        }

        public void onPageScrollStateChanged(int i) {
            this.mScrollState = i;
        }

        public void onPageScrolled(int i, float f, int i2) {
            int childCount = SlidingTabLayout.this.mTitleView.getChildCount();
            if (childCount == 0 || i < 0 || i >= childCount) {
                return;
            }
            SlidingTabLayout.this.onViewPagerPageChanged(i, f);
        }

        public void onPageSelected(int i) {
            int rtlAwareIndex = SlidingTabLayout.this.mViewPager.getRtlAwareIndex(i);
            if (this.mScrollState == 0) {
                SlidingTabLayout.this.onViewPagerPageChanged(rtlAwareIndex, 0.0f);
            }
            int childCount = SlidingTabLayout.this.mTitleView.getChildCount();
            int i2 = 0;
            while (i2 < childCount) {
                SlidingTabLayout.this.mTitleView.getChildAt(i2).setSelected(rtlAwareIndex == i2);
                i2++;
            }
        }
    }

    public SlidingTabLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater from = LayoutInflater.from(context);
        this.mLayoutInflater = from;
        LinearLayout linearLayout = new LinearLayout(context);
        this.mTitleView = linearLayout;
        linearLayout.setGravity(1);
        View inflate = from.inflate(R.layout.sliding_tab_indicator_view, (ViewGroup) this, false);
        this.mIndicatorView = inflate;
        addView(linearLayout, -1, -2);
        addView(inflate, inflate.getLayoutParams());
    }

    private int getIndicatorLeft() {
        int left = this.mTitleView.getChildAt(this.mSelectedPosition).getLeft();
        if (this.mSelectionOffset <= 0.0f || this.mSelectedPosition >= getChildCount() - 1) {
            return left;
        }
        return (int) ((this.mSelectionOffset * this.mTitleView.getChildAt(this.mSelectedPosition + 1).getLeft()) + ((1.0f - this.mSelectionOffset) * left));
    }

    private boolean isRtlMode() {
        return getLayoutDirection() == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onViewPagerPageChanged(int i, float f) {
        this.mSelectedPosition = i;
        this.mSelectionOffset = f;
        this.mIndicatorView.setTranslationX(isRtlMode() ? -getIndicatorLeft() : getIndicatorLeft());
    }

    private void populateTabStrip() {
        PagerAdapter adapter = this.mViewPager.getAdapter();
        int i = 0;
        while (i < adapter.getCount()) {
            TextView textView = (TextView) this.mLayoutInflater.inflate(R.layout.sliding_tab_title_view, (ViewGroup) this.mTitleView, false);
            textView.setText(adapter.getPageTitle(i));
            textView.setOnClickListener(this);
            this.mTitleView.addView(textView);
            textView.setSelected(i == this.mViewPager.getCurrentItem());
            i++;
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int childCount = this.mTitleView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (view == this.mTitleView.getChildAt(i)) {
                this.mViewPager.setCurrentItem(i);
                return;
            }
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mTitleView.getChildCount() > 0) {
            int measuredHeight = getMeasuredHeight();
            int measuredHeight2 = this.mIndicatorView.getMeasuredHeight();
            int measuredWidth = this.mIndicatorView.getMeasuredWidth();
            int measuredWidth2 = getMeasuredWidth();
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            LinearLayout linearLayout = this.mTitleView;
            linearLayout.layout(paddingLeft, 0, linearLayout.getMeasuredWidth() + paddingRight, this.mTitleView.getMeasuredHeight());
            if (isRtlMode()) {
                this.mIndicatorView.layout(measuredWidth2 - measuredWidth, measuredHeight - measuredHeight2, measuredWidth2, measuredHeight);
            } else {
                this.mIndicatorView.layout(0, measuredHeight - measuredHeight2, measuredWidth, measuredHeight);
            }
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int childCount = this.mTitleView.getChildCount();
        if (childCount > 0) {
            this.mIndicatorView.measure(View.MeasureSpec.makeMeasureSpec(this.mTitleView.getMeasuredWidth() / childCount, SearchUpdater.SIM), View.MeasureSpec.makeMeasureSpec(this.mIndicatorView.getMeasuredHeight(), SearchUpdater.SIM));
        }
    }

    public void setViewPager(RtlCompatibleViewPager rtlCompatibleViewPager) {
        this.mTitleView.removeAllViews();
        this.mViewPager = rtlCompatibleViewPager;
        if (rtlCompatibleViewPager != null) {
            rtlCompatibleViewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }
}
