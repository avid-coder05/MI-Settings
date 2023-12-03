package com.android.settings.usagestats.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.settings.R;
import com.android.settings.usagestats.holder.AppUsageListHolder;
import com.android.settings.usagestats.holder.UsageStatsHolder;
import com.android.settings.usagestats.model.AppUsageListFloorData;
import com.android.settings.usagestats.model.DeviceUsageFloorData;

/* loaded from: classes2.dex */
public class UsageStatsPagerItem extends LinearLayout {
    private boolean isWeek;
    private FrameLayout mUnlockContainer;
    private FrameLayout mUsageListContainer;

    public UsageStatsPagerItem(Context context) {
        super(context);
        init();
    }

    public UsageStatsPagerItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public UsageStatsPagerItem(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public UsageStatsPagerItem(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private void init() {
        setOrientation(1);
        LinearLayout.inflate(getContext(), R.layout.usagestats_pager_item, this);
        this.mUsageListContainer = (FrameLayout) findViewById(R.id.usagestats_list);
        this.mUnlockContainer = (FrameLayout) findViewById(R.id.devicestats_unlock);
    }

    private void renderView() {
        AppUsageListHolder appUsageListHolder = new AppUsageListHolder(getContext(), AppUsageListFloorData.getFloorData(), this.isWeek);
        UsageStatsHolder usageStatsHolder = new UsageStatsHolder(getContext(), 3, DeviceUsageFloorData.getDeviceUsageFloorData(), this.isWeek);
        appUsageListHolder.renderView();
        usageStatsHolder.renderView();
        this.mUsageListContainer.addView(appUsageListHolder.getmContentView(), new FrameLayout.LayoutParams(-1, -2));
        this.mUnlockContainer.addView(usageStatsHolder.getmContentView(), new FrameLayout.LayoutParams(-1, -2));
    }

    public void setIsWeekData(boolean z) {
        this.isWeek = z;
        renderView();
    }
}
