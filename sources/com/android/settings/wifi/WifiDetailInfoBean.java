package com.android.settings.wifi;

/* loaded from: classes2.dex */
public class WifiDetailInfoBean {
    private int iconNameId;
    private String summary;
    private int titleId;

    public WifiDetailInfoBean(int i, int i2, String str) {
        this.iconNameId = i;
        this.titleId = i2;
        this.summary = str;
    }

    public int getIconNameId() {
        return this.iconNameId;
    }

    public String getSummary() {
        return this.summary;
    }

    public int getTitleId() {
        return this.titleId;
    }
}
