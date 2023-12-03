package com.miui.maml;

import android.text.TextUtils;
import com.miui.maml.ScreenElementRoot;

/* loaded from: classes2.dex */
public class SystemCommandListener implements ScreenElementRoot.OnExternCommandListener {
    private ScreenElementRoot mRoot;

    public SystemCommandListener(ScreenElementRoot screenElementRoot) {
        this.mRoot = screenElementRoot;
    }

    @Override // com.miui.maml.ScreenElementRoot.OnExternCommandListener
    public void onCommand(String str, Double d, String str2) {
        if (!"__clearResource".equals(str)) {
            if ("__requestUpdate".equals(str)) {
                this.mRoot.requestUpdate();
            }
        } else if (!TextUtils.isEmpty(str2)) {
            this.mRoot.getContext().mResourceManager.clear(str2);
        } else {
            ResourceManager resourceManager = this.mRoot.getContext().mResourceManager;
            ResourceManager.clear();
        }
    }
}
