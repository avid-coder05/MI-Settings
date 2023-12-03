package com.android.settings.search.tree;

import android.content.Context;
import android.content.ContextCompat;
import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IWindowManager;
import android.view.IWindowManagerCompat;
import com.android.settings.utils.Utils;
import com.android.settingslib.search.SettingsTree;
import miui.util.CustomizeUtil;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class InfinityDisplaySettingsTree extends SettingsTree {
    protected InfinityDisplaySettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        if (!"notch_force_black_title".equals(columnValue) || (CustomizeUtil.HAS_NOTCH && !"odin".equals(Build.DEVICE))) {
            if (!"cutout_mode_title".equals(columnValue) || (Utils.supportCutoutMode() && !"odin".equals(Build.DEVICE))) {
                if (!"cutout_type_title".equals(columnValue) || (Utils.supportOverlayRoundedCorner() && !"odin".equals(Build.DEVICE))) {
                    if ("screen_max_aspect_ratio_title".equals(columnValue) && (miui.os.Build.IS_TABLET || "odin".equals(Build.DEVICE))) {
                        return 0;
                    }
                    try {
                        if (IWindowManagerCompat.hasNavigationBar(IWindowManager.Stub.asInterface(ServiceManager.getService("window")), ContextCompat.getDisplayId(((SettingsTree) this).mContext))) {
                            return super.getStatus();
                        }
                        return 0;
                    } catch (RemoteException unused) {
                        return 0;
                    }
                }
                return 0;
            }
            return 0;
        }
        return 0;
    }
}
