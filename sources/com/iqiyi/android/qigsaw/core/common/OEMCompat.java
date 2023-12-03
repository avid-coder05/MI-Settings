package com.iqiyi.android.qigsaw.core.common;

import android.os.Build;
import java.io.File;
import java.io.IOException;

/* loaded from: classes2.dex */
public class OEMCompat {
    private static final String TAG = "Split.OEMCompat";

    public static boolean checkOatFile(File file) {
        try {
            if (SplitElfFile.getFileTypeByMagic(file) == 1) {
                try {
                    try {
                        FileUtil.closeQuietly(new SplitElfFile(file));
                        return true;
                    } catch (Throwable unused) {
                        SplitLog.e(TAG, "final parallel dex optimizer file %s is not elf format, return false", file.getName());
                    }
                } finally {
                    FileUtil.closeQuietly(null);
                }
            }
            return false;
        } catch (IOException unused2) {
            return true;
        }
    }

    public static File getOatFilePath(File file, File file2) {
        String name = file.getName();
        if (!name.endsWith(SplitConstants.DOT_DEX)) {
            int lastIndexOf = name.lastIndexOf(".");
            if (lastIndexOf < 0) {
                name = name + SplitConstants.DOT_DEX;
            } else {
                name = name.substring(0, lastIndexOf) + SplitConstants.DOT_DEX;
            }
        }
        return new File(file2, name);
    }

    public static boolean isSpecialManufacturer() {
        String str = Build.MANUFACTURER;
        return "vivo".equalsIgnoreCase(str) || "oppo".equalsIgnoreCase(str) || "EEBBK".equalsIgnoreCase(str);
    }

    public static boolean shouldCheckOatFileInCurrentSys() {
        int i = Build.VERSION.SDK_INT;
        return i > 20 && i < 26;
    }
}
