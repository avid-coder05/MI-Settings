package com.android.settings.magicwindow;

import android.graphics.drawable.Drawable;
import java.text.Collator;

/* loaded from: classes.dex */
public class ChildAppItemInfo implements Comparable<ChildAppItemInfo> {
    private Drawable mAppIcon;
    private String mAppName;
    private boolean mIsMagicWinEnabled;
    private String mPkg;

    public ChildAppItemInfo(String str, String str2, Drawable drawable, boolean z) {
        this.mPkg = str;
        this.mAppName = str2;
        this.mAppIcon = drawable;
        this.mIsMagicWinEnabled = z;
    }

    @Override // java.lang.Comparable
    public int compareTo(ChildAppItemInfo childAppItemInfo) {
        return !getAppName().equals(childAppItemInfo.getAppName()) ? Collator.getInstance().compare(getAppName(), childAppItemInfo.getAppName()) : Collator.getInstance().compare(getPkg(), childAppItemInfo.getPkg());
    }

    public boolean equals(Object obj) {
        return obj instanceof ChildAppItemInfo ? getPkg().equals(((ChildAppItemInfo) obj).getPkg()) : super.equals(obj);
    }

    public Drawable getAppIcon() {
        return this.mAppIcon;
    }

    public String getAppName() {
        return this.mAppName;
    }

    public boolean getMagicWinEnabled() {
        return this.mIsMagicWinEnabled;
    }

    public String getPkg() {
        return this.mPkg;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public void setMagicWinEnabled(boolean z) {
        this.mIsMagicWinEnabled = z;
    }

    public String toString() {
        return "[AppName:" + this.mAppName + " Pkg:" + this.mPkg + " MagicWinEnabled:" + this.mIsMagicWinEnabled + "]";
    }
}
