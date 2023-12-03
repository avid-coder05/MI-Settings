package com.android.settings.wifi;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settingslib.core.AbstractPreferenceController;
import miui.os.Build;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
public class WapiPreferenceController extends AbstractPreferenceController {
    private Context mContext;

    public WapiPreferenceController(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wapi_cert_manage";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), "wapi_cert_manage")) {
            if (TextUtils.equals("mediatek", FeatureParser.getString("vendor"))) {
                Intent intent = new Intent();
                intent.setClassName("com.wapi.wapicertmanager", "com.wapi.wapicertmanager.WapiCertManagerActivity");
                this.mContext.startActivity(intent);
                return true;
            }
            Intent intent2 = new Intent();
            intent2.setClassName("com.wapi.wapicertmanage", "com.wapi.wapicertmanage.WapiCertManageActivity");
            this.mContext.startActivity(intent2);
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !Build.IS_GLOBAL_BUILD;
    }
}
