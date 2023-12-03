package com.android.settings.recommend.bean;

import android.content.Intent;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public class IndexDetail {
    private AbstractPreferenceController controller;
    private String intent;
    private Intent rawIntent;
    private int resId;

    public IndexDetail(int i) {
        this.resId = i;
    }

    public IndexDetail(int i, String str) {
        this.resId = i;
        this.intent = str;
    }

    public IndexDetail(int i, String str, AbstractPreferenceController abstractPreferenceController) {
        this.resId = i;
        this.intent = str;
        this.controller = abstractPreferenceController;
    }

    public AbstractPreferenceController getController() {
        return this.controller;
    }

    public String getIntent() {
        return this.intent;
    }

    public Intent getRawIntent() {
        return this.rawIntent;
    }

    public int getResId() {
        return this.resId;
    }

    public void setController(AbstractPreferenceController abstractPreferenceController) {
        this.controller = abstractPreferenceController;
    }

    public void setIntent(String str) {
        this.intent = str;
    }

    public void setRawIntent(Intent intent) {
        this.rawIntent = intent;
    }

    public void setResId(int i) {
        this.resId = i;
    }
}
