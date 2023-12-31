package com.airbnb.lottie;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class TextDelegate {
    private final Map<String, String> stringMap = new HashMap();
    private boolean cacheText = true;
    private final LottieAnimationView animationView = null;
    private final LottieDrawable drawable = null;

    TextDelegate() {
    }

    private String getText(String str) {
        return str;
    }

    public final String getTextInternal(String str) {
        if (this.cacheText && this.stringMap.containsKey(str)) {
            return this.stringMap.get(str);
        }
        String text = getText(str);
        if (this.cacheText) {
            this.stringMap.put(str, text);
        }
        return text;
    }
}
