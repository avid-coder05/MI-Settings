package com.android.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class BootloaderApplyActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mAcceptButton;
    private CharSequence mAppLabel;
    private Button mRejectButton;
    private TextView mWarningInfoView;
    private int mCurrentStep = 1;
    private int mAutoNextStepTime = 5;
    private Handler mHandler = new Handler() { // from class: com.android.settings.BootloaderApplyActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            BootloaderApplyActivity.access$006(BootloaderApplyActivity.this);
            if (BootloaderApplyActivity.this.mCurrentStep == 5 && BootloaderApplyActivity.this.mAutoNextStepTime == 0) {
                BootloaderApplyActivity.this.mAcceptButton.setText(R.string.button_text_accept);
                BootloaderApplyActivity.this.mAcceptButton.setEnabled(true);
            } else if (BootloaderApplyActivity.this.mAutoNextStepTime == 0) {
                BootloaderApplyActivity.this.mAcceptButton.setText(R.string.button_text_next_step);
                BootloaderApplyActivity.this.mAcceptButton.setEnabled(true);
            } else {
                if (BootloaderApplyActivity.this.mCurrentStep == 5) {
                    Button button = BootloaderApplyActivity.this.mAcceptButton;
                    BootloaderApplyActivity bootloaderApplyActivity = BootloaderApplyActivity.this;
                    button.setText(bootloaderApplyActivity.getString(R.string.button_text_accept_timer, new Object[]{Integer.valueOf(bootloaderApplyActivity.mAutoNextStepTime)}));
                } else {
                    Button button2 = BootloaderApplyActivity.this.mAcceptButton;
                    BootloaderApplyActivity bootloaderApplyActivity2 = BootloaderApplyActivity.this;
                    button2.setText(bootloaderApplyActivity2.getString(R.string.button_text_next_step_timer, new Object[]{Integer.valueOf(bootloaderApplyActivity2.mAutoNextStepTime)}));
                }
                BootloaderApplyActivity.this.mHandler.removeMessages(100);
                BootloaderApplyActivity.this.mHandler.sendEmptyMessageDelayed(100, 1000L);
            }
        }
    };

    private void acceptApply() {
        setEnabled(true);
    }

    static /* synthetic */ int access$006(BootloaderApplyActivity bootloaderApplyActivity) {
        int i = bootloaderApplyActivity.mAutoNextStepTime - 1;
        bootloaderApplyActivity.mAutoNextStepTime = i;
        return i;
    }

    private String getWarningInfo(int i, CharSequence charSequence) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i != 5) {
                            return null;
                        }
                        return getString(R.string.bootloader_apply_step_5);
                    }
                    return getString(R.string.bootloader_apply_step_4);
                }
                return getString(R.string.bootloader_apply_step_3);
            }
            return getString(R.string.bootloader_apply_step_2);
        }
        return getString(R.string.bootloader_apply_step_1);
    }

    private void rejectApply() {
        setEnabled(false);
    }

    public static void setEnabled(boolean z) {
        SystemProperties.set("persist.fastboot.enable", z ? "1" : "0");
    }

    @Override // miuix.appcompat.app.AppCompatActivity, android.app.Activity
    public void finish() {
        setResult(-1, null);
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.reject) {
            this.mHandler.removeMessages(100);
            rejectApply();
            finish();
        } else if (id == R.id.accept) {
            int i = this.mCurrentStep;
            if (i == 5) {
                this.mHandler.removeMessages(100);
                acceptApply();
                finish();
                return;
            }
            int i2 = i + 1;
            this.mCurrentStep = i2;
            this.mAutoNextStepTime = 5;
            this.mWarningInfoView.setText(getWarningInfo(i2, this.mAppLabel));
            if (this.mCurrentStep == 5) {
                this.mAcceptButton.setText(getString(R.string.button_text_accept_timer, new Object[]{Integer.valueOf(this.mAutoNextStepTime)}));
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
        setContentView(R.layout.bootloader_apply);
        this.mWarningInfoView = (TextView) findViewById(R.id.warning_info);
        Button button = (Button) findViewById(R.id.reject);
        this.mRejectButton = button;
        button.setOnClickListener(this);
        Button button2 = (Button) findViewById(R.id.accept);
        this.mAcceptButton = button2;
        button2.setOnClickListener(this);
        this.mWarningInfoView.setText(getWarningInfo(this.mCurrentStep, this.mAppLabel));
        this.mAcceptButton.setText(getString(R.string.button_text_next_step_timer, new Object[]{Integer.valueOf(this.mAutoNextStepTime)}));
        this.mAcceptButton.setEnabled(false);
        this.mHandler.sendEmptyMessageDelayed(100, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        this.mHandler.removeMessages(100);
        super.onDestroy();
    }
}
