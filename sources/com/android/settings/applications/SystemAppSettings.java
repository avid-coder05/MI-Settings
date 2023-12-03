package com.android.settings.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.report.InternationalCompat;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.widget.FixedIconSizePreference;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.cloud.Constants;
import miui.payment.PaymentManager;

/* loaded from: classes.dex */
public class SystemAppSettings extends SettingsPreferenceFragment {
    private LoadLabelTask mLoadLabelTask;
    private HashSet<String> whiteList = new HashSet<>();
    private HashMap<String, PreferenceActivity.Header> mCls2SystemHeader = new HashMap<>();

    /* loaded from: classes.dex */
    public final class LoadLabelTask extends AsyncTask<Void, Void, Void> {
        LoadLabelTask() {
        }

        private void addPreference(HashMap<String, PreferenceActivity.Header> hashMap) {
            if (hashMap == null || hashMap.entrySet() == null) {
                return;
            }
            for (Map.Entry<String, PreferenceActivity.Header> entry : hashMap.entrySet()) {
                FixedIconSizePreference fixedIconSizePreference = new FixedIconSizePreference(SystemAppSettings.this.getPrefContext(), true);
                PreferenceActivity.Header value = entry.getValue();
                fixedIconSizePreference.setIntent(value.intent);
                fixedIconSizePreference.setTitle(value.title);
                SystemAppSettings systemAppSettings = SystemAppSettings.this;
                fixedIconSizePreference.setIcon(systemAppSettings.getAppIcon(systemAppSettings.getPrefContext(), value));
                SystemAppSettings.this.getPreferenceScreen().addPreference(fixedIconSizePreference);
            }
        }

        private void updateHeader(Context context, PreferenceActivity.Header header) {
            ActivityInfo activityInfo;
            PackageManager packageManager = context.getPackageManager();
            ResolveInfo resolveActivity = packageManager.resolveActivity(header.intent, 0);
            if (resolveActivity == null || (activityInfo = resolveActivity.activityInfo) == null) {
                return;
            }
            header.title = activityInfo.loadLabel(packageManager);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            if (SystemAppSettings.this.mCls2SystemHeader != null && SystemAppSettings.this.mCls2SystemHeader.entrySet() != null) {
                Iterator it = SystemAppSettings.this.mCls2SystemHeader.entrySet().iterator();
                while (it.hasNext()) {
                    updateHeader(SystemAppSettings.this.getPrefContext(), (PreferenceActivity.Header) ((Map.Entry) it.next()).getValue());
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            addPreference(SystemAppSettings.this.mCls2SystemHeader);
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
            PreferenceScreen preferenceScreen = SystemAppSettings.this.getPreferenceScreen();
            if (preferenceScreen != null) {
                preferenceScreen.removeAll();
            }
        }
    }

    private void addSystemAppHeader(ResolveInfo resolveInfo) {
        Intent intent = new Intent("miui.intent.action.APP_SETTINGS");
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        intent.setClassName(activityInfo.packageName, activityInfo.name);
        if (SettingsFeatures.isSplitTablet(getContext())) {
            intent.addMiuiFlags(16);
        }
        PreferenceActivity.Header header = new PreferenceActivity.Header();
        header.title = "system_app";
        header.intent = intent;
        if (resolveInfo.activityInfo.packageName.equals("com.android.phone") && UserHandle.myUserId() != 0) {
            header.id = -1000L;
        }
        if (this.mCls2SystemHeader.containsKey(resolveInfo.activityInfo.name)) {
            return;
        }
        this.mCls2SystemHeader.put(resolveInfo.activityInfo.name, header);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Drawable getAppIcon(Context context, PreferenceActivity.Header header) {
        PackageManager packageManager = context.getPackageManager();
        ResolveInfo resolveActivity = packageManager.resolveActivity(header.intent, 0);
        if (resolveActivity != null) {
            ApplicationInfo applicationInfo = resolveActivity.activityInfo.applicationInfo;
            return (!SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME.equals(applicationInfo.packageName) || header.iconRes == 0) ? applicationInfo.loadIcon(packageManager) : getPrefContext().getResources().getDrawable(header.iconRes);
        }
        return null;
    }

    private PreferenceActivity.Header getMiMoneyHeader() {
        PreferenceActivity.Header header = new PreferenceActivity.Header();
        header.title = getString(R.string.xiaomi_money_service);
        header.id = R.id.mimoney_settings;
        header.intent = new Intent("com.xiaomi.action.VIEW_MILI_CENTER");
        return header;
    }

    private void initWhiteList() {
        this.whiteList.add("com.mi.health");
        this.whiteList.add("com.android.soundrecorder");
    }

    private void insertSystemAppHeader() {
        List<ResolveInfo> queryIntentActivities = getPrefContext().getPackageManager().queryIntentActivities(new Intent("miui.intent.action.APP_SETTINGS"), 0);
        boolean isMibiServiceDisabled = PaymentManager.get(getPrefContext()).isMibiServiceDisabled();
        this.mCls2SystemHeader.clear();
        this.mLoadLabelTask = null;
        int i = -1;
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            if (resolveInfo.system || this.whiteList.contains(resolveInfo.activityInfo.packageName)) {
                if (!resolveInfo.activityInfo.packageName.equals("com.miui.voiceassist") && !"com.miui.googlebase.ui.GmsCoreSettings".equals(resolveInfo.activityInfo.name) && (!resolveInfo.activityInfo.packageName.equals(Constants.CLOUDSERVICE_PACKAGE_NAME) || !MiuiUtils.isDeviceManaged(getPrefContext()))) {
                    if (Utils.isVoiceCapable(getPrefContext()) || !resolveInfo.activityInfo.packageName.equals("com.android.phone")) {
                        if (i >= 1000 && resolveInfo.priority < 1000 && !isMibiServiceDisabled) {
                            this.mCls2SystemHeader.put(resolveInfo.activityInfo.name, getMiMoneyHeader());
                        }
                        addSystemAppHeader(resolveInfo);
                        i = resolveInfo.priority;
                    }
                }
            }
        }
        loadAppLabel();
    }

    private void loadAppLabel() {
        if (this.mLoadLabelTask == null) {
            LoadLabelTask loadLabelTask = new LoadLabelTask();
            this.mLoadLabelTask = loadLabelTask;
            loadLabelTask.execute(new Void[0]);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.system_app_settings);
        initWhiteList();
        InternationalCompat.trackReportEvent("setting_Apps_systemapp");
        if (this.mCls2SystemHeader == null) {
            this.mCls2SystemHeader = new HashMap<>();
        }
        insertSystemAppHeader();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mCls2SystemHeader = null;
    }
}
