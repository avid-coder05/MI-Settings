package com.android.settings.display;

import android.content.Context;
import android.text.BidiFormatter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.View;
import android.widget.EditText;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settingslib.CustomEditTextPreference;
import com.android.settingslib.display.DisplayDensityConfiguration;
import java.text.NumberFormat;

/* loaded from: classes.dex */
public class DensityPreference extends CustomEditTextPreference {
    public DensityPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        updateSummary();
    }

    private int getCurrentSwDp() {
        return (int) (Math.min(r2.widthPixels, r2.heightPixels) / getContext().getResources().getDisplayMetrics().density);
    }

    private void updateSummary() {
        setSummary(getContext().getString(R.string.density_pixel_summary, BidiFormatter.getInstance().unicodeWrap(NumberFormat.getInstance().format(getCurrentSwDp()))));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomEditTextPreference
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        EditText editText = (EditText) view.findViewById(16908291);
        if (editText != null) {
            editText.setInputType(2);
            editText.setText(getCurrentSwDp() + "");
            Utils.setEditTextCursorPosition(editText);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomEditTextPreference
    public void onDialogClosed(boolean z) {
        if (z) {
            try {
                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                DisplayDensityConfiguration.setForcedDisplayDensity(0, Math.max((Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels) * 160) / Math.max(Integer.parseInt(getText()), 320), 120));
                updateSummary();
            } catch (Exception e) {
                Slog.e("DensityPreference", "Couldn't save density", e);
            }
        }
    }
}
