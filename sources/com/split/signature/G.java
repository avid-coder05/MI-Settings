package com.split.signature;

import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import miui.util.RSAUtils;

/* loaded from: classes2.dex */
public class G implements A {
    private final ByteBuffer a;

    private G(ByteBuffer byteBuffer) {
        this.a = byteBuffer.slice();
    }

    private static int a(int i, int i2) {
        return b(b(i), b(i2));
    }

    private static long a(long j) {
        return ((j + PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) - 1) / PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED;
    }

    private static long a(ByteBuffer byteBuffer, int i) {
        return byteBuffer.getInt(i) & 4294967295L;
    }

    private static long a(ByteBuffer byteBuffer, long j) throws D {
        a(byteBuffer);
        long a = a(byteBuffer, byteBuffer.position() + 16);
        if (a < j) {
            a(byteBuffer);
            if (a(byteBuffer, byteBuffer.position() + 12) + a == j) {
                return a;
            }
            throw new D("ZIP Central Directory is not immediately followed by End of Central Directory");
        }
        StringBuilder sb = new StringBuilder(122);
        sb.append("ZIP Central Directory offset out of range: ");
        sb.append(a);
        sb.append(". ZIP End of Central Directory offset: ");
        sb.append(j);
        throw new D(sb.toString());
    }

