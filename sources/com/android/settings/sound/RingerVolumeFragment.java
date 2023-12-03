package com.android.settings.sound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.settings.BaseFragment;
import com.android.settings.PlatformUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import java.io.IOException;
import miui.content.res.ThemeResources;
import miuix.androidbasewidget.widget.SeekBar;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class RingerVolumeFragment extends BaseFragment implements View.OnKeyListener {
    private static final int[] SEEKBAR_MUTED_RES_ID;
    private static final int[] SEEKBAR_UNMUTED_RES_ID;
    private AudioManager mAudioManager;
    private BroadcastReceiver mRingModeChangedReceiver;
    private SeekBarVolumizer[] mSeekBarVolumizer;
    private BroadcastReceiver mVolumeChangedReceiver;
    static final int[] SECTION_ID = {R.id.ringer_section, R.id.notification_section, R.id.alarm_section, R.id.voice_section, R.id.media_section, R.id.bluetooth_section};
    private static final int[] SEEKBAR_TYPE = {2, 5, 4, 0, 3, 6};
    private final int[] DESCPTION_ID = {R.string.volume_ring_description, R.string.volume_notification_description, R.string.volume_alarm_description, R.string.volume_voice_description, R.string.volume_media_description, R.string.volume_bluetooth_description};
    private ImageView[] mCheckBoxes = new ImageView[SEEKBAR_MUTED_RES_ID.length];
    private SeekBar[] mSeekBars = new SeekBar[SECTION_ID.length];
    private Handler mHandler = new Handler() { // from class: com.android.settings.sound.RingerVolumeFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 101) {
                RingerVolumeFragment.this.updateSlidersAndMutedStates();
            } else if (i == 102) {
                int i2 = message.arg1;
                for (int i3 = 0; i3 < RingerVolumeFragment.SEEKBAR_TYPE.length; i3++) {
                    if (RingerVolumeFragment.SEEKBAR_TYPE[i3] == i2) {
                        RingerVolumeFragment.this.updateSlidersAndMutedStates(i3);
                    }
                }
            }
        }
    };

    /* loaded from: classes2.dex */
    public class SeekBarVolumizer implements SeekBar.OnSeekBarChangeListener, Runnable, MediaPlayer.OnCompletionListener {
        private AudioManager mAudioManager;
        private Context mContext;
        private Uri mDefaultUri;
        private double mDegreePerVolume;
        private Handler mHandler;
        private int mLastVolume;
        private int mMaxVolume;
        private MediaPlayer mMediaPlayer;
        private int mMinVolume;
        private boolean mRequestFocus;
        private miuix.androidbasewidget.widget.SeekBar mSeekBar;
        private int mStreamType;
        private int mVolumeBeforeMute;
        private ContentObserver mVolumeObserver;

        public SeekBarVolumizer(RingerVolumeFragment ringerVolumeFragment, Context context, miuix.androidbasewidget.widget.SeekBar seekBar, int i) {
            this(context, seekBar, i, null);
        }

        public SeekBarVolumizer(Context context, miuix.androidbasewidget.widget.SeekBar seekBar, int i, Uri uri) {
            this.mHandler = new Handler();
            this.mLastVolume = -1;
            this.mVolumeBeforeMute = -1;
            this.mVolumeObserver = new ContentObserver(this.mHandler) { // from class: com.android.settings.sound.RingerVolumeFragment.SeekBarVolumizer.1
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    super.onChange(z);
                    if (SeekBarVolumizer.this.mSeekBar == null || SeekBarVolumizer.this.mAudioManager == null) {
                        return;
                    }
                    int lastAudibleStreamVolume = SeekBarVolumizer.this.mAudioManager.isStreamMute(SeekBarVolumizer.this.mStreamType) ? SeekBarVolumizer.this.mAudioManager.getLastAudibleStreamVolume(SeekBarVolumizer.this.mStreamType) : SeekBarVolumizer.this.mAudioManager.getStreamVolume(SeekBarVolumizer.this.mStreamType);
                    SeekBarVolumizer seekBarVolumizer = SeekBarVolumizer.this;
                    if (lastAudibleStreamVolume != seekBarVolumizer.getVolume(seekBarVolumizer.mSeekBar.getProgress())) {
                        SeekBarVolumizer.this.mSeekBar.setProgress(SeekBarVolumizer.this.getProgress(lastAudibleStreamVolume));
                        SeekBarVolumizer.this.mLastVolume = lastAudibleStreamVolume;
                    }
                }
            };
            this.mContext = context;
            AudioManager audioManager = (AudioManager) context.getSystemService("audio");
            this.mAudioManager = audioManager;
            this.mStreamType = i;
            this.mSeekBar = seekBar;
            this.mMinVolume = audioManager.getStreamMinVolume(i);
            this.mMaxVolume = this.mAudioManager.getStreamMaxVolume(this.mStreamType);
            initSeekBar(seekBar, uri);
        }

        private void abandonAudioFocus() {
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                this.mMediaPlayer.release();
                this.mMediaPlayer = null;
            }
            if (this.mRequestFocus) {
                this.mAudioManager.abandonAudioFocus(null);
                this.mRequestFocus = false;
            }
        }

        private void initSeekBar(miuix.androidbasewidget.widget.SeekBar seekBar, Uri uri) {
            seekBar.setMax(100);
            this.mDegreePerVolume = 100.0d / this.mAudioManager.getStreamMaxVolume(this.mStreamType);
            seekBar.setProgress(getProgress(this.mAudioManager.getStreamVolume(this.mStreamType)));
            seekBar.setOnSeekBarChangeListener(this);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.VOLUME_SETTINGS[this.mStreamType]), false, this.mVolumeObserver);
            if (uri == null) {
                int i = this.mStreamType;
                if (i == 2) {
                    uri = Settings.System.DEFAULT_RINGTONE_URI;
                } else if (i == 5) {
                    uri = Settings.System.DEFAULT_NOTIFICATION_URI;
                } else if (i == 4) {
                    uri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                }
                if (uri == null) {
                    uri = RingerVolumeFragment.this.getMediaVolumeUri();
                }
            }
            this.mDefaultUri = uri;
        }

        int getProgress(int i) {
            int i2 = (int) (i * this.mDegreePerVolume);
            int i3 = this.mContext.getSharedPreferences("ringer_volume_progress", 0).getInt(Settings.System.VOLUME_SETTINGS[this.mStreamType], i2);
            return i == getVolume(i3) ? i3 : i2;
        }

        int getVolume(int i) {
            double d = this.mDegreePerVolume;
            int i2 = (int) ((i + (d / 2.0d)) / d);
            return (i <= 0 || i2 != 0) ? (i >= 100 || i2 != this.mMaxVolume) ? i2 : i2 - 1 : i2 + 1;
        }

        public boolean isSamplePlaying() {
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            return mediaPlayer != null && mediaPlayer.isPlaying();
        }

        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mediaPlayer) {
            abandonAudioFocus();
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(android.widget.SeekBar seekBar, int i, boolean z) {
            if (z) {
                double d = this.mDegreePerVolume;
                double d2 = (i + (d / 2.0d)) / d;
                int i2 = this.mMinVolume;
                if (d2 < i2) {
                    seekBar.setProgress(getProgress(i2));
                    return;
                }
                this.mContext.getSharedPreferences("ringer_volume_progress", 0).edit().putInt(Settings.System.VOLUME_SETTINGS[this.mStreamType], i).apply();
                postSetVolume(i);
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            if (isSamplePlaying()) {
                return;
            }
            startSample();
        }

        void postSetVolume(int i) {
            setLastVolume(i);
            this.mHandler.removeCallbacks(this);
            this.mHandler.post(this);
        }

        public void revertStreamVolume() {
            int defaultStreamVolume = PlatformUtils.getDefaultStreamVolume(this.mStreamType);
            int i = this.mStreamType;
            if (i == 3 && (this.mAudioManager.getDevicesForStream(i) & 12) != 0) {
                int integer = RingerVolumeFragment.this.getActivity().getResources().getInteger(RingerVolumeFragment.this.getActivity().getResources().getIdentifier("config_safe_media_volume_index", "integer", ThemeResources.FRAMEWORK_PACKAGE));
                if (defaultStreamVolume > integer) {
                    defaultStreamVolume = integer;
                }
            }
            this.mAudioManager.setStreamVolume(this.mStreamType, defaultStreamVolume, 0);
            this.mLastVolume = defaultStreamVolume;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mAudioManager.setStreamVolume(this.mStreamType, this.mLastVolume, MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE);
        }

        void setLastVolume(int i) {
            this.mLastVolume = getVolume(i);
        }

        public void startSample() {
            RingerVolumeFragment.this.onSampleStarting(this);
            if (this.mDefaultUri == null) {
                this.mMediaPlayer = null;
                return;
            }
            try {
                MediaPlayer mediaPlayer = new MediaPlayer();
                this.mMediaPlayer = mediaPlayer;
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.android.settings.sound.RingerVolumeFragment.SeekBarVolumizer.2
                    @Override // android.media.MediaPlayer.OnErrorListener
                    public boolean onError(MediaPlayer mediaPlayer2, int i, int i2) {
                        try {
                            mediaPlayer2.stop();
                        } catch (IllegalStateException unused) {
                        }
                        mediaPlayer2.release();
                        SeekBarVolumizer.this.mMediaPlayer = null;
                        return true;
                    }
                });
                this.mMediaPlayer.setDataSource(this.mContext, this.mDefaultUri);
                this.mMediaPlayer.setAudioStreamType(this.mStreamType);
                this.mMediaPlayer.setOnCompletionListener(this);
                this.mMediaPlayer.prepare();
                this.mMediaPlayer.start();
                this.mAudioManager.requestAudioFocus(null, 3, 2);
                this.mRequestFocus = true;
            } catch (IOException unused) {
                this.mMediaPlayer = null;
            } catch (SecurityException unused2) {
                this.mMediaPlayer = null;
            }
        }

        public void stop() {
            stopSample();
            this.mContext.getContentResolver().unregisterContentObserver(this.mVolumeObserver);
            this.mSeekBar.setOnSeekBarChangeListener(null);
        }

        public void stopSample() {
            if (this.mMediaPlayer != null) {
                abandonAudioFocus();
            }
        }
    }

    static {
        int i = R.drawable.ic_audio_phone;
        int i2 = R.drawable.ic_audio_media;
        SEEKBAR_MUTED_RES_ID = new int[]{R.drawable.ic_audio_ring_notif_mute, R.drawable.ic_audio_notification_mute, R.drawable.ic_audio_alarm_mute, i, i2, R.drawable.ic_audio_bt_mute};
        SEEKBAR_UNMUTED_RES_ID = new int[]{R.drawable.ic_audio_ring_notif, R.drawable.ic_audio_notification, R.drawable.ic_audio_alarm, i, i2, R.drawable.ic_audio_bt};
    }

    private void cleanup() {
        for (int i = 0; i < SECTION_ID.length; i++) {
            SeekBarVolumizer[] seekBarVolumizerArr = this.mSeekBarVolumizer;
            if (seekBarVolumizerArr[i] != null) {
                seekBarVolumizerArr[i].stop();
                this.mSeekBarVolumizer[i] = null;
            }
        }
        if (this.mRingModeChangedReceiver != null) {
            getActivity().unregisterReceiver(this.mRingModeChangedReceiver);
            this.mRingModeChangedReceiver = null;
        }
        if (this.mVolumeChangedReceiver != null) {
            getActivity().unregisterReceiver(this.mVolumeChangedReceiver);
            this.mVolumeChangedReceiver = null;
        }
        this.mHandler.removeMessages(101);
        this.mHandler.removeMessages(102);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Uri getMediaVolumeUri() {
        return Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.media_volume);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSlidersAndMutedStates() {
        for (int i = 0; i < SEEKBAR_TYPE.length; i++) {
            updateSlidersAndMutedStates(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSlidersAndMutedStates(int i) {
        int streamVolume;
        int i2 = SEEKBAR_TYPE[i];
        boolean isStreamMute = this.mAudioManager.isStreamMute(i2);
        if (this.mCheckBoxes[i] != null) {
            if (i2 == 2 && isStreamMute && this.mAudioManager.shouldVibrate(0)) {
                this.mCheckBoxes[i].setImageResource(R.drawable.ic_audio_ring_notif_vibrate);
            } else {
                this.mCheckBoxes[i].setImageResource(isStreamMute ? SEEKBAR_MUTED_RES_ID[i] : SEEKBAR_UNMUTED_RES_ID[i]);
            }
        }
        if (this.mSeekBars[i] == null || (streamVolume = this.mAudioManager.getStreamVolume(i2)) == this.mSeekBarVolumizer[i].getVolume(this.mSeekBars[i].getProgress())) {
            return;
        }
        this.mSeekBars[i].setProgress(this.mSeekBarVolumizer[i].getProgress(streamVolume));
    }

    @Override // com.android.settings.BaseFragment
    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.preference_dialog_ringervolume_miui, viewGroup, false);
    }

    void doRestoreAllVolumes() {
        for (int i = 0; i < SECTION_ID.length; i++) {
            SeekBarVolumizer[] seekBarVolumizerArr = this.mSeekBarVolumizer;
            if (seekBarVolumizerArr[i] != null) {
                seekBarVolumizerArr[i].stopSample();
                this.mSeekBarVolumizer[i].revertStreamVolume();
            }
        }
        updateSlidersAndMutedStates();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, R.string.volume_restore);
        add.setIcon(R.drawable.action_button_reset_volume);
        add.setShowAsAction(5);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        cleanup();
        super.onDestroy();
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return i == 24 || i == 25 || i == 164;
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return super.onOptionsItemSelected(menuItem);
        }
        restoreAllVolumes();
        return true;
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        for (SeekBarVolumizer seekBarVolumizer : this.mSeekBarVolumizer) {
            seekBarVolumizer.stopSample();
        }
        super.onPause();
    }

    protected void onSampleStarting(SeekBarVolumizer seekBarVolumizer) {
        for (SeekBarVolumizer seekBarVolumizer2 : this.mSeekBarVolumizer) {
            if (seekBarVolumizer2 != null && seekBarVolumizer2 != seekBarVolumizer) {
                seekBarVolumizer2.stopSample();
            }
        }
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        int i;
        super.onViewCreated(view, bundle);
        this.mSeekBarVolumizer = new SeekBarVolumizer[SECTION_ID.length];
        this.mAudioManager = (AudioManager) getActivity().getSystemService("audio");
        int i2 = 0;
        while (true) {
            int[] iArr = SECTION_ID;
            if (i2 >= iArr.length) {
                break;
            }
            View findViewById = view.findViewById(iArr[i2]);
            miuix.androidbasewidget.widget.SeekBar seekBar = (miuix.androidbasewidget.widget.SeekBar) findViewById.findViewById(R.id.volume_seekbar);
            this.mSeekBars[i2] = seekBar;
            int[] iArr2 = SEEKBAR_TYPE;
            if (iArr2[i2] == 3) {
                this.mSeekBarVolumizer[i2] = new SeekBarVolumizer(getActivity(), seekBar, iArr2[i2], getMediaVolumeUri());
            } else {
                this.mSeekBarVolumizer[i2] = new SeekBarVolumizer(this, getActivity(), seekBar, iArr2[i2]);
            }
            this.mCheckBoxes[i2] = (ImageView) findViewById.findViewById(R.id.mute_button);
            ((TextView) findViewById.findViewById(R.id.description_text)).setText(this.DESCPTION_ID[i2]);
            i2++;
        }
        updateSlidersAndMutedStates();
        if (this.mRingModeChangedReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.media.RINGER_MODE_CHANGED");
            this.mRingModeChangedReceiver = new BroadcastReceiver() { // from class: com.android.settings.sound.RingerVolumeFragment.2
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if ("android.media.RINGER_MODE_CHANGED".equals(intent.getAction())) {
                        RingerVolumeFragment.this.mHandler.sendMessage(RingerVolumeFragment.this.mHandler.obtainMessage(101, intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1), 0));
                    }
                }
            };
            getActivity().registerReceiver(this.mRingModeChangedReceiver, intentFilter);
        }
        if (this.mVolumeChangedReceiver == null) {
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.media.VOLUME_CHANGED_ACTION");
            this.mVolumeChangedReceiver = new BroadcastReceiver() { // from class: com.android.settings.sound.RingerVolumeFragment.3
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    int intExtra;
                    if (!"android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction()) || (intExtra = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1)) < 0) {
                        return;
                    }
                    RingerVolumeFragment.this.mHandler.sendMessage(RingerVolumeFragment.this.mHandler.obtainMessage(102, intExtra, intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0)));
                }
            };
            getActivity().registerReceiver(this.mVolumeChangedReceiver, intentFilter2);
        }
        if (Utils.isVoiceCapable(getActivity())) {
            i = R.id.notification_section;
        } else {
            i = R.id.ringer_section;
            view.findViewById(R.id.voice_section).setVisibility(8);
            view.findViewById(R.id.bluetooth_section).setVisibility(8);
            view.findViewById(R.id.alarm_section).setVisibility(8);
        }
        view.findViewById(i).setVisibility(8);
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    void restoreAllVolumes() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.volume_restore).setCancelable(true).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.sound.RingerVolumeFragment.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                RingerVolumeFragment.this.doRestoreAllVolumes();
            }
        }).setMessage(R.string.volume_restore_alert).show();
    }
}
