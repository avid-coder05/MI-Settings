package com.android.settings.slices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.FunctionColumns;
import com.android.settings.search.provider.SettingsProvider;
import java.util.List;

/* loaded from: classes2.dex */
class SlicesIndexer implements Runnable {
    private Context mContext;
    private SlicesDatabaseHelper mHelper;

    public SlicesIndexer(Context context) {
        this.mContext = context;
        this.mHelper = SlicesDatabaseHelper.getInstance(context);
    }

    List<SliceData> getSliceData() {
        return FeatureFactory.getFactory(this.mContext).getSlicesFeatureProvider().getSliceDataConverter(this.mContext).getSliceData();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void indexSliceData() {
        if (this.mHelper.isSliceDataIndexed()) {
            Log.d("SlicesIndexer", "Slices already indexed - returning.");
            return;
        }
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        long currentTimeMillis = System.currentTimeMillis();
        writableDatabase.beginTransaction();
        try {
            this.mHelper.reconstruct(writableDatabase);
            insertSliceData(writableDatabase, getSliceData());
            this.mHelper.setIndexedState();
            Log.d("SlicesIndexer", "Indexing slices database took: " + (System.currentTimeMillis() - currentTimeMillis));
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    void insertSliceData(SQLiteDatabase sQLiteDatabase, List<SliceData> list) {
        for (SliceData sliceData : list) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SettingsProvider.ARGS_KEY, sliceData.getKey());
            contentValues.put("slice_uri", sliceData.getUri().toSafeString());
            contentValues.put("title", sliceData.getTitle());
            contentValues.put(FunctionColumns.SUMMARY, sliceData.getSummary());
            CharSequence screenTitle = sliceData.getScreenTitle();
            if (screenTitle != null) {
                contentValues.put("screentitle", screenTitle.toString());
            }
            contentValues.put("keywords", sliceData.getKeywords());
            contentValues.put("icon", Integer.valueOf(sliceData.getIconResource()));
            contentValues.put(FunctionColumns.FRAGMENT, sliceData.getFragmentClassName());
            contentValues.put("controller", sliceData.getPreferenceController());
            contentValues.put("slice_type", Integer.valueOf(sliceData.getSliceType()));
            contentValues.put("unavailable_slice_subtitle", sliceData.getUnavailableSliceSubtitle());
            contentValues.put("public_slice", Boolean.valueOf(sliceData.isPublicSlice()));
            sQLiteDatabase.replaceOrThrow("slices_index", null, contentValues);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        indexSliceData();
    }
}
