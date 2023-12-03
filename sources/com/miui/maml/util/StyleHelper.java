package com.miui.maml.util;

import android.text.TextUtils;
import com.miui.maml.StylesManager;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class StyleHelper {
    public static String getAttr(Element element, String str, StylesManager.Style style) {
        String attribute = element.getAttribute(str);
        if (TextUtils.isEmpty(attribute)) {
            if (style != null) {
                attribute = style.getAttr(str);
            }
            return attribute != null ? attribute : "";
        }
        return attribute;
    }
}
