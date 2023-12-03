package miuix.slidingwidget.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import java.util.HashMap;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.property.ViewProperty;

/* loaded from: classes5.dex */
public class SlidingLinearLayout extends LinearLayout {
    private final HashMap<View, Pair<Float, Float>> preAddViewPairs;
    private final int[] preLayout;
    private final HashMap<View, Pair<Float, Float>> preRemoveViewPairs;

    public SlidingLinearLayout(Context context) {
        this(context, null);
    }

    public SlidingLinearLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SlidingLinearLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.preAddViewPairs = new HashMap<>();
        this.preRemoveViewPairs = new HashMap<>();
        this.preLayout = new int[4];
        setLayoutTransition(null);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        float floatValue;
        float floatValue2;
        float floatValue3;
        float floatValue4;
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            int childCount = getChildCount();
            char c = 0;
            boolean z2 = getOrientation() != 1 ? Math.abs(this.preLayout[0] - i) > Math.abs(this.preLayout[2] - i3) : Math.abs(this.preLayout[1] - i2) > Math.abs(this.preLayout[3] - i4);
            int i6 = 0;
            while (i6 < childCount) {
                View childAt = getChildAt(i6);
                HashMap<View, Pair<Float, Float>> hashMap = this.preAddViewPairs;
                if (hashMap == null || hashMap.size() <= 0) {
                    i5 = i6;
                } else {
                    Pair<Float, Float> pair = this.preAddViewPairs.get(childAt);
                    if (pair == null || childAt.getVisibility() == 8) {
                        i5 = i6;
                    } else {
                        if ((childAt.getX() != ((Float) pair.first).floatValue() || childAt.getY() != ((Float) pair.second).floatValue()) && !z2) {
                            floatValue3 = ((Float) pair.first).floatValue() - childAt.getX();
                            floatValue4 = ((Float) pair.second).floatValue() - childAt.getY();
                        } else if (childAt.getX() == ((Float) pair.first).floatValue() && childAt.getY() == ((Float) pair.second).floatValue() && z2) {
                            int[] iArr = this.preLayout;
                            float f = iArr[c] - i;
                            floatValue4 = iArr[1] - i2;
                            floatValue3 = f;
                        } else {
                            floatValue3 = 0.0f;
                            floatValue4 = 0.0f;
                        }
                        AnimState animState = new AnimState("start");
                        ViewProperty viewProperty = ViewProperty.TRANSLATION_X;
                        i5 = i6;
                        AnimState add = animState.add(viewProperty, floatValue3);
                        ViewProperty viewProperty2 = ViewProperty.TRANSLATION_Y;
                        AnimState add2 = add.add(viewProperty2, floatValue4);
                        Folme.useAt(childAt).state().setTo(add2).fromTo(add2, new AnimState("end").add(viewProperty, 0.0d).add(viewProperty2, 0.0d), new AnimConfig[0]);
                    }
                    this.preAddViewPairs.remove(childAt);
                }
                HashMap<View, Pair<Float, Float>> hashMap2 = this.preRemoveViewPairs;
                if (hashMap2 != null && hashMap2.size() > 0) {
                    Pair<Float, Float> pair2 = this.preRemoveViewPairs.get(childAt);
                    if (pair2 != null && childAt.getVisibility() != 8) {
                        if ((childAt.getX() != ((Float) pair2.first).floatValue() || childAt.getY() != ((Float) pair2.second).floatValue()) && !z2) {
                            floatValue = ((Float) pair2.first).floatValue() - childAt.getX();
                            floatValue2 = ((Float) pair2.second).floatValue() - childAt.getY();
                        } else if (childAt.getX() == ((Float) pair2.first).floatValue() && childAt.getY() == ((Float) pair2.second).floatValue() && z2) {
                            int[] iArr2 = this.preLayout;
                            floatValue = iArr2[0] - i;
                            floatValue2 = iArr2[1] - i2;
                        } else {
                            floatValue2 = 0.0f;
                            floatValue = 0.0f;
                        }
                        AnimState animState2 = new AnimState("start");
                        ViewProperty viewProperty3 = ViewProperty.TRANSLATION_X;
                        AnimState add3 = animState2.add(viewProperty3, floatValue);
                        ViewProperty viewProperty4 = ViewProperty.TRANSLATION_Y;
                        AnimState add4 = add3.add(viewProperty4, floatValue2);
                        Folme.useAt(childAt).state().setTo(add4).fromTo(add4, new AnimState("end").add(viewProperty3, 0.0d).add(viewProperty4, 0.0d), new AnimConfig[0]);
                    }
                    this.preRemoveViewPairs.remove(childAt);
                }
                i6 = i5 + 1;
                c = 0;
            }
            this.preAddViewPairs.clear();
            this.preRemoveViewPairs.clear();
            int[] iArr3 = this.preLayout;
            iArr3[0] = i;
            iArr3[1] = i2;
            iArr3[2] = i3;
            iArr3[3] = i4;
        }
    }
}
