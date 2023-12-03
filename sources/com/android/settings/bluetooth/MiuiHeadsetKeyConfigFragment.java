package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.bluetooth.ble.app.IMiuiHeadsetService;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.provider.ExtraTelephony;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;
import miuix.preference.DropDownPreference;

/* loaded from: classes.dex */
public final class MiuiHeadsetKeyConfigFragment extends MiuiSettingsPreferenceFragment {
    private BluetoothDevice mDevice;
    private DropDownPreference mDoubleClickLeft;
    private DropDownPreference mDoubleClickRight;
    private DropDownPreference mDropdownPrefLeft;
    private DropDownPreference mDropdownPrefRight;
    private MiuiHeadsetActivity mHeadSetAct;
    private View mRootView;
    private DropDownPreference mTripleClickLeft;
    private DropDownPreference mTripleClickRight;
    private ValuePreference pref_left;
    private ValuePreference pref_right;
    private IMiuiHeadsetService mService = null;
    private String mSupport = "";
    private String mDeviceId = "";
    private String PRESS_KEY_INIT = "000011101110";
    public int mLeftDoubleKey = 0;
    public int mLeftTripleKey = 0;
    public int mRightDoubleKey = 0;
    public int mRightTripleKey = 0;
    public boolean mLeftKey = false;
    public int mDropdownLeftKey = 0;
    public boolean mRightKey = false;
    public int mDropdownRightKey = 0;
    private boolean mDeviceConnected = true;
    private final BroadcastReceiver mBluetoothA2dpReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.MiuiHeadsetKeyConfigFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("MiuiHeadsetKeyConfigFragment", "LDAC: mBluetoothA2dpReceiver.onReceive intent=" + intent);
            if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Log.d("MiuiHeadsetKeyConfigFragment", "state changed " + intExtra + ", " + bluetoothDevice);
                if (intExtra == 0 && bluetoothDevice != null && bluetoothDevice.equals(MiuiHeadsetKeyConfigFragment.this.mDevice)) {
                    MiuiHeadsetKeyConfigFragment.this.mDeviceConnected = false;
                } else if (intExtra == 2 && bluetoothDevice != null && bluetoothDevice.equals(MiuiHeadsetKeyConfigFragment.this.mDevice)) {
                    MiuiHeadsetKeyConfigFragment.this.mDeviceConnected = true;
                }
                MiuiHeadsetKeyConfigFragment miuiHeadsetKeyConfigFragment = MiuiHeadsetKeyConfigFragment.this;
                miuiHeadsetKeyConfigFragment.setPreferenceEnable(miuiHeadsetKeyConfigFragment.mDeviceConnected);
            }
        }
    };
    private final Preference.OnPreferenceChangeListener mPrefChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetKeyConfigFragment.2
        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            try {
                if (MiuiHeadsetKeyConfigFragment.this.mService != null && MiuiHeadsetKeyConfigFragment.this.mDevice != null) {
                    boolean isTWS01Headset = HeadsetIDConstants.isTWS01Headset(MiuiHeadsetKeyConfigFragment.this.mDeviceId);
                    boolean isK77sHeadset = HeadsetIDConstants.isK77sHeadset(MiuiHeadsetKeyConfigFragment.this.mDeviceId);
                    String str = "";
                    String key = preference.getKey();
                    char c = 65535;
                    switch (key.hashCode()) {
                        case -2077954079:
                            if (key.equals("right_triple")) {
                                c = 3;
                                break;
                            }
                            break;
                        case -1725058528:
                            if (key.equals("long_press_right_headset")) {
                                c = 5;
                                break;
                            }
                            break;
                        case -1456925943:
                            if (key.equals("left_double")) {
                                c = 0;
                                break;
                            }
                            break;
                        case -996433002:
                            if (key.equals("left_triple")) {
                                c = 2;
                                break;
                            }
                            break;
                        case 221811561:
                            if (key.equals("long_press_left_headset")) {
                                c = 4;
                                break;
                            }
                            break;
                        case 1756520276:
                            if (key.equals("right_double")) {
                                c = 1;
                                break;
                            }
                            break;
                    }
                    if (c == 0) {
                        if (!isTWS01Headset) {
                            str = ((String) obj).equals("0") ? "0103FF" : "0104FF";
                            MiuiHeadsetKeyConfigFragment.this.mLeftDoubleKey = ((String) obj).equals("0") ? 0 : 1;
                        } else if ("0".equals((String) obj)) {
                            str = "0101FF";
                            MiuiHeadsetKeyConfigFragment.this.mLeftDoubleKey = Integer.valueOf("0").intValue();
                        } else if ("1".equals((String) obj)) {
                            MiuiHeadsetKeyConfigFragment.this.mLeftDoubleKey = Integer.valueOf("1").intValue();
                            str = "0103FF";
                        } else if ("2".equals((String) obj)) {
                            str = "0102FF";
                            MiuiHeadsetKeyConfigFragment.this.mLeftDoubleKey = Integer.valueOf("2").intValue();
                        }
                        Log.d("MiuiHeadsetKeyConfigFragment", "left double: " + str + "==" + Integer.parseInt(str, 16));
                        MiuiHeadsetKeyConfigFragment.this.mService.setFunKey(0, Integer.parseInt(str, 16), MiuiHeadsetKeyConfigFragment.this.mDevice);
                    } else if (c == 1) {
                        if (!isTWS01Headset) {
                            str = ((String) obj).equals("0") ? "01FF03" : "01FF04";
                            MiuiHeadsetKeyConfigFragment.this.mRightDoubleKey = ((String) obj).equals("0") ? 0 : 1;
                        } else if ("0".equals((String) obj)) {
                            str = "01FF01";
                            MiuiHeadsetKeyConfigFragment.this.mRightDoubleKey = Integer.valueOf("0").intValue();
                        } else if ("1".equals((String) obj)) {
                            MiuiHeadsetKeyConfigFragment.this.mRightDoubleKey = Integer.valueOf("1").intValue();
                            str = "01FF03";
                        } else if ("2".equals((String) obj)) {
                            str = "01FF02";
                            MiuiHeadsetKeyConfigFragment.this.mRightDoubleKey = Integer.valueOf("2").intValue();
                        }
                        Log.d("MiuiHeadsetKeyConfigFragment", "right double: " + str + "==" + Integer.parseInt(str, 16));
                        MiuiHeadsetKeyConfigFragment.this.mService.setFunKey(0, Integer.parseInt(str, 16), MiuiHeadsetKeyConfigFragment.this.mDevice);
                    } else if (c == 2) {
                        if (!isTWS01Headset) {
                            str = ((String) obj).equals("0") ? "0202FF" : "0205FF";
                            MiuiHeadsetKeyConfigFragment.this.mLeftTripleKey = ((String) obj).equals("0") ? 0 : 1;
                        } else if ("0".equals((String) obj)) {
                            str = "0203FF";
                            MiuiHeadsetKeyConfigFragment.this.mLeftTripleKey = Integer.valueOf("0").intValue();
                        } else if ("1".equals((String) obj)) {
                            MiuiHeadsetKeyConfigFragment.this.mLeftTripleKey = Integer.valueOf("1").intValue();
                            str = "0202FF";
                        } else if ("2".equals((String) obj)) {
                            str = "0204FF";
                            MiuiHeadsetKeyConfigFragment.this.mLeftTripleKey = Integer.valueOf("2").intValue();
                        } else if (ExtraTelephony.Phonelist.TYPE_VIP.equals((String) obj)) {
                            MiuiHeadsetKeyConfigFragment.this.mLeftTripleKey = Integer.valueOf(ExtraTelephony.Phonelist.TYPE_VIP).intValue();
                            str = "0205FF";
                        }
                        Log.d("MiuiHeadsetKeyConfigFragment", "left triple: " + str + "==" + Integer.parseInt(str, 16));
                        MiuiHeadsetKeyConfigFragment.this.mService.setFunKey(0, Integer.parseInt(str, 16), MiuiHeadsetKeyConfigFragment.this.mDevice);
                    } else if (c == 3) {
                        if (!isTWS01Headset) {
                            str = ((String) obj).equals("0") ? "02FF02" : "02FF05";
                            MiuiHeadsetKeyConfigFragment.this.mRightTripleKey = ((String) obj).equals("0") ? 0 : 1;
                        } else if ("0".equals((String) obj)) {
                            str = "02FF03";
                            MiuiHeadsetKeyConfigFragment.this.mRightTripleKey = Integer.valueOf("0").intValue();
                        } else if ("1".equals((String) obj)) {
                            MiuiHeadsetKeyConfigFragment.this.mRightTripleKey = Integer.valueOf("1").intValue();
                            str = "02FF02";
                        } else if ("2".equals((String) obj)) {
                            str = "02FF04";
                            MiuiHeadsetKeyConfigFragment.this.mRightTripleKey = Integer.valueOf("2").intValue();
                        } else if (ExtraTelephony.Phonelist.TYPE_VIP.equals((String) obj)) {
                            MiuiHeadsetKeyConfigFragment.this.mRightTripleKey = Integer.valueOf(ExtraTelephony.Phonelist.TYPE_VIP).intValue();
                            str = "02FF05";
                        }
                        Log.d("MiuiHeadsetKeyConfigFragment", "right triple: " + str + "==" + Integer.parseInt(str, 16));
                        MiuiHeadsetKeyConfigFragment.this.mService.setFunKey(0, Integer.parseInt(str, 16), MiuiHeadsetKeyConfigFragment.this.mDevice);
                    } else if (c != 4) {
                        if (c == 5) {
                            if (isK77sHeadset) {
                                String str2 = "0".equals((String) obj) ? "03FF00" : "03FF01";
                                MiuiHeadsetKeyConfigFragment.this.mDropdownRightKey = ((String) obj).equals("0") ? 0 : 1;
                                Log.d("MiuiHeadsetKeyConfigFragment", "right long press: " + str2 + "==" + Integer.parseInt(str2, 16));
                                MiuiHeadsetKeyConfigFragment.this.mService.setFunKey(0, Integer.parseInt(str2, 16), MiuiHeadsetKeyConfigFragment.this.mDevice);
                            } else {
                                Log.e("MiuiHeadsetKeyConfigFragment", "right long press: unsupport device:" + MiuiHeadsetKeyConfigFragment.this.mDeviceId);
                            }
                        }
                    } else if (isK77sHeadset) {
                        String str3 = "0".equals((String) obj) ? "0300FF" : "0301FF";
                        MiuiHeadsetKeyConfigFragment.this.mDropdownLeftKey = ((String) obj).equals("0") ? 0 : 1;
                        Log.d("MiuiHeadsetKeyConfigFragment", "left long press: " + str3 + "==" + Integer.parseInt(str3, 16));
                        MiuiHeadsetKeyConfigFragment.this.mService.setFunKey(0, Integer.parseInt(str3, 16), MiuiHeadsetKeyConfigFragment.this.mDevice);
                    } else {
                        Log.e("MiuiHeadsetKeyConfigFragment", "left long press: unsupport device:" + MiuiHeadsetKeyConfigFragment.this.mDeviceId);
                    }
                    MiuiHeadsetKeyConfigFragment.this.updateKeyConfig();
                    return true;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("preference changed service: ");
                sb.append(MiuiHeadsetKeyConfigFragment.this.mService == null);
                sb.append(", device: ");
                sb.append(MiuiHeadsetKeyConfigFragment.this.mDevice == null);
                Log.e("MiuiHeadsetKeyConfigFragment", sb.toString());
                MiuiHeadsetKeyConfigFragment.this.mDoubleClickLeft.setValueIndex(MiuiHeadsetKeyConfigFragment.this.mLeftDoubleKey);
                MiuiHeadsetKeyConfigFragment.this.mTripleClickLeft.setValueIndex(MiuiHeadsetKeyConfigFragment.this.mLeftTripleKey);
                MiuiHeadsetKeyConfigFragment.this.mDoubleClickRight.setValueIndex(MiuiHeadsetKeyConfigFragment.this.mRightDoubleKey);
                MiuiHeadsetKeyConfigFragment.this.mTripleClickRight.setValueIndex(MiuiHeadsetKeyConfigFragment.this.mRightTripleKey);
                return false;
            } catch (Exception e) {
                Log.e("MiuiHeadsetKeyConfigFragment", "ser preferernc listener error: " + e);
                return false;
            }
        }
    };

    private String getRadioButtonConfig() {
        BluetoothDevice bluetoothDevice;
        try {
            IMiuiHeadsetService iMiuiHeadsetService = this.mService;
            if (iMiuiHeadsetService != null && (bluetoothDevice = this.mDevice) != null) {
                String commonCommand = iMiuiHeadsetService.setCommonCommand(106, "", bluetoothDevice);
                if (commonCommand == null || "".equals(commonCommand) || commonCommand.length() != 12) {
                    this.mService.setCommonCommand(105, this.PRESS_KEY_INIT, this.mDevice);
                    commonCommand = this.PRESS_KEY_INIT;
                }
                saveCurrentKeyConfig(commonCommand);
                Log.d("MiuiHeadsetKeyConfigFragment", "get radio button is: " + commonCommand);
                return commonCommand;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("getRadioButtonConfig(): ");
            boolean z = true;
            sb.append(this.mService == null);
            sb.append(", ");
            if (this.mDevice != null) {
                z = false;
            }
            sb.append(z);
            Log.d("MiuiHeadsetKeyConfigFragment", sb.toString());
            return this.PRESS_KEY_INIT;
        } catch (Exception unused) {
            return this.PRESS_KEY_INIT;
        }
    }

    private void gotoPressKeyFragment(String str) {
        if (str == null || "".equals(str) || !("left".equals(str) || "right".equals(str))) {
            Log.e("MiuiHeadsetKeyConfigFragment", "go to fragment presskey, it's title is null");
            return;
        }
        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment = new MiuiHeadsetPressKeyFragment();
        miuiHeadsetPressKeyFragment.setTitleKey(str);
        this.mHeadSetAct.changeFragment(miuiHeadsetPressKeyFragment);
    }

    public static byte[] hexToByteArray(String str) {
        byte[] bArr = new byte[str.length()];
        int i = 0;
        while (i < str.length()) {
            try {
                int i2 = i + 1;
                bArr[i] = (byte) Integer.parseInt(str.substring(i, i2));
                i = i2;
            } catch (Exception e) {
                Log.w("MiuiHeadsetKeyConfigFragment", "hexToByteArray error " + e);
            }
        }
        return bArr;
    }

    private void initKeyConfig() {
        String radioButtonConfig = getRadioButtonConfig();
        Log.d("MiuiHeadsetKeyConfigFragment", "radio button is: " + radioButtonConfig);
        byte[] hexToByteArray = hexToByteArray(radioButtonConfig);
        this.mLeftDoubleKey = hexToByteArray[0];
        this.mLeftTripleKey = hexToByteArray[2];
        this.mRightDoubleKey = hexToByteArray[1];
        this.mRightTripleKey = hexToByteArray[3];
        this.mLeftKey = hexToByteArray[4] != 0;
        this.mDropdownLeftKey = hexToByteArray[4];
        this.mRightKey = hexToByteArray[8] != 0;
        this.mDropdownRightKey = hexToByteArray[8];
    }

    private void initResource() {
        this.mDoubleClickLeft = (DropDownPreference) findPreference("left_double");
        this.mDoubleClickRight = (DropDownPreference) findPreference("right_double");
        this.mTripleClickLeft = (DropDownPreference) findPreference("left_triple");
        this.mTripleClickRight = (DropDownPreference) findPreference("right_triple");
        DropDownPreference dropDownPreference = this.mDoubleClickLeft;
        if (dropDownPreference != null) {
            dropDownPreference.setValueIndex(this.mLeftDoubleKey);
            this.mDoubleClickLeft.setOnPreferenceChangeListener(this.mPrefChangeListener);
        }
        DropDownPreference dropDownPreference2 = this.mDoubleClickRight;
        if (dropDownPreference2 != null) {
            dropDownPreference2.setValueIndex(this.mRightDoubleKey);
            this.mDoubleClickRight.setOnPreferenceChangeListener(this.mPrefChangeListener);
        }
        DropDownPreference dropDownPreference3 = this.mTripleClickLeft;
        if (dropDownPreference3 != null) {
            dropDownPreference3.setValueIndex(this.mLeftTripleKey);
            this.mTripleClickLeft.setOnPreferenceChangeListener(this.mPrefChangeListener);
        }
        DropDownPreference dropDownPreference4 = this.mTripleClickRight;
        if (dropDownPreference4 != null) {
            dropDownPreference4.setValueIndex(this.mRightTripleKey);
            this.mTripleClickRight.setOnPreferenceChangeListener(this.mPrefChangeListener);
        }
        if (HeadsetIDConstants.isK77sHeadset(this.mDeviceId)) {
            this.mDropdownPrefLeft = (DropDownPreference) findPreference("long_press_left_headset");
            this.mDropdownPrefRight = (DropDownPreference) findPreference("long_press_right_headset");
            DropDownPreference dropDownPreference5 = this.mDropdownPrefLeft;
            if (dropDownPreference5 != null) {
                dropDownPreference5.setValueIndex(this.mDropdownLeftKey);
                this.mDropdownPrefLeft.setOnPreferenceChangeListener(this.mPrefChangeListener);
            }
            DropDownPreference dropDownPreference6 = this.mDropdownPrefRight;
            if (dropDownPreference6 != null) {
                dropDownPreference6.setValueIndex(this.mDropdownRightKey);
                this.mDropdownPrefRight.setOnPreferenceChangeListener(this.mPrefChangeListener);
            }
        } else {
            this.pref_left = (ValuePreference) findPreference("long_press_left_headset");
            this.pref_right = (ValuePreference) findPreference("long_press_right_headset");
            this.pref_left.setValue(this.mLeftKey ? R.string.miheadset_key_config_noise_control : R.string.miheadset_key_config_call_ai);
            this.pref_right.setValue(this.mRightKey ? R.string.miheadset_key_config_noise_control : R.string.miheadset_key_config_call_ai);
        }
        setPreferenceEnable(this.mDeviceConnected);
    }

    private void saveCurrentKeyConfig(String str) {
        this.PRESS_KEY_INIT = str;
        this.mHeadSetAct.setDeviceConfig(str);
        Bundle bundle = new Bundle();
        bundle.putString("Headset_Key_Init", str);
        bundle.putString("Headset_DeviceId", this.mDeviceId);
        setArguments(bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPreferenceEnable(boolean z) {
        Log.d("MiuiHeadsetKeyConfigFragment", "setPreferenceEnable " + z);
        DropDownPreference dropDownPreference = this.mDoubleClickLeft;
        if (dropDownPreference != null) {
            dropDownPreference.setEnabled(z);
        }
        DropDownPreference dropDownPreference2 = this.mDoubleClickRight;
        if (dropDownPreference2 != null) {
            dropDownPreference2.setEnabled(z);
        }
        DropDownPreference dropDownPreference3 = this.mTripleClickLeft;
        if (dropDownPreference3 != null) {
            dropDownPreference3.setEnabled(z);
        }
        DropDownPreference dropDownPreference4 = this.mTripleClickRight;
        if (dropDownPreference4 != null) {
            dropDownPreference4.setEnabled(z);
        }
        if (HeadsetIDConstants.isK77sHeadset(this.mDeviceId)) {
            DropDownPreference dropDownPreference5 = this.mDropdownPrefLeft;
            if (dropDownPreference5 != null) {
                dropDownPreference5.setEnabled(z);
            }
            DropDownPreference dropDownPreference6 = this.mDropdownPrefRight;
            if (dropDownPreference6 != null) {
                dropDownPreference6.setEnabled(z);
                return;
            }
            return;
        }
        ValuePreference valuePreference = this.pref_left;
        if (valuePreference != null) {
            valuePreference.setEnabled(z);
        }
        ValuePreference valuePreference2 = this.pref_right;
        if (valuePreference2 != null) {
            valuePreference2.setEnabled(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateKeyConfig() {
        String str;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("updateKeyConfig:");
            boolean z = true;
            sb.append(this.mService != null);
            sb.append(", ");
            if (this.mDevice == null) {
                z = false;
            }
            sb.append(z);
            Log.d("MiuiHeadsetKeyConfigFragment", sb.toString());
            if (this.mService == null || this.mDevice == null) {
                return;
            }
            String radioButtonConfig = getRadioButtonConfig();
            String str2 = String.valueOf(this.mLeftDoubleKey) + String.valueOf(this.mRightDoubleKey) + String.valueOf(this.mLeftTripleKey) + String.valueOf(this.mRightTripleKey);
            if (HeadsetIDConstants.isK77sHeadset(this.mDeviceId)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str2);
                String str3 = "0000";
                sb2.append(this.mDropdownLeftKey == 0 ? "0000" : "1000");
                if (this.mDropdownRightKey != 0) {
                    str3 = "1000";
                }
                sb2.append(str3);
                str = sb2.toString();
            } else {
                str = str2 + radioButtonConfig.substring(4);
            }
            this.mService.setCommonCommand(105, str, this.mDevice);
            saveCurrentKeyConfig(str);
        } catch (Exception e) {
            Log.e("MiuiHeadsetKeyConfigFragment", "Get device load list failed: " + e);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiHeadsetKeyConfigFragment.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        if (HeadsetIDConstants.isTWS01Headset(this.mDeviceId)) {
            Log.e("MiuiHeadsetKeyConfigFragment", "K76 device ID ");
            return R.xml.headset_key_config_TWS01;
        } else if (HeadsetIDConstants.isK77sHeadset(this.mDeviceId)) {
            Log.d("MiuiHeadsetKeyConfigFragment", "K77s device ID ");
            return R.xml.headset_key_config_tws_k77s;
        } else if (HeadsetIDConstants.isK73Headset(this.mDeviceId)) {
            Log.d("MiuiHeadsetKeyConfigFragment", "K73 device ID ");
            return R.xml.headset_key_config;
        } else if (HeadsetIDConstants.isK75Headset(this.mDeviceId)) {
            Log.d("MiuiHeadsetKeyConfigFragment", "K75 device ID ");
            return R.xml.headset_key_config;
        } else {
            return R.xml.headset_key_config;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MiuiHeadsetActivity miuiHeadsetActivity = (MiuiHeadsetActivity) activity;
        this.mDevice = miuiHeadsetActivity.getDevice();
        this.mHeadSetAct = miuiHeadsetActivity;
        this.mSupport = miuiHeadsetActivity.getSupport();
        this.mDeviceId = this.mHeadSetAct.getDeviceID();
        this.PRESS_KEY_INIT = this.mHeadSetAct.getDeviceConfig();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        if (getArguments() != null) {
            String string = getArguments().getString("Headset_Key_Init");
            if (string != null && !"".equals(string)) {
                this.PRESS_KEY_INIT = string;
            }
            String string2 = getArguments().getString("Headset_DeviceId");
            if ("".equals(this.mDeviceId) && string2 != null && !"".equals(string2)) {
                this.mDeviceId = string2;
            }
            Log.d("MiuiHeadsetKeyConfigFragment", "getArguments(), init key:" + this.PRESS_KEY_INIT + ", " + this.mDeviceId);
        }
        super.onCreate(bundle);
        getPreferenceScreen().setOrderingAsAdded(false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.headset_key_config_layout, viewGroup, false);
        this.mRootView = inflate;
        ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
        this.mService = this.mHeadSetAct.getService();
        ActionBar appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setTitle(R.string.miheadset_key_config_gesture_control);
        }
        if (getArguments() != null) {
            this.mDeviceConnected = getArguments().getBoolean("device_connected", true);
        }
        initKeyConfig();
        initResource();
        return this.mRootView;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public void onHiddenChanged(boolean z) {
        MiuiHeadsetActivity miuiHeadsetActivity;
        super.onHiddenChanged(z);
        if (z || (miuiHeadsetActivity = this.mHeadSetAct) == null) {
            return;
        }
        this.PRESS_KEY_INIT = miuiHeadsetActivity.getDeviceConfig();
        initKeyConfig();
        this.pref_left.setValue(this.mLeftKey ? R.string.miheadset_key_config_noise_control : R.string.miheadset_key_config_call_ai);
        this.pref_right.setValue(this.mRightKey ? R.string.miheadset_key_config_noise_control : R.string.miheadset_key_config_call_ai);
        this.mDoubleClickLeft.setValueIndex(this.mLeftDoubleKey);
        this.mTripleClickLeft.setValueIndex(this.mLeftTripleKey);
        this.mDoubleClickRight.setValueIndex(this.mRightDoubleKey);
        this.mTripleClickRight.setValueIndex(this.mRightTripleKey);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        key.hashCode();
        if (key.equals("long_press_right_headset")) {
            gotoPressKeyFragment("right");
            return false;
        } else if (key.equals("long_press_left_headset")) {
            gotoPressKeyFragment("left");
            return false;
        } else {
            return false;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
    }

    public void onServiceConnected() {
        try {
            if (this.mHeadSetAct == null) {
                this.mHeadSetAct = (MiuiHeadsetActivity) getActivity();
            }
            this.mService = this.mHeadSetAct.getService();
            this.mDevice = this.mHeadSetAct.getDevice();
            initKeyConfig();
            initResource();
        } catch (Exception e) {
            Log.e("MiuiHeadsetKeyConfigFragment", "activity define service error " + e);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(this.mBluetoothA2dpReceiver, new IntentFilter("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED"));
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(this.mBluetoothA2dpReceiver);
    }
}
