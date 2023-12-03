package miui.cloud;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.micloudsdk.utils.CloudCoder;
import com.xiaomi.micloudsdk.utils.MiCloudConstants;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import miui.cloud.os.SystemProperties;
import miui.telephony.CloudTelephonyManager;
import miui.telephony.exception.IllegalDeviceException;

/* loaded from: classes3.dex */
public class CloudRequestUtils {
    private static final String TAG = "CloudRequestUtils";
    public static final String URL_CALL_LOG_BASE;
    public static final String URL_CONTACT_BASE;
    public static final String URL_DEV_BASE;
    public static final String URL_DEV_SETTING = "/api/user/device/setting";
    public static final String URL_FIND_DEVICE_BASE;
    public static final String URL_GALLERY_BASE;
    public static final String URL_MICARD_BASE;
    public static final String URL_MICLOUD_STATUS_BASE;
    public static final String URL_MMS_BASE;
    public static final String URL_MUSIC_BASE;
    public static final String URL_NOTE_BASE;
    public static final String URL_RICH_MEDIA_BASE;
    public static final String URL_WIFI_BASE;
    public static final String URL_WIFI_SHARE_BASE;
    private static String sUserAgent;

    /* loaded from: classes3.dex */
    private static class ConnectivityResumedReceiver extends BroadcastReceiver {
        private final Context mContext;
        private final AsyncFuture<Boolean> mFuture;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static final class AsyncFuture<V> extends FutureTask<V> {
            public AsyncFuture() {
                super(new Callable<V>() { // from class: miui.cloud.CloudRequestUtils.ConnectivityResumedReceiver.AsyncFuture.1
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

        private ConnectivityResumedReceiver(Context context) {
            this.mFuture = new AsyncFuture<>();
            this.mContext = context;
        }

        public void await() throws InterruptedException, ExecutionException {
            if (CloudRequestUtils.isNetworkConnected(this.mContext)) {
                this.mFuture.setResult(Boolean.TRUE);
            }
            this.mFuture.get();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("noConnectivity", false)) {
                return;
            }
            Log.i(CloudRequestUtils.TAG, "connectivity resumed");
            this.mFuture.setResult(Boolean.TRUE);
        }
    }

    static {
        boolean z = MiCloudConstants.USE_PREVIEW;
        URL_RICH_MEDIA_BASE = z ? "http://api.micloud.preview.n.xiaomi.net" : "http://fileapi.micloud.xiaomi.net";
        URL_CONTACT_BASE = z ? "http://micloud.preview.n.xiaomi.net" : "http://contactapi.micloud.xiaomi.net";
        URL_MICARD_BASE = z ? "http://micardapi.micloud.preview.n.xiaomi.net" : "http://micardapi.micloud.xiaomi.net";
        URL_MMS_BASE = z ? "http://micloud.preview.n.xiaomi.net" : "http://smsapi.micloud.xiaomi.net";
        URL_GALLERY_BASE = z ? "http://micloud.preview.n.xiaomi.net" : "http://galleryapi.micloud.xiaomi.net";
        URL_FIND_DEVICE_BASE = z ? "http://micloud.preview.n.xiaomi.net" : "http://findapi.micloud.xiaomi.net";
        URL_WIFI_BASE = z ? "http://micloud.preview.n.xiaomi.net" : "http://wifiapi.micloud.xiaomi.net";
        URL_NOTE_BASE = z ? "http://micloud.preview.n.xiaomi.net" : "http://noteapi.micloud.xiaomi.net";
        URL_MUSIC_BASE = z ? "http://micloud.preview.n.xiaomi.net" : "http://musicapi.micloud.xiaomi.net";
        URL_CALL_LOG_BASE = z ? "http://micloud.preview.n.xiaomi.net" : "http://phonecallapi.micloud.xiaomi.net";
        URL_WIFI_SHARE_BASE = z ? "http://micloud.preview.n.xiaomi.net" : "http://wifisharingapi.micloud.xiaomi.net";
        URL_DEV_BASE = z ? "http://api.device.preview.n.xiaomi.net" : "http://api.device.xiaomi.net";
        URL_MICLOUD_STATUS_BASE = z ? "http://statusapi.micloud.preview.n.xiaomi.net" : "http://statusapi.micloud.xiaomi.net";
    }

    private static String convertObsoleteLanguageCodeToNew(String str) {
        if (str == null) {
            return null;
        }
        return "iw".equals(str) ? "he" : "in".equals(str) ? "id" : "ji".equals(str) ? "yi" : str;
    }

    private static void ensureNotOnMainThread(Context context) {
        Looper myLooper = Looper.myLooper();
        if (myLooper != null && myLooper == context.getMainLooper()) {
            throw new IllegalStateException("calling this from your main thread can lead to deadlock");
        }
    }

    public static String getHashedDeviceId(Context context) throws IllegalDeviceException {
        return CloudCoder.hashDeviceInfo(CloudTelephonyManager.blockingGetDeviceId(context));
    }

    public static String getResourceId(Context context) throws IllegalDeviceException {
        return getHashedDeviceId(context);
    }

    public static String getUserAgent() {
        if (sUserAgent == null) {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            String str = Build.MODEL;
            sb2.append(str);
            sb2.append("/");
            sb.append(sb2.toString());
            String str2 = SystemProperties.get("ro.product.mod_device");
            if (TextUtils.isEmpty(str2)) {
                sb.append(str);
            } else {
                sb.append(str2);
            }
            sb.append("; MIUI/");
            sb.append(Build.VERSION.INCREMENTAL);
            sb.append(" E/");
            sb.append(SystemProperties.get("ro.miui.ui.version.name"));
            sb.append(" B/");
            if (miui.os.Build.IS_ALPHA_BUILD) {
                sb.append("A");
            } else if (miui.os.Build.IS_DEVELOPMENT_VERSION) {
                sb.append("D");
            } else if (miui.os.Build.IS_STABLE_VERSION) {
                sb.append("S");
            } else {
                sb.append("null");
            }
            sb.append(" L/");
            Locale locale = Locale.getDefault();
            String language = locale.getLanguage();
            if (language != null) {
                sb.append(convertObsoleteLanguageCodeToNew(language));
                String country = locale.getCountry();
                if (country != null) {
                    sb.append("-");
                    sb.append(country.toUpperCase());
                }
            } else {
                sb.append("EN");
            }
            sb.append(" LO/");
            String region = miui.os.Build.getRegion();
            if (TextUtils.isEmpty(region)) {
                sb.append("null");
            } else {
                sb.append(region.toUpperCase());
            }
            sUserAgent = sb.toString();
        }
        return sUserAgent;
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void waitUntilNetworkConnected(Context context) throws InterruptedException {
        ensureNotOnMainThread(context);
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        ConnectivityResumedReceiver connectivityResumedReceiver = new ConnectivityResumedReceiver(context);
        context.registerReceiver(connectivityResumedReceiver, intentFilter);
        try {
            connectivityResumedReceiver.await();
        } catch (ExecutionException unused) {
        } catch (Throwable th) {
            context.unregisterReceiver(connectivityResumedReceiver);
            throw th;
        }
        context.unregisterReceiver(connectivityResumedReceiver);
    }
}
