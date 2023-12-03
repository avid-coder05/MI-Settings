package com.google.android.play.core.splitinstall;

import android.content.Context;

/* loaded from: classes2.dex */
public class SplitInstallManagerFactory {
    public static SplitInstallManager create(Context context) {
        return new SplitInstallManagerImpl(new SplitInstallService(context), context);
    }
}
