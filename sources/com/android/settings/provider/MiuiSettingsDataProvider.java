package com.android.settings.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import com.android.settings.datetime.DualClockHealper;
import com.android.settingslib.net.DataUsageController;
import miuix.util.Log;

/* loaded from: classes2.dex */
public class MiuiSettingsDataProvider extends ContentProvider {
    private static String AUTHORITY = "com.android.settings.provider.MiuiSettingsDataProvider";
    private static int RESTORE_DELAY_MINLLIS = 60000;
    private static final UriMatcher sMatcher;
    private Handler mHandler;
    private int mOriginHotspotMaxNum = -1;
    private boolean mOriginCellularState = false;
    private boolean mCellularStateInited = false;
    private int mOriginNetworkPriorityMode = -1;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sMatcher = uriMatcher;
        uriMatcher.addURI(AUTHORITY, "dual_zone_info", 1);
        uriMatcher.addURI(AUTHORITY, "zone_info", 2);
        uriMatcher.addURI(AUTHORITY, "hotspot_max_num", 3);
        uriMatcher.addURI(AUTHORITY, "restore_hotspot_num", 4);
        uriMatcher.addURI(AUTHORITY, "start_tethering", 5);
        uriMatcher.addURI(AUTHORITY, "stop_tethering", 6);
        uriMatcher.addURI(AUTHORITY, "set_ap_config", 7);
    }

    private void disableCellularState() {
        if (this.mCellularStateInited) {
            return;
        }
        DataUsageController dataUsageController = new DataUsageController(getContext());
        if (dataUsageController.getTelephonyManager().isDataEnabled()) {
            this.mOriginCellularState = true;
            dataUsageController.setMobileDataEnabled(false);
        }
        this.mCellularStateInited = true;
        Log.i("MiuiSettingsDataProvider", " 6100 try disable mobile data state , origin state = " + this.mOriginCellularState);
    }

    private int getHotSpotMaxStationNum() {
        return ((WifiManager) getContext().getApplicationContext().getSystemService("wifi")).getSoftApConfiguration().getMaxNumberOfClients();
    }

    public static int getTrafficPriority(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "user_network_priority_enabled", 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetCellularState() {
        if (this.mCellularStateInited) {
            DataUsageController dataUsageController = new DataUsageController(getContext());
            Log.i("MiuiSettingsDataProvider", " 6100 try reset mobile data state , origin state = " + this.mOriginCellularState);
            dataUsageController.setMobileDataEnabled(this.mOriginCellularState);
            this.mCellularStateInited = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHotSpotMaxStationNum(int i) {
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService("wifi");
        wifiManager.setSoftApConfiguration(new SoftApConfiguration.Builder(wifiManager.getSoftApConfiguration()).setMaxNumberOfClients(i).build());
    }

    public static void setTrafficPriority(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "user_network_priority_enabled", i);
    }

    private void setWifiApConfig(ContentValues contentValues) {
        ((WifiManager) getContext().getApplicationContext().getSystemService("wifi")).setSoftApConfiguration(new SoftApConfiguration.Builder().setSsid(contentValues.getAsString("ssid")).setBand(contentValues.getAsBoolean("support5g").booleanValue() ? 2 : 1).setPassphrase(contentValues.getAsString("pwd"), 1).setMaxNumberOfClients(10).build());
    }

    private void startTethering(ContentValues contentValues) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService("connectivity");
        ((WifiManager) getContext().getApplicationContext().getSystemService("wifi")).setSoftApConfiguration(new SoftApConfiguration.Builder().setSsid(contentValues.getAsString("ssid")).setBand(contentValues.getAsBoolean("support5g").booleanValue() ? 2 : 1).setPassphrase(contentValues.getAsString("pwd"), 1).setMaxNumberOfClients(1).build());
        connectivityManager.startTethering(contentValues.getAsInteger("type").intValue(), contentValues.getAsBoolean("showProvisioningUi").booleanValue(), new ConnectivityManager.OnStartTetheringCallback() { // from class: com.android.settings.provider.MiuiSettingsDataProvider.2
            public void onTetheringFailed() {
                super.onTetheringFailed();
                Log.d("MiuiSettingsDataProvider", "onTetheringFailed");
            }

            public void onTetheringStarted() {
                super.onTetheringStarted();
                Log.d("MiuiSettingsDataProvider", "onTetheringStarted");
            }
        });
        if (getTrafficPriority(getContext()) != 0) {
            Log.i("MiuiSettingsDataProvider", " setTrafficPriority 0");
            setTrafficPriority(getContext(), 0);
        }
        disableCellularState();
    }

    private void stopTethering(ContentValues contentValues) {
        ((ConnectivityManager) getContext().getSystemService("connectivity")).stopTethering(contentValues.getAsInteger("type").intValue());
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mHandler = new Handler() { // from class: com.android.settings.provider.MiuiSettingsDataProvider.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 4 && MiuiSettingsDataProvider.this.mOriginHotspotMaxNum != -1) {
                    Log.i("MiuiSettingsDataProvider", "restore hotspot num: " + MiuiSettingsDataProvider.this.mOriginHotspotMaxNum);
                    MiuiSettingsDataProvider miuiSettingsDataProvider = MiuiSettingsDataProvider.this;
                    miuiSettingsDataProvider.setHotSpotMaxStationNum(miuiSettingsDataProvider.mOriginHotspotMaxNum);
                    MiuiSettingsDataProvider.this.mOriginHotspotMaxNum = -1;
                    MiuiSettingsDataProvider.this.resetCellularState();
                    removeCallbacksAndMessages(null);
                    if (MiuiSettingsDataProvider.this.mOriginNetworkPriorityMode != -1) {
                        MiuiSettingsDataProvider.setTrafficPriority(MiuiSettingsDataProvider.this.getContext(), MiuiSettingsDataProvider.this.mOriginNetworkPriorityMode);
                        Log.i("MiuiSettingsDataProvider", "restore mOriginNetworkPriorityMode: " + MiuiSettingsDataProvider.this.mOriginNetworkPriorityMode);
                        MiuiSettingsDataProvider.this.mOriginNetworkPriorityMode = -1;
                    }
                }
                super.handleMessage(message);
            }
        };
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        int match = sMatcher.match(uri);
        if (match != 1) {
            if (match != 2) {
                if (match != 3) {
                    return null;
                }
                if (this.mOriginHotspotMaxNum == -1) {
                    this.mOriginHotspotMaxNum = getHotSpotMaxStationNum();
                }
                MatrixCursor matrixCursor = new MatrixCursor(new String[]{"hotspot_max_num"});
                matrixCursor.addRow(new Object[]{Integer.valueOf(this.mOriginHotspotMaxNum)});
                if (this.mOriginNetworkPriorityMode == -1) {
                    this.mOriginNetworkPriorityMode = getTrafficPriority(getContext());
                    Log.i("MiuiSettingsDataProvider", "getTrafficPriority: " + this.mOriginNetworkPriorityMode);
                }
                return matrixCursor;
            }
            return DualClockHealper.getZoneInfoCursor(getContext());
        }
        return DualClockHealper.getDualTimeZoneCursor(getContext());
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (Binder.getCallingUid() != 6100) {
            Log.w("MiuiSettingsDataProvider", "update uid error: " + Binder.getCallingUid());
            return -1;
        }
        int match = sMatcher.match(uri);
        if (match == 3) {
            if (Binder.getCallingUid() != 6100 || contentValues.getAsInteger("hotspot_max_num") == null) {
                return 0;
            }
            Log.i("MiuiSettingsDataProvider", " 6100 try set KEY_UPDATE_HOTSPOT_NUM");
            this.mHandler.removeMessages(4);
            this.mHandler.sendEmptyMessageDelayed(4, RESTORE_DELAY_MINLLIS);
            return 0;
        } else if (match == 4) {
            this.mHandler.removeMessages(4);
            this.mHandler.sendEmptyMessage(4);
            return 0;
        } else if (match == 5) {
            Log.i("MiuiSettingsDataProvider", "update: KEY_START_TETHERING");
            startTethering(contentValues);
            return 0;
        } else if (match == 6) {
            stopTethering(contentValues);
            return 0;
        } else if (match != 7) {
            return 0;
        } else {
            setWifiApConfig(contentValues);
            return 0;
        }
    }
}
