package com.android.settings.development;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import miui.telephony.TelephonyManager;

/* loaded from: classes.dex */
public class FiveGNrcaConfigFragment extends DashboardFragment {
    private final BroadcastReceiver mNrcaReceiver = new BroadcastReceiver() { // from class: com.android.settings.development.FiveGNrcaConfigFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.SIM_STATE_CHANGED")) {
                String stringExtra = intent.getStringExtra("ss");
                Log.d("FiveGNrcaConfigFragment", "ACTION_SIM_STATE_CHANGED, SIM State:" + String.valueOf(stringExtra));
                if (!stringExtra.equals("ABSENT") || TelephonyManager.getDefault().hasIccCard()) {
                    return;
                }
                FiveGNrcaConfigFragment.this.finish();
            }
        }
    };

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        getPrefContext().registerReceiver(this.mNrcaReceiver, intentFilter);
        Log.d("FiveGNrcaConfigFragment", "register broadcastreceiver");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "FiveGNrcaConfigFragment";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.fiveg_nrca_setting;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getPrefContext().unregisterReceiver(this.mNrcaReceiver);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateState();
        registerBroadcastReceiver();
    }

    public void updateState() {
        boolean z = Settings.System.getInt(getPrefContext().getContentResolver(), "airplane_mode_on", -1) == 1;
        boolean hasIccCard = TelephonyManager.getDefault().hasIccCard();
        if (z || !hasIccCard) {
            finish();
        }
    }
}
