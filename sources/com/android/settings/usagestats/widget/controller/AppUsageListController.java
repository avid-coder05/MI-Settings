package com.android.settings.usagestats.widget.controller;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.usagestats.holder.AppInfoItemHolder;
import com.android.settings.usagestats.model.AppUsageListFloorData;
import com.android.settings.usagestats.model.AppUsageStats;
import com.android.settings.usagestats.model.AppValueData;
import com.android.settings.usagestats.model.DayAppUsageStats;
import com.android.settings.usagestats.model.UsageFloorData;
import com.android.settings.usagestats.utils.AppUsageStatsFactory;
import com.android.settings.usagestats.utils.UsageStatsUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public class AppUsageListController extends BaseWidgetController {
    public static ArrayList<AppValueData> mOneDayData;
    public static ArrayList<AppValueData> mWeekData;
    private AppUsageListFloorData mAppUsageFloorData;
    private LinearLayout mListContainer;
    private long mOneDayMaxCount;
    private TextView mTitle;
    private long mWeekMaxCount;
    private static final String TAG = AppUsageListController.class.getSimpleName();
    public static HashMap<String, ArrayList<AppValueData>> mPkAppWeekList = new HashMap<>();

    public AppUsageListController(Context context, UsageFloorData usageFloorData) {
        super(context, usageFloorData);
    }

    private void addMoreItem() {
        View inflate = View.inflate(this.mContext, R.layout.usagestats_more, null);
        this.mListContainer.addView(inflate, new LinearLayout.LayoutParams(-1, -2));
        inflate.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.widget.controller.AppUsageListController.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                AppUsageListController appUsageListController = AppUsageListController.this;
                bundle.putLong("maxValue", appUsageListController.isWeekData ? appUsageListController.mWeekMaxCount : appUsageListController.mOneDayMaxCount);
                bundle.putBoolean("isWeek", AppUsageListController.this.isWeekData);
                new SubSettingLauncher(AppUsageListController.this.mContext).setDestination("com.android.settings.usagestats.UsageAppListFragment").setTitleRes(R.string.usage_state_app_use_time).setArguments(bundle).setResultListener(null, 0).launch();
            }
        });
    }

    private void addView() {
        this.mListContainer.removeAllViews();
        this.mTitle.setText(this.isWeekData ? R.string.usage_state_app_usage_list_week_title : R.string.usage_state_app_usage_list_title);
        ArrayList<AppValueData> arrayList = this.isWeekData ? mWeekData : mOneDayData;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        int i = 0;
        for (final AppValueData appValueData : arrayList) {
            if (i >= 4) {
                addMoreItem();
                return;
            }
            View renderView = renderView(appValueData);
            if (renderView != null) {
                this.mListContainer.addView(renderView, new LinearLayout.LayoutParams(-1, -2));
                renderView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.widget.controller.AppUsageListController.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isWeek", AppUsageListController.this.isWeekData);
                        bundle.putString("packageName", appValueData.getPackageName());
                        new SubSettingLauncher(AppUsageListController.this.mContext).setDestination("com.android.settings.usagestats.UsageStatsAppUsageDetailFragment").setTitleRes(R.string.usage_state_app_usage_detail_title).setArguments(bundle).setResultListener(null, 0).launch();
                    }
                });
                i++;
            }
        }
    }

    private void dealOneDayData() {
        ArrayList<AppValueData> arrayList = mOneDayData;
        if (arrayList != null) {
            arrayList.clear();
        }
        DayAppUsageStats dayAppUsageStats = this.mAppUsageFloorData.getmDayAppUsage();
        if (dayAppUsageStats == null || dayAppUsageStats.getAppUsageStatsMap() == null) {
            return;
        }
        handlerStats(dayAppUsageStats);
        ArrayList<AppValueData> arrayList2 = mOneDayData;
        if (arrayList2 == null) {
            return;
        }
        Collections.sort(arrayList2);
        if (mOneDayData.isEmpty()) {
            return;
        }
        this.mOneDayMaxCount = mOneDayData.get(0).getValue();
    }

    private void dealWeekData() {
        ArrayList<AppValueData> arrayList = mWeekData;
        if (arrayList != null) {
            arrayList.clear();
        }
        HashMap hashMap = new HashMap();
        UsageStatsUtils.dealAppUsageWeekList(mPkAppWeekList, this.mAppUsageFloorData, hashMap);
        if (mWeekData == null || hashMap.values() == null) {
            return;
        }
        mWeekData.addAll(hashMap.values());
        Collections.sort(mWeekData);
        if (mWeekData.isEmpty()) {
            return;
        }
        this.mWeekMaxCount = mWeekData.get(0).getValue();
    }

    private void handlerStats(DayAppUsageStats dayAppUsageStats) {
        for (Map.Entry<String, AppUsageStats> entry : dayAppUsageStats.getAppUsageStatsMap().entrySet()) {
            if (mOneDayData == null) {
                return;
            }
            AppUsageStats value = entry.getValue();
            if (value != null && value.getTotalForegroundTime() >= 0) {
                AppValueData appValueData = new AppValueData();
                appValueData.setPackageName(entry.getKey());
                appValueData.setValue(value.getTotalForegroundTime());
                mOneDayData.add(appValueData);
            }
        }
    }

    private void reDealData() {
        resetData();
        addView();
    }

    private View renderView(AppValueData appValueData) {
        try {
            AppInfoItemHolder appInfoItemHolder = new AppInfoItemHolder(this.mContext);
            appInfoItemHolder.setmValueData(appValueData);
            appInfoItemHolder.setmMaxCount(this.isWeekData ? this.mWeekMaxCount : this.mOneDayMaxCount);
            appInfoItemHolder.renderView();
            return appInfoItemHolder.getmContentView();
        } catch (Exception e) {
            Log.e(TAG, "renderView: wow render fail", e);
            return null;
        }
    }

    private void resetData() {
        if (!this.isWeekData) {
            if (this.mAppUsageFloorData.getmDayAppUsage() == null) {
                this.mAppUsageFloorData.setmDayAppUsage(AppUsageStatsFactory.loadUsageToday(this.mContext.getApplicationContext(), true));
            }
            DayAppUsageStats dayAppUsageStats = BaseWidgetController.mTodayAppUsageStats;
            if (dayAppUsageStats != null) {
                this.mAppUsageFloorData.setmDayAppUsage(dayAppUsageStats);
            }
            if (mOneDayData == null) {
                mOneDayData = new ArrayList<>();
            }
            dealOneDayData();
            return;
        }
        if (this.mAppUsageFloorData.getDayAppUsageStatsWeekList() == null) {
            this.mAppUsageFloorData.setDayAppUsageStatsWeekList(this.mContext.getApplicationContext());
        }
        if (BaseWidgetController.mTodayAppUsageStats != null) {
            List<DayAppUsageStats> dayAppUsageStatsWeekList = this.mAppUsageFloorData.getDayAppUsageStatsWeekList();
            dayAppUsageStatsWeekList.remove(dayAppUsageStatsWeekList.size() - 1);
            dayAppUsageStatsWeekList.add(BaseWidgetController.mTodayAppUsageStats);
        }
        if (mWeekData == null) {
            mWeekData = new ArrayList<>();
        }
        dealWeekData();
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void dealData() {
        this.mListContainer = (LinearLayout) this.mView.findViewById(R.id.ll_app_usage_list_container);
        this.mTitle = (TextView) this.mView.findViewById(R.id.text_total_time);
        this.mAppUsageFloorData = (AppUsageListFloorData) this.mFloorData;
        resetData();
        addView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    public void release() {
        super.release();
        HashMap<String, ArrayList<AppValueData>> hashMap = mPkAppWeekList;
        if (hashMap != null) {
            hashMap.clear();
        }
        ArrayList<AppValueData> arrayList = mOneDayData;
        if (arrayList != null) {
            arrayList.clear();
            mOneDayData = null;
        }
        ArrayList<AppValueData> arrayList2 = mWeekData;
        if (arrayList2 != null) {
            arrayList2.clear();
            mWeekData = null;
        }
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void updateAllData() {
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void updateTodayAppUsageData(DayAppUsageStats dayAppUsageStats) {
        reDealData();
    }
}
