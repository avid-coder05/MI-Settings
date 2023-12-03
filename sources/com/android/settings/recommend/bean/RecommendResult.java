package com.android.settings.recommend.bean;

import java.util.List;

/* loaded from: classes2.dex */
public class RecommendResult {
    private List<RecommendPage> pages;
    private String strategy;
    private int verison;

    public List<RecommendPage> getPages() {
        return this.pages;
    }

    public String getStrategy() {
        return this.strategy;
    }

    public int getVerison() {
        return this.verison;
    }

    public void setPages(List<RecommendPage> list) {
        this.pages = list;
    }

    public void setStrategy(String str) {
        this.strategy = str;
    }

    public void setVerison(int i) {
        this.verison = i;
    }

    public String toString() {
        return "RecommendResult{verison=" + this.verison + ", strategy='" + this.strategy + "', pages=" + this.pages + '}';
    }
}
