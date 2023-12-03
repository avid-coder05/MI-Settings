package miui.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.BaseColumns;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import miui.os.Build;
import miui.telephony.PhoneNumberUtils;
import miui.telephony.phonenumber.CountryCode;
import miui.util.IOUtils;

/* loaded from: classes3.dex */
public final class ExtraTelephony {
    public static final String BANK_CATEGORY_NUMBER_PREFIX_106 = "106";
    public static final String BLOCKED_CONV_ADDR = "blocked_conv_addr";
    public static final String BLOCKED_FLAG = "blocked_flag";
    public static final String BLOCKED_FLAG_ALL_MSG = "2";
    public static final String BLOCKED_FLAG_BLOCKED_MSG = "1";
    public static final String BLOCKED_FLAG_NO_BLOCKED_MSG = "0";
    public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";
    private static final String CALL_BLOCK_TYPE = "callBlockType";
    public static final String CHECK_DUPLICATION = "check_duplication";
    public static final int DEFAULT_THREADS_LIST_TYPE_SP = 1;
    public static final String DIRTY_QUERY_LIMIT = "dirty_query_limit";
    public static final String FORCE_DELETE = "force_delete";
    public static final int INTERCEPT_STATE_ALL = 0;
    public static final int INTERCEPT_STATE_CALL = 2;
    public static final int INTERCEPT_STATE_SMS = 1;
    public static final String LOCAL_PRIVATE_ADDRESS_SYNC = "local.priaddr.sync";
    public static final String LOCAL_SMS_SYNC = "local.sms.sync";
    public static final String LOCAL_STICKY_THREAD_SYNC = "local.stkthrd.sync";
    public static final String LOCAL_SYNC_NAME = "localName";
    public static final String NEED_FULL_INSERT_URI = "need_full_insert_uri";
    public static final String NO_NOTIFY_FLAG = "no_notify";
    public static final String PRIVACY_FLAG = "privacy_flag";
    public static final String PRIVACY_FLAG_ALL_MSG = "2";
    public static final String PRIVACY_FLAG_NO_PRIVATE_MSG = "0";
    public static final String PRIVACY_FLAG_PRIVATE_MSG = "1";
    public static final String PROVIDER_NAME = "antispam";
    public static final String PrefixCode = "***";
    private static final String SMS_BLOCK_TYPE = "smsBlockType";
    public static final int SOURCE_ANYONE = 0;
    public static final int SOURCE_CONTACT = 1;
    public static final int SOURCE_STAR = 2;
    public static final int SOURCE_VIP = 3;
    public static final String SUPPRESS_MAKING_MMS_PREVIEW = "supress_making_mms_preview";
    private static final String TAG = "ExtraTelephony";
    public static final String THREADS_LIST_TYPE = "threads_list_type";
    public static final int THREADS_LIST_TYPE_COMPOSITE = 0;
    public static final int TYPE_INTERCEPT_ADDRESS = 2;
    public static final int TYPE_INTERCEPT_NUMBER = 1;
    public static final int TYPE_INTERCEPT_NUMBER_FRAGMENT = 3;
    public static final String ZEN_MODE = "zen_mode";
    public static final int ZEN_MODE_ALARMS = 3;
    public static final int ZEN_MODE_IMPORTANT_INTERRUPTIONS = 1;
    public static final int ZEN_MODE_MIUI_SILENT = 4;
    public static final int ZEN_MODE_NO_INTERRUPTIONS = 2;
    public static final int ZEN_MODE_OFF = 0;
    private static SilentModeObserver mSilentModeObserver;
    public static final Pattern BANK_CATEGORY_PATTERN = Pattern.compile("银行|信用卡|Bank|BANK|支付宝|中国银联");
    public static final Pattern BANK_CATEGORY_SNIPPET_PATTERN = Pattern.compile("((\\[[\\s\\S]*(银行|信用卡|Bank|BANK|支付宝|中国银联)\\])|(\\【[\\s\\S]*(银行|信用卡|Bank|BANK|支付宝|中国银联)\\】))$");
    private static Set<QuietModeEnableListener> mQuietListeners = new HashSet();

    /* loaded from: classes3.dex */
    public static class AdvancedSeen {
        public static final int NON_SP_UNSEEN = 0;
        public static final int SEEN = 3;
        public static final int SP_NOTIFIED = 2;
        public static final int SP_UNSEEN = 1;
    }

