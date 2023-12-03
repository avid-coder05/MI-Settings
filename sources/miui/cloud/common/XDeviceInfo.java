package miui.cloud.common;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import com.xiaomi.micloudsdk.utils.CloudCoder;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import miui.telephony.CloudTelephonyManager;
import miui.telephony.exception.IllegalDeviceException;

/* loaded from: classes3.dex */
public class XDeviceInfo {
    private static final long EMPTY_DEVICE_ID_CACHE_TIME_MILLIS = 180000;
    private static final int QUERTY_TIME_OUT = 60000;
    private static XDeviceInfo sInstance = null;
    private static KeyStoreType sKeyStoreType = null;
    private static long sLastEmptyDeviceIdTime = -1;
    public final String IMEI;
    public final String MAC;
    public final String SN;
    public final String deviceId;
    public final KeyStoreType keyStoreType;
    public final String model;
    public final PhoneType type;

    /* loaded from: classes3.dex */
    public interface DeviceInfoReayListener {
        void onDeviceInfoReay(XDeviceInfo xDeviceInfo);
    }

    /* loaded from: classes3.dex */
    public enum KeyStoreType {
        TZ("TZ"),
        NONE("NONE");

        private String mDesc;

        KeyStoreType(String str) {
            this.mDesc = str;
        }

        public String getDesc() {
            return this.mDesc;
        }
    }

    /* loaded from: classes3.dex */
    public enum PhoneType {
        PAD("pad"),
        PHONE("phone");

        private String mDesc;

        PhoneType(String str) {
            this.mDesc = str;
        }

        public String getDesc() {
            return this.mDesc;
        }
    }

    static {
        HashSet hashSet = new HashSet();
        hashSet.add("leo");
        hashSet.add("andromeda");
        hashSet.add("begonia");
        hashSet.add("davinciin");
        hashSet.add("raphaelin");
        hashSet.add("begoniain");
        hashSet.add("hennessy");
        hashSet.add("olivelite");
        hashSet.add("olivewood");
        hashSet.add("libra");
        hashSet.add("aqua");
        hashSet.add("gemini");
        hashSet.add("gold");
        hashSet.add("vela");
        hashSet.add("kenzo");
        hashSet.add("grus");
        hashSet.add("tucana");
        hashSet.add("ido");
        hashSet.add("hydrogen");
        hashSet.add("helium");
        hashSet.add("kate");
        hashSet.add("land");
        hashSet.add("lavender");
        hashSet.add("markw");
        hashSet.add("nikel");
        hashSet.add("omega");
        hashSet.add("cepheus");
        hashSet.add("capricorn");
        hashSet.add("laurus");
        hashSet.add("prada");
        hashSet.add("lithium");
        hashSet.add("scorpio");
        hashSet.add("natrium");
        hashSet.add("rolex");
        hashSet.add("mido");
        hashSet.add("santoni");
        hashSet.add("ginkgo");
        hashSet.add("sagit");
        hashSet.add("centaur");
        hashSet.add("oxygen");
        hashSet.add("tiffany");
        hashSet.add("ulysse");
        hashSet.add("ugglite");
        hashSet.add("chiron");
        hashSet.add("ugg");
        hashSet.add("jason");
        hashSet.add("riva");
        hashSet.add("crux");
        hashSet.add("vince");
        hashSet.add("rosy");
        hashSet.add("meri");
        hashSet.add("davinci");
        hashSet.add("pine");
        hashSet.add("whyred");
        hashSet.add("dipper");
        hashSet.add("onc");
        hashSet.add("polaris");
        hashSet.add("pyxis");
        hashSet.add("ysl");
        hashSet.add("wayne");
        hashSet.add("nitrogen");
        hashSet.add("sirius");
        hashSet.add("sakura");
        hashSet.add("sakura_india");
        hashSet.add("beryllium");
        hashSet.add("violet");
        hashSet.add("raphael");
        hashSet.add("cactus");
        hashSet.add("cereus");
        hashSet.add("lotus");
        hashSet.add("willow");
        hashSet.add("clover");
        hashSet.add("ursa");
        hashSet.add("olive");
        hashSet.add("tulip");
        hashSet.add("draco");
        hashSet.add("platina");
        hashSet.add("perseus");
        hashSet.add("equuleus");
        sKeyStoreType = hashSet.contains(Build.DEVICE.toLowerCase()) ? KeyStoreType.TZ : KeyStoreType.NONE;
    }

    private XDeviceInfo(Context context, XDeviceInfo xDeviceInfo, boolean z) {
        PhoneType phoneType = xDeviceInfo.type;
        this.type = phoneType;
        String blockingGetNakedDeviceId = blockingGetNakedDeviceId(context, z);
        this.deviceId = TextUtils.isEmpty(blockingGetNakedDeviceId) ? blockingGetNakedDeviceId : CloudCoder.hashDeviceInfo(blockingGetNakedDeviceId);
        this.IMEI = phoneType != PhoneType.PHONE ? "" : blockingGetNakedDeviceId;
        this.SN = xDeviceInfo.SN;
        this.MAC = xDeviceInfo.MAC;
        this.model = xDeviceInfo.model;
        this.keyStoreType = xDeviceInfo.keyStoreType;
    }

