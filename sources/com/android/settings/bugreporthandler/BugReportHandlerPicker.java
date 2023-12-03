package com.android.settings.bugreporthandler;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.util.Log;
import android.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.applications.defaultapps.DefaultAppPickerFragment;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.RadioButtonPreference;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BugReportHandlerPicker extends DefaultAppPickerFragment {
    private BugReportHandlerUtil mBugReportHandlerUtil;
    private FooterPreference mFooter;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BugreportHandlerAppInfo extends DefaultAppInfo {
        private final Context mContext;

        BugreportHandlerAppInfo(Context context, PackageManager packageManager, int i, PackageItemInfo packageItemInfo, String str) {
            super(context, packageManager, i, packageItemInfo, str, true);
            this.mContext = context;
        }

        @Override // com.android.settingslib.applications.DefaultAppInfo, com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            PackageItemInfo packageItemInfo = this.packageItemInfo;
            if (packageItemInfo != null) {
                return BugReportHandlerPicker.getKey(packageItemInfo.packageName, this.userId);
            }
            return null;
        }

        @Override // com.android.settingslib.applications.DefaultAppInfo, com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            PackageItemInfo packageItemInfo;
            if (this.mContext == null || (packageItemInfo = this.packageItemInfo) == null) {
                return null;
            }
            return "com.android.shell".equals(packageItemInfo.packageName) ? this.mContext.getString(17039666) : super.loadLabel();
        }
    }

    private BugReportHandlerUtil getBugReportHandlerUtil() {
        if (this.mBugReportHandlerUtil == null) {
            setBugReportHandlerUtil(createDefaultBugReportHandlerUtil());
        }
        return this.mBugReportHandlerUtil;
    }

    private String getDescription(String str, int i) {
        Context context = getContext();
        if ("com.android.shell".equals(str)) {
            return context.getString(R.string.system_default_app_subtext);
        }
        if (this.mUserManager.getUserProfiles().size() < 2) {
            return "";
        }
        UserInfo userInfo = this.mUserManager.getUserInfo(i);
        return (userInfo == null || !userInfo.isManagedProfile()) ? context.getString(R.string.personal_profile_app_subtext) : context.getString(R.string.work_profile_app_subtext);
    }

    private static String getHandlerApp(String str) {
        return str.substring(0, str.lastIndexOf(35));
    }

    private static int getHandlerUser(String str) {
        try {
            return Integer.parseInt(str.substring(str.lastIndexOf(35) + 1));
        } catch (NumberFormatException unused) {
            Log.e("BugReportHandlerPicker", "Failed to get handlerUser");
            return 0;
        }
    }

    static String getKey(String str, int i) {
        return str + "#" + i;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected void addStaticPreferences(PreferenceScreen preferenceScreen) {
        if (this.mFooter == null) {
            FooterPreference footerPreference = new FooterPreference(preferenceScreen.getContext());
            this.mFooter = footerPreference;
            footerPreference.setIcon(R.drawable.ic_info_outline_24dp);
            this.mFooter.setSingleLineTitle(false);
            this.mFooter.setTitle(R.string.bug_report_handler_picker_footer_text);
            this.mFooter.setSelectable(false);
        }
        preferenceScreen.addPreference(this.mFooter);
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void bindPreferenceExtra(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        super.bindPreferenceExtra(radioButtonPreference, str, candidateInfo, str2, str3);
        radioButtonPreference.setAppendixVisibility(8);
    }

    DefaultAppInfo createDefaultAppInfo(Context context, PackageManager packageManager, int i, PackageItemInfo packageItemInfo) {
        return new BugreportHandlerAppInfo(context, packageManager, i, packageItemInfo, getDescription(packageItemInfo.packageName, i));
    }

    BugReportHandlerUtil createDefaultBugReportHandlerUtil() {
        return new BugReportHandlerUtil();
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<DefaultAppInfo> getCandidates() {
        Context context = getContext();
        List<Pair<ApplicationInfo, Integer>> validBugReportHandlerInfos = getBugReportHandlerUtil().getValidBugReportHandlerInfos(context);
        ArrayList arrayList = new ArrayList();
        for (Pair<ApplicationInfo, Integer> pair : validBugReportHandlerInfos) {
            arrayList.add(createDefaultAppInfo(context, this.mPm, ((Integer) pair.second).intValue(), (PackageItemInfo) pair.first));
        }
        return arrayList;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        Pair<String, Integer> currentBugReportHandlerAppAndUser = getBugReportHandlerUtil().getCurrentBugReportHandlerAppAndUser(getContext());
        return getKey((String) currentBugReportHandlerAppAndUser.first, ((Integer) currentBugReportHandlerAppAndUser.second).intValue());
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1808;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.bug_report_handler_settings;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context)) {
            return;
        }
        getActivity().finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void onSelectionPerformed(boolean z) {
        super.onSelectionPerformed(z);
        if (!z) {
            getBugReportHandlerUtil().showInvalidChoiceToast(getContext());
            updateCandidates();
            return;
        }
        FragmentActivity activity = getActivity();
        Intent intent = activity == null ? null : activity.getIntent();
        if (intent == null || !"android.settings.BUGREPORT_HANDLER_SETTINGS".equals(intent.getAction())) {
            return;
        }
        getActivity().finish();
    }

    void setBugReportHandlerUtil(BugReportHandlerUtil bugReportHandlerUtil) {
        this.mBugReportHandlerUtil = bugReportHandlerUtil;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        return getBugReportHandlerUtil().setCurrentBugReportHandlerAppAndUser(getContext(), getHandlerApp(str), getHandlerUser(str));
    }
}