    /* loaded from: classes3.dex */
    public static final class AntiSpamMode implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/antispam-mode";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/antispam-mode";
        public static final Uri CONTENT_URI = Uri.parse("content://antispam/mode");
        public static final String NAME = "name";
        public static final String STATE = "state";
    }

    /* loaded from: classes3.dex */
    public static final class AntiSpamSim implements BaseColumns {
        public static final String BACKSOUND_MODE = "backsound_mode";
        public static final String CALL_WAIT = "call_wait";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/antispam-sim";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/antispam-sim";
        public static final Uri CONTENT_URI = Uri.parse("content://antispam/sim");
        public static final String NAME = "name";
        public static final String SIM_ID = "sim_id";
        public static final String STATE = "state";
    }

    /* loaded from: classes3.dex */
    public static final class Blacklist implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/firewall-blacklist";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/firewall-blacklist";
        public static final Uri CONTENT_URI = Uri.parse("content://firewall/blacklist");
        public static final String DISPLAY_NUMBER = "display_number";
        public static final String NOTES = "notes";
        public static final String NUMBER = "number";
        public static final String STATE = "state";
    }

    /* loaded from: classes3.dex */
    public interface DeletableSyncColumns extends SyncColumns {
        public static final String DELETED = "deleted";
    }

    /* loaded from: classes3.dex */
    public static final class FirewallLog implements BaseColumns {
        public static final String CALL_TYPE = "callType";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/antispam-log";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/antispam-log";
        public static final Uri CONTENT_URI = Uri.parse("content://antispam/log");
        public static final Uri CONTENT_URI_LOG_CONVERSATION = Uri.parse("content://antispam/logconversation");
        public static final Uri CONTENT_URI_SMS_LOG = Uri.parse("content://antispam/log_sms");
        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATE = "date";
        public static final String MODE = "mode";
        public static final String NUMBER = "number";
        public static final String READ = "read";
        public static final String REASON = "reason";
        public static final String SIM_ID = "simid";
        public static final String TYPE = "type";
        public static final int TYPE_CALL = 1;
        public static final int TYPE_SMS = 2;

        /* loaded from: classes3.dex */
        public interface CallBlockType {
            public static final int ADDRESS = 13;
            public static final int AGENT = 10;
            public static final int BLACKLIST = 3;
            public static final int CALL_TRANSFER = 15;
            public static final int CLOUDS = 16;
            public static final int CONTACT = 9;
            public static final int FRAUD = 8;
            public static final int HARASS = 14;
            public static final int IMPORT = 5;
            public static final int MUTE_BY_QM = 1;
            public static final int MUTE_NEED_CHECK = 2;
            public static final int NONE = 0;
            public static final int NONE_NEED_CHECK = -1;
            public static final int OVERSEA = 17;
            public static final int PREFIX = 6;
            public static final int PRIVATE_CALL = 4;
            public static final int SELL = 12;
            public static final int STRANGER = 7;
        }

        /* loaded from: classes3.dex */
        public interface SmsBlockType {
            public static final int ADDRESS = 13;
            public static final int BLACKLIST = 3;
            public static final int CLOUDS = 16;
            public static final int CONTACT = 9;
            public static final int FILTER = 4;
            public static final int IMPORT = 5;
            public static final int KEYWORDS = 12;
            public static final int NONE = 0;
            public static final int NONE_BUT_MUTE = 1;
            public static final int PREFIX = 6;
            public static final int SERVICE = 10;
            public static final int STRANGER = 7;
            public static final int URL = 8;
        }
    }

    /* loaded from: classes3.dex */
    public static final class Hms implements BaseColumns {
        public static final String ADDRESS = "address";
        public static final String ADVANCED_SEEN = "advanced_seen";
        public static final Uri CONTENT_URI;
        public static final String DATE = "date";
        public static final String MX_CONTENT = "mx_content";
        public static final String MX_EXTENSION = "mx_extension";
        public static final String MX_MESSAGE_ID = "mx_message_id";
        public static final String MX_SEQ = "mx_seq";
        public static final String MX_TYPE = "mx_type";
        public static final String READ = "read";
        public static final String SEEN = "seen";
        public static final String SNIPPET = "snippet";
        public static final String THREAD_ID = "thread_id";
        public static final Uri THREAD_ID_CONTENT_URI;
        public static final String TYPE = "type";

        static {
            Uri parse = Uri.parse("content://hms/");
            CONTENT_URI = parse;
            THREAD_ID_CONTENT_URI = Uri.withAppendedPath(parse, "threadId");
        }
    }

    /* loaded from: classes3.dex */
    public static final class Judge implements BaseColumns {
        public static final int FORWARD_CALL_ALLOW = 0;
        public static final int FORWARD_CALL_INTERCEPT = 1;
        public static final String IS_FORWARD_CALL = "is_forward_call";
        public static final String IS_REPEATED_BLOCKED_CALL = "is_repeated_blocked_call";
        public static final String IS_REPEATED_NORMAL_CALL = "is_repeated_normal_call";
        public static final int URL_SCAN_RESULT_DANGEROUS = 2;
        public static final int URL_SCAN_RESULT_NORMAL = 0;
        public static final int URL_SCAN_RESULT_RISKY = 1;
        public static final int URL_SCAN_RESULT_UNKNOWN = -1;
        public static final Uri SMS_CONTENT_URI = Uri.parse("content://antispam/sms_judge");
        public static final Uri CALL_CONTENT_URI = Uri.parse("content://antispam/call_judge");
        public static final Uri URL_CONTENT_URI = Uri.parse("content://antispam/url_judge");
        public static final Uri SERVICE_NUM_CONTENT_URI = Uri.parse("content://antispam/service_num_judge");
        public static final Uri CALL_TRANSFER_CONTENT_URI = Uri.parse("content://antispam/call_transfer_intercept_judge");
    }

    /* loaded from: classes3.dex */
    public static final class Keyword implements BaseColumns {
        public static final String CLOUD_UID = "cloudUid";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/antispam-keyword";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/antispam-keyword";
        public static final Uri CONTENT_URI = Uri.parse("content://antispam/keyword");
        public static final String DATA = "data";
        public static final String SIM_ID = "sim_id";
        public static final String TYPE = "type";
        public static final int TYPE_CLOUDS_BLACK = 2;
        public static final int TYPE_CLOUDS_WHITE = 3;
        public static final int TYPE_LOCAL_BLACK = 1;
        public static final int TYPE_LOCAL_WHITE = 4;
    }

    /* loaded from: classes3.dex */
    public static final class Mms implements DeletableSyncColumns {
        public static final String ACCOUNT = "account";
        public static final String ADDRESSES = "addresses";
        public static final String ADVANCED_SEEN = "advanced_seen";
        public static final String BIND_ID = "bind_id";
        public static final String BLOCK_TYPE = "block_type";
        public static final String DATE_FULL = "date_full";
        public static final String DATE_MS_PART = "date_ms_part";
        public static final String ERROR_TYPE = "error_type";
        public static final String FAVORITE_DATE = "favorite_date";
        public static final String FILE_ID = "file_id";
        public static final String MX_EXTENSION = "mx_extension";
        public static final String MX_ID = "mx_id";
        public static final String MX_STATUS = "mx_status";
        public static final String MX_TYPE = "mx_type";
        public static final String NEED_DOWNLOAD = "need_download";
        public static final String OUT_TIME = "out_time";
        public static final String PREVIEW_DATA = "preview_data";
        public static final String PREVIEW_DATA_TS = "preview_data_ts";
        public static final String PREVIEW_TYPE = "preview_type";
        public static final String SIM_ID = "sim_id";
        public static final String SNIPPET = "snippet";
        public static final String TIMED = "timed";

        /* loaded from: classes3.dex */
        public static final class Intents {
            public static final String MAKE_MMS_PREVIEW_ACTION = "android.provider.Telephony.MAKE_MMS_PREVIEW";
        }

        /* loaded from: classes3.dex */
        public static final class PreviewType {
            public static final int AUDIO = 3;
            public static final int IMAGE = 2;
            public static final int NONE = 1;
            public static final int SLIDESHOW = 6;
            public static final int UNKNOWN = 0;
            public static final int VCARD = 5;
            public static final int VIDEO = 4;
        }
    }

    /* loaded from: classes3.dex */
    public static final class MmsSms {
        public static final String EXCLUDE_VERIFICATION_CODES_FLAG = "exclude_verification_codes";
        public static final String EXCLUDE_VERIFICATION_CODES_FLAG_EXCLUDE = "1";
        public static final String EXCLUDE_VERIFICATION_CODES_FLAG_NOT_EXCLUDE = "0";
        public static final String INSERT_PATH_IGNORED = "ignored";
        public static final String INSERT_PATH_INSERTED = "inserted";
        public static final String INSERT_PATH_RESTORED = "restored";
        public static final String INSERT_PATH_UPDATED = "updated";
        public static final int PREVIEW_ADDRESS_COLUMN_INDEX = 1;
        public static final int PREVIEW_BODY_COLUMN_INDEX = 4;
        public static final int PREVIEW_CHARSET_COLUMN_INDEX = 5;
        public static final int PREVIEW_DATE_COLUMN_INDEX = 2;
        public static final int PREVIEW_ID_COLUMN_INDEX = 0;
        public static final int PREVIEW_THREAD_ID_COLUMN_INDEX = 6;
        public static final int PREVIEW_TYPE_COLUMN_INDEX = 3;
        public static final int SYNC_STATE_DIRTY = 0;
        public static final int SYNC_STATE_ERROR = 3;
        public static final int SYNC_STATE_MARKED_DELETING = 65538;
        public static final int SYNC_STATE_NOT_UPLOADABLE = 4;
        public static final int SYNC_STATE_SYNCED = 2;
        public static final int SYNC_STATE_SYNCING = 1;
        public static final Uri CONTENT_PREVIEW_URI = Uri.parse("content://mms-sms/message/preview");
        public static final Uri CONTENT_ALL_LOCKED_URI = Uri.parse("content://mms-sms/locked/all");
        public static final Uri CONTENT_EXPIRED_URI = Uri.parse("content://mms-sms/expired");
        public static final Uri CONTENT_RECENT_RECIPIENTS_URI = Uri.parse("content://mms-sms/recent-recipients");
        public static final Uri CONTENT_UNDERSTAND_INFO_URI = Uri.parse("content://mms-sms/understand-info");
        public static final Uri CONTENT_ALL_UNDERSTAND_INFO_URI = Uri.parse("content://mms-sms/understand-info/all");
        public static final Uri BLOCKED_CONVERSATION_CONTENT_URI = Uri.parse("content://mms-sms/blocked");
        public static final Uri BLOCKED_THREAD_CONTENT_URI = Uri.parse("content://mms-sms/blocked-thread");
    }

    /* loaded from: classes3.dex */
    public static final class Mx {
        public static final int TYPE_COMMON = 0;
        public static final int TYPE_DELIVERED = 17;
        public static final int TYPE_FAILED = 131073;
        public static final int TYPE_INCOMING = 65537;
        public static final int TYPE_PENDING = 1;
        public static final int TYPE_READ = 256;
        public static final int TYPE_SENT = 16;
        public static final int TYPE_WEB = 196609;
    }

    /* loaded from: classes3.dex */
    public static class MxType {
        public static final int AUDIO = 3;
        public static final int IMAGE = 2;
        public static final int MMS = 1;
        public static final int NONE_MX = 0;
        public static final int RED = 5;
        public static final int VIDEO = 4;
    }

    /* loaded from: classes3.dex */
    public static final class Phonelist implements BaseColumns {
        public static final String CLOUD_UUID = "cloudUid";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/antispam-phone_list";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/antispam-phone_list";
        public static final String DISPLAY_NUMBER = "display_number";
        public static final String E_TAG = "e_tag";
        public static final String IS_DISPLAY = "isdisplay";
        public static final String LOCATION = "location";
        public static final String NOTES = "notes";
        public static final String NUMBER = "number";
        public static final String RECORD_ID = "record_id";
        public static final String SIM_ID = "sim_id";
        public static final String STATE = "state";
        public static final String SYNC_DIRTY = "sync_dirty";
        public static final String TYPE = "type";
        public static final String TYPE_BLACK = "1";
        public static final String TYPE_CLOUDS_BLACK = "4";
        public static final String TYPE_CLOUDS_WHITE = "5";
        public static final String TYPE_STRONG_CLOUDS_BLACK = "6";
        public static final String TYPE_STRONG_CLOUDS_WHITE = "7";
        public static final String TYPE_VIP = "3";
        public static final String TYPE_WHITE = "2";
        public static final String UNKNOWN_NUMBER = "-1";
        public static final Uri CONTENT_URI = Uri.parse("content://antispam/phone_list");
        public static final Uri CONTENT_URI_UNSYNCED_COUNT = Uri.parse("content://antispam/unsynced_count");
        public static final Uri CONTENT_URI_SYNCED_COUNT = Uri.parse("content://antispam/synced_count");

        /* loaded from: classes3.dex */
        public static final class Location {
            public static final int IS_CLOUD = 1;
            public static final int IS_LOCAL = 0;
        }

        /* loaded from: classes3.dex */
        public static final class State {
            public static final int ALL = 0;
            public static final int CALL = 2;
            public static final int MSG = 1;
        }

        /* loaded from: classes3.dex */
        public static final class SyncDirty {
            public static final int ADD = 0;
            public static final int DELETE = 1;
            public static final int SYNCED = 3;
            public static final int UPDATE = 2;
        }
    }

    /* loaded from: classes3.dex */
    public interface PrivateAddresses extends DeletableSyncColumns {
        public static final String ADDRESS = "address";
        public static final Uri CONTENT_URI = Uri.parse("content://mms-sms/private-addresses");
        public static final String _ID = "_id";
    }

    /* loaded from: classes3.dex */
    public interface QuietModeEnableListener {
        void onQuietModeEnableChange(boolean z);
    }

    /* loaded from: classes3.dex */
    public static class ServiceCategory {
        public static final int DEFAULT_SERVICE_NUMBER = 1;
        public static final int FINANCE_NUMBER = 2;
        public static final int NOT_SERVICE_NUMBER = 0;
    }

    /* loaded from: classes3.dex */
    private static class SilentModeObserver extends ContentObserver {
        private Context mContext;

        public SilentModeObserver(Context context, Handler handler) {
            super(handler);
            this.mContext = context.getApplicationContext() != null ? context.getApplicationContext() : context;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            if (ExtraTelephony.mQuietListeners != null) {
                Iterator it = ExtraTelephony.mQuietListeners.iterator();
                boolean isSilenceModeEnable = MiuiSettings.SilenceMode.isSilenceModeEnable(this.mContext);
                while (it.hasNext()) {
                    ((QuietModeEnableListener) it.next()).onQuietModeEnableChange(isSilenceModeEnable);
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class SimCards {
        public static final String BIND_ID = "bind_id";
        public static final Uri CONTENT_URI = Uri.parse("content://mms-sms/sim-cards");
        public static final String DL_STATUS = "download_status";
        @Deprecated
        public static final String IMSI = "imsi";
        public static final String MARKER1 = "marker1";
        public static final String MARKER2 = "marker2";
        public static final String MARKER_BASE = "marker_base";
        public static final String NUMBER = "number";
        public static final String SIM_ID = "sim_id";
        @Deprecated
        public static final String SLOT = "slot";
        public static final String SYNC_ENABLED = "sync_enabled";
        public static final String SYNC_EXTRA_INFO = "sync_extra_info";
        public static final String _ID = "_id";

        /* loaded from: classes3.dex */
        public static final class DLStatus {
            public static final int FINISH = 2;
            public static final int INIT = 0;
            public static final int NEED = 1;
        }

        /* loaded from: classes3.dex */
        public static final class SyncStatus {
            public static final int ACTIVE = 1;
            public static final int CLOSED = 2;
            public static final int DIRTY_MASK = 10000;
            public static final int INACTIVE = 0;
        }
    }

    /* loaded from: classes3.dex */
    public static final class Sms implements TextBasedSmsColumns, DeletableSyncColumns {
        public static final String ACCOUNT = "account";
        public static final String ADDRESSES = "addresses";
        public static final String ADVANCED_SEEN = "advanced_seen";
        public static final String B2C_NUMBERS = "b2c_numbers";
        public static final String B2C_TTL = "b2c_ttl";
        public static final String BIND_ID = "bind_id";
        public static final String BLOCK_TYPE = "block_type";
        public static final String FAKE_CELL_TYPE = "fake_cell_type";
        public static final String FAVORITE_DATE = "favorite_date";
        public static final String MX_ID = "mx_id";
        public static final String MX_STATUS = "mx_status";
        public static final String OUT_TIME = "out_time";
        public static final String SIM_ID = "sim_id";
        public static final String TIMED = "timed";
        public static final String URL_RISKY_TYPE = "url_risky_type";

        /* loaded from: classes3.dex */
        public static final class FakeCellType {
            public static final int CHECKED_SAFE = -1;
            public static final int FAKE = 1;
            public static final int NORMAL = 0;
        }

        /* loaded from: classes3.dex */
        public static final class Intents {
            public static final String DISMISS_NEW_MESSAGE_NOTIFICATION_ACTION = "android.provider.Telephony.DISMISS_NEW_MESSAGE_NOTIFICATION";
        }

        /* loaded from: classes3.dex */
        public static final class UrlRiskyType {
            public static final int URL_FRAUD_DANGEROUS = 3;
            public static final int URL_RISKY = 2;
            public static final int URL_SAFE = 0;
            public static final int URL_SUSPICIOUS = 1;
        }
    }

    /* loaded from: classes3.dex */
    public static final class SmsPhrase {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/smsphrase";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/smsphrase";
    }

    /* loaded from: classes3.dex */
    public interface SyncColumns {
        public static final String MARKER = "marker";
        public static final String SOURCE = "source";
        public static final String SYNC_STATE = "sync_state";
    }

    /* loaded from: classes3.dex */
    public interface TextBasedSmsColumns {
        public static final int MESSAGE_TYPE_INVALID = 7;
    }

    /* loaded from: classes3.dex */
    public static final class Threads implements ThreadsColumns {

        /* loaded from: classes3.dex */
        public static final class Intents {
            public static final String THREADS_OBSOLETED_ACTION = "android.intent.action.SMS_THREADS_OBSOLETED_ACTION";
        }
    }

    /* loaded from: classes3.dex */
    public interface ThreadsColumns extends SyncColumns {
        public static final String HAS_DRAFT = "has_draft";
        public static final String LAST_SIM_ID = "last_sim_id";
        public static final String MX_SEQ = "mx_seq";
        public static final String PRIVATE_ADDR_IDS = "private_addr_ids";
        public static final String RMS_TYPE = "rms_type";
        public static final String SP_TYPE = "sp_type";
        public static final String STICK_TIME = "stick_time";
        public static final String UNREAD_COUNT = "unread_count";
    }

    /* loaded from: classes3.dex */
    public static final class UnderstandInfo {
        public static final String CLASS = "class";
        public static final String MSG_ID = "msg_id";
        public static final String MSG_TYPE = "msg_type";
        public static final String OUT_OF_DATE = "out_of_date";
        public static final String VERSION = "version";

        /* loaded from: classes3.dex */
        public static final class MessageType {
            public static final int RMS = 1;
            public static final int SMS = 0;
        }

        /* loaded from: classes3.dex */
        public static final class UnderstandClass {
            public static final int NORMAL = 0;
            public static final int VERIFICATION_CODE = 1;
        }
    }

    /* loaded from: classes3.dex */
    public static final class Whitelist implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/firewall-whitelist";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/firewall-whitelist";
        public static final Uri CONTENT_URI = Uri.parse("content://firewall/whitelist");
        public static final String DISPLAY_NUMBER = "display_number";
        public static final String ISDISPLAY = "isdisplay";
        public static final String NOTES = "notes";
        public static final String NUMBER = "number";
        public static final String STATE = "state";
        public static final String VIP = "vip";
    }

    private static void appendNonSeparator(StringBuilder sb, char c, int i) {
        if (!(i == 0 && c == '+') && Character.digit(c, 10) == -1 && PhoneNumberUtils.isNonSeparator(c)) {
            sb.append(c);
        }
    }

    public static boolean checkKeyguardForQuiet(Context context, String str) {
        Cursor query;
        if (Build.IS_MIUI) {
            if (MiuiSettings.SilenceMode.isSupported) {
                return checkKeyguardForSilentMode(context);
            }
            if (Build.VERSION.SDK_INT < 21) {
                return MiuiSettings.AntiSpam.isQuietModeEnable(context);
            }
            if (MiuiSettings.AntiSpam.isQuietModeEnable(context)) {
                if ("com.android.mms".equals(str) || "com.android.incallui".equals(str) || "com.android.server.telecom".equals(str)) {
                    return true;
                }
                try {
                    query = context.getContentResolver().query(Uri.withAppendedPath(Uri.parse("content://antispamCommon/zenmode"), Phonelist.TYPE_CLOUDS_BLACK), null, null, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (query != null) {
                    query.close();
                    return false;
                }
                if (query != null) {
                    query.close();
                }
                return true;
            }
            return false;
        }
        return true;
    }

    public static boolean checkKeyguardForSilentMode(Context context) {
        if (miui.os.Build.IS_MIUI) {
            return Build.VERSION.SDK_INT < 21 ? MiuiSettings.SilenceMode.isSilenceModeEnable(context) : MiuiSettings.SilenceMode.getZenMode(context) == 1;
        }
        return false;
    }

    public static boolean checkMarkedNumberIntercept(Context context, int i, int i2, String str, int i3, boolean z, int i4) {
        String str2 = (String) ((HashMap) MiuiSettings.AntiSpam.mapIdToState.get(Integer.valueOf(i))).get(Integer.valueOf(i2));
        if (str2 == null) {
            Slog.d(TAG, "the mark type of cid is not found ... allow");
            return false;
        }
        if (!(MiuiSettings.AntiSpam.getMode(context, str2, 1) == 0)) {
            Slog.d(TAG, "the switch of " + str2 + " is not open ... allow");
            return false;
        } else if (isRelatedNumber(context, str)) {
            Slog.d(TAG, "call number is a related number... allow");
            return false;
        } else {
            boolean z2 = MiuiSettings.AntiSpam.getMode(context, (String) ((HashMap) MiuiSettings.AntiSpam.mapIdToMarkTime.get(Integer.valueOf(i))).get(Integer.valueOf(i2)), 50) <= i4;
            Slog.d(TAG, "marking threshold reached ? " + z2);
            if (z || i3 == 398 || z2) {
                Slog.d(TAG, "should intercept this marked call !");
                return true;
            }
            return false;
        }
    }

    public static boolean containsKeywords(Context context, String str, int i, int i2) {
        Cursor query = context.getContentResolver().query(Keyword.CONTENT_URI, null, "type = ? AND sim_id = ? ", new String[]{String.valueOf(i), String.valueOf(i2)}, null);
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    try {
                        String trim = query.getString(query.getColumnIndex("data")).trim();
                        if (!TextUtils.isEmpty(trim) && str.toLowerCase().contains(trim.toLowerCase())) {
                            return true;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Cursor exception in shouldFilter()", e);
                    }
                } finally {
                    query.close();
                }
            }
        }
        return false;
    }

    private static String convertPresentationToFilterNumber(int i, String str) {
        return i == PhoneConstants.PRESENTATION_RESTRICTED ? miui.telephony.PhoneNumberUtils.PRIVATE_NUMBER : i == PhoneConstants.PRESENTATION_PAYPHONE ? miui.telephony.PhoneNumberUtils.PAYPHONE_NUMBER : (TextUtils.isEmpty(str) || i == PhoneConstants.PRESENTATION_UNKNOWN) ? "-1" : str;
    }

    public static int getCallBlockType(final Context context, final String str, final int i, final boolean z, final boolean z2, final boolean z3) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        FutureTask futureTask = new FutureTask(new Callable<Integer>() { // from class: miui.provider.ExtraTelephony.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Integer call() {
                String[] strArr = new String[5];
                strArr[0] = str;
                strArr[1] = String.valueOf(i);
                strArr[2] = z ? Judge.IS_FORWARD_CALL : "";
                strArr[3] = z2 ? Judge.IS_REPEATED_NORMAL_CALL : "";
                strArr[4] = z3 ? Judge.IS_REPEATED_BLOCKED_CALL : "";
                Uri uri = Judge.CALL_CONTENT_URI;
                try {
                    Cursor query = context.getContentResolver().query(uri, strArr, null, null, null);
                    if (query != null) {
                        r7 = query.moveToNext() ? query.getInt(query.getColumnIndex(ExtraTelephony.CALL_BLOCK_TYPE)) : 0;
                        query.close();
                    } else {
                        r7 = context.getContentResolver().update(uri, new ContentValues(), null, strArr);
                    }
                } catch (Exception e) {
                    Log.e(ExtraTelephony.TAG, "getCallBlockType catch exception", e);
                }
                return Integer.valueOf(r7);
            }
        });
        try {
            new Thread(futureTask).start();
            return ((Integer) futureTask.get(5000L, TimeUnit.MILLISECONDS)).intValue();
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException when getCallBlockType", e);
            return 0;
        } catch (ExecutionException e2) {
            Log.e(TAG, "ExecutionException when getCallBlockType", e2);
            return 0;
        } catch (TimeoutException e3) {
            if (!futureTask.isDone()) {
                futureTask.cancel(true);
            }
            Log.e(TAG, "TimeoutException when getCallBlockType", e3);
            return 0;
        }
    }

    public static int getRealBlockType(int i) {
        return i & 127;
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0061  */
    /* JADX WARN: Removed duplicated region for block: B:32:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static int getSmsBlockType(android.content.Context r9, java.lang.String r10, java.lang.String r11, int r12) {
        /*
            r0 = 3
            java.lang.String[] r0 = new java.lang.String[r0]
            java.lang.String r1 = ""
            if (r10 != 0) goto L8
            r10 = r1
        L8:
            r7 = 0
            r0[r7] = r10
            r10 = 1
            if (r11 != 0) goto Lf
            r11 = r1
        Lf:
            r0[r10] = r11
            r10 = 2
            java.lang.String r11 = java.lang.String.valueOf(r12)
            r0[r10] = r11
            android.net.Uri r10 = miui.provider.ExtraTelephony.Judge.SMS_CONTENT_URI
            android.content.ContentResolver r1 = r9.getContentResolver()     // Catch: java.lang.Exception -> L54
            r4 = 0
            r5 = 0
            r6 = 0
            r2 = r10
            r3 = r0
            android.database.Cursor r11 = r1.query(r2, r3, r4, r5, r6)     // Catch: java.lang.Exception -> L54
            if (r11 == 0) goto L45
            boolean r9 = r11.moveToNext()     // Catch: java.lang.Exception -> L54
            if (r9 == 0) goto L3b
            java.lang.String r9 = "smsBlockType"
            int r9 = r11.getColumnIndex(r9)     // Catch: java.lang.Exception -> L54
            int r9 = r11.getInt(r9)     // Catch: java.lang.Exception -> L54
            goto L3c
        L3b:
            r9 = r7
        L3c:
            r11.close()     // Catch: java.lang.Exception -> L40
            goto L5e
        L40:
            r10 = move-exception
            r8 = r10
            r10 = r9
            r9 = r8
            goto L56
        L45:
            android.content.ContentResolver r9 = r9.getContentResolver()     // Catch: java.lang.Exception -> L54
            android.content.ContentValues r11 = new android.content.ContentValues     // Catch: java.lang.Exception -> L54
            r11.<init>()     // Catch: java.lang.Exception -> L54
            r12 = 0
            int r9 = r9.update(r10, r11, r12, r0)     // Catch: java.lang.Exception -> L54
            goto L5e
        L54:
            r9 = move-exception
            r10 = r7
        L56:
            java.lang.String r11 = "ExtraTelephony"
            java.lang.String r12 = "getSmsBlockType error"
            android.util.Log.e(r11, r12, r9)
            r9 = r10
        L5e:
            if (r9 >= 0) goto L61
            goto L62
        L61:
            r7 = r9
        L62:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraTelephony.getSmsBlockType(android.content.Context, java.lang.String, java.lang.String, int):int");
    }

    public static int getSmsURLScanResult(Context context, String str, String str2) {
        try {
            return context.getContentResolver().update(Judge.URL_CONTENT_URI, new ContentValues(), null, new String[]{PhoneNumberUtils.PhoneNumber.parse(str).getNormalizedNumber(false, true), str2});
        } catch (Exception e) {
            Log.e(TAG, "Exception when getSmsURLScanResult()", e);
            return -1;
        }
    }

    public static boolean isAddressInBlack(Context context, String str, int i, int i2) {
        Cursor query = context.getContentResolver().query(Phonelist.CONTENT_URI, null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{PrefixCode + PhoneNumberUtils.PhoneNumber.getLocationAreaCode(context, str), "1", String.valueOf(i2), String.valueOf(1)}, null);
        if (query != null) {
            try {
                try {
                    if (query.moveToNext()) {
                        int i3 = query.getInt(query.getColumnIndex("state"));
                        if (i3 == 0 || i3 == i) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Cursor exception in isAddressInBlack(): ", e);
                }
            } finally {
                query.close();
            }
        }
        return false;
    }

    public static boolean isCallTransferBlocked(Context context, int i) {
        try {
            return context.getContentResolver().update(Judge.CALL_TRANSFER_CONTENT_URI, new ContentValues(), null, new String[]{String.valueOf(i)}) == 1;
        } catch (Exception e) {
            Log.e(TAG, "Exception when isCallTransferBlocked()", e);
            return false;
        }
    }

    public static boolean isInBlacklist(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String normalizeNumber = str.contains("*") ? normalizeNumber(str) : PhoneNumberUtils.PhoneNumber.parse(str).getNormalizedNumber(false, true);
        if (!TextUtils.isEmpty(normalizeNumber)) {
            str = normalizeNumber;
        }
        if (str.matches("[a-zA-Z]*-[a-zA-Z]*")) {
            str = str.substring(str.indexOf("-"));
        }
        Cursor cursor = null;
        try {
            try {
                cursor = context.getContentResolver().query(Phonelist.CONTENT_URI, null, "number = ? AND type = ? AND sync_dirty <> ? ", new String[]{str, "1", String.valueOf(1)}, null);
                if (cursor != null) {
                    return cursor.getCount() > 0;
                }
            } catch (Exception e) {
                Log.e(TAG, "Cursor exception in isInBlacklist(): ", e);
            }
            return false;
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    public static boolean isInBlacklist(Context context, String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String normalizeNumber = str.contains("*") ? normalizeNumber(str) : PhoneNumberUtils.PhoneNumber.parse(str).getNormalizedNumber(false, true);
        if (!TextUtils.isEmpty(normalizeNumber)) {
            str = normalizeNumber;
        }
        if (str.matches("[a-zA-Z]*-[a-zA-Z]*")) {
            str = str.substring(str.indexOf("-"));
        }
        Cursor query = context.getContentResolver().query(Phonelist.CONTENT_URI, null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{str, "1", String.valueOf(i), String.valueOf(1)}, null);
        if (query != null) {
            try {
                return query.getCount() > 0;
            } catch (Exception e) {
                Log.e(TAG, "Cursor exception in isInBlacklist(): ", e);
            } finally {
                query.close();
            }
        }
        return false;
    }

    public static boolean isInBlacklist(Context context, String str, int i, int i2) {
        Cursor query;
        if (!TextUtils.isEmpty(str) && (query = context.getContentResolver().query(Phonelist.CONTENT_URI, null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{str, "1", String.valueOf(i2), String.valueOf(1)}, null)) != null) {
            try {
                try {
                    if (query.moveToNext()) {
                        int i3 = query.getInt(query.getColumnIndex("state"));
                        if (i3 == 0 || i3 == i) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Cursor exception in isInBlacklist(): ", e);
                }
            } finally {
                query.close();
            }
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x003e, code lost:
    
        if (r9.matches(number2regex(r8.getString(r8.getColumnIndex("number")))) == false) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0043, code lost:
    
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0048, code lost:
    
        r9 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x004a, code lost:
    
        r9 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x004b, code lost:
    
        android.util.Log.e(miui.provider.ExtraTelephony.TAG, "Cursor exception when check prefix cloudPhoneList: ", r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0053, code lost:
    
        r8.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0056, code lost:
    
        throw r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0057, code lost:
    
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0023, code lost:
    
        if (r8 == null) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0029, code lost:
    
        if (r8.moveToNext() == false) goto L25;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isInCloudPhoneList(android.content.Context r8, java.lang.String r9, int r10, java.lang.String r11) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r9)
            r1 = 0
            if (r0 == 0) goto L8
            return r1
        L8:
            android.content.ContentResolver r2 = r8.getContentResolver()
            android.net.Uri r3 = miui.provider.ExtraTelephony.Phonelist.CONTENT_URI
            r4 = 0
            r8 = 2
            java.lang.String[] r6 = new java.lang.String[r8]
            r6[r1] = r11
            java.lang.String r8 = java.lang.String.valueOf(r10)
            r10 = 1
            r6[r10] = r8
            r7 = 0
            java.lang.String r5 = "type = ? AND state in (0, ?)"
            android.database.Cursor r8 = r2.query(r3, r4, r5, r6, r7)
            if (r8 == 0) goto L57
        L25:
            boolean r11 = r8.moveToNext()     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            if (r11 == 0) goto L44
            java.lang.String r11 = "number"
            int r11 = r8.getColumnIndex(r11)     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            java.lang.String r11 = r8.getString(r11)     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            java.lang.String r11 = number2regex(r11)     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            boolean r11 = r9.matches(r11)     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            if (r11 == 0) goto L25
            r8.close()
            return r10
        L44:
            r8.close()
            goto L57
        L48:
            r9 = move-exception
            goto L53
        L4a:
            r9 = move-exception
            java.lang.String r10 = "ExtraTelephony"
            java.lang.String r11 = "Cursor exception when check prefix cloudPhoneList: "
            android.util.Log.e(r10, r11, r9)     // Catch: java.lang.Throwable -> L48
            goto L44
        L53:
            r8.close()
            throw r9
        L57:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraTelephony.isInCloudPhoneList(android.content.Context, java.lang.String, int, java.lang.String):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0042, code lost:
    
        if (android.text.TextUtils.equals(r9, r8.getString(0)) != false) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x004a, code lost:
    
        if (r9.startsWith(miui.provider.ExtraTelephony.BANK_CATEGORY_NUMBER_PREFIX_106) == false) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x004c, code lost:
    
        r8.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0050, code lost:
    
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0055, code lost:
    
        r9 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0057, code lost:
    
        r9 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0058, code lost:
    
        r9.printStackTrace();
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x005c, code lost:
    
        r8.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x005f, code lost:
    
        throw r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0060, code lost:
    
        return false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0032, code lost:
    
        if (r8 != null) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0038, code lost:
    
        if (r8.moveToNext() == false) goto L28;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isInSmsWhiteList(android.content.Context r8, java.lang.String r9) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r9)
            r1 = 0
            if (r0 == 0) goto L8
            return r1
        L8:
            android.content.ContentResolver r2 = r8.getContentResolver()
            android.net.Uri r3 = miui.yellowpage.YellowPageContract.AntispamWhiteList.CONTNET_URI
            java.lang.String r8 = "number"
            java.lang.String[] r4 = new java.lang.String[]{r8}
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "number LIKE '"
            r8.append(r0)
            r8.append(r9)
            java.lang.String r0 = "%'"
            r8.append(r0)
            java.lang.String r5 = r8.toString()
            r6 = 0
            r7 = 0
            android.database.Cursor r8 = r2.query(r3, r4, r5, r6, r7)
            if (r8 == 0) goto L60
        L34:
            boolean r0 = r8.moveToNext()     // Catch: java.lang.Throwable -> L55 java.lang.Exception -> L57
            if (r0 == 0) goto L51
            java.lang.String r0 = r8.getString(r1)     // Catch: java.lang.Throwable -> L55 java.lang.Exception -> L57
            boolean r0 = android.text.TextUtils.equals(r9, r0)     // Catch: java.lang.Throwable -> L55 java.lang.Exception -> L57
            if (r0 != 0) goto L4c
            java.lang.String r0 = "106"
            boolean r0 = r9.startsWith(r0)     // Catch: java.lang.Throwable -> L55 java.lang.Exception -> L57
            if (r0 == 0) goto L34
        L4c:
            r9 = 1
            r8.close()
            return r9
        L51:
            r8.close()
            goto L60
        L55:
            r9 = move-exception
            goto L5c
        L57:
            r9 = move-exception
            r9.printStackTrace()     // Catch: java.lang.Throwable -> L55
            goto L51
        L5c:
            r8.close()
            throw r9
        L60:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraTelephony.isInSmsWhiteList(android.content.Context, java.lang.String):boolean");
    }

    public static boolean isInVipList(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Cursor query = context.getContentResolver().query(Phonelist.CONTENT_URI, null, "number = ? AND type = ? AND sync_dirty <> ? ", new String[]{str, Phonelist.TYPE_VIP, String.valueOf(1)}, null);
        try {
            if (query != null) {
                return query.getCount() > 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "Cursor exception in isInVipList(): ", e);
        } finally {
            query.close();
        }
        return false;
    }

    public static boolean isInWhiteList(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Cursor query = context.getContentResolver().query(Phonelist.CONTENT_URI, null, "number = ? AND type = ? AND sync_dirty <> ? ", new String[]{str.contains("*") ? normalizeNumber(str) : PhoneNumberUtils.PhoneNumber.parse(str).getNormalizedNumber(false, true), "2", String.valueOf(1)}, null);
        if (query != null) {
            try {
                return query.getCount() > 0;
            } catch (Exception e) {
                Log.e(TAG, "Cursor exception in isInWhiteList(): ", e);
            } finally {
                query.close();
            }
        }
        return false;
    }

    public static boolean isInWhiteList(Context context, String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Cursor query = context.getContentResolver().query(Phonelist.CONTENT_URI, null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{str.contains("*") ? normalizeNumber(str) : PhoneNumberUtils.PhoneNumber.parse(str).getNormalizedNumber(false, true), "2", String.valueOf(i), String.valueOf(1)}, null);
        try {
            if (query != null) {
                return query.getCount() > 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "Cursor exception in isInWhiteList(): ", e);
        } finally {
            query.close();
        }
        return false;
    }

    public static boolean isInWhiteList(Context context, String str, int i, int i2) {
        int i3;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        int i4 = 4;
        Cursor query = context.getContentResolver().query(Phonelist.CONTENT_URI, null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{PrefixCode + PhoneNumberUtils.PhoneNumber.getLocationAreaCode(context, str), "2", String.valueOf(i2), String.valueOf(1)}, null);
        if (query != null) {
            try {
                try {
                    if (query.moveToNext()) {
                        int i5 = query.getInt(query.getColumnIndex("state"));
                        if (i5 == 0 || i5 == i) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Cursor exception when area check in whiteList: ", e);
                }
            } finally {
            }
        }
        String normalizedNumber = PhoneNumberUtils.PhoneNumber.parse(str).getNormalizedNumber(false, true);
        String str2 = "";
        int i6 = 0;
        while (i6 < normalizedNumber.length()) {
            String str3 = str2 + normalizedNumber.charAt(i6);
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Phonelist.CONTENT_URI;
            String[] strArr = new String[i4];
            strArr[0] = str3 + "*";
            strArr[1] = "2";
            strArr[2] = String.valueOf(i2);
            strArr[3] = String.valueOf(1);
            query = contentResolver.query(uri, null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", strArr, null);
            if (query != null) {
                try {
                    try {
                        if (query.moveToNext() && ((i3 = query.getInt(query.getColumnIndex("state"))) == 0 || i3 == i)) {
                            return true;
                        }
                    } catch (Exception e2) {
                        Log.e(TAG, "Cursor exception when prefix check in whiteList: ", e2);
                    }
                    query.close();
                } finally {
                }
            }
            i6++;
            str2 = str3;
            i4 = 4;
        }
        query = context.getContentResolver().query(Phonelist.CONTENT_URI, null, "number= ? AND type= ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{normalizedNumber, "2", String.valueOf(i2), String.valueOf(1)}, null);
        if (query != null) {
            try {
                try {
                    if (query.moveToNext()) {
                        int i7 = query.getInt(query.getColumnIndex("state"));
                        if (i7 == 0 || i7 == i) {
                            return true;
                        }
                    }
                } catch (Exception e3) {
                    Log.e(TAG, "Cursor exception when complete check in whiteList: ", e3);
                }
            } finally {
            }
        }
        return false;
    }

    public static boolean isPrefixInBlack(Context context, String str, int i, int i2) {
        int i3;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String str2 = "";
        for (int i4 = 0; i4 < str.length(); i4++) {
            str2 = str2 + str.charAt(i4);
            Cursor query = context.getContentResolver().query(Phonelist.CONTENT_URI, null, "number = ? AND type = ? AND sim_id = ? AND sync_dirty <> ? ", new String[]{str2 + "*", "1", String.valueOf(i2), String.valueOf(1)}, null);
            if (query != null) {
                try {
                    try {
                        if (query.moveToNext() && ((i3 = query.getInt(query.getColumnIndex("state"))) == 0 || i3 == i)) {
                            return true;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Cursor exception in isPrefixInBlack(): ", e);
                    }
                } finally {
                    query.close();
                }
            }
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0031, code lost:
    
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x0020, code lost:
    
        if (r1 != null) goto L5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0026, code lost:
    
        if (r1.moveToNext() == false) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x002c, code lost:
    
        if (r1.getInt(0) != 2) goto L23;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean isRelatedNumber(android.content.Context r9, java.lang.String r10) {
        /*
            r0 = 0
            r1 = 0
            android.content.ContentResolver r2 = r9.getContentResolver()     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            android.net.Uri r3 = android.provider.CallLog.Calls.CONTENT_URI     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            java.lang.String r9 = "type"
            java.lang.String[] r4 = new java.lang.String[]{r9}     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            java.lang.String r5 = "number = ? OR normalized_number = ? "
            r9 = 2
            java.lang.String[] r6 = new java.lang.String[r9]     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            r6[r0] = r10     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            r8 = 1
            r6[r8] = r10     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            java.lang.String r7 = "date DESC"
            android.database.Cursor r1 = r2.query(r3, r4, r5, r6, r7)     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            if (r1 == 0) goto L3c
        L22:
            boolean r10 = r1.moveToNext()     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            if (r10 == 0) goto L3c
            int r10 = r1.getInt(r0)     // Catch: java.lang.Throwable -> L32 java.lang.Exception -> L34
            if (r10 != r9) goto L22
            miui.util.IOUtils.closeQuietly(r1)
            return r8
        L32:
            r9 = move-exception
            goto L40
        L34:
            r9 = move-exception
            java.lang.String r10 = "ExtraTelephony"
            java.lang.String r2 = "Cursor exception in isRelatedNumber(): "
            android.util.Log.e(r10, r2, r9)     // Catch: java.lang.Throwable -> L32
        L3c:
            miui.util.IOUtils.closeQuietly(r1)
            return r0
        L40:
            miui.util.IOUtils.closeQuietly(r1)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.ExtraTelephony.isRelatedNumber(android.content.Context, java.lang.String):boolean");
    }

    public static boolean isServiceNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        PhoneNumberUtils.PhoneNumber parse = PhoneNumberUtils.PhoneNumber.parse(str);
        if (parse.isServiceNumber()) {
            return true;
        }
        return parse.isChineseNumber() && str.startsWith(BANK_CATEGORY_NUMBER_PREFIX_106);
    }

    public static boolean isTargetServiceNum(Context context, String str) {
        try {
            return context.getContentResolver().update(Judge.SERVICE_NUM_CONTENT_URI, new ContentValues(), null, new String[]{PhoneNumberUtils.PhoneNumber.parse(str).getNormalizedNumber(false, true)}) == 1;
        } catch (Exception e) {
            Log.e(TAG, "Exception when isTargetServiceNum()", e);
            return false;
        }
    }

    public static boolean isURLFlagRisky(int i) {
        return (i & 128) == 128;
    }

    public static String normalizeNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            appendNonSeparator(sb, charAt, i);
            int digit = Character.digit(charAt, 10);
            if (digit != -1) {
                sb.append(digit);
            } else if (i == 0 && charAt == '+') {
                sb.append(charAt);
            } else if ((charAt >= 'a' && charAt <= 'z') || (charAt >= 'A' && charAt <= 'Z')) {
                return normalizeNumber(android.telephony.PhoneNumberUtils.convertKeypadLettersToDigits(str));
            }
        }
        return sb.toString();
    }

    private static String number2regex(String str) {
        return str.replace("*", "[\\s\\S]*").replace("#", "[\\s\\S]").replace(CountryCode.GSM_GENERAL_IDD_CODE, "\\+");
    }

    private static void registerContentObserver(ContentResolver contentResolver, Uri uri, boolean z, ContentObserver contentObserver, int i) {
        try {
            Method declaredMethod = ContentResolver.class.getDeclaredMethod("registerContentObserver", Uri.class, Boolean.TYPE, ContentObserver.class, Integer.TYPE);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(contentResolver, uri, Boolean.valueOf(z), contentObserver, Integer.valueOf(i));
        } catch (Exception e) {
            Log.w(TAG, "invoke registerContentObserver failed", e);
        }
    }

    public static void registerQuietModeEnableListener(Context context, QuietModeEnableListener quietModeEnableListener) {
        if (miui.os.Build.IS_MIUI) {
            mQuietListeners.add(quietModeEnableListener);
            if (mSilentModeObserver == null) {
                mSilentModeObserver = new SilentModeObserver(context, new Handler());
                if (!MiuiSettings.SilenceMode.isSupported) {
                    registerContentObserver(context.getContentResolver(), Settings.Secure.getUriFor("quiet_mode_enable"), false, mSilentModeObserver, -1);
                    return;
                }
                registerContentObserver(context.getContentResolver(), Settings.Global.getUriFor(ZEN_MODE), false, mSilentModeObserver, -1);
                registerContentObserver(context.getContentResolver(), Settings.System.getUriFor("vibrate_in_silent"), false, mSilentModeObserver, -1);
                registerContentObserver(context.getContentResolver(), Settings.System.getUriFor("show_notification"), false, mSilentModeObserver, -1);
            }
        }
    }

    public static void sendCallInterceptNotification(Context context, String str, int i, int i2) {
        Intent intent = new Intent("miui.intent.action.FIREWALL_UPDATED");
        intent.putExtra("key_sim_id", i2);
        intent.putExtra("notification_intercept_number", str);
        intent.putExtra("key_block_log_type", 1);
        intent.putExtra("notification_block_type", i);
        if (i == 3 || i == 6 || i == 13) {
            intent.putExtra("notification_show_type", 0);
        } else {
            intent.putExtra("notification_show_type", 1);
        }
        context.sendBroadcastAsUser(intent, UserHandle.CURRENT_OR_SELF);
    }

    public static void sendMsgInterceptNotification(Context context, int i, int i2) {
        if (miui.os.Build.IS_MIUI) {
            Intent intent = new Intent("miui.intent.action.FIREWALL_UPDATED");
            intent.putExtra("key_sim_id", i2);
            intent.putExtra("key_block_log_type", 2);
            if (i == 3 || i == 13 || i == 6 || i == 12) {
                intent.putExtra("notification_show_type", 0);
            } else {
                intent.putExtra("notification_show_type", 1);
            }
            context.sendBroadcast(intent);
        }
    }

    public static void unRegisterQuietModeEnableListener(Context context, QuietModeEnableListener quietModeEnableListener) {
        mQuietListeners.remove(quietModeEnableListener);
        if (mQuietListeners.size() > 0 || mSilentModeObserver == null) {
            return;
        }
        context.getContentResolver().unregisterContentObserver(mSilentModeObserver);
        mSilentModeObserver = null;
    }
}
