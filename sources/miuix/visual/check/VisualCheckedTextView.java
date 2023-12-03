package miuix.visual.check;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatTextView;
import java.lang.ref.WeakReference;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.ColorProperty;
import miuix.animation.property.IIntValueProperty;
import miuix.visualcheck.R$color;

/* loaded from: classes5.dex */
public class VisualCheckedTextView extends AppCompatTextView implements VisualCheckItem {
    private static int[] CHECKED_STATE = {16842912};
    private static int[] UNCHECKED_STATE = {-16842912};
    private IStateStyle iCheckedStateStyle;
    private IStateStyle iUnCheckedStateStyle;
    private int mCheckedColor;
    private ColorProperty mColorProperty;
    private TransitionListener mListener;
    private int mUncheckedColor;

    /* loaded from: classes5.dex */
    private static class InnerTransitionListener extends TransitionListener {
        WeakReference<VisualCheckedTextView> mRef;

        InnerTransitionListener(VisualCheckedTextView visualCheckedTextView) {
            this.mRef = new WeakReference<>(visualCheckedTextView);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onUpdate(Object obj, IIntValueProperty iIntValueProperty, int i, float f, boolean z) {
            VisualCheckedTextView visualCheckedTextView = this.mRef.get();
            if (visualCheckedTextView != null) {
                visualCheckedTextView.setTextColor(i);
            }
        }
    }

    public VisualCheckedTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mUncheckedColor = getTextColors().getColorForState(UNCHECKED_STATE, getResources().getColor(R$color.visual_check_textview_unchecked_text_color));
        this.mCheckedColor = getTextColors().getColorForState(CHECKED_STATE, getResources().getColor(R$color.visual_check_textview_checked_text_color));
        this.mListener = new InnerTransitionListener(this);
        this.mColorProperty = new ColorProperty("checkedTextView");
        Folme.clean("text_color_checked");
        Folme.clean("text_color_unchecked");
        this.iCheckedStateStyle = Folme.useValue("text_color_checked").setFlags(1L);
        this.iUnCheckedStateStyle = Folme.useValue("text_color_unchecked").setFlags(1L);
    }

    @Override // miuix.visual.check.VisualCheckItem
    public void onChecked(boolean z) {
        if (z) {
            setTextColor(this.mCheckedColor);
        } else {
            this.iUnCheckedStateStyle.setTo(this.mColorProperty, Integer.valueOf(this.mCheckedColor)).to(this.mColorProperty, Integer.valueOf(this.mUncheckedColor), this.mListener);
        }
    }

    @Override // miuix.visual.check.VisualCheckItem
    public void onVisualCheckBoxTouchEvent(VisualCheckBox visualCheckBox, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            this.iCheckedStateStyle.setTo(this.mColorProperty, Integer.valueOf(this.mUncheckedColor)).to(this.mColorProperty, Integer.valueOf(this.mCheckedColor), this.mListener);
        }
    }
}
