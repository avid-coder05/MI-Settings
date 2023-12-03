package com.android.settings.applications.defaultapps;

import android.app.role.RoleManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import com.android.settings.R;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.widget.CandidateInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/* loaded from: classes.dex */
public class DefaultBrowserPicker extends DefaultAppPickerFragment {
    public static boolean addBrowserRoleHolderAsUser(Context context, String str) {
        if (Build.VERSION.SDK_INT >= 29) {
            RoleManager roleManager = (RoleManager) context.getSystemService(RoleManager.class);
            Executor mainExecutor = context.getMainExecutor();
            Consumer<Boolean> consumer = new Consumer<Boolean>() { // from class: com.android.settings.applications.defaultapps.DefaultBrowserPicker.1
                @Override // java.util.function.Consumer
                public void accept(Boolean bool) {
                }
            };
            try {
                Method declaredMethod = RoleManager.class.getDeclaredMethod("addRoleHolderAsUser", String.class, String.class, Integer.TYPE, UserHandle.class, Executor.class, Consumer.class);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(roleManager, "android.app.role.BROWSER", str, 0, Process.myUserHandle(), mainExecutor, consumer);
                return true;
            } catch (Exception e) {
                Log.i("DefaultBrowserPicker", "addBrowserRoleHolderAsUser", e);
            }
        }
        return false;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<DefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        Context context = getContext();
        for (ResolveInfo resolveInfo : DefaultBrowserPreferenceController.getCandidates(this.mPm, this.mUserId)) {
            try {
                PackageManager packageManager = this.mPm;
                int i = this.mUserId;
                arrayList.add(new DefaultAppInfo(context, packageManager, i, packageManager.getApplicationInfoAsUser(resolveInfo.activityInfo.packageName, 0, i)));
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment
    public String getConfirmationMessage(CandidateInfo candidateInfo) {
        if ("com.android.browser".equals(candidateInfo.getKey()) || miui.os.Build.IS_INTERNATIONAL_BUILD) {
            return null;
        }
        return getContext().getString(R.string.modify_default_browser_message);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        return this.mPm.getDefaultBrowserPackageNameAsUser(this.mUserId);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 785;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    protected int getPreferenceScreenResId() {
        return R.xml.default_browser_settings;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        if (addBrowserRoleHolderAsUser(getContext(), str)) {
            return true;
        }
        return this.mPm.setDefaultBrowserPackageNameAsUser(str, this.mUserId);
    }
}
