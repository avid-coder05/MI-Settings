package miui.imagefilters;

import android.graphics.Color;
import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class GrayScaleFilter extends IImageFilter.AbstractImageFilter {
    private int mBlackColor = -16777216;
    private int mWhiteColor = -1;

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter
    public void processData(ImageData imageData) {
        int i = imageData.width;
        int i2 = imageData.height;
        int[] iArr = imageData.pixels;
        int i3 = this.mBlackColor;
        int i4 = i3 >>> 24;
        int i5 = this.mWhiteColor;
        int i6 = i5 >>> 24;
        int i7 = (i3 >>> 16) & 255;
        int i8 = (i5 >>> 16) & 255;
        int i9 = (i3 >>> 8) & 255;
        int i10 = (i5 >>> 8) & 255;
        int i11 = i3 & 255;
        int i12 = i5 & 255;
        int i13 = 0;
        int i14 = 0;
        while (i14 < i) {
            int i15 = i13;
            while (i15 < i2) {
                int i16 = (i15 * i) + i14;
                int i17 = iArr[i16];
                int i18 = i;
                int convertColorToGrayscale = ImageFilterUtils.convertColorToGrayscale(i17);
                iArr[i16] = ImageFilterUtils.interpolate(i13, 255, i11, i12, convertColorToGrayscale) | (((ImageFilterUtils.interpolate(i13, 255, i4, i6, convertColorToGrayscale) * (i17 >>> 24)) / 255) << 24) | (ImageFilterUtils.interpolate(i13, 255, i7, i8, convertColorToGrayscale) << 16) | (ImageFilterUtils.interpolate(i13, 255, i9, i10, convertColorToGrayscale) << 8);
                i15++;
                i = i18;
                i13 = 0;
            }
            i14++;
            i13 = 0;
        }
    }

    public void setBlackColor(String str) {
        this.mBlackColor = Color.parseColor(str);
    }

    public void setWhiteColor(String str) {
        this.mWhiteColor = Color.parseColor(str);
    }
}
