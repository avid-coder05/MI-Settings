package com.android.settings.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.R$layout;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes2.dex */
public class AddWifiNetworkPreference extends Preference {
    private final Drawable mScanIconDrawable;

    public AddWifiNetworkPreference(Context context) {
        super(context);
        setLayoutResource(R$layout.preference_access_point);
        setWidgetLayoutResource(R.layout.wifi_button_preference_widget);
        setIcon(R.drawable.ic_add_24dp);
        setTitle(R.string.wifi_add_network);
        this.mScanIconDrawable = getDrawable(R.drawable.ic_scan_24dp);
    }

    private Drawable getDrawable(int i) {
        try {
            return getContext().getDrawable(i);
        } catch (Resources.NotFoundException unused) {
            Log.e("AddWifiNetworkPreference", "Resource does not exist: " + i);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(View view) {
        getContext().startActivity(WifiDppUtils.getEnrolleeQrCodeScannerIntent(null));
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ImageButton imageButton = (ImageButton) preferenceViewHolder.findViewById(R.id.button_icon);
        imageButton.setImageDrawable(this.mScanIconDrawable);
        imageButton.setContentDescription(getContext().getString(R.string.wifi_dpp_scan_qr_code));
        imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.AddWifiNetworkPreference$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AddWifiNetworkPreference.this.lambda$onBindViewHolder$0(view);
            }
        });
    }
}
