package miui.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import java.io.File;
import miui.os.Environment;
import miui.os.FileUtils;
import miui.provider.ExtraTelephony;

/* loaded from: classes3.dex */
public class Recordings {
    public static final String APP_RECORD_DIR;
    public static final String AUTHORITY = "records";
    public static final String CALL_RECORD_DIR;
    public static final String FM_RECORD_DIR;
    private static final String[] HEXDIGITS;
    private static final String MEDIA_SCANNER_CLASS = "com.android.providers.media.MediaScannerReceiver";
    private static final String MEDIA_SCANNER_PACKAGE = "com.android.providers.media";
    public static final String RECORDER_ROOT_PATH;
    public static final String SAMPLE_DEFAULT_DIR = "/sound_recorder";
    private static final String TAG = "SoundRecorder:SoundRecorder";

    /* loaded from: classes3.dex */
    public static class CachedAccount {
        public static final Uri CONTENT_URI = Uri.parse("content://records/cached_account");
        public static final String TABLE_NAME = "cached_account";
        public static final String URI_PATH = "cached_account";

        /* loaded from: classes3.dex */
        public static final class Columns implements BaseColumns {
            public static final String ACCOUNT_NAME = "account_name";
        }
    }

    /* loaded from: classes3.dex */
    public static class CallRecords {
        public static final Uri CONTENT_URI = Uri.parse("content://records/call_records");
        public static final String TABLE_NAME = "call_records";
        public static final String URI_PATH = "call_records";

        /* loaded from: classes3.dex */
        public static final class Columns implements BaseColumns {
            public static final String NUMBER = "number";
            public static final String RECORD_ID = "record_id";
        }
    }

    /* loaded from: classes3.dex */
    public static class CallRecordsView {
        public static final Uri CONTENT_URI = Uri.parse("content://records/call_records_view");
        public static final String URI_PATH = "call_records_view";
        public static final String VIEW_NAME = "call_records_view";
    }

    /* loaded from: classes3.dex */
    public static class Downloads {
        public static final Uri CONTENT_URI = Uri.parse("content://records/downloads");
        public static final String TABLE_NAME = "downloads";
        public static final String URI_PATH = "downloads";

        /* loaded from: classes3.dex */
        public static final class Columns implements BaseColumns {
            public static final String FILE_ID = "file_id";
            public static final String FILE_NAME = "file_name";
            public static final String FILE_PATH = "file_path";
            public static final String FILE_SIZE = "file_size";
            public static final String PROGRESS = "progress";
            public static final String REC_ID = "rec_id";
            public static final String STATUS = "status";
        }

        /* loaded from: classes3.dex */
        public static final class Status {
            public static final int Downloading = 1;
            public static final int Failed = 4;
            public static final int Paused = 3;
            public static final int Pendding = 2;
            public static final int PenddingByNetwork = 5;
            public static final int Success = 0;
        }
    }

    /* loaded from: classes3.dex */
    public static class MarkPoints {
        public static final Uri CONTENT_URI = Uri.parse("content://records/mark_points");
        public static final String TABLE_NAME = "mark_points";
        public static final String URI_PATH = "mark_points";

        /* loaded from: classes3.dex */
        public static final class Columns implements BaseColumns {
            public static final String DESCRIPTION = "desp";
            public static final String E_TAG = "e_tag";
            public static final String FILE_SHA1 = "file_sha1";
            public static final String PATH = "path";
            public static final String RECORD_ID = "record_id";
            public static final String SYNC_DIRTY = "sync_dirty";
            public static final String TIME_POINT = "time_point";
            public static final String TYPE = "type";
        }

        /* loaded from: classes3.dex */
        public static final class SyncDirty {
            public static final int DIRTY = 1;
            public static final int SYNCED = 0;
            public static final int SYNC_ERROR = 2;
        }
    }

    /* loaded from: classes3.dex */
    public static class MarkpointsOperations {
        public static final Uri CONTENT_URI = Uri.parse("content://records/markpoint_operations");
        public static final String TABLE_NAME = "markpoint_operations";
        public static final String URI_PATH = "markpoint_operations";

        /* loaded from: classes3.dex */
        public static final class Columns implements BaseColumns {
            public static final String CLOUD_RECORD_ID = "cloud_record_id";
            public static final String E_TAG = "e_tag";
            public static final String OPER = "oper";
            public static final String REC_ID = "rec_id";
        }

        /* loaded from: classes3.dex */
        public static final class Opers {
            public static final int ADD = 103;
            public static final int DELETE = 101;
            public static final int UPDATE = 102;
        }
    }

    /* loaded from: classes3.dex */
    public static class Operations {
        public static final Uri CONTENT_URI = Uri.parse("content://records/operations");
        public static final String TABLE_NAME = "operations";
        public static final String URI_PATH = "operations";

        /* loaded from: classes3.dex */
        public static final class Columns implements BaseColumns {
            public static final String DESC = "decs";
            public static final String FILE_ID = "file_id";
            public static final String OPER = "oper";
            public static final String REC_ID = "rec_id";
        }

