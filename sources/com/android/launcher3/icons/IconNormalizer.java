package com.android.launcher3.icons;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class IconNormalizer {
    private final RectF mAdaptiveIconBounds;
    private float mAdaptiveIconScale;
    private final Bitmap mBitmap;
    private final Rect mBounds;
    private final Canvas mCanvas;
    private boolean mEnableShapeDetection;
    private final float[] mLeftBorder;
    private final Matrix mMatrix;
    private final int mMaxSize;
    private final Paint mPaintMaskShape;
    private final Paint mPaintMaskShapeOutline;
    private final byte[] mPixels;
    private final float[] mRightBorder;
    private final Path mShapePath;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IconNormalizer(Context context, int i, boolean z) {
        int i2 = i * 2;
        this.mMaxSize = i2;
        Bitmap createBitmap = Bitmap.createBitmap(i2, i2, Bitmap.Config.ALPHA_8);
        this.mBitmap = createBitmap;
        this.mCanvas = new Canvas(createBitmap);
        this.mPixels = new byte[i2 * i2];
        this.mLeftBorder = new float[i2];
        this.mRightBorder = new float[i2];
        this.mBounds = new Rect();
        this.mAdaptiveIconBounds = new RectF();
        Paint paint = new Paint();
        this.mPaintMaskShape = paint;
        paint.setColor(-65536);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        Paint paint2 = new Paint();
        this.mPaintMaskShapeOutline = paint2;
        paint2.setStrokeWidth(context.getResources().getDisplayMetrics().density * 2.0f);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(-16777216);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.mShapePath = new Path();
        this.mMatrix = new Matrix();
        this.mAdaptiveIconScale = 0.0f;
        this.mEnableShapeDetection = z;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x002c, code lost:
    
        if ((r5 * r4) < 0.0f) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x002e, code lost:
    
        if (r3 <= r10) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0030, code lost:
    
        r3 = r3 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0041, code lost:
    
        if (((((r8[r1] - r8[r3]) / (r1 - r3)) - r0[r3]) * r4) < 0.0f) goto L27;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void convertToConvexArray(float[] r8, int r9, int r10, int r11) {
        /*
            int r0 = r8.length
            int r0 = r0 + (-1)
            float[] r0 = new float[r0]
            int r1 = r10 + 1
            r2 = 2139095039(0x7f7fffff, float:3.4028235E38)
            r3 = -1
            r4 = r2
        Lc:
            if (r1 > r11) goto L61
            r5 = r8[r1]
            r6 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 > 0) goto L17
            goto L5e
        L17:
            int r5 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r5 != 0) goto L1d
            r3 = r10
            goto L43
        L1d:
            r5 = r8[r1]
            r6 = r8[r3]
            float r5 = r5 - r6
            int r6 = r1 - r3
            float r6 = (float) r6
            float r5 = r5 / r6
            float r5 = r5 - r4
            float r4 = (float) r9
            float r5 = r5 * r4
            r6 = 0
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 >= 0) goto L43
        L2e:
            if (r3 <= r10) goto L43
            int r3 = r3 + (-1)
            r5 = r8[r1]
            r7 = r8[r3]
            float r5 = r5 - r7
            int r7 = r1 - r3
            float r7 = (float) r7
            float r5 = r5 / r7
            r7 = r0[r3]
            float r5 = r5 - r7
            float r5 = r5 * r4
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 < 0) goto L2e
        L43:
            r4 = r8[r1]
            r5 = r8[r3]
            float r4 = r4 - r5
            int r5 = r1 - r3
            float r5 = (float) r5
            float r4 = r4 / r5
            r5 = r3
        L4d:
            if (r5 >= r1) goto L5d
            r0[r5] = r4
            r6 = r8[r3]
            int r7 = r5 - r3
            float r7 = (float) r7
            float r7 = r7 * r4
            float r6 = r6 + r7
            r8[r5] = r6
            int r5 = r5 + 1
            goto L4d
        L5d:
            r3 = r1
        L5e:
            int r1 = r1 + 1
            goto Lc
        L61:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconNormalizer.convertToConvexArray(float[], int, int, int):void");
    }

    private static float getScale(float f, float f2, float f3) {
        float f4 = f / f2;
        if (f / f3 > (f4 < 0.7853982f ? 0.6597222f : ((1.0f - f4) * 0.040449437f) + 0.6510417f)) {
            return (float) Math.sqrt(r4 / r3);
        }
        return 1.0f;
    }

    private boolean isShape(Path path) {
        if (Math.abs((this.mBounds.width() / this.mBounds.height()) - 1.0f) > 0.05f) {
            return false;
        }
        this.mMatrix.reset();
        this.mMatrix.setScale(this.mBounds.width(), this.mBounds.height());
        Matrix matrix = this.mMatrix;
        Rect rect = this.mBounds;
        matrix.postTranslate(rect.left, rect.top);
        path.transform(this.mMatrix, this.mShapePath);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShape);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShapeOutline);
        return isTransparentBitmap();
    }

    private boolean isTransparentBitmap() {
        Rect rect;
        ByteBuffer wrap = ByteBuffer.wrap(this.mPixels);
        wrap.rewind();
        this.mBitmap.copyPixelsToBuffer(wrap);
        Rect rect2 = this.mBounds;
        int i = rect2.top;
        int i2 = this.mMaxSize;
        int i3 = i * i2;
        int i4 = i2 - rect2.right;
        int i5 = 0;
        while (true) {
            rect = this.mBounds;
            if (i >= rect.bottom) {
                break;
            }
            int i6 = rect.left;
            int i7 = i3 + i6;
            while (i6 < this.mBounds.right) {
                if ((this.mPixels[i7] & 255) > 40) {
                    i5++;
                }
                i7++;
                i6++;
            }
            i3 = i7 + i4;
            i++;
        }
        return ((float) i5) / ((float) (rect.width() * this.mBounds.height())) < 0.005f;
    }

    @TargetApi(26)
    public static float normalizeAdaptiveIcon(Drawable drawable, int i, RectF rectF) {
        Rect rect = new Rect(drawable.getBounds());
        drawable.setBounds(0, 0, i, i);
        Path iconMask = ((AdaptiveIconDrawable) drawable).getIconMask();
        Region region = new Region();
        region.setPath(iconMask, new Region(0, 0, i, i));
        Rect bounds = region.getBounds();
        int area = GraphicsUtils.getArea(region);
        if (rectF != null) {
            float f = i;
            rectF.set(bounds.left / f, bounds.top / f, 1.0f - (bounds.right / f), 1.0f - (bounds.bottom / f));
        }
        drawable.setBounds(rect);
        float f2 = area;
        return getScale(f2, f2, i * i);
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0050, code lost:
    
        if (r4 <= r16.mMaxSize) goto L28;
     */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0084  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00d8 A[Catch: all -> 0x012f, TryCatch #0 {, blocks: (B:4:0x0009, B:6:0x000e, B:8:0x0012, B:10:0x0018, B:12:0x0024, B:13:0x0029, B:16:0x002d, B:20:0x003a, B:32:0x005c, B:36:0x0089, B:42:0x0098, B:43:0x009f, B:47:0x00b0, B:48:0x00ba, B:53:0x00c9, B:55:0x00d8, B:59:0x00ec, B:58:0x00e3, B:60:0x00ef, B:62:0x00fb, B:65:0x010f, B:67:0x0113, B:69:0x0116, B:70:0x011f, B:23:0x0040, B:25:0x004e, B:29:0x0056, B:31:0x005a, B:27:0x0052), top: B:78:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:62:0x00fb A[Catch: all -> 0x012f, TryCatch #0 {, blocks: (B:4:0x0009, B:6:0x000e, B:8:0x0012, B:10:0x0018, B:12:0x0024, B:13:0x0029, B:16:0x002d, B:20:0x003a, B:32:0x005c, B:36:0x0089, B:42:0x0098, B:43:0x009f, B:47:0x00b0, B:48:0x00ba, B:53:0x00c9, B:55:0x00d8, B:59:0x00ec, B:58:0x00e3, B:60:0x00ef, B:62:0x00fb, B:65:0x010f, B:67:0x0113, B:69:0x0116, B:70:0x011f, B:23:0x0040, B:25:0x004e, B:29:0x0056, B:31:0x005a, B:27:0x0052), top: B:78:0x0009 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized float getScale(android.graphics.drawable.Drawable r17, android.graphics.RectF r18, android.graphics.Path r19, boolean[] r20) {
        /*
            Method dump skipped, instructions count: 306
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconNormalizer.getScale(android.graphics.drawable.Drawable, android.graphics.RectF, android.graphics.Path, boolean[]):float");
    }
}
