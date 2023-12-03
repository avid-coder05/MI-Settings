package miui.imagefilters;

import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class LevelsFilter extends IImageFilter.AbstractImageFilter {
    private float mInputMin = 0.0f;
    private float mInputMiddle = 1.0f;
    private float mInputMax = 255.0f;
    private float mOutputMin = 0.0f;
    private float mOutputMax = 255.0f;
    private boolean mIsFilterR = true;
    private boolean mIsFilterG = true;
    private boolean mIsFilterB = true;

    private static int interpolate(float f, float f2, float f3, float f4, float f5, int i) {
        float pow;
        float f6 = i;
        if (f6 <= f) {
            return (int) f4;
        }
        if (f6 >= f3) {
            return (int) f5;
        }
        if (f2 == 1.0f) {
            pow = f4 + (((f6 - f) * (f5 - f4)) / (f3 - f));
        } else {
            pow = f4 + ((f5 - f4) * (1.0f - ((float) Math.pow(1.0f - ((f6 - f) / (f3 - f)), f2))));
        }
        return (int) pow;
    }

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter
    public void processData(ImageData imageData) {
        int i = imageData.width;
        int i2 = imageData.height;
        int[] iArr = imageData.pixels;
        for (int i3 = 0; i3 < i; i3++) {
            for (int i4 = 0; i4 < i2; i4++) {
                int i5 = (i4 * i) + i3;
                int i6 = iArr[i5];
                int i7 = (16711680 & i6) >>> 16;
                int i8 = (65280 & i6) >>> 8;
                int i9 = i6 & 255;
                if (this.mIsFilterR) {
                    i7 = interpolate(this.mInputMin, this.mInputMiddle, this.mInputMax, this.mOutputMin, this.mOutputMax, i7);
                }
                if (this.mIsFilterG) {
                    i8 = interpolate(this.mInputMin, this.mInputMiddle, this.mInputMax, this.mOutputMin, this.mOutputMax, i8);
                }
                if (this.mIsFilterB) {
                    i9 = interpolate(this.mInputMin, this.mInputMiddle, this.mInputMax, this.mOutputMin, this.mOutputMax, i9);
                }
                iArr[i5] = (i6 & (-16777216)) | i9 | (i7 << 16) | (i8 << 8);
            }
        }
    }

    public void setChannel(String str) {
        boolean[] zArr = new boolean[3];
        ImageFilterUtils.checkChannelParam(str, zArr);
        this.mIsFilterR = zArr[0];
        this.mIsFilterG = zArr[1];
        this.mIsFilterB = zArr[2];
    }

    public void setInputMax(float f) {
        this.mInputMax = ImageFilterUtils.clamp(2.0f, f, 255.0f);
    }

    public void setInputMiddle(float f) {
        this.mInputMiddle = ImageFilterUtils.clamp(1.0E-4f, f, 9.9999f);
    }

    public void setInputMin(float f) {
        this.mInputMin = ImageFilterUtils.clamp(0.0f, f, 253.0f);
    }

    public void setOutputMax(float f) {
        this.mOutputMax = ImageFilterUtils.clamp(0.0f, f, 255.0f);
    }

    public void setOutputMin(float f) {
        this.mOutputMin = ImageFilterUtils.clamp(0.0f, f, 255.0f);
    }
}
