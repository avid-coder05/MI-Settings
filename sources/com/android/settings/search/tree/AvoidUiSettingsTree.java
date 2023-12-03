package com.android.settings.search.tree;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class AvoidUiSettingsTree extends SettingsTree {
    protected AvoidUiSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected int getStatus() {
        Sensor defaultSensor = ((SensorManager) ((SettingsTree) this).mContext.getSystemService("sensor")).getDefaultSensor(8);
        if ((defaultSensor != null && "Elliptic Proximity".equals(defaultSensor.getName()) && "Elliptic Labs".equals(defaultSensor.getVendor())) || (true ^ ((SettingsTree) this).mContext.getPackageManager().isPackageAvailable("com.miui.sensor.avoid"))) {
            return 0;
        }
        return super.getStatus();
    }
}
