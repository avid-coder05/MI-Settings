package com.android.settings.emergency.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.provider.CallLog;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseCallState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.R;
import com.android.settings.emergency.service.AudioTrackManager;
import com.android.settings.emergency.util.CommonUtils;
import com.android.settings.emergency.util.Config;
import com.android.settings.emergency.util.NotificationUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import miui.app.constants.ThemeManagerConstants;
import miui.os.Build;
import miui.os.DeviceFeature;
import miui.process.ProcessManager;
import miui.provider.ExtraContacts;
import miui.securitycenter.NetworkUtils;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyConstants;
import miui.util.FeatureParser;
import miuix.core.util.IOUtils;
import org.json.JSONObject;
import src.com.android.settings.emergency.util.AudioRecordManager;
import src.com.android.settings.emergency.util.CameraUtils;

/* loaded from: classes.dex */
public class LocationService extends Service {
    private static boolean mIsStaging;
    private AudioManager mAudioManager;
    private AudioRecordManager mAudioRecordManager;
    private AudioTrackManager mAudioTrackManager;
    private BatteryManager mBatteryManager;
    private String mBitmapPath;
    private String mCacheDirPath;
    private boolean mCallActionExecuted;
    private boolean mCallSuccess;
    private int mCameraDirection;
    private CameraUtils mCameraUtils;
    private BroadcastReceiver mCdmaRealConnectedReceiver;
    private List<String> mContacts;
    private SubscriptionInfo mCurrentSubInfo;
    private boolean mIsGPSEnable;
    private int mLastCallState;
    private double mLatitude;
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private double mLongitude;
    private boolean mMobileDataEnable;
    private ContentObserver mObserver;
    private PhoneStateListener mPhoneStateListener;
    private int mPreLocationMode;
    private long mReportId;
    private int mSignalStrength;
    private boolean mSosAroundPhotoEnable;
    private boolean mSosAroundVoiceEnable;
    private boolean mSosCallLogEnable;
    private boolean mSosCallingEnable;
    private TelephonyManager mTelephonyManager;
    private String mVoicePath;
    private PowerManager.WakeLock mWakeLock;
    private Handler mWorkingHandler;
    private HandlerThread mWorkingThread;
    private int mContactsCallIndex = 0;
    private boolean mIsDialingState = false;
    private Handler mMainHandler = new Handler();
    private ISosSnapListener mSnapListener = new ISosSnapListener() { // from class: com.android.settings.emergency.service.LocationService.11
        @Override // com.android.settings.emergency.service.LocationService.ISosSnapListener
        public void executeSendSnap(String str) {
            LocationService.this.mBitmapPath = str;
            if (LocationService.this.mMobileDataEnable) {
                LocationService.this.mWorkingHandler.sendEmptyMessage(4);
            } else {
                LocationService.this.mWorkingHandler.sendEmptyMessageDelayed(4, 3000L);
            }
        }
    };
    private ISosVoiceListener mVoiceListener = new ISosVoiceListener() { // from class: com.android.settings.emergency.service.LocationService.12
        @Override // com.android.settings.emergency.service.LocationService.ISosVoiceListener
        public void executeSendVoice(String str) {
            LocationService.this.mVoicePath = str;
            if (LocationService.this.mMobileDataEnable) {
                LocationService.this.mWorkingHandler.sendEmptyMessage(5);
            } else {
                LocationService.this.mWorkingHandler.sendEmptyMessageDelayed(5, 3000L);
            }
        }
    };

    /* loaded from: classes.dex */
    public interface ISosSnapListener {
        void executeSendSnap(String str);
    }

    /* loaded from: classes.dex */
    public interface ISosVoiceListener {
        void executeSendVoice(String str);
    }

    static /* synthetic */ int access$608(LocationService locationService) {
        int i = locationService.mContactsCallIndex;
        locationService.mContactsCallIndex = i + 1;
        return i;
    }

