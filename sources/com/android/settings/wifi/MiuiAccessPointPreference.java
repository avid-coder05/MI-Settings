package com.android.settings.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.R$string;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPointPreference;
import com.android.settingslib.wifi.SlaveWifiUtils;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import miuix.preference.ConnectPreferenceHelper;

/* loaded from: classes2.dex */
public class MiuiAccessPointPreference extends AccessPointPreference {
    private static final byte[] VENDOR_SPECIFIC_INFO_IOS = {0, 23, -14, 6, 1, 1, 3, 1};
    static final int[] WIFI_6_PIE = {R.drawable.ic_wifi_6_signal_0, R.drawable.ic_wifi_6_signal_1, R.drawable.ic_wifi_6_signal_2, R.drawable.ic_wifi_6_signal_3, R.drawable.ic_wifi_6_signal_4};
    private static Comparator<AccessPointPreference> sSuperComparator = new Comparator<AccessPointPreference>() { // from class: com.android.settings.wifi.MiuiAccessPointPreference.1
        @Override // java.util.Comparator
        public int compare(AccessPointPreference accessPointPreference, AccessPointPreference accessPointPreference2) {
            if (accessPointPreference instanceof AccessPointPreference) {
                if (accessPointPreference2 instanceof AccessPointPreference) {
                    String ssidStr = accessPointPreference.getAccessPoint().getSsidStr();
                    String ssidStr2 = accessPointPreference2.getAccessPoint().getSsidStr();
                    int rssi = accessPointPreference2.getAccessPoint().getRssi() - accessPointPreference.getAccessPoint().getRssi();
                    return rssi == 0 ? ssidStr.compareTo(ssidStr2) : rssi;
                }
                return -1;
            }
            return 1;
        }
    };
    private View.OnClickListener mArrowClickListener;
    private Context mContext;
    private boolean mForSlaveWifi;
    private boolean mHasDetail;
    private ConnectPreferenceHelper mHelper;
    private boolean mIsFreeWifi;
    private boolean mIsInProvision;
    private boolean mShowArrow;

