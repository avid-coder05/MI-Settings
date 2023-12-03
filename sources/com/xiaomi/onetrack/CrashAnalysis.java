package com.xiaomi.onetrack;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.onetrack.api.g;
import com.xiaomi.onetrack.c.d;
import com.xiaomi.onetrack.util.aa;
import com.xiaomi.onetrack.util.ac;
import com.xiaomi.onetrack.util.b;
import com.xiaomi.onetrack.util.i;
import com.xiaomi.onetrack.util.k;
import com.xiaomi.onetrack.util.p;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.mipub.MipubStat;
import miui.provider.ExtraContacts;

/* loaded from: classes2.dex */
public class CrashAnalysis {
    private static final AtomicBoolean t = new AtomicBoolean(false);
    private final FileProcessor[] u;
    private final g v;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class FileProcessor {
        final List<File> a = new ArrayList();
        final String b;
        final String c;

        FileProcessor(String str) {
            this.c = str;
            this.b = str + ".xcrash";
        }

        private String a(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            String[] split = str.split("__");
            if (split.length == 2) {
                String[] split2 = split[0].split("_");
                if (split2.length == 3) {
                    return split2[2];
                }
                return null;
            }
            return null;
        }

        void a() {
            for (int i = 0; i < this.a.size(); i++) {
                String absolutePath = this.a.get(i).getAbsoluteFile().getAbsolutePath();
                String a = a(absolutePath);
                String a2 = k.a(absolutePath, 102400);
                if (!TextUtils.isEmpty(a2) && CrashAnalysis.this.v != null) {
                    String d = CrashAnalysis.d(a2, this.c);
                    String c = CrashAnalysis.c(a2, this.c);
                    long b = CrashAnalysis.b(a2);
                    p.a("CrashAnalysis", "fileName: " + absolutePath);
                    p.a("CrashAnalysis", "feature id: " + d);
                    p.a("CrashAnalysis", "error: " + c);
                    p.a("CrashAnalysis", "crashTimeStamp: " + b);
                    CrashAnalysis.this.v.a(a2, c, this.c, a, d, b);
                    k.a(new File(absolutePath));
                    p.a("CrashAnalysis", "remove reported crash file");
                }
            }
        }

        boolean a(File file) {
            if (file.getName().contains(this.b)) {
                this.a.add(file);
                return true;
            }
            return false;
        }
    }

    private CrashAnalysis(Context context, g gVar) {
        try {
            Object newInstance = Class.forName("xcrash.XCrash$InitParameters").getConstructor(new Class[0]).newInstance(new Object[0]);
            Boolean bool = Boolean.FALSE;
            a(newInstance, "setNativeDumpAllThreads", bool);
            a(newInstance, "setLogDir", a());
            a(newInstance, "setNativeDumpMap", bool);
            a(newInstance, "setNativeDumpFds", bool);
            a(newInstance, "setJavaDumpAllThreads", bool);
            a(newInstance, "setAnrRethrow", bool);
            Class.forName("xcrash.XCrash").getDeclaredMethod("init", Context.class, newInstance.getClass()).invoke(null, context.getApplicationContext(), newInstance);
            p.a("CrashAnalysis", "XCrash init success");
        } catch (Throwable th) {
            p.a("CrashAnalysis", "XCrash init failed: " + th.toString());
        }
        this.v = gVar;
        this.u = new FileProcessor[]{new FileProcessor("java"), new FileProcessor(ExtraContacts.SimAccount.SIM_ANR), new FileProcessor("native")};
    }

    private static String a() {
        return k.a();
    }

    private void a(long j) {
        aa.d((ac.b() * 100) + j);
    }

    private void a(Object obj, String str, Object obj2) throws Exception {
        obj.getClass().getDeclaredMethod(str, obj2.getClass() == Boolean.class ? Boolean.TYPE : obj2.getClass()).invoke(obj, obj2);
    }

