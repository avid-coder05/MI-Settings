package com.android.settings.datausage;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.AttributeSet;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.datausage.TemplatePreference;
import com.android.settings.network.MobileDataEnabledListener;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class BillingCyclePreference extends Preference implements TemplatePreference, MobileDataEnabledListener.Client {
    private MobileDataEnabledListener mListener;
    private TemplatePreference.NetworkServices mServices;
    private int mSubId;
    private NetworkTemplate mTemplate;

    public BillingCyclePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mListener = new MobileDataEnabledListener(context, this);
    }

    private void updateEnabled() {
        try {
            setEnabled(this.mServices.mNetworkService.isBandwidthControlEnabled() && this.mServices.mTelephonyManager.createForSubscriptionId(this.mSubId).isDataEnabledForApn(17) && this.mServices.mUserManager.isAdminUser());
        } catch (RemoteException unused) {
            setEnabled(false);
        }
    }

    @Override // androidx.preference.Preference
    public Intent getIntent() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("network_template", this.mTemplate);
        return new SubSettingLauncher(getContext()).setDestination(BillingCycleSettings.class.getName()).setArguments(bundle).setTitleRes(R.string.billing_cycle).setSourceMetricsCategory(0).toIntent();
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mListener.start(this.mSubId);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onDetached() {
        this.mListener.stop();
        super.onDetached();
    }

    @Override // com.android.settings.network.MobileDataEnabledListener.Client
    public void onMobileDataEnabledChange() {
        updateEnabled();
    }

    @Override // com.android.settings.datausage.TemplatePreference
    public void setTemplate(NetworkTemplate networkTemplate, int i, TemplatePreference.NetworkServices networkServices) {
        this.mTemplate = networkTemplate;
        this.mSubId = i;
        this.mServices = networkServices;
        setSummary((CharSequence) null);
        setIntent(getIntent());
    }
}
