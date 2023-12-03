package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.overlay.FeatureFactory;
import java.util.Objects;

/* loaded from: classes.dex */
class PrivacyPreferenceControllerHelper {
    private final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;
    private final EnterprisePrivacyFeatureProvider mFeatureProvider;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PrivacyPreferenceControllerHelper(Context context) {
        Objects.requireNonNull(context);
        this.mContext = context;
        this.mFeatureProvider = FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context);
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasDeviceOwner() {
        return this.mFeatureProvider.hasDeviceOwner();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFinancedDevice() {
        if (this.mDevicePolicyManager.isDeviceManaged()) {
            DevicePolicyManager devicePolicyManager = this.mDevicePolicyManager;
            if (devicePolicyManager.getDeviceOwnerType(devicePolicyManager.getDeviceOwnerComponentOnAnyUser()) == 1) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateState(Preference preference) {
        if (preference == null) {
            return;
        }
        String deviceOwnerOrganizationName = this.mFeatureProvider.getDeviceOwnerOrganizationName();
        if (deviceOwnerOrganizationName == null) {
            preference.setSummary(R.string.enterprise_privacy_settings_summary_generic);
        } else {
            preference.setSummary(this.mContext.getResources().getString(R.string.enterprise_privacy_settings_summary_with_name, deviceOwnerOrganizationName));
        }
    }
}
