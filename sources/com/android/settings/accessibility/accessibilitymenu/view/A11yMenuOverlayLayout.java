package com.android.settings.accessibility.accessibilitymenu.view;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.android.settings.R;
import com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService;
import com.android.settings.accessibility.accessibilitymenu.model.A11yMenuShortcut;
import com.android.settings.search.SearchUpdater;
import java.util.ArrayList;
import java.util.List;
import miui.content.res.ThemeResources;
import miui.os.Build;

/* loaded from: classes.dex */
public final class A11yMenuOverlayLayout {
    private static final int[] SHORTCUT_LIST_DEFAULT = {0, 1, 2, 8, 7, 6, 3, 4, 5};
    public A11yMenuViewPager a11yMenuViewPager;
    private WindowManager.LayoutParams layoutParams;
    public boolean mIsAdd;
    public FrameLayout mainFrameLayout;
    private final AccessibilityMenuService service;
    public WindowManager windowManager;

    public A11yMenuOverlayLayout(AccessibilityMenuService accessibilityMenuService) {
        this.service = accessibilityMenuService;
        this.windowManager = (WindowManager) accessibilityMenuService.getSystemService("window");
        this.mainFrameLayout = new FrameLayout(accessibilityMenuService);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.layoutParams = layoutParams;
        layoutParams.type = 2032;
        layoutParams.format = -3;
        int i = layoutParams.flags | 32;
        layoutParams.flags = i;
        layoutParams.flags = i | 262144;
        layoutParams.setTitle(accessibilityMenuService.getString(R.string.accessibility_menu_service_name));
        updateLayoutPosition();
        inflateLayoutAndSetOnTouchListener(this.mainFrameLayout);
        A11yMenuViewPager a11yMenuViewPager = new A11yMenuViewPager(accessibilityMenuService);
        this.a11yMenuViewPager = a11yMenuViewPager;
        a11yMenuViewPager.configureViewPagerAndFooter(this.mainFrameLayout, createShortcutList());
    }

    private List<A11yMenuShortcut> createShortcutList() {
        ArrayList arrayList = new ArrayList();
        for (int i : SHORTCUT_LIST_DEFAULT) {
            arrayList.add(new A11yMenuShortcut(i));
        }
        return arrayList;
    }

    public static int getIntegerFromSpecificPackage(Context context, String str, String str2) {
        int identifier;
        try {
            Resources resources = context.createPackageContext(str, 0).getResources();
            if (resources == null || (identifier = resources.getIdentifier(str2, "integer", str)) == 0) {
                return -1;
            }
            return resources.getInteger(identifier);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void inflateLayoutAndSetOnTouchListener(ViewGroup viewGroup) {
        LayoutInflater.from(this.service).inflate(R.layout.paged_menu, viewGroup);
        viewGroup.setOnTouchListener(this.service);
    }

    private void updateLayoutPosition() {
        int rotation = ((WindowManager) this.service.getSystemService("window")).getDefaultDisplay().getRotation();
        if (rotation == 1) {
            WindowManager.LayoutParams layoutParams = this.layoutParams;
            layoutParams.gravity = 8388693;
            layoutParams.width = -2;
            layoutParams.height = -1;
            int i = layoutParams.flags | 256;
            layoutParams.flags = i;
            layoutParams.flags = i | SearchUpdater.GOOGLE;
        } else if (rotation != 3) {
            WindowManager.LayoutParams layoutParams2 = this.layoutParams;
            layoutParams2.gravity = 80;
            layoutParams2.width = -1;
            layoutParams2.height = -2;
        } else {
            WindowManager.LayoutParams layoutParams3 = this.layoutParams;
            layoutParams3.gravity = 8388691;
            layoutParams3.width = -2;
            layoutParams3.height = -1;
            int i2 = layoutParams3.flags | 256;
            layoutParams3.flags = i2;
            layoutParams3.flags = i2 | SearchUpdater.GOOGLE;
        }
    }

    public void addMainFrame(boolean z) {
        if (!this.mainFrameLayout.isAttachedToWindow()) {
            try {
                this.windowManager.addView(this.mainFrameLayout, this.layoutParams);
            } catch (Exception e) {
                Log.e("A11yMenuOverlayLayout", "addMainFrame: ", e);
            }
            this.mIsAdd = true;
        }
        this.mainFrameLayout.setVisibility(z ? 0 : 8);
    }

    public void disableNotificationIfNeeded(Context context) {
        boolean z = Settings.System.getInt(context.getContentResolver(), "power_supersave_mode_open", -1) != 1;
        if (!Build.IS_INTERNATIONAL_BUILD) {
            this.a11yMenuViewPager.disableMenu(0, z);
        }
        this.a11yMenuViewPager.disableMenu(4, z);
        if (Settings.System.getInt(context.getContentResolver(), "use_control_panel", getIntegerFromSpecificPackage(context, ThemeResources.SYSTEMUI_NAME, "use_control_panel_setting_default")) == 1) {
            this.a11yMenuViewPager.disableMenu(6, z);
            this.a11yMenuViewPager.disableMenu(7, z);
        }
    }

    public final boolean hideMenu() {
        if (this.mainFrameLayout.getVisibility() == 0) {
            this.mainFrameLayout.setVisibility(8);
            AccessibilityMenuService accessibilityMenuService = this.service;
            if (accessibilityMenuService != null) {
                accessibilityMenuService.setVisibleFlag(false);
                return true;
            }
            return true;
        }
        return false;
    }

    public final void updateMenuLayout() {
        int visibility = this.mainFrameLayout.getVisibility();
        this.windowManager.removeView(this.mainFrameLayout);
        this.mainFrameLayout = new FrameLayout(this.service);
        updateLayoutPosition();
        inflateLayoutAndSetOnTouchListener(this.mainFrameLayout);
        this.a11yMenuViewPager.configureViewPagerAndFooter(this.mainFrameLayout, createShortcutList());
        if (!this.mainFrameLayout.isAttachedToWindow()) {
            try {
                this.windowManager.addView(this.mainFrameLayout, this.layoutParams);
            } catch (Exception e) {
                Log.e("A11yMenuOverlayLayout", "addMainFrame: ", e);
            }
        }
        this.mainFrameLayout.setVisibility(visibility);
        AccessibilityMenuService accessibilityMenuService = this.service;
        if (accessibilityMenuService != null) {
            accessibilityMenuService.setVisibleFlag(visibility == 0);
        }
    }
}
