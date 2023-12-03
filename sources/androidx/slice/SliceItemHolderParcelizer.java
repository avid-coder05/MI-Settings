package androidx.slice;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.slice.SliceItemHolder;
import androidx.versionedparcelable.VersionedParcel;
import androidx.versionedparcelable.VersionedParcelable;

/* loaded from: classes.dex */
public final class SliceItemHolderParcelizer {
    private static SliceItemHolder.SliceItemPool sBuilder = new SliceItemHolder.SliceItemPool();

    public static SliceItemHolder read(VersionedParcel parcel) {
        SliceItemHolder sliceItemHolder = sBuilder.get();
        sliceItemHolder.mVersionedParcelable = parcel.readVersionedParcelable(sliceItemHolder.mVersionedParcelable, 1);
        sliceItemHolder.mParcelable = parcel.readParcelable(sliceItemHolder.mParcelable, 2);
        sliceItemHolder.mStr = parcel.readString(sliceItemHolder.mStr, 3);
        sliceItemHolder.mInt = parcel.readInt(sliceItemHolder.mInt, 4);
        sliceItemHolder.mLong = parcel.readLong(sliceItemHolder.mLong, 5);
        sliceItemHolder.mBundle = parcel.readBundle(sliceItemHolder.mBundle, 6);
        return sliceItemHolder;
    }

    public static void write(SliceItemHolder obj, VersionedParcel parcel) {
        parcel.setSerializationFlags(true, true);
        VersionedParcelable versionedParcelable = obj.mVersionedParcelable;
        if (versionedParcelable != null) {
            parcel.writeVersionedParcelable(versionedParcelable, 1);
        }
        Parcelable parcelable = obj.mParcelable;
        if (parcelable != null) {
            parcel.writeParcelable(parcelable, 2);
        }
        String str = obj.mStr;
        if (str != null) {
            parcel.writeString(str, 3);
        }
        int i = obj.mInt;
        if (i != 0) {
            parcel.writeInt(i, 4);
        }
        long j = obj.mLong;
        if (0 != j) {
            parcel.writeLong(j, 5);
        }
        Bundle bundle = obj.mBundle;
        if (bundle != null) {
            parcel.writeBundle(bundle, 6);
        }
    }
}