    public MiuiAccessPointPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHasDetail = true;
        this.mShowArrow = true;
    }

    private boolean isMeteredHint(Set<ScanResult> set) {
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
                                        return true;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void startSignalConnectedAnimation() {
        Drawable icon = getIcon();
        if (icon == null) {
            return;
        }
        if (isMeteredHint(getAccessPoint().getScanResults())) {
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

    private void updateConnectAnimation() {
        NetworkInfo.DetailedState detailedState = getAccessPoint().getDetailedState();
        NetworkInfo.DetailedState slaveDetailedState = getAccessPoint().getSlaveDetailedState();
        int connectState = this.mHelper.getConnectState();
        NetworkInfo.DetailedState detailedState2 = NetworkInfo.DetailedState.CONNECTED;
        if (detailedState == detailedState2 || slaveDetailedState == detailedState2) {
            if ((isConnected() || isSlaveConnected() || getAccessPoint().isEphemeral()) && connectState != 1) {
                this.mHelper.setConnectState(1);
                startSignalConnectedAnimation();
            }
        } else if ((detailedState == NetworkInfo.DetailedState.AUTHENTICATING || detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR || detailedState == NetworkInfo.DetailedState.VERIFYING_POOR_LINK || detailedState == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK || detailedState == NetworkInfo.DetailedState.CONNECTING || slaveDetailedState == NetworkInfo.DetailedState.AUTHENTICATING || slaveDetailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR || slaveDetailedState == NetworkInfo.DetailedState.VERIFYING_POOR_LINK || slaveDetailedState == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK || slaveDetailedState == NetworkInfo.DetailedState.CONNECTING) && connectState != 2) {
            this.mHelper.setConnectState(2);
        } else if ((((detailedState == null || detailedState == NetworkInfo.DetailedState.IDLE || detailedState == NetworkInfo.DetailedState.DISCONNECTED || detailedState == NetworkInfo.DetailedState.BLOCKED) && slaveDetailedState == null) || ((slaveDetailedState == null || slaveDetailedState == NetworkInfo.DetailedState.IDLE || slaveDetailedState == NetworkInfo.DetailedState.DISCONNECTED || slaveDetailedState == NetworkInfo.DetailedState.BLOCKED) && detailedState == null)) && connectState != 0) {
            this.mHelper.setConnectState(0);
        }
    }

    private void updateSignalLevel() {
        Drawable icon = getIcon();
        int level = getAccessPoint().getLevel();
        if (icon instanceof LayerDrawable) {
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

    public boolean isXiaomiRouter() {
        return XiaomiRouterUtils.isXiaomiRouter(getAccessPoint().getScanResults());
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        int i;
        HashSet disableWifiAutoConnectSsid;
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        if (this.mIsInProvision) {
            view.setBackgroundResource(R.drawable.provision_list_item_background);
        }
        if (this.mHelper == null) {
            this.mHelper = new ConnectPreferenceHelper(getContext(), this);
        }
        ConnectPreferenceHelper connectPreferenceHelper = this.mHelper;
        int i2 = R.id.l_highlight;
        connectPreferenceHelper.onBindViewHolder(preferenceViewHolder, view.findViewById(i2));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        if (this.mIsInProvision) {
            if (getAccessPoint().isSaved()) {
                updateConnectAnimation();
            } else {
                this.mHelper.setConnectState(0);
            }
        } else if (getAccessPoint().isSaved() || getAccessPoint().isEphemeral()) {
            layoutParams.setMargins(this.mContext.getResources().getDimensionPixelOffset(R.dimen.highlight_side_left_margin), this.mContext.getResources().getDimensionPixelOffset(R.dimen.highlight_top_margin), this.mContext.getResources().getDimensionPixelOffset(R.dimen.highlight_side_right_margin), 0);
            view.findViewById(R.id.cardview).setLayoutParams(layoutParams);
            updateConnectAnimation();
        } else {
            this.mHelper.setConnectState(0);
            layoutParams.setMargins(this.mContext.getResources().getDimensionPixelOffset(R.dimen.highlight_side_left_margin), 0, this.mContext.getResources().getDimensionPixelOffset(R.dimen.highlight_side_right_margin), 0);
            view.findViewById(R.id.cardview).setLayoutParams(layoutParams);
            view.findViewById(i2).setBackground(null);
            view.setBackgroundResource(R.drawable.list_item_background);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.preference_detail);
        imageView.setContentDescription(imageView.getResources().getString(R.string.network_detail, this.mAccessPoint.getTitle()));
        imageView.setEnabled(this.mHasDetail);
        imageView.setOnClickListener(this.mHasDetail ? this.mArrowClickListener : null);
        imageView.setVisibility(this.mShowArrow ? 0 : 8);
        if ((isSlaveConnected() && !this.mForSlaveWifi) || (isConnected() && this.mForSlaveWifi)) {
            imageView.setVisibility(8);
        }
        TextView textView = (TextView) view.findViewById(16908304);
        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(16908310);
        if (this.mIsFreeWifi) {
            i = R.drawable.free_wifi_indicator;
        } else if (isXiaomiRouter()) {
            i = XiaomiRouterUtils.getIndictorDrawableId(getAccessPoint().getScanResults());
            if (isConnected() || isSlaveConnected()) {
                i = R.drawable.xiaomi_wifi_indicator_connected;
            }
        } else {
            i = 0;
        }
        checkedTextView.setCompoundDrawablePadding(checkedTextView.getResources().getDimensionPixelOffset(R.dimen.wifi_title_compound_padding));
        checkedTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, i, 0);
        ImageView imageView2 = (ImageView) view.findViewById(R.id.wifi_band);
        if (i == 0) {
            imageView2.setPadding(0, 0, 0, 0);
        }
        boolean z = false;
        boolean z2 = false;
        for (ScanResult scanResult : getAccessPoint().getScanResults()) {
            if (com.android.settingslib.wifi.WifiUtils.is24GHz(scanResult)) {
                z = true;
            } else if (com.android.settingslib.wifi.WifiUtils.is5GHz(scanResult)) {
                z2 = true;
            }
        }
        float f = this.mContext.getResources().getDisplayMetrics().density;
        Drawable drawable = this.mContext.getResources().getDrawable(R.drawable.band_wifi_5g);
        imageView2.setVisibility(0);
        if (z && z2) {
            Resources resources = this.mContext.getResources();
            int i3 = R.drawable.band_wifi_24g;
            imageView2.setImageDrawable(resources.getDrawable(i3));
            if (i == 0) {
                imageView2.setVisibility(8);
                checkedTextView.setCompoundDrawablePadding(checkedTextView.getResources().getDimensionPixelOffset(R.dimen.wifi_title_compound_padding));
                checkedTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, i3, 0);
            }
        } else if (z2) {
            imageView2.setImageDrawable(drawable);
        } else if (!z2) {
            imageView2.setVisibility(8);
        }
        if (z && z2) {
            if (i != 0) {
                int intrinsicWidth = this.mContext.getResources().getDrawable(R.drawable.band_wifi_24g).getIntrinsicWidth() + ((int) ((f * 5.0f) + 0.5f));
                boolean z3 = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
                int i4 = z3 ? intrinsicWidth : 0;
                if (z3) {
                    intrinsicWidth = 0;
                }
                checkedTextView.setPadding(i4, 0, intrinsicWidth, 0);
            }
        } else if (z2) {
            int intrinsicWidth2 = drawable.getIntrinsicWidth() + ((int) ((f * 5.0f) + 0.5f));
            boolean z4 = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
            int i5 = z4 ? intrinsicWidth2 : 0;
            if (z4) {
                intrinsicWidth2 = 0;
            }
            checkedTextView.setPadding(i5, 0, intrinsicWidth2, 0);
        }
        if (this.mIsInProvision) {
            getContext().getResources().getDimensionPixelOffset(R.dimen.provision_list_left_padding);
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingLeft(), view.getPaddingBottom());
        } else {
            getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_preference_item_icon_margin_end);
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }
        ImageView imageView3 = (ImageView) view.findViewById(16908294);
        int level = getAccessPoint().getLevel();
        if (level > 0) {
            int[] iArr = AccessPointPreference.WIFI_CONNECTION_STRENGTH;
            if (level <= iArr.length) {
                imageView3.setContentDescription(getContext().getString(iArr[level - 1]));
            }
        }
        ImageView imageView4 = (ImageView) view.findViewById(R.id.encryption);
        imageView4.setVisibility(getAccessPoint().getSecurity() != 0 ? 0 : 4);
        if ((isSlaveConnected() && !this.mForSlaveWifi) || (isConnected() && this.mForSlaveWifi)) {
            imageView4.setVisibility(4);
        }
        if (isConnected() || isSlaveConnected()) {
            if (z && z2) {
                Resources resources2 = this.mContext.getResources();
                int i6 = R.drawable.band_wifi_24g_connected;
                imageView2.setImageDrawable(resources2.getDrawable(i6));
                if (i == 0) {
                    imageView2.setVisibility(8);
                    checkedTextView.setCompoundDrawablePadding(checkedTextView.getResources().getDimensionPixelOffset(R.dimen.wifi_title_compound_padding));
                    checkedTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, i6, 0);
                }
            } else if (z2) {
                imageView2.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.band_wifi_5g_connected));
            }
            imageView4.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_wifi_encryption_connected));
        } else {
            imageView4.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_wifi_encryption));
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        TextView textView2 = (TextView) view.findViewById(16908304);
        getAccessPoint();
        boolean isVerboseLoggingEnabled = AccessPoint.isVerboseLoggingEnabled();
        if (isConnected() && getAccessPoint().getDetailedState() == NetworkInfo.DetailedState.CONNECTED && !this.mIsInProvision && !this.mForSlaveWifi) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(wifiManager.getCurrentNetwork());
            if (getAccessPoint().getSecurity() == 2 || getAccessPoint().getSecurity() == 5) {
                String summary = this.mAccessPoint.getSummary();
                Context context = this.mContext;
                int i7 = R$string.wifi_connected_no_internet;
                if (!TextUtils.equals(summary, context.getString(i7))) {
                    if (networkCapabilities != null && networkCapabilities.hasCapability(17)) {
                        textView2.setText(isVerboseLoggingEnabled ? getAccessPointSummary() : this.mContext.getString(R.string.wifi_click_login_wlan));
                    } else if (networkCapabilities == null || networkCapabilities.hasCapability(16)) {
                        textView2.setText((isVerboseLoggingEnabled || com.android.settingslib.wifi.WifiUtils.isInMishow(this.mContext)) ? getAccessPointSummary() : this.mContext.getString(R.string.wifi_click_share_wlan));
                    } else {
                        textView2.setText(this.mContext.getString(i7));
                    }
                }
            }
            if (networkCapabilities != null && networkCapabilities.hasCapability(17)) {
                textView2.setText(isVerboseLoggingEnabled ? getAccessPointSummary() : this.mContext.getString(R.string.wifi_click_login_wlan));
            }
        } else if (isSlaveConnected() && getAccessPoint().getSlaveDetailedState() == NetworkInfo.DetailedState.CONNECTED && !this.mIsInProvision) {
            if (this.mForSlaveWifi) {
                NetworkCapabilities networkCapabilities2 = connectivityManager.getNetworkCapabilities(new SlaveWifiUtils(this.mContext).getSlaveWifiCurrentNetwork());
                if (getAccessPoint().getSecurity() != 2 || TextUtils.equals(this.mAccessPoint.getSlaveSettingsSummary(false), this.mContext.getString(R$string.wifi_connected_no_internet))) {
                    if (networkCapabilities2 != null && networkCapabilities2.hasCapability(17)) {
                        textView2.setText(isVerboseLoggingEnabled ? getAccessPointSummary() : this.mContext.getString(R.string.wifi_click_login_wlan));
                    }
                } else if (networkCapabilities2 == null || !networkCapabilities2.hasCapability(17)) {
                    textView2.setText((isVerboseLoggingEnabled || com.android.settingslib.wifi.WifiUtils.isInMishow(this.mContext)) ? getAccessPointSummary() : this.mContext.getString(R.string.wifi_click_share_wlan));
                } else {
                    textView2.setText(isVerboseLoggingEnabled ? getAccessPointSummary() : this.mContext.getString(R.string.wifi_click_login_wlan));
                }
            } else {
                textView2.setText(isVerboseLoggingEnabled ? this.mContext.getString(R.string.dual_wifi_acceleration) + ", " + getAccessPointSummary() : this.mContext.getString(R.string.dual_wifi_acceleration));
            }
        }
        if (((this.mForSlaveWifi || isSlaveConnected() || !TextUtils.equals(this.mAccessPoint.getSummary(), this.mContext.getString(R$string.wifi_remembered))) && !(this.mForSlaveWifi && TextUtils.equals(this.mAccessPoint.getSlaveSettingsSummary(false), this.mContext.getString(R$string.wifi_remembered)))) || (disableWifiAutoConnectSsid = MiuiSettings.System.getDisableWifiAutoConnectSsid(this.mContext)) == null || this.mAccessPoint.getConfig() == null || !disableWifiAutoConnectSsid.contains(this.mAccessPoint.getConfig().SSID)) {
            return;
        }
        textView2.setText(this.mContext.getString(R.string.wifi_remembered_disabled_auto_connect));
    }

    @Override // com.android.settingslib.wifi.AccessPointPreference
    protected void updateIcon(int i, int i2, boolean z, Context context) {
        Drawable mutate = context.getDrawable(R.drawable.wifi_signal).mutate();
        if (isMeteredHint(getAccessPoint().getScanResults())) {
            mutate = context.getDrawable(R.drawable.wifi_metered).mutate();
        } else if (this.mWifiStandard == 6) {
            mutate = context.getDrawable(R.drawable.wifi6_signal).mutate();
        }
        if (mutate != null) {
            setIcon(mutate);
        }
        updateSignalLevel();
    }
}
