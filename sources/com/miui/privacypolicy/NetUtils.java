package com.miui.privacypolicy;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import miui.provider.Weather;
import miui.util.HashUtils;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class NetUtils {
    protected static final String MIUI_VERSION_NAME = getSystemProperty("ro.miui.ui.version.name", "unknown");

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.privacypolicy.NetUtils$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$privacypolicy$NetUtils$HttpMethod;

        static {
            int[] iArr = new int[HttpMethod.values().length];
            $SwitchMap$com$miui$privacypolicy$NetUtils$HttpMethod = iArr;
            try {
                iArr[HttpMethod.GET.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$privacypolicy$NetUtils$HttpMethod[HttpMethod.DELETE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$privacypolicy$NetUtils$HttpMethod[HttpMethod.POST.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$privacypolicy$NetUtils$HttpMethod[HttpMethod.PUT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* loaded from: classes2.dex */
    protected enum HttpMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public static class NameValuePair implements Comparable<NameValuePair> {
        private String name;
        private String value;

        NameValuePair(String str, String str2) {
            this.name = str;
            this.value = str2;
        }

        @Override // java.lang.Comparable
        public int compareTo(NameValuePair nameValuePair) {
            return this.name.compareTo(nameValuePair.name);
        }
    }

    private static void addBodyIfExists(HttpURLConnection httpURLConnection, JSONObject jSONObject) throws IOException {
        String jSONObject2 = jSONObject.toString();
        if (TextUtils.isEmpty(jSONObject2)) {
            return;
        }
        httpURLConnection.setDoOutput(true);
        httpURLConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
        DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
        dataOutputStream.writeBytes(jSONObject2);
        dataOutputStream.flush();
        dataOutputStream.close();
    }

    private static String encodeParameters(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (!z) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                sb.append('=');
                sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                z = false;
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + ((Object) sb), e);
        }
    }

    private static byte[] getBytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException unused) {
            return str.getBytes();
        }
    }

    private static String getMd5Digest(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HashUtils.MD5);
            messageDigest.update(getBytes(str));
            return String.format("%1$032X", new BigInteger(1, messageDigest.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getParamsSignature(Map<String, String> map, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            arrayList.add(new NameValuePair(entry.getKey(), entry.getValue()));
        }
        Collections.sort(arrayList);
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            NameValuePair nameValuePair = (NameValuePair) arrayList.get(i);
            sb.append(nameValuePair.name);
            sb.append("=");
            sb.append(nameValuePair.value);
        }
        sb.append(str);
        return getMd5Digest(new String(Base64.encodeToString(getBytes(sb.toString()), 2))).toUpperCase();
    }

    protected static String getSystemProperty(String str, String str2) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            return (String) cls.getMethod("get", String.class, String.class).invoke(cls, str, str2);
        } catch (Exception e) {
            Log.e("Privacy_NetUtil", "getSystemProperty error, ", e);
            return str2;
        }
    }

    private static HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(15000);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        return httpURLConnection;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String request(Map<String, String> map, String str, HttpMethod httpMethod, JSONObject jSONObject) {
        ByteArrayOutputStream byteArrayOutputStream;
        InputStream inputStream = null;
        try {
            if (httpMethod == HttpMethod.GET && map != null) {
                String encodeParameters = encodeParameters(map);
                str = str.contains("?") ? str.concat(encodeParameters) : str.concat("?").concat(encodeParameters);
            }
            HttpURLConnection openConnection = openConnection(new URL(str));
            setConnectionParametersForRequest(openConnection, httpMethod, map, jSONObject);
            if (openConnection.getResponseCode() != 200) {
                FileUtils.closeQuietly((InputStream) null);
                FileUtils.closeQuietly((OutputStream) null);
                return "";
            }
            InputStream inputStream2 = openConnection.getInputStream();
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
            } catch (Exception e) {
                inputStream = inputStream2;
                e = e;
                byteArrayOutputStream = null;
            } catch (Throwable th) {
                inputStream = inputStream2;
                th = th;
                byteArrayOutputStream = null;
            }
            try {
                byte[] bArr = new byte[4096];
                while (true) {
                    int read = inputStream2.read(bArr);
                    if (read == -1) {
                        String byteArrayOutputStream2 = byteArrayOutputStream.toString();
                        FileUtils.closeQuietly(inputStream2);
                        FileUtils.closeQuietly(byteArrayOutputStream);
                        return byteArrayOutputStream2;
                    }
                    byteArrayOutputStream.write(bArr, 0, read);
                }
            } catch (Exception e2) {
                inputStream = inputStream2;
                e = e2;
                try {
                    e.printStackTrace();
                    FileUtils.closeQuietly(inputStream);
                    FileUtils.closeQuietly(byteArrayOutputStream);
                    return "";
                } catch (Throwable th2) {
                    th = th2;
                    FileUtils.closeQuietly(inputStream);
                    FileUtils.closeQuietly(byteArrayOutputStream);
                    throw th;
                }
            } catch (Throwable th3) {
                inputStream = inputStream2;
                th = th3;
                FileUtils.closeQuietly(inputStream);
                FileUtils.closeQuietly(byteArrayOutputStream);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            byteArrayOutputStream = null;
        } catch (Throwable th4) {
            th = th4;
            byteArrayOutputStream = null;
        }
    }

    private static void setConnectionParametersForRequest(HttpURLConnection httpURLConnection, HttpMethod httpMethod, Map<String, String> map, JSONObject jSONObject) throws IOException {
        httpURLConnection.addRequestProperty("sign", getParamsSignature(map, "2dcd9s0c-ad3f-2fas-0l3a-abzo301jd0s9"));
        httpURLConnection.addRequestProperty(Weather.WeatherBaseColumns.TIMESTAMP, map.get(Weather.WeatherBaseColumns.TIMESTAMP));
        httpURLConnection.addRequestProperty("source", "sdk");
        int i = AnonymousClass1.$SwitchMap$com$miui$privacypolicy$NetUtils$HttpMethod[httpMethod.ordinal()];
        if (i == 1) {
            httpURLConnection.setRequestMethod("GET");
        } else if (i == 2) {
            httpURLConnection.setRequestMethod("DELETE");
        } else if (i == 3) {
            httpURLConnection.setRequestMethod("POST");
            addBodyIfExists(httpURLConnection, jSONObject);
        } else if (i != 4) {
            throw new IllegalStateException("Unknown method type.");
        } else {
            httpURLConnection.setRequestMethod("PUT");
        }
    }
}
