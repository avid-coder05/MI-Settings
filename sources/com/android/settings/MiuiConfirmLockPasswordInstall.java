package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.RecoverySystem;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class MiuiConfirmLockPasswordInstall extends MiuiConfirmCommonPassword {
    private static final String TAG = "MiuiConfirmLockPasswordInstall";

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ApplyRomFile extends File {
        public ApplyRomFile(String str) {
            super(str);
        }

        @Override // java.io.File
        public String getCanonicalPath() throws IOException {
            String absolutePath = super.getAbsolutePath();
            if (absolutePath.indexOf("/mnt") > -1) {
                absolutePath = absolutePath.substring(4);
            }
            return absolutePath.startsWith("/storage/emulated/0/") ? absolutePath.replace("/storage/emulated/0/", "/data/media/0/") : absolutePath.replace("/storage/sdcard0", "sdcard");
        }
    }

    private void installPackage(String str, String str2, String str3, Boolean bool) {
        try {
            Method declaredMethod = RecoverySystem.class.getDeclaredMethod("installPackage", Context.class, File.class, String.class, Boolean.class, String.class);
            if (declaredMethod != null) {
                declaredMethod.invoke(null, this, new ApplyRomFile(str), str3, bool, str2);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (NullPointerException e4) {
            e4.printStackTrace();
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
        }
    }

    @Override // com.android.settings.SettingsActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            Intent intent2 = getIntent();
            String stringExtra = intent2.getStringExtra("update_file_path");
            String stringExtra2 = intent2.getStringExtra("secret");
            boolean booleanExtra = intent2.getBooleanExtra("erase", false);
            String defaultAESKeyPlaintext = AESUtil.getDefaultAESKeyPlaintext();
            String stringExtra3 = intent.getStringExtra("password");
            try {
                stringExtra3 = AESUtil.encrypt(stringExtra3, defaultAESKeyPlaintext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(stringExtra3)) {
                Log.e(TAG, "empty password");
            } else {
                installPackage(stringExtra, stringExtra3, stringExtra2, Boolean.valueOf(booleanExtra));
            }
        } else {
            Log.e(TAG, "failed to get password");
        }
        finish();
    }
}
