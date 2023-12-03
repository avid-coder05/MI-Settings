package miui.imagefilters;

import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class ThresholdFilter extends IImageFilter.AbstractImageFilter {
    private int mThresholdLevel = 128;
    private boolean mUniform;

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter
    public void processData(ImageData imageData) {
        int i;
        int i2 = imageData.width;
        int i3 = imageData.height;
        int[] iArr = imageData.pixels;
        if (this.mUniform) {
            int length = iArr.length;
            int[] iArr2 = new int[256];
            for (int i4 = 0; i4 < i3; i4++) {
                for (int i5 = 0; i5 < i2; i5++) {
                    int i6 = iArr[(i4 * i2) + i5];
                    if (((i6 >>> 24) & 255) < 10) {
                        length--;
                    } else {
                        int convertColorToGrayscale = ImageFilterUtils.convertColorToGrayscale(i6);
                        iArr2[convertColorToGrayscale] = iArr2[convertColorToGrayscale] + 1;
                    }
                }
            }
            int i7 = (length * this.mThresholdLevel) / 255;
            i = 0;
            int i8 = 0;
            while (true) {
                if (i >= 256) {
                    i = 0;
                    break;
                }
                i8 += iArr2[i];
                if (i8 >= i7) {
                    break;
                }
                i++;
            }
        } else {
            i = this.mThresholdLevel;
        }
        for (int i9 = 0; i9 < i3; i9++) {
            for (int i10 = 0; i10 < i2; i10++) {
                int i11 = (i9 * i2) + i10;
                int i12 = iArr[i11];
                if (ImageFilterUtils.convertColorToGrayscale(i12) >= i) {
                    iArr[i11] = (i12 & (-16777216)) | 16777215;
                } else {
                    iArr[i11] = i12 & (-16777216);
                }
            }
        }
    }

    public void setThresholdLevel(int i) {
        this.mThresholdLevel = i;
    }

    public void setUniform(boolean z) {
        this.mUniform = z;
    }
}
