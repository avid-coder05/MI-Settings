package com.xiaomi.onetrack.api;

import android.os.Process;
import com.xiaomi.onetrack.util.b;
import com.xiaomi.onetrack.util.p;
import java.lang.Thread;
import java.util.Date;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/* loaded from: classes2.dex */
public class e implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler b;
    private final Date g = new Date();
    private int i = 50;
    private int j = 50;
    private int k = 200;
    private boolean l = true;
    private boolean m = true;

    private String a(Date date, Thread thread, String str) {
        return b.a(this.g, date, "java", com.xiaomi.onetrack.e.a.e(), b.a(com.xiaomi.onetrack.e.a.b())) + "pid: " + Process.myPid() + ", tid: " + Process.myTid() + ", name: " + thread.getName() + "  >>> " + b.a(com.xiaomi.onetrack.e.a.b(), Process.myPid()) + " <<<\n\njava stacktrace:\n" + str + "\n";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't wrap try/catch for region: R(14:1|(2:2|3)|(3:5|6|(1:8))|10|11|12|13|14|15|16|17|18|(15:20|21|23|24|(1:26)|27|(1:46)|33|(1:35)|36|(1:38)|39|40|41|42)(1:68)|(1:(0))) */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0088, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x008a, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x008c, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x008f, code lost:
    
        r9 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0090, code lost:
    
        com.xiaomi.onetrack.util.p.b("OneTrackExceptionHandler", "JavaCrashHandler getEmergency failed", r0);
        r0 = null;
     */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0098 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:90:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void a(java.lang.Thread r16, java.lang.Throwable r17) {
        /*
            Method dump skipped, instructions count: 353
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.onetrack.api.e.a(java.lang.Thread, java.lang.Throwable):void");
    }

    public void a() {
        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (defaultUncaughtExceptionHandler instanceof e) {
            return;
        }
        this.b = defaultUncaughtExceptionHandler;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        p.a("OneTrackExceptionHandler", "uncaughtException start");
        FutureTask futureTask = new FutureTask(new f(this, thread, th), null);
        com.xiaomi.onetrack.b.a.a(futureTask);
        try {
            futureTask.get(2L, TimeUnit.SECONDS);
        } catch (Exception e) {
            p.b("OneTrackExceptionHandler", "handleException timeout :" + e.getMessage());
        }
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.b;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th);
        }
    }
}
