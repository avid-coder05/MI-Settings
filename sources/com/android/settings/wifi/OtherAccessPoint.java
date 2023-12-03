package com.android.settings.wifi;

import android.content.Context;
import android.provider.Settings;
import android.view.View;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes2.dex */
public class OtherAccessPoint extends Preference {
    private boolean mIsInProvision;

    public OtherAccessPoint(Context context) {
        super(context, true);
        this.mIsInProvision = !deviceIsProvisioned(context);
        setLayoutResource(R.layout.add_network_title);
    }

    private boolean deviceIsProvisioned(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        view.setBackgroundColor(0);
        if (this.mIsInProvision) {
            view.setBackgroundResource(R.drawable.provision_list_item_background);
        }
    }
}
