package com.android.settings.display;

import android.app.Activity;
import android.app.AppGlobals;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import com.android.settings.BaseSettingsController;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class FontStatusController extends BaseSettingsController {
    private UpdateTask mUpdateTask;

    /* loaded from: classes.dex */
    private static class UpdateTask extends AsyncTask<Void, Void, String> {
        private WeakReference<FontStatusController> mFontStatusControllerRef;

        public UpdateTask(FontStatusController fontStatusController) {
            this.mFontStatusControllerRef = new WeakReference<>(fontStatusController);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(Void... voidArr) {
            return FontFragment.getCurrentUsingFontName(AppGlobals.getInitialApplication());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            FontStatusController fontStatusController = this.mFontStatusControllerRef.get();
            if (fontStatusController != null) {
                fontStatusController.updateResult(str);
            }
        }
    }

    public FontStatusController(Context context, TextView textView) {
        super(context, textView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateResult(String str) {
        Activity activity = (Activity) this.mContext;
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        TextView textView = this.mStatusView;
        if (textView != null) {
            textView.setText(str);
        }
        BaseSettingsController.UpdateCallback updateCallback = this.mUpdateCallback;
        if (updateCallback != null) {
            updateCallback.updateText(str);
        }
    }

    @Override // com.android.settings.BaseSettingsController
    public void pause() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void resume() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void updateStatus() {
        if (this.mStatusView == null && this.mUpdateCallback == null) {
            return;
        }
        UpdateTask updateTask = this.mUpdateTask;
        if (updateTask == null || updateTask.isCancelled() || this.mUpdateTask.getStatus() == AsyncTask.Status.FINISHED) {
            UpdateTask updateTask2 = new UpdateTask(this);
            this.mUpdateTask = updateTask2;
            updateTask2.execute(new Void[0]);
        }
    }
}
