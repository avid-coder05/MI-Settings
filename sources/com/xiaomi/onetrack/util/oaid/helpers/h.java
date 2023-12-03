package com.xiaomi.onetrack.util.oaid.helpers;

import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.xiaomi.onetrack.util.p;
import miui.yellowpage.Tag;

/* loaded from: classes2.dex */
public class h {
    public String a(Context context) {
        Bundle call;
        Uri parse = Uri.parse("content://cn.nubia.identity/identity");
        try {
            int i = Build.VERSION.SDK_INT;
            if (i > 17) {
                ContentProviderClient acquireContentProviderClient = context.getContentResolver().acquireContentProviderClient(parse);
                call = acquireContentProviderClient.call("getOAID", null, null);
                if (i >= 24) {
                    acquireContentProviderClient.close();
                } else {
                    acquireContentProviderClient.release();
                }
            } else {
                call = context.getContentResolver().call(parse, "getOAID", (String) null, (Bundle) null);
            }
            return call.getInt(Tag.TagWebService.CommonResult.RESULT_CODE, -1) == 0 ? call.getString("id") : "";
        } catch (Exception e) {
            p.a("NubiaDeviceIDHelper", e.getMessage());
            return "";
        }
    }
}
