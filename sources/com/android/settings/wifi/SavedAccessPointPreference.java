package com.android.settings.wifi;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.CheckBoxPreference;
import com.android.wifitrackerlib.WifiEntry;

/* loaded from: classes2.dex */
public class SavedAccessPointPreference extends CheckBoxPreference implements View.OnLongClickListener {
    private boolean mBtnChecked;
    private boolean mInActinoMode;
    private OnLongClickListener mLongClickListener;
    private View mView;
    private WifiEntry mWifiEntry;

    /* loaded from: classes2.dex */
    public interface OnLongClickListener {
        boolean onDeteleBtnClick(Preference preference);

        boolean onPreferenceLongClick(Preference preference);
    }

    public SavedAccessPointPreference(WifiEntry wifiEntry, Context context) {
        super(context);
        this.mBtnChecked = false;
        this.mInActinoMode = false;
        this.mWifiEntry = wifiEntry;
        setWidgetLayoutResource(R.layout.preference_checkbutton);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(View view) {
        OnLongClickListener onLongClickListener = this.mLongClickListener;
        if (onLongClickListener != null) {
            onLongClickListener.onDeteleBtnClick(this);
        }
    }

    public WifiEntry getWifiEntry() {
        return this.mWifiEntry;
    }

    @Override // androidx.preference.TwoStatePreference
    public boolean isChecked() {
        return this.mBtnChecked;
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        if (this.mWifiEntry == null) {
            return;
        }
        this.mView = view;
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        Button button = (Button) view.findViewById(R.id.btn_delete);
        if (this.mInActinoMode) {
            checkBox.setVisibility(0);
            button.setVisibility(8);
        } else {
            checkBox.setVisibility(8);
            button.setVisibility(0);
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.SavedAccessPointPreference$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    SavedAccessPointPreference.this.lambda$onBindViewHolder$0(view2);
                }
            });
        }
        checkBox.setChecked(this.mBtnChecked);
        preferenceViewHolder.itemView.setLongClickable(true);
        preferenceViewHolder.itemView.setOnLongClickListener(this);
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        OnLongClickListener onLongClickListener = this.mLongClickListener;
        return onLongClickListener != null && onLongClickListener.onPreferenceLongClick(this);
    }

    public void setActionMode(boolean z) {
        this.mInActinoMode = z;
        View view = this.mView;
        if (view == null) {
            return;
        }
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        Button button = (Button) this.mView.findViewById(R.id.btn_delete);
        if (this.mInActinoMode) {
            checkBox.setVisibility(0);
            button.setVisibility(8);
            return;
        }
        checkBox.setVisibility(8);
        button.setVisibility(0);
    }

    public void setBtnChecked(boolean z) {
        this.mBtnChecked = z;
        View view = this.mView;
        if (view != null) {
            ((CheckBox) view.findViewById(R.id.checkbox)).setChecked(z);
        }
    }

    public void setLongClickListener(OnLongClickListener onLongClickListener) {
        this.mLongClickListener = onLongClickListener;
    }
}
