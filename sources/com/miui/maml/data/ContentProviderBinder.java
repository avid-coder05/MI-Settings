package com.miui.maml.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteFullException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.AsyncQueryHandler;
import com.miui.maml.data.VariableBinder;
import com.miui.maml.elements.ImageScreenElement;
import com.miui.maml.elements.ListScreenElement;
import com.miui.maml.util.TextFormatter;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import miui.os.SystemProperties;
import miui.vip.VipService;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class ContentProviderBinder extends VariableBinder {
    private volatile boolean mAllowReg;
    protected String[] mArgs;
    private boolean mAwareChangeWhilePause;
    private ChangeObserver mChangeObserver;
    protected String[] mColumns;
    protected String mCountName;
    private IndexedVariable mCountVar;
    private Handler mHandler;
    private long mLastQueryTime;
    private Uri mLastUri;
    private List mList;
    private final Object mLock;
    private boolean mNeedsRequery;
    protected String mOrder;
    private QueryHandler mQueryHandler;
    private boolean mSystemBootCompleted;
    private int mUpdateInterval;
    private Runnable mUpdater;
    protected TextFormatter mUriFormatter;
    protected TextFormatter mWhereFormatter;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.data.ContentProviderBinder$2  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type;

        static {
            int[] iArr = new int[ListScreenElement.ColumnInfo.Type.values().length];
            $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type = iArr;
            try {
                iArr[ListScreenElement.ColumnInfo.Type.DOUBLE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[ListScreenElement.ColumnInfo.Type.FLOAT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[ListScreenElement.ColumnInfo.Type.INTEGER.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[ListScreenElement.ColumnInfo.Type.LONG.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[ListScreenElement.ColumnInfo.Type.STRING.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[ListScreenElement.ColumnInfo.Type.BITMAP.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(ContentProviderBinder.this.mHandler);
        }

        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            ContentProviderBinder.this.onContentChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class List {
        private ListScreenElement mList;
        private int mMaxCount;
        private String mName;
        private ScreenElementRoot mRoot;

        public List(Element element, ScreenElementRoot screenElementRoot) {
            this.mName = element.getAttribute("name");
            this.mMaxCount = Utils.getAttrAsInt(element, "maxCount", Integer.MAX_VALUE);
            this.mRoot = screenElementRoot;
        }

        public void fill(Cursor cursor) {
            if (cursor == null) {
                return;
            }
            if (this.mList == null) {
                ListScreenElement listScreenElement = (ListScreenElement) this.mRoot.findElement(this.mName);
                this.mList = listScreenElement;
                if (listScreenElement == null) {
                    Log.e("ContentProviderBinder", "fail to find list: " + this.mName);
                    return;
                }
            }
            this.mList.removeAllItems();
            ArrayList<ListScreenElement.ColumnInfo> columnsInfo = this.mList.getColumnsInfo();
            int size = columnsInfo.size();
            int[] iArr = new int[size];
            Object[] objArr = new Object[size];
            for (int i = 0; i < size; i++) {
                try {
                    iArr[i] = cursor.getColumnIndexOrThrow(columnsInfo.get(i).mVarName);
                } catch (IllegalArgumentException e) {
                    Log.e("ContentProviderBinder", "illegal column:" + columnsInfo.get(i).mVarName + " " + e.toString());
                    return;
                }
            }
            cursor.moveToFirst();
            int count = cursor.getCount();
            int i2 = this.mMaxCount;
            if (count > i2) {
                count = i2;
            }
            for (int i3 = 0; i3 < count; i3++) {
                for (int i4 = 0; i4 < size; i4++) {
                    objArr[i4] = null;
                    ListScreenElement.ColumnInfo columnInfo = columnsInfo.get(i4);
                    int i5 = iArr[i4];
                    if (!cursor.isNull(i5)) {
                        int[] iArr2 = AnonymousClass2.$SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type;
                        int i6 = iArr2[columnInfo.mType.ordinal()];
                        if (i6 == 5) {
                            objArr[i4] = cursor.getString(i5);
                        } else if (i6 != 6) {
                            int i7 = iArr2[columnInfo.mType.ordinal()];
                            if (i7 == 1) {
                                objArr[i4] = Double.valueOf(cursor.getDouble(i5));
                            } else if (i7 == 2) {
                                objArr[i4] = Float.valueOf(cursor.getFloat(i5));
                            } else if (i7 == 3) {
                                objArr[i4] = Integer.valueOf(cursor.getInt(i5));
                            } else if (i7 == 4) {
                                objArr[i4] = Long.valueOf(cursor.getLong(i5));
                            }
                        } else {
                            byte[] blob = cursor.getBlob(i5);
                            if (blob != null) {
                                objArr[i4] = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                            }
                        }
                    }
                }
                this.mList.addItem(objArr);
                cursor.moveToNext();
            }
        }
    }

    /* loaded from: classes2.dex */
    public interface QueryCompleteListener {
        void onQueryCompleted(String str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class QueryHandler extends AsyncQueryHandler {

        /* loaded from: classes2.dex */
        protected class CatchingWorkerHandler extends AsyncQueryHandler.WorkerHandler {
            public CatchingWorkerHandler(Looper looper) {
                super(looper);
            }

            @Override // com.miui.maml.data.AsyncQueryHandler.WorkerHandler, android.os.Handler
            public void handleMessage(Message message) {
                try {
                    super.handleMessage(message);
                } catch (SQLiteDatabaseCorruptException e) {
                    Log.w("ContentProviderBinder", "Exception on background worker thread", e);
                } catch (SQLiteDiskIOException e2) {
                    Log.w("ContentProviderBinder", "Exception on background worker thread", e2);
                } catch (SQLiteFullException e3) {
                    Log.w("ContentProviderBinder", "Exception on background worker thread", e3);
                }
            }
        }

        public QueryHandler(Context context) {
            super(Looper.getMainLooper(), context.getContentResolver());
        }

        @Override // com.miui.maml.data.AsyncQueryHandler
        protected Handler createHandler(Looper looper) {
            return new CatchingWorkerHandler(looper);
        }

        @Override // com.miui.maml.data.AsyncQueryHandler
        protected void onQueryComplete(int i, Object obj, Cursor cursor) {
            ContentProviderBinder.this.onQueryComplete(cursor);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Variable extends VariableBinder.Variable {
        public boolean mBlocked;
        public String mColumn;
        private ImageScreenElement mImageVar;
        private boolean mNoImageElement;
        public int mRow;

        public Variable(Element element, Variables variables) {
            super(element, variables);
            this.mColumn = element.getAttribute("column");
            this.mRow = Utils.getAttrAsInt(element, "row", 0);
        }

        public ImageScreenElement getImageElement(ScreenElementRoot screenElementRoot) {
            if (this.mImageVar == null && !this.mNoImageElement) {
                ImageScreenElement imageScreenElement = (ImageScreenElement) screenElementRoot.findElement(this.mName);
                this.mImageVar = imageScreenElement;
                this.mNoImageElement = imageScreenElement == null;
            }
            return this.mImageVar;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.miui.maml.data.VariableBinder.TypedValue
        public int parseType(String str) {
            int parseType = super.parseType(str);
            if ("blob.bitmap".equalsIgnoreCase(this.mTypeStr)) {
                return 1001;
            }
            this.mNoImageElement = true;
            return parseType;
        }

        public void setNull(ScreenElementRoot screenElementRoot) {
            if (getImageElement(screenElementRoot) != null) {
                getImageElement(screenElementRoot).setBitmap(null);
            } else {
                set((Object) null);
            }
        }
    }

    public ContentProviderBinder(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mLock = new Object();
        this.mAllowReg = true;
        this.mChangeObserver = new ChangeObserver();
        this.mUpdateInterval = -1;
        this.mNeedsRequery = true;
        this.mHandler = screenElementRoot.getContext().getHandler();
        this.mQueryHandler = new QueryHandler(getContext().mContext);
        if (element != null) {
            load(element);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkUpdate() {
        if (this.mUpdateInterval <= 0) {
            return;
        }
        this.mHandler.removeCallbacks(this.mUpdater);
        long currentTimeMillis = System.currentTimeMillis() - this.mLastQueryTime;
        if (currentTimeMillis >= this.mUpdateInterval * VipService.VIP_SERVICE_FAILURE) {
            startQuery();
            currentTimeMillis = 0;
        }
        this.mHandler.postDelayed(this.mUpdater, (this.mUpdateInterval * VipService.VIP_SERVICE_FAILURE) - currentTimeMillis);
    }

    private void load(Element element) {
        Variables variables = getVariables();
        this.mUriFormatter = new TextFormatter(variables, element.getAttribute("uri"), element.getAttribute("uriFormat"), element.getAttribute("uriParas"), Expression.build(variables, element.getAttribute("uriExp")), Expression.build(variables, element.getAttribute("uriFormatExp")));
        String attribute = element.getAttribute("columns");
        this.mColumns = TextUtils.isEmpty(attribute) ? null : attribute.split(",");
        this.mWhereFormatter = new TextFormatter(variables, element.getAttribute("where"), element.getAttribute("whereFormat"), element.getAttribute("whereParas"), Expression.build(variables, element.getAttribute("whereExp")), Expression.build(variables, element.getAttribute("whereFormatExp")));
        String attribute2 = element.getAttribute("args");
        this.mArgs = TextUtils.isEmpty(attribute2) ? null : attribute2.split(",");
        String attribute3 = element.getAttribute("order");
        if (TextUtils.isEmpty(attribute3)) {
            attribute3 = null;
        }
        this.mOrder = attribute3;
        String attribute4 = element.getAttribute("countName");
        String str = TextUtils.isEmpty(attribute4) ? null : attribute4;
        this.mCountName = str;
        if (str != null) {
            this.mCountVar = new IndexedVariable(str, variables, true);
        }
        int attrAsInt = Utils.getAttrAsInt(element, "updateInterval", -1);
        this.mUpdateInterval = attrAsInt;
        if (attrAsInt > 0) {
            this.mUpdater = new Runnable() { // from class: com.miui.maml.data.ContentProviderBinder.1
                @Override // java.lang.Runnable
                public void run() {
                    ContentProviderBinder.this.checkUpdate();
                }
            };
        }
        loadVariables(element);
        Element child = Utils.getChild(element, "List");
        if (child != null) {
            try {
                this.mList = new List(child, this.mRoot);
            } catch (IllegalArgumentException unused) {
                Log.e("ContentProviderBinder", "invalid List");
            }
        }
        this.mAwareChangeWhilePause = Boolean.parseBoolean(element.getAttribute("vigilant"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onQueryComplete(Cursor cursor) {
        if (!this.mFinished) {
            updateVariables(cursor);
        }
        if (cursor != null) {
            cursor.close();
        }
        onUpdateComplete();
    }

    private void registerObserver(Uri uri, boolean z) {
        ContentResolver contentResolver = getContext().mContext.getContentResolver();
        contentResolver.unregisterContentObserver(this.mChangeObserver);
        if (z && this.mAllowReg) {
            synchronized (this.mLock) {
                if (this.mAllowReg) {
                    try {
                        contentResolver.registerContentObserver(uri, true, this.mChangeObserver);
                    } catch (IllegalArgumentException e) {
                        Log.e("ContentProviderBinder", e.toString() + "  uri:" + uri);
                    } catch (SecurityException e2) {
                        Log.e("ContentProviderBinder", e2.toString() + "  uri:" + uri);
                    }
                }
            }
        }
    }

    private void updateVariables(Cursor cursor) {
        int count = cursor == null ? 0 : cursor.getCount();
        IndexedVariable indexedVariable = this.mCountVar;
        if (indexedVariable != null) {
            indexedVariable.set(count);
        }
        List list = this.mList;
        if (list != null) {
            list.fill(cursor);
        }
        if (cursor == null || count == 0) {
            Iterator<VariableBinder.Variable> it = this.mVariables.iterator();
            while (it.hasNext()) {
                ((Variable) it.next()).setNull(this.mRoot);
            }
            return;
        }
        Iterator<VariableBinder.Variable> it2 = this.mVariables.iterator();
        while (it2.hasNext()) {
            VariableBinder.Variable next = it2.next();
            Variable variable = (Variable) next;
            if (!variable.mBlocked) {
                double d = 0.0d;
                if (cursor.moveToPosition(variable.mRow)) {
                    try {
                        int columnIndexOrThrow = cursor.getColumnIndexOrThrow(variable.mColumn);
                        if (cursor.isNull(columnIndexOrThrow)) {
                            variable.setNull(this.mRoot);
                        } else {
                            int i = next.mType;
                            if (i == 2) {
                                next.set(cursor.getString(columnIndexOrThrow));
                            } else if (i == 1001 || i == 7) {
                                byte[] blob = cursor.getBlob(columnIndexOrThrow);
                                Bitmap decodeByteArray = blob != null ? BitmapFactory.decodeByteArray(blob, 0, blob.length) : null;
                                if (next.mType == 7) {
                                    next.set(decodeByteArray);
                                } else {
                                    ImageScreenElement imageElement = variable.getImageElement(this.mRoot);
                                    if (imageElement != null) {
                                        imageElement.setBitmap(decodeByteArray);
                                    }
                                }
                            } else if (i == 8) {
                                ArrayList arrayList = new ArrayList();
                                do {
                                    arrayList.add(Double.valueOf(cursor.getDouble(columnIndexOrThrow)));
                                } while (cursor.moveToNext());
                                next.set(arrayList.toArray());
                            } else if (i != 9) {
                                if (i == 3) {
                                    d = cursor.getInt(columnIndexOrThrow);
                                } else if (i == 4) {
                                    d = cursor.getLong(columnIndexOrThrow);
                                } else if (i == 5) {
                                    d = cursor.getFloat(columnIndexOrThrow);
                                } else if (i != 6) {
                                    Log.w("ContentProviderBinder", "invalide type" + next.mTypeStr);
                                } else {
                                    d = cursor.getDouble(columnIndexOrThrow);
                                }
                                next.set(d);
                            } else {
                                ArrayList arrayList2 = new ArrayList();
                                do {
                                    arrayList2.add(cursor.getString(columnIndexOrThrow));
                                } while (cursor.moveToNext());
                                next.set(arrayList2.toArray());
                            }
                        }
                    } catch (NumberFormatException unused) {
                        Log.w("ContentProviderBinder", String.format("failed to get value from cursor", new Object[0]));
                    } catch (IllegalArgumentException unused2) {
                        Log.e("ContentProviderBinder", "column does not exist: " + variable.mColumn);
                    } catch (Exception e) {
                        Log.e("ContentProviderBinder", e.toString());
                    }
                }
            }
        }
    }

    @Override // com.miui.maml.data.VariableBinder
    public void finish() {
        synchronized (this.mLock) {
            this.mAllowReg = false;
        }
        this.mLastUri = null;
        registerObserver(null, false);
        this.mHandler.removeCallbacks(this.mUpdater);
        setBlockedColumns(null);
        super.finish();
    }

    public final String getUriText() {
        return this.mUriFormatter.getText();
    }

    public void onContentChanged() {
        Log.i("ContentProviderBinder", "ChangeObserver: content changed.");
        if (this.mFinished) {
            return;
        }
        if (!this.mPaused || this.mAwareChangeWhilePause) {
            startQuery();
        } else {
            this.mNeedsRequery = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.data.VariableBinder
    public Variable onLoadVariable(Element element) {
        return new Variable(element, getContext().mVariables);
    }

    @Override // com.miui.maml.data.VariableBinder
    public void pause() {
        super.pause();
        this.mHandler.removeCallbacks(this.mUpdater);
    }

    @Override // com.miui.maml.data.VariableBinder
    public void refresh() {
        super.refresh();
        startQuery();
    }

    @Override // com.miui.maml.data.VariableBinder
    public void resume() {
        super.resume();
        if (this.mNeedsRequery) {
            startQuery();
        } else {
            checkUpdate();
        }
    }

    public final void setBlockedColumns(String[] strArr) {
        HashSet hashSet;
        if (strArr != null) {
            hashSet = new HashSet();
            for (String str : strArr) {
                hashSet.add(str);
            }
        } else {
            hashSet = null;
        }
        Iterator<VariableBinder.Variable> it = this.mVariables.iterator();
        while (it.hasNext()) {
            Variable variable = (Variable) it.next();
            variable.mBlocked = hashSet != null ? hashSet.contains(variable.mColumn) : false;
        }
    }

    @Override // com.miui.maml.data.VariableBinder
    public void startQuery() {
        if (this.mFinished) {
            return;
        }
        String uriText = getUriText();
        if (uriText == null) {
            Log.e("ContentProviderBinder", "start query: uri null");
            return;
        }
        if (!this.mSystemBootCompleted) {
            boolean equals = "1".equals(SystemProperties.get("sys.boot_completed"));
            this.mSystemBootCompleted = equals;
            if (!equals) {
                return;
            }
        }
        this.mNeedsRequery = false;
        this.mQueryHandler.cancelOperation(100);
        Uri parse = Uri.parse(uriText);
        if (parse == null) {
            return;
        }
        if (this.mUpdateInterval == -1 && !parse.equals(this.mLastUri)) {
            registerObserver(parse, true);
            this.mLastUri = parse;
        }
        this.mQueryHandler.startQuery(100, null, parse, this.mColumns, this.mWhereFormatter.getText(), this.mArgs, this.mOrder);
        this.mLastQueryTime = System.currentTimeMillis();
        checkUpdate();
    }
}
