package com.android.settings.wifi.details2;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.wifi.WifiDialog2;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.wifitrackerlib.NetworkDetailsTracker;
import com.android.wifitrackerlib.WifiEntry;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class WifiNetworkDetailsFragment2 extends RestrictedDashboardFragment implements WifiDialog2.WifiDialog2Listener {
    List<AbstractPreferenceController> mControllers;
    boolean mIsUiRestricted;
    NetworkDetailsTracker mNetworkDetailsTracker;
    private WifiDetailPreferenceController2 mWifiDetailPreferenceController2;
    private List<WifiDialog2.WifiDialog2Listener> mWifiDialogListeners;
    private HandlerThread mWorkerThread;

    public WifiNetworkDetailsFragment2() {
        super("no_config_wifi");
        this.mWifiDialogListeners = new ArrayList();
    }

    private void clearWifiEntryCallback() {
        WifiEntry wifiEntry;
        NetworkDetailsTracker networkDetailsTracker = this.mNetworkDetailsTracker;
        if (networkDetailsTracker == null || (wifiEntry = networkDetailsTracker.getWifiEntry()) == null) {
            return;
        }
        wifiEntry.setListener(null);
    }

    private boolean isEditable() {
        WifiEntry wifiEntry;
        NetworkDetailsTracker networkDetailsTracker = this.mNetworkDetailsTracker;
        if (networkDetailsTracker == null || (wifiEntry = networkDetailsTracker.getWifiEntry()) == null) {
            return false;
        }
        return wifiEntry.isSaved();
    }

    private void setupNetworksDetailTracker() {
        if (this.mNetworkDetailsTracker != null) {
            return;
        }
        Context context = getContext();
        HandlerThread handlerThread = new HandlerThread("WifiNetworkDetailsFrg2{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mNetworkDetailsTracker = FeatureFactory.getFactory(context).getWifiTrackerLibProvider().createNetworkDetailsTracker(getSettingsLifecycle(), context, new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.details2.WifiNetworkDetailsFragment2.1
            public long millis() {
                return SystemClock.elapsedRealtime();
            }
        }, 15000L, 10000L, getArguments().getString("key_chosen_wifientry_key"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        this.mControllers = new ArrayList();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        setupNetworksDetailTracker();
        WifiEntry wifiEntry = this.mNetworkDetailsTracker.getWifiEntry();
        WifiSecondSummaryController2 wifiSecondSummaryController2 = new WifiSecondSummaryController2(context);
        wifiSecondSummaryController2.setWifiEntry(wifiEntry);
        this.mControllers.add(wifiSecondSummaryController2);
        WifiDetailPreferenceController2 newInstance = WifiDetailPreferenceController2.newInstance(wifiEntry, connectivityManager, context, this, new Handler(Looper.getMainLooper()), getSettingsLifecycle(), (WifiManager) context.getSystemService(WifiManager.class), this.mMetricsFeatureProvider);
        this.mWifiDetailPreferenceController2 = newInstance;
        this.mControllers.add(newInstance);
        WifiAutoConnectPreferenceController2 wifiAutoConnectPreferenceController2 = new WifiAutoConnectPreferenceController2(context);
        wifiAutoConnectPreferenceController2.setWifiEntry(wifiEntry);
        this.mControllers.add(wifiAutoConnectPreferenceController2);
        AddDevicePreferenceController2 addDevicePreferenceController2 = new AddDevicePreferenceController2(context);
        addDevicePreferenceController2.setWifiEntry(wifiEntry);
        this.mControllers.add(addDevicePreferenceController2);
        WifiMeteredPreferenceController2 wifiMeteredPreferenceController2 = new WifiMeteredPreferenceController2(context, wifiEntry);
        this.mControllers.add(wifiMeteredPreferenceController2);
        WifiPrivacyPreferenceController2 wifiPrivacyPreferenceController2 = new WifiPrivacyPreferenceController2(context);
        wifiPrivacyPreferenceController2.setWifiEntry(wifiEntry);
        this.mControllers.add(wifiPrivacyPreferenceController2);
        WifiSubscriptionDetailPreferenceController2 wifiSubscriptionDetailPreferenceController2 = new WifiSubscriptionDetailPreferenceController2(context);
        wifiSubscriptionDetailPreferenceController2.setWifiEntry(wifiEntry);
        this.mControllers.add(wifiSubscriptionDetailPreferenceController2);
        this.mWifiDialogListeners.add(this.mWifiDetailPreferenceController2);
        this.mWifiDialogListeners.add(wifiPrivacyPreferenceController2);
        this.mWifiDialogListeners.add(wifiMeteredPreferenceController2);
        return this.mControllers;
    }

    protected void displayPreferenceControllers() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (AbstractPreferenceController abstractPreferenceController : this.mControllers) {
            if (!(abstractPreferenceController instanceof WifiDetailPreferenceController2)) {
                abstractPreferenceController.displayPreference(preferenceScreen);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "WifiNetworkDetailsFrg2";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 849;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.wifi_network_details_fragment2;
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setIfOnlyAvailableForAdmins(true);
        this.mIsUiRestricted = isUiRestricted();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (getActivity() == null || this.mWifiDetailPreferenceController2 == null) {
            return null;
        }
        return WifiDialog2.createModal(getActivity(), this, this.mNetworkDetailsTracker.getWifiEntry(), 2);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (!this.mIsUiRestricted && isEditable()) {
            MenuItem add = menu.add(0, 1, 0, R.string.wifi_modify);
            add.setIcon(17302782);
            add.setShowAsAction(2);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mWorkerThread.quit();
        super.onDestroy();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (this.mWifiDetailPreferenceController2.canModifyNetwork()) {
            showDialog(1);
        } else {
            RestrictedLockUtils.EnforcedAdmin deviceOwner = RestrictedLockUtilsInternal.getDeviceOwner(getContext());
            if (deviceOwner == null) {
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getContext().getSystemService("device_policy");
                int managedProfileId = Utils.getManagedProfileId((UserManager) getContext().getSystemService("user"), UserHandle.myUserId());
                if (managedProfileId != -10000) {
                    deviceOwner = new RestrictedLockUtils.EnforcedAdmin(devicePolicyManager.getProfileOwnerAsUser(managedProfileId), null, UserHandle.of(managedProfileId));
                }
            }
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(), deviceOwner);
        }
        return true;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mIsUiRestricted) {
            restrictUi();
        }
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onSubmit(WifiDialog2 wifiDialog2) {
        Iterator<WifiDialog2.WifiDialog2Listener> it = this.mWifiDialogListeners.iterator();
        while (it.hasNext()) {
            it.next().onSubmit(wifiDialog2);
        }
    }

    public void refreshPreferences() {
        lambda$onStart$0();
        displayPreferenceControllers();
    }

    void restrictUi() {
        clearWifiEntryCallback();
        if (!isUiRestrictedByOnlyAdmin()) {
            getEmptyTextView().setText(R.string.wifi_empty_list_user_restricted);
        }
        getPreferenceScreen().removeAll();
    }
}
