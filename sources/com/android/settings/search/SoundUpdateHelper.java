package com.android.settings.search;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;
import android.provider.MiuiSettings;
import com.android.settings.R;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import miui.os.Build;

/* loaded from: classes2.dex */
class SoundUpdateHelper extends BaseSearchUpdateHelper {
    private static final String DOCK_SETTINGS_RESOURCE = "bluetooth_dock_settings_a2dp";
    private static final String DOCK_SOUNDS_ENABLE_RESOURCE = "dock_sounds_enable_title";
    private static final String DOLBY_RESOURCE = "music_title_dolby_control";
    private static final String HEADSET_CALIBRATE_RESOURCE = "music_headset_calibrate";
    private static final String MI_EFFECT_RESOURCE = "music_mi_effect_title";
    private static final String MUSIC_EQUALIZER_RESOURCE = "music_equalizer";
    private static final String MUSIC_HD_RESOURCE = "music_hd_title";
    private static final String MUSIC_HIFI_RESOURCE = "music_hifi_title";
    private static final String NEW_SILENT_RESOURCE = "silent_settings";
    private static final String OLD_SILENT_RESOURCE = "silent_mode_title";
    private static final String RINGTONE_RESOURCE = "ringtone_title";
    private static final String SOUND_RESOURCE = "sound_settings";
    private static final String SOUND_VIBRATE_RESOURCE = "sound_vibrate_settings";
    private static final String ZEN_MODE_RESOURCE = "do_not_disturb_mode";

    SoundUpdateHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void headsetPlug(Context context, ArrayList<ContentProviderOperation> arrayList, boolean z) {
        BaseSearchUpdateHelper.disableByResource(context, arrayList, HEADSET_CALIBRATE_RESOURCE, !z);
        updateEqualizer(context, arrayList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update(Context context, ArrayList<ContentProviderOperation> arrayList) {
        if (!((Vibrator) context.getSystemService("vibrator")).hasVibrator()) {
            Iterator<String> it = BaseSearchUpdateHelper.getIdWithResource(context, "sound_vibrate_settings").iterator();
            while (it.hasNext()) {
                BaseSearchUpdateHelper.updateItemData(context, arrayList, it.next(), "name", context.getResources().getString(R.string.sound_settings));
            }
            BaseSearchUpdateHelper.updatePath(context, arrayList, "sound_vibrate_settings", SOUND_RESOURCE);
        }
        if (Utils.isWifiOnly(context) || Build.IS_TABLET) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, RINGTONE_RESOURCE);
        }
        if (!context.getResources().getBoolean(R.bool.has_dock_settings)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, DOCK_SETTINGS_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, DOCK_SOUNDS_ENABLE_RESOURCE);
        }
        if (!MiuiSettings.SilenceMode.isSupported) {
            BaseSearchUpdateHelper.hideTreeByRootResource(context, arrayList, NEW_SILENT_RESOURCE);
            return;
        }
        BaseSearchUpdateHelper.hideByResource(context, arrayList, OLD_SILENT_RESOURCE);
        BaseSearchUpdateHelper.hideTreeByRootResource(context, arrayList, ZEN_MODE_RESOURCE);
    }

    private static void updateEqualizer(Context context, ArrayList<ContentProviderOperation> arrayList) {
        String parameters;
        String parameters2;
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        BaseSearchUpdateHelper.disableByResource(context, arrayList, MUSIC_EQUALIZER_RESOURCE, !audioManager.isWiredHeadsetOn() || !((parameters = audioManager.getParameters("hifi_mode")) == null || parameters.contains("false")) || (parameters2 = audioManager.getParameters("dirac")) == null || !parameters2.contains("=") || Integer.valueOf(parameters2.substring(parameters2.indexOf("="))).intValue() == 0);
    }
}