    private static Pair<ByteBuffer, Long> a(RandomAccessFile randomAccessFile, int i) throws IOException {
        int i2;
        if (i < 0 || i > 65535) {
            StringBuilder sb = new StringBuilder(27);
            sb.append("maxCommentSize: ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }
        long length = randomAccessFile.length();
        if (length < 22) {
            return null;
        }
        ByteBuffer allocate = ByteBuffer.allocate(((int) Math.min(i, length - 22)) + 22);
        allocate.order(ByteOrder.LITTLE_ENDIAN);
        long capacity = length - allocate.capacity();
        randomAccessFile.seek(capacity);
        randomAccessFile.readFully(allocate.array(), allocate.arrayOffset(), allocate.capacity());
        a(allocate);
        int capacity2 = allocate.capacity();
        if (capacity2 >= 22) {
            int i3 = capacity2 - 22;
            int min = Math.min(i3, 65535);
            for (int i4 = 0; i4 < min; i4++) {
                i2 = i3 - i4;
                if (allocate.getInt(i2) == 101010256 && (allocate.getShort(i2 + 20) & 65535) == i4) {
                    break;
                }
            }
        }
        i2 = -1;
        if (i2 == -1) {
            return null;
        }
        allocate.position(i2);
        ByteBuffer slice = allocate.slice();
        slice.order(ByteOrder.LITTLE_ENDIAN);
        return Pair.create(slice, Long.valueOf(capacity + i2));
    }

    private static Pair<ByteBuffer, Long> a(RandomAccessFile randomAccessFile, long j) throws D, IOException {
        if (j < 32) {
            StringBuilder sb = new StringBuilder(87);
            sb.append("APK too small for APK Signing Block. ZIP Central Directory offset: ");
            sb.append(j);
            throw new D(sb.toString());
        }
        ByteBuffer allocate = ByteBuffer.allocate(24);
        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        allocate.order(byteOrder);
        randomAccessFile.seek(j - allocate.capacity());
        randomAccessFile.readFully(allocate.array(), allocate.arrayOffset(), allocate.capacity());
        if (allocate.getLong(8) == 2334950737559900225L && allocate.getLong(16) == 3617552046287187010L) {
            long j2 = allocate.getLong(0);
            if (j2 < allocate.capacity() || j2 > 2147483639) {
                StringBuilder sb2 = new StringBuilder(57);
                sb2.append("APK Signing Block size out of range: ");
                sb2.append(j2);
                throw new D(sb2.toString());
            }
            int i = (int) (8 + j2);
            long j3 = j - i;
            if (j3 < 0) {
                StringBuilder sb3 = new StringBuilder(59);
                sb3.append("APK Signing Block offset out of range: ");
                sb3.append(j3);
                throw new D(sb3.toString());
            }
            ByteBuffer allocate2 = ByteBuffer.allocate(i);
            allocate2.order(byteOrder);
            randomAccessFile.seek(j3);
            randomAccessFile.readFully(allocate2.array(), allocate2.arrayOffset(), allocate2.capacity());
            long j4 = allocate2.getLong(0);
            if (j4 == j2) {
                return Pair.create(allocate2, Long.valueOf(j3));
            }
            StringBuilder sb4 = new StringBuilder(103);
            sb4.append("APK Signing Block sizes in header and footer do not match: ");
            sb4.append(j4);
            sb4.append(" vs ");
            sb4.append(j2);
            throw new D(sb4.toString());
        }
        throw new D("No APK Signing Block before ZIP Central Directory");
    }

    private static ByteBuffer a(ByteBuffer byteBuffer, int i, int i2) {
        if (i2 < 8) {
            StringBuilder sb = new StringBuilder(38);
            sb.append("end < start: ");
            sb.append(i2);
            sb.append(" < 8");
            throw new IllegalArgumentException(sb.toString());
        }
        int capacity = byteBuffer.capacity();
        if (i2 > byteBuffer.capacity()) {
            StringBuilder sb2 = new StringBuilder(41);
            sb2.append("end > capacity: ");
            sb2.append(i2);
            sb2.append(" > ");
            sb2.append(capacity);
            throw new IllegalArgumentException(sb2.toString());
        }
        int limit = byteBuffer.limit();
        int position = byteBuffer.position();
        try {
            byteBuffer.position(0);
            byteBuffer.limit(i2);
            byteBuffer.position(8);
            ByteBuffer slice = byteBuffer.slice();
            slice.order(byteBuffer.order());
            return slice;
        } finally {
            byteBuffer.position(0);
            byteBuffer.limit(limit);
            byteBuffer.position(position);
        }
    }

    private static void a(int i, byte[] bArr, int i2) {
        bArr[1] = (byte) i;
        bArr[2] = (byte) (i >>> 8);
        bArr[3] = (byte) (i >>> 16);
        bArr[4] = (byte) (i >>> 24);
    }

    private static void a(ByteBuffer byteBuffer) {
        if (byteBuffer.order() != ByteOrder.LITTLE_ENDIAN) {
            throw new IllegalArgumentException("ByteBuffer byte order must be little endian");
        }
    }

    private static void a(Map<Integer, byte[]> map, FileChannel fileChannel, long j, long j2, long j3, ByteBuffer byteBuffer) {
        if (map.isEmpty()) {
            throw new SecurityException("No digests provided");
        }
        B b = new B(fileChannel, 0L, j);
        B b2 = new B(fileChannel, j2, j3 - j2);
        ByteBuffer duplicate = byteBuffer.duplicate();
        duplicate.order(ByteOrder.LITTLE_ENDIAN);
        a(duplicate);
        int position = duplicate.position() + 16;
        if (j < 0 || j > 4294967295L) {
            StringBuilder sb = new StringBuilder(47);
            sb.append("uint32 value of out range: ");
            sb.append(j);
            throw new IllegalArgumentException(sb.toString());
        }
        duplicate.putInt(duplicate.position() + position, (int) j);
        G g = new G(duplicate);
        int size = map.size();
        int[] iArr = new int[size];
        Iterator<Integer> it = map.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            iArr[i] = it.next().intValue();
            i++;
        }
        try {
            byte[][] a = a(iArr, new A[]{b, b2, g});
            for (int i2 = 0; i2 < size; i2++) {
                int i3 = iArr[i2];
                if (!MessageDigest.isEqual(map.get(Integer.valueOf(i3)), a[i2])) {
                    throw new SecurityException(c(i3).concat(" digest of contents did not verify"));
                }
            }
        } catch (DigestException e) {
            throw new SecurityException("Failed to compute digest(s) of contents", e);
        }
    }

    private static boolean a(int i) {
        if (i == 513 || i == 514 || i == 769) {
            return true;
        }
        switch (i) {
            case 257:
            case 258:
            case 259:
            case 260:
                return true;
            default:
                return false;
        }
    }

    private static X509Certificate[] a(ByteBuffer byteBuffer, Map<Integer, byte[]> map, CertificateFactory certificateFactory) throws IOException {
        ByteBuffer b = b(byteBuffer);
        ByteBuffer b2 = b(byteBuffer);
        byte[] c = c(byteBuffer);
        ArrayList arrayList = new ArrayList();
        byte[] bArr = null;
        byte[] bArr2 = null;
        int i = -1;
        int i2 = 0;
        while (b2.hasRemaining()) {
            i2++;
            try {
                ByteBuffer b3 = b(b2);
                if (b3.remaining() < 8) {
                    throw new SecurityException("Signature record too short");
                }
                int i3 = b3.getInt();
                arrayList.add(Integer.valueOf(i3));
                if (a(i3) && (i == -1 || a(i3, i) > 0)) {
                    bArr2 = c(b3);
                    i = i3;
                }
            } catch (IOException | BufferUnderflowException e) {
                StringBuilder sb = new StringBuilder(45);
                sb.append("Failed to parse signature record #");
                sb.append(i2);
                throw new SecurityException(sb.toString(), e);
            }
        }
        if (i == -1) {
            if (i2 == 0) {
                throw new SecurityException("No signatures found");
            }
            throw new SecurityException("No supported signatures found");
        }
        String e2 = e(i);
        Pair<String, ? extends AlgorithmParameterSpec> f = f(i);
        String str = (String) f.first;
        AlgorithmParameterSpec algorithmParameterSpec = (AlgorithmParameterSpec) f.second;
        try {
            PublicKey generatePublic = KeyFactory.getInstance(e2).generatePublic(new X509EncodedKeySpec(c));
            Signature signature = Signature.getInstance(str);
            signature.initVerify(generatePublic);
            if (algorithmParameterSpec != null) {
                signature.setParameter(algorithmParameterSpec);
            }
            signature.update(b);
            if (signature.verify(bArr2)) {
                b.clear();
                ByteBuffer b4 = b(b);
                ArrayList arrayList2 = new ArrayList();
                int i4 = 0;
                while (b4.hasRemaining()) {
                    i4++;
                    try {
                        ByteBuffer b5 = b(b4);
                        if (b5.remaining() < 8) {
                            throw new IOException("Record too short");
                        }
                        int i5 = b5.getInt();
                        arrayList2.add(Integer.valueOf(i5));
                        if (i5 == i) {
                            bArr = c(b5);
                        }
                    } catch (IOException | BufferUnderflowException e3) {
                        StringBuilder sb2 = new StringBuilder(42);
                        sb2.append("Failed to parse digest record #");
                        sb2.append(i4);
                        throw new IOException(sb2.toString(), e3);
                    }
                }
                if (arrayList.equals(arrayList2)) {
                    int b6 = b(i);
                    byte[] put = map.put(Integer.valueOf(b6), bArr);
                    if (put == null || MessageDigest.isEqual(put, bArr)) {
                        ByteBuffer b7 = b(b);
                        ArrayList arrayList3 = new ArrayList();
                        int i6 = 0;
                        while (b7.hasRemaining()) {
                            i6++;
                            byte[] c2 = c(b7);
                            try {
                                arrayList3.add(new X509CertificateEx((X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(c2)), c2));
                            } catch (CertificateException e4) {
                                StringBuilder sb3 = new StringBuilder(41);
                                sb3.append("Failed to decode certificate #");
                                sb3.append(i6);
                                throw new SecurityException(sb3.toString(), e4);
                            }
                        }
                        if (arrayList3.isEmpty()) {
                            throw new SecurityException("No certificates listed");
                        }
                        if (Arrays.equals(c, ((X509Certificate) arrayList3.get(0)).getPublicKey().getEncoded())) {
                            return (X509Certificate[]) arrayList3.toArray(new X509Certificate[arrayList3.size()]);
                        }
                        throw new SecurityException("Public key mismatch between certificate and signature record");
                    }
                    throw new SecurityException(c(b6).concat(" contents digest does not match the digest specified by a preceding signer"));
                }
                throw new SecurityException("Signature algorithms don't match between digests and signatures records");
            }
            throw new SecurityException(String.valueOf(str).concat(" signature did not verify"));
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException e5) {
            StringBuilder sb4 = new StringBuilder(String.valueOf(str).length() + 27);
            sb4.append("Failed to verify ");
            sb4.append(str);
            sb4.append(" signature");
            throw new SecurityException(sb4.toString(), e5);
        }
    }

    private static byte[][] a(int[] iArr, A[] aArr) throws DigestException {
        A[] aArr2 = aArr;
        long j = 0;
        long j2 = 0;
        for (A a : aArr2) {
            j2 += a(a.a());
        }
        if (j2 >= 2097151) {
            StringBuilder sb = new StringBuilder(37);
            sb.append("Too many chunks: ");
            sb.append(j2);
            throw new DigestException(sb.toString());
        }
        int i = (int) j2;
        byte[][] bArr = new byte[iArr.length];
        for (int i2 = 0; i2 < iArr.length; i2++) {
            byte[] bArr2 = new byte[(d(iArr[i2]) * i) + 5];
            bArr2[0] = 90;
            a(i, bArr2, 1);
            bArr[i2] = bArr2;
        }
        byte[] bArr3 = new byte[5];
        bArr3[0] = -91;
        int length = iArr.length;
        MessageDigest[] messageDigestArr = new MessageDigest[length];
        for (int i3 = 0; i3 < iArr.length; i3++) {
            String c = c(iArr[i3]);
            try {
                messageDigestArr[i3] = MessageDigest.getInstance(c);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(c.concat(" digest not supported"), e);
            }
        }
        int length2 = aArr2.length;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (i4 < length2) {
            A a2 = aArr2[i4];
            int i7 = length2;
            int i8 = i4;
            long a3 = a2.a();
            long j3 = j;
            while (a3 > j) {
                int min = (int) Math.min(a3, (long) PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED);
                a(min, bArr3, 1);
                for (int i9 = 0; i9 < length; i9++) {
                    messageDigestArr[i9].update(bArr3);
                }
                try {
                    a2.a(messageDigestArr, j3, min);
                    int i10 = 0;
                    while (i10 < iArr.length) {
                        int i11 = iArr[i10];
                        byte[] bArr4 = bArr3;
                        byte[] bArr5 = bArr[i10];
                        int d = d(i11);
                        A a4 = a2;
                        MessageDigest messageDigest = messageDigestArr[i10];
                        int i12 = length;
                        int digest = messageDigest.digest(bArr5, (i5 * d) + 5, d);
                        if (digest != d) {
                            String algorithm = messageDigest.getAlgorithm();
                            StringBuilder sb2 = new StringBuilder(String.valueOf(algorithm).length() + 46);
                            sb2.append("Unexpected output size of ");
                            sb2.append(algorithm);
                            sb2.append(" digest: ");
                            sb2.append(digest);
                            throw new RuntimeException(sb2.toString());
                        }
                        i10++;
                        bArr3 = bArr4;
                        a2 = a4;
                        length = i12;
                    }
                    long j4 = min;
                    j3 += j4;
                    a3 -= j4;
                    i5++;
                    bArr3 = bArr3;
                    j = 0;
                } catch (IOException e2) {
                    StringBuilder sb3 = new StringBuilder(59);
                    sb3.append("Failed to digest chunk #");
                    sb3.append(i5);
                    sb3.append(" of section #");
                    sb3.append(i6);
                    throw new DigestException(sb3.toString(), e2);
                }
            }
            i6++;
            i4 = i8 + 1;
            aArr2 = aArr;
            length2 = i7;
            j = 0;
        }
        byte[][] bArr6 = new byte[iArr.length];
        for (int i13 = 0; i13 < iArr.length; i13++) {
            int i14 = iArr[i13];
            byte[] bArr7 = bArr[i13];
            String c2 = c(i14);
            try {
                bArr6[i13] = MessageDigest.getInstance(c2).digest(bArr7);
            } catch (NoSuchAlgorithmException e3) {
                throw new RuntimeException(c2.concat(" digest not supported"), e3);
            }
        }
        return bArr6;
    }

    private static X509Certificate[][] a(RandomAccessFile randomAccessFile) throws IOException, D {
        return a(randomAccessFile.getChannel(), b(randomAccessFile));
    }

    public static X509Certificate[][] a(String str) throws IOException, D {
        RandomAccessFile randomAccessFile = new RandomAccessFile(str, "r");
        try {
            X509Certificate[][] a = a(randomAccessFile);
            randomAccessFile.close();
            return a;
        } finally {
            try {
                randomAccessFile.close();
            } catch (IOException unused) {
            }
        }
    }

    private static X509Certificate[][] a(FileChannel fileChannel, C c) {
        HashMap hashMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            try {
                ByteBuffer b = b(c.a);
                int i = 0;
                while (b.hasRemaining()) {
                    i++;
                    try {
                        arrayList.add(a(b(b), hashMap, certificateFactory));
                    } catch (IOException | SecurityException | BufferUnderflowException unused) {
                    }
                }
                if (i > 0) {
                    if (hashMap.isEmpty()) {
                        throw new SecurityException("No content digests found");
                    }
                    a(hashMap, fileChannel, c.b, c.c, c.d, c.e);
                    return (X509Certificate[][]) arrayList.toArray(new X509Certificate[arrayList.size()]);
                }
                throw new SecurityException("No signers found");
            } catch (Throwable th) {
                throw new SecurityException("Failed to read list of signers", th);
            }
        } catch (Throwable th2) {
            throw new RuntimeException("Failed to obtain X.509 CertificateFactory", th2);
        }
    }