    private XDeviceInfo(Context context, boolean z) {
        PhoneType phoneType = CloudTelephonyManager.getMultiSimCount() == 0 ? PhoneType.PAD : PhoneType.PHONE;
        this.type = phoneType;
        String blockingGetNakedDeviceId = blockingGetNakedDeviceId(context, z);
        this.deviceId = TextUtils.isEmpty(blockingGetNakedDeviceId) ? blockingGetNakedDeviceId : CloudCoder.hashDeviceInfo(blockingGetNakedDeviceId);
        this.IMEI = phoneType != PhoneType.PHONE ? "" : blockingGetNakedDeviceId;
        this.SN = Build.SERIAL;
        this.MAC = getMAC(context);
        this.model = Build.MODEL;
        this.keyStoreType = getKeyStoreTypeUnblocking();
    }

    public static void asyncGet(final Context context, final XCallback<DeviceInfoReayListener> xCallback) {
        new Thread(new Runnable() { // from class: miui.cloud.common.XDeviceInfo.1
            @Override // java.lang.Runnable
            public void run() {
                ((DeviceInfoReayListener) XCallback.this.asInterface()).onDeviceInfoReay(XDeviceInfo.syncGet(context));
            }
        }).start();
    }

    public static void asyncGet(final Context context, final boolean z, final XCallback<DeviceInfoReayListener> xCallback) {
        new Thread(new Runnable() { // from class: miui.cloud.common.XDeviceInfo.2
            @Override // java.lang.Runnable
            public void run() {
                ((DeviceInfoReayListener) XCallback.this.asInterface()).onDeviceInfoReay(XDeviceInfo.syncGet(context, z));
            }
        }).start();
    }

    public static synchronized String blockingGetNakedDeviceId(Context context) {
        String blockingGetNakedDeviceId;
        synchronized (XDeviceInfo.class) {
            blockingGetNakedDeviceId = blockingGetNakedDeviceId(context, false);
        }
        return blockingGetNakedDeviceId;
    }

    public static synchronized String blockingGetNakedDeviceId(Context context, boolean z) {
        synchronized (XDeviceInfo.class) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j = sLastEmptyDeviceIdTime;
            if (j == -1 || elapsedRealtime - j >= EMPTY_DEVICE_ID_CACHE_TIME_MILLIS) {
                String str = null;
                try {
                    str = CloudTelephonyManager.blockingGetDeviceId(context, 60000L, z);
                } catch (IllegalDeviceException unused) {
                    XLogger.loge("Failed to get the device id.");
                }
                if (TextUtils.isEmpty(str)) {
                    sLastEmptyDeviceIdTime = elapsedRealtime;
                    return "";
                }
                sLastEmptyDeviceIdTime = -1L;
                return str;
            }
            return "";
        }
    }

    public static KeyStoreType getKeyStoreTypeUnblocking() {
        return Build.VERSION.SDK_INT >= 29 ? KeyStoreType.TZ : sKeyStoreType;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v1 */
    /* JADX WARN: Type inference failed for: r7v6, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v7 */
    /* JADX WARN: Type inference failed for: r7v8 */
    private String getMAC(Context context) {
        int i = 2;
        i = 2;
        i = 2;
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                String name = networkInterface.getName();
                if (name != null && name.toLowerCase().indexOf("wlan") != -1) {
                    try {
                        byte[] hardwareAddress = networkInterface.getHardwareAddress();
                        if (hardwareAddress != null) {
                            StringBuilder sb = new StringBuilder();
                            for (byte b : hardwareAddress) {
                                sb.append(String.format("%02X:", Byte.valueOf(b)));
                            }
                            if (sb.length() > 0) {
                                sb.deleteCharAt(sb.length() - 1);
                            }
                            i = sb.toString();
                            return i;
                        }
                    } catch (SocketException e) {
                        XLogger.log("Failed to get MAC for " + name + ", continue. ", e);
                    }
                }
            }
            return "N/A";
        } catch (SocketException e2) {
            Object[] objArr = new Object[i];
            objArr[0] = "Failed to get MAC. ";
            objArr[1] = e2;
            XLogger.log(objArr);
            return "N/A";
        }
    }

    public static boolean isSupportFido() {
        return "scorpio".equals(Build.DEVICE.toLowerCase());
    }

    public static synchronized XDeviceInfo syncGet(Context context) {
        XDeviceInfo syncGet;
        synchronized (XDeviceInfo.class) {
            syncGet = syncGet(context, false);
        }
        return syncGet;
    }

    public static synchronized XDeviceInfo syncGet(Context context, boolean z) {
        XDeviceInfo xDeviceInfo;
        synchronized (XDeviceInfo.class) {
            if (Looper.getMainLooper().getThread().equals(Thread.currentThread())) {
                throw new IllegalStateException("syncGet can not be called in the main thread. ");
            }
            XDeviceInfo xDeviceInfo2 = sInstance;
            if (xDeviceInfo2 == null) {
                sInstance = new XDeviceInfo(context, z);
            } else {
                sInstance = new XDeviceInfo(context, xDeviceInfo2, z);
            }
            xDeviceInfo = sInstance;
        }
        return xDeviceInfo;
    }

    public String toString() {
        return String.format("type: %s, deviceid: %s, IMEM: %s, SN: %s, MAC: %s, model: %s, keyStoreType: %s", this.type.name(), this.deviceId, this.IMEI, this.SN, this.MAC, this.model, this.keyStoreType);
    }
}
