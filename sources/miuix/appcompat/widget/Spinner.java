package miuix.appcompat.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;
import android.widget.ThemedSpinnerAdapter;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.ViewUtils;
import java.lang.reflect.Field;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$id;
import miuix.appcompat.R$layout;
import miuix.appcompat.R$styleable;
import miuix.appcompat.adapter.SpinnerDoubleLineContentAdapter;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.IActivity;
import miuix.appcompat.internal.adapter.SpinnerCheckableArrayAdapter;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.WindowUtils;
import miuix.internal.util.AnimHelper;
import miuix.internal.util.TaggingDrawableUtil;
import miuix.internal.widget.ListPopup;
import miuix.view.CompatViewMethod;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes5.dex */
public class Spinner extends android.widget.Spinner {
    private static Field FORWARDING_LISTENER;
    int mDropDownMaxWidth;
    int mDropDownMinWidth;
    int mDropDownWidth;
    private OnSpinnerDismissListener mOnSpinnerDismissListener;
    private SpinnerPopup mPopup;
    private final Context mPopupContext;
    private final boolean mPopupSet;
    private SpinnerAdapter mTempAdapter;
    final Rect mTempRect;
    private float mXRelative;
    private float mYRelative;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class DialogPopup implements SpinnerPopup, DialogInterface.OnClickListener {
        private ListAdapter mListAdapter;
        AlertDialog mPopup;
        private CharSequence mPrompt;

        private DialogPopup() {
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void dismiss() {
            AlertDialog alertDialog = this.mPopup;
            if (alertDialog != null) {
                alertDialog.dismiss();
                this.mPopup = null;
            }
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public Drawable getBackground() {
            return null;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public CharSequence getHintText() {
            return this.mPrompt;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public int getHorizontalOffset() {
            return 0;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public int getVerticalOffset() {
            return 0;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public boolean isShowing() {
            AlertDialog alertDialog = this.mPopup;
            return alertDialog != null && alertDialog.isShowing();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            Spinner.this.setSelection(i);
            HapticCompat.performHapticFeedback(Spinner.this, HapticFeedbackConstants.MIUI_POPUP_LIGHT);
            if (Spinner.this.getOnItemClickListener() != null) {
                Spinner.this.performItemClick(null, i, this.mListAdapter.getItemId(i));
            }
            dismiss();
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setAdapter(ListAdapter listAdapter) {
            this.mListAdapter = listAdapter;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setBackgroundDrawable(Drawable drawable) {
            Log.e("Spinner", "Cannot set popup background for MODE_DIALOG, ignoring");
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setHorizontalOffset(int i) {
            Log.e("Spinner", "Cannot set horizontal offset for MODE_DIALOG, ignoring");
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setHorizontalOriginalOffset(int i) {
            Log.e("Spinner", "Cannot set horizontal (original) offset for MODE_DIALOG, ignoring");
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setPromptText(CharSequence charSequence) {
            this.mPrompt = charSequence;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setVerticalOffset(int i) {
            Log.e("Spinner", "Cannot set vertical offset for MODE_DIALOG, ignoring");
        }

        public void show(int i, int i2) {
            if (this.mListAdapter == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(Spinner.this.getPopupContext());
            CharSequence charSequence = this.mPrompt;
            if (charSequence != null) {
                builder.setTitle(charSequence);
            }
            AlertDialog create = builder.setSingleChoiceItems(this.mListAdapter, Spinner.this.getSelectedItemPosition(), this).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: miuix.appcompat.widget.Spinner.DialogPopup.1
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    Spinner.this.onSpinnerDismiss();
                }
            }).create();
            this.mPopup = create;
            ListView listView = create.getListView();
            listView.setTextDirection(i);
            listView.setTextAlignment(i2);
            this.mPopup.show();
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void show(int i, int i2, float f, float f2) {
            show(i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class DialogPopupAdapter extends DropDownAdapter {
        DialogPopupAdapter(SpinnerAdapter spinnerAdapter, Resources.Theme theme) {
            super(spinnerAdapter, theme);
        }
    }

    /* loaded from: classes5.dex */
    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;

        public DropDownAdapter(SpinnerAdapter spinnerAdapter, Resources.Theme theme) {
            this.mAdapter = spinnerAdapter;
            if (spinnerAdapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter) spinnerAdapter;
            }
            if (theme != null) {
                if (Build.VERSION.SDK_INT >= 23 && (spinnerAdapter instanceof ThemedSpinnerAdapter)) {
                    ThemedSpinnerAdapter themedSpinnerAdapter = (ThemedSpinnerAdapter) spinnerAdapter;
                    if (themedSpinnerAdapter.getDropDownViewTheme() != theme) {
                        themedSpinnerAdapter.setDropDownViewTheme(theme);
                    }
                } else if (spinnerAdapter instanceof androidx.appcompat.widget.ThemedSpinnerAdapter) {
                    androidx.appcompat.widget.ThemedSpinnerAdapter themedSpinnerAdapter2 = (androidx.appcompat.widget.ThemedSpinnerAdapter) spinnerAdapter;
                    if (themedSpinnerAdapter2.getDropDownViewTheme() == null) {
                        themedSpinnerAdapter2.setDropDownViewTheme(theme);
                    }
                }
            }
        }

        @Override // android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.areAllItemsEnabled();
            }
            return true;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return 0;
            }
            return spinnerAdapter.getCount();
        }

        @Override // android.widget.SpinnerAdapter
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return null;
            }
            return spinnerAdapter.getDropDownView(i, view, viewGroup);
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return null;
            }
            return spinnerAdapter.getItem(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter == null) {
                return -1L;
            }
            return spinnerAdapter.getItemId(i);
        }

        @Override // android.widget.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View dropDownView = getDropDownView(i, view, viewGroup);
            if (view == null) {
                AnimHelper.addPressAnim(dropDownView);
                Folme.useAt(dropDownView).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(dropDownView, new AnimConfig[0]);
            }
            return dropDownView;
        }

        @Override // android.widget.Adapter
        public int getViewTypeCount() {
            return 1;
        }

        @Override // android.widget.Adapter
        public boolean hasStableIds() {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            return spinnerAdapter != null && spinnerAdapter.hasStableIds();
        }

        @Override // android.widget.Adapter
        public boolean isEmpty() {
            return getCount() == 0;
        }

        @Override // android.widget.ListAdapter
        public boolean isEnabled(int i) {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.isEnabled(i);
            }
            return true;
        }

        @Override // android.widget.Adapter
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter != null) {
                spinnerAdapter.registerDataSetObserver(dataSetObserver);
            }
        }

        @Override // android.widget.Adapter
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter != null) {
                spinnerAdapter.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class DropDownPopupAdapter extends DropDownAdapter {
        DropDownPopupAdapter(SpinnerAdapter spinnerAdapter, Resources.Theme theme) {
            super(spinnerAdapter, theme);
        }

        @Override // miuix.appcompat.widget.Spinner.DropDownAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            TaggingDrawableUtil.updateItemBackground(view2, i, getCount());
            return view2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class DropdownPopup extends ListPopup implements SpinnerPopup {
        ListAdapter mAdapter;
        private View mFenceView;
        private int mFenceX;
        private CharSequence mHintText;
        private int mMarginScreen;
        private int mOriginalHorizontalOffset;
        private final Rect mVisibleRect;

        public DropdownPopup(Context context, AttributeSet attributeSet, int i) {
            super(context);
            this.mVisibleRect = new Rect();
            this.mFenceX = -1;
            this.mMarginScreen = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_context_menu_window_margin_screen);
            setDropDownGravity(8388659);
            setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.appcompat.widget.Spinner.DropdownPopup.1
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
                    Spinner.this.setSelection(i2);
                    Spinner.this.vibrate();
                    if (Spinner.this.getOnItemClickListener() != null) {
                        DropdownPopup dropdownPopup = DropdownPopup.this;
                        Spinner.this.performItemClick(view, i2, dropdownPopup.mAdapter.getItemId(i2));
                    }
                    DropdownPopup.this.dismiss();
                }
            });
        }

        private void checkInFloatingWindowMode() {
            if (this.mFenceView != null) {
                return;
            }
            Spinner spinner = Spinner.this;
            if ((spinner.getContext() instanceof IActivity) && ((IActivity) spinner.getContext()).isInFloatingWindowMode()) {
                setFenceView(spinner.getRootView().findViewById(R$id.action_bar_overlay_layout));
            }
        }

        private int getListViewHeight() {
            if (getListView() == null) {
                this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
                return this.mContentView.getMeasuredHeight() + 0;
            }
            ListAdapter adapter = getListView().getAdapter();
            int i = 0;
            for (int i2 = 0; i2 < adapter.getCount(); i2++) {
                View view = adapter.getView(i2, null, getListView());
                view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
                i += view.getMeasuredHeight();
            }
            return i;
        }

        private int getxInWindow(int i, int i2, View view) {
            boolean z = i2 <= view.getWidth() / 2;
            int i3 = this.mFenceX;
            if (i3 != -1) {
                i2 = i3;
            }
            int i4 = i2 + this.mMarginScreen;
            return (!z || (getWidth() + i4) + this.mMarginScreen >= view.getWidth()) ? ((view.getWidth() - this.mMarginScreen) - getWidth()) + i : i4;
        }

        private float getyInWindow(int i, View view, int i2) {
            float listViewHeight = getListViewHeight();
            float f = i - (listViewHeight / 2.0f);
            if (f < view.getHeight() * 0.1f) {
                f = view.getHeight() * 0.1f;
            }
            if (f + listViewHeight > view.getHeight() * 0.9f) {
                f = (view.getHeight() * 0.9f) - listViewHeight;
            }
            if (f < view.getHeight() * 0.1f) {
                f = view.getHeight() * 0.1f;
                setHeight((int) (view.getHeight() * 0.79999995f));
            }
            return f + i2;
        }

        private void initListView(int i, int i2) {
            ListView listView = getListView();
            listView.setChoiceMode(1);
            listView.setTextDirection(i);
            listView.setTextAlignment(i2);
            int selectedItemPosition = Spinner.this.getSelectedItemPosition();
            listView.setSelection(selectedItemPosition);
            listView.setItemChecked(selectedItemPosition, true);
        }

        private void showWithAnchor(View view, float f, float f2) {
            int[] iArr = new int[2];
            view.getLocationInWindow(iArr);
            int i = (int) f;
            int i2 = iArr[1];
            View view2 = this.mFenceView;
            if (view2 == null) {
                view2 = view.getRootView();
            }
            view2.getLocationInWindow(iArr);
            int i3 = iArr[1];
            int i4 = getxInWindow(iArr[0], i, view2);
            float f3 = getyInWindow(i2 - i3, view2, i3);
            if (isShowing()) {
                update(i4, (int) f3, getWidth(), getHeight());
                return;
            }
            showAtLocation(view, 0, i4, (int) f3);
            ListPopup.changeWindowBackground(this.mRootView.getRootView());
        }

        void computeContentWidth() {
            Drawable background = getBackground();
            int i = 0;
            if (background != null) {
                background.getPadding(Spinner.this.mTempRect);
                i = ViewUtils.isLayoutRtl(Spinner.this) ? Spinner.this.mTempRect.right : -Spinner.this.mTempRect.left;
            } else {
                Rect rect = Spinner.this.mTempRect;
                rect.right = 0;
                rect.left = 0;
            }
            int paddingLeft = Spinner.this.getPaddingLeft();
            int paddingRight = Spinner.this.getPaddingRight();
            int width = Spinner.this.getWidth();
            Spinner spinner = Spinner.this;
            int i2 = spinner.mDropDownWidth;
            if (i2 == -2) {
                int compatMeasureContentWidth = spinner.compatMeasureContentWidth((SpinnerAdapter) this.mAdapter, getBackground());
                int i3 = Spinner.this.getContext().getResources().getDisplayMetrics().widthPixels;
                Rect rect2 = Spinner.this.mTempRect;
                int i4 = (i3 - rect2.left) - rect2.right;
                int i5 = this.mMarginScreen;
                int i6 = i4 - (i5 * 2);
                if (compatMeasureContentWidth > i6) {
                    compatMeasureContentWidth = i6;
                }
                setContentWidth(Math.max(compatMeasureContentWidth, ((width - paddingLeft) - paddingRight) - (i5 * 2)));
            } else if (i2 == -1) {
                setContentWidth(((width - paddingLeft) - paddingRight) - (this.mMarginScreen * 2));
            } else {
                setContentWidth(i2);
            }
            setHorizontalOffset(ViewUtils.isLayoutRtl(Spinner.this) ? i + (((width - paddingRight) - getWidth()) - getHorizontalOriginalOffset()) : i + paddingLeft + getHorizontalOriginalOffset());
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public CharSequence getHintText() {
            return this.mHintText;
        }

        public int getHorizontalOriginalOffset() {
            return this.mOriginalHorizontalOffset;
        }

        @Override // miuix.internal.widget.ListPopup, miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setAdapter(ListAdapter listAdapter) {
            super.setAdapter(listAdapter);
            this.mAdapter = listAdapter;
        }

        @Override // miuix.internal.widget.ListPopup
        public void setContentWidth(int i) {
            super.setContentWidth(Math.max(Math.min(i, Spinner.this.mDropDownMaxWidth), Spinner.this.mDropDownMinWidth));
        }

        public void setFenceView(View view) {
            this.mFenceView = view;
        }

        public void setFenceX(int i) {
            this.mFenceX = i;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setHorizontalOriginalOffset(int i) {
            this.mOriginalHorizontalOffset = i;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void setPromptText(CharSequence charSequence) {
            this.mHintText = charSequence;
        }

        @Override // miuix.appcompat.widget.Spinner.SpinnerPopup
        public void show(int i, int i2, float f, float f2) {
            checkInFloatingWindowMode();
            boolean isShowing = isShowing();
            computeContentWidth();
            setInputMethodMode(2);
            if (prepareShow(Spinner.this, null)) {
                showWithAnchor(Spinner.this, f, f2);
            }
            initListView(i, i2);
            if (isShowing) {
                return;
            }
            setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: miuix.appcompat.widget.Spinner.DropdownPopup.2
                @Override // android.widget.PopupWindow.OnDismissListener
                public void onDismiss() {
                    Spinner.this.onSpinnerDismiss();
                }
            });
        }
    }

