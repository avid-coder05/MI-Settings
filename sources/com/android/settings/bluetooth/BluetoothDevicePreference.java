package com.android.settings.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.miuisettings.preference.Preference;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Locale;
import miui.bluetooth.ble.MiBleDeviceManager;
import miui.provider.ExtraContacts;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;
import miuix.preference.ConnectPreferenceHelper;

/* loaded from: classes.dex */
public class BluetoothDevicePreference extends Preference implements View.OnClickListener {
    protected static boolean mTriggerFromAvaliableDevices = false;
    private static int sDimAlpha = Integer.MIN_VALUE;
    private final int SOURCE_CODEC_TYPE_LC3;
    private final int SOURCE_CODEC_TYPE_LHDCV1;
    private final int SOURCE_CODEC_TYPE_LHDCV2;
    private final int SOURCE_CODEC_TYPE_LHDCV3;
    private int UPDATE;
    private String contentDescription;
    protected MiBleDeviceManager mBleDeviceMgr;
    private BluetoothA2dp mBluetoothA2dp;
    protected CachedBluetoothDevice mCachedDevice;
    private DeviceCallBack mCallBack;
    private int mCurrentCodecType;
    private long mCurrentTime;
    private AlertDialog mDisconnectDialog;
    Handler mHandler;
    private ConnectPreferenceHelper mHelper;
    private boolean mHideSecondTarget;
    private boolean mIsCallbackRemoved;
    private boolean mIsSettingsDevice;
    private boolean mIsUserRestriction;
    private AudioShareJumpPage mJumpAttributePage;
    boolean mNeedNotifyHierarchyChanged;
    private View.OnClickListener mOnSettingsClickListener;
    Resources mResources;
    private boolean mShowDevicesWithoutNames;
    private AlertDialog mSwitchActiveDeviceDialog;
    private int mType;
    private UserManager mUserManager;
    private View mView;

    /* loaded from: classes.dex */
    public interface AudioShareJumpPage {
        void onCallBack(BluetoothDevice bluetoothDevice);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class DeviceCallBack implements CachedBluetoothDevice.Callback {
        WeakReference<BluetoothDevicePreference> preferenceRef;

        DeviceCallBack(BluetoothDevicePreference bluetoothDevicePreference) {
            this.preferenceRef = new WeakReference<>(bluetoothDevicePreference);
        }

        @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
        public void onDeviceAttributesChanged() {
            BluetoothDevicePreference bluetoothDevicePreference;
            WeakReference<BluetoothDevicePreference> weakReference = this.preferenceRef;
            if (weakReference == null || weakReference.get() == null || (bluetoothDevicePreference = this.preferenceRef.get()) == null) {
                return;
            }
            bluetoothDevicePreference.onDeviceAttributesChanged();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SortType {
    }

    public BluetoothDevicePreference(Context context, CachedBluetoothDevice cachedBluetoothDevice, boolean z) {
        super(context, (AttributeSet) null);
        this.UPDATE = 100;
        this.contentDescription = null;
        this.mHideSecondTarget = false;
        this.mIsCallbackRemoved = false;
        this.mNeedNotifyHierarchyChanged = false;
        this.mCurrentCodecType = -1;
        this.SOURCE_CODEC_TYPE_LHDCV2 = 9;
        this.SOURCE_CODEC_TYPE_LHDCV3 = 10;
        this.SOURCE_CODEC_TYPE_LHDCV1 = 11;
        this.SOURCE_CODEC_TYPE_LC3 = 20;
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.bluetooth.BluetoothDevicePreference.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == BluetoothDevicePreference.this.UPDATE) {
                    BluetoothDevicePreference.this.updateAttributes();
                }
            }
        };
        UserManager userManager = (UserManager) context.getSystemService("user");
        this.mUserManager = userManager;
        init(context, cachedBluetoothDevice, z, userManager.hasUserRestriction("no_config_bluetooth"));
    }

