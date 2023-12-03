package com.android.settings.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.utils.LogUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/* loaded from: classes2.dex */
public class WifiConfigForSupplicant {
    private static final String[] WEP_KEY_VAR_NAMES = {"wep_key0", "wep_key1", "wep_key2", "wep_key3"};
    private static WifiConfigForSupplicant sInstance = null;

    /* loaded from: classes2.dex */
    private class WifiConfigInternal {
        private String allowedAuthAlgos;
        private String allowedGroupCiphers;
        private String allowedKeyMgmt;
        private String allowedPairwiseCiphers;
        private String allowedProtocols;
        private String configKey;
        private int creatorUid;
        private boolean hiddenSSID;
        private String psk;
        private int randomMac;
        private String ssid;
        private int wepTxKeyIndex;

        public WifiConfigInternal(WifiConfiguration wifiConfiguration) {
            this.configKey = wifiConfiguration.getKey();
            this.ssid = wifiConfiguration.SSID;
            this.psk = wifiConfiguration.preSharedKey;
            this.wepTxKeyIndex = wifiConfiguration.wepTxKeyIndex;
            this.hiddenSSID = wifiConfiguration.hiddenSSID;
            this.creatorUid = wifiConfiguration.creatorUid;
            this.randomMac = wifiConfiguration.macRandomizationSetting;
            this.allowedKeyMgmt = convertToHex(wifiConfiguration.allowedKeyManagement.toByteArray());
            this.allowedProtocols = convertToHex(wifiConfiguration.allowedProtocols.toByteArray());
            this.allowedAuthAlgos = convertToHex(wifiConfiguration.allowedAuthAlgorithms.toByteArray());
            this.allowedGroupCiphers = convertToHex(wifiConfiguration.allowedGroupCiphers.toByteArray());
            this.allowedPairwiseCiphers = convertToHex(wifiConfiguration.allowedPairwiseCiphers.toByteArray());
        }

