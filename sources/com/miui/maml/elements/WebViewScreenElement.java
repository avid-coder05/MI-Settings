package com.miui.maml.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import com.miui.maml.util.TextFormatter;
import miui.net.ConnectivityHelper;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class WebViewScreenElement extends AnimatedScreenElement {
    private boolean mCachePage;
    private String mCurUrl;
    private Handler mHandler;
    private ViewGroup.LayoutParams mLayoutParams;
    private TextFormatter mUriFormatter;
    private int mUseNetwork;
    private Expression mUseNetworkExp;
    private boolean mViewAdded;
    private WebView mWebView;
    private Context mWindowContext;

    /* loaded from: classes2.dex */
    private class MamlInterface {
        private MamlInterface() {
        }
    }

    public WebViewScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mUseNetwork = 2;
        this.mWindowContext = screenElementRoot.getContext().mContext;
        WebView webView = new WebView(this.mWindowContext);
        this.mWebView = webView;
        webView.setWebViewClient(new WebViewClient() { // from class: com.miui.maml.elements.WebViewScreenElement.1
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView webView2, String str) {
                webView2.loadUrl(str);
                return true;
            }
        });
        this.mWebView.setOnTouchListener(new View.OnTouchListener() { // from class: com.miui.maml.elements.WebViewScreenElement.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                WebViewScreenElement webViewScreenElement = WebViewScreenElement.this;
                webViewScreenElement.mRoot.onUIInteractive(webViewScreenElement, "touch");
                return false;
            }
        });
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.setInitialScale(100);
        String attribute = element.getAttribute("userAgent");
        if (!TextUtils.isEmpty(attribute)) {
            this.mWebView.getSettings().setUserAgentString(attribute);
        }
        this.mWebView.addJavascriptInterface(new MamlInterface(), "maml");
        this.mLayoutParams = new ViewGroup.LayoutParams(-1, -1);
        this.mHandler = getContext().getHandler();
        Variables variables = getVariables();
        this.mUriFormatter = new TextFormatter(variables, element.getAttribute("uri"), Expression.build(variables, element.getAttribute("uriExp")));
        this.mCachePage = Boolean.parseBoolean(element.getAttribute("cachePage"));
        String attribute2 = element.getAttribute("useNetwork");
        if (TextUtils.isEmpty(attribute2) || "all".equalsIgnoreCase(attribute2)) {
            this.mUseNetwork = 2;
        } else if ("wifi".equalsIgnoreCase(attribute2)) {
            this.mUseNetwork = 1;
        } else {
            this.mUseNetworkExp = Expression.build(variables, attribute2);
        }
    }

    private boolean canUseNetwork() {
        int i = this.mUseNetwork;
        if (i == 2) {
            return true;
        }
        return i == 1 && ConnectivityHelper.getInstance().isWifiConnected();
    }

    private final void finishWebView() {
        this.mHandler.post(new Runnable() { // from class: com.miui.maml.elements.WebViewScreenElement.10
            @Override // java.lang.Runnable
            public void run() {
                WebViewScreenElement.this.mRoot.getViewManager().removeView(WebViewScreenElement.this.mWebView);
                WebViewScreenElement.this.mViewAdded = false;
                if (WebViewScreenElement.this.mCachePage) {
                    WebViewScreenElement.this.mWebView.onPause();
                } else {
                    WebViewScreenElement.this.mWebView.loadUrl("about:blank");
                }
            }
        });
    }

    private final void initWebView() {
        if (!this.mViewAdded || this.mCachePage) {
            this.mHandler.post(new Runnable() { // from class: com.miui.maml.elements.WebViewScreenElement.9
                @Override // java.lang.Runnable
                public void run() {
                    if (WebViewScreenElement.this.mViewAdded) {
                        if (WebViewScreenElement.this.mCachePage) {
                            WebViewScreenElement.this.mWebView.onResume();
                            return;
                        }
                        return;
                    }
                    WebViewScreenElement webViewScreenElement = WebViewScreenElement.this;
                    webViewScreenElement.updateLayoutParams(webViewScreenElement.mLayoutParams);
                    Log.d("MAML WebViewScreenElement", "addWebView");
                    WebViewScreenElement.this.mRoot.getViewManager().addView(WebViewScreenElement.this.mWebView, WebViewScreenElement.this.mLayoutParams);
                    WebViewScreenElement.this.mViewAdded = true;
                }
            });
        }
    }

    private void pauseWebView(final boolean z) {
        this.mHandler.post(new Runnable() { // from class: com.miui.maml.elements.WebViewScreenElement.8
            @Override // java.lang.Runnable
            public void run() {
                if (z) {
                    WebViewScreenElement.this.mWebView.onPause();
                } else {
                    WebViewScreenElement.this.mWebView.onResume();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        boolean z;
        int width = (int) getWidth();
        if (layoutParams.width != width) {
            layoutParams.width = width;
            z = true;
        } else {
            z = false;
        }
        int height = (int) getHeight();
        if (layoutParams.height != height) {
            layoutParams.height = height;
            return true;
        }
        return z;
    }

    private final void updateView() {
        if (this.mViewAdded) {
            this.mWebView.setX(getAbsoluteLeft());
            this.mWebView.setY(getAbsoluteTop());
            if (updateLayoutParams(this.mLayoutParams)) {
                this.mWebView.setLayoutParams(this.mLayoutParams);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        super.doTick(j);
        String text = this.mUriFormatter.getText();
        if (!TextUtils.isEmpty(text) && !TextUtils.equals(this.mCurUrl, text)) {
            Log.d("MAML WebViewScreenElement", "loadUrl: " + text);
            loadUrl(text);
        }
        updateView();
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        finishWebView();
        if (this.mCachePage) {
            return;
        }
        this.mCurUrl = null;
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        Expression expression = this.mUseNetworkExp;
        if (expression != null) {
            this.mUseNetwork = (int) expression.evaluate();
        }
        if (this.mRoot.getViewManager() != null) {
            initWebView();
        } else {
            Log.e("MAML WebViewScreenElement", "ViewManager must be set before init");
        }
    }

    public void loadUrl(final String str) {
        if (canUseNetwork() || !str.startsWith("http")) {
            this.mCurUrl = str;
            this.mHandler.post(new Runnable() { // from class: com.miui.maml.elements.WebViewScreenElement.3
                @Override // java.lang.Runnable
                public void run() {
                    WebViewScreenElement.this.mWebView.loadUrl(str);
                }
            });
            return;
        }
        Log.d("MAML WebViewScreenElement", "loadUrl canceled due to useNetwork setting." + str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void onVisibilityChange(final boolean z) {
        super.onVisibilityChange(z);
        this.mHandler.post(new Runnable() { // from class: com.miui.maml.elements.WebViewScreenElement.7
            @Override // java.lang.Runnable
            public void run() {
                WebViewScreenElement.this.mWebView.setVisibility(z ? 0 : 4);
            }
        });
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void pause() {
        super.pause();
        if (this.mViewAdded) {
            pauseWebView(true);
        }
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void render(Canvas canvas) {
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void resume() {
        super.resume();
        if (this.mViewAdded) {
            pauseWebView(false);
        }
    }
}
