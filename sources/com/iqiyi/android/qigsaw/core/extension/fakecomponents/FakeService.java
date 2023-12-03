package com.iqiyi.android.qigsaw.core.extension.fakecomponents;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/* loaded from: classes2.dex */
public class FakeService extends Service {
    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        stopSelf();
        return super.onStartCommand(intent, i, i2);
    }
}
