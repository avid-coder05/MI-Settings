package com.android.settings.emergency.util;

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
import java.util.HashMap;
import java.util.Map;
import miui.util.HashUtils;
import miuix.core.util.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NetworkUtils {
    private static void addBodyIfExists(HttpURLConnection httpURLConnection, Map<String, String> map) throws IOException {
        byte[] bArr;
        if (map == null || map.size() <= 0) {
            bArr = null;
        } else {
            String encodeParameters = encodeParameters(map);
            Log.w("SOS-NetworkUtils", " post body : " + encodeParameters);
            bArr = encodeParameters.getBytes();
        }
        if (bArr != null) {
            httpURLConnection.setDoOutput(true);
            httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.write(bArr);
            dataOutputStream.close();
        }
    }

    public static String buildBase64JsonString(Map<String, String> map) {
        JSONObject jSONObject = new JSONObject();
        try {
            for (String str : map.keySet()) {
                jSONObject.put(str, map.get(str));
            }
        } catch (JSONException e) {
            Log.e("SOS-NetworkUtils", "JSONException when buildBase64JsonString : ", e);
        }
        return Base64.encodeToString(getBytes(jSONObject.toString()), 2);
    }

    public static String buildURLParam(Map<String, String> map) {
        String str = "";
        if (map == null || map.isEmpty()) {
            return "";
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            str = str + entry.getKey() + "=" + entry.getValue() + "&";
        }
        String substring = str.substring(0, str.length() - 1);
        Log.d("SOS-NetworkUtils", "buildParam:" + substring);
        return substring;
    }

    public static String doGet(String str, Map<String, String> map, String str2) {
        ByteArrayOutputStream byteArrayOutputStream;
        InputStream inputStream;
        ByteArrayOutputStream byteArrayOutputStream2;
        HttpURLConnection openConnection;
        int responseCode;
        InputStream inputStream2 = null;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("param", buildBase64JsonString(map));
        hashMap.put("sign", getSignature(hashMap, str2));
        String buildURLParam = buildURLParam(hashMap);
        try {
            if (!TextUtils.isEmpty(buildURLParam)) {
                str = str + "?" + buildURLParam;
            }
            Log.w("SOS-NetworkUtils", "response url :  " + str);
            openConnection = openConnection(str);
            openConnection.setRequestMethod("GET");
            responseCode = openConnection.getResponseCode();
            Log.w("SOS-NetworkUtils", "responseCode :  " + responseCode);
        } catch (Exception e) {
            e = e;
            inputStream = null;
            byteArrayOutputStream2 = null;
        } catch (Throwable th) {
            th = th;
            byteArrayOutputStream = null;
        }
        if (responseCode != 200) {
            IOUtils.closeQuietly((InputStream) null);
            IOUtils.closeQuietly((OutputStream) null);
            return null;
        }
        InputStream inputStream3 = openConnection.getInputStream();
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                byte[] bArr = new byte[4096];
                while (true) {
                    int read = inputStream3.read(bArr);
                    if (read == -1) {
                        String byteArrayOutputStream3 = byteArrayOutputStream.toString();
                        IOUtils.closeQuietly(inputStream3);
                        IOUtils.closeQuietly((OutputStream) byteArrayOutputStream);
                        return byteArrayOutputStream3;
                    }
                    byteArrayOutputStream.write(bArr, 0, read);
                }
            } catch (Exception e2) {
                inputStream = inputStream3;
                e = e2;
                byteArrayOutputStream2 = byteArrayOutputStream;
                try {
                    Log.e("SOS-NetworkUtils", "exception when do get request", e);
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly((OutputStream) byteArrayOutputStream2);
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    inputStream2 = inputStream;
                    byteArrayOutputStream = byteArrayOutputStream2;
                    IOUtils.closeQuietly(inputStream2);
                    IOUtils.closeQuietly((OutputStream) byteArrayOutputStream);
                    throw th;
                }
            } catch (Throwable th3) {
                inputStream2 = inputStream3;
                th = th3;
                IOUtils.closeQuietly(inputStream2);
                IOUtils.closeQuietly((OutputStream) byteArrayOutputStream);
                throw th;
            }
        } catch (Exception e3) {
            byteArrayOutputStream2 = null;
            inputStream = inputStream3;
            e = e3;
        } catch (Throwable th4) {
            inputStream2 = inputStream3;
            th = th4;
            byteArrayOutputStream = null;
        }
    }

    public static String doPost(String str, Map<String, String> map, String str2) {
        ByteArrayOutputStream byteArrayOutputStream;
        InputStream inputStream;
        HttpURLConnection openConnection;
        int responseCode;
        InputStream inputStream2 = null;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("param", buildBase64JsonString(map));
        hashMap.put("sign", getSignature(hashMap, str2));
        try {
            Log.w("SOS-NetworkUtils", "response url :  " + str);
            openConnection = openConnection(str);
            openConnection.setRequestMethod("POST");
            addBodyIfExists(openConnection, hashMap);
            responseCode = openConnection.getResponseCode();
            Log.w("SOS-NetworkUtils", " responseCode :  " + responseCode);
        } catch (Exception e) {
            e = e;
            inputStream = null;
            byteArrayOutputStream = null;
        } catch (Throwable th) {
            th = th;
            byteArrayOutputStream = null;
            IOUtils.closeQuietly(inputStream2);
            IOUtils.closeQuietly((OutputStream) byteArrayOutputStream);
            throw th;
        }
        if (responseCode != 200) {
            IOUtils.closeQuietly((InputStream) null);
            IOUtils.closeQuietly((OutputStream) null);
            return null;
        }
        inputStream = openConnection.getInputStream();
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                try {
                    byte[] bArr = new byte[4096];
                    while (true) {
                        int read = inputStream.read(bArr);
                        if (read == -1) {
                            Log.w("SOS-NetworkUtils", "response string : " + byteArrayOutputStream.toString());
                            String byteArrayOutputStream2 = byteArrayOutputStream.toString();
                            IOUtils.closeQuietly(inputStream);
                            IOUtils.closeQuietly((OutputStream) byteArrayOutputStream);
                            return byteArrayOutputStream2;
                        }
                        byteArrayOutputStream.write(bArr, 0, read);
                    }
                } catch (Exception e2) {
                    e = e2;
                    Log.e("SOS-NetworkUtils", "exception when do post request", e);
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly((OutputStream) byteArrayOutputStream);
                    return null;
                }
            } catch (Throwable th2) {
                th = th2;
                inputStream2 = inputStream;
                IOUtils.closeQuietly(inputStream2);
                IOUtils.closeQuietly((OutputStream) byteArrayOutputStream);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            byteArrayOutputStream = null;
        } catch (Throwable th3) {
            th = th3;
            byteArrayOutputStream = null;
            inputStream2 = inputStream;
            IOUtils.closeQuietly(inputStream2);
            IOUtils.closeQuietly((OutputStream) byteArrayOutputStream);
            throw th;
        }
    }

    private static String encodeParameters(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        try {
            for (String str : map.keySet()) {
                if (!z) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode(str, "UTF-8"));
                sb.append('=');
                sb.append(URLEncoder.encode(map.get(str), "UTF-8"));
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

    public static String getSignature(Map<String, String> map, String str) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (String str2 : map.keySet()) {
            if (!z) {
                sb.append("&");
            }
            sb.append(str2);
            sb.append("=");
            sb.append(map.get(str2));
            z = false;
        }
        sb.append("&");
        sb.append(str);
        return getMd5Digest(Base64.encodeToString(getBytes(sb.toString()), 2));
    }

    public static HttpURLConnection openConnection(String str) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(15000);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        return httpURLConnection;
    }
}
