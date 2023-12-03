package miui.settings.commonlib;

import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/* loaded from: classes4.dex */
public class DeviceUtils {
    private static final int BUFFE_READER_SIZE = 8192;
    private static final String TAG = "Utils";
    private static int mTotalRamStr;

    public static int formatSizeWith1024(long j) {
        float f = (float) j;
        if (f > 921.6d) {
            float f2 = f / 1024.0f;
            if (f2 > 921.6d) {
                if (f2 / 1024.0f > 921.6d) {
                    return (int) Math.ceil(r6 / 1024.0f);
                }
            }
        }
        return 0;
    }

    private static int getRamFromProcMv() {
        FileReader fileReader;
        BufferedReader bufferedReader;
        String[] split;
        String str;
        try {
            fileReader = new FileReader("proc/mv");
            try {
                bufferedReader = new BufferedReader(fileReader, BUFFE_READER_SIZE);
            } finally {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!new File("proc/mv").exists()) {
                Log.i(TAG, "proc/mv not exist");
                bufferedReader.close();
                fileReader.close();
                return 0;
            }
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    bufferedReader.close();
                    fileReader.close();
                    break;
                } else if (!TextUtils.isEmpty(readLine) && readLine.startsWith("D:") && (split = readLine.split(" ")) != null && split.length >= 3 && (str = split[2]) != null && TextUtils.isDigitsOnly(str)) {
                    try {
                        int parseInt = Integer.parseInt(str);
                        bufferedReader.close();
                        fileReader.close();
                        return parseInt;
                    } catch (Exception unused) {
                        continue;
                    }
                }
            }
            return 0;
        } finally {
        }
    }

    private static int getTotalRam() {
        int i = mTotalRamStr;
        if (i != 0) {
            return i;
        }
        int ramFromProcMv = getRamFromProcMv();
        mTotalRamStr = ramFromProcMv;
        return ramFromProcMv;
    }

    public static boolean isMIUILite() {
        return getTotalRam() <= 4;
    }
}
