package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.development.BluetoothDeviceNoNamePreferenceController;
import com.android.settings.development.BluetoothMiFastConnectPreferenceController;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.AbstractPreferenceController;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import miui.util.FeatureParser;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class BluetoothAdvancedSettings extends DashboardFragment {
    LocalBluetoothManager mLocalManager;
    private final String PIC_FOLDER_BASE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Environment.DIRECTORY_DOWNLOADS;
    private boolean DBG_UPLOAD_RESOURCE = Log.isLoggable("MiuiFastConnectResourceLoad", 2);

    private boolean checkLocalCached(String str) {
        if (new File(this.PIC_FOLDER_BASE + File.separator + str + SplitConstants.DOT_ZIP).exists()) {
            return true;
        }
        Log.d("BluetoothAdvancedSettings", "local file not found: " + str + SplitConstants.DOT_ZIP);
        return false;
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception unused) {
                Log.e("BluetoothAdvancedSettings", "failed to close stream");
            }
        }
    }

    private int getSystemProp() {
        String string = FeatureParser.getString("vendor");
        try {
            String valueOf = String.valueOf(-1);
            if ("qcom".equals(string)) {
                valueOf = SystemProperties.get("persist.vendor.bt.a2dp.notification", String.valueOf(-1));
            } else if ("mediatek".equals(string)) {
                return Settings.Secure.getInt(getContentResolver(), "miui_bluetooth_notification", -1);
            }
            return Integer.parseInt(valueOf);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v1 */
    /* JADX WARN: Type inference failed for: r10v14 */
    /* JADX WARN: Type inference failed for: r10v4, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r9v0, types: [com.android.settings.bluetooth.BluetoothAdvancedSettings, androidx.fragment.app.Fragment] */
    public boolean moveResource(String str) {
        ?? r10;
        File file;
        FileInputStream fileInputStream;
        Exception e;
        FileOutputStream fileOutputStream;
        if (TextUtils.isEmpty(str) || !checkLocalCached(str)) {
            return false;
        }
        FileInputStream fileInputStream2 = null;
        try {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(this.PIC_FOLDER_BASE);
                String str2 = File.separator;
                sb.append(str2);
                sb.append(str);
                sb.append(SplitConstants.DOT_ZIP);
                fileInputStream = new FileInputStream(new File(sb.toString()));
                try {
                    try {
                        file = new File(getActivity().getApplicationContext().getFilesDir() + str2 + "temp_" + str + SplitConstants.DOT_ZIP);
                        try {
                            fileOutputStream = new FileOutputStream(file);
                        } catch (Exception e2) {
                            e = e2;
                            fileOutputStream = null;
                        }
                        try {
                            byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
                            while (true) {
                                int read = fileInputStream.read(bArr);
                                if (read == -1) {
                                    fileOutputStream.flush();
                                    closeQuietly(fileInputStream);
                                    closeQuietly(fileOutputStream);
                                    closeQuietly(fileInputStream);
                                    closeQuietly(fileOutputStream);
                                    Log.v("BluetoothAdvancedSettings", "moveResource success");
                                    return true;
                                }
                                fileOutputStream.write(bArr, 0, read);
                            }
                        } catch (Exception e3) {
                            e = e3;
                            if (file != null && file.exists()) {
                                file.delete();
                            }
                            Log.e("BluetoothAdvancedSettings", "failed to move");
                            e.printStackTrace();
                            closeQuietly(fileInputStream);
                            closeQuietly(fileOutputStream);
                            return false;
                        }
                    } catch (Throwable th) {
                        th = th;
                        str = null;
                        fileInputStream2 = fileInputStream;
                        r10 = str;
                        closeQuietly(fileInputStream2);
                        closeQuietly(r10);
                        throw th;
                    }
                } catch (Exception e4) {
                    file = null;
                    e = e4;
                    fileOutputStream = null;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e5) {
            file = null;
            fileInputStream = null;
            e = e5;
            fileOutputStream = null;
        } catch (Throwable th3) {
            th = th3;
            r10 = 0;
            closeQuietly(fileInputStream2);
            closeQuietly(r10);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSystemProp(int i) {
        String string = FeatureParser.getString("vendor");
        try {
            if ("qcom".equals(string)) {
                SystemProperties.set("persist.vendor.bt.a2dp.notification", String.valueOf(i));
            } else if ("mediatek".equals(string)) {
                Settings.Secure.putInt(getContentResolver(), "miui_bluetooth_notification", i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new BluetoothMiFastConnectPreferenceController(context));
        arrayList.add(new BluetoothDeviceNoNamePreferenceController(context));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BluetoothAdvancedSettings";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.bluetooth_advanced_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(getActivity());
        this.mLocalManager = localBtManager;
        if (localBtManager == null) {
            Log.e("BluetoothAdvancedSettings", "Bluetooth is not supported on this device");
            return;
        }
        boolean isLoggable = Log.isLoggable("MiuiThirdAppTest", 2);
        PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("load_textWhiteList_flag");
        if (isLoggable) {
            getPreferenceScreen().addPreference(preferenceGroup);
        } else {
            getPreferenceScreen().removePreference(preferenceGroup);
        }
        if (Settings.Global.getString(getContentResolver(), "Settings.Global.ENABLE_BT_FLAG") == null) {
            getPreferenceScreen().removePreference((PreferenceGroup) findPreference("load_preferenceCategory_flag"));
        }
        PreferenceGroup preferenceGroup2 = (PreferenceGroup) findPreference("bluetooth_fastConnect_resource_load");
        if (this.DBG_UPLOAD_RESOURCE) {
            EditTextPreference editTextPreference = (EditTextPreference) findPreference("key_device_id");
            if (preferenceGroup2 != null && editTextPreference != null) {
                editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.bluetooth.BluetoothAdvancedSettings.1
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public boolean onPreferenceChange(Preference preference, Object obj) {
                        if (obj != null) {
                            final String obj2 = obj.toString();
                            preference.setSummary(obj2);
                            new Thread(new Runnable() { // from class: com.android.settings.bluetooth.BluetoothAdvancedSettings.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    if (BluetoothAdvancedSettings.this.moveResource(obj2)) {
                                        Intent intent = new Intent("android.bluetooth.settings.action.FASTCONNECT_MODIFICATION_COMPLETED");
                                        intent.putExtra("deviceId", obj2);
                                        BluetoothAdvancedSettings.this.getActivity().sendBroadcast(intent);
                                        return;
                                    }
                                    Log.e("BluetoothAdvancedSettings", "fail to move resource from download folder! deviceId = " + obj2);
                                }
                            }).start();
                            return true;
                        }
                        return false;
                    }
                });
            }
        } else if (preferenceGroup2 != null) {
            getPreferenceScreen().removePreference(preferenceGroup2);
        }
        try {
            SwitchPreference switchPreference = (SwitchPreference) findPreference("bluetooth_show_notification");
            if (switchPreference != null) {
                int systemProp = getSystemProp();
                if (systemProp != -1) {
                    switchPreference.setChecked(systemProp != 0);
                    switchPreference.setSingleLineTitle(false);
                    switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.bluetooth.BluetoothAdvancedSettings.2
                        @Override // androidx.preference.Preference.OnPreferenceChangeListener
                        public boolean onPreferenceChange(Preference preference, Object obj) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("stat=");
                            Boolean bool = (Boolean) obj;
                            sb.append(bool.booleanValue());
                            Log.d("BluetoothAdvancedSettings", sb.toString());
                            BluetoothAdvancedSettings.this.setSystemProp(bool.booleanValue() ? 1 : 0);
                            Intent intent = new Intent("com.android.bluetooth.notification");
                            intent.setPackage("com.android.bluetooth");
                            intent.putExtra(YellowPageContract.Settings.DIRECTORY, bool.booleanValue() ? "on" : "off");
                            BluetoothAdvancedSettings.this.getActivity().sendBroadcast(intent);
                            return true;
                        }
                    });
                    return;
                }
                PreferenceGroup preferenceGroup3 = (PreferenceGroup) findPreference("bluetooth_show");
                if (preferenceGroup3 != null) {
                    preferenceGroup3.removePreference(switchPreference);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            Log.d("BluetoothAdvancedSettings", "set scan mode connectable");
            defaultAdapter.setScanMode(21);
        }
        LocalBluetoothManager localBluetoothManager = this.mLocalManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.setForegroundActivity(null);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            Log.d("BluetoothAdvancedSettings", "set scan mode connectable and discoverable");
            defaultAdapter.setScanMode(23);
        }
        LocalBluetoothManager localBluetoothManager = this.mLocalManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.setForegroundActivity(getActivity());
        }
    }
}
