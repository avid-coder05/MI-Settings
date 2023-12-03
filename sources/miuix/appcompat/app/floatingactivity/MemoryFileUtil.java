package miuix.appcompat.app.floatingactivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import java.nio.ByteBuffer;
import java.util.HashMap;
import miuix.appcompat.app.floatingactivity.multiapp.IFloatingService;

/* loaded from: classes5.dex */
public class MemoryFileUtil {
    public static Bitmap readBitmap(Bundle bundle) {
        HashMap hashMap = (HashMap) bundle.getSerializable("parcelFile");
        int i = bundle.getInt("parcelFileLength");
        int i2 = bundle.getInt("key_width");
        int i3 = bundle.getInt("key_height");
        byte[] readFromMemory = readFromMemory(hashMap, i);
        Bitmap bitmap = null;
        if (readFromMemory == null) {
            Log.d("MemoryFileUtil", "getSnapShot with data is null");
            return null;
        }
        try {
            bitmap = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(readFromMemory));
            return bitmap;
        } catch (IllegalArgumentException e) {
            Log.w("MemoryFileUtil", "catch illegal argument exception", e);
            return bitmap;
        } catch (OutOfMemoryError e2) {
            Log.w("MemoryFileUtil", "catch oom exception", e2);
            return bitmap;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x0041 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static byte[] readFromMemory(java.util.HashMap<java.lang.String, android.os.ParcelFileDescriptor> r5, int r6) {
        /*
            java.lang.String r0 = "catch close fd error"
            java.lang.String r1 = "MemoryFileUtil"
            java.lang.String r2 = "parcelFile"
            java.lang.Object r5 = r5.get(r2)
            android.os.ParcelFileDescriptor r5 = (android.os.ParcelFileDescriptor) r5
            r2 = 0
            if (r5 == 0) goto L4a
            byte[] r6 = new byte[r6]
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            java.io.FileDescriptor r4 = r5.getFileDescriptor()     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b
            r3.read(r6)     // Catch: java.lang.Exception -> L27 java.lang.Throwable -> L3d
            r5.close()     // Catch: java.io.IOException -> L22
            goto L26
        L22:
            r5 = move-exception
            android.util.Log.w(r1, r0, r5)
        L26:
            return r6
        L27:
            r6 = move-exception
            goto L2d
        L29:
            r6 = move-exception
            goto L3f
        L2b:
            r6 = move-exception
            r3 = r2
        L2d:
            java.lang.String r4 = "catch read from memory error"
            android.util.Log.w(r1, r4, r6)     // Catch: java.lang.Throwable -> L3d
            if (r3 == 0) goto L4a
            r5.close()     // Catch: java.io.IOException -> L38
            goto L4a
        L38:
            r5 = move-exception
            android.util.Log.w(r1, r0, r5)
            goto L4a
        L3d:
            r6 = move-exception
            r2 = r3
        L3f:
            if (r2 == 0) goto L49
            r5.close()     // Catch: java.io.IOException -> L45
            goto L49
        L45:
            r5 = move-exception
            android.util.Log.w(r1, r0, r5)
        L49:
            throw r6
        L4a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.appcompat.app.floatingactivity.MemoryFileUtil.readFromMemory(java.util.HashMap, int):byte[]");
    }

    public static void sendToFdServer(IFloatingService iFloatingService, byte[] bArr, int i, int i2, int i3, String str, int i4) {
        ParcelFileDescriptor writeToMemory = writeToMemory(bArr, i);
        HashMap hashMap = new HashMap(1);
        hashMap.put("parcelFile", writeToMemory);
        Bundle bundle = new Bundle();
        bundle.putSerializable("parcelFile", hashMap);
        bundle.putInt("parcelFileLength", i);
        bundle.putInt("key_width", i2);
        bundle.putInt("key_height", i3);
        bundle.putInt("key_task_id", i4);
        bundle.putString("key_request_identity", str);
        if (iFloatingService != null) {
            try {
                iFloatingService.callServiceMethod(7, bundle);
            } catch (RemoteException e) {
                Log.w("MemoryFileUtil", "catch stash snapshot to service error", e);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0037, code lost:
    
        if (r1 == null) goto L16;
     */
    /* JADX WARN: Removed duplicated region for block: B:20:0x003f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.os.ParcelFileDescriptor writeToMemory(byte[] r4, int r5) {
        /*
            r0 = 0
            android.os.MemoryFile r1 = new android.os.MemoryFile     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            java.lang.String r2 = ""
            r1.<init>(r2, r5)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            r2 = 0
            r1.writeBytes(r4, r2, r2, r5)     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L3b
            java.lang.Class<android.os.MemoryFile> r4 = android.os.MemoryFile.class
            java.lang.String r5 = "getFileDescriptor"
            java.lang.Class[] r3 = new java.lang.Class[r2]     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L3b
            java.lang.reflect.Method r4 = r4.getDeclaredMethod(r5, r3)     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L3b
            r5 = 1
            r4.setAccessible(r5)     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L3b
            java.lang.Object[] r5 = new java.lang.Object[r2]     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L3b
            java.lang.Object r4 = r4.invoke(r1, r5)     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L3b
            java.io.FileDescriptor r4 = (java.io.FileDescriptor) r4     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L3b
            android.os.ParcelFileDescriptor r0 = android.os.ParcelFileDescriptor.dup(r4)     // Catch: java.lang.Exception -> L2a java.lang.Throwable -> L3b
        L26:
            r1.close()
            goto L3a
        L2a:
            r4 = move-exception
            goto L30
        L2c:
            r4 = move-exception
            goto L3d
        L2e:
            r4 = move-exception
            r1 = r0
        L30:
            java.lang.String r5 = "MemoryFileUtil"
            java.lang.String r2 = "catch write to memory error"
            android.util.Log.w(r5, r2, r4)     // Catch: java.lang.Throwable -> L3b
            if (r1 == 0) goto L3a
            goto L26
        L3a:
            return r0
        L3b:
            r4 = move-exception
            r0 = r1
        L3d:
            if (r0 == 0) goto L42
            r0.close()
        L42:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.appcompat.app.floatingactivity.MemoryFileUtil.writeToMemory(byte[], int):android.os.ParcelFileDescriptor");
    }
}
