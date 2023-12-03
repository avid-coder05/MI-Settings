package com.android.settings.bluetooth;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import java.io.File;
import java.io.FileNotFoundException;

/* loaded from: classes.dex */
public class MiuiFastConnectResourceProvider extends ContentProvider {
    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
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
        return false;
    }

    @Override // android.content.ContentProvider
    public AssetFileDescriptor openAssetFile(Uri uri, String str) throws FileNotFoundException {
        return super.openAssetFile(uri, str);
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        Log.d("MiuiFastConnectResourceProvider", " fetch " + uri);
        if (uri != null && uri.toString().endsWith(SplitConstants.DOT_ZIP) && !uri.toString().contains("../")) {
            try {
                File file = new File(getContext().getFilesDir(), uri.getPath());
                if (file.exists()) {
                    return ParcelFileDescriptor.open(file, 268435456);
                }
            } catch (Exception e) {
                Log.e("MiuiFastConnectResourceProvider", "failed to openFile", e);
            }
        }
        throw new FileNotFoundException("uri not allow");
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
