package miui.cloud.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/* loaded from: classes3.dex */
public class ExtraTelephony {

    /* loaded from: classes3.dex */
    public static class DeletableSyncColumns extends SyncColumns {
        DeletableSyncColumns() {
            throw new RuntimeException("Stub!");
        }

        public static String get_DELETED() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static final class FirewallLog implements BaseColumns {

        /* loaded from: classes3.dex */
        public static final class SmsBlockType {
            SmsBlockType() {
                throw new RuntimeException("Stub!");
            }

            public static int get_NONE_BUT_MUTE() {
                throw new RuntimeException("Stub!");
            }
        }

        FirewallLog() {
            throw new RuntimeException("Stub!");
        }

        public static String get_CONTENT_ITEM_TYPE() {
            throw new RuntimeException("Stub!");
        }

        public static Uri get_CONTENT_URI() {
            throw new RuntimeException("Stub!");
        }

        @Deprecated
        public static String get_MODE() {
            throw new RuntimeException("Stub!");
        }

        public static String get_NUMBER() {
            throw new RuntimeException("Stub!");
        }

        public static String get_SIM_ID() {
            throw new RuntimeException("Stub!");
        }

        public static String get_TYPE() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static final class Mms extends DeletableSyncColumns {
        Mms() {
            throw new RuntimeException("Stub!");
        }

        public static String get_ACCOUNT() {
            throw new RuntimeException("Stub!");
        }

        public static String get_ADDRESSES() {
            throw new RuntimeException("Stub!");
        }

        public static String get_BIND_ID() {
            throw new RuntimeException("Stub!");
        }

        public static String get_BLOCK_TYPE() {
            throw new RuntimeException("Stub!");
        }

        public static String get_DATE_MS_PART() {
            throw new RuntimeException("Stub!");
        }

        public static String get_ERROR_TYPE() {
            throw new RuntimeException("Stub!");
        }

        public static String get_FAVORITE_DATE() {
            throw new RuntimeException("Stub!");
        }

        public static String get_FILE_ID() {
            throw new RuntimeException("Stub!");
        }

        public static String get_MX_EXTENSION() {
            throw new RuntimeException("Stub!");
        }

        public static String get_MX_STATUS() {
            throw new RuntimeException("Stub!");
        }

        public static String get_MX_TYPE() {
            throw new RuntimeException("Stub!");
        }

        public static String get_NEED_DOWNLOAD() {
            throw new RuntimeException("Stub!");
        }

        public static String get_SIM_ID() {
            throw new RuntimeException("Stub!");
        }

        public static String get_TIMED() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static final class MmsSms {
        MmsSms() {
            throw new RuntimeException("Stub!");
        }

        public static String get_INSERT_PATH_IGNORED() {
            throw new RuntimeException("Stub!");
        }

        public static String get_INSERT_PATH_INSERTED() {
            throw new RuntimeException("Stub!");
        }

        public static String get_INSERT_PATH_RESTORED() {
            throw new RuntimeException("Stub!");
        }

        public static String get_INSERT_PATH_UPDATED() {
            throw new RuntimeException("Stub!");
        }

        public static int get_SYNC_STATE_DIRTY() {
            throw new RuntimeException("Stub!");
        }

        public static int get_SYNC_STATE_ERROR() {
            throw new RuntimeException("Stub!");
        }

        public static int get_SYNC_STATE_MARKED_DELETING() {
            throw new RuntimeException("Stub!");
        }

        public static int get_SYNC_STATE_NOT_UPLOADABLE() {
            throw new RuntimeException("Stub!");
        }

        public static int get_SYNC_STATE_SYNCED() {
            throw new RuntimeException("Stub!");
        }

        public static int get_SYNC_STATE_SYNCING() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static final class Mx {
        Mx() {
            throw new RuntimeException("Stub!");
        }

        public static int get_TYPE_COMMON() {
            throw new RuntimeException("Stub!");
        }

        public static int get_TYPE_DELIVERED() {
            throw new RuntimeException("Stub!");
        }

        public static int get_TYPE_FAILED() {
            throw new RuntimeException("Stub!");
        }

        public static int get_TYPE_INCOMING() {
            throw new RuntimeException("Stub!");
        }

        public static int get_TYPE_PENDING() {
            throw new RuntimeException("Stub!");
        }

        public static int get_TYPE_READ() {
            throw new RuntimeException("Stub!");
        }

        public static int get_TYPE_SENT() {
            throw new RuntimeException("Stub!");
        }

        public static int get_TYPE_WEB() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static class MxType {
        MxType() {
            throw new RuntimeException("Stub!");
        }

        public static int get_AUDIO() {
            throw new RuntimeException("Stub!");
        }

        public static int get_IMAGE() {
            throw new RuntimeException("Stub!");
        }

        public static int get_MMS() {
            throw new RuntimeException("Stub!");
        }

        public static int get_NONE_MX() {
            throw new RuntimeException("Stub!");
        }

        public static int get_RED() {
            throw new RuntimeException("Stub!");
        }

        public static int get_VIDEO() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static final class Phonelist implements BaseColumns {

        /* loaded from: classes3.dex */
        public static final class Location {
            Location() {
                throw new RuntimeException("Stub!");
            }

            public static int get_IS_CLOUD() {
                throw new RuntimeException("Stub!");
            }
        }

        /* loaded from: classes3.dex */
        public static final class SyncDirty {
            SyncDirty() {
                throw new RuntimeException("Stub!");
            }

            public static int get_ADD() {
                throw new RuntimeException("Stub!");
            }

            public static int get_DELETE() {
                throw new RuntimeException("Stub!");
            }

            public static int get_SYNCED() {
                throw new RuntimeException("Stub!");
            }

            public static int get_UPDATE() {
                throw new RuntimeException("Stub!");
            }
        }

        Phonelist() {
            throw new RuntimeException("Stub!");
        }

        public static String get_CONTENT_ITEM_TYPE() {
            throw new RuntimeException("Stub!");
        }

        public static Uri get_CONTENT_URI() {
            throw new RuntimeException("Stub!");
        }

        public static String get_DISPLAY_NUMBER() {
            throw new RuntimeException("Stub!");
        }

        public static String get_E_TAG() {
            throw new RuntimeException("Stub!");
        }

        public static String get_IS_DISPLAY() {
            throw new RuntimeException("Stub!");
        }

        public static String get_LOCATION() {
            throw new RuntimeException("Stub!");
        }

        public static String get_NOTES() {
            throw new RuntimeException("Stub!");
        }

        public static String get_NUMBER() {
            throw new RuntimeException("Stub!");
        }

        public static String get_RECORD_ID() {
            throw new RuntimeException("Stub!");
        }

        public static String get_SIM_ID() {
            throw new RuntimeException("Stub!");
        }

        public static String get_STATE() {
            throw new RuntimeException("Stub!");
        }

        public static String get_SYNC_DIRTY() {
            throw new RuntimeException("Stub!");
        }

        public static String get_TYPE() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static final class PrivateAddresses extends DeletableSyncColumns {
        PrivateAddresses() {
            throw new RuntimeException("Stub!");
        }

        public static String get_ADDRESS() {
            throw new RuntimeException("Stub!");
        }

        public static Uri get_CONTENT_URI() {
            throw new RuntimeException("Stub!");
        }

        public static String get__ID() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static final class SimCards {

        /* loaded from: classes3.dex */
        public static final class DLStatus {
            DLStatus() {
                throw new RuntimeException("Stub!");
            }

            public static int get_FINISH() {
                throw new RuntimeException("Stub!");
            }

            public static int get_INIT() {
                throw new RuntimeException("Stub!");
            }

            public static int get_NEED() {
                throw new RuntimeException("Stub!");
            }
        }

        /* loaded from: classes3.dex */
        public static final class SyncStatus {
            SyncStatus() {
                throw new RuntimeException("Stub!");
            }

            public static int get_ACTIVE() {
                throw new RuntimeException("Stub!");
            }

            public static int get_CLOSED() {
                throw new RuntimeException("Stub!");
            }

            public static int get_DIRTY_MASK() {
                throw new RuntimeException("Stub!");
            }

            public static int get_INACTIVE() {
                throw new RuntimeException("Stub!");
            }
        }

        SimCards() {
            throw new RuntimeException("Stub!");
        }

        public static String get_BIND_ID() {
            throw new RuntimeException("Stub!");
        }

        public static Uri get_CONTENT_URI() {
            throw new RuntimeException("Stub!");
        }

        public static String get_DL_STATUS() {
            throw new RuntimeException("Stub!");
        }

        @Deprecated
        public static String get_IMSI() {
            throw new RuntimeException("Stub!");
        }

        public static String get_MARKER1() {
            throw new RuntimeException("Stub!");
        }

        public static String get_MARKER2() {
            throw new RuntimeException("Stub!");
        }

        public static String get_NUMBER() {
            throw new RuntimeException("Stub!");
        }

        public static String get_SIM_ID() {
            throw new RuntimeException("Stub!");
        }

        @Deprecated
        public static String get_SLOT() {
            throw new RuntimeException("Stub!");
        }

        public static String get_SYNC_ENABLED() {
            throw new RuntimeException("Stub!");
        }

        public static String get_SYNC_EXTRA_INFO() {
            throw new RuntimeException("Stub!");
        }

        public static String get__ID() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static final class Sms extends DeletableSyncColumns {
        Sms() {
            throw new RuntimeException("Stub!");
        }

        public static String get_ACCOUNT() {
            throw new RuntimeException("Stub!");
        }

        public static String get_ADDRESSES() {
            throw new RuntimeException("Stub!");
        }

        public static String get_B2C_NUMBERS() {
            throw new RuntimeException("Stub!");
        }

        public static String get_BIND_ID() {
            throw new RuntimeException("Stub!");
        }

        public static String get_BLOCK_TYPE() {
            throw new RuntimeException("Stub!");
        }

        public static String get_FAVORITE_DATE() {
            throw new RuntimeException("Stub!");
        }

        public static String get_MX_STATUS() {
            throw new RuntimeException("Stub!");
        }

        public static String get_SIM_ID() {
            throw new RuntimeException("Stub!");
        }

        public static String get_TIMED() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static class SyncColumns {
        SyncColumns() {
            throw new RuntimeException("Stub!");
        }

        public static String get_MARKER() {
            throw new RuntimeException("Stub!");
        }

        public static String get_SOURCE() {
            throw new RuntimeException("Stub!");
        }

        public static String get_SYNC_STATE() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static final class Threads extends ThreadsColumns {
        Threads() {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static class ThreadsColumns extends SyncColumns {
        ThreadsColumns() {
            throw new RuntimeException("Stub!");
        }

        public static String get_STICK_TIME() {
            throw new RuntimeException("Stub!");
        }
    }

    ExtraTelephony() {
        throw new RuntimeException("Stub!");
    }

    public static String get_CALLER_IS_SYNCADAPTER() {
        throw new RuntimeException("Stub!");
    }

    public static String get_CHECK_DUPLICATION() {
        throw new RuntimeException("Stub!");
    }

    public static String get_LOCAL_PRIVATE_ADDRESS_SYNC() {
        throw new RuntimeException("Stub!");
    }

    public static String get_LOCAL_SMS_SYNC() {
        throw new RuntimeException("Stub!");
    }

    public static String get_LOCAL_STICKY_THREAD_SYNC() {
        throw new RuntimeException("Stub!");
    }

    public static String get_LOCAL_SYNC_NAME() {
        throw new RuntimeException("Stub!");
    }

    public static String get_NEED_FULL_INSERT_URI() {
        throw new RuntimeException("Stub!");
    }

    public static String get_NO_NOTIFY_FLAG() {
        throw new RuntimeException("Stub!");
    }

    public static String get_PRIVACY_FLAG() {
        throw new RuntimeException("Stub!");
    }

    public static String get_PRIVACY_FLAG_ALL_MSG() {
        throw new RuntimeException("Stub!");
    }

    public static String get_SUPPRESS_MAKING_MMS_PREVIEW() {
        throw new RuntimeException("Stub!");
    }
}
