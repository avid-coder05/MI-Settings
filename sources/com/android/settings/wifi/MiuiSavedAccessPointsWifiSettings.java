package com.android.settings.wifi;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkScoreManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.wifi.SavedAccessPointPreference;
import com.android.settings.wifi.operatorutils.Operator;
import com.android.settings.wifi.operatorutils.OperatorFactory;
import com.android.wifitrackerlib.SavedNetworkTracker;
import com.android.wifitrackerlib.WifiEntry;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;
import miuix.view.EditActionMode;

/* loaded from: classes2.dex */
public class MiuiSavedAccessPointsWifiSettings extends SettingsPreferenceFragment implements SavedAccessPointPreference.OnLongClickListener, SavedNetworkTracker.SavedNetworkTrackerCallback {
    private boolean isInActinoMode;
    private List<SavedAccessPointPreference> mAccessPointPreferenceList;
    private ActionMode mEditActionMode;
    private SavedNetworkTracker mSavedNetworkTracker;
    private WifiManager mWifiManager;
    private HandlerThread mWorkerThread;
    private boolean mIsDismiss = true;
    private int mIsSelectConfigsNum = 0;
    List<WifiEntry> mWifiEntries = new ArrayList();

    /* loaded from: classes2.dex */
    private class ActionModeCallBack implements ActionMode.Callback {
        private ActionModeCallBack() {
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == 16908313) {
                actionMode.finish();
            } else if (menuItem.getItemId() == 16908314) {
                if (MiuiSavedAccessPointsWifiSettings.this.isAllChecked()) {
                    ((EditActionMode) actionMode).setButton(16908314, null, R.drawable.action_mode_title_button_select_all);
                    MiuiSavedAccessPointsWifiSettings.this.setAllBtnsChecked(false);
                } else {
                    ((EditActionMode) actionMode).setButton(16908314, null, R.drawable.action_mode_title_button_deselect_all);
                    MiuiSavedAccessPointsWifiSettings.this.setAllBtnsChecked(true);
                }
                MiuiSavedAccessPointsWifiSettings.this.updateActionModeTitle();
            } else if (menuItem.getItemId() == R.id.delete) {
                AlertDialog create = new AlertDialog.Builder(MiuiSavedAccessPointsWifiSettings.this.getActivity(), R.style.AlertDialog_Theme_DayNight).setTitle(R.string.delete_saved_network).setMessage(MiuiSavedAccessPointsWifiSettings.this.getString(R.string.batch_delete_saved_networks)).setPositiveButton(R.string.wifi_menu_forget, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.MiuiSavedAccessPointsWifiSettings.ActionModeCallBack.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MiuiSavedAccessPointsWifiSettings.this.deleteSavedConfigs();
                    }
                }).setNegativeButton(R.string.wifi_setup_cancel, (DialogInterface.OnClickListener) null).create();
                create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.wifi.MiuiSavedAccessPointsWifiSettings.ActionModeCallBack.2
                    @Override // android.content.DialogInterface.OnDismissListener
                    public void onDismiss(DialogInterface dialogInterface) {
                        actionMode.finish();
                    }
                });
                create.show();
                MiuiSavedAccessPointsWifiSettings.this.updateActionModeTitle();
            }
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MiuiSavedAccessPointsWifiSettings.this.mEditActionMode = actionMode;
            MiuiSavedAccessPointsWifiSettings.this.setInActionMode(true);
            MiuiSavedAccessPointsWifiSettings.this.getMenuInflater().inflate(R.menu.saved_network_options, menu);
            MiuiSavedAccessPointsWifiSettings.this.mEditActionMode.setTitle(MiuiSavedAccessPointsWifiSettings.this.getResources().getQuantityString(R.plurals.saved_network_checked_num, 1, 1));
            EditActionMode editActionMode = (EditActionMode) actionMode;
            editActionMode.setButton(16908313, null, R.drawable.action_mode_title_button_cancel);
            editActionMode.setButton(16908314, null, MiuiSavedAccessPointsWifiSettings.this.mAccessPointPreferenceList.size() == 1 ? R.drawable.action_mode_title_button_deselect_all : R.drawable.action_mode_title_button_select_all);
            MiuiUtils.setNavigationBackground(MiuiSavedAccessPointsWifiSettings.this.getActivity(), false);
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
            MiuiSavedAccessPointsWifiSettings.this.mEditActionMode = null;
            MiuiSavedAccessPointsWifiSettings.this.setInActionMode(false);
            MiuiSavedAccessPointsWifiSettings.this.setAllBtnsChecked(false);
            MiuiUtils.setNavigationBackground(MiuiSavedAccessPointsWifiSettings.this.getActivity(), true);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }
    }

    static /* synthetic */ int access$306(MiuiSavedAccessPointsWifiSettings miuiSavedAccessPointsWifiSettings) {
        int i = miuiSavedAccessPointsWifiSettings.mIsSelectConfigsNum - 1;
        miuiSavedAccessPointsWifiSettings.mIsSelectConfigsNum = i;
        return i;
    }

    private boolean canModifyNetWork(WifiConfiguration wifiConfiguration) {
        return (wifiConfiguration == null || WifiUtils.isNetworkLockedDown(getActivity(), wifiConfiguration)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteSavedConfig(final WifiEntry wifiEntry) {
        AutoConnectUtils autoConnectUtils = AutoConnectUtils.getInstance(getActivity());
        WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
        this.mIsSelectConfigsNum = getCheckedNum();
        if (wifiConfiguration == null) {
            if (this.mWifiManager.isWifiEnabled() || !wifiEntry.getSecurityString(false).equals("WPA/WPA2/WPA3-Personal")) {
                Log.e("MiuiSavedAccessPointsWifiSettings", "Deleted config failed, wifiConfig is null.");
                return;
            }
            Iterator it = ((List) this.mWifiManager.getConfiguredNetworks().stream().filter(new Predicate() { // from class: com.android.settings.wifi.MiuiSavedAccessPointsWifiSettings$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$deleteSavedConfig$1;
                    lambda$deleteSavedConfig$1 = MiuiSavedAccessPointsWifiSettings.lambda$deleteSavedConfig$1(WifiEntry.this, (WifiConfiguration) obj);
                    return lambda$deleteSavedConfig$1;
                }
            }).collect(Collectors.toList())).iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                WifiConfiguration wifiConfiguration2 = (WifiConfiguration) it.next();
                if (wifiConfiguration2.isSecurityType(4)) {
                    wifiConfiguration = new WifiConfiguration(wifiConfiguration2);
                    break;
                }
            }
            if (wifiConfiguration == null) {
                return;
            }
        }
        if (!canModifyNetWork(wifiConfiguration)) {
            Log.e("MiuiSavedAccessPointsWifiSettings", "Deleted config failed, wifiConfig is lockdown.");
            return;
        }
        if (!autoConnectUtils.isAutoConnect(wifiConfiguration.SSID)) {
            autoConnectUtils.enableAutoConnect(getActivity(), wifiConfiguration.SSID, true);
        }
        autoConnectUtils.removeNoSecretWifi(getActivity(), wifiConfiguration.SSID);
        WifiConfigurationManager.getInstance(getActivity()).deleteWifiConfiguration(wifiConfiguration);
        this.mWifiManager.forget(wifiConfiguration.networkId, new WifiManager.ActionListener() { // from class: com.android.settings.wifi.MiuiSavedAccessPointsWifiSettings.4
            public void onFailure(int i) {
                if (MiuiSavedAccessPointsWifiSettings.access$306(MiuiSavedAccessPointsWifiSettings.this) == 0) {
                    MiuiSavedAccessPointsWifiSettings.this.updateUI();
                }
                Log.w("MiuiSavedAccessPointsWifiSettings", "deleted config failed: " + wifiEntry.getSsid() + " reason: " + i);
            }

            public void onSuccess() {
                MiuiSavedAccessPointsWifiSettings.this.isInActinoMode = false;
                if (MiuiSavedAccessPointsWifiSettings.access$306(MiuiSavedAccessPointsWifiSettings.this) <= 0) {
                    MiuiSavedAccessPointsWifiSettings.this.updateUI();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteSavedConfigs() {
        WifiEntry wifiEntry;
        List<SavedAccessPointPreference> list = this.mAccessPointPreferenceList;
        if (list != null) {
            for (SavedAccessPointPreference savedAccessPointPreference : list) {
                if (savedAccessPointPreference.isChecked() && (wifiEntry = savedAccessPointPreference.getWifiEntry()) != null && !isOperatorForbidDelSsid(wifiEntry.getSsid())) {
                    deleteSavedConfig(wifiEntry);
                }
            }
        }
    }

    private int getCheckedNum() {
        List<SavedAccessPointPreference> list = this.mAccessPointPreferenceList;
        int i = 0;
        if (list != null) {
            Iterator<SavedAccessPointPreference> it = list.iterator();
            while (it.hasNext()) {
                if (it.next().isChecked()) {
                    i++;
                }
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAllChecked() {
        List<SavedAccessPointPreference> list = this.mAccessPointPreferenceList;
        if (list != null) {
            Iterator<SavedAccessPointPreference> it = list.iterator();
            while (it.hasNext()) {
                if (!it.next().isChecked()) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    private boolean isAllUnChecked() {
        List<SavedAccessPointPreference> list = this.mAccessPointPreferenceList;
        if (list != null) {
            Iterator<SavedAccessPointPreference> it = list.iterator();
            while (it.hasNext()) {
                if (it.next().isChecked()) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    private boolean isOperatorForbidDelSsid(String str) {
        Operator operatorFactory = OperatorFactory.getInstance(getActivity());
        if (operatorFactory != null) {
            return operatorFactory.isForbidDelSsid(str);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$deleteSavedConfig$1(WifiEntry wifiEntry, WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.SSID.equals("\"" + wifiEntry.getSsid() + "\"");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDeleteDialog$0(DialogInterface dialogInterface) {
        this.mIsDismiss = true;
    }

    private ArrayList<SavedAccessPointPreference> resortAccessPoint(Collection<WifiEntry> collection) {
        ArrayList<SavedAccessPointPreference> arrayList = new ArrayList<>();
        if (getActivity() != null) {
            for (WifiEntry wifiEntry : collection) {
                SavedAccessPointPreference savedAccessPointPreference = new SavedAccessPointPreference(wifiEntry, getPrefContext());
                savedAccessPointPreference.setTitle(wifiEntry.getSsid());
                arrayList.add(savedAccessPointPreference);
            }
            Collections.sort(arrayList, new Comparator<SavedAccessPointPreference>() { // from class: com.android.settings.wifi.MiuiSavedAccessPointsWifiSettings.2
                @Override // java.util.Comparator
                public int compare(SavedAccessPointPreference savedAccessPointPreference2, SavedAccessPointPreference savedAccessPointPreference3) {
                    if (savedAccessPointPreference2 instanceof SavedAccessPointPreference) {
                        if (savedAccessPointPreference3 instanceof SavedAccessPointPreference) {
                            String ssid = savedAccessPointPreference2.getWifiEntry().getSsid();
                            String ssid2 = savedAccessPointPreference3.getWifiEntry().getSsid();
                            int compareToIgnoreCase = ssid.compareToIgnoreCase(ssid2);
                            return compareToIgnoreCase == 0 ? ssid.compareTo(ssid2) : compareToIgnoreCase;
                        }
                        return -1;
                    }
                    return 1;
                }
            });
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAllBtnsChecked(boolean z) {
        List<SavedAccessPointPreference> list = this.mAccessPointPreferenceList;
        if (list != null) {
            Iterator<SavedAccessPointPreference> it = list.iterator();
            while (it.hasNext()) {
                it.next().setBtnChecked(z);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setInActionMode(boolean z) {
        this.isInActinoMode = z;
        List<SavedAccessPointPreference> list = this.mAccessPointPreferenceList;
        if (list != null) {
            Iterator<SavedAccessPointPreference> it = list.iterator();
            while (it.hasNext()) {
                it.next().setActionMode(z);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateActionModeTitle() {
        this.mEditActionMode.setTitle(getResources().getQuantityString(R.plurals.saved_network_checked_num, getCheckedNum(), Integer.valueOf(getCheckedNum())));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUI() {
        if (getActivity() != null) {
            getPreferenceScreen().removeAll();
            ArrayList<SavedAccessPointPreference> resortAccessPoint = resortAccessPoint(this.mWifiEntries);
            this.mAccessPointPreferenceList = resortAccessPoint;
            for (SavedAccessPointPreference savedAccessPointPreference : resortAccessPoint) {
                savedAccessPointPreference.setLongClickListener(this);
                getPreferenceScreen().addPreference(savedAccessPointPreference);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiSavedAccessPointsWifiSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        updateUI();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.saved_wifi);
        if (!SettingsFeatures.isSplitTablet(getContext())) {
            getActivity().setRequestedOrientation(1);
        }
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        HandlerThread handlerThread = new HandlerThread("MiuiSavedAccessPointsWifiSettings{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mSavedNetworkTracker = new SavedNetworkTracker(getSettingsLifecycle(), getActivity(), this.mWifiManager, (ConnectivityManager) getActivity().getSystemService(ConnectivityManager.class), (NetworkScoreManager) getActivity().getSystemService(NetworkScoreManager.class), new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.MiuiSavedAccessPointsWifiSettings.1
            public long millis() {
                return SystemClock.elapsedRealtime();
            }
        }, 15000L, 10000L, this);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mWorkerThread.quit();
    }

    @Override // com.android.settings.wifi.SavedAccessPointPreference.OnLongClickListener
    public boolean onDeteleBtnClick(Preference preference) {
        if (this.isInActinoMode) {
            return false;
        }
        if (preference instanceof SavedAccessPointPreference) {
            showDeleteDialog(((SavedAccessPointPreference) preference).getWifiEntry());
            return true;
        }
        return true;
    }

    @Override // com.android.settings.wifi.SavedAccessPointPreference.OnLongClickListener
    public boolean onPreferenceLongClick(Preference preference) {
        if (this.isInActinoMode) {
            return false;
        }
        getActivity().startActionMode(new ActionModeCallBack());
        if (preference instanceof SavedAccessPointPreference) {
            ((SavedAccessPointPreference) preference).setBtnChecked(true);
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        ActionBar appCompatActionBar;
        if (preference instanceof SavedAccessPointPreference) {
            SavedAccessPointPreference savedAccessPointPreference = (SavedAccessPointPreference) preference;
            if (this.isInActinoMode) {
                savedAccessPointPreference.setBtnChecked(!savedAccessPointPreference.isChecked());
                updateActionModeTitle();
                if (isAllUnChecked()) {
                    this.mEditActionMode.finish();
                    if ((getActivity() instanceof AppCompatActivity) && (appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar()) != null) {
                        appCompatActionBar.setTitle(getResources().getString(R.string.saved_wifi));
                    }
                } else if (isAllChecked()) {
                    ((EditActionMode) this.mEditActionMode).setButton(16908314, null, R.drawable.action_mode_title_button_deselect_all);
                } else {
                    ((EditActionMode) this.mEditActionMode).setButton(16908314, null, R.drawable.action_mode_title_button_select_all);
                }
            }
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.android.wifitrackerlib.SavedNetworkTracker.SavedNetworkTrackerCallback
    public void onSavedWifiEntriesChanged() {
        this.mWifiEntries = this.mSavedNetworkTracker.getSavedWifiEntries();
        if (this.isInActinoMode) {
            return;
        }
        updateUI();
    }

    @Override // com.android.wifitrackerlib.SavedNetworkTracker.SavedNetworkTrackerCallback
    public void onSubscriptionWifiEntriesChanged() {
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    /* renamed from: onWifiStateChanged */
    public void lambda$onInternetTypeChanged$4() {
    }

    public void showDeleteDialog(final WifiEntry wifiEntry) {
        AlertDialog create = new AlertDialog.Builder(getActivity(), R.style.AlertDialog_Theme_DayNight).setTitle(R.string.delete_saved_network).setMessage(getString(R.string.wifi_ssid) + ": " + wifiEntry.getSsid() + "\n" + getString(R.string.wifi_security) + ": " + wifiEntry.getSecurityString(false)).setPositiveButton(R.string.wifi_menu_forget, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.MiuiSavedAccessPointsWifiSettings.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiSavedAccessPointsWifiSettings.this.deleteSavedConfig(wifiEntry);
            }
        }).setNegativeButton(R.string.wifi_setup_cancel, (DialogInterface.OnClickListener) null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.wifi.MiuiSavedAccessPointsWifiSettings$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                MiuiSavedAccessPointsWifiSettings.this.lambda$showDeleteDialog$0(dialogInterface);
            }
        }).create();
        if (!this.mIsDismiss || isOperatorForbidDelSsid(wifiEntry.getSsid())) {
            return;
        }
        this.mIsDismiss = false;
        create.show();
    }
}
