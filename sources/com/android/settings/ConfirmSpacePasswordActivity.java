package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.appcompat.app.ActionBar;
import com.android.settings.ConfirmLockPassword;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class ConfirmSpacePasswordActivity extends ConfirmLockPassword.InternalActivity {
    private Context mContext;

    /* loaded from: classes.dex */
    public static class ConfirmSpaceLockPasswordFragment extends ConfirmLockPassword.ConfirmLockPasswordFragment {
        @Override // com.android.settings.ConfirmLockPassword.ConfirmLockPasswordFragment, com.android.settings.BaseConfirmLockFragment
        public View createView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            View createView = super.createView(layoutInflater, viewGroup, bundle);
            ConfirmSpacePasswordActivity.initActionBar(getAppCompatActivity(), this.mUserIdToConfirmPassword);
            return createView;
        }
    }

    public static String getExtraFragmentName() {
        return ConfirmSpaceLockPasswordFragment.class.getName();
    }

    public static void initActionBar(final AppCompatActivity appCompatActivity, final int i) {
        ActionBar appCompatActionBar;
        if (UserHandle.myUserId() == 0 && isAllowDeleteSpace(appCompatActivity, i) && (appCompatActionBar = appCompatActivity.getAppCompatActionBar()) != null) {
            ImageView imageView = new ImageView(appCompatActivity);
            imageView.setLayoutParams(new ActionBar.LayoutParams(-2, -2));
            imageView.setImageResource(R.drawable.miuix_appcompat_action_button_delete_light);
            imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.ConfirmSpacePasswordActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ConfirmSpacePasswordActivity.showDialogByRemoveSpace(AppCompatActivity.this, i);
                }
            });
            appCompatActionBar.setDisplayOptions(16, 16);
            appCompatActionBar.setCustomView(imageView, new ActionBar.LayoutParams(-2, -2, 8388629));
        }
    }

    public static boolean isAllowDeleteSpace(Context context, int i) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "airspace_policy_allow_delete_space", 0, i) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showDialogByRemoveSpace(final AppCompatActivity appCompatActivity, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(appCompatActivity.getResources().getString(R.string.work_space_delete));
        builder.setMessage(appCompatActivity.getResources().getString(R.string.work_space_delete_description));
        builder.setNegativeButton(appCompatActivity.getResources().getString(R.string.new_password_to_new_fingerprint_dialog_negative_msg), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(appCompatActivity.getResources().getString(R.string.work_space_delete_confirm), new DialogInterface.OnClickListener() { // from class: com.android.settings.ConfirmSpacePasswordActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                Intent intent = new Intent("com.miui.securityspace.toRemoveAirSpace");
                intent.putExtra("android.intent.extra.user_handle", i);
                appCompatActivity.sendBroadcast(intent, "android.permission.MANAGE_USERS");
                appCompatActivity.finish();
            }
        });
        builder.show();
    }

    @Override // com.android.settings.ConfirmLockPassword, com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ConfirmSpaceLockPasswordFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.ConfirmLockPassword, com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ConfirmSpaceLockPasswordFragment.class.getName().equals(str);
    }

    @Override // com.android.settings.BaseConfirmLockActivity, com.android.settings.password.ConfirmDeviceCredentialBaseActivity, com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = this;
    }
}
