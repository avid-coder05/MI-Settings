package com.android.settings.wifi;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.R$string;
import com.android.settingslib.wifi.SlaveWifiUtils;
import com.android.settingslib.wifi.WifiEntryPreference;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.StandardWifiEntry;
import com.android.wifitrackerlib.Utils;
import com.android.wifitrackerlib.WifiEntry;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import miui.content.ExtraIntent;
import miui.os.Build;
import miuix.preference.ConnectPreferenceHelper;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes2.dex */
public class MiuiWifiEntryPreference extends WifiEntryPreference {
    private View.OnClickListener mArrowClickListener;
    private int mBatteryLevel;
    private Context mContext;
    private boolean mForSlaveWifi;
    private boolean mHasDetail;
    private ConnectPreferenceHelper mHelper;
    private boolean mIsFreeWifi;
    private boolean mIsInProvision;
    private boolean mIsMeteredHint;
    private int mRssiForCompare;
    private boolean mShowArrow;
    private View mView;
    private WifiEntry mWifiEntry;
    private static final byte[] VENDOR_SPECIFIC_INFO_IOS = {0, 23, -14, 6, 1, 1, 3, 1};
    static final int[] WIFI_6_PIE = {R.drawable.ic_wifi_6_signal_0, R.drawable.ic_wifi_6_signal_1, R.drawable.ic_wifi_6_signal_2, R.drawable.ic_wifi_6_signal_3, R.drawable.ic_wifi_6_signal_4};
    static final int[] BATTERY_LEVEL_CONNECTED = {R.drawable.ap_battery_10_connected, R.drawable.ap_battery_20_connected, R.drawable.ap_battery_30_connected, R.drawable.ap_battery_40_connected, R.drawable.ap_battery_50_connected, R.drawable.ap_battery_60_connected, R.drawable.ap_battery_70_connected, R.drawable.ap_battery_80_connected, R.drawable.ap_battery_90_connected, R.drawable.ap_battery_100_connected};
    private static Comparator<MiuiWifiEntryPreference> sSuperComparator = new Comparator<MiuiWifiEntryPreference>() { // from class: com.android.settings.wifi.MiuiWifiEntryPreference.1
        @Override // java.util.Comparator
        public int compare(MiuiWifiEntryPreference miuiWifiEntryPreference, MiuiWifiEntryPreference miuiWifiEntryPreference2) {
            if (miuiWifiEntryPreference instanceof MiuiWifiEntryPreference) {
                if (miuiWifiEntryPreference2 instanceof MiuiWifiEntryPreference) {
                    String ssid = miuiWifiEntryPreference.getWifiEntry().getSsid();
                    String ssid2 = miuiWifiEntryPreference2.getWifiEntry().getSsid();
                    int i = miuiWifiEntryPreference2.mRssiForCompare - miuiWifiEntryPreference.mRssiForCompare;
                    return i == 0 ? ssid.compareTo(ssid2) : i;
                }
                return -1;
            }
            return 1;
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class ArrowClickListener implements View.OnClickListener {
        private SettingsPreferenceFragment mSpf;
        private WifiEntry mWifiEntry;

        public ArrowClickListener(WifiEntry wifiEntry, SettingsPreferenceFragment settingsPreferenceFragment) {
            this.mWifiEntry = wifiEntry;
            this.mSpf = settingsPreferenceFragment;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString(":miui:starting_window_label", "");
            WifiEntry wifiEntry = this.mWifiEntry;
            bundle.putBoolean("is_salve_wifi", (wifiEntry == null || wifiEntry.getSlaveConnectedState() == 0) ? false : true);
            bundle.putString("ssid", this.mWifiEntry.getSsid());
            bundle.putString("title_name", this.mWifiEntry.getTitle());
            bundle.putString("key_chosen_wifientry_key", this.mWifiEntry.getKey());
            FragmentActivity activity = this.mSpf.getActivity();
            Intent intent = activity != null ? activity.getIntent() : null;
            if (intent != null && intent.getBooleanExtra(ExtraIntent.EXTRA_SHOW_ON_FINDDEVICE_KEYGUARD, false)) {
                bundle.putBoolean(ExtraIntent.EXTRA_SHOW_ON_FINDDEVICE_KEYGUARD, true);
            }
            HapticCompat.performHapticFeedback(view, HapticFeedbackConstants.MIUI_TAP_LIGHT);
            SettingsPreferenceFragment settingsPreferenceFragment = this.mSpf;
            settingsPreferenceFragment.startFragment(settingsPreferenceFragment, MiuiWifiDetailFragment.class.getName(), 200, bundle, 0);
        }
    }

    public MiuiWifiEntryPreference(Context context, WifiEntry wifiEntry) {
        super(context, wifiEntry);
        this.mHasDetail = true;
        this.mShowArrow = true;
        this.mBatteryLevel = -1;
        init(context);
    }

    public MiuiWifiEntryPreference(Context context, WifiEntry wifiEntry, boolean z) {
        super(context, wifiEntry, z);
        this.mHasDetail = true;
        this.mShowArrow = true;
        this.mBatteryLevel = -1;
        this.mForSlaveWifi = z;
        init(context);
    }

    private boolean deviceIsProvisioned(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
    }

    private int getBatteryLevel() {
        int i = this.mBatteryLevel;
        if (i != -1) {
            int i2 = i / 10;
            return i2 == 10 ? i2 - 1 : i2;
        }
        return 9;
    }

    public static Comparator<MiuiWifiEntryPreference> getSuperComparator() {
        return sSuperComparator;
    }

    private String getWifiEntrySummary(boolean z) {
        if (z && WifiEntryPreference.methodGetSummary != null && (this.mWifiEntry instanceof StandardWifiEntry)) {
            try {
                return (String) WifiEntryPreference.methodGetSummary.invoke((StandardWifiEntry) this.mWifiEntry, Boolean.FALSE, Boolean.valueOf(z));
            } catch (Exception e) {
                Log.e("MiuiWifiEntryPreference", "methodGetSummary catch:" + e);
            }
        }
        return getWifiEntry().getSummary(false);
    }

    private void init(Context context) {
        this.mContext = context;
        boolean z = !deviceIsProvisioned(context);
        this.mIsInProvision = z;
        if (z) {
            setLayoutResource(R.layout.provision_accesspoint_preference);
        } else {
            setLayoutResource(R.layout.accesspoint_preference);
        }
        setWidgetLayoutResource(R.layout.preference_widget_wifi_signal);
        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(getWifiEntry().getTargetScanResults());
        this.mRssiForCompare = bestScanResultByLevel != null ? bestScanResultByLevel.level : 0;
    }

    private boolean isPad() {
        return Build.IS_TABLET || Log.isLoggable("MiuiQuickHotspotTest", 2);
    }

    private boolean isPasswordCanShare() {
        return ((getWifiEntry().getSecurity() != 2 && getWifiEntry().getSecurity() != 5) || this.mWifiEntry.getSummary() == null || this.mWifiEntry.getSummary().contains(this.mContext.getString(R$string.wifitrackerlib_wifi_connected_cannot_provide_internet))) ? false : true;
    }

    private void setMasterWifiSummary(ConnectivityManager connectivityManager, WifiManager wifiManager, TextView textView, boolean z) {
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(wifiManager.getCurrentNetwork());
        boolean isVerboseLoggingEnabled = BaseWifiTracker.isVerboseLoggingEnabled();
        if (!isPasswordCanShare()) {
            if (networkCapabilities == null || !networkCapabilities.hasCapability(17)) {
                return;
            }
            textView.setText(isVerboseLoggingEnabled ? getWifiEntrySummary(z) : this.mContext.getString(R.string.wifi_click_login_wlan));
        } else if (networkCapabilities != null && networkCapabilities.hasCapability(17)) {
            textView.setText(isVerboseLoggingEnabled ? getWifiEntrySummary(z) : this.mContext.getString(R.string.wifi_click_login_wlan));
        } else if (networkCapabilities != null && networkCapabilities.hasCapability(24)) {
            textView.setText(isVerboseLoggingEnabled ? getWifiEntrySummary(z) : this.mContext.getResources().getStringArray(R.array.wifitrackerlib_wifi_status)[NetworkInfo.DetailedState.CONNECTED.ordinal()]);
        } else if (networkCapabilities == null || networkCapabilities.hasCapability(16)) {
            textView.setText((isVerboseLoggingEnabled || com.android.settingslib.wifi.WifiUtils.isInMishow(this.mContext)) ? getWifiEntrySummary(z) : this.mContext.getString(R.string.wifi_click_share_wlan));
        } else {
            textView.setText(isVerboseLoggingEnabled ? getWifiEntrySummary(z) : this.mContext.getString(R$string.wifitrackerlib_wifi_connected_cannot_provide_internet));
        }
    }

    private void setSlaveWifiSummary(ConnectivityManager connectivityManager, TextView textView, boolean z) {
        String string;
        boolean isVerboseLoggingEnabled = BaseWifiTracker.isVerboseLoggingEnabled();
        if (!this.mForSlaveWifi) {
            if (isVerboseLoggingEnabled) {
                string = this.mContext.getString(R.string.dual_wifi_acceleration) + ", " + getWifiEntrySummary(z);
            } else {
                string = this.mContext.getString(R.string.dual_wifi_acceleration);
            }
            textView.setText(string);
            return;
        }
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(SlaveWifiUtils.getInstance(this.mContext).getSlaveWifiCurrentNetwork());
        if (!isPasswordCanShare()) {
            if (networkCapabilities == null || !networkCapabilities.hasCapability(17)) {
                return;
            }
            textView.setText(isVerboseLoggingEnabled ? getWifiEntrySummary(z) : this.mContext.getString(R.string.wifi_click_login_wlan));
        } else if (networkCapabilities != null && networkCapabilities.hasCapability(17)) {
            textView.setText(isVerboseLoggingEnabled ? getWifiEntrySummary(z) : this.mContext.getString(R.string.wifi_click_login_wlan));
        } else if (networkCapabilities == null || networkCapabilities.hasCapability(16)) {
            textView.setText((isVerboseLoggingEnabled || com.android.settingslib.wifi.WifiUtils.isInMishow(this.mContext)) ? getWifiEntrySummary(z) : this.mContext.getString(R.string.wifi_click_share_wlan));
        } else {
            textView.setText(isVerboseLoggingEnabled ? getWifiEntrySummary(z) : this.mContext.getString(R$string.wifitrackerlib_wifi_connected_cannot_provide_internet));
        }
    }

    private void startSignalConnectedAnimation() {
        Drawable icon = getIcon();
        if (icon == null) {
            return;
        }
        if (isMeteredHint(getWifiEntry().getScanResults())) {
            if (icon instanceof AnimatedVectorDrawable) {
                ((AnimatedVectorDrawable) icon).start();
            }
        } else if (icon instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) icon;
            int numberOfLayers = layerDrawable.getNumberOfLayers();
            for (int i = 0; i < numberOfLayers; i++) {
                Drawable findDrawableByLayerId = layerDrawable.findDrawableByLayerId(layerDrawable.getId(i));
                if (findDrawableByLayerId instanceof AnimatedVectorDrawable) {
                    ((AnimatedVectorDrawable) findDrawableByLayerId).start();
                }
            }
        }
    }

    private void updateBatteryLevelInternal(int i) {
        if (this.mView != null && isMeteredHint() && isConnected()) {
            this.mBatteryLevel = i;
            ImageView imageView = (ImageView) this.mView.findViewById(R.id.encryption);
            float f = this.mContext.getResources().getDisplayMetrics().density;
            imageView.setLayoutParams(new LinearLayout.LayoutParams((int) (MiuiUtils.getDimenValue(this.mContext, R.dimen.ap_battery_image_width) * f), (int) (MiuiUtils.getDimenValue(this.mContext, R.dimen.ap_battery_image_high) * f)));
            imageView.setVisibility(0);
            imageView.setImageDrawable(this.mContext.getDrawable(BATTERY_LEVEL_CONNECTED[getBatteryLevel()]));
            TextView textView = (TextView) this.mView.findViewById(16908304);
            String string = this.mContext.getResources().getString(R.string.ap_connected_battery_level, com.android.settingslib.Utils.formatPercentage(this.mBatteryLevel));
            textView.setText(this.mContext.getResources().getStringArray(R.array.wifi_status)[5] + string);
        }
    }

    private void updateConnectAnimation() {
        NetworkInfo networkInfo = getWifiEntry().getNetworkInfo();
        NetworkInfo.DetailedState detailedState = networkInfo == null ? null : networkInfo.getDetailedState();
        NetworkInfo slaveNetworkInfo = getWifiEntry().getSlaveNetworkInfo();
        NetworkInfo.DetailedState detailedState2 = slaveNetworkInfo != null ? slaveNetworkInfo.getDetailedState() : null;
        int connectState = this.mHelper.getConnectState();
        Log.d("MiuiWifiEntryPreference", "updateConnectAnimation* state: " + detailedState + ", slaveState: " + detailedState2 + ", animationHelperState: " + connectState + ", hashCode: " + hashCode() + ", miwillEnabled: true");
        updateConnectAnimationWithMiwill(detailedState, detailedState2, connectState);
    }

    private void updateConnectAnimationWithMiwill(NetworkInfo.DetailedState detailedState, NetworkInfo.DetailedState detailedState2, int i) {
        boolean z;
        boolean z2;
        String key = getKey();
        if (this.mWifiEntry instanceof StandardWifiEntry) {
            z2 = key != null && key.startsWith("slave-");
            z = key != null && key.startsWith("master-");
        } else {
            z = false;
            z2 = false;
        }
        if ((z || z2) ? false : true) {
            Log.d("MiuiWifiEntryPreference", "updateConnectAnimationWithMiwill isNormalPref");
            updateConnectAnimationWithoutMiwill(detailedState, detailedState2, i);
            return;
        }
        NetworkInfo.DetailedState detailedState3 = NetworkInfo.DetailedState.CONNECTED;
        if ((detailedState == detailedState3 && z) || (detailedState2 == detailedState3 && z2)) {
            if (!((isConnected() && z) || ((isSlaveConnected() && z2) || getWifiEntry().isSaved())) || i == 1) {
                return;
            }
            this.mHelper.setConnectState(1);
            startSignalConnectedAnimation();
        } else if (((detailedState == NetworkInfo.DetailedState.AUTHENTICATING && z) || ((detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR && z) || ((detailedState == NetworkInfo.DetailedState.VERIFYING_POOR_LINK && z) || ((detailedState == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK && z) || ((detailedState == NetworkInfo.DetailedState.CONNECTING && z) || ((detailedState2 == NetworkInfo.DetailedState.AUTHENTICATING && z2) || ((detailedState2 == NetworkInfo.DetailedState.OBTAINING_IPADDR && z2) || ((detailedState2 == NetworkInfo.DetailedState.VERIFYING_POOR_LINK && z2) || ((detailedState2 == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK && z2) || (detailedState2 == NetworkInfo.DetailedState.CONNECTING && z2)))))))))) && i != 2) {
            this.mHelper.setConnectState(2);
        } else if (((detailedState == null && z) || ((detailedState == NetworkInfo.DetailedState.IDLE && z) || ((detailedState == NetworkInfo.DetailedState.DISCONNECTED && z) || ((detailedState == NetworkInfo.DetailedState.BLOCKED && z) || ((detailedState2 == null && z2) || ((detailedState2 == NetworkInfo.DetailedState.IDLE && z2) || ((detailedState2 == NetworkInfo.DetailedState.DISCONNECTING && z2) || ((detailedState2 == NetworkInfo.DetailedState.DISCONNECTED && z2) || (detailedState2 == NetworkInfo.DetailedState.BLOCKED && z2))))))))) && i != 0) {
            this.mHelper.setConnectState(0);
        } else if (detailedState == null && detailedState2 == null) {
            this.mHelper.setConnectState(0);
        } else {
            Log.w("MiuiWifiEntryPreference", "warning:updateConnectAnimationWithMiwill " + hashCode() + " unexpected state");
        }
    }

    private void updateConnectAnimationWithoutMiwill(NetworkInfo.DetailedState detailedState, NetworkInfo.DetailedState detailedState2, int i) {
        NetworkInfo.DetailedState detailedState3 = NetworkInfo.DetailedState.CONNECTED;
        if (detailedState == detailedState3 || detailedState2 == detailedState3) {
            if ((isConnected() || isSlaveConnected() || getWifiEntry().isSaved()) && i != 1) {
                this.mHelper.setConnectState(1);
                startSignalConnectedAnimation();
            }
        } else if ((detailedState == NetworkInfo.DetailedState.AUTHENTICATING || detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR || detailedState == NetworkInfo.DetailedState.VERIFYING_POOR_LINK || detailedState == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK || detailedState == NetworkInfo.DetailedState.CONNECTING || detailedState2 == NetworkInfo.DetailedState.AUTHENTICATING || detailedState2 == NetworkInfo.DetailedState.OBTAINING_IPADDR || detailedState2 == NetworkInfo.DetailedState.VERIFYING_POOR_LINK || detailedState2 == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK || detailedState2 == NetworkInfo.DetailedState.CONNECTING) && i != 2) {
            this.mHelper.setConnectState(2);
        } else if ((((detailedState == null || detailedState == NetworkInfo.DetailedState.IDLE || detailedState == NetworkInfo.DetailedState.DISCONNECTED || detailedState == NetworkInfo.DetailedState.BLOCKED) && detailedState2 == null) || ((detailedState2 == null || detailedState2 == NetworkInfo.DetailedState.IDLE || detailedState2 == NetworkInfo.DetailedState.DISCONNECTING || detailedState2 == NetworkInfo.DetailedState.DISCONNECTED || detailedState2 == NetworkInfo.DetailedState.BLOCKED) && detailedState == null)) && i != 0) {
            this.mHelper.setConnectState(0);
        } else if (detailedState == null && detailedState2 == null) {
            this.mHelper.setConnectState(0);
        }
    }

    private void updateSignalLevel() {
        Drawable icon = getIcon();
        int level = getWifiEntry().getLevel();
        if (level != -1 && (icon instanceof LayerDrawable)) {
            LayerDrawable layerDrawable = (LayerDrawable) icon;
            int numberOfLayers = layerDrawable.getNumberOfLayers() - 1;
            for (int i = numberOfLayers; i >= 0; i--) {
                layerDrawable.findDrawableByLayerId(layerDrawable.getId(i)).setAlpha(255);
            }
            while (numberOfLayers >= level) {
                layerDrawable.findDrawableByLayerId(layerDrawable.getId(numberOfLayers)).setAlpha(63);
                numberOfLayers--;
            }
        }
    }

    public boolean isMeteredHint() {
        return this.mIsMeteredHint;
    }

    @Override // com.android.settingslib.wifi.WifiEntryPreference
    protected boolean isMeteredHint(Set<ScanResult> set) {
        if (set != null) {
            Iterator<ScanResult> it = set.iterator();
            while (it.hasNext()) {
                ScanResult.InformationElement[] informationElementArr = (ScanResult.InformationElement[]) it.next().getInformationElements().toArray();
                if (informationElementArr != null) {
                    for (int i = 0; i < informationElementArr.length; i++) {
                        if (informationElementArr[i].getId() == 221) {
                            byte[] bArr = VENDOR_SPECIFIC_INFO_IOS;
                            byte[] bArr2 = new byte[bArr.length];
                            try {
                                if (bArr.length <= informationElementArr[i].getBytes().remaining()) {
                                    informationElementArr[i].getBytes().get(bArr2, 0, bArr.length);
                                    if (Arrays.equals(bArr2, bArr)) {
                                        this.mIsMeteredHint = true;
                                        return true;
                                    }
                                    continue;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        this.mIsMeteredHint = false;
        return false;
    }

    public boolean isXiaomiRouter() {
        return XiaomiRouterUtils.isXiaomiRouter(getWifiEntry().getScanResults());
    }

    /* JADX WARN: Removed duplicated region for block: B:161:0x0364  */
    /* JADX WARN: Removed duplicated region for block: B:162:0x0366  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x0387  */
    /* JADX WARN: Removed duplicated region for block: B:179:0x039c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:187:0x03cb  */
    /* JADX WARN: Removed duplicated region for block: B:191:0x03eb  */
    /* JADX WARN: Removed duplicated region for block: B:201:0x0402  */
    /* JADX WARN: Removed duplicated region for block: B:204:0x042b  */
    /* JADX WARN: Removed duplicated region for block: B:209:0x043b  */
    /* JADX WARN: Removed duplicated region for block: B:231:0x048a  */
    /* JADX WARN: Removed duplicated region for block: B:240:0x04bd  */
    /* JADX WARN: Removed duplicated region for block: B:254:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    @Override // com.android.settingslib.wifi.WifiEntryPreference, com.android.settingslib.miuisettings.preference.RadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onBindViewHolder(androidx.preference.PreferenceViewHolder r21) {
        /*
            Method dump skipped, instructions count: 1230
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.MiuiWifiEntryPreference.onBindViewHolder(androidx.preference.PreferenceViewHolder):void");
    }

    public void setArrowClickListener(View.OnClickListener onClickListener) {
        this.mArrowClickListener = onClickListener;
    }

    public void update(WifiEntry wifiEntry) {
        this.mWifiEntry = wifiEntry;
        refresh();
    }

    public void updateBatteryLevel(int i) {
        if (this.mBatteryLevel == i) {
            return;
        }
        updateBatteryLevelInternal(i);
    }

    @Override // com.android.settingslib.wifi.WifiEntryPreference
    protected void updateIcon(boolean z, int i, int i2, boolean z2) {
        Drawable mutate = getContext().getDrawable(R.drawable.wifi_signal).mutate();
        if (isMeteredHint(getWifiEntry().getScanResults())) {
            mutate = getContext().getDrawable(R.drawable.wifi_metered).mutate();
        } else if (this.mWifiStandard == 6) {
            mutate = getContext().getDrawable(R.drawable.wifi6_signal).mutate();
        }
        if (mutate != null) {
            setIcon(mutate);
        }
        updateSignalLevel();
    }
}
