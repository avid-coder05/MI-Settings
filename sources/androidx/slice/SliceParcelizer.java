package androidx.slice;

import androidx.versionedparcelable.VersionedParcel;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class SliceParcelizer {
    public static Slice read(VersionedParcel parcel) {
        Slice slice = new Slice();
        slice.mSpec = (SliceSpec) parcel.readVersionedParcelable(slice.mSpec, 1);
        slice.mItems = (SliceItem[]) parcel.readArray(slice.mItems, 2);
        slice.mHints = (String[]) parcel.readArray(slice.mHints, 3);
        slice.mUri = parcel.readString(slice.mUri, 4);
        slice.onPostParceling();
        return slice;
    }

    public static void write(Slice obj, VersionedParcel parcel) {
        parcel.setSerializationFlags(true, false);
        obj.onPreParceling(parcel.isStream());
        SliceSpec sliceSpec = obj.mSpec;
        if (sliceSpec != null) {
            parcel.writeVersionedParcelable(sliceSpec, 1);
        }
        if (!Arrays.equals(Slice.NO_ITEMS, obj.mItems)) {
            parcel.writeArray(obj.mItems, 2);
        }
        if (!Arrays.equals(Slice.NO_HINTS, obj.mHints)) {
            parcel.writeArray(obj.mHints, 3);
        }
        String str = obj.mUri;
        if (str != null) {
            parcel.writeString(str, 4);
        }
    }
}
