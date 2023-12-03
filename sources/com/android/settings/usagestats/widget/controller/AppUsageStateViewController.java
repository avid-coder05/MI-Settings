package com.android.settings.usagestats.widget.controller;

import android.app.UiModeManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.usagestats.model.AppUsageTotalTimeFloorData;
import com.android.settings.usagestats.model.DayAppUsageStats;
import com.android.settings.usagestats.model.UsageFloorData;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settings.usagestats.utils.AppUsageStatsFactory;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.usagestats.widget.NewBarChartView;
import java.util.List;
import miui.content.res.ThemeResources;

/* loaded from: classes2.dex */
public class AppUsageStateViewController extends BaseWidgetController {
    private boolean isWeek;
    private NewBarChartView mBarChartView;
    private TextView mChannelSelect;
    private View mHourText;
    private TextView mHourTv;
    private View mMenuPoint;
    private View mMinuteText;
    private TextView mMinuteTv;
    private int mSelectColor;
    private int mUnSelectColor;
    private AppUsageTotalTimeFloorData usageTotalTimeFloorData;

    public AppUsageStateViewController(Context context, UsageFloorData usageFloorData) {
        super(context, usageFloorData);
        this.isWeek = true;
    }

    private boolean isDarkModeEnable(Context context) {
        return ((UiModeManager) context.getSystemService(UiModeManager.class)).getNightMode() == 2;
    }

    private void setData() {
        this.usageTotalTimeFloorData.setWeek(this.isWeek);
        if (!this.isWeek && this.usageTotalTimeFloorData.getmMouthAppUsageStatsList() == null) {
            this.usageTotalTimeFloorData.setmMouthAppUsageStatsList(AppUsageStatsFactory.loadUsageMonth(this.mContext.getApplicationContext(), true));
        } else if (this.isWeek && this.usageTotalTimeFloorData.getDayAppUsageStatsWeekList() == null) {
            this.usageTotalTimeFloorData.setDayAppUsageStatsWeekList(this.mContext.getApplicationContext());
        }
        List<DayAppUsageStats> dayAppUsageStatsWeekList = this.isWeek ? this.usageTotalTimeFloorData.getDayAppUsageStatsWeekList() : this.usageTotalTimeFloorData.getmMouthAppUsageStatsList();
        if (BaseWidgetController.mTodayAppUsageStats != null) {
            dayAppUsageStatsWeekList.remove(dayAppUsageStatsWeekList.size() - 1);
            dayAppUsageStatsWeekList.add(BaseWidgetController.mTodayAppUsageStats);
        }
        this.mHourTv.setText(AppInfoUtils.formatTime(this.mContext, dayAppUsageStatsWeekList.get(dayAppUsageStatsWeekList.size() - 1).getTotalUsageTime()));
        this.mBarChartView.setAppUsageList(this.isWeek ? this.usageTotalTimeFloorData.getDayAppUsageStatsWeekList() : this.usageTotalTimeFloorData.getmMouthAppUsageStatsList());
        this.mBarChartView.setWeekData(this.isWeek);
        this.mChannelSelect.setVisibility(0);
        this.mChannelSelect.setText(this.isWeek ? R.string.usage_state_channel_week : R.string.usage_state_channel_mouth);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPopupWindow() {
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void dealData() {
        this.mSelectColor = CommonUtils.queryColor(this.mContext, "progressPrimaryColor", ThemeResources.MIUI_PACKAGE);
        this.mUnSelectColor = isDarkModeEnable(this.mContext) ? this.mContext.getColor(R.color.usage_stats_black40) : CommonUtils.queryColor(this.mContext, "immersion_text_color_normal_light", ThemeResources.MIUI_PACKAGE);
        this.mBarChartView = (NewBarChartView) this.mView.findViewById(R.id.app_usage_bar_view);
        this.mChannelSelect = (TextView) this.mView.findViewById(R.id.tv_app_usage_mouth_channel);
        this.mMenuPoint = this.mView.findViewById(R.id.menu_point);
        this.mHourTv = (TextView) this.mView.findViewById(R.id.tv_app_usage_hour);
        this.mHourText = this.mView.findViewById(R.id.tv_app_usage_hour_text);
        this.mMinuteTv = (TextView) this.mView.findViewById(R.id.tv_app_usage_miunte);
        this.mMinuteText = this.mView.findViewById(R.id.tv_app_usage_miunte_text);
        this.usageTotalTimeFloorData = (AppUsageTotalTimeFloorData) this.mFloorData;
        this.mBarChartView.setBarType(1);
        this.mChannelSelect.setTextColor(AppInfoUtils.getColor(this.mContext, R.color.usage_stats_app_usage_text_select));
        Drawable drawable = this.mContext.getDrawable(R.drawable.ic_arrow_down);
        drawable.setBounds(0, 0, MiuiUtils.dp2px(this.mContext, 4.5f), MiuiUtils.dp2px(this.mContext, 4.5f));
        this.mChannelSelect.setCompoundDrawablePadding(MiuiUtils.dp2px(this.mContext, 4.48f));
        if (CommonUtils.isRtl()) {
            this.mChannelSelect.setCompoundDrawables(drawable, null, null, null);
        } else {
            this.mChannelSelect.setCompoundDrawables(null, null, drawable, null);
        }
        setData();
        this.mChannelSelect.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.widget.controller.AppUsageStateViewController.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AppUsageStateViewController.this.showPopupWindow();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    public void release() {
        NewBarChartView newBarChartView = this.mBarChartView;
        if (newBarChartView != null) {
            newBarChartView.release();
        }
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void updateAllData() {
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void updateTodayAppUsageData(DayAppUsageStats dayAppUsageStats) {
        setData();
    }
}
