package androidx.slice.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import androidx.appcompat.R$attr;
import androidx.appcompat.R$color;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.view.R$dimen;
import androidx.slice.view.R$layout;
import androidx.slice.widget.SliceView;

/* loaded from: classes.dex */
public class SliceActionView extends FrameLayout implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    static final int[] CHECKED_STATE_SET = {16842912};
    private View mActionView;
    private EventInfo mEventInfo;
    private int mIconSize;
    private int mImageSize;
    private SliceActionLoadingListener mLoadingListener;
    private SliceView.OnSliceActionListener mObserver;
    private ProgressBar mProgressView;
    private SliceActionImpl mSliceAction;
    private int mTextActionPadding;

    /* loaded from: classes.dex */
    private static class ImageToggle extends ImageView implements Checkable, View.OnClickListener {
        private boolean mIsChecked;
        private View.OnClickListener mListener;

        ImageToggle(Context context) {
            super(context);
            super.setOnClickListener(this);
        }

        @Override // android.widget.Checkable
        public boolean isChecked() {
            return this.mIsChecked;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            toggle();
        }

        @Override // android.widget.ImageView, android.view.View
        public int[] onCreateDrawableState(int extraSpace) {
            int[] onCreateDrawableState = super.onCreateDrawableState(extraSpace + 1);
            if (this.mIsChecked) {
                ImageView.mergeDrawableStates(onCreateDrawableState, SliceActionView.CHECKED_STATE_SET);
            }
            return onCreateDrawableState;
        }

        @Override // android.widget.Checkable
        public void setChecked(boolean checked) {
            if (this.mIsChecked != checked) {
                this.mIsChecked = checked;
                refreshDrawableState();
                View.OnClickListener onClickListener = this.mListener;
                if (onClickListener != null) {
                    onClickListener.onClick(this);
                }
            }
        }

        @Override // android.view.View
        public void setOnClickListener(View.OnClickListener listener) {
            this.mListener = listener;
        }

        @Override // android.widget.Checkable
        public void toggle() {
            setChecked(!isChecked());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface SliceActionLoadingListener {
        void onSliceActionLoading(SliceItem actionItem, int position);
    }

    public SliceActionView(Context context, SliceStyle style, RowStyle rowStyle) {
        super(context);
        Resources resources = getContext().getResources();
        this.mIconSize = resources.getDimensionPixelSize(R$dimen.abc_slice_icon_size);
        this.mImageSize = resources.getDimensionPixelSize(R$dimen.abc_slice_small_image_size);
        this.mTextActionPadding = 0;
        if (rowStyle != null) {
            this.mIconSize = rowStyle.getIconSize();
            this.mImageSize = rowStyle.getImageSize();
            this.mTextActionPadding = rowStyle.getTextActionPadding();
        }
    }

    private void sendActionInternal() {
        EventInfo eventInfo;
        SliceActionImpl sliceActionImpl = this.mSliceAction;
        if (sliceActionImpl == null || sliceActionImpl.getActionItem() == null) {
            return;
        }
        Intent intent = null;
        try {
            if (this.mSliceAction.isToggle()) {
                boolean isChecked = ((Checkable) this.mActionView).isChecked();
                Intent putExtra = new Intent().addFlags(268435456).putExtra("android.app.slice.extra.TOGGLE_STATE", isChecked);
                EventInfo eventInfo2 = this.mEventInfo;
                if (eventInfo2 != null) {
                    eventInfo2.state = isChecked ? 1 : 0;
                }
                intent = putExtra;
            }
            if (this.mSliceAction.getActionItem().fireActionInternal(getContext(), intent)) {
                setLoading(true);
                SliceActionLoadingListener sliceActionLoadingListener = this.mLoadingListener;
                if (sliceActionLoadingListener != null) {
                    EventInfo eventInfo3 = this.mEventInfo;
                    sliceActionLoadingListener.onSliceActionLoading(this.mSliceAction.getSliceItem(), eventInfo3 != null ? eventInfo3.rowIndex : -1);
                }
            }
            SliceView.OnSliceActionListener onSliceActionListener = this.mObserver;
            if (onSliceActionListener == null || (eventInfo = this.mEventInfo) == null) {
                return;
            }
            onSliceActionListener.onSliceAction(eventInfo, this.mSliceAction.getSliceItem());
        } catch (PendingIntent.CanceledException e) {
            View view = this.mActionView;
            if (view instanceof Checkable) {
                view.setSelected(true ^ ((Checkable) view).isChecked());
            }
            Log.e("SliceActionView", "PendingIntent for slice cannot be sent", e);
        }
    }

    public SliceActionImpl getAction() {
        return this.mSliceAction;
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (this.mSliceAction == null || this.mActionView == null) {
            return;
        }
        sendActionInternal();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (this.mSliceAction == null || this.mActionView == null) {
            return;
        }
        sendActionInternal();
    }

    public void sendAction() {
        SliceActionImpl sliceActionImpl = this.mSliceAction;
        if (sliceActionImpl == null) {
            return;
        }
        if (sliceActionImpl.isToggle()) {
            toggle();
        } else {
            sendActionInternal();
        }
    }

    public void setAction(SliceActionImpl action, EventInfo info, SliceView.OnSliceActionListener listener, int color, SliceActionLoadingListener loadingListener) {
        View view = this.mActionView;
        if (view != null) {
            removeView(view);
            this.mActionView = null;
        }
        View view2 = this.mProgressView;
        if (view2 != null) {
            removeView(view2);
            this.mProgressView = null;
        }
        this.mSliceAction = action;
        this.mEventInfo = info;
        this.mObserver = listener;
        this.mActionView = null;
        this.mLoadingListener = loadingListener;
        int i = 0;
        if (action.isDefaultToggle()) {
            Switch r9 = (Switch) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_switch, (ViewGroup) this, false);
            r9.setChecked(action.isChecked());
            r9.setOnCheckedChangeListener(this);
            r9.setMinimumHeight(this.mImageSize);
            r9.setMinimumWidth(this.mImageSize);
            addView(r9);
            if (color != -1) {
                int colorAttr = SliceViewUtil.getColorAttr(getContext(), 16842800);
                int[] iArr = CHECKED_STATE_SET;
                int[] iArr2 = FrameLayout.EMPTY_STATE_SET;
                ColorStateList colorStateList = new ColorStateList(new int[][]{iArr, iArr2}, new int[]{color, colorAttr});
                Drawable wrap = DrawableCompat.wrap(r9.getTrackDrawable());
                DrawableCompat.setTintList(wrap, colorStateList);
                r9.setTrackDrawable(wrap);
                int colorAttr2 = SliceViewUtil.getColorAttr(getContext(), R$attr.colorSwitchThumbNormal);
                if (colorAttr2 == 0) {
                    colorAttr2 = ContextCompat.getColor(getContext(), R$color.switch_thumb_normal_material_light);
                }
                ColorStateList colorStateList2 = new ColorStateList(new int[][]{iArr, iArr2}, new int[]{color, colorAttr2});
                Drawable wrap2 = DrawableCompat.wrap(r9.getThumbDrawable());
                DrawableCompat.setTintList(wrap2, colorStateList2);
                r9.setThumbDrawable(wrap2);
            }
            this.mActionView = r9;
        } else if (action.getImageMode() == 6) {
            Button button = new Button(getContext());
            this.mActionView = button;
            button.setText(action.getTitle());
            addView(this.mActionView);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mActionView.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -2;
            this.mActionView.setLayoutParams(layoutParams);
            int i2 = this.mTextActionPadding;
            this.mActionView.setPadding(i2, i2, i2, i2);
            this.mActionView.setOnClickListener(this);
        } else if (action.getIcon() != null) {
            if (action.isToggle()) {
                ImageToggle imageToggle = new ImageToggle(getContext());
                imageToggle.setChecked(action.isChecked());
                this.mActionView = imageToggle;
            } else {
                this.mActionView = new ImageView(getContext());
            }
            addView(this.mActionView);
            Drawable loadDrawable = this.mSliceAction.getIcon().loadDrawable(getContext());
            ((ImageView) this.mActionView).setImageDrawable(loadDrawable);
            if (color != -1 && this.mSliceAction.getImageMode() == 0 && loadDrawable != null) {
                DrawableCompat.setTint(loadDrawable, color);
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mActionView.getLayoutParams();
            int i3 = this.mImageSize;
            layoutParams2.width = i3;
            layoutParams2.height = i3;
            this.mActionView.setLayoutParams(layoutParams2);
            if (action.getImageMode() == 0) {
                int i4 = this.mImageSize;
                i = (i4 == -1 ? this.mIconSize : i4 - this.mIconSize) / 2;
            }
            this.mActionView.setPadding(i, i, i, i);
            this.mActionView.setBackground(SliceViewUtil.getDrawable(getContext(), Build.VERSION.SDK_INT >= 21 ? 16843868 : 16843534));
            this.mActionView.setOnClickListener(this);
        }
        if (this.mActionView != null) {
            this.mActionView.setContentDescription(action.getContentDescription() != null ? action.getContentDescription() : action.getTitle());
        }
    }

    public void setLoading(boolean isLoading) {
        if (isLoading) {
            if (this.mProgressView == null) {
                ProgressBar progressBar = (ProgressBar) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_progress_view, (ViewGroup) this, false);
                this.mProgressView = progressBar;
                addView(progressBar);
            }
            SliceViewUtil.tintIndeterminateProgressBar(getContext(), this.mProgressView);
        }
        this.mActionView.setVisibility(isLoading ? 8 : 0);
        this.mProgressView.setVisibility(isLoading ? 0 : 8);
    }

    public void toggle() {
        SliceActionImpl sliceActionImpl;
        if (this.mActionView == null || (sliceActionImpl = this.mSliceAction) == null || !sliceActionImpl.isToggle()) {
            return;
        }
        ((Checkable) this.mActionView).toggle();
    }
}
