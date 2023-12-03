package miui.upnp.typedef.field;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.datatype.DataType;

/* loaded from: classes4.dex */
public class FieldDefinition implements Parcelable {
    public static final Parcelable.Creator<FieldDefinition> CREATOR = new Parcelable.Creator<FieldDefinition>() { // from class: miui.upnp.typedef.field.FieldDefinition.1
        @Override // android.os.Parcelable.Creator
        public FieldDefinition createFromParcel(Parcel parcel) {
            return new FieldDefinition(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public FieldDefinition[] newArray(int i) {
            return new FieldDefinition[i];
        }
    };
    private DataType dataType;
    private String name;

    private FieldDefinition() {
    }

    public FieldDefinition(Parcel parcel) {
        readFromParcel(parcel);
    }

    public FieldDefinition(String str, DataType dataType) {
        this.name = str;
        this.dataType = dataType;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            FieldDefinition fieldDefinition = (FieldDefinition) obj;
            String str = this.name;
            if (str == null) {
                if (fieldDefinition.name != null) {
                    return false;
                }
            } else if (!str.equals(fieldDefinition.name)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        String str = this.name;
        return 31 + (str == null ? 0 : str.hashCode());
    }

    public void readFromParcel(Parcel parcel) {
        this.name = parcel.readString();
        this.dataType = DataType.valueOf(parcel.readString());
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setName(String str) {
        this.name = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.dataType.toString());
    }
}
