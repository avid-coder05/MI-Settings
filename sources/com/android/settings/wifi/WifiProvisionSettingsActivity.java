package com.android.settings.wifi;

import android.os.Bundle;
import com.android.settings.R;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.HashMap;
import miuix.preference.PreferenceFragment;
import miuix.provision.OobeUtil;
import miuix.provision.ProvisionBaseActivity;

/* loaded from: classes2.dex */
public class WifiProvisionSettingsActivity extends ProvisionBaseActivity {
    private static float HALF_ALPHA = 0.5f;
    private static float NO_ALPHA = 1.0f;
    private PreferenceFragment mMiuiWifiSettingsInstance;

    @Override // miuix.provision.ProvisionBaseActivity, miuix.provision.ProvisionAnimHelper.AnimListener
    public void onBackAnimStart() {
        super.onBackAnimStart();
        MiStatInterfaceUtils.trackPageEnd("provision_wifi_page");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.provision.ProvisionBaseActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mMiuiWifiSettingsInstance == null) {
            this.mMiuiWifiSettingsInstance = new MiuiWifiSettings();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.provision_container, this.mMiuiWifiSettingsInstance).commit();
        getSupportFragmentManager().executePendingTransactions();
        setPreviewView(getDrawable(R.drawable.provision_wifi));
        setTitle(R.string.connect_to_internet);
    }

    @Override // miuix.provision.ProvisionBaseActivity, miuix.provision.ProvisionAnimHelper.AnimListener
    public void onNextAminStart() {
        super.onNextAminStart();
        MiStatInterfaceUtils.trackPageEnd("provision_wifi_page");
        MiuiWifiSettings.mIsDisableBack = false;
        setResult(-1);
        finish();
    }

    @Override // miuix.provision.ProvisionBaseActivity
    protected void onNextButtonClick() {
        MiStatInterfaceUtils.trackEvent("provision_wifi_next");
        HashMap hashMap = new HashMap();
        hashMap.put("provision_wifi_state", "next");
        OneTrackInterfaceUtils.track("provision_wifi_state", hashMap);
    }

    @Override // miuix.provision.ProvisionBaseActivity
    protected void onSkipButtonClick() {
        MiStatInterfaceUtils.trackPageEnd("provision_wifi_page");
        MiStatInterfaceUtils.trackEvent("provision_wifi_skip");
        HashMap hashMap = new HashMap();
        hashMap.put("provision_wifi_state", "skip");
        OneTrackInterfaceUtils.track("provision_wifi_state", hashMap);
        setResult(-1);
        finish();
    }

    @Override // miuix.provision.ProvisionBaseActivity
    public void updateButtonState(boolean z) {
        if (!OobeUtil.needFastAnimation()) {
            super.updateButtonState(z);
            return;
        }
        this.mBackBtn.setAlpha(z ? NO_ALPHA : HALF_ALPHA);
        this.mBackBtn.setEnabled(z);
    }
}
