package com.android.settings.usagestats;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.usagestats.controller.AppUsageController;
import com.android.settings.usagestats.model.AppUsageStats;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settings.usagestats.utils.AppUsageStatsFactory;
import com.android.settings.usagestats.utils.DateUtils;
import java.util.ArrayList;
import java.util.Iterator;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;
import miui.process.ProcessManager;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class TimeoverActivity extends AppCompatActivity {
    private static String TAG = "LR-TimeOverActivity";
    private boolean isTimeOver;
    private long mEnterTime;
    private String mForegroundPkg;
    private String pkgName;
    private ImageView vAppIcon;
    private TextView vAppName;
    private View vGot;
    private View vSetLimitTime;
    private TextView vUseTime;
    private boolean isProlong = false;
    private IForegroundInfoListener.Stub mAppObserver = new IForegroundInfoListener.Stub() { // from class: com.android.settings.usagestats.TimeoverActivity.1
        public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) {
            Log.d(TimeoverActivity.TAG, "onForegroundInfoChanged: " + foregroundInfo.mForegroundPackageName);
            TimeoverActivity.this.mForegroundPkg = foregroundInfo.mForegroundPackageName;
        }
    };

    private void findView() {
        this.vAppIcon = (ImageView) findViewById(R.id.iv_app_icon);
        this.vAppName = (TextView) findViewById(R.id.tv_app_name);
        this.vUseTime = (TextView) findViewById(R.id.tv_limit_title);
        this.vSetLimitTime = findViewById(R.id.tv_set_time);
        this.vGot = findViewById(R.id.tv_get_it);
    }

    private void fitCutOut() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.layoutInDisplayCutoutMode = 1;
            window.setAttributes(attributes);
        }
    }

    private void initData() {
        this.vAppIcon.setImageDrawable(AppInfoUtils.getAppLaunchIcon(getApplicationContext(), this.pkgName));
        this.vAppName.setText(AppInfoUtils.getAppName(getApplicationContext(), this.pkgName));
        final ArrayList arrayList = (ArrayList) AppUsageStatsFactory.loadUsageToday(getApplicationContext(), this.pkgName);
        Iterator it = arrayList.iterator();
        final long j = 0;
        while (it.hasNext()) {
            j += ((AppUsageStats) it.next()).getTotalForegroundTime();
        }
        this.vUseTime.setText(getString(R.string.usage_app_over_content, new Object[]{AppInfoUtils.formatTime(getApplicationContext(), j)}));
        this.vSetLimitTime.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.TimeoverActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isWeek", false);
                bundle.putString("packageName", TimeoverActivity.this.pkgName);
                bundle.putSerializable("usageList", arrayList);
                bundle.putLong("usageTime", j);
                bundle.putBoolean("hasTime", true);
                bundle.putBoolean("fromNotification", true);
                if (TimeoverActivity.this.isTimeOver) {
                    bundle.putString("fromPager", TimeoverActivity.class.getSimpleName());
                }
                Intent intent = new Intent(TimeoverActivity.this, UsageAppDetailActivity.class);
                intent.putExtras(bundle);
                TimeoverActivity.this.startActivity(intent);
                Pair<Integer, Integer> systemDefaultEnterAnim = MiuiUtils.getSystemDefaultEnterAnim(TimeoverActivity.this);
                TimeoverActivity.this.overridePendingTransition(((Integer) systemDefaultEnterAnim.first).intValue(), ((Integer) systemDefaultEnterAnim.second).intValue());
                TimeoverActivity.this.isProlong = true;
                TimeoverActivity.this.finish();
            }
        });
        this.vGot.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.TimeoverActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (DateUtils.isInSameDay(TimeoverActivity.this.mEnterTime, System.currentTimeMillis())) {
                    AppUsageController.suspendApp(TimeoverActivity.this.getApplicationContext(), TimeoverActivity.this.pkgName, true);
                }
                TimeoverActivity.this.finish();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        ProcessManager.registerForegroundInfoListener(this.mAppObserver);
        super.onCreate(bundle);
        fitCutOut();
        this.mEnterTime = System.currentTimeMillis();
        Intent intent = getIntent();
        this.pkgName = intent.getStringExtra("pkgName");
        this.isTimeOver = intent.getBooleanExtra("theEnd", false);
        if (TextUtils.isEmpty(this.pkgName)) {
            finish();
            return;
        }
        Log.d(TAG, "onCreate: " + isInMultiWindowMode());
        if (getResources().getConfiguration().orientation == 2) {
            setContentView(R.layout.usagestats_app_time_over_land);
        } else {
            setContentView(R.layout.usagestats_app_time_over);
        }
        getWindow().getDecorView().setBackgroundColor(getColor(R.color.usage_stats_remind_time_over_bg));
        findView();
        initData();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        ProcessManager.unregisterForegroundInfoListener(this.mAppObserver);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
        if (intent == null || !intent.hasExtra("pkgName")) {
            return;
        }
        this.pkgName = intent.getStringExtra("pkgName");
        this.isTimeOver = intent.getBooleanExtra("theEnd", false);
        initData();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ==stop==");
        if ("com.miui.home".equals(this.mForegroundPkg) || TextUtils.equals(this.mForegroundPkg, this.pkgName)) {
            Log.d(TAG, "onStop: home");
            if (!this.isTimeOver || this.isProlong) {
                return;
            }
            this.vGot.performClick();
        }
    }
}
