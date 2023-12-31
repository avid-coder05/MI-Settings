package com.android.settings;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class UsageStatsActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private UsageStatsAdapter mAdapter;
    private LayoutInflater mInflater;
    private PackageManager mPm;
    private UsageStatsManager mUsageStatsManager;

    /* loaded from: classes.dex */
    public static class AppNameComparator implements Comparator<UsageStats> {
        private Map<String, String> mAppLabelList;

        AppNameComparator(Map<String, String> map) {
            this.mAppLabelList = map;
        }

        @Override // java.util.Comparator
        public final int compare(UsageStats usageStats, UsageStats usageStats2) {
            if (usageStats == null && usageStats2 == null) {
                return 0;
            }
            if (usageStats == null) {
                return -1;
            }
            if (usageStats2 == null) {
                return 1;
            }
            return this.mAppLabelList.get(usageStats.getPackageName()).compareTo(this.mAppLabelList.get(usageStats2.getPackageName()));
        }
    }

    /* loaded from: classes.dex */
    static class AppViewHolder {
        TextView lastTimeUsed;
        TextView pkgName;
        TextView usageTime;

        AppViewHolder() {
        }
    }

    /* loaded from: classes.dex */
    public static class LastTimeUsedComparator implements Comparator<UsageStats> {
        @Override // java.util.Comparator
        public final int compare(UsageStats usageStats, UsageStats usageStats2) {
            if (usageStats == null && usageStats2 == null) {
                return 0;
            }
            if (usageStats == null) {
                return 1;
            }
            if (usageStats2 == null) {
                return -1;
            }
            return Long.compare(usageStats2.getLastTimeUsed(), usageStats.getLastTimeUsed());
        }
    }

    /* loaded from: classes.dex */
    class UsageStatsAdapter extends BaseAdapter {
        private AppNameComparator mAppLabelComparator;
        private int mDisplayOrder = 0;
        private LastTimeUsedComparator mLastTimeUsedComparator = new LastTimeUsedComparator();
        private UsageTimeComparator mUsageTimeComparator = new UsageTimeComparator();
        private final ArrayMap<String, String> mAppLabelMap = new ArrayMap<>();
        private final ArrayList<UsageStats> mPackageStats = new ArrayList<>();

        UsageStatsAdapter() {
            Calendar calendar = Calendar.getInstance();
            calendar.add(6, -5);
            List<UsageStats> queryUsageStats = UsageStatsActivity.this.mUsageStatsManager.queryUsageStats(4, calendar.getTimeInMillis(), System.currentTimeMillis());
            if (queryUsageStats == null) {
                return;
            }
            ArrayMap arrayMap = new ArrayMap();
            int size = queryUsageStats.size();
            for (int i = 0; i < size; i++) {
                UsageStats usageStats = queryUsageStats.get(i);
                try {
                    this.mAppLabelMap.put(usageStats.getPackageName(), UsageStatsActivity.this.mPm.getApplicationInfo(usageStats.getPackageName(), 0).loadLabel(UsageStatsActivity.this.mPm).toString());
                    UsageStats usageStats2 = (UsageStats) arrayMap.get(usageStats.getPackageName());
                    if (usageStats2 == null) {
                        arrayMap.put(usageStats.getPackageName(), usageStats);
                    } else {
                        usageStats2.add(usageStats);
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
            this.mPackageStats.addAll(arrayMap.values());
            this.mAppLabelComparator = new AppNameComparator(this.mAppLabelMap);
            sortList();
        }

        private void sortList() {
            int i = this.mDisplayOrder;
            if (i == 0) {
                Collections.sort(this.mPackageStats, this.mUsageTimeComparator);
            } else if (i == 1) {
                Collections.sort(this.mPackageStats, this.mLastTimeUsedComparator);
            } else if (i == 2) {
                Collections.sort(this.mPackageStats, this.mAppLabelComparator);
            }
            notifyDataSetChanged();
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mPackageStats.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mPackageStats.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            AppViewHolder appViewHolder;
            if (view == null) {
                view = UsageStatsActivity.this.mInflater.inflate(R.layout.usage_stats_item, (ViewGroup) null);
                appViewHolder = new AppViewHolder();
                appViewHolder.pkgName = (TextView) view.findViewById(R.id.package_name);
                appViewHolder.lastTimeUsed = (TextView) view.findViewById(R.id.last_time_used);
                appViewHolder.usageTime = (TextView) view.findViewById(R.id.usage_time);
                view.setTag(appViewHolder);
            } else {
                appViewHolder = (AppViewHolder) view.getTag();
            }
            UsageStats usageStats = this.mPackageStats.get(i);
            if (usageStats != null) {
                appViewHolder.pkgName.setText(this.mAppLabelMap.get(usageStats.getPackageName()));
                appViewHolder.lastTimeUsed.setText(DateUtils.formatSameDayTime(usageStats.getLastTimeUsed(), System.currentTimeMillis(), 2, 2));
                appViewHolder.usageTime.setText(DateUtils.formatElapsedTime(usageStats.getTotalTimeInForeground() / 1000));
            } else {
                Log.w("UsageStatsActivity", "No usage stats info for package:" + i);
            }
            return view;
        }

        void sortList(int i) {
            if (this.mDisplayOrder == i) {
                return;
            }
            this.mDisplayOrder = i;
            sortList();
        }
    }

    /* loaded from: classes.dex */
    public static class UsageTimeComparator implements Comparator<UsageStats> {
        @Override // java.util.Comparator
        public final int compare(UsageStats usageStats, UsageStats usageStats2) {
            if (usageStats == null && usageStats2 == null) {
                return 0;
            }
            if (usageStats == null) {
                return 1;
            }
            if (usageStats2 == null) {
                return -1;
            }
            return Long.compare(usageStats2.getTotalTimeInForeground(), usageStats.getTotalTimeInForeground());
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.usage_stats);
        this.mUsageStatsManager = (UsageStatsManager) getSystemService("usagestats");
        this.mInflater = (LayoutInflater) getSystemService("layout_inflater");
        this.mPm = getPackageManager();
        ((Spinner) findViewById(R.id.typeSpinner)).setOnItemSelectedListener(this);
        ListView listView = (ListView) findViewById(R.id.pkg_list);
        UsageStatsAdapter usageStatsAdapter = new UsageStatsAdapter();
        this.mAdapter = usageStatsAdapter;
        listView.setAdapter((ListAdapter) usageStatsAdapter);
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        this.mAdapter.sortList(i);
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