    private static int b(int i) {
        if (i != 513) {
            if (i != 514) {
                if (i != 769) {
                    switch (i) {
                        case 257:
                        case 259:
                            return 1;
                        case 258:
                        case 260:
                            return 2;
                        default:
                            String valueOf = String.valueOf(Long.toHexString(i));
                            throw new IllegalArgumentException(valueOf.length() != 0 ? "Unknown signature algorithm: 0x".concat(valueOf) : new String("Unknown signature algorithm: 0x"));
                    }
                }
                return 1;
            }
            return 2;
        }
        return 1;
    }

    private static int b(int i, int i2) {
        if (i == 1) {
            if (i2 != 1) {
                if (i2 == 2) {
                    return -1;
                }
                StringBuilder sb = new StringBuilder(37);
                sb.append("Unknown digestAlgorithm2: ");
                sb.append(i2);
                throw new IllegalArgumentException(sb.toString());
            }
            return 0;
        } else if (i != 2) {
            StringBuilder sb2 = new StringBuilder(37);
            sb2.append("Unknown digestAlgorithm1: ");
            sb2.append(i);
            throw new IllegalArgumentException(sb2.toString());
        } else if (i2 != 1) {
            if (i2 == 2) {
                return 0;
            }
            StringBuilder sb3 = new StringBuilder(37);
            sb3.append("Unknown digestAlgorithm2: ");
            sb3.append(i2);
            throw new IllegalArgumentException(sb3.toString());
        } else {
            return 1;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x004b  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x002c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static com.split.signature.C b(java.io.RandomAccessFile r11) throws java.io.IOException, com.split.signature.D {
        /*
            android.util.Pair r0 = c(r11)
            java.lang.Object r1 = r0.first
            r10 = r1
            java.nio.ByteBuffer r10 = (java.nio.ByteBuffer) r10
            java.lang.Object r0 = r0.second
            java.lang.Long r0 = (java.lang.Long) r0
            long r8 = r0.longValue()
            r0 = 20
            long r0 = r8 - r0
            r2 = 0
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 < 0) goto L29
            r11.seek(r0)
            int r0 = r11.readInt()
            r1 = 1347094023(0x504b0607, float:1.3624679E10)
            if (r0 != r1) goto L29
            r0 = 1
            goto L2a
        L29:
            r0 = 0
        L2a:
            if (r0 != 0) goto L4b
            long r6 = a(r10, r8)
            android.util.Pair r11 = a(r11, r6)
            java.lang.Object r0 = r11.first
            java.nio.ByteBuffer r0 = (java.nio.ByteBuffer) r0
            java.lang.Object r11 = r11.second
            java.lang.Long r11 = (java.lang.Long) r11
            long r4 = r11.longValue()
            java.nio.ByteBuffer r3 = d(r0)
            com.split.signature.C r11 = new com.split.signature.C
            r2 = r11
            r2.<init>(r3, r4, r6, r8, r10)
            return r11
        L4b:
            com.split.signature.D r11 = new com.split.signature.D
            java.lang.String r0 = "ZIP64 APK not supported"
            r11.<init>(r0)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.split.signature.G.b(java.io.RandomAccessFile):com.split.signature.C");
    }

    private static ByteBuffer b(ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer.remaining() < 4) {
            int remaining = byteBuffer.remaining();
            StringBuilder sb = new StringBuilder(93);
            sb.append("Remaining buffer too short to contain length of length-prefixed field. Remaining: ");
            sb.append(remaining);
            throw new IOException(sb.toString());
        }
        int i = byteBuffer.getInt();
        if (i >= 0) {
            if (i <= byteBuffer.remaining()) {
                return b(byteBuffer, i);
            }
            int remaining2 = byteBuffer.remaining();
            StringBuilder sb2 = new StringBuilder(101);
            sb2.append("Length-prefixed field longer than remaining buffer. Field length: ");
            sb2.append(i);
            sb2.append(", remaining: ");
            sb2.append(remaining2);
            throw new IOException(sb2.toString());
        }
        throw new IllegalArgumentException("Negative length");
    }

    private static ByteBuffer b(ByteBuffer byteBuffer, int i) {
        if (i < 0) {
            StringBuilder sb = new StringBuilder(17);
            sb.append("size: ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }
        int limit = byteBuffer.limit();
        int position = byteBuffer.position();
        int i2 = i + position;
        if (i2 < position || i2 > limit) {
            throw new BufferUnderflowException();
        }
        byteBuffer.limit(i2);
        try {
            ByteBuffer slice = byteBuffer.slice();
            slice.order(byteBuffer.order());
            byteBuffer.position(i2);
            return slice;
        } finally {
            byteBuffer.limit(limit);
        }
    }

    private static Pair<ByteBuffer, Long> c(RandomAccessFile randomAccessFile) throws IOException, D {
        Pair<ByteBuffer, Long> a;
        if (randomAccessFile.length() < 22) {
            a = null;
        } else {
            a = a(randomAccessFile, 0);
            if (a == null) {
                a = a(randomAccessFile, 65535);
            }
        }
        if (a != null) {
            return a;
        }
        long length = randomAccessFile.length();
        StringBuilder sb = new StringBuilder(102);
        sb.append("Not an APK file: ZIP End of Central Directory record not found in file with ");
        sb.append(length);
        sb.append(" bytes");
        throw new D(sb.toString());
    }

    private static String c(int i) {
        if (i != 1) {
            if (i == 2) {
                return "SHA-512";
            }
            StringBuilder sb = new StringBuilder(44);
            sb.append("Unknown content digest algorthm: ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }
        return "SHA-256";
    }

    private static byte[] c(ByteBuffer byteBuffer) throws IOException {
        int i = byteBuffer.getInt();
        if (i >= 0) {
            if (i <= byteBuffer.remaining()) {
                byte[] bArr = new byte[i];
                byteBuffer.get(bArr);
                return bArr;
            }
            int remaining = byteBuffer.remaining();
            StringBuilder sb = new StringBuilder(90);
            sb.append("Underflow while reading length-prefixed value. Length: ");
            sb.append(i);
            sb.append(", available: ");
            sb.append(remaining);
            throw new IOException(sb.toString());
        }
        throw new IOException("Negative length");
    }

    private static int d(int i) {
        if (i != 1) {
            if (i == 2) {
                return 64;
            }
            StringBuilder sb = new StringBuilder(44);
            sb.append("Unknown content digest algorthm: ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }
        return 32;
    }

    private static ByteBuffer d(ByteBuffer byteBuffer) throws D {
        e(byteBuffer);
        ByteBuffer a = a(byteBuffer, 8, byteBuffer.capacity() - 24);
        int i = 0;
        while (a.hasRemaining()) {
            i++;
            if (a.remaining() < 8) {
                StringBuilder sb = new StringBuilder(70);
                sb.append("Insufficient data to read size of APK Signing Block entry #");
                sb.append(i);
                throw new D(sb.toString());
            }
            long j = a.getLong();
            if (j < 4 || j > 2147483647L) {
                StringBuilder sb2 = new StringBuilder(76);
                sb2.append("APK Signing Block entry #");
                sb2.append(i);
                sb2.append(" size out of range: ");
                sb2.append(j);
                throw new D(sb2.toString());
            }
            int i2 = (int) j;
            int position = a.position() + i2;
            if (i2 > a.remaining()) {
                int remaining = a.remaining();
                StringBuilder sb3 = new StringBuilder(91);
                sb3.append("APK Signing Block entry #");
                sb3.append(i);
                sb3.append(" size out of range: ");
                sb3.append(i2);
                sb3.append(", available: ");
                sb3.append(remaining);
                throw new D(sb3.toString());
            } else if (a.getInt() == 1896449818) {
                return b(a, i2 - 4);
            } else {
                a.position(position);
            }
        }
        throw new D("No APK Signature Scheme v2 block in APK Signing Block");
    }

    private static String e(int i) {
        if (i == 513 || i == 514) {
            return "EC";
        }
        if (i != 769) {
            switch (i) {
                case 257:
                case 258:
                case 259:
                case 260:
                    return RSAUtils.KEY_RSA;
                default:
                    String valueOf = String.valueOf(Long.toHexString(i));
                    throw new IllegalArgumentException(valueOf.length() != 0 ? "Unknown signature algorithm: 0x".concat(valueOf) : new String("Unknown signature algorithm: 0x"));
            }
        }
        return "DSA";
    }

    private static void e(ByteBuffer byteBuffer) {
        if (byteBuffer.order() != ByteOrder.LITTLE_ENDIAN) {
            throw new IllegalArgumentException("ByteBuffer byte order must be little endian");
        }
    }

    private static Pair<String, ? extends AlgorithmParameterSpec> f(int i) {
        if (i != 513) {
            if (i != 514) {
                if (i != 769) {
                    switch (i) {
                        case 257:
                            return Pair.create("SHA256withRSA/PSS", new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
                        case 258:
                            return Pair.create("SHA512withRSA/PSS", new PSSParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, 64, 1));
                        case 259:
                            return Pair.create("SHA256withRSA", null);
                        case 260:
                            return Pair.create("SHA512withRSA", null);
                        default:
                            String valueOf = String.valueOf(Long.toHexString(i));
                            throw new IllegalArgumentException(valueOf.length() != 0 ? "Unknown signature algorithm: 0x".concat(valueOf) : new String("Unknown signature algorithm: 0x"));
                    }
                }
                return Pair.create("SHA256withDSA", null);
            }
            return Pair.create("SHA512withECDSA", null);
        }
        return Pair.create("SHA256withECDSA", null);
    }

    @Override // com.split.signature.A
    public long a() {
        return this.a.capacity();
    }

    @Override // com.split.signature.A
    public void a(MessageDigest[] messageDigestArr, long j, int i) {
        ByteBuffer slice;
        synchronized (this.a) {
            int i2 = (int) j;
            this.a.position(i2);
            this.a.limit(i2 + i);
            slice = this.a.slice();
        }
        for (MessageDigest messageDigest : messageDigestArr) {
            slice.position(0);
            messageDigest.update(slice);
        }
    }
}
