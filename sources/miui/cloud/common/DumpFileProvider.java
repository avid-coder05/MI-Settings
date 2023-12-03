package miui.cloud.common;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/* loaded from: classes3.dex */
public class DumpFileProvider extends ContentProvider {
    protected static final String DUMP_FILE_DIR = "dump";
    private static final String METHOD_GET_DUMP_FILE_PATHS = "getDumpFilePaths";
    private static final String RESULT_GET_DUMP_FILE_PATHS = "dumpFilePaths";
    private static final String TAG = "DumpFileProvider";

    private void walkAndGetRelativePaths(String str, File file, ArrayList<String> arrayList) {
        if (file.isFile()) {
            String replace = file.getPath().replace(str, "");
            if (replace.startsWith("/")) {
                replace = replace.substring(1);
            }
            arrayList.add(replace);
            return;
        }
        if (!file.isDirectory()) {
            XLogger.log(TAG, "file is neither a normal file nor a directory: " + file.getPath());
            return;
        }
        for (File file2 : file.listFiles()) {
            walkAndGetRelativePaths(str, file2, arrayList);
        }
    }

    @Override // android.content.ContentProvider
    public Bundle call(String str, String str2, Bundle bundle) {
        Context context = getContext();
        if (METHOD_GET_DUMP_FILE_PATHS.equals(str)) {
            File dumpRootFile = getDumpRootFile(context);
            ArrayList<String> arrayList = new ArrayList<>();
            walkAndGetRelativePaths(dumpRootFile.getPath(), dumpRootFile, arrayList);
            Bundle bundle2 = new Bundle();
            bundle2.putStringArrayList(RESULT_GET_DUMP_FILE_PATHS, arrayList);
            return bundle2;
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    protected File getDumpRootFile(Context context) {
        return new File(context.getFilesDir(), DUMP_FILE_DIR);
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        if (uri.getPath() == null) {
            return null;
        }
        Context context = getContext();
        String path = uri.getPath();
        File dumpRootFile = getDumpRootFile(context);
        File file = new File(dumpRootFile, path);
        try {
            if (file.getCanonicalPath().startsWith(dumpRootFile.getCanonicalPath())) {
                return ParcelFileDescriptor.open(file, 268435456);
            }
            throw new IllegalArgumentException("illegal path " + path);
        } catch (IOException e) {
            XLogger.loge(TAG, " getCanonicalPath failed ", e);
            return null;
        }
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
