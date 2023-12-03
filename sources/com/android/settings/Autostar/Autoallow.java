package com.android.settings.Autostar;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.miui.AppOpsUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.miuisettings.preference.Preference;
import com.miui.maml.util.AppIconsHelper;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Autoallow extends SettingsPreferenceFragment {
    View.OnClickListener ClickListener = new View.OnClickListener() { // from class: com.android.settings.Autostar.Autoallow.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AppPreference appPreference = (AppPreference) view.getTag();
            AppOpsUtils.setApplicationAutoStart(Autoallow.this.getActivity(), appPreference.getApplicationInfo().packageName, true);
            appPreference.v.setVisibility(8);
            appPreference.t.setVisibility(0);
        }
    };
    private List<ApplicationInfo> mApplicationsInfos;
    PackageManager mPm;
    PreferenceGroup mPrefGroup;
    ArrayList<AppPreference> mPrefs;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class AppPreference extends Preference {
        Autoallow fragment;
        ApplicationInfo info;
        TextView t;
        Button v;

        public AppPreference(Context context, Drawable drawable, CharSequence charSequence, Autoallow autoallow, ApplicationInfo applicationInfo) {
            super(context);
            setLayoutResource(R.layout.autorun_preference);
            setIcon(drawable);
            setTitle(charSequence);
            this.fragment = autoallow;
            this.info = applicationInfo;
        }

        public ApplicationInfo getApplicationInfo() {
            return this.info;
        }

        @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
        public void onBindView(View view) {
            super.onBindView(view);
            this.v = (Button) view.findViewById(R.id.enable);
            this.t = (TextView) view.findViewById(R.id.enable_text);
            this.v.setText(R.string.application_item_permission);
            this.v.setOnClickListener(Autoallow.this.ClickListener);
            this.v.setTag(this);
            if (AppOpsUtils.getApplicationAutoStart(view.getContext(), this.info.packageName) == 0) {
                this.v.setVisibility(8);
                this.t.setVisibility(0);
                return;
            }
            this.t.setVisibility(8);
            this.v.setVisibility(0);
        }
    }

    public static boolean isThidPartApp(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 1) == 0 && applicationInfo.uid > 10000;
    }

    private void loadApps() {
        this.mApplicationsInfos.clear();
        for (ApplicationInfo applicationInfo : this.mPm.getInstalledApplications(8192)) {
            if (isThidPartApp(applicationInfo) && AppOpsUtils.getApplicationAutoStart(getActivity(), applicationInfo.packageName) != 0) {
                this.mApplicationsInfos.add(applicationInfo);
            }
        }
    }

    void buildList() {
        getActivity();
        this.mPrefGroup.removeAll();
        this.mPrefs.clear();
        loadApps();
        for (int i = 0; i < this.mApplicationsInfos.size(); i++) {
            ApplicationInfo applicationInfo = this.mApplicationsInfos.get(i);
            try {
                AppPreference appPreference = new AppPreference(getPrefContext(), AppIconsHelper.getIconDrawable(getActivity().getApplication(), applicationInfo, this.mPm), applicationInfo.loadLabel(this.mPm), this, applicationInfo);
                this.mPrefs.add(appPreference);
                this.mPrefGroup.addPreference(appPreference);
            } catch (Exception unused) {
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return Autoallow.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.autorun);
        this.mPm = getPackageManager();
        this.mPrefGroup = (PreferenceGroup) findPreference("autorun");
        this.mApplicationsInfos = new ArrayList();
        this.mPrefs = new ArrayList<>();
        buildList();
    }
}
