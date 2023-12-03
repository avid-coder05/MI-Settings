package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settings.Utils;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.search.SettingsTree;
import com.android.settingslib.search.TinyIntent;
import java.io.IOException;
import java.util.Iterator;
import miui.payment.PaymentManager;
import miui.provider.ExtraTelephony;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public final class SystemAppSettingsTree extends SettingsTree {
    private static final String PKG_NAME_MI_CLOUD_SERVICE = "com.miui.cloudservice";
    public static final String SYSTEM_APP_MARK = "SYSTEM_APP_MARK";
    private static final String TAG = "SystemAppSettingsTree";

    protected SystemAppSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    private void addIndex(String str) {
        String str2;
        JSONObject readJSONObject;
        for (ResolveInfo resolveInfo : ((SettingsTree) this).mContext.getPackageManager().queryIntentActivities(new Intent("miui.intent.action.APP_SETTINGS"), 128)) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null && (str2 = activityInfo.packageName) != null && (str == null || str2.equals(str))) {
                if (!resolveInfo.activityInfo.packageName.equals("com.miui.voiceassist") && (Utils.isVoiceCapable(((SettingsTree) this).mContext) || !resolveInfo.activityInfo.packageName.equals("com.android.phone"))) {
                    if (resolveInfo.system && !"com.miui.googlebase.ui.GmsCoreSettings".equals(resolveInfo.activityInfo.name) && (!resolveInfo.activityInfo.packageName.equals("com.miui.cloudservice") || !MiuiUtils.isDeviceManaged(((SettingsTree) this).mContext))) {
                        String str3 = resolveInfo.activityInfo.packageName;
                        AssetManager assets = SearchUtils.getPackageContext(((SettingsTree) this).mContext, str3).getAssets();
                        String str4 = null;
                        Bundle bundle = resolveInfo.activityInfo.metaData;
                        if (bundle != null) {
                            str4 = bundle.getString("settings_search_index");
                        } else {
                            Log.d(TAG, "no meta-data in " + str3);
                        }
                        try {
                            if (TextUtils.isEmpty(str4)) {
                                Log.d(TAG, "no settings search index in " + str3);
                                readJSONObject = new JSONObject();
                            } else {
                                readJSONObject = SearchUtils.readJSONObject(assets.open(str4));
                            }
                            if (!readJSONObject.has(ExtraTelephony.UnderstandInfo.CLASS)) {
                                readJSONObject.put(ExtraTelephony.UnderstandInfo.CLASS, SystemAppSubTree.class.getName());
                            }
                            readJSONObject.put(FunctionColumns.PACKAGE, resolveInfo.activityInfo.packageName);
                            TinyIntent tinyIntent = new TinyIntent();
                            tinyIntent.setAction("miui.intent.action.APP_SETTINGS");
                            ActivityInfo activityInfo2 = resolveInfo.activityInfo;
                            tinyIntent.setClassName(activityInfo2.packageName, activityInfo2.name);
                            readJSONObject.put(PaymentManager.KEY_INTENT, tinyIntent.toJSONObject());
                            readJSONObject.put("icon", SYSTEM_APP_MARK);
                            if (!readJSONObject.has("resource")) {
                                readJSONObject.put("resource", resolveInfo.activityInfo.packageName);
                            }
                            addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, readJSONObject, this));
                            Log.d(TAG, "add system app " + resolveInfo.activityInfo.packageName);
                        } catch (IOException unused) {
                            Log.d(TAG, "settings search index registered but not found in " + resolveInfo.activityInfo.packageName);
                        } catch (Exception e) {
                            Log.d(TAG, e.getClass().getSimpleName() + " happens when installing " + resolveInfo.activityInfo.packageName, e);
                        }
                    }
                }
            }
        }
    }

    private void removeIndex(String str) {
        if (getSons() == null) {
            return;
        }
        Iterator it = getSons().iterator();
        while (it.hasNext()) {
            SettingsTree settingsTree = (SettingsTree) it.next();
            if (settingsTree.getPackage().equals(str)) {
                settingsTree.removeSelf();
                Log.d(TAG, "remove system app " + str);
                return;
            }
        }
    }

    public boolean initialize() {
        addIndex(null);
        return super.initialize();
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Uri data = intent.getData();
        if (data != null) {
            String action = intent.getAction();
            String schemeSpecificPart = data.getSchemeSpecificPart();
            if (TextUtils.equals(action, "android.intent.action.PACKAGE_ADDED")) {
                addIndex(schemeSpecificPart);
            } else if (TextUtils.equals(action, "android.intent.action.PACKAGE_REMOVED")) {
                removeIndex(schemeSpecificPart);
            } else if (TextUtils.equals(action, "android.intent.action.PACKAGE_CHANGED")) {
                removeIndex(schemeSpecificPart);
                addIndex(schemeSpecificPart);
            }
        }
    }
}
