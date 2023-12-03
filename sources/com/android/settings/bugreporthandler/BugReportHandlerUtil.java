package com.android.settings.bugreporthandler;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import com.android.settings.R;
import java.util.List;

/* loaded from: classes.dex */
public class BugReportHandlerUtil {
    private List<ResolveInfo> getBugReportHandlerAppReceivers(Context context, String str, int i) {
        Intent intent = new Intent("com.android.internal.intent.action.BUGREPORT_REQUESTED");
        intent.setPackage(str);
        return context.getPackageManager().queryBroadcastReceiversAsUser(intent, 1048576, i);
    }

    private String getCustomBugReportHandlerApp(Context context) {
        return Settings.Global.getString(context.getContentResolver(), "custom_bugreport_handler_app");
    }

    private int getCustomBugReportHandlerUser(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "custom_bugreport_handler_user", -10000);
    }

    private String getDefaultBugReportHandlerApp(Context context) {
        return context.getResources().getString(17039916);
    }

    private boolean isBugreportAllowlistedApp(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            return ActivityManager.getService().getBugreportWhitelistedPackages().contains(str);
        } catch (RemoteException e) {
            Log.e("BugReportHandlerUtil", "Failed to get bugreportAllowlistedPackages:", e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getValidBugReportHandlerInfos$0(String str) {
        return !"com.android.shell".equals(str);
    }

    private void setBugreportHandlerAppAndUser(Context context, String str, int i) {
        Settings.Global.putString(context.getContentResolver(), "custom_bugreport_handler_app", str);
        Settings.Global.putInt(context.getContentResolver(), "custom_bugreport_handler_user", i);
    }

    public Pair<String, Integer> getCurrentBugReportHandlerAppAndUser(Context context) {
        boolean z;
        String customBugReportHandlerApp = getCustomBugReportHandlerApp(context);
        int customBugReportHandlerUser = getCustomBugReportHandlerUser(context);
        boolean z2 = true;
        int i = 0;
        if (!isBugreportAllowlistedApp(customBugReportHandlerApp)) {
            customBugReportHandlerApp = getDefaultBugReportHandlerApp(context);
            customBugReportHandlerUser = 0;
            z = false;
        } else if (getBugReportHandlerAppReceivers(context, customBugReportHandlerApp, customBugReportHandlerUser).isEmpty()) {
            customBugReportHandlerApp = getDefaultBugReportHandlerApp(context);
            z = true;
            customBugReportHandlerUser = 0;
        } else {
            z = false;
        }
        if (!isBugreportAllowlistedApp(customBugReportHandlerApp) || getBugReportHandlerAppReceivers(context, customBugReportHandlerApp, customBugReportHandlerUser).isEmpty()) {
            customBugReportHandlerApp = "com.android.shell";
        } else {
            i = customBugReportHandlerUser;
            z2 = z;
        }
        if (z2) {
            setBugreportHandlerAppAndUser(context, customBugReportHandlerApp, i);
        }
        return Pair.create(customBugReportHandlerApp, Integer.valueOf(i));
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0076  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.List<android.util.Pair<android.content.pm.ApplicationInfo, java.lang.Integer>> getValidBugReportHandlerInfos(android.content.Context r9) {
        /*
            r8 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            android.app.IActivityManager r1 = android.app.ActivityManager.getService()     // Catch: android.os.RemoteException -> La4
            java.util.List r1 = r1.getBugreportWhitelistedPackages()     // Catch: android.os.RemoteException -> La4
            java.lang.String r2 = "com.android.shell"
            boolean r3 = r1.contains(r2)
            r4 = 4194304(0x400000, float:5.877472E-39)
            if (r3 == 0) goto L35
            r3 = 0
            java.util.List r5 = r8.getBugReportHandlerAppReceivers(r9, r2, r3)
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L35
            android.content.pm.PackageManager r5 = r9.getPackageManager()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L35
            android.content.pm.ApplicationInfo r2 = r5.getApplicationInfo(r2, r4)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L35
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L35
            android.util.Pair r2 = android.util.Pair.create(r2, r3)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L35
            r0.add(r2)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L35
        L35:
            java.lang.Class<android.os.UserManager> r2 = android.os.UserManager.class
            java.lang.Object r2 = r9.getSystemService(r2)
            android.os.UserManager r2 = (android.os.UserManager) r2
            int r3 = android.os.UserHandle.getCallingUserId()
            java.util.List r2 = r2.getProfiles(r3)
            java.util.stream.Stream r1 = r1.stream()
            com.android.settings.bugreporthandler.BugReportHandlerUtil$$ExternalSyntheticLambda0 r3 = new java.util.function.Predicate() { // from class: com.android.settings.bugreporthandler.BugReportHandlerUtil$$ExternalSyntheticLambda0
                static {
                    /*
                        com.android.settings.bugreporthandler.BugReportHandlerUtil$$ExternalSyntheticLambda0 r0 = new com.android.settings.bugreporthandler.BugReportHandlerUtil$$ExternalSyntheticLambda0
                        r0.<init>()
                        
                        // error: 0x0005: SPUT (r0 I:com.android.settings.bugreporthandler.BugReportHandlerUtil$$ExternalSyntheticLambda0) com.android.settings.bugreporthandler.BugReportHandlerUtil$$ExternalSyntheticLambda0.INSTANCE com.android.settings.bugreporthandler.BugReportHandlerUtil$$ExternalSyntheticLambda0
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bugreporthandler.BugReportHandlerUtil$$ExternalSyntheticLambda0.<clinit>():void");
                }

                {
                    /*
                        r0 = this;
                        r0.<init>()
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bugreporthandler.BugReportHandlerUtil$$ExternalSyntheticLambda0.<init>():void");
                }

                @Override // java.util.function.Predicate
                public final boolean test(java.lang.Object r1) {
                    /*
                        r0 = this;
                        java.lang.String r1 = (java.lang.String) r1
                        boolean r0 = com.android.settings.bugreporthandler.BugReportHandlerUtil.$r8$lambda$FkaXrFWTv6NO5n3_DebLWNwfvrs(r1)
                        return r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bugreporthandler.BugReportHandlerUtil$$ExternalSyntheticLambda0.test(java.lang.Object):boolean");
                }
            }
            java.util.stream.Stream r1 = r1.filter(r3)
            java.util.stream.Collector r3 = java.util.stream.Collectors.toList()
            java.lang.Object r1 = r1.collect(r3)
            java.util.List r1 = (java.util.List) r1
            java.util.Collections.sort(r1)
            java.util.Iterator r1 = r1.iterator()
        L60:
            boolean r3 = r1.hasNext()
            if (r3 == 0) goto La3
            java.lang.Object r3 = r1.next()
            java.lang.String r3 = (java.lang.String) r3
            java.util.Iterator r5 = r2.iterator()
        L70:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L60
            java.lang.Object r6 = r5.next()
            android.content.pm.UserInfo r6 = (android.content.pm.UserInfo) r6
            android.os.UserHandle r6 = r6.getUserHandle()
            int r6 = r6.getIdentifier()
            java.util.List r7 = r8.getBugReportHandlerAppReceivers(r9, r3, r6)
            boolean r7 = r7.isEmpty()
            if (r7 == 0) goto L8f
            goto L70
        L8f:
            android.content.pm.PackageManager r7 = r9.getPackageManager()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L70
            android.content.pm.ApplicationInfo r7 = r7.getApplicationInfo(r3, r4)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L70
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L70
            android.util.Pair r6 = android.util.Pair.create(r7, r6)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L70
            r0.add(r6)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L70
            goto L70
        La3:
            return r0
        La4:
            r8 = move-exception
            java.lang.String r9 = "BugReportHandlerUtil"
            java.lang.String r1 = "Failed to get bugreportAllowlistedPackages:"
            android.util.Log.e(r9, r1, r8)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bugreporthandler.BugReportHandlerUtil.getValidBugReportHandlerInfos(android.content.Context):java.util.List");
    }

    public boolean setCurrentBugReportHandlerAppAndUser(Context context, String str, int i) {
        if (isBugreportAllowlistedApp(str) && !getBugReportHandlerAppReceivers(context, str, i).isEmpty()) {
            setBugreportHandlerAppAndUser(context, str, i);
            return true;
        }
        return false;
    }

    public void showInvalidChoiceToast(Context context) {
        Toast.makeText(context, R.string.select_invalid_bug_report_handler_toast_text, 0).show();
    }
}
