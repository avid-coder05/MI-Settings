package com.iqiyi.android.qigsaw.core.splitdownload;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public final class DownloadRequest implements Parcelable {
    public static final Parcelable.Creator<DownloadRequest> CREATOR = new Parcelable.Creator<DownloadRequest>() { // from class: com.iqiyi.android.qigsaw.core.splitdownload.DownloadRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DownloadRequest createFromParcel(Parcel parcel) {
            return new DownloadRequest(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DownloadRequest[] newArray(int i) {
            return new DownloadRequest[i];
        }
    };
    private final String fileDir;
    private final String fileMD5;
    private final String fileName;
    private final String moduleName;
    private final long size;
    private final String url;

    /* loaded from: classes2.dex */
    public static class Builder {
        private String fileDir;
        private String fileMD5;
        private String fileName;
        private String moduleName;
        private long size;
        private String url;

        public DownloadRequest build() {
            return new DownloadRequest(this);
        }

        public Builder fileDir(String str) {
            this.fileDir = str;
            return this;
        }

        public Builder fileMD5(String str) {
            this.fileMD5 = str;
            return this;
        }

        public Builder fileName(String str) {
            this.fileName = str;
            return this;
        }

        public Builder moduleName(String str) {
            this.moduleName = str;
            return this;
        }

        public Builder size(long j) {
            this.size = j;
            return this;
        }

        public Builder url(String str) {
            this.url = str;
            return this;
        }
    }

    private DownloadRequest(Parcel parcel) {
        this.url = parcel.readString();
        this.fileDir = parcel.readString();
        this.fileName = parcel.readString();
        this.moduleName = parcel.readString();
        this.fileMD5 = parcel.readString();
        this.size = parcel.readLong();
    }

    private DownloadRequest(Builder builder) {
        this.fileDir = builder.fileDir;
        this.url = builder.url;
        this.fileName = builder.fileName;
        this.moduleName = builder.moduleName;
        this.fileMD5 = builder.fileMD5;
        this.size = builder.size;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getFileDir() {
        return this.fileDir;
    }

    public String getFileMD5() {
        return this.fileMD5;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public long getSize() {
        return this.size;
    }

    public String getUrl() {
        return this.url;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.url);
        parcel.writeString(this.fileDir);
        parcel.writeString(this.fileName);
        parcel.writeString(this.moduleName);
        parcel.writeString(this.fileMD5);
        parcel.writeLong(this.size);
    }
}
