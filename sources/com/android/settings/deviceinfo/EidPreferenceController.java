package com.android.settings.deviceinfo;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes.dex */
public class EidPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private String mEid;

    public EidPreferenceController(Context context) {
        super(context);
        try {
            Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
            this.mEid = (String) cls.getMethod("getProductEid", new Class[0]).invoke(cls.getMethod("getDefault", new Class[0]).invoke(null, new Object[0]), new Object[0]);
        } catch (Exception e) {
            Log.e("EidPreferenceController", "get eid exception: ", e);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "esim_id";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (SettingsFeatures.isNeedESIMCustmized() || SettingsFeatures.isNeedESIMFeature()) && !TextUtils.isEmpty(this.mEid);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setVisible(!TextUtils.isEmpty(this.mEid));
        preference.setSummary(this.mEid);
    }
}
