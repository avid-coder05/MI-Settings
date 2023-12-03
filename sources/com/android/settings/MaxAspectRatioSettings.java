package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.utils.AnalyticsUtils;
import java.util.ArrayList;
import java.util.List;
import miui.os.MiuiInit;
import miuix.recyclerview.widget.RecyclerView;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes.dex */
public class MaxAspectRatioSettings extends BaseFragment {
    public static final int TAG_APP_ITEM = R.layout.max_aspect_app_list;
    public AppAdapter mAdapter;
    private ApkIconLoader mApkIconLoader;
    public Context mContext;
    private LayoutInflater mLayoutInflater;
    private PackageManager mPackageManager;
    private AspectAsyncTaskWithProgress<Void, Void> mPkgAsyncTaskWithProgress;
    private RecyclerView mRecyclerView;
    private BroadcastReceiver mPackageChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.MaxAspectRatioSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MaxAspectRatioSettings.this.loadPackages();
        }
    };
    public List<AppItem> mSupportApps = new ArrayList();
    public List<AppItem> mSuggestApps = new ArrayList();
    public List<AppItem> mRestrictApps = new ArrayList();

    /* loaded from: classes.dex */
    public class AppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        View.OnClickListener mClickListener = new View.OnClickListener() { // from class: com.android.settings.MaxAspectRatioSettings.AppAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AppItem appItem = (AppItem) view.getTag(MaxAspectRatioSettings.TAG_APP_ITEM);
                appItem.mRestrict = !appItem.mRestrict;
                MiuiInit.setRestrictAspect(appItem.getPkg(), appItem.mRestrict);
                MaxAspectRatioSettings.this.mAdapter.notifyDataSetChanged();
                AnalyticsUtils.trackModifiedFullscreenModeEvent(MaxAspectRatioSettings.this.mContext, appItem.mAppInfo.packageName, appItem.mRestrict ? "off" : "on");
            }
        };
        private List<AppItem> mItems;

        /* loaded from: classes.dex */
        private class ItemViewHolder extends RecyclerView.ViewHolder {
            public ImageView icon;
            public View root;
            public TextView title;
            public SlidingButton toggle;

            public ItemViewHolder(final View view) {
                super(view);
                this.root = view;
                this.icon = (ImageView) view.findViewById(16908294);
                this.title = (TextView) view.findViewById(16908310);
                SlidingButton slidingButton = (SlidingButton) view.findViewById(R.id.switch_widget);
                this.toggle = slidingButton;
                slidingButton.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.MaxAspectRatioSettings.AppAdapter.ItemViewHolder.1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        view.callOnClick();
                    }
                });
                view.setOnClickListener(AppAdapter.this.mClickListener);
            }
        }

        /* loaded from: classes.dex */
        private class TitleViewHolder extends RecyclerView.ViewHolder {
            public TextView headerTitle;

            public TitleViewHolder(View view) {
                super(view);
                this.headerTitle = (TextView) view.findViewById(R.id.header_title);
                view.setEnabled(false);
            }
        }

        public AppAdapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            List<AppItem> list = this.mItems;
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return this.mItems.get(i).mType;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            AppItem appItem = this.mItems.get(i);
            if (appItem.mType != 0) {
                if (appItem.mType == 1) {
                    ((TitleViewHolder) viewHolder).headerTitle.setText(appItem.mLabel);
                    return;
                }
                return;
            }
            ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
            MaxAspectRatioSettings.this.mApkIconLoader.loadIcon(itemViewHolder.icon, appItem.getPkg());
            itemViewHolder.title.setText(appItem.mLabel);
            itemViewHolder.toggle.setChecked(true ^ appItem.mRestrict);
            itemViewHolder.toggle.setClickable(false);
            itemViewHolder.root.setTag(MaxAspectRatioSettings.TAG_APP_ITEM, appItem);
            itemViewHolder.root.setOnClickListener(this.mClickListener);
            itemViewHolder.root.setEnabled(appItem.mutable());
            itemViewHolder.title.setEnabled(appItem.mutable());
            itemViewHolder.toggle.setEnabled(appItem.mutable());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 0) {
                return new ItemViewHolder(MaxAspectRatioSettings.this.getLayoutInflater().inflate(R.layout.max_aspect_app_list_item, viewGroup, false));
            }
            if (i == 1) {
                return new TitleViewHolder(MaxAspectRatioSettings.this.getLayoutInflater().inflate(R.layout.max_aspect_app_list_header, viewGroup, false));
            }
            return null;
        }

        public void setItems(List<AppItem> list) {
            this.mItems = list;
        }
    }

    /* loaded from: classes.dex */
    public class AppItem {
        private ApplicationInfo mAppInfo;
        private int mDefaultAspectType;
        public String mLabel;
        private boolean mRestrict;
        private int mType;

        public AppItem(ApplicationInfo applicationInfo, boolean z, int i) {
            this.mType = -1;
            this.mAppInfo = applicationInfo;
            this.mLabel = applicationInfo.loadLabel(MaxAspectRatioSettings.this.mPackageManager).toString().replaceAll("\\u00A0", " ").trim();
            this.mType = 0;
            this.mRestrict = z;
            this.mDefaultAspectType = i;
        }

        public AppItem(String str) {
            this.mType = -1;
            this.mLabel = str;
            this.mType = 1;
        }

        public String getPkg() {
            ApplicationInfo applicationInfo = this.mAppInfo;
            if (applicationInfo == null) {
                return null;
            }
            return applicationInfo.packageName;
        }

        public boolean mutable() {
            return this.mDefaultAspectType != 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadPackages() {
        AspectAsyncTaskWithProgress<Void, Void> aspectAsyncTaskWithProgress = this.mPkgAsyncTaskWithProgress;
        if (aspectAsyncTaskWithProgress != null && !aspectAsyncTaskWithProgress.isCancelled()) {
            this.mPkgAsyncTaskWithProgress.cancel(true);
        }
        AspectAsyncTaskWithProgress<Void, Void> aspectAsyncTaskWithProgress2 = new AspectAsyncTaskWithProgress<>(getFragmentManager());
        this.mPkgAsyncTaskWithProgress = aspectAsyncTaskWithProgress2;
        aspectAsyncTaskWithProgress2.setContext(this);
        this.mPkgAsyncTaskWithProgress.setMessage(R.string.max_aspect_settings_all_app_display_loading).setCancelable(false).execute(new Void[0]);
    }

    public AppItem getAppItem(ApplicationInfo applicationInfo, boolean z, int i) {
        return new AppItem(applicationInfo, z, i);
    }

    public AppItem getAppItem(String str) {
        return new AppItem(str);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getAppCompatActivity();
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
        View inflate = layoutInflater.inflate(R.layout.max_aspect_app_list, viewGroup, false);
        if (viewGroup != null) {
            PreferenceFrameLayout.LayoutParams layoutParams = ((ViewGroup) viewGroup.getParent()).getLayoutParams();
            if (layoutParams instanceof PreferenceFrameLayout.LayoutParams) {
                layoutParams.removeBorders = true;
            }
        }
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (this.mPkgAsyncTaskWithProgress.isCancelled()) {
            return;
        }
        this.mPkgAsyncTaskWithProgress.cancel(true);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        loadPackages();
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mRecyclerView = (miuix.recyclerview.widget.RecyclerView) view.findViewById(R.id.recyclerView);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AppAdapter appAdapter = new AppAdapter();
        this.mAdapter = appAdapter;
        this.mRecyclerView.setAdapter(appAdapter);
    }
}
