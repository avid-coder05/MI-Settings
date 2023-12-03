package com.android.settings.display;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;
import com.android.settings.display.ScreenColorBitMapView;
import com.android.settingslib.miuisettings.preference.Preference;
import miui.os.Build;
import miuix.animation.Folme;
import miuix.miuixbasewidget.widget.FilterSortView;
import miuix.preference.FolmeAnimationController;

/* loaded from: classes.dex */
public class ScreenColorPreference extends Preference implements View.OnClickListener, FolmeAnimationController {
    private ScreenColorBitMapView mBitMapView;
    private FilterSortView.TabView mCoolView;
    private FilterSortView.TabView mCustomView;
    private FilterSortView mFilterSortView;
    private int mLastCheckId;
    private FilterSortView.TabView mNatureView;
    private FilterSortView.TabView mWarmView;

    public ScreenColorPreference(Context context) {
        this(context, null);
    }

    public ScreenColorPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ScreenColorPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(R.layout.screen_color_preference);
    }

    private void updateTextColor(boolean z) {
        FilterSortView filterSortView = this.mFilterSortView;
        if (filterSortView != null) {
            filterSortView.setAlpha(z ? 1.0f : 0.4f);
        }
        ScreenColorBitMapView screenColorBitMapView = this.mBitMapView;
        if (screenColorBitMapView != null) {
            screenColorBitMapView.setAlpha(z ? 1.0f : 0.4f);
        }
    }

    public void enablePick(int i, boolean z) {
        if (i == 2) {
            this.mFilterSortView.setFilteredTab(this.mNatureView);
        } else if (i == 3) {
            this.mFilterSortView.setFilteredTab(this.mCoolView);
        } else if (i == 1) {
            this.mFilterSortView.setFilteredTab(this.mWarmView);
        } else {
            this.mFilterSortView.setFilteredTab(this.mCustomView);
        }
        if (z) {
            Settings.System.putInt(getContext().getContentResolver(), "screen_color_level", i);
        }
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        Folme.clean(view);
        view.setBackgroundColor(0);
        this.mNatureView = (FilterSortView.TabView) view.findViewById(R.id.nature_color);
        this.mCoolView = (FilterSortView.TabView) view.findViewById(R.id.cool_color);
        this.mWarmView = (FilterSortView.TabView) view.findViewById(R.id.warm_color);
        this.mCustomView = (FilterSortView.TabView) view.findViewById(R.id.custom_color);
        this.mFilterSortView = (FilterSortView) view.findViewById(R.id.filter_sort_view);
        ScreenColorBitMapView screenColorBitMapView = (ScreenColorBitMapView) view.findViewById(R.id.screen_color_image);
        this.mBitMapView = screenColorBitMapView;
        screenColorBitMapView.setCallback(new ScreenColorBitMapView.Callback() { // from class: com.android.settings.display.ScreenColorPreference.1
            @Override // com.android.settings.display.ScreenColorBitMapView.Callback
            public void onAdjust() {
                ScreenColorPreference.this.enablePick(-1, false);
                ScreenColorPreference.this.mLastCheckId = R.id.custom_color;
            }
        });
        final int i = Settings.System.getInt(getContext().getContentResolver(), "screen_color_level", 2);
        enablePick(i, false);
        if (Build.IS_TABLET) {
            view.postDelayed(new Runnable() { // from class: com.android.settings.display.ScreenColorPreference.2
                @Override // java.lang.Runnable
                public void run() {
                    ScreenColorPreference.this.enablePick(i, false);
                }
            }, 50L);
        }
        updateTextColor(isEnabled());
        this.mFilterSortView.setTabIncatorVisibility(8);
        this.mNatureView.setOnClickListener(this);
        this.mCoolView.setOnClickListener(this);
        this.mWarmView.setOnClickListener(this);
        this.mCustomView.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mLastCheckId == view.getId()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.nature_color) {
            enablePick(2, true);
            this.mBitMapView.setCircleLocation(2);
        } else if (id == R.id.warm_color) {
            enablePick(1, true);
            this.mBitMapView.setCircleLocation(1);
        } else if (id == R.id.cool_color) {
            enablePick(3, true);
            this.mBitMapView.setCircleLocation(3);
        } else if (id == R.id.custom_color) {
            enablePick(-1, false);
        }
        this.mLastCheckId = view.getId();
    }

    @Override // androidx.preference.Preference
    public void onParentChanged(androidx.preference.Preference preference, boolean z) {
        super.onParentChanged(preference, z);
        updateTextColor(!z);
    }
}
