package com.xiaomi.micloudsdk.exception;

import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.opensdk.exception.ServerException;
import miui.vip.VipService;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

/* loaded from: classes2.dex */
public class CloudServerException extends ServerException {
    public int code;
    public int retryTime;
    public long serverDate;
    public int statusCode;

    public CloudServerException(int i) {
        super("status: " + i);
        this.statusCode = i;
    }

    public CloudServerException(int i, int i2, int i3) {
        super("status: " + i);
        this.statusCode = i;
        this.code = i2;
        this.retryTime = i3 * VipService.VIP_SERVICE_FAILURE;
    }

    public CloudServerException(int i, String str) {
        super("status: " + i + " message: " + str);
        this.statusCode = i;
    }

    public CloudServerException(int i, Throwable th) {
        super("status: " + i, th);
        this.statusCode = i;
    }

    public CloudServerException(int i, HttpResponse httpResponse) {
        super("status: " + i);
        String value;
        Header firstHeader;
        this.statusCode = i;
        if (httpResponse != null) {
            try {
                if (httpResponse.getStatusLine() != null && httpResponse.getStatusLine().getStatusCode() == 503 && (firstHeader = httpResponse.getFirstHeader("Retry-After")) != null && !TextUtils.isEmpty(firstHeader.getValue())) {
                    this.retryTime = Integer.valueOf(firstHeader.getValue()).intValue() * VipService.VIP_SERVICE_FAILURE;
                }
                Header firstHeader2 = httpResponse.getFirstHeader("Date");
                if (firstHeader2 == null || (value = firstHeader2.getValue()) == null) {
                    return;
                }
                try {
                    this.serverDate = DateUtils.parseDate(value).getTime();
                } catch (DateParseException e) {
                    Log.w("CloudServerException", "Error parsing server date", e);
                }
            } catch (NumberFormatException unused) {
                Log.d("CloudServerException", "Can't find retry time in 503 HttpURLConnection response");
            }
        }
    }

    public static boolean isMiCloudServerException(int i) {
        return i == 400 || i == 401 || i == 403 || i == 406 || i / 100 == 5;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
