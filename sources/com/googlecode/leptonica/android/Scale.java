package com.googlecode.leptonica.android;

/* loaded from: classes2.dex */
public class Scale {

    /* renamed from: com.googlecode.leptonica.android.Scale$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$googlecode$leptonica$android$Scale$ScaleType;

        static {
            int[] iArr = new int[ScaleType.values().length];
            $SwitchMap$com$googlecode$leptonica$android$Scale$ScaleType = iArr;
            try {
                iArr[ScaleType.FILL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$googlecode$leptonica$android$Scale$ScaleType[ScaleType.FIT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$googlecode$leptonica$android$Scale$ScaleType[ScaleType.FIT_SHRINK.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* loaded from: classes2.dex */
    public enum ScaleType {
        FILL,
        FIT,
        FIT_SHRINK
    }

    static {
        System.loadLibrary("lept");
    }

    private static native long nativeScale(long j, float f, float f2);

    private static native long nativeScaleGeneral(long j, float f, float f2, float f3, int i);

    public static Pix scale(Pix pix, float f) {
        return scale(pix, f, f);
    }

    public static Pix scale(Pix pix, float f, float f2) {
        if (pix != null) {
            if (f > 0.0f) {
                if (f2 > 0.0f) {
                    long nativeScale = nativeScale(pix.getNativePix(), f, f2);
                    if (nativeScale != 0) {
                        return new Pix(nativeScale);
                    }
                    throw new RuntimeException("Failed to natively scale pix");
                }
                throw new IllegalArgumentException("Y scaling factor must be positive");
            }
            throw new IllegalArgumentException("X scaling factor must be positive");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }

    public static Pix scaleToSize(Pix pix, int i, int i2, ScaleType scaleType) {
        if (pix != null) {
            float width = i / pix.getWidth();
            float height = i2 / pix.getHeight();
            int i3 = AnonymousClass1.$SwitchMap$com$googlecode$leptonica$android$Scale$ScaleType[scaleType.ordinal()];
            if (i3 != 2) {
                if (i3 == 3) {
                    width = Math.min(1.0f, Math.min(width, height));
                }
                return scale(pix, width, height);
            }
            width = Math.min(width, height);
            height = width;
            return scale(pix, width, height);
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }

    public static Pix scaleWithoutSharpening(Pix pix, float f) {
        if (pix != null) {
            if (f > 0.0f) {
                return new Pix(nativeScaleGeneral(pix.getNativePix(), f, f, 0.0f, 0));
            }
            throw new IllegalArgumentException("Scaling factor must be positive");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }
}
