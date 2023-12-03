package miui.upnp.typedef.property;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/* loaded from: classes4.dex */
public class Property implements Parcelable {
    private PropertyDefinition definition;
    private volatile PropertyValue value;
    private static final String TAG = Property.class.getSimpleName();
    public static final Parcelable.Creator<Property> CREATOR = new Parcelable.Creator<Property>() { // from class: miui.upnp.typedef.property.Property.1
        @Override // android.os.Parcelable.Creator
        public Property createFromParcel(Parcel parcel) {
            return new Property(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public Property[] newArray(int i) {
            return new Property[i];
        }
    };

    public Property() {
    }

    public Property(Parcel parcel) {
        readFromParcel(parcel);
    }

    public Property(PropertyDefinition propertyDefinition, Object obj) {
        init(propertyDefinition, PropertyValue.create(obj == null ? propertyDefinition.getDataType().createObjectValue() : obj));
    }

    private void init(PropertyDefinition propertyDefinition, PropertyValue propertyValue) {
        if (propertyValue == null) {
            propertyValue = PropertyValue.create(propertyDefinition.getDataType().createObjectValue());
        }
        this.definition = propertyDefinition;
        this.value = propertyValue;
    }

    private boolean isMultipleValue(Object obj) {
        return this.definition.getAllowedValueType() == AllowedValueType.LIST && (obj instanceof String) && ((String) obj).split(",").length > 1;
    }

    public void cleanState() {
        this.value.cleanState();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getCurrentStringValue() {
        return this.definition.getDataType().toStringValue(this.value.getValue());
    }

    public Object getCurrentValue() {
        return this.value.getValue();
    }

    public PropertyDefinition getDefinition() {
        return this.definition;
    }

    public String getOldStringValue() {
        return this.definition.getDataType().toStringValue(this.value.getOldValue());
    }

    public Object getOldValue() {
        return this.value.getOldValue();
    }

    public PropertyValue getPropertyValue() {
        return this.value;
    }

    public boolean isChanged() {
        return this.value.isChanged();
    }

    public void readFromParcel(Parcel parcel) {
        this.definition = (PropertyDefinition) parcel.readParcelable(PropertyDefinition.class.getClassLoader());
        this.value = (PropertyValue) parcel.readParcelable(PropertyValue.class.getClassLoader());
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0069  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean setDataValue(java.lang.Object r9) {
        /*
            r8 = this;
            r0 = 0
            r1 = 1
            if (r9 != 0) goto Ld
            java.lang.String r8 = miui.upnp.typedef.property.Property.TAG
            java.lang.String r9 = "newValue is null"
            android.util.Log.e(r8, r9)
            goto L87
        Ld:
            miui.upnp.typedef.property.PropertyDefinition r2 = r8.definition
            miui.upnp.typedef.property.AllowedValueType r2 = r2.getAllowedValueType()
            miui.upnp.typedef.property.AllowedValueType r3 = miui.upnp.typedef.property.AllowedValueType.LIST
            if (r2 != r3) goto L65
            boolean r2 = r9 instanceof java.lang.String
            if (r2 == 0) goto L65
            r2 = r9
            java.lang.String r2 = (java.lang.String) r2
            java.lang.String r3 = ","
            java.lang.String[] r3 = r2.split(r3)
            int r4 = r3.length
            if (r4 <= r1) goto L65
            java.lang.String r4 = miui.upnp.typedef.property.Property.TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "multiple value: "
            r5.append(r6)
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            android.util.Log.d(r4, r2)
            int r2 = r3.length
            r4 = r0
        L3f:
            if (r4 >= r2) goto L63
            r5 = r3[r4]
            miui.upnp.typedef.property.PropertyDefinition r6 = r8.definition
            boolean r6 = r6.validate(r5)
            if (r6 != 0) goto L5b
            java.lang.String r6 = miui.upnp.typedef.property.Property.TAG
            java.lang.Object[] r7 = new java.lang.Object[r1]
            r7[r0] = r5
            java.lang.String r5 = "invalid value: %s, skip it!"
            java.lang.String r5 = java.lang.String.format(r5, r7)
            android.util.Log.e(r6, r5)
            goto L60
        L5b:
            miui.upnp.typedef.property.PropertyValue r6 = r8.value
            r6.addMultipleValue(r5)
        L60:
            int r4 = r4 + 1
            goto L3f
        L63:
            r2 = r1
            goto L66
        L65:
            r2 = r0
        L66:
            if (r2 == 0) goto L69
            goto L86
        L69:
            miui.upnp.typedef.property.PropertyDefinition r2 = r8.definition
            boolean r2 = r2.validate(r9)
            if (r2 != 0) goto L81
            java.lang.String r8 = miui.upnp.typedef.property.Property.TAG
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r0] = r9
            java.lang.String r9 = "invalid value: %s"
            java.lang.String r9 = java.lang.String.format(r9, r1)
            android.util.Log.e(r8, r9)
            goto L87
        L81:
            miui.upnp.typedef.property.PropertyValue r8 = r8.value
            r8.update(r9)
        L86:
            r0 = r1
        L87:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.upnp.typedef.property.Property.setDataValue(java.lang.Object):boolean");
    }

    public boolean setDataValueByString(String str, boolean z) {
        Object objectValue = this.definition.getDataType().toObjectValue(str);
        if (objectValue == null) {
            if (z) {
                return true;
            }
            Log.d(TAG, "value is null");
        } else if (setDataValue(objectValue)) {
            return true;
        } else {
            Log.e(TAG, String.format("%s setDataValue failed: %s(%s), dataType is: %s", this.definition.getName(), str, objectValue.getClass().getSimpleName(), this.definition.getDataType().getStringType()));
        }
        return false;
    }

    public void setDefinition(PropertyDefinition propertyDefinition) {
        this.definition = propertyDefinition;
        this.value = PropertyValueUtil.createByType(propertyDefinition.getDataType().getJavaDataType());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.definition, i);
        parcel.writeParcelable(this.value, i);
    }
}
