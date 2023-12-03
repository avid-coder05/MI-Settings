package miuix.appcompat.app.floatingactivity;

import android.util.Log;
import miuix.appcompat.R$anim;
import miuix.appcompat.R$id;
import miuix.appcompat.app.AppCompatActivity;
import miuix.autodensity.IDensity;

/* loaded from: classes5.dex */
public class FloatingAnimHelper {
    private static boolean sAutoDpiImported = true;
    private static boolean sTransWithClipAnimSupported;

    static {
        try {
            Class.forName("android.view.animation.TranslateWithClipAnimation");
            sTransWithClipAnimSupported = true;
        } catch (ClassNotFoundException e) {
            Log.w("FloatingAnimHelper", "Failed to get isSupportTransWithClipAnim attributes", e);
        }
    }

    public static void clearFloatingWindowAnim(AppCompatActivity appCompatActivity) {
    }

    public static void execFloatingWindowEnterAnimRomNormal(AppCompatActivity appCompatActivity) {
        appCompatActivity.overridePendingTransition(R$anim.miuix_appcompat_floating_window_enter_anim_normal_rom_enter, R$anim.miuix_appcompat_floating_window_enter_anim_normal_rom_exit);
    }

    public static void execFloatingWindowExitAnimRomNormal(AppCompatActivity appCompatActivity) {
        appCompatActivity.overridePendingTransition(R$anim.miuix_appcompat_floating_window_exit_anim_normal_rom_enter, R$anim.miuix_appcompat_floating_window_exit_anim_normal_rom_exit);
    }

    public static boolean getAutoDensitySupportStatus(AppCompatActivity appCompatActivity) {
        boolean shouldAdaptAutoDensity;
        if (sAutoDpiImported) {
            try {
                if (appCompatActivity instanceof IDensity) {
                    shouldAdaptAutoDensity = ((IDensity) appCompatActivity).shouldAdaptAutoDensity();
                } else if (!(appCompatActivity.getApplication() instanceof IDensity)) {
                    return true;
                } else {
                    shouldAdaptAutoDensity = ((IDensity) appCompatActivity.getApplication()).shouldAdaptAutoDensity();
                }
                return shouldAdaptAutoDensity;
            } catch (Exception unused) {
                return false;
            }
        }
        return false;
    }

    public static boolean isSupportTransWithClipAnim() {
        return sTransWithClipAnimSupported;
    }

    public static void markedPageIndex(AppCompatActivity appCompatActivity, int i) {
        appCompatActivity.getWindow().getDecorView().setTag(R$id.miuix_appcompat_floating_window_index, Integer.valueOf(i));
    }

    public static int obtainPageIndex(AppCompatActivity appCompatActivity) {
        Object tag = appCompatActivity.getWindow().getDecorView().getTag(R$id.miuix_appcompat_floating_window_index);
        if (tag instanceof Integer) {
            return ((Integer) tag).intValue();
        }
        return -1;
    }

    public static void preformFloatingExitAnimWithClip(AppCompatActivity appCompatActivity, boolean z) {
        if (sTransWithClipAnimSupported) {
            if (!z) {
                appCompatActivity.overridePendingTransition(R$anim.miuix_appcompat_floating_window_anim_in_full_screen, R$anim.miuix_appcompat_floating_window_anim_out_full_screen);
            } else if (getAutoDensitySupportStatus(appCompatActivity)) {
                appCompatActivity.overridePendingTransition(R$anim.miuix_appcompat_floating_window_enter_anim_auto_dpi, R$anim.miuix_appcompat_floating_window_exit_anim_auto_dpi);
            } else {
                appCompatActivity.overridePendingTransition(R$anim.miuix_appcompat_floating_window_enter_anim, R$anim.miuix_appcompat_floating_window_exit_anim);
            }
        }
    }

    public static void singleAppFloatingActivityEnter(AppCompatActivity appCompatActivity) {
        if (sTransWithClipAnimSupported) {
            preformFloatingExitAnimWithClip(appCompatActivity, appCompatActivity.isInFloatingWindowMode());
        } else {
            appCompatActivity.executeOpenEnterAnimation();
        }
    }

    public static void singleAppFloatingActivityExit(AppCompatActivity appCompatActivity) {
        if (sTransWithClipAnimSupported) {
            if (!appCompatActivity.isInFloatingWindowMode()) {
                appCompatActivity.overridePendingTransition(R$anim.miuix_appcompat_floating_window_anim_in_full_screen, R$anim.miuix_appcompat_floating_window_anim_out_full_screen);
            } else if (getAutoDensitySupportStatus(appCompatActivity)) {
                appCompatActivity.overridePendingTransition(R$anim.miuix_appcompat_floating_window_enter_anim_auto_dpi, R$anim.miuix_appcompat_floating_window_exit_anim_auto_dpi);
            } else {
                appCompatActivity.overridePendingTransition(R$anim.miuix_appcompat_floating_window_enter_anim, R$anim.miuix_appcompat_floating_window_exit_anim);
            }
        }
    }
}
