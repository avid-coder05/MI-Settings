package com.xiaomi.onetrack.a;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.onetrack.util.p;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import miui.provider.Weather;
import miui.yellowpage.Tag;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class g {
    private f b;
    private ConcurrentHashMap<String, k> c;
    private ConcurrentHashMap<String, Boolean> d;

    /* loaded from: classes2.dex */
    private static class a {
        private static final g a = new g(null);
    }

    private g() {
        this.c = new ConcurrentHashMap<>();
        this.d = new ConcurrentHashMap<>();
        this.b = new f(com.xiaomi.onetrack.e.a.a());
    }

    /* synthetic */ g(h hVar) {
        this();
    }

    public static g a() {
        return a.a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int b(JSONObject jSONObject) {
        try {
            int optInt = jSONObject.optInt("sample", 100);
            if (optInt < 0 || optInt > 100) {
                return 100;
            }
            return optInt;
        } catch (Exception e) {
            p.a("ConfigDbManager", "getCommonSample Exception:" + e.getMessage());
            return 100;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(ArrayList<k> arrayList) {
        StringBuilder sb;
        SQLiteDatabase writableDatabase;
        SQLiteDatabase sQLiteDatabase = null;
        try {
            try {
                writableDatabase = this.b.getWritableDatabase();
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            writableDatabase.beginTransaction();
            Iterator<k> it = arrayList.iterator();
            while (it.hasNext()) {
                k next = it.next();
                ContentValues contentValues = new ContentValues();
                contentValues.put("app_id", next.a);
                contentValues.put(Weather.WeatherBaseColumns.TIMESTAMP, Long.valueOf(next.c));
                JSONObject jSONObject = next.e;
                if (jSONObject != null) {
                    contentValues.put("cloud_data", jSONObject.toString());
                }
                String str = next.d;
                if (str != null) {
                    contentValues.put("data_hash", str);
                }
                if (DatabaseUtils.queryNumEntries(writableDatabase, "events_cloud", "app_id=?", new String[]{next.a}) > 0) {
                    p.a("ConfigDbManager", "database updated, row: " + writableDatabase.update("events_cloud", contentValues, "app_id=?", new String[]{next.a}));
                } else {
                    p.a("ConfigDbManager", "database inserted, row: " + writableDatabase.insert("events_cloud", null, contentValues));
                }
                this.d.put(next.a, Boolean.TRUE);
            }
            writableDatabase.setTransactionSuccessful();
            try {
                writableDatabase.endTransaction();
            } catch (Exception e2) {
                e = e2;
                sb = new StringBuilder();
                sb.append("Exception while endTransaction:");
                sb.append(e);
                p.b("ConfigDbManager", sb.toString());
            }
        } catch (Exception e3) {
            e = e3;
            sQLiteDatabase = writableDatabase;
            p.b("ConfigDbManager", "updateToDb error: ", e);
            if (sQLiteDatabase != null) {
                try {
                    sQLiteDatabase.endTransaction();
                } catch (Exception e4) {
                    e = e4;
                    sb = new StringBuilder();
                    sb.append("Exception while endTransaction:");
                    sb.append(e);
                    p.b("ConfigDbManager", sb.toString());
                }
            }
        } catch (Throwable th2) {
            th = th2;
            sQLiteDatabase = writableDatabase;
            if (sQLiteDatabase != null) {
                try {
                    sQLiteDatabase.endTransaction();
                } catch (Exception e5) {
                    p.b("ConfigDbManager", "Exception while endTransaction:" + e5);
                }
            }
            throw th;
        }
    }

    private JSONObject c(String str, String str2) {
        JSONObject jSONObject;
        JSONArray optJSONArray;
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                if (this.c.get(str) == null || (this.d.containsKey(str) && this.d.get(str).booleanValue())) {
                    b(str);
                }
                k kVar = this.c.get(str);
                if (kVar != null && (jSONObject = kVar.e) != null && (optJSONArray = jSONObject.optJSONArray("events")) != null) {
                    for (int i = 0; i < optJSONArray.length(); i++) {
                        JSONObject jSONObject2 = optJSONArray.getJSONObject(i);
                        if (TextUtils.equals(str2, jSONObject2.optString(Tag.TagWebService.CommonResult.RESULT_TYPE_EVENT))) {
                            if (p.a) {
                                p.a("ConfigDbManager", "getEventConfig:" + jSONObject2.toString());
                            }
                            return jSONObject2;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ConfigDbManager", "getEventConfig error: " + e.toString());
            }
        }
        return null;
    }

    public int a(String str, String str2, String str3, int i) {
        try {
            JSONObject c = c(str, str2);
            if (c == null) {
                p.a("ConfigDbManager", "config not available, use default value");
                return i;
            }
            return c.getInt(str3);
        } catch (Exception e) {
            p.b("ConfigDbManager", "getInt: " + e.toString());
            return i;
        }
    }

    public void a(ArrayList<k> arrayList) {
        com.xiaomi.onetrack.b.a.a(new h(this, arrayList));
    }

    public boolean a(String str, String str2) {
        JSONObject jSONObject;
        try {
            k e = e(str);
            if (e == null || (jSONObject = e.e) == null || !jSONObject.has(str2)) {
                return false;
            }
            return e.e.optBoolean(str2);
        } catch (Exception e2) {
            p.b("ConfigDbManager", "getAppLevelBoolean" + e2.toString());
            return false;
        }
    }

    public boolean a(String str, String str2, String str3, boolean z) {
        try {
            JSONObject c = c(str, str2);
            if (c == null) {
                p.a("ConfigDbManager", "config not available, use default value");
                return z;
            }
            return c.getBoolean(str3);
        } catch (Exception e) {
            p.b("ConfigDbManager", "getBoolean: " + e.toString());
            return z;
        }
    }

    public long b(String str, String str2) {
        k kVar;
        if (TextUtils.isEmpty(str)) {
            return 100L;
        }
        try {
            if (this.c.get(str) == null) {
                b(str);
            }
            if (this.c.get(str) != null) {
                int a2 = a(str, str2, "sample", -1);
                if (a2 != -1 || (kVar = this.c.get(str)) == null) {
                    p.a("ConfigDbManager", "will return event sample " + a2);
                    return a2;
                }
                p.a("ConfigDbManager", "will return common sample " + kVar.b);
                return kVar.b;
            }
        } catch (Exception e) {
            p.b("ConfigDbManager", "getAppEventSample" + e.toString());
        }
        p.a("ConfigDbManager", "will return def sample");
        return 100L;
    }

    public void b(String str) {
        FutureTask futureTask = new FutureTask(new j(this, str));
        com.xiaomi.onetrack.b.a.a(futureTask);
        try {
            k kVar = (k) futureTask.get(5L, TimeUnit.SECONDS);
            if (kVar != null) {
                this.c.put(str, kVar);
                this.d.put(str, Boolean.FALSE);
                if (p.a) {
                    p.a("ConfigDbManager", "getConfig   appId :" + str + " config: " + kVar.toString());
                }
            }
        } catch (Exception e) {
            p.b("ConfigDbManager", "getConfig error: " + e.toString());
        }
    }

    public String c(String str) {
        k e = e(str);
        return e != null ? e.d : "";
    }

    public int d(String str) {
        JSONObject jSONObject;
        k e = e(str);
        if (e == null || (jSONObject = e.e) == null) {
            return 0;
        }
        return jSONObject.optInt("version");
    }

    public k e(String str) {
        p.a("ConfigDbManager", "getAppConfigData start, appId: " + str);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        p.a("ConfigDbManager", "mUpdated: " + this.d + ",ruleDataMap.get(appId): " + this.c.get(str));
        try {
            if (this.c.get(str) == null || (this.d.containsKey(str) && this.d.get(str).booleanValue())) {
                b(str);
            }
        } catch (Exception e) {
            p.b("ConfigDbManager", "getConfig error: " + e.getMessage());
        }
        return this.c.get(str);
    }
}
