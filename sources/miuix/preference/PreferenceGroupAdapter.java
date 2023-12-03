package miuix.preference;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.ViewUtils;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.TwoStatePreference;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.internal.util.EasyModeHelper;
import miuix.appcompat.internal.util.LayoutUIUtils;
import miuix.internal.graphics.drawable.TaggingDrawable;
import miuix.internal.util.AnimHelper;
import miuix.internal.util.AttributeResolver;
import miuix.preference.drawable.MaskTaggingDrawable;
import miuix.view.CompatViewMethod;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public class PreferenceGroupAdapter extends androidx.preference.PreferenceGroupAdapter {
    private static final int[] STATES_TAGS;
    private static final int[] STATE_SET_FIRST;
    private static final int[] STATE_SET_LAST;
    private static final int[] STATE_SET_MIDDLE;
    private static final int[] STATE_SET_NO_TITLE;
    private static final int[] STATE_SET_SINGLE;
    private int mCheckableFilterColorChecked;
    private int mCheckableFilterColorNormal;
    private Paint mClipPaint;
    private PositionDescriptor[] mDescriptors;
    private boolean mExtraPaddingEnable;
    private int mExtraPaddingHorizontal;
    private int mExtraPaddingLevel;
    private int mMaskPaddingBottom;
    private int mMaskPaddingEnd;
    private int mMaskPaddingStart;
    private int mMaskPaddingTop;
    private int mMaskRadius;
    private RecyclerView.AdapterDataObserver mObserver;
    private int mRadioSetItemPaddingStartExtra;
    private RecyclerView mRecyclerView;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class PositionDescriptor {
        int[] status;
        int type;

        PositionDescriptor() {
        }
    }

    static {
        int i = R$attr.state_no_title;
        int[] iArr = {16842915, 16842916, 16842917, 16842918, i};
        STATES_TAGS = iArr;
        Arrays.sort(iArr);
        STATE_SET_SINGLE = new int[]{16842915};
        STATE_SET_FIRST = new int[]{16842916};
        STATE_SET_MIDDLE = new int[]{16842917};
        STATE_SET_LAST = new int[]{16842918};
        STATE_SET_NO_TITLE = new int[]{i};
    }

    public PreferenceGroupAdapter(PreferenceGroup preferenceGroup) {
        super(preferenceGroup);
        this.mObserver = new RecyclerView.AdapterDataObserver() { // from class: miuix.preference.PreferenceGroupAdapter.1
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                super.onChanged();
                PreferenceGroupAdapter preferenceGroupAdapter = PreferenceGroupAdapter.this;
                preferenceGroupAdapter.mDescriptors = new PositionDescriptor[preferenceGroupAdapter.getItemCount()];
            }
        };
        this.mExtraPaddingLevel = 0;
        this.mExtraPaddingHorizontal = 0;
        this.mExtraPaddingEnable = false;
        this.mDescriptors = new PositionDescriptor[getItemCount()];
        initAttr(preferenceGroup.getContext());
    }

    private boolean ableToUseFolmeAnim(Preference preference) {
        return ((preference instanceof PreferenceCategory) || (preference instanceof DropDownPreference) || ((preference instanceof FolmeAnimationController) && !((FolmeAnimationController) preference).isTouchAnimationEnable())) ? false : true;
    }

    private void drawDrawable(Drawable drawable, boolean z, boolean z2) {
        if (drawable instanceof MaskTaggingDrawable) {
            MaskTaggingDrawable maskTaggingDrawable = (MaskTaggingDrawable) drawable;
            maskTaggingDrawable.setMaskEnabled(true);
            maskTaggingDrawable.setClipPaint(this.mClipPaint, this.mMaskPaddingTop, this.mMaskPaddingBottom, this.mMaskPaddingStart, this.mMaskPaddingEnd, this.mMaskRadius);
            boolean isLayoutRtl = ViewUtils.isLayoutRtl(this.mRecyclerView);
            Pair leftAndRightWithRTL = getLeftAndRightWithRTL(this.mRecyclerView, isLayoutRtl);
            maskTaggingDrawable.setLeftRight(((Integer) leftAndRightWithRTL.first).intValue(), ((Integer) leftAndRightWithRTL.second).intValue(), isLayoutRtl);
            maskTaggingDrawable.updateDrawCorner(z, z2);
        }
    }

    private void drawRadioSetPreferenceCategory(RadioButtonPreferenceCategory radioButtonPreferenceCategory) {
        int preferenceCount = radioButtonPreferenceCategory.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = radioButtonPreferenceCategory.getPreference(i);
            if (preference instanceof RadioSetPreferenceCategory) {
                drawRadioSetPreferenceCategory((RadioSetPreferenceCategory) preference);
            }
        }
    }

    private void drawRadioSetPreferenceCategory(RadioSetPreferenceCategory radioSetPreferenceCategory) {
        int preferenceAdapterPosition;
        View childAt;
        int preferenceCount = radioSetPreferenceCategory.getPreferenceCount();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = radioSetPreferenceCategory.getPreference(i);
            if (preference != null && (preferenceAdapterPosition = getPreferenceAdapterPosition(preference)) != -1 && (childAt = this.mRecyclerView.getChildAt(preferenceAdapterPosition)) != null) {
                arrayList.add(childAt);
            }
        }
        drawViews(arrayList);
    }

    private void drawView(View view, boolean z, boolean z2) {
        if (view != null) {
            drawDrawable(view.getBackground(), z, z2);
        }
    }

    private void drawViews(List<View> list) {
        int i = 0;
        while (i < list.size()) {
            boolean z = true;
            boolean z2 = i == 0;
            if (i != list.size() - 1) {
                z = false;
            }
            drawView(list.get(i), z2, z);
            i++;
        }
    }

    private List<Preference> getAllVisiblePreferences(PreferenceGroup preferenceGroup) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (preference.isVisible()) {
                arrayList.add(preference);
            }
        }
        return arrayList;
    }

    private void getPreferenceDescriptor(Preference preference, int i) {
        int[] iArr;
        PreferenceGroup parent;
        int[] iArr2;
        if (i >= 0) {
            PositionDescriptor[] positionDescriptorArr = this.mDescriptors;
            if (i < positionDescriptorArr.length) {
                if (positionDescriptorArr[i] == null) {
                    positionDescriptorArr[i] = new PositionDescriptor();
                }
                iArr = this.mDescriptors[i].status;
                if (iArr == null || (parent = preference.getParent()) == null) {
                }
                List<Preference> allVisiblePreferences = getAllVisiblePreferences(parent);
                if (allVisiblePreferences.isEmpty()) {
                    return;
                }
                int i2 = 1;
                if (allVisiblePreferences.size() == 1) {
                    iArr2 = STATE_SET_SINGLE;
                } else if (preference.compareTo(allVisiblePreferences.get(0)) == 0) {
                    iArr2 = STATE_SET_FIRST;
                    i2 = 2;
                } else if (preference.compareTo(allVisiblePreferences.get(allVisiblePreferences.size() - 1)) == 0) {
                    iArr2 = STATE_SET_LAST;
                    i2 = 4;
                } else {
                    iArr2 = STATE_SET_MIDDLE;
                    i2 = 3;
                }
                if ((preference instanceof PreferenceCategory) && TextUtils.isEmpty(((PreferenceCategory) preference).getTitle())) {
                    int[] iArr3 = STATE_SET_NO_TITLE;
                    int[] iArr4 = new int[iArr3.length + iArr2.length];
                    System.arraycopy(iArr3, 0, iArr4, 0, iArr3.length);
                    System.arraycopy(iArr2, 0, iArr4, iArr3.length, iArr2.length);
                    iArr2 = iArr4;
                }
                PositionDescriptor[] positionDescriptorArr2 = this.mDescriptors;
                positionDescriptorArr2[i].status = iArr2;
                positionDescriptorArr2[i].type = i2;
                return;
            }
        }
        iArr = null;
        if (iArr == null) {
        }
    }

    private boolean isArrowRightVisible(Preference preference) {
        return (preference.getIntent() == null && preference.getFragment() == null && (preference.getOnPreferenceClickListener() == null || (preference instanceof TwoStatePreference)) && !(preference instanceof DialogPreference)) ? false : true;
    }

    private void updateViewBackgroundMask(Preference preference) {
        if (preference == null || this.mRecyclerView == null) {
            return;
        }
        if (preference instanceof RadioButtonPreferenceCategory) {
            drawRadioSetPreferenceCategory((RadioButtonPreferenceCategory) preference);
        } else if (preference instanceof RadioSetPreferenceCategory) {
            drawRadioSetPreferenceCategory((RadioSetPreferenceCategory) preference);
        } else {
            boolean z = preference instanceof RadioButtonPreference;
        }
    }

    public Pair getLeftAndRightWithRTL(RecyclerView recyclerView, boolean z) {
        int width;
        int i;
        int scrollBarSize = recyclerView.getScrollBarSize();
        if (z) {
            i = scrollBarSize * 3;
            width = recyclerView.getWidth();
        } else {
            width = recyclerView.getWidth() - (scrollBarSize * 3);
            i = 0;
        }
        return new Pair(Integer.valueOf(i), Integer.valueOf(width));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getPositionType(int i) {
        return this.mDescriptors[i].type;
    }

    public void initAttr(Context context) {
        this.mRadioSetItemPaddingStartExtra = AttributeResolver.resolveDimensionPixelSize(context, R$attr.preferenceRadioSetChildExtraPaddingStart);
        this.mCheckableFilterColorChecked = AttributeResolver.resolveColor(context, R$attr.checkablePreferenceItemColorFilterChecked);
        this.mCheckableFilterColorNormal = AttributeResolver.resolveColor(context, R$attr.checkablePreferenceItemColorFilterNormal);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        registerAdapterDataObserver(this.mObserver);
        this.mRecyclerView = recyclerView;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.preference.PreferenceGroupAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder, int i) {
        super.onBindViewHolder(preferenceViewHolder, i);
        CompatViewMethod.setForceDarkAllowed(preferenceViewHolder.itemView, false);
        Preference item = getItem(i);
        boolean z = item instanceof PreferenceCategory;
        if (!z) {
            Folme.useAt(preferenceViewHolder.itemView).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(preferenceViewHolder.itemView, new AnimConfig[0]);
        }
        getPreferenceDescriptor(item, i);
        int[] iArr = this.mDescriptors[i].status;
        MaskTaggingDrawable background = preferenceViewHolder.itemView.getBackground();
        if ((background instanceof LevelListDrawable) && ((item instanceof RadioButtonPreference) || z)) {
            background.setLevel(this.mExtraPaddingEnable ? this.mExtraPaddingLevel : 0);
            MaskTaggingDrawable maskTaggingDrawable = new MaskTaggingDrawable(background.getCurrent());
            preferenceViewHolder.itemView.setBackground(maskTaggingDrawable);
            background = maskTaggingDrawable;
        }
        if ((background instanceof StateListDrawable) && TaggingDrawable.containsTagState((StateListDrawable) background, STATES_TAGS)) {
            MaskTaggingDrawable maskTaggingDrawable2 = new MaskTaggingDrawable(background);
            preferenceViewHolder.itemView.setBackground(maskTaggingDrawable2);
            background = maskTaggingDrawable2;
        }
        if (background instanceof MaskTaggingDrawable) {
            MaskTaggingDrawable maskTaggingDrawable3 = (MaskTaggingDrawable) background;
            if (iArr != null) {
                maskTaggingDrawable3.setTaggingState(iArr);
            }
            Rect rect = new Rect();
            if (maskTaggingDrawable3.getPadding(rect)) {
                int i2 = rect.left;
                int i3 = rect.right;
                rect.right = ViewUtils.isLayoutRtl(this.mRecyclerView) ? i2 : i3;
                if (ViewUtils.isLayoutRtl(this.mRecyclerView)) {
                    i2 = i3;
                }
                rect.left = i2;
                if (item.getParent() instanceof RadioSetPreferenceCategory) {
                    ViewGroup.LayoutParams layoutParams = preferenceViewHolder.itemView.getLayoutParams();
                    ViewGroup.MarginLayoutParams marginLayoutParams = layoutParams instanceof ViewGroup.MarginLayoutParams ? (ViewGroup.MarginLayoutParams) layoutParams : new ViewGroup.MarginLayoutParams(layoutParams);
                    marginLayoutParams.setMarginEnd(this.mRecyclerView.getScrollBarSize() * 2);
                    preferenceViewHolder.itemView.setLayoutParams(marginLayoutParams);
                    RadioSetPreferenceCategory radioSetPreferenceCategory = (RadioSetPreferenceCategory) item.getParent();
                    maskTaggingDrawable3.setMaskEnabled(false);
                    maskTaggingDrawable3.setColorFilter(radioSetPreferenceCategory.isChecked() ? this.mCheckableFilterColorChecked : this.mCheckableFilterColorNormal, PorterDuff.Mode.SRC_OVER);
                    RecyclerView recyclerView = this.mRecyclerView;
                    if (recyclerView != null) {
                        boolean z2 = item instanceof RadioButtonPreference;
                        int scrollBarSize = recyclerView.getScrollBarSize();
                        if (ViewUtils.isLayoutRtl(this.mRecyclerView)) {
                            rect.right += z2 ? 0 : this.mRadioSetItemPaddingStartExtra;
                            rect.left -= scrollBarSize * 3;
                        } else {
                            rect.left += z2 ? 0 : this.mRadioSetItemPaddingStartExtra;
                            rect.right -= scrollBarSize * 3;
                        }
                    }
                } else {
                    maskTaggingDrawable3.setColorFilter(null);
                }
                int i4 = rect.left;
                boolean z3 = this.mExtraPaddingEnable;
                preferenceViewHolder.itemView.setPadding(i4 + (z3 ? this.mExtraPaddingHorizontal : 0), rect.top, rect.right + (z3 ? this.mExtraPaddingHorizontal : 0), rect.bottom);
            }
            if ((item instanceof RadioButtonPreference) && ((RadioButtonPreference) item).isChecked()) {
                maskTaggingDrawable3.setTaggingState(new int[]{16842912});
            }
        }
        View findViewById = preferenceViewHolder.itemView.findViewById(R$id.arrow_right);
        if (findViewById != null) {
            findViewById.setVisibility(isArrowRightVisible(item) ? 0 : 8);
        }
        if (ableToUseFolmeAnim(item)) {
            AnimHelper.addPressAnim(preferenceViewHolder.itemView);
        }
        EasyModeHelper.updateTextViewSize((TextView) preferenceViewHolder.itemView.findViewById(16908310));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(this.mObserver);
        this.mRecyclerView = null;
    }

    @Override // androidx.preference.PreferenceGroupAdapter, androidx.preference.Preference.OnPreferenceChangeInternalListener
    public void onPreferenceChange(Preference preference) {
        Preference findPreference;
        super.onPreferenceChange(preference);
        String dependency = preference.getDependency();
        if (TextUtils.isEmpty(dependency) || (findPreference = preference.getPreferenceManager().findPreference(dependency)) == null) {
            return;
        }
        if (!(preference instanceof PreferenceCategory)) {
            preference.setVisible(preference.isEnabled());
        } else if (findPreference instanceof TwoStatePreference) {
            preference.setVisible(((TwoStatePreference) findPreference).isChecked());
        } else {
            preference.setVisible(findPreference.isEnabled());
        }
    }

    @Override // androidx.preference.PreferenceGroupAdapter, androidx.preference.Preference.OnPreferenceChangeInternalListener
    public void onPreferenceVisibilityChange(Preference preference) {
        if (preference != null && !preference.isVisible()) {
            updateViewBackgroundMask(preference);
        }
        super.onPreferenceVisibilityChange(preference);
    }

    public void setClipPaint(Paint paint, int i, int i2, int i3, int i4, int i5) {
        this.mClipPaint = paint;
        this.mMaskPaddingTop = i;
        this.mMaskPaddingBottom = i2;
        this.mMaskPaddingStart = i3;
        this.mMaskPaddingEnd = i4;
        this.mMaskRadius = i5;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setExtraHorizontalPaddingLevel(int i, int i2, boolean z) {
        setExtraHorizontalPaddingLevel(i, i2, z, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setExtraHorizontalPaddingLevel(int i, int i2, boolean z, boolean z2) {
        if (z2 || (LayoutUIUtils.isLevelValid(i) && this.mExtraPaddingLevel != i)) {
            this.mExtraPaddingLevel = i;
            this.mExtraPaddingHorizontal = i2;
            this.mExtraPaddingEnable = z;
            notifyDataSetChanged();
        }
    }
}
