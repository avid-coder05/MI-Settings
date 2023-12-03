package com.android.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.preference.PreferenceFrameLayout;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.TtsSpan;
import android.util.ArraySet;
import android.util.FeatureFlagUtils;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.internal.R;
import com.android.internal.app.UnlaunchableAppActivity;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.dashboard.profileselector.ProfileFragmentBridge;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import com.android.settingslib.widget.ActionBarShadowController;
import com.android.settingslib.widget.AdaptiveIcon;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import miui.telephony.MiuiHeDuoHaoUtil;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public final class Utils extends com.android.settingslib.Utils {
    private static final StringBuilder sBuilder;
    private static final Formatter sFormatter;

    static {
        StringBuilder sb = new StringBuilder(50);
        sBuilder = sb;
        sFormatter = new Formatter(sb, Locale.getDefault());
    }

    public static boolean carrierTableFieldValidate(String str) {
        if (str == null) {
            return false;
        }
        if ("authtype".equalsIgnoreCase(str) || MiuiHeDuoHaoUtil.SUB_ID.equalsIgnoreCase(str)) {
            return true;
        }
        String upperCase = str.toUpperCase();
        try {
            Telephony.Carriers.class.getDeclaredField(upperCase);
            return true;
        } catch (NoSuchFieldException unused) {
            Log.w("Settings", upperCase + "is not a valid field in class Telephony.Carriers");
            return false;
        }
    }

    private static boolean confirmWorkProfileCredentials(Context context, int i) {
        Intent createConfirmDeviceCredentialIntent = ((KeyguardManager) context.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(null, null, i);
        if (createConfirmDeviceCredentialIntent != null) {
            context.startActivity(createConfirmDeviceCredentialIntent);
            return true;
        }
        return false;
    }

    public static SpannableString createAccessibleSequence(CharSequence charSequence, String str) {
        SpannableString spannableString = new SpannableString(charSequence);
        spannableString.setSpan(new TtsSpan.TextBuilder(str).build(), 0, charSequence.length(), 18);
        return spannableString;
    }

    public static Bitmap createBitmap(Drawable drawable, int i, int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return createBitmap;
    }

    public static IconCompat createIconWithDrawable(Drawable drawable) {
        Bitmap createBitmap;
        if (drawable instanceof BitmapDrawable) {
            createBitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicWidth <= 0) {
                intrinsicWidth = 1;
            }
            if (intrinsicHeight <= 0) {
                intrinsicHeight = 1;
            }
            createBitmap = createBitmap(drawable, intrinsicWidth, intrinsicHeight);
        }
        return IconCompat.createWithBitmap(createBitmap);
    }

    public static Locale createLocaleFromString(String str) {
        if (str == null) {
            return Locale.getDefault();
        }
        String[] split = str.split("_", 3);
        return 1 == split.length ? new Locale(split[0]) : 2 == split.length ? new Locale(split[0], split[1]) : new Locale(split[0], split[1], split[2]);
    }

    public static Context createPackageContextAsUser(Context context, int i) {
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.of(i));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Settings", "Failed to create user context", e);
            return null;
        }
    }

    public static int enforceSameOwner(Context context, int i) {
        if (ArrayUtils.contains(((UserManager) context.getSystemService(UserManager.class)).getProfileIdsWithDisabled(UserHandle.myUserId()), i)) {
            return i;
        }
        throw new SecurityException("Given user id " + i + " does not belong to user " + UserHandle.myUserId());
    }

    public static int enforceSystemUser(Context context, int i) {
        if (UserHandle.myUserId() == 0) {
            return i;
        }
        throw new SecurityException("Given user id " + i + " must only be used from USER_SYSTEM, but current user is " + UserHandle.myUserId());
    }

    public static void forceCustomPadding(View view, boolean z) {
        view.setPaddingRelative(z ? view.getPaddingStart() : 0, 0, z ? view.getPaddingEnd() : 0, view.getResources().getDimensionPixelSize(17105475));
    }

    public static String formatDateRange(Context context, long j, long j2) {
        String formatter;
        StringBuilder sb = sBuilder;
        synchronized (sb) {
            sb.setLength(0);
            formatter = DateUtils.formatDateRange(context, sFormatter, j, j2, 65552, null).toString();
        }
        return formatter;
    }

    public static Drawable getAdaptiveIcon(Context context, Drawable drawable, int i) {
        Drawable safeIcon = getSafeIcon(drawable);
        if (safeIcon instanceof AdaptiveIconDrawable) {
            return safeIcon;
        }
        AdaptiveIcon adaptiveIcon = new AdaptiveIcon(context, safeIcon);
        adaptiveIcon.setBackgroundColor(i);
        return adaptiveIcon;
    }

    public static ApplicationInfo getAdminApplicationInfo(Context context, int i) {
        ComponentName profileOwnerAsUser = ((DevicePolicyManager) context.getSystemService("device_policy")).getProfileOwnerAsUser(i);
        if (profileOwnerAsUser == null) {
            return null;
        }
        String packageName = profileOwnerAsUser.getPackageName();
        try {
            return AppGlobals.getPackageManager().getApplicationInfo(packageName, 0, i);
        } catch (RemoteException e) {
            Log.e("Settings", "Error while retrieving application info for package " + packageName + ", userId " + i, e);
            return null;
        }
    }

    public static CharSequence getApplicationLabel(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 4194816).loadLabel(context.getPackageManager());
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("Settings", "Unable to find info for package: " + str);
            return null;
        }
    }

    public static Drawable getBadgedIcon(IconDrawableFactory iconDrawableFactory, PackageManager packageManager, String str, int i) {
        try {
            return iconDrawableFactory.getBadgedIcon(packageManager.getApplicationInfoAsUser(str, 128, i), i);
        } catch (PackageManager.NameNotFoundException unused) {
            return packageManager.getDefaultActivityIcon();
        }
    }

    public static String getBatteryPercentage(Intent intent) {
        return com.android.settingslib.Utils.formatPercentage(com.android.settingslib.Utils.getBatteryLevel(intent));
    }

    public static int getCredentialOwnerUserId(Context context) {
        return getCredentialOwnerUserId(context, UserHandle.myUserId());
    }

    public static int getCredentialOwnerUserId(Context context, int i) {
        return ((UserManager) context.getSystemService(UserManager.class)).getCredentialOwnerProfile(i);
    }

    public static int getCredentialType(Context context, int i) {
        return new LockPatternUtils(context).getCredentialTypeForUser(i);
    }

    public static int getCurrentUserId(UserManager userManager, boolean z) throws IllegalStateException {
        if (z) {
            UserHandle managedProfile = getManagedProfile(userManager);
            if (managedProfile != null) {
                return managedProfile.getIdentifier();
            }
            throw new IllegalStateException("Work profile user ID is not available.");
        }
        return UserHandle.myUserId();
    }

    public static ComponentName getDeviceOwnerComponent(Context context) {
        return ((DevicePolicyManager) context.getSystemService("device_policy")).getDeviceOwnerComponentOnAnyUser();
    }

    public static UserInfo getExistingUser(UserManager userManager, UserHandle userHandle) {
        List<UserInfo> aliveUsers = userManager.getAliveUsers();
        int identifier = userHandle.getIdentifier();
        for (UserInfo userInfo : aliveUsers) {
            if (userInfo.id == identifier) {
                return userInfo;
            }
        }
        return null;
    }

    public static FaceManager getFaceManagerOrNull(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face")) {
            return (FaceManager) context.getSystemService("face");
        }
        return null;
    }

    public static FingerprintManager getFingerprintManagerOrNull(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.fingerprint")) {
            return (FingerprintManager) context.getSystemService("fingerprint");
        }
        return null;
    }

    public static ArraySet<String> getHandledDomains(PackageManager packageManager, String str) {
        List intentFilterVerifications = packageManager.getIntentFilterVerifications(str);
        List<IntentFilter> allIntentFilters = packageManager.getAllIntentFilters(str);
        ArraySet<String> arraySet = new ArraySet<>();
        if (intentFilterVerifications != null && intentFilterVerifications.size() > 0) {
            Iterator it = intentFilterVerifications.iterator();
            while (it.hasNext()) {
                Iterator it2 = ((IntentFilterVerificationInfo) it.next()).getDomains().iterator();
                while (it2.hasNext()) {
                    arraySet.add((String) it2.next());
                }
            }
        }
        if (allIntentFilters != null && allIntentFilters.size() > 0) {
            for (IntentFilter intentFilter : allIntentFilters) {
                if (intentFilter.hasCategory("android.intent.category.BROWSABLE") && (intentFilter.hasDataScheme("http") || intentFilter.hasDataScheme("https"))) {
                    arraySet.addAll(intentFilter.getHostsList());
                }
            }
        }
        return arraySet;
    }

    public static int getHomepageIconColor(Context context) {
        return com.android.settingslib.Utils.getColorAttrDefaultColor(context, 16842808);
    }

    private static String getLocalProfileGivenName(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor query = contentResolver.query(ContactsContract.Profile.CONTENT_RAW_CONTACTS_URI, new String[]{"_id"}, "account_type IS NULL AND account_name IS NULL", null, null);
        if (query == null) {
            return null;
        }
        try {
            if (query.moveToFirst()) {
                long j = query.getLong(0);
                query.close();
                query = contentResolver.query(ContactsContract.Profile.CONTENT_URI.buildUpon().appendPath("data").build(), new String[]{"data2", "data3"}, "raw_contact_id=" + j, null, null);
                if (query == null) {
                    return null;
                }
                try {
                    if (query.moveToFirst()) {
                        String string = query.getString(0);
                        if (TextUtils.isEmpty(string)) {
                            string = query.getString(1);
                        }
                        return string;
                    }
                    return null;
                } finally {
                }
            }
            return null;
        } finally {
        }
    }

    public static String getLocalizedName(Context context, String str) {
        int identifier;
        String str2 = null;
        if (context == null || str == null || str.isEmpty() || (identifier = context.getResources().getIdentifier(str, "string", context.getPackageName())) <= 0) {
            return null;
        }
        try {
            str2 = context.getResources().getString(identifier);
            Log.d("Settings", "Replaced apn name with localized name");
            return str2;
        } catch (Resources.NotFoundException e) {
            Log.e("Settings", "Got execption while getting the localized apn name.", e);
            return str2;
        }
    }

    public static UserHandle getManagedProfile(UserManager userManager) {
        for (UserHandle userHandle : userManager.getUserProfiles()) {
            if (userHandle.getIdentifier() != userManager.getUserHandle() && userManager.getUserInfo(userHandle.getIdentifier()).isManagedProfile()) {
                return userHandle;
            }
        }
        return null;
    }

    public static int getManagedProfileId(UserManager userManager, int i) {
        for (int i2 : userManager.getProfileIdsWithDisabled(i)) {
            if (i2 != i) {
                return i2;
            }
        }
        return -10000;
    }

    public static UserHandle getManagedProfileWithDisabled(UserManager userManager) {
        int myUserId = UserHandle.myUserId();
        List profiles = userManager.getProfiles(myUserId);
        int size = profiles.size();
        for (int i = 0; i < size; i++) {
            UserInfo userInfo = (UserInfo) profiles.get(i);
            if (userInfo.isManagedProfile() && userInfo.getUserHandle().getIdentifier() != myUserId) {
                return userInfo.getUserHandle();
            }
        }
        return null;
    }

    public static String getMeProfileName(Context context, boolean z) {
        return z ? getProfileDisplayName(context) : getShorterNameIfPossible(context);
    }

    private static final String getProfileDisplayName(Context context) {
        Cursor query = context.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, new String[]{"display_name"}, null, null, null);
        if (query == null) {
            return null;
        }
        try {
            if (query.moveToFirst()) {
                return query.getString(0);
            }
            return null;
        } finally {
            query.close();
        }
    }

    private static Drawable getSafeDrawable(Drawable drawable, int i, int i2) {
        int minimumWidth = drawable.getMinimumWidth();
        int minimumHeight = drawable.getMinimumHeight();
        if (minimumWidth > i || minimumHeight > i2) {
            float f = minimumWidth;
            float f2 = minimumHeight;
            float min = Math.min(i / f, i2 / f2);
            int i3 = (int) (f * min);
            int i4 = (int) (f2 * min);
            return new BitmapDrawable((Resources) null, drawable instanceof BitmapDrawable ? Bitmap.createScaledBitmap(((BitmapDrawable) drawable).getBitmap(), i3, i4, false) : createBitmap(drawable, i3, i4));
        }
        return drawable;
    }

    public static Drawable getSafeIcon(Drawable drawable) {
        return (drawable == null || (drawable instanceof VectorDrawable)) ? drawable : getSafeDrawable(drawable, 500, 500);
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x0068 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.os.UserHandle getSecureTargetUser(android.os.IBinder r4, android.os.UserManager r5, android.os.Bundle r6, android.os.Bundle r7) {
        /*
            android.os.UserHandle r0 = new android.os.UserHandle
            int r1 = android.os.UserHandle.myUserId()
            r0.<init>(r1)
            android.app.IActivityManager r1 = android.app.ActivityManager.getService()
            java.lang.String r2 = r1.getLaunchedFromPackage(r4)     // Catch: android.os.RemoteException -> L69
            java.lang.String r3 = "com.android.settings"
            boolean r3 = r3.equals(r2)     // Catch: android.os.RemoteException -> L69
            if (r3 != 0) goto L24
            java.lang.String r3 = "com.miui.securitycore"
            boolean r2 = r3.equals(r2)     // Catch: android.os.RemoteException -> L69
            if (r2 == 0) goto L22
            goto L24
        L22:
            r2 = 0
            goto L25
        L24:
            r2 = 1
        L25:
            android.os.UserHandle r3 = new android.os.UserHandle     // Catch: android.os.RemoteException -> L69
            int r4 = r1.getLaunchedFromUid(r4)     // Catch: android.os.RemoteException -> L69
            int r4 = android.os.UserHandle.getUserId(r4)     // Catch: android.os.RemoteException -> L69
            r3.<init>(r4)     // Catch: android.os.RemoteException -> L69
            boolean r4 = r3.equals(r0)     // Catch: android.os.RemoteException -> L69
            if (r4 != 0) goto L3f
            boolean r4 = isProfileOf(r5, r3)     // Catch: android.os.RemoteException -> L69
            if (r4 == 0) goto L3f
            return r3
        L3f:
            android.os.UserHandle r4 = getUserHandleFromBundle(r7)     // Catch: android.os.RemoteException -> L69
            if (r4 == 0) goto L54
            boolean r7 = r4.equals(r0)     // Catch: android.os.RemoteException -> L69
            if (r7 != 0) goto L54
            if (r2 == 0) goto L54
            boolean r7 = isProfileOf(r5, r4)     // Catch: android.os.RemoteException -> L69
            if (r7 == 0) goto L54
            return r4
        L54:
            android.os.UserHandle r4 = getUserHandleFromBundle(r6)     // Catch: android.os.RemoteException -> L69
            if (r4 == 0) goto L71
            boolean r6 = r4.equals(r0)     // Catch: android.os.RemoteException -> L69
            if (r6 != 0) goto L71
            if (r2 == 0) goto L71
            boolean r5 = isProfileOf(r5, r4)     // Catch: android.os.RemoteException -> L69
            if (r5 == 0) goto L71
            return r4
        L69:
            r4 = move-exception
            java.lang.String r5 = "Settings"
            java.lang.String r6 = "Could not talk to activity manager."
            android.util.Log.v(r5, r6, r4)
        L71:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.Utils.getSecureTargetUser(android.os.IBinder, android.os.UserManager, android.os.Bundle, android.os.Bundle):android.os.UserHandle");
    }

    private static String getShorterNameIfPossible(Context context) {
        String localProfileGivenName = getLocalProfileGivenName(context);
        return !TextUtils.isEmpty(localProfileGivenName) ? localProfileGivenName : getProfileDisplayName(context);
    }

    public static String getString(Context context, String str) {
        return Settings.Global.getString(context.getContentResolver(), str);
    }

    public static Fragment getTargetFragment(Activity activity, String str, Bundle bundle) {
        boolean z = false;
        boolean z2 = bundle != null && bundle.getInt(YellowPageContract.Profile.DIRECTORY) == 1;
        if (bundle != null && bundle.getInt(YellowPageContract.Profile.DIRECTORY) == 2) {
            z = true;
        }
        try {
            if (((UserManager) activity.getSystemService(UserManager.class)).getUserProfiles().size() > 1) {
                Map<String, String> map = ProfileFragmentBridge.FRAGMENT_MAP;
                if (map.get(str) != null && !z && !z2) {
                    return Fragment.instantiate(activity, map.get(str), bundle);
                }
            }
            return Fragment.instantiate(activity, str, bundle);
        } catch (Exception e) {
            Log.e("Settings", "Unable to get target fragment", e);
            return null;
        }
    }

    private static UserHandle getUserHandleFromBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        UserHandle userHandle = (UserHandle) bundle.getParcelable("android.intent.extra.USER");
        if (userHandle != null) {
            return userHandle;
        }
        int i = bundle.getInt("android.intent.extra.USER_ID", -1);
        if (i != -1) {
            return UserHandle.of(i);
        }
        return null;
    }

    public static int getUserIdFromBundle(Context context, Bundle bundle) {
        return getUserIdFromBundle(context, bundle, false);
    }

    public static int getUserIdFromBundle(Context context, Bundle bundle, boolean z) {
        if (bundle == null) {
            return getCredentialOwnerUserId(context);
        }
        boolean z2 = false;
        if (z && bundle.getBoolean("allow_any_user", false)) {
            z2 = true;
        }
        int i = bundle.getInt("android.intent.extra.USER_ID", UserHandle.myUserId());
        return i == -9999 ? z2 ? i : enforceSystemUser(context, i) : z2 ? i : enforceSameOwner(context, i);
    }

    public static boolean hasFaceHardware(Context context) {
        FaceManager faceManagerOrNull = getFaceManagerOrNull(context);
        return faceManagerOrNull != null && faceManagerOrNull.isHardwareDetected();
    }

    public static boolean hasFingerprintHardware(Context context) {
        FingerprintManager fingerprintManagerOrNull = getFingerprintManagerOrNull(context);
        return fingerprintManagerOrNull != null && fingerprintManagerOrNull.isHardwareDetected();
    }

    public static boolean hasMultipleUsers(Context context) {
        return ((UserManager) context.getSystemService(UserManager.class)).getUsers().size() > 1;
    }

    public static View inflateCategoryHeader(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        TypedArray obtainStyledAttributes = layoutInflater.getContext().obtainStyledAttributes(null, R.styleable.Preference, 16842892, 0);
        int resourceId = obtainStyledAttributes.getResourceId(3, 0);
        obtainStyledAttributes.recycle();
        return layoutInflater.inflate(resourceId, viewGroup, false);
    }

    public static boolean isBandwidthControlEnabled() {
        return false;
    }

    public static boolean isBatteryPresent(Context context) {
        return isBatteryPresent(context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")));
    }

    public static boolean isBatteryPresent(Intent intent) {
        return intent.getBooleanExtra("present", true);
    }

    public static boolean isDemoUser(Context context) {
        return UserManager.isDeviceInDemoMode(context) && ((UserManager) context.getSystemService(UserManager.class)).isDemoUser();
    }

    public static boolean isDeviceProvisioned(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
    }

    public static boolean isMonkeyRunning() {
        return ActivityManager.isUserAMonkey();
    }

    public static boolean isMultipleBiometricsSupported(Context context) {
        return hasFingerprintHardware(context) && hasFaceHardware(context);
    }

    public static boolean isNightMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & 48) == 32;
    }

    public static boolean isPackageDirectBootAware(Context context, String str) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
            if (!applicationInfo.isDirectBootAware()) {
                if (!applicationInfo.isPartiallyDirectBootAware()) {
                    return false;
                }
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static boolean isPackageEnabled(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0).enabled;
        } catch (Exception e) {
            Log.e("Settings", "Error while retrieving application info for package " + str, e);
            return false;
        }
    }

    public static boolean isProfileOf(UserInfo userInfo, UserInfo userInfo2) {
        int i;
        return userInfo.id == userInfo2.id || ((i = userInfo.profileGroupId) != -10000 && i == userInfo2.profileGroupId);
    }

    private static boolean isProfileOf(UserManager userManager, UserHandle userHandle) {
        if (userManager == null || userHandle == null) {
            return false;
        }
        return UserHandle.myUserId() == userHandle.getIdentifier() || userManager.getUserProfiles().contains(userHandle);
    }

    public static boolean isProfileOrDeviceOwner(DevicePolicyManager devicePolicyManager, String str, int i) {
        if (devicePolicyManager.getDeviceOwnerUserId() == i && devicePolicyManager.isDeviceOwnerApp(str)) {
            return true;
        }
        ComponentName profileOwnerAsUser = devicePolicyManager.getProfileOwnerAsUser(i);
        return profileOwnerAsUser != null && profileOwnerAsUser.getPackageName().equals(str);
    }

    public static boolean isProfileOrDeviceOwner(UserManager userManager, DevicePolicyManager devicePolicyManager, String str) {
        List users = userManager.getUsers();
        if (devicePolicyManager.isDeviceOwnerAppOnAnyUser(str)) {
            return true;
        }
        int size = users.size();
        for (int i = 0; i < size; i++) {
            ComponentName profileOwnerAsUser = devicePolicyManager.getProfileOwnerAsUser(((UserInfo) users.get(i)).id);
            if (profileOwnerAsUser != null && profileOwnerAsUser.getPackageName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProviderModelEnabled(Context context) {
        return FeatureFlagUtils.isEnabled(context, "settings_provider_model");
    }

    public static boolean isSettingsIntelligence(Context context) {
        return TextUtils.equals(context.getPackageManager().getPackagesForUid(Binder.getCallingUid())[0], context.getString(R.string.config_settingsintelligence_package_name));
    }

    public static boolean isSimSettingsApkAvailable() {
        return VendorUtils.isSimSettingsApkAvailable();
    }

    public static boolean isSupportCTPA(Context context) {
        return context.getApplicationContext().getResources().getBoolean(R.bool.config_support_CT_PA);
    }

    public static boolean isSystemAlertWindowEnabled(Context context) {
        return !((ActivityManager) context.getSystemService("activity")).isLowRamDevice() || Build.VERSION.SDK_INT < 29;
    }

    public static boolean isVoiceCapable(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        return telephonyManager != null && telephonyManager.isVoiceCapable();
    }

    private static boolean isVolumeValid(VolumeInfo volumeInfo) {
        return volumeInfo != null && volumeInfo.getType() == 1 && volumeInfo.isMountedReadable();
    }

    public static void launchIntent(Fragment fragment, Intent intent) {
        try {
            int intExtra = intent.getIntExtra("android.intent.extra.USER_ID", -1);
            if (intExtra == -1) {
                fragment.startActivity(intent);
            } else {
                fragment.getActivity().startActivityAsUser(intent, new UserHandle(intExtra));
            }
        } catch (ActivityNotFoundException unused) {
            Log.w("Settings", "No activity found for " + intent);
        }
    }

    public static VolumeInfo maybeInitializeVolume(StorageManager storageManager, Bundle bundle) {
        VolumeInfo findVolumeById = storageManager.findVolumeById(bundle.getString("android.os.storage.extra.VOLUME_ID", "private"));
        if (isVolumeValid(findVolumeById)) {
            return findVolumeById;
        }
        return null;
    }

    public static void prepareCustomPreferencesList(ViewGroup viewGroup, View view, View view2, boolean z) {
        if (view2.getScrollBarStyle() == 33554432) {
            int dimensionPixelSize = view2.getResources().getDimensionPixelSize(17105475);
            if (viewGroup instanceof PreferenceFrameLayout) {
                view.getLayoutParams().removeBorders = true;
            }
            view2.setPaddingRelative(0, 0, 0, dimensionPixelSize);
        }
    }

    public static void setActionBarShadowAnimation(Activity activity, Lifecycle lifecycle, View view) {
        if (activity == null) {
            Log.w("Settings", "No activity, cannot style actionbar.");
            return;
        }
        ActionBar actionBar = activity.getActionBar();
        if (actionBar == null) {
            Log.w("Settings", "No actionbar, cannot style actionbar.");
            return;
        }
        actionBar.setElevation(0.0f);
        if (lifecycle == null || view == null) {
            return;
        }
        ActionBarShadowController.attachToView(activity, lifecycle, view);
    }

    public static void setEditTextCursorPosition(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    public static boolean startQuietModeDialogIfNecessary(Context context, UserManager userManager, int i) {
        if (userManager.isQuietModeEnabled(UserHandle.of(i))) {
            context.startActivity(UnlaunchableAppActivity.createInQuietModeDialogIntent(i));
            return true;
        }
        return false;
    }

    public static boolean unlockWorkProfileIfNecessary(Context context, int i) {
        try {
            if (ActivityManager.getService().isUserRunning(i, 2) && new LockPatternUtils(context).isSecure(i)) {
                return confirmWorkProfileCredentials(context, i);
            }
            return false;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public static boolean updateHeaderToSpecificActivityFromMetaDataOrRemove(Context context, List<PreferenceActivity.Header> list, PreferenceActivity.Header header) {
        Intent intent = header.intent;
        if (intent != null) {
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 128);
            int size = queryIntentActivities.size();
            for (int i = 0; i < size; i++) {
                ResolveInfo resolveInfo = queryIntentActivities.get(i);
                if ((resolveInfo.activityInfo.applicationInfo.flags & 1) != 0) {
                    header.title = resolveInfo.loadLabel(packageManager).toString();
                    header.summary = "";
                    Intent intent2 = new Intent();
                    ActivityInfo activityInfo = resolveInfo.activityInfo;
                    header.intent = intent2.setClassName(activityInfo.packageName, activityInfo.name);
                    return true;
                }
            }
        }
        list.remove(header);
        return false;
    }

    public static boolean updatePreferenceToSpecificActivityOrRemove(Context context, PreferenceGroup preferenceGroup, String str, int i) {
        Preference findPreference = preferenceGroup.findPreference(str);
        if (findPreference == null) {
            return false;
        }
        Intent intent = findPreference.getIntent();
        if (intent != null) {
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
            int size = queryIntentActivities.size();
            for (int i2 = 0; i2 < size; i2++) {
                ResolveInfo resolveInfo = queryIntentActivities.get(i2);
                if ((resolveInfo.activityInfo.applicationInfo.flags & 1) != 0) {
                    Intent intent2 = new Intent();
                    ActivityInfo activityInfo = resolveInfo.activityInfo;
                    findPreference.setIntent(intent2.setClassName(activityInfo.packageName, activityInfo.name));
                    if ((i & 1) != 0) {
                        findPreference.setTitle(resolveInfo.loadLabel(packageManager));
                    }
                    return true;
                }
            }
        }
        preferenceGroup.removePreference(findPreference);
        return false;
    }
}
