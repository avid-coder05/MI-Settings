package miui.cloud.backup;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import miui.accounts.ExtraAccountManager;
import miui.cloud.backup.data.DataPackage;
import miui.cloud.backup.data.SettingItem;
import miui.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class SettingsBackupHelper {
    private static final String KEY_DATA = "data";
    private static final String KEY_VERSION = "version";
    private static final String TAG = "SettingsBackup";

    private SettingsBackupHelper() {
    }

    @Deprecated
    public static void backupSettings(Context context, ParcelFileDescriptor parcelFileDescriptor, ICloudBackup iCloudBackup) throws IOException {
        FileOutputStream fileOutputStream;
        DataPackage dataPackage = new DataPackage();
        iCloudBackup.onBackupSettings(context, dataPackage);
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        Collection<SettingItem<?>> values = dataPackage.getDataItems().values();
        FileOutputStream fileOutputStream2 = null;
        try {
            if (values != null) {
                try {
                    Iterator<SettingItem<?>> it = values.iterator();
                    while (it.hasNext()) {
                        jSONArray.put(it.next().toJson());
                    }
                    jSONObject.put("packageName", context.getPackageName());
                    jSONObject.put("version", iCloudBackup.getCurrentVersion(context));
                    jSONObject.put("data", jSONArray);
                } catch (IOException e) {
                    e = e;
                    Log.e("SettingsBackup", "IOException in backupSettings", e);
                    IOUtils.closeQuietly(fileOutputStream2);
                } catch (JSONException e2) {
                    e = e2;
                    Log.e("SettingsBackup", "JSONException in backupSettings", e);
                    IOUtils.closeQuietly(fileOutputStream2);
                }
            }
            fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
        } catch (Throwable th) {
            th = th;
        }
        try {
            fileOutputStream.write(jSONObject.toString().getBytes("utf-8"));
            fileOutputStream.flush();
            fileOutputStream.close();
            IOUtils.closeQuietly(fileOutputStream);
        } catch (IOException e3) {
            fileOutputStream2 = fileOutputStream;
            e = e3;
            Log.e("SettingsBackup", "IOException in backupSettings", e);
            IOUtils.closeQuietly(fileOutputStream2);
        } catch (JSONException e4) {
            fileOutputStream2 = fileOutputStream;
            e = e4;
            Log.e("SettingsBackup", "JSONException in backupSettings", e);
            IOUtils.closeQuietly(fileOutputStream2);
        } catch (Throwable th2) {
            fileOutputStream2 = fileOutputStream;
            th = th2;
            IOUtils.closeQuietly(fileOutputStream2);
            throw th;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00a1 A[LOOP:0: B:26:0x009b->B:28:0x00a1, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void backupSettings(android.content.Context r7, android.os.ParcelFileDescriptor r8, miui.cloud.backup.ICloudBackup r9, miui.app.backup.FullBackupAgent r10) throws java.io.IOException {
        /*
            miui.cloud.backup.data.DataPackage r0 = new miui.cloud.backup.data.DataPackage
            r0.<init>()
            r9.onBackupSettings(r7, r0)
            org.json.JSONObject r1 = new org.json.JSONObject
            r1.<init>()
            org.json.JSONArray r2 = new org.json.JSONArray
            r2.<init>()
            java.util.Map r3 = r0.getDataItems()
            java.util.Collection r3 = r3.values()
            java.lang.String r4 = "SettingsBackup"
            r5 = 0
            if (r3 == 0) goto L50
            java.util.Iterator r3 = r3.iterator()     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
        L23:
            boolean r6 = r3.hasNext()     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            if (r6 == 0) goto L37
            java.lang.Object r6 = r3.next()     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            miui.cloud.backup.data.SettingItem r6 = (miui.cloud.backup.data.SettingItem) r6     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            org.json.JSONObject r6 = r6.toJson()     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            r2.put(r6)     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            goto L23
        L37:
            java.lang.String r3 = "packageName"
            java.lang.String r6 = r7.getPackageName()     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            r1.put(r3, r6)     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            java.lang.String r3 = "version"
            int r7 = r9.getCurrentVersion(r7)     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            r1.put(r3, r7)     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            java.lang.String r7 = "data"
            r1.put(r7, r2)     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
        L50:
            java.io.FileOutputStream r7 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            java.io.FileDescriptor r8 = r8.getFileDescriptor()     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            r7.<init>(r8)     // Catch: java.lang.Throwable -> L7d org.json.JSONException -> L7f java.io.IOException -> L86
            java.lang.String r8 = r1.toString()     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L75 java.io.IOException -> L79
            java.lang.String r9 = "utf-8"
            byte[] r8 = r8.getBytes(r9)     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L75 java.io.IOException -> L79
            r7.write(r8)     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L75 java.io.IOException -> L79
            r7.flush()     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L75 java.io.IOException -> L79
            r7.close()     // Catch: java.lang.Throwable -> L71 org.json.JSONException -> L75 java.io.IOException -> L79
            miui.util.IOUtils.closeQuietly(r7)
            goto L8f
        L71:
            r8 = move-exception
            r5 = r7
            r7 = r8
            goto Lac
        L75:
            r8 = move-exception
            r5 = r7
            r7 = r8
            goto L80
        L79:
            r8 = move-exception
            r5 = r7
            r7 = r8
            goto L87
        L7d:
            r7 = move-exception
            goto Lac
        L7f:
            r7 = move-exception
        L80:
            java.lang.String r8 = "JSONException in backupSettings"
            android.util.Log.e(r4, r8, r7)     // Catch: java.lang.Throwable -> L7d
            goto L8c
        L86:
            r7 = move-exception
        L87:
            java.lang.String r8 = "IOException in backupSettings"
            android.util.Log.e(r4, r8, r7)     // Catch: java.lang.Throwable -> L7d
        L8c:
            miui.util.IOUtils.closeQuietly(r5)
        L8f:
            java.util.Map r7 = r0.getFileItems()
            java.util.Set r7 = r7.keySet()
            java.util.Iterator r7 = r7.iterator()
        L9b:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto Lab
            java.lang.Object r8 = r7.next()
            java.lang.String r8 = (java.lang.String) r8
            r10.addAttachedFile(r8)
            goto L9b
        Lab:
            return
        Lac:
            miui.util.IOUtils.closeQuietly(r5)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.cloud.backup.SettingsBackupHelper.backupSettings(android.content.Context, android.os.ParcelFileDescriptor, miui.cloud.backup.ICloudBackup, miui.app.backup.FullBackupAgent):void");
    }

    private static boolean isSettingsBackupEnabled(Account account) {
        if (account == null) {
            return false;
        }
        return ContentResolver.getSyncAutomatically(account, "settings_backup");
    }

    @Deprecated
    public static void requestBackupSettings(Context context) {
        Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(context);
        if (isSettingsBackupEnabled(xiaomiAccount)) {
            requestSettingsBackupManualSync(xiaomiAccount, null);
        }
    }

    private static void requestManualSync(Account account, String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("expedited", true);
        if (str2 != null) {
            bundle.putString("packageName", str2);
        }
        ContentResolver.requestSync(account, str, bundle);
    }

    private static void requestSettingsBackupManualSync(Account account, String str) {
        requestManualSync(account, "settings_backup", str);
    }

    public static void restoreFiles(DataPackage dataPackage) {
        for (Map.Entry<String, ParcelFileDescriptor> entry : dataPackage.getFileItems().entrySet()) {
            restoreOneFile(entry.getKey(), entry.getValue());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v1 */
    /* JADX WARN: Type inference failed for: r7v21 */
    /* JADX WARN: Type inference failed for: r7v6, types: [java.io.OutputStream] */
    public static void restoreOneFile(String str, ParcelFileDescriptor parcelFileDescriptor) {
        ?? r7;
        FileInputStream fileInputStream;
        IOException e;
        FileOutputStream fileOutputStream;
        FileNotFoundException e2;
        FileInputStream fileInputStream2 = null;
        try {
            try {
                fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            } catch (Throwable th) {
                th = th;
            }
        } catch (FileNotFoundException e3) {
            fileInputStream = null;
            e2 = e3;
            fileOutputStream = null;
        } catch (IOException e4) {
            fileInputStream = null;
            e = e4;
            fileOutputStream = null;
        } catch (Throwable th2) {
            th = th2;
            r7 = 0;
            IOUtils.closeQuietly(fileInputStream2);
            IOUtils.closeQuietly((OutputStream) r7);
            throw th;
        }
        try {
            new File(str.substring(0, str.lastIndexOf(File.separator))).mkdirs();
            fileOutputStream = new FileOutputStream(new File(str));
            try {
                byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read <= 0) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
                fileOutputStream.flush();
            } catch (FileNotFoundException e5) {
                e2 = e5;
                Log.e("SettingsBackup", "FileNotFoundException in restoreFiles: " + str, e2);
                IOUtils.closeQuietly(fileInputStream);
                IOUtils.closeQuietly(fileOutputStream);
            } catch (IOException e6) {
                e = e6;
                Log.e("SettingsBackup", "IOException in restoreFiles: " + str, e);
                IOUtils.closeQuietly(fileInputStream);
                IOUtils.closeQuietly(fileOutputStream);
            }
        } catch (FileNotFoundException e7) {
            e2 = e7;
            fileOutputStream = null;
        } catch (IOException e8) {
            e = e8;
            fileOutputStream = null;
        } catch (Throwable th3) {
            th = th3;
            parcelFileDescriptor = null;
            fileInputStream2 = fileInputStream;
            r7 = parcelFileDescriptor;
            IOUtils.closeQuietly(fileInputStream2);
            IOUtils.closeQuietly((OutputStream) r7);
            throw th;
        }
        IOUtils.closeQuietly(fileInputStream);
        IOUtils.closeQuietly(fileOutputStream);
    }

    public static void restoreSettings(Context context, ParcelFileDescriptor parcelFileDescriptor, ICloudBackup iCloudBackup) throws IOException {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2 = null;
        try {
            try {
                bufferedReader = new BufferedReader(new FileReader(parcelFileDescriptor.getFileDescriptor()));
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        } catch (JSONException e2) {
            e = e2;
        }
        try {
            StringBuilder sb = new StringBuilder();
            String property = System.getProperty("line.separator");
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                sb.append(readLine);
                sb.append(property);
            }
            JSONObject jSONObject = new JSONObject(sb.toString());
            if (jSONObject.length() > 0) {
                int optInt = jSONObject.optInt("version");
                JSONArray optJSONArray = jSONObject.optJSONArray("data");
                DataPackage dataPackage = new DataPackage();
                if (optJSONArray != null) {
                    for (int i = 0; i < optJSONArray.length(); i++) {
                        JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                        if (optJSONObject != null) {
                            SettingItem<?> fromJson = SettingItem.fromJson(optJSONObject);
                            dataPackage.addAbstractDataItem(fromJson.key, fromJson);
                        }
                    }
                }
                iCloudBackup.onRestoreSettings(context, dataPackage, optInt);
            }
            IOUtils.closeQuietly(bufferedReader);
        } catch (IOException e3) {
            e = e3;
            bufferedReader2 = bufferedReader;
            Log.e("SettingsBackup", "IOException in restoreSettings", e);
            IOUtils.closeQuietly(bufferedReader2);
        } catch (JSONException e4) {
            e = e4;
            bufferedReader2 = bufferedReader;
            Log.e("SettingsBackup", "JSONException in restoreSettings", e);
            IOUtils.closeQuietly(bufferedReader2);
        } catch (Throwable th2) {
            th = th2;
            bufferedReader2 = bufferedReader;
            IOUtils.closeQuietly(bufferedReader2);
            throw th;
        }
    }
}
