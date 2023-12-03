package com.android.settings.device.controller;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public abstract class BaseDeviceInfoController extends AbstractPreferenceController {
    private boolean mIsAvailable;
    private int mOrder;

    public BaseDeviceInfoController(Context context) {
        super(context);
        this.mIsAvailable = true;
        this.mOrder = -1;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mIsAvailable;
    }

    public void setIsAvailable(boolean z) {
        this.mIsAvailable = z;
    }

    public void setPreferenceTitle(Preference preference, String str) {
        if (preference != null) {
            preference.setTitle(str);
        }
    }

    public void setValueSummary(ValuePreference valuePreference, String str) {
        try {
            valuePreference.setValue(str);
        } catch (RuntimeException unused) {
            valuePreference.setValue(this.mContext.getResources().getString(R.string.device_info_default));
        }
    }
}
