package com.android.settings.sound.coolsound;

/* loaded from: classes2.dex */
public class RingtonePicker {
    private int drawableId;
    private boolean isDisable;
    private int resType;
    private String ringtoneTitle;
    private String ringtoneValue;

    public RingtonePicker(int i, int i2, String str) {
        this.resType = i;
        this.drawableId = i2;
        this.ringtoneTitle = str;
    }

    public int getDrawableId() {
        return this.drawableId;
    }

    public int getResType() {
        return this.resType;
    }

    public String getRingtoneTitle() {
        return this.ringtoneTitle;
    }

    public String getRingtoneValue() {
        return this.ringtoneValue;
    }

    public boolean isDisable() {
        return this.isDisable;
    }

    public void setDisable(boolean z) {
        this.isDisable = z;
    }

    public void setRingtoneValue(String str) {
        this.ringtoneValue = str;
    }
}
