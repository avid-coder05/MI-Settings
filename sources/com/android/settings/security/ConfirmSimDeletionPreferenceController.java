package com.android.settings.security;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

/* loaded from: classes2.dex */
public class ConfirmSimDeletionPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public static final String KEY_CONFIRM_SIM_DELETION = "confirm_sim_deletion";
    private boolean mConfirmationDefaultOn;
    private MetricsFeatureProvider mMetricsFeatureProvider;

    public ConfirmSimDeletionPreferenceController(Context context, String str) {
        super(context, str);
        this.mConfirmationDefaultOn = context.getResources().getBoolean(R.bool.config_sim_deletion_confirmation_default_on);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    private boolean getGlobalState() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), KEY_CONFIRM_SIM_DELETION, this.mConfirmationDefaultOn ? 1 : 0) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreferenceChange$0(Preference preference) {
        this.mMetricsFeatureProvider.action(this.mContext, 1739, new Pair[0]);
        setChecked(false);
        ((TwoStatePreference) preference).setChecked(false);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return MobileNetworkUtils.showEuiccSettings(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public boolean isChecked() {
        return getGlobalState();
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
    public boolean onPreferenceChange(final Preference preference, Object obj) {
        if (preference.getKey().equals(getPreferenceKey())) {
            if (isChecked()) {
                WifiDppUtils.showLockScreen(this.mContext, new Runnable() { // from class: com.android.settings.security.ConfirmSimDeletionPreferenceController$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ConfirmSimDeletionPreferenceController.this.lambda$onPreferenceChange$0(preference);
                    }
                });
                return false;
            }
            this.mMetricsFeatureProvider.action(this.mContext, 1738, new Pair[0]);
            setChecked(true);
            return true;
        }
        return false;
    }

    public boolean setChecked(boolean z) {
        Settings.Global.putInt(this.mContext.getContentResolver(), KEY_CONFIRM_SIM_DELETION, z ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (((KeyguardManager) this.mContext.getSystemService(KeyguardManager.class)).isKeyguardSecure()) {
            preference.setEnabled(true);
            if (preference instanceof TwoStatePreference) {
                ((TwoStatePreference) preference).setChecked(getGlobalState());
            }
            preference.setSummary(R.string.confirm_sim_deletion_description);
            return;
        }
        preference.setEnabled(false);
        if (preference instanceof TwoStatePreference) {
            ((TwoStatePreference) preference).setChecked(false);
        }
        preference.setSummary(R.string.disabled_because_no_backup_security);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
