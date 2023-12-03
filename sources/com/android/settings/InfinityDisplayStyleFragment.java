package com.android.settings;

import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.provider.MiuiSettings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

/* loaded from: classes.dex */
public class InfinityDisplayStyleFragment extends BaseFragment implements View.OnClickListener {
    private RadioButton mInsideButton;
    private RadioButton mShowButton;

    private boolean isInside() {
        return MiuiSettings.Global.getBoolean(getContext().getContentResolver(), "force_black");
    }

    private boolean isOutside() {
        return MiuiSettings.Global.getBoolean(getContext().getContentResolver(), "force_black_v2");
    }

    private void setInside(boolean z) {
        MiuiSettings.Global.putBoolean(getContext().getContentResolver(), "force_black", z);
    }

    private void setOutside(boolean z) {
        MiuiSettings.Global.putBoolean(getContext().getContentResolver(), "force_black_v2", z);
    }

    private void updateRadioState() {
        if (isOutside()) {
            setOutside(false);
            setInside(true);
        }
        if (isInside()) {
            this.mShowButton.setChecked(false);
            this.mInsideButton.setChecked(true);
            return;
        }
        this.mShowButton.setChecked(true);
        this.mInsideButton.setChecked(false);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.notch_style_show_container) {
            setOutside(false);
            setInside(false);
        } else if (id == R.id.notch_style_status_bar_inside_container) {
            setOutside(false);
            setInside(true);
        }
        updateRadioState();
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_notch_style_mode, viewGroup, false);
        if (viewGroup != null) {
            PreferenceFrameLayout.LayoutParams layoutParams = ((ViewGroup) viewGroup.getParent()).getLayoutParams();
            if (layoutParams instanceof PreferenceFrameLayout.LayoutParams) {
                layoutParams.removeBorders = true;
            }
        }
        inflate.findViewById(R.id.notch_style_show_container).setOnClickListener(this);
        inflate.findViewById(R.id.notch_style_status_bar_inside_container).setOnClickListener(this);
        this.mShowButton = (RadioButton) inflate.findViewById(R.id.notch_style_show);
        this.mInsideButton = (RadioButton) inflate.findViewById(R.id.notch_style_status_bar_inside);
        updateRadioState();
        return inflate;
    }
}
