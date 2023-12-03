package com.android.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.IntArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.widget.ExploreByTouchHelper;
import com.android.internal.widget.LockPatternView;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import miui.provider.ExtraCalendarContracts;

/* loaded from: classes.dex */
public class LockPatternView extends View {
    private long mAnimatingPeriodStart;
    private final Matrix mArrowMatrix;
    protected int mAspect;
    protected Bitmap mBitmapBtnRed;
    protected Bitmap mBitmapBtnTouched;
    protected int mBitmapHeight;
    protected int mBitmapWidth;
    protected final Matrix mCircleMatrix;
    private final Path mCurrentPath;
    private float mDiameterFactor;
    private int mDistancePointsHeight;
    private int mDistancePointsWidth;
    private boolean mDrawingProfilingStarted;
    private boolean mEnableHapticFeedback;
    private PatternExploreByTouchHelper mExploreByTouchHelper;
    private float mHitFactor;
    private float mInProgressX;
    private float mInProgressY;
    protected boolean mInStealthMode;
    private boolean mInputEnabled;
    private final Rect mInvalidate;
    private OnPatternListener mOnPatternListener;
    protected Paint mPaint;
    private Paint mPathPaint;
    private ArrayList<LockPatternView.Cell> mPattern;
    protected DisplayMode mPatternDisplayMode;
    private boolean[][] mPatternDrawLookup;
    protected boolean mPatternInProgress;
    protected float mSquareHeight;
    protected float mSquareWidth;
    private int mStrokeAlpha;
    private Paint mWrongPathPaint;

    /* loaded from: classes.dex */
    public enum DisplayMode {
        Correct,
        Animate,
        Wrong
    }

    /* loaded from: classes.dex */
    public interface OnPatternListener {
        void onPatternCellAdded(List<LockPatternView.Cell> list);

        void onPatternCleared();

        void onPatternDetected(List<LockPatternView.Cell> list);

        void onPatternStart();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class PatternExploreByTouchHelper extends ExploreByTouchHelper {
        private final SparseArray<VirtualViewContainer> mItems;
        private Rect mTempRect;

        /* loaded from: classes.dex */
        class VirtualViewContainer {
            CharSequence description;

            public VirtualViewContainer(CharSequence charSequence) {
                this.description = charSequence;
            }
        }

        public PatternExploreByTouchHelper(View view) {
            super(view);
            this.mTempRect = new Rect();
            this.mItems = new SparseArray<>();
            for (int i = 1; i < 10; i++) {
                this.mItems.put(i, new VirtualViewContainer(getTextForVirtualView(i)));
            }
        }

        private Rect getBoundsForVirtualView(int i) {
            int i2 = i - 1;
            Rect rect = this.mTempRect;
            int i3 = i2 / 3;
            float centerXForColumn = LockPatternView.this.getCenterXForColumn(i2 % 3);
            float centerYForRow = LockPatternView.this.getCenterYForRow(i3);
            LockPatternView lockPatternView = LockPatternView.this;
            float f = lockPatternView.mSquareHeight * lockPatternView.mHitFactor * 0.5f;
            LockPatternView lockPatternView2 = LockPatternView.this;
            float f2 = lockPatternView2.mSquareWidth * lockPatternView2.mHitFactor * 0.5f;
            rect.left = (int) (centerXForColumn - f2);
            rect.right = (int) (centerXForColumn + f2);
            rect.top = (int) (centerYForRow - f);
            rect.bottom = (int) (centerYForRow + f);
            return rect;
        }

        private CharSequence getTextForVirtualView(int i) {
            return LockPatternView.this.getResources().getString(17040583, Integer.valueOf(i));
        }

        private int getVirtualViewIdForHit(float f, float f2) {
            int columnHit;
            int rowHit = LockPatternView.this.getRowHit(f2);
            if (rowHit >= 0 && (columnHit = LockPatternView.this.getColumnHit(f)) >= 0) {
                boolean z = LockPatternView.this.mPatternDrawLookup[rowHit][columnHit];
                int i = (rowHit * 3) + columnHit + 1;
                if (z) {
                    return i;
                }
                return Integer.MIN_VALUE;
            }
            return Integer.MIN_VALUE;
        }

        private boolean isClickable(int i) {
            if (i != Integer.MIN_VALUE) {
                int i2 = i - 1;
                return !LockPatternView.this.mPatternDrawLookup[i2 / 3][i2 % 3];
            }
            return false;
        }

        protected int getVirtualViewAt(float f, float f2) {
            return getVirtualViewIdForHit(f, f2);
        }

        protected void getVisibleVirtualViews(IntArray intArray) {
            if (LockPatternView.this.mPatternInProgress) {
                for (int i = 1; i < 10; i++) {
                    intArray.add(i);
                }
            }
        }

        boolean onItemClicked(int i) {
            invalidateVirtualView(i);
            sendEventForVirtualView(i, 1);
            return true;
        }

        protected boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
            if (i2 != 16) {
                return false;
            }
            return onItemClicked(i);
        }

