package com.xiaomi.passport.uicontroller;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.xiaomi.accountsdk.utils.ServerTimeUtil;

/* loaded from: classes2.dex */
public class NotificationWebView extends PassportBaseWebView {
    private final boolean needRemoveAllCookie;
    private final ServerTimeUtil.ServerTimeAlignedListener serverTimeAlignedListener;

    @Override // android.webkit.WebView, android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ServerTimeUtil.addServerTimeChangedListener(this.serverTimeAlignedListener);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        ServerTimeUtil.removeServerTimeChangedListener(this.serverTimeAlignedListener);
        if (this.needRemoveAllCookie) {
            CookieSyncManager.createInstance(getContext());
            CookieManager.getInstance().removeAllCookie();
        }
        super.onDetachedFromWindow();
    }
}