    private long b() {
        long c = aa.c();
        if (c == 0) {
            p.a("CrashAnalysis", "no ticket data found, return max count");
            return 10L;
        }
        long b = ac.b();
        if (c / 100 != b) {
            p.a("CrashAnalysis", "no today's ticket, return max count");
            return 10L;
        }
        long j = c - (b * 100);
        p.a("CrashAnalysis", "today's remain ticket is " + j);
        return j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long b(String str) {
        int i;
        int indexOf;
        if (TextUtils.isEmpty(str)) {
            return 0L;
        }
        try {
            int indexOf2 = str.indexOf("Crash time: '");
            if (indexOf2 == -1 || (indexOf = str.indexOf("'\n", (i = indexOf2 + 13))) == -1) {
                return 0L;
            }
            return b.a(str.substring(i, indexOf));
        } catch (Exception e) {
            p.b("CrashAnalysis", "getCrashTimeStamp error: " + e.toString());
            return 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String c(String str, String str2) {
        int i;
        int indexOf;
        String substring;
        int indexOf2;
        int indexOf3;
        if (TextUtils.isEmpty(str)) {
            return "uncategoried";
        }
        try {
            if (str2.equals(ExtraContacts.SimAccount.SIM_ANR)) {
                int indexOf4 = str.indexOf(" tid=1 ");
                if (indexOf4 == -1 || (indexOf2 = str.indexOf("\n  at ", indexOf4)) == -1 || (indexOf3 = str.indexOf(10, indexOf2 + 6)) == -1) {
                    return "uncategoried";
                }
                substring = str.substring(indexOf2 + 2, indexOf3);
            } else {
                int indexOf5 = str.indexOf("error reason:\n\t");
                if (indexOf5 == -1 || (indexOf = str.indexOf("\n\n", (i = indexOf5 + 15))) == -1) {
                    return "uncategoried";
                }
                substring = str.substring(i, indexOf);
            }
            return substring;
        } catch (Exception e) {
            p.b("CrashAnalysis", "getErrorReasonString error: " + e.toString());
            return "uncategoried";
        }
    }

    private List<File> c() {
        File[] listFiles = new File(a()).listFiles();
        if (listFiles == null) {
            p.a("CrashAnalysis", "this path does not denote a directory, or if an I/O error occurs.");
            return null;
        }
        List<File> asList = Arrays.asList(listFiles);
        Collections.sort(asList, new Comparator<File>() { // from class: com.xiaomi.onetrack.CrashAnalysis.2
            @Override // java.util.Comparator
            public int compare(File file, File file2) {
                return (int) (file.lastModified() - file2.lastModified());
            }
        });
        int size = asList.size();
        if (size > 20) {
            int i = size - 20;
            for (int i2 = 0; i2 < i; i2++) {
                k.a(asList.get(i2));
            }
            return asList.subList(i, size);
        }
        return asList;
    }

    public static String calculateJavaDigest(String str) {
        String[] split = str.replaceAll("\\t", "").split("\\n");
        StringBuilder sb = new StringBuilder();
        int min = Math.min(split.length, 20);
        for (int i = 0; i < min; i++) {
            split[i] = split[i].replaceAll("((java:)|(length=)|(index=)|(Index:)|(Size:))\\d+", "$1XX").replaceAll("\\$[0-9a-fA-F]{1,10}@[0-9a-fA-F]{1,10}|@[0-9a-fA-F]{1,10}|0x[0-9a-fA-F]{1,10}", "XX").replaceAll("\\d+[B,KB,MB]*", "");
        }
        for (int i2 = 0; i2 < min && (!split[i2].contains("...") || !split[i2].contains("more")); i2++) {
            sb.append(split[i2]);
            sb.append('\n');
        }
        return d.h(sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String d(String str, String str2) {
        int i;
        int indexOf;
        String substring;
        int indexOf2;
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            if (str2.equals(ExtraContacts.SimAccount.SIM_ANR)) {
                int indexOf3 = str.indexOf(" tid=1 ");
                if (indexOf3 == -1 || (indexOf2 = str.indexOf("\n\n", indexOf3)) == -1) {
                    return "";
                }
                substring = calculateJavaDigest(str.substring(indexOf3, indexOf2));
            } else {
                int indexOf4 = str.indexOf("backtrace feature id:\n\t");
                if (indexOf4 == -1 || (indexOf = str.indexOf("\n\n", (i = indexOf4 + 23))) == -1) {
                    return "";
                }
                substring = str.substring(i, indexOf);
            }
            return substring;
        } catch (Exception e) {
            p.b("CrashAnalysis", "calculateFeatureId error: " + e.toString());
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean d() {
        boolean z;
        Iterator<File> it;
        List<File> c = c();
        long b = b();
        if (c == null || c.size() <= 0) {
            z = false;
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            long b2 = aa.b();
            long j = MipubStat.STAT_EXPIRY_DATA;
            if (b2 > currentTimeMillis) {
                b2 = currentTimeMillis - MipubStat.STAT_EXPIRY_DATA;
            }
            Iterator<File> it2 = c.iterator();
            long j2 = 0;
            long j3 = 0;
            boolean z2 = false;
            while (it2.hasNext()) {
                File next = it2.next();
                long lastModified = next.lastModified();
                if (lastModified < currentTimeMillis - j || lastModified > currentTimeMillis) {
                    it = it2;
                    p.a("CrashAnalysis", "remove obsolete crash files: " + next.getName());
                    k.a(next);
                } else {
                    if (lastModified <= b2) {
                        p.a("CrashAnalysis", "found already reported crash file, ignore");
                    } else if (b > j2) {
                        FileProcessor[] fileProcessorArr = this.u;
                        int length = fileProcessorArr.length;
                        int i = 0;
                        while (i < length) {
                            Iterator<File> it3 = it2;
                            if (fileProcessorArr[i].a(next)) {
                                p.a("CrashAnalysis", "find crash file:" + next.getName());
                                b--;
                                if (j3 < lastModified) {
                                    j3 = lastModified;
                                }
                                z2 = true;
                            }
                            i++;
                            it2 = it3;
                        }
                    }
                    it = it2;
                }
                it2 = it;
                j = MipubStat.STAT_EXPIRY_DATA;
                j2 = 0;
            }
            if (j3 > j2) {
                aa.c(j3);
            }
            z = z2;
        }
        if (z) {
            a(b);
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e() {
        for (FileProcessor fileProcessor : this.u) {
            fileProcessor.a();
        }
    }

    public static boolean isSupport() {
        try {
            Class.forName("xcrash.XCrash");
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }

    public static void start(final Context context, final g gVar) {
        if (t.compareAndSet(false, true)) {
            i.a(new Runnable() { // from class: com.xiaomi.onetrack.CrashAnalysis.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        CrashAnalysis crashAnalysis = new CrashAnalysis(context, gVar);
                        if (crashAnalysis.d()) {
                            crashAnalysis.e();
                        } else {
                            p.a("CrashAnalysis", "no crash file found");
                        }
                    } catch (Throwable th) {
                        p.b("CrashAnalysis", "processCrash error: " + th.toString());
                    }
                }
            });
        } else {
            p.b("CrashAnalysis", "run method has been invoked more than once");
        }
    }
}
