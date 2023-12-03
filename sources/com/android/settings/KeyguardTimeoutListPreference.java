package com.android.settings;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.compat.RestrictedLockUtilsCompat;
import com.android.settingslib.RestrictedLockUtils;
import java.util.ArrayList;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class KeyguardTimeoutListPreference extends KeyguardRestrictedListPreference implements Preference.OnPreferenceChangeListener {
    private Context mContext;
    private boolean mIsOled;

    public KeyguardTimeoutListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        this.mIsOled = "oled".equals(SystemProperties.get("ro.vendor.display.type")) || "oled".equals(SystemProperties.get("ro.display.type"));
        setOnPreferenceChangeListener(this);
    }

    public void disableUnusableTimeouts() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        long maximumTimeToLock = devicePolicyManager != null ? devicePolicyManager.getMaximumTimeToLock(null) : 0L;
        updateDisableState(maximumTimeToLock == 0);
        if (this.mIsOled && !isDisabledByAdmin()) {
            maximumTimeToLock = maximumTimeToLock == 0 ? 2147483646L : Math.min(maximumTimeToLock, 2147483646L);
        }
        if (maximumTimeToLock == 0) {
            return;
        }
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < entryValues.length; i++) {
            if (Long.parseLong(entryValues[i].toString()) <= maximumTimeToLock) {
                arrayList.add(entries[i]);
                arrayList2.add(entryValues[i]);
            }
        }
        if (arrayList2.size() == 0) {
            Log.e("KeyguardTimeoutListPreference", "screen time out disabled, maxTimeout =" + maximumTimeToLock);
            updateNoEntriesState();
            return;
        }
        setEnabled(true);
        if (arrayList.size() == entries.length && arrayList2.size() == entryValues.length) {
            return;
        }
        int parseInt = Integer.parseInt(getValue());
        setEntries((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]));
        setEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]));
        if (parseInt <= maximumTimeToLock) {
            setValue(String.valueOf(parseInt));
        } else if (arrayList2.size() > 0 && Long.parseLong(((CharSequence) arrayList2.get(arrayList2.size() - 1)).toString()) == maximumTimeToLock) {
            setValue(String.valueOf(maximumTimeToLock));
        }
    }

    public CharSequence getListPreferenceSummary(long j) {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();
        int i = 0;
        for (int i2 = 0; i2 < entryValues.length; i2++) {
            if (j >= Long.parseLong(entryValues[i2].toString())) {
                i = i2;
            }
        }
        try {
            if (Integer.parseInt(entryValues[i].toString()) != j) {
                Settings.System.putInt(this.mContext.getContentResolver(), "screen_off_timeout", Integer.parseInt(entryValues[i].toString()));
            }
        } catch (NumberFormatException e) {
            Log.e("KeyguardTimeoutListPreference", "could not persist screen timeout setting", e);
        }
        return entries[i];
    }

    public void hideListView() {
        if (getDialog() != null) {
            getDialog().dismiss();
        }
    }

    @Override // com.android.settings.KeyguardRestrictedListPreference, com.android.settingslib.miuisettings.preference.ListPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = this.mValueRight;
        if (textView != null) {
            textView.setVisibility(isDisabledByAdmin() ? 8 : 0);
            updateTimeoutPreferenceSummary();
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        try {
            Settings.System.putInt(this.mContext.getContentResolver(), "screen_off_timeout", Integer.parseInt((String) obj));
            updateTimeoutPreferenceSummary();
            return true;
        } catch (NumberFormatException e) {
            Log.e("KeyguardTimeoutListPreference", "could not persist screen timeout setting", e);
            return true;
        }
    }

    @Override // com.android.settings.KeyguardRestrictedListPreference, com.android.settings.CustomListPreference
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        disableUnusableTimeouts();
        super.onPrepareDialogBuilder(builder);
        final RestrictedLockUtils.EnforcedAdmin checkIfMaximumTimeToLockIsSet = RestrictedLockUtilsCompat.checkIfMaximumTimeToLockIsSet(this.mContext);
        if (checkIfMaximumTimeToLockIsSet == null) {
            builder.setView((View) null);
            return;
        }
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.admin_disabled_other_options_footer, (ViewGroup) null);
        builder.setView(inflate);
        inflate.findViewById(R.id.admin_disabled_other_options).findViewById(R.id.admin_more_details_link).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.KeyguardTimeoutListPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(KeyguardTimeoutListPreference.this.mContext, checkIfMaximumTimeToLockIsSet);
            }
        });
    }

    public void updateDisableState(boolean z) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsCompat.checkIfRestrictionEnforced(this.mContext, "no_config_screen_timeout", UserHandle.myUserId());
        if (!z || checkIfRestrictionEnforced == null) {
            setDisabledByAdmin(null);
        } else {
            setDisabledByAdmin(checkIfRestrictionEnforced);
        }
    }

    public void updateNoEntriesState() {
        RestrictedLockUtils.EnforcedAdmin checkIfMaximumTimeToLockIsSet = RestrictedLockUtilsCompat.checkIfMaximumTimeToLockIsSet(this.mContext);
        if (checkIfMaximumTimeToLockIsSet == null) {
            setEnabled(false);
            setDisabledByAdmin(null);
            return;
        }
        CharSequence[] charSequenceArr = new CharSequence[0];
        setEntries(charSequenceArr);
        setEntryValues(charSequenceArr);
        setEnabled(true);
        setDisabledByAdmin(checkIfMaximumTimeToLockIsSet);
    }

    public void updateTimeoutPreferenceSummary() {
        long j = Settings.System.getLong(this.mContext.getContentResolver(), "screen_off_timeout", 30000L);
        String str = "";
        if (j >= 0) {
            if (2147483647L != j || this.mIsOled) {
                CharSequence[] entries = getEntries();
                if (entries != null && entries.length != 0) {
                    str = this.mContext.getString(R.string.screen_timeout_summary, getListPreferenceSummary(j));
                }
            } else {
                str = this.mContext.getString(R.string.screen_never_timeout_title);
            }
        }
        TextView textView = this.mValueRight;
        if (textView != null) {
            textView.setText(str);
        }
    }
}
