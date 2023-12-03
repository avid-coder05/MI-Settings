package com.android.settings.fuelgauge.batterytip.actions;

import android.content.Context;
import android.content.Intent;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settingslib.HelpUtils;

/* loaded from: classes.dex */
public class BatteryDefenderAction extends BatteryTipAction {
    private SettingsActivity mSettingsActivity;

    public BatteryDefenderAction(SettingsActivity settingsActivity) {
        super(settingsActivity.getApplicationContext());
        this.mSettingsActivity = settingsActivity;
    }

    @Override // com.android.settings.fuelgauge.batterytip.actions.BatteryTipAction
    public void handlePositiveAction(int i) {
        this.mMetricsFeatureProvider.action(this.mContext, 1771, i);
        Context context = this.mContext;
        Intent helpIntent = HelpUtils.getHelpIntent(context, context.getString(R.string.help_url_battery_defender), BatteryDefenderAction.class.getName());
        if (helpIntent != null) {
            this.mSettingsActivity.startActivityForResult(helpIntent, 0);
        }
    }
}
