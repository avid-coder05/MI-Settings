package com.miui.maml.elements.filament;

/* loaded from: classes2.dex */
public class Io {
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0054 A[Catch: IOException -> 0x0050, TRY_LEAVE, TryCatch #5 {IOException -> 0x0050, blocks: (B:31:0x004c, B:35:0x0054), top: B:43:0x004c }] */
    /* JADX WARN: Removed duplicated region for block: B:43:0x004c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r5v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r5v1 */
    /* JADX WARN: Type inference failed for: r5v13, types: [java.nio.ByteBuffer] */
    /* JADX WARN: Type inference failed for: r5v14 */
    /* JADX WARN: Type inference failed for: r5v2 */
    /* JADX WARN: Type inference failed for: r5v3, types: [java.nio.ByteBuffer] */
    /* JADX WARN: Type inference failed for: r5v5 */
    /* JADX WARN: Type inference failed for: r5v6, types: [java.nio.ByteBuffer] */
    /* JADX WARN: Type inference failed for: r5v7, types: [java.nio.ByteBuffer] */
    /* JADX WARN: Type inference failed for: r5v8 */
    /* JADX WARN: Type inference failed for: r5v9, types: [java.nio.ByteBuffer] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.nio.ByteBuffer readAsset(com.miui.maml.ResourceManager r4, java.lang.String r5) {
        /*
            r0 = 1
            r1 = 0
            long[] r0 = new long[r0]     // Catch: java.lang.Throwable -> L36 java.io.IOException -> L39
            java.io.InputStream r4 = r4.getInputStream(r5, r0)     // Catch: java.lang.Throwable -> L36 java.io.IOException -> L39
            if (r4 == 0) goto L23
            r5 = 0
            r2 = r0[r5]     // Catch: java.lang.Throwable -> L36 java.io.IOException -> L39
            int r5 = (int) r2     // Catch: java.lang.Throwable -> L36 java.io.IOException -> L39
            int r5 = java.lang.Math.abs(r5)     // Catch: java.lang.Throwable -> L36 java.io.IOException -> L39
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocate(r5)     // Catch: java.lang.Throwable -> L36 java.io.IOException -> L39
            java.nio.channels.ReadableByteChannel r1 = java.nio.channels.Channels.newChannel(r4)     // Catch: java.io.IOException -> L21 java.lang.Throwable -> L49
            r1.read(r5)     // Catch: java.io.IOException -> L21 java.lang.Throwable -> L49
            r1.close()     // Catch: java.io.IOException -> L21 java.lang.Throwable -> L49
            goto L24
        L21:
            r4 = move-exception
            goto L3b
        L23:
            r5 = r1
        L24:
            if (r1 == 0) goto L2c
            r1.close()     // Catch: java.io.IOException -> L2a
            goto L2c
        L2a:
            r4 = move-exception
            goto L32
        L2c:
            if (r5 == 0) goto L48
            r5.rewind()     // Catch: java.io.IOException -> L2a
            goto L48
        L32:
            r4.printStackTrace()
            goto L48
        L36:
            r4 = move-exception
            r5 = r1
            goto L4a
        L39:
            r4 = move-exception
            r5 = r1
        L3b:
            r4.printStackTrace()     // Catch: java.lang.Throwable -> L49
            if (r1 == 0) goto L43
            r1.close()     // Catch: java.io.IOException -> L2a
        L43:
            if (r5 == 0) goto L48
            r5.rewind()     // Catch: java.io.IOException -> L2a
        L48:
            return r5
        L49:
            r4 = move-exception
        L4a:
            if (r1 == 0) goto L52
            r1.close()     // Catch: java.io.IOException -> L50
            goto L52
        L50:
            r5 = move-exception
            goto L58
        L52:
            if (r5 == 0) goto L5b
            r5.rewind()     // Catch: java.io.IOException -> L50
            goto L5b
        L58:
            r5.printStackTrace()
        L5b:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.filament.Io.readAsset(com.miui.maml.ResourceManager, java.lang.String):java.nio.ByteBuffer");
    }
}
