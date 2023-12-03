package com.android.settings.bluetooth;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import com.android.settings.R;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class SpatialSoundWrapper {
    private Method isSupportSpatialAudio;
    private Method mMethod3DSurroundEnable;
    private Object mMiSoundInstance;
    private Method switchSpatialAudio;

    public SpatialSoundWrapper() {
        this.mMethod3DSurroundEnable = null;
        this.isSupportSpatialAudio = null;
        this.switchSpatialAudio = null;
        try {
            Class<?> cls = Class.forName("android.media.audiofx.MiSound");
            Class<?> cls2 = Integer.TYPE;
            Object newInstance = cls.getConstructor(cls2, cls2).newInstance(0, 0);
            this.mMiSoundInstance = newInstance;
            if (newInstance != null) {
                this.mMethod3DSurroundEnable = cls.getMethod("set3dSurround", cls2);
            } else {
                Log.e("SpatialSoundWrapper", "android.media.audiofx.MiSound newInstance get null");
            }
            Class<?> cls3 = Class.forName("android.media.audiofx.MiEffectUtils");
            this.isSupportSpatialAudio = cls3.getMethod("isSupportSpatialAudio", new Class[0]);
            this.switchSpatialAudio = cls3.getMethod("switchSpatialAudio", Context.class, Boolean.TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getAudioFeature() {
        return SystemProperties.getInt("ro.vendor.audio.feature.spatial", 0);
    }

    public String getHeadTrackSummary(Context context, boolean z) {
        if (z && isSupportHeadTrackAlgoPhone()) {
            return context.getString(R.string.headset_head_tracking_desc);
        }
        return null;
    }

    public boolean isEnable3DSurround() {
        return SystemProperties.getBoolean("persist.vendor.audio.3dsurround.enable", false);
    }

    public boolean isPhoneSupportSurroundAlgo() {
        return (getAudioFeature() & 2) != 0;
    }

    public boolean isSupportHeadTrackAlgoPhone() {
        Method method = this.isSupportSpatialAudio;
        if (method == null) {
            Log.e("SpatialSoundWrapper", "isSupportSpatialAudio meth is null");
            return false;
        }
        try {
            Boolean bool = (Boolean) method.invoke(null, new Object[0]);
            if (bool != null) {
                return bool.booleanValue();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSupportSpatialAndSurround() {
        return (getAudioFeature() & 4) != 0;
    }
}