        public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onPopulateAccessibilityEvent(view, accessibilityEvent);
            LockPatternView lockPatternView = LockPatternView.this;
            if (lockPatternView.mPatternInProgress) {
                return;
            }
            accessibilityEvent.setContentDescription(lockPatternView.getContext().getText(17040581));
        }

        protected void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
            VirtualViewContainer virtualViewContainer = this.mItems.get(i);
            if (virtualViewContainer == null) {
                accessibilityEvent.setContentDescription(((View) LockPatternView.this).mContext.getResources().getString(R.string.input_pattern_hint_text));
                return;
            }
            accessibilityEvent.getText().add(virtualViewContainer.description);
            accessibilityEvent.setContentDescription(virtualViewContainer.description);
        }

        protected void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfo accessibilityNodeInfo) {
            accessibilityNodeInfo.setText(getTextForVirtualView(i));
            accessibilityNodeInfo.setContentDescription(getTextForVirtualView(i));
            if (LockPatternView.this.mPatternInProgress) {
                accessibilityNodeInfo.setFocusable(true);
                if (isClickable(i)) {
                    accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                    accessibilityNodeInfo.setClickable(isClickable(i));
                }
            }
            accessibilityNodeInfo.setBoundsInParent(getBoundsForVirtualView(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.settings.LockPatternView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        private final int mDisplayMode;
        private final boolean mInStealthMode;
        private final boolean mInputEnabled;
        private final String mSerializedPattern;
        private final boolean mTactileFeedbackEnabled;

        private SavedState(Parcel parcel) {
            super(parcel);
            this.mSerializedPattern = parcel.readString();
            this.mDisplayMode = parcel.readInt();
            this.mInputEnabled = ((Boolean) parcel.readValue(null)).booleanValue();
            this.mInStealthMode = ((Boolean) parcel.readValue(null)).booleanValue();
            this.mTactileFeedbackEnabled = ((Boolean) parcel.readValue(null)).booleanValue();
        }

        public int getDisplayMode() {
            return this.mDisplayMode;
        }

        public boolean isInStealthMode() {
            return this.mInStealthMode;
        }

        public boolean isInputEnabled() {
            return this.mInputEnabled;
        }

        public boolean isTactileFeedbackEnabled() {
            return this.mTactileFeedbackEnabled;
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeString(this.mSerializedPattern);
            parcel.writeInt(this.mDisplayMode);
            parcel.writeValue(Boolean.valueOf(this.mInputEnabled));
            parcel.writeValue(Boolean.valueOf(this.mInStealthMode));
            parcel.writeValue(Boolean.valueOf(this.mTactileFeedbackEnabled));
        }
    }

    public LockPatternView(Context context) {
        this(context, null);
    }

    public LockPatternView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDrawingProfilingStarted = false;
        this.mPaint = new Paint();
        this.mPathPaint = new Paint();
        this.mWrongPathPaint = new Paint();
        this.mPattern = new ArrayList<>(9);
        this.mPatternDrawLookup = (boolean[][]) Array.newInstance(boolean.class, 3, 3);
        this.mInProgressX = -1.0f;
        this.mInProgressY = -1.0f;
        this.mPatternDisplayMode = DisplayMode.Correct;
        this.mInputEnabled = true;
        this.mInStealthMode = false;
        this.mEnableHapticFeedback = true;
        this.mPatternInProgress = false;
        this.mDiameterFactor = 0.05f;
        this.mStrokeAlpha = 64;
        this.mHitFactor = 0.6f;
        this.mCurrentPath = new Path();
        this.mInvalidate = new Rect();
        this.mArrowMatrix = new Matrix();
        this.mCircleMatrix = new Matrix();
        this.mDistancePointsHeight = 0;
        this.mDistancePointsWidth = 0;
        loadAttrs(context, attributeSet);
        setClickable(true);
        this.mPathPaint.setAntiAlias(true);
        this.mPathPaint.setDither(true);
        this.mPathPaint.setAlpha(this.mStrokeAlpha);
        this.mPathPaint.setStyle(Paint.Style.STROKE);
        this.mPathPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPathPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mWrongPathPaint.setAntiAlias(true);
        this.mWrongPathPaint.setDither(true);
        this.mWrongPathPaint.setAlpha(this.mStrokeAlpha);
        this.mWrongPathPaint.setStyle(Paint.Style.STROKE);
        this.mWrongPathPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mWrongPathPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void addCellToPattern(LockPatternView.Cell cell) {
        this.mPatternDrawLookup[cell.getRow()][cell.getColumn()] = true;
        this.mPattern.add(cell);
        notifyCellAdded();
    }

    private LockPatternView.Cell checkForNewHit(float f, float f2) {
        int columnHit;
        int rowHit = getRowHit(f2);
        if (rowHit >= 0 && (columnHit = getColumnHit(f)) >= 0 && !this.mPatternDrawLookup[rowHit][columnHit]) {
            return LockPatternView.Cell.of(rowHit, columnHit);
        }
        return null;
    }

    private void clearPatternDrawLookup() {
        for (int i = 0; i < 3; i++) {
            for (int i2 = 0; i2 < 3; i2++) {
                this.mPatternDrawLookup[i][i2] = false;
            }
        }
    }

    private LockPatternView.Cell detectAndAddHit(float f, float f2) {
        LockPatternView.Cell checkForNewHit = checkForNewHit(f, f2);
        LockPatternView.Cell cell = null;
        if (checkForNewHit != null) {
            ArrayList<LockPatternView.Cell> arrayList = this.mPattern;
            if (!arrayList.isEmpty()) {
                LockPatternView.Cell cell2 = arrayList.get(arrayList.size() - 1);
                int row = checkForNewHit.getRow() - cell2.getRow();
                int column = checkForNewHit.getColumn() - cell2.getColumn();
                int row2 = cell2.getRow();
                int column2 = cell2.getColumn();
                if (Math.abs(row) == 2 && Math.abs(column) != 1) {
                    row2 = cell2.getRow() + (row > 0 ? 1 : -1);
                }
                if (Math.abs(column) == 2 && Math.abs(row) != 1) {
                    column2 = cell2.getColumn() + (column > 0 ? 1 : -1);
                }
                cell = LockPatternView.Cell.of(row2, column2);
            }
            if (cell != null && !this.mPatternDrawLookup[cell.getRow()][cell.getColumn()]) {
                addCellToPattern(cell);
            }
            addCellToPattern(checkForNewHit);
            if (this.mEnableHapticFeedback) {
                performHapticFeedback(1, 3);
            }
            return checkForNewHit;
        }
        return null;
    }

    private Bitmap getBitmapFor(int i) {
        if (-1 == i) {
            return null;
        }
        return BitmapFactory.decodeResource(getContext().getResources(), i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getCenterXForColumn(int i) {
        float f = ((View) this).mPaddingLeft;
        float f2 = this.mSquareWidth;
        return f + (i * f2) + (f2 / 2.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getCenterYForRow(int i) {
        float f = ((View) this).mPaddingTop;
        float f2 = this.mSquareHeight;
        return f + (i * f2) + (f2 / 2.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getColumnHit(float f) {
        float f2 = this.mSquareWidth;
        float f3 = this.mHitFactor * f2;
        float f4 = ((View) this).mPaddingLeft + ((f2 - f3) / 2.0f);
        for (int i = 0; i < 3; i++) {
            float f5 = (i * f2) + f4;
            if (f >= f5 && f <= f5 + f3) {
                return i;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getRowHit(float f) {
        float f2 = this.mSquareHeight;
        float f3 = this.mHitFactor * f2;
        float f4 = ((View) this).mPaddingTop + ((f2 - f3) / 2.0f);
        for (int i = 0; i < 3; i++) {
            float f5 = (i * f2) + f4;
            if (f >= f5 && f <= f5 + f3) {
                return i;
            }
        }
        return -1;
    }

    private void handleActionDown(MotionEvent motionEvent) {
        resetPattern();
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (detectAndAddHit(x, y) != null) {
            setPatternInProgress(true);
            this.mPatternDisplayMode = DisplayMode.Correct;
            notifyPatternStarted();
            invalidate();
        } else if (this.mPatternInProgress) {
            setPatternInProgress(false);
            notifyPatternCleared();
        }
        this.mInProgressX = x;
        this.mInProgressY = y;
    }

    private void handleActionMove(MotionEvent motionEvent) {
        int historySize = motionEvent.getHistorySize();
        int i = 0;
        while (i < historySize + 1) {
            float historicalX = i < historySize ? motionEvent.getHistoricalX(i) : motionEvent.getX();
            float historicalY = i < historySize ? motionEvent.getHistoricalY(i) : motionEvent.getY();
            LockPatternView.Cell detectAndAddHit = detectAndAddHit(historicalX, historicalY);
            int size = this.mPattern.size();
            if (detectAndAddHit != null && size == 1) {
                setPatternInProgress(true);
                notifyPatternStarted();
            }
            if (Math.abs(historicalX - this.mInProgressX) + Math.abs(historicalY - this.mInProgressY) > this.mSquareWidth * 0.01f) {
                this.mInProgressX = historicalX;
                this.mInProgressY = historicalY;
                invalidate();
            }
            i++;
        }
    }

    private void handleActionUp(MotionEvent motionEvent) {
        if (this.mPattern.isEmpty()) {
            return;
        }
        setPatternInProgress(false);
        notifyPatternDetected();
        invalidate();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v18, types: [com.android.settings.LockPatternView$PatternExploreByTouchHelper, android.view.View$AccessibilityDelegate] */
    private void loadAttrs(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.LockPatternView);
        String string = obtainStyledAttributes.getString(R$styleable.LockPatternView_aspect);
        if ("square".equals(string)) {
            this.mAspect = 0;
        } else if ("lock_width".equals(string)) {
            this.mAspect = 1;
        } else if ("lock_height".equals(string)) {
            this.mAspect = 2;
        } else if ("fixed".equals(string)) {
            this.mAspect = 3;
        } else {
            this.mAspect = 0;
        }
        this.mPathPaint.setColor(obtainStyledAttributes.getColor(R$styleable.LockPatternView_paintColor, -1));
        this.mWrongPathPaint.setColor(obtainStyledAttributes.getColor(R$styleable.LockPatternView_wrongColor, -1));
        this.mDiameterFactor = obtainStyledAttributes.getFloat(R$styleable.LockPatternView_diameterFactor, 0.1f);
        this.mStrokeAlpha = obtainStyledAttributes.getInteger(R$styleable.LockPatternView_pathStrokeAlpha, 128);
        this.mBitmapBtnTouched = getBitmapFor(obtainStyledAttributes.getResourceId(R$styleable.LockPatternView_btnTouchedBmp, -1));
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.LockPatternView_btnRedBmp, -1);
        if (-1 == resourceId) {
            this.mBitmapBtnRed = this.mBitmapBtnTouched;
        } else {
            this.mBitmapBtnRed = getBitmapFor(resourceId);
        }
        Bitmap[] bitmapArr = {this.mBitmapBtnRed, this.mBitmapBtnTouched};
        for (int i = 0; i < 2; i++) {
            Bitmap bitmap = bitmapArr[i];
            if (bitmap != null) {
                this.mBitmapWidth = Math.max(this.mBitmapWidth, bitmap.getWidth());
                this.mBitmapHeight = Math.max(this.mBitmapHeight, bitmap.getHeight());
            }
        }
        ?? patternExploreByTouchHelper = new PatternExploreByTouchHelper(this);
        this.mExploreByTouchHelper = patternExploreByTouchHelper;
        setAccessibilityDelegate(patternExploreByTouchHelper);
        obtainStyledAttributes.recycle();
    }

    private void notifyCellAdded() {
        OnPatternListener onPatternListener = this.mOnPatternListener;
        if (onPatternListener != null) {
            onPatternListener.onPatternCellAdded(this.mPattern);
        }
        this.mExploreByTouchHelper.invalidateRoot();
    }

    private void notifyPatternCleared() {
        sendAccessEvent(286196261);
        OnPatternListener onPatternListener = this.mOnPatternListener;
        if (onPatternListener != null) {
            onPatternListener.onPatternCleared();
        }
    }

    private void notifyPatternDetected() {
        sendAccessEvent(286196262);
        OnPatternListener onPatternListener = this.mOnPatternListener;
        if (onPatternListener != null) {
            onPatternListener.onPatternDetected(this.mPattern);
        }
    }

    private void notifyPatternStarted() {
        sendAccessEvent(286196263);
        OnPatternListener onPatternListener = this.mOnPatternListener;
        if (onPatternListener != null) {
            onPatternListener.onPatternStart();
        }
    }

    private void resetPattern() {
        this.mPattern.clear();
        clearPatternDrawLookup();
        this.mPatternDisplayMode = DisplayMode.Correct;
        invalidate();
    }

    private void sendAccessEvent(int i) {
        announceForAccessibility(((View) this).mContext.getString(i));
    }

    private void setPatternInProgress(boolean z) {
        this.mPatternInProgress = z;
        this.mExploreByTouchHelper.invalidateRoot();
    }

    public void clearPattern() {
        resetPattern();
    }

    public void disableInput() {
        this.mInputEnabled = false;
    }

    @Override // android.view.View
    protected boolean dispatchHoverEvent(MotionEvent motionEvent) {
        return this.mExploreByTouchHelper.dispatchHoverEvent(motionEvent) | super.dispatchHoverEvent(motionEvent);
    }

    protected void drawCircle(Canvas canvas, int i, int i2, boolean z) {
        Bitmap bitmap;
        if (!z || (this.mInStealthMode && this.mPatternDisplayMode != DisplayMode.Wrong)) {
            bitmap = this.mBitmapBtnTouched;
        } else if (this.mPatternInProgress) {
            bitmap = this.mBitmapBtnTouched;
        } else {
            DisplayMode displayMode = this.mPatternDisplayMode;
            if (displayMode == DisplayMode.Wrong) {
                bitmap = this.mBitmapBtnRed;
            } else if (displayMode != DisplayMode.Correct && displayMode != DisplayMode.Animate) {
                throw new IllegalStateException("unknown display mode " + this.mPatternDisplayMode);
            } else {
                bitmap = this.mBitmapBtnTouched;
            }
        }
        int i3 = this.mBitmapWidth;
        int i4 = this.mBitmapHeight;
        float f = this.mSquareWidth;
        int i5 = (int) ((f - i3) / 2.0f);
        int i6 = (int) ((this.mSquareHeight - i4) / 2.0f);
        float min = Math.min(f / i3, 1.0f);
        float min2 = Math.min(this.mSquareHeight / this.mBitmapHeight, 1.0f);
        this.mCircleMatrix.setTranslate(i + i5, i2 + i6);
        this.mCircleMatrix.preTranslate(this.mBitmapWidth / 2, this.mBitmapHeight / 2);
        this.mCircleMatrix.preScale(min, min2);
        this.mCircleMatrix.preTranslate((-this.mBitmapWidth) / 2, (-this.mBitmapHeight) / 2);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, this.mCircleMatrix, this.mPaint);
        }
    }

    public void enableInput() {
        this.mInputEnabled = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int getSuggestedMinimumHeight() {
        return this.mBitmapWidth * 3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int getSuggestedMinimumWidth() {
        return this.mBitmapWidth * 3;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        ArrayList<LockPatternView.Cell> arrayList = this.mPattern;
        int size = arrayList.size();
        boolean[][] zArr = this.mPatternDrawLookup;
        if (this.mPatternDisplayMode == DisplayMode.Animate) {
            int elapsedRealtime = (((int) (SystemClock.elapsedRealtime() - this.mAnimatingPeriodStart)) % ((size + 1) * ExtraCalendarContracts.CALENDAR_ACCESS_LEVEL_LOCAL)) / ExtraCalendarContracts.CALENDAR_ACCESS_LEVEL_LOCAL;
            clearPatternDrawLookup();
            for (int i = 0; i < elapsedRealtime; i++) {
                LockPatternView.Cell cell = arrayList.get(i);
                zArr[cell.getRow()][cell.getColumn()] = true;
            }
            if (elapsedRealtime > 0 && elapsedRealtime < size) {
                float f = (r6 % ExtraCalendarContracts.CALENDAR_ACCESS_LEVEL_LOCAL) / 700.0f;
                LockPatternView.Cell cell2 = arrayList.get(elapsedRealtime - 1);
                float centerXForColumn = getCenterXForColumn(cell2.getColumn());
                float centerYForRow = getCenterYForRow(cell2.getRow());
                LockPatternView.Cell cell3 = arrayList.get(elapsedRealtime);
                float centerXForColumn2 = (getCenterXForColumn(cell3.getColumn()) - centerXForColumn) * f;
                float centerYForRow2 = f * (getCenterYForRow(cell3.getRow()) - centerYForRow);
                this.mInProgressX = centerXForColumn + centerXForColumn2;
                this.mInProgressY = centerYForRow + centerYForRow2;
            }
            invalidate();
        }
        float f2 = this.mSquareWidth;
        float f3 = this.mSquareHeight;
        float f4 = this.mDiameterFactor * f2;
        this.mPathPaint.setStrokeWidth(f4);
        this.mWrongPathPaint.setStrokeWidth(f4);
        Path path = this.mCurrentPath;
        path.rewind();
        int i2 = ((View) this).mPaddingTop;
        int i3 = ((View) this).mPaddingLeft;
        int i4 = 0;
        while (true) {
            if (i4 >= 3) {
                break;
            }
            float f5 = i2 + (i4 * f3);
            int i5 = 0;
            for (int i6 = 3; i5 < i6; i6 = 3) {
                drawCircle(canvas, (int) (i3 + (i5 * f2)), (int) f5, zArr[i4][i5]);
                i5++;
            }
            i4++;
        }
        boolean z = !this.mInStealthMode || this.mPatternDisplayMode == DisplayMode.Wrong;
        boolean z2 = (this.mPaint.getFlags() & 2) != 0;
        this.mPaint.setFilterBitmap(true);
        if (z) {
            int i7 = 0;
            boolean z3 = false;
            while (i7 < size) {
                LockPatternView.Cell cell4 = arrayList.get(i7);
                if (!zArr[cell4.getRow()][cell4.getColumn()]) {
                    break;
                }
                float centerXForColumn3 = getCenterXForColumn(cell4.getColumn());
                float centerYForRow3 = getCenterYForRow(cell4.getRow());
                if (i7 == 0) {
                    path.moveTo(centerXForColumn3, centerYForRow3);
                } else {
                    path.lineTo(centerXForColumn3, centerYForRow3);
                }
                i7++;
                z3 = true;
            }
            if ((this.mPatternInProgress || this.mPatternDisplayMode == DisplayMode.Animate) && z3) {
                path.lineTo(this.mInProgressX, this.mInProgressY);
            }
            if (this.mPatternDisplayMode != DisplayMode.Wrong) {
                canvas.drawPath(path, this.mPathPaint);
            } else {
                canvas.drawPath(path, this.mWrongPathPaint);
            }
        }
        this.mPaint.setFilterBitmap(z2);
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        if (AccessibilityManager.getInstance(((View) this).mContext).isTouchExplorationEnabled()) {
            int action = motionEvent.getAction();
            if (action == 7) {
                motionEvent.setAction(2);
            } else if (action == 9) {
                motionEvent.setAction(0);
            } else if (action == 10) {
                motionEvent.setAction(1);
            }
            onTouchEvent(motionEvent);
            motionEvent.setAction(action);
        }
        return super.onHoverEvent(motionEvent);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int suggestedMinimumWidth = getSuggestedMinimumWidth();
        int suggestedMinimumHeight = getSuggestedMinimumHeight();
        int resolveMeasured = resolveMeasured(i, suggestedMinimumWidth);
        int resolveMeasured2 = resolveMeasured(i2, suggestedMinimumHeight);
        int i3 = this.mAspect;
        if (i3 != 0) {
            if (i3 == 1) {
                resolveMeasured2 = Math.min(resolveMeasured, resolveMeasured2);
            } else if (i3 == 2) {
                resolveMeasured = Math.min(resolveMeasured, resolveMeasured2);
            } else if (i3 == 3) {
                boolean z = this.mDistancePointsWidth == 0;
                resolveMeasured = getResources().getDimensionPixelSize(z ? R.dimen.pattern_settings_lock_pattern_view_size : this.mDistancePointsWidth);
                resolveMeasured2 = getResources().getDimensionPixelSize(z ? R.dimen.pattern_settings_lock_pattern_view_size : this.mDistancePointsHeight);
            }
        } else {
            resolveMeasured = Math.min(resolveMeasured, resolveMeasured2);
            resolveMeasured2 = resolveMeasured;
        }
        setMeasuredDimension(resolveMeasured, resolveMeasured2);
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mPatternDisplayMode = DisplayMode.values()[savedState.getDisplayMode()];
        this.mInputEnabled = savedState.isInputEnabled();
        this.mInStealthMode = savedState.isInStealthMode();
        this.mEnableHapticFeedback = savedState.isTactileFeedbackEnabled();
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        return null;
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        this.mSquareWidth = ((i - ((View) this).mPaddingLeft) - ((View) this).mPaddingRight) / 3.0f;
        this.mSquareHeight = ((i2 - ((View) this).mPaddingTop) - ((View) this).mPaddingBottom) / 3.0f;
        this.mExploreByTouchHelper.invalidateRoot();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mInputEnabled && isEnabled()) {
            int action = motionEvent.getAction();
            if (action == 0) {
                handleActionDown(motionEvent);
                return true;
            } else if (action == 1) {
                handleActionUp(motionEvent);
                return true;
            } else if (action == 2) {
                handleActionMove(motionEvent);
                return true;
            } else if (action != 3) {
                return false;
            } else {
                if (this.mPatternInProgress) {
                    resetPattern();
                    setPatternInProgress(false);
                    notifyPatternCleared();
                }
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int resolveMeasured(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int mode = View.MeasureSpec.getMode(i);
        return mode != Integer.MIN_VALUE ? mode != 0 ? size : i2 : Math.max(size, i2);
    }

    public void setBitmapBtnTouched(int i) {
        this.mBitmapBtnTouched = getBitmapFor(i);
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.mPatternDisplayMode = displayMode;
        if (displayMode == DisplayMode.Animate) {
            if (this.mPattern.size() == 0) {
                throw new IllegalStateException("you must have a pattern to animate if you want to set the display mode to animate");
            }
            this.mAnimatingPeriodStart = SystemClock.elapsedRealtime();
            LockPatternView.Cell cell = this.mPattern.get(0);
            this.mInProgressX = getCenterXForColumn(cell.getColumn());
            this.mInProgressY = getCenterYForRow(cell.getRow());
            clearPatternDrawLookup();
        }
        invalidate();
    }

    public void setInStealthMode(boolean z) {
        this.mInStealthMode = z;
    }

    public void setOnPatternListener(OnPatternListener onPatternListener) {
        this.mOnPatternListener = onPatternListener;
    }

    public void setPattern(DisplayMode displayMode, List<LockPatternView.Cell> list) {
        this.mPattern.clear();
        this.mPattern.addAll(list);
        clearPatternDrawLookup();
        for (LockPatternView.Cell cell : list) {
            this.mPatternDrawLookup[cell.getRow()][cell.getColumn()] = true;
        }
        setDisplayMode(displayMode);
    }

    public void setTactileFeedbackEnabled(boolean z) {
        this.mEnableHapticFeedback = z;
    }
}
