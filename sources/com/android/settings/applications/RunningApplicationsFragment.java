package com.android.settings.applications;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.internal.util.MemInfoReader;
import com.android.settings.BaseFragment;
import com.android.settings.MiuiSettings;
import com.android.settings.R;
import com.android.settings.applications.ApplicationsContainer;
import com.android.settings.applications.RunningProcessesView;
import com.android.settings.applications.RunningState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import miui.content.ExtraIntent;

/* loaded from: classes.dex */
public class RunningApplicationsFragment extends BaseFragment implements AdapterView.OnItemClickListener, AbsListView.RecyclerListener, RunningState.OnRefreshUiListener {
    long SECONDARY_SERVER_MEM;
    private ServiceListAdapter mAdapter;
    private ActivityManager mAm;
    private ApplicationsContainer mContainer;
    private Context mContext;
    RunningState.BaseItem mCurSelected;
    private int mListType;
    private ListView mListView;
    private View mRootView;
    private boolean mShowBackgroundProcess;
    private RunningState mState;
    final HashMap<View, RunningProcessesView.ActiveItem> mActiveItems = new HashMap<>();
    private StringBuilder mBuilder = new StringBuilder(128);
    MemInfoReader mMemInfoReader = new MemInfoReader();
    int mLastNumBackgroundProcesses = -1;
    int mLastNumForegroundProcesses = -1;
    int mLastNumServiceProcesses = -1;
    long mLastBackgroundProcessMemory = -1;
    long mLastForegroundProcessMemory = -1;
    long mLastServiceProcessMemory = -1;
    long mLastAvailMemory = -1;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ServiceListAdapter extends BaseAdapter {
        final LayoutInflater mInflater;
        ArrayList<RunningState.MergedItem> mItems;
        final RunningState mState;

        ServiceListAdapter(RunningState runningState) {
            this.mState = runningState;
            this.mInflater = (LayoutInflater) RunningApplicationsFragment.this.mContext.getSystemService("layout_inflater");
            runningState.setWatchingBackgroundItems(RunningApplicationsFragment.this.mShowBackgroundProcess);
            this.mItems = new ArrayList<>();
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return false;
        }

        public void bindView(View view, int i) {
            synchronized (this.mState.mLock) {
                if (i >= this.mItems.size()) {
                    return;
                }
                RunningApplicationsFragment.this.mActiveItems.put(view, ((RunningProcessesView.ViewHolder) view.getTag()).bind(this.mState, this.mItems.get(i), RunningApplicationsFragment.this.mBuilder));
            }
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mItems.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mItems.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return this.mItems.get(i).hashCode();
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = newView(viewGroup);
            }
            bindView(view, i);
            return view;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean isEmpty() {
            return this.mState.hasData() && this.mItems.size() == 0;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int i) {
            return !this.mItems.get(i).mIsProcess;
        }

        public View newView(ViewGroup viewGroup) {
            View inflate = this.mInflater.inflate(R.layout.running_processes_item, viewGroup, false);
            new RunningProcessesView.ViewHolder(inflate);
            return inflate;
        }

        void refreshItems() {
            ArrayList<RunningState.MergedItem> currentBackgroundItems = RunningApplicationsFragment.this.mShowBackgroundProcess ? this.mState.getCurrentBackgroundItems() : this.mState.getCurrentMergedItems();
            if (this.mItems != currentBackgroundItems) {
                this.mItems = currentBackgroundItems;
            }
        }
    }

