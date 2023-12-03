package com.android.settings.wifi;

import android.app.BroadcastOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkUtils;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.R;
import com.android.settings.wifi.openwifi.Utils;
import com.android.settingslib.wifi.SlaveWifiUtils;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import miui.settings.commonlib.MemoryOptimizationUtil;

/* loaded from: classes2.dex */
public class XiaomiRouterUtils {
    private static HashMap<String, Integer> sWpsDeviceNameToDrawableId;

    static {
        HashMap<String, Integer> hashMap = new HashMap<>();
        sWpsDeviceNameToDrawableId = hashMap;
        hashMap.put("wx", Integer.valueOf(R.drawable.xiaomi_wifi_wechat_indicator));
        sWpsDeviceNameToDrawableId.put("dp", Integer.valueOf(R.drawable.xiaomi_wifi_dianping_indicator));
        sWpsDeviceNameToDrawableId.put("mt", Integer.valueOf(R.drawable.xiaomi_wifi_meituan_indicator));
        sWpsDeviceNameToDrawableId.put("nm", Integer.valueOf(R.drawable.xiaomi_wifi_nuomi_indicator));
        sWpsDeviceNameToDrawableId.put("xiaomi", Integer.valueOf(R.drawable.xiaomi_wifi_indicator));
    }

    public static int getIndictorDrawableId(Set<ScanResult> set) {
        return sWpsDeviceNameToDrawableId.get("xiaomi").intValue();
    }

    public static boolean isXiaomiRouter(Set<ScanResult> set) {
        if (set != null) {
            Iterator<ScanResult> it = set.iterator();
            while (it.hasNext()) {
                ScanResult.InformationElement[] informationElementArr = (ScanResult.InformationElement[]) it.next().getInformationElements().toArray();
                if (informationElementArr != null && informationElementArr.length > 0) {
                    for (int i = 0; i < informationElementArr.length; i++) {
                        if (informationElementArr[i].getId() == 221) {
                            ByteBuffer bytes = informationElementArr[i].getBytes();
                            byte[] bArr = new byte[bytes.limit()];
                            try {
                                bytes.get(bArr, 0, bytes.limit());
                                if (new String(bArr).toLowerCase().contains("xiaomi")) {
                                    return true;
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

    public static void showManageRouter(Context context) {
        DhcpInfo dhcpInfo = ((WifiManager) context.getSystemService("wifi")).getDhcpInfo();
        Intent intent = new Intent("android.intent.action.VIEW");
        if (dhcpInfo != null) {
            intent.setData(Uri.parse("http://" + NetworkUtils.intToInetAddress(dhcpInfo.gateway).getHostAddress()));
        } else {
            intent.setData(Uri.parse("http://192.168.31.1"));
        }
        try {
            Intent defaultBrowserPkgIntent = Utils.getDefaultBrowserPkgIntent(context, intent);
            defaultBrowserPkgIntent.addFlags(335544320);
            context.startActivity(defaultBrowserPkgIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void showSlaveManageRouter(Context context) {
        Intent intent = new Intent();
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setBackgroundActivityStartsAllowed(true);
        intent.addFlags(268435456);
        intent.setPackage(MemoryOptimizationUtil.CONTROLLER_PKG);
        intent.setAction("com.miui.action.OPEN_WIFI_LOGIN");
        SlaveWifiUtils slaveWifiUtils = SlaveWifiUtils.getInstance(context);
        intent.putExtra("miui.intent.extra.OPEN_WIFI_SSID", slaveWifiUtils.getWifiSlaveConnectionInfo().getSSID());
        DhcpInfo slaveDhcpInfo = slaveWifiUtils.getSlaveDhcpInfo();
        if (slaveDhcpInfo != null) {
            intent.setData(Uri.parse("http://" + NetworkUtils.intToInetAddress(slaveDhcpInfo.gateway).getHostAddress()));
        } else {
            intent.setData(Uri.parse("http://192.168.31.1"));
        }
        Network slaveWifiCurrentNetwork = slaveWifiUtils.getSlaveWifiCurrentNetwork();
        if (slaveWifiCurrentNetwork == null) {
            return;
        }
        intent.putExtra("miui.intent.extra.NETWORK", slaveWifiCurrentNetwork);
        intent.putExtra("miui.intent.extra.EXPLICIT_SELECTED", true);
        intent.putExtra("miui.intent.extra.IS_SLAVE", true);
        intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
        context.sendBroadcast(intent, null, makeBasic.toBundle());
    }
}
