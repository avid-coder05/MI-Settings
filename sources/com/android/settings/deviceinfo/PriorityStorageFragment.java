package com.android.settings.deviceinfo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.util.List;

/* loaded from: classes.dex */
public class PriorityStorageFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private PackageManager mPm;

    private void createPreference() {
        Bundle bundle;
        List<ResolveInfo> queryBroadcastReceivers = this.mPm.queryBroadcastReceivers(new Intent("miui.intent.action.PRIORITY_STORAGE"), 640);
        if (queryBroadcastReceivers == null) {
            return;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (ResolveInfo resolveInfo : queryBroadcastReceivers) {
            DropDownPreference dropDownPreference = new DropDownPreference(getPrefContext());
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            ComponentName componentName = new ComponentName(activityInfo.packageName, activityInfo.name);
            String charSequence = resolveInfo.loadLabel(this.mPm).toString();
            dropDownPreference.setKey(componentName.flattenToString());
            dropDownPreference.setEntries(R.array.priority_storage_entries);
            dropDownPreference.setEntryValues(R.array.priority_storage_value);
            int componentEnabledSetting = this.mPm.getComponentEnabledSetting(componentName);
            boolean z = true;
            int i = (componentEnabledSetting == 1 || (componentEnabledSetting == 0 && (bundle = resolveInfo.activityInfo.metaData) != null && bundle.getBoolean("miui.intent.extra.SET_PRIORITY_DEFAULT"))) ? 1 : 0;
            dropDownPreference.setValueIndex(i);
            dropDownPreference.setSummary(dropDownPreference.getEntries()[i]);
            dropDownPreference.setPersistent(false);
            dropDownPreference.setTitle(charSequence);
            Bundle bundle2 = resolveInfo.activityInfo.metaData;
            if (bundle2 != null) {
                z = bundle2.getInt("miui.intent.extra.PRIORITY_STORAGE_KILL_APP") == 0;
            }
            Intent intent = new Intent();
            intent.putExtra("extra_kill_app", z);
            dropDownPreference.setIntent(intent);
            dropDownPreference.setOnPreferenceChangeListener(this);
            dropDownPreference.setOnPreferenceClickListener(this);
            preferenceScreen.addPreference(dropDownPreference);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return PriorityStorageFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mPm = getPackageManager();
        addPreferencesFromResource(R.xml.priority_storage);
        createPreference();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int intValue = Integer.valueOf((String) obj).intValue();
        ComponentName unflattenFromString = ComponentName.unflattenFromString(preference.getKey());
        DropDownPreference dropDownPreference = (DropDownPreference) preference;
        this.mPm.setComponentEnabledSetting(unflattenFromString, intValue == 1 ? 1 : 2, !dropDownPreference.getIntent().getBooleanExtra("extra_kill_app", true));
        dropDownPreference.setSummary(dropDownPreference.getEntries()[intValue]);
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return true;
    }
}
