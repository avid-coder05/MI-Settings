package com.android.settings.slices;

import android.net.Uri;
import android.text.TextUtils;

/* loaded from: classes2.dex */
public class SliceData {
    private final String mFragmentClassName;
    private final int mIconResource;
    private final boolean mIsPublicSlice;
    private final String mKey;
    private final String mKeywords;
    private final String mPreferenceController;
    private final CharSequence mScreenTitle;
    private final int mSliceType;
    private final String mSummary;
    private final String mTitle;
    private final String mUnavailableSliceSubtitle;
    private final Uri mUri;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class Builder {
        private String mFragmentClassName;
        private int mIconResource;
        private boolean mIsPublicSlice;
        private String mKey;
        private String mKeywords;
        private String mPrefControllerClassName;
        private CharSequence mScreenTitle;
        private int mSliceType;
        private String mSummary;
        private String mTitle;
        private String mUnavailableSliceSubtitle;
        private Uri mUri;

        public SliceData build() {
            if (TextUtils.isEmpty(this.mKey)) {
                throw new InvalidSliceDataException("Key cannot be empty");
            }
            if (TextUtils.isEmpty(this.mTitle)) {
                throw new InvalidSliceDataException("Title cannot be empty");
            }
            if (TextUtils.isEmpty(this.mFragmentClassName)) {
                throw new InvalidSliceDataException("Fragment Name cannot be empty");
            }
            if (TextUtils.isEmpty(this.mPrefControllerClassName)) {
                throw new InvalidSliceDataException("Preference Controller cannot be empty");
            }
            return new SliceData(this);
        }

        public Builder setFragmentName(String str) {
            this.mFragmentClassName = str;
            return this;
        }

        public Builder setIcon(int i) {
            this.mIconResource = i;
            return this;
        }

        public Builder setIsPublicSlice(boolean z) {
            this.mIsPublicSlice = z;
            return this;
        }

        public Builder setKey(String str) {
            this.mKey = str;
            return this;
        }

        public Builder setKeywords(String str) {
            this.mKeywords = str;
            return this;
        }

        public Builder setPreferenceControllerClassName(String str) {
            this.mPrefControllerClassName = str;
            return this;
        }

        public Builder setScreenTitle(CharSequence charSequence) {
            this.mScreenTitle = charSequence;
            return this;
        }

        public Builder setSliceType(int i) {
            this.mSliceType = i;
            return this;
        }

        public Builder setSummary(String str) {
            this.mSummary = str;
            return this;
        }

        public Builder setTitle(String str) {
            this.mTitle = str;
            return this;
        }

        public Builder setUnavailableSliceSubtitle(String str) {
            this.mUnavailableSliceSubtitle = str;
            return this;
        }

        public Builder setUri(Uri uri) {
            this.mUri = uri;
            return this;
        }
    }

    /* loaded from: classes2.dex */
    public static class InvalidSliceDataException extends RuntimeException {
        public InvalidSliceDataException(String str) {
            super(str);
        }
    }

    private SliceData(Builder builder) {
        this.mKey = builder.mKey;
        this.mTitle = builder.mTitle;
        this.mSummary = builder.mSummary;
        this.mScreenTitle = builder.mScreenTitle;
        this.mKeywords = builder.mKeywords;
        this.mIconResource = builder.mIconResource;
        this.mFragmentClassName = builder.mFragmentClassName;
        this.mUri = builder.mUri;
        this.mPreferenceController = builder.mPrefControllerClassName;
        this.mSliceType = builder.mSliceType;
        this.mUnavailableSliceSubtitle = builder.mUnavailableSliceSubtitle;
        this.mIsPublicSlice = builder.mIsPublicSlice;
    }

    public boolean equals(Object obj) {
        if (obj instanceof SliceData) {
            return TextUtils.equals(this.mKey, ((SliceData) obj).mKey);
        }
        return false;
    }

    public String getFragmentClassName() {
        return this.mFragmentClassName;
    }

    public int getIconResource() {
        return this.mIconResource;
    }

    public String getKey() {
        return this.mKey;
    }

    public String getKeywords() {
        return this.mKeywords;
    }

    public String getPreferenceController() {
        return this.mPreferenceController;
    }

    public CharSequence getScreenTitle() {
        return this.mScreenTitle;
    }

    public int getSliceType() {
        return this.mSliceType;
    }

    public String getSummary() {
        return this.mSummary;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getUnavailableSliceSubtitle() {
        return this.mUnavailableSliceSubtitle;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public int hashCode() {
        return this.mKey.hashCode();
    }

    public boolean isPublicSlice() {
        return this.mIsPublicSlice;
    }
}
