package com.android.settings;

import android.content.ContentResolver;

/* loaded from: classes.dex */
public class MiuiOptionUtils$Account {
    public static int touchSyncState(int i) {
        boolean masterSyncAutomatically = ContentResolver.getMasterSyncAutomatically();
        if (i == -1 || i == masterSyncAutomatically) {
            return masterSyncAutomatically ? 1 : 0;
        }
        ContentResolver.setMasterSyncAutomatically(i > 0);
        return i;
    }
}
