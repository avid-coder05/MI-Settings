package com.googlecode.leptonica.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.File;

/* loaded from: classes2.dex */
public class ReadFile {
    private static final String LOG_TAG;

    static {
        System.loadLibrary("lept");
        LOG_TAG = ReadFile.class.getSimpleName();
    }

    private static native long nativeReadBitmap(Bitmap bitmap);

    private static native long nativeReadBytes8(byte[] bArr, int i, int i2);

    private static native long nativeReadFile(String str);

    private static native long nativeReadMem(byte[] bArr, int i);

    private static native boolean nativeReplaceBytes8(long j, byte[] bArr, int i, int i2);

    public static Pix readBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(LOG_TAG, "Bitmap must be non-null");
            return null;
        } else if (bitmap.getConfig() != Bitmap.Config.ARGB_8888) {
            Log.e(LOG_TAG, "Bitmap config must be ARGB_8888");
            return null;
        } else {
            long nativeReadBitmap = nativeReadBitmap(bitmap);
            if (nativeReadBitmap == 0) {
                Log.e(LOG_TAG, "Failed to read pix from bitmap");
                return null;
            }
            return new Pix(nativeReadBitmap);
        }
    }

    public static Pix readBytes8(byte[] bArr, int i, int i2) {
        if (bArr != null) {
            if (i > 0) {
                if (i2 > 0) {
                    if (bArr.length >= i * i2) {
                        long nativeReadBytes8 = nativeReadBytes8(bArr, i, i2);
                        if (nativeReadBytes8 != 0) {
                            return new Pix(nativeReadBytes8);
                        }
                        throw new RuntimeException("Failed to read pix from memory");
                    }
                    throw new IllegalArgumentException("Array length does not match dimensions");
                }
                throw new IllegalArgumentException("Image height must be greater than 0");
            }
            throw new IllegalArgumentException("Image width must be greater than 0");
        }
        throw new IllegalArgumentException("Byte array must be non-null");
    }

    public static Pix readFile(File file) {
        if (file == null) {
            Log.e(LOG_TAG, "File must be non-null");
            return null;
        } else if (!file.exists()) {
            Log.e(LOG_TAG, "File does not exist");
            return null;
        } else if (!file.canRead()) {
            Log.e(LOG_TAG, "Cannot read file");
            return null;
        } else {
            long nativeReadFile = nativeReadFile(file.getAbsolutePath());
            if (nativeReadFile != 0) {
                return new Pix(nativeReadFile);
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            if (decodeFile == null) {
                Log.e(LOG_TAG, "Cannot decode bitmap");
                return null;
            }
            Pix readBitmap = readBitmap(decodeFile);
            decodeFile.recycle();
            return readBitmap;
        }
    }

    public static Pix readMem(byte[] bArr) {
        if (bArr == null) {
            Log.e(LOG_TAG, "Image data byte array must be non-null");
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
        Pix readBitmap = readBitmap(decodeByteArray);
        decodeByteArray.recycle();
        return readBitmap;
    }

    public static boolean replaceBytes8(Pix pix, byte[] bArr, int i, int i2) {
        if (pix != null) {
            if (bArr != null) {
                if (i > 0) {
                    if (i2 > 0) {
                        if (bArr.length >= i * i2) {
                            if (pix.getWidth() == i) {
                                if (pix.getHeight() == i2) {
                                    return nativeReplaceBytes8(pix.getNativePix(), bArr, i, i2);
                                }
                                throw new IllegalArgumentException("Source pix height does not match image height");
                            }
                            throw new IllegalArgumentException("Source pix width does not match image width");
                        }
                        throw new IllegalArgumentException("Array length does not match dimensions");
                    }
                    throw new IllegalArgumentException("Image height must be greater than 0");
                }
                throw new IllegalArgumentException("Image width must be greater than 0");
            }
            throw new IllegalArgumentException("Byte array must be non-null");
        }
        throw new IllegalArgumentException("Source pix must be non-null");
    }
}
