package miui.drm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import miui.app.constants.ThemeManagerConstants;
import miui.content.res.ThemeResources;
import miui.content.res.ThemeRuntimeManager;
import miui.drm.DrmManager;
import miui.util.HashUtils;

/* loaded from: classes3.dex */
public class ThemeReceiver extends BroadcastReceiver {
    private static final String TAG = "drm";
    private static Map<String, String> sLocations = new HashMap();
    private static Set<String> sWhiteList = new HashSet();
    private boolean mIsValidating = false;

    /* loaded from: classes3.dex */
    private class ValidateThemeTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;

        public ValidateThemeTask(Context context) {
            this.mContext = context;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            try {
                for (Map.Entry entry : ThemeReceiver.sLocations.entrySet()) {
                    if (!ThemeReceiver.this.validateTheme(this.mContext, (String) entry.getKey(), (String) entry.getValue())) {
                        Log.w("drm", "restore default theme in " + ((String) entry.getKey()));
                        new ThemeRuntimeManager(this.mContext).restoreDefault();
                        return null;
                    }
                }
                return null;
            } catch (Exception e) {
                Log.i("drm", "check theme drm occur exception: " + e.toString());
                e.printStackTrace();
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute((ValidateThemeTask) r1);
            ThemeReceiver.this.mIsValidating = false;
        }
    }

    static {
        sLocations.put("/data/system/theme/", ThemeResources.THEME_RIGHTS_PATH);
        sLocations.put(ThemeRuntimeManager.RUNTIME_PATH_BOOT_ANIMATION, ThemeResources.THEME_RIGHTS_PATH);
        for (String str : ThemeManagerConstants.DRM_WHITE_LIST) {
            sWhiteList.add("/data/system/theme/" + str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean validateTheme(Context context, String str, String str2) {
        String[] list;
        Log.i("drm", "validate theme in " + str);
        File file = new File(str);
        File file2 = new File(str2);
        if (file.exists() && !sWhiteList.contains(file.getAbsolutePath())) {
            if (file.isDirectory()) {
                for (File file3 : file.listFiles()) {
                    if (!validateTheme(context, file3.getAbsolutePath(), str2)) {
                        return false;
                    }
                }
            } else {
                Log.i("drm", "checking component " + file.getAbsolutePath() + " with " + file2.getAbsolutePath());
                DrmManager.DrmResult isLegal = DrmManager.isLegal(context, file, file2);
                if (isLegal != DrmManager.DrmResult.DRM_SUCCESS) {
                    DrmManager.exportFatalLog("drm", "illegal theme component found: " + file.getAbsolutePath() + " hash:" + HashUtils.getSHA1(file) + " " + isLegal);
                    StringBuilder sb = new StringBuilder();
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("checking rightFolder: ");
                    sb2.append(file2.getAbsolutePath());
                    sb.append(sb2.toString());
                    if (file2.isDirectory() && (list = file2.list()) != null) {
                        for (String str3 : list) {
                            sb.append(" " + str3);
                        }
                    }
                    DrmManager.exportFatalLog("drm", sb.toString());
                    return false;
                }
            }
        }
        return true;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (DrmBroadcast.ACTION_CHECK_TIME_UP.equals(intent.getAction())) {
            Log.i("drm", "check theme drm event received");
            if (this.mIsValidating) {
                Log.w("drm", "Validating theme task is running. ");
                return;
            }
            this.mIsValidating = true;
            new ValidateThemeTask(context).execute(new Void[0]);
        }
    }
}
