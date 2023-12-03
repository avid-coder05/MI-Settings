package miuix.preference;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import androidx.appcompat.widget.ViewUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.internal.util.LayoutUIUtils;
import miuix.internal.util.AttributeResolver;
import miuix.internal.util.DeviceHelper;
import miuix.preference.utils.PreferenceLayoutUtils;
import miuix.springback.view.SpringBackLayout;

/* loaded from: classes5.dex */
public abstract class PreferenceFragment extends PreferenceFragmentCompat {
    private boolean mConfigChangeUpdateViewEnable;
    private boolean mExtraPaddingEnable;
    private int mExtraPaddingHorizontal;
    private FrameDecoration mFrameDecoration;
    private PreferenceGroupAdapter mGroupAdapter;
    private int mOrientation;
    private int mScreenHeightDp;
    private int mScreenWidthDp;
    private boolean mAdapterInvalid = true;
    private boolean mItemSelectable = false;
    private int mCurSelectedItem = -1;
    private int mExtraPaddingLevel = 0;

    /* loaded from: classes5.dex */
    private class FrameDecoration extends RecyclerView.ItemDecoration {
        private Paint mClipPaint;
        private int mHeightPixels;
        private int mMaskPaddingBottom;
        private int mMaskPaddingEnd;
        private int mMaskPaddingStart;
        private int mMaskPaddingTop;
        private int mMaskRadius;
        private PreferenceGroupRect mPreferenceGroupRect;
        private Map<Integer, PreferenceGroupRect> mPreferenceGroupRectMaps;

        private FrameDecoration(Context context) {
            initMaskPadding(context);
            this.mClipPaint = new Paint();
            updateClipPaintColor();
            this.mClipPaint.setAntiAlias(true);
            this.mPreferenceGroupRectMaps = new HashMap();
        }

