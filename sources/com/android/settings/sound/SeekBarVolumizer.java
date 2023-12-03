package com.android.settings.sound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import androidx.preference.PreferenceManager;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.utils.ThreadUtils;
import com.miui.whetstone.ReflectionUtils;
import miui.util.AudioManagerHelper;

/* loaded from: classes2.dex */
public class SeekBarVolumizer implements SeekBar.OnSeekBarChangeListener, SharedPreferences.OnSharedPreferenceChangeListener, Handler.Callback {
    public static final Uri VOICE_XIAOMI_ASSISTANT_URI = Uri.parse("android.resource://com.android.settings/" + R.raw.xiaoai_mitang_volume);
    private static boolean sIS_SAMPLE_PLAY;
    private AudioManager mAudioManager;
    private Context mContext;
    private double mDegreePerVolume;
    private Handler mHandler;
    private int mMinProgress;
    private int mMinVolume;
    private VolumeSeekBarPreference mPreference;
    private boolean mRequestFocus;
    private Ringtone mRingtone;
    private SeekBar mSeekBar;
    private SharedPreferences mSharedPreferences;
    private int mStream;
    private int mLastVolume = -1;
    private volatile boolean mIsUpdate = true;
    private int mLastRecordVolume = -1;
    private Receiver mReceiver = new Receiver(this, null);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.sound.SeekBarVolumizer$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public class AnonymousClass1 implements View.OnTouchListener {
        AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouch$0() {
            SeekBarVolumizer.this.mIsUpdate = true;
        }

        /* JADX WARN: Code restructure failed: missing block: B:7:0x000b, code lost:
        
            if (r4 != 2) goto L11;
         */
        @Override // android.view.View.OnTouchListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouch(android.view.View r4, android.view.MotionEvent r5) {
            /*
                r3 = this;
                int r4 = r5.getAction()
                r5 = 0
                if (r4 == 0) goto L1d
                r0 = 1
                if (r4 == r0) goto Le
                r0 = 2
                if (r4 == r0) goto L1d
                goto L22
            Le:
                android.os.Handler r4 = com.android.settingslib.utils.ThreadUtils.getUiThreadHandler()
                com.android.settings.sound.SeekBarVolumizer$1$$ExternalSyntheticLambda0 r0 = new com.android.settings.sound.SeekBarVolumizer$1$$ExternalSyntheticLambda0
                r0.<init>()
                r1 = 10
                r4.postDelayed(r0, r1)
                goto L22
            L1d:
                com.android.settings.sound.SeekBarVolumizer r3 = com.android.settings.sound.SeekBarVolumizer.this
                com.android.settings.sound.SeekBarVolumizer.access$102(r3, r5)
            L22:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.sound.SeekBarVolumizer.AnonymousClass1.onTouch(android.view.View, android.view.MotionEvent):boolean");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class Receiver extends BroadcastReceiver {
        private boolean mListening;

        private Receiver() {
        }

        /* synthetic */ Receiver(SeekBarVolumizer seekBarVolumizer, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.sound.SeekBarVolumizer.Receiver.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!"android.media.VOLUME_CHANGED_ACTION".equals(action)) {
                        if ("android.media.RINGER_MODE_CHANGED".equals(action) || "android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION".equals(action)) {
                            if (SeekBarVolumizer.this.mHandler != null) {
                                SeekBarVolumizer.this.mHandler.removeMessages(4);
                                SeekBarVolumizer.this.mHandler.sendEmptyMessage(4);
                            }
                        } else if ("android.intent.action.HEADSET_PLUG".equals(action) && SeekBarVolumizer.this.mStream == 3) {
                            int lastAudibleStreamVolume = SeekBarVolumizer.this.mAudioManager.getLastAudibleStreamVolume(SeekBarVolumizer.this.mStream);
                            if (SeekBarVolumizer.this.mSeekBar != null) {
                                SeekBarVolumizer seekBarVolumizer = SeekBarVolumizer.this;
                                if (lastAudibleStreamVolume != seekBarVolumizer.progressToVolume(seekBarVolumizer.mSeekBar.getProgress())) {
                                    SeekBarVolumizer.this.mSeekBar.setProgress(SeekBarVolumizer.this.volumeToProgress(lastAudibleStreamVolume));
                                }
                            }
                        }
                        SeekBarVolumizer.this.mPreference.refreshIconState();
                    } else if (SeekBarVolumizer.this.mIsUpdate) {
                        int intExtra = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
                        int intExtra2 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1);
                        if (intExtra == SeekBarVolumizer.this.mStream && SeekBarVolumizer.this.mSeekBar != null) {
                            SeekBarVolumizer seekBarVolumizer2 = SeekBarVolumizer.this;
                            if (seekBarVolumizer2.progressToVolume(seekBarVolumizer2.mSeekBar.getProgress()) != intExtra2) {
                                SeekBarVolumizer.this.mSeekBar.setProgress(SeekBarVolumizer.this.volumeToProgress(intExtra2));
                                SeekBarVolumizer.this.mPreference.updateSeekBarDrawable();
                            }
                        }
                        if (SeekBarVolumizer.this.mLastRecordVolume == -1 || SeekBarVolumizer.this.mLastRecordVolume == 0 || intExtra2 == 0) {
                            SeekBarVolumizer.this.mPreference.refreshIconState();
                        }
                        SeekBarVolumizer.this.mLastRecordVolume = intExtra2;
                    }
                }
            });
        }

        public void setListening(boolean z) {
            if (this.mListening == z) {
                return;
            }
            this.mListening = z;
            if (!z) {
                SeekBarVolumizer.this.mContext.unregisterReceiver(this);
                return;
            }
            IntentFilter intentFilter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
            intentFilter.addAction("android.media.RINGER_MODE_CHANGED");
            intentFilter.addAction("android.intent.action.HEADSET_PLUG");
            SeekBarVolumizer.this.mContext.registerReceiver(this, intentFilter);
        }
    }

    public SeekBarVolumizer(VolumeSeekBarPreference volumeSeekBarPreference) {
        this.mMinVolume = 0;
        this.mMinProgress = 0;
        this.mPreference = volumeSeekBarPreference;
        this.mContext = volumeSeekBarPreference.getContext().getApplicationContext();
        this.mStream = volumeSeekBarPreference.getStream();
        this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
        this.mDegreePerVolume = 1080.0d / r5.getStreamMaxVolume(this.mStream);
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        int streamMinVolumeInt = this.mAudioManager.getStreamMinVolumeInt(this.mStream);
        this.mMinVolume = streamMinVolumeInt;
        this.mMinProgress = volumeToProgress(streamMinVolumeInt);
    }

    private void fadeVolumeTo(float f, float f2, int i) {
        float f3;
        float f4 = f2 - f;
        if (f4 == 0.0f) {
            return;
        }
        float f5 = f;
        for (int i2 = 1; i2 < i + 1; i2++) {
            if (f4 > 0.0f) {
                if (f5 >= f2) {
                    break;
                }
                float f6 = i2 / i;
                f3 = f6 * f6;
                f5 = (f3 * f4) + f;
                this.mRingtone.setVolume(f5);
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException unused) {
                }
            } else if (f5 <= f2) {
                break;
            } else {
                float f7 = 1.0f - (i2 / i);
                f3 = 1.0f - (f7 * f7);
                f5 = (f3 * f4) + f;
                this.mRingtone.setVolume(f5);
                Thread.sleep(10L);
            }
        }
        if (f5 != f2) {
            this.mRingtone.setVolume(f2);
            try {
                Thread.sleep(10L);
            } catch (InterruptedException unused2) {
            }
        }
    }

    private Uri getDefaultUri() {
        int i = this.mStream;
        if (i == 2) {
            return Settings.System.DEFAULT_RINGTONE_URI;
        }
        if (i == 4) {
            return Settings.System.DEFAULT_ALARM_ALERT_URI;
        }
        if (i == 11) {
            return VOICE_XIAOMI_ASSISTANT_URI;
        }
        return Uri.parse("android.resource://" + this.mContext.getPackageName() + "/" + R.raw.media_volume);
    }

    private void handleRingerModeChange() {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager == null || this.mContext == null || this.mStream != audioManager.getUiSoundsStreamType() || !AudioManagerHelper.isSilentEnabled(this.mContext)) {
            return;
        }
        postStopSample(false);
    }

    private void onDisableLoop() {
        Ringtone ringtone = this.mRingtone;
        if (ringtone != null) {
            ringtone.setLooping(false);
        }
    }

    private void onSetVolume() {
        this.mAudioManager.setStreamVolume(this.mStream, this.mLastVolume, (this.mStream == this.mAudioManager.getUiSoundsStreamType() || (this.mStream == 3 && this.mLastVolume == 0)) ? 1049600 : MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE);
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            volumeSeekBarPreference.refreshIconState();
        }
    }

    private void onStartSample(boolean z) {
        Log.i("SeekBarVolumizer", "onStartSample");
        if (z) {
            this.mSharedPreferences.edit().putInt("volume_sample_stream", this.mStream).commit();
        }
        Ringtone ringtone = this.mRingtone;
        if (ringtone != null) {
            if (ringtone.isPlaying()) {
                Log.i("SeekBarVolumizer", "onStartSample isPlaying");
                this.mRingtone.setLooping(true);
                return;
            }
            Log.i("SeekBarVolumizer", "onStartSample restart ringtone");
            this.mRingtone.setLooping(true);
            ReflectionUtils.callMethod(this.mRingtone, "startLocalPlayer", new Object[0]);
        } else if (AudioSystem.isStreamActive(this.mStream, 0)) {
            Log.i("SeekBarVolumizer", "onStartSample isStreamActive");
            if (z) {
                postStartSampleDelay(200L, false);
            }
        } else {
            this.mAudioManager.requestAudioFocus(null, 3, 2);
            this.mRequestFocus = true;
            Ringtone ringtone2 = RingtoneManager.getRingtone(this.mContext, getDefaultUri());
            this.mRingtone = ringtone2;
            if (ringtone2 != null) {
                ringtone2.setStreamType(this.mStream);
                this.mRingtone.setLooping(true);
                ReflectionUtils.callMethod(this.mRingtone, "startLocalPlayer", new Object[0]);
            }
        }
    }

    private void onStopSample(boolean z) {
        Log.i("SeekBarVolumizer", "onStopSample");
        if (this.mRingtone != null) {
            if (z) {
                fadeVolumeTo(1.0f, 0.0f, 100);
            }
            this.mRingtone.stop();
            this.mRingtone = null;
            if (this.mRequestFocus) {
                this.mAudioManager.abandonAudioFocus(null);
                this.mRequestFocus = false;
            }
            if (this.mSharedPreferences.getInt("volume_sample_stream", this.mStream) == this.mStream) {
                sIS_SAMPLE_PLAY = false;
            }
        }
    }

    private void postSetVolume() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(0);
            Handler handler2 = this.mHandler;
            handler2.sendMessage(handler2.obtainMessage(0));
        }
    }

    private void postStartSample(boolean z) {
        Handler handler;
        if (this.mStream == 11 && (handler = this.mHandler) != null) {
            handler.removeMessages(3);
        }
        sIS_SAMPLE_PLAY = true;
        postStartSampleDelay(0L, z);
    }

    private void postStartSampleDelay(long j, boolean z) {
        Handler handler = this.mHandler;
        if (handler != null) {
            if (z) {
                handler.removeMessages(2);
            }
            this.mHandler.removeMessages(1);
            Handler handler2 = this.mHandler;
            handler2.sendMessageDelayed(handler2.obtainMessage(1, Boolean.valueOf(z)), j);
        }
    }

    private void postStopSample(boolean z) {
        postStopSampleDelay(0L, z);
    }

    private void postStopSampleDelay(long j, boolean z) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.sendMessageDelayed(handler.obtainMessage(3), j != 0 ? 500L : 0L);
            this.mHandler.removeMessages(2);
            Handler handler2 = this.mHandler;
            handler2.sendMessageDelayed(handler2.obtainMessage(2, Boolean.valueOf(z)), j);
        }
    }

    private void refreshVolume() {
        int lastAudibleStreamVolume = this.mAudioManager.getLastAudibleStreamVolume(this.mStream);
        if (this.mSeekBar == null || progressToVolume(this.mPreference.getProgress()) == lastAudibleStreamVolume) {
            return;
        }
        this.mSeekBar.setProgress(volumeToProgress(lastAudibleStreamVolume));
        this.mPreference.onProgressChanged(this.mSeekBar, volumeToProgress(lastAudibleStreamVolume), false);
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 0) {
            onSetVolume();
            return false;
        } else if (i == 1) {
            onStartSample(((Boolean) message.obj).booleanValue());
            return false;
        } else if (i == 2) {
            onStopSample(((Boolean) message.obj).booleanValue());
            return false;
        } else if (i == 3) {
            onDisableLoop();
            return false;
        } else if (i != 4) {
            return false;
        } else {
            handleRingerModeChange();
            return false;
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (z) {
            if (!sIS_SAMPLE_PLAY) {
                postStartSample(true);
            }
            int progressToVolume = progressToVolume(i);
            int i2 = this.mLastVolume;
            if (i2 != progressToVolume) {
                if (i2 == 0) {
                    postStartSample(false);
                }
                this.mLastVolume = progressToVolume;
                postSetVolume();
            }
            this.mPreference.onProgressChanged(seekBar, i, z);
        }
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (!"volume_sample_stream".equals(str) || this.mSharedPreferences.getInt("volume_sample_stream", this.mStream) == this.mStream) {
            return;
        }
        postStopSample(false);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        postStartSample(true);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (this.mLastVolume == 0) {
            postStopSample(false);
        } else {
            postStopSampleDelay(2000L, true);
        }
    }

    public void pause() {
        postStopSample(true);
        this.mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        this.mReceiver.setListening(false);
    }

    public int progressToVolume(int i) {
        double d = i;
        double d2 = this.mDegreePerVolume;
        int i2 = (int) (((d2 / 2.0d) + d) / d2);
        return (i <= 0 || d >= d2 / 2.0d) ? (i >= 1080 || d <= 1080.0d - (d2 / 2.0d)) ? i2 : i2 - 1 : i2 + 1;
    }

    public void resume() {
        if (this.mHandler == null) {
            HandlerThread handlerThread = new HandlerThread("SeekBarVolumizerHandler");
            handlerThread.start();
            this.mHandler = new Handler(handlerThread.getLooper(), this);
        }
        this.mReceiver.setListening(true);
        this.mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        refreshVolume();
    }

    public void setSeekBar(SeekBar seekBar) {
        this.mSeekBar = seekBar;
        int lastAudibleStreamVolume = this.mAudioManager.getLastAudibleStreamVolume(this.mStream);
        if (progressToVolume(this.mPreference.getProgress()) != lastAudibleStreamVolume) {
            this.mSeekBar.setProgress(volumeToProgress(lastAudibleStreamVolume));
        }
        this.mSeekBar.setOnTouchListener(new AnonymousClass1());
        this.mSeekBar.setOnSeekBarChangeListener(this);
        ((miuix.androidbasewidget.widget.SeekBar) seekBar).setDraggableMinPercentProgress(this.mMinProgress / seekBar.getMax());
        if (SettingsFeatures.isSupportSettingsHaptic(this.mContext)) {
            this.mSeekBar.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.sound.SeekBarVolumizer.2
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if (action == 0) {
                        MiuiUtils.enableSpringBackLayout(view, false);
                    } else if (action == 1 || action == 3) {
                        MiuiUtils.enableSpringBackLayout(view, true);
                    }
                    return false;
                }
            });
        }
    }

    public void stop() {
        pause();
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.getLooper().quitSafely();
            this.mHandler = null;
        }
    }

    public int volumeToProgress(int i) {
        return (int) ((i * this.mDegreePerVolume) + 0.5d);
    }
}
