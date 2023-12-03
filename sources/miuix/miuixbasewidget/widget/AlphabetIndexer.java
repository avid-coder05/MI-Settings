package miuix.miuixbasewidget.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.ViewUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import miuix.animation.Folme;
import miuix.animation.IVisibleStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.ViewProperty;
import miuix.miuixbasewidget.R$array;
import miuix.miuixbasewidget.R$attr;
import miuix.miuixbasewidget.R$color;
import miuix.miuixbasewidget.R$dimen;
import miuix.miuixbasewidget.R$drawable;
import miuix.miuixbasewidget.R$string;
import miuix.miuixbasewidget.R$style;
import miuix.miuixbasewidget.R$styleable;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes5.dex */
public class AlphabetIndexer extends LinearLayout {
    private final int INVALID_INDEX;
    private Adapter mAdapter;
    private boolean mCancelOverlayTextColorAnim;
    private int mCurVirtualViewId;
    private boolean mDrawOverlay;
    private boolean mEnableAutoDismiss;
    private int mGroupCount;
    private int mGroupItemCount;
    private Handler mHandler;
    private int mIndexWidth;
    private SectionIndexer mIndexer;
    private int mItemHeight;
    private int mItemMargin;
    private int mLastAlphabetIndex;
    private View mLastSelectedItem;
    private int mLeftCount;
    private int mListScrollState;
    private int mMaxItemMargin;
    private int mMinItemMargin;
    private int mOmitItemHeight;
    private TextView mOverlay;
    private Drawable mOverlayBackground;
    private int mOverlayHeight;
    private AnimConfig mOverlayHideAnimConfig;
    private AnimConfig mOverlayShowAnimConfig;
    private int mOverlayTextAppearanceRes;
    private int mOverlayTextColor;
    private TextPaint mOverlayTextPaint;
    private int mOverlayTextSize;
    private int mOverlayWidth;
    private int mScreenHeightDp;
    HashMap<Object, Integer> mSectionMap;
    private int mSelectedAlphaIndex;
    private TextHighlighter mTextHighlighter;
    private ViewTreeObserver.OnPreDrawListener mUpdateItemsListener;
    private int mVerticalPosition;
    private int mViewHeight;

    /* loaded from: classes5.dex */
    public interface Adapter {
        int getFirstVisibleItemPosition();

        int getItemCount();

        int getListHeaderCount();

        void scrollToPosition(int i);

        void stopScroll();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class TextHighlighter {
        int mActivatedColor;
        int mHighlightColor;
        int mIndexerTextSize;
        String[] mIndexes;
        int mNormalColor;

        TextHighlighter(Context context, TypedArray typedArray) {
            Resources resources = context.getResources();
            CharSequence[] textArray = typedArray.getTextArray(R$styleable.MiuixAppcompatAlphabetIndexer_miuixAppcompatIndexerTable);
            if (textArray != null) {
                this.mIndexes = new String[textArray.length];
                int length = textArray.length;
                int i = 0;
                int i2 = 0;
                while (i < length) {
                    this.mIndexes[i2] = textArray[i].toString();
                    i++;
                    i2++;
                }
            } else {
                this.mIndexes = resources.getStringArray(R$array.alphabet_table);
            }
            ColorStateList colorStateList = AppCompatResources.getColorStateList(context, typedArray.getResourceId(R$styleable.MiuixAppcompatAlphabetIndexer_miuixAppcompatIndexerTextColorList, R$color.miuix_appcompat_alphabet_indexer_text_light));
            this.mHighlightColor = colorStateList.getColorForState(new int[]{16842913}, resources.getColor(R$color.miuix_appcompat_alphabet_indexer_highlight_text_color));
            this.mActivatedColor = colorStateList.getColorForState(new int[]{16843518}, resources.getColor(R$color.miuix_appcompat_alphabet_indexer_activated_text_color));
            this.mNormalColor = colorStateList.getColorForState(new int[0], resources.getColor(R$color.miuix_appcompat_alphabet_indexer_text_color));
            this.mIndexerTextSize = typedArray.getDimensionPixelSize(R$styleable.MiuixAppcompatAlphabetIndexer_miuixAppcompatIndexerTextSize, resources.getDimensionPixelSize(R$dimen.miuix_appcompat_alphabet_indexer_text_size));
        }
    }

    public AlphabetIndexer(Context context) {
        this(context, null);
    }

