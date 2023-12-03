package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sysprop.SetupWizardProperties;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.util.ThemeHelper;
import java.util.Arrays;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes.dex */
public class SetupWizardUtils {
    public static Bundle copyLifecycleExtra(Bundle bundle, Bundle bundle2) {
        for (String str : Arrays.asList("firstRun", "isSetupFlow")) {
            bundle2.putBoolean(str, bundle.getBoolean(str, false));
        }
        return bundle2;
    }

    public static void copySetupExtras(Intent intent, Intent intent2) {
        WizardManagerHelper.copyWizardManagerExtras(intent, intent2);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0032, code lost:
    
        if (r0.equals("glif_light") == false) goto L10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x007c, code lost:
    
        if (r0.equals("glif_light") == false) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x00cf, code lost:
    
        if (r0.equals("glif_light") == false) goto L84;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static int getTheme(android.content.Context r14, android.content.Intent r15) {
        /*
            Method dump skipped, instructions count: 396
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.SetupWizardUtils.getTheme(android.content.Context, android.content.Intent):int");
    }

    public static String getThemeString(Intent intent) {
        String stringExtra = intent.getStringExtra(ThemeManagerConstants.COMPONENT_CODE_MASK);
        return stringExtra == null ? (String) SetupWizardProperties.theme().orElse("") : stringExtra;
    }

    public static int getTransparentTheme(Context context, Intent intent) {
        int theme = getTheme(context, intent);
        return theme == R.style.GlifV3Theme_DayNight ? R.style.GlifV3Theme_DayNight_Transparent : theme == R.style.GlifV3Theme_Light ? R.style.GlifV3Theme_Light_Transparent : theme == R.style.GlifV2Theme_DayNight ? R.style.GlifV2Theme_DayNight_Transparent : theme == R.style.GlifV2Theme_Light ? R.style.GlifV2Theme_Light_Transparent : theme == R.style.GlifTheme_DayNight ? R.style.SetupWizardTheme_DayNight_Transparent : theme == R.style.GlifTheme_Light ? R.style.SetupWizardTheme_Light_Transparent : theme == R.style.GlifV3Theme ? R.style.GlifV3Theme_Transparent : theme == R.style.GlifV2Theme ? R.style.GlifV2Theme_Transparent : theme == R.style.GlifTheme ? R.style.SetupWizardTheme_Transparent : ThemeHelper.isSetupWizardDayNightEnabled(context) ? R.style.GlifV2Theme_DayNight_Transparent : R.style.GlifV2Theme_Light_Transparent;
    }
}
