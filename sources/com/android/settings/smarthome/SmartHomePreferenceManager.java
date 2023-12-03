package com.android.settings.smarthome;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.search.FunctionColumns;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.cloud.sync.MiCloudStatusInfo;
import miuix.preference.DropDownPreference;

/* loaded from: classes2.dex */
public class SmartHomePreferenceManager {
    private final boolean mAddDeviceLockedFlags;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private Handler mHandler;
    private final boolean mIsControlsSupported;
    private final PackageManager mPackageManager;
    private RecyclerView mRecyclerView;
    private DropDownPreference mSmartHome;
    private View mView;
    private boolean mIsReceiverRegistered = false;
    private final ContentObserver mSettingsObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.smarthome.SmartHomePreferenceManager.2
        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            SmartHomePreferenceManager.this.reloadServices();
        }
    };
    private final BroadcastReceiver mPackageReceiver = new BroadcastReceiver() { // from class: com.android.settings.smarthome.SmartHomePreferenceManager.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            SmartHomePreferenceManager.this.reloadServices();
        }
    };

    public SmartHomePreferenceManager(Context context, boolean z) {
        this.mContext = context;
        ContentResolver contentResolver = context.getContentResolver();
        this.mContentResolver = contentResolver;
        this.mPackageManager = context.getPackageManager();
        this.mAddDeviceLockedFlags = z;
        this.mIsControlsSupported = Settings.System.getInt(contentResolver, "SMART_CONTROLS_SUPPORT", 0) == 1;
        this.mView = new View(context);
        this.mHandler = new Handler();
    }

    private String getControlsAction() {
        return Build.VERSION.SDK_INT < 30 ? "miui.service.controls.ControlsProviderService" : "android.service.controls.ControlsProviderService";
    }

    private String getDefaultValue() {
        return miui.os.Build.IS_INTERNATIONAL_BUILD ? "" : "com.xiaomi.smarthome/com.xiaomi.smarthome.controls.MiControlsProviderService";
    }

    public static boolean isControlsSupported(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "SMART_CONTROLS_SUPPORT", 0) == 1;
    }

    public static boolean isExpandableUnderLockscreen(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "smart_home_keyguard", 0) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onResume$0(Preference preference, Object obj) {
        String str = (String) obj;
        if (TextUtils.equals(this.mSmartHome.getValue(), str)) {
            return true;
        }
        Settings.System.putString(this.mContentResolver, "smart_home", str);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performClick() {
        try {
            Method declaredMethod = DropDownPreference.class.getDeclaredMethod("performClick", View.class);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(this.mSmartHome, this.mView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerReceivers() {
        if (this.mIsReceiverRegistered) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme(FunctionColumns.PACKAGE);
        this.mContext.registerReceiver(this.mPackageReceiver, intentFilter);
        this.mContentResolver.registerContentObserver(Settings.System.getUriFor("smart_home"), false, this.mSettingsObserver);
        this.mIsReceiverRegistered = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reloadServices() {
        ArrayList arrayList = new ArrayList();
        Iterator it = this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent(getControlsAction()), this.mAddDeviceLockedFlags ? 786564 : 132, ActivityManager.getCurrentUser()).iterator();
        while (it.hasNext()) {
            ServiceInfo serviceInfo = ((ResolveInfo) it.next()).serviceInfo;
            if ("android.permission.BIND_CONTROLS".equals(serviceInfo.permission)) {
                arrayList.add(serviceInfo);
            }
        }
        reloadSmartHome(arrayList);
    }

    private void reloadSmartHome(List<ServiceInfo> list) {
        if (this.mSmartHome != null) {
            int size = list.size();
            int i = size + 1;
            String[] strArr = new String[i];
            CharSequence[] charSequenceArr = new String[i];
            int i2 = 0;
            strArr[0] = MiCloudStatusInfo.QuotaInfo.WARN_NONE;
            charSequenceArr[0] = this.mContext.getString(R.string.smart_home_value_none);
            int i3 = 0;
            while (i3 < size) {
                ServiceInfo serviceInfo = list.get(i3);
                i3++;
                strArr[i3] = serviceInfo.getComponentName().flattenToString();
                try {
                    PackageManager packageManager = this.mPackageManager;
                    charSequenceArr[i3] = packageManager.getApplicationLabel(packageManager.getApplicationInfo(serviceInfo.packageName, 128));
                } catch (PackageManager.NameNotFoundException e) {
                    charSequenceArr[i3] = this.mContext.getString(R.string.smart_home_value_unknown);
                    e.printStackTrace();
                }
            }
            this.mSmartHome.setEntryValues(strArr);
            this.mSmartHome.setEntries(charSequenceArr);
            String string = Settings.System.getString(this.mContentResolver, "smart_home");
            if (TextUtils.isEmpty(string)) {
                string = getDefaultValue();
            }
            int findIndexOfValue = this.mSmartHome.findIndexOfValue(string);
            if (findIndexOfValue >= 0 && findIndexOfValue < i) {
                i2 = findIndexOfValue;
            }
            this.mSmartHome.setValueIndex(i2);
        }
    }

    public static void setExpandableUnderLockscreen(Context context, boolean z) {
        Settings.System.putInt(context.getContentResolver(), "smart_home_keyguard", z ? 1 : 0);
    }

    private void unregisterReceivers() {
        if (this.mIsReceiverRegistered) {
            this.mContext.unregisterReceiver(this.mPackageReceiver);
            this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
            this.mIsReceiverRegistered = false;
        }
    }

    public void onCreate(DropDownPreference dropDownPreference) {
        this.mSmartHome = dropDownPreference;
    }

    public void onPause() {
        unregisterReceivers();
    }

    public void onResume() {
        if (!this.mIsControlsSupported) {
            this.mSmartHome.setVisible(false);
            return;
        }
        this.mSmartHome.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.smarthome.SmartHomePreferenceManager$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                boolean lambda$onResume$0;
                lambda$onResume$0 = SmartHomePreferenceManager.this.lambda$onResume$0(preference, obj);
                return lambda$onResume$0;
            }
        });
        reloadServices();
        registerReceivers();
        if (Settings.System.getInt(this.mContentResolver, "auto_show_apps", 0) == 1) {
            Settings.System.putInt(this.mContentResolver, "auto_show_apps", 0);
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            }
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.smarthome.SmartHomePreferenceManager.1
                @Override // java.lang.Runnable
                public void run() {
                    SmartHomePreferenceManager.this.performClick();
                }
            }, 300L);
        }
    }

    public void setEnabled(boolean z) {
        DropDownPreference dropDownPreference = this.mSmartHome;
        if (dropDownPreference != null) {
            dropDownPreference.setEnabled(z);
        }
    }

    public void setListView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    public void setVisible(boolean z) {
        DropDownPreference dropDownPreference = this.mSmartHome;
        if (dropDownPreference != null) {
            dropDownPreference.setVisible(z);
        }
    }
}
