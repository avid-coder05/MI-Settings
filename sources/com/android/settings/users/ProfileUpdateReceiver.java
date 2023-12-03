package com.android.settings.users;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.Utils;
import com.android.settingslib.R$string;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes2.dex */
public class ProfileUpdateReceiver extends BroadcastReceiver {
    /* JADX INFO: Access modifiers changed from: private */
    public static void copyProfileName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(YellowPageContract.Profile.DIRECTORY, 0);
        if (sharedPreferences.contains("name_copied_once")) {
            return;
        }
        int myUserId = UserHandle.myUserId();
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        String meProfileName = Utils.getMeProfileName(context, false);
        if (meProfileName == null || meProfileName.length() <= 0 || isCurrentNameInteresting(context, userManager)) {
            return;
        }
        userManager.setUserName(myUserId, meProfileName);
        sharedPreferences.edit().putBoolean("name_copied_once", true).commit();
    }

    private static boolean isCurrentNameInteresting(Context context, UserManager userManager) {
        if (userManager.isUserNameSet()) {
            String userName = userManager.getUserName();
            return (userName == null || userName.equals((userManager.isRestrictedProfile() || userManager.isProfile()) ? context.getString(R$string.user_new_profile_name) : context.getString(R$string.user_new_user_name))) ? false : true;
        }
        return false;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        new Thread() { // from class: com.android.settings.users.ProfileUpdateReceiver.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                UserSettings.copyMeProfilePhoto(context, null);
                ProfileUpdateReceiver.copyProfileName(context);
            }
        }.start();
    }
}
