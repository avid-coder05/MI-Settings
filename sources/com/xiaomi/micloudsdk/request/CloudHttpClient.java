package com.xiaomi.micloudsdk.request;

import com.xiaomi.micloudsdk.request.utils.RequestContext;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;

/* loaded from: classes2.dex */
public class CloudHttpClient implements HttpClient {
    private HttpClient mProxy;

    protected CloudHttpClient(HttpClient httpClient) {
        this.mProxy = httpClient;
    }

    protected static DefaultHttpClient initClient() {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpProtocolParams.setUserAgent(defaultHttpClient.getParams(), RequestContext.getUserAgent());
        return defaultHttpClient;
    }

    public static CloudHttpClient newInstance() {
        return new CloudHttpClient(initClient());
    }
}
