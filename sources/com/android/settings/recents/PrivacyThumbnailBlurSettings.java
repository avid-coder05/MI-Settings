package com.android.settings.recents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.provider.MiuiSettings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.ApkIconLoader;
import com.android.settings.R;
import com.android.settings.utils.Utils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import miuix.appcompat.app.AppCompatActivity;
import miuix.recyclerview.widget.RecyclerView;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes2.dex */
public class PrivacyThumbnailBlurSettings extends AppCompatActivity {
    public static final int TAG_APP_ITEM = R.layout.privacy_thumbnail_blur_settings;
    public AppAdapter mAdapter;
    private ApkIconLoader mApkIconLoader;
    private PrivacyThumbnailAsyncTaskWithProgress mPkgAsyncTaskWithProgress;
    private RecyclerView mRecyclerView;
    public List<AppItem> mThumbnailBlurEnableApps = new ArrayList();
    public List<AppItem> mThumbnailBlurDisableApps = new ArrayList();
    private BroadcastReceiver mPackageChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.recents.PrivacyThumbnailBlurSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            PrivacyThumbnailBlurSettings.this.loadPackages();
        }
    };

    /* loaded from: classes2.dex */
    public class AppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        View.OnClickListener mClickListener = new View.OnClickListener() { // from class: com.android.settings.recents.PrivacyThumbnailBlurSettings.AppAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AppItem appItem = (AppItem) view.getTag(PrivacyThumbnailBlurSettings.TAG_APP_ITEM);
                boolean z = true;
                appItem.mPrivacyThumbnailBlurEnable = !appItem.mPrivacyThumbnailBlurEnable;
                HashSet<String> convertStringToSet = Utils.convertStringToSet(MiuiSettings.System.getString(PrivacyThumbnailBlurSettings.this.getContentResolver(), "miui_recents_privacy_thumbnail_blur", ""));
                String pkg = appItem.getPkg();
                boolean contains = convertStringToSet.contains(pkg);
                boolean z2 = false;
                if (appItem.mPrivacyThumbnailBlurEnable && !contains) {
                    convertStringToSet.add(pkg);
                    z2 = true;
                }
                if (appItem.mPrivacyThumbnailBlurEnable || !contains) {
                    z = z2;
                } else {
                    convertStringToSet.remove(pkg);
                }
                if (z) {
                    MiuiSettings.System.putString(PrivacyThumbnailBlurSettings.this.getContentResolver(), "miui_recents_privacy_thumbnail_blur", Utils.convertSetToString(convertStringToSet));
                }
                PrivacyThumbnailBlurSettings.this.mAdapter.notifyDataSetChanged();
            }
        };
        private List<AppItem> mItems;

        /* loaded from: classes2.dex */
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
                slidingButton.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.recents.PrivacyThumbnailBlurSettings.AppAdapter.ItemViewHolder.1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        view.callOnClick();
                    }
                });
                view.setOnClickListener(AppAdapter.this.mClickListener);
            }
        }

        /* loaded from: classes2.dex */
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
            PrivacyThumbnailBlurSettings.this.mApkIconLoader.loadIcon(itemViewHolder.icon, appItem.getPkg());
            itemViewHolder.title.setText(appItem.mLabel);
            itemViewHolder.toggle.setChecked(appItem.mPrivacyThumbnailBlurEnable);
            itemViewHolder.toggle.setClickable(false);
            itemViewHolder.root.setTag(PrivacyThumbnailBlurSettings.TAG_APP_ITEM, appItem);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 0) {
                return new ItemViewHolder(PrivacyThumbnailBlurSettings.this.getLayoutInflater().inflate(R.layout.privacy_thumbnail_blur_list_item, viewGroup, false));
            }
            if (i == 1) {
                return new TitleViewHolder(PrivacyThumbnailBlurSettings.this.getLayoutInflater().inflate(R.layout.privacy_thumbnail_blur_list_header, viewGroup, false));
            }
            return null;
        }

        public void setItems(List<AppItem> list) {
            this.mItems = list;
        }
    }

    /* loaded from: classes2.dex */
    public class AppItem {
        private ApplicationInfo mAppInfo;
        public String mLabel;
        private boolean mPrivacyThumbnailBlurEnable;
        private int mType;

        public AppItem(ApplicationInfo applicationInfo, boolean z) {
            this.mType = -1;
            this.mAppInfo = applicationInfo;
            this.mLabel = applicationInfo.loadLabel(PrivacyThumbnailBlurSettings.this.getPackageManager()).toString().replaceAll("\\u00A0", " ").trim();
            this.mType = 0;
            this.mPrivacyThumbnailBlurEnable = z;
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
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadPackages() {
        PrivacyThumbnailAsyncTaskWithProgress privacyThumbnailAsyncTaskWithProgress = this.mPkgAsyncTaskWithProgress;
        if (privacyThumbnailAsyncTaskWithProgress != null && !privacyThumbnailAsyncTaskWithProgress.isCancelled()) {
            this.mPkgAsyncTaskWithProgress.cancel(true);
        }
        PrivacyThumbnailAsyncTaskWithProgress privacyThumbnailAsyncTaskWithProgress2 = new PrivacyThumbnailAsyncTaskWithProgress(getSupportFragmentManager());
        this.mPkgAsyncTaskWithProgress = privacyThumbnailAsyncTaskWithProgress2;
        privacyThumbnailAsyncTaskWithProgress2.setContext(this);
        this.mPkgAsyncTaskWithProgress.setMessage(R.string.privacy_thumbnail_blur_progress_message).setCancelable(false).execute(new Void[0]);
    }

    public AppItem getAppItem(ApplicationInfo applicationInfo, boolean z) {
        return new AppItem(applicationInfo, z);
    }

    public AppItem getAppItem(String str) {
        return new AppItem(str);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        registerReceiver(this.mPackageChangeReceiver, intentFilter);
        setContentView(R.layout.privacy_thumbnail_blur_settings);
        this.mApkIconLoader = new ApkIconLoader(this);
        this.mRecyclerView = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.recyclerView);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        AppAdapter appAdapter = new AppAdapter();
        this.mAdapter = appAdapter;
        this.mRecyclerView.setAdapter(appAdapter);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        unregisterReceiver(this.mPackageChangeReceiver);
        this.mApkIconLoader.stop();
        super.onDestroy();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        if (this.mPkgAsyncTaskWithProgress.isCancelled()) {
            return;
        }
        this.mPkgAsyncTaskWithProgress.cancel(true);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        loadPackages();
    }
}
