package com.android.settings.device.controller;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.device.DeviceBasicInfoPresenter;
import com.android.settings.device.MiuiMyDeviceDetailSettings;
import com.android.settings.device.MiuiMyDeviceSettings;

/* loaded from: classes.dex */
public class MiuiAllSpecsController extends BaseDeviceInfoController {
    private MiuiMyDeviceSettings mFragment;

    public MiuiAllSpecsController(Context context, MiuiMyDeviceSettings miuiMyDeviceSettings) {
        super(context);
        this.mFragment = miuiMyDeviceSettings;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "device_more_parameter";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey())) {
            Bundle bundle = new Bundle();
            DeviceBasicInfoPresenter presenter = this.mFragment.getPresenter();
            if (presenter != null && presenter.isCardsInitComplete()) {
                bundle.putParcelableArray("cards_data", presenter.getCards());
            }
            Resources resources = this.mFragment.getActivity().getResources();
            int i = R.string.complete_parameters;
            bundle.putString(":settings:show_fragment_title", resources.getString(i));
            MiuiMyDeviceSettings miuiMyDeviceSettings = this.mFragment;
            miuiMyDeviceSettings.startFragment(miuiMyDeviceSettings, MiuiMyDeviceDetailSettings.class.getName(), i, 0, bundle);
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }
}
