package com.miui.privacypolicy;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/* loaded from: classes2.dex */
public class FileUtils {
    /* JADX INFO: Access modifiers changed from: protected */
    public static void closeQuietly(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e("Privacy_FileUtils", "closeQuietly InputStream error " + e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void closeQuietly(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.flush();
            } catch (IOException e) {
                Log.e("Privacy_FileUtils", "closeQuietly OutputStream error " + e);
            }
            try {
                outputStream.close();
            } catch (IOException e2) {
                Log.e("Privacy_FileUtils", "closeQuietly OutputStream error " + e2);
            }
        }
    }

    protected static void closeQuietly(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                Log.e("Privacy_FileUtils", "closeQuietly Writer error " + e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean deleteFile(Context context, String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getFilesDir().getPath());
        String str3 = File.separator;
        sb.append(str3);
        sb.append("privacypolicy");
        sb.append(str3);
        sb.append(str2);
        sb.append(str3);
        sb.append(str);
        File file = new File(sb.toString());
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isAgreeErrorFileExit(Context context, String str) {
        return !TextUtils.isEmpty(readData(context, "privacy_agree_error", str));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isVersionFileExit(Context context, String str) {
        return !TextUtils.isEmpty(readData(context, "privacy_version", str));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v4, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7 */
    public static String readData(Context context, String str, String str2) {
        FileInputStream fileInputStream;
        StringBuilder sb = new StringBuilder();
        sb.append(context.getFilesDir().getPath());
        String str3 = File.separator;
        sb.append(str3);
        sb.append("privacypolicy");
        sb.append(str3);
        sb.append((String) str2);
        sb.append(str3);
        sb.append(str);
        String sb2 = sb.toString();
        if (new File(sb2).exists()) {
            InputStream inputStream = null;
            String str4 = null;
            try {
                try {
                    fileInputStream = new FileInputStream(sb2);
                    try {
                        str4 = readInputStream(fileInputStream);
                        str2 = fileInputStream;
                    } catch (Exception e) {
                        e = e;
                        Log.e("Privacy_FileUtils", "readData fail!", e);
                        str2 = fileInputStream;
                        closeQuietly((InputStream) str2);
                        return str4;
                    }
                } catch (Throwable th) {
                    th = th;
                    inputStream = str2;
                    closeQuietly(inputStream);
                    throw th;
                }
            } catch (Exception e2) {
                e = e2;
                fileInputStream = null;
            } catch (Throwable th2) {
                th = th2;
                closeQuietly(inputStream);
                throw th;
            }
            closeQuietly((InputStream) str2);
            return str4;
        }
        return "";
    }

    private static String readInputStream(FileInputStream fileInputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
        while (true) {
            try {
                try {
                    int read = fileInputStream.read(bArr, 0, MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE);
                    if (read <= 0) {
                        return byteArrayOutputStream.toString();
                    }
                    byteArrayOutputStream.write(bArr, 0, read);
                } catch (Exception e) {
                    Log.e("Privacy_FileUtils", "readInputStream fail!", e);
                    closeQuietly(byteArrayOutputStream);
                    return null;
                }
            } finally {
                closeQuietly(byteArrayOutputStream);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r6v1 */
    /* JADX WARN: Type inference failed for: r6v2 */
    /* JADX WARN: Type inference failed for: r6v3 */
    /* JADX WARN: Type inference failed for: r6v4, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r6v5 */
    /* JADX WARN: Type inference failed for: r6v6, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r6v7, types: [java.io.OutputStream, java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r6v8 */
    /* JADX WARN: Type inference failed for: r6v9 */
    public static void saveData(String str, Context context, String str2, String str3) {
        BufferedWriter bufferedWriter;
        StringBuilder sb = new StringBuilder();
        sb.append(context.getFilesDir().getPath());
        String str4 = File.separator;
        sb.append(str4);
        sb.append("privacypolicy");
        sb.append(str4);
        sb.append((String) str3);
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        BufferedWriter bufferedWriter2 = null;
        try {
            try {
                str3 = new FileOutputStream(new File(file, str2));
                try {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(str3));
                } catch (Exception e) {
                    e = e;
                }
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e2) {
            e = e2;
            str3 = 0;
        } catch (Throwable th2) {
            th = th2;
            str3 = 0;
        }
        try {
            bufferedWriter.write(str);
            closeQuietly(bufferedWriter);
            str3 = str3;
        } catch (Exception e3) {
            e = e3;
            bufferedWriter2 = bufferedWriter;
            Log.e("Privacy_FileUtils", "saveData fail!", e);
            closeQuietly(bufferedWriter2);
            str3 = str3;
            closeQuietly((OutputStream) str3);
        } catch (Throwable th3) {
            th = th3;
            bufferedWriter2 = bufferedWriter;
            closeQuietly(bufferedWriter2);
            closeQuietly((OutputStream) str3);
            throw th;
        }
        closeQuietly((OutputStream) str3);
    }
}
