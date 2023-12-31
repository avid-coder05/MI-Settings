package com.android.settings.applications.appops;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import com.android.settings.R;
import com.android.settings.applications.appops.AppOpsState;
import com.android.settings.search.FunctionColumns;
import java.util.List;

/* loaded from: classes.dex */
public class AppOpsCategory extends ListFragment implements LoaderManager.LoaderCallbacks<List<AppOpsState.AppOpEntry>> {
    AppListAdapter mAdapter;
    AppOpsState mState;

    /* loaded from: classes.dex */
    public static class AppListAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        List<AppOpsState.AppOpEntry> mList;
        private final Resources mResources;
        private final AppOpsState mState;

        public AppListAdapter(Context context, AppOpsState appOpsState) {
            this.mResources = context.getResources();
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.mState = appOpsState;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            List<AppOpsState.AppOpEntry> list = this.mList;
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public AppOpsState.AppOpEntry getItem(int i) {
            return this.mList.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.mInflater.inflate(R.layout.app_ops_item, viewGroup, false);
            }
            AppOpsState.AppOpEntry item = getItem(i);
            ((ImageView) view.findViewById(R.id.app_icon)).setImageDrawable(item.getAppEntry().getIcon());
            ((TextView) view.findViewById(R.id.app_name)).setText(item.getAppEntry().getLabel());
            ((TextView) view.findViewById(R.id.op_name)).setText(item.getTimeText(this.mResources, false));
            view.findViewById(R.id.op_time).setVisibility(8);
            ((Switch) view.findViewById(R.id.op_switch)).setChecked(item.getPrimaryOpMode() == 0);
            return view;
        }

        public void setData(List<AppOpsState.AppOpEntry> list) {
            this.mList = list;
            notifyDataSetChanged();
        }
    }

    /* loaded from: classes.dex */
    public static class AppListLoader extends AsyncTaskLoader<List<AppOpsState.AppOpEntry>> {
        List<AppOpsState.AppOpEntry> mApps;
        final InterestingConfigChanges mLastConfig;
        PackageIntentReceiver mPackageObserver;
        final AppOpsState mState;
        final AppOpsState.OpsTemplate mTemplate;

        public AppListLoader(Context context, AppOpsState appOpsState, AppOpsState.OpsTemplate opsTemplate) {
            super(context);
            this.mLastConfig = new InterestingConfigChanges();
            this.mState = appOpsState;
            this.mTemplate = opsTemplate;
        }

        @Override // androidx.loader.content.Loader
        public void deliverResult(List<AppOpsState.AppOpEntry> list) {
            if (isReset() && list != null) {
                onReleaseResources(list);
            }
            this.mApps = list;
            if (isStarted()) {
                super.deliverResult((AppListLoader) list);
            }
            if (list != null) {
                onReleaseResources(list);
            }
        }

        @Override // androidx.loader.content.AsyncTaskLoader
        public List<AppOpsState.AppOpEntry> loadInBackground() {
            return this.mState.buildState(this.mTemplate, 0, null, AppOpsState.LABEL_COMPARATOR);
        }

        @Override // androidx.loader.content.AsyncTaskLoader
        public void onCanceled(List<AppOpsState.AppOpEntry> list) {
            super.onCanceled((AppListLoader) list);
            onReleaseResources(list);
        }

        protected void onReleaseResources(List<AppOpsState.AppOpEntry> list) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onReset() {
            super.onReset();
            onStopLoading();
            List<AppOpsState.AppOpEntry> list = this.mApps;
            if (list != null) {
                onReleaseResources(list);
                this.mApps = null;
            }
            if (this.mPackageObserver != null) {
                getContext().unregisterReceiver(this.mPackageObserver);
                this.mPackageObserver = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onStartLoading() {
            onContentChanged();
            List<AppOpsState.AppOpEntry> list = this.mApps;
            if (list != null) {
                deliverResult(list);
            }
            if (this.mPackageObserver == null) {
                this.mPackageObserver = new PackageIntentReceiver(this);
            }
            boolean applyNewConfig = this.mLastConfig.applyNewConfig(getContext().getResources());
            if (takeContentChanged() || this.mApps == null || applyNewConfig) {
                forceLoad();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.loader.content.Loader
        public void onStopLoading() {
            cancelLoad();
        }
    }

    /* loaded from: classes.dex */
    public static class InterestingConfigChanges {
        final Configuration mLastConfiguration = new Configuration();
        int mLastDensity;

        boolean applyNewConfig(Resources resources) {
            int updateFrom = this.mLastConfiguration.updateFrom(resources.getConfiguration());
            if ((this.mLastDensity != resources.getDisplayMetrics().densityDpi) || (updateFrom & 772) != 0) {
                this.mLastDensity = resources.getDisplayMetrics().densityDpi;
                return true;
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static class PackageIntentReceiver extends BroadcastReceiver {
        final AppListLoader mLoader;

        public PackageIntentReceiver(AppListLoader appListLoader) {
            this.mLoader = appListLoader;
            IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
            intentFilter.addDataScheme(FunctionColumns.PACKAGE);
            appListLoader.getContext().registerReceiver(this, intentFilter);
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
            intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
            appListLoader.getContext().registerReceiver(this, intentFilter2);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            this.mLoader.onContentChanged();
        }
    }

    public AppOpsCategory() {
    }

    public AppOpsCategory(AppOpsState.OpsTemplate opsTemplate) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("template", opsTemplate);
        setArguments(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setEmptyText("No applications");
        setHasOptionsMenu(true);
        AppListAdapter appListAdapter = new AppListAdapter(getActivity(), this.mState);
        this.mAdapter = appListAdapter;
        setListAdapter(appListAdapter);
        setListShown(false);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mState = new AppOpsState(getActivity());
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<List<AppOpsState.AppOpEntry>> onCreateLoader(int i, Bundle bundle) {
        Bundle arguments = getArguments();
        return new AppListLoader(getActivity(), this.mState, arguments != null ? (AppOpsState.OpsTemplate) arguments.getParcelable("template") : null);
    }

    @Override // androidx.fragment.app.ListFragment
    public void onListItemClick(ListView listView, View view, int i, long j) {
        AppOpsState.AppOpEntry item = this.mAdapter.getItem(i);
        if (item != null) {
            Switch r2 = (Switch) view.findViewById(R.id.op_switch);
            boolean z = !r2.isChecked() ? 1 : 0;
            r2.setChecked(z);
            AppOpsManager.OpEntry opEntry = item.getOpEntry(0);
            int i2 = !z ? 1 : 0;
            this.mState.getAppOpsManager().setMode(opEntry.getOp(), item.getAppEntry().getApplicationInfo().uid, item.getAppEntry().getApplicationInfo().packageName, i2);
            item.overridePrimaryOpMode(i2);
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<List<AppOpsState.AppOpEntry>> loader, List<AppOpsState.AppOpEntry> list) {
        this.mAdapter.setData(list);
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<List<AppOpsState.AppOpEntry>> loader) {
        this.mAdapter.setData(null);
    }
}
