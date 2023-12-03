package com.android.settings.device.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Pair;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.utils.SettingsFeatures;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiOneKeyMirgrateController extends BaseDeviceInfoController {
    public static final String APP_STORE_URL;
    private Activity mActivity;

    static {
        APP_STORE_URL = Build.IS_INTERNATIONAL_BUILD ? "https://play.google.com/store/apps/details?id=com.miui.huanji" : "https://app.xiaomi.com/details?id=com.miui.huanji";
    }

    public MiuiOneKeyMirgrateController(Context context) {
        super(context);
    }

    public MiuiOneKeyMirgrateController(Context context, Activity activity) {
        super(context);
        this.mActivity = activity;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "one_key_migrate";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), "one_key_migrate") || this.mActivity == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        Intent intent = new Intent("com.intent.action.Huanji");
        intent.setPackage("com.miui.huanji");
        intent.putExtra("request_from", "com.android.settings");
        if (!MiuiUtils.isIntentActivityExistAsUser(this.mContext, intent, UserHandle.myUserId())) {
            intent = new Intent("android.intent.action.VIEW", Uri.parse(APP_STORE_URL));
        }
        intent.setFlags(268435456);
        this.mActivity.startActivity(intent);
        Pair<Integer, Integer> systemDefaultEnterAnim = MiuiUtils.getSystemDefaultEnterAnim(this.mActivity);
        this.mActivity.overridePendingTransition(((Integer) systemDefaultEnterAnim.first).intValue(), ((Integer) systemDefaultEnterAnim.second).intValue());
        return true;
    }

    @Override // com.android.settings.device.controller.BaseDeviceInfoController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !SettingsFeatures.isNeedRemoveOneKeyMigrate(this.mContext);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
    }
}
