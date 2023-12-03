package com.android.settings.emergency.service;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.Thread;

/* loaded from: classes.dex */
public class AudioTrackManager {
    private static boolean IS_DEVICE_ONCE;
    private static boolean IS_DEVICE_SAKURA;
    private static AudioTrackManager mInstance;
    private AudioTrack audioTrack;
    private AudioTrack.Builder audioTrackBuilder;
    private int bufferSize;
    private DataInputStream dis;
    private AudioManager mAudioManager;
    private OnPlayCompleteListener mCompleteListener;
    private Thread recordThread;
    private boolean isCancelled = false;
    private boolean isStart = false;
    Runnable recordRunnable = new Runnable() { // from class: com.android.settings.emergency.service.AudioTrackManager.1
        @Override // java.lang.Runnable
        public void run() {
            try {
                Log.w("AudioTrackManager", "STAR PLAY SOS AUDIO ...");
                Process.setThreadPriority(-19);
                if (AudioTrackManager.this.audioTrack == null) {
                    AudioTrackManager audioTrackManager = AudioTrackManager.this;
                    audioTrackManager.audioTrack = audioTrackManager.audioTrackBuilder.build();
                    AudioTrack audioTrack = AudioTrackManager.this.audioTrack;
                    AudioTrackManager audioTrackManager2 = AudioTrackManager.this;
                    audioTrack.setPreferredDevice(audioTrackManager2.getTelephonyDevice(audioTrackManager2.mAudioManager));
                    AudioTrackManager.this.isCancelled = false;
                }
                byte[] bArr = new byte[AudioTrackManager.this.bufferSize];
                while (AudioTrackManager.this.dis.available() > 0 && !AudioTrackManager.this.isCancelled) {
                    int read = AudioTrackManager.this.dis.read(bArr);
                    if (read != -3 && read != -2 && read != 0 && read != -1) {
                        AudioTrackManager.this.audioTrack.play();
                        AudioTrackManager.this.audioTrack.write(bArr, 0, read);
                    }
                }
                if (AudioTrackManager.this.mCompleteListener != null && !AudioTrackManager.this.isCancelled) {
                    AudioTrackManager.this.mCompleteListener.onPlayComplete();
                }
                AudioTrackManager.this.stopPlay();
            } catch (Exception e) {
                Log.e("AudioTrackManager", "exception when play sos audio :", e);
            }
        }
    };

    /* loaded from: classes.dex */
    public interface OnPlayCompleteListener {
        void onPlayComplete();
    }

    static {
        String str = Build.DEVICE;
        IS_DEVICE_ONCE = "onc".equals(str);
        IS_DEVICE_SAKURA = "sakura".equals(str);
    }

    public AudioTrackManager(Context context) {
        boolean z = IS_DEVICE_ONCE;
        int i = (z || IS_DEVICE_SAKURA) ? 8000 : 16000;
        int i2 = (z || IS_DEVICE_SAKURA) ? 12 : 4;
        this.bufferSize = AudioTrack.getMinBufferSize(i, i2, 2);
        this.audioTrackBuilder = new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().setFlags(Integer.MIN_VALUE).setUsage(1).setContentType(2).build()).setBufferSizeInBytes(this.bufferSize * 2).setTransferMode(1).setAudioFormat(new AudioFormat.Builder().setChannelMask(i2).setEncoding(2).setSampleRate(i).build());
        this.mAudioManager = (AudioManager) context.getSystemService(AudioManager.class);
    }

    private void destroyThread() {
        try {
            try {
                this.isStart = false;
                Thread thread = this.recordThread;
                if (thread != null && Thread.State.RUNNABLE == thread.getState()) {
                    try {
                        Thread.sleep(500L);
                        this.recordThread.interrupt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                this.recordThread = null;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static synchronized AudioTrackManager getInstance(Context context) {
        AudioTrackManager audioTrackManager;
        synchronized (AudioTrackManager.class) {
            if (mInstance == null) {
                mInstance = new AudioTrackManager(context);
            }
            audioTrackManager = mInstance;
        }
        return audioTrackManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AudioDeviceInfo getTelephonyDevice(AudioManager audioManager) {
        for (AudioDeviceInfo audioDeviceInfo : audioManager.getDevices(2)) {
            if (audioDeviceInfo.getType() == 18) {
                return audioDeviceInfo;
            }
        }
        return null;
    }

    private void setPath(String str) throws Exception {
        this.dis = new DataInputStream(new FileInputStream(new File(str)));
    }

    private void startThread() {
        destroyThread();
        this.isStart = true;
        if (this.recordThread == null) {
            Thread thread = new Thread(this.recordRunnable);
            this.recordThread = thread;
            thread.start();
        }
    }

    public void cancelPlay() {
        Log.w("AudioTrackManager", "SOS AUDIO CANCELED...");
        this.isCancelled = true;
    }

    public void setOnCompleteListener(OnPlayCompleteListener onPlayCompleteListener) {
        this.mCompleteListener = onPlayCompleteListener;
    }

    public void startPlay(String str) {
        try {
            setPath(str);
            startThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        try {
            destroyThread();
            AudioTrack audioTrack = this.audioTrack;
            if (audioTrack != null) {
                if (audioTrack.getState() == 1) {
                    this.audioTrack.stop();
                }
                Log.w("AudioTrackManager", "RELEASE SOS AUDIO TRACK ...");
                this.audioTrack.release();
                this.audioTrack = null;
            }
            DataInputStream dataInputStream = this.dis;
            if (dataInputStream != null) {
                dataInputStream.close();
            }
        } catch (Exception e) {
            Log.e("AudioTrackManager", "exception when stop sos audio :", e);
        }
    }
}
