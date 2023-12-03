package com.android.settings.search.tree;

import android.content.Context;
import android.provider.Settings;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class DateTimeSettingsTree extends SettingsTree {
    protected DateTimeSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        if ("date_time_set_date".equals(columnValue) || columnValue.equals("date_time_set_time")) {
            if (Settings.Global.getInt(((SettingsTree) this).mContext.getContentResolver(), "auto_time", 0) > 0) {
                return 1;
            }
        } else if ("date_time_set_timezone".equals(columnValue) && Settings.Global.getInt(((SettingsTree) this).mContext.getContentResolver(), "auto_time_zone", 0) > 0) {
            return 1;
        }
        return super.getStatus();
    }
}
