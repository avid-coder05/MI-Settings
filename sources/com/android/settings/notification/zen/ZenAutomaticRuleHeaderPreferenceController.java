package com.android.settings.notification.zen;

import android.app.Activity;
import android.app.AutomaticZenRule;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.service.notification.ZenModeConfig;
import android.util.Slog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.R;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;

/* loaded from: classes2.dex */
public class ZenAutomaticRuleHeaderPreferenceController extends AbstractZenModePreferenceController {
    private final String KEY;
    private EntityHeaderController mController;
    private final PreferenceFragmentCompat mFragment;
    private String mId;
    private AutomaticZenRule mRule;

    public ZenAutomaticRuleHeaderPreferenceController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, Lifecycle lifecycle) {
        super(context, "pref_app_header", lifecycle);
        this.KEY = "pref_app_header";
        this.mFragment = preferenceFragmentCompat;
    }

    private Drawable getIcon() {
        try {
            PackageManager packageManager = this.mContext.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.mRule.getOwner().getPackageName(), 0);
            if (applicationInfo.isSystemApp()) {
                if (ZenModeConfig.isValidScheduleConditionId(this.mRule.getConditionId())) {
                    return this.mContext.getDrawable(R.drawable.ic_timelapse);
                }
                if (ZenModeConfig.isValidEventConditionId(this.mRule.getConditionId())) {
                    return this.mContext.getDrawable(R.drawable.ic_event);
                }
            }
            return applicationInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.w("PrefControllerMixin", "Unable to load icon - PackageManager.NameNotFoundException");
            return null;
        }
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "pref_app_header";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mRule != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onResume(AutomaticZenRule automaticZenRule, String str) {
        this.mRule = automaticZenRule;
        this.mId = str;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        PreferenceFragmentCompat preferenceFragmentCompat;
        if (this.mRule == null || (preferenceFragmentCompat = this.mFragment) == null) {
            return;
        }
        if (this.mController == null) {
            this.mController = EntityHeaderController.newInstance(preferenceFragmentCompat.getActivity(), this.mFragment, ((LayoutPreference) preference).findViewById(R.id.entity_header));
        }
        this.mController.setIcon(getIcon()).setLabel(this.mRule.getName()).done((Activity) this.mFragment.getActivity(), false);
    }
}
