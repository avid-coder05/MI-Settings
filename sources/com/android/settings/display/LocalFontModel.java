package com.android.settings.display;

import android.graphics.Typeface;
import java.io.File;
import java.util.List;

/* loaded from: classes.dex */
public class LocalFontModel {
    private Typeface.Builder builder;
    private String contentUri;
    private File fontAssert;
    private List<Integer> fontWeight;
    private String id;
    private boolean isUsing;
    private boolean isVariable;
    private String title;
    private Typeface typeface;

    public LocalFontModel(String str, String str2, String str3, boolean z) {
        this.id = str;
        this.title = str2;
        this.contentUri = str3;
        this.isUsing = z;
    }

    public Typeface.Builder getBuilder() {
        return this.builder;
    }

    public String getContentUri() {
        return this.contentUri;
    }

    public File getFontAssert() {
        return this.fontAssert;
    }

    public List<Integer> getFontWeight() {
        return this.fontWeight;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public Typeface getTypeface() {
        return this.typeface;
    }

    public boolean isUsing() {
        return this.isUsing;
    }

    public boolean isVariable() {
        return this.isVariable;
    }

    public void setBuilder(Typeface.Builder builder) {
        this.builder = builder;
    }

    public void setFontAssert(File file) {
        this.fontAssert = file;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public void setUsing(boolean z) {
        this.isUsing = z;
    }

    public String toString() {
        return "LocalFontModel{id='" + this.id + "', title='" + this.title + "', contentUri='" + this.contentUri + "', isUsing=" + this.isUsing + ", fontAssert=" + this.fontAssert + ", typeface=" + this.typeface + ", isVariable=" + this.isVariable + ", fontWeight=" + this.fontWeight + '}';
    }
}
