package com.android.settings.utils;

import java.io.File;

/* loaded from: classes2.dex */
public class MiuiFileUtils {
    public static boolean isDirEmpty(String str) {
        String[] list;
        try {
            File file = new File(str);
            if (file.exists() && file.isDirectory() && (list = file.list()) != null) {
                return list.length == 0;
            }
            return true;
        } catch (Exception unused) {
            return true;
        }
    }
}
