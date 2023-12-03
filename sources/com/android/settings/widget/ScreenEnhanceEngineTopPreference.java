package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import java.util.LinkedList;
import miuix.animation.Folme;
import miuix.util.Log;

/* loaded from: classes2.dex */
public class ScreenEnhanceEngineTopPreference extends Preference {
    private final int TYPE_ADD_IMAGE_VIEW;
    private final int TYPE_ADD_VIDEO_VIEW;
    private final int TYPE_REPLACE_IMAGE_VIEW;
    private final int TYPE_SET_RADIUS;
    private final int TYPE_SET_SUMMARY_TEXT;
    private LinkedList<Object> headViewAddList;
    private LinkedList<Integer> headViewTypeList;
    private ScreenEnhanceEngineTopView screenEnhanceEngineTopView;

    public ScreenEnhanceEngineTopPreference(Context context) {
        super(context);
        this.TYPE_ADD_IMAGE_VIEW = 0;
        this.TYPE_ADD_VIDEO_VIEW = 1;
        this.TYPE_SET_SUMMARY_TEXT = 2;
        this.TYPE_SET_RADIUS = 3;
        this.TYPE_REPLACE_IMAGE_VIEW = 4;
        this.screenEnhanceEngineTopView = null;
        this.headViewAddList = null;
        this.headViewTypeList = null;
        mInit();
    }

    public ScreenEnhanceEngineTopPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.TYPE_ADD_IMAGE_VIEW = 0;
        this.TYPE_ADD_VIDEO_VIEW = 1;
        this.TYPE_SET_SUMMARY_TEXT = 2;
        this.TYPE_SET_RADIUS = 3;
        this.TYPE_REPLACE_IMAGE_VIEW = 4;
        this.screenEnhanceEngineTopView = null;
        this.headViewAddList = null;
        this.headViewTypeList = null;
        mInit();
    }

    public ScreenEnhanceEngineTopPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.TYPE_ADD_IMAGE_VIEW = 0;
        this.TYPE_ADD_VIDEO_VIEW = 1;
        this.TYPE_SET_SUMMARY_TEXT = 2;
        this.TYPE_SET_RADIUS = 3;
        this.TYPE_REPLACE_IMAGE_VIEW = 4;
        this.screenEnhanceEngineTopView = null;
        this.headViewAddList = null;
        this.headViewTypeList = null;
        mInit();
    }

    public ScreenEnhanceEngineTopPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.TYPE_ADD_IMAGE_VIEW = 0;
        this.TYPE_ADD_VIDEO_VIEW = 1;
        this.TYPE_SET_SUMMARY_TEXT = 2;
        this.TYPE_SET_RADIUS = 3;
        this.TYPE_REPLACE_IMAGE_VIEW = 4;
        this.screenEnhanceEngineTopView = null;
        this.headViewAddList = null;
        this.headViewTypeList = null;
        mInit();
    }

    private void mInit() {
        this.headViewAddList = new LinkedList<>();
        this.headViewTypeList = new LinkedList<>();
    }

    public void addImageView(int i) {
        ScreenEnhanceEngineTopView screenEnhanceEngineTopView = this.screenEnhanceEngineTopView;
        if (screenEnhanceEngineTopView != null) {
            screenEnhanceEngineTopView.addImageView(i);
            return;
        }
        this.headViewAddList.add(Integer.valueOf(i));
        this.headViewTypeList.add(0);
    }

    public void addVideoView(int i) {
        ScreenEnhanceEngineTopView screenEnhanceEngineTopView = this.screenEnhanceEngineTopView;
        if (screenEnhanceEngineTopView != null) {
            screenEnhanceEngineTopView.addVideoView(i);
            return;
        }
        this.headViewAddList.add(Integer.valueOf(i));
        this.headViewTypeList.add(1);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        int i = 0;
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Log.e("screenenhance", "Folme clean");
            Folme.clean(view);
            view.setBackgroundResource(0);
        }
        ScreenEnhanceEngineTopView screenEnhanceEngineTopView = (ScreenEnhanceEngineTopView) view.findViewById(R.id.screen_enhance_engine_top_view);
        this.screenEnhanceEngineTopView = screenEnhanceEngineTopView;
        if (screenEnhanceEngineTopView != null) {
            int size = this.headViewAddList.size();
            while (i < size) {
                int intValue = this.headViewTypeList.get(i).intValue();
                if (intValue == 0) {
                    this.screenEnhanceEngineTopView.addImageView(((Integer) this.headViewAddList.get(i)).intValue());
                } else if (intValue == 1) {
                    this.screenEnhanceEngineTopView.addVideoView(((Integer) this.headViewAddList.get(i)).intValue());
                } else if (intValue == 2) {
                    this.screenEnhanceEngineTopView.setSummaryText(((Integer) this.headViewAddList.get(i)).intValue());
                } else if (intValue == 3) {
                    this.screenEnhanceEngineTopView.setRadius(((Float) this.headViewAddList.get(i)).floatValue());
                } else if (intValue == 4) {
                    ScreenEnhanceEngineTopView screenEnhanceEngineTopView2 = this.screenEnhanceEngineTopView;
                    int intValue2 = ((Integer) this.headViewAddList.get(i)).intValue();
                    i++;
                    screenEnhanceEngineTopView2.replaceImageView(intValue2, ((Integer) this.headViewAddList.get(i)).intValue());
                }
                i++;
            }
        }
    }
}
