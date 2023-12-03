package miui.upnp.typedef.property;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import miui.upnp.typedef.datatype.DataType;

/* loaded from: classes4.dex */
public class PropertyDefinition implements Parcelable {
    private AllowedValueList allowedValueList;
    private AllowedValueRange allowedValueRange;
    private AllowedValueType allowedValueType = AllowedValueType.ANY;
    private DataType dataType;
    private String defaultValue;
    private String name;
    private boolean sendEvents;
    private static final String TAG = PropertyDefinition.class.getSimpleName();
    public static final Parcelable.Creator<PropertyDefinition> CREATOR = new Parcelable.Creator<PropertyDefinition>() { // from class: miui.upnp.typedef.property.PropertyDefinition.1
        @Override // android.os.Parcelable.Creator
        public PropertyDefinition createFromParcel(Parcel parcel) {
            return new PropertyDefinition(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public PropertyDefinition[] newArray(int i) {
            return new PropertyDefinition[i];
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: miui.upnp.typedef.property.PropertyDefinition$2  reason: invalid class name */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$miui$upnp$typedef$property$AllowedValueType;

        static {
            int[] iArr = new int[AllowedValueType.values().length];
            $SwitchMap$miui$upnp$typedef$property$AllowedValueType = iArr;
            try {
                iArr[AllowedValueType.ANY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$property$AllowedValueType[AllowedValueType.LIST.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$property$AllowedValueType[AllowedValueType.RANGE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public PropertyDefinition() {
    }

    public PropertyDefinition(Parcel parcel) {
        readFromParcel(parcel);
    }

    public PropertyDefinition(String str, DataType dataType) {
        setName(str);
        setDataType(dataType);
    }

    public PropertyDefinition(String str, DataType dataType, boolean z) {
        setName(str);
        setDataType(dataType);
        setSendEvents(z);
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
            PropertyDefinition propertyDefinition = (PropertyDefinition) obj;
            String str = this.name;
            if (str == null) {
                if (propertyDefinition.name != null) {
                    return false;
                }
            } else if (!str.equals(propertyDefinition.name)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public AllowedValueList getAllowedValueList() {
        return this.allowedValueList;
    }

    public AllowedValueRange getAllowedValueRange() {
        return this.allowedValueRange;
    }

    public AllowedValueType getAllowedValueType() {
        return this.allowedValueType;
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        String str = this.name;
        return 31 + (str == null ? 0 : str.hashCode());
    }

    public boolean isSendEvents() {
        return this.sendEvents;
    }

    public void readFromParcel(Parcel parcel) {
        boolean[] zArr = new boolean[1];
        parcel.readBooleanArray(zArr);
        this.sendEvents = zArr[0];
        this.name = parcel.readString();
        this.dataType = DataType.valueOf(parcel.readString());
        this.defaultValue = parcel.readString();
        AllowedValueType retrieveType = AllowedValueType.retrieveType(parcel.readInt());
        this.allowedValueType = retrieveType;
        int i = AnonymousClass2.$SwitchMap$miui$upnp$typedef$property$AllowedValueType[retrieveType.ordinal()];
        if (i == 2) {
            this.allowedValueList = (AllowedValueList) parcel.readParcelable(AllowedValueList.class.getClassLoader());
        } else if (i != 3) {
        } else {
            this.allowedValueRange = (AllowedValueRange) parcel.readParcelable(AllowedValueRange.class.getClassLoader());
        }
    }

    public void setAllowedValueList(AllowedValueList allowedValueList) {
        this.allowedValueType = AllowedValueType.LIST;
        this.allowedValueList = allowedValueList;
    }

    public void setAllowedValueRange(AllowedValueRange allowedValueRange) {
        this.allowedValueType = AllowedValueType.RANGE;
        this.allowedValueRange = allowedValueRange;
    }

    public void setAllowedValueType(AllowedValueType allowedValueType) {
        this.allowedValueType = allowedValueType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setDefaultValue(String str) {
        this.defaultValue = str;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setSendEvents(String str) {
        this.sendEvents = DataType.BooleanValueOf(str).booleanValue();
    }

    public void setSendEvents(boolean z) {
        this.sendEvents = z;
    }

    public boolean validate(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!this.dataType.getJavaDataType().equals(obj.getClass())) {
            Log.e(TAG, String.format("dataType is %s, value type is %s, invalid!", this.dataType.getJavaDataType().getSimpleName(), obj.getClass().getSimpleName()));
            return false;
        }
        AllowedValueList allowedValueList = this.allowedValueList;
        if (allowedValueList != null) {
            return allowedValueList.isValid(obj);
        }
        AllowedValueRange allowedValueRange = this.allowedValueRange;
        if (allowedValueRange != null) {
            return allowedValueRange.isValid(obj);
        }
        return true;
    }

    public Object valueOf(String str) {
        return this.dataType.toObjectValue(str);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBooleanArray(new boolean[]{this.sendEvents});
        parcel.writeString(this.name);
        parcel.writeString(this.dataType.toString());
        parcel.writeString(this.defaultValue);
        parcel.writeInt(this.allowedValueType.toInt());
        int i2 = AnonymousClass2.$SwitchMap$miui$upnp$typedef$property$AllowedValueType[this.allowedValueType.ordinal()];
        if (i2 == 2) {
            parcel.writeParcelable(this.allowedValueList, i);
        } else if (i2 != 3) {
        } else {
            parcel.writeParcelable(this.allowedValueRange, i);
        }
    }
}
