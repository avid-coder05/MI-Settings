package com.android.settings.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.widget.SeekBarPreference;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.HeadsetProfile;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.settingslib.bluetooth.MapProfile;
import com.android.settingslib.bluetooth.PanProfile;
import com.android.settingslib.bluetooth.PbapServerProfile;
import com.android.settingslib.miuisettings.preference.EditTextPreference;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import miui.os.Build;
import miui.provider.ExtraContacts;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.ProgressDialog;

/* loaded from: classes.dex */
public final class DeviceProfilesSettings extends MiuiSettingsPreferenceFragment implements CachedBluetoothDevice.Callback, Preference.OnPreferenceChangeListener {
    private AudioManager mAbsAudioManager;
    private String mAbsVolFeature;
    private AudioManager mAudioManager;
    private PreferenceGroup mAudioRepairContainer;
    private AlertDialog mAudioRepairDialog;
    private ProgressDialog mAudioRepairingDialog;
    private PreferenceGroup mAudioShareContainer;
    private int mAudioStreamMax;
    private PreferenceGroup mBleAudioCategory;
    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothHeadset mBluetoothHfp;
    private CachedBluetoothDevice mCachedDevice;
    private PreferenceGroup mCodecContainer;
    private DelayRunnable mDelayRunnable;
    private String mDeviceMacAddress;
    private EditTextPreference mDeviceNamePref;
    private AlertDialog mDialog;
    private AlertDialog mDisconnectDialog;
    private EditText mEtDeviceRename;
    private Handler mHandler;
    private InputMethodManager mInputManager;
    private LocalBluetoothManager mManager;
    private PreferenceGroup mProfileContainer;
    private boolean mProfileGroupIsRemoved;
    private LocalBluetoothProfileManager mProfileManager;
    private RenameEditTextPreference mRenameDeviceNamePref;
    private View mRootView;
    private final int SOURCE_CODEC_TYPE_LHDCV2 = 9;
    private final int SOURCE_CODEC_TYPE_LHDCV3 = 10;
    private final int SOURCE_CODEC_TYPE_LHDCV1 = 11;
    private final Object mBluetoothA2dpLock = new Object();
    private final Object mBluetoothHfpLock = new Object();
    private boolean mUpdatePrefForA2DPConnected = false;
    private boolean mLDACDevice = false;
    private boolean mLHDCV3Device = false;
    private boolean mLHDCV2Device = false;
    private boolean mLHDCV1Device = false;
    private boolean mAACDevice = false;
    private boolean mAADevice = false;
    private boolean mSBCLlDevice = false;
    private boolean mLC3Switching = false;
    private boolean mIsInAbsWhitelist = false;
    private boolean isSingleHeadsetConn = false;
    private boolean mIsBleAudioDevice = false;
    private final HashMap<LocalBluetoothProfile, CheckBoxPreference> mAutoConnectPrefs = new HashMap<>();
    private BluetoothProfile.ServiceListener mBluetoothA2dpServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.4
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            BluetoothCodecStatus codecStatus;
            Log.d("DeviceProfilesSettings", "onServiceConnected()");
            synchronized (DeviceProfilesSettings.this.mBluetoothA2dpLock) {
                DeviceProfilesSettings.this.mBluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
                if (FeatureParser.getBoolean("support_audio_share", false) && (((DeviceProfilesSettings.this.mBluetoothA2dp != null && DeviceProfilesSettings.this.mBluetoothA2dp.getActiveDevice() == null) || !DeviceProfilesSettings.this.mCachedDevice.isConnectedA2dpDevice() || DeviceProfilesSettings.this.mCachedDevice.isActiveDevice(2)) && DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_share_container") != null && DeviceProfilesSettings.this.mAudioShareContainer != null)) {
                    DeviceProfilesSettings.this.getPreferenceScreen().removePreference(DeviceProfilesSettings.this.mAudioShareContainer);
                }
                int i2 = -1;
                if (DeviceProfilesSettings.this.mBluetoothA2dp != null && (codecStatus = DeviceProfilesSettings.this.mBluetoothA2dp.getCodecStatus(DeviceProfilesSettings.this.mCachedDevice.getDevice())) != null) {
                    i2 = codecStatus.getCodecConfig().getCodecType();
                }
                BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                DeviceProfilesSettings deviceProfilesSettings = DeviceProfilesSettings.this;
                if (!deviceProfilesSettings.isDeviceInListForAudioRepair(deviceProfilesSettings.mCachedDevice.getAddress(), "persist.vendor.bt.a2dp.choppy") && defaultAdapter.isEnabled() && i2 != 0 && i2 != 1 && DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_repair_container") != null) {
                    DeviceProfilesSettings.this.getPreferenceScreen().removePreference(DeviceProfilesSettings.this.mAudioRepairContainer);
                }
                if (DeviceProfilesSettings.this.mUpdatePrefForA2DPConnected) {
                    DeviceProfilesSettings.this.mUpdatePrefForA2DPConnected = false;
                    DeviceProfilesSettings.this.updateCodecStatus();
                }
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            Log.d("DeviceProfilesSettings", "onServiceDisconnected()");
            synchronized (DeviceProfilesSettings.this.mBluetoothA2dpLock) {
                DeviceProfilesSettings.this.closeProfileProxy(1);
            }
        }
    };
    private BluetoothProfile.ServiceListener mBluetoothHfpServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.5
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            Log.d("DeviceProfilesSettings", "onHfpServiceConnected()");
            synchronized (DeviceProfilesSettings.this.mBluetoothHfpLock) {
                DeviceProfilesSettings.this.mBluetoothHfp = (BluetoothHeadset) bluetoothProfile;
                if (FeatureParser.getBoolean("support_audio_share", false) && DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_share_container") != null && DeviceProfilesSettings.this.mBluetoothHfp.isAudioOn()) {
                    CheckBoxPreference checkBoxPreference = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_share_switch_pre");
                    BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_share_volume_pre");
                    if (checkBoxPreference != null) {
                        checkBoxPreference.setEnabled(false);
                        Log.d("DeviceProfilesSettings", "mBluetoothHfp.isAudioOn() == on, prefAudioShareSwitch.setDisabled");
                    }
                    if (bluetoothVolumeSeekBarPreference != null) {
                        bluetoothVolumeSeekBarPreference.setEnabled(false);
                    }
                }
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            Log.d("DeviceProfilesSettings", "onHfpServiceDisconnected()");
            synchronized (DeviceProfilesSettings.this.mBluetoothHfpLock) {
                DeviceProfilesSettings.this.closeProfileProxy(2);
            }
        }
    };
    private BroadcastReceiver mBluetoothA2dpReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.6
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("DeviceProfilesSettings", "LDAC: mBluetoothA2dpReceiver.onReceive intent=" + intent);
            String action = intent.getAction();
            if ("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED".equals(action)) {
                Log.d("DeviceProfilesSettings", "Received BluetoothCodecStatus=" + ((BluetoothCodecStatus) intent.getParcelableExtra("android.bluetooth.extra.CODEC_STATUS")));
                if (DeviceProfilesSettings.this.mHandler == null) {
                    DeviceProfilesSettings.this.mHandler = new Handler();
                }
                if (DeviceProfilesSettings.this.mDelayRunnable == null) {
                    DeviceProfilesSettings deviceProfilesSettings = DeviceProfilesSettings.this;
                    deviceProfilesSettings.mDelayRunnable = new DelayRunnable(deviceProfilesSettings);
                }
                DeviceProfilesSettings.this.mHandler.removeCallbacks(DeviceProfilesSettings.this.mDelayRunnable);
                DeviceProfilesSettings.this.mHandler.postDelayed(DeviceProfilesSettings.this.mDelayRunnable, 1500L);
            } else if (!"android.bluetooth.a2dp.profile.action.PLAYING_STATE_CHANGED".equals(action)) {
                if (FeatureParser.getBoolean("support_audio_share", false) && "MultiA2dp.ACTION.VOLUME_CHANGED".equals(action)) {
                    int intExtra = intent.getIntExtra("MultiA2dp.EXTRA.VOLUME_VALUE", 50);
                    Log.d("DeviceProfilesSettings", "ACTION_MULTIA2DP_VOLUME_CHANGED received value is: " + intExtra);
                    DeviceProfilesSettings.this.setAudioShareVolume(intExtra);
                }
            } else {
                int intExtra2 = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 11);
                Log.d("DeviceProfilesSettings", " updateA2DPPlayingState transition: " + intent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 11) + "->" + intExtra2);
                Settings.Secure.getString(context.getContentResolver(), "miui_store_audio_share_device_address");
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("le_audio_pre");
                if (DeviceProfilesSettings.this.mCachedDevice.isActiveDevice(2)) {
                    CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("abs_volume_pre");
                    if ((checkBoxPreference == null || !(DeviceProfilesSettings.this.mCachedDevice.getLeAudioStatus() == 1 || DeviceProfilesSettings.this.mCachedDevice.getSpecificCodecStatus("LEAUDIO") == 1)) && checkBoxPreference2 != null) {
                        if (intExtra2 == 11) {
                            checkBoxPreference2.setEnabled(true);
                        } else if (intExtra2 == 10) {
                            checkBoxPreference2.setEnabled(false);
                        }
                    }
                }
            }
        }
    };
    private BroadcastReceiver mBluetoothMultiA2DPStateResultReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.7
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("DeviceProfilesSettings", "mBluetoothMultiA2DPStateResultReceiver.Receive intent=" + intent);
            String action = intent.getAction();
            if (action == null) {
                Log.e("DeviceProfilesSettings", "Received intent with null action");
            } else if (action == "MultiA2dp.ACTION.RESET_STATE_CHANGED") {
                Log.d("DeviceProfilesSettings", "action == ACTION_MULTIA2DP_STATE_RESULT_CHANGED");
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                int intExtra = intent.getIntExtra("MultiA2dp.EXTRA.STATE", -1);
                if (bluetoothDevice.getAddress().equals(DeviceProfilesSettings.this.mCachedDevice.getAddress())) {
                    DeviceProfilesSettings.this.handleMultiA2DPState(intExtra);
                    return;
                }
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_share_switch_pre");
                if (intExtra != 1 || checkBoxPreference == null) {
                    return;
                }
                checkBoxPreference.setEnabled(true);
            } else if ((action == "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED" || action == "android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED") && FeatureParser.getBoolean("support_audio_share", false)) {
                if (((DeviceProfilesSettings.this.mBluetoothA2dp == null || DeviceProfilesSettings.this.mBluetoothA2dp.getActiveDevice() != null) && DeviceProfilesSettings.this.mCachedDevice.isConnectedA2dpDevice() && !DeviceProfilesSettings.this.mCachedDevice.isActiveDevice(2)) || DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_share_container") == null || DeviceProfilesSettings.this.mAudioShareContainer == null) {
                    return;
                }
                DeviceProfilesSettings.this.getPreferenceScreen().removePreference(DeviceProfilesSettings.this.mAudioShareContainer);
                Log.d("DeviceProfilesSettings", "getActiveDevice() == null,remove audio share container");
            }
        }
    };
    private BroadcastReceiver mBluetoothHfpAudioStateReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.8
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            CheckBoxPreference checkBoxPreference;
            if (intent.getAction() == null) {
                Log.e("DeviceProfilesSettings", "Received mBluetoothHfpAudioStateReceiver intent with null action");
                return;
            }
            int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
            Log.d("DeviceProfilesSettings", "mBluetoothHfpAudioStateReceiver BluetoothProfile.EXTRA_STATE =" + intExtra);
            if (intExtra == 12) {
                if (LocalBluetoothProfileManager.isTbsProfileEnabled() && DeviceProfilesSettings.this.mCachedDevice.isDualModeDevice() && (checkBoxPreference = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("le_audio_pre")) != null) {
                    checkBoxPreference.setEnabled(false);
                    Log.d("DeviceProfilesSettings", "leAudioPre.setEnabled(false) when STATE_AUDIO_CONNECTED");
                }
            } else if (intExtra == 10) {
                if (LocalBluetoothProfileManager.isTbsProfileEnabled() && DeviceProfilesSettings.this.mCachedDevice.isDualModeDevice()) {
                    CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("le_audio_pre");
                    if (checkBoxPreference2 == null || DeviceProfilesSettings.this.mLC3Switching) {
                        return;
                    }
                    checkBoxPreference2.setEnabled(true);
                    Log.d("DeviceProfilesSettings", "leAudioPre.setEnabled(true) when STATE_AUDIO_DISCONNECTED");
                    return;
                }
                CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_share_switch_pre");
                BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_share_volume_pre");
                if (checkBoxPreference3 != null) {
                    checkBoxPreference3.setEnabled(true);
                    Log.d("DeviceProfilesSettings", "BluetoothHeadset.STATE_AUDIO_DISCONNECTED, prefAudioShareSwitch.setEnabled");
                }
                if (bluetoothVolumeSeekBarPreference != null) {
                    bluetoothVolumeSeekBarPreference.setEnabled(true);
                }
            }
        }
    };
    private BroadcastReceiver mBluetoothAudioRepairResultReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.9
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("DeviceProfilesSettings", "mBluetoothAudioRepairResultReceiver.Receive intent=" + intent);
            if (intent.getAction() == null) {
                Log.e("DeviceProfilesSettings", "Received intent with null action");
            } else if (!BluetoothAdapter.getDefaultAdapter().isEnabled() || DeviceProfilesSettings.this.mAudioRepairingDialog == null) {
            } else {
                DeviceProfilesSettings.this.mAudioRepairingDialog.dismiss();
                DeviceProfilesSettings.this.mAudioRepairingDialog = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.bt_audiorepair_open_dialog_title);
                builder.setMessage(R.string.bt_audiorepair_dialog_open_success);
                builder.setCancelable(true);
                builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
                DeviceProfilesSettings.this.mAudioRepairDialog = builder.create();
                DeviceProfilesSettings.this.mAudioRepairDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.9.1
                    @Override // android.content.DialogInterface.OnDismissListener
                    public void onDismiss(DialogInterface dialogInterface) {
                        DeviceProfilesSettings.this.mAudioRepairDialog = null;
                        DeviceProfilesSettings.this.finish();
                    }
                });
                DeviceProfilesSettings.this.mAudioRepairDialog.show();
            }
        }
    };
    private final Preference.OnPreferenceChangeListener mPrefChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.10
        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            if (DeviceProfilesSettings.this.mBluetoothA2dp.getConnectionState(DeviceProfilesSettings.this.mCachedDevice.getDevice()) == 2 || (LocalBluetoothProfileManager.isTbsProfileEnabled() && DeviceProfilesSettings.this.mCachedDevice.isDualModeDevice() && DeviceProfilesSettings.this.mCachedDevice.isConnected())) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                if (((Boolean) obj).booleanValue()) {
                    checkBoxPreference.setChecked(true);
                    DeviceProfilesSettings.this.mCachedDevice.setSpecificCodecStatus("latency_val", 1);
                } else {
                    checkBoxPreference.setChecked(false);
                    DeviceProfilesSettings.this.mCachedDevice.setSpecificCodecStatus("latency_val", 0);
                }
                DeviceProfilesSettings.this.handleCheckBoxPreferenceEnabled(checkBoxPreference);
            }
            return false;
        }
    };
    private Runnable mRunnable = new Runnable() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.12
        @Override // java.lang.Runnable
        public void run() {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("ldac_pre");
            CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("latency_pre");
            CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("le_audio_pre");
            if (checkBoxPreference != null) {
                checkBoxPreference.setEnabled(true);
            }
            if (checkBoxPreference3 != null && !LocalBluetoothProfileManager.isTbsProfileEnabled()) {
                checkBoxPreference3.setEnabled(true);
            }
            if (checkBoxPreference2 != null) {
                if (DeviceProfilesSettings.this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") == 0 || DeviceProfilesSettings.this.mCachedDevice.getSpecificCodecStatus("LHDC_V2") == 0 || DeviceProfilesSettings.this.mCachedDevice.getSpecificCodecStatus("LHDC_V1") == 0) {
                    checkBoxPreference2.setEnabled(false);
                } else {
                    checkBoxPreference2.setEnabled(true);
                }
            }
        }
    };
    private Runnable mCodecConfigRun = new Runnable() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.15
        @Override // java.lang.Runnable
        public void run() {
            if (DeviceProfilesSettings.this.mCachedDevice != null) {
                DeviceProfilesSettings.this.mCachedDevice.connect(true);
            }
        }
    };
    private Runnable mDisableVolumeRun = new Runnable() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.18
        @Override // java.lang.Runnable
        public void run() {
            DeviceProfilesSettings.this.sendBroadcastEnableOrDisable(false);
        }
    };
    private Runnable mDelayOpenAudioShareRunnable = new Runnable() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.19
        @Override // java.lang.Runnable
        public void run() {
            DeviceProfilesSettings.this.onAudioShareSwitchPrefClicked((CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_share_switch_pre"));
        }
    };
    private Runnable mAudioShareCheckA2DPActiveExistRunnable = new Runnable() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.20
        @Override // java.lang.Runnable
        public void run() {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_share_switch_pre");
            if (DeviceProfilesSettings.this.mBluetoothA2dp == null) {
                Log.d("DeviceProfilesSettings", "mBluetoothA2dp == null");
            } else if (DeviceProfilesSettings.this.mBluetoothA2dp.getActiveDevice() != null) {
                Log.d("DeviceProfilesSettings", "mBluetoothA2dp.getActiveDevice() != null");
            } else {
                if (checkBoxPreference != null) {
                    checkBoxPreference.setEnabled(false);
                    checkBoxPreference.setVisible(false);
                    Log.d("DeviceProfilesSettings", "getActiveDevice() == null,disable checkbox");
                }
                if (DeviceProfilesSettings.this.getPreferenceScreen().findPreference("audio_share_container") == null || DeviceProfilesSettings.this.mAudioShareContainer == null) {
                    return;
                }
                DeviceProfilesSettings.this.getPreferenceScreen().removePreference(DeviceProfilesSettings.this.mAudioShareContainer);
                Log.d("DeviceProfilesSettings", "getActiveDevice() == null,remove audio share container");
            }
        }
    };
    private Runnable mAudioRepairRunnable = new Runnable() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.27
        @Override // java.lang.Runnable
        public void run() {
            BluetoothAdapter.getDefaultAdapter().enable();
        }
    };

    /* renamed from: com.android.settings.bluetooth.DeviceProfilesSettings$24  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass24 implements Preference.OnPreferenceClickListener {
        final /* synthetic */ DeviceProfilesSettings this$0;

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            try {
                if (this.this$0.mAudioRepairDialog == null) {
                    this.this$0.createDialogForOpenAudioRepair();
                    return true;
                }
                return true;
            } catch (Exception e) {
                Log.v("DeviceProfilesSettings", "onAudioRepairClicked failed ", e);
                return true;
            }
        }
    }

    /* loaded from: classes.dex */
    private static final class DelayRunnable implements Runnable {
        WeakReference<DeviceProfilesSettings> mRef;

        DelayRunnable(DeviceProfilesSettings deviceProfilesSettings) {
            this.mRef = new WeakReference<>(deviceProfilesSettings);
        }

        @Override // java.lang.Runnable
        public void run() {
            DeviceProfilesSettings deviceProfilesSettings = this.mRef.get();
            if (deviceProfilesSettings != null) {
                deviceProfilesSettings.updateCodecStatus();
            }
        }
    }

    /* loaded from: classes.dex */
    private class RenameEditTextPreference implements TextWatcher {
        private RenameEditTextPreference() {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            Dialog dialog = DeviceProfilesSettings.this.mDeviceNamePref.getDialog();
            if (dialog instanceof AlertDialog) {
                ((AlertDialog) dialog).getButton(-1).setEnabled(editable.length() > 0);
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    private void addAudioShareConfigPreference() {
        BluetoothVolumeSeekBarPreference createAudioShareConfigPreference = createAudioShareConfigPreference();
        PreferenceGroup preferenceGroup = this.mAudioShareContainer;
        if (preferenceGroup != null) {
            preferenceGroup.addPreference(createAudioShareConfigPreference);
            Log.d("DeviceProfilesSettings", "mAudioShareContainer.addPreference");
        }
    }

    private void addLatencyCodecPreference() {
        if (this.mCachedDevice.getSpecificCodecStatus("latency_pre") == 1) {
            this.mCodecContainer.addPreference(createLatencyCodecPreference());
        } else if (this.mCachedDevice.getSpecificCodecStatus("latency_val") == 0 && this.mCachedDevice.getSpecificCodecStatus("latency_pre") == 0) {
            this.mCachedDevice.setSpecificCodecStatus("latency_pre", 1);
            this.mCodecContainer.addPreference(createLatencyCodecPreference());
        }
    }

    private void addPreferencesForAbsoluteVolume() {
        this.mCodecContainer.addPreference(createAbsoluteVolumePreference());
    }

    private void addPreferencesForAudioShare() {
        Log.d("DeviceProfilesSettings", "mCachedDevice.isConnectedA2dpDevice() = " + this.mCachedDevice.isConnectedA2dpDevice());
        Log.d("DeviceProfilesSettings", "mCachedDevice.isActiveDevice = " + this.mCachedDevice.isActiveDevice(2));
        if (!this.mCachedDevice.isConnectedA2dpDevice() || this.mCachedDevice.isActiveDevice(2)) {
            if (getPreferenceScreen().findPreference("audio_share_container") == null || this.mAudioShareContainer == null) {
                return;
            }
            getPreferenceScreen().removePreference(this.mAudioShareContainer);
            return;
        }
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
        checkBoxPreference.setKey("audio_share_switch_pre");
        checkBoxPreference.setTitle(R.string.bt_audio_share_switch_title);
        checkBoxPreference.setSummary(R.string.bt_audio_share_switch_summary);
        boolean z = false;
        checkBoxPreference.setPersistent(false);
        checkBoxPreference.setOnPreferenceChangeListener(this);
        Log.d("DeviceProfilesSettings", "temp = " + this.mCachedDevice.getSpecificCodecStatus("AUDIO_SHARE_SWITCH"));
        FragmentActivity activity = getActivity();
        String string = Settings.Secure.getString(activity.getContentResolver(), "miui_store_audio_share_device_address");
        Log.d("DeviceProfilesSettings", "KEY_STORE_AUDIO_SHARE_DEVICE = " + string);
        if (string == null || string.equals(this.mCachedDevice.getAddress())) {
            z = true;
        } else {
            this.mCachedDevice.setSpecificCodecStatus("AUDIO_SHARE_SWITCH", 0);
            if (string.equals("pending")) {
                checkBoxPreference.setEnabled(false);
            }
        }
        checkBoxPreference.setChecked(z);
        String string2 = Settings.Secure.getString(activity.getContentResolver(), "miui_store_audio_share_window_pop");
        if (string2 != null && !checkBoxPreference.isChecked() && string2.equals("NeedPop")) {
            handleDelayOpenAudioShare();
            Settings.Secure.putString(activity.getContentResolver(), "miui_store_audio_share_window_pop", "HadPoped");
        }
        PreferenceGroup preferenceGroup = this.mAudioShareContainer;
        if (preferenceGroup != null) {
            preferenceGroup.addPreference(checkBoxPreference);
        }
        addAudioShareConfigPreference();
        handleAudioShareConfigStatus(checkBoxPreference.isChecked());
        handleCheckA2DPActiveExist();
    }

    private void addPreferencesForLeAudio() {
        this.mCodecContainer.addPreference(createLeAudioPreference());
    }

    private void addPreferencesForProfiles() {
        LocalBluetoothProfile pbapProfile;
        for (LocalBluetoothProfile localBluetoothProfile : this.mCachedDevice.getConnectableProfiles()) {
            if (!PbapServerProfile.NAME.equals(localBluetoothProfile.toString())) {
                CheckBoxPreference createProfilePreference = createProfilePreference(localBluetoothProfile);
                if (localBluetoothProfile.toString().equals("BCProfile")) {
                    Log.d("DeviceProfilesSettings", "Device support ble audio !");
                    boolean z = false;
                    if (SystemProperties.getBoolean("persist.vendor.service.bt.lea_test", false)) {
                        if (this.mBleAudioCategory != null) {
                            boolean isEnabled = localBluetoothProfile.isEnabled(this.mCachedDevice.getDevice());
                            boolean isConnected = this.mCachedDevice.isConnected();
                            Log.d("DeviceProfilesSettings", "mBleAudioCategory not null add to show ! connect state: " + isConnected + " profile enabled: " + isEnabled);
                            createProfilePreference.setOrder(1);
                            createProfilePreference.setChecked(isEnabled && isConnected);
                            this.mBleAudioCategory.addPreference(createProfilePreference);
                            getPreferenceScreen().addPreference(this.mBleAudioCategory);
                            Preference findPreference = findPreference("bleAudioBroadcastAdd");
                            if (findPreference != null && this.mCachedDevice != null) {
                                if (isConnected && isEnabled) {
                                    z = true;
                                }
                                findPreference.setEnabled(z);
                            }
                        } else {
                            Log.d("DeviceProfilesSettings", "mBleAudioCategory is null do nothing and return!");
                        }
                    }
                } else {
                    this.mProfileContainer.addPreference(createProfilePreference);
                }
            }
        }
        if (this.mCachedDevice.getPhonebookPermissionChoice() != 0 && (pbapProfile = this.mManager.getProfileManager().getPbapProfile()) != null) {
            this.mProfileContainer.addPreference(createProfilePreference(pbapProfile));
        }
        MapProfile mapProfile = this.mManager.getProfileManager().getMapProfile();
        if (this.mCachedDevice.getMessagePermissionChoice() != 0 && findPreference(mapProfile.toString()) == null) {
            this.mProfileContainer.addPreference(createProfilePreference(mapProfile));
        }
        showOrHideProfileGroup();
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0099  */
    /* JADX WARN: Removed duplicated region for block: B:51:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void addPreferencesForSpecialCodec() {
        /*
            Method dump skipped, instructions count: 249
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.DeviceProfilesSettings.addPreferencesForSpecialCodec():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addToWhiteList(String str) {
        String str2 = SystemProperties.get(str, "");
        Log.d("DeviceProfilesSettings", "addToWhiteList(): whitelist before add is " + str2 + ", current dev is " + this.mCachedDevice.getAddress().toLowerCase() + ", prop is " + str);
        if (str2.length() >= 90) {
            str2 = str2.substring(18);
        }
        if (str2.indexOf(this.mCachedDevice.getAddress().toLowerCase()) >= 0) {
            Log.d("DeviceProfilesSettings", "addToWhiteList(): the device has already in whitelist,do nothing");
            return;
        }
        StringBuilder sb = new StringBuilder(str2);
        sb.append(this.mCachedDevice.getAddress().toLowerCase());
        sb.append(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        Log.d("DeviceProfilesSettings", "addToWhiteList(): whitelist after add is " + sb.toString());
        SystemProperties.set(str, sb.toString());
    }

    private void askDisconnect(Context context, final LocalBluetoothProfile localBluetoothProfile) {
        final CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        String name = cachedBluetoothDevice.getName();
        if (TextUtils.isEmpty(name)) {
            name = context.getString(R.string.bluetooth_device);
        }
        String string = context.getString(localBluetoothProfile.getNameResource(cachedBluetoothDevice.getDevice()));
        AlertDialog showDisconnectDialog = Utils.showDisconnectDialog(context, this.mDisconnectDialog, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                cachedBluetoothDevice.disconnect(localBluetoothProfile);
                if (localBluetoothProfile instanceof MapProfile) {
                    cachedBluetoothDevice.setMessagePermissionChoice(2);
                }
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) DeviceProfilesSettings.this.findPreference(localBluetoothProfile.toString());
                if (checkBoxPreference != null) {
                    DeviceProfilesSettings.this.refreshProfilePreference(checkBoxPreference, localBluetoothProfile);
                }
            }
        }, context.getString(R.string.bluetooth_disable_profile_title), Html.fromHtml(context.getString(R.string.bluetooth_disable_profile_message, string, name)));
        this.mDisconnectDialog = showDisconnectDialog;
        showDisconnectDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.3
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) DeviceProfilesSettings.this.findPreference(localBluetoothProfile.toString());
                if (checkBoxPreference != null) {
                    DeviceProfilesSettings.this.refreshProfilePreference(checkBoxPreference, localBluetoothProfile);
                }
            }
        });
    }

    private void broadcastMultiA2dpStateChange(BluetoothDevice bluetoothDevice, int i) {
        Intent intent = new Intent("MultiA2dp.ACTION.STATE_CHANGED");
        intent.setPackage("com.android.bluetooth");
        intent.putExtra("android.bluetooth.device.extra.DEVICE", bluetoothDevice);
        intent.putExtra("MultiA2dp.EXTRA.STATE", i);
        try {
            getActivity().sendBroadcast(intent);
        } catch (Exception e) {
            Log.v("DeviceProfilesSettings", "send broadcast failed ", e);
        }
    }

    private void broadcastMultiA2dpVolumChange(BluetoothDevice bluetoothDevice, int i) {
        Intent intent = new Intent("MultiA2dp.ACTION.SETVOLUME_CHANGED");
        intent.setPackage("com.android.bluetooth");
        intent.putExtra("android.bluetooth.device.extra.DEVICE", bluetoothDevice);
        intent.putExtra("MultiA2dp.EXTRA.VOLUME_VALUE", i);
        try {
            getActivity().sendBroadcast(intent);
        } catch (Exception e) {
            Log.v("DeviceProfilesSettings", "send broadcast failed ", e);
        }
    }

    private void closeAbsVolume() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("abs_volume_pre");
        if (checkBoxPreference != null) {
            Log.i("DeviceProfilesSettings", "onAbsVolumePrefClicked  set false");
            this.mBluetoothA2dp.setAvrcpAbsoluteVolume(this.mAudioStreamMax);
            checkBoxPreference.setChecked(false);
            handleDisableVolume();
            this.mCachedDevice.setSpecificCodecStatus("ABSOLUTEVOLUME", 0);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x00eb  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0110  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void closeLeAudio() {
        /*
            Method dump skipped, instructions count: 363
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.DeviceProfilesSettings.closeLeAudio():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeProfileProxy(int i) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            if (i == 0) {
                defaultAdapter.closeProfileProxy(2, this.mBluetoothA2dp);
                this.mBluetoothA2dp = null;
                defaultAdapter.closeProfileProxy(1, this.mBluetoothHfp);
                this.mBluetoothHfp = null;
            } else if (i == 1) {
                defaultAdapter.closeProfileProxy(2, this.mBluetoothA2dp);
                this.mBluetoothA2dp = null;
            } else if (i != 2) {
            } else {
                defaultAdapter.closeProfileProxy(1, this.mBluetoothHfp);
                this.mBluetoothHfp = null;
            }
        }
    }

    private CheckBoxPreference createAbsoluteVolumePreference() {
        try {
            Log.d("DeviceProfilesSettings", "create createAbsoluteVolumePreference");
            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
            checkBoxPreference.setKey("abs_volume_pre");
            checkBoxPreference.setTitle(R.string.bt_absVolume_pre_title);
            checkBoxPreference.setSummary(R.string.bt_absVolume_summary);
            checkBoxPreference.setPersistent(false);
            checkBoxPreference.setOnPreferenceChangeListener(this);
            checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("ABSOLUTEVOLUME") == 1);
            BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
            BluetoothDevice activeDevice = bluetoothA2dp != null ? bluetoothA2dp.getActiveDevice() : null;
            Log.d("DeviceProfilesSettings", "onAbsVolumePrefClicked mBluetoothA2dp " + this.mBluetoothA2dp);
            if (this.mBluetoothA2dp == null || activeDevice == null || !activeDevice.equals(this.mCachedDevice)) {
                checkBoxPreference.setEnabled(false);
            } else {
                checkBoxPreference.setEnabled(true);
            }
            return checkBoxPreference;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DeviceProfilesSettings", "error " + e);
            return null;
        }
    }

    private BluetoothVolumeSeekBarPreference createAudioShareConfigPreference() {
        BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = new BluetoothVolumeSeekBarPreference(getPrefContext());
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("audio_share_switch_pre");
        bluetoothVolumeSeekBarPreference.setKey("audio_share_volume_pre");
        bluetoothVolumeSeekBarPreference.setTitle(this.mCachedDevice.getName());
        bluetoothVolumeSeekBarPreference.setMin(0);
        bluetoothVolumeSeekBarPreference.setMax(100);
        String string = Settings.Secure.getString(getActivity().getContentResolver(), "miui_bluetooth_audio_share_volume");
        int i = 50;
        if (string != null) {
            try {
                i = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                Log.d("DeviceProfilesSettings", "Integer.parseInt E: " + e.toString());
            }
        }
        Log.d("DeviceProfilesSettings", "KEY_AUDIO_SHARE_VOLUME_PRE = " + i);
        bluetoothVolumeSeekBarPreference.setProgress(i);
        bluetoothVolumeSeekBarPreference.setIcon(R.drawable.ic_bt_headphones_a2dp_bonded);
        bluetoothVolumeSeekBarPreference.setPersistent(false);
        bluetoothVolumeSeekBarPreference.setOrder(80);
        bluetoothVolumeSeekBarPreference.setVisible(checkBoxPreference.isChecked());
        bluetoothVolumeSeekBarPreference.setOnPreferenceChangeListener(this);
        bluetoothVolumeSeekBarPreference.setStopTrackingTouchListener(new SeekBarPreference.StopTrackingTouchListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.21
            @Override // com.android.settings.widget.SeekBarPreference.StopTrackingTouchListener
            public void onStopTrackingTouch() {
                DeviceProfilesSettings.this.handleAudioShareVolume();
            }
        });
        return bluetoothVolumeSeekBarPreference;
    }

    private void createDialog() {
        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("ldac_pre");
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.13
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                DeviceProfilesSettings.this.writeBluetoothA2dpConfiguration(true);
                DeviceProfilesSettings.this.handleCheckBoxPreferenceEnabled(checkBoxPreference);
                CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("latency_pre");
                if (checkBoxPreference == null || DeviceProfilesSettings.this.mBluetoothA2dp == null) {
                    return;
                }
                checkBoxPreference.setChecked(true);
                if (checkBoxPreference2 != null) {
                    checkBoxPreference2.setChecked(true);
                    checkBoxPreference2.setEnabled(true);
                }
                if (DeviceProfilesSettings.this.mLHDCV3Device) {
                    DeviceProfilesSettings.this.mCachedDevice.setSpecificCodecStatus("LHDC_V3", 1);
                    DeviceProfilesSettings.this.mCachedDevice.setSpecificCodecStatus("latency_val", 1);
                } else if (DeviceProfilesSettings.this.mLHDCV2Device) {
                    DeviceProfilesSettings.this.mCachedDevice.setSpecificCodecStatus("LHDC_V2", 1);
                    DeviceProfilesSettings.this.mCachedDevice.setSpecificCodecStatus("latency_val", 1);
                } else if (DeviceProfilesSettings.this.mLHDCV1Device) {
                    DeviceProfilesSettings.this.mCachedDevice.setSpecificCodecStatus("LHDC_V1", 1);
                    DeviceProfilesSettings.this.mCachedDevice.setSpecificCodecStatus("latency_val", 1);
                } else if (DeviceProfilesSettings.this.mLDACDevice) {
                    DeviceProfilesSettings.this.mCachedDevice.setSpecificCodecStatus("LDAC", 1);
                } else if (DeviceProfilesSettings.this.mAACDevice) {
                    DeviceProfilesSettings.this.mCachedDevice.setSpecificCodecStatus("AAC", 1);
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (this.mLHDCV3Device || this.mLHDCV2Device || this.mLHDCV1Device) {
            builder.setTitle(R.string.bt_lhdc_open_dialog_title);
            builder.setMessage(R.string.bt_lhdc_open_dialog_summary);
        } else if (this.mLDACDevice) {
            builder.setTitle(R.string.bt_ldac_open_dialog_title);
            builder.setMessage(R.string.bt_ldac_open_dialog_summary);
        } else {
            builder.setTitle(R.string.bt_aac_open_dialog_title);
            builder.setMessage(R.string.bt_aac_open_dialog_summary);
        }
        builder.setCancelable(true);
        builder.setPositiveButton(17039370, onClickListener);
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.14
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (checkBoxPreference != null) {
                    if (DeviceProfilesSettings.this.mLHDCV3Device) {
                        checkBoxPreference.setChecked(DeviceProfilesSettings.this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") == 1);
                    } else if (DeviceProfilesSettings.this.mLHDCV2Device) {
                        checkBoxPreference.setChecked(DeviceProfilesSettings.this.mCachedDevice.getSpecificCodecStatus("LHDC_V2") == 1);
                    } else if (DeviceProfilesSettings.this.mLHDCV1Device) {
                        checkBoxPreference.setChecked(DeviceProfilesSettings.this.mCachedDevice.getSpecificCodecStatus("LHDC_V1") == 1);
                    } else if (DeviceProfilesSettings.this.mLDACDevice) {
                        checkBoxPreference.setChecked(DeviceProfilesSettings.this.mCachedDevice.getSpecificCodecStatus("LDAC") == 1);
                    } else if (DeviceProfilesSettings.this.mAACDevice) {
                        checkBoxPreference.setChecked(DeviceProfilesSettings.this.mCachedDevice.getSpecificCodecStatus("AAC") == 1);
                    }
                }
            }
        });
        this.mDialog.show();
    }

    private void createDialogForLeAudio(final CheckBoxPreference checkBoxPreference) {
        final CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) getPreferenceScreen().findPreference("le_audio_pre");
        final CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) getPreferenceScreen().findPreference("abs_volume_pre");
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.22
            /* JADX WARN: Removed duplicated region for block: B:32:0x00e7  */
            /* JADX WARN: Removed duplicated region for block: B:38:0x012f  */
            /* JADX WARN: Removed duplicated region for block: B:46:0x014b  */
            @Override // android.content.DialogInterface.OnClickListener
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void onClick(android.content.DialogInterface r18, int r19) {
                /*
                    Method dump skipped, instructions count: 716
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.DeviceProfilesSettings.AnonymousClass22.onClick(android.content.DialogInterface, int):void");
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.bt_leaudio_open_dialog_title);
        builder.setMessage(R.string.bt_leaudio_open_dialog_summary);
        builder.setCancelable(true);
        builder.setPositiveButton(17039370, onClickListener);
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.23
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (checkBoxPreference2 != null) {
                    if (LocalBluetoothProfileManager.isTbsProfileEnabled()) {
                        checkBoxPreference2.setChecked(DeviceProfilesSettings.this.mCachedDevice.getLeAudioStatus() == 1);
                    } else {
                        checkBoxPreference2.setChecked(DeviceProfilesSettings.this.mCachedDevice.getSpecificCodecStatus("LEAUDIO") == 1);
                    }
                }
            }
        });
        this.mDialog.show();
    }

    private void createDialogForOpenAbsVolume() {
        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("abs_volume_pre");
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.16
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (checkBoxPreference != null) {
                    Log.i("DeviceProfilesSettings", "onAbsVolumePrefClicked  set true");
                    checkBoxPreference.setChecked(true);
                    DeviceProfilesSettings.this.mCachedDevice.setSpecificCodecStatus("ABSOLUTEVOLUME", 1);
                    DeviceProfilesSettings.this.sendBroadcastEnableOrDisable(true);
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.bt_absVolume_open_dialog_title);
        builder.setMessage(R.string.bt_absVolume_open_dialog_summary);
        builder.setCancelable(true);
        builder.setPositiveButton(17039370, onClickListener);
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.17
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                CheckBoxPreference checkBoxPreference2 = checkBoxPreference;
                if (checkBoxPreference2 != null) {
                    checkBoxPreference2.setChecked(DeviceProfilesSettings.this.mCachedDevice.getSpecificCodecStatus("ABSOLUTEVOLUME") == 1);
                }
            }
        });
        this.mDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createDialogForOpenAudioRepair() {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.25
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                FragmentActivity activity = DeviceProfilesSettings.this.getActivity();
                DeviceProfilesSettings.this.mAudioRepairingDialog = new ProgressDialog(activity);
                DeviceProfilesSettings deviceProfilesSettings = DeviceProfilesSettings.this;
                if (deviceProfilesSettings.isDeviceInListForAudioRepair(deviceProfilesSettings.mCachedDevice.getAddress(), "persist.vendor.bt.a2dp.choppy")) {
                    DeviceProfilesSettings.this.delFromWhiteList("persist.vendor.bt.a2dp.choppy");
                } else {
                    DeviceProfilesSettings.this.addToWhiteList("persist.vendor.bt.a2dp.choppy");
                }
                BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                if (defaultAdapter.isEnabled()) {
                    Log.d("DeviceProfilesSettings", "Disable BT for audio repair");
                    defaultAdapter.disable();
                    DeviceProfilesSettings.this.mAudioRepairingDialog.setCancelable(false);
                    DeviceProfilesSettings.this.mAudioRepairingDialog.setCanceledOnTouchOutside(false);
                    DeviceProfilesSettings.this.mAudioRepairingDialog.setMessage(DeviceProfilesSettings.this.getString(R.string.bt_audiorepair_working));
                    DeviceProfilesSettings.this.mAudioRepairingDialog.show();
                    if (DeviceProfilesSettings.this.mHandler == null) {
                        DeviceProfilesSettings.this.mHandler = new Handler();
                    }
                    HashMap hashMap = new HashMap();
                    hashMap.put("bqr_trigger", "user_add");
                    OneTrackInterfaceUtils.track("BQR_TRIG", hashMap);
                    DeviceProfilesSettings.this.mHandler.postDelayed(DeviceProfilesSettings.this.mAudioRepairRunnable, 1500L);
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.bt_audiorepair_open_dialog_title);
        builder.setCancelable(true);
        builder.setMessage(R.string.bt_audiorepair_open_dialog_summary);
        builder.setPositiveButton(R.string.bt_audiorepair_dialog_openok, onClickListener);
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        this.mAudioRepairDialog = create;
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.26
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                DeviceProfilesSettings.this.mAudioRepairDialog = null;
            }
        });
        this.mAudioRepairDialog.show();
    }

    private CheckBoxPreference createLatencyCodecPreference() {
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
        checkBoxPreference.setKey("latency_pre");
        if (this.mSBCLlDevice) {
            checkBoxPreference.setTitle(R.string.codec_low_latency_zmi_title);
        } else {
            checkBoxPreference.setTitle(R.string.codec_low_latency_title);
        }
        if (this.mCachedDevice.getSpecificCodecStatus("aptxadaptive_video") == 1) {
            checkBoxPreference.setSummary(R.string.codec_low_latency_video_summary);
        } else if (this.mSBCLlDevice) {
            checkBoxPreference.setSummary(R.string.codec_low_latency_zmi_summary);
        } else {
            checkBoxPreference.setSummary(R.string.codec_low_latency_summary);
        }
        checkBoxPreference.setPersistent(false);
        checkBoxPreference.setOnPreferenceChangeListener(this.mPrefChangeListener);
        if (this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") == 0 || this.mCachedDevice.getSpecificCodecStatus("LHDC_V2") == 0 || this.mCachedDevice.getSpecificCodecStatus("LHDC_V1") == 0) {
            checkBoxPreference.setEnabled(false);
        }
        checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("latency_val") == 1);
        if (this.mCachedDevice.getSpecificCodecStatus("latency_pre") != 1) {
            this.mCachedDevice.setSpecificCodecStatus("latency_val", 0);
        }
        return checkBoxPreference;
    }

    private CheckBoxPreference createLeAudioPreference() {
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
        checkBoxPreference.setKey("le_audio_pre");
        checkBoxPreference.setTitle(R.string.bt_leaudio_pre_title);
        checkBoxPreference.setSummary(R.string.bt_leaudio_summary);
        checkBoxPreference.setPersistent(false);
        checkBoxPreference.setOnPreferenceChangeListener(this);
        if (LocalBluetoothProfileManager.isTbsProfileEnabled()) {
            checkBoxPreference.setChecked(this.mCachedDevice.getLeAudioStatus() == 1);
        } else {
            checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("LEAUDIO") == 1);
        }
        Log.i("DeviceProfilesSettings", " createLeAudioPreference");
        return checkBoxPreference;
    }

    private CheckBoxPreference createProfilePreference(LocalBluetoothProfile localBluetoothProfile) {
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
        checkBoxPreference.setKey(localBluetoothProfile.toString());
        checkBoxPreference.setTitle(localBluetoothProfile.getNameResource(this.mCachedDevice.getDevice()));
        checkBoxPreference.setPersistent(false);
        checkBoxPreference.setOrder(getProfilePreferenceIndex(localBluetoothProfile.getOrdinal()));
        checkBoxPreference.setOnPreferenceChangeListener(this);
        checkBoxPreference.setEnabled(!this.mCachedDevice.isBusy());
        refreshProfilePreference(checkBoxPreference, localBluetoothProfile);
        return checkBoxPreference;
    }

    private CheckBoxPreference createSpecialCodecPreference(String str) {
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
        checkBoxPreference.setKey("ldac_pre");
        if ("LDAC".equals(str)) {
            checkBoxPreference.setTitle(R.string.bt_ldac_pre_title);
        } else if ("LHDC_V3".equals(str)) {
            checkBoxPreference.setTitle(R.string.bt_lhdc_pre_title);
        } else if ("LHDC_V2".equals(str)) {
            checkBoxPreference.setTitle(R.string.bt_lhdc_pre_title);
        } else if ("LHDC_V1".equals(str)) {
            checkBoxPreference.setTitle(R.string.bt_lhdc_pre_title);
        } else {
            checkBoxPreference.setTitle(R.string.bt_aac_pre_title);
        }
        checkBoxPreference.setSummary(R.string.bt_pre_summary);
        checkBoxPreference.setPersistent(false);
        checkBoxPreference.setOnPreferenceChangeListener(this);
        if ("LDAC".equals(str)) {
            checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("LDAC") == 1);
        } else if ("LHDC_V3".equals(str)) {
            checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") == 1);
        } else if ("LHDC_V2".equals(str)) {
            checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("LHDC_V2") == 1);
        } else if ("LHDC_V1".equals(str)) {
            checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("LHDC_V1") == 1);
        } else {
            checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("AAC") == 1);
        }
        return checkBoxPreference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void delFromWhiteList(String str) {
        String str2 = SystemProperties.get(str, "");
        if (str2.length() < 18) {
            Log.w("DeviceProfilesSettings", "delFromWhiteList(): no valid device in white list");
            return;
        }
        Log.d("DeviceProfilesSettings", "delFromWhiteList(): whitelist before del is " + str2 + ", current dev is " + this.mCachedDevice.getAddress().toLowerCase() + ", prop is " + str);
        StringBuilder sb = new StringBuilder(this.mCachedDevice.getAddress().toLowerCase());
        sb.append(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        SystemProperties.set(str, str2.replaceAll(sb.toString(), ""));
        StringBuilder sb2 = new StringBuilder();
        sb2.append("delFromWhiteList(): whitelist after del is ");
        sb2.append(SystemProperties.get(str, ""));
        Log.d("DeviceProfilesSettings", sb2.toString());
    }

    private void delFromWhiteListForAbsoluteVolume(String str) {
        String str2 = SystemProperties.get(str, "");
        if (str2.length() < 18) {
            Log.w("DeviceProfilesSettings", "delFromWhiteList(): no valid device in white list");
            return;
        }
        Log.d("DeviceProfilesSettings", "delFromWhiteList(): whitelist before del is " + str2 + ", current dev is " + this.mCachedDevice.getAddress().toLowerCase() + ", prop is " + str);
        StringBuilder sb = new StringBuilder(this.mCachedDevice.getAddress());
        sb.append(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        SystemProperties.set(str, str2.replaceAll(sb.toString(), ""));
        StringBuilder sb2 = new StringBuilder();
        sb2.append("delFromWhiteList(): whitelist after del is ");
        sb2.append(SystemProperties.get(str, ""));
        Log.d("DeviceProfilesSettings", sb2.toString());
    }

    private void deleteSaveMacForLeAudio() {
        String str;
        if (LocalBluetoothProfileManager.isTbsProfileEnabled()) {
            Log.i("DeviceProfilesSettings", "deleteSaveMacForLeAudio: do nothing");
            return;
        }
        Context context = getContext();
        String string = Settings.Global.getString(context.getContentResolver(), "three_mac_for_ble_f");
        if (string != null && string.contains(this.mDeviceMacAddress) && string.length() % 54 == 0) {
            int indexOf = string.indexOf(this.mDeviceMacAddress);
            if (string.length() == 54) {
                str = "";
            } else if (indexOf == 0 || indexOf + 54 != string.length()) {
                str = string.substring(0, indexOf) + string.substring(indexOf + 54, string.length());
            } else {
                str = string.substring(0, indexOf);
            }
            Log.i("DeviceProfilesSettings", "updateValue is" + str);
            Settings.Global.putString(context.getContentResolver(), "three_mac_for_ble_f", str);
        }
    }

    private void disconnectLeAudio() {
        String str;
        int indexOf;
        int indexOf2;
        String string = Settings.Global.getString(getContext().getContentResolver(), "three_mac_for_ble_f");
        String str2 = "00:00:00:00:00:00";
        if (string == null || string.length() < (indexOf2 = (indexOf = string.indexOf(this.mDeviceMacAddress)) + 53) || !string.contains(this.mDeviceMacAddress)) {
            str = "00:00:00:00:00:00";
        } else {
            Log.i("DeviceProfilesSettings", "startIndex is " + indexOf + " value is " + string);
            str2 = string.substring(indexOf + 18, indexOf + 35);
            str = string.substring(indexOf + 36, indexOf2);
            Log.i("DeviceProfilesSettings", "leStr1 is " + str2 + " leStr2 is " + str);
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            BluetoothDevice remoteDevice = defaultAdapter.getRemoteDevice(str2);
            BluetoothDevice remoteDevice2 = defaultAdapter.getRemoteDevice(str);
            if (remoteDevice != null && remoteDevice.getBondState() != 10) {
                defaultAdapter.disconnectAllEnabledProfiles(remoteDevice);
                Log.i("DeviceProfilesSettings", "disconnect leStr1");
            }
            if (remoteDevice2 == null || remoteDevice2.getBondState() == 10) {
                return;
            }
            defaultAdapter.disconnectAllEnabledProfiles(remoteDevice2);
            Log.i("DeviceProfilesSettings", "disconnect leStr2");
        }
    }

    private BluetoothCodecConfig getCodecConfig(BluetoothA2dp bluetoothA2dp, int i, int i2) {
        int i3;
        int i4;
        BluetoothCodecConfig bluetoothCodecConfig;
        BluetoothCodecStatus codecStatus = bluetoothA2dp.getCodecStatus(this.mCachedDevice.getDevice());
        BluetoothCodecConfig bluetoothCodecConfig2 = null;
        if (codecStatus == null) {
            return null;
        }
        BluetoothCodecConfig[] codecsSelectableCapabilities = codecStatus.getCodecsSelectableCapabilities();
        int length = codecsSelectableCapabilities.length;
        int i5 = 0;
        while (i5 < length) {
            BluetoothCodecConfig bluetoothCodecConfig3 = codecsSelectableCapabilities[i5];
            if (i == bluetoothCodecConfig3.getCodecType()) {
                if ((i == 10 || i == 9 || i == 11) && bluetoothCodecConfig3.getCodecSpecific3() == 1) {
                    i3 = length;
                    i4 = i5;
                    bluetoothCodecConfig = new BluetoothCodecConfig(i, i2, bluetoothCodecConfig3.getSampleRate(), bluetoothCodecConfig3.getBitsPerSample(), bluetoothCodecConfig3.getChannelMode(), bluetoothCodecConfig3.getCodecSpecific1(), bluetoothCodecConfig3.getCodecSpecific2(), 0L, bluetoothCodecConfig3.getCodecSpecific4());
                } else {
                    i3 = length;
                    i4 = i5;
                    bluetoothCodecConfig = new BluetoothCodecConfig(i, i2, bluetoothCodecConfig3.getSampleRate(), bluetoothCodecConfig3.getBitsPerSample(), bluetoothCodecConfig3.getChannelMode(), bluetoothCodecConfig3.getCodecSpecific1(), bluetoothCodecConfig3.getCodecSpecific2(), bluetoothCodecConfig3.getCodecSpecific3(), bluetoothCodecConfig3.getCodecSpecific4());
                }
                bluetoothCodecConfig2 = bluetoothCodecConfig;
            } else {
                i3 = length;
                i4 = i5;
            }
            i5 = i4 + 1;
            length = i3;
        }
        return bluetoothCodecConfig2;
    }

    private LocalBluetoothProfile getProfileOf(Preference preference) {
        if ((preference instanceof CheckBoxPreference) && !TextUtils.isEmpty(preference.getKey())) {
            try {
                return this.mProfileManager.getProfileByName(preference.getKey());
            } catch (IllegalArgumentException unused) {
                return null;
            }
        }
        return null;
    }

    private int getProfilePreferenceIndex(int i) {
        return this.mProfileContainer.getOrder() + (i * 10);
    }

    private void getProfileProxy() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            return;
        }
        defaultAdapter.getProfileProxy(getActivity().getApplicationContext(), this.mBluetoothA2dpServiceListener, 2);
        defaultAdapter.getProfileProxy(getActivity().getApplicationContext(), this.mBluetoothHfpServiceListener, 1);
    }

    private void handleAudioShareConfigStatus(boolean z) {
        BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) getPreferenceScreen().findPreference("audio_share_volume_pre");
        if (bluetoothVolumeSeekBarPreference != null) {
            bluetoothVolumeSeekBarPreference.setEnabled(z);
        } else {
            Log.d("DeviceProfilesSettings", "BluetoothVolumeSeekBarPreference == null");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAudioShareVolume() {
        int progress = ((BluetoothVolumeSeekBarPreference) getPreferenceScreen().findPreference("audio_share_volume_pre")).getProgress();
        Log.d("DeviceProfilesSettings", "SeekBarPreference value = " + progress);
        broadcastMultiA2dpVolumChange(this.mCachedDevice.getDevice(), progress);
        Settings.Secure.putString(getContext().getContentResolver(), "miui_bluetooth_audio_share_volume", String.valueOf(progress));
    }

    private void handleCheckA2DPActiveExist() {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        this.mHandler.postDelayed(this.mAudioShareCheckA2DPActiveExistRunnable, 50L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCheckBoxPreferenceEnabled(CheckBoxPreference checkBoxPreference) {
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(false);
            if (this.mHandler == null) {
                this.mHandler = new Handler();
            }
            if (!"le_audio_pre".equals(checkBoxPreference.getKey()) || !LocalBluetoothProfileManager.isTbsProfileEnabled() || !this.mCachedDevice.isDualModeDevice()) {
                this.mHandler.postDelayed(this.mRunnable, 3000L);
                return;
            }
            this.mLC3Switching = true;
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.11
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        DeviceProfilesSettings.this.mLC3Switching = false;
                        Log.d("DeviceProfilesSettings", "leAudioPre: Timeout to set mLC3Switching false");
                        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) DeviceProfilesSettings.this.getPreferenceScreen().findPreference("le_audio_pre");
                        if (checkBoxPreference2 != null) {
                            if ((DeviceProfilesSettings.this.mCachedDevice.getLeAudioStatus() != 1 && !DeviceProfilesSettings.this.isHfpConnected()) || DeviceProfilesSettings.this.isSCOOn() || DeviceProfilesSettings.this.isLeAudioCgOn() || DeviceProfilesSettings.this.isSingleHeadsetConn) {
                                return;
                            }
                            checkBoxPreference2.setEnabled(true);
                            Log.d("DeviceProfilesSettings", "leAudioPre: Timeout to enable LC3 Pref");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000L);
            Log.d("DeviceProfilesSettings", "leAudioPre: Delay 2s to enable LC3 Pref");
        }
    }

    private void handleDelayOpenAudioShare() {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        this.mHandler.postDelayed(this.mDelayOpenAudioShareRunnable, 200L);
    }

    private void handleDisableVolume() {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        this.mHandler.postDelayed(this.mDisableVolumeRun, 300L);
    }

    private void handleHeadSetConnect() {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        this.mHandler.postDelayed(this.mCodecConfigRun, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMultiA2DPState(int i) {
        Log.d("DeviceProfilesSettings", "handleMultiA2DPState = " + i);
        if (i == 0 || i == 1) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("audio_share_switch_pre");
            BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) getPreferenceScreen().findPreference("audio_share_volume_pre");
            CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) getPreferenceScreen().findPreference("ldac_pre");
            CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) getPreferenceScreen().findPreference("latency_pre");
            boolean z = false;
            if (i == 1) {
                if (checkBoxPreference != null) {
                    checkBoxPreference.setEnabled(true);
                    checkBoxPreference.setChecked(true);
                    this.mCachedDevice.setSpecificCodecStatus("AUDIO_SHARE_SWITCH", 1);
                }
                if (bluetoothVolumeSeekBarPreference != null) {
                    bluetoothVolumeSeekBarPreference.setVisible(true);
                    bluetoothVolumeSeekBarPreference.setEnabled(true);
                }
                String string = Settings.Secure.getString(getActivity().getContentResolver(), "miui_store_audio_share_device_address");
                if (checkBoxPreference2 != null && (this.mCachedDevice.isActiveDevice(2) || (string != null && this.mCachedDevice.getAddress().equals(string)))) {
                    checkBoxPreference2.setEnabled(false);
                }
                if (checkBoxPreference3 != null) {
                    checkBoxPreference3.setEnabled(false);
                }
                Log.d("DeviceProfilesSettings", "handleMultiA2DPState enabled");
            } else if (i == 0) {
                if (checkBoxPreference != null) {
                    checkBoxPreference.setChecked(false);
                    checkBoxPreference.setEnabled(true);
                    this.mCachedDevice.setSpecificCodecStatus("AUDIO_SHARE_SWITCH", 0);
                }
                if (bluetoothVolumeSeekBarPreference != null) {
                    bluetoothVolumeSeekBarPreference.setProgress(50);
                    bluetoothVolumeSeekBarPreference.setEnabled(false);
                    bluetoothVolumeSeekBarPreference.setVisible(false);
                    this.mCachedDevice.setSpecificCodecStatus("audio_share_volume_pre", 50);
                }
                if (checkBoxPreference2 != null) {
                    checkBoxPreference2.setEnabled(true);
                }
                if (this.mLHDCV3Device && this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") == 1) {
                    z = true;
                }
                if (checkBoxPreference3 != null) {
                    if (this.mLHDCV3Device) {
                        checkBoxPreference3.setEnabled(z);
                    } else {
                        checkBoxPreference3.setEnabled(true);
                    }
                }
                Log.d("DeviceProfilesSettings", "handleMultiA2DPState disabled");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMultiLeDevices() {
        BluetoothDevice remoteDevice;
        Context context = getContext();
        String string = Settings.Global.getString(context.getContentResolver(), "three_mac_for_ble_f");
        if (string == null || string.length() <= 54) {
            Log.i("DeviceProfilesSettings", "handleMultiLeDevices is not need");
            return;
        }
        int i = 0;
        while (i < string.length() / 54) {
            int i2 = i + 1;
            if (string.length() >= (i2 * 54) - 1) {
                int i3 = i * 54;
                String substring = string.substring(i3, i3 + 17);
                if (substring != null && substring.length() == 17) {
                    Log.i("DeviceProfilesSettings", "handleMultiLeDevices brMac is " + substring);
                    if (substring.equalsIgnoreCase(this.mDeviceMacAddress)) {
                        Log.i("DeviceProfilesSettings", "ignore oneself");
                    } else {
                        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (defaultAdapter != null && (remoteDevice = defaultAdapter.getRemoteDevice(substring)) != null) {
                            CachedBluetoothDevice findDevice = this.mManager.getCachedDeviceManager().findDevice(remoteDevice);
                            if (findDevice == null) {
                                Log.i("DeviceProfilesSettings", "mCachedDevice is null and new one ");
                                findDevice = new CachedBluetoothDevice(context, this.mProfileManager, remoteDevice);
                            }
                            Log.i("DeviceProfilesSettings", "mLeCachedDevice mac is " + findDevice.getAddress());
                            int i4 = i3 + 36;
                            String substring2 = string.substring(i3 + 18, i4 + (-1));
                            String substring3 = string.substring(i4, i3 + 54 + (-1));
                            Log.i("DeviceProfilesSettings", "handleMultiLeDevices leStr1 is " + substring2 + " leStr2 is " + substring3);
                            BluetoothDevice remoteDevice2 = defaultAdapter.getRemoteDevice(substring2);
                            BluetoothDevice remoteDevice3 = defaultAdapter.getRemoteDevice(substring3);
                            if (remoteDevice2 != null && remoteDevice2.getBondState() != 10) {
                                if (remoteDevice2.getBondState() == 11) {
                                    remoteDevice2.cancelBondProcess();
                                } else {
                                    remoteDevice2.removeBond();
                                }
                                Log.i("DeviceProfilesSettings", "handleMultiLeDevices remove bond leStr1");
                            }
                            if (remoteDevice3 != null && remoteDevice3.getBondState() != 10) {
                                if (remoteDevice3.getBondState() == 11) {
                                    remoteDevice3.cancelBondProcess();
                                } else {
                                    remoteDevice3.removeBond();
                                }
                                Log.i("DeviceProfilesSettings", "handleMultiLeDevices remove bond leStr2");
                            }
                            findDevice.setSpecificCodecStatus("LEAUDIO", 2);
                        }
                    }
                }
            }
            i = i2;
        }
    }

    private void hideSoftInput(EditText editText) {
        if (editText == null) {
            return;
        }
        if (this.mInputManager == null) {
            this.mInputManager = (InputMethodManager) getSystemService("input_method");
        }
        this.mInputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private boolean isDeviceInListForAbsoluteVolume(String str, String str2) {
        String str3 = SystemProperties.get(str2, "");
        if (str3.indexOf(str) == -1) {
            Log.d("DeviceProfilesSettings", "can't find " + str + " in " + str3);
            return false;
        }
        Log.d("DeviceProfilesSettings", "device " + str + " is in list " + str3);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDeviceInListForAudioRepair(String str, String str2) {
        String str3 = SystemProperties.get(str2, "");
        if (str3.indexOf(str.toLowerCase()) == -1) {
            Log.d("DeviceProfilesSettings", "can't find " + str + " in " + str3);
            return false;
        }
        Log.d("DeviceProfilesSettings", "device " + str + " is in list " + str3);
        return true;
    }

    private boolean isDeviceRenameDialogShowing() {
        EditText editText = this.mEtDeviceRename;
        return editText != null && editText.getVisibility() == 0;
    }

    private boolean isLeAudioBrDevice(String str) {
        Context context;
        String string;
        String string2;
        boolean isLoggable;
        try {
            context = getContext();
            string = Settings.Global.getString(context.getContentResolver(), "lc3Enable");
            string2 = Settings.Global.getString(context.getContentResolver(), "lc3CGState");
            isLoggable = Log.isLoggable("Lc3TestMode", 2);
        } catch (Exception e) {
            Log.v("DeviceProfilesSettings", "isLeAudioBrDevice Exception " + e);
        }
        if (LocalBluetoothProfileManager.isTbsProfileEnabled()) {
            if (this.mCachedDevice.isDualModeDevice()) {
                Log.d("DeviceProfilesSettings", "cgEnable" + string2);
                return isLoggable || !"false".equals(string2);
            }
            return false;
        }
        String string3 = Settings.Global.getString(context.getContentResolver(), "three_mac_for_ble_f");
        if (string3 != null && string3.contains(str)) {
            Log.i("DeviceProfilesSettings", "device isLeAudioBrDevice");
            if ((isLoggable || (string != null && string.equals("true"))) && !Build.IS_INTERNATIONAL_BUILD) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLeAudioCgOn() {
        AudioManager audioManager;
        try {
            if (!LocalBluetoothProfileManager.isTbsProfileEnabled() || (audioManager = this.mAudioManager) == null) {
                return false;
            }
            return audioManager.isBluetoothScoOn();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSCOOn() {
        BluetoothDevice device = this.mCachedDevice.getDevice();
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHfp;
        return bluetoothHeadset != null && bluetoothHeadset.isAudioConnected(device);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(EditText editText) {
        if (!TextUtils.isEmpty(editText.getText())) {
            editText.setSelection(editText.length());
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        showSoftInput(editText);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(final EditText editText) {
        this.mEtDeviceRename = editText;
        editText.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DeviceProfilesSettings.this.lambda$onCreate$0(editText);
            }
        }, 150L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$2() {
        if (isDeviceRenameDialogShowing()) {
            if (!TextUtils.isEmpty(this.mEtDeviceRename.getText())) {
                EditText editText = this.mEtDeviceRename;
                editText.setSelection(editText.length());
            }
            this.mEtDeviceRename.setFocusable(true);
            this.mEtDeviceRename.setFocusableInTouchMode(true);
            this.mEtDeviceRename.requestFocus();
            showSoftInput(this.mEtDeviceRename);
        }
    }

    private void onAbsVolumePrefClicked(CheckBoxPreference checkBoxPreference) {
        try {
            String str = "";
            BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
            if (bluetoothA2dp != null && bluetoothA2dp.getActiveDevice() != null) {
                str = this.mBluetoothA2dp.getActiveDevice().getAddress();
            }
            Log.v("DeviceProfilesSettings", "mDeviceMacAddress is " + this.mDeviceMacAddress + " activeMac is " + str);
            if (this.mBluetoothA2dp == null || str == null || !this.mDeviceMacAddress.equals(str)) {
                return;
            }
            this.mCachedDevice.setSpecificCodecStatus("ABSOLUTEVOLUMEOPERATE", 3);
            if (checkBoxPreference.isChecked()) {
                closeAbsVolume();
            } else {
                createDialogForOpenAbsVolume();
            }
        } catch (Exception e) {
            Log.v("DeviceProfilesSettings", "onAbsVolumePrefClicked failed ", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAudioShareSwitchPrefClicked(CheckBoxPreference checkBoxPreference) {
        if (checkBoxPreference == null) {
            Log.d("DeviceProfilesSettings", "CheckBoxPreference pref == null");
            return;
        }
        String string = Settings.Secure.getString(getActivity().getContentResolver(), "miui_store_audio_share_device_address");
        BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) getPreferenceScreen().findPreference("audio_share_volume_pre");
        if (this.mBluetoothA2dp != null) {
            if (checkBoxPreference.isChecked()) {
                Log.d("DeviceProfilesSettings", "KEY_STORE_AUDIO_SHARE_DEVICE = " + string);
                checkBoxPreference.setChecked(false);
                broadcastMultiA2dpStateChange(null, 0);
                handleAudioShareConfigStatus(false);
                Log.d("DeviceProfilesSettings", "CheckBoxPreference = unchecked");
            } else {
                if (this.mBluetoothA2dp.getConnectionState(this.mCachedDevice.getDevice()) == 2) {
                    String address = this.mCachedDevice.getAddress();
                    Log.d("DeviceProfilesSettings", "cachedDeviceAddress = " + address);
                    Log.d("DeviceProfilesSettings", "KEY_STORE_AUDIO_SHARE_DEVICE = " + string);
                    int i = (string == null || string.isEmpty() || string.equals("pending") || string.equals(address)) ? 1 : 2;
                    checkBoxPreference.setChecked(true);
                    broadcastMultiA2dpStateChange(this.mCachedDevice.getDevice(), i);
                }
            }
            checkBoxPreference.setEnabled(false);
            bluetoothVolumeSeekBarPreference.setEnabled(false);
            CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) getPreferenceScreen().findPreference("ldac_pre");
            CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) getPreferenceScreen().findPreference("latency_pre");
            if (checkBoxPreference2 != null) {
                checkBoxPreference2.setEnabled(false);
            }
            if (checkBoxPreference3 != null) {
                checkBoxPreference3.setEnabled(false);
            }
        }
    }

    private void onLeAudioPrefClicked(CheckBoxPreference checkBoxPreference) {
        try {
            if (LocalBluetoothProfileManager.isTbsProfileEnabled()) {
                if (this.mCachedDevice.isDualModeDevice()) {
                    if (!checkBoxPreference.isChecked()) {
                        createDialogForLeAudio(checkBoxPreference);
                        return;
                    }
                    closeLeAudio();
                    refreshProfiles();
                    handleCheckBoxPreferenceEnabled(checkBoxPreference);
                    return;
                }
                return;
            }
            BluetoothHeadset bluetoothHeadset = this.mBluetoothHfp;
            if (bluetoothHeadset != null) {
                String address = bluetoothHeadset.getActiveDevice() != null ? this.mBluetoothHfp.getActiveDevice().getAddress() : "";
                Log.v("DeviceProfilesSettings", "mDeviceMacAddress is " + this.mDeviceMacAddress + " activeMac is " + address);
                if (address == null || !this.mDeviceMacAddress.equals(address)) {
                    return;
                }
                if (!checkBoxPreference.isChecked()) {
                    createDialogForLeAudio(checkBoxPreference);
                    return;
                }
                closeLeAudio();
                handleCheckBoxPreferenceEnabled(checkBoxPreference);
            }
        } catch (Exception e) {
            Log.v("DeviceProfilesSettings", "onLeAudioPrefClicked failed ", e);
        }
    }

    private void onPrefClicked(CheckBoxPreference checkBoxPreference) {
        if (this.mBluetoothA2dp != null) {
            if (!checkBoxPreference.isChecked()) {
                if (this.mBluetoothA2dp.getConnectionState(this.mCachedDevice.getDevice()) == 2) {
                    createDialog();
                    return;
                }
                return;
            }
            if (this.mBluetoothA2dp.getConnectionState(this.mCachedDevice.getDevice()) == 2) {
                writeBluetoothA2dpConfiguration(false);
                checkBoxPreference.setChecked(false);
                CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) getPreferenceScreen().findPreference("latency_pre");
                if (checkBoxPreference2 != null) {
                    checkBoxPreference2.setChecked(false);
                    checkBoxPreference2.setEnabled(false);
                }
                if (this.mLHDCV3Device) {
                    this.mCachedDevice.setSpecificCodecStatus("LHDC_V3", 0);
                    this.mCachedDevice.setSpecificCodecStatus("latency_val", 0);
                } else if (this.mLHDCV2Device) {
                    this.mCachedDevice.setSpecificCodecStatus("LHDC_V2", 0);
                    this.mCachedDevice.setSpecificCodecStatus("latency_val", 0);
                } else if (this.mLHDCV1Device) {
                    this.mCachedDevice.setSpecificCodecStatus("LHDC_V1", 0);
                    this.mCachedDevice.setSpecificCodecStatus("latency_val", 0);
                } else if (this.mLDACDevice) {
                    this.mCachedDevice.setSpecificCodecStatus("LDAC", 0);
                } else if (this.mAACDevice) {
                    this.mCachedDevice.setSpecificCodecStatus("AAC", 0);
                }
            }
            handleCheckBoxPreferenceEnabled(checkBoxPreference);
        }
    }

    private void onProfileClicked(LocalBluetoothProfile localBluetoothProfile, CheckBoxPreference checkBoxPreference) {
        BluetoothDevice device = this.mCachedDevice.getDevice();
        if (PbapServerProfile.NAME.equals(checkBoxPreference.getKey())) {
            int i = this.mCachedDevice.getPhonebookPermissionChoice() == 1 ? 2 : 1;
            this.mCachedDevice.setPhonebookPermissionChoice(i);
            checkBoxPreference.setChecked(i == 1);
            PbapServerProfile pbapProfile = this.mManager.getProfileManager().getPbapProfile();
            int connectionStatus = pbapProfile.getConnectionStatus(device);
            if (connectionStatus == 2) {
                pbapProfile.setEnabled(device, false);
                return;
            } else if (connectionStatus == 0) {
                pbapProfile.setEnabled(device, true);
                return;
            } else {
                return;
            }
        }
        boolean z = localBluetoothProfile.getConnectionStatus(device) == 2;
        if (!checkBoxPreference.isChecked()) {
            if (localBluetoothProfile instanceof MapProfile) {
                this.mCachedDevice.setMessagePermissionChoice(1);
            }
            if (!localBluetoothProfile.isEnabled(device)) {
                this.mCachedDevice.connectProfile(localBluetoothProfile);
            } else if (localBluetoothProfile instanceof PanProfile) {
                this.mCachedDevice.connectProfile(localBluetoothProfile);
            } else {
                localBluetoothProfile.setEnabled(device, false);
            }
            refreshProfilePreference(checkBoxPreference, localBluetoothProfile);
        } else if (z) {
            askDisconnect(getActivity(), localBluetoothProfile);
        } else {
            localBluetoothProfile.setEnabled(device, false);
            if (localBluetoothProfile instanceof MapProfile) {
                this.mCachedDevice.setMessagePermissionChoice(2);
            }
            CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference(localBluetoothProfile.toString());
            if (checkBoxPreference2 != null) {
                refreshProfilePreference(checkBoxPreference2, localBluetoothProfile);
            }
        }
    }

    private void refresh() {
        String name = this.mCachedDevice.getName();
        this.mDeviceNamePref.setSummary(name);
        this.mDeviceNamePref.setText(name);
        refreshProfiles();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshProfilePreference(CheckBoxPreference checkBoxPreference, LocalBluetoothProfile localBluetoothProfile) {
        BluetoothDevice device = this.mCachedDevice.getDevice();
        checkBoxPreference.setEnabled(!this.mCachedDevice.isBusy());
        if (localBluetoothProfile instanceof MapProfile) {
            checkBoxPreference.setChecked(this.mCachedDevice.getMessagePermissionChoice() == 1);
        } else if (localBluetoothProfile instanceof PbapServerProfile) {
            checkBoxPreference.setChecked(this.mCachedDevice.getPhonebookPermissionChoice() == 1);
        } else if (localBluetoothProfile instanceof PanProfile) {
            checkBoxPreference.setChecked(localBluetoothProfile.getConnectionStatus(device) == 2);
        } else {
            Log.d("DeviceProfilesSettings", "refreshProfilePreference profile is connected: " + localBluetoothProfile.isEnabled(device));
            checkBoxPreference.setChecked(localBluetoothProfile.isEnabled(device));
            if (localBluetoothProfile.toString().equals("BCProfile")) {
                boolean isEnabled = localBluetoothProfile.isEnabled(this.mCachedDevice.getDevice());
                boolean isConnected = this.mCachedDevice.isConnected();
                checkBoxPreference.setChecked(isEnabled && isConnected);
                Log.d("DeviceProfilesSettings", "bc profile enable disable it : " + isConnected);
                Preference findPreference = findPreference("bleAudioBroadcastAdd");
                if (findPreference != null) {
                    findPreference.setEnabled(isConnected && isEnabled);
                }
            }
            if (LocalBluetoothProfileManager.isTbsProfileEnabled() && this.mCachedDevice.isDualModeDevice()) {
                try {
                    String string = Settings.Global.getString(getContext().getContentResolver(), this.mDeviceMacAddress);
                    CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) getPreferenceScreen().findPreference("le_audio_pre");
                    if (this.mCachedDevice.getLeAudioStatus() != 0) {
                        checkBoxPreference.setEnabled(false);
                    } else {
                        checkBoxPreference.setEnabled(true);
                    }
                    if ((localBluetoothProfile instanceof HeadsetProfile) && checkBoxPreference2 != null) {
                        if (localBluetoothProfile.getConnectionStatus(device) != 2 && this.mCachedDevice.getLeAudioStatus() != 1) {
                            checkBoxPreference2.setEnabled(false);
                            Log.d("DeviceProfilesSettings", "leAudioPre.setEnabled(false) when HFP is unavailable");
                        }
                        if ((localBluetoothProfile.getConnectionStatus(device) == 2 || this.mCachedDevice.getLeAudioStatus() == 1) && !isSCOOn() && !isLeAudioCgOn() && !this.isSingleHeadsetConn && !this.mLC3Switching) {
                            checkBoxPreference2.setEnabled(true);
                            Log.d("DeviceProfilesSettings", "leAudioPre.setEnabled(true) when HFP/LEAuido is available");
                        }
                    }
                    if (checkBoxPreference2 == null || TextUtils.isEmpty(string) || string.length() < 2) {
                        this.isSingleHeadsetConn = false;
                    } else {
                        char charAt = string.charAt(0);
                        char charAt2 = string.charAt(1);
                        if ((charAt == '0' && charAt2 == '1') || (charAt == '1' && charAt2 == '0')) {
                            checkBoxPreference2.setEnabled(false);
                            Log.d("DeviceProfilesSettings", "leAudioPre.setEnabled(false) when power 01 or 10");
                            this.isSingleHeadsetConn = true;
                        } else {
                            this.isSingleHeadsetConn = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        checkBoxPreference.setSummary(localBluetoothProfile.getSummaryResourceForDevice(device));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshProfiles() {
        Preference findPreference;
        for (LocalBluetoothProfile localBluetoothProfile : this.mCachedDevice.getConnectableProfiles()) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(localBluetoothProfile.toString());
            if (checkBoxPreference == null) {
                CheckBoxPreference createProfilePreference = createProfilePreference(localBluetoothProfile);
                if (localBluetoothProfile.toString().equals("BCProfile")) {
                    Log.d("DeviceProfilesSettings", "refreshProfiles Device support ble audio !");
                    boolean z = false;
                    if (SystemProperties.getBoolean("persist.vendor.service.bt.lea_test", false)) {
                        if (this.mBleAudioCategory != null) {
                            boolean isEnabled = localBluetoothProfile.isEnabled(this.mCachedDevice.getDevice());
                            boolean isConnected = this.mCachedDevice.isConnected();
                            Log.d("DeviceProfilesSettings", "refreshProfiles mBleAudioCategory not null add to show ! connet state: " + isConnected + " profile enabled: " + isEnabled);
                            createProfilePreference.setOrder(1);
                            createProfilePreference.setChecked(isEnabled && isConnected);
                            this.mBleAudioCategory.addPreference(createProfilePreference);
                            getPreferenceScreen().addPreference(this.mBleAudioCategory);
                            Preference findPreference2 = findPreference("bleAudioBroadcastAdd");
                            if (findPreference2 != null) {
                                if (isConnected && isEnabled) {
                                    z = true;
                                }
                                findPreference2.setEnabled(z);
                            }
                        } else {
                            Log.d("DeviceProfilesSettings", "refreshProfiles mBleAudioCategory is null do nothing and return!");
                        }
                    }
                } else {
                    this.mProfileContainer.addPreference(createProfilePreference);
                }
            } else {
                refreshProfilePreference(checkBoxPreference, localBluetoothProfile);
            }
        }
        for (LocalBluetoothProfile localBluetoothProfile2 : this.mCachedDevice.getRemovedProfiles()) {
            if (!PbapServerProfile.NAME.equals(localBluetoothProfile2.toString()) && (findPreference = findPreference(localBluetoothProfile2.toString())) != null) {
                Log.d("DeviceProfilesSettings", "Removing " + localBluetoothProfile2.toString() + " from profile list");
                this.mProfileContainer.removePreference(findPreference);
            }
        }
        showOrHideProfileGroup();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBroadcastEnableOrDisable(boolean z) {
        Log.v("DeviceProfilesSettings", "sendBroadcastEnableOrDisable enter and value is " + z);
        try {
            Intent intent = new Intent("miui.bluetooth.absolute_volume_enable_disable");
            intent.setPackage("com.android.bluetooth");
            intent.putExtra("absolute_volume_mac", this.mCachedDevice.getAddress());
            intent.putExtra("absolute_volume_value", z);
            getActivity().sendBroadcast(intent);
        } catch (Exception e) {
            Log.v("DeviceProfilesSettings", "send msg failed ", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sentIgnorePairDilogIntent(String str, String str2) {
        if (str == null) {
            str = "00:00:00:00:00:00";
        }
        if (str2 == null) {
            str2 = "00:00:00:00:00:00";
        }
        Context context = getContext();
        Settings.Global.putLong(context.getContentResolver(), "fast_connect_show_dialog", System.currentTimeMillis());
        Intent intent = new Intent("miui.bluetooth.FAST_CONNECT_DEVICE_BOND");
        intent.putExtra("FAST_CONNECT_CURRENT_DEVICE", str);
        intent.putExtra("FAST_CONNECT_PEER_DEVICE", str2);
        intent.putExtra("android.intent.extra.PACKAGE_NAME", "com.xiaomi.bluetooth");
        intent.setPackage("com.android.bluetooth");
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
        Log.i("DeviceProfilesSettings", "sentIgnorePairDilogIntent leMac1 is " + str + " leMac2 is " + str2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAudioShareVolume(int i) {
        BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) getPreferenceScreen().findPreference("audio_share_volume_pre");
        if (bluetoothVolumeSeekBarPreference == null) {
            Log.d("DeviceProfilesSettings", "BluetoothVolumeSeekBarPreference == null");
            return;
        }
        bluetoothVolumeSeekBarPreference.setProgress(i);
        Log.d("DeviceProfilesSettings", "setAudioShareVolume as: " + i);
    }

    private void showOrHideProfileGroup() {
        int preferenceCount = this.mProfileContainer.getPreferenceCount();
        boolean z = this.mProfileGroupIsRemoved;
        if (!z && preferenceCount == 0) {
            getPreferenceScreen().removePreference(this.mProfileContainer);
            this.mProfileGroupIsRemoved = true;
        } else if (!z || preferenceCount == 0) {
        } else {
            getPreferenceScreen().addPreference(this.mProfileContainer);
            this.mProfileGroupIsRemoved = false;
        }
    }

    private void showSoftInput(EditText editText) {
        if (editText == null) {
            return;
        }
        if (this.mInputManager == null) {
            this.mInputManager = (InputMethodManager) getSystemService("input_method");
        }
        editText.requestFocus();
        this.mInputManager.showSoftInput(editText, 0);
    }

    private void unpairDevice() {
        this.mCachedDevice.unpair();
        boolean equals = "mediatek".equals(FeatureParser.getString("vendor"));
        boolean equals2 = "qcom".equals(FeatureParser.getString("vendor"));
        this.mManager.getCachedDeviceManager().removeDevice(this.mCachedDevice);
        if (this.mLHDCV3Device) {
            writeCodecUserConfigureToProperty(true);
            this.mCachedDevice.setSupportedCodec(this.mDeviceMacAddress, "LHDC_V3", false);
            this.mCachedDevice.setSpecificCodecStatus("LHDC_V3", 2);
            this.mCachedDevice.setDialogChoice(this.mDeviceMacAddress, 2);
            this.mCachedDevice.setSpecificCodecStatus("latency_pre", 2);
            this.mCachedDevice.setSpecificCodecStatus("latency_val", 2);
        } else if (this.mLHDCV2Device) {
            writeCodecUserConfigureToProperty(true);
            this.mCachedDevice.setSupportedCodec(this.mDeviceMacAddress, "LHDC_V2", false);
            this.mCachedDevice.setSpecificCodecStatus("LHDC_V2", 2);
            this.mCachedDevice.setDialogChoice(this.mDeviceMacAddress, 2);
            this.mCachedDevice.setSpecificCodecStatus("latency_pre", 2);
            this.mCachedDevice.setSpecificCodecStatus("latency_val", 2);
        } else if (this.mLHDCV1Device) {
            writeCodecUserConfigureToProperty(true);
            this.mCachedDevice.setSupportedCodec(this.mDeviceMacAddress, "LHDC_V1", false);
            this.mCachedDevice.setSpecificCodecStatus("LHDC_V1", 2);
            this.mCachedDevice.setDialogChoice(this.mDeviceMacAddress, 2);
            this.mCachedDevice.setSpecificCodecStatus("latency_pre", 2);
            this.mCachedDevice.setSpecificCodecStatus("latency_val", 2);
        } else if (this.mLDACDevice) {
            writeCodecUserConfigureToProperty(true);
            this.mCachedDevice.setSupportedCodec(this.mDeviceMacAddress, "LDAC", false);
            this.mCachedDevice.setSpecificCodecStatus("LDAC", 2);
            this.mCachedDevice.setDialogChoice(this.mDeviceMacAddress, 2);
        } else if (this.mAADevice) {
            this.mCachedDevice.setSpecificCodecStatus("aptX Adaptive", 2);
            this.mCachedDevice.setSupportedCodec(this.mDeviceMacAddress, "aptX Adaptive", false);
            this.mCachedDevice.setSpecificCodecStatus("latency_pre", 2);
            this.mCachedDevice.setSpecificCodecStatus("latency_val", 2);
            this.mCachedDevice.setSpecificCodecStatus("aptxadaptive_video", 2);
        } else if (this.mAACDevice) {
            writeCodecUserConfigureToProperty(false);
            this.mCachedDevice.setDialogChoice(this.mDeviceMacAddress, 2);
            this.mCachedDevice.setSpecificCodecStatus("AAC", 2);
            this.mCachedDevice.setSupportedCodec(this.mDeviceMacAddress, "AAC", false);
            this.mCachedDevice.setSpecificCodecStatus("latency_pre", 2);
            this.mCachedDevice.setSpecificCodecStatus("latency_val", 2);
            this.mCachedDevice.setSpecificCodecStatus("zmi_latency", 2);
        } else if (this.mSBCLlDevice) {
            this.mCachedDevice.setSpecificCodecStatus("latency_pre", 2);
            this.mCachedDevice.setSpecificCodecStatus("latency_val", 2);
            this.mCachedDevice.setSpecificCodecStatus("zmi_latency", 2);
        }
        try {
            this.mCachedDevice.setSpecificCodecStatus("ABSOLUTEVOLUME", 2);
            this.mCachedDevice.setSpecificCodecStatus("ABSOLUTEVOLUMEOPERATE", 2);
            if (LocalBluetoothProfileManager.isTbsProfileEnabled()) {
                this.mCachedDevice.setLeAudioStatus(0);
            } else {
                this.mCachedDevice.setSpecificCodecStatus("LEAUDIO", 2);
            }
            if (equals) {
                delFromWhiteListForAbsoluteVolume("persist.vendor.bluetooth.a2dp.absolute.volume.whitelistall");
            } else if (equals2) {
                delFromWhiteListForAbsoluteVolume("persist.vendor.bt.a2dp.absolute.volume.whitelistall");
            } else {
                Log.v("DeviceProfilesSettings", "no work to do");
            }
            unpairLeAudio();
            deleteSaveMacForLeAudio();
        } catch (Exception e) {
            Log.w("DeviceProfilesSettings", "delFromWhiteListForAbsoluteVolume failed " + e);
        }
    }

    private void unpairLeAudio() {
        String str;
        int indexOf;
        int indexOf2;
        Context context = getContext();
        if (LocalBluetoothProfileManager.isTbsProfileEnabled() && this.mCachedDevice.isDualModeDevice()) {
            this.mCachedDevice.unpair();
            return;
        }
        String string = Settings.Global.getString(context.getContentResolver(), "three_mac_for_ble_f");
        String str2 = "00:00:00:00:00:00";
        if (string == null || string.length() < (indexOf2 = (indexOf = string.indexOf(this.mDeviceMacAddress)) + 53) || !string.contains(this.mDeviceMacAddress)) {
            str = "00:00:00:00:00:00";
        } else {
            Log.i("DeviceProfilesSettings", "startIndex is " + indexOf + " value is " + string);
            str2 = string.substring(indexOf + 18, indexOf + 35);
            str = string.substring(indexOf + 36, indexOf2);
            Log.i("DeviceProfilesSettings", "leStr1 is " + str2 + " leStr2 is " + str);
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            BluetoothDevice remoteDevice = defaultAdapter.getRemoteDevice(str2);
            BluetoothDevice remoteDevice2 = defaultAdapter.getRemoteDevice(str);
            if (remoteDevice != null && remoteDevice.getBondState() != 10) {
                if (remoteDevice.getBondState() == 11) {
                    remoteDevice.cancelBondProcess();
                } else {
                    remoteDevice.removeBond();
                }
                Log.i("DeviceProfilesSettings", "remove bond leStr1");
            }
            if (remoteDevice2 == null || remoteDevice2.getBondState() == 10) {
                return;
            }
            if (remoteDevice2.getBondState() == 11) {
                remoteDevice2.cancelBondProcess();
            } else {
                remoteDevice2.removeBond();
            }
            Log.i("DeviceProfilesSettings", "remove bond leStr2");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCodecStatus() {
        BluetoothCodecConfig codecConfig;
        Log.d("DeviceProfilesSettings", "updateCodecStatus()");
        "mediatek".equals(FeatureParser.getString("vendor"));
        if (this.mCachedDevice == null) {
            return;
        }
        synchronized (this.mBluetoothA2dpLock) {
            BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
            if (bluetoothA2dp != null) {
                BluetoothCodecStatus codecStatus = bluetoothA2dp.getCodecStatus(this.mCachedDevice.getDevice());
                codecConfig = codecStatus != null ? codecStatus.getCodecConfig() : null;
            } else {
                this.mUpdatePrefForA2DPConnected = true;
            }
        }
        CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        if (!cachedBluetoothDevice.isSupportedCodec(cachedBluetoothDevice.getAddress(), "LDAC")) {
            CachedBluetoothDevice cachedBluetoothDevice2 = this.mCachedDevice;
            if (!cachedBluetoothDevice2.isSupportedCodec(cachedBluetoothDevice2.getAddress(), "LHDC_V3")) {
                CachedBluetoothDevice cachedBluetoothDevice3 = this.mCachedDevice;
                if (!cachedBluetoothDevice3.isSupportedCodec(cachedBluetoothDevice3.getAddress(), "LHDC_V2")) {
                    CachedBluetoothDevice cachedBluetoothDevice4 = this.mCachedDevice;
                    if (!cachedBluetoothDevice4.isSupportedCodec(cachedBluetoothDevice4.getAddress(), "LHDC_V1")) {
                        CachedBluetoothDevice cachedBluetoothDevice5 = this.mCachedDevice;
                        if (!cachedBluetoothDevice5.isSupportedCodec(cachedBluetoothDevice5.getAddress(), "AAC")) {
                            return;
                        }
                    }
                }
            }
        }
        if (codecConfig == null) {
            return;
        }
        if (FeatureParser.getBoolean("support_audio_share", false)) {
            String string = Settings.Secure.getString(getActivity().getContentResolver(), "miui_store_audio_share_device_address");
            Log.d("DeviceProfilesSettings", "updateCodecStatus KEY_STORE_AUDIO_SHARE_DEVICE = " + string);
            if (string != null && ((string.equals("pending") && codecConfig.getCodecType() == 0) || string.equals(this.mCachedDevice.getAddress()))) {
                return;
            }
        }
        CachedBluetoothDevice cachedBluetoothDevice6 = this.mCachedDevice;
        if (cachedBluetoothDevice6.isSupportedCodec(cachedBluetoothDevice6.getAddress(), "LDAC")) {
            this.mCachedDevice.setSpecificCodecStatus("LDAC", "LDAC".equals(codecConfig.getCodecName()) ? 1 : 0);
        } else {
            CachedBluetoothDevice cachedBluetoothDevice7 = this.mCachedDevice;
            if (cachedBluetoothDevice7.isSupportedCodec(cachedBluetoothDevice7.getAddress(), "LHDC_V3")) {
                this.mCachedDevice.setSpecificCodecStatus("LHDC_V3", (codecConfig.getCodecName() == null || !codecConfig.getCodecName().contains("LHDC")) ? 0 : 1);
            } else {
                CachedBluetoothDevice cachedBluetoothDevice8 = this.mCachedDevice;
                if (cachedBluetoothDevice8.isSupportedCodec(cachedBluetoothDevice8.getAddress(), "LHDC_V2")) {
                    this.mCachedDevice.setSpecificCodecStatus("LHDC_V2", (codecConfig.getCodecName() == null || !codecConfig.getCodecName().contains("LHDC")) ? 0 : 1);
                } else {
                    CachedBluetoothDevice cachedBluetoothDevice9 = this.mCachedDevice;
                    if (cachedBluetoothDevice9.isSupportedCodec(cachedBluetoothDevice9.getAddress(), "LHDC_V1")) {
                        this.mCachedDevice.setSpecificCodecStatus("LHDC_V1", (codecConfig.getCodecName() == null || !codecConfig.getCodecName().contains("LHDC")) ? 0 : 1);
                    } else {
                        CachedBluetoothDevice cachedBluetoothDevice10 = this.mCachedDevice;
                        if (cachedBluetoothDevice10.isSupportedCodec(cachedBluetoothDevice10.getAddress(), "AAC")) {
                            this.mCachedDevice.setSpecificCodecStatus("AAC", "AAC".equals(codecConfig.getCodecName()) ? 1 : 0);
                        }
                    }
                }
            }
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("ldac_pre");
        if (checkBoxPreference != null) {
            if (this.mLHDCV3Device) {
                checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") == 1);
            } else if (this.mLHDCV2Device) {
                checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("LHDC_V2") == 1);
            } else if (this.mLHDCV1Device) {
                checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("LHDC_V1") == 1);
            } else if (this.mLDACDevice) {
                checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("LDAC") == 1);
            } else {
                checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("AAC") == 1);
            }
            checkBoxPreference.setEnabled(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x001d, code lost:
    
        if (r7 != false) goto L8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0026, code lost:
    
        if (r7 != false) goto L8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x002e, code lost:
    
        if (r7 != false) goto L8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0012, code lost:
    
        if (r7 != false) goto L8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0014, code lost:
    
        r2 = 1000000;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0015, code lost:
    
        r3 = r2;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void writeBluetoothA2dpConfiguration(boolean r7) {
        /*
            r6 = this;
            android.bluetooth.BluetoothA2dp r0 = r6.mBluetoothA2dp
            if (r0 == 0) goto Lce
            r6.writeCodecUserConfigureToProperty(r7)
            boolean r0 = r6.mLHDCV3Device
            r1 = 0
            r2 = -1
            r3 = 1000000(0xf4240, float:1.401298E-39)
            if (r0 == 0) goto L17
            r0 = 10
            if (r7 == 0) goto L15
        L14:
            r2 = r3
        L15:
            r3 = r2
            goto L38
        L17:
            boolean r0 = r6.mLHDCV2Device
            if (r0 == 0) goto L20
            r0 = 9
            if (r7 == 0) goto L15
            goto L14
        L20:
            boolean r0 = r6.mLHDCV1Device
            if (r0 == 0) goto L29
            r0 = 11
            if (r7 == 0) goto L15
            goto L14
        L29:
            boolean r0 = r6.mLDACDevice
            if (r0 == 0) goto L31
            r0 = 4
            if (r7 == 0) goto L15
            goto L14
        L31:
            boolean r0 = r6.mAACDevice
            if (r0 == 0) goto L37
            r0 = r7
            goto L38
        L37:
            r0 = r1
        L38:
            java.lang.String r2 = "audio"
            java.lang.Object r2 = r6.getSystemService(r2)
            android.media.AudioManager r2 = (android.media.AudioManager) r2
            if (r2 == 0) goto L46
            boolean r1 = r2.isMusicActive()
        L46:
            java.lang.String r2 = "support_ldac"
            r4 = 1
            boolean r2 = miui.util.FeatureParser.getBoolean(r2, r4)
            r2 = r2 ^ r4
            java.lang.String r4 = "mediatek"
            java.lang.String r5 = "vendor"
            java.lang.String r5 = miui.util.FeatureParser.getString(r5)
            boolean r4 = r4.equals(r5)
            if (r7 != 0) goto L83
            boolean r7 = r6.mLDACDevice
            if (r7 == 0) goto L83
            if (r2 == 0) goto L83
            if (r1 == 0) goto L83
            com.android.settingslib.bluetooth.CachedBluetoothDevice r7 = r6.mCachedDevice
            if (r7 == 0) goto L83
            if (r4 != 0) goto L83
            java.lang.String r7 = "DeviceProfilesSettings"
            java.lang.String r0 = "music is playing, reconnect a2dp"
            android.util.Log.d(r7, r0)
            com.android.settingslib.bluetooth.LocalBluetoothProfileManager r7 = r6.mProfileManager
            com.android.settingslib.bluetooth.A2dpProfile r7 = r7.getA2dpProfile()
            com.android.settingslib.bluetooth.CachedBluetoothDevice r0 = r6.mCachedDevice
            r0.disconnect(r7)
            r6.handleHeadSetConnect()
            goto Lce
        L83:
            java.lang.Object r7 = r6.mBluetoothA2dpLock
            monitor-enter(r7)
            android.bluetooth.BluetoothA2dp r1 = r6.mBluetoothA2dp     // Catch: java.lang.Throwable -> Lcb
            android.bluetooth.BluetoothCodecConfig r1 = r6.getCodecConfig(r1, r0, r3)     // Catch: java.lang.Throwable -> Lcb
            if (r1 == 0) goto L9c
            com.android.settingslib.bluetooth.CachedBluetoothDevice r2 = r6.mCachedDevice     // Catch: java.lang.Throwable -> Lcb
            if (r2 == 0) goto L9c
            android.bluetooth.BluetoothA2dp r6 = r6.mBluetoothA2dp     // Catch: java.lang.Throwable -> Lcb
            android.bluetooth.BluetoothDevice r0 = r2.getDevice()     // Catch: java.lang.Throwable -> Lcb
            r6.setCodecConfigPreference(r0, r1)     // Catch: java.lang.Throwable -> Lcb
            goto Lb2
        L9c:
            java.lang.String r6 = "DeviceProfilesSettings"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lcb
            r2.<init>()     // Catch: java.lang.Throwable -> Lcb
            java.lang.String r3 = "Codec is not selectable: "
            r2.append(r3)     // Catch: java.lang.Throwable -> Lcb
            r2.append(r0)     // Catch: java.lang.Throwable -> Lcb
            java.lang.String r0 = r2.toString()     // Catch: java.lang.Throwable -> Lcb
            android.util.Log.w(r6, r0)     // Catch: java.lang.Throwable -> Lcb
        Lb2:
            monitor-exit(r7)     // Catch: java.lang.Throwable -> Lcb
            java.lang.String r6 = "DeviceProfilesSettings"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r0 = "writeBluetoothA2dpConfiguration(): newcodecConfig="
            r7.append(r0)
            r7.append(r1)
            java.lang.String r7 = r7.toString()
            android.util.Log.d(r6, r7)
            goto Lce
        Lcb:
            r6 = move-exception
            monitor-exit(r7)     // Catch: java.lang.Throwable -> Lcb
            throw r6
        Lce:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.DeviceProfilesSettings.writeBluetoothA2dpConfiguration(boolean):void");
    }

    private void writeCodecUserConfigureToProperty(boolean z) {
        boolean equals = "mediatek".equals(FeatureParser.getString("vendor"));
        if ((this.mLHDCV3Device || this.mLHDCV2Device || this.mLHDCV1Device) && equals) {
            if (z) {
                delFromWhiteList("persist.vendor.bluetooth.a2dp.lhdc.whitelist");
            } else {
                addToWhiteList("persist.vendor.bluetooth.a2dp.lhdc.whitelist");
            }
        }
        if (equals) {
            return;
        }
        if (this.mLHDCV3Device || this.mLHDCV2Device || this.mLHDCV1Device) {
            SystemProperties.set("persist.vendor.bt.a2dp.lhdc.enabled", z ? "true" : "false");
            if (z) {
                delFromWhiteList("persist.vendor.bt.a2dp.lhdc.whitelist");
            } else {
                addToWhiteList("persist.vendor.bt.a2dp.lhdc.whitelist");
            }
        } else if (!this.mLDACDevice && this.mAACDevice) {
            String address = this.mCachedDevice.getAddress();
            SystemProperties.set("persist.vendor.bt.a2dp.aac.whitelist", (!z || address == null) ? "null" : address.toLowerCase());
            if (z) {
                addToWhiteList("persist.vendor.bt.a2dp.aac.whitelists");
            } else {
                delFromWhiteList("persist.vendor.bt.a2dp.aac.whitelists");
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return DeviceProfilesSettings.class.getName();
    }

    public boolean isHfpConnected() {
        BluetoothDevice device = this.mCachedDevice.getDevice();
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHfp;
        boolean z = bluetoothHeadset != null && bluetoothHeadset.getConnectionState(device) == 2;
        Log.d("DeviceProfilesSettings", "isHfpConnected: " + z + " device: " + device);
        return z;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mAudioManager = (AudioManager) getActivity().getSystemService("audio");
        BluetoothDevice bluetoothDevice = bundle != null ? (BluetoothDevice) bundle.getParcelable("device") : (BluetoothDevice) getArguments().getParcelable("device");
        addPreferencesFromResource(R.xml.bluetooth_device_advanced);
        getPreferenceScreen().setOrderingAsAdded(false);
        this.mProfileContainer = (PreferenceGroup) findPreference("profile_container");
        this.mCodecContainer = (PreferenceGroup) findPreference("ldac_container");
        this.mDeviceNamePref = (EditTextPreference) findPreference("rename_device");
        PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("bleShareAudioCategory");
        this.mBleAudioCategory = preferenceGroup;
        if (preferenceGroup != null) {
            Log.d("DeviceProfilesSettings", "mBleAudioCategory not null and default remove it !");
            getPreferenceScreen().removePreference(this.mBleAudioCategory);
        } else {
            Log.d("DeviceProfilesSettings", "mBleAudioCategory is null");
        }
        if (bluetoothDevice == null) {
            Log.w("DeviceProfilesSettings", "Activity started without a remote Bluetooth device");
            finish();
            return;
        }
        this.mRenameDeviceNamePref = new RenameEditTextPreference();
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(getActivity());
        this.mManager = localBtManager;
        CachedBluetoothDeviceManager cachedDeviceManager = localBtManager.getCachedDeviceManager();
        this.mProfileManager = this.mManager.getProfileManager();
        CachedBluetoothDevice findDevice = cachedDeviceManager.findDevice(bluetoothDevice);
        this.mCachedDevice = findDevice;
        if (findDevice == null) {
            Log.w("DeviceProfilesSettings", "Device not found, cannot connect to it");
            finish();
            return;
        }
        String name = findDevice.getName();
        this.mDeviceNamePref.setSummary(name);
        this.mDeviceNamePref.setText(name);
        this.mDeviceNamePref.setOnPreferenceChangeListener(this);
        String address = this.mCachedDevice.getDevice().getAddress();
        this.mDeviceMacAddress = address;
        this.mLDACDevice = this.mCachedDevice.isSupportedCodec(address, "LDAC");
        if (!FeatureParser.getBoolean("support_lhdc", true) || FeatureParser.getBoolean("support_lhdc_offload", true)) {
            this.mLHDCV3Device = this.mCachedDevice.isSupportedCodec(this.mDeviceMacAddress, "LHDC_V3");
            this.mLHDCV2Device = this.mCachedDevice.isSupportedCodec(this.mDeviceMacAddress, "LHDC_V2");
            this.mLHDCV1Device = this.mCachedDevice.isSupportedCodec(this.mDeviceMacAddress, "LHDC_V1");
        }
        if (FeatureParser.getBoolean("support_a2dp_latency", false)) {
            this.mAADevice = this.mCachedDevice.isSupportedCodec(this.mDeviceMacAddress, "aptX Adaptive");
            this.mSBCLlDevice = this.mCachedDevice.getSpecificCodecStatus("zmi_latency") == 1;
        }
        this.mAACDevice = this.mCachedDevice.isSupportedCodec(this.mDeviceMacAddress, "AAC");
        this.mDeviceNamePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                String name2 = DeviceProfilesSettings.this.mCachedDevice.getName();
                DeviceProfilesSettings.this.mDeviceNamePref.setSummary(name2);
                DeviceProfilesSettings.this.mDeviceNamePref.setText(name2);
                DeviceProfilesSettings.this.getContext();
                DeviceProfilesSettings.this.mDeviceNamePref.getEditText();
                return true;
            }
        });
        this.mDeviceNamePref.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings$$ExternalSyntheticLambda0
            @Override // androidx.preference.EditTextPreference.OnBindEditTextListener
            public final void onBindEditText(EditText editText) {
                DeviceProfilesSettings.this.lambda$onCreate$1(editText);
            }
        });
        this.mAudioShareContainer = (PreferenceGroup) findPreference("audio_share_container");
        if (FeatureParser.getBoolean("support_audio_share", false)) {
            addPreferencesForAudioShare();
            Log.d("DeviceProfilesSettings", "SUPPORT_AUDIO_SHARE_FEATURE == true");
        } else if (getPreferenceScreen().findPreference("audio_share_container") != null && this.mAudioShareContainer != null) {
            getPreferenceScreen().removePreference(this.mAudioShareContainer);
        }
        this.mAudioRepairContainer = (PreferenceGroup) findPreference("audio_repair_container");
        if (getPreferenceScreen().findPreference("audio_repair_container") != null) {
            getPreferenceScreen().removePreference(this.mAudioRepairContainer);
        }
        addPreferencesForProfiles();
        try {
            boolean equals = "mediatek".equals(FeatureParser.getString("vendor"));
            boolean equals2 = "qcom".equals(FeatureParser.getString("vendor"));
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            this.mAbsAudioManager = audioManager;
            this.mAudioStreamMax = audioManager.getStreamMaxVolume(3);
            if (equals) {
                if (Settings.Global.getInt(getActivity().getContentResolver(), "persist_vendor_bt_a2dp_absvolfeature_mtk", 0) == 1) {
                    this.mAbsVolFeature = "true";
                }
                this.mIsInAbsWhitelist = isDeviceInListForAbsoluteVolume(this.mCachedDevice.getAddress(), "persist.vendor.bluetooth.a2dp.absolute.volume.whitelistall");
            } else if (equals2) {
                this.mAbsVolFeature = SystemProperties.get("persist.vendor.bt.a2dp.absvolfeature", ExtraContacts.DefaultAccount.NAME);
                this.mIsInAbsWhitelist = isDeviceInListForAbsoluteVolume(this.mCachedDevice.getAddress(), "persist.vendor.bt.a2dp.absolute.volume.whitelistall");
            } else {
                Log.v("DeviceProfilesSettings", "addPreferencesForAbsoluteVolume null");
            }
        } catch (Exception e) {
            Log.w("DeviceProfilesSettings", "addPreferencesForAbsoluteVolume failed " + e);
        }
        if (this.mIsInAbsWhitelist && this.mAbsVolFeature.equals("true")) {
            addPreferencesForAbsoluteVolume();
            Log.w("DeviceProfilesSettings", "addPreferencesForAbsoluteVolume on create");
        }
        boolean isLeAudioBrDevice = isLeAudioBrDevice(this.mDeviceMacAddress);
        this.mIsBleAudioDevice = isLeAudioBrDevice;
        if (isLeAudioBrDevice) {
            addPreferencesForLeAudio();
        }
        addPreferencesForSpecialCodec();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.bluetooth_device_advanced, viewGroup, false);
        this.mRootView = inflate;
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        return this.mRootView;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mDisconnectDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDisconnectDialog = null;
        }
        AlertDialog alertDialog2 = this.mDialog;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
            this.mDialog = null;
        }
        AlertDialog alertDialog3 = this.mAudioRepairDialog;
        if (alertDialog3 != null) {
            alertDialog3.dismiss();
            this.mAudioRepairDialog = null;
        }
        ProgressDialog progressDialog = this.mAudioRepairingDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.mAudioRepairingDialog = null;
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacks(null);
        }
    }

    @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
        refresh();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        if (isDeviceRenameDialogShowing()) {
            hideSoftInput(this.mEtDeviceRename);
        }
        super.onPause();
        this.mCachedDevice.unregisterCallback(this);
        this.mManager.setForegroundActivity(null);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            return;
        }
        Log.d("DeviceProfilesSettings", "set scan mode connectable");
        defaultAdapter.setScanMode(21);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mDeviceNamePref) {
            String trim = ((String) obj).trim();
            if (TextUtils.isEmpty(trim)) {
                trim = this.mCachedDevice.getDevice().getName();
            } else {
                int length = trim.length();
                if (trim.getBytes().length > 31) {
                    int i = length - 1;
                    while (true) {
                        if (i <= 0) {
                            break;
                        } else if (trim.substring(0, i).getBytes().length <= 31) {
                            trim = trim.substring(0, i);
                            break;
                        } else {
                            i--;
                        }
                    }
                }
            }
            this.mCachedDevice.setName(trim);
            return true;
        } else if (!(preference instanceof CheckBoxPreference)) {
            return FeatureParser.getBoolean("support_audio_share", false) && (preference instanceof BluetoothVolumeSeekBarPreference);
        } else if ("ldac_pre".equals(preference.getKey())) {
            onPrefClicked((CheckBoxPreference) preference);
            return true;
        } else if ("abs_volume_pre".equals(preference.getKey())) {
            onAbsVolumePrefClicked((CheckBoxPreference) preference);
            return true;
        } else if ("le_audio_pre".equals(preference.getKey())) {
            onLeAudioPrefClicked((CheckBoxPreference) preference);
            return true;
        } else if (FeatureParser.getBoolean("support_audio_share", false) && "audio_share_switch_pre".equals(preference.getKey())) {
            onAudioShareSwitchPrefClicked((CheckBoxPreference) preference);
            return true;
        } else {
            onProfileClicked(getProfileOf(preference), (CheckBoxPreference) preference);
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        Log.d("DeviceProfilesSettings", "preference clicked key is: " + key);
        key.hashCode();
        char c = 65535;
        switch (key.hashCode()) {
            case -840336141:
                if (key.equals("unpair")) {
                    c = 0;
                    break;
                }
                break;
            case -786073861:
                if (key.equals("bleAudioBroadcastAdd")) {
                    c = 1;
                    break;
                }
                break;
            case -389457405:
                if (key.equals("bleShareAudioBroadcastSwitch")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                unpairDevice();
                finish();
                break;
            case 1:
                Log.d("DeviceProfilesSettings", "preference clicked KEY_BLE_ADUIO_BROADCAST_ADD");
                Bundle bundle = new Bundle();
                bundle.putParcelable("device", this.mCachedDevice.getDevice());
                MiuiUtils.startPreferencePanel(getActivity(), "com.android.settings.bluetooth.MiuiBluetoothShareBroadcastFragment", bundle, R.string.bluetooth_share_broadcast, null, null, 0);
                break;
            case 2:
                Log.d("DeviceProfilesSettings", "preference clicked KEY_BLE_ADUIO_SHARE_BROADCAST_SWITCH");
                break;
            default:
                return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        String string;
        super.onResume();
        this.mManager.setForegroundActivity(getActivity());
        CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        if (cachedBluetoothDevice != null) {
            cachedBluetoothDevice.registerCallback(this);
            if (this.mCachedDevice.getBondState() == 10) {
                getActivity().finish();
                return;
            }
        }
        refresh();
        EditText editText = this.mDeviceNamePref.getEditText();
        if (editText != null) {
            editText.addTextChangedListener(this.mRenameDeviceNamePref);
            Dialog dialog = this.mDeviceNamePref.getDialog();
            if (dialog instanceof AlertDialog) {
                ((AlertDialog) dialog).getButton(-1).setEnabled(editText.getText().length() > 0);
            }
        }
        if (isDeviceRenameDialogShowing()) {
            this.mEtDeviceRename.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.DeviceProfilesSettings$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DeviceProfilesSettings.this.lambda$onResume$2();
                }
            }, 150L);
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("abs_volume_pre");
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) getPreferenceScreen().findPreference("le_audio_pre");
        if (checkBoxPreference != null) {
            Settings.Secure.getString(getActivity().getContentResolver(), "miui_store_audio_share_device_address");
            if (this.mCachedDevice.isActiveDevice(2) || (checkBoxPreference2 != null && ((this.mCachedDevice.getSpecificCodecStatus("LEAUDIO") == 1 || this.mCachedDevice.getLeAudioStatus() == 1) && this.mAbsAudioManager != null))) {
                Log.i("DeviceProfilesSettings", "on resume change state");
                if (this.mAbsAudioManager.isMusicActive() || this.mCachedDevice.getSpecificCodecStatus("LEAUDIO") == 1 || this.mCachedDevice.getLeAudioStatus() == 1) {
                    checkBoxPreference.setEnabled(false);
                } else {
                    checkBoxPreference.setEnabled(true);
                }
            }
            checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("ABSOLUTEVOLUME") == 1);
        }
        Preference findPreference = getPreferenceScreen().findPreference("codec_claimer");
        if (findPreference != null) {
            findPreference.setVisible(false);
            if (this.mLHDCV3Device || this.mLHDCV2Device || this.mLHDCV1Device) {
                String string2 = Settings.Secure.getString(getContext().getContentResolver(), "miui_bluetooth_lhdc_whitelist_cache");
                if (string2 == null || string2 == "") {
                    findPreference.setTitle(R.string.bt_lhdc_declaration);
                    findPreference.setVisible(true);
                }
            } else if (this.mLDACDevice) {
                findPreference.setTitle(R.string.bt_ldac_declaration);
                findPreference.setVisible(true);
            }
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            Log.d("DeviceProfilesSettings", "set scan mode connectable and discoverable");
            defaultAdapter.setScanMode(23);
        }
        if (FeatureParser.getBoolean("support_audio_share", false) && (string = Settings.Secure.getString(getActivity().getContentResolver(), "miui_store_audio_share_device_address")) != null && string.isEmpty()) {
            CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) getPreferenceScreen().findPreference("audio_share_switch_pre");
            BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) getPreferenceScreen().findPreference("audio_share_volume_pre");
            CheckBoxPreference checkBoxPreference4 = (CheckBoxPreference) getPreferenceScreen().findPreference("ldac_pre");
            CheckBoxPreference checkBoxPreference5 = (CheckBoxPreference) getPreferenceScreen().findPreference("latency_pre");
            if (checkBoxPreference3 != null) {
                checkBoxPreference3.setChecked(false);
                checkBoxPreference3.setEnabled(true);
                this.mCachedDevice.setSpecificCodecStatus("AUDIO_SHARE_SWITCH", 0);
            }
            if (bluetoothVolumeSeekBarPreference != null) {
                bluetoothVolumeSeekBarPreference.setProgress(50);
                bluetoothVolumeSeekBarPreference.setEnabled(false);
                bluetoothVolumeSeekBarPreference.setVisible(false);
                this.mCachedDevice.setSpecificCodecStatus("audio_share_volume_pre", 50);
            }
            if (checkBoxPreference4 != null) {
                checkBoxPreference4.setEnabled(true);
            }
            boolean z = this.mLHDCV3Device && this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") == 1;
            if (checkBoxPreference5 != null) {
                if (this.mLHDCV3Device) {
                    checkBoxPreference5.setEnabled(z);
                } else {
                    checkBoxPreference5.setEnabled(true);
                }
            }
        }
        if (LocalBluetoothProfileManager.isTbsProfileEnabled() && this.mCachedDevice.isDualModeDevice() && checkBoxPreference2 != null) {
            if (isSCOOn() || isLeAudioCgOn()) {
                checkBoxPreference2.setEnabled(false);
                Log.d("DeviceProfilesSettings", "leAudioPre.setEnabled(false) when calling");
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("device", this.mCachedDevice.getDevice());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED");
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.PLAYING_STATE_CHANGED");
        if (FeatureParser.getBoolean("support_audio_share", false)) {
            intentFilter.addAction("MultiA2dp.ACTION.VOLUME_CHANGED");
        }
        getActivity().registerReceiver(this.mBluetoothA2dpReceiver, intentFilter);
        if (FeatureParser.getBoolean("support_audio_share", false)) {
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
            intentFilter2.addAction("android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED");
            intentFilter2.addAction("MultiA2dp.ACTION.RESET_STATE_CHANGED");
            getActivity().registerReceiver(this.mBluetoothMultiA2DPStateResultReceiver, intentFilter2);
            IntentFilter intentFilter3 = new IntentFilter();
            intentFilter3.addAction("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
            getActivity().registerReceiver(this.mBluetoothHfpAudioStateReceiver, intentFilter3);
        } else if (LocalBluetoothProfileManager.isTbsProfileEnabled() && this.mCachedDevice.isDualModeDevice()) {
            IntentFilter intentFilter4 = new IntentFilter();
            intentFilter4.addAction("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
            getActivity().registerReceiver(this.mBluetoothHfpAudioStateReceiver, intentFilter4);
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mAudioRepairingDialog != null && defaultAdapter.isEnabled()) {
            this.mAudioRepairingDialog.dismiss();
            this.mAudioRepairingDialog = null;
        }
        IntentFilter intentFilter5 = new IntentFilter();
        intentFilter5.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        getActivity().registerReceiver(this.mBluetoothAudioRepairResultReceiver, intentFilter5);
        getProfileProxy();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        if (isDeviceRenameDialogShowing()) {
            hideSoftInput(this.mEtDeviceRename);
        }
        super.onStop();
        getActivity().unregisterReceiver(this.mBluetoothA2dpReceiver);
        getActivity().unregisterReceiver(this.mBluetoothAudioRepairResultReceiver);
        if (FeatureParser.getBoolean("support_audio_share", false)) {
            getActivity().unregisterReceiver(this.mBluetoothMultiA2DPStateResultReceiver);
            getActivity().unregisterReceiver(this.mBluetoothHfpAudioStateReceiver);
        } else if (LocalBluetoothProfileManager.isTbsProfileEnabled() && this.mCachedDevice.isDualModeDevice()) {
            try {
                getActivity().unregisterReceiver(this.mBluetoothHfpAudioStateReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        closeProfileProxy(0);
    }
}
