package com.android.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.SystemProperties;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import com.android.settingslib.CustomEditTextPreference;

/* loaded from: classes.dex */
class EditPinPreference extends CustomEditTextPreference {
    private OnPinEnteredListener mPinListener;

    /* loaded from: classes.dex */
    interface OnPinEnteredListener {
        void onPinEntered(EditPinPreference editPinPreference, boolean z);
    }

    public EditPinPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public EditPinPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean isDialogOpen() {
        Dialog dialog = getDialog();
        return dialog != null && dialog.isShowing();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomEditTextPreference
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        EditText editText = (EditText) view.findViewById(16908291);
        if (editText != null) {
            editText.setInputType(18);
            editText.setTextAlignment(5);
            String str = SystemProperties.get("ro.miui.region", "unknown");
            if (str == null || !str.equals("KR")) {
                return;
            }
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomEditTextPreference
    public void onDialogClosed(boolean z) {
        super.onDialogClosed(z);
        OnPinEnteredListener onPinEnteredListener = this.mPinListener;
        if (onPinEnteredListener != null) {
            onPinEnteredListener.onPinEntered(this, z);
        }
    }

    public void setOnPinEnteredListener(OnPinEnteredListener onPinEnteredListener) {
        this.mPinListener = onPinEnteredListener;
    }

    public void showPinDialog() {
        Dialog dialog = getDialog();
        if (dialog == null || !dialog.isShowing()) {
            onClick();
        }
    }
}
