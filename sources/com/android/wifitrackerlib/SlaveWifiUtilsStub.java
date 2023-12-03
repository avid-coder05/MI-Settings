package com.android.wifitrackerlib;

import android.content.Context;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import com.android.net.module.util.NetUtils;
import com.android.settings.wifi.details2.WifiDetailPreferenceController2$$ExternalSyntheticLambda8;
import com.android.settingslib.wifi.SlaveWifiUtils;
import com.android.wifitrackerlib.WifiEntry;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/* loaded from: classes2.dex */
public class SlaveWifiUtilsStub {
    private static volatile SlaveWifiUtilsStub mInstance;
    private static volatile Class mSlaveWifiUtils;
    private static volatile ISlaveWifiUtils mUtils;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.wifitrackerlib.SlaveWifiUtilsStub$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$net$NetworkInfo$DetailedState;

        static {
            int[] iArr = new int[NetworkInfo.DetailedState.values().length];
            $SwitchMap$android$net$NetworkInfo$DetailedState = iArr;
            try {
                iArr[NetworkInfo.DetailedState.SCANNING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.CONNECTING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.AUTHENTICATING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.OBTAINING_IPADDR.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.VERIFYING_POOR_LINK.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.CONNECTED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    static {
        try {
            mSlaveWifiUtils = SlaveWifiUtils.class;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SlaveWifiUtilsStub(Context context) {
        try {
            if (mSlaveWifiUtils != null) {
                mUtils = (ISlaveWifiUtils) mSlaveWifiUtils.getConstructor(Context.class).newInstance(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final SlaveWifiUtilsStub getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SlaveWifiUtilsStub.class) {
                if (mInstance == null) {
                    mInstance = new SlaveWifiUtilsStub(context);
                }
            }
        }
        return mInstance;
    }

    public static int getSlaveConnectedState(NetworkInfo networkInfo) {
        if (networkInfo == null) {
            return 0;
        }
        switch (AnonymousClass1.$SwitchMap$android$net$NetworkInfo$DetailedState[networkInfo.getDetailedState().ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return 1;
            case 7:
                return 2;
            default:
                return 0;
        }
    }

    public static void initSlaveConnectedInfo(LinkProperties linkProperties, WifiEntry.ConnectedInfo connectedInfo) {
        ArrayList arrayList = new ArrayList();
        for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
            if (linkAddress.getAddress() instanceof Inet4Address) {
                connectedInfo.ipAddress = linkAddress.getAddress().getHostAddress();
                try {
                    connectedInfo.subnetMask = NetUtils.getNetworkPart(InetAddress.getByAddress(new byte[]{-1, -1, -1, -1}), linkAddress.getPrefixLength()).getHostAddress();
                } catch (UnknownHostException unused) {
                }
            } else if (linkAddress.getAddress() instanceof Inet6Address) {
                arrayList.add(linkAddress.getAddress().getHostAddress());
            }
        }
        connectedInfo.ipv6Addresses = arrayList;
        Iterator<RouteInfo> it = linkProperties.getRoutes().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            RouteInfo next = it.next();
            if (next.isDefaultRoute() && (next.getDestination().getAddress() instanceof Inet4Address) && next.hasGateway()) {
                connectedInfo.gateway = next.getGateway().getHostAddress();
                break;
            }
        }
        connectedInfo.dnsServers = (List) linkProperties.getDnsServers().stream().map(WifiDetailPreferenceController2$$ExternalSyntheticLambda8.INSTANCE).collect(Collectors.toList());
    }

    public void connectToSlaveAp(int i) {
        if (mUtils != null) {
            mUtils.connectToSlaveAp(i);
        }
    }

    public void connectToSlaveAp(WifiConfiguration wifiConfiguration) {
        if (mUtils != null) {
            mUtils.connectToSlaveAp(wifiConfiguration);
        }
    }

    public Network getSlaveWifiCurrentNetwork() {
        if (mUtils != null) {
            return mUtils.getSlaveWifiCurrentNetwork();
        }
        return null;
    }

    public WifiInfo getWifiSlaveConnectionInfo() {
        if (mUtils != null) {
            return mUtils.getWifiSlaveConnectionInfo();
        }
        return null;
    }

    public boolean is24GHz(ScanResult scanResult) {
        if (mUtils != null) {
            return mUtils.is24GHz(scanResult);
        }
        return false;
    }

    public boolean is5GHz(ScanResult scanResult) {
        if (mUtils != null) {
            return mUtils.is5GHz(scanResult);
        }
        return false;
    }

    public boolean isSlaveWifiEnabled() {
        if (mUtils != null) {
            return mUtils.isSlaveWifiEnabled();
        }
        return false;
    }
}
