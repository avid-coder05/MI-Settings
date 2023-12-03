package com.android.settings.faceunlock;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.face.BaseMiuiFaceManager;
import android.hardware.face.Face;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.Window;
import android.view.animation.PathInterpolator;
import com.android.settings.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.content.res.ThemeResources;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.utils.EaseManager;

/* loaded from: classes.dex */
public class KeyguardSettingsFaceUnlockUtils {
    public static final PathInterpolator SLOWDOWN_INTERPOLATOR = new PathInterpolator(0.5f, 1.0f, 0.5f, 1.0f);
    private static WeakReference<BaseMiuiFaceManager> mWeakReferenceFaceManager = null;
    public static final AnimConfig HIDE_ANIM_CONFING = new AnimConfig().setEase(new EaseManager.InterpolateEaseStyle(0, 0.99f, 0.2f));
    public static final AnimConfig TWO_ELEMENTS_SHOW_ANIM_CONFING = new AnimConfig().setDelay(60).setEase(new EaseManager.InterpolateEaseStyle(16, new float[0]).setDuration(300));
    public static final AnimConfig SHOW_ANIM_CONFING = new AnimConfig().setEase(new EaseManager.InterpolateEaseStyle(16, new float[0]).setDuration(300));

    public static void createCardFolmeTouchStyle(View view) {
        Folme.useAt(view).touch().handleTouchOf(view, new AnimConfig());
    }

    public static String generateFaceDataName(Context context, List<String> list) {
        try {
            boolean[] zArr = new boolean[2];
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                int parseFaceDataNameIndex = parseFaceDataNameIndex(context, it.next());
                if (parseFaceDataNameIndex > 0 && parseFaceDataNameIndex <= 2) {
                    zArr[parseFaceDataNameIndex - 1] = true;
                }
            }
            for (int i = 0; i < 2; i++) {
                if (!zArr[i]) {
                    return context.getString(R.string.multi_face_name_prefix) + (i + 1);
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(context.getClass().getName(), e.getMessage(), e);
            return null;
        }
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap, float f, int i, int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawCircle(i, i2, f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    public static List<String> getEnrolledFaceList(Context context) {
        getFaceManager(context);
        List enrolledFaces = mWeakReferenceFaceManager.get().getEnrolledFaces();
        ArrayList arrayList = new ArrayList();
        if (enrolledFaces != null && enrolledFaces.size() > 0) {
            Iterator it = enrolledFaces.iterator();
            while (it.hasNext()) {
                arrayList.add(Integer.toString(((Face) it.next()).getBiometricId()));
            }
        }
        return arrayList;
    }

    public static int getEnrolledFacesNumber(Context context) {
        getFaceManager(context);
        return mWeakReferenceFaceManager.get().getEnrolledFaces().size();
    }

    public static long getFaceDataCreateDate(Context context, String str) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("face_create_date", 0);
        long j = sharedPreferences.getLong(str, 0L);
        if (j == 0) {
            long currentTimeMillis = System.currentTimeMillis();
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putLong(str, currentTimeMillis);
            edit.commit();
            return currentTimeMillis;
        }
        return j;
    }

    public static String getFaceDataName(Context context, String str) {
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "settings_face_id_prefix_" + str, 0);
        if (TextUtils.isEmpty(stringForUser)) {
            stringForUser = context.getSharedPreferences("face_name", 0).getString(str, "");
            if (!TextUtils.isEmpty(stringForUser)) {
                setFaceDataName(context, str, stringForUser);
            }
        }
        return stringForUser;
    }

    private static void getFaceManager(Context context) {
        WeakReference<BaseMiuiFaceManager> weakReference = mWeakReferenceFaceManager;
        if (weakReference == null || weakReference.get() == null) {
            mWeakReferenceFaceManager = new WeakReference<>((BaseMiuiFaceManager) context.getSystemService("miui_face"));
        }
    }

    public static Bitmap getFirstFrameOfVideo(Context context, int i) {
        Uri parse = Uri.parse("android.resource://" + context.getPackageName() + "/" + i);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(context, parse);
        return mediaMetadataRetriever.getFrameAtTime(1000L, 0);
    }

    public static int getStatusBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", ThemeResources.FRAMEWORK_PACKAGE);
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static boolean hasEnrolledFaces(Context context) {
        getFaceManager(context);
        return mWeakReferenceFaceManager.get().hasEnrolledTemplates();
    }

    public static boolean isDeviceProvisioned(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) == 1;
    }

