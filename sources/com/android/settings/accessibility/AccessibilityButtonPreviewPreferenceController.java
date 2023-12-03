package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.ImageView;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.LayoutPreference;

/* loaded from: classes.dex */
public class AccessibilityButtonPreviewPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private static final float DEFAULT_OPACITY = 0.55f;
    private static final int DEFAULT_SIZE = 0;
    private static final int SMALL_SIZE = 0;
    final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    private FloatingMenuLayerDrawable mFloatingMenuPreviewDrawable;
    ImageView mPreview;

    public AccessibilityButtonPreviewPreferenceController(Context context, String str) {
        super(context, str);
        this.mContentResolver = context.getContentResolver();
        this.mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.accessibility.AccessibilityButtonPreviewPreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                AccessibilityButtonPreviewPreferenceController.this.updatePreviewPreference();
            }
        };
    }

    private Drawable getFloatingMenuPreviewDrawable(int i, int i2) {
        FloatingMenuLayerDrawable floatingMenuLayerDrawable = this.mFloatingMenuPreviewDrawable;
        if (floatingMenuLayerDrawable == null) {
            this.mFloatingMenuPreviewDrawable = FloatingMenuLayerDrawable.createLayerDrawable(this.mContext, i, i2);
        } else {
            floatingMenuLayerDrawable.updateLayerDrawable(this.mContext, i, i2);
        }
        return this.mFloatingMenuPreviewDrawable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreviewPreference() {
        if (!AccessibilityUtil.isFloatingMenuEnabled(this.mContext)) {
            this.mPreview.setImageDrawable(this.mContext.getDrawable(R.drawable.accessibility_button_navigation_miui));
            return;
        }
        int i = Settings.Secure.getInt(this.mContentResolver, "accessibility_floating_menu_size", 0);
        this.mPreview.setImageDrawable(getFloatingMenuPreviewDrawable(i == 0 ? R.drawable.accessibility_button_preview_small_floating_menu : R.drawable.accessibility_button_preview_large_floating_menu, (int) (Settings.Secure.getFloat(this.mContentResolver, "accessibility_floating_menu_opacity", DEFAULT_OPACITY) * 100.0f)));
        this.mPreview.invalidate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreview = (ImageView) ((LayoutPreference) preferenceScreen.findPreference(getPreferenceKey())).findViewById(R.id.preview_image);
        updatePreviewPreference();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_button_mode"), false, this.mContentObserver);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_floating_menu_size"), false, this.mContentObserver);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_floating_menu_opacity"), false, this.mContentObserver);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
