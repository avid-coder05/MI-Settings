package miui.sharesdk.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Map;

/* loaded from: classes4.dex */
public final class ShareResource implements Parcelable {
    public static final Parcelable.Creator<ShareResource> CREATOR = new Parcelable.Creator<ShareResource>() { // from class: miui.sharesdk.model.ShareResource.1
        @Override // android.os.Parcelable.Creator
        public ShareResource createFromParcel(Parcel parcel) {
            return new ShareResource(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public ShareResource[] newArray(int i) {
            return new ShareResource[i];
        }
    };
    private final String resId;
    private final String subResId;

    protected ShareResource(Parcel parcel) {
        this.resId = parcel.readString();
        this.subResId = parcel.readString();
    }

    public ShareResource(String str, String str2) {
        this.resId = str;
        this.subResId = str2;
    }

    public void addToRequestParam(Map<String, String> map) {
        map.put("resId", this.resId);
        map.put("subResId", this.subResId);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.resId);
        parcel.writeString(this.subResId);
    }
}
