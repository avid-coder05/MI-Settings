package com.android.settings.search;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes2.dex */
public class RemoteSearchResult implements Parcelable {
    public static final Parcelable.Creator<RemoteSearchResult> CREATOR = new Parcelable.Creator<RemoteSearchResult>() { // from class: com.android.settings.search.RemoteSearchResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteSearchResult createFromParcel(Parcel parcel) {
            return new RemoteSearchResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteSearchResult[] newArray(int i) {
            return new RemoteSearchResult[i];
        }
    };
    private final String path;
    private final int score;
    private final String searchOrigin;
    private final String title;

    protected RemoteSearchResult(Parcel parcel) {
        this.title = parcel.readString();
        this.path = parcel.readString();
        this.score = parcel.readInt();
        this.searchOrigin = parcel.readString();
    }

    public RemoteSearchResult(String str) {
        this.title = "";
        this.path = "";
        this.score = Integer.MIN_VALUE;
        this.searchOrigin = str;
    }

    public RemoteSearchResult(String str, String str2, int i, String str3) {
        this.title = str;
        this.path = str2;
        this.score = i;
        this.searchOrigin = str3;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getPath() {
        return this.path;
    }

    public int getScore() {
        return this.score;
    }

    public String getSearchOrigin() {
        return this.searchOrigin;
    }

    public String getTitle() {
        return this.title;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.path);
        parcel.writeInt(this.score);
        parcel.writeString(this.searchOrigin);
    }
}
