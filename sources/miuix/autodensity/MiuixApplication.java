package miuix.autodensity;

import android.app.Application;

/* loaded from: classes5.dex */
public class MiuixApplication extends Application implements IDensity {
    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        AutoDensityConfig.init(this);
    }
}