    public static boolean isLargeScreen(Context context) {
        return Build.DEVICE.equals("cetus") && (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    public static boolean isSupportFaceUnlock(Context context) {
        getFaceManager(context);
        return mWeakReferenceFaceManager.get().isHardwareDetected();
    }

    public static boolean isSupportLiftingCamera(Context context) {
        try {
            Class<?> cls = Class.forName("miui.os.DeviceFeature");
            return ((Boolean) cls.getDeclaredMethod("hasPopupCameraSupport", null).invoke(cls, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("miui_face", "reflect error when get hasPopupCameraSupport state", e);
            return false;
        }
    }

    public static boolean isSupportMultiFaceInput(Context context) {
        getFaceManager(context);
        return mWeakReferenceFaceManager.get().isSupportMultiFaceInput();
    }

    private static int parseFaceDataNameIndex(Context context, String str) {
        if (TextUtils.isEmpty(getFaceDataName(context, str))) {
            return -1;
        }
        return r0.charAt(r0.length() - 1) - '0';
    }

    public static void removeFaceData(Context context, String str) {
        SharedPreferences.Editor edit = context.getSharedPreferences("face_create_date", 0).edit();
        edit.remove(str);
        edit.commit();
        SharedPreferences.Editor edit2 = context.getSharedPreferences("face_name", 0).edit();
        edit2.remove(str);
        edit2.commit();
        Settings.Secure.putStringForUser(context.getContentResolver(), "settings_face_id_prefix_" + str, null, 0);
    }

    public static void resetFaceUnlockSettingValues(Context context) {
        if (!(isSupportMultiFaceInput(context) && getEnrolledFacesNumber(context) == 0) && isSupportMultiFaceInput(context)) {
            return;
        }
        Settings.Secure.putIntForUser(context.getContentResolver(), "face_unlcok_apply_for_lock", 0, UserHandle.myUserId());
        Settings.Secure.putIntForUser(context.getContentResolver(), "face_unlock_success_stay_screen", 0, UserHandle.myUserId());
        Settings.Secure.putIntForUser(context.getContentResolver(), "face_unlock_success_show_message", 0, UserHandle.myUserId());
        Settings.Secure.putIntForUser(context.getContentResolver(), "face_unlock_by_notification_screen_on", 0, UserHandle.myUserId());
    }

    public static void setFaceDataName(Context context, String str, String str2) {
        Settings.Secure.putStringForUser(context.getContentResolver(), "settings_face_id_prefix_" + str, str2, 0);
    }

    public static void setFaceEnrollViewStatus(Context context, Window window) {
        window.setStatusBarColor(0);
        window.setNavigationBarColor(0);
        window.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        window.addFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        window.addFlags(128);
    }

    public static void setFaceUnlockSettingValues(Context context, int i) {
        if (!(isSupportMultiFaceInput(context) && i == 1) && isSupportMultiFaceInput(context)) {
            return;
        }
        Settings.Secure.putIntForUser(context.getContentResolver(), "face_unlcok_apply_for_lock", 1, UserHandle.myUserId());
        Settings.Secure.putIntForUser(context.getContentResolver(), "face_unlock_success_stay_screen", !isSupportLiftingCamera(context) ? 1 : 0, UserHandle.myUserId());
        Settings.Secure.putIntForUser(context.getContentResolver(), "face_unlock_success_show_message", 0, UserHandle.myUserId());
        ContentResolver contentResolver = context.getContentResolver();
        boolean isSupportLiftingCamera = isSupportLiftingCamera(context);
        Settings.Secure.putIntForUser(contentResolver, "face_unlock_by_notification_screen_on", isSupportLiftingCamera ? 1 : 0, UserHandle.myUserId());
    }
}
