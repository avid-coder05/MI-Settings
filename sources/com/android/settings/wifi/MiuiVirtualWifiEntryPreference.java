package com.android.settings.wifi;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;
import java.util.Locale;
import miuix.preference.ConnectPreferenceHelper;

/* loaded from: classes2.dex */
public class MiuiVirtualWifiEntryPreference extends RadioButtonPreference {
    static final int[] BATTERY_LEVEL = {R.drawable.ap_battery_10, R.drawable.ap_battery_20, R.drawable.ap_battery_30, R.drawable.ap_battery_40, R.drawable.ap_battery_50, R.drawable.ap_battery_60, R.drawable.ap_battery_70, R.drawable.ap_battery_80, R.drawable.ap_battery_90, R.drawable.ap_battery_100};
    private int mBatteryLevel;
    private Context mContext;
    private ConnectPreferenceHelper mHelper;
    private boolean mIs5GHz;
    private int mState;
    private String mTitle;
    private View mView;

    public MiuiVirtualWifiEntryPreference(Context context, AttributeSet attributeSet, String str, int i, boolean z) {
        super(context, attributeSet);
        init(context, str, i, z);
    }

    private int getBatteryLevel() {
        int i = this.mBatteryLevel;
        if (i != -1) {
            int i2 = i / 10;
            return i2 == 10 ? i2 - 1 : i2;
        }
        return 9;
    }

    private void init(Context context, String str, int i, boolean z) {
        this.mContext = context;
        this.mTitle = str;
        this.mBatteryLevel = i;
        this.mIs5GHz = z;
        setLayoutResource(R.layout.accesspoint_preference);
        setWidgetLayoutResource(R.layout.preference_widget_ap_battery);
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mView = view;
        if (this.mHelper == null) {
            this.mHelper = new ConnectPreferenceHelper(getContext(), this);
        }
        this.mHelper.onBindViewHolder(preferenceViewHolder, view.findViewById(R.id.l_highlight));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(this.mContext.getResources().getDimensionPixelOffset(R.dimen.highlight_side_left_margin), this.mContext.getResources().getDimensionPixelOffset(R.dimen.highlight_top_margin), this.mContext.getResources().getDimensionPixelOffset(R.dimen.highlight_side_right_margin), 0);
        view.findViewById(R.id.cardview).setLayoutParams(layoutParams);
        updateState(this.mState);
        ImageView imageView = (ImageView) view.findViewById(R.id.preference_detail);
        imageView.setContentDescription(imageView.getResources().getString(R.string.network_detail, this.mTitle));
        imageView.setEnabled(false);
        imageView.setOnClickListener(null);
        imageView.setVisibility(0);
        TextView textView = (TextView) view.findViewById(16908304);
        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(16908310);
        checkedTextView.setCompoundDrawablePadding(checkedTextView.getResources().getDimensionPixelOffset(R.dimen.wifi_title_compound_padding));
        checkedTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        ImageView imageView2 = (ImageView) view.findViewById(R.id.wifi_band);
        imageView2.setPadding(0, 0, 0, 0);
        Drawable drawable = this.mContext.getResources().getDrawable(R.drawable.band_wifi_5g);
        imageView2.setVisibility(8);
        if (this.mIs5GHz) {
            imageView2.setVisibility(0);
            imageView2.setImageDrawable(drawable);
            int intrinsicWidth = drawable.getIntrinsicWidth() + ((int) ((this.mContext.getResources().getDisplayMetrics().density * 5.0f) + 0.5f));
            boolean z = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
            int i = z ? intrinsicWidth : 0;
            if (z) {
                intrinsicWidth = 0;
            }
            checkedTextView.setPadding(i, 0, intrinsicWidth, 0);
        }
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        updateBatteryLevel(this.mBatteryLevel);
    }

    public void updateBatteryLevel(int i) {
        View view = this.mView;
        if (view != null) {
            this.mBatteryLevel = i;
            ImageView imageView = (ImageView) view.findViewById(R.id.encryption);
            imageView.setVisibility(0);
            imageView.setImageDrawable(this.mContext.getDrawable(BATTERY_LEVEL[getBatteryLevel()]));
        }
    }

    public void updateIcon() {
        Drawable mutate = this.mContext.getDrawable(R.drawable.wifi_metered).mutate();
        if (mutate != null) {
            setIcon(mutate);
        }
    }

    public void updateState(int i) {
        this.mState = i;
        ConnectPreferenceHelper connectPreferenceHelper = this.mHelper;
        if (connectPreferenceHelper == null || connectPreferenceHelper.getConnectState() == i) {
            return;
        }
        this.mHelper.setConnectState(i);
    }

    public void updateSummary() {
        setSummary(this.mContext.getResources().getStringArray(R.array.wifi_status)[this.mState]);
    }
}
