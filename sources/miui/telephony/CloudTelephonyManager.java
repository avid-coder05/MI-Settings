package miui.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import micloud.compat.v18.utils.BusyWaitUtil;
import micloud.net.ConnectivityHelper;
import miui.cloud.common.XLogger;
import miui.cloud.util.AnonymousDeviceIdUtil;
import miui.cloud.util.SysHelper;
import miui.telephony.exception.IllegalDeviceException;

/* loaded from: classes4.dex */
public class CloudTelephonyManager {
    public static final String SLOT_ID = miui.cloud.telephony.SubscriptionManager.getSLOT_KEY();
    private static final String TAG = "CloudTelephonyManager";
    private static volatile String sDeviceIdCache;
    private static volatile DeviceIdConfiguration sDeviceIdConfiguration;
    static volatile DeviceIdConfiguration sDeviceIdConfigurationTestInjection;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class AsyncFuture<V> extends FutureTask<V> {
        public AsyncFuture() {
            super(new Callable<V>() { // from class: miui.telephony.CloudTelephonyManager.AsyncFuture.1
                @Override // java.util.concurrent.Callable
                public V call() throws Exception {
                    throw new IllegalStateException("this should never be called");
                }
            });
        }

        public void setResult(V v) {
            set(v);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public interface DeviceIdConfiguration {
        boolean checkValid(Context context, String str);

        long getBusywaitRetryIntervalMillisRecommandation(Context context);

        long getBusywaitTimeoutMillisRecommandation(Context context);

        String tryGetId(Context context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static final class TypedSimId {
        private static final String SP = ",";
        public static final int TYPE_ICCID = 1;
        public static final int TYPE_IMSI = 2;
        public static final int TYPE_UNKNOWN = 0;
        public final int type;
        public final String value;

        /* JADX INFO: Access modifiers changed from: package-private */
        public TypedSimId(int i, String str) {
            this.type = i;
            this.value = str;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static TypedSimId parse(String str) {
            String[] split = str.split(SP);
            return (split.length == 2 && TextUtils.isDigitsOnly(split[0])) ? new TypedSimId(Integer.parseInt(split[0]), split[1]) : new TypedSimId(0, str);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || TypedSimId.class != obj.getClass()) {
                return false;
            }
            TypedSimId typedSimId = (TypedSimId) obj;
            if (this.type != typedSimId.type) {
                return false;
            }
            String str = this.value;
            return str == null ? typedSimId.value == null : str.equals(typedSimId.value);
        }

        public int hashCode() {
            int i = this.type * 31;
            String str = this.value;
            return i + (str != null ? str.hashCode() : 0);
        }

        public String toPlain() {
            return this.type + SP + this.value;
        }

        public String toString() {
            return toPlain();
        }
    }

    public static String blockingGetDeviceId(Context context) throws IllegalDeviceException {
        return blockingGetDeviceId(context, false);
    }

    public static String blockingGetDeviceId(Context context, long j) throws IllegalDeviceException {
        return blockingGetDeviceId(context, j, false);
    }

    public static String blockingGetDeviceId(final Context context, long j, boolean z) throws IllegalDeviceException {
        ensureNotOnMainThread(context);
        Log.i(TAG, "blockingGetDeviceId is called by " + context.getPackageName() + " with timeout: " + j);
        String str = sDeviceIdCache;
        if (z || TextUtils.isEmpty(str)) {
            final DeviceIdConfiguration deviceIdConfiguration = getDeviceIdConfiguration(context);
            if (j < 0) {
                j = 0;
            }
            String str2 = null;
            try {
                str2 = (String) BusyWaitUtil.busyWait(new BusyWaitUtil.Action<String>() { // from class: miui.telephony.CloudTelephonyManager.1
                    @Override // micloud.compat.v18.utils.BusyWaitUtil.Action
                    public String doAction(long j2, long j3) throws BusyWaitUtil.NotAvailableException {
                        String tryGetId = DeviceIdConfiguration.this.tryGetId(context);
                        if (TextUtils.isEmpty(tryGetId)) {
                            Log.i(CloudTelephonyManager.TAG, "deviceid is empty after " + j3 + " retries");
                            throw new BusyWaitUtil.NotAvailableException();
                        }
                        Log.i(CloudTelephonyManager.TAG, "got deviceid after " + j3 + " retries");
                        CloudTelephonyManager.logDeviceId(tryGetId);
                        return tryGetId;
                    }
                }, j, deviceIdConfiguration.getBusywaitRetryIntervalMillisRecommandation(context));
            } catch (InterruptedException e) {
                Log.e(TAG, "blockingGetDeviceId, InterruptedException while busy-waiting", e);
            } catch (TimeoutException e2) {
                Log.e(TAG, "blockingGetDeviceId, busy-wait timeout", e2);
            }
            if (deviceIdConfiguration.checkValid(context, str2)) {
                sDeviceIdCache = str2;
                return str2;
            }
            SysHelper.showInvalidDeviceIdWarning(context, str2);
            throw new IllegalDeviceException("can't get a valid device id");
        }
        return str;
    }

    public static String blockingGetDeviceId(Context context, boolean z) throws IllegalDeviceException {
        return blockingGetDeviceId(context, getDeviceIdConfiguration(context).getBusywaitTimeoutMillisRecommandation(context), z);
    }

    public static String blockingGetSimId(Context context, int i) throws IllegalDeviceException {
        return blockingGetTypedSimId(context, i).toPlain();
    }

    private static TypedSimId blockingGetTypedSimId(Context context, int i) throws IllegalDeviceException {
        try {
            return blockingGetTypedSimId(context, i, -1L);
        } catch (TimeoutException unused) {
            throw new IllegalStateException("Never reach here. ");
        }
    }

    private static TypedSimId blockingGetTypedSimId(Context context, int i, long j) throws IllegalDeviceException, TimeoutException {
        TypedSimId waitAndGetSimId = waitAndGetSimId(context, i, j);
        if (waitAndGetSimId != null) {
            return waitAndGetSimId;
        }
        throw new IllegalDeviceException("failed to get sim id");
    }

    private static void ensureNotOnMainThread(Context context) {
        Looper myLooper = Looper.myLooper();
        if (myLooper != null && myLooper == context.getMainLooper()) {
            throw new IllegalStateException("calling this from your main thread can lead to deadlock");
        }
    }

    public static int getAvailableSimCount() {
        return miui.cloud.telephony.TelephonyManager.getDefault().getIccCardCount();
    }

    public static int getDefaultSlotId() {
        return miui.cloud.telephony.SubscriptionManager.getDefault().getDefaultSlotId();
    }

    private static DeviceIdConfiguration getDeviceIdConfiguration(Context context) {
        DeviceIdConfiguration deviceIdConfiguration = sDeviceIdConfigurationTestInjection;
        if (deviceIdConfiguration != null) {
            return deviceIdConfiguration;
        }
        DeviceIdConfiguration deviceIdConfiguration2 = sDeviceIdConfiguration;
        if (deviceIdConfiguration2 != null) {
            return deviceIdConfiguration2;
        }
        synchronized (DeviceIdConfiguration.class) {
            DeviceIdConfiguration deviceIdConfiguration3 = sDeviceIdConfiguration;
            if (deviceIdConfiguration3 != null) {
                return deviceIdConfiguration3;
            }
            if (AnonymousDeviceIdUtil.isEnforced(context)) {
                logDeviceIdInfo("getDeviceId: use restrict_imie");
                DeviceIdConfiguration deviceIdConfiguration4 = new DeviceIdConfiguration() { // from class: miui.telephony.CloudTelephonyManager.2
                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public boolean checkValid(Context context2, String str) {
                        return !TextUtils.isEmpty(str);
                    }

                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public long getBusywaitRetryIntervalMillisRecommandation(Context context2) {
                        return 30000L;
                    }

                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public long getBusywaitTimeoutMillisRecommandation(Context context2) {
                        return 60000L;
                    }

                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public String tryGetId(Context context2) {
                        if (AnonymousDeviceIdUtil.useOAID()) {
                            CloudTelephonyManager.logDeviceIdInfo("getDeviceId: restrict_imie, try get oaid");
                            String oaid = AnonymousDeviceIdUtil.getOAID(context2);
                            if (!TextUtils.isEmpty(oaid)) {
                                CloudTelephonyManager.logDeviceIdInfo("getDeviceId: restrict_imei, use oaid");
                                return oaid;
                            }
                        }
                        Bundle call = context2.getContentResolver().call(Uri.parse("content://com.xiaomi.cloud.cloudidprovider"), "getCloudId", (String) null, (Bundle) null);
                        if (call != null) {
                            CloudTelephonyManager.logDeviceIdInfo("getDeviceId: restrict_imie, use cloudId");
                            return call.getString("result_id");
                        }
                        CloudTelephonyManager.logDeviceIdInfo("getDeviceId: restrict_imie, use androidId");
                        return AnonymousDeviceIdUtil.getAndroidId(context2);
                    }
                };
                sDeviceIdConfiguration = deviceIdConfiguration4;
                return deviceIdConfiguration4;
            } else if (hasTelephonyFeature(context)) {
                logDeviceIdInfo("getDeviceId: use no_restrict_imei");
                DeviceIdConfiguration deviceIdConfiguration5 = new DeviceIdConfiguration() { // from class: miui.telephony.CloudTelephonyManager.3
                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public boolean checkValid(Context context2, String str) {
                        return SysHelper.validateIMEI(str);
                    }

                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public long getBusywaitRetryIntervalMillisRecommandation(Context context2) {
                        return 5000L;
                    }

                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public long getBusywaitTimeoutMillisRecommandation(Context context2) {
                        return 300000L;
                    }

                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public String tryGetId(Context context2) {
                        return miui.cloud.telephony.TelephonyManager.getDefault().getMiuiDeviceId();
                    }
                };
                sDeviceIdConfiguration = deviceIdConfiguration5;
                return deviceIdConfiguration5;
            } else {
                logDeviceIdInfo("getDeviceId: use macAddress");
                DeviceIdConfiguration deviceIdConfiguration6 = new DeviceIdConfiguration() { // from class: miui.telephony.CloudTelephonyManager.4
                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public boolean checkValid(Context context2, String str) {
                        return SysHelper.validateMAC(str);
                    }

                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public long getBusywaitRetryIntervalMillisRecommandation(Context context2) {
                        return 10000L;
                    }

                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public long getBusywaitTimeoutMillisRecommandation(Context context2) {
                        return 60000L;
                    }

                    @Override // miui.telephony.CloudTelephonyManager.DeviceIdConfiguration
                    public String tryGetId(Context context2) {
                        return ConnectivityHelper.getInstance(context2).getMacAddress();
                    }
                };
                sDeviceIdConfiguration = deviceIdConfiguration6;
                return deviceIdConfiguration6;
            }
        }
    }

    public static String getDeviceIdQuietly(Context context) {
        String tryGetId = getDeviceIdConfiguration(context).tryGetId(context);
        logDeviceId(tryGetId);
        return tryGetId;
    }

    public static String getLine1Number(Context context, int i) {
        return miui.cloud.telephony.TelephonyManager.getDefault().getLine1NumberForSlot(i);
    }

    public static int getMultiSimCount() {
        return miui.cloud.telephony.TelephonyManager.getDefault().getPhoneCount();
    }

    public static String getSimId(Context context, int i) {
        TypedSimId simIdByPhoneType = getSimIdByPhoneType(miui.cloud.telephony.TelephonyManager.getDefault(), i);
        if (simIdByPhoneType != null) {
            return simIdByPhoneType.toPlain();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TypedSimId getSimIdByPhoneType(miui.cloud.telephony.TelephonyManager telephonyManager, int i) {
        int phoneTypeForSlot = telephonyManager.getPhoneTypeForSlot(i);
        Log.v(TAG, "phone type: " + phoneTypeForSlot);
        if (phoneTypeForSlot == miui.cloud.telephony.TelephonyManager.getPHONE_TYPE_CDMA()) {
            String simSerialNumberForSlot = telephonyManager.getSimSerialNumberForSlot(i);
            if (TextUtils.isEmpty(simSerialNumberForSlot)) {
                return null;
            }
            return new TypedSimId(1, simSerialNumberForSlot);
        } else if (phoneTypeForSlot == miui.cloud.telephony.TelephonyManager.getPHONE_TYPE_GSM()) {
            String subscriberIdForSlot = telephonyManager.getSubscriberIdForSlot(i);
            if (TextUtils.isEmpty(subscriberIdForSlot)) {
                return null;
            }
            return new TypedSimId(2, subscriberIdForSlot);
        } else if (phoneTypeForSlot == 0) {
            String simSerialNumberForSlot2 = telephonyManager.getSimSerialNumberForSlot(i);
            if (TextUtils.isEmpty(simSerialNumberForSlot2)) {
                return null;
            }
            return new TypedSimId(1, simSerialNumberForSlot2);
        } else {
            return null;
        }
    }

    private static TypedSimId getSimIdByPhoneTypeForSubId(miui.cloud.telephony.TelephonyManager telephonyManager, int i) {
        int phoneTypeForSubscription = telephonyManager.getPhoneTypeForSubscription(i);
        Log.v(TAG, "device type: " + phoneTypeForSubscription);
        if (phoneTypeForSubscription == miui.cloud.telephony.TelephonyManager.getPHONE_TYPE_CDMA()) {
            String simSerialNumberForSubscription = telephonyManager.getSimSerialNumberForSubscription(i);
            if (TextUtils.isEmpty(simSerialNumberForSubscription)) {
                return null;
            }
            return new TypedSimId(1, simSerialNumberForSubscription);
        } else if (phoneTypeForSubscription == miui.cloud.telephony.TelephonyManager.getPHONE_TYPE_GSM()) {
            String subscriberIdForSubscription = telephonyManager.getSubscriberIdForSubscription(i);
            if (TextUtils.isEmpty(subscriberIdForSubscription)) {
                return null;
            }
            return new TypedSimId(2, subscriberIdForSubscription);
        } else {
            return null;
        }
    }

    public static long getSimIdBySlotId(Context context, int i) {
        return miui.cloud.telephony.SubscriptionManager.getDefault().getSubscriptionIdForSlot(i);
    }

    public static String getSimIdForSubId(Context context, int i) {
        TypedSimId simIdByPhoneTypeForSubId = getSimIdByPhoneTypeForSubId(miui.cloud.telephony.TelephonyManager.getDefault(), i);
        if (simIdByPhoneTypeForSubId != null) {
            return simIdByPhoneTypeForSubId.toPlain();
        }
        return null;
    }

    public static String getSimOperator(Context context, int i) {
        return miui.cloud.telephony.TelephonyManager.getDefault().getSimOperatorForSlot(i);
    }

    public static String getSimOperatorName(Context context, int i) {
        return miui.cloud.telephony.TelephonyManager.getDefault().getSimOperatorNameForSlot(i);
    }

    public static int getSlotIdBySimId(Context context, long j) {
        return miui.cloud.telephony.SubscriptionManager.getDefault().getSlotIdForSubscription((int) j);
    }

    private static boolean hasTelephonyFeature(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    public static boolean isMultiSimSupported() {
        return miui.cloud.telephony.TelephonyManager.getDefault().isMultiSimEnabled();
    }

    public static boolean isSimInserted(Context context, int i) {
        return miui.cloud.telephony.TelephonyManager.getDefault().hasIccCard(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logDeviceId(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("getDeviceId: ");
        sb.append((str == null || str.length() < 2) ? "wrongId" : str.substring(0, 2));
        logDeviceIdInfo(sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logDeviceIdInfo(String str) {
        XLogger.logi(TAG, str);
        Log.i(TAG, str);
    }

    private static TypedSimId waitAndGetSimId(Context context, final int i, long j) throws TimeoutException {
        ensureNotOnMainThread(context);
        if (SysHelper.hasModemCapability()) {
            final miui.cloud.telephony.TelephonyManager telephonyManager = miui.cloud.telephony.TelephonyManager.getDefault();
            final AsyncFuture asyncFuture = new AsyncFuture();
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: miui.telephony.CloudTelephonyManager.5
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context2, Intent intent) {
                    if (miui.cloud.telephony.TelephonyManager.getIccCardConstants_INTENT_VALUE_ICC_IMSI().equals(intent.getStringExtra(miui.cloud.telephony.TelephonyManager.getIccCardConstants_INTENT_KEY_ICC_STATE()))) {
                        AsyncFuture.this.setResult(CloudTelephonyManager.getSimIdByPhoneType(telephonyManager, i));
                    }
                }
            };
            context.registerReceiver(broadcastReceiver, new IntentFilter(miui.cloud.telephony.TelephonyManager.getTelephonyIntents_ACTION_SIM_STATE_CHANGED()));
            TypedSimId simIdByPhoneType = getSimIdByPhoneType(telephonyManager, i);
            if (simIdByPhoneType != null) {
                asyncFuture.setResult(simIdByPhoneType);
            }
            try {
                return j < 0 ? (TypedSimId) asyncFuture.get() : (TypedSimId) asyncFuture.get(j, TimeUnit.MILLISECONDS);
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
                return null;
            } catch (Exception e) {
                if (e instanceof TimeoutException) {
                    throw ((TimeoutException) e);
                }
                Log.e(TAG, "exception when get sim id", e);
                return null;
            } finally {
                context.unregisterReceiver(broadcastReceiver);
            }
        }
        return null;
    }
}
