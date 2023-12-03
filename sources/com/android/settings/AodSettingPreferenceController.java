package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.AodPreferenceController;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.AodUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.HashSet;
import java.util.Set;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class AodSettingPreferenceController extends TogglePreferenceController {
    public static final String KEY_AOD_SETTINGS = "aod_settings_switch";
    private boolean mAodShowModeStyleSelectAvaliable;
    public Set<AodPreferenceController> mControllerList;
    public PreferenceScreen mPreferenceScreen;

    public AodSettingPreferenceController(Context context) {
        super(context, KEY_AOD_SETTINGS);
        this.mControllerList = new HashSet();
        this.mAodShowModeStyleSelectAvaliable = false;
    }

    private boolean handleToggleInversionPreferenceChange(boolean z) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        if ((Settings.Secure.getInt(this.mContext.getContentResolver(), "accessibility_display_inversion_enabled", 0) == 1) && z) {
            builder.setMessage(R.string.aod_close_color_inversion);
            builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null);
            builder.setPositiveButton(R.string.aod_to_close, new DialogInterface.OnClickListener() { // from class: com.android.settings.AodSettingPreferenceController.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((AbstractPreferenceController) AodSettingPreferenceController.this).mContext.startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
                }
            });
            builder.create().show();
            return false;
        }
        AodUtils.setAodModeState(this.mContext.getApplicationContext(), z);
        AodUtils.setAodModeUserSet(this.mContext.getApplicationContext(), z);
        if (!z && Settings.Secure.getInt(this.mContext.getContentResolver(), "need_reset_aod_time", 0) == 1) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), "aod_mode_time", 1);
            Settings.Secure.putInt(this.mContext.getContentResolver(), "need_reset_aod_time", 0);
        }
        return true;
    }

    public void addController(AodPreferenceController aodPreferenceController) {
        if (aodPreferenceController == null || this.mControllerList.contains(aodPreferenceController)) {
            return;
        }
        this.mControllerList.add(aodPreferenceController);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return !AodUtils.isAodAvailable(this.mContext.getApplicationContext()) ? 3 : 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return AodUtils.isAodEnabled(this.mContext.getApplicationContext());
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public void setAodShowModeStyleSelectAvaliable(boolean z) {
        this.mAodShowModeStyleSelectAvaliable = z;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        boolean handleToggleInversionPreferenceChange = handleToggleInversionPreferenceChange(z);
        if (handleToggleInversionPreferenceChange) {
            for (AodPreferenceController aodPreferenceController : this.mControllerList) {
                aodPreferenceController.updateState(this.mPreferenceScreen.findPreference(aodPreferenceController.getPreferenceKey()));
            }
        }
        return handleToggleInversionPreferenceChange;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
