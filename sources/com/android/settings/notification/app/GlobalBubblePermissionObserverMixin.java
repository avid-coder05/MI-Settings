package com.android.settings.notification.app;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

/* loaded from: classes2.dex */
public class GlobalBubblePermissionObserverMixin extends ContentObserver {
    private final Context mContext;
    private final Listener mListener;

    /* loaded from: classes2.dex */
    public interface Listener {
        void onGlobalBubblePermissionChanged();
    }

    public GlobalBubblePermissionObserverMixin(Context context, Listener listener) {
        super(new Handler(Looper.getMainLooper()));
        this.mContext = context;
        this.mListener = listener;
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z, Uri uri) {
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onGlobalBubblePermissionChanged();
        }
    }

    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("notification_bubbles"), false, this);
    }

    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this);
    }
}