        /* loaded from: classes3.dex */
        public static final class Opers {
            public static final int DELETE = 0;
            public static final int RENAME = 1;
        }
    }

    /* loaded from: classes3.dex */
    public static class RecordingNotifications {
        public static final Uri CONTENT_URI = Uri.parse("content://records/recordingnotifications");
        public static final String EXTRA_DIRPATH = "extra_dirpath";
        public static final String EXTRA_RECTYPE = "extra_rectype";
        public static final String TABLE_NAME = "recordingnotifications";
        public static final String URI_PATH = "recordingnotifications";

        /* loaded from: classes3.dex */
        public static final class Columns implements BaseColumns {
            public static final String CNT_UNREAD = "cnt_unread";
            public static final String NOTIF_DESC = "NOTIF_DESC";
            public static final String REC_TYPE = "rec_type";
        }
    }

    /* loaded from: classes3.dex */
    public static class Records {
        public static final Uri CONTENT_URI = Uri.parse("content://records/records");
        public static final String TABLE_NAME = "records";
        public static final String URI_PATH = "records";

        /* loaded from: classes3.dex */
        public static final class Columns implements BaseColumns {
            public static final String CLOUD_SYNC_TIME = "cloud_sync_time";
            public static final String CONTENT = "content";
            public static final String CREATE_TIME = "create_time";
            public static final String DB_SYNC_TIME = "db_sync_time";
            public static final String DURATION = "duration";
            public static final String FILE_ID = "file_id";
            public static final String FILE_NAME = "file_name";
            public static final String FILE_PATH = "file_path";
            public static final String FILE_SIZE = "file_size";
            public static final String IN_CLOUD = "in_cloud";
            public static final String IN_LOCAL = "in_local";
            public static final String REC_DESC = "rec_desc";
            public static final String REC_TYPE = "rec_type";
            public static final String SHA1 = "sha1";
            public static final String SYNC_DIRTY = "sync_dirty";
        }

        /* loaded from: classes3.dex */
        public static final class InCloud {
            public static final int IN_CLOUD = 1;
            public static final int NOT_IN_CLOUD = 0;
        }

        /* loaded from: classes3.dex */
        public static final class InLocal {
            public static final int IN_LOCAL = 1;
            public static final int NOT_IN_LOCAL = 0;
        }

        /* loaded from: classes3.dex */
        public static final class Order {
            public static final String BY_CTREAT_TIME_DESC = "cloud_sync_time DESC";
        }

        /* loaded from: classes3.dex */
        public static final class RecType {
            public static final int APP = 3;
            public static final int CALL = 1;
            public static final int FM = 2;
            public static final int NORMAL = 0;
        }

        /* loaded from: classes3.dex */
        public static final class SyncDirty {
            public static final int DIRTY = 1;
            public static final int SYNCED = 0;
        }
    }

    /* loaded from: classes3.dex */
    public static class SyncTokens {
        public static final Uri CONTENT_URI = Uri.parse("content://records/synctokens");
        public static final String OLD_TABLE_NAME = "markpoint_synctoken";
        public static final String TABLE_NAME = "synctokens";
        public static final String URI_PATH = "synctokens";

        /* loaded from: classes3.dex */
        public static final class Columns implements BaseColumns {
            public static final String SYNC_EXTRA_INFO = "sync_extra_info";
            public static final String SYNC_TOKEN = "sync_token";
            public static final String SYNC_TOKEN_TYPE = "sync_token_type";
            public static final String WATER_MARK = "water_mark";
        }

        /* loaded from: classes3.dex */
        public static final class TokenType {
            public static final int TYPE_FILE_LIST = 1;
            public static final int TYPE_MARK_POINT = 0;
        }
    }

    static {
        String str = Environment.getExternalStorageMiuiDirectory().getAbsolutePath() + SAMPLE_DEFAULT_DIR;
        RECORDER_ROOT_PATH = str;
        CALL_RECORD_DIR = str + "/call_rec";
        FM_RECORD_DIR = str + "/fm_rec";
        APP_RECORD_DIR = str + "/app_rec";
        HEXDIGITS = new String[]{"0", "1", "2", ExtraTelephony.Phonelist.TYPE_VIP, ExtraTelephony.Phonelist.TYPE_CLOUDS_BLACK, ExtraTelephony.Phonelist.TYPE_CLOUDS_WHITE, ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_BLACK, ExtraTelephony.Phonelist.TYPE_STRONG_CLOUDS_WHITE, "8", "9", "a", "b", "c", MiCloudSmsCmd.TYPE_DISCARD_TOKEN, "e", "f"};
    }

    private static String byteArrayToHexString(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (int i = 0; i < bArr.length; i++) {
            String[] strArr = HEXDIGITS;
            stringBuffer.append(strArr[(bArr[i] >>> 4) & 15]);
            stringBuffer.append(strArr[bArr[i] & 15]);
        }
        return stringBuffer.toString();
    }

