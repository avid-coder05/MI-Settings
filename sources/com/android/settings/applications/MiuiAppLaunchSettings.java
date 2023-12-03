package com.android.settings.applications;

import android.app.ActionBar;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.util.List;
import miuix.appcompat.app.AlertDialog;
import miuix.springback.view.SpringBackLayout;

/* loaded from: classes.dex */
public class MiuiAppLaunchSettings extends AppInfoBase implements View.OnClickListener {
    private static final Intent sBrowserIntent = new Intent().setAction("android.intent.action.VIEW").addCategory("android.intent.category.BROWSABLE").setData(Uri.parse("http:"));
    private DropDownPreference mAppLinkState;
    private TextView mAppSummary;
    private MiuiClearDefaultsPreference mClearDefaultsPreference;
    private PreferenceCategory mDomainUrlsCategory;
    private boolean mHasDomainUrls;
    private View mHeadContent;
    private TextView mHeadTitle;
    private TextView mHomeBack;
    private ImageView mIconView;
    private boolean mIsBrowser;
    private PackageManager mPm;

    private void buildState() {
        this.mAppLinkState.setEntries(new CharSequence[]{getString(R.string.app_link_open_always), getString(R.string.app_link_open_ask), getString(R.string.app_link_open_never)});
        this.mAppLinkState.setEntryValues(new CharSequence[]{Integer.toString(2), Integer.toString(4), Integer.toString(3)});
        if (this.mIsBrowser) {
            this.mAppLinkState.setShouldDisableView(true);
            this.mAppLinkState.setEnabled(false);
            this.mDomainUrlsCategory.setShouldDisableView(true);
            this.mDomainUrlsCategory.setEnabled(false);
            return;
        }
        this.mAppLinkState.setEnabled(this.mHasDomainUrls);
        if (this.mHasDomainUrls) {
            int intentVerificationStatusAsUser = this.mPm.getIntentVerificationStatusAsUser(this.mPackageName, UserHandle.myUserId());
            this.mAppLinkState.setValue(Integer.toString(intentVerificationStatusAsUser != 0 ? intentVerificationStatusAsUser : 4));
            updateSummary(this.mAppLinkState.getValue());
            this.mAppLinkState.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.applications.MiuiAppLaunchSettings.1
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    String str = (String) obj;
                    MiuiAppLaunchSettings.this.updateSummary(str);
                    return MiuiAppLaunchSettings.this.updateAppLinkState(Integer.parseInt(str));
                }
            });
        }
    }

    private CharSequence[] getEntries(String str) {
        ArraySet<String> handledDomains = Utils.getHandledDomains(this.mPm, str);
        return (CharSequence[]) handledDomains.toArray(new CharSequence[handledDomains.size()]);
    }

    private boolean isBrowserApp(String str) {
        Intent intent = sBrowserIntent;
        intent.setPackage(str);
        List queryIntentActivitiesAsUser = this.mPm.queryIntentActivitiesAsUser(intent, 131072, UserHandle.myUserId());
        int size = queryIntentActivitiesAsUser.size();
        for (int i = 0; i < size; i++) {
            ResolveInfo resolveInfo = (ResolveInfo) queryIntentActivitiesAsUser.get(i);
            if (resolveInfo.activityInfo != null && resolveInfo.handleAllWebDataURI) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateAppLinkState(int i) {
        if (this.mIsBrowser) {
            return false;
        }
        int myUserId = UserHandle.myUserId();
        if (this.mPm.getIntentVerificationStatusAsUser(this.mPackageName, myUserId) == i) {
            return false;
        }
        boolean updateIntentVerificationStatusAsUser = this.mPm.updateIntentVerificationStatusAsUser(this.mPackageName, i, myUserId);
        if (updateIntentVerificationStatusAsUser) {
            return i == this.mPm.getIntentVerificationStatusAsUser(this.mPackageName, myUserId);
        }
        Log.e("MiuiAppLaunchSettings", "Couldn't update intent verification status!");
        return updateIntentVerificationStatusAsUser;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSummary(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        CharSequence[] entryValues = this.mAppLinkState.getEntryValues();
        CharSequence[] entries = this.mAppLinkState.getEntries();
        for (int i = 0; i < entryValues.length; i++) {
            if (str.equals(entryValues[i])) {
                this.mAppLinkState.setSummary(entries[i]);
                return;
            }
        }
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() != 16908332) {
            return;
        }
        finish();
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setThemeRes(R.style.Theme_AppLaunchSettings);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.app_launch_settings, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        View view = (View) getListView().getParent();
        if (view instanceof SpringBackLayout) {
            view.setEnabled(false);
        }
        addPreferencesFromResource(R.xml.app_launch_settings);
        if (this.mPackageInfo == null) {
            return inflate;
        }
        this.mPm = getActivity().getPackageManager();
        this.mIsBrowser = isBrowserApp(this.mPackageName);
        this.mHasDomainUrls = (this.mAppEntry.info.privateFlags & 16) != 0;
        this.mIconView = (ImageView) inflate.findViewById(R.id.app_detail_icon);
        this.mAppSummary = (TextView) inflate.findViewById(R.id.app_detail_summary);
        this.mHeadContent = inflate.findViewById(R.id.app_head);
        ((TextView) inflate.findViewById(R.id.app_detail_title)).setText(this.mPackageInfo.applicationInfo.loadLabel(this.mPm));
        this.mAppSummary.setText(this.mPackageInfo.versionName);
        this.mIconView.setFocusable(true);
        this.mIconView.setFocusableInTouchMode(true);
        this.mIconView.requestFocus();
        this.mIconView.setImageDrawable(IconDrawableFactory.newInstance(getActivity()).getBadgedIcon(this.mPackageInfo.applicationInfo));
        this.mAppLinkState = (DropDownPreference) findPreference("app_link_state");
        this.mDomainUrlsCategory = (PreferenceCategory) findPreference("app_launch_supported_domain_urls");
        this.mClearDefaultsPreference = (MiuiClearDefaultsPreference) findPreference("app_launch_clear_defaults");
        if (!this.mIsBrowser) {
            CharSequence[] entries = getEntries(this.mPackageName);
            this.mDomainUrlsCategory.removeAll();
            for (CharSequence charSequence : entries) {
                ValuePreference valuePreference = new ValuePreference(getPrefContext());
                valuePreference.setTitle(charSequence);
                valuePreference.setShowRightArrow(false);
                this.mDomainUrlsCategory.addPreference(valuePreference);
            }
        }
        return inflate;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        TextView textView = this.mHeadTitle;
        if (textView != null) {
            textView.setText(this.mPackageInfo.applicationInfo.loadLabel(this.mPm));
        }
        buildState();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        ActionBar actionBar;
        super.onStart();
        FragmentActivity activity = getActivity();
        if (activity == null || (actionBar = getActionBar()) == null) {
            return;
        }
        actionBar.setDisplayOptions(16, 16);
        actionBar.setCustomView(R.layout.app_title_layout);
        View customView = actionBar.getCustomView();
        TextView textView = (TextView) customView.findViewById(16908332);
        this.mHomeBack = textView;
        textView.setOnClickListener(this);
        if (((UiModeManager) activity.getSystemService(UiModeManager.class)).getNightMode() == 2) {
            this.mHomeBack.setCompoundDrawablesWithIntrinsicBounds(activity.getResources().getDrawable(R.drawable.miuix_appcompat_action_bar_back_dark), (Drawable) null, (Drawable) null, (Drawable) null);
        } else {
            this.mHomeBack.setCompoundDrawablesWithIntrinsicBounds(activity.getResources().getDrawable(R.drawable.miuix_appcompat_action_bar_back_light), (Drawable) null, (Drawable) null, (Drawable) null);
        }
        this.mHeadTitle = (TextView) customView.findViewById(16908310);
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        MiuiClearDefaultsPreference miuiClearDefaultsPreference = this.mClearDefaultsPreference;
        if (miuiClearDefaultsPreference != null) {
            miuiClearDefaultsPreference.setPackageName(this.mPackageName);
            this.mClearDefaultsPreference.setAppEntry(this.mAppEntry);
            return true;
        }
        return true;
    }
}
