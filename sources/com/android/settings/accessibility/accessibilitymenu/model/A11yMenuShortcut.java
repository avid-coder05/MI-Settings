package com.android.settings.accessibility.accessibilitymenu.model;

import android.util.Log;
import com.android.settings.accessibility.accessibilitymenu.utils.A11yMenuUtils;

/* loaded from: classes.dex */
public final class A11yMenuShortcut {
    public int imageSrc;
    public int imgContentDescription;
    public int labelText;
    public int shortcutId;

    public A11yMenuShortcut(int i) {
        this.shortcutId = i;
        if (i < 0 || i > 12) {
            this.shortcutId = 0;
            Log.e("A11yMenuShortcut", "setId to default UNSPECIFIED_ID as id is invalid.");
        }
        A11yMenuUtils.setShortcutResByShortcutId(this.shortcutId, this);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof A11yMenuShortcut) && this.shortcutId == ((A11yMenuShortcut) obj).shortcutId;
    }

    public final int hashCode() {
        return this.shortcutId;
    }
}
