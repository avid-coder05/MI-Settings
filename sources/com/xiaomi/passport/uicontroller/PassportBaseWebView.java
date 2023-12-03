package com.xiaomi.passport.uicontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import com.xiaomi.accountsdk.utils.UIUtils;
import com.xiaomi.accountsdk.utils.XMPassportUtil;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes2.dex */
public class PassportBaseWebView extends WebView {
    public PassportBaseWebView(Context context) {
        super(context);
        init(context);
    }

    public PassportBaseWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public PassportBaseWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        UIUtils.adaptForceDarkInApi29(context, this);
        setBackgroundColor(UIUtils.isSystemNightMode(getContext()) ? getResources().getColor(17170444) : getResources().getColor(17170443));
    }

    @Override // android.webkit.WebView
    public void loadUrl(String str) {
        loadUrl(str, new HashMap());
    }

    @Override // android.webkit.WebView
    public void loadUrl(String str, Map<String, String> map) {
        super.loadUrl(XMPassportUtil.buildUrlWithNightModeQueryParam(getContext(), XMPassportUtil.buildUrlWithLocaleQueryParam(str)), map);
    }
}
