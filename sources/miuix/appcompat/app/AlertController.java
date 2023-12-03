package miuix.appcompat.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.view.ViewCompat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.utils.CommonUtils;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$bool;
import miuix.appcompat.R$color;
import miuix.appcompat.R$dimen;
import miuix.appcompat.R$id;
import miuix.appcompat.R$layout;
import miuix.appcompat.R$style;
import miuix.appcompat.R$styleable;
import miuix.appcompat.app.AlertController;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.internal.util.EasyModeHelper;
import miuix.appcompat.internal.widget.DialogButtonPanel;
import miuix.appcompat.internal.widget.DialogParentPanel2;
import miuix.appcompat.internal.widget.DialogRootView;
import miuix.appcompat.internal.widget.NestedScrollViewExpander;
import miuix.appcompat.widget.DialogAnimHelper;
import miuix.core.util.MiuixUIUtils;
import miuix.core.util.WindowUtils;
import miuix.internal.util.AnimHelper;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.DeviceHelper;
import miuix.internal.util.ReflectUtil;
import miuix.internal.widget.GroupButton;
import miuix.view.CompatViewMethod;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public class AlertController {
    ListAdapter mAdapter;
    private final int mAlertDialogLayout;
    Button mButtonNegative;
    Message mButtonNegativeMessage;
    private CharSequence mButtonNegativeText;
    Button mButtonNeutral;
    Message mButtonNeutralMessage;
    private CharSequence mButtonNeutralText;
    Button mButtonPositive;
    Message mButtonPositiveMessage;
    private CharSequence mButtonPositiveText;
    private CharSequence mCheckBoxMessage;
    private CharSequence mComment;
    private TextView mCommentView;
    private final Context mContext;
    private final Thread mCreateThread;
    private View mCustomTitleView;
    final AppCompatDialog mDialog;
    private int mDialogContentLayout;
    private DialogRootView mDialogRootView;
    private View mDimBg;
    private List<ButtonInfo> mExtraButtonList;
    private int mFakeLandScreenMinorSize;
    Handler mHandler;
    boolean mHapticFeedbackEnabled;
    private Drawable mIcon;
    private View mInflatedView;
    private boolean mIsChecked;
    private boolean mIsDialogAnimating;
    private boolean mLandscapePanel;
    private AlertDialog.OnDialogLayoutReloadListener mLayoutReloadListener;
    int mListItemLayout;
    int mListLayout;
    ListView mListView;
    private CharSequence mMessage;
    private TextView mMessageView;
    int mMultiChoiceItemLayout;
    private int mPanelAndImeMargin;
    private final int mPanelMaxWidth;
    private final int mPanelMaxWidthLand;
    private int mPanelOriginLeftMargin;
    private int mPanelOriginRightMargin;
    private DialogParentPanel2 mParentPanel;
    private boolean mPreferLandscape;
    private int mScreenMinorSize;
    private boolean mSetupWindowInsetsAnimation;
    private AlertDialog.OnDialogShowAnimListener mShowAnimListener;
    private final boolean mShowTitle;
    int mSingleChoiceItemLayout;
    private CharSequence mTitle;
    private TextView mTitleView;
    private boolean mTreatAsLandConfig;
    private View mView;
    private int mViewLayoutResId;
    private final Window mWindow;
    private WindowManager mWindowManager;
    private boolean mIsDebugEnabled = false;
    private int mIconId = 0;
    int mCheckedItem = -1;
    private boolean mCancelable = true;
    private boolean mCanceledOnTouchOutside = true;
    private int mScreenOrientation = 0;
    private Point mWindowSize = new Point();
    private Point mScreenRealSize = new Point();
    private AlertDialog.OnDialogShowAnimListener mShowAnimListenerWrapper = new AlertDialog.OnDialogShowAnimListener() { // from class: miuix.appcompat.app.AlertController.1
        @Override // miuix.appcompat.app.AlertDialog.OnDialogShowAnimListener
        public void onShowAnimComplete() {
            AlertController.this.mIsDialogAnimating = false;
            if (AlertController.this.mShowAnimListener != null) {
                AlertController.this.mShowAnimListener.onShowAnimComplete();
            }
        }

        @Override // miuix.appcompat.app.AlertDialog.OnDialogShowAnimListener
        public void onShowAnimStart() {
            AlertController.this.mIsDialogAnimating = true;
            if (AlertController.this.mShowAnimListener != null) {
                AlertController.this.mShowAnimListener.onShowAnimStart();
            }
        }
    };
    private boolean mIsEnableImmersive = true;
    private final View.OnClickListener mButtonHandler = new View.OnClickListener() { // from class: miuix.appcompat.app.AlertController.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            int i = HapticFeedbackConstants.MIUI_TAP_LIGHT;
            AlertController alertController = AlertController.this;
            if (view == alertController.mButtonPositive) {
                Message message = alertController.mButtonPositiveMessage;
                r3 = message != null ? Message.obtain(message) : null;
                i = HapticFeedbackConstants.MIUI_TAP_NORMAL;
            } else if (view == alertController.mButtonNegative) {
                Message message2 = alertController.mButtonNegativeMessage;
                if (message2 != null) {
                    r3 = Message.obtain(message2);
                }
            } else if (view == alertController.mButtonNeutral) {
                Message message3 = alertController.mButtonNeutralMessage;
                if (message3 != null) {
                    r3 = Message.obtain(message3);
                }
            } else {
                if (alertController.mExtraButtonList != null && !AlertController.this.mExtraButtonList.isEmpty()) {
                    Iterator it = AlertController.this.mExtraButtonList.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        ButtonInfo buttonInfo = (ButtonInfo) it.next();
                        if (view == buttonInfo.mButton) {
                            r3 = buttonInfo.mMsg;
                            break;
                        }
                    }
                }
                if ((view instanceof GroupButton) && ((GroupButton) view).isPrimary()) {
                    i = HapticFeedbackConstants.MIUI_TAP_NORMAL;
                }
            }
            HapticCompat.performHapticFeedbackAsync(view, i);
            if (r3 != null) {
                r3.sendToTarget();
            }
            AlertController.this.mHandler.sendEmptyMessage(-1651327837);
        }
    };
    private boolean mInsetsAnimationPlayed = false;
    private final LayoutChangeListener mLayoutChangeListener = new LayoutChangeListener(this);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: miuix.appcompat.app.AlertController$5  reason: invalid class name */
    /* loaded from: classes5.dex */
    public class AnonymousClass5 implements View.OnApplyWindowInsetsListener {
        AnonymousClass5() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onApplyWindowInsets$0(WindowInsets windowInsets) {
            AlertController.this.updateDialogPanelByWindowInsets(windowInsets);
        }

        @Override // android.view.View.OnApplyWindowInsetsListener
        public WindowInsets onApplyWindowInsets(View view, final WindowInsets windowInsets) {
            view.post(new Runnable() { // from class: miuix.appcompat.app.AlertController$5$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AlertController.AnonymousClass5.this.lambda$onApplyWindowInsets$0(windowInsets);
                }
            });
            return WindowInsets.CONSUMED;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public static class AlertParams {
        ListAdapter mAdapter;
        CharSequence mCheckBoxMessage;
        boolean[] mCheckedItems;
        CharSequence mComment;
        final Context mContext;
        Cursor mCursor;
        View mCustomTitleView;
        boolean mHapticFeedbackEnabled;
        Drawable mIcon;
        final LayoutInflater mInflater;
        boolean mIsChecked;
        String mIsCheckedColumn;
        boolean mIsMultiChoice;
        boolean mIsSingleChoice;
        CharSequence[] mItems;
        String mLabelColumn;
        CharSequence mMessage;
        DialogInterface.OnClickListener mNegativeButtonListener;
        CharSequence mNegativeButtonText;
        DialogInterface.OnClickListener mNeutralButtonListener;
        CharSequence mNeutralButtonText;
        DialogInterface.OnCancelListener mOnCancelListener;
        DialogInterface.OnMultiChoiceClickListener mOnCheckboxClickListener;
        DialogInterface.OnClickListener mOnClickListener;
        AlertDialog.OnDialogShowAnimListener mOnDialogShowAnimListener;
        DialogInterface.OnDismissListener mOnDismissListener;
        AdapterView.OnItemSelectedListener mOnItemSelectedListener;
        DialogInterface.OnKeyListener mOnKeyListener;
        OnPrepareListViewListener mOnPrepareListViewListener;
        DialogInterface.OnShowListener mOnShowListener;
        DialogInterface.OnClickListener mPositiveButtonListener;
        CharSequence mPositiveButtonText;
        boolean mPreferLandscape;
        CharSequence mTitle;
        View mView;
        int mViewLayoutResId;
        int mIconId = 0;
        int mIconAttrId = 0;
        int mCheckedItem = -1;
        boolean mCancelable = true;
        boolean mEnableDialogImmersive = true;
        List<ButtonInfo> mExtraButtonList = new ArrayList();

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes5.dex */
        public interface OnPrepareListViewListener {
            void onPrepareListView(ListView listView);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public AlertParams(Context context) {
            this.mContext = context;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        private void createListView(final AlertController alertController) {
            ListAdapter listAdapter;
            final ListView listView = (ListView) this.mInflater.inflate(alertController.mListLayout, (ViewGroup) null);
            if (this.mIsMultiChoice) {
                listAdapter = this.mCursor == null ? new ArrayAdapter<CharSequence>(this.mContext, alertController.mMultiChoiceItemLayout, 16908308, this.mItems) { // from class: miuix.appcompat.app.AlertController.AlertParams.1
                    @Override // android.widget.ArrayAdapter, android.widget.Adapter
                    public View getView(int i, View view, ViewGroup viewGroup) {
                        View view2 = super.getView(i, view, viewGroup);
                        boolean[] zArr = AlertParams.this.mCheckedItems;
                        if (zArr != null && zArr[i]) {
                            listView.setItemChecked(i, true);
                        }
                        CompatViewMethod.setForceDarkAllowed(view2, false);
                        if (view == null) {
                            AnimHelper.addPressAnim(view2);
                        }
                        EasyModeHelper.updateTextViewSize((TextView) view2.findViewById(16908308));
                        return view2;
                    }
                } : new CursorAdapter(this.mContext, this.mCursor, false) { // from class: miuix.appcompat.app.AlertController.AlertParams.2
                    private final int mIsCheckedIndex;
                    private final int mLabelIndex;

                    {
                        Cursor cursor = getCursor();
                        this.mLabelIndex = cursor.getColumnIndexOrThrow(AlertParams.this.mLabelColumn);
                        this.mIsCheckedIndex = cursor.getColumnIndexOrThrow(AlertParams.this.mIsCheckedColumn);
                    }

                    @Override // android.widget.CursorAdapter
                    public void bindView(View view, Context context, Cursor cursor) {
                        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(16908308);
                        checkedTextView.setText(cursor.getString(this.mLabelIndex));
                        listView.setItemChecked(cursor.getPosition(), cursor.getInt(this.mIsCheckedIndex) == 1);
                        EasyModeHelper.updateTextViewSize(checkedTextView);
                    }

                    @Override // android.widget.CursorAdapter
                    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                        View inflate = AlertParams.this.mInflater.inflate(alertController.mMultiChoiceItemLayout, viewGroup, false);
                        AnimHelper.addPressAnim(inflate);
                        CompatViewMethod.setForceDarkAllowed(inflate, false);
                        return inflate;
                    }
                };
            } else {
                int i = this.mIsSingleChoice ? alertController.mSingleChoiceItemLayout : alertController.mListItemLayout;
                if (this.mCursor != null) {
                    listAdapter = new SimpleCursorAdapter(this.mContext, i, this.mCursor, new String[]{this.mLabelColumn}, new int[]{16908308}) { // from class: miuix.appcompat.app.AlertController.AlertParams.3
                        @Override // android.widget.CursorAdapter, android.widget.Adapter
                        public View getView(int i2, View view, ViewGroup viewGroup) {
                            View view2 = super.getView(i2, view, viewGroup);
                            if (view == null) {
                                AnimHelper.addPressAnim(view2);
                            }
                            EasyModeHelper.updateTextViewSize((TextView) view2.findViewById(16908308));
                            return view2;
                        }
                    };
                } else {
                    listAdapter = this.mAdapter;
                    if (listAdapter == null) {
                        listAdapter = new CheckedItemAdapter(this.mContext, i, 16908308, this.mItems);
                    }
                }
            }
            OnPrepareListViewListener onPrepareListViewListener = this.mOnPrepareListViewListener;
            if (onPrepareListViewListener != null) {
                onPrepareListViewListener.onPrepareListView(listView);
            }
            alertController.mAdapter = listAdapter;
            alertController.mCheckedItem = this.mCheckedItem;
            if (this.mOnClickListener != null) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.appcompat.app.AlertController.AlertParams.4
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
                        AlertParams.this.mOnClickListener.onClick(alertController.mDialog, i2);
                        if (AlertParams.this.mIsSingleChoice) {
                            return;
                        }
                        alertController.mDialog.dismiss();
                    }
                });
            } else if (this.mOnCheckboxClickListener != null) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: miuix.appcompat.app.AlertController.AlertParams.5
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
                        boolean[] zArr = AlertParams.this.mCheckedItems;
                        if (zArr != null) {
                            zArr[i2] = listView.isItemChecked(i2);
                        }
                        AlertParams.this.mOnCheckboxClickListener.onClick(alertController.mDialog, i2, listView.isItemChecked(i2));
                    }
                });
            }
            AdapterView.OnItemSelectedListener onItemSelectedListener = this.mOnItemSelectedListener;
            if (onItemSelectedListener != null) {
                listView.setOnItemSelectedListener(onItemSelectedListener);
            }
            if (this.mIsSingleChoice) {
                listView.setChoiceMode(1);
            } else if (this.mIsMultiChoice) {
                listView.setChoiceMode(2);
            }
            alertController.mListView = listView;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void apply(AlertController alertController) {
            View view = this.mCustomTitleView;
            if (view != null) {
                alertController.setCustomTitle(view);
            } else {
                CharSequence charSequence = this.mTitle;
                if (charSequence != null) {
                    alertController.setTitle(charSequence);
                }
                Drawable drawable = this.mIcon;
                if (drawable != null) {
                    alertController.setIcon(drawable);
                }
                int i = this.mIconId;
                if (i != 0) {
                    alertController.setIcon(i);
                }
                int i2 = this.mIconAttrId;
                if (i2 != 0) {
                    alertController.setIcon(alertController.getIconAttributeResId(i2));
                }
            }
            CharSequence charSequence2 = this.mMessage;
            if (charSequence2 != null) {
                alertController.setMessage(charSequence2);
            }
            CharSequence charSequence3 = this.mComment;
            if (charSequence3 != null) {
                alertController.setComment(charSequence3);
            }
            CharSequence charSequence4 = this.mPositiveButtonText;
            if (charSequence4 != null) {
                alertController.setButton(-1, charSequence4, this.mPositiveButtonListener, null);
            }
            CharSequence charSequence5 = this.mNegativeButtonText;
            if (charSequence5 != null) {
                alertController.setButton(-2, charSequence5, this.mNegativeButtonListener, null);
            }
            CharSequence charSequence6 = this.mNeutralButtonText;
            if (charSequence6 != null) {
                alertController.setButton(-3, charSequence6, this.mNeutralButtonListener, null);
            }
            if (this.mExtraButtonList != null) {
                alertController.mExtraButtonList = new ArrayList(this.mExtraButtonList);
            }
            if (this.mItems != null || this.mCursor != null || this.mAdapter != null) {
                createListView(alertController);
            }
            View view2 = this.mView;
            if (view2 != null) {
                alertController.setView(view2);
            } else {
                int i3 = this.mViewLayoutResId;
                if (i3 != 0) {
                    alertController.setView(i3);
                }
            }
            CharSequence charSequence7 = this.mCheckBoxMessage;
            if (charSequence7 != null) {
                alertController.setCheckBox(this.mIsChecked, charSequence7);
            }
            alertController.mHapticFeedbackEnabled = this.mHapticFeedbackEnabled;
            alertController.setEnableImmersive(this.mEnableDialogImmersive);
            alertController.setPreferLandscape(this.mPreferLandscape);
        }
    }

    /* loaded from: classes5.dex */
    private static final class ButtonHandler extends Handler {
        private static final int MSG_DISMISS_DIALOG = -1651327837;
        private final WeakReference<DialogInterface> mDialog;

        ButtonHandler(DialogInterface dialogInterface) {
            this.mDialog = new WeakReference<>(dialogInterface);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            DialogInterface dialogInterface = this.mDialog.get();
            int i = message.what;
            if (i != MSG_DISMISS_DIALOG) {
                ((DialogInterface.OnClickListener) message.obj).onClick(dialogInterface, i);
            } else if (dialogInterface != null) {
                dialogInterface.dismiss();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public static class ButtonInfo {
        private GroupButton mButton;
        private Message mMsg;
        private final DialogInterface.OnClickListener mOnClickListener;
        private final int mStyle;
        private final CharSequence mText;
        private final int mWhich;

        ButtonInfo(CharSequence charSequence, int i, DialogInterface.OnClickListener onClickListener, int i2) {
            this.mText = charSequence;
            this.mStyle = i;
            this.mMsg = null;
            this.mOnClickListener = onClickListener;
            this.mWhich = i2;
        }

        ButtonInfo(CharSequence charSequence, int i, Message message) {
            this.mText = charSequence;
            this.mStyle = i;
            this.mMsg = message;
            this.mOnClickListener = null;
            this.mWhich = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        public CheckedItemAdapter(Context context, int i, int i2, CharSequence[] charSequenceArr) {
            super(context, i, i2, charSequenceArr);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            if (view == null) {
                AnimHelper.addPressAnim(view2);
            }
            EasyModeHelper.updateTextViewSize((TextView) view2.findViewById(16908308));
            return view2;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class LayoutChangeListener implements View.OnLayoutChangeListener {
        private final WeakReference<AlertController> mHost;
        private final Rect mWindowVisibleFrame = new Rect();

        LayoutChangeListener(AlertController alertController) {
            this.mHost = new WeakReference<>(alertController);
        }

        private void changeViewPadding(View view, int i, int i2) {
            view.setPadding(i, 0, i2, 0);
        }

        private void handleImeChange(View view, Rect rect, AlertController alertController) {
            int i;
            int height = (view.getHeight() - alertController.getDialogPanelExtraBottomPadding()) - rect.bottom;
            if (height > 0) {
                i = (-height) + MiuixUIUtils.getNavigationBarHeight(alertController.mContext);
                DialogAnimHelper.cancelAnimator();
            } else {
                i = 0;
            }
            alertController.translateDialogPanel(i);
        }

        private void handleMultiWindowLandscapeChange(AlertController alertController, int i) {
            if (!MiuixUIUtils.isInMultiWindowMode(alertController.mContext)) {
                DialogRootView dialogRootView = alertController.mDialogRootView;
                if (dialogRootView.getPaddingLeft() > 0 || dialogRootView.getPaddingRight() > 0) {
                    changeViewPadding(dialogRootView, 0, 0);
                    return;
                }
                return;
            }
            Rect rect = this.mWindowVisibleFrame;
            if (rect.left <= 0) {
                changeViewPadding(alertController.mDialogRootView, 0, 0);
                return;
            }
            int width = i - rect.width();
            if (this.mWindowVisibleFrame.right == i) {
                changeViewPadding(alertController.mDialogRootView, width, 0);
            } else {
                changeViewPadding(alertController.mDialogRootView, 0, width);
            }
        }

        public boolean hasNavigationBarHeightInMultiWindowMode() {
            this.mHost.get().mWindowManager.getDefaultDisplay().getRealSize(this.mHost.get().mScreenRealSize);
            Rect rect = this.mWindowVisibleFrame;
            return (rect.left == 0 && rect.right == this.mHost.get().mScreenRealSize.x && this.mWindowVisibleFrame.top <= MiuixUIUtils.getStatusBarHeight(this.mHost.get().mContext)) ? false : true;
        }

        public boolean isInMultiScreenTop() {
            AlertController alertController = this.mHost.get();
            if (alertController != null) {
                alertController.mWindowManager.getDefaultDisplay().getRealSize(alertController.mScreenRealSize);
                Rect rect = this.mWindowVisibleFrame;
                if (rect.left == 0 && rect.right == alertController.mScreenRealSize.x) {
                    int i = (int) (alertController.mScreenRealSize.y * 0.75f);
                    Rect rect2 = this.mWindowVisibleFrame;
                    return rect2.top >= 0 && rect2.bottom <= i;
                }
                return false;
            }
            return false;
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            AlertController alertController = this.mHost.get();
            if (alertController != null) {
                view.getWindowVisibleDisplayFrame(this.mWindowVisibleFrame);
                handleMultiWindowLandscapeChange(alertController, i3);
                if (Build.VERSION.SDK_INT < 30) {
                    if (view.findFocus() != null) {
                        if (alertController.isFreeFormMode()) {
                            return;
                        }
                        handleImeChange(view, this.mWindowVisibleFrame, alertController);
                    } else if (alertController.mParentPanel.getTranslationY() < 0.0f) {
                        alertController.translateDialogPanel(0);
                    }
                }
            }
        }
    }

    public AlertController(Context context, AppCompatDialog appCompatDialog, Window window) {
        this.mContext = context;
        this.mDialog = appCompatDialog;
        this.mWindow = window;
        this.mHandler = new ButtonHandler(appCompatDialog);
        initScreenMinorSize(context);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(null, R$styleable.AlertDialog, 16842845, 0);
        this.mAlertDialogLayout = obtainStyledAttributes.getResourceId(R$styleable.AlertDialog_layout, 0);
        this.mListLayout = obtainStyledAttributes.getResourceId(R$styleable.AlertDialog_listLayout, 0);
        this.mMultiChoiceItemLayout = obtainStyledAttributes.getResourceId(R$styleable.AlertDialog_multiChoiceItemLayout, 0);
        this.mSingleChoiceItemLayout = obtainStyledAttributes.getResourceId(R$styleable.AlertDialog_singleChoiceItemLayout, 0);
        this.mListItemLayout = obtainStyledAttributes.getResourceId(R$styleable.AlertDialog_listItemLayout, 0);
        this.mShowTitle = obtainStyledAttributes.getBoolean(R$styleable.AlertDialog_showTitle, true);
        obtainStyledAttributes.recycle();
        appCompatDialog.supportRequestWindowFeature(1);
        if (Build.VERSION.SDK_INT < 28 && isMiuiLegacyNotch()) {
            ReflectUtil.callObjectMethod(window, "addExtraFlags", new Class[]{Integer.TYPE}, 768);
        }
        this.mTreatAsLandConfig = context.getResources().getBoolean(R$bool.treat_as_land);
        this.mPanelMaxWidth = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_dialog_max_width);
        this.mPanelMaxWidthLand = context.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_dialog_max_width_land);
        this.mCreateThread = Thread.currentThread();
        isDialogImeDebugEnabled();
    }

    private void addPressAnimInternal(View view) {
        Drawable buttonSelectorBackground;
        if (!AnimHelper.isDialogDebugInAndroidUIThreadEnabled()) {
            AnimHelper.addPressAnim(view);
        } else if (!(view instanceof GroupButton) || (buttonSelectorBackground = ((GroupButton) view).getButtonSelectorBackground()) == null) {
        } else {
            view.setBackground(buttonSelectorBackground);
        }
    }

    static boolean canTextInput(View view) {
        if (view.onCheckIsTextEditor()) {
            return true;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            while (childCount > 0) {
                childCount--;
                if (canTextInput(viewGroup.getChildAt(childCount))) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private void changeTitlePadding(TextView textView) {
        textView.setPadding(textView.getPaddingLeft(), textView.getPaddingTop(), textView.getPaddingRight(), 0);
    }

    private void checkAndClearFocus() {
        View currentFocus = this.mWindow.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
            hideSoftIME();
        }
    }

    private boolean checkThread() {
        return this.mCreateThread == Thread.currentThread();
    }

    private void cleanWindowInsetsAnimation() {
        if (this.mSetupWindowInsetsAnimation) {
            this.mWindow.getDecorView().setWindowInsetsAnimationCallback(null);
            this.mWindow.getDecorView().setOnApplyWindowInsetsListener(null);
            this.mSetupWindowInsetsAnimation = false;
        }
    }

    private void clearFitSystemWindow(View view) {
        if ((view instanceof DialogParentPanel2) || view == null) {
            return;
        }
        int i = 0;
        view.setFitsSystemWindows(false);
        if (!(view instanceof ViewGroup)) {
            return;
        }
        while (true) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (i >= viewGroup.getChildCount()) {
                return;
            }
            clearFitSystemWindow(viewGroup.getChildAt(i));
            i++;
        }
    }

    private void disableForceDark(View view) {
        CompatViewMethod.setForceDarkAllowed(view, false);
    }

    private int getCutoutMode(int i, int i2) {
        return i2 == 0 ? i == 2 ? 2 : 1 : i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDialogPanelExtraBottomPadding() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDialogPanelMargin() {
        int[] iArr = new int[2];
        this.mParentPanel.getLocationInWindow(iArr);
        return (this.mWindow.getDecorView().getHeight() - (iArr[1] + this.mParentPanel.getHeight())) - this.mContext.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_dialog_ime_margin);
    }

    private int getGravity() {
        return isRealTablet() ? 17 : 81;
    }

    private int getPanelWidth(boolean z) {
        int i;
        int i2 = R$layout.miuix_appcompat_alert_dialog_content;
        this.mLandscapePanel = false;
        if (this.mPreferLandscape && shouldUseLandscapePanel()) {
            i2 = R$layout.miuix_appcompat_alert_dialog_content_land;
            this.mLandscapePanel = true;
            i = this.mPanelMaxWidthLand;
        } else {
            i = shouldLimitWidth() ? this.mPanelMaxWidth : z ? this.mTreatAsLandConfig ? this.mFakeLandScreenMinorSize : this.mScreenMinorSize : -1;
        }
        if (this.mDialogContentLayout != i2) {
            this.mDialogContentLayout = i2;
            DialogParentPanel2 dialogParentPanel2 = this.mParentPanel;
            if (dialogParentPanel2 != null) {
                this.mDialogRootView.removeView(dialogParentPanel2);
            }
            DialogParentPanel2 dialogParentPanel22 = (DialogParentPanel2) LayoutInflater.from(this.mContext).inflate(this.mDialogContentLayout, (ViewGroup) this.mDialogRootView, false);
            this.mParentPanel = dialogParentPanel22;
            this.mDialogRootView.addView(dialogParentPanel22);
        }
        return i;
    }

    private int getScreenOrientation() {
        WindowManager windowManager = this.mWindowManager;
        if (windowManager == null) {
            return 0;
        }
        int rotation = windowManager.getDefaultDisplay().getRotation();
        return (rotation == 1 || rotation == 3) ? 2 : 1;
    }

    private void hideSoftIME() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.mContext.getSystemService(InputMethodManager.class);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(this.mParentPanel.getWindowToken(), 0);
        }
    }

    private void initScreenMinorSize(Context context) {
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        updateMinorScreenSize();
        this.mFakeLandScreenMinorSize = context.getResources().getDimensionPixelSize(R$dimen.fake_landscape_screen_minor_size);
    }

    private boolean isCancelable() {
        return this.mCancelable;
    }

    private boolean isCanceledOnTouchOutside() {
        return this.mCanceledOnTouchOutside;
    }

    private boolean isDialogImeDebugEnabled() {
        String str = "";
        try {
            String readProp = CommonUtils.readProp("log.tag.alertdialog.ime.debug.enable");
            if (readProp != null) {
                str = readProp;
            }
        } catch (Exception e) {
            Log.i("AlertController", "can not access property log.tag.alertdialog.ime.enable, undebugable", e);
        }
        Log.d("AlertController", "Alert dialog ime debugEnable = " + str);
        boolean equals = TextUtils.equals("true", str);
        this.mIsDebugEnabled = equals;
        return equals;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isFreeFormMode() {
        return MiuixUIUtils.isFreeformMode(this.mContext);
    }

    private boolean isInPcMode() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "synergy_mode", 0) == 1;
    }

    private boolean isLandscape() {
        return isLandscape(getScreenOrientation());
    }

    private boolean isLandscape(int i) {
        if (this.mTreatAsLandConfig) {
            return true;
        }
        if (i != 2) {
            return false;
        }
        if (isInPcMode()) {
            this.mWindowManager.getDefaultDisplay().getRealSize(this.mScreenRealSize);
            Point point = this.mScreenRealSize;
            return point.x > point.y;
        }
        return true;
    }

    @Deprecated
    private boolean isMiuiLegacyNotch() {
        Class<?> cls = ReflectUtil.getClass("android.os.SystemProperties");
        Class cls2 = Integer.TYPE;
        return ((Integer) ReflectUtil.callStaticObjectMethod(cls, cls2, "getInt", new Class[]{String.class, cls2}, "ro.miui.notch", 0)).intValue() == 1;
    }

    private boolean isNeedUpdateDialogPanelTranslationY() {
        boolean isInMultiWindowMode = MiuixUIUtils.isInMultiWindowMode(this.mContext);
        char c = (!isInMultiWindowMode || isFreeFormMode()) ? (char) 65535 : DeviceHelper.isTablet(this.mContext) ? (char) 0 : (char) 1;
        if (this.mIsDialogAnimating) {
            if (c == 0) {
                return true;
            }
        } else if (this.mSetupWindowInsetsAnimation && (this.mInsetsAnimationPlayed || isInMultiWindowMode)) {
            return true;
        }
        return false;
    }

    private boolean isRealTablet() {
        return DeviceHelper.isTablet(this.mContext) && !DeviceHelper.isFoldDevice();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$installContent$0(Configuration configuration) {
        onConfigurationChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setupView$1(View view) {
        if (isCancelable() && isCanceledOnTouchOutside()) {
            hideSoftIME();
            this.mDialog.cancel();
        }
    }

    private void onLayoutReload() {
        ((AlertDialog) this.mDialog).onLayoutReload();
        AlertDialog.OnDialogLayoutReloadListener onDialogLayoutReloadListener = this.mLayoutReloadListener;
        if (onDialogLayoutReloadListener != null) {
            onDialogLayoutReloadListener.onLayoutReload();
        }
    }

    private void reInitLandConfig() {
        this.mTreatAsLandConfig = this.mContext.getApplicationContext().getResources().getBoolean(R$bool.treat_as_land);
        this.mFakeLandScreenMinorSize = this.mContext.getApplicationContext().getResources().getDimensionPixelSize(R$dimen.fake_landscape_screen_minor_size);
        updateMinorScreenSize();
    }

    private void setupButtons(ViewGroup viewGroup) {
        int i;
        Button button = (Button) viewGroup.findViewById(16908313);
        this.mButtonPositive = button;
        button.setOnClickListener(this.mButtonHandler);
        EasyModeHelper.updateTextViewSize(this.mButtonPositive);
        if (TextUtils.isEmpty(this.mButtonPositiveText)) {
            this.mButtonPositive.setVisibility(8);
            i = 0;
        } else {
            this.mButtonPositive.setText(this.mButtonPositiveText);
            this.mButtonPositive.setVisibility(0);
            disableForceDark(this.mButtonPositive);
            addPressAnimInternal(this.mButtonPositive);
            i = 1;
        }
        Button button2 = (Button) viewGroup.findViewById(16908314);
        this.mButtonNegative = button2;
        button2.setOnClickListener(this.mButtonHandler);
        EasyModeHelper.updateTextViewSize(this.mButtonNegative);
        if (TextUtils.isEmpty(this.mButtonNegativeText)) {
            this.mButtonNegative.setVisibility(8);
        } else {
            this.mButtonNegative.setText(this.mButtonNegativeText);
            this.mButtonNegative.setVisibility(0);
            i++;
            disableForceDark(this.mButtonNegative);
            addPressAnimInternal(this.mButtonNegative);
        }
        Button button3 = (Button) viewGroup.findViewById(16908315);
        this.mButtonNeutral = button3;
        button3.setOnClickListener(this.mButtonHandler);
        EasyModeHelper.updateTextViewSize(this.mButtonNeutral);
        if (TextUtils.isEmpty(this.mButtonNeutralText)) {
            this.mButtonNeutral.setVisibility(8);
        } else {
            this.mButtonNeutral.setText(this.mButtonNeutralText);
            this.mButtonNeutral.setVisibility(0);
            i++;
            disableForceDark(this.mButtonNeutral);
            addPressAnimInternal(this.mButtonNeutral);
        }
        List<ButtonInfo> list = this.mExtraButtonList;
        if (list != null && !list.isEmpty()) {
            for (ButtonInfo buttonInfo : this.mExtraButtonList) {
                if (buttonInfo.mButton != null) {
                    ViewParent parent = buttonInfo.mButton.getParent();
                    if (parent instanceof ViewGroup) {
                        ((ViewGroup) parent).removeView(buttonInfo.mButton);
                    }
                }
            }
            for (ButtonInfo buttonInfo2 : this.mExtraButtonList) {
                if (buttonInfo2.mButton == null) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
                    buttonInfo2.mButton = new GroupButton(this.mContext, null, buttonInfo2.mStyle);
                    buttonInfo2.mButton.setText(buttonInfo2.mText);
                    buttonInfo2.mButton.setOnClickListener(this.mButtonHandler);
                    buttonInfo2.mButton.setLayoutParams(layoutParams);
                    buttonInfo2.mButton.setMaxLines(2);
                    buttonInfo2.mButton.setGravity(17);
                }
                if (buttonInfo2.mMsg == null) {
                    buttonInfo2.mMsg = this.mHandler.obtainMessage(buttonInfo2.mWhich, buttonInfo2.mOnClickListener);
                }
                if (buttonInfo2.mButton.getVisibility() != 8) {
                    i++;
                    EasyModeHelper.updateTextViewSize(buttonInfo2.mButton);
                    disableForceDark(buttonInfo2.mButton);
                    addPressAnimInternal(buttonInfo2.mButton);
                }
                viewGroup.addView(buttonInfo2.mButton);
            }
        }
        if (i == 0) {
            viewGroup.setVisibility(8);
        } else {
            ((DialogButtonPanel) viewGroup).setForceVertical(this.mLandscapePanel);
            viewGroup.invalidate();
        }
        Point point = new Point();
        Point point2 = new Point();
        WindowUtils.getScreenAndWindowSize(this.mContext, point, point2);
        if (((float) point2.y) <= ((float) Math.max(point.x, point.y)) * 0.3f) {
            return;
        }
        ViewGroup viewGroup2 = (ViewGroup) viewGroup.getParent();
        if (viewGroup2 != null) {
            viewGroup2.removeView(viewGroup);
        }
        this.mParentPanel.addView(viewGroup);
    }

    private void setupCheckbox(CheckBox checkBox) {
        if (this.mCheckBoxMessage == null) {
            checkBox.setVisibility(8);
            return;
        }
        checkBox.setVisibility(0);
        checkBox.setChecked(this.mIsChecked);
        checkBox.setText(this.mCheckBoxMessage);
    }

    private void setupContent(ViewGroup viewGroup) {
        View childAt;
        FrameLayout frameLayout = (FrameLayout) viewGroup.findViewById(16908331);
        boolean z = false;
        if (this.mListView == null) {
            ViewGroup viewGroup2 = (ViewGroup) viewGroup.findViewById(R$id.contentView);
            if (viewGroup2 != null) {
                setupContentView(viewGroup2);
            }
            if (frameLayout != null) {
                boolean z2 = setupCustomContent(frameLayout);
                if (z2 && (childAt = frameLayout.getChildAt(0)) != null) {
                    ViewCompat.setNestedScrollingEnabled(childAt, true);
                }
                z = z2;
            }
            NestedScrollViewExpander nestedScrollViewExpander = (NestedScrollViewExpander) viewGroup;
            if (!z) {
                frameLayout = null;
            }
            nestedScrollViewExpander.setExpandView(frameLayout);
            return;
        }
        if (!(frameLayout != null ? setupCustomContent(frameLayout) : false)) {
            viewGroup.removeView(viewGroup.findViewById(R$id.contentView));
            viewGroup.removeView(frameLayout);
            if (this.mListView.getParent() != null) {
                ((ViewGroup) this.mListView.getParent()).removeView(this.mListView);
            }
            this.mListView.setMinimumHeight(AttributeResolver.resolveDimensionPixelSize(this.mContext, R$attr.dialogListPreferredItemHeight));
            ViewCompat.setNestedScrollingEnabled(this.mListView, true);
            viewGroup.addView(this.mListView, 0, new ViewGroup.MarginLayoutParams(-1, -2));
            ((NestedScrollViewExpander) viewGroup).setExpandView(this.mListView);
            return;
        }
        int i = R$id.contentView;
        viewGroup.removeView(viewGroup.findViewById(i));
        viewGroup.removeView(frameLayout);
        LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
        linearLayout.setOrientation(1);
        if (this.mListView.getParent() != null) {
            ((ViewGroup) this.mListView.getParent()).removeView(this.mListView);
        }
        ViewCompat.setNestedScrollingEnabled(this.mListView, true);
        linearLayout.addView(this.mListView, 0, new ViewGroup.MarginLayoutParams(-1, -2));
        Point windowSize = WindowUtils.getWindowSize(this.mContext);
        int resolveDimensionPixelSize = AttributeResolver.resolveDimensionPixelSize(this.mContext, R$attr.dialogListPreferredItemHeight);
        int i2 = (int) (windowSize.y * 0.35f);
        boolean z3 = this.mAdapter.getCount() * resolveDimensionPixelSize > i2;
        if (z3) {
            int i3 = resolveDimensionPixelSize * (i2 / resolveDimensionPixelSize);
            this.mListView.setMinimumHeight(i3);
            ViewGroup.LayoutParams layoutParams = this.mListView.getLayoutParams();
            layoutParams.height = i3;
            this.mListView.setLayoutParams(layoutParams);
            linearLayout.addView(frameLayout, new LinearLayout.LayoutParams(-1, -2, 0.0f));
        } else {
            ViewGroup.LayoutParams layoutParams2 = this.mListView.getLayoutParams();
            layoutParams2.height = -2;
            this.mListView.setLayoutParams(layoutParams2);
            linearLayout.addView(frameLayout, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        }
        viewGroup.addView(linearLayout, 0, new ViewGroup.MarginLayoutParams(-1, -2));
        ViewGroup viewGroup3 = (ViewGroup) viewGroup.findViewById(i);
        if (viewGroup3 != null) {
            setupContentView(viewGroup3);
        }
        ((NestedScrollViewExpander) viewGroup).setExpandView(z3 ? null : linearLayout);
    }

    private void setupContentView(ViewGroup viewGroup) {
        CharSequence charSequence;
        this.mMessageView = (TextView) viewGroup.findViewById(R$id.message);
        this.mCommentView = (TextView) viewGroup.findViewById(R$id.comment);
        TextView textView = this.mMessageView;
        if (textView == null || (charSequence = this.mMessage) == null) {
            ((ViewGroup) viewGroup.getParent()).removeView(viewGroup);
            return;
        }
        textView.setText(charSequence);
        TextView textView2 = this.mCommentView;
        if (textView2 != null) {
            CharSequence charSequence2 = this.mComment;
            if (charSequence2 != null) {
                textView2.setText(charSequence2);
            } else {
                textView2.setVisibility(8);
            }
        }
    }

    private boolean setupCustomContent(ViewGroup viewGroup) {
        View view = this.mInflatedView;
        View view2 = null;
        if (view != null && view.getParent() != null) {
            ((ViewGroup) this.mInflatedView.getParent()).removeView(this.mInflatedView);
            this.mInflatedView = null;
        }
        View view3 = this.mView;
        if (view3 != null) {
            view2 = view3;
        } else if (this.mViewLayoutResId != 0) {
            view2 = LayoutInflater.from(this.mContext).inflate(this.mViewLayoutResId, viewGroup, false);
            this.mInflatedView = view2;
        }
        boolean z = view2 != null;
        if (!z || !canTextInput(view2)) {
            this.mWindow.setFlags(131072, 131072);
        }
        if (z) {
            if (view2.getParent() != null) {
                ((ViewGroup) view2.getParent()).removeView(view2);
            }
            viewGroup.addView(view2);
        } else {
            ((ViewGroup) viewGroup.getParent()).removeView(viewGroup);
        }
        return z;
    }

    private void setupImmersiveWindow() {
        this.mWindow.setLayout(-1, -1);
        this.mWindow.setBackgroundDrawableResource(R$color.miuix_appcompat_transparent);
        this.mWindow.setDimAmount(0.0f);
        this.mWindow.setWindowAnimations(0);
        this.mWindow.addFlags(-2147481344);
        int i = Build.VERSION.SDK_INT;
        if (i > 28) {
            Activity associatedActivity = ((AlertDialog) this.mDialog).getAssociatedActivity();
            if (associatedActivity != null) {
                this.mWindow.getAttributes().layoutInDisplayCutoutMode = getCutoutMode(getScreenOrientation(), associatedActivity.getWindow().getAttributes().layoutInDisplayCutoutMode);
            } else {
                this.mWindow.getAttributes().layoutInDisplayCutoutMode = getScreenOrientation() != 2 ? 1 : 2;
            }
        }
        clearFitSystemWindow(this.mWindow.getDecorView());
        if (i >= 30) {
            this.mWindow.getAttributes().setFitInsetsSides(0);
            Activity associatedActivity2 = ((AlertDialog) this.mDialog).getAssociatedActivity();
            if (associatedActivity2 == null || (associatedActivity2.getWindow().getAttributes().flags & MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE) != 0) {
                return;
            }
            this.mWindow.clearFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE);
        }
    }

    private void setupNonImmersiveWindow() {
        int panelWidth = getPanelWidth(isLandscape());
        WindowUtils.getWindowSize(this.mContext, this.mWindowSize);
        if (!shouldLimitWidth() && panelWidth == -1) {
            panelWidth = this.mWindowSize.x - (this.mContext.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_dialog_width_margin) * 2);
        }
        int gravity = getGravity();
        this.mWindow.setGravity(gravity);
        if ((gravity & 80) > 0) {
            this.mWindow.getAttributes().verticalMargin = (this.mContext.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_dialog_bottom_margin) * 1.0f) / this.mWindowSize.y;
        }
        this.mWindow.addFlags(2);
        this.mWindow.setDimAmount(0.3f);
        this.mWindow.setLayout(panelWidth, -2);
        this.mWindow.setBackgroundDrawableResource(R$color.miuix_appcompat_transparent);
        if (isRealTablet()) {
            this.mWindow.setWindowAnimations(R$style.Animation_Dialog_Center);
        }
    }

    private void setupTitle(ViewGroup viewGroup) {
        ImageView imageView = (ImageView) this.mWindow.findViewById(16908294);
        View view = this.mCustomTitleView;
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) this.mCustomTitleView.getParent()).removeView(this.mCustomTitleView);
            }
            viewGroup.addView(this.mCustomTitleView, 0, new ViewGroup.LayoutParams(-1, -2));
            this.mWindow.findViewById(R$id.alertTitle).setVisibility(8);
            imageView.setVisibility(8);
        } else if ((!TextUtils.isEmpty(this.mTitle)) != true || !this.mShowTitle) {
            this.mWindow.findViewById(R$id.alertTitle).setVisibility(8);
            imageView.setVisibility(8);
            viewGroup.setVisibility(8);
        } else {
            TextView textView = (TextView) this.mWindow.findViewById(R$id.alertTitle);
            this.mTitleView = textView;
            textView.setText(this.mTitle);
            int i = this.mIconId;
            if (i != 0) {
                imageView.setImageResource(i);
            } else {
                Drawable drawable = this.mIcon;
                if (drawable != null) {
                    imageView.setImageDrawable(drawable);
                } else {
                    this.mTitleView.setPadding(imageView.getPaddingLeft(), imageView.getPaddingTop(), imageView.getPaddingRight(), imageView.getPaddingBottom());
                    imageView.setVisibility(8);
                }
            }
            if (this.mMessage == null || viewGroup.getVisibility() == 8) {
                return;
            }
            changeTitlePadding(this.mTitleView);
        }
    }

    private void setupView(boolean z) {
        ListAdapter listAdapter;
        if (isDialogImmersive()) {
            this.mDimBg.setOnClickListener(new View.OnClickListener() { // from class: miuix.appcompat.app.AlertController$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AlertController.this.lambda$setupView$1(view);
                }
            });
            updateDialogPanel();
        } else {
            this.mDimBg.setVisibility(8);
        }
        if (!z && !this.mPreferLandscape) {
            this.mParentPanel.post(new Runnable() { // from class: miuix.appcompat.app.AlertController.3
                @Override // java.lang.Runnable
                public void run() {
                    ViewGroup viewGroup = (ViewGroup) AlertController.this.mParentPanel.findViewById(R$id.contentPanel);
                    ViewGroup viewGroup2 = (ViewGroup) AlertController.this.mParentPanel.findViewById(R$id.buttonPanel);
                    if (viewGroup != null) {
                        AlertController.this.updateContent(viewGroup);
                        if (viewGroup2 != null) {
                            AlertController.this.updateButtons(viewGroup2, viewGroup);
                        }
                    }
                }
            });
            return;
        }
        ViewGroup viewGroup = (ViewGroup) this.mParentPanel.findViewById(R$id.topPanel);
        ViewGroup viewGroup2 = (ViewGroup) this.mParentPanel.findViewById(R$id.contentPanel);
        ViewGroup viewGroup3 = (ViewGroup) this.mParentPanel.findViewById(R$id.buttonPanel);
        if (viewGroup2 != null) {
            setupContent(viewGroup2);
        }
        if (viewGroup3 != null) {
            setupButtons(viewGroup3);
        }
        if (viewGroup != null) {
            setupTitle(viewGroup);
        }
        if ((viewGroup == null || viewGroup.getVisibility() == 8) ? false : true) {
            View findViewById = (this.mMessage == null && this.mListView == null) ? null : viewGroup.findViewById(R$id.titleDividerNoCustom);
            if (findViewById != null) {
                findViewById.setVisibility(0);
            }
        }
        ListView listView = this.mListView;
        if (listView != null && (listAdapter = this.mAdapter) != null) {
            listView.setAdapter(listAdapter);
            int i = this.mCheckedItem;
            if (i > -1) {
                listView.setItemChecked(i, true);
                listView.setSelection(i);
            }
        }
        CheckBox checkBox = (CheckBox) this.mParentPanel.findViewById(16908289);
        if (checkBox != null) {
            setupCheckbox(checkBox);
        }
        if (z) {
            return;
        }
        onLayoutReload();
    }

    private void setupWindow() {
        if (isDialogImmersive()) {
            setupImmersiveWindow();
        } else {
            setupNonImmersiveWindow();
        }
    }

    private void setupWindowInsetsAnimation() {
        if (isDialogImmersive()) {
            this.mWindow.setSoftInputMode((this.mWindow.getAttributes().softInputMode & 15) | 48);
            this.mWindow.getDecorView().setWindowInsetsAnimationCallback(new WindowInsetsAnimation.Callback(1) { // from class: miuix.appcompat.app.AlertController.4
                @Override // android.view.WindowInsetsAnimation.Callback
                public void onEnd(WindowInsetsAnimation windowInsetsAnimation) {
                    super.onEnd(windowInsetsAnimation);
                    AlertController.this.mInsetsAnimationPlayed = true;
                    WindowInsets rootWindowInsets = AlertController.this.mWindow.getDecorView().getRootWindowInsets();
                    if (rootWindowInsets != null) {
                        Insets insets = rootWindowInsets.getInsets(WindowInsets.Type.ime());
                        if (insets.bottom <= 0 && AlertController.this.mParentPanel.getTranslationY() < 0.0f) {
                            AlertController.this.translateDialogPanel(0);
                        }
                        AlertController.this.updateParentPanelMarginByWindowInsets(rootWindowInsets);
                        AlertController.this.updateDimBgBottomMargin(insets.bottom);
                    }
                }

                @Override // android.view.WindowInsetsAnimation.Callback
                public void onPrepare(WindowInsetsAnimation windowInsetsAnimation) {
                    super.onPrepare(windowInsetsAnimation);
                    DialogAnimHelper.cancelAnimator();
                    AlertController.this.mInsetsAnimationPlayed = false;
                }

                @Override // android.view.WindowInsetsAnimation.Callback
                public WindowInsets onProgress(WindowInsets windowInsets, List<WindowInsetsAnimation> list) {
                    Insets insets = windowInsets.getInsets(WindowInsets.Type.ime());
                    if (windowInsets.isVisible(WindowInsets.Type.ime())) {
                        if (AlertController.this.mIsDebugEnabled) {
                            Log.d("AlertController", "WindowInsetsAnimation onProgress ime : " + insets.bottom);
                        }
                        int i = insets.bottom - AlertController.this.mPanelAndImeMargin;
                        if (i < 0) {
                            i = 0;
                        }
                        AlertController.this.translateDialogPanel(-i);
                    }
                    AlertController.this.updateDimBgBottomMargin(insets.bottom);
                    return windowInsets;
                }

                @Override // android.view.WindowInsetsAnimation.Callback
                public WindowInsetsAnimation.Bounds onStart(WindowInsetsAnimation windowInsetsAnimation, WindowInsetsAnimation.Bounds bounds) {
                    AlertController.this.mPanelAndImeMargin = (int) (r0.getDialogPanelMargin() + AlertController.this.mParentPanel.getTranslationY());
                    if (AlertController.this.mIsDebugEnabled) {
                        Log.d("AlertController", "WindowInsetsAnimation onStart mPanelAndImeMargin : " + AlertController.this.mPanelAndImeMargin);
                    }
                    if (AlertController.this.mPanelAndImeMargin <= 0) {
                        AlertController.this.mPanelAndImeMargin = 0;
                    }
                    return super.onStart(windowInsetsAnimation, bounds);
                }
            });
            this.mWindow.getDecorView().setOnApplyWindowInsetsListener(new AnonymousClass5());
            this.mSetupWindowInsetsAnimation = true;
        }
    }

    private boolean shouldLimitWidth() {
        return this.mContext.getApplicationContext().getResources().getConfiguration().screenWidthDp >= 376;
    }

    private boolean shouldUseLandscapePanel() {
        int i = !TextUtils.isEmpty(this.mButtonNegativeText) ? 1 : 0;
        if (!TextUtils.isEmpty(this.mButtonNeutralText)) {
            i++;
        }
        if (!TextUtils.isEmpty(this.mButtonPositiveText)) {
            i++;
        }
        List<ButtonInfo> list = this.mExtraButtonList;
        if (list != null) {
            i += list.size();
        }
        if (i == 0) {
            return false;
        }
        WindowUtils.getWindowSize(this.mContext, this.mWindowSize);
        Point point = this.mWindowSize;
        int i2 = point.x;
        return i2 >= this.mPanelMaxWidthLand && i2 * 2 > point.y && this.mPreferLandscape;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void translateDialogPanel(int i) {
        if (this.mIsDebugEnabled) {
            Log.d("AlertController", "The DialogPanel transitionY for : " + i);
        }
        this.mParentPanel.animate().cancel();
        this.mParentPanel.setTranslationY(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateButtons(ViewGroup viewGroup, ViewGroup viewGroup2) {
        Point point = new Point();
        Point point2 = new Point();
        WindowUtils.getScreenAndWindowSize(this.mContext, point, point2);
        if (!(((float) point2.y) <= ((float) Math.max(point.x, point.y)) * 0.3f)) {
            ViewGroup viewGroup3 = (ViewGroup) viewGroup.getParent();
            if (viewGroup3 == this.mParentPanel) {
                return;
            }
            if (viewGroup3 != null) {
                viewGroup3.removeView(viewGroup);
            }
            this.mParentPanel.addView(viewGroup);
            return;
        }
        ViewGroup viewGroup4 = (ViewGroup) viewGroup.getParent();
        if (viewGroup4 != this.mParentPanel) {
            return;
        }
        if (viewGroup4 != null) {
            viewGroup4.removeView(viewGroup);
        }
        if (viewGroup2 != null) {
            viewGroup2.addView(viewGroup);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateContent(ViewGroup viewGroup) {
        FrameLayout frameLayout = (FrameLayout) viewGroup.findViewById(16908331);
        boolean z = frameLayout != null && frameLayout.getChildCount() > 0;
        Point windowSize = WindowUtils.getWindowSize(this.mContext);
        if (this.mListView == null || !z) {
            return;
        }
        int resolveDimensionPixelSize = AttributeResolver.resolveDimensionPixelSize(this.mContext, R$attr.dialogListPreferredItemHeight);
        int i = (int) (windowSize.y * 0.35f);
        if (!(this.mAdapter.getCount() * resolveDimensionPixelSize > i)) {
            ViewGroup.LayoutParams layoutParams = this.mListView.getLayoutParams();
            layoutParams.height = -2;
            this.mListView.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
            layoutParams2.height = 0;
            layoutParams2.weight = 1.0f;
            frameLayout.setLayoutParams(layoutParams2);
            ((NestedScrollViewExpander) viewGroup).setExpandView((View) frameLayout.getParent());
            viewGroup.requestLayout();
            return;
        }
        int i2 = resolveDimensionPixelSize * (i / resolveDimensionPixelSize);
        this.mListView.setMinimumHeight(i2);
        ViewGroup.LayoutParams layoutParams3 = this.mListView.getLayoutParams();
        layoutParams3.height = i2;
        this.mListView.setLayoutParams(layoutParams3);
        LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams4.height = -2;
        layoutParams4.weight = 0.0f;
        frameLayout.setLayoutParams(layoutParams4);
        ((NestedScrollViewExpander) viewGroup).setExpandView(null);
        viewGroup.requestLayout();
    }

    private void updateDialogPanel() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getPanelWidth(isLandscape()), -2);
        layoutParams.gravity = getGravity();
        int dimensionPixelSize = shouldLimitWidth() ? 0 : this.mContext.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_dialog_width_margin);
        layoutParams.rightMargin = dimensionPixelSize;
        layoutParams.leftMargin = dimensionPixelSize;
        this.mPanelOriginLeftMargin = dimensionPixelSize;
        this.mPanelOriginRightMargin = dimensionPixelSize;
        this.mParentPanel.setLayoutParams(layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDialogPanelByWindowInsets(WindowInsets windowInsets) {
        updateParentPanelMarginByWindowInsets(windowInsets);
        if (isNeedUpdateDialogPanelTranslationY()) {
            Insets insets = windowInsets.getInsets(WindowInsets.Type.ime());
            if (this.mIsDebugEnabled) {
                Log.d("AlertController", "======================Debug for checkTranslateDialogPanel======================");
                Log.d("AlertController", "The imeInset info: " + insets.toString());
            }
            updateDimBgBottomMargin(insets.bottom);
            updateDialogPanelTranslationYByIme(insets.bottom);
            if (this.mIsDebugEnabled) {
                Log.d("AlertController", "===================End of Debug for checkTranslateDialogPanel===================");
            }
        }
    }

    private void updateDialogPanelTranslationYByIme(int i) {
        if (i <= 0) {
            if (this.mParentPanel.getTranslationY() < 0.0f) {
                translateDialogPanel(0);
                return;
            }
            return;
        }
        int dialogPanelMargin = (int) (getDialogPanelMargin() + this.mParentPanel.getTranslationY());
        this.mPanelAndImeMargin = dialogPanelMargin;
        if (dialogPanelMargin <= 0) {
            this.mPanelAndImeMargin = 0;
        }
        int i2 = this.mPanelAndImeMargin;
        if (i2 >= i) {
            translateDialogPanel(0);
        } else if (!this.mIsDialogAnimating) {
            translateDialogPanel(i2 - i);
        } else {
            this.mParentPanel.animate().cancel();
            this.mParentPanel.animate().setDuration(200L).translationY(this.mPanelAndImeMargin - i).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDimBgBottomMargin(int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mDimBg.getLayoutParams();
        if (marginLayoutParams.bottomMargin != i) {
            marginLayoutParams.bottomMargin = i;
            this.mDimBg.requestLayout();
        }
    }

    private void updateMinorScreenSize() {
        Configuration configuration = this.mContext.getResources().getConfiguration();
        int min = (int) (Math.min(configuration.screenWidthDp, configuration.screenHeightDp) * (configuration.densityDpi / 160.0f));
        if (min > 0) {
            this.mScreenMinorSize = min;
            return;
        }
        Point point = new Point();
        this.mWindowManager.getDefaultDisplay().getSize(point);
        this.mScreenMinorSize = Math.min(point.x, point.y);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateParentPanelMarginByWindowInsets(WindowInsets windowInsets) {
        if (isRealTablet() || windowInsets == null) {
            return;
        }
        Insets insets = windowInsets.getInsets(WindowInsets.Type.navigationBars());
        Insets insets2 = windowInsets.getInsets(WindowInsets.Type.statusBars());
        Insets insets3 = windowInsets.getInsets(WindowInsets.Type.displayCutout());
        if (this.mIsDebugEnabled) {
            Log.d("AlertController", "updateParentPanel navigationBar " + insets);
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mParentPanel.getLayoutParams();
        int i = insets2.top;
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.miuix_appcompat_dialog_bottom_margin);
        int max = Math.max(Math.max(i, dimensionPixelSize), insets3.top);
        int x = (int) this.mParentPanel.getX();
        int width = (int) ((this.mDialogRootView.getWidth() - this.mParentPanel.getX()) - this.mParentPanel.getWidth());
        int max2 = Math.max(insets3.left, insets.left);
        boolean z = false;
        int max3 = x >= max2 ? marginLayoutParams.leftMargin : Math.max(0, (max2 - x) - this.mPanelOriginLeftMargin);
        int max4 = Math.max(insets3.right, insets.right);
        int max5 = width >= max4 ? marginLayoutParams.rightMargin : Math.max(0, (max4 - width) - this.mPanelOriginRightMargin);
        int i2 = dimensionPixelSize + insets.bottom;
        boolean z2 = true;
        if (marginLayoutParams.topMargin != max) {
            marginLayoutParams.topMargin = max;
            z = true;
        }
        if (marginLayoutParams.bottomMargin != i2) {
            marginLayoutParams.bottomMargin = i2;
            z = true;
        }
        if (marginLayoutParams.leftMargin != max3) {
            marginLayoutParams.leftMargin = max3;
            z = true;
        }
        if (marginLayoutParams.rightMargin != max5) {
            marginLayoutParams.rightMargin = max5;
        } else {
            z2 = z;
        }
        if (z2) {
            this.mParentPanel.requestLayout();
        }
    }

    private void updateWindowCutoutMode() {
        int screenOrientation = getScreenOrientation();
        if (Build.VERSION.SDK_INT <= 28 || this.mScreenOrientation == screenOrientation) {
            return;
        }
        this.mScreenOrientation = screenOrientation;
        Activity associatedActivity = ((AlertDialog) this.mDialog).getAssociatedActivity();
        if (associatedActivity != null) {
            int cutoutMode = getCutoutMode(screenOrientation, associatedActivity.getWindow().getAttributes().layoutInDisplayCutoutMode);
            if (this.mWindow.getAttributes().layoutInDisplayCutoutMode != cutoutMode) {
                this.mWindow.getAttributes().layoutInDisplayCutoutMode = cutoutMode;
                if (this.mDialog.isShowing()) {
                    this.mWindowManager.updateViewLayout(this.mWindow.getDecorView(), this.mWindow.getAttributes());
                    return;
                }
                return;
            }
            return;
        }
        int i = getScreenOrientation() != 2 ? 1 : 2;
        if (this.mWindow.getAttributes().layoutInDisplayCutoutMode != i) {
            this.mWindow.getAttributes().layoutInDisplayCutoutMode = i;
            if (this.mDialog.isShowing()) {
                this.mWindowManager.updateViewLayout(this.mWindow.getDecorView(), this.mWindow.getAttributes());
            }
        }
    }

    public void dismiss(DialogAnimHelper.OnDismiss onDismiss) {
        if (Build.VERSION.SDK_INT >= 30) {
            cleanWindowInsetsAnimation();
        }
        DialogParentPanel2 dialogParentPanel2 = this.mParentPanel;
        if (dialogParentPanel2 == null) {
            if (onDismiss != null) {
                onDismiss.end();
            }
        } else if (dialogParentPanel2.isAttachedToWindow()) {
            checkAndClearFocus();
            DialogAnimHelper.executeDismissAnim(this.mParentPanel, this.mDimBg, onDismiss);
        } else {
            Log.d("AlertController", "dialog is not attached to window when dismiss is invoked");
            try {
                ((AlertDialog) this.mDialog).realDismiss();
            } catch (IllegalArgumentException e) {
                Log.wtf("AlertController", "Not catch the dialog will throw the illegalArgumentException (In Case cause the crash , we expect it should be caught)", e);
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return keyEvent.getKeyCode() == 82;
    }

    public Button getButton(int i) {
        if (i != -3) {
            if (i != -2) {
                if (i != -1) {
                    List<ButtonInfo> list = this.mExtraButtonList;
                    if (list == null || list.isEmpty()) {
                        return null;
                    }
                    for (ButtonInfo buttonInfo : this.mExtraButtonList) {
                        if (buttonInfo.mWhich == i) {
                            return buttonInfo.mButton;
                        }
                    }
                    return null;
                }
                return this.mButtonPositive;
            }
            return this.mButtonNegative;
        }
        return this.mButtonNeutral;
    }

    public int getIconAttributeResId(int i) {
        TypedValue typedValue = new TypedValue();
        this.mContext.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.resourceId;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public TextView getMessageView() {
        return this.mMessageView;
    }

    public void installContent() {
        this.mDialog.setContentView(this.mAlertDialogLayout);
        this.mDialogRootView = (DialogRootView) this.mWindow.findViewById(R$id.dialog_root_view);
        this.mDimBg = this.mWindow.findViewById(R$id.dialog_dim_bg);
        this.mDialogRootView.setConfigurationChangedCallback(new DialogRootView.ConfigurationChangedCallback() { // from class: miuix.appcompat.app.AlertController$$ExternalSyntheticLambda1
            @Override // miuix.appcompat.internal.widget.DialogRootView.ConfigurationChangedCallback
            public final void onConfigurationChanged(Configuration configuration) {
                AlertController.this.lambda$installContent$0(configuration);
            }
        });
        setupWindow();
        setupView(true);
    }

    public boolean isChecked() {
        boolean isChecked = ((CheckBox) this.mWindow.findViewById(16908289)).isChecked();
        this.mIsChecked = isChecked;
        return isChecked;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDialogImmersive() {
        return this.mIsEnableImmersive && Build.VERSION.SDK_INT >= 30;
    }

    public void onAttachedToWindow() {
        reInitLandConfig();
        if (isDialogImmersive()) {
            updateDialogPanel();
        } else {
            setupNonImmersiveWindow();
        }
        if (Build.VERSION.SDK_INT >= 30) {
            setupWindowInsetsAnimation();
        }
    }

    public void onConfigurationChanged() {
        if (!checkThread()) {
            Log.w("AlertController", "dialog is created in thread:" + this.mCreateThread + ", but onConfigurationChanged is called from different thread:" + Thread.currentThread() + ", so this onConfigurationChanged call should be ignore");
            return;
        }
        if (isDialogImmersive()) {
            this.mWindow.getDecorView().removeOnLayoutChangeListener(this.mLayoutChangeListener);
        }
        if (this.mWindow.getDecorView().isAttachedToWindow()) {
            reInitLandConfig();
            if (isDialogImmersive()) {
                updateWindowCutoutMode();
            } else {
                setupNonImmersiveWindow();
            }
            setupView(false);
        }
        if (isDialogImmersive()) {
            this.mWindow.getDecorView().addOnLayoutChangeListener(this.mLayoutChangeListener);
        }
    }

    public void onDetachedFromWindow() {
        if (AnimHelper.isDialogDebugInAndroidUIThreadEnabled()) {
            return;
        }
        Folme.clean(this.mParentPanel, this.mDimBg);
        translateDialogPanel(0);
    }

    public void onStart() {
        if (isDialogImmersive()) {
            reInitLandConfig();
            updateWindowCutoutMode();
            DialogAnimHelper.executeShowAnim(this.mParentPanel, this.mDimBg, isLandscape(), this.mShowAnimListenerWrapper);
            this.mWindow.getDecorView().addOnLayoutChangeListener(this.mLayoutChangeListener);
        }
    }

    public void onStop() {
        if (isDialogImmersive()) {
            this.mWindow.getDecorView().removeOnLayoutChangeListener(this.mLayoutChangeListener);
        }
    }

    public void setButton(int i, CharSequence charSequence, DialogInterface.OnClickListener onClickListener, Message message) {
        if (message == null && onClickListener != null) {
            message = this.mHandler.obtainMessage(i, onClickListener);
        }
        if (i == -3) {
            this.mButtonNeutralText = charSequence;
            this.mButtonNeutralMessage = message;
        } else if (i == -2) {
            this.mButtonNegativeText = charSequence;
            this.mButtonNegativeMessage = message;
        } else if (i != -1) {
            throw new IllegalArgumentException("Button does not exist");
        } else {
            this.mButtonPositiveText = charSequence;
            this.mButtonPositiveMessage = message;
        }
    }

    public void setCancelable(boolean z) {
        this.mCancelable = z;
    }

    public void setCanceledOnTouchOutside(boolean z) {
        this.mCanceledOnTouchOutside = z;
    }

    public void setCheckBox(boolean z, CharSequence charSequence) {
        this.mIsChecked = z;
        this.mCheckBoxMessage = charSequence;
    }

    public void setComment(CharSequence charSequence) {
        this.mComment = charSequence;
        TextView textView = this.mCommentView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public void setCustomTitle(View view) {
        this.mCustomTitleView = view;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEnableImmersive(boolean z) {
        this.mIsEnableImmersive = z;
    }

    public void setIcon(int i) {
        this.mIcon = null;
        this.mIconId = i;
    }

    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        this.mIconId = 0;
    }

    public void setMessage(CharSequence charSequence) {
        this.mMessage = charSequence;
        TextView textView = this.mMessageView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    void setPreferLandscape(boolean z) {
        this.mPreferLandscape = z;
    }

    public void setShowAnimListener(AlertDialog.OnDialogShowAnimListener onDialogShowAnimListener) {
        this.mShowAnimListener = onDialogShowAnimListener;
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public void setView(int i) {
        this.mView = null;
        this.mViewLayoutResId = i;
    }

    public void setView(View view) {
        this.mView = view;
        this.mViewLayoutResId = 0;
    }
}
