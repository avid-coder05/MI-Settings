package com.android.settings.search.tree;

import android.content.Context;
import com.android.settings.utils.StatusBarUtils;
import com.android.settings.utils.Utils;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class NotificationControlCenterSettingsTree extends SettingsTree {
    protected NotificationControlCenterSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected int getStatus() {
        if ("control_center_style".equals(getColumnValue("resource")) && (Utils.isPad() || Utils.isFold() || StatusBarUtils.isForceUseControlPanel(((SettingsTree) this).mContext))) {
            return 0;
        }
        return super.getStatus();
    }
}
