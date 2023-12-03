package com.android.settings.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchIndexableData;
import android.provider.Settings;
import android.text.BidiFormatter;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.LinkifyUtils;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.bluetooth.BluetoothDevicePreference;
import com.android.settings.location.BluetoothScanningFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.android.settingslib.utils.ThreadUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import miui.bluetooth.ble.MiBleDeviceManager;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes.dex */
public class BluetoothSettings extends DeviceListPreferenceFragment {
    private final int SOURCE_CODEC_TYPE_LHDCV1;
    private final int SOURCE_CODEC_TYPE_LHDCV2;
    private final int SOURCE_CODEC_TYPE_LHDCV3;
    private boolean mAACDialogCreated;
    private BluetoothDevicePreference.AudioShareJumpPage mAudioShareJumpPage;
    protected PreferenceGroup mAvailableDevicesCategory;
    private boolean mAvailableDevicesCategoryIsPresent;
    private BluetoothA2dp mBluetoothA2dp;
    private final Object mBluetoothA2dpLock;
    private BroadcastReceiver mBluetoothA2dpReceiver;
    CheckBoxPreference mBluetoothEnablePreference;
    protected MiuiBluetoothEnabler mBluetoothEnabler;
    private LocalBtA2dpServiceListener mBtA2dpServiceListener;
    private final View.OnClickListener mDeviceProfilesListener;
    private AlertDialog mDialog;
    private boolean mDialogCreatedForConnected;
    protected GattProfile mGattProfile;
    protected Handler mHandler;
    private boolean mInitialScanStarted;
    private boolean mInitiateDiscoverable;
    private final IntentFilter mIntentFilter;
    protected MiBleDeviceManager mMiBleDeviceManager;
    protected PreferenceGroup mPairedDevicesCategory;
    private final BroadcastReceiver mReceiver;
    protected CachedBluetoothDevice mTempBLEDevice;
    private static final HashSet<String> operatorsPartProducts = new HashSet<String>() { // from class: com.android.settings.bluetooth.BluetoothSettings.1
        {
            add(",23415");
            add(",21401");
            add(",26202");
        }
    };
    private static final HashSet<String> operatorsAllProducts = new HashSet<String>() { // from class: com.android.settings.bluetooth.BluetoothSettings.2
        {
            add(",26806");
        }
    };
    private static View mSettingsDialogView = null;
    public static final String[] SUPPORT_DATASYNC_GLOBAL_HEADSET_DEVICEID = {"0201010001"};
    public static final String[] SUPPORT_DATASYNC_CN_HEADSET_DEVICEID = {"0201010000"};
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.bluetooth.BluetoothSettings.9
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            Resources resources = context.getResources();
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            int i = R.string.bluetooth_settings;
            searchIndexableRaw.title = resources.getString(i);
            searchIndexableRaw.screenTitle = resources.getString(i);
            ((SearchIndexableData) searchIndexableRaw).key = "main_toggle_bluetooth";
            arrayList.add(searchIndexableRaw);
            LocalBluetoothManager localBtManager = Utils.getLocalBtManager(context);
            if (localBtManager != null) {
                for (BluetoothDevice bluetoothDevice : localBtManager.getBluetoothAdapter().getBondedDevices()) {
                    SearchIndexableRaw searchIndexableRaw2 = new SearchIndexableRaw(context);
                    searchIndexableRaw2.title = bluetoothDevice.getName();
                    searchIndexableRaw2.screenTitle = resources.getString(R.string.bluetooth_settings);
                    ((SearchIndexableData) searchIndexableRaw2).enabled = z;
                    arrayList.add(searchIndexableRaw2);
                }
            }
            return arrayList;
        }
    };

    /* renamed from: com.android.settings.bluetooth.BluetoothSettings$7  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass7 implements LinkifyUtils.OnClickListener {
        final /* synthetic */ BluetoothSettings this$0;

        @Override // com.android.settings.LinkifyUtils.OnClickListener
        public void onClick() {
            ((SettingsActivity) this.this$0.getActivity()).startPreferencePanel(this.this$0, BluetoothScanningFragment.class.getName(), null, R.string.location_scanning_screen_title, null, null, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class LocalBtA2dpServiceListener implements BluetoothProfile.ServiceListener {
        private WeakReference<BluetoothSettings> mOuterRef;

        public LocalBtA2dpServiceListener(BluetoothSettings bluetoothSettings) {
            this.mOuterRef = new WeakReference<>(bluetoothSettings);
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            Log.d("BluetoothSettings", "onServiceConnected()");
            BluetoothSettings bluetoothSettings = this.mOuterRef.get();
            if (bluetoothSettings != null) {
                bluetoothSettings.handleServiceConnected((BluetoothA2dp) bluetoothProfile);
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            Log.d("BluetoothSettings", "onServiceDisconnected()");
            BluetoothSettings bluetoothSettings = this.mOuterRef.get();
            if (bluetoothSettings != null) {
                bluetoothSettings.closeProfileProxy();
            }
        }
    }

    public BluetoothSettings() {
        super("no_config_bluetooth");
        this.SOURCE_CODEC_TYPE_LHDCV2 = 9;
        this.SOURCE_CODEC_TYPE_LHDCV3 = 10;
        this.SOURCE_CODEC_TYPE_LHDCV1 = 11;
        this.mBluetoothA2dpLock = new Object();
        this.mAACDialogCreated = true;
        this.mDialogCreatedForConnected = true;
        this.mBtA2dpServiceListener = null;
        this.mHandler = new Handler() { // from class: com.android.settings.bluetooth.BluetoothSettings.3
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what == 0) {
                    BluetoothSettings.this.updateCodecIconForLc3();
                    BluetoothSettings.this.updateCodecIcon();
                }
            }
        };
        this.mAudioShareJumpPage = new BluetoothDevicePreference.AudioShareJumpPage() { // from class: com.android.settings.bluetooth.BluetoothSettings.4
            @Override // com.android.settings.bluetooth.BluetoothDevicePreference.AudioShareJumpPage
            public void onCallBack(BluetoothDevice bluetoothDevice) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("device", bluetoothDevice);
                MiuiUtils.startPreferencePanel(BluetoothSettings.this.getActivity(), DeviceProfilesSettings.class.getName(), bundle, R.string.bluetooth_device_advanced_title, null, null, 0);
            }
        };
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.BluetoothSettings.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                action.equals("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED");
                if (intExtra == 12) {
                    BluetoothSettings.this.mInitiateDiscoverable = true;
                }
            }
        };
        this.mDeviceProfilesListener = new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothSettings.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!(view.getTag() instanceof CachedBluetoothDevice)) {
                    Log.w("BluetoothSettings", "onClick() called for other View: " + view);
                    return;
                }
                CachedBluetoothDevice cachedBluetoothDevice = (CachedBluetoothDevice) view.getTag();
                Bundle bundle = new Bundle();
                bundle.putParcelable("device", cachedBluetoothDevice.getDevice());
                HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_TAP_LIGHT);
                BluetoothDevice device = cachedBluetoothDevice.getDevice();
                if (BluetoothSettings.this.mMiBleDeviceManager.getDeviceType(device.getAddress()) != 0) {
                    GattProfile gattProfile = BluetoothSettings.this.mGattProfile;
                    if (GattProfile.isBond(cachedBluetoothDevice.getDevice())) {
                        Log.d("BluetoothSettings", "mibandSupportHid gotoBleProfile success");
                        BluetoothSettings bluetoothSettings = BluetoothSettings.this;
                        bluetoothSettings.mTempBLEDevice = cachedBluetoothDevice;
                        MiuiBTUtils.gotoBleProfile(bluetoothSettings.getActivity(), device);
                        return;
                    }
                }
                if (BluetoothSettings.this.checkStartMiuiHeadset(cachedBluetoothDevice)) {
                    return;
                }
                MiuiUtils.startPreferencePanel(BluetoothSettings.this.getActivity(), DeviceProfilesSettings.class.getName(), bundle, R.string.bluetooth_device_advanced_title, null, null, 0);
            }
        };
        this.mBluetoothA2dpReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.BluetoothSettings.11
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Log.d("BluetoothSettings", "mBluetoothA2dpReceiver.onReceive() intent=" + intent);
                String action = intent.getAction();
                if ("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED".equals(action)) {
                    Log.d("BluetoothSettings", "Received BluetoothCodecStatus=" + ((BluetoothCodecStatus) intent.getParcelableExtra("android.bluetooth.extra.CODEC_STATUS")));
                    BluetoothSettings.this.updateCodecIcon();
                } else if ("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED".equals(action)) {
                    int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    Log.d("BluetoothSettings", "Received CONNECTION_STATE_CHANGED state=" + intExtra + " device=" + bluetoothDevice);
                    if (intExtra == 2) {
                        BluetoothSettings.this.updateCodecIcon();
                    } else if (intExtra == 0) {
                        BluetoothSettings.this.updateCodecIcon(bluetoothDevice);
                    }
                } else if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(action) && "Wireless transmission".equals((String) BluetoothSettings.this.getIntent().getExtra("from", "null"))) {
                    int intExtra2 = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                    BluetoothDevice bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    if (bluetoothDevice2 == null || intExtra2 != 2) {
                        return;
                    }
                    Intent intent2 = new Intent();
                    intent2.putExtra("Bluetooth device name", bluetoothDevice2.getName());
                    BluetoothSettings.this.getActivity().setResult(-1, intent2);
                    BluetoothSettings.this.getActivity().finish();
                } else if ("android.bluetooth.action.LEAUDIO_CONNECTION_STATE_CHANGED".equals(action)) {
                    try {
                        int intExtra3 = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                        BluetoothDevice bluetoothDevice3 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                        Log.i("BluetoothSettings", " device is " + bluetoothDevice3.getAddress() + " , and state is " + intExtra3);
                        if (intExtra3 == 2) {
                            BluetoothSettings.this.updateLeAudioCodecIcon(bluetoothDevice3);
                        } else if (intExtra3 == 0 && !BluetoothSettings.this.twoLeNotAllDisconnected(bluetoothDevice3)) {
                            BluetoothSettings.this.updateCodecIcon();
                        }
                    } catch (Exception e) {
                        Log.i("BluetoothSettings", " ACTION_LEAUDIO_CONNECTION_STATE_CHANGED failed " + e);
                    }
                }
            }
        };
        this.mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeProfileProxy() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            Log.d("BluetoothSettings", "closeProfileProxy()");
            defaultAdapter.closeProfileProxy(2, this.mBluetoothA2dp);
            synchronized (this.mBluetoothA2dpLock) {
                this.mBluetoothA2dp = null;
            }
        }
        this.mBtA2dpServiceListener = null;
        Handler handler = this.mHandler;
        if (handler == null || !handler.hasMessages(0)) {
            return;
        }
        this.mHandler.removeMessages(0);
    }

    private void createDialog(final CachedBluetoothDevice cachedBluetoothDevice, String str) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothSettings.10
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (BluetoothSettings.this.mDialog != null) {
                    Log.d("BluetoothSettings", " isRemember: " + BluetoothSettings.this.mDialog.isChecked());
                    CachedBluetoothDevice cachedBluetoothDevice2 = cachedBluetoothDevice;
                    cachedBluetoothDevice2.setDialogChoice(cachedBluetoothDevice2.getDevice().getAddress(), BluetoothSettings.this.mDialog.isChecked() ? 1 : 0);
                }
                if (i == -2 && !BluetoothSettings.this.checkStartMiuiHeadset(cachedBluetoothDevice)) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("device", cachedBluetoothDevice.getDevice());
                    MiuiUtils.startPreferencePanel(BluetoothSettings.this.getActivity(), DeviceProfilesSettings.class.getName(), bundle, R.string.bluetooth_device_advanced_title, null, null, 0);
                }
            }
        };
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String address = cachedBluetoothDevice.getDevice().getAddress();
        if ("LHDC_V3".equals(str) || "LHDC_V2".equals(str) || "LHDC_V1".equals(str)) {
            builder.setTitle(R.string.bt_lhdc_dialog_title);
            builder.setMessage(R.string.bt_lhdc_dialog_summary);
        } else if ("LDAC".equals(str)) {
            builder.setTitle(R.string.bt_ldac_dialog_title);
            if (!cachedBluetoothDevice.isSupportedCodec(address, "LHDC_V3") && !cachedBluetoothDevice.isSupportedCodec(address, "LHDC_V2") && !cachedBluetoothDevice.isSupportedCodec(address, "LHDC_V1")) {
                builder.setMessage(R.string.bt_ldac_dialog_summary);
            }
        } else if ("AAC".equals(str)) {
            builder.setTitle(R.string.bt_aac_dialog_title);
            if ((!cachedBluetoothDevice.isSupportedCodec(address, "LHDC_V3") && !cachedBluetoothDevice.isSupportedCodec(address, "LHDC_V2") && !cachedBluetoothDevice.isSupportedCodec(address, "LHDC_V1")) || cachedBluetoothDevice.isSupportedCodec(address, "LDAC")) {
                builder.setMessage(R.string.bt_aac_dialog_summary);
            }
        }
        builder.setCancelable(true);
        builder.setPositiveButton(17039370, onClickListener);
        builder.setNegativeButton(R.string.bt_dialog_settings, onClickListener);
        builder.setCheckBox(false, activity.getText(R.string.bt_dialog_remember_choice));
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    private void getProfileProxy() {
        if (this.mBtA2dpServiceListener == null) {
            this.mBtA2dpServiceListener = new LocalBtA2dpServiceListener(this);
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            return;
        }
        Log.d("BluetoothSettings", "getProfileProxy()");
        defaultAdapter.getProfileProxy(getActivity().getApplicationContext(), this.mBtA2dpServiceListener, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleServiceConnected(BluetoothA2dp bluetoothA2dp) {
        synchronized (this.mBluetoothA2dpLock) {
            this.mBluetoothA2dp = bluetoothA2dp;
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            if (handler.hasMessages(0)) {
                this.mHandler.removeMessages(0);
            }
            this.mHandler.sendEmptyMessageDelayed(0, 500L);
        }
    }

    private boolean isCodecTopAACDevice(BluetoothCodecStatus bluetoothCodecStatus) {
        boolean z = false;
        boolean z2 = false;
        for (BluetoothCodecConfig bluetoothCodecConfig : bluetoothCodecStatus.getCodecsSelectableCapabilities()) {
            Log.d("BluetoothSettings", "A2DP Codec Selectable Capability: " + bluetoothCodecConfig.toString());
            int codecType = bluetoothCodecConfig.getCodecType();
            if (codecType == 10 || codecType == 9 || codecType == 11 || codecType == 100 || codecType == 101 || codecType == 4 || codecType == 3 || codecType == 2) {
                return false;
            }
            if (codecType == 1) {
                z = true;
            } else if (codecType == 0) {
                z2 = true;
            }
        }
        return z && z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDeviceIdSupportShow(String str) {
        if (TextUtils.isEmpty(str)) {
            Log.d("BluetoothSettings", "deviceid from database is null");
            return false;
        }
        String[] strArr = Build.IS_INTERNATIONAL_BUILD ? SUPPORT_DATASYNC_GLOBAL_HEADSET_DEVICEID : SUPPORT_DATASYNC_CN_HEADSET_DEVICEID;
        if (strArr == null || strArr.length <= 0) {
            Log.d("BluetoothSettings", "support deviceId is empty");
        } else {
            for (String str2 : strArr) {
                if (str.equals(str2)) {
                    return true;
                }
            }
        }
        return isCloudSharedDeviceId(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:18:0x007b  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00e7 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean twoLeNotAllDisconnected(android.bluetooth.BluetoothDevice r9) {
        /*
            r8 = this;
            java.lang.String r0 = "00:00:00:00:00:00"
            if (r9 == 0) goto L9
            java.lang.String r9 = r9.getAddress()
            goto La
        L9:
            r9 = r0
        La:
            androidx.fragment.app.FragmentActivity r8 = r8.getActivity()
            android.content.ContentResolver r1 = r8.getContentResolver()
            java.lang.String r2 = "three_mac_for_ble_f"
            java.lang.String r1 = android.provider.Settings.Global.getString(r1, r2)
            r2 = 1
            if (r1 == 0) goto L44
            boolean r3 = r1.contains(r9)
            if (r3 == 0) goto L44
            int r3 = r1.indexOf(r9)
            int r4 = r1.length()
            int r5 = r3 + 36
            int r5 = r5 - r2
            r6 = 18
            if (r4 < r5) goto L3a
            int r3 = r3 + r6
            java.lang.String r3 = r1.substring(r3, r5)
            r7 = r3
            r3 = r0
            r0 = r7
            goto L45
        L3a:
            if (r3 < r6) goto L44
            int r4 = r3 + (-18)
            int r3 = r3 - r2
            java.lang.String r3 = r1.substring(r4, r3)
            goto L45
        L44:
            r3 = r0
        L45:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "value is  "
            r4.append(r5)
            r4.append(r1)
            java.lang.String r1 = " leMac is "
            r4.append(r1)
            r4.append(r9)
            java.lang.String r9 = " otherLeMac1 is "
            r4.append(r9)
            r4.append(r0)
            java.lang.String r9 = " otherLeMac2 is "
            r4.append(r9)
            r4.append(r3)
            java.lang.String r9 = r4.toString()
            java.lang.String r1 = "BluetoothSettings"
            android.util.Log.v(r1, r9)
            android.bluetooth.BluetoothAdapter r9 = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
            r4 = 0
            if (r9 == 0) goto Le7
            android.bluetooth.BluetoothDevice r0 = r9.getRemoteDevice(r0)
            android.bluetooth.BluetoothDevice r9 = r9.getRemoteDevice(r3)
            if (r0 != 0) goto L88
            if (r9 != 0) goto L88
            return r4
        L88:
            com.android.settingslib.bluetooth.LocalBluetoothManager r3 = com.android.settings.bluetooth.Utils.getLocalBtManager(r8)
            com.android.settingslib.bluetooth.CachedBluetoothDeviceManager r5 = r3.getCachedDeviceManager()
            com.android.settingslib.bluetooth.LocalBluetoothProfileManager r3 = r3.getProfileManager()
            com.android.settingslib.bluetooth.CachedBluetoothDevice r6 = r5.findDevice(r0)
            com.android.settingslib.bluetooth.CachedBluetoothDevice r5 = r5.findDevice(r9)
            if (r6 != 0) goto La8
            java.lang.String r6 = "mCachedDevice1 is null and new one "
            android.util.Log.i(r1, r6)
            com.android.settingslib.bluetooth.CachedBluetoothDevice r6 = new com.android.settingslib.bluetooth.CachedBluetoothDevice
            r6.<init>(r8, r3, r0)
        La8:
            if (r5 != 0) goto Lb4
            java.lang.String r0 = "mCachedDevice2 is null and new one "
            android.util.Log.i(r1, r0)
            com.android.settingslib.bluetooth.CachedBluetoothDevice r5 = new com.android.settingslib.bluetooth.CachedBluetoothDevice
            r5.<init>(r8, r3, r9)
        Lb4:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "mCachedDevice1  isLeAudioConnected "
            r8.append(r9)
            boolean r9 = r6.isConnectedLeAudioDevice()
            r8.append(r9)
            java.lang.String r9 = " mCachedDevice2  isLeAudioConnected "
            r8.append(r9)
            boolean r9 = r5.isConnectedLeAudioDevice()
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            android.util.Log.i(r1, r8)
            boolean r8 = r6.isConnectedLeAudioDevice()
            if (r8 != 0) goto Le6
            boolean r8 = r5.isConnectedLeAudioDevice()
            if (r8 == 0) goto Le5
            goto Le6
        Le5:
            r2 = r4
        Le6:
            return r2
        Le7:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.BluetoothSettings.twoLeNotAllDisconnected(android.bluetooth.BluetoothDevice):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x016a, code lost:
    
        if (r6.isSupportedCodec(r3.getAddress(), "AAC") != false) goto L76;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x016c, code lost:
    
        r6.setSupportedCodec(r3.getAddress(), "AAC", true);
        android.os.SystemProperties.set("persist.vendor.bt.a2dp.aac.whitelist", "null");
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x0180, code lost:
    
        if (r12.mAACDialogCreated != false) goto L76;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0182, code lost:
    
        r12.mAACDialogCreated = true;
        createDialog(r6, "AAC");
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x0191, code lost:
    
        if (r7.getCodecSpecific3() == 1) goto L119;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0193, code lost:
    
        r6.setSpecificCodecStatus("AAC", 0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateCodecIcon() {
        /*
            Method dump skipped, instructions count: 447
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.BluetoothSettings.updateCodecIcon():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCodecIcon(BluetoothDevice bluetoothDevice) {
        BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) this.mPairedDevicesCategory.findPreference(bluetoothDevice.getAddress());
        if (bluetoothDevicePreference != null) {
            bluetoothDevicePreference.updateCodecIcon((BluetoothA2dp) null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCodecIconForLc3() {
        try {
            Log.v("BluetoothSettings", "enter updateCodecIconForLc3 ");
            FragmentActivity activity = getActivity();
            if (LocalBluetoothProfileManager.isTbsProfileEnabled()) {
                LocalBluetoothManager localBtManager = Utils.getLocalBtManager(activity);
                LocalBluetoothProfileManager profileManager = localBtManager.getProfileManager();
                if (profileManager != null) {
                    List<BluetoothDevice> leAudioConnectedDevices = profileManager.getLeAudioConnectedDevices();
                    while (leAudioConnectedDevices != null && !leAudioConnectedDevices.isEmpty()) {
                        BluetoothDevice remove = leAudioConnectedDevices.remove(0);
                        if (remove != null) {
                            CachedBluetoothDevice findDevice = localBtManager.getCachedDeviceManager().findDevice(remove);
                            if (findDevice == null) {
                                findDevice = new CachedBluetoothDevice(activity, profileManager, remove);
                            }
                            String findBrAddress = findDevice.findBrAddress();
                            BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) this.mPairedDevicesCategory.findPreference(findBrAddress);
                            if (bluetoothDevicePreference == null) {
                                Log.v("BluetoothSettings", "preference is null ");
                                return;
                            } else if (findDevice.isConnectedLeAudioDevice()) {
                                Log.v("BluetoothSettings", "updateCodecIconForLeAudio for " + findBrAddress);
                                bluetoothDevicePreference.updateCodecIconForLeAudio();
                                return;
                            } else {
                                Log.v("BluetoothSettings", "updateCodecIconForNOLeAudio for " + findBrAddress);
                                bluetoothDevicePreference.updateCodecIconForNoLeAudio();
                            }
                        }
                    }
                    return;
                }
                return;
            }
            String string = Settings.Global.getString(activity.getContentResolver(), "three_mac_for_ble_f");
            Log.v("BluetoothSettings", "value is  " + string);
            if (string != null) {
                for (int i = 0; i < string.length() / 54; i++) {
                    String substring = string.substring(i, i + 17);
                    Log.i("BluetoothSettings", "brMac is " + substring);
                    synchronized (this.mBluetoothA2dpLock) {
                        BluetoothDevice activeDevice = this.mBluetoothA2dp.getActiveDevice();
                        if (activeDevice == null || !activeDevice.getAddress().equalsIgnoreCase(substring)) {
                            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (defaultAdapter != null && defaultAdapter.getRemoteDevice(substring) != null) {
                                LocalBluetoothManager localBtManager2 = Utils.getLocalBtManager(activity);
                                CachedBluetoothDeviceManager cachedDeviceManager = localBtManager2.getCachedDeviceManager();
                                int indexOf = string.indexOf(substring);
                                Log.i("BluetoothSettings", "startIndex is " + indexOf + " value is " + string);
                                String substring2 = string.substring(indexOf + 18, indexOf + 35);
                                String substring3 = string.substring(indexOf + 36, indexOf + 53);
                                if (substring2 != null && substring3 != null) {
                                    Log.i("BluetoothSettings", "leStr1 is " + substring2 + " leStr2 is " + substring3);
                                    BluetoothDevice remoteDevice = defaultAdapter.getRemoteDevice(substring2);
                                    BluetoothDevice remoteDevice2 = defaultAdapter.getRemoteDevice(substring3);
                                    if (remoteDevice != null || remoteDevice2 != null) {
                                        LocalBluetoothProfileManager profileManager2 = localBtManager2.getProfileManager();
                                        CachedBluetoothDevice findDevice2 = cachedDeviceManager.findDevice(remoteDevice);
                                        CachedBluetoothDevice findDevice3 = cachedDeviceManager.findDevice(remoteDevice2);
                                        if (findDevice2 == null) {
                                            Log.i("BluetoothSettings", "mCachedDevice1 is null and new one ");
                                            findDevice2 = new CachedBluetoothDevice(activity, profileManager2, remoteDevice);
                                        }
                                        if (findDevice3 == null) {
                                            Log.i("BluetoothSettings", "mCachedDevice2 is null and new one ");
                                            findDevice3 = new CachedBluetoothDevice(activity, profileManager2, remoteDevice2);
                                        }
                                        Log.i("BluetoothSettings", "mCachedDevice1  isLeAudioConnected " + findDevice2.isConnectedLeAudioDevice() + " mCachedDevice2  isLeAudioConnected " + findDevice3.isConnectedLeAudioDevice());
                                        BluetoothDevicePreference bluetoothDevicePreference2 = (BluetoothDevicePreference) this.mPairedDevicesCategory.findPreference(substring);
                                        if (bluetoothDevicePreference2 != null) {
                                            Log.v("BluetoothSettings", "enter updateCodecIconForLc3 ");
                                            if (!findDevice2.isConnectedLeAudioDevice() && !findDevice3.isConnectedLeAudioDevice()) {
                                                bluetoothDevicePreference2.updateCodecIconForNoLeAudio();
                                            }
                                            bluetoothDevicePreference2.updateCodecIconForLeAudio();
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.i("BluetoothSettings", "updateCodecIconForLc3 is active so return");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.i("BluetoothSettings", "updateCodecIconForLc3 Exception error is " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLeAudioCodecIcon(BluetoothDevice bluetoothDevice) {
        String substring;
        Log.v("BluetoothSettings", "enter updateLeAudioCodecIcon ");
        String str = "00:00:00:00:00:00";
        String address = bluetoothDevice != null ? bluetoothDevice.getAddress() : "00:00:00:00:00:00";
        FragmentActivity activity = getActivity();
        String string = Settings.Global.getString(activity.getContentResolver(), "three_mac_for_ble_f");
        Log.v("BluetoothSettings", "value is  " + string + " leMac is " + address);
        if (string != null && string.contains(address) && string.length() % 54 == 0 && string.length() >= 54) {
            int indexOf = string.indexOf(address);
            Log.v("BluetoothSettings", "startIndex is " + indexOf);
            int i = (indexOf / 18) % 18;
            if (i == 1) {
                substring = string.substring(indexOf - 18, indexOf - 1);
                Log.v("BluetoothSettings", "brMac come from le1 and address is " + substring);
            } else if (i == 2) {
                substring = string.substring(indexOf - 36, (indexOf - 18) - 1);
                Log.v("BluetoothSettings", "brMac come from le2 and address is " + substring);
            }
            str = substring;
        } else if (LocalBluetoothProfileManager.isTbsProfileEnabled()) {
            LocalBluetoothManager localBtManager = Utils.getLocalBtManager(activity);
            LocalBluetoothProfileManager profileManager = localBtManager.getProfileManager();
            CachedBluetoothDevice findDevice = localBtManager.getCachedDeviceManager().findDevice(bluetoothDevice);
            if (findDevice == null) {
                findDevice = new CachedBluetoothDevice(activity, profileManager, bluetoothDevice);
            }
            str = findDevice.findBrAddress();
            Log.v("BluetoothSettings", "brMac is " + str);
        }
        BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) this.mPairedDevicesCategory.findPreference(str);
        if (bluetoothDevicePreference != null) {
            bluetoothDevicePreference.updateCodecIconForLeAudio();
        }
    }

    private void updateOnSavedDevice() {
        final Context context = getContext();
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.bluetooth.BluetoothSettings.6
            /* JADX WARN: Removed duplicated region for block: B:75:0x00da A[SYNTHETIC] */
            /* JADX WARN: Removed duplicated region for block: B:80:0x0049 A[SYNTHETIC] */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void run() {
                /*
                    Method dump skipped, instructions count: 366
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.BluetoothSettings.AnonymousClass6.run():void");
            }
        });
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void addDeviceCategory(PreferenceGroup preferenceGroup, int i, BluetoothDeviceFilter.Filter filter, boolean z) {
        cacheRemoveAllPrefs(preferenceGroup);
        if ("available_devices_category".equals(preferenceGroup.getKey())) {
            preferenceGroup.setTitle(i);
        }
        setFilter(filter);
        setDeviceListGroup(preferenceGroup);
        if (z) {
            addCachedDevices();
        }
        preferenceGroup.setEnabled(true);
        removeCachedPrefs(preferenceGroup);
    }

    public void addNewOnSavedDevice(String str, String str2, String str3, int i, List<LocalBluetoothProfile> list) {
    }

    public boolean checkStartMiuiHeadset(CachedBluetoothDevice cachedBluetoothDevice) {
        return false;
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public String getDeviceListKey() {
        return null;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_bluetooth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BluetoothSettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 24;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return BluetoothSettings.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.bluetooth_settings;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void initDevicePreference(BluetoothDevicePreference bluetoothDevicePreference) {
        CachedBluetoothDevice cachedDevice = bluetoothDevicePreference.getCachedDevice();
        if (cachedDevice.getBondState() == 12 || (this.mMiBleDeviceManager.getDeviceType(cachedDevice.getDevice().getAddress()) != 0 && GattProfile.isBond(cachedDevice.getDevice()))) {
            bluetoothDevicePreference.setOnSettingsClickListener(this.mDeviceProfilesListener);
            bluetoothDevicePreference.setAudioShareJumpPage(this.mAudioShareJumpPage);
        }
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void initPreferencesFromPreferenceScreen() {
        Context prefContext = getPrefContext();
        PreferenceCategory preferenceCategory = new PreferenceCategory(prefContext);
        this.mPairedDevicesCategory = preferenceCategory;
        preferenceCategory.setLayoutResource(R.layout.preference_bt_category_paired);
        this.mPairedDevicesCategory.setKey("paired_devices");
        this.mPairedDevicesCategory.setOrder(1);
        getPreferenceScreen().addPreference(this.mPairedDevicesCategory);
        MiuiBluetoothFilterCategory miuiBluetoothFilterCategory = new MiuiBluetoothFilterCategory(prefContext);
        this.mAvailableDevicesCategory = miuiBluetoothFilterCategory;
        miuiBluetoothFilterCategory.setSelectable(false);
        this.mAvailableDevicesCategory.setOrder(2);
        if (this.mLocalAdapter.getBluetoothState() == 12) {
            getPreferenceScreen().addPreference(this.mAvailableDevicesCategory);
        }
        setHasOptionsMenu(true);
    }

    public boolean isCloudSharedDeviceId(String str) {
        return false;
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mInitialScanStarted = bundle != null ? bundle.getBoolean("scan_started", false) : false;
        this.mInitiateDiscoverable = bundle != null ? bundle.getBoolean("initiate_discoverable", false) : true;
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
        super.onBluetoothStateChanged(i);
        if (12 == i) {
            this.mInitiateDiscoverable = true;
            this.mDialogCreatedForConnected = false;
            this.mAACDialogCreated = false;
            getProfileProxy();
        } else if (10 == i) {
            this.mInitiateDiscoverable = false;
            closeProfileProxy();
        }
    }

    @Override // miuix.preference.PreferenceFragment, androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        getActivity();
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocalBluetoothAdapter bluetoothAdapter = this.mLocalManager.getBluetoothAdapter();
        this.mLocalAdapter = bluetoothAdapter;
        if (bluetoothAdapter != null) {
            bluetoothAdapter.stopScanning();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (this.mLocalAdapter == null || isUiRestricted()) {
            return;
        }
        boolean z = this.mLocalAdapter.getBluetoothState() == 12;
        boolean isDiscovering = this.mLocalAdapter.isDiscovering();
        menu.add(0, 1, 0, isDiscovering ? R.string.bluetooth_searching_for_devices : R.string.bluetooth_search_for_devices).setEnabled(z && !isDiscovering).setShowAsAction(0);
        menu.add(0, 2, 0, R.string.bluetooth_rename_device).setEnabled(z).setShowAsAction(0);
        menu.add(0, 3, 0, R.string.bluetooth_show_received_files).setShowAsAction(0);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDialog = null;
        }
        if (this.mBtA2dpServiceListener != null) {
            this.mBtA2dpServiceListener = null;
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        this.mAACDialogCreated = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void onDevicePreferenceClick(BluetoothDevicePreference bluetoothDevicePreference) {
        this.mLocalAdapter.stopScanning();
        super.onDevicePreferenceClick(bluetoothDevicePreference);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            if (this.mLocalAdapter.getBluetoothState() == 12) {
                this.mMetricsFeatureProvider.action(getActivity(), 160, new Pair[0]);
                if (this.mLocalAdapter.isDiscovering()) {
                    this.mLocalAdapter.stopScanning();
                } else {
                    startScanning();
                }
            }
            return true;
        } else if (itemId == 2) {
            this.mMetricsFeatureProvider.action(getActivity(), 161, new Pair[0]);
            return true;
        } else if (itemId != 3) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            this.mMetricsFeatureProvider.action(getActivity(), 162, new Pair[0]);
            Intent intent = new Intent("android.btopp.intent.action.OPEN_RECEIVED_FILES");
            intent.setPackage("com.android.bluetooth");
            getActivity().sendBroadcast(intent);
            return true;
        }
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("scan_started", this.mInitialScanStarted);
        bundle.putBoolean("initiate_discoverable", this.mInitiateDiscoverable);
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.bluetooth.BluetoothCallback
    public void onScanningStateChanged(boolean z) {
        super.onScanningStateChanged(z);
        if (getActivity() != null) {
            invalidateOptionsMenu();
        }
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        MiuiBluetoothEnabler miuiBluetoothEnabler = this.mBluetoothEnabler;
        if (miuiBluetoothEnabler != null) {
            miuiBluetoothEnabler.resume();
        }
        super.onStart();
        this.mInitiateDiscoverable = true;
        if (isUiRestricted()) {
            setDeviceListGroup(getPreferenceScreen());
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(R.string.bluetooth_empty_list_user_restricted);
            }
            removeAllDevices();
            return;
        }
        getActivity().registerReceiver(this.mReceiver, this.mIntentFilter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED");
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.action.LEAUDIO_CONNECTION_STATE_CHANGED");
        getActivity().registerReceiver(this.mBluetoothA2dpReceiver, intentFilter);
        getProfileProxy();
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        MiuiBluetoothEnabler miuiBluetoothEnabler = this.mBluetoothEnabler;
        if (miuiBluetoothEnabler != null) {
            miuiBluetoothEnabler.pause();
        }
        if (isUiRestricted()) {
            return;
        }
        getActivity().unregisterReceiver(this.mReceiver);
        getActivity().unregisterReceiver(this.mBluetoothA2dpReceiver);
        closeProfileProxy();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDeviceListGroup(PreferenceGroup preferenceGroup) {
        this.mDeviceListGroup = preferenceGroup;
    }

    void setTextSpan(CharSequence charSequence, CharSequence charSequence2) {
        if (charSequence instanceof Spannable) {
            ((Spannable) charSequence).setSpan(new TextAppearanceSpan(getActivity(), 16973892), 0, charSequence2.length(), 33);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void startScanning() {
        if (isUiRestricted()) {
            return;
        }
        if (!this.mAvailableDevicesCategoryIsPresent) {
            getPreferenceScreen().addPreference(this.mAvailableDevicesCategory);
            this.mAvailableDevicesCategoryIsPresent = true;
        }
        PreferenceGroup preferenceGroup = this.mAvailableDevicesCategory;
        if (preferenceGroup != null) {
            preferenceGroup.setOrderingAsAdded(false);
            setFilter(BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER);
            setDeviceListGroup(this.mAvailableDevicesCategory);
            removeAllDevices();
        }
        this.mLocalManager.getCachedDeviceManager().clearNonBondedDevices();
        PreferenceGroup preferenceGroup2 = this.mAvailableDevicesCategory;
        if (preferenceGroup2 != null) {
            preferenceGroup2.removeAll();
        } else {
            Log.e("BluetoothSettings", "mAvailableDevicesCategory is null.");
        }
        this.mInitialScanStarted = true;
        this.mLocalAdapter.startScanning(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateContent(int i) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        switch (i) {
            case 10:
                isUiRestricted();
                break;
            case 11:
                this.mInitialScanStarted = false;
                break;
            case 12:
                this.mDevicePreferenceMap.clear();
                this.mTempDevicePreferenceMap.clear();
                if (!isUiRestricted()) {
                    if (!this.mInitialScanStarted) {
                        this.mLocalManager.getCachedDeviceManager().clearNonBondedDevices();
                        PreferenceGroup preferenceGroup = this.mAvailableDevicesCategory;
                        if (preferenceGroup != null) {
                            ((MiuiBluetoothFilterCategory) preferenceGroup).removeAll();
                        }
                    }
                    addDeviceCategory(this.mPairedDevicesCategory, R.string.bluetooth_preference_paired_devices, BluetoothDeviceFilter.BONDED_DEVICE_FILTER, true);
                    try {
                        updateOnSavedDevice();
                    } catch (Exception e) {
                        Log.d("BluetoothSettings", "updateOnSavedDevice throw Exception: " + e);
                    }
                    int preferenceCount = this.mPairedDevicesCategory.getPreferenceCount();
                    if (isUiRestricted() || preferenceCount <= 0) {
                        PreferenceGroup preferenceGroup2 = this.mAvailableDevicesCategory;
                        if (preferenceGroup2 != null) {
                            ((MiuiBluetoothFilterCategory) preferenceGroup2).setShowDivider(true);
                        }
                        if (preferenceScreen.findPreference("paired_devices") != null) {
                            preferenceScreen.removePreference(this.mPairedDevicesCategory);
                        }
                    } else if (preferenceScreen.findPreference("paired_devices") == null) {
                        preferenceScreen.addPreference(this.mPairedDevicesCategory);
                    }
                    addDeviceCategory(this.mAvailableDevicesCategory, R.string.bluetooth_preference_found_devices, BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER, this.mInitialScanStarted);
                    if (!this.mInitialScanStarted) {
                        startScanning();
                    }
                    BidiFormatter.getInstance(getResources().getConfiguration().getLocales().get(0));
                    getActivity().invalidateOptionsMenu();
                    if (this.mInitiateDiscoverable) {
                        LocalBluetoothManager localBluetoothManager = this.mLocalManager;
                        if (localBluetoothManager == null || localBluetoothManager.getForegroundActivity() == null) {
                            Log.d("BluetoothSettings", "set scan mode null");
                        } else {
                            Log.d("BluetoothSettings", "set scan mode connectable and discoverable");
                            this.mLocalAdapter.setScanMode(23);
                        }
                        this.mInitiateDiscoverable = false;
                        return;
                    }
                    return;
                }
                break;
        }
        setDeviceListGroup(preferenceScreen);
        removeAllDevices();
        this.mAvailableDevicesCategoryIsPresent = false;
        if (isUiRestricted()) {
            return;
        }
        invalidateOptionsMenu();
    }
}
