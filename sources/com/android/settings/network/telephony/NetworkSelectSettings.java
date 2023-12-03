package com.android.settings.network.telephony;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.CellIdentity;
import android.telephony.CellInfo;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.internal.telephony.OperatorInfo;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.network.telephony.NetworkScanHelper;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

/* loaded from: classes2.dex */
public class NetworkSelectSettings extends DashboardFragment {
    List<CellInfo> mCellInfoList;
    private List<String> mForbiddenPlmns;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private NetworkScanHelper mNetworkScanHelper;
    PreferenceCategory mPreferenceCategory;
    private View mProgressHeader;
    private long mRequestIdManualNetworkScan;
    private long mRequestIdManualNetworkSelect;
    NetworkOperatorPreference mSelectedPreference;
    private Preference mStatusMessagePreference;
    TelephonyManager mTelephonyManager;
    private boolean mUseNewApi;
    private long mWaitingForNumberOfScanResults;
    private int mSubId = -1;
    private boolean mShow4GForLTE = false;
    private final ExecutorService mNetworkScanExecutor = Executors.newFixedThreadPool(1);
    boolean mIsAggregationEnabled = false;
    private final Handler mHandler = new Handler() { // from class: com.android.settings.network.telephony.NetworkSelectSettings.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                boolean booleanValue = ((Boolean) message.obj).booleanValue();
                NetworkSelectSettings.this.stopNetworkQuery();
                NetworkSelectSettings.this.setProgressBarVisible(false);
                NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
                NetworkOperatorPreference networkOperatorPreference = NetworkSelectSettings.this.mSelectedPreference;
                if (networkOperatorPreference != null) {
                    networkOperatorPreference.setSummary(booleanValue ? R.string.network_connected : R.string.network_could_not_connect);
                } else {
                    Log.e("NetworkSelectSettings", "No preference to update!");
                }
            } else if (i == 2) {
                List<CellInfo> list = (List) message.obj;
                if (NetworkSelectSettings.this.mRequestIdManualNetworkScan < NetworkSelectSettings.this.mRequestIdManualNetworkSelect) {
                    Log.d("NetworkSelectSettings", "CellInfoList (drop): " + CellInfoUtil.cellInfoListToString(new ArrayList(list)));
                    return;
                }
                NetworkSelectSettings.access$310(NetworkSelectSettings.this);
                if (NetworkSelectSettings.this.mWaitingForNumberOfScanResults <= 0 && !NetworkSelectSettings.this.isResumed()) {
                    NetworkSelectSettings.this.stopNetworkQuery();
                }
                NetworkSelectSettings networkSelectSettings = NetworkSelectSettings.this;
                networkSelectSettings.mCellInfoList = networkSelectSettings.doAggregation(list);
                Log.d("NetworkSelectSettings", "CellInfoList: " + CellInfoUtil.cellInfoListToString(NetworkSelectSettings.this.mCellInfoList));
                List<CellInfo> list2 = NetworkSelectSettings.this.mCellInfoList;
                if (list2 == null || list2.size() == 0) {
                    if (NetworkSelectSettings.this.getPreferenceScreen().isEnabled()) {
                        NetworkSelectSettings.this.addMessagePreference(R.string.empty_networks_list);
                        NetworkSelectSettings.this.setProgressBarVisible(true);
                        return;
                    }
                    return;
                }
                NetworkOperatorPreference updateAllPreferenceCategory = NetworkSelectSettings.this.updateAllPreferenceCategory();
                if (updateAllPreferenceCategory != null) {
                    NetworkSelectSettings networkSelectSettings2 = NetworkSelectSettings.this;
                    if (networkSelectSettings2.mSelectedPreference != null) {
                        networkSelectSettings2.mSelectedPreference = updateAllPreferenceCategory;
                    }
                } else if (!NetworkSelectSettings.this.getPreferenceScreen().isEnabled() && updateAllPreferenceCategory == null) {
                    NetworkSelectSettings.this.mSelectedPreference.setSummary(R.string.network_connecting);
                }
                NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
            } else if (i == 3) {
                NetworkSelectSettings.this.stopNetworkQuery();
                Log.i("NetworkSelectSettings", "Network scan failure " + message.arg1 + ": scan request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkScan) + ", waiting for scan results = " + NetworkSelectSettings.this.mWaitingForNumberOfScanResults + ", select request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkSelect));
                if (NetworkSelectSettings.this.mRequestIdManualNetworkScan < NetworkSelectSettings.this.mRequestIdManualNetworkSelect) {
                    return;
                }
                if (NetworkSelectSettings.this.getPreferenceScreen().isEnabled()) {
                    NetworkSelectSettings.this.addMessagePreference(R.string.network_query_error);
                    return;
                }
                NetworkSelectSettings.this.clearPreferenceSummary();
                NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
            } else if (i != 4) {
            } else {
                NetworkSelectSettings.this.stopNetworkQuery();
                Log.d("NetworkSelectSettings", "Network scan complete: scan request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkScan) + ", waiting for scan results = " + NetworkSelectSettings.this.mWaitingForNumberOfScanResults + ", select request 0x" + Long.toHexString(NetworkSelectSettings.this.mRequestIdManualNetworkSelect));
                if (NetworkSelectSettings.this.mRequestIdManualNetworkScan < NetworkSelectSettings.this.mRequestIdManualNetworkSelect) {
                    return;
                }
                if (!NetworkSelectSettings.this.getPreferenceScreen().isEnabled()) {
                    NetworkSelectSettings.this.clearPreferenceSummary();
                    NetworkSelectSettings.this.getPreferenceScreen().setEnabled(true);
                    return;
                }
                NetworkSelectSettings networkSelectSettings3 = NetworkSelectSettings.this;
                if (networkSelectSettings3.mCellInfoList == null) {
                    networkSelectSettings3.addMessagePreference(R.string.empty_networks_list);
                }
            }
        }
    };
    private final NetworkScanHelper.NetworkScanCallback mCallback = new NetworkScanHelper.NetworkScanCallback() { // from class: com.android.settings.network.telephony.NetworkSelectSettings.2
        @Override // com.android.settings.network.telephony.NetworkScanHelper.NetworkScanCallback
        public void onComplete() {
            NetworkSelectSettings.this.mHandler.obtainMessage(4).sendToTarget();
        }

        @Override // com.android.settings.network.telephony.NetworkScanHelper.NetworkScanCallback
        public void onError(int i) {
            NetworkSelectSettings.this.mHandler.obtainMessage(3, i, 0).sendToTarget();
        }

        @Override // com.android.settings.network.telephony.NetworkScanHelper.NetworkScanCallback
        public void onResults(List<CellInfo> list) {
            NetworkSelectSettings.this.mHandler.obtainMessage(2, list).sendToTarget();
        }
    };

    static /* synthetic */ long access$310(NetworkSelectSettings networkSelectSettings) {
        long j = networkSelectSettings.mWaitingForNumberOfScanResults;
        networkSelectSettings.mWaitingForNumberOfScanResults = j - 1;
        return j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addMessagePreference(int i) {
        setProgressBarVisible(false);
        this.mStatusMessagePreference.setTitle(i);
        this.mPreferenceCategory.removeAll();
        this.mPreferenceCategory.addPreference(this.mStatusMessagePreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearPreferenceSummary() {
        int preferenceCount = this.mPreferenceCategory.getPreferenceCount();
        while (preferenceCount > 0) {
            preferenceCount--;
            ((NetworkOperatorPreference) this.mPreferenceCategory.getPreference(preferenceCount)).setSummary((CharSequence) null);
        }
    }

    private void forceUpdateConnectedPreferenceCategory() {
        ServiceState serviceState;
        List networkRegistrationInfoListForTransportType;
        int dataState = this.mTelephonyManager.getDataState();
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (dataState != 2 || (serviceState = telephonyManager.getServiceState()) == null || (networkRegistrationInfoListForTransportType = serviceState.getNetworkRegistrationInfoListForTransportType(1)) == null || networkRegistrationInfoListForTransportType.size() == 0) {
            return;
        }
        if (this.mForbiddenPlmns == null) {
            updateForbiddenPlmns();
        }
        Iterator it = networkRegistrationInfoListForTransportType.iterator();
        while (it.hasNext()) {
            CellIdentity cellIdentity = ((NetworkRegistrationInfo) it.next()).getCellIdentity();
            if (cellIdentity != null) {
                NetworkOperatorPreference networkOperatorPreference = new NetworkOperatorPreference(getPrefContext(), cellIdentity, this.mForbiddenPlmns, this.mShow4GForLTE);
                if (!networkOperatorPreference.isForbiddenNetwork()) {
                    networkOperatorPreference.setSummary(R.string.network_connected);
                    networkOperatorPreference.setIcon(4);
                    this.mPreferenceCategory.addPreference(networkOperatorPreference);
                    return;
                }
            }
        }
    }

    private long getNewRequestId() {
        return Math.max(this.mRequestIdManualNetworkSelect, this.mRequestIdManualNetworkScan) + 1;
    }

    private boolean isProgressBarVisible() {
        View view = this.mProgressHeader;
        return view != null && view.getVisibility() == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$doAggregation$1(String str, Class cls, CellInfo cellInfo) {
        return CellInfoUtil.getNetworkTitle(cellInfo.getCellIdentity(), CellInfoUtil.getCellIdentityMccMnc(cellInfo.getCellIdentity())).equals(str) && cellInfo.getClass().equals(cls);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreferenceTreeClick$0(OperatorInfo operatorInfo) {
        Message obtainMessage = this.mHandler.obtainMessage(1);
        obtainMessage.obj = Boolean.valueOf(this.mTelephonyManager.setNetworkSelectionModeManual(operatorInfo, true));
        obtainMessage.sendToTarget();
    }

    private void startNetworkQuery() {
        setProgressBarVisible(true);
        if (this.mNetworkScanHelper != null) {
            this.mRequestIdManualNetworkScan = getNewRequestId();
            this.mWaitingForNumberOfScanResults = 2L;
            this.mNetworkScanHelper.startNetworkScan(this.mUseNewApi ? 2 : 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopNetworkQuery() {
        setProgressBarVisible(false);
        NetworkScanHelper networkScanHelper = this.mNetworkScanHelper;
        if (networkScanHelper != null) {
            this.mWaitingForNumberOfScanResults = 0L;
            networkScanHelper.stopNetworkQuery();
        }
    }

    List<CellInfo> doAggregation(List<CellInfo> list) {
        if (!this.mIsAggregationEnabled) {
            Log.d("NetworkSelectSettings", "no aggregation");
            return new ArrayList(list);
        }
        ArrayList arrayList = new ArrayList();
        for (CellInfo cellInfo : list) {
            final String networkTitle = CellInfoUtil.getNetworkTitle(cellInfo.getCellIdentity(), CellInfoUtil.getCellIdentityMccMnc(cellInfo.getCellIdentity()));
            final Class<?> cls = cellInfo.getClass();
            if (!arrayList.stream().anyMatch(new Predicate() { // from class: com.android.settings.network.telephony.NetworkSelectSettings$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$doAggregation$1;
                    lambda$doAggregation$1 = NetworkSelectSettings.lambda$doAggregation$1(networkTitle, cls, (CellInfo) obj);
                    return lambda$doAggregation$1;
                }
            })) {
                arrayList.add(cellInfo);
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NetworkSelectSettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1581;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.choose_network;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mUseNewApi = getContext().getResources().getBoolean(17891545);
        this.mSubId = getArguments().getInt("android.provider.extra.SUB_ID");
        this.mPreferenceCategory = (PreferenceCategory) findPreference("network_operators_preference");
        Preference preference = new Preference(getContext());
        this.mStatusMessagePreference = preference;
        preference.setSelectable(false);
        this.mSelectedPreference = null;
        TelephonyManager createForSubscriptionId = ((TelephonyManager) getContext().getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        this.mTelephonyManager = createForSubscriptionId;
        this.mNetworkScanHelper = new NetworkScanHelper(createForSubscriptionId, this.mCallback, this.mNetworkScanExecutor);
        PersistableBundle configForSubId = ((CarrierConfigManager) getContext().getSystemService("carrier_config")).getConfigForSubId(this.mSubId);
        if (configForSubId != null) {
            this.mShow4GForLTE = configForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
        }
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        this.mIsAggregationEnabled = getContext().getResources().getBoolean(R.bool.config_network_selection_list_aggregation_enabled);
        Log.d("NetworkSelectSettings", "init: mUseNewApi:" + this.mUseNewApi + " ,mIsAggregationEnabled:" + this.mIsAggregationEnabled);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        stopNetworkQuery();
        this.mNetworkScanExecutor.shutdown();
        super.onDestroy();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference != this.mSelectedPreference) {
            stopNetworkQuery();
            clearPreferenceSummary();
            NetworkOperatorPreference networkOperatorPreference = this.mSelectedPreference;
            if (networkOperatorPreference != null) {
                networkOperatorPreference.setSummary(R.string.network_disconnected);
            }
            NetworkOperatorPreference networkOperatorPreference2 = (NetworkOperatorPreference) preference;
            this.mSelectedPreference = networkOperatorPreference2;
            networkOperatorPreference2.setSummary(R.string.network_connecting);
            this.mMetricsFeatureProvider.action(getContext(), 1210, new Pair[0]);
            setProgressBarVisible(true);
            getPreferenceScreen().setEnabled(false);
            this.mRequestIdManualNetworkSelect = getNewRequestId();
            this.mWaitingForNumberOfScanResults = 2L;
            final OperatorInfo operatorInfo = this.mSelectedPreference.getOperatorInfo();
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.network.telephony.NetworkSelectSettings$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkSelectSettings.this.lambda$onPreferenceTreeClick$0(operatorInfo);
                }
            });
        }
        return true;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        updateForbiddenPlmns();
        if (!isProgressBarVisible() && this.mWaitingForNumberOfScanResults <= 0) {
            startNetworkQuery();
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.mWaitingForNumberOfScanResults <= 0) {
            stopNetworkQuery();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (getActivity() != null) {
            this.mProgressHeader = setPinnedHeaderView(R.layout.progress_header).findViewById(R.id.progress_bar_animation);
            setProgressBarVisible(false);
        }
        forceUpdateConnectedPreferenceCategory();
    }

    protected void setProgressBarVisible(boolean z) {
        View view = this.mProgressHeader;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0048  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0072  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0079  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    com.android.settings.network.telephony.NetworkOperatorPreference updateAllPreferenceCategory() {
        /*
            r10 = this;
            androidx.preference.PreferenceCategory r0 = r10.mPreferenceCategory
            int r0 = r0.getPreferenceCount()
        L6:
            java.util.List<android.telephony.CellInfo> r1 = r10.mCellInfoList
            int r1 = r1.size()
            if (r0 <= r1) goto L1a
            int r0 = r0 + (-1)
            androidx.preference.PreferenceCategory r1 = r10.mPreferenceCategory
            androidx.preference.Preference r2 = r1.getPreference(r0)
            r1.removePreference(r2)
            goto L6
        L1a:
            r1 = 0
            r2 = 0
            r3 = r1
            r4 = r2
        L1e:
            java.util.List<android.telephony.CellInfo> r5 = r10.mCellInfoList
            int r5 = r5.size()
            if (r3 >= r5) goto L7f
            java.util.List<android.telephony.CellInfo> r5 = r10.mCellInfoList
            java.lang.Object r5 = r5.get(r3)
            android.telephony.CellInfo r5 = (android.telephony.CellInfo) r5
            if (r3 >= r0) goto L45
            androidx.preference.PreferenceCategory r6 = r10.mPreferenceCategory
            androidx.preference.Preference r6 = r6.getPreference(r3)
            boolean r7 = r6 instanceof com.android.settings.network.telephony.NetworkOperatorPreference
            if (r7 == 0) goto L40
            com.android.settings.network.telephony.NetworkOperatorPreference r6 = (com.android.settings.network.telephony.NetworkOperatorPreference) r6
            r6.updateCell(r5)
            goto L46
        L40:
            androidx.preference.PreferenceCategory r7 = r10.mPreferenceCategory
            r7.removePreference(r6)
        L45:
            r6 = r2
        L46:
            if (r6 != 0) goto L5d
            com.android.settings.network.telephony.NetworkOperatorPreference r6 = new com.android.settings.network.telephony.NetworkOperatorPreference
            android.content.Context r7 = r10.getPrefContext()
            java.util.List<java.lang.String> r8 = r10.mForbiddenPlmns
            boolean r9 = r10.mShow4GForLTE
            r6.<init>(r7, r5, r8, r9)
            r6.setOrder(r3)
            androidx.preference.PreferenceCategory r5 = r10.mPreferenceCategory
            r5.addPreference(r6)
        L5d:
            java.lang.String r5 = r6.getOperatorName()
            r6.setKey(r5)
            java.util.List<android.telephony.CellInfo> r5 = r10.mCellInfoList
            java.lang.Object r5 = r5.get(r3)
            android.telephony.CellInfo r5 = (android.telephony.CellInfo) r5
            boolean r5 = r5.isRegistered()
            if (r5 == 0) goto L79
            int r4 = com.android.settings.R.string.network_connected
            r6.setSummary(r4)
            r4 = r6
            goto L7c
        L79:
            r6.setSummary(r2)
        L7c:
            int r3 = r3 + 1
            goto L1e
        L7f:
            java.util.List<android.telephony.CellInfo> r0 = r10.mCellInfoList
            int r0 = r0.size()
            if (r1 >= r0) goto La6
            java.util.List<android.telephony.CellInfo> r0 = r10.mCellInfoList
            java.lang.Object r0 = r0.get(r1)
            android.telephony.CellInfo r0 = (android.telephony.CellInfo) r0
            com.android.settings.network.telephony.NetworkOperatorPreference r2 = r10.mSelectedPreference
            if (r2 == 0) goto La3
            boolean r0 = r2.isSameCell(r0)
            if (r0 == 0) goto La3
            androidx.preference.PreferenceCategory r0 = r10.mPreferenceCategory
            androidx.preference.Preference r0 = r0.getPreference(r1)
            com.android.settings.network.telephony.NetworkOperatorPreference r0 = (com.android.settings.network.telephony.NetworkOperatorPreference) r0
            r10.mSelectedPreference = r0
        La3:
            int r1 = r1 + 1
            goto L7f
        La6:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.NetworkSelectSettings.updateAllPreferenceCategory():com.android.settings.network.telephony.NetworkOperatorPreference");
    }

    void updateForbiddenPlmns() {
        String[] forbiddenPlmns = this.mTelephonyManager.getForbiddenPlmns();
        this.mForbiddenPlmns = forbiddenPlmns != null ? Arrays.asList(forbiddenPlmns) : new ArrayList<>();
    }
}
