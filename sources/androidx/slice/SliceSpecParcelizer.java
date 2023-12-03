package androidx.slice;

import androidx.versionedparcelable.VersionedParcel;

/* loaded from: classes.dex */
public final class SliceSpecParcelizer {
    public static SliceSpec read(VersionedParcel parcel) {
        SliceSpec sliceSpec = new SliceSpec();
        sliceSpec.mType = parcel.readString(sliceSpec.mType, 1);
        sliceSpec.mRevision = parcel.readInt(sliceSpec.mRevision, 2);
        return sliceSpec;
    }

    public static void write(SliceSpec obj, VersionedParcel parcel) {
        parcel.setSerializationFlags(true, false);
        parcel.writeString(obj.mType, 1);
        int i = obj.mRevision;
        if (1 != i) {
            parcel.writeInt(i, 2);
        }
    }
}