        private boolean checkEndRadioButtonPreferenceCategory(RecyclerView recyclerView, int i, int i2) {
            int i3 = i + 1;
            if (i3 < i2) {
                return !(PreferenceFragment.this.mGroupAdapter.getItem(recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i3))) instanceof RadioSetPreferenceCategory);
            }
            return false;
        }

        private void drawMask(Canvas canvas, int i, int i2, int i3, int i4, boolean z, boolean z2, boolean z3, boolean z4) {
            if (PreferenceFragment.this.mAdapterInvalid) {
                return;
            }
            float f = i2;
            float f2 = i4;
            RectF rectF = new RectF(i, f, i3, f2);
            RectF rectF2 = new RectF(i + (z4 ? this.mMaskPaddingEnd : this.mMaskPaddingStart) + (PreferenceFragment.this.mExtraPaddingEnable ? PreferenceFragment.this.mExtraPaddingHorizontal : 0), f, i3 - ((z4 ? this.mMaskPaddingStart : this.mMaskPaddingEnd) + (PreferenceFragment.this.mExtraPaddingEnable ? PreferenceFragment.this.mExtraPaddingHorizontal : 0)), f2);
            Path path = new Path();
            float f3 = z ? this.mMaskRadius : 0.0f;
            float f4 = z2 ? this.mMaskRadius : 0.0f;
            path.addRoundRect(rectF2, new float[]{f3, f3, f3, f3, f4, f4, f4, f4}, Path.Direction.CW);
            int saveLayer = canvas.saveLayer(rectF, this.mClipPaint, 31);
            canvas.drawRect(rectF, this.mClipPaint);
            if (z3) {
                this.mClipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            } else {
                this.mClipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
            }
            canvas.drawPath(path, this.mClipPaint);
            this.mClipPaint.setXfermode(null);
            canvas.restoreToCount(saveLayer);
        }

        private int findNearViewY(RecyclerView recyclerView, View view, int i, int i2, boolean z) {
            View childAt;
            if (z) {
                if (view == null || view.getBottom() + view.getHeight() >= this.mHeightPixels) {
                    return -1;
                }
                do {
                    i++;
                    if (i < i2) {
                        childAt = recyclerView.getChildAt(i);
                    }
                } while (childAt == null);
                return (int) childAt.getY();
            }
            for (int i3 = i - 1; i3 > i2; i3--) {
                View childAt2 = recyclerView.getChildAt(i3);
                if (childAt2 != null) {
                    return ((int) childAt2.getY()) + childAt2.getHeight();
                }
            }
            return -1;
        }

        private void updateOperationTopAndBottom(RecyclerView recyclerView, PreferenceGroupRect preferenceGroupRect) {
            int size = preferenceGroupRect.preferenceList.size();
            int i = 0;
            int i2 = 0;
            int i3 = -1;
            int i4 = -1;
            for (int i5 = 0; i5 < size; i5++) {
                View childAt = recyclerView.getChildAt(preferenceGroupRect.preferenceList.get(i5).intValue());
                if (childAt != null) {
                    int top = childAt.getTop();
                    int bottom = childAt.getBottom();
                    if (i5 == 0) {
                        i2 = bottom;
                        i = top;
                    }
                    if (i > top) {
                        i = top;
                    }
                    if (i2 < bottom) {
                        i2 = bottom;
                    }
                }
                int i6 = preferenceGroupRect.nextViewY;
                if (i6 != -1 && i6 > preferenceGroupRect.preViewHY) {
                    i3 = i6 - this.mMaskPaddingTop;
                }
                int i7 = preferenceGroupRect.preViewHY;
                if (i7 != -1 && i7 < i6) {
                    i4 = i6 - this.mMaskPaddingTop;
                }
            }
            preferenceGroupRect.currentEndtb = new int[]{i, i2};
            if (i3 != -1) {
                i2 = i3;
            }
            if (i4 != -1) {
                i = i4;
            }
            preferenceGroupRect.currentMovetb = new int[]{i, i2};
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            int childAdapterPosition;
            Preference item;
            if (PreferenceFragment.this.mAdapterInvalid || (item = PreferenceFragment.this.mGroupAdapter.getItem((childAdapterPosition = recyclerView.getChildAdapterPosition(view)))) == null || !(item.getParent() instanceof RadioSetPreferenceCategory)) {
                return;
            }
            if (ViewUtils.isLayoutRtl(recyclerView)) {
                rect.left = recyclerView.getScrollBarSize();
            } else {
                rect.right = recyclerView.getScrollBarSize();
            }
            int positionType = PreferenceFragment.this.mGroupAdapter.getPositionType(childAdapterPosition);
            if (positionType == 1) {
                rect.top += this.mMaskPaddingTop;
                rect.bottom += this.mMaskPaddingBottom;
            } else if (positionType == 2) {
                rect.top += this.mMaskPaddingTop;
            } else if (positionType == 4) {
                rect.bottom += this.mMaskPaddingBottom;
            }
        }

        public void initMaskPadding(Context context) {
            this.mMaskPaddingTop = context.getResources().getDimensionPixelSize(R$dimen.miuix_preference_checkable_item_mask_padding_top);
            this.mMaskPaddingBottom = context.getResources().getDimensionPixelSize(R$dimen.miuix_preference_checkable_item_mask_padding_bottom);
            this.mMaskPaddingStart = AttributeResolver.resolveDimensionPixelSize(context, R$attr.preferenceCheckableItemMaskPaddingStart);
            this.mMaskPaddingEnd = AttributeResolver.resolveDimensionPixelSize(context, R$attr.preferenceCheckableItemSetMaskPaddingEnd);
            this.mMaskRadius = context.getResources().getDimensionPixelSize(R$dimen.miuix_preference_checkable_item_mask_radius);
            this.mHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void onDrawOver(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
            boolean z;
            int i;
            int i2;
            View view;
            if (PreferenceFragment.this.mAdapterInvalid) {
                return;
            }
            this.mPreferenceGroupRectMaps.clear();
            int childCount = recyclerView.getChildCount();
            boolean isLayoutRtl = ViewUtils.isLayoutRtl(recyclerView);
            Pair leftAndRightWithRTL = PreferenceFragment.this.mGroupAdapter.getLeftAndRightWithRTL(recyclerView, isLayoutRtl);
            int intValue = ((Integer) leftAndRightWithRTL.first).intValue();
            int intValue2 = ((Integer) leftAndRightWithRTL.second).intValue();
            int i3 = 0;
            while (true) {
                if (i3 >= childCount) {
                    break;
                }
                View childAt = recyclerView.getChildAt(i3);
                int childAdapterPosition = recyclerView.getChildAdapterPosition(childAt);
                Preference item = PreferenceFragment.this.mGroupAdapter.getItem(childAdapterPosition);
                if (item != null && (item.getParent() instanceof RadioSetPreferenceCategory)) {
                    int positionType = PreferenceFragment.this.mGroupAdapter.getPositionType(childAdapterPosition);
                    if (positionType == 1 || positionType == 2) {
                        PreferenceGroupRect preferenceGroupRect = new PreferenceGroupRect();
                        this.mPreferenceGroupRect = preferenceGroupRect;
                        preferenceGroupRect.type |= 1;
                        preferenceGroupRect.startRadioButtonCategory = true;
                        i2 = positionType;
                        view = childAt;
                        preferenceGroupRect.preViewHY = findNearViewY(recyclerView, childAt, i3, 0, false);
                        this.mPreferenceGroupRect.addPreference(i3);
                    } else {
                        i2 = positionType;
                        view = childAt;
                    }
                    if (i2 == 4 || i2 == 3) {
                        PreferenceGroupRect preferenceGroupRect2 = this.mPreferenceGroupRect;
                        if (preferenceGroupRect2 != null) {
                            preferenceGroupRect2.addPreference(i3);
                        } else {
                            PreferenceGroupRect preferenceGroupRect3 = new PreferenceGroupRect();
                            this.mPreferenceGroupRect = preferenceGroupRect3;
                            preferenceGroupRect3.addPreference(i3);
                        }
                        this.mPreferenceGroupRect.type |= 2;
                    }
                    PreferenceGroupRect preferenceGroupRect4 = this.mPreferenceGroupRect;
                    if (preferenceGroupRect4 != null && (i2 == 1 || i2 == 4)) {
                        preferenceGroupRect4.nextViewY = findNearViewY(recyclerView, view, i3, childCount, true);
                        this.mPreferenceGroupRect.index = this.mPreferenceGroupRectMaps.size();
                        this.mPreferenceGroupRect.endRadioButtonCategory = checkEndRadioButtonPreferenceCategory(recyclerView, i3, childCount);
                        PreferenceGroupRect preferenceGroupRect5 = this.mPreferenceGroupRect;
                        preferenceGroupRect5.type |= 4;
                        this.mPreferenceGroupRectMaps.put(Integer.valueOf(preferenceGroupRect5.index), this.mPreferenceGroupRect);
                        this.mPreferenceGroupRect = null;
                    }
                }
                i3++;
            }
            PreferenceGroupRect preferenceGroupRect6 = this.mPreferenceGroupRect;
            if (preferenceGroupRect6 != null && preferenceGroupRect6.preferenceList.size() > 0) {
                PreferenceGroupRect preferenceGroupRect7 = this.mPreferenceGroupRect;
                preferenceGroupRect7.nextViewY = -1;
                preferenceGroupRect7.index = this.mPreferenceGroupRectMaps.size();
                PreferenceGroupRect preferenceGroupRect8 = this.mPreferenceGroupRect;
                preferenceGroupRect8.endRadioButtonCategory = false;
                this.mPreferenceGroupRectMaps.put(Integer.valueOf(preferenceGroupRect8.index), this.mPreferenceGroupRect);
                this.mPreferenceGroupRect = null;
            }
            Map<Integer, PreferenceGroupRect> map = this.mPreferenceGroupRectMaps;
            if (map == null || map.size() <= 0) {
                return;
            }
            Iterator<Map.Entry<Integer, PreferenceGroupRect>> it = this.mPreferenceGroupRectMaps.entrySet().iterator();
            while (it.hasNext()) {
                updateOperationTopAndBottom(recyclerView, it.next().getValue());
            }
            for (Map.Entry<Integer, PreferenceGroupRect> entry : this.mPreferenceGroupRectMaps.entrySet()) {
                int intValue3 = entry.getKey().intValue();
                PreferenceGroupRect value = entry.getValue();
                int i4 = value.currentMovetb[1];
                if (intValue3 == 0) {
                    z = false;
                    i = value.currentEndtb[0];
                } else {
                    z = false;
                    i = value.preViewHY + this.mMaskPaddingBottom;
                }
                int i5 = i;
                drawMask(canvas, intValue, i5 - this.mMaskPaddingTop, intValue2, i5, false, false, true, isLayoutRtl);
                drawMask(canvas, intValue, i4, intValue2, i4 + this.mMaskPaddingBottom, false, false, true, isLayoutRtl);
                int i6 = value.type;
                drawMask(canvas, intValue, i5, intValue2, i4, (i6 & 1) != 0 ? true : z, (i6 & 4) != 0 ? true : z, false, isLayoutRtl);
            }
        }

        public void updateClipPaintColor() {
            if (!(PreferenceFragment.this.getActivity() instanceof AppCompatActivity) || ((AppCompatActivity) PreferenceFragment.this.getActivity()).isInFloatingWindowMode()) {
                this.mClipPaint.setColor(AttributeResolver.resolveColor(PreferenceFragment.this.getContext(), R$attr.preferenceCheckableMaskColor));
            } else {
                this.mClipPaint.setColor(AttributeResolver.resolveColor(PreferenceFragment.this.getContext(), R$attr.preferenceNormalCheckableMaskColor));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class PreferenceGroupRect {
        public int[] currentEndtb;
        public int[] currentMovetb;
        public boolean endRadioButtonCategory;
        public int index;
        public int nextViewY;
        public int preViewHY;
        public List<Integer> preferenceList;
        public boolean startRadioButtonCategory;
        public int type;

        private PreferenceGroupRect() {
            this.preferenceList = new ArrayList();
            this.currentMovetb = null;
            this.currentEndtb = null;
            this.index = 0;
            this.preViewHY = -1;
            this.nextViewY = -1;
            this.endRadioButtonCategory = false;
            this.startRadioButtonCategory = false;
            this.type = 0;
        }

        public void addPreference(int i) {
            this.preferenceList.add(Integer.valueOf(i));
        }

        public String toString() {
            return "PreferenceGroupRect{preferenceList=" + this.preferenceList + ", currentMovetb=" + Arrays.toString(this.currentMovetb) + ", currentEndtb=" + Arrays.toString(this.currentEndtb) + ", index=" + this.index + ", preViewHY=" + this.preViewHY + ", nextViewY=" + this.nextViewY + ", end=" + this.endRadioButtonCategory + '}';
        }
    }

    private boolean isTabletOrFold() {
        return DeviceHelper.isTablet(getActivity()) || DeviceHelper.isFoldDevice();
    }

    private void notifyExtraPaddingLevelChange() {
        PreferenceGroupAdapter preferenceGroupAdapter = this.mGroupAdapter;
        if (preferenceGroupAdapter != null) {
            preferenceGroupAdapter.setExtraHorizontalPaddingLevel(this.mExtraPaddingLevel, this.mExtraPaddingHorizontal, this.mExtraPaddingEnable);
        }
    }

    public boolean isConfigChangeUpdateViewEnable() {
        return true;
    }

    @Override // androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        PreferenceScreen preferenceScreen;
        LinearLayoutManager linearLayoutManager;
        final int findFirstVisibleItemPosition;
        View findViewByPosition;
        super.onConfigurationChanged(configuration);
        int i = configuration.orientation;
        if (i == this.mOrientation && configuration.screenWidthDp == this.mScreenWidthDp && configuration.screenHeightDp == this.mScreenHeightDp) {
            return;
        }
        this.mOrientation = i;
        this.mScreenWidthDp = configuration.screenWidthDp;
        this.mScreenHeightDp = configuration.screenHeightDp;
        if (getActivity() == null || !isTabletOrFold() || !this.mConfigChangeUpdateViewEnable || (preferenceScreen = getPreferenceScreen()) == null) {
            return;
        }
        this.mFrameDecoration.initMaskPadding(preferenceScreen.getContext());
        this.mFrameDecoration.updateClipPaintColor();
        this.mGroupAdapter.initAttr(preferenceScreen.getContext());
        this.mGroupAdapter.setClipPaint(this.mFrameDecoration.mClipPaint, this.mFrameDecoration.mMaskPaddingTop, this.mFrameDecoration.mMaskPaddingBottom, this.mFrameDecoration.mMaskPaddingStart, this.mFrameDecoration.mMaskPaddingEnd, this.mFrameDecoration.mMaskRadius);
        final RecyclerView.LayoutManager layoutManager = getListView().getLayoutManager();
        if ((layoutManager instanceof LinearLayoutManager) && (findViewByPosition = linearLayoutManager.findViewByPosition((findFirstVisibleItemPosition = (linearLayoutManager = (LinearLayoutManager) layoutManager).findFirstVisibleItemPosition()))) != null) {
            final int top = findViewByPosition.getTop();
            getListView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: miuix.preference.PreferenceFragment.1
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    if (layoutManager.getChildAt(0) == null) {
                        return;
                    }
                    ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(findFirstVisibleItemPosition, top);
                    PreferenceFragment.this.getListView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
        int extraPaddingByLevel = PreferenceLayoutUtils.getExtraPaddingByLevel(getContext(), this.mExtraPaddingLevel);
        this.mExtraPaddingHorizontal = extraPaddingByLevel;
        this.mGroupAdapter.setExtraHorizontalPaddingLevel(this.mExtraPaddingLevel, extraPaddingByLevel, this.mExtraPaddingEnable, true);
        getListView().setAdapter(this.mGroupAdapter);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mConfigChangeUpdateViewEnable = isConfigChangeUpdateViewEnable();
        Configuration configuration = getResources().getConfiguration();
        this.mOrientation = configuration.orientation;
        this.mScreenWidthDp = configuration.screenWidthDp;
        this.mScreenHeightDp = configuration.screenHeightDp;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    protected final RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        PreferenceGroupAdapter preferenceGroupAdapter = new PreferenceGroupAdapter(preferenceScreen);
        this.mGroupAdapter = preferenceGroupAdapter;
        preferenceGroupAdapter.setExtraHorizontalPaddingLevel(this.mExtraPaddingLevel, this.mExtraPaddingHorizontal, this.mExtraPaddingEnable);
        this.mAdapterInvalid = this.mGroupAdapter.getItemCount() < 1;
        this.mGroupAdapter.setClipPaint(this.mFrameDecoration.mClipPaint, this.mFrameDecoration.mMaskPaddingTop, this.mFrameDecoration.mMaskPaddingBottom, this.mFrameDecoration.mMaskPaddingStart, this.mFrameDecoration.mMaskPaddingEnd, this.mFrameDecoration.mMaskRadius);
        return this.mGroupAdapter;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        RecyclerView recyclerView = (RecyclerView) layoutInflater.inflate(R$layout.miuix_preference_recyclerview, viewGroup, false);
        if (recyclerView instanceof miuix.recyclerview.widget.RecyclerView) {
            ((miuix.recyclerview.widget.RecyclerView) recyclerView).setSpringEnabled(false);
        }
        recyclerView.setLayoutManager(onCreateLayoutManager());
        FrameDecoration frameDecoration = new FrameDecoration(recyclerView.getContext());
        this.mFrameDecoration = frameDecoration;
        recyclerView.addItemDecoration(frameDecoration);
        if (viewGroup instanceof SpringBackLayout) {
            ((SpringBackLayout) viewGroup).setTarget(recyclerView);
        }
        return recyclerView;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FragmentActivity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
            int extraHorizontalPaddingLevel = appCompatActivity.getExtraHorizontalPaddingLevel();
            this.mExtraPaddingEnable = extraHorizontalPaddingLevel != 0;
            setExtraHorizontalPaddingLevel(extraHorizontalPaddingLevel, false);
            appCompatActivity.setExtraHorizontalPaddingEnable(false);
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnDisplayPreferenceDialogListener
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment newInstance;
        boolean onPreferenceDisplayDialog = getCallbackFragment() instanceof PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback ? ((PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) getCallbackFragment()).onPreferenceDisplayDialog(this, preference) : false;
        if (!onPreferenceDisplayDialog && (getActivity() instanceof PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback)) {
            onPreferenceDisplayDialog = ((PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) getActivity()).onPreferenceDisplayDialog(this, preference);
        }
        if (!onPreferenceDisplayDialog && getFragmentManager().findFragmentByTag("androidx.preference.PreferenceFragment.DIALOG") == null) {
            if (preference instanceof EditTextPreference) {
                newInstance = EditTextPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            } else if (preference instanceof ListPreference) {
                newInstance = ListPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            } else if (!(preference instanceof MultiSelectListPreference)) {
                throw new IllegalArgumentException("Cannot display dialog for an unknown Preference type: " + preference.getClass().getSimpleName() + ". Make sure to implement onPreferenceDisplayDialog() to handle displaying a custom dialog for this Preference.");
            } else {
                newInstance = MultiSelectListPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            }
            newInstance.setTargetFragment(this, 0);
            newInstance.show(getFragmentManager(), "androidx.preference.PreferenceFragment.DIALOG");
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        int order;
        int i;
        View childAt;
        if (this.mItemSelectable && (order = preference.getOrder()) != (i = this.mCurSelectedItem)) {
            if (i >= 0 && (childAt = getListView().getChildAt(this.mCurSelectedItem)) != null) {
                childAt.setSelected(false);
            }
            View childAt2 = getListView().getChildAt(order);
            if (childAt2 != null) {
                childAt2.setSelected(true);
                this.mCurSelectedItem = order;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    void setExtraHorizontalPaddingLevel(int i, boolean z) {
        if (!LayoutUIUtils.isLevelValid(i) || this.mExtraPaddingLevel == i) {
            return;
        }
        this.mExtraPaddingLevel = i;
        this.mExtraPaddingHorizontal = PreferenceLayoutUtils.getExtraPaddingByLevel(getContext(), i);
        if (z) {
            notifyExtraPaddingLevelChange();
        }
    }
}
