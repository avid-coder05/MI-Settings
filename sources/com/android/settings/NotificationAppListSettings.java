package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.notification.NotificationSettingsHelper;
import com.android.settings.search.FunctionColumns;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import miuix.appcompat.app.Fragment;
import miuix.os.AsyncTaskWithProgress;

/* loaded from: classes.dex */
public class NotificationAppListSettings extends BaseFragment {
    public static final int TAG_APP_ITEM = R.string.manage_notification_title;
    private AppAdapter mAdapter;
    private ApkIconLoader mApkIconLoader;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private PackageManager mPackageManager;
    private PkgAsyncTaskWithProgress<Void, Void> mPkgAsyncTaskWithProgress;
    private BroadcastReceiver mPackageChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.NotificationAppListSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            NotificationAppListSettings.this.loadPackages();
        }
    };
    List<AppItem> mDisabledApps = new ArrayList();
    List<AppItem> mEnabledApps = new ArrayList();

    /* loaded from: classes.dex */
    private class AppAdapter extends BaseAdapter {
        View.OnClickListener mClickListener = new View.OnClickListener() { // from class: com.android.settings.NotificationAppListSettings.AppAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AppItem appItem = (AppItem) view.getTag(NotificationAppListSettings.TAG_APP_ITEM);
                Bundle bundle = new Bundle();
                bundle.putString(FunctionColumns.PACKAGE, appItem.mPkgName);
                bundle.putInt("uid", appItem.mUid);
                AppAdapter appAdapter = AppAdapter.this;
                appAdapter.startPreferencePanel(bundle, appItem.mLabel, NotificationAppListSettings.this);
            }
        };
        private List<AppItem> mItems;

        public AppAdapter() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startPreferencePanel(Bundle bundle, String str, Fragment fragment) {
            FragmentActivity activity = NotificationAppListSettings.this.getActivity();
            if (activity instanceof SettingsActivity) {
                SettingsActivityCompat.startPreferencePanel(activity, fragment, "com.android.settings.notification.AppNotificationSettings", bundle, 0, str, null, 0);
            } else if (activity instanceof MiuiSettings) {
                ((MiuiSettings) activity).startPreferencePanel("com.android.settings.notification.AppNotificationSettings", bundle, 0, str, null, 0);
            }
        }

        @Override // android.widget.Adapter
        public int getCount() {
            List<AppItem> list = this.mItems;
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        @Override // android.widget.Adapter
        public AppItem getItem(int i) {
            return this.mItems.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int i) {
            return this.mItems.get(i).mType;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            View view2;
            View view3;
            ViewHolder viewHolder2;
            AppItem appItem = this.mItems.get(i);
            int i2 = appItem.mType;
            if (i2 != 0) {
                if (i2 == 1) {
                    if (view == null) {
                        viewHolder = new ViewHolder();
                        view2 = NotificationAppListSettings.this.mLayoutInflater.inflate(R.layout.status_bar_app_list_header, (ViewGroup) null);
                        viewHolder.headerTitle = (TextView) view2.findViewById(R.id.header_title);
                        view2.setEnabled(false);
                        view2.setTag(viewHolder);
                    } else {
                        viewHolder = (ViewHolder) view.getTag();
                        view2 = view;
                    }
                    viewHolder.headerTitle.setText(appItem.mLabel);
                    return view2;
                }
                return view;
            }
            if (view == null) {
                viewHolder2 = new ViewHolder();
                view3 = NotificationAppListSettings.this.mLayoutInflater.inflate(R.layout.status_bar_app_list_item, (ViewGroup) null);
                viewHolder2.icon = (ImageView) view3.findViewById(16908294);
                viewHolder2.title = (TextView) view3.findViewById(16908310);
                view3.setTag(viewHolder2);
            } else {
                view3 = view;
                viewHolder2 = (ViewHolder) view.getTag();
            }
            NotificationAppListSettings.this.mApkIconLoader.loadIcon(viewHolder2.icon, appItem.mPkgName);
            viewHolder2.title.setText(appItem.mLabel);
            view3.setTag(NotificationAppListSettings.TAG_APP_ITEM, appItem);
            view3.setOnClickListener(this.mClickListener);
            return view3;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getViewTypeCount() {
            return 2;
        }

        public void setItems(List<AppItem> list) {
            this.mItems = list;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AppItem {
        public String mLabel;
        public String mPkgName;
        public int mType;
        public int mUid;

        public AppItem(String str) {
            this.mUid = -1;
            this.mType = -1;
            this.mLabel = str;
            this.mType = 1;
        }

        public AppItem(String str, String str2, int i) {
            this.mUid = -1;
            this.mType = -1;
            this.mPkgName = str;
            this.mLabel = str2;
            this.mUid = i;
            this.mType = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PkgAsyncTaskWithProgress<Params, Result> extends AsyncTaskWithProgress<Params, Result> {
        private static ArrayList<String> sPkgs;
        private WeakReference<NotificationAppListSettings> mWeakSettings;

        static {
            ArrayList<String> arrayList = new ArrayList<>();
            sPkgs = arrayList;
            arrayList.add("com.miui.hybrid");
            sPkgs.add("com.miui.android.fashiongallery");
            sPkgs.add("com.mfashiongallery.emag");
        }

        public PkgAsyncTaskWithProgress(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        private void addSpecifiedActivities(PackageManager packageManager, List<ResolveInfo> list) {
            Intent intent = new Intent("android.intent.action.MAIN");
            Iterator<String> it = sPkgs.iterator();
            while (it.hasNext()) {
                intent.setPackage(it.next());
                ResolveInfo resolveActivity = packageManager.resolveActivity(intent, 0);
                if (resolveActivity != null) {
                    list.add(resolveActivity);
                }
            }
        }

        @Override // android.os.AsyncTask
        protected Result doInBackground(Params... paramsArr) {
            NotificationAppListSettings notificationAppListSettings = this.mWeakSettings.get();
            if (notificationAppListSettings != null && notificationAppListSettings.getContext() != null) {
                notificationAppListSettings.mDisabledApps.clear();
                notificationAppListSettings.mEnabledApps.clear();
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");
                List<ResolveInfo> queryIntentActivities = notificationAppListSettings.mPackageManager.queryIntentActivities(intent, 0);
                addSpecifiedActivities(notificationAppListSettings.mPackageManager, queryIntentActivities);
                HashSet hashSet = new HashSet();
                for (ResolveInfo resolveInfo : queryIntentActivities) {
                    ActivityInfo activityInfo = resolveInfo.activityInfo;
                    String str = activityInfo.packageName;
                    String str2 = activityInfo.name;
                    if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2) && !hashSet.contains(str)) {
                        AppItem appItem = notificationAppListSettings.getAppItem(resolveInfo.activityInfo.applicationInfo);
                        if (NotificationSettingsHelper.isNotificationsBanned(notificationAppListSettings.mContext, str)) {
                            notificationAppListSettings.mDisabledApps.add(appItem);
                        } else {
                            notificationAppListSettings.mEnabledApps.add(appItem);
                        }
                        hashSet.add(str);
                    }
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // miuix.os.AsyncTaskWithProgress, android.os.AsyncTask
        public void onPostExecute(Result result) {
            super.onPostExecute(result);
            NotificationAppListSettings notificationAppListSettings = this.mWeakSettings.get();
            if (notificationAppListSettings == null || notificationAppListSettings.getContext() == null) {
                return;
            }
            ArrayList arrayList = new ArrayList();
            Collections.sort(notificationAppListSettings.mDisabledApps, new Comparator<AppItem>() { // from class: com.android.settings.NotificationAppListSettings.PkgAsyncTaskWithProgress.1
                private final Collator sCollator = Collator.getInstance();

                @Override // java.util.Comparator
                public int compare(AppItem appItem, AppItem appItem2) {
                    return this.sCollator.compare(appItem.mLabel, appItem2.mLabel);
                }
            });
            Collections.sort(notificationAppListSettings.mEnabledApps, new Comparator<AppItem>() { // from class: com.android.settings.NotificationAppListSettings.PkgAsyncTaskWithProgress.2
                private final Collator sCollator = Collator.getInstance();

                @Override // java.util.Comparator
                public int compare(AppItem appItem, AppItem appItem2) {
                    return this.sCollator.compare(appItem.mLabel, appItem2.mLabel);
                }
            });
            if (notificationAppListSettings.mDisabledApps.size() > 0) {
                arrayList.add(notificationAppListSettings.getAppItem(notificationAppListSettings.mContext.getResources().getQuantityString(R.plurals.status_bar_settings_disabled_header_title, notificationAppListSettings.mDisabledApps.size(), Integer.valueOf(notificationAppListSettings.mDisabledApps.size()))));
                arrayList.addAll(notificationAppListSettings.mDisabledApps);
            }
            if (notificationAppListSettings.mEnabledApps.size() > 0) {
                arrayList.add(notificationAppListSettings.getAppItem(notificationAppListSettings.mContext.getResources().getQuantityString(R.plurals.status_bar_settings_enabled_header_title, notificationAppListSettings.mEnabledApps.size(), Integer.valueOf(notificationAppListSettings.mEnabledApps.size()))));
                arrayList.addAll(notificationAppListSettings.mEnabledApps);
            }
            notificationAppListSettings.mAdapter.setItems(arrayList);
            notificationAppListSettings.mAdapter.notifyDataSetChanged();
        }

        public void setContext(NotificationAppListSettings notificationAppListSettings) {
            this.mWeakSettings = new WeakReference<>(notificationAppListSettings);
        }
    }

    /* loaded from: classes.dex */
    private static class ViewHolder {
        public TextView headerTitle;
        public ImageView icon;
        public TextView title;

        private ViewHolder() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadPackages() {
        PkgAsyncTaskWithProgress<Void, Void> pkgAsyncTaskWithProgress = this.mPkgAsyncTaskWithProgress;
        if (pkgAsyncTaskWithProgress != null && !pkgAsyncTaskWithProgress.isCancelled()) {
            this.mPkgAsyncTaskWithProgress.cancel(true);
        }
        PkgAsyncTaskWithProgress<Void, Void> pkgAsyncTaskWithProgress2 = new PkgAsyncTaskWithProgress<>(getFragmentManager());
        this.mPkgAsyncTaskWithProgress = pkgAsyncTaskWithProgress2;
        pkgAsyncTaskWithProgress2.setContext(this);
        this.mPkgAsyncTaskWithProgress.setMessage(R.string.loading).setCancelable(false).execute(new Void[0]);
    }

    public AppItem getAppItem(ApplicationInfo applicationInfo) {
        return new AppItem(applicationInfo != null ? applicationInfo.packageName : "", applicationInfo != null ? applicationInfo.loadLabel(this.mPackageManager).toString().replaceAll("\\u00A0", " ").trim() : "", applicationInfo != null ? applicationInfo.uid : -1);
    }

    public AppItem getAppItem(String str) {
        return new AppItem(str);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        this.mApkIconLoader = new ApkIconLoader(this.mContext);
        this.mPackageManager = getPackageManager();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        this.mContext.registerReceiver(this.mPackageChangeReceiver, intentFilter);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mContext.unregisterReceiver(this.mPackageChangeReceiver);
        this.mApkIconLoader.stop();
        super.onDestroy();
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mLayoutInflater = layoutInflater;
        View inflate = layoutInflater.inflate(R.layout.status_bar_app_list, viewGroup, false);
        if (viewGroup != null) {
            MiuiUtils.updateFragmentView(getActivity(), viewGroup);
        }
        return inflate;
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        loadPackages();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        PkgAsyncTaskWithProgress<Void, Void> pkgAsyncTaskWithProgress = this.mPkgAsyncTaskWithProgress;
        if (pkgAsyncTaskWithProgress == null || pkgAsyncTaskWithProgress.getStatus() != AsyncTask.Status.RUNNING) {
            return;
        }
        this.mPkgAsyncTaskWithProgress.cancel(true);
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        AppAdapter appAdapter = new AppAdapter();
        this.mAdapter = appAdapter;
        listView.setAdapter((ListAdapter) appAdapter);
    }
}
