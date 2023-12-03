package miui.imagefilters;

import android.graphics.Bitmap;

/* loaded from: classes3.dex */
public class ImageData {
    private int[] backPixels;
    int height;
    private int mHashCode = 0;
    int[] pixels;
    int width;

    public ImageData(int i, int i2) {
        this.width = i;
        this.height = i2;
        this.pixels = new int[i * i2];
    }

    public static ImageData bitmapToImageData(Bitmap bitmap) {
        ImageData imageData = new ImageData(bitmap.getWidth(), bitmap.getHeight());
        int[] iArr = imageData.pixels;
        int i = imageData.width;
        bitmap.getPixels(iArr, 0, i, 0, 0, i, imageData.height);
        imageData.generalRandomNum(100);
        return imageData;
    }

    public static Bitmap imageDataToBitmap(ImageData imageData) {
        return Bitmap.createBitmap(imageData.pixels, imageData.width, imageData.height, Bitmap.Config.ARGB_8888);
    }

    public int generalRandomNum(int i) {
        int i2 = this.mHashCode;
        if (i2 == 0) {
            int i3 = this.width / 8;
            int i4 = this.height / 8;
            for (int i5 = 1; i5 < 8; i5++) {
                i2 ^= this.pixels[((i5 * i4) * this.width) + (i5 * i3)];
            }
            if (i2 == 0) {
                i2 = i;
            }
            i2 &= Integer.MAX_VALUE;
        }
        this.mHashCode = i2;
        return i2 % i;
    }

    public int[] getBackPixels() {
        int[] iArr = this.backPixels;
        if (iArr == null || iArr.length != this.pixels.length) {
            this.backPixels = new int[this.pixels.length];
        }
        return this.backPixels;
    }

    public void swapPixels() {
        int[] iArr = this.pixels;
        this.pixels = this.backPixels;
        this.backPixels = iArr;
    }
}
