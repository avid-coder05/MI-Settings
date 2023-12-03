package com.android.settings.usagestats;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import com.android.settings.BaseFragment;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.usagestats.holder.AppInfoItemHolder;
import com.android.settings.usagestats.model.AppValueData;
import com.android.settings.usagestats.widget.CustomListView;
import com.android.settings.usagestats.widget.controller.AppUsageListController;
import com.android.settingslib.util.MiStatInterfaceUtils;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class UsageAppListFragment extends BaseFragment {
    private static final String TAG = UsageAppListFragment.class.getSimpleName();
    private boolean isWeekList;
    private ArrayList<AppValueData> mDataList;
    private CustomListView mListView;
    private long mMaxValue;

    /* loaded from: classes2.dex */
    private static class MyAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<AppValueData> mDataList;
        private final long mMaxValue;

        public MyAdapter(Context context, ArrayList<AppValueData> arrayList, long j) {
            this.mDataList = arrayList;
            this.mContext = context;
            this.mMaxValue = j;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            ArrayList<AppValueData> arrayList = this.mDataList;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mDataList.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            AppInfoItemHolder appInfoItemHolder;
            if (view == null) {
                appInfoItemHolder = new AppInfoItemHolder(this.mContext);
                view2 = appInfoItemHolder.getmContentView();
                view2.setTag(appInfoItemHolder);
            } else {
                view2 = view;
                appInfoItemHolder = (AppInfoItemHolder) view.getTag();
            }
            appInfoItemHolder.setmMaxCount(this.mMaxValue);
            appInfoItemHolder.setmValueData(this.mDataList.get(i));
            appInfoItemHolder.renderView();
            return view2;
        }
    }

    private String getName() {
        return UsageAppListFragment.class.getName();
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
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.usagestats_app_usage_list, viewGroup, false);
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
        if (TextUtils.isEmpty(getName())) {
            return;
        }
        try {
            MiStatInterfaceUtils.trackPageStart(getName());
        } catch (IllegalStateException unused) {
            Log.d(TAG, "onResume: IllegalStateException occurs ");
        }
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        this.mListView = (CustomListView) view.findViewById(R.id.usagestats_app_list);
        Bundle arguments = getArguments();
        if (arguments == null) {
            getActivity().finish();
            return;
        }
        this.mMaxValue = arguments.getLong("maxValue", Long.MAX_VALUE);
        boolean z = arguments.getBoolean("isWeek", false);
        this.isWeekList = z;
        this.mDataList = z ? AppUsageListController.mWeekData : AppUsageListController.mOneDayData;
        MyAdapter myAdapter = new MyAdapter(getActivity().getApplicationContext(), this.mDataList, this.mMaxValue);
        this.mListView.setVerticalScrollBarEnabled(false);
        this.mListView.setFastScrollEnabled(false);
        this.mListView.setAdapter((ListAdapter) myAdapter);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.usagestats.UsageAppListFragment.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                AppValueData appValueData = (AppValueData) UsageAppListFragment.this.mDataList.get(i);
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("isWeek", UsageAppListFragment.this.isWeekList);
                bundle2.putString("packageName", appValueData.getPackageName());
                new SubSettingLauncher(UsageAppListFragment.this.getActivity()).setDestination("com.android.settings.usagestats.UsageStatsAppUsageDetailFragment").setTitleRes(R.string.usage_state_app_usage_detail_title).setArguments(bundle2).setResultListener(null, 0).launch();
            }
        });
    }
}
