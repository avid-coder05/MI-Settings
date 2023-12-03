package src.com.android.settings.emergency.util;

import android.annotation.SuppressLint;
import android.media.AudioRecord;
import android.util.Log;
import com.android.settings.emergency.service.LocationService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

@SuppressLint({"MissingPermission"})
/* loaded from: classes5.dex */
public class AudioRecordManager {
    private static final Object mObject = new Object();
    private String mFolderName;
    private LocationService.ISosVoiceListener mVoiceListener;
    private AudioRecord mAudioRecord = null;
    private int mRecordBufsize = 0;
    private boolean mIsRecording = false;

    /* loaded from: classes5.dex */
    public static class ConvertToMp3AndSendRunnable implements Runnable {
        private WeakReference<AudioRecordManager> audioRecordManagerWeakReference;

        /* renamed from: src  reason: collision with root package name */
        private String f2src;
        private String target;

        public ConvertToMp3AndSendRunnable(AudioRecordManager audioRecordManager, String str, String str2) {
            this.audioRecordManagerWeakReference = new WeakReference<>(audioRecordManager);
            this.f2src = str;
            this.target = str2;
        }

        /* JADX WARN: Removed duplicated region for block: B:78:0x00e5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:86:0x00f5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 296
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: src.com.android.settings.emergency.util.AudioRecordManager.ConvertToMp3AndSendRunnable.run():void");
        }
    }

    public AudioRecordManager(LocationService.ISosVoiceListener iSosVoiceListener, String str) {
        this.mFolderName = str;
        this.mVoiceListener = iSosVoiceListener;
        createAudioRecord();
    }

    private void createAudioRecord() {
        this.mRecordBufsize = AudioRecord.getMinBufferSize(8000, 16, 2);
        this.mAudioRecord = new AudioRecord(1, 8000, 16, 2, this.mRecordBufsize);
    }

    public static void deleteFile(final String str) {
        ThreadPool.execute(new Runnable() { // from class: src.com.android.settings.emergency.util.AudioRecordManager.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (AudioRecordManager.mObject) {
                    File file = new File(str + "/sosvoice.pcm");
                    if (file.exists()) {
                        file.delete();
                    }
                    File file2 = new File(str + "/sosvoice.mp3");
                    if (file2.exists()) {
                        file2.delete();
                    }
                }
            }
        });
    }

    public void convertAudioFiles(String str, String str2) {
        ThreadPool.execute(new ConvertToMp3AndSendRunnable(this, str, str2));
    }

    public void startRecord() {
        if (this.mIsRecording) {
            return;
        }
        this.mIsRecording = true;
        this.mAudioRecord.startRecording();
        Log.i("SOS-AudioRecordManager", "startRecord");
        ThreadPool.execute(new Runnable() { // from class: src.com.android.settings.emergency.util.AudioRecordManager.1
            @Override // java.lang.Runnable
            public void run() {
                String str;
                String iOException;
                synchronized (AudioRecordManager.mObject) {
                    byte[] bArr = new byte[AudioRecordManager.this.mRecordBufsize];
                    File file = new File(AudioRecordManager.this.mFolderName + "/sosvoice.pcm");
                    FileOutputStream fileOutputStream = null;
                    try {
                        try {
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                            while (AudioRecordManager.this.mIsRecording) {
                                try {
                                    if (-3 != AudioRecordManager.this.mAudioRecord.read(bArr, 0, AudioRecordManager.this.mRecordBufsize)) {
                                        fileOutputStream2.write(bArr);
                                    }
                                } catch (Exception e) {
                                    e = e;
                                    fileOutputStream = fileOutputStream2;
                                    Log.e("SOS-AudioRecordManager", e.toString());
                                    if (fileOutputStream != null) {
                                        try {
                                            fileOutputStream.close();
                                        } catch (IOException e2) {
                                            str = "SOS-AudioRecordManager";
                                            iOException = e2.toString();
                                            Log.e(str, iOException);
                                        }
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    fileOutputStream = fileOutputStream2;
                                    if (fileOutputStream != null) {
                                        try {
                                            fileOutputStream.close();
                                        } catch (IOException e3) {
                                            Log.e("SOS-AudioRecordManager", e3.toString());
                                        }
                                    }
                                    throw th;
                                }
                            }
                            try {
                                fileOutputStream2.close();
                            } catch (IOException e4) {
                                str = "SOS-AudioRecordManager";
                                iOException = e4.toString();
                                Log.e(str, iOException);
                            }
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    } catch (Exception e5) {
                        e = e5;
                    }
                }
            }
        });
    }

    public void stopRecordAndSend() {
        this.mIsRecording = false;
        AudioRecord audioRecord = this.mAudioRecord;
        if (audioRecord != null) {
            audioRecord.stop();
            Log.i("SOS-AudioRecordManager", "stop record");
            convertAudioFiles(this.mFolderName + "/sosvoice.pcm", this.mFolderName + "/sosvoice.mp3");
            this.mAudioRecord.release();
            this.mAudioRecord = null;
        }
    }
}
