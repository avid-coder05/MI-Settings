package com.android.settings.usagestats.widget.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.usagestats.model.AppValueData;
import com.android.settings.usagestats.model.DayDeviceUsageStats;
import com.android.settings.usagestats.model.DeviceUsageFloorData;
import com.android.settings.usagestats.model.UsageFloorData;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settings.usagestats.widget.LevelBarView;
import com.android.settings.usagestats.widget.NewBarChartView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public class NotificationCountController extends BaseWidgetController {
    private static final String TAG = NotificationCountController.class.getSimpleName();
    private DeviceUsageFloorData deviceUsageFloorData;
    private LinearLayout mAppListContainer;
    private NewBarChartView mNewBarView;
    private List<AppValueData> mOneDayData;
    private long mOneDayMaxCount;
    private List<AppValueData> mWeekData;
    private long mWeekListMaxCount;

    public NotificationCountController(Context context, UsageFloorData usageFloorData) {
        super(context, usageFloorData);
    }

    private void addMoreItem() {
        View inflate = View.inflate(this.mContext, R.layout.usagestats_more, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        ((TextView) inflate.findViewById(R.id.tv_text)).setText(R.string.usage_state_notification_manager);
        this.mAppListContainer.addView(inflate, layoutParams);
        inflate.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.widget.controller.NotificationCountController.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AppInfoUtils.jumpToNotificationManager(NotificationCountController.this.mContext);
            }
        });
    }

    private void addView() {
        this.mAppListContainer.removeAllViews();
        List<AppValueData> list = this.isWeekData ? this.mWeekData : this.mOneDayData;
        if (list == null || list.isEmpty()) {
            addMoreItem();
            return;
        }
        int i = 0;
        for (final AppValueData appValueData : list) {
            if (i >= 4) {
                break;
            }
            View inflate = View.inflate(this.mContext, R.layout.widget_app_usage_item, null);
            if (renderView(inflate, appValueData)) {
                this.mAppListContainer.addView(inflate, new LinearLayout.LayoutParams(-1, -2));
                i++;
                inflate.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.widget.controller.NotificationCountController.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        Intent intent = new Intent("android.intent.action.MAIN");
                        intent.setClassName("com.android.settings", "com.android.settings.Settings$NotificationFilterActivity");
                        intent.putExtra("appName", AppInfoUtils.getAppName(NotificationCountController.this.mContext, appValueData.getPackageName()));
                        intent.putExtra("packageName", appValueData.getPackageName());
                        if (!(NotificationCountController.this.mContext instanceof Activity)) {
                            intent.addFlags(268435456);
                        }
                        NotificationCountController.this.mContext.startActivity(intent);
                    }
                });
            }
        }
        addMoreItem();
    }

    private void dealOneDayData() {
        DayDeviceUsageStats deviceOneDayStats = this.deviceUsageFloorData.getDeviceOneDayStats();
        this.mNewBarView.setOneDayDataList(deviceOneDayStats.getNotificationList());
        this.mNewBarView.setWeekData(false);
        if (this.mOneDayData.isEmpty()) {
            resetOneDayData(deviceOneDayStats);
        }
        addView();
    }

    private void dealWeekData() {
        List<DayDeviceUsageStats> deviceUsageWeekList = this.deviceUsageFloorData.getDeviceUsageWeekList();
        this.mNewBarView.setDeviceUsageList(deviceUsageWeekList);
        this.mNewBarView.setWeekData(true);
        if (this.mWeekData.isEmpty()) {
            resetWeekData(deviceUsageWeekList);
        }
        addView();
    }

    private boolean renderView(View view, AppValueData appValueData) {
        if (appValueData.getValue() <= 0) {
            return false;
        }
        try {
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_app_icon);
            TextView textView = (TextView) view.findViewById(R.id.tv_app_name);
            LevelBarView levelBarView = (LevelBarView) view.findViewById(R.id.seekbar_app_usage_time);
            TextView textView2 = (TextView) view.findViewById(R.id.tv_app_usage_time);
            imageView.setImageDrawable(AppInfoUtils.getAppLaunchIcon(this.mContext, appValueData.getPackageName()));
            textView.setText(AppInfoUtils.getAppName(this.mContext, appValueData.getPackageName()));
            textView2.setText(this.mContext.getResources().getQuantityString(R.plurals.usage_state_noti_count, (int) appValueData.getValue(), Integer.valueOf((int) appValueData.getValue())));
            levelBarView.setIsNoti(true);
            levelBarView.setMaxLevel(this.isWeekData ? this.mWeekListMaxCount : this.mOneDayMaxCount);
            levelBarView.setCurrentLevel(appValueData.getValue());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "renderView: wow render fail", e);
            return false;
        }
    }

    private void resetData() {
        if (this.isWeekData) {
            this.deviceUsageFloorData.setDeviceUsageWeekList(this.mContext.getApplicationContext());
            if (this.mWeekData == null) {
                this.mWeekData = new ArrayList();
            }
            dealWeekData();
            return;
        }
        this.deviceUsageFloorData.setDeviceOneDayStats(this.mContext.getApplicationContext());
        if (this.mOneDayData == null) {
            this.mOneDayData = new ArrayList();
        }
        dealOneDayData();
    }

    private void resetOneDayData(DayDeviceUsageStats dayDeviceUsageStats) {
        this.mOneDayData.clear();
        if (dayDeviceUsageStats == null || dayDeviceUsageStats.getAppNotificationStatsMap() == null) {
            return;
        }
        for (Map.Entry<String, DayDeviceUsageStats.AppNotificationStats> entry : dayDeviceUsageStats.getAppNotificationStatsMap().entrySet()) {
            if (entry.getValue() == null || entry.getValue().getReceivedNotificationCount() < 0) {
                break;
            }
            AppValueData appValueData = new AppValueData();
            appValueData.setPackageName(entry.getKey());
            appValueData.setValue(entry.getValue().getReceivedNotificationCount());
            this.mOneDayData.add(appValueData);
        }
        Collections.sort(this.mOneDayData);
        if (this.mOneDayData.isEmpty()) {
            return;
        }
        this.mOneDayMaxCount = this.mOneDayData.get(0).getValue();
    }

    private void resetWeekData(List<DayDeviceUsageStats> list) {
        this.mWeekData.clear();
        HashMap hashMap = new HashMap();
        if (list.isEmpty()) {
            return;
        }
        Iterator<DayDeviceUsageStats> it = list.iterator();
        while (it.hasNext()) {
            ArrayMap<String, DayDeviceUsageStats.AppNotificationStats> appNotificationStatsMap = it.next().getAppNotificationStatsMap();
            if (appNotificationStatsMap != null && appNotificationStatsMap.entrySet() != null) {
                for (Map.Entry<String, DayDeviceUsageStats.AppNotificationStats> entry : appNotificationStatsMap.entrySet()) {
                    String key = entry.getKey();
                    if (entry.getValue() == null) {
                        break;
                    }
                    long receivedNotificationCount = entry.getValue().getReceivedNotificationCount();
                    if (hashMap.containsKey(key)) {
                        AppValueData appValueData = (AppValueData) hashMap.get(key);
                        appValueData.setValue(appValueData.getValue() + receivedNotificationCount);
                    } else {
                        AppValueData appValueData2 = new AppValueData();
                        appValueData2.setPackageName(key);
                        appValueData2.setValue(receivedNotificationCount);
                        hashMap.put(key, appValueData2);
                    }
                }
            }
        }
        this.mWeekData.addAll(hashMap.values());
        Collections.sort(this.mWeekData);
        if (this.mWeekData.isEmpty()) {
            return;
        }
        this.mWeekListMaxCount = this.mWeekData.get(0).getValue();
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void dealData() {
        this.mNewBarView = (NewBarChartView) this.mView.findViewById(R.id.device_usage_bar_view);
        LinearLayout linearLayout = (LinearLayout) this.mView.findViewById(R.id.ll_app_notification_list_container);
        this.mAppListContainer = linearLayout;
        linearLayout.setVisibility(0);
        this.deviceUsageFloorData = (DeviceUsageFloorData) this.mFloorData;
        this.mNewBarView.setBarType(2);
        resetData();
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
        List<AppValueData> list = this.mWeekData;
        if (list != null) {
            list.clear();
        }
        List<AppValueData> list2 = this.mOneDayData;
        if (list2 != null) {
            list2.clear();
        }
        resetData();
    }
}
