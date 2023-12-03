package miuix.smooth;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

/* loaded from: classes5.dex */
public class SmoothPathProvider2 {
    private float mSmooth = 0.8f;
    private float mKsi = 0.46f;

    /* loaded from: classes5.dex */
    public static class CornerData {
        public PointF[] bezierAnchorHorizontal = new PointF[4];
        public PointF[] bezierAnchorVertical = new PointF[4];
        public float radius;
        public RectF rect;
        public double smoothForHorizontal;
        public double smoothForVertical;
        public float swapAngle;
        public double thetaForHorizontal;
        public double thetaForVertical;

        public void build(float f, RectF rectF, float f2, float f3, double d, float f4, int i) {
            this.radius = f;
            float width = rectF.width();
            float height = rectF.height();
            float f5 = rectF.left;
            float f6 = rectF.top;
            float f7 = rectF.right;
            float f8 = rectF.bottom;
            this.smoothForHorizontal = SmoothPathProvider2.smoothForWidth(width, this.radius, d, f4);
            this.smoothForVertical = SmoothPathProvider2.smoothForHeight(height, this.radius, d, f4);
            this.thetaForHorizontal = SmoothPathProvider2.thetaForWidth(this.smoothForHorizontal);
            double thetaForHeight = SmoothPathProvider2.thetaForHeight(this.smoothForVertical);
            this.thetaForVertical = thetaForHeight;
            this.swapAngle = (float) SmoothPathProvider2.radToAngle((1.5707963267948966d - thetaForHeight) - this.thetaForHorizontal);
            double d2 = f4;
            double kForWidth = SmoothPathProvider2.kForWidth(this.smoothForHorizontal * d2, this.thetaForHorizontal);
            double mForWidth = SmoothPathProvider2.mForWidth(this.radius, this.thetaForHorizontal);
            double nForWidth = SmoothPathProvider2.nForWidth(this.radius, this.thetaForHorizontal);
            double pForWidth = SmoothPathProvider2.pForWidth(this.radius, this.thetaForHorizontal);
            double xForWidth = SmoothPathProvider2.xForWidth(this.radius, this.thetaForHorizontal);
            double yForWidth = SmoothPathProvider2.yForWidth(kForWidth, xForWidth);
            double kForHeight = SmoothPathProvider2.kForHeight(this.smoothForVertical * d2, this.thetaForVertical);
            double mForHeight = SmoothPathProvider2.mForHeight(this.radius, this.thetaForVertical);
            double nForHeight = SmoothPathProvider2.nForHeight(this.radius, this.thetaForVertical);
            double pForHeight = SmoothPathProvider2.pForHeight(this.radius, this.thetaForVertical);
            double xForHeight = SmoothPathProvider2.xForHeight(this.radius, this.thetaForVertical);
            double yForHeight = SmoothPathProvider2.yForHeight(kForHeight, xForHeight);
            if (i == 0) {
                float f9 = f5 + f2;
                float f10 = f6 + f3;
                float f11 = this.radius;
                this.rect = new RectF(f9, f10, (f11 * 2.0f) + f9, (f11 * 2.0f) + f10);
                double d3 = f9;
                double d4 = f10;
                this.bezierAnchorHorizontal[0] = new PointF((float) (mForWidth + d3), (float) (nForWidth + d4));
                this.bezierAnchorHorizontal[1] = new PointF((float) (pForWidth + d3), f10);
                double d5 = pForWidth + xForWidth;
                this.bezierAnchorHorizontal[2] = new PointF((float) (d5 + d3), f10);
                this.bezierAnchorHorizontal[3] = new PointF((float) (d5 + yForWidth + d3), f10);
                double d6 = xForHeight + pForHeight;
                this.bezierAnchorVertical[0] = new PointF(f9, (float) (d6 + yForHeight + d4));
                this.bezierAnchorVertical[1] = new PointF(f9, (float) (d6 + d4));
                this.bezierAnchorVertical[2] = new PointF(f9, (float) (pForHeight + d4));
                this.bezierAnchorVertical[3] = new PointF((float) (mForHeight + d3), (float) (nForHeight + d4));
            } else if (i == 1) {
                float f12 = f6 + f3;
                float f13 = this.radius;
                float f14 = f7 - f2;
                this.rect = new RectF((f7 - (f13 * 2.0f)) - f2, f12, f14, (f13 * 2.0f) + f12);
                double d7 = f7;
                double d8 = d7 - pForWidth;
                double d9 = d8 - xForWidth;
                double d10 = f2;
                this.bezierAnchorHorizontal[0] = new PointF((float) ((d9 - yForWidth) - d10), f12);
                this.bezierAnchorHorizontal[1] = new PointF((float) (d9 - d10), f12);
                this.bezierAnchorHorizontal[2] = new PointF((float) (d8 - d10), f12);
                double d11 = f12;
                this.bezierAnchorHorizontal[3] = new PointF((float) ((d7 - mForWidth) - d10), (float) (nForWidth + d11));
                this.bezierAnchorVertical[0] = new PointF((float) ((d7 - mForHeight) - d10), (float) (nForHeight + d11));
                this.bezierAnchorVertical[1] = new PointF(f14, (float) (pForHeight + d11));
                double d12 = pForHeight + xForHeight;
                this.bezierAnchorVertical[2] = new PointF(f14, (float) (d12 + d11));
                this.bezierAnchorVertical[3] = new PointF(f14, (float) (d12 + yForHeight + d11));
            } else if (i == 2) {
                float f15 = this.radius;
                float f16 = f7 - f2;
                float f17 = f8 - f3;
                this.rect = new RectF((f7 - (f15 * 2.0f)) - f2, (f8 - (f15 * 2.0f)) - f3, f16, f17);
                double d13 = f7;
                double d14 = f2;
                double d15 = f8;
                double d16 = f3;
                this.bezierAnchorHorizontal[0] = new PointF((float) ((d13 - mForWidth) - d14), (float) ((d15 - nForWidth) - d16));
                double d17 = d13 - pForWidth;
                this.bezierAnchorHorizontal[1] = new PointF((float) (d17 - d14), f17);
                double d18 = d17 - xForWidth;
                this.bezierAnchorHorizontal[2] = new PointF((float) (d18 - d14), f17);
                this.bezierAnchorHorizontal[3] = new PointF((float) ((d18 - yForWidth) - d14), f17);
                double d19 = d15 - pForHeight;
                double d20 = d19 - xForHeight;
                this.bezierAnchorVertical[0] = new PointF(f16, (float) ((d20 - yForHeight) - d16));
                this.bezierAnchorVertical[1] = new PointF(f16, (float) (d20 - d16));
                this.bezierAnchorVertical[2] = new PointF(f16, (float) (d19 - d16));
                this.bezierAnchorVertical[3] = new PointF((float) ((d13 - mForHeight) - d14), (float) ((d15 - nForHeight) - d16));
            } else if (i == 3) {
                float f18 = f5 + f2;
                float f19 = this.radius;
                float f20 = f8 - f3;
                this.rect = new RectF(f18, (f8 - (f19 * 2.0f)) - f3, (f19 * 2.0f) + f18, f20);
                double d21 = pForWidth + xForWidth;
                double d22 = f18;
                this.bezierAnchorHorizontal[0] = new PointF((float) (d21 + yForWidth + d22), f20);
                this.bezierAnchorHorizontal[1] = new PointF((float) (d21 + d22), f20);
                this.bezierAnchorHorizontal[2] = new PointF((float) (pForWidth + d22), f20);
                float f21 = (float) (mForWidth + d22);
                double d23 = f8;
                double d24 = f3;
                this.bezierAnchorHorizontal[3] = new PointF(f21, (float) ((d23 - nForWidth) - d24));
                this.bezierAnchorVertical[0] = new PointF((float) (mForHeight + d22), (float) ((d23 - nForHeight) - d24));
                double d25 = d23 - pForHeight;
                this.bezierAnchorVertical[1] = new PointF(f18, (float) (d25 - d24));
                double d26 = d25 - xForHeight;
                this.bezierAnchorVertical[2] = new PointF(f18, (float) (d26 - d24));
                this.bezierAnchorVertical[3] = new PointF(f18, (float) ((d26 - yForHeight) - d24));
            }
        }
    }

