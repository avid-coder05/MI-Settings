package com.android.settings.search.tree;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.android.settings.dashboard.DashboardFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.search.SettingsTree;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import miui.os.Build;
import miui.provider.ExtraTelephony;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class SecurityStatusSettingsTree extends SettingsTree {
    private String mTitle;

    protected SecurityStatusSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mTitle = jSONObject.optString("title");
    }

    public LinkedList<SettingsTree> getSons() {
        List<Tile> tiles;
        if ("security_status".equals(getColumnValue("resource"))) {
            LinkedList sons = super.getSons();
            if (sons != null) {
                for (int size = sons.size() - 1; size >= 0; size--) {
                    SettingsTree settingsTree = (SettingsTree) sons.get(size);
                    if (Boolean.parseBoolean(settingsTree.getColumnValue("temporary"))) {
                        settingsTree.removeSelf();
                    }
                }
            }
            DashboardFeatureProvider dashboardFeatureProvider = FeatureFactory.getFactory(((SettingsTree) this).mContext).getDashboardFeatureProvider(((SettingsTree) this).mContext);
            DashboardCategory tilesForCategory = dashboardFeatureProvider.getTilesForCategory("com.android.settings.category.ia.security");
            if (tilesForCategory != null && (tiles = tilesForCategory.getTiles()) != null && !tiles.isEmpty()) {
                Iterator<Tile> it = tiles.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Tile next = it.next();
                    if ("security_status_package_verifier".equals(dashboardFeatureProvider.getDashboardKeyForTile(next))) {
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("temporary", true);
                            jSONObject.put("title", next.getTitle(((SettingsTree) this).mContext));
                            jSONObject.put(ExtraTelephony.UnderstandInfo.CLASS, SecurityStatusSettingsTree.class.getName());
                            addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
                            break;
                        } catch (JSONException unused) {
                        }
                    }
                }
            }
        }
        return super.getSons();
    }

    protected String getTitle(boolean z) {
        return !TextUtils.isEmpty(this.mTitle) ? this.mTitle : super.getTitle(z);
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        if (Build.IS_INTERNATIONAL_BUILD) {
            if ("security_update".equals(columnValue) && TextUtils.isEmpty(Build.VERSION.SECURITY_PATCH)) {
                return true;
            }
            return super.initialize();
        }
        return true;
    }
}
