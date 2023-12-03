package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppData;
import android.net.wifi.WifiManager;
import android.os.SystemProperties;
import android.text.TextUtils;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.Settings;
import com.android.settings.recommend.PageIndexManager;
import com.android.settings.wifi.MiuiWifiAssistFeatureSupport;
import com.android.settings.wifi.NetworkCheckController;
import com.android.settings.wifi.WifiAssistantController;
import com.android.settings.wifi.WifiTrafficUtils;
import com.android.settings.wifi.linkturbo.LinkTurboClient;
import com.android.settings.wifi.linkturbo.LinkTurboUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.search.SettingsTree;
import com.android.settingslib.wifi.SlaveWifiUtils;
import java.util.Iterator;
import miui.os.Build;
import miui.provider.ExtraTelephony;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class WifiSettingsTree extends SettingsTree {
    public static final String WIFI_INSTALL_CREDENTIALS = "wifi_install_credentials";

    protected WifiSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public Intent getIntent() {
        String columnValue = getColumnValue("resource");
        if (!WIFI_INSTALL_CREDENTIALS.equals(columnValue)) {
            return "wifi_menu_p2p".equals(columnValue) ? new Intent(((SettingsTree) this).mContext, Settings.WifiP2pSettingsActivity.class) : ("dual_wifi".equals(columnValue) || "dual_wifi_auto_disable".equals(columnValue)) ? new Intent("android.settings.DUAL_WIFI.WIFI_SETTINGS") : super.getIntent();
        }
        Intent intent = new Intent("android.credentials.INSTALL_AS_USER");
        intent.setClassName("com.android.certinstaller", "com.android.certinstaller.CertInstallerMain");
        intent.putExtra("install_as_uid", PageIndexManager.PAGE_ACCESSIBILITY_PHYSICAL);
        return intent;
    }

    public Intent getIntentForStart() {
        return WIFI_INSTALL_CREDENTIALS.equals(getColumnValue("resource")) ? getIntent() : super.getIntentForStart();
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        WifiManager wifiManager = (WifiManager) ((SettingsTree) this).mContext.getApplicationContext().getSystemService("wifi");
        LocationManager locationManager = (LocationManager) ((SettingsTree) this).mContext.getApplicationContext().getSystemService("location");
        if ("wifi_enable_data_and_wifi_roam".equals(columnValue) && Utils.isWifiOnly(((SettingsTree) this).mContext)) {
            return 0;
        }
        if ("dual_wifi".equals(columnValue) || "dual_wifi_auto_disable".equals(columnValue)) {
            if (!SlaveWifiUtils.getInstance(((SettingsTree) this).mContext).isUiVisible(((SettingsTree) this).mContext)) {
                return 0;
            }
        } else if (TextUtils.equals("wifi_enhanced_handover", columnValue) && !((SettingsTree) this).mContext.getResources().getBoolean(R.bool.config_show_enhanced_handover_swith)) {
            return 0;
        } else {
            if ("smart_dual_sim_title".equals(columnValue)) {
                if (LinkTurboUtils.shouldHideSmartDualSimButton(((SettingsTree) this).mContext)) {
                    return 0;
                }
            } else if (!"wifi_setting_frequency_band_title".equals(columnValue)) {
                if ("wifi_priority_type_title".equals(columnValue) || "gsm_to_wifi_connect_type_title".equals(columnValue) || "select_ssid_type_title".equals(columnValue) || "wifi_dialog_remind_type_title".equals(columnValue) || "wifi_priority_settings_title".equals(columnValue)) {
                    if (!Build.IS_CM_CUSTOMIZATION) {
                        return 0;
                    }
                } else if ("wifi_verbose_logging".equals(columnValue)) {
                    return 0;
                } else {
                    boolean z = true;
                    if ("use_open_wifi_automatically_title".equals(columnValue)) {
                        NetworkScoreManager networkScoreManager = (NetworkScoreManager) ((SettingsTree) this).mContext.getSystemService("network_score");
                        boolean z2 = networkScoreManager.getActiveScorer() != null;
                        if (!z2) {
                            Iterator it = networkScoreManager.getAllValidScorers().iterator();
                            while (it.hasNext()) {
                                if (((NetworkScorerAppData) it.next()).getEnableUseOpenWifiActivity() != null) {
                                    break;
                                }
                            }
                        }
                        z = z2;
                        if (!z) {
                            return 0;
                        }
                    } else if ("wapi_cert_manage_title".equals(columnValue)) {
                        if (Build.IS_GLOBAL_BUILD) {
                            return 0;
                        }
                    } else if ("wifi_link_turbo".equals(columnValue) || "multi_network_acceleration_app_settings".equals(columnValue) || "wifi_multi_network_acceleration".equals(columnValue)) {
                        if (!LinkTurboClient.isLinkTurboSupported(((SettingsTree) this).mContext)) {
                            return 0;
                        }
                    } else if ("network_and_internet_preferences_title".equals(columnValue)) {
                        return 0;
                    } else {
                        if (("wifi_wakeup".equals(columnValue) && !RegionUtils.IS_JP_KDDI) || "wifi_saved_access_points_label".equals(columnValue) || "condition_airplane_title".equals(columnValue) || "resetting_internet_text".equals(columnValue)) {
                            return 0;
                        }
                        if ("wifi_assistant".equals(columnValue)) {
                            if (!new WifiAssistantController(((SettingsTree) this).mContext).isAvailable()) {
                                return 0;
                            }
                        } else if ("wifi_menu_p2p".equals(columnValue) && wifiManager != null && locationManager != null && (new SlaveWifiUtils(((SettingsTree) this).mContext).isSlaveWifiEnabled() || wifiManager.getWifiApState() == 13 || wifiManager.getWifiState() == 1 || !locationManager.isLocationEnabled())) {
                            return 0;
                        } else {
                            if ("wifi_assist".equals(columnValue) && !MiuiWifiAssistFeatureSupport.isWifiAssistAvailable(((SettingsTree) this).mContext)) {
                                return 0;
                            }
                        }
                    }
                }
            }
        }
        return super.getStatus();
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        if ("connect_mode".equals(columnValue)) {
            String str = SystemProperties.get("ro.boot.hwversion");
            String[] split = str != null ? str.split("\\.") : null;
            if (split != null && split.length > 0 && split[0].equals(ExtraTelephony.Phonelist.TYPE_CLOUDS_BLACK)) {
                return true;
            }
        } else if ("traffic_balance".equals(columnValue)) {
            if (!SystemProperties.getBoolean("sys.net.support.netprio", false)) {
                return true;
            }
        } else if ("netcheck_title".equals(columnValue)) {
            if (!new NetworkCheckController(((SettingsTree) this).mContext).isAvailable()) {
                return true;
            }
        } else if ("wifi_cellular_data_fallback_title".equals(columnValue)) {
            if (((SettingsTree) this).mContext.getResources().getInteger(17694875) == 1) {
                return true;
            }
        } else if ("wifi_traffic_priority".equals(columnValue)) {
            if (!WifiTrafficUtils.isTrafficPrioritySupport()) {
                return true;
            }
        } else if (BluetoothSettingsTree.BT_CONNECT_HELP.equals(columnValue)) {
            return true;
        }
        return super.initialize();
    }
}
