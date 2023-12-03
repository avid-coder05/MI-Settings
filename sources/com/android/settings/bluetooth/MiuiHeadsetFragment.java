package com.android.settings.bluetooth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.bluetooth.ble.app.IMiuiHeadsetService;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.bluetooth.MiuiHeadsetAncAdjustView;
import com.android.settings.bluetooth.MiuiHeadsetTransparentAdjustView;
import com.android.settings.bluetooth.tws.MiuiHeadsetAnimation;
import com.android.settings.search.SearchUpdater;
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
import com.android.settingslib.util.ToastUtil;
import com.xiaomi.account.openauth.XMAuthericationException;
import com.xiaomi.account.openauth.XiaomiOAuthFuture;
import com.xiaomi.account.openauth.XiaomiOAuthResults;
import com.xiaomi.account.openauth.XiaomiOAuthorize;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import miui.provider.ExtraContacts;
import miui.provider.ExtraTelephony;
import miui.util.FeatureParser;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;
import miuix.preference.DropDownPreference;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class MiuiHeadsetFragment extends MiuiSettingsPreferenceFragment implements CachedBluetoothDevice.Callback, Preference.OnPreferenceChangeListener, MiuiHeadsetAncAdjustView.AncLevelChangeListener, MiuiHeadsetTransparentAdjustView.TransparentLevelChangeListener {
    private int BATTERY_CHARGE_FLAG;
    private int BATTERY_VALUE_FLAG;
    private int INVALID_BATTERY;
    private DropDownPreference configCodec;
    private CheckBoxPreference headTrackingCheckBox;
    private boolean isSupportHeadTrackAlgo;
    private AudioManager mAbsAudioManager;
    private String mAbsVolFeature;
    private XiaomiOAuthResults mAccountResult;
    private Activity mActivity;
    private String mAncCached;
    private String mAncLevelMap;
    private final Object mAncLock;
    private int mAncPendingStatus;
    private AudioManager mAudioManager;
    private Runnable mAudioShareCheckA2DPActiveExistRunnable;
    private PreferenceGroup mAudioShareContainer;
    private int mAudioStreamMax;
    private CheckBoxPreference mAutoAck;
    private String mBatteryCached;
    private int mBattery_box;
    private int mBattery_left;
    private int mBattery_right;
    private PreferenceGroup mBleAudioCategory;
    private BluetoothA2dp mBluetoothA2dp;
    private BroadcastReceiver mBluetoothA2dpReceiver;
    private BluetoothProfile.ServiceListener mBluetoothA2dpServiceListener;
    private BluetoothHeadset mBluetoothHfp;
    private BroadcastReceiver mBluetoothHfpAudioStateReceiver;
    private BluetoothProfile.ServiceListener mBluetoothHfpServiceListener;
    private BroadcastReceiver mBluetoothMultiA2DPStateResultReceiver;
    private CachedBluetoothDevice mCachedDevice;
    private Runnable mCodecConfigRun;
    private PreferenceGroup mCodecContainer;
    private Runnable mDelayOpenAudioShareRunnable;
    private DelayRunnable mDelayRunnable;
    private BluetoothDevice mDevice;
    private String mDeviceId;
    private String mDeviceMacAddress;
    private AlertDialog mDialog;
    private Runnable mDisableVolumeRun;
    private AlertDialog mDisconnectDialog;
    private Executor mExecutor;
    private String mFwVersion;
    private int mFwVersionCode;
    private Handler mHandler;
    private long[] mHits;
    private CheckBoxPreference mInearTest;
    boolean mInitedAtUi;
    private String mLastOnlineMessage;
    private String mLastOnlineUrl;
    private String mLastOnlineVerion;
    private int mLastOnlineVersionCode;
    private boolean mLocalExist;
    private final String mLocalFile;
    private LocalBluetoothManager mManager;
    private MiuiHeadsetAncAdjustView mMiuiHeadsetAncAdjustView;
    private MiuiHeadsetAncAdjustView mMiuiHeadsetAncAdjustViewWindNoise;
    MiuiHeadsetAnimation mMiuiHeadsetAnimation;
    private MiuiHeadsetTransparentAdjustView mMiuiHeadsetTransparentAdjustView;
    private CheckBoxPreference mMultiConnect;
    private CheckBoxPreference mNotifiDisplay;
    private Boolean mOtaIndicate;
    private String mPid;
    private final Preference.OnPreferenceChangeListener mPrefChangeListener;
    private MiuiHeadsetPreferenceConfig mPrefConfig;
    private PreferenceGroup mProfileContainer;
    private boolean mProfileGroupIsRemoved;
    private LocalBluetoothProfileManager mProfileManager;
    private TextView mRenameText;
    private View mRenameView;
    private View mRootView;
    private Runnable mRunnable;
    private IMiuiHeadsetService mService;
    private Boolean mShowAutoAck;
    private final SpatialSoundWrapper mSpatialSoundWrapper;
    private String mSupport;
    private Boolean mSupportAnc;
    private int mSupportAncWindVersionCode;
    private Boolean mSupportAntiLost;
    private Boolean mSupportAudioMode;
    private Boolean mSupportAutoAck;
    private Boolean mSupportCodecChange;
    private Boolean mSupportEqualizer;
    private Boolean mSupportGameMode;
    private Boolean mSupportGyr;
    private Boolean mSupportInear;
    private Boolean mSupportMultiConnect;
    private Boolean mSupportOta;
    private Boolean mSupportSignleEarMode;
    private Boolean mSupportWindNoise;
    private HandlerThread mThread;
    private String mToken;
    private String mVersion;
    private int mVersionCodeLocal;
    private String mVid;
    private String mWindNoiseAncLevel;
    private MessageHandler mWorkHandler;
    private PreferenceGroup spaceAudioPreferenceGroup;
    private AsyncTask waitResultTask;
    private static final String[] K71_CODEC_ENALBE_PRODUCTS = {"star", "mars", "venus", "haydn", "haydnin", "cetus", "cmi", "umi", "lmi", "lmipro", "lmiin", "apollo", "cas", "alioth", "picasso", "gauguin"};
    private static final String[] K77S_CODEC_ENABLE_PRODUCTS = {"haydn", "alioth", "star", "mars", "renoir", "venus", "cmi", "umi", "lmi", "cas", "gauguin", "odin", "phoenix", "apollo", "picasso", "lmipro", "gauguinpro", "haydnpro", "mona", "lisa", "vili", "cetus", "psyche"};
    private static final String[] K77S_GL_CODEC_ENABLE_PRODUCTS = {"venus", "renoir", "courbet", "alioth", "aliothin", "haydn", "haydnin", "chopin", "star", "mars", "ares", "aresin", "odin", "phoenix", "phoenixin", "cmi", "umi", "lmi", "lmipro", "lmiin", "lmiinpro", "cas", "gauguin", "gauguinpro", "gauguininpro", "apollo", "picasso", "cezanne", "sweet", "sweetin"};
    private static final String[] K73_CODEC_ENABLE_PRODUCTS = {"star", "mars", "venus", "haydn", "haydnin", "cetus", "cmi", "umi", "lmi", "lmipro", "lmiin", "apollo", "cas", "alioth", "picasso", "gauguin", "vili", "odin", "mona", "enuma", "lime", "lemon", "pomelo", "cezanne", "chopin", "ares", "renoir", "haydnpro", "gauguinpro", "camellia", "lisa", "begonia", "cannon", "lancelot", "merlin", "cetus", "psyche", "pissarro", "pissarropro", "pissarroin", "pissarroinpro", "evergo", "evergreen"};
    private static final String[] K73A_GL_CODEC_ENABLE_PRODUCTS = {"star", "mars", "venus", "vili", "haydn", "haydnin", "agate", "amber", "cmi", "umi", "apollo", "tucana", "toco", "psyche", "pissarro", "pissarropro", "pissarroin", "pissarroinpro", "evergo", "evergreen"};
    private static final String[] K73_HD_AUDIO_ENABLE_PRODUCTS = {"odin", "mona"};
    private static long APPID = 2882303761518263901L;
    private static String REDIRECTURL = "https://www.xiaomi.com";
    public static final String[] supportSetCodecDeviceId = {"0201010000", "0201010001", "01010605", "01010607", "01010703", "01010704", "01011004", "01010705", "01010707", "01011103"};
    private String mPendingAnc = "";
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
    private String mHDValue = "";
    private final HashMap<LocalBluetoothProfile, CheckBoxPreference> mAutoConnectPrefs = new HashMap<>();

    /* renamed from: com.android.settings.bluetooth.MiuiHeadsetFragment$48  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass48 implements Runnable {
        final /* synthetic */ MiuiHeadsetFragment this$0;
        final /* synthetic */ String val$callbackData;

        @Override // java.lang.Runnable
        public void run() {
            if (!LocalBluetoothProfileManager.isTbsProfileEnabled() || !this.this$0.mCachedDevice.isDualModeDevice() || TextUtils.isEmpty(this.val$callbackData) || this.val$callbackData.length() < 8) {
                return;
            }
            try {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) this.this$0.getPreferenceScreen().findPreference("le_audio_pre");
                String str = this.val$callbackData.split(",")[0];
                String str2 = this.val$callbackData.split(",")[1];
                if (checkBoxPreference != null && ((str.equals("255") && !str2.equals("255")) || (str2.equals("255") && !str.equals("255")))) {
                    checkBoxPreference.setEnabled(false);
                    this.this$0.isSingleHeadsetConn = true;
                    Log.d("MiuiHeadsetFragment", "leAudioPre.setEnabled(false) when power 01 or 10");
                } else if (checkBoxPreference == null || str2.equals("255") || str.equals("255") || str2.equals("") || str.equals("")) {
                } else {
                    this.this$0.isSingleHeadsetConn = false;
                    if (this.this$0.mLC3Switching || this.this$0.isSCOOn() || this.this$0.isLeAudioCgOn()) {
                        return;
                    }
                    if (this.this$0.isHfpConnected() || this.this$0.mCachedDevice.getLeAudioStatus() == 1) {
                        checkBoxPreference.setEnabled(true);
                        Log.d("MiuiHeadsetFragment", "leAudioPre.setEnabled(true) when power 11");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* loaded from: classes.dex */
    private static final class DelayRunnable implements Runnable {
        WeakReference<MiuiHeadsetFragment> mRef;

        DelayRunnable(MiuiHeadsetFragment miuiHeadsetFragment) {
            this.mRef = new WeakReference<>(miuiHeadsetFragment);
        }

        @Override // java.lang.Runnable
        public void run() {
            MiuiHeadsetFragment miuiHeadsetFragment = this.mRef.get();
            if (miuiHeadsetFragment != null) {
                miuiHeadsetFragment.updateCodecStatus();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class MessageHandler extends Handler {
        private MessageHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            try {
                int i = message.what;
                Log.d("MiuiHeadsetFragment", "handleMessage: what: " + i);
                if (i == 201) {
                    MiuiHeadsetFragment miuiHeadsetFragment = MiuiHeadsetFragment.this;
                    if (miuiHeadsetFragment.mMiuiHeadsetAnimation == null) {
                        miuiHeadsetFragment.mMiuiHeadsetAnimation = new MiuiHeadsetAnimation(miuiHeadsetFragment.mDeviceId, MiuiHeadsetFragment.this.getActivity(), MiuiHeadsetFragment.this.mRootView, MiuiHeadsetFragment.this.mHandler, MiuiHeadsetFragment.this.mService, MiuiHeadsetFragment.this.mWorkHandler);
                    }
                    MiuiHeadsetFragment.this.mMiuiHeadsetAnimation.checkAndDoCopy();
                    return;
                }
                switch (i) {
                    case 100:
                        if (!TextUtils.isEmpty(MiuiHeadsetFragment.this.mToken)) {
                            MiuiHeadsetFragment.this.mService.setCommonCommand(103, MiuiHeadsetFragment.this.mVid + "," + MiuiHeadsetFragment.this.mPid + "," + MiuiHeadsetFragment.this.mFwVersion + "," + MiuiHeadsetFragment.this.mToken, MiuiHeadsetFragment.this.mDevice);
                        } else if (MiuiHeadsetFragment.this.mAccountResult != null) {
                            MiuiHeadsetFragment.this.mService.setCommonCommand(103, MiuiHeadsetFragment.this.mVid + "," + MiuiHeadsetFragment.this.mPid + "," + MiuiHeadsetFragment.this.mFwVersion + "," + MiuiHeadsetFragment.this.mAccountResult.getAccessToken(), MiuiHeadsetFragment.this.mDevice);
                        }
                        if (message.arg1 == 1) {
                            MiuiHeadsetFragment.this.otaHandler();
                            return;
                        }
                        return;
                    case 101:
                        MiuiHeadsetFragment miuiHeadsetFragment2 = MiuiHeadsetFragment.this;
                        if (miuiHeadsetFragment2.mMiuiHeadsetAnimation == null) {
                            miuiHeadsetFragment2.mMiuiHeadsetAnimation = new MiuiHeadsetAnimation(miuiHeadsetFragment2.mDeviceId, MiuiHeadsetFragment.this.getActivity(), MiuiHeadsetFragment.this.mRootView, MiuiHeadsetFragment.this.mHandler, MiuiHeadsetFragment.this.mService, MiuiHeadsetFragment.this.mWorkHandler);
                        }
                        MiuiHeadsetFragment.this.mMiuiHeadsetAnimation.loadDefault();
                        return;
                    case 102:
                        removeMessages(102);
                        MiuiHeadsetFragment.this.updateAndEnableCode(true);
                        return;
                    case 103:
                        synchronized (MiuiHeadsetFragment.this.mAncLock) {
                            removeMessages(103);
                            if (TextUtils.isEmpty(MiuiHeadsetFragment.this.mPendingAnc)) {
                                MiuiHeadsetFragment.this.mAncPendingStatus = 0;
                            } else {
                                MiuiHeadsetFragment miuiHeadsetFragment3 = MiuiHeadsetFragment.this;
                                miuiHeadsetFragment3.deviceReportInfoAnc(miuiHeadsetFragment3.mPendingAnc);
                                MiuiHeadsetFragment.this.mAncPendingStatus = 1;
                                MiuiHeadsetFragment.this.mWorkHandler.sendMessageDelayed(MiuiHeadsetFragment.this.mWorkHandler.obtainMessage(103), 1500L);
                                MiuiHeadsetFragment.this.mPendingAnc = "";
                            }
                        }
                        return;
                    case 104:
                        MiuiHeadsetFragment.this.updateAtUiInfo(MiuiHeadsetFragment.this.mService.setCommonCommand(109, "", MiuiHeadsetFragment.this.mDevice));
                        MiuiHeadsetFragment.this.refreshGyrStatus();
                        return;
                    case 105:
                        if (MiuiHeadsetFragment.this.mService != null) {
                            MiuiHeadsetFragment.this.mService.setCommonCommand(122, "", MiuiHeadsetFragment.this.mDevice);
                            return;
                        }
                        return;
                    case 106:
                        if (MiuiHeadsetFragment.this.mService != null) {
                            Log.d("MiuiHeadsetFragment", "HD adudio status: " + MiuiHeadsetFragment.this.mHDValue);
                            MiuiHeadsetFragment.this.mService.setCommonCommand(121, MiuiHeadsetFragment.this.mHDValue, MiuiHeadsetFragment.this.mDevice);
                            return;
                        }
                        return;
                    case 107:
                        String str = "";
                        if (MiuiHeadsetFragment.this.mService != null && MiuiHeadsetFragment.this.mDevice != null) {
                            str = MiuiHeadsetFragment.this.mService.setCommonCommand(115, "", MiuiHeadsetFragment.this.mDevice);
                        }
                        MiuiHeadsetFragment.this.updateNotificationSwitchState(str);
                        return;
                    default:
                        return;
                }
            } catch (Exception e) {
                Log.e("MiuiHeadsetFragment", "error " + e);
            }
        }
    }

    public MiuiHeadsetFragment() {
        Boolean bool = Boolean.FALSE;
        this.mSupportOta = bool;
        this.mSupportAntiLost = bool;
        this.mSupportInear = bool;
        this.mSupportGameMode = bool;
        this.mSupportEqualizer = bool;
        this.mSupportAnc = bool;
        this.mOtaIndicate = bool;
        this.mSupportAudioMode = bool;
        this.mSupportSignleEarMode = bool;
        this.mSupportAutoAck = bool;
        this.mSupportMultiConnect = bool;
        this.mSupportGyr = bool;
        this.mSupportCodecChange = bool;
        this.mSupportWindNoise = bool;
        this.mShowAutoAck = Boolean.TRUE;
        this.mAncLevelMap = "";
        this.mBatteryCached = "";
        this.mAncCached = "";
        this.mWindNoiseAncLevel = "";
        this.mInitedAtUi = false;
        this.mHits = new long[3];
        this.mLocalExist = false;
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        String str = File.separator;
        sb.append(str);
        sb.append(Environment.DIRECTORY_DOWNLOADS);
        sb.append(str);
        sb.append("miuibluetooth");
        sb.append(str);
        sb.append("OTA.bin");
        this.mLocalFile = sb.toString();
        this.mMiuiHeadsetAnimation = null;
        this.mWorkHandler = null;
        this.mVid = "";
        this.mPid = "";
        this.mFwVersion = "";
        this.mLastOnlineVerion = "";
        this.mLastOnlineMessage = "";
        this.mLastOnlineUrl = "";
        this.mFwVersionCode = -1;
        this.mLastOnlineVersionCode = -1;
        this.mSupportAncWindVersionCode = 30259;
        this.mVersionCodeLocal = 0;
        this.mAccountResult = null;
        this.mToken = null;
        this.mExecutor = Executors.newCachedThreadPool();
        this.mService = null;
        this.INVALID_BATTERY = 255;
        this.BATTERY_CHARGE_FLAG = 128;
        this.BATTERY_VALUE_FLAG = 127;
        this.mBattery_left = 0;
        this.mBattery_right = 0;
        this.mBattery_box = 0;
        this.mVersion = "";
        this.mAncPendingStatus = 0;
        this.isSupportHeadTrackAlgo = false;
        this.mSpatialSoundWrapper = new SpatialSoundWrapper();
        this.mBluetoothA2dpServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.5
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                Log.d("MiuiHeadsetFragment", "onA2dpServiceConnected()");
                synchronized (MiuiHeadsetFragment.this.mBluetoothA2dpLock) {
                    MiuiHeadsetFragment.this.mBluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
                    if (FeatureParser.getBoolean("support_audio_share", false) && (((MiuiHeadsetFragment.this.mBluetoothA2dp != null && MiuiHeadsetFragment.this.mBluetoothA2dp.getActiveDevice() == null) || !MiuiHeadsetFragment.this.mCachedDevice.isConnectedA2dpDevice() || MiuiHeadsetFragment.this.mCachedDevice.isActiveDevice(2)) && MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("audio_share_container") != null)) {
                        MiuiHeadsetFragment.this.getPreferenceScreen().removePreference(MiuiHeadsetFragment.this.mAudioShareContainer);
                    }
                    if (MiuiHeadsetFragment.this.mUpdatePrefForA2DPConnected) {
                        MiuiHeadsetFragment.this.mUpdatePrefForA2DPConnected = false;
                        MiuiHeadsetFragment.this.updateCodecStatus();
                    }
                    MiuiHeadsetFragment.this.setDeviceAACWhiteListConfig(true);
                    MiuiHeadsetFragment.this.updateAndEnableCode(true);
                }
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                Log.d("MiuiHeadsetFragment", "onA2dpServiceDisconnected()");
                synchronized (MiuiHeadsetFragment.this.mBluetoothA2dpLock) {
                    MiuiHeadsetFragment.this.closeProfileProxy(1);
                }
            }
        };
        this.mBluetoothHfpServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.6
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                Log.d("MiuiHeadsetFragment", "onHfpServiceConnected()");
                synchronized (MiuiHeadsetFragment.this.mBluetoothHfpLock) {
                    MiuiHeadsetFragment.this.mBluetoothHfp = (BluetoothHeadset) bluetoothProfile;
                    if (FeatureParser.getBoolean("support_audio_share", false) && MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("audio_share_container") != null && MiuiHeadsetFragment.this.mBluetoothHfp.isAudioOn()) {
                        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("audio_share_switch_pre");
                        BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("audio_share_volume_pre");
                        if (checkBoxPreference != null) {
                            checkBoxPreference.setEnabled(false);
                            Log.d("MiuiHeadsetFragment", "mBluetoothHfp.isAudioOn() == on, prefAudioShareSwitch.setDisabled");
                        }
                        if (bluetoothVolumeSeekBarPreference != null) {
                            bluetoothVolumeSeekBarPreference.setEnabled(false);
                        }
                    }
                    if (MiuiHeadsetFragment.this.mBluetoothHfp.getConnectionState(MiuiHeadsetFragment.this.mDevice) == 2) {
                        if (MiuiHeadsetFragment.this.mRenameView != null) {
                            MiuiHeadsetFragment.this.mRenameView.setEnabled(true);
                            MiuiHeadsetFragment.this.mRenameText.setTextColor(MiuiHeadsetFragment.this.mActivity.getResources().getColor(R.color.first_text_color));
                        }
                        if (MiuiHeadsetFragment.this.mSupportGyr.booleanValue() && MiuiHeadsetFragment.this.headTrackingCheckBox != null && MiuiHeadsetFragment.this.isSupportHeadTrackAlgo) {
                            MiuiHeadsetFragment.this.headTrackingCheckBox.setEnabled(true);
                        }
                        if (MiuiHeadsetFragment.this.mSupportInear.booleanValue() && MiuiHeadsetFragment.this.mInearTest != null) {
                            MiuiHeadsetFragment.this.mInearTest.setEnabled(true);
                        }
                        if (MiuiHeadsetFragment.this.mShowAutoAck.booleanValue() && MiuiHeadsetFragment.this.mAutoAck != null) {
                            MiuiHeadsetFragment.this.mAutoAck.setEnabled(true);
                        }
                    } else {
                        if (MiuiHeadsetFragment.this.mRenameView != null) {
                            MiuiHeadsetFragment.this.mRenameView.setEnabled(false);
                            MiuiHeadsetFragment.this.mRenameText.setTextColor(MiuiHeadsetFragment.this.mActivity.getResources().getColor(R.color.second_text_color));
                        }
                        if (MiuiHeadsetFragment.this.mSupportGyr.booleanValue() && MiuiHeadsetFragment.this.headTrackingCheckBox != null && MiuiHeadsetFragment.this.isSupportHeadTrackAlgo) {
                            MiuiHeadsetFragment.this.headTrackingCheckBox.setEnabled(false);
                        }
                        if (MiuiHeadsetFragment.this.mSupportInear.booleanValue() && MiuiHeadsetFragment.this.mInearTest != null) {
                            MiuiHeadsetFragment.this.mInearTest.setEnabled(false);
                        }
                        if (MiuiHeadsetFragment.this.mShowAutoAck.booleanValue() && MiuiHeadsetFragment.this.mAutoAck != null) {
                            MiuiHeadsetFragment.this.mAutoAck.setEnabled(false);
                        }
                    }
                    MiuiHeadsetFragment.this.updateHeadTrackEnable();
                    if (MiuiHeadsetFragment.this.mService != null) {
                        try {
                            MiuiHeadsetFragment.this.mService.connect(MiuiHeadsetFragment.this.mDevice);
                            MiuiHeadsetFragment.this.mWorkHandler.sendMessage(MiuiHeadsetFragment.this.mWorkHandler.obtainMessage(104));
                        } catch (Exception unused) {
                            Log.e("MiuiHeadsetFragment", "connect the device mma");
                        }
                    }
                }
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                Log.d("MiuiHeadsetFragment", "onHfpServiceDisconnected()");
                synchronized (MiuiHeadsetFragment.this.mBluetoothHfpLock) {
                    MiuiHeadsetFragment.this.closeProfileProxy(2);
                }
            }
        };
        this.mBluetoothA2dpReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Log.d("MiuiHeadsetFragment", "LDAC: mBluetoothA2dpReceiver.onReceive intent=" + intent);
                String action = intent.getAction();
                if ("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED".equals(action)) {
                    Log.d("MiuiHeadsetFragment", "Received BluetoothCodecStatus=" + ((BluetoothCodecStatus) intent.getParcelableExtra("android.bluetooth.extra.CODEC_STATUS")));
                    if (MiuiHeadsetFragment.this.mHandler == null) {
                        MiuiHeadsetFragment.this.mHandler = new Handler();
                    }
                    if (MiuiHeadsetFragment.this.mDelayRunnable == null) {
                        MiuiHeadsetFragment miuiHeadsetFragment = MiuiHeadsetFragment.this;
                        miuiHeadsetFragment.mDelayRunnable = new DelayRunnable(miuiHeadsetFragment);
                    }
                    MiuiHeadsetFragment.this.setDeviceAACWhiteListConfig(true);
                    MiuiHeadsetFragment.this.mHandler.removeCallbacks(MiuiHeadsetFragment.this.mDelayRunnable);
                    MiuiHeadsetFragment.this.mHandler.postDelayed(MiuiHeadsetFragment.this.mDelayRunnable, 1500L);
                    MiuiHeadsetFragment.this.updateAndEnableCode(false);
                } else if ("android.bluetooth.a2dp.profile.action.PLAYING_STATE_CHANGED".equals(action)) {
                    int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 11);
                    Log.d("MiuiHeadsetFragment", " updateA2DPPlayingState transition: " + intent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 11) + "->" + intExtra);
                    Settings.Secure.getString(context.getContentResolver(), "miui_store_audio_share_device_address");
                    CheckBoxPreference checkBoxPreference = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("le_audio_pre");
                    if (MiuiHeadsetFragment.this.mCachedDevice.isActiveDevice(2)) {
                        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("abs_volume_pre");
                        if ((checkBoxPreference == null || !(MiuiHeadsetFragment.this.mCachedDevice.getLeAudioStatus() == 1 || MiuiHeadsetFragment.this.mCachedDevice.getSpecificCodecStatus("LEAUDIO") == 1)) && checkBoxPreference2 != null) {
                            if (intExtra == 11) {
                                checkBoxPreference2.setEnabled(true);
                            } else if (intExtra == 10) {
                                checkBoxPreference2.setEnabled(false);
                            }
                        }
                    }
                } else if (FeatureParser.getBoolean("support_audio_share", false) && "MultiA2dp.ACTION.VOLUME_CHANGED".equals(action)) {
                    int intExtra2 = intent.getIntExtra("MultiA2dp.EXTRA.VOLUME_VALUE", 50);
                    Log.d("MiuiHeadsetFragment", "ACTION_MULTIA2DP_VOLUME_CHANGED received value is: " + intExtra2);
                    MiuiHeadsetFragment.this.setAudioShareVolume(intExtra2);
                } else if (!"android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(action)) {
                    if ("android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED".equals(action)) {
                        MiuiHeadsetFragment.this.updateAndEnableCode(true);
                        MiuiHeadsetFragment.this.updateHeadTrackEnable();
                    }
                } else {
                    MiuiHeadsetFragment.this.updateAndEnableCode(true);
                    if (MiuiHeadsetFragment.this.mBluetoothHfp == null && MiuiHeadsetFragment.this.mBluetoothA2dp == null) {
                        Log.d("MiuiHeadsetFragment", "getProfileProxy.");
                        MiuiHeadsetFragment.this.getProfileProxy();
                    }
                    int intExtra3 = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    if (intExtra3 == 0 && bluetoothDevice != null && bluetoothDevice.equals(MiuiHeadsetFragment.this.mDevice)) {
                        MiuiHeadsetFragment.this.refreshInDisconnect();
                        if (MiuiHeadsetFragment.this.mRenameView != null) {
                            MiuiHeadsetFragment.this.mRenameView.setEnabled(false);
                            MiuiHeadsetFragment.this.mRenameText.setTextColor(MiuiHeadsetFragment.this.mActivity.getResources().getColor(R.color.second_text_color));
                        }
                    } else if (intExtra3 == 2 && bluetoothDevice != null && bluetoothDevice.equals(MiuiHeadsetFragment.this.mDevice)) {
                        if (MiuiHeadsetFragment.this.mRenameView != null) {
                            MiuiHeadsetFragment.this.mRenameView.setEnabled(true);
                            MiuiHeadsetFragment.this.mRenameText.setTextColor(MiuiHeadsetFragment.this.mActivity.getResources().getColor(R.color.first_text_color));
                        }
                        if (MiuiHeadsetFragment.this.mSupportGyr.booleanValue() && MiuiHeadsetFragment.this.headTrackingCheckBox != null && MiuiHeadsetFragment.this.isSupportHeadTrackAlgo) {
                            MiuiHeadsetFragment.this.headTrackingCheckBox.setEnabled(true);
                        }
                        MiuiHeadsetFragment.this.updateHeadTrackEnable();
                        if (MiuiHeadsetFragment.this.mSupportInear.booleanValue() && MiuiHeadsetFragment.this.mInearTest != null) {
                            MiuiHeadsetFragment.this.mInearTest.setEnabled(true);
                        }
                        if (!MiuiHeadsetFragment.this.mShowAutoAck.booleanValue() || MiuiHeadsetFragment.this.mAutoAck == null) {
                            return;
                        }
                        MiuiHeadsetFragment.this.mAutoAck.setEnabled(true);
                    }
                }
            }
        };
        this.mBluetoothMultiA2DPStateResultReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.8
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Log.d("MiuiHeadsetFragment", "mBluetoothMultiA2DPStateResultReceiver.Receive intent=" + intent);
                String action = intent.getAction();
                if (action == null) {
                    Log.e("MiuiHeadsetFragment", "Received intent with null action");
                } else if (action == "MultiA2dp.ACTION.RESET_STATE_CHANGED") {
                    Log.d("MiuiHeadsetFragment", "action == ACTION_MULTIA2DP_STATE_RESULT_CHANGED");
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    int intExtra = intent.getIntExtra("MultiA2dp.EXTRA.STATE", -1);
                    if (bluetoothDevice.getAddress().equals(MiuiHeadsetFragment.this.mCachedDevice.getAddress())) {
                        MiuiHeadsetFragment.this.handleMultiA2DPState(intExtra);
                        return;
                    }
                    CheckBoxPreference checkBoxPreference = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("audio_share_switch_pre");
                    if (intExtra != 1 || checkBoxPreference == null) {
                        return;
                    }
                    checkBoxPreference.setEnabled(true);
                } else if (action == "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED" || action == "android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED") {
                    if (FeatureParser.getBoolean("support_audio_share", false) && (((MiuiHeadsetFragment.this.mBluetoothA2dp != null && MiuiHeadsetFragment.this.mBluetoothA2dp.getActiveDevice() == null) || !MiuiHeadsetFragment.this.mCachedDevice.isConnectedA2dpDevice() || MiuiHeadsetFragment.this.mCachedDevice.isActiveDevice(2)) && MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("audio_share_container") != null)) {
                        MiuiHeadsetFragment.this.getPreferenceScreen().removePreference(MiuiHeadsetFragment.this.mAudioShareContainer);
                        Log.d("MiuiHeadsetFragment", "getActiveDevice() == null,remove audio share container");
                    }
                    MiuiHeadsetFragment.this.updateCodecIndex();
                }
            }
        };
        this.mBluetoothHfpAudioStateReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.9
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                CheckBoxPreference checkBoxPreference;
                if (intent.getAction() == null) {
                    Log.e("MiuiHeadsetFragment", "Received mBluetoothHfpAudioStateReceiver intent with null action");
                    return;
                }
                int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
                Log.d("MiuiHeadsetFragment", "mBluetoothHfpAudioStateReceiver BluetoothProfile.EXTRA_STATE =" + intExtra);
                if (intExtra == 12) {
                    if (LocalBluetoothProfileManager.isTbsProfileEnabled() && MiuiHeadsetFragment.this.mCachedDevice.isDualModeDevice() && (checkBoxPreference = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("le_audio_pre")) != null) {
                        checkBoxPreference.setEnabled(false);
                        Log.d("MiuiHeadsetFragment", "leAudioPre.setEnabled(false) when STATE_AUDIO_CONNECTED");
                    }
                } else if (intExtra == 10) {
                    if (LocalBluetoothProfileManager.isTbsProfileEnabled() && MiuiHeadsetFragment.this.mCachedDevice.isDualModeDevice()) {
                        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("le_audio_pre");
                        if (checkBoxPreference2 == null || MiuiHeadsetFragment.this.mLC3Switching) {
                            return;
                        }
                        checkBoxPreference2.setEnabled(true);
                        Log.d("MiuiHeadsetFragment", "leAudioPre.setEnabled(true) when STATE_AUDIO_DISCONNECTED");
                        return;
                    }
                    CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("audio_share_switch_pre");
                    BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("audio_share_volume_pre");
                    if (checkBoxPreference3 != null) {
                        checkBoxPreference3.setEnabled(true);
                        Log.d("MiuiHeadsetFragment", "BluetoothHeadset.STATE_AUDIO_DISCONNECTED, prefAudioShareSwitch.setEnabled");
                    }
                    if (bluetoothVolumeSeekBarPreference != null) {
                        bluetoothVolumeSeekBarPreference.setEnabled(true);
                    }
                }
            }
        };
        this.mPrefChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.10
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                if (MiuiHeadsetFragment.this.mBluetoothA2dp.getConnectionState(MiuiHeadsetFragment.this.mCachedDevice.getDevice()) == 2 || (LocalBluetoothProfileManager.isTbsProfileEnabled() && MiuiHeadsetFragment.this.mCachedDevice.isDualModeDevice() && MiuiHeadsetFragment.this.mCachedDevice.isConnected())) {
                    CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                    if (((Boolean) obj).booleanValue()) {
                        checkBoxPreference.setChecked(true);
                        MiuiHeadsetFragment.this.mCachedDevice.setSpecificCodecStatus("latency_val", 1);
                    } else {
                        checkBoxPreference.setChecked(false);
                        MiuiHeadsetFragment.this.mCachedDevice.setSpecificCodecStatus("latency_val", 0);
                    }
                    MiuiHeadsetFragment.this.handleCheckBoxPreferenceEnabled(checkBoxPreference);
                }
                return false;
            }
        };
        this.mRunnable = new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.12
            @Override // java.lang.Runnable
            public void run() {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("ldac_pre");
                CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("latency_pre");
                CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("le_audio_pre");
                if (checkBoxPreference != null) {
                    checkBoxPreference.setEnabled(true);
                }
                if (checkBoxPreference3 != null && !LocalBluetoothProfileManager.isTbsProfileEnabled()) {
                    checkBoxPreference3.setEnabled(true);
                }
                if (checkBoxPreference2 != null) {
                    if (MiuiHeadsetFragment.this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") == 0 || MiuiHeadsetFragment.this.mCachedDevice.getSpecificCodecStatus("LHDC_V2") == 0 || MiuiHeadsetFragment.this.mCachedDevice.getSpecificCodecStatus("LHDC_V1") == 0) {
                        checkBoxPreference2.setEnabled(false);
                    } else {
                        checkBoxPreference2.setEnabled(true);
                    }
                }
            }
        };
        this.mCodecConfigRun = new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.15
            @Override // java.lang.Runnable
            public void run() {
                if (MiuiHeadsetFragment.this.mCachedDevice != null) {
                    MiuiHeadsetFragment.this.mCachedDevice.connect(true);
                }
            }
        };
        this.mDisableVolumeRun = new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.18
            @Override // java.lang.Runnable
            public void run() {
                MiuiHeadsetFragment.this.sendBroadcastEnableOrDisable(false);
            }
        };
        this.mDelayOpenAudioShareRunnable = new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.19
            @Override // java.lang.Runnable
            public void run() {
                MiuiHeadsetFragment.this.onAudioShareSwitchPrefClicked((CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("audio_share_switch_pre"));
            }
        };
        this.mAudioShareCheckA2DPActiveExistRunnable = new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.20
            @Override // java.lang.Runnable
            public void run() {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("audio_share_switch_pre");
                if (MiuiHeadsetFragment.this.mBluetoothA2dp == null) {
                    Log.d("MiuiHeadsetFragment", "mBluetoothA2dp == null");
                } else if (MiuiHeadsetFragment.this.mBluetoothA2dp.getActiveDevice() != null) {
                    Log.d("MiuiHeadsetFragment", "mBluetoothA2dp.getActiveDevice() != null");
                } else {
                    if (checkBoxPreference != null) {
                        checkBoxPreference.setEnabled(false);
                        checkBoxPreference.setVisible(false);
                        Log.d("MiuiHeadsetFragment", "getActiveDevice() == null,disable checkbox");
                    }
                    if (MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("audio_share_container") != null) {
                        MiuiHeadsetFragment.this.getPreferenceScreen().removePreference(MiuiHeadsetFragment.this.mAudioShareContainer);
                        Log.d("MiuiHeadsetFragment", "getActiveDevice() == null,remove audio share container");
                    }
                }
            }
        };
        this.mAncLock = new Object();
    }

    private void addAudioShareConfigPreference() {
        this.mAudioShareContainer.addPreference(createAudioShareConfigPreference());
        Log.d("MiuiHeadsetFragment", "mAudioShareContainer.addPreference");
    }

    private void addHdAudio() {
        try {
            if (k73HDAudioEable(this.mDeviceId)) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("ldac_container");
                Preference findPreference = findPreference("hd_audio");
                if (findPreference == null) {
                    findPreference = createHDAudioPreference();
                }
                if (preferenceGroup != null) {
                    preferenceGroup.addPreference(findPreference);
                }
            }
        } catch (Exception e) {
            Log.d("MiuiHeadsetFragment", "error: " + e);
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
        Log.d("MiuiHeadsetFragment", "mCachedDevice.isConnectedA2dpDevice() = " + this.mCachedDevice.isConnectedA2dpDevice());
        Log.d("MiuiHeadsetFragment", "mCachedDevice.isActiveDevice = " + this.mCachedDevice.isActiveDevice(2));
        if (!this.mCachedDevice.isConnectedA2dpDevice() || this.mCachedDevice.isActiveDevice(2)) {
            if (getPreferenceScreen().findPreference("audio_share_container") != null) {
                getPreferenceScreen().removePreference(this.mAudioShareContainer);
                return;
            }
            return;
        }
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
        checkBoxPreference.setKey("audio_share_switch_pre");
        checkBoxPreference.setTitle(R.string.bt_audio_share_switch_title);
        checkBoxPreference.setSummary(R.string.bt_audio_share_switch_summary);
        boolean z = false;
        checkBoxPreference.setPersistent(false);
        checkBoxPreference.setOnPreferenceChangeListener(this);
        Log.d("MiuiHeadsetFragment", "temp = " + this.mCachedDevice.getSpecificCodecStatus("AUDIO_SHARE_SWITCH"));
        FragmentActivity activity = getActivity();
        String string = Settings.Secure.getString(activity.getContentResolver(), "miui_store_audio_share_device_address");
        Log.d("MiuiHeadsetFragment", "KEY_STORE_AUDIO_SHARE_DEVICE = " + string);
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
        this.mAudioShareContainer.addPreference(checkBoxPreference);
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
                    Log.d("MiuiHeadsetFragment", "Device support ble audio !");
                    if (SystemProperties.getBoolean("persist.vendor.service.bt.lea_test", false)) {
                        if (this.mBleAudioCategory != null) {
                            Log.d("MiuiHeadsetFragment", "mBleAudioCategory not null add to show !");
                            createProfilePreference.setOrder(1);
                            this.mBleAudioCategory.addPreference(createProfilePreference);
                            getPreferenceScreen().addPreference(this.mBleAudioCategory);
                        } else {
                            Log.d("MiuiHeadsetFragment", "mBleAudioCategory is null do nothing and return!");
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

    /* JADX WARN: Removed duplicated region for block: B:32:0x0091  */
    /* JADX WARN: Removed duplicated region for block: B:49:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void addPreferencesForSpecialCodec() {
        /*
            r5 = this;
            boolean r0 = r5.mLHDCV3Device
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L16
            java.lang.String r0 = "LHDC_V3"
            androidx.preference.CheckBoxPreference r0 = r5.createSpecialCodecPreference(r0)
            androidx.preference.PreferenceGroup r3 = r5.mCodecContainer
            r3.addPreference(r0)
            r5.addLatencyCodecPreference()
            goto L8e
        L16:
            boolean r0 = r5.mLHDCV2Device
            if (r0 == 0) goto L29
            java.lang.String r0 = "LHDC_V2"
            androidx.preference.CheckBoxPreference r0 = r5.createSpecialCodecPreference(r0)
            androidx.preference.PreferenceGroup r3 = r5.mCodecContainer
            r3.addPreference(r0)
            r5.addLatencyCodecPreference()
            goto L8e
        L29:
            boolean r0 = r5.mLHDCV1Device
            if (r0 == 0) goto L3c
            java.lang.String r0 = "LHDC_V1"
            androidx.preference.CheckBoxPreference r0 = r5.createSpecialCodecPreference(r0)
            androidx.preference.PreferenceGroup r3 = r5.mCodecContainer
            r3.addPreference(r0)
            r5.addLatencyCodecPreference()
            goto L8e
        L3c:
            boolean r0 = r5.mLDACDevice
            if (r0 == 0) goto L4c
            java.lang.String r0 = "LDAC"
            androidx.preference.CheckBoxPreference r0 = r5.createSpecialCodecPreference(r0)
            androidx.preference.PreferenceGroup r3 = r5.mCodecContainer
            r3.addPreference(r0)
            goto L8e
        L4c:
            boolean r0 = r5.mAADevice
            if (r0 == 0) goto L54
            r5.addLatencyCodecPreference()
            goto L8e
        L54:
            boolean r0 = r5.mAACDevice
            if (r0 == 0) goto L67
            java.lang.String r0 = "AAC"
            androidx.preference.CheckBoxPreference r0 = r5.createSpecialCodecPreference(r0)
            androidx.preference.PreferenceGroup r3 = r5.mCodecContainer
            r3.addPreference(r0)
            r5.addLatencyCodecPreference()
            goto L8e
        L67:
            boolean r0 = r5.mSBCLlDevice
            if (r0 == 0) goto L6f
            r5.addLatencyCodecPreference()
            goto L8e
        L6f:
            androidx.preference.PreferenceScreen r0 = r5.getPreferenceScreen()
            java.lang.String r3 = "ldac_container"
            androidx.preference.Preference r0 = r0.findPreference(r3)
            if (r0 == 0) goto L8e
            boolean r0 = r5.mIsInAbsWhitelist
            if (r0 != 0) goto L8e
            boolean r0 = r5.mIsBleAudioDevice
            if (r0 != 0) goto L8e
            androidx.preference.PreferenceScreen r0 = r5.getPreferenceScreen()
            androidx.preference.PreferenceGroup r3 = r5.mCodecContainer
            r0.removePreference(r3)
            r0 = r1
            goto L8f
        L8e:
            r0 = r2
        L8f:
            if (r0 != 0) goto Lf0
            androidx.fragment.app.FragmentActivity r0 = r5.getActivity()
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r3 = "miui_store_audio_share_device_address"
            java.lang.String r0 = android.provider.Settings.Secure.getString(r0, r3)
            java.lang.String r3 = "support_audio_share"
            boolean r3 = miui.util.FeatureParser.getBoolean(r3, r2)
            if (r3 == 0) goto Lf0
            if (r0 == 0) goto Lf0
            boolean r3 = r0.isEmpty()
            if (r3 != 0) goto Lf0
            com.android.settingslib.bluetooth.CachedBluetoothDevice r3 = r5.mCachedDevice
            r4 = 2
            boolean r3 = r3.isActiveDevice(r4)
            if (r3 == r1) goto Lce
            com.android.settingslib.bluetooth.CachedBluetoothDevice r1 = r5.mCachedDevice
            java.lang.String r1 = r1.getAddress()
            boolean r1 = r1.equals(r0)
            if (r1 != 0) goto Lce
            java.lang.String r1 = "pending"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto Lf0
        Lce:
            androidx.preference.PreferenceScreen r0 = r5.getPreferenceScreen()
            java.lang.String r1 = "ldac_pre"
            androidx.preference.Preference r0 = r0.findPreference(r1)
            androidx.preference.CheckBoxPreference r0 = (androidx.preference.CheckBoxPreference) r0
            androidx.preference.PreferenceScreen r5 = r5.getPreferenceScreen()
            java.lang.String r1 = "latency_pre"
            androidx.preference.Preference r5 = r5.findPreference(r1)
            androidx.preference.CheckBoxPreference r5 = (androidx.preference.CheckBoxPreference) r5
            if (r0 == 0) goto Leb
            r0.setEnabled(r2)
        Leb:
            if (r5 == 0) goto Lf0
            r5.setEnabled(r2)
        Lf0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.MiuiHeadsetFragment.addPreferencesForSpecialCodec():void");
    }

    private void addToWhiteList(String str) {
        String str2 = SystemProperties.get(str, "");
        Log.d("MiuiHeadsetFragment", "addToWhiteList(): whitelist before add is " + str2 + ", current dev is " + this.mCachedDevice.getAddress().toLowerCase() + ", prop is " + str);
        if (str2.length() >= 90) {
            str2 = str2.substring(18);
        }
        if (str2.indexOf(this.mCachedDevice.getAddress().toLowerCase()) >= 0) {
            Log.d("MiuiHeadsetFragment", "addToWhiteList(): the device has already in whitelist,do nothing");
            return;
        }
        StringBuilder sb = new StringBuilder(str2);
        sb.append(this.mCachedDevice.getAddress().toLowerCase());
        sb.append(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        Log.d("MiuiHeadsetFragment", "addToWhiteList(): whitelist after add is " + sb.toString());
        SystemProperties.set(str, sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void askDisconnect() {
        Activity activity = this.mActivity;
        Utils.showDisconnectDialog(activity, null, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.42
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (MiuiHeadsetFragment.this.mDevice != null) {
                    MiuiHeadsetFragment.this.mCachedDevice.disconnect();
                }
                if (MiuiHeadsetFragment.this.mService != null) {
                    try {
                        MiuiHeadsetFragment.this.mService.disconnect(MiuiHeadsetFragment.this.mCachedDevice.getDevice());
                        MiuiHeadsetFragment.this.finish();
                    } catch (Exception e) {
                        Log.e("MiuiHeadsetFragment", "error " + e);
                    }
                }
            }
        }, this.mCachedDevice.getName(), Html.fromHtml(activity.getString(R.string.miheadset_disconnect_device)));
    }

    private void askDisconnect(Context context, final LocalBluetoothProfile localBluetoothProfile) {
        final CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        String name = cachedBluetoothDevice.getName();
        if (TextUtils.isEmpty(name)) {
            name = context.getString(R.string.bluetooth_device);
        }
        String string = context.getString(localBluetoothProfile.getNameResource(cachedBluetoothDevice.getDevice()));
        AlertDialog showDisconnectDialog = Utils.showDisconnectDialog(context, this.mDisconnectDialog, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                cachedBluetoothDevice.disconnect(localBluetoothProfile);
                if (localBluetoothProfile instanceof MapProfile) {
                    cachedBluetoothDevice.setMessagePermissionChoice(2);
                }
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) MiuiHeadsetFragment.this.findPreference(localBluetoothProfile.toString());
                if (checkBoxPreference != null) {
                    MiuiHeadsetFragment.this.refreshProfilePreference(checkBoxPreference, localBluetoothProfile);
                }
            }
        }, context.getString(R.string.bluetooth_disable_profile_title), Html.fromHtml(context.getString(R.string.bluetooth_disable_profile_message, string, name)));
        this.mDisconnectDialog = showDisconnectDialog;
        showDisconnectDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.4
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) MiuiHeadsetFragment.this.findPreference(localBluetoothProfile.toString());
                if (checkBoxPreference != null) {
                    MiuiHeadsetFragment.this.refreshProfilePreference(checkBoxPreference, localBluetoothProfile);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void askUnpair() {
        Activity activity = this.mActivity;
        String alias = this.mDevice.getAlias();
        if (TextUtils.isEmpty(alias)) {
            alias = this.mDevice.getName();
        }
        Utils.showDisconnectDialog(activity, null, new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.43
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiHeadsetFragment.this.unpairDevice();
                MiuiHeadsetFragment.this.deleteOnSavedDevice();
                if (MiuiHeadsetFragment.this.mPrefConfig != null && MiuiHeadsetFragment.this.mActivity != null) {
                    MiuiHeadsetFragment.this.mPrefConfig.clearSharedPreferencesConfig(MiuiHeadsetFragment.this.mActivity);
                }
                MiuiHeadsetFragment.this.finish();
            }
        }, activity.getString(R.string.miheadset_ignore), Html.fromHtml(String.format(activity.getString(R.string.miheadset_unpair_device_text), alias)));
    }

    private void broadcastMultiA2dpStateChange(BluetoothDevice bluetoothDevice, int i) {
        Intent intent = new Intent("MultiA2dp.ACTION.STATE_CHANGED");
        intent.setPackage("com.android.bluetooth");
        intent.putExtra("android.bluetooth.device.extra.DEVICE", bluetoothDevice);
        intent.putExtra("MultiA2dp.EXTRA.STATE", i);
        try {
            getActivity().sendBroadcast(intent);
        } catch (Exception e) {
            Log.v("MiuiHeadsetFragment", "send broadcast failed ", e);
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
            Log.v("MiuiHeadsetFragment", "send broadcast failed ", e);
        }
    }

    private boolean checkAISupport() {
        Context applicationContext = getActivity().getApplicationContext();
        long version = applicationContext != null ? getVersion(applicationContext) : -1L;
        Log.d("MiuiHeadsetFragment", "checkAISupport " + this.mDeviceId + " ai version= " + version);
        if ("0201010000".equals(this.mDeviceId) || HeadsetIDConstants.isTWS01DomesticHeadset(this.mDeviceId)) {
            return HeadsetIDConstants.isK76sHeadset(this.mDeviceId) ? version >= 505118000 : version >= 305019010;
        } else if (HeadsetIDConstants.isK73DomesticHeadset(this.mDeviceId)) {
            return version >= 505109000;
        } else if (HeadsetIDConstants.isK75DomesticHeadset(this.mDeviceId)) {
            return version >= 505115000;
        } else if (HeadsetIDConstants.isK77sDomesticHeadset(this.mDeviceId)) {
            return false;
        } else {
            Log.d("MiuiHeadsetFragment", "checkAISupport device not support:" + this.mDeviceId);
            return false;
        }
    }

    private boolean checkPhoneCodecEnable(String str) {
        String str2 = SystemProperties.get("ro.product.device");
        Log.e("MiuiHeadsetFragment", "checkPhoneCodecEnable " + str + ", " + str2);
        if (TextUtils.isEmpty(str2)) {
            return false;
        }
        if (HeadsetIDConstants.isK77sHeadset(str)) {
            if (!"qcom".equals(FeatureParser.getString("vendor")) || Build.VERSION.SDK_INT <= 30) {
                return Arrays.asList(HeadsetIDConstants.isK77sDomesticHeadset(str) ? K77S_CODEC_ENABLE_PRODUCTS : K77S_GL_CODEC_ENABLE_PRODUCTS).contains(str2);
            }
            return true;
        } else if (!HeadsetIDConstants.isK73Headset(str)) {
            if (HeadsetIDConstants.isK75Headset(str)) {
                return false;
            }
            return ((HeadsetIDConstants.isK71Headset(str) || HeadsetIDConstants.isK71HeadsetGlobal(str)) && Build.VERSION.SDK_INT > 30) ? "qcom".equals(FeatureParser.getString("vendor")) : Arrays.asList(K71_CODEC_ENALBE_PRODUCTS).contains(str2);
        } else if (!"qcom".equals(FeatureParser.getString("vendor")) || this.mLHDCV3Device) {
            if (Build.VERSION.SDK_INT > 30) {
                return true;
            }
            return Arrays.asList(HeadsetIDConstants.isK73DomesticHeadset(str) ? K73_CODEC_ENABLE_PRODUCTS : K73A_GL_CODEC_ENABLE_PRODUCTS).contains(str2);
        } else {
            return false;
        }
    }

    private void closeAbsVolume() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("abs_volume_pre");
        if (checkBoxPreference != null) {
            Log.i("MiuiHeadsetFragment", "onAbsVolumePrefClicked  set false");
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.MiuiHeadsetFragment.closeLeAudio():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeProfileProxy(int i) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("MiuiHeadsetFragment", " " + i);
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
            Log.d("MiuiHeadsetFragment", "create createAbsoluteVolumePreference");
            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(this.mCodecContainer.getContext());
            checkBoxPreference.setKey("abs_volume_pre");
            checkBoxPreference.setTitle(R.string.bt_absVolume_pre_title);
            checkBoxPreference.setSummary(R.string.bt_absVolume_summary);
            checkBoxPreference.setPersistent(false);
            checkBoxPreference.setOnPreferenceChangeListener(this);
            checkBoxPreference.setChecked(this.mCachedDevice.getSpecificCodecStatus("ABSOLUTEVOLUME") == 1);
            BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
            BluetoothDevice activeDevice = bluetoothA2dp != null ? bluetoothA2dp.getActiveDevice() : null;
            Log.d("MiuiHeadsetFragment", "onAbsVolumePrefClicked mBluetoothA2dp " + this.mBluetoothA2dp);
            if (this.mBluetoothA2dp == null || activeDevice == null || !activeDevice.equals(this.mCachedDevice)) {
                checkBoxPreference.setEnabled(false);
            } else {
                checkBoxPreference.setEnabled(true);
            }
            return checkBoxPreference;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MiuiHeadsetFragment", "error " + e);
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
                Log.d("MiuiHeadsetFragment", "Integer.parseInt E: " + e.toString());
            }
        }
        Log.d("MiuiHeadsetFragment", "KEY_AUDIO_SHARE_VOLUME_PRE = " + i);
        bluetoothVolumeSeekBarPreference.setProgress(i);
        bluetoothVolumeSeekBarPreference.setIcon(R.drawable.ic_bt_headphones_a2dp_bonded);
        bluetoothVolumeSeekBarPreference.setPersistent(false);
        bluetoothVolumeSeekBarPreference.setOrder(80);
        bluetoothVolumeSeekBarPreference.setVisible(checkBoxPreference.isChecked());
        bluetoothVolumeSeekBarPreference.setOnPreferenceChangeListener(this);
        bluetoothVolumeSeekBarPreference.setStopTrackingTouchListener(new SeekBarPreference.StopTrackingTouchListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.21
            @Override // com.android.settings.widget.SeekBarPreference.StopTrackingTouchListener
            public void onStopTrackingTouch() {
                MiuiHeadsetFragment.this.handleAudioShareVolume();
            }
        });
        return bluetoothVolumeSeekBarPreference;
    }

    private void createDialog() {
        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("ldac_pre");
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.13
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiHeadsetFragment.this.writeBluetoothA2dpConfiguration(true);
                CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("latency_pre");
                if (checkBoxPreference == null || MiuiHeadsetFragment.this.mBluetoothA2dp == null) {
                    return;
                }
                checkBoxPreference.setChecked(true);
                if (checkBoxPreference2 != null) {
                    checkBoxPreference2.setChecked(true);
                    checkBoxPreference2.setEnabled(true);
                }
                CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("hd_audio");
                if (checkBoxPreference3 != null) {
                    checkBoxPreference3.setEnabled(true);
                }
                if (MiuiHeadsetFragment.this.mLHDCV3Device) {
                    MiuiHeadsetFragment.this.mDevice.setSpecificCodecStatus("STORE_DEVICE_CODEC", 10);
                    MiuiHeadsetFragment.this.mCachedDevice.setSpecificCodecStatus("LHDC_V3", 1);
                    MiuiHeadsetFragment.this.mCachedDevice.setSpecificCodecStatus("latency_val", 1);
                } else if (MiuiHeadsetFragment.this.mLHDCV2Device) {
                    MiuiHeadsetFragment.this.mCachedDevice.setSpecificCodecStatus("LHDC_V2", 1);
                    MiuiHeadsetFragment.this.mCachedDevice.setSpecificCodecStatus("latency_val", 1);
                } else if (MiuiHeadsetFragment.this.mLHDCV1Device) {
                    MiuiHeadsetFragment.this.mCachedDevice.setSpecificCodecStatus("LHDC_V1", 1);
                    MiuiHeadsetFragment.this.mCachedDevice.setSpecificCodecStatus("latency_val", 1);
                } else if (MiuiHeadsetFragment.this.mLDACDevice) {
                    MiuiHeadsetFragment.this.mCachedDevice.setSpecificCodecStatus("LDAC", 1);
                } else if (MiuiHeadsetFragment.this.mAACDevice) {
                    MiuiHeadsetFragment.this.mDevice.setSpecificCodecStatus("STORE_DEVICE_CODEC", 1);
                    MiuiHeadsetFragment.this.mCachedDevice.setSpecificCodecStatus("AAC", 1);
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
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.14
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (checkBoxPreference != null) {
                    if (MiuiHeadsetFragment.this.mLHDCV3Device) {
                        checkBoxPreference.setChecked(MiuiHeadsetFragment.this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") == 1);
                        if (MiuiHeadsetFragment.this.configCodec != null) {
                            MiuiHeadsetFragment.this.configCodec.setEnabled(!checkBoxPreference.isChecked());
                        }
                    } else if (MiuiHeadsetFragment.this.mLHDCV2Device) {
                        checkBoxPreference.setChecked(MiuiHeadsetFragment.this.mCachedDevice.getSpecificCodecStatus("LHDC_V2") == 1);
                    } else if (MiuiHeadsetFragment.this.mLHDCV1Device) {
                        checkBoxPreference.setChecked(MiuiHeadsetFragment.this.mCachedDevice.getSpecificCodecStatus("LHDC_V1") == 1);
                    } else if (MiuiHeadsetFragment.this.mLDACDevice) {
                        checkBoxPreference.setChecked(MiuiHeadsetFragment.this.mCachedDevice.getSpecificCodecStatus("LDAC") == 1);
                    } else if (MiuiHeadsetFragment.this.mAACDevice) {
                        checkBoxPreference.setChecked(MiuiHeadsetFragment.this.mCachedDevice.getSpecificCodecStatus("AAC") == 1);
                    }
                }
            }
        });
        this.mDialog.show();
    }

    private void createDialogForLeAudio(final CheckBoxPreference checkBoxPreference) {
        final CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) getPreferenceScreen().findPreference("le_audio_pre");
        final CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) getPreferenceScreen().findPreference("abs_volume_pre");
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.44
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.MiuiHeadsetFragment.AnonymousClass44.onClick(android.content.DialogInterface, int):void");
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
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.45
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (checkBoxPreference2 != null) {
                    if (LocalBluetoothProfileManager.isTbsProfileEnabled()) {
                        checkBoxPreference2.setChecked(MiuiHeadsetFragment.this.mCachedDevice.getLeAudioStatus() == 1);
                    } else {
                        checkBoxPreference2.setChecked(MiuiHeadsetFragment.this.mCachedDevice.getSpecificCodecStatus("LEAUDIO") == 1);
                    }
                }
            }
        });
        this.mDialog.show();
    }

    private void createDialogForOpenAbsVolume() {
        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("abs_volume_pre");
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.16
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (checkBoxPreference != null) {
                    Log.i("MiuiHeadsetFragment", "onAbsVolumePrefClicked  set true");
                    checkBoxPreference.setChecked(true);
                    MiuiHeadsetFragment.this.mCachedDevice.setSpecificCodecStatus("ABSOLUTEVOLUME", 1);
                    MiuiHeadsetFragment.this.sendBroadcastEnableOrDisable(true);
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
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.17
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                CheckBoxPreference checkBoxPreference2 = checkBoxPreference;
                if (checkBoxPreference2 != null) {
                    checkBoxPreference2.setChecked(MiuiHeadsetFragment.this.mCachedDevice.getSpecificCodecStatus("ABSOLUTEVOLUME") == 1);
                }
            }
        });
        this.mDialog.show();
    }

    private CheckBoxPreference createHDAudioPreference() {
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
        checkBoxPreference.setKey("hd_audio");
        checkBoxPreference.setTitle(R.string.miheadset_hd_audio_title);
        checkBoxPreference.setSummary(String.format(getResources().getString(R.string.miheadset_hd_audio_summary), 24, 96));
        checkBoxPreference.setPersistent(false);
        checkBoxPreference.setOrder(2);
        checkBoxPreference.setOnPreferenceChangeListener(this);
        if (this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") == 0) {
            checkBoxPreference.setEnabled(false);
            checkBoxPreference.setChecked(false);
        }
        return checkBoxPreference;
    }

    private CheckBoxPreference createLatencyCodecPreference() {
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(this.mCodecContainer.getContext());
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
        checkBoxPreference.setOrder(3);
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
        Log.i("MiuiHeadsetFragment", " createLeAudioPreference");
        return checkBoxPreference;
    }

    private CheckBoxPreference createProfilePreference(LocalBluetoothProfile localBluetoothProfile) {
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(this.mProfileContainer.getContext());
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
        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(this.mCodecContainer.getContext());
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

    private void delFromWhiteList(String str) {
        String str2 = SystemProperties.get(str, "");
        if (str2.length() < 18) {
            Log.w("MiuiHeadsetFragment", "delFromWhiteList(): no valid device in white list");
            return;
        }
        Log.d("MiuiHeadsetFragment", "delFromWhiteList(): whitelist before del is " + str2 + ", current dev is " + this.mCachedDevice.getAddress().toLowerCase() + ", prop is " + str);
        StringBuilder sb = new StringBuilder(this.mCachedDevice.getAddress().toLowerCase());
        sb.append(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        SystemProperties.set(str, str2.replaceAll(sb.toString(), ""));
        StringBuilder sb2 = new StringBuilder();
        sb2.append("delFromWhiteList(): whitelist after del is ");
        sb2.append(SystemProperties.get(str, ""));
        Log.d("MiuiHeadsetFragment", sb2.toString());
    }

    private void delFromWhiteListForAbsoluteVolume(String str) {
        String str2 = SystemProperties.get(str, "");
        if (str2.length() < 18) {
            Log.w("MiuiHeadsetFragment", "delFromWhiteList(): no valid device in white list");
            return;
        }
        Log.d("MiuiHeadsetFragment", "delFromWhiteList(): whitelist before del is " + str2 + ", current dev is " + this.mCachedDevice.getAddress().toLowerCase() + ", prop is " + str);
        StringBuilder sb = new StringBuilder(this.mCachedDevice.getAddress());
        sb.append(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        SystemProperties.set(str, str2.replaceAll(sb.toString(), ""));
        StringBuilder sb2 = new StringBuilder();
        sb2.append("delFromWhiteList(): whitelist after del is ");
        sb2.append(SystemProperties.get(str, ""));
        Log.d("MiuiHeadsetFragment", sb2.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteOnSavedDevice() {
        new MiuiOnSavedDeviceDataUtils(getPrefContext()).deleteDeviceData(this.mCachedDevice.getDevice().getAddress());
    }

    private void deleteSaveMacForLeAudio() {
        String str;
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
            Log.i("MiuiHeadsetFragment", "updateValue is" + str);
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
            Log.i("MiuiHeadsetFragment", "startIndex is " + indexOf + " value is " + string);
            str2 = string.substring(indexOf + 18, indexOf + 35);
            str = string.substring(indexOf + 36, indexOf2);
            Log.i("MiuiHeadsetFragment", "leStr1 is " + str2 + " leStr2 is " + str);
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            BluetoothDevice remoteDevice = defaultAdapter.getRemoteDevice(str2);
            BluetoothDevice remoteDevice2 = defaultAdapter.getRemoteDevice(str);
            if (remoteDevice != null && remoteDevice.getBondState() != 10) {
                defaultAdapter.disconnectAllEnabledProfiles(remoteDevice);
                Log.i("MiuiHeadsetFragment", "disconnect leStr1");
            }
            if (remoteDevice2 == null || remoteDevice2.getBondState() == 10) {
                return;
            }
            defaultAdapter.disconnectAllEnabledProfiles(remoteDevice2);
            Log.i("MiuiHeadsetFragment", "disconnect leStr2");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getAccountInfo(int i) {
        if (!"CN".equals(MiuiBTUtils.getRegion())) {
            if (!isNetValidated(this.mActivity)) {
                Log.e("MiuiHeadsetFragment", "network is not active");
                return;
            }
            this.mToken = "INTL";
            MessageHandler messageHandler = this.mWorkHandler;
            messageHandler.sendMessage(messageHandler.obtainMessage(100, i, 0));
            return;
        }
        IMiuiHeadsetService iMiuiHeadsetService = this.mService;
        if (iMiuiHeadsetService == null) {
            Log.e("MiuiHeadsetFragment", "error service is null when ota");
            return;
        }
        try {
            this.mToken = iMiuiHeadsetService.setCommonCommand(104, "", this.mDevice);
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "error " + e);
        }
        if (TextUtils.isEmpty(this.mToken)) {
            if (isNetValidated(this.mActivity)) {
                waitAndShowFutureResult(new XiaomiOAuthorize().setAppId(APPID).setRedirectUrl(REDIRECTURL).setSkipConfirm(true).startGetAccessToken(this.mActivity), i);
                return;
            } else {
                Log.e("MiuiHeadsetFragment", "network is not active");
                return;
            }
        }
        try {
            Settings.System.putString(this.mActivity.getContentResolver(), "com.xiaomi.bluetooth.headset.account", "");
        } catch (Exception unused) {
            Log.d("MiuiHeadsetFragment", "clean account set");
        }
        MessageHandler messageHandler2 = this.mWorkHandler;
        messageHandler2.sendMessage(messageHandler2.obtainMessage(100, i, 0));
    }

    private BluetoothCodecConfig getCodecConfig(BluetoothA2dp bluetoothA2dp, int i, int i2) {
        int i3;
        int i4;
        BluetoothCodecConfig bluetoothCodecConfig;
        BluetoothCodecStatus codecStatus = bluetoothA2dp.getCodecStatus(this.mCachedDevice.getDevice());
        BluetoothCodecConfig bluetoothCodecConfig2 = null;
        if (codecStatus == null) {
            Log.d("MiuiHeadsetFragment", "getCodecConfig(): BluetoothCodecStatus is null");
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

    /* JADX INFO: Access modifiers changed from: private */
    public String getDefaultAncLevel(int i) {
        String str;
        try {
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "error " + e);
        }
        if (isSupportWindNoise(this.mDeviceId) && !TextUtils.isEmpty(this.mWindNoiseAncLevel) && this.mWindNoiseAncLevel.equals("01") && i == 1) {
            return "0104";
        }
        if (TextUtils.isEmpty(this.mAncLevelMap)) {
            if (i == 1 && (str = this.mDeviceId) != null && (HeadsetIDConstants.isK73Headset(str) || HeadsetIDConstants.isK75Headset(this.mDeviceId))) {
                return "0102";
            }
            return "0" + i + "00";
        }
        for (String str2 : this.mAncLevelMap.split("\\;")) {
            if (str2.startsWith("0" + i)) {
                return str2;
            }
        }
        return "0" + i + "00";
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

    /* JADX INFO: Access modifiers changed from: private */
    public void getProfileProxy() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            return;
        }
        defaultAdapter.getProfileProxy(getActivity().getApplicationContext(), this.mBluetoothA2dpServiceListener, 2);
        defaultAdapter.getProfileProxy(getActivity().getApplicationContext(), this.mBluetoothHfpServiceListener, 1);
    }

    public static long getVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo("com.miui.voiceassist", 0).getLongVersionCode();
        } catch (Exception unused) {
            return -1L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Account getXiaomiAccount(Context context) {
        if (context == null) {
            Log.e("MiuiHeadsetFragment", "context is null to get account");
            return null;
        }
        Account[] accountsByType = AccountManager.get(context).getAccountsByType("com.xiaomi");
        if (accountsByType.length > 0) {
            return accountsByType[0];
        }
        return null;
    }

    private void gotoFitnessFragment() {
        ((MiuiHeadsetActivity) this.mActivity).changeFragment(new MiuiHeadsetFitnessFragment());
    }

    private void gotoKeyConfigFragment() {
        MiuiHeadsetKeyConfigFragment miuiHeadsetKeyConfigFragment = new MiuiHeadsetKeyConfigFragment();
        if (this.mBluetoothA2dp != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("device_connected", this.mBluetoothA2dp.getConnectionState(this.mCachedDevice.getDevice()) == 2);
            miuiHeadsetKeyConfigFragment.setArguments(bundle);
        }
        ((MiuiHeadsetActivity) this.mActivity).changeFragment(miuiHeadsetKeyConfigFragment);
    }

    private void handleAudioShareConfigStatus(boolean z) {
        BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) getPreferenceScreen().findPreference("audio_share_volume_pre");
        if (bluetoothVolumeSeekBarPreference != null) {
            bluetoothVolumeSeekBarPreference.setEnabled(z);
        } else {
            Log.d("MiuiHeadsetFragment", "BluetoothVolumeSeekBarPreference == null");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAudioShareVolume() {
        int progress = ((BluetoothVolumeSeekBarPreference) getPreferenceScreen().findPreference("audio_share_volume_pre")).getProgress();
        Log.d("MiuiHeadsetFragment", "SeekBarPreference value = " + progress);
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
                this.mHandler.postDelayed(this.mRunnable, 2000L);
                return;
            }
            this.mLC3Switching = true;
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.11
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        MiuiHeadsetFragment.this.mLC3Switching = false;
                        Log.d("MiuiHeadsetFragment", "leAudioPre: Timeout to set mLC3Switching false");
                        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("le_audio_pre");
                        if (checkBoxPreference2 != null) {
                            if ((MiuiHeadsetFragment.this.mCachedDevice.getLeAudioStatus() != 1 && !MiuiHeadsetFragment.this.isHfpConnected()) || MiuiHeadsetFragment.this.isSCOOn() || MiuiHeadsetFragment.this.isLeAudioCgOn() || MiuiHeadsetFragment.this.isSingleHeadsetConn) {
                                return;
                            }
                            checkBoxPreference2.setEnabled(true);
                            Log.d("MiuiHeadsetFragment", "leAudioPre: Timeout to enable LC3 Pref");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("MiuiHeadsetFragment", "error " + e);
                    }
                }
            }, 2000L);
            Log.d("MiuiHeadsetFragment", "leAudioPre: Delay 2s to enable LC3 Pref");
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

    private void handleHdAudio(String str, final String str2) {
        if (TextUtils.isEmpty(str) || this.mDevice == null || TextUtils.isEmpty(str2) || !str.equalsIgnoreCase(this.mDevice.getAddress())) {
            return;
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    MiuiHeadsetFragment miuiHeadsetFragment = MiuiHeadsetFragment.this;
                    if (miuiHeadsetFragment.k73HDAudioEable(miuiHeadsetFragment.mDeviceId)) {
                        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) MiuiHeadsetFragment.this.getPreferenceScreen().findPreference("hd_audio");
                        int parseInt = Integer.parseInt(str2);
                        if (checkBoxPreference != null) {
                            if (parseInt == -1) {
                                Log.d("MiuiHeadsetFragment", "not support");
                                checkBoxPreference.setChecked(false);
                            } else if (parseInt == 0) {
                                checkBoxPreference.setChecked(false);
                            } else if (parseInt == 1) {
                                checkBoxPreference.setChecked(true);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d("MiuiHeadsetFragment", "deal HD audio error: " + e);
                }
            }
        }, 10L);
    }

    private void handleHeadSetConnect() {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        this.mHandler.postDelayed(this.mCodecConfigRun, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMultiA2DPState(int i) {
        DropDownPreference dropDownPreference;
        Log.d("MiuiHeadsetFragment", "handleMultiA2DPState = " + i);
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
                DropDownPreference dropDownPreference2 = this.configCodec;
                if (dropDownPreference2 != null) {
                    dropDownPreference2.setEnabled(false);
                }
                Log.d("MiuiHeadsetFragment", "handleMultiA2DPState enabled");
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
                if (this.mLHDCV3Device && (dropDownPreference = this.configCodec) != null) {
                    dropDownPreference.setEnabled(!z);
                }
                if (checkBoxPreference3 != null) {
                    if (this.mLHDCV3Device) {
                        checkBoxPreference3.setEnabled(z);
                    } else {
                        checkBoxPreference3.setEnabled(true);
                    }
                }
                Log.d("MiuiHeadsetFragment", "handleMultiA2DPState disabled");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMultiLeDevices() {
        BluetoothDevice remoteDevice;
        Context context = getContext();
        String string = Settings.Global.getString(context.getContentResolver(), "three_mac_for_ble_f");
        if (string == null || string.length() <= 54) {
            Log.i("MiuiHeadsetFragment", "handleMultiLeDevices is not need");
            return;
        }
        int i = 0;
        while (i < string.length() / 54) {
            int i2 = i + 1;
            if (string.length() >= (i2 * 54) - 1) {
                int i3 = i * 54;
                String substring = string.substring(i3, i3 + 17);
                if (substring != null && substring.length() == 17) {
                    Log.i("MiuiHeadsetFragment", "handleMultiLeDevices brMac is " + substring);
                    if (substring.equalsIgnoreCase(this.mDeviceMacAddress)) {
                        Log.i("MiuiHeadsetFragment", "ignore oneself");
                    } else {
                        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (defaultAdapter != null && (remoteDevice = defaultAdapter.getRemoteDevice(substring)) != null) {
                            CachedBluetoothDevice findDevice = this.mManager.getCachedDeviceManager().findDevice(remoteDevice);
                            if (findDevice == null) {
                                Log.i("MiuiHeadsetFragment", "mCachedDevice is null and new one ");
                                findDevice = new CachedBluetoothDevice(context, this.mProfileManager, remoteDevice);
                            }
                            Log.i("MiuiHeadsetFragment", "mLeCachedDevice mac is " + findDevice.getAddress());
                            int i4 = i3 + 36;
                            String substring2 = string.substring(i3 + 18, i4 + (-1));
                            String substring3 = string.substring(i4, i3 + 54 + (-1));
                            Log.i("MiuiHeadsetFragment", "handleMultiLeDevices leStr1 is " + substring2 + " leStr2 is " + substring3);
                            BluetoothDevice remoteDevice2 = defaultAdapter.getRemoteDevice(substring2);
                            BluetoothDevice remoteDevice3 = defaultAdapter.getRemoteDevice(substring3);
                            if (remoteDevice2 != null && remoteDevice2.getBondState() != 10) {
                                if (remoteDevice2.getBondState() == 11) {
                                    remoteDevice2.cancelBondProcess();
                                } else {
                                    remoteDevice2.removeBond();
                                }
                                Log.i("MiuiHeadsetFragment", "handleMultiLeDevices remove bond leStr1");
                            }
                            if (remoteDevice3 != null && remoteDevice3.getBondState() != 10) {
                                if (remoteDevice3.getBondState() == 11) {
                                    remoteDevice3.cancelBondProcess();
                                } else {
                                    remoteDevice3.removeBond();
                                }
                                Log.i("MiuiHeadsetFragment", "handleMultiLeDevices remove bond leStr2");
                            }
                            findDevice.setSpecificCodecStatus("LEAUDIO", 2);
                        }
                    }
                }
            }
            i = i2;
        }
    }

    private void handleOtaInfo(String str) {
        int i;
        BluetoothDevice bluetoothDevice = this.mDevice;
        if (bluetoothDevice == null || !bluetoothDevice.getAddress().equalsIgnoreCase(str)) {
            return;
        }
        int i2 = this.mFwVersionCode;
        if (i2 != -1 && (i = this.mLastOnlineVersionCode) != -1 && i2 < i) {
            Log.d("MiuiHeadsetFragment", "the version is not last");
            this.mRootView.findViewById(R.id.red_dot).setVisibility(0);
        } else if (TextUtils.isEmpty(this.mFwVersion)) {
            Log.e("MiuiHeadsetFragment", "handleOtaInfo() some error occur!");
        } else {
            Log.d("MiuiHeadsetFragment", "the version has been the last");
            this.mRootView.findViewById(R.id.red_dot).setVisibility(4);
        }
        this.mOtaIndicate = Boolean.TRUE;
    }

    public static String hexToBinaryString(String str) {
        int i = 0;
        String str2 = "";
        while (str2.length() < 8) {
            try {
                int i2 = i + 2;
                String binaryString = Integer.toBinaryString(Integer.parseInt(str.substring(i, i2)));
                if (binaryString.length() != 4) {
                    while (binaryString.length() < 4) {
                        binaryString = "0" + binaryString;
                    }
                }
                str2 = str2 + binaryString;
                i = i2;
            } catch (Exception e) {
                Log.w("MiuiHeadsetFragment", "hexToBinaryString error " + e);
            }
        }
        return str2;
    }

    private void initButton() {
        View view = this.mRootView;
        if (view != null) {
            CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.button_modify);
            CheckedTextView checkedTextView2 = (CheckedTextView) this.mRootView.findViewById(R.id.button_delete);
            Folme.useAt(checkedTextView).touch().handleTouchOf(checkedTextView, new AnimConfig[0]);
            Folme.useAt(checkedTextView2).touch().handleTouchOf(checkedTextView2, new AnimConfig[0]);
            checkedTextView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.27
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    if (MiuiHeadsetFragment.this.mDevice.isConnected()) {
                        MiuiHeadsetFragment.this.askDisconnect();
                    }
                }
            });
            checkedTextView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.28
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    if (!HeadsetIDConstants.isTWS01Headset(MiuiHeadsetFragment.this.mDeviceId) && !HeadsetIDConstants.isK77sHeadset(MiuiHeadsetFragment.this.mDeviceId) && !HeadsetIDConstants.isK73Headset(MiuiHeadsetFragment.this.mDeviceId) && !HeadsetIDConstants.isK75Headset(MiuiHeadsetFragment.this.mDeviceId) && !HeadsetIDConstants.isSupportZimiAdapter("common", MiuiHeadsetFragment.this.mDeviceId)) {
                        MiuiHeadsetFragment.this.askUnpair();
                        return;
                    }
                    MiuiHeadsetFragment.this.unpairDevice();
                    MiuiHeadsetFragment.this.deleteOnSavedDevice();
                    if (MiuiHeadsetFragment.this.mPrefConfig != null && MiuiHeadsetFragment.this.mActivity != null) {
                        MiuiHeadsetFragment.this.mPrefConfig.clearSharedPreferencesConfig(MiuiHeadsetFragment.this.mActivity);
                    }
                    MiuiHeadsetFragment.this.finish();
                }
            });
        }
    }

    private void initDeviceName() {
        View view = this.mRootView;
        TextView textView = view != null ? (TextView) view.findViewById(R.id.deviceName) : null;
        if (textView != null) {
            textView.setText(this.mDevice.getAlias());
        }
    }

    private void initResource() {
        PreferenceGroup preferenceGroup;
        this.mInearTest = (CheckBoxPreference) findPreference("Ineartest");
        this.mAutoAck = (CheckBoxPreference) findPreference("AutoAckMode");
        this.mMultiConnect = (CheckBoxPreference) findPreference("MultiConnectMode");
        this.mNotifiDisplay = (CheckBoxPreference) findPreference("notificationdisplay");
        this.configCodec = (DropDownPreference) findPreference("codecType");
        try {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                String string = Settings.Global.getString(activity.getContentResolver(), "headset_notification_feature_enable");
                if (TextUtils.isEmpty(string) || !string.equals("false")) {
                    Log.d("MiuiHeadsetFragment", "cloud data switch of notification is enable! " + string);
                } else {
                    Log.d("MiuiHeadsetFragment", "cloud data switch of notification is disenable! ");
                    if (this.mNotifiDisplay != null && (preferenceGroup = (PreferenceGroup) findPreference("switchConfig")) != null) {
                        Settings.Secure.putInt(activity.getContentResolver(), "notification_bt_display_switch_is_enable", 0);
                        this.mNotifiDisplay.setEnabled(false);
                        if (this.mNotifiDisplay.isChecked()) {
                            Intent intent = new Intent("com.android.bluetooth.headset.notification");
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("Device", this.mDevice);
                            intent.putExtra("btData", bundle);
                            activity.sendBroadcast(intent);
                            this.mNotifiDisplay.setChecked(false);
                        }
                        preferenceGroup.removePreference(this.mNotifiDisplay);
                        this.mNotifiDisplay = null;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "cloud data switch of notification get faied " + e);
        }
        CheckBoxPreference checkBoxPreference = this.mInearTest;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(this);
            this.mInearTest.setChecked(false);
        }
        CheckBoxPreference checkBoxPreference2 = this.mAutoAck;
        if (checkBoxPreference2 != null) {
            checkBoxPreference2.setOnPreferenceChangeListener(this);
            this.mAutoAck.setChecked(false);
        }
        CheckBoxPreference checkBoxPreference3 = this.mMultiConnect;
        if (checkBoxPreference3 != null) {
            checkBoxPreference3.setOnPreferenceChangeListener(this);
        }
        DropDownPreference dropDownPreference = this.configCodec;
        if (dropDownPreference != null) {
            dropDownPreference.setOnPreferenceChangeListener(this);
        }
        View view = this.mRootView;
        if (view != null) {
            view.findViewById(R.id.otaLayout).setEnabled(false);
            updateAncUi("-1", false);
        }
        notificationSwitchInit();
    }

    private void initSpatialAudioPreferences() {
        Log.d("MiuiHeadsetFragment", "isEnable3DSurround " + this.mSpatialSoundWrapper.isEnable3DSurround());
        Log.d("MiuiHeadsetFragment", "isSupportHeadTrackAlgoPhone " + this.mSpatialSoundWrapper.isSupportHeadTrackAlgoPhone());
        Log.d("MiuiHeadsetFragment", "isPhoneSupportSurroundAlgo " + this.mSpatialSoundWrapper.isPhoneSupportSurroundAlgo());
        Log.d("MiuiHeadsetFragment", "isSupportSpatialAndSurround " + this.mSpatialSoundWrapper.isSupportSpatialAndSurround());
        this.spaceAudioPreferenceGroup = (PreferenceGroup) findPreference("spatial_audio_root_key");
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("surround_sound_3d_key");
        this.headTrackingCheckBox = (CheckBoxPreference) findPreference("headset_head_tracking_key");
        String headTrackSummary = this.mSpatialSoundWrapper.getHeadTrackSummary(getActivity(), this.mSupportGyr.booleanValue());
        if (headTrackSummary == null) {
            getPreferenceScreen().removePreference(this.spaceAudioPreferenceGroup);
            this.isSupportHeadTrackAlgo = false;
            return;
        }
        this.isSupportHeadTrackAlgo = true;
        this.spaceAudioPreferenceGroup.removePreference(checkBoxPreference);
        Log.d("MiuiHeadsetFragment", "headTrackSummary= " + headTrackSummary);
        this.headTrackingCheckBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                if (MiuiHeadsetFragment.this.mService != null) {
                    try {
                        Log.e("MiuiHeadsetFragment", "headTrackingCheckBox setCommonCommand " + ((Boolean) obj).booleanValue());
                        MiuiHeadsetFragment.this.mService.setCommonCommand(113, booleanValue ? "1" : "0", MiuiHeadsetFragment.this.mDevice);
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return false;
            }
        });
    }

    private boolean isDeviceInListForAbsoluteVolume(String str, String str2) {
        String str3 = SystemProperties.get(str2, "");
        if (str3.indexOf(str) == -1) {
            Log.d("MiuiHeadsetFragment", "can't find " + str + " in " + str3);
            return false;
        }
        Log.d("MiuiHeadsetFragment", "device " + str + " is in list " + str3);
        return true;
    }

    private boolean isLeAudioBrDevice(String str) {
        try {
        } catch (Exception e) {
            Log.v("MiuiHeadsetFragment", "isLeAudioBrDevice Exception " + e);
        }
        if (LocalBluetoothProfileManager.isTbsProfileEnabled()) {
            return this.mCachedDevice.isDualModeDevice();
        }
        Context context = getContext();
        String string = Settings.Global.getString(context.getContentResolver(), "three_mac_for_ble_f");
        if (string != null && string.contains(str)) {
            Log.i("MiuiHeadsetFragment", "device isLeAudioBrDevice");
            boolean isLoggable = Log.isLoggable("Lc3TestMode", 2);
            String string2 = Settings.Global.getString(context.getContentResolver(), "lc3Enable");
            if ((isLoggable || (string2 != null && string2.equals("true"))) && !miui.os.Build.IS_INTERNATIONAL_BUILD) {
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

    public static boolean isNetValidated(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            return connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork()).hasCapability(16);
        } catch (Exception unused) {
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
    public boolean isSupportWindNoise(String str) {
        if (HeadsetIDConstants.isK71Headset(str)) {
            int i = this.mSupportAncWindVersionCode;
            return i <= this.mFwVersionCode || i <= this.mVersionCodeLocal;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean k73HDAudioEable(String str) {
        String str2;
        try {
            str2 = SystemProperties.get("ro.product.device");
        } catch (Exception e) {
            Log.d("MiuiHeadsetFragment", "deal HD audio enable error: " + e);
        }
        if (!TextUtils.isEmpty(str2)) {
            return HeadsetIDConstants.isK73DomesticHeadset(this.mDeviceId) && Arrays.asList(K73_HD_AUDIO_ENABLE_PRODUCTS).contains(str2);
        }
        Log.d("MiuiHeadsetFragment", "phone is empty");
        return false;
    }

    private void loadDevice() {
        Preference findPreference;
        PreferenceGroup preferenceGroup;
        try {
            String[] split = this.mSupport.split("\\,");
            if (split.length != 2) {
                Log.e("MiuiHeadsetFragment", "error length");
                return;
            }
            this.mDeviceId = split[0];
            if (!TextUtils.isEmpty(split[1]) && split[1].length() == 24) {
                if (split[1].charAt(0) == '1') {
                    this.mSupportOta = Boolean.TRUE;
                }
                if (split[1].charAt(1) == '1') {
                    this.mSupportAntiLost = Boolean.TRUE;
                }
                if (split[1].charAt(2) == '1') {
                    this.mSupportInear = Boolean.TRUE;
                }
                if (split[1].charAt(16) == '1') {
                    this.mSupportAnc = Boolean.TRUE;
                }
                if (split[1].charAt(3) == '1') {
                    this.mSupportAudioMode = Boolean.TRUE;
                }
                if (split[1].charAt(4) == '1') {
                    this.mSupportSignleEarMode = Boolean.TRUE;
                }
                if (split[1].charAt(5) == '1') {
                    this.mSupportAutoAck = Boolean.TRUE;
                }
                if (split[1].charAt(6) == '1') {
                    this.mSupportMultiConnect = Boolean.TRUE;
                }
                if (split[1].charAt(21) == '1') {
                    this.mSupportGyr = Boolean.TRUE;
                }
            }
            if (isDeviceIdSupportSetCodec(this.mDeviceId)) {
                this.mSupportCodecChange = Boolean.TRUE;
            }
            PreferenceGroup preferenceGroup2 = (PreferenceGroup) findPreference("switchConfig");
            PreferenceGroup preferenceGroup3 = (PreferenceGroup) findPreference("BtConfig");
            if (preferenceGroup2 != null && HeadsetIDConstants.isK77sHeadset(this.mDeviceId)) {
                Log.d("MiuiHeadsetFragment", "loadDevice: remove preference:fitness_check");
                preferenceGroup2.removePreference(findPreference("fitness_check"));
            }
            if (!this.mSupportInear.booleanValue() && !this.mSupportMultiConnect.booleanValue() && !this.mSupportAutoAck.booleanValue() && !this.mSupportAntiLost.booleanValue() && !this.mSupportCodecChange.booleanValue()) {
                getPreferenceScreen().removePreference(preferenceGroup2);
                preferenceGroup = (PreferenceGroup) findPreference("moreSettingsInAi");
                if (preferenceGroup != null || checkAISupport()) {
                }
                getPreferenceScreen().removePreference(preferenceGroup);
                Log.e("MiuiHeadsetFragment", "remove preference moreSettingsInAi");
                return;
            }
            if (!this.mSupportInear.booleanValue() && preferenceGroup2 != null) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("Ineartest");
                this.mInearTest = checkBoxPreference;
                preferenceGroup2.removePreference(checkBoxPreference);
            }
            if (preferenceGroup2 != null && (!this.mSupportMultiConnect.booleanValue() || !HeadsetIDConstants.isTWS01GlobalHeadset(this.mDeviceId))) {
                CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("MultiConnectMode");
                this.mMultiConnect = checkBoxPreference2;
                preferenceGroup2.removePreference(checkBoxPreference2);
            }
            if (((!HeadsetIDConstants.isUseInearBitForAutoAckHeadset(this.mDeviceId) && !this.mSupportAutoAck.booleanValue()) || (HeadsetIDConstants.isUseInearBitForAutoAckHeadset(this.mDeviceId) && !this.mSupportInear.booleanValue())) && preferenceGroup2 != null) {
                CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("AutoAckMode");
                this.mAutoAck = checkBoxPreference3;
                preferenceGroup2.removePreference(checkBoxPreference3);
                this.mShowAutoAck = Boolean.FALSE;
            }
            if (!this.mSupportAntiLost.booleanValue() && preferenceGroup2 != null) {
                preferenceGroup2.removePreference(findPreference("mi_headset_loss_dialog"));
            }
            if (!checkPhoneCodecEnable(this.mDeviceId)) {
                this.mSupportCodecChange = Boolean.FALSE;
            }
            if (!this.mSupportCodecChange.booleanValue() && preferenceGroup2 != null && (findPreference = findPreference("codecType")) != null) {
                preferenceGroup2.removePreference(findPreference);
            }
            preferenceGroup = (PreferenceGroup) findPreference("moreSettingsInAi");
            if (preferenceGroup != null) {
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "error " + e);
        }
    }

    private void miHeadsetLost() {
        ((MiuiHeadsetActivity) this.mActivity).changeFragment(new MiuiHeadsetAntiLostFragment());
    }

    private void notificationSwitchInit() {
        CheckBoxPreference checkBoxPreference = this.mNotifiDisplay;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(this);
            this.mNotifiDisplay.setChecked(false);
        }
    }

    private void onAbsVolumePrefClicked(CheckBoxPreference checkBoxPreference) {
        try {
            String address = this.mBluetoothA2dp.getActiveDevice() != null ? this.mBluetoothA2dp.getActiveDevice().getAddress() : "";
            Log.v("MiuiHeadsetFragment", "mDeviceMacAddress is " + this.mDeviceMacAddress + " activeMac is " + address);
            if (this.mBluetoothA2dp == null || address == null || !this.mDeviceMacAddress.equals(address)) {
                return;
            }
            this.mCachedDevice.setSpecificCodecStatus("ABSOLUTEVOLUMEOPERATE", 3);
            if (checkBoxPreference.isChecked()) {
                closeAbsVolume();
            } else {
                createDialogForOpenAbsVolume();
            }
        } catch (Exception e) {
            Log.v("MiuiHeadsetFragment", "onAbsVolumePrefClicked failed ", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAudioShareSwitchPrefClicked(CheckBoxPreference checkBoxPreference) {
        if (checkBoxPreference == null) {
            Log.d("MiuiHeadsetFragment", "CheckBoxPreference pref == null");
            return;
        }
        String string = Settings.Secure.getString(getActivity().getContentResolver(), "miui_store_audio_share_device_address");
        BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) getPreferenceScreen().findPreference("audio_share_volume_pre");
        if (this.mBluetoothA2dp != null) {
            if (checkBoxPreference.isChecked()) {
                Log.d("MiuiHeadsetFragment", "KEY_STORE_AUDIO_SHARE_DEVICE = " + string);
                checkBoxPreference.setChecked(false);
                broadcastMultiA2dpStateChange(null, 0);
                handleAudioShareConfigStatus(false);
                Log.d("MiuiHeadsetFragment", "CheckBoxPreference = unchecked");
            } else {
                if (this.mBluetoothA2dp.getConnectionState(this.mCachedDevice.getDevice()) == 2) {
                    String address = this.mCachedDevice.getAddress();
                    Log.d("MiuiHeadsetFragment", "cachedDeviceAddress = " + address);
                    Log.d("MiuiHeadsetFragment", "KEY_STORE_AUDIO_SHARE_DEVICE = " + string);
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
                Log.v("MiuiHeadsetFragment", "mDeviceMacAddress is " + this.mDeviceMacAddress + " activeMac is " + address);
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
            Log.v("MiuiHeadsetFragment", "onLeAudioPrefClicked failed ", e);
        }
    }

    private void onPrefClicked(CheckBoxPreference checkBoxPreference) {
        if (this.mBluetoothA2dp != null) {
            if (checkBoxPreference.isChecked()) {
                if (this.mBluetoothA2dp.getConnectionState(this.mCachedDevice.getDevice()) == 2) {
                    writeBluetoothA2dpConfiguration(false);
                    checkBoxPreference.setChecked(false);
                    MessageHandler messageHandler = this.mWorkHandler;
                    if (messageHandler != null) {
                        messageHandler.sendMessageDelayed(messageHandler.obtainMessage(102), 1000L);
                    }
                    CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) getPreferenceScreen().findPreference("latency_pre");
                    if (checkBoxPreference2 != null) {
                        checkBoxPreference2.setChecked(false);
                        checkBoxPreference2.setEnabled(false);
                    }
                    CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) getPreferenceScreen().findPreference("hd_audio");
                    if (checkBoxPreference3 != null) {
                        checkBoxPreference3.setChecked(false);
                        checkBoxPreference3.setEnabled(false);
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
                        this.mCachedDevice.setSpecificCodecStatus("STORE_DEVICE_CODEC", 2);
                    }
                }
            } else if (this.mBluetoothA2dp.getConnectionState(this.mCachedDevice.getDevice()) == 2) {
                createDialog();
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

    /* JADX INFO: Access modifiers changed from: private */
    public void otaHandler() {
        int i = this.mFwVersionCode;
        if (i != -1 && i < this.mLastOnlineVersionCode) {
            otaStart();
        } else if (TextUtils.isEmpty(this.mFwVersion) || !this.mOtaIndicate.booleanValue()) {
        } else {
            ToastUtil.show(this.mActivity, R.string.miheadset_last_version, 1);
        }
    }

    private void otaStart() {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("OnlineVersion", this.mLastOnlineVerion);
            jSONObject.put("OnlineVersionCode", this.mLastOnlineVersionCode);
            this.mService.startOta(this.mDevice, this.mLastOnlineUrl, this.mLastOnlineMessage, jSONObject.toString());
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "error " + e);
        }
    }

    private void refresh() {
        this.mCachedDevice.getName();
        refreshProfiles();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshDeviceFunKeyInfo(String str, String str2) {
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        int i;
        int i2;
        String str8;
        String str9;
        int i3;
        String str10;
        int i4;
        String str11;
        String str12;
        String str13;
        String str14;
        int i5;
        String str15;
        String str16;
        try {
            if (this.mService == null) {
                this.mService = ((MiuiHeadsetActivity) this.mActivity).getService();
            }
            String commonCommand = this.mService.setCommonCommand(106, "", this.mDevice);
            if (commonCommand == null || "".equals(commonCommand) || commonCommand.length() != 12) {
                commonCommand = "000011101110";
            }
            try {
                if (str == "") {
                    String substring = str2.substring(0, 6);
                    String substring2 = str2.substring(6, 12);
                    String substring3 = str2.substring(12, 18);
                    str4 = "MiuiHeadsetFragment";
                    if (HeadsetIDConstants.isTWS01Headset(this.mDeviceId)) {
                        str6 = commonCommand;
                        if (substring.substring(0, 2).equals("01")) {
                            if ("01".equals(substring.substring(2, 4))) {
                                str16 = "0";
                            } else if ("03".equals(substring.substring(2, 4))) {
                                str16 = "1";
                            } else if ("02".equals(substring.substring(2, 4))) {
                                str16 = "2";
                            } else {
                                str16 = "0";
                            }
                            str7 = substring3;
                            if ("01".equals(substring.substring(4, 6))) {
                                str5 = str16 + "0";
                            } else if ("03".equals(substring.substring(4, 6))) {
                                str5 = str16 + "1";
                            } else if ("02".equals(substring.substring(4, 6))) {
                                str5 = str16 + "2";
                            } else {
                                str5 = str16 + "0";
                            }
                            i5 = 0;
                            i = 2;
                        } else {
                            str7 = substring3;
                            i = 2;
                            str5 = "";
                            i5 = 0;
                        }
                        if (substring2.substring(i5, i).equals("02")) {
                            if ("03".equals(substring2.substring(i, 4))) {
                                str15 = str5 + "0";
                            } else if ("02".equals(substring2.substring(2, 4))) {
                                str15 = str5 + "1";
                            } else if ("04".equals(substring2.substring(2, 4))) {
                                str15 = str5 + "2";
                            } else if ("05".equals(substring2.substring(2, 4))) {
                                str15 = str5 + ExtraTelephony.Phonelist.TYPE_VIP;
                            } else {
                                str15 = str5 + "0";
                            }
                            if ("03".equals(substring2.substring(4, 6))) {
                                str5 = str15 + "0";
                            } else if ("02".equals(substring2.substring(4, 6))) {
                                str5 = str15 + "1";
                            } else if ("04".equals(substring2.substring(4, 6))) {
                                str5 = str15 + "2";
                            } else if ("05".equals(substring2.substring(4, 6))) {
                                str5 = str15 + ExtraTelephony.Phonelist.TYPE_VIP;
                            } else {
                                str5 = str15 + "0";
                            }
                            str10 = str7;
                            i4 = 0;
                            i3 = 2;
                        }
                        i3 = i;
                        str10 = str7;
                        i4 = 0;
                    } else {
                        str6 = commonCommand;
                        str7 = substring3;
                        if (substring.substring(0, 2).equals("01")) {
                            if (substring.substring(2, 4).equals("03")) {
                                str9 = "0";
                            } else if (substring.substring(2, 4).equals("04")) {
                                str9 = "1";
                            } else {
                                str9 = "0";
                                this.mService.setFunKey(1, Integer.parseInt("0103FF", 16), this.mDevice);
                            }
                            if (substring.substring(4, 6).equals("03")) {
                                str5 = str9 + "0";
                            } else if (substring.substring(4, 6).equals("04")) {
                                str5 = str9 + "1";
                            } else {
                                str5 = str9 + "0";
                                this.mService.setFunKey(1, Integer.parseInt("01FF03", 16), this.mDevice);
                            }
                            i2 = 0;
                            i = 2;
                        } else {
                            i = 2;
                            str5 = "";
                            i2 = 0;
                        }
                        if (substring2.substring(i2, i).equals("02")) {
                            if (substring2.substring(i, 4).equals("02")) {
                                str8 = str5 + "0";
                            } else if (substring2.substring(2, 4).equals("05")) {
                                str8 = str5 + "1";
                            } else {
                                str8 = str5 + "0";
                                this.mService.setFunKey(1, Integer.parseInt("0202FF", 16), this.mDevice);
                            }
                            if (substring2.substring(4, 6).equals("02")) {
                                str5 = str8 + "0";
                            } else if (substring2.substring(4, 6).equals("05")) {
                                str5 = str8 + "1";
                            } else {
                                str5 = str8 + "0";
                                this.mService.setFunKey(1, Integer.parseInt("02FF02", 16), this.mDevice);
                            }
                            str10 = str7;
                            i4 = 0;
                            i3 = 2;
                        }
                        i3 = i;
                        str10 = str7;
                        i4 = 0;
                    }
                    if (str10.substring(i4, i3).equals("03")) {
                        if (HeadsetIDConstants.isK77sHeadset(this.mDeviceId)) {
                            if (str10.substring(2, 4).equals("00")) {
                                str13 = str5 + "0";
                            } else if (str10.substring(2, 4).equals("01")) {
                                str13 = str5 + "1";
                            } else {
                                str13 = str5 + "1";
                                this.mService.setFunKey(1, Integer.parseInt("0301FF", 16), this.mDevice);
                            }
                            String str17 = str13 + "000";
                            if (str10.substring(4, 6).equals("00")) {
                                str14 = str17 + "0";
                            } else if (str10.substring(4, 6).equals("01")) {
                                str14 = str17 + "1";
                            } else {
                                str14 = str17 + "1";
                                this.mService.setFunKey(1, Integer.parseInt("03FF01", 16), this.mDevice);
                            }
                            str5 = str14 + "000";
                        } else {
                            if (str10.substring(2, 4).equals("00")) {
                                str11 = str5 + "0";
                            } else if (str10.substring(2, 4).equals("06")) {
                                str11 = str5 + "1";
                            } else {
                                str11 = str5 + "1";
                                this.mService.setFunKey(1, Integer.parseInt("0306FF", 16), this.mDevice);
                            }
                            commonCommand = str6;
                            String substring4 = commonCommand.substring(5, 8);
                            String substring5 = commonCommand.substring(9, 12);
                            String str18 = str11 + substring4;
                            if (str10.substring(4, 6).equals("00")) {
                                str12 = str18 + "0";
                            } else if (str10.substring(4, 6).equals("06")) {
                                str12 = str18 + "1";
                            } else {
                                str12 = str18 + "1";
                                this.mService.setFunKey(1, Integer.parseInt("03FF06", 16), this.mDevice);
                            }
                            str5 = str12 + substring5;
                        }
                    }
                    commonCommand = str6;
                } else {
                    str4 = "MiuiHeadsetFragment";
                    str5 = "";
                }
                if (str2 == "") {
                    String substring6 = commonCommand.substring(0, 5);
                    String substring7 = commonCommand.substring(8, 9);
                    String hexToBinaryString = hexToBinaryString(str);
                    str5 = substring6 + hexToBinaryString.substring(1, 4) + substring7 + hexToBinaryString.substring(5);
                }
                str3 = str4;
            } catch (Exception e) {
                e = e;
                str3 = str4;
            }
        } catch (Exception e2) {
            e = e2;
            str3 = "MiuiHeadsetFragment";
        }
        try {
            Log.d(str3, "update device init key config: " + str5);
            this.mService.setCommonCommand(105, str5, this.mDevice);
            ((MiuiHeadsetActivity) getActivity()).setDeviceConfig(str5);
        } catch (Exception e3) {
            e = e3;
            Log.e(str3, "set fun key config error: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshGyrStatus() {
        Handler handler;
        if (!this.isSupportHeadTrackAlgo || (handler = this.mHandler) == null) {
            Log.e("MiuiHeadsetFragment", "refreshGyrStatus  mHandler is null");
        } else {
            handler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.30
                @Override // java.lang.Runnable
                public void run() {
                    if (!MiuiHeadsetFragment.this.mSupportGyr.booleanValue() || MiuiHeadsetFragment.this.headTrackingCheckBox == null) {
                        return;
                    }
                    if (MiuiHeadsetFragment.this.mService == null) {
                        Log.e("MiuiHeadsetFragment", "refreshGyrStatus but mService is null");
                        return;
                    }
                    try {
                        String commonCommand = MiuiHeadsetFragment.this.mService.setCommonCommand(112, "", MiuiHeadsetFragment.this.mDevice);
                        Log.w("MiuiHeadsetFragment", " get gyr stat : " + commonCommand);
                        if (commonCommand == null || !commonCommand.equals("1")) {
                            MiuiHeadsetFragment.this.headTrackingCheckBox.setChecked(false);
                        } else {
                            MiuiHeadsetFragment.this.headTrackingCheckBox.setChecked(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 10L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshInearUi(boolean z) {
        CheckBoxPreference checkBoxPreference = this.mInearTest;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(z);
        }
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
            checkBoxPreference.setChecked(localBluetoothProfile.isEnabled(device));
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
                            Log.d("MiuiHeadsetFragment", "leAudioPre.setEnabled(false) when HFP is unavailable");
                        }
                        if ((localBluetoothProfile.getConnectionStatus(device) == 2 || this.mCachedDevice.getLeAudioStatus() == 1) && !isSCOOn() && !isLeAudioCgOn() && !this.isSingleHeadsetConn && !this.mLC3Switching) {
                            checkBoxPreference2.setEnabled(true);
                            Log.d("MiuiHeadsetFragment", "leAudioPre.setEnabled(true) when HFP/LEAuido is available");
                        }
                    }
                    if (checkBoxPreference2 != null && !TextUtils.isEmpty(string) && string.length() >= 2) {
                        char charAt = string.charAt(0);
                        char charAt2 = string.charAt(1);
                        if ((charAt == '0' && charAt2 == '1') || (charAt == '1' && charAt2 == '0')) {
                            checkBoxPreference2.setEnabled(false);
                            Log.d("MiuiHeadsetFragment", "leAudioPre.setEnabled(false) when power 01 or 10");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("MiuiHeadsetFragment", "error " + e);
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
                    Log.d("MiuiHeadsetFragment", "refreshProfiles Device support ble audio !");
                    if (SystemProperties.getBoolean("persist.vendor.service.bt.lea_test", false)) {
                        if (this.mBleAudioCategory != null) {
                            Log.d("MiuiHeadsetFragment", "refreshProfiles mBleAudioCategory not null add to show !");
                            createProfilePreference.setOrder(1);
                            this.mBleAudioCategory.addPreference(createProfilePreference);
                            getPreferenceScreen().addPreference(this.mBleAudioCategory);
                        } else {
                            Log.d("MiuiHeadsetFragment", "refreshProfiles mBleAudioCategory is null do nothing and return!");
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
                Log.d("MiuiHeadsetFragment", "Removing " + localBluetoothProfile2.toString() + " from profile list");
                this.mProfileContainer.removePreference(findPreference);
            }
        }
        showOrHideProfileGroup();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBroadcastEnableOrDisable(boolean z) {
        Log.v("MiuiHeadsetFragment", "sendBroadcastEnableOrDisable enter and value is " + z);
        try {
            Intent intent = new Intent("miui.bluetooth.absolute_volume_enable_disable");
            intent.setPackage("com.android.bluetooth");
            intent.putExtra("absolute_volume_mac", this.mCachedDevice.getAddress());
            intent.putExtra("absolute_volume_value", z);
            getActivity().sendBroadcast(intent);
        } catch (Exception e) {
            Log.v("MiuiHeadsetFragment", "send msg failed ", e);
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
        Log.i("MiuiHeadsetFragment", "sentIgnorePairDilogIntent leMac1 is " + str + " leMac2 is " + str2);
    }

    private void setAncOnClick() {
        if (this.mRootView == null) {
            return;
        }
        Log.d("MiuiHeadsetFragment", "setAncOnClick " + this.mDeviceId);
        if (HeadsetIDConstants.isK77sHeadset(this.mDeviceId)) {
            this.mRootView.findViewById(R.id.anclayout).setVisibility(8);
            return;
        }
        this.mRootView.findViewById(R.id.anclayout).setVisibility(0);
        View findViewById = this.mRootView.findViewById(R.id.transport);
        View findViewById2 = this.mRootView.findViewById(R.id.openAnc);
        View findViewById3 = this.mRootView.findViewById(R.id.closeAnc);
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.22
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MiuiHeadsetFragment.this.updateAncMode(2, true);
            }
        });
        findViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.23
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MiuiHeadsetFragment.this.updateAncMode(1, true);
            }
        });
        findViewById3.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.24
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MiuiHeadsetFragment.this.updateAncMode(0, true);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAudioShareVolume(int i) {
        BluetoothVolumeSeekBarPreference bluetoothVolumeSeekBarPreference = (BluetoothVolumeSeekBarPreference) getPreferenceScreen().findPreference("audio_share_volume_pre");
        if (bluetoothVolumeSeekBarPreference == null) {
            Log.d("MiuiHeadsetFragment", "BluetoothVolumeSeekBarPreference == null");
            return;
        }
        bluetoothVolumeSeekBarPreference.setProgress(i);
        Log.d("MiuiHeadsetFragment", "setAudioShareVolume as: " + i);
    }

    private void setBatteryClick() {
        if (this.mRootView == null) {
            return;
        }
        if (!new File(this.mLocalFile).exists()) {
            Log.d("MiuiHeadsetFragment", "found no local ota file");
            return;
        }
        this.mLocalExist = true;
        this.mRootView.findViewById(R.id.headsetBatteryInfo).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.25
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.e("MiuiHeadsetFragment", "local ota");
                System.arraycopy(MiuiHeadsetFragment.this.mHits, 1, MiuiHeadsetFragment.this.mHits, 0, MiuiHeadsetFragment.this.mHits.length - 1);
                MiuiHeadsetFragment.this.mHits[MiuiHeadsetFragment.this.mHits.length - 1] = SystemClock.uptimeMillis();
                if (MiuiHeadsetFragment.this.mLocalExist && MiuiHeadsetFragment.this.mHits[0] >= SystemClock.uptimeMillis() - 500 && MiuiHeadsetFragment.this.mLocalExist) {
                    MiuiHeadsetFragment.this.mLocalExist = false;
                    PreferenceGroup preferenceGroup = (PreferenceGroup) MiuiHeadsetFragment.this.findPreference("moreSettingsInAi");
                    if (preferenceGroup != null) {
                        Preference preference = new Preference(preferenceGroup.getContext());
                        preference.setKey("localOta");
                        preference.setTitle(MiuiHeadsetFragment.this.mActivity.getResources().getString(R.string.miheadset_local_ota));
                        preferenceGroup.addPreference(preference);
                    }
                }
            }
        });
    }

    private void setCodecInfo(int i) {
        try {
            Log.d("MiuiHeadsetFragment", "setCodec " + i);
            BluetoothCodecConfig codecConfig = getCodecConfig(this.mBluetoothA2dp, i, 1000000);
            BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
            if (bluetoothA2dp == null || codecConfig == null || this.mWorkHandler == null) {
                return;
            }
            bluetoothA2dp.setCodecConfigPreference(this.mCachedDevice.getDevice(), codecConfig);
            if (i == 2) {
                i *= -1;
            }
            this.mDevice.setSpecificCodecStatus("STORE_DEVICE_CODEC", i);
            MessageHandler messageHandler = this.mWorkHandler;
            messageHandler.sendMessageDelayed(messageHandler.obtainMessage(102), 1000L);
            DropDownPreference dropDownPreference = (DropDownPreference) findPreference("codecType");
            if (dropDownPreference == null || TextUtils.isEmpty(this.mDeviceId) || !isDeviceIdSupportSetCodec(this.mDeviceId)) {
                return;
            }
            dropDownPreference.setEnabled(false);
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "set codec failed " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDeviceAACWhiteListConfig(boolean z) {
        BluetoothDevice bluetoothDevice;
        BluetoothCodecStatus codecStatus;
        try {
            if (TextUtils.isEmpty(this.mDeviceId) || !checkPhoneCodecEnable(this.mDeviceId) || "mediatek".equals(FeatureParser.getString("vendor"))) {
                return;
            }
            BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
            boolean z2 = false;
            z2 = false;
            z2 = false;
            if (bluetoothA2dp != null && (bluetoothDevice = this.mDevice) != null && (codecStatus = bluetoothA2dp.getCodecStatus(bluetoothDevice)) != null) {
                boolean z3 = false;
                for (BluetoothCodecConfig bluetoothCodecConfig : codecStatus.getCodecsSelectableCapabilities()) {
                    if (bluetoothCodecConfig != null && bluetoothCodecConfig.getCodecType() == 1) {
                        z3 = true;
                    }
                }
                z2 = z3;
            }
            Log.d("MiuiHeadsetFragment", "device support aac: " + z2 + ", add aac list: " + z);
            if (!z || z2) {
                String address = this.mCachedDevice.getAddress();
                SystemProperties.set("persist.vendor.bt.a2dp.aac.whitelist", (!z || address == null) ? "null" : address.toLowerCase());
                if (z) {
                    addToWhiteList("persist.vendor.bt.a2dp.aac.whitelists");
                } else {
                    delFromWhiteList("persist.vendor.bt.a2dp.aac.whitelists");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOtaOnclick() {
        View view = this.mRootView;
        if (view == null) {
            return;
        }
        view.findViewById(R.id.otaLayout).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.40
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                try {
                    if (MiuiBTUtils.isNetworkAvailable(MiuiHeadsetFragment.this.mActivity)) {
                        MiuiHeadsetFragment.this.getAccountInfo(1);
                    } else {
                        ToastUtil.show(MiuiHeadsetFragment.this.mActivity, MiuiHeadsetFragment.this.mActivity.getResources().getString(R.string.miheadset_network_not_available), 1);
                    }
                } catch (Exception e) {
                    Log.e("MiuiHeadsetFragment", "error " + e);
                }
            }
        });
    }

    private void setRenameOnclick() {
        View view = this.mRootView;
        if (view == null) {
            return;
        }
        this.mRenameView = view.findViewById(R.id.renameLayout);
        this.mRenameText = (TextView) this.mRootView.findViewById(R.id.rename);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
        final LayoutInflater layoutInflater = getLayoutInflater();
        this.mRenameView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.26
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                final View inflate = layoutInflater.inflate(R.layout.rename, (ViewGroup) null);
                AlertDialog create = builder.setTitle(MiuiHeadsetFragment.this.mActivity.getString(R.string.miheadset_rename_device)).setView(inflate).setNegativeButton(MiuiHeadsetFragment.this.mActivity.getString(R.string.miheadset_cancel_device), new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.26.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton(MiuiHeadsetFragment.this.mActivity.getString(R.string.miheadset_ok_device), new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.26.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String trim = ((EditText) inflate.findViewById(R.id.renameEdit)).getText().toString().trim();
                        if (TextUtils.isEmpty(trim)) {
                            trim = MiuiHeadsetFragment.this.mCachedDevice.getName();
                        } else {
                            int length = trim.length();
                            if (trim.getBytes().length > 31) {
                                int i2 = length - 1;
                                while (true) {
                                    if (i2 <= 0) {
                                        break;
                                    } else if (trim.substring(0, i2).getBytes().length <= 31) {
                                        trim = trim.substring(0, i2);
                                        break;
                                    } else {
                                        i2--;
                                    }
                                }
                            }
                        }
                        MiuiHeadsetFragment.this.mCachedDevice.setName(trim);
                        if (!TextUtils.isEmpty(trim) && MiuiHeadsetFragment.this.mBluetoothHfp != null) {
                            String format = String.format("%02x", Integer.valueOf(trim.getBytes().length + 1));
                            boolean sendVendorSpecificResultCode = MiuiHeadsetFragment.this.mBluetoothHfp.sendVendorSpecificResultCode(MiuiHeadsetFragment.this.mDevice, "+XIAOMI", "FF01020103" + format + "01" + trim + "FF");
                            StringBuilder sb = new StringBuilder();
                            sb.append("call sendVendorSpecificResultCode(), result = ");
                            sb.append(sendVendorSpecificResultCode);
                            Log.v("MiuiHeadsetFragment", sb.toString());
                        }
                        TextView textView = (TextView) MiuiHeadsetFragment.this.mRootView.findViewById(R.id.deviceName);
                        if (textView != null) {
                            textView.setText(trim);
                        }
                        ActionBar appCompatActionBar = ((AppCompatActivity) MiuiHeadsetFragment.this.getActivity()).getAppCompatActionBar();
                        if (appCompatActionBar != null) {
                            appCompatActionBar.setTitle(trim);
                        }
                        try {
                            if (MiuiHeadsetFragment.this.mService != null && MiuiHeadsetFragment.this.mDevice != null) {
                                MiuiHeadsetFragment.this.mService.setCommonCommand(111, "", MiuiHeadsetFragment.this.mDevice);
                            }
                        } catch (Exception unused) {
                            Log.d("MiuiHeadsetFragment", "set command failed!");
                        }
                        if (MiuiHeadsetFragment.this.syncAliasToCloud(trim)) {
                            Log.v("MiuiHeadsetFragment", "syncAliasToCloud success");
                        } else {
                            Log.d("MiuiHeadsetFragment", "syncAliasToCloud failed");
                        }
                    }
                }).create();
                create.show();
                EditText editText = (EditText) create.findViewById(R.id.renameEdit);
                if (editText != null) {
                    editText.setText(MiuiHeadsetFragment.this.mDevice.getAlias());
                    editText.requestFocus();
                }
                create.getWindow().setSoftInputMode(5);
            }
        });
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

    private void startAssist() {
        Intent intent = new Intent();
        intent.setPackage("com.miui.voiceassist");
        intent.setAction("com.miui.voiceassist.FAST_CONNECT_MORE_SETTING");
        intent.putExtra("classicDeviceMac", this.mDevice.getAddress());
        intent.putExtra("launch_router_source", 2);
        intent.addFlags(268435456);
        if (this.mActivity.getPackageManager().resolveActivity(intent, SearchUpdater.GOOGLE) != null) {
            try {
                this.mActivity.startActivity(intent);
            } catch (Exception unused) {
                Log.d("MiuiHeadsetFragment", "voiceassist start fail");
            }
        } else if (this.mActivity.getPackageManager().getLaunchIntentForPackage("com.miui.voiceassist") != null) {
            Log.d("MiuiHeadsetFragment", "voiceassist app version old");
        } else {
            Log.d("MiuiHeadsetFragment", "voiceassist not found");
        }
    }

    private void startLocalOta() {
        IMiuiHeadsetService iMiuiHeadsetService = this.mService;
        if (iMiuiHeadsetService != null) {
            try {
                iMiuiHeadsetService.localOta(this.mCachedDevice.getDevice());
            } catch (Exception e) {
                Log.e("MiuiHeadsetFragment", "error " + e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean syncAliasToCloud(String str) {
        String address = this.mCachedDevice.getDevice().getAddress();
        MiuiOnSavedDeviceDataUtils miuiOnSavedDeviceDataUtils = new MiuiOnSavedDeviceDataUtils(getPrefContext());
        ContentValues queryDeviceByMac = miuiOnSavedDeviceDataUtils.queryDeviceByMac(address);
        if (queryDeviceByMac == null) {
            Log.d("MiuiHeadsetFragment", "syncAliasToCloud: contentValues is null");
            return false;
        }
        String asString = queryDeviceByMac.getAsString("name");
        if (!TextUtils.isEmpty(asString) && asString.equals(str)) {
            Log.d("MiuiHeadsetFragment", "syncAliasToCloud: name is already same");
            return true;
        }
        queryDeviceByMac.put("name", str);
        miuiOnSavedDeviceDataUtils.createAndUpdateData(queryDeviceByMac);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unpairDevice() {
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
            if (!LocalBluetoothProfileManager.isTbsProfileEnabled()) {
                this.mCachedDevice.setSpecificCodecStatus("LEAUDIO", 2);
            }
            if (equals) {
                delFromWhiteListForAbsoluteVolume("persist.vendor.bluetooth.a2dp.absolute.volume.whitelistall");
            } else if (equals2) {
                delFromWhiteListForAbsoluteVolume("persist.vendor.bt.a2dp.absolute.volume.whitelistall");
            } else {
                Log.v("MiuiHeadsetFragment", "no work to do");
            }
            unpairLeAudio();
            deleteSaveMacForLeAudio();
            setDeviceAACWhiteListConfig(false);
        } catch (Exception e) {
            Log.w("MiuiHeadsetFragment", "delFromWhiteListForAbsoluteVolume failed " + e);
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
            Log.i("MiuiHeadsetFragment", "startIndex is " + indexOf + " value is " + string);
            str2 = string.substring(indexOf + 18, indexOf + 35);
            str = string.substring(indexOf + 36, indexOf2);
            Log.i("MiuiHeadsetFragment", "leStr1 is " + str2 + " leStr2 is " + str);
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
                Log.i("MiuiHeadsetFragment", "remove bond leStr1");
            }
            if (remoteDevice2 == null || remoteDevice2.getBondState() == 10) {
                return;
            }
            if (remoteDevice2.getBondState() == 11) {
                remoteDevice2.cancelBondProcess();
            } else {
                remoteDevice2.removeBond();
            }
            Log.i("MiuiHeadsetFragment", "remove bond leStr2");
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0099, code lost:
    
        if ("0".equals(r0) == false) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x009b, code lost:
    
        r8 = r6.mActivity;
        com.android.settingslib.util.ToastUtil.show(r8, r8.getResources().getString(com.android.settings.R.string.miheadset_anc_indicate), 1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00b0, code lost:
    
        if ("0201".equals(r7) == false) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00b2, code lost:
    
        r6.mMiuiHeadsetTransparentAdjustView.setCurrentPointIndex(1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00be, code lost:
    
        if ("0200".equals(r7) == false) goto L73;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00c0, code lost:
    
        r6.mMiuiHeadsetTransparentAdjustView.setCurrentPointIndex(0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00c5, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:?, code lost:
    
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateAncLevel(java.lang.String r7, boolean r8) {
        /*
            Method dump skipped, instructions count: 330
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.MiuiHeadsetFragment.updateAncLevel(java.lang.String, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x007d, code lost:
    
        if ("0".equals(r0) == false) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x007f, code lost:
    
        r5 = r5.mActivity;
        com.android.settingslib.util.ToastUtil.show(r5, r5.getResources().getString(com.android.settings.R.string.miheadset_anc_indicate), 1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x008e, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00b1, code lost:
    
        if ("0".equals(r0) == false) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00b3, code lost:
    
        r5 = r5.mActivity;
        com.android.settingslib.util.ToastUtil.show(r5, r5.getResources().getString(com.android.settings.R.string.miheadset_anc_indicate), 1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00c2, code lost:
    
        return;
     */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00e9 A[Catch: Exception -> 0x011d, TryCatch #0 {Exception -> 0x011d, blocks: (B:2:0x0000, B:4:0x0006, B:5:0x0010, B:7:0x003f, B:10:0x004a, B:12:0x0052, B:16:0x005d, B:18:0x0065, B:44:0x00e9, B:46:0x00ed, B:48:0x00f1, B:49:0x00f3, B:55:0x011b, B:21:0x0077, B:23:0x007f, B:26:0x0091, B:28:0x0099, B:31:0x00ab, B:33:0x00b3, B:36:0x00c5, B:38:0x00cd, B:41:0x00d7, B:50:0x00f4, B:51:0x0117), top: B:59:0x0000, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x011c A[ADDED_TO_REGION, ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateAncMode(int r6, boolean r7) {
        /*
            Method dump skipped, instructions count: 294
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.MiuiHeadsetFragment.updateAncMode(int, boolean):void");
    }

    private void updateAncRTL() {
        View view = this.mRootView;
        if (view != null) {
            ((TextView) view.findViewById(R.id.ancAdapterText)).setGravity(8388611);
            String str = this.mDeviceId;
            if (str == null || !(HeadsetIDConstants.isTWS01Headset(str) || HeadsetIDConstants.isK73Headset(this.mDeviceId) || HeadsetIDConstants.isK75Headset(this.mDeviceId) || isSupportWindNoise(this.mDeviceId) || HeadsetIDConstants.isSupportZimiAdapter("anc", this.mDeviceId))) {
                ((TextView) this.mRootView.findViewById(R.id.ancLowText)).setGravity(8388611);
            } else {
                ((TextView) this.mRootView.findViewById(R.id.ancLowText)).setGravity(17);
            }
            ((TextView) this.mRootView.findViewById(R.id.ancHighText)).setGravity(8388613);
            ((TextView) this.mRootView.findViewById(R.id.enhanceVoiceText)).setGravity(8388611);
            ((TextView) this.mRootView.findViewById(R.id.transparentModeText)).setGravity(8388613);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAndEnableCode(final boolean z) {
        try {
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.47
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        MiuiHeadsetFragment.this.updateCodecIndex();
                        DropDownPreference dropDownPreference = (DropDownPreference) MiuiHeadsetFragment.this.findPreference("codecType");
                        if (dropDownPreference == null || TextUtils.isEmpty(MiuiHeadsetFragment.this.mDeviceId)) {
                            return;
                        }
                        MiuiHeadsetFragment miuiHeadsetFragment = MiuiHeadsetFragment.this;
                        if (miuiHeadsetFragment.isDeviceIdSupportSetCodec(miuiHeadsetFragment.mDeviceId)) {
                            if (MiuiHeadsetFragment.this.mBluetoothA2dp == null) {
                                dropDownPreference.setEnabled(false);
                                return;
                            }
                            BluetoothDevice activeDevice = MiuiHeadsetFragment.this.mBluetoothA2dp.getActiveDevice();
                            if (MiuiHeadsetFragment.this.mDevice == null || activeDevice == null || !activeDevice.equals(MiuiHeadsetFragment.this.mDevice)) {
                                dropDownPreference.setEnabled(false);
                                return;
                            }
                            if (z) {
                                dropDownPreference.setEnabled(true);
                            }
                            BluetoothCodecStatus codecStatus = MiuiHeadsetFragment.this.mBluetoothA2dp.getCodecStatus(MiuiHeadsetFragment.this.mDevice);
                            BluetoothCodecConfig codecConfig = codecStatus != null ? codecStatus.getCodecConfig() : null;
                            if (codecConfig == null) {
                                return;
                            }
                            int codecType = codecConfig.getCodecType();
                            if (codecType != 0 && codecType != 1 && codecType != 100) {
                                if (codecType == 10) {
                                    dropDownPreference.setEnabled(false);
                                }
                                Log.e("MiuiHeadsetFragment", "unsupport codec! " + codecType);
                                return;
                            }
                            dropDownPreference.setValue("" + codecType);
                        }
                    } catch (Exception e) {
                        Log.d("MiuiHeadsetFragment", "codec error!" + e);
                    }
                }
            }, 50L);
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "update the codec failed " + e);
        }
    }

    private void updateBatteryIcon(int i, int i2, boolean z) {
        int[] iArr = {R.drawable.battery10, R.drawable.battery20, R.drawable.battery30, R.drawable.battery40, R.drawable.battery50, R.drawable.battery60, R.drawable.battery70, R.drawable.battery80, R.drawable.battery90, R.drawable.battery100};
        int[] iArr2 = {R.drawable.battery10_charge, R.drawable.battery20_charge, R.drawable.battery30_charge, R.drawable.battery40_charge, R.drawable.battery50_charge, R.drawable.battery60_charge, R.drawable.battery70_charge, R.drawable.battery80_charge, R.drawable.battery90_charge, R.drawable.battery100_charge};
        if (z) {
            ((ImageView) this.mRootView.findViewById(i)).setImageDrawable(getResources().getDrawable(iArr2[(i2 - 1) / 10]));
        } else {
            ((ImageView) this.mRootView.findViewById(i)).setImageDrawable(getResources().getDrawable(iArr[(i2 - 1) / 10]));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCodecIndex() {
        BluetoothDevice bluetoothDevice;
        try {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
            if (bluetoothA2dp != null && (bluetoothDevice = this.mDevice) != null) {
                BluetoothCodecStatus codecStatus = bluetoothA2dp.getCodecStatus(bluetoothDevice);
                arrayList.add(String.valueOf(0));
                arrayList2.add(getActivity().getApplicationContext().getString(R.string.headset_sbc));
                if (codecStatus == null) {
                    Log.d("MiuiHeadsetFragment", "codec config is null!");
                    return;
                }
                for (BluetoothCodecConfig bluetoothCodecConfig : codecStatus.getCodecsSelectableCapabilities()) {
                    if (bluetoothCodecConfig.getCodecType() == 1) {
                        arrayList.add(String.valueOf(1));
                        arrayList2.add(getActivity().getApplicationContext().getString(R.string.headset_aac));
                    } else if (bluetoothCodecConfig.getCodecType() == 100) {
                        arrayList.add(String.valueOf(100));
                        arrayList2.add(getActivity().getApplicationContext().getString(R.string.headset_aptx_adapter));
                    }
                }
            }
            DropDownPreference dropDownPreference = (DropDownPreference) findPreference("codecType");
            if (TextUtils.isEmpty(this.mDeviceId) || dropDownPreference == null || !isDeviceIdSupportSetCodec(this.mDeviceId) || arrayList.size() <= 0 || arrayList2.size() <= 0) {
                return;
            }
            dropDownPreference.setVisible(false);
            dropDownPreference.setEntries((CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]));
            dropDownPreference.setEntryValues((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]));
            dropDownPreference.setValue(String.valueOf(0));
            dropDownPreference.setVisible(true);
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "error " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCodecStatus() {
        BluetoothCodecConfig codecConfig;
        Log.d("MiuiHeadsetFragment", "updateCodecStatus()");
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
            Log.d("MiuiHeadsetFragment", "updateCodecStatus KEY_STORE_AUDIO_SHARE_DEVICE = " + string);
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
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHeadTrackEnable() {
        if (this.mSupportGyr.booleanValue() && this.headTrackingCheckBox != null && this.isSupportHeadTrackAlgo) {
            boolean z = false;
            BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
            if (bluetoothA2dp != null && this.mBluetoothHfp != null && this.mDevice != null) {
                BluetoothDevice activeDevice = bluetoothA2dp.getActiveDevice();
                if (this.mBluetoothHfp.getConnectionState(this.mDevice) == 2 && activeDevice != null && activeDevice.equals(this.mDevice)) {
                    z = true;
                }
            }
            this.headTrackingCheckBox.setEnabled(z);
        }
    }

    private void updateLayoutMargin() {
        try {
            if (this.mRootView == null) {
                return;
            }
            Log.d("MiuiHeadsetFragment", "updateLayoutMargin() ");
            int dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_battery_marginLeft);
            updateView(this.mRootView.findViewById(R.id.batteryleft), dimensionPixelSize, 0);
            updateView(this.mRootView.findViewById(R.id.batteryright), dimensionPixelSize, 0);
            updateView(this.mRootView.findViewById(R.id.batterybox), dimensionPixelSize, 0);
            int dimensionPixelSize2 = this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_anc_level_layout_marginLeft);
            updateView(this.mRootView.findViewById(R.id.ancAdjust), dimensionPixelSize2);
            updateView(this.mRootView.findViewById(R.id.transparentAdjust), dimensionPixelSize2);
            int dimensionPixelSize3 = this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_anc_level_Text_marginLeft);
            updateView(this.mRootView.findViewById(R.id.ancAdjustText), dimensionPixelSize3);
            updateView(this.mRootView.findViewById(R.id.transparentAdjustText), dimensionPixelSize3);
            updateView(this.mRootView.findViewById(R.id.layoutDivider), this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_divider_layout_marginLeft));
            updateView(this.mRootView.findViewById(R.id.check_button), this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_ignore_marginLeft));
            updateOtaTextViewLayout();
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "error " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotificationSwitchState(final String str) {
        try {
            Log.d("MiuiHeadsetFragment", "detail notification status: " + str);
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.29
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            if (MiuiHeadsetFragment.this.mNotifiDisplay != null) {
                                if ("true".equals(str)) {
                                    MiuiHeadsetFragment.this.mNotifiDisplay.setChecked(true);
                                } else {
                                    MiuiHeadsetFragment.this.mNotifiDisplay.setChecked(false);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOtaTextViewLayout() {
        this.mRootView.findViewById(R.id.updateTextViewParent).post(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.46
            @Override // java.lang.Runnable
            public void run() {
                if (MiuiHeadsetFragment.this.mRootView != null) {
                    int dimensionPixelSize = MiuiHeadsetFragment.this.mActivity.getResources().getDimensionPixelSize(R.dimen.sim_red_dot_size);
                    int dimensionPixelSize2 = MiuiHeadsetFragment.this.mActivity.getResources().getDimensionPixelSize(R.dimen.headset_version_renameLayout_checkversion_red_margin);
                    View findViewById = MiuiHeadsetFragment.this.mRootView.findViewById(R.id.updateTextViewParent);
                    findViewById.measure(0, 0);
                    int width = findViewById.getWidth();
                    View findViewById2 = MiuiHeadsetFragment.this.mRootView.findViewById(R.id.updateTextView);
                    findViewById2.measure(0, 0);
                    Paint paint = new Paint();
                    TextView textView = (TextView) findViewById2;
                    paint.setTextSize(textView.getTextSize());
                    float measureText = paint.measureText(textView.getText().toString());
                    if (width == 0) {
                        Log.e("MiuiHeadsetFragment", "parents is 0");
                        return;
                    }
                    int i = dimensionPixelSize2 * 2;
                    if (measureText + dimensionPixelSize + i <= width) {
                        findViewById2.getLayoutParams().width = -2;
                        ViewGroup.LayoutParams layoutParams = findViewById2.getLayoutParams();
                        if (layoutParams != null) {
                            layoutParams.width = -2;
                            findViewById2.setLayoutParams(layoutParams);
                        }
                        Log.e("MiuiHeadsetFragment", "set ota WRAP_CONTENT  ");
                        return;
                    }
                    int i2 = (width - dimensionPixelSize) - i;
                    findViewById2.getLayoutParams().width = i2;
                    ViewGroup.LayoutParams layoutParams2 = findViewById2.getLayoutParams();
                    if (layoutParams2 != null) {
                        layoutParams2.width = i2;
                        findViewById2.setLayoutParams(layoutParams2);
                    }
                    Log.e("MiuiHeadsetFragment", "update to ota paraments " + i2);
                }
            }
        });
    }

    private void updateStatus(String str, String str2, final int i, final int i2, final int i3, String str3) {
        validateVIDPID(str, str2);
        String[] split = str3.split("\\+");
        if (split != null && split.length == 2) {
            this.mFwVersionCode = Integer.parseInt(split[0]);
            this.mFwVersion = split[1];
        }
        this.mBattery_left = i;
        this.mBattery_right = i3;
        this.mBattery_box = i2;
        this.mVersion = this.mFwVersion;
        if (this.mHandler != null) {
            if ("CN".equals(MiuiBTUtils.getRegion())) {
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.34
                    @Override // java.lang.Runnable
                    public void run() {
                        String str4;
                        MiuiHeadsetFragment miuiHeadsetFragment = MiuiHeadsetFragment.this;
                        miuiHeadsetFragment.refreshStatusUi(i, i3, i2, miuiHeadsetFragment.mFwVersion);
                        MiuiHeadsetFragment miuiHeadsetFragment2 = MiuiHeadsetFragment.this;
                        if (miuiHeadsetFragment2.getXiaomiAccount(miuiHeadsetFragment2.mActivity) == null) {
                            Log.d("MiuiHeadsetFragment", "device had not login");
                            return;
                        }
                        try {
                            str4 = Settings.System.getString(MiuiHeadsetFragment.this.mActivity.getContentResolver(), "com.xiaomi.bluetooth.headset.account");
                        } catch (Exception unused) {
                            Log.i("MiuiHeadsetFragment", "the account has not been set");
                            str4 = "";
                        }
                        if ("forbid".equals(str4)) {
                            return;
                        }
                        MiuiHeadsetFragment.this.getAccountInfo(-1);
                    }
                }, 10L);
            } else {
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.35
                    @Override // java.lang.Runnable
                    public void run() {
                        MiuiHeadsetFragment miuiHeadsetFragment = MiuiHeadsetFragment.this;
                        miuiHeadsetFragment.refreshStatusUi(i, i3, i2, miuiHeadsetFragment.mFwVersion);
                        MiuiHeadsetFragment.this.getAccountInfo(-1);
                    }
                }, 10L);
            }
        }
    }

    private void updateView(View view, int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.setMarginEnd(i);
        marginLayoutParams.setMarginStart(i);
        view.setLayoutParams(marginLayoutParams);
    }

    private void updateView(View view, int i, int i2) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.setMarginStart(i);
        marginLayoutParams.setMarginEnd(i2);
        view.setLayoutParams(marginLayoutParams);
    }

    private void validateVIDPID(String str, String str2) {
        int length;
        int length2;
        this.mVid = str;
        this.mPid = str2;
        Log.d("MiuiHeadsetFragment", "1 mVid=" + this.mVid + " mPid=" + this.mPid);
        if (!TextUtils.isEmpty(this.mVid) && (length2 = this.mVid.length()) != 4) {
            if (length2 == 3) {
                this.mVid = "0" + this.mVid;
            } else if (length2 == 2) {
                this.mVid = "00" + this.mVid;
            } else if (length2 == 1) {
                this.mVid = "000" + this.mVid;
            }
        }
        if (!TextUtils.isEmpty(this.mPid) && (length = this.mPid.length()) != 4) {
            if (length == 3) {
                this.mPid = "0" + this.mPid;
            } else if (length == 2) {
                this.mPid = "00" + this.mPid;
            } else if (length == 1) {
                this.mPid = "000" + this.mPid;
            }
        }
        Log.d("MiuiHeadsetFragment", "2 mVid=" + this.mVid + " mPid=" + this.mPid);
    }

    private <V> void waitAndShowFutureResult(final XiaomiOAuthFuture<V> xiaomiOAuthFuture, final int i) {
        this.waitResultTask = new AsyncTask<Void, Void, V>() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.41
            Exception e;

            private void setAccountForbid() {
                try {
                    Settings.System.putString(MiuiHeadsetFragment.this.mActivity.getContentResolver(), "com.xiaomi.bluetooth.headset.account", "forbid");
                } catch (Exception e) {
                    Log.e("MiuiHeadsetFragment", " setAccount " + e);
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public V doInBackground(Void... voidArr) {
                try {
                    return (V) xiaomiOAuthFuture.getResult();
                } catch (OperationCanceledException e) {
                    this.e = e;
                    this.setAccountForbid();
                    return null;
                } catch (XMAuthericationException e2) {
                    this.e = e2;
                    return null;
                } catch (IOException e3) {
                    this.e = e3;
                    return null;
                }
            }

            @Override // android.os.AsyncTask
            protected void onPostExecute(V v) {
                if (v != null && (v instanceof XiaomiOAuthResults)) {
                    MiuiHeadsetFragment.this.mAccountResult = (XiaomiOAuthResults) v;
                    try {
                        Settings.System.putString(MiuiHeadsetFragment.this.mActivity.getContentResolver(), "com.xiaomi.bluetooth.headset.account", "");
                    } catch (Exception unused) {
                        Log.d("MiuiHeadsetFragment", "clean account set");
                    }
                }
                MiuiHeadsetFragment.this.mWorkHandler.sendMessage(MiuiHeadsetFragment.this.mWorkHandler.obtainMessage(100, i, 0));
            }

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
            }
        }.executeOnExecutor(this.mExecutor, new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x001b, code lost:
    
        if (r9 != false) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0024, code lost:
    
        if (r9 != false) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x002c, code lost:
    
        if (r9 != false) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0012, code lost:
    
        if (r9 != false) goto L28;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void writeBluetoothA2dpConfiguration(boolean r9) {
        /*
            Method dump skipped, instructions count: 246
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.MiuiHeadsetFragment.writeBluetoothA2dpConfiguration(boolean):void");
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
        } else if (this.mLDACDevice) {
            SystemProperties.set("persist.vendor.bt.a2dp.ldac.enabled", z ? "true" : "false");
        } else if (this.mAACDevice) {
            String address = this.mCachedDevice.getAddress();
            SystemProperties.set("persist.vendor.bt.a2dp.aac.whitelist", (!z || address == null) ? "null" : address.toLowerCase());
            if (z) {
                addToWhiteList("persist.vendor.bt.a2dp.aac.whitelists");
            } else {
                delFromWhiteList("persist.vendor.bt.a2dp.aac.whitelists");
            }
        }
    }

    public void deviceReportInfoAnc(final String str) {
        Log.e("MiuiHeadsetFragment", "deviceReportInfoAnc: " + str);
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.39
                @Override // java.lang.Runnable
                public void run() {
                    MiuiHeadsetFragment.this.updateAncUi(str, false);
                }
            }, 10L);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void finish() {
        super.finish();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiHeadsetFragment.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.headsetLayout;
    }

    public void initHandler() {
        HandlerThread handlerThread = new HandlerThread("HeadSetFragment");
        this.mThread = handlerThread;
        handlerThread.start();
        this.mWorkHandler = new MessageHandler(this.mThread.getLooper());
    }

    public boolean isDeviceIdSupportSetCodec(String str) {
        String[] strArr = supportSetCodecDeviceId;
        if (strArr == null || strArr.length <= 0) {
            return false;
        }
        return !TextUtils.isEmpty(str) && Arrays.asList(strArr).contains(str);
    }

    public boolean isHfpConnected() {
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHfp;
        return bluetoothHeadset != null && bluetoothHeadset.getConnectionState(this.mDevice) == 2;
    }

    @Override // com.android.settings.bluetooth.MiuiHeadsetAncAdjustView.AncLevelChangeListener
    public void onAncLevelChange(int i) {
        Log.d("MiuiHeadsetFragment", "onAncLevelChange " + i);
        if (i == 0) {
            if (HeadsetIDConstants.isTWS01Headset(this.mDeviceId) || HeadsetIDConstants.isK73Headset(this.mDeviceId) || HeadsetIDConstants.isSupportZimiAdapter("anc", this.mDeviceId)) {
                updateAncLevel("0103", true);
            } else {
                updateAncLevel("0101", true);
            }
        } else if (i == 1) {
            if (HeadsetIDConstants.isTWS01Headset(this.mDeviceId) || HeadsetIDConstants.isK73Headset(this.mDeviceId) || HeadsetIDConstants.isSupportZimiAdapter("anc", this.mDeviceId)) {
                updateAncLevel("0101", true);
            } else {
                updateAncLevel("0100", true);
            }
        } else if (i == 2) {
            if (HeadsetIDConstants.isTWS01Headset(this.mDeviceId) || HeadsetIDConstants.isK73Headset(this.mDeviceId) || HeadsetIDConstants.isSupportZimiAdapter("anc", this.mDeviceId)) {
                updateAncLevel("0100", true);
            } else {
                updateAncLevel("0102", true);
            }
        } else if (i != 3) {
        } else {
            if (HeadsetIDConstants.isTWS01Headset(this.mDeviceId) || HeadsetIDConstants.isK73Headset(this.mDeviceId) || HeadsetIDConstants.isSupportZimiAdapter("anc", this.mDeviceId)) {
                updateAncLevel("0102", true);
            } else if (isSupportWindNoise(this.mDeviceId)) {
                updateAncLevel("0104", true);
            } else if (HeadsetIDConstants.isK75Headset(this.mDeviceId)) {
                updateAncLevel("0104", true);
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = getActivity();
        MiuiHeadsetActivity miuiHeadsetActivity = (MiuiHeadsetActivity) activity;
        this.mDevice = miuiHeadsetActivity.getDevice();
        this.mSupport = miuiHeadsetActivity.getSupport();
    }

    @Override // miuix.preference.PreferenceFragment, androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        updateLayoutMargin();
        super.onConfigurationChanged(configuration);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mAudioManager = (AudioManager) getActivity().getSystemService("audio");
        if (getArguments() != null) {
            this.mDevice = (BluetoothDevice) getArguments().getParcelable("BT_Device");
            this.mSupport = getArguments().getString("BT_Device_Support");
            Log.d("MiuiHeadsetFragment", "getArguments(), mDevice: " + this.mDevice + " mSupport: " + this.mSupport);
        }
        BluetoothDevice bluetoothDevice = this.mDevice;
        addPreferencesFromResource(R.xml.headsetLayout);
        getPreferenceScreen().setOrderingAsAdded(false);
        this.mProfileContainer = (PreferenceGroup) findPreference("profile_container");
        this.mCodecContainer = (PreferenceGroup) findPreference("ldac_container");
        if (bluetoothDevice == null) {
            Log.w("MiuiHeadsetFragment", "Activity started without a remote Bluetooth device");
            finish();
            return;
        }
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(this.mActivity);
        this.mManager = localBtManager;
        CachedBluetoothDeviceManager cachedDeviceManager = localBtManager.getCachedDeviceManager();
        this.mProfileManager = this.mManager.getProfileManager();
        CachedBluetoothDevice findDevice = cachedDeviceManager.findDevice(bluetoothDevice);
        this.mCachedDevice = findDevice;
        if (findDevice == null) {
            CachedBluetoothDevice addDevice = cachedDeviceManager.addDevice(this.mDevice);
            this.mCachedDevice = addDevice;
            if (addDevice == null) {
                Log.e("MiuiHeadsetFragment", "cacheddevice is null");
                finish();
                return;
            }
        }
        this.mCachedDevice.getName();
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
        this.mAudioShareContainer = (PreferenceGroup) findPreference("audio_share_container");
        if (FeatureParser.getBoolean("support_audio_share", false)) {
            addPreferencesForAudioShare();
            Log.d("MiuiHeadsetFragment", "SUPPORT_AUDIO_SHARE_FEATURE == true");
        } else if (getPreferenceScreen().findPreference("audio_share_container") != null) {
            getPreferenceScreen().removePreference(this.mAudioShareContainer);
        }
        addPreferencesForProfiles();
        try {
            boolean equals = "mediatek".equals(FeatureParser.getString("vendor"));
            boolean equals2 = "qcom".equals(FeatureParser.getString("vendor"));
            AudioManager audioManager = (AudioManager) getActivity().getSystemService("audio");
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
                Log.v("MiuiHeadsetFragment", "addPreferencesForAbsoluteVolume null");
            }
        } catch (Exception e) {
            Log.w("MiuiHeadsetFragment", "addPreferencesForAbsoluteVolume failed " + e);
        }
        if (this.mIsInAbsWhitelist && this.mAbsVolFeature.equals("true")) {
            addPreferencesForAbsoluteVolume();
            Log.w("MiuiHeadsetFragment", "addPreferencesForAbsoluteVolume on create");
        }
        boolean isLeAudioBrDevice = isLeAudioBrDevice(this.mDeviceMacAddress);
        this.mIsBleAudioDevice = isLeAudioBrDevice;
        if (isLeAudioBrDevice) {
            addPreferencesForLeAudio();
        }
        addPreferencesForSpecialCodec();
        loadDevice();
        initSpatialAudioPreferences();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        BluetoothDevice bluetoothDevice;
        CheckBoxPreference checkBoxPreference;
        CheckBoxPreference checkBoxPreference2;
        Log.d("MiuiHeadsetFragment", "onCreateView");
        if (this.mDevice == null) {
            Log.e("MiuiHeadsetFragment", "mDevice is null in onCreateView()");
            finish();
            return null;
        }
        int i = getResources().getConfiguration().orientation;
        View inflate = layoutInflater.inflate(R.layout.headsetLayout, viewGroup, false);
        this.mRootView = inflate;
        ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
        this.mMiuiHeadsetAncAdjustView = (MiuiHeadsetAncAdjustView) this.mRootView.findViewById(R.id.ancAdjustView);
        if (HeadsetIDConstants.isTWS01Headset(this.mDeviceId) || HeadsetIDConstants.isK73Headset(this.mDeviceId) || HeadsetIDConstants.isK75Headset(this.mDeviceId) || HeadsetIDConstants.isSupportZimiAdapter("anc", this.mDeviceId)) {
            this.mMiuiHeadsetAncAdjustView.setPointCount(4);
        }
        this.mMiuiHeadsetAncAdjustView.setAncLevelChangeListener(this);
        MiuiHeadsetAncAdjustView miuiHeadsetAncAdjustView = (MiuiHeadsetAncAdjustView) this.mRootView.findViewById(R.id.ancAdjustView2);
        this.mMiuiHeadsetAncAdjustViewWindNoise = miuiHeadsetAncAdjustView;
        miuiHeadsetAncAdjustView.setPointCount(4);
        this.mMiuiHeadsetAncAdjustViewWindNoise.setAncLevelChangeListener(this);
        MiuiHeadsetTransparentAdjustView miuiHeadsetTransparentAdjustView = (MiuiHeadsetTransparentAdjustView) this.mRootView.findViewById(R.id.transparentAdjustView);
        this.mMiuiHeadsetTransparentAdjustView = miuiHeadsetTransparentAdjustView;
        miuiHeadsetTransparentAdjustView.setTransparentLevelChangeListener(this);
        setRenameOnclick();
        initDeviceName();
        setOtaOnclick();
        initResource();
        setAncOnClick();
        setBatteryClick();
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        initHandler();
        initButton();
        updateAndEnableCode(true);
        PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("bleShareAudioCategory");
        this.mBleAudioCategory = preferenceGroup;
        if (preferenceGroup != null) {
            Log.d("MiuiHeadsetFragment", "mBleAudioCategory not null and default remove it !");
            getPreferenceScreen().removePreference(this.mBleAudioCategory);
        } else {
            Log.d("MiuiHeadsetFragment", "mBleAudioCategory is null");
        }
        if (this.mPrefConfig == null && (bluetoothDevice = this.mDevice) != null && this.mActivity != null && (checkBoxPreference = this.mInearTest) != null && (checkBoxPreference2 = this.mAutoAck) != null) {
            MiuiHeadsetPreferenceConfig miuiHeadsetPreferenceConfig = new MiuiHeadsetPreferenceConfig(bluetoothDevice, checkBoxPreference, checkBoxPreference2);
            this.mPrefConfig = miuiHeadsetPreferenceConfig;
            miuiHeadsetPreferenceConfig.initPreferenceConfig(this.mActivity);
        }
        addHdAudio();
        MessageHandler messageHandler = this.mWorkHandler;
        messageHandler.sendMessageDelayed(messageHandler.obtainMessage(101), 500L);
        try {
            IMiuiHeadsetService service = ((MiuiHeadsetActivity) this.mActivity).getService();
            this.mService = service;
            if (service != null && this.mBluetoothHfp != null) {
                service.connect(this.mDevice);
                MessageHandler messageHandler2 = this.mWorkHandler;
                messageHandler2.sendMessage(messageHandler2.obtainMessage(104));
            }
            MessageHandler messageHandler3 = this.mWorkHandler;
            messageHandler3.sendMessage(messageHandler3.obtainMessage(107));
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "miui headset activity service error " + e);
        }
        return this.mRootView;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        Log.d("MiuiHeadsetFragment", "Destory ");
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
        AsyncTask asyncTask = this.waitResultTask;
        if (asyncTask != null && !asyncTask.isCancelled()) {
            this.waitResultTask.cancel(false);
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacks(null);
        }
        HandlerThread handlerThread = this.mThread;
        if (handlerThread != null) {
            handlerThread.quit();
        }
    }

    @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
        refresh();
    }

    @Override // androidx.fragment.app.Fragment
    public void onHiddenChanged(boolean z) {
        String str;
        super.onHiddenChanged(z);
        if (!z) {
            try {
                if (this.mService == null) {
                    this.mService = ((MiuiHeadsetActivity) this.mActivity).getService();
                    if (this.mBluetoothHfp != null) {
                        MessageHandler messageHandler = this.mWorkHandler;
                        messageHandler.sendMessage(messageHandler.obtainMessage(104));
                    }
                    this.mService.setCommonCommand(107, "", this.mDevice);
                }
            } catch (Exception e) {
                Log.e("MiuiHeadsetFragment", "onHiddenChanged set service error " + e);
            }
        }
        if (!z && this.mDevice == null) {
            this.mDevice = ((MiuiHeadsetActivity) this.mActivity).getDevice();
        }
        ActionBar appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        if (z || appCompatActionBar == null) {
            return;
        }
        appCompatActionBar.setTitle(this.mDevice.getAlias());
        if (this.mBattery_left != 0 && this.mBattery_right != 0 && this.mBattery_box != 0 && (str = this.mVersion) != null && !"".equals(str)) {
            refreshStatusUi(this.mBattery_left, this.mBattery_right, this.mBattery_box, this.mVersion);
        }
        try {
            IMiuiHeadsetService iMiuiHeadsetService = this.mService;
            if (iMiuiHeadsetService != null) {
                iMiuiHeadsetService.getDeviceConfig(this.mDevice);
            }
        } catch (Exception e2) {
            Log.e("MiuiHeadsetFragment", "miui headset getDeviceConfig error " + e2);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Log.d("MiuiHeadsetFragment", "pause ");
        this.mCachedDevice.unregisterCallback(this);
        this.mManager.setForegroundActivity(null);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            return;
        }
        Log.d("MiuiHeadsetFragment", "set scan mode connectable");
        defaultAdapter.setScanMode(21);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Activity activity;
        Activity activity2;
        try {
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "error " + e);
        }
        if (this.mService == null) {
            Log.e("MiuiHeadsetFragment", "preference changed service is null");
            return false;
        }
        String key = preference.getKey();
        char c = 65535;
        switch (key.hashCode()) {
            case -2103013767:
                if (key.equals("AudioMode")) {
                    c = 4;
                    break;
                }
                break;
            case -2062328109:
                if (key.equals("hd_audio")) {
                    c = 6;
                    break;
                }
                break;
            case -1124263824:
                if (key.equals("codecType")) {
                    c = 2;
                    break;
                }
                break;
            case -894162691:
                if (key.equals("AutoAckMode")) {
                    c = 1;
                    break;
                }
                break;
            case 958163668:
                if (key.equals("MultiConnectMode")) {
                    c = 3;
                    break;
                }
                break;
            case 1047904631:
                if (key.equals("notificationdisplay")) {
                    c = 5;
                    break;
                }
                break;
            case 1964843011:
                if (key.equals("Ineartest")) {
                    c = 0;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                MiuiHeadsetPreferenceConfig miuiHeadsetPreferenceConfig = this.mPrefConfig;
                if (miuiHeadsetPreferenceConfig != null && (activity = this.mActivity) != null) {
                    miuiHeadsetPreferenceConfig.setSharedPref((Boolean) obj, "InEarTestPref", activity);
                }
                setInEarStatus();
                return true;
            case 1:
                if (this.mAutoAck == null || !((Boolean) obj).booleanValue()) {
                    this.mService.setCommonCommand(3, "00", this.mDevice);
                } else {
                    this.mService.setCommonCommand(3, "01", this.mDevice);
                }
                MiuiHeadsetPreferenceConfig miuiHeadsetPreferenceConfig2 = this.mPrefConfig;
                if (miuiHeadsetPreferenceConfig2 != null && (activity2 = this.mActivity) != null) {
                    miuiHeadsetPreferenceConfig2.setSharedPref((Boolean) obj, "AutoAckModePref", activity2);
                }
                return true;
            case 2:
                setCodecInfo(Integer.parseInt((String) obj));
                break;
            case 3:
                if (this.mMultiConnect == null || !((Boolean) obj).booleanValue()) {
                    this.mService.setCommonCommand(4, "00", this.mDevice);
                } else {
                    this.mService.setCommonCommand(4, "01", this.mDevice);
                }
                return true;
            case 4:
                this.mService.setCommonCommand(1, (String) obj, this.mDevice);
                return true;
            case 5:
                if (this.mNotifiDisplay != null) {
                    String valueOf = String.valueOf((Boolean) obj);
                    BluetoothDevice bluetoothDevice = this.mDevice;
                    if (bluetoothDevice != null) {
                        this.mService.setCommonCommand(114, valueOf, bluetoothDevice);
                    }
                }
                return true;
            case 6:
                this.mHDValue = "10|" + (((Boolean) obj).booleanValue() ? 1 : 0);
                MessageHandler messageHandler = this.mWorkHandler;
                messageHandler.sendMessage(messageHandler.obtainMessage(106));
                return true;
        }
        if (!(preference instanceof CheckBoxPreference)) {
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
        key.hashCode();
        char c = 65535;
        switch (key.hashCode()) {
            case -1682991092:
                if (key.equals("voicetraining")) {
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
            case -361203580:
                if (key.equals("mi_headset_loss_dialog")) {
                    c = 3;
                    break;
                }
                break;
            case 63949090:
                if (key.equals("key_config")) {
                    c = 4;
                    break;
                }
                break;
            case 865394673:
                if (key.equals("fitness_check")) {
                    c = 5;
                    break;
                }
                break;
            case 1900780465:
                if (key.equals("localOta")) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                startAssist();
                break;
            case 1:
                Log.d("MiuiHeadsetFragment", "preference clicked KEY_BLE_ADUIO_BROADCAST_ADD");
                Bundle bundle = new Bundle();
                bundle.putParcelable("device", this.mCachedDevice.getDevice());
                MiuiUtils.startPreferencePanel(getActivity(), "com.android.settings.bluetooth.MiuiBluetoothShareBroadcastFragment", bundle, R.string.bluetooth_share_broadcast, null, null, 0);
                break;
            case 2:
                Log.d("MiuiHeadsetFragment", "preference clicked KEY_BLE_ADUIO_SHARE_BROADCAST_SWITCH");
                break;
            case 3:
                miHeadsetLost();
                break;
            case 4:
                gotoKeyConfigFragment();
                break;
            case 5:
                gotoFitnessFragment();
                break;
            case 6:
                startLocalOta();
                break;
        }
        return false;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:108:0x022e -> B:131:0x0234). Please submit an issue!!! */
    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        DropDownPreference dropDownPreference;
        String string;
        BluetoothHeadset bluetoothHeadset;
        super.onResume();
        Log.d("MiuiHeadsetFragment", "onResume ");
        if (this.mMiuiHeadsetAnimation == null) {
            this.mMiuiHeadsetAnimation = new MiuiHeadsetAnimation(this.mDeviceId, getActivity(), this.mRootView, this.mHandler, this.mService, this.mWorkHandler);
        }
        this.mInitedAtUi = false;
        BluetoothHeadset bluetoothHeadset2 = this.mBluetoothHfp;
        if (bluetoothHeadset2 == null || !(bluetoothHeadset2 == null || bluetoothHeadset2.getConnectionState(this.mDevice) == 2)) {
            refreshInDisconnect();
        } else if (this.mWorkHandler != null && (bluetoothHeadset = this.mBluetoothHfp) != null && bluetoothHeadset.getConnectionState(this.mDevice) == 2) {
            MessageHandler messageHandler = this.mWorkHandler;
            messageHandler.sendMessage(messageHandler.obtainMessage(104));
        }
        if (k73HDAudioEable(this.mDeviceId)) {
            MessageHandler messageHandler2 = this.mWorkHandler;
            messageHandler2.sendMessage(messageHandler2.obtainMessage(105));
        }
        this.mManager.setForegroundActivity(this.mActivity);
        CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        if (cachedBluetoothDevice != null) {
            cachedBluetoothDevice.registerCallback(this);
            if (this.mCachedDevice.getBondState() == 10) {
                Log.e("MiuiHeadsetFragment", "error for bond state " + this.mCachedDevice.getBondState());
            }
        }
        refresh();
        updateLayoutMargin();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceScreen().findPreference("abs_volume_pre");
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) getPreferenceScreen().findPreference("le_audio_pre");
        if (checkBoxPreference != null) {
            Settings.Secure.getString(getActivity().getContentResolver(), "miui_store_audio_share_device_address");
            if (this.mCachedDevice.isActiveDevice(2) || (checkBoxPreference2 != null && ((this.mCachedDevice.getSpecificCodecStatus("LEAUDIO") == 1 || this.mCachedDevice.getLeAudioStatus() == 1) && this.mAbsAudioManager != null))) {
                Log.i("MiuiHeadsetFragment", "on resume change state");
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
            setDeviceAACWhiteListConfig(true);
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            Log.d("MiuiHeadsetFragment", "set scan mode connectable and discoverable");
            defaultAdapter.setScanMode(23);
        }
        boolean z = this.mLHDCV3Device && this.mCachedDevice.getSpecificCodecStatus("LHDC_V3") == 1;
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
            if (checkBoxPreference5 != null) {
                if (this.mLHDCV3Device) {
                    checkBoxPreference5.setEnabled(z);
                } else {
                    checkBoxPreference5.setEnabled(true);
                }
            }
            try {
                CheckBoxPreference checkBoxPreference6 = (CheckBoxPreference) getPreferenceScreen().findPreference("hd_audio");
                if (checkBoxPreference6 != null) {
                    if (this.mLHDCV3Device) {
                        checkBoxPreference6.setEnabled(z);
                    } else {
                        PreferenceGroup preferenceGroup = this.mCodecContainer;
                        if (preferenceGroup != null) {
                            preferenceGroup.removePreference(checkBoxPreference6);
                        }
                    }
                }
            } catch (Exception unused) {
                Log.d("MiuiHeadsetFragment", "prefHdAudio error!");
            }
        }
        if (this.mLHDCV3Device && (dropDownPreference = this.configCodec) != null) {
            dropDownPreference.setEnabled(!z);
        }
        if (LocalBluetoothProfileManager.isTbsProfileEnabled() && this.mCachedDevice.isDualModeDevice()) {
            if (checkBoxPreference2 != null && (isSCOOn() || isLeAudioCgOn())) {
                checkBoxPreference2.setEnabled(false);
                Log.d("MiuiHeadsetFragment", "leAudioPre.setEnabled(false) when calling");
            }
            if (checkBoxPreference2 == null || isHfpConnected() || this.mCachedDevice.getLeAudioStatus() == 1) {
                return;
            }
            checkBoxPreference2.setEnabled(false);
            Log.d("MiuiHeadsetFragment", "leAudioPre.setEnabled(false) when HFP is unavailable x");
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("device", this.mCachedDevice.getDevice());
    }

    public void onServiceConnected() {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        try {
            if (this.mService == null) {
                this.mService = ((MiuiHeadsetActivity) this.mActivity).getService();
                if (this.mBluetoothHfp != null) {
                    MessageHandler messageHandler = this.mWorkHandler;
                    messageHandler.sendMessage(messageHandler.obtainMessage(104));
                }
                String commonCommand = this.mService.setCommonCommand(110, "", this.mDevice);
                Log.d("MiuiHeadsetFragment", "get local version code: " + commonCommand);
                if (!TextUtils.isEmpty(commonCommand)) {
                    this.mVersionCodeLocal = Integer.parseInt(commonCommand);
                }
                MessageHandler messageHandler2 = this.mWorkHandler;
                messageHandler2.sendMessage(messageHandler2.obtainMessage(107));
                if (k73HDAudioEable(this.mDeviceId)) {
                    MessageHandler messageHandler3 = this.mWorkHandler;
                    messageHandler3.sendMessage(messageHandler3.obtainMessage(105));
                }
            }
            if (this.mDevice == null) {
                this.mDevice = ((MiuiHeadsetActivity) this.mActivity).getDevice();
            }
            this.mMiuiHeadsetAnimation.updateService(this.mService);
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "activity define service error " + e);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Log.d("MiuiHeadsetFragment", "onStart()");
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED");
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.PLAYING_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED");
        intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
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
        getProfileProxy();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        Log.d("MiuiHeadsetFragment", "onstop ");
        getActivity().unregisterReceiver(this.mBluetoothA2dpReceiver);
        if (FeatureParser.getBoolean("support_audio_share", false)) {
            getActivity().unregisterReceiver(this.mBluetoothMultiA2DPStateResultReceiver);
            getActivity().unregisterReceiver(this.mBluetoothHfpAudioStateReceiver);
        } else if (LocalBluetoothProfileManager.isTbsProfileEnabled() && this.mCachedDevice.isDualModeDevice()) {
            try {
                getActivity().unregisterReceiver(this.mBluetoothHfpAudioStateReceiver);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("MiuiHeadsetFragment", "error " + e);
            }
        }
        closeProfileProxy(0);
    }

    @Override // com.android.settings.bluetooth.MiuiHeadsetTransparentAdjustView.TransparentLevelChangeListener
    public void onTransparentLevelChange(int i) {
        Log.d("MiuiHeadsetFragment", "onTransparentLevelChange " + i);
        if (i == 0) {
            updateAncLevel("0201", true);
        } else if (i != 1) {
        } else {
            updateAncLevel("0200", true);
        }
    }

    public void refreshConfigInfo(final int i, final String str) {
        Log.d("MiuiHeadsetFragment", "receiver message" + str + "type " + i);
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.38
                @Override // java.lang.Runnable
                public void run() {
                    int i2 = i;
                    if (i2 == 3) {
                        if (MiuiHeadsetFragment.this.mAutoAck != null) {
                            MiuiHeadsetFragment.this.mAutoAck.setChecked("01".equals(str));
                        }
                    } else if (i2 == 4 && MiuiHeadsetFragment.this.mMultiConnect != null) {
                        MiuiHeadsetFragment.this.mMultiConnect.setChecked("01".equals(str));
                    }
                }
            }, 10L);
        }
    }

    public void refreshFunKeyInfo(final String str, final String str2) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.36
                @Override // java.lang.Runnable
                public void run() {
                    MiuiHeadsetFragment.this.refreshDeviceFunKeyInfo(str, str2);
                }
            }, 10L);
        }
    }

    void refreshInDisconnect() {
        CheckBoxPreference checkBoxPreference;
        CheckBoxPreference checkBoxPreference2;
        CheckBoxPreference checkBoxPreference3;
        try {
            ((TextView) getView().findViewById(R.id.leftBattery)).setText("-");
            ((TextView) getView().findViewById(R.id.rightBattery)).setText("-");
            ((TextView) getView().findViewById(R.id.boxBattery)).setText("-");
            ((TextView) getView().findViewById(R.id.versionName)).setText(this.mActivity.getResources().getString(R.string.miheadset_connectHeadsetForFw));
            updateBatteryIcon(R.id.imageLeftBattery, 100, false);
            updateBatteryIcon(R.id.imageBoxBattery, 100, false);
            updateBatteryIcon(R.id.imageRightBattery, 100, false);
            updateAncUi("-1", false);
            this.mRootView.findViewById(R.id.otaLayout).setEnabled(false);
            this.mRootView.findViewById(R.id.red_dot).setVisibility(4);
            this.mOtaIndicate = Boolean.FALSE;
            this.mInitedAtUi = false;
            if (this.mSupportGyr.booleanValue() && (checkBoxPreference3 = this.headTrackingCheckBox) != null && this.isSupportHeadTrackAlgo) {
                checkBoxPreference3.setEnabled(false);
            }
            if (this.mSupportInear.booleanValue() && (checkBoxPreference2 = this.mInearTest) != null) {
                checkBoxPreference2.setEnabled(false);
            }
            if (!this.mShowAutoAck.booleanValue() || (checkBoxPreference = this.mAutoAck) == null) {
                return;
            }
            checkBoxPreference.setEnabled(false);
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "error " + e);
        }
    }

    public void refreshInearInfo(final boolean z) {
        Log.e("MiuiHeadsetFragment", "get inear succeed" + z);
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.37
                @Override // java.lang.Runnable
                public void run() {
                    MiuiHeadsetFragment.this.refreshInearUi(z);
                }
            }, 10L);
        }
    }

    public void refreshStatus(String str, String str2) {
        BluetoothDevice bluetoothDevice;
        String str3 = str;
        try {
            Log.d("MiuiHeadsetFragment", "refreshStatus address@" + str3 + ",callbackData=>" + str2);
            if (!TextUtils.isEmpty(str) && this.mDevice != null) {
                if (str3.startsWith("ONLINEVERSION")) {
                    JSONObject jSONObject = new JSONObject(str2);
                    this.mLastOnlineVerion = jSONObject.optString("versionDisplayName");
                    this.mLastOnlineVersionCode = Integer.parseInt(jSONObject.optString("version"), 16);
                    this.mLastOnlineUrl = jSONObject.optString("url");
                    this.mLastOnlineMessage = jSONObject.optString("versionDisplayDescription");
                    handleOtaInfo(str3.replace("ONLINEVERSION", ""));
                } else if (str3.startsWith("SERVER_NOT_ACCESSIBLE")) {
                    handleOtaInfo(str3.replace("SERVER_NOT_ACCESSIBLE", ""));
                } else if (str3.startsWith("hd_audio")) {
                    handleHdAudio(str3.replace("hd_audio", ""), str2);
                } else if (str3.startsWith("FIRST_INFOS")) {
                    String replace = str3.replace("FIRST_INFOS", "");
                    if (!TextUtils.isEmpty(replace) && this.mDevice.getAddress().equalsIgnoreCase(replace)) {
                        updateAtUiInfo(str2);
                        return;
                    }
                    Log.d("MiuiHeadsetFragment", "AT callback data is not belong to the device");
                } else {
                    if (str3.startsWith("GYR_CTRL")) {
                        str3 = str3.replace("GYR_CTRL", "");
                        if (this.mDevice.getAddress().equalsIgnoreCase(str3) && this.isSupportHeadTrackAlgo && this.mHandler != null) {
                            if (str2 == null || !str2.equals("1")) {
                                this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.33
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        if (MiuiHeadsetFragment.this.headTrackingCheckBox != null) {
                                            MiuiHeadsetFragment.this.headTrackingCheckBox.setChecked(false);
                                        } else {
                                            Log.e("MiuiHeadsetFragment", "headTrackingCheckBox is null @@@@@@");
                                        }
                                    }
                                }, 10L);
                            } else {
                                this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.32
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        if (MiuiHeadsetFragment.this.headTrackingCheckBox != null) {
                                            MiuiHeadsetFragment.this.headTrackingCheckBox.setChecked(true);
                                        } else {
                                            Log.e("MiuiHeadsetFragment", "headTrackingCheckBox is null @@@@@@");
                                        }
                                    }
                                }, 10L);
                            }
                        }
                    } else if (str3.startsWith("WIND_NOISE")) {
                        String replace2 = str3.replace("WIND_NOISE", "");
                        if (TextUtils.isEmpty(str2) || !HeadsetIDConstants.isK71Headset(this.mDeviceId) || TextUtils.isEmpty(replace2) || (bluetoothDevice = this.mDevice) == null || !replace2.equalsIgnoreCase(bluetoothDevice.getAddress())) {
                            return;
                        }
                        if (!"01".equals(str2)) {
                            this.mSupportWindNoise = Boolean.FALSE;
                            return;
                        }
                        this.mSupportWindNoise = Boolean.TRUE;
                        this.mPendingAnc = "0104";
                        return;
                    }
                    Log.d("MiuiHeadsetFragment", "mSupportWindNoise:" + this.mSupportWindNoise);
                    if (this.mBluetoothHfp == null) {
                        Log.d("MiuiHeadsetFragment", "mBluetoothHfp null");
                        return;
                    }
                    String[] split = str2.split("\\,", -1);
                    if (!str3.equalsIgnoreCase(this.mDevice.getAddress())) {
                        Log.d("MiuiHeadsetFragment", "callback data is not belong to this device");
                    } else if (split.length != 16) {
                    } else {
                        boolean z = !TextUtils.isEmpty(split[0]);
                        boolean z2 = !TextUtils.isEmpty(split[1]);
                        boolean z3 = !TextUtils.isEmpty(split[2]);
                        boolean z4 = !TextUtils.isEmpty(split[3]);
                        TextUtils.isEmpty(split[4]);
                        TextUtils.isEmpty(split[5]);
                        TextUtils.isEmpty(split[6]);
                        boolean z5 = !TextUtils.isEmpty(split[7]);
                        boolean z6 = !TextUtils.isEmpty(split[8]);
                        boolean z7 = !TextUtils.isEmpty(split[9]);
                        TextUtils.isEmpty(split[10]);
                        boolean z8 = !TextUtils.isEmpty(split[11]);
                        boolean z9 = !TextUtils.isEmpty(split[12]);
                        boolean z10 = !TextUtils.isEmpty(split[13]);
                        boolean z11 = !TextUtils.isEmpty(split[14]);
                        if (z && z2 && z3 && z4) {
                            updateStatus(split[4], split[5], Integer.valueOf(split[0]).intValue(), Integer.valueOf(split[2]).intValue(), Integer.valueOf(split[1]).intValue(), split[3]);
                        }
                        if (z5) {
                            synchronized (this.mAncLock) {
                                Log.d("MiuiHeadsetFragment", "ancLevelExist mAncPendingStatus: " + this.mAncPendingStatus + " mPendingAnc: " + this.mPendingAnc + " data[7]=" + split[7]);
                                if (this.mAncPendingStatus == 0) {
                                    this.mAncPendingStatus = 1;
                                    this.mWorkHandler.removeMessages(103);
                                    MessageHandler messageHandler = this.mWorkHandler;
                                    messageHandler.sendMessageDelayed(messageHandler.obtainMessage(103), 1500L);
                                    if (this.mSupportWindNoise.booleanValue()) {
                                        deviceReportInfoAnc("0104");
                                    } else {
                                        deviceReportInfoAnc(split[7]);
                                    }
                                } else if (this.mSupportWindNoise.booleanValue()) {
                                    this.mPendingAnc = "0104";
                                } else {
                                    this.mPendingAnc = split[7];
                                }
                                Log.d("MiuiHeadsetFragment", "mAncPendingStatus: " + this.mAncPendingStatus + " mPendingAnc: " + this.mPendingAnc);
                            }
                        }
                        if (z7) {
                            refreshFunKeyInfo(split[9], "");
                        }
                        if (z6) {
                            refreshInearInfo(Boolean.parseBoolean(split[8]));
                        }
                        if (z8) {
                            refreshConfigInfo(1, split[11]);
                        }
                        if (z9) {
                            refreshFunKeyInfo("", split[12]);
                        }
                        if (z10) {
                            refreshConfigInfo(3, split[13]);
                        }
                        if (z11) {
                            refreshConfigInfo(4, split[14]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "error to refresh status " + e);
        }
    }

    void refreshStatusUi(int i, int i2, int i3, String str) {
        try {
            if (this.mRootView == null) {
                Log.d("MiuiHeadsetFragment", "mRootView is null");
                return;
            }
            BluetoothHeadset bluetoothHeadset = this.mBluetoothHfp;
            if (bluetoothHeadset == null || (bluetoothHeadset != null && bluetoothHeadset.getConnectionState(this.mDevice) != 2)) {
                i = this.INVALID_BATTERY;
                i2 = i;
                i3 = i2;
            }
            Log.d("MiuiHeadsetFragment", "refreshStatusUi, left:" + i + ", right:" + i2 + ", box:" + i3 + ", version:" + str);
            int i4 = 0;
            if (i != this.INVALID_BATTERY) {
                int i5 = this.BATTERY_CHARGE_FLAG;
                boolean z = (i & i5) == i5;
                int i6 = i & this.BATTERY_VALUE_FLAG;
                if (i6 >= 0 && i6 <= 100) {
                    ((TextView) this.mRootView.findViewById(R.id.leftBattery)).setText(String.valueOf(i6) + "%");
                    updateBatteryIcon(R.id.imageLeftBattery, i6, z);
                }
            } else {
                ((TextView) this.mRootView.findViewById(R.id.leftBattery)).setText("-");
                updateBatteryIcon(R.id.imageLeftBattery, 100, false);
            }
            if (i2 != this.INVALID_BATTERY) {
                int i7 = this.BATTERY_CHARGE_FLAG;
                boolean z2 = (i2 & i7) == i7;
                int i8 = i2 & this.BATTERY_VALUE_FLAG;
                if (i8 >= 0 && i8 <= 100) {
                    ((TextView) this.mRootView.findViewById(R.id.rightBattery)).setText(String.valueOf(i8) + "%");
                    updateBatteryIcon(R.id.imageRightBattery, i8, z2);
                }
            } else {
                ((TextView) this.mRootView.findViewById(R.id.rightBattery)).setText("-");
                updateBatteryIcon(R.id.imageRightBattery, 100, false);
            }
            if (i3 != this.INVALID_BATTERY) {
                int i9 = this.BATTERY_CHARGE_FLAG;
                boolean z3 = (i3 & i9) == i9;
                int i10 = this.BATTERY_VALUE_FLAG & i3;
                if (i10 >= 0 && i10 <= 100) {
                    ((TextView) this.mRootView.findViewById(R.id.boxBattery)).setText(String.valueOf(i10) + "%");
                    updateBatteryIcon(R.id.imageBoxBattery, i10, z3);
                }
            } else {
                ((TextView) this.mRootView.findViewById(R.id.boxBattery)).setText("-");
                updateBatteryIcon(R.id.imageBoxBattery, 100, false);
            }
            if (!TextUtils.isEmpty(str)) {
                ((TextView) this.mRootView.findViewById(R.id.versionName)).setText(str);
            }
            View view = this.mRootView;
            if (view != null) {
                View findViewById = view.findViewById(R.id.anclayout);
                if (!this.mSupportAnc.booleanValue()) {
                    i4 = 8;
                }
                findViewById.setVisibility(i4);
            }
            if (this.mRootView == null || !this.mSupportOta.booleanValue()) {
                return;
            }
            this.mRootView.findViewById(R.id.otaLayout).setEnabled(true);
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "error " + e);
        }
    }

    public void setInEarStatus() {
        try {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("Ineartest");
            if (checkBoxPreference == null || !checkBoxPreference.isChecked()) {
                this.mService.changePlayStatus(0, this.mDevice);
            } else {
                this.mService.changePlayStatus(1, this.mDevice);
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetFragment", "error " + e);
        }
    }

    public void setPadAlpha(boolean z) {
        if (z) {
            float f = this.mActivity.getResources().getFloat(R.dimen.opacity_enabled);
            this.mRootView.findViewById(R.id.ancAdjust).setAlpha(f);
            this.mRootView.findViewById(R.id.ancAdjustText).setAlpha(f);
            this.mRootView.findViewById(R.id.transparentAdjust).setAlpha(f);
            this.mRootView.findViewById(R.id.transparentAdjustText).setAlpha(f);
            this.mRootView.findViewById(R.id.ancAdjustView).setEnabled(true);
            this.mRootView.findViewById(R.id.transparentAdjustView).setEnabled(true);
            return;
        }
        float f2 = this.mActivity.getResources().getFloat(R.dimen.opacity_disabled);
        View view = this.mRootView;
        int i = R.id.ancAdjust;
        view.findViewById(i).setVisibility(0);
        View view2 = this.mRootView;
        int i2 = R.id.ancAdjustText;
        view2.findViewById(i2).setVisibility(0);
        if (HeadsetIDConstants.isTWS01Headset(this.mDeviceId)) {
            this.mRootView.findViewById(R.id.ancAdapterText).setVisibility(0);
        }
        View view3 = this.mRootView;
        int i3 = R.id.transparentAdjust;
        view3.findViewById(i3).setVisibility(8);
        View view4 = this.mRootView;
        int i4 = R.id.transparentAdjustText;
        view4.findViewById(i4).setVisibility(8);
        this.mRootView.findViewById(i).setAlpha(f2);
        this.mRootView.findViewById(i2).setAlpha(f2);
        this.mRootView.findViewById(i3).setAlpha(f2);
        this.mRootView.findViewById(i4).setAlpha(f2);
        this.mRootView.findViewById(R.id.ancAdjustView).setEnabled(false);
        this.mRootView.findViewById(R.id.transparentAdjustView).setEnabled(false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:93:0x05cf, code lost:
    
        if (com.android.settings.bluetooth.HeadsetIDConstants.isSupportZimiAdapter(r22, r24.mDeviceId) != false) goto L94;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateAncUi(java.lang.String r25, boolean r26) {
        /*
            Method dump skipped, instructions count: 2324
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.MiuiHeadsetFragment.updateAncUi(java.lang.String, boolean):void");
    }

    void updateAtUiInfo(final String str) {
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.MiuiHeadsetFragment.31
            @Override // java.lang.Runnable
            public void run() {
                String[] split;
                try {
                    if (!TextUtils.isEmpty(str) && (split = str.split("\\|", -1)) != null && split.length == 4) {
                        Log.d("MiuiHeadsetFragment", "at indicate" + str);
                        if (!TextUtils.isEmpty(split[1])) {
                            MiuiHeadsetFragment.this.mAncLevelMap = split[1];
                        }
                        if (TextUtils.isEmpty(split[2]) && TextUtils.isEmpty(split[0])) {
                            return;
                        }
                        MiuiHeadsetFragment.this.mBatteryCached = split[2];
                        MiuiHeadsetFragment.this.mAncCached = split[0];
                        MiuiHeadsetFragment.this.mWindNoiseAncLevel = split[3];
                        String[] split2 = MiuiHeadsetFragment.this.mBatteryCached.split("\\,");
                        if (MiuiHeadsetFragment.this.mBluetoothHfp == null || MiuiHeadsetFragment.this.mBluetoothHfp.getConnectionState(MiuiHeadsetFragment.this.mDevice) != 2) {
                            return;
                        }
                        MiuiHeadsetFragment miuiHeadsetFragment = MiuiHeadsetFragment.this;
                        if (!miuiHeadsetFragment.mInitedAtUi && !TextUtils.isEmpty(miuiHeadsetFragment.mBatteryCached)) {
                            MiuiHeadsetFragment.this.refreshStatusUi(Integer.valueOf(split2[0]).intValue(), Integer.valueOf(split2[1]).intValue(), Integer.valueOf(split2[2]).intValue(), "");
                        }
                        MiuiHeadsetFragment miuiHeadsetFragment2 = MiuiHeadsetFragment.this;
                        if (!miuiHeadsetFragment2.mInitedAtUi && !TextUtils.isEmpty(miuiHeadsetFragment2.mAncCached)) {
                            MiuiHeadsetFragment miuiHeadsetFragment3 = MiuiHeadsetFragment.this;
                            if (miuiHeadsetFragment3.isSupportWindNoise(miuiHeadsetFragment3.mDeviceId) && !TextUtils.isEmpty(MiuiHeadsetFragment.this.mWindNoiseAncLevel) && MiuiHeadsetFragment.this.mWindNoiseAncLevel.equals("01") && MiuiHeadsetFragment.this.mAncCached.equals("1")) {
                                MiuiHeadsetFragment.this.updateAncUi("0104", false);
                            } else {
                                MiuiHeadsetFragment miuiHeadsetFragment4 = MiuiHeadsetFragment.this;
                                miuiHeadsetFragment4.updateAncUi(miuiHeadsetFragment4.getDefaultAncLevel(Integer.valueOf(miuiHeadsetFragment4.mAncCached).intValue()), false);
                            }
                        }
                        MiuiHeadsetFragment.this.mInitedAtUi = true;
                    }
                } catch (Exception e) {
                    Log.e("MiuiHeadsetFragment", "error " + e);
                }
            }
        }, 10L);
    }

    public void updateImageParament(View view, boolean z) {
        ImageView imageView = (ImageView) view;
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = -1;
        if (z) {
            layoutParams.height = this.mActivity.getResources().getInteger(R.integer.headset_set_dimens);
        } else {
            layoutParams.height = this.mActivity.getResources().getInteger(R.integer.headset_unset_dimens);
        }
        imageView.setLayoutParams(layoutParams);
    }
}
