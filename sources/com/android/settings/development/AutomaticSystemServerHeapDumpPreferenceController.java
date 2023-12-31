package com.android.settings.development;

import android.content.Context;
import android.os.Build;
import android.os.UserManager;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class AutomaticSystemServerHeapDumpPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private final boolean mIsConfigEnabled;
    private final UserManager mUserManager;

    public AutomaticSystemServerHeapDumpPreferenceController(Context context) {
        super(context);
        this.mIsConfigEnabled = context.getResources().getBoolean(17891488);
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "automatic_system_server_heap_dumps";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return Build.IS_DEBUGGABLE && this.mIsConfigEnabled && !this.mUserManager.hasUserRestriction("no_debugging_features");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.Secure.putInt(this.mContext.getContentResolver(), "enable_automatic_system_server_heap_dumps", 0);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "enable_automatic_system_server_heap_dumps", ((Boolean) obj).booleanValue() ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(Settings.Secure.getInt(this.mContext.getContentResolver(), "enable_automatic_system_server_heap_dumps", 1) != 0);
    }
}
