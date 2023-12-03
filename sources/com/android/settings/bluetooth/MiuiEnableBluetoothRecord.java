package com.android.settings.bluetooth;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import miui.content.res.ThemeResources;

/* loaded from: classes.dex */
class MiuiEnableBluetoothRecord {
    private static MiuiEnableBluetoothRecord mMiuiEnableBluetoothRecord;
    private static long mOpenCheckTime;
    private static long mOpenStartTime;
    ArrayList<String> mSystemPackage = new ArrayList<>();
    private String mRecordEnableBt = "0#0#0,";
    private HashMap<String, String> mPackageTime = new HashMap<>();
    private LinkedList<String> mPackageList = new LinkedList<>();
    private HashMap<String, String> mPackageAction = new HashMap<>();

    public MiuiEnableBluetoothRecord() {
        this.mSystemPackage.add(ThemeResources.SYSTEMUI_NAME);
        this.mSystemPackage.add(ThemeResources.FRAMEWORK_PACKAGE);
        this.mSystemPackage.add("com.android.settings");
        this.mSystemPackage.add("com.android.nfc");
    }

    public static String getAppNameByPackageName(Context context, String str) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 128)).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public static synchronized MiuiEnableBluetoothRecord getInstance() {
        MiuiEnableBluetoothRecord miuiEnableBluetoothRecord;
        synchronized (MiuiEnableBluetoothRecord.class) {
            if (mMiuiEnableBluetoothRecord == null) {
                Log.d("MiuiEnableBluetoothRecord", "new MiuiEnableBluetoothRecord()");
                mMiuiEnableBluetoothRecord = new MiuiEnableBluetoothRecord();
            }
            miuiEnableBluetoothRecord = mMiuiEnableBluetoothRecord;
        }
        return miuiEnableBluetoothRecord;
    }

    private void initRecordInfo(Context context, String str, String str2) {
        Log.d("MiuiEnableBluetoothRecord", "initRecordInfo: ");
        try {
            this.mPackageList.clear();
            this.mPackageTime.clear();
            this.mRecordEnableBt = Settings.Global.getString(context.getContentResolver(), "Settings.Global.ENABLE_BLUETOOTH_RECORD");
            Log.d("MiuiEnableBluetoothRecord", "mRecordEnableBt: " + this.mRecordEnableBt);
            String str3 = this.mRecordEnableBt;
            if (str3 == null) {
                String time = getTime();
                Log.d("MiuiEnableBluetoothRecord", "currentTime = " + time);
                this.mRecordEnableBt = str + "#" + time + "#" + str2 + ",";
                this.mPackageTime.put(str, time);
                this.mPackageAction.put(str, str2);
                this.mPackageList.add(str);
                Settings.Global.putString(context.getContentResolver(), "Settings.Global.ENABLE_BLUETOOTH_RECORD", this.mRecordEnableBt);
                Log.d("MiuiEnableBluetoothRecord", "mRecordEnableBt = " + this.mRecordEnableBt);
                return;
            }
            for (String str4 : str3.split(",")) {
                String[] split = str4.split("#");
                if (split.length == 3) {
                    Log.d("MiuiEnableBluetoothRecord", "initRecordInfo: " + split[0] + ", " + split[1] + ", " + split[2]);
                    this.mPackageTime.put(split[0], split[1]);
                    this.mPackageAction.put(split[0], split[2]);
                    this.mPackageList.add(split[0]);
                } else {
                    Log.e("MiuiEnableBluetoothRecord", "initRecordInfo Shouldn't be here!");
                }
            }
            String time2 = getTime();
            if (this.mPackageList.contains(str)) {
                Log.d("MiuiEnableBluetoothRecord", "contain packagename = " + str);
                this.mPackageList.remove(str);
            }
            this.mPackageTime.put(str, time2);
            this.mPackageAction.put(str, str2);
            this.mPackageList.addFirst(str);
            writeToSettingsGlobal(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToSettingsGlobal(Context context) {
        Log.d("MiuiEnableBluetoothRecord", "writeToSettingsGlobal");
        try {
            this.mRecordEnableBt = "";
            for (int i = 0; i < 10 && i < this.mPackageList.size(); i++) {
                String str = this.mPackageList.get(i);
                String str2 = this.mPackageTime.get(str);
                String str3 = this.mPackageAction.get(str);
                if (this.mPackageTime != null) {
                    this.mRecordEnableBt += str + "#" + str2 + "#" + str3 + ",";
                }
            }
            Log.d("MiuiEnableBluetoothRecord", " final mRecordEnableBt = " + this.mRecordEnableBt);
            Settings.Global.putString(context.getContentResolver(), "Settings.Global.ENABLE_BLUETOOTH_RECORD", this.mRecordEnableBt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNotification(Context context, String str, boolean z) {
        try {
            String appNameByPackageName = getAppNameByPackageName(context, str);
            Log.d("MiuiEnableBluetoothRecord", "packageName = " + str + " appName = " + appNameByPackageName);
            Settings.Global.putString(context.getContentResolver(), "Settings.Global.ENABLE_BT_FLAG", "true");
            if (this.mSystemPackage.contains(str)) {
                Log.d("MiuiEnableBluetoothRecord", "package is system package,do not recode");
                return;
            }
            Log.d("MiuiEnableBluetoothRecord", "now time = " + getTime());
            if (!z) {
                context.getString(286195835);
                context.getString(286195834, appNameByPackageName);
                initRecordInfo(context, str, context.getString(286195833));
                return;
            }
            context.getString(286195837);
            context.getString(286195836, appNameByPackageName);
            initRecordInfo(context, str, context.getString(286195838));
            Log.d("MiuiEnableBluetoothRecord", "check enable bt time");
            long currentTimeMillis = System.currentTimeMillis();
            mOpenCheckTime = currentTimeMillis;
            if (currentTimeMillis - mOpenStartTime > 100) {
                Log.d("MiuiEnableBluetoothRecord", "do not notification ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
