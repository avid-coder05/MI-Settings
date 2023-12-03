package com.android.settings.enterprise;

import android.content.Context;
import com.android.settings.R;
import com.android.settingslib.enterprise.DeviceAdminStringProvider;
import java.util.Objects;

/* loaded from: classes.dex */
class DeviceAdminStringProviderImpl implements DeviceAdminStringProvider {
    private final Context mContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeviceAdminStringProviderImpl(Context context) {
        Objects.requireNonNull(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDefaultDisabledByPolicyContent() {
        return this.mContext.getString(R.string.default_admin_support_msg);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDefaultDisabledByPolicyTitle() {
        return this.mContext.getString(R.string.disabled_by_policy_title);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisableCameraTitle() {
        return this.mContext.getString(R.string.disabled_by_policy_title_camera);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisableScreenCaptureTitle() {
        return this.mContext.getString(R.string.disabled_by_policy_title_screen_capture);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisabledBiometricsParentConsentContent() {
        return this.mContext.getString(R.string.disabled_by_policy_content_biometric_parental_consent);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisabledBiometricsParentConsentTitle() {
        return this.mContext.getString(R.string.disabled_by_policy_title_biometric_parental_consent);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisabledByPolicyTitleForFinancedDevice() {
        return this.mContext.getString(R.string.disabled_by_policy_title_financed_device);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisallowAdjustVolumeTitle() {
        return this.mContext.getString(R.string.disabled_by_policy_title_adjust_volume);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisallowOutgoingCallsTitle() {
        return this.mContext.getString(R.string.disabled_by_policy_title_outgoing_calls);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getDisallowSmsTitle() {
        return this.mContext.getString(R.string.disabled_by_policy_title_sms);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getLearnMoreHelpPageUrl() {
        return this.mContext.getString(R.string.help_url_action_disabled_by_it_admin);
    }

    @Override // com.android.settingslib.enterprise.DeviceAdminStringProvider
    public String getSuspendPackagesTitle() {
        return this.mContext.getString(R.string.disabled_by_policy_title_suspend_packages);
    }
}
