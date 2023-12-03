package com.xiaomi.onetrack.b;

import android.os.HandlerThread;
import android.text.TextUtils;
import com.xiaomi.onetrack.util.x;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import miui.yellowpage.Tag;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class p {
    private l g;

    /* loaded from: classes2.dex */
    private static class a {
        private static final p a = new p();
    }

    private p() {
        b();
    }

    public static p a() {
        return a.a;
    }

    private boolean a(JSONArray jSONArray) {
        try {
            String b = x.a().b();
            String jSONArray2 = jSONArray.toString();
            com.xiaomi.onetrack.util.p.a("UploaderEngine", " payload:" + jSONArray2);
            byte[] a2 = a(a(jSONArray2));
            com.xiaomi.onetrack.util.p.a("UploaderEngine", "before zip and encrypt, len=" + jSONArray2.length() + ", after=" + a2.length);
            String a3 = com.xiaomi.onetrack.f.b.a(b, a2);
            StringBuilder sb = new StringBuilder();
            sb.append("sendDataToServer response: ");
            sb.append(a3);
            com.xiaomi.onetrack.util.p.a("UploaderEngine", sb.toString());
            if (TextUtils.isEmpty(a3)) {
                return false;
            }
            return b(a3);
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("UploaderEngine", "Exception while uploading ", e);
            return false;
        }
    }

    private static byte[] a(String str) {
        GZIPOutputStream gZIPOutputStream;
        ByteArrayOutputStream byteArrayOutputStream;
        ByteArrayOutputStream byteArrayOutputStream2 = null;
        byte[] bArr = null;
        try {
            try {
                byteArrayOutputStream = new ByteArrayOutputStream(str.getBytes("UTF-8").length);
            } catch (Throwable th) {
                th = th;
            }
            try {
                gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                try {
                    gZIPOutputStream.write(str.getBytes("UTF-8"));
                    gZIPOutputStream.finish();
                    bArr = byteArrayOutputStream.toByteArray();
                } catch (Exception e) {
                    e = e;
                    com.xiaomi.onetrack.util.p.b("UploaderEngine", " zipData failed! " + e.toString());
                    com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream);
                    com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
                    return bArr;
                }
            } catch (Exception e2) {
                e = e2;
                gZIPOutputStream = null;
            } catch (Throwable th2) {
                th = th2;
                gZIPOutputStream = null;
                byteArrayOutputStream2 = byteArrayOutputStream;
                com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream2);
                com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            byteArrayOutputStream = null;
            gZIPOutputStream = null;
        } catch (Throwable th3) {
            th = th3;
            gZIPOutputStream = null;
            com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream2);
            com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
            throw th;
        }
        com.xiaomi.onetrack.util.m.a((OutputStream) byteArrayOutputStream);
        com.xiaomi.onetrack.util.m.a((OutputStream) gZIPOutputStream);
        return bArr;
    }

    private byte[] a(byte[] bArr) {
        if (bArr == null) {
            com.xiaomi.onetrack.util.p.b("UploaderEngine", "content is null");
            return null;
        }
        return com.xiaomi.onetrack.c.a.a(bArr, com.xiaomi.onetrack.c.c.a(com.xiaomi.onetrack.c.f.a().b()[0]));
    }

    private void b() {
        HandlerThread handlerThread = new HandlerThread("onetrack_uploader_worker");
        handlerThread.start();
        this.g = new l(handlerThread.getLooper());
    }

    private boolean b(String str) {
        boolean z = false;
        try {
            JSONObject jSONObject = new JSONObject(str);
            int optInt = jSONObject.optInt(Tag.TagWebService.CommonResult.RESULT_CODE);
            if (optInt == 0) {
                com.xiaomi.onetrack.util.p.a("UploaderEngine", "成功发送数据到服务端");
                com.xiaomi.onetrack.a.a.a(jSONObject);
                z = true;
            } else if (optInt == -3) {
                com.xiaomi.onetrack.util.p.b("UploaderEngine", "signature expired, will update");
                com.xiaomi.onetrack.c.f.a().c();
            } else {
                com.xiaomi.onetrack.util.p.b("UploaderEngine", "Error: status code=" + optInt);
            }
        } catch (Exception e) {
            com.xiaomi.onetrack.util.p.b("UploaderEngine", "parseUploadingResult exception ", e);
        }
        return z;
    }

    public synchronized void a(int i, boolean z) {
        l lVar = this.g;
        if (lVar != null) {
            lVar.a(i, z);
        } else {
            com.xiaomi.onetrack.util.p.b("UploaderEngine", "*** impossible, upload timer should not be null");
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0077, code lost:
    
        return true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean a(int r8) {
        /*
            r7 = this;
            java.lang.String r0 = "UploaderEngine"
            java.lang.String r1 = "即将读取数据库并上传数据"
            com.xiaomi.onetrack.util.p.a(r0, r1)
        L8:
            com.xiaomi.onetrack.b.b r1 = com.xiaomi.onetrack.b.b.a()
            com.xiaomi.onetrack.b.g r1 = r1.a(r8)
            r2 = 1
            if (r1 != 0) goto L29
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r1 = "满足条件的记录为空，即将返回, priority="
            r7.append(r1)
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            com.xiaomi.onetrack.util.p.a(r0, r7)
            return r2
        L29:
            java.util.ArrayList<java.lang.Long> r3 = r1.c
            org.json.JSONArray r4 = r1.a
            boolean r4 = r7.a(r4)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "upload success:"
            r5.append(r6)
            r5.append(r4)
            java.lang.String r5 = r5.toString()
            com.xiaomi.onetrack.util.p.a(r0, r5)
            if (r4 != 0) goto L4a
            r7 = 0
            return r7
        L4a:
            com.xiaomi.onetrack.b.b r4 = com.xiaomi.onetrack.b.b.a()
            int r3 = r4.a(r3)
            if (r3 != 0) goto L5f
            java.lang.Throwable r7 = new java.lang.Throwable
            r7.<init>()
            java.lang.String r8 = "delete DB failed!"
            com.xiaomi.onetrack.util.p.b(r0, r8, r7)
            goto L77
        L5f:
            boolean r1 = r1.d
            if (r1 == 0) goto L8
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r1 = "No more records for prio="
            r7.append(r1)
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            com.xiaomi.onetrack.util.p.a(r0, r7)
        L77:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.onetrack.b.p.a(int):boolean");
    }
}
