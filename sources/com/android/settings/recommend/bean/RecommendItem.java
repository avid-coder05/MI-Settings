package com.android.settings.recommend.bean;

import android.os.Bundle;

/* loaded from: classes2.dex */
public class RecommendItem {
    private int calcRank;
    private int category;
    private int configRank;
    private Bundle extra;
    private String intent;
    private int intentType;
    private int itemFlag;
    private int targetPageIndex;
    private String targetPageTitle;

    public int getCalcRank() {
        return this.calcRank;
    }

    public int getCategory() {
        return this.category;
    }

    public int getConfigRank() {
        return this.configRank;
    }

    public Bundle getExtra() {
        return this.extra;
    }

    public String getIntent() {
        return this.intent;
    }

    public int getIntentType() {
        return this.intentType;
    }

    public int getItemFlag() {
        return this.itemFlag;
    }

    public int getTargetPageIndex() {
        return this.targetPageIndex;
    }

    public String getTargetPageTitle() {
        return this.targetPageTitle;
    }

    public void setCalcRank(int i) {
        this.calcRank = i;
    }

    public void setCategory(int i) {
        this.category = i;
    }

    public void setConfigRank(int i) {
        this.configRank = i;
    }

    public void setExtra(Bundle bundle) {
        this.extra = bundle;
    }

    public void setIntent(String str) {
        this.intent = str;
    }

    public void setIntentType(int i) {
        this.intentType = i;
    }

    public void setItemFlag(int i) {
        this.itemFlag = i;
    }

    public void setTargetPageIndex(int i) {
        this.targetPageIndex = i;
    }

    public void setTargetPageTitle(String str) {
        this.targetPageTitle = str;
    }

    public String toString() {
        return "RecommendItem{sourcePageIndex=" + this.targetPageIndex + ", sourcePageTitle='" + this.targetPageTitle + "', configRank=" + this.configRank + ", calcRank=" + this.calcRank + ", itemFlag=" + this.itemFlag + ", category=" + this.category + ", intent=" + this.intent + ", intentType=" + this.intentType + '}';
    }
}
