package com.android.settings.device;

import android.app.Activity;
import android.app.AppGlobals;
import android.content.Context;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.BaseSettingsController;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.report.InternationalCompat;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.utils.TabletUtils;
import java.lang.ref.WeakReference;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;

/* loaded from: classes.dex */
public class DeviceStatusController extends BaseSettingsController {
    private ImageView mArrowRight;
    private TextView mRightValue;
    private UpdateTask mUpdateTask;
    private View.OnClickListener mVersionClick;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class UpdateTask extends AsyncTask<Void, Void, String> {
        private WeakReference<DeviceStatusController> mOuterRef;

        public UpdateTask(DeviceStatusController deviceStatusController) {
            this.mOuterRef = new WeakReference<>(deviceStatusController);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(Void... voidArr) {
            return MiuiAboutPhoneUtils.getUpdateInfo(AppGlobals.getInitialApplication());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            DeviceStatusController deviceStatusController = this.mOuterRef.get();
            if (deviceStatusController != null) {
                deviceStatusController.updateResult(str);
            }
        }
    }

    public DeviceStatusController(Context context, TextView textView) {
        super(context, textView);
        this.mVersionClick = new View.OnClickListener() { // from class: com.android.settings.device.DeviceStatusController.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (((BaseSettingsController) DeviceStatusController.this).mContext == null || !(((BaseSettingsController) DeviceStatusController.this).mContext instanceof Activity)) {
                    return;
                }
                InternationalCompat.trackReportEvent("about_phone_click");
                MiuiAboutPhoneUtils.startUpdater((Activity) ((BaseSettingsController) DeviceStatusController.this).mContext);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateResult(String str) {
        String miuiVersionWithoutBuildType;
        if (this.mRightValue != null) {
            if (UserHandle.myUserId() != 0) {
                str = null;
            }
            ImageView imageView = this.mArrowRight;
            if (imageView != null) {
                imageView.setVisibility(TextUtils.isEmpty(str) ? 0 : 8);
                if (SettingsFeatures.isSplitTabletDevice()) {
                    this.mArrowRight.setVisibility(8);
                }
                this.mRightValue.setClickable(!TextUtils.isEmpty(str));
            }
            if (TabletUtils.IS_TABLET) {
                this.mRightValue.setVisibility(8);
                return;
            }
            if (TextUtils.isEmpty(str)) {
                miuiVersionWithoutBuildType = MiuiAboutPhoneUtils.getMiuiVersionWithoutBuildType(this.mContext, false);
                this.mRightValue.setBackground(null);
                this.mRightValue.setPadding(0, this.mContext.getResources().getDimensionPixelOffset(R.dimen.preference_value_padding_top), 0, this.mContext.getResources().getDimensionPixelOffset(R.dimen.preference_value_padding_bottom));
                this.mRightValue.setTextAppearance(R.style.Miuix_AppCompat_TextAppearance_PreferenceRight);
                if (MiuiUtils.isMiuiSdkSupportFolme()) {
                    Folme.clean(this.mRightValue);
                }
            } else {
                miuiVersionWithoutBuildType = this.mContext.getString(R.string.settings_new_version_btn);
                this.mRightValue.setBackgroundResource(R.drawable.new_version_button);
                this.mRightValue.setTextColor(this.mContext.getResources().getColor(R.color.new_version_text_color));
                this.mRightValue.setOnClickListener(this.mVersionClick);
                InternationalCompat.trackReportEvent("about_phone_pv");
                if (MiuiUtils.isMiuiSdkSupportFolme()) {
                    Folme.useAt(this.mRightValue).touch().handleTouchOf(this.mRightValue, new AnimConfig[0]);
                }
            }
            this.mRightValue.setText(miuiVersionWithoutBuildType);
            this.mRightValue.setVisibility(0);
            this.mRightValue.setGravity(8388629);
        }
    }

    @Override // com.android.settings.BaseSettingsController
    public void pause() {
        UpdateTask updateTask = this.mUpdateTask;
        if (updateTask != null) {
            updateTask.cancel(true);
        }
    }

    @Override // com.android.settings.BaseSettingsController
    public void resume() {
        updateState();
    }

    public void setUpTextView(TextView textView, ImageView imageView) {
        this.mRightValue = textView;
        this.mArrowRight = imageView;
        updateState();
    }

    public void updateState() {
        UpdateTask updateTask = this.mUpdateTask;
        if (updateTask != null && updateTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.mUpdateTask.cancel(true);
        }
        UpdateTask updateTask2 = new UpdateTask(this);
        this.mUpdateTask = updateTask2;
        updateTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.BaseSettingsController
    public void updateStatus() {
    }
}
