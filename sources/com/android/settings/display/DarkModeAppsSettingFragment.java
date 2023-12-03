package com.android.settings.display;

import android.app.ActionBar;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.display.DarkModeAppsListAdapter;
import com.android.settings.display.util.BaseFragment;
import com.android.settings.display.util.DarkModeAppCacheManager;
import com.miui.darkmode.DarkModeAppDetailInfo;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.appcompat.app.AlertDialog;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes.dex */
public class DarkModeAppsSettingFragment extends BaseFragment implements DarkModeAppsListAdapter.OnAppCheckedListener {
    private List<DarkModeAppInfo> mAppList = new ArrayList();
    private ArrayList<Integer> mArray;
    private AlertDialog mConfirmDialog;
    private Context mContext;
    private DarkModeAppCacheManager mDarkModeAppCacheManager;
    private DarkModeAppsListAdapter mDarkModeAppsListAdapter;
    private RecyclerView mDarkModeAppsRecyclerView;
    private TextView mEmptyView;
    private boolean mShouldShowConfirmDialog;
    private List<UsageStats> mStats;
    private Handler mUIHandler;
    private UsageStatsManager mUsageStatsManager;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MainHandler extends Handler {
        private WeakReference<DarkModeAppsSettingFragment> mOuterRef;

        public MainHandler(DarkModeAppsSettingFragment darkModeAppsSettingFragment) {
            this.mOuterRef = new WeakReference<>(darkModeAppsSettingFragment);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            DarkModeAppsSettingFragment darkModeAppsSettingFragment = this.mOuterRef.get();
            if (darkModeAppsSettingFragment != null && message.what == 1) {
                darkModeAppsSettingFragment.mArray = message.getData().getIntegerArrayList("array");
                darkModeAppsSettingFragment.removeGameFromList(darkModeAppsSettingFragment.mArray);
            }
        }
    }

