package com.android.settings.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.bluetooth.ble.app.IMiuiHeadsetService;
import com.android.settings.MiuiDeviceNameEditFragment;
import com.android.settings.MiuiSearchDrawable;
import com.android.settings.MiuiUtils;
import com.android.settings.OneTrackManager;
import com.android.settings.R;
import com.android.settings.SettingsApplication;
import com.android.settings.bluetooth.plugin.BluetoothCloudControlTools;
import com.android.settings.connectivity.MiuiBluetoothDataBaseOperaterUtil;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.bluetooth.ble.MiBleDeviceManager;
import miui.yellowpage.Tag;

/* loaded from: classes.dex */
public class MiuiBluetoothSettings extends BluetoothSettings {
    private BluetoothUpdateTextPreference mBluetoothUpdateTextPreference;
    private Preference mBroadcastAudioPreference;
    private ValuePreference mDeviceNameEditPreference;
    private Handler mHandler;
    private Handler mMainHandler;
    private MiuiSearchDrawable mSearchIcon;
    private HandlerThread mThread;
    private static final String TAG = MiuiBluetoothSettings.class.getSimpleName();
    private static String HEADSETPLUGIN_INITED_NOTIFY = "BLUETOOTHHEADSETPLUGIN_INITED";
    private static int HEADSETPLUGIN_ENABLE = 1;
    private static int HEADSETPLUGIN_NOTSET = -1;
    private static int HEADSETPLUGIN_INITED = 1;
    private static String PREFIX_SUPPORT_CLOUD_SHARE_DEVICEID = "cloud_shared";
    boolean mMiBleDeviceManagerInited = false;
    boolean mShowDevicesWithoutNamesOld = false;
    private int mStatus = HEADSETPLUGIN_NOTSET;
    private DeleteDeviceZipRunnable mDeleteDeviceZipRunnable = new DeleteDeviceZipRunnable(this);
    private int mBondState = 10;
    private IMiuiHeadsetService mService = null;
    private SettingsObserver mObserver = null;
    private Map<BluetoothDevice, DeviceSurpport> mCachedDeviceInfoList = Collections.synchronizedMap(new HashMap());
    private HeadsetInfoHandler mInfoHandler = null;
    private BluetoothCloudControlTools mBluetoothCloudControlTools = null;
    private ServiceConnection mConnection = new ServiceConnection() { // from class: com.android.settings.bluetooth.MiuiBluetoothSettings.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MiuiBluetoothSettings.this.mService = IMiuiHeadsetService.Stub.asInterface(iBinder);
            try {
                if (MiuiBluetoothSettings.this.mObserver != null) {
                    MiuiBluetoothSettings.this.getContext().getContentResolver().unregisterContentObserver(MiuiBluetoothSettings.this.mObserver);
                    MiuiBluetoothSettings.this.mObserver = null;
                }
            } catch (Exception e) {
                Log.e(MiuiBluetoothSettings.TAG, "error " + e);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            MiuiBluetoothSettings.this.mService = null;
        }
    };
    private boolean mBtEnablePrefDelayTag = false;
    private Runnable mRunnable = new Runnable() { // from class: com.android.settings.bluetooth.MiuiBluetoothSettings.2
        @Override // java.lang.Runnable
        public void run() {
            MiuiBluetoothSettings.this.mLocalAdapter.setBluetoothEnabled(false);
        }
    };
    private Preference.OnPreferenceChangeListener mBtEnablePrefChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.bluetooth.MiuiBluetoothSettings.5
        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            if (MiuiBluetoothSettings.this.getListView().isComputingLayout()) {
                Log.e(MiuiBluetoothSettings.TAG, "RecycleView is computing layout, SKIP!");
                if (MiuiBluetoothSettings.this.mRunnable != null) {
                    MiuiBluetoothSettings.this.mBtEnablePrefDelayTag = true;
                    MiuiBluetoothSettings.this.getListView().postDelayed(MiuiBluetoothSettings.this.mRunnable, 800L);
                }
                return false;
            }
            if (MiuiBluetoothSettings.this.mBtEnablePrefDelayTag) {
                MiuiBluetoothSettings.this.getListView().removeCallbacks(MiuiBluetoothSettings.this.mRunnable);
                MiuiBluetoothSettings.this.mBtEnablePrefDelayTag = false;
            }
            MiuiBluetoothSettings.this.mBluetoothEnabler.checkedChanged(((Boolean) obj).booleanValue());
            MiuiBluetoothSettings.this.updateDeviceNamePreferenceStatus(false);
            MiuiBluetoothSettings.this.updateBroadcastAudioPreference(false);
            return true;
        }
    };
    private Preference.OnPreferenceClickListener mUpdateClickListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.bluetooth.MiuiBluetoothSettings.6
        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            MiuiBluetoothSettings.this.mBluetoothCloudControlTools.handlePreferenceTreeClick();
            return true;
        }
    };
    private Preference.OnPreferenceClickListener mBtHelpClickListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.bluetooth.MiuiBluetoothSettings.7
        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent();
            intent.putExtra("COME_FROM", "MIUI_BT_CONNECT_HELP");
            intent.setClassName("com.android.settings", "com.android.settings.bluetooth.MiuiHeadsetActivityPlugin");
            if (FitSplitUtils.isFitSplit()) {
                intent.addMiuiFlags(16);
            }
            MiuiBluetoothSettings.this.startActivity(intent);
            OneTrackManager.trackHelpClick(MiuiBluetoothSettings.this.getContext(), "BT");
            return true;
        }
    };
    private Preference.OnPreferenceClickListener mClickListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.bluetooth.MiuiBluetoothSettings.8
        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            Bundle bundle = new Bundle();
            bundle.putString(":miui:starting_window_label", "bluetooth_label");
            MiuiBluetoothSettings miuiBluetoothSettings = MiuiBluetoothSettings.this;
            miuiBluetoothSettings.startFragment(miuiBluetoothSettings, MiuiDeviceNameEditFragment.class.getName(), 0, bundle, 0);
            return true;
        }
    };
    private final View.OnClickListener mRefreshListener = new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiBluetoothSettings.9
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (MiuiBluetoothSettings.this.mBondState == 11) {
                Log.v(MiuiBluetoothSettings.TAG, "Cannot start scanning since device is in bonding state.");
            } else if (MiuiBluetoothSettings.this.mLocalAdapter.getBluetoothState() == 12) {
                ((InstrumentedPreferenceFragment) MiuiBluetoothSettings.this).mMetricsFeatureProvider.action(MiuiBluetoothSettings.this.getActivity(), 160, new Pair[0]);
                if (MiuiBluetoothSettings.this.mLocalAdapter.isDiscovering()) {
                    MiuiBluetoothSettings.this.mLocalAdapter.stopScanning();
                    return;
                }
                PreferenceGroup preferenceGroup = MiuiBluetoothSettings.this.mAvailableDevicesCategory;
                if (preferenceGroup != null) {
                    ((MiuiBluetoothFilterCategory) preferenceGroup).removeAll();
                }
                MiuiBluetoothSettings.this.startScanning();
            }
        }
    };
    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.MiuiBluetoothSettings.10
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d(MiuiBluetoothSettings.TAG, "BluetoothReceiver.onReceive intent=" + intent);
            if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (intExtra != 2 || bluetoothDevice == null || MiuiBluetoothSettings.this.mInfoHandler == null) {
                    return;
                }
                MiuiBluetoothSettings.this.mInfoHandler.sendMessageDelayed(MiuiBluetoothSettings.this.mInfoHandler.obtainMessage(0, bluetoothDevice), 500L);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CheckAsyncTask extends AsyncTask<CachedBluetoothDevice, Void, Boolean> {
        private WeakReference<CachedBluetoothDevice> cachedDeviceRef;
        private WeakReference<PreferenceGroup> deviceListGroupRef;
        private WeakReference<MiuiBluetoothSettings> miuiBluetoothSettingsRef;
        private WeakReference<Context> weakReference;

        CheckAsyncTask(Context context, MiuiBluetoothSettings miuiBluetoothSettings, CachedBluetoothDevice cachedBluetoothDevice, PreferenceGroup preferenceGroup) {
            this.weakReference = new WeakReference<>(context);
            this.miuiBluetoothSettingsRef = new WeakReference<>(miuiBluetoothSettings);
            this.deviceListGroupRef = new WeakReference<>(preferenceGroup);
            this.cachedDeviceRef = new WeakReference<>(cachedBluetoothDevice);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(CachedBluetoothDevice... cachedBluetoothDeviceArr) {
            CachedBluetoothDevice cachedBluetoothDevice = cachedBluetoothDeviceArr[0];
            return (!MiuiBTUtils.isRarelyUsedBluetoothDevice(cachedBluetoothDevice) || MiuiBTUtils.isNearByBluetoothDevice(cachedBluetoothDevice)) ? Boolean.TRUE : Boolean.FALSE;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            WeakReference<Context> weakReference = this.weakReference;
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            CachedBluetoothDevice cachedBluetoothDevice = this.cachedDeviceRef.get();
            if (bool.booleanValue()) {
                MiuiBluetoothSettings miuiBluetoothSettings = this.miuiBluetoothSettingsRef.get();
                if (miuiBluetoothSettings == null || cachedBluetoothDevice == null) {
                    return;
                }
                miuiBluetoothSettings.addDevice(cachedBluetoothDevice);
                return;
            }
            PreferenceGroup preferenceGroup = this.deviceListGroupRef.get();
            if (preferenceGroup == null || !(preferenceGroup instanceof MiuiBluetoothFilterCategory) || cachedBluetoothDevice == null) {
                return;
            }
            ((MiuiBluetoothFilterCategory) preferenceGroup).addDeviceCache(cachedBluetoothDevice);
        }
    }

    /* loaded from: classes.dex */
    private static class DeleteDeviceZipRunnable implements Runnable {
        private WeakReference<MiuiBluetoothSettings> mBluetoothSettings;

        public DeleteDeviceZipRunnable(MiuiBluetoothSettings miuiBluetoothSettings) {
            this.mBluetoothSettings = new WeakReference<>(miuiBluetoothSettings);
        }

        private synchronized void checkAndDeleteDeviceZip() {
            MiuiBluetoothSettings miuiBluetoothSettings;
            try {
                miuiBluetoothSettings = this.mBluetoothSettings.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (miuiBluetoothSettings == null) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
            String str = File.separator;
            sb.append(str);
            sb.append(Environment.DIRECTORY_DOWNLOADS);
            sb.append(str);
            sb.append("MiuiFastConnect");
            String sb2 = sb.toString();
            Log.d(MiuiBluetoothSettings.TAG, "checkAndDeleteDeviceZip base folder: " + sb2);
            LocalBluetoothAdapter localBluetoothAdapter = miuiBluetoothSettings.mLocalAdapter;
            if (localBluetoothAdapter != null && localBluetoothAdapter.getState() == 10) {
                File file = new File(sb2);
                if (!file.exists()) {
                    Settings.Global.putString(miuiBluetoothSettings.getContext().getContentResolver(), "miui_download_delete_fail_device", "");
                    return;
                } else if (deleteFile(file)) {
                    Settings.Global.putString(miuiBluetoothSettings.getContext().getContentResolver(), "miui_download_delete_fail_device", "");
                    Log.d(MiuiBluetoothSettings.TAG, "checkAndDeleteDeviceZip delete all");
                    return;
                }
            }
            String string = Settings.Global.getString(miuiBluetoothSettings.getContext().getContentResolver(), "miui_download_delete_fail_device");
            if (!TextUtils.isEmpty(string)) {
                for (String str2 : string.split(",")) {
                    String str3 = str2 + SplitConstants.DOT_ZIP;
                    File file2 = new File(sb2, str3);
                    if (file2.exists()) {
                        Log.d(MiuiBluetoothSettings.TAG, "checkAndDeleteDeviceZip: " + str3 + ", " + file2.delete());
                    }
                }
                Settings.Global.putString(miuiBluetoothSettings.getContext().getContentResolver(), "miui_download_delete_fail_device", "");
            }
        }

        private boolean deleteFile(File file) {
            LocalBluetoothAdapter localBluetoothAdapter;
            if (file == null) {
                return true;
            }
            MiuiBluetoothSettings miuiBluetoothSettings = this.mBluetoothSettings.get();
            if (miuiBluetoothSettings != null && (localBluetoothAdapter = miuiBluetoothSettings.mLocalAdapter) != null && localBluetoothAdapter.getState() == 10) {
                try {
                    if (file.isDirectory()) {
                        for (File file2 : file.listFiles()) {
                            deleteFile(file2);
                        }
                        file.delete();
                    } else {
                        file.delete();
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override // java.lang.Runnable
        public void run() {
            checkAndDeleteDeviceZip();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DeviceSurpport {
        public String mConfig;
        public String mPlugin;

        public DeviceSurpport(String str, String str2) {
            this.mConfig = str;
            this.mPlugin = str2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class HeadsetInfoHandler extends Handler {
        private WeakReference<MiuiBluetoothSettings> miuiBluetoothSettingsRef;

        public HeadsetInfoHandler(Looper looper, MiuiBluetoothSettings miuiBluetoothSettings) {
            super(looper);
            this.miuiBluetoothSettingsRef = new WeakReference<>(miuiBluetoothSettings);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            try {
                Log.d(MiuiBluetoothSettings.TAG, "handleMessage: what: " + message.what);
                int i = message.what;
                if (i == 0) {
                    MiuiBluetoothSettings.this.checkHeadsetSurpportMiuiFragment((BluetoothDevice) message.obj);
                } else if (i != 1) {
                } else {
                    WeakReference<MiuiBluetoothSettings> weakReference = this.miuiBluetoothSettingsRef;
                    MiuiBluetoothSettings miuiBluetoothSettings = weakReference != null ? weakReference.get() : null;
                    if (miuiBluetoothSettings != null) {
                        miuiBluetoothSettings.checkEnableHelpPreference();
                    }
                }
            } catch (Exception e) {
                Log.e(MiuiBluetoothSettings.TAG, "handler error:" + e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            try {
                if (MiuiBluetoothSettings.this.mService == null) {
                    if (DeviceListPreferenceFragment.headSetFeatureIsEnable(MiuiBluetoothSettings.this.getContext()) || DeviceListPreferenceFragment.headSetMoreDetailEnable(MiuiBluetoothSettings.this.getContext())) {
                        Log.d(MiuiBluetoothSettings.TAG, "properity changed init the headset service start ");
                        Intent intent = new Intent("miui.bluetooth.mible.BluetoothHeadsetService");
                        intent.setPackage("com.xiaomi.bluetooth");
                        MiuiBluetoothSettings.this.getContext().bindService(intent, MiuiBluetoothSettings.this.mConnection, 1);
                    }
                }
            } catch (Exception e) {
                Log.e(MiuiBluetoothSettings.TAG, "error " + e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkEnableHelpPreference() {
        try {
            boolean z = true;
            boolean z2 = false;
            boolean z3 = getResources().getConfiguration().locale.getCountry().equals("CN") && getResources().getConfiguration().locale.getLanguage().equals("zh");
            if (this.mStatus == HEADSETPLUGIN_INITED) {
                String str = "";
                SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
                if (splitInfoManagerService != null) {
                    str = splitInfoManagerService.getCurrentSplitInfoVersion();
                    if (z3 && !TextUtils.isEmpty(str) && MiuiBluetoothDataBaseOperaterUtil.queryPluginSupport(getActivity(), str, "bt_help")) {
                        Log.d(TAG, "current settings verison: " + str + ", manager: " + splitInfoManagerService + ", languageFlag: " + z3);
                        z2 = z;
                    }
                }
                z = false;
                Log.d(TAG, "current settings verison: " + str + ", manager: " + splitInfoManagerService + ", languageFlag: " + z3);
                z2 = z;
            }
            setConnectHelpPreferenceVisible(z2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkHeadsetSurpportMiuiFragment(BluetoothDevice bluetoothDevice) {
        String str;
        String str2 = "";
        if (this.mService == null) {
            return;
        }
        String str3 = TAG;
        Log.d(str3, "checkHeadsetSurpportMiuiFragment: " + bluetoothDevice.getAddress());
        try {
            String checkSupport = this.mService.checkSupport(bluetoothDevice);
            if ("".equals(checkSupport)) {
                return;
            }
            if (this.mStatus == HEADSETPLUGIN_INITED) {
                String currentSplitInfoVersion = SplitInfoManagerService.getInstance().getCurrentSplitInfoVersion();
                str = this.mService.setCommonCommand(120, "" + currentSplitInfoVersion, bluetoothDevice);
                str2 = currentSplitInfoVersion;
            } else {
                str = "";
            }
            Log.d(str3, "current settings verison:" + str2);
            Log.d(str3, "Device:" + bluetoothDevice.getAddress() + ", headset customer:" + checkSupport + ", plugin state:" + str);
            if (this.mCachedDeviceInfoList == null) {
                this.mCachedDeviceInfoList = Collections.synchronizedMap(new HashMap());
            }
            this.mCachedDeviceInfoList.put(bluetoothDevice, new DeviceSurpport(checkSupport, str));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "error " + e);
        }
    }

    private void initService() {
        try {
            if (!DeviceListPreferenceFragment.headSetFeatureIsEnable(getContext()) && !DeviceListPreferenceFragment.headSetMoreDetailEnable(getContext())) {
                if (this.mObserver == null) {
                    this.mObserver = new SettingsObserver(null);
                    getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("mi_tws_hs_feature_enable"), false, this.mObserver);
                    getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("mi_tws_deviceid_list_hs_feature_enable"), false, this.mObserver);
                }
            }
            Log.d(TAG, "init the headset service start ");
            Intent intent = new Intent("miui.bluetooth.mible.BluetoothHeadsetService");
            intent.setPackage("com.xiaomi.bluetooth");
            getContext().bindService(intent, this.mConnection, 1);
        } catch (Exception e) {
            Log.e(TAG, "init the headset service failed " + e);
        }
    }

    private void internalSmoothScrollToPosition() {
        RecyclerView listView = getListView();
        if (listView == null || listView.getChildCount() <= 0) {
            return;
        }
        listView.smoothScrollToPosition(0);
    }

    private void renameMyDevice() {
        String bluetoothName = MiuiBTUtils.isCustomizedOperator() ? MiuiBTUtils.getBluetoothName() : MiuiSettings.System.getDeviceName(getActivity());
        this.mLocalAdapter.setName(bluetoothName);
        this.mDeviceNameEditPreference.setValue(bluetoothName);
    }

    private void setConnectHelpPreferenceVisible(final boolean z) {
        if (this.mMainHandler == null) {
            this.mMainHandler = new Handler(Looper.getMainLooper());
        }
        this.mMainHandler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiBluetoothSettings.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Log.d(MiuiBluetoothSettings.TAG, "connect help prefernce visible: " + z);
                    Preference findPreference = MiuiBluetoothSettings.this.findPreference("bt_connect_help_flag");
                    if (findPreference != null) {
                        findPreference.setVisible(z);
                        if (z) {
                            findPreference.setOnPreferenceClickListener(MiuiBluetoothSettings.this.mBtHelpClickListener);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBroadcastAudioPreference(boolean z) {
        Preference preference = this.mBroadcastAudioPreference;
        if (preference != null) {
            preference.setEnabled(z);
            if (z) {
                this.mBroadcastAudioPreference.setSummary(R.string.bluetooth_broadcast_audio_summary);
            } else {
                this.mBroadcastAudioPreference.setSummary(R.string.bluetooth_broadcast_audio_summary_disable);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDeviceNamePreferenceStatus(boolean z) {
        if (this.mDeviceNameEditPreference == null || UserHandle.myUserId() != 0 || isUiRestricted()) {
            return;
        }
        this.mDeviceNameEditPreference.setEnabled(z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void addCachedDevices() {
        this.mGattProfile.getBondDevices();
        super.addCachedDevices();
    }

    void addDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        super.onDeviceAdded(cachedBluetoothDevice);
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings
    public void addNewOnSavedDevice(String str, String str2, String str3, int i, List<LocalBluetoothProfile> list) {
        Preference findPreference;
        PreferenceGroup preferenceGroup = this.mPairedDevicesCategory;
        if (preferenceGroup != null) {
            if (preferenceGroup.findPreference(str) != null) {
                Log.d(TAG, "this device preference exists in mPairedDevicesCategory");
                return;
            }
            PreferenceGroup preferenceGroup2 = this.mAvailableDevicesCategory;
            if (preferenceGroup2 != null && (findPreference = preferenceGroup2.findPreference(str)) != null) {
                CachedBluetoothDevice cachedDevice = ((BluetoothDevicePreference) findPreference).getCachedDevice();
                setDeviceListGroup(this.mAvailableDevicesCategory);
                onDeviceDeleted(cachedDevice);
                PreferenceGroup preferenceGroup3 = this.mAvailableDevicesCategory;
                if (preferenceGroup3 != null) {
                    ((MiuiBluetoothFilterCategory) preferenceGroup3).setShowDivider(false);
                }
            }
            BluetoothDeviceFilter.Filter filter = this.mFilter;
            PreferenceGroup preferenceGroup4 = this.mDeviceListGroup;
            getPreferenceScreen().addPreference(this.mPairedDevicesCategory);
            setFilter(BluetoothDeviceFilter.BONDED_DEVICE_FILTER);
            String str4 = TAG;
            Log.d(str4, "set the pointer to mPairedDevicesCategory");
            setDeviceListGroup(this.mPairedDevicesCategory);
            Log.d(str4, "start to create DevicePreference");
            getDeviceFromOnLineBluetooth(str, str2, str3, new BluetoothClass(i), list);
            setFilter(filter);
            setDeviceListGroup(preferenceGroup4);
        }
    }

    public void checkAndDeleteOnSavedDevice() {
        PreferenceGroup preferenceGroup;
        Preference findPreference;
        String string = Settings.Global.getString(getContext().getContentResolver(), "virtual_bluetooth_device_delete");
        if (!TextUtils.isEmpty(string) && !string.equals("0") && (preferenceGroup = this.mPairedDevicesCategory) != null && (findPreference = preferenceGroup.findPreference(string)) != null) {
            this.mPairedDevicesCategory.removePreference(findPreference);
        }
        Settings.Global.putString(getContext().getContentResolver(), "virtual_bluetooth_device_delete", "0");
    }

    void checkDevicePreference(CachedBluetoothDevice cachedBluetoothDevice) {
        new CheckAsyncTask(getActivity(), this, cachedBluetoothDevice, this.mDeviceListGroup).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cachedBluetoothDevice);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0088  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x00b4  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x00c9  */
    @Override // com.android.settings.bluetooth.BluetoothSettings
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean checkStartMiuiHeadset(com.android.settingslib.bluetooth.CachedBluetoothDevice r7) {
        /*
            r6 = this;
            java.util.Map<android.bluetooth.BluetoothDevice, com.android.settings.bluetooth.MiuiBluetoothSettings$DeviceSurpport> r0 = r6.mCachedDeviceInfoList
            java.lang.String r1 = ""
            if (r0 == 0) goto L33
            android.bluetooth.BluetoothDevice r2 = r7.getDevice()
            java.lang.Object r0 = r0.get(r2)
            com.android.settings.bluetooth.MiuiBluetoothSettings$DeviceSurpport r0 = (com.android.settings.bluetooth.MiuiBluetoothSettings.DeviceSurpport) r0
            if (r0 != 0) goto L25
            android.bluetooth.BluetoothDevice r0 = r7.getDevice()
            r6.checkHeadsetSurpportMiuiFragment(r0)
            java.util.Map<android.bluetooth.BluetoothDevice, com.android.settings.bluetooth.MiuiBluetoothSettings$DeviceSurpport> r0 = r6.mCachedDeviceInfoList
            android.bluetooth.BluetoothDevice r2 = r7.getDevice()
            java.lang.Object r0 = r0.get(r2)
            com.android.settings.bluetooth.MiuiBluetoothSettings$DeviceSurpport r0 = (com.android.settings.bluetooth.MiuiBluetoothSettings.DeviceSurpport) r0
        L25:
            if (r0 == 0) goto L33
            java.lang.String r1 = com.android.settings.bluetooth.MiuiBluetoothSettings.TAG
            java.lang.String r2 = "deviceSupportInfo is exit."
            android.util.Log.d(r1, r2)
            java.lang.String r1 = r0.mConfig
            java.lang.String r0 = r0.mPlugin
            goto L34
        L33:
            r0 = r1
        L34:
            java.lang.String r2 = com.android.settings.bluetooth.MiuiBluetoothSettings.TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Device:"
            r3.append(r4)
            android.bluetooth.BluetoothDevice r4 = r7.getDevice()
            java.lang.String r4 = r4.getAddress()
            r3.append(r4)
            java.lang.String r4 = ", headset customer:"
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = ", plugin state:"
            r3.append(r4)
            r3.append(r0)
            java.lang.String r4 = ", mStatus:"
            r3.append(r4)
            int r4 = r6.mStatus
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            android.util.Log.d(r2, r3)
            android.content.Intent r3 = new android.content.Intent
            java.lang.String r4 = "miui.bluetooth.action.HEADSET_SETTINGS"
            r3.<init>(r4)
            android.bluetooth.BluetoothDevice r7 = r7.getDevice()
            java.lang.String r4 = "android.bluetooth.device.extra.DEVICE"
            r3.putExtra(r4, r7)
            r7 = 268468224(0x10008000, float:2.5342157E-29)
            r3.addFlags(r7)
            boolean r7 = com.android.settings.bluetooth.FitSplitUtils.isFitSplit()
            if (r7 == 0) goto L98
            r7 = 32768(0x8000, float:4.5918E-41)
            r3.removeFlags(r7)
            r7 = 268435456(0x10000000, float:2.524355E-29)
            r3.removeFlags(r7)
            r7 = 16
            r3.addMiuiFlags(r7)
        L98:
            java.lang.String r7 = "MIUI_HEADSET_SUPPORT"
            r3.putExtra(r7, r1)
            java.lang.String r7 = "COME_FROM"
            java.lang.String r4 = "MIUI_BLUETOOTH_SETTINGS"
            r3.putExtra(r7, r4)
            java.lang.String r7 = "android.intent.category.DEFAULT"
            r3.addCategory(r7)
            java.lang.String r7 = "true"
            boolean r7 = r7.equals(r0)
            r0 = 1
            r4 = 0
            if (r7 == 0) goto Lc9
            int r7 = r6.mStatus
            int r2 = com.android.settings.bluetooth.MiuiBluetoothSettings.HEADSETPLUGIN_INITED
            if (r7 != r2) goto Lee
            boolean r7 = android.text.TextUtils.isEmpty(r1)
            if (r7 != 0) goto Lee
            java.lang.String r7 = "miui.bluetooth.action.HEADSET_SETTINGS_PLUGIN"
            r3.setAction(r7)
            r6.startActivityForResult(r3, r4)
            return r0
        Lc9:
            boolean r7 = android.text.TextUtils.isEmpty(r1)
            if (r7 != 0) goto Lee
            boolean r7 = com.android.settings.bluetooth.HeadsetIDConstants.checkSupport(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r5 = "headset customer checkSettingsSupport "
            r1.append(r5)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r2, r1)
            if (r7 != 0) goto Lea
            return r4
        Lea:
            r6.startActivityForResult(r3, r4)
            return r0
        Lee:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.MiuiBluetoothSettings.checkStartMiuiHeadset(com.android.settingslib.bluetooth.CachedBluetoothDevice):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void createDevicePreference(CachedBluetoothDevice cachedBluetoothDevice) {
        GattProfile gattProfile;
        Log.d(TAG, "createDevicePreference");
        if (this.mDeviceListGroup instanceof PreferenceScreen) {
            return;
        }
        if (!this.mMiBleDeviceManagerInited || (this.mMiBleDeviceManager.getDeviceType(cachedBluetoothDevice.getDevice().getAddress()) == 0 && !((gattProfile = this.mGattProfile) != null && gattProfile.isBleDevice(cachedBluetoothDevice.getDevice()) && GattProfile.isBond(cachedBluetoothDevice.getDevice())))) {
            super.createDevicePreference(cachedBluetoothDevice);
            return;
        }
        String address = cachedBluetoothDevice.getDevice().getAddress();
        BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) getCachedPreference(address);
        if (bluetoothDevicePreference != null) {
            this.mPairedDevicesCategory.removePreference(bluetoothDevicePreference);
            this.mAvailableDevicesCategory.removePreference(bluetoothDevicePreference);
        }
        MiuiBluetoothDevicePreference miuiBluetoothDevicePreference = new MiuiBluetoothDevicePreference(getPrefContext(), cachedBluetoothDevice, this.mMiBleDeviceManager, this.mShowDevicesWithoutNames);
        miuiBluetoothDevicePreference.setKey(address);
        initDevicePreference(miuiBluetoothDevicePreference);
        if (GattProfile.isBond(cachedBluetoothDevice.getDevice()) || cachedBluetoothDevice.getBondState() == 12) {
            this.mPairedDevicesCategory.addPreference(miuiBluetoothDevicePreference);
        } else {
            this.mAvailableDevicesCategory.addPreference(miuiBluetoothDevicePreference);
        }
        this.mDevicePreferenceMap.put(cachedBluetoothDevice, miuiBluetoothDevicePreference);
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiBluetoothSettings.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void initDevicePreference(BluetoothDevicePreference bluetoothDevicePreference) {
        HeadsetInfoHandler headsetInfoHandler;
        CachedBluetoothDevice cachedDevice = bluetoothDevicePreference.getCachedDevice();
        if (GattProfile.isBond(cachedDevice.getDevice()) && this.mMiBleDeviceManager.getDeviceType(cachedDevice.getDevice().getAddress()) != 0) {
            bluetoothDevicePreference.setWidgetLayoutResource(R.layout.miuix_preference_connect_widget_layout);
        }
        if (cachedDevice.getDevice().getBondState() == 12 && (headsetInfoHandler = this.mInfoHandler) != null) {
            headsetInfoHandler.sendMessage(headsetInfoHandler.obtainMessage(0, cachedDevice.getDevice()));
        }
        super.initDevicePreference(bluetoothDevicePreference);
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void initPreferencesFromPreferenceScreen() {
        super.initPreferencesFromPreferenceScreen();
        FragmentActivity activity = getActivity();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("bluetooth_enable");
        this.mBluetoothEnablePreference = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this.mBtEnablePrefChangeListener);
        this.mBluetoothEnabler = new MiuiBluetoothEnabler(activity, this.mBluetoothEnablePreference);
        ValuePreference valuePreference = (ValuePreference) findPreference("bluetooth_device_name_edit");
        this.mDeviceNameEditPreference = valuePreference;
        valuePreference.setShowRightArrow(true);
        this.mDeviceNameEditPreference.setOnPreferenceClickListener(this.mClickListener);
        this.mDeviceNameEditPreference.setEnabled(UserHandle.myUserId() == 0 && !isUiRestricted());
        this.mBroadcastAudioPreference = findPreference("bluetooth_broadcast_audio_settings");
        if (!SystemProperties.getBoolean("persist.vendor.service.bt.lea_test", false)) {
            getPreferenceScreen().removePreference(this.mBroadcastAudioPreference);
            this.mBroadcastAudioPreference = null;
        }
        ((MiuiBluetoothFilterCategory) this.mAvailableDevicesCategory).setOnSettingsClickListener(this.mRefreshListener);
        if (this.mLocalAdapter.getBluetoothState() != 12) {
            updateProgressUi(false);
            updateBroadcastAudioPreference(false);
        }
        BluetoothUpdateTextPreference bluetoothUpdateTextPreference = (BluetoothUpdateTextPreference) findPreference("bluetooth_version_update");
        this.mBluetoothUpdateTextPreference = bluetoothUpdateTextPreference;
        if (bluetoothUpdateTextPreference != null) {
            bluetoothUpdateTextPreference.setOnPreferenceClickListener(this.mUpdateClickListener);
        }
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings
    public boolean isCloudSharedDeviceId(String str) {
        try {
            if (this.mStatus == HEADSETPLUGIN_INITED) {
                return MiuiBluetoothDataBaseOperaterUtil.queryPluginSupport(getActivity(), PREFIX_SUPPORT_CLOUD_SHARE_DEVICEID + str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "deviceIdExist end");
        return false;
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (intent != null) {
            onFragmentResult(i, intent.getExtras());
        }
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
        super.onBluetoothStateChanged(i);
        if (i == 10) {
            updateProgressUi(false);
            updateDeviceNamePreferenceStatus(true);
            updateBroadcastAudioPreference(false);
            if (this.mDeleteDeviceZipRunnable != null) {
                new Thread(this.mDeleteDeviceZipRunnable).start();
            }
        }
        if (i == 12) {
            this.mGattProfile.getBondDevices();
            updateDeviceNamePreferenceStatus(true);
            updateBroadcastAudioPreference(true);
        }
        updateContent(i);
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        initService();
        this.mSearchIcon = new MiuiSearchDrawable(getActivity());
        this.mMiBleDeviceManager = MiBleDeviceManager.createManager(getActivity(), new MiBleDeviceManager.MiBleDeviceManagerListener() { // from class: com.android.settings.bluetooth.MiuiBluetoothSettings.3
            @Override // miui.bluetooth.ble.MiBleDeviceManager.MiBleDeviceManagerListener
            public void onDestroy() {
                MiuiBluetoothSettings.this.mMiBleDeviceManagerInited = false;
            }

            @Override // miui.bluetooth.ble.MiBleDeviceManager.MiBleDeviceManagerListener
            public void onInit(MiBleDeviceManager miBleDeviceManager) {
                MiuiBluetoothSettings miuiBluetoothSettings = MiuiBluetoothSettings.this;
                miuiBluetoothSettings.mMiBleDeviceManagerInited = true;
                GattProfile gattProfile = miuiBluetoothSettings.mGattProfile;
                if (gattProfile != null) {
                    gattProfile.getBondDevices();
                }
                MiuiBluetoothSettings miuiBluetoothSettings2 = MiuiBluetoothSettings.this;
                miuiBluetoothSettings2.updateContent(miuiBluetoothSettings2.mLocalAdapter.getBluetoothState());
            }
        });
        super.onCreate(bundle);
        this.mGattProfile = new GattProfile(getActivity().getApplicationContext(), this.mLocalAdapter, this.mLocalManager.getCachedDeviceManager(), this.mLocalManager.getProfileManager(), this.mMiBleDeviceManager);
        HandlerThread handlerThread = new HandlerThread("MiuiHeadsetHandler");
        this.mThread = handlerThread;
        handlerThread.start();
        this.mInfoHandler = new HeadsetInfoHandler(this.mThread.getLooper(), this);
        if (this.mLocalManager == null || isUiRestricted()) {
            return;
        }
        this.mLocalManager.getEventManager().registerCallback(this);
        this.mStatus = ((SettingsApplication) getActivity().getApplication()).mQigsawStarted;
        BluetoothCloudControlTools bluetoothCloudControlTools = new BluetoothCloudControlTools(getContext(), this.mStatus);
        this.mBluetoothCloudControlTools = bluetoothCloudControlTools;
        if (!bluetoothCloudControlTools.checkNewInfo()) {
            BluetoothUpdateTextPreference bluetoothUpdateTextPreference = (BluetoothUpdateTextPreference) findPreference("bluetooth_version_update");
            this.mBluetoothUpdateTextPreference = bluetoothUpdateTextPreference;
            if (bluetoothUpdateTextPreference != null) {
                getPreferenceScreen().removePreference(this.mBluetoothUpdateTextPreference);
            }
        }
        Preference findPreference = findPreference("bt_connect_help_flag");
        if (findPreference != null) {
            findPreference.setVisible(false);
        }
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        removeAllDevices();
        PreferenceGroup preferenceGroup = this.mPairedDevicesCategory;
        if (preferenceGroup != null) {
            preferenceGroup.removeAll();
        }
        PreferenceGroup preferenceGroup2 = this.mAvailableDevicesCategory;
        if (preferenceGroup2 != null) {
            preferenceGroup2.removeAll();
        }
        if (this.mLocalManager != null && !isUiRestricted()) {
            this.mLocalManager.getEventManager().unregisterCallback(this);
        }
        CheckBoxPreference checkBoxPreference = this.mBluetoothEnablePreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(null);
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            if (handler.hasMessages(0)) {
                this.mHandler.removeMessages(0);
            }
            this.mHandler = null;
        }
        try {
            HandlerThread handlerThread = this.mThread;
            if (handlerThread != null) {
                handlerThread.quit();
            }
            if (this.mInfoHandler != null) {
                this.mInfoHandler = null;
            }
            if (this.mBluetoothReceiver != null) {
                getActivity().unregisterReceiver(this.mBluetoothReceiver);
                this.mBluetoothReceiver = null;
            }
            Handler handler2 = this.mMainHandler;
            if (handler2 != null) {
                handler2.removeCallbacksAndMessages(null);
                this.mMainHandler = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "error " + e);
        }
        this.mRunnable = null;
        this.mMiBleDeviceManager.close();
        try {
            this.mGattProfile.cleanup();
            this.mGattProfile = null;
        } catch (Throwable th) {
            th.printStackTrace();
        }
        if (this.mConnection != null && this.mService != null) {
            getContext().unbindService(this.mConnection);
            this.mService = null;
        }
        try {
            if (this.mObserver != null) {
                getContext().getContentResolver().unregisterContentObserver(this.mObserver);
                this.mObserver = null;
            }
        } catch (Exception e2) {
            Log.e(TAG, "error " + e2);
        }
        super.onDestroy();
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mDevicePreferenceMap.get(cachedBluetoothDevice) == null && this.mLocalAdapter.getBluetoothState() == 12) {
            boolean z = cachedBluetoothDevice.getBondState() == 12;
            String str = TAG;
            Log.d(str, "onDeviceAdded, isBonded: " + z + ", mFliter: " + this.mFilter + ", mDeviceListGroup: " + this.mDeviceListGroup);
            if (this.mGattProfile.isBleDevice(cachedBluetoothDevice.getDevice())) {
                this.mGattProfile.getBondDevices();
                if (this.mMiBleDeviceManagerInited && this.mMiBleDeviceManager.getDeviceType(cachedBluetoothDevice.getDevice().getAddress()) == 0) {
                    BluetoothClass bluetoothClass = cachedBluetoothDevice.getDevice().getBluetoothClass();
                    if (bluetoothClass == null) {
                        Log.v(str, "BLE device without bt class found: " + cachedBluetoothDevice.getDevice().getAddress() + " " + cachedBluetoothDevice.getName());
                        return;
                    } else if (bluetoothClass.doesClassMatch(3)) {
                        Log.v(str, "HID over BLE device found: " + cachedBluetoothDevice.getDevice().getAddress() + " " + cachedBluetoothDevice.getName());
                    } else if (GattProfile.isBond(cachedBluetoothDevice.getDevice())) {
                        Log.v(str, "Bonded BLE device found: " + cachedBluetoothDevice.getDevice().getAddress() + " " + cachedBluetoothDevice.getName());
                    } else {
                        Log.v(str, "Unknown ble device found: " + cachedBluetoothDevice.getDevice().getAddress() + " " + cachedBluetoothDevice.getName() + " " + bluetoothClass.getMajorDeviceClass());
                        if (!z) {
                            checkDevicePreference(cachedBluetoothDevice);
                            return;
                        }
                        Log.d(str, "The unknown ble device is bonded!");
                    }
                }
                BluetoothDeviceFilter.Filter filter = this.mFilter;
                if (filter == BluetoothDeviceFilter.BONDED_DEVICE_FILTER) {
                    if (GattProfile.isBond(cachedBluetoothDevice.getDevice())) {
                        createDevicePreference(cachedBluetoothDevice);
                        return;
                    }
                } else if (filter == BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER && GattProfile.isBond(cachedBluetoothDevice.getDevice())) {
                    return;
                }
            }
            if (z) {
                super.onDeviceAdded(cachedBluetoothDevice);
            } else {
                checkDevicePreference(cachedBluetoothDevice);
            }
        }
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onDeviceBondStateChanged(cachedBluetoothDevice, i);
        this.mBondState = i;
        if (i != 12) {
            if (i != 10) {
                if (i == 11) {
                    BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) this.mPairedDevicesCategory.findPreference(cachedBluetoothDevice.getAddress());
                    if (bluetoothDevicePreference != null) {
                        this.mPairedDevicesCategory.removePreference(bluetoothDevicePreference);
                    }
                    if (this.mPairedDevicesCategory.getPreferenceCount() > 0 || getPreferenceScreen().findPreference(this.mPairedDevicesCategory.getKey()) == null) {
                        return;
                    }
                    getPreferenceScreen().removePreference(this.mPairedDevicesCategory);
                    return;
                }
                return;
            }
            try {
                internalSmoothScrollToPosition();
            } catch (Exception e) {
                Log.e(TAG, "internalSmoothScrollToPosition E: " + e.toString());
            }
            BluetoothDevicePreference remove = this.mDevicePreferenceMap.remove(cachedBluetoothDevice);
            Map<BluetoothDevice, DeviceSurpport> map = this.mCachedDeviceInfoList;
            if (map != null) {
                map.remove(cachedBluetoothDevice.getDevice());
            }
            if (remove == null) {
                remove = (BluetoothDevicePreference) this.mPairedDevicesCategory.findPreference(cachedBluetoothDevice.getAddress());
                Log.v(TAG, "the preference in mDevicePreferenceMap is null, find it from mPairedDevicesCategory");
            }
            if (remove != null) {
                this.mAvailableDevicesCategory.removePreference(remove);
                this.mPairedDevicesCategory.removePreference(remove);
            }
            if (this.mPairedDevicesCategory.getPreferenceCount() <= 0) {
                PreferenceGroup preferenceGroup = this.mAvailableDevicesCategory;
                if (preferenceGroup != null) {
                    ((MiuiBluetoothFilterCategory) preferenceGroup).setShowDivider(true);
                }
                getPreferenceScreen().removePreference(this.mPairedDevicesCategory);
            }
            setFilter(BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER);
            setDeviceListGroup(this.mAvailableDevicesCategory);
            onDeviceAdded(cachedBluetoothDevice);
            return;
        }
        try {
            internalSmoothScrollToPosition();
        } catch (Exception e2) {
            Log.e(TAG, "internalSmoothScrollToPosition E: " + e2.toString());
        }
        setDeviceListGroup(this.mPairedDevicesCategory);
        checkReCreateOnLineDevice(cachedBluetoothDevice);
        setDeviceListGroup(this.mAvailableDevicesCategory);
        onDeviceDeleted(cachedBluetoothDevice);
        PreferenceGroup preferenceGroup2 = this.mAvailableDevicesCategory;
        if (preferenceGroup2 != null) {
            BluetoothDevicePreference bluetoothDevicePreference2 = (BluetoothDevicePreference) preferenceGroup2.findPreference(cachedBluetoothDevice.getAddress());
            Log.d(TAG, "find preference: " + bluetoothDevicePreference2);
            if (bluetoothDevicePreference2 != null) {
                this.mAvailableDevicesCategory.removePreference(bluetoothDevicePreference2);
            }
        }
        PreferenceGroup preferenceGroup3 = this.mAvailableDevicesCategory;
        if (preferenceGroup3 != null) {
            ((MiuiBluetoothFilterCategory) preferenceGroup3).setShowDivider(false);
        }
        getPreferenceScreen().addPreference(this.mPairedDevicesCategory);
        setFilter(BluetoothDeviceFilter.BONDED_DEVICE_FILTER);
        setDeviceListGroup(this.mPairedDevicesCategory);
        createDevicePreference(cachedBluetoothDevice);
        PreferenceGroup preferenceGroup4 = this.mPairedDevicesCategory;
        if (preferenceGroup4 == null || !(preferenceGroup4 instanceof PreferenceCategory) || preferenceGroup4 == null) {
            return;
        }
        int preferenceCount = preferenceGroup4.getPreferenceCount();
        for (int i2 = 0; i2 < preferenceCount; i2++) {
            Preference preference = preferenceGroup4.getPreference(i2);
            if (preference != null && (preference instanceof MiuiOnLineBluetoothDevicePreference) && cachedBluetoothDevice.getAddress().equals(preference.getKey())) {
                Log.d(TAG, "repeated Preference: " + preference);
                this.mPairedDevicesCategory.removePreference(preference);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void onDevicePreferenceClick(BluetoothDevicePreference bluetoothDevicePreference) {
        if (this.mMiBleDeviceManagerInited && this.mMiBleDeviceManager != null) {
            BluetoothDevice device = bluetoothDevicePreference.getCachedDevice().getDevice();
            if (this.mMiBleDeviceManager.getDeviceType(device.getAddress()) != 0 && this.mMiBleDeviceManager.getDeviceType(device.getAddress()) != 999) {
                this.mTempBLEDevice = bluetoothDevicePreference.getCachedDevice();
                MiuiBTUtils.gotoBleProfile(getActivity(), device);
                Log.d(TAG, "onDevicePreferenceClick gotoBleProfile is ok ");
                return;
            }
        }
        super.onDevicePreferenceClick(bluetoothDevicePreference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onFragmentResult(int i, Bundle bundle) {
        super.onFragmentResult(i, bundle);
        if (bundle != null && i == 101) {
            try {
                ((MiuiBluetoothFilterCategory) this.mAvailableDevicesCategory).setCount(bundle.getInt(Tag.TagPhone.MARKED_COUNT));
            } catch (Exception e) {
                Log.w(TAG, "set count error: " + e);
            }
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onHearingAidAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothDeviceFilter.Filter filter = this.mFilter;
        PreferenceGroup preferenceGroup = this.mDeviceListGroup;
        setFilter(BluetoothDeviceFilter.BONDED_DEVICE_FILTER);
        setDeviceListGroup(this.mPairedDevicesCategory);
        createDevicePreference(cachedBluetoothDevice);
        setFilter(filter);
        setDeviceListGroup(preferenceGroup);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onHearingAidDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        PreferenceGroup preferenceGroup;
        PreferenceGroup preferenceGroup2;
        BluetoothDevicePreference remove = this.mDevicePreferenceMap.remove(cachedBluetoothDevice);
        if (remove == null && (preferenceGroup2 = this.mPairedDevicesCategory) != null) {
            remove = (BluetoothDevicePreference) preferenceGroup2.findPreference(cachedBluetoothDevice.getAddress());
            Log.v(TAG, "the preference in mDevicePreferenceMap is null, find it from mPairedDevicesCategory");
        }
        if (remove == null || (preferenceGroup = this.mPairedDevicesCategory) == null) {
            return;
        }
        preferenceGroup.removePreference(remove);
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1 && this.mBondState == 11) {
            Log.v(TAG, "Cannot start scanning since device is in bonding state.");
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        MiuiBluetoothSettings miuiBluetoothSettings;
        int i;
        LocalBluetoothAdapter localBluetoothAdapter = this.mLocalAdapter;
        if (localBluetoothAdapter == null || localBluetoothAdapter.isDiscovering()) {
            miuiBluetoothSettings = null;
            i = 0;
        } else {
            miuiBluetoothSettings = this;
            i = 101;
        }
        if (preference instanceof MiuiMiscBluetoothPreference) {
            this.mContinueDiscovery = true;
            MiuiUtils.startPreferencePanel(getActivity(), MiuiMiscBtListFragment.class.getName(), null, R.string.bluetooth_device_misc_title, null, miuiBluetoothSettings, i);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        if (i == 2) {
            try {
                internalSmoothScrollToPosition();
            } catch (Exception e) {
                Log.e(TAG, "internalSmoothScrollToPosition E: " + e.toString());
            }
        }
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mLocalAdapter.getBluetoothState() == 12) {
            updateDeviceNamePreferenceStatus(true);
            if (getActivity() != null) {
                invalidateOptionsMenu();
            }
        }
        renameMyDevice();
        this.mShowDevicesWithoutNamesOld = this.mShowDevicesWithoutNames;
        boolean z = SystemProperties.getBoolean("persist.bluetooth.showdeviceswithoutnames", false);
        this.mShowDevicesWithoutNames = z;
        PreferenceGroup preferenceGroup = this.mAvailableDevicesCategory;
        if (preferenceGroup != null) {
            ((MiuiBluetoothFilterCategory) preferenceGroup).setShowDevicesWithoutNames(z);
            if (this.mShowDevicesWithoutNamesOld != this.mShowDevicesWithoutNames) {
                ((MiuiBluetoothFilterCategory) this.mAvailableDevicesCategory).setCount(updateCachedRarelyUsedDevice());
            }
        }
        GattProfile gattProfile = this.mGattProfile;
        if (gattProfile != null) {
            gattProfile.getBondDevices();
        }
        CachedBluetoothDevice cachedBluetoothDevice = this.mTempBLEDevice;
        if (cachedBluetoothDevice != null) {
            cachedBluetoothDevice.refresh();
            this.mTempBLEDevice = null;
        }
        checkAndDeleteOnSavedDevice();
        if (this.mDeleteDeviceZipRunnable != null) {
            new Thread(this.mDeleteDeviceZipRunnable).start();
        }
        HeadsetInfoHandler headsetInfoHandler = this.mInfoHandler;
        if (headsetInfoHandler != null) {
            headsetInfoHandler.sendEmptyMessage(1);
        }
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.bluetooth.BluetoothCallback
    public void onScanningStateChanged(boolean z) {
        PreferenceGroup preferenceGroup;
        super.onScanningStateChanged(z);
        if (z || (preferenceGroup = this.mAvailableDevicesCategory) == null) {
            return;
        }
        ((MiuiBluetoothFilterCategory) preferenceGroup).setCount(updateCachedRarelyUsedDevice());
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mBondState = 10;
        PreferenceGroup preferenceGroup = this.mAvailableDevicesCategory;
        if (preferenceGroup != null) {
            ((MiuiBluetoothFilterCategory) preferenceGroup).updateRarelyUsedDevicePreference();
        }
        getActivity().registerReceiver(this.mBluetoothReceiver, new IntentFilter("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED"));
    }

    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.BluetoothSettings, com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void startScanning() {
        if (this.mAvailableDevicesCategory == null) {
            MiuiBluetoothFilterCategory miuiBluetoothFilterCategory = new MiuiBluetoothFilterCategory(getPrefContext());
            this.mAvailableDevicesCategory = miuiBluetoothFilterCategory;
            miuiBluetoothFilterCategory.setSelectable(false);
            ((MiuiBluetoothFilterCategory) this.mAvailableDevicesCategory).setOnSettingsClickListener(this.mRefreshListener);
            if (this.mLocalAdapter.getBluetoothState() != 12) {
                updateProgressUi(false);
            }
        }
        super.startScanning();
    }

    public int updateCachedRarelyUsedDevice() {
        int i = 0;
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mLocalManager.getCachedDeviceManager().getCachedDevicesCopy()) {
            if (MiuiBTUtils.isRarelyUsedBluetoothDevice(cachedBluetoothDevice) && cachedBluetoothDevice.getBondState() != 12 && !GattProfile.isBond(cachedBluetoothDevice.getDevice()) && !MiuiBTUtils.isNearByBluetoothDevice(cachedBluetoothDevice) && MiuiBTUtils.isVisibleDevice(this.mShowDevicesWithoutNames, cachedBluetoothDevice)) {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothSettings
    public void updateContent(int i) {
        renameMyDevice();
        super.updateContent(i);
        PreferenceGroup preferenceGroup = this.mAvailableDevicesCategory;
        if (preferenceGroup != null) {
            ((MiuiBluetoothFilterCategory) preferenceGroup).updateRarelyUsedDevicePreference();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void updateProgressUi(boolean z) {
        PreferenceGroup preferenceGroup = this.mAvailableDevicesCategory;
        if (preferenceGroup != null) {
            ((MiuiBluetoothFilterCategory) preferenceGroup).updateRefreshUI(z);
        }
    }
}
