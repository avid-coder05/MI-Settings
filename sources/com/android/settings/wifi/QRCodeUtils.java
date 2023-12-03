package com.android.settings.wifi;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.HashMap;
import miui.provider.ExtraContacts;

/* loaded from: classes2.dex */
public class QRCodeUtils {
    private static String addBackSlash(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c == ';') {
                sb.append('\\');
            }
            if (c == '\\') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static Bitmap getQrcode(Context context, String str) {
        try {
            HashMap hashMap = new HashMap(2);
            hashMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.qrcode_size);
            BitMatrix encode = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, dimensionPixelSize, dimensionPixelSize, hashMap);
            int width = encode.getWidth();
            int height = encode.getHeight();
            int[] iArr = new int[width * height];
            int color = context.getResources().getColor(R.color.qrcode_pixel_color);
            int color2 = context.getResources().getColor(R.color.qrcode_other_color);
            for (int i = 0; i < height; i++) {
                int i2 = i * width;
                for (int i3 = 0; i3 < width; i3++) {
                    iArr[i2 + i3] = encode.get(i3, i) ? color : color2;
                }
            }
            Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            createBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
            return createBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getQrcodeText(String str, String str2, String str3, boolean z) {
        return "WIFI:T:" + str + ";P:" + str2 + ";S:" + str3 + ";H:" + z + ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION;
    }

    private static String getSecurityString(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.allowedKeyManagement.get(8)) {
            return "SAE";
        }
        if (wifiConfiguration.allowedKeyManagement.get(1)) {
            return "WPA";
        }
        if (wifiConfiguration.allowedKeyManagement.get(2) || wifiConfiguration.allowedKeyManagement.get(3) || wifiConfiguration.wepKeys[0] == null) {
            return null;
        }
        return "WEP";
    }

    public static Bitmap getTetherQrcode(Context context, SoftApConfiguration softApConfiguration) {
        String tetherQrcodeText = getTetherQrcodeText(context, softApConfiguration);
        if (TextUtils.isEmpty(tetherQrcodeText)) {
            return null;
        }
        return getQrcode(context, tetherQrcodeText);
    }

    public static String getTetherQrcodeText(Context context, SoftApConfiguration softApConfiguration) {
        int securityTypeIndex = WifiApDialog.getSecurityTypeIndex(softApConfiguration);
        String str = securityTypeIndex >= 0 ? context.getResources().getStringArray(MiuiUtils.getInstance().isWpa3SoftApSupport(context) ? R.array.wifi_ap_security_with_sae : R.array.wifi_ap_security)[securityTypeIndex] : "";
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (str.startsWith("WPA3")) {
            str = "SAE";
        } else if (str.startsWith("WPA")) {
            str = "WPA";
        }
        String ssid = softApConfiguration.getSsid();
        String passphrase = softApConfiguration.getPassphrase();
        return getQrcodeText(str, addBackSlash(TextUtils.isEmpty(passphrase) ? "" : passphrase), addBackSlash(ssid), softApConfiguration.isHiddenSsid());
    }

    public static Bitmap getWifiQrcode(Context context, WifiConfiguration wifiConfiguration) {
        String wifiQrcodeText = getWifiQrcodeText(context, wifiConfiguration);
        if (TextUtils.isEmpty(wifiQrcodeText)) {
            return null;
        }
        return getQrcode(context, wifiQrcodeText);
    }

    public static String getWifiQrcodeText(Context context, WifiConfiguration wifiConfiguration) {
        String[] strArr;
        if (wifiConfiguration == null) {
            Log.e("QRCodeUtils", "getWifiQrcodeText: wifiEntryConfig is empty, return null!");
            return null;
        }
        String securityString = getSecurityString(wifiConfiguration);
        if (TextUtils.isEmpty(securityString)) {
            return null;
        }
        String removeDoubleQuotes = removeDoubleQuotes(wifiConfiguration.SSID);
        WifiConfiguration wifiConfigurationWithPsk = WifiConfigurationManager.getInstance(context).getWifiConfigurationWithPsk(wifiConfiguration);
        if (wifiConfigurationWithPsk == null) {
            wifiConfigurationWithPsk = WifiConfigForSupplicant.getInstance().getWifiConfiguration(wifiConfiguration, context);
        }
        if (wifiConfigurationWithPsk == null) {
            return null;
        }
        String str = wifiConfiguration.preSharedKey;
        if ("WEP".equals(securityString) && (strArr = wifiConfiguration.wepKeys) != null) {
            str = strArr[0];
        }
        if (TextUtils.isEmpty(str)) {
            str = "";
        }
        return getQrcodeText(securityString, addBackSlash(removeDoubleQuotes(str)), addBackSlash(removeDoubleQuotes), wifiConfiguration.hiddenSSID);
    }

    public static String removeDoubleQuotes(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int length = str.length();
        if (length <= 1 || str.charAt(0) != '\"') {
            return str;
        }
        int i = length - 1;
        return str.charAt(i) == '\"' ? str.substring(1, i) : str;
    }
}
