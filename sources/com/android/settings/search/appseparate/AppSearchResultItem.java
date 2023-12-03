package com.android.settings.search.appseparate;

import android.content.pm.ApplicationInfo;
import android.os.UserHandle;
import com.android.settings.search.SearchResultItem;

/* loaded from: classes2.dex */
public class AppSearchResultItem extends SearchResultItem {
    public static final int DISABLED = 1;
    public static final int INVISIBLE = 0;
    public static final int VISIBLE = 2;
    private final String appName;
    private String extras;
    private int iconResId;
    private final ApplicationInfo info;
    private String intentAction;
    private String intentTargetClass;
    private String intentTargetPackage;
    private String keywords;
    private String other;
    private String packageName;
    private String summaryOff;
    private String summaryOn;
    private String uriString;

    /* loaded from: classes2.dex */
    public static class Builder extends SearchResultItem.Builder {
        private String appName;
        private String extras;
        private String intentAction;
        private String intentTargetClass;
        private String intentTargetPackage;
        private String keywords;
        private int mIconResId;
        private ApplicationInfo mInfo;
        private String other;
        private String packageName;
        private String summaryOff;
        private String summaryOn;
        private String uriString;

        public Builder(int i) {
            super(i);
        }

        @Override // com.android.settings.search.SearchResultItem.Builder
        public AppSearchResultItem build() {
            return new AppSearchResultItem(this);
        }

        public Builder setAppInfo(ApplicationInfo applicationInfo) {
            this.mInfo = applicationInfo;
            return this;
        }

        public Builder setAppName(String str) {
            this.appName = str;
            return this;
        }

        public Builder setExtras(String str) {
            this.extras = str;
            return this;
        }

        public Builder setIconResId(int i) {
            this.mIconResId = i;
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

        @Override // com.android.settings.search.SearchResultItem.Builder
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

        public Builder setUriString(String str) {
            this.uriString = str;
            return this;
        }
    }

    AppSearchResultItem(Builder builder) {
        super(builder);
        this.info = builder.mInfo;
        this.iconResId = builder.mIconResId;
        this.packageName = builder.packageName;
        this.summaryOn = builder.summaryOn;
        this.summaryOff = builder.summaryOff;
        this.keywords = builder.keywords;
        this.intentAction = builder.intentAction;
        this.intentTargetPackage = builder.intentTargetPackage;
        this.intentTargetClass = builder.intentTargetClass;
        this.uriString = builder.uriString;
        this.extras = builder.extras;
        this.other = builder.other;
        this.appName = builder.appName;
    }

    public String getAppName() {
        return this.appName;
    }

    public UserHandle getAppUserHandle() {
        return new UserHandle(UserHandle.getUserId(this.info.uid));
    }

    public int getIconResId() {
        return this.iconResId;
    }

    public ApplicationInfo getInfo() {
        return this.info;
    }
}
