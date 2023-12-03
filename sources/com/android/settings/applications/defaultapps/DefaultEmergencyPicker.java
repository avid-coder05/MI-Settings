package com.android.settings.applications.defaultapps;

import android.app.role.RoleManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.CollectionUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/* loaded from: classes.dex */
public class DefaultEmergencyPicker extends DefaultAppPickerFragment {
    private boolean isSystemApp(ApplicationInfo applicationInfo) {
        return (applicationInfo == null || (applicationInfo.flags & 1) == 0) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setDefaultKey$0(Boolean bool) {
        if (bool.booleanValue()) {
            return;
        }
        Log.e("DefaultEmergencyPicker", "Failed to set emergency default app.");
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<DefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        List<ResolveInfo> queryIntentActivities = this.mPm.queryIntentActivities(DefaultEmergencyPreferenceController.QUERY_INTENT, 0);
        Context context = getContext();
        Iterator<ResolveInfo> it = queryIntentActivities.iterator();
        PackageInfo packageInfo = null;
        while (it.hasNext()) {
            try {
                PackageInfo packageInfo2 = this.mPm.getPackageInfo(it.next().activityInfo.packageName, 0);
                ApplicationInfo applicationInfo = packageInfo2.applicationInfo;
                arrayList.add(new DefaultAppInfo(context, this.mPm, this.mUserId, applicationInfo));
                if (isSystemApp(applicationInfo) && (packageInfo == null || packageInfo.firstInstallTime > packageInfo2.firstInstallTime)) {
                    packageInfo = packageInfo2;
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
            if (packageInfo != null && TextUtils.isEmpty(getDefaultKey())) {
                setDefaultKey(packageInfo.packageName);
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment
    public String getConfirmationMessage(CandidateInfo candidateInfo) {
        if (Utils.isPackageDirectBootAware(getContext(), candidateInfo.getKey())) {
            return null;
        }
        return getContext().getString(R.string.direct_boot_unaware_dialog_message);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        return (String) CollectionUtils.firstOrNull(((RoleManager) getContext().getSystemService(RoleManager.class)).getRoleHolders("android.app.role.EMERGENCY"));
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 786;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.default_emergency_settings;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        String defaultKey = getDefaultKey();
        if (TextUtils.isEmpty(str) || TextUtils.equals(str, defaultKey)) {
            return false;
        }
        ((RoleManager) getContext().getSystemService(RoleManager.class)).addRoleHolderAsUser("android.app.role.EMERGENCY", str, 0, Process.myUserHandle(), AsyncTask.THREAD_POOL_EXECUTOR, new Consumer() { // from class: com.android.settings.applications.defaultapps.DefaultEmergencyPicker$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DefaultEmergencyPicker.lambda$setDefaultKey$0((Boolean) obj);
            }
        });
        return true;
    }
}
