package com.xiaomi.mirror;

import android.app.PendingIntent;
import android.net.Uri;
import android.view.View;

/* loaded from: classes2.dex */
public class MirrorMenu {
    static final int TYPE_COMMON = 0;
    static final int TYPE_NEW_DISPLAY_OPEN = 1;
    static final int TYPE_PC_OPEN = 2;

    /* loaded from: classes2.dex */
    public static class Builder {
        public MirrorMenu build() {
            throw new UnsupportedOperationException("Stub!");
        }

        public Builder setCallback(Callback callback) {
            throw new UnsupportedOperationException("Stub!");
        }

        public Builder setLabel(CharSequence charSequence) {
            throw new UnsupportedOperationException("Stub!");
        }
    }

    /* loaded from: classes2.dex */
    public interface Callback {
        void onClick(View view, MirrorMenu mirrorMenu);
    }

    /* loaded from: classes2.dex */
    public static class NewDisplayOpenBuilder {
        public MirrorMenu build() {
            throw new UnsupportedOperationException("Stub!");
        }

        public NewDisplayOpenBuilder setLabel(CharSequence charSequence) {
            throw new UnsupportedOperationException("Stub!");
        }

        public NewDisplayOpenBuilder setPendingIntent(PendingIntent pendingIntent) {
            throw new UnsupportedOperationException("Stub!");
        }
    }

    /* loaded from: classes2.dex */
    public static class PcOpenBuilder {
        public MirrorMenu build() {
            throw new UnsupportedOperationException("Stub!");
        }

        public PcOpenBuilder setExtra(String str) {
            throw new UnsupportedOperationException("Stub!");
        }

        public PcOpenBuilder setLabel(CharSequence charSequence) {
            throw new UnsupportedOperationException("Stub!");
        }

        public PcOpenBuilder setUri(Uri uri) {
            throw new UnsupportedOperationException("Stub!");
        }
    }

    public CharSequence getLabel() {
        throw new UnsupportedOperationException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Callback getListener() {
        throw new UnsupportedOperationException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PendingIntent getPendingIntent() {
        throw new UnsupportedOperationException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getType() {
        throw new UnsupportedOperationException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Uri getUri() {
        throw new UnsupportedOperationException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean needCallRemote() {
        throw new UnsupportedOperationException("Stub!");
    }
}