    private List<UsageStats> getUsageStats() {
        long currentTimeMillis = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) this.mContext.getSystemService(UsageStatsManager.class);
        this.mUsageStatsManager = usageStatsManager;
        return usageStatsManager.queryUsageStats(0, currentTimeMillis - 3600000, currentTimeMillis);
    }

    private void initAppsList() {
        if (this.mUIHandler == null) {
            this.mUIHandler = new MainHandler(this);
        }
        this.mUIHandler.post(new Runnable() { // from class: com.android.settings.display.DarkModeAppsSettingFragment.1
            @Override // java.lang.Runnable
            public void run() {
                List<DarkModeAppDetailInfo> darkModeAppInfoList = DarkModeAppsSettingFragment.this.mDarkModeAppCacheManager.getDarkModeAppInfoList();
                DarkModeAppsSettingFragment.this.mAppList.clear();
                Iterator<DarkModeAppDetailInfo> it = darkModeAppInfoList.iterator();
                while (it.hasNext()) {
                    DarkModeAppInfo darkModeAppInfo = new DarkModeAppInfo(it.next());
                    int i = 0;
                    Iterator it2 = DarkModeAppsSettingFragment.this.mStats.iterator();
                    while (true) {
                        if (it2.hasNext()) {
                            UsageStats usageStats = (UsageStats) it2.next();
                            if (usageStats.getPackageName().equals(darkModeAppInfo.getPkgName())) {
                                darkModeAppInfo.setLastTimeUsed(usageStats.getLastTimeUsed());
                                DarkModeAppsSettingFragment.this.mStats.remove(i);
                                break;
                            }
                            i++;
                        }
                    }
                    DarkModeAppsSettingFragment.this.mAppList.add(darkModeAppInfo);
                }
                DarkModeAppsSettingFragment.this.mDarkModeAppsListAdapter.refreshAppList(DarkModeAppsSettingFragment.this.mAppList);
                DarkModeAppsSettingFragment.this.mDarkModeAppsListAdapter.setOnItemClickListener(new DarkModeAppsListAdapter.OnItemClickListener() { // from class: com.android.settings.display.DarkModeAppsSettingFragment.1.1
                    @Override // com.android.settings.display.DarkModeAppsListAdapter.OnItemClickListener
                    public void onItemClick(SlidingButton slidingButton, int i2) {
                        DarkModeAppInfo darkModeAppInfo2 = DarkModeAppsSettingFragment.this.mDarkModeAppsListAdapter.getData().get(i2);
                        if (darkModeAppInfo2 != null) {
                            DarkModeAppsSettingFragment.this.onAppChecked(slidingButton, !darkModeAppInfo2.isEnabled(), darkModeAppInfo2);
                        }
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAppChecked$0(CompoundButton compoundButton, DarkModeAppInfo darkModeAppInfo, DialogInterface dialogInterface, int i) {
        compoundButton.setChecked(false);
        this.mDarkModeAppsListAdapter.setAppDarkMode(darkModeAppInfo, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAppChecked$1(CompoundButton compoundButton, DarkModeAppInfo darkModeAppInfo, DialogInterface dialogInterface, int i) {
        compoundButton.setChecked(true);
        this.mDarkModeAppsListAdapter.setAppDarkMode(darkModeAppInfo, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAppChecked$2(DialogInterface dialogInterface) {
        if (this.mConfirmDialog.isChecked()) {
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "dark_mode_show_confirm_dialog", 1, -2);
            this.mShouldShowConfirmDialog = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeGameFromList(ArrayList<Integer> arrayList) {
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                if (arrayList.get(size).equals(1)) {
                    Log.d("DarkModeAppsSettingFragment", "removeGameFromList App: " + this.mAppList.get(size).getPkgName());
                    this.mAppList.remove(size);
                }
            }
        }
        this.mDarkModeAppsListAdapter.refreshAppList(this.mAppList);
    }

    @Override // com.android.settings.display.util.BaseFragment
    protected void initView() {
        this.mContext = getContext();
        this.mEmptyView = (TextView) findViewById(16908292);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.apps_list);
        this.mDarkModeAppsRecyclerView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.mDarkModeAppCacheManager = DarkModeAppCacheManager.getInstance(getActivity());
        this.mStats = getUsageStats();
        initAppsList();
        DarkModeAppsListAdapter darkModeAppsListAdapter = new DarkModeAppsListAdapter(getActivity(), this.mAppList, this);
        this.mDarkModeAppsListAdapter = darkModeAppsListAdapter;
        this.mDarkModeAppsRecyclerView.setAdapter(darkModeAppsListAdapter);
        this.mShouldShowConfirmDialog = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "dark_mode_show_confirm_dialog", 0, -2) == 0;
    }

    @Override // com.android.settings.display.DarkModeAppsListAdapter.OnAppCheckedListener
    public void onAppChecked(final CompoundButton compoundButton, boolean z, final DarkModeAppInfo darkModeAppInfo) {
        if (compoundButton == null || darkModeAppInfo == null) {
            return;
        }
        Log.i("DarkModeAppsSettingFragment", "onAppChecked, appInfo:" + darkModeAppInfo.getPkgName() + ", isChecked:" + z);
        if (!this.mShouldShowConfirmDialog || !z) {
            compoundButton.setChecked(z);
            this.mDarkModeAppsListAdapter.setAppDarkMode(darkModeAppInfo, z);
            return;
        }
        AlertDialog alertDialog = this.mConfirmDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mConfirmDialog.dismiss();
        }
        AlertDialog create = new AlertDialog.Builder(this.mContext).setCancelable(false).setTitle(R.string.dark_mode_alert_dialog_title).setMessage(R.string.dark_mode_alert_dialog_message).setCheckBox(false, this.mContext.getResources().getString(R.string.dark_mode_alert_dialog_checkbox)).setNegativeButton(R.string.dark_mode_alert_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.display.DarkModeAppsSettingFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                DarkModeAppsSettingFragment.this.lambda$onAppChecked$0(compoundButton, darkModeAppInfo, dialogInterface, i);
            }
        }).setPositiveButton(R.string.dark_mode_alert_dialog_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.display.DarkModeAppsSettingFragment$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                DarkModeAppsSettingFragment.this.lambda$onAppChecked$1(compoundButton, darkModeAppInfo, dialogInterface, i);
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.display.DarkModeAppsSettingFragment$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                DarkModeAppsSettingFragment.this.lambda$onAppChecked$2(dialogInterface);
            }
        }).create();
        this.mConfirmDialog = create;
        create.show();
    }

    @Override // com.android.settings.display.util.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.settings.display.util.BaseFragment
    protected int onCreateViewLayout() {
        return R.layout.dark_mode_apps_setting;
    }

    @Override // com.android.settings.display.util.BaseFragment
    protected int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        Handler handler = this.mUIHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            this.mUIHandler = null;
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }
}
