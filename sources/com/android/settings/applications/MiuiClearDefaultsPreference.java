package com.android.settings.applications;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.miuisettings.preference.Preference;
import com.android.settingslib.util.ToastUtil;

/* loaded from: classes.dex */
public class MiuiClearDefaultsPreference extends Preference {
    protected ApplicationsState.AppEntry mAppEntry;
    private AppWidgetManager mAppWidgetManager;
    private boolean mHasDefault;
    private String mPackageName;
    private PackageManager mPm;
    private IUsbManager mUsbManager;

    public MiuiClearDefaultsPreference(Context context) {
        super(context);
        this.mHasDefault = false;
        init(context);
    }

    public MiuiClearDefaultsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHasDefault = false;
        init(context);
    }

    public MiuiClearDefaultsPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHasDefault = false;
        init(context);
    }

    public MiuiClearDefaultsPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mHasDefault = false;
        init(context);
    }

    private void init(Context context) {
        this.mAppWidgetManager = AppWidgetManager.getInstance(context);
        this.mPm = context.getPackageManager();
        this.mUsbManager = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDefaultBrowser(String str) {
        return str.equals(this.mPm.getDefaultBrowserPackageNameAsUser(UserHandle.myUserId()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetLaunchDefaultsUi() {
        setSummary(R.string.auto_launch_disable_text);
        this.mHasDefault = false;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.applications.MiuiClearDefaultsPreference.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(androidx.preference.Preference preference) {
                if (!MiuiClearDefaultsPreference.this.mHasDefault) {
                    ToastUtil.show(MiuiClearDefaultsPreference.this.getContext(), R.string.auto_launch_disable_text, 0);
                    return true;
                }
                if (MiuiClearDefaultsPreference.this.mUsbManager != null) {
                    int myUserId = UserHandle.myUserId();
                    MiuiClearDefaultsPreference.this.mPm.clearPackagePreferredActivities(MiuiClearDefaultsPreference.this.mPackageName);
                    MiuiClearDefaultsPreference miuiClearDefaultsPreference = MiuiClearDefaultsPreference.this;
                    if (miuiClearDefaultsPreference.isDefaultBrowser(miuiClearDefaultsPreference.mPackageName)) {
                        MiuiClearDefaultsPreference.this.mPm.setDefaultBrowserPackageNameAsUser(null, myUserId);
                    }
                    try {
                        MiuiClearDefaultsPreference.this.mUsbManager.clearDefaults(MiuiClearDefaultsPreference.this.mPackageName, myUserId);
                    } catch (RemoteException e) {
                        Log.e("MiuiClearDefaultsPreference", "mUsbManager.clearDefaults", e);
                    }
                    MiuiClearDefaultsPreference.this.mAppWidgetManager.setBindAppWidgetPermission(MiuiClearDefaultsPreference.this.mPackageName, false);
                    MiuiClearDefaultsPreference.this.resetLaunchDefaultsUi();
                }
                return true;
            }
        });
        updateUI(preferenceViewHolder.itemView);
    }

    public void setAppEntry(ApplicationsState.AppEntry appEntry) {
        this.mAppEntry = appEntry;
    }

    public void setPackageName(String str) {
        this.mPackageName = str;
    }

    public boolean updateUI(View view) {
        final boolean hasBindAppWidgetPermission = this.mAppWidgetManager.hasBindAppWidgetPermission(this.mAppEntry.info.packageName);
        final boolean z = AppUtils.hasPreferredActivities(this.mPm, this.mPackageName) || isDefaultBrowser(this.mPackageName) || AppUtils.hasUsbDefaults(this.mUsbManager, this.mPackageName);
        if (z || hasBindAppWidgetPermission) {
            view.post(new Runnable() { // from class: com.android.settings.applications.MiuiClearDefaultsPreference.3
                @Override // java.lang.Runnable
                public void run() {
                    boolean z2 = hasBindAppWidgetPermission;
                    boolean z3 = z2 && z;
                    if (z2) {
                        MiuiClearDefaultsPreference.this.setSummary(R.string.auto_launch_label_generic);
                    } else {
                        MiuiClearDefaultsPreference.this.setSummary(R.string.auto_launch_label);
                    }
                    Context context = MiuiClearDefaultsPreference.this.getContext();
                    CharSequence charSequence = null;
                    int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.installed_app_details_bullet_offset);
                    if (z) {
                        CharSequence text = context.getText(R.string.auto_launch_enable_text);
                        SpannableString spannableString = new SpannableString(text);
                        if (z3) {
                            spannableString.setSpan(new BulletSpan(dimensionPixelSize), 0, text.length(), 0);
                        }
                        charSequence = TextUtils.concat(spannableString, "\n");
                    }
                    if (hasBindAppWidgetPermission) {
                        CharSequence text2 = context.getText(R.string.always_allow_bind_appwidgets_text);
                        SpannableString spannableString2 = new SpannableString(text2);
                        if (z3) {
                            spannableString2.setSpan(new BulletSpan(dimensionPixelSize), 0, text2.length(), 0);
                        }
                        charSequence = charSequence == null ? TextUtils.concat(spannableString2, "\n") : TextUtils.concat(charSequence, "\n", spannableString2, "\n");
                    }
                    MiuiClearDefaultsPreference.this.setSummary(charSequence);
                    MiuiClearDefaultsPreference.this.mHasDefault = true;
                }
            });
        } else {
            view.post(new Runnable() { // from class: com.android.settings.applications.MiuiClearDefaultsPreference.2
                @Override // java.lang.Runnable
                public void run() {
                    MiuiClearDefaultsPreference.this.resetLaunchDefaultsUi();
                }
            });
        }
        return true;
    }
}
