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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.bluetooth.ble.app.IMiuiHeadsetService;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.R;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;
import miuix.preference.RadioButtonPreference;

/* loaded from: classes.dex */
public final class MiuiHeadsetPressKeyFragment extends MiuiSettingsPreferenceFragment {
    private RadioButtonPreference mCallAI;
    private LinearLayout mCheckList;
    private CheckBox mClose;
    private BluetoothDevice mDevice;
    private MiuiHeadsetActivity mHeadSetAct;
    private RadioButtonPreference mNoiseControl;
    private CheckBox mOpenAnc;
    private View mRootView;
    private CheckBox mTransparent;
    private PreferenceGroup pressKeyGroup;
    private IMiuiHeadsetService mService = null;
    public int mLeftDoubleKey = 0;
    public int mLeftTripleKey = 0;
    public int mRightDoubleKey = 0;
    public int mRightTripleKey = 0;
    public boolean mLeftKey = false;
    public boolean mLeftOpenAnc = false;
    public boolean mLeftTransparent = false;
    public boolean mLeftClose = false;
    public boolean mRightKey = false;
    public boolean mRightOpenAnc = false;
    public boolean mRightTransparent = false;
    public boolean mRightClose = false;
    private String mSupport = "";
    private String mDeviceId = "";
    public String PRESS_KEY_INIT = "000011101110";
    private String mTitle = "left";
    private String mLL = "FF";
    private String mRR = "FF";
    private boolean callAiIsremove = false;
    private final BroadcastReceiver mBluetoothA2dpReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.MiuiHeadsetPressKeyFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("MiuiHeadsetPressKeyFragment", "LDAC: mBluetoothA2dpReceiver.onReceive intent=" + intent);
            if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Log.d("MiuiHeadsetPressKeyFragment", "state changed " + intExtra + ", " + bluetoothDevice);
                if (intExtra == 0 && bluetoothDevice != null && bluetoothDevice.equals(MiuiHeadsetPressKeyFragment.this.mDevice)) {
                    MiuiHeadsetPressKeyFragment.this.setPreferenceEnable(false);
                } else if (intExtra == 2 && bluetoothDevice != null && bluetoothDevice.equals(MiuiHeadsetPressKeyFragment.this.mDevice)) {
                    MiuiHeadsetPressKeyFragment.this.setPreferenceEnable(true);
                }
            }
        }
    };
    private final Preference.OnPreferenceChangeListener mPrefChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetPressKeyFragment.2
        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            try {
                if (MiuiHeadsetPressKeyFragment.this.mService == null) {
                    Log.e("MiuiHeadsetPressKeyFragment", "preference changed service is null");
                    return false;
                } else if (!((Boolean) obj).booleanValue()) {
                    Log.d("MiuiHeadsetPressKeyFragment", "RadioButtonPreference cann't change to false.");
                    return false;
                } else {
                    boolean z = true;
                    if (MiuiHeadsetPressKeyFragment.this.mCallAI != null && MiuiHeadsetPressKeyFragment.this.mNoiseControl != null) {
                        String key = preference.getKey();
                        char c = 65535;
                        int hashCode = key.hashCode();
                        if (hashCode != -1113394437) {
                            if (hashCode == 980279719 && key.equals("config_call_mi_ai")) {
                                c = 0;
                            }
                        } else if (key.equals("config_noise_control")) {
                            c = 1;
                        }
                        if (c != 0) {
                            if (c == 1) {
                                if (MiuiHeadsetPressKeyFragment.this.mCallAI != null && MiuiHeadsetPressKeyFragment.this.mCallAI.isChecked()) {
                                    MiuiHeadsetPressKeyFragment.this.mCallAI.setChecked(false);
                                }
                                if (MiuiHeadsetPressKeyFragment.this.mNoiseControl != null) {
                                    MiuiHeadsetPressKeyFragment.this.mNoiseControl.setChecked(true);
                                    if (MiuiHeadsetPressKeyFragment.this.mTitle.equals("left")) {
                                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment = MiuiHeadsetPressKeyFragment.this;
                                        miuiHeadsetPressKeyFragment.mLeftKey = true;
                                        miuiHeadsetPressKeyFragment.mLL = "06";
                                    } else if (MiuiHeadsetPressKeyFragment.this.mTitle.equals("right")) {
                                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment2 = MiuiHeadsetPressKeyFragment.this;
                                        miuiHeadsetPressKeyFragment2.mRightKey = true;
                                        miuiHeadsetPressKeyFragment2.mRR = "06";
                                    }
                                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment3 = MiuiHeadsetPressKeyFragment.this;
                                    miuiHeadsetPressKeyFragment3.showNoiseControlList(miuiHeadsetPressKeyFragment3.mTitle);
                                }
                            }
                        } else if (MiuiHeadsetPressKeyFragment.this.mNoiseControl.isChecked()) {
                            if (MiuiHeadsetPressKeyFragment.this.mCallAI != null) {
                                MiuiHeadsetPressKeyFragment.this.mCallAI.setChecked(true);
                            }
                            MiuiHeadsetPressKeyFragment.this.mNoiseControl.setChecked(false);
                            if (MiuiHeadsetPressKeyFragment.this.mTitle.equals("left")) {
                                MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment4 = MiuiHeadsetPressKeyFragment.this;
                                miuiHeadsetPressKeyFragment4.mLeftKey = false;
                                miuiHeadsetPressKeyFragment4.mLL = "00";
                            } else if (MiuiHeadsetPressKeyFragment.this.mTitle.equals("right")) {
                                MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment5 = MiuiHeadsetPressKeyFragment.this;
                                miuiHeadsetPressKeyFragment5.mRightKey = false;
                                miuiHeadsetPressKeyFragment5.mRR = "00";
                            }
                            MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment6 = MiuiHeadsetPressKeyFragment.this;
                            miuiHeadsetPressKeyFragment6.removeNoiseControlList(miuiHeadsetPressKeyFragment6.mTitle);
                        }
                        MiuiHeadsetPressKeyFragment.this.updateKeyPressConfig();
                        return true;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("RadioButtonPreference is null:");
                    sb.append(MiuiHeadsetPressKeyFragment.this.mCallAI == null);
                    if (MiuiHeadsetPressKeyFragment.this.mNoiseControl != null) {
                        z = false;
                    }
                    sb.append(z);
                    Log.d("MiuiHeadsetPressKeyFragment", sb.toString());
                    return false;
                }
            } catch (Exception e) {
                Log.e("MiuiHeadsetPressKeyFragment", "preference listener error " + e);
                return false;
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public String booleanToString(boolean z) {
        return z ? "1" : "0";
    }

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
                saveFragmentInitInfo(this.mTitle, commonCommand);
                Log.d("MiuiHeadsetPressKeyFragment", "get radio button is: " + commonCommand);
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
            Log.d("MiuiHeadsetPressKeyFragment", sb.toString());
            return this.PRESS_KEY_INIT;
        } catch (Exception unused) {
            return this.PRESS_KEY_INIT;
        }
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
                Log.w("MiuiHeadsetPressKeyFragment", "hexToByteArray failed: " + e);
            }
        }
        return bArr;
    }

    private void initRadioButton(String str) {
        RadioButtonPreference radioButtonPreference;
        this.pressKeyGroup = (PreferenceGroup) findPreference("press_key_group");
        this.mCallAI = (RadioButtonPreference) findPreference("config_call_mi_ai");
        this.mNoiseControl = (RadioButtonPreference) findPreference("config_noise_control");
        if ("0201010001".equals(this.mDeviceId)) {
            Log.d("MiuiHeadsetPressKeyFragment", "mDeviceId equals TWSID_GL");
            PreferenceGroup preferenceGroup = this.pressKeyGroup;
            if (preferenceGroup != null && (radioButtonPreference = this.mCallAI) != null) {
                preferenceGroup.removePreference(radioButtonPreference);
                this.callAiIsremove = true;
                this.mCallAI = null;
            }
        }
        RadioButtonPreference radioButtonPreference2 = this.mCallAI;
        if (radioButtonPreference2 != null) {
            radioButtonPreference2.setOnPreferenceChangeListener(this.mPrefChangeListener);
        }
        RadioButtonPreference radioButtonPreference3 = this.mNoiseControl;
        if (radioButtonPreference3 != null) {
            radioButtonPreference3.setOnPreferenceChangeListener(this.mPrefChangeListener);
        }
        try {
            if (this.mService == null) {
                this.mService = this.mHeadSetAct.getService();
            }
            String radioButtonConfig = getRadioButtonConfig();
            Log.d("MiuiHeadsetPressKeyFragment", "radio button init to: " + radioButtonConfig);
            refeshFragment(this.mTitle, radioButtonConfig);
        } catch (Exception e) {
            Log.w("MiuiHeadsetPressKeyFragment", "get radio press key newConfig failed: " + e);
        }
    }

    private void initTitle() {
        ActionBar appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        if (this.mTitle.equals("left") && appCompatActionBar != null) {
            appCompatActionBar.setTitle(R.string.miheadset_key_config_press_left);
        } else if (!this.mTitle.equals("right") || appCompatActionBar == null) {
        } else {
            appCompatActionBar.setTitle(R.string.miheadset_key_config_press_right);
        }
    }

    private void refeshFragment(String str, String str2) {
        RadioButtonPreference radioButtonPreference;
        RadioButtonPreference radioButtonPreference2;
        RadioButtonPreference radioButtonPreference3;
        try {
            Log.d("MiuiHeadsetPressKeyFragment", "refeshFragment: " + str2);
            byte[] hexToByteArray = hexToByteArray(str2);
            this.mLeftDoubleKey = hexToByteArray[0];
            this.mLeftTripleKey = hexToByteArray[1];
            this.mRightDoubleKey = hexToByteArray[2];
            this.mRightTripleKey = hexToByteArray[3];
            this.mLeftKey = hexToByteArray[4] != 0;
            this.mLeftTransparent = hexToByteArray[5] != 0;
            this.mLeftOpenAnc = hexToByteArray[6] != 0;
            this.mLeftClose = hexToByteArray[7] != 0;
            this.mRightKey = hexToByteArray[8] != 0;
            this.mRightTransparent = hexToByteArray[9] != 0;
            this.mRightOpenAnc = hexToByteArray[10] != 0;
            this.mRightClose = hexToByteArray[11] != 0;
            ((MiuiHeadsetActivity) getActivity()).mDeviceConfig = str2;
            RadioButtonPreference radioButtonPreference4 = this.mCallAI;
            if (radioButtonPreference4 != null) {
                radioButtonPreference4.setChecked(false);
            }
            RadioButtonPreference radioButtonPreference5 = this.mNoiseControl;
            if (radioButtonPreference5 != null) {
                radioButtonPreference5.setChecked(false);
            }
            if (str.equals("left") && (((radioButtonPreference2 = this.mCallAI) != null || this.callAiIsremove) && (radioButtonPreference3 = this.mNoiseControl) != null)) {
                if (this.mLeftKey) {
                    radioButtonPreference3.setChecked(true);
                    showNoiseControlList(str);
                } else if (radioButtonPreference2 != null) {
                    radioButtonPreference2.setChecked(true);
                }
            } else if (str.equals("right")) {
                RadioButtonPreference radioButtonPreference6 = this.mCallAI;
                if ((radioButtonPreference6 != null || this.callAiIsremove) && (radioButtonPreference = this.mNoiseControl) != null) {
                    if (this.mRightKey) {
                        radioButtonPreference.setChecked(true);
                        showNoiseControlList(str);
                    } else if (radioButtonPreference6 != null) {
                        radioButtonPreference6.setChecked(true);
                    }
                }
            }
        } catch (Exception e) {
            Log.w("MiuiHeadsetPressKeyFragment", "get radio press key newConfig failed: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeNoiseControlList(String str) {
        this.mCheckList.setVisibility(8);
    }

    private void saveFragmentInitInfo(String str, String str2) {
        this.PRESS_KEY_INIT = str2;
        this.mHeadSetAct.setDeviceConfig(str2);
        Bundle bundle = new Bundle();
        bundle.putString("Headset_Side", str);
        bundle.putString("Headset_Key_Init", str2);
        bundle.putString("Headset_DeviceId", this.mDeviceId);
        setArguments(bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPreferenceEnable(boolean z) {
        Log.d("MiuiHeadsetPressKeyFragment", "setPreferenceEnable " + z);
        PreferenceGroup preferenceGroup = this.pressKeyGroup;
        if (preferenceGroup != null) {
            preferenceGroup.setEnabled(z);
        }
        LinearLayout linearLayout = this.mCheckList;
        if (linearLayout != null) {
            linearLayout.setEnabled(z);
        }
        CheckBox checkBox = this.mOpenAnc;
        if (checkBox != null) {
            checkBox.setEnabled(z);
        }
        CheckBox checkBox2 = this.mTransparent;
        if (checkBox2 != null) {
            checkBox2.setEnabled(z);
        }
        CheckBox checkBox3 = this.mClose;
        if (checkBox3 != null) {
            checkBox3.setEnabled(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showNoiseControlList(String str) {
        this.mCheckList.setVisibility(0);
        this.mOpenAnc = (CheckBox) this.mRootView.findViewById(R.id.miheadset_key_openAnc_checkbox);
        this.mTransparent = (CheckBox) this.mRootView.findViewById(R.id.miheadset_key_transparent_checkbox);
        CheckBox checkBox = (CheckBox) this.mRootView.findViewById(R.id.miheadset_key_close_checkbox);
        this.mClose = checkBox;
        if (this.mOpenAnc == null || this.mTransparent == null || checkBox == null) {
            Log.e("MiuiHeadsetPressKeyFragment", "Checkbox init failed!");
            return;
        }
        if (str.equals("left")) {
            this.mOpenAnc.setChecked(this.mLeftOpenAnc);
            this.mTransparent.setChecked(this.mLeftTransparent);
            this.mClose.setChecked(this.mLeftClose);
        } else if (str.equals("right")) {
            this.mOpenAnc.setChecked(this.mRightOpenAnc);
            this.mTransparent.setChecked(this.mRightTransparent);
            this.mClose.setChecked(this.mRightClose);
        }
        this.mOpenAnc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetPressKeyFragment.3
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                try {
                    if (MiuiHeadsetPressKeyFragment.this.mTitle.equals("left")) {
                        if (MiuiHeadsetPressKeyFragment.this.mTransparent.isChecked() && MiuiHeadsetPressKeyFragment.this.mClose.isChecked()) {
                            MiuiHeadsetPressKeyFragment.this.mLeftOpenAnc = z;
                        } else if (!z) {
                            MiuiHeadsetPressKeyFragment.this.mOpenAnc.setChecked(true);
                            return;
                        } else {
                            MiuiHeadsetPressKeyFragment.this.mLeftOpenAnc = true;
                        }
                        StringBuilder sb = new StringBuilder();
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment = MiuiHeadsetPressKeyFragment.this;
                        sb.append(miuiHeadsetPressKeyFragment.booleanToString(miuiHeadsetPressKeyFragment.mLeftTransparent));
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment2 = MiuiHeadsetPressKeyFragment.this;
                        sb.append(miuiHeadsetPressKeyFragment2.booleanToString(miuiHeadsetPressKeyFragment2.mLeftOpenAnc));
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment3 = MiuiHeadsetPressKeyFragment.this;
                        sb.append(miuiHeadsetPressKeyFragment3.booleanToString(miuiHeadsetPressKeyFragment3.mLeftClose));
                        String str2 = "0" + Integer.toHexString(Integer.parseInt(sb.toString(), 2));
                        if (!HeadsetIDConstants.isTWS01Headset(MiuiHeadsetPressKeyFragment.this.mDeviceId)) {
                            MiuiHeadsetPressKeyFragment.this.updateANCConfig(str2, "FF");
                            return;
                        }
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment4 = MiuiHeadsetPressKeyFragment.this;
                        miuiHeadsetPressKeyFragment4.mRightOpenAnc = miuiHeadsetPressKeyFragment4.mLeftOpenAnc;
                        miuiHeadsetPressKeyFragment4.mRightTransparent = miuiHeadsetPressKeyFragment4.mLeftTransparent;
                        miuiHeadsetPressKeyFragment4.mRightClose = miuiHeadsetPressKeyFragment4.mLeftClose;
                        miuiHeadsetPressKeyFragment4.updateANCConfig(str2, str2);
                        return;
                    }
                    if (MiuiHeadsetPressKeyFragment.this.mTransparent.isChecked() && MiuiHeadsetPressKeyFragment.this.mClose.isChecked()) {
                        MiuiHeadsetPressKeyFragment.this.mRightOpenAnc = z;
                    } else if (!z) {
                        MiuiHeadsetPressKeyFragment.this.mOpenAnc.setChecked(true);
                        return;
                    } else {
                        MiuiHeadsetPressKeyFragment.this.mRightOpenAnc = true;
                    }
                    StringBuilder sb2 = new StringBuilder();
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment5 = MiuiHeadsetPressKeyFragment.this;
                    sb2.append(miuiHeadsetPressKeyFragment5.booleanToString(miuiHeadsetPressKeyFragment5.mRightTransparent));
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment6 = MiuiHeadsetPressKeyFragment.this;
                    sb2.append(miuiHeadsetPressKeyFragment6.booleanToString(miuiHeadsetPressKeyFragment6.mRightOpenAnc));
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment7 = MiuiHeadsetPressKeyFragment.this;
                    sb2.append(miuiHeadsetPressKeyFragment7.booleanToString(miuiHeadsetPressKeyFragment7.mRightClose));
                    String str3 = "0" + Integer.toHexString(Integer.parseInt(sb2.toString(), 2));
                    if (!HeadsetIDConstants.isTWS01Headset(MiuiHeadsetPressKeyFragment.this.mDeviceId)) {
                        MiuiHeadsetPressKeyFragment.this.updateANCConfig("FF", str3);
                        return;
                    }
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment8 = MiuiHeadsetPressKeyFragment.this;
                    miuiHeadsetPressKeyFragment8.mLeftOpenAnc = miuiHeadsetPressKeyFragment8.mRightOpenAnc;
                    miuiHeadsetPressKeyFragment8.mLeftTransparent = miuiHeadsetPressKeyFragment8.mRightTransparent;
                    miuiHeadsetPressKeyFragment8.mLeftClose = miuiHeadsetPressKeyFragment8.mRightClose;
                    miuiHeadsetPressKeyFragment8.updateANCConfig(str3, str3);
                } catch (Exception e) {
                    Log.e("MiuiHeadsetPressKeyFragment", "OpenAnc onCheckedChanged error: " + e);
                }
            }
        });
        this.mTransparent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetPressKeyFragment.4
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                try {
                    if (MiuiHeadsetPressKeyFragment.this.mTitle.equals("left")) {
                        if (MiuiHeadsetPressKeyFragment.this.mOpenAnc.isChecked() && MiuiHeadsetPressKeyFragment.this.mClose.isChecked()) {
                            MiuiHeadsetPressKeyFragment.this.mLeftTransparent = z;
                        } else if (!z) {
                            MiuiHeadsetPressKeyFragment.this.mTransparent.setChecked(true);
                            return;
                        } else {
                            MiuiHeadsetPressKeyFragment.this.mLeftTransparent = true;
                        }
                        StringBuilder sb = new StringBuilder();
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment = MiuiHeadsetPressKeyFragment.this;
                        sb.append(miuiHeadsetPressKeyFragment.booleanToString(miuiHeadsetPressKeyFragment.mLeftTransparent));
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment2 = MiuiHeadsetPressKeyFragment.this;
                        sb.append(miuiHeadsetPressKeyFragment2.booleanToString(miuiHeadsetPressKeyFragment2.mLeftOpenAnc));
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment3 = MiuiHeadsetPressKeyFragment.this;
                        sb.append(miuiHeadsetPressKeyFragment3.booleanToString(miuiHeadsetPressKeyFragment3.mLeftClose));
                        String str2 = "0" + Integer.toHexString(Integer.parseInt(sb.toString(), 2));
                        if (!HeadsetIDConstants.isTWS01Headset(MiuiHeadsetPressKeyFragment.this.mDeviceId)) {
                            MiuiHeadsetPressKeyFragment.this.updateANCConfig(str2, "FF");
                            return;
                        }
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment4 = MiuiHeadsetPressKeyFragment.this;
                        miuiHeadsetPressKeyFragment4.mRightOpenAnc = miuiHeadsetPressKeyFragment4.mLeftOpenAnc;
                        miuiHeadsetPressKeyFragment4.mRightTransparent = miuiHeadsetPressKeyFragment4.mLeftTransparent;
                        miuiHeadsetPressKeyFragment4.mRightClose = miuiHeadsetPressKeyFragment4.mLeftClose;
                        miuiHeadsetPressKeyFragment4.updateANCConfig(str2, str2);
                        return;
                    }
                    if (MiuiHeadsetPressKeyFragment.this.mOpenAnc.isChecked() && MiuiHeadsetPressKeyFragment.this.mClose.isChecked()) {
                        MiuiHeadsetPressKeyFragment.this.mRightTransparent = z;
                    } else if (!z) {
                        MiuiHeadsetPressKeyFragment.this.mTransparent.setChecked(true);
                        return;
                    } else {
                        MiuiHeadsetPressKeyFragment.this.mRightTransparent = true;
                    }
                    StringBuilder sb2 = new StringBuilder();
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment5 = MiuiHeadsetPressKeyFragment.this;
                    sb2.append(miuiHeadsetPressKeyFragment5.booleanToString(miuiHeadsetPressKeyFragment5.mRightTransparent));
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment6 = MiuiHeadsetPressKeyFragment.this;
                    sb2.append(miuiHeadsetPressKeyFragment6.booleanToString(miuiHeadsetPressKeyFragment6.mRightOpenAnc));
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment7 = MiuiHeadsetPressKeyFragment.this;
                    sb2.append(miuiHeadsetPressKeyFragment7.booleanToString(miuiHeadsetPressKeyFragment7.mRightClose));
                    String str3 = "0" + Integer.toHexString(Integer.parseInt(sb2.toString(), 2));
                    if (!HeadsetIDConstants.isTWS01Headset(MiuiHeadsetPressKeyFragment.this.mDeviceId)) {
                        MiuiHeadsetPressKeyFragment.this.updateANCConfig("FF", str3);
                        return;
                    }
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment8 = MiuiHeadsetPressKeyFragment.this;
                    miuiHeadsetPressKeyFragment8.mLeftOpenAnc = miuiHeadsetPressKeyFragment8.mRightOpenAnc;
                    miuiHeadsetPressKeyFragment8.mLeftTransparent = miuiHeadsetPressKeyFragment8.mRightTransparent;
                    miuiHeadsetPressKeyFragment8.mLeftClose = miuiHeadsetPressKeyFragment8.mRightClose;
                    miuiHeadsetPressKeyFragment8.updateANCConfig(str3, str3);
                } catch (Exception e) {
                    Log.e("MiuiHeadsetPressKeyFragment", "Transparent onCheckedChanged error: " + e);
                }
            }
        });
        this.mClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetPressKeyFragment.5
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                try {
                    if (MiuiHeadsetPressKeyFragment.this.mTitle.equals("left")) {
                        if (MiuiHeadsetPressKeyFragment.this.mOpenAnc.isChecked() && MiuiHeadsetPressKeyFragment.this.mTransparent.isChecked()) {
                            MiuiHeadsetPressKeyFragment.this.mLeftClose = z;
                        } else if (!z) {
                            MiuiHeadsetPressKeyFragment.this.mClose.setChecked(true);
                            return;
                        } else {
                            MiuiHeadsetPressKeyFragment.this.mLeftClose = true;
                        }
                        StringBuilder sb = new StringBuilder();
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment = MiuiHeadsetPressKeyFragment.this;
                        sb.append(miuiHeadsetPressKeyFragment.booleanToString(miuiHeadsetPressKeyFragment.mLeftTransparent));
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment2 = MiuiHeadsetPressKeyFragment.this;
                        sb.append(miuiHeadsetPressKeyFragment2.booleanToString(miuiHeadsetPressKeyFragment2.mLeftOpenAnc));
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment3 = MiuiHeadsetPressKeyFragment.this;
                        sb.append(miuiHeadsetPressKeyFragment3.booleanToString(miuiHeadsetPressKeyFragment3.mLeftClose));
                        String str2 = "0" + Integer.toHexString(Integer.parseInt(sb.toString(), 2));
                        if (!HeadsetIDConstants.isTWS01Headset(MiuiHeadsetPressKeyFragment.this.mDeviceId)) {
                            MiuiHeadsetPressKeyFragment.this.updateANCConfig(str2, "FF");
                            return;
                        }
                        MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment4 = MiuiHeadsetPressKeyFragment.this;
                        miuiHeadsetPressKeyFragment4.mRightOpenAnc = miuiHeadsetPressKeyFragment4.mLeftOpenAnc;
                        miuiHeadsetPressKeyFragment4.mRightTransparent = miuiHeadsetPressKeyFragment4.mLeftTransparent;
                        miuiHeadsetPressKeyFragment4.mRightClose = miuiHeadsetPressKeyFragment4.mLeftClose;
                        miuiHeadsetPressKeyFragment4.updateANCConfig(str2, str2);
                        return;
                    }
                    if (MiuiHeadsetPressKeyFragment.this.mOpenAnc.isChecked() && MiuiHeadsetPressKeyFragment.this.mTransparent.isChecked()) {
                        MiuiHeadsetPressKeyFragment.this.mRightClose = z;
                    } else if (!z) {
                        MiuiHeadsetPressKeyFragment.this.mClose.setChecked(true);
                        return;
                    } else {
                        MiuiHeadsetPressKeyFragment.this.mRightClose = true;
                    }
                    StringBuilder sb2 = new StringBuilder();
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment5 = MiuiHeadsetPressKeyFragment.this;
                    sb2.append(miuiHeadsetPressKeyFragment5.booleanToString(miuiHeadsetPressKeyFragment5.mRightTransparent));
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment6 = MiuiHeadsetPressKeyFragment.this;
                    sb2.append(miuiHeadsetPressKeyFragment6.booleanToString(miuiHeadsetPressKeyFragment6.mRightOpenAnc));
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment7 = MiuiHeadsetPressKeyFragment.this;
                    sb2.append(miuiHeadsetPressKeyFragment7.booleanToString(miuiHeadsetPressKeyFragment7.mRightClose));
                    String str3 = "0" + Integer.toHexString(Integer.parseInt(sb2.toString(), 2));
                    if (!HeadsetIDConstants.isTWS01Headset(MiuiHeadsetPressKeyFragment.this.mDeviceId)) {
                        MiuiHeadsetPressKeyFragment.this.updateANCConfig("FF", str3);
                        return;
                    }
                    MiuiHeadsetPressKeyFragment miuiHeadsetPressKeyFragment8 = MiuiHeadsetPressKeyFragment.this;
                    miuiHeadsetPressKeyFragment8.mLeftOpenAnc = miuiHeadsetPressKeyFragment8.mRightOpenAnc;
                    miuiHeadsetPressKeyFragment8.mLeftTransparent = miuiHeadsetPressKeyFragment8.mRightTransparent;
                    miuiHeadsetPressKeyFragment8.mLeftClose = miuiHeadsetPressKeyFragment8.mRightClose;
                    miuiHeadsetPressKeyFragment8.updateANCConfig(str3, str3);
                } catch (Exception e) {
                    Log.e("MiuiHeadsetPressKeyFragment", "Close onCheckedChanged error: " + e);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateANCConfig(String str, String str2) {
        try {
            boolean z = this.mLeftOpenAnc;
            if (z == this.mLeftTransparent) {
                if (!z) {
                    return;
                }
            } else if (!this.mLeftClose) {
                return;
            }
            boolean z2 = this.mRightOpenAnc;
            if (z2 == this.mRightTransparent) {
                if (!z2) {
                    return;
                }
            } else if (!this.mRightClose) {
                return;
            }
            IMiuiHeadsetService iMiuiHeadsetService = this.mService;
            if (iMiuiHeadsetService != null) {
                boolean z3 = true;
                if (iMiuiHeadsetService != null && this.mDevice != null) {
                    if (!updateDeviceConfig()) {
                        Log.w("MiuiHeadsetPressKeyFragment", "updateDeviceConfig failed!");
                        return;
                    }
                    String str3 = str + str2;
                    Log.d("MiuiHeadsetPressKeyFragment", "update ANC config+ " + str3);
                    this.mService.setFunKey(1, Integer.parseInt(str3, 16), this.mDevice);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Send ANC config failed: Service: ");
                sb.append(this.mService != null);
                sb.append(",Device: ");
                if (this.mDevice == null) {
                    z3 = false;
                }
                sb.append(z3);
                Log.w("MiuiHeadsetPressKeyFragment", sb.toString());
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetPressKeyFragment", "update ANC config failed: " + e);
        }
    }

    private boolean updateDeviceConfig() {
        try {
            String str = String.valueOf(this.mLeftDoubleKey) + String.valueOf(this.mLeftTripleKey) + String.valueOf(this.mRightDoubleKey) + String.valueOf(this.mRightTripleKey) + booleanToString(this.mLeftKey) + booleanToString(this.mLeftTransparent) + booleanToString(this.mLeftOpenAnc) + booleanToString(this.mLeftClose) + booleanToString(this.mRightKey) + booleanToString(this.mRightTransparent) + booleanToString(this.mRightOpenAnc) + booleanToString(this.mRightClose);
            Log.d("MiuiHeadsetPressKeyFragment", "update Device newConfig+ " + str);
            IMiuiHeadsetService iMiuiHeadsetService = this.mService;
            if (iMiuiHeadsetService == null && this.mDevice == null) {
                return false;
            }
            iMiuiHeadsetService.setCommonCommand(105, str, this.mDevice);
            saveFragmentInitInfo(this.mTitle, str);
            return true;
        } catch (Exception e) {
            Log.e("MiuiHeadsetPressKeyFragment", "update device key config failed: " + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateKeyPressConfig() {
        try {
            boolean z = this.mLeftOpenAnc;
            if (z == this.mLeftTransparent) {
                if (!z) {
                    return;
                }
            } else if (!this.mLeftClose) {
                return;
            }
            boolean z2 = this.mRightOpenAnc;
            if (z2 == this.mRightTransparent) {
                if (!z2) {
                    return;
                }
            } else if (!this.mRightClose) {
                return;
            }
            IMiuiHeadsetService iMiuiHeadsetService = this.mService;
            if (iMiuiHeadsetService != null) {
                if (iMiuiHeadsetService != null && this.mDevice != null) {
                    if (!updateDeviceConfig()) {
                        Log.w("MiuiHeadsetPressKeyFragment", "updateDeviceConfig failed!");
                        return;
                    }
                    String str = "03" + this.mLL + this.mRR;
                    if (this.mLL.equals("FF") && this.mRR.equals("FF")) {
                        Log.d("MiuiHeadsetPressKeyFragment", "no press key config to update ");
                        return;
                    }
                    Log.d("MiuiHeadsetPressKeyFragment", "updateKeyPressConfig: update key config+ " + str);
                    this.mRR = "FF";
                    this.mLL = "FF";
                    this.mService.setFunKey(0, Integer.parseInt(str, 16), this.mDevice);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Send press key config failed: Service: ");
                sb.append(this.mService != null);
                sb.append(",Device: ");
                sb.append(this.mDevice != null);
                Log.w("MiuiHeadsetPressKeyFragment", sb.toString());
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetPressKeyFragment", "update press key config failed: " + e);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiHeadsetPressKeyFragment.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.headset_key_press_config;
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MiuiHeadsetActivity miuiHeadsetActivity = (MiuiHeadsetActivity) activity;
        this.mDevice = miuiHeadsetActivity.getDevice();
        this.mHeadSetAct = miuiHeadsetActivity;
        this.mSupport = miuiHeadsetActivity.getSupport();
        this.mDeviceId = this.mHeadSetAct.getDeviceID();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            String string = getArguments().getString("Headset_Side");
            if (string != null && !"".equals(string)) {
                this.mTitle = string;
            }
            String string2 = getArguments().getString("Headset_Key_Init");
            if (string2 != null && !"".equals(string2)) {
                this.PRESS_KEY_INIT = string2;
            }
            String string3 = getArguments().getString("Headset_DeviceId");
            if ("".equals(this.mDeviceId) && string3 != null && !"".equals(string3)) {
                this.mDeviceId = string3;
            }
            Log.d("MiuiHeadsetPressKeyFragment", "getArguments(), mTitle: " + this.mTitle + ", init key:" + this.PRESS_KEY_INIT);
        }
        getPreferenceScreen().setOrderingAsAdded(false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.headset_key_press_config, viewGroup, false);
        this.mRootView = inflate;
        ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
        try {
            IMiuiHeadsetService service = this.mHeadSetAct.getService();
            this.mService = service;
            if (service == null) {
                Log.e("MiuiHeadsetPressKeyFragment", "Service is null");
            }
            LinearLayout linearLayout = (LinearLayout) this.mRootView.findViewById(R.id.miheadset_checklist);
            this.mCheckList = linearLayout;
            if (linearLayout == null) {
                Log.e("MiuiHeadsetPressKeyFragment", "CheckList is null");
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetPressKeyFragment", "miui headset activity service error " + e);
        }
        initTitle();
        initRadioButton(this.mTitle);
        return this.mRootView;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        ActionBar appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setTitle(R.string.miheadset_key_config_gesture_control);
        }
        super.onDestroy();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
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
            String radioButtonConfig = getRadioButtonConfig();
            Log.d("MiuiHeadsetPressKeyFragment", "onServiceConnected: radio button is: " + radioButtonConfig);
            refeshFragment(this.mTitle, radioButtonConfig);
        } catch (Exception e) {
            Log.e("MiuiHeadsetPressKeyFragment", "activity define service error " + e);
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

    public void setTitleKey(String str) {
        this.mTitle = str;
    }
}
