package com.xiaomi.onetrack.b;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class h {
    private static String h = "onetrack_netaccess_%s";
    private static SimpleDateFormat i = new SimpleDateFormat("yyyyMMdd");
    private static boolean j = false;
    private static volatile boolean k = true;
    private static volatile boolean l = false;

    public static synchronized void a(com.xiaomi.onetrack.api.d dVar) {
        synchronized (h.class) {
            if (d()) {
                if (dVar != null && b()) {
                    l = true;
                    com.xiaomi.onetrack.util.i.a(new j(dVar));
                }
            }
        }
    }

    public static void a(String str, String str2) {
        com.xiaomi.onetrack.util.i.a(new i(str, str2));
    }

    public static void a(boolean z) {
        j = z;
    }

    public static void b(boolean z) {
        File file = new File(com.xiaomi.onetrack.e.a.a().getFilesDir(), ".ot_net_allowed");
        File file2 = new File(com.xiaomi.onetrack.e.a.a().getFilesDir(), ".ot_net_disallowed");
        try {
            if (z) {
                file.createNewFile();
                if (file2.exists()) {
                    file2.delete();
                }
            } else {
                file2.createNewFile();
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (IOException e) {
            com.xiaomi.onetrack.util.p.b("NetworkAccessManager", "setNetworkAccessStateEnabled: " + z + "failed ", e);
        }
    }

    public static boolean b() {
        return !new File(com.xiaomi.onetrack.e.a.a().getFilesDir(), ".ot_net_disallowed").exists();
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x0092 A[Catch: all -> 0x00b2, TryCatch #1 {, blocks: (B:4:0x0003, B:9:0x002c, B:16:0x0052, B:17:0x0055, B:31:0x008a, B:33:0x0092, B:34:0x009f, B:36:0x00a5, B:30:0x0086, B:41:0x00ab, B:42:0x00b1), top: B:46:0x0003 }] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00a5 A[Catch: all -> 0x00b2, TRY_LEAVE, TryCatch #1 {, blocks: (B:4:0x0003, B:9:0x002c, B:16:0x0052, B:17:0x0055, B:31:0x008a, B:33:0x0092, B:34:0x009f, B:36:0x00a5, B:30:0x0086, B:41:0x00ab, B:42:0x00b1), top: B:46:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static synchronized java.util.List<org.json.JSONObject> c() {
        /*
            java.lang.Class<com.xiaomi.onetrack.b.h> r0 = com.xiaomi.onetrack.b.h.class
            monitor-enter(r0)
            java.text.SimpleDateFormat r1 = com.xiaomi.onetrack.b.h.i     // Catch: java.lang.Throwable -> Lb2
            java.util.Date r2 = new java.util.Date     // Catch: java.lang.Throwable -> Lb2
            r2.<init>()     // Catch: java.lang.Throwable -> Lb2
            java.lang.String r1 = r1.format(r2)     // Catch: java.lang.Throwable -> Lb2
            java.lang.String r2 = com.xiaomi.onetrack.b.h.h     // Catch: java.lang.Throwable -> Lb2
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> Lb2
            r5 = 0
            r4[r5] = r1     // Catch: java.lang.Throwable -> Lb2
            java.lang.String r1 = java.lang.String.format(r2, r4)     // Catch: java.lang.Throwable -> Lb2
            java.io.File r2 = new java.io.File     // Catch: java.lang.Throwable -> Lb2
            java.lang.String r4 = e()     // Catch: java.lang.Throwable -> Lb2
            r2.<init>(r4, r1)     // Catch: java.lang.Throwable -> Lb2
            boolean r1 = r2.exists()     // Catch: java.lang.Throwable -> Lb2
            r4 = 0
            if (r1 != 0) goto L2c
            monitor-exit(r0)
            return r4
        L2c:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch: java.lang.Throwable -> Lb2
            r1.<init>()     // Catch: java.lang.Throwable -> Lb2
            java.io.FileReader r5 = new java.io.FileReader     // Catch: java.lang.Throwable -> L62 java.lang.Exception -> L65
            r5.<init>(r2)     // Catch: java.lang.Throwable -> L62 java.lang.Exception -> L65
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
            r2.<init>(r5)     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
        L3b:
            java.lang.String r4 = r2.readLine()     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> La9
            if (r4 == 0) goto L52
            byte[] r4 = com.xiaomi.onetrack.c.c.a(r4)     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> La9
            java.lang.String r4 = com.xiaomi.onetrack.b.b.a(r4)     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> La9
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> La9
            r6.<init>(r4)     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> La9
            r1.add(r6)     // Catch: java.lang.Exception -> L59 java.lang.Throwable -> La9
            goto L3b
        L52:
            com.xiaomi.onetrack.util.m.a(r2)     // Catch: java.lang.Throwable -> Lb2
        L55:
            com.xiaomi.onetrack.util.m.a(r5)     // Catch: java.lang.Throwable -> Lb2
            goto L8a
        L59:
            r4 = move-exception
            goto L69
        L5b:
            r1 = move-exception
            goto Lab
        L5d:
            r2 = move-exception
            r9 = r4
            r4 = r2
            r2 = r9
            goto L69
        L62:
            r1 = move-exception
            r5 = r4
            goto Lab
        L65:
            r2 = move-exception
            r5 = r4
            r4 = r2
            r2 = r5
        L69:
            java.lang.String r6 = "NetworkAccessManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> La9
            r7.<init>()     // Catch: java.lang.Throwable -> La9
            java.lang.String r8 = "cta getCacheData error: "
            r7.append(r8)     // Catch: java.lang.Throwable -> La9
            java.lang.String r8 = r4.toString()     // Catch: java.lang.Throwable -> La9
            r7.append(r8)     // Catch: java.lang.Throwable -> La9
            java.lang.String r7 = r7.toString()     // Catch: java.lang.Throwable -> La9
            com.xiaomi.onetrack.util.p.b(r6, r7)     // Catch: java.lang.Throwable -> La9
            r4.printStackTrace()     // Catch: java.lang.Throwable -> La9
            com.xiaomi.onetrack.util.m.a(r2)     // Catch: java.lang.Throwable -> Lb2
            goto L55
        L8a:
            int r2 = r1.size()     // Catch: java.lang.Throwable -> Lb2
            r4 = 100
            if (r2 <= r4) goto L9f
            int r2 = r1.size()     // Catch: java.lang.Throwable -> Lb2
            int r2 = r2 - r4
            int r4 = r1.size()     // Catch: java.lang.Throwable -> Lb2
            java.util.List r1 = r1.subList(r2, r4)     // Catch: java.lang.Throwable -> Lb2
        L9f:
            int r2 = r1.size()     // Catch: java.lang.Throwable -> Lb2
            if (r2 <= 0) goto La7
            com.xiaomi.onetrack.b.h.k = r3     // Catch: java.lang.Throwable -> Lb2
        La7:
            monitor-exit(r0)
            return r1
        La9:
            r1 = move-exception
            r4 = r2
        Lab:
            com.xiaomi.onetrack.util.m.a(r4)     // Catch: java.lang.Throwable -> Lb2
            com.xiaomi.onetrack.util.m.a(r5)     // Catch: java.lang.Throwable -> Lb2
            throw r1     // Catch: java.lang.Throwable -> Lb2
        Lb2:
            r1 = move-exception
            monitor-exit(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.onetrack.b.h.c():java.util.List");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void c(String str, String str2) {
        FileWriter fileWriter;
        byte[] a;
        BufferedWriter bufferedWriter;
        synchronized (h.class) {
            File file = new File(e(), String.format(h, i.format(new Date())));
            BufferedWriter bufferedWriter2 = null;
            try {
                if (!file.exists()) {
                    if (file.getParentFile().exists()) {
                        file.createNewFile();
                    } else {
                        new File(file.getParentFile().getAbsolutePath()).mkdirs();
                        file.createNewFile();
                    }
                }
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("eventName", str);
                jSONObject.put("data", str2);
                a = b.a(jSONObject.toString());
                fileWriter = new FileWriter(file, true);
                try {
                    try {
                        bufferedWriter = new BufferedWriter(fileWriter);
                    } catch (Exception e) {
                        e = e;
                    }
                } catch (Throwable th) {
                    th = th;
                }
            } catch (Exception e2) {
                e = e2;
                fileWriter = null;
            } catch (Throwable th2) {
                th = th2;
                fileWriter = null;
            }
            try {
                bufferedWriter.write(com.xiaomi.onetrack.c.c.a(a));
                bufferedWriter.newLine();
                k = true;
                com.xiaomi.onetrack.util.m.a(bufferedWriter);
            } catch (Exception e3) {
                e = e3;
                bufferedWriter2 = bufferedWriter;
                com.xiaomi.onetrack.util.p.b("NetworkAccessManager", "cta doSaveData error: " + e.toString());
                e.printStackTrace();
                com.xiaomi.onetrack.util.m.a(bufferedWriter2);
                com.xiaomi.onetrack.util.m.a(fileWriter);
            } catch (Throwable th3) {
                th = th3;
                bufferedWriter2 = bufferedWriter;
                com.xiaomi.onetrack.util.m.a(bufferedWriter2);
                com.xiaomi.onetrack.util.m.a(fileWriter);
                throw th;
            }
            com.xiaomi.onetrack.util.m.a(fileWriter);
        }
    }

    public static synchronized void c(boolean z) {
        File file;
        synchronized (h.class) {
            try {
                file = new File(e());
            } catch (Exception e) {
                com.xiaomi.onetrack.util.p.b("NetworkAccessManager", "cta removeObsoleteEvent error: " + e.toString());
                e.printStackTrace();
            }
            if (file.exists() && file.isDirectory()) {
                String format = String.format(h, i.format(new Date()));
                File[] listFiles = file.listFiles();
                for (int i2 = 0; i2 < listFiles.length; i2++) {
                    if (listFiles[i2].isFile() && (z || !listFiles[i2].getName().equalsIgnoreCase(format))) {
                        listFiles[i2].delete();
                    }
                }
                if (file.listFiles().length == 0) {
                    k = false;
                }
                return;
            }
            k = false;
        }
    }

    public static boolean d() {
        return !l && k;
    }

    private static String e() {
        return com.xiaomi.onetrack.e.a.a().getFilesDir().getAbsolutePath() + File.separator + "networkAccess";
    }
}
