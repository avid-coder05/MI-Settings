package com.android.settings.network;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public abstract class GlobalSettingsChangeListener extends ContentObserver implements LifecycleObserver, AutoCloseable {
    private Context mContext;
    private String mField;
    private Lifecycle mLifecycle;
    private AtomicBoolean mListening;
    private Uri mUri;

    public GlobalSettingsChangeListener(Context context, String str) {
        this(Looper.getMainLooper(), context, str);
    }

    public GlobalSettingsChangeListener(Looper looper, Context context, String str) {
        super(new Handler(looper));
        this.mContext = context;
        this.mField = str;
        this.mUri = Settings.Global.getUriFor(str);
        this.mListening = new AtomicBoolean(false);
        monitorUri(true);
    }

    private void monitorUri(boolean z) {
        if (this.mListening.compareAndSet(!z, z)) {
            if (z) {
                this.mContext.getContentResolver().registerContentObserver(this.mUri, false, this);
            } else {
                this.mContext.getContentResolver().unregisterContentObserver(this);
            }
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        monitorUri(false);
        notifyChangeBasedOn(null);
    }

    public void notifyChangeBasedOn(Lifecycle lifecycle) {
        Lifecycle lifecycle2 = this.mLifecycle;
        if (lifecycle2 != null) {
            lifecycle2.removeObserver(this);
        }
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mLifecycle = lifecycle;
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z) {
        if (this.mListening.get()) {
            onChanged(this.mField);
        }
    }

    public abstract void onChanged(String str);

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        close();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
        monitorUri(true);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
        monitorUri(false);
    }
}
