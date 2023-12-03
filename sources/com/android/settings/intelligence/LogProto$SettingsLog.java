package com.android.settings.intelligence;

import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;

/* loaded from: classes.dex */
public final class LogProto$SettingsLog extends GeneratedMessageLite<LogProto$SettingsLog, Builder> implements MessageLiteOrBuilder {
    public static final int ACTION_FIELD_NUMBER = 2;
    public static final int ATTRIBUTION_FIELD_NUMBER = 1;
    public static final int CHANGED_PREFERENCE_INT_VALUE_FIELD_NUMBER = 5;
    public static final int CHANGED_PREFERENCE_KEY_FIELD_NUMBER = 4;
    private static final LogProto$SettingsLog DEFAULT_INSTANCE;
    public static final int PAGE_ID_FIELD_NUMBER = 3;
    private static volatile Parser<LogProto$SettingsLog> PARSER = null;
    public static final int TIMESTAMP_FIELD_NUMBER = 6;
    private int action_;
    private int attribution_;
    private int bitField0_;
    private int changedPreferenceIntValue_;
    private int pageId_;
    private String changedPreferenceKey_ = "";
    private String timestamp_ = "";

    /* loaded from: classes.dex */
    public static final class Builder extends GeneratedMessageLite.Builder<LogProto$SettingsLog, Builder> implements MessageLiteOrBuilder {
        private Builder() {
            super(LogProto$SettingsLog.DEFAULT_INSTANCE);
        }

        /* synthetic */ Builder(LogProto$1 logProto$1) {
            this();
        }
    }

    static {
        LogProto$SettingsLog logProto$SettingsLog = new LogProto$SettingsLog();
        DEFAULT_INSTANCE = logProto$SettingsLog;
        GeneratedMessageLite.registerDefaultInstance(LogProto$SettingsLog.class, logProto$SettingsLog);
    }

    private LogProto$SettingsLog() {
    }

    @Override // com.google.protobuf.GeneratedMessageLite
    protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        LogProto$1 logProto$1 = null;
        switch (LogProto$1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new LogProto$SettingsLog();
            case 2:
                return new Builder(logProto$1);
            case 3:
                return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001\u0004\u0000\u0002\u0004\u0001\u0003\u0004\u0002\u0004\b\u0003\u0005\u0004\u0004\u0006\b\u0005", new Object[]{"bitField0_", "attribution_", "action_", "pageId_", "changedPreferenceKey_", "changedPreferenceIntValue_", "timestamp_"});
            case 4:
                return DEFAULT_INSTANCE;
            case 5:
                Parser<LogProto$SettingsLog> parser = PARSER;
                if (parser == null) {
                    synchronized (LogProto$SettingsLog.class) {
                        parser = PARSER;
                        if (parser == null) {
                            parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                            PARSER = parser;
                        }
                    }
                }
                return parser;
            case 6:
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
