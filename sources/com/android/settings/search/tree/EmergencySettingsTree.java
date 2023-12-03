package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.search.SettingsTree;
import com.android.settingslib.search.TinyIntent;
import java.util.HashMap;
import java.util.LinkedList;
import miui.payment.PaymentManager;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class EmergencySettingsTree extends SettingsTree {
    private static final String TAG = "EmergencySettingsTree";
    private static final String TILES_FOR_CATEGOR_EMERGENCY = "com.android.settings.category.ia.emergency";
    private static HashMap<String, String> sTileMapCache = new HashMap<>();

    /* loaded from: classes2.dex */
    private interface Emergency_Location_Information {
        public static final String CLASS_NAME = "com.google.android.gms.thunderbird.settings.ThunderbirdSettingInjectorPlatformService";
        public static final String PACKAGE_NAME = "com.google.android.gms";
        public static final String RESOURCE = "com.google.android.gms/com.google.android.gms.thunderbird.settings.ThunderbirdSettingInjectorPlatformService";
    }

    /* loaded from: classes2.dex */
    private interface First_Aid_Info {
        public static final String CLASS_NAME = "com.android.emergency.EmergencyInfoActivity";
        public static final String PACKAGE_NAME = "com.android.emergency";
        public static final String RESOURCE = "com.android.emergency/com.android.emergency.EmergencyInfoActivity";
    }

    protected EmergencySettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    private void addItem() {
        DashboardCategory tilesForCategory = FeatureFactory.getFactory(((SettingsTree) this).mContext).getDashboardFeatureProvider(((SettingsTree) this).mContext).getTilesForCategory(TILES_FOR_CATEGOR_EMERGENCY);
        if (tilesForCategory != null) {
            for (Tile tile : tilesForCategory.getTiles()) {
                String valueOf = String.valueOf(tile.getTitle(((SettingsTree) this).mContext));
                String str = tile.getPackageName() + "/" + tile.getComponentName();
                Log.i(TAG, "Tile title: " + valueOf);
                Log.i(TAG, "Tile resource: " + str);
                sTileMapCache.put(str, valueOf);
                addSonJson(str, valueOf);
            }
        }
    }

    private void addSonJson(String str, String str2) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("resource", str);
            jSONObject.put("title", str2);
            jSONObject.put("temporary", true);
            if (str.equals(Emergency_Location_Information.RESOURCE)) {
                jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent().setClassName("com.google.android.gms", Emergency_Location_Information.CLASS_NAME).toJSONObject());
            } else if (str.equals(First_Aid_Info.RESOURCE)) {
                jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent().setClassName(First_Aid_Info.PACKAGE_NAME, First_Aid_Info.CLASS_NAME).toJSONObject());
            }
            addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Intent getIntent() {
        return MiuiSecurityAndPrivacySettingsTree.CELL_BROADCAST_SETTINGS.equals(getColumnValue("resource")) ? new TinyIntent("android.intent.action.MAIN").setClassName("com.google.android.cellbroadcastreceiver", "com.android.cellbroadcastreceiver.CellBroadcastSettings").toIntent() : super.getIntent();
    }

    public LinkedList<SettingsTree> getSons() {
        if ("emergency_settings_preference_title".equals(getColumnValue("resource")) && MiuiUtils.isSupportSafetyEmergencySettings(((SettingsTree) this).mContext)) {
            LinkedList sons = super.getSons();
            if (sons != null) {
                for (int size = sons.size() - 1; size >= 0; size--) {
                    SettingsTree settingsTree = (SettingsTree) sons.get(size);
                    if (Boolean.parseBoolean(settingsTree.getColumnValue("temporary"))) {
                        settingsTree.removeSelf();
                    }
                }
            }
            addItem();
        }
        return super.getSons();
    }

    protected String getTitle(boolean z) {
        String str;
        String columnValue = getColumnValue("resource");
        HashMap<String, String> hashMap = sTileMapCache;
        return (hashMap == null || !hashMap.containsKey(columnValue) || (str = sTileMapCache.get(columnValue)) == null) ? super.getTitle(z) : str;
    }

    public boolean initialize() {
        if (!"emergency_settings_preference_title".equals(getColumnValue("resource")) || MiuiUtils.isSupportSafetyEmergencySettings(((SettingsTree) this).mContext)) {
            return super.initialize();
        }
        return true;
    }
}
