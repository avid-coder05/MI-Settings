package com.android.wifitrackerlib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.ConfigParser;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.PasspointR1Provider;
import android.net.wifi.hotspot2.ProvisioningCallback;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.miui.cloudservice.IPasspointKeyInterface;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import miui.cloud.Constants;

/* loaded from: classes2.dex */
public class WifiPasspointProvision {
    private static final Random RANDOM = new Random();
    private static WifiPasspointProvision sInstance;
    private ServiceConnection mConnection = new ServiceConnection() { // from class: com.android.wifitrackerlib.WifiPasspointProvision.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WifiPasspointProvision.this.mIPasspointKeyInterface = IPasspointKeyInterface.Stub.asInterface(iBinder);
            Log.i("WifiPasspointProvision", "onServiceConnected");
            if (WifiPasspointProvision.this.mIPasspointKeyInterface == null) {
                Log.i("WifiPasspointProvision", "mIPasspointKeyInterface == null");
            } else {
                Log.i("WifiPasspointProvision", "mIPasspointKeyInterface != null");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("WifiPasspointProvision", "onServiceDisconnected");
        }
    };
    private Context mContext;
    private IPasspointKeyInterface mIPasspointKeyInterface;
    private PasspointConfiguration mPasspointConfig;
    private WifiManager mWifiManager;

    public WifiPasspointProvision(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mWifiManager = (WifiManager) applicationContext.getSystemService("wifi");
    }

