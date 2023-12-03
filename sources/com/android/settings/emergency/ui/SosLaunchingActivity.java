package com.android.settings.emergency.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Keep;
import com.android.settings.R;
import com.android.settings.emergency.service.LocationService;
import com.android.settings.emergency.ui.view.CircleProgressBar;
import com.android.settings.emergency.util.CommonUtils;
import com.android.settings.emergency.util.NotchAdapterUtils;
import com.android.settingslib.util.MiStatInterfaceUtils;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;
import src.com.android.settings.emergency.util.ThreadPool;

/* loaded from: classes.dex */
public class SosLaunchingActivity extends AppCompatActivity implements View.OnClickListener {
    private ValueAnimator animator;
    private AnimatorListenerAdapter mAnimatorListener = new AnimatorListenerAdapter() { // from class: com.android.settings.emergency.ui.SosLaunchingActivity.5
        private boolean isCancelled;

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
            this.isCancelled = true;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            if (this.isCancelled) {
                return;
            }
            Intent intent = new Intent("action_enter_sos_mode");
            intent.setClass(SosLaunchingActivity.this, LocationService.class);
            SosLaunchingActivity.this.startService(intent);
            Intent intent2 = new Intent();
            intent2.setClass(SosLaunchingActivity.this, SosExitAlertActivity.class);
            SosLaunchingActivity.this.startActivity(intent2);
            SosLaunchingActivity.this.finish();
        }
    };
    private View mCancel;
    private CircleProgressBar mProgressBar;
    private TextView mTimeTextView;

    private void startAnimation() {
        ValueAnimator ofInt = ValueAnimator.ofInt(0, 10);
        this.animator = ofInt;
        ofInt.setDuration(5000L);
        this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.emergency.ui.SosLaunchingActivity.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                SosLaunchingActivity.this.mProgressBar.setProgress((float) (valueAnimator.getCurrentPlayTime() / 50));
                SosLaunchingActivity.this.mTimeTextView.setText(SosLaunchingActivity.this.getString(R.string.miui_sos_launching_summary, new Object[]{Integer.valueOf((int) (5 - (valueAnimator.getCurrentPlayTime() / 1000)))}));
            }
        });
        this.animator.addListener(this.mAnimatorListener);
        this.animator.start();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.animator == null || view.getId() != R.id.sos_cancel) {
            return;
        }
        this.animator.cancel();
        MiStatInterfaceUtils.trackEvent("enter_sos_click_cancel");
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        NotchAdapterUtils.fitNotchForFullScreen(this);
        getWindow().addFlags(524288);
        if (CommonUtils.getCurrentEnableSubInfo() == null) {
            new AlertDialog.Builder(this).setTitle(R.string.miui_sos_launch_error).setMessage(R.string.miui_sos_launch_error_message).setPositiveButton(R.string.miui_sos_launch_error_confirm, (DialogInterface.OnClickListener) null).setCancelable(false).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.emergency.ui.SosLaunchingActivity.1
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    SosLaunchingActivity.this.finish();
                }
            }).create().show();
        } else {
            Vibrator vibrator = (Vibrator) getSystemService("vibrator");
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(500L);
            }
            setContentView(R.layout.activity_sos_launching);
            CircleProgressBar circleProgressBar = (CircleProgressBar) findViewById(R.id.sos_progressbar);
            this.mProgressBar = circleProgressBar;
            circleProgressBar.setMax(100);
            this.mTimeTextView = (TextView) findViewById(R.id.counting_down);
            View findViewById = findViewById(R.id.sos_cancel);
            this.mCancel = findViewById;
            findViewById.setOnClickListener(this);
            startAnimation();
            ThreadPool.execute(new Runnable() { // from class: com.android.settings.emergency.ui.SosLaunchingActivity.2
                @Override // java.lang.Runnable
                public void run() {
                    SosLaunchingActivity.this.getCacheDir();
                }
            });
        }
        new Handler().postDelayed(new Runnable() { // from class: com.android.settings.emergency.ui.SosLaunchingActivity.3
            @Override // java.lang.Runnable
            public void run() {
                PowerManager.WakeLock newWakeLock = ((PowerManager) SosLaunchingActivity.this.getSystemService("power")).newWakeLock(805306378, "bright");
                newWakeLock.acquire();
                newWakeLock.release();
            }
        }, 2000L);
        MiStatInterfaceUtils.trackEvent("enter_sos");
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            getWindow().getDecorView().setSystemUiVisibility(5894);
        }
    }

    @Keep
    public void setTime(int i) {
        this.mTimeTextView.setText(getString(R.string.miui_sos_launching_summary, new Object[]{Integer.valueOf(i)}));
    }
}
