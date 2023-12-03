package com.android.settings.cloud.push;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class CloudManager {
    public static int clearInstallCompatData(Context context) {
        return context.getContentResolver().delete(CloudEntity$InstallCompatibility.CONTENT_URI, null, null);
    }

    public static int clearRunningCompatData(Context context) {
        return context.getContentResolver().delete(CloudEntity$RunningCompatibility.CONTENT_URI, null, null);
    }

    public static void insertInstallCompatData(Context context, List<InstallCompatibility> list) {
        ContentResolver contentResolver = context.getContentResolver();
        for (InstallCompatibility installCompatibility : list) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("c_package_name", installCompatibility.getPackageName());
            contentValues.put("c_message", installCompatibility.getMessage());
            if (installCompatibility.isPrecise()) {
                contentValues.put("c_precise", (Integer) 1);
            } else {
                contentValues.put("c_precise", (Integer) (-1));
            }
            Set<Integer> versions = installCompatibility.getVersions();
            if (!versions.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                Iterator<Integer> it = versions.iterator();
                while (it.hasNext()) {
                    sb.append(String.valueOf(it.next()));
                    sb.append("-");
                }
                sb.deleteCharAt(sb.length() - 1);
                contentValues.put("c_versions", sb.toString());
            }
            contentResolver.insert(CloudEntity$InstallCompatibility.CONTENT_URI, contentValues);
        }
    }

    public static void insertRunningCompatData(Context context, List<RunningCompatibility> list) {
        ContentResolver contentResolver = context.getContentResolver();
        for (RunningCompatibility runningCompatibility : list) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("c_package_name", runningCompatibility.getPackageName());
            contentValues.put("c_message", runningCompatibility.getMessage());
            if (runningCompatibility.isPrecise()) {
                contentValues.put("c_precise", (Integer) 1);
            } else {
                contentValues.put("c_precise", (Integer) (-1));
            }
            Set<Integer> versions = runningCompatibility.getVersions();
            if (!versions.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                Iterator<Integer> it = versions.iterator();
                while (it.hasNext()) {
                    sb.append(String.valueOf(it.next()));
                    sb.append("-");
                }
                sb.deleteCharAt(sb.length() - 1);
                contentValues.put("c_versions", sb.toString());
            }
            contentResolver.insert(CloudEntity$RunningCompatibility.CONTENT_URI, contentValues);
        }
    }
}
