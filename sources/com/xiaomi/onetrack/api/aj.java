package com.xiaomi.onetrack.api;

import android.os.Process;
import com.xiaomi.onetrack.Configuration;
import com.xiaomi.onetrack.OneTrack;
import com.xiaomi.onetrack.api.al;
import com.xiaomi.onetrack.util.i;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.v;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes2.dex */
public class aj implements al.b, d {
    private final ConcurrentHashMap<String, String> d = new ConcurrentHashMap<>();
    private Configuration e;
    private al f;
    private v g;

    public aj(Configuration configuration, v vVar) {
        this.e = configuration;
        this.g = vVar;
        al a = al.a();
        this.f = a;
        a.a(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void b() {
        try {
            synchronized (this.d) {
                if (this.d.size() <= 0) {
                    return;
                }
                for (Map.Entry<String, String> entry : this.d.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    this.f.b(value, key, this.e);
                    if (p.a) {
                        p.a("OneTrackSystemImp", "name:" + value + "data :" + key);
                    }
                }
                this.d.clear();
            }
        } catch (Exception e) {
            p.a("OneTrackSystemImp", "trackCachedEvents: " + e.toString());
        }
    }

    private boolean b(String str, String str2) {
        if (OneTrack.isDisable()) {
            return false;
        }
        if ((str != null && str.equals("onetrack_bug_report")) || str2 == null || str2.length() * 2 <= 102400) {
            return true;
        }
        p.a("OneTrackSystemImp", "Event size exceed limitation!");
        return false;
    }

    @Override // com.xiaomi.onetrack.api.al.b
    public void a() {
        i.a(new ak(this));
    }

    @Override // com.xiaomi.onetrack.api.d
    public void a(int i) {
        this.f.a(i);
    }

    @Override // com.xiaomi.onetrack.api.d
    public void a(String str, String str2) {
        v vVar = this.g;
        if (vVar != null && !vVar.a(str)) {
            p.a("OneTrackSystemImp", "The privacy policy is not permitted, and the event is not basic or recommend event or custom dau event, skip it.");
        } else if (b(str, str2)) {
            if (com.xiaomi.onetrack.b.h.b()) {
                com.xiaomi.onetrack.b.h.a(this);
            } else if (!"onetrack_cta_status".equalsIgnoreCase(str)) {
                com.xiaomi.onetrack.b.h.a(str, str2);
                return;
            }
            if (p.a) {
                p.a("OneTrackSystemImp", "track name:" + str + " data :" + str2 + " tid" + Process.myTid());
            }
            synchronized (this.d) {
                if (!this.f.a(str, str2, this.e)) {
                    this.d.put(str2, str);
                    if (p.a) {
                        p.a("OneTrackSystemImp", "track mIOneTrackService is null!" + this.d.size() + "  " + str2);
                    }
                }
            }
        }
    }
}
