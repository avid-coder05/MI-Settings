package com.miui.maml.elements;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.ContextVariables;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.elements.VariableArrayElement;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class ListScreenElement extends ElementGroup {
    private static double ACC = -800.0d;
    private static String DATA_TYPE_BITMAP = "bitmap";
    private static String DATA_TYPE_DOUBLE = "double";
    private static String DATA_TYPE_FLOAT = "float";
    private static String DATA_TYPE_INTEGER = "int";
    private static String DATA_TYPE_INTEGER1 = "integer";
    private static String DATA_TYPE_LONG = "long";
    private static String DATA_TYPE_STRING = "string";
    protected AttrDataBinders mAttrDataBinders;
    private int mBottomIndex;
    private int mCachedItemCount;
    private boolean mClearOnFinish;
    private ArrayList<ColumnInfo> mColumnsInfo;
    private int mCurrentIndex;
    private long mCurrentTime;
    private ArrayList<DataIndexMap> mDataList;
    private ArrayList<Integer> mIndexOrder;
    private IndexedVariable[] mIndexedVariables;
    private ElementGroup mInnerGroup;
    private boolean mIsChildScroll;
    private boolean mIsScroll;
    private boolean mIsUpDirection;
    private ListItemElement mItem;
    private int mItemCount;
    private long mLastTime;
    protected ListData mListData;
    private final Object mLock;
    private Expression mMaxHeight;
    private boolean mMoving;
    private double mOffsetX;
    private double mOffsetY;
    private boolean mPressed;
    private ArrayList<Integer> mReuseIndex;
    private AnimatedScreenElement mScrollBar;
    private int mSelectedId;
    private IndexedVariable mSelectedIdVar;
    private double mSpeed;
    private long mStartAnimTime;
    private float mStartAnimY;
    private int mTopIndex;
    private double mTouchStartX;
    private double mTouchStartY;
    private int mVisibleItemCount;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.elements.ListScreenElement$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type;

        static {
            int[] iArr = new int[ColumnInfo.Type.values().length];
            $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type = iArr;
            try {
                iArr[ColumnInfo.Type.STRING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[ColumnInfo.Type.BITMAP.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[ColumnInfo.Type.INTEGER.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[ColumnInfo.Type.DOUBLE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[ColumnInfo.Type.LONG.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[ColumnInfo.Type.FLOAT.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class Column {
        public ListScreenElement mList;
        public String mName;
        public VariableArrayElement.VarObserver mObserver;
        public ScreenElementRoot mRoot;
        public String mTarget;
        public VariableArrayElement mTargetElement;

        public Column(Element element, ScreenElementRoot screenElementRoot, ListScreenElement listScreenElement) {
            this.mRoot = screenElementRoot;
            this.mList = listScreenElement;
            if (element != null) {
                load(element);
            }
        }

        private void load(Element element) {
            this.mName = element.getAttribute("name");
            this.mTarget = element.getAttribute("target");
            this.mObserver = new VariableArrayElement.VarObserver() { // from class: com.miui.maml.elements.ListScreenElement.Column.1
                @Override // com.miui.maml.elements.VariableArrayElement.VarObserver
                public void onDataChange(Object[] objArr) {
                    Column column = Column.this;
                    column.mList.addColumn(column.mName, objArr);
                }
            };
        }

        public void finish() {
            VariableArrayElement variableArrayElement = this.mTargetElement;
            if (variableArrayElement != null) {
                variableArrayElement.registerVarObserver(this.mObserver, false);
            }
        }

        public void init() {
            if (this.mTargetElement == null) {
                ScreenElement findElement = this.mRoot.findElement(this.mTarget);
                if (!(findElement instanceof VariableArrayElement)) {
                    Log.e("ListScreenElement", "can't find VarArray:" + this.mTarget);
                    return;
                }
                this.mTargetElement = (VariableArrayElement) findElement;
            }
            this.mTargetElement.registerVarObserver(this.mObserver, true);
        }
    }

    /* loaded from: classes2.dex */
    public static class ColumnInfo {
        public Type mType;
        public String mVarName;

        /* loaded from: classes2.dex */
        public enum Type {
            STRING,
            BITMAP,
            INTEGER,
            DOUBLE,
            LONG,
            FLOAT;

            public boolean isNumber() {
                return this == INTEGER || this == DOUBLE || this == LONG || this == FLOAT;
            }
        }

        public ColumnInfo(String str) {
            int indexOf = str.indexOf(":");
            if (indexOf == -1) {
                throw new IllegalArgumentException("List: invalid item data " + str);
            }
            this.mVarName = str.substring(0, indexOf);
            String substring = str.substring(indexOf + 1);
            if (ListScreenElement.DATA_TYPE_STRING.equals(substring)) {
                this.mType = Type.STRING;
            } else if (ListScreenElement.DATA_TYPE_BITMAP.equals(substring)) {
                this.mType = Type.BITMAP;
            } else if (ListScreenElement.DATA_TYPE_INTEGER.equals(substring) || ListScreenElement.DATA_TYPE_INTEGER1.equals(substring)) {
                this.mType = Type.INTEGER;
            } else if (ListScreenElement.DATA_TYPE_DOUBLE.equals(substring)) {
                this.mType = Type.DOUBLE;
            } else if (ListScreenElement.DATA_TYPE_LONG.equals(substring)) {
                this.mType = Type.LONG;
            } else if (ListScreenElement.DATA_TYPE_FLOAT.equals(substring)) {
                this.mType = Type.FLOAT;
            } else {
                throw new IllegalArgumentException("List: invalid item data type:" + substring);
            }
        }

        public static ArrayList<ColumnInfo> createColumnsInfo(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            ArrayList<ColumnInfo> arrayList = new ArrayList<>();
            for (String str2 : str.split(",")) {
                arrayList.add(new ColumnInfo(str2));
            }
            return arrayList;
        }

        public boolean validate(Object obj) {
            if (obj == null) {
                return true;
            }
            switch (AnonymousClass1.$SwitchMap$com$miui$maml$elements$ListScreenElement$ColumnInfo$Type[this.mType.ordinal()]) {
                case 1:
                    return obj instanceof String;
                case 2:
                    return obj instanceof Bitmap;
                case 3:
                    return obj instanceof Integer;
                case 4:
                    return obj instanceof Double;
                case 5:
                    return obj instanceof Long;
                case 6:
                    return obj instanceof Float;
                default:
                    return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class DataIndexMap {
        public Object[] mData;
        public int mElementIndex;
        public boolean mNeedRebind;

        public DataIndexMap(Object[] objArr) {
            this(objArr, -1);
        }

        public DataIndexMap(Object[] objArr, int i) {
            this.mElementIndex = -1;
            this.mData = objArr;
            this.mElementIndex = i;
        }

        public void setData(int i, Object obj) {
            Object[] objArr = this.mData;
            if (objArr == null || objArr.length <= i) {
                return;
            }
            objArr[i] = obj;
            this.mNeedRebind = true;
        }
    }

    /* loaded from: classes2.dex */
    public static class ListData {
        public ArrayList<Column> mColumns = new ArrayList<>();
        public ListScreenElement mList;
        public ScreenElementRoot mRoot;

        public ListData(Element element, ScreenElementRoot screenElementRoot, ListScreenElement listScreenElement) {
            this.mRoot = screenElementRoot;
            this.mList = listScreenElement;
            if (element != null) {
                load(element);
            }
        }

        private void load(Element element) {
            Utils.traverseXmlElementChildren(element, "Column", new Utils.XmlTraverseListener() { // from class: com.miui.maml.elements.ListScreenElement.ListData.1
                @Override // com.miui.maml.util.Utils.XmlTraverseListener
                public void onChild(Element element2) {
                    ListData listData = ListData.this;
                    listData.mColumns.add(new Column(element2, listData.mRoot, listData.mList));
                }
            });
        }

        public void finish() {
            Iterator<Column> it = this.mColumns.iterator();
            while (it.hasNext()) {
                Column next = it.next();
                if (next != null) {
                    next.finish();
                }
            }
        }

        public void init() {
            Iterator<Column> it = this.mColumns.iterator();
            while (it.hasNext()) {
                Column next = it.next();
                if (next != null) {
                    next.init();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ListItemElement extends ElementGroup {
        private int mDataIndex;
        private AnimatedScreenElement mDivider;
        protected Element mNode;

        public ListItemElement(Element element, ScreenElementRoot screenElementRoot) {
            super(element, screenElementRoot);
            this.mDataIndex = -1;
            this.mNode = element;
            ScreenElement findElement = findElement("divider");
            if (findElement instanceof AnimatedScreenElement) {
                this.mDivider = (AnimatedScreenElement) findElement;
                removeElement(findElement);
                addElement(this.mDivider);
            }
            this.mAlignV = ScreenElement.AlignV.TOP;
        }

        public int getDataIndex() {
            return this.mDataIndex;
        }

        public void setDataIndex(int i) {
            this.mDataIndex = i;
            AnimatedScreenElement animatedScreenElement = this.mDivider;
            if (animatedScreenElement != null) {
                if (i <= 0) {
                    animatedScreenElement.show(false);
                } else {
                    animatedScreenElement.show(true);
                }
            }
        }
    }

    public ListScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mLock = new Object();
        this.mDataList = new ArrayList<>();
        this.mIndexOrder = new ArrayList<>();
        this.mReuseIndex = new ArrayList<>();
        this.mCurrentIndex = -1;
        if (this.mItem == null) {
            Log.e("ListScreenElement", "no item");
            throw new IllegalArgumentException("List: no item");
        }
        setClip(true);
        this.mMaxHeight = Expression.build(getVariables(), element.getAttribute("maxHeight"));
        this.mClearOnFinish = Boolean.parseBoolean(element.getAttribute("clearOnFinish"));
        String attribute = element.getAttribute("data");
        if (TextUtils.isEmpty(attribute)) {
            Log.e("ListScreenElement", "no data");
            throw new IllegalArgumentException("List: no data");
        }
        ArrayList<ColumnInfo> createColumnsInfo = ColumnInfo.createColumnsInfo(attribute);
        this.mColumnsInfo = createColumnsInfo;
        if (createColumnsInfo == null) {
            Log.e("ListScreenElement", "invalid item data");
            throw new IllegalArgumentException("List: invalid item data");
        }
        this.mIndexedVariables = new IndexedVariable[createColumnsInfo.size()];
        Element child = Utils.getChild(element, "AttrDataBinders");
        if (child == null) {
            Log.e("ListScreenElement", "no attr data binder");
            throw new IllegalArgumentException("List: no attr data binder");
        }
        this.mAttrDataBinders = new AttrDataBinders(child, this.mRoot.getContext().mContextVariables);
        Element child2 = Utils.getChild(element, "Data");
        if (child2 != null) {
            this.mListData = new ListData(child2, this.mRoot, this);
        }
        ScreenElement findElement = findElement("scrollbar");
        if (findElement instanceof AnimatedScreenElement) {
            AnimatedScreenElement animatedScreenElement = (AnimatedScreenElement) findElement;
            this.mScrollBar = animatedScreenElement;
            animatedScreenElement.mAlignV = ScreenElement.AlignV.TOP;
            removeElement(findElement);
            addElement(this.mScrollBar);
        }
        this.mSelectedIdVar = new IndexedVariable(this.mName + ".selectedId", this.mRoot.getContext().mVariables, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addColumn(String str, Object[] objArr) {
        if (str == null || objArr == null) {
            return;
        }
        int i = -1;
        int size = this.mColumnsInfo.size();
        int i2 = 0;
        int i3 = 0;
        while (true) {
            if (i3 >= size) {
                break;
            } else if (str.equals(this.mColumnsInfo.get(i3).mVarName)) {
                i = i3;
                break;
            } else {
                i3++;
            }
        }
        if (i < 0) {
            return;
        }
        synchronized (this.mLock) {
            int length = objArr.length;
            int size2 = this.mDataList.size();
            while (i2 < size2) {
                this.mDataList.get(i2).setData(i, i2 < length ? objArr[i2] : null);
                if (this.mDataList.get(i2).mElementIndex >= 0) {
                    getItem(i2);
                }
                i2++;
            }
            while (size2 < length) {
                Object[] objArr2 = new Object[this.mColumnsInfo.size()];
                objArr2[i] = objArr[size2];
                addItem(objArr2);
                size2++;
            }
            clearEmptyRowLocked();
        }
        requestUpdate();
    }

    private void bindDataLocked(ListItemElement listItemElement, int i, int i2) {
        if (i2 < 0 || i2 >= this.mItemCount) {
            Log.e("ListScreenElement", "invalid item data");
            return;
        }
        Object[] objArr = this.mDataList.get(i2).mData;
        listItemElement.setDataIndex(i2);
        this.mDataList.get(i2).mElementIndex = i;
        this.mDataList.get(i2).mNeedRebind = false;
        listItemElement.setY(i2 * this.mItem.getHeight());
        int size = this.mColumnsInfo.size();
        ContextVariables contextVariables = getContext().mContextVariables;
        for (int i3 = 0; i3 < size; i3++) {
            contextVariables.setVar(this.mColumnsInfo.get(i3).mVarName, objArr[i3]);
        }
        AttrDataBinders attrDataBinders = this.mAttrDataBinders;
        if (attrDataBinders != null) {
            attrDataBinders.bind(listItemElement);
        }
    }

    private void checkVisibility() {
        synchronized (this.mLock) {
            ArrayList<ScreenElement> elements = this.mInnerGroup.getElements();
            for (int i = 0; i < elements.size(); i++) {
                ListItemElement listItemElement = (ListItemElement) elements.get(i);
                int dataIndex = listItemElement.getDataIndex();
                if (dataIndex < 0 || dataIndex < this.mTopIndex || dataIndex > this.mBottomIndex) {
                    if (listItemElement.isVisible()) {
                        listItemElement.show(false);
                    }
                } else if (!listItemElement.isVisible()) {
                    listItemElement.show(true);
                }
            }
        }
    }

    private void clearEmptyRowLocked() {
        for (int size = this.mDataList.size() - 1; size >= 0; size--) {
            Object[] objArr = this.mDataList.get(size).mData;
            int length = objArr.length;
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = true;
                    break;
                } else if (objArr[i] != null) {
                    break;
                } else {
                    i++;
                }
            }
            if (!z) {
                return;
            }
            removeItemLocked(size);
        }
    }

    private ListItemElement getItem(int i) {
        synchronized (this.mLock) {
            ListItemElement listItemElement = null;
            if (i >= 0) {
                if (i < this.mItemCount) {
                    int i2 = this.mDataList.get(i).mElementIndex;
                    if (i2 >= 0 && i2 < this.mInnerGroup.getElements().size()) {
                        listItemElement = (ListItemElement) this.mInnerGroup.getElements().get(i2);
                    }
                    if (i2 < 0 || (listItemElement != null && listItemElement.getDataIndex() != i)) {
                        i2 = getUseableElementIndex();
                        listItemElement = (ListItemElement) this.mInnerGroup.getElements().get(i2);
                        if (listItemElement != null && listItemElement.getDataIndex() < 0) {
                            listItemElement.reset();
                        }
                    }
                    if (listItemElement != null && (listItemElement.getDataIndex() != i || this.mDataList.get(i).mNeedRebind)) {
                        bindDataLocked(listItemElement, i2, i);
                    }
                    return listItemElement;
                }
            }
            return null;
        }
    }

    private int getUseableElementIndex() {
        int intValue;
        if (this.mReuseIndex.size() > 0) {
            intValue = this.mReuseIndex.remove(0).intValue();
        } else if (this.mIsUpDirection) {
            intValue = this.mIndexOrder.remove(0).intValue();
        } else {
            intValue = this.mIndexOrder.remove(r0.size() - 1).intValue();
        }
        if (this.mIsUpDirection) {
            this.mIndexOrder.add(Integer.valueOf(intValue));
        } else {
            this.mIndexOrder.add(0, Integer.valueOf(intValue));
        }
        return intValue;
    }

    private void moveTo(double d) {
        if (d < getHeight() - (this.mItemCount * this.mItem.getHeight())) {
            d = getHeight() - (this.mItemCount * this.mItem.getHeight());
            this.mStartAnimTime = 0L;
        }
        if (d > 0.0d) {
            this.mStartAnimTime = 0L;
            d = 0.0d;
        }
        this.mInnerGroup.setY((float) d);
        this.mTopIndex = Math.min((int) Math.floor((-d) / this.mItem.getHeight()), (this.mItemCount - ((int) (getHeight() / this.mItem.getHeight()))) - 1);
        this.mBottomIndex = Math.min(((int) (getHeight() / this.mItem.getHeight())) + this.mTopIndex, this.mItemCount - 1);
        for (int i = this.mTopIndex; i <= this.mBottomIndex; i++) {
            getItem(i);
        }
        checkVisibility();
        updateScorllBar();
    }

    private void removeItemLocked(int i) {
        if (i < 0 || i >= this.mItemCount) {
            return;
        }
        this.mDataList.remove(i);
        this.mItemCount--;
        setActualHeight(descale(getHeight()));
        int size = this.mIndexOrder.size();
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            ListItemElement listItemElement = (ListItemElement) this.mInnerGroup.getElements().get(this.mIndexOrder.get(i3).intValue());
            int dataIndex = listItemElement.getDataIndex();
            if (dataIndex == i) {
                listItemElement.setDataIndex(-1);
                listItemElement.setY(-1.7976931348623157E308d);
                listItemElement.show(false);
                i2 = i3;
            } else if (dataIndex > i) {
                listItemElement.setDataIndex(dataIndex - 1);
                listItemElement.setY(r5 * this.mItem.getHeight());
            }
        }
        if (size > 0) {
            int intValue = this.mIndexOrder.remove(i2).intValue();
            moveTo(this.mInnerGroup.getY());
            this.mReuseIndex.add(Integer.valueOf(intValue));
        }
        requestUpdate();
    }

    private void resetInner() {
        AnimatedScreenElement animatedScreenElement = this.mScrollBar;
        if (animatedScreenElement != null) {
            animatedScreenElement.show(false);
        }
        this.mMoving = false;
        this.mIsScroll = false;
        this.mIsChildScroll = false;
        this.mStartAnimTime = -1L;
        this.mSpeed = 0.0d;
    }

    private void setVariables() {
        int size = this.mColumnsInfo.size();
        for (int i = 0; i < size; i++) {
            ColumnInfo columnInfo = this.mColumnsInfo.get(i);
            if (columnInfo.mType != ColumnInfo.Type.BITMAP) {
                IndexedVariable[] indexedVariableArr = this.mIndexedVariables;
                if (indexedVariableArr[i] == null) {
                    indexedVariableArr[i] = new IndexedVariable(this.mName + "." + columnInfo.mVarName, this.mRoot.getContext().mVariables, columnInfo.mType.isNumber());
                }
                synchronized (this.mLock) {
                    IndexedVariable indexedVariable = this.mIndexedVariables[i];
                    int i2 = this.mSelectedId;
                    indexedVariable.set((i2 < 0 || i2 >= this.mDataList.size()) ? null : this.mDataList.get(this.mSelectedId).mData[i]);
                }
            }
        }
    }

    private void startAnimation() {
        this.mStartAnimTime = SystemClock.elapsedRealtime();
        this.mStartAnimY = this.mInnerGroup.getY();
    }

    private void updateScorllBar() {
        if (this.mScrollBar == null || !this.mIsScroll) {
            return;
        }
        double height = this.mItemCount * this.mItem.getHeight();
        double height2 = getHeight();
        double d = height2 / height;
        boolean z = true;
        if (d >= 1.0d) {
            d = 0.0d;
            z = false;
        }
        double y = this.mInnerGroup.getY() / (height2 - height);
        if (y > 1.0d) {
            y = 1.0d;
        }
        this.mScrollBar.setY((float) ((1.0d - d) * height2 * y));
        this.mScrollBar.setHeight((float) (height2 * d));
        if (this.mScrollBar.isVisible() != z) {
            this.mScrollBar.show(z);
        }
    }

    public void addItem(Object... objArr) {
        if (objArr == null) {
            return;
        }
        if (objArr.length != this.mColumnsInfo.size()) {
            Log.e("ListScreenElement", "invalid item data count");
            return;
        }
        int length = objArr.length;
        for (int i = 0; i < length; i++) {
            if (!this.mColumnsInfo.get(i).validate(objArr[i])) {
                Log.e("ListScreenElement", "invalid item data type: " + objArr[i]);
                return;
            }
        }
        synchronized (this.mLock) {
            this.mDataList.add(new DataIndexMap((Object[]) objArr.clone()));
            this.mCurrentIndex++;
            this.mItemCount++;
            setActualHeight(descale(getHeight()));
            int max = (int) (Math.max(super.getHeight(), scale(evaluate(this.mMaxHeight))) / this.mItem.getHeight());
            this.mVisibleItemCount = max;
            this.mCachedItemCount = max * 2;
            int size = this.mInnerGroup.getElements().size();
            if (size < this.mCachedItemCount) {
                ListItemElement listItemElement = this.mItem;
                ListItemElement listItemElement2 = new ListItemElement(listItemElement.mNode, listItemElement.mRoot);
                this.mInnerGroup.addElement(listItemElement2);
                this.mDataList.get(this.mCurrentIndex).mElementIndex = size;
                this.mSelectedId = this.mCurrentIndex;
                listItemElement2.init();
                this.mSelectedId = -1;
                bindDataLocked(listItemElement2, size, this.mCurrentIndex);
                this.mIndexOrder.add(Integer.valueOf(this.mCurrentIndex));
            }
        }
        requestUpdate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        super.doTick(j);
        long j2 = this.mStartAnimTime;
        if (j2 < 0 || this.mPressed) {
            return;
        }
        long j3 = j - j2;
        if (j2 != 0) {
            double d = this.mSpeed;
            double d2 = ACC;
            double d3 = j3;
            if (((d2 * d3) / 1000.0d) + d >= 0.0d) {
                double d4 = ((d * d3) / 1000.0d) + ((((d2 * 0.5d) * d3) * d3) / 1000000.0d);
                this.mOffsetY = d4;
                double d5 = this.mStartAnimY;
                if (this.mIsUpDirection) {
                    d4 = -d4;
                }
                moveTo(d5 + d4);
                requestUpdate();
            }
        }
        resetInner();
        requestUpdate();
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.ScreenElement
    public ScreenElement findElement(String str) {
        int i;
        ScreenElement findElement;
        synchronized (this.mLock) {
            int i2 = this.mSelectedId;
            return (i2 < 0 || i2 >= this.mItemCount || (i = this.mDataList.get(i2).mElementIndex) < 0 || (findElement = ((ListItemElement) this.mInnerGroup.getElements().get(i)).findElement(str)) == null) ? super.findElement(str) : findElement;
        }
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        if (this.mClearOnFinish) {
            removeAllItems();
        }
        ListData listData = this.mListData;
        if (listData != null) {
            listData.finish();
        }
    }

    public ArrayList<ColumnInfo> getColumnsInfo() {
        return this.mColumnsInfo;
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement
    public float getHeight() {
        return this.mMaxHeight == null ? super.getHeight() : Math.min(this.mItemCount * this.mItem.getHeight(), scale(evaluate(this.mMaxHeight)));
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        resetInner();
        this.mInnerGroup.setY(0.0d);
        setActualHeight(descale(getHeight()));
        this.mSelectedId = -1;
        this.mSelectedIdVar.set(-1);
        setVariables();
        ListData listData = this.mListData;
        if (listData != null) {
            listData.init();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ElementGroup
    public ScreenElement onCreateChild(Element element) {
        if (element.getTagName().equalsIgnoreCase("Item") && this.mInnerGroup == null) {
            this.mItem = new ListItemElement(element, this.mRoot);
            ElementGroup elementGroup = new ElementGroup((Element) null, this.mRoot);
            this.mInnerGroup = elementGroup;
            return elementGroup;
        }
        return super.onCreateChild(element);
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public boolean onTouch(MotionEvent motionEvent) {
        boolean z;
        boolean z2;
        if (isVisible()) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 0) {
                if (actionMasked == 1) {
                    z = false;
                    this.mPressed = false;
                    if (this.mMoving) {
                        Log.i("ListScreenElement", "unlock touch up");
                        performAction("up");
                        onActionUp();
                        if (this.mSpeed < 400.0d) {
                            resetInner();
                        } else {
                            startAnimation();
                        }
                        z2 = true;
                    }
                } else if (actionMasked != 2) {
                    if (actionMasked == 3) {
                        this.mPressed = false;
                        if (this.mMoving) {
                            performAction("cancel");
                            resetInner();
                            this.mStartAnimTime = -1L;
                            z = false;
                            z2 = true;
                        }
                    }
                    z = false;
                } else if (this.mMoving) {
                    this.mCurrentTime = SystemClock.elapsedRealtime();
                    double d = y;
                    double d2 = d - this.mTouchStartY;
                    this.mOffsetY = d2;
                    this.mOffsetX = x - this.mTouchStartX;
                    if (!this.mIsScroll && !this.mIsChildScroll) {
                        double abs = Math.abs(d2);
                        double abs2 = Math.abs(this.mOffsetX);
                        if (abs > 5.0d && !this.mIsChildScroll && abs >= abs2) {
                            this.mIsScroll = true;
                        } else if (abs2 > 5.0d && !this.mIsScroll && abs < abs2) {
                            this.mIsChildScroll = true;
                        }
                    }
                    this.mIsUpDirection = this.mOffsetY < 0.0d || this.mIsChildScroll;
                    if (this.mIsScroll) {
                        motionEvent.setAction(3);
                        performAction("move");
                        onActionMove(x, y);
                        this.mSpeed = (Math.abs(this.mOffsetY) / (this.mCurrentTime - this.mLastTime)) * 1000.0d;
                        moveTo(this.mInnerGroup.getY() + this.mOffsetY);
                        this.mTouchStartY = d;
                        this.mLastTime = this.mCurrentTime;
                    }
                    z2 = true;
                    z = false;
                } else {
                    z = false;
                }
                z2 = z;
            } else {
                z = false;
                if (touched(x, y)) {
                    this.mMoving = true;
                    this.mPressed = true;
                    performAction("down");
                    onActionDown(x, y);
                    this.mStartAnimTime = -1L;
                    this.mSpeed = 0.0d;
                    this.mLastTime = SystemClock.elapsedRealtime();
                    int floor = (int) Math.floor((y - this.mInnerGroup.getAbsoluteTop()) / this.mItem.getHeight());
                    this.mSelectedId = floor;
                    this.mSelectedIdVar.set(floor);
                    setVariables();
                    this.mTouchStartX = x;
                    this.mTouchStartY = y;
                    updateScorllBar();
                    z2 = true;
                }
                z2 = z;
            }
            if (super.onTouch(motionEvent) || (z2 && this.mInterceptTouch)) {
                return true;
            }
            return z;
        }
        return false;
    }

    public void removeAllItems() {
        synchronized (this.mLock) {
            this.mInnerGroup.removeAllElements();
            this.mInnerGroup.setY(0.0d);
            this.mDataList.clear();
            this.mIndexOrder.clear();
            this.mReuseIndex.clear();
            this.mCurrentIndex = -1;
            this.mItemCount = 0;
            setActualHeight(descale(getHeight()));
        }
    }
}
