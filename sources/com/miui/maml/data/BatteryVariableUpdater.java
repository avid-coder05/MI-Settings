package com.miui.maml.data;

import android.content.Context;
import android.content.Intent;
import miui.provider.Weather;

/* loaded from: classes2.dex */
public class BatteryVariableUpdater extends NotifierVariableUpdater {
    private IndexedVariable mBatteryLevel;
    private int mLevel;

    public BatteryVariableUpdater(VariableUpdaterManager variableUpdaterManager) {
        super(variableUpdaterManager, "android.intent.action.BATTERY_CHANGED");
        this.mBatteryLevel = new IndexedVariable("battery_level", getRoot().getContext().mVariables, true);
    }

    @Override // com.miui.maml.NotifierManager.OnNotifyListener
    public void onNotify(Context context, Intent intent, Object obj) {
        int intExtra;
        if (!intent.getAction().equals("android.intent.action.BATTERY_CHANGED") || (intExtra = intent.getIntExtra(Weather.AlertInfo.LEVEL, -1)) == -1 || this.mLevel == intExtra) {
            return;
        }
        this.mBatteryLevel.set(intExtra >= 100 ? 100.0d : intExtra);
        this.mLevel = intExtra;
        getRoot().requestUpdate();
    }
}
