package miui.imagefilters;

import android.graphics.Bitmap;
import android.util.Log;
import miui.cloud.CloudPushConstants;
import miui.content.res.IconCustomizer;
import miui.imagefilters.FilterParamType;
import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class ImageFilterUtils {
    static final float COLOR_TO_GRAYSCALE_FACTOR_B = 0.114f;
    static final float COLOR_TO_GRAYSCALE_FACTOR_G = 0.587f;
    static final float COLOR_TO_GRAYSCALE_FACTOR_R = 0.299f;
    static final String TAG = "ImageFilterUtils";

    public static int HslToRgb(float f, float f2, float f3) {
        int i;
        int i2;
        int i3;
        if (f2 == 0.0f) {
            i = (int) (255.0f * f3);
            i2 = i;
            i3 = i2;
        } else {
            float f4 = f3 < 0.5f ? (f2 + 1.0f) * f3 : (f3 + f2) - (f3 * f2);
            float f5 = (2.0f * f3) - f4;
            float f6 = f / 360.0f;
            float[] fArr = new float[3];
            fArr[0] = f6 + 0.33333334f;
            fArr[1] = f6;
            fArr[2] = f6 - 0.33333334f;
            for (int i4 = 0; i4 < 3; i4++) {
                if (fArr[i4] < 0.0f) {
                    fArr[i4] = (float) (fArr[i4] + 1.0d);
                } else if (fArr[i4] > 1.0f) {
                    fArr[i4] = (float) (fArr[i4] - 1.0d);
                }
                if (fArr[i4] * 6.0f < 1.0f) {
                    fArr[i4] = ((f4 - f5) * 6.0f * fArr[i4]) + f5;
                } else if (fArr[i4] * 2.0d < 1.0d) {
                    fArr[i4] = f4;
                } else if (fArr[i4] * 3.0d < 2.0d) {
                    fArr[i4] = ((f4 - f5) * (0.6666667f - fArr[i4]) * 6.0f) + f5;
                } else {
                    fArr[i4] = f5;
                }
            }
            i = (int) (fArr[0] * 255.0d);
            i2 = (int) (fArr[1] * 255.0d);
            i3 = (int) (fArr[2] * 255.0d);
        }
        return (clamp(0, i, 255) << 16) | (-16777216) | (clamp(0, i2, 255) << 8) | clamp(0, i3, 255);
    }

    public static int HslToRgb(float[] fArr) {
        return HslToRgb(fArr[0], fArr[1], fArr[2]);
    }

    public static int HsvToRgb(float f, float f2, float f3) {
        float f4;
        float f5;
        float f6;
        if (f2 == 0.0f) {
            f5 = f3;
            f4 = f5;
        } else {
            float f7 = f / 60.0f;
            int floor = (int) Math.floor(f7);
            float f8 = f7 - floor;
            f4 = (1.0f - f2) * f3;
            float f9 = (1.0f - (f2 * f8)) * f3;
            f5 = (1.0f - (f2 * (1.0f - f8))) * f3;
            if (floor == 0) {
                f6 = f4;
                f4 = f5;
            } else if (floor == 1) {
                f5 = f4;
                f4 = f3;
                f3 = f9;
            } else if (floor == 2) {
                f4 = f3;
                f3 = f4;
            } else if (floor == 3) {
                f5 = f3;
                f3 = f4;
                f4 = f9;
            } else if (floor == 4) {
                f6 = f3;
                f3 = f5;
            } else if (floor != 5) {
                f5 = 0.0f;
                f3 = 0.0f;
                f4 = 0.0f;
            } else {
                f5 = f9;
            }
            f5 = f6;
        }
        return ((int) (clamp(0.0f, f5, 1.0f) * 255.0f)) | (((int) (clamp(0.0f, f3, 1.0f) * 255.0f)) << 16) | (-16777216) | (((int) (clamp(0.0f, f4, 1.0f) * 255.0f)) << 8);
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x0078  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void RgbToHsl(int r10, int r11, int r12, float[] r13) {
        /*
            float r10 = (float) r10
            r0 = 1132396544(0x437f0000, float:255.0)
            float r10 = r10 / r0
            float r11 = (float) r11
            float r11 = r11 / r0
            float r12 = (float) r12
            float r12 = r12 / r0
            float r0 = java.lang.Math.max(r11, r12)
            float r0 = java.lang.Math.max(r10, r0)
            float r1 = java.lang.Math.min(r11, r12)
            float r1 = java.lang.Math.min(r10, r1)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            r3 = 1135869952(0x43b40000, float:360.0)
            r4 = 0
            if (r2 != 0) goto L21
        L1f:
            r11 = r4
            goto L58
        L21:
            int r5 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            r6 = 1114636288(0x42700000, float:60.0)
            if (r5 != 0) goto L31
            int r7 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r7 < 0) goto L31
            float r11 = r11 - r12
            float r11 = r11 * r6
            float r10 = r0 - r1
            float r11 = r11 / r10
            goto L58
        L31:
            if (r5 != 0) goto L3e
            int r5 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r5 >= 0) goto L3e
            float r11 = r11 - r12
            float r11 = r11 * r6
            float r10 = r0 - r1
            float r11 = r11 / r10
            float r11 = r11 + r3
            goto L58
        L3e:
            int r5 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r5 != 0) goto L4c
            float r12 = r12 - r10
            float r12 = r12 * r6
            float r10 = r0 - r1
            float r12 = r12 / r10
            r10 = 1123024896(0x42f00000, float:120.0)
            float r11 = r12 + r10
            goto L58
        L4c:
            int r12 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r12 != 0) goto L1f
            float r10 = r10 - r11
            float r10 = r10 * r6
            float r11 = r0 - r1
            float r10 = r10 / r11
            r11 = 1131413504(0x43700000, float:240.0)
            float r11 = r11 + r10
        L58:
            float r10 = r0 + r1
            r12 = 1073741824(0x40000000, float:2.0)
            float r5 = r10 / r12
            int r6 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r6 == 0) goto L7c
            if (r2 != 0) goto L65
            goto L7c
        L65:
            int r2 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            r6 = 4602678819172646912(0x3fe0000000000000, double:0.5)
            if (r2 >= 0) goto L73
            double r8 = (double) r5
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 > 0) goto L73
            float r0 = r0 - r1
            float r0 = r0 / r10
            goto L7d
        L73:
            double r8 = (double) r5
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 <= 0) goto L7c
            float r0 = r0 - r1
            float r12 = r12 - r10
            float r0 = r0 / r12
            goto L7d
        L7c:
            r0 = r4
        L7d:
            r10 = 0
            float r11 = clamp(r4, r11, r3)
            r13[r10] = r11
            r10 = 1
            r11 = 1065353216(0x3f800000, float:1.0)
            float r12 = clamp(r4, r0, r11)
            r13[r10] = r12
            r10 = 2
            float r11 = clamp(r4, r5, r11)
            r13[r10] = r11
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.imagefilters.ImageFilterUtils.RgbToHsl(int, int, int, float[]):void");
    }

    public static void RgbToHsl(int i, float[] fArr) {
        RgbToHsl((i >>> 16) & 255, (i >>> 8) & 255, i & 255, fArr);
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x005c  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x005e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void RgbToHsv(int r7, int r8, int r9, float[] r10) {
        /*
            float r7 = (float) r7
            r0 = 1132396544(0x437f0000, float:255.0)
            float r7 = r7 / r0
            float r8 = (float) r8
            float r8 = r8 / r0
            float r9 = (float) r9
            float r9 = r9 / r0
            float r0 = java.lang.Math.max(r8, r9)
            float r0 = java.lang.Math.max(r7, r0)
            float r1 = java.lang.Math.min(r8, r9)
            float r1 = java.lang.Math.min(r7, r1)
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            r3 = 1135869952(0x43b40000, float:360.0)
            r4 = 0
            if (r2 != 0) goto L21
        L1f:
            r8 = r4
            goto L58
        L21:
            int r2 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            r5 = 1114636288(0x42700000, float:60.0)
            if (r2 != 0) goto L31
            int r6 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r6 < 0) goto L31
            float r8 = r8 - r9
            float r8 = r8 * r5
            float r7 = r0 - r1
            float r8 = r8 / r7
            goto L58
        L31:
            if (r2 != 0) goto L3e
            int r2 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r2 >= 0) goto L3e
            float r8 = r8 - r9
            float r8 = r8 * r5
            float r7 = r0 - r1
            float r8 = r8 / r7
            float r8 = r8 + r3
            goto L58
        L3e:
            int r2 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r2 != 0) goto L4c
            float r9 = r9 - r7
            float r9 = r9 * r5
            float r7 = r0 - r1
            float r9 = r9 / r7
            r7 = 1123024896(0x42f00000, float:120.0)
            float r8 = r9 + r7
            goto L58
        L4c:
            int r9 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r9 != 0) goto L1f
            float r7 = r7 - r8
            float r7 = r7 * r5
            float r8 = r0 - r1
            float r7 = r7 / r8
            r8 = 1131413504(0x43700000, float:240.0)
            float r8 = r8 + r7
        L58:
            int r7 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r7 != 0) goto L5e
            r7 = r4
            goto L61
        L5e:
            float r7 = r0 - r1
            float r7 = r7 / r0
        L61:
            r9 = 0
            float r8 = clamp(r4, r8, r3)
            r10[r9] = r8
            r8 = 1
            r9 = 1065353216(0x3f800000, float:1.0)
            float r7 = clamp(r4, r7, r9)
            r10[r8] = r7
            r7 = 2
            float r8 = clamp(r4, r0, r9)
            r10[r7] = r8
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.imagefilters.ImageFilterUtils.RgbToHsv(int, int, int, float[]):void");
    }

    public static void RgbToHsv(int i, float[] fArr) {
        RgbToHsv((i >>> 16) & 255, (i >>> 8) & 255, i & 255, fArr);
    }

    public static void checkChannelParam(String str, boolean[] zArr) {
        if (str.equalsIgnoreCase("r") || str.equalsIgnoreCase("red")) {
            zArr[0] = true;
        } else if (str.equalsIgnoreCase(CloudPushConstants.WATERMARK_TYPE.GLOBAL) || str.equalsIgnoreCase("green")) {
            zArr[1] = true;
        } else if (str.equalsIgnoreCase("b") || str.equalsIgnoreCase("blue")) {
            zArr[2] = true;
        } else if (str.equalsIgnoreCase("a") || str.equalsIgnoreCase("alpha")) {
            if (zArr.length >= 4) {
                zArr[3] = true;
            }
        } else {
            for (int i = 0; i < zArr.length; i++) {
                zArr[i] = true;
            }
        }
    }

    public static float clamp(float f, float f2, float f3) {
        return f2 <= f ? f : f2 >= f3 ? f3 : f2;
    }

    public static int clamp(int i, int i2, int i3) {
        return i2 <= i ? i : i2 >= i3 ? i3 : i2;
    }

    public static int convertColorToGrayscale(int i) {
        return (int) ((((16711680 & i) >>> 16) * COLOR_TO_GRAYSCALE_FACTOR_R) + (((65280 & i) >>> 8) * COLOR_TO_GRAYSCALE_FACTOR_G) + ((i & 255) * COLOR_TO_GRAYSCALE_FACTOR_B));
    }

    public static int interpolate(int i, int i2, int i3, int i4, int i5) {
        return (int) (i3 + ((i5 * (i4 - i3)) / (i2 - i)));
    }

    public static void interpolate(float[] fArr, float[] fArr2, float f, float[] fArr3) {
        int min = Math.min(fArr.length, fArr2.length);
        for (int i = 0; i < min; i++) {
            fArr3[i] = fArr[i] + ((fArr2[i] - fArr[i]) * f);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v14, types: [float] */
    /* JADX WARN: Type inference failed for: r10v20, types: [int] */
    /* JADX WARN: Type inference failed for: r10v8, types: [double] */
    /* JADX WARN: Type inference failed for: r1v5, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.reflect.Method[]] */
    /* JADX WARN: Type inference failed for: r6v0 */
    /* JADX WARN: Type inference failed for: r6v1 */
    /* JADX WARN: Type inference failed for: r6v2 */
    /* JADX WARN: Type inference failed for: r6v4, types: [double[]] */
    /* JADX WARN: Type inference failed for: r6v5, types: [float[]] */
    /* JADX WARN: Type inference failed for: r6v6, types: [int[]] */
    /* JADX WARN: Type inference failed for: r6v7 */
    /* JADX WARN: Type inference failed for: r6v8 */
    /* JADX WARN: Type inference failed for: r6v9 */
    /* JADX WARN: Type inference failed for: r8v0 */
    /* JADX WARN: Type inference failed for: r8v1, types: [java.lang.reflect.Method] */
    /* JADX WARN: Type inference failed for: r8v2, types: [java.lang.reflect.Method] */
    public static boolean setProperty(Object obj, String str, Object obj2) {
        ?? r6;
        ?? r8;
        Object obj3;
        String str2 = "set" + str;
        ?? methods = obj.getClass().getMethods();
        int length = methods.length;
        int i = 0;
        while (true) {
            r6 = 0;
            r6 = 0;
            if (i >= length) {
                r8 = 0;
                break;
            }
            r8 = methods[i];
            if (str2.equalsIgnoreCase(r8.getName()) && r8.getParameterTypes().length == 1) {
                break;
            }
            i++;
        }
        if (r8 == 0) {
            Log.w(TAG, "unknown property:" + str + ",obj:" + obj);
            return false;
        }
        FilterParamType filterParamType = (FilterParamType) r8.getAnnotation(FilterParamType.class);
        FilterParamType.ParamType value = filterParamType == null ? FilterParamType.ParamType.DEFAULT : filterParamType.value();
        Class<?> cls = r8.getParameterTypes()[0];
        try {
            if (obj2 instanceof String) {
                String str3 = (String) obj2;
                if (String.class.equals(cls)) {
                    r6 = str3;
                } else {
                    Class cls2 = Integer.TYPE;
                    if (cls2.equals(cls)) {
                        int parseInt = Integer.parseInt(str3);
                        if (value == FilterParamType.ParamType.ICON_SIZE) {
                            parseInt = IconCustomizer.hdpiIconSizeToCurrent(parseInt);
                        }
                        obj3 = Integer.valueOf(parseInt);
                    } else {
                        Class cls3 = Float.TYPE;
                        if (cls3.equals(cls)) {
                            float parseFloat = Float.parseFloat(str3);
                            if (value == FilterParamType.ParamType.ICON_SIZE) {
                                parseFloat = IconCustomizer.hdpiIconSizeToCurrent(parseFloat);
                            }
                            obj3 = Float.valueOf(parseFloat);
                        } else if (Double.TYPE.equals(cls)) {
                            double parseDouble = Double.parseDouble(str3);
                            if (value == FilterParamType.ParamType.ICON_SIZE) {
                                parseDouble = IconCustomizer.hdpiIconSizeToCurrent(parseDouble);
                            }
                            obj3 = Double.valueOf(parseDouble);
                        } else if (Boolean.TYPE.equals(cls)) {
                            obj3 = Boolean.valueOf(Boolean.parseBoolean(str3));
                        } else if (Bitmap.class.equals(cls)) {
                            obj3 = IconCustomizer.getRawIcon(str3);
                        } else if (cls.isEnum()) {
                            obj3 = Enum.valueOf(cls, str3);
                        } else if (!cls.isArray()) {
                            Log.w(TAG, "unknown param type:" + cls.getName() + ",obj:" + obj + ",property:" + str);
                            return false;
                        } else {
                            Class<?> componentType = cls.getComponentType();
                            String[] split = str3.split(",");
                            if (cls2.equals(componentType)) {
                                int length2 = split.length;
                                r6 = new int[length2];
                                for (int i2 = 0; i2 < length2; i2++) {
                                    r6[i2] = Integer.parseInt(split[i2].trim());
                                    if (value == FilterParamType.ParamType.ICON_SIZE) {
                                        r6[i2] = IconCustomizer.hdpiIconSizeToCurrent((int) r6[i2]);
                                    }
                                }
                            } else if (cls3.equals(componentType)) {
                                int length3 = split.length;
                                r6 = new float[length3];
                                for (int i3 = 0; i3 < length3; i3++) {
                                    r6[i3] = Float.parseFloat(split[i3].trim());
                                    if (value == FilterParamType.ParamType.ICON_SIZE) {
                                        r6[i3] = IconCustomizer.hdpiIconSizeToCurrent((float) r6[i3]);
                                    }
                                }
                            } else if (Double.TYPE.equals(componentType)) {
                                int length4 = split.length;
                                r6 = new double[length4];
                                for (int i4 = 0; i4 < length4; i4++) {
                                    r6[i4] = Float.parseFloat(split[i4].trim());
                                    if (value == FilterParamType.ParamType.ICON_SIZE) {
                                        r6[i4] = IconCustomizer.hdpiIconSizeToCurrent((double) r6[i4]);
                                    }
                                }
                            }
                        }
                    }
                    r6 = obj3;
                }
            } else if (obj2 instanceof IImageFilter.ImageFilterGroup) {
                r6 = obj2;
            }
            r8.invoke(obj, new Object[]{r6});
            return true;
        } catch (Exception e) {
            Log.e(TAG, "set property fail. obj:" + obj + ",property:" + str + ",value:" + obj2, e);
            return false;
        }
    }
}
