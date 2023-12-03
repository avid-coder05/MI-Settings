package miuix.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

/* loaded from: classes5.dex */
public class FileUtils {
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");

    public static boolean copyFile(File file, File file2) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                boolean copyToFile = copyToFile(fileInputStream, file2);
                fileInputStream.close();
                return copyToFile;
            } catch (Throwable th) {
                fileInputStream.close();
                throw th;
            }
        } catch (IOException unused) {
            return false;
        }
    }

    /* JADX WARN: Finally extract failed */
    public static boolean copyToFile(InputStream inputStream, File file) {
        try {
            if (!file.exists() || file.delete()) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                try {
                    byte[] bArr = new byte[4096];
                    while (true) {
                        int read = inputStream.read(bArr);
                        if (read < 0) {
                            break;
                        }
                        fileOutputStream.write(bArr, 0, read);
                    }
                    fileOutputStream.flush();
                    try {
                        fileOutputStream.getFD().sync();
                    } catch (IOException unused) {
                    }
                    fileOutputStream.close();
                    return true;
                } catch (Throwable th) {
                    fileOutputStream.flush();
                    try {
                        fileOutputStream.getFD().sync();
                    } catch (IOException unused2) {
                    }
                    fileOutputStream.close();
                    throw th;
                }
            }
            return false;
        } catch (IOException unused3) {
            return false;
        }
    }
}
