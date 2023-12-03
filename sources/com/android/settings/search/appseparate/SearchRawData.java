package com.android.settings.search.appseparate;

/* loaded from: classes2.dex */
public class SearchRawData {
    public String extras;
    public int iconResId;
    public String intentAction;
    public String intentTargetClass;
    public String intentTargetPackage;
    public String intentUri;
    public String keywords;
    public String other;
    public String packageName;
    public String summaryOff;
    public String summaryOn;
    public String title;
    public String uriString;

    /* loaded from: classes2.dex */
    public static final class Builder {
        private String extras;
        private int iconResId;
        private String intentAction;
        private String intentTargetClass;
        private String intentTargetPackage;
        private String intentUri;
        private String keywords;
        private String other;
        private String packageName;
        private String summaryOff;
        private String summaryOn;
        private String title;
        private String uriString;

        public SearchRawData build() {
            return new SearchRawData(this);
        }

        public Builder setExtras(String str) {
            this.extras = str;
            return this;
        }

        public Builder setIconResId(int i) {
            this.iconResId = i;
            return this;
        }

        public Builder setIntentAction(String str) {
            this.intentAction = str;
            return this;
        }

        public Builder setIntentTargetClass(String str) {
            this.intentTargetClass = str;
            return this;
        }

        public Builder setIntentTargetPackage(String str) {
            this.intentTargetPackage = str;
            return this;
        }

        public void setIntentUri(String str) {
            this.intentUri = str;
        }

        public Builder setKeywords(String str) {
            this.keywords = str;
            return this;
        }

        public Builder setOther(String str) {
            this.other = str;
            return this;
        }

        public Builder setPackageName(String str) {
            this.packageName = str;
            return this;
        }

        public Builder setSummaryOff(String str) {
            this.summaryOff = str;
            return this;
        }

        public Builder setSummaryOn(String str) {
            this.summaryOn = str;
            return this;
        }

        public Builder setTitle(String str) {
            this.title = str;
            return this;
        }

        public Builder setUriString(String str) {
            this.uriString = str;
            return this;
        }
    }

    private SearchRawData(Builder builder) {
        this.title = builder.title;
        this.packageName = builder.packageName;
        this.summaryOn = builder.summaryOn;
        this.summaryOff = builder.summaryOff;
        this.keywords = builder.keywords;
        this.iconResId = builder.iconResId;
        this.intentAction = builder.intentAction;
        this.intentTargetPackage = builder.intentTargetPackage;
        this.intentTargetClass = builder.intentTargetClass;
        this.uriString = builder.uriString;
        this.extras = builder.extras;
        this.other = builder.other;
        this.intentUri = builder.intentUri;
    }
}
