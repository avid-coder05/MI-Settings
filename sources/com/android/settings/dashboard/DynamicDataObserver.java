package com.android.settings.dashboard;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

/* loaded from: classes.dex */
public abstract class DynamicDataObserver extends ContentObserver {
    /* JADX INFO: Access modifiers changed from: protected */
    public DynamicDataObserver() {
        super(new Handler(Looper.getMainLooper()));
    }

    public abstract Uri getUri();

    @Override // android.database.ContentObserver
    public void onChange(boolean z) {
        onDataChanged();
    }

    public abstract void onDataChanged();
}
