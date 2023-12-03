package miui.io;

import android.system.Os;

/* loaded from: classes3.dex */
public class FileStat {
    public static long getCreatedTime(String str) {
        try {
            return Os.lstat(str).st_ctime * 1000;
        } catch (Exception unused) {
            return 0L;
        }
    }
}
