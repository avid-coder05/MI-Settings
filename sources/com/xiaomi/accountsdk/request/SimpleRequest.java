package com.xiaomi.accountsdk.request;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.MiuiWindowManager$LayoutParams;
import com.xiaomi.accountsdk.account.XMPassportSettings;
import com.xiaomi.accountsdk.request.intercept.NetworkInterceptor;
import com.xiaomi.accountsdk.request.log.TransportLogHelper;
import com.xiaomi.accountsdk.utils.DiagnosisLog;
import com.xiaomi.accountsdk.utils.DiagnosisLogInterface;
import com.xiaomi.accountsdk.utils.EasyMap;
import com.xiaomi.accountsdk.utils.IOUtils;
import com.xiaomi.accountsdk.utils.ObjectUtils;
import com.xiaomi.accountsdk.utils.VersionUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import miui.yellowpage.YellowPageContract;

@SuppressLint({"NewApi"})
/* loaded from: classes2.dex */
public final class SimpleRequest {
    private static ConnectivityListener sConnectivityListener;
    private static final Logger log = Logger.getLogger(SimpleRequest.class.getSimpleName());
    private static HttpURLConnectionFactory sHttpURLConnectionFactory = new HttpURLConnectionFactory() { // from class: com.xiaomi.accountsdk.request.SimpleRequest.1
        @Override // com.xiaomi.accountsdk.request.SimpleRequest.HttpURLConnectionFactory
        public HttpURLConnection makeConn(URL url) throws IOException {
            if (SimpleRequest.sConnectivityListener != null) {
                SimpleRequest.sConnectivityListener.onOpenUrlConnection(url);
            }
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if (Build.VERSION.SDK_INT <= 19 && (httpURLConnection instanceof HttpsURLConnection)) {
                try {
                    ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(TlsCompatSocketFactory.sslSocketFactory());
                } catch (IllegalStateException e) {
                    SimpleRequest.log.log(Level.SEVERE, "SimpleRequest", (Throwable) e);
                } catch (KeyManagementException e2) {
                    SimpleRequest.log.log(Level.SEVERE, "SimpleRequest", (Throwable) e2);
                } catch (KeyStoreException e3) {
                    SimpleRequest.log.log(Level.SEVERE, "SimpleRequest", (Throwable) e3);
                } catch (NoSuchAlgorithmException e4) {
                    SimpleRequest.log.log(Level.SEVERE, "SimpleRequest", (Throwable) e4);
                }
            }
            return httpURLConnection;
        }
    };
    private static RequestLoggerForTest sRequestLoggerForTest = null;

    /* loaded from: classes2.dex */
    public static class HeaderContent {
        private int httpCode = -1;
        private final Set<String> cookieKeys = new HashSet();
        private final Map<String, String> headers = new HashMap();

        public int getHttpCode() {
            return this.httpCode;
        }

        public void putCookies(Map<String, String> map) {
            putHeaders(map);
            if (map != null) {
                this.cookieKeys.addAll(map.keySet());
            }
        }

        public void putHeaders(Map<String, String> map) {
            this.headers.putAll(map);
        }

        public void setHttpCode(int i) {
            this.httpCode = i;
        }
    }

    /* loaded from: classes2.dex */
    public interface HttpURLConnectionFactory {
        HttpURLConnection makeConn(URL url) throws IOException;
    }

    /* loaded from: classes2.dex */
    public interface RequestLoggerForTest {
        void log(String str, Map<String, String> map, Map<String, String> map2, Map<String, String> map3, boolean z, Integer num, Map<String, String> map4);
    }

    /* loaded from: classes2.dex */
    public static class StringContent extends HeaderContent {
        private String body;

        public StringContent(String str) {
            this.body = str;
        }

        public String getBody() {
            return this.body;
        }

        public String toString() {
            return "StringContent{body='" + this.body + "'}";
        }
    }

    private static String appendUrl(String str, Map<String, String> map) {
        Objects.requireNonNull(str, "origin is not allowed null");
        Uri.Builder buildUpon = Uri.parse(str).buildUpon();
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                buildUpon.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return buildUpon.build().toString();
    }

