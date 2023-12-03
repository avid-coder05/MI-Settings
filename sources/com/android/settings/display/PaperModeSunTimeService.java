package com.android.settings.display;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.android.settings.display.PaperModeSunTimeHelper;

/* loaded from: classes.dex */
public class PaperModeSunTimeService extends Service {
    private ContentObserver mContentObserver;
    private HandlerThread mHT;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class WeatherCityChangeObserver extends ContentObserver {
        private Context mContext;

        public WeatherCityChangeObserver(Handler handler, Context context) {
            super(handler);
            this.mContext = context;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            Context context = this.mContext;
            if (context != null) {
                final PaperModeSunTimeHelper.SunTime sunTwilightTime = PaperModeSunTimeHelper.getSunTwilightTime(context);
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.android.settings.display.PaperModeSunTimeService.WeatherCityChangeObserver.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (sunTwilightTime != null) {
                            PaperModeSunTimeHelper.broadcastSunTime(WeatherCityChangeObserver.this.mContext, sunTwilightTime);
                        }
                    }
                });
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread("PaperMode city change...");
        this.mHT = handlerThread;
        handlerThread.start();
        registerObserver(true);
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        Log.e("PaperModeSunTimeService", "onDestroy: destroy...");
        registerObserver(false);
        HandlerThread handlerThread = this.mHT;
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.e("PaperModeSunTimeService", "onStartCommand: start...");
        return super.onStartCommand(intent, i, i2);
    }

    public void registerObserver(boolean z) {
        try {
            if (z) {
                this.mContentObserver = new WeatherCityChangeObserver(new Handler(this.mHT.getLooper()), getApplicationContext());
                getContentResolver().registerContentObserver(Uri.parse("content://weather/selected_city"), false, this.mContentObserver);
            } else {
                getContentResolver().unregisterContentObserver(this.mContentObserver);
            }
        } catch (Exception e) {
            Log.e("PaperModeSunTimeService", "registerObserver: ", e);
            if (z) {
                stopSelf();
            }
        }
    }
}
