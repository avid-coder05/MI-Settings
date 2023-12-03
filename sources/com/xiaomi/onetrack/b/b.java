package com.xiaomi.onetrack.b;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.settings.search.FunctionColumns;
import com.xiaomi.onetrack.util.aa;
import java.util.ArrayList;
import miui.provider.Weather;

/* loaded from: classes2.dex */
public class b {
    private static b h;
    private static BroadcastReceiver j = new c();
    private a i = new a(com.xiaomi.onetrack.e.a.a());

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class a extends SQLiteOpenHelper {
        public a(Context context) {
            super(context, "onetrack", (SQLiteDatabase.CursorFactory) null, 1);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE events (_id INTEGER PRIMARY KEY AUTOINCREMENT,appid TEXT,package TEXT,event_name TEXT,priority INTEGER,data BLOB,timestamp INTEGER)");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    private b() {
        b();
    }

    public static b a() {
        if (h == null) {
            a(com.xiaomi.onetrack.e.a.b());
        }
        return h;
    }

    public static String a(byte[] bArr) {
        return new String(com.xiaomi.onetrack.c.a.b(bArr, com.xiaomi.onetrack.c.d.a(com.xiaomi.onetrack.c.c.a(), true).getBytes()));
    }

    public static void a(Context context) {
        if (h == null) {
            synchronized (b.class) {
                if (h == null) {
                    h = new b();
                }
            }
        }
        b(context);
    }

    public static byte[] a(String str) {
        return com.xiaomi.onetrack.c.a.a(str.getBytes(), com.xiaomi.onetrack.c.d.a(com.xiaomi.onetrack.c.c.a(), true).getBytes());
    }

    private static void b(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        context.registerReceiver(j, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(com.xiaomi.onetrack.e.b bVar) {
        synchronized (this.i) {
            if (!bVar.h()) {
                com.xiaomi.onetrack.util.p.c("EventManager", "addEventToDatabase event is inValid, event:" + bVar.d());
                return;
            }
            SQLiteDatabase writableDatabase = this.i.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("appid", bVar.b());
            contentValues.put(FunctionColumns.PACKAGE, bVar.c());
            contentValues.put("event_name", bVar.d());
            contentValues.put("priority", Integer.valueOf(bVar.e()));
            contentValues.put(Weather.WeatherBaseColumns.TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
            byte[] a2 = a(bVar.f().toString());
            if (a2.length > 204800) {
                com.xiaomi.onetrack.util.p.b("EventManager", "Too large data, discard ***");
                return;
            }
            contentValues.put("data", a2);
            long insert = writableDatabase.insert("events", null, contentValues);
            com.xiaomi.onetrack.util.p.a("EventManager", "DB-Thread: EventManager.addEventToDatabase , row=" + insert);
            if (insert != -1) {
                if (com.xiaomi.onetrack.util.p.a) {
                    com.xiaomi.onetrack.util.p.a("EventManager", "添加后，DB 中事件个数为 " + c());
                }
                long currentTimeMillis = System.currentTimeMillis();
                if ("onetrack_active".equals(bVar.d())) {
                    aa.a(currentTimeMillis);
                }
                com.xiaomi.onetrack.a.m.a(false);
            }
        }
    }

    private void d() {
        try {
            this.i.getWritableDatabase().delete("events", null, null);
            com.xiaomi.onetrack.util.p.a("EventManager", "delete table events");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int a(ArrayList<Long> arrayList) {
        synchronized (this.i) {
            if (arrayList != null) {
                if (arrayList.size() != 0) {
                    try {
                        SQLiteDatabase writableDatabase = this.i.getWritableDatabase();
                        boolean z = true;
                        StringBuilder sb = new StringBuilder(((Long.toString(arrayList.get(0).longValue()).length() + 1) * arrayList.size()) + 16);
                        sb.append("_id");
                        sb.append(" in (");
                        sb.append(arrayList.get(0));
                        int size = arrayList.size();
                        for (int i = 1; i < size; i++) {
                            sb.append(",");
                            sb.append(arrayList.get(i));
                        }
                        sb.append(")");
                        int delete = writableDatabase.delete("events", sb.toString(), null);
                        com.xiaomi.onetrack.util.p.a("EventManager", "deleted events count " + delete);
                        long c = a().c();
                        if (c != 0) {
                            z = false;
                        }
                        com.xiaomi.onetrack.a.m.a(z);
                        com.xiaomi.onetrack.util.p.a("EventManager", "after delete DB record remains=" + c);
                        return delete;
                    } catch (Exception e) {
                        com.xiaomi.onetrack.util.p.b("EventManager", "e=" + e);
                        return 0;
                    }
                }
            }
            return 0;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:0x00e2, code lost:
    
        if (r10.size() <= 0) goto L51;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00e8, code lost:
    
        if (r3.isAfterLast() == false) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00ea, code lost:
    
        com.xiaomi.onetrack.util.p.a("EventManager", "cursor isAfterLast");
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00ef, code lost:
    
        r13 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00f5, code lost:
    
        if (r3.getInt(r6) <= r22) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00f8, code lost:
    
        r13 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00f9, code lost:
    
        r0 = new com.xiaomi.onetrack.b.g(r9, r15, r10, r13);
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00fe, code lost:
    
        r3.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0101, code lost:
    
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x010f, code lost:
    
        if (r3 == null) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0111, code lost:
    
        r3.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x011f, code lost:
    
        if (r3 == null) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0122, code lost:
    
        return null;
     */
    /* JADX WARN: Not initialized variable reg: 3, insn: 0x0124: MOVE (r11 I:??[OBJECT, ARRAY]) = (r3 I:??[OBJECT, ARRAY]), block:B:59:0x0124 */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0127  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.xiaomi.onetrack.b.g a(int r22) {
        /*
            Method dump skipped, instructions count: 299
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.onetrack.b.b.a(int):com.xiaomi.onetrack.b.g");
    }

    public synchronized void a(com.xiaomi.onetrack.e.b bVar) {
        com.xiaomi.onetrack.b.a.a(new e(this, bVar));
    }

    public void b() {
        com.xiaomi.onetrack.b.a.a(new f(this));
    }

    public long c() {
        return DatabaseUtils.queryNumEntries(this.i.getReadableDatabase(), "events");
    }
}
