package com.xiaomi.onetrack.util;

import android.text.TextUtils;
import android.util.LruCache;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;

/* loaded from: classes2.dex */
public class k {
    private static LruCache<String, a> d = new l(1048576);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class a {
        String a;

        private a() {
        }

        /* synthetic */ a(l lVar) {
            this();
        }
    }

    public static String a() {
        return c("tombstone");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v4, types: [com.xiaomi.onetrack.util.l] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v7, types: [java.io.Closeable] */
    /* JADX WARN: Type inference failed for: r0v9 */
    public static String a(String str) {
        BufferedReader bufferedReader;
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        a aVar = d.get(str);
        if (aVar != null) {
            return aVar.a;
        }
        BufferedReader bufferedReader2 = 0;
        bufferedReader2 = 0;
        try {
            try {
                File file = new File(b(), str);
                StringBuilder sb = new StringBuilder();
                if (file.exists()) {
                    bufferedReader = new BufferedReader(new FileReader(file));
                    while (true) {
                        try {
                            String readLine = bufferedReader.readLine();
                            if (readLine == null) {
                                break;
                            }
                            sb.append(readLine);
                        } catch (Exception e) {
                            e = e;
                            bufferedReader2 = bufferedReader;
                            p.c("FileUtil", "get error:" + e.toString());
                            m.a((Closeable) bufferedReader2);
                            return "";
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader2 = bufferedReader;
                            m.a((Closeable) bufferedReader2);
                            throw th;
                        }
                    }
                } else {
                    bufferedReader = null;
                }
                String sb2 = sb.toString();
                a aVar2 = new a(bufferedReader2);
                aVar2.a = sb2;
                d.put(str, aVar2);
                m.a(bufferedReader);
                return sb2;
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static String a(String str, int i) {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2 = null;
        try {
            File file = new File(str);
            StringBuilder sb = new StringBuilder();
            if (file.exists()) {
                bufferedReader = new BufferedReader(new FileReader(file));
                do {
                    try {
                        try {
                            String readLine = bufferedReader.readLine();
                            if (readLine == null) {
                                break;
                            }
                            sb.append(readLine);
                            sb.append("\n");
                        } catch (Exception e) {
                            e = e;
                            p.c("FileUtil", "get error:" + e.toString());
                            m.a(bufferedReader);
                            return null;
                        }
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader2 = bufferedReader;
                        m.a(bufferedReader2);
                        throw th;
                    }
                } while (sb.length() <= i);
            } else {
                bufferedReader = null;
            }
            if (sb.length() > i) {
                String substring = sb.substring(0, i - 1);
                m.a(bufferedReader);
                return substring;
            }
            String sb2 = sb.toString();
            m.a(bufferedReader);
            return sb2;
        } catch (Exception e2) {
            e = e2;
            bufferedReader = null;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static void a(File file) {
        try {
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            p.c("FileUtil", "failed to remove file: " + file.getName() + "," + e.toString());
        }
    }

    private static String b() {
        return c("onetrack");
    }

    private static String c(String str) {
        String str2 = com.xiaomi.onetrack.e.a.a().getFilesDir().getAbsolutePath() + File.separator + str;
        File file = new File(str2);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str2;
    }
}
