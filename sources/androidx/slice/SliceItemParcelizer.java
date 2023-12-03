package androidx.slice;

import androidx.versionedparcelable.VersionedParcel;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class SliceItemParcelizer {
    public static SliceItem read(VersionedParcel parcel) {
        SliceItem sliceItem = new SliceItem();
        sliceItem.mHints = (String[]) parcel.readArray(sliceItem.mHints, 1);
        sliceItem.mFormat = parcel.readString(sliceItem.mFormat, 2);
        sliceItem.mSubType = parcel.readString(sliceItem.mSubType, 3);
        sliceItem.mHolder = (SliceItemHolder) parcel.readVersionedParcelable(sliceItem.mHolder, 4);
        sliceItem.onPostParceling();
        return sliceItem;
    }

    public static void write(SliceItem obj, VersionedParcel parcel) {
        parcel.setSerializationFlags(true, true);
        obj.onPreParceling(parcel.isStream());
        if (!Arrays.equals(Slice.NO_HINTS, obj.mHints)) {
            parcel.writeArray(obj.mHints, 1);
        }
        if (!"text".equals(obj.mFormat)) {
            parcel.writeString(obj.mFormat, 2);
        }
        String str = obj.mSubType;
        if (str != null) {
            parcel.writeString(str, 3);
        }
        parcel.writeVersionedParcelable(obj.mHolder, 4);
    }
}
