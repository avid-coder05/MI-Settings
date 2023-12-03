package com.android.settings.wifi;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.LinkifyUtils;
import com.android.settings.R;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.SettingsActivity;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.datausage.DataUsagePreference;
import com.android.settings.datausage.DataUsageUtils;
import com.android.settings.location.WifiScanningFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.ConnectedWifiEntryPreference;
import com.android.settings.wifi.WifiDialog2;
import com.android.settings.wifi.details2.WifiNetworkDetailsFragment2;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.SlaveWifiUtils;
import com.android.settingslib.wifi.WifiEntryPreference;
import com.android.settingslib.wifi.WifiSavedConfigUtils;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import miui.util.FeatureParser;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class WifiSettings extends RestrictedSettingsFragment implements WifiPickerTracker.WifiPickerTrackerCallback, WifiDialog2.WifiDialog2Listener, DialogInterface.OnDismissListener {
    static final int ADD_NETWORK_REQUEST = 2;
    static final int MENU_ID_DISCONNECT = 3;
    static final int MENU_ID_FORGET = 4;
    static final String PREF_KEY_DATA_USAGE = "wifi_data_usage";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.wifi_settings) { // from class: com.android.settings.wifi.WifiSettings.9
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (WifiSavedConfigUtils.getAllConfigsCount(context, (WifiManager) context.getSystemService(WifiManager.class)) == 0) {
                nonIndexableKeys.add("saved_networks");
            }
            if (!DataUsageUtils.hasWifiRadio(context)) {
                nonIndexableKeys.add(WifiSettings.PREF_KEY_DATA_USAGE);
            }
            return nonIndexableKeys;
        }
    };
    private ActionBar mActionBar;
    AddWifiNetworkPreference mAddWifiNetworkPreference;
    private boolean mClickedConnect;
    Preference mConfigureWifiSettingsPreference;
    protected WifiManager.ActionListener mConnectListener;
    private PreferenceCategory mConnectedWifiEntryPreferenceCategory;
    ConnectivityManager mConnectivityManager;
    DataUsagePreference mDataUsagePreference;
    private WifiDialog2 mDialog;
    private int mDialogMode;
    private WifiEntry mDialogWifiEntry;
    private String mDialogWifiEntryKey;
    private boolean mEnableNextOnConnection;
    private IntentFilter mFilter;
    protected WifiManager.ActionListener mForgetListener;
    private final Runnable mHideProgressBarRunnable;
    protected boolean mIsDppQrCodeFgShow;
    private boolean mIsRestricted;
    protected boolean mIsShareDialogShow;
    private boolean mIsWifiEntryListStale;
    private String mOpenSsid;
    private View mProgressHeader;
    private BroadcastReceiver mReceiver;
    protected WifiManager.ActionListener mSaveListener;
    Preference mSavedNetworksPreference;
    private WifiEntry mSelectedWifiEntry;
    protected SlaveWifiUtils mSlaveWifiUtils;
    private LinkablePreference mStatusMessagePreference;
    private final Runnable mUpdateWifiEntryPreferencesRunnable;
    protected MiuiWifiEnabler mWifiEnabler;
    private PreferenceCategory mWifiEntryPreferenceCategory;
    protected WifiManager mWifiManager;
    WifiPickerTracker mWifiPickerTracker;
    private HandlerThread mWorkerThread;

    /* loaded from: classes2.dex */
    private class WifiConnectActionListener implements WifiManager.ActionListener {
        private WifiConnectActionListener() {
        }

        public void onFailure(int i) {
            if (WifiSettings.this.isFinishingOrDestroyed()) {
                return;
            }
            Toast.makeText(WifiSettings.this.getContext(), R.string.wifi_failed_connect_message, 0).show();
        }

        public void onSuccess() {
            WifiSettings.this.mClickedConnect = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class WifiEntryConnectCallback implements WifiEntry.ConnectCallback {
        final WifiEntry mConnectWifiEntry;
        final boolean mEditIfNoConfig;
        final boolean mFullScreenEdit;

        WifiEntryConnectCallback(WifiEntry wifiEntry, boolean z, boolean z2) {
            this.mConnectWifiEntry = wifiEntry;
            this.mEditIfNoConfig = z;
            this.mFullScreenEdit = z2;
        }

        @Override // com.android.wifitrackerlib.WifiEntry.ConnectCallback
        public void onConnectResult(int i) {
            if (WifiSettings.this.isFinishingOrDestroyed()) {
                return;
            }
            if (i == 0) {
                WifiSettings.this.mClickedConnect = true;
            } else if (i != 1) {
                if (i == 2) {
                    Toast.makeText(WifiSettings.this.getContext(), R.string.wifi_failed_connect_message, 0).show();
                }
            } else if (this.mEditIfNoConfig) {
                if (this.mFullScreenEdit) {
                    WifiSettings.this.launchConfigNewNetworkFragment(this.mConnectWifiEntry);
                } else {
                    WifiSettings.this.showDialog(this.mConnectWifiEntry, 1);
                }
            }
        }
    }

    public WifiSettings() {
        super("no_config_wifi");
        this.mIsWifiEntryListStale = true;
        this.mUpdateWifiEntryPreferencesRunnable = new Runnable() { // from class: com.android.settings.wifi.WifiSettings$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                WifiSettings.this.lambda$new$0();
            }
        };
        this.mHideProgressBarRunnable = new Runnable() { // from class: com.android.settings.wifi.WifiSettings$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                WifiSettings.this.lambda$new$1();
            }
        };
    }

    private void addPreferences() {
        if (isFromMiuiSlaveWifiSettings()) {
            addPreferencesFromResource(R.xml.slave_wifi_settings);
        } else {
            addPreferencesFromResource(R.xml.wifi_settings);
        }
        this.mConnectedWifiEntryPreferenceCategory = (PreferenceCategory) findPreference("connected_access_point");
        this.mWifiEntryPreferenceCategory = (PreferenceCategory) findPreference("access_points");
        this.mConfigureWifiSettingsPreference = findPreference("configure_wifi_settings");
        this.mSavedNetworksPreference = findPreference("saved_networks");
        this.mAddWifiNetworkPreference = new AddWifiNetworkPreference(getPrefContext());
        this.mStatusMessagePreference = (LinkablePreference) findPreference("wifi_status_message");
        DataUsagePreference dataUsagePreference = (DataUsagePreference) findPreference(PREF_KEY_DATA_USAGE);
        this.mDataUsagePreference = dataUsagePreference;
        dataUsagePreference.setVisible(false);
        this.mStatusMessagePreference.setVisible(false);
    }

    private boolean canForgetNetwork() {
        return this.mSelectedWifiEntry.canForget() && !WifiUtils.isNetworkLockedDown(getActivity(), this.mSelectedWifiEntry.getWifiConfiguration());
    }

    private void forget(WifiEntry wifiEntry) {
        this.mMetricsFeatureProvider.action(getActivity(), 137, new Pair[0]);
        wifiEntry.forget(null);
    }

    private String getSavedNetworkSettingsSummaryText(int i, int i2) {
        if (i2 == 0) {
            return getResources().getQuantityString(R.plurals.wifi_saved_access_points_summary, i, Integer.valueOf(i));
        }
        if (i == 0) {
            return getResources().getQuantityString(R.plurals.wifi_saved_passpoint_access_points_summary, i2, Integer.valueOf(i2));
        }
        int i3 = i + i2;
        return getResources().getQuantityString(R.plurals.wifi_saved_all_access_points_summary, i3, Integer.valueOf(i3));
    }

    private void handleAddNetworkSubmitEvent(Intent intent) {
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("wifi_config_key");
        if (wifiConfiguration != null) {
            this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initActionBar() {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (getActivity() instanceof AppCompatActivity) {
            this.mActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        }
        ImageView imageView = new ImageView(activity);
        if (!isFromMiuiSlaveWifiSettings() ? this.mWifiManager.isWifiEnabled() : this.mSlaveWifiUtils.isSlaveWifiEnabled()) {
            imageView.setBackgroundResource(R.drawable.ic_union_disable);
            imageView.setImportantForAccessibility(2);
        } else {
            imageView.setBackgroundResource(R.drawable.ic_union);
            imageView.setContentDescription(getResources().getString(R.string.wifi_dpp_scan_qr_code));
            imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.WifiSettings.10
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    int i;
                    AccessPoint accessPoint;
                    WifiSettings wifiSettings = WifiSettings.this;
                    wifiSettings.mIsShareDialogShow = false;
                    wifiSettings.mIsDppQrCodeFgShow = false;
                    MiStatInterfaceUtils.trackEvent("wifi_qrCode_scanner");
                    OneTrackInterfaceUtils.track("wifi_qrCode_scanner", null);
                    int networkId = WifiSettings.this.mWifiManager.getConnectionInfo().getNetworkId();
                    List<WifiConfiguration> configuredNetworks = WifiSettings.this.mWifiManager.getConfiguredNetworks();
                    WifiConfiguration wifiConfiguration = new WifiConfiguration();
                    Iterator<WifiConfiguration> it = configuredNetworks.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        WifiConfiguration next = it.next();
                        if (next.networkId == networkId) {
                            wifiConfiguration = next;
                            break;
                        }
                    }
                    if (wifiConfiguration.networkId != -1) {
                        accessPoint = new AccessPoint(WifiSettings.this.getActivity(), wifiConfiguration);
                        i = accessPoint.getSecurity();
                    } else {
                        i = 0;
                        accessPoint = null;
                    }
                    if ((i != 2 && i != 5) || TextUtils.equals("mediatek", FeatureParser.getString("vendor")) || !WifiSettings.this.mWifiManager.isEasyConnectSupported() || WifiSettings.this.isFromMiuiSlaveWifiSettings()) {
                        activity.startActivityForResult(WifiDppUtils.getEnrolleeQrCodeScannerIntent(WifiSettings.this.getActivity(), null, WifiSettings.this.isFromMiuiSlaveWifiSettings()), 0);
                        activity.overridePendingTransition(0, 0);
                        return;
                    }
                    Intent miuiConfiguratorQrCodeScannerIntentOrNull = WifiDppUtils.getMiuiConfiguratorQrCodeScannerIntentOrNull(WifiSettings.this.getActivity(), WifiSettings.this.mWifiManager, accessPoint);
                    if (miuiConfiguratorQrCodeScannerIntentOrNull != null) {
                        activity.startActivityForResult(miuiConfiguratorQrCodeScannerIntentOrNull, 1);
                        activity.overridePendingTransition(0, 0);
                    }
                }
            });
        }
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.setEndView(imageView);
        }
    }

    private static boolean isDisabledByWrongPassword(WifiEntry wifiEntry) {
        WifiConfiguration.NetworkSelectionStatus networkSelectionStatus;
        WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
        return (wifiConfiguration == null || (networkSelectionStatus = wifiConfiguration.getNetworkSelectionStatus()) == null || networkSelectionStatus.getNetworkSelectionStatus() == 0 || 8 != networkSelectionStatus.getNetworkSelectionDisableReason()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isFromMiuiSlaveWifiSettings() {
        return "MiuiSlaveWifiSettings".equals(getClass().getSimpleName());
    }

    private static boolean isVerboseLoggingEnabled() {
        return BaseWifiTracker.isVerboseLoggingEnabled();
    }

    private boolean isWifiWakeupEnabled() {
        Context context = getContext();
        return this.mWifiManager.isAutoWakeupEnabled() && this.mWifiManager.isScanAlwaysAvailable() && Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) == 0 && !((PowerManager) context.getSystemService(PowerManager.class)).isPowerSaveMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        setProgressBarVisible(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onContextItemSelected$2() {
        launchWifiDppConfiguratorActivity(this.mSelectedWifiEntry);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onWifiEntriesChanged$3(WifiEntry wifiEntry) {
        return TextUtils.equals(this.mOpenSsid, wifiEntry.getSsid());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onWifiEntriesChanged$4(WifiEntry wifiEntry) {
        return (wifiEntry.getSecurity() == 0 || wifiEntry.getSecurity() == 4) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onWifiEntriesChanged$5(WifiEntry wifiEntry) {
        return !wifiEntry.isSaved() || isDisabledByWrongPassword(wifiEntry);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setOffMessage$9() {
        new SubSettingLauncher(getContext()).setDestination(WifiScanningFragment.class.getName()).setTitleRes(R.string.location_scanning_wifi_always_scanning_title).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateWifiEntryPreferences$6(WifiEntry wifiEntry, ConnectedWifiEntryPreference connectedWifiEntryPreference, Preference preference) {
        if (wifiEntry.canSignIn()) {
            wifiEntry.signIn(null);
            return true;
        }
        launchNetworkDetailsFragment(connectedWifiEntryPreference);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateWifiEntryPreferences$7(ConnectedWifiEntryPreference connectedWifiEntryPreference, ConnectedWifiEntryPreference connectedWifiEntryPreference2) {
        launchNetworkDetailsFragment(connectedWifiEntryPreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateWifiEntryPreferences$8(WifiEntry wifiEntry, WifiEntryPreference wifiEntryPreference) {
        openSubscriptionHelpPage(wifiEntry);
    }

    private void launchAddNetworkFragment() {
        new SubSettingLauncher(getContext()).setTitleRes(R.string.wifi_add_network).setDestination(AddNetworkFragment.class.getName()).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 2).launch();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchConfigNewNetworkFragment(WifiEntry wifiEntry) {
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", wifiEntry.getKey());
        new SubSettingLauncher(getContext()).setTitleText(wifiEntry.getTitle()).setDestination(ConfigureWifiEntryFragment.class.getName()).setArguments(bundle).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 3).launch();
    }

    private void launchNetworkDetailsFragment(LongPressWifiEntryPreference longPressWifiEntryPreference) {
        WifiEntry wifiEntry = longPressWifiEntryPreference.getWifiEntry();
        Context context = getContext();
        CharSequence title = FeatureFlagUtils.isEnabled(context, "settings_wifi_details_datausage_header") ? wifiEntry.getTitle() : context.getText(R.string.pref_title_network_details);
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", wifiEntry.getKey());
        new SubSettingLauncher(context).setTitleText(title).setDestination(WifiNetworkDetailsFragment2.class.getName()).setArguments(bundle).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    private void launchWifiDppConfiguratorActivity(WifiEntry wifiEntry) {
        Intent configuratorQrCodeGeneratorIntentOrNull = WifiDppUtils.getConfiguratorQrCodeGeneratorIntentOrNull(getContext(), this.mWifiManager, wifiEntry);
        if (configuratorQrCodeGeneratorIntentOrNull == null) {
            Log.e("WifiSettings", "Launch Wi-Fi DPP QR code generator with a wrong Wi-Fi network!");
            return;
        }
        this.mMetricsFeatureProvider.action(0, 1710, 1595, null, Integer.MIN_VALUE);
        startActivity(configuratorQrCodeGeneratorIntentOrNull);
    }

    private void onAddNetworkPressed() {
        launchAddNetworkFragment();
    }

    private void removeConnectedWifiEntryPreference() {
        this.mConnectedWifiEntryPreferenceCategory.removeAll();
        this.mConnectedWifiEntryPreferenceCategory.setVisible(false);
    }

    private void removeWifiEntryPreference() {
        this.mWifiEntryPreferenceCategory.removeAll();
        this.mWifiEntryPreferenceCategory.setVisible(false);
    }

    private void restrictUi() {
        if (!isUiRestrictedByOnlyAdmin()) {
            getEmptyTextView().setText(R.string.wifi_empty_list_user_restricted);
        }
        getPreferenceScreen().removeAll();
    }

    private void setOffMessage() {
        this.mStatusMessagePreference.setText(getText(R.string.wifi_empty_list_wifi_off), this.mWifiManager.isScanAlwaysAvailable() ? getText(R.string.wifi_scan_notify_text) : getText(R.string.wifi_scan_notify_text_scanning_off), new LinkifyUtils.OnClickListener() { // from class: com.android.settings.wifi.WifiSettings$$ExternalSyntheticLambda1
            @Override // com.android.settings.LinkifyUtils.OnClickListener
            public final void onClick() {
                WifiSettings.this.lambda$setOffMessage$9();
            }
        });
        removeConnectedWifiEntryPreference();
        removeWifiEntryPreference();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDialog(WifiEntry wifiEntry, int i) {
        if (WifiUtils.isNetworkLockedDown(getActivity(), wifiEntry.getWifiConfiguration()) && wifiEntry.getConnectedState() == 2) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), RestrictedLockUtilsInternal.getDeviceOwner(getActivity()));
            return;
        }
        if (this.mDialog != null) {
            removeDialog(1);
            this.mDialog = null;
        }
        this.mDialogWifiEntry = wifiEntry;
        this.mDialogWifiEntryKey = wifiEntry.getKey();
        this.mDialogMode = i;
        showDialog(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateWifiEntryPreferences  reason: merged with bridge method [inline-methods] */
    public void lambda$new$0() {
        if (this.mWifiPickerTracker.getWifiState() != 3) {
            return;
        }
        this.mStatusMessagePreference.setVisible(false);
        this.mWifiEntryPreferenceCategory.setVisible(true);
        final WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
        this.mConnectedWifiEntryPreferenceCategory.setVisible(connectedWifiEntry != null);
        if (connectedWifiEntry != null) {
            LongPressWifiEntryPreference longPressWifiEntryPreference = (LongPressWifiEntryPreference) this.mConnectedWifiEntryPreferenceCategory.findPreference(connectedWifiEntry.getKey());
            if (longPressWifiEntryPreference == null || longPressWifiEntryPreference.getWifiEntry() != connectedWifiEntry) {
                this.mConnectedWifiEntryPreferenceCategory.removeAll();
                final ConnectedWifiEntryPreference connectedWifiEntryPreference = new ConnectedWifiEntryPreference(getPrefContext(), connectedWifiEntry, this);
                connectedWifiEntryPreference.setKey(connectedWifiEntry.getKey());
                connectedWifiEntryPreference.refresh();
                this.mConnectedWifiEntryPreferenceCategory.addPreference(connectedWifiEntryPreference);
                connectedWifiEntryPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.wifi.WifiSettings$$ExternalSyntheticLambda0
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        boolean lambda$updateWifiEntryPreferences$6;
                        lambda$updateWifiEntryPreferences$6 = WifiSettings.this.lambda$updateWifiEntryPreferences$6(connectedWifiEntry, connectedWifiEntryPreference, preference);
                        return lambda$updateWifiEntryPreferences$6;
                    }
                });
                connectedWifiEntryPreference.setOnGearClickListener(new ConnectedWifiEntryPreference.OnGearClickListener() { // from class: com.android.settings.wifi.WifiSettings$$ExternalSyntheticLambda2
                    @Override // com.android.settings.wifi.ConnectedWifiEntryPreference.OnGearClickListener
                    public final void onGearClick(ConnectedWifiEntryPreference connectedWifiEntryPreference2) {
                        WifiSettings.this.lambda$updateWifiEntryPreferences$7(connectedWifiEntryPreference, connectedWifiEntryPreference2);
                    }
                });
                if (this.mClickedConnect) {
                    this.mClickedConnect = false;
                    scrollToPreference(this.mConnectedWifiEntryPreferenceCategory);
                }
            }
        } else {
            this.mConnectedWifiEntryPreferenceCategory.removeAll();
        }
        cacheRemoveAllPrefs(this.mWifiEntryPreferenceCategory);
        boolean z = false;
        int i = 0;
        for (final WifiEntry wifiEntry : this.mWifiPickerTracker.getWifiEntries()) {
            String key = wifiEntry.getKey();
            LongPressWifiEntryPreference longPressWifiEntryPreference2 = (LongPressWifiEntryPreference) getCachedPreference(key);
            if (longPressWifiEntryPreference2 != null) {
                if (longPressWifiEntryPreference2.getWifiEntry() == wifiEntry) {
                    longPressWifiEntryPreference2.setOrder(i);
                    i++;
                    z = true;
                } else {
                    removePreference(key);
                }
            }
            LongPressWifiEntryPreference createLongPressWifiEntryPreference = createLongPressWifiEntryPreference(wifiEntry);
            createLongPressWifiEntryPreference.setKey(wifiEntry.getKey());
            int i2 = i + 1;
            createLongPressWifiEntryPreference.setOrder(i);
            createLongPressWifiEntryPreference.refresh();
            if (wifiEntry.getHelpUriString() != null) {
                createLongPressWifiEntryPreference.setOnButtonClickListener(new WifiEntryPreference.OnButtonClickListener() { // from class: com.android.settings.wifi.WifiSettings$$ExternalSyntheticLambda3
                    @Override // com.android.settingslib.wifi.WifiEntryPreference.OnButtonClickListener
                    public final void onButtonClick(WifiEntryPreference wifiEntryPreference) {
                        WifiSettings.this.lambda$updateWifiEntryPreferences$8(wifiEntry, wifiEntryPreference);
                    }
                });
            }
            this.mWifiEntryPreferenceCategory.addPreference(createLongPressWifiEntryPreference);
            z = true;
            i = i2;
        }
        removeCachedPrefs(this.mWifiEntryPreferenceCategory);
        if (z) {
            getView().postDelayed(this.mHideProgressBarRunnable, 1700L);
        } else {
            setProgressBarVisible(true);
            Preference preference = new Preference(getPrefContext());
            preference.setSelectable(false);
            preference.setSummary(R.string.wifi_empty_list_wifi_on);
            preference.setOrder(i);
            preference.setKey("wifi_empty_list");
            this.mWifiEntryPreferenceCategory.addPreference(preference);
            i++;
        }
        this.mAddWifiNetworkPreference.setOrder(i);
        this.mWifiEntryPreferenceCategory.addPreference(this.mAddWifiNetworkPreference);
        setAdditionalSettingsSummaries();
    }

    private void updateWifiEntryPreferencesDelayed() {
        if (getActivity() == null || this.mIsRestricted || this.mWifiPickerTracker.getWifiState() != 3) {
            return;
        }
        View view = getView();
        Handler handler = view.getHandler();
        if (handler == null || !handler.hasCallbacks(this.mUpdateWifiEntryPreferencesRunnable)) {
            setProgressBarVisible(true);
            view.postDelayed(this.mUpdateWifiEntryPreferencesRunnable, 300L);
        }
    }

    protected void addMessagePreference(int i) {
        this.mStatusMessagePreference.setTitle(i);
    }

    protected void changeNextButtonState(boolean z) {
        if (this.mEnableNextOnConnection && hasNextButton()) {
            getNextButton().setEnabled(z);
        }
    }

    void connect(WifiEntry wifiEntry, boolean z, boolean z2) {
        this.mMetricsFeatureProvider.action(getActivity(), 135, wifiEntry.isSaved());
        wifiEntry.connect(new WifiEntryConnectCallback(wifiEntry, z, z2));
    }

    LongPressWifiEntryPreference createLongPressWifiEntryPreference(WifiEntry wifiEntry) {
        return new LongPressWifiEntryPreference(getPrefContext(), wifiEntry, this);
    }

    Intent getHelpIntent(Context context, String str) {
        return HelpUtils.getHelpIntent(context, str, context.getClass().getName());
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_wifi;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 103;
    }

    void handleAddNetworkRequest(int i, Intent intent) {
        if (i == -1) {
            handleAddNetworkSubmitEvent(intent);
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Context context = getContext();
        HandlerThread handlerThread = new HandlerThread("WifiSettings{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mWifiPickerTracker = FeatureFactory.getFactory(context).getWifiTrackerLibProvider().createWifiPickerTracker(getSettingsLifecycle(), context, new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.WifiSettings.2
            public long millis() {
                return SystemClock.elapsedRealtime();
            }
        }, 15000L, 10000L, this);
        if (getActivity() != null) {
            this.mWifiManager = (WifiManager) getActivity().getSystemService(WifiManager.class);
        }
        this.mConnectListener = new WifiConnectListener(getActivity());
        this.mSaveListener = new WifiManager.ActionListener() { // from class: com.android.settings.wifi.WifiSettings.3
            public void onFailure(int i) {
                FragmentActivity activity = WifiSettings.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, R.string.wifi_failed_save_message, 0).show();
                }
            }

            public void onSuccess() {
            }
        };
        this.mForgetListener = new WifiManager.ActionListener() { // from class: com.android.settings.wifi.WifiSettings.4
            public void onFailure(int i) {
                FragmentActivity activity = WifiSettings.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, R.string.wifi_failed_forget_message, 0).show();
                }
            }

            public void onSuccess() {
            }
        };
        registerForContextMenu(getListView());
        setHasOptionsMenu(true);
        if (bundle != null) {
            this.mDialogMode = bundle.getInt("dialog_mode");
            this.mDialogWifiEntryKey = bundle.getString("wifi_ap_key");
        }
        Intent intent = getActivity().getIntent();
        this.mEnableNextOnConnection = intent.getBooleanExtra("wifi_enable_next_on_connect", false);
        if (intent.hasExtra("wifi_start_connect_ssid")) {
            this.mOpenSsid = intent.getStringExtra("wifi_start_connect_ssid");
        }
        lambda$onInternetTypeChanged$4();
    }

    @Override // com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        WifiConfiguration wifiConfiguration;
        WifiDialog2 wifiDialog2;
        super.onActivityResult(i, i2, intent);
        if (i == 2) {
            handleAddNetworkRequest(i2, intent);
        } else if (i == 0) {
            if (i2 != -1 || (wifiDialog2 = this.mDialog) == null) {
                return;
            }
            wifiDialog2.dismiss();
        } else if (i == 3) {
            if (i2 != -1 || (wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("network_config_key")) == null) {
                return;
            }
            this.mWifiManager.connect(wifiConfiguration, new WifiConnectActionListener());
        } else if (i == 4) {
        } else {
            boolean z = this.mIsRestricted;
            boolean isUiRestricted = isUiRestricted();
            this.mIsRestricted = isUiRestricted;
            if (z && !isUiRestricted && getPreferenceScreen().getPreferenceCount() == 0) {
                addPreferences();
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onContextItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2) {
            connect(this.mSelectedWifiEntry, true, false);
            return true;
        } else if (itemId == 3) {
            this.mSelectedWifiEntry.disconnect(null);
            return true;
        } else if (itemId == 4) {
            forget(this.mSelectedWifiEntry);
            return true;
        } else if (itemId == 5) {
            showDialog(this.mSelectedWifiEntry, 2);
            return true;
        } else if (itemId != 6) {
            return super.onContextItemSelected(menuItem);
        } else {
            WifiDppUtils.showLockScreen(getContext(), new Runnable() { // from class: com.android.settings.wifi.WifiSettings$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    WifiSettings.this.lambda$onContextItemSelected$2();
                }
            });
            return true;
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (FeatureFlagUtils.isEnabled(getContext(), "settings_provider_model")) {
            Intent intent = new Intent("android.settings.NETWORK_PROVIDER_SETTINGS");
            intent.addFlags(268468224);
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) {
                intent.putExtras(extras);
            }
            getContext().startActivity(intent);
            finish();
            return;
        }
        setAnimationAllowed(false);
        addPreferences();
        this.mIsRestricted = isUiRestricted();
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.WifiSettings.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent2) {
                WifiSettings.this.initActionBar();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        this.mFilter = intentFilter;
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        this.mFilter.addAction("android.net.wifi.WIFI_SLAVE_STATE_CHANGED");
        this.mSlaveWifiUtils = new SlaveWifiUtils(getActivity());
    }

    @Override // androidx.fragment.app.Fragment, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        Preference preference = (Preference) view.getTag();
        if (preference instanceof LongPressWifiEntryPreference) {
            WifiEntry wifiEntry = ((LongPressWifiEntryPreference) preference).getWifiEntry();
            this.mSelectedWifiEntry = wifiEntry;
            contextMenu.setHeaderTitle(wifiEntry.getTitle());
            if (this.mSelectedWifiEntry.canConnect()) {
                contextMenu.add(0, 2, 0, R.string.wifi_connect);
            }
            if (this.mSelectedWifiEntry.canDisconnect()) {
                contextMenu.add(0, 6, 0, R.string.share);
                contextMenu.add(0, 3, 1, R.string.wifi_disconnect_button_text);
            }
            if (canForgetNetwork()) {
                contextMenu.add(0, 4, 0, R.string.forget);
            }
            if (WifiUtils.isNetworkLockedDown(getActivity(), this.mSelectedWifiEntry.getWifiConfiguration()) || !this.mSelectedWifiEntry.isSaved() || this.mSelectedWifiEntry.getConnectedState() == 2) {
                return;
            }
            contextMenu.add(0, 5, 0, R.string.wifi_modify);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 1) {
            return super.onCreateDialog(i);
        }
        WifiDialog2 createModal = WifiDialog2.createModal(getActivity(), this, this.mDialogWifiEntry, this.mDialogMode);
        this.mDialog = createModal;
        return createModal;
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.setEndView(null);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onDialogShowing() {
        super.onDialogShowing();
        setOnDismissListener(this);
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        this.mDialog = null;
        this.mDialogWifiEntry = null;
        this.mDialogWifiEntryKey = null;
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onForget(WifiDialog2 wifiDialog2) {
        forget(wifiDialog2.getWifiEntry());
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedNetworksChanged() {
        if (isFinishingOrDestroyed()) {
            return;
        }
        setAdditionalSettingsSummaries();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedSubscriptionsChanged() {
        if (isFinishingOrDestroyed()) {
            return;
        }
        setAdditionalSettingsSummaries();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        MiuiWifiEnabler miuiWifiEnabler = this.mWifiEnabler;
        if (miuiWifiEnabler != null) {
            miuiWifiEnabler.pause();
        }
        if (getActivity() != null) {
            getActivity().unregisterReceiver(this.mReceiver);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getFragment() != null) {
            preference.setOnPreferenceClickListener(null);
            return super.onPreferenceTreeClick(preference);
        }
        if (preference instanceof LongPressWifiEntryPreference) {
            WifiEntry wifiEntry = ((LongPressWifiEntryPreference) preference).getWifiEntry();
            if (wifiEntry.shouldEditBeforeConnect()) {
                launchConfigNewNetworkFragment(wifiEntry);
                return true;
            }
            connect(wifiEntry, true, true);
        } else if (preference != this.mAddWifiNetworkPreference) {
            return super.onPreferenceTreeClick(preference);
        } else {
            onAddNetworkPressed();
        }
        return true;
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        FragmentActivity activity = getActivity();
        super.onResume();
        boolean z = this.mIsRestricted;
        boolean isUiRestricted = isUiRestricted();
        this.mIsRestricted = isUiRestricted;
        if (!z && isUiRestricted) {
            restrictUi();
        }
        MiuiWifiEnabler miuiWifiEnabler = this.mWifiEnabler;
        if (miuiWifiEnabler != null) {
            miuiWifiEnabler.resume(activity);
        }
        changeNextButtonState(this.mWifiPickerTracker.getConnectedWifiEntry() != null);
        initActionBar();
        if (activity != null) {
            activity.registerReceiver(this.mReceiver, this.mFilter);
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mDialog != null) {
            bundle.putInt("dialog_mode", this.mDialogMode);
            bundle.putString("wifi_ap_key", this.mDialogWifiEntryKey);
        }
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onScan(WifiDialog2 wifiDialog2, String str) {
        startActivityForResult(WifiDppUtils.getEnrolleeQrCodeScannerIntent(str), 0);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        getView().removeCallbacks(this.mUpdateWifiEntryPreferencesRunnable);
        getView().removeCallbacks(this.mHideProgressBarRunnable);
        this.mIsWifiEntryListStale = true;
        super.onStop();
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onSubmit(WifiDialog2 wifiDialog2) {
        int mode = wifiDialog2.getMode();
        WifiConfiguration config = wifiDialog2.getController().getConfig();
        WifiEntry wifiEntry = wifiDialog2.getWifiEntry();
        WifiConfigController2 controller = wifiDialog2.getController();
        if (mode == 2) {
            if (config == null) {
                Toast.makeText(getContext(), R.string.wifi_failed_save_message, 0).show();
            } else if (controller.checkWapiParam()) {
                this.mWifiManager.save(config, this.mSaveListener);
            } else {
                controller.getCurSecurity();
            }
        } else if (mode == 1 || (mode == 0 && wifiEntry.canConnect())) {
            if (config == null) {
                connect(wifiEntry, false, false);
            } else if (controller.checkWapiParam()) {
                this.mWifiManager.connect(config, new WifiConnectActionListener());
            } else {
                controller.getCurSecurity();
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            this.mProgressHeader = setPinnedHeaderView(R.layout.progress_header).findViewById(R.id.progress_bar_animation);
            setProgressBarVisible(false);
        }
        if (activity instanceof SettingsActivity) {
            ((SettingsActivity) activity).getSwitchBar().setTitle(getContext().getString(R.string.wifi_settings_primary_switch_title));
        }
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        if (this.mIsWifiEntryListStale) {
            this.mIsWifiEntryListStale = false;
            lambda$new$0();
        } else {
            updateWifiEntryPreferencesDelayed();
        }
        changeNextButtonState(this.mWifiPickerTracker.getConnectedWifiEntry() != null);
        if (this.mOpenSsid != null) {
            Optional<WifiEntry> findFirst = this.mWifiPickerTracker.getWifiEntries().stream().filter(new Predicate() { // from class: com.android.settings.wifi.WifiSettings$$ExternalSyntheticLambda7
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onWifiEntriesChanged$3;
                    lambda$onWifiEntriesChanged$3 = WifiSettings.this.lambda$onWifiEntriesChanged$3((WifiEntry) obj);
                    return lambda$onWifiEntriesChanged$3;
                }
            }).filter(new Predicate() { // from class: com.android.settings.wifi.WifiSettings$$ExternalSyntheticLambda8
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onWifiEntriesChanged$4;
                    lambda$onWifiEntriesChanged$4 = WifiSettings.lambda$onWifiEntriesChanged$4((WifiEntry) obj);
                    return lambda$onWifiEntriesChanged$4;
                }
            }).filter(new Predicate() { // from class: com.android.settings.wifi.WifiSettings$$ExternalSyntheticLambda9
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onWifiEntriesChanged$5;
                    lambda$onWifiEntriesChanged$5 = WifiSettings.lambda$onWifiEntriesChanged$5((WifiEntry) obj);
                    return lambda$onWifiEntriesChanged$5;
                }
            }).findFirst();
            if (findFirst.isPresent()) {
                this.mOpenSsid = null;
                launchConfigNewNetworkFragment(findFirst.get());
            }
        }
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    /* renamed from: onWifiStateChanged */
    public void lambda$onInternetTypeChanged$4() {
        if (this.mIsRestricted) {
            return;
        }
        int wifiState = this.mWifiPickerTracker.getWifiState();
        if (isVerboseLoggingEnabled()) {
            Log.i("WifiSettings", "onWifiStateChanged called with wifi state: " + wifiState);
        }
        if (wifiState == 0) {
            removeConnectedWifiEntryPreference();
            removeWifiEntryPreference();
            addMessagePreference(R.string.wifi_stopping);
        } else if (wifiState == 1) {
            setOffMessage();
            setProgressBarVisible(false);
            this.mClickedConnect = false;
        } else if (wifiState != 2) {
            if (wifiState != 3) {
                return;
            }
            lambda$new$0();
        } else {
            removeConnectedWifiEntryPreference();
            removeWifiEntryPreference();
            addMessagePreference(R.string.wifi_starting);
            setProgressBarVisible(true);
        }
    }

    void openSubscriptionHelpPage(WifiEntry wifiEntry) {
        Intent helpIntent = getHelpIntent(getContext(), wifiEntry.getHelpUriString());
        if (helpIntent != null) {
            try {
                startActivityForResult(helpIntent, 4);
            } catch (ActivityNotFoundException unused) {
                Log.e("WifiSettings", "Activity was not found for intent, " + helpIntent.toString());
            }
        }
    }

    void setAdditionalSettingsSummaries() {
        Preference preference = this.mConfigureWifiSettingsPreference;
        if (preference == null) {
            return;
        }
        preference.setSummary(getString(isWifiWakeupEnabled() ? R.string.wifi_configure_settings_preference_summary_wakeup_on : R.string.wifi_configure_settings_preference_summary_wakeup_off));
        int numSavedNetworks = this.mWifiPickerTracker.getNumSavedNetworks();
        int numSavedSubscriptions = this.mWifiPickerTracker.getNumSavedSubscriptions();
        if (numSavedNetworks + numSavedSubscriptions <= 0) {
            setVisible(this.mSavedNetworksPreference, false);
            return;
        }
        setVisible(this.mSavedNetworksPreference, true);
        this.mSavedNetworksPreference.setSummary(getSavedNetworkSettingsSummaryText(numSavedNetworks, numSavedSubscriptions));
    }

    protected void setProgressBarVisible(boolean z) {
        View view = this.mProgressHeader;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
    }
}
