package com.android.settings.backup;

import com.android.settings.backup.SettingProtos$Settings;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/* loaded from: classes.dex */
public final class SyncRootProtos$SyncRoot extends GeneratedMessageLite implements MessageLiteOrBuilder {
    public static final int SETTING_FIELD_NUMBER = 7;
    private static final SyncRootProtos$SyncRoot defaultInstance;
    private static final long serialVersionUID = 0;
    private int bitField0_;
    private byte memoizedIsInitialized;
    private int memoizedSerializedSize;
    private SettingProtos$Settings setting_;

    /* loaded from: classes.dex */
    public static final class Builder extends GeneratedMessageLite.Builder<SyncRootProtos$SyncRoot, Builder> implements MessageLiteOrBuilder {
        private int bitField0_;
        private SettingProtos$Settings setting_ = SettingProtos$Settings.getDefaultInstance();

        private Builder() {
            maybeForceBuilderInitialization();
        }

        static /* synthetic */ Builder access$100() {
            return create();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public SyncRootProtos$SyncRoot buildParsed() throws InvalidProtocolBufferException {
            SyncRootProtos$SyncRoot buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw AbstractMessageLite.Builder.newUninitializedMessageException(buildPartial).asInvalidProtocolBufferException();
        }

        private static Builder create() {
            return new Builder();
        }

        private void maybeForceBuilderInitialization() {
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public SyncRootProtos$SyncRoot build() {
            SyncRootProtos$SyncRoot buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw AbstractMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public SyncRootProtos$SyncRoot buildPartial() {
            SyncRootProtos$SyncRoot syncRootProtos$SyncRoot = new SyncRootProtos$SyncRoot(this);
            int i = (this.bitField0_ & 1) != 1 ? 0 : 1;
            syncRootProtos$SyncRoot.setting_ = this.setting_;
            syncRootProtos$SyncRoot.bitField0_ = i;
            return syncRootProtos$SyncRoot;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLite.Builder
        public Builder clear() {
            super.clear();
            this.setting_ = SettingProtos$Settings.getDefaultInstance();
            this.bitField0_ &= -2;
            return this;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.AbstractMessageLite.Builder
        /* renamed from: clone */
        public Builder mo69clone() {
            return create().mergeFrom(buildPartial());
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLiteOrBuilder
        public SyncRootProtos$SyncRoot getDefaultInstanceForType() {
            return SyncRootProtos$SyncRoot.getDefaultInstance();
        }

        public SettingProtos$Settings getSetting() {
            return this.setting_;
        }

        public boolean hasSetting() {
            return (this.bitField0_ & 1) == 1;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder, com.google.protobuf.MessageLiteOrBuilder
        public final boolean isInitialized() {
            return true;
        }

        @Override // com.google.protobuf.GeneratedMessageLite.Builder
        public Builder mergeFrom(SyncRootProtos$SyncRoot syncRootProtos$SyncRoot) {
            if (syncRootProtos$SyncRoot != SyncRootProtos$SyncRoot.getDefaultInstance() && syncRootProtos$SyncRoot.hasSetting()) {
                mergeSetting(syncRootProtos$SyncRoot.getSetting());
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
                if (readTag == 58) {
                    SettingProtos$Settings.Builder newBuilder = SettingProtos$Settings.newBuilder();
                    if (hasSetting()) {
                        newBuilder.mergeFrom(getSetting());
                    }
                    codedInputStream.readMessage(newBuilder, extensionRegistryLite);
                    setSetting(newBuilder.buildPartial());
                } else if (!parseUnknownField(codedInputStream, extensionRegistryLite, readTag)) {
                    return this;
                }
            }
        }

        public Builder mergeSetting(SettingProtos$Settings settingProtos$Settings) {
            if ((this.bitField0_ & 1) != 1 || this.setting_ == SettingProtos$Settings.getDefaultInstance()) {
                this.setting_ = settingProtos$Settings;
            } else {
                this.setting_ = SettingProtos$Settings.newBuilder(this.setting_).mergeFrom(settingProtos$Settings).buildPartial();
            }
            this.bitField0_ |= 1;
            return this;
        }

        public Builder setSetting(SettingProtos$Settings settingProtos$Settings) {
            Objects.requireNonNull(settingProtos$Settings);
            this.setting_ = settingProtos$Settings;
            this.bitField0_ |= 1;
            return this;
        }
    }

    static {
        SyncRootProtos$SyncRoot syncRootProtos$SyncRoot = new SyncRootProtos$SyncRoot(true);
        defaultInstance = syncRootProtos$SyncRoot;
        syncRootProtos$SyncRoot.initFields();
    }

    private SyncRootProtos$SyncRoot(Builder builder) {
        super(builder);
        this.memoizedIsInitialized = (byte) -1;
        this.memoizedSerializedSize = -1;
    }

    private SyncRootProtos$SyncRoot(boolean z) {
        this.memoizedIsInitialized = (byte) -1;
        this.memoizedSerializedSize = -1;
    }

    public static SyncRootProtos$SyncRoot getDefaultInstance() {
        return defaultInstance;
    }

    private void initFields() {
        this.setting_ = SettingProtos$Settings.getDefaultInstance();
    }

    public static Builder newBuilder() {
        return Builder.access$100();
    }

    public static Builder newBuilder(SyncRootProtos$SyncRoot syncRootProtos$SyncRoot) {
        return newBuilder().mergeFrom(syncRootProtos$SyncRoot);
    }

    public static SyncRootProtos$SyncRoot parseFrom(InputStream inputStream) throws IOException {
        return ((Builder) newBuilder().mergeFrom(inputStream)).buildParsed();
    }

    @Override // com.google.protobuf.GeneratedMessageLite, com.google.protobuf.MessageLiteOrBuilder
    public SyncRootProtos$SyncRoot getDefaultInstanceForType() {
        return defaultInstance;
    }

    @Override // com.google.protobuf.GeneratedMessageLite, com.google.protobuf.MessageLite
    public int getSerializedSize() {
        int i = this.memoizedSerializedSize;
        if (i != -1) {
            return i;
        }
        int computeMessageSize = (this.bitField0_ & 1) == 1 ? 0 + CodedOutputStream.computeMessageSize(7, this.setting_) : 0;
        this.memoizedSerializedSize = computeMessageSize;
        return computeMessageSize;
    }

    public SettingProtos$Settings getSetting() {
        return this.setting_;
    }

    public boolean hasSetting() {
        return (this.bitField0_ & 1) == 1;
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
            codedOutputStream.writeMessage(7, this.setting_);
        }
    }
}
