package com.android.settings.Autostar;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.miui.AppOpsUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.miuisettings.preference.Preference;
import com.miui.maml.util.AppIconsHelper;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AutoMangement extends SettingsPreferenceFragment {
    TextView EmptyView;
    FrameLayout count;
    Activity mActivity;
    private List<ApplicationInfo> mApplicationsInfos;
    View.OnClickListener mClickListener = new View.OnClickListener() { // from class: com.android.settings.Autostar.AutoMangement.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ApplicationInfo applicationInfo = (ApplicationInfo) view.getTag();
            AppOpsUtils.setApplicationAutoStart(AutoMangement.this.getActivity(), applicationInfo.packageName, false);
            ((ActivityManager) AutoMangement.this.getSystemService("activity")).forceStopPackage(applicationInfo.packageName);
            AutoMangement.this.buildList();
            AutoMangement autoMangement = AutoMangement.this;
            PreferenceGroup preferenceGroup = autoMangement.mPrefGroup;
            Resources resources = autoMangement.getResources();
            int i = R.plurals.auto_startup_count;
            preferenceGroup.setTitle(resources.getQuantityString(i, AutoMangement.this.mApplicationsInfos.size(), Integer.valueOf(AutoMangement.this.mApplicationsInfos.size())));
            AutoMangement autoMangement2 = AutoMangement.this;
            autoMangement2.mtext.setText(autoMangement2.getResources().getQuantityString(i, AutoMangement.this.mApplicationsInfos.size(), Integer.valueOf(AutoMangement.this.mApplicationsInfos.size())));
            if (AutoMangement.this.mApplicationsInfos.isEmpty()) {
                AutoMangement.this.count.setVisibility(4);
                AutoMangement.this.EmptyView.setVisibility(0);
                return;
            }
            AutoMangement.this.count.setVisibility(0);
            AutoMangement.this.EmptyView.setVisibility(4);
        }
    };
    View.OnClickListener mClickadd = new View.OnClickListener() { // from class: com.android.settings.Autostar.AutoMangement.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AutoMangement autoMangement = AutoMangement.this;
            autoMangement.startFragment(autoMangement.mFragment, Autoallow.class.getName(), 0, (Bundle) null, R.string.add_auto_startup_application);
        }
    };
    Fragment mFragment;
    PackageManager mPm;
    PreferenceGroup mPrefGroup;
    ArrayList<AppPreference> mPrefs;
    View mView;
    TextView mtext;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class AppPreference extends Preference {
        AutoMangement fragment;
        ApplicationInfo info;

        public AppPreference(Context context, Drawable drawable, CharSequence charSequence, AutoMangement autoMangement, ApplicationInfo applicationInfo) {
            super(context);
            setLayoutResource(R.layout.autorun_preference);
            setIcon(drawable);
            setTitle(charSequence);
            this.fragment = autoMangement;
            this.info = applicationInfo;
        }

        @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
        public void onBindView(View view) {
            super.onBindView(view);
            Button button = (Button) view.findViewById(R.id.enable);
            button.setOnClickListener(AutoMangement.this.mClickListener);
            button.setTag(this.info);
        }
    }

    public static boolean isThidPartApp(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 1) == 0 && applicationInfo.uid > 10000;
    }

    private void loadApps() {
        this.mApplicationsInfos.clear();
        for (ApplicationInfo applicationInfo : this.mPm.getInstalledApplications(8192)) {
            if (isThidPartApp(applicationInfo) && AppOpsUtils.getApplicationAutoStart(getActivity(), applicationInfo.packageName) == 0) {
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
        return AutoMangement.class.getName();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        buildList();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.autorun);
        this.mPm = getPackageManager();
        this.mFragment = this;
        this.mActivity = getActivity();
        this.mPrefGroup = (PreferenceGroup) findPreference("autorun");
        this.mApplicationsInfos = new ArrayList();
        this.mPrefs = new ArrayList<>();
        buildList();
        this.mPrefGroup.setTitle(getResources().getQuantityString(R.plurals.auto_startup_count, this.mApplicationsInfos.size(), Integer.valueOf(this.mApplicationsInfos.size())));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.autorun_pref, viewGroup, false);
        this.mView = inflate;
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        Button button = (Button) this.mView.findViewById(R.id.btn);
        this.EmptyView = (TextView) this.mView.findViewById(16908292);
        this.mtext = (TextView) this.mView.findViewById(R.id.startup_count);
        this.count = (FrameLayout) this.mView.findViewById(R.id.count);
        button.setOnClickListener(this.mClickadd);
        return this.mView;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        buildList();
        this.mtext.setText(getResources().getQuantityString(R.plurals.auto_startup_count, this.mApplicationsInfos.size(), Integer.valueOf(this.mApplicationsInfos.size())));
        if (this.mApplicationsInfos.isEmpty()) {
            this.count.setVisibility(4);
            this.EmptyView.setVisibility(0);
            return;
        }
        this.count.setVisibility(0);
        this.EmptyView.setVisibility(4);
    }
}
