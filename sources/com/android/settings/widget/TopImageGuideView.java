package com.android.settings.widget;

import android.content.Context;
import android.provider.MiuiSettings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.android.settings.R;
import com.android.settings.display.DarkModeTimeModeUtil;
import com.android.settings.report.InternationalCompat;

/* loaded from: classes2.dex */
public class TopImageGuideView extends RelativeLayout {
    private String DARK_MODE_OPEN_BEFORE_TIME_MODE;
    private Context mContext;
    private View mDarkModeEnable;
    private View mDarkModeOuterView;
    private View mLightModeEnable;
    private View mLightModeOuterView;

    public TopImageGuideView(Context context) {
        this(context, null);
    }

    public TopImageGuideView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TopImageGuideView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.DARK_MODE_OPEN_BEFORE_TIME_MODE = "dark_mode_open_before_time_mode";
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mContext = getContext();
        this.mDarkModeEnable = findViewById(R.id.dark_mode_enable);
        this.mLightModeEnable = findViewById(R.id.light_mode_enable);
        this.mDarkModeOuterView = findViewById(R.id.dark_mode_outer_view);
        this.mLightModeOuterView = findViewById(R.id.light_mode_outer_view);
        this.mDarkModeEnable.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.TopImageGuideView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                InternationalCompat.trackReportSwitchStatus("setting_Display_DM", Boolean.TRUE);
                if (DarkModeTimeModeUtil.isDarkModeEnable(TopImageGuideView.this.mContext)) {
                    return;
                }
                MiuiSettings.System.putBoolean(TopImageGuideView.this.mContext.getContentResolver(), TopImageGuideView.this.DARK_MODE_OPEN_BEFORE_TIME_MODE, true);
                DarkModeTimeModeUtil.setDarkModeEnable(TopImageGuideView.this.mContext, true, false);
                TopImageGuideView.this.mDarkModeOuterView.invalidate();
            }
        });
        this.mLightModeEnable.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.TopImageGuideView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                InternationalCompat.trackReportSwitchStatus("setting_Display_DM", Boolean.FALSE);
                if (DarkModeTimeModeUtil.isDarkModeEnable(TopImageGuideView.this.mContext)) {
                    MiuiSettings.System.putBoolean(TopImageGuideView.this.mContext.getContentResolver(), TopImageGuideView.this.DARK_MODE_OPEN_BEFORE_TIME_MODE, false);
                    DarkModeTimeModeUtil.setDarkModeEnable(TopImageGuideView.this.mContext, false, false);
                    TopImageGuideView.this.mDarkModeOuterView.invalidate();
                }
            }
        });
        if (DarkModeTimeModeUtil.isDarkModeEnable(this.mContext)) {
            this.mLightModeOuterView.setBackground(null);
            this.mDarkModeOuterView.setBackground(getResources().getDrawable(R.drawable.light_dark_mode_outer));
            return;
        }
        this.mDarkModeOuterView.setBackground(null);
        this.mLightModeOuterView.setBackground(getResources().getDrawable(R.drawable.light_dark_mode_outer));
    }
}
