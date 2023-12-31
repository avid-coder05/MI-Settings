package com.google.android.setupdesign.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.setupdesign.R$attr;
import com.google.android.setupdesign.R$id;
import com.google.android.setupdesign.R$layout;
import com.google.android.setupdesign.R$style;

/* loaded from: classes2.dex */
public class NavigationBar extends LinearLayout implements View.OnClickListener {
    private Button backButton;
    private NavigationBarListener listener;
    private Button moreButton;
    private Button nextButton;

    /* loaded from: classes2.dex */
    public interface NavigationBarListener {
        void onNavigateBack();

        void onNavigateNext();
    }

    public NavigationBar(Context context) {
        super(getThemedContext(context));
        init();
    }

    public NavigationBar(Context context, AttributeSet attributeSet) {
        super(getThemedContext(context), attributeSet);
        init();
    }

    @TargetApi(11)
    public NavigationBar(Context context, AttributeSet attributeSet, int i) {
        super(getThemedContext(context), attributeSet, i);
        init();
    }

    private static int getNavbarTheme(Context context) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{R$attr.sudNavBarTheme, 16842800, 16842801});
        int resourceId = obtainStyledAttributes.getResourceId(0, 0);
        if (resourceId == 0) {
            float[] fArr = new float[3];
            float[] fArr2 = new float[3];
            Color.colorToHSV(obtainStyledAttributes.getColor(1, 0), fArr);
            Color.colorToHSV(obtainStyledAttributes.getColor(2, 0), fArr2);
            resourceId = fArr[2] > fArr2[2] ? R$style.SudNavBarThemeDark : R$style.SudNavBarThemeLight;
        }
        obtainStyledAttributes.recycle();
        return resourceId;
    }

    private static Context getThemedContext(Context context) {
        return new ContextThemeWrapper(context, getNavbarTheme(context));
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        View.inflate(getContext(), R$layout.sud_navbar_view, this);
        this.nextButton = (Button) findViewById(R$id.sud_navbar_next);
        this.backButton = (Button) findViewById(R$id.sud_navbar_back);
        this.moreButton = (Button) findViewById(R$id.sud_navbar_more);
    }

    public Button getBackButton() {
        return this.backButton;
    }

    public Button getMoreButton() {
        return this.moreButton;
    }

    public Button getNextButton() {
        return this.nextButton;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.listener != null) {
            if (view == getBackButton()) {
                this.listener.onNavigateBack();
            } else if (view == getNextButton()) {
                this.listener.onNavigateNext();
            }
        }
    }

    public void setNavigationBarListener(NavigationBarListener navigationBarListener) {
        this.listener = navigationBarListener;
        if (navigationBarListener != null) {
            getBackButton().setOnClickListener(this);
            getNextButton().setOnClickListener(this);
        }
    }
}
