package com.android.settings.widget;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

/* loaded from: classes2.dex */
public abstract class EmptyTextSettings extends SettingsPreferenceFragment {
    private TextView mEmpty;

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        TextView textView = new TextView(getContext());
        this.mEmpty = textView;
        textView.setGravity(17);
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.empty_text_padding);
        this.mEmpty.setPadding(dimensionPixelSize, 0, dimensionPixelSize, 0);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(16842817, typedValue, true);
        this.mEmpty.setTextAppearance(typedValue.resourceId);
        ((ViewGroup) view.findViewById(16908351).getParent()).addView(this.mEmpty, new ViewGroup.LayoutParams(-1, -1));
        setEmptyView(this.mEmpty);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setEmptyText(int i) {
        this.mEmpty.setText(i);
    }
}
