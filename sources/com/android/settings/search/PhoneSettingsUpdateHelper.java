package com.android.settings.search;

import android.content.Context;

/* loaded from: classes2.dex */
public class PhoneSettingsUpdateHelper extends BaseSearchUpdateHelper {
    private static final String ANTISPAM_RESOURCE = "antispam_setting";
    private static final String AUTOIP_RESOURCE = "autoip";
    private static final String AUTO_REDIAL_RESOURCE = "auto_redial_pref_title";
    private static final String AUTO_RETRY_RESOURCE = "auto_retry_mode_title";
    private static final String CALLER_ID_RESOURCE = "caller_id_title";
    private static final String CALL_BACKGROUND_RESOURCE = "call_background_setting";
    private static final String CALL_RECORD_RESOURCE = "call_record_setting";
    private static final String CDMA_DISPLAY_PRECISE_CALL_RESOURCE = "cdma_display_precise_call_state_title";
    private static final String CONNECT_DISCONNECT_VIBRATE_RESOURCE = "connect_disconnect_vibrate_title";
    private static final String DATA_USAGE_RESOURCE = "preference_data_usage_title";
    private static final String DIAL_PAD_TOUCH_RESOURCE = "preference_dial_pad_touch_tone_title";
    private static final String DTMF_TONE_RESOURCE = "dtmf_tones_title";
    private static final String DUAL_4G_RESOURCE = "dual_4g_switch_title";
    private static final String ENABLE_PROXIMITY_RESOURCE = "enable_proximity_title";
    private static final String FLASH_WHEN_RING_RESOURCE = "flash_when_ring_title";
    private static final String HANDON_RINGER_RESOURCE = "handon_ringer_title";
    private static final String INTERNATIONAL_ROAMING_RESOURCE = "international_roaming_setting";
    private static final String MOBILE_NETWORK_RESOURCE = "sim_management_title";
    private static final String MOBILE_NETWORK_SINGLESIM_RESOURCE = "sim_management_title_singlesim";
    private static final String PHONE_PACKAGE = "com.android.phone";
    private static final String SIP_RESOURCE = "sip_settings";
    private static final String T9_INDEX_RESOURCE = "t9_indexing_method_title";
    private static final String TELOCATION_AUTO_COUNTRY_CODE_RESOURCE = "telocation_auto_country_code";
    private static final String TELOCATION_CONTACTS_COUNTRYCODE_RESOURCE = "telocation_contacts_countrycode";
    private static final String TELOCATION_ENABLE_RESOURCE = "telocation_enable_title";
    private static final String TELOCATION_RESOURCE = "preference_telocation_title";
    private static final String TURNOVER_MUTE_RESOURCE = "turnover_mute_title";
    private static final String VICE_SLOT_VOLTE_SWITCH_RESOURCE = "vice_slot_volte_data_switch_title";
    private static final String VOICE_PRIVACY_RESOURCE = "voice_privacy";
    private static final String VOLTE_SWITCH_RESOURCE = "volte_switch_title";

    private static boolean getBoolean(Context context, String str) {
        return context.getResources().getBoolean(context.getResources().getIdentifier(str, "bool", PHONE_PACKAGE));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0186  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0198  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x019f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void update(android.content.Context r17, java.util.ArrayList<android.content.ContentProviderOperation> r18) {
        /*
            Method dump skipped, instructions count: 419
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.search.PhoneSettingsUpdateHelper.update(android.content.Context, java.util.ArrayList):void");
    }
}
