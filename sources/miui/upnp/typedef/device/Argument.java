package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.field.FieldList;

/* loaded from: classes4.dex */
public class Argument implements Parcelable {
    public static final Parcelable.Creator<Argument> CREATOR = new Parcelable.Creator<Argument>() { // from class: miui.upnp.typedef.device.Argument.1
        @Override // android.os.Parcelable.Creator
        public Argument createFromParcel(Parcel parcel) {
            return new Argument(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public Argument[] newArray(int i) {
            return new Argument[i];
        }
    };
    private FieldList fields = new FieldList();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: miui.upnp.typedef.device.Argument$2  reason: invalid class name */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$miui$upnp$typedef$device$Argument$Direction;

        static {
            int[] iArr = new int[Direction.values().length];
            $SwitchMap$miui$upnp$typedef$device$Argument$Direction = iArr;
            try {
                iArr[Direction.IN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$device$Argument$Direction[Direction.OUT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    /* loaded from: classes4.dex */
    public enum Direction {
        UNDEFINED,
        IN,
        OUT;

        private static final String STR_in = "in";
        private static final String STR_out = "out";
        private static final String STR_undefined = "undefined";

        public static Direction retrieveType(String str) {
            return str.equals(STR_undefined) ? UNDEFINED : str.equals(STR_in) ? IN : str.equals(STR_out) ? OUT : UNDEFINED;
        }

        @Override // java.lang.Enum
        public String toString() {
            int i = AnonymousClass2.$SwitchMap$miui$upnp$typedef$device$Argument$Direction[ordinal()];
            return i != 1 ? i != 2 ? STR_undefined : STR_out : STR_in;
        }
    }

    public Argument() {
        initialize();
    }

    public Argument(Parcel parcel) {
        initialize();
        readFromParcel(parcel);
    }

    public Argument(String str, String str2, String str3) {
        initialize();
        setName(str);
        setDirection(str2);
        setRelatedProperty(str3);
    }

    public Argument(String str, Direction direction, String str2) {
        initialize();
        setName(str);
        setDirection(direction);
        setRelatedProperty(str2);
    }

    private void initialize() {
        this.fields.initField(ArgumentDefinition.Name, null);
        this.fields.initField(ArgumentDefinition.Direction, null);
        this.fields.initField(ArgumentDefinition.RelatedProperty, null);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Direction getDirection() {
        return Direction.retrieveType((String) this.fields.getValue(ArgumentDefinition.Direction));
    }

    public String getName() {
        return (String) this.fields.getValue(ArgumentDefinition.Name);
    }

    public String getRelatedProperty() {
        return (String) this.fields.getValue(ArgumentDefinition.RelatedProperty);
    }

    public void readFromParcel(Parcel parcel) {
        this.fields = (FieldList) parcel.readParcelable(FieldList.class.getClassLoader());
    }

    public boolean setDirection(String str) {
        return this.fields.setValue(ArgumentDefinition.Direction, str);
    }

    public boolean setDirection(Direction direction) {
        return setDirection(direction.toString());
    }

    public boolean setName(String str) {
        return this.fields.setValue(ArgumentDefinition.Name, str);
    }

    public boolean setRelatedProperty(String str) {
        return this.fields.setValue(ArgumentDefinition.RelatedProperty, str);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.fields, i);
    }
}
