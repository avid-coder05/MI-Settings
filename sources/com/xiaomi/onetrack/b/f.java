package com.xiaomi.onetrack.b;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class f implements Runnable {
    final /* synthetic */ b a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public f(b bVar) {
        this.a = bVar;
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x00c4, code lost:
    
        if (r1 == null) goto L20;
     */
    @Override // java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void run() {
        /*
            r14 = this;
            com.xiaomi.onetrack.b.b r0 = r14.a
            com.xiaomi.onetrack.b.b$a r0 = com.xiaomi.onetrack.b.b.a(r0)
            monitor-enter(r0)
            r1 = 0
            com.xiaomi.onetrack.b.b r14 = r14.a     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            com.xiaomi.onetrack.b.b$a r14 = com.xiaomi.onetrack.b.b.a(r14)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            android.database.sqlite.SQLiteDatabase r14 = r14.getWritableDatabase()     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            java.util.Calendar r2 = java.util.Calendar.getInstance()     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            long r3 = java.lang.System.currentTimeMillis()     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r2.setTimeInMillis(r3)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r3 = 6
            int r4 = r2.get(r3)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            int r4 = r4 + (-7)
            r2.set(r3, r4)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r3 = 11
            r10 = 0
            r2.set(r3, r10)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r3 = 12
            r2.set(r3, r10)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r3 = 13
            r2.set(r3, r10)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            long r2 = r2.getTimeInMillis()     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            java.lang.String r11 = "timestamp < ? "
            r12 = 1
            java.lang.String[] r13 = new java.lang.String[r12]     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            java.lang.String r2 = java.lang.Long.toString(r2)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r13[r10] = r2     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            java.lang.String r3 = "events"
            java.lang.String r2 = "timestamp"
            java.lang.String[] r4 = new java.lang.String[]{r2}     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r7 = 0
            r8 = 0
            java.lang.String r9 = "timestamp ASC"
            r2 = r14
            r5 = r11
            r6 = r13
            android.database.Cursor r1 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            int r2 = r1.getCount()     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            if (r2 == 0) goto L7e
            java.lang.String r2 = "events"
            int r14 = r14.delete(r2, r11, r13)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            java.lang.String r2 = "EventManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r3.<init>()     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            java.lang.String r4 = "*** deleted obsolete item count="
            r3.append(r4)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r3.append(r14)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            java.lang.String r14 = r3.toString()     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            com.xiaomi.onetrack.util.p.a(r2, r14)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
        L7e:
            com.xiaomi.onetrack.b.b r14 = com.xiaomi.onetrack.b.b.a()     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            long r2 = r14.c()     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r4 = 0
            int r14 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r14 != 0) goto L8d
            r10 = r12
        L8d:
            com.xiaomi.onetrack.a.m.a(r10)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            java.lang.String r14 = "EventManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r4.<init>()     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            java.lang.String r5 = "after delete obsolete record remains="
            r4.append(r5)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            r4.append(r2)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            java.lang.String r2 = r4.toString()     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
            com.xiaomi.onetrack.util.p.a(r14, r2)     // Catch: java.lang.Throwable -> Laa java.lang.Exception -> Lac
        La6:
            r1.close()     // Catch: java.lang.Throwable -> Lcf
            goto Lc7
        Laa:
            r14 = move-exception
            goto Lc9
        Lac:
            r14 = move-exception
            java.lang.String r2 = "EventManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Laa
            r3.<init>()     // Catch: java.lang.Throwable -> Laa
            java.lang.String r4 = "remove obsolete events failed with "
            r3.append(r4)     // Catch: java.lang.Throwable -> Laa
            r3.append(r14)     // Catch: java.lang.Throwable -> Laa
            java.lang.String r14 = r3.toString()     // Catch: java.lang.Throwable -> Laa
            com.xiaomi.onetrack.util.p.d(r2, r14)     // Catch: java.lang.Throwable -> Laa
            if (r1 == 0) goto Lc7
            goto La6
        Lc7:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Lcf
            return
        Lc9:
            if (r1 == 0) goto Lce
            r1.close()     // Catch: java.lang.Throwable -> Lcf
        Lce:
            throw r14     // Catch: java.lang.Throwable -> Lcf
        Lcf:
            r14 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Lcf
            throw r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.onetrack.b.f.run():void");
    }
}
