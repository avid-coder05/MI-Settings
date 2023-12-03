package com.android.settings.usagestats;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.BaseFragment;
import com.android.settings.R;
import com.android.settings.usagestats.cache.DiskLruCacheUtils;
import com.android.settings.usagestats.holder.BaseHolder;
import com.android.settings.usagestats.holder.TabBarViewHolder;
import com.android.settings.usagestats.model.AppUsageListFloorData;
import com.android.settings.usagestats.model.AppUsageTotalTimeFloorData;
import com.android.settings.usagestats.model.DayAppUsageStats;
import com.android.settings.usagestats.model.DayInfo;
import com.android.settings.usagestats.model.DeviceUsageFloorData;
import com.android.settings.usagestats.model.UsageFloorData;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settings.usagestats.utils.AppUsageStatsFactory;
import com.android.settings.usagestats.utils.CacheUtils;
import com.android.settings.usagestats.utils.ControllerObserverUtil;
import com.android.settings.usagestats.utils.DateUtils;
import com.android.settings.usagestats.utils.DeviceUsageStatsFactory;
import com.android.settings.usagestats.widget.CustomListView;
import com.android.settings.usagestats.widget.controller.BaseWidgetController;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import miuix.androidbasewidget.widget.ProgressBar;

/* loaded from: classes2.dex */
public class UsageStatsMainFragment extends BaseFragment {
    private DayAppUsageStats mDayAppUsage;
    private List<UsageFloorData> mFloorDataList;
    private Handler mHandler;
    private CustomListView mListView;
    private View mLoadingContainer;
    private TextView mLoadingText;
    private ProgressBar mProgressBar;
    private FrameLayout mTabContainer;
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private BaseHolder tabHolder;
    private int mTapBarPosition = Integer.MAX_VALUE;
    private boolean isFirst = true;

    /* loaded from: classes2.dex */
    private static class DataDisposeHandler extends Handler {
        boolean isLoadAllTodayData;
        boolean isLoadDeviceTodayData;
        DayAppUsageStats mDayUsageStats;
        WeakReference<UsageStatsMainFragment> mWeakFragment;

        DataDisposeHandler(Looper looper, UsageStatsMainFragment usageStatsMainFragment) {
            super(looper);
            this.isLoadAllTodayData = false;
            this.isLoadDeviceTodayData = false;
            this.mWeakFragment = new WeakReference<>(usageStatsMainFragment);
        }