    public static String bytesToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder("");
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static WifiPasspointProvision getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WifiPasspointProvision(context);
        }
        return sInstance;
    }

    private String getPassword() {
        String signMac = signMac();
        if (signMac != null) {
            return new String(Base64.encode(signMac.getBytes(StandardCharsets.UTF_8), 0), StandardCharsets.UTF_8);
        }
        return null;
    }

    public static String getUserName(Context context) {
        String[] factoryMacAddresses = ((WifiManager) context.getSystemService("wifi")).getFactoryMacAddresses();
        String str = "";
        String str2 = (factoryMacAddresses == null || factoryMacAddresses.length <= 0) ? "" : factoryMacAddresses[0];
        if (TextUtils.isEmpty(str2)) {
            byte[] bArr = new byte[6];
            RANDOM.nextBytes(bArr);
            str2 = bytesToHexString(bArr);
            Log.e("WifiPasspointProvision", "get mac address failure, so use random one");
        }
        if (str2 != null) {
            str = "XIAOMI:" + str2.replace(":", "");
        }
        return str.toUpperCase();
    }

    public static byte[] hexStringToBytes(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        String upperCase = str.toUpperCase();
        int length = upperCase.length() / 2;
        char[] charArray = upperCase.toCharArray();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) (charToByte(charArray[i2 + 1]) | (charToByte(charArray[i2]) << 4));
        }
        return bArr;
    }

    public static boolean isPasspointR1Supported() {
        return !SystemProperties.get("ro.product.mod_device", "").contains("_global") && SystemProperties.getInt("ro.vendor.net.enable_passpoint_r1", 0) == 1;
    }

    private String signMac() {
        try {
            byte[] hexStringToBytes = hexStringToBytes(this.mIPasspointKeyInterface.getPassword(getUserName(this.mContext)));
            if (hexStringToBytes == null) {
                Log.e("WifiPasspointProvision", "Failed to get passwd");
                return null;
            }
            try {
                return Base64.encodeToString(new String(hexStringToBytes, "ISO-8859-1").substring(0, 12).getBytes("ISO-8859-1"), 2);
            } catch (UnsupportedEncodingException unused) {
                Log.e("WifiPasspointProvision", "Unsupported Encoding");
                return null;
            }
        } catch (RemoteException | NullPointerException unused2) {
            Log.e("WifiPasspointProvision", "get passwd fail: ");
            return null;
        }
    }

    public boolean addOrUpdatePasspointConfiguration(PasspointConfiguration passpointConfiguration) {
        boolean z;
        try {
            this.mWifiManager.addOrUpdatePasspointConfiguration(passpointConfiguration);
            this.mWifiManager.setPasspointMeteredOverride(passpointConfiguration.getHomeSp().getFqdn(), 2);
            z = true;
        } catch (RuntimeException e) {
            Log.w("WifiPasspointProvision", "Caught exception while installing wifi config: " + e);
            z = false;
        }
        if (z) {
            Log.d("WifiPasspointProvision", "installing wifi config sucessfully. friendlyName:" + this.mPasspointConfig.getHomeSp().getFriendlyName());
        }
        return z;
    }

    public void bindPasspointKeyService() {
        if (this.mIPasspointKeyInterface != null) {
            return;
        }
        Intent intent = new Intent("com.miui.cloudservice.PasspointService");
        intent.setClassName(Constants.CLOUDSERVICE_PACKAGE_NAME, "com.miui.cloudservice.alipay.provision.PasspointService");
        this.mContext.bindService(intent, this.mConnection, 1);
    }

    public PasspointConfiguration createPasspointConfig() {
        Credential.UserCredential userCredential;
        PasspointConfiguration createPasspointConfigurationPerTemplate = createPasspointConfigurationPerTemplate();
        if (createPasspointConfigurationPerTemplate == null) {
            Log.d("WifiPasspointProvision", "failed to build passpoint configuration from template!");
            return null;
        }
        Credential credential = createPasspointConfigurationPerTemplate.getCredential();
        if (credential != null && (userCredential = credential.getUserCredential()) != null) {
            userCredential.setUsername(getUserName(this.mContext));
            String password = getPassword();
            if (password == null) {
                Log.e("WifiPasspointProvision", "failure to get password!");
                return null;
            }
            userCredential.setPassword(password);
            credential.setUserCredential(userCredential);
            createPasspointConfigurationPerTemplate.setCredential(credential);
        }
        return createPasspointConfigurationPerTemplate;
    }

    public PasspointConfiguration createPasspointConfigurationPerTemplate() {
        byte[] parseFile = parseFile(new File("vendor/etc/wifi/passpointProfile.conf"));
        if (parseFile != null) {
            this.mPasspointConfig = ConfigParser.parsePasspointConfig("application/x-wifi-config", parseFile);
        } else {
            Log.e("WifiPasspointProvision", "Passpoint Profile is null!");
        }
        if (this.mPasspointConfig == null) {
            Log.e("WifiPasspointProvision", "failed to build passpoint configuration!");
        }
        return this.mPasspointConfig;
    }

    public byte[] parseFile(File file) {
        FileInputStream fileInputStream;
        byte[] bArr;
        BufferedInputStream bufferedInputStream;
        BufferedInputStream bufferedInputStream2 = null;
        r0 = null;
        byte[] bArr2 = null;
        r0 = null;
        r0 = null;
        bufferedInputStream2 = null;
        BufferedInputStream bufferedInputStream3 = null;
        BufferedInputStream bufferedInputStream4 = null;
        try {
            try {
                fileInputStream = new FileInputStream(file);
                try {
                    bufferedInputStream = new BufferedInputStream(fileInputStream);
                } catch (FileNotFoundException e) {
                    e = e;
                    bArr = null;
                } catch (IOException e2) {
                    e = e2;
                    bArr = null;
                }
            } catch (Throwable th) {
                th = th;
            }
            try {
                int length = (int) file.length();
                bArr2 = new byte[length];
                int read = bufferedInputStream.read(bArr2);
                if (length != read) {
                    Log.w("WifiPasspointProvision", "parse passpoint file, file len: " + length + ", buffer len: " + read);
                }
                try {
                    bufferedInputStream.close();
                    fileInputStream.close();
                    return bArr2;
                } catch (IOException e3) {
                    Log.e("WifiPasspointProvision", e3.getMessage());
                    return bArr2;
                }
            } catch (FileNotFoundException e4) {
                e = e4;
                bArr = bArr2;
                bufferedInputStream3 = bufferedInputStream;
                e.printStackTrace();
                if (bufferedInputStream3 != null) {
                    try {
                        bufferedInputStream3.close();
                    } catch (IOException e5) {
                        e = e5;
                        Log.e("WifiPasspointProvision", e.getMessage());
                        return bArr;
                    }
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                return bArr;
            } catch (IOException e6) {
                e = e6;
                bArr = bArr2;
                bufferedInputStream4 = bufferedInputStream;
                e.printStackTrace();
                if (bufferedInputStream4 != null) {
                    try {
                        bufferedInputStream4.close();
                    } catch (IOException e7) {
                        e = e7;
                        Log.e("WifiPasspointProvision", e.getMessage());
                        return bArr;
                    }
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                return bArr;
            } catch (Throwable th2) {
                th = th2;
                bufferedInputStream2 = bufferedInputStream;
                if (bufferedInputStream2 != null) {
                    try {
                        bufferedInputStream2.close();
                    } catch (IOException e8) {
                        Log.e("WifiPasspointProvision", e8.getMessage());
                        throw th;
                    }
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e9) {
            e = e9;
            fileInputStream = null;
            bArr = null;
        } catch (IOException e10) {
            e = e10;
            fileInputStream = null;
            bArr = null;
        } catch (Throwable th3) {
            th = th3;
            fileInputStream = null;
        }
    }

    public boolean startPasspointProvisioning() {
        PasspointConfiguration createPasspointConfig = createPasspointConfig();
        if (createPasspointConfig != null) {
            return addOrUpdatePasspointConfiguration(createPasspointConfig);
        }
        return false;
    }

    public void startR1SubscriptionProvisioning(PasspointR1Provider passpointR1Provider, ProvisioningCallback provisioningCallback) {
        if (provisioningCallback == null) {
            Log.d("WifiPasspointProvision", "no callback");
            return;
        }
        String domainName = passpointR1Provider.getDomainName();
        if (domainName == null) {
            provisioningCallback.onProvisioningFailure(2);
        } else if (!domainName.contains("exands.com")) {
            provisioningCallback.onProvisioningFailure(3);
        } else if (startPasspointProvisioning()) {
            provisioningCallback.onProvisioningComplete();
        }
    }
}
