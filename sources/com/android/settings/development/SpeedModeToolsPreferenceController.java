package com.android.settings.development;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.view.MiuiWindowManager$LayoutParams;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.MiuiSettingsReceiver;
import com.android.settings.R;
import com.android.settings.SubSettings;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.CommonNotification;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes.dex */
public class SpeedModeToolsPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public static final String SPEED_MODE_ENABLE = "speed_mode_enable";
    public static final String SPEED_MODE_KEY = "speed_mode";
    private final Context mContext;
    private SwitchPreference mPreference;

    public SpeedModeToolsPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
    }

    private void hideSpeedModeActivatedNotification() {
        ((NotificationManager) this.mContext.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION)).cancel(null, R.string.speed_mode_noti_title);
    }

    private void showSpeedModeActivatedNotification() {
        String string = this.mContext.getString(R.string.auto_task_operation_close);
        CommonNotification.Builder builder = new CommonNotification.Builder(this.mContext);
        int i = R.string.speed_mode_noti_title;
        CommonNotification.Builder actionText = builder.setNotifyId(i).setChannel(SPEED_MODE_KEY, this.mContext.getResources().getString(R.string.menu_item_notification_power_text)).setActionText(string);
        int i2 = R.drawable.ic_performance_notification;
        actionText.setNotificationIcon(i2).setSmallIcon(i2).setContentTitle(this.mContext.getString(i)).setContentText(this.mContext.getString(R.string.speed_mode_noti_summary)).setImportance(4).setEnableKeyguard(true).setEnableFloat(true).setResident(true);
        Intent intent = new Intent(this.mContext, SubSettings.class);
        intent.setAction("android.intent.action.MAIN");
        intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
        intent.putExtra(":settings:show_fragment", DevelopmentSettingsDashboardFragment.class.getName());
        intent.setFlags(268435456);
        builder.setResultIntent(intent, 0);
        Intent intent2 = new Intent(this.mContext, MiuiSettingsReceiver.class);
        intent2.setAction("miui.intent.action.settings.SPEED_MODE_CLOSED");
        builder.setActionPendingIntent(PendingIntent.getBroadcast(this.mContext, 0, intent2, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE));
        builder.build().show();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), SPEED_MODE_ENABLE, 0) == 1 ? 0 : 3;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return SPEED_MODE_KEY;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public void onDeveloperOptionsSwitchDisabled() {
        if (this.mPreference != null) {
            hideSpeedModeActivatedNotification();
            this.mPreference.setChecked(false);
            this.mPreference.setEnabled(false);
            Settings.System.putInt(this.mContext.getContentResolver(), SPEED_MODE_KEY, 0);
            this.mContext.getContentResolver().notifyChange(Settings.System.getUriFor(SPEED_MODE_KEY), null);
        }
    }

    public void onDeveloperOptionsSwitchEnabled() {
        SwitchPreference switchPreference = this.mPreference;
        if (switchPreference != null) {
            switchPreference.setEnabled(true);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (((Boolean) obj).booleanValue()) {
            showSpeedModeActivatedNotification();
            this.mPreference.setChecked(true);
            Settings.System.putInt(this.mContext.getContentResolver(), SPEED_MODE_KEY, 1);
        } else {
            hideSpeedModeActivatedNotification();
            this.mPreference.setChecked(false);
            Settings.System.putInt(this.mContext.getContentResolver(), SPEED_MODE_KEY, 0);
        }
        this.mContext.getContentResolver().notifyChange(Settings.System.getUriFor(SPEED_MODE_KEY), null);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        try {
            if (Settings.System.getInt(this.mContext.getContentResolver(), SPEED_MODE_KEY) == 0) {
                this.mPreference.setChecked(false);
            } else {
                this.mPreference.setChecked(true);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
