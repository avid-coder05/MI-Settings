package com.android.settings.sound;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.ExtraRingtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import com.android.settings.MiuiSoundSettings;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.notification.AudioHelper;
import com.android.settings.utils.MiuiBaseController;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class MiuiWorkSoundPreferenceController extends MiuiBaseController<PreferenceGroup> implements PreferenceControllerMixin {
    private AudioHelper mHelper;
    private int mManagedProfileId;
    private final BroadcastReceiver mManagedProfileReceiver;
    private final MiuiSoundSettings mParent;
    private UserManager mUserManager;
    private boolean mVoiceCapable;
    private MiuiWorkRingtonePreference mWorkAlarmRingtonePreference;
    private MiuiWorkRingtonePreference mWorkNotificationRingtonePreference;
    private MiuiWorkRingtonePreference mWorkPhoneRingtonePreference;
    private PreferenceGroup mWorkPreferenceCategory;
    private TwoStatePreference mWorkUsePersonalSounds;

    /* loaded from: classes2.dex */
    public static class UnifyWorkDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        public static void show(MiuiSoundSettings miuiSoundSettings) {
            FragmentManager fragmentManager = miuiSoundSettings.getFragmentManager();
            if (fragmentManager.findFragmentByTag("UnifyWorkDialogFragment") == null) {
                UnifyWorkDialogFragment unifyWorkDialogFragment = new UnifyWorkDialogFragment();
                unifyWorkDialogFragment.setTargetFragment(miuiSoundSettings, 200);
                unifyWorkDialogFragment.show(fragmentManager, "UnifyWorkDialogFragment");
            }
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 553;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            MiuiSoundSettings miuiSoundSettings = (MiuiSoundSettings) getTargetFragment();
            if (miuiSoundSettings.isAdded()) {
                miuiSoundSettings.enableWorkSync();
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getActivity()).setTitle(R.string.work_sync_dialog_title).setMessage(R.string.work_sync_dialog_message).setPositiveButton(R.string.work_sync_dialog_yes, this).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).create();
        }
    }

    public MiuiWorkSoundPreferenceController(PreferenceScreen preferenceScreen, MiuiSoundSettings miuiSoundSettings) {
        super(preferenceScreen);
        this.mManagedProfileReceiver = new BroadcastReceiver() { // from class: com.android.settings.sound.MiuiWorkSoundPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                int identifier = ((UserHandle) intent.getExtra("android.intent.extra.USER")).getIdentifier();
                String action = intent.getAction();
                action.hashCode();
                if (action.equals("android.intent.action.MANAGED_PROFILE_ADDED")) {
                    MiuiWorkSoundPreferenceController.this.onManagedProfileAdded(identifier);
                } else if (action.equals("android.intent.action.MANAGED_PROFILE_REMOVED")) {
                    MiuiWorkSoundPreferenceController.this.onManagedProfileRemoved(identifier);
                }
            }
        };
        this.mParent = miuiSoundSettings;
    }

    private void disableWorkSync() {
        RingtoneManager.disableSyncFromParent(getManagedProfileContext());
        disableWorkSyncSettings();
    }

    private void disableWorkSyncSettings() {
        MiuiWorkRingtonePreference miuiWorkRingtonePreference = this.mWorkPhoneRingtonePreference;
        if (miuiWorkRingtonePreference != null) {
            miuiWorkRingtonePreference.setEnabled(true);
        }
        this.mWorkNotificationRingtonePreference.setEnabled(true);
        this.mWorkAlarmRingtonePreference.setEnabled(true);
        updateWorkRingtoneSummaries();
    }

    private void enableWorkSyncSettings() {
        this.mWorkUsePersonalSounds.setChecked(true);
        MiuiWorkRingtonePreference miuiWorkRingtonePreference = this.mWorkPhoneRingtonePreference;
        if (miuiWorkRingtonePreference != null) {
            miuiWorkRingtonePreference.setSummary(R.string.work_sound_same_as_personal);
        }
        MiuiWorkRingtonePreference miuiWorkRingtonePreference2 = this.mWorkNotificationRingtonePreference;
        int i = R.string.work_sound_same_as_personal;
        miuiWorkRingtonePreference2.setSummary(i);
        this.mWorkAlarmRingtonePreference.setSummary(i);
    }

    private Context getManagedProfileContext() {
        int i = this.mManagedProfileId;
        if (i == -10000) {
            return null;
        }
        return this.mHelper.createPackageContextAsUser(i);
    }

    private static Context getManagedProfileContext(int i, AudioHelper audioHelper) {
        if (i == -10000) {
            return null;
        }
        return audioHelper.createPackageContextAsUser(i);
    }

    private MiuiWorkRingtonePreference initWorkPreference(PreferenceGroup preferenceGroup, String str) {
        MiuiWorkRingtonePreference miuiWorkRingtonePreference = (MiuiWorkRingtonePreference) preferenceGroup.findPreference(str);
        if (miuiWorkRingtonePreference != null) {
            miuiWorkRingtonePreference.setUserId(this.mManagedProfileId);
        }
        return miuiWorkRingtonePreference;
    }

    public static boolean isSearchAvailable(Context context) {
        UserManager userManager = UserManager.get(context);
        AudioHelper audioHelper = new AudioHelper(context);
        return (getManagedProfileContext(audioHelper.getManagedProfileId(userManager), audioHelper) == null || audioHelper.getManagedProfileId(userManager) == -10000 || !shouldShowRingtoneSettings(audioHelper)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateWorkPreferences$0(Preference preference, Object obj) {
        if (((Boolean) obj).booleanValue()) {
            UnifyWorkDialogFragment.show(this.mParent);
            return false;
        }
        disableWorkSync();
        return true;
    }

    private boolean shouldShowRingtoneSettings() {
        return !this.mHelper.isSingleVolume();
    }

    private static boolean shouldShowRingtoneSettings(AudioHelper audioHelper) {
        return !audioHelper.isSingleVolume();
    }

    private CharSequence updateRingtoneName(Context context, Uri uri) {
        return (context == null || !this.mHelper.isUserUnlocked(this.mUserManager, context.getUserId())) ? this.mContext.getString(R.string.managed_profile_not_available_label) : ExtraRingtone.getRingtoneTitle(context, uri, true);
    }

    private void updateWorkPreferences() {
        if (this.mWorkPreferenceCategory == null) {
            return;
        }
        boolean isAvailable = isAvailable();
        setVisible(this.mWorkPreferenceCategory, isAvailable);
        if (isAvailable) {
            if (this.mWorkUsePersonalSounds == null) {
                TwoStatePreference twoStatePreference = (TwoStatePreference) this.mWorkPreferenceCategory.findPreference("work_use_personal_sounds");
                this.mWorkUsePersonalSounds = twoStatePreference;
                twoStatePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.sound.MiuiWorkSoundPreferenceController$$ExternalSyntheticLambda0
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        boolean lambda$updateWorkPreferences$0;
                        lambda$updateWorkPreferences$0 = MiuiWorkSoundPreferenceController.this.lambda$updateWorkPreferences$0(preference, obj);
                        return lambda$updateWorkPreferences$0;
                    }
                });
            }
            if (this.mWorkPhoneRingtonePreference == null) {
                this.mWorkPhoneRingtonePreference = initWorkPreference(this.mWorkPreferenceCategory, "work_ringtone");
            }
            if (this.mWorkNotificationRingtonePreference == null) {
                this.mWorkNotificationRingtonePreference = initWorkPreference(this.mWorkPreferenceCategory, "work_notification_ringtone");
            }
            if (this.mWorkAlarmRingtonePreference == null) {
                this.mWorkAlarmRingtonePreference = initWorkPreference(this.mWorkPreferenceCategory, "work_alarm_ringtone");
            }
            if (!this.mVoiceCapable) {
                setVisible(this.mWorkPhoneRingtonePreference, false);
                this.mWorkPhoneRingtonePreference = null;
            }
            Context managedProfileContext = getManagedProfileContext();
            if (managedProfileContext != null) {
                if (Settings.Secure.getIntForUser(managedProfileContext.getContentResolver(), "sync_parent_sounds", 0, this.mManagedProfileId) == 1) {
                    enableWorkSyncSettings();
                } else {
                    disableWorkSyncSettings();
                }
            }
        }
    }

    private void updateWorkRingtoneSummaries() {
        Context managedProfileContext = getManagedProfileContext();
        MiuiWorkRingtonePreference miuiWorkRingtonePreference = this.mWorkPhoneRingtonePreference;
        if (miuiWorkRingtonePreference != null) {
            miuiWorkRingtonePreference.setSummary(updateRingtoneName(managedProfileContext, miuiWorkRingtonePreference.getUri()));
        }
        MiuiWorkRingtonePreference miuiWorkRingtonePreference2 = this.mWorkNotificationRingtonePreference;
        miuiWorkRingtonePreference2.setSummary(updateRingtoneName(managedProfileContext, miuiWorkRingtonePreference2.getUri()));
        MiuiWorkRingtonePreference miuiWorkRingtonePreference3 = this.mWorkAlarmRingtonePreference;
        miuiWorkRingtonePreference3.setSummary(updateRingtoneName(managedProfileContext, miuiWorkRingtonePreference3.getUri()));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceScreen.findPreference("sound_work_settings_section");
        this.mWorkPreferenceCategory = preferenceGroup;
        if (preferenceGroup != null) {
            setVisible(preferenceGroup, isAvailable());
        }
    }

    public void enableWorkSync() {
        RingtoneManager.enableSyncFromParent(getManagedProfileContext());
        enableWorkSyncSettings();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "sound_work_settings_section";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        return false;
    }

    @Override // com.android.settings.utils.MiuiBaseController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (getManagedProfileContext() == null || this.mHelper.getManagedProfileId(this.mUserManager) == -10000 || !shouldShowRingtoneSettings()) ? false : true;
    }

    @Override // com.android.settings.utils.MiuiBaseController
    protected void onAttach() {
        this.mUserManager = UserManager.get(this.mContext);
        this.mVoiceCapable = Utils.isVoiceCapable(this.mContext);
        this.mHelper = new AudioHelper(this.mContext);
    }

    public void onManagedProfileAdded(int i) {
        if (this.mManagedProfileId == -10000) {
            this.mManagedProfileId = i;
            updateWorkPreferences();
        }
    }

    public void onManagedProfileRemoved(int i) {
        if (this.mManagedProfileId == i) {
            this.mManagedProfileId = this.mHelper.getManagedProfileId(this.mUserManager);
            updateWorkPreferences();
        }
    }

    @Override // com.android.settings.utils.MiuiBaseController
    public void onPause() {
        this.mContext.unregisterReceiver(this.mManagedProfileReceiver);
    }

    @Override // com.android.settings.utils.MiuiBaseController
    public void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
        this.mContext.registerReceiver(this.mManagedProfileReceiver, intentFilter);
        this.mManagedProfileId = this.mHelper.getManagedProfileId(this.mUserManager);
        updateWorkPreferences();
    }
}
