package com.android.settings;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ExtraRingtone;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.sound.HapticSeekBarPreference;
import com.android.settings.sound.RingtoneCardPreference;
import com.android.settings.sound.coolsound.CoolSoundUtils;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.lang.ref.WeakReference;
import miui.app.constants.ThemeManagerConstants;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiSoundSettingsBase extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String[] NEED_VOICE_CAPABILITY = {ThemeManagerConstants.COMPONENT_CODE_RINGTONE, "category_calls_and_notification", "vibrate_when_ringing", "sms_received_sound", "sms_delivered_sound"};
    private AudioManager mAudioManager;
    private CheckBoxPreference mDockAudioMediaEnabled;
    private Preference mDockAudioSettings;
    private Intent mDockIntent;
    private CheckBoxPreference mDockSounds;
    protected CheckBoxPreference mHapticFeedback;
    protected PreferenceCategory mHapticFeedbackCategory;
    protected DropDownPreference mHapticFeedbackLevel;
    protected HapticSeekBarPreference mHapticFeedbackSeekbar;
    private Preference mMusicFx;
    private DefaultRingtonePreference mNotificationPreference;
    protected RingtoneCardPreference mRingtoneCardPreference;
    private Runnable mRingtoneLookupRunnable;
    private DefaultRingtonePreference mRingtonePreference;
    private View mRootView;
    protected boolean mSupportCoolSound;
    protected CheckBoxPreference mSystemHapticPreference;
    private CheckBoxPreference mVibrateWhenRinging;
    private SoundBaseUIHandler mHandler = new SoundBaseUIHandler(this);
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.MiuiSoundSettingsBase.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.DOCK_EVENT")) {
                MiuiSoundSettingsBase.this.handleDockChange(intent);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SoundBaseUIHandler extends Handler {
        private final WeakReference<MiuiSoundSettingsBase> mSoundSettingsBaseRef;

        SoundBaseUIHandler(MiuiSoundSettingsBase miuiSoundSettingsBase) {
            super(Looper.getMainLooper());
            this.mSoundSettingsBaseRef = new WeakReference<>(miuiSoundSettingsBase);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            MiuiSoundSettingsBase miuiSoundSettingsBase = this.mSoundSettingsBaseRef.get();
            if (miuiSoundSettingsBase == null) {
                return;
            }
            if (miuiSoundSettingsBase.mSupportCoolSound) {
                int i = message.what;
                if (i == 1) {
                    miuiSoundSettingsBase.setRingtoneValue(0, (CharSequence) message.obj);
                    return;
                } else if (i != 2) {
                    miuiSoundSettingsBase.updateValue(message);
                    return;
                } else {
                    miuiSoundSettingsBase.setRingtoneValue(5, (CharSequence) message.obj);
                    return;
                }
            }
            int i2 = message.what;
            if (i2 == 1) {
                if (miuiSoundSettingsBase.mRingtonePreference != null) {
                    miuiSoundSettingsBase.mRingtonePreference.setSummary((CharSequence) message.obj);
                }
            } else if (i2 != 2) {
                miuiSoundSettingsBase.handleOthersSummery(message);
            } else if (miuiSoundSettingsBase.mNotificationPreference != null) {
                miuiSoundSettingsBase.mNotificationPreference.setSummary((CharSequence) message.obj);
            }
        }
    }

    private Dialog createUndockedMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dock_not_found_title);
        builder.setMessage(R.string.dock_not_found_text);
        builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    private String getMashupSound(Context context) {
        String string = Settings.System.getString(context.getContentResolver(), "notification_sound");
        return string != null ? (string.toLowerCase().contains("mashup_sound") || string.toLowerCase().contains("animals")) ? MiuiUtils.getMashupSoundSummary(context, string) : "" : "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDockChange(Intent intent) {
        if (this.mDockAudioSettings != null) {
            int intExtra = intent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
            boolean z = intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE") != null;
            this.mDockIntent = intent;
            if (intExtra == 0) {
                this.mDockAudioSettings.setEnabled(false);
                return;
            }
            try {
                removeDialog(1);
            } catch (IllegalArgumentException unused) {
            }
            if (z) {
                this.mDockAudioSettings.setEnabled(true);
            } else if (intExtra != 3) {
                this.mDockAudioSettings.setEnabled(false);
            } else {
                ContentResolver contentResolver = getContentResolver();
                this.mDockAudioSettings.setEnabled(true);
                if (Settings.Global.getInt(contentResolver, "dock_audio_media_enabled", -1) == -1) {
                    Settings.Global.putInt(contentResolver, "dock_audio_media_enabled", 0);
                }
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("dock_audio_media_enabled");
                this.mDockAudioMediaEnabled = checkBoxPreference;
                checkBoxPreference.setOnPreferenceChangeListener(this);
                this.mDockAudioMediaEnabled.setPersistent(false);
                this.mDockAudioMediaEnabled.setChecked(Settings.Global.getInt(contentResolver, "dock_audio_media_enabled", 0) != 0);
            }
        }
    }

    private void initDockSettings() {
        ContentResolver contentResolver = getContentResolver();
        if (!needsDockSettings()) {
            getPreferenceScreen().removePreference(findPreference("dock_category"));
            getPreferenceScreen().removePreference(findPreference("dock_audio"));
            getPreferenceScreen().removePreference(findPreference("dock_sounds"));
            Settings.Global.putInt(contentResolver, "dock_audio_media_enabled", 1);
            return;
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("dock_sounds");
        this.mDockSounds = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        this.mDockSounds.setPersistent(false);
        this.mDockSounds.setChecked(Settings.Global.getInt(contentResolver, "dock_sounds_enabled", 0) != 0);
        Preference findPreference = findPreference("dock_audio");
        this.mDockAudioSettings = findPreference;
        findPreference.setEnabled(false);
    }

    private void initSettingsHaptic() {
        if (SettingsFeatures.isSupportSettingsHaptic(requireContext())) {
            removeHapticFeedbackLevel();
            removeHapticSystemHapticPreference();
            removeHapticSeekbar();
        } else if (!SettingsFeatures.isSystemHapticNeeded()) {
            removeHapticSystemHapticPreference();
            removeHapticSeekbar();
        } else {
            removeHapticFeedbackLevel();
            if (!isSupportFeedbackSeekbar()) {
                removeHapticSeekbar();
                return;
            }
            this.mSystemHapticPreference.setTitle(R.string.open_haptic_feedback);
            this.mSystemHapticPreference.setSummary(R.string.haptic_feedback_summary);
            this.mHapticFeedbackSeekbar.setIcon(R.drawable.ic_haptic_feedback);
            this.mHapticFeedbackSeekbar.setDependency("system_haptic_feedback");
        }
    }

    public static boolean isSupportFeedbackSeekbar() {
        return SettingsFeatures.FEATURE_HAPTIC_INFINITE_LEVEL;
    }

    private void lookupRingtoneNames() {
        new Thread(this.mRingtoneLookupRunnable).start();
    }

    private boolean needsDockSettings() {
        return needsDockSettings(getPrefContext());
    }

    public static boolean needsDockSettings(Context context) {
        if (context == null) {
            return false;
        }
        return context.getResources().getBoolean(R.bool.has_dock_settings);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void preLoadProcess() {
        Intent intent = new Intent("com.xiaomi.misettings.action_PRELOAD");
        intent.setPackage("com.xiaomi.misettings");
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        activity.startService(intent);
    }

    private void removeHapticFeedbackLevel() {
        if (this.mHapticFeedbackLevel != null) {
            getPreferenceScreen().removePreference(this.mHapticFeedbackLevel);
            this.mHapticFeedbackLevel = null;
        }
    }

    private void removeHapticSeekbar() {
        if (this.mHapticFeedbackSeekbar != null) {
            getPreferenceScreen().removePreference(this.mHapticFeedbackSeekbar);
            this.mHapticFeedbackSeekbar = null;
        }
        if (this.mHapticFeedbackCategory != null) {
            getPreferenceScreen().removePreference(this.mHapticFeedbackCategory);
            this.mHapticFeedbackCategory = null;
        }
    }

    private void removeHapticSystemHapticPreference() {
        if (this.mSystemHapticPreference != null) {
            getPreferenceScreen().removePreference(this.mSystemHapticPreference);
            this.mSystemHapticPreference = null;
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_sound;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiSoundSettingsBase.class.getName();
    }

    protected void handleOthersSummery(Message message) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isRingtoneViewEnable(int i) {
        if (this.mRingtoneCardPreference != null) {
            return !r0.isViewDisable(i);
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ContentResolver contentResolver = getContentResolver();
        addPreferencesFromResource(R.xml.sound_settings);
        this.mAudioManager = (AudioManager) getSystemService("audio");
        this.mSupportCoolSound = CoolSoundUtils.isSupportCoolSound(getContext());
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("vibrate_when_ringing");
        this.mVibrateWhenRinging = checkBoxPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setPersistent(false);
            this.mVibrateWhenRinging.setChecked(Settings.System.getInt(contentResolver, "vibrate_when_ringing", 0) != 0);
        }
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("haptic_feedback");
        this.mHapticFeedback = checkBoxPreference2;
        if (checkBoxPreference2 != null) {
            checkBoxPreference2.setPersistent(false);
            this.mHapticFeedback.setChecked(Settings.System.getInt(contentResolver, "haptic_feedback_enabled", 1) != 0);
            getPreferenceScreen().removePreference(this.mHapticFeedback);
            this.mHapticFeedback = null;
        }
        this.mRingtoneCardPreference = (RingtoneCardPreference) findPreference("ringtone_card");
        this.mRingtonePreference = (DefaultRingtonePreference) findPreference(ThemeManagerConstants.COMPONENT_CODE_RINGTONE);
        this.mNotificationPreference = (DefaultRingtonePreference) findPreference("notification_sound");
        if (this.mSupportCoolSound) {
            removePreference("category_calls_and_notification");
            removePreference(ThemeManagerConstants.COMPONENT_CODE_RINGTONE);
            removePreference("notification_sound");
            this.mRingtonePreference = null;
            this.mNotificationPreference = null;
        } else {
            removePreference("ringtone_card");
            this.mRingtoneCardPreference = null;
        }
        this.mHapticFeedbackLevel = (DropDownPreference) findPreference("haptic_feedback_level");
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("system_haptic_feedback");
        this.mSystemHapticPreference = checkBoxPreference3;
        checkBoxPreference3.setOnPreferenceChangeListener(this);
        this.mHapticFeedbackCategory = (PreferenceCategory) findPreference("haptic_feedback_category");
        this.mHapticFeedbackSeekbar = (HapticSeekBarPreference) findPreference("haptic_feedback_progress");
        Vibrator vibrator = (Vibrator) getSystemService("vibrator");
        if (vibrator == null || !vibrator.hasVibrator()) {
            if (this.mVibrateWhenRinging != null) {
                getPreferenceScreen().removePreference(this.mVibrateWhenRinging);
            }
            removeHapticFeedbackLevel();
        }
        initSettingsHaptic();
        if (!Utils.isVoiceCapable(requireContext())) {
            for (String str : NEED_VOICE_CAPABILITY) {
                Preference findPreference = findPreference(str);
                if (findPreference != null) {
                    getPreferenceScreen().removePreference(findPreference);
                }
            }
        }
        this.mRingtoneLookupRunnable = new Runnable() { // from class: com.android.settings.MiuiSoundSettingsBase.2
            @Override // java.lang.Runnable
            public void run() {
                MiuiSoundSettingsBase miuiSoundSettingsBase = MiuiSoundSettingsBase.this;
                if (miuiSoundSettingsBase.mSupportCoolSound) {
                    if (miuiSoundSettingsBase.isRingtoneViewEnable(0)) {
                        MiuiSoundSettingsBase miuiSoundSettingsBase2 = MiuiSoundSettingsBase.this;
                        miuiSoundSettingsBase2.updateRingtoneName(miuiSoundSettingsBase2.mRingtoneCardPreference.getUri(0), 1);
                    }
                    if (MiuiSoundSettingsBase.this.isRingtoneViewEnable(5)) {
                        MiuiSoundSettingsBase miuiSoundSettingsBase3 = MiuiSoundSettingsBase.this;
                        miuiSoundSettingsBase3.updateRingtoneName(miuiSoundSettingsBase3.mRingtoneCardPreference.getUri(5), 2);
                    }
                    MiuiSoundSettingsBase.this.updateOthers();
                    return;
                }
                if (miuiSoundSettingsBase.mRingtonePreference != null) {
                    MiuiSoundSettingsBase miuiSoundSettingsBase4 = MiuiSoundSettingsBase.this;
                    miuiSoundSettingsBase4.updateRingtoneName(miuiSoundSettingsBase4.mRingtonePreference.getUri(), 1);
                }
                if (MiuiSoundSettingsBase.this.mNotificationPreference != null) {
                    MiuiSoundSettingsBase miuiSoundSettingsBase5 = MiuiSoundSettingsBase.this;
                    miuiSoundSettingsBase5.updateRingtoneName(miuiSoundSettingsBase5.mNotificationPreference.getUri(), 2);
                }
                MiuiSoundSettingsBase.this.ringtoneLookupOthers();
            }
        };
        initDockSettings();
        this.mHandler.post(new Runnable() { // from class: com.android.settings.MiuiSoundSettingsBase$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MiuiSoundSettingsBase.this.preLoadProcess();
            }
        });
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1) {
            return createUndockedMessage();
        }
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mRootView = super.onCreateView(layoutInflater, viewGroup, bundle);
        getListView().setVerticalScrollBarEnabled(false);
        return this.mRootView;
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mRootView = null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mReceiver);
        CheckBoxPreference checkBoxPreference = this.mDockSounds;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(null);
        }
        CheckBoxPreference checkBoxPreference2 = this.mDockAudioMediaEnabled;
        if (checkBoxPreference2 != null) {
            checkBoxPreference2.setOnPreferenceChangeListener(null);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("emergency_tone".equals(preference.getKey())) {
            try {
                Settings.Global.putInt(getContentResolver(), "emergency_tone", Integer.parseInt((String) obj));
                return true;
            } catch (NumberFormatException e) {
                Log.e("SoundSettings", "could not persist emergency tone setting", e);
                return true;
            }
        } else if (preference == this.mDockSounds) {
            Settings.Global.putInt(getContentResolver(), "dock_sounds_enabled", ((Boolean) obj).booleanValue() ? 1 : 0);
            return true;
        } else if (preference == this.mDockAudioMediaEnabled) {
            Settings.Global.putInt(getContentResolver(), "dock_audio_media_enabled", ((Boolean) obj).booleanValue() ? 1 : 0);
            return true;
        } else {
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == this.mVibrateWhenRinging) {
            Settings.System.putInt(getContentResolver(), "vibrate_when_ringing", this.mVibrateWhenRinging.isChecked() ? 1 : 0);
        } else if (preference == this.mHapticFeedback) {
            Settings.System.putInt(getContentResolver(), "haptic_feedback_enabled", this.mHapticFeedback.isChecked() ? 1 : 0);
        } else {
            if (preference == this.mMusicFx) {
                return false;
            }
            if (preference == this.mDockAudioSettings) {
                Intent intent = this.mDockIntent;
                if ((intent != null ? intent.getIntExtra("android.intent.extra.DOCK_STATE", 0) : 0) == 0) {
                    showDialog(1);
                } else {
                    if ((this.mDockIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE") != null ? 1 : null) != null) {
                        getActivity().sendBroadcast(new Intent(this.mDockIntent));
                    } else {
                        PreferenceScreen preferenceScreen2 = (PreferenceScreen) this.mDockAudioSettings;
                        preferenceScreen2.getExtras().putBoolean("checked", Settings.Global.getInt(getContentResolver(), "dock_audio_media_enabled", 0) == 1);
                        super.onPreferenceTreeClick(preferenceScreen2, preferenceScreen2);
                    }
                }
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        CheckBoxPreference checkBoxPreference;
        super.onResume();
        lookupRingtoneNames();
        HapticSeekBarPreference hapticSeekBarPreference = this.mHapticFeedbackSeekbar;
        if (hapticSeekBarPreference != null && (checkBoxPreference = this.mSystemHapticPreference) != null) {
            hapticSeekBarPreference.setVisible(checkBoxPreference.isChecked());
        }
        getActivity().registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.DOCK_EVENT"));
    }

    protected void ringtoneLookupOthers() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setRingtoneValue(int i, CharSequence charSequence) {
        RingtoneCardPreference ringtoneCardPreference = this.mRingtoneCardPreference;
        if (ringtoneCardPreference != null) {
            ringtoneCardPreference.setValue(i, charSequence);
        }
    }

    protected void updateOthers() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateRingtoneName(Uri uri, int i) {
        String mashupSound;
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (!this.mSupportCoolSound || i != 7) {
            String ringtoneTitle = ExtraRingtone.getRingtoneTitle(activity, uri, true);
            if (i == 2 && (mashupSound = getMashupSound(activity)) != "") {
                ringtoneTitle = mashupSound;
            }
            SoundBaseUIHandler soundBaseUIHandler = this.mHandler;
            soundBaseUIHandler.sendMessage(soundBaseUIHandler.obtainMessage(i, ringtoneTitle));
            return;
        }
        try {
            String string = activity.getContentResolver().call(Uri.parse("content://com.android.deskclock"), "defaultAlarmAlert", (String) null, (Bundle) null).getString("defaultAlarmAlert");
            if (TextUtils.isEmpty(string)) {
                return;
            }
            SoundBaseUIHandler soundBaseUIHandler2 = this.mHandler;
            soundBaseUIHandler2.sendMessage(soundBaseUIHandler2.obtainMessage(7, string));
        } catch (Exception unused) {
            Log.e("SoundSettings", "Get alarm ring value error!");
        }
    }

    protected void updateValue(Message message) {
    }
}
