package miui.sharesdk.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Map;
import miui.yellowpage.Tag;

/* loaded from: classes4.dex */
public final class InviteServerExtension implements Parcelable {
    public static final Parcelable.Creator<InviteServerExtension> CREATOR = new Parcelable.Creator<InviteServerExtension>() { // from class: miui.sharesdk.model.InviteServerExtension.1
        @Override // android.os.Parcelable.Creator
        public InviteServerExtension createFromParcel(Parcel parcel) {
            return new InviteServerExtension(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public InviteServerExtension[] newArray(int i) {
            return new InviteServerExtension[i];
        }
    };
    public final String extraInfo;
    public final String shareContent;

    public InviteServerExtension(Parcel parcel) {
        this.extraInfo = parcel.readString();
        this.shareContent = parcel.readString();
    }

    public InviteServerExtension(String str, String str2) {
        if (str == null || str2 == null) {
            throw new IllegalArgumentException("null == extraInfo || null == shareContentStr");
        }
        this.extraInfo = str;
        this.shareContent = str2;
    }

    public void addToRequestParam(Map<String, String> map) {
        map.put(Tag.TagYellowPage.EXTRA_INFO, this.extraInfo);
        map.put("shareContentStr", this.shareContent);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.extraInfo);
        parcel.writeString(this.shareContent);
    }
}
