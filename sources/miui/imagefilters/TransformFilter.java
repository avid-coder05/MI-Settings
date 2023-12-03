package miui.imagefilters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import miui.imagefilters.FilterParamType;
import miui.imagefilters.IImageFilter;

/* loaded from: classes3.dex */
public class TransformFilter extends IImageFilter.AbstractImageFilter {
    private float[] mPointsMapping;
    private Paint mPaint = new Paint(3);
    private boolean mBasedOnContent = false;
    private boolean mKeepAspectRatio = true;
    private int mMinVisibleAlpha = 10;

    private int scanEdge(int i, int i2, int[] iArr, boolean z, boolean z2) {
        int i3 = z ? i : i2;
        if (!z) {
            i2 = i;
        }
        int i4 = z2 ? 0 : i3 - 1;
        int i5 = z2 ? i3 - 1 : 0;
        while (i4 != i5) {
            for (int i6 = 0; i6 < i2; i6++) {
                if ((iArr[z ? (i6 * i) + i4 : (i4 * i) + i6] >>> 24) > this.mMinVisibleAlpha) {
                    return i4;
                }
            }
            i4 = z2 ? i4 + 1 : i4 - 1;
        }
        return -1;
    }

    @Override // miui.imagefilters.IImageFilter.AbstractImageFilter
    public void processData(ImageData imageData) {
        int i;
        float f;
        int i2 = imageData.width;
        int i3 = imageData.height;
        int[] iArr = imageData.pixels;
        float f2 = i2;
        float f3 = i3;
        float[] fArr = this.mPointsMapping;
        float f4 = 0.0f;
        if (fArr == null) {
            fArr = new float[]{0.0f, 0.0f, f2, 0.0f, f2, f3, 0.0f, f3};
        }
        float[] fArr2 = fArr;
        if (this.mBasedOnContent) {
            i = 8;
            float scanEdge = scanEdge(i2, i3, iArr, true, true);
            f = scanEdge(i2, i3, iArr, false, true);
            float scanEdge2 = scanEdge(i2, i3, iArr, true, false);
            float scanEdge3 = scanEdge(i2, i3, iArr, false, false);
            if (scanEdge == -1.0f || scanEdge == scanEdge2 || f == scanEdge3) {
                return;
            }
            if (this.mKeepAspectRatio) {
                float f5 = scanEdge2 - scanEdge;
                float f6 = scanEdge3 - f;
                float f7 = f5 / f2;
                float f8 = f6 / f3;
                if (f7 > f8) {
                    float f9 = ((f7 * f3) - f6) / 2.0f;
                    f3 = scanEdge3 + f9;
                    f -= f9;
                } else {
                    float f10 = ((f8 * f2) - f5) / 2.0f;
                    f4 = scanEdge - f10;
                    f2 = scanEdge2 + f10;
                    f3 = scanEdge3;
                }
            } else {
                f3 = scanEdge3;
            }
            f2 = scanEdge2;
            f4 = scanEdge;
        } else {
            i = 8;
            f = 0.0f;
        }
        float[] fArr3 = new float[i];
        fArr3[0] = f4;
        fArr3[1] = f;
        fArr3[2] = f2;
        fArr3[3] = f;
        fArr3[4] = f2;
        fArr3[5] = f3;
        fArr3[6] = f4;
        fArr3[7] = f3;
        Matrix matrix = new Matrix();
        if (matrix.setPolyToPoly(fArr3, 0, fArr2, 0, 4)) {
            Bitmap imageDataToBitmap = ImageData.imageDataToBitmap(imageData);
            Bitmap createBitmap = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
            new Canvas(createBitmap).drawBitmap(imageDataToBitmap, matrix, this.mPaint);
            imageData.pixels = ImageData.bitmapToImageData(createBitmap).pixels;
        }
    }

    public void setBasedOnContent(boolean z) {
        this.mBasedOnContent = z;
    }

    public void setContentKeepAspectRatio(boolean z) {
        this.mKeepAspectRatio = z;
    }

    public void setMinVisibleAlpha(int i) {
        this.mMinVisibleAlpha = ImageFilterUtils.clamp(0, i, 255);
    }

    @FilterParamType(FilterParamType.ParamType.ICON_SIZE)
    public void setPointsMapping(float[] fArr) {
        if (fArr == null || fArr.length == 8) {
            this.mPointsMapping = fArr;
        }
    }
}
