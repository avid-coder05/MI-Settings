package miui.upnp.typedef.property;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.error.UpnpError;
import miui.upnp.typedef.exception.UpnpException;

/* loaded from: classes4.dex */
public class AllowedValueRange implements Parcelable {
    private DataType dataType;
    private Object maxValue;
    private Object minValue;
    private static final String TAG = AllowedValueRange.class.getSimpleName();
    public static final Parcelable.Creator<AllowedValueRange> CREATOR = new Parcelable.Creator<AllowedValueRange>() { // from class: miui.upnp.typedef.property.AllowedValueRange.1
        @Override // android.os.Parcelable.Creator
        public AllowedValueRange createFromParcel(Parcel parcel) {
            return new AllowedValueRange(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public AllowedValueRange[] newArray(int i) {
            return new AllowedValueRange[i];
        }
    };

    private AllowedValueRange() {
    }

    public AllowedValueRange(Parcel parcel) {
        readFromParcel(parcel);
    }

    public static AllowedValueRange create(DataType dataType, Object obj, Object obj2) throws UpnpException {
        if (dataType.getJavaDataType().isInstance(obj)) {
            if (dataType.getJavaDataType().isInstance(obj2)) {
                if (dataType.validate(obj, obj2)) {
                    AllowedValueRange allowedValueRange = new AllowedValueRange();
                    allowedValueRange.dataType = dataType;
                    allowedValueRange.minValue = obj;
                    allowedValueRange.maxValue = obj2;
                    return allowedValueRange;
                }
                throw new UpnpException(UpnpError.INVALID_ARGUMENT, "min >= max");
            }
            throw new UpnpException(UpnpError.INVALID_ARGUMENT, "max dataType invalid");
        }
        throw new UpnpException(UpnpError.INVALID_ARGUMENT, "min dataType invalid");
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Object getMaxValue() {
        return this.maxValue;
    }

    public Object getMinValue() {
        return this.minValue;
    }

    public boolean isValid(Object obj) {
        return this.dataType.validate(this.minValue, obj, this.maxValue);
    }

    public void readFromParcel(Parcel parcel) {
        DataType valueOf = DataType.valueOf(parcel.readString());
        this.dataType = valueOf;
        this.minValue = valueOf.toObjectValue(parcel.readString());
        this.maxValue = this.dataType.toObjectValue(parcel.readString());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.dataType.toString());
        parcel.writeString(this.dataType.toStringValue(this.minValue));
        parcel.writeString(this.dataType.toStringValue(this.maxValue));
    }
}
