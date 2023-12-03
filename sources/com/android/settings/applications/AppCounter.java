package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.MiuiWindowManager$LayoutParams;
import java.util.Iterator;

/* loaded from: classes.dex */
public abstract class AppCounter extends AsyncTask<Void, Void, Integer> {
    protected final PackageManager mPm;
    protected final UserManager mUm;

    public AppCounter(Context context, PackageManager packageManager) {
        this.mPm = packageManager;
        this.mUm = (UserManager) context.getSystemService("user");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Integer doInBackground(Void... voidArr) {
        int i = 0;
        for (UserInfo userInfo : this.mUm.getProfiles(UserHandle.myUserId())) {
            Iterator it = this.mPm.getInstalledApplicationsAsUser(33280 | (userInfo.isAdmin() ? MiuiWindowManager$LayoutParams.EXTRA_FLAG_ACQUIRES_SLEEP_TOKEN : 0), userInfo.id).iterator();
            while (it.hasNext()) {
                if (includeInCount((ApplicationInfo) it.next())) {
                    i++;
                }
            }
        }
        return Integer.valueOf(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void executeInForeground() {
        onPostExecute(doInBackground(new Void[0]));
    }

    protected abstract boolean includeInCount(ApplicationInfo applicationInfo);

    protected abstract void onCountComplete(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Integer num) {
        onCountComplete(num.intValue());
    }
}
