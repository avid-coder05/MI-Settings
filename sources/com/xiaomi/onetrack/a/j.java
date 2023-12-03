package com.xiaomi.onetrack.a;

import java.util.concurrent.Callable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class j implements Callable<k> {
    final /* synthetic */ String a;
    final /* synthetic */ g b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public j(g gVar, String str) {
        this.b = gVar;
        this.a = str;
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x008b, code lost:
    
        if (r12 == null) goto L25;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0098 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r12v0, types: [com.xiaomi.onetrack.a.j] */
    /* JADX WARN: Type inference failed for: r12v2 */
    /* JADX WARN: Type inference failed for: r12v4, types: [android.database.Cursor] */
    @Override // java.util.concurrent.Callable
    /* renamed from: a  reason: merged with bridge method [inline-methods] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.xiaomi.onetrack.a.k call() throws java.lang.Exception {
        /*
            r12 = this;
            java.lang.String r0 = "getConfig  cursor.close"
            java.lang.String r1 = "ConfigDbManager"
            r2 = 0
            java.lang.String r6 = "app_id=?"
            com.xiaomi.onetrack.a.g r3 = r12.b     // Catch: java.lang.Throwable -> L7d java.lang.Exception -> L82
            com.xiaomi.onetrack.a.f r3 = com.xiaomi.onetrack.a.g.a(r3)     // Catch: java.lang.Throwable -> L7d java.lang.Exception -> L82
            android.database.sqlite.SQLiteDatabase r3 = r3.getWritableDatabase()     // Catch: java.lang.Throwable -> L7d java.lang.Exception -> L82
            java.lang.String r4 = "events_cloud"
            r5 = 0
            r7 = 1
            java.lang.String[] r7 = new java.lang.String[r7]     // Catch: java.lang.Throwable -> L7d java.lang.Exception -> L82
            r8 = 0
            java.lang.String r12 = r12.a     // Catch: java.lang.Throwable -> L7d java.lang.Exception -> L82
            r7[r8] = r12     // Catch: java.lang.Throwable -> L7d java.lang.Exception -> L82
            r8 = 0
            r9 = 0
            r10 = 0
            android.database.Cursor r12 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch: java.lang.Throwable -> L7d java.lang.Exception -> L82
            java.lang.String r3 = "app_id"
            int r3 = r12.getColumnIndex(r3)     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            java.lang.String r4 = "cloud_data"
            int r4 = r12.getColumnIndex(r4)     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            java.lang.String r5 = "data_hash"
            int r5 = r12.getColumnIndex(r5)     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            java.lang.String r6 = "timestamp"
            int r6 = r12.getColumnIndex(r6)     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            boolean r7 = r12.moveToNext()     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            if (r7 == 0) goto L8d
            com.xiaomi.onetrack.a.k r7 = new com.xiaomi.onetrack.a.k     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            r7.<init>()     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            java.lang.String r3 = r12.getString(r3)     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            r7.a = r3     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            java.lang.String r3 = r12.getString(r4)     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            if (r4 != 0) goto L5e
            org.json.JSONObject r4 = new org.json.JSONObject     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            r4.<init>(r3)     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            r7.e = r4     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
        L5e:
            org.json.JSONObject r3 = r7.e     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            int r3 = com.xiaomi.onetrack.a.g.a(r3)     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            long r3 = (long) r3     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            r7.b = r3     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            java.lang.String r3 = r12.getString(r5)     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            r7.d = r3     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            long r3 = r12.getLong(r6)     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            r7.c = r3     // Catch: java.lang.Exception -> L7b java.lang.Throwable -> L95
            r12.close()     // Catch: java.lang.Exception -> L77
            goto L7a
        L77:
            com.xiaomi.onetrack.util.p.a(r1, r0)
        L7a:
            return r7
        L7b:
            r3 = move-exception
            goto L84
        L7d:
            r12 = move-exception
            r11 = r2
            r2 = r12
            r12 = r11
            goto L96
        L82:
            r3 = move-exception
            r12 = r2
        L84:
            java.lang.String r3 = r3.getMessage()     // Catch: java.lang.Throwable -> L95
            com.xiaomi.onetrack.util.p.a(r1, r3)     // Catch: java.lang.Throwable -> L95
            if (r12 == 0) goto L94
        L8d:
            r12.close()     // Catch: java.lang.Exception -> L91
            goto L94
        L91:
            com.xiaomi.onetrack.util.p.a(r1, r0)
        L94:
            return r2
        L95:
            r2 = move-exception
        L96:
            if (r12 == 0) goto L9f
            r12.close()     // Catch: java.lang.Exception -> L9c
            goto L9f
        L9c:
            com.xiaomi.onetrack.util.p.a(r1, r0)
        L9f:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.onetrack.a.j.call():com.xiaomi.onetrack.a.k");
    }
}
