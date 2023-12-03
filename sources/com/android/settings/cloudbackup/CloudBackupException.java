package com.android.settings.cloudbackup;

import com.android.settingslib.util.MiStatInterfaceUtils;

/* loaded from: classes.dex */
public class CloudBackupException extends Exception {
    public CloudBackupException(String str) {
        super(str);
    }

    public static void trackException() {
        trackException("CloudBackupException");
    }

    public static void trackException(String str) {
        MiStatInterfaceUtils.trackException(new CloudBackupException(str));
    }
}
