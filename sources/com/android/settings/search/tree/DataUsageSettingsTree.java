package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class DataUsageSettingsTree extends SettingsTree {
    protected DataUsageSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public Intent getIntent() {
        if (Build.DEVICE.equals("cappu")) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
            intent.putExtra(":settings:show_fragment", "com.android.settings.datausage.DataUsageSummary");
            intent.putExtra(":android:no_headers", true);
            return intent;
        }
        return super.getIntent().putExtra("slot_num_tag", true);
    }

    public boolean initialize() {
        if (Build.DEVICE.equals("clover")) {
            return true;
        }
        return super.initialize();
    }
}
