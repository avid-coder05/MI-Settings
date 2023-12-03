package miui.upnp.typedef.property;

/* loaded from: classes4.dex */
public enum AllowedValueType {
    ANY,
    LIST,
    RANGE;

    /* renamed from: miui.upnp.typedef.property.AllowedValueType$1  reason: invalid class name */
    /* loaded from: classes4.dex */
    static /* synthetic */ class AnonymousClass1 {
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

    public static AllowedValueType retrieveType(int i) {
        return i != 0 ? i != 1 ? i != 2 ? ANY : RANGE : LIST : ANY;
    }

    public int toInt() {
        int i = AnonymousClass1.$SwitchMap$miui$upnp$typedef$property$AllowedValueType[ordinal()];
        if (i != 2) {
            return i != 3 ? 0 : 2;
        }
        return 1;
    }
}
