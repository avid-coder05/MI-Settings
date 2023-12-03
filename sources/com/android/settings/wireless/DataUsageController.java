package com.android.settings.wireless;

import android.content.Context;
import android.content.Intent;
import androidx.preference.Preference;
import com.android.settingslib.core.AbstractPreferenceController;
import miui.os.Build;

/* loaded from: classes2.dex */
public class DataUsageController extends AbstractPreferenceController {
    public DataUsageController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "data_usage_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        Intent intent;
        if (!"data_usage_settings".equals(preference.getKey()) || (intent = preference.getIntent()) == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        intent.putExtra("slot_num_tag", true);
        this.mContext.startActivity(intent);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !Build.IS_TABLET;
    }
}
