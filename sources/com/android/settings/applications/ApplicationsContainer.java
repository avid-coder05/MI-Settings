package com.android.settings.applications;

import android.app.INotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.NetworkPolicyManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;
import com.android.settings.BaseFragment;
import com.android.settings.MiuiSettings;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.applications.defaultapps.MiuiDefaultAppSettings;
import com.android.settings.development.AppViewHolder;
import com.android.settings.report.InternationalCompat;
import com.android.settings.search.FunctionColumns;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import miui.app.constants.ThemeManagerConstants;
import miui.yellowpage.YellowPageContract;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class ApplicationsContainer extends BaseFragment implements ManageAppClickListener, DialogInterface.OnClickListener, DialogInterface.OnDismissListener, ActionBar.FragmentViewPagerChangeListener {
    private ActionBar mActionBar;
    private AppCompatActivity mActivity;
    private boolean mActivityResumed;
    private ApplicationsState mApplicationsState;
    public TabInfo mCurTab;
    ApplicationsState.AppEntry mCurrentEntity;
    String mCurrentPkgName;
    private Menu mOptionsMenu;
    private AlertDialog mResetDialog;
    private RunningState mState;
    private int mSortOrder = 4;
    private final ArrayList<TabInfo> mTabs = new ArrayList<>();
    private int[] mTabTexts = {R.string.filter_apps_all, R.string.filter_apps_third_party, R.string.filter_apps_running, R.string.filter_apps_cached};
    private int mDefaultListType = -1;

    /* loaded from: classes.dex */
    public static class ApplicationsAdapter extends BaseAdapter implements Filterable, ApplicationsState.Callbacks, AbsListView.RecyclerListener {
        private ArrayList<ApplicationsState.AppEntry> mBaseEntries;
        private Comparator<ApplicationsState.AppEntry> mComparatorObj;
        private final Context mContext;
        CharSequence mCurFilterPrefix;
        private ArrayList<ApplicationsState.AppEntry> mEntries;
        private final int mFilterMode;
        private ApplicationsState.AppFilter mFilterObj;
        private int mFisrtVisiblePosition;
        private IconLoader mIconLoader;
        private boolean mResumed;
        private final ApplicationsState.Session mSession;
        private final TabInfo mTab;
        private final ArrayList<View> mActive = new ArrayList<>();
        private int mLastSortMode = -1;
        private int mWhichSize = 0;
        private Filter mFilter = new Filter() { // from class: com.android.settings.applications.ApplicationsContainer.ApplicationsAdapter.1
            @Override // android.widget.Filter
            protected Filter.FilterResults performFiltering(CharSequence charSequence) {
                ApplicationsAdapter applicationsAdapter = ApplicationsAdapter.this;
                ArrayList<ApplicationsState.AppEntry> applyPrefixFilter = applicationsAdapter.applyPrefixFilter(charSequence, applicationsAdapter.mBaseEntries);
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = applyPrefixFilter;
                filterResults.count = applyPrefixFilter.size();
                return filterResults;
            }

            @Override // android.widget.Filter
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                ApplicationsAdapter applicationsAdapter = ApplicationsAdapter.this;
                applicationsAdapter.mCurFilterPrefix = charSequence;
                applicationsAdapter.mEntries = (ArrayList) filterResults.values;
                ApplicationsAdapter.this.notifyDataSetChanged();
            }
        };

        public ApplicationsAdapter(ApplicationsState applicationsState, TabInfo tabInfo, int i) {
            this.mSession = applicationsState.newSession(this);
            this.mTab = tabInfo;
            this.mContext = tabInfo.mOwner.getActivity();
            this.mFilterMode = i;
            IconLoader iconLoader = new IconLoader("IconLoader-" + tabInfo.mListType);
            this.mIconLoader = iconLoader;
            iconLoader.start();
        }

        ArrayList<ApplicationsState.AppEntry> applyPrefixFilter(CharSequence charSequence, ArrayList<ApplicationsState.AppEntry> arrayList) {
            if (charSequence == null || charSequence.length() == 0) {
                return arrayList;
            }
            String normalize = ApplicationsState.normalize(charSequence.toString());
            String str = " " + normalize;
            ArrayList<ApplicationsState.AppEntry> arrayList2 = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                ApplicationsState.AppEntry appEntry = arrayList.get(i);
                String normalizedLabel = appEntry.getNormalizedLabel();
                if (normalizedLabel.startsWith(normalize) || normalizedLabel.indexOf(str) != -1) {
                    arrayList2.add(appEntry);
                }
            }
            return arrayList2;
        }

        public void destroy() {
            this.mResumed = false;
            this.mSession.onDestroy();
            this.mIconLoader.stop();
        }

        public ApplicationsState.AppEntry getAppEntry(int i) {
            return this.mEntries.get(i);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            if (arrayList != null) {
                return arrayList.size();
            }
            return 0;
        }

        @Override // android.widget.Filterable
        public Filter getFilter() {
            return this.mFilter;
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mEntries.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return this.mEntries.get(i).id;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            AppViewHolder createOrRecycle = AppViewHolder.createOrRecycle(this.mTab.mInflater, view);
            ApplicationsState.AppEntry appEntry = this.mEntries.get(i);
            synchronized (appEntry) {
                createOrRecycle.entry = appEntry;
                String str = appEntry.label;
                if (str != null) {
                    createOrRecycle.appName.setText(str);
                }
                createOrRecycle.appIcon.setTag(appEntry.info.packageName);
                this.mIconLoader.loadIcon(createOrRecycle.appIcon, appEntry, i);
                createOrRecycle.updateSizeText(this.mTab.mInvalidSizeStr, this.mWhichSize);
                createOrRecycle.disabled.setVisibility(appEntry.info.enabled ? 8 : 0);
            }
            this.mActive.remove(view);
            this.mActive.add(view);
            return view;
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onAllSizesComputed() {
            if (this.mLastSortMode == 5) {
                rebuild(false);
            }
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onLauncherInfoChanged() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onLoadEntriesCompleted() {
            new AsyncTask<Void, Void, ArrayList<ApplicationsState.AppEntry>>() { // from class: com.android.settings.applications.ApplicationsContainer.ApplicationsAdapter.2
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public ArrayList<ApplicationsState.AppEntry> doInBackground(Void... voidArr) {
                    return ApplicationsAdapter.this.mSession.rebuild(ApplicationsAdapter.this.mFilterObj, ApplicationsAdapter.this.mComparatorObj);
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(ArrayList<ApplicationsState.AppEntry> arrayList) {
                    ApplicationsAdapter.this.mBaseEntries = arrayList;
                    if (ApplicationsAdapter.this.mBaseEntries != null) {
                        ApplicationsAdapter applicationsAdapter = ApplicationsAdapter.this;
                        applicationsAdapter.mEntries = applicationsAdapter.applyPrefixFilter(applicationsAdapter.mCurFilterPrefix, applicationsAdapter.mBaseEntries);
                    }
                    ApplicationsAdapter.this.notifyDataSetChanged();
                    if (ApplicationsAdapter.this.mFisrtVisiblePosition <= 0 || ApplicationsAdapter.this.mEntries == null || ApplicationsAdapter.this.mEntries.size() <= ApplicationsAdapter.this.mFisrtVisiblePosition) {
                        return;
                    }
                    ApplicationsAdapter.this.mTab.mListView.setSelection(ApplicationsAdapter.this.mFisrtVisiblePosition);
                    ApplicationsAdapter.this.mFisrtVisiblePosition = 0;
                }
            }.execute(new Void[0]);
        }

        @Override // android.widget.AbsListView.RecyclerListener
        public void onMovedToScrapHeap(View view) {
            this.mActive.remove(view);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageIconChanged() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageListChanged() {
            rebuild(false);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageSizeChanged(String str) {
            for (int i = 0; i < this.mActive.size(); i++) {
                AppViewHolder appViewHolder = (AppViewHolder) this.mActive.get(i).getTag();
                if (appViewHolder.entry.info.packageName.equals(str)) {
                    synchronized (appViewHolder.entry) {
                        appViewHolder.updateSizeText(this.mTab.mInvalidSizeStr, this.mWhichSize);
                    }
                    if (appViewHolder.entry.info.packageName.equals(this.mTab.mOwner.mCurrentPkgName) && this.mLastSortMode == 5) {
                        rebuild(false);
                        return;
                    }
                    return;
                }
            }
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
            if (this.mTab.mLoadingContainer.getVisibility() == 0) {
                this.mTab.mLoadingContainer.startAnimation(AnimationUtils.loadAnimation(this.mContext, 17432577));
            }
            this.mTab.mListView.setVisibility(0);
            this.mTab.mLoadingContainer.setVisibility(8);
            this.mBaseEntries = arrayList;
            this.mEntries = applyPrefixFilter(this.mCurFilterPrefix, arrayList);
            notifyDataSetChanged();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onRunningStateChanged(boolean z) {
        }

        public void pause() {
            if (this.mResumed) {
                this.mResumed = false;
                this.mSession.onPause();
            }
            this.mFisrtVisiblePosition = this.mTab.mListView.getFirstVisiblePosition();
        }

        public void rebuild(int i) {
            if (i == this.mLastSortMode) {
                return;
            }
            this.mLastSortMode = i;
            rebuild(true);
        }

        public void rebuild(final boolean z) {
            if (Environment.isExternalStorageEmulated()) {
                this.mWhichSize = 0;
            } else {
                this.mWhichSize = 1;
            }
            if (this.mFilterMode != 1) {
                this.mFilterObj = null;
            } else {
                this.mFilterObj = ApplicationsState.FILTER_THIRD_PARTY;
            }
            if (this.mLastSortMode != 5) {
                this.mComparatorObj = ApplicationsState.ALPHA_COMPARATOR;
            } else {
                int i = this.mWhichSize;
                if (i == 1) {
                    this.mComparatorObj = ApplicationsState.INTERNAL_SIZE_COMPARATOR;
                } else if (i != 2) {
                    this.mComparatorObj = ApplicationsState.SIZE_COMPARATOR;
                } else {
                    this.mComparatorObj = ApplicationsState.EXTERNAL_SIZE_COMPARATOR;
                }
            }
            new AsyncTask<Void, Void, ArrayList<ApplicationsState.AppEntry>>() { // from class: com.android.settings.applications.ApplicationsContainer.ApplicationsAdapter.4
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public ArrayList<ApplicationsState.AppEntry> doInBackground(Void... voidArr) {
                    return ApplicationsAdapter.this.mSession.rebuild(ApplicationsAdapter.this.mFilterObj, ApplicationsAdapter.this.mComparatorObj);
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(ArrayList<ApplicationsState.AppEntry> arrayList) {
                    if (arrayList != null || z) {
                        ApplicationsAdapter.this.mBaseEntries = arrayList;
                        if (ApplicationsAdapter.this.mBaseEntries != null) {
                            ApplicationsAdapter applicationsAdapter = ApplicationsAdapter.this;
                            applicationsAdapter.mEntries = applicationsAdapter.applyPrefixFilter(applicationsAdapter.mCurFilterPrefix, applicationsAdapter.mBaseEntries);
                        } else {
                            ApplicationsAdapter.this.mEntries = null;
                        }
                        ApplicationsAdapter.this.notifyDataSetChanged();
                        if (arrayList == null) {
                            ApplicationsAdapter.this.mTab.mListView.setVisibility(4);
                            ApplicationsAdapter.this.mTab.mLoadingContainer.setVisibility(0);
                            return;
                        }
                        ApplicationsAdapter.this.mTab.mListView.setVisibility(0);
                        ApplicationsAdapter.this.mTab.mLoadingContainer.setVisibility(8);
                    }
                }
            }.execute(new Void[0]);
        }

        public void resume(final int i) {
            if (this.mResumed) {
                rebuild(i);
            } else {
                new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.applications.ApplicationsContainer.ApplicationsAdapter.3
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Void doInBackground(Void... voidArr) {
                        ApplicationsAdapter.this.mSession.onResume();
                        return null;
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public void onPostExecute(Void r3) {
                        ApplicationsAdapter.this.mResumed = true;
                        ApplicationsAdapter.this.mLastSortMode = i;
                        ApplicationsAdapter.this.rebuild(true);
                    }
                }.execute(new Void[0]);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class TabInfo implements AdapterView.OnItemClickListener {
        public ApplicationsAdapter mApplications;
        public final ApplicationsState mApplicationsState;
        public final ManageAppClickListener mClickListener;
        public final CharSequence mComputingSizeStr;
        public final int mFilter;
        public LayoutInflater mInflater;
        public final CharSequence mInvalidSizeStr;
        public boolean mIsBuild;
        private boolean mIsUpdate;
        public final CharSequence mLabel;
        public final int mListType;
        private ListView mListView;
        private View mLoadingContainer;
        public final ApplicationsContainer mOwner;
        public View mRootView;

        public TabInfo(ApplicationsContainer applicationsContainer, ApplicationsState applicationsState, CharSequence charSequence, int i, ManageAppClickListener manageAppClickListener) {
            this.mOwner = applicationsContainer;
            this.mApplicationsState = applicationsState;
            this.mLabel = charSequence;
            this.mListType = i;
            if (i != 1) {
                this.mFilter = 0;
            } else {
                this.mFilter = 1;
            }
            this.mClickListener = manageAppClickListener;
            this.mInvalidSizeStr = applicationsContainer.getText(R.string.invalid_size_value);
            this.mComputingSizeStr = applicationsContainer.getText(R.string.computing_size);
        }

        public View build(LayoutInflater layoutInflater) {
            View view = this.mRootView;
            if (view != null) {
                return view;
            }
            this.mInflater = layoutInflater;
            View inflate = layoutInflater.inflate(R.layout.manage_applications_main, (ViewGroup) null);
            this.mRootView = inflate;
            View findViewById = inflate.findViewById(R.id.loading_container);
            this.mLoadingContainer = findViewById;
            findViewById.setVisibility(0);
            View findViewById2 = this.mRootView.findViewById(16908292);
            ListView listView = (ListView) this.mRootView.findViewById(16908298);
            if (findViewById2 != null) {
                listView.setEmptyView(findViewById2);
            }
            listView.setOnItemClickListener(this);
            listView.setSaveEnabled(true);
            listView.setItemsCanFocus(true);
            listView.setTextFilterEnabled(true);
            this.mListView = listView;
            ApplicationsAdapter applicationsAdapter = new ApplicationsAdapter(this.mApplicationsState, this, this.mFilter);
            this.mApplications = applicationsAdapter;
            this.mListView.setAdapter((ListAdapter) applicationsAdapter);
            this.mListView.setRecyclerListener(this.mApplications);
            if (this.mOwner.getDefaultListType() == this.mListType) {
                this.mOwner.updateCurrentTab(this);
            }
            this.mIsBuild = true;
            return this.mRootView;
        }

        public void detachView() {
            ViewGroup viewGroup;
            ApplicationsAdapter applicationsAdapter = this.mApplications;
            if (applicationsAdapter != null) {
                applicationsAdapter.destroy();
            }
            View view = this.mRootView;
            if (view != null && (viewGroup = (ViewGroup) view.getParent()) != null) {
                viewGroup.removeView(this.mRootView);
            }
            this.mIsUpdate = false;
        }

        boolean isUpdate() {
            return this.mIsUpdate;
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            this.mClickListener.onItemClick(this, adapterView, view, i, j);
        }

        public void pause() {
            ApplicationsAdapter applicationsAdapter = this.mApplications;
            if (applicationsAdapter != null) {
                applicationsAdapter.pause();
            }
            this.mIsUpdate = false;
        }

        public void resume(int i) {
            ApplicationsAdapter applicationsAdapter = this.mApplications;
            if (applicationsAdapter != null) {
                applicationsAdapter.resume(i);
                this.mIsUpdate = true;
            }
        }

        public void setUpdate(boolean z) {
            this.mIsUpdate = z;
        }
    }

    private void handleRunningState(int i) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        RunningApplicationsFragment runningApplicationsFragment = i != 2 ? i != 3 ? null : (RunningApplicationsFragment) childFragmentManager.findFragmentByTag(String.valueOf(R.string.filter_apps_cached)) : (RunningApplicationsFragment) childFragmentManager.findFragmentByTag(String.valueOf(R.string.filter_apps_running));
        if (runningApplicationsFragment != null) {
            runningApplicationsFragment.resumeRunningState();
        } else {
            pauseRunningState();
        }
    }

    private void onRestoreInstanceState(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        this.mSortOrder = bundle.getInt("sortOrder", this.mSortOrder);
        int i = bundle.getInt("defaultListType", -1);
        if (i != -1) {
            this.mDefaultListType = i;
        }
        if (bundle.getBoolean("resetDialog")) {
            buildResetDialog();
        }
    }

    private void onViewPagerChanged(int i) {
        this.mDefaultListType = i;
        TabInfo tabInfo = this.mTabs.get(i);
        this.mCurTab = tabInfo;
        updateCurrentTab(tabInfo);
        handleRunningState(i);
    }

    private void pauseRunningState() {
        if (this.mState.mResumed) {
            Log.v("ApplicationsContainer", "pause RunningState");
            this.mState.pause();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void setupContents() {
        Class cls;
        Class cls2;
        if (this.mActionBar != null) {
            return;
        }
        ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
        this.mActionBar = appCompatActionBar;
        appCompatActionBar.setFragmentViewPagerMode(this.mActivity, false);
        this.mActionBar.addOnFragmentViewPagerChangeListener(this);
        int i = 0;
        while (i < this.mTabTexts.length) {
            ActionBar.Tab newTab = this.mActionBar.newTab();
            String valueOf = String.valueOf(this.mTabTexts[i]);
            newTab.setText(getString(this.mTabTexts[i]));
            Class cls3 = null;
            int i2 = 3;
            if (i != 0) {
                if (i == 1) {
                    i2 = 1;
                } else if (i != 2) {
                    cls2 = i == 3 ? RunningApplicationsFragment.class : ManageApplicationsFragment.class;
                } else {
                    cls = RunningApplicationsFragment.class;
                    i2 = 2;
                    Bundle bundle = new Bundle();
                    bundle.putInt("filter_app_key", i2);
                    this.mActionBar.addFragmentTab(valueOf, newTab, i, cls, bundle, true);
                    i++;
                }
                cls = cls2;
                Bundle bundle2 = new Bundle();
                bundle2.putInt("filter_app_key", i2);
                this.mActionBar.addFragmentTab(valueOf, newTab, i, cls, bundle2, true);
                i++;
            } else {
                cls3 = ManageApplicationsFragment.class;
            }
            cls = cls3;
            i2 = 0;
            Bundle bundle22 = new Bundle();
            bundle22.putInt("filter_app_key", i2);
            this.mActionBar.addFragmentTab(valueOf, newTab, i, cls, bundle22, true);
            i++;
        }
        this.mActionBar.setSelectedNavigationItem(this.mDefaultListType);
    }

    private void startApplicationDetailsActivity() {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.applications.InstalledAppDetailsTop");
        intent.putExtra(FunctionColumns.PACKAGE, this.mCurrentPkgName);
        intent.putExtra("is_xspace_app", this.mCurrentEntity.isXSpaceApp);
        intent.putExtra(":android:show_fragment_title", R.string.application_info_label);
        startActivity(intent);
    }

    void buildResetDialog() {
        if (this.mResetDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
            builder.setTitle(R.string.reset_app_preferences_title);
            builder.setMessage(R.string.reset_app_preferences_desc);
            builder.setPositiveButton(R.string.reset_app_preferences_button, this);
            builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null);
            AlertDialog show = builder.show();
            this.mResetDialog = show;
            show.setOnDismissListener(this);
        }
    }

    public int getDefaultListType() {
        return this.mDefaultListType;
    }

    public ArrayList<TabInfo> getTabs() {
        return this.mTabs;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Intent intent = new Intent();
        intent.setClassName(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.appmanager.AppManagerMainActivity");
        intent.putExtra("enter_way", YellowPageContract.Settings.DIRECTORY);
        if (MiuiUtils.isActivityAvalible(this.mActivity.getApplicationContext(), intent)) {
            startActivity(intent);
            finish();
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (this.mResetDialog == dialogInterface) {
            final PackageManager packageManager = this.mActivity.getPackageManager();
            final INotificationManager asInterface = INotificationManager.Stub.asInterface(ServiceManager.getService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION));
            final NetworkPolicyManager from = NetworkPolicyManager.from(this.mActivity);
            final Handler handler = new Handler();
            new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.applications.ApplicationsContainer.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Void doInBackground(Void... voidArr) {
                    List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(512);
                    for (int i2 = 0; i2 < installedApplications.size(); i2++) {
                        ApplicationInfo applicationInfo = installedApplications.get(i2);
                        try {
                            asInterface.setNotificationsEnabledForPackage(applicationInfo.packageName, applicationInfo.uid, true);
                        } catch (RemoteException unused) {
                        }
                        packageManager.clearPackagePreferredActivities(applicationInfo.packageName);
                        if (!applicationInfo.enabled && packageManager.getApplicationEnabledSetting(applicationInfo.packageName) == 3) {
                            packageManager.setApplicationEnabledSetting(applicationInfo.packageName, 0, 1);
                        }
                    }
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    packageManager.getPreferredActivities(arrayList, arrayList2, null);
                    for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                        packageManager.clearPackagePreferredActivities(((ComponentName) arrayList2.get(i3)).getPackageName());
                    }
                    for (int i4 : from.getUidsWithPolicy(1)) {
                        from.setUidPolicy(i4, 0);
                    }
                    handler.post(new Runnable() { // from class: com.android.settings.applications.ApplicationsContainer.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (ApplicationsContainer.this.mActivityResumed) {
                                for (int i5 = 0; i5 < ApplicationsContainer.this.mTabs.size(); i5++) {
                                    ApplicationsAdapter applicationsAdapter = ((TabInfo) ApplicationsContainer.this.mTabs.get(i5)).mApplications;
                                    if (applicationsAdapter != null) {
                                        applicationsAdapter.pause();
                                    }
                                }
                                ApplicationsContainer applicationsContainer = ApplicationsContainer.this;
                                TabInfo tabInfo = applicationsContainer.mCurTab;
                                if (tabInfo != null) {
                                    tabInfo.resume(applicationsContainer.mSortOrder);
                                }
                            }
                        }
                    });
                    return null;
                }
            }.execute(new Void[0]);
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        AppCompatActivity appCompatActivity = getAppCompatActivity();
        this.mActivity = appCompatActivity;
        this.mApplicationsState = ApplicationsState.getInstance(appCompatActivity.getApplication());
        this.mDefaultListType = this.mActivity.getIntent().getIntExtra("com.android.settings.APPLICATION_LIST_TYPE", 0);
        this.mState = RunningState.getInstance(this.mActivity);
        this.mTabs.add(new TabInfo(this, this.mApplicationsState, getString(R.string.filter_apps_all), 0, this));
        this.mTabs.add(new TabInfo(this, this.mApplicationsState, getString(R.string.filter_apps_third_party), 1, this));
        this.mTabs.add(new TabInfo(this, this.mApplicationsState, getString(R.string.filter_apps_running), 2, this));
        this.mTabs.add(new TabInfo(this, this.mApplicationsState, getString(R.string.filter_apps_cached), 3, this));
        onRestoreInstanceState(bundle);
        InternationalCompat.trackReportEvent("setting_Apps_appmanger");
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        this.mOptionsMenu = menu;
        menu.add(0, 4, 1, R.string.sort_order_alpha).setShowAsAction(0);
        menu.add(0, 5, 2, R.string.sort_order_size).setShowAsAction(0);
        menu.add(0, 8, 4, R.string.reset_app_preferences).setShowAsAction(0);
        menu.add(0, 9, 5, R.string.preferred_app_settings).setIcon(R.drawable.action_button_setting).setShowAsAction(5);
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        for (int i = 0; i < this.mTabTexts.length; i++) {
            this.mTabs.get(i).detachView();
        }
        super.onDetach();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        if (this.mResetDialog == dialogInterface) {
            this.mResetDialog = null;
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        setupContents();
        return null;
    }

    @Override // com.android.settings.applications.ManageAppClickListener
    public void onItemClick(TabInfo tabInfo, AdapterView<?> adapterView, View view, int i, long j) {
        ApplicationsAdapter applicationsAdapter = tabInfo.mApplications;
        if (applicationsAdapter == null || applicationsAdapter.getCount() <= i) {
            return;
        }
        ApplicationsState.AppEntry appEntry = tabInfo.mApplications.getAppEntry(i);
        this.mCurrentEntity = appEntry;
        this.mCurrentPkgName = appEntry.info.packageName;
        if (!(this.mActivity instanceof MiuiSettings)) {
            startApplicationDetailsActivity();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, this.mCurrentPkgName);
        int i2 = R.string.application_info_label;
        bundle.putInt(":android:show_fragment_title", i2);
        bundle.putBoolean("is_xspace_app", this.mCurrentEntity.isXSpaceApp);
        startFragment(this, InstalledAppDetailsFragment.class.getName(), 0, bundle, i2);
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 4 || itemId == 5) {
            boolean z = this.mSortOrder == itemId;
            this.mSortOrder = itemId;
            for (int i = 0; i <= 1; i++) {
                this.mTabs.get(i).setUpdate(z);
                updateCurrentTab(this.mTabs.get(i));
            }
        } else if (itemId == 8) {
            buildResetDialog();
        } else if (itemId != 9) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            startFragment(this, MiuiDefaultAppSettings.class.getName(), 0, null, 0);
        }
        return true;
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrollStateChanged(int i) {
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageSelected(int i) {
        onViewPagerChanged(i);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mActivityResumed = false;
        for (int i = 0; i < this.mTabs.size(); i++) {
            this.mTabs.get(i).pause();
        }
        pauseRunningState();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateOptionsMenu();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mActivityResumed = true;
        if (-1 == this.mDefaultListType) {
            this.mDefaultListType = 0;
        }
        TabInfo tabInfo = this.mTabs.get(this.mDefaultListType);
        this.mCurTab = tabInfo;
        int i = this.mDefaultListType;
        if (i == 2 || i == 3) {
            handleRunningState(i);
        } else if (tabInfo.mIsBuild) {
            updateCurrentTab(tabInfo);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("sortOrder", this.mSortOrder);
        int i = this.mDefaultListType;
        if (i != -1) {
            bundle.putInt("defaultListType", i);
        }
        if (this.mResetDialog != null) {
            bundle.putBoolean("resetDialog", true);
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        AlertDialog alertDialog = this.mResetDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mResetDialog = null;
        }
    }

    public void updateCurrentTab(TabInfo tabInfo) {
        updateOptionsMenu();
        invalidateOptionsMenu();
        if (tabInfo.isUpdate()) {
            return;
        }
        this.mCurTab = tabInfo;
        if (this.mActivityResumed) {
            tabInfo.resume(this.mSortOrder);
        } else {
            tabInfo.pause();
        }
    }

    void updateOptionsMenu() {
        int i;
        Menu menu = this.mOptionsMenu;
        if (menu == null) {
            return;
        }
        TabInfo tabInfo = this.mCurTab;
        if (tabInfo == null || !((i = tabInfo.mListType) == 0 || i == 1)) {
            menu.findItem(4).setVisible(false);
            this.mOptionsMenu.findItem(5).setVisible(false);
            this.mOptionsMenu.findItem(8).setVisible(false);
            return;
        }
        menu.findItem(4).setVisible(this.mSortOrder != 4);
        this.mOptionsMenu.findItem(5).setVisible(this.mSortOrder != 5);
        this.mOptionsMenu.findItem(8).setVisible(true);
    }
}
