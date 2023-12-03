package com.android.settings.accessibility.accessibilitymenu;

import android.accessibilityservice.AccessibilityButtonController;
import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import com.android.settings.accessibility.accessibilitymenu.ScreenMonitor;
import com.android.settings.accessibility.accessibilitymenu.view.A11yMenuOverlayLayout;
import com.android.settings.search.SearchUpdater;

/* loaded from: classes.dex */
public class AccessibilityMenuService extends AccessibilityService implements View.OnTouchListener, ScreenMonitor.ScreenStateChangeListener {
    public static boolean isReceiveA11yMenuInitBrodcast;
    public A11yMenuOverlayLayout a11yMenuLayout;
    private AccessibilityButtonController.AccessibilityButtonCallback accessibilityButtonCallback;
    private AccessibilityButtonController accessibilityButtonController;
    public AudioManager audioManager;
    private boolean isVisibleFlag;
    private int lastOrientation;
    public long lastTimeTouchedOutside = 0;
    private ScreenMonitor screenMonitor;

    @Override // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = configuration.orientation;
        Log.d("AccessibilityMenuService", "onConfigurationChanged: tempOrientation --> " + i);
        if (i != this.lastOrientation) {
            A11yMenuOverlayLayout a11yMenuOverlayLayout = this.a11yMenuLayout;
            if (a11yMenuOverlayLayout == null) {
                return;
            }
            a11yMenuOverlayLayout.updateMenuLayout();
            this.lastOrientation = i;
            return;
        }
        A11yMenuOverlayLayout a11yMenuOverlayLayout2 = this.a11yMenuLayout;
        if (a11yMenuOverlayLayout2 != null) {
            try {
                a11yMenuOverlayLayout2.windowManager.removeView(a11yMenuOverlayLayout2.mainFrameLayout);
            } catch (Exception unused) {
            }
        }
        A11yMenuOverlayLayout a11yMenuOverlayLayout3 = new A11yMenuOverlayLayout(this);
        this.a11yMenuLayout = a11yMenuOverlayLayout3;
        a11yMenuOverlayLayout3.addMainFrame(this.isVisibleFlag);
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
    }

    @Override // android.accessibilityservice.AccessibilityService
    protected boolean onKeyEvent(KeyEvent keyEvent) {
        if (this.a11yMenuLayout == null || keyEvent.getKeyCode() != 4) {
            return false;
        }
        this.a11yMenuLayout.hideMenu();
        return false;
    }

    @Override // android.accessibilityservice.AccessibilityService
    protected void onServiceConnected() {
        this.lastOrientation = ((WindowManager) getSystemService("window")).getDefaultDisplay().getRotation();
        if (Build.VERSION.SDK_INT >= 26) {
            A11yMenuOverlayLayout a11yMenuOverlayLayout = new A11yMenuOverlayLayout(this);
            this.a11yMenuLayout = a11yMenuOverlayLayout;
            a11yMenuOverlayLayout.addMainFrame(this.isVisibleFlag);
            this.accessibilityButtonController = getAccessibilityButtonController();
            AccessibilityButtonController.AccessibilityButtonCallback accessibilityButtonCallback = new AccessibilityButtonController.AccessibilityButtonCallback() { // from class: com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService.1
                @Override // android.accessibilityservice.AccessibilityButtonController.AccessibilityButtonCallback
                public void onClicked(AccessibilityButtonController accessibilityButtonController) {
                    if (((KeyguardManager) AccessibilityMenuService.this.getSystemService("keyguard")).isDeviceLocked()) {
                        return;
                    }
                    long uptimeMillis = SystemClock.uptimeMillis();
                    AccessibilityMenuService accessibilityMenuService = AccessibilityMenuService.this;
                    if (uptimeMillis - accessibilityMenuService.lastTimeTouchedOutside > 200) {
                        int i = accessibilityMenuService.a11yMenuLayout.mainFrameLayout.getVisibility() == 0 ? 8 : 0;
                        AccessibilityMenuService.this.a11yMenuLayout.mainFrameLayout.setVisibility(i);
                        AccessibilityMenuService accessibilityMenuService2 = AccessibilityMenuService.this;
                        accessibilityMenuService2.a11yMenuLayout.disableNotificationIfNeeded(accessibilityMenuService2);
                        AccessibilityMenuService.this.isVisibleFlag = i == 0;
                    }
                }
            };
            this.accessibilityButtonCallback = accessibilityButtonCallback;
            this.accessibilityButtonController.registerAccessibilityButtonCallback(accessibilityButtonCallback);
            ScreenMonitor screenMonitor = new ScreenMonitor(this);
            this.screenMonitor = screenMonitor;
            registerReceiver(screenMonitor, ScreenMonitor.STATE_CHANGE_FILTER);
            if (isReceiveA11yMenuInitBrodcast) {
                isReceiveA11yMenuInitBrodcast = false;
                getMainThreadHandler().postDelayed(new Runnable() { // from class: com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        AccessibilityMenuService.this.setMenuLayoutVisible();
                    }
                }, 100L);
            }
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 4 && this.a11yMenuLayout.hideMenu()) {
            this.lastTimeTouchedOutside = SystemClock.uptimeMillis();
            return false;
        }
        return false;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        ScreenMonitor screenMonitor = this.screenMonitor;
        if (screenMonitor != null) {
            unregisterReceiver(screenMonitor);
        }
        AccessibilityButtonController accessibilityButtonController = this.accessibilityButtonController;
        if (accessibilityButtonController != null) {
            accessibilityButtonController.unregisterAccessibilityButtonCallback(this.accessibilityButtonCallback);
        }
        A11yMenuOverlayLayout a11yMenuOverlayLayout = this.a11yMenuLayout;
        if (a11yMenuOverlayLayout != null) {
            try {
                a11yMenuOverlayLayout.windowManager.removeView(a11yMenuOverlayLayout.mainFrameLayout);
            } catch (Exception e) {
                Log.e("AccessibilityMenuService", "onUnbind --> removeView: ", e);
            }
        }
        isReceiveA11yMenuInitBrodcast = false;
        this.isVisibleFlag = false;
        return super.onUnbind(intent);
    }

    public void screenShot() {
        performGlobalAction(9);
    }

    @Override // com.android.settings.accessibility.accessibilitymenu.ScreenMonitor.ScreenStateChangeListener
    public final void screenTurnedOff() {
        A11yMenuOverlayLayout a11yMenuOverlayLayout = this.a11yMenuLayout;
        if (a11yMenuOverlayLayout == null) {
            return;
        }
        a11yMenuOverlayLayout.hideMenu();
    }

    public void setMenuLayoutVisible() {
        FrameLayout frameLayout;
        A11yMenuOverlayLayout a11yMenuOverlayLayout = this.a11yMenuLayout;
        if (a11yMenuOverlayLayout == null || (frameLayout = a11yMenuOverlayLayout.mainFrameLayout) == null) {
            return;
        }
        frameLayout.setVisibility(0);
        this.isVisibleFlag = true;
    }

    public void setVisibleFlag(boolean z) {
        this.isVisibleFlag = z;
    }

    public final void startActivityIfIntentIsSafe(Intent intent, int i) {
        if (getPackageManager().queryIntentActivities(intent, SearchUpdater.GOOGLE).isEmpty()) {
            return;
        }
        intent.setFlags(i);
        startActivity(intent);
    }
}
