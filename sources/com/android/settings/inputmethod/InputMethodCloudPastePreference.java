package com.android.settings.inputmethod;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;

/* loaded from: classes.dex */
public class InputMethodCloudPastePreference extends Preference {
    private ImageView mCloudBubbleImage;
    private Context mContext;
    private ImageView mRedPointImage;
    private View mRootView;

    public InputMethodCloudPastePreference(Context context) {
        super(context);
        this.mContext = context;
        setLayoutResource(R.layout.input_method_cloud_data_paste_mode_settings);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        int cloudClipboardQuickPasteMode = InputMethodFunctionSelectUtils.getCloudClipboardQuickPasteMode(this.mContext);
        if (this.mRootView == null) {
            this.mRootView = preferenceViewHolder.itemView;
        }
        this.mRedPointImage = (ImageView) this.mRootView.findViewById(R.id.red_point);
        this.mCloudBubbleImage = (ImageView) this.mRootView.findViewById(R.id.bubble_image);
        setImageShow(cloudClipboardQuickPasteMode);
        super.onBindViewHolder(preferenceViewHolder);
    }

    public void setImageShow(int i) {
        if (i == 0) {
            this.mRedPointImage.setVisibility(0);
            this.mCloudBubbleImage.setVisibility(8);
        } else if (i == 1) {
            this.mRedPointImage.setVisibility(8);
            this.mCloudBubbleImage.setVisibility(0);
        } else if (i == 2) {
            this.mCloudBubbleImage.setVisibility(8);
            this.mRedPointImage.setVisibility(8);
        } else {
            Log.e("InputMethodCloudPaste", "selectValue is error : " + i);
        }
    }
}
