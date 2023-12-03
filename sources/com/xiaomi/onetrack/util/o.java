package com.xiaomi.onetrack.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import com.xiaomi.onetrack.e.a;
import java.util.UUID;

/* loaded from: classes2.dex */
public class o {
    private static final String a = "o";
    private static volatile o f;
    private static String g;
    private static String j;
    private boolean k = false;
    private final Context h = a.a();
    private final Context i = a.b();

    private o() {
        j = a.e();
    }

    public static o a() {
        if (f == null) {
            synchronized (o.class) {
                if (f == null) {
                    f = new o();
                }
            }
        }
        return f;
    }

    private void b(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            Uri parse = Uri.parse("content://com.miui.analytics.OneTrackProvider/insId");
            ContentValues contentValues = new ContentValues();
            contentValues.put(j, str);
            this.i.getContentResolver().insert(parse, contentValues);
        } catch (Exception e) {
            aa.e(str);
            p.a(a, "setRemoteCacheInstanceId e", e);
        }
    }

    private String c() {
        String str = null;
        try {
            Uri.Builder buildUpon = Uri.parse("content://com.miui.analytics.OneTrackProvider/insId").buildUpon();
            buildUpon.appendQueryParameter("pkg", j);
            buildUpon.appendQueryParameter("sign", com.xiaomi.onetrack.c.a.a("insId" + j));
            Cursor query = this.i.getContentResolver().query(buildUpon.build(), null, null, null, null);
            if (query != null) {
                while (query.moveToNext()) {
                    str = query.getString(0);
                }
                query.close();
            }
        } catch (Exception e) {
            p.a(a, "getRemoteCacheInstanceId e", e);
        }
        return str;
    }

    private String d() {
        String a2 = aa.a(this.h);
        if (TextUtils.isEmpty(a2)) {
            return aa.m();
        }
        aa.e(a2);
        return a2;
    }

    public void a(Boolean bool) {
        this.k = bool.booleanValue();
    }

    public void a(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        g = str;
        if (this.k) {
            b(str);
        }
        aa.e(g);
    }

    public String b() {
        String d;
        if (TextUtils.isEmpty(g)) {
            if (this.k) {
                d = c();
                String d2 = d();
                if (TextUtils.isEmpty(d) && !TextUtils.isEmpty(d2)) {
                    b(d2);
                    d = d2;
                } else if (!TextUtils.isEmpty(d) && TextUtils.isEmpty(d2)) {
                    aa.e(d);
                }
            } else {
                d = d();
            }
            if (TextUtils.isEmpty(d)) {
                String uuid = UUID.randomUUID().toString();
                g = uuid;
                if (this.k) {
                    b(uuid);
                }
                aa.e(g);
            } else {
                g = d;
            }
            return g;
        }
        return g;
    }
}