    /* loaded from: classes5.dex */
    public interface OnSpinnerDismissListener {
        void onSpinnerDismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: miuix.appcompat.widget.Spinner.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean mShowDropdown;

        SavedState(Parcel parcel) {
            super(parcel);
            this.mShowDropdown = parcel.readByte() != 0;
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeByte(this.mShowDropdown ? (byte) 1 : (byte) 0);
        }
    }

    /* loaded from: classes5.dex */
    private static class SpinnerCheckedProvider implements SpinnerCheckableArrayAdapter.CheckedStateProvider {
        private Spinner mSpinner;

        public SpinnerCheckedProvider(Spinner spinner) {
            this.mSpinner = spinner;
        }

        @Override // miuix.appcompat.internal.adapter.SpinnerCheckableArrayAdapter.CheckedStateProvider
        public boolean isChecked(int i) {
            return this.mSpinner.getSelectedItemPosition() == i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public interface SpinnerPopup {
        void dismiss();

        Drawable getBackground();

        CharSequence getHintText();

        int getHorizontalOffset();

        int getVerticalOffset();

        boolean isShowing();

        void setAdapter(ListAdapter listAdapter);

        void setBackgroundDrawable(Drawable drawable);

        void setHorizontalOffset(int i);

        void setHorizontalOriginalOffset(int i);

        void setPromptText(CharSequence charSequence);

        void setVerticalOffset(int i);

        void show(int i, int i2, float f, float f2);
    }

    static {
        try {
            Field declaredField = android.widget.Spinner.class.getDeclaredField("mForwardingListener");
            FORWARDING_LISTENER = declaredField;
            declaredField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.e("Spinner", "static initializer: ", e);
        }
    }

    public Spinner(Context context) {
        this(context, (AttributeSet) null);
    }

    public Spinner(Context context, int i) {
        this(context, null, R$attr.miuiSpinnerStyle, i);
    }

    public Spinner(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.miuiSpinnerStyle);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, -1);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i, int i2) {
        this(context, attributeSet, i, i2, null);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i, int i2, Resources.Theme theme) {
        super(context, attributeSet, i);
        this.mTempRect = new Rect();
        int[] iArr = R$styleable.Spinner;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, iArr, i, 0);
        if (theme != null) {
            this.mPopupContext = new ContextThemeWrapper(context, theme);
        } else {
            int resourceId = obtainStyledAttributes.getResourceId(R$styleable.Spinner_popupTheme, 0);
            if (resourceId != 0) {
                this.mPopupContext = new ContextThemeWrapper(context, resourceId);
            } else {
                this.mPopupContext = context;
            }
        }
        i2 = i2 == -1 ? obtainStyledAttributes.getInt(R$styleable.Spinner_spinnerModeCompat, 0) : i2;
        if (i2 == 0) {
            DialogPopup dialogPopup = new DialogPopup();
            this.mPopup = dialogPopup;
            dialogPopup.setPromptText(obtainStyledAttributes.getString(R$styleable.Spinner_android_prompt));
        } else if (i2 == 1) {
            DropdownPopup dropdownPopup = new DropdownPopup(this.mPopupContext, attributeSet, i);
            TypedArray obtainStyledAttributes2 = this.mPopupContext.obtainStyledAttributes(attributeSet, iArr, i, 0);
            this.mDropDownWidth = obtainStyledAttributes2.getLayoutDimension(R$styleable.Spinner_android_dropDownWidth, -2);
            this.mDropDownMinWidth = obtainStyledAttributes2.getLayoutDimension(R$styleable.Spinner_dropDownMinWidth, -2);
            this.mDropDownMaxWidth = obtainStyledAttributes2.getLayoutDimension(R$styleable.Spinner_dropDownMaxWidth, -2);
            int i3 = R$styleable.Spinner_android_popupBackground;
            int resourceId2 = obtainStyledAttributes2.getResourceId(i3, 0);
            if (resourceId2 != 0) {
                setPopupBackgroundResource(resourceId2);
            } else {
                dropdownPopup.setBackgroundDrawable(obtainStyledAttributes2.getDrawable(i3));
            }
            dropdownPopup.setPromptText(obtainStyledAttributes.getString(R$styleable.Spinner_android_prompt));
            obtainStyledAttributes2.recycle();
            this.mPopup = dropdownPopup;
            makeSupperForwardingListenerInvalid();
        }
        CharSequence[] textArray = obtainStyledAttributes.getTextArray(R$styleable.Spinner_android_entries);
        if (textArray != null) {
            ArrayAdapter arrayAdapter = new ArrayAdapter(context, R$layout.miuix_appcompat_simple_spinner_layout, 16908308, textArray);
            arrayAdapter.setDropDownViewResource(R$layout.miuix_appcompat_simple_spinner_dropdown_item);
            setAdapter((SpinnerAdapter) arrayAdapter);
        }
        obtainStyledAttributes.recycle();
        this.mPopupSet = true;
        SpinnerAdapter spinnerAdapter = this.mTempAdapter;
        if (spinnerAdapter != null) {
            setAdapter(spinnerAdapter);
            this.mTempAdapter = null;
        }
        CompatViewMethod.setForceDarkAllowed(this, false);
    }

    private void addPressAnim() {
        if (getBackground() != null) {
            Folme.useAt(this).touch().setTintMode(1).setScale(1.0f, new ITouchStyle.TouchType[0]).touchDown(new AnimConfig[0]);
        }
    }

    private void clearCachedSize() {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (!(spinnerPopup instanceof DropdownPopup) || ((DropdownPopup) spinnerPopup).getHeight() <= 0) {
            return;
        }
        ((DropdownPopup) this.mPopup).setHeight(-2);
        ((DropdownPopup) this.mPopup).setWidth(-2);
    }

    private int compatMeasureSelectItemWidth(SpinnerAdapter spinnerAdapter, Drawable drawable) {
        if (spinnerAdapter == null || spinnerAdapter.getCount() == 0) {
            return 0;
        }
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
        View view = spinnerAdapter.getView(Math.max(0, getSelectedItemPosition()), null, this);
        if (view.getLayoutParams() == null) {
            view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        }
        view.measure(makeMeasureSpec, makeMeasureSpec2);
        int max = Math.max(0, view.getMeasuredWidth());
        if (drawable != null) {
            drawable.getPadding(this.mTempRect);
            Rect rect = this.mTempRect;
            return max + rect.left + rect.right;
        }
        return max;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onConfigurationChanged$0() {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null && spinnerPopup.isShowing() && (this.mPopup instanceof DropdownPopup)) {
            if (MiuixUIUtils.isFreeformMode(this.mPopupContext)) {
                dismissPopup();
                return;
            }
            Point windowSize = WindowUtils.getWindowSize(getPopupContext());
            showPopup(windowSize.x * this.mXRelative, windowSize.y * this.mYRelative);
        }
    }

    private void makeSupperForwardingListenerInvalid() {
        Field field = FORWARDING_LISTENER;
        if (field == null) {
            return;
        }
        try {
            field.set(this, null);
        } catch (IllegalAccessException e) {
            Log.e("Spinner", "makeSupperForwardingListenerInvalid: ", e);
        }
    }

    private void notifySpinnerDismiss() {
        OnSpinnerDismissListener onSpinnerDismissListener = this.mOnSpinnerDismissListener;
        if (onSpinnerDismissListener != null) {
            onSpinnerDismissListener.onSpinnerDismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSpinnerDismiss() {
        Folme.useAt(this).touch().touchUp(new AnimConfig[0]);
        notifySpinnerDismiss();
    }

    private boolean superViewPerformClick() {
        sendAccessibilityEvent(1);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void vibrate() {
        HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_MESH_NORMAL);
    }

    int compatMeasureContentWidth(SpinnerAdapter spinnerAdapter, Drawable drawable) {
        int i = 0;
        if (spinnerAdapter == null) {
            return 0;
        }
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
        int max = Math.max(0, getSelectedItemPosition());
        int min = Math.min(spinnerAdapter.getCount(), max + 15);
        View view = null;
        int i2 = 0;
        for (int max2 = Math.max(0, max - (15 - (min - max))); max2 < min; max2++) {
            int itemViewType = spinnerAdapter.getItemViewType(max2);
            if (itemViewType != i) {
                view = null;
                i = itemViewType;
            }
            view = spinnerAdapter.getView(max2, view, this);
            if (view.getLayoutParams() == null) {
                view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            }
            view.measure(makeMeasureSpec, makeMeasureSpec2);
            i2 = Math.max(i2, view.getMeasuredWidth());
        }
        if (drawable != null) {
            drawable.getPadding(this.mTempRect);
            Rect rect = this.mTempRect;
            return i2 + rect.left + rect.right;
        }
        return i2;
    }

    void dismissPopup() {
        this.mPopup.dismiss();
    }

    @Override // android.widget.Spinner
    public int getDropDownHorizontalOffset() {
        SpinnerPopup spinnerPopup = this.mPopup;
        return spinnerPopup != null ? spinnerPopup.getHorizontalOffset() : super.getDropDownHorizontalOffset();
    }

    @Override // android.widget.Spinner
    public int getDropDownVerticalOffset() {
        SpinnerPopup spinnerPopup = this.mPopup;
        return spinnerPopup != null ? spinnerPopup.getVerticalOffset() : super.getDropDownVerticalOffset();
    }

    @Override // android.widget.Spinner
    public int getDropDownWidth() {
        return this.mPopup != null ? this.mDropDownWidth : super.getDropDownWidth();
    }

    @Override // android.widget.Spinner
    public Drawable getPopupBackground() {
        SpinnerPopup spinnerPopup = this.mPopup;
        return spinnerPopup != null ? spinnerPopup.getBackground() : super.getPopupBackground();
    }

    @Override // android.widget.Spinner
    public Context getPopupContext() {
        return this.mPopupContext;
    }

    @Override // android.widget.Spinner
    public CharSequence getPrompt() {
        SpinnerPopup spinnerPopup = this.mPopup;
        return spinnerPopup != null ? spinnerPopup.getHintText() : super.getPrompt();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        post(new Runnable() { // from class: miuix.appcompat.widget.Spinner$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Spinner.this.lambda$onConfigurationChanged$0();
            }
        });
    }

    @Override // android.widget.Spinner, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup == null || !spinnerPopup.isShowing()) {
            return;
        }
        this.mPopup.dismiss();
    }

    @Override // android.widget.Spinner, android.widget.AbsSpinner, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mPopup == null || View.MeasureSpec.getMode(i) != Integer.MIN_VALUE) {
            return;
        }
        setMeasuredDimension(Math.min(Math.min(getMeasuredWidth(), compatMeasureSelectItemWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(i)), getMeasuredHeight());
    }

    @Override // android.widget.Spinner, android.widget.AbsSpinner, android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        ViewTreeObserver viewTreeObserver;
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (!savedState.mShowDropdown || (viewTreeObserver = getViewTreeObserver()) == null) {
            return;
        }
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: miuix.appcompat.widget.Spinner.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (!Spinner.this.mPopup.isShowing()) {
                    Spinner.this.showPopup();
                }
                ViewTreeObserver viewTreeObserver2 = Spinner.this.getViewTreeObserver();
                if (viewTreeObserver2 != null) {
                    viewTreeObserver2.removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override // android.widget.Spinner, android.widget.AbsSpinner, android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        SpinnerPopup spinnerPopup = this.mPopup;
        savedState.mShowDropdown = spinnerPopup != null && spinnerPopup.isShowing();
        return savedState;
    }

    @Override // android.widget.Spinner, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            addPressAnim();
        }
        if (motionEvent.getAction() == 1 && !isPressed() && getBackground() != null) {
            Folme.useAt(this).touch().touchUp(new AnimConfig[0]);
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.widget.Spinner, android.view.View
    public boolean performClick() {
        getLocationInWindow(new int[2]);
        return performClick(r0[0], r0[1]);
    }

    public boolean performClick(float f, float f2) {
        Point windowSize = WindowUtils.getWindowSize(getPopupContext());
        this.mXRelative = f / windowSize.x;
        this.mYRelative = f2 / windowSize.y;
        if (superViewPerformClick()) {
            return true;
        }
        if (this.mPopup != null) {
            clearCachedSize();
            if (!this.mPopup.isShowing()) {
                showPopup(f, f2);
                HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_POPUP_LIGHT);
            }
            return true;
        }
        return super.performClick();
    }

    @Override // android.widget.AdapterView
    public void setAdapter(SpinnerAdapter spinnerAdapter) {
        if (!this.mPopupSet) {
            this.mTempAdapter = spinnerAdapter;
            return;
        }
        super.setAdapter(spinnerAdapter);
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DialogPopup) {
            spinnerPopup.setAdapter(new DialogPopupAdapter(spinnerAdapter, getPopupContext().getTheme()));
        } else if (spinnerPopup instanceof DropdownPopup) {
            spinnerPopup.setAdapter(new DropDownPopupAdapter(spinnerAdapter, getPopupContext().getTheme()));
        }
    }

    public void setDoubleLineContentAdapter(SpinnerDoubleLineContentAdapter spinnerDoubleLineContentAdapter) {
        setAdapter((SpinnerAdapter) new SpinnerCheckableArrayAdapter(getContext(), R$layout.miuix_appcompat_simple_spinner_layout, spinnerDoubleLineContentAdapter, new SpinnerCheckedProvider(this)));
    }

    @Override // android.widget.Spinner
    public void setDropDownHorizontalOffset(int i) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup == null) {
            super.setDropDownHorizontalOffset(i);
            return;
        }
        spinnerPopup.setHorizontalOriginalOffset(i);
        this.mPopup.setHorizontalOffset(i);
    }

    @Override // android.widget.Spinner
    public void setDropDownVerticalOffset(int i) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setVerticalOffset(i);
        } else {
            super.setDropDownVerticalOffset(i);
        }
    }

    @Override // android.widget.Spinner
    public void setDropDownWidth(int i) {
        if (this.mPopup != null) {
            this.mDropDownWidth = i;
        } else {
            super.setDropDownWidth(i);
        }
    }

    public void setFenceView(View view) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DropdownPopup) {
            ((DropdownPopup) spinnerPopup).setFenceView(view);
        }
    }

    public void setFenceX(int i) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DropdownPopup) {
            ((DropdownPopup) spinnerPopup).setFenceX(i);
        }
    }

    public void setFenceXFromView(View view) {
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        setFenceX(iArr[0]);
    }

    public void setOnSpinnerDismissListener(OnSpinnerDismissListener onSpinnerDismissListener) {
        this.mOnSpinnerDismissListener = onSpinnerDismissListener;
    }

    @Override // android.widget.Spinner
    public void setPopupBackgroundDrawable(Drawable drawable) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setBackgroundDrawable(drawable);
        } else {
            super.setPopupBackgroundDrawable(drawable);
        }
    }

    @Override // android.widget.Spinner
    public void setPopupBackgroundResource(int i) {
        setPopupBackgroundDrawable(AppCompatResources.getDrawable(getPopupContext(), i));
    }

    @Override // android.widget.Spinner
    public void setPrompt(CharSequence charSequence) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setPromptText(charSequence);
        } else {
            super.setPrompt(charSequence);
        }
    }

    void showPopup() {
        getLocationInWindow(new int[2]);
        showPopup(r0[0], r0[1]);
    }

    void showPopup(float f, float f2) {
        this.mPopup.show(getTextDirection(), getTextAlignment(), f, f2);
    }
}
