package miui.provider;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes3.dex */
public class Notes {
    public static final String AUTHORITY = "notes";
    public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";
    private static final String NOTES_PACKAGE_NAME = "com.miui.notes";
    private static final String TAG = "Notes";

    /* loaded from: classes3.dex */
    public interface Account extends BaseColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final Uri CONTENT_URI = Uri.parse("content://notes/account");
        public static final String DATA = "data";
    }

    /* loaded from: classes3.dex */
    public interface CallData extends Data {
        public static final String CALL_DATE = "data1";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/call_note";
        public static final String PHONE_NUMBER = "data3";
    }

    /* loaded from: classes3.dex */
    public interface Data extends BaseColumns {
        public static final String CONTENT = "content";
        public static final Uri CONTENT_URI;
        public static final Uri CONTENT_URI_FOR_SYNC_ADAPTER;
        public static final String CREATED_DATE = "created_date";
        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATA3 = "data3";
        public static final String DATA4 = "data4";
        public static final String DATA5 = "data5";
        public static final String DIRTY = "dirty";
        public static final String FILE_ID = "file_id";
        public static final String ID = "_id";
        public static final Uri MEDIA_URI;
        public static final String MIME_TYPE = "mime_type";
        public static final String MODIFIED_DATE = "modified_date";
        public static final String NOTE_ID = "note_id";

        static {
            Uri parse = Uri.parse("content://notes/data");
            CONTENT_URI = parse;
            CONTENT_URI_FOR_SYNC_ADAPTER = Notes.appendSyncAdapterFlag(parse);
            MEDIA_URI = Uri.parse("content://notes/data/media");
        }
    }

    /* loaded from: classes3.dex */
    public static class Intents {
        public static final String INTENT_ACTION_REFRESH_ALARM = "com.miui.notes.action.REFRESH_ALARM";
        public static final String INTENT_ACTION_REFRESH_WIDGET = "com.miui.notes.action.REFRESH_WIDGET";
        public static final String INTENT_EXTRA_ALERT_DATE = "com.miui.notes.alert_date";
        public static final String INTENT_EXTRA_BACKGROUND_ID = "com.miui.notes.background_color_id";
        public static final String INTENT_EXTRA_CALL_DATE = "com.miui.notes.call_date";
        public static final String INTENT_EXTRA_FOLDER_ID = "com.miui.notes.folder_id";
        public static final String INTENT_EXTRA_SNIPPET = "com.miui.notes.snippet";
        public static final String INTENT_EXTRA_SOURCE_INTENT = "com.miui.notes.source_intent";
        public static final String INTENT_EXTRA_SOURCE_NAME = "com.miui.notes.source_name";
        public static final String INTENT_EXTRA_WIDGET_ID = "com.miui.notes.widget_id";
        public static final String INTENT_EXTRA_WIDGET_TYPE = "com.miui.notes.widget_type";
    }

    /* loaded from: classes3.dex */
    public interface Note extends BaseColumns {
        public static final String ACCOUNT_ID = "account_id";
        public static final String ALERTED_DATE = "alert_date";
        public static final String ALERT_TAG = "alert_tag";
        public static final String BG_COLOR_ID = "bg_color_id";
        public static final Uri CONTENT_URI;
        public static final Uri CONTENT_URI_ATOMIC;
        public static final Uri CONTENT_URI_FOR_SYNC_ADAPTER;
        public static final String CREATED_DATE = "created_date";
        public static final String DELETION_TAG = "deletion_tag";
        public static final String HAS_ATTACHMENT = "has_attachment";
        public static final String ID = "_id";
        public static final int ID_CALL_RECORD_FOLDER = -2;
        public static final int ID_PRIVACY_FOLER = -4;
        public static final int ID_ROOT_FOLDER = 0;
        public static final int ID_TEMPARAY_FOLDER = -1;
        public static final int ID_TRASH_FOLER = -3;
        public static final String IN_VALID_FOLDER_SELECTION = "(parent_id>=0 OR parent_id=-2 OR parent_id=-4)";
        public static final String LOCAL_MODIFIED = "local_modified";
        public static final String MODIFIED_DATE = "modified_date";
        public static final String MOVED_DATE = "moved_date";
        public static final String NOTES_COUNT = "notes_count";
        public static final String ORIGIN_PARENT_ID = "origin_parent_id";
        public static final String PARENT_ID = "parent_id";
        public static final String PLAIN_TEXT = "plain_text";
        public static final String SNIPPET = "snippet";
        public static final String SOURCE_INTENT = "source_intent";
        public static final String SOURCE_NAME = "source_name";
        public static final String SOURCE_PACKAGE = "source_package";
        public static final String STICK_DATE = "stick_date";
        public static final String SUBJECT = "subject";
        public static final String SYNC_DATA1 = "sync_data1";
        public static final String SYNC_DATA2 = "sync_data2";
        public static final String SYNC_DATA3 = "sync_data3";
        public static final String SYNC_DATA4 = "sync_data4";
        public static final String SYNC_DATA5 = "sync_data5";
        public static final String SYNC_DATA6 = "sync_data6";
        public static final String SYNC_DATA7 = "sync_data7";
        public static final String SYNC_DATA8 = "sync_data8";
        public static final String SYNC_ID = "sync_id";
        public static final String SYNC_TAG = "sync_tag";
        public static final String THEME_ID = "theme_id";
        public static final String TYPE = "type";
        public static final int TYPE_FOLDER = 1;
        public static final int TYPE_NOTE = 0;
        public static final int TYPE_SYSTEM = 2;
        public static final int TYPE_WIDGET_INVALIDE = -1;
        public static final int TYPE_WIDGET_SIMPLE = 1;
        public static final String VALID_FOLDER_SELECTION = "((type=1 AND parent_id=0) OR _id=-2)";
        public static final String VERSION = "version";
        public static final String WIDGET_ID = "widget_id";
        public static final String WIDGET_TYPE = "widget_type";

        static {
            Uri parse = Uri.parse("content://notes/note");
            CONTENT_URI = parse;
            CONTENT_URI_FOR_SYNC_ADAPTER = Notes.appendSyncAdapterFlag(parse);
            CONTENT_URI_ATOMIC = Uri.parse("content://notes/note/atomic");
        }
    }

    /* loaded from: classes3.dex */
    public interface TextData extends Data {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/text_note";
    }

    /* loaded from: classes3.dex */
    public static final class Utils {
        public static final int CLEAR_ACCOUNT_WIPE_ALL = 0;
        public static final int CLEAR_ACCOUNT_WIPE_NONE = 2;
        public static final int CLEAR_ACCOUNT_WIPE_SYNC = 1;
        private static final int IMAGE_DIMENSION_MAX = 1920;
        private static final String KEY_DATA_BYTES = "data_bytes";
        private static final String KEY_DATA_VALUES = "data_values";

        private static void addDataValuesToNoteValues(ContentValues contentValues, ArrayList<ContentValues> arrayList) {
            if (arrayList == null || arrayList.isEmpty()) {
                return;
            }
            removeSnippetIfHasDataContent(contentValues, arrayList);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(KEY_DATA_VALUES, arrayList);
            contentValues.put(KEY_DATA_BYTES, marshall(bundle));
        }

        public static boolean clearAccount(Context context, int i) {
            ArrayList<ContentProviderOperation> arrayList = new ArrayList<>();
            arrayList.add(getAccountDeleteOP());
            if (i != 0) {
                if (i == 1) {
                    arrayList.add(getNoteDeleteOP(true));
                } else if (i != 2) {
                    Log.w(Notes.TAG, "Unknown wipeMode: " + i);
                }
                arrayList.add(getTemporaryDeleteOP());
                arrayList.add(getSyncClearOP());
                arrayList.add(getDirtyUpdateOP());
            } else {
                arrayList.add(getNoteDeleteOP(false));
            }
            try {
                context.getContentResolver().applyBatch("notes", arrayList);
                if (i != 2) {
                    updateAllWidgets(context);
                }
                return true;
            } catch (OperationApplicationException e) {
                Log.e(Notes.TAG, "Fail to clear account", e);
                return false;
            } catch (RemoteException e2) {
                Log.e(Notes.TAG, "Fail to clear account", e2);
                return false;
            }
        }

        public static boolean clearAccount(Context context, boolean z) {
            return clearAccount(context, z ? 0 : 2);
        }

        public static Bitmap createThumbnail(String str) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap scaleBitmap = scaleBitmap(str, options);
            if (scaleBitmap == null) {
                Log.e(Notes.TAG, "Fail to createThumbnail");
                return null;
            }
            Bitmap rotateBitmap = rotateBitmap(str, scaleBitmap, options.outMimeType);
            if (rotateBitmap == null) {
                Log.e(Notes.TAG, "Fail to rotateBitmap");
                return null;
            }
            return rotateBitmap;
        }

        private static ContentProviderOperation getAccountDeleteOP() {
            return ContentProviderOperation.newDelete(Account.CONTENT_URI).build();
        }

        private static String getContentFromData(ArrayList<ContentValues> arrayList) {
            Iterator<ContentValues> it = arrayList.iterator();
            while (it.hasNext()) {
                ContentValues next = it.next();
                if (next.containsKey(Data.MIME_TYPE) && TextData.CONTENT_ITEM_TYPE.equals(next.getAsString(Data.MIME_TYPE)) && next.containsKey("content")) {
                    return next.getAsString("content");
                }
            }
            return null;
        }

        private static ContentProviderOperation getDirtyUpdateOP() {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Data.DIRTY, (Integer) 1);
            contentValues.put("file_id", "");
            return ContentProviderOperation.newUpdate(Data.CONTENT_URI_FOR_SYNC_ADAPTER).withValues(contentValues).build();
        }

        private static ContentProviderOperation getNoteDeleteOP(boolean z) {
            String str = "_id>0";
            if (z) {
                str = "_id>0 AND sync_id>0 AND local_modified=0";
            }
            return ContentProviderOperation.newDelete(Note.CONTENT_URI_FOR_SYNC_ADAPTER).withSelection(str, null).build();
        }

        private static ContentProviderOperation getSyncClearOP() {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Note.SYNC_ID, (Integer) 0);
            contentValues.put(Note.SYNC_TAG, "");
            contentValues.put(Note.LOCAL_MODIFIED, (Integer) 1);
            contentValues.put("version", (Integer) 0);
            contentValues.put(Note.ORIGIN_PARENT_ID, (Integer) 0);
            contentValues.put("account_id", (Integer) 0);
            contentValues.put(Note.SYNC_DATA1, (Integer) 0);
            contentValues.put(Note.SYNC_DATA2, (Integer) 0);
            contentValues.put(Note.SYNC_DATA3, (Integer) 0);
            contentValues.put(Note.SYNC_DATA4, (Integer) 0);
            contentValues.put(Note.SYNC_DATA5, (Integer) 0);
            contentValues.put(Note.SYNC_DATA6, "");
            contentValues.put(Note.SYNC_DATA7, "");
            contentValues.put(Note.SYNC_DATA8, "");
            return ContentProviderOperation.newUpdate(Note.CONTENT_URI_FOR_SYNC_ADAPTER).withValues(contentValues).withSelection("_id>0", null).build();
        }

        private static ContentProviderOperation getTemporaryDeleteOP() {
            return ContentProviderOperation.newDelete(Note.CONTENT_URI_FOR_SYNC_ADAPTER).withSelection("not (parent_id>=0 OR parent_id=-2 OR parent_id=-4)", null).build();
        }

        public static int getTotalUnsyncedCount(Context context) {
            int i = 0;
            for (int i2 : getUnsyncedCount(context)) {
                i += i2;
            }
            return i;
        }

        public static int[] getUnsyncedCount(Context context) {
            int i;
            int i2;
            Cursor query = context.getContentResolver().query(Note.CONTENT_URI_FOR_SYNC_ADAPTER, new String[]{"_id", "type"}, "local_modified=1 AND _id>0 AND (sync_id>0 OR (sync_id<=0 AND snippet<>''))", null, null);
            if (query != null) {
                i = 0;
                i2 = 0;
                while (query.moveToNext()) {
                    try {
                        int i3 = query.getInt(1);
                        if (i3 == 0) {
                            i++;
                        } else if (i3 == 1) {
                            i2++;
                        }
                    } finally {
                        query.close();
                    }
                }
            } else {
                Log.e(Notes.TAG, "getUnsyncedCount: cursor is null");
                i = 0;
                i2 = 0;
            }
            return new int[]{i, i2};
        }

        public static Uri insertNoteAtomic(Context context, ContentValues contentValues, ArrayList<ContentValues> arrayList) {
            return insertNoteAtomic(context, contentValues, arrayList, false);
        }

        public static Uri insertNoteAtomic(Context context, ContentValues contentValues, ArrayList<ContentValues> arrayList, boolean z) {
            addDataValuesToNoteValues(contentValues, arrayList);
            Uri uri = Note.CONTENT_URI_ATOMIC;
            if (z) {
                uri = Notes.appendSyncAdapterFlag(uri);
            }
            return context.getContentResolver().insert(uri, contentValues);
        }

        private static byte[] marshall(Bundle bundle) {
            Parcel obtain = Parcel.obtain();
            try {
                bundle.writeToParcel(obtain, 0);
                return obtain.marshall();
            } finally {
                obtain.recycle();
            }
        }

        private static void releaseCanvas(Canvas canvas) {
            try {
                Method declaredMethod = Canvas.class.getDeclaredMethod("release", new Class[0]);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(canvas, new Object[0]);
            } catch (Exception e) {
                Log.w(Notes.TAG, "invoke Canvas.release failed", e);
            }
        }

        public static ArrayList<ContentValues> removeDataValuesFromNoteValues(ContentValues contentValues) {
            String contentFromData;
            byte[] asByteArray = contentValues.getAsByteArray(KEY_DATA_BYTES);
            contentValues.remove(KEY_DATA_BYTES);
            if (asByteArray != null) {
                ArrayList<ContentValues> parcelableArrayList = unmarshall(asByteArray).getParcelableArrayList(KEY_DATA_VALUES);
                if (contentValues.containsKey("snippet") && contentValues.getAsString("snippet") == null && (contentFromData = getContentFromData(parcelableArrayList)) != null) {
                    contentValues.put("snippet", contentFromData);
                }
                return parcelableArrayList;
            }
            return null;
        }

        private static void removeSnippetIfHasDataContent(ContentValues contentValues, ArrayList<ContentValues> arrayList) {
            String contentFromData;
            if (contentValues.containsKey("snippet") && (contentFromData = getContentFromData(arrayList)) != null && contentFromData.equals(contentValues.getAsString("snippet"))) {
                contentValues.put("snippet", (String) null);
            }
        }

        /* JADX WARN: Can't wrap try/catch for region: R(10:(14:12|(2:14|(1:16))(1:50)|(1:18)|19|20|21|22|24|25|27|28|29|30|31)(1:51)|21|22|24|25|27|28|29|30|31) */
        /* JADX WARN: Can't wrap try/catch for region: R(15:10|(14:12|(2:14|(1:16))(1:50)|(1:18)|19|20|21|22|24|25|27|28|29|30|31)(1:51)|49|(0)|19|20|21|22|24|25|27|28|29|30|31) */
        /* JADX WARN: Code restructure failed: missing block: B:28:0x006c, code lost:
        
            r7 = e;
         */
        /* JADX WARN: Code restructure failed: missing block: B:30:0x006e, code lost:
        
            r1 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:31:0x006f, code lost:
        
            r1 = null;
            r7 = r1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:34:0x0077, code lost:
        
            r9 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:35:0x0078, code lost:
        
            r1 = null;
            r7 = r9;
            r9 = null;
         */
        /* JADX WARN: Code restructure failed: missing block: B:36:0x007b, code lost:
        
            android.util.Log.e(miui.provider.Notes.TAG, "Fail to rotateBitmap", r7);
         */
        /* JADX WARN: Code restructure failed: missing block: B:37:0x0080, code lost:
        
            r8.recycle();
            r1 = r1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:38:0x0083, code lost:
        
            if (r1 == null) goto L40;
         */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:21:0x0053  */
        /* JADX WARN: Type inference failed for: r1v1 */
        /* JADX WARN: Type inference failed for: r1v16 */
        /* JADX WARN: Type inference failed for: r1v4 */
        /* JADX WARN: Type inference failed for: r1v5 */
        /* JADX WARN: Type inference failed for: r1v6 */
        /* JADX WARN: Type inference failed for: r1v7, types: [android.graphics.Canvas] */
        /* JADX WARN: Type inference failed for: r1v8 */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private static android.graphics.Bitmap rotateBitmap(java.lang.String r7, android.graphics.Bitmap r8, java.lang.String r9) {
            /*
                java.lang.String r0 = "Notes"
                java.lang.String r1 = "image/jpeg"
                boolean r9 = r1.equals(r9)
                if (r9 != 0) goto Lb
                return r8
            Lb:
                android.media.ExifInterface r9 = new android.media.ExifInterface     // Catch: java.io.IOException -> L91
                r9.<init>(r7)     // Catch: java.io.IOException -> L91
                java.lang.String r7 = "Orientation"
                r1 = 1
                int r7 = r9.getAttributeInt(r7, r1)
                if (r7 != r1) goto L1a
                return r8
            L1a:
                int r9 = r8.getWidth()
                int r2 = r8.getHeight()
                r3 = 0
                android.graphics.Matrix r4 = new android.graphics.Matrix
                r4.<init>()
                r5 = 3
                if (r7 == r5) goto L45
                r5 = 6
                if (r7 == r5) goto L3c
                r5 = 8
                if (r7 == r5) goto L33
                goto L50
            L33:
                r7 = 1132920832(0x43870000, float:270.0)
                int r3 = r9 / 2
                float r3 = (float) r3
                r4.postRotate(r7, r3, r3)
                goto L51
            L3c:
                r7 = 1119092736(0x42b40000, float:90.0)
                int r3 = r2 / 2
                float r3 = (float) r3
                r4.postRotate(r7, r3, r3)
                goto L51
            L45:
                r7 = 1127481344(0x43340000, float:180.0)
                int r1 = r9 / 2
                float r1 = (float) r1
                int r5 = r2 / 2
                float r5 = (float) r5
                r4.postRotate(r7, r1, r5)
            L50:
                r1 = r3
            L51:
                if (r1 == 0) goto L56
                r6 = r2
                r2 = r9
                r9 = r6
            L56:
                r7 = 0
                android.graphics.Bitmap$Config r1 = android.graphics.Bitmap.Config.ARGB_8888     // Catch: java.lang.Throwable -> L73 java.lang.OutOfMemoryError -> L77
                android.graphics.Bitmap r9 = android.graphics.Bitmap.createBitmap(r9, r2, r1)     // Catch: java.lang.Throwable -> L73 java.lang.OutOfMemoryError -> L77
                android.graphics.Canvas r1 = new android.graphics.Canvas     // Catch: java.lang.OutOfMemoryError -> L6e java.lang.Throwable -> L73
                r1.<init>(r9)     // Catch: java.lang.OutOfMemoryError -> L6e java.lang.Throwable -> L73
                r1.drawBitmap(r8, r4, r7)     // Catch: java.lang.OutOfMemoryError -> L6c java.lang.Throwable -> L87
                r8.recycle()
            L68:
                releaseCanvas(r1)
                goto L86
            L6c:
                r7 = move-exception
                goto L7b
            L6e:
                r1 = move-exception
                r6 = r1
                r1 = r7
                r7 = r6
                goto L7b
            L73:
                r9 = move-exception
                r1 = r7
                r7 = r9
                goto L88
            L77:
                r9 = move-exception
                r1 = r7
                r7 = r9
                r9 = r1
            L7b:
                java.lang.String r2 = "Fail to rotateBitmap"
                android.util.Log.e(r0, r2, r7)     // Catch: java.lang.Throwable -> L87
                r8.recycle()
                if (r1 == 0) goto L86
                goto L68
            L86:
                return r9
            L87:
                r7 = move-exception
            L88:
                r8.recycle()
                if (r1 == 0) goto L90
                releaseCanvas(r1)
            L90:
                throw r7
            L91:
                r7 = move-exception
                java.lang.String r9 = "createThumbnail fail"
                android.util.Log.e(r0, r9, r7)
                return r8
            */
            throw new UnsupportedOperationException("Method not decompiled: miui.provider.Notes.Utils.rotateBitmap(java.lang.String, android.graphics.Bitmap, java.lang.String):android.graphics.Bitmap");
        }

        private static Bitmap scaleBitmap(String str, BitmapFactory.Options options) {
            Bitmap scaleBitmapByRegion;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(str, options);
            int i = options.outWidth;
            int i2 = options.outHeight;
            if (i <= 0 || i2 <= 0) {
                return null;
            }
            try {
                if (i > IMAGE_DIMENSION_MAX || i2 > IMAGE_DIMENSION_MAX) {
                    float max = Math.max(i, i2) / 1920.0f;
                    int max2 = Math.max(1, (int) (i / max));
                    int max3 = Math.max(1, (int) (i2 / max));
                    int i3 = 1;
                    for (int i4 = (int) max; i4 > 1; i4 >>= 1) {
                        i3 <<= 1;
                    }
                    scaleBitmapByRegion = (i * i2) / (i3 * i3) > 7372800 ? scaleBitmapByRegion(str, i, i2, max2, max3, i3) : miui.graphics.BitmapFactory.decodeBitmap(str, max2, max3, false);
                } else {
                    scaleBitmapByRegion = miui.graphics.BitmapFactory.decodeBitmap(str, false);
                }
                return scaleBitmapByRegion;
            } catch (IOException e) {
                Log.e(Notes.TAG, "Fail to decode " + str, e);
                return null;
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:43:0x00bb, code lost:
        
            if (r14 != null) goto L18;
         */
        /* JADX WARN: Removed duplicated region for block: B:48:0x00c2  */
        /* JADX WARN: Removed duplicated region for block: B:50:0x00c7  */
        /* JADX WARN: Removed duplicated region for block: B:52:0x00cc  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private static android.graphics.Bitmap scaleBitmapByRegion(java.lang.String r16, int r17, int r18, int r19, int r20, int r21) throws java.io.IOException {
            /*
                Method dump skipped, instructions count: 208
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: miui.provider.Notes.Utils.scaleBitmapByRegion(java.lang.String, int, int, int, int, int):android.graphics.Bitmap");
        }

        private static Bundle unmarshall(byte[] bArr) {
            Parcel obtain = Parcel.obtain();
            try {
                obtain.unmarshall(bArr, 0, bArr.length);
                obtain.setDataPosition(0);
                return obtain.readBundle();
            } finally {
                obtain.recycle();
            }
        }

        public static void updateAllAlarms(Context context) {
            Intent intent = new Intent(Intents.INTENT_ACTION_REFRESH_ALARM);
            intent.setPackage(Notes.NOTES_PACKAGE_NAME);
            context.sendBroadcast(intent);
        }

        public static void updateAllWidgets(Context context) {
            Intent intent = new Intent(Intents.INTENT_ACTION_REFRESH_WIDGET);
            intent.setPackage(Notes.NOTES_PACKAGE_NAME);
            context.sendBroadcast(intent);
        }

        public static int updateNoteAtomic(Context context, long j, ContentValues contentValues, ArrayList<ContentValues> arrayList, String str, String[] strArr) {
            return updateNoteAtomic(context, j, contentValues, arrayList, str, strArr, false);
        }

        public static int updateNoteAtomic(Context context, long j, ContentValues contentValues, ArrayList<ContentValues> arrayList, String str, String[] strArr, boolean z) {
            addDataValuesToNoteValues(contentValues, arrayList);
            Uri withAppendedId = ContentUris.withAppendedId(Note.CONTENT_URI_ATOMIC, j);
            if (z) {
                withAppendedId = Notes.appendSyncAdapterFlag(withAppendedId);
            }
            return context.getContentResolver().update(withAppendedId, contentValues, str, strArr);
        }
    }

    public static Uri appendSyncAdapterFlag(Uri uri) {
        return uri.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build();
    }
}
