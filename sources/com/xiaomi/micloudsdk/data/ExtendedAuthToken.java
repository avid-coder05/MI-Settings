package com.xiaomi.micloudsdk.data;

import android.text.TextUtils;
import com.xiaomi.micloudsdk.data.IAuthToken;
import com.xiaomi.micloudsdk.request.utils.CloudRequestHelper;
import com.xiaomi.micloudsdk.request.utils.HttpUtils;
import com.xiaomi.micloudsdk.utils.CryptCoder;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/* loaded from: classes2.dex */
public final class ExtendedAuthToken implements IAuthToken {
    public final String authToken;
    public final String security;

    private ExtendedAuthToken(String str, String str2) {
        this.authToken = str;
        this.security = str2;
    }

    public static ExtendedAuthToken parse(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String[] split = str.split(",");
        if (split.length != 2 || TextUtils.isEmpty(split[0]) || TextUtils.isEmpty(split[1])) {
            return null;
        }
        return new ExtendedAuthToken(split[0], split[1]);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || ExtendedAuthToken.class != obj.getClass()) {
            return false;
        }
        ExtendedAuthToken extendedAuthToken = (ExtendedAuthToken) obj;
        return Objects.equals(this.authToken, extendedAuthToken.authToken) && Objects.equals(this.security, extendedAuthToken.security);
    }

    @Override // com.xiaomi.micloudsdk.data.IAuthToken
    public IAuthToken.HttpRequestBuilder getBuilderForCloudRequest() {
        return new IAuthToken.HttpRequestBuilder() { // from class: com.xiaomi.micloudsdk.data.ExtendedAuthToken.1
            @Override // com.xiaomi.micloudsdk.data.IAuthToken.HttpRequestBuilder
            public void addAdditionalHeaders(List<Header> list) {
            }

            @Override // com.xiaomi.micloudsdk.data.IAuthToken.HttpRequestBuilder
            public void addParams(ArrayList<NameValuePair> arrayList) {
            }

            @Override // com.xiaomi.micloudsdk.data.IAuthToken.HttpRequestBuilder
            public CryptCoder getCryptCoder(String str) {
                return CloudRequestHelper.getCryptCoder(str, ExtendedAuthToken.this.security);
            }

            @Override // com.xiaomi.micloudsdk.data.IAuthToken.HttpRequestBuilder
            public String getServiceToken() {
                return ExtendedAuthToken.this.authToken;
            }

            @Override // com.xiaomi.micloudsdk.data.IAuthToken.HttpRequestBuilder
            public void signParams(HttpUtils.HttpMethod httpMethod, String str, ArrayList<NameValuePair> arrayList) throws UnsupportedEncodingException {
                arrayList.add(new BasicNameValuePair("signature", HttpUtils.getSignature(httpMethod, str, arrayList, ExtendedAuthToken.this.security)));
            }
        };
    }

    public int hashCode() {
        String str = this.authToken;
        int hashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.security;
        return hashCode + (str2 != null ? str2.hashCode() : 0);
    }
}
