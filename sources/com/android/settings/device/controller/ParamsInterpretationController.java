package com.android.settings.device.controller;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.utils.AssetsResourcesLoadUtil;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.android.settingslib.utils.ThreadUtils;
import java.util.HashMap;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class ParamsInterpretationController extends BasePreferenceController {
    private static final String ASSETS_JSON = "device_link.json";
    public static final String AUTHORITY = "content://com.xiaomi.vipaccount.provider/device_link";
    private static final String MI_FEN_PACKAGE_NAME = "com.xiaomi.vipaccount";
    private static final String PREF_FILE = "ParamsInterpretationPref";
    private static final String PREF_KEY_PARAMS = "PREF_KEY_PARAMS";
    private static final String TAG = "ParamsInterpretationCon";
    private DeviceEntranceBean deviceEntranceBean;

    /* loaded from: classes.dex */
    public static class DeviceEntranceBean {
        public String h5Url;
        public String mioUrl;

        public boolean isSupport() {
            return (TextUtils.isEmpty(this.mioUrl) && TextUtils.isEmpty(this.h5Url)) ? false : true;
        }
    }

    public ParamsInterpretationController(Context context) {
        this(context, "device_params_interpretation");
    }

    public ParamsInterpretationController(Context context, String str) {
        super(context, str);
        if (isNotSupport()) {
            return;
        }
        syncDeviceBean();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v0, types: [com.android.settings.device.controller.ParamsInterpretationController] */
    /* JADX WARN: Type inference failed for: r3v0 */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v11 */
    /* JADX WARN: Type inference failed for: r3v12 */
    /* JADX WARN: Type inference failed for: r3v2, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r3v5, types: [com.android.settings.device.controller.ParamsInterpretationController$DeviceEntranceBean] */
    /* JADX WARN: Type inference failed for: r3v9 */
    public String queryRemoteMifenDeviceInfo(Context context) {
        DeviceEntranceBean deviceEntranceBean;
        DeviceEntranceBean deviceEntranceBean2 = 0;
        deviceEntranceBean2 = 0;
        deviceEntranceBean2 = 0;
        Cursor cursor = null;
        try {
            try {
                Log.d(TAG, "queryRemoteMifenDeviceInfo");
                Cursor query = context.getContentResolver().query(Uri.parse(AUTHORITY), new String[]{"deviceNo", "mioUrl", "h5Url"}, null, null, null);
                if (query != null) {
                    try {
                        try {
                            if (query.moveToFirst()) {
                                String string = query.getString(query.getColumnIndex("mioUrl"));
                                String string2 = query.getString(query.getColumnIndex("h5Url"));
                                Log.d(TAG, "Device: query success," + string + "   " + string2);
                                deviceEntranceBean = new DeviceEntranceBean();
                                try {
                                    deviceEntranceBean.h5Url = string2;
                                    deviceEntranceBean.mioUrl = string;
                                    deviceEntranceBean2 = deviceEntranceBean;
                                } catch (Exception e) {
                                    e = e;
                                    cursor = query;
                                    Log.d(TAG, "Device: query failed, %s", e);
                                    if (cursor != null) {
                                        cursor.close();
                                    }
                                    deviceEntranceBean2 = deviceEntranceBean;
                                    return jsonObjToString(deviceEntranceBean2);
                                }
                            }
                        } catch (Throwable th) {
                            th = th;
                            deviceEntranceBean2 = query;
                            if (deviceEntranceBean2 != 0) {
                                deviceEntranceBean2.close();
                            }
                            throw th;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        deviceEntranceBean = null;
                    }
                }
                if (query != null) {
                    query.close();
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e3) {
            e = e3;
            deviceEntranceBean = null;
        }
        return jsonObjToString(deviceEntranceBean2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveDeviceInfo(String str) {
        SharedPreferences.Editor edit = this.mContext.getSharedPreferences(PREF_FILE, 0).edit();
        edit.putString(PREF_KEY_PARAMS, str);
        edit.apply();
    }

    private void startMifenApp(String str) {
        this.mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUi(Preference preference) {
        syncDeviceBean();
        if (preference != null) {
            setVisible(preference, isShowEntrance());
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    protected DeviceEntranceBean deviceJsonObjToBean(JSONObject jSONObject) {
        DeviceEntranceBean deviceEntranceBean = new DeviceEntranceBean();
        if (jSONObject != null) {
            deviceEntranceBean.h5Url = jSONObject.optString("h5Url");
            deviceEntranceBean.mioUrl = jSONObject.optString("mioUrl");
        }
        return deviceEntranceBean;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isNotSupport()) {
            return;
        }
        syncMifenDeviceInfo(preferenceScreen.findPreference(getPreferenceKey()));
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!isNotSupport() && isShowEntrance()) ? 0 : 2;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        try {
            if (preference.getKey().equals(getPreferenceKey()) && isShowEntrance()) {
                String str = isMifenAppSupport() ? this.deviceEntranceBean.mioUrl : this.deviceEntranceBean.h5Url;
                HashMap hashMap = new HashMap();
                hashMap.put("link", str);
                OneTrackInterfaceUtils.track("miui_settings_mifen", hashMap);
                startMifenApp(str);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "stat mifen app error");
            e.printStackTrace();
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    protected boolean isMifenAppSupport() {
        return CommonUtils.isAppExist(this.mContext.getApplicationContext(), MI_FEN_PACKAGE_NAME) && CommonUtils.getAppVersionCode(this.mContext, MI_FEN_PACKAGE_NAME) > 20000;
    }

    public boolean isNotSupport() {
        return Build.IS_INTERNATIONAL_BUILD;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    protected boolean isShowEntrance() {
        DeviceEntranceBean deviceEntranceBean = this.deviceEntranceBean;
        return deviceEntranceBean != null && deviceEntranceBean.isSupport();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    protected String jsonObjToString(DeviceEntranceBean deviceEntranceBean) {
        if (deviceEntranceBean == null) {
            return "";
        }
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("h5Url", deviceEntranceBean.h5Url);
            jSONObject.put("mioUrl", deviceEntranceBean.mioUrl);
            Log.d(TAG, "jsonObjToString" + jSONObject.toString());
            return jSONObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected DeviceEntranceBean queryDeviceBean() {
        DeviceEntranceBean queryLocalDeviceInfo = queryLocalDeviceInfo();
        if (queryLocalDeviceInfo == null || !queryLocalDeviceInfo.isSupport()) {
            try {
                queryLocalDeviceInfo = deviceJsonObjToBean((JSONObject) new JSONObject(AssetsResourcesLoadUtil.loadJson(this.mContext, ASSETS_JSON)).opt(android.os.Build.DEVICE));
                Log.d(TAG, "json parse success" + queryLocalDeviceInfo.mioUrl);
                return queryLocalDeviceInfo;
            } catch (Exception e) {
                Log.e(TAG, "json parse error==>" + e.getMessage());
                e.printStackTrace();
                return queryLocalDeviceInfo;
            }
        }
        return queryLocalDeviceInfo;
    }

    public DeviceEntranceBean queryLocalDeviceInfo() {
        String string = this.mContext.getSharedPreferences(PREF_FILE, 0).getString(PREF_KEY_PARAMS, "");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        try {
            return deviceJsonObjToBean(new JSONObject(string));
        } catch (Exception unused) {
            return null;
        }
    }

    protected void syncDeviceBean() {
    }

    protected void syncMifenDeviceInfo(final Preference preference) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.device.controller.ParamsInterpretationController.1
            @Override // java.lang.Runnable
            public void run() {
                ParamsInterpretationController paramsInterpretationController = ParamsInterpretationController.this;
                paramsInterpretationController.saveDeviceInfo(paramsInterpretationController.queryRemoteMifenDeviceInfo(((AbstractPreferenceController) paramsInterpretationController).mContext));
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.device.controller.ParamsInterpretationController.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AnonymousClass1 anonymousClass1 = AnonymousClass1.this;
                        ParamsInterpretationController.this.updateUi(preference);
                    }
                });
            }
        });
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
