package com.android.settings.haptic.data;

/* loaded from: classes.dex */
public class HapticResource {
    private int contentDescription;
    private int showRes;
    private int showType;
    private int subTitleRes;
    private int videoBgRes;

    public HapticResource(int i, int i2, int i3, int i4, int i5) {
        this.showRes = i;
        this.subTitleRes = i2;
        this.videoBgRes = i3;
        this.contentDescription = i4;
        this.showType = i5;
    }

    public int getContentDescription() {
        return this.contentDescription;
    }

    public int getShowRes() {
        return this.showRes;
    }

    public int getSubTitleRes() {
        return this.subTitleRes;
    }

    public int getVideoBgRes() {
        return this.videoBgRes;
    }
}
