package com.android.settings.dashboard;

import android.content.Context;
import com.android.settings.search.tree.WirelessSettingsTree;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.search.SearchUtils;

/* loaded from: classes.dex */
class DashBoardTileInjector {
    public static void setTileSummary(Context context, Tile tile) {
        if (context == null || tile == null || tile.getPackageName() == null || !tile.getPackageName().equals(WirelessSettingsTree.Android_Auto.PACKAGE_NAME) || tile.getCategory() == null || !tile.getCategory().equals("com.android.settings.category.ia.device")) {
            return;
        }
        tile.overrideSummary(SearchUtils.getString(context, WirelessSettingsTree.Android_Auto.PACKAGE_NAME, "system_settings_summary"));
    }
}
