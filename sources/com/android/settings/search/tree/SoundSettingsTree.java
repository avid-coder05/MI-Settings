package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.UserManager;
import android.os.Vibrator;
import com.android.settings.MiuiSoundSettings;
import com.android.settings.MiuiSoundSettingsActivity;
import com.android.settings.MiuiSoundSettingsBase;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.search.FunctionColumns;
import com.android.settings.sound.DtmfToneController;
import com.android.settings.sound.MiuiStereoModeController;
import com.android.settings.sound.MiuiWorkSoundPreferenceController;
import com.android.settings.sound.coolsound.CoolSoundUtils;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class SoundSettingsTree extends SettingsTree {
    private static final String KEY_DOCK_AUDIO_SETTINGS = "dock_audio";
    public static final String REPEAT_CALL = "repeat_call";
    public static final String SOUND_SETTINGS_TAB_SOUND = "sound_settings_tab_sound";
    public static final String SOUND_VIBRATE_SETTINGS = "sound_vibrate_settings";
    public static final String VIP_LIST = "vip_list";

    protected SoundSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    private Intent buildMiuiSoundSettingsActivityIntent(int i) {
        Intent intent = new Intent(((SettingsTree) this).mContext, MiuiSoundSettingsActivity.class);
        intent.putExtra("extra_tab_position", i);
        return intent;
    }

    public Intent getIntent() {
        Intent intent = super.getIntent();
        if (SettingsFeatures.isSupportSettingsHaptic(((SettingsTree) this).mContext)) {
            String columnValue = getColumnValue(FunctionColumns.FRAGMENT);
            columnValue.hashCode();
            return !columnValue.equals("com.android.settings.haptic.HapticFragment") ? !columnValue.equals("com.android.settings.MiuiSoundSettings") ? intent : buildMiuiSoundSettingsActivityIntent(0) : buildMiuiSoundSettingsActivityIntent(1);
        } else if (SOUND_VIBRATE_SETTINGS.equals(getColumnValue("resource"))) {
            Intent intent2 = new Intent();
            intent2.setAction("android.intent.action.MAIN");
            intent2.setClassName("com.android.settings", "com.android.settings.SubSettings");
            intent2.putExtra(":settings:show_fragment", "com.android.settings.MiuiSoundSettings");
            intent2.putExtra(":android:no_headers", true);
            return intent2;
        } else {
            return intent;
        }
    }

    protected String getPath(boolean z, boolean z2) {
        String path = super.getPath(z, z2);
        String str = "/" + SearchUtils.getString(getPackageContext(((SettingsTree) this).mContext), SOUND_VIBRATE_SETTINGS);
        return (SettingsFeatures.isSupportSettingsHaptic(((SettingsTree) this).mContext) || !path.contains(str)) ? path : path.replace(str, "");
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        boolean isSupportCoolSound = CoolSoundUtils.isSupportCoolSound(((SettingsTree) this).mContext);
        if (SOUND_VIBRATE_SETTINGS.equals(columnValue)) {
            if (!((Vibrator) ((SettingsTree) this).mContext.getSystemService("vibrator")).hasVibrator()) {
                setColumnValue("resource", "sound_settings");
            }
        } else if ("sound_settings".equals(columnValue)) {
            if (((Vibrator) ((SettingsTree) this).mContext.getSystemService("vibrator")).hasVibrator()) {
                setColumnValue("resource", SOUND_VIBRATE_SETTINGS);
            }
        } else if ("ringtone_title".equals(columnValue)) {
            if (MiuiSoundSettings.hideRingtonePreference(((SettingsTree) this).mContext) || isSupportCoolSound) {
                return 0;
            }
        } else if ("dtmf_tone_enable_title".equals(columnValue)) {
            if (DtmfToneController.hideDtmfTonePreference(((SettingsTree) this).mContext)) {
                return 0;
            }
        } else if ("sms_received_sound_title".equals(columnValue)) {
            if (!Utils.isVoiceCapable(((SettingsTree) this).mContext) || SettingsFeatures.isNeedRemoveSmsReceivedSound(((SettingsTree) this).mContext) || isSupportCoolSound) {
                return 0;
            }
        } else if ("calendar_sound_title".equals(columnValue) || "notification_sound_title".equals(columnValue)) {
            if (isSupportCoolSound) {
                return 0;
            }
        } else if ("vibrate_when_ringing_title".equals(columnValue) || "vibrate_in_silent_title".equals(columnValue)) {
            if (!((Vibrator) ((SettingsTree) this).mContext.getSystemService("vibrator")).hasVibrator()) {
                return 0;
            }
        } else if ("haptic_feedback_enable_title".equals(columnValue)) {
            if (!((Vibrator) ((SettingsTree) this).mContext.getSystemService("vibrator")).hasVibrator() || SettingsFeatures.isSystemHapticNeeded()) {
                return 0;
            }
        } else if ("sound_stereo_mode_title".equals(columnValue)) {
            if (!MiuiStereoModeController.IS_SUPPORT_STEREO) {
                return 0;
            }
        } else if ("incall_show".equals(columnValue) && !SettingsFeatures.isIncallShowNeeded(((SettingsTree) this).mContext)) {
            return 0;
        } else {
            if ("work_ringtone_title".equals(columnValue)) {
                if (!Utils.isVoiceCapable(((SettingsTree) this).mContext)) {
                    return 0;
                }
            } else if ("headset_settings_title".equals(columnValue) || "sound_assist_title".equals(columnValue)) {
                if (!SettingsFeatures.isMisoundShowNeeded(((SettingsTree) this).mContext)) {
                    return 0;
                }
            } else if ("ring_to_you".equals(columnValue) && (!SettingsFeatures.IS_NEED_ADD_RINGTOYOU || !Boolean.parseBoolean(getColumnValue(FunctionColumns.IS_CHECKBOX)))) {
                return 0;
            } else {
                if ("time_zen_mode_turn_on".equals(columnValue) || "time_zen_mode_turn_off".equals(columnValue) || "repeat_days_in_week".equals(columnValue)) {
                    if (!SettingsFeatures.isZenModeRuleOn(((SettingsTree) this).mContext)) {
                        return 0;
                    }
                } else if ("haptic_feedback_progress".equals(columnValue) && !MiuiSoundSettings.isSystemHapticEnable(((SettingsTree) this).mContext)) {
                    return 0;
                }
            }
        }
        String columnValue2 = getColumnValue("category_origin");
        if ("sound_work_settings".equals(columnValue2)) {
            if (Utils.getManagedProfile(UserManager.get(((SettingsTree) this).mContext)) == null || !MiuiWorkSoundPreferenceController.isSearchAvailable(((SettingsTree) this).mContext)) {
                return 0;
            }
        } else if ("haptic_feedback_title".equals(columnValue2) && SettingsFeatures.isSupportSettingsHaptic(((SettingsTree) this).mContext)) {
            return 0;
        }
        return super.getStatus();
    }

    protected String getTitle(boolean z) {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        if (columnValue.equals("system_haptic_feedback")) {
            if (SettingsFeatures.FEATURE_HAPTIC_INFINITE_LEVEL) {
                return ((SettingsTree) this).mContext.getResources().getString(R.string.open_haptic_feedback);
            }
        } else if (columnValue.equals(SOUND_SETTINGS_TAB_SOUND) && !SettingsFeatures.isSupportSettingsHaptic(((SettingsTree) this).mContext)) {
            return ((SettingsTree) this).mContext.getResources().getString(R.string.sound_vibrate_settings);
        }
        return super.getTitle(z);
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        if ("bluetooth_dock_settings_a2dp".equals(columnValue) || "dock_sounds_enable_title".equals(columnValue)) {
            if (!((SettingsTree) this).mContext.getResources().getBoolean(R.bool.has_dock_settings)) {
                return true;
            }
        } else if ("backtouch_enable_title".equals(columnValue)) {
            if (!"centaur".equals(Build.DEVICE)) {
                return true;
            }
        } else if ("vibrate_when_ringing_title".equals(columnValue)) {
            if (miui.os.Build.IS_TABLET) {
                return true;
            }
        } else if ("system_haptic_feedback".equals(columnValue)) {
            if (!SettingsFeatures.isSystemHapticNeeded()) {
                return true;
            }
        } else if ("haptic_feedback_progress".equals(columnValue)) {
            if (!SettingsFeatures.isSystemHapticNeeded() || !SettingsFeatures.FEATURE_HAPTIC_INFINITE_LEVEL) {
                return true;
            }
        } else if ("sound_settings_tab_haptic".equals(columnValue)) {
            if (!SettingsFeatures.isSupportSettingsHaptic(((SettingsTree) this).mContext)) {
                return true;
            }
        } else if (SOUND_VIBRATE_SETTINGS.equals(columnValue)) {
            if (SettingsFeatures.isSupportSettingsHaptic(((SettingsTree) this).mContext)) {
                setColumnValue("resource", "sound_haptic_settings");
            }
        } else if (SOUND_SETTINGS_TAB_SOUND.equals(columnValue)) {
            if (!SettingsFeatures.isSupportSettingsHaptic(((SettingsTree) this).mContext)) {
                setColumnValue("resource", SOUND_VIBRATE_SETTINGS);
            }
        } else if ("vip_list".equals(columnValue) || "repeat_call".equals(columnValue)) {
            if (miui.os.Build.IS_TABLET) {
                return true;
            }
        } else if ("voice_assist_volume_option_title".equals(columnValue) && MiuiUtils.excludeXiaoAi(((SettingsTree) this).mContext)) {
            return true;
        } else {
            if (KEY_DOCK_AUDIO_SETTINGS.equals(columnValue) && !MiuiSoundSettingsBase.needsDockSettings(((SettingsTree) this).mContext)) {
                return true;
            }
            if ("dock_audio_settings_title".equals(columnValue) && !MiuiSoundSettingsBase.needsDockSettings(((SettingsTree) this).mContext)) {
                return true;
            }
        }
        return super.initialize();
    }
}
