package com.android.settings.emergency.ui;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.id.IdentifierManager;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.cloud.util.Utils;
import com.android.settings.emergency.service.LocationService;
import com.android.settings.emergency.ui.view.SosCustomPreference;
import com.android.settings.emergency.util.CommonUtils;
import com.android.settings.emergency.util.Config;
import com.android.settings.emergency.util.LicenseHelper;
import com.android.settings.report.InternationalCompat;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settings.security.PrivacyRevocationController;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.miui.privacypolicy.PrivacyManager;
import java.lang.ref.WeakReference;
import java.util.Locale;
import miui.os.Build;
import miui.provider.ExtraContacts;
import miuix.appcompat.app.AlertDialog;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SosSettings extends MiuiSettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private Context mContext;
    private CountDownTimer mCountdownTimer;
    private PreferenceCategory mGuardCategory;
    private Handler mHandler;
    private PreferenceCategory mPaCategory;
    private CheckBoxPreference mSosAroundPhotoPref;
    private CheckBoxPreference mSosAroundVoicePref;
    private CheckBoxPreference mSosCallLogPref;
    private CheckBoxPreference mSosCallingPref;
    private ValuePreference mSosContactsAddPref;
    private CheckBoxPreference mSosEnablePref;
    private Preference mSosGuardPref;
    private ValuePreference mSosPaPref;
    private SosCustomPreference mSosPlayerPref;
    private Preference mSosPrivacyPref;
    private ValuePreference mSosPrivacyRevoke;
    private PreferenceCategory mSosSettingsCategory;
    private MediaPlayer player;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PrivacyRevokeTask extends AsyncTask<Void, Void, Integer> {
        private WeakReference<SosSettings> mWeakContextReference;

        public PrivacyRevokeTask(SosSettings sosSettings) {
            this.mWeakContextReference = new WeakReference<>(sosSettings);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Integer doInBackground(Void... voidArr) {
            SosSettings sosSettings = this.mWeakContextReference.get();
            if (sosSettings == null || isCancelled()) {
                return null;
            }
            return Integer.valueOf(PrivacyManager.privacyRevoke(sosSettings.mContext, "SOS", IdentifierManager.getOAID(sosSettings.mContext)));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Integer num) {
            SosSettings sosSettings = this.mWeakContextReference.get();
            if (sosSettings == null) {
                return;
            }
            if (num.intValue() != 1) {
                sosSettings.showRevokeFailedDialog(sosSettings.mContext);
                return;
            }
            Config.setSosEnable(sosSettings.mContext, false);
            sosSettings.mSosEnablePref.setChecked(false);
            Config.setSosPrivacyConfirmed(sosSettings.mContext, false);
            sosSettings.mContext.stopService(new Intent(sosSettings.mContext, LocationService.class));
            Config.setSosEmergencyContacts(sosSettings.mContext, null);
            sosSettings.getActivity().finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void agreeProlicy(final Context context) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        new Thread(new Runnable() { // from class: com.android.settings.emergency.ui.SosSettings.18
            @Override // java.lang.Runnable
            public void run() {
                Context context2 = context;
                Log.w("SosSettings", "cta dialog agree = " + PrivacyManager.privacyAgree(context2, "SOS", IdentifierManager.getOAID(context2)));
            }
        }).start();
    }

    private void checkPrivacyUpdate(final Context context) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        new Thread(new Runnable() { // from class: com.android.settings.emergency.ui.SosSettings.19
            @Override // java.lang.Runnable
            public void run() {
                Context context2 = context;
                String requestPrivacyUpdate = PrivacyManager.requestPrivacyUpdate(context2, "SOS", IdentifierManager.getOAID(context2));
                Log.d("SosSettings", "run: checkPrivacyUpdate result  = " + requestPrivacyUpdate);
                if (TextUtils.isEmpty(requestPrivacyUpdate)) {
                    return;
                }
                try {
                    JSONObject optJSONObject = new JSONObject(requestPrivacyUpdate).optJSONObject("translation");
                    if (optJSONObject != null) {
                        final String optString = optJSONObject.optString(Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry());
                        if (TextUtils.isEmpty(optString)) {
                            return;
                        }
                        SosSettings.this.mHandler.post(new Runnable() { // from class: com.android.settings.emergency.ui.SosSettings.19.1
                            @Override // java.lang.Runnable
                            public void run() {
                                Log.d("SosSettings", "run: updateInfo = " + optString);
                                SosSettings.this.showPrivacyUpdateDialog(optString);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("SosSettings", "checkPrivacyUpdate error", e);
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableSoS() {
        Config.setSosPrivacyConfirmed(this.mContext, true);
        if (TextUtils.isEmpty(Config.getSosEmergencyContacts(this.mContext))) {
            showEmergencyContactsGuidingDialog();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRevoke() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        new PrivacyRevokeTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void openMediaPlayer() {
        try {
            if (this.player == null) {
                MediaPlayer create = MediaPlayer.create(this.mContext, R.raw.sos_player_voice);
                this.player = create;
                create.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.settings.emergency.ui.SosSettings.1
                    @Override // android.media.MediaPlayer.OnCompletionListener
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        SosSettings.this.mSosPlayerPref.setPlayIcon(true);
                    }
                });
            }
            if (this.player.isPlaying()) {
                this.mSosPlayerPref.setPlayIcon(true);
                this.player.pause();
                return;
            }
            this.mSosPlayerPref.setPlayIcon(false);
            this.player.start();
        } catch (Exception e) {
            Log.e("SosSettings", "Media Player Exception!", e);
        }
    }

    private void showCallLogEnableDialog() {
        new AlertDialog.Builder(this.mContext).setTitle(R.string.emergency_sos_title).setMessage(R.string.sos_phone_log_privacy_dialog_message).setCancelable(false).setPositiveButton(R.string.sos_phone_call_privacy_dialog_message_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Config.setSosCallLogConfirmed(SosSettings.this.mContext, true);
                Config.setSosCallLogEnable(SosSettings.this.mContext, true);
                SosSettings.this.mSosCallLogPref.setChecked(true);
            }
        }).setNegativeButton(R.string.miui_sos_exit_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SosSettings.this.mSosCallLogPref.setChecked(false);
            }
        }).show();
    }

    private void showCallingEnableDialog() {
        new AlertDialog.Builder(this.mContext).setTitle(R.string.emergency_sos_title).setMessage(R.string.sos_phone_call_privacy_dialog_message).setCancelable(false).setPositiveButton(R.string.sos_phone_call_privacy_dialog_message_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Config.setSosCallingConfirmed(SosSettings.this.mContext, true);
                Config.setSosCallingEnable(SosSettings.this.mContext, true);
                SosSettings.this.mSosCallingPref.setChecked(true);
            }
        }).setNegativeButton(R.string.miui_sos_exit_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SosSettings.this.mSosCallingPref.setChecked(false);
            }
        }).show();
    }

    private void showEmergencyContactsGuidingDialog() {
        new AlertDialog.Builder(this.mContext).setTitle(R.string.miui_sos_remind_title).setMessage(R.string.miui_sos_remind_open).setCancelable(false).setPositiveButton(R.string.miui_sos_remind_add, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SosSettings.this.mContext, EmergencyContactsActivity.class);
                intent.putExtra("first_open", true);
                SosSettings.this.startActivity(intent);
            }
        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SosSettings.this.mSosEnablePref.setChecked(false);
            }
        }).show();
    }

    private void showNoInterntDialog() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        new AlertDialog.Builder(this.mContext).setTitle(R.string.sos_privacy_policy_no_net_title).setMessage(R.string.sos_privacy_policy_no_net_message).setCancelable(false).setPositiveButton(R.string.miui_sos_launch_error_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.12
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPrivacyNotAgreeDialog() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle(R.string.sos_privacy_policy_change_title_reject).setMessage(LicenseHelper.buildPrivacyPolicyNoticeDisagree(this.mContext)).setCancelable(false).setPositiveButton(R.string.free_wifi_user_agreement_allow, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.23
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SosSettings sosSettings = SosSettings.this;
                sosSettings.agreeProlicy(sosSettings.mContext);
            }
        }).setNegativeButton(R.string.miui_sos_exit_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.22
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SosSettings.this.finish();
            }
        }).create();
        create.show();
        create.getMessageView().setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showPrivacyPolicyDialog(final boolean z) {
        AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle(Build.IS_INTERNATIONAL_BUILD ? R.string.sos_privacy_dialog_title : R.string.emergency_sos_title).setMessage(LicenseHelper.buildPrivacyPolicyNotice(this.mContext)).setCancelable(false).setPositiveButton(R.string.free_wifi_user_agreement_allow, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (z) {
                    SosSettings.this.enableSoS();
                    if (Build.IS_INTERNATIONAL_BUILD) {
                        return;
                    }
                    SosSettings sosSettings = SosSettings.this;
                    sosSettings.agreeProlicy(sosSettings.mContext);
                } else if (SosSettings.this.isAdded()) {
                    Intent intent = new Intent("miui.intent.action.PRIVACY_AUTHORIZATION_DIALOG");
                    intent.putExtra(SettingsProvider.ARGS_KEY, "com.android.settings");
                    SosSettings.this.startActivityForResult(intent, 220);
                }
            }
        }).setNegativeButton(R.string.miui_sos_exit_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SosSettings.this.mSosEnablePref.setChecked(false);
            }
        }).create();
        create.show();
        create.getMessageView().setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showPrivacyRevockeDialog() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        AlertDialog.Builder title = new AlertDialog.Builder(this.mContext).setCancelable(false).setTitle(R.string.sos_privacy_policy_change_title_reject);
        title.setMessage(LicenseHelper.buildPolicyRevoke(this.mContext));
        title.setNegativeButton(R.string.privacy_authorize_revoke, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.13
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                SosSettings.this.handleRevoke();
            }
        });
        title.setPositiveButton(R.string.miui_sos_exit_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.14
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        title.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.emergency.ui.SosSettings.15
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (SosSettings.this.mCountdownTimer != null) {
                    SosSettings.this.mCountdownTimer.cancel();
                    SosSettings.this.mCountdownTimer = null;
                }
            }
        });
        AlertDialog create = title.create();
        create.show();
        create.getMessageView().setMovementMethod(LinkMovementMethod.getInstance());
        final Button button = create.getButton(-2);
        button.setEnabled(false);
        this.mCountdownTimer = new CountDownTimer(10000L, 1000L) { // from class: com.android.settings.emergency.ui.SosSettings.16
            @Override // android.os.CountDownTimer
            public void onFinish() {
                button.setEnabled(true);
                button.setText(SosSettings.this.getResources().getString(R.string.privacy_authorize_revoke));
            }

            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                button.setText(SosSettings.this.getResources().getString(R.string.privacy_authorize_revoke_time, Long.valueOf(j / 1000)));
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPrivacyUpdateDialog(String str) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        new AlertDialog.Builder(this.mContext).setTitle(R.string.sos_privacy_policy_change_title).setMessage(LicenseHelper.buildPrivacyPolicyNoticeUpdate(this.mContext, this.mContext.getResources().getString(R.string.sos_privacy_policy_change_subtitle) + "\n\n" + str + "\n\n")).setCancelable(false).setPositiveButton(R.string.free_wifi_user_agreement_allow, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.21
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SosSettings sosSettings = SosSettings.this;
                sosSettings.agreeProlicy(sosSettings.mContext);
            }
        }).setNegativeButton(R.string.miui_sos_exit_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.20
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SosSettings.this.showPrivacyNotAgreeDialog();
            }
        }).show().getMessageView().setMovementMethod(LinkMovementMethod.getInstance());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showRevokeFailedDialog(Context context) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.sos_privacy_revoke_failed_dialog_title).setMessage(R.string.sos_privacy_revoke_failed_dialog_content).setPositiveButton(R.string.sos_privacy_policy_no_net_button, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.17
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void showSosCloseDialog() {
        new AlertDialog.Builder(this.mContext).setTitle(R.string.miui_sos_exit_dialog_title).setMessage(R.string.miui_sos_remind_close).setCancelable(false).setPositiveButton(R.string.miui_sos_remind_close_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.11
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Config.setSosEnable(SosSettings.this.mContext, false);
                SosSettings.this.mSosEnablePref.setChecked(false);
                SosSettings.this.mContext.stopService(new Intent(SosSettings.this.mContext, LocationService.class));
            }
        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.emergency.ui.SosSettings.10
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SosSettings.this.mSosEnablePref.setChecked(true);
            }
        }).show();
    }

    private void showUserInstruction() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        if (Utils.isConnected(this.mContext)) {
            showPrivacyRevockeDialog();
        } else {
            showNoInterntDialog();
        }
    }

    private void updatePaPreference() {
        Resources resources;
        int i;
        if (Build.IS_INTERNATIONAL_BUILD || !Config.isPaSupport(this.mContext)) {
            this.mPaCategory.removePreference(this.mSosPaPref);
            getPreferenceScreen().removePreference(this.mPaCategory);
            return;
        }
        ValuePreference valuePreference = this.mSosPaPref;
        if (Config.isPaEnable(this.mContext)) {
            resources = getResources();
            i = R.string.miui_sos_pa_turn_on;
        } else {
            resources = getResources();
            i = R.string.miui_sos_pa_turn_off;
        }
        valuePreference.setValue(resources.getString(i));
    }

    private void updateUI() {
        this.mSosEnablePref.setChecked(Config.isSosEnable(this.mContext));
        String sosEmergencyContacts = Config.getSosEmergencyContacts(this.mContext);
        int length = TextUtils.isEmpty(sosEmergencyContacts) ? 0 : sosEmergencyContacts.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION).length;
        this.mSosContactsAddPref.setValue(getResources().getQuantityString(R.plurals.miui_sos_emergency_contacts_quantity, length, Integer.valueOf(length)));
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return SosSettings.class.getName();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 220 && i2 == -1) {
            enableSoS();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        addPreferencesFromResource(R.xml.miui_sos_settings);
        this.mHandler = new Handler();
        this.mContext = getContext();
        InternationalCompat.trackReportEvent("setting_Passwords_security_sos");
        this.mSosEnablePref = (CheckBoxPreference) findPreference("miui_sos_enable");
        this.mSosCallingPref = (CheckBoxPreference) findPreference("miui_sos_calling");
        this.mSosCallLogPref = (CheckBoxPreference) findPreference("miui_sos_call_log");
        this.mSosAroundPhotoPref = (CheckBoxPreference) findPreference("miui_sos_around_photo");
        this.mSosAroundVoicePref = (CheckBoxPreference) findPreference("miui_sos_around_voice");
        this.mSosPlayerPref = (SosCustomPreference) findPreference("key_sos_player");
        this.mSosContactsAddPref = (ValuePreference) findPreference("miui_sos_contacts_add");
        this.mSosPrivacyRevoke = (ValuePreference) findPreference("key_sos_privacy_revoke");
        this.mGuardCategory = (PreferenceCategory) findPreference("miui_sos_guard");
        this.mSosGuardPref = findPreference("key_sos_guard");
        this.mSosPrivacyPref = findPreference("key_sos_privacy");
        this.mSosPaPref = (ValuePreference) findPreference("key_sos_pa");
        this.mPaCategory = (PreferenceCategory) findPreference("miui_sos_pa");
        this.mSosSettingsCategory = (PreferenceCategory) findPreference("miui_sos_settings");
        this.mSosCallingPref.setChecked(Config.isSosCallingEnable(this.mContext));
        this.mSosCallLogPref.setChecked(Config.isSosCallLogEnable(this.mContext));
        this.mSosAroundPhotoPref.setChecked(Config.isSosEmergencyAroundPhoto(this.mContext));
        this.mSosAroundPhotoPref.setSummary(this.mContext.getString(R.string.miui_sos_around_photo_summary, 1));
        this.mSosAroundVoicePref.setChecked(Config.isSosEmergencyAroundVoice(this.mContext));
        this.mSosAroundVoicePref.setSummary(this.mContext.getString(R.string.miui_sos_around_voice_summary, 5));
        this.mSosAroundVoicePref.setTitle(this.mContext.getString(R.string.miui_sos_around_voice_title, 5));
        this.mSosEnablePref.setOnPreferenceChangeListener(this);
        this.mSosCallingPref.setOnPreferenceChangeListener(this);
        this.mSosCallLogPref.setOnPreferenceChangeListener(this);
        this.mSosAroundPhotoPref.setOnPreferenceChangeListener(this);
        this.mSosAroundVoicePref.setOnPreferenceChangeListener(this);
        this.mSosPlayerPref.setOnPreferenceClickListener(this);
        this.mSosContactsAddPref.setOnPreferenceClickListener(this);
        this.mSosPrivacyRevoke.setOnPreferenceClickListener(this);
        this.mSosGuardPref.setOnPreferenceClickListener(this);
        this.mSosPrivacyPref.setOnPreferenceClickListener(this);
        this.mSosPaPref.setOnPreferenceClickListener(this);
        this.mSosContactsAddPref.setShowRightArrow(true);
        this.mSosPrivacyRevoke.setShowRightArrow(true);
        this.mSosPaPref.setShowRightArrow(true);
        if (!SystemProperties.getBoolean("ro.vendor.audio.sos", false) || Build.IS_INTERNATIONAL_BUILD) {
            this.mSosSettingsCategory.removePreference(this.mSosCallingPref);
            this.mSosSettingsCategory.removePreference(this.mSosPlayerPref);
            if (Build.IS_INTERNATIONAL_BUILD) {
                this.mGuardCategory.removePreference(this.mSosGuardPref);
                this.mGuardCategory.removePreference(this.mSosPrivacyRevoke);
            }
        }
        if (!CommonUtils.isSosNewFeatureSupport(this.mContext)) {
            this.mSosSettingsCategory.removePreference(this.mSosAroundVoicePref);
            this.mSosSettingsCategory.removePreference(this.mSosAroundPhotoPref);
        }
        if (Build.VERSION.SDK_INT < 26) {
            this.mSosSettingsCategory.removePreference(this.mSosAroundPhotoPref);
        }
        if (!Locale.getDefault().getLanguage().equals("zh") || !Locale.getDefault().getCountry().equals("CN")) {
            this.mGuardCategory.removePreference(this.mSosGuardPref);
        }
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD && Config.isSosEnable(this.mContext)) {
            checkPrivacyUpdate(this.mContext);
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            return;
        }
        this.mSosPlayerPref.setPlayIcon(true);
        this.player.pause();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference == this.mSosEnablePref) {
            boolean isEnabled = !PrivacyRevocationController.hidePrivacyRevoke() ? MiuiSettings.Privacy.isEnabled(this.mContext, "com.android.settings") : true;
            if (booleanValue && (!Config.isSosPrivacyConfirmed(this.mContext) || !isEnabled)) {
                showPrivacyPolicyDialog(isEnabled);
                return true;
            }
            if (booleanValue && TextUtils.isEmpty(Config.getSosEmergencyContacts(this.mContext))) {
                showEmergencyContactsGuidingDialog();
            } else if (booleanValue || !Config.isInSosMode(this.mContext)) {
                Config.setSosEnable(this.mContext, booleanValue);
            } else {
                showSosCloseDialog();
            }
            return true;
        } else if (preference == this.mSosCallingPref) {
            if (miui.os.Build.IS_INTERNATIONAL_BUILD || !booleanValue || Config.isSosCallingConfirmed(this.mContext)) {
                Config.setSosCallingEnable(this.mContext, booleanValue);
            } else {
                showCallingEnableDialog();
            }
            return true;
        } else if (preference == this.mSosCallLogPref) {
            if (miui.os.Build.IS_INTERNATIONAL_BUILD || !booleanValue || Config.isSosCallLogConfirmed(this.mContext)) {
                Config.setSosCallLogEnable(this.mContext, booleanValue);
            } else {
                showCallLogEnableDialog();
            }
            return true;
        } else if (preference == this.mSosAroundPhotoPref) {
            Config.setSosEmergencyAroundPhoto(this.mContext, booleanValue);
            return true;
        } else if (preference == this.mSosAroundVoicePref) {
            Config.setSosEmergencyAroundVoice(this.mContext, booleanValue);
            return true;
        } else {
            return false;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mSosContactsAddPref) {
            startActivity(new Intent(this.mContext, EmergencyContactsActivity.class));
            return true;
        } else if (preference == this.mSosPrivacyRevoke) {
            showUserInstruction();
            return true;
        } else if (preference == this.mSosPlayerPref) {
            openMediaPlayer();
            return true;
        } else if (preference == this.mSosGuardPref) {
            startActivity(new Intent("miui.intent.action.green_guard_activity"));
            return true;
        } else if (preference != this.mSosPrivacyPref) {
            if (preference == this.mSosPaPref) {
                startActivity(new Intent("miui.intent.action.WARNINGCENTER_POLICE_ASSIST"));
                return true;
            }
            return true;
        } else {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(LicenseHelper.getSosPrivacyUrl()));
            intent.putExtra("com.android.browser.application_id", this.mContext.getPackageName());
            try {
                this.mContext.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                Log.e("SosSettings", "Actvity was not found for intent, " + intent.toString(), e);
                return true;
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateUI();
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.emergency_sos_title);
        }
        updatePaPreference();
    }
}
