package miui.imagefilters;

import java.lang.reflect.Array;
import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class EdgesFilter extends IImageFilter.AbstractImageFilter {
    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter
    public void processData(ImageData imageData) {
        int i = imageData.width;
        int i2 = imageData.height;
        int[] iArr = imageData.pixels;
        float[] fArr = new float[3];
        int[][] iArr2 = (int[][]) Array.newInstance(int.class, i, i2);
        for (int i3 = 0; i3 < i2; i3++) {
            for (int i4 = 0; i4 < i; i4++) {
                iArr2[i4][i3] = ImageFilterUtils.convertColorToGrayscale(iArr[(i3 * i) + i4]);
            }
        }
        for (int i5 = 1; i5 < i2 - 1; i5++) {
            for (int i6 = 1; i6 < i - 1; i6++) {
                int i7 = (i5 * i) + i6;
                int i8 = i6 - 1;
                int i9 = i5 - 1;
                int i10 = i9 + 2;
                int i11 = i8 + 1;
                int i12 = (((-iArr2[i8][i9]) + iArr2[i8][i10]) - (iArr2[i11][i9] * 2)) + (iArr2[i11][i10] * 2);
                int i13 = i8 + 2;
                int i14 = i9 + 1;
                int clamp = 255 - ImageFilterUtils.clamp(0, Math.abs((i12 - iArr2[i13][i9]) + iArr2[i13][i10]) + Math.abs(((((iArr2[i8][i9] + (iArr2[i8][i14] * 2)) + iArr2[i8][i10]) - iArr2[i13][i9]) - (iArr2[i13][i14] * 2)) - iArr2[i13][i10]), 255);
                ImageFilterUtils.RgbToHsl(iArr[i7], fArr);
                fArr[2] = clamp / 255.0f;
                iArr[i7] = (ImageFilterUtils.HslToRgb(fArr) & 16777215) | (iArr[i7] & (-16777216));
            }
        }
    }
}