        private void loadUsageByInterval(UsageStatsMainFragment usageStatsMainFragment) {
            Context applicationContext = usageStatsMainFragment.getActivity().getApplicationContext();
            if (applicationContext == null) {
                Log.e("UsageStatsMainFragment", "loadUsageByInterval: context is null!");
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            long j = DateUtils.today();
            if (this.mDayUsageStats == null) {
                this.mDayUsageStats = new DayAppUsageStats(new DayInfo(null, j));
            }
            List<Long> timeList = AppUsageStatsFactory.getTimeList(applicationContext, currentTimeMillis, true);
            this.mDayUsageStats.getAppUsageStatsMap().clear();
            AppInfoUtils.rebuildResult(applicationContext, this.mDayUsageStats);
            int i = 0;
            for (int size = timeList.size(); i < size; size = size) {
                long longValue = timeList.get(i).longValue();
                AppUsageStatsFactory.loadUsageByEndTime(applicationContext, this.mDayUsageStats, longValue, AppInfoUtils.getCacheTime(applicationContext));
                AppInfoUtils.setCacheTime(applicationContext, longValue);
                this.mDayUsageStats.setTotalUsageTime(0L);
                this.mDayUsageStats.updateUsageStats();
                usageStatsMainFragment.notifyUpdateTodayData(this.mDayUsageStats);
                i++;
                timeList = timeList;
            }
            AppInfoUtils.serializeResult(applicationContext, this.mDayUsageStats.getAppUsageStatsMap());
            AppUsageStatsFactory.loadUsageByEndTime(applicationContext, this.mDayUsageStats, currentTimeMillis, AppInfoUtils.getCacheTime(applicationContext));
            AppUsageStatsFactory.filterUsageEventResult(applicationContext, j, currentTimeMillis, this.mDayUsageStats.getAppUsageStatsMap());
            this.mDayUsageStats.setTotalUsageTime(0L);
            this.mDayUsageStats.updateUsageStats();
            usageStatsMainFragment.notifyUpdateTodayData(this.mDayUsageStats);
            Slog.d("UsageStatsMainFragment", "loadTodayData:duration=" + (System.currentTimeMillis() - currentTimeMillis));
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            UsageStatsMainFragment usageStatsMainFragment = this.mWeakFragment.get();
            if (usageStatsMainFragment == null) {
                Log.e("UsageStatsMainFragment", "handleMessage: fragment is null!");
                return;
            }
            switch (message.what) {
                case 1:
                    usageStatsMainFragment.initFloorDataList();
                    return;
                case 2:
                    usageStatsMainFragment.collectEnterCount();
                    return;
                case 3:
                    usageStatsMainFragment.onResumeReport();
                    return;
                case 4:
                    usageStatsMainFragment.onPauseReport();
                    return;
                case 5:
                    usageStatsMainFragment.loadMonthData();
                    return;
                case 6:
                    if (this.isLoadAllTodayData || usageStatsMainFragment.getActivity() == null) {
                        return;
                    }
                    this.isLoadAllTodayData = true;
                    loadUsageByInterval(usageStatsMainFragment);
                    this.isLoadAllTodayData = false;
                    return;
                case 7:
                    if (this.isLoadDeviceTodayData || usageStatsMainFragment.getActivity() == null) {
                        return;
                    }
                    this.isLoadDeviceTodayData = true;
                    UsageFloorData.setmDeviceOneDayStats(DeviceUsageStatsFactory.loadDeviceUsageToday(usageStatsMainFragment.getActivity().getApplicationContext()));
                    usageStatsMainFragment.updateDeviceData();
                    this.isLoadDeviceTodayData = false;
                    return;
                case 8:
                    if (usageStatsMainFragment.getActivity() != null) {
                        DiskLruCacheUtils.getInstance(usageStatsMainFragment.getActivity().getApplicationContext()).close();
                        CacheUtils.clearIllegalData(usageStatsMainFragment.getActivity().getApplicationContext());
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void collectEnterCount() {
        try {
            if (getActivity() != null) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("settings_app_timer", 0);
                int i = sharedPreferences.getInt("usage_stats_main_count", 0) + 1;
                sharedPreferences.edit().putInt("usage_stats_main_count", i).commit();
                HashMap hashMap = new HashMap();
                hashMap.put("usage_stats_main_count", String.valueOf(i));
                OneTrackInterfaceUtils.track("usageStatsMainEnter", hashMap);
                Log.d("UsageStatsMainFragment", "collectEnterCount: enterCount=" + i);
            }
        } catch (Exception unused) {
            Log.d("UsageStatsMainFragment", "collectEnterCount: exception occurs");
        }
    }

    private String getLoadingText() {
        return getString(R.string.screen_paper_mode_apps_loading) + "...";
    }

    private String getName() {
        return UsageStatsMainFragment.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initFloorDataList() {
        if (getActivity() == null) {
            return;
        }
        FragmentActivity activity = getActivity();
        long currentTimeMillis = System.currentTimeMillis();
        this.mFloorDataList = new ArrayList();
        AppUsageTotalTimeFloorData appUsageTotalTimeFloorData = new AppUsageTotalTimeFloorData(1);
        appUsageTotalTimeFloorData.setWeek(true);
        appUsageTotalTimeFloorData.setDayAppUsageStatsWeekList(activity);
        this.mFloorDataList.add(appUsageTotalTimeFloorData);
        this.mFloorDataList.add(new UsageFloorData(7));
        this.mFloorDataList.add(new UsageFloorData(6));
        this.mFloorDataList.add(new UsageFloorData(7));
        int i = 0;
        this.mFloorDataList.add(new UsageFloorData(0));
        this.mFloorDataList.add(new UsageFloorData(2));
        this.mDayAppUsage = new DayAppUsageStats(new DayInfo(null, DateUtils.today()));
        AppUsageListFloorData.getFloorData().setmDayAppUsage(this.mDayAppUsage);
        AppUsageListFloorData.getFloorData().setDeviceUsageWeekList(activity);
        DeviceUsageFloorData.getDeviceUsageFloorData().setDeviceOneDayStats(activity);
        DeviceUsageFloorData.getDeviceUsageFloorData().setDeviceUsageWeekList(activity);
        this.mFloorDataList.add(new UsageFloorData(8));
        this.mFloorDataList.add(new UsageFloorData(0));
        this.mFloorDataList.add(new UsageFloorData(0));
        while (true) {
            if (i >= this.mFloorDataList.size()) {
                break;
            } else if (this.mFloorDataList.get(i).getFloorType() == 2) {
                this.mTapBarPosition = i;
                break;
            } else {
                i++;
            }
        }
        this.mHandler.post(new Runnable() { // from class: com.android.settings.usagestats.UsageStatsMainFragment.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    UsageStatsMainFragment.this.renderListView();
                    UsageStatsMainFragment.this.mThreadHandler.sendEmptyMessage(6);
                } catch (Exception e) {
                    Log.e("UsageStatsMainFragment", "run: ", e);
                }
            }
        });
        Log.d("UsageStatsMainFragment", "initFloorDataList: duration=" + (System.currentTimeMillis() - currentTimeMillis));
    }

    private boolean isShouldUpdateTodayUsage(DayAppUsageStats dayAppUsageStats) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadMonthData() {
        if (getActivity() == null) {
            return;
        }
        Context applicationContext = getActivity().getApplicationContext();
        for (UsageFloorData usageFloorData : this.mFloorDataList) {
            if (usageFloorData.getFloorType() == 1) {
                AppUsageTotalTimeFloorData appUsageTotalTimeFloorData = (AppUsageTotalTimeFloorData) usageFloorData;
                if (usageFloorData.isWeek()) {
                    appUsageTotalTimeFloorData.setmMouthAppUsageStatsList(AppUsageStatsFactory.loadUsageMonth(applicationContext, true));
                    return;
                } else {
                    appUsageTotalTimeFloorData.setDayAppUsageStatsWeekList(applicationContext);
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyUpdateTodayData(final DayAppUsageStats dayAppUsageStats) {
        if (isShouldUpdateTodayUsage(dayAppUsageStats)) {
            this.mHandler.post(new Runnable() { // from class: com.android.settings.usagestats.UsageStatsMainFragment.3
                @Override // java.lang.Runnable
                public void run() {
                    ControllerObserverUtil.getInstance().notify(dayAppUsageStats);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPauseReport() {
        if (TextUtils.isEmpty(getName())) {
            return;
        }
        try {
            MiStatInterfaceUtils.trackPageEnd(getName());
        } catch (IllegalStateException unused) {
            Log.d("UsageStatsMainFragment", "onPause: IllegalStateException occurs");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onResumeReport() {
        if (TextUtils.isEmpty(getName())) {
            return;
        }
        try {
            MiStatInterfaceUtils.trackPageStart(getName());
        } catch (IllegalStateException unused) {
            Log.d("UsageStatsMainFragment", "onResume: IllegalStateException occurs");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renderListView() {
        this.mListView.setAdapter((ListAdapter) new UsageStatsMainAdapter(getActivity(), this.mFloorDataList));
        this.mListView.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: com.android.settings.usagestats.UsageStatsMainFragment.2
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                if (i >= UsageStatsMainFragment.this.mTapBarPosition) {
                    UsageStatsMainFragment.this.mTabContainer.setVisibility(0);
                } else {
                    UsageStatsMainFragment.this.mTabContainer.setVisibility(8);
                }
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }
        });
        this.mLoadingContainer.setVisibility(8);
        this.mThreadHandler.sendEmptyMessage(5);
    }

    private void updateData() {
        this.mThreadHandler.sendEmptyMessageDelayed(7, 150L);
        this.mThreadHandler.sendEmptyMessageDelayed(6, 50L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDeviceData() {
        this.mHandler.post(new Runnable() { // from class: com.android.settings.usagestats.UsageStatsMainFragment.4
            @Override // java.lang.Runnable
            public void run() {
                ControllerObserverUtil.getInstance().notify("notify_device_usage_data");
            }
        });
    }

    @Override // com.android.settings.BaseFragment
    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.usagestats_main_fragment, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(1);
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mHandler = new Handler();
        HandlerThread handlerThread = new HandlerThread("Usage stats home page data mThread...");
        this.mThread = handlerThread;
        handlerThread.start();
        DataDisposeHandler dataDisposeHandler = new DataDisposeHandler(this.mThread.getLooper(), this);
        this.mThreadHandler = dataDisposeHandler;
        dataDisposeHandler.sendEmptyMessage(2);
        this.mThreadHandler.sendEmptyMessage(8);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        BaseWidgetController.renewWeekState();
        UsageFloorData.initAll();
        ControllerObserverUtil.getInstance().notify("notify_release");
        ControllerObserverUtil.getInstance().removeAllObserver();
        this.mHandler.removeCallbacksAndMessages(null);
        this.mThreadHandler.removeCallbacksAndMessages(null);
        this.mThread.quitSafely();
        CustomListView customListView = this.mListView;
        if (customListView != null) {
            customListView.setOnScrollListener(null);
        }
        DiskLruCacheUtils.getInstance(getActivity()).flush();
        DiskLruCacheUtils.getInstance(getActivity()).close();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mThreadHandler.sendEmptyMessage(4);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (!this.isFirst) {
            updateData();
        }
        this.isFirst = false;
        this.mThreadHandler.sendEmptyMessage(3);
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mTabContainer = (FrameLayout) view.findViewById(R.id.tab_container);
        if (this.tabHolder == null) {
            TabBarViewHolder tabBarViewHolder = new TabBarViewHolder(getActivity().getApplicationContext());
            this.tabHolder = tabBarViewHolder;
            tabBarViewHolder.renderView();
        }
        this.mTabContainer.addView(this.tabHolder.getmContentView(), new FrameLayout.LayoutParams(-1, -2));
        this.mTabContainer.setVisibility(8);
        this.mListView = (CustomListView) view.findViewById(R.id.usagestats_list_item);
        this.mProgressBar = (ProgressBar) view.findViewById(R.id.my_progressBar);
        this.mLoadingContainer = view.findViewById(R.id.loading_container);
        TextView textView = (TextView) view.findViewById(R.id.loading_text);
        this.mLoadingText = textView;
        textView.setText(getLoadingText());
        this.mThreadHandler.sendEmptyMessage(1);
    }
}
