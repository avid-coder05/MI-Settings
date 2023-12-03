package com.android.settings.operator.kddi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.android.settings.R;
import miuix.androidbasewidget.widget.ProgressBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class MiuiMobileDataUsedActivity extends AppCompatActivity {
    private TextView btnSkip;
    private int count = 0;
    private Intent data = new Intent();
    private Handler mHandler = new Handler() { // from class: com.android.settings.operator.kddi.MiuiMobileDataUsedActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            if (MiuiMobileDataUsedActivity.this.count != 10) {
                MiuiMobileDataUsedActivity.access$008(MiuiMobileDataUsedActivity.this);
                MiuiMobileDataUsedActivity.this.showCheckMobileDataDialog();
                return;
            }
            MiuiMobileDataUsedActivity.this.count = 0;
            MiuiMobileDataUsedActivity miuiMobileDataUsedActivity = MiuiMobileDataUsedActivity.this;
            miuiMobileDataUsedActivity.showDataConnectionDialog(miuiMobileDataUsedActivity);
            MiuiMobileDataUsedActivity.this.mProgressBar.setVisibility(8);
        }
    };
    private ProgressBar mProgressBar;

    static /* synthetic */ int access$008(MiuiMobileDataUsedActivity miuiMobileDataUsedActivity) {
        int i = miuiMobileDataUsedActivity.count;
        miuiMobileDataUsedActivity.count = i + 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkMobileDataDialog() {
        this.mProgressBar.setVisibility(0);
        showCheckMobileDataDialog();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCheckMobileDataDialog() {
        if (this.mProgressBar != null) {
            if (TelephonyManager.getDefault().getDataState() != 2) {
                this.mHandler.sendEmptyMessageDelayed(1, 300L);
                return;
            }
            this.mProgressBar.setVisibility(8);
            this.data.putExtra("next", true);
            setResult(-1, this.data);
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDataConnectionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_check_title);
        builder.setMessage(R.string.dialog_check_context);
        builder.setPositiveButton(R.string.dialog_check_button_positive, new DialogInterface.OnClickListener() { // from class: com.android.settings.operator.kddi.MiuiMobileDataUsedActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                MiuiMobileDataUsedActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_check_button_negative, new DialogInterface.OnClickListener() { // from class: com.android.settings.operator.kddi.MiuiMobileDataUsedActivity.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiMobileDataUsedActivity.this.checkMobileDataDialog();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_miui_mobile_data);
        this.btnSkip = (TextView) findViewById(R.id.btn_skip);
        this.mProgressBar = (ProgressBar) findViewById(R.id.wps_progress_bar);
        this.btnSkip.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.operator.kddi.MiuiMobileDataUsedActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MiuiMobileDataUsedActivity.this.mProgressBar.setVisibility(8);
                MiuiMobileDataUsedActivity.this.finish();
            }
        });
        checkMobileDataDialog();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4 && keyEvent.getRepeatCount() == 0) {
            finish();
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        this.mHandler.removeMessages(1);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
    }
}
