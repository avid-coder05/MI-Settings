package com.android.settings.network.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.euicc.EuiccManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsRcsManager;
import android.telephony.ims.ProvisioningManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.android.settings.R;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.search.SearchUpdater;
import com.android.settingslib.Utils;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.graph.SignalDrawable;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: classes2.dex */
public class MobileNetworkUtils {
    public static final Drawable EMPTY_DRAWABLE = new ColorDrawable(0);

    public static boolean activeNetworkIsCellular(Context context) {
        NetworkCapabilities networkCapabilities;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null || (networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)) == null) {
            return false;
        }
        return networkCapabilities.hasTransport(0);
    }

    private static Intent buildConfigureIntent(Context context, PhoneAccountHandle phoneAccountHandle, String str) {
        if (phoneAccountHandle == null || phoneAccountHandle.getComponentName() == null || TextUtils.isEmpty(phoneAccountHandle.getComponentName().getPackageName())) {
            return null;
        }
        Intent intent = new Intent(str);
        intent.setPackage(phoneAccountHandle.getComponentName().getPackageName());
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandle);
        if (context.getPackageManager().queryIntentActivities(intent, 0).size() == 0) {
            return null;
        }
        return intent;
    }

    public static Intent buildPhoneAccountConfigureIntent(Context context, PhoneAccountHandle phoneAccountHandle) {
        Intent buildConfigureIntent = buildConfigureIntent(context, phoneAccountHandle, "android.telecom.action.CONFIGURE_PHONE_ACCOUNT");
        return buildConfigureIntent == null ? buildConfigureIntent(context, phoneAccountHandle, "android.telecom.action.CONNECTION_SERVICE_CONFIGURE") : buildConfigureIntent;
    }

    private static int[] getActiveSubscriptionIdList(Context context) {
        List<SubscriptionInfo> activeSubscriptionInfoList = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoList();
        int i = 0;
        if (activeSubscriptionInfoList == null) {
            return new int[0];
        }
        int[] iArr = new int[activeSubscriptionInfoList.size()];
        Iterator<SubscriptionInfo> it = activeSubscriptionInfoList.iterator();
        while (it.hasNext()) {
            iArr[i] = it.next().getSubscriptionId();
            i++;
        }
        return iArr;
    }

    private static int getAdjustedRaf(int i) {
        if ((i & 32771) > 0) {
            i |= 32771;
        }
        if ((i & 17284) > 0) {
            i |= 17284;
        }
        if ((i & 72) > 0) {
            i |= 72;
        }
        if ((i & 10288) > 0) {
            i |= 10288;
        }
        if ((i & 266240) > 0) {
            i |= 266240;
        }
        return (i & 524288) > 0 ? i | 524288 : i;
    }

    public static int getAvailability(Context context, int i, TelephonyAvailabilityCallback telephonyAvailabilityCallback) {
        if (i != -1) {
            return telephonyAvailabilityCallback.getAvailabilityStatus(i);
        }
        int[] activeSubscriptionIdList = getActiveSubscriptionIdList(context);
        if (ArrayUtils.isEmpty(activeSubscriptionIdList)) {
            return telephonyAvailabilityCallback.getAvailabilityStatus(-1);
        }
        for (int i2 : activeSubscriptionIdList) {
            int availabilityStatus = telephonyAvailabilityCallback.getAvailabilityStatus(i2);
            if (availabilityStatus == 0) {
                return availabilityStatus;
            }
        }
        return telephonyAvailabilityCallback.getAvailabilityStatus(activeSubscriptionIdList[0]);
    }

    public static CharSequence getCurrentCarrierNameForDisplay(Context context) {
        SubscriptionInfo subscriptionInfo;
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        return (subscriptionManager == null || (subscriptionInfo = getSubscriptionInfo(subscriptionManager, SubscriptionManager.getDefaultSubscriptionId())) == null) ? getOperatorNameFromTelephonyManager(context) : subscriptionInfo.getCarrierName();
    }

    public static CharSequence getCurrentCarrierNameForDisplay(Context context, int i) {
        SubscriptionInfo subscriptionInfo;
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        return (subscriptionManager == null || (subscriptionInfo = getSubscriptionInfo(subscriptionManager, i)) == null) ? getOperatorNameFromTelephonyManager(context) : subscriptionInfo.getCarrierName();
    }

    private static ImsRcsManager getImsRcsManager(ImsManager imsManager, int i) {
        if (imsManager == null) {
            return null;
        }
        try {
            return imsManager.getImsRcsManager(i);
        } catch (Exception e) {
            Log.w("MobileNetworkUtils", "Could not resolve ImsRcsManager: " + e.getMessage());
            return null;
        }
    }

    public static int getNetworkTypeFromRaf(int i) {
        switch (getAdjustedRaf(i)) {
            case 72:
                return 5;
            case 10288:
                return 6;
            case 10360:
                return 4;
            case 17284:
                return 2;
            case 32771:
                return 1;
            case 50055:
                return 0;
            case 60415:
                return 7;
            case SearchUpdater.GOOGLE /* 65536 */:
                return 13;
            case 82820:
                return 14;
            case 98307:
                return 16;
            case 115591:
                return 18;
            case 125951:
                return 21;
            case 266240:
                return 11;
            case 276600:
                return 8;
            case 283524:
                return 12;
            case 316295:
                return 9;
            case 326655:
                return 10;
            case 331776:
                return 15;
            case 349060:
                return 19;
            case 364547:
                return 17;
            case 381831:
                return 20;
            case 392191:
                return 22;
            case 524288:
                return 23;
            case 790528:
                return 24;
            case 800888:
                return 25;
            case 807812:
                return 28;
            case 840583:
                return 26;
            case 850943:
                return 27;
            case 856064:
                return 29;
            case 873348:
                return 31;
            case 888835:
                return 30;
            case 906119:
                return 32;
            case 916479:
                return 33;
            default:
                return -1;
        }
    }

    private static String getOperatorNameFromTelephonyManager(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (telephonyManager == null) {
            return null;
        }
        return telephonyManager.getNetworkOperatorName();
    }

    private static CharSequence getPreferredCallStatus(Context context, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.getSubscriptionId() == SubscriptionManager.getDefaultVoiceSubscriptionId() ? setSummaryResId(context, R.string.calls_sms_preferred) : "";
    }

    private static CharSequence getPreferredSmsStatus(Context context, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.getSubscriptionId() == SubscriptionManager.getDefaultSmsSubscriptionId() ? setSummaryResId(context, R.string.calls_sms_preferred) : "";
    }

    public static CharSequence getPreferredStatus(Context context, SubscriptionManager subscriptionManager, boolean z) {
        List<SubscriptionInfo> activeSubscriptions = SubscriptionUtil.getActiveSubscriptions(subscriptionManager);
        if (activeSubscriptions.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (SubscriptionInfo subscriptionInfo : activeSubscriptions) {
            int size = activeSubscriptions.size();
            CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, context);
            if (size == 1 && SubscriptionManager.isValidSubscriptionId(subscriptionInfo.getSubscriptionId())) {
                return uniqueSubscriptionDisplayName;
            }
            CharSequence preferredCallStatus = z ? getPreferredCallStatus(context, subscriptionInfo) : getPreferredSmsStatus(context, subscriptionInfo);
            if (preferredCallStatus.toString().isEmpty()) {
                sb.append(uniqueSubscriptionDisplayName);
            } else {
                sb.append(uniqueSubscriptionDisplayName);
                sb.append(" (");
                sb.append(preferredCallStatus);
                sb.append(")");
            }
            if (subscriptionInfo != activeSubscriptions.get(activeSubscriptions.size() - 1)) {
                sb.append(", ");
            }
        }
        return sb;
    }

    public static long getRafFromNetworkType(int i) {
        switch (i) {
            case 0:
                return 50055L;
            case 1:
                return 32771L;
            case 2:
                return 17284L;
            case 3:
                return 50055L;
            case 4:
                return 10360L;
            case 5:
                return 72L;
            case 6:
                return 10288L;
            case 7:
                return 60415L;
            case 8:
                return 276600L;
            case 9:
                return 316295L;
            case 10:
                return 326655L;
            case 11:
                return 266240L;
            case 12:
                return 283524L;
            case 13:
                return PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH;
            case 14:
                return 82820L;
            case 15:
                return 331776L;
            case 16:
                return 98307L;
            case 17:
                return 364547L;
            case 18:
                return 115591L;
            case 19:
                return 349060L;
            case 20:
                return 381831L;
            case 21:
                return 125951L;
            case 22:
                return 392191L;
            case 23:
                return PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED;
            case 24:
                return 790528L;
            case 25:
                return 800888L;
            case 26:
                return 840583L;
            case 27:
                return 850943L;
            case 28:
                return 807812L;
            case 29:
                return 856064L;
            case 30:
                return 888835L;
            case 31:
                return 873348L;
            case 32:
                return 906119L;
            case 33:
                return 916479L;
            default:
                return 0L;
        }
    }

    public static int getSearchableSubscriptionId(Context context) {
        int[] activeSubscriptionIdList = getActiveSubscriptionIdList(context);
        if (activeSubscriptionIdList.length >= 1) {
            return activeSubscriptionIdList[0];
        }
        return -1;
    }

    public static Drawable getSignalStrengthIcon(Context context, int i, int i2, int i3, boolean z) {
        SignalDrawable signalDrawable = new SignalDrawable(context);
        signalDrawable.setLevel(SignalDrawable.getState(i, i2, z));
        Drawable drawable = i3 == 0 ? EMPTY_DRAWABLE : context.getResources().getDrawable(i3, context.getTheme());
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.signal_strength_icon_size);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawable, signalDrawable});
        layerDrawable.setLayerGravity(0, 51);
        layerDrawable.setLayerGravity(1, 85);
        layerDrawable.setLayerSize(1, dimensionPixelSize, dimensionPixelSize);
        layerDrawable.setTintList(Utils.getColorAttr(context, 16843817));
        return layerDrawable;
    }

    private static SubscriptionInfo getSubscriptionInfo(SubscriptionManager subscriptionManager, int i) {
        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList == null) {
            return null;
        }
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            if (subscriptionInfo.getSubscriptionId() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    public static boolean isCdmaOptions(Context context, int i) {
        int networkTypeFromRaf;
        if (i == -1) {
            return false;
        }
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
        if (createForSubscriptionId.getPhoneType() == 2) {
            return true;
        }
        if (configForSubId == null || configForSubId.getBoolean("hide_carrier_network_settings_bool") || !configForSubId.getBoolean("world_phone_bool")) {
            return isWorldMode(context, i) && ((networkTypeFromRaf = getNetworkTypeFromRaf((int) createForSubscriptionId.getAllowedNetworkTypesForReason(0))) == 9 || networkTypeFromRaf == 8 || networkTypeFromRaf == 26 || networkTypeFromRaf == 25 || shouldSpeciallyUpdateGsmCdma(context, i));
        }
        return true;
    }

    public static boolean isContactDiscoveryEnabled(Context context, int i) {
        return isContactDiscoveryEnabled((ImsManager) context.getSystemService(ImsManager.class), i);
    }

    public static boolean isContactDiscoveryEnabled(ImsManager imsManager, int i) {
        ImsRcsManager imsRcsManager = getImsRcsManager(imsManager, i);
        if (imsRcsManager == null) {
            return false;
        }
        try {
            return imsRcsManager.getUceAdapter().isUceSettingEnabled();
        } catch (ImsException e) {
            Log.w("MobileNetworkUtils", "UCE service is not available: " + e.getMessage());
            return false;
        }
    }

    public static boolean isContactDiscoveryVisible(Context context, int i) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager == null) {
            Log.w("MobileNetworkUtils", "isContactDiscoveryVisible: Could not resolve carrier config");
            return false;
        }
        PersistableBundle configForSubId = carrierConfigManager.getConfigForSubId(i);
        return configForSubId.getBoolean("use_rcs_presence_bool", false) || configForSubId.getBoolean("ims.rcs_bulk_capability_exchange_bool", false);
    }

    private static boolean isCurrentCountrySupported(Context context) {
        EuiccManager euiccManager = (EuiccManager) context.getSystemService(EuiccManager.class);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        for (int i = 0; i < telephonyManager.getPhoneCount(); i++) {
            String networkCountryIso = telephonyManager.getNetworkCountryIso(i);
            if (euiccManager.isSupportedCountry(networkCountryIso)) {
                Log.i("MobileNetworkUtils", "isCurrentCountrySupported: eSIM is supported in " + networkCountryIso);
                return true;
            }
        }
        Log.i("MobileNetworkUtils", "isCurrentCountrySupported: eSIM is not supported in the current country.");
        return false;
    }

    public static boolean isDpcApnEnforced(Context context) {
        Cursor query = context.getContentResolver().query(Telephony.Carriers.ENFORCE_MANAGED_URI, null, null, null, null);
        if (query != null) {
            try {
                if (query.getCount() == 1) {
                    query.moveToFirst();
                    boolean z = query.getInt(0) > 0;
                    query.close();
                    return z;
                }
            } catch (Throwable th) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (query != null) {
            query.close();
        }
        return false;
    }

    private static boolean isGsmBasicOptions(Context context, int i) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
        if (createForSubscriptionId.getPhoneType() == 1) {
            return true;
        }
        return (configForSubId == null || configForSubId.getBoolean("hide_carrier_network_settings_bool") || !configForSubId.getBoolean("world_phone_bool")) ? false : true;
    }

    public static boolean isGsmOptions(Context context, int i) {
        if (i == -1) {
            return false;
        }
        if (isGsmBasicOptions(context, i)) {
            return true;
        }
        int networkTypeFromRaf = getNetworkTypeFromRaf((int) ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).getAllowedNetworkTypesForReason(0));
        return isWorldMode(context, i) && (networkTypeFromRaf == 8 || networkTypeFromRaf == 9 || networkTypeFromRaf == 25 || networkTypeFromRaf == 26 || shouldSpeciallyUpdateGsmCdma(context, i));
    }

    public static boolean isMobileDataEnabled(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (telephonyManager.isDataEnabled()) {
            return true;
        }
        TelephonyManager createForSubscriptionId = telephonyManager.createForSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId());
        return createForSubscriptionId != null && createForSubscriptionId.isDataEnabled();
    }

    public static boolean isTdscdmaSupported(Context context, int i) {
        return isTdscdmaSupported(context, ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i));
    }

    private static boolean isTdscdmaSupported(Context context, TelephonyManager telephonyManager) {
        PersistableBundle config = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfig();
        if (config == null) {
            return false;
        }
        if (config.getBoolean("support_tdscdma_bool")) {
            return true;
        }
        ServiceState serviceState = telephonyManager.getServiceState();
        String operatorNumeric = serviceState != null ? serviceState.getOperatorNumeric() : null;
        String[] stringArray = config.getStringArray("support_tdscdma_roaming_networks_string_array");
        if (stringArray != null && operatorNumeric != null) {
            for (String str : stringArray) {
                if (operatorNumeric.equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isWfcProvisionedOnDevice(int i) {
        ProvisioningManager createForSubscriptionId = ProvisioningManager.createForSubscriptionId(i);
        if (createForSubscriptionId == null) {
            return true;
        }
        return createForSubscriptionId.getProvisioningStatusForCapability(1, 1);
    }

    public static boolean isWifiCallingEnabled(Context context, int i, WifiCallingQueryImsState wifiCallingQueryImsState, PhoneAccountHandle phoneAccountHandle) {
        if (phoneAccountHandle == null) {
            phoneAccountHandle = ((TelecomManager) context.getSystemService(TelecomManager.class)).getSimCallManagerForSubscription(i);
        }
        if (phoneAccountHandle != null) {
            return buildPhoneAccountConfigureIntent(context, phoneAccountHandle) != null;
        }
        if (wifiCallingQueryImsState == null) {
            wifiCallingQueryImsState = new WifiCallingQueryImsState(context, i);
        }
        return wifiCallingQueryImsState.isReadyToWifiCalling();
    }

    public static boolean isWorldMode(Context context, int i) {
        PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
        if (configForSubId == null) {
            return false;
        }
        return configForSubId.getBoolean("world_mode_enabled_bool");
    }

    public static void setContactDiscoveryEnabled(ImsManager imsManager, int i, boolean z) {
        ImsRcsManager imsRcsManager = getImsRcsManager(imsManager, i);
        if (imsRcsManager == null) {
            return;
        }
        try {
            imsRcsManager.getUceAdapter().setUceSettingEnabled(z);
        } catch (ImsException e) {
            Log.w("MobileNetworkUtils", "UCE service is not available: " + e.getMessage());
        }
    }

    public static void setMobileDataEnabled(Context context, int i, boolean z, boolean z2) {
        List<SubscriptionInfo> activeSubscriptionInfoList;
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        createForSubscriptionId.setDataEnabled(z);
        if (!z2 || (activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList()) == null) {
            return;
        }
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            if (subscriptionInfo.getSubscriptionId() != i && !subscriptionInfo.isOpportunistic()) {
                ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(subscriptionInfo.getSubscriptionId()).setDataEnabled(false);
            }
        }
    }

    private static String setSummaryResId(Context context, int i) {
        return context.getResources().getString(i);
    }

    public static boolean shouldDisplayNetworkSelectOptions(Context context, int i) {
        int networkTypeFromRaf;
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
        if (i == -1 || configForSubId == null || !configForSubId.getBoolean("operator_selection_expand_bool") || configForSubId.getBoolean("hide_carrier_network_settings_bool") || ((configForSubId.getBoolean("csp_enabled_bool") && !createForSubscriptionId.isManualNetworkSelectionAllowed()) || (((networkTypeFromRaf = getNetworkTypeFromRaf((int) createForSubscriptionId.getAllowedNetworkTypesForReason(0))) == 8 && isWorldMode(context, i)) || shouldSpeciallyUpdateGsmCdma(context, i)))) {
            return false;
        }
        if (isGsmBasicOptions(context, i)) {
            return true;
        }
        return isWorldMode(context, i) && networkTypeFromRaf == 9;
    }

    static boolean shouldSpeciallyUpdateGsmCdma(Context context, int i) {
        int networkTypeFromRaf = getNetworkTypeFromRaf((int) ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).getAllowedNetworkTypesForReason(0));
        return (networkTypeFromRaf == 17 || networkTypeFromRaf == 20 || networkTypeFromRaf == 15 || networkTypeFromRaf == 19 || networkTypeFromRaf == 22 || networkTypeFromRaf == 10) && !isTdscdmaSupported(context, i) && isWorldMode(context, i);
    }

    public static boolean showEuiccSettings(final Context context) {
        try {
            return ((Boolean) ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.android.settings.network.telephony.MobileNetworkUtils$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    Object showEuiccSettingsDetecting;
                    showEuiccSettingsDetecting = MobileNetworkUtils.showEuiccSettingsDetecting(context);
                    return showEuiccSettingsDetecting;
                }
            }).get(1L, TimeUnit.SECONDS)).booleanValue();
        } catch (InterruptedException | ExecutionException | TimeoutException unused) {
            Log.w("MobileNetworkUtils", "Accessing Euicc takes too long: +" + (SystemClock.elapsedRealtime() - SystemClock.elapsedRealtime()) + "ms");
            return false;
        }
    }

    public static Boolean showEuiccSettingsDetecting(Context context) {
        if (!((EuiccManager) context.getSystemService(EuiccManager.class)).isEnabled()) {
            Log.w("MobileNetworkUtils", "EuiccManager is not enabled.");
            return Boolean.FALSE;
        }
        ContentResolver contentResolver = context.getContentResolver();
        boolean contains = Arrays.asList(TextUtils.split(SystemProperties.get("ro.setupwizard.esim_cid_ignore", ""), ",")).contains(SystemProperties.get("ro.boot.cid", (String) null));
        boolean z = true;
        boolean z2 = SystemProperties.getBoolean("esim.enable_esim_system_ui_by_default", true);
        boolean z3 = Settings.Global.getInt(contentResolver, "euicc_provisioned", 0) != 0;
        boolean isDevelopmentSettingsEnabled = DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
        Log.i("MobileNetworkUtils", String.format("showEuiccSettings: esimIgnoredDevice: %b, enabledEsimUiByDefault: %b, euiccProvisioned: %b, inDeveloperMode: %b.", Boolean.valueOf(contains), Boolean.valueOf(z2), Boolean.valueOf(z3), Boolean.valueOf(isDevelopmentSettingsEnabled)));
        if (!z3 && ((contains || !isDevelopmentSettingsEnabled) && (contains || !z2 || !isCurrentCountrySupported(context)))) {
            z = false;
        }
        return Boolean.valueOf(z);
    }
}
