package com.android.settings.search;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import com.android.settings.R;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
class AboutDeviceUpdateHelper extends BaseSearchUpdateHelper {
    private static final String ACTION_COPYRIGHT = "android.settings.COPYRIGHT";
    private static final String ACTION_LICENSE = "android.settings.LICENSE";
    private static final String ACTION_TERMS = "android.settings.TERMS";
    private static final String APPROVE_RESOURCE = "approve_title";
    private static final String BASEBAND_VERSION_RESOURCE = "baseband_version";
    private static final String COPYRIGHT_RESOURCE = "copyright_title";
    private static final String HARDWARE_VERSION_RESOURCE = "hardware_version";
    private static final String INSTRUCTION_RESOURCE = "instruction_title";
    private static final String LICENSE_RESOURCE = "license_title";
    private static final String PRE_INSTALLED_APPLICATION_RESOURCE = "pre_installed_application";
    private static final String SAFETY_LEGAL_RESOURCE = "settings_safetylegal_title";
    private static final String STATUS_BT_ADDRESS_RESOURCE = "status_bt_address";
    private static final String STATUS_SERIALNO_RESOURCE = "status_serialno";
    private static final String STATUS_SERIAL_NUMBER_RESOURCE = "status_serial_number";
    private static final String STATUS_WIMAX_MAC_ADDRESS_RESOURCE = "status_wimax_mac_address";
    private static final String TERMS_RESOURCE = "terms_title";
    private static final String TYPE_APPROVAL_RESOURCE = "wifi_type_approval_title";

    AboutDeviceUpdateHelper() {
    }

    private static void hideByResourceIfNoActivity(Context context, ArrayList<ContentProviderOperation> arrayList, String str, Intent intent) {
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
        int size = queryIntentActivities.size();
        for (int i = 0; i < size; i++) {
            if ((queryIntentActivities.get(i).activityInfo.applicationInfo.flags & 1) != 0) {
                return;
            }
        }
        BaseSearchUpdateHelper.hideByResource(context, arrayList, str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update(Context context, ArrayList<ContentProviderOperation> arrayList) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (Utils.isWifiOnly(context)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, BASEBAND_VERSION_RESOURCE);
        }
        if (TextUtils.isEmpty(SystemProperties.get("ro.miui.cust_hardware", ""))) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, HARDWARE_VERSION_RESOURCE);
        }
        if (TextUtils.isEmpty(context.getResources().getString(R.string.wifi_type_approval))) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, TYPE_APPROVAL_RESOURCE);
        }
        if (TextUtils.isEmpty(SystemProperties.get("ro.url.safetylegal"))) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SAFETY_LEGAL_RESOURCE);
        }
        if (!MiuiAboutPhoneUtils.enableShowCredentials()) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, APPROVE_RESOURCE);
        }
        hideByResourceIfNoActivity(context, arrayList, COPYRIGHT_RESOURCE, new Intent(ACTION_COPYRIGHT));
        hideByResourceIfNoActivity(context, arrayList, LICENSE_RESOURCE, new Intent(ACTION_LICENSE));
        hideByResourceIfNoActivity(context, arrayList, TERMS_RESOURCE, new Intent(ACTION_TERMS));
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, STATUS_BT_ADDRESS_RESOURCE);
        }
        if (connectivityManager.getNetworkInfo(6) == null) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, STATUS_WIMAX_MAC_ADDRESS_RESOURCE);
        }
        String str = Build.SERIAL;
        if (str == null || str.equals("")) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, STATUS_SERIAL_NUMBER_RESOURCE);
        }
        if (TextUtils.isEmpty(SystemProperties.get("permanent.hw.custom.serialno"))) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, STATUS_SERIALNO_RESOURCE);
        }
        if (MiuiAboutPhoneUtils.supportDisplayPreInstalledApplication()) {
            return;
        }
        BaseSearchUpdateHelper.hideByResource(context, arrayList, PRE_INSTALLED_APPLICATION_RESOURCE);
    }
}
