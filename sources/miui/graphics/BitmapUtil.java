package miui.graphics;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Field;

/* loaded from: classes3.dex */
public class BitmapUtil {
    private static final int COLOR_BYTE_SIZE = 4;
    private static final String TAG = "BitmapUtil";

    public static byte[] getBuffer(Bitmap bitmap) {
        byte[] bArr = null;
        if (Build.VERSION.SDK_INT < 26) {
            try {
                Field declaredField = Bitmap.class.getDeclaredField("mBuffer");
                declaredField.setAccessible(true);
                return (byte[]) declaredField.get(bitmap);
            } catch (Exception e) {
                Log.w(TAG, "get Bitmap.mBuffer failed!", e);
                return null;
            } catch (OutOfMemoryError e2) {
                Log.e(TAG, "failed to get Bitmap.mBuffer", e2);
                return null;
            }
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int byteCount = bitmap.getByteCount() / 4;
        try {
            int[] iArr = new int[byteCount];
            bitmap.getPixels(iArr, 0, bitmap.getRowBytes() / 4, 0, 0, width, height);
            bArr = new byte[bitmap.getByteCount()];
            for (int i = 0; i < byteCount; i++) {
                int i2 = i * 4;
                bArr[i2 + 3] = (byte) ((iArr[i] >> 24) & 255);
                bArr[i2] = (byte) ((iArr[i] >> 16) & 255);
                bArr[i2 + 1] = (byte) ((iArr[i] >> 8) & 255);
                bArr[i2 + 2] = (byte) (iArr[i] & 255);
            }
            return bArr;
        } catch (OutOfMemoryError e3) {
            Log.e(TAG, "failed to get buffer, baseWidth = " + width + ", baseHeight = " + height, e3);
            return bArr;
        }
    }
}
