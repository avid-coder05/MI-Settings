package com.android.settings.backup;

import com.android.settings.backup.SettingProtos$LockSetting;
import com.android.settings.backup.SettingProtos$SecureSetting;
import com.android.settings.backup.SettingProtos$SystemSetting;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/* loaded from: classes.dex */
public final class SettingProtos$Settings extends GeneratedMessageLite implements MessageLiteOrBuilder {
    public static final int LOCK_FIELD_NUMBER = 3;
    public static final int SECURE_FIELD_NUMBER = 2;
    public static final int SYSTEM_FIELD_NUMBER = 1;
    private static final SettingProtos$Settings defaultInstance;
    private static final long serialVersionUID = 0;
    private List<SettingProtos$LockSetting> lock_;
    private byte memoizedIsInitialized;
    private int memoizedSerializedSize;
    private List<SettingProtos$SecureSetting> secure_;
    private List<SettingProtos$SystemSetting> system_;

    /* loaded from: classes.dex */
    public static final class Builder extends GeneratedMessageLite.Builder<SettingProtos$Settings, Builder> implements MessageLiteOrBuilder {
        private int bitField0_;
        private List<SettingProtos$SystemSetting> system_ = Collections.emptyList();
        private List<SettingProtos$SecureSetting> secure_ = Collections.emptyList();
        private List<SettingProtos$LockSetting> lock_ = Collections.emptyList();

        private Builder() {
            maybeForceBuilderInitialization();
        }

        static /* synthetic */ Builder access$2500() {
            return create();
        }

        private static Builder create() {
            return new Builder();
        }

        private void ensureLockIsMutable() {
            if ((this.bitField0_ & 4) != 4) {
                this.lock_ = new ArrayList(this.lock_);
                this.bitField0_ |= 4;
            }
        }

        private void ensureSecureIsMutable() {
            if ((this.bitField0_ & 2) != 2) {
                this.secure_ = new ArrayList(this.secure_);
                this.bitField0_ |= 2;
            }
        }

        private void ensureSystemIsMutable() {
            if ((this.bitField0_ & 1) != 1) {
                this.system_ = new ArrayList(this.system_);
                this.bitField0_ |= 1;
            }
        }

        private void maybeForceBuilderInitialization() {
        }

        public Builder addLock(SettingProtos$LockSetting settingProtos$LockSetting) {
            Objects.requireNonNull(settingProtos$LockSetting);
            ensureLockIsMutable();
            this.lock_.add(settingProtos$LockSetting);
            return this;
        }

        public Builder addSecure(SettingProtos$SecureSetting settingProtos$SecureSetting) {
            Objects.requireNonNull(settingProtos$SecureSetting);
            ensureSecureIsMutable();
            this.secure_.add(settingProtos$SecureSetting);
            return this;
        }

