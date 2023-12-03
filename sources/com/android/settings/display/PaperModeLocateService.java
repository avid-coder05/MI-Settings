package com.android.settings.display;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.settings.display.PaperModeSunTimeHelper;

/* loaded from: classes.dex */
public class PaperModeLocateService extends JobService {
    private Handler mHandler;
    private JobParameters mJobParameters;
    private Location mLocation;
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class LocationListener implements android.location.LocationListener {
        private LocationListener() {
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            StringBuilder sb = new StringBuilder();
            sb.append("requestLocationUpdates returns, location is null: ");
            sb.append(location == null);
            Log.d("PaperModeLocateService", sb.toString());
            if (location != null) {
                PaperModeLocateService.this.mLocation = location;
                PaperModeLocateService paperModeLocateService = PaperModeLocateService.this;
                paperModeLocateService.broadcastLocationChanged(paperModeLocateService.mLocation);
                PaperModeLocateService.this.removeLocationUpdates();
                PaperModeLocateService paperModeLocateService2 = PaperModeLocateService.this;
                paperModeLocateService2.jobFinished(paperModeLocateService2.mJobParameters, false);
            }
        }

        @Override // android.location.LocationListener
        public void onProviderDisabled(String str) {
        }

        @Override // android.location.LocationListener
        public void onProviderEnabled(String str) {
        }

        @Override // android.location.LocationListener
        public void onStatusChanged(String str, int i, Bundle bundle) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastLocationChanged(Location location) {
        broadcastSunTime(PaperModeSunTimeHelper.calculateTwilightTime(location));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastSunTime(PaperModeSunTimeHelper.SunTime sunTime) {
        PaperModeSunTimeHelper.broadcastSunTime(getApplicationContext(), sunTime);
    }

    private void obtainTwilightTime() {
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.display.PaperModeLocateService.1
            @Override // java.lang.Runnable
            public void run() {
                final PaperModeSunTimeHelper.SunTime sunTwilightTime = PaperModeSunTimeHelper.getSunTwilightTime(PaperModeLocateService.this.getApplicationContext());
                PaperModeLocateService.this.mHandler.post(new Runnable() { // from class: com.android.settings.display.PaperModeLocateService.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        PaperModeSunTimeHelper.SunTime sunTime = sunTwilightTime;
                        if (sunTime == null) {
                            PaperModeLocateService.this.requestLocation();
                            return;
                        }
                        PaperModeLocateService.this.broadcastSunTime(sunTime);
                        PaperModeLocateService paperModeLocateService = PaperModeLocateService.this;
                        paperModeLocateService.jobFinished(paperModeLocateService.mJobParameters, false);
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeLocationUpdates() {
        LocationListener locationListener = this.mLocationListener;
        if (locationListener != null) {
            this.mLocationManager.removeUpdates(locationListener);
            this.mLocationListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestLocation() {
        if (this.mLocationManager.getProvider("network") != null) {
            try {
                Log.d("PaperModeLocateService", "requestLocationUpdates");
                this.mLocationManager.requestLocationUpdates(LocationRequest.createFromDeprecatedProvider("network", 1000L, 0.0f, true), this.mLocationListener, (Looper) null);
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.display.PaperModeLocateService.2
                    @Override // java.lang.Runnable
                    public void run() {
                        PaperModeLocateService.this.removeLocationUpdates();
                    }
                }, 300000L);
            } catch (IllegalArgumentException e) {
                Log.e("PaperModeLocateService", "register location listener error: " + e);
            }
            Location lastKnownLocation = this.mLocationManager.getLastKnownLocation("network");
            this.mLocation = lastKnownLocation;
            if (lastKnownLocation != null) {
                broadcastLocationChanged(lastKnownLocation);
            }
        }
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("PaperModeLocateService", "PaperModeLocateService start");
        this.mJobParameters = jobParameters;
        this.mLocationManager = (LocationManager) getSystemService("location");
        this.mLocationListener = new LocationListener();
        this.mHandler = new Handler(Looper.getMainLooper());
        obtainTwilightTime();
        return false;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
