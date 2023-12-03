package com.android.settings;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.PreferenceFrameLayout;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import miui.securityspace.CrossUserUtils;
import miui.util.AutoDisableScreenButtonsHelper;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;
import miuix.os.AsyncTaskWithProgress;

/* loaded from: classes.dex */
public class AutoDisableScreenButtonsAppListSettings extends BaseFragment {
    public static final int TAG_APP_ITEM = R.string.auto_disable_screenbuttons_title;
    public static final int[] mListFlags = {1, 2, 3};
    private AppAdapter mAdapter;
    private ApkIconLoader mApkIconLoader;
    private CheckBox mCheckbox;
    private Context mContext;
    private Dialog mDialog;
    private LayoutInflater mLayoutInflater;
    public String[] mListChoices;
    private PackageManager mPackageManager;
    private RecyclerView mRecyclerview;
    private BroadcastReceiver mPackageChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.AutoDisableScreenButtonsAppListSettings.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            AutoDisableScreenButtonsAppListSettings.this.loadPackages();
        }
    };
    private List<AppItem> mEnabledApps = new ArrayList();
    private List<AppItem> mDisabledApps = new ArrayList();

    /* loaded from: classes.dex */
    public class AppAdapter extends RecyclerView.Adapter<ViewHolder> {
        View.OnClickListener mClickListener = new View.OnClickListener() { // from class: com.android.settings.AutoDisableScreenButtonsAppListSettings.AppAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AppAdapter.this.showDialog((AppItem) view.getTag(AutoDisableScreenButtonsAppListSettings.TAG_APP_ITEM));
            }
        };
        private View mHeaderView;
        private List<AppItem> mItems;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView headerTitle;
            ImageView icon;
            TextView summary;
            TextView title;

            ViewHolder(View view) {
                super(view);
                if (view == AppAdapter.this.mHeaderView) {
                    return;
                }
                this.icon = (ImageView) view.findViewById(16908294);
                this.title = (TextView) view.findViewById(16908310);
                this.summary = (TextView) view.findViewById(16908304);
                this.headerTitle = (TextView) view.findViewById(R.id.header_title);
            }
        }

        public AppAdapter() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getFlagByIndex(int i) {
            if (i >= 0) {
                int[] iArr = AutoDisableScreenButtonsAppListSettings.mListFlags;
                if (i >= iArr.length) {
                    return 3;
                }
                return iArr[i];
            }
            return 3;
        }

        private int getIndexByFlag(int i) {
            int i2 = 0;
            while (true) {
                int[] iArr = AutoDisableScreenButtonsAppListSettings.mListFlags;
                if (i2 >= iArr.length) {
                    return 2;
                }
                if (iArr[i2] == i) {
                    return i2;
                }
                i2++;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            List<AppItem> list = this.mItems;
            if (list != null) {
                return this.mHeaderView != null ? list.size() + 1 : list.size();
            }
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (this.mHeaderView != null) {
                if (i == 0) {
                    return 3;
                }
                return this.mItems.get(i - 1).mType;
            }
            return this.mItems.get(i).mType;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (this.mHeaderView != null) {
                if (i == 0) {
                    return;
                }
                i--;
            }
            AppItem appItem = this.mItems.get(i);
            if (appItem.mType != 0) {
                if (appItem.mType == 1) {
                    viewHolder.headerTitle.setText(appItem.mLabel);
                    return;
                }
                return;
            }
            if (TextUtils.isEmpty(appItem.mDes)) {
                viewHolder.summary.setVisibility(8);
            } else {
                viewHolder.summary.setText(appItem.mDes);
                viewHolder.summary.setVisibility(0);
            }
            AutoDisableScreenButtonsAppListSettings.this.mApkIconLoader.loadIcon(viewHolder.icon, appItem.getPkg());
            viewHolder.title.setText(appItem.mLabel);
            viewHolder.itemView.setTag(AutoDisableScreenButtonsAppListSettings.TAG_APP_ITEM, appItem);
            viewHolder.itemView.setOnClickListener(this.mClickListener);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.auto_disable_screenbuttons_app_list_item, viewGroup, false);
            } else if (i == 1) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.auto_disable_screenbuttons_app_list_header, viewGroup, false);
            } else {
                view = this.mHeaderView;
                if (view == null || i != 3) {
                    view = null;
                }
            }
            return new ViewHolder(view);
        }

        public void setHeaderView(View view) {
            this.mHeaderView = view;
        }

        public void setItems(List<AppItem> list) {
            this.mItems = list;
        }

        protected void showDialog(final AppItem appItem) {
            AlertDialog.Builder negativeButton = new AlertDialog.Builder(AutoDisableScreenButtonsAppListSettings.this.mContext).setPositiveButton((CharSequence) null, (DialogInterface.OnClickListener) null).setNegativeButton(R.string.auto_disable_screenbuttons_cancel, (DialogInterface.OnClickListener) null);
            negativeButton.setSingleChoiceItems(AutoDisableScreenButtonsAppListSettings.this.mListChoices, getIndexByFlag(appItem.mFlag), new DialogInterface.OnClickListener() { // from class: com.android.settings.AutoDisableScreenButtonsAppListSettings.AppAdapter.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    int flagByIndex = AppAdapter.this.getFlagByIndex(i);
                    appItem.setFlag(flagByIndex);
                    AutoDisableScreenButtonsHelper.setFlag(AutoDisableScreenButtonsAppListSettings.this.mContext, appItem.getPkg(), flagByIndex);
                    dialogInterface.dismiss();
                    AutoDisableScreenButtonsAppListSettings.this.loadPackages();
                }
            });
            AutoDisableScreenButtonsAppListSettings.this.mDialog = negativeButton.create();
            AutoDisableScreenButtonsAppListSettings.this.mDialog.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AppItem {
        private ApplicationInfo mAppInfo;
        private String mDes;
        private int mFlag;
        private String mLabel;
        private int mType;

        public AppItem(ApplicationInfo applicationInfo, int i) {
            this.mType = -1;
            this.mAppInfo = applicationInfo;
            this.mLabel = applicationInfo.loadLabel(AutoDisableScreenButtonsAppListSettings.this.mPackageManager).toString().replaceAll("\\u00A0", " ").trim();
            this.mType = 0;
            setFlag(i);
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

        public void setFlag(int i) {
            this.mFlag = i;
            this.mDes = AutoDisableScreenButtonsAppListSettings.getAppText(AutoDisableScreenButtonsAppListSettings.this.mContext, getPkg(), i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getAppText(Context context, String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (i == 1) {
            return context.getString(R.string.auto_disable_screenbuttons_ask);
        }
        if (i == 2) {
            return context.getString(R.string.auto_disable_screenbuttons_auto);
        }
        if (i == 3) {
            return context.getString(R.string.auto_disable_screenbuttons_no);
        }
        return null;
    }

    private void initHeaderView() {
        View inflate;
        if (FeatureParser.getBoolean("is_pad", false) && (inflate = View.inflate(this.mContext, R.layout.auto_disable_screenbuttons_checkbox, null)) != null) {
            boolean booleanForUser = MiuiSettings.System.getBooleanForUser(this.mContext.getContentResolver(), "enable_auto_disable_screen_rotation", true, CrossUserUtils.getCurrentUserId());
            CheckBox checkBox = (CheckBox) inflate.findViewById(16908289);
            this.mCheckbox = checkBox;
            checkBox.setChecked(booleanForUser);
            this.mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.AutoDisableScreenButtonsAppListSettings.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    MiuiSettings.System.putBooleanForUser(AutoDisableScreenButtonsAppListSettings.this.mContext.getContentResolver(), "enable_auto_disable_screen_rotation", z, CrossUserUtils.getCurrentUserId());
                    AutoDisableScreenButtonsAppListSettings.this.getActivity().sendBroadcastAsUser(new Intent("miui.intent.action.CHANGE_AUTO_DISABLE_BTN_STATUS").putExtra("is_auto_disable_btn_rotation", z), new UserHandle(CrossUserUtils.getCurrentUserId()));
                }
            });
            inflate.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.AutoDisableScreenButtonsAppListSettings.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    AutoDisableScreenButtonsAppListSettings.this.mCheckbox.toggle();
                }
            });
            this.mAdapter.setHeaderView(inflate);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadPackages() {
        new AsyncTaskWithProgress<Void, Void>(getFragmentManager()) { // from class: com.android.settings.AutoDisableScreenButtonsAppListSettings.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");
                List<ResolveInfo> queryIntentActivities = AutoDisableScreenButtonsAppListSettings.this.mPackageManager.queryIntentActivities(intent, 0);
                HashSet hashSet = new HashSet();
                for (ResolveInfo resolveInfo : queryIntentActivities) {
                    ActivityInfo activityInfo = resolveInfo.activityInfo;
                    String str = activityInfo.packageName;
                    String str2 = activityInfo.name;
                    if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2) && !hashSet.contains(str)) {
                        int appFlag = AutoDisableScreenButtonsHelper.getAppFlag(AutoDisableScreenButtonsAppListSettings.this.mContext, str);
                        AppItem appItem = new AppItem(resolveInfo.activityInfo.applicationInfo, appFlag);
                        if (appFlag == 3) {
                            AutoDisableScreenButtonsAppListSettings.this.mDisabledApps.add(appItem);
                        } else {
                            AutoDisableScreenButtonsAppListSettings.this.mEnabledApps.add(appItem);
                        }
                        hashSet.add(str);
                    }
                }
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // miuix.os.AsyncTaskWithProgress, android.os.AsyncTask
            public void onPostExecute(Void r10) {
                super.onPostExecute((AnonymousClass4) r10);
                ArrayList arrayList = new ArrayList();
                Collections.sort(AutoDisableScreenButtonsAppListSettings.this.mEnabledApps, new Comparator<AppItem>() { // from class: com.android.settings.AutoDisableScreenButtonsAppListSettings.4.1
                    private final Collator sCollator = Collator.getInstance();

                    @Override // java.util.Comparator
                    public int compare(AppItem appItem, AppItem appItem2) {
                        return this.sCollator.compare(appItem.mLabel, appItem2.mLabel);
                    }
                });
                Collections.sort(AutoDisableScreenButtonsAppListSettings.this.mDisabledApps, new Comparator<AppItem>() { // from class: com.android.settings.AutoDisableScreenButtonsAppListSettings.4.2
                    private final Collator sCollator = Collator.getInstance();

                    @Override // java.util.Comparator
                    public int compare(AppItem appItem, AppItem appItem2) {
                        return this.sCollator.compare(appItem.mLabel, appItem2.mLabel);
                    }
                });
                if (AutoDisableScreenButtonsAppListSettings.this.mEnabledApps.size() > 0) {
                    AutoDisableScreenButtonsAppListSettings autoDisableScreenButtonsAppListSettings = AutoDisableScreenButtonsAppListSettings.this;
                    arrayList.add(new AppItem(autoDisableScreenButtonsAppListSettings.mContext.getResources().getQuantityString(R.plurals.auto_disable_screenbuttons_enabled_header_title, AutoDisableScreenButtonsAppListSettings.this.mEnabledApps.size(), Integer.valueOf(AutoDisableScreenButtonsAppListSettings.this.mEnabledApps.size()))));
                    arrayList.addAll(AutoDisableScreenButtonsAppListSettings.this.mEnabledApps);
                }
                if (AutoDisableScreenButtonsAppListSettings.this.mDisabledApps.size() > 0) {
                    AutoDisableScreenButtonsAppListSettings autoDisableScreenButtonsAppListSettings2 = AutoDisableScreenButtonsAppListSettings.this;
                    arrayList.add(new AppItem(autoDisableScreenButtonsAppListSettings2.mContext.getResources().getQuantityString(R.plurals.auto_disable_screenbuttons_disabled_header_title, AutoDisableScreenButtonsAppListSettings.this.mDisabledApps.size(), Integer.valueOf(AutoDisableScreenButtonsAppListSettings.this.mDisabledApps.size()))));
                    arrayList.addAll(AutoDisableScreenButtonsAppListSettings.this.mDisabledApps);
                }
                AutoDisableScreenButtonsAppListSettings.this.mAdapter.setItems(arrayList);
                AutoDisableScreenButtonsAppListSettings.this.mAdapter.notifyDataSetChanged();
                AutoDisableScreenButtonsAppListSettings.this.mEnabledApps.clear();
                AutoDisableScreenButtonsAppListSettings.this.mDisabledApps.clear();
            }
        }.setCancelable(false).execute(new Void[0]);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        JobDispatcher.scheduleJob(activity.getApplicationContext(), 44010);
        this.mApkIconLoader = new ApkIconLoader(this.mContext.getApplicationContext());
        this.mPackageManager = getPackageManager();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        this.mContext.registerReceiver(this.mPackageChangeReceiver, intentFilter);
        this.mListChoices = new String[]{getString(R.string.auto_disable_screenbuttons_ask), getString(R.string.auto_disable_screenbuttons_auto), getString(R.string.auto_disable_screenbuttons_no)};
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mContext.unregisterReceiver(this.mPackageChangeReceiver);
        this.mApkIconLoader.stop();
        Dialog dialog = this.mDialog;
        if (dialog != null && dialog.isShowing()) {
            this.mDialog.dismiss();
        }
        this.mDialog = null;
        super.onDestroy();
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mLayoutInflater = layoutInflater;
        View inflate = layoutInflater.inflate(R.layout.auto_disable_screenbuttons_app_list, viewGroup, false);
        if (viewGroup != null) {
            PreferenceFrameLayout.LayoutParams layoutParams = ((ViewGroup) viewGroup.getParent()).getLayoutParams();
            if (layoutParams instanceof PreferenceFrameLayout.LayoutParams) {
                layoutParams.removeBorders = true;
            }
        }
        return inflate;
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mRecyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        this.mAdapter = new AppAdapter();
        this.mRecyclerview.setLayoutManager(new LinearLayoutManager(this.mContext));
        initHeaderView();
        loadPackages();
        this.mRecyclerview.setAdapter(this.mAdapter);
    }
}
