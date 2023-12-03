package okio;

import java.util.List;
import java.util.RandomAccess;
import kotlin.collections.AbstractList;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

/* compiled from: Options.kt */
/* loaded from: classes5.dex */
public final class Options extends AbstractList<ByteString> implements RandomAccess {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final ByteString[] byteStrings;
    @NotNull
    private final int[] trie;

    /* compiled from: Options.kt */
    /* loaded from: classes5.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private final void buildTrieRecursive(long j, Buffer buffer, int i, List<? extends ByteString> list, int i2, int i3, List<Integer> list2) {
            int i4;
            int i5;
            int i6;
            int i7;
            int i8;
            Buffer buffer2;
            int i9 = i;
            if (!(i2 < i3)) {
                throw new IllegalArgumentException("Failed requirement.".toString());
            }
            if (i2 < i3) {
                int i10 = i2;
                while (true) {
                    int i11 = i10 + 1;
                    if (!(list.get(i10).size() >= i9)) {
                        throw new IllegalArgumentException("Failed requirement.".toString());
                    }
                    if (i11 >= i3) {
                        break;
                    }
                    i10 = i11;
                }
            }
            ByteString byteString = list.get(i2);
            ByteString byteString2 = list.get(i3 - 1);
            int i12 = -1;
            if (i9 == byteString.size()) {
                int intValue = list2.get(i2).intValue();
                int i13 = i2 + 1;
                ByteString byteString3 = list.get(i13);
                i4 = i13;
                i5 = intValue;
                byteString = byteString3;
            } else {
                i4 = i2;
                i5 = -1;
            }
            if (byteString.getByte(i9) == byteString2.getByte(i9)) {
                int min = Math.min(byteString.size(), byteString2.size());
                if (i9 < min) {
                    int i14 = i9;
                    i6 = 0;
                    while (true) {
                        int i15 = i14 + 1;
                        if (byteString.getByte(i14) != byteString2.getByte(i14)) {
                            break;
                        }
                        i6++;
                        if (i15 >= min) {
                            break;
                        }
                        i14 = i15;
                    }
                } else {
                    i6 = 0;
                }
                long intCount = j + getIntCount(buffer) + 2 + i6 + 1;
                buffer.writeInt(-i6);
                buffer.writeInt(i5);
                int i16 = i9 + i6;
                if (i9 < i16) {
                    while (true) {
                        int i17 = i9 + 1;
                        buffer.writeInt(byteString.getByte(i9) & 255);
                        if (i17 >= i16) {
                            break;
                        }
                        i9 = i17;
                    }
                }
                if (i4 + 1 == i3) {
                    if (!(i16 == list.get(i4).size())) {
                        throw new IllegalStateException("Check failed.".toString());
                    }
                    buffer.writeInt(list2.get(i4).intValue());
                    return;
                }
                Buffer buffer3 = new Buffer();
                buffer.writeInt(((int) (getIntCount(buffer3) + intCount)) * (-1));
                buildTrieRecursive(intCount, buffer3, i16, list, i4, i3, list2);
                buffer.writeAll(buffer3);
                return;
            }
            int i18 = i4 + 1;
            int i19 = 1;
            if (i18 < i3) {
                while (true) {
                    int i20 = i18 + 1;
                    if (list.get(i18 - 1).getByte(i9) != list.get(i18).getByte(i9)) {
                        i19++;
                    }
                    if (i20 >= i3) {
                        break;
                    }
                    i18 = i20;
                }
            }
            long intCount2 = j + getIntCount(buffer) + 2 + (i19 * 2);
            buffer.writeInt(i19);
            buffer.writeInt(i5);
            if (i4 < i3) {
                int i21 = i4;
                while (true) {
                    int i22 = i21 + 1;
                    byte b = list.get(i21).getByte(i9);
                    if (i21 == i4 || b != list.get(i21 - 1).getByte(i9)) {
                        buffer.writeInt(b & 255);
                    }
                    if (i22 >= i3) {
                        break;
                    }
                    i21 = i22;
                }
            }
            Buffer buffer4 = new Buffer();
            while (i4 < i3) {
                byte b2 = list.get(i4).getByte(i9);
                int i23 = i4 + 1;
                if (i23 < i3) {
                    int i24 = i23;
                    while (true) {
                        int i25 = i24 + 1;
                        if (b2 != list.get(i24).getByte(i9)) {
                            i7 = i24;
                            break;
                        } else if (i25 >= i3) {
                            break;
                        } else {
                            i24 = i25;
                        }
                    }
                }
                i7 = i3;
                if (i23 == i7 && i9 + 1 == list.get(i4).size()) {
                    buffer.writeInt(list2.get(i4).intValue());
                    i8 = i7;
                    buffer2 = buffer4;
                } else {
                    buffer.writeInt(((int) (intCount2 + getIntCount(buffer4))) * i12);
                    i8 = i7;
                    buffer2 = buffer4;
                    buildTrieRecursive(intCount2, buffer4, i9 + 1, list, i4, i7, list2);
                }
                buffer4 = buffer2;
                i4 = i8;
                i12 = -1;
            }
            buffer.writeAll(buffer4);
        }

        static /* synthetic */ void buildTrieRecursive$default(Companion companion, long j, Buffer buffer, int i, List list, int i2, int i3, List list2, int i4, Object obj) {
            companion.buildTrieRecursive((i4 & 1) != 0 ? 0L : j, buffer, (i4 & 4) != 0 ? 0 : i, list, (i4 & 16) != 0 ? 0 : i2, (i4 & 32) != 0 ? list.size() : i3, list2);
        }

