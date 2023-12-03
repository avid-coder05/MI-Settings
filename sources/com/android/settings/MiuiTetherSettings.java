package com.android.settings;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.TetheringManager;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiClient;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SystemSettings$System;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.report.InternationalCompat;
import com.android.settings.wifi.EditTetherFragment;
import com.android.settings.wifi.QRCodeUtils;
import com.android.settings.wifi.WifiApDialog;
import com.android.settings.wifi.WifiApEnabler;
import com.android.settings.wifi.WifiTetherAutoOffController;
import com.android.settings.wifi.WifiTetherUseWifi6Controller;
import com.android.settingslib.TetherUtil;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.miui.enterprise.RestrictionsHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import miui.os.Build;
import miui.payment.PaymentManager;
import miui.util.ResourceMapper;
import miui.yellowpage.YellowPageContract;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiTetherSettings extends RestrictedSettingsFragment implements DialogInterface.OnClickListener, Preference.OnPreferenceChangeListener, DataSaverBackend.Listener {
    private static final int CONFIG_SUBTEXT = R.string.wifi_tether_configure_subtext;
    private boolean isClickUsb;
    private AlertDialog mAlertDialog;
    private boolean mBluetoothEnableForTether;
    private AtomicReference<BluetoothPan> mBluetoothPan;
    private String[] mBluetoothRegexs;
    private SwitchPreference mBluetoothTether;
    private ConnectivityManager mCm;
    private Preference mCreateNetwork;
    private DataSaverBackend mDataSaverBackend;
    private boolean mDataSaverEnabled;
    private Preference mDataSaverFooter;
    private Handler mDelayHandler;
    private Runnable mDelayStartTetherRunnable;
    private PreferenceCategory mDeviceManagementCategory;
    private WifiApDialog mDialog;
    private EthernetManager mEm;
    private CheckBoxPreference mEnableWifiAp;
    private EthernetListener mEthernetListener;
    private String mEthernetRegex;
    private SwitchPreference mEthernetTether;
    private Handler mHandler;
    private IntentFilter mIntentFilter;
    private boolean mMassStorageActive;
    private int mNumClients;
    private BluetoothProfile.ServiceListener mProfileServiceListener;
    private String[] mProvisionApp;
    private boolean mRestartWifiApAfterConfigChange;
    private String[] mSecurityType;
    private Preference mShareQrcode;
    private ValuePreference mShowDeivces;
    private WifiManager.SoftApCallback mSoftApCallback;
    private SoftApConfiguration mSoftApConfig;
    private boolean mSoftApEnabled;
    private OnStartTetheringCallback mStartTetheringCallback;
    private BroadcastReceiver mTetherChangeReceiver;
    private int mTetherChoice;
    private ValuePreference mTetherDataUsageLimit;
    private WeakReference<Activity> mTetherSettingsActivityRef;
    private TetheringEventCallback mTetheringEventCallback;
    private TetheringManager mTetheringManager;
    private boolean mTetheringProvisionNeeded;
    private UserManager mUm;
    private boolean mUnavailable;
    private boolean mUsbConnected;
    private String[] mUsbRegexs;
    private SwitchPreference mUsbTether;
    private WifiApEnabler mWifiApEnabler;
    private WifiManager mWifiManager;
    private String[] mWifiRegexs;
    private WifiTetherAutoOffController mWifiTetherAutoOffController;
    private WifiTetherUseWifi6Controller mWifiTetherUseWifi6Controller;
    private boolean tmpUsbConnected;

    /* loaded from: classes.dex */
    private static class BluetoothListener implements BluetoothProfile.ServiceListener {
        WeakReference<MiuiTetherSettings> mFragment;

        public BluetoothListener(MiuiTetherSettings miuiTetherSettings) {
            this.mFragment = new WeakReference<>(miuiTetherSettings);
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            MiuiTetherSettings miuiTetherSettings = this.mFragment.get();
            if (miuiTetherSettings != null) {
                BluetoothPan bluetoothPan = (BluetoothPan) bluetoothProfile;
                miuiTetherSettings.mBluetoothPan.set(bluetoothPan);
                BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                if (defaultAdapter != null && defaultAdapter.getState() == 12 && miuiTetherSettings.mBluetoothEnableForTether && bluetoothPan != null && miuiTetherSettings.isAdded()) {
                    if (miuiTetherSettings.mTetheringProvisionNeeded) {
                        Log.d("MiuiTetherSettings", "Ready to start Bluetooth Tethering!");
                        miuiTetherSettings.mTetheringProvisionNeeded = false;
                        miuiTetherSettings.startTethering(2);
                    } else {
                        bluetoothPan.setBluetoothTethering(true);
                    }
                    miuiTetherSettings.mBluetoothEnableForTether = false;
                    miuiTetherSettings.updateState();
                }
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            if (this.mFragment.get() != null) {
                this.mFragment.get().mBluetoothPan.set(null);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class DelayWeekHandler extends Handler {
        private final WeakReference<MiuiTetherSettings> mRef;

        public DelayWeekHandler(Looper looper, MiuiTetherSettings miuiTetherSettings) {
            super(looper);
            this.mRef = new WeakReference<>(miuiTetherSettings);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            MiuiTetherSettings miuiTetherSettings;
            WeakReference<MiuiTetherSettings> weakReference = this.mRef;
            if (weakReference == null || (miuiTetherSettings = weakReference.get()) == null || !miuiTetherSettings.isAdded() || miuiTetherSettings.isDetached()) {
                return;
            }
            int i = message.what;
            if (i == 1) {
                miuiTetherSettings.mUsbConnected = miuiTetherSettings.tmpUsbConnected;
                miuiTetherSettings.updateState();
                miuiTetherSettings.isClickUsb = false;
            } else if (i == 2) {
                Intent intent = (Intent) message.getData().getParcelable(PaymentManager.KEY_INTENT);
                if (intent != null) {
                    ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("availableArray");
                    ArrayList<String> stringArrayListExtra2 = intent.getStringArrayListExtra("tetherArray");
                    ArrayList<String> stringArrayListExtra3 = intent.getStringArrayListExtra("erroredArray");
                    miuiTetherSettings.updateState((String[]) stringArrayListExtra.toArray(new String[stringArrayListExtra.size()]), (String[]) stringArrayListExtra2.toArray(new String[stringArrayListExtra2.size()]), (String[]) stringArrayListExtra3.toArray(new String[stringArrayListExtra3.size()]));
                }
                miuiTetherSettings.isClickUsb = false;
            }
        }
    }

    /* loaded from: classes.dex */
    private final class EthernetListener implements EthernetManager.Listener {
        private EthernetListener() {
        }

        public void onAvailabilityChanged(String str, boolean z) {
            Handler handler = MiuiTetherSettings.this.mHandler;
            final MiuiTetherSettings miuiTetherSettings = MiuiTetherSettings.this;
            handler.post(new Runnable() { // from class: com.android.settings.MiuiTetherSettings$EthernetListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MiuiTetherSettings.access$1400(MiuiTetherSettings.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class OnStartTetheringCallback extends ConnectivityManager.OnStartTetheringCallback {
        final WeakReference<MiuiTetherSettings> mTetherSettings;

        OnStartTetheringCallback(MiuiTetherSettings miuiTetherSettings) {
            this.mTetherSettings = new WeakReference<>(miuiTetherSettings);
        }

        private void update() {
            MiuiTetherSettings miuiTetherSettings = this.mTetherSettings.get();
            if (miuiTetherSettings != null) {
                miuiTetherSettings.updateState();
            }
        }

        public void onTetheringFailed() {
            update();
        }

        public void onTetheringStarted() {
            update();
        }
    }

    /* loaded from: classes.dex */
    private class TetherChangeReceiver extends BroadcastReceiver {
        private TetherChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            BluetoothPan bluetoothPan;
            String action = intent.getAction();
            if (action.equals("android.net.conn.TETHER_STATE_CHANGED")) {
                ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("availableArray");
                ArrayList<String> stringArrayListExtra2 = intent.getStringArrayListExtra("tetherArray");
                ArrayList<String> stringArrayListExtra3 = intent.getStringArrayListExtra("erroredArray");
                if (!MiuiTetherSettings.this.isClickUsb) {
                    MiuiTetherSettings.this.updateState((String[]) stringArrayListExtra.toArray(new String[stringArrayListExtra.size()]), (String[]) stringArrayListExtra2.toArray(new String[stringArrayListExtra2.size()]), (String[]) stringArrayListExtra3.toArray(new String[stringArrayListExtra3.size()]));
                    return;
                }
                MiuiTetherSettings.this.clearDelayMsg();
                if (MiuiTetherSettings.this.mDelayHandler != null) {
                    Message obtainMessage = MiuiTetherSettings.this.mDelayHandler.obtainMessage(2);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(PaymentManager.KEY_INTENT, intent);
                    obtainMessage.setData(bundle);
                    MiuiTetherSettings.this.mDelayHandler.sendMessageDelayed(obtainMessage, 500L);
                }
            } else if (action.equals("android.intent.action.MEDIA_SHARED")) {
                MiuiTetherSettings.this.mMassStorageActive = true;
                MiuiTetherSettings.this.updateState();
            } else if (action.equals("android.intent.action.MEDIA_UNSHARED")) {
                MiuiTetherSettings.this.mMassStorageActive = false;
                MiuiTetherSettings.this.updateState();
            } else if (action.equals("android.hardware.usb.action.USB_STATE")) {
                if (!MiuiTetherSettings.this.isClickUsb) {
                    MiuiTetherSettings.this.mUsbConnected = intent.getBooleanExtra(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED, false);
                    MiuiTetherSettings.this.updateState();
                    return;
                }
                MiuiTetherSettings.this.tmpUsbConnected = intent.getBooleanExtra(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED, false);
                MiuiTetherSettings.this.clearDelayMsg();
                MiuiTetherSettings.this.mDelayHandler.sendEmptyMessageDelayed(1, 500L);
            } else if (!action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                if (action.equals("android.bluetooth.action.STATE_CHANGED")) {
                    MiuiTetherSettings.this.updateState();
                } else if (action.equals("android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED")) {
                    Log.d("MiuiTetherSettings", "update statue when receive bluetoothPan state changed!");
                    MiuiTetherSettings.this.updateState();
                }
            } else {
                if (MiuiTetherSettings.this.mBluetoothEnableForTether) {
                    int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                    if (intExtra == Integer.MIN_VALUE || intExtra == 10) {
                        MiuiTetherSettings.this.mBluetoothEnableForTether = false;
                    } else if (intExtra == 12 && (bluetoothPan = (BluetoothPan) MiuiTetherSettings.this.mBluetoothPan.get()) != null) {
                        if (MiuiTetherSettings.this.mTetheringProvisionNeeded) {
                            Log.d("MiuiTetherSettings", "Start Bluetooth Tethering!");
                            MiuiTetherSettings.this.mTetheringProvisionNeeded = false;
                            MiuiTetherSettings.this.startTethering(2);
                        } else {
                            bluetoothPan.setBluetoothTethering(true);
                        }
                        MiuiTetherSettings.this.mBluetoothEnableForTether = false;
                    }
                }
                MiuiTetherSettings.this.updateState();
            }
        }
    }

    /* loaded from: classes.dex */
    private final class TetheringEventCallback implements TetheringManager.TetheringEventCallback {
        private TetheringEventCallback() {
        }

        public void onTetheredInterfacesChanged(List<String> list) {
            if (MiuiTetherSettings.this.getActivity() == null) {
                Log.w("MiuiTetherSettings", "This activity may have been destroyed!!!");
            } else if (MiuiTetherSettings.this.mDelayHandler == null || !MiuiTetherSettings.this.mDelayHandler.hasMessagesOrCallbacks()) {
                MiuiTetherSettings.this.updateState();
            }
        }
    }

    public MiuiTetherSettings() {
        super("no_config_tethering");
        this.mSoftApConfig = null;
        this.mRestartWifiApAfterConfigChange = false;
        this.mNumClients = 0;
        this.mBluetoothPan = new AtomicReference<>();
        this.mTetheringProvisionNeeded = false;
        this.mTetherChoice = -1;
        this.isClickUsb = false;
        this.tmpUsbConnected = false;
        this.mSoftApCallback = new WifiManager.SoftApCallback() { // from class: com.android.settings.MiuiTetherSettings.1
            public void onConnectedClientsChanged(List<WifiClient> list) {
                MiuiTetherSettings.this.mNumClients = list.size();
                MiuiTetherSettings.this.manageShowConnectedDevices();
            }

            public void onStateChanged(int i, int i2) {
                if (i == 11) {
                    if (MiuiTetherSettings.this.mRestartWifiApAfterConfigChange) {
                        MiuiTetherSettings.this.mRestartWifiApAfterConfigChange = false;
                        Log.d("MiuiTetherSettings", "Restarting WifiAp due to prior config change.");
                        if (MiuiTetherSettings.this.isAdded()) {
                            if (MiuiTetherSettings.this.mDelayStartTetherRunnable == null) {
                                MiuiTetherSettings.this.mDelayStartTetherRunnable = new Runnable() { // from class: com.android.settings.MiuiTetherSettings.1.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        MiuiTetherSettings.this.startTethering(0);
                                    }
                                };
                            }
                            MiuiTetherSettings.this.getContext().getMainThreadHandler().postDelayed(MiuiTetherSettings.this.mDelayStartTetherRunnable, 500L);
                        } else {
                            Log.d("MiuiTetherSettings", "The fragment is not added, skip restart.");
                        }
                    }
                    MiuiTetherSettings.this.mSoftApEnabled = false;
                    MiuiTetherSettings.this.manageShowConnectedDevices();
                } else if (i == 13) {
                    MiuiTetherSettings.this.mSoftApEnabled = true;
                    MiuiTetherSettings.this.manageShowConnectedDevices();
                }
                MiuiTetherSettings.this.showOrHideShareQrcode(i == 13);
            }
        };
        this.mProfileServiceListener = new BluetoothListener(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void access$1400(MiuiTetherSettings miuiTetherSettings) {
        miuiTetherSettings.updateState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearDelayMsg() {
        Handler handler = this.mDelayHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private static String findIface(String[] strArr, String[] strArr2) {
        for (String str : strArr) {
            for (String str2 : strArr2) {
                if (str.matches(str2)) {
                    return str;
                }
            }
        }
        return null;
    }

    private boolean getMobileDataEnabled() {
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
        if (telephonyManager == null) {
            Log.d("MiuiTetherSettings", "getMobileDataEnabled()- remote exception retVal=false");
            return false;
        }
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        Log.d("MiuiTetherSettings", "getMobileDataEnabled()+ subId=" + defaultDataSubscriptionId);
        boolean isDataEnabled = telephonyManager.createForSubscriptionId(defaultDataSubscriptionId).isDataEnabled();
        Log.d("MiuiTetherSettings", "getMobileDataEnabled()- subId=" + defaultDataSubscriptionId + " retVal=" + isDataEnabled);
        return isDataEnabled;
    }

    private void initWifiTethering() {
        getActivity();
        WifiManager wifiManager = (WifiManager) getSystemService("wifi");
        this.mWifiManager = wifiManager;
        this.mSoftApConfig = wifiManager.getSoftApConfiguration();
        this.mSecurityType = getResources().getStringArray(MiuiUtils.getInstance().isWpa3SoftApSupport(getContext()) ? R.array.wifi_ap_security_with_sae : R.array.wifi_ap_security);
        if (this.mSoftApConfig != null) {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            boolean z = defaultSharedPreferences.getBoolean("wifi_tether_opened", false);
            boolean z2 = defaultSharedPreferences.getBoolean("wifi_ap_ssid_changed", false);
            String str = SystemProperties.get("ro.product.model");
            String str2 = SystemProperties.get(SystemSettings$System.RO_MARKET_NAME);
            String ssid = this.mSoftApConfig.getSsid();
            if (this.mWifiManager.getWifiApState() == 11 && !z && !z2 && ssid != null) {
                Log.d("MiuiTetherSettings", "Hotspot SSID will be reseted!");
                if (Build.IS_CM_CUSTOMIZATION_TEST) {
                    str = android.os.Build.DEVICE;
                } else if (!TextUtils.isEmpty(str2)) {
                    str = str2;
                } else if (TextUtils.isEmpty(str)) {
                    str = getResources().getString(R.string.wifi_tether_configure_ssid_default);
                }
                this.mSoftApConfig = new SoftApConfiguration.Builder(this.mSoftApConfig).setSsid(str).build();
                setWifiApConfiguration();
            }
            WifiApEnabler wifiApEnabler = this.mWifiApEnabler;
            if (wifiApEnabler != null) {
                wifiApEnabler.updateConfigSummary(this.mSoftApConfig);
            }
        }
    }

    private boolean isSecurityEqualsNone() {
        if (isAdded()) {
            return this.mSecurityType[WifiApDialog.getSecurityTypeIndex(this.mSoftApConfig)].equalsIgnoreCase(getString(R.string.wifi_security_none));
        }
        return true;
    }

    private boolean isShowConfirmDlg(Context context) {
        return Build.IS_CM_CUSTOMIZATION && !getMobileDataEnabled() && (TelephonyManager.from(context).getSimState() == 5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void manageShowConnectedDevices() {
        if (isAdded()) {
            if (!this.mSoftApEnabled) {
                this.mShowDeivces.setValue(getString(R.string.tether_settings_disabled));
                return;
            }
            ValuePreference valuePreference = this.mShowDeivces;
            Resources resources = getResources();
            int i = R.plurals.connected_devices_number;
            int i2 = this.mNumClients;
            valuePreference.setValue(resources.getQuantityString(i, i2, Integer.valueOf(i2)));
        }
    }

    private void setUsbTethering(boolean z) {
        this.mUsbTether.setChecked(false);
        this.mUsbTether.setEnabled(false);
        if (this.mTetheringManager.setUsbTethering(z) != 0) {
            this.mUsbTether.setSummary(R.string.usb_tethering_errored_subtext);
        }
    }

    private void showConfirmDlg(Context context) {
        new AlertDialog.Builder(context).setTitle(R.string.enable_mobile_data_when_opening_hotspot).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiTetherSettings.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiTetherSettings.this.startProvisioningIfNecessary(0);
                TelephonyManager.from(MiuiTetherSettings.this.getActivity()).enableDataConnectivity();
            }
        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiTetherSettings.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiTetherSettings.this.startProvisioningIfNecessary(0);
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showOrHideShareQrcode(boolean z) {
        if (this.mShareQrcode != null) {
            if (!z || isSecurityEqualsNone()) {
                getPreferenceScreen().removePreference(this.mShareQrcode);
            } else {
                getPreferenceScreen().addPreference(this.mShareQrcode);
            }
        }
    }

    private void showSharePasswordDialog() {
        Bitmap tetherQrcode = QRCodeUtils.getTetherQrcode(getActivity(), this.mSoftApConfig);
        if (tetherQrcode == null) {
            return;
        }
        final Window window = getActivity().getWindow();
        final WindowManager.LayoutParams attributes = window.getAttributes();
        final float f = attributes.screenBrightness;
        if (f < 0.8f) {
            attributes.screenBrightness = 0.8f;
            window.setAttributes(attributes);
        }
        window.addFlags(128);
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.wifi_share_qrcode, (ViewGroup) null);
        ((ImageView) inflate.findViewById(R.id.qrcode)).setImageBitmap(tetherQrcode);
        AlertDialog create = new AlertDialog.Builder(getActivity(), R.style.AlertDialog_Theme_DayNight).setTitle(R.string.wifi_share_qrcode_title).setView(inflate).setPositiveButton(R.string.wifi_share_qrcode_finish, (DialogInterface.OnClickListener) null).create();
        this.mAlertDialog = create;
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.MiuiTetherSettings.5
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                WindowManager.LayoutParams layoutParams = attributes;
                if (layoutParams.screenBrightness == 0.8f) {
                    layoutParams.screenBrightness = f;
                    window.setAttributes(layoutParams);
                }
                window.clearFlags(128);
            }
        });
        this.mAlertDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startProvisioningIfNecessary(int i) {
        this.mTetherChoice = i;
        if (TetherUtil.isProvisioningNeeded(getActivity())) {
            startTetheringProvisioning(this.mTetherChoice);
        } else if (i == 1 || i == 2 || i == 5) {
            startTethering();
        } else if (!TetherUtil.isProvisioningNeeded(getActivity())) {
            startTethering(i);
        } else {
            Intent intent = new Intent("android.intent.action.MAIN");
            String[] strArr = this.mProvisionApp;
            intent.setClassName(strArr[0], strArr[1]);
            startActivityForResult(intent, 0);
        }
    }

    private void startTethering() {
        int i = this.mTetherChoice;
        if (i == 1) {
            setUsbTethering(true);
        } else if (i != 2) {
            if (i != 5) {
                return;
            }
            this.mCm.startTethering(i, true, this.mStartTetheringCallback, this.mHandler);
        } else {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter.getState() == 10) {
                this.mBluetoothEnableForTether = true;
                defaultAdapter.enable();
                this.mBluetoothTether.setSummary(R.string.bluetooth_turning_on);
                this.mBluetoothTether.setEnabled(false);
                return;
            }
            BluetoothPan bluetoothPan = this.mBluetoothPan.get();
            if (bluetoothPan != null) {
                bluetoothPan.setBluetoothTethering(true);
            }
            this.mBluetoothTether.setSummary(R.string.bluetooth_tethering_available_subtext);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTethering(int i) {
        this.mCm.startTethering(i, true, new ConnectivityManager.OnStartTetheringCallback() { // from class: com.android.settings.MiuiTetherSettings.4
            public void onTetheringFailed() {
            }

            public void onTetheringStarted() {
            }
        });
    }

    private void startTetheringProvisioning(int i) {
        if (i == 2) {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter.getState() == 10) {
                this.mBluetoothEnableForTether = true;
                this.mTetheringProvisionNeeded = true;
                defaultAdapter.enable();
                this.mBluetoothTether.setSummary(R.string.bluetooth_turning_on);
                this.mBluetoothTether.setEnabled(false);
                return;
            }
        }
        startTethering(i);
    }

    private void updateBluetoothState(String[] strArr, String[] strArr2, String[] strArr3) {
        boolean z = false;
        for (String str : strArr3) {
            for (String str2 : this.mBluetoothRegexs) {
                if (str.matches(str2)) {
                    z = true;
                }
            }
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            return;
        }
        int state = defaultAdapter.getState();
        if (state == 13) {
            this.mBluetoothTether.setEnabled(false);
            this.mBluetoothTether.setSummary(R.string.bluetooth_turning_off);
        } else if (state == 11) {
            this.mBluetoothTether.setEnabled(false);
            this.mBluetoothTether.setSummary(R.string.bluetooth_turning_on);
        } else {
            BluetoothPan bluetoothPan = this.mBluetoothPan.get();
            if (state != 12 || bluetoothPan == null || !bluetoothPan.isTetheringOn()) {
                this.mBluetoothTether.setEnabled(true);
                this.mBluetoothTether.setChecked(false);
                this.mBluetoothTether.setSummary(R.string.bluetooth_tethering_available_subtext);
                return;
            }
            this.mBluetoothTether.setChecked(true);
            this.mBluetoothTether.setEnabled(true);
            int size = bluetoothPan.getConnectedDevices().size();
            if (size > 1) {
                this.mBluetoothTether.setSummary(getString(R.string.bluetooth_tethering_devices_connected_subtext, Integer.valueOf(size)));
            } else if (size == 1) {
                this.mBluetoothTether.setSummary(R.string.bluetooth_tethering_device_connected_subtext);
            } else if (z) {
                this.mBluetoothTether.setSummary(R.string.bluetooth_tethering_errored_subtext);
            } else {
                this.mBluetoothTether.setSummary(R.string.bluetooth_tethering_available_subtext);
            }
        }
    }

    private void updateEthernetState(String[] strArr, String[] strArr2) {
        EthernetManager ethernetManager;
        boolean z = false;
        for (String str : strArr) {
            if (str.matches(this.mEthernetRegex)) {
                z = true;
            }
        }
        boolean z2 = false;
        for (String str2 : strArr2) {
            if (str2.matches(this.mEthernetRegex)) {
                z2 = true;
            }
        }
        if (z2) {
            this.mEthernetTether.setEnabled(!this.mDataSaverEnabled);
            this.mEthernetTether.setChecked(true);
        } else if (z || ((ethernetManager = this.mEm) != null && ethernetManager.isAvailable())) {
            this.mEthernetTether.setEnabled(!this.mDataSaverEnabled);
            this.mEthernetTether.setChecked(false);
        } else {
            this.mEthernetTether.setEnabled(false);
            this.mEthernetTether.setChecked(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateState() {
        updateState(this.mTetheringManager.getTetherableIfaces(), this.mTetheringManager.getTetheredIfaces(), this.mTetheringManager.getTetheringErroredIfaces());
        updateStateForEnterprise();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateState(String[] strArr, String[] strArr2, String[] strArr3) {
        updateUsbState(strArr, strArr2, strArr3);
        updateBluetoothState(strArr, strArr2, strArr3);
        updateEthernetState(strArr, strArr2);
    }

    private void updateStateForEnterprise() {
        if (RestrictionsHelper.hasRestriction(getActivity(), "disallow_tether")) {
            this.mUsbTether.setEnabled(false);
            this.mBluetoothTether.setEnabled(false);
            this.mEthernetTether.setEnabled(false);
            Log.d("Enterprise", "Tether is restricted");
        }
    }

    private void updateUsbState(String[] strArr, String[] strArr2, String[] strArr3) {
        boolean z = this.mUsbConnected && !this.mMassStorageActive;
        int i = 0;
        for (String str : strArr) {
            for (String str2 : this.mUsbRegexs) {
                if (str.matches(str2) && i == 0) {
                    i = this.mTetheringManager.getLastTetherError(str);
                }
            }
        }
        boolean z2 = false;
        for (String str3 : strArr2) {
            for (String str4 : this.mUsbRegexs) {
                if (str3.matches(str4)) {
                    z2 = true;
                }
            }
        }
        boolean z3 = false;
        for (String str5 : strArr3) {
            for (String str6 : this.mUsbRegexs) {
                if (str5.matches(str6)) {
                    z3 = true;
                }
            }
        }
        if (z2) {
            this.mUsbTether.setSummary(R.string.usb_tethering_active_subtext);
            this.mUsbTether.setEnabled(true);
            this.mUsbTether.setChecked(true);
        } else if (z) {
            if (i == 0 || i == 16) {
                this.mUsbTether.setSummary(R.string.usb_tethering_available_subtext);
            } else {
                this.mUsbTether.setSummary(R.string.usb_tethering_errored_subtext);
            }
            this.mUsbTether.setEnabled(true);
            this.mUsbTether.setChecked(false);
        } else if (z3) {
            this.mUsbTether.setSummary(R.string.usb_tethering_errored_subtext);
            this.mUsbTether.setEnabled(false);
            this.mUsbTether.setChecked(false);
        } else if (this.mMassStorageActive) {
            this.mUsbTether.setSummary(R.string.usb_tethering_storage_active_subtext);
            this.mUsbTether.setEnabled(false);
            this.mUsbTether.setChecked(false);
        } else {
            this.mUsbTether.setSummary(R.string.usb_tethering_unavailable_subtext);
            this.mUsbTether.setEnabled(false);
            this.mUsbTether.setChecked(false);
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_tether;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 90;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiTetherSettings.class.getName();
    }

    @Override // com.android.settings.RestrictedSettingsFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 1 || intent == null) {
            return;
        }
        onFragmentResult(i, intent.getExtras());
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            SoftApConfiguration softApConfig = this.mDialog.getSoftApConfig();
            this.mSoftApConfig = softApConfig;
            if (softApConfig != null) {
                if (this.mWifiManager.getWifiApState() == 13) {
                    this.mCm.stopTethering(0);
                    this.mRestartWifiApAfterConfigChange = true;
                } else {
                    this.mWifiManager.setSoftApConfiguration(this.mSoftApConfig);
                }
                WifiApDialog.getSecurityTypeIndex(this.mSoftApConfig);
            }
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        InternationalCompat.trackReportEvent("setting_Connection_hotspot");
        addPreferencesFromResource(R.xml.miui_tether_prefs);
        this.mEnableWifiAp = (CheckBoxPreference) findPreference("enable_wifi_ap");
        this.mCreateNetwork = findPreference("wifi_ap_ssid_and_security");
        if (RegionUtils.IS_JP_KDDI) {
            this.mEnableWifiAp.setTitle(R.string.wifi_tether_checkbox_kddi_text);
            this.mCreateNetwork.setTitle(R.string.wifi_tether_configure_ap_kddi_text);
        }
        this.mShareQrcode = findPreference("tether_share_qrcode");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("tether_device_management");
        this.mDeviceManagementCategory = preferenceCategory;
        ValuePreference valuePreference = (ValuePreference) preferenceCategory.findPreference("show_connected_devices");
        this.mShowDeivces = valuePreference;
        valuePreference.setShowRightArrow(true);
        ValuePreference valuePreference2 = (ValuePreference) findPreference("tether_data_usage_limit");
        this.mTetherDataUsageLimit = valuePreference2;
        valuePreference2.setShowRightArrow(true);
        if (SystemProperties.getBoolean("ro.radio.noril", false)) {
            getPreferenceScreen().removePreference(this.mTetherDataUsageLimit);
        }
        DataSaverBackend dataSaverBackend = new DataSaverBackend(getContext());
        this.mDataSaverBackend = dataSaverBackend;
        this.mDataSaverEnabled = dataSaverBackend.isDataSaverEnabled();
        this.mDataSaverFooter = findPreference("disabled_on_data_saver");
        this.mDataSaverBackend.addListener(this);
        UserManager userManager = (UserManager) getSystemService("user");
        this.mUm = userManager;
        if (userManager.hasUserRestriction("no_config_tethering") || UserHandle.myUserId() != 0) {
            this.mUnavailable = true;
            setPreferenceScreen(new PreferenceScreen(getPrefContext(), null));
            return;
        }
        this.mHandler = new Handler();
        this.mTetheringManager = (TetheringManager) getSystemService("tethering");
        this.mCm = (ConnectivityManager) getSystemService("connectivity");
        this.mEm = (EthernetManager) getSystemService("ethernet");
        this.mWifiRegexs = this.mTetheringManager.getTetherableWifiRegexs();
        Preference findPreference = findPreference("tether_auto_disable");
        Preference findPreference2 = findPreference("tether_use_wifi6");
        boolean z = this.mWifiRegexs.length != 0;
        FragmentActivity activity = getActivity();
        this.mTetherSettingsActivityRef = new WeakReference<>(getActivity());
        if (!z || Utils.isMonkeyRunning()) {
            getPreferenceScreen().removePreference(this.mEnableWifiAp);
            getPreferenceScreen().removePreference(this.mCreateNetwork);
            getPreferenceScreen().removePreference(this.mShareQrcode);
            getPreferenceScreen().removePreference(this.mDeviceManagementCategory);
            getPreferenceScreen().removePreference(findPreference);
            getPreferenceScreen().removePreference(findPreference2);
        } else {
            this.mWifiTetherAutoOffController = new WifiTetherAutoOffController(activity, getLifecycle(), findPreference);
            if (activity.getResources().getBoolean(R.bool.config_show_softap_wifi6)) {
                this.mWifiTetherUseWifi6Controller = new WifiTetherUseWifi6Controller(this.mTetherSettingsActivityRef.get(), getLifecycle(), findPreference2);
            } else {
                getPreferenceScreen().removePreference(findPreference2);
            }
            this.mWifiApEnabler = new WifiApEnabler(activity, this.mDataSaverBackend, this.mEnableWifiAp);
            showOrHideShareQrcode(this.mEnableWifiAp.isChecked());
            manageShowConnectedDevices();
            IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
            this.mIntentFilter = intentFilter;
            intentFilter.addAction("android.net.conn.TETHER_STATE_CHANGED");
            this.mIntentFilter.addAction("android.intent.action.AIRPLANE_MODE");
            this.mIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            this.mIntentFilter.addAction(MiuiUtils.getInstance().getTetherDeviceChangedAction());
        }
        try {
            this.mProvisionApp = getResources().getStringArray(ResourceMapper.resolveReference(getActivity().getResources(), 285409290));
        } catch (Resources.NotFoundException unused) {
            Log.e("MiuiTetherSettings", "Resources not found!");
        }
        onDataSaverChanged(this.mDataSaverBackend.isDataSaverEnabled());
        SwitchPreference switchPreference = (SwitchPreference) findPreference("usb_tether_settings");
        this.mUsbTether = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("enable_bluetooth_tethering");
        this.mBluetoothTether = switchPreference2;
        switchPreference2.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference3 = (SwitchPreference) findPreference("enable_ethernet_tethering");
        this.mEthernetTether = switchPreference3;
        switchPreference3.setOnPreferenceChangeListener(this);
        this.mUsbRegexs = this.mTetheringManager.getTetherableUsbRegexs();
        this.mBluetoothRegexs = this.mTetheringManager.getTetherableBluetoothRegexs();
        String string = getContext().getResources().getString(17039958);
        this.mEthernetRegex = string;
        boolean z2 = this.mUsbRegexs.length != 0;
        boolean z3 = this.mBluetoothRegexs.length != 0;
        boolean z4 = !TextUtils.isEmpty(string);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.getProfileProxy(getActivity().getApplicationContext(), this.mProfileServiceListener, 5);
        }
        if (!z2 || Utils.isMonkeyRunning()) {
            getPreferenceScreen().removePreference(this.mUsbTether);
        }
        if (!z4) {
            getPreferenceScreen().removePreference(this.mEthernetTether);
        }
        boolean z5 = UserHandle.myUserId() != 0;
        if (!z3 || z5) {
            getPreferenceScreen().removePreference(this.mBluetoothTether);
            return;
        }
        BluetoothPan bluetoothPan = this.mBluetoothPan.get();
        if (bluetoothPan == null || !bluetoothPan.isTetheringOn()) {
            this.mBluetoothTether.setChecked(false);
        } else {
            this.mBluetoothTether.setChecked(true);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1) {
            WifiApDialog wifiApDialog = new WifiApDialog(getActivity(), this, this.mSoftApConfig);
            this.mDialog = wifiApDialog;
            return wifiApDialog;
        }
        return null;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        this.mDataSaverEnabled = z;
        StringBuilder sb = new StringBuilder();
        sb.append("set enableWifiApSwitch to ");
        sb.append(!this.mDataSaverEnabled);
        Log.d("MiuiTetherSettings", sb.toString());
        this.mEnableWifiAp.setEnabled(!this.mDataSaverEnabled);
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        SwitchPreference switchPreference = this.mUsbTether;
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener(null);
        }
        SwitchPreference switchPreference2 = this.mBluetoothTether;
        if (switchPreference2 != null) {
            switchPreference2.setOnPreferenceChangeListener(null);
        }
        SwitchPreference switchPreference3 = this.mEthernetTether;
        if (switchPreference3 != null) {
            switchPreference3.setOnPreferenceChangeListener(null);
        }
        this.mDataSaverBackend.remListener(this);
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mAlertDialog.dismiss();
        }
        this.mAlertDialog = null;
        super.onDestroy();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.closeProfileProxy(5, (BluetoothProfile) this.mBluetoothPan.get());
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onFragmentResult(int i, Bundle bundle) {
        if (i == 1) {
            this.mSoftApConfig = (SoftApConfiguration) bundle.getParcelable("config");
            setWifiApConfiguration();
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        RecyclerView listView = getListView();
        if (listView == null || !listView.isComputingLayout()) {
            this.isClickUsb = false;
            if ("usb_tether_settings".equals(preference.getKey())) {
                this.isClickUsb = true;
                clearDelayMsg();
                boolean booleanValue = ((Boolean) obj).booleanValue();
                if (booleanValue) {
                    startProvisioningIfNecessary(1);
                } else {
                    setUsbTethering(booleanValue);
                }
                return true;
            } else if ("enable_bluetooth_tethering".equals(preference.getKey())) {
                if (((Boolean) obj).booleanValue()) {
                    startProvisioningIfNecessary(2);
                } else {
                    String findIface = findIface(this.mTetheringManager.getTetheredIfaces(), this.mBluetoothRegexs);
                    boolean z = (findIface == null || this.mTetheringManager.untether(findIface) == 0) ? false : true;
                    BluetoothPan bluetoothPan = this.mBluetoothPan.get();
                    if (bluetoothPan != null) {
                        bluetoothPan.setBluetoothTethering(false);
                    }
                    if (z) {
                        this.mBluetoothTether.setSummary(R.string.bluetooth_tethering_errored_subtext);
                    } else {
                        this.mBluetoothTether.setSummary(R.string.bluetooth_tethering_available_subtext);
                    }
                }
                return true;
            } else if (!"enable_wifi_ap".equals(preference.getKey())) {
                if ("enable_ethernet_tethering".equals(preference.getKey())) {
                    if (((Boolean) obj).booleanValue()) {
                        startProvisioningIfNecessary(5);
                    } else {
                        this.mCm.stopTethering(5);
                    }
                }
                return false;
            } else {
                if (((Boolean) obj).booleanValue()) {
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
                    edit.putBoolean("wifi_tether_opened", true);
                    edit.commit();
                    if (isShowConfirmDlg(getActivity())) {
                        showConfirmDlg(getActivity());
                        return false;
                    }
                    startProvisioningIfNecessary(0);
                } else {
                    this.mCm.stopTethering(0);
                }
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        AlertDialog alertDialog;
        if (preference == this.mCreateNetwork) {
            startFragment(this, EditTetherFragment.class.getName(), 1, (Bundle) null, 0);
        } else if (preference == this.mShareQrcode && ((alertDialog = this.mAlertDialog) == null || !alertDialog.isShowing())) {
            showSharePasswordDialog();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        initWifiTethering();
        if (this.mUnavailable) {
            TextView textView = (TextView) getView().findViewById(16908292);
            if (textView != null) {
                textView.setText(R.string.tethering_settings_not_available);
                setEmptyView(textView);
                return;
            }
            return;
        }
        this.mStartTetheringCallback = new OnStartTetheringCallback(this);
        this.mTetheringEventCallback = new TetheringEventCallback();
        this.mTetheringManager.registerTetheringEventCallback(new HandlerExecutor(this.mHandler), this.mTetheringEventCallback);
        if (this.mWifiApEnabler != null) {
            this.mEnableWifiAp.setOnPreferenceChangeListener(this);
            this.mWifiApEnabler.resume();
            this.mWifiManager.registerSoftApCallback(new HandlerExecutor(this.mHandler), this.mSoftApCallback);
        }
        this.mMassStorageActive = "shared".equals(Environment.getExternalStorageState());
        this.mTetherChangeReceiver = new TetherChangeReceiver();
        FragmentActivity activity = getActivity();
        Intent registerReceiver = activity.registerReceiver(this.mTetherChangeReceiver, new IntentFilter("android.net.conn.TETHER_STATE_CHANGED"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_STATE");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.MEDIA_SHARED");
        intentFilter2.addAction("android.intent.action.MEDIA_UNSHARED");
        intentFilter2.addDataScheme("file");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter2);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter3.addAction("android.bluetooth.action.STATE_CHANGED");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter3);
        IntentFilter intentFilter4 = new IntentFilter();
        intentFilter4.addAction("android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter4);
        if (registerReceiver != null) {
            this.mTetherChangeReceiver.onReceive(activity, registerReceiver);
        }
        this.mDelayHandler = new DelayWeekHandler(Looper.getMainLooper(), this);
        EthernetListener ethernetListener = new EthernetListener();
        this.mEthernetListener = ethernetListener;
        EthernetManager ethernetManager = this.mEm;
        if (ethernetManager != null) {
            ethernetManager.addListener(ethernetListener);
        }
        updateState();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.mUnavailable) {
            return;
        }
        if (this.mWifiApEnabler != null) {
            this.mEnableWifiAp.setOnPreferenceChangeListener(null);
            this.mWifiApEnabler.pause();
            this.mWifiManager.unregisterSoftApCallback(this.mSoftApCallback);
        }
        getActivity().unregisterReceiver(this.mTetherChangeReceiver);
        this.mTetheringManager.unregisterTetheringEventCallback(this.mTetheringEventCallback);
        EthernetManager ethernetManager = this.mEm;
        if (ethernetManager != null) {
            ethernetManager.removeListener(this.mEthernetListener);
        }
        this.mTetherChangeReceiver = null;
        this.mTetheringEventCallback = null;
        this.mEthernetListener = null;
        getContext().getMainThreadHandler().removeCallbacks(this.mDelayStartTetherRunnable);
        clearDelayMsg();
    }

    public void setWifiApConfiguration() {
        if (this.mSoftApConfig != null) {
            if (this.mWifiManager.getWifiApState() == 13) {
                this.mCm.stopTethering(0);
                this.mRestartWifiApAfterConfigChange = true;
            }
            this.mWifiManager.setSoftApConfiguration(this.mSoftApConfig);
        }
    }
}