    private void startServiceDetailsActivity(RunningState.MergedItem mergedItem) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.applications.RunningServiceDetailsActivity");
        intent.putExtra("uid", mergedItem.mProcess.mUid);
        intent.putExtra(ExtraIntent.EXTRA_XIAOMI_ACCOUNT_USER_ID, mergedItem.mProcess.mUserId);
        intent.putExtra("process", mergedItem.mProcess.mProcessName);
        intent.putExtra("background", this.mShowBackgroundProcess);
        startActivity(intent);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int i = getArguments().getInt("filter_app_key");
        this.mListType = i;
        this.mShowBackgroundProcess = 3 == i;
        this.mContext = getActivity();
        ApplicationsContainer applicationsContainer = (ApplicationsContainer) getActivity().getSupportFragmentManager().findFragmentByTag(ApplicationsContainer.class.getName());
        this.mContainer = applicationsContainer;
        if (applicationsContainer == null) {
            this.mContainer = (ApplicationsContainer) getParentFragment();
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = this.mRootView;
        if (view != null) {
            return view;
        }
        this.mAm = (ActivityManager) getActivity().getSystemService("activity");
        this.mState = RunningState.getInstance(getActivity());
        View inflate = layoutInflater.inflate(R.layout.manage_applications_main, (ViewGroup) null);
        this.mRootView = inflate;
        this.mListView = (ListView) inflate.findViewById(16908298);
        View findViewById = this.mRootView.findViewById(16908292);
        if (findViewById != null) {
            this.mListView.setEmptyView(findViewById);
        }
        this.mListView.setOnItemClickListener(this);
        this.mListView.setRecyclerListener(this);
        ServiceListAdapter serviceListAdapter = new ServiceListAdapter(this.mState);
        this.mAdapter = serviceListAdapter;
        this.mListView.setAdapter((ListAdapter) serviceListAdapter);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        this.mAm.getMemoryInfo(memoryInfo);
        this.SECONDARY_SERVER_MEM = memoryInfo.secondaryServerThreshold;
        return this.mRootView;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        RunningState.MergedItem mergedItem = (RunningState.MergedItem) ((ListView) adapterView).getAdapter().getItem(i);
        this.mCurSelected = mergedItem;
        if (!(getActivity() instanceof MiuiSettings)) {
            startServiceDetailsActivity(mergedItem);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("uid", mergedItem.mProcess.mUid);
        bundle.putString("process", mergedItem.mProcess.mProcessName);
        bundle.putBoolean("background", this.mShowBackgroundProcess);
        bundle.putInt(":android:show_fragment_title", R.string.application_info_label);
        startFragment((ApplicationsContainer) getParentFragment(), RunningServiceDetails.class.getName(), 0, bundle, R.string.runningservicedetails_settings_title);
    }

    @Override // android.widget.AbsListView.RecyclerListener
    public void onMovedToScrapHeap(View view) {
        this.mActiveItems.remove(view);
    }

    @Override // com.android.settings.applications.RunningState.OnRefreshUiListener
    public void onRefreshUi(int i) {
        if (i == 0) {
            updateTimes();
        } else if (i == 1) {
            refreshUi(false);
            updateTimes();
        } else if (i != 2) {
        } else {
            refreshUi(true);
            updateTimes();
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        ApplicationsContainer.TabInfo tabInfo;
        int i;
        super.onResume();
        ApplicationsContainer applicationsContainer = this.mContainer;
        if (applicationsContainer == null || (tabInfo = applicationsContainer.mCurTab) == null || (i = tabInfo.mListType) != this.mListType) {
            return;
        }
        Log.v("RunningApplicationsFragment", "call resume RunningState, tab = " + i);
        resumeRunningState();
    }

    void refreshUi(boolean z) {
        if (z) {
            ServiceListAdapter serviceListAdapter = (ServiceListAdapter) this.mListView.getAdapter();
            serviceListAdapter.refreshItems();
            serviceListAdapter.notifyDataSetChanged();
        }
        this.mMemInfoReader.readMemInfo();
        long freeSize = (this.mMemInfoReader.getFreeSize() + this.mMemInfoReader.getCachedSize()) - this.SECONDARY_SERVER_MEM;
        if (freeSize < 0) {
            freeSize = 0;
        }
        synchronized (this.mState.mLock) {
            int i = this.mLastNumBackgroundProcesses;
            RunningState runningState = this.mState;
            int i2 = runningState.mNumBackgroundProcesses;
            if (i != i2 || this.mLastBackgroundProcessMemory != runningState.mBackgroundProcessMemory || this.mLastAvailMemory != freeSize) {
                this.mLastNumBackgroundProcesses = i2;
                this.mLastBackgroundProcessMemory = runningState.mBackgroundProcessMemory;
                this.mLastAvailMemory = freeSize;
            }
            int i3 = this.mLastNumForegroundProcesses;
            int i4 = runningState.mNumForegroundProcesses;
            if (i3 != i4 || this.mLastForegroundProcessMemory != runningState.mForegroundProcessMemory || this.mLastNumServiceProcesses != runningState.mNumServiceProcesses || this.mLastServiceProcessMemory != runningState.mServiceProcessMemory) {
                this.mLastNumForegroundProcesses = i4;
                this.mLastForegroundProcessMemory = runningState.mForegroundProcessMemory;
                this.mLastNumServiceProcesses = runningState.mNumServiceProcesses;
                this.mLastServiceProcessMemory = runningState.mServiceProcessMemory;
            }
        }
    }

    public void resumeRunningState() {
        Log.v("RunningApplicationsFragment", "resume RunningState, tab = " + this.mListType);
        if (this.mState == null) {
            this.mState = RunningState.getInstance(getActivity());
        }
        this.mState.resume(this);
        if (this.mAdapter == null || !this.mState.hasData()) {
            return;
        }
        refreshUi(true);
    }

    void updateTimes() {
        Iterator<RunningProcessesView.ActiveItem> it = this.mActiveItems.values().iterator();
        while (it.hasNext()) {
            RunningProcessesView.ActiveItem next = it.next();
            if (next.mRootView.getWindowToken() == null) {
                it.remove();
            } else {
                next.updateTime(getActivity(), this.mBuilder);
            }
        }
    }
}
