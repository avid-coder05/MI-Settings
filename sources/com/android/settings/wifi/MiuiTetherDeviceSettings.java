package com.android.settings.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.MacAddress;
import android.net.TetheredClient;
import android.net.TetheringManager;
import android.net.wifi.MiuiWifiManager;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiClient;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.lang.Character;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class MiuiTetherDeviceSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private Set<String> mBlockList;
    private Preference mBlockListPreference;
    private SharedPreferences mBlockListPrefs;
    private PreferenceCategory mCategory;
    private Collection<TetheredClient> mClients;
    private ConnectivityManager mCm;
    private List<WifiClient> mConnectedDevices;
    private AddDeviceToBlockListDialog mDialog;
    private boolean mDialogShow;
    private Handler mHandler;
    private DropDownPreference mListPreference;
    private int mMaxNumberOfClients;
    private MiuiWifiManager mMiuiWifiManager;
    private SoftApConfiguration mSoftApConfig;
    private TetheringManager mTm;
    private WifiManager mWifiManager;
    private final Object mLock = new Object();
    private boolean isUpdateOnceNeeded = true;
    private WifiManager.SoftApCallback mSoftApCallback = new WifiManager.SoftApCallback() { // from class: com.android.settings.wifi.MiuiTetherDeviceSettings.1
        public void onConnectedClientsChanged(List<WifiClient> list) {
            MiuiTetherDeviceSettings.this.updateStaConnectStatus();
        }

        public void onStateChanged(int i, int i2) {
        }
    };
    private TetheringManager.TetheringEventCallback mTetheringCallback = new TetheringManager.TetheringEventCallback() { // from class: com.android.settings.wifi.MiuiTetherDeviceSettings.2
        public void onClientsChanged(Collection<TetheredClient> collection) {
            if (collection == null) {
                return;
            }
            synchronized (MiuiTetherDeviceSettings.this.mLock) {
                MiuiTetherDeviceSettings.this.mClients = new ArrayList(collection);
            }
            if (MiuiTetherDeviceSettings.this.isUpdateOnceNeeded) {
                MiuiTetherDeviceSettings.this.isUpdateOnceNeeded = false;
                MiuiTetherDeviceSettings.this.updateStaConnectStatus();
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class AddDeviceToBlockListDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
        private String mBlockDeviceName;
        private boolean mConfigureConfirmed;
        private AlertDialog mDialog;
        private MacAddress macAddress;

        private AddDeviceToBlockListDialog(MacAddress macAddress, String str) {
            this.macAddress = macAddress;
            this.mBlockDeviceName = str;
            FragmentActivity activity = MiuiTetherDeviceSettings.this.getActivity();
            AlertDialog create = new AlertDialog.Builder(activity).setTitle(activity.getResources().getString(R.string.block_list_dialog_title)).setMessage(String.format(activity.getString(R.string.block_list_dialog_content), this.macAddress.toString())).setIconAttribute(16843605).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
            this.mDialog = create;
            create.setOnDismissListener(this);
        }

        public String getBlockDeviceName() {
            return this.mBlockDeviceName;
        }

        public String getDeviceInfo() {
            return this.macAddress.toString();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            this.mConfigureConfirmed = i == -1;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) {
            MiuiTetherDeviceSettings.this.mDialogShow = false;
            if (this.mConfigureConfirmed) {
                MiuiTetherDeviceSettings.this.mBlockList.add(this.macAddress.toString());
                MiuiTetherDeviceSettings.this.saveInfoToSharedPreferences(this.macAddress, this.mBlockDeviceName);
                if (MiuiUtils.getInstance().isSapBlacklistOffloadSupport(MiuiTetherDeviceSettings.this.getContext()) && MiuiTetherDeviceSettings.this.mBlockList.size() < 20) {
                    MiuiTetherDeviceSettings.this.mMiuiWifiManager.addHotSpotMacBlackListOffload(this.macAddress.toString());
                }
                MiuiUtils.getInstance().setHotSpotMacBlackSet(MiuiTetherDeviceSettings.this.getActivity(), MiuiTetherDeviceSettings.this.mBlockList);
                this.mConfigureConfirmed = false;
            }
        }

        public void show() {
            MiuiTetherDeviceSettings.this.mDialogShow = true;
            this.mDialog.show();
        }
    }

    /* loaded from: classes2.dex */
    private class restartWifiAp extends AsyncTask<Void, Integer, Void> {
        private Context context;

        public restartWifiAp(Context context) {
            this.context = context;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            for (int i = 0; i < 10; i++) {
                if (MiuiTetherDeviceSettings.this.mWifiManager.getWifiApState() == 11) {
                    MiuiTetherDeviceSettings.this.mCm.startTethering(0, true, new ConnectivityManager.OnStartTetheringCallback() { // from class: com.android.settings.wifi.MiuiTetherDeviceSettings.restartWifiAp.1
                        public void onTetheringFailed() {
                        }

                        public void onTetheringStarted() {
                        }
                    });
                    return null;
                }
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addDevicesPreference() {
        if (getPreferenceScreen().findPreference("connected_devices") == null) {
            getPreferenceScreen().addPreference(this.mCategory);
        }
        this.mCategory.removeAll();
        synchronized (this.mLock) {
            for (TetheredClient tetheredClient : this.mClients) {
                boolean z = false;
                if (tetheredClient.getTetheringType() == 0) {
                    String macAddress = tetheredClient.getMacAddress().toString();
                    Iterator<String> it = this.mBlockList.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        } else if (macAddress.equals(it.next())) {
                            z = true;
                            break;
                        }
                    }
                    if (!z) {
                        this.mCategory.addPreference(createPreference(tetheredClient));
                    }
                }
            }
        }
        if (this.mCategory.getPreferenceCount() == 0) {
            getPreferenceScreen().removePreference(this.mCategory);
        }
    }

    private SoftApConfiguration buildMaxConnectedDevicesNumber(int i) {
        SoftApConfiguration build = new SoftApConfiguration.Builder(this.mSoftApConfig).setMaxNumberOfClients(i).build();
        this.mSoftApConfig = build;
        return build;
    }

    private void cleanDeviceInfoInSharedPreferences() {
        for (String str : this.mBlockListPrefs.getAll().keySet()) {
            if (!this.mBlockList.contains(str)) {
                removeInfoFromSharedPreferences(str);
            }
        }
    }

    private ValuePreference createPreference(TetheredClient tetheredClient) {
        ValuePreference valuePreference = new ValuePreference(getPrefContext());
        String str = null;
        for (int i = 0; i < tetheredClient.getAddresses().size(); i++) {
            str = ((TetheredClient.AddressInfo) tetheredClient.getAddresses().get(i)).getHostname();
        }
        if (TextUtils.isEmpty(str) || isGarbledCode(str)) {
            valuePreference.setTitle(tetheredClient.getMacAddress().toString());
        } else {
            valuePreference.setTitle(str);
            valuePreference.setSummary(tetheredClient.getMacAddress().toString());
        }
        valuePreference.setShowRightArrow(false);
        return valuePreference;
    }

    private void getBlockList() {
        this.mBlockList = MiuiUtils.getInstance().getHotSpotMacBlackSet(getContext());
        this.mBlockListPrefs = getActivity().getSharedPreferences("tetherBlockListPrefs", 0);
        cleanDeviceInfoInSharedPreferences();
    }

    private int getMaxNumberOfClients() {
        int maxNumberOfClients = this.mSoftApConfig.getMaxNumberOfClients();
        this.mMaxNumberOfClients = maxNumberOfClients;
        return maxNumberOfClients;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<WifiClient> getTetherConnectedDevices() {
        MiuiWifiManager miuiWifiManager = this.mMiuiWifiManager;
        return miuiWifiManager != null ? miuiWifiManager.getConnectedWifiClient() : new ArrayList();
    }

    private void initTethering() {
        this.mHandler = new Handler();
        this.mCm = (ConnectivityManager) getSystemService("connectivity");
        this.mTm = (TetheringManager) getSystemService("tethering");
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        this.mMiuiWifiManager = (MiuiWifiManager) getSystemService("MiuiWifiService");
        this.mSoftApConfig = this.mWifiManager.getSoftApConfiguration();
        this.mClients = new ArrayList();
        this.mConnectedDevices = new ArrayList();
        getBlockList();
    }

    private boolean isChineseEncoding(char c) {
        Character.UnicodeBlock of = Character.UnicodeBlock.of(c);
        return of == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || of == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || of == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || of == Character.UnicodeBlock.GENERAL_PUNCTUATION || of == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || of == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    private boolean isGarbledCode(String str) {
        Pattern compile = Pattern.compile("\\s*|t*|r*|n*");
        if (compile == null) {
            return true;
        }
        char[] charArray = compile.matcher(str).replaceAll("").replaceAll("\\p{P}", "").trim().toCharArray();
        int i = 0;
        for (int i2 = 0; i2 < charArray.length; i2++) {
            if (!Character.isLetterOrDigit(charArray[i2]) && !isChineseEncoding(charArray[i2])) {
                i++;
            }
        }
        return i != 0;
    }

    private void removeInfoFromSharedPreferences(String str) {
        SharedPreferences.Editor edit = this.mBlockListPrefs.edit();
        edit.remove(str);
        edit.commit();
    }

    private void restoreDisDialog() {
        Bundle bundle = getArguments() != null ? getArguments().getBundle("saved_bundle") : null;
        if (bundle != null) {
            boolean z = bundle.getBoolean("show_dialog");
            this.mDialogShow = z;
            if (z) {
                AddDeviceToBlockListDialog addDeviceToBlockListDialog = new AddDeviceToBlockListDialog(MacAddress.fromString(bundle.getString("save_device_mac")), bundle.getString("save_device_name"));
                this.mDialog = addDeviceToBlockListDialog;
                addDeviceToBlockListDialog.show();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveInfoToSharedPreferences(MacAddress macAddress, String str) {
        SharedPreferences.Editor edit = this.mBlockListPrefs.edit();
        edit.putString(macAddress.toString(), str);
        edit.commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStaConnectStatus() {
        if (isAdded()) {
            getContext().getMainThreadHandler().postDelayed(new Runnable() { // from class: com.android.settings.wifi.MiuiTetherDeviceSettings.3
                @Override // java.lang.Runnable
                public void run() {
                    if (MiuiTetherDeviceSettings.this.getActivity() == null || MiuiTetherDeviceSettings.this.mConnectedDevices == null) {
                        return;
                    }
                    int size = MiuiTetherDeviceSettings.this.mConnectedDevices.size();
                    MiuiTetherDeviceSettings miuiTetherDeviceSettings = MiuiTetherDeviceSettings.this;
                    miuiTetherDeviceSettings.mConnectedDevices = miuiTetherDeviceSettings.getTetherConnectedDevices();
                    if (size != MiuiTetherDeviceSettings.this.mConnectedDevices.size()) {
                        MiuiTetherDeviceSettings.this.addDevicesPreference();
                    }
                }
            }, 500L);
        } else {
            Log.d("MiuiTetherDeviceSettings", "MiuiTetherDeviceSettings is not releated to Activity, so ignore it");
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiTetherDeviceSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initTethering();
        addPreferencesFromResource(R.xml.miui_tether_devices);
        this.mCategory = (PreferenceCategory) findPreference("connected_devices");
        this.mBlockListPreference = findPreference("block_list");
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("max_number");
        this.mListPreference = dropDownPreference;
        dropDownPreference.setValue(Integer.toString(getMaxNumberOfClients() == 0 ? 2007 : this.mMaxNumberOfClients));
        DropDownPreference dropDownPreference2 = this.mListPreference;
        dropDownPreference2.setSummary(dropDownPreference2.getEntry());
        this.mListPreference.setOnPreferenceChangeListener(this);
        addDevicesPreference();
        restoreDisDialog();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mListPreference) {
            String str = (String) obj;
            if (buildMaxConnectedDevicesNumber(Integer.parseInt(str)) != null) {
                this.mWifiManager.setSoftApConfiguration(this.mSoftApConfig);
                this.mListPreference.setValue(str);
                DropDownPreference dropDownPreference = this.mListPreference;
                dropDownPreference.setSummary(dropDownPreference.getEntry());
                if (this.mWifiManager.getWifiApState() == 13) {
                    Context context = getContext();
                    this.mCm.stopTethering(0);
                    new restartWifiAp(context).execute(new Void[0]);
                    return true;
                }
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == this.mBlockListPreference) {
            startFragment(this, MiuiTetherBlockList.class.getName(), 1, (Bundle) null, R.string.block_list_title);
        } else if (preference != this.mListPreference) {
            AddDeviceToBlockListDialog addDeviceToBlockListDialog = new AddDeviceToBlockListDialog(MacAddress.fromString((preference.getSummary() == null ? preference.getTitle() : preference.getSummary()).toString()), preference.getSummary() == null ? null : preference.getTitle().toString());
            this.mDialog = addDeviceToBlockListDialog;
            addDeviceToBlockListDialog.show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        getBlockList();
        updateStaConnectStatus();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mDialog != null) {
            bundle.putBoolean("show_dialog", this.mDialogShow);
            bundle.putString("save_device_name", this.mDialog.getBlockDeviceName());
            bundle.putString("save_device_mac", this.mDialog.getDeviceInfo());
            getArguments().putBundle("saved_bundle", bundle);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mWifiManager.registerSoftApCallback(new HandlerExecutor(this.mHandler), this.mSoftApCallback);
        this.mTm.registerTetheringEventCallback(new HandlerExecutor(this.mHandler), this.mTetheringCallback);
        View inflate = View.inflate(getActivity(), R.layout.tether_no_device_connected, null);
        ((ViewGroup) getListView().getParent()).addView(inflate);
        setEmptyView(inflate);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mWifiManager.unregisterSoftApCallback(this.mSoftApCallback);
        this.mTm.unregisterTetheringEventCallback(this.mTetheringCallback);
    }
}
