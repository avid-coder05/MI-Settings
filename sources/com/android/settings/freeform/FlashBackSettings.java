package com.android.settings.freeform;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.tree.SecuritySettingsTree;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes.dex */
public class FlashBackSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static boolean sFlashBacSettingDebug = true;
    private CheckBoxPreference mBackgroundStartSwitchPreference;
    private SQLiteDatabase mDB;
    private FlashBackDataHelper mDBHelper;
    private int mFlashBackBackgroundStartSwitchState;
    private FlashBackMainSwitchObserver mFlashBackMainSwitchObserver;
    private int mFlashBackMainSwitchState;
    private Intent mKillFlashBackServiceIntent;
    private CheckBoxPreference mMainSwitchPreference;
    private PackageManager mPackageManager;
    private PreferenceCategory mPreferenceCategoryForAppSupport;
    private PreferenceCategory mPreferenceCategoryForBackgroundSupport;
    private ArrayList<CheckBoxPreference> mAppsCheckBoxPreferenceList = new ArrayList<>();
    private HashMap<String, String> mPackageNameMap = new HashMap<>();

    /* loaded from: classes.dex */
    private class FlashBackMainSwitchObserver extends ContentObserver {
        public FlashBackMainSwitchObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            FlashBackSettings.this.getPrefContext().getContentResolver().registerContentObserver(Settings.Secure.getUriFor("FlashBackMainSwitch"), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            if (FlashBackSettings.sFlashBacSettingDebug) {
                Log.d("FBSettings", "MainSetting Value change: " + z + " mFlashBackMainSwitchValue: " + FlashBackSettings.this.mFlashBackMainSwitchState);
            }
            if (FlashBackSettings.this.mFlashBackMainSwitchState == 1) {
                FlashBackSettings.this.mPreferenceCategoryForBackgroundSupport.setVisible(true);
                FlashBackSettings.this.mPreferenceCategoryForAppSupport.setVisible(true);
                FlashBackSettings.this.mBackgroundStartSwitchPreference.setVisible(true);
                for (int i = 0; i < FlashBackSettings.this.mAppsCheckBoxPreferenceList.size(); i++) {
                    FlashBackSettings.this.getPreferenceScreen().addPreference((Preference) FlashBackSettings.this.mAppsCheckBoxPreferenceList.get(i));
                    ((CheckBoxPreference) FlashBackSettings.this.mAppsCheckBoxPreferenceList.get(i)).setVisible(true);
                }
                return;
            }
            FlashBackSettings.this.mPreferenceCategoryForBackgroundSupport.setVisible(false);
            FlashBackSettings.this.mPreferenceCategoryForAppSupport.setVisible(false);
            FlashBackSettings.this.mBackgroundStartSwitchPreference.setVisible(false);
            for (int i2 = 0; i2 < FlashBackSettings.this.mAppsCheckBoxPreferenceList.size(); i2++) {
                FlashBackSettings.this.getPreferenceScreen().addPreference((Preference) FlashBackSettings.this.mAppsCheckBoxPreferenceList.get(i2));
                ((CheckBoxPreference) FlashBackSettings.this.mAppsCheckBoxPreferenceList.get(i2)).setVisible(false);
            }
        }

        void unObserve() {
            FlashBackSettings.this.getPrefContext().getContentResolver().unregisterContentObserver(this);
        }
    }

    private void addAppSupportStatus(String str) {
        boolean readMetaData = str.equals("com.tencent.tmgp.sgame") ? readMetaData(SecuritySettingsTree.COM_XIAOMI_JOYOSE) : readMetaData(str);
        try {
            Cursor query = this.mDB.query("FlashBack_Support_Apps", new String[]{"packageName"}, "packageName=?", new String[]{str}, null, null, null, null);
            if (readMetaData) {
                if (query.getCount() == 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("packageName", str);
                    contentValues.put("switch", (Integer) 1);
                    this.mDB.insert("FlashBack_Support_Apps", null, contentValues);
                    contentValues.clear();
                }
            } else if (query.getCount() > 0) {
                this.mDB.delete("FlashBack_Support_Apps", "packageName=?", new String[]{str});
            }
            query.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void backgroundStartSwitchPreferenceOnChange(Object obj) {
        Boolean bool = (Boolean) obj;
        Settings.System.putInt(getContentResolver(), "FlashBackBackgroundStartSwitch", bool.booleanValue() ? 1 : 0);
        this.mFlashBackBackgroundStartSwitchState = bool.booleanValue() ? 1 : 0;
        if (sFlashBacSettingDebug) {
            Log.d("FBSettings", "mFlashBackBackgroundStartSwitch New Result: " + this.mFlashBackBackgroundStartSwitchState);
        }
    }

    private void buildCheckBoxPreference(ApplicationInfo applicationInfo, Drawable drawable, String str, int i, String str2) {
        if (applicationInfo != null) {
            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
            checkBoxPreference.setTitle(str);
            setCheckBoxPreferenceSummary(checkBoxPreference, str2);
            checkBoxPreference.setIcon(drawable);
            checkBoxPreference.setChecked(i == 1);
            getPreferenceScreen().addPreference(checkBoxPreference);
            this.mAppsCheckBoxPreferenceList.add(checkBoxPreference);
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.freeform.FlashBackSettings.3
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    FlashBackSettings.this.performClick(preference, (Boolean) obj);
                    return true;
                }
            });
        }
    }

    private void checkVisibility() {
        if (this.mFlashBackMainSwitchState != 1) {
            this.mPreferenceCategoryForBackgroundSupport.setVisible(false);
            this.mPreferenceCategoryForAppSupport.setVisible(false);
            this.mBackgroundStartSwitchPreference.setVisible(false);
            for (int i = 0; i < this.mAppsCheckBoxPreferenceList.size(); i++) {
                getPreferenceScreen().addPreference(this.mAppsCheckBoxPreferenceList.get(i));
                this.mAppsCheckBoxPreferenceList.get(i).setVisible(false);
            }
        }
    }

    private void initAppsData() {
        addAppSupportStatus("com.tencent.tmgp.sgame");
        addAppSupportStatus("com.sankuai.meituan");
        addAppSupportStatus("com.sankuai.meituan.takeoutnew");
        addAppSupportStatus("com.baidu.BaiduMap");
        addAppSupportStatus("cn.ishansong");
        addAppSupportStatus("com.sdu.didi.psnger");
    }

    private void initCurrentFlashBackApp() {
        try {
            Cursor query = this.mDB.query("FlashBack_Current_App", null, null, null, null, null, null);
            if (query.getCount() > 0) {
                return;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("packageName", "No_Current_Running_FlashBack_App");
            this.mDB.insert("FlashBack_Current_App", null, contentValues);
            contentValues.clear();
            query.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSettingsData() {
        try {
            int i = Settings.System.getInt(getContentResolver(), "FlashBackMainSwitch", -1);
            this.mFlashBackMainSwitchState = i;
            if (i == -1) {
                this.mFlashBackMainSwitchState = 1;
                Settings.System.putInt(getContentResolver(), "FlashBackMainSwitch", 1);
            }
            if (sFlashBacSettingDebug) {
                Log.d("FBSettings", "FlashBackMainSwitch Current Result: " + this.mFlashBackMainSwitchState);
            }
            int i2 = Settings.System.getInt(getContentResolver(), "FlashBackBackgroundStartSwitch", -1);
            this.mFlashBackBackgroundStartSwitchState = i2;
            if (i2 == -1) {
                this.mFlashBackBackgroundStartSwitchState = 0;
                Settings.System.putInt(getContentResolver(), "FlashBackBackgroundStartSwitch", 0);
            }
            if (sFlashBacSettingDebug) {
                Log.d("FBSettings", "mFlashBackBackgroundStartSwitchValue Current Result: " + this.mFlashBackBackgroundStartSwitchState);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSettingsView() {
        this.mMainSwitchPreference = (CheckBoxPreference) findPreference("flashback_main_switch_preference");
        this.mBackgroundStartSwitchPreference = (CheckBoxPreference) findPreference("flashback_background_start_switch_preference");
        this.mPreferenceCategoryForBackgroundSupport = (PreferenceCategory) findPreference("flashback_support_background");
        this.mPreferenceCategoryForAppSupport = (PreferenceCategory) findPreference("flashback_support_apps");
        this.mMainSwitchPreference.setChecked(this.mFlashBackMainSwitchState == 1);
        this.mBackgroundStartSwitchPreference.setChecked(this.mFlashBackBackgroundStartSwitchState == 1);
        this.mMainSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.freeform.FlashBackSettings.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                FlashBackSettings.this.mainSwitchPreferenceOnChange(obj);
                return true;
            }
        });
        this.mBackgroundStartSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.freeform.FlashBackSettings.2
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                FlashBackSettings.this.backgroundStartSwitchPreferenceOnChange(obj);
                return true;
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x001d, code lost:
    
        r8 = r1.getString(r1.getColumnIndex("packageName"));
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x002e, code lost:
    
        if (r8.equals("com.sankuai.meituan.takeoutnew") == false) goto L13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0031, code lost:
    
        r7 = r1.getInt(r1.getColumnIndex("switch"));
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0044, code lost:
    
        r4 = r9.mPackageManager.getApplicationInfo(r8, 128);
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0046, code lost:
    
        r2 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0047, code lost:
    
        r2.printStackTrace();
        r4 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x001b, code lost:
    
        if (r1.moveToFirst() != false) goto L10;
     */
    /* JADX WARN: Removed duplicated region for block: B:23:0x006c  */
    /* JADX WARN: Removed duplicated region for block: B:31:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void initSupportAppsView(java.util.HashMap<java.lang.String, java.lang.String> r10) {
        /*
            r9 = this;
            r0 = 0
            android.database.sqlite.SQLiteDatabase r1 = r9.mDB     // Catch: java.lang.Exception -> L10
            java.lang.String r2 = "FlashBack_Support_Apps"
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)     // Catch: java.lang.Exception -> L10
            goto L15
        L10:
            r1 = move-exception
            r1.printStackTrace()
            r1 = r0
        L15:
            if (r1 == 0) goto L6a
            boolean r2 = r1.moveToFirst()
            if (r2 == 0) goto L6a
        L1d:
            java.lang.String r2 = "packageName"
            int r2 = r1.getColumnIndex(r2)
            java.lang.String r8 = r1.getString(r2)
            java.lang.String r2 = "com.sankuai.meituan.takeoutnew"
            boolean r2 = r8.equals(r2)
            if (r2 == 0) goto L31
            goto L64
        L31:
            java.lang.String r2 = "switch"
            int r2 = r1.getColumnIndex(r2)
            int r7 = r1.getInt(r2)
            android.content.pm.PackageManager r2 = r9.mPackageManager     // Catch: java.lang.Exception -> L46
            r3 = 128(0x80, float:1.8E-43)
            android.content.pm.ApplicationInfo r2 = r2.getApplicationInfo(r8, r3)     // Catch: java.lang.Exception -> L46
            r4 = r2
            goto L4b
        L46:
            r2 = move-exception
            r2.printStackTrace()
            r4 = r0
        L4b:
            if (r4 == 0) goto L64
            android.content.pm.PackageManager r2 = r9.mPackageManager
            android.graphics.drawable.Drawable r5 = r2.getApplicationIcon(r4)
            android.content.pm.PackageManager r2 = r9.mPackageManager
            java.lang.CharSequence r2 = r2.getApplicationLabel(r4)
            java.lang.String r6 = r2.toString()
            r10.put(r6, r8)
            r3 = r9
            r3.buildCheckBoxPreference(r4, r5, r6, r7, r8)
        L64:
            boolean r2 = r1.moveToNext()
            if (r2 != 0) goto L1d
        L6a:
            if (r1 == 0) goto L6f
            r1.close()
        L6f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.freeform.FlashBackSettings.initSupportAppsView(java.util.HashMap):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mainSwitchPreferenceOnChange(Object obj) {
        Boolean bool = (Boolean) obj;
        Settings.System.putInt(getContentResolver(), "FlashBackMainSwitch", bool.booleanValue() ? 1 : 0);
        this.mFlashBackMainSwitchState = bool.booleanValue() ? 1 : 0;
        if (sFlashBacSettingDebug) {
            Log.d("FBSettings", "mFlashBackMainSwitch New Result: " + this.mFlashBackMainSwitchState);
        }
        if (this.mFlashBackMainSwitchState == 0) {
            getPrefContext().stopService(this.mKillFlashBackServiceIntent);
        }
        getPrefContext().getContentResolver().notifyChange(Settings.Secure.getUriFor("FlashBackMainSwitch"), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performClick(Preference preference, Boolean bool) {
        Cursor cursor = null;
        try {
            try {
                String str = this.mPackageNameMap.get((String) preference.getTitle());
                Cursor query = this.mDB.query("FlashBack_Support_Apps", new String[]{"switch"}, "packageName=?", new String[]{str}, null, null, null, null);
                query.moveToFirst();
                query.getInt(query.getColumnIndex("switch"));
                ContentValues contentValues = new ContentValues();
                contentValues.put("switch", Integer.valueOf(bool.booleanValue() ? 1 : 0));
                this.mDB.update("FlashBack_Support_Apps", contentValues, "packageName=?", new String[]{str});
                cursor = this.mDB.query("FlashBack_Current_App", null, null, null, null, null, null, null);
                cursor.moveToFirst();
                String string = cursor.getString(cursor.getColumnIndex("packageName"));
                if (!bool.booleanValue() && str.equals(string)) {
                    getPrefContext().sendBroadcast(new Intent("miui.intent.action_kill_flashback_leash"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (cursor == null) {
                    return;
                }
            }
            cursor.close();
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    private boolean readMetaData(String str) {
        try {
            return ((Boolean) this.mPackageManager.getApplicationInfo(str, 128).metaData.get("miui_flashback_support")).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void removeCheckBoxPreference() {
        for (int i = 0; i < this.mAppsCheckBoxPreferenceList.size(); i++) {
            getPreferenceScreen().removePreference(this.mAppsCheckBoxPreferenceList.get(i));
        }
        this.mAppsCheckBoxPreferenceList.clear();
    }

    private CheckBoxPreference setCheckBoxPreferenceSummary(CheckBoxPreference checkBoxPreference, String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1873044753:
                if (str.equals("com.tencent.tmgp.sgame")) {
                    c = 0;
                    break;
                }
                break;
            case -1709882794:
                if (str.equals("com.sankuai.meituan")) {
                    c = 1;
                    break;
                }
                break;
            case -949179023:
                if (str.equals("com.sankuai.meituan.takeoutnew")) {
                    c = 2;
                    break;
                }
                break;
            case -918490570:
                if (str.equals("com.sdu.didi.psnger")) {
                    c = 3;
                    break;
                }
                break;
            case 744792033:
                if (str.equals("com.baidu.BaiduMap")) {
                    c = 4;
                    break;
                }
                break;
            case 2092235517:
                if (str.equals("cn.ishansong")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                checkBoxPreference.setSummary(R.string.flashback_wangzhe_summary);
                break;
            case 1:
                checkBoxPreference.setSummary(R.string.flashback_meituan_summary);
                break;
            case 2:
                checkBoxPreference.setSummary(R.string.flashback_meituan_waimai_summary);
                break;
            case 3:
                checkBoxPreference.setSummary(R.string.flashback_didi_summary);
                break;
            case 4:
                checkBoxPreference.setSummary(R.string.flashback_baidu_map_summary);
                break;
            case 5:
                checkBoxPreference.setSummary(R.string.flashback_shansong_summary);
                break;
        }
        return checkBoxPreference;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.flashback_settings);
        FlashBackDataHelper flashBackDataHelper = new FlashBackDataHelper(getPrefContext(), "FlashBackSupportApps.db", null, 0);
        this.mDBHelper = flashBackDataHelper;
        this.mDB = flashBackDataHelper.getWritableDatabase();
        this.mPackageManager = getPackageManager();
        this.mFlashBackMainSwitchObserver = new FlashBackMainSwitchObserver(new Handler());
        Intent intent = new Intent();
        this.mKillFlashBackServiceIntent = intent;
        intent.setPackage("com.miui.freeform");
        this.mKillFlashBackServiceIntent.setAction("miui.intent.action.FLASHBACK_WINDOW");
        this.mKillFlashBackServiceIntent.setClassName("com.miui.freeform", "com.miui.flashback.MiuiFlashbackWindowService");
        initSettingsData();
        initSettingsView();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        removeCheckBoxPreference();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mFlashBackMainSwitchObserver.observe();
        initAppsData();
        initCurrentFlashBackApp();
        initSupportAppsView(this.mPackageNameMap);
        checkVisibility();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mFlashBackMainSwitchObserver.unObserve();
    }
}
