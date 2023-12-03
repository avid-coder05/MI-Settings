package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.bluetooth.ble.app.IMiuiHeadsetService;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.bluetooth.tws.MiuiHeadsetAnimation;
import com.android.settings.utils.EncryptionUtil;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public final class MiuiHeadsetFitnessFragment extends MiuiSettingsPreferenceFragment implements View.OnClickListener {
    private AudioManager mAudioManager;
    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothHeadset mBluetoothHfp;
    private Button mCheckBtn;
    private BluetoothDevice mDevice;
    private Button mDoneBtn;
    private InnerHandler mHandler;
    private MiuiHeadsetActivity mHeadSetAct;
    private ImageView mIconLeftIV;
    private ImageView mIconRightIV;
    private boolean mMusicNeedPlay;
    private int mOrientation;
    private LinearLayout mProgressLL;
    private Button mReCheckBtn;
    private LinearLayout mRecheckLL;
    private LinearLayout mResultLL;
    private TextView mResultLeftTV;
    private TextView mResultRightTV;
    private View mRootView;
    private SoundPool mSoundPool;
    private int mStreamID;
    private TextView mSummaryTV;
    private AlertDialog mDialog = null;
    private IMiuiHeadsetService mService = null;
    private final Object mBluetoothHfpLock = new Object();
    private final Object mBluetoothA2dpLock = new Object();
    private String mDeviceId = "";
    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("MiuiHeadsetFitnessFragment", "BluetoothReceiver.onReceive intent=" + intent);
            if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (intExtra == 2 && bluetoothDevice != null && bluetoothDevice.equals(MiuiHeadsetFitnessFragment.this.mDevice) && MiuiHeadsetFitnessFragment.this.mRootView != null) {
                    MiuiHeadsetFitnessFragment.this.setPreferenceEnable(true);
                } else if (intExtra != 0 || bluetoothDevice == null || !bluetoothDevice.equals(MiuiHeadsetFitnessFragment.this.mDevice) || MiuiHeadsetFitnessFragment.this.mRootView == null) {
                } else {
                    MiuiHeadsetFitnessFragment.this.setPreferenceEnable(false);
                }
            }
        }
    };
    private BluetoothProfile.ServiceListener mBluetoothHfpServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.15
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            Log.d("MiuiHeadsetFitnessFragment", "onHfpServiceConnected()");
            synchronized (MiuiHeadsetFitnessFragment.this.mBluetoothHfpLock) {
                MiuiHeadsetFitnessFragment.this.mBluetoothHfp = (BluetoothHeadset) bluetoothProfile;
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            Log.d("MiuiHeadsetFitnessFragment", "onHfpServiceDisconnected()");
            synchronized (MiuiHeadsetFitnessFragment.this.mBluetoothHfpLock) {
                MiuiHeadsetFitnessFragment.this.closeProfileProxy();
            }
        }
    };
    private BluetoothProfile.ServiceListener mBluetoothA2dpServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.16
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            Log.d("MiuiHeadsetFitnessFragment", "onA2dpServiceConnected()");
            synchronized (MiuiHeadsetFitnessFragment.this.mBluetoothA2dpLock) {
                MiuiHeadsetFitnessFragment.this.mBluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            Log.d("MiuiHeadsetFitnessFragment", "onA2dpServiceDisconnected()");
            synchronized (MiuiHeadsetFitnessFragment.this.mBluetoothA2dpLock) {
                MiuiHeadsetFitnessFragment.this.closeProfileProxy();
            }
        }
    };
    int state = 0;

    /* renamed from: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment$17  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass17 implements View.OnClickListener {
        final /* synthetic */ MiuiHeadsetFitnessFragment this$0;

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MiuiHeadsetFitnessFragment miuiHeadsetFitnessFragment = this.this$0;
            int i = miuiHeadsetFitnessFragment.state;
            if (i == 0) {
                miuiHeadsetFitnessFragment.state = 1;
                miuiHeadsetFitnessFragment.updateUIForResult("0000");
            } else if (i == 1) {
                miuiHeadsetFitnessFragment.state = 2;
                miuiHeadsetFitnessFragment.updateUIForResult("0202");
            } else if (i == 2) {
                miuiHeadsetFitnessFragment.state = 3;
                miuiHeadsetFitnessFragment.updateUIForResult("0102");
            } else if (i == 3) {
                miuiHeadsetFitnessFragment.state = 4;
                miuiHeadsetFitnessFragment.updateUIForResult("0201");
            } else if (i == 4) {
                miuiHeadsetFitnessFragment.state = 5;
                miuiHeadsetFitnessFragment.updateUIForResult("0101");
            } else if (i == 5) {
                miuiHeadsetFitnessFragment.state = 6;
                miuiHeadsetFitnessFragment.updateUIForResult("0303");
            } else if (i == 6) {
                miuiHeadsetFitnessFragment.state = 7;
                miuiHeadsetFitnessFragment.updateUIForResult("0100");
            } else if (i == 7) {
                miuiHeadsetFitnessFragment.state = 8;
                miuiHeadsetFitnessFragment.updateUIForResult("0001");
            } else if (i == 8) {
                miuiHeadsetFitnessFragment.state = 9;
                miuiHeadsetFitnessFragment.updateUIForResult("0200");
            } else if (i == 9) {
                miuiHeadsetFitnessFragment.state = 10;
                miuiHeadsetFitnessFragment.updateUIForResult("0002");
            } else if (i == 10) {
                miuiHeadsetFitnessFragment.state = 0;
                miuiHeadsetFitnessFragment.updateUIForResult("0909");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InnerHandler extends Handler {
        private final WeakReference<MiuiHeadsetFitnessFragment> mFragment;

        public InnerHandler(MiuiHeadsetFitnessFragment miuiHeadsetFitnessFragment) {
            this.mFragment = new WeakReference<>(miuiHeadsetFitnessFragment);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            MiuiHeadsetFitnessFragment miuiHeadsetFitnessFragment;
            MiuiHeadsetFitnessFragment miuiHeadsetFitnessFragment2;
            MiuiHeadsetFitnessFragment miuiHeadsetFitnessFragment3;
            switch (message.what) {
                case 100:
                    if (this.mFragment.get() == null || (miuiHeadsetFitnessFragment = this.mFragment.get()) == null) {
                        return;
                    }
                    if (miuiHeadsetFitnessFragment.isSCOOn()) {
                        miuiHeadsetFitnessFragment.createDialog(R.string.miheadset_fitness_check_result_dialog2);
                        return;
                    }
                    miuiHeadsetFitnessFragment.startFitnessCheck();
                    miuiHeadsetFitnessFragment.updateUIToChecking();
                    sendEmptyMessageDelayed(102, 10000L);
                    return;
                case 101:
                    if (this.mFragment.get() == null || (miuiHeadsetFitnessFragment2 = this.mFragment.get()) == null) {
                        return;
                    }
                    String str = (String) message.obj;
                    miuiHeadsetFitnessFragment2.updateUIForResult(str);
                    if ("0303".equals(str)) {
                        return;
                    }
                    removeMessages(102);
                    if ("0909".equals(str)) {
                        return;
                    }
                    miuiHeadsetFitnessFragment2.stopDetectingMusic();
                    miuiHeadsetFitnessFragment2.checkIfNeedPlayMusic();
                    return;
                case 102:
                    Log.e("MiuiHeadsetFitnessFragment", "FITNESS_CHECK_TIMEOUT!");
                    if (this.mFragment.get() == null || (miuiHeadsetFitnessFragment3 = this.mFragment.get()) == null) {
                        return;
                    }
                    miuiHeadsetFitnessFragment3.updateUIToCheck();
                    miuiHeadsetFitnessFragment3.stopDetectingMusic();
                    miuiHeadsetFitnessFragment3.checkIfNeedPlayMusic();
                    return;
                default:
                    return;
            }
        }
    }

    private void back() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkIfNeedPauseMusic() {
        synchronized (this.mBluetoothA2dpLock) {
            BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
            if (bluetoothA2dp != null && this.mAudioManager != null) {
                if (bluetoothA2dp.isA2dpPlaying(this.mDevice) && this.mAudioManager.isMusicActive()) {
                    this.mMusicNeedPlay = true;
                    Log.d("MiuiHeadsetFitnessFragment", "NeedPauseMusic()");
                    this.mAudioManager.dispatchMediaKeyEvent(new KeyEvent(0, 127));
                    this.mAudioManager.dispatchMediaKeyEvent(new KeyEvent(1, 127));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkIfNeedPlayMusic() {
        if (this.mAudioManager != null && this.mMusicNeedPlay) {
            this.mMusicNeedPlay = false;
            Log.d("MiuiHeadsetFitnessFragment", "playMusic()");
            this.mAudioManager.dispatchMediaKeyEvent(new KeyEvent(0, 126));
            this.mAudioManager.dispatchMediaKeyEvent(new KeyEvent(1, 126));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeProfileProxy() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            BluetoothHeadset bluetoothHeadset = this.mBluetoothHfp;
            if (bluetoothHeadset != null) {
                defaultAdapter.closeProfileProxy(1, bluetoothHeadset);
                this.mBluetoothHfp = null;
            }
            BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
            if (bluetoothA2dp != null) {
                defaultAdapter.closeProfileProxy(2, bluetoothA2dp);
                this.mBluetoothA2dp = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createDialog(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.miheadset_fitness_check_result_dialog_title);
        builder.setMessage(i);
        builder.setCancelable(true);
        builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        final AlertDialog create = builder.create();
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.14
            @Override // java.lang.Runnable
            public void run() {
                create.show();
                MiuiHeadsetFitnessFragment.this.mDialog = create;
            }
        });
    }

    private void getProfileProxy() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            return;
        }
        defaultAdapter.getProfileProxy(getActivity().getApplicationContext(), this.mBluetoothHfpServiceListener, 1);
        defaultAdapter.getProfileProxy(getActivity().getApplicationContext(), this.mBluetoothA2dpServiceListener, 2);
    }

    private void initWidget() {
        Handler handler = new Handler();
        this.mResultLL = (LinearLayout) this.mRootView.findViewById(R.id.layout_result);
        this.mRecheckLL = (LinearLayout) this.mRootView.findViewById(R.id.fitness_btn_lauout);
        this.mProgressLL = (LinearLayout) this.mRootView.findViewById(R.id.fitness_progress_ll);
        this.mSummaryTV = (TextView) this.mRootView.findViewById(R.id.fitness_summary);
        this.mResultLeftTV = (TextView) this.mRootView.findViewById(R.id.fitness_result_l);
        this.mResultRightTV = (TextView) this.mRootView.findViewById(R.id.fitness_result_r);
        Button button = (Button) this.mRootView.findViewById(R.id.fitness_start);
        this.mCheckBtn = button;
        button.setOnClickListener(this);
        this.mCheckBtn.setTag("continue");
        Button button2 = (Button) this.mRootView.findViewById(R.id.fitness_btn_restart);
        this.mReCheckBtn = button2;
        button2.setOnClickListener(this);
        Button button3 = (Button) this.mRootView.findViewById(R.id.fitness_btn_done);
        this.mDoneBtn = button3;
        button3.setOnClickListener(this);
        this.mIconLeftIV = (ImageView) this.mRootView.findViewById(R.id.fitness_icon_l);
        this.mIconRightIV = (ImageView) this.mRootView.findViewById(R.id.fitness_icon_r);
        final ImageView imageView = (ImageView) this.mRootView.findViewById(R.id.fitness_icon);
        if (imageView != null) {
            final Context context = imageView.getContext();
            if (HeadsetIDConstants.isTWS01GrayHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_icon_Gray);
            } else if (HeadsetIDConstants.isTWS01BlackHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_icon_Black);
            } else if (HeadsetIDConstants.isTWS01YellowHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_icon_Yellow);
            } else if (HeadsetIDConstants.isK73WhiteHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_k73_white);
            } else if (HeadsetIDConstants.isK73BlackHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_k73_black);
            } else if (HeadsetIDConstants.isK73GreenHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_k73_green);
            } else if (HeadsetIDConstants.isK76sHeadset(this.mDeviceId)) {
                handler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.3
                    @Override // java.lang.Runnable
                    public void run() {
                        Bitmap decodeImageResource = HeadsetIDConstants.decodeImageResource(context, R.drawable.headset_fitness_TWS01S);
                        if (decodeImageResource == null) {
                            Log.d("MiuiHeadsetFitnessFragment", "bitmap null");
                            return;
                        }
                        imageView.setImageDrawable(new BitmapDrawable(context.getResources(), decodeImageResource));
                    }
                }, 1L);
                imageView.setImageResource(R.drawable.headset_fitness_TWS01S);
            } else if (HeadsetIDConstants.isTWS200(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_TWS200);
            } else if (HeadsetIDConstants.isK73LBlueHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_k73_lblue);
            } else if (HeadsetIDConstants.isK73AWhiteHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_k73a_white_enc);
                imageView.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.4
                    @Override // java.lang.Runnable
                    public void run() {
                        Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey(context, R.drawable.headset_fitness_k73a_white_enc, "k73@GL_fitness_white");
                        if (decodeImageResourceByKey == null) {
                            Log.d("MiuiHeadsetFitnessFragment", "bitmap null");
                            return;
                        }
                        imageView.setImageDrawable(new BitmapDrawable(MiuiHeadsetFitnessFragment.this.getResources(), decodeImageResourceByKey));
                    }
                }, 1L);
            } else if (HeadsetIDConstants.isK73ABlackHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_k73a_black_enc);
                imageView.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.5
                    @Override // java.lang.Runnable
                    public void run() {
                        Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey(context, R.drawable.headset_fitness_k73a_black_enc, "k73@GL_fitness_black");
                        if (decodeImageResourceByKey == null) {
                            Log.d("MiuiHeadsetFitnessFragment", "bitmap null");
                            return;
                        }
                        imageView.setImageDrawable(new BitmapDrawable(MiuiHeadsetFitnessFragment.this.getResources(), decodeImageResourceByKey));
                    }
                }, 1L);
            } else if (HeadsetIDConstants.isK73AGreenHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_k73a_green_enc);
                imageView.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.6
                    @Override // java.lang.Runnable
                    public void run() {
                        Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey(context, R.drawable.headset_fitness_k73a_green_enc, "k73@GL_fitness_green");
                        if (decodeImageResourceByKey == null) {
                            Log.d("MiuiHeadsetFitnessFragment", "bitmap null");
                            return;
                        }
                        imageView.setImageDrawable(new BitmapDrawable(MiuiHeadsetFitnessFragment.this.getResources(), decodeImageResourceByKey));
                    }
                }, 1L);
            } else if (HeadsetIDConstants.isK75WhiteHeadset(this.mDeviceId) || HeadsetIDConstants.isK75AWhiteHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_k75_white_enc);
                imageView.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.7
                    @Override // java.lang.Runnable
                    public void run() {
                        Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey(context, R.drawable.headset_fitness_k75_white_enc, "k75_white");
                        if (decodeImageResourceByKey == null) {
                            Log.d("MiuiHeadsetFitnessFragment", "bitmap null");
                            return;
                        }
                        imageView.setImageDrawable(new BitmapDrawable(MiuiHeadsetFitnessFragment.this.getResources(), decodeImageResourceByKey));
                    }
                }, 1L);
            } else if (HeadsetIDConstants.isK75BlackHeadset(this.mDeviceId) || HeadsetIDConstants.isK75ABlackHeadset(this.mDeviceId)) {
                imageView.setImageResource(R.drawable.headset_fitness_k75_black_enc);
                imageView.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.8
                    @Override // java.lang.Runnable
                    public void run() {
                        Bitmap decodeImageResourceByKey = EncryptionUtil.decodeImageResourceByKey(context, R.drawable.headset_fitness_k75_black_enc, "k75_black");
                        if (decodeImageResourceByKey == null) {
                            Log.d("MiuiHeadsetFitnessFragment", "bitmap null");
                            return;
                        }
                        imageView.setImageDrawable(new BitmapDrawable(MiuiHeadsetFitnessFragment.this.getResources(), decodeImageResourceByKey));
                    }
                }, 1L);
            } else if (!"0201010001".equals(this.mDeviceId) && !"0201010000".equals(this.mDeviceId)) {
                this.mDeviceId.hashCode();
                int i = R.drawable.bt_headset_fitness_default;
                if (MiuiHeadsetAnimation.checkLocalCached(this.mDeviceId, context)) {
                    BitmapDrawable fitnessDeviceDrawable = MiuiHeadsetAnimation.getFitnessDeviceDrawable(this.mDeviceId, context);
                    if (fitnessDeviceDrawable != null) {
                        imageView.setImageDrawable(fitnessDeviceDrawable);
                    }
                } else {
                    imageView.setImageResource(i);
                }
            }
            updateIconMargin();
        }
        BluetoothDevice bluetoothDevice = this.mDevice;
        setPreferenceEnable(bluetoothDevice != null ? bluetoothDevice.isConnected() : false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSCOOn() {
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHfp;
        return bluetoothHeadset != null && bluetoothHeadset.isAudioConnected(this.mDevice);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playDetectingMusic() {
        SoundPool soundPool = this.mSoundPool;
        if (soundPool != null) {
            this.mStreamID = soundPool.play(1, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    private void releaseDetectingMusic() {
        SoundPool soundPool = this.mSoundPool;
        if (soundPool != null) {
            soundPool.stop(this.mStreamID);
            this.mSoundPool.release();
        }
    }

    private void saveDeviceInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("Headset_DeviceId", this.mDeviceId);
        setArguments(bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPreferenceEnable(boolean z) {
        Log.d("MiuiHeadsetFitnessFragment", "setPreferenceEnable " + z);
        Button button = this.mCheckBtn;
        if (button != null) {
            button.setEnabled(z);
        }
        Button button2 = this.mReCheckBtn;
        if (button2 != null) {
            button2.setEnabled(z);
        }
        Button button3 = this.mDoneBtn;
        if (button3 != null) {
            button3.setEnabled(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFitnessCheck() {
        try {
            this.mService.setCommonCommand(5, "01", this.mDevice);
        } catch (Exception e) {
            Log.e("MiuiHeadsetFitnessFragment", "startFitnessCheck failed: " + e);
            updateUIToCheck();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopDetectingMusic() {
        int i;
        SoundPool soundPool = this.mSoundPool;
        if (soundPool == null || (i = this.mStreamID) == 0) {
            return;
        }
        soundPool.stop(i);
        this.mStreamID = 0;
    }

    private void updateIconMargin() {
        View view;
        final ImageView imageView;
        String str = Build.PRODUCT;
        if (str == null || str.isEmpty() || (view = this.mRootView) == null || this.mHeadSetAct == null || (imageView = (ImageView) view.findViewById(R.id.fitness_icon)) == null || this.mSummaryTV == null || this.mIconLeftIV == null || this.mIconRightIV == null) {
            return;
        }
        imageView.setVisibility(4);
        this.mIconLeftIV.setVisibility(4);
        this.mIconRightIV.setVisibility(4);
        this.mSummaryTV.post(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.9
            @Override // java.lang.Runnable
            public void run() {
                int height = MiuiHeadsetFitnessFragment.this.mSummaryTV.getHeight();
                if (height >= 180) {
                    int dimensionPixelSize = MiuiHeadsetFitnessFragment.this.mHeadSetAct.getResources().getDimensionPixelSize(R.dimen.headset_fitness_icon_margin_top);
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
                    int i = dimensionPixelSize + height + 135;
                    if (marginLayoutParams.topMargin != i) {
                        marginLayoutParams.topMargin = i;
                        imageView.setLayoutParams(marginLayoutParams);
                    }
                }
                imageView.setVisibility(0);
                MiuiHeadsetFitnessFragment.this.mIconLeftIV.setVisibility(0);
                MiuiHeadsetFitnessFragment.this.mIconRightIV.setVisibility(0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUIForResult(final String str) {
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.13
            @Override // java.lang.Runnable
            public void run() {
                Log.d("MiuiHeadsetFitnessFragment", "updateUIForResult = " + str);
                String str2 = str;
                str2.hashCode();
                char c = 65535;
                switch (str2.hashCode()) {
                    case 1477632:
                        if (str2.equals("0000")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 1477633:
                        if (str2.equals("0001")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1477634:
                        if (str2.equals("0002")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1478593:
                        if (str2.equals("0100")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1478594:
                        if (str2.equals("0101")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1478595:
                        if (str2.equals("0102")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 1479554:
                        if (str2.equals("0200")) {
                            c = 6;
                            break;
                        }
                        break;
                    case 1479555:
                        if (str2.equals("0201")) {
                            c = 7;
                            break;
                        }
                        break;
                    case 1479556:
                        if (str2.equals("0202")) {
                            c = '\b';
                            break;
                        }
                        break;
                    case 1480518:
                        if (str2.equals("0303")) {
                            c = '\t';
                            break;
                        }
                        break;
                    case 1486290:
                        if (str2.equals("0909")) {
                            c = '\n';
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        Log.e("MiuiHeadsetFitnessFragment", "FITNESS_VALUE_UNKOWN!");
                        MiuiHeadsetFitnessFragment.this.updateUIToCheck();
                        return;
                    case 1:
                    case 7:
                        MiuiHeadsetFitnessFragment.this.mCheckBtn.setVisibility(4);
                        MiuiHeadsetFitnessFragment.this.mProgressLL.setVisibility(4);
                        MiuiHeadsetFitnessFragment.this.mRecheckLL.setVisibility(0);
                        MiuiHeadsetFitnessFragment.this.mResultLL.setVisibility(0);
                        MiuiHeadsetFitnessFragment.this.mSummaryTV.setText(R.string.miheadset_fitness_check_result_summary2);
                        MiuiHeadsetFitnessFragment.this.mResultLeftTV.setText(R.string.miheadset_fitness_check_result_l_not_ok);
                        MiuiHeadsetFitnessFragment.this.mResultLeftTV.setTextColor(MiuiHeadsetFitnessFragment.this.getActivity().getColor(R.color.headset_fitness_result_not_ok));
                        MiuiHeadsetFitnessFragment.this.mResultRightTV.setText(R.string.miheadset_fitness_check_result_ok);
                        MiuiHeadsetFitnessFragment.this.mResultRightTV.setTextColor(MiuiHeadsetFitnessFragment.this.getActivity().getColor(R.color.headset_fitness_result_ok));
                        MiuiHeadsetFitnessFragment.this.mIconLeftIV.setImageResource(R.drawable.headset_fitness_left_not_ok);
                        MiuiHeadsetFitnessFragment.this.mIconRightIV.setImageResource(R.drawable.headset_fitness_right_ok);
                        return;
                    case 2:
                    case 6:
                    case '\b':
                        MiuiHeadsetFitnessFragment.this.mCheckBtn.setVisibility(4);
                        MiuiHeadsetFitnessFragment.this.mProgressLL.setVisibility(4);
                        MiuiHeadsetFitnessFragment.this.mRecheckLL.setVisibility(0);
                        MiuiHeadsetFitnessFragment.this.mResultLL.setVisibility(0);
                        MiuiHeadsetFitnessFragment.this.mSummaryTV.setText(R.string.miheadset_fitness_check_result_summary2);
                        MiuiHeadsetFitnessFragment.this.mResultLeftTV.setText(R.string.miheadset_fitness_check_result_l_not_ok);
                        TextView textView = MiuiHeadsetFitnessFragment.this.mResultLeftTV;
                        FragmentActivity activity = MiuiHeadsetFitnessFragment.this.getActivity();
                        int i = R.color.headset_fitness_result_not_ok;
                        textView.setTextColor(activity.getColor(i));
                        MiuiHeadsetFitnessFragment.this.mResultRightTV.setText(R.string.miheadset_fitness_check_result_r_not_ok);
                        MiuiHeadsetFitnessFragment.this.mResultRightTV.setTextColor(MiuiHeadsetFitnessFragment.this.getActivity().getColor(i));
                        MiuiHeadsetFitnessFragment.this.mIconLeftIV.setImageResource(R.drawable.headset_fitness_left_not_ok);
                        MiuiHeadsetFitnessFragment.this.mIconRightIV.setImageResource(R.drawable.headset_fitness_right_not_ok);
                        return;
                    case 3:
                    case 5:
                        MiuiHeadsetFitnessFragment.this.mCheckBtn.setVisibility(4);
                        MiuiHeadsetFitnessFragment.this.mProgressLL.setVisibility(4);
                        MiuiHeadsetFitnessFragment.this.mRecheckLL.setVisibility(0);
                        MiuiHeadsetFitnessFragment.this.mResultLL.setVisibility(0);
                        MiuiHeadsetFitnessFragment.this.mSummaryTV.setText(R.string.miheadset_fitness_check_result_summary2);
                        MiuiHeadsetFitnessFragment.this.mResultLeftTV.setText(R.string.miheadset_fitness_check_result_ok);
                        MiuiHeadsetFitnessFragment.this.mResultLeftTV.setTextColor(MiuiHeadsetFitnessFragment.this.getActivity().getColor(R.color.headset_fitness_result_ok));
                        MiuiHeadsetFitnessFragment.this.mResultRightTV.setText(R.string.miheadset_fitness_check_result_r_not_ok);
                        MiuiHeadsetFitnessFragment.this.mResultRightTV.setTextColor(MiuiHeadsetFitnessFragment.this.getActivity().getColor(R.color.headset_fitness_result_not_ok));
                        MiuiHeadsetFitnessFragment.this.mIconLeftIV.setImageResource(R.drawable.headset_fitness_left_ok);
                        MiuiHeadsetFitnessFragment.this.mIconRightIV.setImageResource(R.drawable.headset_fitness_right_not_ok);
                        return;
                    case 4:
                        MiuiHeadsetFitnessFragment.this.mCheckBtn.setVisibility(4);
                        MiuiHeadsetFitnessFragment.this.mProgressLL.setVisibility(4);
                        MiuiHeadsetFitnessFragment.this.mRecheckLL.setVisibility(0);
                        MiuiHeadsetFitnessFragment.this.mResultLL.setVisibility(0);
                        MiuiHeadsetFitnessFragment.this.mSummaryTV.setText(R.string.miheadset_fitness_check_result_summary1);
                        TextView textView2 = MiuiHeadsetFitnessFragment.this.mResultLeftTV;
                        int i2 = R.string.miheadset_fitness_check_result_ok;
                        textView2.setText(i2);
                        TextView textView3 = MiuiHeadsetFitnessFragment.this.mResultLeftTV;
                        FragmentActivity activity2 = MiuiHeadsetFitnessFragment.this.getActivity();
                        int i3 = R.color.headset_fitness_result_ok;
                        textView3.setTextColor(activity2.getColor(i3));
                        MiuiHeadsetFitnessFragment.this.mResultRightTV.setText(i2);
                        MiuiHeadsetFitnessFragment.this.mResultRightTV.setTextColor(MiuiHeadsetFitnessFragment.this.getActivity().getColor(i3));
                        MiuiHeadsetFitnessFragment.this.mIconLeftIV.setImageResource(R.drawable.headset_fitness_left_ok);
                        MiuiHeadsetFitnessFragment.this.mIconRightIV.setImageResource(R.drawable.headset_fitness_right_ok);
                        return;
                    case '\t':
                        MiuiHeadsetFitnessFragment.this.checkIfNeedPauseMusic();
                        MiuiHeadsetFitnessFragment.this.playDetectingMusic();
                        return;
                    case '\n':
                        MiuiHeadsetFitnessFragment.this.mRecheckLL.setVisibility(4);
                        MiuiHeadsetFitnessFragment.this.mResultLL.setVisibility(4);
                        MiuiHeadsetFitnessFragment.this.mIconLeftIV.setImageResource(R.drawable.headset_fitness_left);
                        MiuiHeadsetFitnessFragment.this.mIconRightIV.setImageResource(R.drawable.headset_fitness_right);
                        MiuiHeadsetFitnessFragment.this.updateUIToCheck();
                        MiuiHeadsetFitnessFragment.this.createDialog(R.string.miheadset_fitness_check_result_dialog1);
                        return;
                    default:
                        Log.e("MiuiHeadsetFitnessFragment", "unkonw result !");
                        return;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUIToCheck() {
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.10
            @Override // java.lang.Runnable
            public void run() {
                MiuiHeadsetFitnessFragment.this.mProgressLL.setVisibility(4);
                MiuiHeadsetFitnessFragment.this.mCheckBtn.setVisibility(0);
                MiuiHeadsetFitnessFragment.this.mCheckBtn.setTag("check");
                MiuiHeadsetFitnessFragment.this.mCheckBtn.setText(R.string.miheadset_fitness_check_start);
                MiuiHeadsetFitnessFragment.this.mSummaryTV.setText(R.string.miheadset_fitness_check_summary2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUIToChecking() {
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.11
            @Override // java.lang.Runnable
            public void run() {
                MiuiHeadsetFitnessFragment.this.mResultLL.setVisibility(4);
                MiuiHeadsetFitnessFragment.this.mIconLeftIV.setImageResource(R.drawable.headset_fitness_left);
                MiuiHeadsetFitnessFragment.this.mIconRightIV.setImageResource(R.drawable.headset_fitness_right);
                MiuiHeadsetFitnessFragment.this.mCheckBtn.setVisibility(4);
                MiuiHeadsetFitnessFragment.this.mProgressLL.setVisibility(0);
                MiuiHeadsetFitnessFragment.this.mSummaryTV.setText(R.string.miheadset_fitness_check_summary3);
            }
        });
    }

    private void updateUIToCheckingForRestart() {
        getActivity().runOnUiThread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.12
            @Override // java.lang.Runnable
            public void run() {
                MiuiHeadsetFitnessFragment.this.mResultLL.setVisibility(4);
                MiuiHeadsetFitnessFragment.this.mRecheckLL.setVisibility(4);
                MiuiHeadsetFitnessFragment.this.mCheckBtn.setVisibility(4);
                MiuiHeadsetFitnessFragment.this.mProgressLL.setVisibility(0);
                MiuiHeadsetFitnessFragment.this.mSummaryTV.setText(R.string.miheadset_fitness_check_summary3);
                MiuiHeadsetFitnessFragment.this.mIconLeftIV.setImageResource(R.drawable.headset_fitness_left);
                MiuiHeadsetFitnessFragment.this.mIconRightIV.setImageResource(R.drawable.headset_fitness_right);
            }
        });
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiHeadsetFitnessFragment.class.getName();
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MiuiHeadsetActivity miuiHeadsetActivity = (MiuiHeadsetActivity) activity;
        this.mDevice = miuiHeadsetActivity.getDevice();
        this.mHeadSetAct = miuiHeadsetActivity;
        this.mService = miuiHeadsetActivity.getService();
        String deviceID = this.mHeadSetAct.getDeviceID();
        this.mDeviceId = deviceID;
        if (deviceID != null && !"".equals(deviceID)) {
            saveDeviceInfo();
        }
        this.mOrientation = this.mHeadSetAct.getResources().getConfiguration().orientation;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fitness_start) {
            String str = (String) this.mCheckBtn.getTag();
            if ("continue".equals(str)) {
                updateUIToCheck();
            } else if ("check".equals(str)) {
                this.mHandler.sendEmptyMessage(100);
            }
        } else if (id == R.id.fitness_btn_restart) {
            updateUIToCheckingForRestart();
            this.mHandler.sendEmptyMessage(100);
            this.mHandler.sendEmptyMessageDelayed(102, 10000L);
        } else if (id == R.id.fitness_btn_done) {
            back();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        String str;
        if (getArguments() != null && (str = this.mDeviceId) != null && "".equals(str)) {
            this.mDeviceId = getArguments().getString("Headset_DeviceId");
        }
        super.onCreate(bundle);
        this.mHandler = new InnerHandler(this);
        this.mAudioManager = (AudioManager) getSystemService("audio");
        if (this.mHeadSetAct != null) {
            new Thread(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFitnessFragment.2
                @Override // java.lang.Runnable
                public void run() {
                    MiuiHeadsetFitnessFragment.this.mSoundPool = new SoundPool(1, 3, 5);
                    MiuiHeadsetFitnessFragment.this.mSoundPool.load(MiuiHeadsetFitnessFragment.this.mHeadSetAct, R.raw.headset_fitness_detect, 1);
                }
            }).start();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mRootView = layoutInflater.inflate(R.layout.headset_fitness_layout, viewGroup, false);
        ActionBar appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setTitle(" ");
        }
        if (this.mRootView != null) {
            initWidget();
        }
        return this.mRootView;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mHandler.removeMessages(102);
        releaseDetectingMusic();
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
        getProfileProxy();
    }

    public void onServiceConnected() {
        Log.d("MiuiHeadsetFitnessFragment", "onServiceConnected()");
        this.mService = this.mHeadSetAct.getService();
        BluetoothDevice device = this.mHeadSetAct.getDevice();
        this.mDevice = device;
        if (device != null) {
            setPreferenceEnable(device.isConnected());
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(this.mBluetoothReceiver, new IntentFilter("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED"));
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (HeadsetIDConstants.isTWS01Headset(this.mDeviceId) || HeadsetIDConstants.isK73Headset(this.mDeviceId) || HeadsetIDConstants.isK75Headset(this.mDeviceId) || HeadsetIDConstants.isSupportZimiAdapter("common", this.mDeviceId)) {
            try {
                if (getActivity().getResources().getString(R.string.miheadset_fitness_check_summary3).equals(this.mSummaryTV.getText())) {
                    Log.d("MiuiHeadsetFitnessFragment", "Stop Checking K76/K73/K75, device:" + this.mDeviceId);
                    this.mService.setCommonCommand(5, "00", this.mDevice);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        closeProfileProxy();
        getActivity().unregisterReceiver(this.mBluetoothReceiver);
    }

    public void refreshStatus(String str, String str2) {
        Log.d("MiuiHeadsetFitnessFragment", "refreshStatus: " + str2);
        String[] split = str2.split("\\,", -1);
        if (split.length == 16 && (!TextUtils.isEmpty(split[15])) == true) {
            InnerHandler innerHandler = this.mHandler;
            innerHandler.sendMessage(innerHandler.obtainMessage(101, split[15]));
        }
    }
}
