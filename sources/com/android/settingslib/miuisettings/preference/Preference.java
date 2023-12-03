package com.android.settingslib.miuisettings.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.search.SearchUpdater;
import com.android.settingslib.R$id;
import com.android.settingslib.R$styleable;

/* loaded from: classes2.dex */
public class Preference extends androidx.preference.Preference implements PreferenceApiDiff {
    private PreferenceDelegate mDelegate;
    private boolean mForceRightArrow;
    private boolean mShowRightArrow;
    private int mTitleRes;

    public Preference(Context context) {
        super(context);
        this.mForceRightArrow = false;
        this.mShowRightArrow = false;
        init(context, null);
    }

    public Preference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mForceRightArrow = false;
        this.mShowRightArrow = false;
        init(context, attributeSet);
    }

    public Preference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mForceRightArrow = false;
        this.mShowRightArrow = false;
        init(context, attributeSet);
    }

    public Preference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mForceRightArrow = false;
        this.mShowRightArrow = false;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.Preference, i, i2);
        this.mTitleRes = getResourceId(obtainStyledAttributes, R$styleable.Preference_title, R$styleable.Preference_android_title, 0);
        obtainStyledAttributes.recycle();
        init(context, attributeSet);
    }

    public Preference(Context context, AttributeSet attributeSet, int i, int i2, boolean z) {
        super(context, attributeSet, i, i2);
        this.mForceRightArrow = false;
        this.mShowRightArrow = false;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.Preference, i, i2);
        this.mTitleRes = getResourceId(obtainStyledAttributes, R$styleable.Preference_title, R$styleable.Preference_android_title, 0);
        obtainStyledAttributes.recycle();
        this.mShowRightArrow = z;
        init(context, attributeSet);
    }

    public Preference(Context context, AttributeSet attributeSet, int i, boolean z) {
        super(context, attributeSet, i);
        this.mForceRightArrow = false;
        this.mShowRightArrow = false;
        this.mShowRightArrow = z;
        init(context, attributeSet);
    }

    public Preference(Context context, AttributeSet attributeSet, boolean z) {
        super(context, attributeSet);
        this.mForceRightArrow = false;
        this.mShowRightArrow = false;
        this.mShowRightArrow = z;
        init(context, attributeSet);
    }

    public Preference(Context context, boolean z) {
        super(context);
        this.mForceRightArrow = false;
        this.mShowRightArrow = false;
        this.mShowRightArrow = z;
        init(context, null);
    }

    public static int getResourceId(TypedArray typedArray, int i, int i2, int i3) {
        return typedArray.getResourceId(i, typedArray.getResourceId(i2, i3));
    }

    private void init(Context context, AttributeSet attributeSet) {
        boolean z = false;
        if (attributeSet != null) {
            boolean attributeBooleanValue = attributeSet.getAttributeBooleanValue("http://schemas.android.com/apk/miuisettings", "showIcon", false);
            TypedValue peekValue = context.obtainStyledAttributes(attributeSet, R$styleable.Preference).peekValue(R$styleable.Preference_showRightArrow);
            if (peekValue != null) {
                if (peekValue.type == 18 && peekValue.data != 0) {
                    z = true;
                }
                this.mShowRightArrow = z;
            }
            z = attributeBooleanValue;
        }
        if (this.mShowRightArrow && getIntent() == null && getFragment() == null && getOnPreferenceClickListener() == null) {
            setIntent(new Intent("com.android.settings.TEST_ARROW"));
        }
        this.mDelegate = new PreferenceDelegate(this, this, z);
        onCreateView(null);
    }

    public int getTitleRes() {
        return this.mTitleRes;
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        this.mDelegate.onAttachedToHierarchy(preferenceManager);
    }

    public void onBindView(View view) {
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        this.mDelegate.onBindViewStart(preferenceViewHolder.itemView);
        super.onBindViewHolder(preferenceViewHolder);
        this.mDelegate.onBindViewEnd(preferenceViewHolder.itemView);
        if (this.mShowRightArrow && getIntent() == null && getFragment() == null && getOnPreferenceClickListener() == null) {
            setIntent(new Intent("com.android.settings.TEST_ARROW"));
        }
        View findViewById = preferenceViewHolder.itemView.findViewById(R$id.arrow_right);
        if (findViewById == null || !this.mForceRightArrow) {
            return;
        }
        findViewById.setVisibility(this.mShowRightArrow ? 0 : 8);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public View onCreateView(ViewGroup viewGroup) {
        return null;
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
    }

    public void onFragmentBindPreference(ListView listView) {
        this.mDelegate.onFragmentBindPreference(listView);
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (getIntent() != null && getIntent().resolveActivityInfo(getContext().getPackageManager(), SearchUpdater.GOOGLE) == null) {
            setIntent(null);
        }
        super.performClick();
    }

    public void setShowRightArrow(boolean z) {
        this.mForceRightArrow = true;
        this.mShowRightArrow = z;
    }

    @Override // androidx.preference.Preference
    public void setTitle(int i) {
        setTitle(getContext().getString(i));
        this.mTitleRes = i;
    }
}