    private static String encode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static String encodeFormatAndJoinMap(Map<String, String> map, String str) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(str);
            }
            String encode = encode(entry.getKey());
            String encode2 = !TextUtils.isEmpty(entry.getValue()) ? encode(entry.getValue()) : "";
            sb.append(encode);
            sb.append("=");
            sb.append(encode2);
        }
        return sb.toString();
    }

    private static DiagnosisLogInterface getDiagnosisLogger() {
        return DiagnosisLog.get();
    }

    protected static String joinMap(Map<String, String> map, String str) {
        if (map == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(str);
            }
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key);
            sb.append("=");
            sb.append(value);
        }
        return sb.toString();
    }

    protected static HttpURLConnection makeConn(String str, Map<String, String> map, Map<String, String> map2, Integer num) {
        URL url;
        try {
            url = new URL(str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            url = null;
        }
        if (url == null) {
            log.severe("failed to init url");
            return null;
        }
        if (num == null) {
            num = 30000;
        }
        try {
            CookieHandler.setDefault(null);
            HttpURLConnection makeConn = sHttpURLConnectionFactory.makeConn(url);
            makeConn.setInstanceFollowRedirects(false);
            makeConn.setConnectTimeout(num.intValue());
            makeConn.setReadTimeout(num.intValue());
            makeConn.setUseCaches(false);
            makeConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if ((map2 == null || TextUtils.isEmpty(map2.get("User-Agent"))) && !TextUtils.isEmpty(XMPassportSettings.getUserAgent())) {
                makeConn.setRequestProperty("User-Agent", XMPassportSettings.getUserAgent());
            }
            if (map == null) {
                map = new EasyMap<>();
            }
            map.put("sdkVersion", VersionUtils.getVersion());
            makeConn.setRequestProperty("Cookie", joinMap(map, "; "));
            if (map2 != null) {
                for (String str2 : map2.keySet()) {
                    makeConn.setRequestProperty(str2, map2.get(str2));
                }
            }
            return makeConn;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    protected static Map<String, String> parseCookies(List<HttpCookie> list) {
        HashMap hashMap = new HashMap();
        for (HttpCookie httpCookie : list) {
            if (!httpCookie.hasExpired()) {
                String name = httpCookie.getName();
                String value = httpCookie.getValue();
                if (name != null) {
                    hashMap.put(name, value);
                }
            }
        }
        return hashMap;
    }

    public static StringContent postAsString(String str, Map<String, String> map, Map<String, String> map2, Map<String, String> map3, Map<String, String> map4, boolean z, Integer num) throws IOException, AccessDeniedException, AuthenticationFailureException {
        Integer num2;
        int responseCode;
        Integer valueOf;
        RequestLoggerForTest requestLoggerForTest = sRequestLoggerForTest;
        if (requestLoggerForTest != null) {
            requestLoggerForTest.log(str, map, map3, map2, z, num, map4);
        }
        String appendUrl = map4 != null ? appendUrl(str, map4) : str;
        String logPostRequest = getDiagnosisLogger().logPostRequest(str, map4, appendUrl, map, map2, map3);
        long currentTimeMillis = System.currentTimeMillis();
        NetworkInterceptor.get().onBegin(logPostRequest, "POST", str);
        TransportLogHelper.logRequestStarted();
        HttpURLConnection makeConn = makeConn(appendUrl, map2, map3, num);
        if (makeConn == null) {
            log.severe("failed to create URLConnection");
            throw new IOException("failed to create connection");
        }
        try {
            try {
                try {
                    makeConn.setDoInput(true);
                    makeConn.setDoOutput(true);
                    makeConn.setRequestMethod("POST");
                    makeConn.connect();
                    if (map != null && !map.isEmpty()) {
                        String encodeFormatAndJoinMap = encodeFormatAndJoinMap(map, "&");
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(makeConn.getOutputStream());
                        try {
                            bufferedOutputStream.write(encodeFormatAndJoinMap.getBytes("utf-8"));
                            IOUtils.closeQuietly(bufferedOutputStream);
                        } catch (Throwable th) {
                            IOUtils.closeQuietly(bufferedOutputStream);
                            throw th;
                        }
                    }
                    responseCode = makeConn.getResponseCode();
                    valueOf = Integer.valueOf(responseCode);
                } catch (ProtocolException unused) {
                    throw new IOException("protocol error");
                }
            } catch (Exception e) {
                e = e;
                num2 = null;
            }
            try {
                getDiagnosisLogger().logResponseCode(logPostRequest, responseCode);
                if (responseCode != 200 && responseCode != 302) {
                    if (responseCode != 403) {
                        if (responseCode == 401 || responseCode == 400) {
                            AuthenticationFailureException authenticationFailureException = new AuthenticationFailureException(responseCode, "authentication failure for post, code: " + responseCode);
                            authenticationFailureException.setWwwAuthenticateHeader(makeConn.getHeaderField("WWW-Authenticate"));
                            authenticationFailureException.setCaDisableSecondsHeader(makeConn.getHeaderField("CA-DISABLE-SECONDS"));
                            throw authenticationFailureException;
                        }
                        Logger logger = log;
                        logger.info("http status error when POST: " + responseCode);
                        if (responseCode == 301) {
                            logger.info("unexpected redirect from " + makeConn.getURL().getHost() + " to " + makeConn.getHeaderField("Location"));
                        }
                        throw new IOException("unexpected http res code: " + responseCode);
                    }
                    throw new AccessDeniedException(responseCode, "access denied, encrypt error or user is forbidden to access the resource");
                }
                Map<String, List<String>> headerFields = makeConn.getHeaderFields();
                URI create = URI.create(appendUrl);
                String host = create.getHost();
                final HashSet hashSet = new HashSet();
                hashSet.add(host);
                if (map3 != null && map3.containsKey(YellowPageContract.Permission.HOST)) {
                    hashSet.add(map3.get(YellowPageContract.Permission.HOST));
                }
                if (hashSet.contains("c.id.mi.com")) {
                    hashSet.add("account.xiaomi.com");
                }
                CookieManager cookieManager = new CookieManager(null, new CookiePolicy() { // from class: com.xiaomi.accountsdk.request.SimpleRequest.2
                    @Override // java.net.CookiePolicy
                    public boolean shouldAccept(URI uri, HttpCookie httpCookie) {
                        String domain = httpCookie.getDomain();
                        Iterator it = hashSet.iterator();
                        while (it.hasNext()) {
                            if (HttpCookie.domainMatches(domain, (String) it.next())) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
                cookieManager.put(create, headerFields);
                HashMap hashMap = new HashMap();
                CookieStore cookieStore = cookieManager.getCookieStore();
                Iterator it = hashSet.iterator();
                while (it.hasNext()) {
                    Map<String, String> parseCookies = parseCookies(cookieStore.get(URI.create(appendUrl.replaceFirst(host, (String) it.next()))));
                    if (parseCookies != null) {
                        hashMap.putAll(parseCookies);
                    }
                }
                StringBuilder sb = new StringBuilder();
                if (z) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(makeConn.getInputStream()), MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE);
                    while (true) {
                        try {
                            String readLine = bufferedReader.readLine();
                            if (readLine == null) {
                                break;
                            }
                            sb.append(readLine);
                        } catch (Throwable th2) {
                            IOUtils.closeQuietly(bufferedReader);
                            throw th2;
                        }
                    }
                    IOUtils.closeQuietly(bufferedReader);
                }
                String sb2 = sb.toString();
                NetworkInterceptor.get().onSuccess(logPostRequest, "POST", str, currentTimeMillis, responseCode, sb2.length());
                StringContent stringContent = new StringContent(sb2);
                stringContent.putCookies(hashMap);
                stringContent.setHttpCode(responseCode);
                stringContent.putHeaders(ObjectUtils.listToMap(headerFields));
                getDiagnosisLogger().logResponse(logPostRequest, sb2, headerFields, hashMap);
                TransportLogHelper.logRequestSuccessed();
                return stringContent;
            } catch (Exception e2) {
                e = e2;
                num2 = valueOf;
                NetworkInterceptor.get().onException(logPostRequest, "POST", str, currentTimeMillis, e, num2);
                getDiagnosisLogger().logRequestException(e);
                TransportLogHelper.logRequestException(e);
                throw e;
            }
        } finally {
            makeConn.disconnect();
        }
    }

    public static StringContent postAsString(String str, Map<String, String> map, Map<String, String> map2, boolean z) throws IOException, AccessDeniedException, AuthenticationFailureException {
        return postAsString(str, map, map2, null, null, z, null);
    }
}
