package com.xiaomi.onetrack.util;

import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.system.Os;
import android.text.TextUtils;
import android.view.MiuiWindowManager$LayoutParams;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes2.dex */
public class b {
    private static final String[] m = {"/data/local/su", "/data/local/bin/su", "/data/local/xbin/su", "/system/xbin/su", "/system/bin/su", "/system/bin/.ext/su", "/system/bin/failsafe/su", "/system/sd/xbin/su", "/system/usr/we-need-root/su", "/sbin/su", "/su/bin/su"};

    public static long a(String str) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static String a(int i, int i2, int i3) {
        int myPid = Process.myPid();
        StringBuilder sb = new StringBuilder();
        sb.append("logcat:\n");
        if (i > 0) {
            a(myPid, sb, "main", i, 'D');
        }
        if (i2 > 0) {
            a(myPid, sb, "system", i2, 'W');
        }
        if (i3 > 0) {
            a(myPid, sb, "events", i2, 'I');
        }
        sb.append("\n");
        return sb.toString();
    }

    public static String a(Context context) {
        String str;
        try {
            str = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception unused) {
            str = null;
        }
        return TextUtils.isEmpty(str) ? "unknown" : str;
    }

    /* JADX WARN: Code restructure failed: missing block: B:33:0x0078, code lost:
    
        if (r0 == null) goto L35;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String a(android.content.Context r4, int r5) {
        /*
            java.lang.String r0 = "activity"
            java.lang.Object r4 = r4.getSystemService(r0)     // Catch: java.lang.Exception -> L2f
            android.app.ActivityManager r4 = (android.app.ActivityManager) r4     // Catch: java.lang.Exception -> L2f
            if (r4 == 0) goto L2f
            java.util.List r4 = r4.getRunningAppProcesses()     // Catch: java.lang.Exception -> L2f
            if (r4 == 0) goto L2f
            java.util.Iterator r4 = r4.iterator()     // Catch: java.lang.Exception -> L2f
        L14:
            boolean r0 = r4.hasNext()     // Catch: java.lang.Exception -> L2f
            if (r0 == 0) goto L2f
            java.lang.Object r0 = r4.next()     // Catch: java.lang.Exception -> L2f
            android.app.ActivityManager$RunningAppProcessInfo r0 = (android.app.ActivityManager.RunningAppProcessInfo) r0     // Catch: java.lang.Exception -> L2f
            int r1 = r0.pid     // Catch: java.lang.Exception -> L2f
            if (r1 != r5) goto L14
            java.lang.String r1 = r0.processName     // Catch: java.lang.Exception -> L2f
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch: java.lang.Exception -> L2f
            if (r1 != 0) goto L14
            java.lang.String r4 = r0.processName     // Catch: java.lang.Exception -> L2f
            return r4
        L2f:
            r4 = 0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L6e java.lang.Exception -> L77
            java.io.FileReader r1 = new java.io.FileReader     // Catch: java.lang.Throwable -> L6e java.lang.Exception -> L77
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L6e java.lang.Exception -> L77
            r2.<init>()     // Catch: java.lang.Throwable -> L6e java.lang.Exception -> L77
            java.lang.String r3 = "/proc/"
            r2.append(r3)     // Catch: java.lang.Throwable -> L6e java.lang.Exception -> L77
            r2.append(r5)     // Catch: java.lang.Throwable -> L6e java.lang.Exception -> L77
            java.lang.String r5 = "/cmdline"
            r2.append(r5)     // Catch: java.lang.Throwable -> L6e java.lang.Exception -> L77
            java.lang.String r5 = r2.toString()     // Catch: java.lang.Throwable -> L6e java.lang.Exception -> L77
            r1.<init>(r5)     // Catch: java.lang.Throwable -> L6e java.lang.Exception -> L77
            r0.<init>(r1)     // Catch: java.lang.Throwable -> L6e java.lang.Exception -> L77
            java.lang.String r5 = r0.readLine()     // Catch: java.lang.Throwable -> L6c java.lang.Exception -> L78
            boolean r1 = android.text.TextUtils.isEmpty(r5)     // Catch: java.lang.Throwable -> L6c java.lang.Exception -> L78
            if (r1 != 0) goto L68
            java.lang.String r5 = r5.trim()     // Catch: java.lang.Throwable -> L6c java.lang.Exception -> L78
            boolean r1 = android.text.TextUtils.isEmpty(r5)     // Catch: java.lang.Throwable -> L6c java.lang.Exception -> L78
            if (r1 != 0) goto L68
            r0.close()     // Catch: java.lang.Exception -> L67
        L67:
            return r5
        L68:
            r0.close()     // Catch: java.lang.Exception -> L7b
            goto L7b
        L6c:
            r4 = move-exception
            goto L71
        L6e:
            r5 = move-exception
            r0 = r4
            r4 = r5
        L71:
            if (r0 == 0) goto L76
            r0.close()     // Catch: java.lang.Exception -> L76
        L76:
            throw r4
        L77:
            r0 = r4
        L78:
            if (r0 == 0) goto L7b
            goto L68
        L7b:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.onetrack.util.b.a(android.content.Context, int):java.lang.String");
    }

    private static String a(String str, int i) {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            try {
                try {
                    BufferedReader bufferedReader2 = new BufferedReader(new FileReader(str));
                    int i2 = 0;
                    while (true) {
                        try {
                            String readLine = bufferedReader2.readLine();
                            if (readLine == null) {
                                break;
                            }
                            String trim = readLine.trim();
                            if (trim.length() > 0) {
                                i2++;
                                if (i == 0 || i2 <= i) {
                                    sb.append("  ");
                                    sb.append(trim);
                                    sb.append("\n");
                                }
                            }
                        } catch (Exception e) {
                            e = e;
                            bufferedReader = bufferedReader2;
                            p.c("CrashUtil", "CrashUtil getInfo(" + str + ") failed", e);
                            if (bufferedReader != null) {
                                bufferedReader.close();
                            }
                            return sb.toString();
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader = bufferedReader2;
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (Exception unused) {
                                }
                            }
                            throw th;
                        }
                    }
                    if (i > 0 && i2 > i) {
                        sb.append("  ......\n");
                        sb.append("  (number of records: ");
                        sb.append(i2);
                        sb.append(")\n");
                    }
                    bufferedReader2.close();
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Exception unused2) {
        }
    }

    public static String a(Date date, Date date2, String str, String str2, String str3) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        StringBuilder sb = new StringBuilder();
        sb.append("*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***\nTombstone maker: 'OneTrack 1.2.9'\nCrash type: '");
        sb.append(str);
        sb.append("'\nStart time: '");
        sb.append(simpleDateFormat.format(date));
        sb.append("'\nCrash time: '");
        sb.append(simpleDateFormat.format(date2));
        sb.append("'\nApp ID: '");
        sb.append(str2);
        sb.append("'\nApp version: '");
        sb.append(str3);
        sb.append("'\nRooted: '");
        sb.append(a() ? "Yes" : "No");
        sb.append("'\nAPI level: '");
        sb.append(Build.VERSION.SDK_INT);
        sb.append("'\nOS version: '");
        sb.append(Build.VERSION.RELEASE);
        sb.append("'\nABI list: '");
        sb.append(b());
        sb.append("'\nManufacturer: '");
        sb.append(Build.MANUFACTURER);
        sb.append("'\nBrand: '");
        sb.append(Build.BRAND);
        sb.append("'\nModel: '");
        sb.append(Build.MODEL);
        sb.append("'\nBuild fingerprint: '");
        sb.append(Build.FINGERPRINT);
        sb.append("'\n");
        return sb.toString();
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x00e6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void a(int r9, java.lang.StringBuilder r10, java.lang.String r11, int r12, char r13) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 0
            r2 = 24
            if (r0 < r2) goto L9
            r0 = 1
            goto La
        L9:
            r0 = r1
        La:
            java.lang.String r9 = java.lang.Integer.toString(r9)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = " "
            r2.append(r3)
            r2.append(r9)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.lang.String r5 = "/system/bin/logcat"
            r4.add(r5)
            java.lang.String r5 = "-b"
            r4.add(r5)
            r4.add(r11)
            java.lang.String r5 = "-d"
            r4.add(r5)
            java.lang.String r5 = "-v"
            r4.add(r5)
            java.lang.String r5 = "threadtime"
            r4.add(r5)
            java.lang.String r5 = "-t"
            r4.add(r5)
            if (r0 == 0) goto L4c
            goto L54
        L4c:
            double r5 = (double) r12
            r7 = 4608083138725491507(0x3ff3333333333333, double:1.2)
            double r5 = r5 * r7
            int r12 = (int) r5
        L54:
            java.lang.String r12 = java.lang.Integer.toString(r12)
            r4.add(r12)
            if (r0 == 0) goto L65
            java.lang.String r12 = "--pid"
            r4.add(r12)
            r4.add(r9)
        L65:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r12 = "*:"
            r9.append(r12)
            r9.append(r13)
            java.lang.String r9 = r9.toString()
            r4.add(r9)
            java.lang.Object[] r9 = r4.toArray()
            java.lang.String r12 = "--------- tail end of log "
            r10.append(r12)
            r10.append(r11)
            java.lang.String r11 = " ("
            r10.append(r11)
            java.lang.String r9 = android.text.TextUtils.join(r3, r9)
            r10.append(r9)
            java.lang.String r9 = ")\n"
            r10.append(r9)
            r9 = 0
            java.lang.ProcessBuilder r11 = new java.lang.ProcessBuilder     // Catch: java.lang.Throwable -> Ld1 java.lang.Exception -> Ld5
            java.lang.String[] r12 = new java.lang.String[r1]     // Catch: java.lang.Throwable -> Ld1 java.lang.Exception -> Ld5
            r11.<init>(r12)     // Catch: java.lang.Throwable -> Ld1 java.lang.Exception -> Ld5
            java.lang.ProcessBuilder r11 = r11.command(r4)     // Catch: java.lang.Throwable -> Ld1 java.lang.Exception -> Ld5
            java.lang.Process r11 = r11.start()     // Catch: java.lang.Throwable -> Ld1 java.lang.Exception -> Ld5
            java.io.BufferedReader r12 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> Ld1 java.lang.Exception -> Ld5
            java.io.InputStreamReader r13 = new java.io.InputStreamReader     // Catch: java.lang.Throwable -> Ld1 java.lang.Exception -> Ld5
            java.io.InputStream r11 = r11.getInputStream()     // Catch: java.lang.Throwable -> Ld1 java.lang.Exception -> Ld5
            r13.<init>(r11)     // Catch: java.lang.Throwable -> Ld1 java.lang.Exception -> Ld5
            r12.<init>(r13)     // Catch: java.lang.Throwable -> Ld1 java.lang.Exception -> Ld5
        Lb4:
            java.lang.String r9 = r12.readLine()     // Catch: java.lang.Exception -> Lcf java.lang.Throwable -> Le3
            if (r9 == 0) goto Lcb
            if (r0 != 0) goto Lc2
            boolean r11 = r9.contains(r2)     // Catch: java.lang.Exception -> Lcf java.lang.Throwable -> Le3
            if (r11 == 0) goto Lb4
        Lc2:
            r10.append(r9)     // Catch: java.lang.Exception -> Lcf java.lang.Throwable -> Le3
            java.lang.String r9 = "\n"
            r10.append(r9)     // Catch: java.lang.Exception -> Lcf java.lang.Throwable -> Le3
            goto Lb4
        Lcb:
            r12.close()     // Catch: java.io.IOException -> Le2
            goto Le2
        Lcf:
            r9 = move-exception
            goto Ld8
        Ld1:
            r10 = move-exception
            r12 = r9
            r9 = r10
            goto Le4
        Ld5:
            r10 = move-exception
            r12 = r9
            r9 = r10
        Ld8:
            java.lang.String r10 = "CrashUtil"
            java.lang.String r11 = "CrashUtil run logcat command failed"
            com.xiaomi.onetrack.util.p.b(r10, r11, r9)     // Catch: java.lang.Throwable -> Le3
            if (r12 == 0) goto Le2
            goto Lcb
        Le2:
            return
        Le3:
            r9 = move-exception
        Le4:
            if (r12 == 0) goto Le9
            r12.close()     // Catch: java.io.IOException -> Le9
        Le9:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.onetrack.util.b.a(int, java.lang.StringBuilder, java.lang.String, int, char):void");
    }

    static boolean a() {
        try {
            for (String str : m) {
                if (new File(str).exists()) {
                    return true;
                }
            }
        } catch (Exception unused) {
        }
        return false;
    }

    static String b() {
        if (Build.VERSION.SDK_INT >= 21) {
            return TextUtils.join(",", Build.SUPPORTED_ABIS);
        }
        String str = Build.CPU_ABI;
        String str2 = Build.CPU_ABI2;
        if (TextUtils.isEmpty(str2)) {
            return str;
        }
        return str + "," + str2;
    }

    private static String b(String str) {
        return a(str, 0);
    }

    static String c() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Process Summary (From: android.os.Debug.MemoryInfo)\n");
        Locale locale = Locale.US;
        sb.append(String.format(locale, "%21s %8s\n", "", "Pss(KB)"));
        sb.append(String.format(locale, "%21s %8s\n", "", "------"));
        try {
            Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
            Debug.getMemoryInfo(memoryInfo);
            int i = Build.VERSION.SDK_INT;
            if (i >= 23) {
                sb.append(String.format(locale, "%21s %8s\n", "Java Heap:", memoryInfo.getMemoryStat("summary.java-heap")));
                sb.append(String.format(locale, "%21s %8s\n", "Native Heap:", memoryInfo.getMemoryStat("summary.native-heap")));
                sb.append(String.format(locale, "%21s %8s\n", "Code:", memoryInfo.getMemoryStat("summary.code")));
                sb.append(String.format(locale, "%21s %8s\n", "Stack:", memoryInfo.getMemoryStat("summary.stack")));
                sb.append(String.format(locale, "%21s %8s\n", "Graphics:", memoryInfo.getMemoryStat("summary.graphics")));
                sb.append(String.format(locale, "%21s %8s\n", "Private Other:", memoryInfo.getMemoryStat("summary.private-other")));
                sb.append(String.format(locale, "%21s %8s\n", "System:", memoryInfo.getMemoryStat("summary.system")));
                sb.append(String.format(locale, "%21s %8s %21s %8s\n", "TOTAL:", memoryInfo.getMemoryStat("summary.total-pss"), "TOTAL SWAP:", memoryInfo.getMemoryStat("summary.total-swap")));
            } else {
                sb.append(String.format(locale, "%21s %8s\n", "Java Heap:", "~ " + memoryInfo.dalvikPrivateDirty));
                sb.append(String.format(locale, "%21s %8s\n", "Native Heap:", String.valueOf(memoryInfo.nativePrivateDirty)));
                sb.append(String.format(locale, "%21s %8s\n", "Private Other:", "~ " + memoryInfo.otherPrivateDirty));
                if (i >= 19) {
                    sb.append(String.format(locale, "%21s %8s\n", "System:", String.valueOf((memoryInfo.getTotalPss() - memoryInfo.getTotalPrivateDirty()) - memoryInfo.getTotalPrivateClean())));
                } else {
                    sb.append(String.format(locale, "%21s %8s\n", "System:", "~ " + (memoryInfo.getTotalPss() - memoryInfo.getTotalPrivateDirty())));
                }
                sb.append(String.format(locale, "%21s %8s\n", "TOTAL:", String.valueOf(memoryInfo.getTotalPss())));
            }
        } catch (Exception e) {
            p.b("CrashUtil", "CrashUtil getProcessMemoryInfo failed", e);
        }
        return sb.toString();
    }

    public static String d() {
        return "memory info:\n System Summary (From: /proc/meminfo)\n" + b("/proc/meminfo") + "-\n Process Status (From: /proc/PID/status)\n" + b("/proc/self/status") + "-\n Process Limits (From: /proc/PID/limits)\n" + b("/proc/self/limits") + "-\n" + c() + "\n";
    }

    public static String e() {
        if (Build.VERSION.SDK_INT >= 29) {
            return "network info:\nNot supported on Android Q (API level 29) and later.\n\n";
        }
        return "network info:\n TCP over IPv4 (From: /proc/PID/net/tcp)\n" + a("/proc/self/net/tcp", (int) MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE) + "-\n TCP over IPv6 (From: /proc/PID/net/tcp6)\n" + a("/proc/self/net/tcp6", (int) MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE) + "-\n UDP over IPv4 (From: /proc/PID/net/udp)\n" + a("/proc/self/net/udp", (int) MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE) + "-\n UDP over IPv6 (From: /proc/PID/net/udp6)\n" + a("/proc/self/net/udp6", (int) MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE) + "-\n ICMP in IPv4 (From: /proc/PID/net/icmp)\n" + a("/proc/self/net/icmp", 256) + "-\n ICMP in IPv6 (From: /proc/PID/net/icmp6)\n" + a("/proc/self/net/icmp6", 256) + "-\n UNIX domain (From: /proc/PID/net/unix)\n" + a("/proc/self/net/unix", 256) + "\n";
    }

    public static String f() {
        StringBuilder sb = new StringBuilder("open files:\n");
        try {
            File[] listFiles = new File("/proc/self/fd").listFiles(new c());
            if (listFiles != null) {
                int i = 0;
                for (File file : listFiles) {
                    String str = null;
                    try {
                        str = Build.VERSION.SDK_INT >= 21 ? Os.readlink(file.getAbsolutePath()) : file.getCanonicalPath();
                    } catch (Exception unused) {
                    }
                    sb.append("    fd ");
                    sb.append(file.getName());
                    sb.append(": ");
                    sb.append(TextUtils.isEmpty(str) ? "???" : str.trim());
                    sb.append('\n');
                    i++;
                    if (i > 1024) {
                        break;
                    }
                }
                if (listFiles.length > 1024) {
                    sb.append("    ......\n");
                }
                sb.append("    (number of FDs: ");
                sb.append(listFiles.length);
                sb.append(")\n");
            }
        } catch (Exception unused2) {
        }
        sb.append('\n');
        return sb.toString();
    }
}
