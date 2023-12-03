package com.xiaomi.onetrack.a;

import android.text.TextUtils;
import com.xiaomi.onetrack.util.p;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class c implements Runnable {
    final /* synthetic */ JSONObject a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public c(JSONObject jSONObject) {
        this.a = jSONObject;
    }

    @Override // java.lang.Runnable
    public void run() {
        boolean e;
        p.a("AppConfigUpdater", "checkAppConfigVersion start");
        JSONArray optJSONArray = this.a.optJSONArray("config");
        if (optJSONArray != null) {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < optJSONArray.length(); i++) {
                JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                String optString = optJSONObject == null ? "" : optJSONObject.optString("appId");
                p.a("AppConfigUpdater", "appId: " + optString);
                if (!TextUtils.isEmpty(optString)) {
                    int optInt = optJSONObject == null ? 0 : optJSONObject.optInt("version");
                    int d = g.a().d(optString);
                    e = a.e(optString);
                    p.a("AppConfigUpdater", "local version: " + d + ", server version: " + optInt + ", canUpdate: " + e);
                    if (optInt > 0 && optInt > d && e) {
                        arrayList.add(optString);
                    }
                }
            }
            if (arrayList.size() > 0) {
                a.b(arrayList);
            } else {
                p.a("AppConfigUpdater", "no appIds need to pull cloud data");
            }
        }
    }
}
