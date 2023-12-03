package com.xiaomi.settingsdk.backup.data;

import android.content.SharedPreferences;

/* loaded from: classes2.dex */
public class PrefsBackupHelper {

    /* loaded from: classes2.dex */
    public static class PrefEntry {
        PrefEntry() {
            throw new RuntimeException("Stub!");
        }

        public static PrefEntry createBoolEntry(String str, String str2) {
            throw new RuntimeException("Stub!");
        }

        public static PrefEntry createBoolEntry(String str, String str2, boolean z) {
            throw new RuntimeException("Stub!");
        }

        public static PrefEntry createIntEntry(String str, String str2) {
            throw new RuntimeException("Stub!");
        }

        public static PrefEntry createIntEntry(String str, String str2, int i) {
            throw new RuntimeException("Stub!");
        }

        public static PrefEntry createLongEntry(String str, String str2) {
            throw new RuntimeException("Stub!");
        }

        public static PrefEntry createLongEntry(String str, String str2, long j) {
            throw new RuntimeException("Stub!");
        }

        public static PrefEntry createStringEntry(String str, String str2) {
            throw new RuntimeException("Stub!");
        }

        public static PrefEntry createStringEntry(String str, String str2, String str3) {
            throw new RuntimeException("Stub!");
        }

        public String getCloudKey() {
            throw new RuntimeException("Stub!");
        }

        public Object getDefaultValue() {
            throw new RuntimeException("Stub!");
        }

        public String getLocalKey() {
            throw new RuntimeException("Stub!");
        }

        public Class<?> getValueClass() {
            throw new RuntimeException("Stub!");
        }
    }

    PrefsBackupHelper() {
        throw new RuntimeException("Stub!");
    }

    public static void backup(SharedPreferences sharedPreferences, DataPackage dataPackage, PrefEntry[] prefEntryArr) {
        throw new RuntimeException("Stub!");
    }

    public static void restore(SharedPreferences sharedPreferences, DataPackage dataPackage, PrefEntry[] prefEntryArr) {
        throw new RuntimeException("Stub!");
    }
}
