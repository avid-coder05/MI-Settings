package miuix.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.constraintlayout.widget.Barrier;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.settings.search.SearchUpdater;
import miuix.appcompat.R$id;
import miuix.appcompat.app.floatingactivity.FloatingABOLayoutSpec;

/* loaded from: classes5.dex */
public class DialogParentPanel extends ConstraintLayout {
    private final int[] EMPTY;
    private LinearLayout mButtonGroup;
    private View mButtonPanel;
    private View mContentPanel;
    private View mCustomPanel;
    private FloatingABOLayoutSpec mFloatingWindowSize;
    private Barrier mLayoutSplit;
    private int[] mReferenceIds;
    private boolean mShouldAdjustLayout;
    private View mTopPanel;

    public DialogParentPanel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.EMPTY = new int[0];
        FloatingABOLayoutSpec floatingABOLayoutSpec = new FloatingABOLayoutSpec(context, attributeSet);
        this.mFloatingWindowSize = floatingABOLayoutSpec;
        floatingABOLayoutSpec.setIsInDialogMode(true);
    }

    private void changeHorizontalParams(ConstraintLayout.LayoutParams layoutParams, int i) {
        layoutParams.startToStart = i;
        layoutParams.endToEnd = i;
    }

    private void changeVerticalParams(ConstraintLayout.LayoutParams layoutParams, int i) {
        layoutParams.topToTop = i;
        layoutParams.bottomToBottom = i;
    }

    private ConstraintLayout.LayoutParams getChildLayoutParams(View view) {
        return (ConstraintLayout.LayoutParams) view.getLayoutParams();
    }

    private void init() {
        this.mButtonPanel = findViewById(R$id.buttonPanel);
        int i = R$id.topPanel;
        this.mTopPanel = findViewById(i);
        int i2 = R$id.contentPanel;
        this.mContentPanel = findViewById(i2);
        int i3 = R$id.customPanel;
        this.mCustomPanel = findViewById(i3);
        this.mButtonGroup = (LinearLayout) findViewById(R$id.buttonGroup);
        this.mReferenceIds = new int[]{i, i2, i3};
    }

    public void adjustLayout() {
        ConstraintLayout.LayoutParams childLayoutParams = getChildLayoutParams(this.mButtonPanel);
        ConstraintLayout.LayoutParams childLayoutParams2 = getChildLayoutParams(this.mTopPanel);
        ConstraintLayout.LayoutParams childLayoutParams3 = getChildLayoutParams(this.mContentPanel);
        ConstraintLayout.LayoutParams childLayoutParams4 = getChildLayoutParams(this.mCustomPanel);
        if (shouldAdjustLayout()) {
            this.mLayoutSplit.setType(6);
            this.mLayoutSplit.setReferencedIds(this.mReferenceIds);
            this.mButtonGroup.setOrientation(1);
            childLayoutParams2.matchConstraintPercentWidth = 0.5f;
            childLayoutParams2.startToStart = 0;
            childLayoutParams2.topToTop = 0;
            childLayoutParams2.endToEnd = -1;
            childLayoutParams3.matchConstraintPercentWidth = 0.5f;
            childLayoutParams3.startToStart = 0;
            childLayoutParams3.endToEnd = -1;
            childLayoutParams3.topToBottom = R$id.topPanel;
            ((ViewGroup.MarginLayoutParams) childLayoutParams3).height = 0;
            childLayoutParams3.constrainedHeight = false;
            childLayoutParams3.matchConstraintDefaultHeight = 0;
            childLayoutParams4.matchConstraintPercentWidth = 0.5f;
            childLayoutParams4.startToStart = 0;
            childLayoutParams4.topToBottom = R$id.contentPanel;
            childLayoutParams4.endToEnd = -1;
            childLayoutParams4.bottomToTop = -1;
            childLayoutParams4.bottomToBottom = 0;
            ((ViewGroup.MarginLayoutParams) childLayoutParams4).height = 0;
            childLayoutParams4.constrainedHeight = false;
            childLayoutParams4.matchConstraintDefaultHeight = 0;
            childLayoutParams.matchConstraintPercentWidth = 0.5f;
            childLayoutParams.startToStart = -1;
            childLayoutParams.topToBottom = -1;
            childLayoutParams.endToEnd = 0;
            changeVerticalParams(childLayoutParams, 0);
        } else {
            this.mLayoutSplit.setReferencedIds(this.EMPTY);
            this.mButtonGroup.setOrientation(0);
            childLayoutParams2.matchConstraintPercentWidth = 1.0f;
            changeHorizontalParams(childLayoutParams2, 0);
            childLayoutParams2.topToTop = 0;
            childLayoutParams3.matchConstraintPercentWidth = 1.0f;
            childLayoutParams3.constrainedHeight = true;
            ((ViewGroup.MarginLayoutParams) childLayoutParams3).height = -2;
            changeHorizontalParams(childLayoutParams3, 0);
            childLayoutParams4.matchConstraintPercentWidth = 1.0f;
            childLayoutParams4.constrainedHeight = true;
            ((ViewGroup.MarginLayoutParams) childLayoutParams4).height = -2;
            changeHorizontalParams(childLayoutParams4, 0);
            childLayoutParams4.bottomToTop = R$id.buttonPanel;
            childLayoutParams.matchConstraintPercentWidth = 1.0f;
            changeHorizontalParams(childLayoutParams, 0);
            childLayoutParams.startToEnd = -1;
            childLayoutParams.topToTop = -1;
            childLayoutParams.topToBottom = R$id.customPanel;
            childLayoutParams.bottomToBottom = 0;
        }
        this.mButtonPanel.setLayoutParams(childLayoutParams);
        this.mTopPanel.setLayoutParams(childLayoutParams2);
        this.mContentPanel.setLayoutParams(childLayoutParams3);
        this.mCustomPanel.setLayoutParams(childLayoutParams4);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mFloatingWindowSize.onConfigurationChanged();
        adjustLayout();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.constraintlayout.widget.ConstraintLayout, android.view.View
    public void onMeasure(int i, int i2) {
        int heightMeasureSpecForDialog = this.mFloatingWindowSize.getHeightMeasureSpecForDialog(i2);
        if (shouldAdjustLayout()) {
            heightMeasureSpecForDialog = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpecForDialog), SearchUpdater.SIM);
        }
        super.onMeasure(this.mFloatingWindowSize.getWidthMeasureSpecForDialog(i), heightMeasureSpecForDialog);
    }

    public void setShouldAdjustLayout(boolean z) {
        this.mShouldAdjustLayout = z;
    }

    public boolean shouldAdjustLayout() {
        return this.mShouldAdjustLayout;
    }
}
