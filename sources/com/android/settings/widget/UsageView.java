package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$styleable;
import java.util.Locale;

/* loaded from: classes2.dex */
public class UsageView extends FrameLayout {
    private final TextView[] mBottomLabels;
    private final TextView[] mLabels;
    private final UsageGraph mUsageGraph;

    public UsageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R$layout.usage_view, this);
        this.mUsageGraph = (UsageGraph) findViewById(R$id.usage_graph);
        TextView[] textViewArr = {(TextView) findViewById(R$id.label_bottom), (TextView) findViewById(R$id.label_middle), (TextView) findViewById(R$id.label_top)};
        this.mLabels = textViewArr;
        this.mBottomLabels = new TextView[]{(TextView) findViewById(R$id.label_start), (TextView) findViewById(R$id.label_end)};
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.UsageView, 0, 0);
        int i = R$styleable.UsageView_sideLabels;
        if (obtainStyledAttributes.hasValue(i)) {
            setSideLabels(obtainStyledAttributes.getTextArray(i));
        }
        int i2 = R$styleable.UsageView_bottomLabels;
        if (obtainStyledAttributes.hasValue(i2)) {
            setBottomLabels(obtainStyledAttributes.getTextArray(i2));
        }
        int i3 = R$styleable.UsageView_textColor;
        if (obtainStyledAttributes.hasValue(i3)) {
            int color = obtainStyledAttributes.getColor(i3, 0);
            for (TextView textView : textViewArr) {
                textView.setTextColor(color);
            }
            for (TextView textView2 : this.mBottomLabels) {
                textView2.setTextColor(color);
            }
        }
        int i4 = R$styleable.UsageView_android_gravity;
        if (obtainStyledAttributes.hasValue(i4)) {
            int i5 = obtainStyledAttributes.getInt(i4, 0);
            if (i5 == 8388613) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R$id.graph_label_group);
                LinearLayout linearLayout2 = (LinearLayout) findViewById(R$id.label_group);
                linearLayout.removeView(linearLayout2);
                linearLayout.addView(linearLayout2);
                linearLayout2.setGravity(8388613);
                LinearLayout linearLayout3 = (LinearLayout) findViewById(R$id.bottom_label_group);
                View findViewById = linearLayout3.findViewById(R$id.bottom_label_space);
                linearLayout3.removeView(findViewById);
                linearLayout3.addView(findViewById);
            } else if (i5 != 8388611) {
                throw new IllegalArgumentException("Unsupported gravity " + i5);
            }
        }
        this.mUsageGraph.setAccentColor(obtainStyledAttributes.getColor(R$styleable.UsageView_android_colorAccent, 0));
        obtainStyledAttributes.recycle();
        String language = Locale.getDefault().getLanguage();
        if (TextUtils.equals(language, new Locale("fa").getLanguage()) || TextUtils.equals(language, new Locale("ur").getLanguage())) {
            findViewById(R$id.graph_label_group).setLayoutDirection(0);
            findViewById(R$id.bottom_label_group).setLayoutDirection(0);
        }
    }

    private void setWeight(int i, float f) {
        View findViewById = findViewById(i);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
        layoutParams.weight = f;
        findViewById.setLayoutParams(layoutParams);
    }

    public void addPath(SparseIntArray sparseIntArray) {
        this.mUsageGraph.addPath(sparseIntArray);
    }

    public void addProjectedPath(SparseIntArray sparseIntArray) {
        this.mUsageGraph.addProjectedPath(sparseIntArray);
    }

    public void clearPaths() {
        this.mUsageGraph.clearPaths();
    }

    public void configureGraph(int i, int i2) {
        this.mUsageGraph.setMax(i, i2);
    }

    public void setAccentColor(int i) {
        this.mUsageGraph.setAccentColor(i);
    }

    public void setBottomLabels(CharSequence[] charSequenceArr) {
        if (charSequenceArr.length != this.mBottomLabels.length) {
            throw new IllegalArgumentException("Invalid number of labels");
        }
        int i = 0;
        while (true) {
            TextView[] textViewArr = this.mBottomLabels;
            if (i >= textViewArr.length) {
                return;
            }
            textViewArr[i].setText(charSequenceArr[i]);
            i++;
        }
    }

    public void setDividerColors(int i, int i2) {
        this.mUsageGraph.setDividerColors(i, i2);
    }

    public void setDividerLoc(int i) {
        this.mUsageGraph.setDividerLoc(i);
    }

    public void setSideLabelWeights(float f, float f2) {
        setWeight(R$id.space1, f);
        setWeight(R$id.space2, f2);
    }

    public void setSideLabels(CharSequence[] charSequenceArr) {
        if (charSequenceArr.length != this.mLabels.length) {
            throw new IllegalArgumentException("Invalid number of labels");
        }
        int i = 0;
        while (true) {
            TextView[] textViewArr = this.mLabels;
            if (i >= textViewArr.length) {
                return;
            }
            textViewArr[i].setText(charSequenceArr[i]);
            i++;
        }
    }
}
