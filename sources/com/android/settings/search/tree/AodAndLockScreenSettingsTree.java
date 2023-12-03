package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import com.android.settings.AodAndLockScreenSettings;
import com.android.settings.KeyguardAdvancedSettings;
import com.android.settings.MiuiKeyguardSettingsUtils;
import com.android.settings.R;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.AodUtils;
import com.android.settingslib.search.SettingsTree;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class AodAndLockScreenSettingsTree extends SettingsTree {
    private static final String AOD_AND_LOCK_SCREEN_SETTINGS_TITLE = "aod_and_lock_screen_settings_title";
    private Context mContext;

    protected AodAndLockScreenSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mContext = context;
    }

    private void addSon(String str, boolean z) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("resource", str);
            jSONObject.put(FunctionColumns.IS_CHECKBOX, z);
            addSon(SettingsTree.newInstance(this.mContext, jSONObject, this));
        } catch (JSONException unused) {
        }
    }

    public Intent getIntent() {
        String columnValue = getColumnValue("resource");
        if ("lockscreen_magazine".equals(columnValue)) {
            return AodAndLockScreenSettings.getWallpaperIntent(this.mContext);
        }
        if ("pref_show_lunar_calendar_title".equals(columnValue) || "lock_screen_signature_title".equals(columnValue)) {
            return AodAndLockScreenSettings.getKeyguardClockIntent(this.mContext);
        }
        if ("ambient_display_screen_title".equals(columnValue) || "aod_style_title".equals(columnValue) || MiuiSettingsTree.AOD_NOTIFICATION_STYLE.equals(columnValue)) {
            Intent intent = super.getIntent();
            if (AodUtils.supportSettingSplit(this.mContext)) {
                if (intent == null) {
                    intent = AodAndLockScreenSettings.getAodIntent(this.mContext);
                }
                intent.addFlags(268435456);
            }
            return intent;
        }
        return super.getIntent();
    }

    protected int getStatus() {
        boolean z;
        String columnValue = getColumnValue("resource");
        if ("ambient_display_screen_title".equals(columnValue)) {
            if (!AodUtils.isAodAvailable(this.mContext)) {
                return 0;
            }
        } else if ("aod_style_title".equals(columnValue)) {
            if (!AodUtils.isAodAvailable(this.mContext) || !AodUtils.isAodEnabled(this.mContext)) {
                return 0;
            }
        } else if ("aod_show_mode_title".equals(columnValue)) {
            if (!AodUtils.isAodAvailable(this.mContext) || !AodUtils.isAodEnabled(this.mContext)) {
                return 0;
            }
        } else if ("eye_gaze_title".equals(columnValue)) {
            if (!AodAndLockScreenSettings.isAdaptiveSleepSupported(this.mContext)) {
                return 0;
            }
        } else if ("pick_up_gesture_wakeup_title".equals(columnValue)) {
            if (!AodAndLockScreenSettings.isSupportPickupWakeup(this.mContext)) {
                return 0;
            }
        } else if ("wakeup_for_keyguard_notification_title".equals(columnValue)) {
            if (MiuiKeyguardSettingsUtils.isSupportAodAnimateDevice(this.mContext)) {
                return 0;
            }
        } else if (MiuiSettingsTree.AOD_NOTIFICATION_STYLE.equals(columnValue)) {
            if (!MiuiKeyguardSettingsUtils.isSupportAodAnimateDevice(this.mContext)) {
                return 0;
            }
        } else if ("lockscreen_magazine".equals(columnValue)) {
            if (!AodAndLockScreenSettings.isLockScreenMagazineAvailable(this.mContext)) {
                return 0;
            }
        } else if ("pref_show_lunar_calendar_title".equals(columnValue)) {
            if (!Locale.CHINESE.getLanguage().equals(Locale.getDefault().getLanguage())) {
                return 0;
            }
        } else if ("screen_on_proximity_sensor_title".equals(columnValue)) {
            if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.sensor.proximity") || KeyguardAdvancedSettings.isEllipticProximity(this.mContext)) {
                return 0;
            }
        } else if ("smartcover_lock_or_unlock_screen_tittle".equals(columnValue)) {
            this.mContext.getResources();
            int identifier = Resources.getSystem().getIdentifier("config_smartCoverEnabled", "bool", "android.miui");
            if (identifier > 0) {
                this.mContext.getResources();
                z = Resources.getSystem().getBoolean(identifier);
            } else {
                z = false;
            }
            if (!z) {
                return 0;
            }
        }
        return super.getStatus();
    }

    public String getTitle(boolean z) {
        return (!AOD_AND_LOCK_SCREEN_SETTINGS_TITLE.equals(getColumnValue("resource")) || AodUtils.isAodAvailable(this.mContext)) ? super.getTitle(z) : this.mContext.getResources().getString(R.string.lock_screen_settings_title);
    }

    public boolean initialize() {
        if ("choose_keyguard_clock_style".equals(getColumnValue("resource"))) {
            addSon("pref_show_lunar_calendar_title", true);
            addSon("lock_screen_signature_title", false);
        }
        return super.initialize();
    }
}