        private String convertToHex(byte[] bArr) {
            StringBuilder sb = new StringBuilder(bArr.length * 2);
            for (byte b : bArr) {
                int i = (b >> 4) & 15;
                sb.append((char) (i >= 10 ? (i + 97) - 10 : i + 48));
                int i2 = b & 15;
                sb.append((char) (i2 >= 10 ? (i2 + 97) - 10 : i2 + 48));
            }
            return sb.toString();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("network={\n");
            sb.append("ConfigKey=" + this.configKey + "\n");
            sb.append("SSID=" + this.ssid + "\n");
            sb.append("PreSharedKey=" + this.psk + "\n");
            sb.append("WEPTxKeyIndex=" + this.wepTxKeyIndex + "\n");
            sb.append("HiddenSSID=" + this.hiddenSSID + "\n");
            sb.append("AllowedKeyMgmt=" + this.allowedKeyMgmt + "\n");
            sb.append("AllowedProtocols=" + this.allowedProtocols + "\n");
            sb.append("AllowedAuthAlgos=" + this.allowedAuthAlgos + "\n");
            sb.append("AllowedGroupCiphers=" + this.allowedGroupCiphers + "\n");
            sb.append("AllowedPairwiseCiphers=" + this.allowedPairwiseCiphers + "\n");
            sb.append("CreatorUid=" + this.creatorUid + "\n");
            sb.append("MacRandomizationSetting=" + this.randomMac + "\n");
            sb.append("}\n");
            return sb.toString();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x001c, code lost:
    
        if (r2 >= 'A') goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static int fromHex(char r2, boolean r3) throws java.lang.NumberFormatException {
        /*
            r0 = 57
            if (r2 > r0) goto La
            r0 = 48
            if (r2 < r0) goto La
            int r2 = r2 - r0
            return r2
        La:
            r0 = 97
            if (r2 < r0) goto L16
            r1 = 102(0x66, float:1.43E-43)
            if (r2 > r1) goto L16
        L12:
            int r2 = r2 + 10
            int r2 = r2 - r0
            return r2
        L16:
            r0 = 70
            if (r2 > r0) goto L1f
            r0 = 65
            if (r2 < r0) goto L1f
            goto L12
        L1f:
            if (r3 == 0) goto L23
            r2 = -1
            return r2
        L23:
            java.lang.NumberFormatException r3 = new java.lang.NumberFormatException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Bad hex-character: "
            r0.append(r1)
            r0.append(r2)
            java.lang.String r2 = r0.toString()
            r3.<init>(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConfigForSupplicant.fromHex(char, boolean):int");
    }

    public static WifiConfigForSupplicant getInstance() {
        synchronized (WifiConfigForSupplicant.class) {
            if (sInstance == null) {
                sInstance = new WifiConfigForSupplicant();
            }
        }
        return sInstance;
    }

    private byte[] hexToBytes(String str) {
        if ((str.length() & 1) == 1) {
            throw new NumberFormatException("Odd length hex string: " + str.length());
        }
        byte[] bArr = new byte[str.length() >> 1];
        int i = 0;
        for (int i2 = 0; i2 < str.length(); i2 += 2) {
            bArr[i] = (byte) (((fromHex(str.charAt(i2), false) & 15) << 4) | (fromHex(str.charAt(i2 + 1), false) & 15));
            i++;
        }
        return bArr;
    }

    private String parsePairValue(String str) {
        return str.substring(str.indexOf(61) + 1).trim();
    }

    private String parsePairValueRemoveDoubleQuotes(String str) {
        String parsePairValue = parsePairValue(str);
        return !TextUtils.isEmpty(parsePairValue) ? removeDoubleQuotes(parsePairValue) : parsePairValue;
    }

    private void parseString(String str, String[] strArr, BitSet bitSet) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        String replace = str.replace('-', '_');
        for (int i = 0; i < strArr.length; i++) {
            if (replace.contains(strArr[i])) {
                bitSet.set(i);
            }
        }
    }

    private String removeDoubleQuotes(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        int length = str.length();
        if (length > 1 && str.charAt(0) == '\"') {
            int i = length - 1;
            if (str.charAt(i) == '\"') {
                return str.substring(1, i);
            }
        }
        return str;
    }

    private void setWifiConfigurationField(String str, WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null || TextUtils.isEmpty(str)) {
            return;
        }
        String trim = str.trim();
        if (trim.startsWith("SSID") || trim.startsWith("ssid")) {
            wifiConfiguration.SSID = parsePairValueRemoveDoubleQuotes(trim);
            wifiConfiguration.SSID = "\"" + wifiConfiguration.SSID + "\"";
        } else if (trim.startsWith("BSSID") || trim.startsWith("bssid")) {
            wifiConfiguration.BSSID = parsePairValue(trim);
        } else if (trim.startsWith("PreSharedKey") || trim.startsWith("psk")) {
            wifiConfiguration.preSharedKey = parsePairValue(trim);
        } else {
            String[] strArr = WEP_KEY_VAR_NAMES;
            if (trim.startsWith(strArr[0])) {
                wifiConfiguration.wepKeys[0] = parsePairValue(trim);
            } else if (trim.startsWith(strArr[1])) {
                wifiConfiguration.wepKeys[1] = parsePairValue(trim);
            } else if (trim.startsWith(strArr[2])) {
                wifiConfiguration.wepKeys[2] = parsePairValue(trim);
            } else if (trim.startsWith(strArr[3])) {
                wifiConfiguration.wepKeys[3] = parsePairValue(trim);
            } else if (trim.startsWith("WEPTxKeyIndex") || trim.startsWith("wep_tx_keyidx")) {
                wifiConfiguration.wepTxKeyIndex = Integer.valueOf(parsePairValue(trim)).intValue();
            } else if (trim.startsWith("HiddenSSID") || trim.startsWith("scan_ssid")) {
                wifiConfiguration.hiddenSSID = Boolean.valueOf(parsePairValue(trim)).booleanValue();
            } else if (trim.startsWith("AllowedKeyMgmt")) {
                wifiConfiguration.allowedKeyManagement = BitSet.valueOf(hexToBytes(parsePairValue(trim)));
            } else if (trim.startsWith("key_mgmt")) {
                parseString(parsePairValue(trim), WifiConfiguration.KeyMgmt.strings, wifiConfiguration.allowedKeyManagement);
            } else if (trim.startsWith("EapMethod")) {
                wifiConfiguration.enterpriseConfig.setEapMethod(Integer.valueOf(parsePairValue(trim)).intValue());
            } else if (trim.startsWith("eap")) {
                TextUtils.isEmpty(parsePairValue(trim));
            } else if (trim.startsWith("Phase2Method")) {
                wifiConfiguration.enterpriseConfig.setEapMethod(Integer.valueOf(parsePairValueRemoveDoubleQuotes(trim)).intValue());
            } else if (trim.startsWith("phase2")) {
                TextUtils.isEmpty(parsePairValueRemoveDoubleQuotes(trim));
            } else if (trim.startsWith("Identity") || trim.startsWith("identity")) {
                wifiConfiguration.enterpriseConfig.setIdentity(parsePairValueRemoveDoubleQuotes(trim));
            } else if (trim.startsWith("AnonIdentity") || trim.startsWith("anonymous_identity")) {
                wifiConfiguration.enterpriseConfig.setAnonymousIdentity(parsePairValueRemoveDoubleQuotes(trim));
            } else if (trim.startsWith("Password") || trim.startsWith("password")) {
                wifiConfiguration.enterpriseConfig.setPassword(parsePairValueRemoveDoubleQuotes(trim));
            } else if (trim.startsWith("ClientCert") || trim.startsWith("client_cert")) {
                wifiConfiguration.enterpriseConfig.setClientCertificateAlias(parsePairValueRemoveDoubleQuotes(trim));
            } else if (trim.startsWith("PrivateKeyId") || trim.startsWith("key_id")) {
                wifiConfiguration.enterpriseConfig.setClientCertificateAlias(parsePairValueRemoveDoubleQuotes(trim));
            } else if (trim.startsWith("CaCert") || trim.startsWith("ca_cert")) {
                wifiConfiguration.enterpriseConfig.setCaCertificateAliases(new String[]{parsePairValueRemoveDoubleQuotes(trim)});
            } else if (trim.startsWith("CreatorUid")) {
                wifiConfiguration.creatorUid = Integer.valueOf(parsePairValue(trim)).intValue();
            } else if (trim.startsWith("MacRandomizationSetting")) {
                wifiConfiguration.macRandomizationSetting = Integer.valueOf(parsePairValue(trim)).intValue();
            }
        }
    }

    public List<String> getConfiguredNetworks(Context context) {
        ArrayList arrayList = new ArrayList();
        for (WifiConfiguration wifiConfiguration : ((WifiManager) context.getSystemService("wifi")).getPrivilegedConfiguredNetworks()) {
            if (!wifiConfiguration.allowedKeyManagement.get(2) && !wifiConfiguration.allowedKeyManagement.get(3)) {
                WifiConfigInternal wifiConfigInternal = new WifiConfigInternal(wifiConfiguration);
                arrayList.add(wifiConfigInternal.toString());
                LogUtil.logCloudSync("SupplicantManager", "getConfiguredNetworks " + wifiConfigInternal.toString());
            }
        }
        return arrayList;
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x00ac, code lost:
    
        if (r3 == null) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00ae, code lost:
    
        r3.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00d1, code lost:
    
        if (r3 == null) goto L53;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.HashMap<java.lang.String, android.net.wifi.WifiConfiguration> getRestoreWifiConfigs(android.content.Context r10) {
        /*
            r9 = this;
            java.lang.String r0 = ", "
            java.lang.String r1 = "SupplicantManager"
            java.io.File r2 = new java.io.File
            java.io.File r10 = r10.getCacheDir()
            java.io.File r10 = r10.getParentFile()
            java.lang.String r3 = "wpa_supplicant.tmp"
            r2.<init>(r10, r3)
            boolean r10 = r2.exists()
            r3 = 0
            if (r10 != 0) goto L1c
            return r3
        L1c:
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L8d java.io.FileNotFoundException -> Lb2
            java.io.FileReader r5 = new java.io.FileReader     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L8d java.io.FileNotFoundException -> Lb2
            r5.<init>(r2)     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L8d java.io.FileNotFoundException -> Lb2
            r4.<init>(r5)     // Catch: java.lang.Throwable -> L8b java.io.IOException -> L8d java.io.FileNotFoundException -> Lb2
            java.lang.String r5 = r4.readLine()     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            r6 = r3
        L30:
            if (r5 == 0) goto L7b
            java.lang.String r7 = "[ \\t]*network=\\{"
            boolean r7 = r5.matches(r7)     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            if (r7 == 0) goto L40
            android.net.wifi.WifiConfiguration r6 = new android.net.wifi.WifiConfiguration     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            r6.<init>()     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            goto L71
        L40:
            java.lang.String r7 = "[ \\t]*\\}"
            boolean r7 = r5.matches(r7)     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            if (r7 == 0) goto L71
            if (r6 == 0) goto L71
            r6.convertLegacyFieldsToSecurityParamsIfNeeded()     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            boolean r7 = r6.needsPreSharedKey()     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            if (r7 != 0) goto L55
            r6.preSharedKey = r3     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
        L55:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            r7.<init>()     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            java.lang.String r8 = "getRestoreWifiConfigs: "
            r7.append(r8)     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            r7.append(r6)     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            java.lang.String r7 = r7.toString()     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            com.android.settings.utils.LogUtil.logCloudSync(r1, r7)     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            java.lang.String r7 = r6.getKey()     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            r10.put(r7, r6)     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            r6 = r3
        L71:
            if (r6 == 0) goto L76
            r9.setWifiConfigurationField(r5, r6)     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
        L76:
            java.lang.String r5 = r4.readLine()     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            goto L30
        L7b:
            r2.delete()     // Catch: java.lang.Throwable -> L82 java.io.IOException -> L85 java.io.FileNotFoundException -> L88
            r4.close()     // Catch: java.io.IOException -> Ld4
            goto Ld4
        L82:
            r9 = move-exception
            r3 = r4
            goto Ld5
        L85:
            r9 = move-exception
            r3 = r4
            goto L8e
        L88:
            r9 = move-exception
            r3 = r4
            goto Lb3
        L8b:
            r9 = move-exception
            goto Ld5
        L8d:
            r9 = move-exception
        L8e:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L8b
            r4.<init>()     // Catch: java.lang.Throwable -> L8b
            java.lang.String r5 = "Could not read "
            r4.append(r5)     // Catch: java.lang.Throwable -> L8b
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L8b
            r4.append(r2)     // Catch: java.lang.Throwable -> L8b
            r4.append(r0)     // Catch: java.lang.Throwable -> L8b
            r4.append(r9)     // Catch: java.lang.Throwable -> L8b
            java.lang.String r9 = r4.toString()     // Catch: java.lang.Throwable -> L8b
            android.util.Log.e(r1, r9)     // Catch: java.lang.Throwable -> L8b
            if (r3 == 0) goto Ld4
        Lae:
            r3.close()     // Catch: java.io.IOException -> Ld4
            goto Ld4
        Lb2:
            r9 = move-exception
        Lb3:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L8b
            r4.<init>()     // Catch: java.lang.Throwable -> L8b
            java.lang.String r5 = "Could not open "
            r4.append(r5)     // Catch: java.lang.Throwable -> L8b
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L8b
            r4.append(r2)     // Catch: java.lang.Throwable -> L8b
            r4.append(r0)     // Catch: java.lang.Throwable -> L8b
            r4.append(r9)     // Catch: java.lang.Throwable -> L8b
            java.lang.String r9 = r4.toString()     // Catch: java.lang.Throwable -> L8b
            android.util.Log.e(r1, r9)     // Catch: java.lang.Throwable -> L8b
            if (r3 == 0) goto Ld4
            goto Lae
        Ld4:
            return r10
        Ld5:
            if (r3 == 0) goto Lda
            r3.close()     // Catch: java.io.IOException -> Lda
        Lda:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConfigForSupplicant.getRestoreWifiConfigs(android.content.Context):java.util.HashMap");
    }

    public WifiConfiguration getWifiConfiguration(WifiConfiguration wifiConfiguration, Context context) {
        if (context != null && wifiConfiguration != null) {
            for (WifiConfiguration wifiConfiguration2 : ((WifiManager) context.getSystemService(WifiManager.class)).getPrivilegedConfiguredNetworks()) {
                if (wifiConfiguration2.networkId == wifiConfiguration.networkId) {
                    return wifiConfiguration2;
                }
            }
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r3v1, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r3v11 */
    /* JADX WARN: Type inference failed for: r3v12 */
    /* JADX WARN: Type inference failed for: r3v13 */
    /* JADX WARN: Type inference failed for: r3v14 */
    /* JADX WARN: Type inference failed for: r3v15 */
    /* JADX WARN: Type inference failed for: r3v16 */
    /* JADX WARN: Type inference failed for: r3v17, types: [java.io.BufferedWriter] */
    /* JADX WARN: Type inference failed for: r3v18 */
    /* JADX WARN: Type inference failed for: r3v19 */
    /* JADX WARN: Type inference failed for: r3v3 */
    /* JADX WARN: Type inference failed for: r3v4 */
    /* JADX WARN: Type inference failed for: r3v5 */
    /* JADX WARN: Type inference failed for: r3v6, types: [java.io.BufferedWriter] */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r3v8 */
    /* JADX WARN: Type inference failed for: r3v9, types: [java.io.BufferedWriter] */
    public void storeWifiConfigs(Context context, FileDescriptor fileDescriptor) {
        ?? file;
        File file2 = new File(context.getCacheDir().getParentFile(), "wpa_supplicant.tmp");
        if (!file2.exists()) {
            try {
                file2.createNewFile();
            } catch (IOException e) {
                ?? sb = new StringBuilder();
                sb.append("Could not create ");
                file = file2.toString();
                sb.append(file);
                sb.append(", ");
                sb.append(e);
                Log.e("SupplicantManager", sb.toString());
            }
        }
        BufferedReader bufferedReader = null;
        try {
            try {
                try {
                    BufferedReader bufferedReader2 = new BufferedReader(new FileReader(fileDescriptor));
                    try {
                        file = new BufferedWriter(new FileWriter(file2));
                        while (true) {
                            try {
                                String readLine = bufferedReader2.readLine();
                                if (readLine == null) {
                                    break;
                                }
                                file.write(readLine);
                                file.write("\n");
                            } catch (FileNotFoundException e2) {
                                e = e2;
                                bufferedReader = bufferedReader2;
                                file = file;
                                Log.e("SupplicantManager", "Could not open " + fileDescriptor + ", " + e);
                                if (bufferedReader != null) {
                                    bufferedReader.close();
                                }
                                if (file == 0) {
                                    return;
                                }
                                file.close();
                            } catch (IOException e3) {
                                e = e3;
                                bufferedReader = bufferedReader2;
                                file = file;
                                Log.e("SupplicantManager", "Could not read " + fileDescriptor + ", " + e);
                                if (bufferedReader != null) {
                                    bufferedReader.close();
                                }
                                if (file == 0) {
                                    return;
                                }
                                file.close();
                            } catch (Throwable th) {
                                th = th;
                                bufferedReader = bufferedReader2;
                                if (bufferedReader != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (IOException unused) {
                                        throw th;
                                    }
                                }
                                if (file != 0) {
                                    file.close();
                                }
                                throw th;
                            }
                        }
                        file.flush();
                        bufferedReader2.close();
                    } catch (FileNotFoundException e4) {
                        e = e4;
                        file = 0;
                    } catch (IOException e5) {
                        e = e5;
                        file = 0;
                    } catch (Throwable th2) {
                        th = th2;
                        file = 0;
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            } catch (FileNotFoundException e6) {
                e = e6;
                file = 0;
            } catch (IOException e7) {
                e = e7;
                file = 0;
            } catch (Throwable th4) {
                th = th4;
                file = 0;
            }
            file.close();
        } catch (IOException unused2) {
        }
    }
}
