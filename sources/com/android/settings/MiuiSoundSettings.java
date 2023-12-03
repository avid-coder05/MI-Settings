package com.android.settings;

import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.aidl.IRemoteGetDeviceInfoService;
import com.android.settings.aidl.IRequestCallback;
import com.android.settings.device.DeviceParamsInitHelper;
import com.android.settings.device.JSONUtils;
import com.android.settings.device.ParseMiShopDataUtils;
import com.android.settings.device.RemoteServiceUtil;
import com.android.settings.dndmode.Alarm;
import com.android.settings.notification.SilentModeUtils;
import com.android.settings.recommend.PageIndexManager;
import com.android.settings.report.InternationalCompat;
import com.android.settings.search.tree.MiuiSecurityAndPrivacySettingsTree;
import com.android.settings.sound.MiuiAlarmRingtonePreferenceController;
import com.android.settings.sound.MiuiWorkRingtonePreference;
import com.android.settings.sound.MiuiWorkSoundPreferenceController;
import com.android.settings.sound.RingtoneCardPreference;
import com.android.settings.sound.SeekBarVolumizer;
import com.android.settings.sound.VolumeSeekBarPreference;
import com.android.settings.soundsettings.LabelPreferenceWithBg;
import com.android.settings.soundsettings.RepeatPreferenceWithBg;
import com.android.settings.soundsettings.SoundSpeakerDescPreference;
import com.android.settings.utils.MiuiBaseController;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.widget.CustomCheckBoxPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import miui.app.constants.ThemeManagerConstants;
import miui.os.Build;
import miui.provider.ExtraTelephony;
import miui.util.HapticFeedbackUtil;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.TimePickerDialog;
import miuix.pickerwidget.date.DateUtils;
import miuix.pickerwidget.widget.TimePicker;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MiuiSoundSettings extends MiuiSoundSettingsBase implements Preference.OnPreferenceClickListener {
    private static final Comparator<SortZenRule> RULE_COMPARATOR;
    private static final List<String> mRestrictedKeyList;
    private MiuiDefaultRingtonePreference mAlarmRingtonePreference;
    private AudioManager mAudioManager;
    private RepeatPreferenceWithBg mAutoRuleRepeatPref;
    private LabelPreferenceWithBg mAutoRuleTurnOffPref;
    private LabelPreferenceWithBg mAutoRuleTurnOnPref;
    private MiuiDefaultRingtonePreference mCalendarSoundPreference;
    private final Handler mContentHandler;
    private Context mContext;
    private ZenRuleInfo mCurrZenRuleInfo;
    private UpdateInfoCallback mDeviceInfoCallback;
    private DeviceParamsInitHelper mHelper;
    private MiuiAlarmRingtonePreferenceController mMiuiAlarmRingtoneController;
    private CheckBoxPreference mMuteMediaSoundPref;
    private NotificationManager mNotificationManager;
    private MiuiDefaultRingtonePreference mNotificationSoundPreference;
    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;
    private IRemoteGetDeviceInfoService mRemoteService;
    private RemoteServiceConn mRemoteServiceConn;
    private CheckBoxPreference mRepeatedIncallPref;
    private MiuiWorkRingtonePreference mRequestPreference;
    private CheckBoxPreference mRingerModeSettingPref;
    private MiuiDefaultRingtonePreference mRingtonePreference;
    private CheckBoxPreference mScreenLockedOnlyPref;
    private CheckBoxPreference mSilentModePref;
    private MiuiDefaultRingtonePreference mSmsDeliveredSoundPreference;
    private MiuiDefaultRingtonePreference mSmsReceivedSoundPreference;
    private PreferenceCategory mSoundModeCategory;
    private PreferenceCategory mSoundSpeakerCategory;
    private SoundSpeakerDescPreference mSoundSpeakerPreference;
    private boolean mTimeFlag;
    protected int mUserId;
    private final ContentObserver mVibrateSettingsObserver;
    private CheckBoxPreference mVibrateWhenRingingPref;
    private CheckBoxPreference mVibrateWhenSilentPref;
    private DropDownPreference mVipListPref;
    private CustomCheckBoxPreference mZenAutoRuleTogglePref;
    private PreferenceCategory mZenModeCategory;
    private ZenModeConfig mZenModeConfig;
    private CheckBoxPreference mZenModeTogglePref;
    private MiuiWorkSoundPreferenceController workSoundController;
    private Handler mHandler = new SoundUIHandler(this);
    private List<MiuiBaseController> mControllers = new ArrayList();
    private ArrayList<VolumeSeekBarPreference> mVolumePrefs = new ArrayList<>();
    private boolean mVolumeDownPressed = false;
    private final SoundSettingsObserver mObserver = new SoundSettingsObserver();
    private final BroadcastReceiver mRingerModeReceiver = new BroadcastReceiver() { // from class: com.android.settings.MiuiSoundSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("MiuiSoundSettings", "mRingerModeReceiver, onReceive(), intent : " + intent);
            MiuiSoundSettings.this.sendRefreshMsg();
        }
    };

    /* loaded from: classes.dex */
    private class RemoteServiceConn implements ServiceConnection {
        private RemoteServiceConn() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            FragmentActivity activity = MiuiSoundSettings.this.getActivity();
            if (activity == null) {
                return;
            }
            MiuiSoundSettings.this.mRemoteService = IRemoteGetDeviceInfoService.Stub.asInterface(iBinder);
            MiuiSoundSettings miuiSoundSettings = MiuiSoundSettings.this;
            miuiSoundSettings.mHelper = new DeviceParamsInitHelper(activity, miuiSoundSettings.mRemoteService);
            try {
                MiuiSoundSettings.this.mRemoteService.registerCallback(MiuiSoundSettings.this.mDeviceInfoCallback);
                MiuiSoundSettings.this.mHelper.initSoundParams();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w("MiuiSoundSettings", "onServiceDisconnected");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SortZenRule {
        String id;
        ZenModeConfig.ZenRule rule;

        private SortZenRule() {
        }
    }

    /* loaded from: classes.dex */
    private class SoundSettingsObserver extends ContentObserver {
        private final Uri MUTE_MUSIC_AT_SILENT_URI;
        private final Uri VIBRATE_IN_NORMAL_URI;
        private final Uri VIBRATE_IN_SILENT_URI;
        private final Uri ZEN_MODE_CONFIG_ETAG_URI;
        private final Uri ZEN_MODE_URI;

        public SoundSettingsObserver() {
            super(MiuiSoundSettings.this.mHandler);
            this.ZEN_MODE_URI = Settings.Global.getUriFor(ExtraTelephony.ZEN_MODE);
            this.ZEN_MODE_CONFIG_ETAG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
            this.MUTE_MUSIC_AT_SILENT_URI = Settings.System.getUriFor("mute_music_at_silent");
            this.VIBRATE_IN_SILENT_URI = Settings.System.getUriFor("vibrate_in_silent");
            this.VIBRATE_IN_NORMAL_URI = Settings.System.getUriFor("vibrate_in_normal");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            Log.d("MiuiSoundSettings", "SoundSettingsObserver, onChange(), selfChange : " + z + ", uri : " + uri);
            if (uri != null && this.ZEN_MODE_CONFIG_ETAG_URI.toString().equals(uri.toString())) {
                MiuiSoundSettings.this.mHandler.removeMessages(3);
                MiuiSoundSettings.this.mHandler.sendMessage(MiuiSoundSettings.this.mHandler.obtainMessage(3));
                MiuiSoundSettings.this.mHandler.removeMessages(5);
                MiuiSoundSettings.this.mHandler.sendMessage(MiuiSoundSettings.this.mHandler.obtainMessage(5));
            } else if (uri == null || !this.ZEN_MODE_URI.toString().equals(uri.toString())) {
                MiuiSoundSettings.this.mHandler.sendMessage(MiuiSoundSettings.this.mHandler.obtainMessage(1));
            } else {
                MiuiSoundSettings.this.mHandler.removeMessages(4);
                MiuiSoundSettings.this.mHandler.sendMessage(MiuiSoundSettings.this.mHandler.obtainMessage(4));
                MiuiSoundSettings.this.mHandler.removeMessages(5);
                MiuiSoundSettings.this.mHandler.sendMessage(MiuiSoundSettings.this.mHandler.obtainMessage(5));
            }
        }

        public void register() {
            ContentResolver contentResolver = MiuiSoundSettings.this.getContentResolver();
            contentResolver.registerContentObserver(this.ZEN_MODE_URI, false, this, -1);
            contentResolver.registerContentObserver(this.ZEN_MODE_CONFIG_ETAG_URI, false, this, -1);
            contentResolver.registerContentObserver(this.MUTE_MUSIC_AT_SILENT_URI, false, this, -1);
            contentResolver.registerContentObserver(this.VIBRATE_IN_SILENT_URI, false, this, -1);
            contentResolver.registerContentObserver(this.VIBRATE_IN_NORMAL_URI, false, this, -1);
        }

        public void unregister() {
            MiuiSoundSettings.this.getContentResolver().unregisterContentObserver(this);
        }
    }

    /* loaded from: classes.dex */
    private static class SoundUIHandler extends Handler {
        private final WeakReference<MiuiSoundSettings> mSoundSettingsRef;

        SoundUIHandler(MiuiSoundSettings miuiSoundSettings) {
            super(Looper.getMainLooper());
            this.mSoundSettingsRef = new WeakReference<>(miuiSoundSettings);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            MiuiSoundSettings miuiSoundSettings = this.mSoundSettingsRef.get();
            if (miuiSoundSettings == null) {
                return;
            }
            switch (message.what) {
                case 1:
                case 2:
                    miuiSoundSettings.refreshVolumeAndVibrate();
                    return;
                case 3:
                    miuiSoundSettings.refreshZenRuleSettings();
                    return;
                case 4:
                    miuiSoundSettings.refreshZenModeSetting();
                    miuiSoundSettings.refreshZenRuleSettings();
                    return;
                case 5:
                    miuiSoundSettings.refreshVipListUI();
                    break;
                case 6:
                    break;
                default:
                    return;
            }
            miuiSoundSettings.updateSoundDesc((String) message.obj);
        }
    }

    /* loaded from: classes.dex */
    private static class UpdateInfoCallback extends IRequestCallback.Stub {
        private WeakReference<MiuiSoundSettings> mFragmentRef;

        public UpdateInfoCallback(MiuiSoundSettings miuiSoundSettings) {
            this.mFragmentRef = new WeakReference<>(miuiSoundSettings);
        }

        @Override // com.android.settings.aidl.IRequestCallback
        public void onRequestComplete(int i, String str) {
            MiuiSoundSettings miuiSoundSettings = this.mFragmentRef.get();
            if (i == 2 && miuiSoundSettings != null) {
                miuiSoundSettings.initSoundParams(str);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ZenRuleInfo {
        AutomaticZenRule mAutomaticZenRule;
        Context mContext;
        Alarm.DaysOfWeek mDefBootDof;
        String mDefBootRepeatSummary;
        int mDefEndTime;
        int mDefStartTime;
        String mId;
        String mName;
        ZenModeConfig.ScheduleInfo mScheduleInfo;
        ZenModeConfig.ZenRule mZenRule;

        private ZenRuleInfo() {
        }

        private String addZenRule(AutomaticZenRule automaticZenRule) {
            try {
                String addAutomaticZenRule = NotificationManager.from(this.mContext).addAutomaticZenRule(automaticZenRule);
                Log.i("MiuiSoundSettings", "addZenRule savedRule=" + NotificationManager.from(this.mContext).getAutomaticZenRule(addAutomaticZenRule).toString());
                return addAutomaticZenRule;
            } catch (Exception unused) {
                return null;
            }
        }

        private AutomaticZenRule createAutomaticZenRule(ZenModeConfig.ZenRule zenRule) {
            return new AutomaticZenRule(zenRule.name, zenRule.component, zenRule.conditionId, NotificationManager.zenModeToInterruptionFilter(zenRule.zenMode), zenRule.enabled);
        }

        private void initAutomaticZenRule(ZenModeConfig.ZenRule zenRule) {
            AutomaticZenRule createAutomaticZenRule = createAutomaticZenRule(zenRule);
            this.mAutomaticZenRule = createAutomaticZenRule;
            this.mId = addZenRule(createAutomaticZenRule);
            Log.d("MiuiSoundSettings", "initAutomaticZenRule id=" + this.mId);
            if (TextUtils.isEmpty(this.mId)) {
                return;
            }
            MiuiSoundSettings.this.mZenModeConfig.automaticRules.put(this.mId, this.mZenRule);
        }

        private void initScheduleInfo(Context context) {
            this.mDefStartTime = 1380;
            this.mDefEndTime = 420;
            Alarm.DaysOfWeek daysOfWeek = new Alarm.DaysOfWeek(127);
            this.mDefBootDof = daysOfWeek;
            this.mDefBootRepeatSummary = daysOfWeek.toString(context, true);
            ZenModeConfig.ScheduleInfo scheduleInfo = new ZenModeConfig.ScheduleInfo();
            this.mScheduleInfo = scheduleInfo;
            scheduleInfo.days = SilentModeUtils.getDaysArray(this.mDefBootDof.getBooleanArray());
            ZenModeConfig.ScheduleInfo scheduleInfo2 = this.mScheduleInfo;
            int i = this.mDefStartTime;
            scheduleInfo2.startHour = i / 60;
            scheduleInfo2.startMinute = i % 60;
            int i2 = this.mDefEndTime;
            scheduleInfo2.endHour = i2 / 60;
            scheduleInfo2.endMinute = i2 % 60;
            scheduleInfo2.exitAtAlarm = false;
        }

        private void initZenRule(ZenModeConfig.ScheduleInfo scheduleInfo) {
            ZenModeConfig.ZenRule zenRule = new ZenModeConfig.ZenRule();
            this.mZenRule = zenRule;
            zenRule.enabled = false;
            zenRule.zenMode = 1;
            zenRule.conditionId = ZenModeConfig.toScheduleConditionId(scheduleInfo);
            this.mZenRule.component = ZenModeConfig.getScheduleConditionProvider();
            Log.i("MiuiSoundSettings", "initZenRule component=" + this.mZenRule.component);
            this.mZenRule.name = this.mName;
        }

        private boolean setZenRule(String str, AutomaticZenRule automaticZenRule) {
            boolean z;
            Log.i("MiuiSoundSettings", "setZenRule rule=" + automaticZenRule.toString());
            try {
                z = NotificationManager.from(this.mContext).updateAutomaticZenRule(str, automaticZenRule);
            } catch (Exception e) {
                Log.i("MiuiSoundSettings", "setZenRule error =" + e.getMessage());
                z = false;
            }
            Log.i("MiuiSoundSettings", "setZenRule success=" + z);
            return z;
        }

        public boolean commitRule() {
            ZenModeConfig.ScheduleInfo scheduleInfo = this.mScheduleInfo;
            int i = this.mDefStartTime;
            scheduleInfo.startHour = i / 60;
            scheduleInfo.startMinute = i % 60;
            int i2 = this.mDefEndTime;
            scheduleInfo.endHour = i2 / 60;
            scheduleInfo.endMinute = i2 % 60;
            scheduleInfo.days = SilentModeUtils.getDaysArray(this.mDefBootDof.getBooleanArray());
            ZenModeConfig.ScheduleInfo scheduleInfo2 = this.mScheduleInfo;
            scheduleInfo2.exitAtAlarm = false;
            this.mZenRule.conditionId = ZenModeConfig.toScheduleConditionId(scheduleInfo2);
            ZenModeConfig.ZenRule zenRule = this.mZenRule;
            zenRule.zenMode = 1;
            zenRule.condition = null;
            zenRule.snoozing = false;
            return setZenRule(this.mId, createAutomaticZenRule(zenRule));
        }

        public Alarm.DaysOfWeek getDefBootDof() {
            return this.mDefBootDof;
        }

        public int getDefEndTime() {
            return this.mDefEndTime;
        }

        public int getDefStartTime() {
            return this.mDefStartTime;
        }

        public void init(Context context, ZenModeConfig zenModeConfig) {
            this.mContext = context;
            String string = context.getResources().getString(R.string.timed_titlei);
            Object[] objArr = new Object[1];
            objArr[0] = Integer.valueOf(zenModeConfig != null ? 1 + zenModeConfig.automaticRules.size() : 1);
            this.mName = String.format(string, objArr);
            initScheduleInfo(context);
            initZenRule(this.mScheduleInfo);
            initAutomaticZenRule(this.mZenRule);
        }

        public void setDefBootDof(Alarm.DaysOfWeek daysOfWeek) {
            this.mDefBootDof = daysOfWeek;
            this.mDefBootRepeatSummary = daysOfWeek.toString(this.mContext, true);
        }

        public void setDefEndTime(int i) {
            this.mDefEndTime = i;
        }

        public void setDefStartTime(int i) {
            this.mDefStartTime = i;
        }

        public void updateSceduleInfo(Context context, String str, ZenModeConfig.ZenRule zenRule, ZenModeConfig.ScheduleInfo scheduleInfo, AutomaticZenRule automaticZenRule) {
            this.mContext = context;
            this.mId = str;
            Log.d("MiuiSoundSettings", "updateSceduleInfo id=" + this.mId);
            this.mZenRule = zenRule;
            this.mScheduleInfo = scheduleInfo;
            this.mAutomaticZenRule = automaticZenRule;
            Log.d("MiuiSoundSettings", "updateSceduleInfo mScheduleInfo=" + this.mScheduleInfo);
            ZenModeConfig.ScheduleInfo scheduleInfo2 = this.mScheduleInfo;
            if (scheduleInfo2 == null) {
                return;
            }
            this.mDefStartTime = (scheduleInfo2.startHour * 60) + scheduleInfo2.startMinute;
            this.mDefEndTime = (scheduleInfo2.endHour * 60) + scheduleInfo2.endMinute;
            Alarm.DaysOfWeek daysOfWeek = new Alarm.DaysOfWeek(SilentModeUtils.parseDays(scheduleInfo2.days));
            this.mDefBootDof = daysOfWeek;
            this.mDefBootRepeatSummary = daysOfWeek.toString(context, true);
        }
    }

    static {
        ArrayList arrayList = new ArrayList();
        mRestrictedKeyList = arrayList;
        arrayList.add("ring_volume");
        arrayList.add("media_volume");
        arrayList.add("alarm_volume");
        RULE_COMPARATOR = new Comparator<SortZenRule>() { // from class: com.android.settings.MiuiSoundSettings.3
            private String key(SortZenRule sortZenRule) {
                ZenModeConfig.ZenRule zenRule = sortZenRule.rule;
                return (ZenModeConfig.isValidScheduleConditionId(zenRule.conditionId) ? 1 : ZenModeConfig.isValidEventConditionId(zenRule.conditionId) ? 2 : 3) + zenRule.name;
            }

            @Override // java.util.Comparator
            public int compare(SortZenRule sortZenRule, SortZenRule sortZenRule2) {
                return key(sortZenRule).compareTo(key(sortZenRule2));
            }
        };
    }

    public MiuiSoundSettings() {
        Handler handler = new Handler();
        this.mContentHandler = handler;
        this.mVibrateSettingsObserver = new ContentObserver(handler) { // from class: com.android.settings.MiuiSoundSettings.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                super.onChange(z);
                Log.d("MiuiSoundSettings", "mVibrateSettingsObserver, onChange(), selfChange : " + z);
                MiuiSoundSettings.this.sendRefreshMsg();
            }
        };
        this.mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() { // from class: com.android.settings.MiuiSoundSettings.4
            @Override // miuix.appcompat.app.TimePickerDialog.OnTimeSetListener
            public void onTimeSet(TimePicker timePicker, int i, int i2) {
                if (MiuiSoundSettings.this.mTimeFlag) {
                    MiuiSoundSettings.this.mCurrZenRuleInfo.setDefEndTime((i * 60) + i2);
                    MiuiSoundSettings.this.mAutoRuleTurnOffPref.setLabel(MiuiSoundSettings.this.formatChooseTime(i, i2));
                } else {
                    MiuiSoundSettings.this.mCurrZenRuleInfo.setDefStartTime((i * 60) + i2);
                    MiuiSoundSettings.this.mAutoRuleTurnOnPref.setLabel(MiuiSoundSettings.this.formatChooseTime(i, i2));
                }
                MiuiSoundSettings.this.mCurrZenRuleInfo.commitRule();
            }
        };
    }

    private void enableZenConfig(String str, boolean z) {
        Log.i("MiuiSoundSettings", "enableZenConfig ruleId=" + str);
        if (TextUtils.isEmpty(str)) {
            return;
        }
        AutomaticZenRule automaticZenRule = NotificationManager.from(this.mContext).getAutomaticZenRule(str);
        Log.i("MiuiSoundSettings", "enableZenConfig rule=" + automaticZenRule);
        if (automaticZenRule != null) {
            automaticZenRule.setEnabled(z);
            NotificationManager.from(this.mContext).updateAutomaticZenRule(str, automaticZenRule);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String formatChooseTime(int i, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, i);
        calendar.set(12, i2);
        return DateUtils.formatDateTime(this.mContext, calendar.getTimeInMillis(), 12);
    }

    public static int getHapticFeedbackLevelValue(Context context) {
        if (Settings.System.getInt(context.getContentResolver(), "haptic_feedback_enabled", 1) == 0) {
            return -1;
        }
        return Math.min(2, Math.max(0, Settings.System.getInt(context.getContentResolver(), "haptic_feedback_level", MiuiSettings.System.HAPTIC_FEEDBACK_LEVEL_DEFAULT)));
    }

    public static boolean hideRingtonePreference(Context context) {
        return Build.IS_TABLET || com.android.settingslib.Utils.isWifiOnly(context) || UserHandle.myUserId() != 0 || !Utils.isVoiceCapable(context);
    }

    private void initRingToYouPreference() {
        if (!SettingsFeatures.IS_NEED_ADD_RINGTOYOU) {
            removePreference("ring_toyou");
            removePreference("ring_toyou_check");
            return;
        }
        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("ring_toyou_check");
        final Preference findPreference = findPreference("ring_toyou");
        if (checkBoxPreference == null || findPreference == null) {
            return;
        }
        if (Settings.System.getInt(getContentResolver(), "ring_toyou", 1) != 0) {
            getPreferenceScreen().removePreference(checkBoxPreference);
            getPreferenceScreen().addPreference(findPreference);
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.MiuiSoundSettings$$ExternalSyntheticLambda1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$initRingToYouPreference$0;
                    lambda$initRingToYouPreference$0 = MiuiSoundSettings.this.lambda$initRingToYouPreference$0(preference);
                    return lambda$initRingToYouPreference$0;
                }
            });
            return;
        }
        getPreferenceScreen().removePreference(findPreference);
        getPreferenceScreen().addPreference(checkBoxPreference);
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.MiuiSoundSettings$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                boolean lambda$initRingToYouPreference$1;
                lambda$initRingToYouPreference$1 = MiuiSoundSettings.this.lambda$initRingToYouPreference$1(checkBoxPreference, findPreference, preference, obj);
                return lambda$initRingToYouPreference$1;
            }
        });
    }

    private void initRingtoneType() {
        Log.v("MiuiSoundSettings", "init all ringtone type");
        if (this.mSupportCoolSound) {
            return;
        }
        MiuiDefaultRingtonePreference miuiDefaultRingtonePreference = this.mRingtonePreference;
        if (miuiDefaultRingtonePreference != null) {
            miuiDefaultRingtonePreference.setRingtoneType(1);
        }
        MiuiDefaultRingtonePreference miuiDefaultRingtonePreference2 = this.mSmsReceivedSoundPreference;
        if (miuiDefaultRingtonePreference2 != null) {
            miuiDefaultRingtonePreference2.setRingtoneType(16);
        }
        MiuiDefaultRingtonePreference miuiDefaultRingtonePreference3 = this.mSmsDeliveredSoundPreference;
        if (miuiDefaultRingtonePreference3 != null) {
            miuiDefaultRingtonePreference3.setRingtoneType(8);
        }
        MiuiDefaultRingtonePreference miuiDefaultRingtonePreference4 = this.mCalendarSoundPreference;
        if (miuiDefaultRingtonePreference4 != null) {
            miuiDefaultRingtonePreference4.setRingtoneType(4096);
        }
        MiuiDefaultRingtonePreference miuiDefaultRingtonePreference5 = this.mNotificationSoundPreference;
        if (miuiDefaultRingtonePreference5 != null) {
            miuiDefaultRingtonePreference5.setRingtoneType(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initSoundParams(String str) {
        Handler handler;
        if (TextUtils.isEmpty(str) || !ParseMiShopDataUtils.showBasicItems(str) || (handler = this.mHandler) == null) {
            return;
        }
        Message obtainMessage = handler.obtainMessage(6);
        obtainMessage.obj = str;
        this.mHandler.sendMessage(obtainMessage);
    }

    private void initVolumePreference(String str, int i, int i2) {
        VolumeSeekBarPreference volumeSeekBarPreference = (VolumeSeekBarPreference) findPreference(str);
        if (volumeSeekBarPreference == null) {
            return;
        }
        volumeSeekBarPreference.setStream(i);
        volumeSeekBarPreference.setIcon(i2);
        volumeSeekBarPreference.setSeekBarVolumizer(new SeekBarVolumizer(volumeSeekBarPreference));
        this.mVolumePrefs.add(volumeSeekBarPreference);
    }

    private boolean isAutomationRuleEnabled(ZenModeConfig zenModeConfig) {
        ArrayMap arrayMap;
        if (zenModeConfig == null || (arrayMap = zenModeConfig.automaticRules) == null) {
            return false;
        }
        Iterator it = arrayMap.values().iterator();
        while (it.hasNext()) {
            if (((ZenModeConfig.ZenRule) it.next()).enabled) {
                return true;
            }
        }
        return false;
    }

    private boolean isInCommunication() {
        return ((TelecomManager) getContext().getSystemService("telecom")).isInCall() || this.mAudioManager.getMode() == 3;
    }

    public static boolean isSystemHapticEnable(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "haptic_feedback_enabled", 1) == 1;
    }

    private boolean isValidScheduleConditionId(Uri uri) {
        return ZenModeConfig.tryParseScheduleConditionId(uri) != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initRingToYouPreference$0(Preference preference) {
        if (preference.getFragment() == null || !(getActivity() instanceof PreferenceFragmentCompat.OnPreferenceStartFragmentCallback)) {
            return false;
        }
        return ((PreferenceFragmentCompat.OnPreferenceStartFragmentCallback) getActivity()).onPreferenceStartFragment(this, preference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initRingToYouPreference$1(CheckBoxPreference checkBoxPreference, Preference preference, Preference preference2, Object obj) {
        if (checkBoxPreference.isChecked()) {
            return true;
        }
        getPreferenceScreen().addPreference(preference);
        getPreferenceScreen().removePreference(checkBoxPreference);
        Settings.System.putInt(getContentResolver(), "ring_toyou", checkBoxPreference.isChecked() ? 1 : 0);
        return true;
    }

    private void reapplyPolicyWithCurrent() {
        this.mNotificationManager.setNotificationPolicy(this.mNotificationManager.getNotificationPolicy());
    }

    private void refreshMuteModeSetting() {
        if (this.mRingerModeSettingPref == null) {
            return;
        }
        this.mRingerModeSettingPref.setChecked(MiuiSettings.SoundMode.isSilenceModeOn(this.mContext));
        int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "mute_music_at_silent", -1, -3);
        Log.d("MiuiSoundSettings", "refreshMuteModeSetting(), muteMediaValue : " + intForUser);
        if (intForUser == 1) {
            this.mMuteMediaSoundPref.setChecked(true);
        } else if (intForUser == 0) {
            this.mMuteMediaSoundPref.setChecked(false);
        } else {
            this.mMuteMediaSoundPref.setChecked(false);
        }
        refreshVolumePrefDrawable();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshVipListUI() {
        ZenModeConfig zenModeConfig = this.mNotificationManager.getZenModeConfig();
        boolean z = Build.IS_TABLET;
        if (!z) {
            NotificationManager.Policy notificationPolicy = this.mNotificationManager.getNotificationPolicy();
            Log.d("MiuiSoundSettings", "refreshVipListUI(), current policy : " + notificationPolicy);
            this.mRepeatedIncallPref.setChecked((notificationPolicy.priorityCategories & 16) != 0);
        }
        if (z || !isAdded()) {
            return;
        }
        this.mVipListPref.setValue(String.valueOf(zenModeConfig.allowCalls ? zenModeConfig.allowCallsFrom : getResources().getStringArray(R.array.vip_mode_text).length - 1));
    }

    private void refreshVolumePrefDrawable() {
        ArrayList<VolumeSeekBarPreference> arrayList = this.mVolumePrefs;
        if (arrayList == null) {
            return;
        }
        Iterator<VolumeSeekBarPreference> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().updateSeekBarDrawable();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshZenModeSetting() {
        if (this.mZenModeTogglePref == null) {
            return;
        }
        this.mZenModeTogglePref.setChecked(this.mNotificationManager.getZenMode() != 0);
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "zen_mode_intercepted_when_unlocked", -1);
        if (i == 0) {
            this.mScreenLockedOnlyPref.setChecked(true);
        } else if (i == 1) {
            this.mScreenLockedOnlyPref.setChecked(false);
        } else {
            this.mScreenLockedOnlyPref.setChecked(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshZenRuleSettings() {
        if (this.mZenAutoRuleTogglePref == null) {
            return;
        }
        boolean isAutomationRuleEnabled = isAutomationRuleEnabled(this.mZenModeConfig);
        Log.i("MiuiSoundSettings", "refreshZenRuleSettings autoRuleEnable=" + isAutomationRuleEnabled);
        this.mZenAutoRuleTogglePref.setChecked(isAutomationRuleEnabled);
        this.mAutoRuleTurnOnPref.setVisible(isAutomationRuleEnabled);
        this.mAutoRuleTurnOffPref.setVisible(isAutomationRuleEnabled);
        this.mAutoRuleRepeatPref.setVisible(isAutomationRuleEnabled);
        ZenRuleInfo zenRuleInfo = this.mCurrZenRuleInfo;
        if (zenRuleInfo == null) {
            return;
        }
        this.mAutoRuleTurnOnPref.setLabel(formatChooseTime(zenRuleInfo.getDefStartTime() / 60, this.mCurrZenRuleInfo.getDefStartTime() % 60));
        this.mAutoRuleTurnOffPref.setLabel(formatChooseTime(this.mCurrZenRuleInfo.getDefEndTime() / 60, this.mCurrZenRuleInfo.getDefEndTime() % 60));
        this.mAutoRuleRepeatPref.setDaysOfWeek(this.mCurrZenRuleInfo.getDefBootDof());
        Log.i("MiuiSoundSettings", "refreshZenRuleSettings autoRuleEnable=" + this.mCurrZenRuleInfo.getDefBootDof());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendRefreshMsg() {
        Handler handler = this.mHandler;
        if (handler == null) {
            return;
        }
        handler.removeMessages(2);
        this.mHandler.sendEmptyMessage(2);
    }

    private void setFragmentTitle() {
        int i = R.string.sound_settings;
        Vibrator vibrator = (Vibrator) getSystemService("vibrator");
        if (SettingsFeatures.isSupportSettingsHaptic(getActivity())) {
            i = R.string.sound_haptic_settings;
        } else if (vibrator.hasVibrator()) {
            i = R.string.sound_vibrate_settings;
        }
        ActionBar appCompatActionBar = getAppCompatActionBar();
        if (appCompatActionBar == null || i <= 0) {
            return;
        }
        appCompatActionBar.setTitle(i);
    }

    private void setHapticFeedbackLevelValue(int i) {
        if (i >= 0) {
            Settings.System.putInt(getContentResolver(), "haptic_feedback_enabled", 1);
            Settings.System.putInt(getContentResolver(), "haptic_feedback_intensity", ((Vibrator) getActivity().getSystemService(Vibrator.class)).getDefaultHapticFeedbackIntensity());
            Settings.System.putInt(getContentResolver(), "haptic_feedback_level", i);
            new HapticFeedbackUtil(getActivity(), true).performHapticFeedback(1, false);
        } else {
            Settings.System.putInt(getContentResolver(), "haptic_feedback_enabled", 0);
        }
        this.mHapticFeedbackLevel.setValue(String.valueOf(i));
        DropDownPreference dropDownPreference = this.mHapticFeedbackLevel;
        dropDownPreference.setSummary(dropDownPreference.getEntry());
    }

    public static void setSystemHapticEnable(Context context, boolean z) {
        Settings.System.putInt(context.getContentResolver(), "haptic_feedback_enabled", z ? 1 : 0);
    }

    private void updateMuteCheckPref(boolean z) {
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "mute_music_at_silent", z ? 1 : 0, -3);
        refreshVolumePrefDrawable();
        sendRefreshMsg();
    }

    private void updateRepeatedIncallPref(boolean z) {
        NotificationManager.Policy notificationPolicy = this.mNotificationManager.getNotificationPolicy();
        int i = notificationPolicy.priorityCategories;
        this.mNotificationManager.setNotificationPolicy(new NotificationManager.Policy(z ? i | 16 : i & (-17), notificationPolicy.priorityCallSenders, notificationPolicy.priorityMessageSenders, notificationPolicy.suppressedVisualEffects, notificationPolicy.state));
    }

    private void updateRingerModeSettingPref(boolean z) {
        MiuiSettings.SoundMode.setSilenceModeOn(this.mContext, z);
        refreshVolumePrefDrawable();
        sendRefreshMsg();
    }

    private void updateVibrateInNormalPref(boolean z) {
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "vibrate_in_normal", z ? 1 : 0, -3);
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "vibrate_when_ringing", z ? 1 : 0, -3);
    }

    private void updateVibrateInSilentPref(boolean z) {
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "vibrate_in_silent", z ? 1 : 0, -3);
        if (this.mAudioManager.getRingerModeInternal() != 2) {
            this.mAudioManager.setRingerModeInternal(z ? 1 : 0);
        }
    }

    private void updateVipListPref(Object obj) {
        int i;
        int i2;
        int i3;
        int parseInt = Integer.parseInt((String) obj);
        boolean z = parseInt <= 2;
        NotificationManager.Policy notificationPolicy = this.mNotificationManager.getNotificationPolicy();
        int i4 = notificationPolicy.priorityCategories;
        int i5 = notificationPolicy.priorityCallSenders;
        int i6 = notificationPolicy.priorityMessageSenders;
        if (z) {
            int i7 = i4 | 8;
            if (this.mNotificationManager.getZenMode() == 0) {
                i2 = parseInt;
                i3 = i2;
                i = i7 | 4;
                this.mNotificationManager.setNotificationPolicy(new NotificationManager.Policy(i, i2, i3, notificationPolicy.suppressedVisualEffects, notificationPolicy.state));
            }
            i2 = parseInt;
            i = i7 & (-5);
        } else {
            i = i4 & (-9) & (-5);
            i2 = i5;
        }
        i3 = i6;
        this.mNotificationManager.setNotificationPolicy(new NotificationManager.Policy(i, i2, i3, notificationPolicy.suppressedVisualEffects, notificationPolicy.state));
    }

    private void updateZenCheckPref(boolean z) {
        MiuiSettings.SoundMode.setZenModeOn(this.mContext, z, "MiuiSoundSettings");
    }

    private void updateZenConfig(boolean z) {
        ZenRuleInfo zenRuleInfo = this.mCurrZenRuleInfo;
        if (zenRuleInfo != null) {
            String str = zenRuleInfo.mId;
            Log.i("MiuiSoundSettings", "updateZenConfig ruleId=" + str);
            if (TextUtils.isEmpty(str)) {
                return;
            }
            AutomaticZenRule automaticZenRule = NotificationManager.from(this.mContext).getAutomaticZenRule(str);
            Log.i("MiuiSoundSettings", "updateZenConfig rule=" + automaticZenRule);
            if (automaticZenRule != null) {
                automaticZenRule.setEnabled(z);
                NotificationManager.from(this.mContext).updateAutomaticZenRule(str, automaticZenRule);
            }
            this.mCurrZenRuleInfo.mZenRule.enabled = z;
            refreshZenRuleSettings();
        }
    }

    private void updateZenLockScreenOnlyPref(boolean z) {
        Settings.System.putInt(this.mContext.getContentResolver(), "zen_mode_intercepted_when_unlocked", !z ? 1 : 0);
        reapplyPolicyWithCurrent();
    }

    private void updateZenModeConfig() {
        ZenModeConfig zenModeConfig = SilentModeUtils.getZenModeConfig(this.mContext);
        if (zenModeConfig == null) {
            Log.i("MiuiSoundSettings", "updateZenModeConfig getZenModeConfig is null");
        } else if (!zenModeConfig.equals(this.mZenModeConfig)) {
            this.mZenModeConfig = zenModeConfig;
            this.mCurrZenRuleInfo = new ZenRuleInfo();
            Log.d("MiuiSoundSettings", "updateZenModeConfig mConfig=" + this.mZenModeConfig);
            ArrayMap arrayMap = this.mZenModeConfig.automaticRules;
            if (arrayMap == null || arrayMap.size() <= 0) {
                Log.d("MiuiSoundSettings", "updateZenModeConfig mConfig.automaticRules is null");
                this.mCurrZenRuleInfo.init(this.mContext, this.mZenModeConfig);
                return;
            }
            int size = arrayMap.size();
            SortZenRule[] sortZenRuleArr = new SortZenRule[size];
            for (int i = 0; i < arrayMap.size(); i++) {
                SortZenRule sortZenRule = new SortZenRule();
                sortZenRule.id = (String) arrayMap.keyAt(i);
                sortZenRule.rule = (ZenModeConfig.ZenRule) arrayMap.valueAt(i);
                sortZenRuleArr[i] = sortZenRule;
            }
            Arrays.sort(sortZenRuleArr, RULE_COMPARATOR);
            SortZenRule sortZenRule2 = sortZenRuleArr[0];
            for (int i2 = 0; i2 < size; i2++) {
                if (isValidScheduleConditionId(sortZenRuleArr[i2].rule.conditionId)) {
                    Log.i("MiuiSoundSettings", "updateZenModeConfig zenRule=" + sortZenRuleArr[i2].rule.toString());
                    if (sortZenRuleArr[i2].rule.enabled) {
                        if (!sortZenRule2.rule.enabled) {
                            sortZenRule2 = sortZenRuleArr[i2];
                        } else if (sortZenRule2 != sortZenRuleArr[i2]) {
                            enableZenConfig(sortZenRuleArr[i2].id, false);
                        }
                    }
                }
            }
            if (!isValidScheduleConditionId(sortZenRule2.rule.conditionId)) {
                Log.d("MiuiSoundSettings", "updateZenModeConfig not validScheduleConditionId");
                this.mCurrZenRuleInfo.init(this.mContext, this.mZenModeConfig);
                return;
            }
            String str = sortZenRule2.id;
            Log.d("MiuiSoundSettings", "updateZenModeConfig id=" + str);
            this.mZenModeConfig.automaticRules.valueAt(0);
            this.mCurrZenRuleInfo.updateSceduleInfo(this.mContext, str, sortZenRule2.rule, ZenModeConfig.tryParseScheduleConditionId(sortZenRule2.rule.conditionId), NotificationManager.from(this.mContext).getAutomaticZenRule(str));
        }
    }

    public void enableWorkSync() {
        MiuiWorkSoundPreferenceController miuiWorkSoundPreferenceController = this.workSoundController;
        if (miuiWorkSoundPreferenceController != null) {
            miuiWorkSoundPreferenceController.enableWorkSync();
        }
    }

    @Override // com.android.settings.MiuiSoundSettingsBase, com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return "MiuiSoundSettings";
    }

    @Override // com.android.settings.MiuiSoundSettingsBase
    protected void handleOthersSummery(Message message) {
        MiuiDefaultRingtonePreference miuiDefaultRingtonePreference;
        int i = message.what;
        if (i == 3) {
            MiuiDefaultRingtonePreference miuiDefaultRingtonePreference2 = this.mSmsReceivedSoundPreference;
            if (miuiDefaultRingtonePreference2 != null) {
                miuiDefaultRingtonePreference2.setSummary((CharSequence) message.obj);
            }
        } else if (i != 4) {
            if (i == 6 && (miuiDefaultRingtonePreference = this.mCalendarSoundPreference) != null) {
                miuiDefaultRingtonePreference.setSummary((CharSequence) message.obj);
            }
        } else {
            MiuiDefaultRingtonePreference miuiDefaultRingtonePreference3 = this.mSmsDeliveredSoundPreference;
            if (miuiDefaultRingtonePreference3 != null) {
                miuiDefaultRingtonePreference3.setSummary((CharSequence) message.obj);
            }
        }
    }

    @Override // com.android.settings.MiuiSoundSettingsBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        RingtoneCardPreference ringtoneCardPreference;
        Preference findPreference;
        super.onCreate(bundle);
        this.mContext = requireActivity().getApplicationContext();
        this.mUserId = UserHandle.myUserId();
        this.mNotificationManager = NotificationManager.from(this.mContext);
        initVolumePreference("ring_volume", 2, R.drawable.ring_volume_icon);
        initVolumePreference("alarm_volume", 4, R.drawable.alarm_volume_icon);
        initVolumePreference("media_volume", 3, R.drawable.media_volume_icon);
        if (MiuiUtils.includeXiaoAi(this.mContext)) {
            initVolumePreference("voice_assist_volume", 11, R.drawable.xiaoai_volume_icon);
            mRestrictedKeyList.add("voice_assist_volume");
        } else {
            getPreferenceScreen().removePreference(findPreference("voice_assist_volume"));
        }
        this.mSoundModeCategory = (PreferenceCategory) findPreference("sound_mode_category");
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("ringer_mode_setting");
        this.mRingerModeSettingPref = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("mute_media_sound");
        this.mMuteMediaSoundPref = checkBoxPreference2;
        checkBoxPreference2.setOnPreferenceChangeListener(this);
        refreshMuteModeSetting();
        this.mZenModeCategory = (PreferenceCategory) findPreference("zen_mode_category");
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("zen_mode_toggle");
        this.mZenModeTogglePref = checkBoxPreference3;
        checkBoxPreference3.setOnPreferenceChangeListener(this);
        CheckBoxPreference checkBoxPreference4 = (CheckBoxPreference) findPreference("screen_locked_only");
        this.mScreenLockedOnlyPref = checkBoxPreference4;
        checkBoxPreference4.setOnPreferenceChangeListener(this);
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("vip_list_setting");
        this.mVipListPref = dropDownPreference;
        boolean z = Build.IS_TABLET;
        if (z) {
            this.mZenModeCategory.removePreference(dropDownPreference);
        } else {
            dropDownPreference.setOnPreferenceChangeListener(this);
        }
        CheckBoxPreference checkBoxPreference5 = (CheckBoxPreference) findPreference("repeated_incall_notification");
        this.mRepeatedIncallPref = checkBoxPreference5;
        checkBoxPreference5.setSummary(String.format(getResources().getString(R.string.repeat_call_info), 15));
        if (z) {
            this.mZenModeCategory.removePreference(this.mRepeatedIncallPref);
        } else {
            this.mRepeatedIncallPref.setOnPreferenceChangeListener(this);
            this.mRepeatedIncallPref.setChecked(MiuiSettings.AntiSpam.isRepeatedCallActionEnable(this.mContext));
        }
        refreshZenModeSetting();
        refreshVipListUI();
        updateZenModeConfig();
        CustomCheckBoxPreference customCheckBoxPreference = (CustomCheckBoxPreference) findPreference("zen_mode_automatic_rule_toggle");
        this.mZenAutoRuleTogglePref = customCheckBoxPreference;
        customCheckBoxPreference.setOnPreferenceChangeListener(this);
        this.mAutoRuleTurnOnPref = (LabelPreferenceWithBg) findPreference("time_turn_on");
        this.mAutoRuleTurnOffPref = (LabelPreferenceWithBg) findPreference("time_turn_off");
        this.mAutoRuleRepeatPref = (RepeatPreferenceWithBg) findPreference("repeat_days");
        this.mAutoRuleTurnOnPref.setOnPreferenceClickListener(this);
        this.mAutoRuleTurnOffPref.setOnPreferenceClickListener(this);
        this.mAutoRuleRepeatPref.setOnPreferenceChangeListener(this);
        refreshZenRuleSettings();
        CheckBoxPreference checkBoxPreference6 = (CheckBoxPreference) findPreference("ring_toyou_check");
        findPreference("ring_toyou");
        initRingToYouPreference();
        DropDownPreference dropDownPreference2 = this.mHapticFeedbackLevel;
        if (dropDownPreference2 != null) {
            dropDownPreference2.setOnPreferenceChangeListener(this);
            this.mHapticFeedbackLevel.setValue(String.valueOf(getHapticFeedbackLevelValue(getActivity())));
            DropDownPreference dropDownPreference3 = this.mHapticFeedbackLevel;
            dropDownPreference3.setSummary(dropDownPreference3.getEntry());
        }
        this.mSmsReceivedSoundPreference = (MiuiDefaultRingtonePreference) findPreference("sms_received_sound");
        this.mSmsDeliveredSoundPreference = (MiuiDefaultRingtonePreference) findPreference("sms_delivered_sound");
        this.mCalendarSoundPreference = (MiuiDefaultRingtonePreference) findPreference("calendar_sound");
        this.mNotificationSoundPreference = (MiuiDefaultRingtonePreference) findPreference("notification_sound");
        this.mAlarmRingtonePreference = (MiuiDefaultRingtonePreference) findPreference("alarm_ringtone");
        if (this.mSupportCoolSound) {
            removePreference("sms_received_sound");
            removePreference("sms_delivered_sound");
            removePreference("calendar_sound");
            removePreference("notification_sound");
            this.mSmsReceivedSoundPreference = null;
            this.mSmsDeliveredSoundPreference = null;
            this.mCalendarSoundPreference = null;
            this.mNotificationSoundPreference = null;
        }
        if (com.android.settingslib.Utils.isWifiOnly(getActivity()) && this.mSmsDeliveredSoundPreference != null) {
            getPreferenceScreen().removePreference(this.mSmsDeliveredSoundPreference);
            this.mSmsDeliveredSoundPreference = null;
        }
        if (SettingsFeatures.isNeedRemoveSmsReceivedSound(getActivity()) && this.mSmsReceivedSoundPreference != null) {
            getPreferenceScreen().removePreference(this.mSmsReceivedSoundPreference);
            this.mSmsReceivedSoundPreference = null;
        }
        this.mAudioManager = (AudioManager) getSystemService("audio");
        this.mSilentModePref = (CheckBoxPreference) findPreference("silent_mode");
        this.mVibrateWhenSilentPref = (CheckBoxPreference) findPreference("key_vibrate_when_silent");
        this.mVibrateWhenRingingPref = (CheckBoxPreference) findPreference("key_vibrate_when_ringing");
        this.mSilentModePref.setOnPreferenceChangeListener(this);
        this.mVibrateWhenSilentPref.setOnPreferenceChangeListener(this);
        this.mVibrateWhenRingingPref.setOnPreferenceChangeListener(this);
        if (MiuiSettings.SilenceMode.isSupported) {
            getPreferenceScreen().removePreference(this.mSilentModePref);
            this.mSilentModePref = null;
        } else {
            getPreferenceScreen().removePreference(this.mSoundModeCategory);
            this.mSoundModeCategory = null;
            getPreferenceScreen().removePreference(this.mZenModeCategory);
            this.mZenModeCategory = null;
        }
        if (!((Vibrator) getSystemService("vibrator")).hasVibrator()) {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            CheckBoxPreference checkBoxPreference7 = this.mVibrateWhenSilentPref;
            if (checkBoxPreference7 != null) {
                preferenceScreen.removePreference(checkBoxPreference7);
            }
            CheckBoxPreference checkBoxPreference8 = this.mVibrateWhenRingingPref;
            if (checkBoxPreference8 != null) {
                preferenceScreen.removePreference(checkBoxPreference8);
            }
            Preference findPreference2 = findPreference("miui_vibrate_category");
            if (findPreference2 != null) {
                preferenceScreen.removePreference(findPreference2);
            }
            this.mVibrateWhenRingingPref = null;
            this.mVibrateWhenSilentPref = null;
        }
        if (z && this.mVibrateWhenRingingPref != null) {
            getPreferenceScreen().removePreference(this.mVibrateWhenRingingPref);
            this.mVibrateWhenRingingPref = null;
        }
        CheckBoxPreference checkBoxPreference9 = this.mSystemHapticPreference;
        if (checkBoxPreference9 != null) {
            checkBoxPreference9.setChecked(isSystemHapticEnable(this.mContext));
            this.mSystemHapticPreference.setOnPreferenceChangeListener(this);
        }
        this.mRingtonePreference = (MiuiDefaultRingtonePreference) findPreference(ThemeManagerConstants.COMPONENT_CODE_RINGTONE);
        if (hideRingtonePreference(getActivity()) && this.mRingtonePreference != null) {
            getPreferenceScreen().removePreference(this.mRingtonePreference);
            this.mRingtonePreference = null;
        }
        initRingtoneType();
        this.mObserver.register();
        this.workSoundController = new MiuiWorkSoundPreferenceController(getPreferenceScreen(), this);
        this.mMiuiAlarmRingtoneController = new MiuiAlarmRingtonePreferenceController(getPreferenceScreen());
        this.mControllers.add(this.workSoundController);
        this.mControllers.add(this.mMiuiAlarmRingtoneController);
        if (!SettingsFeatures.isIncallShowNeeded(getContext()) && (findPreference = findPreference("incall_show")) != null) {
            getPreferenceScreen().removePreference(findPreference);
        }
        if (!SettingsFeatures.isMisoundShowNeeded(getContext())) {
            Preference findPreference3 = findPreference(PageIndexManager.KEY_HEADSET_SETTINGS);
            if (findPreference3 != null) {
                getPreferenceScreen().removePreference(findPreference3);
            }
            Preference findPreference4 = findPreference("sound_assist_settings");
            if (findPreference4 != null) {
                getPreferenceScreen().removePreference(findPreference4);
            }
        }
        if (hideRingtonePreference(this.mContext) && (ringtoneCardPreference = this.mRingtoneCardPreference) != null) {
            ringtoneCardPreference.setDisable(0);
        }
        this.mSoundSpeakerCategory = (PreferenceCategory) findPreference("sound_speaker_category");
        SoundSpeakerDescPreference soundSpeakerDescPreference = (SoundSpeakerDescPreference) findPreference("sound_speaker_preference");
        this.mSoundSpeakerPreference = soundSpeakerDescPreference;
        soundSpeakerDescPreference.setVisible(false);
        this.mSoundSpeakerCategory.setVisible(false);
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        this.mDeviceInfoCallback = new UpdateInfoCallback(this);
        this.mRemoteServiceConn = new RemoteServiceConn();
        RemoteServiceUtil.bindRemoteService(getActivity(), this.mRemoteServiceConn);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        Iterator<MiuiBaseController> it = this.mControllers.iterator();
        while (it.hasNext()) {
            it.next().destroy();
        }
        Iterator<VolumeSeekBarPreference> it2 = this.mVolumePrefs.iterator();
        while (it2.hasNext()) {
            it2.next().getSeekBarVolumizer().stop();
        }
        CheckBoxPreference checkBoxPreference = this.mSilentModePref;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(null);
        }
        CheckBoxPreference checkBoxPreference2 = this.mVibrateWhenSilentPref;
        if (checkBoxPreference2 != null) {
            checkBoxPreference2.setOnPreferenceChangeListener(null);
        }
        CheckBoxPreference checkBoxPreference3 = this.mVibrateWhenRingingPref;
        if (checkBoxPreference3 != null) {
            checkBoxPreference3.setOnPreferenceChangeListener(null);
        }
        this.mObserver.unregister();
        IRemoteGetDeviceInfoService iRemoteGetDeviceInfoService = this.mRemoteService;
        if (iRemoteGetDeviceInfoService != null) {
            try {
                UpdateInfoCallback updateInfoCallback = this.mDeviceInfoCallback;
                if (updateInfoCallback != null) {
                    iRemoteGetDeviceInfoService.unregisteCallback(updateInfoCallback);
                    this.mDeviceInfoCallback = null;
                }
                this.mRemoteService = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (this.mRemoteServiceConn != null) {
            RemoteServiceUtil.unBindRemoteService(getActivity(), this.mRemoteServiceConn);
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override // com.android.settings.MiuiSoundSettingsBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        Iterator<MiuiBaseController> it = this.mControllers.iterator();
        while (it.hasNext()) {
            it.next().pause();
        }
        getContentResolver().unregisterContentObserver(this.mVibrateSettingsObserver);
        getActivity().unregisterReceiver(this.mRingerModeReceiver);
        this.mHandler.removeMessages(1);
        super.onPause();
        Iterator<VolumeSeekBarPreference> it2 = this.mVolumePrefs.iterator();
        while (it2.hasNext()) {
            it2.next().getSeekBarVolumizer().pause();
        }
        this.mHandler.removeMessages(2);
        ZenRuleInfo zenRuleInfo = this.mCurrZenRuleInfo;
        if (zenRuleInfo != null) {
            zenRuleInfo.commitRule();
            String str = this.mRingerModeSettingPref.isChecked() ? "on" : "off";
            String str2 = this.mMuteMediaSoundPref.isChecked() ? "silent" : "media";
            if (this.mRingerModeSettingPref.isChecked()) {
                str = str + "_" + str2;
            }
            MiStatInterfaceUtils.trackPreferenceValue("ringer_mode_setting", str);
            OneTrackInterfaceUtils.trackSwitchEvent("ringer_mode_setting", this.mRingerModeSettingPref.isChecked());
            OneTrackInterfaceUtils.trackSwitchEvent("mute_media_sound", this.mMuteMediaSoundPref.isChecked());
            String str3 = this.mZenModeTogglePref.isChecked() ? "on" : "off";
            String str4 = this.mScreenLockedOnlyPref.isChecked() ? "always" : "lockscreen";
            if (this.mZenModeTogglePref.isChecked()) {
                str3 = str3 + "_" + str4;
            }
            MiStatInterfaceUtils.trackPreferenceValue("zen_mode_toggle", str3);
            OneTrackInterfaceUtils.trackSwitchEvent("zen_mode_toggle", this.mZenModeTogglePref.isChecked());
            OneTrackInterfaceUtils.trackSwitchEvent("screen_locked_only", this.mScreenLockedOnlyPref.isChecked());
            String str5 = this.mZenAutoRuleTogglePref.isChecked() ? "on" : "off";
            Alarm.DaysOfWeek daysOfWeek = this.mCurrZenRuleInfo.mDefBootDof;
            String str6 = (daysOfWeek == null || !daysOfWeek.isRepeatSet()) ? "null" : "repeat";
            if (this.mZenAutoRuleTogglePref.isChecked()) {
                str5 = str5 + "_" + str6;
            }
            MiStatInterfaceUtils.trackPreferenceValue("zen_mode_automatic_rule_toggle", str5);
            OneTrackInterfaceUtils.trackPreferenceValue("zen_mode_automatic_rule_toggle", str5);
        }
    }

    @Override // com.android.settings.MiuiSoundSettingsBase, androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (getListView().isComputingLayout()) {
            Log.e("MiuiSoundSettings", "isComputingLayout");
            return false;
        } else if (preference == this.mHapticFeedbackLevel) {
            setHapticFeedbackLevelValue(Integer.parseInt((String) obj));
            return true;
        } else if (preference == this.mSystemHapticPreference) {
            setSystemHapticEnable(this.mContext, ((Boolean) obj).booleanValue());
            return true;
        } else if (preference == this.mSilentModePref && !MiuiSettings.SilenceMode.isSupported) {
            MiuiSettings.SoundMode.setSilenceModeOn(this.mContext, ((Boolean) obj).booleanValue());
            return true;
        } else {
            CheckBoxPreference checkBoxPreference = this.mVibrateWhenRingingPref;
            if (checkBoxPreference != null && preference == checkBoxPreference) {
                InternationalCompat.trackReportSwitchStatus("setting_sound_sring", obj);
                updateVibrateInNormalPref(((Boolean) obj).booleanValue());
                return true;
            }
            CheckBoxPreference checkBoxPreference2 = this.mVibrateWhenSilentPref;
            if (checkBoxPreference2 != null && preference == checkBoxPreference2) {
                InternationalCompat.trackReportSwitchStatus("setting_sound_smute", obj);
                updateVibrateInSilentPref(((Boolean) obj).booleanValue());
                return true;
            } else if (preference == this.mRingerModeSettingPref) {
                StringBuilder sb = new StringBuilder();
                sb.append("mRingerModeSettingPref change, objValue : ");
                Boolean bool = (Boolean) obj;
                sb.append(bool);
                Log.d("MiuiSoundSettings", sb.toString());
                updateRingerModeSettingPref(bool.booleanValue());
                return true;
            } else if (preference == this.mMuteMediaSoundPref) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("mMediaSoundSettingPref change, objValue : ");
                Boolean bool2 = (Boolean) obj;
                sb2.append(bool2);
                Log.d("MiuiSoundSettings", sb2.toString());
                updateMuteCheckPref(bool2.booleanValue());
                return true;
            } else if (preference == this.mZenModeTogglePref) {
                updateZenCheckPref(((Boolean) obj).booleanValue());
                return true;
            } else if (preference == this.mScreenLockedOnlyPref) {
                updateZenLockScreenOnlyPref(((Boolean) obj).booleanValue());
                return true;
            } else if (preference == this.mZenAutoRuleTogglePref) {
                Log.i("MiuiSoundSettings", "onPreferenceChange mZenAutoRuleTogglePref");
                updateZenConfig(((Boolean) obj).booleanValue());
                return true;
            } else {
                RepeatPreferenceWithBg repeatPreferenceWithBg = this.mAutoRuleRepeatPref;
                if (preference == repeatPreferenceWithBg) {
                    this.mCurrZenRuleInfo.setDefBootDof(repeatPreferenceWithBg.getDaysOfWeek());
                    this.mCurrZenRuleInfo.commitRule();
                    return true;
                } else if (preference == this.mVipListPref) {
                    updateVipListPref(obj);
                    return true;
                } else if (preference == this.mRepeatedIncallPref) {
                    updateRepeatedIncallPref(((Boolean) obj).booleanValue());
                    return true;
                } else {
                    return true;
                }
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        int defEndTime;
        String key = preference.getKey();
        if (TextUtils.equals(key, "time_turn_on") || TextUtils.equals(key, "time_turn_off")) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this.mOnTimeSetListener, this.mCurrZenRuleInfo.getDefStartTime() / 60, this.mCurrZenRuleInfo.getDefStartTime() % 60, DateFormat.is24HourFormat(getActivity()));
            if (TextUtils.equals(key, "time_turn_on")) {
                this.mTimeFlag = false;
                defEndTime = this.mCurrZenRuleInfo.getDefStartTime();
                timePickerDialog.setTitle(R.string.time_zen_mode_turn_on);
            } else {
                this.mTimeFlag = true;
                defEndTime = this.mCurrZenRuleInfo.getDefEndTime();
                timePickerDialog.setTitle(R.string.paper_mode_end_time_title);
            }
            if (defEndTime <= 0) {
                defEndTime = 0;
            }
            timePickerDialog.updateTime(defEndTime / 60, defEndTime % 60);
            timePickerDialog.show();
        }
        return false;
    }

    @Override // com.android.settings.MiuiSoundSettingsBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (isAdded()) {
            MiStatInterfaceUtils.trackPreferenceClick(getName(), MiuiUtils.getResourceName(getActivity(), preference.getTitle()));
        }
        if (preference instanceof MiuiWorkRingtonePreference) {
            MiuiWorkRingtonePreference miuiWorkRingtonePreference = (MiuiWorkRingtonePreference) preference;
            this.mRequestPreference = miuiWorkRingtonePreference;
            int userId = miuiWorkRingtonePreference.getUserId();
            Intent intent = this.mRequestPreference.getIntent();
            if (MiuiUtils.isIntentActivityExistAsUser(this.mContext, intent, userId)) {
                getActivity().startActivityForResultAsUser(intent, 200, null, UserHandle.of(userId));
                return true;
            }
            new AlertDialog.Builder(getActivity()).setTitle(R.string.work_sound_permission_dialog_title).setMessage(R.string.work_sound_permission_dialog_message).setNeutralButton(R.string.work_sound_permission_dialog_button_text_known, (DialogInterface.OnClickListener) null).create().show();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.MiuiSoundSettingsBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setFragmentTitle();
        Iterator<MiuiBaseController> it = this.mControllers.iterator();
        while (it.hasNext()) {
            it.next().resume();
        }
        IntentFilter intentFilter = new IntentFilter("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        getActivity().registerReceiver(this.mRingerModeReceiver, intentFilter);
        ContentResolver contentResolver = getContentResolver();
        contentResolver.registerContentObserver(Settings.System.getUriFor("vibrate_in_silent"), false, this.mVibrateSettingsObserver);
        contentResolver.registerContentObserver(Settings.System.getUriFor("vibrate_in_normal"), false, this.mVibrateSettingsObserver);
        Iterator<VolumeSeekBarPreference> it2 = this.mVolumePrefs.iterator();
        while (it2.hasNext()) {
            it2.next().getSeekBarVolumizer().resume();
        }
        sendRefreshMsg();
        refreshVipListUI();
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_adjust_volume", this.mUserId);
        boolean hasBaseUserRestriction = RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_adjust_volume", this.mUserId);
        Iterator<String> it3 = mRestrictedKeyList.iterator();
        while (it3.hasNext()) {
            Preference findPreference = findPreference(it3.next());
            if (findPreference != null) {
                findPreference.setEnabled(!hasBaseUserRestriction);
            }
            if ((findPreference instanceof RestrictedPreference) && !hasBaseUserRestriction) {
                ((RestrictedPreference) findPreference).setDisabledByAdmin(checkIfRestrictionEnforced);
            }
        }
        RestrictedPreference restrictedPreference = (RestrictedPreference) findPreference(MiuiSecurityAndPrivacySettingsTree.CELL_BROADCAST_SETTINGS);
        if (restrictedPreference != null) {
            restrictedPreference.checkRestrictionAndSetDisabled("no_config_cell_broadcasts");
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    void refreshVolumeAndVibrate() {
        Log.i("MiuiSoundSettings", "refreshVolumeAndVibrate");
        Context context = getContext();
        if (context == null) {
            return;
        }
        CheckBoxPreference checkBoxPreference = this.mSilentModePref;
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(MiuiSettings.SoundMode.isSilenceModeOn(this.mContext));
        }
        if (this.mVibrateWhenSilentPref != null) {
            this.mVibrateWhenSilentPref.setChecked(Settings.System.getIntForUser(context.getContentResolver(), "vibrate_in_silent", 1, -3) == 1);
        }
        if (this.mVibrateWhenRingingPref != null) {
            this.mVibrateWhenRingingPref.setChecked(Settings.System.getIntForUser(context.getContentResolver(), "vibrate_in_normal", MiuiSettings.System.VIBRATE_IN_NORMAL_DEFAULT ? 1 : 0, -3) == 1);
        }
        refreshMuteModeSetting();
        refreshZenModeSetting();
        refreshZenRuleSettings();
    }

    @Override // com.android.settings.MiuiSoundSettingsBase
    protected void ringtoneLookupOthers() {
        if (getActivity() == null) {
            return;
        }
        MiuiDefaultRingtonePreference miuiDefaultRingtonePreference = this.mSmsReceivedSoundPreference;
        if (miuiDefaultRingtonePreference != null) {
            updateRingtoneName(miuiDefaultRingtonePreference.getUri(), 3);
        }
        MiuiDefaultRingtonePreference miuiDefaultRingtonePreference2 = this.mSmsDeliveredSoundPreference;
        if (miuiDefaultRingtonePreference2 != null) {
            updateRingtoneName(miuiDefaultRingtonePreference2.getUri(), 4);
        }
        MiuiDefaultRingtonePreference miuiDefaultRingtonePreference3 = this.mCalendarSoundPreference;
        if (miuiDefaultRingtonePreference3 != null) {
            updateRingtoneName(miuiDefaultRingtonePreference3.getUri(), 6);
        }
        MiuiDefaultRingtonePreference miuiDefaultRingtonePreference4 = this.mAlarmRingtonePreference;
        if (miuiDefaultRingtonePreference4 != null) {
            updateRingtoneName(miuiDefaultRingtonePreference4.getUri(), 5);
        }
    }

    @Override // com.android.settings.MiuiSoundSettingsBase
    protected void updateOthers() {
        if (getActivity() == null) {
            return;
        }
        if (isRingtoneViewEnable(3)) {
            updateRingtoneName(this.mRingtoneCardPreference.getUri(3), 7);
        }
        if (isRingtoneViewEnable(4)) {
            updateRingtoneName(this.mRingtoneCardPreference.getUri(4), 6);
        }
    }

    public void updateSoundDesc(String str) {
        JSONArray basicItemsArray = ParseMiShopDataUtils.getBasicItemsArray(str);
        boolean z = false;
        z = false;
        String str2 = null;
        if (basicItemsArray != null && basicItemsArray.length() > 0) {
            String str3 = null;
            boolean z2 = false;
            for (int i = 0; i < basicItemsArray.length(); i++) {
                JSONObject jSONObject = JSONUtils.getJSONObject(basicItemsArray, i);
                int itemIndex = ParseMiShopDataUtils.getItemIndex(jSONObject);
                if (itemIndex == 0) {
                    str3 = ParseMiShopDataUtils.getItemSummary(jSONObject);
                } else if (itemIndex == 1) {
                    z2 = ParseMiShopDataUtils.getItemBooleanSummary(jSONObject);
                }
            }
            z = z2;
            str2 = str3;
        }
        PreferenceCategory preferenceCategory = this.mSoundSpeakerCategory;
        if (preferenceCategory != null) {
            preferenceCategory.setVisible(!TextUtils.isEmpty(str2));
            this.mSoundSpeakerPreference.setVisible(true ^ TextUtils.isEmpty(str2));
            this.mSoundSpeakerPreference.setHarman(z);
            this.mSoundSpeakerPreference.setSummary(str2);
        }
    }

    @Override // com.android.settings.MiuiSoundSettingsBase
    protected void updateValue(Message message) {
        int i = message.what;
        if (i == 6) {
            setRingtoneValue(4, (CharSequence) message.obj);
        } else if (i != 7) {
        } else {
            setRingtoneValue(3, (CharSequence) message.obj);
        }
    }
}
