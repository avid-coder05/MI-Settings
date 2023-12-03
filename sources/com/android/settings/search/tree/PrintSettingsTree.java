package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class PrintSettingsTree extends SettingsTree {
    protected PrintSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected int getStatus() {
        if (SearchUtils.isEmpty(((SettingsTree) this).mContext.getPackageManager().queryIntentServices(new Intent("android.printservice.PrintService"), 132))) {
            return 0;
        }
        return super.getStatus();
    }
}
