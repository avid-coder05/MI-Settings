package miuix.visual.check;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import miuix.visual.check.VisualCheckBox;
import miuix.visualcheck.R$styleable;

/* loaded from: classes5.dex */
public class VisualCheckGroup extends FlowLayout {
    private int mCheckedId;
    private VisualCheckBox.OnCheckedChangeListener mChildOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;
    private boolean mProtectFromCheckedChange;

    /* loaded from: classes5.dex */
    private class CheckedStateTracker implements VisualCheckBox.OnCheckedChangeListener {
        private CheckedStateTracker() {
        }

        @Override // miuix.visual.check.VisualCheckBox.OnCheckedChangeListener
        public void onCheckedChanged(VisualCheckBox visualCheckBox, boolean z) {
            if (VisualCheckGroup.this.mProtectFromCheckedChange) {
                return;
            }
            VisualCheckGroup.this.mProtectFromCheckedChange = true;
            if (VisualCheckGroup.this.mCheckedId != -1) {
                VisualCheckGroup visualCheckGroup = VisualCheckGroup.this;
                visualCheckGroup.setCheckedStateForView(visualCheckGroup.mCheckedId, false);
            }
            VisualCheckGroup.this.mProtectFromCheckedChange = false;
            VisualCheckGroup.this.setCheckedId(visualCheckBox.getId());
        }
    }

    /* loaded from: classes5.dex */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(VisualCheckGroup visualCheckGroup, int i);
    }

    /* loaded from: classes5.dex */
    private class PassThroughHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {
        private PassThroughHierarchyChangeListener() {
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewAdded(View view, View view2) {
            if (view == VisualCheckGroup.this && (view2 instanceof VisualCheckBox)) {
                if (view2.getId() == -1) {
                    view2.setId(View.generateViewId());
                }
                ((VisualCheckBox) view2).setOnCheckedChangeWidgetListener(VisualCheckGroup.this.mChildOnCheckedChangeListener);
            }
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewRemoved(View view, View view2) {
            if (view == VisualCheckGroup.this && (view2 instanceof VisualCheckBox)) {
                ((VisualCheckBox) view2).setOnCheckedChangeWidgetListener(null);
            }
        }
    }

    public VisualCheckGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCheckedId = -1;
        this.mProtectFromCheckedChange = false;
        PassThroughHierarchyChangeListener passThroughHierarchyChangeListener = new PassThroughHierarchyChangeListener();
        this.mPassThroughListener = passThroughHierarchyChangeListener;
        super.setOnHierarchyChangeListener(passThroughHierarchyChangeListener);
        this.mChildOnCheckedChangeListener = new CheckedStateTracker();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.VisualCheckGroup);
        this.mCheckedId = obtainStyledAttributes.getResourceId(R$styleable.VisualCheckGroup_checkedButton, -1);
        obtainStyledAttributes.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCheckedId(int i) {
        this.mCheckedId = i;
        OnCheckedChangeListener onCheckedChangeListener = this.mOnCheckedChangeListener;
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCheckedStateForView(int i, boolean z) {
        View findViewById = findViewById(i);
        if (findViewById == null || !(findViewById instanceof VisualCheckBox)) {
            return;
        }
        ((VisualCheckBox) findViewById).setChecked(z);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        int i = this.mCheckedId;
        if (i != -1) {
            this.mProtectFromCheckedChange = true;
            setCheckedStateForView(i, true);
            this.mProtectFromCheckedChange = false;
            setCheckedId(this.mCheckedId);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }
}
