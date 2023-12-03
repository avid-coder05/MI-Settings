package com.android.settings.backup;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.MessageLiteOrBuilder;
import java.io.IOException;
import java.util.Objects;

/* loaded from: classes.dex */
public final class SettingProtos$LockSetting extends GeneratedMessageLite implements MessageLiteOrBuilder {
    public static final int GUID_FIELD_NUMBER = 1;
    public static final int LUID_FIELD_NUMBER = 2;
    public static final int NAME_FIELD_NUMBER = 3;
    public static final int VALUE_FIELD_NUMBER = 4;
    private static final SettingProtos$LockSetting defaultInstance;
    private static final long serialVersionUID = 0;
    private int bitField0_;
    private Object guid_;
    private Object luid_;
    private byte memoizedIsInitialized;
    private int memoizedSerializedSize;
    private Object name_;
    private long value_;

    /* loaded from: classes.dex */
    public static final class Builder extends GeneratedMessageLite.Builder<SettingProtos$LockSetting, Builder> implements MessageLiteOrBuilder {
        private int bitField0_;
        private Object guid_ = "";
        private Object luid_ = "";
        private Object name_ = "";
        private long value_;

        private Builder() {
            maybeForceBuilderInitialization();
        }

        static /* synthetic */ Builder access$1700() {
            return create();
        }

        private static Builder create() {
            return new Builder();
        }

