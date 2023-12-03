package com.android.settings.speaker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import java.io.IOException;
import java.lang.ref.WeakReference;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class SpeakerSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = SpeakerSettings.class.getSimpleName();
    AudioManager am;
    private MainThreadHandler mMainThreadHandler;
    private MediaPlayer mMediaPlayer;
    private CheckBoxPreference speakerCheckBox;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class MainThreadHandler extends Handler {
        private WeakReference<Fragment> mFragmentRef;

        private MainThreadHandler(Looper looper, Fragment fragment) {
            super(looper);
            this.mFragmentRef = new WeakReference<>(fragment);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void stopCleanSoundDelayed(long j) {
            sendEmptyMessageDelayed(1, j);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            SpeakerSettings speakerSettings = (SpeakerSettings) this.mFragmentRef.get();
            if (speakerSettings == null || message.what != 1 || speakerSettings.getActivity() == null) {
                return;
            }
            speakerSettings.stopCleanSound();
        }
    }

    private void playCleanSound() {
        AssetFileDescriptor openRawResourceFd = getResources().openRawResourceFd(R.raw.speaker_clean_sound);
        if (openRawResourceFd == null) {
            throw new RuntimeException("afd == NULL,  the file exists but is compressed. ");
        }
        this.mMediaPlayer = new MediaPlayer();
        getActivity().setVolumeControlStream(3);
        this.mMediaPlayer.setAudioStreamType(3);
        this.mMediaPlayer.setLooping(true);
        this.mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.android.settings.speaker.SpeakerSettings.1
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                Log.e(SpeakerSettings.TAG, "Error on playing speaker clean sound. ");
                return false;
            }
        });
        try {
            this.mMediaPlayer.setDataSource(openRawResourceFd.getFileDescriptor(), openRawResourceFd.getStartOffset(), openRawResourceFd.getLength());
            openRawResourceFd.close();
            this.mMediaPlayer.setVolume(1.0f, 1.0f);
            this.mMediaPlayer.prepare();
            this.mMediaPlayer.start();
            this.mMainThreadHandler.stopCleanSoundDelayed(30000L);
        } catch (IOException e) {
            Log.e(TAG, "play speaker clean sound failed!", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopCleanSound() {
        this.speakerCheckBox.setEnabled(true);
        this.speakerCheckBox.setChecked(false);
        removeDialog(0);
        stopPlayer();
    }

    private void stopPlayer() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            this.mMediaPlayer = null;
            return;
        }
        synchronized (this) {
            MediaPlayer mediaPlayer2 = this.mMediaPlayer;
            if (mediaPlayer2 != null) {
                try {
                    try {
                        mediaPlayer2.stop();
                        this.mMediaPlayer.release();
                    } catch (IllegalStateException unused) {
                        Log.e(TAG, "IllegalStateException while stop clean sound!");
                        this.mMediaPlayer.release();
                    }
                    this.mMediaPlayer = null;
                } catch (Throwable th) {
                    this.mMediaPlayer.release();
                    this.mMediaPlayer = null;
                    throw th;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return TAG;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.speaker_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("key_speaker_auto_clean");
        this.speakerCheckBox = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        this.am = (AudioManager) getSystemService("audio");
        this.mMainThreadHandler = new MainThreadHandler(Looper.getMainLooper(), this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 0) {
            AlertDialog create = new AlertDialog.Builder(getActivity()).setTitle(R.string.dialog_speaker_auto_clean_title).setMessage(R.string.dialog_speaker_auto_clean_content).setPositiveButton(R.string.dialog_speaker_auto_clean_button, (DialogInterface.OnClickListener) null).create();
            create.setCanceledOnTouchOutside(false);
            return create;
        }
        throw new IllegalArgumentException();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("key_speaker_auto_clean".equals(preference.getKey())) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            AudioManager audioManager = this.am;
            StringBuilder sb = new StringBuilder();
            sb.append("status_earpiece_clean=");
            sb.append(booleanValue ? "on" : "off");
            audioManager.setParameters(sb.toString());
            if (booleanValue) {
                this.speakerCheckBox.setEnabled(false);
                showDialog(0);
                playCleanSound();
                return true;
            }
            return true;
        }
        return true;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ActionBar appCompatActionBar = getAppCompatActionBar();
        if (appCompatActionBar == null || TextUtils.isEmpty(getPreferenceScreen().getTitle())) {
            return;
        }
        appCompatActionBar.setTitle(getPreferenceScreen().getTitle());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        stopPlayer();
    }
}
