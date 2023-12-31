package com.google.android.setupdesign;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupdesign.template.RecyclerMixin;
import com.google.android.setupdesign.template.RecyclerViewScrollHandlingDelegate;
import com.google.android.setupdesign.template.RequireScrollMixin;

/* loaded from: classes2.dex */
public class GlifRecyclerLayout extends GlifLayout {
    protected RecyclerMixin recyclerMixin;

    public GlifRecyclerLayout(Context context) {
        this(context, 0, 0);
    }

    public GlifRecyclerLayout(Context context, int i) {
        this(context, i, 0);
    }

    public GlifRecyclerLayout(Context context, int i, int i2) {
        super(context, i, i2);
        init(null, 0);
    }

    public GlifRecyclerLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet, 0);
    }

    @TargetApi(11)
    public GlifRecyclerLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        if (isInEditMode()) {
            return;
        }
        this.recyclerMixin.parseAttributes(attributeSet, i);
        registerMixin(RecyclerMixin.class, this.recyclerMixin);
        RequireScrollMixin requireScrollMixin = (RequireScrollMixin) getMixin(RequireScrollMixin.class);
        requireScrollMixin.setScrollHandlingDelegate(new RecyclerViewScrollHandlingDelegate(requireScrollMixin, getRecyclerView()));
        View findManagedViewById = findManagedViewById(R$id.sud_landscape_content_area);
        if (findManagedViewById != null) {
            GlifLayout.applyPartnerCustomizationContentPaddingTopStyle(findManagedViewById);
        }
        updateLandscapeMiddleHorizontalSpacing();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.GlifLayout, com.google.android.setupcompat.PartnerCustomizationLayout, com.google.android.setupcompat.internal.TemplateLayout
    public ViewGroup findContainer(int i) {
        if (i == 0) {
            i = R$id.sud_recycler_view;
        }
        return super.findContainer(i);
    }

    @Override // com.google.android.setupcompat.internal.TemplateLayout
    public <T extends View> T findManagedViewById(int i) {
        T t;
        View header = this.recyclerMixin.getHeader();
        return (header == null || (t = (T) header.findViewById(i)) == null) ? (T) super.findViewById(i) : t;
    }

    public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getAdapter() {
        return this.recyclerMixin.getAdapter();
    }

    public Drawable getDivider() {
        return this.recyclerMixin.getDivider();
    }

    @Deprecated
    public int getDividerInset() {
        return this.recyclerMixin.getDividerInset();
    }

    public int getDividerInsetEnd() {
        return this.recyclerMixin.getDividerInsetEnd();
    }

    public int getDividerInsetStart() {
        return this.recyclerMixin.getDividerInsetStart();
    }

    public RecyclerView getRecyclerView() {
        return this.recyclerMixin.getRecyclerView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.GlifLayout, com.google.android.setupcompat.PartnerCustomizationLayout, com.google.android.setupcompat.internal.TemplateLayout
    public View onInflateTemplate(LayoutInflater layoutInflater, int i) {
        if (i == 0) {
            i = R$layout.sud_glif_recycler_template;
        }
        return super.onInflateTemplate(layoutInflater, i);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.recyclerMixin.onLayout();
    }

    @Override // com.google.android.setupcompat.internal.TemplateLayout
    protected void onTemplateInflated() {
        View findViewById = findViewById(R$id.sud_recycler_view);
        if (!(findViewById instanceof RecyclerView)) {
            throw new IllegalStateException("GlifRecyclerLayout should use a template with recycler view");
        }
        this.recyclerMixin = new RecyclerMixin(this, (RecyclerView) findViewById);
    }

    public void setAdapter(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        this.recyclerMixin.setAdapter(adapter);
    }

    @Deprecated
    public void setDividerInset(int i) {
        this.recyclerMixin.setDividerInset(i);
    }

    public void setDividerItemDecoration(DividerItemDecoration dividerItemDecoration) {
        this.recyclerMixin.setDividerItemDecoration(dividerItemDecoration);
    }
}
