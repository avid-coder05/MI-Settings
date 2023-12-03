package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.Locale;
import miui.os.Build;

/* loaded from: classes.dex */
public class XiaomiHpLocationController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "XiaomiHpLocationController";
    private static final String XM_HP_LOCATION = "xiaomi_high_precise_location";
    private static final int XM_HP_LOCATION_ERROR = 100;
    private static final int XM_HP_LOCATION_OFF = 2;
    private static final int XM_HP_LOCATION_ON = 1;
    private Context mContext;
    private PreferenceScreen mPs;
    private RestrictedSwitchPreference mSwitchBar;
    private boolean mValidListener;

    public XiaomiHpLocationController(Context context, String str, Lifecycle lifecycle) {
        super(context, str);
        this.mContext = context;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    private boolean hasXiaomiHpFeature() {
        return SystemProperties.getBoolean("persist.vendor.gnss.hpLocSetUI", false);
    }

    private boolean isDisabled() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), XM_HP_LOCATION, 2) == 2;
    }

    private boolean isZh() {
        return Locale.getDefault().toString().endsWith("zh_CN");
    }

    private void setXmHpLocationOn(boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("set to ");
        sb.append(z ? "on" : "off");
        Log.d(TAG, sb.toString());
        if (z) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), XM_HP_LOCATION, 1);
        } else {
            Settings.Secure.putInt(this.mContext.getContentResolver(), XM_HP_LOCATION, 2);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPs = preferenceScreen;
        this.mSwitchBar = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        if (isDisabled()) {
            this.mSwitchBar.setChecked(false);
        } else {
            this.mSwitchBar.setChecked(true);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!Build.IS_INTERNATIONAL_BUILD && isZh() && hasXiaomiHpFeature()) {
            Log.d(TAG, "could show switch button");
            return 0;
        }
        setXmHpLocationOn(false);
        Log.d(TAG, "not China version or not specific device");
        return 3;
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
        if ("location_toggle".equals(preference.getKey())) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            RestrictedSwitchPreference restrictedSwitchPreference2 = (RestrictedSwitchPreference) this.mPs.findPreference(XM_HP_LOCATION);
            if (restrictedSwitchPreference2 != null) {
                restrictedSwitchPreference2.setEnabled(restrictedSwitchPreference.isChecked());
                restrictedSwitchPreference2.setChecked(restrictedSwitchPreference.isChecked());
                return true;
            }
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), XM_HP_LOCATION, 2);
        StringBuilder sb = new StringBuilder();
        sb.append("current mode is: ");
        sb.append(i == 1 ? "on" : "off");
        Log.d(TAG, sb.toString());
        if (i == 1) {
            setXmHpLocationOn(false);
            return true;
        } else if (i == 2) {
            setXmHpLocationOn(true);
            return true;
        } else if (i == 100) {
            Log.e(TAG, "ERROR: unknown state ");
            return true;
        } else {
            return false;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mValidListener) {
            return;
        }
        this.mSwitchBar.setOnPreferenceChangeListener(this);
        this.mValidListener = true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mValidListener) {
            this.mSwitchBar.setOnPreferenceChangeListener(null);
            this.mValidListener = false;
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
