package com.android.settings.search.tree;

import android.content.Context;
import android.media.AudioServiceInjector;
import android.provider.MiuiSettings;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SettingsTree;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class MiuiSilentSettingsTree extends SettingsTree {
    public static final String MUTE_MUSIC_STREAM = "mute_music_stream";
    public static final String MUTE_VOICEASSIST_STREAM = "mute_voiceassist_stream";
    public static final String NETWORK_ALARM = "network_alarm";
    public static final String POPUP_WINDOW = "popup_window";
    public static final String REPEAT_CALL = "repeat_call";
    public static final String VIP_LIST = "vip_list";

    protected MiuiSilentSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    protected int getStatus() {
        char c;
        String columnValue = getColumnValue("resource");
        int zenMode = MiuiSettings.SilenceMode.getZenMode(((SettingsTree) this).mContext);
        columnValue.hashCode();
        switch (columnValue.hashCode()) {
            case -621126400:
                if (columnValue.equals(NETWORK_ALARM)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1058808640:
                if (columnValue.equals(MUTE_MUSIC_STREAM)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1489316736:
                if (columnValue.equals("vip_list")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1597001346:
                if (columnValue.equals("repeat_call")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1963832842:
                if (columnValue.equals(MUTE_VOICEASSIST_STREAM)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1967754979:
                if (columnValue.equals(POPUP_WINDOW)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                if (!SettingsFeatures.isSupportCustomZenPriorityPkg() || zenMode != 1) {
                    return 0;
                }
            case 1:
                if (zenMode != 4) {
                    return 0;
                }
                break;
            case 2:
            case 3:
            case 5:
                if (zenMode != 1) {
                    return 0;
                }
                break;
            case 4:
                if (zenMode != 4 || AudioServiceInjector.getVoiceAssistNum() == -1 || Build.IS_GLOBAL_BUILD) {
                    return 0;
                }
                break;
        }
        return super.getStatus();
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        if ((columnValue.equals("vip_list") || columnValue.equals("repeat_call")) && Build.IS_TABLET) {
            return true;
        }
        return super.initialize();
    }
}
