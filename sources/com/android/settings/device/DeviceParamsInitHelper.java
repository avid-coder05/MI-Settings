package com.android.settings.device;

import android.accounts.Account;
import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.aidl.IRemoteGetDeviceInfoService;
import com.android.settings.device.MiuiAboutPhoneUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import miui.accounts.ExtraAccountManager;

/* loaded from: classes.dex */
public class DeviceParamsInitHelper {
    private Context mContext;
    private IRemoteGetDeviceInfoService mRemoteService;

    /* renamed from: com.android.settings.device.DeviceParamsInitHelper$1  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$device$MiuiAboutPhoneUtils$PhoneConfigurationType;

        static {
            int[] iArr = new int[MiuiAboutPhoneUtils.PhoneConfigurationType.values().length];
            $SwitchMap$com$android$settings$device$MiuiAboutPhoneUtils$PhoneConfigurationType = iArr;
            try {
                iArr[MiuiAboutPhoneUtils.PhoneConfigurationType.STANDARD_CONFIGURATION_VERSION.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$device$MiuiAboutPhoneUtils$PhoneConfigurationType[MiuiAboutPhoneUtils.PhoneConfigurationType.HIGH_CONFIGURATION_VERSION.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$device$MiuiAboutPhoneUtils$PhoneConfigurationType[MiuiAboutPhoneUtils.PhoneConfigurationType.ENJOY_VERSIION.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public DeviceParamsInitHelper(Context context, IRemoteGetDeviceInfoService iRemoteGetDeviceInfoService) {
        this.mContext = context;
        this.mRemoteService = iRemoteGetDeviceInfoService;
    }

    public void initCameraParams(boolean z) {
        if (this.mRemoteService == null) {
            Log.e("DeviceParamsInitHelper", "initCameraParams remoteService is null");
            return;
        }
        HashMap hashMap = new HashMap();
        try {
            Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(this.mContext);
            if (xiaomiAccount != null) {
                hashMap.put("miId", URLEncoder.encode(MD5Util.encode(xiaomiAccount.name), "UTF-8"));
            }
            String str = Build.DEVICE;
            hashMap.put("device", URLEncoder.encode(str, "UTF-8"));
            if (z) {
                str = Build.MODEL;
            }
            hashMap.put("model", URLEncoder.encode(str, "UTF-8"));
            hashMap.put("product", URLEncoder.encode(Build.PRODUCT, "UTF-8"));
        } catch (UnsupportedEncodingException unused) {
            Log.e("DeviceParamsInitHelper", "UnsupportedEncodingException");
        }
        Locale locale = Locale.US;
        hashMap.put("langType", locale.getLanguage() + locale.getCountry());
        MiuiAboutPhoneUtils.PhoneConfigurationType phoneConfigurationType = MiuiAboutPhoneUtils.PhoneConfigurationType.STANDARD_CONFIGURATION_VERSION;
        try {
            phoneConfigurationType = MiuiAboutPhoneUtils.getInstance(this.mContext).getPhoneConfigurationType();
        } catch (Exception unused2) {
            Log.e("DeviceParamsInitHelper", "unable to get phoneConfigurationType");
        }
        int i = AnonymousClass1.$SwitchMap$com$android$settings$device$MiuiAboutPhoneUtils$PhoneConfigurationType[phoneConfigurationType.ordinal()];
        if (i == 1) {
            hashMap.put("version", "0");
        } else if (i == 2) {
            hashMap.put("version", "1");
        } else if (i != 3) {
            hashMap.put("version", "0");
        } else {
            hashMap.put("version", "2");
        }
        hashMap.put("region", SystemProperties.get("ro.miui.region", "CN"));
        hashMap.put("type", "camera");
        try {
            this.mRemoteService.getDeviceInfo(1, hashMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void initDeviceParams(boolean z) {
        if (this.mRemoteService == null) {
            Log.e("DeviceParamsInitHelper", "initDeviceParams remoteService is null");
            return;
        }
        HashMap hashMap = new HashMap();
        try {
            String deviceId = ((TelephonyManager) this.mContext.getSystemService("phone")).getDeviceId();
            if (!TextUtils.isEmpty(deviceId)) {
                hashMap.put("imei", URLEncoder.encode(MD5Util.encode(deviceId), "UTF-8"));
            }
            String str = Build.DEVICE;
            hashMap.put("device", URLEncoder.encode(str, "UTF-8"));
            hashMap.put("model", z ? URLEncoder.encode(Build.MODEL, "UTF-8") : URLEncoder.encode(str, "UTF-8"));
            hashMap.put("product", URLEncoder.encode(Build.PRODUCT, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MiuiAboutPhoneUtils.PhoneConfigurationType phoneConfigurationType = MiuiAboutPhoneUtils.PhoneConfigurationType.STANDARD_CONFIGURATION_VERSION;
        try {
            phoneConfigurationType = MiuiAboutPhoneUtils.getInstance(this.mContext).getPhoneConfigurationType();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        Locale locale = Locale.US;
        hashMap.put("langType", locale.getLanguage() + locale.getCountry());
        int i = AnonymousClass1.$SwitchMap$com$android$settings$device$MiuiAboutPhoneUtils$PhoneConfigurationType[phoneConfigurationType.ordinal()];
        if (i == 1) {
            hashMap.put("version", "0");
        } else if (i == 2) {
            hashMap.put("version", "1");
        } else if (i != 3) {
            hashMap.put("version", "0");
        } else {
            hashMap.put("version", "2");
        }
        try {
            this.mRemoteService.getDeviceInfo(0, hashMap);
        } catch (RemoteException e3) {
            e3.printStackTrace();
        }
    }

    public void initSoundParams() {
        if (this.mRemoteService == null) {
            Log.e("DeviceParamsInitHelper", "initSoundParams remoteService is null");
            return;
        }
        HashMap hashMap = new HashMap();
        try {
            String deviceId = ((TelephonyManager) this.mContext.getSystemService("phone")).getDeviceId();
            if (!TextUtils.isEmpty(deviceId)) {
                hashMap.put("imei", URLEncoder.encode(MD5Util.encode(deviceId), "UTF-8"));
            }
            hashMap.put("device", URLEncoder.encode(Build.DEVICE, "UTF-8"));
            hashMap.put("model", URLEncoder.encode(Build.MODEL, "UTF-8"));
            hashMap.put("product", URLEncoder.encode(Build.PRODUCT, "UTF-8"));
        } catch (UnsupportedEncodingException unused) {
            Log.e("DeviceParamsInitHelper", "UnsupportedEncodingException");
        }
        Locale locale = Locale.US;
        hashMap.put("langType", locale.getLanguage() + locale.getCountry());
        hashMap.put("version", "0");
        try {
            this.mRemoteService.getDeviceInfo(2, hashMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
