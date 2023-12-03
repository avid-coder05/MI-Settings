package com.android.settings.shoulderkey;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settings.R;
import java.util.ArrayList;
import miui.provider.Weather;

/* loaded from: classes2.dex */
public class SoundPoolUtil {
    private static Context mContext;
    private static SoundPool mSoundPool;
    private static final ArrayMap<String, Integer> SOUNDS_MAP = new ArrayMap<>();
    private static final ArrayList<Integer> LOADED_SOUND_IDS = new ArrayList<>();
    private static boolean mIsSoundPooLoadComplete = false;

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkSoundPoolLoadCompleted() {
        if (LOADED_SOUND_IDS.size() == 4) {
            mIsSoundPooLoadComplete = true;
        }
    }

    public static void init(Context context) {
        mContext = context;
        if (Build.VERSION.SDK_INT >= 21) {
            mSoundPool = new SoundPool.Builder().setMaxStreams(20).setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(1).build()).build();
        } else {
            mSoundPool = new SoundPool(10, 1, 0);
        }
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() { // from class: com.android.settings.shoulderkey.SoundPoolUtil.1
            @Override // android.media.SoundPool.OnLoadCompleteListener
            public void onLoadComplete(SoundPool soundPool, int i, int i2) {
                if (i2 == 0) {
                    Log.d("SoundPoolUtil", "SoundPool Load Complete, sampleId:" + i);
                    SoundPoolUtil.LOADED_SOUND_IDS.add(Integer.valueOf(i));
                    SoundPoolUtil.checkSoundPoolLoadCompleted();
                }
            }
        });
        ArrayMap<String, Integer> arrayMap = SOUNDS_MAP;
        arrayMap.put("classic", Integer.valueOf(loadSound(R.raw.keys_kanata_open_l)));
        arrayMap.put("bullet", Integer.valueOf(loadSound(R.raw.keys_mechanicals_open_l)));
        arrayMap.put("current", Integer.valueOf(loadSound(R.raw.keys_scifi_open_l)));
        arrayMap.put(Weather.WeatherBaseColumns.WIND, Integer.valueOf(loadSound(R.raw.keys_car_open_l)));
    }

    private static int loadSound(int i) {
        SoundPool soundPool = mSoundPool;
        if (soundPool == null) {
            return -1;
        }
        return soundPool.load(mContext, i, 1);
    }

    public static void play(String str, boolean z) {
        if (mIsSoundPooLoadComplete) {
            ArrayMap<String, Integer> arrayMap = SOUNDS_MAP;
            if (arrayMap.indexOfKey(str) >= 0) {
                mSoundPool.play(arrayMap.get(str).intValue(), 1.0f, 1.0f, 1, z ? -1 : 0, 0.95f);
            }
        }
    }

    public static void release() {
        if (mSoundPool != null) {
            Log.d("SoundPoolUtil", "SoundPool release");
            mIsSoundPooLoadComplete = false;
            mSoundPool.release();
            SOUNDS_MAP.clear();
            LOADED_SOUND_IDS.clear();
            mSoundPool = null;
        }
    }
}
