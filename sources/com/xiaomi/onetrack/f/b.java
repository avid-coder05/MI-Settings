package com.xiaomi.onetrack.f;

import android.text.TextUtils;
import com.xiaomi.onetrack.c.d;
import com.xiaomi.onetrack.c.f;
import com.xiaomi.onetrack.e.a;
import com.xiaomi.onetrack.util.m;
import com.xiaomi.onetrack.util.p;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/* loaded from: classes2.dex */
public class b {
    private static String h = "HttpUtil";

    private static String a(String str, String str2, Map<String, String> map, boolean z) {
        String a;
        OutputStream outputStream;
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        String str3;
        InputStream inputStream2 = null;
        if (map == null) {
            a = null;
        } else {
            try {
                a = a(map, z);
            } catch (Exception e) {
                e = e;
                outputStream = null;
                httpURLConnection = null;
                inputStream = null;
                p.b(h, "HttpUtils POST 上传异常", e);
                m.a(inputStream);
                m.a(outputStream);
                m.a(httpURLConnection);
                return null;
            } catch (Throwable th) {
                th = th;
                outputStream = null;
                httpURLConnection = null;
                m.a(inputStream2);
                m.a(outputStream);
                m.a(httpURLConnection);
                throw th;
            }
        }
        if (!"GET".equals(str) || a == null) {
            str3 = str2;
        } else {
            str3 = str2 + "? " + a;
        }
        httpURLConnection = (HttpURLConnection) new URL(str3).openConnection();
        try {
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(15000);
            try {
                try {
                    if ("GET".equals(str)) {
                        httpURLConnection.setRequestMethod("GET");
                    } else if ("POST".equals(str) && a != null) {
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        httpURLConnection.setDoOutput(true);
                        byte[] bytes = a.getBytes("UTF-8");
                        outputStream = httpURLConnection.getOutputStream();
                        try {
                            outputStream.write(bytes, 0, bytes.length);
                            outputStream.flush();
                            int responseCode = httpURLConnection.getResponseCode();
                            inputStream = httpURLConnection.getInputStream();
                            byte[] b = m.b(inputStream);
                            p.a(h, String.format("HttpUtils POST 上传成功 url: %s, code: %s", str2, Integer.valueOf(responseCode)));
                            String str4 = new String(b, "UTF-8");
                            m.a(inputStream);
                            m.a(outputStream);
                            m.a(httpURLConnection);
                            return str4;
                        } catch (Exception e2) {
                            e = e2;
                            inputStream = null;
                            p.b(h, "HttpUtils POST 上传异常", e);
                            m.a(inputStream);
                            m.a(outputStream);
                            m.a(httpURLConnection);
                            return null;
                        } catch (Throwable th2) {
                            th = th2;
                            m.a(inputStream2);
                            m.a(outputStream);
                            m.a(httpURLConnection);
                            throw th;
                        }
                    }
                    byte[] b2 = m.b(inputStream);
                    p.a(h, String.format("HttpUtils POST 上传成功 url: %s, code: %s", str2, Integer.valueOf(responseCode)));
                    String str42 = new String(b2, "UTF-8");
                    m.a(inputStream);
                    m.a(outputStream);
                    m.a(httpURLConnection);
                    return str42;
                } catch (Exception e3) {
                    e = e3;
                    p.b(h, "HttpUtils POST 上传异常", e);
                    m.a(inputStream);
                    m.a(outputStream);
                    m.a(httpURLConnection);
                    return null;
                }
            } catch (Throwable th3) {
                th = th3;
                inputStream2 = inputStream;
                m.a(inputStream2);
                m.a(outputStream);
                m.a(httpURLConnection);
                throw th;
            }
            outputStream = null;
            int responseCode2 = httpURLConnection.getResponseCode();
            inputStream = httpURLConnection.getInputStream();
        } catch (Exception e4) {
            e = e4;
            outputStream = null;
            inputStream = null;
        } catch (Throwable th4) {
            th = th4;
            outputStream = null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static String a(String str, byte[] bArr) throws IOException {
        HttpURLConnection httpURLConnection;
        OutputStream outputStream;
        InputStream inputStream;
        p.a(h, "doPost url=" + str + ", len=" + bArr.length);
        InputStream inputStream2 = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        } catch (IOException e) {
            e = e;
            httpURLConnection = null;
            outputStream = null;
        } catch (Throwable th) {
            th = th;
            httpURLConnection = null;
            outputStream = null;
        }
        try {
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
            String str2 = f.a().b()[1];
            httpURLConnection.setRequestProperty("OT_SID", str2);
            httpURLConnection.setRequestProperty("OT_ts", Long.toString(System.currentTimeMillis()));
            httpURLConnection.setRequestProperty("OT_net", c.a(a.b()).toString());
            httpURLConnection.setRequestProperty("OT_sender", a.e());
            httpURLConnection.setRequestProperty("OT_protocol", "3.0");
            p.a(h, "sid:" + str2);
            outputStream = httpURLConnection.getOutputStream();
        } catch (IOException e2) {
            e = e2;
            outputStream = 0;
            inputStream = outputStream;
            p.b(h, String.format("HttpUtils POST 上传失败, url: %s, error: %s", str, e.getMessage()));
            m.a(inputStream);
            m.a(outputStream);
            m.a(httpURLConnection);
            return null;
        } catch (Throwable th2) {
            th = th2;
            outputStream = null;
        }
        try {
            outputStream.write(bArr, 0, bArr.length);
            outputStream.flush();
            int responseCode = httpURLConnection.getResponseCode();
            inputStream = httpURLConnection.getInputStream();
            try {
                try {
                    byte[] b = m.b(inputStream);
                    p.a(h, String.format("HttpUtils POST 上传成功 url: %s, code: %s", str, Integer.valueOf(responseCode)));
                    String str3 = new String(b, "UTF-8");
                    m.a(inputStream);
                    m.a(outputStream);
                    m.a(httpURLConnection);
                    return str3;
                } catch (IOException e3) {
                    e = e3;
                    p.b(h, String.format("HttpUtils POST 上传失败, url: %s, error: %s", str, e.getMessage()));
                    m.a(inputStream);
                    m.a(outputStream);
                    m.a(httpURLConnection);
                    return null;
                }
            } catch (Throwable th3) {
                th = th3;
                inputStream2 = inputStream;
                m.a(inputStream2);
                m.a(outputStream);
                m.a(httpURLConnection);
                throw th;
            }
        } catch (IOException e4) {
            e = e4;
            inputStream = null;
        } catch (Throwable th4) {
            th = th4;
            m.a(inputStream2);
            m.a(outputStream);
            m.a(httpURLConnection);
            throw th;
        }
    }

    public static String a(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        if (map != null) {
            ArrayList<String> arrayList = new ArrayList(map.keySet());
            Collections.sort(arrayList);
            for (String str : arrayList) {
                if (!TextUtils.isEmpty(str)) {
                    sb.append(str);
                    sb.append(map.get(str));
                }
            }
        }
        sb.append("miui_sdkconfig_jafej!@#)(*e@!#");
        return d.c(sb.toString());
    }

    private static String a(Map<String, String> map, boolean z) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    sb.append("=");
                    sb.append(URLEncoder.encode(entry.getValue() == null ? "null" : entry.getValue(), "UTF-8"));
                }
            } catch (UnsupportedEncodingException unused) {
                p.b(h, "format params failed");
            }
        }
        if (z) {
            String a = a(map);
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode("sign", "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(a, "UTF-8"));
        }
        return sb.toString();
    }

    public static String b(String str, Map<String, String> map, boolean z) throws IOException {
        return a("POST", str, map, z);
    }
}
