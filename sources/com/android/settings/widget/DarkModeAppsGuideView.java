package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import com.android.settings.R;
import com.android.settings.display.DarkModeTimeModeUtil;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes2.dex */
public class DarkModeAppsGuideView extends LinearLayout {
    private LinearLayout mBackgroundLayout;
    public SlidingButton mBackgroundSlidingButton;
    private Context mContext;
    private LinearLayout mWallPaperLayout;
    public SlidingButton mWallPaperSlidingButton;

    public DarkModeAppsGuideView(Context context) {
        super(context);
    }

    public DarkModeAppsGuideView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DarkModeAppsGuideView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mContext = getContext();
        this.mWallPaperSlidingButton = (SlidingButton) findViewById(R.id.sliding_button_wallpaper);
        this.mBackgroundSlidingButton = (SlidingButton) findViewById(R.id.sliding_button_background);
        this.mWallPaperLayout = (LinearLayout) findViewById(R.id.wallpaper);
        this.mBackgroundLayout = (LinearLayout) findViewById(R.id.background);
        this.mWallPaperSlidingButton.setChecked(DarkModeTimeModeUtil.isDarkWallpaperModeEnable(this.mContext.getApplicationContext()));
        this.mBackgroundSlidingButton.setChecked(DarkModeTimeModeUtil.isDarkModeContrastEnable(this.mContext.getApplicationContext()));
        this.mWallPaperSlidingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.widget.DarkModeAppsGuideView.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                DarkModeAppsGuideView.this.mWallPaperSlidingButton.setChecked(z);
                DarkModeTimeModeUtil.setDarkWallpaperModeEnable(DarkModeAppsGuideView.this.mContext.getApplicationContext(), z);
            }
        });
        this.mBackgroundSlidingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.widget.DarkModeAppsGuideView.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                DarkModeAppsGuideView.this.mBackgroundSlidingButton.setChecked(z);
                DarkModeTimeModeUtil.setDarkModeContrastEnable(DarkModeAppsGuideView.this.mContext.getApplicationContext(), z);
            }
        });
        this.mWallPaperLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.DarkModeAppsGuideView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DarkModeAppsGuideView.this.mWallPaperSlidingButton.setChecked(!r2.isChecked());
                DarkModeTimeModeUtil.setDarkWallpaperModeEnable(DarkModeAppsGuideView.this.mContext.getApplicationContext(), DarkModeAppsGuideView.this.mWallPaperSlidingButton.isChecked());
            }
        });
        this.mBackgroundLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.widget.DarkModeAppsGuideView.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DarkModeAppsGuideView.this.mBackgroundSlidingButton.setChecked(!r2.isChecked());
                DarkModeTimeModeUtil.setDarkModeContrastEnable(DarkModeAppsGuideView.this.mContext.getApplicationContext(), DarkModeAppsGuideView.this.mBackgroundSlidingButton.isChecked());
            }
        });
    }
}
