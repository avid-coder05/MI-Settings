package com.android.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.android.settingslib.utils.ThreadUtils;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import miui.bluetooth.ble.MiBleProfile;
import miui.bluetooth.ble.MiBleUnlockProfile;
import miui.provider.ExtraTelephony;

/* loaded from: classes.dex */
public class MiuiSecurityBluetoothMatchDeviceFragment extends Fragment {
    private ImageView mBluetoothDeviceConfirmed;
    private ImageView mBluetoothDeviceDefault;
    private ImageView mBluetoothDeviceLight;
    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    private BluetoothDevice mDevice;
    private TextView mInstructionText;
    private MiuiLockPatternUtils mLockPatternUtils;
    private View mOkButton;
    private MiBleUnlockProfile mUnlockProfile;
    private final String TAG = "MiuiSecurityBluetoothMatchDeviceFragment";
    private boolean mSetKeyOnReady = false;
    private int mSettingStep = 0;
    private byte[] mCachedKeyBytes = null;
    private String mDeviceMajorClass = "";
    private String mDeviceMinorClass = "";
    private String mDeviceType = "";
    boolean isOld = true;
    boolean bonding = false;
    private MiBleUnlockProfile.OnUnlockStateChangeListener mUnlockListener = new MiBleUnlockProfile.OnUnlockStateChangeListener() { // from class: com.android.settings.MiuiSecurityBluetoothMatchDeviceFragment.1
        @Override // miui.bluetooth.ble.MiBleUnlockProfile.OnUnlockStateChangeListener
        public void onUnlocked(byte b) {
            try {
                Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "get state " + ((int) b));
                if (MiuiSecurityBluetoothMatchDeviceFragment.this.mCachedKeyBytes != null && b == 3) {
                    MiuiSecurityBluetoothMatchDeviceFragment.this.mLockPatternUtils.setBluetoothUnlockEnabled(true);
                    MiuiSecurityBluetoothMatchDeviceFragment.this.mLockPatternUtils.setBluetoothAddressToUnlock(MiuiSecurityBluetoothMatchDeviceFragment.this.mDevice.getAddress());
                    MiuiSecurityBluetoothMatchDeviceFragment.this.mLockPatternUtils.setBluetoothNameToUnlock(MiuiSecurityBluetoothMatchDeviceFragment.this.mDevice.getName());
                    MiuiSecurityBluetoothMatchDeviceFragment.this.mLockPatternUtils.setBluetoothKeyToUnlock(Base64.encodeToString(MiuiSecurityBluetoothMatchDeviceFragment.this.mCachedKeyBytes, 0));
                    MiuiSecurityBluetoothMatchDeviceFragment miuiSecurityBluetoothMatchDeviceFragment = MiuiSecurityBluetoothMatchDeviceFragment.this;
                    miuiSecurityBluetoothMatchDeviceFragment.saveDevice(miuiSecurityBluetoothMatchDeviceFragment.getContext(), MiuiSecurityBluetoothMatchDeviceFragment.this.mDevice.getAddress(), MiuiSecurityBluetoothMatchDeviceFragment.this.mDeviceType, MiuiSecurityBluetoothMatchDeviceFragment.this.mDeviceMajorClass, MiuiSecurityBluetoothMatchDeviceFragment.this.mDeviceMinorClass, true);
                    MiuiSecurityBluetoothMatchDeviceFragment.this.mSettingStep = 2;
                    MiuiSecurityBluetoothMatchDeviceFragment.this.getActivity().sendBroadcast(new Intent("com.miui.keyguard.bluetoothdeviceunlock"));
                    MiuiSecurityBluetoothMatchDeviceFragment.this.switchToSucceedLayout();
                } else if (b == 4) {
                    MiuiSecurityBluetoothMatchDeviceFragment.this.mSettingStep = -1;
                    MiuiSecurityBluetoothMatchDeviceFragment.this.mCachedKeyBytes = null;
                    Toast.makeText(MiuiSecurityBluetoothMatchDeviceFragment.this.getContext(), R.string.bluetooth_unlock_reject, 0).show();
                    MiuiSecurityBluetoothMatchDeviceFragment.this.finish();
                } else if (b == 5) {
                    MiuiSecurityBluetoothMatchDeviceFragment.this.mSettingStep = -1;
                    MiuiSecurityBluetoothMatchDeviceFragment.this.mCachedKeyBytes = null;
                    Toast.makeText(MiuiSecurityBluetoothMatchDeviceFragment.this.getContext(), R.string.bluetooth_unlock_unsupport, 0).show();
                    MiuiSecurityBluetoothMatchDeviceFragment.this.finish();
                } else if (MiuiSecurityBluetoothMatchDeviceFragment.this.mCachedKeyBytes != null) {
                    Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "unhandle  " + ((int) b));
                }
            } catch (Exception e) {
                Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "error " + e);
            }
        }
    };
    private MiBleProfile.IProfileStateChangeCallback mProfileStateChangeCallback = new MiBleProfile.IProfileStateChangeCallback() { // from class: com.android.settings.MiuiSecurityBluetoothMatchDeviceFragment.2
        @Override // miui.bluetooth.ble.MiBleProfile.IProfileStateChangeCallback
        public void onState(int i) {
            if (i == 4) {
                MiuiSecurityBluetoothMatchDeviceFragment miuiSecurityBluetoothMatchDeviceFragment = MiuiSecurityBluetoothMatchDeviceFragment.this;
                if (!miuiSecurityBluetoothMatchDeviceFragment.isOld) {
                    miuiSecurityBluetoothMatchDeviceFragment.mUnlockProfile.registerUnlockListener(MiuiSecurityBluetoothMatchDeviceFragment.this.mUnlockListener);
                }
                if (MiuiSecurityBluetoothMatchDeviceFragment.this.mSetKeyOnReady) {
                    MiuiSecurityBluetoothMatchDeviceFragment miuiSecurityBluetoothMatchDeviceFragment2 = MiuiSecurityBluetoothMatchDeviceFragment.this;
                    if (miuiSecurityBluetoothMatchDeviceFragment2.isOld || miuiSecurityBluetoothMatchDeviceFragment2.checkBtBond()) {
                        MiuiSecurityBluetoothMatchDeviceFragment.this.setKeyToDevice();
                    }
                }
            }
        }
    };
    private BroadcastReceiver mBondStatusReceiver = null;

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkBtBond() {
        if (this.bonding || this.isOld || this.mDevice.getBondState() == 12) {
            return true;
        }
        this.bonding = true;
        Log.d("MiuiSecurityBluetoothMatchDeviceFragment", "createbond start !!");
        if (!this.mDevice.createBond()) {
            this.bonding = false;
            Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "error createbond");
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] generateKey() {
        byte[] bArr = new byte[16];
        new SecureRandom().nextBytes(bArr);
        return bArr;
    }

    private void loadDeviceInfo(String str) {
        String str2 = "";
        try {
            str2 = Settings.Global.getString(getContext().getContentResolver(), "com.xiaomi.bluetooth.UNLOCK_DEVICE");
            if (TextUtils.isEmpty(str2) || str2.indexOf(str) == -1) {
                str2 = Settings.Global.getString(getContext().getContentResolver(), "com.xiaomi.bluetooth.UNLOCK_DEVICE_DIRECT");
                if (TextUtils.isEmpty(str2)) {
                    return;
                }
                if (str2.indexOf(str) == -1) {
                    return;
                }
            }
        } catch (Exception e) {
            Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "get Device type failed " + e);
        }
        String[] split = str2.split("\\,");
        if (split == null || split.length != 4) {
            return;
        }
        this.mDeviceMajorClass = split[2];
        this.mDeviceMinorClass = split[3];
        this.mDeviceType = split[1];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveDevice(Context context, String str, String str2, String str3, String str4, boolean z) {
        String str5 = z ? "com.xiaomi.bluetooth.UNLOCK_DEVICE_DIRECT" : "com.xiaomi.bluetooth.UNLOCK_DEVICE";
        try {
            Settings.Global.putString(context.getContentResolver(), str5, str + "," + str2 + "," + str3 + "," + str4);
        } catch (Exception e) {
            Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "unlock save ScanResult failed " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setKeyToDevice() {
        if (this.mUnlockProfile.isReady()) {
            startSendKeyTask();
            return;
        }
        this.mUnlockProfile.connect();
        this.mSetKeyOnReady = true;
    }

    private void startSendKeyTask() {
        this.mSettingStep = 1;
        if (isAdded()) {
            switchToTapConfirmingLayout();
        }
        new AsyncTask<Void, Void, Boolean>() { // from class: com.android.settings.MiuiSecurityBluetoothMatchDeviceFragment.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... voidArr) {
                try {
                    MiuiSecurityBluetoothMatchDeviceFragment miuiSecurityBluetoothMatchDeviceFragment = MiuiSecurityBluetoothMatchDeviceFragment.this;
                    if (miuiSecurityBluetoothMatchDeviceFragment.isOld) {
                        return miuiSecurityBluetoothMatchDeviceFragment.doInBackgroundHandle();
                    }
                    miuiSecurityBluetoothMatchDeviceFragment.mCachedKeyBytes = miuiSecurityBluetoothMatchDeviceFragment.generateKey();
                    if (TextUtils.isEmpty(MiuiSecurityBluetoothMatchDeviceFragment.this.mDeviceType)) {
                        MiuiSecurityBluetoothMatchDeviceFragment.this.mUnlockProfile.setLock(new String(MiuiSecurityBluetoothMatchDeviceFragment.this.mCachedKeyBytes));
                    } else {
                        MiBleUnlockProfile miBleUnlockProfile = MiuiSecurityBluetoothMatchDeviceFragment.this.mUnlockProfile;
                        MiuiSecurityBluetoothMatchDeviceFragment miuiSecurityBluetoothMatchDeviceFragment2 = MiuiSecurityBluetoothMatchDeviceFragment.this;
                        miBleUnlockProfile.setLock(miuiSecurityBluetoothMatchDeviceFragment2.bytesToHexString(miuiSecurityBluetoothMatchDeviceFragment2.mCachedKeyBytes));
                    }
                    return Boolean.TRUE;
                } catch (Exception e) {
                    Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "error to do background " + e);
                    return Boolean.FALSE;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean bool) {
                super.onPostExecute((AnonymousClass4) bool);
                MiuiSecurityBluetoothMatchDeviceFragment miuiSecurityBluetoothMatchDeviceFragment = MiuiSecurityBluetoothMatchDeviceFragment.this;
                if (miuiSecurityBluetoothMatchDeviceFragment.isOld) {
                    miuiSecurityBluetoothMatchDeviceFragment.doPostHandle(bool);
                }
            }

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                super.onPreExecute();
                MiuiSecurityBluetoothMatchDeviceFragment.this.mSetKeyOnReady = false;
            }
        }.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchToSucceedLayout() {
        this.mInstructionText.setText(R.string.bluetooth_unlock_device_matched_text);
        this.mBluetoothDeviceLight.clearAnimation();
        this.mBluetoothDeviceLight.setVisibility(8);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        AlphaAnimation alphaAnimation2 = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500L);
        alphaAnimation2.setDuration(500L);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.settings.MiuiSecurityBluetoothMatchDeviceFragment.5
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                MiuiSecurityBluetoothMatchDeviceFragment.this.mBluetoothDeviceDefault.setVisibility(8);
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }
        });
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.settings.MiuiSecurityBluetoothMatchDeviceFragment.6
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                MiuiSecurityBluetoothMatchDeviceFragment.this.mBluetoothDeviceConfirmed.setVisibility(0);
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }
        });
        this.mBluetoothDeviceDefault.startAnimation(alphaAnimation);
        this.mBluetoothDeviceConfirmed.startAnimation(alphaAnimation2);
        this.mOkButton.setVisibility(0);
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.MiuiSecurityBluetoothMatchDeviceFragment.7
            @Override // java.lang.Runnable
            public void run() {
                try {
                    HashMap hashMap = new HashMap();
                    MiStatInterfaceUtils.trackPreferenceValue("bluetooth_device_unlock", "on");
                    hashMap.put("bluetooth_device_unlock_status", Boolean.TRUE);
                    OneTrackInterfaceUtils.track("bluetooth_device_unlock", hashMap);
                    Log.v("MiuiSecurityBluetoothMatchDeviceFragment", "track bluetooth_device_unlock success");
                } catch (Exception e) {
                    Log.v("MiuiSecurityBluetoothMatchDeviceFragment", "track bluetooth_device_unlock failed " + e);
                }
            }
        });
    }

    private void switchToTapConfirmingLayout() {
        int width = this.mBluetoothDeviceDefault.getWidth() < this.mBluetoothDeviceDefault.getHeight() ? this.mBluetoothDeviceDefault.getWidth() : this.mBluetoothDeviceDefault.getHeight();
        String name = this.mDevice.getName();
        if (!TextUtils.isEmpty(this.mDeviceMajorClass) && "1".equals(this.mDeviceMajorClass)) {
            this.mInstructionText.setText(R.string.bluetooth_unlock_confirm_device_text_for_mi_wear);
        } else if ("MI Band 2".equalsIgnoreCase(name) || "Mi Band 3".equalsIgnoreCase(name)) {
            this.mInstructionText.setText(R.string.bluetooth_unlock_confirm_device_text_for_miband2);
        } else if (name == null || !name.startsWith("Amazfit Watch")) {
            this.mInstructionText.setText(R.string.bluetooth_unlock_confirm_device_text);
        } else {
            this.mInstructionText.setText(R.string.bluetooth_unlock_confirm_device_text_for_huami_watch);
        }
        this.mBluetoothDeviceLight.setVisibility(0);
        ViewGroup.LayoutParams layoutParams = this.mBluetoothDeviceLight.getLayoutParams();
        int i = width / 18;
        layoutParams.width = i;
        layoutParams.height = i;
        this.mBluetoothDeviceLight.setLayoutParams(layoutParams);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 9.0f, 1.0f, 9.0f, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setRepeatMode(1);
        scaleAnimation.setRepeatCount(-1);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        alphaAnimation.setRepeatMode(1);
        alphaAnimation.setRepeatCount(-1);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setDuration(2000L);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        this.mBluetoothDeviceLight.startAnimation(animationSet);
    }

    public String bytesToHexString(byte[] bArr) {
        try {
            StringBuilder sb = new StringBuilder(bArr.length);
            for (byte b : bArr) {
                String hexString = Integer.toHexString(b & 255);
                if (hexString.length() < 2) {
                    sb.append(0);
                }
                sb.append(hexString.toUpperCase());
            }
            return sb.toString();
        } catch (Exception e) {
            Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "error when bytesToHexString" + e);
            return "";
        }
    }

    public Boolean doInBackgroundHandle() {
        byte[] generateKey = generateKey();
        if (!this.mUnlockProfile.setLock(new String(generateKey))) {
            Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "doInBackgroundHandle setlock failed disconnect");
            this.mUnlockProfile.disconnect();
            return Boolean.FALSE;
        }
        Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "doInBackgroundHandle disconnect");
        this.mUnlockProfile.disconnect();
        this.mLockPatternUtils.setBluetoothUnlockEnabled(true);
        this.mLockPatternUtils.setBluetoothAddressToUnlock(this.mDevice.getAddress());
        this.mLockPatternUtils.setBluetoothNameToUnlock(this.mDevice.getName());
        this.mLockPatternUtils.setBluetoothKeyToUnlock(Base64.encodeToString(generateKey, 0));
        String string = Settings.Global.getString(getContext().getContentResolver(), "mi_band_hid_support");
        if (string != null && string.equals(this.mDevice.getAddress()) && this.mDevice.getBondState() == 10) {
            this.mDevice.createBond();
        }
        return Boolean.TRUE;
    }

    public void doPostHandle(Boolean bool) {
        if (!bool.booleanValue()) {
            this.mSettingStep = -1;
            return;
        }
        this.mSettingStep = 2;
        if (isAdded()) {
            getActivity().sendBroadcast(new Intent("com.miui.keyguard.bluetoothdeviceunlock"));
            switchToSucceedLayout();
        }
    }

    public void finish() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (isResumed()) {
            activity.onBackPressed();
        } else {
            activity.finish();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 100) {
            if (i2 != -1) {
                finish();
            } else if (this.isOld || checkBtBond()) {
                setKeyToDevice();
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        this.mLockPatternUtils = new MiuiLockPatternUtils(getActivity());
        this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(100).build();
        Intent intent = getActivity().getIntent();
        Bundle bundleExtra = intent.getBundleExtra(":android:show_fragment_args");
        String string = bundleExtra != null ? bundleExtra.getString("device_address") : null;
        if (TextUtils.isEmpty(string)) {
            string = intent.getStringExtra("device_address");
        }
        if (TextUtils.isEmpty(string) && getArguments() != null) {
            string = getArguments().getString("device_address");
        }
        if (TextUtils.isEmpty(string)) {
            finish();
            return;
        }
        this.mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(string);
        try {
            Intent intent2 = new Intent("miui.intent.action.SETUNLOCK");
            intent2.setPackage("com.xiaomi.bluetooth");
            getActivity().sendBroadcast(intent2);
            Settings.Global.putString(getContext().getContentResolver(), "miui.bluetooth.SETUNLOCKTIME", String.valueOf(new Date().getTime()));
        } catch (Exception e) {
            Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "error " + e);
        }
        this.mBondStatusReceiver = new BroadcastReceiver() { // from class: com.android.settings.MiuiSecurityBluetoothMatchDeviceFragment.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent3) {
                int intExtra;
                try {
                    if (intent3.getAction().equals("android.bluetooth.device.action.BOND_STATE_CHANGED")) {
                        BluetoothDevice bluetoothDevice = (BluetoothDevice) intent3.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                        if (bluetoothDevice.getAddress().equals(MiuiSecurityBluetoothMatchDeviceFragment.this.mDevice.getAddress()) && (intExtra = intent3.getIntExtra("android.bluetooth.device.extra.BOND_STATE", 10)) != 11) {
                            if (intExtra != 12) {
                                MiuiSecurityBluetoothMatchDeviceFragment.this.bonding = false;
                            } else if (bluetoothDevice.getType() != 1) {
                                MiuiSecurityBluetoothMatchDeviceFragment miuiSecurityBluetoothMatchDeviceFragment = MiuiSecurityBluetoothMatchDeviceFragment.this;
                                miuiSecurityBluetoothMatchDeviceFragment.bonding = false;
                                if (miuiSecurityBluetoothMatchDeviceFragment.isOld) {
                                    return;
                                }
                                miuiSecurityBluetoothMatchDeviceFragment.setKeyToDevice();
                                Log.d("MiuiSecurityBluetoothMatchDeviceFragment", "onReceive:BOND_BONDED: calling connectGatt device=" + bluetoothDevice);
                            }
                        }
                    }
                } catch (Exception e2) {
                    Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "handle bond failed " + e2);
                }
            }
        };
        try {
            getContext().registerReceiver(this.mBondStatusReceiver, new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED"));
            this.mDeviceType = getArguments().getString("DEVICE_TYPE");
            this.mDeviceMajorClass = getArguments().getString("DEVICE_TYPE_MAJOR");
            this.mDeviceMinorClass = getArguments().getString("DEVICE_TYPE_MINOR");
            Log.d("MiuiSecurityBluetoothMatchDeviceFragment", "get the device info = " + this.mDeviceType + " " + this.mDeviceMajorClass + " " + this.mDeviceMinorClass);
            if (TextUtils.isEmpty(this.mDeviceType)) {
                loadDeviceInfo(string);
            } else {
                saveDevice(getContext(), this.mDevice.getAddress(), this.mDeviceType, this.mDeviceMajorClass, this.mDeviceMinorClass, false);
            }
        } catch (Exception e2) {
            Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "create failed " + e2);
        }
        if (!TextUtils.isEmpty(this.mDeviceType)) {
            this.isOld = false;
        }
        MiBleUnlockProfile miBleUnlockProfile = new MiBleUnlockProfile(getActivity(), this.mDevice.getAddress(), this.mProfileStateChangeCallback);
        this.mUnlockProfile = miBleUnlockProfile;
        miBleUnlockProfile.connect();
        if (!(getArguments() != null ? getArguments().getBoolean("password_confirmed") : false)) {
            this.mChooseLockSettingsHelper.launch();
        } else if (this.isOld || checkBtBond()) {
            setKeyToDevice();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.security_bluetooth_device_match_layout, (ViewGroup) null);
        this.mInstructionText = (TextView) inflate.findViewById(R.id.instruction_text);
        this.mBluetoothDeviceDefault = (ImageView) inflate.findViewById(R.id.bluetooth_device_default);
        this.mBluetoothDeviceConfirmed = (ImageView) inflate.findViewById(R.id.bluetooth_device_confirmed);
        this.mBluetoothDeviceLight = (ImageView) inflate.findViewById(R.id.bluetooth_device_light);
        this.mOkButton = inflate.findViewById(R.id.ok_button);
        Log.e("MiuiSecurityBluetoothMatchDeviceFragment", " " + this.mDeviceMajorClass + this.mDeviceMinorClass);
        if (TextUtils.isEmpty(this.mDeviceMajorClass) || !"1".equals(this.mDeviceMajorClass)) {
            String name = this.mDevice.getName();
            if ("MI Band 2".equalsIgnoreCase(name) || "Mi Band 3".equalsIgnoreCase(name)) {
                this.mBluetoothDeviceDefault.setImageResource(R.drawable.bluetooth_device_unlock_default_for_miband2);
                this.mBluetoothDeviceConfirmed.setImageResource(R.drawable.bluetooth_device_unlock_confirmed_for_miband2);
            } else if (name != null && name.startsWith("Amazfit Watch")) {
                this.mBluetoothDeviceDefault.setImageResource(R.drawable.bluetooth_device_unlock_default_for_huami_watch);
                this.mBluetoothDeviceConfirmed.setImageResource(R.drawable.bluetooth_device_unlock_confirmed_for_huami_watch);
            }
        } else {
            Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "device info " + this.mDeviceMajorClass + this.mDeviceMinorClass);
            if ("1".equals(this.mDeviceMinorClass) || "2".equals(this.mDeviceMinorClass)) {
                this.mBluetoothDeviceDefault.setImageResource(R.drawable.unlock_01_bind);
                this.mBluetoothDeviceConfirmed.setImageResource(R.drawable.unlock_01_granted);
            } else if (ExtraTelephony.Phonelist.TYPE_VIP.equals(this.mDeviceMinorClass)) {
                this.mBluetoothDeviceDefault.setImageResource(R.drawable.unlock_03_bind);
                this.mBluetoothDeviceConfirmed.setImageResource(R.drawable.unlock_03_granted);
            } else if (ExtraTelephony.Phonelist.TYPE_CLOUDS_BLACK.equals(this.mDeviceMinorClass)) {
                this.mBluetoothDeviceDefault.setImageResource(R.drawable.unlock_04_bind);
                this.mBluetoothDeviceConfirmed.setImageResource(R.drawable.unlock_04_granted);
            }
        }
        this.mInstructionText.setText(R.string.bluetooth_searching_for_devices);
        this.mBluetoothDeviceLight.setVisibility(8);
        this.mOkButton.setVisibility(4);
        this.mOkButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MiuiSecurityBluetoothMatchDeviceFragment.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (MiuiSecurityBluetoothMatchDeviceFragment.this.getTargetFragment() != null) {
                    MiuiSecurityBluetoothMatchDeviceFragment.this.finish();
                    return;
                }
                MiuiSecurityBluetoothMatchDeviceFragment.this.getActivity().setResult(-1);
                MiuiSecurityBluetoothMatchDeviceFragment.this.finish();
            }
        });
        super.onCreateView(layoutInflater, viewGroup, bundle);
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        try {
            if (this.mUnlockProfile != null) {
                if (!this.isOld) {
                    Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "onDestroy disconnect");
                    this.mUnlockProfile.unregisterUnlockListener();
                }
                this.mUnlockProfile.disconnect();
            }
            if (this.mBondStatusReceiver != null) {
                getContext().unregisterReceiver(this.mBondStatusReceiver);
                this.mBondStatusReceiver = null;
            }
        } catch (Exception e) {
            Log.e("MiuiSecurityBluetoothMatchDeviceFragment", "error to destory " + e);
        }
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        int i = this.mSettingStep;
        if (i == 1) {
            switchToTapConfirmingLayout();
        } else if (i == 2) {
            switchToSucceedLayout();
        }
    }
}
