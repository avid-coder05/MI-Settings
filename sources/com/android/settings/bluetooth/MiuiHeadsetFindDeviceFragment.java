package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import com.android.bluetooth.ble.app.IMiuiHeadsetService;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.bluetooth.tws.MiuiHeadsetAnimation;
import com.android.settings.utils.EncryptionUtil;
import java.lang.reflect.Method;
import miui.provider.ExtraTelephony;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiHeadsetFindDeviceFragment extends MiuiSettingsPreferenceFragment {
    private MiuiHeadsetActivity mActivity;
    private TextView mAliasText;
    private ImageView mBothIcon;
    private TextView mBothStateText;
    private TextView mBothText;
    private ImageView mConnectStateIcon;
    private TextView mConnectStateText;
    private BluetoothDevice mDevice;
    private ImageView mDeviceImage;
    private View mFindDeviceView;
    private LinearLayout mHandleTips;
    private MiuiHeadsetActivity mHeadSetAct;
    private ImageView mLeftIcon;
    private TextView mLeftStateText;
    private TextView mLeftText;
    private ImageView mRightIcon;
    private TextView mRightStateText;
    private TextView mRightText;
    private IMiuiHeadsetService mService = null;
    private AlertDialog mDialog = null;
    private boolean mLeftPlaying = false;
    private boolean mBothPlaying = false;
    private boolean mRightPlaying = false;
    private boolean mLeftConnected = false;
    private boolean mRightConnected = false;
    private String mCallbackStatus = "";
    private String mDeviceId = "";
    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("MiuiHeadsetFindDeviceFragment", "BluetoothReceiver.onReceive intent=" + intent);
            if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (intExtra == 0 && bluetoothDevice != null && bluetoothDevice.equals(MiuiHeadsetFindDeviceFragment.this.mDevice) && MiuiHeadsetFindDeviceFragment.this.mFindDeviceView != null) {
                    MiuiHeadsetFindDeviceFragment.this.updateDisconnectedView();
                } else if (intExtra != 2 || bluetoothDevice == null || !bluetoothDevice.equals(MiuiHeadsetFindDeviceFragment.this.mDevice) || MiuiHeadsetFindDeviceFragment.this.mFindDeviceView == null) {
                } else {
                    MiuiHeadsetFindDeviceFragment.this.getBatteryInfo();
                    MiuiHeadsetFindDeviceFragment.this.updateConnectedView();
                }
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void askDeviceDialog(final int i) {
        Log.e("MiuiHeadsetFindDeviceFragment", "askDeviceDialog");
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        showDeviceDialog(activity, null, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.18
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                Log.e("MiuiHeadsetFindDeviceFragment", "askDeviceDialog onclick envent");
                int i3 = i;
                if (i3 == 0) {
                    if (MiuiHeadsetFindDeviceFragment.this.mBothPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdBoth(false);
                    }
                    if (MiuiHeadsetFindDeviceFragment.this.mRightPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdRight(false);
                    }
                    MiuiHeadsetFindDeviceFragment.this.sendCmdLeft(!r2.mLeftPlaying);
                } else if (i3 == 1) {
                    if (MiuiHeadsetFindDeviceFragment.this.mBothPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdBoth(false);
                    }
                    if (MiuiHeadsetFindDeviceFragment.this.mLeftPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdLeft(false);
                    }
                    MiuiHeadsetFindDeviceFragment.this.sendCmdRight(!r2.mRightPlaying);
                } else if (i3 != 2) {
                } else {
                    if (MiuiHeadsetFindDeviceFragment.this.mRightPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdRight(false);
                    }
                    if (MiuiHeadsetFindDeviceFragment.this.mLeftPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdLeft(false);
                    }
                    MiuiHeadsetFindDeviceFragment.this.sendCmdBoth(!r2.mBothPlaying);
                }
            }
        }, activity.getString(R.string.headset_find_voice_warning), Html.fromHtml(activity.getString(R.string.headset_find_play_warning)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getBatteryInfo() {
        IMiuiHeadsetService iMiuiHeadsetService;
        Log.d("MiuiHeadsetFindDeviceFragment", "getBatteryInfo ");
        try {
            BluetoothDevice bluetoothDevice = this.mDevice;
            if (bluetoothDevice != null && (iMiuiHeadsetService = this.mService) != null) {
                iMiuiHeadsetService.setCommonCommand(107, "", bluetoothDevice);
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetFindDeviceFragment", "get connected status failed " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getInEarStatus() {
        IMiuiHeadsetService iMiuiHeadsetService;
        Log.d("MiuiHeadsetFindDeviceFragment", "getInEarStatus ");
        BluetoothDevice bluetoothDevice = this.mDevice;
        if (bluetoothDevice != null && (iMiuiHeadsetService = this.mService) != null) {
            try {
                return iMiuiHeadsetService.setCommonCommand(102, "", bluetoothDevice);
            } catch (Exception e) {
                Log.e("MiuiHeadsetFindDeviceFragment", "isInEar SET_INEAR_STATUS_CMD failed: " + e);
            }
        }
        return "";
    }

    private void handleCallbackStatus(String str) {
        Log.d("MiuiHeadsetFindDeviceFragment", "handleCallbackStatus " + str);
        if (this.mCallbackStatus == str) {
            return;
        }
        this.mCallbackStatus = str;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 1477635:
                if (str.equals("0003")) {
                    c = 0;
                    break;
                }
                break;
            case 1478594:
                if (str.equals("0101")) {
                    c = 1;
                    break;
                }
                break;
            case 1478595:
                if (str.equals("0102")) {
                    c = 2;
                    break;
                }
                break;
            case 1478596:
                if (str.equals("0103")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                updateUILeft(false);
                updateUIRight(false);
                updateUIBoth(false);
                return;
            case 1:
                updateUILeft(true);
                updateUIRight(false);
                updateUIBoth(false);
                return;
            case 2:
                updateUILeft(false);
                updateUIRight(true);
                updateUIBoth(false);
                return;
            case 3:
                updateUILeft(false);
                updateUIRight(false);
                updateUIBoth(true);
                return;
            default:
                return;
        }
    }

    private void initView() {
        Log.d("MiuiHeadsetFindDeviceFragment", "init ");
        final Context applicationContext = this.mActivity.getApplicationContext();
        Handler handler = new Handler();
        ActionBar appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setTitle(R.string.headset_find_title);
        }
        TextView textView = (TextView) this.mFindDeviceView.findViewById(R.id.tv_device_name);
        this.mAliasText = textView;
        textView.setText(this.mDevice.getAlias());
        this.mConnectStateIcon = (ImageView) this.mFindDeviceView.findViewById(R.id.iv_connect_state);
        this.mConnectStateText = (TextView) this.mFindDeviceView.findViewById(R.id.tv_connect_state);
        this.mDeviceImage = (ImageView) this.mFindDeviceView.findViewById(R.id.iv_device_icon);
        this.mLeftIcon = (ImageView) this.mFindDeviceView.findViewById(R.id.iv_state_icon_left);
        this.mBothIcon = (ImageView) this.mFindDeviceView.findViewById(R.id.iv_state_icon_both);
        this.mRightIcon = (ImageView) this.mFindDeviceView.findViewById(R.id.iv_state_icon_right);
        this.mLeftText = (TextView) this.mFindDeviceView.findViewById(R.id.tv_state_title_left);
        this.mBothText = (TextView) this.mFindDeviceView.findViewById(R.id.tv_state_title_both);
        this.mRightText = (TextView) this.mFindDeviceView.findViewById(R.id.tv_state_title_right);
        this.mLeftStateText = (TextView) this.mFindDeviceView.findViewById(R.id.tv_state_description_left);
        this.mBothStateText = (TextView) this.mFindDeviceView.findViewById(R.id.tv_state_description_both);
        this.mRightStateText = (TextView) this.mFindDeviceView.findViewById(R.id.tv_state_description_right);
        this.mHandleTips = (LinearLayout) this.mFindDeviceView.findViewById(R.id.handle_tip);
        if (HeadsetIDConstants.isTWS01Headset(this.mDeviceId)) {
            Log.d("MiuiHeadsetFindDeviceFragment", "K76 fitness");
            ImageView imageView = this.mDeviceImage;
            if (imageView != null) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                Log.d("MiuiHeadsetFindDeviceFragment", layoutParams.height + " " + layoutParams.width + " " + layoutParams.weight);
                layoutParams.height = (int) (((double) layoutParams.height) * 1.16d);
                this.mDeviceImage.setLayoutParams(layoutParams);
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mDeviceImage.getLayoutParams();
                Log.d("MiuiHeadsetFindDeviceFragment", layoutParams2.height + " " + layoutParams2.width + " " + layoutParams2.weight);
                if (HeadsetIDConstants.isTWS01GrayHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_TWS01_Gray);
                } else if (HeadsetIDConstants.isTWS01BlackHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_TWS01_Black);
                } else if (HeadsetIDConstants.isTWS01YellowHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_TWS01_Yellow);
                } else if (HeadsetIDConstants.isK76sHeadset(this.mDeviceId)) {
                    layoutParams2.height = (int) (layoutParams2.height * 1.2d);
                    this.mDeviceImage.setLayoutParams(layoutParams2);
                    handler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.2
                        @Override // java.lang.Runnable
                        public void run() {
                            Bitmap decodeImageResource = HeadsetIDConstants.decodeImageResource(applicationContext, R.drawable.headset_find_device_TWS01S);
                            if (decodeImageResource == null) {
                                Log.d("MiuiHeadsetFindDeviceFragment", "bitmap null");
                                return;
                            }
                            MiuiHeadsetFindDeviceFragment.this.mDeviceImage.setImageDrawable(new BitmapDrawable(applicationContext.getResources(), decodeImageResource));
                        }
                    }, 1L);
                    this.mDeviceImage.setImageResource(R.drawable.headset_find_device_TWS01S);
                }
            }
        } else if (HeadsetIDConstants.isK73Headset(this.mDeviceId)) {
            Log.d("MiuiHeadsetFindDeviceFragment", "K73 headset find device");
            ImageView imageView2 = this.mDeviceImage;
            if (imageView2 != null) {
                LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) imageView2.getLayoutParams();
                Log.d("MiuiHeadsetFindDeviceFragment", layoutParams3.height + " " + layoutParams3.width + " " + layoutParams3.weight);
                layoutParams3.height = (int) (((double) layoutParams3.height) * 1.16d);
                this.mDeviceImage.setLayoutParams(layoutParams3);
                LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) this.mDeviceImage.getLayoutParams();
                Log.d("MiuiHeadsetFindDeviceFragment", layoutParams4.height + " " + layoutParams4.width + " " + layoutParams4.weight);
                if (HeadsetIDConstants.isK73WhiteHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_find_device_k73_white);
                } else if (HeadsetIDConstants.isK73BlackHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_find_device_k73_black);
                } else if (HeadsetIDConstants.isK73GreenHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_find_device_k73_green);
                } else if (HeadsetIDConstants.isK73LBlueHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_find_device_k73_lblue);
                } else if (HeadsetIDConstants.isK73AWhiteHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_default_k73a_white_enc);
                    this.mDeviceImage.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.3
                        @Override // java.lang.Runnable
                        public void run() {
                            Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey(applicationContext, R.drawable.headset_default_k73a_white_enc, "k73@GL_default_white");
                            if (decodeImageResourceByKey == null) {
                                Log.d("MiuiHeadsetFindDeviceFragment", "bitmap null");
                                return;
                            }
                            MiuiHeadsetFindDeviceFragment.this.mDeviceImage.setImageDrawable(new BitmapDrawable(MiuiHeadsetFindDeviceFragment.this.getResources(), decodeImageResourceByKey));
                        }
                    }, 1L);
                } else if (HeadsetIDConstants.isK73ABlackHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_default_k73a_black_enc);
                    this.mDeviceImage.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.4
                        @Override // java.lang.Runnable
                        public void run() {
                            Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey(applicationContext, R.drawable.headset_default_k73a_black_enc, "k73@GL_default_black");
                            if (decodeImageResourceByKey == null) {
                                Log.d("MiuiHeadsetFindDeviceFragment", "bitmap null");
                                return;
                            }
                            MiuiHeadsetFindDeviceFragment.this.mDeviceImage.setImageDrawable(new BitmapDrawable(MiuiHeadsetFindDeviceFragment.this.getResources(), decodeImageResourceByKey));
                        }
                    }, 1L);
                } else if (HeadsetIDConstants.isK73AGreenHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_default_k73a_green_enc);
                    this.mDeviceImage.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.5
                        @Override // java.lang.Runnable
                        public void run() {
                            Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey(applicationContext, R.drawable.headset_default_k73a_green_enc, "k73@GL_default_green");
                            if (decodeImageResourceByKey == null) {
                                Log.d("MiuiHeadsetFindDeviceFragment", "bitmap null");
                                return;
                            }
                            MiuiHeadsetFindDeviceFragment.this.mDeviceImage.setImageDrawable(new BitmapDrawable(MiuiHeadsetFindDeviceFragment.this.getResources(), decodeImageResourceByKey));
                        }
                    }, 1L);
                }
            }
        } else if (HeadsetIDConstants.isK75Headset(this.mDeviceId)) {
            Log.d("MiuiHeadsetFindDeviceFragment", "K75 headset find device");
            ImageView imageView3 = this.mDeviceImage;
            if (imageView3 != null) {
                LinearLayout.LayoutParams layoutParams5 = (LinearLayout.LayoutParams) imageView3.getLayoutParams();
                Log.d("MiuiHeadsetFindDeviceFragment", layoutParams5.height + " " + layoutParams5.width + " " + layoutParams5.weight);
                layoutParams5.height = (int) (((double) layoutParams5.height) * 1.16d);
                this.mDeviceImage.setLayoutParams(layoutParams5);
                LinearLayout.LayoutParams layoutParams6 = (LinearLayout.LayoutParams) this.mDeviceImage.getLayoutParams();
                Log.d("MiuiHeadsetFindDeviceFragment", layoutParams6.height + " " + layoutParams6.width + " " + layoutParams6.weight);
                if (HeadsetIDConstants.isK75WhiteHeadset(this.mDeviceId) || HeadsetIDConstants.isK75AWhiteHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_find_device_k75_white_enc);
                    this.mDeviceImage.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.6
                        @Override // java.lang.Runnable
                        public void run() {
                            Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey(applicationContext, R.drawable.headset_find_device_k75_white_enc, "k75_white");
                            if (decodeImageResourceByKey == null) {
                                Log.d("MiuiHeadsetFindDeviceFragment", "bitmap null");
                                return;
                            }
                            MiuiHeadsetFindDeviceFragment.this.mDeviceImage.setImageDrawable(new BitmapDrawable(MiuiHeadsetFindDeviceFragment.this.getResources(), decodeImageResourceByKey));
                        }
                    }, 1L);
                } else if (HeadsetIDConstants.isK75BlackHeadset(this.mDeviceId) || HeadsetIDConstants.isK75ABlackHeadset(this.mDeviceId)) {
                    this.mDeviceImage.setImageResource(R.drawable.headset_find_device_k75_black_enc);
                    this.mDeviceImage.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.7
                        @Override // java.lang.Runnable
                        public void run() {
                            Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey(applicationContext, R.drawable.headset_find_device_k75_black_enc, "k75_black");
                            if (decodeImageResourceByKey == null) {
                                Log.d("MiuiHeadsetFindDeviceFragment", "bitmap null");
                                return;
                            }
                            MiuiHeadsetFindDeviceFragment.this.mDeviceImage.setImageDrawable(new BitmapDrawable(MiuiHeadsetFindDeviceFragment.this.getResources(), decodeImageResourceByKey));
                        }
                    }, 1L);
                }
            }
        } else if (HeadsetIDConstants.isTWS200(this.mDeviceId)) {
            LinearLayout.LayoutParams layoutParams7 = (LinearLayout.LayoutParams) this.mDeviceImage.getLayoutParams();
            Log.d("MiuiHeadsetFindDeviceFragment", layoutParams7.height + " " + layoutParams7.width + " " + layoutParams7.weight);
            layoutParams7.height = (int) (((double) layoutParams7.height) * 1.16d);
            this.mDeviceImage.setLayoutParams(layoutParams7);
            LinearLayout.LayoutParams layoutParams8 = (LinearLayout.LayoutParams) this.mDeviceImage.getLayoutParams();
            Log.d("MiuiHeadsetFindDeviceFragment", layoutParams8.height + " " + layoutParams8.width + " " + layoutParams8.weight);
            this.mDeviceImage.setImageResource(R.drawable.headset_find_device_TWS200);
        } else if (HeadsetIDConstants.isK77sHeadset(this.mDeviceId)) {
            Log.d("MiuiHeadsetFindDeviceFragment", "initView K77s fitness");
            ImageView imageView4 = this.mDeviceImage;
            if (imageView4 != null) {
                imageView4.setImageResource(R.drawable.headset_TWS_k77s);
            }
        } else if (!"0201010001".equals(this.mDeviceId) && !"0201010000".equals(this.mDeviceId)) {
            this.mDeviceId.hashCode();
            int i = R.drawable.bt_headset_find_detail;
            if (MiuiHeadsetAnimation.checkLocalCached(this.mDeviceId, applicationContext)) {
                BitmapDrawable findDeviceDrawable = MiuiHeadsetAnimation.getFindDeviceDrawable(this.mDeviceId, applicationContext);
                if (findDeviceDrawable != null) {
                    this.mDeviceImage.setImageDrawable(findDeviceDrawable);
                }
            } else {
                this.mDeviceImage.setImageResource(i);
            }
        }
        if (this.mDevice.isConnected()) {
            getBatteryInfo();
            updateConnectedView();
        } else {
            updateDisconnectedView();
        }
        setListener();
    }

    private boolean judgeStatusByBattery(String str) {
        Log.d("MiuiHeadsetFindDeviceFragment", "judgeStatusByBattery ");
        int parseInt = Integer.parseInt(str) & 127;
        return parseInt > 0 && parseInt <= 100;
    }

    private void saveDeviceInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("Headset_DeviceId", this.mDeviceId);
        setArguments(bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendCmdBoth(boolean z) {
        IMiuiHeadsetService iMiuiHeadsetService;
        Log.d("MiuiHeadsetFindDeviceFragment", " sendCmdBoth ");
        try {
            BluetoothDevice bluetoothDevice = this.mDevice;
            if (bluetoothDevice != null && (iMiuiHeadsetService = this.mService) != null) {
                if (z) {
                    iMiuiHeadsetService.setCommonCommand(108, "0103", bluetoothDevice);
                } else {
                    iMiuiHeadsetService.setCommonCommand(108, "0003", bluetoothDevice);
                }
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetFindDeviceFragment", " SET_FIND_DEVICE_CMD_BOTH failed: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendCmdLeft(boolean z) {
        IMiuiHeadsetService iMiuiHeadsetService;
        Log.d("MiuiHeadsetFindDeviceFragment", " sendCmdLeft ");
        try {
            BluetoothDevice bluetoothDevice = this.mDevice;
            if (bluetoothDevice != null && (iMiuiHeadsetService = this.mService) != null) {
                if (z) {
                    iMiuiHeadsetService.setCommonCommand(108, "0101", bluetoothDevice);
                } else {
                    iMiuiHeadsetService.setCommonCommand(108, "0001", bluetoothDevice);
                }
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetFindDeviceFragment", " FIND_DEVICE_STATUS_LEFT failed: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendCmdRight(boolean z) {
        IMiuiHeadsetService iMiuiHeadsetService;
        Log.d("MiuiHeadsetFindDeviceFragment", " sendCmdRight ");
        try {
            BluetoothDevice bluetoothDevice = this.mDevice;
            if (bluetoothDevice != null && (iMiuiHeadsetService = this.mService) != null) {
                if (z) {
                    iMiuiHeadsetService.setCommonCommand(108, "0102", bluetoothDevice);
                } else {
                    iMiuiHeadsetService.setCommonCommand(108, "0002", bluetoothDevice);
                }
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetFindDeviceFragment", " SET_FIND_DEVICE_CMD_RIGHT failed: " + e);
        }
    }

    private void setListener() {
        this.mLeftIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!MiuiHeadsetFindDeviceFragment.this.mLeftConnected) {
                    Log.e("MiuiHeadsetFindDeviceFragment", "earphone left is not connected");
                } else if (MiuiHeadsetFindDeviceFragment.this.mLeftPlaying) {
                    MiuiHeadsetFindDeviceFragment.this.sendCmdLeft(false);
                } else if ("2".equals(MiuiHeadsetFindDeviceFragment.this.getInEarStatus()) || "1".equals(MiuiHeadsetFindDeviceFragment.this.getInEarStatus())) {
                    Log.d("MiuiHeadsetFindDeviceFragment", "earphone is in ear");
                    MiuiHeadsetFindDeviceFragment.this.askDeviceDialog(0);
                } else {
                    if (MiuiHeadsetFindDeviceFragment.this.mRightPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdRight(false);
                    }
                    if (MiuiHeadsetFindDeviceFragment.this.mBothPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdBoth(false);
                    }
                    MiuiHeadsetFindDeviceFragment.this.sendCmdLeft(!r3.mLeftPlaying);
                }
            }
        });
        this.mRightIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!MiuiHeadsetFindDeviceFragment.this.mRightConnected) {
                    Log.e("MiuiHeadsetFindDeviceFragment", "earphone right is not connected");
                } else if (MiuiHeadsetFindDeviceFragment.this.mRightPlaying) {
                    MiuiHeadsetFindDeviceFragment.this.sendCmdRight(false);
                } else if (ExtraTelephony.Phonelist.TYPE_VIP.equals(MiuiHeadsetFindDeviceFragment.this.getInEarStatus()) || "1".equals(MiuiHeadsetFindDeviceFragment.this.getInEarStatus())) {
                    Log.d("MiuiHeadsetFindDeviceFragment", "earphone is in ear");
                    MiuiHeadsetFindDeviceFragment.this.askDeviceDialog(1);
                } else {
                    if (MiuiHeadsetFindDeviceFragment.this.mBothPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdBoth(false);
                    }
                    if (MiuiHeadsetFindDeviceFragment.this.mLeftPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdLeft(false);
                    }
                    MiuiHeadsetFindDeviceFragment.this.sendCmdRight(!r4.mRightPlaying);
                }
            }
        });
        this.mBothIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!MiuiHeadsetFindDeviceFragment.this.mLeftConnected || !MiuiHeadsetFindDeviceFragment.this.mRightConnected) {
                    Log.e("MiuiHeadsetFindDeviceFragment", "earphone both is not connected");
                } else if (MiuiHeadsetFindDeviceFragment.this.mBothPlaying) {
                    MiuiHeadsetFindDeviceFragment.this.sendCmdBoth(false);
                } else if (!"0".equals(MiuiHeadsetFindDeviceFragment.this.getInEarStatus())) {
                    Log.d("MiuiHeadsetFindDeviceFragment", " earphone is in ear");
                    MiuiHeadsetFindDeviceFragment.this.askDeviceDialog(2);
                } else {
                    if (MiuiHeadsetFindDeviceFragment.this.mRightPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdRight(false);
                    }
                    if (MiuiHeadsetFindDeviceFragment.this.mLeftPlaying) {
                        MiuiHeadsetFindDeviceFragment.this.sendCmdLeft(false);
                    }
                    MiuiHeadsetFindDeviceFragment.this.sendCmdBoth(!r3.mBothPlaying);
                }
            }
        });
    }

    private AlertDialog showDeviceDialog(Context context, AlertDialog alertDialog, DialogInterface.OnClickListener onClickListener, CharSequence charSequence, CharSequence charSequence2) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(context).setPositiveButton(context.getString(R.string.headset_find_query_play), onClickListener).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        } else if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        alertDialog.setTitle(charSequence);
        alertDialog.setMessage(charSequence2);
        alertDialog.show();
        this.mDialog = alertDialog;
        return alertDialog;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConnectedView() {
        Log.d("MiuiHeadsetFindDeviceFragment", " updateConnectedView connect");
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.11
            @Override // java.lang.Runnable
            public void run() {
                MiuiHeadsetFindDeviceFragment.this.mAliasText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(R.color.headset_find_device_80));
                MiuiHeadsetFindDeviceFragment.this.mConnectStateIcon.setImageResource(R.drawable.headset_connect_blue);
                MiuiHeadsetFindDeviceFragment.this.mConnectStateText.setText(R.string.headset_find_notify_connected);
                MiuiHeadsetFindDeviceFragment.this.mDeviceImage.setAlpha(1.0f);
                MiuiHeadsetFindDeviceFragment.this.mHandleTips.setVisibility(0);
            }
        });
    }

    private void updateConnetedItemView(final boolean z, final boolean z2) {
        Log.d("MiuiHeadsetFindDeviceFragment", "updateConnetedItemView connect item " + z + z2);
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.14
            @Override // java.lang.Runnable
            public void run() {
                CardView cardView = (CardView) MiuiHeadsetFindDeviceFragment.this.mFindDeviceView.findViewById(R.id.view_corner_both);
                if (z && z2) {
                    MiuiHeadsetFindDeviceFragment.this.updateConnectedView();
                    TextView textView = MiuiHeadsetFindDeviceFragment.this.mBothText;
                    Resources resources = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                    int i = R.color.headset_find_device_80;
                    textView.setTextColor(resources.getColor(i));
                    MiuiHeadsetFindDeviceFragment.this.mBothStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i));
                    MiuiHeadsetFindDeviceFragment.this.mBothStateText.setText(R.string.headset_find_connected);
                    MiuiHeadsetFindDeviceFragment.this.mBothIcon.setAlpha(1.0f);
                } else {
                    MiuiHeadsetFindDeviceFragment.this.mBothIcon.setAlpha(0.1f);
                    TextView textView2 = MiuiHeadsetFindDeviceFragment.this.mBothText;
                    Resources resources2 = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                    int i2 = R.color.headset_find_device_30;
                    textView2.setTextColor(resources2.getColor(i2));
                    MiuiHeadsetFindDeviceFragment.this.mBothStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i2));
                    MiuiHeadsetFindDeviceFragment.this.mBothStateText.setText(R.string.headset_find_disconnect);
                    MiuiHeadsetFindDeviceFragment.this.mBothIcon.setImageResource(R.drawable.headset_play_bell);
                    cardView.setCardBackgroundColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(R.color.headset_find_device_gray));
                }
                if (!z) {
                    MiuiHeadsetFindDeviceFragment.this.mLeftIcon.setAlpha(0.1f);
                    TextView textView3 = MiuiHeadsetFindDeviceFragment.this.mLeftText;
                    Resources resources3 = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                    int i3 = R.color.headset_find_device_30;
                    textView3.setTextColor(resources3.getColor(i3));
                    MiuiHeadsetFindDeviceFragment.this.mLeftStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i3));
                    MiuiHeadsetFindDeviceFragment.this.mLeftStateText.setText(R.string.headset_find_disconnect);
                    MiuiHeadsetFindDeviceFragment.this.mLeftIcon.setImageResource(R.drawable.headset_play_bell);
                    ((CardView) MiuiHeadsetFindDeviceFragment.this.mFindDeviceView.findViewById(R.id.view_corner_left)).setCardBackgroundColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(R.color.headset_find_device_gray));
                } else if (!MiuiHeadsetFindDeviceFragment.this.mLeftPlaying) {
                    MiuiHeadsetFindDeviceFragment.this.mLeftIcon.setAlpha(1.0f);
                    TextView textView4 = MiuiHeadsetFindDeviceFragment.this.mLeftText;
                    Resources resources4 = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                    int i4 = R.color.headset_find_device_80;
                    textView4.setTextColor(resources4.getColor(i4));
                    MiuiHeadsetFindDeviceFragment.this.mLeftStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i4));
                    MiuiHeadsetFindDeviceFragment.this.mLeftStateText.setText(R.string.headset_find_connected);
                }
                if (z2) {
                    if (MiuiHeadsetFindDeviceFragment.this.mRightPlaying) {
                        return;
                    }
                    TextView textView5 = MiuiHeadsetFindDeviceFragment.this.mRightText;
                    Resources resources5 = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                    int i5 = R.color.headset_find_device_80;
                    textView5.setTextColor(resources5.getColor(i5));
                    MiuiHeadsetFindDeviceFragment.this.mRightStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i5));
                    MiuiHeadsetFindDeviceFragment.this.mRightStateText.setText(R.string.headset_find_connected);
                    MiuiHeadsetFindDeviceFragment.this.mRightIcon.setAlpha(1.0f);
                    return;
                }
                MiuiHeadsetFindDeviceFragment.this.mRightIcon.setAlpha(0.1f);
                TextView textView6 = MiuiHeadsetFindDeviceFragment.this.mRightText;
                Resources resources6 = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                int i6 = R.color.headset_find_device_30;
                textView6.setTextColor(resources6.getColor(i6));
                MiuiHeadsetFindDeviceFragment.this.mRightStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i6));
                MiuiHeadsetFindDeviceFragment.this.mRightStateText.setText(R.string.headset_find_disconnect);
                MiuiHeadsetFindDeviceFragment.this.mRightIcon.setImageResource(R.drawable.headset_play_bell);
                ((CardView) MiuiHeadsetFindDeviceFragment.this.mFindDeviceView.findViewById(R.id.view_corner_right)).setCardBackgroundColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(R.color.headset_find_device_gray));
            }
        });
        this.mLeftConnected = z;
        this.mRightConnected = z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDisconnectedItemView() {
        Log.d("MiuiHeadsetFindDeviceFragment", "item disconnect ");
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.13
            @Override // java.lang.Runnable
            public void run() {
                MiuiHeadsetFindDeviceFragment.this.mLeftIcon.setAlpha(0.1f);
                MiuiHeadsetFindDeviceFragment.this.mBothIcon.setAlpha(0.1f);
                MiuiHeadsetFindDeviceFragment.this.mRightIcon.setAlpha(0.1f);
                TextView textView = MiuiHeadsetFindDeviceFragment.this.mLeftText;
                Resources resources = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                int i = R.color.headset_find_device_30;
                textView.setTextColor(resources.getColor(i));
                MiuiHeadsetFindDeviceFragment.this.mLeftStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i));
                TextView textView2 = MiuiHeadsetFindDeviceFragment.this.mLeftStateText;
                int i2 = R.string.headset_find_disconnect;
                textView2.setText(i2);
                ImageView imageView = MiuiHeadsetFindDeviceFragment.this.mLeftIcon;
                int i3 = R.drawable.headset_play_bell;
                imageView.setImageResource(i3);
                CardView cardView = (CardView) MiuiHeadsetFindDeviceFragment.this.mFindDeviceView.findViewById(R.id.view_corner_left);
                Resources resources2 = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                int i4 = R.color.headset_find_device_gray;
                cardView.setCardBackgroundColor(resources2.getColor(i4));
                MiuiHeadsetFindDeviceFragment.this.mRightText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i));
                MiuiHeadsetFindDeviceFragment.this.mRightStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i));
                MiuiHeadsetFindDeviceFragment.this.mRightStateText.setText(i2);
                MiuiHeadsetFindDeviceFragment.this.mRightIcon.setImageResource(i3);
                ((CardView) MiuiHeadsetFindDeviceFragment.this.mFindDeviceView.findViewById(R.id.view_corner_right)).setCardBackgroundColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i4));
                MiuiHeadsetFindDeviceFragment.this.mBothText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i));
                MiuiHeadsetFindDeviceFragment.this.mBothStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i));
                MiuiHeadsetFindDeviceFragment.this.mBothStateText.setText(i2);
                MiuiHeadsetFindDeviceFragment.this.mBothIcon.setImageResource(i3);
                ((CardView) MiuiHeadsetFindDeviceFragment.this.mFindDeviceView.findViewById(R.id.view_corner_both)).setCardBackgroundColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i4));
            }
        });
        this.mLeftConnected = false;
        this.mRightConnected = false;
        this.mLeftPlaying = false;
        this.mRightPlaying = false;
        this.mBothPlaying = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDisconnectedView() {
        Log.d("MiuiHeadsetFindDeviceFragment", "updateDisconnectedView disconnect");
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.12
            @Override // java.lang.Runnable
            public void run() {
                MiuiHeadsetFindDeviceFragment.this.mAliasText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(R.color.headset_find_device_30));
                MiuiHeadsetFindDeviceFragment.this.mConnectStateIcon.setImageResource(R.drawable.headset_disconnect_red);
                MiuiHeadsetFindDeviceFragment.this.mConnectStateText.setText(R.string.headset_find_notify_disconnect);
                MiuiHeadsetFindDeviceFragment.this.mDeviceImage.setAlpha(0.3f);
                MiuiHeadsetFindDeviceFragment.this.updateDisconnectedItemView();
                MiuiHeadsetFindDeviceFragment.this.mHandleTips.setVisibility(8);
            }
        });
    }

    private void updateUIBoth(final boolean z) {
        Log.d("MiuiHeadsetFindDeviceFragment", " updateUIBoth ");
        if (this.mLeftConnected && this.mRightConnected) {
            getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.16
                @Override // java.lang.Runnable
                public void run() {
                    CardView cardView = (CardView) MiuiHeadsetFindDeviceFragment.this.mFindDeviceView.findViewById(R.id.view_corner_both);
                    if (z) {
                        TextView textView = MiuiHeadsetFindDeviceFragment.this.mBothText;
                        Resources resources = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                        int i = R.color.headset_find_device_white;
                        textView.setTextColor(resources.getColor(i));
                        MiuiHeadsetFindDeviceFragment.this.mBothStateText.setText(R.string.headset_find_playing);
                        MiuiHeadsetFindDeviceFragment.this.mBothStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i));
                        MiuiHeadsetFindDeviceFragment.this.mBothIcon.setImageResource(R.drawable.headset_earphone_both);
                        cardView.setCardBackgroundColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(R.color.headset_find_device_blue));
                        return;
                    }
                    TextView textView2 = MiuiHeadsetFindDeviceFragment.this.mBothText;
                    Resources resources2 = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                    int i2 = R.color.headset_find_device_80;
                    textView2.setTextColor(resources2.getColor(i2));
                    MiuiHeadsetFindDeviceFragment.this.mBothStateText.setText(R.string.headset_find_connected);
                    MiuiHeadsetFindDeviceFragment.this.mBothStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i2));
                    MiuiHeadsetFindDeviceFragment.this.mBothIcon.setImageResource(R.drawable.headset_play_bell);
                    cardView.setCardBackgroundColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(R.color.headset_find_device_gray));
                }
            });
            this.mBothPlaying = z;
        }
    }

    private void updateUILeft(final boolean z) {
        Log.d("MiuiHeadsetFindDeviceFragment", " updateUILeft ");
        if (this.mLeftConnected) {
            getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.15
                @Override // java.lang.Runnable
                public void run() {
                    CardView cardView = (CardView) MiuiHeadsetFindDeviceFragment.this.mFindDeviceView.findViewById(R.id.view_corner_left);
                    if (z) {
                        TextView textView = MiuiHeadsetFindDeviceFragment.this.mLeftText;
                        Resources resources = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                        int i = R.color.headset_find_device_white;
                        textView.setTextColor(resources.getColor(i));
                        MiuiHeadsetFindDeviceFragment.this.mLeftStateText.setText(R.string.headset_find_playing);
                        MiuiHeadsetFindDeviceFragment.this.mLeftStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i));
                        MiuiHeadsetFindDeviceFragment.this.mLeftIcon.setImageResource(R.drawable.headset_earphone_left);
                        cardView.setCardBackgroundColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(R.color.headset_find_device_blue));
                        return;
                    }
                    TextView textView2 = MiuiHeadsetFindDeviceFragment.this.mLeftText;
                    Resources resources2 = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                    int i2 = R.color.headset_find_device_80;
                    textView2.setTextColor(resources2.getColor(i2));
                    MiuiHeadsetFindDeviceFragment.this.mLeftStateText.setText(R.string.headset_find_connected);
                    MiuiHeadsetFindDeviceFragment.this.mLeftStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i2));
                    MiuiHeadsetFindDeviceFragment.this.mLeftIcon.setImageResource(R.drawable.headset_play_bell);
                    cardView.setCardBackgroundColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(R.color.headset_find_device_gray));
                }
            });
            this.mLeftPlaying = z;
        }
    }

    private void updateUIRight(final boolean z) {
        Log.d("MiuiHeadsetFindDeviceFragment", " updateUIRight ");
        if (this.mRightConnected) {
            getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFindDeviceFragment.17
                @Override // java.lang.Runnable
                public void run() {
                    CardView cardView = (CardView) MiuiHeadsetFindDeviceFragment.this.mFindDeviceView.findViewById(R.id.view_corner_right);
                    if (z) {
                        TextView textView = MiuiHeadsetFindDeviceFragment.this.mRightText;
                        Resources resources = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                        int i = R.color.headset_find_device_white;
                        textView.setTextColor(resources.getColor(i));
                        MiuiHeadsetFindDeviceFragment.this.mRightStateText.setText(R.string.headset_find_playing);
                        MiuiHeadsetFindDeviceFragment.this.mRightStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i));
                        MiuiHeadsetFindDeviceFragment.this.mRightIcon.setImageResource(R.drawable.headset_earphone_right);
                        cardView.setCardBackgroundColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(R.color.headset_find_device_blue));
                        return;
                    }
                    TextView textView2 = MiuiHeadsetFindDeviceFragment.this.mRightText;
                    Resources resources2 = MiuiHeadsetFindDeviceFragment.this.mActivity.getResources();
                    int i2 = R.color.headset_find_device_80;
                    textView2.setTextColor(resources2.getColor(i2));
                    MiuiHeadsetFindDeviceFragment.this.mRightStateText.setText(R.string.headset_find_connected);
                    MiuiHeadsetFindDeviceFragment.this.mRightStateText.setTextColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(i2));
                    MiuiHeadsetFindDeviceFragment.this.mRightIcon.setImageResource(R.drawable.headset_play_bell);
                    cardView.setCardBackgroundColor(MiuiHeadsetFindDeviceFragment.this.mActivity.getResources().getColor(R.color.headset_find_device_gray));
                }
            });
            this.mRightPlaying = z;
        }
    }

    private void updateView(View view, int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.setMarginEnd(i);
        marginLayoutParams.setMarginStart(i);
        view.setLayoutParams(marginLayoutParams);
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MiuiHeadsetActivity miuiHeadsetActivity = (MiuiHeadsetActivity) activity;
        this.mHeadSetAct = miuiHeadsetActivity;
        String deviceID = miuiHeadsetActivity.getDeviceID();
        this.mDeviceId = deviceID;
        if (deviceID == null || "".equals(deviceID)) {
            return;
        }
        saveDeviceInfo();
    }

    @Override // miuix.preference.PreferenceFragment, androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        int dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_find_device_tex_device_name_width);
        int dimensionPixelSize2 = this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_find_device_image_device_icon_width);
        int dimensionPixelSize3 = this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_find_device_image_device_icon_height);
        int dimensionPixelSize4 = this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_find_device_linear_tips_height);
        Log.w("MiuiHeadsetFindDeviceFragment", "config changed " + dimensionPixelSize4 + " " + dimensionPixelSize2 + " " + dimensionPixelSize2 + dimensionPixelSize);
        int dimensionPixelSize5 = this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_find_device_card_width) - this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_find_device_card_content_marginStart);
        View view = this.mFindDeviceView;
        if (view != null) {
            ((TextView) view.findViewById(R.id.tv_set_message)).getLayoutParams().width = dimensionPixelSize4;
            ((TextView) this.mFindDeviceView.findViewById(R.id.tv_set_title)).getLayoutParams().width = dimensionPixelSize4;
            ((LinearLayout) this.mFindDeviceView.findViewById(R.id.item_left)).getLayoutParams().width = dimensionPixelSize5;
            ((TextView) this.mFindDeviceView.findViewById(R.id.tv_state_title_left)).getLayoutParams().width = dimensionPixelSize5;
            ((TextView) this.mFindDeviceView.findViewById(R.id.tv_state_description_left)).getLayoutParams().width = dimensionPixelSize5;
            ((LinearLayout) this.mFindDeviceView.findViewById(R.id.item_both)).getLayoutParams().width = dimensionPixelSize5;
            ((TextView) this.mFindDeviceView.findViewById(R.id.tv_state_title_both)).getLayoutParams().width = dimensionPixelSize5;
            ((TextView) this.mFindDeviceView.findViewById(R.id.tv_state_description_both)).getLayoutParams().width = dimensionPixelSize5;
            ((LinearLayout) this.mFindDeviceView.findViewById(R.id.item_right)).getLayoutParams().width = dimensionPixelSize5;
            ((TextView) this.mFindDeviceView.findViewById(R.id.tv_state_title_right)).getLayoutParams().width = dimensionPixelSize5;
            ((TextView) this.mFindDeviceView.findViewById(R.id.tv_state_description_right)).getLayoutParams().width = dimensionPixelSize5;
        }
        ImageView imageView = this.mDeviceImage;
        if (imageView != null) {
            imageView.getLayoutParams().width = dimensionPixelSize2;
            this.mDeviceImage.getLayoutParams().height = dimensionPixelSize3;
        }
        TextView textView = this.mAliasText;
        if (textView != null) {
            textView.getLayoutParams().width = dimensionPixelSize;
        }
        if (this.mFindDeviceView != null) {
            updateView(this.mFindDeviceView.findViewById(R.id.find_module), this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_find_device_card_family_margin_start));
        }
        super.onConfigurationChanged(configuration);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        if (getArguments() != null) {
            this.mDeviceId = getArguments().getString("Headset_DeviceId");
        }
        super.onCreate(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mDevice = ((MiuiHeadsetActivity) getActivity()).getDevice();
        this.mActivity = (MiuiHeadsetActivity) getActivity();
        this.mFindDeviceView = layoutInflater.inflate(R.layout.headset_find_device, viewGroup, false);
        this.mService = this.mActivity.getService();
        initView();
        return this.mFindDeviceView;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (this.mLeftPlaying) {
            sendCmdLeft(false);
        }
        if (this.mRightPlaying) {
            sendCmdRight(false);
        }
        if (this.mBothPlaying) {
            sendCmdBoth(false);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            try {
                Method declaredMethod = this.mDialog.getClass().getDeclaredMethod("realDismiss", new Class[0]);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(this.mDialog, new Object[0]);
                this.mDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Log.d("MiuiHeadsetFindDeviceFragment", "onResume ");
        try {
            BluetoothDevice bluetoothDevice = this.mDevice;
            if (bluetoothDevice == null || !(bluetoothDevice == null || bluetoothDevice.isConnected())) {
                Log.d("MiuiHeadsetFindDeviceFragment", "device: " + this.mDevice);
                updateDisconnectedView();
            }
        } catch (Exception e) {
            Log.d("MiuiHeadsetFindDeviceFragment", "onResume error: " + e);
        }
    }

    public void onServiceConnected() {
        this.mService = this.mActivity.getService();
        this.mDevice = this.mActivity.getDevice();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(this.mBluetoothReceiver, new IntentFilter("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED"));
        Log.d("MiuiHeadsetFindDeviceFragment", " onStart ");
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.mBluetoothReceiver != null) {
            getActivity().unregisterReceiver(this.mBluetoothReceiver);
        }
        Log.d("MiuiHeadsetFindDeviceFragment", " onStop");
    }

    public void refreshStatus(String str, String str2) {
        String[] split = str2.split("\\,", -1);
        if (split.length != 16) {
            return;
        }
        boolean z = false;
        boolean z2 = !TextUtils.isEmpty(split[0]) && judgeStatusByBattery(split[0]);
        if (!TextUtils.isEmpty(split[1]) && judgeStatusByBattery(split[1])) {
            z = true;
        }
        boolean isEmpty = true ^ TextUtils.isEmpty(split[10]);
        Log.d("MiuiHeadsetFindDeviceFragment", " status " + str2 + "  " + str + z2 + z);
        if (!(getFragmentManager().findFragmentById(R.id.layout_content) instanceof MiuiHeadsetFindDeviceFragment) || this.mFindDeviceView == null) {
            Log.d("MiuiHeadsetFindDeviceFragment", "no MiuiHeadsetFindDeviceFragment no refresh");
            return;
        }
        if ((z2 || z) && (this.mLeftConnected != z2 || this.mRightConnected != z)) {
            updateConnetedItemView(z2, z);
        }
        if (isEmpty) {
            handleCallbackStatus(split[10]);
        }
    }
}
