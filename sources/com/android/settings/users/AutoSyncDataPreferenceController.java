package com.android.settings.users;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class AutoSyncDataPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final Fragment mParentFragment;
    protected UserHandle mUserHandle;
    protected final UserManager mUserManager;

    /* loaded from: classes2.dex */
    public static class ConfirmAutoSyncChangeFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        boolean mEnabling;
        CheckBoxPreference mPreference;
        UserHandle mUserHandle;

        public static void show(Fragment fragment, boolean z, UserHandle userHandle, CheckBoxPreference checkBoxPreference) {
            if (fragment.isAdded()) {
                ConfirmAutoSyncChangeFragment confirmAutoSyncChangeFragment = new ConfirmAutoSyncChangeFragment();
                confirmAutoSyncChangeFragment.mEnabling = z;
                confirmAutoSyncChangeFragment.mUserHandle = userHandle;
                confirmAutoSyncChangeFragment.setTargetFragment(fragment, 0);
                confirmAutoSyncChangeFragment.mPreference = checkBoxPreference;
                confirmAutoSyncChangeFragment.show(fragment.getFragmentManager(), "confirmAutoSyncChange");
            }
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 535;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                ContentResolver.setMasterSyncAutomaticallyAsUser(this.mEnabling, this.mUserHandle.getIdentifier());
                CheckBoxPreference checkBoxPreference = this.mPreference;
                if (checkBoxPreference != null) {
                    checkBoxPreference.setChecked(this.mEnabling);
                }
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            FragmentActivity activity = getActivity();
            if (bundle != null) {
                this.mEnabling = bundle.getBoolean("enabling");
                this.mUserHandle = (UserHandle) bundle.getParcelable("userHandle");
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            if (this.mEnabling) {
                builder.setTitle(R.string.data_usage_auto_sync_on_dialog_title);
                builder.setMessage(R.string.data_usage_auto_sync_on_dialog);
            } else {
                builder.setTitle(R.string.data_usage_auto_sync_off_dialog_title);
                builder.setMessage(R.string.data_usage_auto_sync_off_dialog);
            }
            builder.setPositiveButton(17039370, this);
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            return builder.create();
        }

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putBoolean("enabling", this.mEnabling);
            bundle.putParcelable("userHandle", this.mUserHandle);
        }
    }

    public AutoSyncDataPreferenceController(Context context, Fragment fragment) {
        super(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mParentFragment = fragment;
        this.mUserHandle = Process.myUserHandle();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "auto_sync_account_data";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !this.mUserManager.isManagedProfile() && (this.mUserManager.isRestrictedProfile() || this.mUserManager.getProfiles(UserHandle.myUserId()).size() == 1);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (getPreferenceKey().equals(preference.getKey())) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            boolean booleanValue = ((Boolean) obj).booleanValue();
            checkBoxPreference.setChecked(!booleanValue);
            if (ActivityManager.isUserAMonkey()) {
                Log.d("AutoSyncDataController", "ignoring monkey's attempt to flip sync state");
                return true;
            }
            ConfirmAutoSyncChangeFragment.show(this.mParentFragment, booleanValue, this.mUserHandle, checkBoxPreference);
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((CheckBoxPreference) preference).setChecked(ContentResolver.getMasterSyncAutomaticallyAsUser(this.mUserHandle.getIdentifier()));
    }
}
