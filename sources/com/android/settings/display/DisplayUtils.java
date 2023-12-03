package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.provider.MiuiSettings;
import android.provider.Settings;
import com.android.settings.JobDispatcher;
import java.util.HashSet;
import miui.accounts.ExtraAccountManager;
import miui.cloud.sync.providers.CalendarSyncInfoProvider;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;

/* loaded from: classes.dex */
public class DisplayUtils {
    protected static final HashSet<String> WHITE_LIST;

    static {
        HashSet<String> hashSet = new HashSet<>();
        WHITE_LIST = hashSet;
        hashSet.add("com.android.soundrecorder");
        hashSet.add(ContactsSyncInfoProvider.AUTHORITY);
        hashSet.add("com.android.browser");
        hashSet.add("com.mi.globalbrowser");
        hashSet.add("com.android.stk");
        hashSet.add("com.android.mms");
        hashSet.add("com.android.thememanager");
        hashSet.add("com.android.deskclock");
        hashSet.add("com.android.gallery3d");
        hashSet.add("com.android.updater");
        hashSet.add("com.mi.android.globalFileexplorer");
        hashSet.add("com.android.fileexplorer");
        hashSet.add(CalendarSyncInfoProvider.AUTHORITY);
        hashSet.add("com.android.vending");
        hashSet.add("com.android.apps.tag");
        hashSet.add("com.android.email");
        hashSet.add("com.miui.networkassistant");
        hashSet.add("com.android.providers.downloads.ui");
        hashSet.add("com.google.android.talk");
        hashSet.add("com.google.android.gm");
        hashSet.add("com.android.camera");
        hashSet.add("com.miui.camera");
        hashSet.add("com.miui.gallery");
        hashSet.add("com.miui.player");
        hashSet.add("com.miui.backup");
        hashSet.add("com.miui.notes");
        hashSet.add("com.xiaomi.market");
        hashSet.add("com.miui.antispam");
        hashSet.add("com.miui.video");
        hashSet.add("net.cactii.flash2");
        hashSet.add("com.xiaomi.gamecenter");
        hashSet.add("com.google.android.music");
        hashSet.add("com.google.android.youtube");
        hashSet.add("com.google.android.apps.plus");
        hashSet.add("com.facebook.orca");
        hashSet.add("com.android.chrome");
        hashSet.add(ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
        hashSet.add("com.xiaomi.payment");
        hashSet.add("com.mipay.wallet");
        hashSet.add("com.xiaomi.jr");
        hashSet.add("com.miui.mipub");
        hashSet.add("com.miui.weather2");
        hashSet.add("com.android.settings");
        hashSet.add("com.htc.album");
    }

    public static void setScreenPaperModeGetLocation(Context context) {
        boolean z = Settings.System.getInt(context.getContentResolver(), "paper_mode_scheduler_type", 2) == 1;
        if (MiuiSettings.ScreenEffect.isScreenPaperModeSupported && z) {
            JobDispatcher.scheduleJob(context, 44009);
            context.startService(new Intent(context, PaperModeSunTimeService.class));
        }
    }
}
