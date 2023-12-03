package com.android.settings.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.MiuiSettings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.lang.ref.WeakReference;

/* loaded from: classes2.dex */
public class WifiTetherUseWifi6Controller implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnResume, OnPause {
    private ConnectivityManager mCm;
    private Context mContext;
    private RestartWifiAp mRestartWifiAp;
    private CheckBoxPreference mTetherUseWifi6;
    private WifiManager mWifiManager;

    /* loaded from: classes2.dex */
    class RestartWifiAp extends AsyncTask<Void, Integer, Integer> {
        private WeakReference<Context> mContext;

        public RestartWifiAp(Context context) {
            this.mContext = new WeakReference<>(context);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Code restructure failed: missing block: B:23:0x005a, code lost:
        
            return -3;
         */
        @Override // android.os.AsyncTask
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.Integer doInBackground(java.lang.Void... r6) {
            /*
                r5 = this;
                java.lang.ref.WeakReference<android.content.Context> r6 = r5.mContext
                java.lang.Object r6 = r6.get()
                android.content.Context r6 = (android.content.Context) r6
                r0 = 0
                if (r6 != 0) goto L10
                java.lang.Integer r5 = java.lang.Integer.valueOf(r0)
                return r5
            L10:
                java.lang.String r1 = "wifi"
                java.lang.Object r1 = r6.getSystemService(r1)
                android.net.wifi.WifiManager r1 = (android.net.wifi.WifiManager) r1
                java.lang.String r2 = "connectivity"
                java.lang.Object r6 = r6.getSystemService(r2)
                android.net.ConnectivityManager r6 = (android.net.ConnectivityManager) r6
                r2 = r0
            L22:
                r3 = 10
                if (r2 >= r3) goto L55
                boolean r3 = r5.isCancelled()
                if (r3 == 0) goto L32
                r5 = -1
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                return r5
            L32:
                int r3 = r1.getWifiApState()
                r4 = 11
                if (r3 == r4) goto L4c
                r3 = 500(0x1f4, double:2.47E-321)
                java.lang.Thread.sleep(r3)     // Catch: java.lang.InterruptedException -> L42
                int r2 = r2 + 1
                goto L22
            L42:
                r5 = move-exception
                r5.printStackTrace()
                r5 = -2
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                return r5
            L4c:
                com.android.settings.wifi.WifiTetherUseWifi6Controller$RestartWifiAp$1 r1 = new com.android.settings.wifi.WifiTetherUseWifi6Controller$RestartWifiAp$1
                r1.<init>()
                r5 = 1
                r6.startTethering(r0, r5, r1)
            L55:
                r5 = -3
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiTetherUseWifi6Controller.RestartWifiAp.doInBackground(java.lang.Void[]):java.lang.Integer");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Integer num) {
            if (WifiTetherUseWifi6Controller.this.mTetherUseWifi6 != null) {
                WifiTetherUseWifi6Controller.this.mTetherUseWifi6.setEnabled(true);
            }
        }
    }

    public WifiTetherUseWifi6Controller(Context context, Lifecycle lifecycle, Preference preference) {
        this.mContext = context;
        this.mTetherUseWifi6 = (CheckBoxPreference) preference;
        this.mCm = (ConnectivityManager) context.getSystemService("connectivity");
        this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        CheckBoxPreference checkBoxPreference = this.mTetherUseWifi6;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(null);
        }
        RestartWifiAp restartWifiAp = this.mRestartWifiAp;
        if (restartWifiAp != null) {
            restartWifiAp.cancel(true);
            this.mRestartWifiAp = null;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        MiuiSettings.System.putBooleanForUser(this.mContext.getContentResolver(), "hotspot_80211ax_support", ((Boolean) obj).booleanValue(), -2);
        if (this.mTetherUseWifi6 == null || this.mCm == null || this.mWifiManager.getWifiApState() != 13) {
            return true;
        }
        this.mTetherUseWifi6.setEnabled(false);
        this.mCm.stopTethering(0);
        RestartWifiAp restartWifiAp = new RestartWifiAp(this.mContext);
        this.mRestartWifiAp = restartWifiAp;
        restartWifiAp.execute(new Void[0]);
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (this.mTetherUseWifi6 != null) {
            updateState();
            this.mTetherUseWifi6.setOnPreferenceChangeListener(this);
        }
    }

    public void updateState() {
        boolean booleanForUser = MiuiSettings.System.getBooleanForUser(this.mContext.getContentResolver(), "hotspot_80211ax_support", false, -2);
        CheckBoxPreference checkBoxPreference = this.mTetherUseWifi6;
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(true);
            this.mTetherUseWifi6.setChecked(booleanForUser);
        }
    }
}
