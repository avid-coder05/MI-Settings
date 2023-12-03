package com.android.settings.usagestats.widget.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.OriginalViewPager;
import androidx.viewpager.widget.PagerAdapter;
import com.android.settings.R;
import com.android.settings.usagestats.model.UsageFloorData;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.usagestats.utils.ControllerObserverUtil;
import com.android.settings.usagestats.widget.NoScrollViewPager;
import com.android.settings.usagestats.widget.UsageStatsPagerItem;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class PagerViewController extends BaseWidgetController {
    private boolean isRtl;
    private List<UsageStatsPagerItem> items;
    private NoScrollViewPager viewPager;

    /* loaded from: classes2.dex */
    static class MyPagerAdapter extends PagerAdapter {
        private List<UsageStatsPagerItem> list;

        MyPagerAdapter(List<UsageStatsPagerItem> list) {
            this.list = list;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            List<UsageStatsPagerItem> list = this.list;
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            UsageStatsPagerItem usageStatsPagerItem = this.list.get(i);
            viewGroup.addView(usageStatsPagerItem, new ViewGroup.LayoutParams(-1, -2));
            return usageStatsPagerItem;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return obj == view;
        }
    }

    public PagerViewController(Context context, UsageFloorData usageFloorData) {
        super(context, usageFloorData);
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void changeChannel(boolean z) {
        this.isWeekData = z;
        changeSelectedItem();
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void changeSelectedItem() {
        boolean z = this.isWeekData;
        int i = z;
        if (this.isRtl) {
            i = !z ? 1 : 0;
        }
        NoScrollViewPager noScrollViewPager = this.viewPager;
        if (noScrollViewPager != null) {
            noScrollViewPager.setCurrentItem(i);
        }
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void dealData() {
        this.isRtl = CommonUtils.isRtl();
        this.viewPager = (NoScrollViewPager) this.mView.findViewById(R.id.viewPager);
        this.items = new ArrayList();
        UsageStatsPagerItem usageStatsPagerItem = new UsageStatsPagerItem(this.mContext);
        usageStatsPagerItem.setIsWeekData(false);
        UsageStatsPagerItem usageStatsPagerItem2 = new UsageStatsPagerItem(this.mContext);
        usageStatsPagerItem2.setIsWeekData(true);
        if (this.isRtl) {
            this.items.add(usageStatsPagerItem2);
            this.items.add(usageStatsPagerItem);
        } else {
            this.items.add(usageStatsPagerItem);
            this.items.add(usageStatsPagerItem2);
        }
        this.viewPager.setAdapter(new MyPagerAdapter(this.items));
        if (this.isRtl) {
            this.viewPager.setCurrentItem(this.items.size() - 1, false);
        }
        this.viewPager.setOnPageChangeListener(new OriginalViewPager.SimpleOnPageChangeListener() { // from class: com.android.settings.usagestats.widget.controller.PagerViewController.1
            @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
                PagerViewController.this.viewPager.resize();
                ControllerObserverUtil controllerObserverUtil = ControllerObserverUtil.getInstance();
                boolean z = false;
                if (!PagerViewController.this.isRtl ? i == 1 : i == 0) {
                    z = true;
                }
                controllerObserverUtil.notify(Boolean.valueOf(z));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    public void release() {
        super.release();
        NoScrollViewPager noScrollViewPager = this.viewPager;
        if (noScrollViewPager != null) {
            noScrollViewPager.setOnPageChangeListener(null);
        }
    }
}
