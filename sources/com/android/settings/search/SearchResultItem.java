package com.android.settings.search;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/* loaded from: classes2.dex */
public class SearchResultItem implements Comparable<SearchResultItem> {
    public static final SearchResultItem EMPTY = new SearchResultItem(1);
    public static final int SEARCH_EMPTY = 1;
    public static final int SEARCH_ITEM_NORMAL = 0;
    public static final int SEARCH_SEPARATE_APP = 2;
    public String category;
    public boolean checkbox;
    public Drawable globalSearchIcon;
    public String icon;
    public Intent intent;
    public boolean isGlobalSearch;
    public String keywords;
    public String path;
    public String pkg;
    public String resource;
    public double score;
    public int status;
    public String summary;
    public String title;
    public final int type;

    /* loaded from: classes2.dex */
    public static class Builder {
        private String category;
        private boolean checkbox;
        private Drawable globalSearchIcon;
        private String icon;
        private Intent intent;
        private boolean isGlobalSearch;
        private String keywords;
        private String path;
        private String pkg;
        private String resource;
        private double score;
        private int status;
        private String summary;
        private String title;
        private final int type;

        public Builder(int i) {
            this.type = i;
        }

        public SearchResultItem build() {
            return new SearchResultItem(this);
        }

        public Builder setCategory(String str) {
            this.category = str;
            return this;
        }

        public Builder setCheckbox(boolean z) {
            this.checkbox = z;
            return this;
        }

        public Builder setGlobalSearch(boolean z) {
            this.isGlobalSearch = z;
            return this;
        }

        public Builder setGlobalSearchIcon(Drawable drawable) {
            this.globalSearchIcon = drawable;
            return this;
        }

        public Builder setIcon(String str) {
            this.icon = str;
            return this;
        }

        public Builder setIntent(Intent intent) {
            this.intent = intent;
            return this;
        }

        public Builder setKeywords(String str) {
            this.keywords = str;
            return this;
        }

        public Builder setPath(String str) {
            this.path = str;
            return this;
        }

        public Builder setPkg(String str) {
            this.pkg = str;
            return this;
        }

        public Builder setResource(String str) {
            this.resource = str;
            return this;
        }

        public Builder setScore(double d) {
            this.score = d;
            return this;
        }

        public Builder setStatus(int i) {
            this.status = i;
            return this;
        }

        public Builder setSummary(String str) {
            this.summary = str;
            return this;
        }

        public Builder setTitle(String str) {
            this.title = str;
            return this;
        }
    }

    public SearchResultItem(int i) {
        this.type = i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SearchResultItem(Builder builder) {
        this.pkg = builder.pkg;
        this.resource = builder.resource;
        this.title = builder.title;
        this.category = builder.category;
        this.path = builder.path;
        this.keywords = builder.keywords;
        this.summary = builder.summary;
        this.icon = builder.icon;
        this.checkbox = builder.checkbox;
        this.intent = builder.intent;
        this.status = builder.status;
        this.type = builder.type;
        this.score = builder.score;
        this.isGlobalSearch = builder.isGlobalSearch;
        this.globalSearchIcon = builder.globalSearchIcon;
    }

    @Override // java.lang.Comparable
    public int compareTo(SearchResultItem searchResultItem) {
        if (searchResultItem == null) {
            return -1;
        }
        return Double.compare(searchResultItem.score, this.score);
    }
}
