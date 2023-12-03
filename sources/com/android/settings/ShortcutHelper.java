package com.android.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/* compiled from: SettingsApplication.java */
/* loaded from: classes.dex */
class ShortcutHelper {
    private static ShortcutHelper INST;
    private Context mContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: SettingsApplication.java */
    /* renamed from: com.android.settings.ShortcutHelper$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$ShortcutHelper$Shortcut;

        static {
            int[] iArr = new int[Shortcut.values().length];
            $SwitchMap$com$android$settings$ShortcutHelper$Shortcut = iArr;
            try {
                iArr[Shortcut.OPTIMIZE_CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$ShortcutHelper$Shortcut[Shortcut.NETWORK_ASSISTANT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$ShortcutHelper$Shortcut[Shortcut.ANTISPAM.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$settings$ShortcutHelper$Shortcut[Shortcut.POWER_CENTER.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$settings$ShortcutHelper$Shortcut[Shortcut.VIRUS_CENTER.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$settings$ShortcutHelper$Shortcut[Shortcut.PERM_CENTER.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    /* compiled from: SettingsApplication.java */
    /* loaded from: classes.dex */
    public enum Shortcut {
        OPTIMIZE_CENTER,
        POWER_CENTER,
        VIRUS_CENTER,
        PERM_CENTER,
        NETWORK_ASSISTANT,
        ANTISPAM
    }

    private ShortcutHelper(Context context) {
        this.mContext = context;
    }

    private Intent createPendingIntent(Shortcut shortcut) {
        Intent intent = new Intent();
        intent.setFlags(270532608);
        switch (AnonymousClass1.$SwitchMap$com$android$settings$ShortcutHelper$Shortcut[shortcut.ordinal()]) {
            case 1:
                intent.setAction("miui.intent.action.GARBAGE_CLEANUP");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setComponent(new ComponentName("com.android.settings", "com.miui.optimizecenter.MainActivity"));
                break;
            case 2:
                intent.setAction("android.intent.action.VIEW_DATA_USAGE_SUMMARY");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setComponent(new ComponentName("com.miui.networkassistant", "com.miui.networkassistant.ui.MainActivity"));
                break;
            case 3:
                intent.setAction("android.intent.action.SET_FIREWALL");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setComponent(new ComponentName("com.miui.antispam", "com.miui.antispam.ui.activity.MainActivity"));
                break;
            case 4:
                intent.setAction("com.miui.powercenter.PowerCenter");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setComponent(new ComponentName("com.android.settings", "com.miui.powercenter.PowerCenter"));
                break;
            case 5:
                intent.setAction("miui.intent.action.VIRUS_SCAN");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setComponent(new ComponentName("com.android.settings", "com.miui.viruscenter.activity.VirusScanAppActivity"));
                break;
            case 6:
                intent.setAction("miui.intent.action.PERM_CENTER");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setComponent(new ComponentName("com.android.settings", "com.miui.securitycenter.permission.PermMainActivity"));
                break;
        }
        return intent;
    }

    public static ShortcutHelper getInstance(Context context) {
        if (INST == null) {
            INST = new ShortcutHelper(context.getApplicationContext());
        }
        return INST;
    }

    public Intent createShortcutIntent(Shortcut shortcut, String str) {
        int i;
        String str2;
        switch (AnonymousClass1.$SwitchMap$com$android$settings$ShortcutHelper$Shortcut[shortcut.ordinal()]) {
            case 1:
                i = R.drawable.ic_launcher_rubbish_clean;
                str2 = "com.android.settings:string/cleaner";
                break;
            case 2:
                i = R.drawable.ic_launcher_network_assistant;
                str2 = "com.android.settings:string/network_assistant";
                break;
            case 3:
                i = R.drawable.ic_launcher_anti_spam;
                str2 = "com.android.settings:string/anti_spam";
                break;
            case 4:
                i = R.drawable.ic_launcher_power_optimize;
                str2 = "com.android.settings:string/power_mgr";
                break;
            case 5:
                i = R.drawable.ic_launcher_virus_scan;
                str2 = "com.android.settings:string/virus_scan";
                break;
            case 6:
                i = R.drawable.ic_launcher_license_manage;
                str2 = "com.android.settings:string/permission_mgr";
                break;
            default:
                i = -1;
                str2 = null;
                break;
        }
        Intent createPendingIntent = createPendingIntent(shortcut);
        if (createPendingIntent == null) {
            return null;
        }
        Intent intent = new Intent(str);
        intent.putExtra("duplicate", false);
        intent.putExtra("android.intent.extra.shortcut.NAME", str2);
        intent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(this.mContext, i));
        intent.putExtra("android.intent.extra.shortcut.INTENT", createPendingIntent);
        return intent;
    }

    public void removeShortcut(Shortcut shortcut) {
        this.mContext.sendBroadcast(createShortcutIntent(shortcut, "com.miui.home.launcher.action.UNINSTALL_SHORTCUT"));
    }
}
