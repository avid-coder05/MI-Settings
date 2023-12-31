package com.android.settings.users;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.R$string;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.content.ExtraIntent;

/* loaded from: classes2.dex */
public class UserDetailsSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private static final String TAG = UserDetailsSettings.class.getSimpleName();
    Preference mAppAndContentAccessPref;
    private Bundle mDefaultGuestRestrictions;
    private boolean mGuestUserAutoCreated;
    private SwitchPreference mPhonePref;
    Preference mRemoveUserPref;
    RestrictedPreference mSwitchUserPref;
    private UserCapabilities mUserCaps;
    UserInfo mUserInfo;
    private UserManager mUserManager;
    private final AtomicBoolean mGuestCreationScheduled = new AtomicBoolean();
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private void enableCallsAndSms(boolean z) {
        this.mPhonePref.setChecked(z);
        if (!this.mUserInfo.isGuest()) {
            UserHandle of = UserHandle.of(this.mUserInfo.id);
            this.mUserManager.setUserRestriction("no_outgoing_calls", !z, of);
            this.mUserManager.setUserRestriction("no_sms", !z, of);
            return;
        }
        this.mDefaultGuestRestrictions.putBoolean("no_outgoing_calls", !z);
        this.mDefaultGuestRestrictions.putBoolean("no_sms", true);
        this.mUserManager.setDefaultGuestRestrictions(this.mDefaultGuestRestrictions);
        for (UserInfo userInfo : this.mUserManager.getAliveUsers()) {
            if (userInfo.isGuest()) {
                UserHandle of2 = UserHandle.of(userInfo.id);
                for (String str : this.mDefaultGuestRestrictions.keySet()) {
                    this.mUserManager.setUserRestriction(str, this.mDefaultGuestRestrictions.getBoolean(str), of2);
                }
            }
        }
    }

    private boolean isSecondaryUser(UserInfo userInfo) {
        return "android.os.usertype.full.SECONDARY".equals(userInfo.userType);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        removeUser();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        enableCallsAndSms(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$2(DialogInterface dialogInterface, int i) {
        enableCallsAndSms(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$3(DialogInterface dialogInterface, int i) {
        if (canSwitchUserNow()) {
            switchUser();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$4(DialogInterface dialogInterface, int i) {
        resetGuest();
    }

    private void openAppAndContentAccessScreen(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt(ExtraIntent.EXTRA_XIAOMI_ACCOUNT_USER_ID, this.mUserInfo.id);
        bundle.putBoolean("new_user", z);
        new SubSettingLauncher(getContext()).setDestination(AppRestrictionsFragment.class.getName()).setArguments(bundle).setTitleRes(R.string.user_restrictions_title).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    private void removeUser() {
        this.mUserManager.removeUser(this.mUserInfo.id);
        finishFragment();
    }

    private void resetGuest() {
        if (this.mUserInfo.isGuest()) {
            this.mMetricsFeatureProvider.action(getActivity(), 1763, new Pair[0]);
            this.mUserManager.removeUser(this.mUserInfo.id);
            setResult(100);
            finishFragment();
        }
    }

    private boolean shouldShowSetupPromptDialog() {
        return isSecondaryUser(this.mUserInfo) && !this.mUserInfo.isInitialized();
    }

    boolean canDeleteUser() {
        FragmentActivity activity;
        if (this.mUserManager.isAdminUser() && (activity = getActivity()) != null) {
            RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(activity, "no_remove_user", UserHandle.myUserId());
            if (checkIfRestrictionEnforced != null) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(activity, checkIfRestrictionEnforced);
                return false;
            }
            return true;
        }
        return false;
    }

    boolean canSwitchUserNow() {
        return this.mUserManager.getUserSwitchability() == 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 98;
    }

    void initialize(Context context, Bundle bundle) {
        int i = bundle != null ? bundle.getInt(ExtraIntent.EXTRA_XIAOMI_ACCOUNT_USER_ID, -10000) : -10000;
        if (i == -10000) {
            throw new IllegalStateException("Arguments to this fragment must contain the user id");
        }
        boolean z = bundle.getBoolean("new_user", false);
        this.mUserInfo = this.mUserManager.getUserInfo(i);
        this.mSwitchUserPref = (RestrictedPreference) findPreference("switch_user");
        this.mPhonePref = (SwitchPreference) findPreference("enable_calling");
        this.mRemoveUserPref = findPreference("remove_user");
        this.mAppAndContentAccessPref = findPreference("app_and_content_access");
        this.mSwitchUserPref.setTitle(context.getString(R$string.user_switch_to_user, UserSettings.getUserName(context, this.mUserInfo)));
        if (this.mUserCaps.mDisallowSwitchUser) {
            this.mSwitchUserPref.setDisabledByAdmin(RestrictedLockUtilsInternal.getDeviceOwner(context));
        } else {
            this.mSwitchUserPref.setDisabledByAdmin(null);
            this.mSwitchUserPref.setSelectable(true);
            this.mSwitchUserPref.setOnPreferenceClickListener(this);
        }
        if (!this.mUserManager.isAdminUser()) {
            removePreference("enable_calling");
            removePreference("remove_user");
            removePreference("app_and_content_access");
            return;
        }
        if (!Utils.isVoiceCapable(context)) {
            removePreference("enable_calling");
        }
        if (this.mUserInfo.isRestricted()) {
            removePreference("enable_calling");
            if (z) {
                openAppAndContentAccessScreen(true);
            }
        } else {
            removePreference("app_and_content_access");
        }
        if (this.mUserInfo.isGuest()) {
            this.mPhonePref.setTitle(R.string.user_enable_calling);
            this.mDefaultGuestRestrictions = this.mUserManager.getDefaultGuestRestrictions();
            this.mPhonePref.setChecked(!r12.getBoolean("no_outgoing_calls"));
            this.mRemoveUserPref.setTitle(this.mGuestUserAutoCreated ? R$string.guest_reset_guest : R.string.user_exit_guest_title);
            if (this.mGuestUserAutoCreated) {
                this.mRemoveUserPref.setEnabled((this.mUserInfo.flags & 16) != 0);
            }
        } else {
            this.mPhonePref.setChecked(!this.mUserManager.hasUserRestriction("no_outgoing_calls", new UserHandle(i)));
            this.mRemoveUserPref.setTitle(R.string.user_remove_user);
        }
        if (RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_remove_user", UserHandle.myUserId())) {
            removePreference("remove_user");
        }
        this.mRemoveUserPref.setOnPreferenceClickListener(this);
        this.mPhonePref.setOnPreferenceChangeListener(this);
        this.mAppAndContentAccessPref.setOnPreferenceClickListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mUserManager = (UserManager) activity.getSystemService("user");
        this.mUserCaps = UserCapabilities.create(activity);
        addPreferencesFromResource(R.xml.user_details_settings);
        this.mGuestUserAutoCreated = getPrefContext().getResources().getBoolean(17891569);
        initialize(activity, getArguments());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (getActivity() == null) {
            return null;
        }
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i == 5) {
                            return UserDialogs.createResetGuestDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.android.settings.users.UserDetailsSettings$$ExternalSyntheticLambda0
                                @Override // android.content.DialogInterface.OnClickListener
                                public final void onClick(DialogInterface dialogInterface, int i2) {
                                    UserDetailsSettings.this.lambda$onCreateDialog$4(dialogInterface, i2);
                                }
                            });
                        }
                        throw new IllegalArgumentException("Unsupported dialogId " + i);
                    }
                    return UserDialogs.createSetupUserDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.android.settings.users.UserDetailsSettings$$ExternalSyntheticLambda1
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i2) {
                            UserDetailsSettings.this.lambda$onCreateDialog$3(dialogInterface, i2);
                        }
                    });
                }
                return UserDialogs.createEnablePhoneCallsAndSmsDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.android.settings.users.UserDetailsSettings$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        UserDetailsSettings.this.lambda$onCreateDialog$2(dialogInterface, i2);
                    }
                });
            }
            return UserDialogs.createEnablePhoneCallsDialog(getActivity(), new DialogInterface.OnClickListener() { // from class: com.android.settings.users.UserDetailsSettings$$ExternalSyntheticLambda3
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    UserDetailsSettings.this.lambda$onCreateDialog$1(dialogInterface, i2);
                }
            });
        }
        return UserDialogs.createRemoveDialog(getActivity(), this.mUserInfo.id, new DialogInterface.OnClickListener() { // from class: com.android.settings.users.UserDetailsSettings$$ExternalSyntheticLambda4
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                UserDetailsSettings.this.lambda$onCreateDialog$0(dialogInterface, i2);
            }
        });
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (Boolean.TRUE.equals(obj)) {
            showDialog(this.mUserInfo.isGuest() ? 2 : 3);
            return false;
        }
        enableCallsAndSms(false);
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mRemoveUserPref) {
            if (canDeleteUser()) {
                if (this.mUserInfo.isGuest()) {
                    showDialog(5);
                } else {
                    showDialog(1);
                }
                return true;
            }
        } else if (preference == this.mSwitchUserPref) {
            if (canSwitchUserNow()) {
                if (shouldShowSetupPromptDialog()) {
                    showDialog(4);
                } else {
                    switchUser();
                }
                return true;
            }
        } else if (preference == this.mAppAndContentAccessPref) {
            openAppAndContentAccessScreen(false);
            return true;
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mSwitchUserPref.setEnabled(canSwitchUserNow());
        if (this.mGuestUserAutoCreated) {
            this.mRemoveUserPref.setEnabled((this.mUserInfo.flags & 16) != 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsPreferenceFragment
    public void showDialog(int i) {
        super.showDialog(i);
    }

    void switchUser() {
        try {
            try {
                if (this.mUserInfo.isGuest()) {
                    this.mMetricsFeatureProvider.action(getActivity(), 1765, new Pair[0]);
                }
                ActivityManager.getService().switchUser(this.mUserInfo.id);
            } catch (RemoteException unused) {
                Log.e(TAG, "Error while switching to other user.");
            }
        } finally {
            finishFragment();
        }
    }
}
