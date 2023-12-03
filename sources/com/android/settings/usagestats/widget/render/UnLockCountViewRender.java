package com.android.settings.usagestats.widget.render;

import android.content.Context;
import com.android.settings.R;
import com.android.settings.usagestats.model.DayDeviceUsageStats;

/* loaded from: classes2.dex */
public class UnLockCountViewRender extends DeviceUsageViewRender {
    public UnLockCountViewRender(Context context) {
        super(context);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected int getBarColor(int i) {
        if (this.isWeekData) {
            if (i == (isRtl() ? 0 : this.mDataSize - 1)) {
                return getColor(R.color.usage_stats_app_usage_bar_today);
            }
        }
        return getColor(R.color.usage_stats_app_usage_bar_unlock);
    }

    @Override // com.android.settings.usagestats.widget.render.DeviceUsageViewRender
    protected int getDeviceTypeCount(DayDeviceUsageStats dayDeviceUsageStats) {
        return dayDeviceUsageStats.getTotalUnlock();
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected void getTipTitle(int i) {
        if (this.isWeekData) {
            this.mTipTile = getString(R.string.usage_state_unlock_mourth_day, this.dateFormat.format(Long.valueOf(this.mWeekDeviceList.get(i).getDayInfo().dayBeginningTime)));
            return;
        }
        Context context = this.mContext;
        int i2 = R.string.usage_state_device_unlock_today_tip_title;
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(isRtl() ? (this.mDataSize - i) - 1 : i);
        objArr[1] = Integer.valueOf(isRtl() ? this.mDataSize - i : i + 1);
        this.mTipTile = context.getString(i2, objArr);
    }

    @Override // com.android.settings.usagestats.widget.render.BaseViewRender
    protected void getTipValue(int i) {
        if (this.isWeekData) {
            this.mTipValue = this.mContext.getResources().getQuantityString(R.plurals.usage_state_unlock_count, this.mWeekDeviceList.get(i).getTotalUnlock(), Integer.valueOf(this.mWeekDeviceList.get(i).getTotalUnlock()));
        } else {
            this.mTipValue = this.mContext.getResources().getQuantityString(R.plurals.usage_state_unlock_count, this.mOneDayList.get(i).intValue(), this.mOneDayList.get(i));
        }
    }

    @Override // com.android.settings.usagestats.widget.render.DeviceUsageViewRender
    protected int getTodayExplainId() {
        return R.plurals.usage_state_device_explain_today;
    }

    @Override // com.android.settings.usagestats.widget.render.DeviceUsageViewRender
    protected int getWeekExplainId() {
        return R.plurals.usage_state_device_explain_week;
    }
}
