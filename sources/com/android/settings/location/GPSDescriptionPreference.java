package com.android.settings.location;

import android.content.Context;
import android.location.LocationManager;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import miuix.animation.Folme;

/* loaded from: classes.dex */
public class GPSDescriptionPreference extends Preference {
    private boolean gpsSupport;
    private boolean indiaBuild;
    private Context mContext;

    public GPSDescriptionPreference(Context context) {
        super(context);
        this.indiaBuild = false;
        this.gpsSupport = false;
        init(context);
    }

    public GPSDescriptionPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.indiaBuild = false;
        this.gpsSupport = false;
        init(context);
    }

    public GPSDescriptionPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.indiaBuild = false;
        this.gpsSupport = false;
        init(context);
    }

    public GPSDescriptionPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.indiaBuild = false;
        this.gpsSupport = false;
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.indiaBuild = SystemProperties.get("ro.product.mod_device", "").endsWith("in_global");
        this.gpsSupport = SystemProperties.getBoolean("ro.config.gnss.support", false);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setBackgroundResource(0);
        TextView textView = (TextView) view.findViewById(16908304);
        boolean isProviderEnabled = ((LocationManager) this.mContext.getSystemService("location")).isProviderEnabled("gps");
        this.gpsSupport = isProviderEnabled;
        if (isProviderEnabled || this.mContext.getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
            textView.setText(this.mContext.getResources().getString(this.indiaBuild ? R.string.gps_description_global : R.string.gps_description));
        } else {
            textView.setText(this.mContext.getResources().getString(R.string.location_description_no_gps));
        }
    }
}