    public AlphabetIndexer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.miuixAppcompatAlphabetIndexerStyle);
    }

    public AlphabetIndexer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.INVALID_INDEX = -1;
        this.mGroupItemCount = 1;
        this.mLeftCount = 0;
        this.mGroupCount = 0;
        this.mSelectedAlphaIndex = -1;
        this.mSectionMap = new HashMap<>();
        this.mListScrollState = 0;
        this.mUpdateItemsListener = new ViewTreeObserver.OnPreDrawListener() { // from class: miuix.miuixbasewidget.widget.AlphabetIndexer.1
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                AlphabetIndexer.this.getViewTreeObserver().removeOnPreDrawListener(this);
                AlphabetIndexer.this.updateItemsAfterHeightChanged();
                return false;
            }
        };
        this.mEnableAutoDismiss = true;
        this.mScreenHeightDp = -1;
        this.mHandler = new Handler() { // from class: miuix.miuixbasewidget.widget.AlphabetIndexer.4
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 1) {
                    return;
                }
                AlphabetIndexer.this.hideOverlay();
            }
        };
        this.mCurVirtualViewId = -1;
        parseAttrs(attributeSet, i);
        init();
    }

    private int calculateIndex(float f) {
        int i = this.mItemMargin;
        int i2 = this.mItemHeight + (i * 2);
        View childAt = getChildAt(0);
        if (childAt != null) {
            i = ((ViewGroup.MarginLayoutParams) childAt.getLayoutParams()).topMargin;
            i2 = childAt.getHeight() + (i * 2);
        }
        int length = this.mTextHighlighter.mIndexes.length;
        if (length != getChildCount()) {
            float f2 = i2;
            if (f > f2) {
                int i3 = i2 * 2;
                if (f > (getHeight() - getPaddingTop()) - i3) {
                    return (length - 2) + (((int) (f - ((getHeight() - getPaddingTop()) - i3))) / i2);
                }
                int i4 = (i * 2) + i2 + this.mOmitItemHeight + (this.mItemMargin * 2);
                int i5 = (int) (f - f2);
                int i6 = i5 / i4;
                int i7 = i5 % i4 > i2 ? 1 : 0;
                int i8 = this.mLeftCount;
                if (i6 < i8) {
                    return ((this.mGroupItemCount + 1) * i6) + 1 + i7;
                }
                int i9 = this.mGroupItemCount;
                return ((i9 + 1) * i8) + 1 + (i9 * (i6 - i8)) + i7;
            }
        }
        return (int) (f / i2);
    }

    private int calculateOverlayPosition(int i) {
        View childAt = getChildAt(getChildIndex(i));
        if (childAt == null) {
            return 0;
        }
        int top = (childAt.getTop() + childAt.getBottom()) / 2;
        if (top <= 0) {
            top = (int) (((r5 + 1 + 0.5d) * this.mItemHeight) + getPaddingTop());
        }
        return top + getMarginTop();
    }

    private void clearLastChecked(int i) {
        if (i < 0) {
            return;
        }
        View childAt = getChildAt(getChildIndex(i));
        if (childAt instanceof TextView) {
            ((TextView) childAt).setTextColor(this.mTextHighlighter.mNormalColor);
        } else if (childAt instanceof ImageView) {
            ((ImageView) childAt).setImageResource(R$drawable.miuix_ic_omit);
        }
    }

    private void constructItem(int i) {
        this.mItemMargin = i;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.gravity = 1;
        layoutParams.bottomMargin = i;
        layoutParams.topMargin = i;
        layoutParams.weight = 1.0f;
        Typeface create = Typeface.create("mipro-medium", 0);
        for (String str : this.mTextHighlighter.mIndexes) {
            TextView textView = new TextView(getContext());
            textView.setTypeface(create);
            textView.setGravity(17);
            textView.setHeight(this.mItemHeight);
            textView.setIncludeFontPadding(false);
            textView.setTextColor(this.mTextHighlighter.mNormalColor);
            textView.setTextSize(0, this.mTextHighlighter.mIndexerTextSize);
            if (TextUtils.equals(str, "!")) {
                str = "♥";
            }
            textView.setText(str);
            textView.setImportantForAccessibility(2);
            attachViewToParent(textView, -1, layoutParams);
        }
    }

    private void constructItemWithOmit(int i) {
        int i2;
        int i3;
        int paddingTop = (i - getPaddingTop()) - getPaddingBottom();
        int length = this.mTextHighlighter.mIndexes.length;
        int i4 = this.mItemHeight;
        int i5 = this.mMinItemMargin;
        int i6 = i4 + (i5 * 2);
        int i7 = this.mOmitItemHeight + i6 + (i5 * 2);
        int i8 = paddingTop - (i6 * 3);
        int i9 = i8 / i7;
        this.mGroupCount = i9;
        if (i9 < 1) {
            this.mGroupCount = 1;
        }
        int i10 = i8 % i7;
        int i11 = length - 3;
        int i12 = this.mGroupCount;
        int i13 = i11 / i12;
        this.mGroupItemCount = i13;
        if (i13 < 2) {
            this.mGroupItemCount = 2;
            int i14 = i11 / 2;
            i10 += i7 * (i12 - i14);
            this.mGroupCount = i14;
        }
        int i15 = this.mGroupItemCount;
        int i16 = this.mGroupCount;
        this.mLeftCount = i11 - (i15 * i16);
        this.mItemMargin = i5;
        if (i10 > 0) {
            this.mItemMargin = Math.min(i5, ((i10 / 2) / ((i16 * 2) + 3)) + i5);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.gravity = 1;
        int i17 = this.mItemMargin;
        layoutParams.bottomMargin = i17;
        layoutParams.topMargin = i17;
        layoutParams.weight = 1.0f;
        Typeface create = Typeface.create("mipro-medium", 0);
        for (int i18 = 0; i18 < length; i18++) {
            int i19 = this.mGroupItemCount;
            int i20 = this.mLeftCount;
            if (i18 < (i19 + 1) * i20) {
                i19++;
                i2 = i18;
            } else {
                i2 = i18 - ((i19 + 1) * i20);
            }
            if (i18 <= 1 || i18 >= length - 2 || (i3 = (i2 - 1) % i19) == 0) {
                String str = this.mTextHighlighter.mIndexes[i18];
                TextView textView = new TextView(getContext());
                textView.setTypeface(create);
                textView.setGravity(17);
                textView.setHeight(this.mItemHeight);
                textView.setIncludeFontPadding(false);
                textView.setTextColor(this.mTextHighlighter.mNormalColor);
                textView.setTextSize(0, this.mTextHighlighter.mIndexerTextSize);
                if (TextUtils.equals(str, "!")) {
                    str = "♥";
                }
                textView.setText(str);
                textView.setImportantForAccessibility(2);
                attachViewToParent(textView, -1, layoutParams);
            } else if (i3 == 1) {
                ImageView imageView = new ImageView(getContext());
                imageView.setMaxHeight(this.mOmitItemHeight);
                imageView.setMaxWidth(this.mOmitItemHeight);
                imageView.setImageResource(R$drawable.miuix_ic_omit);
                imageView.setImportantForAccessibility(2);
                attachViewToParent(imageView, -1, layoutParams);
            }
        }
    }

    private void drawThumbInternal(CharSequence charSequence, float f) {
        if (this.mAdapter == null || this.mOverlay == null) {
            return;
        }
        this.mCancelOverlayTextColorAnim = true;
        if (TextUtils.equals(charSequence, "!")) {
            charSequence = "♥";
        }
        if (!TextUtils.equals(this.mOverlay.getText(), charSequence)) {
            HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_MESH_NORMAL);
        }
        this.mOverlay.setTranslationY(f - getMarginTop());
        updateOverlayTextAlpha(1.0f);
        this.mOverlay.setText(charSequence);
        this.mOverlay.setPaddingRelative((this.mOverlayHeight - ((int) this.mOverlayTextPaint.measureText(charSequence.toString()))) / 2, 0, 0, 0);
        this.mOverlay.setVisibility(0);
        showOverlay();
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x003b, code lost:
    
        if ((r7 % r3) == 0) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0055, code lost:
    
        if ((r7 % r3) == 0) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0057, code lost:
    
        r4 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0058, code lost:
    
        r2 = r0 + r4;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int getChildIndex(int r7) {
        /*
            r6 = this;
            miuix.miuixbasewidget.widget.AlphabetIndexer$TextHighlighter r0 = r6.mTextHighlighter
            java.lang.String[] r0 = r0.mIndexes
            int r0 = r0.length
            int r1 = r0 + (-1)
            if (r7 <= r1) goto Lb
            r2 = r1
            goto Lc
        Lb:
            r2 = r7
        Lc:
            int r3 = r6.getChildCount()
            if (r3 == r0) goto L5a
            int r3 = r6.mGroupItemCount
            r4 = 1
            if (r3 <= r4) goto L5a
            if (r7 <= r4) goto L5a
            int r0 = r0 + (-2)
            r5 = 0
            if (r7 < r0) goto L2a
            int r7 = r6.mGroupCount
            int r7 = r7 * 2
            int r7 = r7 + r4
            if (r2 != r1) goto L26
            goto L27
        L26:
            r4 = r5
        L27:
            int r2 = r7 + r4
            goto L5a
        L2a:
            int r0 = r6.mLeftCount
            if (r0 <= 0) goto L4e
            int r1 = r3 + 1
            int r1 = r1 * r0
            if (r7 >= r1) goto L3e
            int r3 = r3 + r4
            int r7 = r7 - r4
            int r0 = r7 / r3
            int r7 = r7 % r3
            int r0 = r0 * 2
            int r0 = r0 + r4
            if (r7 != 0) goto L58
            goto L57
        L3e:
            int r1 = r7 - r0
            int r1 = r1 - r4
            int r1 = r1 / r3
            int r7 = r7 - r0
            int r7 = r7 - r4
            int r7 = r7 % r3
            int r1 = r1 * 2
            int r1 = r1 + r4
            if (r7 != 0) goto L4b
            r4 = r5
        L4b:
            int r2 = r1 + r4
            goto L5a
        L4e:
            int r7 = r7 - r4
            int r0 = r7 / r3
            int r7 = r7 % r3
            int r0 = r0 * 2
            int r0 = r0 + r4
            if (r7 != 0) goto L58
        L57:
            r4 = r5
        L58:
            int r2 = r0 + r4
        L5a:
            int r6 = r6.normalizeIndex(r2)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.miuixbasewidget.widget.AlphabetIndexer.getChildIndex(int):int");
    }

    private int getIndex(String str) {
        int i = this.mLastAlphabetIndex;
        int i2 = 0;
        while (true) {
            String[] strArr = this.mTextHighlighter.mIndexes;
            if (i2 >= strArr.length) {
                break;
            }
            if (TextUtils.equals(str, strArr[i2])) {
                i = i2;
            }
            i2++;
        }
        if (i == -1) {
            return 0;
        }
        return i;
    }

    private int getListOffset() {
        Adapter adapter = this.mAdapter;
        if (adapter == null) {
            return 0;
        }
        return adapter.getListHeaderCount();
    }

    private int getMarginTop() {
        return ((ViewGroup.MarginLayoutParams) getLayoutParams()).topMargin;
    }

    private int getMarinEnd() {
        return ((ViewGroup.MarginLayoutParams) getLayoutParams()).getMarginEnd();
    }

    private int getPosition(int i, SectionIndexer sectionIndexer) {
        Object[] sections = sectionIndexer == null ? null : sectionIndexer.getSections();
        if (sections == null || (getHeight() - getPaddingTop()) - getPaddingBottom() <= 0 || i < 0) {
            return -1;
        }
        if (i >= this.mTextHighlighter.mIndexes.length) {
            return sections.length;
        }
        this.mSectionMap.clear();
        for (int i2 = 0; i2 < sections.length; i2++) {
            this.mSectionMap.put(sections[i2].toString().toUpperCase(), Integer.valueOf(i2));
        }
        String[] strArr = this.mTextHighlighter.mIndexes;
        int i3 = 0;
        while (true) {
            int i4 = i3 + i;
            if (i4 >= strArr.length && i < i3) {
                return 0;
            }
            int i5 = i - i3;
            if (i4 < strArr.length && this.mSectionMap.containsKey(strArr[i4])) {
                return this.mSectionMap.get(strArr[i4]).intValue();
            }
            if (i5 >= 0 && this.mSectionMap.containsKey(strArr[i5])) {
                return this.mSectionMap.get(strArr[i5]).intValue();
            }
            i3++;
        }
    }

    private SectionIndexer getSectionIndexer() {
        return this.mIndexer;
    }

    private boolean hasShown() {
        TextView textView = this.mOverlay;
        return textView != null && textView.getVisibility() == 0 && this.mOverlay.getAlpha() == 1.0f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideOverlay() {
        TextView textView = this.mOverlay;
        if (textView != null) {
            Folme.useAt(textView).visible().setFlags(1L).setScale(1.0f, IVisibleStyle.VisibleType.SHOW).setScale(0.0f, IVisibleStyle.VisibleType.HIDE).hide(this.mOverlayHideAnimConfig);
        }
    }

    private void init() {
        this.mVerticalPosition = 8388613;
        setGravity(1);
        setOrientation(1);
        initAnimConfig();
        constructItem(this.mMaxItemMargin);
        setClickable(true);
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.mScreenHeightDp = getResources().getConfiguration().screenHeightDp;
    }

    private void initAnimConfig() {
        AnimConfig animConfig = new AnimConfig();
        this.mOverlayShowAnimConfig = animConfig;
        animConfig.addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.AlphabetIndexer.2
            @Override // miuix.animation.listener.TransitionListener
            public void onComplete(Object obj) {
                super.onComplete(obj);
                if (AlphabetIndexer.this.isPressed() || !AlphabetIndexer.this.mEnableAutoDismiss) {
                    return;
                }
                AlphabetIndexer.this.stop(0);
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
                for (UpdateInfo updateInfo : collection) {
                    if (updateInfo.property == ViewProperty.SCALE_X) {
                        AlphabetIndexer.this.updateOverlayTranslationX(updateInfo.getFloatValue());
                        return;
                    }
                }
            }
        });
        AnimConfig animConfig2 = new AnimConfig();
        this.mOverlayHideAnimConfig = animConfig2;
        animConfig2.addListeners(new TransitionListener() { // from class: miuix.miuixbasewidget.widget.AlphabetIndexer.3
            @Override // miuix.animation.listener.TransitionListener
            public void onBegin(Object obj, Collection<UpdateInfo> collection) {
                super.onBegin(obj, collection);
                Iterator<UpdateInfo> it = collection.iterator();
                while (it.hasNext()) {
                    if (it.next().property == ViewProperty.AUTO_ALPHA) {
                        AlphabetIndexer.this.mCancelOverlayTextColorAnim = false;
                        return;
                    }
                }
            }

            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                super.onUpdate(obj, collection);
                for (UpdateInfo updateInfo : collection) {
                    FloatProperty floatProperty = updateInfo.property;
                    if (floatProperty == ViewProperty.SCALE_X) {
                        AlphabetIndexer.this.updateOverlayTranslationX(updateInfo.getFloatValue());
                    } else if (floatProperty == ViewProperty.AUTO_ALPHA && !AlphabetIndexer.this.mCancelOverlayTextColorAnim) {
                        AlphabetIndexer.this.updateOverlayTextAlpha(updateInfo.getFloatValue());
                    }
                }
            }
        });
    }

    private int normalizeIndex(int i) {
        if (i < 0) {
            return 0;
        }
        return i >= getChildCount() ? getChildCount() - 1 : i;
    }

    private void parseAttrs(AttributeSet attributeSet, int i) {
        Resources resources = getContext().getResources();
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.MiuixAppcompatAlphabetIndexer, i, R$style.Widget_AlphabetIndexer_Starred_DayNight);
        this.mTextHighlighter = new TextHighlighter(getContext(), obtainStyledAttributes);
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.MiuixAppcompatAlphabetIndexer_miuixAppcompatDrawOverlay, true);
        this.mDrawOverlay = z;
        if (z) {
            this.mOverlayTextSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.MiuixAppcompatAlphabetIndexer_miuixAppcompatOverlayTextSize, resources.getDimensionPixelSize(R$dimen.miuix_appcompat_alphabet_indexer_overlay_text_size));
            this.mOverlayTextColor = obtainStyledAttributes.getColor(R$styleable.MiuixAppcompatAlphabetIndexer_miuixAppcompatOverlayTextColor, resources.getColor(R$color.miuix_appcompat_alphabet_indexer_overlay_text_color));
            this.mOverlayTextAppearanceRes = obtainStyledAttributes.getResourceId(R$styleable.MiuixAppcompatAlphabetIndexer_miuixAppCompatOverlayTextAppearance, R$style.Widget_TextAppearance_AlphabetIndexer_Overlay);
            this.mOverlayBackground = obtainStyledAttributes.getDrawable(R$styleable.MiuixAppcompatAlphabetIndexer_miuixAppcompatOverlayBackground);
            this.mItemHeight = resources.getDimensionPixelOffset(R$dimen.miuix_appcompat_alphabet_indexer_item_height);
            this.mOmitItemHeight = resources.getDimensionPixelOffset(R$dimen.miuix_appcompat_alphabet_indexer_omit_item_height);
            int i2 = R$dimen.miuix_appcompat_alphabet_indexer_item_margin;
            this.mItemMargin = resources.getDimensionPixelOffset(i2);
            this.mMaxItemMargin = resources.getDimensionPixelOffset(i2);
            this.mMinItemMargin = resources.getDimensionPixelOffset(R$dimen.miuix_appcompat_alphabet_indexer_min_item_margin);
            this.mOverlayWidth = resources.getDimensionPixelOffset(R$dimen.miuix_appcompat_alphabet_overlay_width);
            this.mOverlayHeight = resources.getDimensionPixelOffset(R$dimen.miuix_appcompat_alphabet_overlay_height);
            this.mIndexWidth = resources.getDimensionPixelOffset(R$dimen.miuix_appcompat_alphabet_indexer_min_width);
        }
        obtainStyledAttributes.recycle();
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x0088  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void scrollTo(android.widget.SectionIndexer r17, int r18) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r2 = r18
            miuix.miuixbasewidget.widget.AlphabetIndexer$Adapter r3 = r0.mAdapter
            if (r3 != 0) goto Lb
            return
        Lb:
            r3.stopScroll()
            miuix.miuixbasewidget.widget.AlphabetIndexer$Adapter r3 = r0.mAdapter
            int r3 = r3.getItemCount()
            int r4 = r16.getListOffset()
            r5 = 1065353216(0x3f800000, float:1.0)
            float r6 = (float) r3
            float r5 = r5 / r6
            r6 = 1090519040(0x41000000, float:8.0)
            float r5 = r5 / r6
            java.lang.Object[] r6 = r17.getSections()
            if (r6 == 0) goto L90
            int r7 = r6.length
            r8 = 1
            if (r7 <= r8) goto L90
            int r7 = r6.length
            if (r2 < r7) goto L2f
            int r9 = r7 + (-1)
            goto L30
        L2f:
            r9 = r2
        L30:
            int r10 = r1.getPositionForSection(r9)
            int r11 = r9 + 1
            int r12 = r7 + (-1)
            if (r9 >= r12) goto L3f
            int r12 = r1.getPositionForSection(r11)
            goto L40
        L3f:
            r12 = r3
        L40:
            r13 = r9
            if (r12 != r10) goto L58
            r14 = r10
        L44:
            if (r13 <= 0) goto L56
            int r13 = r13 + (-1)
            int r14 = r1.getPositionForSection(r13)
            if (r14 == r10) goto L4f
            goto L57
        L4f:
            if (r13 != 0) goto L44
            r13 = 0
            r10 = r14
            r14 = r13
            r13 = r9
            goto L59
        L56:
            r13 = r9
        L57:
            r10 = r14
        L58:
            r14 = r13
        L59:
            int r15 = r11 + 1
        L5b:
            if (r15 >= r7) goto L69
            int r8 = r1.getPositionForSection(r15)
            if (r8 != r12) goto L69
            int r15 = r15 + 1
            int r11 = r11 + 1
            r8 = 1
            goto L5b
        L69:
            float r1 = (float) r13
            float r7 = (float) r7
            float r1 = r1 / r7
            float r8 = (float) r11
            float r8 = r8 / r7
            float r2 = (float) r2
            float r2 = r2 / r7
            if (r13 != r9) goto L79
            float r7 = r2 - r1
            int r5 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r5 >= 0) goto L79
            goto L84
        L79:
            int r12 = r12 - r10
            float r5 = (float) r12
            float r2 = r2 - r1
            float r5 = r5 * r2
            float r8 = r8 - r1
            float r5 = r5 / r8
            int r1 = java.lang.Math.round(r5)
            int r10 = r10 + r1
        L84:
            r1 = 1
            int r3 = r3 - r1
            if (r10 <= r3) goto L89
            r10 = r3
        L89:
            miuix.miuixbasewidget.widget.AlphabetIndexer$Adapter r1 = r0.mAdapter
            int r10 = r10 + r4
            r1.scrollToPosition(r10)
            goto L9e
        L90:
            int r1 = r2 * r3
            float r1 = (float) r1
            int r1 = java.lang.Math.round(r1)
            miuix.miuixbasewidget.widget.AlphabetIndexer$Adapter r2 = r0.mAdapter
            int r1 = r1 + r4
            r2.scrollToPosition(r1)
            r14 = -1
        L9e:
            r0.updateOverlay(r14, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.miuixbasewidget.widget.AlphabetIndexer.scrollTo(android.widget.SectionIndexer, int):void");
    }

    private void scrollToSelection(int i, SectionIndexer sectionIndexer) {
        if (this.mAdapter == null) {
            return;
        }
        int position = getPosition(i, sectionIndexer);
        if (position < 0) {
            this.mAdapter.scrollToPosition(0);
        } else {
            scrollTo(sectionIndexer, position);
        }
    }

    private void setChecked(int i) {
        this.mSelectedAlphaIndex = i;
        View view = this.mLastSelectedItem;
        if (view != null) {
            updateIndexItemColor(view, false);
        }
        View childAt = getChildAt(getChildIndex(i));
        this.mLastSelectedItem = childAt;
        updateIndexItemColor(childAt, true);
        View view2 = this.mLastSelectedItem;
        if (view2 != null) {
            view2.requestLayout();
        }
    }

    private void showOverlay() {
        TextView textView = this.mOverlay;
        if (textView != null) {
            Folme.useAt(textView).visible().setFlags(1L).setScale(0.0f, IVisibleStyle.VisibleType.HIDE).setScale(1.0f, IVisibleStyle.VisibleType.SHOW).show(this.mOverlayShowAnimConfig);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stop(int i) {
        this.mHandler.removeMessages(1);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), i <= 0 ? 0L : i);
    }

    private void updateIndexItemColor(View view, boolean z) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            TextHighlighter textHighlighter = this.mTextHighlighter;
            textView.setTextColor(z ? textHighlighter.mHighlightColor : textHighlighter.mNormalColor);
        } else if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(z ? R$drawable.miuix_ic_omit_selected : R$drawable.miuix_ic_omit);
        }
    }

    private void updateItemMargin(int i) {
        View childAt = getChildAt(0);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childAt.getLayoutParams();
        layoutParams.bottomMargin = i;
        layoutParams.topMargin = i;
        childAt.setLayoutParams(layoutParams);
        this.mItemMargin = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateItemsAfterHeightChanged() {
        int height = getHeight();
        this.mViewHeight = height;
        View childAt = getChildAt(0);
        int height2 = childAt.getHeight();
        if ((this.mTextHighlighter.mIndexes.length * (this.mItemHeight + (this.mMinItemMargin * 2))) + getPaddingTop() + getPaddingBottom() > height) {
            if (getChildCount() > 0) {
                removeAllViews();
                this.mSelectedAlphaIndex = -1;
            }
            constructItemWithOmit(height);
            Adapter adapter = this.mAdapter;
            if (adapter != null) {
                int sectionForPosition = getSectionIndexer().getSectionForPosition(adapter.getFirstVisibleItemPosition());
                int i = this.mSelectedAlphaIndex;
                if (i != sectionForPosition) {
                    clearLastChecked(i);
                    setChecked(sectionForPosition);
                    return;
                }
                return;
            }
            return;
        }
        int paddingTop = ((((height - getPaddingTop()) - getPaddingBottom()) / this.mTextHighlighter.mIndexes.length) - this.mItemHeight) / 2;
        if (getChildCount() != this.mTextHighlighter.mIndexes.length) {
            removeAllViews();
            constructItem(Math.min(this.mMaxItemMargin, paddingTop));
        } else if (Math.min(this.mMaxItemMargin, paddingTop) != this.mItemMargin) {
            updateItemMargin(Math.min(this.mMaxItemMargin, paddingTop));
        } else if (height2 != this.mItemHeight) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childAt.getLayoutParams();
            layoutParams.height = this.mItemHeight;
            layoutParams.topMargin = 0;
            layoutParams.bottomMargin = 0;
            childAt.setLayoutParams(layoutParams);
        }
    }

    private void updateOverlay(int i, Object[] objArr) {
        if (i < 0 || objArr == null) {
            return;
        }
        String obj = objArr[i].toString();
        if (TextUtils.isEmpty(obj)) {
            return;
        }
        drawThumbInternal(obj.toUpperCase().subSequence(0, 1), calculateOverlayPosition(getIndex(r2)));
    }

    private void updateOverlayLayout() {
        TextView textView = this.mOverlay;
        if (textView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.setMarginEnd(this.mIndexWidth + getMarinEnd() + 1);
            this.mOverlay.setLayoutParams(layoutParams);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOverlayTextAlpha(float f) {
        TextView textView = this.mOverlay;
        textView.setTextColor(textView.getTextColors().withAlpha((int) (f * 255.0f)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOverlayTranslationX(float f) {
        float width = (this.mOverlay.getWidth() / 2) * (1.0f - f);
        if (ViewUtils.isLayoutRtl(this)) {
            width *= -1.0f;
        }
        this.mOverlay.setTranslationX(width);
    }

    private void updateVerticalPadding() {
        int dimensionPixelOffset = getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_alphabet_indexer_padding_vertical);
        setPadding(getPaddingStart(), dimensionPixelOffset, getPaddingEnd(), dimensionPixelOffset);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return getClass().getName();
    }

    public int getIndexerIntrinsicWidth() {
        Drawable background = getBackground();
        if (background != null) {
            return background.getIntrinsicWidth();
        }
        return 0;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = configuration.screenHeightDp;
        if (i != this.mScreenHeightDp) {
            this.mScreenHeightDp = i;
            this.mMaxItemMargin = getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_alphabet_indexer_item_margin);
            updateVerticalPadding();
            updateOverlayLayout();
            this.mSelectedAlphaIndex = -1;
            removeAllViews();
            constructItem(this.mMaxItemMargin);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (!isEnabled() || this.mAdapter == null || getSectionIndexer() == null || getSectionIndexer().getSections() == null) {
            return;
        }
        int sectionForPosition = getSectionIndexer().getSectionForPosition(this.mAdapter.getFirstVisibleItemPosition() - getListOffset());
        if (sectionForPosition < 0 || sectionForPosition >= getSectionIndexer().getSections().length) {
            return;
        }
        if (sectionForPosition > 0) {
            accessibilityNodeInfo.addAction(8192);
        }
        if (sectionForPosition < getSectionIndexer().getSections().length - 1) {
            accessibilityNodeInfo.addAction(4096);
        }
        int i = Build.VERSION.SDK_INT;
        if (i >= 24) {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS);
        }
        accessibilityNodeInfo.setRangeInfo(AccessibilityNodeInfo.RangeInfo.obtain(0, -1.0f, (float) (getSectionIndexer().getSections().length - 1), sectionForPosition));
        Object obj = getSectionIndexer().getSections()[sectionForPosition];
        if (obj instanceof String) {
            String str = (String) obj;
            if (TextUtils.equals(str, "!")) {
                str = getContext().getString(R$string.miuix_indexer_collect);
            }
            accessibilityNodeInfo.setContentDescription(str);
        }
        if (i >= 30) {
            accessibilityNodeInfo.setStateDescription(getContext().getString(R$string.miuix_alphabet_indexer_name));
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mViewHeight != i4 - i2) {
            getViewTreeObserver().addOnPreDrawListener(this.mUpdateItemsListener);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x003b, code lost:
    
        if (r2 != 6) goto L37;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            miuix.miuixbasewidget.widget.AlphabetIndexer$Adapter r0 = r6.mAdapter
            r1 = 0
            if (r0 != 0) goto L9
            r6.stop(r1)
            return r1
        L9:
            android.widget.SectionIndexer r0 = r6.getSectionIndexer()
            if (r0 != 0) goto L13
            r6.stop(r1)
            return r1
        L13:
            int r2 = r7.getActionMasked()
            float r3 = r7.getY()
            int r4 = r6.getPaddingTop()
            float r4 = (float) r4
            float r3 = r3 - r4
            r4 = 0
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 < 0) goto L27
            goto L28
        L27:
            r3 = r4
        L28:
            int r3 = r6.calculateIndex(r3)
            r4 = 1
            if (r2 == 0) goto L56
            if (r2 == r4) goto L3e
            r5 = 2
            if (r2 == r5) goto L64
            r5 = 3
            if (r2 == r5) goto L3e
            r5 = 5
            if (r2 == r5) goto L56
            r0 = 6
            if (r2 == r0) goto L3e
            goto L6f
        L3e:
            int r0 = r7.getActionIndex()
            int r7 = r7.getPointerId(r0)
            if (r7 == 0) goto L49
            goto L6f
        L49:
            r6.setPressed(r1)
            boolean r7 = r6.hasShown()
            if (r7 == 0) goto L6f
            r6.stop(r1)
            goto L6f
        L56:
            int r1 = r7.getActionIndex()
            int r7 = r7.getPointerId(r1)
            if (r7 == 0) goto L61
            goto L6f
        L61:
            r6.setPressed(r4)
        L64:
            int r7 = r6.mSelectedAlphaIndex
            r6.clearLastChecked(r7)
            r6.setChecked(r3)
            r6.scrollToSelection(r3, r0)
        L6f:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.miuixbasewidget.widget.AlphabetIndexer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (super.performAccessibilityAction(i, bundle)) {
            return true;
        }
        if (!isEnabled() || this.mAdapter == null || getSectionIndexer() == null) {
            return false;
        }
        if (i == 4096 || i == 8192) {
            int sectionForPosition = getSectionIndexer().getSectionForPosition(this.mAdapter.getFirstVisibleItemPosition() - getListOffset());
            while (true) {
                sectionForPosition = i == 4096 ? sectionForPosition + 1 : sectionForPosition - 1;
                if (sectionForPosition > getSectionIndexer().getSections().length - 1 || sectionForPosition < 0) {
                    break;
                }
                if (getSectionIndexer().getSectionForPosition(getSectionIndexer().getPositionForSection(sectionForPosition)) == sectionForPosition) {
                    setChecked(sectionForPosition);
                    scrollToSelection(sectionForPosition, getSectionIndexer());
                    Object obj = getSectionIndexer().getSections()[sectionForPosition];
                    if (obj instanceof String) {
                        String string = getContext().getString(R$string.miuix_indexer_selected);
                        Object[] objArr = new Object[1];
                        if (TextUtils.equals((String) obj, "!")) {
                            obj = getContext().getString(R$string.miuix_indexer_collect);
                        }
                        objArr[0] = obj;
                        announceForAccessibility(String.format(string, objArr));
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void setSectionIndexer(SectionIndexer sectionIndexer) {
        this.mIndexer = sectionIndexer;
    }

    public void setVerticalPosition(boolean z) {
        this.mVerticalPosition = z ? 8388613 : 8388611;
    }
}