        public Builder addSystem(SettingProtos$SystemSetting settingProtos$SystemSetting) {
            Objects.requireNonNull(settingProtos$SystemSetting);
            ensureSystemIsMutable();
            this.system_.add(settingProtos$SystemSetting);
            return this;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public SettingProtos$Settings build() {
            SettingProtos$Settings buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw AbstractMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public SettingProtos$Settings buildPartial() {
            SettingProtos$Settings settingProtos$Settings = new SettingProtos$Settings(this);
            if ((this.bitField0_ & 1) == 1) {
                this.system_ = Collections.unmodifiableList(this.system_);
                this.bitField0_ &= -2;
            }
            settingProtos$Settings.system_ = this.system_;
            if ((this.bitField0_ & 2) == 2) {
                this.secure_ = Collections.unmodifiableList(this.secure_);
                this.bitField0_ &= -3;
            }
            settingProtos$Settings.secure_ = this.secure_;
            if ((this.bitField0_ & 4) == 4) {
                this.lock_ = Collections.unmodifiableList(this.lock_);
                this.bitField0_ &= -5;
            }
            settingProtos$Settings.lock_ = this.lock_;
            return settingProtos$Settings;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public Builder clear() {
            super.clear();
            this.system_ = Collections.emptyList();
            this.bitField0_ &= -2;
            this.secure_ = Collections.emptyList();
            this.bitField0_ &= -3;
            this.lock_ = Collections.emptyList();
            this.bitField0_ &= -5;
            return this;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.AbstractMessageLite.Builder
        /* renamed from: clone */
        public Builder mo69clone() {
            return create().mergeFrom(buildPartial());
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLiteOrBuilder
        public SettingProtos$Settings getDefaultInstanceForType() {
            return SettingProtos$Settings.getDefaultInstance();
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder
        public Builder mergeFrom(SettingProtos$Settings settingProtos$Settings) {
            if (settingProtos$Settings == SettingProtos$Settings.getDefaultInstance()) {
                return this;
            }
            if (!settingProtos$Settings.system_.isEmpty()) {
                if (this.system_.isEmpty()) {
                    this.system_ = settingProtos$Settings.system_;
                    this.bitField0_ &= -2;
                } else {
                    ensureSystemIsMutable();
                    this.system_.addAll(settingProtos$Settings.system_);
                }
            }
            if (!settingProtos$Settings.secure_.isEmpty()) {
                if (this.secure_.isEmpty()) {
                    this.secure_ = settingProtos$Settings.secure_;
                    this.bitField0_ &= -3;
                } else {
                    ensureSecureIsMutable();
                    this.secure_.addAll(settingProtos$Settings.secure_);
                }
            }
            if (!settingProtos$Settings.lock_.isEmpty()) {
                if (this.lock_.isEmpty()) {
                    this.lock_ = settingProtos$Settings.lock_;
                    this.bitField0_ &= -5;
                } else {
                    ensureLockIsMutable();
                    this.lock_.addAll(settingProtos$Settings.lock_);
                }
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
                    SettingProtos$SystemSetting.Builder newBuilder = SettingProtos$SystemSetting.newBuilder();
                    codedInputStream.readMessage(newBuilder, extensionRegistryLite);
                    addSystem(newBuilder.buildPartial());
                } else if (readTag == 18) {
                    SettingProtos$SecureSetting.Builder newBuilder2 = SettingProtos$SecureSetting.newBuilder();
                    codedInputStream.readMessage(newBuilder2, extensionRegistryLite);
                    addSecure(newBuilder2.buildPartial());
                } else if (readTag == 26) {
                    SettingProtos$LockSetting.Builder newBuilder3 = SettingProtos$LockSetting.newBuilder();
                    codedInputStream.readMessage(newBuilder3, extensionRegistryLite);
                    addLock(newBuilder3.buildPartial());
                } else if (!parseUnknownField(codedInputStream, extensionRegistryLite, readTag)) {
                    return this;
                }
            }
        }
    }

    static {
        SettingProtos$Settings settingProtos$Settings = new SettingProtos$Settings(true);
        defaultInstance = settingProtos$Settings;
        settingProtos$Settings.initFields();
    }

    private SettingProtos$Settings(Builder builder) {
        super(builder);
        this.memoizedIsInitialized = (byte) -1;
        this.memoizedSerializedSize = -1;
    }

    private SettingProtos$Settings(boolean z) {
        this.memoizedIsInitialized = (byte) -1;
        this.memoizedSerializedSize = -1;
    }

    public static SettingProtos$Settings getDefaultInstance() {
        return defaultInstance;
    }

    private void initFields() {
        this.system_ = Collections.emptyList();
        this.secure_ = Collections.emptyList();
        this.lock_ = Collections.emptyList();
    }

    public static Builder newBuilder() {
        return Builder.access$2500();
    }

    public static Builder newBuilder(SettingProtos$Settings settingProtos$Settings) {
        return newBuilder().mergeFrom(settingProtos$Settings);
    }

    @Override // com.google.protobuf.GeneratedMessageLite, com.google.protobuf.MessageLiteOrBuilder
    public SettingProtos$Settings getDefaultInstanceForType() {
        return defaultInstance;
    }

    public List<SettingProtos$LockSetting> getLockList() {
        return this.lock_;
    }

    public List<SettingProtos$SecureSetting> getSecureList() {
        return this.secure_;
    }

    @Override // com.google.protobuf.GeneratedMessageLite, com.google.protobuf.MessageLite
    public int getSerializedSize() {
        int i = this.memoizedSerializedSize;
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < this.system_.size(); i3++) {
            i2 += CodedOutputStream.computeMessageSize(1, this.system_.get(i3));
        }
        for (int i4 = 0; i4 < this.secure_.size(); i4++) {
            i2 += CodedOutputStream.computeMessageSize(2, this.secure_.get(i4));
        }
        for (int i5 = 0; i5 < this.lock_.size(); i5++) {
            i2 += CodedOutputStream.computeMessageSize(3, this.lock_.get(i5));
        }
        this.memoizedSerializedSize = i2;
        return i2;
    }

    public List<SettingProtos$SystemSetting> getSystemList() {
        return this.system_;
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
        for (int i = 0; i < this.system_.size(); i++) {
            codedOutputStream.writeMessage(1, this.system_.get(i));
        }
        for (int i2 = 0; i2 < this.secure_.size(); i2++) {
            codedOutputStream.writeMessage(2, this.secure_.get(i2));
        }
        for (int i3 = 0; i3 < this.lock_.size(); i3++) {
            codedOutputStream.writeMessage(3, this.lock_.get(i3));
        }
    }
}
