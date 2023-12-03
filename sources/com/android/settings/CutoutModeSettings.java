package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import miui.os.MiuiInit;
import miuix.appcompat.app.AlertDialog;
import miuix.recyclerview.widget.RecyclerView;

/* loaded from: classes.dex */
public class CutoutModeSettings extends BaseFragment {
    public static final int TAG_APP_ITEM = R.layout.cutout_mode_app_list;
    public AppAdapter mAdapter;
    private ApkIconLoader mApkIconLoader;
    private AppDialogAdapter mAppDialogAdapter;
    private String[] mContentArray;
    public Context mContext;
    private LayoutInflater mLayoutInflater;
    private RecyclerView mListView;
    private PackageManager mPackageManager;
    private CutoutModeAsyncTaskWithProgress<Void, Void> mPkgAsyncTaskWithProgress;
    private BroadcastReceiver mPackageChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.CutoutModeSettings.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            CutoutModeSettings.this.loadPackages();
        }
    };
    public List<AppItem> mSupportApps = new ArrayList();

    /* loaded from: classes.dex */
    public class AppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<AppItem> mItems;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public ImageView icon;
            public TextView summary;
            public TextView title;

            public ItemViewHolder(View view) {
                super(view);
                this.icon = (ImageView) view.findViewById(16908294);
                this.title = (TextView) view.findViewById(16908310);
                this.summary = (TextView) view.findViewById(16908304);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class TitleViewHolder extends RecyclerView.ViewHolder {
            public TextView headerTitle;

            public TitleViewHolder(View view) {
                super(view);
                this.headerTitle = (TextView) view.findViewById(R.id.header_title);
            }
        }

        public AppAdapter() {
        }

        private void onBindItemViewHolder(ItemViewHolder itemViewHolder, final AppItem appItem, final int i) {
            CutoutModeSettings.this.mApkIconLoader.loadIcon(itemViewHolder.icon, appItem.getPkg());
            itemViewHolder.title.setText(appItem.mLabel);
            int i2 = appItem.mCutoutMode;
            if (i2 == 0) {
                itemViewHolder.summary.setText(R.string.cutout_mode_default);
            } else if (i2 == 1) {
                itemViewHolder.summary.setText(R.string.cutout_mode_always);
            }
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.CutoutModeSettings.AppAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    CutoutModeSettings.this.showCutoutModeDialog(appItem, i);
                }
            });
        }

        private void onBindTitleViewHolder(TitleViewHolder titleViewHolder, AppItem appItem) {
            titleViewHolder.headerTitle.setText(appItem.mLabel);
        }

        public AppItem getItem(int i) {
            return this.mItems.get(i);
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
            if (viewHolder instanceof ItemViewHolder) {
                onBindItemViewHolder((ItemViewHolder) viewHolder, appItem, i);
            } else if (viewHolder instanceof TitleViewHolder) {
                onBindTitleViewHolder((TitleViewHolder) viewHolder, appItem);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return i == 0 ? new ItemViewHolder(LayoutInflater.from(CutoutModeSettings.this.mContext).inflate(R.layout.cutout_mode_app_list_item, viewGroup, false)) : new TitleViewHolder(LayoutInflater.from(CutoutModeSettings.this.mContext).inflate(R.layout.cutout_mode_app_list_header, viewGroup, false));
        }

        public void setItems(List<AppItem> list) {
            this.mItems = list;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AppDialogAdapter extends BaseAdapter {
        private int mCheckedPosition;
        private String[] mDatas;
        private LayoutInflater mInflater;

        /* loaded from: classes.dex */
        class ViewHolder {
            CheckedTextView checkedView;

            ViewHolder() {
            }
        }

        public AppDialogAdapter(Context context, String[] strArr, int i) {
            this.mDatas = strArr;
            this.mCheckedPosition = i;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mDatas.length;
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mDatas[i];
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = this.mInflater.inflate(R.layout.miuix_appcompat_select_dialog_singlechoice, (ViewGroup) null);
                viewHolder = new ViewHolder();
                viewHolder.checkedView = (CheckedTextView) view.findViewById(16908308);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (this.mDatas != null && getCount() > 0) {
                viewHolder.checkedView.setText(this.mDatas[i]);
                viewHolder.checkedView.setChecked(i == this.mCheckedPosition);
            }
            return view;
        }
    }

    /* loaded from: classes.dex */
    public class AppItem {
        private ApplicationInfo mAppInfo;
        private int mCutoutMode;
        public String mLabel;
        private int mType;

        public AppItem(ApplicationInfo applicationInfo, int i) {
            this.mType = -1;
            this.mAppInfo = applicationInfo;
            this.mLabel = applicationInfo.loadLabel(CutoutModeSettings.this.mPackageManager).toString().replaceAll("\\u00A0", " ").trim();
            this.mType = 0;
            this.mCutoutMode = i;
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

    private boolean isAttatched() {
        return getAppCompatActivity() != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadPackages() {
        CutoutModeAsyncTaskWithProgress<Void, Void> cutoutModeAsyncTaskWithProgress = this.mPkgAsyncTaskWithProgress;
        if (cutoutModeAsyncTaskWithProgress != null && !cutoutModeAsyncTaskWithProgress.isCancelled()) {
            this.mPkgAsyncTaskWithProgress.cancel(true);
        }
        CutoutModeAsyncTaskWithProgress<Void, Void> cutoutModeAsyncTaskWithProgress2 = new CutoutModeAsyncTaskWithProgress<>(getFragmentManager());
        this.mPkgAsyncTaskWithProgress = cutoutModeAsyncTaskWithProgress2;
        cutoutModeAsyncTaskWithProgress2.setContext(this);
        this.mPkgAsyncTaskWithProgress.setMessage(R.string.max_aspect_settings_all_app_display_loading).setCancelable(false).execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void prepareCutoutDialogBuild(AlertDialog.Builder builder, final String str, final int i, final int i2) {
        AppDialogAdapter appDialogAdapter = new AppDialogAdapter(getAppCompatActivity(), this.mContentArray, i);
        this.mAppDialogAdapter = appDialogAdapter;
        builder.setSingleChoiceItems(appDialogAdapter, i, new DialogInterface.OnClickListener() { // from class: com.android.settings.CutoutModeSettings.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i3) {
                if (i3 < 0 || i3 >= CutoutModeSettings.this.mContentArray.length) {
                    return;
                }
                if (i3 != i) {
                    CutoutModeSettings.this.mAdapter.getItem(i2).mCutoutMode = i3;
                    MiuiInit.setCutoutMode(str, i3);
                    CutoutModeSettings.this.mAdapter.notifyDataSetChanged();
                }
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton((CharSequence) null, (DialogInterface.OnClickListener) null);
        builder.setNegativeButton((CharSequence) null, (DialogInterface.OnClickListener) null);
    }

    public AppItem getAppItem(ApplicationInfo applicationInfo, int i) {
        return new AppItem(applicationInfo, i);
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
        View inflate = layoutInflater.inflate(R.layout.cutout_mode_app_list, viewGroup, false);
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
        this.mListView = (miuix.recyclerview.widget.RecyclerView) view.findViewById(R.id.listview);
        this.mContentArray = getContext().getResources().getStringArray(R.array.cutout_mode_entries);
        this.mListView.setLayoutManager(new LinearLayoutManager(this.mContext));
        AppAdapter appAdapter = new AppAdapter();
        this.mAdapter = appAdapter;
        this.mListView.setAdapter(appAdapter);
    }

    void showCutoutModeDialog(final AppItem appItem, final int i) {
        if (isAttatched()) {
            CommonDialog commonDialog = new CommonDialog(getAppCompatActivity(), null) { // from class: com.android.settings.CutoutModeSettings.1
                @Override // com.android.settings.CommonDialog
                protected void onPrepareBuild(AlertDialog.Builder builder) {
                    CutoutModeSettings.this.prepareCutoutDialogBuild(builder, appItem.getPkg(), appItem.mCutoutMode, i);
                }
            };
            commonDialog.setTitle(appItem.mLabel);
            commonDialog.show();
        }
    }
}
