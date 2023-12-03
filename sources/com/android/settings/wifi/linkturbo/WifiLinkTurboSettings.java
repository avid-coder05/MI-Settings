package com.android.settings.wifi.linkturbo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.ApkIconLoader;
import com.android.settings.R;
import com.android.settings.wifi.linkturbo.WifiLinkTurboOptions;
import java.util.ArrayList;
import java.util.List;
import miuix.appcompat.app.AppCompatActivity;
import miuix.slidingwidget.widget.SlidingButton;
import miuix.util.Log;

/* loaded from: classes2.dex */
public class WifiLinkTurboSettings extends AppCompatActivity implements WifiLinkTurboOptions.FragmentListener {
    public AppAdapter mAdapter;
    private RecyclerView mAppRecyclerView;
    public int mFlag;
    private LinkTurboAppDataTrafficTaskWithProgress mLinkTurboAppDataTrafficTaskWithProgress;
    public LinkTurboClient mLinkTurboClient;
    private PackageManager mPackageManager;
    private boolean mRecommendAppsLoaded;
    private boolean mTurboAppsLoaded;
    public List<AppItem> mRecommendApps = new ArrayList();
    public List<AppItem> mSupportApps = new ArrayList();
    private Handler mMainHandler = new Handler() { // from class: com.android.settings.wifi.linkturbo.WifiLinkTurboSettings.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int intValue = ((Integer) message.obj).intValue();
            Log.d("WifiLinkTurboSettings", "setLinkTurboOptions " + intValue);
            WifiLinkTurboSettings wifiLinkTurboSettings = WifiLinkTurboSettings.this;
            if (wifiLinkTurboSettings.mFlag != intValue) {
                wifiLinkTurboSettings.mFlag = intValue;
                wifiLinkTurboSettings.mAppRecyclerView.setVisibility(0);
                WifiLinkTurboSettings.this.loadPackages();
                WifiLinkTurboSettings wifiLinkTurboSettings2 = WifiLinkTurboSettings.this;
                wifiLinkTurboSettings2.mAdapter.setItems(wifiLinkTurboSettings2.mFlag == 0 ? wifiLinkTurboSettings2.mRecommendApps : wifiLinkTurboSettings2.mSupportApps);
            }
        }
    };

    /* loaded from: classes2.dex */
    public class AppAdapter extends RecyclerView.Adapter<ViewHolder> {
        ApkIconLoader mApkIconLoader;
        Context mContext;
        private List<AppItem> mItems;
        LayoutInflater mLayoutInflater;
        final int TAG_APP_ITEM = R.layout.link_turbo_app_list;
        View.OnClickListener mClickListener = new View.OnClickListener() { // from class: com.android.settings.wifi.linkturbo.WifiLinkTurboSettings.AppAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AppItem appItem = (AppItem) view.getTag(AppAdapter.this.TAG_APP_ITEM);
                boolean z = appItem.mIsChecked;
                if (z) {
                    if (z && WifiLinkTurboSettings.this.mLinkTurboClient.removeUidInLinkTurboWhiteList(appItem.mUid)) {
                        Log.d("WifiLinkTurboSettings", "removeUidInLinkTurboWhiteList is " + appItem.mUid);
                        appItem.mIsChecked = false;
                    }
                } else if (WifiLinkTurboSettings.this.mLinkTurboClient.isLinkTurboWhiteListReachMax()) {
                    Toast.makeText(AppAdapter.this.mContext, R.string.link_turbo_settings_all_app_reach_max, 0).show();
                } else if (WifiLinkTurboSettings.this.mLinkTurboClient.addUidToLinkTurboWhiteList(appItem.mUid)) {
                    Log.d("WifiLinkTurboSettings", "addUidToLinkTurboWhiteList is " + appItem.mUid);
                    appItem.mIsChecked = true;
                }
                ((SlidingButton) view.findViewById(R.id.slide_link_turbo)).setChecked(appItem.mIsChecked);
            }
        };

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public class ViewHolder extends RecyclerView.ViewHolder {
            SlidingButton button;
            TextView dayDataTraffic;
            ImageView icon;
            TextView monthDataTraffic;
            TextView title;

            ViewHolder(View view) {
                super(view);
                this.icon = (ImageView) view.findViewById(16908294);
                this.title = (TextView) view.findViewById(16908310);
                this.dayDataTraffic = (TextView) view.findViewById(R.id.day_data_traffic);
                this.monthDataTraffic = (TextView) view.findViewById(R.id.month_data_traffic);
                this.button = (SlidingButton) view.findViewById(R.id.slide_link_turbo);
            }
        }

        public AppAdapter(Context context) {
            this.mContext = context;
            this.mLayoutInflater = LayoutInflater.from(context);
            this.mApkIconLoader = new ApkIconLoader(context);
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
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            AppItem appItem = this.mItems.get(i);
            this.mApkIconLoader.loadIcon(viewHolder.icon, appItem.getPkg());
            viewHolder.title.setText(appItem.mLabel);
            viewHolder.dayDataTraffic.setText(WifiLinkTurboSettings.this.getString(R.string.link_turbo_usage_state_today) + ": " + FormatBytesUtil.formatBytes(appItem.mMobileDataTrafficOfThisDay));
            viewHolder.monthDataTraffic.setText(WifiLinkTurboSettings.this.getString(R.string.link_turbo_usage_state_month) + ": " + FormatBytesUtil.formatBytes(appItem.mMobileDataTrafficOfThisMonth));
            boolean z = WifiLinkTurboSettings.this.mFlag == 0;
            viewHolder.button.setVisibility(z ? 4 : 0);
            if (z) {
                return;
            }
            viewHolder.button.setChecked(appItem.mIsChecked);
            viewHolder.itemView.setTag(this.TAG_APP_ITEM, appItem);
            viewHolder.itemView.setOnClickListener(this.mClickListener);
            viewHolder.button.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.wifi.linkturbo.WifiLinkTurboSettings.AppAdapter.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    viewHolder.itemView.callOnClick();
                }
            });
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.link_turbo_app_mobileusage_list_item, viewGroup, false));
        }

        public void setItems(List<AppItem> list) {
            this.mItems = list;
            notifyDataSetChanged();
        }
    }

    /* loaded from: classes2.dex */
    public class AppItem {
        private ApplicationInfo mAppInfo;
        public boolean mIsChecked;
        public String mLabel;
        public long mMobileDataTrafficOfThisDay;
        public long mMobileDataTrafficOfThisMonth;
        public int mUid;

        public AppItem(ApplicationInfo applicationInfo, int i, boolean z, long j, long j2) {
            this.mIsChecked = false;
            this.mUid = -1;
            this.mAppInfo = applicationInfo;
            this.mLabel = applicationInfo.loadLabel(WifiLinkTurboSettings.this.mPackageManager).toString().replaceAll("\\u00A0", " ").trim();
            this.mMobileDataTrafficOfThisDay = j;
            this.mMobileDataTrafficOfThisMonth = j2;
            this.mUid = i;
            this.mIsChecked = z;
        }

        public String getPkg() {
            ApplicationInfo applicationInfo = this.mAppInfo;
            if (applicationInfo == null) {
                return null;
            }
            return applicationInfo.packageName;
        }
    }

    private void cancelLoadPackage() {
        LinkTurboAppDataTrafficTaskWithProgress linkTurboAppDataTrafficTaskWithProgress = this.mLinkTurboAppDataTrafficTaskWithProgress;
        if (linkTurboAppDataTrafficTaskWithProgress == null || linkTurboAppDataTrafficTaskWithProgress.isCancelled()) {
            return;
        }
        this.mLinkTurboAppDataTrafficTaskWithProgress.cancel(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadPackages() {
        int i = this.mFlag;
        if (i == 0 && this.mRecommendAppsLoaded) {
            return;
        }
        if (i == 0 || !this.mTurboAppsLoaded) {
            cancelLoadPackage();
            LinkTurboAppDataTrafficTaskWithProgress linkTurboAppDataTrafficTaskWithProgress = new LinkTurboAppDataTrafficTaskWithProgress();
            this.mLinkTurboAppDataTrafficTaskWithProgress = linkTurboAppDataTrafficTaskWithProgress;
            linkTurboAppDataTrafficTaskWithProgress.setContext(this);
            this.mLinkTurboAppDataTrafficTaskWithProgress.setLinkTurboClient(this.mLinkTurboClient);
            this.mLinkTurboAppDataTrafficTaskWithProgress.execute(new Void[0]);
        }
    }

    public void enableWifiLinkTurbo(boolean z) {
        LinkTurboClient linkTurboClient = this.mLinkTurboClient;
        if (linkTurboClient != null) {
            linkTurboClient.setLinkTurboEnable(z);
            Settings.System.putInt(getContentResolver(), "linkturbo_is_enable", z ? 1 : 0);
        }
    }

    @Override // com.android.settings.wifi.linkturbo.WifiLinkTurboOptions.FragmentListener
    public void enableWifiLinkTurboCallback(boolean z) {
        Log.d("WifiLinkTurboSettings", "enableWifiLinkTurbo " + z);
        enableWifiLinkTurbo(z);
        if (z) {
            this.mAppRecyclerView.setVisibility(0);
            loadPackages();
            return;
        }
        this.mAppRecyclerView.setVisibility(8);
        cancelLoadPackage();
    }

    public AppItem getAppItem(ApplicationInfo applicationInfo, int i, boolean z, long j, long j2) {
        return new AppItem(applicationInfo, i, z, j, j2);
    }

    public boolean isWifiLinkTurboEnabled() {
        LinkTurboClient linkTurboClient = this.mLinkTurboClient;
        if (linkTurboClient != null) {
            return linkTurboClient.getLinkTurboEnable();
        }
        return false;
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.wifi_link_turbo);
        this.mLinkTurboClient = new LinkTurboClient(this);
        this.mPackageManager = getPackageManager();
        this.mAppRecyclerView = (RecyclerView) findViewById(R.id.link_turbo_app);
        this.mAdapter = new AppAdapter(this);
        this.mAppRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.mAppRecyclerView.setAdapter(this.mAdapter);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        LinkTurboClient linkTurboClient = this.mLinkTurboClient;
        if (linkTurboClient != null) {
            linkTurboClient.ShutDownLinkTurboService();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        cancelLoadPackage();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.mFlag = LinkTurboUtils.getLinkTurboOptions(this);
        if (!isWifiLinkTurboEnabled()) {
            this.mAppRecyclerView.setVisibility(8);
            return;
        }
        this.mAppRecyclerView.setVisibility(0);
        loadPackages();
    }

    public void setAllApps(List<AppItem> list) {
        if (this.mFlag == 0) {
            this.mRecommendApps = list;
            this.mRecommendAppsLoaded = true;
            return;
        }
        this.mSupportApps = list;
        this.mTurboAppsLoaded = true;
    }

    @Override // com.android.settings.wifi.linkturbo.WifiLinkTurboOptions.FragmentListener
    public void setLinkTurboOptionsCallback(int i) {
        this.mMainHandler.removeMessages(0);
        Message obtainMessage = this.mMainHandler.obtainMessage(0);
        obtainMessage.obj = Integer.valueOf(i);
        this.mMainHandler.sendMessageDelayed(obtainMessage, 500L);
    }
}
