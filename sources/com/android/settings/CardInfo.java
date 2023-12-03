package com.android.settings;

import android.view.View;

/* loaded from: classes.dex */
public class CardInfo {
    private int checkedIconResId;
    private int iconResId;
    private boolean isChecked;
    private boolean isDisable;
    private View.OnClickListener onClickListener;
    private int titleResId;
    private int valueResId;

    public CardInfo(int i, int i2, int i3) {
        this.iconResId = i;
        this.titleResId = i2;
        this.valueResId = i3;
    }

    public int getCheckedIconResId() {
        return this.checkedIconResId;
    }

    public int getIconResId() {
        return this.iconResId;
    }

    public View.OnClickListener getOnClickListener() {
        return this.onClickListener;
    }

    public int getTitleResId() {
        return this.titleResId;
    }

    public int getValueResId() {
        return this.valueResId;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public boolean isDisable() {
        return this.isDisable;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
    }

    public void setCheckedIconResId(int i) {
        this.checkedIconResId = i;
    }

    public void setDisable(boolean z) {
        this.isDisable = z;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setValueResId(int i) {
        this.valueResId = i;
    }
}
