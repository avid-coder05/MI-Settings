package com.android.settings.wifi;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Pair;
import com.android.settings.utils.LogUtil;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import miui.provider.Wifi;
import miui.securityspace.CrossUserUtils;

/* loaded from: classes2.dex */
public class WifiConfigurationManager {
    private static volatile WifiConfigurationManager sInstance;
    private Context mContext;
    private WifiManager mWifiManager;

    private WifiConfigurationManager(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mWifiManager = (WifiManager) applicationContext.getSystemService("wifi");
    }

    private String configKey(ScanResult scanResult) {
        String str = "\"" + scanResult.SSID + "\"";
        if (scanResult.capabilities.contains("WEP")) {
            str = str + "WEP";
        }
        if (scanResult.capabilities.contains("PSK")) {
            str = str + WifiConfiguration.KeyMgmt.strings[1];
        } else if (scanResult.capabilities.contains("SAE")) {
            str = str + WifiConfiguration.KeyMgmt.strings[8];
        }
        if (scanResult.capabilities.contains("EAP") || scanResult.capabilities.contains("IEEE8021X")) {
            str = str + WifiConfiguration.KeyMgmt.strings[2];
        }
        if (str.equals("\"" + scanResult.SSID + "\"")) {
            return str + WifiConfiguration.KeyMgmt.strings[0];
        }
        return str;
    }

