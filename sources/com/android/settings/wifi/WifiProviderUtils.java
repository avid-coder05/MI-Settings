package com.android.settings.wifi;

import android.content.Context;
import android.os.storage.StorageManager;

/* loaded from: classes2.dex */
public class WifiProviderUtils {
    public static Context getDeviceEncryptedContext(Context context) {
        return context.isDeviceProtectedStorage() ? context : context.createDeviceProtectedStorageContext();
    }

    public static boolean isFileBasedEncryptionEnabled() {
        return StorageManager.isFileEncryptedNativeOrEmulated();
    }
}
