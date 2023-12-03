package miuix.appcompat.app.floatingactivity.helper;

import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.SplitUtils;
import miuix.internal.util.DeviceHelper;

/* loaded from: classes5.dex */
public class FloatingHelperFactory {
    public static BaseFloatingActivityHelper get(AppCompatActivity appCompatActivity) {
        boolean isIntentFromSettingsSplit = SplitUtils.isIntentFromSettingsSplit(appCompatActivity.getIntent());
        return (isIntentFromSettingsSplit || !DeviceHelper.isFoldDevice()) ? (isIntentFromSettingsSplit || !DeviceHelper.isTablet(appCompatActivity)) ? new PhoneFloatingActivityHelper(appCompatActivity) : new PadFloatingActivityHelper(appCompatActivity) : new FoldFloatingActivityHelper(appCompatActivity);
    }
}