    public static WifiConfigurationManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (WifiConfigForSupplicant.class) {
                if (sInstance == null) {
                    sInstance = new WifiConfigurationManager(context);
                }
            }
        }
        return sInstance;
    }

    private Pair getSelection(WifiConfiguration wifiConfiguration) {
        String[] strArr;
        String str;
        if (wifiConfiguration.allowedKeyManagement.get(8) || wifiConfiguration.allowedKeyManagement.get(1)) {
            String[] strArr2 = WifiConfiguration.KeyMgmt.strings;
            strArr = new String[]{removeDoubleQuotes(wifiConfiguration.SSID), strArr2[8], strArr2[1]};
            str = "ssid= ? and (keyMgmt= ? or keyMgmt= ?)";
        } else {
            strArr = new String[]{removeDoubleQuotes(wifiConfiguration.SSID), makeString(wifiConfiguration.allowedKeyManagement, WifiConfiguration.KeyMgmt.strings)};
            str = "ssid= ? and keyMgmt= ?";
        }
        return new Pair(str, strArr);
    }

    private static Uri getUserOwnerUri(Uri uri) {
        return UserHandle.myUserId() != 0 ? CrossUserUtils.addUserIdForUri(uri, 0) : uri;
    }

    private Map<String, WifiConfiguration> getWifiConfigs() {
        Cursor query = this.mContext.getContentResolver().query(getUserOwnerUri(Wifi.CONTENT_URI), null, "deleted=0", null, null);
        HashMap hashMap = new HashMap();
        if (query != null) {
            try {
                if (query.getCount() > 0) {
                    query.moveToPosition(-1);
                    while (query.moveToNext()) {
                        WifiConfiguration wifiConfiguration = getWifiConfiguration(query);
                        String str = wifiConfiguration.BSSID;
                        if (str != null && !str.isEmpty()) {
                            hashMap.put(wifiConfiguration.getKey(), wifiConfiguration);
                            LogUtil.logCloudSync("WifiConfigurationManager", "getWifiConfigs " + wifiConfiguration.getKey() + " BSSID=" + wifiConfiguration.BSSID);
                        }
                    }
                }
            } finally {
                query.close();
            }
        }
        if (query != null) {
        }
        return hashMap;
    }

    private WifiConfiguration getWifiConfiguration(Cursor cursor) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = convertToQuotedString(cursor.getString(cursor.getColumnIndex("ssid")));
        wifiConfiguration.BSSID = cursor.getString(cursor.getColumnIndex("bssid"));
        wifiConfiguration.lastConnected = cursor.getLong(cursor.getColumnIndex("adhoc"));
        String string = cursor.getString(cursor.getColumnIndex("psk"));
        wifiConfiguration.preSharedKey = string;
        if (TextUtils.isEmpty(string)) {
            wifiConfiguration.preSharedKey = null;
        }
        wifiConfiguration.wepKeys[0] = cursor.getString(cursor.getColumnIndex("wepkey0"));
        wifiConfiguration.wepKeys[1] = cursor.getString(cursor.getColumnIndex("wepkey1"));
        wifiConfiguration.wepKeys[2] = cursor.getString(cursor.getColumnIndex("wepkey2"));
        wifiConfiguration.wepKeys[3] = cursor.getString(cursor.getColumnIndex("wepkey3"));
        for (int i = 0; i < 4; i++) {
            if (TextUtils.isEmpty(wifiConfiguration.wepKeys[i])) {
                wifiConfiguration.wepKeys[i] = null;
            }
        }
        wifiConfiguration.wepTxKeyIndex = cursor.getInt(cursor.getColumnIndex("wep_tx_keyidx"));
        wifiConfiguration.hiddenSSID = cursor.getInt(cursor.getColumnIndex("scan_ssid")) == 0;
        parseString(cursor.getString(cursor.getColumnIndex("keyMgmt")), WifiConfiguration.KeyMgmt.strings, wifiConfiguration.allowedKeyManagement);
        if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("eap")))) {
            wifiConfiguration.enterpriseConfig.setEapMethod(0);
        }
        if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("phase2")))) {
            wifiConfiguration.enterpriseConfig.setPhase2Method(0);
        }
        String string2 = cursor.getString(cursor.getColumnIndex("identity"));
        if (!TextUtils.isEmpty(string2)) {
            wifiConfiguration.enterpriseConfig.setIdentity(string2);
        }
        String string3 = cursor.getString(cursor.getColumnIndex("anonymousIdentity"));
        if (!TextUtils.isEmpty(string3)) {
            wifiConfiguration.enterpriseConfig.setAnonymousIdentity(string3);
        }
        String string4 = cursor.getString(cursor.getColumnIndex("password"));
        if (!TextUtils.isEmpty(string4)) {
            wifiConfiguration.enterpriseConfig.setPassword(string4);
        }
        String string5 = cursor.getString(cursor.getColumnIndex("clientCert"));
        if (!TextUtils.isEmpty(string5)) {
            wifiConfiguration.enterpriseConfig.setClientCertificateAlias(string5);
        }
        String string6 = cursor.getString(cursor.getColumnIndex("caCert"));
        if (!TextUtils.isEmpty(string6)) {
            wifiConfiguration.enterpriseConfig.setCaCertificateAliases(new String[]{string6});
        }
        return wifiConfiguration;
    }

    private boolean hasWepKeys(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.wepKeys == null) {
            return false;
        }
        int i = 0;
        while (true) {
            String[] strArr = wifiConfiguration.wepKeys;
            if (i >= strArr.length) {
                return false;
            }
            if (strArr[i] != null) {
                return true;
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$getRestoreWifiConfigurations$0(WifiConfiguration wifiConfiguration, WifiConfiguration wifiConfiguration2) {
        return Long.compare(wifiConfiguration2.lastConnected, wifiConfiguration.lastConnected);
    }

    private String makeString(BitSet bitSet, String[] strArr) {
        StringBuffer stringBuffer = new StringBuffer();
        BitSet bitSet2 = bitSet.get(0, strArr.length);
        int i = -1;
        while (true) {
            i = bitSet2.nextSetBit(i + 1);
            if (i == -1) {
                break;
            }
            stringBuffer.append(strArr[i]);
            stringBuffer.append(' ');
        }
        if (bitSet2.cardinality() > 0) {
            stringBuffer.setLength(stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }

    private boolean match(Cursor cursor, WifiConfiguration wifiConfiguration) {
        cursor.moveToLast();
        WifiConfiguration wifiConfiguration2 = getWifiConfiguration(cursor);
        if (!wifiConfiguration2.allowedKeyManagement.get(0) || !wifiConfiguration.allowedKeyManagement.get(0)) {
            return ((wifiConfiguration2.allowedKeyManagement.get(1) && wifiConfiguration.allowedKeyManagement.get(1)) || ((wifiConfiguration2.allowedKeyManagement.get(4) && wifiConfiguration.allowedKeyManagement.get(4)) || (wifiConfiguration2.allowedKeyManagement.get(8) && wifiConfiguration.allowedKeyManagement.get(8)))) && TextUtils.equals(wifiConfiguration.preSharedKey, wifiConfiguration2.preSharedKey) && wifiConfiguration.lastConnected == wifiConfiguration2.lastConnected;
        } else if (wifiConfiguration2.wepKeys.length != wifiConfiguration.wepKeys.length) {
            return false;
        } else {
            int i = 0;
            while (true) {
                String[] strArr = wifiConfiguration.wepKeys;
                if (i >= strArr.length) {
                    return wifiConfiguration.lastConnected == wifiConfiguration2.lastConnected;
                } else if (!TextUtils.equals(strArr[i], wifiConfiguration2.wepKeys[i])) {
                    return false;
                } else {
                    i++;
                }
            }
        }
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

    /* JADX WARN: Code restructure failed: missing block: B:34:0x0072, code lost:
    
        if (r0 == null) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x007c, code lost:
    
        if (r0 == null) goto L41;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String readCertFile(java.lang.String r6) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r6)
            r1 = 0
            if (r0 != 0) goto L7f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.io.File r2 = android.os.Environment.getDataDirectory()
            java.lang.String r2 = r2.getAbsolutePath()
            r0.append(r2)
            java.lang.String r2 = "/data"
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            boolean r0 = r6.startsWith(r0)
            if (r0 == 0) goto L28
            goto L7f
        L28:
            java.io.File r0 = new java.io.File
            r0.<init>(r6)
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L5c java.io.IOException -> L6b java.io.FileNotFoundException -> L75
            r6.<init>(r0)     // Catch: java.lang.Throwable -> L5c java.io.IOException -> L6b java.io.FileNotFoundException -> L75
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch: java.lang.Throwable -> L53 java.io.IOException -> L58 java.io.FileNotFoundException -> L5a
            r2 = 512(0x200, float:7.17E-43)
            r0.<init>(r2)     // Catch: java.lang.Throwable -> L53 java.io.IOException -> L58 java.io.FileNotFoundException -> L5a
            byte[] r2 = new byte[r2]     // Catch: java.lang.Throwable -> L51 java.io.IOException -> L6d java.io.FileNotFoundException -> L77
        L3b:
            int r3 = r6.read(r2)     // Catch: java.lang.Throwable -> L51 java.io.IOException -> L6d java.io.FileNotFoundException -> L77
            if (r3 < 0) goto L46
            r4 = 0
            r0.write(r2, r4, r3)     // Catch: java.lang.Throwable -> L51 java.io.IOException -> L6d java.io.FileNotFoundException -> L77
            goto L3b
        L46:
            java.lang.String r1 = r0.toString()     // Catch: java.lang.Throwable -> L51 java.io.IOException -> L6d java.io.FileNotFoundException -> L77
            r6.close()     // Catch: java.io.IOException -> L4d
        L4d:
            r0.close()     // Catch: java.io.IOException -> L7f
            goto L7f
        L51:
            r1 = move-exception
            goto L60
        L53:
            r0 = move-exception
            r5 = r1
            r1 = r0
            r0 = r5
            goto L60
        L58:
            r0 = r1
            goto L6d
        L5a:
            r0 = r1
            goto L77
        L5c:
            r6 = move-exception
            r0 = r1
            r1 = r6
            r6 = r0
        L60:
            if (r6 == 0) goto L65
            r6.close()     // Catch: java.io.IOException -> L65
        L65:
            if (r0 == 0) goto L6a
            r0.close()     // Catch: java.io.IOException -> L6a
        L6a:
            throw r1
        L6b:
            r6 = r1
            r0 = r6
        L6d:
            if (r6 == 0) goto L72
            r6.close()     // Catch: java.io.IOException -> L72
        L72:
            if (r0 == 0) goto L7f
            goto L4d
        L75:
            r6 = r1
            r0 = r6
        L77:
            if (r6 == 0) goto L7c
            r6.close()     // Catch: java.io.IOException -> L7c
        L7c:
            if (r0 == 0) goto L7f
            goto L4d
        L7f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConfigurationManager.readCertFile(java.lang.String):java.lang.String");
    }

    private static void requestSync(Context context) {
        Account xiaomiAccount = WifiShareUtils.getXiaomiAccount(context);
        if (UserHandle.myUserId() != 0 || xiaomiAccount == null) {
            return;
        }
        ContentResolver.requestSync(new Account(xiaomiAccount.name, xiaomiAccount.type), "wifi", new Bundle());
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x010a A[Catch: all -> 0x00d8, TryCatch #0 {all -> 0x00d8, blocks: (B:17:0x0087, B:19:0x008d, B:23:0x0096, B:24:0x009b, B:26:0x00a1, B:28:0x00ab, B:31:0x00b2, B:35:0x00bd, B:40:0x00db, B:42:0x010a, B:45:0x011d), top: B:58:0x0087 }] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0124  */
    /* JADX WARN: Removed duplicated region for block: B:70:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void addOrUpdateWifiConfiguration(android.net.wifi.WifiConfiguration r15) {
        /*
            Method dump skipped, instructions count: 335
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConfigurationManager.addOrUpdateWifiConfiguration(android.net.wifi.WifiConfiguration):void");
    }

    public String convertToQuotedString(String str) {
        return "\"" + str + "\"";
    }

    public void deleteWifiConfiguration(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration != null) {
            LogUtil.logCloudSync("WifiConfigurationManager", "deleteWifiConfiguration " + wifiConfiguration.getKey());
            ContentValues contentValues = new ContentValues();
            contentValues.put("deleted", (Integer) 1);
            Pair selection = getSelection(wifiConfiguration);
            this.mContext.getContentResolver().update(getUserOwnerUri(Wifi.CONTENT_URI), contentValues, (String) selection.first, (String[]) selection.second);
            requestSync(this.mContext);
        }
    }

    public ArrayList<WifiConfiguration> filterUnsavedWifiConfigurations(Map<String, WifiConfiguration> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        List<WifiConfiguration> configuredNetworks = this.mWifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                map.remove(wifiConfiguration.getKey());
                LogUtil.logCloudSync("WifiConfigurationManager", "filterUnsavedWifiConfigurations saved " + wifiConfiguration.getKey());
                if (map.isEmpty()) {
                    return null;
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    public ArrayList<WifiConfiguration> getRestoreWifiConfigurations() {
        WifiConfiguration wifiConfiguration;
        Map<String, WifiConfiguration> wifiConfigs = getWifiConfigs();
        List<ScanResult> scanResults = this.mWifiManager.getScanResults();
        HashMap hashMap = new HashMap();
        if (!Build.IS_CM_CUSTOMIZATION_TEST && scanResults != null && wifiConfigs != null && !wifiConfigs.isEmpty()) {
            ArrayList arrayList = new ArrayList(wifiConfigs.values());
            arrayList.sort(new Comparator() { // from class: com.android.settings.wifi.WifiConfigurationManager$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$getRestoreWifiConfigurations$0;
                    lambda$getRestoreWifiConfigurations$0 = WifiConfigurationManager.lambda$getRestoreWifiConfigurations$0((WifiConfiguration) obj, (WifiConfiguration) obj2);
                    return lambda$getRestoreWifiConfigurations$0;
                }
            });
            if (arrayList.size() <= 10) {
                Iterator<WifiConfiguration> it = wifiConfigs.values().iterator();
                while (it.hasNext()) {
                    it.next().BSSID = null;
                }
                return filterUnsavedWifiConfigurations(wifiConfigs);
            }
            for (WifiConfiguration wifiConfiguration2 : arrayList.subList(0, 10)) {
                wifiConfiguration2.BSSID = null;
                hashMap.put(wifiConfiguration2.getKey(), wifiConfiguration2);
                LogUtil.logCloudSync("WifiConfigurationManager", "getRestoreWifiConfigurations RecentlyUsed " + wifiConfiguration2.getKey() + " BSSID=" + wifiConfiguration2.BSSID);
            }
            for (ScanResult scanResult : scanResults) {
                if (!TextUtils.isEmpty(scanResult.SSID) && (wifiConfiguration = wifiConfigs.get(configKey(scanResult))) != null) {
                    wifiConfiguration.BSSID = null;
                    hashMap.put(wifiConfiguration.getKey(), wifiConfiguration);
                    LogUtil.logCloudSync("WifiConfigurationManager", "getRestoreWifiConfigurations ScanResult " + wifiConfiguration.getKey());
                }
            }
        }
        return filterUnsavedWifiConfigurations(hashMap);
    }

    public List<String> getUnSavedAccessPoints() {
        ArrayList arrayList = new ArrayList();
        Map<String, WifiConfiguration> wifiConfigs = getWifiConfigs();
        if (wifiConfigs != null && !wifiConfigs.isEmpty()) {
            List<WifiConfiguration> configuredNetworks = this.mWifiManager.getConfiguredNetworks();
            if (configuredNetworks != null) {
                Iterator<WifiConfiguration> it = configuredNetworks.iterator();
                while (it.hasNext()) {
                    wifiConfigs.remove(it.next().getKey());
                    if (wifiConfigs.isEmpty()) {
                        return arrayList;
                    }
                }
            }
            arrayList.addAll(wifiConfigs.keySet());
        }
        return arrayList;
    }

    public WifiConfiguration getWifiConfigurationWithPsk(WifiConfiguration wifiConfiguration) {
        WifiConfiguration wifiConfiguration2 = null;
        if (wifiConfiguration != null && !TextUtils.isEmpty(wifiConfiguration.SSID)) {
            Cursor query = this.mContext.getContentResolver().query(getUserOwnerUri(Wifi.CONTENT_URI), null, "ssid= ? and keyMgmt= ? and deleted= 0", new String[]{removeDoubleQuotes(wifiConfiguration.SSID), makeString(wifiConfiguration.allowedKeyManagement, WifiConfiguration.KeyMgmt.strings)}, null);
            if (query != null) {
                try {
                    if (query.getCount() > 0) {
                        query.moveToLast();
                        wifiConfiguration2 = getWifiConfiguration(query);
                    }
                } finally {
                    query.close();
                }
            }
            if (query != null) {
            }
        }
        return wifiConfiguration2;
    }

    public ContentValues makeWifiEntryValues(WifiConfiguration wifiConfiguration) {
        String[] strArr;
        String str;
        ContentValues contentValues = new ContentValues();
        if (wifiConfiguration == null) {
            return contentValues;
        }
        String str2 = wifiConfiguration.SSID;
        String str3 = wifiConfiguration.BSSID;
        long j = wifiConfiguration.lastConnected;
        String str4 = wifiConfiguration.preSharedKey;
        String[] strArr2 = wifiConfiguration.wepKeys;
        int i = wifiConfiguration.wepTxKeyIndex;
        int i2 = !wifiConfiguration.hiddenSSID ? 1 : 0;
        String makeString = makeString(wifiConfiguration.allowedKeyManagement, WifiConfiguration.KeyMgmt.strings);
        String str5 = wifiConfiguration.enterpriseConfig.getEapMethod() != -1 ? null : "";
        String str6 = wifiConfiguration.enterpriseConfig.getPhase2Method() != 0 ? null : "";
        String identity = wifiConfiguration.enterpriseConfig.getIdentity();
        String anonymousIdentity = wifiConfiguration.enterpriseConfig.getAnonymousIdentity();
        String password = wifiConfiguration.enterpriseConfig.getPassword();
        String[] caCertificateAliases = wifiConfiguration.enterpriseConfig.getCaCertificateAliases();
        String str7 = str6;
        String clientCertificateAlias = wifiConfiguration.enterpriseConfig.getClientCertificateAlias();
        String[] caCertificateAliases2 = wifiConfiguration.enterpriseConfig.getCaCertificateAliases();
        if (caCertificateAliases != null) {
            str = readCertFile(caCertificateAliases[0]);
            strArr = caCertificateAliases;
        } else {
            strArr = caCertificateAliases;
            str = null;
        }
        String readCertFile = readCertFile(clientCertificateAlias);
        String readCertFile2 = caCertificateAliases2 != null ? readCertFile(caCertificateAliases2[0]) : null;
        if (!TextUtils.isEmpty(str2)) {
            str2 = removeDoubleQuotes(str2);
        }
        contentValues.put("ssid", str2);
        contentValues.put("bssid", str3);
        contentValues.put("adhoc", Long.valueOf(j));
        if (!TextUtils.isEmpty(str3)) {
            contentValues.put("uuid", str3.replace(":", ""));
        }
        contentValues.put("psk", str4);
        contentValues.put("wepkey0", strArr2[0]);
        contentValues.put("wepkey1", strArr2[1]);
        contentValues.put("wepkey2", strArr2[2]);
        contentValues.put("wepkey3", strArr2[3]);
        contentValues.put("wep_tx_keyidx", Integer.valueOf(i));
        contentValues.put("scan_ssid", Integer.valueOf(i2));
        contentValues.put("keyMgmt", makeString);
        contentValues.put("eap", str5);
        contentValues.put("phase2", str7);
        contentValues.put("identity", identity);
        contentValues.put("anonymousIdentity", anonymousIdentity);
        contentValues.put("password", password);
        contentValues.put("clientCert", strArr != null ? strArr[0] : null);
        contentValues.put("privateKey", clientCertificateAlias);
        contentValues.put("caCert", caCertificateAliases2 != null ? caCertificateAliases2[0] : null);
        contentValues.put("clientCertFile", str);
        contentValues.put("privateKeyFile", readCertFile);
        contentValues.put("caCertFile", readCertFile2);
        return contentValues;
    }

    public String removeDoubleQuotes(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
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
}