        private void maybeForceBuilderInitialization() {
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public SettingProtos$LockSetting build() {
            SettingProtos$LockSetting buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw AbstractMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public SettingProtos$LockSetting buildPartial() {
            SettingProtos$LockSetting settingProtos$LockSetting = new SettingProtos$LockSetting(this);
            int i = this.bitField0_;
            int i2 = (i & 1) != 1 ? 0 : 1;
            settingProtos$LockSetting.guid_ = this.guid_;
            if ((i & 2) == 2) {
                i2 |= 2;
            }
            settingProtos$LockSetting.luid_ = this.luid_;
            if ((i & 4) == 4) {
                i2 |= 4;
            }
            settingProtos$LockSetting.name_ = this.name_;
            if ((i & 8) == 8) {
                i2 |= 8;
            }
            settingProtos$LockSetting.value_ = this.value_;
            settingProtos$LockSetting.bitField0_ = i2;
            return settingProtos$LockSetting;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public Builder clear() {
            super.clear();
            this.guid_ = "";
            int i = this.bitField0_ & (-2);
            this.bitField0_ = i;
            this.luid_ = "";
            int i2 = i & (-3);
            this.bitField0_ = i2;
            this.name_ = "";
            int i3 = i2 & (-5);
            this.bitField0_ = i3;
            this.value_ = 0L;
            this.bitField0_ = i3 & (-9);
            return this;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.AbstractMessageLite.Builder
        /* renamed from: clone */
        public Builder mo69clone() {
            return create().mergeFrom(buildPartial());
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLiteOrBuilder
        public SettingProtos$LockSetting getDefaultInstanceForType() {
            return SettingProtos$LockSetting.getDefaultInstance();
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder
        public Builder mergeFrom(SettingProtos$LockSetting settingProtos$LockSetting) {
            if (settingProtos$LockSetting == SettingProtos$LockSetting.getDefaultInstance()) {
                return this;
            }
            if (settingProtos$LockSetting.hasGuid()) {
                setGuid(settingProtos$LockSetting.getGuid());
            }
            if (settingProtos$LockSetting.hasLuid()) {
                setLuid(settingProtos$LockSetting.getLuid());
            }
            if (settingProtos$LockSetting.hasName()) {
                setName(settingProtos$LockSetting.getName());
            }
            if (settingProtos$LockSetting.hasValue()) {
                setValue(settingProtos$LockSetting.getValue());
            }
            return this;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.AbstractMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public Builder mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            while (true) {
                int readTag = codedInputStream.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    this.bitField0_ |= 1;
                    this.guid_ = codedInputStream.readBytes();
                } else if (readTag == 18) {
                    this.bitField0_ |= 2;
                    this.luid_ = codedInputStream.readBytes();
                } else if (readTag == 26) {
                    this.bitField0_ |= 4;
                    this.name_ = codedInputStream.readBytes();
                } else if (readTag == 32) {
                    this.bitField0_ |= 8;
                    this.value_ = codedInputStream.readSInt64();
                } else if (!parseUnknownField(codedInputStream, extensionRegistryLite, readTag)) {
                    return this;
                }
            }
        }

        public Builder setGuid(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 1;
            this.guid_ = str;
            return this;
        }

        public Builder setLuid(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 2;
            this.luid_ = str;
            return this;
        }

        public Builder setName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 4;
            this.name_ = str;
            return this;
        }

        public Builder setValue(long j) {
            this.bitField0_ |= 8;
            this.value_ = j;
            return this;
        }
    }

    static {
        SettingProtos$LockSetting settingProtos$LockSetting = new SettingProtos$LockSetting(true);
        defaultInstance = settingProtos$LockSetting;
        settingProtos$LockSetting.initFields();
    }

    private SettingProtos$LockSetting(Builder builder) {
        super(builder);
        this.memoizedIsInitialized = (byte) -1;
        this.memoizedSerializedSize = -1;
    }

    private SettingProtos$LockSetting(boolean z) {
        this.memoizedIsInitialized = (byte) -1;
        this.memoizedSerializedSize = -1;
    }

    public static SettingProtos$LockSetting getDefaultInstance() {
        return defaultInstance;
    }

    private ByteString getGuidBytes() {
        Object obj = this.guid_;
        if (obj instanceof String) {
            ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
            this.guid_ = copyFromUtf8;
            return copyFromUtf8;
        }
        return (ByteString) obj;
    }

    private ByteString getLuidBytes() {
        Object obj = this.luid_;
        if (obj instanceof String) {
            ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
            this.luid_ = copyFromUtf8;
            return copyFromUtf8;
        }
        return (ByteString) obj;
    }

    private ByteString getNameBytes() {
        Object obj = this.name_;
        if (obj instanceof String) {
            ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
            this.name_ = copyFromUtf8;
            return copyFromUtf8;
        }
        return (ByteString) obj;
    }

    private void initFields() {
        this.guid_ = "";
        this.luid_ = "";
        this.name_ = "";
        this.value_ = 0L;
    }

    public static Builder newBuilder() {
        return Builder.access$1700();
    }

    public static Builder newBuilder(SettingProtos$LockSetting settingProtos$LockSetting) {
        return newBuilder().mergeFrom(settingProtos$LockSetting);
    }

    @Override // com.google.protobuf.GeneratedMessageLite, com.google.protobuf.MessageLiteOrBuilder
    public SettingProtos$LockSetting getDefaultInstanceForType() {
        return defaultInstance;
    }

    public String getGuid() {
        Object obj = this.guid_;
        if (obj instanceof String) {
            return (String) obj;
        }
        ByteString byteString = (ByteString) obj;
        String stringUtf8 = byteString.toStringUtf8();
        if (Internal.isValidUtf8(byteString)) {
            this.guid_ = stringUtf8;
        }
        return stringUtf8;
    }

    public String getLuid() {
        Object obj = this.luid_;
        if (obj instanceof String) {
            return (String) obj;
        }
        ByteString byteString = (ByteString) obj;
        String stringUtf8 = byteString.toStringUtf8();
        if (Internal.isValidUtf8(byteString)) {
            this.luid_ = stringUtf8;
        }
        return stringUtf8;
    }

    public String getName() {
        Object obj = this.name_;
        if (obj instanceof String) {
            return (String) obj;
        }
        ByteString byteString = (ByteString) obj;
        String stringUtf8 = byteString.toStringUtf8();
        if (Internal.isValidUtf8(byteString)) {
            this.name_ = stringUtf8;
        }
        return stringUtf8;
    }

    @Override // com.google.protobuf.GeneratedMessageLite, com.google.protobuf.MessageLite
    public int getSerializedSize() {
        int i = this.memoizedSerializedSize;
        if (i != -1) {
            return i;
        }
        int computeBytesSize = (this.bitField0_ & 1) == 1 ? 0 + CodedOutputStream.computeBytesSize(1, getGuidBytes()) : 0;
        if ((this.bitField0_ & 2) == 2) {
            computeBytesSize += CodedOutputStream.computeBytesSize(2, getLuidBytes());
        }
        if ((this.bitField0_ & 4) == 4) {
            computeBytesSize += CodedOutputStream.computeBytesSize(3, getNameBytes());
        }
        if ((this.bitField0_ & 8) == 8) {
            computeBytesSize += CodedOutputStream.computeSInt64Size(4, this.value_);
        }
        this.memoizedSerializedSize = computeBytesSize;
        return computeBytesSize;
    }

    public long getValue() {
        return this.value_;
    }

    public boolean hasGuid() {
        return (this.bitField0_ & 1) == 1;
    }

    public boolean hasLuid() {
        return (this.bitField0_ & 2) == 2;
    }

    public boolean hasName() {
        return (this.bitField0_ & 4) == 4;
    }

    public boolean hasValue() {
        return (this.bitField0_ & 8) == 8;
    }

    @Override // com.google.protobuf.GeneratedMessageLite, com.google.protobuf.MessageLiteOrBuilder
    public final boolean isInitialized() {
        byte b = this.memoizedIsInitialized;
        if (b != -1) {
            return b == 1;
        }
        this.memoizedIsInitialized = (byte) 1;
        return true;
    }

    @Override // com.google.protobuf.GeneratedMessageLite, com.google.protobuf.MessageLite
    public Builder newBuilderForType() {
        return newBuilder();
    }

    @Override // com.google.protobuf.GeneratedMessageLite, com.google.protobuf.MessageLite
    public Builder toBuilder() {
        return newBuilder(this);
    }

    @Override // com.google.protobuf.GeneratedMessageLite, com.google.protobuf.MessageLite
    public void writeTo(CodedOutputStream codedOutputStream) throws IOException {
        getSerializedSize();
        if ((this.bitField0_ & 1) == 1) {
            codedOutputStream.writeBytes(1, getGuidBytes());
        }
        if ((this.bitField0_ & 2) == 2) {
            codedOutputStream.writeBytes(2, getLuidBytes());
        }
        if ((this.bitField0_ & 4) == 4) {
            codedOutputStream.writeBytes(3, getNameBytes());
        }
        if ((this.bitField0_ & 8) == 8) {
            codedOutputStream.writeSInt64(4, this.value_);
        }
    }
}
