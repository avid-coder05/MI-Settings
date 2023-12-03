package com.android.settings.cloud.push;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.android.settings.R;
import java.util.Iterator;
import java.util.List;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes.dex */
public class CompatChecker {
    public static CompatChecker INST;
    private ActivityManager mActivityManager;
    private Context mContext;
    private ExistCompatibility mItem = null;
    private PackageManager mPackageManager;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        boolean finished;

        private PackageDeleteObserver() {
        }

        public void packageDeleted(String str, int i) {
            synchronized (this) {
                if (CompatChecker.this.mItem != null) {
                    String title = CompatChecker.this.mItem.getTitle();
                    String ticker = CompatChecker.this.mItem.getTicker();
                    String message = CompatChecker.this.mItem.getMessage();
                    if (title != null && !title.equals("") && ticker != null && !ticker.equals("") && message != null && !message.equals("")) {
                        NotificationManager notificationManager = (NotificationManager) CompatChecker.this.mContext.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
                        Notification notification = new Notification(R.drawable.ic_launcher_settings, ticker, System.currentTimeMillis());
                        int i2 = notification.defaults | 1;
                        notification.defaults = i2;
                        notification.defaults = i2 | 2;
                        notification.flags |= 16;
                        notification.setLatestEventInfo(CompatChecker.this.mContext, title, message, PendingIntent.getActivity(CompatChecker.this.mContext, 0, new Intent(), 0));
                        notificationManager.notify(0, notification);
                    }
                }
                this.finished = true;
                notifyAll();
            }
        }
    }

    private CompatChecker(Context context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mActivityManager = (ActivityManager) context.getSystemService("activity");
    }

    public static CompatChecker getInstance(Context context) {
        if (INST == null) {
            INST = new CompatChecker(context.getApplicationContext());
        }
        return INST;
    }

    public void checkExistCompat(ExistCompatibility existCompatibility) {
        if (existCompatibility == null) {
            return;
        }
        String packageName = existCompatibility.getPackageName();
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return;
            }
            if (existCompatibility.isPrecise()) {
                if (existCompatibility.getVersions().contains(Integer.valueOf(packageInfo.versionCode))) {
                    this.mItem = existCompatibility;
                    deletePackage(packageName);
                    return;
                }
                return;
            }
            Iterator<Integer> it = existCompatibility.getVersions().iterator();
            while (it.hasNext()) {
                if (packageInfo.versionCode >= it.next().intValue()) {
                    this.mItem = existCompatibility;
                    deletePackage(packageName);
                    return;
                }
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }

    public void checkExistCompats(List<ExistCompatibility> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Iterator<ExistCompatibility> it = list.iterator();
        while (it.hasNext()) {
            checkExistCompat(it.next());
        }
    }

    public void checkRunningCompat(RunningCompatibility runningCompatibility) {
        if (runningCompatibility == null) {
            return;
        }
        String packageName = runningCompatibility.getPackageName();
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return;
            }
            if (runningCompatibility.isPrecise()) {
                if (runningCompatibility.getVersions().contains(Integer.valueOf(packageInfo.versionCode))) {
                    this.mActivityManager.forceStopPackage(packageName);
                    return;
                }
                return;
            }
            Iterator<Integer> it = runningCompatibility.getVersions().iterator();
            while (it.hasNext()) {
                if (packageInfo.versionCode >= it.next().intValue()) {
                    this.mActivityManager.forceStopPackage(packageName);
                    return;
                }
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }

    public void checkRunningCompats(List<RunningCompatibility> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Iterator<RunningCompatibility> it = list.iterator();
        while (it.hasNext()) {
            checkRunningCompat(it.next());
        }
    }

    public void deletePackage(String str) {
        IPackageDeleteObserver packageDeleteObserver = new PackageDeleteObserver();
        this.mPackageManager.deletePackage(str, packageDeleteObserver, 0);
        synchronized (packageDeleteObserver) {
            while (!packageDeleteObserver.finished) {
                try {
                    packageDeleteObserver.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