    public BluetoothDevicePreference(Context context, CachedBluetoothDevice cachedBluetoothDevice, boolean z, int i) {
        super(context, (AttributeSet) null);
        this.UPDATE = 100;
        this.contentDescription = null;
        this.mHideSecondTarget = false;
        this.mIsCallbackRemoved = false;
        this.mNeedNotifyHierarchyChanged = false;
        this.mCurrentCodecType = -1;
        this.SOURCE_CODEC_TYPE_LHDCV2 = 9;
        this.SOURCE_CODEC_TYPE_LHDCV3 = 10;
        this.SOURCE_CODEC_TYPE_LHDCV1 = 11;
        this.SOURCE_CODEC_TYPE_LC3 = 20;
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.bluetooth.BluetoothDevicePreference.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == BluetoothDevicePreference.this.UPDATE) {
                    BluetoothDevicePreference.this.updateAttributes();
                }
            }
        };
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mCurrentTime = System.currentTimeMillis();
        this.mType = i;
        init(context, cachedBluetoothDevice, z, this.mUserManager.hasUserRestriction("no_config_bluetooth"));
    }

    public BluetoothDevicePreference(Context context, CachedBluetoothDevice cachedBluetoothDevice, boolean z, MiBleDeviceManager miBleDeviceManager) {
        super(context, (AttributeSet) null);
        this.UPDATE = 100;
        this.contentDescription = null;
        this.mHideSecondTarget = false;
        this.mIsCallbackRemoved = false;
        this.mNeedNotifyHierarchyChanged = false;
        this.mCurrentCodecType = -1;
        this.SOURCE_CODEC_TYPE_LHDCV2 = 9;
        this.SOURCE_CODEC_TYPE_LHDCV3 = 10;
        this.SOURCE_CODEC_TYPE_LHDCV1 = 11;
        this.SOURCE_CODEC_TYPE_LC3 = 20;
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.bluetooth.BluetoothDevicePreference.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == BluetoothDevicePreference.this.UPDATE) {
                    BluetoothDevicePreference.this.updateAttributes();
                }
            }
        };
        this.mBleDeviceMgr = miBleDeviceManager;
        UserManager userManager = (UserManager) context.getSystemService("user");
        this.mUserManager = userManager;
        init(context, cachedBluetoothDevice, z, userManager.hasUserRestriction("no_config_bluetooth"));
    }

    private void askDisconnect() {
        Context context = getContext();
        String name = this.mCachedDevice.getName();
        if (TextUtils.isEmpty(name)) {
            name = context.getString(R.string.bluetooth_device);
        }
        String string = context.getString(R.string.bluetooth_disconnect_all_profiles, name);
        this.mDisconnectDialog = Utils.showDisconnectDialog(context, this.mDisconnectDialog, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothDevicePreference.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                BluetoothDevicePreference.this.mCachedDevice.disconnect();
            }
        }, context.getString(R.string.bluetooth_disconnect_title), Html.fromHtml(string));
    }

    private void askSwtichActiveDevice() {
        final Context context = getContext();
        String string = context.getString(R.string.bluetooth_audio_share_feature_notice_summary);
        String string2 = context.getString(R.string.bluetooth_audio_share_feature_notice_title);
        this.mSwitchActiveDeviceDialog = Utils.showSwitchActiveDeviceDialog(context, this.mSwitchActiveDeviceDialog, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothDevicePreference.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                BluetoothDevicePreference.this.mCachedDevice.setActive();
            }
        }, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothDevicePreference.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Settings.Secure.putString(context.getContentResolver(), "miui_store_audio_share_window_pop", "NeedPop");
                BluetoothDevicePreference.this.mJumpAttributePage.onCallBack(BluetoothDevicePreference.this.mCachedDevice.getDevice());
                Log.d("BluetoothDevicePreference", "mJumpAttributePage.onCallBack");
            }
        }, string2, Html.fromHtml(string));
    }

    private int getRecognizableCodecType(BluetoothCodecConfig bluetoothCodecConfig) {
        int codecType = bluetoothCodecConfig.getCodecType();
        if (codecType != 0 && codecType != 1 && codecType != 2 && codecType != 3 && codecType != 4 && codecType != 100 && codecType != 101) {
            switch (codecType) {
                case 9:
                case 10:
                case 11:
                    break;
                default:
                    return -1;
            }
        }
        return bluetoothCodecConfig.getCodecType();
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x005a A[Catch: Exception -> 0x0159, TryCatch #0 {Exception -> 0x0159, blocks: (B:3:0x0003, B:5:0x0016, B:7:0x001c, B:9:0x0020, B:11:0x0026, B:14:0x002e, B:16:0x0037, B:18:0x003e, B:20:0x0044, B:22:0x004a, B:24:0x0050, B:26:0x005a, B:28:0x0060, B:29:0x0065, B:33:0x006e, B:35:0x0074, B:36:0x0079, B:40:0x0081, B:43:0x0096, B:45:0x009e, B:47:0x00a4, B:49:0x00f3, B:51:0x00f9, B:55:0x0106, B:58:0x0115, B:60:0x011f, B:61:0x0127, B:63:0x0151), top: B:70:0x0003 }] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x006e A[Catch: Exception -> 0x0159, TryCatch #0 {Exception -> 0x0159, blocks: (B:3:0x0003, B:5:0x0016, B:7:0x001c, B:9:0x0020, B:11:0x0026, B:14:0x002e, B:16:0x0037, B:18:0x003e, B:20:0x0044, B:22:0x004a, B:24:0x0050, B:26:0x005a, B:28:0x0060, B:29:0x0065, B:33:0x006e, B:35:0x0074, B:36:0x0079, B:40:0x0081, B:43:0x0096, B:45:0x009e, B:47:0x00a4, B:49:0x00f3, B:51:0x00f9, B:55:0x0106, B:58:0x0115, B:60:0x011f, B:61:0x0127, B:63:0x0151), top: B:70:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean isLeAudioConnected() {
        /*
            Method dump skipped, instructions count: 367
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.BluetoothDevicePreference.isLeAudioConnected():boolean");
    }

    private boolean isMatchAudioSharePublicityCondition(BluetoothDevice bluetoothDevice) {
        BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
        if (bluetoothA2dp == null || bluetoothA2dp.getActiveDevice() == null || this.mBluetoothA2dp.getActiveDevice().equals(bluetoothDevice)) {
            return false;
        }
        Log.d("BluetoothDevicePreference", "isMatchAudioShareCondition = true");
        return true;
    }

    private boolean isNeedShowDialog(int i, BluetoothCodecConfig bluetoothCodecConfig, BluetoothCodecConfig[] bluetoothCodecConfigArr, BluetoothA2dp bluetoothA2dp, boolean z) {
        boolean z2;
        boolean z3;
        int i2 = i;
        BluetoothCodecConfig[] bluetoothCodecConfigArr2 = bluetoothCodecConfigArr;
        BluetoothDevice device = this.mCachedDevice.getDevice();
        int length = bluetoothCodecConfigArr2.length;
        int i3 = 0;
        int i4 = 0;
        boolean z4 = false;
        while (true) {
            z2 = true;
            if (i4 >= length) {
                break;
            }
            if ("LDAC".equals(bluetoothCodecConfigArr2[i4].getCodecName())) {
                z4 = true;
            }
            i4++;
            z4 = z4;
        }
        String string = Settings.Secure.getString(getContext().getContentResolver(), "miui_store_audio_share_device_address");
        if (FeatureParser.getBoolean("support_audio_share", false) && string != null && !string.isEmpty()) {
            Log.d("BluetoothDevicePreference", "codec is sbc in audio sharing mode");
            return false;
        }
        Log.d("BluetoothDevicePreference", "isNeedShowDialog defaultCloseLHDC = " + z);
        if ((!FeatureParser.getBoolean("support_lhdc", true) || FeatureParser.getBoolean("support_lhdc_offload", false)) && z) {
            int length2 = bluetoothCodecConfigArr2.length;
            boolean z5 = false;
            boolean z6 = false;
            boolean z7 = false;
            boolean z8 = false;
            while (i3 < length2) {
                int codecType = bluetoothCodecConfigArr2[i3].getCodecType();
                StringBuilder sb = new StringBuilder();
                int i5 = length2;
                sb.append("codecCapability.getCodecType = ");
                sb.append(codecType);
                Log.d("BluetoothDevicePreference", sb.toString());
                if (codecType == 10) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("isSupportLHDCV3 = ");
                    z3 = true;
                    sb2.append(true);
                    Log.d("BluetoothDevicePreference", sb2.toString());
                    z5 = true;
                    z6 = true;
                } else {
                    z3 = true;
                    if (codecType == 9) {
                        Log.d("BluetoothDevicePreference", "isSupportLHDCV2 = true");
                        z5 = true;
                        z7 = true;
                    } else if (codecType == 11) {
                        Log.d("BluetoothDevicePreference", "isSupportLHDCV1 = true");
                        z5 = true;
                        z8 = true;
                    }
                }
                i3++;
                bluetoothCodecConfigArr2 = bluetoothCodecConfigArr;
                z2 = z3;
                length2 = i5;
            }
            boolean z9 = z2;
            if (z5 == z9) {
                if (i2 == 10) {
                    this.mCachedDevice.setSupportedCodec(device.getAddress(), "LHDC_V3", z9);
                    this.mCachedDevice.setSpecificCodecStatus("LHDC_V3", z9 ? 1 : 0);
                } else if (i2 == 9) {
                    this.mCachedDevice.setSupportedCodec(device.getAddress(), "LHDC_V2", z9);
                    this.mCachedDevice.setSpecificCodecStatus("LHDC_V2", z9 ? 1 : 0);
                } else if (i2 == 11) {
                    this.mCachedDevice.setSupportedCodec(device.getAddress(), "LHDC_V1", z9);
                    this.mCachedDevice.setSpecificCodecStatus("LHDC_V1", z9 ? 1 : 0);
                } else if (z6 == z9) {
                    this.mCachedDevice.setSpecificCodecStatus("LHDC_V3", 0);
                    i2 = 10;
                } else if (z7 == z9) {
                    this.mCachedDevice.setSpecificCodecStatus("LHDC_V2", 0);
                    i2 = 9;
                } else if (z8 == z9) {
                    this.mCachedDevice.setSpecificCodecStatus("LHDC_V1", 0);
                    i2 = 11;
                }
            }
            Log.d("BluetoothDevicePreference", "final codec type = " + i2);
        }
        if (i2 == 4) {
            if (!this.mCachedDevice.isSupportedCodec(device.getAddress(), "LDAC")) {
                this.mCachedDevice.setSupportedCodec(device.getAddress(), "LDAC", true);
                this.mCachedDevice.setSpecificCodecStatus("LDAC", 1);
                return true;
            } else if (this.mCachedDevice.getSpecificCodecStatus("LDAC") != 1) {
                Log.d("BluetoothDevicePreference", "LDAC CodecStatus is disabled, no need show Dialog!");
                return false;
            } else if (!this.mCachedDevice.getDialogChoice(device.getAddress()) && "true".equals(SystemProperties.get("persist.vendor.bt.a2dp.ldac.enabled", "true"))) {
                return true;
            }
        } else if (i2 == 10) {
            if (!this.mCachedDevice.isSupportedCodec(device.getAddress(), "LHDC_V3")) {
                this.mCachedDevice.setSupportedCodec(device.getAddress(), "LHDC_V3", true);
                this.mCachedDevice.setSpecificCodecStatus("LHDC_V3", !z ? 1 : 0);
                return !z ? 1 : 0;
            } else if (this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") != 1 && !z) {
                Log.d("BluetoothDevicePreference", "LHDC_V3 CodecStatus Disable");
                bluetoothCodecConfig.setCodecPriority(-1);
                bluetoothA2dp.setCodecConfigPreference(device, bluetoothCodecConfig);
                return false;
            } else if (!this.mCachedDevice.getDialogChoice(device.getAddress())) {
                return !z ? 1 : 0;
            }
        } else if (i2 == 9) {
            if (!this.mCachedDevice.isSupportedCodec(device.getAddress(), "LHDC_V2")) {
                this.mCachedDevice.setSupportedCodec(device.getAddress(), "LHDC_V2", true);
                this.mCachedDevice.setSpecificCodecStatus("LHDC_V2", !z ? 1 : 0);
                return !z ? 1 : 0;
            } else if (this.mCachedDevice.getSpecificCodecStatus("LHDC_V2") != 1 && !z) {
                Log.d("BluetoothDevicePreference", "LHDC_V2 CodecStatus Disable");
                bluetoothCodecConfig.setCodecPriority(-1);
                bluetoothA2dp.setCodecConfigPreference(device, bluetoothCodecConfig);
                return false;
            } else if (!this.mCachedDevice.getDialogChoice(device.getAddress())) {
                return !z ? 1 : 0;
            }
        } else if (i2 == 11) {
            if (!this.mCachedDevice.isSupportedCodec(device.getAddress(), "LHDC_V1")) {
                this.mCachedDevice.setSupportedCodec(device.getAddress(), "LHDC_V1", true);
                this.mCachedDevice.setSpecificCodecStatus("LHDC_V1", !z ? 1 : 0);
                return !z ? 1 : 0;
            } else if (this.mCachedDevice.getSpecificCodecStatus("LHDC_V1") != 1 && !z) {
                Log.d("BluetoothDevicePreference", "LHDC_V1 CodecStatus Disable");
                bluetoothCodecConfig.setCodecPriority(-1);
                bluetoothA2dp.setCodecConfigPreference(device, bluetoothCodecConfig);
                return false;
            } else if (!this.mCachedDevice.getDialogChoice(device.getAddress())) {
                return true ^ (z ? 1 : 0);
            }
        } else if (i2 != 100) {
            if (!z4 || i2 == -1) {
                return false;
            }
            this.mCachedDevice.setSpecificCodecStatus("LDAC", 0);
            return false;
        } else if (!this.mCachedDevice.isSupportedCodec(device.getAddress(), "aptX Adaptive")) {
            this.mCachedDevice.setSupportedCodec(device.getAddress(), "aptX Adaptive", true);
            return false;
        }
        return false;
    }

    private void pair() {
        if (this.mCachedDevice.startPairing()) {
            return;
        }
        Utils.showError(getContext(), this.mCachedDevice.getName(), R.string.bluetooth_pairing_error_message);
    }

    private void setLeAudioDeviceActive() {
        String str;
        String str2;
        BluetoothAdapter defaultAdapter;
        try {
            Context context = getContext();
            LocalBluetoothManager localBtManager = Utils.getLocalBtManager(context);
            CachedBluetoothDeviceManager cachedDeviceManager = localBtManager.getCachedDeviceManager();
            LocalBluetoothProfileManager profileManager = localBtManager.getProfileManager();
            if (profileManager == null || !LocalBluetoothProfileManager.isTbsProfileEnabled()) {
                return;
            }
            CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
            if (cachedBluetoothDevice == null) {
                Log.i("BluetoothDevicePreference", "setLeAudioDeviceActive: null device return false");
                return;
            }
            String findLeAddress = cachedBluetoothDevice.findLeAddress();
            if (findLeAddress != null) {
                String[] split = findLeAddress.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
                if (split.length > 1) {
                    str2 = split[0];
                    str = split[1];
                    defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (defaultAdapter != null || "".equals(str2) || "".equals(str)) {
                        return;
                    }
                    BluetoothDevice remoteDevice = defaultAdapter.getRemoteDevice(str2);
                    BluetoothDevice remoteDevice2 = defaultAdapter.getRemoteDevice(str);
                    if (remoteDevice != null) {
                        CachedBluetoothDevice findDevice = cachedDeviceManager.findDevice(remoteDevice);
                        if (findDevice == null) {
                            findDevice = new CachedBluetoothDevice(context, profileManager, remoteDevice);
                        }
                        if (findDevice.isConnectedLeAudioDevice()) {
                            Log.i("BluetoothDevicePreference", "setLeAudioDeviceActive: LE1 device connected");
                            findDevice.setActive();
                            return;
                        }
                    }
                    if (remoteDevice2 != null) {
                        CachedBluetoothDevice findDevice2 = cachedDeviceManager.findDevice(remoteDevice2);
                        if (findDevice2 == null) {
                            findDevice2 = new CachedBluetoothDevice(context, profileManager, remoteDevice2);
                        }
                        if (findDevice2.isConnectedLeAudioDevice()) {
                            Log.i("BluetoothDevicePreference", "setLeAudioDeviceActive: LE2 device connected");
                            findDevice2.setActive();
                            return;
                        }
                        return;
                    }
                    return;
                }
            }
            str = "";
            str2 = str;
            defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter != null) {
            }
        } catch (Exception e) {
            Log.i("BluetoothDevicePreference", "setLeAudioDeviceActive failed " + e);
        }
    }

    private static void setMTriggerFromAvaliableDevices(boolean z) {
        mTriggerFromAvaliableDevices = z;
    }

    private void updateOrder(CachedBluetoothDevice cachedBluetoothDevice) {
        if (cachedBluetoothDevice.isConnected()) {
            setOrder(-1);
        } else {
            setOrder(0);
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BluetoothDevicePreference)) {
            return false;
        }
        return this.mCachedDevice.equals(((BluetoothDevicePreference) obj).mCachedDevice);
    }

    public CachedBluetoothDevice getBluetoothDevice() {
        return this.mCachedDevice;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Pair<Drawable, String> getBtClassDrawableWithDescription() {
        return BluetoothUtils.getBtClassDrawableWithDescription(getContext(), this.mCachedDevice);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CachedBluetoothDevice getCachedDevice() {
        return this.mCachedDevice;
    }

    public String getCodecName(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i != 20) {
                                if (i != 100) {
                                    if (i != 101) {
                                        switch (i) {
                                            case 9:
                                                return "LHDC_V2";
                                            case 10:
                                                return "LHDC_V3";
                                            case 11:
                                                return "LHDC_V1";
                                            default:
                                                return "UNKNOWN";
                                        }
                                    }
                                    return "aptX TWS+";
                                }
                                return "aptX Adaptive";
                            }
                            return "LC3";
                        }
                        return "LDAC";
                    }
                    return "aptX HD";
                }
                return "aptX";
            }
            return "AAC";
        }
        return "SBC";
    }

    public int hashCode() {
        return this.mCachedDevice.hashCode();
    }

    public void hideSecondTarget(boolean z) {
        this.mHideSecondTarget = z;
    }

    public void init(Context context, CachedBluetoothDevice cachedBluetoothDevice, boolean z, boolean z2) {
        this.mResources = getContext().getResources();
        this.mShowDevicesWithoutNames = z;
        this.mIsUserRestriction = z2;
        if (sDimAlpha == Integer.MIN_VALUE) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(16842803, typedValue, true);
            sDimAlpha = (int) (typedValue.getFloat() * 255.0f);
        }
        this.mCachedDevice = cachedBluetoothDevice;
        DeviceCallBack deviceCallBack = new DeviceCallBack(this);
        this.mCallBack = deviceCallBack;
        this.mCachedDevice.registerCallback(deviceCallBack);
        if ((cachedBluetoothDevice.getBondState() == 12 || GattProfile.isBond(this.mCachedDevice.getDevice())) && !this.mIsUserRestriction) {
            setWidgetLayoutResource(R.layout.miuix_preference_connect_widget_layout);
        }
        setLayoutResource(R.layout.preference_bt_icon_corner);
        setShouldDisableView(false);
        this.mHelper = new ConnectPreferenceHelper(context, this);
        setOrder(1);
        updateAttributes();
        this.mHandler.sendEmptyMessageDelayed(this.UPDATE, 500L);
    }

    public boolean isWearableWatchDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothClass bluetoothClass;
        try {
            String string = Settings.Global.getString(getContext().getContentResolver(), "bluetooth_wearable_watch_hfp_active_switch");
            if (cachedBluetoothDevice == null || cachedBluetoothDevice.getDevice() == null || !"true".equals(string) || (bluetoothClass = cachedBluetoothDevice.getDevice().getBluetoothClass()) == null) {
                return false;
            }
            return bluetoothClass.getDeviceClass() == 1796;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        MiBleDeviceManager miBleDeviceManager;
        super.onBindViewHolder(preferenceViewHolder);
        if (findPreferenceInHierarchy("bt_checkbox") != null) {
            setDependency("bt_checkbox");
        }
        View view = preferenceViewHolder.itemView;
        this.mView = view;
        int bondState = this.mCachedDevice.getBondState();
        ImageView imageView = (ImageView) view.findViewById(R.id.preference_detail);
        if (imageView != null) {
            if (bondState == 12 || ((miBleDeviceManager = this.mBleDeviceMgr) != null && miBleDeviceManager.getDeviceType(this.mCachedDevice.getDevice().getAddress()) != 0)) {
                imageView.setOnClickListener(this);
            }
            imageView.setContentDescription(getContext().getString(R.string.bluetooth_device_details));
            imageView.setTag(this.mCachedDevice);
            updateCodecIcon(this.mCurrentCodecType);
        }
        ImageView imageView2 = (ImageView) view.findViewById(16908294);
        if (imageView2 != null) {
            imageView2.setContentDescription(this.contentDescription);
            imageView2.setImportantForAccessibility(2);
            imageView2.setElevation(getContext().getResources().getDimension(R.dimen.bt_icon_elevation));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        View findViewById = view.findViewById(R.id.view_corner);
        View findViewById2 = view.findViewById(R.id.view_high_light_root);
        if (bondState == 12 || GattProfile.isBond(this.mCachedDevice.getDevice())) {
            if (findViewById == null || findViewById2 == null) {
                view.setPaddingRelative(getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_preference_item_padding_start), 0, getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_preference_item_padding_end), 0);
            } else {
                layoutParams.setMargins(0, getContext().getResources().getDimensionPixelOffset(R.dimen.preference_bt_custom_margin_top), 0, getContext().getResources().getDimensionPixelOffset(R.dimen.preference_bt_custom_margin_bottom));
                findViewById.setLayoutParams(layoutParams);
                findViewById2.setPaddingRelative(getContext().getResources().getDimensionPixelOffset(R.dimen.preference_bt_custom_padding_start), 0, 0, 0);
                view.setPaddingRelative(getContext().getResources().getDimensionPixelOffset(R.dimen.preference_bt_custom_margin_start), 0, getContext().getResources().getDimensionPixelOffset(R.dimen.preference_bt_custom_margin_end), 0);
            }
            preferenceViewHolder.setIsRecyclable(false);
        } else {
            if (findViewById != null && findViewById2 != null) {
                layoutParams.setMargins(0, 0, 0, 0);
                findViewById.setLayoutParams(layoutParams);
                findViewById2.setPaddingRelative(0, 0, 0, 0);
            }
            view.setPaddingRelative(getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_preference_item_padding_start), 0, getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_preference_item_padding_end), 0);
        }
        if (findViewById2 != null) {
            this.mIsSettingsDevice = true;
            ConnectPreferenceHelper connectPreferenceHelper = this.mHelper;
            if (connectPreferenceHelper != null) {
                connectPreferenceHelper.setIconAnimEnabled(bondState == 12 || GattProfile.isBond(this.mCachedDevice.getDevice()));
                this.mHelper.onBindViewHolder(preferenceViewHolder, findViewById2);
            }
            if (bondState != 12 && !GattProfile.isBond(this.mCachedDevice.getDevice())) {
                findViewById2.setBackground(null);
                TypedValue typedValue = new TypedValue();
                getContext().getTheme().resolveAttribute(R.attr.preferenceItemBackground, typedValue, true);
                view.setBackgroundResource(typedValue.resourceId);
            }
        }
        if (this.mIsSettingsDevice) {
            return;
        }
        setConnectState(0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        View.OnClickListener onClickListener = this.mOnSettingsClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onClicked() {
        boolean z;
        Context context = getContext();
        Utils.getLocalBtManager(context).getProfileManager();
        int i = -1;
        int i2 = -1;
        for (LocalBluetoothProfile localBluetoothProfile : this.mCachedDevice.getProfiles()) {
            Log.d("BluetoothDevicePreference", "support Profiles() = " + localBluetoothProfile);
            int profileId = localBluetoothProfile.getProfileId();
            if (profileId == 1) {
                i = localBluetoothProfile.getConnectionPolicy(this.mCachedDevice.getDevice());
                Log.d("BluetoothDevicePreference", "HFP getConnectionPolicy = " + i);
            } else if (profileId == 2) {
                i2 = localBluetoothProfile.getConnectionPolicy(this.mCachedDevice.getDevice());
                Log.d("BluetoothDevicePreference", "A2DP getConnectionPolicy = " + i2);
            }
        }
        if (Settings.Secure.getInt(context.getContentResolver(), "A2DP_HFP_GLOBAL", 0) == 1) {
            Log.d("BluetoothDevicePreference", "set isEnable true ");
            z = true;
        } else {
            z = false;
        }
        if (((this.mCachedDevice.isConnectedA2dpDevice() && !this.mCachedDevice.isActiveDevice(2)) || ((this.mCachedDevice.isConnectedHfpDevice() && !this.mCachedDevice.isActiveDevice(1)) || (this.mCachedDevice.isConnectedHearingAidDevice() && !this.mCachedDevice.isActiveDevice(21)))) && !isLeAudioConnected()) {
            boolean z2 = this.mCachedDevice.isActiveDevice(2) || this.mCachedDevice.isActiveDevice(1);
            String string = Settings.Secure.getString(context.getContentResolver(), "miui_store_audio_share_device_address");
            if (FeatureParser.getBoolean("support_audio_share", false) && Settings.Secure.getString(context.getContentResolver(), "miui_store_audio_share_window_pop") == null && string != null && string.isEmpty() && isMatchAudioSharePublicityCondition(this.mCachedDevice.getDevice()) && !this.mCachedDevice.getDevice().isTwsPlusDevice()) {
                askSwtichActiveDevice();
                Settings.Secure.putString(context.getContentResolver(), "miui_store_audio_share_window_pop", "HadPoped");
                return;
            } else if (!isWearableWatchDevice(this.mCachedDevice) && this.mCachedDevice.setActive() && (!z2 || !this.mCachedDevice.getDevice().isTwsPlusDevice())) {
                return;
            }
        } else if (isLeAudioConnected() && this.mCachedDevice.isDualModeDevice() && !this.mCachedDevice.isActiveDevice(22)) {
            setLeAudioDeviceActive();
            return;
        } else if (z && i == 100 && this.mCachedDevice.isConnectedA2dpDevice() && this.mCachedDevice.isActiveDevice(2) && !this.mCachedDevice.isConnectedHfpDevice() && !this.mCachedDevice.isActiveDevice(1) && !isLeAudioConnected()) {
            try {
                Log.d("BluetoothDevicePreference", "connectProfile HEADSET");
                this.mCachedDevice.connect();
                return;
            } catch (Exception unused) {
                Log.d("BluetoothDevicePreference", "Exception error");
                return;
            }
        } else if (z && i2 == 100 && this.mCachedDevice.isConnectedHfpDevice() && this.mCachedDevice.isActiveDevice(1) && !this.mCachedDevice.isConnectedA2dpDevice() && !this.mCachedDevice.isActiveDevice(2) && !isLeAudioConnected()) {
            try {
                Log.d("BluetoothDevicePreference", "connectProfile A2DP");
                this.mCachedDevice.connect();
                return;
            } catch (Exception unused2) {
                Log.d("BluetoothDevicePreference", "Exception error");
                return;
            }
        }
        int bondState = this.mCachedDevice.getBondState();
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        if (this.mCachedDevice.isConnected()) {
            metricsFeatureProvider.action(context, 868, new Pair[0]);
            askDisconnect();
        } else if (bondState == 12) {
            metricsFeatureProvider.action(context, 867, new Pair[0]);
            this.mCachedDevice.connect();
        } else if (bondState == 10) {
            metricsFeatureProvider.action(context, 866, new Pair[0]);
            if (!this.mCachedDevice.hasHumanReadableName()) {
                metricsFeatureProvider.action(context, 1096, new Pair[0]);
            }
            setMTriggerFromAvaliableDevices(true);
            pair();
        }
    }

    public void onDeviceAttributesChanged() {
        this.mHandler.removeMessages(this.UPDATE);
        this.mHandler.sendEmptyMessageDelayed(this.UPDATE, 100L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        this.mCachedDevice.unregisterCallback(this.mCallBack);
        this.mCallBack = null;
        this.mBluetoothA2dp = null;
        AlertDialog alertDialog = this.mSwitchActiveDeviceDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mSwitchActiveDeviceDialog = null;
        }
        AlertDialog alertDialog2 = this.mDisconnectDialog;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
            this.mDisconnectDialog = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void rebind() {
        Log.d("BluetoothDevicePreference", "device: " + this.mCachedDevice.getName() + " rebind()");
        onDeviceAttributesChanged();
        this.mCachedDevice.unregisterCallback(this.mCallBack);
        this.mCachedDevice.registerCallback(this.mCallBack);
    }

    public void setAudioShareJumpPage(AudioShareJumpPage audioShareJumpPage) {
        this.mJumpAttributePage = audioShareJumpPage;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCachedDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        this.mCachedDevice = cachedBluetoothDevice;
    }

    public void setConnectState(int i) {
        this.mHelper.setConnectState(i);
    }

    public void setOnSettingsClickListener(View.OnClickListener onClickListener) {
        this.mOnSettingsClickListener = onClickListener;
    }

    public void updateAttributes() {
        String name = this.mCachedDevice.getName();
        setTitle(name);
        setSummary(this.mCachedDevice.getConnectionSummary());
        Pair<Drawable, String> btClassDrawableWithDescription = getBtClassDrawableWithDescription();
        Object obj = btClassDrawableWithDescription.first;
        if (obj != null) {
            setIcon(((Drawable) obj).mutate());
            this.contentDescription = (String) btClassDrawableWithDescription.second;
        }
        if (this.mCachedDevice.getBondState() == 12 || GattProfile.isBond(this.mCachedDevice.getDevice())) {
            updateOrder(this.mCachedDevice);
            int connectionState = this.mCachedDevice.getConnectionState();
            if (this.mHelper.getConnectState() != connectionState) {
                setConnectState(connectionState);
            }
        }
        boolean z = true;
        setEnabled(!this.mCachedDevice.isBusy());
        if (!this.mShowDevicesWithoutNames && this.mCachedDevice.getAddress().equals(name)) {
            z = false;
        }
        setVisible(z);
        if (this.mNeedNotifyHierarchyChanged) {
            notifyHierarchyChanged();
        }
        Log.d("BluetoothDevicePreference", "device: " + name + " onDeviceAttributesChanged()");
    }

    public void updateCodecIcon(int i) {
        String codecName;
        View view = this.mView;
        if (view != null) {
            TextView textView = (TextView) view.findViewById(16908310);
            TextView textView2 = (TextView) this.mView.findViewById(R.id.iv_codec);
            if (isLeAudioConnected()) {
                codecName = "LC3";
                textView2.setText("LC3");
                textView2.setTextSize(9.6f);
                textView2.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
                int measuredWidth = (int) (textView2.getMeasuredWidth() + (getContext().getResources().getDisplayMetrics().density * 5.0f) + 0.5f);
                boolean z = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
                int i2 = z ? measuredWidth : 0;
                if (z) {
                    measuredWidth = 0;
                }
                textView.setPadding(i2, 0, measuredWidth, 0);
                textView2.setVisibility(0);
            } else {
                codecName = getCodecName(i).toLowerCase().contains("lhdc") ? "LHDC" : getCodecName(i);
            }
            Log.d("BluetoothDevicePreference", "codecType is " + i);
            if (i != -1) {
                textView2.setText(codecName);
                textView2.setTextSize(9.6f);
                textView2.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
                int measuredWidth2 = (int) (textView2.getMeasuredWidth() + (getContext().getResources().getDisplayMetrics().density * 5.0f) + 0.5f);
                boolean z2 = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
                int i3 = z2 ? measuredWidth2 : 0;
                if (z2) {
                    measuredWidth2 = 0;
                }
                textView.setPadding(i3, 0, measuredWidth2, 0);
                textView2.setVisibility(0);
            } else if (!isLeAudioConnected()) {
                textView2.setVisibility(8);
            }
        }
        this.mCurrentCodecType = i;
    }

    public synchronized boolean updateCodecIcon(BluetoothA2dp bluetoothA2dp) {
        boolean z = false;
        if (bluetoothA2dp == null) {
            updateCodecIcon(-1);
            Log.e("BluetoothDevicePreference", "BluetoothA2dp NULL");
            return false;
        }
        BluetoothDevice device = this.mCachedDevice.getDevice();
        if (device == null) {
            return false;
        }
        this.mBluetoothA2dp = bluetoothA2dp;
        BluetoothCodecStatus codecStatus = bluetoothA2dp.getCodecStatus(device);
        BluetoothCodecConfig[] bluetoothCodecConfigArr = null;
        BluetoothCodecConfig codecConfig = codecStatus == null ? null : codecStatus.getCodecConfig();
        if (codecStatus != null) {
            bluetoothCodecConfigArr = codecStatus.getCodecsSelectableCapabilities();
        }
        BluetoothCodecConfig[] bluetoothCodecConfigArr2 = bluetoothCodecConfigArr;
        if (codecConfig != null && bluetoothCodecConfigArr2 != null) {
            int recognizableCodecType = getRecognizableCodecType(codecConfig);
            Log.d("BluetoothDevicePreference", "CodecType : " + recognizableCodecType);
            updateCodecIcon(recognizableCodecType);
            setSummary(this.mCachedDevice.getConnectionSummary());
            String string = Settings.Secure.getString(getContext().getContentResolver(), "miui_bluetooth_lhdc_whitelist_cache");
            if (string != null && string != "") {
                z = true;
            }
            return isNeedShowDialog(recognizableCodecType, codecConfig, bluetoothCodecConfigArr2, bluetoothA2dp, z);
        }
        return false;
    }

    public void updateCodecIconForLeAudio() {
        Log.i("BluetoothDevicePreference", "enter updateCodecIconForLeAudio");
        View view = this.mView;
        if (view != null) {
            TextView textView = (TextView) view.findViewById(16908310);
            TextView textView2 = (TextView) this.mView.findViewById(R.id.iv_codec);
            textView2.setText("LC3");
            textView2.setTextSize(9.6f);
            textView2.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            int measuredWidth = (int) (textView2.getMeasuredWidth() + (getContext().getResources().getDisplayMetrics().density * 5.0f) + 0.5f);
            boolean z = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
            int i = z ? measuredWidth : 0;
            if (z) {
                measuredWidth = 0;
            }
            textView.setPadding(i, 0, measuredWidth, 0);
            textView2.setVisibility(0);
            this.mCurrentCodecType = 20;
        }
    }

    public void updateCodecIconForNoLeAudio() {
        Log.i("BluetoothDevicePreference", "enter updateCodecIconForNoLeAudio");
        View view = this.mView;
        if (view != null) {
            ((TextView) view.findViewById(R.id.iv_codec)).setVisibility(8);
        }
    }
}
