package com.miui.maml;

import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import com.miui.maml.util.HideSdkDependencyUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import miui.content.res.ThemeResources;
import miui.util.IOUtils;

/* loaded from: classes2.dex */
public class SoundManager implements SoundPool.OnLoadCompleteListener {
    private Handler mHandler;
    private boolean mInitialized;
    private ResourceManager mResourceManager;
    private SoundPool mSoundPool;
    private HashMap<String, Integer> mSoundPoolMap = new HashMap<>();
    private HashMap<Integer, SoundOptions> mPendingSoundMap = new HashMap<>();
    private ArrayList<Integer> mPlayingSoundMap = new ArrayList<>();
    private Object mInitSignal = new Object();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.SoundManager$2  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$SoundManager$Command;

        static {
            int[] iArr = new int[Command.values().length];
            $SwitchMap$com$miui$maml$SoundManager$Command = iArr;
            try {
                iArr[Command.Play.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$SoundManager$Command[Command.Pause.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$SoundManager$Command[Command.Resume.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$maml$SoundManager$Command[Command.Stop.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* loaded from: classes2.dex */
    public enum Command {
        Play,
        Pause,
        Resume,
        Stop;

        public static Command parse(String str) {
            return "pause".equals(str) ? Pause : "resume".equals(str) ? Resume : "stop".equals(str) ? Stop : Play;
        }
    }

    /* loaded from: classes2.dex */
    public static class SoundOptions {
        public boolean mKeepCur;
        public boolean mLoop;
        public float mVolume;

        public SoundOptions(boolean z, boolean z2, float f) {
            this.mKeepCur = z;
            this.mLoop = z2;
            if (f < 0.0f) {
                this.mVolume = 0.0f;
            } else if (f > 1.0f) {
                this.mVolume = 1.0f;
            } else {
                this.mVolume = f;
            }
        }
    }

    public SoundManager(ScreenContext screenContext) {
        this.mResourceManager = screenContext.mResourceManager;
        this.mHandler = screenContext.getHandler();
    }

    private void init() {
        if (this.mInitialized) {
            return;
        }
        if (Thread.currentThread().getId() == this.mHandler.getLooper().getThread().getId()) {
            SoundPool soundPool = new SoundPool(8, 3, 100);
            this.mSoundPool = soundPool;
            soundPool.setOnLoadCompleteListener(this);
            this.mInitialized = true;
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.miui.maml.SoundManager.1
            @Override // java.lang.Runnable
            public void run() {
                SoundManager.this.mSoundPool = new SoundPool(8, 3, 100);
                SoundManager.this.mSoundPool.setOnLoadCompleteListener(SoundManager.this);
                synchronized (SoundManager.this.mInitSignal) {
                    SoundManager.this.mInitialized = true;
                    SoundManager.this.mInitSignal.notify();
                }
            }
        });
        synchronized (this.mInitSignal) {
            while (!this.mInitialized) {
                try {
                    this.mInitSignal.wait();
                } catch (InterruptedException unused) {
                }
            }
        }
    }

    private synchronized int playSoundImp(int i, SoundOptions soundOptions) {
        int play;
        if (this.mSoundPool == null) {
            return 0;
        }
        if (!soundOptions.mKeepCur) {
            stopAllPlaying();
        }
        try {
            synchronized (this.mPlayingSoundMap) {
                SoundPool soundPool = this.mSoundPool;
                float f = soundOptions.mVolume;
                play = soundPool.play(i, f, f, 1, soundOptions.mLoop ? -1 : 0, 1.0f);
                this.mPlayingSoundMap.add(Integer.valueOf(play));
            }
            return play;
        } catch (Exception e) {
            Log.e("MamlSoundManager", e.toString());
            return 0;
        }
    }

    @Override // android.media.SoundPool.OnLoadCompleteListener
    public void onLoadComplete(SoundPool soundPool, int i, int i2) {
        if (i2 == 0) {
            playSoundImp(i, this.mPendingSoundMap.get(Integer.valueOf(i)));
        }
        this.mPendingSoundMap.remove(Integer.valueOf(i));
    }

    public void pause() {
        stopAllPlaying();
    }

    public synchronized int playSound(String str, SoundOptions soundOptions) {
        Integer num;
        if (!this.mInitialized) {
            init();
        }
        if (this.mSoundPool == null) {
            return 0;
        }
        Integer num2 = this.mSoundPoolMap.get(str);
        if (num2 != null) {
            return playSoundImp(num2.intValue(), soundOptions);
        }
        if (Build.VERSION.SDK_INT < 26) {
            MemoryFile file = this.mResourceManager.getFile(str);
            if (file == null) {
                Log.e("MamlSoundManager", "the sound does not exist: " + str);
                return 0;
            } else if (file.length() > 524288) {
                Log.w("MamlSoundManager", String.format("the sound file is larger than %d KB: %s", 512, str));
                return 0;
            } else {
                num = Integer.valueOf(this.mSoundPool.load(HideSdkDependencyUtils.MemoryFile_getFileDescriptor(file), 0L, file.length(), 1));
                this.mSoundPoolMap.put(str, num);
                file.close();
            }
        } else {
            File file2 = new File(ThemeResources.THEME_MAGIC_PATH + "lockscreen_audio/advance/" + str);
            if (!file2.exists()) {
                Log.e("MamlSoundManager", "the sound does not exist: " + str);
                return 0;
            } else if (file2.length() > PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED) {
                Log.w("MamlSoundManager", String.format("the sound file is larger than %d KB: %s", 512, str));
                return 0;
            } else {
                ParcelFileDescriptor parcelFileDescriptor = null;
                try {
                    try {
                        parcelFileDescriptor = ParcelFileDescriptor.open(file2, 268435456);
                        if (parcelFileDescriptor != null) {
                            num2 = Integer.valueOf(this.mSoundPool.load(parcelFileDescriptor.getFileDescriptor(), 0L, file2.length(), 1));
                            this.mSoundPoolMap.put(str, num2);
                        }
                    } catch (IOException e) {
                        Log.e("MamlSoundManager", "fail to load sound. ", e);
                    }
                    num = num2;
                } finally {
                    IOUtils.closeQuietly(parcelFileDescriptor);
                }
            }
        }
        this.mPendingSoundMap.put(num, soundOptions);
        return 0;
    }

    public synchronized void playSound(int i, Command command) {
        if (!this.mInitialized) {
            init();
        }
        if (this.mSoundPool != null && i > 0) {
            int i2 = AnonymousClass2.$SwitchMap$com$miui$maml$SoundManager$Command[command.ordinal()];
            if (i2 == 2) {
                this.mSoundPool.pause(i);
            } else if (i2 == 3) {
                this.mSoundPool.resume(i);
            } else if (i2 == 4) {
                this.mSoundPool.stop(i);
                synchronized (this.mPlayingSoundMap) {
                    this.mPlayingSoundMap.remove(Integer.valueOf(i));
                }
            }
        }
    }

    public synchronized void release() {
        if (this.mInitialized) {
            stopAllPlaying();
            if (this.mSoundPool != null) {
                this.mSoundPoolMap.clear();
                this.mSoundPool.setOnLoadCompleteListener(null);
                this.mSoundPool.release();
                this.mSoundPool = null;
            }
            this.mInitialized = false;
        }
    }

    protected void stopAllPlaying() {
        if (this.mPlayingSoundMap.isEmpty()) {
            return;
        }
        synchronized (this.mPlayingSoundMap) {
            if (this.mSoundPool != null) {
                Iterator<Integer> it = this.mPlayingSoundMap.iterator();
                while (it.hasNext()) {
                    this.mSoundPool.stop(it.next().intValue());
                }
            }
            this.mPlayingSoundMap.clear();
        }
    }
}
