package com.android.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiMasterClearApplyActivity extends AppCompatActivity implements View.OnClickListener {
    private static int[] STEP_RES;
    private Button mAcceptButton;
    private int mAutoNextStepTime;
    private int mCurrentStep;
    private Handler mHandler = new Handler() { // from class: com.android.settings.MiuiMasterClearApplyActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            MiuiMasterClearApplyActivity.access$006(MiuiMasterClearApplyActivity.this);
            if (MiuiMasterClearApplyActivity.this.mCurrentStep == 2 && MiuiMasterClearApplyActivity.this.mAutoNextStepTime == 0) {
                MiuiMasterClearApplyActivity.this.mAcceptButton.setText(R.string.button_text_ok);
                MiuiMasterClearApplyActivity.this.mAcceptButton.setEnabled(true);
            } else if (MiuiMasterClearApplyActivity.this.mAutoNextStepTime == 0) {
                MiuiMasterClearApplyActivity.this.mAcceptButton.setText(R.string.button_text_next_step);
                MiuiMasterClearApplyActivity.this.mAcceptButton.setEnabled(true);
            } else {
                if (MiuiMasterClearApplyActivity.this.mCurrentStep == 2) {
                    Button button = MiuiMasterClearApplyActivity.this.mAcceptButton;
                    MiuiMasterClearApplyActivity miuiMasterClearApplyActivity = MiuiMasterClearApplyActivity.this;
                    button.setText(miuiMasterClearApplyActivity.getString(R.string.button_text_ok_timer, new Object[]{Integer.valueOf(miuiMasterClearApplyActivity.mAutoNextStepTime)}));
                } else {
                    Button button2 = MiuiMasterClearApplyActivity.this.mAcceptButton;
                    MiuiMasterClearApplyActivity miuiMasterClearApplyActivity2 = MiuiMasterClearApplyActivity.this;
                    button2.setText(miuiMasterClearApplyActivity2.getString(R.string.button_text_next_step_timer, new Object[]{Integer.valueOf(miuiMasterClearApplyActivity2.mAutoNextStepTime)}));
                }
                MiuiMasterClearApplyActivity.this.mHandler.removeMessages(100);
                MiuiMasterClearApplyActivity.this.mHandler.sendEmptyMessageDelayed(100, 1000L);
            }
        }
    };
    private int mNextStepTime;
    private Button mRejectButton;
    private TextView mWarningInfoView;

    static {
        STEP_RES = r0;
        int[] iArr = {0, R.string.master_clear_apply_step_1, R.string.master_clear_apply_step_2};
    }

    static /* synthetic */ int access$006(MiuiMasterClearApplyActivity miuiMasterClearApplyActivity) {
        int i = miuiMasterClearApplyActivity.mAutoNextStepTime - 1;
        miuiMasterClearApplyActivity.mAutoNextStepTime = i;
        return i;
    }

    private CharSequence getWarningInfo(int i) {
        return getText(STEP_RES[i]);
    }

    private void resetNextStepTime() {
        this.mAutoNextStepTime = this.mNextStepTime;
    }

    @Override // miuix.appcompat.app.AppCompatActivity, android.app.Activity
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cancel) {
            this.mHandler.removeMessages(100);
            setResult(0);
            finish();
        } else if (id == R.id.ok) {
            int i = this.mCurrentStep;
            if (i == 2) {
                this.mHandler.removeMessages(100);
                setResult(-1);
                finish();
                return;
            }
            this.mCurrentStep = i + 1;
            resetNextStepTime();
            this.mWarningInfoView.setText(getWarningInfo(this.mCurrentStep));
            if (this.mCurrentStep == 2) {
                this.mAcceptButton.setText(getString(R.string.button_text_ok_timer, new Object[]{Integer.valueOf(this.mAutoNextStepTime)}));
            } else {
                this.mAcceptButton.setText(getString(R.string.button_text_next_step_timer, new Object[]{Integer.valueOf(this.mAutoNextStepTime)}));
            }
            this.mAcceptButton.setEnabled(false);
            this.mHandler.removeMessages(100);
            this.mHandler.sendEmptyMessageDelayed(100, 1000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP, MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        boolean booleanExtra = getIntent().getBooleanExtra("format_internal_storage", true);
        this.mNextStepTime = getIntent().getBooleanExtra("1217", false) ? 3 : 10;
        this.mCurrentStep = booleanExtra ? 1 : 2;
        setContentView(R.layout.master_clear_apply);
        resetNextStepTime();
        this.mWarningInfoView = (TextView) findViewById(R.id.warning_info);
        Button button = (Button) findViewById(R.id.cancel);
        this.mRejectButton = button;
        button.setOnClickListener(this);
        Button button2 = (Button) findViewById(R.id.ok);
        this.mAcceptButton = button2;
        button2.setOnClickListener(this);
        this.mWarningInfoView.setText(getWarningInfo(this.mCurrentStep));
        this.mAcceptButton.setText(getString(booleanExtra ? R.string.button_text_next_step_timer : R.string.button_text_ok_timer, new Object[]{Integer.valueOf(this.mAutoNextStepTime)}));
        this.mAcceptButton.setEnabled(false);
        this.mHandler.sendEmptyMessageDelayed(100, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        this.mHandler.removeMessages(100);
    }
}
