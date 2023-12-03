package miui.imagefilters;

import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class SpreadFilter extends IImageFilter.AbstractImageFilter {
    private int mRadius;
    private boolean mIsSpreadBlack = true;
    private boolean mIsAlphaSpread = false;

    private static final int getColor(int[] iArr, int i, int i2, int i3, int i4) {
        return iArr[(ImageFilterUtils.clamp(0, i2, i4 - 1) * i3) + ImageFilterUtils.clamp(0, i, i3 - 1)];
    }

    private int getLuminance(int i, int i2) {
        int convertColorToGrayscale = ImageFilterUtils.convertColorToGrayscale(i);
        if (this.mIsSpreadBlack) {
            convertColorToGrayscale = 255 - convertColorToGrayscale;
        }
        return convertColorToGrayscale * i2;
    }

    private int processPerPixel(int[] iArr, int i, int i2, int i3, int i4) {
        int i5 = this.mRadius;
        int i6 = i5 * i5;
        int i7 = 0;
        int i8 = this.mIsAlphaSpread ? 255 : 0;
        int i9 = -1;
        int i10 = -i5;
        while (true) {
            int i11 = this.mRadius;
            if (i10 > i11) {
                return (i8 << 24) | (16777215 & i7);
            }
            for (int i12 = -i11; i12 <= this.mRadius; i12++) {
                if ((i10 * i10) + (i12 * i12) <= i6) {
                    int color = getColor(iArr, i + i12, i2 + i10, i3, i4);
                    int i13 = (color >>> 24) & 255;
                    int luminance = getLuminance(color, i13);
                    i9 = Math.max(luminance, i9);
                    if (i9 == luminance) {
                        i7 = color;
                    }
                    i8 = this.mIsAlphaSpread ? Math.min(i8, i13) : Math.max(i8, i13);
                }
            }
            i10++;
        }
    }

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter
    public void processData(ImageData imageData) {
        int i = imageData.width;
        int i2 = imageData.height;
        int[] iArr = imageData.pixels;
        int[] backPixels = imageData.getBackPixels();
        for (int i3 = 0; i3 <= i2 - 1; i3++) {
            for (int i4 = 0; i4 <= i - 1; i4++) {
                backPixels[(i3 * i) + i4] = processPerPixel(iArr, i4, i3, i, i2);
            }
        }
        imageData.swapPixels();
    }

    public void setIsAlphaSpread(boolean z) {
        this.mIsAlphaSpread = z;
    }

    public void setIsSpreadBlack(boolean z) {
        this.mIsSpreadBlack = z;
    }

    public void setRadius(int i) {
        this.mRadius = i;
    }
}