        private final long getIntCount(Buffer buffer) {
            return buffer.size() / 4;
        }

        /* JADX WARN: Code restructure failed: missing block: B:55:0x00e6, code lost:
        
            continue;
         */
        @org.jetbrains.annotations.NotNull
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final okio.Options of(@org.jetbrains.annotations.NotNull okio.ByteString... r17) {
            /*
                Method dump skipped, instructions count: 316
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: okio.Options.Companion.of(okio.ByteString[]):okio.Options");
        }
    }

    private Options(ByteString[] byteStringArr, int[] iArr) {
        this.byteStrings = byteStringArr;
        this.trie = iArr;
    }

    public /* synthetic */ Options(ByteString[] byteStringArr, int[] iArr, DefaultConstructorMarker defaultConstructorMarker) {
        this(byteStringArr, iArr);
    }

    @NotNull
    public static final Options of(@NotNull ByteString... byteStringArr) {
        return Companion.of(byteStringArr);
    }

    @Override // kotlin.collections.AbstractCollection, java.util.Collection
    public final /* bridge */ boolean contains(Object obj) {
        if (obj instanceof ByteString) {
            return contains((ByteString) obj);
        }
        return false;
    }

    public /* bridge */ boolean contains(ByteString byteString) {
        return super.contains((Object) byteString);
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    @NotNull
    public ByteString get(int i) {
        return this.byteStrings[i];
    }

    @NotNull
    public final ByteString[] getByteStrings$external__okio__android_common__okio_lib() {
        return this.byteStrings;
    }

    @Override // kotlin.collections.AbstractCollection
    public int getSize() {
        return this.byteStrings.length;
    }

    @NotNull
    public final int[] getTrie$external__okio__android_common__okio_lib() {
        return this.trie;
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int indexOf(Object obj) {
        if (obj instanceof ByteString) {
            return indexOf((ByteString) obj);
        }
        return -1;
    }

    public /* bridge */ int indexOf(ByteString byteString) {
        return super.indexOf((Object) byteString);
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int lastIndexOf(Object obj) {
        if (obj instanceof ByteString) {
            return lastIndexOf((ByteString) obj);
        }
        return -1;
    }

    public /* bridge */ int lastIndexOf(ByteString byteString) {
        return super.lastIndexOf((Object) byteString);
    }
}
