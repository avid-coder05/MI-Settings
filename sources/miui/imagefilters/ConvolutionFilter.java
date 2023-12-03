package miui.imagefilters;

import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class ConvolutionFilter extends IImageFilter.AbstractImageFilter {
    private int mBias;
    private int mDivisor;
    private boolean mIsParamsFormated;
    private int[] mMatrix;
    private int mMatrixSideLength;
    private int mRepeatCount;
    private boolean mIsFilterR = true;
    private boolean mIsFilterG = true;
    private boolean mIsFilterB = true;
    private boolean mIsFilterA = true;

    private synchronized void formatParams() {
        if (this.mIsParamsFormated) {
            return;
        }
        this.mIsParamsFormated = true;
        int[] iArr = this.mMatrix;
        if (iArr != null) {
            if (iArr.length == 9) {
                this.mMatrixSideLength = 3;
            } else if (iArr.length == 25) {
                this.mMatrixSideLength = 5;
            } else {
                int sqrt = (int) Math.sqrt(iArr.length);
                this.mMatrixSideLength = sqrt;
                if (sqrt * sqrt != this.mMatrix.length) {
                    throw new RuntimeException("matrix must be a square matrix.");
                }
                if (sqrt % 2 != 1) {
                    throw new RuntimeException("matrixX and matrixY must be odd.");
                }
            }
            if (this.mDivisor == 0) {
                int i = 0;
                while (true) {
                    int[] iArr2 = this.mMatrix;
                    if (i >= iArr2.length) {
                        break;
                    }
                    this.mDivisor += iArr2[i];
                    i++;
                }
                if (this.mDivisor == 0) {
                    this.mDivisor = 1;
                }
            }
        }
    }

    private static final int getColor(int[] iArr, int i, int i2, int i3, int i4) {
        return iArr[(ImageFilterUtils.clamp(0, i2, i4 - 1) * i3) + ImageFilterUtils.clamp(0, i, i3 - 1)];
    }

    private void processOnce(ImageData imageData) {
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

    private int processPerPixel(int[] iArr, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        int i8;
        int i9 = (this.mMatrixSideLength - 1) / 2;
        int i10 = -i9;
        int i11 = 0;
        int i12 = 0;
        int i13 = 0;
        int i14 = 0;
        int i15 = 0;
        for (int i16 = i10; i16 <= i9; i16++) {
            int i17 = i10;
            while (i17 <= i9) {
                int i18 = this.mMatrix[i14];
                int color = getColor(iArr, i + i17, i2 + i16, i3, i4);
                if (this.mIsFilterR) {
                    i7 = i9;
                    i8 = 255;
                    i11 += ((color >>> 16) & 255) * i18;
                } else {
                    i7 = i9;
                    i8 = 255;
                }
                if (this.mIsFilterG) {
                    i12 += ((color >>> 8) & i8) * i18;
                }
                if (this.mIsFilterB) {
                    i13 += (color & 255) * i18;
                }
                if (this.mIsFilterA) {
                    i15 += i18 * ((color >>> 24) & 255);
                }
                i14++;
                i17++;
                i9 = i7;
            }
        }
        int color2 = getColor(iArr, i, i2, i3, i4);
        if (this.mIsFilterR) {
            i5 = 255;
            i6 = ImageFilterUtils.clamp(0, (i11 / this.mDivisor) + this.mBias, 255);
        } else {
            i5 = 255;
            i6 = (color2 >>> 16) & 255;
        }
        return ((this.mIsFilterA ? ImageFilterUtils.clamp(0, (i15 / this.mDivisor) + this.mBias, 255) : (color2 >>> 24) & 255) << 24) | (i6 << 16) | ((this.mIsFilterG ? ImageFilterUtils.clamp(0, (i12 / this.mDivisor) + this.mBias, i5) : (color2 >>> 8) & 255) << 8) | (this.mIsFilterB ? ImageFilterUtils.clamp(0, (i13 / this.mDivisor) + this.mBias, i5) : color2 & 255);
    }

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter
    public void processData(ImageData imageData) {
        if (!this.mIsParamsFormated) {
            formatParams();
        }
        if (this.mMatrix == null) {
            return;
        }
        int i = this.mRepeatCount;
        if (i <= 1) {
            i = 1;
        }
        for (int i2 = 0; i2 < i; i2++) {
            processOnce(imageData);
        }
    }

    public void setBias(int i) {
        this.mBias = i;
    }

    public void setChannel(String str) {
        boolean[] zArr = new boolean[4];
        ImageFilterUtils.checkChannelParam(str, zArr);
        this.mIsFilterR = zArr[0];
        this.mIsFilterG = zArr[1];
        this.mIsFilterB = zArr[2];
        this.mIsFilterA = zArr[3];
    }

    public void setDivisor(int i) {
        this.mDivisor = i;
        this.mIsParamsFormated = false;
    }

    public void setMatrix(int[] iArr) {
        this.mMatrix = iArr;
        this.mIsParamsFormated = false;
    }

    public void setRepeatCount(int i) {
        this.mRepeatCount = i;
    }
}
