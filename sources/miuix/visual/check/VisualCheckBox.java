package miuix.visual.check;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes5.dex */
public class VisualCheckBox extends LinearLayout {
    private boolean mChecked;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;
    private List<VisualCheckItem> mVisualCheckItems;

    /* loaded from: classes5.dex */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(VisualCheckBox visualCheckBox, boolean z);
    }

    /* loaded from: classes5.dex */
    private class PassThroughHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {
        private PassThroughHierarchyChangeListener() {
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewAdded(View view, View view2) {
            VisualCheckBox visualCheckBox = VisualCheckBox.this;
            if (view == visualCheckBox && (view2 instanceof VisualCheckItem)) {
                visualCheckBox.mVisualCheckItems.add((VisualCheckItem) view2);
            }
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewRemoved(View view, View view2) {
            VisualCheckBox visualCheckBox = VisualCheckBox.this;
            if (view == visualCheckBox && (view2 instanceof VisualCheckItem)) {
                visualCheckBox.mVisualCheckItems.remove(view2);
            }
        }
    }

    public VisualCheckBox(Context context) {
        this(context, null);
    }

    public VisualCheckBox(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VisualCheckBox(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mVisualCheckItems = new ArrayList();
        setOrientation(1);
        PassThroughHierarchyChangeListener passThroughHierarchyChangeListener = new PassThroughHierarchyChangeListener();
        this.mPassThroughListener = passThroughHierarchyChangeListener;
        super.setOnHierarchyChangeListener(passThroughHierarchyChangeListener);
    }

    private void notifyChecked(boolean z) {
        Iterator<VisualCheckItem> it = this.mVisualCheckItems.iterator();
        while (it.hasNext()) {
            it.next().onChecked(z);
        }
    }

    private void notifyTouchEvent(MotionEvent motionEvent) {
        Iterator<VisualCheckItem> it = this.mVisualCheckItems.iterator();
        while (it.hasNext()) {
            it.next().onVisualCheckBoxTouchEvent(this, motionEvent);
        }
    }

    private CharSequence obtainDescriptionFromChild() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == 0) {
                if (!TextUtils.isEmpty(childAt.getContentDescription())) {
                    return childAt.getContentDescription();
                }
                if (childAt instanceof TextView) {
                    return ((TextView) childAt).getText();
                }
            }
        }
        return null;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return VisualCheckBox.class.getName();
    }

    @ViewDebug.ExportedProperty
    public boolean isChecked() {
        return this.mChecked;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (TextUtils.isEmpty(getContentDescription())) {
            CharSequence obtainDescriptionFromChild = obtainDescriptionFromChild();
            if (TextUtils.isEmpty(obtainDescriptionFromChild)) {
                return;
            }
            setContentDescription(obtainDescriptionFromChild);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setChecked(this.mChecked);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setCheckable(!this.mChecked);
        accessibilityNodeInfo.setSelected(this.mChecked);
        accessibilityNodeInfo.setChecked(this.mChecked);
        CharSequence obtainDescriptionFromChild = obtainDescriptionFromChild();
        if (TextUtils.isEmpty(obtainDescriptionFromChild)) {
            return;
        }
        accessibilityNodeInfo.setText(obtainDescriptionFromChild);
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        CharSequence obtainDescriptionFromChild = obtainDescriptionFromChild();
        if (TextUtils.isEmpty(obtainDescriptionFromChild)) {
            return;
        }
        accessibilityEvent.getText().add(obtainDescriptionFromChild);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mVisualCheckItems.size() > 0) {
            notifyTouchEvent(motionEvent);
            if (motionEvent.getAction() == 1) {
                toggle();
                HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_MESH_NORMAL);
            }
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    public void setChecked(boolean z) {
        if (this.mChecked != z) {
            this.mChecked = z;
            notifyChecked(z);
            OnCheckedChangeListener onCheckedChangeListener = this.mOnCheckedChangeWidgetListener;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, this.mChecked);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnCheckedChangeWidgetListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeWidgetListener = onCheckedChangeListener;
    }

    public void toggle() {
        if (isChecked()) {
            return;
        }
        setChecked(!this.mChecked);
        sendAccessibilityEvent(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON);
    }
}
