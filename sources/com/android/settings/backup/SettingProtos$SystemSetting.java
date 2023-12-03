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
public final class SettingProtos$SystemSetting extends GeneratedMessageLite implements MessageLiteOrBuilder {
    public static final int GUID_FIELD_NUMBER = 1;
    public static final int LUID_FIELD_NUMBER = 2;
    public static final int NAME_FIELD_NUMBER = 3;
    public static final int VALUE_FIELD_NUMBER = 4;
    private static final SettingProtos$SystemSetting defaultInstance;
    private static final long serialVersionUID = 0;
    private int bitField0_;
    private Object guid_;
    private Object luid_;
    private byte memoizedIsInitialized;
    private int memoizedSerializedSize;
    private Object name_;
    private Object value_;

    /* loaded from: classes.dex */
    public static final class Builder extends GeneratedMessageLite.Builder<SettingProtos$SystemSetting, Builder> implements MessageLiteOrBuilder {
        private int bitField0_;
        private Object guid_ = "";
        private Object luid_ = "";
        private Object name_ = "";
        private Object value_ = "";

        private Builder() {
            maybeForceBuilderInitialization();
        }

        static /* synthetic */ Builder access$100() {
            return create();
        }

        private static Builder create() {
            return new Builder();
        }

        private void maybeForceBuilderInitialization() {
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public SettingProtos$SystemSetting build() {
            SettingProtos$SystemSetting buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw AbstractMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public SettingProtos$SystemSetting buildPartial() {
            SettingProtos$SystemSetting settingProtos$SystemSetting = new SettingProtos$SystemSetting(this);
            int i = this.bitField0_;
            int i2 = (i & 1) != 1 ? 0 : 1;
            settingProtos$SystemSetting.guid_ = this.guid_;
            if ((i & 2) == 2) {
                i2 |= 2;
            }
            settingProtos$SystemSetting.luid_ = this.luid_;
            if ((i & 4) == 4) {
                i2 |= 4;
            }
            settingProtos$SystemSetting.name_ = this.name_;
            if ((i & 8) == 8) {
                i2 |= 8;
            }
            settingProtos$SystemSetting.value_ = this.value_;
            settingProtos$SystemSetting.bitField0_ = i2;
            return settingProtos$SystemSetting;
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
            this.value_ = "";
            this.bitField0_ = i3 & (-9);
            return this;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.AbstractMessageLite.Builder
        /* renamed from: clone */
        public Builder mo69clone() {
            return create().mergeFrom(buildPartial());
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLiteOrBuilder
        public SettingProtos$SystemSetting getDefaultInstanceForType() {
            return SettingProtos$SystemSetting.getDefaultInstance();
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder
        public Builder mergeFrom(SettingProtos$SystemSetting settingProtos$SystemSetting) {
            if (settingProtos$SystemSetting == SettingProtos$SystemSetting.getDefaultInstance()) {
                return this;
            }
            if (settingProtos$SystemSetting.hasGuid()) {
                setGuid(settingProtos$SystemSetting.getGuid());
            }
            if (settingProtos$SystemSetting.hasLuid()) {
                setLuid(settingProtos$SystemSetting.getLuid());
            }
            if (settingProtos$SystemSetting.hasName()) {
                setName(settingProtos$SystemSetting.getName());
            }
            if (settingProtos$SystemSetting.hasValue()) {
                setValue(settingProtos$SystemSetting.getValue());
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
                } else if (readTag == 34) {
                    this.bitField0_ |= 8;
                    this.value_ = codedInputStream.readBytes();
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

        public Builder setValue(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 8;
            this.value_ = str;
            return this;
        }
    }

    static {
        SettingProtos$SystemSetting settingProtos$SystemSetting = new SettingProtos$SystemSetting(true);
        defaultInstance = settingProtos$SystemSetting;
        settingProtos$SystemSetting.initFields();
    }

    private SettingProtos$SystemSetting(Builder builder) {
        super(builder);
        this.memoizedIsInitialized = (byte) -1;
        this.memoizedSerializedSize = -1;
    }

    private SettingProtos$SystemSetting(boolean z) {
        this.memoizedIsInitialized = (byte) -1;
        this.memoizedSerializedSize = -1;
    }

    public static SettingProtos$SystemSetting getDefaultInstance() {
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

    private ByteString getValueBytes() {
        Object obj = this.value_;
        if (obj instanceof String) {
            ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
            this.value_ = copyFromUtf8;
            return copyFromUtf8;
        }
        return (ByteString) obj;
    }

    private void initFields() {
        this.guid_ = "";
        this.luid_ = "";
        this.name_ = "";
        this.value_ = "";
    }

    public static Builder newBuilder() {
        return Builder.access$100();
    }

    public static Builder newBuilder(SettingProtos$SystemSetting settingProtos$SystemSetting) {
        return newBuilder().mergeFrom(settingProtos$SystemSetting);
    }

    @Override // com.google.protobuf.GeneratedMessageLite, com.google.protobuf.MessageLiteOrBuilder
    public SettingProtos$SystemSetting getDefaultInstanceForType() {
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
            computeBytesSize += CodedOutputStream.computeBytesSize(4, getValueBytes());
        }
        this.memoizedSerializedSize = computeBytesSize;
        return computeBytesSize;
    }

    public String getValue() {
        Object obj = this.value_;
        if (obj instanceof String) {
            return (String) obj;
        }
        ByteString byteString = (ByteString) obj;
        String stringUtf8 = byteString.toStringUtf8();
        if (Internal.isValidUtf8(byteString)) {
            this.value_ = stringUtf8;
        }
        return stringUtf8;
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
            codedOutputStream.writeBytes(4, getValueBytes());
        }
    }
}