    public static int getNotificationUnreadCount(Context context, String str) {
        int i = 0;
        String[] strArr = {str};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(RecordingNotifications.CONTENT_URI, new String[]{RecordingNotifications.Columns.CNT_UNREAD}, "rec_type=?", strArr, null, null);
            if (cursor != null && cursor.getCount() == 1) {
                cursor.moveToFirst();
                i = cursor.getInt(0);
            }
            return i;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX WARN: Not initialized variable reg: 3, insn: 0x0031: MOVE (r2 I:??[OBJECT, ARRAY]) = (r3 I:??[OBJECT, ARRAY]), block:B:14:0x0031 */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0046 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String getSha1(java.io.File r7) {
        /*
            java.lang.String r0 = "Exception when close inputstream"
            java.lang.String r1 = "SoundRecorder:SoundRecorder"
            r2 = 0
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L35 java.lang.Exception -> L37
            r3.<init>(r7)     // Catch: java.lang.Throwable -> L35 java.lang.Exception -> L37
            java.lang.String r7 = "SHA1"
            java.security.MessageDigest r7 = java.security.MessageDigest.getInstance(r7)     // Catch: java.lang.Throwable -> L30 java.lang.Exception -> L33
            r4 = 8192(0x2000, float:1.148E-41)
            byte[] r4 = new byte[r4]     // Catch: java.lang.Throwable -> L30 java.lang.Exception -> L33
        L14:
            int r5 = r3.read(r4)     // Catch: java.lang.Throwable -> L30 java.lang.Exception -> L33
            if (r5 < 0) goto L1f
            r6 = 0
            r7.update(r4, r6, r5)     // Catch: java.lang.Throwable -> L30 java.lang.Exception -> L33
            goto L14
        L1f:
            byte[] r7 = r7.digest()     // Catch: java.lang.Throwable -> L30 java.lang.Exception -> L33
            java.lang.String r2 = byteArrayToHexString(r7)     // Catch: java.lang.Throwable -> L30 java.lang.Exception -> L33
            r3.close()     // Catch: java.io.IOException -> L2b
            goto L43
        L2b:
            r7 = move-exception
            android.util.Log.e(r1, r0, r7)
            goto L43
        L30:
            r7 = move-exception
            r2 = r3
            goto L44
        L33:
            r7 = move-exception
            goto L39
        L35:
            r7 = move-exception
            goto L44
        L37:
            r7 = move-exception
            r3 = r2
        L39:
            java.lang.String r4 = "Exception when getSha1"
            android.util.Log.e(r1, r4, r7)     // Catch: java.lang.Throwable -> L30
            if (r3 == 0) goto L43
            r3.close()     // Catch: java.io.IOException -> L2b
        L43:
            return r2
        L44:
            if (r2 == 0) goto L4e
            r2.close()     // Catch: java.io.IOException -> L4a
            goto L4e
        L4a:
            r2 = move-exception
            android.util.Log.e(r1, r0, r2)
        L4e:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.provider.Recordings.getSha1(java.io.File):java.lang.String");
    }

    public static void notifyRecording(Context context, String str, long j) {
        if (str == null) {
            return;
        }
        File file = new File(str);
        int i = str.startsWith(CALL_RECORD_DIR) ? 1 : str.startsWith(FM_RECORD_DIR) ? 2 : str.startsWith(APP_RECORD_DIR) ? 3 : str.startsWith(RECORDER_ROOT_PATH) ? 0 : -1;
        if (!file.exists() || i == -1) {
            return;
        }
        FileUtils.addNoMedia(RECORDER_ROOT_PATH);
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setClassName(MEDIA_SCANNER_PACKAGE, MEDIA_SCANNER_CLASS);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
        String sha1 = getSha1(file);
        if (TextUtils.isEmpty(sha1)) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("file_path", str);
        contentValues.put("file_name", file.getName());
        contentValues.put("create_time", Long.valueOf(file.lastModified()));
        contentValues.put("rec_type", Integer.valueOf(i));
        contentValues.put(Records.Columns.DB_SYNC_TIME, Long.valueOf(System.currentTimeMillis()));
        contentValues.put("duration", Long.valueOf(j / 1000));
        contentValues.put("sync_dirty", (Integer) 1);
        contentValues.put(Records.Columns.IN_LOCAL, (Integer) 1);
        contentValues.put(Records.Columns.IN_CLOUD, (Integer) 0);
        contentValues.put(Records.Columns.SHA1, sha1);
        context.getContentResolver().insert(Records.CONTENT_URI, contentValues);
    }

    public static void setNotificationUnreadCount(Context context, String str, int i, String str2) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] strArr = {RecordingNotifications.Columns.CNT_UNREAD};
        boolean z = false;
        String[] strArr2 = {str};
        Cursor cursor = null;
        try {
            Uri uri = RecordingNotifications.CONTENT_URI;
            cursor = contentResolver.query(uri, strArr, "rec_type=?", strArr2, null, null);
            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    z = true;
                }
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(RecordingNotifications.Columns.CNT_UNREAD, Integer.valueOf(i));
            if (str2 != null) {
                contentValues.put(RecordingNotifications.Columns.NOTIF_DESC, str2);
            }
            if (z) {
                contentResolver.update(uri, contentValues, "rec_type=?", strArr2);
                return;
            }
            contentValues.put("rec_type", str);
            contentResolver.insert(uri, contentValues);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