    /* loaded from: classes5.dex */
    public static class SmoothData {
        public float height;
        public float ksi;
        public double smooth;
        public float width;
        public CornerData topLeft = null;
        public CornerData topRight = null;
        public CornerData bottomRight = null;
        public CornerData bottomLeft = null;

        public SmoothData(float f, float f2, double d, float f3) {
            this.width = f;
            this.height = f2;
            this.smooth = d;
            this.ksi = f3;
        }
    }

    private void ensureFourCornerData(SmoothData smoothData) {
        if (smoothData.topLeft == null) {
            smoothData.topLeft = new CornerData();
        }
        if (smoothData.topRight == null) {
            smoothData.topRight = new CornerData();
        }
        if (smoothData.bottomRight == null) {
            smoothData.bottomRight = new CornerData();
        }
        if (smoothData.bottomLeft == null) {
            smoothData.bottomLeft = new CornerData();
        }
    }

    private boolean isFourCornerDataValid(SmoothData smoothData) {
        return smoothData.topLeft == null || smoothData.topRight == null || smoothData.bottomRight == null || smoothData.bottomLeft == null;
    }

    private static boolean isHeightCollapsed(float f, float f2, float f3, double d, float f4) {
        return ((double) f) <= ((double) (f2 + f3)) * ((d * ((double) f4)) + 1.0d);
    }