    private void acquireWakeLock() {
        Log.d("SOS-LocationService", "acquire wakelock");
        PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "SOS-LocationService");
        this.mWakeLock = newWakeLock;
        newWakeLock.acquire();
    }

    private void copySOSAudio() {
        FileOutputStream fileOutputStream;
        Log.d("SOS-LocationService", "copy audio");
        if (!SystemProperties.getBoolean("ro.vendor.audio.sos", false) || Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        Log.d("SOS-LocationService", "start copy audio");
        InputStream inputStream = null;
        try {
        } catch (IOException e) {
            e = e;
            fileOutputStream = null;
        } catch (Throwable th) {
            th = th;
            fileOutputStream = null;
        }
        if (!new File(this.mCacheDirPath + "/emergency_contact.wav").exists()) {
            if (CommonUtils.ensureDirs(this.mCacheDirPath + "/emergency_contact.wav")) {
                InputStream openRawResource = getResources().openRawResource(R.raw.emergency_contact);
                try {
                    fileOutputStream = new FileOutputStream(this.mCacheDirPath + "/emergency_contact.wav");
                    try {
                        byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
                        while (true) {
                            int read = openRawResource.read(bArr);
                            if (read == -1) {
                                break;
                            }
                            fileOutputStream.write(bArr, 0, read);
                        }
                        fileOutputStream.flush();
                        inputStream = openRawResource;
                    } catch (IOException e2) {
                        e = e2;
                        inputStream = openRawResource;
                        try {
                            Log.e("SOS-LocationService", "IOException when copy audio file :", e);
                            IOUtils.closeQuietly(inputStream);
                            IOUtils.closeQuietly((OutputStream) fileOutputStream);
                        } catch (Throwable th2) {
                            th = th2;
                            IOUtils.closeQuietly(inputStream);
                            IOUtils.closeQuietly((OutputStream) fileOutputStream);
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        inputStream = openRawResource;
                        IOUtils.closeQuietly(inputStream);
                        IOUtils.closeQuietly((OutputStream) fileOutputStream);
                        throw th;
                    }
                } catch (IOException e3) {
                    e = e3;
                    fileOutputStream = null;
                } catch (Throwable th4) {
                    th = th4;
                    fileOutputStream = null;
                }
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly((OutputStream) fileOutputStream);
            }
        }
        fileOutputStream = null;
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly((OutputStream) fileOutputStream);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createEmergencyStatusBar() {
        CommonUtils.setSosStatusBarVisibility(this, true);
    }

    private void enforceLocationServiceEnable() {
        this.mObserver = new ContentObserver(this.mMainHandler) { // from class: com.android.settings.emergency.service.LocationService.8
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                Settings.Secure.putInt(LocationService.this.getContentResolver(), "location_mode", 3);
            }
        };
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor("location_providers_allowed"), true, this.mObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getBatteryLeft() {
        return this.mBatteryManager.getIntProperty(4) + "%";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getRecentCallLogs() {
        Cursor cursor = null;
        try {
            try {
                cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{"number", "date", "duration", "name"}, "date > " + (System.currentTimeMillis() - 3600000), null, ExtraContacts.Calls.DEFAULT_SORT_ORDER);
                if (cursor != null && cursor.getCount() > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.miui_sos_msg_call_log_prefix));
                    sb.append("\n");
                    while (cursor.moveToNext()) {
                        String string = cursor.getString(cursor.getColumnIndex("number"));
                        long j = cursor.getLong(cursor.getColumnIndex("duration"));
                        long j2 = cursor.getLong(cursor.getColumnIndex("date"));
                        String string2 = cursor.getString(cursor.getColumnIndex("name"));
                        if (TextUtils.isEmpty(string2)) {
                            string2 = getString(R.string.miui_sos_call_log_stranger);
                        }
                        int i = (int) j;
                        sb.append(CommonUtils.getFormatTime(j2));
                        sb.append(" ");
                        sb.append(string2);
                        sb.append(" ");
                        sb.append(string);
                        sb.append(" ");
                        sb.append(getString(R.string.miui_sos_call_log_duration));
                        sb.append(getString(R.string.miui_sos_call_log_time_unit, new Object[]{Integer.valueOf(i / 60), Integer.valueOf(i % 60)}));
                        sb.append("\n");
                    }
                    return sb.toString();
                }
            } catch (Exception e) {
                Slog.e("SOS-LocationService", "Exception when init repeated calls : ", e);
            }
            IOUtils.closeQuietly(cursor);
            return getString(R.string.miui_sos_call_log_none);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private String getRequestDomain() {
        String str = mIsStaging ? "http://staging.srv.sec.miui.com" : "https://srv.sec.miui.com";
        if (Build.IS_INTERNATIONAL_BUILD) {
            String region = Build.getRegion();
            return "RU".equals(region) ? "https://srv-ru.sec.intl.miui.com" : "IN".equals(region) ? "https://srv-ind.sec.intl.miui.com" : "https://srv.sec.intl.miui.com";
        }
        return str;
    }

    private void initConfig() {
        Log.d("SOS-LocationService", "init config");
        Config.setInSosModeState(this, true);
        this.mSosCallLogEnable = Config.isSosCallLogEnable(this);
        this.mSosCallingEnable = Config.isSosCallingEnable(this);
        this.mSosAroundPhotoEnable = Config.isSosEmergencyAroundPhoto(this);
        this.mSosAroundVoiceEnable = Config.isSosEmergencyAroundVoice(this);
        this.mContacts = new ArrayList(Arrays.asList(Config.getSosEmergencyContacts(this).split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)));
        boolean isLockedApplication = ProcessManager.isLockedApplication(getPackageName(), 0);
        Config.setApplicationLockedState(this, isLockedApplication);
        if (!isLockedApplication) {
            ProcessManager.updateApplicationLockedState(getPackageName(), 0, true);
        }
        boolean isMobileDataEnable = CommonUtils.isMobileDataEnable(this);
        this.mMobileDataEnable = isMobileDataEnable;
        if (!isMobileDataEnable) {
            NetworkUtils.setMobileDataState(this, true);
        }
        this.mReportId = System.currentTimeMillis() + Math.abs(new Random().nextLong());
        this.mCurrentSubInfo = CommonUtils.getCurrentEnableSubInfo();
        AudioTrackManager audioTrackManager = AudioTrackManager.getInstance(this);
        this.mAudioTrackManager = audioTrackManager;
        audioTrackManager.setOnCompleteListener(new AudioTrackManager.OnPlayCompleteListener() { // from class: com.android.settings.emergency.service.LocationService.2
            @Override // com.android.settings.emergency.service.AudioTrackManager.OnPlayCompleteListener
            public void onPlayComplete() {
                Log.w("SOS-LocationService", "SOS AUDIO PLAY COMPLETE");
                Log.e("SOS-LocationService", "end call after sos audio!");
                ((TelecomManager) LocationService.this.getSystemService("telecom")).endCall();
            }
        });
        this.mAudioManager = (AudioManager) getSystemService("audio");
        this.mBatteryManager = (BatteryManager) getSystemService("batterymanager");
    }

    private void initHandler() {
        Log.d("SOS-LocationService", "init work handler");
        String requestDomain = getRequestDomain();
        final String str = requestDomain + "/SOS/report";
        final String str2 = requestDomain + "/SOS/msm";
        HandlerThread handlerThread = new HandlerThread("sos_working_thread");
        this.mWorkingThread = handlerThread;
        handlerThread.start();
        Handler handler = new Handler(this.mWorkingThread.getLooper()) { // from class: com.android.settings.emergency.service.LocationService.5
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    Log.d("SOS-LocationService", "report location");
                    HashMap hashMap = new HashMap();
                    hashMap.put("reportId", String.valueOf(LocationService.this.mReportId));
                    hashMap.put("lgt", String.valueOf(LocationService.this.mLongitude));
                    hashMap.put("ltt", String.valueOf(LocationService.this.mLatitude));
                    hashMap.put("battery", LocationService.this.getBatteryLeft());
                    hashMap.put("signal", String.valueOf(LocationService.this.mSignalStrength));
                    hashMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
                    com.android.settings.emergency.util.NetworkUtils.doPost(str, hashMap, "mkzt5239-a34f-3ty9-eb73-75456745ns5c");
                } else if (i == 2) {
                    Log.d("SOS-LocationService", "send message");
                    try {
                        HashMap hashMap2 = new HashMap();
                        hashMap2.put("reportId", String.valueOf(LocationService.this.mReportId));
                        hashMap2.put("area", Locale.getDefault().toString());
                        String doGet = com.android.settings.emergency.util.NetworkUtils.doGet(str2, hashMap2, "mkzt5239-a34f-3ty9-eb73-75456745ns5c");
                        String string = TextUtils.isEmpty(doGet) ? null : new JSONObject(doGet).getString("data");
                        if (TextUtils.isEmpty(string)) {
                            return;
                        }
                        PendingIntent broadcast = PendingIntent.getBroadcast(LocationService.this, 0, new Intent("DELIVERED_SMS_ACTION_IN_SOS"), 201326592);
                        for (int i2 = 0; i2 < LocationService.this.mContacts.size(); i2++) {
                            CommonUtils.sendTextMessage((String) LocationService.this.mContacts.get(i2), string, broadcast, LocationService.this.mCurrentSubInfo.getSlotId());
                            if (LocationService.this.mSosCallLogEnable) {
                                CommonUtils.sendTextMessage((String) LocationService.this.mContacts.get(i2), LocationService.this.getRecentCallLogs(), broadcast, LocationService.this.mCurrentSubInfo.getSlotId());
                            }
                        }
                    } catch (Exception e) {
                        Log.e("SOS-LocationService", "Exception when sending sos message: ", e);
                    }
                } else if (i == 3) {
                    if (LocationService.this.mCallActionExecuted) {
                        return;
                    }
                    LocationService.this.mCallActionExecuted = true;
                    Log.e("SOS-LocationService", "start sos call !");
                    LocationService locationService = LocationService.this;
                    locationService.startCallIntent(locationService, (String) locationService.mContacts.get(LocationService.access$608(LocationService.this)), LocationService.this.mCurrentSubInfo.getSlotId());
                } else if (i != 4) {
                    if (i != 5) {
                        return;
                    }
                    if (TextUtils.isEmpty(LocationService.this.mVoicePath)) {
                        Log.i("SOS-LocationService", "handleMessage: the mVoicePath is isEmpty !");
                        return;
                    }
                    Log.d("SOS-LocationService", "handleMessage: start send voice ");
                    LocationService locationService2 = LocationService.this;
                    locationService2.sendSOSBitmapOrVoice(locationService2.mVoicePath, 3);
                } else if (TextUtils.isEmpty(LocationService.this.mBitmapPath)) {
                    Log.i("SOS-LocationService", "handleMessage: SEND BITMAP mBitmapPath is null !");
                } else {
                    Log.d("SOS-LocationService", "handleMessage start send bitmap mCameraDirection：" + LocationService.this.mCameraDirection);
                    LocationService locationService3 = LocationService.this;
                    locationService3.sendSOSBitmapOrVoice(locationService3.mBitmapPath, 1);
                    if (LocationService.this.mCameraDirection == 0) {
                        LocationService.this.mCameraDirection = 1;
                        LocationService.this.sendSosAroundPhoto();
                    }
                }
            }
        };
        this.mWorkingHandler = handler;
        if (this.mMobileDataEnable) {
            handler.sendEmptyMessage(2);
        } else {
            handler.sendEmptyMessageDelayed(2, 3000L);
        }
        if (this.mSosCallingEnable) {
            Log.d("SOS-LocationService", "sos calling enabled");
            this.mWorkingHandler.sendEmptyMessageDelayed(3, 18000L);
        }
    }

    private void initPhoneState() {
        Log.d("SOS-LocationService", "init phone state");
        this.mPhoneStateListener = new PhoneStateListener() { // from class: com.android.settings.emergency.service.LocationService.3
            public void onPreciseCallStateChanged(PreciseCallState preciseCallState) {
                int foregroundCallState = preciseCallState.getForegroundCallState();
                Log.i("SOS-LocationService", "currentState:" + foregroundCallState);
                if (3 == foregroundCallState) {
                    LocationService.this.mIsDialingState = true;
                }
                if (!LocationService.this.isCdmaSim() && 1 == foregroundCallState && 1 != LocationService.this.mLastCallState) {
                    LocationService.this.playAudioAfterCallConnected();
                }
                if (foregroundCallState == 0 || 7 == foregroundCallState) {
                    Log.i("SOS-LocationService", "isSuccess:" + LocationService.this.mCallSuccess + " index:" + LocationService.this.mContactsCallIndex);
                    if (LocationService.this.mCallSuccess) {
                        AudioTrackManager.getInstance(LocationService.this).cancelPlay();
                    }
                    if (LocationService.this.mIsDialingState && !LocationService.this.mCallSuccess && LocationService.this.mContactsCallIndex < LocationService.this.mContacts.size()) {
                        Log.e("SOS-LocationService", "start sos call next!");
                        LocationService locationService = LocationService.this;
                        locationService.startCallIntentDelay(locationService, (String) locationService.mContacts.get(LocationService.access$608(LocationService.this)), LocationService.this.mCurrentSubInfo.getSlotId());
                    }
                    LocationService.this.createEmergencyStatusBar();
                } else {
                    LocationService.this.removeEmergencyStatusBar();
                }
                LocationService.this.mLastCallState = foregroundCallState;
            }

            @Override // android.telephony.PhoneStateListener
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                LocationService.this.mSignalStrength = miui.telephony.TelephonyManager.getDefault().getMiuiLevel(signalStrength);
            }
        };
        if (this.mSosCallingEnable && isCdmaSim()) {
            Log.w("SOS-LocationService", "is cdma sim card !");
            this.mCdmaRealConnectedReceiver = new BroadcastReceiver() { // from class: com.android.settings.emergency.service.LocationService.4
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    Log.e("SOS-LocationService", "cdma real connected !");
                    LocationService.this.playAudioAfterCallConnected();
                }
            };
            registerReceiver(this.mCdmaRealConnectedReceiver, new IntentFilter(TelephonyConstants.ACTION_CDMA_CALL_REAL_CONNECTED));
        }
        int i = this.mSosCallingEnable ? 2304 : 256;
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService("phone");
        this.mTelephonyManager = telephonyManager;
        telephonyManager.createForSubscriptionId(this.mCurrentSubInfo.getSubscriptionId()).listen(this.mPhoneStateListener, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCdmaSim() {
        return 2 == miui.telephony.TelephonyManager.getDefault().getPhoneTypeForSlot(this.mCurrentSubInfo.getSlotId());
    }

    private void openGPS() {
        if (this.mIsGPSEnable) {
            return;
        }
        this.mPreLocationMode = Settings.Secure.getInt(getContentResolver(), "location_mode", 3);
        Settings.Secure.putInt(getContentResolver(), "location_mode", 3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playAudioAfterCallConnected() {
        Log.d("SOS-LocationService", "play audio");
        if (FeatureParser.getBoolean("is_mediatek", false)) {
            this.mAudioManager.setParameters("Set_SpeechCall_DL_Mute=1");
        } else {
            this.mAudioManager.setParameters("incall_music_mute=true");
        }
        this.mMainHandler.postDelayed(new Runnable() { // from class: com.android.settings.emergency.service.LocationService.7
            @Override // java.lang.Runnable
            public void run() {
                LocationService.this.mAudioTrackManager.startPlay(LocationService.this.mCacheDirPath + "/emergency_contact.wav");
            }
        }, 2000L);
        this.mCallSuccess = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeEmergencyStatusBar() {
        CommonUtils.setSosStatusBarVisibility(this, false);
    }

    private void restoreGPS() {
        if (this.mIsGPSEnable) {
            return;
        }
        Settings.Secure.putInt(getContentResolver(), "location_mode", this.mPreLocationMode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSOSBitmapOrVoice(String str, int i) {
        Uri outputMediaFileUri = CommonUtils.getOutputMediaFileUri(this, "com.android.mms", str);
        if (outputMediaFileUri == null) {
            Log.i("SOS-LocationService", "sendSOSBitmapOrVoice: uri is null !");
            return;
        }
        Intent intent = new Intent();
        intent.setClassName("com.android.mms", "com.android.mms.ui.NoConfirmationSendService");
        intent.setAction("com.android.mms.intent.action.SEND_MMS_NO_CONFIRMATION");
        intent.putExtra("path", outputMediaFileUri.toString());
        intent.putExtra("attachment_type", i);
        intent.setData(Uri.fromParts(ThemeManagerConstants.COMPONENT_CODE_MMS, Config.getSosEmergencyContacts(this), null));
        startService(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSosAroundPhoto() {
        if (!this.mSosAroundPhotoEnable) {
            Log.i("SOS-LocationService", "sendSosAroundPhoto: mSosAroundPhotoEnable = " + this.mSosAroundPhotoEnable);
        } else if (this.mCameraDirection != 1 || !DeviceFeature.hasPopupCameraSupport()) {
            Log.d("SOS-LocationService", "mSosAroundPhotoEnable  CameraUtils init ：mCameraDirection = " + this.mCameraDirection);
            CameraUtils cameraUtils = new CameraUtils(this, this.mCameraDirection, this.mSnapListener, this.mCacheDirPath);
            this.mCameraUtils = cameraUtils;
            cameraUtils.prepareCameraAndTakePhoto();
        } else {
            Log.i("SOS-LocationService", "mCameraDirection = " + this.mCameraDirection);
            Log.i("SOS-LocationService", "hasPopupCameraSupport() = " + DeviceFeature.hasPopupCameraSupport());
        }
    }

    private void sendSosAroundVoice() {
        if (!this.mSosAroundVoiceEnable) {
            Log.i("SOS-LocationService", "sendSosAroundVoice: mSosAroundVoiceEnable = " + this.mSosAroundVoiceEnable);
            return;
        }
        AudioRecordManager audioRecordManager = new AudioRecordManager(this.mVoiceListener, this.mCacheDirPath);
        this.mAudioRecordManager = audioRecordManager;
        audioRecordManager.startRecord();
        Log.i("SOS-LocationService", "sendSosAroundVoice: startRecord");
        new Handler().postDelayed(new Runnable() { // from class: com.android.settings.emergency.service.LocationService.1
            @Override // java.lang.Runnable
            public void run() {
                LocationService.this.mAudioRecordManager.stopRecordAndSend();
                Log.i("SOS-LocationService", "sendSosAroundVoice: stopRecordAndSend");
            }
        }, 5000L);
    }

    private void showSOSNotification() {
        Log.d("SOS-LocationService", "show notification");
        NotificationUtils.createNotificationChannel((NotificationManager) getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION), "com.android.settings.emergency", getString(R.string.sos_privacy_dialog_title), 4);
        startForeground(5386518, NotificationUtils.createNotificationBuilder(this, "com.android.settings.emergency").setWhen(System.currentTimeMillis()).setContentTitle(getString(R.string.miui_sos_statusbar_title)).setSmallIcon(R.drawable.stat_sys_sos).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sos_notification_icon)).build());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startCallIntentDelay(final Context context, final String str, final int i) {
        this.mWorkingHandler.postDelayed(new Runnable() { // from class: com.android.settings.emergency.service.LocationService.10
            @Override // java.lang.Runnable
            public void run() {
                LocationService.this.startCallIntent(context, str, i);
            }
        }, 5000L);
    }

    private void startLocation() {
        Log.d("SOS-LocationService", "start location");
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(1);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(1);
        this.mLocationListener = new LocationListener() { // from class: com.android.settings.emergency.service.LocationService.6
            @Override // android.location.LocationListener
            public void onLocationChanged(Location location) {
                Log.w("SOS-LocationService", "onLocationChanged !");
                LocationService.this.mLatitude = location.getLatitude();
                LocationService.this.mLongitude = location.getLongitude();
                LocationService.this.mWorkingHandler.sendEmptyMessage(1);
                LocationService.this.mWorkingHandler.postDelayed(new Runnable() { // from class: com.android.settings.emergency.service.LocationService.6.1
                    @Override // java.lang.Runnable
                    public void run() {
                        LocationManager locationManager = LocationService.this.mLocationManager;
                        AnonymousClass6 anonymousClass6 = AnonymousClass6.this;
                        locationManager.requestSingleUpdate(criteria, LocationService.this.mLocationListener, (Looper) null);
                    }
                }, 60000L);
            }

            @Override // android.location.LocationListener
            public void onProviderDisabled(String str) {
            }

            @Override // android.location.LocationListener
            public void onProviderEnabled(String str) {
            }

            @Override // android.location.LocationListener
            public void onStatusChanged(String str, int i, Bundle bundle) {
            }
        };
        LocationManager locationManager = (LocationManager) getSystemService("location");
        this.mLocationManager = locationManager;
        this.mIsGPSEnable = locationManager.isProviderEnabled("gps");
        openGPS();
        enforceLocationServiceEnable();
        this.mLocationManager.requestSingleUpdate(criteria, this.mLocationListener, (Looper) null);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.w("SOS-LocationService", "ENTER SOS!");
        this.mCacheDirPath = getCacheDir().getPath();
        showSOSNotification();
        acquireWakeLock();
        initConfig();
        copySOSAudio();
        initPhoneState();
        initHandler();
        startLocation();
        if (CommonUtils.isSosNewFeatureSupport(this)) {
            sendSosAroundPhoto();
            sendSosAroundVoice();
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.e("SOS-LocationService", "onDestroy ！");
        Config.setInSosModeState(this, false);
        restoreGPS();
        this.mLocationManager.removeUpdates(this.mLocationListener);
        removeEmergencyStatusBar();
        getContentResolver().unregisterContentObserver(this.mObserver);
        this.mWorkingThread.quitSafely();
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        if (this.mSosCallingEnable && isCdmaSim()) {
            unregisterReceiver(this.mCdmaRealConnectedReceiver);
        }
        if (!this.mMobileDataEnable) {
            NetworkUtils.setMobileDataState(this, false);
        }
        if (!Config.isLockedApplication(this)) {
            ProcessManager.updateApplicationLockedState(getPackageName(), 0, false);
        }
        if (FeatureParser.getBoolean("is_mediatek", false)) {
            this.mAudioManager.setParameters("Set_SpeechCall_DL_Mute=0");
        }
        this.mWakeLock.release();
        AudioRecordManager.deleteFile(this.mCacheDirPath);
        CameraUtils.deleteFile(this.mCacheDirPath);
        super.onDestroy();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null && "action_enter_sos_mode".equals(intent.getAction())) {
            createEmergencyStatusBar();
        }
        return super.onStartCommand(intent, i, i2);
    }

    public void startCallIntent(Context context, String str, int i) {
        Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.fromParts("tel", str, null));
        if (i != -1) {
            SubscriptionManager.putSlotIdExtra(intent, i);
            intent.putExtra("com.android.phone.extra.slot", i);
        }
        intent.setPackage("com.android.server.telecom");
        intent.setFlags(335544320);
        context.startActivity(intent);
        Log.e("SOS-LocationService", "start call in sos :");
        this.mMainHandler.postDelayed(new Runnable() { // from class: com.android.settings.emergency.service.LocationService.9
            @Override // java.lang.Runnable
            public void run() {
                Intent intent2 = new Intent("android.intent.action.MAIN");
                intent2.addCategory("android.intent.category.HOME");
                intent2.addFlags(268435456);
                LocationService.this.startActivity(intent2);
            }
        }, 1000L);
        this.mIsDialingState = false;
    }
}
