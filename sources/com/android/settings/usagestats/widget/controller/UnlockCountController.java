package com.android.settings.usagestats.widget.controller;

import android.content.Context;
import com.android.settings.R;
import com.android.settings.usagestats.model.DeviceUsageFloorData;
import com.android.settings.usagestats.model.UsageFloorData;
import com.android.settings.usagestats.widget.NewBarChartView;

/* loaded from: classes2.dex */
public class UnlockCountController extends BaseWidgetController {
    private DeviceUsageFloorData deviceUsageFloorData;
    private NewBarChartView mNewBarView;

    public UnlockCountController(Context context, UsageFloorData usageFloorData) {
        super(context, usageFloorData);
    }

    private void changeData() {
        resetData();
        if (this.isWeekData) {
            this.mNewBarView.setDeviceUsageList(this.deviceUsageFloorData.getDeviceUsageWeekList());
        } else {
            this.mNewBarView.setOneDayDataList(this.deviceUsageFloorData.getDeviceOneDayStats().getUnlockList());
        }
        this.mNewBarView.setWeekData(this.isWeekData);
    }

    private void resetData() {
        if (this.isWeekData) {
            this.deviceUsageFloorData.setDeviceUsageWeekList(this.mContext.getApplicationContext());
        } else {
            this.deviceUsageFloorData.setDeviceOneDayStats(this.mContext.getApplicationContext());
        }
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void dealData() {
        NewBarChartView newBarChartView = (NewBarChartView) this.mView.findViewById(R.id.device_usage_bar_view);
        this.mNewBarView = newBarChartView;
        this.deviceUsageFloorData = (DeviceUsageFloorData) this.mFloorData;
        newBarChartView.setBarType(3);
        changeData();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    public void release() {
        NewBarChartView newBarChartView = this.mNewBarView;
        if (newBarChartView != null) {
            newBarChartView.release();
        }
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void updateDeviceMsg() {
        changeData();
    }
}
