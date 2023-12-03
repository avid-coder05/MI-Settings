package miui.upnp.typedef.device.urn;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes4.dex */
public class Urn implements Parcelable {
    public static final Parcelable.Creator<Urn> CREATOR = new Parcelable.Creator<Urn>() { // from class: miui.upnp.typedef.device.urn.Urn.1
        @Override // android.os.Parcelable.Creator
        public Urn createFromParcel(Parcel parcel) {
            return new Urn(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public Urn[] newArray(int i) {
            return new Urn[i];
        }
    };
    private static final String URN = "urn";
    private String domain;
    private String subType;
    private Type type = Type.UNDEFINED;
    private String version;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: miui.upnp.typedef.device.urn.Urn$2  reason: invalid class name */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$miui$upnp$typedef$device$urn$Urn$Type;

        static {
            int[] iArr = new int[Type.values().length];
            $SwitchMap$miui$upnp$typedef$device$urn$Urn$Type = iArr;
            try {
                iArr[Type.DEVICE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$device$urn$Urn$Type[Type.SERVICE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    /* loaded from: classes4.dex */
    public enum Type {
        UNDEFINED,
        DEVICE,
        SERVICE;

        private static final String STR_DEVICE = "device";
        private static final String STR_SERVICE = "service";
        private static final String STR_UNDEFINED = "undefined";

        public static Type retrieveType(String str) {
            return str.equals(STR_UNDEFINED) ? UNDEFINED : str.equals("device") ? DEVICE : str.equals(STR_SERVICE) ? SERVICE : UNDEFINED;
        }

        @Override // java.lang.Enum
        public String toString() {
            int i = AnonymousClass2.$SwitchMap$miui$upnp$typedef$device$urn$Urn$Type[ordinal()];
            return i != 1 ? i != 2 ? STR_UNDEFINED : STR_SERVICE : "device";
        }
    }

    public Urn() {
    }

    public Urn(Parcel parcel) {
        readFromParcel(parcel);
    }

    public static Urn create(String str, Type type, String str2, float f) {
        return create(str, type, str2, String.valueOf(f));
    }

    public static Urn create(String str, Type type, String str2, int i) {
        return create(str, type, str2, String.valueOf(i));
    }

    public static Urn create(String str, Type type, String str2, String str3) {
        Urn urn = new Urn();
        urn.domain = str;
        urn.type = type;
        urn.subType = str2;
        urn.version = str3;
        return urn;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Urn)) {
            Urn urn = (Urn) obj;
            String str = this.domain;
            if (str == null) {
                if (urn.domain != null) {
                    return false;
                }
            } else if (!str.equals(urn.domain)) {
                return false;
            }
            if (this.type != urn.type) {
                return false;
            }
            String str2 = this.subType;
            if (str2 == null) {
                if (urn.subType != null) {
                    return false;
                }
            } else if (!str2.equals(urn.subType)) {
                return false;
            }
            return this.version.equals(urn.version);
        }
        return false;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getSubType() {
        return this.subType;
    }

    public Type getType() {
        return this.type;
    }

    public String getVersion() {
        return this.version;
    }

    public int hashCode() {
        String str = this.domain;
        int hashCode = ((str == null ? 0 : str.hashCode()) + 31) * 31;
        Type type = this.type;
        int hashCode2 = (hashCode + (type == null ? 0 : type.hashCode())) * 31;
        String str2 = this.subType;
        int hashCode3 = (hashCode2 + (str2 == null ? 0 : str2.hashCode())) * 31;
        String str3 = this.version;
        return hashCode3 + (str3 != null ? str3.hashCode() : 0);
    }

    public boolean parse(String str) {
        String[] split = str.split(":");
        if (split.length == 5 && split[0].equals(URN)) {
            this.domain = split[1];
            this.type = Type.retrieveType(split[2]);
            this.subType = split[3];
            try {
                this.version = split[4];
                return true;
            } catch (NumberFormatException unused) {
            }
        }
        return false;
    }

    public void readFromParcel(Parcel parcel) {
        parse(parcel.readString());
    }

    public void setDomain(String str) {
        this.domain = str;
    }

    public void setSubType(String str) {
        this.subType = str;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setVersion(String str) {
        this.version = str;
    }

    public String toString() {
        return String.format("%s:%s:%s:%s:%s", URN, this.domain, this.type.toString(), this.subType, this.version);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(toString());
    }
}
