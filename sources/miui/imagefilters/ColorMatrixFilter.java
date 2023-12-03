package miui.imagefilters;

import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class ColorMatrixFilter extends IImageFilter.AbstractImageFilter {
    private float[] mColorMatrix;

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter
    public void processData(ImageData imageData) {
        float[] fArr = this.mColorMatrix;
        if (fArr == null || fArr.length != 20) {
            return;
        }
        int i = imageData.width;
        int i2 = imageData.height;
        int[] iArr = imageData.pixels;
        float f = fArr[0];
        float f2 = fArr[1];
        float f3 = fArr[2];
        float f4 = fArr[3];
        float f5 = fArr[4];
        float f6 = fArr[5];
        float f7 = fArr[6];
        float f8 = fArr[7];
        float f9 = fArr[8];
        float f10 = fArr[9];
        float f11 = fArr[10];
        float f12 = fArr[11];
        float f13 = fArr[12];
        float f14 = fArr[13];
        float f15 = fArr[14];
        float f16 = fArr[15];
        float f17 = fArr[16];
        float f18 = fArr[17];
        float f19 = fArr[18];
        float f20 = fArr[19];
        int i3 = 0;
        while (i3 <= i2 - 1) {
            int i4 = 0;
            while (i4 <= i - 1) {
                int i5 = (i3 * i) + i4;
                int i6 = i;
                int i7 = iArr[i5];
                int i8 = i2;
                int i9 = i3;
                int i10 = i4;
                int[] iArr2 = iArr;
                float f21 = (i7 >>> 16) & 255;
                float f22 = (i7 >>> 8) & 255;
                float f23 = i7 & 255;
                float f24 = (i7 >>> 24) & 255;
                float f25 = f3;
                iArr2[i5] = (ImageFilterUtils.clamp(0, (int) (((((f21 * f16) + (f22 * f17)) + (f23 * f18)) + (f24 * f19)) + f20), 255) << 24) | (ImageFilterUtils.clamp(0, (int) (((((f * f21) + (f2 * f22)) + (f3 * f23)) + (f4 * f24)) + f5), 255) << 16) | (ImageFilterUtils.clamp(0, (int) (((((f6 * f21) + (f7 * f22)) + (f8 * f23)) + (f9 * f24)) + f10), 255) << 8) | ImageFilterUtils.clamp(0, (int) ((f11 * f21) + (f12 * f22) + (f13 * f23) + (f14 * f24) + f15), 255);
                i4 = i10 + 1;
                f2 = f2;
                i = i6;
                i2 = i8;
                i3 = i9;
                iArr = iArr2;
                f = f;
                f3 = f25;
                f4 = f4;
            }
            i3++;
            i = i;
            f = f;
        }
    }

    public void setColorMatrix(float[] fArr) {
        this.mColorMatrix = fArr;
    }
}
