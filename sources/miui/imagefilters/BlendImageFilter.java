package miui.imagefilters;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.Log;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Map;
import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class BlendImageFilter extends IImageFilter.AbstractImageFilter {
    static final int BLEND_TYPE_COLOR = 21;
    static final int BLEND_TYPE_COLOR_BURN = 10;
    static final int BLEND_TYPE_COLOR_DODGE = 9;
    static final int BLEND_TYPE_DARKEN = 3;
    static final int BLEND_TYPE_DIFFERENCE = 5;
    static final int BLEND_TYPE_DIVIDE = 23;
    static final int BLEND_TYPE_EXCLUSION = 18;
    static final int BLEND_TYPE_HARD_LIGHT = 12;
    static final int BLEND_TYPE_HARD_MIX = 17;
    static final int BLEND_TYPE_HUE = 19;
    static final int BLEND_TYPE_LIGHTEN = 4;
    static final int BLEND_TYPE_LINEAR_BURN = 7;
    static final int BLEND_TYPE_LINEAR_DODGE = 6;
    static final int BLEND_TYPE_LINEAR_LIGHT = 15;
    static final int BLEND_TYPE_LUMINOSITY = 22;
    static final int BLEND_TYPE_MULTIPLY = 1;
    static final int BLEND_TYPE_NORMAL = 0;
    static final int BLEND_TYPE_OPACITY = 11;
    static final int BLEND_TYPE_OVERLAY = 8;
    static final int BLEND_TYPE_PIN_LIGHT = 16;
    static final int BLEND_TYPE_SATURATION = 20;
    static final int BLEND_TYPE_SCREEN = 2;
    static final int BLEND_TYPE_SOFT_LIGHT = 13;
    static final int BLEND_TYPE_SUBTRACT = 24;
    static final int BLEND_TYPE_VIVID_LIGHT = 14;
    static final String TAG = "BlendImageFilter";
    private IImageFilter.ImageFilterGroup mInputFilters;
    private ImageData mInputImage;
    private SoftReference<Map.Entry<Integer, ImageData>> mInputImageCache;
    private boolean mUseOriginalImage;
    private int mBlendType = 0;
    private boolean mIsInputImageOnTop = true;
    private PorterDuff.Mode mPorterDuffMode = PorterDuff.Mode.SRC_ATOP;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: miui.imagefilters.BlendImageFilter$41  reason: invalid class name */
    /* loaded from: classes3.dex */
    public static /* synthetic */ class AnonymousClass41 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$PorterDuff$Mode;

        static {
            int[] iArr = new int[PorterDuff.Mode.values().length];
            $SwitchMap$android$graphics$PorterDuff$Mode = iArr;
            try {
                iArr[PorterDuff.Mode.CLEAR.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DST.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DST_ATOP.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DST_IN.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DST_OUT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DST_OVER.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SRC.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SRC_ATOP.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SRC_IN.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SRC_OUT.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SRC_OVER.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.XOR.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.SCREEN.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.LIGHTEN.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$android$graphics$PorterDuff$Mode[PorterDuff.Mode.DARKEN.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
        }
    }

    /* loaded from: classes3.dex */
    abstract class Blender {
        Blender() {
        }

        public abstract int blendColor(int i, int i2);
    }

    /* loaded from: classes3.dex */
    abstract class BlenderPerChannel extends Blender {
        BlenderPerChannel() {
            super();
        }

        public abstract float blendChannel(float f, float f2);

        @Override // miui.imagefilters.BlendImageFilter.Blender
        public int blendColor(int i, int i2) {
            return ((int) (ImageFilterUtils.clamp(0.0f, blendChannel((i & 255) / 255.0f, (i2 & 255) / 255.0f), 1.0f) * 255.0f)) | ((-16777216) & i2) | (((int) (ImageFilterUtils.clamp(0.0f, blendChannel(((i >>> 16) & 255) / 255.0f, ((i2 >>> 16) & 255) / 255.0f), 1.0f) * 255.0f)) << 16) | (((int) (ImageFilterUtils.clamp(0.0f, blendChannel(((i >>> 8) & 255) / 255.0f, ((i2 >>> 8) & 255) / 255.0f), 1.0f) * 255.0f)) << 8);
        }
    }

    /* loaded from: classes3.dex */
    abstract class PorterDuffBlender {
        PorterDuffBlender() {
        }

        public abstract int blendFinal(int i, int i2);
    }

    /* loaded from: classes3.dex */
    abstract class PorterDuffBlenderPerChannel extends PorterDuffBlender {
        PorterDuffBlenderPerChannel() {
            super();
        }

        public abstract float blendAlpha(float f, float f2);

        public abstract float blendChannel(float f, float f2, float f3, float f4);

        @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlender
        public int blendFinal(int i, int i2) {
            int i3 = (i2 >>> 24) & 255;
            int i4 = ((i2 >>> 16) & 255) * i3;
            int i5 = ((i2 >>> 8) & 255) * i3;
            int i6 = (i2 & 255) * i3;
            int i7 = (i >>> 24) & 255;
            int i8 = ((i >>> 16) & 255) * i7;
            int i9 = ((i >>> 8) & 255) * i7;
            int i10 = (i & 255) * i7;
            float f = i7 / 255.0f;
            float f2 = i3 / 255.0f;
            int clamp = (int) (ImageFilterUtils.clamp(0.0f, blendAlpha(f, f2), 1.0f) * 255.0f);
            return (clamp << 24) | ((clamp == 0 ? 0 : (int) ImageFilterUtils.clamp(0.0f, blendChannel(i8, i4, f, f2) / clamp, 255.0f)) << 16) | ((clamp == 0 ? 0 : (int) ImageFilterUtils.clamp(0.0f, blendChannel(i9, i5, f, f2) / clamp, 255.0f)) << 8) | (clamp != 0 ? (int) ImageFilterUtils.clamp(0.0f, blendChannel(i10, i6, f, f2) / clamp, 255.0f) : 0);
        }
    }

    private Blender getCurrentBlender() {
        switch (this.mBlendType) {
            case 0:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.1
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return f2;
                    }
                };
            case 1:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.2
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return f * f2;
                    }
                };
            case 2:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.3
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return 1.0f - ((1.0f - f) * (1.0f - f2));
                    }
                };
            case 3:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.16
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return Math.min(f, f2);
                    }
                };
            case 4:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.17
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return Math.max(f, f2);
                    }
                };
            case 5:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.15
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return Math.abs(f - f2);
                    }
                };
            case 6:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.8
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return f2 + f;
                    }
                };
            case 7:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.10
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return (f2 + f) - 1.0f;
                    }
                };
            case 8:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.4
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return f < 0.5f ? f * 2.0f * f2 : 1.0f - (((1.0f - f) * 2.0f) * (1.0f - f2));
                    }
                };
            case 9:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.7
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return f / (1.0f - f2);
                    }
                };
            case 10:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.9
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return 1.0f - ((1.0f - f) / f2);
                    }
                };
            case 11:
                return new Blender() { // from class: miui.imagefilters.BlendImageFilter.25
                    private float blendChannel(float f, float f2, float f3) {
                        return (f2 * f3) + ((1.0f - f3) * f);
                    }

                    @Override // miui.imagefilters.BlendImageFilter.Blender
                    public int blendColor(int i, int i2) {
                        float f = ((i2 >>> 24) & 255) / 255.0f;
                        return ((int) (ImageFilterUtils.clamp(0.0f, blendChannel((i & 255) / 255.0f, (i2 & 255) / 255.0f, f), 1.0f) * 255.0f)) | ((-16777216) & i2) | (((int) (ImageFilterUtils.clamp(0.0f, blendChannel(((i >>> 16) & 255) / 255.0f, ((i2 >>> 16) & 255) / 255.0f, f), 1.0f) * 255.0f)) << 16) | (((int) (ImageFilterUtils.clamp(0.0f, blendChannel(((i >>> 8) & 255) / 255.0f, ((i2 >>> 8) & 255) / 255.0f, f), 1.0f) * 255.0f)) << 8);
                    }
                };
            case 12:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.6
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return f2 < 0.5f ? f * 2.0f * f2 : 1.0f - (((1.0f - f) * 2.0f) * (1.0f - f2));
                    }
                };
            case 13:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.5
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        float f3;
                        float sqrt;
                        if (f2 < 0.5f) {
                            f3 = f * 2.0f * f2;
                            sqrt = f * f * (1.0f - (f2 * 2.0f));
                        } else {
                            f3 = f * 2.0f * (1.0f - f2);
                            sqrt = ((float) Math.sqrt(f)) * ((f2 * 2.0f) - 1.0f);
                        }
                        return f3 + sqrt;
                    }
                };
            case 14:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.11
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return f2 <= 0.5f ? 1.0f - ((1.0f - f) / (f2 * 2.0f)) : f / ((1.0f - f2) * 2.0f);
                    }
                };
            case 15:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.12
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return (f + (f2 * 2.0f)) - 1.0f;
                    }
                };
            case 16:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.18
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        float f3 = f2 * 2.0f;
                        float f4 = f3 - 1.0f;
                        return f < f4 ? f4 : f < f3 ? f : f3;
                    }
                };
            case 17:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.19
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return f2 < 1.0f - f ? 0.0f : 1.0f;
                    }
                };
            case 18:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.20
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return (f2 + f) - ((f2 * 2.0f) * f);
                    }
                };
            case 19:
                return new Blender() { // from class: miui.imagefilters.BlendImageFilter.21
                    @Override // miui.imagefilters.BlendImageFilter.Blender
                    public int blendColor(int i, int i2) {
                        float[] fArr = new float[3];
                        float[] fArr2 = new float[3];
                        ImageFilterUtils.RgbToHsl(i2, fArr);
                        ImageFilterUtils.RgbToHsl(i, fArr2);
                        return (ImageFilterUtils.HslToRgb(fArr[0], fArr2[1], fArr2[2]) & 16777215) | ((-16777216) & i2);
                    }
                };
            case 20:
                return new Blender() { // from class: miui.imagefilters.BlendImageFilter.22
                    @Override // miui.imagefilters.BlendImageFilter.Blender
                    public int blendColor(int i, int i2) {
                        float[] fArr = new float[3];
                        float[] fArr2 = new float[3];
                        ImageFilterUtils.RgbToHsl(i2, fArr);
                        ImageFilterUtils.RgbToHsl(i, fArr2);
                        return (ImageFilterUtils.HslToRgb(fArr2[0], fArr[1], fArr2[2]) & 16777215) | ((-16777216) & i2);
                    }
                };
            case 21:
                return new Blender() { // from class: miui.imagefilters.BlendImageFilter.23
                    @Override // miui.imagefilters.BlendImageFilter.Blender
                    public int blendColor(int i, int i2) {
                        float[] fArr = new float[3];
                        float[] fArr2 = new float[3];
                        ImageFilterUtils.RgbToHsl(i2, fArr);
                        ImageFilterUtils.RgbToHsl(i, fArr2);
                        return (ImageFilterUtils.HslToRgb(fArr[0], fArr[1], fArr2[2]) & 16777215) | ((-16777216) & i2);
                    }
                };
            case 22:
                return new Blender() { // from class: miui.imagefilters.BlendImageFilter.24
                    @Override // miui.imagefilters.BlendImageFilter.Blender
                    public int blendColor(int i, int i2) {
                        float[] fArr = new float[3];
                        float[] fArr2 = new float[3];
                        ImageFilterUtils.RgbToHsl(i2, fArr);
                        ImageFilterUtils.RgbToHsl(i, fArr2);
                        return (ImageFilterUtils.HslToRgb(fArr2[0], fArr2[1], fArr[2]) & 16777215) | ((-16777216) & i2);
                    }
                };
            case 23:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.13
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return f / f2;
                    }
                };
            case 24:
                return new BlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.14
                    @Override // miui.imagefilters.BlendImageFilter.BlenderPerChannel
                    public float blendChannel(float f, float f2) {
                        return f - f2;
                    }
                };
            default:
                Log.w(TAG, "unknown blender type:" + this.mBlendType);
                return null;
        }
    }

    private PorterDuffBlender getCurrentPorterDuffBlender() {
        switch (AnonymousClass41.$SwitchMap$android$graphics$PorterDuff$Mode[this.mPorterDuffMode.ordinal()]) {
            case 1:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.26
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return 0.0f;
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return 0.0f;
                    }
                };
            case 2:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.27
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return f;
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return f;
                    }
                };
            case 3:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.28
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return f2;
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return (f4 * f) + (f2 * (1.0f - f3));
                    }
                };
            case 4:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.29
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return f2 * f;
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return f4 * f;
                    }
                };
            case 5:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.30
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return f * (1.0f - f2);
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return f * (1.0f - f4);
                    }
                };
            case 6:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.31
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return f2 + ((1.0f - f2) * f);
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return f + ((1.0f - f3) * f2);
                    }
                };
            case 7:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.32
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return f2;
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return f2;
                    }
                };
            case 8:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.33
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return f;
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return (f2 * f3) + ((1.0f - f4) * f);
                    }
                };
            case 9:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.34
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return f2 * f;
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return f2 * f3;
                    }
                };
            case 10:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.35
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return f2 * (1.0f - f);
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return f2 * (1.0f - f3);
                    }
                };
            case 11:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.36
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return f2 + ((1.0f - f2) * f);
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return f2 + ((1.0f - f4) * f);
                    }
                };
            case 12:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.37
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return (f2 + f) - ((f2 * 2.0f) * f);
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return (f2 * (1.0f - f3)) + ((1.0f - f4) * f);
                    }
                };
            case 13:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.38
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return (f2 + f) - (f2 * f);
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return (f2 + f) - ((f2 * f) / 255.0f);
                    }
                };
            case 14:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.39
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return (f2 + f) - (f2 * f);
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return ((1.0f - f3) * f2) + ((1.0f - f4) * f) + Math.max(f2, f);
                    }
                };
            case 15:
                return new PorterDuffBlenderPerChannel() { // from class: miui.imagefilters.BlendImageFilter.40
                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendAlpha(float f, float f2) {
                        return (f2 + f) - (f2 * f);
                    }

                    @Override // miui.imagefilters.BlendImageFilter.PorterDuffBlenderPerChannel
                    public float blendChannel(float f, float f2, float f3, float f4) {
                        return ((1.0f - f3) * f2) + ((1.0f - f4) * f) + Math.min(f2, f);
                    }
                };
            default:
                Log.w(TAG, "unsupport porter duff mode:" + this.mPorterDuffMode);
                return null;
        }
    }

    private int mergeWidthHeight(int i, int i2) {
        if (i > 32767 || i2 > 32767) {
            throw new RuntimeException("image's width or height to large:" + i + "x" + i2);
        }
        return (i << 16) | i2;
    }

    private ImageData obtainInputImageBySize(int i, int i2) {
        int mergeWidthHeight = mergeWidthHeight(i, i2);
        SoftReference<Map.Entry<Integer, ImageData>> softReference = this.mInputImageCache;
        Map.Entry<Integer, ImageData> entry = softReference == null ? null : softReference.get();
        if (entry == null || entry.getKey().intValue() != mergeWidthHeight) {
            ImageData imageData = this.mInputImage;
            if (mergeWidthHeight(imageData.width, imageData.height) == mergeWidthHeight) {
                return this.mInputImage;
            }
            ImageData bitmapToImageData = ImageData.bitmapToImageData(Bitmap.createScaledBitmap(ImageData.imageDataToBitmap(this.mInputImage), i, i2, true));
            this.mInputImageCache = new SoftReference<>(new AbstractMap.SimpleImmutableEntry(Integer.valueOf(mergeWidthHeight), bitmapToImageData));
            return bitmapToImageData;
        }
        return entry.getValue();
    }

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter, miui.imagefilters.IImageFilter
    public boolean canConcurrence() {
        if (this.mUseOriginalImage) {
            return false;
        }
        return super.canConcurrence();
    }

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter
    public void processData(ImageData imageData) {
        Blender currentBlender;
        PorterDuffBlender currentPorterDuffBlender;
        int[] iArr;
        if (this.mInputImage == null || (currentBlender = getCurrentBlender()) == null || (currentPorterDuffBlender = getCurrentPorterDuffBlender()) == null) {
            return;
        }
        int i = imageData.width;
        int i2 = imageData.height;
        int[] iArr2 = imageData.pixels;
        int[] iArr3 = obtainInputImageBySize(i, i2).pixels;
        if (this.mIsInputImageOnTop) {
            iArr = iArr3;
            iArr3 = iArr2;
        } else {
            iArr = iArr2;
        }
        for (int i3 = 0; i3 < i; i3++) {
            for (int i4 = 0; i4 < i2; i4++) {
                int i5 = (i4 * i) + i3;
                int i6 = iArr3[i5];
                iArr2[i5] = currentPorterDuffBlender.blendFinal(i6, currentBlender.blendColor(i6, iArr[i5]));
            }
        }
    }

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter, miui.imagefilters.IImageFilter
    public void putOriginalImage(Bitmap bitmap) {
        if (this.mUseOriginalImage) {
            IImageFilter.ImageFilterGroup imageFilterGroup = this.mInputFilters;
            if (imageFilterGroup != null) {
                this.mInputImage = imageFilterGroup.processAll(bitmap);
            } else {
                this.mInputImage = ImageData.bitmapToImageData(bitmap);
            }
            this.mInputImageCache = null;
        }
    }

    public void setBlendType(int i) {
        this.mBlendType = i;
    }

    public void setBlendTypeName(String str) {
        if (TextUtils.isEmpty(str) || str.equalsIgnoreCase("Normal")) {
            this.mBlendType = 0;
        } else if (str.equalsIgnoreCase("Multiply")) {
            this.mBlendType = 1;
        } else if (str.equalsIgnoreCase("Screen")) {
            this.mBlendType = 2;
        } else if (str.equalsIgnoreCase("Darken")) {
            this.mBlendType = 3;
        } else if (str.equalsIgnoreCase("Lighten")) {
            this.mBlendType = 4;
        } else if (str.equalsIgnoreCase("Difference")) {
            this.mBlendType = 5;
        } else if (str.equalsIgnoreCase("LinearDodge")) {
            this.mBlendType = 6;
        } else if (str.equalsIgnoreCase("LinearBurn")) {
            this.mBlendType = 7;
        } else if (str.equalsIgnoreCase("Overlay")) {
            this.mBlendType = 8;
        } else if (str.equalsIgnoreCase("ColorDodge")) {
            this.mBlendType = 9;
        } else if (str.equalsIgnoreCase("ColorBurn")) {
            this.mBlendType = 10;
        } else if (str.equalsIgnoreCase("Opacity")) {
            this.mBlendType = 11;
        } else if (str.equalsIgnoreCase("HardLight")) {
            this.mBlendType = 12;
        } else if (str.equalsIgnoreCase("SoftLight")) {
            this.mBlendType = 13;
        } else if (str.equalsIgnoreCase("VividLight")) {
            this.mBlendType = 14;
        } else if (str.equalsIgnoreCase("LinearLight")) {
            this.mBlendType = 15;
        } else if (str.equalsIgnoreCase("PinLight")) {
            this.mBlendType = 16;
        } else if (str.equalsIgnoreCase("HardMix")) {
            this.mBlendType = 17;
        } else if (str.equalsIgnoreCase("Exclusion")) {
            this.mBlendType = 18;
        } else if (str.equalsIgnoreCase("Hue")) {
            this.mBlendType = 19;
        } else if (str.equalsIgnoreCase("Saturation")) {
            this.mBlendType = 20;
        } else if (str.equalsIgnoreCase("Color")) {
            this.mBlendType = 21;
        } else if (str.equalsIgnoreCase("Luminosity")) {
            this.mBlendType = 22;
        } else if (str.equalsIgnoreCase("Divide")) {
            this.mBlendType = 23;
        } else if (str.equalsIgnoreCase("Subtract")) {
            this.mBlendType = 24;
        } else {
            Log.d(TAG, "unknown blend type name: " + str);
        }
    }

    public void setInputFilters(IImageFilter.ImageFilterGroup imageFilterGroup) {
        this.mUseOriginalImage = true;
        this.mInputFilters = imageFilterGroup;
    }

    public void setInputImage(Bitmap bitmap) {
        this.mInputImage = ImageData.bitmapToImageData(bitmap);
        this.mInputImageCache = null;
    }

    public void setIsInputImageOnTop(boolean z) {
        this.mIsInputImageOnTop = z;
    }

    public void setPorterDuffMode(PorterDuff.Mode mode) {
        this.mPorterDuffMode = mode;
    }

    public void setUseOriginalImage(boolean z) {
        this.mUseOriginalImage = z;
    }
}
