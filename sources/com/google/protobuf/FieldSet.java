package com.google.protobuf;

import com.google.protobuf.FieldSet.FieldDescriptorLite;
import com.google.protobuf.Internal;
import com.google.protobuf.LazyField;
import com.google.protobuf.MessageLite;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class FieldSet<FieldDescriptorType extends FieldDescriptorLite<FieldDescriptorType>> {
    private static final FieldSet DEFAULT_INSTANCE = new FieldSet(true);
    private boolean isImmutable;
    private boolean hasLazyField = false;
    private final SmallSortedMap<FieldDescriptorType, Object> fields = SmallSortedMap.newFieldMap(16);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.google.protobuf.FieldSet$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$WireFormat$FieldType;
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$WireFormat$JavaType;

        static {
            int[] iArr = new int[WireFormat.FieldType.values().length];
            $SwitchMap$com$google$protobuf$WireFormat$FieldType = iArr;
            try {
                iArr[WireFormat.FieldType.DOUBLE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FLOAT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT64.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT64.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT32.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED64.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED32.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.BOOL.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.GROUP.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.MESSAGE.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.STRING.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.BYTES.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT32.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED32.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED64.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT32.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT64.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.ENUM.ordinal()] = 18;
            } catch (NoSuchFieldError unused18) {
            }
            int[] iArr2 = new int[WireFormat.JavaType.values().length];
            $SwitchMap$com$google$protobuf$WireFormat$JavaType = iArr2;
            try {
                iArr2[WireFormat.JavaType.INT.ordinal()] = 1;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.LONG.ordinal()] = 2;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.FLOAT.ordinal()] = 3;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.DOUBLE.ordinal()] = 4;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.BOOLEAN.ordinal()] = 5;
            } catch (NoSuchFieldError unused23) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.STRING.ordinal()] = 6;
            } catch (NoSuchFieldError unused24) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.BYTE_STRING.ordinal()] = 7;
            } catch (NoSuchFieldError unused25) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.ENUM.ordinal()] = 8;
            } catch (NoSuchFieldError unused26) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$JavaType[WireFormat.JavaType.MESSAGE.ordinal()] = 9;
            } catch (NoSuchFieldError unused27) {
            }
        }
    }

    /* loaded from: classes2.dex */
    public interface FieldDescriptorLite<T extends FieldDescriptorLite<T>> extends Comparable<T> {
        Internal.EnumLiteMap<?> getEnumType();

        WireFormat.JavaType getLiteJavaType();

        WireFormat.FieldType getLiteType();

        int getNumber();

        MessageLite.Builder internalMergeFrom(MessageLite.Builder builder, MessageLite messageLite);

        boolean isPacked();

        boolean isRepeated();
    }

    private FieldSet() {
    }

    private FieldSet(boolean z) {
        makeImmutable();
    }

    private void cloneFieldEntry(Map<FieldDescriptorType, Object> map, Map.Entry<FieldDescriptorType, Object> entry) {
        FieldDescriptorType key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof LazyField) {
            map.put(key, ((LazyField) value).getValue());
        } else {
            map.put(key, value);
        }
    }

    private Object cloneIfMutable(Object obj) {
        if (obj instanceof byte[]) {
            byte[] bArr = (byte[]) obj;
            byte[] bArr2 = new byte[bArr.length];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            return bArr2;
        }
        return obj;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeElementSize(WireFormat.FieldType fieldType, int i, Object obj) {
        int computeTagSize = CodedOutputStream.computeTagSize(i);
        if (fieldType == WireFormat.FieldType.GROUP) {
            computeTagSize *= 2;
        }
        return computeTagSize + computeElementSizeNoTag(fieldType, obj);
    }

    static int computeElementSizeNoTag(WireFormat.FieldType fieldType, Object obj) {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$WireFormat$FieldType[fieldType.ordinal()]) {
            case 1:
                return CodedOutputStream.computeDoubleSizeNoTag(((Double) obj).doubleValue());
            case 2:
                return CodedOutputStream.computeFloatSizeNoTag(((Float) obj).floatValue());
            case 3:
                return CodedOutputStream.computeInt64SizeNoTag(((Long) obj).longValue());
            case 4:
                return CodedOutputStream.computeUInt64SizeNoTag(((Long) obj).longValue());
            case 5:
                return CodedOutputStream.computeInt32SizeNoTag(((Integer) obj).intValue());
            case 6:
                return CodedOutputStream.computeFixed64SizeNoTag(((Long) obj).longValue());
            case 7:
                return CodedOutputStream.computeFixed32SizeNoTag(((Integer) obj).intValue());
            case 8:
                return CodedOutputStream.computeBoolSizeNoTag(((Boolean) obj).booleanValue());
            case 9:
                return CodedOutputStream.computeGroupSizeNoTag((MessageLite) obj);
            case 10:
                return obj instanceof LazyField ? CodedOutputStream.computeLazyFieldSizeNoTag((LazyField) obj) : CodedOutputStream.computeMessageSizeNoTag((MessageLite) obj);
            case 11:
                return obj instanceof ByteString ? CodedOutputStream.computeBytesSizeNoTag((ByteString) obj) : CodedOutputStream.computeStringSizeNoTag((String) obj);
            case 12:
                return obj instanceof ByteString ? CodedOutputStream.computeBytesSizeNoTag((ByteString) obj) : CodedOutputStream.computeByteArraySizeNoTag((byte[]) obj);
            case 13:
                return CodedOutputStream.computeUInt32SizeNoTag(((Integer) obj).intValue());
            case 14:
                return CodedOutputStream.computeSFixed32SizeNoTag(((Integer) obj).intValue());
            case 15:
                return CodedOutputStream.computeSFixed64SizeNoTag(((Long) obj).longValue());
            case 16:
                return CodedOutputStream.computeSInt32SizeNoTag(((Integer) obj).intValue());
            case 17:
                return CodedOutputStream.computeSInt64SizeNoTag(((Long) obj).longValue());
            case 18:
                return obj instanceof Internal.EnumLite ? CodedOutputStream.computeEnumSizeNoTag(((Internal.EnumLite) obj).getNumber()) : CodedOutputStream.computeEnumSizeNoTag(((Integer) obj).intValue());
            default:
                throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
        }
    }

    public static int computeFieldSize(FieldDescriptorLite<?> fieldDescriptorLite, Object obj) {
        WireFormat.FieldType liteType = fieldDescriptorLite.getLiteType();
        int number = fieldDescriptorLite.getNumber();
        if (fieldDescriptorLite.isRepeated()) {
            int i = 0;
            if (fieldDescriptorLite.isPacked()) {
                Iterator it = ((List) obj).iterator();
                while (it.hasNext()) {
                    i += computeElementSizeNoTag(liteType, it.next());
                }
                return CodedOutputStream.computeTagSize(number) + i + CodedOutputStream.computeRawVarint32Size(i);
            }
            Iterator it2 = ((List) obj).iterator();
            while (it2.hasNext()) {
                i += computeElementSize(liteType, number, it2.next());
            }
            return i;
        }
        return computeElementSize(liteType, number, obj);
    }

    public static <T extends FieldDescriptorLite<T>> FieldSet<T> emptySet() {
        return DEFAULT_INSTANCE;
    }

    private int getMessageSetSerializedSize(Map.Entry<FieldDescriptorType, Object> entry) {
        FieldDescriptorType key = entry.getKey();
        Object value = entry.getValue();
        return (key.getLiteJavaType() != WireFormat.JavaType.MESSAGE || key.isRepeated() || key.isPacked()) ? computeFieldSize(key, value) : value instanceof LazyField ? CodedOutputStream.computeLazyFieldMessageSetExtensionSize(entry.getKey().getNumber(), (LazyField) value) : CodedOutputStream.computeMessageSetExtensionSize(entry.getKey().getNumber(), (MessageLite) value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getWireFormatForFieldType(WireFormat.FieldType fieldType, boolean z) {
        if (z) {
            return 2;
        }
        return fieldType.getWireType();
    }

    private boolean isInitialized(Map.Entry<FieldDescriptorType, Object> entry) {
        FieldDescriptorType key = entry.getKey();
        if (key.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            if (key.isRepeated()) {
                Iterator it = ((List) entry.getValue()).iterator();
                while (it.hasNext()) {
                    if (!((MessageLite) it.next()).isInitialized()) {
                        return false;
                    }
                }
            } else {
                Object value = entry.getValue();
                if (!(value instanceof MessageLite)) {
                    if (value instanceof LazyField) {
                        return true;
                    }
                    throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
                } else if (!((MessageLite) value).isInitialized()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void mergeFromField(Map.Entry<FieldDescriptorType, Object> entry) {
        FieldDescriptorType key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof LazyField) {
            value = ((LazyField) value).getValue();
        }
        if (key.isRepeated()) {
            Object field = getField(key);
            if (field == null) {
                field = new ArrayList();
            }
            Iterator it = ((List) value).iterator();
            while (it.hasNext()) {
                ((List) field).add(cloneIfMutable(it.next()));
            }
            this.fields.put((SmallSortedMap<FieldDescriptorType, Object>) key, (FieldDescriptorType) field);
        } else if (key.getLiteJavaType() != WireFormat.JavaType.MESSAGE) {
            this.fields.put((SmallSortedMap<FieldDescriptorType, Object>) key, (FieldDescriptorType) cloneIfMutable(value));
        } else {
            Object field2 = getField(key);
            if (field2 == null) {
                this.fields.put((SmallSortedMap<FieldDescriptorType, Object>) key, (FieldDescriptorType) cloneIfMutable(value));
                return;
            }
            this.fields.put((SmallSortedMap<FieldDescriptorType, Object>) key, (FieldDescriptorType) key.internalMergeFrom(((MessageLite) field2).toBuilder(), (MessageLite) value).build());
        }
    }

    public static <T extends FieldDescriptorLite<T>> FieldSet<T> newFieldSet() {
        return new FieldSet<>();
    }

    public static Object readPrimitiveField(CodedInputStream codedInputStream, WireFormat.FieldType fieldType, boolean z) throws IOException {
        return z ? WireFormat.readPrimitiveField(codedInputStream, fieldType, WireFormat.Utf8Validation.STRICT) : WireFormat.readPrimitiveField(codedInputStream, fieldType, WireFormat.Utf8Validation.LOOSE);
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0024, code lost:
    
        if ((r3 instanceof com.google.protobuf.Internal.EnumLite) == false) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x002d, code lost:
    
        if ((r3 instanceof byte[]) == false) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0030, code lost:
    
        r0 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x001b, code lost:
    
        if ((r3 instanceof com.google.protobuf.LazyField) == false) goto L20;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void verifyType(com.google.protobuf.WireFormat.FieldType r2, java.lang.Object r3) {
        /*
            com.google.protobuf.Internal.checkNotNull(r3)
            int[] r0 = com.google.protobuf.FieldSet.AnonymousClass1.$SwitchMap$com$google$protobuf$WireFormat$JavaType
            com.google.protobuf.WireFormat$JavaType r2 = r2.getJavaType()
            int r2 = r2.ordinal()
            r2 = r0[r2]
            r0 = 1
            r1 = 0
            switch(r2) {
                case 1: goto L42;
                case 2: goto L3f;
                case 3: goto L3c;
                case 4: goto L39;
                case 5: goto L36;
                case 6: goto L33;
                case 7: goto L27;
                case 8: goto L1e;
                case 9: goto L15;
                default: goto L14;
            }
        L14:
            goto L44
        L15:
            boolean r2 = r3 instanceof com.google.protobuf.MessageLite
            if (r2 != 0) goto L31
            boolean r2 = r3 instanceof com.google.protobuf.LazyField
            if (r2 == 0) goto L30
            goto L31
        L1e:
            boolean r2 = r3 instanceof java.lang.Integer
            if (r2 != 0) goto L31
            boolean r2 = r3 instanceof com.google.protobuf.Internal.EnumLite
            if (r2 == 0) goto L30
            goto L31
        L27:
            boolean r2 = r3 instanceof com.google.protobuf.ByteString
            if (r2 != 0) goto L31
            boolean r2 = r3 instanceof byte[]
            if (r2 == 0) goto L30
            goto L31
        L30:
            r0 = r1
        L31:
            r1 = r0
            goto L44
        L33:
            boolean r1 = r3 instanceof java.lang.String
            goto L44
        L36:
            boolean r1 = r3 instanceof java.lang.Boolean
            goto L44
        L39:
            boolean r1 = r3 instanceof java.lang.Double
            goto L44
        L3c:
            boolean r1 = r3 instanceof java.lang.Float
            goto L44
        L3f:
            boolean r1 = r3 instanceof java.lang.Long
            goto L44
        L42:
            boolean r1 = r3 instanceof java.lang.Integer
        L44:
            if (r1 == 0) goto L47
            return
        L47:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "Wrong object type used with protocol message reflection."
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.FieldSet.verifyType(com.google.protobuf.WireFormat$FieldType, java.lang.Object):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void writeElement(CodedOutputStream codedOutputStream, WireFormat.FieldType fieldType, int i, Object obj) throws IOException {
        if (fieldType == WireFormat.FieldType.GROUP) {
            codedOutputStream.writeGroup(i, (MessageLite) obj);
            return;
        }
        codedOutputStream.writeTag(i, getWireFormatForFieldType(fieldType, false));
        writeElementNoTag(codedOutputStream, fieldType, obj);
    }

    static void writeElementNoTag(CodedOutputStream codedOutputStream, WireFormat.FieldType fieldType, Object obj) throws IOException {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$WireFormat$FieldType[fieldType.ordinal()]) {
            case 1:
                codedOutputStream.writeDoubleNoTag(((Double) obj).doubleValue());
                return;
            case 2:
                codedOutputStream.writeFloatNoTag(((Float) obj).floatValue());
                return;
            case 3:
                codedOutputStream.writeInt64NoTag(((Long) obj).longValue());
                return;
            case 4:
                codedOutputStream.writeUInt64NoTag(((Long) obj).longValue());
                return;
            case 5:
                codedOutputStream.writeInt32NoTag(((Integer) obj).intValue());
                return;
            case 6:
                codedOutputStream.writeFixed64NoTag(((Long) obj).longValue());
                return;
            case 7:
                codedOutputStream.writeFixed32NoTag(((Integer) obj).intValue());
                return;
            case 8:
                codedOutputStream.writeBoolNoTag(((Boolean) obj).booleanValue());
                return;
            case 9:
                codedOutputStream.writeGroupNoTag((MessageLite) obj);
                return;
            case 10:
                codedOutputStream.writeMessageNoTag((MessageLite) obj);
                return;
            case 11:
                if (obj instanceof ByteString) {
                    codedOutputStream.writeBytesNoTag((ByteString) obj);
                    return;
                } else {
                    codedOutputStream.writeStringNoTag((String) obj);
                    return;
                }
            case 12:
                if (obj instanceof ByteString) {
                    codedOutputStream.writeBytesNoTag((ByteString) obj);
                    return;
                } else {
                    codedOutputStream.writeByteArrayNoTag((byte[]) obj);
                    return;
                }
            case 13:
                codedOutputStream.writeUInt32NoTag(((Integer) obj).intValue());
                return;
            case 14:
                codedOutputStream.writeSFixed32NoTag(((Integer) obj).intValue());
                return;
            case 15:
                codedOutputStream.writeSFixed64NoTag(((Long) obj).longValue());
                return;
            case 16:
                codedOutputStream.writeSInt32NoTag(((Integer) obj).intValue());
                return;
            case 17:
                codedOutputStream.writeSInt64NoTag(((Long) obj).longValue());
                return;
            case 18:
                if (obj instanceof Internal.EnumLite) {
                    codedOutputStream.writeEnumNoTag(((Internal.EnumLite) obj).getNumber());
                    return;
                } else {
                    codedOutputStream.writeEnumNoTag(((Integer) obj).intValue());
                    return;
                }
            default:
                return;
        }
    }

    public static void writeField(FieldDescriptorLite<?> fieldDescriptorLite, Object obj, CodedOutputStream codedOutputStream) throws IOException {
        WireFormat.FieldType liteType = fieldDescriptorLite.getLiteType();
        int number = fieldDescriptorLite.getNumber();
        if (!fieldDescriptorLite.isRepeated()) {
            if (obj instanceof LazyField) {
                writeElement(codedOutputStream, liteType, number, ((LazyField) obj).getValue());
                return;
            } else {
                writeElement(codedOutputStream, liteType, number, obj);
                return;
            }
        }
        List list = (List) obj;
        if (!fieldDescriptorLite.isPacked()) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                writeElement(codedOutputStream, liteType, number, it.next());
            }
            return;
        }
        codedOutputStream.writeTag(number, 2);
        int i = 0;
        Iterator it2 = list.iterator();
        while (it2.hasNext()) {
            i += computeElementSizeNoTag(liteType, it2.next());
        }
        codedOutputStream.writeRawVarint32(i);
        Iterator it3 = list.iterator();
        while (it3.hasNext()) {
            writeElementNoTag(codedOutputStream, liteType, it3.next());
        }
    }

    private void writeMessageSetTo(Map.Entry<FieldDescriptorType, Object> entry, CodedOutputStream codedOutputStream) throws IOException {
        FieldDescriptorType key = entry.getKey();
        if (key.getLiteJavaType() != WireFormat.JavaType.MESSAGE || key.isRepeated() || key.isPacked()) {
            writeField(key, entry.getValue(), codedOutputStream);
            return;
        }
        Object value = entry.getValue();
        if (value instanceof LazyField) {
            value = ((LazyField) value).getValue();
        }
        codedOutputStream.writeMessageSetExtension(entry.getKey().getNumber(), (MessageLite) value);
    }

    public void addRepeatedField(FieldDescriptorType fielddescriptortype, Object obj) {
        List list;
        if (!fielddescriptortype.isRepeated()) {
            throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
        }
        verifyType(fielddescriptortype.getLiteType(), obj);
        Object field = getField(fielddescriptortype);
        if (field == null) {
            list = new ArrayList();
            this.fields.put((SmallSortedMap<FieldDescriptorType, Object>) fielddescriptortype, (FieldDescriptorType) list);
        } else {
            list = (List) field;
        }
        list.add(obj);
    }

    public void clear() {
        this.fields.clear();
        this.hasLazyField = false;
    }

    public void clearField(FieldDescriptorType fielddescriptortype) {
        this.fields.remove(fielddescriptortype);
        if (this.fields.isEmpty()) {
            this.hasLazyField = false;
        }
    }

    /* renamed from: clone  reason: merged with bridge method [inline-methods] */
    public FieldSet<FieldDescriptorType> m387clone() {
        FieldSet<FieldDescriptorType> newFieldSet = newFieldSet();
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            Map.Entry<FieldDescriptorType, Object> arrayEntryAt = this.fields.getArrayEntryAt(i);
            newFieldSet.setField(arrayEntryAt.getKey(), arrayEntryAt.getValue());
        }
        for (Map.Entry<FieldDescriptorType, Object> entry : this.fields.getOverflowEntries()) {
            newFieldSet.setField(entry.getKey(), entry.getValue());
        }
        newFieldSet.hasLazyField = this.hasLazyField;
        return newFieldSet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Iterator<Map.Entry<FieldDescriptorType, Object>> descendingIterator() {
        return this.hasLazyField ? new LazyField.LazyIterator(this.fields.descendingEntrySet().iterator()) : this.fields.descendingEntrySet().iterator();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof FieldSet) {
            return this.fields.equals(((FieldSet) obj).fields);
        }
        return false;
    }

    public Map<FieldDescriptorType, Object> getAllFields() {
        if (!this.hasLazyField) {
            boolean isImmutable = this.fields.isImmutable();
            SmallSortedMap<FieldDescriptorType, Object> smallSortedMap = this.fields;
            return isImmutable ? smallSortedMap : Collections.unmodifiableMap(smallSortedMap);
        }
        SmallSortedMap newFieldMap = SmallSortedMap.newFieldMap(16);
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            cloneFieldEntry(newFieldMap, this.fields.getArrayEntryAt(i));
        }
        Iterator<Map.Entry<FieldDescriptorType, Object>> it = this.fields.getOverflowEntries().iterator();
        while (it.hasNext()) {
            cloneFieldEntry(newFieldMap, it.next());
        }
        if (this.fields.isImmutable()) {
            newFieldMap.makeImmutable();
        }
        return newFieldMap;
    }

    public Object getField(FieldDescriptorType fielddescriptortype) {
        Object obj = this.fields.get(fielddescriptortype);
        return obj instanceof LazyField ? ((LazyField) obj).getValue() : obj;
    }

    public int getMessageSetSerializedSize() {
        int i = 0;
        for (int i2 = 0; i2 < this.fields.getNumArrayEntries(); i2++) {
            i += getMessageSetSerializedSize(this.fields.getArrayEntryAt(i2));
        }
        Iterator<Map.Entry<FieldDescriptorType, Object>> it = this.fields.getOverflowEntries().iterator();
        while (it.hasNext()) {
            i += getMessageSetSerializedSize(it.next());
        }
        return i;
    }

    public Object getRepeatedField(FieldDescriptorType fielddescriptortype, int i) {
        if (fielddescriptortype.isRepeated()) {
            Object field = getField(fielddescriptortype);
            if (field != null) {
                return ((List) field).get(i);
            }
            throw new IndexOutOfBoundsException();
        }
        throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
    }

    public int getRepeatedFieldCount(FieldDescriptorType fielddescriptortype) {
        if (fielddescriptortype.isRepeated()) {
            Object field = getField(fielddescriptortype);
            if (field == null) {
                return 0;
            }
            return ((List) field).size();
        }
        throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
    }

    public int getSerializedSize() {
        int i = 0;
        for (int i2 = 0; i2 < this.fields.getNumArrayEntries(); i2++) {
            Map.Entry<FieldDescriptorType, Object> arrayEntryAt = this.fields.getArrayEntryAt(i2);
            i += computeFieldSize(arrayEntryAt.getKey(), arrayEntryAt.getValue());
        }
        for (Map.Entry<FieldDescriptorType, Object> entry : this.fields.getOverflowEntries()) {
            i += computeFieldSize(entry.getKey(), entry.getValue());
        }
        return i;
    }

    public boolean hasField(FieldDescriptorType fielddescriptortype) {
        if (fielddescriptortype.isRepeated()) {
            throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
        }
        return this.fields.get(fielddescriptortype) != null;
    }

    public int hashCode() {
        return this.fields.hashCode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEmpty() {
        return this.fields.isEmpty();
    }

    public boolean isImmutable() {
        return this.isImmutable;
    }

    public boolean isInitialized() {
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            if (!isInitialized(this.fields.getArrayEntryAt(i))) {
                return false;
            }
        }
        Iterator<Map.Entry<FieldDescriptorType, Object>> it = this.fields.getOverflowEntries().iterator();
        while (it.hasNext()) {
            if (!isInitialized(it.next())) {
                return false;
            }
        }
        return true;
    }

    public Iterator<Map.Entry<FieldDescriptorType, Object>> iterator() {
        return this.hasLazyField ? new LazyField.LazyIterator(this.fields.entrySet().iterator()) : this.fields.entrySet().iterator();
    }

    public void makeImmutable() {
        if (this.isImmutable) {
            return;
        }
        this.fields.makeImmutable();
        this.isImmutable = true;
    }

    public void mergeFrom(FieldSet<FieldDescriptorType> fieldSet) {
        for (int i = 0; i < fieldSet.fields.getNumArrayEntries(); i++) {
            mergeFromField(fieldSet.fields.getArrayEntryAt(i));
        }
        Iterator<Map.Entry<FieldDescriptorType, Object>> it = fieldSet.fields.getOverflowEntries().iterator();
        while (it.hasNext()) {
            mergeFromField(it.next());
        }
    }

    public void setField(FieldDescriptorType fielddescriptortype, Object obj) {
        if (!fielddescriptortype.isRepeated()) {
            verifyType(fielddescriptortype.getLiteType(), obj);
        } else if (!(obj instanceof List)) {
            throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
        } else {
            ArrayList arrayList = new ArrayList();
            arrayList.addAll((List) obj);
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                verifyType(fielddescriptortype.getLiteType(), it.next());
            }
            obj = arrayList;
        }
        if (obj instanceof LazyField) {
            this.hasLazyField = true;
        }
        this.fields.put((SmallSortedMap<FieldDescriptorType, Object>) fielddescriptortype, (FieldDescriptorType) obj);
    }

    public void setRepeatedField(FieldDescriptorType fielddescriptortype, int i, Object obj) {
        if (!fielddescriptortype.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        Object field = getField(fielddescriptortype);
        if (field == null) {
            throw new IndexOutOfBoundsException();
        }
        verifyType(fielddescriptortype.getLiteType(), obj);
        ((List) field).set(i, obj);
    }

    public void writeMessageSetTo(CodedOutputStream codedOutputStream) throws IOException {
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            writeMessageSetTo(this.fields.getArrayEntryAt(i), codedOutputStream);
        }
        Iterator<Map.Entry<FieldDescriptorType, Object>> it = this.fields.getOverflowEntries().iterator();
        while (it.hasNext()) {
            writeMessageSetTo(it.next(), codedOutputStream);
        }
    }

    public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
        for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
            Map.Entry<FieldDescriptorType, Object> arrayEntryAt = this.fields.getArrayEntryAt(i);
            writeField(arrayEntryAt.getKey(), arrayEntryAt.getValue(), codedOutputStream);
        }
        for (Map.Entry<FieldDescriptorType, Object> entry : this.fields.getOverflowEntries()) {
            writeField(entry.getKey(), entry.getValue(), codedOutputStream);
        }
    }
}
