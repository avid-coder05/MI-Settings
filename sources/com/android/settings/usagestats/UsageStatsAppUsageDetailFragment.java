package com.android.settings.usagestats;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.BaseFragment;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.usagestats.holder.AppTimeLimitSetHolder;
import com.android.settings.usagestats.model.AppUsageListFloorData;
import com.android.settings.usagestats.model.AppUsageStats;
import com.android.settings.usagestats.model.AppValueData;
import com.android.settings.usagestats.model.DayInfo;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settings.usagestats.utils.AppUsageStatsFactory;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.usagestats.utils.DateUtils;
import com.android.settings.usagestats.utils.UsageStatsUtils;
import com.android.settings.usagestats.widget.NewBarChartView;
import com.android.settings.usagestats.widget.controller.AppUsageListController;
import com.android.settingslib.util.MiStatInterfaceUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;
import miui.process.ProcessManager;

/* loaded from: classes2.dex */
public class UsageStatsAppUsageDetailFragment extends BaseFragment {
    private static final String TAG = UsageStatsAppUsageDetailFragment.class.getSimpleName();
    private AppTimeLimitSetHolder holder;
    private boolean isFromNotification;
    private boolean isWeekData;
    private AppUsageListFloorData mAppUsageFloorData;
    private NewBarChartView mBarView;
    private long mEnterTime;
    private String mForegroundPkg;
    private String mFromPager;
    private ImageView mIvAppIcon;
    private FrameLayout mLimitContainer;
    private String mPkName;
    private TextView mTvAppName;
    private TextView mTvTimeDuration;
    private ArrayList<AppValueData> mWeekData;
    public HashMap<String, ArrayList<AppValueData>> mPkAppWeekList = new HashMap<>();
    private IForegroundInfoListener.Stub mAppObserver = new IForegroundInfoListener.Stub() { // from class: com.android.settings.usagestats.UsageStatsAppUsageDetailFragment.1
        public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) {
            Log.d(UsageStatsAppUsageDetailFragment.TAG, "onForegroundInfoChanged: " + foregroundInfo.mForegroundPackageName);
            UsageStatsAppUsageDetailFragment.this.mForegroundPkg = foregroundInfo.mForegroundPackageName;
        }
    };

    private void addLimitItem(int i) {
        if (getActivity() == null || MiuiUtils.isSecondSpace(getActivity())) {
            return;
        }
        AppTimeLimitSetHolder appTimeLimitSetHolder = new AppTimeLimitSetHolder(getActivity(), this.mPkName);
        this.holder = appTimeLimitSetHolder;
        appTimeLimitSetHolder.setFromPager(this.mFromPager);
        this.holder.setTodayUsageTime(i);
        this.holder.renderView();
        this.mLimitContainer.addView(this.holder.getmContentView(), new FrameLayout.LayoutParams(-1, -2));
    }

    private void dealWeekData(HashMap<String, ArrayList<AppValueData>> hashMap, AppUsageListFloorData appUsageListFloorData) {
        UsageStatsUtils.dealAppUsageWeekList(hashMap, appUsageListFloorData, null);
    }

    private void ensureUpdateTodayTime() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.mEnterTime;
        if (j == 0) {
            this.mEnterTime = currentTimeMillis;
        } else if (currentTimeMillis - j >= DateUtils.INTERVAL_MINUTE) {
            Log.d(TAG, "ensureUpdateTodayTime: updateTime");
            if (!this.isWeekData) {
                List<AppUsageStats> loadUsageToday = AppUsageStatsFactory.loadUsageToday(getActivity(), this.mPkName);
                this.mBarView.setOneAppOneDayList(loadUsageToday);
                updateHolderTodayTime(getTodayUsageTime(loadUsageToday));
                return;
            }
            ArrayList<AppValueData> arrayList = this.mWeekData;
            if (arrayList == null || arrayList.size() == 0) {
                return;
            }
            long loadTodayTotalTimeForPackage = AppUsageStatsFactory.loadTodayTotalTimeForPackage(getActivity(), this.mPkName, DateUtils.today(), currentTimeMillis);
            if (DateUtils.isInSameDay(currentTimeMillis, this.mEnterTime)) {
                this.mWeekData.get(r2.size() - 1).setValue(loadTodayTotalTimeForPackage);
            } else {
                AppValueData appValueData = new AppValueData();
                appValueData.setDayInfo(new DayInfo(null, DateUtils.today()));
                appValueData.setPackageName(this.mPkName);
                appValueData.setValue(loadTodayTotalTimeForPackage);
                this.mWeekData.remove(0);
                this.mWeekData.add(appValueData);
            }
            this.mBarView.setOneAppWeekList(this.mWeekData);
            updateHolderTodayTime((int) (loadTodayTotalTimeForPackage / DateUtils.INTERVAL_MINUTE));
        }
    }

    private String getName() {
        return UsageStatsAppUsageDetailFragment.class.getName();
    }

    private int getTodayUsageTime(List<AppUsageStats> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        long j = 0;
        Iterator<AppUsageStats> it = list.iterator();
        while (it.hasNext()) {
            j += it.next().getTotalForegroundTime();
        }
        return (int) (j / DateUtils.INTERVAL_MINUTE);
    }

    private void initData() {
        Bundle arguments = getArguments();
        this.mPkName = arguments.getString("packageName");
        this.mFromPager = arguments.getString("fromPager", "");
        this.isFromNotification = arguments.getBoolean("fromNotification");
        if (TextUtils.isEmpty(this.mPkName)) {
            finish();
            return;
        }
        if (this.isFromNotification) {
            ProcessManager.registerForegroundInfoListener(this.mAppObserver);
        }
        boolean z = arguments.getBoolean("isWeek", false);
        this.isWeekData = z;
        if (z) {
            this.mTvTimeDuration.setText(R.string.usage_state_week_total_text);
            HashMap<String, ArrayList<AppValueData>> hashMap = AppUsageListController.mPkAppWeekList;
            if (hashMap == null || hashMap.isEmpty()) {
                AppUsageListFloorData.getFloorData().setDeviceUsageWeekList(getActivity());
                AppUsageListFloorData floorData = AppUsageListFloorData.getFloorData();
                this.mAppUsageFloorData = floorData;
                dealWeekData(this.mPkAppWeekList, floorData);
                this.mWeekData = this.mPkAppWeekList.get(this.mPkName);
            } else {
                this.mWeekData = AppUsageListController.mPkAppWeekList.get(this.mPkName);
            }
        }
        renderView(arguments);
    }

    private void initView(View view) {
        this.mTvAppName = (TextView) view.findViewById(R.id.tv_app_name);
        this.mIvAppIcon = (ImageView) view.findViewById(R.id.iv_detail_icon);
        this.mBarView = (NewBarChartView) view.findViewById(R.id.bar_app_usage_detail);
        this.mTvTimeDuration = (TextView) view.findViewById(R.id.tv_usage_time);
        this.mLimitContainer = (FrameLayout) view.findViewById(R.id.fl_limit_container);
    }

    private int loadTodayData() {
        List<AppUsageStats> loadUsageToday = AppUsageStatsFactory.loadUsageToday(getContext().getApplicationContext(), this.mPkName);
        this.mBarView.setOneAppOneDayList(loadUsageToday);
        return getTodayUsageTime(loadUsageToday);
    }

    private void renderView(Bundle bundle) {
        int loadTodayData;
        long j;
        this.mTvAppName.setText(AppInfoUtils.getAppName(getContext().getApplicationContext(), this.mPkName));
        this.mIvAppIcon.setImageDrawable(AppInfoUtils.getAppLaunchIcon(getContext().getApplicationContext(), this.mPkName));
        this.mBarView.setBarType(4);
        if (this.isWeekData) {
            this.mBarView.setOneAppWeekList(this.mWeekData);
            j = this.mWeekData.get(r5.size() - 1).getValue() / DateUtils.INTERVAL_MINUTE;
        } else if (!bundle.getBoolean("hasTime", false)) {
            loadTodayData = loadTodayData();
            this.mBarView.setWeekData(this.isWeekData);
            addLimitItem(loadTodayData);
        } else {
            this.mBarView.setOneAppOneDayList((ArrayList) bundle.getSerializable("usageList"));
            j = bundle.getLong("usageTime") / DateUtils.INTERVAL_MINUTE;
        }
        loadTodayData = (int) j;
        this.mBarView.setWeekData(this.isWeekData);
        addLimitItem(loadTodayData);
    }

    private void updateHolderTodayTime(int i) {
        AppTimeLimitSetHolder appTimeLimitSetHolder = this.holder;
        if (appTimeLimitSetHolder != null) {
            appTimeLimitSetHolder.setTodayUsageTime(i);
        }
    }

    @Override // com.android.settings.BaseFragment
    public void finish() {
        super.finish();
        if (getActivity() != null) {
            Pair<Integer, Integer> systemDefaultExitAnim = CommonUtils.getSystemDefaultExitAnim(getActivity());
            getActivity().overridePendingTransition(((Integer) systemDefaultExitAnim.first).intValue(), ((Integer) systemDefaultExitAnim.second).intValue());
        }
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
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        NewBarChartView newBarChartView = this.mBarView;
        if (newBarChartView != null) {
            newBarChartView.release();
        }
        AppTimeLimitSetHolder appTimeLimitSetHolder = this.holder;
        if (appTimeLimitSetHolder != null) {
            appTimeLimitSetHolder.onDestroy();
        }
        if (this.isFromNotification) {
            ProcessManager.unregisterForegroundInfoListener(this.mAppObserver);
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.usagestats_app_usage_detail, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (TextUtils.isEmpty(getName())) {
            return;
        }
        try {
            MiStatInterfaceUtils.trackPageEnd(getName());
        } catch (IllegalStateException unused) {
            Log.d(TAG, "onPause: IllegalStateException occurs ");
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(getName())) {
            try {
                MiStatInterfaceUtils.trackPageStart(getName());
                ensureUpdateTodayTime();
            } catch (Exception unused) {
                Log.d(TAG, "onResume: IllegalStateException occurs ");
            }
        }
        AppTimeLimitSetHolder appTimeLimitSetHolder = this.holder;
        if (appTimeLimitSetHolder != null) {
            appTimeLimitSetHolder.onResume();
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        AppTimeLimitSetHolder appTimeLimitSetHolder = this.holder;
        if (appTimeLimitSetHolder != null) {
            appTimeLimitSetHolder.onStop();
        }
        if (this.isFromNotification && "com.miui.home".equals(this.mForegroundPkg)) {
            finish();
        }
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        if (getArguments() == null) {
            getActivity().finish();
            return;
        }
        initView(view);
        initData();
    }
}
