package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import miui.os.Build;

/* loaded from: classes.dex */
public class SDCardStateUploader {
    public static String TAG = "SDCardStateUploader";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Context mContext;
    private String mRegion = Build.getRegion().toLowerCase();

    public SDCardStateUploader(Context context) {
        this.mContext = context;
    }

    public static boolean canUploadSDCardState(Context context) {
        if (Build.IS_ALPHA_BUILD || Build.IS_DEVELOPMENT_VERSION) {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String string = defaultSharedPreferences.getString("sd_card_current_date", "");
            String format = sdf.format(new Date());
            if (string.equals(format)) {
                return false;
            }
            SharedPreferences.Editor edit = defaultSharedPreferences.edit();
            edit.putString("sd_card_current_date", format);
            return edit.commit();
        }
        return false;
    }

    private boolean hasSDCard() {
        DiskInfo findDiskById;
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService("storage");
        for (VolumeInfo volumeInfo : storageManager.getVolumes()) {
            if (volumeInfo.getType() == 0 && (findDiskById = storageManager.findDiskById(volumeInfo.getDiskId())) != null && findDiskById.isSd() && volumeInfo.isMountedReadable()) {
                return true;
            }
        }
        return false;
    }

    public void upload() {
        boolean hasSDCard = hasSDCard();
        if (Build.IS_ALPHA_BUILD) {
            Log.v(TAG, "sdcard info, sd card = " + hasSDCard + ", region = " + this.mRegion);
        }
        HashMap hashMap = new HashMap();
        hashMap.put("region", this.mRegion);
        hashMap.put("hasSDCard", Boolean.valueOf(hasSDCard));
        OneTrackInterfaceUtils.track("external_sd_card", hashMap);
    }
}
