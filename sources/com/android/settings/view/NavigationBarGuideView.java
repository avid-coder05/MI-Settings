package com.android.settings.view;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MiuiSettings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.VideoView;
import androidx.preference.PreferenceManager;
import com.android.settings.R;
import com.android.settings.utils.AnalyticsUtils;
import java.lang.reflect.Method;
import miui.content.res.ThemeResources;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class NavigationBarGuideView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = NavigationBarGuideView.class.getSimpleName();
    private AlertDialog mAlertDialog;
    private boolean mClickOnDialog;
    private boolean mDemoExistes;
    private View mFullScreenContainer;
    private RadioButton mFullScreenRadio;
    private VideoView mFullScreenVideoView;
    private boolean mHasCheckedDemo;
    private boolean mIsShowGestureLine;
    private View mNavigationHandle;
    private boolean mNeedShowDialog;
    private View mVirtualKeyContainer;
    private RadioButton mVirtualKeyRadio;

    public NavigationBarGuideView(Context context) {
        this(context, null);
    }

    public NavigationBarGuideView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NavigationBarGuideView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private boolean checkDemoExist() {
        if (!this.mHasCheckedDemo) {
            this.mHasCheckedDemo = true;
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.HomeDemoAct"));
            intent.putExtra("DEMO_TYPE", "DEMO_TO_HOME");
            if (getContext().getPackageManager().resolveActivity(intent, 0) != null) {
                this.mDemoExistes = true;
            }
        }
        return this.mDemoExistes;
    }

    private void createDialog() {
        this.mClickOnDialog = false;
        AlertDialog create = new AlertDialog.Builder(((LinearLayout) this).mContext).setTitle(R.string.navigation_guide_dialog_title).setMessage(R.string.navigation_guide_dialog_summary).setCheckBox(false, ((LinearLayout) this).mContext.getResources().getString(R.string.navigation_guide_dialog_dont_show_again)).setPositiveButton(R.string.navigation_guide_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.view.NavigationBarGuideView.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                NavigationBarGuideView.this.mClickOnDialog = true;
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(ThemeResources.SYSTEMUI_NAME, "com.android.systemui.fsgesture.DemoIntroduceAct"));
                    ((LinearLayout) NavigationBarGuideView.this).mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e(NavigationBarGuideView.TAG, "not fullscreen phone but configed fsgmode : " + e);
                }
                AnalyticsUtils.trackClickLearnBtnEvent(NavigationBarGuideView.this.getContext());
            }
        }).setNeutralButton(R.string.navigation_guide_dialog_skip, new DialogInterface.OnClickListener() { // from class: com.android.settings.view.NavigationBarGuideView.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                NavigationBarGuideView.this.mClickOnDialog = true;
            }
        }).create();
        this.mAlertDialog = create;
        create.setCanceledOnTouchOutside(true);
        this.mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.view.NavigationBarGuideView.4
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (NavigationBarGuideView.this.mClickOnDialog && NavigationBarGuideView.this.mAlertDialog != null) {
                    NavigationBarGuideView.this.setScreenButtonHidden(true);
                    NavigationBarGuideView.this.mFullScreenRadio.setChecked(true);
                    NavigationBarGuideView.this.mVirtualKeyRadio.setChecked(false);
                    SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(((LinearLayout) NavigationBarGuideView.this).mContext.getApplicationContext());
                    NavigationBarGuideView navigationBarGuideView = NavigationBarGuideView.this;
                    navigationBarGuideView.mNeedShowDialog = true ^ navigationBarGuideView.mAlertDialog.isChecked();
                    defaultSharedPreferences.edit().putBoolean("need_show_navigation_guide", NavigationBarGuideView.this.mNeedShowDialog).apply();
                }
                NavigationBarGuideView.this.mAlertDialog = null;
            }
        });
    }

    private boolean isScreenButtonHidden() {
        return MiuiSettings.Global.getBoolean(((LinearLayout) this).mContext.getContentResolver(), "force_fsg_nav_bar");
    }

    private void setNavigationVisibility() {
        if (this.mIsShowGestureLine) {
            this.mNavigationHandle.setVisibility(0);
        } else {
            this.mNavigationHandle.setVisibility(8);
        }
    }

    private void setVideoViewUnFocus() {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                Method declaredMethod = this.mFullScreenVideoView.getClass().getDeclaredMethod("setAudioFocusRequest", Integer.TYPE);
                if (declaredMethod != null) {
                    declaredMethod.invoke(this.mFullScreenVideoView, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startVideoView(final VideoView videoView, int i) {
        videoView.setVideoURI(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + i));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.view.NavigationBarGuideView.1
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() { // from class: com.android.settings.view.NavigationBarGuideView.1.1
                    @Override // android.media.MediaPlayer.OnInfoListener
                    public boolean onInfo(MediaPlayer mediaPlayer2, int i2, int i3) {
                        if (i2 == 3) {
                            videoView.setBackgroundColor(0);
                            return true;
                        }
                        return true;
                    }
                });
                mediaPlayer.start();
            }
        });
    }

    private void updateRadioState() {
        if (isScreenButtonHidden()) {
            this.mFullScreenRadio.setChecked(true);
            this.mVirtualKeyRadio.setChecked(false);
            return;
        }
        this.mFullScreenRadio.setChecked(false);
        this.mVirtualKeyRadio.setChecked(true);
    }

    private void updateVideoBackground() {
        this.mFullScreenVideoView.setBackgroundResource(R.drawable.navigation_picker_full_screen);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateVideoBackground();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        boolean isScreenButtonHidden = isScreenButtonHidden();
        if (view != this.mFullScreenContainer) {
            if (isScreenButtonHidden) {
                setScreenButtonHidden(false);
            }
            this.mFullScreenRadio.setChecked(false);
            this.mVirtualKeyRadio.setChecked(true);
        } else if (isScreenButtonHidden) {
        } else {
            if (this.mNeedShowDialog && checkDemoExist()) {
                createDialog();
                this.mAlertDialog.show();
                AnalyticsUtils.trackLearnGesturesWindowEvent(getContext());
                return;
            }
            setScreenButtonHidden(true);
            this.mFullScreenRadio.setChecked(true);
            this.mVirtualKeyRadio.setChecked(false);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mFullScreenVideoView = (VideoView) findViewById(R.id.video_view_full_screen);
        setVideoViewUnFocus();
        startVideoView(this.mFullScreenVideoView, R.raw.navigation_picker_full_screen);
        this.mFullScreenContainer = findViewById(R.id.full_screen_container);
        this.mVirtualKeyContainer = findViewById(R.id.virtual_keys_container);
        this.mFullScreenContainer.setOnClickListener(this);
        this.mVirtualKeyContainer.setOnClickListener(this);
        this.mFullScreenRadio = (RadioButton) findViewById(R.id.radio_button_full_screen);
        this.mVirtualKeyRadio = (RadioButton) findViewById(R.id.radio_button_virtual_keys);
        this.mNavigationHandle = findViewById(R.id.navigation_handle);
        updateRadioState();
        this.mNeedShowDialog = PreferenceManager.getDefaultSharedPreferences(((LinearLayout) this).mContext.getApplicationContext()).getBoolean("need_show_navigation_guide", true);
        setNavigationVisibility();
    }

    public void onPause() {
        updateVideoBackground();
    }

    public void onResume() {
        updateRadioState();
    }

    public boolean requestAccessibilityFocus() {
        RadioButton radioButton = this.mFullScreenRadio;
        if (radioButton == null || !radioButton.isChecked()) {
            RadioButton radioButton2 = this.mVirtualKeyRadio;
            return (radioButton2 == null || !radioButton2.isChecked()) ? super.requestAccessibilityFocus() : this.mVirtualKeyRadio.requestAccessibilityFocus();
        }
        return this.mFullScreenRadio.requestAccessibilityFocus();
    }

    public void setIsShowGestureLine(boolean z) {
        if (z != this.mIsShowGestureLine) {
            this.mIsShowGestureLine = z;
            setNavigationVisibility();
        }
    }

    void setScreenButtonHidden(boolean z) {
        MiuiSettings.Global.putBoolean(((LinearLayout) this).mContext.getContentResolver(), "force_fsg_nav_bar", z);
    }
}
