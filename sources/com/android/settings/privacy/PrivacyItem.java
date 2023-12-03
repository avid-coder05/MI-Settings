package com.android.settings.privacy;

import android.graphics.drawable.Drawable;

/* loaded from: classes2.dex */
public class PrivacyItem {
    public Drawable drawable;
    public boolean enable;
    public String label;
    public String packageName;

    public String toString() {
        return "PrivacyItem [packageName=" + this.packageName + ", label=" + this.label + ", drawable=" + this.drawable + ", enable=" + this.enable + "]";
    }
}
