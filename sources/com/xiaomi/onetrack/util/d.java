package com.xiaomi.onetrack.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.xiaomi.onetrack.e.a;
import miui.yellowpage.Tag;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class d {
    private static final String a = "d";
    private static volatile d f;
    private final Context h;
    private Handler i = new e(this, Looper.getMainLooper());
    private BroadcastReceiver j = new f(this);

    private d() {
        Context b = a.b();
        this.h = b;
        a(b);
    }

    public static d a() {
        if (f == null) {
            synchronized (d.class) {
                if (f == null) {
                    f = new d();
                }
            }
        }
        return f;
    }

    private void a(Context context) {
        if (context == null) {
            return;
        }
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.xiaomi.onetrack.DEBUG");
            context.registerReceiver(this.j, intentFilter, "com.xiaomi.onetrack.permissions.DEBUG_MODE", null);
        } catch (Exception e) {
            p.a(a, "registerDebugModeReceiver: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            int optInt = jSONObject.optInt(Tag.TagWebService.CommonResult.RESULT_CODE);
            String optString = jSONObject.optString("message");
            String optString2 = jSONObject.optString("result");
            boolean optBoolean = jSONObject.optBoolean("success");
            Message obtain = Message.obtain();
            obtain.what = 100;
            Bundle bundle = new Bundle();
            if (optInt == 0 && optBoolean) {
                bundle.putString("hint", optString2);
            } else {
                bundle.putString("hint", optString);
            }
            obtain.setData(bundle);
            this.i.sendMessage(obtain);
        } catch (JSONException e) {
            p.b(a, e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str, String str2, String str3) {
        i.a(new g(this, str, str2, str3));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b(String str) {
        Toast.makeText(this.h, str, 1).show();
    }
}