    private static boolean isWidthCollapsed(float f, float f2, float f3, double d, float f4) {
        return ((double) f) <= ((double) (f2 + f3)) * ((d * ((double) f4)) + 1.0d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double kForHeight(double d, double d2) {
        if (d2 == 0.0d) {
            return 0.0d;
        }
        double d3 = d2 / 2.0d;
        return (((((d * 0.46000000834465027d) + Math.tan(d3)) * 2.0d) * (Math.cos(d2) + 1.0d)) / (Math.tan(d3) * 3.0d)) - 1.0d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double kForWidth(double d, double d2) {
        if (d2 == 0.0d) {
            return 0.0d;
        }
        double d3 = d2 / 2.0d;
        return (((((d * 0.46000000834465027d) + Math.tan(d3)) * 2.0d) * (Math.cos(d2) + 1.0d)) / (Math.tan(d3) * 3.0d)) - 1.0d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double mForHeight(float f, double d) {
        return f * (1.0d - Math.cos(d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double mForWidth(float f, double d) {
        return f * (1.0d - Math.sin(d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double nForHeight(float f, double d) {
        return f * (1.0d - Math.sin(d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double nForWidth(float f, double d) {
        return f * (1.0d - Math.cos(d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double pForHeight(float f, double d) {
        return f * (1.0d - Math.tan(d / 2.0d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double pForWidth(float f, double d) {
        return f * (1.0d - Math.tan(d / 2.0d));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double radToAngle(double d) {
        return (d * 180.0d) / 3.141592653589793d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double smoothForHeight(float f, float f2, double d, float f3) {
        return isHeightCollapsed(f, f2, f2, d, f3) ? Math.max(Math.min(((f / (f2 * 2.0f)) - 1.0f) / f3, 1.0f), 0.0f) : d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double smoothForWidth(float f, float f2, double d, float f3) {
        return isWidthCollapsed(f, f2, f2, d, f3) ? Math.max(Math.min(((f / (f2 * 2.0f)) - 1.0f) / f3, 1.0f), 0.0f) : d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double thetaForHeight(double d) {
        return (d * 3.141592653589793d) / 4.0d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double thetaForWidth(double d) {
        return (d * 3.141592653589793d) / 4.0d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double xForHeight(float f, double d) {
        return ((f * 1.5d) * Math.tan(d / 2.0d)) / (Math.cos(d) + 1.0d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double xForWidth(float f, double d) {
        return ((f * 1.5d) * Math.tan(d / 2.0d)) / (Math.cos(d) + 1.0d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double yForHeight(double d, double d2) {
        return d * d2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static double yForWidth(double d, double d2) {
        return d * d2;
    }

    public SmoothData buildSmoothData(RectF rectF, float f, float f2, float f3) {
        return buildSmoothData(rectF, new float[]{f, f, f, f, f, f, f, f}, f2, f3);
    }

    public SmoothData buildSmoothData(RectF rectF, float[] fArr, float f, float f2) {
        if (fArr == null) {
            return null;
        }
        float ksi = getKsi();
        float smooth = getSmooth();
        float width = rectF.width();
        float height = rectF.height();
        double d = smooth;
        SmoothData smoothData = new SmoothData(width, height, d, ksi);
        float[] fArr2 = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        for (int i = 0; i < Math.min(8, fArr.length); i++) {
            if (!Float.isNaN(fArr[i])) {
                fArr2[i] = fArr[i];
            }
        }
        float f3 = fArr2[0];
        float f4 = fArr2[1];
        float f5 = fArr2[2];
        float f6 = fArr2[3];
        float f7 = fArr2[4];
        float f8 = fArr2[5];
        float f9 = fArr2[6];
        float f10 = fArr2[7];
        if (f3 + f5 > width) {
            f3 = (fArr2[0] * width) / (fArr2[0] + fArr2[2]);
            f5 = (fArr2[2] * width) / (fArr2[0] + fArr2[2]);
        }
        if (f6 + f8 > height) {
            f6 = (fArr2[3] * height) / (fArr2[3] + fArr2[5]);
            f8 = (fArr2[5] * height) / (fArr2[3] + fArr2[5]);
        }
        float f11 = f8;
        if (f7 + f9 > width) {
            f7 = (fArr2[4] * width) / (fArr2[4] + fArr2[6]);
            f9 = (width * fArr2[6]) / (fArr2[4] + fArr2[6]);
        }
        float f12 = f7;
        float f13 = f9;
        if (f10 + f4 > height) {
            f10 = (fArr2[7] * height) / (fArr2[7] + fArr2[1]);
            f4 = (height * fArr2[1]) / (fArr2[7] + fArr2[1]);
        }
        ensureFourCornerData(smoothData);
        smoothData.topLeft.build(Math.min(f3, f4), rectF, f, f2, d, ksi, 0);
        smoothData.topRight.build(Math.min(f5, f6), rectF, f, f2, d, ksi, 1);
        smoothData.bottomRight.build(Math.min(f12, f11), rectF, f, f2, d, ksi, 2);
        smoothData.bottomLeft.build(Math.min(f13, f10), rectF, f, f2, d, ksi, 3);
        return smoothData;
    }

    float getKsi() {
        return this.mKsi;
    }

    float getSmooth() {
        return this.mSmooth;
    }

    public Path getSmoothPath(Path path, SmoothData smoothData) {
        Path path2 = path == null ? new Path() : path;
        path2.reset();
        if (smoothData == null) {
            return path2;
        }
        if (isFourCornerDataValid(smoothData)) {
            path2.addRect(new RectF(0.0f, 0.0f, smoothData.width, smoothData.height), Path.Direction.CCW);
            return path2;
        }
        CornerData cornerData = smoothData.topLeft;
        if (cornerData.swapAngle != 0.0f) {
            path2.arcTo(cornerData.rect, (float) radToAngle(cornerData.thetaForVertical + 3.141592653589793d), smoothData.topLeft.swapAngle);
        } else {
            PointF[] pointFArr = cornerData.bezierAnchorHorizontal;
            path2.moveTo(pointFArr[0].x, pointFArr[0].y);
        }
        CornerData cornerData2 = smoothData.topLeft;
        if (cornerData2.smoothForHorizontal != 0.0d) {
            PointF[] pointFArr2 = cornerData2.bezierAnchorHorizontal;
            path2.cubicTo(pointFArr2[1].x, pointFArr2[1].y, pointFArr2[2].x, pointFArr2[2].y, pointFArr2[3].x, pointFArr2[3].y);
        }
        if (!isWidthCollapsed(smoothData.width, smoothData.topLeft.radius, smoothData.topRight.radius, smoothData.smooth, smoothData.ksi)) {
            PointF[] pointFArr3 = smoothData.topRight.bezierAnchorHorizontal;
            path2.lineTo(pointFArr3[0].x, pointFArr3[0].y);
        }
        CornerData cornerData3 = smoothData.topRight;
        if (cornerData3.smoothForHorizontal != 0.0d) {
            PointF[] pointFArr4 = cornerData3.bezierAnchorHorizontal;
            path2.cubicTo(pointFArr4[1].x, pointFArr4[1].y, pointFArr4[2].x, pointFArr4[2].y, pointFArr4[3].x, pointFArr4[3].y);
        }
        CornerData cornerData4 = smoothData.topRight;
        if (cornerData4.swapAngle != 0.0f) {
            path2.arcTo(cornerData4.rect, (float) radToAngle(cornerData4.thetaForHorizontal + 4.71238898038469d), smoothData.topRight.swapAngle);
        }
        CornerData cornerData5 = smoothData.topRight;
        if (cornerData5.smoothForVertical != 0.0d) {
            PointF[] pointFArr5 = cornerData5.bezierAnchorVertical;
            path2.cubicTo(pointFArr5[1].x, pointFArr5[1].y, pointFArr5[2].x, pointFArr5[2].y, pointFArr5[3].x, pointFArr5[3].y);
        }
        if (!isHeightCollapsed(smoothData.height, smoothData.topRight.radius, smoothData.bottomRight.radius, smoothData.smooth, smoothData.ksi)) {
            PointF[] pointFArr6 = smoothData.bottomRight.bezierAnchorVertical;
            path2.lineTo(pointFArr6[0].x, pointFArr6[0].y);
        }
        CornerData cornerData6 = smoothData.bottomRight;
        if (cornerData6.smoothForVertical != 0.0d) {
            PointF[] pointFArr7 = cornerData6.bezierAnchorVertical;
            path2.cubicTo(pointFArr7[1].x, pointFArr7[1].y, pointFArr7[2].x, pointFArr7[2].y, pointFArr7[3].x, pointFArr7[3].y);
        }
        CornerData cornerData7 = smoothData.bottomRight;
        if (cornerData7.swapAngle != 0.0f) {
            path2.arcTo(cornerData7.rect, (float) radToAngle(cornerData7.thetaForVertical), smoothData.bottomRight.swapAngle);
        }
        CornerData cornerData8 = smoothData.bottomRight;
        if (cornerData8.smoothForHorizontal != 0.0d) {
            PointF[] pointFArr8 = cornerData8.bezierAnchorHorizontal;
            path2.cubicTo(pointFArr8[1].x, pointFArr8[1].y, pointFArr8[2].x, pointFArr8[2].y, pointFArr8[3].x, pointFArr8[3].y);
        }
        if (!isWidthCollapsed(smoothData.width, smoothData.bottomRight.radius, smoothData.bottomLeft.radius, smoothData.smooth, smoothData.ksi)) {
            PointF[] pointFArr9 = smoothData.bottomLeft.bezierAnchorHorizontal;
            path2.lineTo(pointFArr9[0].x, pointFArr9[0].y);
        }
        CornerData cornerData9 = smoothData.bottomLeft;
        if (cornerData9.smoothForHorizontal != 0.0d) {
            PointF[] pointFArr10 = cornerData9.bezierAnchorHorizontal;
            path2.cubicTo(pointFArr10[1].x, pointFArr10[1].y, pointFArr10[2].x, pointFArr10[2].y, pointFArr10[3].x, pointFArr10[3].y);
        }
        CornerData cornerData10 = smoothData.bottomLeft;
        if (cornerData10.swapAngle != 0.0f) {
            path2.arcTo(cornerData10.rect, (float) radToAngle(cornerData10.thetaForHorizontal + 1.5707963267948966d), smoothData.bottomLeft.swapAngle);
        }
        CornerData cornerData11 = smoothData.bottomLeft;
        if (cornerData11.smoothForVertical != 0.0d) {
            PointF[] pointFArr11 = cornerData11.bezierAnchorVertical;
            path2.cubicTo(pointFArr11[1].x, pointFArr11[1].y, pointFArr11[2].x, pointFArr11[2].y, pointFArr11[3].x, pointFArr11[3].y);
        }
        if (!isHeightCollapsed(smoothData.height, smoothData.bottomLeft.radius, smoothData.topLeft.radius, smoothData.smooth, smoothData.ksi)) {
            PointF[] pointFArr12 = smoothData.topLeft.bezierAnchorVertical;
            path2.lineTo(pointFArr12[0].x, pointFArr12[0].y);
        }
        CornerData cornerData12 = smoothData.topLeft;
        if (cornerData12.smoothForVertical != 0.0d) {
            PointF[] pointFArr13 = cornerData12.bezierAnchorVertical;
            path2.cubicTo(pointFArr13[1].x, pointFArr13[1].y, pointFArr13[2].x, pointFArr13[2].y, pointFArr13[3].x, pointFArr13[3].y);
        }
        path2.close();
        return path2;
    }
}
